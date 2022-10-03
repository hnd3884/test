package com.me.mdm.onpremise.server.metracker;

import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEDCTrackerMDMAndroidImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    public Properties getTrackerProperties() {
        final com.me.mdm.server.metracker.MEDCTrackerMDMAndroidImpl mdmCoreDataCollector = new com.me.mdm.server.metracker.MEDCTrackerMDMAndroidImpl();
        return mdmCoreDataCollector.getTrackerProperties();
    }
}
