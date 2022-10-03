package com.me.ems.onpremise.uac.core;

import java.util.Hashtable;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.lm.Authenticator;
import com.me.ems.framework.uac.api.v1.service.CoreUserService;
import com.zoho.authentication.lm.LDAPAuthenticator;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.onpremise.server.authentication.LocalAuthenticatorAPI;
import com.me.devicemanagement.framework.server.authentication.CredentialAPI;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.Properties;
import java.util.StringTokenizer;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.ArrayList;
import com.me.ems.framework.uac.ad_handler.ADAccessProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.HashMap;
import java.util.logging.Logger;

public class UserManagementUtil
{
    static Logger logger;
    
    public static HashMap<String, Object> checkIfUserIsValid(final String userName, final String domainName) {
        final HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("isValidUser", Boolean.FALSE);
        try {
            final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
            boolean result;
            if (productCode != null && productCode.equals("DCMSP")) {
                result = ADAccessProvider.getInstance().isValidADObjectName(domainName, userName, 2);
            }
            else {
                result = com.me.devicemanagement.onpremise.winaccess.ADAccessProvider.getInstance().isValidADObjectName(domainName, userName, 2);
            }
            if (result) {
                result = false;
                final List lisAttr = new ArrayList();
                lisAttr.add("userWorkstations");
                Properties prop;
                if (productCode != null && productCode.equals("DCMSP")) {
                    prop = ADAccessProvider.getInstance().getThisADObjectByName(domainName, userName, 2, lisAttr);
                }
                else {
                    prop = com.me.devicemanagement.onpremise.winaccess.ADAccessProvider.getInstance().getThisADObjectByName(domainName, userName, 2, lisAttr);
                }
                final String computers = prop.getProperty("userWorkstations");
                if (computers.compareTo("-") != 0) {
                    final String server = SyMUtil.getServerName();
                    final StringTokenizer st = new StringTokenizer(computers, ",");
                    while (st.hasMoreTokens()) {
                        final String token = st.nextToken();
                        if (token.equalsIgnoreCase(server)) {
                            result = true;
                            break;
                        }
                    }
                }
                else {
                    result = true;
                }
                if (result) {
                    List availableList;
                    if (productCode != null && productCode.equals("DCMSP")) {
                        availableList = ADAccessProvider.getInstance().getUserDetails(domainName, userName);
                    }
                    else {
                        availableList = com.me.devicemanagement.onpremise.winaccess.ADAccessProvider.getInstance().getUserDetails(domainName, userName);
                    }
                    if (availableList != null) {
                        for (final Properties idAndName : availableList) {
                            final String name = idAndName.getProperty("sAMAccountName");
                            if (name.equalsIgnoreCase(userName)) {
                                resultMap.put("email", ((Hashtable<K, Object>)idAndName).get("mail"));
                                break;
                            }
                        }
                    }
                }
                resultMap.put("isValidUser", result);
            }
        }
        catch (final Exception exp) {
            UserManagementUtil.logger.log(Level.WARNING, "Exception while validating new domain details...", exp);
        }
        return resultMap;
    }
    
    public void defaultPasswordChanged(final Long userID, final boolean isPasswordDefault) {
        try {
            final Boolean isPasswordChangeRequired = isPasswordDefault;
            SyMUtil.updateUserParameter(userID, "isDefaultPasswordChangeRequired", isPasswordChangeRequired.toString());
            Map<Long, Boolean> isRequiredCache = (Map<Long, Boolean>)ApiFactoryProvider.getCacheAccessAPI().getCache("isDefaultPasswordChangeRequired");
            if (isRequiredCache == null) {
                isRequiredCache = new HashMap<Long, Boolean>();
            }
            isRequiredCache.put(DMUserHandler.getLoginIdForUserId(userID), isPasswordChangeRequired);
            ApiFactoryProvider.getCacheAccessAPI().putCache("isDefaultPasswordChangeRequired", (Object)isRequiredCache, 2);
        }
        catch (final SyMException sye) {
            UserManagementUtil.logger.log(Level.SEVERE, "defaultPasswordChanged: Exception while updating userParams", (Throwable)sye);
        }
    }
    
