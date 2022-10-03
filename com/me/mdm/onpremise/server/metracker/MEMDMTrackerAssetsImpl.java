package com.me.mdm.onpremise.server.metracker;

import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackerAssetsImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    public Properties getTrackerProperties() {
        final com.me.mdm.server.metracker.MEMDMTrackerAssetsImpl mdmCoreDataCollector = new com.me.mdm.server.metracker.MEMDMTrackerAssetsImpl();
        return mdmCoreDataCollector.getTrackerProperties();
    }
}
