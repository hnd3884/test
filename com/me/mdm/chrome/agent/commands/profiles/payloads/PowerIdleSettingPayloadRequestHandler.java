package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.PowerManagementIdleSettings;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class PowerIdleSettingPayloadRequestHandler extends PayloadRequestHandler
{
    private static final String POWER_MGMT_IDLE_SETTINGS = "PowerManageemntIdleSettings";
    
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            final JSONObject PowerMgmtSettings = payloadData.optJSONObject("PayloadData");
            this.logger.info("Payload data for PowerIdle Management settings" + PowerMgmtSettings);
            final UserPolicy userPolicy = new UserPolicy();
            final PowerManagementIdleSettings powerManagementIdleSettings = new PowerManagementIdleSettings();
            powerManagementIdleSettings.setPowerManagementIdleSettings(PowerMgmtSettings.toString());
            userPolicy.setPowerManagementIdleSettings(powerManagementIdleSettings);
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, modifyPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            this.logger.info("Going to remove the Power mgmt Profile");
            final Context context = request.getContainer().getContext();
            final UserPolicy userPolicy = new UserPolicy();
            userPolicy.setPowerManagementIdleSettings(new PowerManagementIdleSettings().setPowerManagementIdleSettings("{}"));
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
}
