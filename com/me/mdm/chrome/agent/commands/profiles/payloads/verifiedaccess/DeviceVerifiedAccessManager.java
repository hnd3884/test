package com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess;

import org.json.JSONObject;
import java.util.List;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import com.google.chromedevicemanagement.v1.model.DeviceAttestationEnabled;
import com.google.chromedevicemanagement.v1.model.DeviceVerifiedModeRequired;
import com.google.chromedevicemanagement.v1.model.DeviceVerifiedAccessControl;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.Context;

public class DeviceVerifiedAccessManager extends VerifiedAccessManager
{
    private static final String DEVICE_VERIFIED_ACCESS = "USER_VERIFIED_ACCESS";
    
    @Override
    public void addAccessControlAccounts(final Context context, final PayloadResponse payloadResp, final Boolean isProfileInstall) {
        try {
            final boolean isAttestationenabled = new MDMAgentParamsTableHandler(context).getBooleanValue("IsAttestationEnabled");
            final boolean isverifiedModeEnabled = new MDMAgentParamsTableHandler(context).getBooleanValue("IsVerifiedModeEnabled");
            final List accountsWithFullControl = this.getFullControlAccessAccounts(context, "USER_VERIFIED_ACCESS");
            final List accountsWithpartialControl = this.getPartialControlAccessAccounts(context, "USER_VERIFIED_ACCESS");
            final DeviceVerifiedAccessControl deviceVerifiedAccessControl = new DeviceVerifiedAccessControl();
            deviceVerifiedAccessControl.setAccountsWithFullAccess(accountsWithFullControl);
            deviceVerifiedAccessControl.setAccountsWithLimitedAccess(accountsWithpartialControl);
            final DeviceVerifiedModeRequired deviceVerifiedModeRequired = new DeviceVerifiedModeRequired();
            deviceVerifiedModeRequired.setDeviceVerifiedModeRequired(Boolean.valueOf(isverifiedModeEnabled));
            final DeviceAttestationEnabled deviceAttestationEnabled = new DeviceAttestationEnabled();
            deviceAttestationEnabled.setDeviceAttestationEnabled(Boolean.valueOf(isAttestationenabled));
            deviceAttestationEnabled.setAttestationForContentProtectionEnabled(Boolean.valueOf(isAttestationenabled));
            final DevicePolicy devicePolicy = new DevicePolicy();
            devicePolicy.setDeviceAttestationEnabled(deviceAttestationEnabled);
            devicePolicy.setDeviceVerifiedModeRequired(deviceVerifiedModeRequired);
            devicePolicy.setDeviceVerifiedAccessControl(deviceVerifiedAccessControl);
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Device addAccessControlAccounts : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, isProfileInstall);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
}
