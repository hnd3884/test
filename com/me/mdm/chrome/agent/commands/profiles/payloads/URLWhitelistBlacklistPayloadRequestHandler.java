package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class URLWhitelistBlacklistPayloadRequestHandler extends PayloadRequestHandler
{
    public static final String APPLIED_PAYLOAD_IDENTIFIER = "AppliedPayloadIdentifier";
    public static final String ENABLE_BOOLMARKS = "EnableBookmarks";
    
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
            final boolean isBookMarksEnabled = payloadData.optBoolean("EnableBookmarks", false);
            final boolean isEditBookMarksAllowed = payloadData.optBoolean("IsEditBookmarksAllowed", false);
            ChromeDeviceManager.getInstance().getURLFilterManager().addPayloadIdentifierToDB(context, payloadIdentifierName);
            new MDMAgentParamsTableHandler(context).addJSONObject(payloadIdentifierName, payloadData);
            this.logger.log(Level.INFO, "Going to apply webcontent Filter Payload ");
            ChromeDeviceManager.getInstance().getURLFilterManager().applyURLFilterPolicy(context);
            if (isBookMarksEnabled) {
                ChromeDeviceManager.getInstance().getBookmarkManager().enableBookmarksBar(context, isEditBookMarksAllowed, payloadResp);
                final JSONArray jsonArray = payloadData.getJSONArray("WhitelistedURLs");
                ChromeDeviceManager.getInstance().getBookmarkManager().addPayloadIdentifierToDB(context, payloadIdentifierName);
                new MDMAgentParamsTableHandler(context).addJSONArray(payloadIdentifierName, jsonArray);
                ChromeDeviceManager.getInstance().getBookmarkManager().setBookmarkData(context, payloadResp, true);
            }
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
            final Context context = request.getContainer().getContext();
            final String payloadIdentifierName = payloadReq.getPayloadIdentifier();
            ChromeDeviceManager.getInstance().getURLFilterManager().removePayloadData(context, payloadIdentifierName);
            ChromeDeviceManager.getInstance().getBookmarkManager().removePayloadData(context, payloadIdentifierName);
            ChromeDeviceManager.getInstance().getURLFilterManager().revertURLFilterPolicy(context);
            ChromeDeviceManager.getInstance().getBookmarkManager().setBookmarkData(context, payloadResp, false);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
}
