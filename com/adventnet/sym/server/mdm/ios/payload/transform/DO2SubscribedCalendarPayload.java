package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.SubscribedCalendarsPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2SubscribedCalendarPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2SubscribedCalendarPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        SubscribedCalendarsPayload payload = null;
        final SubscribedCalendarsPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("SubscibedCalendarPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String subCalAccountDescription = (String)row.get("ACCOUNT_DESCRIPTION");
                final String subCalAccountHostName = (String)row.get("CALENDAR_URL");
                final String subCalAccountUsername = (String)row.get("ACCOUNT_USERNAME");
                final boolean subCalAccountUseSSL = (boolean)row.get("USE_SSL");
                String subCalAccountPassword = "";
                if (row.get("ACCOUNT_PASSWORD_ID") != null) {
                    final Long subCalAccountPasswordId = (Long)row.get("ACCOUNT_PASSWORD_ID");
                    subCalAccountPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(subCalAccountPasswordId.toString());
                }
                payload = new SubscribedCalendarsPayload(1, "MDM", "com.mdm.mobiledevice.carddav", "Subscribed Calendar Policy");
                payload.setSubCalAccountDescription(subCalAccountDescription);
                payload.setSubCalAccountHostName(subCalAccountHostName.trim());
                payload.setSubCalAccountPassword(subCalAccountPassword);
                payload.setSubCalAccountPassword(subCalAccountPassword);
                payload.setSubCalAccountUsername(subCalAccountUsername);
                payload.setSubCalAccountUseSSL(subCalAccountUseSSL);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while publishing subcribed calender", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
}
