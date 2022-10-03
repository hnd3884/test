package com.me.mdm.onpremise.notification;

import java.util.Hashtable;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.mdm.server.util.MDMCheckSumProvider;
import com.me.mdm.onpremise.server.agent.AgentSecretsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.util.CloudAPIDataPost;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class IOSFCMNotificationCreatorHandler implements SchedulerExecutionInterface
{
    private Logger logger;
    private static final String FCM_SERVER_KEY = "fcm_server_key";
    private static final String FCM_AGENT_API_KEY = "fcm_agent_api_key";
    private static final String FCM_AGENT_SENDER_ID = "fcm_agent_sender_id";
    private static final String FCM_AGENT_PROJECT_ID = "fcm_agent_project_id";
    private static final String FCM_AGENT_APP_ID = "fcm_agent_app_id";
    
    public IOSFCMNotificationCreatorHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject getFCMDetailsFromCreator() {
        final JSONObject jsObject = new JSONObject();
        try {
            this.logger.log(Level.INFO, "Getting FCM Keys for IOS Announcement Notification from secure API");
            final String postUrl = "https://mdm.manageengine.com/api/v1/mdm/secretkeys";
            final CloudAPIDataPost postDataForSet1 = new CloudAPIDataPost();
            final JSONObject submitJSONObjectForSet1 = new JSONObject();
            final String keyName = "FCM_PUSH_NOTIFICATION_KEYS";
            submitJSONObjectForSet1.put("keys", (Object)(keyName + "_SET1"));
            postDataForSet1.encryptAndPostDataToCloud(postUrl, submitJSONObjectForSet1, "keys");
            final CloudAPIDataPost postDataForSet2 = new CloudAPIDataPost();
            final JSONObject submitJSONObjectForSet2 = new JSONObject();
            submitJSONObjectForSet2.put("keys", (Object)(keyName + "_SET2"));
            postDataForSet2.encryptAndPostDataToCloud(postUrl, submitJSONObjectForSet2, "keys");
            if (postDataForSet1.status.toString().startsWith("20") && postDataForSet2.status.toString().startsWith("20")) {
                final String responseContentForSet1 = postDataForSet1.response;
                final String responseContentForSet2 = postDataForSet2.response;
                if (SyMUtil.isValidJSON(responseContentForSet1) && SyMUtil.isValidJSON(responseContentForSet2)) {
                    final JSONObject notificationJSONSet1 = new JSONObject(responseContentForSet1);
                    final JSONObject notificationJSONSet2 = new JSONObject(responseContentForSet2);
                    notificationJSONSet2.keySet().forEach(keyStr -> notificationJSONSet1.put(keyStr, notificationJSONSet2.get(keyStr)));
                    return notificationJSONSet1;
                }
                this.logger.log(Level.INFO, "Failed due to Some Error in mdm.manageengine.com API ");
            }
            else {
                this.logger.log(Level.INFO, "Failed due to Some Error in mdm.manageengine.com API");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getFCMDetailsFromCreator : ", e);
        }
        return jsObject;
    }
    
    public void checkAndAddFCMCreatorDetails(final Properties prop) {
        try {
            JSONObject fcmCreatorDetails = null;
            final Properties appProp = MDMUtil.getInstance().getMDMApplicationProperties();
            final String app = appProp.getProperty("FCMCreatorReportName");
            if (app != null && app.equalsIgnoreCase("FCM_PushNotification_App_Details_Enterprise")) {
                fcmCreatorDetails = new JSONObject();
                this.logger.log(Level.INFO, "Key enabled for local testing. So reading FCM Announcement Notification keys from mdmApplication.conf");
                fcmCreatorDetails.put("fcm_server_key", (Object)appProp.getProperty("fcm_server_key"));
                fcmCreatorDetails.put("fcm_agent_api_key", (Object)appProp.getProperty("fcm_agent_api_key"));
                fcmCreatorDetails.put("fcm_agent_sender_id", (Object)appProp.getProperty("fcm_agent_sender_id"));
                fcmCreatorDetails.put("fcm_agent_project_id", (Object)appProp.getProperty("fcm_agent_project_id"));
                fcmCreatorDetails.put("fcm_agent_app_id", (Object)appProp.getProperty("fcm_agent_app_id"));
            }
            else {
                final JSONObject fcmServerKeyJsonFromDB = AgentSecretsHandler.getInstance().getAgentSecret(1);
                final JSONObject fcmAgentKeyJsonFromDB = AgentSecretsHandler.getInstance().getAgentSecret(2);
                final String fcmServerKeyFromDB = (String)fcmServerKeyJsonFromDB.opt("fcm_server_key");
                final String fcmAgentKeyFromDB = (String)fcmAgentKeyJsonFromDB.opt("fcm_agent_api_key");
                final String fcmServerKeyHashFromDB = (fcmServerKeyFromDB != null) ? MDMCheckSumProvider.getInstance().getSHA256HashFromString(fcmServerKeyFromDB) : null;
                final String fcmAgentKeyHashFromDB = (fcmAgentKeyFromDB != null) ? MDMCheckSumProvider.getInstance().getSHA256HashFromString(fcmAgentKeyFromDB) : null;
                final String fcmServerKeyHashFromAPI = ((Hashtable<K, String>)prop).get("FCMServer");
                final String fcmAgentKeyhashFromAPI = ((Hashtable<K, String>)prop).get("FCMAgent");
                final Boolean hashFromApiIsNull = fcmAgentKeyhashFromAPI == null || fcmServerKeyHashFromAPI == null;
                final Boolean hashFromDBIsNull = fcmServerKeyHashFromDB == null || fcmAgentKeyHashFromDB == null;
                final Boolean serverKeyhashNotEqual = fcmServerKeyHashFromDB != null && fcmServerKeyHashFromAPI != null && !fcmServerKeyHashFromDB.equals(fcmServerKeyHashFromAPI);
                final Boolean agentKeyhashNotEqual = fcmAgentKeyHashFromDB != null && fcmAgentKeyhashFromAPI != null && !fcmAgentKeyHashFromDB.equals(fcmAgentKeyhashFromAPI);
                if (hashFromApiIsNull || hashFromDBIsNull || serverKeyhashNotEqual || agentKeyhashNotEqual) {
                    this.logger.log(Level.INFO, "Getting FCM keys for Announcement from secure cloud API");
                    fcmCreatorDetails = this.getFCMDetailsFromCreator();
                }
            }
            if (fcmCreatorDetails != null) {
                final String serverKey = CryptoUtil.decrypt(fcmCreatorDetails.getString("fcm_server_key"), "1602569284318");
                final String fcmAgentApIKey = CryptoUtil.decrypt(fcmCreatorDetails.getString("fcm_agent_api_key"), "1602569284318");
                final String senderId = CryptoUtil.decrypt(fcmCreatorDetails.getString("fcm_agent_sender_id"), "1602569284318");
                final String projectId = fcmCreatorDetails.getString("fcm_agent_project_id");
                final String appId = CryptoUtil.decrypt(fcmCreatorDetails.getString("fcm_agent_app_id"), "1602569284318");
                final JSONObject fcmServerKeyJSON = new JSONObject();
                fcmServerKeyJSON.put("fcm_server_key", (Object)serverKey);
                AgentSecretsHandler.getInstance().addOrUpdateAgentSecret(1, fcmServerKeyJSON);
                final JSONObject fcmAgentKeyJSON = new JSONObject();
                fcmAgentKeyJSON.put("fcm_agent_api_key", (Object)fcmAgentApIKey);
                fcmAgentKeyJSON.put("fcm_agent_sender_id", (Object)senderId);
                fcmAgentKeyJSON.put("fcm_agent_project_id", (Object)projectId);
                fcmAgentKeyJSON.put("fcm_agent_app_id", (Object)appId);
                AgentSecretsHandler.getInstance().addOrUpdateAgentSecret(2, fcmAgentKeyJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred : ", e);
        }
    }
    
    public String getFcmServerKey() {
        final JSONObject object = AgentSecretsHandler.getInstance().getAgentSecret(1);
        return object.getString("fcm_server_key");
    }
    
    public JSONObject getFCMAgentDetails() {
        final JSONObject object = AgentSecretsHandler.getInstance().getAgentSecret(2);
        final String apiKey = object.getString("fcm_agent_api_key");
        final String projectId = object.getString("fcm_agent_project_id");
        final String senderId = object.getString("fcm_agent_sender_id");
        final String appId = object.getString("fcm_agent_app_id");
        return MDMAgentSettingsHandler.getInstance().getFCMPushNotificationConfig(apiKey, projectId, senderId, appId);
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Task executed for FCM creator");
        this.checkAndAddFCMCreatorDetails(props);
    }
}
