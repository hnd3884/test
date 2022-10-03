package com.me.mdm.server.profiles.config;

import java.util.List;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class ChromeKioskConfigHandler extends DefaultConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        JSONArray result = null;
        try {
            if (!dataObject.isEmpty()) {
                result = super.DOToAPIJSON(dataObject, configName, tableName);
                Iterator iterator = dataObject.getRows("MdAppGroupDetails");
                final List<Long> appGroupIDList = new ArrayList<Long>();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    appGroupIDList.add((Long)row.get("APP_GROUP_ID"));
                }
                final JSONObject appDetailsJsonObj = new MDMAppMgmtHandler().getAppInformation(appGroupIDList);
                final JSONArray appDetailsJsonArray = new JSONArray();
                iterator = appDetailsJsonObj.keys();
                while (iterator.hasNext()) {
                    final String key = iterator.next();
                    final JSONObject appDetails = appDetailsJsonObj.getJSONObject(key);
                    final JSONObject appDetailsLowerCase = new JSONObject();
                    final Iterator appDetailItr = appDetails.keys();
                    while (appDetailItr.hasNext()) {
                        final String appDetailsKey = appDetailItr.next();
                        if (appDetailsKey.equals("APP_GROUP_ID")) {
                            appDetailsLowerCase.put("app_id", appDetails.get(appDetailsKey));
                        }
                        else {
                            appDetailsLowerCase.put(appDetailsKey.toLowerCase(), appDetails.get(appDetailsKey));
                        }
                    }
                    appDetailsJsonArray.put((Object)appDetailsLowerCase);
                }
                result.getJSONObject(0).put("allowed_apps", (Object)appDetailsJsonArray);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in Chrome Kiosk Config", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
}
