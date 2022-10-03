package com.me.mdm.onpremise.server.micstracker;

import org.json.JSONObject;
import com.me.mdm.server.tracker.mics.MICSMailerAPI;

public class MDMMICSMailerUtil implements MICSMailerAPI
{
    public void postDataToMicsForMailer(final JSONObject jsonObject) {
    }
    
    public boolean isFeatureExcluded(final JSONObject jsonObject) {
        return false;
    }
}
