package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.ios.MDMNSArray;
import java.util.List;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;

public class SSOPayload extends IOSPayload
{
    public SSOPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.sso", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final NSDictionary kerberosDict = new NSDictionary();
        this.getPayloadDict().put("Kerberos", (NSObject)kerberosDict);
    }
    
    public void setAccountName(final String name) {
        this.getPayloadDict().put("Name", (Object)name);
    }
    
    public void setPrincipalName(final String principalName) {
        this.getKerberosDict().put("PrincipalName", (Object)principalName);
    }
    
    public void setRelam(final String relam) {
        this.getKerberosDict().put("Realm", (Object)relam);
    }
    
    public void setURL(final List urlList) {
        final NSArray urlArray = MDMNSArray.getNSArrayFromList(urlList);
        this.getKerberosDict().put("URLPrefixMatches", (NSObject)urlArray);
    }
    
    public void setApp(final List appList) {
        final NSArray appArray = MDMNSArray.getNSArrayFromList(appList);
        this.getKerberosDict().put("AppIdentifierMatches", (NSObject)appArray);
    }
    
    public void setPayloadCertificateUUID(final String certificateUUID) {
        this.getKerberosDict().put("PayloadCertificateUUID", (Object)certificateUUID);
    }
    
    private NSDictionary getKerberosDict() {
        return (NSDictionary)this.getPayloadDict().get((Object)"Kerberos");
    }
}
