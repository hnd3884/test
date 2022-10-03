package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidEmailPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidEmailPolicyPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidEmailPayload emailPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("AndroidEMailPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long configdataItem = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final String displyName = (String)row.get("ACCOUNT_NAME");
                final String emailAddress = (String)row.get("MAIL_ADDRESS");
                final String senderName = (String)row.get("SENDER_NAME");
                final boolean allowHTMLContent = (boolean)row.get("ALLOW_HTML_CONTENT");
                final boolean allowForward = (boolean)row.get("ALLOW_FORWARD");
                final boolean allowNotify = (boolean)row.get("ALLOW_NOTIFY");
                final boolean allowUserChangeSettings = (boolean)row.get("ALLOW_CHANGE_SETTING");
                final boolean defaultAccount = (boolean)row.get("DEFAULT_ACCOUNT");
                final String signature = (String)row.get("SIGNATURE");
                final String incomingProtocol = (String)row.get("INCOMING_PROTOCOL");
                final String incominHost = (String)row.get("INCOMING_SERVER_HOSTNAME");
                final int incominPort = (int)row.get("INCOMING_SERVER_PORT");
                final String incominUserName = (String)row.get("INCOMING_USER_NAME");
                final boolean incomingServerUseSSL = (boolean)row.get("INCOMING_SERVER_USE_SSL");
                final boolean incomingServerUseTLS = (boolean)row.get("INCOMING_SERVER_USE_TLS");
                final boolean incomingServerAcceptCertificate = (boolean)row.get("INCOMING_SERVER_CERTIFICATE");
                final String outgoingProtocol = (String)row.get("OUTGOING_PROTOCOL");
                final String ourgoingHost = (String)row.get("OUTGOING_SERVER_HOSTNAME");
                final int outgoingPort = (int)row.get("OUTGOING_SERVER_PORT");
                final String outgoingUserName = (String)row.get("OUTGOING_USER_NAME");
                final boolean outgoingServerUseSSL = (boolean)row.get("OUTGOING_SERVER_USE_SSL");
                final boolean outgoingServerUseTLS = (boolean)row.get("OUTGOING_SERVER_USE_TLS");
                final boolean outgoingServerAcceptCertificate = (boolean)row.get("OUTGOING_SERVER_CERTIFICATE");
                emailPayload = new AndroidEmailPayload("1.0", "com.mdm.mobiledevice.email", "Email Policy");
                emailPayload.setDisplyName(displyName);
                emailPayload.setEMailAddress(emailAddress);
                emailPayload.setSenderName(senderName);
                emailPayload.setAllowHTMLContent(allowHTMLContent);
                emailPayload.setAllowForward(allowForward);
                emailPayload.setNotify(allowNotify);
                emailPayload.setDefault(defaultAccount);
                emailPayload.setAllowUserToChangeSettings(allowUserChangeSettings);
                emailPayload.setSignature((signature != null) ? signature : "");
                emailPayload.setIncomingProtocol(incomingProtocol);
                emailPayload.setIncomingServerHost(incominHost);
                emailPayload.setIncomingServerPort(incominPort);
                emailPayload.setIncomingServerUserName(incominUserName);
                emailPayload.setIncomingServerUseSSL(incomingServerUseSSL);
                emailPayload.setIncomingServerUseTLS(incomingServerUseTLS);
                emailPayload.setIncomingServerAcceptAllCertificate(incomingServerAcceptCertificate);
                emailPayload.setOutgoingProtocol(outgoingProtocol);
                emailPayload.setOutgoingServerHost(ourgoingHost);
                emailPayload.setOutgoingServerPort(outgoingPort);
                emailPayload.setOutgoingServerUserName(outgoingUserName);
                emailPayload.setOutgoingServerUseSSL(outgoingServerUseSSL);
                emailPayload.setOutgoingServerUseTLS(outgoingServerUseTLS);
                emailPayload.setOutgoingServerAcceptAllCertificate(outgoingServerAcceptCertificate);
                String incomingServerPassword = "";
                if (row.get("INCOMING_SERVER_PASSWORD_ID") != null) {
                    final Long incomingServerPasswordId = (Long)row.get("INCOMING_SERVER_PASSWORD_ID");
                    incomingServerPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(incomingServerPasswordId.toString());
                }
                emailPayload.setIncomingServerPassword(incomingServerPassword);
                String outgoingServerPassword = "";
                if (row.get("OUTGOING_SERVER_PASSWORD_ID") != null) {
                    final Long outgoingServerPasswordId = (Long)row.get("OUTGOING_SERVER_PASSWORD_ID");
                    outgoingServerPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(outgoingServerPasswordId.toString());
                }
                emailPayload.setOutgoingServerPassword(outgoingServerPassword);
                emailPayload.setPayloadUUID(configdataItem);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2AndroidEmailPolicyPayload.class.getName()).log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return emailPayload;
    }
}
