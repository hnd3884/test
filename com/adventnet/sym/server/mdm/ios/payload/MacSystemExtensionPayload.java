package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class MacSystemExtensionPayload extends IOSPayload
{
    public MacSystemExtensionPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String payloadType) {
        super(payloadVersion, payloadType, payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowSystemOverrides(final Boolean value) {
        this.getPayloadDict().put("AllowUserOverrides", (Object)value);
    }
    
    public void setAllowedTeamIdentifiers(final NSArray teamIdentifiers) {
        this.getPayloadDict().put("AllowedTeamIdentifiers", (NSObject)teamIdentifiers);
    }
    
    public void setAllowedKernelExtensions(final NSDictionary kernelExtensions) {
        this.getPayloadDict().put("AllowedKernelExtensions", (NSObject)kernelExtensions);
    }
    
    public void setAllowedSystemExtensions(final NSDictionary kernelExtensions) {
        this.getPayloadDict().put("AllowedSystemExtensions", (NSObject)kernelExtensions);
    }
    
    public void setAllowedSystemExtensionsType(final NSDictionary systemExtensionsType) {
        this.getPayloadDict().put("AllowedSystemExtensionTypes", (NSObject)systemExtensionsType);
    }
}
