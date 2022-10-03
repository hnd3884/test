package com.me.ems.onpremise.uac.summaryserver.probe.util;

import java.util.HashMap;
import java.util.Arrays;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Date;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.ProbeUsersUtil;
import com.me.ems.onpremise.uac.core.UserOperationsInterface;
import com.me.ems.framework.uac.handler.UserOperationsHandler;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.probe.PSOnPremiseUserUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.List;
import java.util.Collection;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.authentication.util.AuthUtil;
import com.me.ems.onpremise.uac.summaryserver.common.util.UserServiceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.ems.onpremise.uac.core.CoreUserUtil;

public class PSCoreUserUtil extends CoreUserUtil
{
    public Long addUser(final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws Exception {
        Long loginID = probeHandlerObject.optLong("loginID");
        final Long userID = probeHandlerObject.optLong("userID");
        try {
            final String userName = (String)addUserJObj.get("userName");
            final String roleID = (String)addUserJObj.get("role_ID");
            final String domainName = addUserJObj.optString("domainName", "-");
            final JSONArray staticCustomGroups = (JSONArray)addUserJObj.opt("Static_Custom_Groups");
            final JSONArray mdmDeviceGroups = (JSONArray)addUserJObj.opt("Mdm_Device_Groups");
            final String[] roList = (String[])addUserJObj.opt("roList");
            final List<String> newList = UserServiceUtil.getCombinedScopeList(staticCustomGroups, mdmDeviceGroups, roList);
            final Integer computerScope = addUserJObj.optInt("scope", 0);
            final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
            final String domainNameForLog = addUserJObj.optString("domainName", "Local");
            final String computerScopeForLog = (computerScope == 0) ? "ALL_MANAGED_COMPUTERS" : ((computerScope == 1) ? "SCOPE_CG" : "SCOPE_RO");
            final String mdmScopeForLog = (mdmScope == 0) ? "ALL_MANAGED_MOBILE_DEVICES" : "SCOPE_CG";
            final String emailID = addUserJObj.has("USER_EMAIL_ID") ? addUserJObj.get("USER_EMAIL_ID").toString() : "";
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
                PSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - addUser : EmailExists");
                throw new SyMException(720, I18N.getMsg("ems.admin.admin.email_already_exists", new Object[] { emailID }), "ems.admin.admin.email_already_exists", (Throwable)null);
            }
            if (!isUserAccountAvailable(userName, domainName)) {
                PSCoreUserUtil.logger.log(Level.INFO, "In addUser of DCUserHandler...Going to add  user " + userName + " in domain = " + domainNameForLog + " with Role id = " + roleID + " computerscope " + computerScopeForLog + " MDM Scope " + mdmScopeForLog);
                addUserJObj.put("service_id", (Object)AuthUtil.getServiceId("System"));
                this.deleteIfPreviousUser(userID);
                loginID = this.addUserInUserTablesWOPassword(addUserJObj, probeHandlerObject);
                PSCoreUserUtil.logger.log(Level.INFO, "User " + userName + " added in domain " + domainNameForLog + " with Role id = " + roleID + " computerscope = " + computerScopeForLog + " MDM Scope = " + mdmScopeForLog + " mapping list CG/RO resources id= " + newList);
                this.updateSystemParams(addUserJObj);
            }
            else {
                loginID = this.handleExistingUser(addUserJObj);
            }
            addUserJObj.put("scopeList", (Collection)newList);
            CoreUserUtil.handleUserAddedListeners(loginID, addUserJObj);
        }
        catch (final SyMException symEx) {
            PSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - userAdded : ", (Throwable)symEx);
            throw symEx;
        }
        catch (final Exception ex) {
            PSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - userAdded : ", ex);
            throw ex;
        }
        return loginID;
    }
    
