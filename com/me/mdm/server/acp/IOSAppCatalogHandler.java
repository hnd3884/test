package com.me.mdm.server.acp;

import java.util.Hashtable;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.HashMap;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IOSAppCatalogHandler extends MDMAppCatalogHandler
{
    private final Logger logger;
    
    public IOSAppCatalogHandler() {
        this.logger = Logger.getLogger("MDMAppCatalogLogger");
    }
    
    @Override
    protected JSONObject processApplicationList(final JSONObject requestJSON) throws JSONException, SQLException, QueryConstructionException, DataAccessException, Exception {
        final JSONObject responseJSON = super.processApplicationList(requestJSON);
        final JSONObject iosResponseJSON = (JSONObject)responseJSON.get("MsgResponse");
        responseJSON.put("MsgResponse", (Object)iosResponseJSON);
        return responseJSON;
    }
    
    public String addRefetchConfigDetails(final String responseData, final String udid) throws Exception {
        final JSONObject responseJson = new JSONObject(responseData);
        final JSONObject iosResponseJSON = responseJson.getJSONObject("MsgResponse");
        final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromUdid(udid);
        final boolean refetch = MDMiOSEntrollmentUtil.getInstance().getReenrollReq(erid);
        iosResponseJSON.put("refetchiosconfig", refetch);
        if (refetch) {
            final String refetchUrl = MDMiOSEntrollmentUtil.getInstance().getReenrollUrl(erid);
            iosResponseJSON.put("refetchlink", (Object)refetchUrl);
        }
        else {
            iosResponseJSON.put("refetchiosconfig", false);
        }
        responseJson.put("MsgResponse", (Object)iosResponseJSON);
        return responseJson.toString();
    }
    
    @Override
    protected JSONObject processAppInstall(final JSONObject requestJSON) throws JSONException, Exception {
        JSONObject responseJSON = null;
        final JSONObject messageRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String isScheduledAppInstallationNeeded = messageRequestJSON.optString("IsScheduledAppInstallationNeeded", "false");
        final String appIdentifier = messageRequestJSON.optString("AppIdentifier", "");
        final String userAgent = messageRequestJSON.optString("userAgent", "");
        final String appStatus = messageRequestJSON.optString("AppStatus", "");
        final Long appId = Long.parseLong(messageRequestJSON.get("AppId").toString());
        if (isScheduledAppInstallationNeeded.equalsIgnoreCase("true")) {
            responseJSON = this.processAppInstallInScheduler(appId);
            return responseJSON;
        }
        responseJSON = super.processAppInstall(requestJSON);
        Boolean isIOS7AndAbove = true;
        if (userAgent.contains("OS 4_") || userAgent.contains("OS 5_") || userAgent.contains("OS 6_")) {
            isIOS7AndAbove = false;
        }
        if (appIdentifier.equalsIgnoreCase("com.manageengine.mdm.iosagent")) {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(this.resourceId);
            final List resourceList = new ArrayList();
            resourceList.add(this.resourceId);
            DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
            DeviceCommandRepository.getInstance().addLocationConfigurationCommand(resourceList, 2);
            DeviceCommandRepository.getInstance().addBatteryConfigurationCommand(resourceList, 2);
            if (!isIOS7AndAbove && appStatus.equalsIgnoreCase("YetToInstall")) {
                final String authPassword = IosNativeAppHandler.getInstance().generateEnrollmentId(this.resourceId);
                final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(this.resourceId);
                final HashMap userInfoMap = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
                final String userName = userInfoMap.get("NAME");
                final String userEmail = userInfoMap.get("EMAIL_ADDRESS");
                final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
                final Properties serverProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                final Properties enrolAuthlMailProperties = new Properties();
                ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_name$", userName);
                ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_name$", ((Hashtable<K, Object>)serverProps).get("NAT_ADDRESS"));
                ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_port$", ((Hashtable<K, Object>)serverProps).get("NAT_HTTPS_PORT"));
                ((Hashtable<String, String>)enrolAuthlMailProperties).put("$enrollment_id$", authPassword);
                ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_emailid$", userEmail);
                mailGenerator.sendMail(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_TEMPLATE, "MDM", customerID, enrolAuthlMailProperties);
            }
        }
        return responseJSON;
    }
    
    private JSONObject processAppInstallInScheduler(final Long appId) throws JSONException, Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"InstallApplicationResponse");
        responseJSON.put("MsgResponse", (Object)this.setAppInstallInScheduler(appId));
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    private JSONObject setAppInstallInScheduler(final Long appId) throws JSONException, Exception {
        final String messageResponse = "{}";
        final long SCH_COMMAND_TIME = 120000L;
        final Long appGroupId = (Long)DBUtil.getValueFromDB("MdAppToGroupRel", "APP_ID", (Object)appId, "APP_GROUP_ID");
        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
        handler.updateAppInstallationStatus(this.resourceId, appGroupId, appId, 1, "--", 0);
        final Long collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
        final Properties prop = new Properties();
        ((Hashtable<String, Long>)prop).put("collectionId", collectionId);
        MDMUtil.getInstance().scheduleMDMCommand(this.resourceId, "InstallApplication", System.currentTimeMillis() + SCH_COMMAND_TIME, prop);
        final JSONObject messageResponseJSON = new JSONObject(messageResponse);
        return messageResponseJSON;
    }
    
    public void scheduleAppCatalogSync(final List appIdList, final Long resId, final Long executeAfterTime) throws JSONException {
        MDMUtil.getInstance().scheduleMDMCommand(resId, "ManagedApplicationList", System.currentTimeMillis() + executeAfterTime);
    }
    
    public void scheduleFetchAgentInstallStatus(final Long resId, final Long executeAfterTime) throws JSONException {
        this.logger.log(Level.INFO, "Sending fetch agent details command after agent install command is send " + resId);
        MDMUtil.getInstance().scheduleMDMCommand(resId, "FetchAppleAgentDetails", System.currentTimeMillis() + executeAfterTime);
    }
    
    @Override
    protected JSONObject processScheduleAppCatalogSync(final JSONObject requestJSON) throws JSONException {
        this.scheduleAppCatalogSync(null, this.resourceId, 0L);
        final JSONObject responseJSON = new JSONObject();
        final JSONObject messageResponseJSON = new JSONObject();
        final Long syncTime = AppsUtil.getInstance().getAppCatalogSyncTime(this.resourceId);
        messageResponseJSON.put("SyncTime", (Object)syncTime);
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        responseJSON.put("Status", (Object)"Acknowledged");
        return responseJSON;
    }
    
    public void deleteAppCatalogForDevices(final Long appID, final List<Long> resourceList) {
        try {
            final DeleteQuery appCatDelQuery = (DeleteQuery)new DeleteQueryImpl("MdAppCatalogToResource");
            final Criteria appCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)appID, 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            appCatDelQuery.setCriteria(appCriteria.and(resCriteria));
            MDMUtil.getPersistence().delete(appCatDelQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteAppCatalogForDevices", e);
        }
    }
}
