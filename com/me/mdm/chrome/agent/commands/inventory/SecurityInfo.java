package com.me.mdm.chrome.agent.commands.inventory;

import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.Context;

public class SecurityInfo extends InventoryInfo
{
    public SecurityInfo(final Context context) throws IOException {
        super(context);
    }
    
    @Override
    public JSONObject fetchInfo(final JSONObject inventoryInfo) throws Throwable {
        JSONObject securityInfo = new JSONObject();
        securityInfo = this.fetchSecurityInfo(this.context, securityInfo);
        inventoryInfo.put("SecurityDetails", (Object)securityInfo);
        return inventoryInfo;
    }
}
