package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.LdapPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2LdapPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2LdapPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        LdapPayload ldapPayload = null;
        final LdapPayload[] payloadArray = { null };
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("LdapPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String description = (String)payloadRow.get("ACCOUNT_DISPLAY_NAME");
                    final String userName = (String)payloadRow.get("ACCOUNT_USERNAME");
                    final String hostName = (String)payloadRow.get("ACCOUNT_HOSTNAME");
                    final Boolean isSSL = (Boolean)payloadRow.get("USE_SSL");
                    String password = "";
                    if (payloadRow.get("ACCOUNT_PASSWORD_ID") != null) {
                        final Long ldapPasswordID = (Long)payloadRow.get("ACCOUNT_PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(ldapPasswordID.toString());
                    }
                    ldapPayload = new LdapPayload(1, "MDM", "com.mdm.mobiledevice.ldap", "LDAP Profile Configuration");
                    if (description != null && !description.equalsIgnoreCase("--")) {
                        ldapPayload.setLdapAccountDescription(description);
                    }
                    if (userName != null && !userName.equalsIgnoreCase("--")) {
                        ldapPayload.setLdapAccountUserName(userName);
                    }
                    if (hostName != null && !hostName.equalsIgnoreCase("--")) {
                        ldapPayload.setLdapAccountHostName(hostName);
                    }
                    if (password != null && !password.equalsIgnoreCase("--")) {
                        ldapPayload.setLdapAccountPassword(password);
                    }
                    ldapPayload.setLdapAccountUseSSL(isSSL);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in LDAP payload", exp);
        }
        payloadArray[0] = ldapPayload;
        return payloadArray;
    }
}
