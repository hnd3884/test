package com.me.mdm.chrome.agent.commands.profiles.payloads;

import org.json.JSONException;
import com.me.mdm.chrome.agent.commands.profiles.ONCPayload;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public abstract class ONCPayloadRequestHandler extends PayloadRequestHandler
{
    public Logger logger;
    
    public ONCPayloadRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final JSONObject payloadData = payloadReq.getPayloadData();
            final JSONObject oncData = payloadData.getJSONObject("PayloadData");
            this.removeConfigInONC(payloadReq.getExistingONCPayload(), String.valueOf(oncData.get("GUID")));
            this.addConfigToONCProfile(payloadReq.getExistingONCPayload(), oncData);
            payloadResp.setIsONCPayload(true);
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
        this.processInstallPayload(request, response, oldPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final JSONObject payloadData = payloadReq.getPayloadData();
            JSONObject oncData = payloadData.getJSONObject("PayloadData");
            final String guid = String.valueOf(oncData.get("GUID"));
            oncData = new JSONObject();
            oncData.put("Remove", true);
            oncData.put("GUID", (Object)guid);
            this.removeConfigInONC(payloadReq.getExistingONCPayload(), guid);
            payloadResp.setIsONCPayload(true);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    protected abstract void addConfigToONCProfile(final ONCPayload p0, final JSONObject p1) throws JSONException;
    
    protected abstract boolean removeConfigInONC(final ONCPayload p0, final String p1) throws JSONException;
}
