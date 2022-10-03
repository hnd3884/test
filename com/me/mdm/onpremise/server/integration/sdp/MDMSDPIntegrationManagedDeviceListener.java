package com.me.mdm.onpremise.server.integration.sdp;

import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MDMSDPIntegrationManagedDeviceListener extends ManagedDeviceListener
{
    public void deviceManaged(final DeviceEvent deviceEvent) {
        MDMSDPAssetDataProcessor.getInstance().deviceAddedChangesinSDP(deviceEvent);
    }
    
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        MDMSDPAssetDataProcessor.getInstance().deviceDeleteChangesinSDP(deviceEvent);
    }
    
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        MDMSDPAssetDataProcessor.getInstance().deviceDeleteChangesinSDP(deviceEvent);
    }
}
