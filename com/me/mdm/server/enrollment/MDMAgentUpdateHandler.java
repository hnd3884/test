package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONException;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.adventnet.sym.server.mdm.util.MDMAgentBuildVersionsUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import org.json.simple.JSONObject;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;
import java.util.logging.Logger;

public class MDMAgentUpdateHandler
{
    public Logger logger;
    private static MDMAgentUpdateHandler updateHandler;
    
    public MDMAgentUpdateHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static MDMAgentUpdateHandler getInstance() {
        if (MDMAgentUpdateHandler.updateHandler == null) {
            MDMAgentUpdateHandler.updateHandler = new MDMAgentUpdateHandler();
        }
        return MDMAgentUpdateHandler.updateHandler;
    }
    
    public void updateAgentUpgradeStatus(final HashMap hashMap) throws Exception {
        String loggerMsg = " Agent version is updated for resource ";
        String eventMsg = "dc.mdm.actionlog.enrollment.agent_upgrade_success";
        String remarks = "dc.mdm.enroll.successfully_upgraded";
        final String androidAgent = hashMap.get("AgentVersion");
        final String sVersionCode = hashMap.get("AgentVersionCode");
        final Long versionCode = (sVersionCode != null) ? Long.parseLong(sVersionCode) : -1L;
        final String notifiedVersion = hashMap.get("NOTIFIED_AGENT_VERSION");
        final String udid = hashMap.get("UDID");
        Long resourceId = hashMap.containsKey("RESOURCE_ID") ? hashMap.get("RESOURCE_ID") : null;
        if (resourceId == null) {
            resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        }
        if (hashMap.containsKey("REMARKS")) {
            remarks = hashMap.get("REMARKS");
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", ManagedDeviceHandler.getInstance().getManagedDeviceStatus(resourceId));
        ((Hashtable<String, String>)properties).put("REMARKS", remarks);
        ((Hashtable<String, Long>)properties).put("RESOURCE_ID", resourceId);
        ((Hashtable<String, Long>)properties).put("AGENT_VERSION_CODE", versionCode);
        if (androidAgent != null) {
            ((Hashtable<String, String>)properties).put("AGENT_VERSION", androidAgent);
        }
        if (notifiedVersion != null) {
            ((Hashtable<String, String>)properties).put("NOTIFIED_AGENT_VERSION", notifiedVersion);
        }
        ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
        if (hashMap.containsKey("Status")) {
            loggerMsg = "Agent upgrade failed for resource ";
            eventMsg = "dc.mdm.actionlog.enrollment.agent_upgrade_failed";
        }
        this.logger.log(Level.INFO, "{0}{1}", new Object[] { loggerMsg, resourceId });
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
        final String sDeviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, MDMEventConstant.DC_SYSTEM_USER, eventMsg, sDeviceName, customerId);
        final JSONObject logJSON = new JSONObject();
        logJSON.put((Object)"REMARKS", (Object)"upgrade-success");
        logJSON.put((Object)"NAME", (Object)sDeviceName);
        logJSON.put((Object)"UDID", (Object)udid);
        logJSON.put((Object)"RESOURCE_ID", (Object)resourceId);
        MDMOneLineLogger.log(Level.INFO, "AGENT_UPGRADE", logJSON);
    }
    
    public String getAgentNotifiedVersion(final int queueDataType) throws DataAccessException {
        String agentVersion = ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("ANDROID_AGENT_VERSION");
        if (queueDataType == 102) {
            agentVersion = ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("SAFE_AGENT_VERSION");
        }
        else if (queueDataType == 1) {
            agentVersion = ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("IOS_AGENT_VERSION");
        }
        else if (queueDataType == 3) {
            agentVersion = ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("ANDROID_ADMIN_VERSION");
        }
        return agentVersion;
    }
    
