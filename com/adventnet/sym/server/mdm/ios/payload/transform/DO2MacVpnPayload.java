package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.MacVPNPolicyPayload;
import com.adventnet.sym.server.mdm.ios.payload.MacPerAppVPNPolicyPayload;
import com.adventnet.sym.server.mdm.ios.payload.VPNPayLoadType;

public class DO2MacVpnPayload extends DO2VpnPayload
{
    @Override
    protected VPNPayLoadType getPerAppVPNPayload() {
        return new MacPerAppVPNPolicyPayload(1, "MDM", "com.mdm.mobiledevice.vpn.applayer.mac", "Mac Per-app VPN Configuration");
    }
    
    @Override
    protected VPNPayLoadType getVPNPayload() {
        return new MacVPNPolicyPayload(1, "MDM", "com.mdm.mobiledevice.vpn.ac", "Mac VPN Configuration");
    }
}
