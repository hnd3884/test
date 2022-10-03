package com.adventnet.sym.server.mdm.safe.payload;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;

public class SafeActiveSyncPayload extends AndroidPayload
{
    public SafeActiveSyncPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
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
        this.getPayloadJSON().put("SyncNotes", syncNotes);
    }
    
    public void setMailNumberOfPastDaysToSync(final int days) throws JSONException {
        this.getPayloadJSON().put("MailNumberOfPastDaysToSync", days);
    }
    
    public void setUseSSL(final boolean useSSL) throws JSONException {
        this.getPayloadJSON().put("SSL", useSSL);
    }
    
    public void setCertificate(final byte[] certificate) throws JSONException {
        this.getPayloadJSON().put("Certificate", (Object)certificate);
    }
    
    public void setCertificatePassword(final String password) throws JSONException {
        this.getPayloadJSON().put("CertificatePassword", (Object)password);
    }
}
