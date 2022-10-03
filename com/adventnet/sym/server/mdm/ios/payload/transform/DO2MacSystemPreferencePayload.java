package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.dd.plist.NSArray;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.ios.payload.MacSystemPreferencePayload;
import com.me.mdm.server.profiles.config.MacSystemPreferenceConfigHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2MacSystemPreferencePayload implements DO2Payload
{
    private Logger logger;
    
    public DO2MacSystemPreferencePayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final JSONObject payloadDetails = new MacSystemPreferenceConfigHandler().DOToAPIJSON(dataObject, "macsystempreference").getJSONObject(0);
            final MacSystemPreferencePayload payload = new MacSystemPreferencePayload(1, "MDM", "com.manageengine.systempreference.profile", "macOS System preference", "com.apple.systempreferences");
            final MacSystemPreferencePayload[] payloadArray = { null };
            final Map<String, String> preferencePayloadKeyMap = new HashMap<String, String>() {
                {
                    this.put("ENABLED_PREFERENCES", "EnabledPreferencePanes");
                    this.put("DISABLED_PREFERENCES", "DisabledPreferencePanes");
                    this.put("HIDDEN_PREFERENCES", "HiddenPreferencePanes");
                }
            };
            for (final String key : preferencePayloadKeyMap.keySet()) {
                if (payloadDetails.has(key)) {
                    final JSONArray preferences = payloadDetails.getJSONArray(key);
                    if (preferences.length() <= 0) {
                        continue;
                    }
                    payload.setPreferencePaneArray(preferencePayloadKeyMap.get(key), this.getNSArrayFromStringJSONArray(preferences));
                }
            }
            payloadArray[0] = payload;
            return payloadArray;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to convert dataObject to payload for macOS System preference", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private NSArray getNSArrayFromStringJSONArray(final JSONArray array) {
        final NSArray nsArray = new NSArray(array.length());
        for (int i = 0; i < array.length(); ++i) {
            nsArray.setValue(i, (Object)array.getString(i));
        }
        return nsArray;
    }
}
