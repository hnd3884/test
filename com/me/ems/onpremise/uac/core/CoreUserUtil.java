package com.me.ems.onpremise.uac.core;

import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.onpremise.server.general.CountryProvider;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.emsalerts.notifications.core.handlers.EmailAlertsHandler;
import com.me.emsalerts.notifications.core.AlertDetails;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.UUID;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.deletionfw.DeletionFramework;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.authentication.UserListenerHandler;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.TreeMap;
import com.me.devicemanagement.onpremise.webclient.admin.UserController;
import com.me.ems.framework.uac.handler.UserOperationsHandler;
import com.me.devicemanagement.framework.server.api.DCSDPRequestAPI;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.authentication.util.AuthUtil;
import java.util.Collection;
import java.util.Arrays;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;

public class CoreUserUtil extends DMUserHandler
{
    public static String getLoginNameFromId(final long loginId) throws APIException {
        final Map<String, Object> loginDetails = validateLoginId(loginId);
        return loginDetails.get("NAME");
    }
    
    public static Map<String, Object> validateLoginId(final long loginId) throws APIException {
        final Map<String, Object> loginDetails = getLoginDetails(Long.valueOf(loginId));
        if (loginDetails == null || loginDetails.isEmpty()) {
            throw new APIException("UAC001");
        }
        return loginDetails;
    }
    
    public static String getAuthTypeFromId(final long loginId) throws APIException {
        final Map<String, Object> loginDetails = validateLoginId(loginId);
        String domainName = loginDetails.get("DOMAINNAME");
        if (domainName == null) {
            domainName = "local";
        }
        return domainName;
    }
    
    public static List getUserMappingCG(final Long loginID) {
        return getUserMappingCG(loginID, false);
    }
    
    public static List getUserMappingCG(final Long loginID, final boolean forUACPage) {
        return getUserMappingCG(loginID, forUACPage, null);
    }
    
