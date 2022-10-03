package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class LdapPayload extends IOSPayload
{
    public LdapPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.ldap.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setLdapAccountDescription(final String description) {
        this.getPayloadDict().put("LDAPAccountDescription", (Object)description);
    }
    
    public void setLdapAccountHostName(final String hostName) {
        this.getPayloadDict().put("LDAPAccountHostName", (Object)hostName);
    }
    
    public void setLdapAccountPassword(final String passwd) {
        this.getPayloadDict().put("LDAPAccountPassword", (Object)passwd);
    }
    
    public void setLdapAccountUseSSL(final boolean useSSL) {
        this.getPayloadDict().put("LDAPAccountUseSSL", (Object)useSSL);
    }
    
    public void setLdapAccountUserName(final String userName) {
        this.getPayloadDict().put("LDAPAccountUserName", (Object)userName);
    }
    
    public void setLdapSearchSettings(final NSArray nsarray) {
        this.getPayloadDict().put("LDAPSearchSettings", (NSObject)nsarray);
    }
    
    public NSDictionary createSearchDict(final String description, final String scope, final String searchBase) {
        final NSDictionary dict = new NSDictionary();
        dict.put("LDAPSearchSettingDescription", (Object)description);
        dict.put("LDAPSearchSettingScope", (Object)scope);
        dict.put("LDAPSearchSettingSearchBase", (Object)searchBase);
        return dict;
    }
}
