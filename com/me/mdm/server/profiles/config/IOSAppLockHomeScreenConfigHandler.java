package com.me.mdm.server.profiles.config;

import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class IOSAppLockHomeScreenConfigHandler extends IOSAppLockConfigHandler
{
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (!dataObject.isEmpty() && configJSON.has("payload_id")) {
            super.checkAndAddInnerJSON(configJSON, dataObject, configName);
            final JSONArray configProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            new ScreenLayoutConfigHandler().checkAndAddInnerJSON(configJSON, dataObject, configName);
            final JSONObject screenLayoutConfigObject = this.getSubConfigProperties(configProperties, "ScreenLayoutSettings");
            final String screenLayoutSettingKey = screenLayoutConfigObject.getString("alias");
            final JSONArray screenSettingProperties = screenLayoutConfigObject.getJSONArray("properties");
            final String screenLayoutModelKey = this.getSubConfigProperties(screenSettingProperties, "SCREEN_MODEL_TYPE").getString("alias");
            if (configJSON.has(screenLayoutSettingKey)) {
                final JSONObject object = configJSON.getJSONObject(screenLayoutSettingKey);
                if (object.has(screenLayoutModelKey)) {
                    final JSONObject screenSettingObject = new JSONObject();
                    screenSettingObject.put(screenLayoutModelKey, object.get(screenLayoutModelKey));
                    configJSON.put(screenLayoutSettingKey, (Object)screenSettingObject);
                }
            }
        }
    }
}
