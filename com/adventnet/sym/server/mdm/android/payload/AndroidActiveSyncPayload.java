package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;

public class AndroidActiveSyncPayload extends AndroidPayload
{
    public AndroidActiveSyncPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "ActiveSync", payloadIdentifier, payloadDisplayName);
    }
    
    public void setEMailAddress(final String emailAddress) throws JSONException {
        this.getPayloadJSON().put("EmailAddress", (Object)emailAddress);
    }
    
    public void setUserName(final String userName) throws JSONException {
        this.getPayloadJSON().put("UserName", (Object)userName);
    }
    
    public void setDomainName(final String domain) throws JSONException {
        this.getPayloadJSON().put("DomainName", (Object)domain);
    }
    
    public void setHost(final String host) throws JSONException {
        this.getPayloadJSON().put("Host", (Object)host);
    }
    
    public void setPassword(final String password) throws JSONException {
        this.getPayloadJSON().put("Password", (Object)password);
    }
    
    public void setAcceptAllCertificate(final boolean acceptCertificate) throws JSONException {
        this.getPayloadJSON().put("AcceptAllCertificate", acceptCertificate);
    }
    
    public void setDefault(final String defaultAcc) throws JSONException {
        this.getPayloadJSON().put("IsDefault", (Object)defaultAcc);
    }
    
    public void setSyncCalendar(final boolean syncCalendar) throws JSONException {
        this.getPayloadJSON().put("SyncCalendar", syncCalendar);
    }
    
    public void setSyncContacts(final boolean syncContacts) throws JSONException {
        this.getPayloadJSON().put("SyncContacts", syncContacts);
    }
    
    public void setSyncTasks(final boolean syncTasks) throws JSONException {
        this.getPayloadJSON().put("SyncTasks", syncTasks);
    }
    
    public void setSyncNotes(final boolean syncNotes) throws JSONException {
        this.getPayloadJSON().put("syncNotes", syncNotes);
    }
    
    public void setMailNumberOfPastDaysToSync(final int days) throws JSONException {
        this.getPayloadJSON().put("MailNumberOfPastDaysToSync", days);
    }
    
    public void setMaxMailToSync(final int days) throws JSONException {
        this.getPayloadJSON().put("MaxEmailAgeToSync", days);
    }
    
    public void setUseSSL(final boolean useSSL) throws JSONException {
        this.getPayloadJSON().put("SSL", useSSL);
    }
    
    public void setCertificate(final String certificate) throws JSONException {
        this.getPayloadJSON().put("Certificate", (Object)certificate);
    }
    
    public void setCertificatePassword(final String password) throws JSONException {
        this.getPayloadJSON().put("CertificatePassword", (Object)password);
    }
    
    public void setDisplayName(final String displayName) throws JSONException {
        this.getPayloadJSON().put("DisplayName", (Object)displayName);
    }
    
    public void setPastDaysToCalendarSync(final int days) throws JSONException {
        this.getPayloadJSON().put("PreviousCalenderDaysToSync", days);
    }
    
    public void setMaxCalendarToSync(final int days) throws JSONException {
        this.getPayloadJSON().put("MaxCalendarAgeToSync", days);
    }
    
    public void allowMailSettingModify(final boolean allowSettingModify) throws JSONException {
        this.getPayloadJSON().put("AllowMailSettingsChange", allowSettingModify);
    }
    
    public void allowMailForward(final boolean allowForward) throws JSONException {
        this.getPayloadJSON().put("AllowForward", allowForward);
    }
    
    public void allowHTMLContent(final boolean allowHTMLContent) throws JSONException {
        this.getPayloadJSON().put("AllowHTMLMail", allowHTMLContent);
    }
    
    public void setSyncSchedule(final int schedule) throws JSONException {
        this.getPayloadJSON().put("OffPeakSyncSchedule", schedule);
    }
    
    public void allowIncomingAttachments(final boolean allowIncomingAttachment) throws JSONException {
        this.getPayloadJSON().put("AllowIncomingAttachments", allowIncomingAttachment);
    }
    
    public void setIncomingAttachmentSize(final int size) throws JSONException {
        this.getPayloadJSON().put("IncomingAttachmentsMaxSize", size);
    }
    
    public void setPeakDays(final int peakDays) throws JSONException {
        this.getPayloadJSON().put("PeakDays", peakDays);
    }
    
    public void setPeakDayStartAt(final int startAt) throws JSONException {
        this.getPayloadJSON().put("PeakStartMinute", startAt);
    }
    
    public void setPeakDayEndsAt(final int endAt) throws JSONException {
        this.getPayloadJSON().put("PeakEndMinute", endAt);
    }
    
    public void setPeakDaySyncSchedule(final int schedule) throws JSONException {
        this.getPayloadJSON().put("PeakSyncSchedule", schedule);
    }
    
    public void setAsDefaultAccount(final boolean defaultAccount) throws JSONException {
        this.getPayloadJSON().put("IsDefault", defaultAccount);
    }
    
    public void setSignature(final String signature) throws JSONException {
        this.getPayloadJSON().put("Signature", (Object)signature);
    }
    
    public void allowNotifyOnEmail(final boolean notity) throws JSONException {
        this.getPayloadJSON().put("IsNotify", notity);
    }
    
    public void setViberate(final boolean viberate) throws JSONException {
        this.getPayloadJSON().put("VibrateOnEamil", viberate);
    }
    
    public void setRetrivalSize(final int size) throws JSONException {
        this.getPayloadJSON().put("EmailRetrivalSize", size);
    }
    
    public void setMaxEmailBodyTruncationSize(final int size) throws JSONException {
        this.getPayloadJSON().put("MaxEmailBodyTruncationSize", size);
    }
    
    public void setMaxMailHTMLBodyTruncationSize(final int size) throws JSONException {
        this.getPayloadJSON().put("MaxHTMLEmailBodyTruncationSize", size);
    }
    
    public void setRoamingSettings(final int roamingSetting) throws JSONException {
        this.getPayloadJSON().put("RoamingSyncSchedule", roamingSetting);
    }
    
    public void setPrimaryClientPref(final int primaryClientPref) throws JSONException {
        this.getPayloadJSON().put("PrimaryClientPref", primaryClientPref);
    }
    
    public void setSecondaryClientPref(final int secondaryClientPref) throws JSONException {
        this.getPayloadJSON().put("SecondaryClientPref", secondaryClientPref);
    }
    
    public void setEnrollType(final String type) throws JSONException {
        this.getPayloadJSON().put("ClientCertEnrollType", (Object)type);
    }
}
