package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class ManagedBookmarkPayloadRequestHandler extends PayloadRequestHandler
{
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            final JSONArray jsonArray = payloadData.getJSONArray("Bookmarks");
            final boolean isEditingBookMarksAllowed = payloadData.optBoolean("IsEditBookmarksAllowed");
            final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
            ChromeDeviceManager.getInstance().getBookmarkManager().enableBookmarksBar(context, isEditingBookMarksAllowed, payloadResp);
            ChromeDeviceManager.getInstance().getBookmarkManager().addPayloadIdentifierToDB(context, payloadIdentifierName);
            new MDMAgentParamsTableHandler(context).addJSONArray(payloadIdentifierName, jsonArray);
            ChromeDeviceManager.getInstance().getBookmarkManager().setBookmarkData(context, payloadResp, true);
        }
        catch (final Exception ex) {
            payloadResp.setErrorCode(12132);
            payloadResp.setErrorMsg(((GoogleJsonResponseException)ex).getDetails().getMessage());
        }
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
            ChromeDeviceManager.getInstance().getBookmarkManager().removePayloadData(context, payloadIdentifierName);
            ChromeDeviceManager.getInstance().getBookmarkManager().setBookmarkData(context, payloadResp, false);
        }
        catch (final Exception ex) {
            payloadResp.setErrorCode(12132);
            payloadResp.setErrorMsg(((GoogleJsonResponseException)ex).getDetails().getMessage());
        }
    }
}
