package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacScreenSaverPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacScreenSaverPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("MacScreenSaverSettings");
            while (iterator.hasNext()) {
                final Row screenSaverRow = iterator.next();
                final Integer windowIdleTime = (Integer)screenSaverRow.get("WINDOW_IDLE_TIME");
                final String modulePath = (String)screenSaverRow.get("MODULE_PATH");
                final Boolean askForPassword = (Boolean)screenSaverRow.get("ASK_FOR_PASSWORD");
                final Integer askForPasswordDelay = (Integer)screenSaverRow.get("PASSWORD_DELAY");
                final MacScreenSaverPayload screenSaverPayload = new MacScreenSaverPayload(1, "MDM", "com.apple.screensaver", "Screen Saver Config");
                screenSaverPayload.setLoginWindowIdleTime(windowIdleTime);
                if (MDMStringUtils.isEmpty(modulePath)) {
                    screenSaverPayload.setModulePath(modulePath);
                }
                screenSaverPayload.setAskForPassword(askForPassword);
                if (askForPassword) {
                    screenSaverPayload.setAskForPasswordDelay(askForPasswordDelay);
                }
                payloadArray[0] = screenSaverPayload;
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in screensaver", e);
        }
        return payloadArray;
    }
}
