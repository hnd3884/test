package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class ManagedGuestSessionPayloadHandler extends PayloadRequestHandler
{
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        final Context context = request.getContainer().getContext();
        final JSONObject payloadData = payloadReq.getPayloadData();
        ChromeDeviceManager.getInstance().getPublicSessionManager().setManagedGuestSession(context, payloadData, payloadResp);
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, modifyPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        final Context context = request.getContainer().getContext();
        ChromeDeviceManager.getInstance().getPublicSessionManager().revertManagedGuestSession(context, payloadResp);
    }
}
