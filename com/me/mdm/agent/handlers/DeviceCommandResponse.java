package com.me.mdm.agent.handlers;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class DeviceCommandResponse
{
    public String commandVersion;
    public String commandUUID;
    public String status;
    public String udid;
    public String responseType;
    public Long resourceID;
    public JSONObject responseData;
    
    public DeviceCommandResponse(final JSONObject queueCommandData) throws JSONException {
        this.commandVersion = null;
        this.commandUUID = null;
        this.status = null;
        this.udid = null;
        this.responseType = null;
        this.resourceID = null;
        this.responseData = null;
        this.commandVersion = queueCommandData.optString("CommandVersion");
        this.commandUUID = String.valueOf(queueCommandData.get("CommandUUID")).split(";")[0];
        this.status = String.valueOf(queueCommandData.get("Status"));
        this.udid = String.valueOf(queueCommandData.get("UDID"));
        final JSONObject responseJSON = queueCommandData.getJSONObject("CommandResponse");
        JSONUtil.getInstance();
        this.responseType = JSONUtil.optStringIgnoreKeyCase(responseJSON, "ResponseType");
        this.responseData = queueCommandData.getJSONObject("CommandResponse").optJSONObject("ResponseData");
        this.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.udid);
    }
}
