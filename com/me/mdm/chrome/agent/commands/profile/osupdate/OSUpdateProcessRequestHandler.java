package com.me.mdm.chrome.agent.commands.profile.osupdate;

import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.commands.profiles.payloads.KioskPayloadRequestHandler;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.AutoUpdateSettings;
import com.google.chromedevicemanagement.v1.model.ReleaseChannel;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;

public class OSUpdateProcessRequestHandler extends ProcessRequestHandler
{
    private static final String AUTO_UPDATE_ENABLED = "AutoUpdatesEnabled";
    private static final String MAX_TARGET_PREFIX = "MaxUpdatableTargetPrefix";
    private static final String RANDOM_SCATTER_DURATION = "RandomScatterDuration";
    private static final String REBOOT_AFTER_UPDATE = "AutoRebbotAfterUpdate";
    private static final String RELEASE_CHANEEL = "ReleaseChannel";
    
    public void applyOSUpdatePolicy(final Context context) {
        try {
            final JSONObject payloadData = new MDMAgentParamsTableHandler(context).getJSONObject("ChromeOsUpdatePolicy");
            if (payloadData == null) {
                this.logger.log(Level.INFO, "No OS Policy to apply");
                return;
            }
            final DevicePolicy devicePolicy = new DevicePolicy();
            final int releasechannel = payloadData.optInt("ReleaseChannel", 0);
            final ReleaseChannel releaseChannel = new ReleaseChannel();
            if (releasechannel == 0) {
                releaseChannel.setReleaseChannelType("RELEASE_CHANNEL_UNSPECIFIED");
            }
            if (releasechannel == 1) {
                releaseChannel.setReleaseChannelType("DELEGATED");
            }
            if (releasechannel == 2) {
                releaseChannel.setReleaseChannelType("STABLE");
            }
            if (releasechannel == 3) {
                releaseChannel.setReleaseChannelType("BETA");
            }
            if (releasechannel == 4) {
                releaseChannel.setReleaseChannelType("DEV");
            }
            devicePolicy.setReleaseChannel(releaseChannel);
            final boolean isAutoUpdateEnabled = payloadData.optBoolean("AutoUpdatesEnabled");
            final JSONObject policyData = payloadData.optJSONObject("PolicyData");
            final String maxTargetPrefix = policyData.optString("MaxUpdatableTargetPrefix", "");
            final int randomScatterDays = policyData.optInt("RandomScatterDuration", -1);
            final Boolean autoRebbotAfterUpdate = policyData.optBoolean("AutoRebbotAfterUpdate");
            final AutoUpdateSettings updateSettings = new AutoUpdateSettings();
            updateSettings.setUpdateEnabled(Boolean.valueOf(isAutoUpdateEnabled));
            if (isAutoUpdateEnabled) {
                updateSettings.setTargetPlatformVersionPrefix(maxTargetPrefix);
                if (randomScatterDays != -1) {
                    updateSettings.setScatterFactorDuration(randomScatterDays * 60 * 60 * 24 + "s");
                }
            }
            updateSettings.setRebootAfterUpdate(autoRebbotAfterUpdate);
            devicePolicy.setAutoUpdateSettings(updateSettings);
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final IOException ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeUpdatePolicy(final Context context) {
        try {
            final DevicePolicy devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().getDevicePolicy(context.getCMPAEnterpriseAndUDID()).execute();
            devicePolicy.setAutoUpdateSettings(new AutoUpdateSettings());
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final IOException ex) {
            Logger.getLogger(KioskPayloadRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void processRequest(final Request request, final Response response) {
        final String requestCommand = request.requestType;
        final Context context = request.getContainer().getContext();
        if (requestCommand.equalsIgnoreCase("ChromeOsUpdatePolicy")) {
            final JSONObject policyDetails = (JSONObject)request.requestData;
            this.logger.info("Payload Data:" + policyDetails);
            new MDMAgentParamsTableHandler(context).addJSONObject("ChromeOsUpdatePolicy", policyDetails);
            this.applyOSUpdatePolicy(context);
        }
        else if (requestCommand.equalsIgnoreCase("RemoveChromeOsUpdatePolicy")) {
            new MDMAgentParamsTableHandler(context).removeValue("ChromeOsUpdatePolicy");
            this.removeUpdatePolicy(context);
        }
    }
}