    protected Long addUserInUserTablesWOPassword(final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws Exception {
        Long loginID = null;
        try {
            SyMUtil.getUserTransaction().begin();
            final DataObject addUserDO = SyMUtil.getPersistence().constructDataObject();
            this.addPSUser(addUserDO, addUserJObj, probeHandlerObject);
            loginID = PSOnPremiseUserUtil.addPSUserWithoutPassword(addUserDO, addUserJObj, probeHandlerObject);
            addUserJObj.put("loginID", (Object)loginID);
            this.addUserInDCCoreTables(loginID, addUserJObj);
            this.addUserInDCOnPremiseTables(loginID, addUserJObj);
            addUserJObj.put("newUser", true);
            if (probeHandlerObject.has("token")) {
                addUserJObj.put("token", probeHandlerObject.get("token"));
            }
            this.addOrUpdateUserStatus(addUserDO, addUserJObj);
            final List<String> classNames = UserOperationsHandler.getUserOperationsImplClassNames();
            for (final String className : classNames) {
                final UserOperationsInterface userOperation = (UserOperationsInterface)Class.forName(className).newInstance();
                userOperation.doAddUserTableOperations(addUserJObj);
            }
            final String[] probeList = (String[])addUserJObj.opt("probeList");
            ProbeUsersUtil.addProbeUsers(loginID, probeList);
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            PSCoreUserUtil.logger.log(Level.INFO, "Exception while adding user", e);
            SyMUtil.getUserTransaction().rollback();
            throw e;
        }
        return loginID;
    }
    
    public void addPSUser(final DataObject addUserDO, final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws DataAccessException {
        final String userName = (String)addUserJObj.get("userName");
        final String loginName = (String)addUserJObj.get("loginName");
        final String emailID = addUserJObj.optString("USER_EMAIL_ID");
        final String phNum = addUserJObj.optString("USER_PH_NO");
        final String domainName = addUserJObj.optString("domainName", "-");
        final String description = addUserJObj.optString("description", (String)null);
        final Long serviceId = (Long)addUserJObj.get("service_id");
        final Long accountProfileId = (Long)addUserJObj.get("accountprofile_id");
        final JSONArray roleIdsArray = (JSONArray)addUserJObj.opt("roleIdsList");
        final List<Long> roleIdsList = getIdsFromJsonArray(roleIdsArray);
        final Object summaryGroupID = addUserJObj.get("summaryGroupID");
        final Long userID = probeHandlerObject.getLong("userID");
        final Long loginID = probeHandlerObject.getLong("loginID");
        final Long accountID = probeHandlerObject.getLong("accountID");
        final Long contactInfoID = probeHandlerObject.getLong("contactInfoID");
        this.addPSUserInAAAUser(addUserDO, userName, description, userID);
        this.addPSUserInAAALogin(addUserDO, loginName, domainName, userID, loginID);
        this.addPSUserInAAAAccount(addUserDO, serviceId, accountProfileId, accountID, loginID);
        this.addPSUserInAAAAuthorizedRole(addUserDO, roleIdsList, accountID);
        this.addPSUserInAAAContactInfo(addUserDO, emailID, phNum, contactInfoID);
        this.addPSUserInAAAUserContactInfo(addUserDO, userID, contactInfoID);
        this.addPSUserInUserSummaryMapping(addUserDO, summaryGroupID, loginID);
    }
    
    public void addPSUserInAAAUser(final DataObject newUser, final String username, final String description, final Long userID) throws DataAccessException {
        final Row users = new Row("AaaUser");
        users.set("USER_ID", (Object)userID);
        users.set("FIRST_NAME", (Object)username);
        users.set("CREATEDTIME", (Object)new Date().getTime());
        if (description != null) {
            users.set("DESCRIPTION", (Object)description);
        }
        newUser.addRow(users);
    }
    
    public void addPSUserInAAALogin(final DataObject newUser, final String loginName, String domainName, final Long userID, final Long loginID) throws DataAccessException {
        final Row login = new Row("AaaLogin");
        login.set("NAME", (Object)loginName);
        login.set("USER_ID", (Object)userID);
        login.set("LOGIN_ID", (Object)loginID);
        domainName = ((domainName != null) ? domainName.toLowerCase() : "-");
        login.set("DOMAINNAME", (Object)domainName);
        newUser.addRow(login);
    }
    
    public void addPSUserInAAAAccount(final DataObject newUser, final Long serviceId, final Long accountProfileId, final Long accountId, final long loginId) throws DataAccessException {
        final Row accRow = new Row("AaaAccount");
        accRow.set("ACCOUNT_ID", (Object)accountId);
        accRow.set("LOGIN_ID", (Object)loginId);
        accRow.set("SERVICE_ID", (Object)serviceId);
        accRow.set("ACCOUNTPROFILE_ID", (Object)accountProfileId);
        accRow.set("CREATEDTIME", (Object)System.currentTimeMillis());
        newUser.addRow(accRow);
    }
    
    public void addPSUserInAAAAuthorizedRole(final DataObject newUser, final List<Long> roleIdsList, final Long accountID) throws DataAccessException {
        for (final Long roleID : roleIdsList) {
            final Row accAuthRow = new Row("AaaAuthorizedRole");
            accAuthRow.set("ACCOUNT_ID", (Object)accountID);
            accAuthRow.set("ROLE_ID", (Object)roleID);
            newUser.addRow(accAuthRow);
        }
    }
    
    public void addPSUserInAAAContactInfo(final DataObject newUser, final String emailID, final String phNum, final Long contactInfoID) throws DataAccessException {
        final Row accContactInfoRow = new Row("AaaContactInfo");
        accContactInfoRow.set("CONTACTINFO_ID", (Object)contactInfoID);
        accContactInfoRow.set("EMAILID", (Object)emailID);
        accContactInfoRow.set("LANDLINE", (Object)phNum);
        newUser.addRow(accContactInfoRow);
    }
    
    public void addPSUserInAAAUserContactInfo(final DataObject newUser, final Long userID, final Long contactInfoID) throws DataAccessException {
        final Row accUserContactInfoRow = new Row("AaaUserContactInfo");
        accUserContactInfoRow.set("USER_ID", (Object)userID);
        accUserContactInfoRow.set("CONTACTINFO_ID", (Object)contactInfoID);
        newUser.addRow(accUserContactInfoRow);
    }
    
    public DataObject addPSUserInUserSummaryMapping(final DataObject newUser, final Object summaryGroupID, final Long loginID) throws DataAccessException {
        final Row userSummaryMappingRow = new Row("UserSummaryMapping");
        userSummaryMappingRow.set("SUMMARYGROUP_ID", summaryGroupID);
        userSummaryMappingRow.set("LOGIN_ID", (Object)loginID);
        newUser.addRow(userSummaryMappingRow);
        return newUser;
    }
    
    @Override
    public void addUserInDCCoreTables(final Long loginID, final JSONObject addUserJObj) throws Exception {
        final String roleID = (String)addUserJObj.get("role_ID");
        final String[] cgList = (String[])addUserJObj.opt("cgList");
        final Object sCustomerIDs = addUserJObj.opt("sCustomerIDs");
        final Long userID = getDCUserID(loginID);
        setRoleForUser(loginID, Long.valueOf(Long.parseLong(roleID)));
        this.processDCScopeofUser(addUserJObj);
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
    
    private void processDCScopeofUser(final JSONObject jsonObject) throws Exception {
        final ArrayList<String> cgList = this.processDCCGListofUser(jsonObject);
        jsonObject.put("cgList", (Object)cgList.toArray(new String[0]));
        ApiFactoryProvider.getUserManagementAPIHandler().addUser(jsonObject);
    }
    
    public ArrayList processDCCGListofUser(final JSONObject jsonObject) throws Exception {
        final JSONArray staticCustomGroups = (JSONArray)jsonObject.opt("Static_Custom_Groups");
        final JSONArray mdmDeviceGroups = (JSONArray)jsonObject.opt("Mdm_Device_Groups");
        final ArrayList<String> list = new ArrayList<String>();
        if (staticCustomGroups != null && staticCustomGroups.length() > 0) {
            for (int index = 0; index < staticCustomGroups.length(); ++index) {
                final JSONObject cgData = staticCustomGroups.getJSONObject(index);
                list.add(Long.toString(cgData.getLong("groupId")));
            }
        }
        if (mdmDeviceGroups != null && mdmDeviceGroups.length() > 0) {
            for (int index = 0; index < mdmDeviceGroups.length(); ++index) {
                final JSONObject cgData = mdmDeviceGroups.getJSONObject(index);
                list.add(Long.toString(cgData.getLong("group_id")));
            }
        }
        return list;
    }
    
    private void deleteIfPreviousUser(final Long userID) throws DataAccessException {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaUser");
            final Criteria deleteCriteria = new Criteria(new Column("AaaUser", "USER_ID"), (Object)userID, 0);
            deleteQuery.setCriteria(deleteCriteria);
            DataAccess.delete(deleteQuery);
        }
        catch (final DataAccessException dataAccessException) {
            PSCoreUserUtil.logger.log(Level.SEVERE, "Exception occurred in PSUserHandler.deleteIfPreviousUser()");
            throw dataAccessException;
        }
    }
    
    @Override
    public void modifyUser(final JSONObject modifyUserJObj) throws Exception {
        PSCoreUserUtil.logger.log(Level.INFO, "UserMgmt: PSUserHandler.modifyUser() method begins");
        try {
            final Long loginID = (Long)modifyUserJObj.get("loginID");
            final String userName = (String)modifyUserJObj.get("userName");
            final String roleID = modifyUserJObj.optString("role_ID", (String)null);
            final int computerScope = modifyUserJObj.optInt("scope", 0);
            final int mdmScope = modifyUserJObj.optInt("mdmScope", 0);
            final JSONArray staticCustomGroups = (JSONArray)modifyUserJObj.opt("Static_Custom_Groups");
            final JSONArray mdmDeviceGroups = (JSONArray)modifyUserJObj.opt("Mdm_Device_Groups");
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
            final boolean isPluginUser = modifyUserJObj.has("IS_PLUGIN_USER") ? modifyUserJObj.getBoolean("IS_PLUGIN_USER") : Boolean.FALSE;
            if (!isPluginUser && emailID != null && !emailID.isEmpty() && this.isEmailAlreadyExists(emailID, loginID)) {
                PSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in PSUserHandler.modifyUser() : EmailExists");
                throw new SyMException(720, I18N.getMsg("dc.admin.uac.EMAIL_EXISTS", new Object[] { emailID }), "dc.admin.uac.EMAIL_EXISTS", (Throwable)null);
            }
            if (!modifyUserJObj.optString("IS_PLUGIN_USER").equalsIgnoreCase("true") && roleID != null) {
                modifyUserJObj.put("roleChanged", (Object)"true");
            }
            final String oldUserName = getDCUser(loginID);
            final Long oldRoleID = getRoleIdForUser(loginID);
            final String logDataBeforeModify = "Going to modify '" + oldUserName + "' with Role id = '" + oldRoleID + "' mapped CG/RO resources id= '" + oldMappedList + "'";
            PSCoreUserUtil.logger.log(Level.INFO, logDataBeforeModify);
            this.modifyUserInUserTables(modifyUserJObj);
            final String logDataAfterModify = "User '" + oldUserName + "' has been modified with username='" + userName + "' RoleId='" + roleID + " Domain='" + domainNameForLog + "' ComputerScope='" + computerScopeForLog + "' of Groups='" + staticCustomGroups.toString() + "' MDM Scope='" + mdmScopeForLog + "' of Groups='" + mdmDeviceGroups.toString() + "' mapping list CG/RO ResourcesIds:" + newList;
            PSCoreUserUtil.logger.log(Level.INFO, logDataAfterModify);
            this.updateSystemParams(modifyUserJObj);
            CoreUserUtil.handleUserModifiedListeners(modifyUserJObj);
            PSCoreUserUtil.logger.log(Level.INFO, "UserMgmt: PSUserHandler.modifyUser() method completed");
        }
        catch (final Exception ex) {
            PSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in PSUserHandler.modifyUser()");
            throw ex;
        }
    }
    
    @Override
    public HashMap sendAccountActivationMail(final JSONObject alertJSON) throws Exception {
        final HashMap responseMap = new HashMap();
        responseMap.put("status", "failure");
        return responseMap;
    }
    
    @Override
    public HashMap sendAccountPasswordResetMail(final JSONObject alertJSON) throws Exception {
        final HashMap responseMap = new HashMap();
        responseMap.put("status", "failure");
        return responseMap;
    }
}