    public String getSafeAgentVersion(final Long resourceId) {
        String agentgentVersion = ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("KNOX_AGENT_VERSION");
        try {
            final String currentAgentVersion = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceId, "AGENT_VERSION");
            if (currentAgentVersion.contains("S")) {
                agentgentVersion = ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("SAFE_AGENT_VERSION");
            }
        }
        catch (final Exception ex) {
            this.logger.warning("Exception on getting SAFE version of resource " + resourceId);
        }
        return agentgentVersion;
    }
    
    public void updateAppAgentVersion(final Long deviceId, final String appVersion, final String appVersionCode) {
        try {
            final Criteria deviceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("ManagedDevice", deviceCri);
            Row deviceRow = null;
            if (!dObj.isEmpty()) {
                deviceRow = dObj.getRow("ManagedDevice", deviceCri);
                deviceRow.set("AGENT_VERSION", (Object)appVersion);
                if (appVersionCode != null) {
                    deviceRow.set("AGENT_VERSION_CODE", (Object)Long.parseLong(appVersionCode));
                }
                dObj.updateRow(deviceRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in updateAppAgentVersion ", ex);
        }
    }
    
    public org.json.JSONObject getAgentUpgradeRequestData(final int agentType, final Long customerId, final boolean newUrlServer) throws JSONException {
        final Properties serverProp = MDMUtil.getMDMServerInfo();
        String agentVersion = null;
        Long agentVersionCode = null;
        Long mandatoryAgentVersionCode = null;
        String agentDownloadUrl = null;
        switch (agentType) {
            case 1: {
                agentVersion = ((Hashtable<K, String>)serverProp).get("IOS_AGENT_VERSION");
                agentVersionCode = ((Hashtable<K, Long>)serverProp).get("IOS_AGENT_VERSION_CODE");
                mandatoryAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("MANDATORY_IOS_CODE");
                final Long appId = MDMAppMgmtHandler.getInstance().getIOSNativeAgentAppId(customerId);
                agentDownloadUrl = (newUrlServer ? ("/installAppReq.mobileapps?isAppUpgrade=true&appId=" + appId) : ("/installAppReq.mobapps?isAppUpgrade=true&appId=" + appId));
                break;
            }
            case 8: {
                agentVersion = ((Hashtable<K, String>)serverProp).get("MAC_AGENT_VERSION");
                agentVersionCode = ((Hashtable<K, Long>)serverProp).get("MAC_AGENT_VERSION_CODE");
                mandatoryAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("MANDATORY_MAC_CODE");
                agentDownloadUrl = MDMAgentBuildVersionsUtil.getMDMAgentInfo("macagenturl") + String.valueOf(agentVersionCode) + "/MDMMacAgent.pkg";
                break;
            }
            case 6: {
                agentVersion = ((Hashtable<K, String>)serverProp).get("ANDROID_ADMIN_VERSION");
                agentVersionCode = ((Hashtable<K, Long>)serverProp).get("ANDROID_ADMIN_VERSION_CODE");
                mandatoryAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("MANDATORY_ADMIN_CODE");
                agentDownloadUrl = (CustomerInfoUtil.isSAS ? "/agent/MDMAdminApp.apk" : "/agent/mdm/admin/MDMAndroidAdmin.apk");
                break;
            }
            case 2:
            case 3: {
                return this.getAndroidAgentUpgradeData(agentType);
            }
            case 4: {
                agentVersion = ((Hashtable<K, String>)serverProp).get("WINDOWS_AGENT_VERSION");
                agentVersionCode = ((Hashtable<K, Long>)serverProp).get("WINDOWS_AGENT_VERSION_CODE");
                mandatoryAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("MANDATORY_WINDOWS_CODE");
                final Properties companyHubAppProp = WpCompanyHubAppHandler.getInstance().getWpCompanyHubAppDetails(customerId);
                if (companyHubAppProp != null) {
                    agentDownloadUrl = ((Hashtable<K, String>)companyHubAppProp).get("APP_FILE_PATH");
                    agentDownloadUrl = agentDownloadUrl.replace("\\", "/");
                    break;
                }
                break;
            }
        }
        final org.json.JSONObject upgradeRequestJson = new org.json.JSONObject();
        upgradeRequestJson.put("AgentVersion", (Object)agentVersion);
        upgradeRequestJson.put("AgentVersionCode", (Object)agentVersionCode);
        upgradeRequestJson.put("MandatoryAgentVersionCode", (Object)mandatoryAgentVersionCode);
        upgradeRequestJson.put("DownloadURL", (Object)agentDownloadUrl);
        return upgradeRequestJson;
    }
    
    private org.json.JSONObject getAndroidAgentUpgradeData(final int agentType) throws JSONException {
        final Properties serverProp = MDMUtil.getMDMServerInfo();
        final String androidAgentVersion = ((Hashtable<K, String>)serverProp).get("ANDROID_AGENT_VERSION");
        final Long androidAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("ANDROID_AGENT_VERSION_CODE");
        final Long androidMandatoryAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("MANDATORY_ANDROID_CODE");
        final String androidAgentDownloadUrl = MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 5);
        final String knoxAgentVersion = ((Hashtable<K, String>)serverProp).get("KNOX_AGENT_VERSION");
        final Long knoxAgentVersionCode = ((Hashtable<K, Long>)serverProp).get("KNOX_AGENT_VERSION_CODE");
        final String knoxAgentDownloadUrl = "/agent/MDMKnoxAgent.apk";
        final org.json.JSONObject upgradeRequestJson = new org.json.JSONObject();
        upgradeRequestJson.put("AgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 2));
        upgradeRequestJson.put("AgentVersion", (Object)androidAgentVersion);
        upgradeRequestJson.put("AgentVersionCode", (Object)androidAgentVersionCode);
        upgradeRequestJson.put("MandatoryAgentVersionCode", (Object)androidMandatoryAgentVersionCode);
        upgradeRequestJson.put("DownloadURL", (Object)androidAgentDownloadUrl);
        upgradeRequestJson.put("KnoxAgentVersion", (Object)knoxAgentVersion);
        upgradeRequestJson.put("KnoxAgentVersionCode", (Object)knoxAgentVersionCode);
        upgradeRequestJson.put("KnoxDownloadURL", (Object)knoxAgentDownloadUrl);
        upgradeRequestJson.put("APKDownloadURL", (Object)androidAgentDownloadUrl);
        upgradeRequestJson.put("VersionName", (Object)androidAgentVersion);
        upgradeRequestJson.put("VersionCode", (Object)androidAgentVersionCode);
        return upgradeRequestJson;
    }
    
    public String getAndroidAgentUpgradeErrRemarks(final String fullData) throws Exception {
        String upgradeErrRemarks = "dc.mdm.enroll.agent_upgrade.failed";
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new org.json.JSONObject(fullData));
        final String errCodeStr = hmap.get("ErrorCode");
        final String errorMsg = hmap.get("ErrorMsg");
        boolean isValidKeyFromAgent = false;
        if (errorMsg != null) {
            isValidKeyFromAgent = (errorMsg.trim().indexOf(" ") == -1);
            upgradeErrRemarks = errorMsg;
        }
        if (errCodeStr != null && !isValidKeyFromAgent) {
            final int upgradeErrCode = Integer.parseInt(errCodeStr);
            switch (upgradeErrCode) {
                case 12050: {
                    upgradeErrRemarks = "dc.mdm.enroll.agent_upgrade.failed.agent_already_uptodate";
                    break;
                }
                case 12180: {
                    upgradeErrRemarks = "dc.mdm.enroll.agent_upgrade.failed.agent_download";
                    break;
                }
                case 12051:
                case 12060: {
                    upgradeErrRemarks = "dc.mdm.enroll.agent_upgrade.failed";
                    break;
                }
            }
        }
        return upgradeErrRemarks;
    }
    
    static {
        MDMAgentUpdateHandler.updateHandler = null;
    }
}
