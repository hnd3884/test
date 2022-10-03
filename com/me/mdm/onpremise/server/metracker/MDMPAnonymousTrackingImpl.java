package com.me.mdm.onpremise.server.metracker;

import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import com.me.mdm.server.factory.MDMAnonymousTrackingAPI;

public class MDMPAnonymousTrackingImpl implements MDMAnonymousTrackingAPI
{
    public boolean isAnonymousTrackingEnbled() {
        final boolean isMETrackEnabled = METrackerHandler.getMETrackSettings();
        return isMETrackEnabled;
    }
}
