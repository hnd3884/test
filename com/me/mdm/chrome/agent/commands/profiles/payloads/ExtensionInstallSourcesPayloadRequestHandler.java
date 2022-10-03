package com.me.mdm.chrome.agent.commands.profiles.payloads;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class ExtensionInstallSourcesPayloadRequestHandler extends PayloadRequestHandler
{
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            final JSONArray extensionInstallSourceURLs = payloadData.optJSONArray("AllowedUrls");
            final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
            this.logger.info("Going to install Extension install source Payload : " + payloadData + "\n:" + extensionInstallSourceURLs + "\n" + payloadIdentifierName);
            if (extensionInstallSourceURLs.length() > 0) {
                ChromeDeviceManager.getInstance().getExtensionInstallSourceManager().addPayloadIdentifierToDB(context, payloadIdentifierName);
                new MDMAgentParamsTableHandler(context).addJSONArray(payloadIdentifierName, extensionInstallSourceURLs);
                ChromeDeviceManager.getInstance().getExtensionInstallSourceManager().applyExtensionInstallSources(context, payloadResp, true);
            }
        }
        catch (final Exception e) {
            this.logger.info("Exception while Applying Extension installSources" + e);
        }
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, modifyPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            this.logger.info("Going to Remove Extension Install Source payload");
            final Context context = request.getContainer().getContext();
            final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
            ChromeDeviceManager.getInstance().getExtensionInstallSourceManager().removePayloadData(context, payloadIdentifierName);
            ChromeDeviceManager.getInstance().getExtensionInstallSourceManager().applyExtensionInstallSources(context, payloadResp, false);
        }
        catch (final Exception ex) {
            payloadResp.setErrorCode(12132);
            payloadResp.setErrorMsg(((GoogleJsonResponseException)ex).getDetails().getMessage());
            this.logger.info("Exception while removing Profiles");
        }
    }
}
