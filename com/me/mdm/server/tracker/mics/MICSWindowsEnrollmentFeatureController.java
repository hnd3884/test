package com.me.mdm.server.tracker.mics;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSWindowsEnrollmentFeatureController implements MICSEnrollmentFeatureController
{
    public static String featureName;
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        return this.getFeatureTrackingJSON(micsMailerSubFeature, MICSWindowsEnrollmentFeatureController.featureName);
    }
    
    public static EnrollmentType getEnrollmentType(final int enrollmentToken) {
        switch (enrollmentToken) {
            case 30: {
                return EnrollmentType.MOBILE;
            }
            case 31: {
                return EnrollmentType.LAPTOP;
            }
            case 32: {
                return EnrollmentType.AZURE;
            }
            default: {
                MICSWindowsEnrollmentFeatureController.LOGGER.log(Level.INFO, "Template token is not in the list {0}", enrollmentToken);
                return null;
            }
        }
    }
    
    static {
        MICSWindowsEnrollmentFeatureController.featureName = "ENROLLMENT_WINDOWS";
    }
    
    public enum EnrollmentType implements MICSEnrollmentFeatureController.EnrollmentType
    {
        MOBILE("Mobile"), 
        LAPTOP("Laptop"), 
        AZURE("Azure"), 
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
