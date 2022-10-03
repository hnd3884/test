package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2LoginWindowSettingPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloadArray = new IOSPayload[4];
        try {
            payloadArray[0] = new DO2MacLoginWindowPayload().createPayload(dataObject)[0];
            payloadArray[1] = new DO2MacMcxPayload().createPayload(dataObject)[0];
            payloadArray[2] = new DO2MacGlobalPreferencesPayload().createPayload(dataObject)[0];
            payloadArray[3] = new DO2MacScreenSaverPayload().createPayload(dataObject)[0];
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in login window setting", ex);
        }
        return payloadArray;
    }
}
