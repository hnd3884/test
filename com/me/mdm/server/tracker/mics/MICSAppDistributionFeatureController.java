package com.me.mdm.server.tracker.mics;

import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSAppDistributionFeatureController implements MICSMailerFeatureController
{
    public static final String FEATURE_NAME = "App_Distribution";
    public static final String SUB_FEATURE = "Distribution_Type";
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)"App_Distribution");
        jsonObject.put("Distribution_Type", (Object)micsMailerSubFeature.get(0).getDistributionType());
        return jsonObject;
    }
    
    public static void addTrackingData(final AppDistributionType distributionType) {
        try {
            MICSAppDistributionFeatureController.LOGGER.log(Level.INFO, "MICS tracking for app distribution {0}", distributionType);
            final MICSAppDistributionFeatureController micsAppDistributionFeatureController = new MICSAppDistributionFeatureController();
            micsAppDistributionFeatureController.addTrackingData(micsAppDistributionFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(distributionType))));
        }
        catch (final Exception e) {
            MICSAppDistributionFeatureController.LOGGER.log(Level.SEVERE, "MICS Tracker Exception - Exception while adding App Distribution Tracking ", e);
        }
    }
    
    public enum AppDistributionType implements MICSMailerAPI.MICSMailerSubFeature
    {
        Silent_Distribution("Silent_Distribution"), 
        App_Catalog("App_Catalog");
        
        String distributionType;
        
        private AppDistributionType(final String distributionType) {
            this.distributionType = distributionType;
        }
        
        public String getDistributionType() {
            return this.distributionType;
        }
    }
}
