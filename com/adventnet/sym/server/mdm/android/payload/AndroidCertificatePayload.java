package com.adventnet.sym.server.mdm.android.payload;

import com.dd.plist.NSData;
import org.json.JSONException;

public class AndroidCertificatePayload extends AndroidPayload
{
    public AndroidCertificatePayload() {
    }
    
    public AndroidCertificatePayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "InstallCertificate", payloadIdentifier, payloadDisplayName);
    }
    
    public void setCertificateFileName(final String certificateFileName) throws JSONException {
        this.getPayloadJSON().put("CertificateFileName", (Object)certificateFileName);
    }
    
    public void setPayloadContent(final NSData data) throws JSONException {
        this.getPayloadJSON().put("PayloadContent", (Object)data);
    }
    
    public void setPayloadType(final String type) throws JSONException {
        this.getPayloadJSON().put("PayloadType", (Object)type);
    }
    
    public void setPassword(final String password) throws JSONException {
        this.getPayloadJSON().put("Password", (Object)password);
    }
    
    public void setCertificateContent(final String certificateContent) throws JSONException {
        this.getPayloadJSON().put("CertificateContent", (Object)certificateContent);
    }
    
    public void setPayloadDisplayName(final String displayName) throws JSONException {
        this.getPayloadJSON().put("PayloadDisplayName", (Object)displayName);
    }
    
    public void setCertificateType(final String certficateType) throws JSONException {
        this.getPayloadJSON().put("CertificateType", (Object)certficateType);
    }
}
