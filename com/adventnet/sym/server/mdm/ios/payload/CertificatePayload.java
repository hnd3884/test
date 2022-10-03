package com.adventnet.sym.server.mdm.ios.payload;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.dd.plist.NSObject;
import com.dd.plist.NSData;

public class CertificatePayload extends IOSPayload
{
    public CertificatePayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.security.pkcs12", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setPayloadCertificateFileName(final String certificateFileName) {
        this.getPayloadDict().put("PayloadCertificateFileName", (Object)certificateFileName);
    }
    
    public void setPayloadContent(final NSData data) {
        this.getPayloadDict().put("PayloadContent", (NSObject)data);
    }
    
    public void setPayloadType(final String type) {
        this.getPayloadDict().put("PayloadType", (Object)type);
    }
    
    public void setPassword(final String password) {
        this.getPayloadDict().put("Password", (Object)password);
    }
    
    public void setPayloadContent(final String certificateCompletePathWithName) {
        try {
            final byte[] b = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(certificateCompletePathWithName);
            this.setPayloadContent(b);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void setOtherAppAccess(final boolean appAccess) {
        this.getPayloadDict().put("AllowAllAppsAccess", (Object)appAccess);
    }
    
    public void setKeyExtractable(final boolean keyExtractable) {
        this.getPayloadDict().put("KeyIsExtractable", (Object)keyExtractable);
    }
    
    public void setPayloadContent(final byte[] certficateContent) {
        this.getPayloadDict().put("PayloadContent", (Object)certficateContent);
    }
    
    public void setPayloadDisplayName(final String displayName) {
        this.getPayloadDict().put("PayloadDisplayName", (Object)displayName);
    }
    
    public void setCertificatePayloadContent(final String certificatePayloadContent) {
        this.getPayloadDict().put("PayloadContent", (Object)certificatePayloadContent);
    }
}
