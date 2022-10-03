package com.me.mdm.server.tracker.mics;

import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSChromeEnrollmentFeatureController implements MICSEnrollmentFeatureController
{
    public static final String FEATURE_NAME = "Enrollment_Chrome";
    public static final String SUB_FEATURE = "Type";
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        return this.getFeatureTrackingJSON(micsMailerSubFeature, "Enrollment_Chrome");
    }
    
    public static void addTrackingData(final EnrollmentStatus enrollmentStatus) {
        try {
            MICSChromeEnrollmentFeatureController.LOGGER.log(Level.INFO, "MICS Chrome Enrollment Tracking - {0}", enrollmentStatus);
            final MICSChromeEnrollmentFeatureController micsChromeEnrollmentFeatureController = new MICSChromeEnrollmentFeatureController();
            final ArrayList<MICSMailerAPI.MICSMailerSubFeature> subFeatures = new ArrayList<MICSMailerAPI.MICSMailerSubFeature>((Collection<? extends MICSMailerAPI.MICSMailerSubFeature>)Arrays.asList(EnrollmentType.CHROME_OS, enrollmentStatus));
            micsChromeEnrollmentFeatureController.addTrackingData(micsChromeEnrollmentFeatureController.getTrackingJSON(subFeatures));
        }
        catch (final Exception e) {
            MICSChromeEnrollmentFeatureController.LOGGER.log(Level.INFO, "MICS Tracker Exception - Exception while adding chrome enrollment tracking ", e);
        }
    }
    
    public enum EnrollmentType implements MICSEnrollmentFeatureController.EnrollmentType
    {
        CHROME_OS("ChromeOS");
        
        String name;
        
        private EnrollmentType(final String type) {
            this.name = type;
        }
        
        @Override
        public String getEnrollmentFeatureName() {
            return this.name;
        }
    }
}
