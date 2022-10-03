package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.CalDavPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2CalDavPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2CalDavPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final CalDavPayload[] payloadArray = { null };
        CalDavPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("CalDAVPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String calDAVAccountDescription = (String)row.get("ACCOUNT_NAME");
                final String calDAVHostName = (String)row.get("ACCOUNT_HOSTNAME");
                Integer calDAVPort = (Integer)row.get("ACCOUNT_PORT");
                final String calDAVPrincipalURL = (String)row.get("PRINCIPAL_URL");
                final String calDAVUsername = (String)row.get("ACCOUNT_USERNAME");
                final boolean calDAVUseSSL = (boolean)row.get("USE_SSL");
                String calDAVPassword = "";
                if (row.get("ACCOUNT_PASSWORD_ID") != null) {
                    final Long calDAVPasswordID = (Long)row.get("ACCOUNT_PASSWORD_ID");
                    calDAVPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(calDAVPasswordID.toString());
                }
                payload = new CalDavPayload(1, "MDM", "com.mdm.mobiledevice.caldav", "CalDAV Policy");
                payload.setCalDAVAccountDescription(calDAVAccountDescription);
                payload.setCalDAVHostName(calDAVHostName.trim());
                payload.setCalDAVPassword(calDAVPassword);
                if (calDAVPort == -1) {
                    if (calDAVUseSSL) {
                        calDAVPort = 8443;
                    }
                    else {
                        calDAVPort = 8008;
                    }
                }
                payload.setCalDAVPort(calDAVPort);
                payload.setCalDAVPrincipalURL(calDAVPrincipalURL);
                payload.setCalDAVUseSSL(calDAVUseSSL);
                payload.setCalDAVUsername(calDAVUsername);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating caldavpolicy", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
}
