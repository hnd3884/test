package com.me.mdm.internaltool;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.RedisQueueUtil;
import com.me.devicemanagement.onpremise.webclient.admin.DCQueueCountAction;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.mdm.onpremise.server.authentication.MDMPUserHandler;
import org.json.JSONArray;
import java.util.Collection;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import com.me.devicemanagement.framework.server.authorization.RoleHandler;
import com.me.mdm.server.role.RolesFacade;
import java.util.Properties;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.common.MDMEventConstant;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.internaltool.BaseInternalToolHandler;

public class InternalToolHandler extends BaseInternalToolHandler
{
    private static final String IT_ASSET_MANAGER = "IT Asset Manager";
    private static final String PROFILE_MANAGER = "Profile Manager";
    private static final String APP_MANAGER = "App Manager";
    private static final String ADMIN = "admin";
    private static Logger logger;
    
    public void createDemoUsers(final JSONObject jsonObject) {
        try {
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            final String[] staticUsersNotToDelete = { "admin", MDMEventConstant.DC_SYSTEM_USER, "SDP-SYSTEM-USER", "dummy" };
            final Criteria dontDelete = new Criteria(Column.getColumn("AaaUser", "FIRST_NAME"), (Object)staticUsersNotToDelete, 9);
            MDMUtil.getPersistence().delete(dontDelete);
            final List userList = new ArrayList();
            final Properties userProp = new Properties();
            final RolesFacade rolesFacade = new RolesFacade();
            final JSONObject rolesObject = rolesFacade.getRolesForRoleForm();
            if (!isMSP) {
                new RoleHandler().addRoles("Profile Manager", "dc.admin.uac.default_mdm_profile_manager", MDMEventConstant.DC_SYSTEM_USER, new String[] { String.valueOf(rolesObject.get("MDM_Configurations_Write")), String.valueOf(rolesObject.get("MDM_Configurations_Read")) });
                new RoleHandler().addRoles("App Manager", "dc.admin.uac.default_mdm_app_manager", MDMEventConstant.DC_SYSTEM_USER, new String[] { String.valueOf(rolesObject.get("MDM_AppMgmt_Write")), String.valueOf(rolesObject.get("MDM_AppMgmt_Read")) });
                userList.add("Administrator");
                userList.add("IT Asset Manager");
                userList.add("Technician");
                userList.add("Profile Manager");
                userList.add("App Manager");
                userProp.setProperty("Administrator", "admin");
                userProp.setProperty("IT Asset Manager", "assetmanager");
                userProp.setProperty("Technician", "technician");
                userProp.setProperty("Profile Manager", "profilemanager");
                userProp.setProperty("App Manager", "appmanager");
                TwoFactorAction.UpdateTwoFactorDiabledDetails();
                UserMgmtUtil.deleteUserMgmtParameter(new String[] { "authType", "otp" });
            }
            else {
                final String sCustomerIDs = CustomerInfoUtil.getInstance().getCreatedCustomerIdsAsString();
                final String[] sCustomerIDArray = sCustomerIDs.split(",");
                userList.add("Administrator");
                userList.add("Technician");
                userList.add("Customer Administrator");
                userProp.setProperty("Administrator", "admin");
                userProp.setProperty("admin_Cust_Id", sCustomerIDs);
                userProp.setProperty("Technician", "technician");
                userProp.setProperty("technician_Cust_Id", sCustomerIDs);
                userProp.setProperty("Customer Administrator", "customer");
                if (sCustomerIDArray.length > 0) {
                    userProp.setProperty("customer_Cust_Id", sCustomerIDArray[0]);
                }
            }
            final DataObject dobj = this.getRoleID();
            final Properties localeProp = SyMUtil.getLocalesProperties();
            for (int i = 0; i < userList.size(); ++i) {
                final String roleName = userList.get(i) + "";
                final String userName = userProp.getProperty(roleName);
                for (final String key : localeProp.stringPropertyNames()) {
                    final String[] langCountry = key.split("_");
                    String userName_Locale = userName.trim() + "_" + key;
                    final String password;
                    userName_Locale = (password = userName_Locale.toLowerCase());
                    final Row r = dobj.getRow("UMRole", new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)roleName, 0));
                    final String role_ID = r.getOriginalValue("UM_ROLE_ID") + "";
                    final Properties contactInfoProp = new Properties();
                    ((Hashtable<String, String>)contactInfoProp).put("USER_EMAIL_ID", "");
                    ((Hashtable<String, String>)contactInfoProp).put("USER_PH_NO", "");
                    ((Hashtable<String, String>)contactInfoProp).put("USER_LOCALE", key);
                    final String sCustomerIDs2 = userProp.getProperty(userName + "_Cust_Id");
                    final JSONObject addUserObj = new JSONObject();
                    addUserObj.put("userName", (Object)userName_Locale);
                    addUserObj.put("loginName", (Object)userName_Locale);
                    addUserObj.put("password", (Object)password);
                    addUserObj.put("role_ID", (Object)role_ID);
                    addUserObj.put("isDCUser", true);
                    addUserObj.put("USER_LOCALE", (Object)key);
                    addUserObj.put("sCustomerIDs", (Object)sCustomerIDs2);
                    addUserObj.put("mdmScope", 0);
                    final List roleNameList = new ArrayList();
                    roleNameList.add("All_Managed_Mobile_Devices");
                    final List<Long> list = DMUserHandler.getRoleList(role_ID);
                    final List<Long> roleIdsList = DMUserHandler.getRoleIdsFromRoleName(roleNameList);
                    roleIdsList.addAll(list);
                    final JSONArray roleIdsListArray = new JSONArray((Collection)roleIdsList);
                    addUserObj.put("roleIdsList", (Object)roleIdsListArray);
                    try {
                        final Object summaryGroupID = DMUserHandler.getSummaryGroupID(role_ID);
                        addUserObj.put("summaryGroupID", summaryGroupID);
                        final Long id = MDMPUserHandler.getInstance().addUserForMDM(addUserObj);
                        final Long userID = DMUserHandler.getDCUserID(id);
                        final String defaultTimeZoneID = SyMUtil.getDefaultTimeZoneID();
                        DMOnPremiseUserUtil.changeAAAProfile(userID, langCountry[0], langCountry[1], defaultTimeZoneID);
                        DCEventLogUtil.getInstance().addEvent(701, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "dc.support.USER_ADDED", (Object)userName, true);
                    }
                    catch (final Exception ex) {
                        InternalToolHandler.logger.log(Level.SEVERE, "Error occurred  addUser method ", ex);
                        DCEventLogUtil.getInstance().addEvent(702, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "dc.support.USER_EXISTS", (Object)userName, true);
                    }
                }
            }
        }
        catch (final Exception e) {
            InternalToolHandler.logger.log(Level.WARNING, "Issue in adding demo user", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private DataObject getRoleID() throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("UMRole"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dobj = MDMUtil.getPersistence().get(query);
        return dobj;
    }
    
    public JSONObject suspendQueue(final JSONObject jsonObject) throws Exception {
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        final String qName = msgBody.optString("queue_name");
        try {
            final DCQueue queueObj = DCQueueHandler.getQueue(qName);
            final boolean qSuspend = queueObj.isQueueSuspended();
            if (!qSuspend) {
                queueObj.suspendQExecution();
            }
        }
        catch (final Exception e) {
            InternalToolHandler.logger.log(Level.WARNING, "Cannot suspend queue", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public JSONObject resumeQueue(final JSONObject jsonObject) throws Exception {
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        final String qName = msgBody.optString("queue_name");
        try {
            final DCQueue queueObj = DCQueueHandler.getQueue(qName);
            final boolean qSuspend = queueObj.isQueueSuspended();
            if (qSuspend) {
                queueObj.resumeQExecution();
            }
        }
        catch (final Exception e) {
            InternalToolHandler.logger.log(Level.WARNING, "Cannot resume queue", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public JSONObject refreshQueue(final JSONObject jsonObject) throws Exception {
        final DCQueueCountAction qCount = new DCQueueCountAction();
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        long processTime = 0L;
        long lastDataTime = 0L;
        long count = 0L;
        long memoryCount = 0L;
        try {
            final String qTabName = msgBody.optString("queue_table_name");
            final String qName = msgBody.optString("queue_name");
            final long qTabId = msgBody.optLong("queue_table_id", -1L);
            final boolean isRedis = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
            if (isRedis) {
                final Properties props = RedisQueueUtil.getRedisQueueDetails(qName);
                if (props != null) {
                    memoryCount = ((Hashtable<K, Long>)props).get("memoryCount");
                    count = ((Hashtable<K, Long>)props).get("totalCount");
                    processTime = ((Hashtable<K, Long>)props).get("firstTime");
                    lastDataTime = ((Hashtable<K, Long>)props).get("lastTime");
                }
            }
            else {
                count = qCount.getQueueCount(qTabName);
                processTime = qCount.getProcessTime(qTabName, 0);
                lastDataTime = qCount.getProcessTime(qTabName, 1);
                memoryCount = new DCQueueHandler().getMemoryCount(qName);
            }
            qCount.addorUpdateQCountTable(qTabId, count, memoryCount, processTime, lastDataTime);
        }
        catch (final Exception e) {
            InternalToolHandler.logger.log(Level.WARNING, "Cannot refresh queue", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    static {
        InternalToolHandler.logger = Logger.getLogger("MDMLogger");
    }
}
