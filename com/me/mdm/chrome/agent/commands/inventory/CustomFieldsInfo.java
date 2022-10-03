package com.me.mdm.chrome.agent.commands.inventory;

import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.Context;

public class CustomFieldsInfo extends InventoryInfo
{
    public CustomFieldsInfo(final Context context) throws IOException {
        super(context);
    }
    
    @Override
    public JSONObject fetchInfo(final JSONObject inventoryInfo) throws Throwable {
        final JSONObject deviceInfo = this.getCustomerFieldsInfo();
        inventoryInfo.put("CustomFieldsDetails", (Object)deviceInfo);
        return inventoryInfo;
    }
}
