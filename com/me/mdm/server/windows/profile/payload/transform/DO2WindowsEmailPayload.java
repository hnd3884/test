package com.me.mdm.server.windows.profile.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WindowsEmailPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsEmailPayload extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsEmailPayload winEmailPayload = new WindowsEmailPayload();
        winEmailPayload.getReplacePayloadCommand().addRequestItem(winEmailPayload.createTargetItemTagElement("%email_payload_xml%"));
        WindowsEmailPayload emailPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("WpEmailPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String guid = (String)row.get("GUID");
                final String accountIconPath = (String)row.get("ACCOUNT_ICON_NAME");
                final String accountType = (String)row.get("ACCOUNT_TYPE");
                final String authName = (String)row.get("AUTH_NAME");
                final Integer authReq = (Integer)row.get("AUTH_REQ");
                String authSecret = "";
                final String domain = (String)row.get("DOMAIN");
                final Integer downloadDay = (Integer)row.get("DWN_DAY");
                final String inComingServer = (String)row.get("IN_SERVER");
                final Integer inComingServerPort = (Integer)row.get("IN_SERVER_PORT");
                final Integer linger = (Integer)row.get("LINGER");
                final String senderEmailName = (String)row.get("NAME");
                final String outGoingEmailServer = (String)row.get("OUT_SERVER");
                final Integer outServerPort = (Integer)row.get("OUT_SERVER_PORT");
                final String replyEmailAddress = (String)row.get("REPLY_ADDR");
                final String serviceName = (String)row.get("SERVICE_NAME");
                final String serviceType = (String)row.get("SERVICE_TYPE");
                final String smtpAltAuthName = (String)row.get("SMTP_ALT_AUTH_NAME");
                final String smtpAltDomain = (String)row.get("SMTP_ALT_DOMAIN");
                final Boolean smtpAlternativeEnabled = (Boolean)row.get("SMTP_ALT_ENABLED");
                final Boolean inComingServerUseSSL = (Boolean)row.get("USE_SSl_IN");
                final Boolean outGoingServerUseSSL = (Boolean)row.get("USE_SSL_OUT");
                String sUseSSLInComing = "0";
                String sUseSSLOutGoing = "0";
                String sSmtpAlternativeEnabled = "0";
                if (inComingServerUseSSL) {
                    sUseSSLInComing = "1";
                }
                if (outGoingServerUseSSL) {
                    sUseSSLOutGoing = "1";
                }
                if (smtpAlternativeEnabled) {
                    sSmtpAlternativeEnabled = "1";
                }
                emailPayload = new WindowsEmailPayload();
                if (guid != null) {
                    emailPayload.setGuid(guid);
                    emailPayload.setAccountType(accountType, guid);
                    emailPayload.setAuthName(authName, guid);
                    emailPayload.setAuthReq(authReq, guid);
                    if (row.get("AUTH_SECRET_ID") != null) {
                        final Long authSecretId = (Long)row.get("AUTH_SECRET_ID");
                        authSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(authSecretId.toString());
                    }
                    emailPayload.setAuthSecret(authSecret, guid);
                    emailPayload.setDomain(domain, guid);
                    emailPayload.setDownloadDay(downloadDay, guid);
                    emailPayload.setInServer(inComingServer + ":" + inComingServerPort, guid);
                    emailPayload.setLinger(linger, guid);
                    emailPayload.setSenderEmailName(senderEmailName, guid);
                    emailPayload.setOutServer(outGoingEmailServer + ":" + outServerPort, guid);
                    emailPayload.setReplyAddress(replyEmailAddress, guid);
                    emailPayload.setServiceName(serviceName, guid);
                    emailPayload.setServiceType(serviceType.toUpperCase(), guid);
                    emailPayload.setSmtpAltAuthName(smtpAltAuthName, guid);
                    emailPayload.setSmtpAltDomain(smtpAltDomain, guid);
                    emailPayload.setSmtpAltEnabled(sSmtpAlternativeEnabled, guid);
                    emailPayload.setInComingServerUseSSL(sUseSSLInComing, guid);
                    emailPayload.setOutGoingServerUseSSL(sUseSSLOutGoing, guid);
                    String smtpAltPassword = "";
                    if (row.get("SMTP_ALT_PASSWORD_ID") != null) {
                        final Long smtp_alt_password_id = (Long)row.get("SMTP_ALT_PASSWORD_ID");
                        smtpAltPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(smtp_alt_password_id.toString());
                    }
                    emailPayload.setSmtpAltPassword(smtpAltPassword, guid);
                }
            }
            this.packOsSpecificPayloadToXML(dataObject, emailPayload, "install", "WindowsEmail");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Email payload ", ex);
        }
        return winEmailPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsEmailPayload payload = new WindowsEmailPayload();
        payload.getDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%email_payload_xml%"));
        WindowsEmailPayload emailPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("WpEmailPolicy");
            while (iterator.hasNext()) {
                emailPayload = new WindowsEmailPayload();
                final Row row = iterator.next();
                final String guid = (String)row.get("GUID");
                if (guid != null) {
                    emailPayload.setRemoveProfilePayload(guid);
                }
            }
            this.packOsSpecificPayloadToXML(dataObject, emailPayload, "remove", "WindowsEmail");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Email remove payload ", ex);
        }
        return payload;
    }
}
