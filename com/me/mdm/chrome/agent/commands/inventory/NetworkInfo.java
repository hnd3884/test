package com.me.mdm.chrome.agent.commands.inventory;

import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.Context;

public class NetworkInfo extends InventoryInfo
{
    public NetworkInfo(final Context context) throws IOException {
        super(context);
    }
    
    @Override
    public JSONObject fetchInfo(final JSONObject inventoryInfo) throws Throwable {
        JSONObject networkInfo = new JSONObject();
        networkInfo = this.fetchNetworkInfo(this.context, networkInfo);
        inventoryInfo.put("NetworkDetails", (Object)networkInfo);
        return inventoryInfo;
    }
}
