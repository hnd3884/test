package com.me.mdm.server.tracker.mics;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSAndroidEnrollmentFeatureController implements MICSEnrollmentFeatureController
{
    public static String featureName;
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        return this.getFeatureTrackingJSON(micsMailerSubFeature, MICSAndroidEnrollmentFeatureController.featureName);
    }
    
    public static MICSEnrollmentFeatureController.EnrollmentType getEnrollmentType(final int enrollmentTokenType) {
        switch (enrollmentTokenType) {
            case 20: {
                return EnrollmentType.NFC;
            }
            case 21: {
                return EnrollmentType.KNOX;
            }
            case 22: {
                return EnrollmentType.EMM;
            }
            case 23: {
                return EnrollmentType.ZERO_TOUCH;
            }
            default: {
                MICSAndroidEnrollmentFeatureController.LOGGER.log(Level.INFO, "template token not in the list {0}", enrollmentTokenType);
                return null;
            }
        }
    }
    
    static {
        MICSAndroidEnrollmentFeatureController.featureName = "ENROLLMENT_ANDROID";
    }
    
    public enum EnrollmentType implements MICSEnrollmentFeatureController.EnrollmentType
    {
        EMM("EMM"), 
        ZERO_TOUCH("Zero Touch"), 
        NFC("NFC"), 
        KNOX("KNOX"), 
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
