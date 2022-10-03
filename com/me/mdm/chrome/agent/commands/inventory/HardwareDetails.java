package com.me.mdm.chrome.agent.commands.inventory;

import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.Context;

public class HardwareDetails extends InventoryInfo
{
    public HardwareDetails(final Context context) throws IOException {
        super(context);
    }
    
    @Override
    public JSONObject fetchInfo(final JSONObject inventoryInfo) throws Throwable {
        JSONObject deviceInfo = this.getDeviceInfo();
        deviceInfo = this.fetchDiskInfo(deviceInfo);
        inventoryInfo.put("DeviceDetails", (Object)deviceInfo);
        return inventoryInfo;
    }
}
