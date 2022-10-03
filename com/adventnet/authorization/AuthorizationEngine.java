package com.adventnet.authorization;

import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.Set;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.HashSet;
import java.util.NoSuchElementException;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.cache.CacheRepository;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.ds.query.SelectQueryImpl;
import java.util.Map;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.persistence.ActionInfo;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.OperationInfo;
import com.adventnet.persistence.DataAccess;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.ArrayList;
import java.rmi.RemoteException;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.Credential;
import com.adventnet.authorization.util.AuthorizationUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.authentication.util.AuthUtil;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class AuthorizationEngine
{
    private static Logger logger;
    private static HashMap ejbToModuleMap;
    private static HashMap tableToTrustedRoleMap;
    public static boolean isLocal;
    private static boolean fGAEnabled;
    private static boolean cGAEnabled;
    private static HashMap fGAEnabledMap;
    private static int aliasCount;
    private static List simplePermTableList;
    
    public static void checkPermission(final String beanName, final Method method, final Object[] arguments) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "check permission called for bean : {0}, method : {1} & args {1}", new Object[] { beanName, method, arguments });
        final Credential credential = AuthUtil.getUserCredential();
        String principalname = null;
        if (!getCGAEnabled()) {
            AuthorizationEngine.logger.log(Level.FINE, "CGA is disabled in the system. Ignoring Authorization check");
            return;
        }
        if (credential == null) {
            AuthorizationEngine.logger.log(Level.FINE, "Privileged Thread. CGA not required. i.e, Credential is null.");
            return;
        }
        principalname = credential.getLoginName();
        AuthorizationEngine.logger.log(Level.FINEST, "checking method permission for user : {0}", principalname);
        final List roles = getAuthorizedRoles();
        if (roles == null) {
            final String message = "User - " + principalname + "- do not have permission to access. No roles assigned to user";
            AuthorizationEngine.logger.log(Level.FINEST, message);
            throw new AuthorizationException(message);
        }
        if (isTrustedRoleRequestForCGA(beanName, roles)) {
            return;
        }
        final DataObject methPermission = getmethodPermission();
        for (int rNo = 0; rNo < roles.size(); ++rNo) {
            final Long roleId = (Long)AuthDBUtil.getObject("AaaRole", "ROLE_ID", "NAME", (Object)roles.get(rNo));
            final Column rid = Column.getColumn("AaaAce", "ROLE_ID");
            rid.setType(-5);
            final Criteria condition = new Criteria(rid, (Object)roleId, 0);
            final Join join = new Join("AaaAce", "AaaMethodPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 2);
            final Iterator it = methPermission.getRows("AaaAce", condition, join);
            if (it != null) {
                if (it.hasNext()) {
                    final Row mpRow = new Row("AaaMethodPermission");
                    while (it.hasNext()) {
                        final Row ace = it.next();
                        final Long permissionId = (Long)ace.get("PERMISSION_ID");
                        mpRow.set("PERMISSION_ID", (Object)permissionId);
                        final Row mp_row = methPermission.getFirstRow("AaaMethodPermission", mpRow);
                        final String bean = (String)mp_row.get("BEAN_NAME");
                        if (!bean.equals("*") && beanName != null && !bean.equals(beanName)) {
                            continue;
                        }
                        final String methodName = (String)mp_row.get("METHOD_NAME");
                        if (methodName.equals("*")) {
                            AuthorizationEngine.logger.log(Level.FINE, " Methods in this Bean are not access restricted for the role {0} ", roles.get(rNo));
                            return;
                        }
                        if (!methodName.equals(method.getName())) {
                            AuthorizationEngine.logger.log(Level.FINEST, " Method name doesn't matches method permission defined, name is {0} incomming request is {1} ", new Object[] { methodName, method.getName() });
                        }
                        else {
                            AuthorizationEngine.logger.log(Level.FINEST, " Method name matches method permission defined, name is {0} incomming request is {1} ", new Object[] { methodName, method.getName() });
                            final String[] params = getMethodParams(permissionId, methPermission);
                            if (AuthorizationUtil.paramsMatches(method.getParameterTypes(), params)) {
                                AuthorizationEngine.logger.log(Level.FINE, " Method permission granted ");
                                return;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        final String formattedMsg = "User - " + principalname + " do not have permission to access method - " + method;
        AuthorizationEngine.logger.log(Level.FINEST, formattedMsg);
        throw new AuthorizationException(formattedMsg);
    }
    
    private static boolean isTrustedRoleRequestForCGA(final String beanName, final List roles) {
        String modulename = null;
        if (AuthorizationEngine.ejbToModuleMap == null) {
            AuthorizationEngine.logger.log(Level.SEVERE, "ejbToModuleMap is null. Skipping TrustedRole check");
            return false;
        }
        AuthorizationEngine.logger.log(Level.FINEST, "ejbToModuleMap is : {0}", AuthorizationEngine.ejbToModuleMap);
        if (AuthorizationEngine.ejbToModuleMap.containsKey(beanName)) {
            AuthorizationEngine.logger.log(Level.FINEST, "ejbToModuleMap contains bean : {0}", beanName);
            modulename = AuthorizationEngine.ejbToModuleMap.get(beanName);
            AuthorizationEngine.logger.log(Level.FINEST, "module name obtained from map is : {0}", modulename);
            if (modulename == null) {
                AuthorizationEngine.logger.log(Level.WARNING, "module name obtained for bean : {0} is null. Ignoring trusted role check");
                return false;
            }
            if (!AuthorizationUtil.isAuthznReqForModule(modulename)) {
                AuthorizationEngine.logger.log(Level.FINEST, "authorization not required for module as no roles exist for the module");
                return true;
            }
            final String trustedRole = AuthorizationUtil.getTrustedRole(modulename);
            if (trustedRole == null) {
                AuthorizationEngine.logger.log(Level.WARNING, "trusted role obtained for module : {0} is null. return false");
                return false;
            }
            AuthorizationEngine.logger.log(Level.FINEST, "trustedRole obtained for the application is : {0}", trustedRole);
            if (roles.contains(trustedRole)) {
                AuthorizationEngine.logger.log(Level.FINEST, "bean : {0} is accessed via trusted role : {1}", new String[] { beanName, trustedRole });
                return true;
            }
        }
        return false;
    }
    
    public static void checkPermission(final Method method, final Object[] arguments) throws DataAccessException, RemoteException {
        checkPermission(null, method, arguments);
    }
    
    public static boolean isFGARequired() throws DataAccessException, RemoteException {
        if (!getFGAEnabled()) {
            AuthorizationEngine.logger.log(Level.FINE, "FGA is disabled in the system. return");
            return false;
        }
        if (AuthUtil.getUserCredential() == null) {
            AuthorizationEngine.logger.log(Level.FINE, "privileged thread, hence FGA not required");
            return false;
        }
        return true;
    }
    
    public static void checkUpdatedColumn(final DataObject dao) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "checkUpdatedColumn invoked with DO : {0}", dao);
        if (!isFGARequired()) {
            return;
        }
        List tables = dao.getTableNames();
        if (tables.size() == 0) {
            AuthorizationEngine.logger.log(Level.FINER, "list of tables obtained from DO empty, hence no authorization check required. return");
            return;
        }
        final List roles = getAuthorizedRoles();
        final String principalname = AuthUtil.getUserCredential().getLoginName();
        if (roles == null) {
            AuthorizationEngine.logger.log(Level.WARNING, "In-sufficient privilege. No roles assigned for this user {0}", principalname);
            final String message = "User : " + principalname + " do not have permission to access table. Access Denied. No roles assigned for the user";
            throw new AuthorizationException(message);
        }
        tables = getToBeAuthorizedTables(tables, roles, "U");
        if (tables == null || tables.size() == 0) {
            AuthorizationEngine.logger.log(Level.FINE, "authorization check succeeded without any scoping");
            return;
        }
        AuthorizationEngine.logger.log(Level.FINE, " The following set of tables requires Authorization based scoping {0} ", tables);
        final DataObject tabPermission = getTablePermission(roles, "U");
        final HashMap tableaccessSPI = getTableAccessSPIs();
        for (int rNo = 0; rNo < roles.size(); ++rNo) {
            final List perm = getTablePermission(roles.get(rNo), "U", tabPermission);
            if (perm != null) {
                if (perm.size() != 0) {
                    for (int pNo = 0; pNo < perm.size(); ++pNo) {
                        final Row perm_row = perm.get(pNo);
                        final Object permissionId = perm_row.get("PERMISSION_ID");
                        if (checkForUpdateColumns((Long)permissionId, dao, tabPermission, tableaccessSPI)) {
                            AuthorizationEngine.logger.log(Level.FINEST, "update column checks completed");
                            return;
                        }
                    }
                }
            }
        }
        AuthorizationEngine.logger.log(Level.WARNING, " Update Columns does not pass the criteria check : Operation not allowed : Authorization Exception {0}", AuthUtil.getUserCredential().getLoginName());
        final String message2 = "Update Columns does not pass the criteria check : Operation not allowed for the user " + AuthUtil.getUserCredential().getLoginName();
        throw new AuthorizationException(message2);
    }
    
    public static List getEditableElements(final List doList) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINE, " Retrieving Permitted DO for Update operation ");
        final List toReturn = new ArrayList();
        final Iterator it = doList.iterator();
        while (it.hasNext()) {
            try {
                final DataObject dao = it.next();
                checkPermission(dao, "U");
                toReturn.add(dao);
                AuthorizationEngine.logger.log(Level.FINE, " DO satisfies the update permission added to the list ");
            }
            catch (final Exception ex) {
                AuthorizationEngine.logger.log(Level.FINE, " Got Exception while checking for update permission. DataObject Not authorized for updation ", ex);
            }
        }
        return toReturn;
    }
    
    public static List getAccessibleElements(final List doList) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINE, " Retrieving Permitted DO for Read operation");
        final List toReturn = new ArrayList();
        final Iterator it = doList.iterator();
        final HashMap tableaccessSPI = getTableAccessSPIs();
        while (it.hasNext()) {
            try {
                final DataObject dao = it.next();
                checkDOForRead(dao, tableaccessSPI);
                toReturn.add(dao);
                AuthorizationEngine.logger.log(Level.FINE, " DO satisfies the read permission added to the list ");
            }
            catch (final Exception ex) {
                AuthorizationEngine.logger.log(Level.FINE, " Got Exception while checking for read permission not added to return list ", ex);
            }
        }
        return toReturn;
    }
    
    private static void checkDOForRead(final DataObject dao, final HashMap tableaccessSPI) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINE, " Retrieving Permitted DOs for Read operation for permission");
        if (!isFGARequired()) {
            return;
        }
        List tables = dao.getTableNames();
        if (tables.size() == 0) {
            AuthorizationEngine.logger.log(Level.FINER, "list of tables obtained from DO is empty. hence return");
            return;
        }
        final List roles = getAuthorizedRoles();
        if (roles == null) {
            final String message = "No roles assigned for this user " + AuthUtil.getUserCredential().getLoginName();
            throw new AuthorizationException(message);
        }
        tables = getToBeAuthorizedTables(tables, roles, "R");
        if (tables == null || tables.size() == 0) {
            AuthorizationEngine.logger.log(Level.FINEST, "All tables in query are accessed through trusted principal of the module: hence no authorization required !");
            return;
        }
        AuthorizationEngine.logger.log(Level.FINEST, " The following set of tables requires Authorization based scoping {0}", tables);
        SelectQuery toReturn = null;
        final DataObject tabPermission = getTablePermission(roles, "R");
        for (int rNo = 0; rNo < roles.size(); ++rNo) {
            final List permList = getTablePermission(roles.get(rNo), "R", tabPermission);
            if (permList != null) {
                final Row perm = permList.get(0);
                final Object permissionId = perm.get("PERMISSION_ID");
                final List impliedTables = getImpliedTables((Long)permissionId, tabPermission);
                impliedTables.add(perm.get("TABLE_NAME"));
                if (!impliedTables.containsAll(tables)) {
                    AuthorizationEngine.logger.log(Level.FINEST, " All tables were not present in permission list not considering the permission for authorization{0} required {1} available :{2}", new Object[] { roles.get(rNo), tables, impliedTables });
                }
                else if (toReturn == null) {
                    toReturn = getSQ(permissionId, null, null, "R", tabPermission, tableaccessSPI);
                }
                else {
                    final SelectQuery tempSQ = getSQ(permissionId, null, null, "R", tabPermission, tableaccessSPI);
                    final Criteria tempCriteria = toReturn.getCriteria();
                    toReturn.setCriteria(tempCriteria.or(tempSQ.getCriteria()));
                    toReturn.getJoins().removeAll(tempSQ.getJoins());
                    toReturn.getJoins().addAll(tempSQ.getJoins());
                }
            }
        }
        if (toReturn == null) {
            final String message2 = "In-sufficient privilege for user " + AuthUtil.getUserCredential().getLoginName() + " for operation Read";
            throw new AuthorizationException(message2);
        }
        Iterator it = tables.iterator();
        String tableName = null;
        String baseTable = null;
        int prevCount = 10000;
        int currentCount = 0;
        int rowCount = 0;
        while (it.hasNext()) {
            tableName = it.next();
            toReturn.addSelectColumn(Column.getColumn(tableName, "*"));
            AuthorizationEngine.logger.log(Level.FINEST, "Adding Select Column = {0}", Column.getColumn(tableName, "*"));
            currentCount = AuthorizationUtil.getCount(dao.getRows(tableName));
            rowCount += currentCount;
            if (currentCount < prevCount) {
                prevCount = currentCount;
                baseTable = tableName;
            }
        }
        AuthorizationEngine.logger.log(Level.FINEST, " Base Table Name is  = {0}", baseTable);
        List pks = null;
        try {
            pks = MetaDataUtil.getTableDefinitionByName(baseTable).getPrimaryKey().getColumnList();
        }
        catch (final MetaDataException mde) {
            AuthorizationEngine.logger.log(Level.FINEST, " Exception ", (Throwable)mde);
        }
        it = dao.getRows(baseTable);
        Criteria inCriteria = null;
        while (it.hasNext()) {
            final Row row = it.next();
            Criteria pkCriteria = null;
            String pkCol = null;
            AuthorizationEngine.logger.log(Level.FINEST, " Looping Base Table instances for Inclause Criteria {0}", new Integer(pks.size()));
            for (int i = 0; i < pks.size(); ++i) {
                pkCol = pks.get(i);
                if (pkCriteria == null) {
                    pkCriteria = new Criteria(Column.getColumn(baseTable, pkCol), row.get(pkCol), 0);
                }
                else {
                    pkCriteria = pkCriteria.and(new Criteria(Column.getColumn(baseTable, pkCol), row.get(pkCol), 0));
                }
            }
            AuthorizationEngine.logger.log(Level.FINEST, " In Clause Criteria is {0}", inCriteria);
            if (inCriteria == null) {
                inCriteria = pkCriteria;
            }
            else {
                inCriteria = inCriteria.or(pkCriteria);
            }
        }
        AuthorizationEngine.logger.log(Level.FINEST, " Final InClause Criteria is {0}", inCriteria);
        final Criteria temp = toReturn.getCriteria();
        AuthorizationEngine.logger.log(Level.FINEST, " Criteria in constructed query is {0}", temp);
        if (temp != null) {
            if (inCriteria != null) {
                toReturn.setCriteria(temp.and(inCriteria));
            }
            else {
                toReturn.setCriteria(temp);
            }
        }
        else {
            toReturn.setCriteria(inCriteria);
        }
        AuthorizationEngine.logger.log(Level.FINEST, "Final query executed is  {0}", toReturn);
        final DataObject authResult = DataAccess.get(toReturn);
        it = tables.iterator();
        int authorizedCount = 0;
        while (it.hasNext()) {
            tableName = it.next();
            authorizedCount += AuthorizationUtil.getCount(authResult.getRows(tableName));
        }
        AuthorizationEngine.logger.log(Level.FINE, " Final result is Authorized Count  {0} Incomming Row count is {1}", new Object[] { new Integer(authorizedCount), new Integer(rowCount) });
        if (authorizedCount < rowCount) {
            AuthorizationEngine.logger.log(Level.FINEST, " In-sufficient privilege for user for Read operation, permitted do is {0} requested do {1}", new Object[] { authResult, dao });
            final String message3 = "In-sufficient privilege for user to perform Read operation " + AuthUtil.getUserCredential().getLoginName();
            throw new AuthorizationException(message3);
        }
    }
    
    public static void scopeSelectQuery(final SelectQuery sq) throws DataAccessException, RemoteException {
        checkPermission(sq, null, null, "R");
    }
    
    public static SelectQuery getScopeQueryForDO(final OperationInfo oi, final String type) throws DataAccessException, RemoteException {
        final List tables = oi.getTableNames();
        final DataObject dao = oi.getDataObject();
        return checkPermission(null, dao, tables, type);
    }
    
    public static void authorizeDeleteOperation(final List tables, final SelectQuery sq) throws DataAccessException, RemoteException {
        final List tableNames = new ArrayList();
        if (tables != null) {
            for (int size = tables.size(), i = 0; i < size; ++i) {
                tableNames.add(tables.get(i).getTableName());
            }
        }
        final SelectQuery scopedQuery = checkPermission(null, null, tableNames, "D");
        AuthorizationEngine.logger.log(Level.FINE, "scoped query obtained from policy for tableNames : {0} for type : D is : {1}", new Object[] { tableNames, scopedQuery });
        if (scopedQuery == null) {
            return;
        }
        final Criteria incomingCriteria = sq.getCriteria();
        final Criteria scopedCriteria = scopedQuery.getCriteria();
        AuthorizationEngine.logger.log(Level.FINE, "Criteria obtained from incoming select query is : {0} and from scoped query is : {1}", new Object[] { incomingCriteria, scopedCriteria });
        if (incomingCriteria != null) {
            if (scopedCriteria != null) {
                scopedQuery.setCriteria(incomingCriteria.and(scopedCriteria));
            }
            else {
                scopedQuery.setCriteria(incomingCriteria);
            }
        }
        else if (scopedCriteria != null) {
            scopedQuery.setCriteria(scopedCriteria);
        }
        AuthorizationEngine.logger.log(Level.FINE, "final scoped query is : {0}", scopedQuery);
        int total = 0;
        int scopedTotal = 0;
        final SelectQuery sqForCount = AuthorizationUtil.getSelectQueryForTotal(sq);
        final SelectQuery scopedSqForCount = AuthorizationUtil.getSelectQueryForTotal(scopedQuery);
        AuthorizationEngine.logger.log(Level.FINE, "BulkDelete Select Query for Count is = {0}", sqForCount);
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        DataSet scopedDs = null;
        try {
            conn = relationalAPI.getConnection();
            AuthorizationEngine.logger.log(Level.FINEST, "conn : {0}", conn);
            ds = relationalAPI.executeQuery((Query)sqForCount, conn);
            while (ds.next()) {
                total = ds.getInt(1);
                AuthorizationEngine.logger.log(Level.FINER, "Total count : {0}", new Integer(total));
            }
            AuthorizationEngine.logger.log(Level.FINEST, " Scoped SQL for count : {0}", relationalAPI.getSelectSQL((Query)scopedSqForCount));
            scopedDs = relationalAPI.executeQuery((Query)scopedSqForCount, conn);
            while (scopedDs.next()) {
                scopedTotal = scopedDs.getInt(1);
                AuthorizationEngine.logger.log(Level.FINER, "Scoped Total count : {0}", new Integer(scopedTotal));
            }
            if (total > scopedTotal) {
                AuthorizationEngine.logger.log(Level.WARNING, "User : {0} not permitted to access the following tables : {1}", new Object[] { AuthUtil.getUserCredential().getLoginName(), tables });
                final String message = "Access Denied for user : " + AuthUtil.getUserCredential().getLoginName();
                throw new AuthorizationException(message);
            }
        }
        catch (final QueryConstructionException qce) {
            final String message2 = " Exception occured during QueryConstruction, Query is \" " + sqForCount + " \" " + qce;
            throw new AuthorizationException(message2);
        }
        catch (final SQLException sqle) {
            final String message2 = "Exception occured when executing query, Query is \" " + sqForCount + " \" " + sqle;
            throw new AuthorizationException(message2);
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException se) {
                    AuthorizationEngine.logger.log(Level.SEVERE, "Exception when cleaning up resources : {0}", se);
                }
            }
            if (scopedDs != null) {
                try {
                    scopedDs.close();
                }
                catch (final SQLException se) {
                    AuthorizationEngine.logger.log(Level.SEVERE, "Exception when cleaning up resources : {0}", se);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException se) {
                    AuthorizationEngine.logger.log(Level.SEVERE, "Exception when cleaning up resources : {0}", se);
                }
            }
        }
    }
    
    public static void checkPermission(final DataObject dao, final String type) throws DataAccessException, RemoteException {
        checkPermission(null, dao, null, type);
    }
    
    private static SelectQuery checkPermission(SelectQuery inSq, final DataObject dao, final List tableList, final String type) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINE, "check permission called for type : {0} \nwith DO : {1}, \nSelect Query : {2} and \ntableList : {3}", new Object[] { type, dao, inSq, tableList });
        if (!isFGARequired()) {
            return null;
        }
        final List roles = getAuthorizedRoles();
        final String principalname = AuthUtil.getUserCredential().getLoginName();
        if (roles == null) {
            AuthorizationEngine.logger.log(Level.WARNING, "User : {0} do not have permission to access table. Access Denied. No roles assigned for the user", principalname);
            final String message = "User : " + principalname + " do not have permission to access table. Access Denied. No roles assigned for the user";
            throw new AuthorizationException(message);
        }
        SelectQuery toReturn = null;
        List tables = new ArrayList();
        if (type.equals("R")) {
            if (inSq == null) {
                return null;
            }
            final List tempList = inSq.getTableList();
            for (final Table table : tempList) {
                if (!tables.contains(table.getTableName())) {
                    tables.add(table.getTableName());
                }
            }
        }
        else if (type.equals("U")) {
            if (dao != null) {
                final List operations = dao.getOperations();
                if (operations != null) {
                    final int operationSize = operations.size();
                    ActionInfo actionPerf = null;
                    for (int i = 0; i < operationSize; ++i) {
                        actionPerf = operations.get(i);
                        tables.add(actionPerf.getValue().getOriginalTableName());
                    }
                }
            }
        }
        else if (tableList != null && dao == null) {
            final Iterator tIt2 = tableList.iterator();
            while (tIt2.hasNext()) {
                tables.add(tIt2.next());
            }
        }
        else if (tableList != null) {
            tables = tableList;
        }
        else {
            final List tempList = dao.getTableNames();
            for (final String table2 : tempList) {
                tables.add(table2);
            }
        }
        AuthorizationEngine.logger.log(Level.FINER, "list of tables to be authorized : {0}", tables);
        tables = getToBeAuthorizedTables(tables, roles, type);
        if (tables == null || tables.size() == 0) {
            AuthorizationEngine.logger.log(Level.FINER, "authorization check succeeded without any scoping");
            return null;
        }
        AuthorizationEngine.logger.log(Level.FINER, "table list that require scoping are : {0}", tables);
        final DataObject tabPermission = getTablePermission(roles, type);
        for (int rNo = 0; rNo < roles.size(); ++rNo) {
            SelectQuery roleSQ = null;
            final List permList = getTablePermission(roles.get(rNo), type, tabPermission);
            if (permList == null || permList.size() == 0) {
                AuthorizationEngine.logger.log(Level.FINER, "skip role : {0} as it has no table permissions", roles.get(rNo));
            }
            else {
                AuthorizationEngine.logger.log(Level.FINER, "table permission list obtained for role : {0} are : {1}", new Object[] { roles.get(rNo), permList });
                for (int pNo = 0; pNo < permList.size(); ++pNo) {
                    final Row perm = permList.get(pNo);
                    final Object permissionId = perm.get("PERMISSION_ID");
                    if (type.equals("R")) {
                        try {
                            inSq = checkForReadColumns((Long)permissionId, inSq, tabPermission);
                        }
                        catch (final QueryConstructionException qce) {
                            AuthorizationEngine.logger.log(Level.SEVERE, " Exception while scoping select query for read permission ", (Throwable)qce);
                        }
                    }
                    final List impliedTables = getImpliedTables((Long)permissionId, tabPermission);
                    impliedTables.add(perm.get("TABLE_NAME"));
                    if (!impliedTables.containsAll(tables)) {
                        AuthorizationEngine.logger.log(Level.FINEST, "skip permission : {0} as all required tables were not present. req list [ {1} ], present list [ {2}]", new Object[] { permissionId, tables, impliedTables });
                    }
                    else {
                        AuthorizationEngine.logger.log(Level.FINER, "permission : {0} satisfies the required table list [ {1}]", new Object[] { permissionId, tables });
                        final HashMap tableaccessSPI = getTableAccessSPIs();
                        if (roleSQ == null) {
                            if (type.equals("R")) {
                                roleSQ = getSQ(permissionId, inSq, tables, "R", tabPermission, tableaccessSPI);
                            }
                            else {
                                roleSQ = getSQ(permissionId, inSq, tables, "CUD", tabPermission, tableaccessSPI);
                            }
                        }
                        else {
                            SelectQuery tempSQ;
                            if (type.equals("R")) {
                                tempSQ = getSQ(permissionId, inSq, tables, "R", tabPermission, tableaccessSPI);
                            }
                            else {
                                tempSQ = getSQ(permissionId, inSq, tables, "CUD", tabPermission, tableaccessSPI);
                            }
                            if (tempSQ != null) {
                                final Criteria tempCriteria = tempSQ.getCriteria();
                                if (tempCriteria != null) {
                                    AuthorizationEngine.logger.log(Level.FINEST, " Select Queries before merging criteria {0} Next : \n {1}", new Object[] { roleSQ, tempSQ });
                                    if (roleSQ.getCriteria() != null) {
                                        roleSQ.setCriteria(tempCriteria.and(roleSQ.getCriteria()));
                                    }
                                    else {
                                        roleSQ.setCriteria(tempCriteria);
                                    }
                                    AuthorizationEngine.logger.log(Level.FINEST, " Select Queries after merging criteria {0} ", roleSQ);
                                }
                                AuthorizationEngine.logger.log(Level.FINEST, " Select Queries before merging Joins {0} Next : \n {1}", new Object[] { roleSQ, tempSQ });
                                final List sqJoins = roleSQ.getJoins();
                                sqJoins.removeAll(tempSQ.getJoins());
                                sqJoins.addAll(tempSQ.getJoins());
                                constructJoins(roleSQ, sqJoins);
                                AuthorizationEngine.logger.log(Level.FINEST, " Select Queries after merging Joins {0}", roleSQ);
                            }
                        }
                    }
                }
                if (toReturn == null) {
                    toReturn = roleSQ;
                }
                else {
                    final SelectQuery tempSQ2 = roleSQ;
                    if (tempSQ2 != null) {
                        final Criteria tempCriteria2 = tempSQ2.getCriteria();
                        if (tempCriteria2 != null) {
                            AuthorizationEngine.logger.log(Level.FINEST, " Select Queries before merging criteria {0} Next : \n {1}", new Object[] { toReturn, tempSQ2 });
                            if (toReturn.getCriteria() != null) {
                                toReturn.setCriteria(tempCriteria2.or(toReturn.getCriteria()));
                            }
                            else {
                                toReturn.setCriteria(tempCriteria2);
                            }
                            AuthorizationEngine.logger.log(Level.FINEST, " Select Queries after merging criteria {0}", toReturn);
                        }
                        AuthorizationEngine.logger.log(Level.FINEST, " Select Queries before merging Joins {0} Next : \n {1}", new Object[] { toReturn, tempSQ2 });
                        final List sqJoins2 = toReturn.getJoins();
                        sqJoins2.removeAll(tempSQ2.getJoins());
                        sqJoins2.addAll(tempSQ2.getJoins());
                        constructJoins(toReturn, sqJoins2);
                        AuthorizationEngine.logger.log(Level.FINEST, " Select Queries after merging Joins {0}", toReturn);
                    }
                }
            }
        }
        if (toReturn == null) {
            AuthorizationEngine.logger.log(Level.FINER, "select query to return is null after checking all roles, authorization failed");
            AuthorizationEngine.logger.log(Level.WARNING, " In-sufficient privilege for user {0} to perform operation type ({1}) on tables {2}", new Object[] { principalname, type, tables });
            final String message2 = "In-sufficient privilege for user " + principalname + " to perform the operation";
            throw new AuthorizationException(message2);
        }
        if (type.equals("R")) {
            final Criteria temp = inSq.getCriteria();
            final HashMap map = AuthorizationUtil.getTableAliasForSQ(inSq);
            Criteria criteriaConst = toReturn.getCriteria();
            criteriaConst = modifyCriteriaWithTableAlias(criteriaConst, map, String.valueOf(AuthorizationEngine.aliasCount));
            if (temp != null) {
                if (criteriaConst != null) {
                    inSq.setCriteria(temp.and(criteriaConst));
                }
                else {
                    inSq.setCriteria(temp);
                }
            }
            else {
                inSq.setCriteria(criteriaConst);
            }
            AuthorizationEngine.logger.log(Level.FINEST, " Joins Constructed {0} Incomming Joins {1}", new Object[] { toReturn.getJoins(), inSq.getJoins() });
            toReturn.getJoins().removeAll(inSq.getJoins());
            List joinList = AuthorizationUtil.trimJoins(inSq, toReturn.getJoins());
            joinList = modifyJoinsForAlias(toReturn.getJoins(), map, String.valueOf(AuthorizationEngine.aliasCount));
            constructJoins(inSq, joinList);
            AuthorizationEngine.logger.log(Level.FINEST, "selectQuery after scoping is : {0}", inSq);
            ++AuthorizationEngine.aliasCount;
            return null;
        }
        if (tableList != null && dao == null) {
            return toReturn;
        }
        Iterator it = tables.iterator();
        String tableName = null;
        String baseTable = null;
        int prevCount = 10000;
        int currentCount = 0;
        int rowCount = 0;
        while (it.hasNext()) {
            tableName = it.next();
            toReturn.addSelectColumn(Column.getColumn(tableName, "*"));
            AuthorizationEngine.logger.log(Level.FINEST, "Adding Select Column = {0}", Column.getColumn(tableName, "*"));
            currentCount = AuthorizationUtil.getCount(dao.getRows(tableName));
            rowCount += currentCount;
            if (currentCount < prevCount) {
                prevCount = currentCount;
                baseTable = tableName;
            }
        }
        if (type.equals("D")) {
            AuthorizationEngine.logger.log(Level.FINE, " Select Query returned for Delete operation is {0} ", toReturn);
            return toReturn;
        }
        AuthorizationEngine.logger.log(Level.FINE, "Scoped selecy query to be executed to get authorizedCount = {0}", toReturn);
        AuthorizationEngine.logger.log(Level.FINEST, "base table name is  = {0}", baseTable);
        List pks = null;
        try {
            pks = MetaDataUtil.getTableDefinitionByName(baseTable).getPrimaryKey().getColumnList();
        }
        catch (final MetaDataException mde) {
            AuthorizationEngine.logger.log(Level.FINE, " Exception while getting TableDefinition ", (Throwable)mde);
        }
        it = dao.getRows(baseTable);
        Criteria inCriteria = null;
        while (it.hasNext()) {
            final Row row = it.next();
            Criteria pkCriteria = null;
            String pkCol = null;
            AuthorizationEngine.logger.log(Level.FINEST, " Looping Base Table instances for Inclause Criteria {0}", new Integer(pks.size()));
            for (int j = 0; j < pks.size(); ++j) {
                pkCol = pks.get(j);
                if (pkCriteria == null) {
                    pkCriteria = new Criteria(Column.getColumn(baseTable, pkCol), row.get(pkCol), 0);
                }
                else {
                    pkCriteria = pkCriteria.and(new Criteria(Column.getColumn(baseTable, pkCol), row.get(pkCol), 0));
                }
            }
            if (inCriteria == null) {
                inCriteria = pkCriteria;
            }
            else {
                inCriteria = inCriteria.or(pkCriteria);
            }
        }
        AuthorizationEngine.logger.log(Level.FINER, "Final InClause Criteria is {0}", inCriteria);
        final Criteria temp2 = toReturn.getCriteria();
        AuthorizationEngine.logger.log(Level.FINEST, " Criteria in constructed query is {0}", temp2);
        if (temp2 != null) {
            if (inCriteria != null) {
                toReturn.setCriteria(temp2.and(inCriteria));
            }
            else {
                toReturn.setCriteria(temp2);
            }
        }
        else {
            toReturn.setCriteria(inCriteria);
        }
        AuthorizationEngine.logger.log(Level.FINE, "scoped query to be executed for update is : {0}", toReturn);
        final DataObject authResult = DataAccess.get(toReturn);
        AuthorizationEngine.logger.log(Level.FINE, "result obtained after executing scoped query : {0} is {1}", new Object[] { toReturn, authResult });
        it = tables.iterator();
        int authorizedCount = 0;
        while (it.hasNext()) {
            tableName = it.next();
            authorizedCount += AuthorizationUtil.getCount(authResult.getRows(tableName));
        }
        AuthorizationEngine.logger.log(Level.FINE, "number of rows in incomming DO = {0} & authorized DO = {1}", new Object[] { new Integer(rowCount), new Integer(authorizedCount) });
        if (authorizedCount < rowCount) {
            AuthorizationEngine.logger.log(Level.FINER, "number of rows in incomming DO is greater than number of rows in authorized DO, hence deny access");
            AuthorizationEngine.logger.log(Level.FINEST, " In-sufficient privilege for user : {0} to perform operation type ({1}) on DO, permitted DO is {2} requested do {3}", new Object[] { AuthUtil.getUserCredential().getLoginName(), type, authResult, dao });
            final String message3 = "In-sufficient privilege for user " + AuthUtil.getUserCredential().getLoginName() + " to perform this operation";
            throw new AuthorizationException(message3);
        }
        AuthorizationEngine.logger.log(Level.FINER, "number of rows in incomming DO satisfied by the authorized DO. authorization cleared");
        return null;
    }
    
    private static List getToBeAuthorizedTables(final List tableNames, final List roleList, final String accessType) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINER, "filtering tobe authorized tables list based on simple tablePermissions for tables : {0}, roles : {1}, for accessType {2}", new Object[] { tableNames, roleList, accessType });
        try {
            for (final DataObject daO : getsimpleTablePermission(roleList, accessType)) {
                final Iterator tps = daO.getRows("AaaTablePermission");
                while (tps.hasNext()) {
                    final Row r = tps.next();
                    final String tName = (String)r.get("TABLE_NAME");
                    final String aType = (String)r.get("ACCESS_TYPE");
                    if (tName.equals("*") && aType.indexOf(accessType) >= 0) {
                        final Long permId = (Long)r.get("PERMISSION_ID");
                        Row permRow = new Row("AaaPermission");
                        permRow.set("PERMISSION_ID", (Object)permId);
                        permRow = daO.getFirstRow("AaaPermission", permRow);
                        Row serviceRow = new Row("AaaService");
                        serviceRow.set("SERVICE_ID", permRow.get("SERVICE_ID"));
                        serviceRow = daO.getFirstRow("AaaService", serviceRow);
                        String serviceName = (String)serviceRow.get("NAME");
                        AuthorizationEngine.logger.log(Level.FINEST, "service : {0} obtained for permission id : {1}", new Object[] { serviceName, permId });
                        if (serviceName == null) {
                            AuthorizationEngine.logger.log(Level.SEVERE, "service name obtained for permissionid : {0} is null", permId);
                            serviceName = "";
                        }
                        if (serviceName.equals("System")) {
                            AuthorizationEngine.logger.log(Level.FINER, "tobe authorized tables emptied as permission with * obtained for service System");
                            return null;
                        }
                        filterServiceSpecificTables(serviceName, tableNames);
                        AuthorizationEngine.logger.log(Level.FINER, "tables belonging to service : {0} removed from tobe authorized tablelist", serviceName);
                    }
                    else {
                        if (aType.indexOf(accessType) < 0 || !tableNames.contains(tName)) {
                            continue;
                        }
                        AuthorizationEngine.logger.log(Level.FINE, "table [{0}] satisfied by simple table permission : {1}", new Object[] { tName, r.get("PERMISSION_ID") });
                        tableNames.remove(tName);
                    }
                }
            }
        }
        catch (final DataAccessException dae) {
            AuthorizationEngine.logger.log(Level.FINEST, "DataAccessException caught while getting tobe Authorized tables : {0}", (Throwable)dae);
        }
        final String[] tNames = tableNames.toArray(new String[0]);
        final int tablesLength = tNames.length;
        final List toReturn = new ArrayList();
        String tableName = null;
        for (int i = 0; i < tablesLength; ++i) {
            tableName = tNames[i];
            if (!isTrustedRoleRequestForFGA(tableName, roleList)) {
                toReturn.add(tableName);
            }
        }
        AuthorizationEngine.logger.log(Level.FINEST, "About to return the table list : {0}", toReturn);
        return toReturn;
    }
    
    private static void filterServiceSpecificTables(final String serviceName, final List tableNames) throws DataAccessException {
        AuthorizationEngine.logger.log(Level.FINEST, "filterServiceSpecific tables invoked for service : {0} with tables : {1}", new Object[] { serviceName, tableNames });
        final Iterator moduleItr = AuthorizationUtil.getModulesForService(serviceName);
        String module = null;
        final List moduleList = new ArrayList();
        while (moduleItr.hasNext()) {
            module = (String)moduleItr.next().get("MODULENAME");
            moduleList.add(module);
        }
        if (serviceName.equals("ServerContainer")) {
            moduleList.add("DeploymentManager");
            moduleList.add("Persistence");
        }
        AuthorizationEngine.logger.log(Level.FINEST, "list of modules obtained for service : {0} are : {1}", new Object[] { serviceName, moduleList });
        for (int totalModules = moduleList.size(), i = 0; i < totalModules; ++i) {
            module = moduleList.get(i);
            final List tableList = AuthorizationUtil.getTablesForModule(module);
            AuthorizationEngine.logger.log(Level.FINEST, "tables belonging to module : {0} are : {1}", new Object[] { module, tableList });
            tableNames.removeAll(tableList);
            AuthorizationEngine.logger.log(Level.FINEST, "tables belonging to module : {0} removed from tobe authorized list : {1}", new Object[] { module, tableNames });
        }
    }
    
    private static SelectQuery checkForReadColumns(final Long permissionId, final SelectQuery query, final DataObject tabPermission) throws DataAccessException, RemoteException, QueryConstructionException {
        final SelectQuery squery = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery((Query)query);
        final List columnList = squery.getSelectColumns();
        final List permittedColumns = new ArrayList();
        final Column col = Column.getColumn("AaaTableReadPermission", "PERMISSION_ID");
        col.setType(-5);
        final Criteria criteria = new Criteria(col, (Object)permissionId, 0);
        final Iterator it = tabPermission.getRows("AaaTableReadPermission", criteria);
        if (it == null || !it.hasNext()) {
            AuthorizationEngine.logger.log(Level.FINEST, "tableReadPermission not defined for permissionid {0}, hence ignored", permissionId);
        }
        else {
            while (it.hasNext()) {
                final Row readColumn = it.next();
                permittedColumns.add(Column.getColumn((String)readColumn.get("TABLE_NAME"), (String)readColumn.get("COLUMN_NAME")));
            }
            for (int i = 0; i < columnList.size(); ++i) {
                final Column selectColumn = columnList.get(i);
                if (!permittedColumns.contains(selectColumn)) {
                    squery.removeSelectColumn(selectColumn);
                }
            }
        }
        return squery;
    }
    
    private static boolean checkForUpdateColumns(final Long permissionId, final DataObject dao, final DataObject tabPermission, final HashMap tableaccessSPI) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "checking the update columns for permission {0} against DO {1}", new Object[] { permissionId, dao });
        final Column col = Column.getColumn("AaaTableUpdatePermission", "PERMISSION_ID");
        col.setType(-5);
        final Criteria updateCriteria = new Criteria(col, (Object)permissionId, 0);
        final Iterator it = tabPermission.getRows("AaaTableUpdatePermission", updateCriteria);
        String columnName = null;
        final String modName = getModuleNameForPermissionId(permissionId);
        String tableName = null;
        Criteria criteria = null;
        while (it.hasNext()) {
            final Row updateColumn = it.next();
            AuthorizationEngine.logger.log(Level.FINEST, " Looping columns for permission {0}", updateColumn);
            columnName = (String)updateColumn.get("COLUMN_NAME");
            tableName = (String)updateColumn.get("TABLE_NAME");
            final String criteriaString = (String)updateColumn.get("UPDATE_CRITERIA");
            if (criteriaString == null) {
                continue;
            }
            if (criteriaString.indexOf("$") != -1) {
                criteria = substituteDollarVariables(criteriaString, tableName, modName);
            }
            else {
                criteria = new Criteria(criteriaString);
            }
            if (criteria == null) {
                continue;
            }
            criteria = QueryUtil.syncForDataType(criteria);
            AuthorizationEngine.logger.log(Level.FINE, " Criteria constructed for Update Column check is  {0}", criteria);
            final List aiList = dao.getOperations();
            AuthorizationEngine.logger.log(Level.FINE, " Action Info List is {0}", aiList);
            for (final ActionInfo ai : aiList) {
                final Row row = ai.getValue();
                AuthorizationEngine.logger.log(Level.FINEST, " Looping Action Info rows {0} Action Info is {1} Action Info Original Values is {2}", new Object[] { row, ai, row.getOriginalValues() });
                if (!tableName.equals(row.getTableName())) {
                    AuthorizationEngine.logger.log(Level.FINEST, " Table Name not matches {0} ActionInfo table name is {1}", new Object[] { tableName, row.getTableName() });
                }
                else {
                    final Map value = AuthorizationUtil.getRowAsMap(row);
                    final Column updateCol = Column.getColumn(tableName, columnName);
                    AuthorizationEngine.logger.log(Level.FINEST, " Looping update columns for matches {0} Hash Map {1}", new Object[] { updateCol, value });
                    if (!updateCriteriaCheckRequired(row, columnName)) {
                        AuthorizationEngine.logger.log(Level.FINE, " Update criteria need not be applied for this column ");
                    }
                    else {
                        if (!criteria.matches(value)) {
                            AuthorizationEngine.logger.log(Level.FINEST, " Update criteria Does not Matches ");
                            return false;
                        }
                        AuthorizationEngine.logger.log(Level.FINE, " Update criteria Matches ");
                        value.remove(columnName);
                    }
                }
            }
        }
        AuthorizationEngine.logger.log(Level.FINE, "checkForUpdateColumns succeeded");
        return true;
    }
    
    private static boolean updateCriteriaCheckRequired(final Row tableInstance, final String colName) {
        AuthorizationEngine.logger.log(Level.FINEST, " Update criteria check to be applied for this column {0} column {1}", new Object[] { tableInstance, colName });
        AuthorizationEngine.logger.log(Level.FINEST, " Update criteria Old Value : New Value is  {0} : {1} ", new Object[] { tableInstance.get(colName), tableInstance.getOriginalValue(colName) });
        if (tableInstance.get(colName) == null) {
            return tableInstance.getOriginalValue(colName) == null;
        }
        return !tableInstance.get(colName).equals(tableInstance.getOriginalValue(colName));
    }
    
    private static boolean isTrustedRoleRequestForFGA(final String tableName, final List roleNames) {
        AuthorizationEngine.logger.log(Level.FINEST, " isTrustedRoleRequestForFGA - for table {0} with roles : {1}", new Object[] { tableName, roleNames });
        String trustedrole = AuthorizationEngine.tableToTrustedRoleMap.get(tableName);
        AuthorizationEngine.logger.log(Level.FINEST, "trustedrole obtained for table is : {0}", trustedrole);
        if (trustedrole != null) {
            return roleNames.contains(trustedrole);
        }
        AuthorizationEngine.logger.log(Level.FINEST, "table : {0} not present in trusted role map. trying to fetch it from db", tableName);
        try {
            final String modulename = MetaDataUtil.getModuleNameOfTable(tableName);
            if (modulename == null) {
                AuthorizationEngine.logger.log(Level.FINER, "modulename obtained for table : {0} is null, hence unknown. return true", tableName);
                return true;
            }
            if (!AuthorizationUtil.isAuthznReqForModule(modulename)) {
                AuthorizationEngine.logger.log(Level.FINER, "authorization not required for table : {0} of module : {1}", new Object[] { tableName, modulename });
                return true;
            }
            trustedrole = AuthorizationUtil.getTrustedRole(modulename);
            AuthorizationEngine.logger.log(Level.FINEST, "modulename and trusted role obtained are : {0}, {1}", new Object[] { modulename, trustedrole });
            if (trustedrole != null) {
                AuthorizationEngine.tableToTrustedRoleMap.put(tableName, trustedrole);
                return roleNames.contains(trustedrole);
            }
        }
        catch (final Exception e) {
            AuthorizationEngine.logger.log(Level.SEVERE, "Exception caught while validating for trustedrole request : ", e);
        }
        return false;
    }
    
    private static DataObject getTablePermission(final List roles, final String accessType) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "getTablePermission invoked");
        final SelectQuery sq = getSQToFetchTablePermission(roles, accessType);
        final DataObject dobj = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
        AuthorizationEngine.logger.log(Level.FINEST, "tablePermission fetched for roleNames : {0} and access type : {1} is : {2}", new Object[] { roles, accessType, dobj });
        return dobj;
    }
    
    private static DataObject getmethodPermission() throws DataAccessException, RemoteException {
        SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaMethodPermission"));
        sq.addSelectColumn(Column.getColumn("AaaPermission", "*"));
        sq.addJoin(new Join("AaaMethodPermission", "AaaPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        final DataObject methodPermission = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
        sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaMethodPermission"));
        sq.addSelectColumn(Column.getColumn("AaaMethodPermission", "*"));
        sq.addSelectColumn(Column.getColumn("AaaMethodParams", "*"));
        sq.addJoin(new Join("AaaMethodPermission", "AaaMethodParams", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        sq.addJoin(new Join("AaaMethodPermission", "AaaPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        methodPermission.merge(AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq));
        sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaMethodPermission"));
        sq.addSelectColumn(Column.getColumn("AaaAce", "*"));
        sq.addJoin(new Join("AaaMethodPermission", "AaaAce", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        sq.addJoin(new Join("AaaMethodPermission", "AaaPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        methodPermission.merge(AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq));
        AuthorizationEngine.logger.log(Level.FINE, " Method Permissions loadded is {0}", methodPermission);
        return methodPermission;
    }
    
    private static List getsimpleTablePermission(final List roles, final String accessType) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "getSimpleTablePermission invoked");
        final CacheRepository cacheRep = CacheManager.getCacheRepository();
        final List dObjList = new ArrayList();
        final int size = (roles == null) ? 0 : roles.size();
        if (size == 0 || accessType == null) {
            return dObjList;
        }
        String roleName = null;
        for (int i = 0; i < size; ++i) {
            roleName = roles.get(i);
            final String key = roleName.concat("::" + accessType);
            DataObject dobj = (DataObject)cacheRep.getFromCache((Object)key, AuthorizationEngine.simplePermTableList, false);
            if (dobj == null) {
                AuthorizationEngine.logger.log(Level.FINEST, "simpleTablePerm obtained for key : {0} from cache is null. fetching from db", key);
                dobj = DataAccess.get(getSQToFetchSimpleTablePermission(roleName, accessType));
                cacheRep.addToCache((Object)key, (Object)dobj, AuthorizationEngine.simplePermTableList);
                AuthorizationEngine.logger.log(Level.FINEST, "simpleTablePerm fetched from db is : {0} added to cache", dobj);
            }
            else {
                AuthorizationEngine.logger.log(Level.FINEST, "simpleTablePerm obtained from cache for key : {0} is : {1}", new Object[] { key, dobj });
            }
            if (!dobj.isEmpty()) {
                dObjList.add(dobj);
            }
        }
        return dObjList;
    }
    
    private static SelectQuery getSQToFetchSimpleTablePermission(final String roleName, final String accessType) {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaRole", "AaaAce", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        sq.addJoin(new Join("AaaAce", "AaaPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 2));
        sq.addJoin(new Join("AaaPermission", "AaaTablePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 2));
        sq.addJoin(new Join("AaaPermission", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        sq.addJoin(new Join("AaaTablePermission", "AaaImpliedPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        sq.addJoin(new Join("AaaTablePermission", "AaaTableReadPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        sq.addJoin(new Join("AaaTablePermission", "AaaTableUpdatePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        Criteria criteria = new Criteria(Column.getColumn("AaaTablePermission", "CRITERIA"), (Object)null, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("AaaTableReadPermission", "PERMISSION_ID"), (Object)null, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaTableUpdatePermission", "PERMISSION_ID"), (Object)null, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaImpliedPermission", "PERMISSION_ID"), (Object)null, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaTablePermission", "ACCESS_TYPE"), (Object)("*" + accessType + "*"), 2));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roleName, 0));
        final ArrayList shortCol = new ArrayList();
        shortCol.add(new SortColumn(Column.getColumn("AaaRole", "ROLE_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaAce", "ROLE_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaPermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaImpliedPermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaTableReadPermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaTableUpdatePermission", "PERMISSION_ID"), true));
        sq.addSortColumns((List)shortCol);
        sq.setCriteria(criteria);
        return sq;
    }
    
    private static SelectQuery getSQToFetchTablePermission(final List roles, final String accessType) {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaRole", "AaaAce", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        sq.addJoin(new Join("AaaAce", "AaaPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 2));
        sq.addJoin(new Join("AaaPermission", "AaaTablePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 2));
        sq.addJoin(new Join("AaaTablePermission", "AaaImpliedPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        sq.addJoin(new Join("AaaImpliedPermission", "AaaImpliedTableColumn", new String[] { "PERMISSION_ID", "IMPLIED_TABLE_NAME" }, new String[] { "PERMISSION_ID", "IMPLIED_TABLE_NAME" }, 1));
        sq.addJoin(new Join("AaaImpliedPermission", "AaaImpliedByTableColumn", new String[] { "PERMISSION_ID", "IMPLIED_TABLE_NAME" }, new String[] { "PERMISSION_ID", "IMPLIED_TABLE_NAME" }, 1));
        sq.addJoin(new Join("AaaTablePermission", "AaaTableUpdatePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        sq.addJoin(new Join("AaaTablePermission", "AaaTableReadPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
        Criteria criteria = new Criteria(Column.getColumn("AaaTablePermission", "ACCESS_TYPE"), (Object)("*" + accessType + "*"), 2);
        criteria = criteria.and(Column.getColumn("AaaRole", "NAME"), (Object)roles.toArray(new String[0]), 8);
        final ArrayList shortCol = new ArrayList();
        shortCol.add(new SortColumn(Column.getColumn("AaaRole", "ROLE_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaAce", "ROLE_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaPermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaTablePermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaImpliedPermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaImpliedByTableColumn", "IMPLIED_TABLE_NAME"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaTableUpdatePermission", "PERMISSION_ID"), true));
        shortCol.add(new SortColumn(Column.getColumn("AaaTableReadPermission", "PERMISSION_ID"), true));
        sq.addSortColumns((List)shortCol);
        sq.setCriteria(criteria);
        return sq;
    }
    
    public static void setEjbToModuleMap(final HashMap map) {
        AuthorizationEngine.logger.log(Level.FINEST, "setEJBToModuleMap called with map : {0}", map);
        AuthorizationEngine.ejbToModuleMap = map;
    }
    
    public static void setFGAEnabled(final boolean authorization) {
        AuthorizationEngine.logger.log(Level.FINE, "fine grained authorization flag set to {0}", new Boolean(authorization));
        AuthorizationEngine.fGAEnabled = authorization;
    }
    
    public static boolean getFGAEnabled() {
        return AuthorizationEngine.fGAEnabled;
    }
    
    public static void setCGAEnabled(final boolean authorization) {
        AuthorizationEngine.logger.log(Level.FINE, "course grained authorization flag set to {0}", new Boolean(authorization));
        AuthorizationEngine.cGAEnabled = authorization;
    }
    
    public static boolean getCGAEnabled() {
        return AuthorizationEngine.cGAEnabled;
    }
    
    private static List getJoinsForTable(final SelectQuery sq, final List joins, final String tablename) {
        final List toReturn = new ArrayList();
        AuthorizationEngine.logger.log(Level.FINEST, " getJoinsForTable invoked with sq : {0}, join list : {1} and table : {2}", new Object[] { sq, joins, tablename });
        if (tablename == null) {
            return toReturn;
        }
        final List tableList = sq.getTableList();
        String baseName = null;
        Join join = null;
        for (int i = 0; i < joins.size(); ++i) {
            join = joins.get(i);
            final String refTableAlias = join.getReferencedTableAlias();
            AuthorizationEngine.logger.log(Level.FINEST, "ref table alias obtained from join : {0}", refTableAlias);
            if (refTableAlias.equals(tablename)) {
                toReturn.add(join);
                baseName = join.getBaseTableAlias();
                break;
            }
        }
        if (baseName == null) {
            AuthorizationEngine.logger.log(Level.FINEST, "Unable to find Join for table Swapping.... {0}", tablename);
            final List swJoins = swapJoins(joins);
            for (int j = 0; j < swJoins.size(); ++j) {
                join = swJoins.get(j);
                if (join.getReferencedTableAlias().equals(tablename)) {
                    toReturn.add(join);
                    baseName = join.getBaseTableAlias();
                    break;
                }
            }
            if (baseName == null) {
                AuthorizationEngine.logger.log(Level.FINEST, "basetable name is still null, hence no join added to list");
            }
        }
        if (AuthorizationUtil.isTablePresentInList(tableList, baseName)) {
            return toReturn;
        }
        final List tmpList = getJoinsForTable(sq, joins, baseName);
        toReturn.addAll(tmpList);
        return toReturn;
    }
    
    private static List getJoinsRequired(final SelectQuery sq, final List joins, final Criteria criteria, final String sqTableName, final List doTableList, final String type) {
        final List toReturn = new ArrayList();
        AuthorizationEngine.logger.log(Level.FINEST, "getJoinsRequired invoked with criteria : {0}, Sq table : {1}, type : {2}", new Object[] { criteria, sqTableName, type });
        if (type.equals("R") && criteria == null) {
            return toReturn;
        }
        final List criteriaList = AuthorizationUtil.getCriteriaAsList(criteria);
        AuthorizationEngine.logger.log(Level.FINEST, " Criteria list returned is {0}", criteriaList);
        final List tableList = sq.getTableList();
        List selectJoins = new ArrayList();
        for (int i = 0; i < criteriaList.size(); ++i) {
            final Column column = criteriaList.get(i).getColumn();
            final String tableName = column.getTableAlias();
            if (!AuthorizationUtil.isTablePresentInList(tableList, tableName)) {
                if (!AuthorizationUtil.isTablePresentInJoin(tableName, toReturn)) {
                    selectJoins = getJoinsForTable(sq, joins, tableName);
                    AuthorizationEngine.logger.log(Level.FINEST, " Total Joins List returned for table {0} is {1}", new Object[] { tableName, selectJoins });
                    toReturn.addAll(selectJoins);
                }
            }
        }
        if (doTableList != null) {
            for (int i = 0; i < doTableList.size(); ++i) {
                final String tableName2 = doTableList.get(i);
                if (!AuthorizationUtil.isTablePresentInList(tableList, tableName2)) {
                    if (!AuthorizationUtil.isTablePresentInJoin(tableName2, toReturn)) {
                        selectJoins = getJoinsForTable(sq, joins, tableName2);
                        AuthorizationEngine.logger.log(Level.FINEST, " Total Joins List returned for table {0} is {1}", new Object[] { tableName2, selectJoins });
                        toReturn.addAll(selectJoins);
                    }
                }
            }
        }
        if (!AuthorizationUtil.isTablePresentInList(tableList, sqTableName) && !AuthorizationUtil.isTablePresentInJoin(sqTableName, toReturn) && sqTableName != null) {
            selectJoins = getJoinsForTable(sq, joins, sqTableName);
            AuthorizationEngine.logger.log(Level.FINEST, " Total Joins List returned for table {0} is {1} ", new Object[] { sqTableName, selectJoins });
            toReturn.addAll(selectJoins);
        }
        AuthorizationEngine.logger.log(Level.FINER, "list of joins returned : {0} ", toReturn);
        return toReturn;
    }
    
    private static List swapJoins(final List joins) {
        final List toReturn = new ArrayList();
        for (int i = 0; i < joins.size(); ++i) {
            final Join tmp = joins.get(i);
            final int colNo = tmp.getNumberOfColumns();
            final String[] baseColumns = new String[colNo];
            final String[] referColumns = new String[colNo];
            for (int j = 0; j < colNo; ++j) {
                baseColumns[j] = tmp.getBaseTableColumn(j);
                referColumns[j] = tmp.getReferencedTableColumn(j);
            }
            final Join swapJoin = new Join(tmp.getReferencedTableName(), tmp.getBaseTableName(), referColumns, baseColumns, tmp.getReferencedTableAlias(), tmp.getBaseTableAlias(), tmp.getJoinType());
            AuthorizationEngine.logger.log(Level.FINEST, " Adding Swapped join {0}", swapJoin);
            toReturn.add(swapJoin);
        }
        AuthorizationEngine.logger.log(Level.FINEST, " Returning Swapped Joins List {0}", toReturn);
        return toReturn;
    }
    
    private static void constructJoins(final SelectQuery selectQuery, List joins) {
        int loopCnt = 0;
        AuthorizationEngine.logger.log(Level.FINEST, " Incomming Join List is {0} SelectQuery {1}", new Object[] { joins, selectQuery });
        final String tableName = selectQuery.getTableList().get(0).getTableAlias();
        boolean inBaseList = false;
        if (joins == null || joins.size() == 0) {
            return;
        }
        for (int i = 0; i < joins.size(); ++i) {
            final Join tmp = (Join)joins.get(i);
            if (tmp.getBaseTableAlias().equals(tableName)) {
                AuthorizationEngine.logger.log(Level.FINEST, " Basetable is in Joins Base List Proceeding ..... {0}", tableName);
                inBaseList = true;
                break;
            }
        }
        if (!inBaseList) {
            AuthorizationEngine.logger.log(Level.FINEST, " Basetable is not in Joins Base List swapping .... {0}", tableName);
            joins = swapJoins(joins);
        }
        while ((joins = addJoins(selectQuery, joins)).size() != 0) {
            if (++loopCnt > 100) {
                AuthorizationEngine.logger.log(Level.FINEST, " Loop Count Exceeds swapping ..... ");
                joins = swapJoins(joins);
                loopCnt = 0;
            }
        }
    }
    
    private static List addJoins(final SelectQuery selectQuery, final List joins) {
        final int join_length = joins.size();
        final List addLater = new ArrayList();
        for (int i = 0; i < join_length; ++i) {
            try {
                final Join join = joins.get(i);
                if (checkPresenceOfJoin(selectQuery, join)) {
                    AuthorizationEngine.logger.log(Level.FINEST, " Join already present in Select Query {0} Join is {1} Skipping ...", new Object[] { selectQuery, join });
                }
                else {
                    AuthorizationEngine.logger.log(Level.FINEST, " Constructing Add Join for Select Query {0} Join is {1} ", new Object[] { selectQuery, join });
                    selectQuery.addJoin(join);
                }
            }
            catch (final IllegalArgumentException e) {
                AuthorizationEngine.logger.log(Level.FINEST, " Adding Join Fails will be added later {0}", addLater);
                addLater.add(joins.get(i));
            }
        }
        return addLater;
    }
    
    private static boolean checkPresenceOfJoin(final SelectQuery selectQuery, final Join join) {
        final List tableList = selectQuery.getTableList();
        return checkTable(tableList, join.getBaseTableAlias()) && checkTable(tableList, join.getReferencedTableAlias());
    }
    
    private static boolean checkTable(final List tableList, final String tableName) {
        AuthorizationEngine.logger.log(Level.FINEST, " checkTable for List  {0} and table {1}", new Object[] { tableList, tableName });
        for (int size = tableList.size(), i = 0; i < size; ++i) {
            if (tableName.equals(tableList.get(i).getTableAlias())) {
                return true;
            }
        }
        return false;
    }
    
    private static SelectQuery getSQ(final Object permissionId, final SelectQuery inSq, final List doTableList, final String accessType, final DataObject tabPermission, final HashMap tableaccessSPI) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINE, "get scoped query for permission id {0} invoked", permissionId);
        AuthorizationEngine.logger.log(Level.FINE, "get scoped query for tabpermission {0} ", tabPermission);
        SelectQuery sQ = null;
        Row permission = new Row("AaaTablePermission");
        permission.set("PERMISSION_ID", permissionId);
        permission = tabPermission.getFirstRow("AaaTablePermission", permission);
        Criteria baseCriteria = null;
        final String criteria = (String)permission.get("CRITERIA");
        AuthorizationEngine.logger.log(Level.FINEST, "criteria obtained from base table permission is {0}", criteria);
        final String tableName = (String)permission.get("TABLE_NAME");
        final String modName = getModuleNameForPermissionId((Long)permissionId);
        if (criteria != null) {
            if (criteria.indexOf("$") != -1) {
                baseCriteria = substituteDollarVariables(criteria, tableName, modName);
            }
            else {
                baseCriteria = new Criteria(criteria);
            }
        }
        final Column column = Column.getColumn("AaaImpliedPermission", "PERMISSION_ID");
        column.setType(-5);
        final Criteria implied = new Criteria(column, permissionId, 0);
        Iterator it = tabPermission.getRows("AaaImpliedPermission", implied);
        while (it.hasNext()) {
            final Row implRow = it.next();
            final String impliedTableName = (String)implRow.get("IMPLIED_TABLE_NAME");
            final String crit = (String)implRow.get("CRITERIA");
            AuthorizationEngine.logger.log(Level.FINEST, "criteria obtained from implied table permission is {0}", crit);
            if (crit == null) {
                continue;
            }
            Criteria impliedCriteria = null;
            if (crit.indexOf("$") != -1) {
                impliedCriteria = substituteDollarVariables(crit, impliedTableName, modName);
            }
            else {
                impliedCriteria = new Criteria(crit);
            }
            if (impliedCriteria == null) {
                continue;
            }
            if (baseCriteria == null) {
                baseCriteria = impliedCriteria;
            }
            else {
                baseCriteria = baseCriteria.and(impliedCriteria);
            }
        }
        it = tabPermission.getRows("AaaImpliedPermission", implied);
        List joins = new ArrayList();
        while (it.hasNext()) {
            final Row row = it.next();
            final String impliedTableName2 = (String)row.get("IMPLIED_TABLE_NAME");
            final String impliedByTableName = (String)row.get("IMPLIED_BY_TABLE_NAME");
            Column col = Column.getColumn("AaaImpliedTableColumn", "PERMISSION_ID");
            Column col2 = Column.getColumn("AaaImpliedTableColumn", "IMPLIED_TABLE_NAME");
            col.setType(-5);
            col2.setType(12);
            final Iterator imIt = tabPermission.getRows("AaaImpliedTableColumn", new Criteria(col, permissionId, 0).and(new Criteria(col2, (Object)impliedTableName2, 0)));
            final Iterator imItCnt = tabPermission.getRows("AaaImpliedTableColumn", new Criteria(col, permissionId, 0).and(new Criteria(col2, (Object)impliedTableName2, 0)));
            if (imIt == null || !imIt.hasNext()) {
                AuthorizationEngine.logger.log(Level.SEVERE, "critical error, no joins specified {0}", imIt);
            }
            int joinLength = 0;
            while (imItCnt.hasNext()) {
                final Row param = imItCnt.next();
                ++joinLength;
            }
            AuthorizationEngine.logger.log(Level.FINEST, " Joins length is {0}", new Integer(joinLength));
            final String[] impliedColumn = new String[joinLength];
            while (imIt.hasNext()) {
                final Row param2 = imIt.next();
                final String type = (String)param2.get("COLUMN_NAME");
                final Long order = (Long)param2.get("COLUMN_ORDER");
                impliedColumn[order.intValue()] = type;
            }
            col = Column.getColumn("AaaImpliedByTableColumn", "PERMISSION_ID");
            col2 = Column.getColumn("AaaImpliedByTableColumn", "IMPLIED_TABLE_NAME");
            final Column col3 = Column.getColumn("AaaImpliedByTableColumn", "IMPLIED_BY_TABLE_NAME");
            col.setType(-5);
            col2.setType(12);
            col3.setType(12);
            final Iterator imByIt = tabPermission.getRows("AaaImpliedByTableColumn", new Criteria(col, permissionId, 0).and(new Criteria(col2, (Object)impliedTableName2, 0)).and(new Criteria(col3, (Object)impliedByTableName, 0)));
            final Iterator imByItCnt = tabPermission.getRows("AaaImpliedByTableColumn", new Criteria(col, permissionId, 0).and(new Criteria(col2, (Object)impliedTableName2, 0)).and(new Criteria(col3, (Object)impliedByTableName, 0)));
            if (imByIt == null || !imByIt.hasNext()) {
                AuthorizationEngine.logger.log(Level.FINEST, " Critical Issue relation columns in Implied relation is Null/Empty {0}", imByIt);
            }
            int joinByLength = 0;
            while (imByItCnt.hasNext()) {
                final Row param3 = imByItCnt.next();
                ++joinByLength;
            }
            if (joinByLength != joinLength) {
                AuthorizationEngine.logger.log(Level.FINEST, " Critical Error Join Length are not equals ImByColn length {0} ImColn Length {1}", new Object[] { new Integer(joinByLength), new Integer(joinLength) });
            }
            final String[] impliedByColumn = new String[joinByLength];
            while (imByIt.hasNext()) {
                final Row param4 = imByIt.next();
                final String type2 = (String)param4.get("COLUMN_NAME");
                final Long order2 = (Long)param4.get("COLUMN_ORDER");
                AuthorizationEngine.logger.log(Level.FINEST, " Array List  {0}", param4);
                AuthorizationEngine.logger.log(Level.FINEST, " Array List {0} {1} ", new Object[] { type2, order2 });
                impliedByColumn[order2.intValue()] = type2;
            }
            joins.add(new Join(impliedByTableName, impliedTableName2, impliedByColumn, impliedColumn, 2));
            AuthorizationEngine.logger.log(Level.FINEST, " Adding Join {0}", joins);
        }
        if (baseCriteria == null) {
            sQ = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        }
        sQ = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        String sqTableName = null;
        if (inSq != null) {
            sqTableName = inSq.getTableList().get(0).getTableName();
        }
        joins = getJoinsRequired(sQ, joins, baseCriteria, sqTableName, doTableList, accessType);
        constructJoins(sQ, joins);
        sQ.setCriteria(baseCriteria);
        AuthorizationEngine.logger.log(Level.FINE, "scoped query returned for permission id {0} is : {1}", new Object[] { permissionId, sQ });
        return sQ;
    }
    
    private static List modifyJoinsForAlias(final List joins, final HashMap map, final String aliasCount) {
        final List toReturn = new ArrayList();
        for (int i = 0; i < joins.size(); ++i) {
            final Join jo = joins.get(i);
            final List tmp = modifyJoinWithTableAlias(jo, map, aliasCount);
            toReturn.addAll(tmp);
        }
        return toReturn;
    }
    
    private static List modifyJoinWithTableAlias(final Join join, final HashMap map, final String aliasCount) {
        final List toReturn = new ArrayList();
        AuthorizationEngine.logger.log(Level.FINEST, "modifyJoinWithTableAlias invoked for join : {0}", join);
        final int columnNumber = join.getNumberOfColumns();
        final String[] baseTableColumns = new String[columnNumber];
        final String[] referencedTableColumns = new String[columnNumber];
        for (int i = 0; i < columnNumber; ++i) {
            baseTableColumns[i] = join.getBaseTableColumn(i);
            referencedTableColumns[i] = join.getReferencedTableColumn(i);
        }
        final String tableName = join.getBaseTableName();
        List aliasList = map.get(tableName);
        final String refTableName = join.getReferencedTableName();
        List raliasList = map.get(refTableName);
        if (aliasList == null) {
            aliasList = new ArrayList();
            aliasList.add(tableName + "_" + aliasCount);
        }
        if (raliasList == null) {
            raliasList = new ArrayList();
            raliasList.add(refTableName + "_" + aliasCount);
        }
        AuthorizationEngine.logger.log(Level.FINEST, " Alias list returned is {0} For table {1}", new Object[] { aliasList, tableName });
        final String tableAlias = aliasList.get(0);
        final String rtableAlias = raliasList.get(0);
        toReturn.add(new Join(join.getBaseTableName(), join.getReferencedTableName(), baseTableColumns, referencedTableColumns, tableAlias, rtableAlias, join.getJoinType()));
        AuthorizationEngine.logger.log(Level.FINEST, "Joins returned is {0}", toReturn);
        return toReturn;
    }
    
    private static Criteria modifyCriteriaWithTableAlias(final Criteria criteria, final HashMap map, final String aliasCount) {
        if (criteria == null) {
            return null;
        }
        AuthorizationEngine.logger.log(Level.FINE, "modify criteria with table alias invoked for criteria : {0} and alias count : {1}", new Object[] { criteria, aliasCount });
        final String operator = criteria.getOperator();
        final Criteria rightCriteria = criteria.getRightCriteria();
        Criteria leftCriteria = criteria.getLeftCriteria();
        Criteria returnedCriteria = null;
        AuthorizationEngine.logger.log(Level.FINEST, " Right Criteria passed further is {0} Operator is {1} ", new Object[] { rightCriteria, operator });
        if (rightCriteria != null) {
            returnedCriteria = modifyCriteriaWithTableAlias(rightCriteria, map, aliasCount);
            AuthorizationEngine.logger.log(Level.FINEST, " Returned Criteria is {0}", returnedCriteria);
        }
        AuthorizationEngine.logger.log(Level.FINEST, " Left Criteria being handled is {0}", leftCriteria);
        Criteria newCriteria = null;
        if (leftCriteria == null) {
            leftCriteria = criteria;
        }
        final Column column = leftCriteria.getColumn();
        final String tableName = column.getTableAlias();
        final List aliasList = map.get(tableName);
        AuthorizationEngine.logger.log(Level.FINEST, " Alias list returned is {0} For table {1}", new Object[] { aliasList, tableName });
        if (aliasList == null) {
            final String tableAlias = tableName + "_" + aliasCount;
            final Column newColumn = Column.getColumn(tableAlias, column.getColumnName(), column.getColumnAlias());
            newCriteria = new Criteria(newColumn, leftCriteria.getValue(), leftCriteria.getComparator());
            AuthorizationEngine.logger.log(Level.FINEST, " New table Alias appended criteria is {0}", newCriteria);
        }
        else {
            for (int i = 0; i < aliasList.size(); ++i) {
                final String tableAlias2 = aliasList.get(i);
                final Column newColumn2 = Column.getColumn(tableAlias2, column.getColumnName(), column.getColumnAlias());
                if (newCriteria == null) {
                    newCriteria = new Criteria(newColumn2, leftCriteria.getValue(), leftCriteria.getComparator());
                }
                else {
                    newCriteria = newCriteria.and(new Criteria(newColumn2, leftCriteria.getValue(), leftCriteria.getComparator()));
                }
            }
        }
        if (returnedCriteria != null) {
            AuthorizationEngine.logger.log(Level.FINEST, "Operator value is {0}", operator.toUpperCase());
            if (operator.toUpperCase().trim().equals("AND")) {
                newCriteria = newCriteria.and(returnedCriteria);
            }
            else {
                newCriteria = newCriteria.or(returnedCriteria);
            }
        }
        AuthorizationEngine.logger.log(Level.FINE, "criteria after alias replacement is {0}", newCriteria);
        return newCriteria;
    }
    
    private static String[] getMethodParams(final Long perm_id, final DataObject methPermission) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "In get method params for permission {0} ", perm_id);
        final Column col = Column.getColumn("AaaMethodParams", "PERMISSION_ID");
        col.setType(-5);
        final Criteria condition = new Criteria(col, (Object)perm_id, 0);
        final Iterator it = methPermission.getRows("AaaMethodParams", condition);
        final Iterator itCnt = methPermission.getRows("AaaMethodParams", condition);
        if (it == null || !it.hasNext()) {
            return new String[0];
        }
        int paramLength = 0;
        while (itCnt.hasNext()) {
            itCnt.next();
            ++paramLength;
        }
        final String[] toReturn = new String[paramLength];
        AuthorizationEngine.logger.log(Level.FINEST, " Param Length count is {0} ", new Integer(paramLength));
        while (it.hasNext()) {
            final Row param = it.next();
            final String type = (String)param.get("PARAM_TYPE");
            final Long order = (Long)param.get("PARAM_ORDER");
            toReturn[order.intValue()] = type;
        }
        return toReturn;
    }
    
    private static HashMap getTableAccessSPIs() throws DataAccessException {
        final HashMap toReturn = new HashMap();
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaTableAccessSPI"));
        sq.addSelectColumn(Column.getColumn("AaaTableAccessSPI", "*"));
        DataObject dao = null;
        dao = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
        final Iterator it = dao.getRows("AaaTableAccessSPI");
        while (it.hasNext()) {
            final Row row = it.next();
            final String tableName = (String)row.get("TABLE_NAME");
            final String modName = "AaaServer";
            final String className = (String)row.get("CRITERIA_CLASS");
            TableAccessSPI critCon = null;
            try {
                AuthorizationEngine.logger.log(Level.FINEST, "Criteria Update class = {0}", className);
                final Class updaterClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                critCon = updaterClass.newInstance();
            }
            catch (final Exception ex) {
                AuthorizationEngine.logger.log(Level.WARNING, "Error while loading TableAccessSPI ", ex);
            }
            if (critCon != null) {
                toReturn.put(tableName + modName, critCon);
            }
        }
        return toReturn;
    }
    
    private static List getAuthorizedRoles() {
        final Credential cred = AuthUtil.getUserCredential();
        if (cred == null) {
            return null;
        }
        final List roleNames = cred.getRoles();
        if (roleNames == null || roleNames.size() == 0) {
            return null;
        }
        return roleNames;
    }
    
    private static String getModuleNameForPermissionId(final Long permissionId) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "GETMODULENAME FOR PERMISSION ID CALLED. IGNORED .... RETURN");
        return "AaaServer";
    }
    
    private static String getRoleModule(final String roleName) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, "GETROLEMODULE CALLED . IGNORED - RETURN");
        return "AaaServer";
    }
    
    private static List getImpliedTables(final Long permissionId, final DataObject tabPermission) throws DataAccessException, RemoteException {
        AuthorizationEngine.logger.log(Level.FINEST, " Get implied tables for the permission {0}", permissionId);
        final List perms = new ArrayList();
        final Row condition = new Row("AaaImpliedPermission");
        condition.set("PERMISSION_ID", (Object)permissionId);
        final Column col = Column.getColumn("AaaImpliedPermission", "PERMISSION_ID");
        col.setType(-5);
        final Iterator it = tabPermission.getRows("AaaImpliedPermission", new Criteria(col, (Object)permissionId, 0));
        Row perm_row = null;
        while (it.hasNext()) {
            perm_row = it.next();
            perms.add(perm_row.get("IMPLIED_TABLE_NAME"));
        }
        AuthorizationEngine.logger.log(Level.FINEST, " Implied Permission list returned is {0}", perms);
        return perms;
    }
    
    private static List getTablePermission(final String roleName, final String type, final DataObject tabPermission) throws DataAccessException, RemoteException {
        final List toReturn = new ArrayList();
        AuthorizationEngine.logger.log(Level.FINEST, "getTablePermission invoked for role : {0} and accessType : {1}", new Object[] { roleName, type });
        try {
            final Long roleId = (Long)AuthDBUtil.getObject("AaaRole", "ROLE_ID", "NAME", (Object)roleName);
            final Column col = Column.getColumn("AaaAce", "ROLE_ID");
            col.setType(-5);
            Criteria condition = new Criteria(col, (Object)roleId, 0);
            final Iterator ace = tabPermission.getRows("AaaAce", condition);
            while (ace.hasNext()) {
                final Row ace_row = ace.next();
                final Object perm_id = ace_row.get("PERMISSION_ID");
                final Column tp_col = Column.getColumn("AaaTablePermission", "PERMISSION_ID");
                tp_col.setType(-5);
                condition = new Criteria(tp_col, perm_id, 0);
                final Iterator tp = tabPermission.getRows("AaaTablePermission", condition);
                if (tp.hasNext()) {
                    final Row tp_row = tp.next();
                    final String accessType = (String)tp_row.get("ACCESS_TYPE");
                    if (accessType.toUpperCase().indexOf(type) == -1) {
                        continue;
                    }
                    AuthorizationEngine.logger.log(Level.FINEST, "table permission satisfied is {0}", perm_id);
                    toReturn.add(tp_row);
                }
            }
            AuthorizationEngine.logger.log(Level.FINEST, "table permission list statisfying the role {0} ", toReturn);
            return toReturn;
        }
        catch (final DataAccessException e) {
            AuthorizationEngine.logger.log(Level.FINE, " No permission for role {0} ", roleName);
            AuthorizationEngine.logger.log(Level.FINE, "", (Throwable)e);
        }
        catch (final NoSuchElementException nse) {
            AuthorizationEngine.logger.log(Level.FINE, " No permission for role {0} ", roleName);
            AuthorizationEngine.logger.log(Level.FINE, "", nse);
            return null;
        }
        return null;
    }
    
    public static void addTableCacheForUnModelledModules() throws Exception {
        AuthorizationEngine.logger.log(Level.FINEST, "addTableCacheForUnModelledModule called");
        final String trustedRole = AuthorizationUtil.getTrustedRole("CoreContainer");
        final HashMap moduleTableMap = new HashMap();
        final Set allModules = new HashSet(MetaDataUtil.getAllModuleNames());
        final DataObject moduleDO = DataAccess.get("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), (Object)"*", 2));
        final Iterator modItr = moduleDO.getRows("Module");
        final List moduleDbList = new ArrayList();
        String module = null;
        while (modItr.hasNext()) {
            module = (String)modItr.next().get("MODULENAME");
            if (!moduleDbList.contains(module)) {
                moduleDbList.add(module);
            }
        }
        AuthorizationEngine.logger.log(Level.FINEST, "Module list obtained from DB is : {0}", moduleDbList);
        allModules.removeAll(moduleDbList);
        AuthorizationEngine.logger.log(Level.FINEST, "after removing the modules in db : {0}", allModules);
        final Iterator itr = allModules.iterator();
        while (itr.hasNext()) {
            module = itr.next();
            final DataDictionary dd = MetaDataUtil.getDataDictionary(module);
            final List tableDefnList = dd.getTableDefinitions();
            final int size = tableDefnList.size();
            TableDefinition tableDefn = null;
            for (int j = 0; j < size; ++j) {
                tableDefn = tableDefnList.get(j);
                moduleTableMap.put(tableDefn.getTableName(), trustedRole);
            }
            AuthorizationEngine.logger.log(Level.FINEST, "table cache for unmodelled module : {0} is : {1}", new Object[] { module, moduleTableMap });
            AuthorizationEngine.tableToTrustedRoleMap.putAll(moduleTableMap);
            AuthorizationEngine.logger.log(Level.FINEST, "tableToTrustedRoleMap cache after add for unmodelled module : {0} is  : {1}", new Object[] { module, AuthorizationEngine.tableToTrustedRoleMap });
        }
    }
    
    public static void updateTableCache(final String module, final boolean add) throws Exception {
        AuthorizationEngine.logger.log(Level.FINEST, "addTableCache called for module : {0}", module);
        final List tablesInModule = new ArrayList();
        final DataDictionary dd = MetaDataUtil.getDataDictionary(module);
        if (dd == null) {
            AuthorizationEngine.logger.log(Level.FINEST, "data dictionary does not exist for the module : {0}. Ignored", module);
            return;
        }
        final List tableDefnList = dd.getTableDefinitions();
        final int size = tableDefnList.size();
        TableDefinition tableDefn = null;
        for (int i = 0; i < size; ++i) {
            tableDefn = tableDefnList.get(i);
            tablesInModule.add(tableDefn.getTableName());
        }
        AuthorizationEngine.logger.log(Level.FINEST, "Table list in module : {0} = {1}", new Object[] { module, tablesInModule });
        final int totalTables = tablesInModule.size();
        if (add) {
            final String trustedRole = AuthorizationUtil.getTrustedRole(module);
            if (trustedRole == null) {
                AuthorizationEngine.logger.log(Level.WARNING, "Trusted role obtained for module : {0} is null. Ignoring tables for updating table cache", module);
                return;
            }
            final HashMap moduleTableMap = new HashMap();
            for (int j = 0; j < totalTables; ++j) {
                moduleTableMap.put(tablesInModule.get(j), trustedRole);
            }
            AuthorizationEngine.logger.log(Level.FINEST, "table cache for module : {0} is : {1}", new Object[] { module, moduleTableMap });
            AuthorizationEngine.tableToTrustedRoleMap.putAll(moduleTableMap);
            AuthorizationEngine.logger.log(Level.FINEST, "tableToTrustedRoleMap cache after add for module : {0} is  : {1}", new Object[] { module, AuthorizationEngine.tableToTrustedRoleMap });
        }
        else {
            for (int k = 0; k < totalTables; ++k) {
                AuthorizationEngine.tableToTrustedRoleMap.remove(tablesInModule.get(k));
            }
        }
    }
    
    public static void removeTableCache(final String module) {
        AuthorizationEngine.logger.log(Level.FINEST, "REMOVE TABLE CACHE CALLED FOR MODULE - {0}. IGNORED", module);
    }
    
    private static Criteria substituteDollarVariables(String criteriaSql, final String tableName, final String moduleName) {
        AuthorizationEngine.logger.log(Level.FINE, "substituting dollar variables for criteria : {0}", criteriaSql);
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            if (criteriaSql.indexOf("$AAAUSER.USER_ID") != -1) {
                AuthorizationEngine.logger.log(Level.FINEST, "substituting {0} for $AAAUSER.USER_ID", credential.getUserId());
                criteriaSql = criteriaSql.replaceAll("[$]AAAUSER.USER_ID", String.valueOf(credential.getUserId()));
            }
            if (criteriaSql.indexOf("$AAALOGIN.LOGIN_ID") != -1) {
                AuthorizationEngine.logger.log(Level.FINEST, "substituting {0} for $AAALOGIN.LOGIN_ID", credential.getLoginId());
                criteriaSql = criteriaSql.replaceAll("[$]AAALOGIN.LOGIN_ID", String.valueOf(credential.getLoginId()));
            }
            if (criteriaSql.indexOf("$AAAACCOUNT.ACCOUNT_ID") != -1) {
                AuthorizationEngine.logger.log(Level.FINEST, "substituting {0} for $AAAACCOUNT.ACCOUNT_ID", credential.getAccountId());
                criteriaSql = criteriaSql.replaceAll("[$]AAAACCOUNT.ACCOUNT_ID", String.valueOf(credential.getAccountId()));
            }
            if (criteriaSql.indexOf("$AAALOGIN.NAME") != -1) {
                AuthorizationEngine.logger.log(Level.FINEST, "substituting {0} for $AAALOGIN.NAME", credential.getLoginName());
                criteriaSql = criteriaSql.replaceAll("[$]AAALOGIN.NAME", String.valueOf(credential.getLoginName()));
            }
        }
        else {
            AuthorizationEngine.logger.log(Level.SEVERE, "Credential object obtained is null, unable to replace aaa dollar variables");
        }
        AuthorizationEngine.logger.log(Level.FINE, "criteria after replacing aaa dollar variables : {0}", criteriaSql);
        Criteria criteria = null;
        try {
            criteria = new Criteria(criteriaSql);
            if (criteriaSql.indexOf("$") != -1) {
                final HashMap tableaccessSPI = getTableAccessSPIs();
                final String key = tableName + moduleName;
                final TableAccessSPI spi = tableaccessSPI.get(key);
                if (spi != null) {
                    criteria = spi.update(criteria);
                }
                else {
                    criteria = null;
                    AuthorizationEngine.logger.log(Level.SEVERE, "could not load TableAccessSPI class : {0} for table : {1} of module : {2}", new Object[] { tableaccessSPI, tableName, moduleName });
                }
            }
        }
        catch (final DataAccessException dae) {
            AuthorizationEngine.logger.log(Level.SEVERE, "DataAccessException occured while trying to replace dollar variables : {0}", (Throwable)dae);
        }
        return criteria;
    }
    
    static {
        AuthorizationEngine.logger = null;
        AuthorizationEngine.ejbToModuleMap = null;
        AuthorizationEngine.tableToTrustedRoleMap = new HashMap();
        AuthorizationEngine.isLocal = false;
        AuthorizationEngine.fGAEnabled = false;
        AuthorizationEngine.cGAEnabled = false;
        AuthorizationEngine.fGAEnabledMap = new HashMap();
        AuthorizationEngine.aliasCount = 0;
        AuthorizationEngine.simplePermTableList = null;
        AuthorizationEngine.logger = Logger.getLogger(AuthorizationEngine.class.getName());
        (AuthorizationEngine.simplePermTableList = new ArrayList()).add("AaaService");
        AuthorizationEngine.simplePermTableList.add("AaaRole");
        AuthorizationEngine.simplePermTableList.add("AaaAce");
        AuthorizationEngine.simplePermTableList.add("AaaPermission");
        AuthorizationEngine.simplePermTableList.add("AaaTablePermission");
        AuthorizationEngine.simplePermTableList.add("AaaTableReadPermission");
        AuthorizationEngine.simplePermTableList.add("AaaTableUpdatePermission");
        AuthorizationEngine.simplePermTableList.add("AaaImpliedPermission");
        AuthorizationEngine.logger.log(Level.FINEST, "simplePermTableList initialized to : {0}", AuthorizationEngine.simplePermTableList);
    }
}