    public static List getUserMappingCG(final Long loginID, final boolean forUACPage, final String domainName) {
        final List<Hashtable> customGroupList = new ArrayList<Hashtable>();
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("Resource"));
            final Join cgJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            Criteria computerTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)1, 0);
            final Criteria staticUniqueCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)new Integer[] { 1, 5 }, 8);
            if (domainName != null && !domainName.equals("")) {
                final Criteria domainCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0);
                computerTypeCriteria = computerTypeCriteria.and(domainCri);
            }
            computerTypeCriteria = computerTypeCriteria.and(staticUniqueCriteria);
            selectQuery.addJoin(cgJoin);
            if (!isUserManagingAllComputers(loginID) || forUACPage) {
                final Join userJoin = new Join("Resource", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
                selectQuery.addJoin(userJoin);
                final Criteria userCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginID, 0);
                computerTypeCriteria = computerTypeCriteria.and(userCriteria);
            }
            selectQuery.setCriteria(computerTypeCriteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), true);
            final SortColumn sortCol = new SortColumn(Column.getColumn("Resource", "NAME"), true);
            selectQuery.addSortColumn(sortColumn, 0);
            selectQuery.addSortColumn(sortCol, 1);
            final DataObject cgResourceDO = SyMUtil.getPersistence().get((SelectQuery)selectQuery);
            if (!cgResourceDO.isEmpty() && cgResourceDO.containsTable("Resource")) {
                final Iterator iterator = cgResourceDO.getRows("Resource");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Hashtable customGroupHashtable = new Hashtable();
                    customGroupHashtable.put("customGroupID", row.get("RESOURCE_ID"));
                    customGroupHashtable.put("customGroupName", row.get("NAME"));
                    customGroupHashtable.put("customGroupDomain", row.get("DOMAIN_NETBIOS_NAME"));
                    customGroupList.add(customGroupHashtable);
                }
            }
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.WARNING, "Error in getting the CG list of user IDs", e);
        }
        return customGroupList;
    }
    
    public static DataObject getUserDetails(final Long loginId) throws DataAccessException {
        SelectQuery selectQuery = getDCUsersQuery();
        selectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaLoginExtn", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "DOMAINNAME"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "LANDLINE"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLoginExtn", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLoginExtn", "SCOPE"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLoginExtn", "MDM_SCOPE"));
        final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
        selectQuery.setCriteria(criteria);
        selectQuery = ApiFactoryProvider.getAuthUtilAccessAPI().getActiveUsersCriteria(selectQuery);
        return DataAccess.get(selectQuery);
    }
    
    public Long addUser(final JSONObject addUserJObj) throws Exception {
        Long loginID = null;
        try {
            final String userName = (String)addUserJObj.get("userName");
            final String roleID = (String)addUserJObj.get("role_ID");
            final String domainName = addUserJObj.optString("domainName", "-");
            final String[] cgList = (String[])addUserJObj.opt("cgList");
            final String[] roList = (String[])addUserJObj.opt("roList");
            final Integer computerScope = addUserJObj.optInt("scope", 0);
            final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
            final String domainNameForLog = addUserJObj.optString("domainName", "Local");
            final String computerScopeForLog = (computerScope == 0) ? "ALL_MANAGED_COMPUTERS" : ((computerScope == 1) ? "SCOPE_CG" : "SCOPE_RO");
            final String mdmScopeForLog = (mdmScope == 0) ? "ALL_MANAGED_MOBILE_DEVICES" : "SCOPE_CG";
            final String emailID = addUserJObj.has("USER_EMAIL_ID") ? addUserJObj.get("USER_EMAIL_ID").toString() : "";
            ArrayList<String> newList = new ArrayList<String>();
            if (cgList != null && cgList.length > 0) {
                newList = new ArrayList<String>(Arrays.asList(cgList));
            }
            else if (roList != null && roList.length > 0) {
                newList = new ArrayList<String>(Arrays.asList(roList));
            }
            addUserJObj.put("passwordProfileId", (Object)AuthUtil.getPasswordProfileId("Profile 2"));
            addUserJObj.put("accountprofile_id", (Object)AuthUtil.getAccountProfileId("Profile 2"));
            final boolean isPluginUser = addUserJObj.has("isPluginUser") && addUserJObj.getBoolean("isPluginUser");
            if (!isPluginUser && (!addUserJObj.has("domainName") || addUserJObj.get("domainName") == null || domainName.equals("-") || addUserJObj.get("domainName").toString().equalsIgnoreCase("local"))) {
                final Long passwdPolicyId = DMOnPremiseUserUtil.getConfiguredCustomPasswdPolicyID();
                final Long passwdProfileId = DMOnPremiseUserUtil.getConfiguredCustomPasswdProfileID();
                final Long accProfileId = DMOnPremiseUserUtil.getConfiguredCustomAccountProfileID();
                if (passwdPolicyId != null) {
                    addUserJObj.put("passwordPolicyId", (Object)passwdPolicyId);
                }
                if (passwdProfileId != null) {
                    addUserJObj.put("passwordProfileId", (Object)passwdProfileId);
                }
                if (accProfileId != null) {
                    addUserJObj.put("accountprofile_id", (Object)accProfileId);
                }
            }
            if (!isPluginUser && emailID != null && !emailID.isEmpty() && this.isEmailAlreadyExists(emailID, null)) {
                CoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - addUser : EmailExists");
                throw new SyMException(720, I18N.getMsg("ems.admin.admin.email_already_exists", new Object[] { emailID }), "ems.admin.admin.email_already_exists", (Throwable)null);
            }
            if (!isUserAccountAvailable(userName, domainName)) {
                CoreUserUtil.logger.log(Level.INFO, "In addUser of DCUserHandler...Going to add  user " + userName + " in domain = " + domainNameForLog + " with Role id = " + roleID + " computerscope " + computerScopeForLog + " MDM Scope " + mdmScopeForLog);
                addUserJObj.put("service_id", (Object)AuthUtil.getServiceId("System"));
                loginID = this.addUserInUserTablesWOPassword(addUserJObj);
                CoreUserUtil.logger.log(Level.INFO, "User " + userName + " added in domain " + domainNameForLog + " with Role id = " + roleID + " computerscope = " + computerScopeForLog + " MDM Scope = " + mdmScopeForLog + " mapping list CG/RO resources id= " + newList);
                this.updateSystemParams(addUserJObj);
            }
            else {
                final boolean bAssetExplorerEnabled = SolutionUtil.getInstance().isAEIntegrationMode();
                final boolean bSDPDeskEnabled = SolutionUtil.getInstance().isIntegrationMode();
                final boolean bSdpEnabled = SolutionUtil.getInstance().isInvIntegrationMode();
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                Long buildno = null;
                try {
                    final String sdpBuildNumber = SolutionUtil.getInstance().getSDPBuildNumber();
                    buildno = Long.parseLong(sdpBuildNumber);
                    CoreUserUtil.logger.log(Level.INFO, "build number for SDP", sdpBuildNumber);
                }
                catch (final Exception e) {
                    CoreUserUtil.logger.log(Level.INFO, "Exception while getting sdp build no");
                }
                if (bSdpEnabled && !isMSP && ((bSDPDeskEnabled && buildno != null && buildno > 11299L) || bAssetExplorerEnabled)) {
                    loginID = DMUserHandler.getLoginIdForUser(userName, domainName);
                    final Long sdpUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, "HelpDesk");
                    final Long aeUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, "AssetExplorer");
                    if (sdpUserStatus != -1L || aeUserStatus != -1L) {
                        final Properties properties = new Properties();
                        Long appID = SolutionUtil.getInstance().getIntegratedApplicationId("HelpDesk");
                        if (aeUserStatus != -1L) {
                            appID = SolutionUtil.getInstance().getIntegratedApplicationId("AssetExplorer");
                        }
                        addUserJObj.put("loginID", (Object)loginID);
                        addUserJObj.put("contactinfoID", ((Hashtable<K, Object>)DMUserHandler.getContactInfoProp(DMUserHandler.getDCUserID(loginID))).get("contactInfoID"));
                        this.modifyUser(addUserJObj);
                        properties.setProperty("applicationID", appID.toString());
                        properties.setProperty("loginID", loginID.toString());
                        properties.setProperty("status", "2");
                        SolutionUtil.getInstance().addIntegratedServiceUser(properties);
                        final DCSDPRequestAPI sdpUserHandler = (DCSDPRequestAPI)Class.forName("com.me.dconpremise.webclient.sdp.util.DCRequestHandlerUtil").newInstance();
                        sdpUserHandler.changeSDPUser(loginID, "ACTIVE", false);
                    }
                }
                else {
                    loginID = this.handleExistingUser(addUserJObj);
                }
            }
            addUserJObj.put("scopeList", (Collection)newList);
            handleUserAddedListeners(loginID, addUserJObj);
        }
        catch (final SyMException symEx) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - userAdded : ", (Throwable)symEx);
            throw symEx;
        }
        catch (final Exception ex) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - userAdded : ", ex);
            throw ex;
        }
        return loginID;
    }
    
    private Long addUserInUserTablesWOPassword(final JSONObject addUserJObj) throws Exception {
        Long loginID = null;
        try {
            SyMUtil.getUserTransaction().begin();
            final DataObject addUserDO = SyMUtil.getPersistence().constructDataObject();
            new DMUserHandler().addUser(addUserDO, addUserJObj);
            loginID = DMOnPremiseUserUtil.addUserWithoutPassword(addUserDO, addUserJObj);
            addUserJObj.put("loginID", (Object)loginID);
            this.addUserInDCCoreTables(loginID, addUserJObj);
            this.addUserInDCOnPremiseTables(loginID, addUserJObj);
            addUserJObj.put("newUser", true);
            this.addOrUpdateUserStatus(addUserDO, addUserJObj);
            final List<String> classNames = UserOperationsHandler.getUserOperationsImplClassNames();
            for (final String className : classNames) {
                final UserOperationsInterface userOperation = (UserOperationsInterface)Class.forName(className).newInstance();
                userOperation.doAddUserTableOperations(addUserJObj);
            }
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.INFO, "Exception while adding user", e);
            SyMUtil.getUserTransaction().rollback();
            throw e;
        }
        return loginID;
    }
    
    public boolean isEmailAlreadyExists(final String emailID, final Long loginID) throws DataAccessException {
        final SelectQuery emailQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
        final Join userContactJoin = new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        final Join contactInfoJoin = new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2);
        final Join userStatusJoin = new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        Criteria criteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)emailID, 0);
        criteria = criteria.and(new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0));
        emailQuery.addJoin(userContactJoin);
        emailQuery.addJoin(contactInfoJoin);
        emailQuery.addJoin(userStatusJoin);
        if (loginID != null) {
            criteria = criteria.and(new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 1));
        }
        emailQuery.setCriteria(criteria);
        emailQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject emailExistsDO = SyMUtil.getPersistence().get(emailQuery);
        return emailExistsDO != null && !emailExistsDO.isEmpty();
    }
    
    protected void updateSystemParams(final JSONObject addUserJObj) {
        try {
            final String domainName = addUserJObj.optString("domainName", (String)null);
            final UserController uc = new UserController();
            final TreeMap domainList = UserController.getADDomainNamesForLoginPage();
            final String defaultDomainName = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
            if (domainList == null && domainName != null && !domainName.equalsIgnoreCase("-") && (defaultDomainName == null || !domainName.equalsIgnoreCase(defaultDomainName))) {
                SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", domainName);
            }
            final String isPasswordChanged = SyMUtil.getSyMParameter("IS_PASSWORD_CHANGED");
            if (isPasswordChanged != null && isPasswordChanged.equals("false")) {
                SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
            }
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception while updating system params...", e);
        }
    }
    
    public void addUserInDCCoreTables(final Long loginID, final JSONObject addUserJObj) throws Exception {
        final String roleID = (String)addUserJObj.get("role_ID");
        final String[] cgList = (String[])addUserJObj.opt("cgList");
        final Object sCustomerIDs = addUserJObj.opt("sCustomerIDs");
        final Long userID = getDCUserID(loginID);
        setRoleForUser(loginID, Long.valueOf(Long.parseLong(roleID)));
        ApiFactoryProvider.getUserManagementAPIHandler().addUser(addUserJObj);
        if (sCustomerIDs != null) {
            if (sCustomerIDs instanceof String) {
                CustomerInfoUtil.getInstance().addCustomersToUserMapping(userID, (String)sCustomerIDs);
            }
            else {
                CustomerInfoUtil.getInstance().addCustomersToUserMapping(userID, (List)sCustomerIDs);
            }
        }
        addOrUpdateAPIKeyForLoginId(loginID);
    }
    
    protected void addUserInDCOnPremiseTables(final Long loginID, final JSONObject addUserJObj) throws Exception {
        final String locale = addUserJObj.optString("USER_LOCALE");
        final String[] langCountry = locale.split("_");
        final String defaultTimeZoneID = SyMUtil.getDefaultTimeZoneID();
        final Long userID = getDCUserID(loginID);
        DMOnPremiseUserUtil.changeAAAProfile(userID, langCountry[0], langCountry[1], defaultTimeZoneID);
    }
    
    protected Long handleExistingUser(final JSONObject addUserJObj) throws Exception {
        Long loginID = null;
        final String userName = (String)addUserJObj.get("userName");
        final String domainName = addUserJObj.optString("domainName", "-");
        final String domainNameForLog = addUserJObj.optString("domainName", "Local");
        final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
        if (userName.equalsIgnoreCase("admin") && (domainName == null || domainName.equalsIgnoreCase("-") || domainName.equalsIgnoreCase("local")) && DMOnPremiseUserUtil.isDefaultAdminDisabled(defaultAdminUVHLoginID)) {
            if (DMOnPremiseUserUtil.unHideDefaultAdmin(defaultAdminUVHLoginID)) {
                this.modifyDefaultAdmin(addUserJObj);
                loginID = defaultAdminUVHLoginID;
            }
            return loginID;
        }
        CoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - addUser : UserExists");
        throw new SyMException(717, I18N.getMsg("dc.admin.uac.USER_EXISTS", new Object[] { domainNameForLog, userName }), "dc.admin.uac.USER_EXISTS", (Throwable)null);
    }
    
    private void modifyDefaultAdmin(JSONObject addUserJObj) {
        try {
            final String userName = addUserJObj.optString("userName");
            final String password = addUserJObj.optString("password");
            final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
            final Long accountProfileId = addUserJObj.getLong("accountprofile_id");
            final Long passwordProfileId = addUserJObj.getLong("passwordProfileId");
            final Long passwordPolicyId = addUserJObj.has("passwordPolicyId") ? Long.valueOf(addUserJObj.getLong("passwordPolicyId")) : null;
            addUserJObj.put("loginID", (Object)defaultAdminUVHLoginID);
            addUserJObj.put("roleChanged", (Object)"true");
            final Criteria userCriteria = new Criteria(Column.getColumn("AaaUser", "FIRST_NAME"), (Object)userName, 0);
            DataObject dataObject = this.getModifyUserDetailDO(userCriteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AaaContactInfo");
                final Long contactInfoID = (Long)row.get("CONTACTINFO_ID");
                addUserJObj.put("contactinfoID", (Object)contactInfoID);
            }
            final Row row = dataObject.getFirstRow("AaaAccount");
            row.set("ACCOUNTPROFILE_ID", (Object)accountProfileId);
            dataObject.updateRow(row);
            SyMUtil.getPersistence().update(dataObject);
            final Long accountId = (Long)row.get("ACCOUNT_ID");
            Criteria criteria = new Criteria(Column.getColumn("AaaAccPassword", "ACCOUNT_ID"), (Object)accountId, 0);
            dataObject = SyMUtil.getPersistence().get("AaaAccPassword", criteria);
            criteria = new Criteria(Column.getColumn("AaaPassword", "PASSWORD_ID"), dataObject.getFirstValue("AaaAccPassword", "PASSWORD_ID"), 0);
            final DataObject dataObj = SyMUtil.getPersistence().get("AaaPassword", criteria);
            final Row passwordRow = dataObj.getFirstRow("AaaPassword");
            passwordRow.set("PASSWDPROFILE_ID", (Object)passwordProfileId);
            if (passwordPolicyId != null) {
                passwordRow.set("PASSWDRULE_ID", (Object)passwordPolicyId);
            }
            dataObj.updateRow(passwordRow);
            final DCSDPRequestAPI sdpUserHandler = (DCSDPRequestAPI)Class.forName("com.me.dconpremise.webclient.sdp.util.DCRequestHandlerUtil").newInstance();
            addUserJObj = sdpUserHandler.addHiddenRoles(addUserJObj);
            this.modifyUserInUserTables(addUserJObj);
            SYMClientUtil.changePassword(userName, password);
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.WARNING, "Exception while modifying DefaultAdmin ", e);
        }
    }
    
    protected void modifyUserInUserTables(final JSONObject modifyUserJObj) throws Exception {
        try {
            SyMUtil.getUserTransaction().begin();
            new DMUserHandler().updateUser(modifyUserJObj);
            DMOnPremiseUserUtil.modifyUser(modifyUserJObj);
            this.updateUserInDCCoreTables(modifyUserJObj);
            this.updateUserInDCOnPremiseTables(modifyUserJObj);
            final List<String> classNames = UserOperationsHandler.getUserOperationsImplClassNames();
            for (final String className : classNames) {
                final UserOperationsInterface userOperation = (UserOperationsInterface)Class.forName(className).newInstance();
                userOperation.doModifyUserTableOperations(modifyUserJObj);
            }
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.INFO, "Exception while modify user in user tables", e);
            SyMUtil.getUserTransaction().rollback();
            throw e;
        }
    }
    
    protected void updateUserInDCCoreTables(final JSONObject modifyUserJObj) throws Exception {
        final Long loginID = (Long)modifyUserJObj.get("loginID");
        final String roleID = modifyUserJObj.optString("role_ID", (String)null);
        final Long oldRoleID = getRoleIdForUser(loginID);
        final String[] cgList = (String[])modifyUserJObj.opt("cgList");
        final Object sCustomerIDs = modifyUserJObj.opt("sCustomerIDs");
        ApiFactoryProvider.getUserManagementAPIHandler().updateUser(modifyUserJObj);
        if (modifyUserJObj.optString("roleChanged").equalsIgnoreCase("true")) {
            setRoleForUser(loginID, Long.valueOf(Long.parseLong(roleID)));
        }
        if (sCustomerIDs != null && !sCustomerIDs.equals("")) {
            final Long userID = getDCUserID(loginID);
            CustomerInfoUtil.getInstance().removeUserFromCustomerMapping(userID);
            if (sCustomerIDs instanceof String) {
                CustomerInfoUtil.getInstance().addCustomersToUserMapping(userID, (String)sCustomerIDs);
            }
            else {
                CustomerInfoUtil.getInstance().addCustomersToUserMapping(userID, (List)sCustomerIDs);
            }
        }
        addOrUpdateAPIKeyForLoginId(loginID);
        if (roleID != null && oldRoleID != Long.parseLong(roleID)) {
            final Criteria deleteDisplayOrderCri = new Criteria(new Column("HomePageSummaryDisplayOrder", "LOGIN_ID"), (Object)loginID, 0);
            SyMUtil.getPersistence().delete(deleteDisplayOrderCri);
        }
    }
    
    private void updateUserInDCOnPremiseTables(final JSONObject modifyUserJObj) throws Exception {
        final Long loginID = (Long)modifyUserJObj.get("loginID");
        final Long userid = getDCUserID(loginID);
        if (!modifyUserJObj.optString("USER_LOCALE").isEmpty()) {
            final String[] langCountry = ((String)modifyUserJObj.get("USER_LOCALE")).split("_");
            String seletedTimeZone = null;
            if (!modifyUserJObj.optString("PERSONALISE_TIME_ZONE").isEmpty()) {
                seletedTimeZone = (String)modifyUserJObj.get("PERSONALISE_TIME_ZONE");
            }
            if (seletedTimeZone == null || seletedTimeZone.isEmpty()) {
                seletedTimeZone = SyMUtil.getDefaultTimeZoneID();
            }
            DMOnPremiseUserUtil.changeAAAProfile(userid, langCountry[0], langCountry[1], seletedTimeZone);
        }
    }
    
    protected static void handleUserAddedListeners(final Long loginID, final JSONObject addUserJObj) throws Exception {
        final String userName = (String)addUserJObj.get("userName");
        final String roleID = (String)addUserJObj.get("role_ID");
        final Integer computerScope = addUserJObj.optInt("scope", 0);
        final UserEvent userEvent = new UserEvent();
        try {
            final JSONArray scopeListStr = (JSONArray)addUserJObj.get("scopeList");
            final ArrayList<Long> scopeList = new ArrayList<Long>();
            for (int i = 0; i < scopeListStr.length(); ++i) {
                final String scopeStr = scopeListStr.getString(i);
                final Long scope = Long.parseLong(scopeStr);
                scopeList.add(scope);
            }
            userEvent.scopeList = scopeList;
        }
        catch (final Exception exp) {
            CoreUserUtil.logger.log(Level.SEVERE, exp.getMessage());
        }
        userEvent.loginID = loginID;
        userEvent.userName = userName;
        userEvent.isAdminUser = isDefaultAdministratorRole(roleID);
        userEvent.isUserRoleChanged = Boolean.FALSE;
        userEvent.scope = computerScope;
        if (CustomerInfoUtil.getInstance().isMSP()) {
            final String[] custArray = addUserJObj.getString("sCustomerIDs").split(",");
            final List<Long> custList = new ArrayList<Long>();
            for (final String custId : custArray) {
                custList.add(Long.parseLong(custId));
            }
            userEvent.managedCustomers = custList;
        }
        UserListenerHandler.getInstance().invokeUsedAddedListeners(userEvent);
    }
    
    public void modifyUser(JSONObject modifyUserJObj) throws Exception {
        try {
            final Long loginID = (Long)modifyUserJObj.get("loginID");
            final String userName = (String)modifyUserJObj.get("userName");
            final String roleID = modifyUserJObj.optString("role_ID", (String)null);
            final Integer computerScope = modifyUserJObj.optInt("scope", 0);
            final Integer mdmScope = modifyUserJObj.optInt("mdmScope", 0);
            final String[] cgList = (String[])modifyUserJObj.opt("cgList");
            final String[] roList = (String[])modifyUserJObj.opt("roList");
            final String[] oldMappedArray = (String[])modifyUserJObj.opt("oldMappedList");
            final String[] newMappedArray = (String[])modifyUserJObj.opt("newMappedList");
            final String emailID = modifyUserJObj.has("USER_EMAIL_ID") ? modifyUserJObj.get("USER_EMAIL_ID").toString() : "";
            ArrayList<String> oldMappedList = new ArrayList<String>();
            if (oldMappedArray != null && oldMappedArray.length > 0) {
                oldMappedList = new ArrayList<String>(Arrays.asList(oldMappedArray));
            }
            ArrayList<String> newList = new ArrayList<String>();
            if (newMappedArray != null && newMappedArray.length > 0) {
                newList = new ArrayList<String>(Arrays.asList(newMappedArray));
            }
            final String domainNameForLog = modifyUserJObj.optString("domainName", "Local");
            final String computerScopeForLog = (computerScope == 0) ? "ALL_MANAGED_COMPUTERS" : ((computerScope == 1) ? "SCOPE_CG" : "SCOPE_RO");
            final String mdmScopeForLog = (mdmScope == 0) ? "ALL_MANAGED_MOBILE_DEVICES" : "SCOPE_CG";
            final boolean isPluginUser = modifyUserJObj.has("IS_PLUGIN_USER") && modifyUserJObj.getBoolean("IS_PLUGIN_USER");
            if (!isPluginUser && emailID != null && !emailID.isEmpty() && this.isEmailAlreadyExists(emailID, loginID)) {
                CoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - modifyUser : EmailExists");
                throw new SyMException(720, I18N.getMsg("dc.admin.uac.EMAIL_EXISTS", new Object[] { emailID }), "dc.admin.uac.EMAIL_EXISTS", (Throwable)null);
            }
            final String oldUserName = getDCUser(loginID);
            final Long oldRoleID = getRoleIdForUser(loginID);
            if (roleID != null) {
                modifyUserJObj.put("roleChanged", (Object)"true");
                final DCSDPRequestAPI sdpUserHandler = (DCSDPRequestAPI)Class.forName("com.me.dconpremise.webclient.sdp.util.DCRequestHandlerUtil").newInstance();
                modifyUserJObj = sdpUserHandler.addHiddenRoles(modifyUserJObj);
            }
            CoreUserUtil.logger.log(Level.INFO, "Going to modify " + oldUserName + " with Role id = " + oldRoleID + " mapped CG/RO resources id= " + oldMappedList);
            this.modifyUserInUserTables(modifyUserJObj);
            CoreUserUtil.logger.log(Level.INFO, oldUserName + " has been successfully modifed with username = " + userName + " Role id =" + roleID + " domain = " + domainNameForLog + " computerscope = " + computerScopeForLog + " MDM Scope=" + mdmScopeForLog + " mapping list CG/RO resources id= " + newList);
            this.updateSystemParams(modifyUserJObj);
            handleUserModifiedListeners(modifyUserJObj);
        }
        catch (final Exception ex) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - ModifyUser : ", ex);
            throw ex;
        }
    }
    
    protected static void handleUserModifiedListeners(final JSONObject modifyUserJObj) throws Exception {
        final Long loginID = (Long)modifyUserJObj.get("loginID");
        final Long oldUserID = getDCUserID(loginID);
        final Long oldRoleID = getRoleIdForUser(loginID);
        final String[] oldMappedArray = (String[])modifyUserJObj.opt("oldMappedList");
        ArrayList<String> oldMappedList = new ArrayList<String>();
        if (oldMappedArray != null && oldMappedArray.length > 0) {
            oldMappedList = new ArrayList<String>(Arrays.asList(oldMappedArray));
        }
        ArrayList<String> newList = new ArrayList<String>();
        final String[] cgList = (String[])modifyUserJObj.opt("cgList");
        final String[] roList = (String[])modifyUserJObj.opt("roList");
        if (cgList != null && cgList.length > 0) {
            newList = new ArrayList<String>(Arrays.asList(cgList));
        }
        else if (roList != null && roList.length > 0) {
            newList = new ArrayList<String>(Arrays.asList(roList));
        }
        final String userName = (String)modifyUserJObj.get("userName");
        final String roleID = modifyUserJObj.optString("role_ID", (String)null);
        final Integer computerScope = modifyUserJObj.optInt("scope", 0);
        final UserEvent userEvent = new UserEvent();
        final Long userid = getDCUserID(loginID);
        userEvent.loginID = loginID;
        userEvent.isPreviouslyManagingAllComputers = isUserManagingAllComputers(loginID);
        userEvent.userID = oldUserID;
        userEvent.isUserRoleChanged = ((roleID == null) ? Boolean.FALSE : ((oldRoleID != Long.parseLong(roleID)) ? Boolean.TRUE : Boolean.FALSE));
        userEvent.userName = userName;
        userEvent.scope = computerScope;
        userEvent.isAdminUser = isDefaultAdministratorRole((roleID == null) ? String.valueOf(oldRoleID) : roleID);
        userEvent.isUserScopeChanged = (oldMappedList.containsAll(newList) ? (newList.containsAll(oldMappedList) ? Boolean.FALSE : Boolean.TRUE) : Boolean.TRUE);
        final ArrayList<Long> scopeList = new ArrayList<Long>();
        for (final String scopeStr : newList) {
            final Long scope = Long.parseLong(scopeStr);
            scopeList.add(scope);
        }
        userEvent.scopeList = scopeList;
        if (CustomerInfoUtil.getInstance().isMSP()) {
            final String[] custArray = modifyUserJObj.getString("sCustomerIDs").split(",");
            final List<Long> custList = new ArrayList<Long>();
            for (final String custId : custArray) {
                custList.add(Long.parseLong(custId));
            }
            userEvent.managedCustomers = custList;
        }
        UserListenerHandler.getInstance().invokeuserModifiedListeners(userEvent);
    }
    
    public boolean deleteUser(final JSONObject deleteUserJObj, final HashMap userDelPII) throws Exception {
        boolean status = false;
        final Long loginID = (Long)deleteUserJObj.get("loginID");
        final boolean isPluginUser = (boolean)deleteUserJObj.get("isPluginUser");
        try {
            final Long roleID = getRoleIdForUser(Long.valueOf((long)loginID));
            final Long userID = getDCUserID(Long.valueOf((long)loginID));
            final HashMap userContactInfo = getUserContactProps(userID, userDelPII);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCAaaLogin"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("DCAaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final boolean bAssetExplorerEnabled = SolutionUtil.getInstance().isAEIntegrationMode();
            String appName = "HelpDesk";
            if (bAssetExplorerEnabled) {
                appName = "AssetExplorer";
            }
            final boolean fromSDP = deleteUserJObj.optBoolean("fromSDP", false);
            final Long sdpUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, appName);
            if (isPluginUser && dataObject.size("DCAAALOGIN") <= 0 && sdpUserStatus != null && sdpUserStatus != 1L) {
                CoreUserUtil.logger.log(Level.WARNING, "DELETE USER SDP == The user already created by DC. So we are not going to Delete this user from SDP : {0} ", loginID);
                return false;
            }
            final boolean bSdpEnabled = SolutionUtil.getInstance().isInvIntegrationMode();
            final boolean bSDPDeskEnabled = SolutionUtil.getInstance().isIntegrationMode();
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            Long buildno = null;
            try {
                final String sdpBuildNumber = SolutionUtil.getInstance().getSDPBuildNumber();
                buildno = Long.parseLong(sdpBuildNumber);
                CoreUserUtil.logger.log(Level.INFO, "build number for SDP", sdpBuildNumber);
            }
            catch (final Exception e) {
                CoreUserUtil.logger.log(Level.INFO, "Exception while getting sdp build no", e);
            }
            if (!isMSP && ((bSDPDeskEnabled && buildno != null && buildno > 11299L) || bAssetExplorerEnabled) && bSdpEnabled && ((!isPluginUser && dataObject.size("DCAAALOGIN") > 0) || sdpUserStatus == 2L) && !fromSDP) {
                final Long appID = SolutionUtil.getInstance().getIntegratedApplicationId(appName);
                final Properties properties = new Properties();
                properties.setProperty("applicationID", appID.toString());
                properties.setProperty("loginID", loginID.toString());
                properties.setProperty("status", "1");
                SolutionUtil.getInstance().addIntegratedServiceUser(properties);
                final DCSDPRequestAPI sdpUserHandler = (DCSDPRequestAPI)Class.forName("com.me.dconpremise.webclient.sdp.util.DCRequestHandlerUtil").newInstance();
                sdpUserHandler.changeSDPUser(loginID, "DISABLED", true);
                return true;
            }
            final List customersForUser = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userID);
            synchronized (CoreUserUtil.USER_HANDLING_LOCK) {
                status = this.deleteUserInUserTables(loginID, userID);
            }
            this.updateSystemParamsForDeleteUser();
            handleUserDeletedListeners(loginID, userID, roleID, userContactInfo, customersForUser);
        }
        catch (final Exception e2) {
            status = false;
            CoreUserUtil.logger.log(Level.SEVERE, "Error in deleting user", e2);
        }
        return status;
    }
    
    private boolean deleteUserInUserTables(final Long loginID, final Long userID) throws Exception {
        try {
            SyMUtil.getUserTransaction().begin();
            CoreUserUtil.logger.log(Level.INFO, " User delete : Trasaction begun");
            this.deleteUser(loginID, userID);
            CoreUserUtil.logger.log(Level.INFO, " User delete : AAALOGIN deleted");
            final Criteria twoFactorCriteria = new Criteria(new Column("AaaUserTwoFactorDetails", "USER_ID"), (Object)userID, 0);
            SyMUtil.getPersistence().delete(twoFactorCriteria);
            CoreUserUtil.logger.log(Level.INFO, " User delete : Twofactorauthentication deleted");
            updateDeletedUserContact(userID, loginID);
            CoreUserUtil.logger.log(Level.INFO, " User delete : User contact details deleted");
            this.removeUserLinkDetails(userID);
            CoreUserUtil.logger.log(Level.INFO, " User delete : User link details deleted");
            final List<String> classNames = UserOperationsHandler.getUserOperationsImplClassNames();
            for (final String className : classNames) {
                final UserOperationsInterface userOperation = (UserOperationsInterface)Class.forName(className).newInstance();
                userOperation.doDeleteUserTableOperations(loginID);
            }
            SyMUtil.getUserTransaction().commit();
            DeletionFramework.transactionCommitted();
            CoreUserUtil.logger.log(Level.INFO, " User delete : Trasaction commit");
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.INFO, "Exception while delete user in user tables", e);
            SyMUtil.getUserTransaction().rollback();
            DeletionFramework.transactionRollback();
            throw e;
        }
        return true;
    }
    
    public static void updateDeletedUserContact(final Long userID, final Long loginID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUserContactInfo"));
            sq.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("AaaUserContactInfo", "USER_ID"), (Object)userID, 0));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
                if (loginID != null && loginID.equals(defaultAdminUVHLoginID)) {
                    final Row userRow = dataObject.getFirstRow("AaaContactInfo");
                    userRow.set("EMAILID", (Object)"");
                    userRow.set("LANDLINE", (Object)"");
                    dataObject.updateRow(userRow);
                    DataAccess.update(dataObject);
                    CoreUserUtil.logger.log(Level.INFO, "AAAContactInfo Table IS updated for the user :" + userID);
                }
                else {
                    final Row userContactRow = dataObject.getFirstRow("AaaContactInfo");
                    dataObject.deleteRow(userContactRow);
                    DataAccess.update(dataObject);
                    CoreUserUtil.logger.log(Level.INFO, "AAAUserContactInfo Table IS deteled for the user :" + userID);
                }
            }
            else {
                CoreUserUtil.logger.log(Level.WARNING, "AAAUserContactInfo Table IS Empty for the user :" + userID);
            }
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.WARNING, "Exception while deleting the APIKey details for SDP");
        }
    }
    
    public void removeUserLinkDetails(final Long userId) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaUserLinkDetails");
        final Criteria criteria = new Criteria(Column.getColumn("AaaUserLinkDetails", "USER_ID"), (Object)userId, 0);
        deleteQuery.setCriteria(criteria);
        SyMUtil.getPersistence().delete(deleteQuery);
    }
    
    private void updateSystemParamsForDeleteUser() {
        try {
            final String defaultDomain = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
            if (defaultDomain != null && !defaultDomain.equals("dcLocal")) {
                final SelectQuery defaultDomainUsersSelect = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
                defaultDomainUsersSelect.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
                defaultDomainUsersSelect.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
                final Criteria defaultDomainCri = new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)defaultDomain, 0);
                defaultDomainUsersSelect.setCriteria(defaultDomainCri);
                final DataObject defaultDomainDataObject = SyMUtil.getPersistence().get(defaultDomainUsersSelect);
                if (defaultDomainDataObject != null) {
                    final Iterator defaultDomainUsers = defaultDomainDataObject.getRows("AaaLogin");
                    if (defaultDomainUsers == null || !defaultDomainUsers.hasNext()) {
                        SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", "dcLocal");
                    }
                }
                else {
                    SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", "dcLocal");
                }
            }
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.SEVERE, "Expection updating system params in delete user", e);
        }
    }
    
    protected static void handleUserDeletedListeners(final Long loginID, final Long userID, final Long roleID, final HashMap userContactInfo, final List customersForUser) throws Exception {
        final UserEvent userEvent = new UserEvent();
        userEvent.loginID = loginID;
        userEvent.userID = userID;
        userEvent.isAdminUser = isDefaultAdministratorRole(roleID);
        userEvent.userContactInfo = userContactInfo;
        userEvent.managedCustomers = customersForUser;
        UserListenerHandler.getInstance().invokeuserDeletedListeners(userEvent);
    }
    
    public static ArrayList<String> getCustomerMappingDetailsInString(final Long loginID) {
        final ArrayList<String> customerIdsList = new ArrayList<String>();
        try {
            final ArrayList<Long> customerIdsListForUser = new ArrayList<Long>();
            final Long userId = getDCUserID(loginID);
            final List<HashMap> customerList = CustomerInfoUtil.getInstance().getCustomerDetailsForUser(userId);
            for (int i = 0; i < customerList.size(); ++i) {
                customerIdsListForUser.add(customerList.get(i).get("CUSTOMER_ID"));
            }
            for (final Long customerId : customerIdsListForUser) {
                customerIdsList.add(customerId + "");
            }
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception while getting the customers mapped with user:", e);
        }
        return customerIdsList;
    }
    
    public DataObject addOrUpdateUserStatus(DataObject addUserDO, final JSONObject userJObj) throws Exception {
        final boolean newUser = userJObj.optBoolean("newUser");
        if (userJObj.has("USER_EMAIL_ID")) {
            userJObj.put("userEmail", userJObj.get("USER_EMAIL_ID"));
        }
        this.addOrUpdateUserLinkDetailsRow(addUserDO, userJObj);
        final Row aaaUserLinkDetails = addUserDO.getRow("AaaUserLinkDetails");
        final Row aaaUser = addUserDO.getRow("AaaUser");
        final boolean mailSentStatus = userJObj.getBoolean("mailSent");
        final Integer status = newUser ? Integer.valueOf(1) : null;
        Integer remarks;
        if (mailSentStatus) {
            remarks = (newUser ? 3 : 5);
        }
        else {
            remarks = (newUser ? 2 : 6);
            final String meTrackKey = newUser ? "newUserMailServerFailure" : "resetPasswordMailServerFailure";
            METrackerUtil.incrementMETrackParams(meTrackKey);
        }
        this.addOrUpdateAccountStatusExtnRow(addUserDO, status, remarks);
        addUserDO = SyMUtil.getPersistence().update(addUserDO);
        CoreUserUtil.logger.log(Level.INFO, "Created the token for the user {0} and mail sent status {1}", new Object[] { aaaUser.get("USER_ID"), mailSentStatus });
        return addUserDO;
    }
    
    public DataObject addOrUpdateUserLinkDetailsRow(final DataObject addUserDO, final JSONObject userJObj) throws Exception {
        final Boolean newUser = userJObj.optBoolean("newUser");
        final Row aaaUser = addUserDO.getRow("AaaUser");
        final Long userId = (Long)aaaUser.get("USER_ID");
        final Long currentTime = System.currentTimeMillis();
        final Long expiryTime = newUser ? (86400000L + currentTime) : (1800000L + currentTime);
        final Integer tokenType = newUser ? 101 : 102;
        String token = UUID.randomUUID().toString();
        if (userJObj.has("token")) {
            token = userJObj.getString("token");
        }
        final JSONObject alertJSON = new JSONObject();
        Row aaaUserLinkDetails = addUserDO.getRow("AaaUserLinkDetails");
        if (aaaUserLinkDetails == null) {
            aaaUserLinkDetails = new Row("AaaUserLinkDetails");
            aaaUserLinkDetails.set("USER_ID", (Object)userId);
            aaaUserLinkDetails.set("TOKEN", (Object)token);
            aaaUserLinkDetails.set("CREATED_TIME", (Object)currentTime);
            aaaUserLinkDetails.set("EXPIRY_TIME", (Object)expiryTime);
            aaaUserLinkDetails.set("TOKEN_TYPE", (Object)tokenType);
            addUserDO.addRow(aaaUserLinkDetails);
        }
        else {
            final Long existingExpiryTime = aaaUserLinkDetails.getLong("EXPIRY_TIME");
            alertJSON.put("linkExpired", existingExpiryTime < currentTime);
            aaaUserLinkDetails.set("TOKEN", (Object)token);
            aaaUserLinkDetails.set("CREATED_TIME", (Object)currentTime);
            aaaUserLinkDetails.set("TOKEN_TYPE", (Object)tokenType);
            aaaUserLinkDetails.set("EXPIRY_TIME", (Object)expiryTime);
            addUserDO.updateRow(aaaUserLinkDetails);
        }
        userJObj.put("token", (Object)token);
        alertJSON.put("technicianID", userJObj.get("technicianID"));
        alertJSON.put("userID", (Object)userId);
        alertJSON.put("linkExpiry", (Object)expiryTime);
        alertJSON.put("domainName", userJObj.get("domainName"));
        alertJSON.put("userEmail", userJObj.get("userEmail"));
        if (newUser) {
            alertJSON.put("newPasswordLink", (Object)token);
            alertJSON.put("linkExpiry", 24);
            if (userJObj.has("adminEmail")) {
                alertJSON.put("adminEmail", userJObj.get("adminEmail"));
            }
            final Map alertResponse = this.sendAccountActivationMail(alertJSON);
            userJObj.put("mailSent", alertResponse.get("status").equals("success"));
        }
        else {
            METrackerUtil.incrementMETrackParams("resetPasswordInitiated");
            alertJSON.put("linkExpiry", 30);
            alertJSON.put("resetPasswordLink", (Object)token);
            Long eventCode;
            if (userJObj.has("adminEmail")) {
                alertJSON.put("adminEmail", userJObj.get("adminEmail"));
                eventCode = UserConstants.UserAlertConstant.CHANGE_PASSWORD_FOR_TECH;
            }
            else if (alertJSON.optBoolean("linkExpired")) {
                eventCode = UserConstants.UserAlertConstant.RESET_PASSWORD_ON_EXPIRY;
            }
            else {
                eventCode = UserConstants.UserAlertConstant.USER_ACCOUNT_PASSWORD_RESET;
            }
            alertJSON.put("eventCode", (Object)eventCode);
            final Map alertResponse2 = this.sendAccountPasswordResetMail(alertJSON);
            userJObj.put("mailSent", alertResponse2.get("status").equals("success"));
        }
        return addUserDO;
    }
    
    public DataObject getModifyUserDetailDO(final Criteria userCriteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaContactInfo"));
        selectQuery.addJoin(new Join("AaaContactInfo", "AaaUserContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
        selectQuery.setCriteria(userCriteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public void updateContactInfo(final Long contactInfoId, final JSONObject userDetails) throws DataAccessException {
        final String emailID = userDetails.optString("USER_EMAIL_ID");
        final String phNum = userDetails.optString("USER_PHONE_NUM");
        final Criteria criteria = new Criteria(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"), (Object)contactInfoId, 0);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaContactInfo");
        if (emailID != null && !emailID.isEmpty()) {
            updateQuery.setUpdateColumn("EMAILID", (Object)emailID);
        }
        if (phNum != null && !phNum.isEmpty()) {
            updateQuery.setUpdateColumn("LANDLINE", (Object)phNum);
        }
        updateQuery.setCriteria(criteria);
        SyMUtil.getPersistence().update(updateQuery);
    }
    
    public DataObject getContactDOFromMail(final Criteria mailCriteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        selectQuery.addJoin(new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaAccount", "AaaAccountStatusExtn", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUser", "AaaUserLinkDetails", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        Criteria criteria = mailCriteria;
        criteria = criteria.and(new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "MOBILE"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "DOMAINNAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatusExtn", "ACCOUNT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatusExtn", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "*"));
        return SyMUtil.getPersistence().get(selectQuery);
    }
    
    public DataObject getTokenDetails(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUserLinkDetails"));
        selectQuery.addJoin(new Join("AaaUserLinkDetails", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "TOKEN"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "TOKEN_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "EXPIRY_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserLinkDetails", "CREATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "DOMAINNAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "MOBILE"));
        final DataObject tokenDO = SyMUtil.getPersistence().get(selectQuery);
        return tokenDO;
    }
    
    public HashMap sendAccountActivationMail(final JSONObject alertJSON) throws Exception {
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final Long technicianId = alertJSON.getLong("technicianID");
            final String token = alertJSON.getString("newPasswordLink");
            final String baseURLStr = SyMUtil.getServerBaseUrlForMail();
            final Properties alertProps = new Properties();
            ((Hashtable<String, String>)alertProps).put("$product_name$", ProductUrlLoader.getInstance().getValue("productname"));
            ((Hashtable<String, String>)alertProps).put("$user_name$", DMUserHandler.getUserNameFromUserID(Long.valueOf(alertJSON.getLong("userID"))));
            ((Hashtable<String, String>)alertProps).put("$admin_email$", alertJSON.getString("adminEmail"));
            ((Hashtable<String, Long>)alertProps).put("$link_expiry_time$", alertJSON.getLong("linkExpiry"));
            ((Hashtable<String, String>)alertProps).put("$account_created$", I18N.getMsg("ems.admin.alerts.account_created", new Object[0]));
            String domainName = alertJSON.optString("domainName");
            new UserController();
            final TreeMap domainList = UserController.getADDomainNamesForLoginPage();
            final Boolean isADUser = domainList != null;
            Long alertConstant;
            String createUserLink;
            if (isADUser) {
                if (domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) {
                    domainName = "Local Authentication";
                    alertConstant = UserConstants.UserAlertConstant.NEW_USER_ACCOUNT_ACTIVATION;
                    createUserLink = baseURLStr + "/client#/login/create-password?userToken=" + token;
                }
                else {
                    alertConstant = UserConstants.UserAlertConstant.AD_USER_ACCOUNT_ACTIVATION;
                    createUserLink = baseURLStr + "/client#/login/activate?userToken=" + token;
                }
                ((Hashtable<String, String>)alertProps).put("$domain_name$", domainName);
            }
            else {
                alertConstant = UserConstants.UserAlertConstant.USER_ACCOUNT_ACTIVATION_WITHOUT_DOMAIN;
                createUserLink = baseURLStr + "/client#/login/create-password?userToken=" + token;
            }
            ((Hashtable<String, String>)alertProps).put("$create_password_link$", createUserLink);
            final HashMap responseMap = this.sendAlertMail(alertConstant, customerId, technicianId, alertJSON.getString("userEmail"), alertProps);
            return responseMap;
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception while sending user activation mail", e);
            throw e;
        }
    }
    
    public HashMap sendAccountPasswordResetMail(final JSONObject alertJSON) throws Exception {
        try {
            final Long customerId = alertJSON.optLong("customerID", -1L);
            final Long technicianId = alertJSON.getLong("technicianID");
            final String token = alertJSON.optString("resetPasswordLink");
            final String baseURLStr = SyMUtil.getServerBaseUrlForMail();
            final Properties alertProps = new Properties();
            ((Hashtable<String, String>)alertProps).put("$product_name$", ProductUrlLoader.getInstance().getValue("productname"));
            ((Hashtable<String, String>)alertProps).put("$user_name$", DMUserHandler.getUserNameFromUserID(Long.valueOf(alertJSON.getLong("userID"))));
            ((Hashtable<String, String>)alertProps).put("$password_reset$", I18N.getMsg("ems.admin.alerts.password_reset", new Object[0]));
            Long eventCode = alertJSON.optLong("eventCode");
            String resetPasswordLink = "";
            if (token.length() > 0) {
                resetPasswordLink = baseURLStr + "/client#/login/reset-password?userToken=" + token;
            }
            HashMap responseMap = new HashMap();
            if (eventCode.equals(UserConstants.UserAlertConstant.USER_ACCOUNT_PASSWORD_RESET)) {
                ((Hashtable<String, String>)alertProps).put("$reset_password_link$", resetPasswordLink);
                ((Hashtable<String, Long>)alertProps).put("$link_expiry_time$", alertJSON.getLong("linkExpiry"));
                eventCode = UserConstants.UserAlertConstant.USER_ACCOUNT_PASSWORD_RESET;
            }
            else if (eventCode.equals(UserConstants.UserAlertConstant.RESET_PASSWORD_ON_EXPIRY)) {
                ((Hashtable<String, String>)alertProps).put("$reset_password_link$", resetPasswordLink);
                ((Hashtable<String, Long>)alertProps).put("$link_expiry_time$", alertJSON.getLong("linkExpiry"));
                eventCode = UserConstants.UserAlertConstant.RESET_PASSWORD_ON_EXPIRY;
            }
            else if (eventCode.equals(UserConstants.UserAlertConstant.THIRD_PARTY_RESET_PASSWORD)) {
                eventCode = UserConstants.UserAlertConstant.THIRD_PARTY_RESET_PASSWORD;
            }
            else if (eventCode.equals(UserConstants.UserAlertConstant.CHANGE_PASSWORD_FOR_TECH)) {
                ((Hashtable<String, String>)alertProps).put("$admin_email$", alertJSON.getString("adminEmail"));
                ((Hashtable<String, String>)alertProps).put("$reset_password_link$", resetPasswordLink);
                ((Hashtable<String, Long>)alertProps).put("$link_expiry_time$", alertJSON.getLong("linkExpiry"));
                ((Hashtable<String, String>)alertProps).put("$change_password$", I18N.getMsg("ems.admin.alerts.change_password", new Object[0]));
                ((Hashtable<String, String>)alertProps).put("$account$", I18N.getMsg("ems.admin.alerts.account", new Object[0]));
                eventCode = UserConstants.UserAlertConstant.CHANGE_PASSWORD_FOR_TECH;
            }
            responseMap = this.sendAlertMail(eventCode, customerId, technicianId, alertJSON.getString("userEmail"), alertProps);
            return responseMap;
        }
        catch (final Exception e) {
            CoreUserUtil.logger.log(Level.SEVERE, "Exception while sending reset password mail", e);
            throw e;
        }
    }
    
    public HashMap sendAlertMail(final Long eventCode, final Long customerId, final Long technicianID, final String email, final Properties alertProps) {
        final TemplatesUtil templatesUtil = new TemplatesUtil();
        HashMap alertResponse = new HashMap();
        try {
            final AlertDetails alertDetails = new AlertDetails(eventCode, customerId, technicianID, false);
            alertDetails.alertProps = alertProps;
            alertDetails.mediumID = templatesUtil.getMediumIdByName("EMAIL");
            final EmailAlertsHandler emailAlertsHandler = new EmailAlertsHandler();
            final HashMap alertData = emailAlertsHandler.constructAlertData(alertDetails);
            final HashMap<String, String> mailImages = new HashMap<String, String>();
            mailImages.put("OrgLogo", CustomerInfoUtil.getInstance().getLogoPath(customerId));
            final JSONObject inlineObj = new JSONObject();
            inlineObj.put("InlineImages", (Map)mailImages);
            alertData.put("additionalParams", inlineObj);
            alertData.put("quickSend", true);
            DMSecurityLogger.info(CoreUserUtil.logger, CoreUserUtil.class.getName(), "sendAlertMail", "sending mail alert with data: {0}", (Object)alertData);
            alertResponse = emailAlertsHandler.sendAlert((Object)email, alertData);
            CoreUserUtil.logger.log(Level.INFO, "Alert sent for AlertType {0}", eventCode);
        }
        catch (final Exception ex) {
            CoreUserUtil.logger.log(Level.WARNING, "Exception while sending mail for Alert type = " + eventCode, ex);
            alertResponse.put("status", "error");
        }
        return alertResponse;
    }
    
    public static Map getCareNumbersUIListJson() {
        final Map careJson = new HashMap();
        try {
            String countryCode = CountryProvider.getInstance().countryCodeFromDefaultTimeZoneID();
            countryCode = countryCode.toUpperCase();
            CoreUserUtil.logger.log(Level.INFO, "Country code received for conclude initial care number : " + countryCode);
            final String defaultTollFree = (String)FrameworkConfigurations.getSpecificPropertyIfExists("CARE_NUMBERS_PROPERTIES", "DefaultTollFree", (Object)null);
            final JSONObject tollFreeNumbers = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("CARE_NUMBERS_PROPERTIES", "TollFree", (Object)null);
            final JSONObject telephoneNumbers = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("CARE_NUMBERS_PROPERTIES", "Telephone", (Object)null);
            final JSONObject didNumbers = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("CARE_NUMBERS_PROPERTIES", "DID", (Object)null);
            Object initialNumber = "";
            String initialNumberCountry = countryCode;
            if (telephoneNumbers.has(countryCode)) {
                initialNumber = telephoneNumbers.get(countryCode);
            }
            else if (tollFreeNumbers.has(countryCode)) {
                initialNumber = tollFreeNumbers.get(countryCode);
            }
            else {
                initialNumber = tollFreeNumbers.get(defaultTollFree);
                initialNumberCountry = defaultTollFree;
            }
            if (initialNumber instanceof JSONArray) {
                initialNumber = ((JSONArray)initialNumber).get(0).toString();
            }
            careJson.put("initialCareNumber", initialNumber.toString());
            careJson.put("initialCareCountry", initialNumberCountry);
            careJson.put("TollFree", tollFreeNumbers.toMap());
            careJson.put("Telephone", telephoneNumbers.toMap());
            careJson.put("DID", didNumbers.toMap());
        }
        catch (final Exception e) {
            careJson.put("initialCareNumber", "+1 888 720 9500");
            careJson.put("initialCareCountry", "US");
            CoreUserUtil.logger.log(Level.WARNING, "Exception occurred while concat care numbers json : ", e);
        }
        return careJson;
    }
}
