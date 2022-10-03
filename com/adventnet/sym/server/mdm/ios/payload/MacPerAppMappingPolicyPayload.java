package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import java.util.ArrayList;
import java.util.List;
import com.dd.plist.NSDictionary;

public class MacPerAppMappingPolicyPayload extends IOSPayload
{
    NSDictionary vpnDictionary;
    List<NSDictionary> appList;
    
    public MacPerAppMappingPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.vpn.managed.appmapping", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.vpnDictionary = null;
        this.appList = new ArrayList<NSDictionary>();
    }
    
    public void createAppDictionary(final String bundleIdentifier, final String codeSignature, final String designatedRequirement, final String vpnUUID) {
        this.vpnDictionary = new NSDictionary();
        this.setBundleIdentifier(bundleIdentifier);
        this.setCodeSignature(codeSignature);
        this.setDesignatedRequirement(designatedRequirement);
        this.setVPNUUID(vpnUUID);
        this.appList.add(this.vpnDictionary);
    }
    
    private void setBundleIdentifier(final String bundleIdentifier) {
        this.vpnDictionary.put("Identifier", (Object)bundleIdentifier);
    }
    
    private void setCodeSignature(final String codeSignature) {
        this.vpnDictionary.put("SigningIdentifier", (Object)codeSignature);
    }
    
    private void setDesignatedRequirement(final String designatedRequirement) {
        this.vpnDictionary.put("DesignatedRequirement", (Object)designatedRequirement);
    }
    
    private void setVPNUUID(final String vpnUUID) {
        this.vpnDictionary.put("VPNUUID", (Object)vpnUUID);
    }
    
    public void addAppMapping() {
        final NSArray nsArray = new NSArray(this.appList.size());
        this.getPayloadDict().put("AppLayerVPNMapping", (NSObject)nsArray);
        for (int i = 0; i < this.appList.size(); ++i) {
            nsArray.setValue(i, (Object)this.appList.get(i));
        }
    }
}
