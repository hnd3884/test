package com.me.mdm.chrome.agent.commands.profiles.payloads;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess.UserVerifiedAccessManger;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class UserVerifiedAccessPayloadRequestHandler extends PayloadRequestHandler
{
    private static final String USER_VERIFIED_ACCESS = "USER_VERIFIED_ACCESS";
    
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        final Context context = request.getContainer().getContext();
        final JSONObject payloadData = payloadReq.getPayloadData();
        final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
        this.logger.info("user verify Access Payload :" + payloadData);
        new UserVerifiedAccessManger().addPayloadIdentifierToDB(context, payloadIdentifierName, "USER_VERIFIED_ACCESS");
        new MDMAgentParamsTableHandler(context).addJSONObject(payloadIdentifierName, payloadData);
        new MDMAgentParamsTableHandler(context).addBooleanValue("IsAttestationEnabled", payloadData.optBoolean("IsAttestationEnabled", true));
        new MDMAgentParamsTableHandler(context).addBooleanValue("IsVerifiedModeEnabled", payloadData.optBoolean("IsVerifiedModeEnabled", true));
        ChromeDeviceManager.getInstance().getVerifiedAccessManager(context).addAccessControlAccounts(context, payloadResp, true);
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, modifyPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
            ChromeDeviceManager.getInstance().getVerifiedAccessManager(context).removePayloadData(context, payloadIdentifierName, "USER_VERIFIED_ACCESS");
            ChromeDeviceManager.getInstance().getVerifiedAccessManager(context).addAccessControlAccounts(context, payloadResp, false);
        }
        catch (final Exception ex) {
            payloadResp.setErrorCode(12132);
            payloadResp.setErrorMsg(((GoogleJsonResponseException)ex).getDetails().getMessage());
            this.logger.info("Exception while removing Profiles");
        }
    }
}
