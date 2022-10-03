package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;

public class MacEnergySaverPolicyPayload extends IOSPayload
{
    public MacEnergySaverPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String payloadType) {
        super(payloadVersion, payloadType, payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setSleepDisabled(final Boolean value) {
        this.getPayloadDict().put("SleepDisabled", (Object)value);
    }
    
    public void setDestroyFVKeyOnStandby(final Boolean value) {
        this.getPayloadDict().put("DestroyFVKeyOnStandby", (Object)value);
    }
    
    public void setSchedule(final NSDictionary dict) {
        this.getPayloadDict().put("com.apple.EnergySaver.desktop.Schedule", (NSObject)dict);
    }
    
    public void setDesktopSettings(final NSDictionary dict) {
        this.getPayloadDict().put("com.apple.EnergySaver.desktop.ACPower", (NSObject)dict);
    }
    
    public void setPortableACPowerSettings(final NSDictionary dict) {
        this.getPayloadDict().put("com.apple.EnergySaver.portable.ACPower", (NSObject)dict);
    }
    
    public void setPortableBatterySettings(final NSDictionary dict) {
        this.getPayloadDict().put("com.apple.EnergySaver.portable.BatteryPower", (NSObject)dict);
    }
}
