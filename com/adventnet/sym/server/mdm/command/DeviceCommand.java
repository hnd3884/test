package com.adventnet.sym.server.mdm.command;

import org.json.JSONObject;

public class DeviceCommand
{
    public String commandUUID;
    public String commandFilePath;
    public String commandType;
    public String commandStr;
    public int commandDataType;
    public int priority;
    public Boolean dynamicVariable;
    
    public DeviceCommand() {
        this.commandUUID = null;
        this.commandFilePath = null;
        this.commandType = null;
        this.commandStr = null;
        this.commandDataType = 1;
        this.priority = 100;
        this.dynamicVariable = Boolean.FALSE;
    }
    
    public String serializeToJsonString() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd_uuid", (Object)this.commandUUID);
        jsonObject.put("cmd_file_path", (Object)this.commandFilePath);
        jsonObject.put("cmd_type", (Object)this.commandType);
        jsonObject.put("cmd_str", (Object)this.commandStr);
        jsonObject.put("cmd_data_type", this.commandDataType);
        jsonObject.put("priority", this.priority);
        jsonObject.put("has_dy_var", (Object)this.dynamicVariable);
        return jsonObject.toString();
    }
    
    public static DeviceCommand fromJsonString(final String deviceCommandJsonString) {
        final JSONObject jsonObject = new JSONObject(deviceCommandJsonString);
        final DeviceCommand deviceCommand = new DeviceCommand();
        deviceCommand.commandUUID = jsonObject.optString("cmd_uuid", (String)null);
        deviceCommand.commandFilePath = jsonObject.optString("cmd_file_path", (String)null);
        deviceCommand.commandType = jsonObject.optString("cmd_type", (String)null);
        deviceCommand.commandStr = jsonObject.optString("cmd_str", (String)null);
        deviceCommand.commandDataType = jsonObject.optInt("cmd_data_type", 1);
        deviceCommand.priority = jsonObject.optInt("priority", 100);
        deviceCommand.dynamicVariable = jsonObject.optBoolean("has_dy_var", (boolean)Boolean.FALSE);
        return deviceCommand;
    }
}
