package com.me.mdm.server.windows.profile.payload;

import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WindowsAppConfigPayload extends WindowsPayload
{
    String keyPrefix;
    Logger logger;
    
    public WindowsAppConfigPayload(final JSONObject params) {
        this.keyPrefix = null;
        this.logger = Logger.getLogger("MDMConfigLogger");
        final String packageFamilyName = params.optString("packageFamilyName");
        final String type = params.optString("type", "nonStore");
        this.keyPrefix = "./User/Vendor/MSFT/EnterpriseModernAppManagement/AppManagement/" + type + "/" + packageFamilyName + "/AppSettingPolicy/";
    }
    
    public void setConfiguration(final JSONArray configJSONArray) {
        try {
            this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix.substring(0, this.keyPrefix.length() - 1)));
            for (int i = 0; i < configJSONArray.length(); ++i) {
                final JSONObject configJsonTemp = configJSONArray.getJSONObject(i);
                final String key = this.keyPrefix + configJsonTemp.get("key");
                final String value = String.valueOf(configJsonTemp.get("value"));
                this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(key, value, "chr"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in savingAppConfiguration ", e);
        }
    }
}
