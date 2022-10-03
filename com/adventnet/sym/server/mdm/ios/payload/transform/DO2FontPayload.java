package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.mac.FontPayload;
import com.me.mdm.server.profiles.font.FontDetailsHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2FontPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloads = { null };
        try {
            final Row fontRow = dataObject.getRow("FontDetails");
            final String name = (String)fontRow.get("NAME");
            final String filePath = new FontDetailsHandler().getFontFilePath(fontRow);
            final FontPayload payload = new FontPayload(1, "MDM", "com.apple.fonts", "Font payload");
            payload.setFontData(filePath);
            payload.setFontName(name);
            payloads[0] = payload;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in font payload", e);
        }
        return payloads;
    }
}
