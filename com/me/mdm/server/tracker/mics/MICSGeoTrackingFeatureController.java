package com.me.mdm.server.tracker.mics;

import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSGeoTrackingFeatureController implements MICSMailerFeatureController
{
    public static final String FEATURE_NAME = "Geo_Tracking";
    public static final String SUB_FEATURE = "operation";
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)"Geo_Tracking");
        jsonObject.put("operation", (Object)micsMailerSubFeature.get(0).getOperation());
        return jsonObject;
    }
    
    public static void addTrackingData(final Integer locationStatus) {
        try {
            MICSGeoTrackingFeatureController.LOGGER.log(Level.INFO, "MICS Tracking for Geo Location Tracking Type - {0}", locationStatus);
            final MICSGeoTrackingFeatureController micsGeoTrackingFeatureController = new MICSGeoTrackingFeatureController();
            switch (locationStatus) {
                case 1:
                case 2: {
                    micsGeoTrackingFeatureController.addTrackingData(micsGeoTrackingFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(GeoTrackingOperation.ENABLED))));
                    break;
                }
                case 3: {
                    micsGeoTrackingFeatureController.addTrackingData(micsGeoTrackingFeatureController.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(GeoTrackingOperation.DISABLED))));
                    break;
                }
            }
        }
        catch (final Exception e) {
            MICSGeoTrackingFeatureController.LOGGER.log(Level.INFO, "MICS Tracker Exception - Exception while tracking geo tracking feature ", e);
        }
    }
    
    public enum GeoTrackingOperation implements MICSMailerAPI.MICSMailerSubFeature
    {
        ENABLED("Enabled"), 
        DISABLED("Disabled");
        
        String operation;
        
        private GeoTrackingOperation(final String operation) {
            this.operation = operation;
        }
        
        public String getOperation() {
            return this.operation;
        }
    }
}
