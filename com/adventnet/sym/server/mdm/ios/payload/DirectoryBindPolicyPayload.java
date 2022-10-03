package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class DirectoryBindPolicyPayload extends IOSPayload
{
    public DirectoryBindPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.DirectoryService.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.getPayloadDict().put("ADCreateMobileAccountAtLoginFlag", (Object)true);
        this.getPayloadDict().put("ADWarnUserBeforeCreatingMAFlag", (Object)true);
        this.getPayloadDict().put("ADForceHomeLocalFlag", (Object)true);
        this.getPayloadDict().put("ADUseWindowsUNCPathFlag", (Object)true);
        this.getPayloadDict().put("ADAllowMultiDomainAuthFlag", (Object)true);
        this.getPayloadDict().put("ADDefaultUserShellFlag", (Object)true);
        this.getPayloadDict().put("ADMapUIDAttributeFlag", (Object)false);
        this.getPayloadDict().put("ADMapGIDAttributeFlag", (Object)false);
        this.getPayloadDict().put("ADMapGGIDAttributeFlag", (Object)false);
        this.getPayloadDict().put("ADNamespaceFlag", (Object)true);
        this.getPayloadDict().put("ADPacketSignFlag", (Object)true);
        this.getPayloadDict().put("ADPacketEncryptFlag", (Object)true);
        this.getPayloadDict().put("ADTrustChangePassIntervalDaysFlag", (Object)true);
        this.getPayloadDict().put("ADRestrictDDNSFlag", (Object)false);
        this.getPayloadDict().put("ADDomainAdminGroupListFlag", (Object)false);
    }
    
    public void setHostName(final String value) {
        this.getPayloadDict().put("HostName", (Object)value);
    }
    
    public void setUserName(final String value) {
        this.getPayloadDict().put("UserName", (Object)value);
    }
    
    public void setClientID(final String value) {
        this.getPayloadDict().put("ClientID", (Object)value);
    }
    
    public void setPassword(final String value) {
        this.getPayloadDict().put("Password", (Object)value);
    }
    
    public void setADOrganizationalUnit(final String value) {
        this.getPayloadDict().put("ADOrganizationalUnit", (Object)value);
    }
    
    public void setADMountStyle(final String value) {
        this.getPayloadDict().put("ADMountStyle", (Object)value);
    }
    
    public void setADPreferredDCServerFlag(final boolean value) {
        this.getPayloadDict().put("ADPreferredDCServerFlag", (Object)value);
    }
    
    public void setADCreateMobileAccountAtLogin(final boolean value) {
        this.getPayloadDict().put("ADCreateMobileAccountAtLogin", (Object)value);
    }
    
    public void setADWarnUserBeforeCreatingMA(final boolean value) {
        this.getPayloadDict().put("ADWarnUserBeforeCreatingMA", (Object)value);
    }
    
    public void setADDomainAdminGroupListFlag(final boolean value) {
        this.getPayloadDict().put("ADDomainAdminGroupListFlag", (Object)value);
    }
    
    public void setADRestrictDDNSFlag(final boolean value) {
        this.getPayloadDict().put("ADRestrictDDNSFlag", (Object)value);
    }
    
    public void setADForceHomeLocal(final boolean value) {
        this.getPayloadDict().put("ADForceHomeLocal", (Object)value);
    }
    
    public void setADUseWindowsUNCPath(final boolean value) {
        this.getPayloadDict().put("ADUseWindowsUNCPath", (Object)value);
    }
    
    public void setADAllowMultiDomainAuth(final boolean value) {
        this.getPayloadDict().put("ADAllowMultiDomainAuth", (Object)value);
    }
    
    public void setADDefaultUserShell(final String value) {
        this.getPayloadDict().put("ADDefaultUserShell", (Object)value);
    }
    
    public void setADPreferredDCServer(final String value) {
        this.getPayloadDict().put("ADPreferredDCServer", (Object)value);
    }
    
    public void setADDomainAdminGroupList(final NSArray value) {
        this.getPayloadDict().put("ADDomainAdminGroupList", (NSObject)value);
    }
    
    public void setADNamespace(final String value) {
        this.getPayloadDict().put("ADNamespace", (Object)value);
    }
    
    public void setADPacketSign(final String value) {
        this.getPayloadDict().put("ADPacketSign", (Object)value);
    }
    
    public void setADPacketEncrypt(final String value) {
        this.getPayloadDict().put("ADPacketEncrypt", (Object)value);
    }
    
    public void setADRestrictDDNS(final NSArray value) {
        this.getPayloadDict().put("ADRestrictDDNS", (NSObject)value);
    }
    
    public void setADTrustChangePassIntervalDays(final int value) {
        this.getPayloadDict().put("ADTrustChangePassIntervalDays", (Object)value);
    }
}
