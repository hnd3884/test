package com.adventnet.authentication.util;

import com.adventnet.authentication.PasswordException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.net.URL;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.persistence.Persistence;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.adventnet.authentication.internal.WritableCredential;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.authentication.Credential;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class AuthDBUtil
{
    private static Logger logger;
    
    public static String getServiceNameForContext(final String contextName) {
        String service = null;
        try {
            String context = null;
            if (contextName != null && !contextName.equals("")) {
                final StringTokenizer tkzr = new StringTokenizer(contextName, "/");
                if (tkzr.hasMoreTokens()) {
                    context = tkzr.nextToken();
                }
            }
            else {
                context = "/";
            }
            if (context == null) {
                AuthDBUtil.logger.log(Level.SEVERE, "context name obtained is null, unable to get service name");
                return null;
            }
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ModuleContext"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.addJoin(new Join("ModuleContext", "Module", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            sq.addJoin(new Join("Module", "AaaModuleService", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            sq.addJoin(new Join("AaaModuleService", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("ModuleContext", "CONTEXT"), (Object)context, 0));
            final DataObject dobj = getCachedPersistence("PureCachedPersistence").get(sq);
            service = (String)dobj.getFirstValue("AaaService", "NAME");
        }
        catch (final DataAccessException dae) {
            AuthDBUtil.logger.log(Level.FINER, "DataAccessException occured while trying to fetch service name for context : {0} - {1}", new Object[] { contextName, dae.getMessage() });
        }
        if (service == null) {
            AuthDBUtil.logger.log(Level.FINE, "service name obtained is null for context : {0}, using service System", contextName);
            service = "System";
        }
        return service;
    }
    
    public static void associateSsoSession(final String ssoId, final Credential credential) {
        if (credential == null || ssoId == null) {
            AuthDBUtil.logger.log(Level.SEVERE, "addUserCredential invoked with credential/ssoId as null, unable to map the ssoId to sessionid");
            return;
        }
        AuthUtil.setUserCredential(credential);
        final Long sessionId = credential.getSessionId();
        try {
            final DataObject dobj = DataAccess.constructDataObject();
            if (ssoId != null && sessionId != -1L && sessionId != 0L) {
                final DataObject existingDobj = DataAccess.get("AaaAccHttpSession", new Criteria(Column.getColumn("AaaAccHttpSession", "SESSION_ID"), (Object)sessionId, 0));
                if (existingDobj != null && !existingDobj.isEmpty()) {
                    AuthDBUtil.logger.log(Level.FINEST, "Existing dataObject :: " + existingDobj);
                    final Row accHttpRow = existingDobj.getRow("AaaAccHttpSession");
                    accHttpRow.set("SSO_ID", (Object)ssoId);
                    existingDobj.updateRow(accHttpRow);
                    final DataObject updated = DataAccess.update(existingDobj);
                    AuthDBUtil.logger.log(Level.FINEST, "Updated dataObject :: " + existingDobj);
                }
                else {
                    final Row r = new Row("AaaAccHttpSession");
                    r.set("SESSION_ID", (Object)sessionId);
                    r.set("SSO_ID", (Object)ssoId);
                    dobj.addRow(r);
                    AuthDBUtil.logger.log(Level.FINEST, "DataObject to add : {0}", dobj);
                    DataAccess.add(dobj);
                }
            }
            else {
                AuthDBUtil.logger.log(Level.WARNING, "unable to map ssoid : {0} to sessionid : {1}", new Object[] { ssoId, sessionId });
            }
        }
        catch (final DataAccessException dae) {
            AuthDBUtil.logger.log(Level.SEVERE, "DataAccessException occured while mapping ssoid : {0} to sessionid : {1} - {2}", new Object[] { ssoId, sessionId, dae });
        }
    }
    
    public static Credential constructCredential(final String loginName, final String serviceName) {
        return constructCredential(loginName, serviceName, (String)null);
    }
    
    public static Credential constructCredential(final String loginName, final String serviceName, final String domainName) {
        Credential cr = null;
        try {
            final DataObject dobj = getAccountDO(loginName, serviceName, domainName);
            final WritableCredential wcr = constructCredential(dobj);
            cr = AuthUtil.transform(wcr);
        }
        catch (final DataAccessException dae) {
            final String msg = "DataAcccessException occured while constructing credential for " + loginName + " serviceName " + serviceName;
            AuthDBUtil.logger.log(Level.WARNING, msg);
            AuthDBUtil.logger.log(Level.FINE, msg + "{0}", (Throwable)dae);
        }
        return cr;
    }
    
    public static WritableCredential constructCredential(final DataObject accountDO) {
        final WritableCredential wcr = new WritableCredential();
        try {
            final Long userId = (Long)accountDO.getFirstValue("AaaLogin", "USER_ID");
            wcr.setUserId(userId);
            final Long loginId = (Long)accountDO.getFirstValue("AaaLogin", "LOGIN_ID");
            wcr.setLoginId(loginId);
            final Long accId = (Long)accountDO.getFirstValue("AaaAccount", "ACCOUNT_ID");
            wcr.setAccountId(accId);
            final String loginName = (String)accountDO.getFirstValue("AaaLogin", "NAME");
            wcr.setLoginName(loginName);
            final String serviceName = (String)accountDO.getFirstValue("AaaService", "NAME");
            wcr.setServiceName(serviceName);
            final String domainName = (String)accountDO.getFirstValue("AaaLogin", 4);
            wcr.setDomainName(domainName);
            final List roles = getAuthorizedRoles(accountDO);
            wcr.addRoles(roles);
            if (accountDO.containsTable("AaaUserProfile")) {
                final Row userProfileRow = accountDO.getFirstRow("AaaUserProfile");
                final String timezone = (String)userProfileRow.get("TIMEZONE");
                wcr.setTimeZone(timezone);
                final String countryCode = (String)userProfileRow.get("COUNTRY_CODE");
                wcr.setCountryCode(countryCode);
                final String langCode = (String)userProfileRow.get("LANGUAGE_CODE");
                wcr.setLangCode(langCode);
            }
        }
        catch (final DataAccessException de) {
            AuthDBUtil.logger.log(Level.SEVERE, "Exception occured while constructing credential object for ssoId : {0}", de.getMessage());
            AuthDBUtil.logger.log(Level.FINEST, "DataAccessException occured while constructing credential for ssoId : ", (Throwable)de);
        }
        return wcr;
    }
    
    public static Credential constructCredential(final String contextname, final String ssoId, final HttpServletRequest request, final List dynamicRoles) {
        Credential cr = null;
        try {
            final String service = getServiceNameForContext(contextname);
            final DataObject accountDO = getAccountDOForSSOID(ssoId, service);
            final WritableCredential wcr = constructCredential(accountDO);
            if (dynamicRoles != null && !dynamicRoles.isEmpty()) {
                wcr.addRoles(dynamicRoles);
            }
            final Long sessionId = (Long)accountDO.getFirstValue("AaaAccSession", "SESSION_ID");
            wcr.setSessionId(sessionId);
            final String hostName = request.getRemoteHost();
            wcr.setHostName(hostName);
            cr = AuthUtil.transform(wcr);
        }
        catch (final DataAccessException de) {
            AuthDBUtil.logger.log(Level.SEVERE, "Exception occured while constructing credential object for ssoId : {0}", de.getMessage());
            AuthDBUtil.logger.log(Level.FINEST, "DataAccessException occured while constructing credential for ssoId : {0}", (Throwable)de);
        }
        return cr;
    }
    
    public static Credential constructCredential(final String contextname, final String ssoId, final HttpServletRequest request) {
        return constructCredential(contextname, ssoId, request, null);
    }
    
    public static List getAuthorizedRoles(final DataObject accountDO) throws DataAccessException {
        final List roleList = getAccountAuthorizedRoles(accountDO);
        final List impliedRoles = getImpliedRoles(roleList);
        roleList.addAll(impliedRoles);
        return roleList;
    }
    
    private static List getAccountAuthorizedRoles(final DataObject accountDO) throws DataAccessException {
        final List roleList = new ArrayList();
        try {
            if (accountDO.containsTable("AaaRole")) {
                final Iterator it = accountDO.getRows("AaaRole");
                Row roleRow = null;
                while (it.hasNext()) {
                    roleRow = it.next();
                    roleList.add(roleRow.get("NAME"));
                }
                AuthDBUtil.logger.log(Level.FINEST, "authorized roles : {0}", roleList);
            }
            else {
                AuthDBUtil.logger.log(Level.FINEST, "account dataobject does not contain role table");
            }
        }
        catch (final DataAccessException e) {
            AuthDBUtil.logger.log(Level.WARNING, "unable to fetch roles from the account dataobject : ", (Throwable)e);
        }
        return roleList;
    }
    
    private static List getImpliedRoles(final List roleList) throws DataAccessException {
        final List impRoleList = new ArrayList();
        if (roleList == null || roleList.size() == 0) {
            return impRoleList;
        }
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole", "impByRoleAlias"));
        sq.addSelectColumn(Column.getColumn("impByRoleAlias", "*"));
        sq.addSelectColumn(Column.getColumn("impRoleAlias", "*"));
        sq.addJoin(new Join("AaaRole", "AaaImpliedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, "impByRoleAlias", "aaaimpliedrole", 2));
        sq.addJoin(new Join("AaaImpliedRole", "AaaRole", new String[] { "IMPLIEDROLE_ID" }, new String[] { "ROLE_ID" }, "aaaimpliedrole", "impRoleAlias", 2));
        sq.setCriteria(new Criteria(Column.getColumn("impByRoleAlias", "NAME"), (Object)roleList.toArray(), 8));
        DataObject dobj = null;
        try {
            dobj = getCachedPersistence("PureCachedPersistence").get(sq);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        AuthDBUtil.logger.log(Level.FINEST, "dataobject obtained for implied roles is : {0}", dobj);
        if (!dobj.isEmpty()) {
            final Iterator itr = dobj.getRows("impRoleAlias");
            Row temp = null;
            while (itr.hasNext()) {
                temp = itr.next();
                impRoleList.add(temp.get("NAME"));
            }
        }
        return impRoleList;
    }
    
    public static DataObject getAccountDOForSSOID(final String ssoId, final String serviceName) throws DataAccessException {
        AuthDBUtil.logger.log(Level.FINEST, "fetching account dataobject for ssoid : {0} and service : {1}", new Object[] { ssoId, serviceName });
        DataObject accountDobj = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        query.addJoin(new Join("AaaLogin", "AaaUserProfile", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        query.addJoin(new Join("AaaAccount", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        query.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
        query.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
        query.addJoin(new Join("AaaAccount", "AaaAccSession", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        query.addJoin(new Join("AaaAccSession", "AaaAccHttpSession", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0);
        criteria = criteria.or(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)"System", 0));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaAccHttpSession", "SSO_ID"), (Object)ssoId, 0));
        query.setCriteria(criteria);
        AuthDBUtil.logger.log(Level.FINEST, "Criteri string = {0}", criteria);
        try {
            accountDobj = getCachedPersistence("PureCachedPersistence").get(query);
            AuthDBUtil.logger.log(Level.FINEST, "account data object fetched for ssoid : {0} and service : {1} is : {2}", new Object[] { ssoId, serviceName, accountDobj });
        }
        catch (final Exception e) {
            throw new DataAccessException("Exception occured while fetching account dataobject for ssoid ", (Throwable)e);
        }
        return accountDobj;
    }
    
    public static List getSessionIds(final String ssoId) {
        final List sessionIdList = new ArrayList();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccHttpSession", "SSO_ID"), (Object)ssoId, 0);
            final DataObject dobj = DataAccess.get("AaaAccHttpSession", criteria);
            final Iterator itr = dobj.getRows("AaaAccHttpSession");
            Row row = null;
            while (itr.hasNext()) {
                row = itr.next();
                sessionIdList.add(row.get("SESSION_ID"));
            }
        }
        catch (final DataAccessException dae) {
            AuthDBUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to get sessionIds for ssoId : ", (Throwable)dae);
        }
        return sessionIdList;
    }
    
    public static DataObject getAccountDO(final String loginName, final String serviceName) throws DataAccessException {
        return getAccountDO(loginName, serviceName, null);
    }
    
    public static DataObject getAccountDO(final String loginName, final String serviceName, String domainName) throws DataAccessException {
        AuthDBUtil.logger.log(Level.FINEST, "fetching account dataobject for loginname : {0}, service : {1}", new Object[] { "*****", serviceName });
        DataObject accountDobj = null;
        try {
            domainName = (String)((domainName == null || domainName.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domainName);
        }
        catch (final MetaDataException e1) {
            AuthDBUtil.logger.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        query.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        query.addJoin(new Join("AaaUser", "AaaUserProfile", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        query.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        query.addJoin(new Join("AaaAccount", "AaaAccountStatus", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
        query.addJoin(new Join("AaaAccount", "AaaAccBadLoginStatus", new Criteria(Column.getColumn("AaaAccount", "ACCOUNT_ID"), (Object)Column.getColumn("AaaAccBadLoginStatus", "ACCOUNT_ID"), 0).and(new Criteria(Column.getColumn("AaaAccBadLoginStatus", "NUMOF_BADLOGIN"), (Object)new Integer(0), 1)), 1));
        query.addJoin(new Join("AaaAccount", "AaaAccAdminProfile", new String[] { "ACCOUNTPROFILE_ID" }, new String[] { "ACCOUNTPROFILE_ID" }, 1));
        query.addJoin(new Join("AaaAccount", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        query.addJoin(new Join("AaaAccount", "AaaAccPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
        query.addJoin(new Join("AaaAccPassword", "AaaPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2));
        query.addJoin(new Join("AaaPassword", "AaaPasswordProfile", new String[] { "PASSWDPROFILE_ID" }, new String[] { "PASSWDPROFILE_ID" }, 1));
        query.addJoin(new Join("AaaPassword", "AaaPasswordStatus", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 1));
        query.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
        query.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
        Criteria criteria = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0, false));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
        query.setCriteria(criteria);
        try {
            accountDobj = DataAccess.get(query);
            if (accountDobj.getRow("AaaAccount") == null) {
                Criteria criteriaForSys = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)"System", 0);
                criteriaForSys = criteriaForSys.and(new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0, false));
                criteriaForSys = criteriaForSys.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
                final SelectQuery clonedQuery = (SelectQuery)query.clone();
                clonedQuery.setCriteria(criteriaForSys);
                accountDobj = DataAccess.get(clonedQuery);
            }
        }
        catch (final Exception e2) {
            AuthDBUtil.logger.log(Level.FINEST, "Exception occured while fetching account dataobject", e2);
            throw new DataAccessException("Exception occured while fetching account dataobject", (Throwable)e2);
        }
        return accountDobj;
    }
    
    public static DataObject getAccountPasswordDO(final String loginName, final String serviceName) throws DataAccessException {
        return getAccountPasswordDO(loginName, serviceName, null);
    }
    
    public static DataObject getAccountPasswordDO(final String loginName, final String serviceName, String domainName) throws DataAccessException {
        AuthDBUtil.logger.log(Level.FINEST, "fetching account password dataobject for loginname : {0}, service : {1}", new Object[] { "*****", serviceName });
        DataObject accPassDO = null;
        try {
            domainName = (String)((domainName == null || domainName.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domainName);
        }
        catch (final MetaDataException e1) {
            AuthDBUtil.logger.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        query.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        query.addJoin(new Join("AaaAccount", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        query.addJoin(new Join("AaaAccount", "AaaAccPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        query.addJoin(new Join("AaaAccPassword", "AaaPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, "AaaAccPassword", "currentPass", 2));
        query.addJoin(new Join("AaaPassword", "AaaPasswordProfile", new String[] { "PASSWDPROFILE_ID" }, new String[] { "PASSWDPROFILE_ID" }, "currentPass", "AaaPasswordProfile", 2));
        query.addJoin(new Join("AaaPassword", "AaaPasswordStatus", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, "currentPass", "currentPassStatus", 2));
        query.addJoin(new Join("AaaPassword", "AaaPasswordRule", new String[] { "PASSWDRULE_ID" }, new String[] { "PASSWDRULE_ID" }, "currentPass", "AaaPasswordRule", 2));
        query.addJoin(new Join("AaaAccount", "AaaAccOldPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
        query.addJoin(new Join("AaaAccOldPassword", "AaaPassword", new String[] { "OLDPASSWORD_ID" }, new String[] { "PASSWORD_ID" }, "AaaAccOldPassword", "oldPass", 1));
        query.addJoin(new Join("AaaPassword", "AaaPasswordStatus", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, "oldPass", "oldPassStatus", 1));
        query.addSelectColumn(Column.getColumn("currentPass", "*"));
        query.addSelectColumn(Column.getColumn("currentPassStatus", "*"));
        query.addSelectColumn(Column.getColumn("AaaAccPassword", "*"));
        query.addSelectColumn(Column.getColumn("AaaAccOldPassword", "*"));
        query.addSelectColumn(Column.getColumn("AaaPasswordProfile", "*"));
        query.addSelectColumn(Column.getColumn("AaaPasswordRule", "*"));
        query.addSelectColumn(Column.getColumn("oldPass", "*"));
        query.addSelectColumn(Column.getColumn("oldPassStatus", "*"));
        Criteria criteria = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0, false));
        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
        query.setCriteria(criteria);
        try {
            accPassDO = DataAccess.get(query);
            if (!accPassDO.containsTable("currentPass")) {
                Criteria criteriaForSys = new Criteria(Column.getColumn("AaaService", "NAME"), (Object)"System", 0);
                criteriaForSys = criteriaForSys.and(new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0, false));
                criteriaForSys = criteriaForSys.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
                final SelectQuery clonedQuery = (SelectQuery)query.clone();
                clonedQuery.setCriteria(criteriaForSys);
                accPassDO = DataAccess.get(clonedQuery);
            }
        }
        catch (final Exception e2) {
            throw new DataAccessException("Exception occured when fetching account password dataobject", (Throwable)e2);
        }
        return accPassDO;
    }
    
    public static Object getBeanInstance(final String beanName) {
        Object instance = null;
        try {
            instance = BeanUtil.lookup(beanName);
        }
        catch (final Exception e) {
            AuthDBUtil.logger.log(Level.SEVERE, "Exception caught while looking up Bean : {0}  : {1}", new Object[] { beanName, e });
        }
        return instance;
    }
    
    public static Persistence getPersistence(final String beanName) {
        return (Persistence)getBeanInstance(beanName);
    }
    
    public static ReadOnlyPersistence getCachedPersistence(final String beanName) {
        return (ReadOnlyPersistence)getBeanInstance(beanName);
    }
    
    public static Object getObject(final String tablename, final String getColName, final String gvnColName, final Object value) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn(tablename, gvnColName), value, 0);
        DataObject dobj = null;
        try {
            dobj = getCachedPersistence("PureCachedPersistence").get(tablename, criteria);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        return dobj.getFirstValue(tablename, getColName);
    }
    
    public static Row getRowMatching(final String tablename, final String colName, final Object value) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn(tablename, colName), value, 0);
        DataObject dobj = null;
        try {
            dobj = getCachedPersistence("PureCachedPersistence").get(tablename, criteria);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        return dobj.getFirstRow(tablename);
    }
    
    public static DataObject updateAuthConf(DataObject authConfDO) throws Exception {
        try {
            AuthDBUtil.logger.log(Level.INFO, "account conf DO obtained is : {0}", authConfDO);
            if (authConfDO.getRow("AaaUser") != null && authConfDO.getRow("AaaAccount") != null) {
                authConfDO = fillUserStatus(authConfDO);
                authConfDO = fillTimeStamp(authConfDO);
                authConfDO = fillAccountStatus(authConfDO, true);
                authConfDO = fillPasswordStatus(authConfDO, true);
            }
            AuthDBUtil.logger.log(Level.INFO, "authconf DO to be populated is : {0}", authConfDO);
            return DataAccess.update(authConfDO);
        }
        catch (final DataAccessException dae) {
            AuthDBUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to populate authconf : {0}", authConfDO);
            AuthDBUtil.logger.log(Level.SEVERE, "Exception  : {0}", (Throwable)dae);
            throw new RuntimeException("DataAccessException occured while populating authconf dataobject", (Throwable)dae);
        }
    }
    
    public static DataObject populateAuthConf(final URL url, final Row confToModuleRow) throws Exception {
        try {
            DataObject authConfDO = Xml2DoConverter.transform(url);
            AuthDBUtil.logger.log(Level.FINEST, "account conf DO obtained from xml is : {0}", authConfDO);
            authConfDO = fillUserStatus(authConfDO);
            authConfDO = fillTimeStamp(authConfDO);
            authConfDO = fillAccountStatus(authConfDO);
            authConfDO = fillPasswordStatus(authConfDO);
            if (confToModuleRow != null) {
                final Row row = authConfDO.getRow("ConfFile");
                if (row != null) {
                    final Object fileID = row.get("FILEID");
                    confToModuleRow.set(1, fileID);
                    authConfDO.addRow(confToModuleRow);
                }
            }
            AuthDBUtil.logger.log(Level.FINEST, "authconf DO to be populated is : {0}", authConfDO);
            return DataAccess.add(authConfDO);
        }
        catch (final DataAccessException dae) {
            AuthDBUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to populate authconf url : {0}", url);
            AuthDBUtil.logger.log(Level.SEVERE, "Exception  : {0}", (Throwable)dae);
            throw new RuntimeException("DataAccessException occured while populating authconf dataobject", (Throwable)dae);
        }
    }
    
    public static DataObject populateAuthConf(DataObject authConfDO) throws Exception {
        try {
            AuthDBUtil.logger.log(Level.FINEST, "account conf DO obtained is : {0}", authConfDO);
            authConfDO = fillUserStatus(authConfDO);
            authConfDO = fillTimeStamp(authConfDO);
            authConfDO = fillAccountStatus(authConfDO);
            authConfDO = fillPasswordStatus(authConfDO);
            AuthDBUtil.logger.log(Level.FINEST, "authconf DO to be populated is : {0}", authConfDO);
            return DataAccess.add(authConfDO);
        }
        catch (final DataAccessException dae) {
            AuthDBUtil.logger.log(Level.SEVERE, "DataAccessException caught while trying to populate authconf : {0}", authConfDO);
            AuthDBUtil.logger.log(Level.SEVERE, "Exception  : {0}", (Throwable)dae);
            throw new RuntimeException("DataAccessException occured while populating authconf dataobject", (Throwable)dae);
        }
    }
    
    private static DataObject fillTimeStamp(final DataObject dobj) throws Exception {
        AuthDBUtil.logger.log(Level.FINEST, "fileTimeStamp called with DO : {0}", dobj);
        Row temp = null;
        final Long now = new Long(System.currentTimeMillis());
        final Iterator orgIterator = dobj.getRows("AaaOrganization");
        if (orgIterator != null) {
            while (orgIterator.hasNext()) {
                temp = orgIterator.next();
                temp.set("CREATEDTIME", (Object)now);
                dobj.updateRow(temp);
            }
        }
        final Iterator userIterator = dobj.getRows("AaaUser");
        if (userIterator != null) {
            while (userIterator.hasNext()) {
                temp = userIterator.next();
                temp.set("CREATEDTIME", (Object)now);
                dobj.updateRow(temp);
            }
        }
        final Iterator accountIterator = dobj.getRows("AaaAccount");
        if (accountIterator != null) {
            while (accountIterator.hasNext()) {
                temp = accountIterator.next();
                temp.set("CREATEDTIME", (Object)now);
                dobj.updateRow(temp);
            }
        }
        final Iterator passwdIterator = dobj.getRows("AaaPassword");
        if (passwdIterator != null) {
            while (passwdIterator.hasNext()) {
                temp = passwdIterator.next();
                temp.set("CREATEDTIME", (Object)now);
                dobj.updateRow(temp);
            }
        }
        AuthDBUtil.logger.log(Level.FINEST, "updated the auth-conf data object with timestamp : {0}", dobj);
        return dobj;
    }
    
    private static DataObject fillUserStatus(final DataObject dobj) throws Exception {
        final Iterator userItr = dobj.getRows("AaaUser");
        while (userItr.hasNext()) {
            final Row userRow = userItr.next();
            final Row userStatusRow = new Row("AaaUserStatus");
            userStatusRow.set("USER_ID", userRow.get("USER_ID"));
            userStatusRow.set("STATUS", (Object)"ACTIVE");
            userStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
            final Row xmlRow = dobj.getRow("AaaUserStatus", userStatusRow);
            if (xmlRow == null) {
                dobj.addRow(userStatusRow);
            }
            else {
                final int[] indices = xmlRow.getChangedColumnIndex();
                for (int index = 0; index < indices.length; ++index) {
                    userStatusRow.set(indices[index], xmlRow.get(indices[index]));
                }
                dobj.updateRow(userStatusRow);
            }
        }
        AuthDBUtil.logger.log(Level.FINEST, "dataobject after filling user status : {0}", dobj);
        return dobj;
    }
    
    private static DataObject fillAccountStatus(final DataObject dobj) throws Exception {
        return fillAccountStatus(dobj, false);
    }
    
    private static DataObject fillAccountStatus(final DataObject dobj, final boolean fetchFromDB) throws Exception {
        final Iterator accounts = dobj.getRows("AaaAccount");
        Row profileRow = null;
        Row accRow = null;
        long createtime = 0L;
        int validityPeriod = 0;
        long expat = 0L;
        final long multiplyF = 86400000L;
        AuthDBUtil.logger.log(Level.FINEST, " muliply factor = {0}", new Long(multiplyF));
        while (accounts.hasNext()) {
            accRow = accounts.next();
            createtime = (long)accRow.get("CREATEDTIME");
            AuthDBUtil.logger.log(Level.FINEST, "creatime time : {0}", new Long(createtime));
            final Row accAdminProfileRow = new Row("AaaAccAdminProfile");
            accAdminProfileRow.set("ACCOUNTPROFILE_ID", accRow.get("ACCOUNTPROFILE_ID"));
            profileRow = dobj.getRow("AaaAccAdminProfile", accAdminProfileRow);
            if (profileRow == null) {
                if (fetchFromDB) {
                    final Long accAdminProfileId = (Long)accRow.get(4);
                    final DataObject accAdminProfileDO = DataAccess.get("AaaAccAdminProfile", new Criteria(new Column("AaaAccAdminProfile", "ACCOUNTPROFILE_ID"), (Object)accAdminProfileId, 0));
                    profileRow = accAdminProfileDO.getRow("AaaAccAdminProfile");
                }
                if (profileRow == null) {
                    throw new Exception("AaaAccAdminProfile row not found in the dataobject" + dobj);
                }
            }
            validityPeriod = (int)profileRow.get("EXP_AFTER");
            AuthDBUtil.logger.log(Level.FINEST, "validity period = {0}", new Integer(validityPeriod));
            if (validityPeriod == -1) {
                expat = -1L;
            }
            else {
                expat = createtime + validityPeriod * multiplyF;
            }
            final Row accStatusRow = new Row("AaaAccountStatus");
            accStatusRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accStatusRow.set("EXPIREAT", (Object)new Long(expat));
            accStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
            accStatusRow.set("STATUS", (Object)"NEW");
            dobj.addRow(accStatusRow);
        }
        AuthDBUtil.logger.log(Level.FINEST, "account dataobject after filling AaaAccountStatus is : {0}", dobj);
        return dobj;
    }
    
    private static DataObject fillPasswordStatus(final DataObject dobj) throws Exception {
        return fillPasswordStatus(dobj, false);
    }
    
    private static DataObject fillPasswordStatus(final DataObject dobj, final boolean fetchFromDB) throws Exception {
        final Iterator passwords = dobj.getRows("AaaPassword");
        Row profileRow = null;
        Row passRow = null;
        long createtime = 0L;
        int validityPeriod = 0;
        long expat = 0L;
        final long multiplyF = 86400000L;
        while (passwords.hasNext()) {
            passRow = passwords.next();
            createtime = (long)passRow.get("CREATEDTIME");
            AuthDBUtil.logger.log(Level.FINEST, "creatime time of password : {0}", new Long(createtime));
            final Row passProfileRow_pk = new Row("AaaPasswordProfile");
            passProfileRow_pk.set("PASSWDPROFILE_ID", passRow.get("PASSWDPROFILE_ID"));
            profileRow = dobj.getRow("AaaPasswordProfile", passProfileRow_pk);
            if (profileRow == null) {
                if (fetchFromDB) {
                    final DataObject passwordProfileDO = DataAccess.get("AaaPasswordProfile", new Criteria(new Column("AaaPasswordProfile", "PASSWDPROFILE_ID"), passRow.get(5), 0));
                    profileRow = passwordProfileDO.getRow("AaaPasswordProfile");
                }
                if (profileRow == null) {
                    throw new Exception("No row found for AaaPasswordProfile in the dataobject " + dobj);
                }
            }
            validityPeriod = (int)profileRow.get("EXP_AFTER");
            AuthDBUtil.logger.log(Level.FINEST, "validity period of password = {0}", new Integer(validityPeriod));
            if (validityPeriod == -1) {
                expat = -1L;
            }
            else {
                expat = createtime + validityPeriod * multiplyF;
            }
            final Row passStatusRow = new Row("AaaPasswordStatus");
            passStatusRow.set("PASSWORD_ID", passRow.get("PASSWORD_ID"));
            passStatusRow.set("EXPIREAT", (Object)new Long(expat));
            passStatusRow.set("STATUS", (Object)"NEW");
            passStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
            dobj.addRow(passStatusRow);
        }
        AuthDBUtil.logger.log(Level.FINEST, "account dataobject after filling AaaPasswordStatus is : {0}", dobj);
        return dobj;
    }
    
    public static List getAccountList() {
        final List accountList = new ArrayList();
        try {
            final Criteria crit = null;
            final DataObject dobj = getCachedPersistence("PureCachedPersistence").get("AaaAccount", crit);
            if (!dobj.isEmpty()) {
                final Iterator itr = dobj.getRows("AaaAccount");
                Row temp = null;
                while (itr.hasNext()) {
                    temp = itr.next();
                    accountList.add(temp.get("ACCOUNT_ID"));
                }
            }
        }
        catch (final DataAccessException e) {
            AuthDBUtil.logger.log(Level.WARNING, "unable to fetch the account for service : ", (Throwable)e);
        }
        return accountList;
    }
    
    public static List getAuthorizedRolesList(final Long accountId) throws DataAccessException {
        final List roleList = getAuthRolesList(accountId);
        final List impliedRoles = getImpliedRoles(roleList);
        roleList.addAll(impliedRoles);
        return roleList;
    }
    
    private static List getAuthRolesList(final Long accountId) throws DataAccessException {
        final List roleList = new ArrayList();
        try {
            final DataObject dobj = getCachedPersistence("PureCachedPersistence").get("AaaAuthorizedRole", new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)accountId, 0));
            if (!dobj.isEmpty()) {
                final Iterator itr = dobj.getRows("AaaAuthorizedRole");
                Row temp = null;
                while (itr.hasNext()) {
                    temp = itr.next();
                    final Long roleId = (Long)temp.get("ROLE_ID");
                    final String roleName = (String)getObject("AaaRole", "NAME", "ROLE_ID", roleId);
                    roleList.add(roleName);
                }
                AuthDBUtil.logger.log(Level.FINEST, "authorized roles : {0}", roleList);
            }
            else {
                AuthDBUtil.logger.log(Level.FINEST, "accountid does not have a corresponding role");
            }
        }
        catch (final DataAccessException e) {
            AuthDBUtil.logger.log(Level.WARNING, "unable to fetch roles from the account dataobject : ", (Throwable)e);
        }
        return roleList;
    }
    
    public static void addAccountDO(final String loginName, String domain, final String defaultRole) throws DataAccessException, PasswordException {
        try {
            domain = (String)((domain == null || domain.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domain);
        }
        catch (final MetaDataException e1) {
            AuthDBUtil.logger.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        final WritableDataObject dobj = new WritableDataObject();
        final Row userRow = new Row("AaaUser");
        userRow.set(2, (Object)loginName);
        dobj.addRow(userRow);
        final Row loginRow = new Row("AaaLogin");
        loginRow.set(3, (Object)loginName);
        loginRow.set(2, userRow.get(1));
        loginRow.set("DOMAINNAME", (Object)domain);
        dobj.addRow(loginRow);
        final Row accRow = new Row("AaaAccount");
        accRow.set(3, (Object)AuthUtil.getServiceId("System"));
        accRow.set(4, (Object)AuthUtil.getAccountProfileId("Profile 1"));
        accRow.set(2, loginRow.get("LOGIN_ID"));
        final Row passwordRow = new Row("AaaPassword");
        passwordRow.set("PASSWORD", (Object)"NOPASSWD");
        passwordRow.set("PASSWDPROFILE_ID", (Object)AuthUtil.getPasswordProfileId("Profile 1"));
        dobj.addRow(passwordRow);
        final Row accPassRow = new Row("AaaAccPassword");
        accPassRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
        accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
        dobj.addRow(accPassRow);
        if (defaultRole != null) {
            final Row accAuthRow = new Row("AaaAuthorizedRole");
            accAuthRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accAuthRow.set("ROLE_ID", (Object)AuthUtil.getRoleId(defaultRole));
            dobj.addRow(accAuthRow);
        }
        dobj.addRow(accRow);
        final int noOfSubAccounts = -1;
        final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
        accOwnerProfileRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
        accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
        dobj.addRow(accOwnerProfileRow);
        AuthUtil.createUserAccount((DataObject)dobj);
    }
    
    static {
        AuthDBUtil.logger = Logger.getLogger(AuthDBUtil.class.getName());
    }
}
