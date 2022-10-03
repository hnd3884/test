package com.me.mdm.chrome.agent.commands.profiles.payloads.privacy;

import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.core.communication.CommunicationStatus;
import com.me.mdm.chrome.agent.core.MessageUtil;
import java.util.Iterator;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import com.google.chromedevicemanagement.v1.model.DeviceReporting;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class PrivacyManager
{
    public Logger logger;
    
    public PrivacyManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public void setPrivacySettings(final Context context, final JSONObject privacyData) {
        try {
            this.logger.log(Level.INFO, "Privacy Data : {0}", privacyData);
            final boolean isDeviceReportingEnabled = privacyData.optBoolean("DeviceReportingEnabled", false);
            final boolean isRecentUserReportingEnabled = privacyData.optBoolean("RecentUserReporting", false);
            this.setDeviceReportingEnabled(context, isDeviceReportingEnabled);
            this.setRecentUserReportingEnabled(context, isRecentUserReportingEnabled);
            final DeviceReporting deviceReporting = new DeviceReporting();
            deviceReporting.setDeviceStateReportingEnabled(Boolean.valueOf(isDeviceReportingEnabled));
            deviceReporting.setRecentUsersReportingEnabled(Boolean.valueOf(isRecentUserReportingEnabled));
            final DevicePolicy devicePolicy = new DevicePolicy();
            devicePolicy.setDeviceReporting(deviceReporting);
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while applying Privacy payload", e);
        }
    }
    
    public String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s + ",");
        }
        return builder.toString();
    }
    
    public void getPrivacyStatus(final Context context) {
        try {
            final MessageUtil msgUtil = new MessageUtil(context);
            msgUtil.messageType = "PrivacySettings";
            msgUtil.setMessageData(new JSONObject());
            final CommunicationStatus status = msgUtil.postMessageData();
            if (status.getStatus() == 0) {
                final JSONObject jsonObject = new JSONObject(status.getUrlDataBuffer());
                this.setPrivacySettings(context, jsonObject);
            }
            else {
                this.logger.log(Level.SEVERE, "Failed to receive Privacy settings  : {0}", status.getErrorMessage());
                this.setPrivacySettings(context, new JSONObject());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while posting data : {0}", e);
        }
    }
    
    private void setDeviceReportingEnabled(final Context context, final boolean isDeviceReportingEnabled) {
        new MDMAgentParamsTableHandler(context).addBooleanValue("DeviceReportingEnabled", isDeviceReportingEnabled);
    }
    
    private void setRecentUserReportingEnabled(final Context context, final boolean isRecentUserEnabled) {
        new MDMAgentParamsTableHandler(context).addBooleanValue("RecentUserReporting", isRecentUserEnabled);
    }
    
    public boolean isDeviceReportingEnabled(final Context context) {
        return new MDMAgentParamsTableHandler(context).getBooleanValue("DeviceReportingEnabled");
    }
    
    public boolean isRecentUserReportingEnabled(final Context context) {
        return new MDMAgentParamsTableHandler(context).getBooleanValue("RecentUserReporting");
    }
}
