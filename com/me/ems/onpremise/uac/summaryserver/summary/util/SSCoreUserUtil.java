package com.me.ems.onpremise.uac.summaryserver.summary.util;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.ProbeUsersUtil;
import com.adventnet.persistence.DataAccess;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.ems.onpremise.uac.core.UserOperationsInterface;
import com.me.ems.framework.uac.handler.UserOperationsHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
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

public class SSCoreUserUtil extends CoreUserUtil
{
    public Long addUser(final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws Exception {
        Long loginID = null;
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
                SSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - addUser : EmailExists");
                throw new SyMException(720, I18N.getMsg("ems.admin.admin.email_already_exists", new Object[] { emailID }), "ems.admin.admin.email_already_exists", (Throwable)null);
            }
            if (!isUserAccountAvailable(userName, domainName)) {
                SSCoreUserUtil.logger.log(Level.INFO, "In addUser of DCUserHandler...Going to add  user " + userName + " in domain = " + domainNameForLog + " with Role id = " + roleID + " computerscope " + computerScopeForLog + " MDM Scope " + mdmScopeForLog);
                addUserJObj.put("service_id", (Object)AuthUtil.getServiceId("System"));
                loginID = this.addUserInUserTablesWOPassword(addUserJObj, probeHandlerObject);
                SSCoreUserUtil.logger.log(Level.INFO, "User " + userName + " added in domain " + domainNameForLog + " with Role id = " + roleID + " computerscope = " + computerScopeForLog + " MDM Scope = " + mdmScopeForLog + " mapping list CG/RO resources id= " + newList);
                this.updateSystemParams(addUserJObj);
            }
            else {
                loginID = this.handleExistingUser(addUserJObj);
            }
            addUserJObj.put("scopeList", (Collection)newList);
            CoreUserUtil.handleUserAddedListeners(loginID, addUserJObj);
        }
        catch (final SyMException symEx) {
            SSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - userAdded : ", (Throwable)symEx);
            throw symEx;
        }
        catch (final Exception ex) {
            SSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred in DCUserhandler - userAdded : ", ex);
            throw ex;
        }
        return loginID;
    }
    
    protected Long addUserInUserTablesWOPassword(final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws Exception {
        Long loginID = null;
        try {
            SyMUtil.getUserTransaction().begin();
            final DataObject addUserDO = SyMUtil.getPersistence().constructDataObject();
            new DMUserHandler().addUser(addUserDO, addUserJObj);
            loginID = DMOnPremiseUserUtil.addUserWithoutPassword(addUserDO, addUserJObj);
            addUserJObj.put("loginID", (Object)loginID);
            this.addUserInDCCoreTables(loginID, addUserJObj);
            this.addSummaryProbeHandling(addUserJObj, addUserDO, probeHandlerObject);
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
            SSCoreUserUtil.logger.log(Level.INFO, "Exception while adding user", e);
            SyMUtil.getUserTransaction().rollback();
            throw e;
        }
        return loginID;
    }
    
    public JSONObject addSummaryProbeHandling(final JSONObject addUserJObj, final DataObject addUserDO, final JSONObject probeHandlerObject) throws DataAccessException {
        final Long loginID = (Long)addUserDO.getFirstValue("AaaLogin", "LOGIN_ID");
        probeHandlerObject.put("userID", addUserDO.getFirstValue("AaaUser", "USER_ID"));
        probeHandlerObject.put("loginID", addUserDO.getFirstValue("AaaLogin", "LOGIN_ID"));
        probeHandlerObject.put("accountID", addUserDO.getFirstValue("AaaAccount", "ACCOUNT_ID"));
        probeHandlerObject.put("contactInfoID", addUserDO.getFirstValue("AaaContactInfo", "CONTACTINFO_ID"));
        final Row loginExtn = addUserDO.getRow("AaaLoginExtn");
        loginExtn.set("PROBE_SCOPE", addUserJObj.get("probeScope"));
        addUserDO.updateRow(loginExtn);
        DataAccess.update(addUserDO);
        final String[] probeList = (String[])addUserJObj.opt("probeList");
        ProbeUsersUtil.addProbeUsers(loginID, probeList);
        return probeHandlerObject;
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
        ApiFactoryProvider.getUserManagementAPIHandler().addUser(jsonObject);
        this.processDCCGListofUser(jsonObject);
    }
    
    public void processDCCGListofUser(final JSONObject jsonObject) throws Exception {
        final Long loginID = (Long)jsonObject.get("loginID");
        final JSONArray staticCustomGroups = (JSONArray)jsonObject.opt("Static_Custom_Groups");
        final JSONArray mdmDeviceGroups = (JSONArray)jsonObject.opt("Mdm_Device_Groups");
        if (staticCustomGroups != null && staticCustomGroups.length() > 0) {
            this.setCustomGroupForUser(loginID, staticCustomGroups, 1);
        }
        if (mdmDeviceGroups != null && mdmDeviceGroups.length() > 0) {
            this.setCustomGroupForUser(loginID, mdmDeviceGroups, 2);
        }
    }
    
    public void setCustomGroupForUser(final Long loginId, final JSONArray groupList, final int scopeType) {
        final String id = (scopeType == 1) ? "groupId" : "group_id";
        final String name = (scopeType == 1) ? "groupName" : "name";
        try {
            if (groupList != null && groupList.length() != 0) {
                final WritableDataObject dobj = new WritableDataObject();
                for (int index = 0; index < groupList.length(); ++index) {
                    final JSONObject cgData = groupList.getJSONObject(index);
                    final Row userCustomGroupMapping = new Row("SSUserCustomGroupMapping");
                    userCustomGroupMapping.set("LOGIN_ID", (Object)loginId);
                    userCustomGroupMapping.set("GROUP_RESOURCE_ID", cgData.get(id));
                    userCustomGroupMapping.set("GROUP_RESOURCE_NAME", cgData.get(name));
                    userCustomGroupMapping.set("SCOPE_TYPE", (Object)scopeType);
                    dobj.addRow(userCustomGroupMapping);
                }
                SyMUtil.getPersistence().update((DataObject)dobj);
            }
        }
        catch (final Exception exception) {
            SSCoreUserUtil.logger.log(Level.SEVERE, "Exception while adding Custom groups for the user ", exception);
        }
    }
    
    public static List<Hashtable<String, Object>> getAssignedCustomGroupsForUser(final Long loginID, final int customGroupType) {
        SSCoreUserUtil.logger.log(Level.INFO, () -> "Fetching Assigned Custom Groups for User " + n);
        final List<Hashtable<String, Object>> customGroupsDetails = new ArrayList<Hashtable<String, Object>>();
        final String id = (customGroupType == 1) ? "groupId" : "group_id";
        final String name = (customGroupType == 1) ? "groupName" : "name";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SSUserCustomGroupMapping"));
            final Criteria userCriteria = new Criteria(Column.getColumn("SSUserCustomGroupMapping", "LOGIN_ID"), (Object)loginID, 0);
            final Criteria groupTypeCriteria = new Criteria(Column.getColumn("SSUserCustomGroupMapping", "SCOPE_TYPE"), (Object)customGroupType, 0);
            selectQuery.setCriteria(userCriteria.and(groupTypeCriteria));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("SSUserCustomGroupMapping");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Hashtable<String, Object> customGroupData = new Hashtable<String, Object>();
                    customGroupData.put(id, row.get("GROUP_RESOURCE_ID"));
                    customGroupData.put(name, row.get("GROUP_RESOURCE_NAME"));
                    customGroupsDetails.add(customGroupData);
                }
            }
        }
        catch (final DataAccessException e) {
            SSCoreUserUtil.logger.log(Level.SEVERE, "Exception Occurred while getting Custom Groups for User :" + loginID, (Throwable)e);
        }
        return customGroupsDetails;
    }
    
    public static List<Hashtable<String, Object>> getAssignedComputerGroupsForUser(final Long loginID) {
        return getAssignedCustomGroupsForUser(loginID, 1);
    }
    
    public static List<Hashtable<String, Object>> getAssignedDeviceGroupsForUser(final Long loginID) {
        return getAssignedCustomGroupsForUser(loginID, 2);
    }
}
