package com.me.mdm.chrome.agent.core;

import java.util.logging.Level;
import org.json.JSONException;
import java.util.logging.Logger;
import org.json.JSONObject;

public class Response
{
    private JSONObject commandResponseObject;
    private JSONObject commandObject;
    Logger logger;
    
    public Response() throws Exception {
        this.commandResponseObject = null;
        this.commandObject = null;
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.commandResponseObject = new JSONObject();
        this.commandObject = new JSONObject();
    }
    
    public void setCommandUUID(final String commandUUID) throws JSONException {
        this.commandObject.put("CommandUUID", (Object)commandUUID);
    }
    
    public void setResponseType(final String responseCommand) throws JSONException {
        this.commandResponseObject.put("ResponseType", (Object)responseCommand);
    }
    
    public void setDeviceUDID(final String deviceUDID) throws JSONException {
        this.commandObject.put("UDID", (Object)deviceUDID);
    }
    
    public void setResponseData(final Object responseObject) throws JSONException {
        this.commandResponseObject.put("ResponseData", responseObject);
    }
    
    public JSONObject getResponseJSON() throws JSONException {
        this.commandObject.put("CommandResponse", (Object)this.commandResponseObject);
        return this.commandObject;
    }
    
    public void setCommandVersion(final Object commandVersion) throws JSONException {
        this.commandObject.put("CommandVersion", commandVersion);
    }
    
    public void setStatus(final String status) throws JSONException {
        this.commandObject.put("Status", (Object)status);
    }
    
    public void setState(final String state) throws JSONException {
        this.commandObject.put("State", (Object)state);
    }
    
    public void setRemarks(final String remarks) throws JSONException {
        this.commandObject.put("Remarks", (Object)remarks);
    }
    
    public String getRemarks() {
        return this.commandObject.optString("Remarks", (String)null);
    }
    
    public void setErrorCode(final int errorCode) {
        try {
            this.commandObject.put("Status", (Object)"Error");
            this.commandObject.put("ErrorCode", errorCode);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in updating error code in the command object {0}", exp.getMessage());
        }
    }
    
    public void setErrorMessage(final String errorMsg) throws JSONException {
        this.commandObject.put("ErrorMsg", (Object)errorMsg);
    }
    
    public void setPayloadStatus(final JSONObject jsonStatus) throws JSONException {
        this.commandObject.put("PayloadStatus", (Object)jsonStatus);
    }
    
    public void setScope(final String scope) throws JSONException {
        this.commandObject.put("CommandScope", (Object)scope);
    }
    
    public String getStatus() {
        String status = this.commandObject.optString("Status");
        if (status == null) {
            status = "Status";
        }
        return status;
    }
}
