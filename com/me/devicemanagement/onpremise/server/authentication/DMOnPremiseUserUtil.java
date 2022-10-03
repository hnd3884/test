package com.me.devicemanagement.onpremise.server.authentication;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.regex.Matcher;
import com.adventnet.authentication.util.AuthDBUtil;
import java.util.regex.Pattern;
import com.adventnet.authentication.PAM;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.ds.query.Join;
import com.adventnet.authentication.PasswordException;
import java.util.List;
import com.adventnet.authentication.Credential;
import java.util.Iterator;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Date;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.admin.DMZendeskAPI;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class DMOnPremiseUserUtil
{
    public static final int SCOPE_ALL = 0;
    protected static Logger logger;
    public static final String DEFAULT_THEME = "sdp-blue";
    
    public static void changeAAAProfile(final Long userID, final String langCode, final String countryCode, final String timeZone) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "*"));
            Criteria criteria = new Criteria(Column.getColumn("AaaUserProfile", "USER_ID"), (Object)userID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            Row userRow = dobj.getRow("AaaUserProfile");
            if (userRow != null) {
                userRow.set("LANGUAGE_CODE", (Object)langCode);
                userRow.set("COUNTRY_CODE", (Object)countryCode);
                userRow.set("TIMEZONE", (Object)timeZone);
                dobj.updateRow(userRow);
                SyMUtil.getPersistence().update(dobj);
            }
            else {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaGenderHonorific"));
                sq.addSelectColumn(new Column("AaaGenderHonorific", "GH_ID"));
                criteria = new Criteria(Column.getColumn("AaaGenderHonorific", "GENDER"), (Object)"Male", 0);
                criteria = criteria.and(new Criteria(Column.getColumn("AaaGenderHonorific", "HONORIFIC"), (Object)"Mr", 0));
                sq.setCriteria(criteria);
                final DataObject dataObj = SyMUtil.getPersistence().get(sq);
                final Row r = dataObj.getFirstRow("AaaGenderHonorific");
                final WritableDataObject dobjNew = new WritableDataObject();
                userRow = new Row("AaaUserProfile");
                userRow.set("USER_ID", (Object)userID);
                userRow.set("GH_ID", r.get("GH_ID"));
                userRow.set("LANGUAGE_CODE", (Object)langCode);
                userRow.set("COUNTRY_CODE", (Object)countryCode);
                userRow.set("TIMEZONE", (Object)timeZone);
                dobjNew.addRow(userRow);
                SyMUtil.getPersistence().add((DataObject)dobjNew);
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception while set UserLanguage & country code :", e);
        }
    }
    
    public static boolean isDefaultAdminDisabled(final Long loginID) {
        try {
            final Long usreID = DMUserHandler.getUserID(loginID);
            final Criteria crt = new Criteria(new Column("AaaUserStatus", "USER_ID"), (Object)usreID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaUserStatus", crt);
            if (!dataObject.isEmpty()) {
                final String status = (String)dataObject.getFirstValue("AaaUserStatus", "STATUS");
                return !status.equalsIgnoreCase("ACTIVE");
            }
            return true;
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception in isDefaultAdminDisabled for loginID: " + loginID, e);
            return false;
        }
    }
    
    public static boolean unHideDefaultAdmin(final Long loginID) {
        try {
            final Long usreID = DMUserHandler.getUserID(loginID);
            final Criteria crt = new Criteria(new Column("AaaUserStatus", "USER_ID"), (Object)usreID, 0);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserStatus");
            updateQuery.setCriteria(crt);
            updateQuery.setUpdateColumn("STATUS", (Object)"ACTIVE");
            SyMUtil.getPersistence().update(updateQuery);
            final DataObject dObject = (DataObject)new WritableDataObject();
            final Row row = new Row("UsersRoleMapping");
            row.set("LOGIN_ID", (Object)loginID);
            row.set("UM_ROLE_ID", (Object)DMUserHandler.getRoleID("Administrator"));
            dObject.addRow(row);
            SyMUtil.getPersistence().update(dObject);
            return true;
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception while setting unHideDefaultAdmin for loginID: " + loginID, e);
            return false;
        }
    }
    
    public static boolean hideDefaultAdmin(final Long loginID) {
        try {
            final Long usreID = DMUserHandler.getUserID(loginID);
            final Criteria crt = new Criteria(new Column("AaaUserStatus", "USER_ID"), (Object)usreID, 0);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserStatus");
            updateQuery.setCriteria(crt);
            updateQuery.setUpdateColumn("STATUS", (Object)"DISABLED");
            SyMUtil.getPersistence().update(updateQuery);
            DMOnPremiseUserHandler.deleteOldPasswordForLogin(loginID);
            final Criteria selectCriteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginID, 0);
            SyMUtil.getPersistence().delete(selectCriteria);
            deleteAPIKeydetailsforSDPDC(loginID);
            DMUserHandler.addOrUpdateAPIKeyForLoginId(loginID);
            deleteSpiceworksUserNameForAdmin(loginID);
            final DMZendeskAPI dmZenDeskAPI = ApiFactoryProvider.getZendeskAPI();
            if (dmZenDeskAPI != null) {
                dmZenDeskAPI.deleteZendeskUserMappingForAdmin(loginID);
            }
            return true;
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception while setting hideDefaultAdmin for loginID: " + loginID, e);
            return false;
        }
    }
    
    private static void deleteAPIKeydetailsforSDPDC(final Long loginID) {
        try {
            final Criteria apikeyCriteria = new Criteria(new Column("APIKeyDetails", "LOGIN_ID"), (Object)loginID, 0);
            final Criteria apikeyCriteriaforSDP = new Criteria(new Column("APIKeyDetails", "SERVICE_TYPE"), (Object)Integer.valueOf("101"), 0);
            final Criteria criteria = apikeyCriteria.and(apikeyCriteriaforSDP);
            SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception while deleting the APIKey details for SDP");
        }
    }
    
    protected static void createORRetainDefaultAdminUser(final JSONObject modifyUserJObj) throws Exception {
        DMOnPremiseUserUtil.logger.log(Level.INFO, "Entering into createORRetainDefaultAdminUser()");
        try {
            final String loginName = "admin";
            final String serviceName = "System";
            final Long login_id_0 = DBUtil.getUVHValue("AaaLogin:login_id:0");
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)login_id_0, 0);
            final DataObject dobj = DataAccess.get("AaaLogin", criteria);
            if (dobj.isEmpty()) {
                reCreateUserForDC(loginName, loginName, "-", modifyUserJObj);
            }
            else {
                try {
                    AuthUtil.getAccountId(loginName, serviceName, "-");
                }
                catch (final DataAccessException dae) {
                    dae.printStackTrace();
                    DMOnPremiseUserUtil.logger.log(Level.INFO, "Default user dose not exists in DB so we create admin user");
                    reCreateUserForDC(loginName, loginName, "-", modifyUserJObj);
                }
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static Object getObject(final String tablename, final String getColName, final String gvnColName, final Object value) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn(tablename, gvnColName), value, 0);
        DataObject dobj = null;
        try {
            dobj = DataAccess.get(tablename, criteria);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        return dobj.getFirstValue(tablename, getColName);
    }
    
    protected static void deleteDummyAdmin(final Long userId, final Long loginId) throws DataAccessException {
        final Long login_id_0 = DBUtil.getUVHValue("AaaLogin:login_id:0");
        final Criteria selectCriteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)login_id_0, 0);
        DataAccess.delete(selectCriteria);
        updateUVHValue("AaaUser:user_id:0", userId);
        updateUVHValue("AaaLogin:login_id:0", loginId);
    }
    
    protected static void renameDummyAdmin(final Long userId, final Long loginId) throws DataAccessException {
        final Long login_id_0 = DBUtil.getUVHValue("AaaLogin:login_id:0");
        try {
            final UpdateQuery upDateQuery = (UpdateQuery)new UpdateQueryImpl("AaaLogin");
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)login_id_0, 0);
            upDateQuery.setCriteria(criteria);
            upDateQuery.setUpdateColumn("NAME", (Object)"admin_temp_0808");
            DataAccess.update(upDateQuery);
        }
        catch (final Exception re) {
            re.printStackTrace();
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
    }
    
    private static void reCreateUserForDC(final String userName, final String password, final String domainName, final JSONObject modifyUserJObj) throws Exception {
        final Long org_id_0 = DBUtil.getUVHValue("AaaOrganization:org_id:0");
        final Long gh_id_0 = DBUtil.getUVHValue("AaaGenderHonorific:gh_id:0");
        final Long accountprofile_id_1 = DBUtil.getUVHValue("AaaAccAdminProfile:accountprofile_id:1");
        final boolean isTwoFactorEnabledGlobaly = modifyUserJObj.optBoolean("isTwoFactorEnabledGlobaly", false);
        if (userName != null) {
            final WritableDataObject dobj = new WritableDataObject();
            final Row userRow = new Row("AaaUser");
            userRow.set("FIRST_NAME", (Object)userName);
            userRow.set("CREATEDTIME", (Object)new Date().getTime());
            dobj.addRow(userRow);
            final Row accContactInfoRow = new Row("AaaContactInfo");
            dobj.addRow(accContactInfoRow);
            final Row accUserContactInfoRow = new Row("AaaUserContactInfo");
            accUserContactInfoRow.set("USER_ID", userRow.get("USER_ID"));
            accUserContactInfoRow.set("CONTACTINFO_ID", accContactInfoRow.get("CONTACTINFO_ID"));
            dobj.addRow(accUserContactInfoRow);
            final Row aaaORGUserRow = new Row("AaaOrgUser");
            aaaORGUserRow.set("USER_ID", userRow.get("USER_ID"));
            aaaORGUserRow.set("ORG_ID", (Object)org_id_0);
            dobj.addRow(aaaORGUserRow);
            final Row aaaORGContactUserRow = new Row("AaaOrgContactUser");
            aaaORGContactUserRow.set("USER_ID", userRow.get("USER_ID"));
            aaaORGContactUserRow.set("ORG_ID", (Object)org_id_0);
            dobj.addRow(aaaORGContactUserRow);
            final Row aaaUserProfileRow = new Row("AaaUserProfile");
            aaaUserProfileRow.set("USER_ID", userRow.get("USER_ID"));
            aaaUserProfileRow.set("GH_ID", (Object)gh_id_0);
            aaaUserProfileRow.set("TIMEZONE", (Object)SyMUtil.getDefaultTimeZoneID());
            aaaUserProfileRow.set("LANGUAGE_CODE", (Object)"en");
            aaaUserProfileRow.set("COUNTRY_CODE", (Object)"us");
            dobj.addRow(aaaUserProfileRow);
            final Row aaaUserTwoFactorDetailsRow = new Row("AaaUserTwoFactorDetails");
            aaaUserTwoFactorDetailsRow.set("USER_ID", userRow.get("USER_ID"));
            aaaUserTwoFactorDetailsRow.set("ENABLED", (Object)isTwoFactorEnabledGlobaly);
            aaaUserTwoFactorDetailsRow.set("TWOFACTORAUTHIMPL", (Object)"com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            aaaUserTwoFactorDetailsRow.set("TWOFACTORPASSWORD", (Object)" ");
            dobj.addRow(aaaUserTwoFactorDetailsRow);
            final Row loginRow = new Row("AaaLogin");
            loginRow.set("NAME", (Object)userName);
            loginRow.set("USER_ID", userRow.get("USER_ID"));
            loginRow.set("DOMAINNAME", (Object)domainName.toLowerCase());
            dobj.addRow(loginRow);
            final Row accRow = new Row("AaaAccount");
            accRow.set("SERVICE_ID", getObject("AaaService", "SERVICE_ID", "NAME", "System"));
            accRow.set("ACCOUNTPROFILE_ID", (Object)accountprofile_id_1);
            accRow.set("LOGIN_ID", loginRow.get("LOGIN_ID"));
            final Row passwordRow = new Row("AaaPassword");
            passwordRow.set("PASSWORD", (Object)password);
            passwordRow.set("PASSWDPROFILE_ID", getObject("AaaPasswordProfile", "PASSWDPROFILE_ID", "NAME", "Profile 2"));
            dobj.addRow(passwordRow);
            final Row accPassRow = new Row("AaaAccPassword");
            accPassRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
            dobj.addRow(accPassRow);
            dobj.addRow(accRow);
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            dobj.addRow(accOwnerProfileRow);
            createDefaultAdminUserAccount((DataObject)dobj);
            final UpdateQuery upDateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserStatus");
            final Criteria criteria = new Criteria(Column.getColumn("AaaUserStatus", "USER_ID"), userRow.get("USER_ID"), 0);
            upDateQuery.setCriteria(criteria);
            upDateQuery.setUpdateColumn("STATUS", (Object)"DISABLED");
            DataAccess.update(upDateQuery);
            updateUVHValue("AaaUser:user_id:0", userRow.get("USER_ID"));
            updateUVHValue("AaaLogin:login_id:0", loginRow.get("LOGIN_ID"));
        }
    }
    
    public static void updateUVHValue(final String uvhPattern, final Object value) throws DataAccessException {
        try {
            final UpdateQuery upDateQuery = (UpdateQuery)new UpdateQueryImpl("UVHValues");
            final Criteria criteria = new Criteria(Column.getColumn("UVHValues", "PATTERN"), (Object)uvhPattern, 0);
            upDateQuery.setCriteria(criteria);
            upDateQuery.setUpdateColumn("GENVALUES", value);
            DataAccess.update(upDateQuery);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
    }
    
    public static DataObject createDefaultAdminUserAccount(final DataObject accountDO) throws DataAccessException, PasswordException {
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "createDefaultAdminUserAccount invoked with dataobject : {0}", accountDO);
        Iterator accItr = accountDO.getRows("AaaAccount");
        final int numOfAcc = getCount(accItr);
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            validateForAccountCreation(credential.getAccountId(), numOfAcc);
        }
        final List requiredTables = Arrays.asList("AaaUser", "AaaLogin", "AaaAccount", "AaaPassword", "AaaAccPassword");
        final List tablesFromDO = accountDO.getTableNames();
        if (!tablesFromDO.containsAll(requiredTables)) {
            throw new DataAccessException("In sufficient data for creating an account, required tables in dataobject " + requiredTables);
        }
        final long now = System.currentTimeMillis();
        final Row userRow = accountDO.getFirstRow("AaaUser");
        userRow.set("CREATEDTIME", (Object)new Long(now));
        final Row userStatusRow = new Row("AaaUserStatus");
        userStatusRow.set("USER_ID", userRow.get("USER_ID"));
        userStatusRow.set("STATUS", (Object)"ACTIVE");
        userStatusRow.set("UPDATEDTIME", (Object)new Long(now));
        accountDO.addRow(userStatusRow);
        final Row loginRow = accountDO.getFirstRow("AaaLogin");
        if (loginRow.get("USER_ID") == null) {
            loginRow.set("USER_ID", userRow.get("USER_ID"));
            accountDO.updateRow(loginRow);
        }
        accItr = accountDO.getRows("AaaAccount");
        Row accountRow = null;
        final List serviceIds = new ArrayList();
        while (accItr.hasNext()) {
            accountRow = accItr.next();
            serviceIds.add(accountRow.get("SERVICE_ID"));
            if (accountRow.get("LOGIN_ID") == null) {
                accountRow.set("LOGIN_ID", loginRow.get("LOGIN_ID"));
            }
            accountRow.set("CREATEDTIME", (Object)new Long(now));
            accountDO.updateRow(accountRow);
            Row accOwnerProfileRow = null;
            try {
                accOwnerProfileRow = accountDO.getFirstRow("AaaAccOwnerProfile", accountRow);
            }
            catch (final DataAccessException dae) {
                throw new DataAccessException("No account profile configured");
            }
            if (accOwnerProfileRow == null) {
                accOwnerProfileRow = new Row("AaaAccOwnerProfile");
                accOwnerProfileRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
                accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(0));
                accountDO.addRow(accOwnerProfileRow);
            }
            if (!accountDO.containsTable("AaaAccountOwner") && credential != null) {
                final Row accOwnerRow = new Row("AaaAccountOwner");
                accOwnerRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
                accOwnerRow.set("OWNERACCOUNT_ID", (Object)credential.getAccountId());
                accountDO.addRow(accOwnerRow);
            }
            final String accAdminProfile = (String)getObject("AaaAccAdminProfile", "NAME", "ACCOUNTPROFILE_ID", accountRow.get("ACCOUNTPROFILE_ID"));
            final Row accStatusRow = constructAccStatusRow(accountRow, accAdminProfile);
            accountDO.addRow(accStatusRow);
        }
        final Iterator passItr = accountDO.getRows("AaaPassword");
        Row passwordRow = null;
        while (passItr.hasNext()) {
            passwordRow = passItr.next();
            Long passRuleId = (Long)passwordRow.get("PASSWDRULE_ID");
            final Long passProfileId = (Long)passwordRow.get("PASSWDPROFILE_ID");
            if (passRuleId == null) {
                final String[] serviceNames = getServiceNames(serviceIds);
                passRuleId = getCompatiblePassRuleId(serviceNames);
                passwordRow.set("PASSWDRULE_ID", (Object)passRuleId);
            }
            final String salt = BCrypt.gensalt();
            final String encPass = AuthUtil.getEncryptedPassword((String)passwordRow.get("PASSWORD"), salt, (String)passwordRow.get("ALGORITHM"));
            passwordRow.set("PASSWORD", (Object)encPass);
            passwordRow.set("ALGORITHM", passwordRow.get("ALGORITHM"));
            passwordRow.set("CREATEDTIME", (Object)new Long(now));
            passwordRow.set("SALT", (Object)salt);
            accountDO.updateRow(passwordRow);
            final String passwordProfile = (String)getObject("AaaPasswordProfile", "NAME", "PASSWDPROFILE_ID", passProfileId);
            final Row passStatusRow = constructPassStatusRow(passwordRow, passwordProfile);
            accountDO.addRow(passStatusRow);
        }
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "account validated dataobject is : {0}", accountDO);
        final DataObject addedDO = DataAccess.add(accountDO);
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "account added successfully");
        return addedDO;
    }
    
    public static DataObject createUserAccountWithoutPassword(final DataObject accountDO) throws DataAccessException, PasswordException {
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "createUserAccount invoked with dataobject : {0}", accountDO);
        Iterator accItr = accountDO.getRows("AaaAccount");
        final int numOfAcc = getCount(accItr);
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            validateForAccountCreation(credential.getAccountId(), numOfAcc);
        }
        final List requiredTables = Arrays.asList("AaaUser", "AaaLogin", "AaaAccount");
        final List tablesFromDO = accountDO.getTableNames();
        if (!tablesFromDO.containsAll(requiredTables)) {
            throw new DataAccessException("In sufficient data for creating an account, required tables in dataobject " + requiredTables);
        }
        final long now = System.currentTimeMillis();
        final Row userRow = accountDO.getFirstRow("AaaUser");
        userRow.set("CREATEDTIME", (Object)new Long(now));
        final Row userStatusRow = new Row("AaaUserStatus");
        userStatusRow.set("USER_ID", userRow.get("USER_ID"));
        userStatusRow.set("STATUS", (Object)"ACTIVE");
        userStatusRow.set("UPDATEDTIME", (Object)new Long(now));
        accountDO.addRow(userStatusRow);
        final Row loginRow = accountDO.getFirstRow("AaaLogin");
        if (loginRow.get("USER_ID") == null) {
            loginRow.set("USER_ID", userRow.get("USER_ID"));
            accountDO.updateRow(loginRow);
        }
        accItr = accountDO.getRows("AaaAccount");
        Row accountRow = null;
        final List serviceIds = new ArrayList();
        while (accItr.hasNext()) {
            accountRow = accItr.next();
            serviceIds.add(accountRow.get("SERVICE_ID"));
            if (accountRow.get("LOGIN_ID") == null) {
                accountRow.set("LOGIN_ID", loginRow.get("LOGIN_ID"));
            }
            accountRow.set("CREATEDTIME", (Object)new Long(now));
            accountDO.updateRow(accountRow);
            Row accOwnerProfileRow = null;
            try {
                accOwnerProfileRow = accountDO.getFirstRow("AaaAccOwnerProfile", accountRow);
            }
            catch (final DataAccessException dae) {
                throw new DataAccessException("No account profile configured");
            }
            if (accOwnerProfileRow == null) {
                accOwnerProfileRow = new Row("AaaAccOwnerProfile");
                accOwnerProfileRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
                accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(0));
                accountDO.addRow(accOwnerProfileRow);
            }
            if (!accountDO.containsTable("AaaAccountOwner") && credential != null) {
                final Row accOwnerRow = new Row("AaaAccountOwner");
                accOwnerRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
                accOwnerRow.set("OWNERACCOUNT_ID", (Object)credential.getAccountId());
                accountDO.addRow(accOwnerRow);
            }
            final String accAdminProfile = (String)getObject("AaaAccAdminProfile", "NAME", "ACCOUNTPROFILE_ID", accountRow.get("ACCOUNTPROFILE_ID"));
            final Row accStatusRow = constructAccStatusRow(accountRow, accAdminProfile);
            accountDO.addRow(accStatusRow);
        }
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "account validated dataobject is : {0}", accountDO);
        final DataObject addedDO = DataAccess.add(accountDO);
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "account added successfully");
        return addedDO;
    }
    
    private static int getCount(final Iterator itr) {
        int count = 0;
        while (itr.hasNext()) {
            ++count;
            itr.next();
        }
        return count;
    }
    
    private static void validateForAccountCreation(final Long accountId, final int numOfAccToCreate) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccount", "parentAcc"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaAccount", "AaaAccountOwner", new String[] { "ACCOUNT_ID" }, new String[] { "OWNERACCOUNT_ID" }, "parentAcc", "AaaAccountOwner", 1));
        sq.addJoin(new Join("AaaAccountOwner", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, "AaaAccountOwner", "childAcc", 1));
        sq.addJoin(new Join("AaaAccount", "AaaAccOwnerProfile", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, "parentAcc", "AaaAccOwnerProfile", 1));
        final Criteria criteria = new Criteria(Column.getColumn("parentAcc", "ACCOUNT_ID"), (Object)accountId, 0);
        sq.setCriteria(criteria);
        final DataObject dobj = DataAccess.get(sq);
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "DataObject fetched for childaccounts : {0}", dobj);
        if (!dobj.containsTable("AaaAccOwnerProfile")) {
            throw new DataAccessException("No account profile configured");
        }
        final Row accOwnerProfileRow = dobj.getFirstRow("AaaAccOwnerProfile");
        final int allowedSubAcc = (int)accOwnerProfileRow.get("ALLOWED_SUBACCOUNT");
        if (allowedSubAcc == -1) {
            return;
        }
        final Iterator itr = dobj.getRows("childAcc");
        final int noOfChildAccAvble = getCount(itr);
        if (allowedSubAcc >= noOfChildAccAvble + numOfAccToCreate) {
            return;
        }
        DMOnPremiseUserUtil.logger.log(Level.WARNING, "user permitted to add only {0} accounts. Already {1} created", new Object[] { new Integer(allowedSubAcc), new Integer(noOfChildAccAvble) });
        throw new DataAccessException("Not permitted to add more than [" + allowedSubAcc + "] user accounts");
    }
    
    private static Row constructAccStatusRow(final Row accountRow, final String accAdminProfile) throws DataAccessException {
        final Row accProfileRow = getRowMatching("AaaAccAdminProfile", "NAME", accAdminProfile);
        final int expPeriod = (int)accProfileRow.get("EXP_AFTER");
        long expAt = -1L;
        if (expPeriod != -1) {
            final long createdTime = (long)accountRow.get("CREATEDTIME");
            expAt = createdTime + expPeriod * 24 * 60 * 60 * 1000L;
        }
        final Row accStatusRow = new Row("AaaAccountStatus");
        accStatusRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
        accStatusRow.set("STATUS", (Object)"NEW");
        accStatusRow.set("EXPIREAT", (Object)new Long(expAt));
        accStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "account status row constructed is : {0}", new Object[] { accStatusRow });
        return accStatusRow;
    }
    
    private static String[] getServiceNames(final List serviceIds) {
        final String[] serviceNames = new String[10];
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AaaService", "SERVICE_ID"), (Object)serviceIds.toArray(), 8);
            final DataObject dobj = DataAccess.get("AaaService", criteria);
            final Iterator itr = dobj.getRows("AaaService");
            Row serviceRow = null;
            int i = 0;
            while (itr.hasNext()) {
                serviceRow = itr.next();
                serviceNames[i] = (String)serviceRow.get("NAME");
                ++i;
            }
        }
        catch (final DataAccessException dae) {
            DMOnPremiseUserUtil.logger.log(Level.FINEST, "DataAccessException ocured while fetching service names from ids ", (Throwable)dae);
        }
        return serviceNames;
    }
    
    public static Long getCompatiblePassRuleId(final String[] services) throws DataAccessException {
        final int servicesLen = services.length;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaServicePasswordRule"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaServicePasswordRule", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        sq.addJoin(new Join("AaaServicePasswordRule", "AaaPasswordRule", new String[] { "PASSWDRULE_ID" }, new String[] { "PASSWDRULE_ID" }, 2));
        sq.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)services, 8));
        DataObject dobj = null;
        try {
            dobj = DataAccess.get(sq);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        final Iterator itr = dobj.getRows("AaaPasswordRule");
        final int count = getCount(itr);
        if (count == 0 || count > 1) {
            DMOnPremiseUserUtil.logger.log(Level.FINEST, "more than one password rule obtained. so use password rule mapped to service System");
            final SelectQuery clonedSq = (SelectQuery)sq.clone();
            clonedSq.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)"System", 0));
            try {
                dobj = DataAccess.get(clonedSq);
            }
            catch (final Exception re2) {
                throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re2);
            }
        }
        final Row passRuleRow = dobj.getFirstRow("AaaPasswordRule");
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "passwordrule row obtained matching services : {0} is : {1}", new Object[] { services, passRuleRow });
        return (Long)passRuleRow.get("PASSWDRULE_ID");
    }
    
    public static Row getRowMatching(final String tablename, final String colName, final Object value) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn(tablename, colName), value, 0);
        DataObject dobj = null;
        try {
            dobj = DataAccess.get(tablename, criteria);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        return dobj.getFirstRow(tablename);
    }
    
    private static Row constructPassStatusRow(final Row passwordRow, final String passwordProfile) throws DataAccessException {
        final Row passProfileRow = getRowMatching("AaaPasswordProfile", "NAME", passwordProfile);
        final int expPeriod = (int)passProfileRow.get("EXP_AFTER");
        long expAt = -1L;
        if (expPeriod != -1) {
            final long createdTime = (long)passwordRow.get("CREATEDTIME");
            expAt = createdTime + expPeriod * 24 * 60 * 60 * 1000;
        }
        final Row passwordStatusRow = new Row("AaaPasswordStatus");
        passwordStatusRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
        passwordStatusRow.set("STATUS", (Object)"NEW");
        passwordStatusRow.set("EXPIREAT", (Object)new Long(expAt));
        passwordStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
        DMOnPremiseUserUtil.logger.log(Level.FINEST, "password status row constructed is : {0}", passwordStatusRow);
        return passwordStatusRow;
    }
    
    private static void validateForPasswordRule(final String loginName, final String newPasswd, final Row passRuleRow) throws PasswordException {
        final int minLen = (int)passRuleRow.get("MIN_LENGTH");
        final int maxLen = (int)passRuleRow.get("MAX_LENGTH");
        final boolean loginNameIndep = (boolean)passRuleRow.get("LOGINNAME_INDEPNDT");
        final int currSize = newPasswd.length();
        if (currSize < minLen) {
            throw new PasswordException("Length of the new password is smaller than expected value of : " + minLen);
        }
        if (currSize > maxLen) {
            throw new PasswordException("Length of the new password is greater than expected value of : " + maxLen);
        }
        if (loginNameIndep && loginName.equals(newPasswd)) {
            throw new PasswordException("password cannot be the same as loginname");
        }
    }
    
    public static boolean isUserCreatedByDC(final Long loginId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCAAALOGIN"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("DCAAALOGIN", "LOGIN_ID"), (Object)loginId, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.size("DCAAALOGIN") <= 0) {
                DMOnPremiseUserUtil.logger.log(Level.WARNING, "The user already created by DC. So we are not going to update his role");
                return true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void addOrUpdateUserInLoginExtn(final Long loginID, final Integer computerScope, final Integer mdmScope) throws Exception {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AaaLoginExtn", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLoginExtn", criteria);
            if (dataObject.isEmpty()) {
                final Row row = new Row("AaaLoginExtn");
                row.set("LOGIN_ID", (Object)loginID);
                row.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
                row.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
                row.set("SCOPE", (Object)computerScope);
                row.set("MDM_SCOPE", (Object)mdmScope);
                dataObject.addRow(row);
                SyMUtil.getPersistence().add(dataObject);
            }
            else {
                final Row row = dataObject.getFirstRow("AaaLoginExtn");
                row.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
                row.set("SCOPE", (Object)computerScope);
                row.set("MDM_SCOPE", (Object)mdmScope);
                dataObject.updateRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, " Exception while adding customer-user relation in DB ...", ex);
            throw ex;
        }
    }
    
    public static Long addUser(final DataObject addUserDO, final JSONObject addUserJObj) throws Exception {
        Long loginID = null;
        try {
            final String password = (String)addUserJObj.get("password");
            final Integer computerScope = addUserJObj.optInt("scope", 0);
            final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
            final boolean isTwoFactorEnabledGlobaly = addUserJObj.optBoolean("isTwoFactorEnabledGlobaly", false);
            final UniqueValueHolder loginId = (UniqueValueHolder)addUserDO.getFirstValue("AaaLogin", "LOGIN_ID");
            final Long passwordPolicyId = addUserJObj.has("passwordPolicyId") ? Long.valueOf(addUserJObj.getLong("passwordPolicyId")) : null;
            final Long passwordProfileId = addUserJObj.has("passwordProfileId") ? addUserJObj.getLong("passwordProfileId") : AuthUtil.getPasswordProfileId("Profile 2");
            final Row passwordRow = new Row("AaaPassword");
            passwordRow.set("PASSWORD", (Object)password);
            if (passwordPolicyId != null) {
                passwordRow.set("PASSWDRULE_ID", (Object)passwordPolicyId);
            }
            passwordRow.set("PASSWDPROFILE_ID", (Object)passwordProfileId);
            addUserDO.addRow(passwordRow);
            final Row accRow = addUserDO.getRow("AaaAccount");
            final Row accPassRow = new Row("AaaAccPassword");
            accPassRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
            addUserDO.addRow(accPassRow);
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            addUserDO.addRow(accOwnerProfileRow);
            final Row aaaLoginExtn = new Row("AaaLoginExtn");
            aaaLoginExtn.set("LOGIN_ID", (Object)loginId);
            aaaLoginExtn.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("SCOPE", (Object)computerScope);
            aaaLoginExtn.set("MDM_SCOPE", (Object)mdmScope);
            addUserDO.addRow(aaaLoginExtn);
            final Row userRow = addUserDO.getRow("AaaUser");
            final Row accUserTwoFactorDetailsRow = new Row("AaaUserTwoFactorDetails");
            accUserTwoFactorDetailsRow.set("USER_ID", userRow.get("USER_ID"));
            accUserTwoFactorDetailsRow.set("ENABLED", (Object)isTwoFactorEnabledGlobaly);
            accUserTwoFactorDetailsRow.set("TWOFACTORAUTHIMPL", (Object)"com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            accUserTwoFactorDetailsRow.set("TWOFACTORPASSWORD", (Object)" ");
            addUserDO.addRow(accUserTwoFactorDetailsRow);
            AuthUtil.createUserAccount(addUserDO);
            loginID = (Long)loginId.getValue();
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - AddUser : ", ex);
            throw ex;
        }
        return loginID;
    }
    
    public static Long addUserWithoutPassword(final DataObject addUserDO, final JSONObject addUserJObj) throws Exception {
        DMOnPremiseUserUtil.logger.log(Level.INFO, "Add user without password for user {0}", addUserJObj.get("userName"));
        Long loginID = null;
        try {
            final Integer computerScope = addUserJObj.optInt("scope", 0);
            final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
            final boolean isTwoFactorEnabledGlobaly = addUserJObj.optBoolean("isTwoFactorEnabledGlobaly", false);
            final UniqueValueHolder loginId = (UniqueValueHolder)addUserDO.getFirstValue("AaaLogin", "LOGIN_ID");
            final Row accRow = addUserDO.getRow("AaaAccount");
            final int noOfSubAccounts = -1;
            final Row accOwnerProfileRow = new Row("AaaAccOwnerProfile");
            accOwnerProfileRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
            accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(noOfSubAccounts));
            addUserDO.addRow(accOwnerProfileRow);
            final Row aaaLoginExtn = new Row("AaaLoginExtn");
            aaaLoginExtn.set("LOGIN_ID", (Object)loginId);
            aaaLoginExtn.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("SCOPE", (Object)computerScope);
            aaaLoginExtn.set("MDM_SCOPE", (Object)mdmScope);
            addUserDO.addRow(aaaLoginExtn);
            final Row userRow = addUserDO.getRow("AaaUser");
            final Row accUserTwoFactorDetailsRow = new Row("AaaUserTwoFactorDetails");
            accUserTwoFactorDetailsRow.set("USER_ID", userRow.get("USER_ID"));
            accUserTwoFactorDetailsRow.set("ENABLED", (Object)isTwoFactorEnabledGlobaly);
            accUserTwoFactorDetailsRow.set("TWOFACTORAUTHIMPL", (Object)"com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            accUserTwoFactorDetailsRow.set("TWOFACTORPASSWORD", (Object)" ");
            addUserDO.addRow(accUserTwoFactorDetailsRow);
            createUserAccountWithoutPassword(addUserDO);
            loginID = (Long)loginId.getValue();
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - AddUser : ", ex);
            throw ex;
        }
        return loginID;
    }
    
    public static void modifyUser(final JSONObject modifyUserJObj) throws Exception {
        try {
            final Long loginID = (Long)modifyUserJObj.get("loginID");
            final String contactinfoID = String.valueOf(modifyUserJObj.get("contactinfoID"));
            final String userName = (String)modifyUserJObj.get("userName");
            final String password = modifyUserJObj.optString("password", (String)null);
            final String emailID = modifyUserJObj.optString("USER_EMAIL_ID");
            final String phNum = modifyUserJObj.optString("USER_PH_NO");
            final String domainName = modifyUserJObj.optString("domainName", "-");
            final Integer computerScope = modifyUserJObj.optInt("scope", 0);
            final Integer mdmScope = modifyUserJObj.optInt("mdmScope", 0);
            final String oldUserName = DMUserHandler.getDCUser(loginID);
            final Long oldUserID = DMUserHandler.getDCUserID(loginID);
            Criteria criteria = new Criteria(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"), (Object)contactinfoID, 0);
            UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaContactInfo");
            if (emailID != null && !emailID.isEmpty()) {
                updateQuery.setUpdateColumn("EMAILID", (Object)emailID);
            }
            updateQuery.setUpdateColumn("LANDLINE", (Object)phNum);
            updateQuery.setCriteria(criteria);
            SyMUtil.getPersistence().update(updateQuery);
            if (!oldUserName.equalsIgnoreCase(userName) && userName.equalsIgnoreCase("admin")) {
                renameDummyAdmin(oldUserID, loginID);
            }
            addOrUpdateUserInLoginExtn(loginID, computerScope, mdmScope);
            criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaLogin");
            updateQuery.setUpdateColumn("DOMAINNAME", (Object)domainName.toLowerCase());
            if (!oldUserName.equalsIgnoreCase(userName)) {
                updateQuery.setUpdateColumn("NAME", (Object)userName);
            }
            updateQuery.setCriteria(criteria);
            SyMUtil.getPersistence().update(updateQuery);
            if (!oldUserName.equalsIgnoreCase(userName)) {
                criteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)oldUserID, 0);
                updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUser");
                updateQuery.setUpdateColumn("FIRST_NAME", (Object)userName);
                updateQuery.setCriteria(criteria);
                SyMUtil.getPersistence().update(updateQuery);
                createORRetainDefaultAdminUser(modifyUserJObj);
            }
            if (!oldUserName.equalsIgnoreCase(userName) && userName.equalsIgnoreCase("admin")) {
                deleteDummyAdmin(oldUserID, loginID);
            }
            if ((domainName == null || domainName.equalsIgnoreCase("-")) && password != null) {
                final String serviceName = SYMClientUtil.getServiceName(userName);
                AuthUtil.changePassword(userName, serviceName, password);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - ModifyUser : ", ex);
            throw ex;
        }
    }
    
    public static String getMDMScopeInString(final Long scopeID) {
        String scopeLevel = "";
        try {
            scopeLevel = I18N.getMsg("dc.admin.addUser.all_device_label", new Object[0]);
            if (scopeID == 1L) {
                scopeLevel = I18N.getMsg("dc.common.GROUPS", new Object[0]);
            }
            return scopeLevel;
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception in getScopeInString for scopeID: " + scopeID, ex);
            return scopeLevel;
        }
    }
    
    public static boolean isUserLimitReached() {
        return LicenseProvider.getInstance().isUserLimitReached();
    }
    
    public static boolean isActiveUser(final String pluginLoginName, final String domainName) {
        final Boolean status = isActiveUser(pluginLoginName, domainName, "-");
        return status;
    }
    
    public static boolean isActiveUser(final String pluginLoginName, final String domainName, final String reqURI) {
        Boolean status = Boolean.TRUE;
        final String defaultPluginLoginName = "admin";
        Long loginID = null;
        try {
            loginID = DMUserHandler.getLoginIdForUser(pluginLoginName, domainName);
            if (loginID == null && pluginLoginName != null && domainName == null && (pluginLoginName.equalsIgnoreCase("administrator") || pluginLoginName.equalsIgnoreCase("admin"))) {
                loginID = DMUserHandler.getLoginIdForUser(defaultPluginLoginName, domainName);
            }
            if (!SolutionUtil.getInstance().isInvIntegrationMode() || (reqURI != null && !reqURI.contains("/htmlViewer"))) {
                status = !isDefaultAdminDisabled(loginID);
            }
            else {
                final boolean bServiceDeskEnabled = SolutionUtil.getInstance().isIntegrationMode();
                final boolean bAssetExplorerEnabled = SolutionUtil.getInstance().isAEIntegrationMode();
                Long sdpUserStatus = -1L;
                if (bServiceDeskEnabled) {
                    sdpUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, "HelpDesk");
                }
                if (bAssetExplorerEnabled) {
                    sdpUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, "AssetExplorer");
                }
                final String isOldSDPCustomer = SolutionUtil.getInstance().getIntegrationParamsValue("isOldSDPCustomer");
                if (isOldSDPCustomer != null && isOldSDPCustomer.equalsIgnoreCase("yes") && sdpUserStatus == 1L) {
                    return status;
                }
                status = !isDefaultAdminDisabled(loginID);
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception in isActiveUser for scopeID: ", e);
        }
        return status;
    }
    
    private static void deleteSpiceworksUserNameForAdmin(final Long loginID) {
        try {
            if (SyMUtil.getSyMParameter("isSpiceworksEnabled") != null && SyMUtil.getSyMParameter("isSpiceworksEnabled").equalsIgnoreCase("enabled")) {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DCSpiceworksUserMapping"));
                sq.addSelectColumn(Column.getColumn((String)null, "*"));
                sq.setCriteria(new Criteria(Column.getColumn("DCSpiceworksUserMapping", "LOGIN_ID"), (Object)loginID, 0));
                final DataObject dobj = SyMUtil.getPersistence().get(sq);
                if (!dobj.isEmpty()) {
                    final Row spiceUserRow = dobj.getFirstRow("DCSpiceworksUserMapping");
                    if (spiceUserRow.get("SPICE_USER_NAME") != null) {
                        DMOnPremiseUserUtil.logger.log(Level.WARNING, "Spice user name is available");
                        final Criteria crt = new Criteria(new Column("DCSpiceworksUserMapping", "LOGIN_ID"), (Object)loginID, 0);
                        SyMUtil.getPersistence().delete(crt);
                        DMOnPremiseUserUtil.logger.log(Level.WARNING, "Updated spice user name");
                    }
                }
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception Occured while deleting Spiceworks Username for Admin User");
        }
    }
    
    public static void logoutAllSessions(final ArrayList<String> sessionId) {
        try {
            for (final String sessionID : sessionId) {
                PAM.logout(sessionID);
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in logout of all sessions ", e);
        }
    }
    
    public static ArrayList getSessionValue() {
        final ArrayList<String> sessionValue = new ArrayList<String>();
        try {
            final Long accountId = getAccountID();
            final ArrayList<Long> sessionId = getActiveSession(accountId);
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccHttpSession", "SESSION_ID"), (Object)sessionId.toArray(new Long[sessionId.size()]), 8);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaAccHttpSession", criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AaaAccHttpSession");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    sessionValue.add((String)row.get("SSO_ID"));
                }
                return sessionValue;
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getting Session value : ", e);
        }
        return sessionValue;
    }
    
    public static ArrayList getSessionValueForUser(final Long accountID) {
        final ArrayList<String> sessionValue = new ArrayList<String>();
        try {
            final ArrayList<Long> sessionId = getActiveSession(accountID);
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccHttpSession", "SESSION_ID"), (Object)sessionId.toArray(new Long[sessionId.size()]), 8);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaAccHttpSession", criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AaaAccHttpSession");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    sessionValue.add((String)row.get("SSO_ID"));
                }
                return sessionValue;
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getting Session value : ", e);
        }
        return sessionValue;
    }
    
    public static String getSSOIDForSessionID(final Long sessionID) {
        try {
            return (String)DBUtil.getValueFromDB("AaaAccHttpSession", "SESSION_ID", (Object)sessionID, "SSO_ID");
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getting Session value : ", e);
            return null;
        }
    }
    
    public static ArrayList getActiveSession(final Long accountId) {
        final ArrayList<Long> list = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
            final Criteria AccountIdCriteria = new Criteria(Column.getColumn("AaaAccSession", "ACCOUNT_ID"), (Object)accountId, 0);
            final Criteria statusCriteria = new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"ACTIVE", 0);
            final Join join = new Join("AaaAccSession", "AaaAccHttpSession", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 2);
            selectQuery.setCriteria(AccountIdCriteria.and(statusCriteria));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "SESSION_ID"));
            selectQuery.addJoin(join);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AaaAccSession");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    list.add((Long)row.get("SESSION_ID"));
                }
                return list;
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getting Active Sesssion : " + e);
        }
        return list;
    }
    
    public static List<Long> getActiveSessionForLoginID(final Long loginID) {
        final Long accountID = getAccountIdForLoginId(loginID);
        return getActiveSession(accountID);
    }
    
    public static Long getAccountID() {
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaAccount", criteria);
            return dataObject.isEmpty() ? null : ((Long)dataObject.getFirstValue("AaaAccount", "ACCOUNT_ID"));
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getting accountID : " + e);
            return null;
        }
    }
    
    public static Long getAccountIdForLoginId(final Long loginId) {
        final String sourceMethod = "DMOnpremiseUserUtil::getAccountIdForLoginId";
        Long accountId = null;
        try {
            final SelectQuery selectQuery = DMUserHandler.getLoginAccountQuery();
            final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
            selectQuery.setCriteria(criteria);
            accountId = (Long)SyMUtil.getCachedPersistence().get(selectQuery).getFirstValue("AaaAccount", "ACCOUNT_ID");
        }
        catch (final DataAccessException ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, sourceMethod + " - Exception while getting account ID for login ID", (Throwable)ex);
        }
        return accountId;
    }
    
    public static boolean isValidPassword(final String loginName, final String password, final JSONObject passwordPolicy) throws PasswordException {
        if (password != null) {
            try {
                final Integer minLength = (Integer)(passwordPolicy.has("MIN_LENGTH") ? passwordPolicy.get("MIN_LENGTH") : 5);
                final Boolean isComplexPwd = passwordPolicy.has("IS_COMPLEX_PASSWORD") && (boolean)passwordPolicy.get("IS_COMPLEX_PASSWORD");
                final Integer reuseFor = (Integer)(passwordPolicy.has("PREVENT_REUSE_FOR") ? passwordPolicy.get("PREVENT_REUSE_FOR") : -1);
                final Boolean loginNameIndep = passwordPolicy.has("IS_LOGIN_NAME_USAGE_RESTRICTED") && (boolean)passwordPolicy.get("IS_LOGIN_NAME_USAGE_RESTRICTED");
                final String complexPasswordRegex = isComplexPwd ? "(?=.*[a-z])(?=.*[A-Z])(?=.*[!~@#$%^&+=_*])" : "";
                final String expression = "^(?=.{" + minLength + ",25})" + complexPasswordRegex + ".*$";
                final Pattern pattern = Pattern.compile(expression);
                final Matcher matcher = pattern.matcher(password);
                if (!matcher.find()) {
                    return false;
                }
                final String serviceName = "System";
                final DataObject accountDO = AuthDBUtil.getAccountPasswordDO(loginName, serviceName, (String)null);
                if (reuseFor != -1 && reuseFor != 0) {
                    final Iterator oldPassItr = accountDO.getRows("oldPass");
                    validateNewPassword(oldPassItr, password);
                }
                if (loginNameIndep && loginName.equalsIgnoreCase(password)) {
                    throw new PasswordException("Password cannot be the same as Login Name");
                }
            }
            catch (final PasswordException pwdEx) {
                throw pwdEx;
            }
            catch (final Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public static boolean addOrUpdatePasswordPolicy(final JSONObject policyDetails) {
        boolean isUpdated = Boolean.FALSE;
        DMOnPremiseUserUtil.logger.log(Level.INFO, "In addOrUpdatePasswordPolicy with policyDetails", policyDetails);
        try {
            final Integer minLength = Integer.parseInt((String)policyDetails.get("MIN_LENGTH"));
            Integer preventReuseFor = Integer.parseInt((String)policyDetails.get("PREVENT_REUSE_FOR"));
            final Boolean isComplexPassword = (Boolean)policyDetails.get("IS_COMPLEX_PASSWORD");
            final Boolean loginRestrictionEnabled = (Boolean)policyDetails.get("ENABLE_LOGIN_RESTRICTION");
            Integer badAttempt = Integer.parseInt((String)policyDetails.get("BAD_ATTEMPT"));
            Integer lockPeriod = Integer.parseInt((String)policyDetails.get("LOCK_PERIOD"));
            final Boolean loginNameIndep = policyDetails.has("IS_LOGIN_NAME_USAGE_RESTRICTED") && (boolean)policyDetails.get("IS_LOGIN_NAME_USAGE_RESTRICTED");
            final DataObject policyDO = getCustomPasswordPolicyDO(true);
            final DataObject passwdProfileDO = getCustomPasswdProfileDO(true);
            final DataObject accProfileDO = getCustomAccountProfileDO(true);
            preventReuseFor = ((preventReuseFor == 0) ? -1 : preventReuseFor);
            if (!loginRestrictionEnabled) {
                badAttempt = -1;
                lockPeriod = 0;
                unlockAccount();
            }
            if (policyDO != null && passwdProfileDO != null && accProfileDO != null) {
                Row policyRow = policyDO.getRow("AaaPasswordRule");
                if (policyRow == null) {
                    policyRow = new Row("AaaPasswordRule");
                }
                policyRow.set("NAME", (Object)"CUSTOM_POLICY");
                policyRow.set("MIN_LENGTH", (Object)minLength);
                policyRow.set("MAX_LENGTH", (Object)25);
                policyRow.set("LOGINNAME_INDEPNDT", (Object)loginNameIndep);
                Row passwdProfileRow = passwdProfileDO.getRow("AaaPasswordProfile");
                if (passwdProfileRow == null) {
                    passwdProfileRow = new Row("AaaPasswordProfile");
                }
                passwdProfileRow.set("NAME", (Object)"CUSTOM_PROFILE");
                passwdProfileRow.set("NUMOF_OLDPASSWD", (Object)preventReuseFor);
                passwdProfileRow.set("CHPASSWD_ONFL", (Object)false);
                passwdProfileRow.set("LOGIN_AFTEREXP", (Object)(-1));
                passwdProfileRow.set("UPDATE_INTERVAL", (Object)lockPeriod);
                if (isComplexPassword) {
                    policyRow.set("REQ_MIXEDCASE", (Object)Boolean.TRUE);
                    policyRow.set("NUMOF_SPLCHAR", (Object)1);
                }
                else {
                    policyRow.set("REQ_MIXEDCASE", (Object)Boolean.FALSE);
                    policyRow.set("NUMOF_SPLCHAR", (Object)(-1));
                }
                Row accProfileRow = accProfileDO.getRow("AaaAccAdminProfile");
                if (accProfileRow == null) {
                    accProfileRow = new Row("AaaAccAdminProfile");
                }
                accProfileRow.set("NAME", (Object)"CUSTOM_PROFILE");
                accProfileRow.set("ALLOWED_BADLOGIN", (Object)badAttempt);
                final boolean isPolicyUpdated = checkAndUpdateDO(policyDO, policyRow);
                final boolean isPasswdProfileUpdated = checkAndUpdateDO(passwdProfileDO, passwdProfileRow);
                final boolean isAccProfileUpdated = checkAndUpdateDO(accProfileDO, accProfileRow);
                if (policyDO.isEmpty() || passwdProfileDO.isEmpty() || accProfileDO.isEmpty()) {
                    final Long passPolicyID = (Long)policyRow.get("PASSWDRULE_ID");
                    final Long passProfileID = (Long)policyRow.get("PASSWDRULE_ID");
                    final Long accProfileID = (Long)accProfileRow.get("ACCOUNTPROFILE_ID");
                    updatePasswordProfileForAllUsers(passProfileID, passPolicyID, accProfileID);
                }
                isUpdated = (isPolicyUpdated && isPasswdProfileUpdated && isAccProfileUpdated);
                final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                DCEventLogUtil.getInstance().addEvent(8000, userName, (HashMap)null, "dc.uac.PASSWORDPOLICY.UPDATE", (Object)null, true);
            }
        }
        catch (final Exception e) {
            isUpdated = false;
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in addOrUpdatePasswordPolicy", e);
        }
        return isUpdated;
    }
    
    protected static void unlockAccount() throws Exception {
        try {
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("AaaAccBadLoginStatus");
            uq.setUpdateColumn("NUMOF_BADLOGIN", (Object)new Integer(0));
            SyMUtil.getPersistence().update(uq);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaPasswordStatus");
            updateQuery.setUpdateColumn("STATUS", (Object)"ACTIVE");
            updateQuery.setUpdateColumn("UPDATEDTIME", (Object)System.currentTimeMillis());
            SyMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.WARNING, "Exception in unlocking account ", e);
        }
    }
    
    protected static DataObject getCustomAccountProfileDO(final Boolean getCustomProfile) throws Exception {
        DataObject profileDO = null;
        try {
            final SelectQuery accProfileSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccAdminProfile"));
            accProfileSelect.addSelectColumn(Column.getColumn("AaaAccAdminProfile", "ACCOUNTPROFILE_ID"));
            accProfileSelect.addSelectColumn(Column.getColumn("AaaAccAdminProfile", "NAME"));
            accProfileSelect.addSelectColumn(Column.getColumn("AaaAccAdminProfile", "ALLOWED_BADLOGIN"));
            Criteria accCriteria = new Criteria(Column.getColumn("AaaAccAdminProfile", "NAME"), (Object)"CUSTOM_PROFILE", 0, (boolean)Boolean.FALSE);
            accProfileSelect.setCriteria(accCriteria);
            profileDO = SyMUtil.getPersistence().get(accProfileSelect);
            if ((profileDO == null || profileDO.isEmpty()) && !getCustomProfile) {
                accCriteria = new Criteria(Column.getColumn("AaaAccAdminProfile", "NAME"), (Object)"Profile 2", 0, (boolean)Boolean.FALSE);
                accProfileSelect.setCriteria(accCriteria);
                profileDO = SyMUtil.getPersistence().get(accProfileSelect);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getCustomPasswordPolicyDO", ex);
        }
        return profileDO;
    }
    
    protected static boolean checkAndUpdateDO(final DataObject dataObject, final Row row) {
        boolean isUpdated = false;
        try {
            if (dataObject != null && !dataObject.isEmpty()) {
                dataObject.updateRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
            else {
                final WritableDataObject writableDO = new WritableDataObject();
                writableDO.addRow(row);
                SyMUtil.getPersistence().update((DataObject)writableDO);
            }
            isUpdated = true;
        }
        catch (final Exception ex) {
            isUpdated = false;
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in addOrUpdatePasswordPolicy", ex);
        }
        return isUpdated;
    }
    
    protected static DataObject getCustomPasswordPolicyDO(final Boolean getCustomProfile) {
        DataObject policyDO = null;
        try {
            final SelectQuery passwordPolicySelect = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaPasswordRule"));
            passwordPolicySelect.addSelectColumn(Column.getColumn("AaaPasswordRule", "PASSWDRULE_ID"));
            passwordPolicySelect.addSelectColumn(Column.getColumn("AaaPasswordRule", "NAME"));
            passwordPolicySelect.addSelectColumn(Column.getColumn("AaaPasswordRule", "MIN_LENGTH"));
            passwordPolicySelect.addSelectColumn(Column.getColumn("AaaPasswordRule", "REQ_MIXEDCASE"));
            passwordPolicySelect.addSelectColumn(Column.getColumn("AaaPasswordRule", "NUMOF_SPLCHAR"));
            passwordPolicySelect.addSelectColumn(Column.getColumn("AaaPasswordRule", "LOGINNAME_INDEPNDT"));
            Criteria passRuleCrit = new Criteria(Column.getColumn("AaaPasswordRule", "NAME"), (Object)"CUSTOM_POLICY", 0, (boolean)Boolean.FALSE);
            passwordPolicySelect.setCriteria(passRuleCrit);
            policyDO = SyMUtil.getPersistence().get(passwordPolicySelect);
            if ((policyDO == null || policyDO.isEmpty()) && !getCustomProfile) {
                passRuleCrit = new Criteria(Column.getColumn("AaaPasswordRule", "NAME"), (Object)"Normal", 0, (boolean)Boolean.FALSE);
                passwordPolicySelect.setCriteria(passRuleCrit);
                policyDO = SyMUtil.getPersistence().get(passwordPolicySelect);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getCustomPasswordPolicyDO", ex);
        }
        return policyDO;
    }
    
    protected static DataObject getCustomPasswdProfileDO(final Boolean getCustomProfile) {
        DataObject profileDO = null;
        try {
            final SelectQuery passwordProfileSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaPasswordProfile"));
            passwordProfileSelect.addSelectColumn(Column.getColumn("AaaPasswordProfile", "PASSWDPROFILE_ID"));
            passwordProfileSelect.addSelectColumn(Column.getColumn("AaaPasswordProfile", "NAME"));
            passwordProfileSelect.addSelectColumn(Column.getColumn("AaaPasswordProfile", "NUMOF_OLDPASSWD"));
            passwordProfileSelect.addSelectColumn(Column.getColumn("AaaPasswordProfile", "UPDATE_INTERVAL"));
            Criteria passProfileCrit = new Criteria(Column.getColumn("AaaPasswordProfile", "NAME"), (Object)"CUSTOM_PROFILE", 0, (boolean)Boolean.FALSE);
            passwordProfileSelect.setCriteria(passProfileCrit);
            profileDO = SyMUtil.getPersistence().get(passwordProfileSelect);
            if ((profileDO == null || profileDO.isEmpty()) && !getCustomProfile) {
                passProfileCrit = new Criteria(Column.getColumn("AaaPasswordProfile", "NAME"), (Object)"Profile 2", 0, (boolean)Boolean.FALSE);
                passwordProfileSelect.setCriteria(passProfileCrit);
                profileDO = SyMUtil.getPersistence().get(passwordProfileSelect);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getCustomPasswdProfileDO", ex);
        }
        return profileDO;
    }
    
    public static JSONObject getPasswordPolicyDetails() {
        final JSONObject policyDetails = new JSONObject();
        try {
            final DataObject policyDO = getCustomPasswordPolicyDO(false);
            final DataObject passwdProfileDO = getCustomPasswdProfileDO(false);
            final DataObject accProfileDO = getCustomAccountProfileDO(false);
            if (policyDO != null && !policyDO.isEmpty()) {
                final Row policyRow = policyDO.getFirstRow("AaaPasswordRule");
                final Integer minLength = (Integer)policyRow.get("MIN_LENGTH");
                final Integer noOfSplChar = (Integer)policyRow.get("NUMOF_SPLCHAR");
                final Boolean mixedCase = (Boolean)policyRow.get("REQ_MIXEDCASE");
                final Boolean loginNameIndep = (Boolean)policyRow.get("LOGINNAME_INDEPNDT");
                policyDetails.put("IS_LOGIN_NAME_USAGE_RESTRICTED", (Object)loginNameIndep);
                policyDetails.put("MIN_LENGTH", (Object)minLength);
                policyDetails.put("IS_COMPLEX_PASSWORD", (Object)mixedCase);
            }
            if (passwdProfileDO != null && !passwdProfileDO.isEmpty()) {
                final Row profileRow = passwdProfileDO.getFirstRow("AaaPasswordProfile");
                Integer preventReuseFor = (Integer)profileRow.get("NUMOF_OLDPASSWD");
                final Integer lockPeriod = (Integer)profileRow.get("UPDATE_INTERVAL");
                preventReuseFor = ((preventReuseFor == -1) ? 0 : preventReuseFor);
                policyDetails.put("PREVENT_REUSE_FOR", (Object)preventReuseFor);
                policyDetails.put("LOCK_PERIOD", (Object)lockPeriod);
            }
            if (accProfileDO != null && !accProfileDO.isEmpty()) {
                final Row profileRow = accProfileDO.getFirstRow("AaaAccAdminProfile");
                Integer badAttempt = (Integer)profileRow.get("ALLOWED_BADLOGIN");
                boolean loginRestrictionEnabled = true;
                if (badAttempt < 1) {
                    badAttempt = 1;
                    loginRestrictionEnabled = false;
                }
                policyDetails.put("BAD_ATTEMPT", (Object)badAttempt);
                policyDetails.put("ENABLE_LOGIN_RESTRICTION", loginRestrictionEnabled);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getPasswordPolicyDetails", ex);
        }
        return policyDetails;
    }
    
    public static JSONObject getDefaultPasswordPolicy() {
        final JSONObject policyDetails = new JSONObject();
        try {
            policyDetails.put("MIN_LENGTH", 5);
            policyDetails.put("IS_COMPLEX_PASSWORD", (Object)Boolean.FALSE);
            policyDetails.put("PREVENT_REUSE_FOR", 0);
            policyDetails.put("ENABLE_LOGIN_RESTRICTION", (Object)Boolean.FALSE);
            policyDetails.put("BAD_ATTEMPT", 1);
            policyDetails.put("LOCK_PERIOD", 1);
            policyDetails.put("IS_LOGIN_NAME_USAGE_RESTRICTED", (Object)Boolean.FALSE);
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getDefaultPasswordPolicy", e);
        }
        return policyDetails;
    }
    
    public static Long getConfiguredCustomPasswdPolicyID() {
        Long policyId = null;
        try {
            final DataObject policyDO = getCustomPasswordPolicyDO(false);
            final Row policyRow = policyDO.getFirstRow("AaaPasswordRule");
            policyId = (Long)policyRow.get("PASSWDRULE_ID");
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getConfiguredCustomPasswdPolicyID", ex);
        }
        return policyId;
    }
    
    public static Long getConfiguredCustomPasswdProfileID() {
        Long profileId = null;
        try {
            final DataObject profileDO = getCustomPasswdProfileDO(false);
            final Row profileRow = profileDO.getFirstRow("AaaPasswordProfile");
            profileId = (Long)profileRow.get("PASSWDPROFILE_ID");
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getConfiguredCustomPasswdProfileID", ex);
        }
        return profileId;
    }
    
    public static Long getConfiguredCustomAccountProfileID() {
        Long profileId = null;
        try {
            final DataObject profileDO = getCustomAccountProfileDO(false);
            final Row profileRow = profileDO.getFirstRow("AaaAccAdminProfile");
            profileId = (Long)profileRow.get("ACCOUNTPROFILE_ID");
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in getConfiguredCustomAccountProfileID ", ex);
        }
        return profileId;
    }
    
    protected static void updatePasswordProfileForAllUsers(final Long passwdProfileId, final Long policyId, final Long accProfileId) {
        final SelectQuery createdUsersSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaPassword"));
        final Join aaaaccPasswordJoin = new Join("AaaPassword", "AaaAccPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2);
        final Join aaaaccountJoin = new Join("AaaAccPassword", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
        final Join aaaLoginJoin = new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
        final Join dcaaaLoginJoin = new Join("AaaLogin", "DCAaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1);
        createdUsersSelect.addJoin(aaaaccPasswordJoin);
        createdUsersSelect.addJoin(aaaaccountJoin);
        createdUsersSelect.addJoin(aaaLoginJoin);
        createdUsersSelect.addJoin(dcaaaLoginJoin);
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaPassword", "PASSWORD_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaPassword", "PASSWDRULE_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaPassword", "PASSWDPROFILE_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaPassword", "PASSWDRULE_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaAccPassword", "ACCOUNT_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaAccPassword", "PASSWORD_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNTPROFILE_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        createdUsersSelect.addSelectColumn(Column.getColumn("DCAaaLogin", "LOGIN_ID"));
        final Criteria excludeSDPCriteria = new Criteria(Column.getColumn("DCAaaLogin", "LOGIN_ID"), (Object)null, 0);
        createdUsersSelect.setCriteria(excludeSDPCriteria);
        try {
            final DataObject createdUsersDO = SyMUtil.getPersistence().get(createdUsersSelect);
            if (createdUsersDO != null) {
                Iterator itr = createdUsersDO.getRows("AaaPassword");
                while (itr.hasNext()) {
                    final Row passRow = itr.next();
                    passRow.set("PASSWDPROFILE_ID", (Object)passwdProfileId);
                    passRow.set("PASSWDRULE_ID", (Object)policyId);
                    createdUsersDO.updateRow(passRow);
                }
                itr = createdUsersDO.getRows("AaaAccount");
                while (itr.hasNext()) {
                    final Row profileRow = itr.next();
                    profileRow.set("ACCOUNTPROFILE_ID", (Object)accProfileId);
                    createdUsersDO.updateRow(profileRow);
                }
                SyMUtil.getPersistence().update(createdUsersDO);
            }
        }
        catch (final Exception ex) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception in updatePasswordProfileForAllUsers", ex);
        }
    }
    
    private static void validateNewPassword(final Iterator oldPassItr, final String newPassword) throws PasswordException {
        Row oldPassRow = null;
        String oldPasswd = null;
        String salt = null;
        while (oldPassItr.hasNext()) {
            oldPassRow = oldPassItr.next();
            salt = (String)oldPassRow.get("SALT");
            oldPasswd = (String)oldPassRow.get("PASSWORD");
            final String algorithm = (String)oldPassRow.get("ALGORITHM");
            if (AuthUtil.getEncryptedPassword(newPassword, salt, algorithm).equals(oldPasswd)) {
                throw new PasswordException("new password matches one of old password");
            }
        }
    }
    
    public static Boolean isLanguageChanged(final Long userID, final String language) {
        try {
            String lang = null;
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("AaaUserProfile", "USER_ID"), (Object)userID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            final Row userRow = dobj.getRow("AaaUserProfile");
            if (userRow != null) {
                lang = (String)userRow.get("LANGUAGE_CODE");
                DMOnPremiseUserUtil.logger.info("The current lang is : " + lang);
                DMOnPremiseUserUtil.logger.info("The ui lang is : " + language);
                if (!lang.equalsIgnoreCase(language)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            DMOnPremiseUserUtil.logger.log(Level.SEVERE, "Exception while isLanguageChanged ", e);
        }
        return false;
    }
    
    public static DataObject setNewUserPassword(DataObject userDO, final JSONObject addUserJObj) throws DataAccessException, PasswordException {
        final String password = (String)addUserJObj.get("password");
        final Long now = System.currentTimeMillis();
        final Row accRow = userDO.getRow("AaaAccount");
        final Row loginRow = userDO.getRow("AaaLogin");
        final String domainName = (String)loginRow.get("DOMAINNAME");
        final boolean isLocalUser = domainName == null || domainName.equalsIgnoreCase("-");
        Long passwordPolicyId;
        Long passwordProfileId;
        if (isLocalUser) {
            passwordPolicyId = getConfiguredCustomPasswdPolicyID();
            passwordProfileId = getConfiguredCustomPasswdProfileID();
        }
        else {
            final ArrayList serviceIds = new ArrayList();
            serviceIds.add(accRow.get("SERVICE_ID"));
            final String[] serviceNames = getServiceNames(serviceIds);
            passwordPolicyId = getCompatiblePassRuleId(serviceNames);
            passwordProfileId = AuthUtil.getPasswordProfileId("Profile 2");
        }
        final Row passRuleRow = getRowMatching("AaaPasswordRule", "PASSWDRULE_ID", passwordPolicyId);
        validateForPasswordRule((String)loginRow.get("NAME"), password, passRuleRow);
        final Row passProfileRow = AuthDBUtil.getRowMatching("AaaPasswordProfile", "PASSWDPROFILE_ID", (Object)passwordProfileId);
        final Object workFactor = passProfileRow.get("FACTOR");
        final String algorithm = "bcrypt";
        int workload;
        if (workFactor != null && Integer.parseInt(workFactor.toString()) > 0) {
            workload = Integer.parseInt(workFactor.toString());
        }
        else {
            workload = PAM.workload;
        }
        final String salt = BCrypt.gensalt(workload);
        final String encPass = AuthUtil.getEncryptedPassword(password, salt, algorithm);
        final Row passwordRow = new Row("AaaPassword");
        passwordRow.set("PASSWDRULE_ID", (Object)passwordPolicyId);
        passwordRow.set("PASSWDPROFILE_ID", (Object)passwordProfileId);
        passwordRow.set("PASSWORD", (Object)encPass);
        passwordRow.set("ALGORITHM", (Object)algorithm);
        passwordRow.set("CREATEDTIME", (Object)new Long(now));
        passwordRow.set("SALT", (Object)salt);
        userDO.addRow(passwordRow);
        final Row accPassRow = new Row("AaaAccPassword");
        accPassRow.set("ACCOUNT_ID", accRow.get("ACCOUNT_ID"));
        accPassRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
        userDO.addRow(accPassRow);
        final String passwordProfile = (String)passProfileRow.get("NAME");
        final Row passStatusRow = constructPassStatusRow(passwordRow, passwordProfile);
        userDO.addRow(passStatusRow);
        userDO = DataAccess.update(userDO);
        return userDO;
    }
    
    static {
        DMOnPremiseUserUtil.logger = Logger.getLogger("UserManagementLogger");
    }
}
