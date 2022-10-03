package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacLoginWindowItemSettingPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacLoginWindowItemSettingPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] payloads = { null };
        try {
            final int size = dataObject.size("MacLoginWindowItems");
            if (size > 0) {
                payloads = new IOSPayload[] { null, new DO2MacLoginWindowItemPayload().createPayload(dataObject)[0] };
            }
            final Row loginItemSettingRow = dataObject.getRow("MacLoginWindowItemSettings");
            final boolean suppression = (boolean)loginItemSettingRow.get("DISABLE_LOGIN_ITEMS_SUPPRESSION");
            final MacLoginWindowItemSettingPayload itemSettingPayload = new MacLoginWindowItemSettingPayload(1, "MDM", "LoginWindow", "Login window setting");
            itemSettingPayload.setLoginItemSuppression(suppression);
            payloads[0] = itemSettingPayload;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in login window item", e);
        }
        return payloads;
    }
}
