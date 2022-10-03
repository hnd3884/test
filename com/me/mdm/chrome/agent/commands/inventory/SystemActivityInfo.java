package com.me.mdm.chrome.agent.commands.inventory;

import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.Context;

public class SystemActivityInfo extends InventoryInfo
{
    public SystemActivityInfo(final Context context) throws IOException {
        super(context);
    }
    
    @Override
    public JSONObject fetchInfo(final JSONObject inventoryInfo) throws Throwable {
        JSONObject systemActivityInfo = new JSONObject();
        systemActivityInfo = this.fetchSystemActivityInfo(this.context, systemActivityInfo);
        inventoryInfo.put("SystemActivityDetails", (Object)systemActivityInfo);
        return inventoryInfo;
    }
}
