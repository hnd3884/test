package com.me.mdm.server.tracker.mics;

import org.json.JSONObject;
import java.util.ArrayList;

public interface MICSEnrollmentFeatureController extends MICSMailerFeatureController
{
    public static final String SUBFEATURE_1 = "type";
    public static final String SUBFEATURE_2 = "operation";
    
    default JSONObject getFeatureTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature, final String featureName) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)featureName);
        jsonObject.put("type", (Object)micsMailerSubFeature.get(0).getEnrollmentFeatureName());
        jsonObject.put("operation", (Object)micsMailerSubFeature.get(1).getEnrollmentStatus());
        return jsonObject;
    }
    
    public enum EnrollmentStatus implements MICSMailerAPI.MICSMailerSubFeature
    {
        START("start"), 
        COMPLETE("complete");
        
        String status;
        
        private EnrollmentStatus(final String status) {
            this.status = status;
        }
        
        public String getEnrollmentStatus() {
            return this.status;
        }
    }
    
    public interface EnrollmentType extends MICSMailerAPI.MICSMailerSubFeature
    {
        String getEnrollmentFeatureName();
    }
}
