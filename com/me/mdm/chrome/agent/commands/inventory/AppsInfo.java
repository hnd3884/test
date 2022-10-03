package com.me.mdm.chrome.agent.commands.inventory;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.Context;

public class AppsInfo extends InventoryInfo
{
    public AppsInfo(final Context context) throws IOException {
        super(context);
    }
    
    @Override
    public JSONObject fetchInfo(final JSONObject inventoryInfo) throws Throwable {
        final JSONObject wrapperJSON = new JSONObject();
        final JSONArray inventoryInfoArray = this.fetchAppsInfo(this.context);
        wrapperJSON.put("AppList", (Object)inventoryInfoArray);
        return inventoryInfo;
    }
}
