package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSString;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSSet;
import com.dd.plist.NSDictionary;

public class VPNPayLoadType extends IOSPayload
{
    int connectionType;
    public static final int L2TP = 1;
    public static final int PPTP = 2;
    public static final int IPSEC = 3;
    public static final int CISCO_LEGACY_ANYCONNECT = 4;
    public static final int JUNIPER_SSL = 5;
    public static final int F5_SSL = 6;
    public static final int CUSTOM_SSL = 7;
    public static final int PULSE_SECURE = 8;
    public static final int IKEV2 = 9;
    public static final int CISCO_ANYCONNECT = 10;
    public static final int SONICWALL = 11;
    public static final int ARUBA_VIA = 12;
    public static final int CHECKPOINT_MOBILE = 13;
    public int securityAssociationType;
    private NSDictionary odRulesdict;
    private NSSet odRulesSet;
    private NSSet evalActionRuleSet;
    
    public VPNPayLoadType(final int payloadVersion, final String payLoadType, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, payLoadType, payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.connectionType = 0;
        this.securityAssociationType = 0;
        this.odRulesdict = null;
        this.odRulesSet = null;
        this.evalActionRuleSet = null;
    }
    
    public void initializeDicts(final int connectionType) {
        this.connectionType = connectionType;
        final NSDictionary proxyDict = new NSDictionary();
        this.getPayloadDict().put("Proxies", (NSObject)proxyDict);
        switch (connectionType) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13: {
                final NSDictionary vpnDict = new NSDictionary();
                final NSDictionary vendorConfigDict = new NSDictionary();
                this.getPayloadDict().put("VPN", (NSObject)vpnDict);
                this.getPayloadDict().put("VendorConfig", (NSObject)vendorConfigDict);
                break;
            }
            case 1:
            case 2: {
                final NSDictionary pppDict = new NSDictionary();
                this.getPayloadDict().put("PPP", (NSObject)pppDict);
                break;
            }
            case 3: {
                final NSDictionary ipsecDict = new NSDictionary();
                this.getPayloadDict().put("IPSec", (NSObject)ipsecDict);
                break;
            }
            case 9: {
                final NSDictionary ikev2Dict = new NSDictionary();
                this.getPayloadDict().put("IKEv2", (NSObject)ikev2Dict);
                final NSDictionary ikeSecurityAssociationDict = new NSDictionary();
                ikev2Dict.put("IKESecurityAssociationParameters", (NSObject)ikeSecurityAssociationDict);
                final NSDictionary childSecurityAssociationDict = new NSDictionary();
                ikev2Dict.put("ChildSecurityAssociationParameters", (NSObject)childSecurityAssociationDict);
                break;
            }
        }
        final NSDictionary ipv4Dict = new NSDictionary();
        this.getPayloadDict().put("IPv4", (NSObject)ipv4Dict);
        if (connectionType == 1 || connectionType == 2) {
            final NSDictionary eapDict = new NSDictionary();
            this.getPayloadDict().put("EAP", (NSObject)eapDict);
            final NSDictionary ipsecDict2 = new NSDictionary();
            this.getPayloadDict().put("IPSec", (NSObject)ipsecDict2);
        }
        this.addVPNSubType(connectionType);
    }
    
    protected void addVPNSubType(final int connectionType) {
        switch (connectionType) {
            case 4: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.cisco.anyconnect.applevpn.plugin");
                break;
            }
            case 5: {
                this.getPayloadDict().put("VPNSubType", (Object)"net.juniper.sslvpn");
                break;
            }
            case 6: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.f5.F5-Edge-Client.vpnplugin");
                break;
            }
            case 8: {
                this.getPayloadDict().put("VPNSubType", (Object)"net.pulsesecure.PulseSecure.vpnplugin");
                break;
            }
            case 10: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.cisco.anyconnect");
                break;
            }
            case 11: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.sonicwall.SonicWALL-SSLVPN.vpnplugin");
                break;
            }
            case 12: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.arubanetworks.aruba-via.vpnplugin");
                break;
            }
            case 13: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.checkpoint.CheckPoint-VPN.vpnplugin");
                break;
            }
        }
    }
    
    protected NSDictionary getProxyDict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("Proxies");
    }
    
    protected NSDictionary getVPNDict(final int connectionType) {
        switch (connectionType) {
            case 3: {
                return (NSDictionary)this.getPayloadDict().objectForKey("IPSec");
            }
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13: {
                return (NSDictionary)this.getPayloadDict().objectForKey("VPN");
            }
            case 9: {
                return (NSDictionary)this.getPayloadDict().objectForKey("IKEv2");
            }
            case 1:
            case 2: {
                return (NSDictionary)this.getPayloadDict().objectForKey("PPP");
            }
            default: {
                return null;
            }
        }
    }
    
    protected NSDictionary getEAPDict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("EAP");
    }
    
    protected NSDictionary getIKESecurityAssociation() {
        return (NSDictionary)this.getVPNDict(this.connectionType).objectForKey("IKESecurityAssociationParameters");
    }
    
    protected NSDictionary getChildSecurityAssociation() {
        return (NSDictionary)this.getVPNDict(this.connectionType).objectForKey("ChildSecurityAssociationParameters");
    }
    
    public void setUserDefinedName(final String userDefinedName) {
        this.getPayloadDict().put("UserDefinedName", (Object)userDefinedName);
    }
    
    public void setOverridePrimary(final Integer overridePrimary) {
        ((NSDictionary)this.getPayloadDict().objectForKey("IPv4")).put("OverridePrimary", (Object)overridePrimary);
    }
    
    public void setVPNType(final String vpnType) {
        this.getPayloadDict().put("VPNType", (Object)vpnType);
    }
    
    public void setAuthName(final String authName) {
        this.getVPNDict(this.connectionType).put("AuthName", (Object)authName);
    }
    
    public void setAuthPassword(final String authPassword) {
        this.getVPNDict(this.connectionType).put("AuthPassword", (Object)authPassword);
    }
    
    public void setTokenCard(final Boolean tokenCard) {
        this.getVPNDict(this.connectionType).put("TokenCard", (Object)tokenCard);
    }
    
    public void setCommRemoteAddress(final String remoteAddress) {
        this.getVPNDict(this.connectionType).put("CommRemoteAddress", (Object)remoteAddress);
    }
    
    public void setRSASecurID() {
        final NSArray plugInArray = new NSArray(1);
        plugInArray.setValue(0, (Object)new NSString("EAP-RSA"));
        final NSArray protocolArray = new NSArray(1);
        protocolArray.setValue(0, (Object)new NSString("EAP"));
        this.setAuthEAPPlugins(plugInArray);
        this.setAuthProtocol(protocolArray);
    }
    
    public void setAuthEAPPlugins(final NSArray pluginArray) {
        this.getVPNDict(this.connectionType).put("AuthEAPPlugins", (NSObject)pluginArray);
    }
    
    public void setAuthProtocol(final NSArray protocol) {
        this.getVPNDict(this.connectionType).put("AuthProtocol", (NSObject)protocol);
    }
    
    public void setCCPMPPE40Enabled(final Boolean ppe40Enabled) {
        this.getVPNDict(this.connectionType).put("CCPMPPE40Enabled", (Object)ppe40Enabled);
    }
    
    public void setCCPMPPE128Enabled(final Boolean ppe128Enabled) {
        this.getVPNDict(this.connectionType).put("CCPMPPE128Enabled", (Object)ppe128Enabled);
    }
    
    public void setCCPEnabled(final Boolean ccpEnabled) {
        this.getVPNDict(this.connectionType).put("CCPEnabled", (Object)ccpEnabled);
    }
    
    public void setRemoteAddress(final String remoteAddress) {
        this.getVPNDict(this.connectionType).put("RemoteAddress", (Object)remoteAddress);
    }
    
    public void setAuthenticationMethod(final String authMethod) {
        if (this.connectionType == 1) {
            this.getVPNDict(3).put("AuthenticationMethod", (Object)authMethod);
        }
        else {
            this.getVPNDict(this.connectionType).put("AuthenticationMethod", (Object)authMethod);
        }
    }
    
    public void setProviderBundleIdentifier(final String identifier) {
        this.getVPNDict(this.connectionType).put("ProviderBundleIdentifier", (Object)identifier);
    }
    
    public void setXAuthName(final String authName) {
        this.getVPNDict(this.connectionType).put("XAuthName", (Object)authName);
    }
    
    public void setXAuthEnabled(final Integer authEnabled) {
        this.getVPNDict(this.connectionType).put("XAuthEnabled", (Object)authEnabled);
    }
    
    public void setLocalIdentifier(final String localIdentifier) {
        this.getVPNDict(this.connectionType).put("LocalIdentifier", (Object)localIdentifier);
    }
    
    public void setLocalIdentifierType(final String identifierType) {
        this.getVPNDict(this.connectionType).put("LocalIdentifierType", (Object)identifierType);
    }
    
    public void setSharedSecret(final String sharedSecret) {
        if (this.connectionType == 1) {
            this.getVPNDict(3).put("SharedSecret", (Object)sharedSecret);
        }
        else {
            this.getVPNDict(this.connectionType).put("SharedSecret", (Object)sharedSecret);
        }
    }
    
    public void setXAuthPasswordEncryption(final Boolean promptForPassword) {
        if (promptForPassword) {
            this.getVPNDict(this.connectionType).put("XAuthPasswordEncryption", (Object)"Prompt");
        }
    }
    
    public void setXAuthPassword(final String password) {
        this.getVPNDict(this.connectionType).put("XAuthPassword", (Object)password);
    }
    
    public void setEAPEnabling() {
        this.getVPNDict(this.connectionType).put("ExtendedAuthEnabled", (Object)1);
    }
    
    public void setDeadPeerDetectionRate(final String dpdRateType) {
        this.getVPNDict(this.connectionType).put("DeadPeerDetectionRate", (Object)dpdRateType);
    }
    
    public void setPerfectForwardSecrecy(final int pfsEnable) {
        this.getVPNDict(this.connectionType).put("EnablePFS", (Object)pfsEnable);
    }
    
    public void setCertificateRevocationCheck(final int revocationCheck) {
        this.getVPNDict(this.connectionType).put("EnableCertificateRevocationCheck", (Object)revocationCheck);
    }
    
    public void setDisableMOBIKE(final int disableMOBIKE) {
        this.getVPNDict(this.connectionType).put("DisableMOBIKE", (Object)disableMOBIKE);
    }
    
    public void setDisableRedirect(final int mobileRedirect) {
        this.getVPNDict(this.connectionType).put("DisableRedirect", (Object)mobileRedirect);
    }
    
    public void setInternalIPSubnet(final int internalIPSubnet) {
        this.getVPNDict(this.connectionType).put("UseConfigurationAttributeInternalIPSubnet", (Object)internalIPSubnet);
    }
    
    public void setEncryptionAlgorithm(final String encryptionAlgorithm) {
        if (this.securityAssociationType == 0) {
            this.getIKESecurityAssociation().put("EncryptionAlgorithm", (Object)encryptionAlgorithm);
        }
        else {
            this.getChildSecurityAssociation().put("EncryptionAlgorithm", (Object)encryptionAlgorithm);
        }
    }
    
    public void setIntegrityAlgorithm(final String integrityAlgorithm) {
        if (this.securityAssociationType == 0) {
            this.getIKESecurityAssociation().put("IntegrityAlgorithm", (Object)integrityAlgorithm);
        }
        else {
            this.getChildSecurityAssociation().put("IntegrityAlgorithm", (Object)integrityAlgorithm);
        }
    }
    
    public void setDiffieHellmanGroup(final int diffieHellmanGroup) {
        if (this.securityAssociationType == 0) {
            this.getIKESecurityAssociation().put("DiffieHellmanGroup", (Object)diffieHellmanGroup);
        }
        else {
            this.getChildSecurityAssociation().put("DiffieHellmanGroup", (Object)diffieHellmanGroup);
        }
    }
    
    public void setTimeLifeInMinutes(final int lifeInMinutes) {
        if (this.securityAssociationType == 0) {
            this.getIKESecurityAssociation().put("LifeTimeInMinutes", (Object)lifeInMinutes);
        }
        else {
            this.getChildSecurityAssociation().put("LifeTimeInMinutes", (Object)lifeInMinutes);
        }
    }
    
    public void setPayloadCertificateUUID(final String certificateUUID) {
        this.getVPNDict(this.connectionType).put("PayloadCertificateUUID", (Object)certificateUUID);
    }
    
    public void setPromptForVPNPIN(final Boolean promptForPIN) {
        this.getVPNDict(this.connectionType).put("PromptForVPNPIN", (Object)promptForPIN);
    }
    
    public void setRemoteIdentifier(final String remoteid) {
        this.getVPNDict(this.connectionType).put("RemoteIdentifier", (Object)remoteid);
    }
    
    public void setOnDemandEnable(final Integer enableOnDemand) {
        this.getVPNDict(this.connectionType).put("OnDemandEnabled", (Object)enableOnDemand);
    }
    
    public void setOnDemandMatchDomainsAlways(final String[] alwaysEstablish) {
        final NSArray establishArray = new NSArray(alwaysEstablish.length);
        for (int i = 0; i < alwaysEstablish.length; ++i) {
            establishArray.setValue(i, (Object)new NSString(alwaysEstablish[i]));
        }
        this.getVPNDict(this.connectionType).put("OnDemandMatchDomainsAlways", (NSObject)establishArray);
    }
    
    public void setOnDemandMatchDomainsNever(final String[] never) {
        final NSArray establishArray = new NSArray(never.length);
        for (int i = 0; i < never.length; ++i) {
            establishArray.setValue(i, (Object)new NSString(never[i]));
        }
        this.getVPNDict(this.connectionType).put("OnDemandMatchDomainsNever", (NSObject)establishArray);
    }
    
    public void setOnDemandMatchDomainsOnRetry(final String[] establishIfNeeded) {
        final NSArray establishArray = new NSArray(establishIfNeeded.length);
        for (int i = 0; i < establishIfNeeded.length; ++i) {
            establishArray.setValue(i, (Object)new NSString(establishIfNeeded[i]));
        }
        this.getVPNDict(this.connectionType).put("OnDemandMatchDomainsOnRetry", (NSObject)establishArray);
    }
    
    public void setHTTPEnable(final Integer httpEnable) {
        this.getProxyDict().put("HTTPEnable", (Object)httpEnable);
        this.getProxyDict().put("HTTPSEnable", (Object)httpEnable);
    }
    
    public void setHTTPPort(final Integer port) {
        this.getProxyDict().put("HTTPPort", (Object)port);
        this.getProxyDict().put("HTTPSPort", (Object)port);
    }
    
    public void setHTTPProxy(final String httpProxy) {
        this.getProxyDict().put("HTTPProxy", (Object)httpProxy);
        this.getProxyDict().put("HTTPSProxy", (Object)httpProxy);
    }
    
    public void setHTTPProxyPassword(final String httpPassword) {
        this.getProxyDict().put("HTTPProxyPassword", (Object)httpPassword);
    }
    
    public void setHTTPProxyUsername(final String userName) {
        this.getProxyDict().put("HTTPProxyUsername", (Object)userName);
    }
    
    public void setProxyAutoConfigEnable(final Integer configEnable) {
        this.getProxyDict().put("ProxyAutoConfigEnable", (Object)configEnable);
    }
    
    public void setProxyAutoDiscoveryEnable(final Integer discoverEnable) {
        this.getProxyDict().put("ProxyAutoDiscoveryEnable", (Object)discoverEnable);
    }
    
    public void setProxyAutoConfigURLString(final String urlString) {
        this.getProxyDict().put("ProxyAutoConfigURLString", (Object)urlString);
    }
    
    public void setGroupForConnection(final String group) {
        if (this.connectionType == 11) {
            ((NSDictionary)this.getPayloadDict().objectForKey("VendorConfig")).put("LoginGroupOrDomain", (Object)group);
        }
        else {
            ((NSDictionary)this.getPayloadDict().objectForKey("VendorConfig")).put("Group", (Object)group);
        }
    }
    
    public void setRealm(final String realm) {
        ((NSDictionary)this.getPayloadDict().objectForKey("VendorConfig")).put("Realm", (Object)realm);
    }
    
    public void setRole(final String role) {
        ((NSDictionary)this.getPayloadDict().objectForKey("VendorConfig")).put("Role", (Object)role);
    }
    
    public void setCustomData(final String key, final String value) {
        ((NSDictionary)this.getPayloadDict().objectForKey("VendorConfig")).put(key, (Object)value);
    }
    
    public void setCustomSSLIdentifier(final String vpnSubType) {
        this.getPayloadDict().put("VPNSubType", (Object)vpnSubType);
    }
    
    public void createOnDemandDict() {
        this.odRulesdict = new NSDictionary();
    }
    
    public NSDictionary getOnDemandDict() {
        return this.odRulesdict;
    }
    
    public void createOnDemandSet() {
        this.odRulesSet = new NSSet();
    }
    
    public NSSet getOnDemandSet() {
        return this.odRulesSet;
    }
    
    public void createEvalActionDict() {
        this.evalActionRuleSet = new NSSet();
    }
    
    public NSSet getEvalActionDict() {
        return this.evalActionRuleSet;
    }
    
    public void setOdDictsInNSSet() {
        this.getOnDemandSet().addObject((NSObject)this.getOnDemandDict());
    }
    
    public void setEvalActionDictsInNSSet() {
        this.getEvalActionDict().addObject((NSObject)this.getOnDemandDict());
    }
    
    public void setOnDemandEnabled(final Integer onDemandEnabled) {
        this.getPayloadDict().put("OnDemandEnabled", (Object)onDemandEnabled);
    }
    
    public void setOnDemandRules() {
        this.getPayloadDict().put("OnDemandRules", (NSObject)this.getOnDemandSet());
    }
    
    public void setVpnDNSDomainMatch(final String value) {
        final NSSet valueSet = this.convertStringToNSset(value);
        this.getOnDemandDict().put("DNSDomainMatch", (NSObject)valueSet);
    }
    
    public void setDNSServerAddressMatch(final String value) {
        final NSSet valueSet = this.convertStringToNSset(value);
        this.getOnDemandDict().put("DNSServerAddressMatch", (NSObject)valueSet);
    }
    
    public void setInterfaceTypeMatch(final String value) {
        this.getOnDemandDict().put("InterfaceTypeMatch", (Object)value);
    }
    
    public void setSSIDMatch(final String value) {
        final NSSet valueSet = this.convertStringToNSset(value);
        this.getOnDemandDict().put("SSIDMatch", (NSObject)valueSet);
    }
    
    public void setURLStringProbe(final String value) {
        this.getOnDemandDict().put("URLStringProbe", (Object)value);
    }
    
    public void setVpnODAction(final String action) {
        this.getOnDemandDict().put("Action", (Object)action);
    }
    
    public void setDomains(final String value) {
        final NSSet valueSet = this.convertStringToNSset(value);
        this.getOnDemandDict().put("Domains", (NSObject)valueSet);
    }
    
    public void setRequiredDNSServers(final String value) {
        final NSSet valueSet = this.convertStringToNSset(value);
        this.getOnDemandDict().put("RequiredDNSServers", (NSObject)valueSet);
    }
    
    public void setRequiredURLStringProbe(final String value) {
        this.getOnDemandDict().put("RequiredURLStringProbe", (Object)value);
    }
    
    public void setDomainAction(final String action) {
        this.getOnDemandDict().put("DomainAction", (Object)action);
    }
    
    public void setActionParameters() {
        this.getOnDemandDict().put("ActionParameters", (NSObject)this.getEvalActionDict());
    }
    
    public void setTLSMinVersion(final String version) {
        this.getVPNDict(this.connectionType).put("TLSMinimumVersion", (Object)version);
    }
    
    public void setTLSMaxVersion(final String version) {
        this.getVPNDict(this.connectionType).put("TLSMaximumVersion", (Object)version);
    }
    
    public void setPerAppVPNUUID(final String strVPNUUID) {
        this.getPayloadDict().put("VPNUUID", (Object)strVPNUUID);
    }
    
    public void setPerAppVPNOnDemandMatchAppEnabled(final boolean odMatchAppEnabledFlag) {
        this.getVPNDict(this.connectionType).put("OnDemandMatchAppEnabled", (Object)odMatchAppEnabledFlag);
    }
    
    public void setPerAppVPNProviderType(final String providerType) {
        this.getVPNDict(this.connectionType).put("ProviderType", (Object)providerType);
    }
    
    private NSSet convertStringToNSset(final String value) {
        final NSSet ruleSet = new NSSet();
        final String[] valueArray = value.split(",");
        for (int i = 0; i < valueArray.length; ++i) {
            ruleSet.addObject((NSObject)new NSString(valueArray[i]));
        }
        return ruleSet;
    }
}
