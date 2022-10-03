package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacGlobalPreferencesPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacGlobalPreferencesPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("MacLoginWindowSettings");
            while (iterator.hasNext()) {
                final Row loginWindowSettingRow = iterator.next();
                final boolean multipleSession = (boolean)loginWindowSettingRow.get("ALLOW_MULTIPLE_SESSION");
                final Integer autoDelay = (Integer)loginWindowSettingRow.get("AUTO_LOGOUT_DELAY");
                final MacGlobalPreferencesPayload globalPreferencesPayload = new MacGlobalPreferencesPayload(1, "MDM", ".GlobalPreferences", "Mac Global Preference payload");
                globalPreferencesPayload.setMultipleSessionEnabled(multipleSession);
                globalPreferencesPayload.setAutoLogout(autoDelay);
                payloadArray[0] = globalPreferencesPayload;
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in Global Preference", e);
        }
        return payloadArray;
    }
}
