package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.EMailPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2EmailPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2EmailPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        EMailPayload payload = null;
        final EMailPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("EMailPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String emailAccountDescription = (String)row.get("ACCOUNT_NAME");
                String emailAccountType = (String)row.get("ACCOUNT_TYPE");
                final String incomingMailServerIMAPPathPrefix = (String)row.get("ACCOUNT_PATH_PREFIX");
                final String emailAccountName = (String)row.get("ACCOUNT_USER_NAME");
                final String emailAddress = (String)row.get("MAIL_ADDRESS");
                final boolean preventMove = (boolean)row.get("PREVENT_MOVE");
                final boolean disableMailSync = (boolean)row.get("DISABLE_MAIL_RECENTS_SYNCING");
                final String incomingMailServerHostName = (String)row.get("INCOMING_SERVER_HOSTNAME");
                Integer incomingMailServerPortNumber = (Integer)row.get("INCOMING_SERVER_PORT");
                final String incomingMailServerUsername = (String)row.get("INCOMING_SERVER_USERNAME");
                final String incomingMailServerAuthentication = (String)row.get("INCOMING_SERVER_AUTH");
                final boolean incomingMailServerUseSSL = (boolean)row.get("INCOMING_SERVER_USE_SSL");
                final String outgoingMailServerHostName = (String)row.get("OUTGOING_SERVER_HOSTNAME");
                Integer outgoingMailServerPortNumber = (Integer)row.get("OUTGOING_SERVER_PORT");
                final String outgoingMailServerUsername = (String)row.get("OUTGOING_SERVER_USERNAME");
                final String outgoingMailServerAuthentication = (String)row.get("OUTGOING_SERVER_AUTH");
                final boolean outgoingPasswordSameAsIncomingPassword = (boolean)row.get("OUTGOING_PWD_AS_INCOME_PWD");
                final boolean preventAppSheet = (boolean)row.get("USE_ONLY_MAIL_APP");
                final boolean outgoingMailServerUseSSL = (boolean)row.get("OUTGOING_SERVER_USE_SSL");
                final boolean sMIMEEnabled = (boolean)row.get("USE_MIME_ENCRYPT");
                String incomingPassword = "";
                String outgoingPassword = "";
                if (row.get("INCOMING_PASSWORD_ID") != null) {
                    final Long incomingServerPasswordId = (Long)row.get("INCOMING_PASSWORD_ID");
                    incomingPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(incomingServerPasswordId.toString());
                }
                if (row.get("OUTGOING_PASSWORD_ID") != null) {
                    final Long outgoingPasswordID = (Long)row.get("OUTGOING_PASSWORD_ID");
                    outgoingPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(outgoingPasswordID.toString());
                }
                payload = new EMailPayload(1, "MDM", "com.mdm.mobiledevice.email", "Email Policy");
                payload.setAccountDescription(emailAccountDescription);
                payload.setAccountDisplayName(emailAccountName);
                if (emailAccountType != null && emailAccountType.equalsIgnoreCase("POP")) {
                    emailAccountType = "EmailTypePOP";
                }
                else if (emailAccountType != null && emailAccountType.equalsIgnoreCase("IMAP")) {
                    emailAccountType = "EmailTypeIMAP";
                }
                payload.setAccountType(emailAccountType);
                String incomingAuthType = null;
                if (incomingMailServerAuthentication.equalsIgnoreCase("none")) {
                    incomingAuthType = "EmailAuthNone";
                }
                else if (incomingMailServerAuthentication.equalsIgnoreCase("password")) {
                    incomingAuthType = "EmailAuthPassword";
                }
                else if (incomingMailServerAuthentication.equalsIgnoreCase("md5")) {
                    incomingAuthType = "EmailAuthCRAMMD5";
                }
                else if (incomingMailServerAuthentication.equalsIgnoreCase("ntlm")) {
                    incomingAuthType = "EmailAuthNTLM";
                }
                else if (incomingMailServerAuthentication.equalsIgnoreCase("htpmd5")) {
                    incomingAuthType = "EmailAuthHTTPMD5";
                }
                payload.setIncomingMailServerAuthentication(incomingAuthType);
                if (emailAddress != null) {
                    payload.setEMailAddress(emailAddress.trim());
                }
                payload.setIncomingMailServerHostName(incomingMailServerHostName.trim());
                if (incomingMailServerIMAPPathPrefix != null && !incomingMailServerIMAPPathPrefix.isEmpty()) {
                    payload.setIncomingMailServerIMAPPathPrefix(incomingMailServerIMAPPathPrefix);
                }
                if (incomingMailServerPortNumber == -1) {
                    incomingMailServerPortNumber = 143;
                }
                payload.setIncomingMailServerPortNumber(incomingMailServerPortNumber);
                payload.setIncomingMailServerUseSSL(incomingMailServerUseSSL);
                payload.setIncomingMailServerUsername(incomingMailServerUsername);
                payload.setIncomingPassword(incomingPassword);
                String outGoingAuthType = null;
                if (outgoingMailServerAuthentication.equalsIgnoreCase("none")) {
                    outGoingAuthType = "EmailAuthNone";
                }
                else if (outgoingMailServerAuthentication.equalsIgnoreCase("password")) {
                    outGoingAuthType = "EmailAuthPassword";
                }
                else if (outgoingMailServerAuthentication.equalsIgnoreCase("md5")) {
                    outGoingAuthType = "EmailAuthCRAMMD5";
                }
                else if (outgoingMailServerAuthentication.equalsIgnoreCase("ntlm")) {
                    outGoingAuthType = "EmailAuthNTLM";
                }
                else if (outgoingMailServerAuthentication.equalsIgnoreCase("htpmd5")) {
                    outGoingAuthType = "EmailAuthHTTPMD5";
                }
                payload.setOutgoingMailServerAuthentication(outGoingAuthType);
                payload.setOutgoingMailServerHostName(outgoingMailServerHostName.trim());
                if (outgoingMailServerPortNumber == -1) {
                    outgoingMailServerPortNumber = 587;
                }
                payload.setOutgoingMailServerPortNumber(outgoingMailServerPortNumber);
                payload.setOutgoingMailServerUseSSL(outgoingMailServerUseSSL);
                payload.setOutgoingMailServerUsername(outgoingMailServerUsername);
                if (outgoingPassword != null) {
                    payload.setOutgoingPassword(outgoingPassword);
                }
                payload.setOutgoingPasswordSameAsIncomingPassword(outgoingPasswordSameAsIncomingPassword);
                payload.setPreventMove(preventMove);
                payload.setDisableMailSync(disableMailSync);
                payload.setUseOnlyMailApp(preventAppSheet);
                payload.setSMIMEEnabled(sMIMEEnabled);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating email payload.", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
}