    public boolean isDefaultPasswordUsed(final long loginId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaPassword"));
            selectQuery.addSelectColumn(new Column("AaaPassword", "*"));
            selectQuery.addJoin(new Join("AaaPassword", "AaaAccPassword", new String[] { "PASSWORD_ID" }, new String[] { "ACCOUNT_ID" }, 1));
            selectQuery.addJoin(new Join("AaaAccPassword", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)loginId, 0);
            selectQuery.setCriteria(criteria);
            final Row passwordRow = SyMUtil.getPersistence().get(selectQuery).getFirstRow("AaaPassword");
            final String defaultPassword = "admin";
            final String salt = (String)passwordRow.get("SALT");
            final String algorithm = (String)passwordRow.get("ALGORITHM");
            final String password = (String)passwordRow.get("PASSWORD");
            final String encryptedDefaultPassword = AuthUtil.getEncryptedPassword(defaultPassword, salt, algorithm);
            if (password.equals(encryptedDefaultPassword)) {
                return true;
            }
        }
        catch (final DataAccessException dae) {
            UserManagementUtil.logger.log(Level.INFO, "Exception while checking isDefaultPasswordUsed for loginID " + loginId, (Throwable)dae);
        }
        return false;
    }
    
    public boolean getPwdChangeRequiredStatus(final Long loginID) throws Exception {
        Boolean isRequired = false;
        Map<Long, Boolean> isRequiredCache = (Map<Long, Boolean>)ApiFactoryProvider.getCacheAccessAPI().getCache("isDefaultPasswordChangeRequired");
        if (isRequiredCache == null) {
            isRequiredCache = new HashMap<Long, Boolean>();
        }
        else if (isRequiredCache.containsKey(loginID)) {
            return isRequiredCache.get(loginID);
        }
        final Long userID = DMUserHandler.getUserID(loginID);
        final String isRequiredParam = SyMUtil.getUserParameter(userID, "isDefaultPasswordChangeRequired");
        if (isRequiredParam != null) {
            isRequired = Boolean.parseBoolean(isRequiredParam);
            isRequiredCache.put(loginID, isRequired);
            ApiFactoryProvider.getCacheAccessAPI().putCache("isDefaultPasswordChangeRequired", (Object)isRequiredCache, 2);
            return isRequired;
        }
        if (this.isDefaultPasswordUsed(loginID)) {
            isRequired = true;
        }
        isRequiredCache.put(loginID, isRequired);
        SyMUtil.updateUserParameter(userID, "isDefaultPasswordChangeRequired", isRequired.toString());
        ApiFactoryProvider.getCacheAccessAPI().putCache("isDefaultPasswordChangeRequired", (Object)isRequiredCache, 2);
        return isRequired;
    }
    
    public void updateLocalUsersCache(final Long loginId, final boolean isLocalUser) {
        Map<Long, Boolean> usersMap = (Map<Long, Boolean>)ApiFactoryProvider.getCacheAccessAPI().getCache("localUsersMap", 3);
        if (usersMap == null) {
            usersMap = new HashMap<Long, Boolean>();
        }
        usersMap.put(loginId, isLocalUser);
        ApiFactoryProvider.getCacheAccessAPI().putCache("localUsersMap", (Object)usersMap, 2);
    }
    
    public boolean isLocalUser(final Long loginId) {
        Map<Long, Boolean> usersMap = (Map<Long, Boolean>)ApiFactoryProvider.getCacheAccessAPI().getCache("localUsersMap", 3);
        if (usersMap == null) {
            usersMap = new HashMap<Long, Boolean>();
        }
        else if (usersMap.containsKey(loginId)) {
            return usersMap.get(loginId);
        }
        final String domainName = DMUserHandler.getDCUserDomain(loginId);
        final boolean isLocalUser = StringUtils.isEmpty((CharSequence)domainName) || domainName.equalsIgnoreCase("-");
        usersMap.put(loginId, isLocalUser);
        ApiFactoryProvider.getCacheAccessAPI().putCache("localUsersMap", (Object)usersMap, 2);
        return isLocalUser;
    }
    
    public boolean isPasswordChangeRequired() {
        Boolean isRequired = false;
        try {
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (!LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T") && this.isLocalUser(loginId)) {
                isRequired = this.getPwdChangeRequiredStatus(loginId);
            }
            else {
                isRequired = false;
            }
        }
        catch (final Exception e) {
            UserManagementUtil.logger.log(Level.INFO, "Exception in isPasswordChangeRequired", e);
        }
        return isRequired;
    }
    
    public static User validateAndAuthenticateUser(final String userName, final String password, String domainName) throws Exception {
        final CredentialAPI credential = new CredentialAPI(userName, password, domainName);
        Authenticator authenticator = null;
        boolean isValidLogin = false;
        if (domainName == null || domainName.equalsIgnoreCase("local") || domainName.equals("")) {
            authenticator = new LocalAuthenticatorAPI(credential);
            isValidLogin = authenticator.authenticate();
            domainName = null;
        }
        else {
            final ArrayList<String> dclist = new ArrayList<String>();
            final Column column = Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN");
            final Criteria criteria = new Criteria(column, (Object)domainName, 0);
            final DataObject dataObject = DataAccess.get("ActiveDirectoryInfo", criteria);
            final Row row = dataObject.getFirstRow("ActiveDirectoryInfo");
            final String serverName = (String)row.get("SERVERNAME");
            final String secServerName = (String)row.get("SECONDARYSERVERNAME");
            final boolean ssl = (boolean)row.get("ISSSL");
            Integer port = (Integer)row.get("port");
            port = ((port == null) ? (ssl ? 636 : 389) : port);
            dclist.add(serverName);
            if (secServerName != null) {
                dclist.addAll(Arrays.asList(secServerName.split(",")));
            }
            for (final String dc : dclist) {
                if (new LDAPAuthenticator().authenticateUser(dc, domainName, userName, password, ssl, (int)port)) {
                    isValidLogin = true;
                    break;
                }
            }
        }
        User dcUser = null;
        if (isValidLogin) {
            dcUser = new CoreUserService().getLoginDataForUser(userName, domainName);
        }
        return dcUser;
    }
    
    public static void updateBadLoginCount(final String username, String service, String domain, final String hostName) {
        service = ((service == null) ? "System" : service);
        domain = (domain.equals("-") ? null : domain);
        try {
            final DataObject accountDO = AuthDBUtil.getAccountDO(username, service, domain);
            final Row accAdminProfileRow = accountDO.getFirstRow("AaaAccAdminProfile");
            final Row pwdStatusRow = accountDO.getRow("AaaPasswordStatus");
            if (pwdStatusRow.get(4).equals("BADLOGIN")) {
                return;
            }
            final int allowBadLoginCount = (int)accAdminProfileRow.get(10);
            final Row badLoginRow = new Row("AaaAccBadLoginStatus");
            badLoginRow.set(1, accountDO.getFirstValue("AaaAccount", 1));
            badLoginRow.set(3, (Object)hostName);
            badLoginRow.set(2, (Object)new Integer(1));
            badLoginRow.set(4, (Object)new Long(System.currentTimeMillis()));
            badLoginRow.set(5, (Object)hostName);
            accountDO.addRow(badLoginRow);
            final int totalBadLogins = accountDO.size("AaaAccBadLoginStatus");
            if (allowBadLoginCount >= 0 && totalBadLogins >= allowBadLoginCount) {
                pwdStatusRow.set(4, (Object)"BADLOGIN");
                pwdStatusRow.set(5, (Object)new Long(System.currentTimeMillis()));
                accountDO.updateRow(pwdStatusRow);
            }
            DataAccess.update(accountDO);
        }
        catch (final Exception e) {
            UserManagementUtil.logger.log(Level.WARNING, "Exception occured while updating BadLoginCount." + e);
        }
    }
    
    public static boolean isAccountLocked(final String loginName, String serviceName, String domainName) {
        serviceName = ((serviceName == null) ? "System" : serviceName);
        domainName = (domainName.equals("-") ? null : domainName);
        try {
            final DataObject accountDO = AuthDBUtil.getAccountDO(loginName, serviceName, domainName);
            final Row pstatsRow = accountDO.getRow("AaaPasswordStatus");
            if (pstatsRow != null && pstatsRow.get("STATUS").equals("BADLOGIN")) {
                final int updateInterval = (int)accountDO.getFirstValue("AaaPasswordProfile", "UPDATE_INTERVAL");
                final long statusUpdatedTime = (long)accountDO.getFirstValue("AaaPasswordStatus", "UPDATEDTIME");
                if (updateInterval == -1 || updateInterval <= 0 || statusUpdatedTime + updateInterval * 60 * 1000L >= System.currentTimeMillis()) {
                    return true;
                }
                final Row passStatusRow = accountDO.getFirstRow("AaaPasswordStatus");
                passStatusRow.set("STATUS", (Object)"ACTIVE");
                passStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                accountDO.updateRow(passStatusRow);
                DataAccess.update(accountDO);
            }
        }
        catch (final Exception var6) {
            UserManagementUtil.logger.log(Level.WARNING, "Exception occurred while checking lock status", var6);
        }
        return false;
    }
    
    public static void removeBadLoginStatusIfPresent(final String username, String serviceName, String domainName) {
        serviceName = ((serviceName == null) ? "System" : serviceName);
        domainName = (domainName.equals("-") ? null : domainName);
        try {
            final DataObject accountDO = AuthDBUtil.getAccountDO(username, serviceName, domainName);
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("AaaAccBadLoginStatus");
            uq.setUpdateColumn("NUMOF_BADLOGIN", (Object)new Integer(0));
            uq.setCriteria(new Criteria(Column.getColumn("AaaAccBadLoginStatus", "ACCOUNT_ID"), accountDO.getRow("AaaAccount").get(1), 0));
            DataAccess.update(uq);
        }
        catch (final DataAccessException var2) {
            UserManagementUtil.logger.log(Level.WARNING, "DataAccessException occurred while remove bad login status", (Throwable)var2);
        }
    }
    
    public void closeAllUserSessionsExceptCurrent() throws Exception {
        final List<Long> userIDs = DMUserHandler.getAvailableUserIDs(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)"-", 0));
        userIDs.remove(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
        for (final Long userID : userIDs) {
            if (this.isDefaultPasswordUsed(DMUserHandler.getLoginIdForUserId(userID))) {
                AuthUtil.closeAllSessions(userID);
            }
        }
    }
    
    public static boolean isUserNameLengthCriteriaPassed(final String userName) {
        Boolean lengthCriteria = Boolean.FALSE;
        if (userName != null) {
            final int length = userName.length();
            lengthCriteria = (length > 4 && length < 101);
        }
        return lengthCriteria;
    }
    
    public static boolean isEmailExistForOtherUser(final String emailID, final Long userID) {
        boolean isEmailExist = false;
        try {
            final SelectQuery emailOtherUserSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
            emailOtherUserSelectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            emailOtherUserSelectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            emailOtherUserSelectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            emailOtherUserSelectQuery.addSelectColumn(new Column("AaaContactInfo", "*"));
            final ArrayList userIdExclusionList = new ArrayList();
            userIdExclusionList.add(userID);
            userIdExclusionList.add(DBUtil.getUVHValue("AaaUser:user_id:3"));
            emailOtherUserSelectQuery.setCriteria(new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userIdExclusionList.toArray(), 9));
            final DataObject dataObject = SyMUtil.getPersistence().get(emailOtherUserSelectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AaaContactInfo");
                while (iterator.hasNext()) {
                    final Row userContactRow = iterator.next();
                    final String userEmail = (String)userContactRow.get("EMAILID");
                    if (userEmail != null && !userEmail.equalsIgnoreCase("") && emailID.equalsIgnoreCase(userEmail)) {
                        isEmailExist = true;
                        break;
                    }
                }
            }
            SyMUtil.updateUserParameter(userID, "isEmailExistForOtherUser", String.valueOf(isEmailExist));
        }
        catch (final Exception e) {
            UserManagementUtil.logger.log(Level.SEVERE, "Exception in retrieving the isEmailExistForOtherUser data for deleting user", e);
        }
        return isEmailExist;
    }
    
    public static boolean isUserMailServerEmailSame(final String userEmailAddress) {
        boolean status = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SmtpConfiguration"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getRow("SmtpConfiguration");
                final String mailServerEmail = (String)row.get("SENDER_ADDRESS");
                if (mailServerEmail.equalsIgnoreCase(userEmailAddress)) {
                    status = true;
                }
            }
        }
        catch (final Exception e) {
            UserManagementUtil.logger.log(Level.SEVERE, "Exception while validating Email of Mail Server Settings with User Email : ", e);
        }
        return status;
    }
    
    public int getUsersWithoutEmailAddress() {
        int noEmailCount = 0;
        try {
            final SelectQuery userQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
            userQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            userQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            userQuery.addSelectColumn(new Column("AaaUser", "USER_ID"));
            userQuery.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
            userQuery.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
            userQuery.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
            userQuery.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
            final Criteria emailCriteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)"", 0);
            userQuery.setCriteria(emailCriteria);
            final DataObject userDO = SyMUtil.getPersistence().get(userQuery);
            noEmailCount = userDO.size("AaaContactInfo");
        }
        catch (final Exception ex) {
            UserManagementUtil.logger.log(Level.SEVERE, "Exception in getUsersWithoutEmailAddress :{0}", ex);
        }
        return noEmailCount;
    }
    
    public int getDuplicateUserEmailCount() {
        int duplicateEmailCount = 0;
        try {
            final SelectQuery userQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
            userQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            userQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            final DerivedTable duplicateEmail1 = this.duplicateEmailTable();
            final Criteria joinCri = new Criteria(Column.getColumn("AaaContactInfo", "EMAILID"), (Object)Column.getColumn("duplicateEmail", "EMAILID"), 0);
            userQuery.addJoin(new Join(Table.getTable("AaaContactInfo"), (Table)duplicateEmail1, joinCri, 1));
            final Criteria emailCriteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)"", 1);
            Criteria invalidMailCriteria = emailCriteria.and(new Criteria(Column.getColumn("duplicateEmail", "EMAILID"), (Object)null, 1));
            invalidMailCriteria = invalidMailCriteria.and(new Criteria(Column.getColumn("AaaContactInfo", "EMAILID"), (Object)"", 1));
            userQuery.setCriteria(invalidMailCriteria);
            userQuery.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)userQuery);
            while (ds.next()) {
                ++duplicateEmailCount;
            }
        }
        catch (final Exception ex) {
            UserManagementUtil.logger.log(Level.SEVERE, "Exception in getDuplicateUserEmailCount :{0}", ex);
        }
        return duplicateEmailCount;
    }
    
    private DerivedTable duplicateEmailTable() {
        final SelectQuery duplicateEmail = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaContactInfo"));
        duplicateEmail.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
        final Column countCol = Column.getColumn("AaaContactInfo", "EMAILID").count();
        final GroupByColumn groupByMailCol = new GroupByColumn(new Column("AaaContactInfo", "EMAILID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByMailCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)1, 5));
        duplicateEmail.setGroupByClause(groupByClause);
        final DerivedTable duplicateEmailDerievedTab = new DerivedTable("duplicateEmail", (Query)duplicateEmail);
        return duplicateEmailDerievedTab;
    }
    
    static {
        UserManagementUtil.logger = Logger.getLogger("UserManagementLogger");
    }
}
