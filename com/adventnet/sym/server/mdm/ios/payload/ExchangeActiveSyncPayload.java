package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSData;

public class ExchangeActiveSyncPayload extends IOSPayload
{
    public ExchangeActiveSyncPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.eas.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAccountDisplayName(final String displayName) {
        this.getPayloadDict().put("PayloadDisplayName", (Object)displayName);
    }
    
    public void setExchangeServerHost(final String hostName) {
        this.getPayloadDict().put("Host", (Object)hostName);
    }
    
    public void setMailNumberOfPastDaysToSync(final int days) {
        this.getPayloadDict().put("MailNumberOfPastDaysToSync", (Object)days);
    }
    
    public void setPreventMove(final boolean moveEnabled) {
        this.getPayloadDict().put("PreventMove", (Object)moveEnabled);
    }
    
    public void setDisableMailSync(final boolean disableMailSync) {
        this.getPayloadDict().put("disableMailRecentsSyncing", (Object)disableMailSync);
    }
    
    public void useSSL(final boolean ssl) {
        this.getPayloadDict().put("SSL", (Object)ssl);
    }
    
    public void setPreventAppSheet(final boolean preventAppSheet) {
        this.getPayloadDict().put("PreventAppSheet", (Object)preventAppSheet);
    }
    
    public void setOauth(final boolean value) {
        this.getPayloadDict().put("OAuth", (Object)value);
    }
    
    public void setEMailAddress(final String emailAddress) {
        this.getPayloadDict().put("EmailAddress", (Object)emailAddress);
    }
    
    public void setPassword(final String password) {
        this.getPayloadDict().put("Password", (Object)password);
    }
    
    public void setUserName(final String userName) {
        this.getPayloadDict().put("UserName", (Object)userName);
    }
    
    public void setCertificate(final NSData data) {
        this.getPayloadDict().put("Certificate", (NSObject)data);
    }
    
    public void setPayloadCertificateUUID(final String payloadCertificateUUID) {
        this.getPayloadDict().put("PayloadCertificateUUID", (Object)payloadCertificateUUID);
    }
    
    public void setSMIMEEncryptionCertificateUUID(final String encryptionCertificateUUID) {
        this.getPayloadDict().put("SMIMEEncryptByDefault", (Object)true);
        this.getPayloadDict().put("SMIMEEncryptionCertificateUUID", (Object)encryptionCertificateUUID);
    }
    
    public void setSMIMESigningCertificateUUID(final String signingCertificateUUID) {
        this.getPayloadDict().put("SMIMESigningEnabled", (Object)true);
        this.getPayloadDict().put("SMIMESigningCertificateUUID", (Object)signingCertificateUUID);
    }
    
    public void setOAuthSignInUrl(final String url) {
        this.getPayloadDict().put("OAuthSignInURL", (Object)url);
    }
    
    public void setOAuthTokerequestUrl(final String url) {
        this.getPayloadDict().put("OAuthTokenRequestURL", (Object)url);
    }
    
    public void setEnableCalender(final boolean value) {
        this.getPayloadDict().put("EnableCalendars", (Object)value);
    }
    
    public void setEnableContacts(final boolean value) {
        this.getPayloadDict().put("EnableContacts", (Object)value);
    }
    
    public void setEnableMail(final boolean value) {
        this.getPayloadDict().put("EnableMail", (Object)value);
    }
    
    public void setEnableNotes(final boolean value) {
        this.getPayloadDict().put("EnableNotes", (Object)value);
    }
    
    public void setEnableReminder(final boolean value) {
        this.getPayloadDict().put("EnableReminders", (Object)value);
    }
    
    public void setRestrictCalender(final boolean value) {
        this.getPayloadDict().put("EnableCalendarsUserOverridable", (Object)value);
    }
    
    public void setRestrictContacts(final boolean value) {
        this.getPayloadDict().put("EnableContactsUserOverridable", (Object)value);
    }
    
    public void setRestrictMail(final boolean value) {
        this.getPayloadDict().put("EnableMailUserOverridable", (Object)value);
    }
    
    public void setRestrictNotes(final boolean value) {
        this.getPayloadDict().put("EnableNotesUserOverridable", (Object)value);
    }
    
    public void setRestrictReminder(final boolean value) {
        this.getPayloadDict().put("EnableRemindersUserOverridable", (Object)value);
    }
}
