package com.me.mdm.server.tracker.mics;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MICSProfileFeatureController implements MICSMailerFeatureController
{
    public static final Logger LOGGER;
    public static String featureName;
    public static String subfeature1;
    public static String subfeature2;
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        return null;
    }
    
    public JSONObject getTrackingJSON(final OSType osType, final ProfileOperation profileOperation) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)MICSProfileFeatureController.featureName);
        jsonObject.put(MICSProfileFeatureController.subfeature1, (Object)osType.getOstype());
        jsonObject.put(MICSProfileFeatureController.subfeature2, (Object)profileOperation.getProfileOperation());
        return jsonObject;
    }
    
    public static void addTrackingData(final int platformType, final ProfileOperation profileOperation) {
        try {
            final MICSProfileFeatureController micsProfileFeatureController = new MICSProfileFeatureController();
            JSONObject jsonObject = null;
            MICSProfileFeatureController.LOGGER.log(Level.INFO, "MICS Profile Tracking - Platform {0}, ProfileOperation {1}", new Object[] { platformType, profileOperation.getProfileOperation() });
            switch (platformType) {
                case 1: {
                    jsonObject = micsProfileFeatureController.getTrackingJSON(OSType.APPLE, profileOperation);
                    break;
                }
                case 2: {
                    jsonObject = micsProfileFeatureController.getTrackingJSON(OSType.ANDROID, profileOperation);
                    break;
                }
                case 3: {
                    jsonObject = micsProfileFeatureController.getTrackingJSON(OSType.WINDOWS, profileOperation);
                    break;
                }
                case 4: {
                    jsonObject = micsProfileFeatureController.getTrackingJSON(OSType.CHROME, profileOperation);
                    break;
                }
                case 6: {
                    jsonObject = micsProfileFeatureController.getTrackingJSON(OSType.MAC_OS, profileOperation);
                    break;
                }
                case 7: {
                    jsonObject = micsProfileFeatureController.getTrackingJSON(OSType.TV_OS, profileOperation);
                    break;
                }
            }
            if (jsonObject != null) {
                micsProfileFeatureController.addTrackingData(jsonObject);
            }
        }
        catch (final Exception e) {
            MICSProfileFeatureController.LOGGER.log(Level.SEVERE, "MICS Tracker Exception - Exception while add tracking for profile creation", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MICSProfileFeatureController.class.getName());
        MICSProfileFeatureController.featureName = "PROFILE";
        MICSProfileFeatureController.subfeature1 = "OS";
        MICSProfileFeatureController.subfeature2 = "operation";
    }
    
    public enum OSType implements MICSMailerAPI.MICSMailerSubFeature
    {
        ANDROID("Android"), 
        APPLE("Apple"), 
        WINDOWS("Windows"), 
        CHROME("Chrome"), 
        MAC_OS("macOS"), 
        TV_OS("tvOS");
        
        String ostype;
        
        private OSType(final String name) {
            this.ostype = name;
        }
        
        public String getOstype() {
            return this.ostype;
        }
    }
    
    public enum ProfileOperation implements MICSMailerAPI.MICSMailerSubFeature
    {
        CREATE("Create"), 
        EDIT("Edit"), 
        DELETE("Delete");
        
        String operation;
        
        private ProfileOperation(final String operation) {
            this.operation = operation;
        }
        
        public String getProfileOperation() {
            return this.operation;
        }
    }
}
