package com.me.mdm.agent.handlers;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;

public class DeviceMessageRequest
{
    public String devicePlatform;
    public String udid;
    public String messageType;
    public String messageVersion;
    public JSONObject messageRequest;
    public Long resourceID;
    
    public DeviceMessageRequest(final JSONObject queueMessageData) throws JSONException {
        this.devicePlatform = null;
        this.udid = null;
        this.messageType = null;
        this.messageVersion = null;
        this.messageRequest = null;
        this.resourceID = null;
        this.udid = String.valueOf(queueMessageData.get("UDID"));
        if (queueMessageData.has("MessageType")) {
            this.messageType = String.valueOf(queueMessageData.get("MessageType"));
            this.messageVersion = queueMessageData.optString("MessageVersion");
            this.devicePlatform = queueMessageData.optString("DevicePlatform");
            this.messageRequest = queueMessageData.optJSONObject("MessageRequest");
            if (this.messageRequest == null) {
                this.messageRequest = queueMessageData.optJSONObject("Message");
            }
        }
        else {
            this.messageType = String.valueOf(queueMessageData.get("MsgRequestType"));
            this.messageVersion = queueMessageData.optString("MsgVersion");
            this.devicePlatform = queueMessageData.optString("DevicePlatform");
            this.messageRequest = queueMessageData.optJSONObject("MsgRequest");
        }
        this.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.udid);
    }
}
