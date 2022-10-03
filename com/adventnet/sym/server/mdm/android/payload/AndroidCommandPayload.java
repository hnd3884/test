package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

public class AndroidCommandPayload extends AndroidPayload
{
    String commandUUID;
    
    public AndroidCommandPayload() throws JSONException {
        this.commandUUID = null;
        this.commandUUID = UUID.randomUUID().toString();
        final JSONObject commandJSON = new JSONObject();
        this.getPayloadJSON().put("CommandUUID", (Object)this.commandUUID);
        this.getPayloadJSON().put("Command", (Object)commandJSON);
    }
    
    protected JSONObject getCommandJSON() throws JSONException {
        return (JSONObject)this.getPayloadJSON().get("Command");
    }
    
    public void setRequestType(final String requestType) throws JSONException {
        this.getCommandJSON().put("RequestType", (Object)requestType);
        this.commandUUID = requestType + ";" + this.commandUUID;
        this.getPayloadJSON().put("CommandUUID", (Object)this.commandUUID);
    }
    
    public void setRequestData(final Object requestData) throws JSONException {
        this.getCommandJSON().put("RequestData", requestData);
    }
    
    public Object getRequestData() throws JSONException {
        return this.getCommandJSON().get("RequestData");
    }
    
    public String getCommandUUID() {
        return this.commandUUID;
    }
    
    public void setCommandUUID(final String commandUUID) throws JSONException {
        final String requestType = (String)this.getCommandJSON().get("RequestType");
        if (requestType != null) {
            this.commandUUID = requestType + ";" + commandUUID;
        }
        this.getPayloadJSON().put("CommandUUID", (Object)this.commandUUID);
    }
    
    public void setCommandUUID(final String commandUUID, final boolean appendReqType) throws JSONException {
        if (appendReqType) {
            this.setCommandUUID(commandUUID);
        }
        else {
            this.getPayloadJSON().put("CommandUUID", (Object)commandUUID);
        }
    }
    
    public void setScope(final int scope) throws JSONException {
        if (scope == 1) {
            this.getPayloadJSON().put("CommandScope", (Object)"container");
        }
        else if (scope == -1) {
            this.getPayloadJSON().put("CommandScope", (Object)"%scope%");
        }
        else {
            this.getPayloadJSON().put("CommandScope", (Object)"device");
        }
    }
    
    public void setCommandVersion(final String commandVersion) throws JSONException {
        this.getPayloadJSON().put("CommandVersion", (Object)commandVersion);
    }
}
