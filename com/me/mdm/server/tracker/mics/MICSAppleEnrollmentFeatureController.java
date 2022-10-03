package com.me.mdm.server.tracker.mics;

import org.json.JSONObject;
import java.util.ArrayList;

public class MICSAppleEnrollmentFeatureController implements MICSEnrollmentFeatureController
{
    public static String featureName;
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        return this.getFeatureTrackingJSON(micsMailerSubFeature, MICSAppleEnrollmentFeatureController.featureName);
    }
    
    static {
        MICSAppleEnrollmentFeatureController.featureName = "ENROLLMENT_APPLE";
    }
    
    public enum EnrollmentType implements MICSEnrollmentFeatureController.EnrollmentType
    {
        APPLE_CONFIGURATOR("Apple Configurator"), 
        DEP("Apple Enrollment (DEP)"), 
        INVITE("Invite");
        
        String name;
        
        private EnrollmentType(final String name) {
            this.name = name;
        }
        
        @Override
        public String getEnrollmentFeatureName() {
            return this.name;
        }
    }
}
