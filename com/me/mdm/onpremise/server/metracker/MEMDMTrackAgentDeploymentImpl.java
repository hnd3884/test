package com.me.mdm.onpremise.server.metracker;

import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackAgentDeploymentImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    public Properties getTrackerProperties() {
        final com.me.mdm.server.metracker.MEMDMTrackAgentDeploymentImpl mdmCoreDataCollector = new com.me.mdm.server.metracker.MEMDMTrackAgentDeploymentImpl();
        return mdmCoreDataCollector.getTrackerProperties();
    }
}
