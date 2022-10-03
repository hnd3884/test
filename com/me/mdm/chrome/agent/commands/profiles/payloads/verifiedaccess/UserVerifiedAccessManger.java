package com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess;

import org.json.JSONObject;
import java.util.List;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.google.chromedevicemanagement.v1.model.UserAttestationEnabled;
import com.google.chromedevicemanagement.v1.model.UserVerifiedModeRequired;
import com.google.chromedevicemanagement.v1.model.UserVerifiedAccessControl;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.Context;

public class UserVerifiedAccessManger extends VerifiedAccessManager
{
    private static final String USER_VERIFIED_ACCESS = "USER_VERIFIED_ACCESS";
    
    @Override
    public void addAccessControlAccounts(final Context context, final PayloadResponse payloadResp, final Boolean isProfileInstall) {
        try {
            final boolean isAttestationenabled = new MDMAgentParamsTableHandler(context).getBooleanValue("IsAttestationEnabled");
            final boolean isverifiedModeEnabled = new MDMAgentParamsTableHandler(context).getBooleanValue("IsVerifiedModeEnabled");
            final List accountsWithFullControl = this.getFullControlAccessAccounts(context, "USER_VERIFIED_ACCESS");
            final List accountsWithpartialControl = this.getPartialControlAccessAccounts(context, "USER_VERIFIED_ACCESS");
            final UserVerifiedAccessControl userVerifiedAccessControl = new UserVerifiedAccessControl();
            if (accountsWithFullControl.size() > 0) {
                userVerifiedAccessControl.setAccountsWithFullAccess(accountsWithFullControl);
            }
            if (accountsWithpartialControl.size() > 0) {
                userVerifiedAccessControl.setAccountsWithLimitedAccess(accountsWithpartialControl);
            }
            final UserVerifiedModeRequired userVerifiedModeRequired = new UserVerifiedModeRequired();
            userVerifiedModeRequired.setUserVerifiedModeRequired(Boolean.valueOf(isverifiedModeEnabled));
            final UserAttestationEnabled userAttestationEnabled = new UserAttestationEnabled();
            userAttestationEnabled.setUserAttestationEnabled(Boolean.valueOf(isAttestationenabled));
            final UserPolicy userPolicy = new UserPolicy();
            userPolicy.setUserAttestationEnabled(userAttestationEnabled);
            userPolicy.setUserVerifiedAccessControl(userVerifiedAccessControl);
            userPolicy.setUserVerifiedModeRequired(userVerifiedModeRequired);
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in User addAccessControlAccounts :", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, isProfileInstall);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
}
