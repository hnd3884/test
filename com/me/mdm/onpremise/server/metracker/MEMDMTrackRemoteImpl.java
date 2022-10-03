package com.me.mdm.onpremise.server.metracker;

import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackRemoteImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    public Properties getTrackerProperties() {
        return new com.me.mdm.server.metracker.MEMDMTrackRemoteImpl().getTrackerProperties();
    }
}
