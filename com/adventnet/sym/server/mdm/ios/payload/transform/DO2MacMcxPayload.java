package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacMcxPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacMcxPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("MacLoginWindowSettings");
            while (iterator.hasNext()) {
                final Row macLoginWindowSettingRow = iterator.next();
                final Boolean allowGuestAccount = (Boolean)macLoginWindowSettingRow.get("ALLOW_GUEST_ACCOUNT");
                final MacMcxPayload mcxPayload = new MacMcxPayload(1, "MDM", "com.apple.MCX", "Mac MCX Configuration");
                mcxPayload.setGuestAccountSetting(allowGuestAccount);
                payloadArray[0] = mcxPayload;
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in mcx payload", e);
        }
        return payloadArray;
    }
}
