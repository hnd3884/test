package com.adventnet.authorization.util;

import com.adventnet.persistence.QueryConstructor;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Collection;
import java.util.Map;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.util.AuthDBUtil;
import java.util.logging.Logger;

public class AuthorizationUtil
{
    private static Logger logger;
    
    public static Long getServiceIdForApplication(final String module) throws DataAccessException {
        final Long moduleId = (Long)AuthDBUtil.getObject("Module", "MODULE_ID", "MODULENAME", (Object)module);
        return (Long)AuthDBUtil.getObject("AaaModuleService", "SERVICE_ID", "MODULE_ID", (Object)moduleId);
    }
    
    public static Long getServiceId(final String serviceName) throws Exception {
        return (Long)AuthDBUtil.getObject("AaaService", "SERVICE_ID", "NAME", (Object)serviceName);
    }
    
    public static String getServiceName(final Long serviceId) throws Exception {
        return (String)AuthDBUtil.getObject("AaaService", "NAME", "SERVICE_ID", (Object)serviceId);
    }
    
    public static String getTrustedRole(final String modulename) {
        String trustedRole = null;
        try {
            final Join module_appserv = new Join("Module", "AaaModuleService", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
            final Join appserv_serv = new Join("AaaModuleService", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2);
            final Join serv_tr = new Join("AaaService", "AaaTrustedRole", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2);
            final Join tr_r = new Join("AaaTrustedRole", "AaaRole", new String[] { "TRUSTED_ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            final Criteria criteria = new Criteria(Column.getColumn("Module", "MODULENAME"), (Object)modulename, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Module"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.addJoin(module_appserv);
            sq.addJoin(appserv_serv);
            sq.addJoin(serv_tr);
            sq.addJoin(tr_r);
            sq.setCriteria(criteria);
            final DataObject dobj = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
            AuthorizationUtil.logger.log(Level.FINEST, "Trusted Role DataObject obtained is : {0}", dobj);
            trustedRole = (String)dobj.getFirstValue("AaaRole", "NAME");
            AuthorizationUtil.logger.log(Level.FINEST, "TrustedRole obtained for module : {0}, Service : {1} is : {2}", new Object[] { modulename, dobj.getFirstValue("AaaService", "NAME"), trustedRole });
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while trying to get trusted role for application : {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught while trying to get trusted role for application : {0}", e);
        }
        return trustedRole;
    }
    
    public static List getTablesFromQuery(final SelectQuery sq) {
        final List toReturn = new ArrayList();
        final List colList = sq.getSelectColumns();
        final List tabList = sq.getTableList();
        for (int i = 0; i < colList.size(); ++i) {
            final Column col = colList.get(i);
            if (col.getColumnName().equals("*") && col.getTableAlias() == null) {
                return tabList;
            }
            final String tableAlias = col.getTableAlias();
            for (int j = 0; j < tabList.size(); ++j) {
                if (tabList.get(j).getTableAlias().equals(tableAlias)) {
                    toReturn.add(tabList.get(j));
                    break;
                }
            }
        }
        return toReturn;
    }
    
    public static SelectQuery getSelectQueryForTotal(SelectQuery selectQuery) {
        final List tables = selectQuery.getTableList();
        final List joins = selectQuery.getJoins();
        final List groupByColumns = selectQuery.getGroupByColumns();
        final Criteria criteria = selectQuery.getCriteria();
        final Table baseTable = tables.remove(0);
        selectQuery = (SelectQuery)new SelectQueryImpl(baseTable);
        for (int size = tables.size(), i = 0; i < size; ++i) {
            final Join join = joins.get(i);
            selectQuery.addJoin(join);
        }
        selectQuery.addGroupByColumns(groupByColumns);
        selectQuery.setCriteria(criteria);
        final Column pkColumn = Column.getColumn((String)null, "*");
        final Column countStar = pkColumn.count();
        selectQuery.addSelectColumn(countStar);
        return selectQuery;
    }
    
    public static int getCount(final Iterator it) {
        int i = 0;
        while (it.hasNext()) {
            it.next();
            ++i;
        }
        return i;
    }
    
    public static HashMap getTableAliasForSQ(final SelectQuery sq) {
        final List tmpList = sq.getTableList();
        final HashMap toReturn = new HashMap();
        for (int i = 0; i < tmpList.size(); ++i) {
            List ls = new ArrayList();
            final Table table = tmpList.get(i);
            if (toReturn.containsKey(table.getTableName())) {
                ls = toReturn.get(table.getTableName());
                ls.add(table.getTableAlias());
            }
            else {
                ls.add(table.getTableAlias());
            }
            toReturn.put(table.getTableName(), ls);
        }
        AuthorizationUtil.logger.log(Level.FINEST, " Table Name Alias map returned is {0}", toReturn);
        return toReturn;
    }
    
    public static boolean isTablePresentInJoin(final String tablename, final List joinList) {
        AuthorizationUtil.logger.log(Level.FINEST, " isTablePresentInJoin - Joins {0} for table {1} ", new Object[] { joinList, tablename });
        boolean isPresent = false;
        if (tablename == null) {
            return isPresent;
        }
        for (int i = 0; i < joinList.size(); ++i) {
            if (joinList.get(i).getBaseTableAlias().equals(tablename) || joinList.get(i).getReferencedTableAlias().equals(tablename)) {
                isPresent = true;
            }
        }
        AuthorizationUtil.logger.log(Level.FINEST, "isTablePresentInJoin returning : {0}", new Boolean(isPresent));
        return isPresent;
    }
    
    public static Map getRowAsMap(final Row row) {
        final Map nameValueMap = new HashMap();
        final List colNames = row.getColumns();
        for (int i = 0; i < colNames.size(); ++i) {
            final Column tmpCol = Column.getColumn(row.getTableName(), (String)colNames.get(i));
            nameValueMap.put(tmpCol, row.get((String)colNames.get(i)));
        }
        return nameValueMap;
    }
    
    public static List getCriteriaAsList(final Criteria criteria) {
        AuthorizationUtil.logger.log(Level.FINEST, "Criteria comming for getCriteria is {0}", criteria);
        final List toReturn = new ArrayList();
        if (criteria == null) {
            return toReturn;
        }
        final String operator = criteria.getOperator();
        final Criteria rightCriteria = criteria.getRightCriteria();
        final Criteria leftCriteria = criteria.getLeftCriteria();
        AuthorizationUtil.logger.log(Level.FINEST, " Left Criteria being handled is {0}", leftCriteria);
        if (leftCriteria != null) {
            final List criList = getCriteriaAsList(leftCriteria);
            AuthorizationUtil.logger.log(Level.FINEST, " Returned Left Criteria are {0}", criList);
            toReturn.addAll(criList);
        }
        AuthorizationUtil.logger.log(Level.FINEST, " Right Criteria being handled is {0}", rightCriteria);
        if (rightCriteria != null) {
            final List criList = getCriteriaAsList(rightCriteria);
            AuthorizationUtil.logger.log(Level.FINEST, " Returned Right Criteria are {0}", criList);
            toReturn.addAll(criList);
        }
        if (rightCriteria == null && leftCriteria == null) {
            toReturn.add(criteria);
        }
        return toReturn;
    }
    
    public static boolean isTablePresentInList(final List tableList, final String tableName) {
        AuthorizationUtil.logger.log(Level.FINEST, "isTablePresentInList for List {0} for table {1} ", new Object[] { tableList, tableName });
        if (tableName == null) {
            return false;
        }
        for (int size = tableList.size(), i = 0; i < size; ++i) {
            if (tableName.equals(tableList.get(i).getTableName())) {
                return true;
            }
        }
        AuthorizationUtil.logger.log(Level.FINEST, " returning false ");
        return false;
    }
    
    public static List trimJoins(final SelectQuery sq, final List joins) {
        AuthorizationUtil.logger.log(Level.FINEST, " Trimming Joins List  {0} for Select Query {1} ", new Object[] { joins, sq });
        final List toReturn = new ArrayList();
        final List tableList = sq.getTableList();
        for (int i = 0; i < joins.size(); ++i) {
            final Join ji = joins.get(i);
            final String baseTableName = ji.getBaseTableName();
            final String refTableName = ji.getReferencedTableName();
            if (!isTablePresentInList(tableList, baseTableName) || !isTablePresentInList(tableList, refTableName)) {
                toReturn.add(ji);
            }
        }
        AuthorizationUtil.logger.log(Level.FINEST, " Joins list after trimming is {0}", toReturn);
        return toReturn;
    }
    
    public static boolean paramsMatches(final Class[] incommingParams, final String[] defParams) {
        AuthorizationUtil.logger.log(Level.FINEST, "param matches called");
        if (incommingParams.length != defParams.length) {
            AuthorizationUtil.logger.log(Level.FINEST, " Argument length doesn't matches {0}  {1}", new Object[] { new Integer(incommingParams.length), new Integer(defParams.length) });
            return false;
        }
        for (int i = 0; i < defParams.length; ++i) {
            AuthorizationUtil.logger.log(Level.FINEST, " Method param defined {0} incomming {1} ", new Object[] { defParams[i], incommingParams[i] });
            if (!incommingParams[i].getName().equals(defParams[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAuthznReqForModule(final String module) {
        AuthorizationUtil.logger.log(Level.FINEST, "isAuthznReqForModule invoked for module : {0}", module);
        try {
            final DataObject moduleDO = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), (Object)module, 0));
            if (moduleDO != null && moduleDO.containsTable("Module")) {
                final Long moduleId = (Long)moduleDO.getRow("Module").get("MODULE_ID");
                final DataObject disableDO = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get("AaaDisableAuth", new Criteria(Column.getColumn("AaaDisableAuth", "MODULE_ID"), (Object)moduleId, 0));
                return !disableDO.containsTable("AaaDisableAuth");
            }
            return true;
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
            AuthorizationUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to check isAuthznReqForModule {0}", dae.getMessage());
            return true;
        }
    }
    
    public static Iterator getModulesForService(final String serviceName) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaService"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaService", "AaaModuleService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        sq.addJoin(new Join("AaaModuleService", "Module", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
        sq.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0));
        DataObject dobj = null;
        try {
            dobj = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        return dobj.getRows("Module");
    }
    
    public static List getTablesForModule(final String moduleName) {
        final List tableNameList = new ArrayList();
        if (moduleName == null) {
            return tableNameList;
        }
        try {
            final DataDictionary dd = MetaDataUtil.getDataDictionary(moduleName);
            if (dd == null) {
                return tableNameList;
            }
            final List tableDefnList = dd.getTableDefinitions();
            final int size = tableDefnList.size();
            TableDefinition tableDefn = null;
            for (int i = 0; i < size; ++i) {
                tableDefn = tableDefnList.get(i);
                tableNameList.add(tableDefn.getTableName());
            }
        }
        catch (final MetaDataException mde) {
            AuthorizationUtil.logger.log(Level.SEVERE, "MetaDataException occured while fetching tablenames for module : {0}", (Throwable)mde);
        }
        AuthorizationUtil.logger.log(Level.FINEST, "tablenames obtained for module : {0} is {1}", new Object[] { moduleName, tableNameList });
        return tableNameList;
    }
    
    public static DataObject createPermissionForSelectQuery(final SelectQuery query, final String pname, final String ptype, final String serviceName) {
        DataObject returnObj = null;
        AuthorizationUtil.logger.log(Level.FINEST, "creating simple permission for selectquery : {0} with access type {1}", new Object[] { query, ptype });
        if (query != null) {
            try {
                final List tables = query.getTableList();
                final List joins = query.getJoins();
                final List selectColumns = query.getSelectColumns();
                final Criteria criteria = query.getCriteria();
                final List criteriaList = getCriteriaAsList(criteria);
                System.out.println("criteriaList in sq is... " + criteriaList);
                final Table baseTable = tables.remove(0);
                Criteria tblPermCrit = null;
                final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
                final DataObject dobj = persistence.constructDataObject();
                final long permissionId = getPermissionID(pname);
                if (permissionId == -1L) {
                    final Row permRow = new Row("AaaPermission");
                    permRow.set("SERVICE_ID", (Object)getServiceId(serviceName));
                    permRow.set("PERMISSION_NAME", (Object)pname);
                    dobj.addRow(permRow);
                    for (int cr = 0; cr < criteriaList.size(); ++cr) {
                        final Criteria andCrit = criteriaList.get(cr);
                        if (andCrit.getColumn().getTableAlias().equalsIgnoreCase(baseTable.getTableName())) {
                            if (tblPermCrit != null) {
                                tblPermCrit.and(andCrit);
                            }
                            else {
                                tblPermCrit = andCrit;
                            }
                        }
                    }
                    final Row tblPermRow = new Row("AaaTablePermission");
                    tblPermRow.set("PERMISSION_ID", permRow.get("PERMISSION_ID"));
                    tblPermRow.set("TABLE_NAME", (Object)baseTable.getTableName());
                    tblPermRow.set("ACCESS_TYPE", (Object)ptype);
                    if (tblPermCrit != null) {
                        tblPermRow.set("CRITERIA", (Object)tblPermCrit.toString());
                    }
                    dobj.addRow(tblPermRow);
                    for (int j = 0; j < selectColumns.size(); ++j) {
                        final Column selectColumn = selectColumns.get(j);
                        final String colName = selectColumn.getColumnName();
                        if (!colName.equals("*")) {
                            final Row readtblPermRow = new Row("AaaTableReadPermission");
                            readtblPermRow.set("PERMISSION_ID", permRow.get("PERMISSION_ID"));
                            readtblPermRow.set("TABLE_NAME", (Object)selectColumn.getTableAlias());
                            readtblPermRow.set("COLUMN_NAME", (Object)colName);
                            dobj.addRow(readtblPermRow);
                        }
                    }
                    for (int i = 0; i < joins.size(); ++i) {
                        final Join k = joins.get(i);
                        Criteria impPermCrit = null;
                        for (int impcr = 0; impcr < criteriaList.size(); ++impcr) {
                            final Criteria andCrit2 = criteriaList.get(impcr);
                            if (andCrit2.getColumn().getTableAlias().equalsIgnoreCase(k.getReferencedTableName())) {
                                if (impPermCrit != null) {
                                    impPermCrit = impPermCrit.and(andCrit2);
                                }
                                else {
                                    impPermCrit = andCrit2;
                                }
                            }
                        }
                        final Row impRow = new Row("AaaImpliedPermission");
                        impRow.set("PERMISSION_ID", permRow.get("PERMISSION_ID"));
                        impRow.set("IMPLIED_TABLE_NAME", (Object)k.getReferencedTableName());
                        impRow.set("IMPLIED_BY_TABLE_NAME", (Object)k.getBaseTableName());
                        if (impPermCrit != null) {
                            impRow.set("CRITERIA", (Object)impPermCrit.toString());
                        }
                        dobj.addRow(impRow);
                        for (int l = 0; l < k.getReferencedTableColumns().length; ++l) {
                            final Row imptcRow = new Row("AaaImpliedTableColumn");
                            imptcRow.set("PERMISSION_ID", permRow.get("PERMISSION_ID"));
                            imptcRow.set("IMPLIED_TABLE_NAME", (Object)k.getReferencedTableName());
                            imptcRow.set("COLUMN_NAME", (Object)k.getReferencedTableColumn(l));
                            imptcRow.set("COLUMN_ORDER", (Object)new Long(l));
                            dobj.addRow(imptcRow);
                        }
                        for (int kk = 0; kk < k.getBaseTableColumns().length; ++kk) {
                            final Row impbtcRow = new Row("AaaImpliedByTableColumn");
                            impbtcRow.set("PERMISSION_ID", permRow.get("PERMISSION_ID"));
                            impbtcRow.set("IMPLIED_TABLE_NAME", (Object)k.getReferencedTableName());
                            impbtcRow.set("IMPLIED_BY_TABLE_NAME", (Object)k.getBaseTableName());
                            impbtcRow.set("COLUMN_NAME", (Object)k.getBaseTableColumn(kk));
                            impbtcRow.set("COLUMN_ORDER", (Object)new Long(kk));
                            dobj.addRow(impbtcRow);
                        }
                    }
                    returnObj = persistence.add(dobj);
                    AuthorizationUtil.logger.log(Level.FINEST, "permission dobj is : {0}", new Object[] { dobj });
                }
                else {
                    persistence.delete(new Criteria(Column.getColumn("AaaImpliedPermission", "PERMISSION_ID"), (Object)new Long(permissionId), 0));
                    persistence.delete(new Criteria(Column.getColumn("AaaTableUpdatePermission", "PERMISSION_ID"), (Object)new Long(permissionId), 0));
                    persistence.delete(new Criteria(Column.getColumn("AaaTableReadPermission", "PERMISSION_ID"), (Object)new Long(permissionId), 0));
                    final DataObject tblpermobj = persistence.get("AaaTablePermission", new Criteria(Column.getColumn("AaaTablePermission", "PERMISSION_ID"), (Object)new Long(permissionId), 0));
                    for (int cr = 0; cr < criteriaList.size(); ++cr) {
                        final Criteria andCrit = criteriaList.get(cr);
                        if (andCrit.getColumn().getTableAlias().equalsIgnoreCase(baseTable.getTableName())) {
                            if (tblPermCrit != null) {
                                tblPermCrit.and(andCrit);
                            }
                            else {
                                tblPermCrit = andCrit;
                            }
                        }
                    }
                    final Row tblPermRow = tblpermobj.getRow("AaaTablePermission");
                    tblPermRow.set("TABLE_NAME", (Object)baseTable.getTableName());
                    tblPermRow.set("ACCESS_TYPE", (Object)ptype);
                    if (tblPermCrit != null) {
                        tblPermRow.set("CRITERIA", (Object)tblPermCrit.toString());
                    }
                    else {
                        tblPermRow.set("CRITERIA", (Object)null);
                    }
                    tblpermobj.updateRow(tblPermRow);
                    persistence.update(tblpermobj);
                    for (int m = 0; m < selectColumns.size(); ++m) {
                        final Column selectColumn = selectColumns.get(m);
                        final String colName = selectColumn.getColumnName();
                        if (!colName.equals("*")) {
                            final Row readtblPermRow = new Row("AaaTableReadPermission");
                            readtblPermRow.set("PERMISSION_ID", (Object)new Long(permissionId));
                            readtblPermRow.set("TABLE_NAME", (Object)selectColumn.getTableAlias());
                            readtblPermRow.set("COLUMN_NAME", (Object)colName);
                            dobj.addRow(readtblPermRow);
                        }
                    }
                    for (int i = 0; i < joins.size(); ++i) {
                        final Join k = joins.get(i);
                        Criteria impPermCrit = null;
                        for (int impcr = 0; impcr < criteriaList.size(); ++impcr) {
                            final Criteria andCrit2 = criteriaList.get(impcr);
                            if (andCrit2.getColumn().getTableAlias().equalsIgnoreCase(k.getReferencedTableName())) {
                                if (impPermCrit != null) {
                                    impPermCrit.and(andCrit2);
                                }
                                else {
                                    impPermCrit = andCrit2;
                                }
                            }
                        }
                        final Row impRow = new Row("AaaImpliedPermission");
                        impRow.set("PERMISSION_ID", (Object)new Long(permissionId));
                        impRow.set("IMPLIED_TABLE_NAME", (Object)k.getReferencedTableName());
                        impRow.set("IMPLIED_BY_TABLE_NAME", (Object)k.getBaseTableName());
                        if (impPermCrit != null) {
                            impRow.set("CRITERIA", (Object)impPermCrit.toString());
                        }
                        dobj.addRow(impRow);
                        final Row imptcRow2 = new Row("AaaImpliedTableColumn");
                        imptcRow2.set("PERMISSION_ID", (Object)new Long(permissionId));
                        imptcRow2.set("IMPLIED_TABLE_NAME", (Object)k.getReferencedTableName());
                        for (int k2 = 0; k2 < k.getReferencedTableColumns().length; ++k2) {
                            imptcRow2.set("COLUMN_NAME", (Object)k.getReferencedTableColumn(k2));
                            imptcRow2.set("COLUMN_ORDER", (Object)new Long(k2));
                            dobj.addRow(imptcRow2);
                        }
                        final Row impbtcRow = new Row("AaaImpliedByTableColumn");
                        impbtcRow.set("PERMISSION_ID", (Object)new Long(permissionId));
                        impbtcRow.set("IMPLIED_TABLE_NAME", (Object)k.getReferencedTableName());
                        impbtcRow.set("IMPLIED_BY_TABLE_NAME", (Object)k.getBaseTableName());
                        for (int kk2 = 0; kk2 < k.getBaseTableColumns().length; ++kk2) {
                            impbtcRow.set("COLUMN_NAME", (Object)k.getBaseTableColumn(kk2));
                            impbtcRow.set("COLUMN_ORDER", (Object)new Long(kk2));
                            dobj.addRow(impbtcRow);
                        }
                    }
                    returnObj = persistence.add(dobj);
                    AuthorizationUtil.logger.log(Level.FINEST, "updated permission dobj is : {0}", new Object[] { dobj });
                }
            }
            catch (final DataAccessException dae) {
                dae.printStackTrace();
                AuthorizationUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to create permissions for selectquery {0}", dae.getMessage());
            }
            catch (final Exception e) {
                e.printStackTrace();
                AuthorizationUtil.logger.log(Level.SEVERE, "Exception caught in create permissions for selectquery {0}", e.getMessage());
            }
        }
        else {
            AuthorizationUtil.logger.log(Level.SEVERE, "permissions for selectquery not created since selectquery is NULL");
        }
        return returnObj;
    }
    
    public static DataObject createSimplePermission(final String permName, final String tableName, final String type, final String criteria, final String serviceName) {
        DataObject returnObj = null;
        AuthorizationUtil.logger.log(Level.FINEST, "creating simple permission for table : {0} with access {1} ", new Object[] { tableName, type });
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            final long permissionId = getPermissionID(permName);
            if (permissionId == -1L) {
                final Row permRow = new Row("AaaPermission");
                permRow.set("PERMISSION_NAME", (Object)permName);
                permRow.set("SERVICE_ID", (Object)getServiceId(serviceName));
                dobj.addRow(permRow);
                final Row tablepermRow = new Row("AaaTablePermission");
                tablepermRow.set("PERMISSION_ID", permRow.get("PERMISSION_ID"));
                tablepermRow.set("TABLE_NAME", (Object)tableName);
                tablepermRow.set("CRITERIA", (Object)criteria);
                tablepermRow.set("ACCESS_TYPE", (Object)type);
                dobj.addRow(tablepermRow);
                returnObj = persistence.add(dobj);
                AuthorizationUtil.logger.log(Level.FINEST, "simple permission dobj is : {0}", new Object[] { dobj });
            }
            else {
                final DataObject tblpermobj = persistence.get("AaaTablePermission", new Criteria(Column.getColumn("AaaTablePermission", "PERMISSION_ID"), (Object)new Long(permissionId), 0));
                final Row tblPermRow = tblpermobj.getRow("AaaTablePermission");
                tblPermRow.set("TABLE_NAME", (Object)tableName);
                tblPermRow.set("ACCESS_TYPE", (Object)type);
                tblPermRow.set("CRITERIA", (Object)criteria);
                tblpermobj.updateRow(tblPermRow);
                returnObj = persistence.update(tblpermobj);
                AuthorizationUtil.logger.log(Level.FINEST, "updated simple permission dobj is : {0}", new Object[] { dobj });
            }
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while trying to create simple permissions for table {0}", dae.getMessage());
        }
        catch (final Exception e) {
            e.printStackTrace();
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in create simple permission {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject addRole(final String roleName, final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            final Row userRow = new Row("AaaRole");
            userRow.set("NAME", (Object)roleName);
            userRow.set("SERVICE_ID", (Object)getServiceId(serviceName));
            dobj.addRow(userRow);
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while trying to create a role {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in create role {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject addImpliedRole(final String roleName, final String impRoleName, final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            final Row impRow = new Row("AaaImpliedRole");
            impRow.set("ROLE_ID", (Object)getRoleID(roleName, serviceName));
            impRow.set("IMPLIEDROLE_ID", (Object)getRoleID(impRoleName, serviceName));
            dobj.addRow(impRow);
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while trying to create a implied role {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in create implied role {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject addImpliedRoles(final String roleName, final List impRoleList, final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            final Long roleId = getRoleID(roleName, serviceName);
            for (int i = 0; i < impRoleList.size(); ++i) {
                final Row impRow = new Row("AaaImpliedRole");
                impRow.set("ROLE_ID", (Object)roleId);
                impRow.set("IMPLIEDROLE_ID", (Object)getRoleID(impRoleList.get(i), serviceName));
                dobj.addRow(impRow);
            }
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while trying to create a implied role list {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in create implied role list {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static Long getPermissionID(final String pName) throws DataAccessException {
        final Criteria ct = new Criteria(Column.getColumn("AaaPermission", "PERMISSION_NAME"), (Object)pName, 0);
        final DataObject permDO = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get("AaaPermission", ct);
        if (permDO != null && permDO.containsTable("AaaPermission")) {
            final Long permID = (Long)permDO.getRow("AaaPermission").get("PERMISSION_ID");
            return permID;
        }
        return new Long(-1L);
    }
    
    public static Long getRoleID(final String rName, final String sName) throws DataAccessException, Exception {
        final Criteria ct = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)rName, 0);
        final Criteria ct2 = ct.and(new Criteria(Column.getColumn("AaaRole", "SERVICE_ID"), (Object)getServiceId(sName), 0));
        final DataObject roleDO = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get("AaaRole", ct2);
        if (roleDO != null && roleDO.containsTable("AaaRole")) {
            final Long roleID = (Long)roleDO.getRow("AaaRole").get("ROLE_ID");
            return roleID;
        }
        return new Long(-1L);
    }
    
    public static DataObject assignPermissionToRole(final String pName, final String rName, final String sName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            final Row aceRow = new Row("AaaAce");
            aceRow.set("PERMISSION_ID", (Object)getPermissionID(pName));
            aceRow.set("ROLE_ID", (Object)getRoleID(rName, sName));
            dobj.addRow(aceRow);
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while assigning perm. to role {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in assigning perm. to role {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject assignPermissionsToRoles(final List pName, final List rName, final String sName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            for (int i = 0; i < rName.size(); ++i) {
                final Long roleId = getRoleID(rName.get(i), sName);
                for (int j = 0; j < pName.size(); ++j) {
                    final Row aceRow = new Row("AaaAce");
                    aceRow.set("PERMISSION_ID", (Object)getPermissionID(pName.get(j)));
                    aceRow.set("ROLE_ID", (Object)roleId);
                    dobj.addRow(aceRow);
                }
            }
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while assigning permlist. to rolelist {0}", dae.getMessage());
            dae.printStackTrace();
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in assigning permlist. to rolelist {0}", e.getMessage());
            e.printStackTrace();
        }
        return returnObj;
    }
    
    public static DataObject assignRoleToAccount(final String roleName, final String loginName, final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            final Row authRole = new Row("AaaAuthorizedRole");
            authRole.set("ROLE_ID", (Object)getRoleID(roleName, serviceName));
            authRole.set("ACCOUNT_ID", (Object)AuthUtil.getAccountId(loginName, serviceName));
            dobj.addRow(authRole);
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while assigning roles to account {0}", dae.getMessage());
            dae.printStackTrace();
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in assigning roles to account {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject assignRolesToAccounts(final List roleName, final List loginName, final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final DataObject dobj = persistence.constructDataObject();
            for (int i = 0; i < loginName.size(); ++i) {
                final Long accountId = AuthUtil.getAccountId((String)loginName.get(i), serviceName);
                for (int j = 0; j < roleName.size(); ++j) {
                    final Row authRole = new Row("AaaAuthorizedRole");
                    authRole.set("ROLE_ID", (Object)getRoleID(roleName.get(j), serviceName));
                    authRole.set("ACCOUNT_ID", (Object)accountId);
                    dobj.addRow(authRole);
                }
            }
            returnObj = persistence.add(dobj);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while assigning roles to account {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught in assigning roles to account {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject getPermissionsForService(final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final Table tbl = Table.getTable("AaaPermission");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(tbl);
            final Column c = Column.getColumn((String)null, "*");
            sq.addSelectColumn(c);
            sq.addJoin(new Join("AaaPermission", "AaaSimplePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaPermission", "AaaMethodPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaMethodPermission", "AaaMethodParams", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaPermission", "AaaTablePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaTablePermission", "AaaTableUpdatePermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaTablePermission", "AaaTableReadPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaTablePermission", "AaaImpliedPermission", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaImpliedPermission", "AaaImpliedTableColumn", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaImpliedPermission", "AaaImpliedByTableColumn", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaPermission", "AaaAce", new String[] { "PERMISSION_ID" }, new String[] { "PERMISSION_ID" }, 1));
            sq.addJoin(new Join("AaaPermission", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 1));
            if (serviceName != null) {
                sq.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0));
            }
            returnObj = persistence.get(sq);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while getting permissions for service {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught while getting permissions for service {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject getRolesForService(final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final Table tble = Table.getTable("AaaRole");
            final SelectQuery sqy = (SelectQuery)new SelectQueryImpl(tble);
            final Column cln = Column.getColumn((String)null, "*");
            sqy.addSelectColumn(cln);
            sqy.addJoin(new Join("AaaRole", "AaaRoleToCategory", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            sqy.addJoin(new Join("AaaRoleToCategory", "AaaRoleCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 1));
            sqy.addJoin(new Join("AaaRole", "AaaRoleOwner", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            sqy.addJoin(new Join("AaaRole", "AaaImpliedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            sqy.addJoin(new Join("AaaRole", "AaaAuthorizedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            sqy.addJoin(new Join("AaaRole", "AaaTrustedRole", new String[] { "ROLE_ID" }, new String[] { "TRUSTED_ROLE_ID" }, 1));
            sqy.addJoin(new Join("AaaRole", "AaaAce", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            sqy.addJoin(new Join("AaaRole", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 1));
            if (serviceName != null) {
                sqy.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0));
            }
            returnObj = persistence.get(sqy);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while getting roles for service {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught while getting roles for service {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject getAccountsForService(final String serviceName) {
        DataObject returnObj = null;
        try {
            final Persistence persistence = AuthDBUtil.getPersistence("Persistence");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            query.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            query.addJoin(new Join("AaaUser", "AaaUserProfile", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
            query.addJoin(new Join("AaaUserProfile", "AaaGenderHonorific", new String[] { "GH_ID" }, new String[] { "GH_ID" }, 1));
            query.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            query.addJoin(new Join("AaaAccount", "AaaAccountStatus", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
            query.addJoin(new Join("AaaAccount", "AaaAccBadLoginStatus", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
            query.addJoin(new Join("AaaAccount", "AaaAccAdminProfile", new String[] { "ACCOUNTPROFILE_ID" }, new String[] { "ACCOUNTPROFILE_ID" }, 1));
            query.addJoin(new Join("AaaAccount", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
            query.addJoin(new Join("AaaAccount", "AaaAccPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
            query.addJoin(new Join("AaaAccPassword", "AaaPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2));
            query.addJoin(new Join("AaaPassword", "AaaPasswordProfile", new String[] { "PASSWDPROFILE_ID" }, new String[] { "PASSWDPROFILE_ID" }, 1));
            query.addJoin(new Join("AaaPassword", "AaaPasswordRule", new String[] { "PASSWDRULE_ID" }, new String[] { "PASSWDRULE_ID" }, 1));
            query.addJoin(new Join("AaaPassword", "AaaPasswordStatus", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 1));
            query.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
            query.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
            if (serviceName != null) {
                final Criteria criteria = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0);
                query.setCriteria(criteria);
            }
            returnObj = persistence.get(query);
        }
        catch (final DataAccessException dae) {
            AuthorizationUtil.logger.log(Level.FINEST, "DataAccessException caught while getting accounts for service {0}", dae.getMessage());
        }
        catch (final Exception e) {
            AuthorizationUtil.logger.log(Level.FINEST, "Exception caught while getting accounts for service {0}", e.getMessage());
        }
        return returnObj;
    }
    
    public static DataObject getCompleteAuthDetails(final String serviceName) throws DataAccessException {
        final DataObject returnObj = getPermissionsForService(serviceName);
        returnObj.merge(getRolesForService(serviceName));
        returnObj.merge(getAccountsForService(serviceName));
        return returnObj;
    }
    
    public static DataObject getCompleteAuthDetails() throws DataAccessException {
        final String serviceName = null;
        return getCompleteAuthDetails(serviceName);
    }
    
    public static DataObject createPermissionForPersonality(final String personalityName, final String pname, final String ptype, final String serviceName) {
        SelectQuery personalitySQ = null;
        AuthorizationUtil.logger.log(Level.FINEST, "creating permission for personality {0} with access {1} ", new Object[] { personalityName, ptype });
        try {
            final List tblList = PersonalityConfigurationUtil.getConstituentTables(personalityName);
            final Criteria ct = null;
            personalitySQ = QueryConstructor.get(tblList, ct);
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
            AuthorizationUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to create permissions for personality {0}", dae.getMessage());
        }
        return createPermissionForSelectQuery(personalitySQ, pname, ptype, serviceName);
    }
    
    public static DataObject createPermissionForPersonality(final String personalityName, final String pname, final String ptype, final Criteria crit, final String serviceName) {
        SelectQuery personalitySQ = null;
        AuthorizationUtil.logger.log(Level.FINEST, "creating permission for personality {0} with access {1} ", new Object[] { personalityName, ptype });
        try {
            final List tblList = PersonalityConfigurationUtil.getConstituentTables(personalityName);
            final Criteria ct = null;
            personalitySQ = QueryConstructor.get(tblList, ct);
            personalitySQ.setCriteria(crit);
            System.out.println("select query for personality is " + personalitySQ);
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
            AuthorizationUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to create permissions for personality {0}", dae.getMessage());
        }
        return createPermissionForSelectQuery(personalitySQ, pname, ptype, serviceName);
    }
    
    static {
        AuthorizationUtil.logger = Logger.getLogger(AuthorizationUtil.class.getName());
    }
}
