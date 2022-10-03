package com.me.mdm.server.profiles.config;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class AndroidKioskHomeScreenConfigHandler extends AndroidKioskConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        JSONArray result;
        try {
            result = super.DOToAPIJSON(dataObject, configName, tableName);
            final JSONObject configObject = result.getJSONObject(0);
            new ScreenLayoutConfigHandler().checkAndAddInnerJSON(configObject, dataObject, configName);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in Chrome Kiosk Config", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        super.validateServerJSON(serverJSON);
        if (serverJSON.has("ScreenLayout")) {
            final JSONObject screenLayoutObject = serverJSON.getJSONObject("ScreenLayout");
            if (screenLayoutObject.has("ScreenLayoutPageDetails")) {
                final JSONArray pages = screenLayoutObject.getJSONArray("ScreenLayoutPageDetails");
                for (int i = 0; i < pages.length(); ++i) {
                    final JSONObject pageObject = pages.getJSONObject(i);
                    final int pageType = pageObject.getInt("PAGE_TYPE");
                    if (pageType == 2) {
                        final JSONArray appsArray = pageObject.getJSONArray("ScreenPageLayout");
                        final JSONObject screenSetting = serverJSON.getJSONObject("ScreenLayoutSettings");
                        final int iconSize = screenSetting.getInt("ICON_SIZE");
                        if ((iconSize == 75 && appsArray.length() > 3) || appsArray.length() > 4) {
                            throw new APIHTTPException("COM0015", new Object[0]);
                        }
                    }
                }
            }
        }
    }
}
