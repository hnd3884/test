package com.adventnet.sym.server.mdm.command;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceMessage
{
    public String messageType;
    public JSONObject messageResponse;
    public JSONObject messageJson;
    public String status;
    public String messageVersion;
    
    public DeviceMessage() {
        this.messageJson = new JSONObject();
    }
    
    public JSONObject getMessageJSON() {
        return this.messageJson;
    }
    
    public void setMessageStatus(final String messageStatus) throws JSONException {
        this.status = messageStatus;
        this.getMessageJSON().put("Status", (Object)this.status);
    }
    
    public void setMessageType(final String messageType) throws JSONException {
        this.messageType = messageType;
        this.getMessageJSON().put("MessageType", (Object)messageType);
    }
    
    public void setMessageResponseJSON(final JSONObject messageResponseJSON) throws JSONException {
        this.messageResponse = messageResponseJSON;
        this.getMessageJSON().put("MessageResponse", (Object)this.messageResponse);
    }
}
