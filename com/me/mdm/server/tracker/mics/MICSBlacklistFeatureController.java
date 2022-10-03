package com.me.mdm.server.tracker.mics;

import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSBlacklistFeatureController implements MICSMailerFeatureController
{
    public static final String FEATURE_NAME = "Block_Listing_App";
    public static final String SUB_FEATURE = "operation";
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)"Block_Listing_App");
        jsonObject.put("operation", (Object)micsMailerSubFeature.get(0).getBlacklistOperation());
        return jsonObject;
    }
    
    public static void addTrackingData(final int associationType) {
        try {
            MICSBlacklistFeatureController.LOGGER.log(Level.INFO, "MICS Blacklist Operation Tracking Type - {0}", associationType);
            final MICSBlacklistFeatureController micsBlacklistFeatureController = new MICSBlacklistFeatureController();
            switch (associationType) {
                case 1: {
                    micsBlacklistFeatureController.addTrackingData(micsBlacklistFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(BlacklistOperation.ENLIST))));
                    break;
                }
                case 2: {
                    micsBlacklistFeatureController.addTrackingData(micsBlacklistFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(BlacklistOperation.DELIST))));
                    break;
                }
            }
        }
        catch (final Exception e) {
            MICSBlacklistFeatureController.LOGGER.log(Level.SEVERE, "MICS Tracker Exception - Exception while add blacklist tracking data", e);
        }
    }
    
    public enum BlacklistOperation implements MICSMailerAPI.MICSMailerSubFeature
    {
        ENLIST("Enlist"), 
        DELIST("Delist");
        
        String operation;
        
        private BlacklistOperation(final String operation) {
            this.operation = operation;
        }
        
        public String getBlacklistOperation() {
            return this.operation;
        }
    }
}
