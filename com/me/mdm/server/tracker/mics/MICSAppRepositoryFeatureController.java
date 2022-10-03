package com.me.mdm.server.tracker.mics;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSAppRepositoryFeatureController implements MICSMailerFeatureController
{
    public static final String FEATURE_NAME = "App_Repository";
    public static final String SUB_FEATURE_1 = "Operation";
    public static final String SUB_FEATURE_2 = "App_Type";
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)"App_Repository");
        jsonObject.put("Operation", (Object)micsMailerSubFeature.get(0).getOperation());
        jsonObject.put("App_Type", (Object)micsMailerSubFeature.get(1).getAppType());
        return jsonObject;
    }
    
    public static void addTrackingData(final Integer platform, final AppOperation appOperation, final boolean isEnterpriseApp, final boolean isMSI) {
        try {
            final ArrayList<MICSMailerAPI.MICSMailerSubFeature> subFeatures = new ArrayList<MICSMailerAPI.MICSMailerSubFeature>();
            subFeatures.add(appOperation);
            switch (platform) {
                case 1: {
                    if (isEnterpriseApp) {
                        subFeatures.add(AppType.APPLE_ENTERPRISE_APP);
                        break;
                    }
                    subFeatures.add(AppType.APPLE_STORE_APP);
                    break;
                }
                case 2: {
                    if (isEnterpriseApp) {
                        subFeatures.add(AppType.ANDROID_ENTERPRISE_APP);
                        break;
                    }
                    subFeatures.add(AppType.PLAY_STORE_APP);
                    break;
                }
                case 3: {
                    if (isMSI) {
                        subFeatures.add(AppType.MSI_APPLICATION);
                        break;
                    }
                    if (isEnterpriseApp) {
                        subFeatures.add(AppType.WINDOWS_ENTERPRISE_APP);
                        break;
                    }
                    subFeatures.add(AppType.BUSINESS_APP_STORE);
                    break;
                }
                case 4: {
                    if (isEnterpriseApp) {
                        subFeatures.add(AppType.CHROME_CUSTOM_APP);
                        break;
                    }
                    subFeatures.add(AppType.CHROME_WEB_STORE_APP);
                    break;
                }
                default: {
                    MICSAppRepositoryFeatureController.LOGGER.log(Level.INFO, "Platform is not listed {0}", platform);
                    return;
                }
            }
            MICSAppRepositoryFeatureController.LOGGER.log(Level.INFO, "MICS App repository tracking - {0}", subFeatures);
            final MICSAppRepositoryFeatureController micsAppRepositoryFeatureController = new MICSAppRepositoryFeatureController();
            micsAppRepositoryFeatureController.addTrackingData(micsAppRepositoryFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSAppRepositoryFeatureController.LOGGER.log(Level.SEVERE, "MICS Tracker Exception - Exception while adding app repository tracking details ", e);
        }
    }
    
    public enum AppOperation implements MICSMailerAPI.MICSMailerSubFeature
    {
        ADD_APP("Add_App"), 
        DELETE_APP("Delete_App");
        
        String operation;
        
        private AppOperation(final String operation) {
            this.operation = operation;
        }
        
        public String getOperation() {
            return this.operation;
        }
    }
    
    public enum AppType implements MICSMailerAPI.MICSMailerSubFeature
    {
        APPLE_STORE_APP("Apple Store App"), 
        APPLE_ENTERPRISE_APP("Apple Enterprise App"), 
        PLAY_STORE_APP("Play Store App"), 
        ANDROID_ENTERPRISE_APP("Android Enterprise App"), 
        BUSINESS_APP_STORE("Business Store App"), 
        WINDOWS_ENTERPRISE_APP("Windows Enterprise APP"), 
        MSI_APPLICATION("MSI Application"), 
        CHROME_WEB_STORE_APP("Chrome Web Store App"), 
        CHROME_CUSTOM_APP("Chrome Custom App");
        
        String appType;
        
        private AppType(final String appType) {
            this.appType = appType;
        }
        
        public String getAppType() {
            return this.appType;
        }
    }
}
