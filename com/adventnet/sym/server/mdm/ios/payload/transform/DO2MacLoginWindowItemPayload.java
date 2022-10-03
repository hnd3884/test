package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacLoginWindowItemPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacLoginWindowItemPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloads = { null };
        try {
            final int size = dataObject.size("MacLoginWindowItems");
            final MacLoginWindowItemPayload windowItemPayload = new MacLoginWindowItemPayload(1, "MDM", "com.apple.loginitems.managed", "Mac Login Window Item");
            windowItemPayload.setLoginWindowArray(size);
            final Iterator iterator = dataObject.getRows("MacLoginWindowItems");
            int position = 0;
            while (iterator.hasNext()) {
                final Row macLoginWindowItem = iterator.next();
                final String path = (String)macLoginWindowItem.get("ITEM_PATH");
                final boolean hide = (boolean)macLoginWindowItem.get("HIDE_ITEM");
                windowItemPayload.setLoginWindowItem(position++, path, hide);
            }
            payloads[0] = windowItemPayload;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in login window item", e);
        }
        return payloads;
    }
}
