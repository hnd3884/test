package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSData;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;

public class IOSSCEPPayload extends IOSPayload
{
    NSDictionary scepDict;
    
    public IOSSCEPPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.security.scep", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.scepDict = new NSDictionary();
    }
    
    public void setURL(final String value) {
        this.scepDict.put("URL", (Object)value);
    }
    
    public void setName(final String value) {
        this.scepDict.put("Name", (Object)value);
    }
    
    public void setSubject(final NSArray subjectOIDS) {
        this.scepDict.put("Subject", (NSObject)subjectOIDS);
    }
    
    public void setChallenge(final String value) {
        this.scepDict.put("Challenge", (Object)value);
    }
    
    public void setKeysize(final Long value) {
        this.scepDict.put("Keysize", (Object)value);
    }
    
    public void setKeyType(final String value) {
        this.scepDict.put("KeyType", (Object)value);
    }
    
    public void setKeyUsage(final int value) {
        this.scepDict.put("KeyUsage", (Object)value);
    }
    
    public void setRetries(final Long value) {
        this.scepDict.put("Retries", (Object)value);
    }
    
    public void setRetryDelay(final Long value) {
        this.scepDict.put("RetryDelay", (Object)value);
    }
    
    public void setSubjectAltName(final NSDictionary subAltName) {
        this.scepDict.put("SubjectAltName", (NSObject)subAltName);
    }
    
    public void setCAFingerprint(final NSData value) {
        this.scepDict.put("CAFingerprint", (NSObject)value);
    }
    
    public void setPayloadContent() {
        this.getPayloadDict().put("PayloadContent", (NSObject)this.scepDict);
    }
}
