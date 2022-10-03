package com.me.mdm.server.tracker.mics;

import org.json.JSONObject;

public interface MICSMailerAPI
{
    void postDataToMicsForMailer(final JSONObject p0);
    
    boolean isFeatureExcluded(final JSONObject p0);
    
    public interface MICSMailerSubFeature
    {
    }
}
