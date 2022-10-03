package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.CardDavPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2CardDavPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2CardDavPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        CardDavPayload payload = null;
        final CardDavPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("CardDAVPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String cardDAVAccountDescription = (String)row.get("ACCOUNT_DISPLAY_NAME");
                final String cardDAVHostName = (String)row.get("ACCOUNT_HOSTNAME");
                Integer cardDAVPort = (Integer)row.get("ACCOUNT_PORT");
                final String cardDAVPrincipalURL = (String)row.get("PRINCIPAL_URL");
                final String cardDAVUsername = (String)row.get("ACCOUNT_USERNAME");
                final boolean cardDAVUseSSL = (boolean)row.get("USE_SSL");
                String cardDAVPassword = "";
                if (row.get("ACCOUNT_PASSWORD_ID") != null) {
                    final Long cardDAVPasswordID = (Long)row.get("ACCOUNT_PASSWORD_ID");
                    cardDAVPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(cardDAVPasswordID.toString());
                }
                payload = new CardDavPayload(1, "MDM", "com.mdm.mobiledevice.carddav", "CardDAV Policy");
                payload.setCardDAVAccountDescription(cardDAVAccountDescription);
                payload.setCardDAVHostName(cardDAVHostName.trim());
                payload.setCardDAVPassword(cardDAVPassword);
                if (cardDAVPort == -1) {
                    if (cardDAVUseSSL) {
                        cardDAVPort = 8843;
                    }
                    else {
                        cardDAVPort = 8800;
                    }
                }
                payload.setCardDAVPort(cardDAVPort);
                payload.setCardDAVPrincipalURL(cardDAVPrincipalURL);
                payload.setCardDAVUseSSL(cardDAVUseSSL);
                payload.setCardDAVUsername(cardDAVUsername);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating carddav policy", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
}
