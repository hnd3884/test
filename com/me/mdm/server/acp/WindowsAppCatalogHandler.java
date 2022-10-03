package com.me.mdm.server.acp;

import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;

public class WindowsAppCatalogHandler extends MDMAppCatalogHandler
{
    @Override
    protected JSONObject modifyManagedAppResponse(final JSONObject managedAppJSON, final DMDataSetWrapper dataSet) throws Exception {
        String background = (String)dataSet.getValue("IMG_BG");
        if (background == null || background.contains("transparent")) {
            background = "#0078d7";
        }
        managedAppJSON.put("AppImageBackground", (Object)background);
        return managedAppJSON;
    }
}
