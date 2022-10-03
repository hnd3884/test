package com.me.mdm.server.tracker.mics;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSStoreConfigurationFeatureController implements MICSMailerFeatureController
{
    public static final String FEATURE_NAME = "Store_Configuration";
    public static final String SUB_FEATURE_1 = "Type";
    public static final String SUB_FEATURE_2 = "Operation";
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)"Store_Configuration");
        jsonObject.put("Type", (Object)micsMailerSubFeature.get(0).getStoreConfigurationType());
        jsonObject.put("Operation", (Object)micsMailerSubFeature.get(1).getOperation());
        return jsonObject;
    }
    
    public static void addTrackingData(final int platformType, final StoreConfigurationOperation storeConfigurationOperation) {
        try {
            final ArrayList<MICSMailerAPI.MICSMailerSubFeature> subFeatures = new ArrayList<MICSMailerAPI.MICSMailerSubFeature>();
            switch (platformType) {
                case 1: {
                    subFeatures.add(StoreConfigurationType.APPLE_APP_MANAGEMENT);
                    break;
                }
                case 2: {
                    subFeatures.add(StoreConfigurationType.MANAGED_GOOGLE_PLAY);
                    break;
                }
                case 3: {
                    subFeatures.add(StoreConfigurationType.WINDOWS_BUSINESS_STORE);
                    break;
                }
                default: {
                    MICSStoreConfigurationFeatureController.LOGGER.log(Level.INFO, "MICS Tracking skipped for other platforms {0}", platformType);
                    return;
                }
            }
            subFeatures.add(storeConfigurationOperation);
            MICSStoreConfigurationFeatureController.LOGGER.log(Level.INFO, "MICS Store app configuration tracking - {0}", subFeatures);
            final MICSStoreConfigurationFeatureController micsStoreConfigurationFeatureController = new MICSStoreConfigurationFeatureController();
            micsStoreConfigurationFeatureController.addTrackingData(micsStoreConfigurationFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSStoreConfigurationFeatureController.LOGGER.log(Level.SEVERE, "MICS Tracker Exception - Exception while adding store configuration tracking ", e);
        }
    }
    
    public enum StoreConfigurationType implements MICSMailerAPI.MICSMailerSubFeature
    {
        APPLE_APP_MANAGEMENT("Apple_App_Management"), 
        MANAGED_GOOGLE_PLAY("Managed_Google_Play"), 
        WINDOWS_BUSINESS_STORE("Windows_Business_Store");
        
        String storeConfigurationType;
        
        private StoreConfigurationType(final String storeConfigurationType) {
            this.storeConfigurationType = storeConfigurationType;
        }
        
        public String getStoreConfigurationType() {
            return this.storeConfigurationType;
        }
    }
    
    public enum StoreConfigurationOperation implements MICSMailerAPI.MICSMailerSubFeature
    {
        START("Start"), 
        COMPLETE("Complete");
        
        String operation;
        
        private StoreConfigurationOperation(final String operation) {
            this.operation = operation;
        }
        
        public String getOperation() {
            return this.operation;
        }
    }
}
