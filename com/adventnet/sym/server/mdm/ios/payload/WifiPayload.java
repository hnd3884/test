package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSString;
import com.dd.plist.NSNumber;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;

public class WifiPayload extends IOSPayload
{
    public static final String ENCRYPTION_TYPE_NONE = "None";
    public static final String ENCRYPTION_TYPE_WEP = "WEP";
    public static final String ENCRYPTION_TYPE_WPA = "WPA";
    public static final String ENCRYPTION_TYPE_WPA2 = "WPA2";
    public static final String ENCRYPTION_TYPE_WPA3 = "WPA3";
    public static final String ENCRYPTION_TYPE_ANY = "Any";
    public static final int PPTP = 2;
    public static final int IPSEC = 3;
    public static final int CISCO_ANYCONNECT = 4;
    public static final int JUNIPER_SSL = 5;
    public static final int F5_SSL = 6;
    public static final int CUSTOM_SSL = 7;
    
    public WifiPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.wifi.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void initializeDicts() {
        final NSDictionary eapClientDict = new NSDictionary();
        this.getPayloadDict().put("EAPClientConfiguration", (NSObject)eapClientDict);
    }
    
    protected NSDictionary getEAPClientDict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("EAPClientConfiguration");
    }
    
    protected NSDictionary getProxyDict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("Proxies");
    }
    
    public void setSSID_STR(final String ssidName) {
        this.getPayloadDict().put("SSID_STR", (Object)ssidName);
    }
    
    public void setHIDDEN_NETWORK(final Boolean hidden) {
        this.getPayloadDict().put("HIDDEN_NETWORK", (Object)hidden);
    }
    
    public void setEncryptionType(final String encryptionType) {
        this.getPayloadDict().put("EncryptionType", (Object)encryptionType);
    }
    
    public void setPassword(final String password) {
        this.getPayloadDict().put("Password", (Object)password);
    }
    
    public void setAutoJoin(final Boolean autoJoin) {
        this.getPayloadDict().put("AutoJoin", (Object)autoJoin);
    }
    
    public void setPayloadCertificateUUID(final String uuid) {
        this.getPayloadDict().put("PayloadCertificateUUID", (Object)uuid);
    }
    
    public void setUserName(final String userName) {
        this.getEAPClientDict().put("UserName", (Object)userName);
    }
    
    public void setUserPassword(final String userPassword) {
        this.getEAPClientDict().put("UserPassword", (Object)userPassword);
    }
    
    public void setAcceptEAPTypes(final Integer[] eapTypes) {
        final NSArray eapTypesArray = new NSArray(eapTypes.length);
        for (int i = 0; i < eapTypes.length; ++i) {
            eapTypesArray.setValue(i, (Object)new NSNumber((int)eapTypes[i]));
        }
        this.getEAPClientDict().put("AcceptEAPTypes", (NSObject)eapTypesArray);
    }
    
    public void setPayloadCertificateAnchorUUID(final String[] trustedCertificates) {
        final NSArray establishArray = new NSArray(trustedCertificates.length);
        for (int i = 0; i < trustedCertificates.length; ++i) {
            establishArray.setValue(i, (Object)new NSString(trustedCertificates[i]));
        }
        this.getEAPClientDict().put("PayloadCertificateAnchorUUID", (NSObject)establishArray);
    }
    
    public void setTLSTrustedServerNames(final String[] serverNames) {
        final NSArray establishArray = new NSArray(serverNames.length);
        for (int i = 0; i < serverNames.length; ++i) {
            establishArray.setValue(i, (Object)new NSString(serverNames[i]));
        }
        this.getEAPClientDict().put("TLSTrustedServerNames", (NSObject)establishArray);
    }
    
    public void setTLSAllowTrustExceptions(final Boolean allowException) {
        this.getEAPClientDict().put("TLSAllowTrustExceptions", (Object)allowException);
    }
    
    public void setTTLSInnerAuthentication(final String authentication) {
        this.getEAPClientDict().put("TTLSInnerAuthentication", (Object)authentication);
    }
    
    public void setOuterIdentity(final String outerIdentity) {
        this.getEAPClientDict().put("OuterIdentity", (Object)outerIdentity);
    }
    
    public void setEAPFASTUsePAC(final Boolean usePAC) {
        this.getEAPClientDict().put("EAPFASTUsePAC", (Object)usePAC);
    }
    
    public void setEAPFASTProvisionPAC(final Boolean useProvisionPAC) {
        this.getEAPClientDict().put("EAPFASTProvisionPAC", (Object)useProvisionPAC);
    }
    
    public void setEAPFASTProvisionPACAnonymously(final Boolean useAnonymously) {
        this.getEAPClientDict().put("EAPFASTProvisionPACAnonymously", (Object)useAnonymously);
    }
    
    public void setOneTimeUserPassword(final Boolean oneTimeUserPwd) {
        this.getEAPClientDict().put("OneTimeUserPassword", (Object)oneTimeUserPwd);
    }
    
    public void setWiFiProxy(final String proxyType) {
        this.getPayloadDict().put("ProxyType", (Object)proxyType);
    }
    
    public void setProxyServer(final String proxyServer) {
        this.getPayloadDict().put("ProxyServer", (Object)proxyServer);
    }
    
    public void setProxyServerPort(final Integer port) {
        this.getPayloadDict().put("ProxyServerPort", (Object)port);
    }
    
    public void setProxyUsername(final String userName) {
        this.getPayloadDict().put("ProxyUsername", (Object)userName);
    }
    
    public void setProxyPassword(final String httpPassword) {
        this.getPayloadDict().put("ProxyPassword", (Object)httpPassword);
    }
    
    public void setProxyAutoConfigURLString(final String urlString) {
        this.getPayloadDict().put("ProxyPACURL", (Object)urlString);
    }
    
    public void setDisableAssociationMACRandomization(final Boolean value) {
        this.getPayloadDict().put("DisableAssociationMACRandomization", (Object)value);
    }
    
    public void setSetupModes(final NSArray value) {
        this.getPayloadDict().put("SetupModes", (NSObject)value);
    }
}
