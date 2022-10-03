package com.me.mdm.server.tracker.mics;

import org.json.JSONObject;
import java.util.ArrayList;

public class MICSSelfEnrollmentFeatureController implements MICSEnrollmentFeatureController
{
    public static String featureName;
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("feature", (Object)MICSSelfEnrollmentFeatureController.featureName);
        jsonObject.put("type", (Object)micsMailerSubFeature.get(0).getEnrollmentStatus());
        return jsonObject;
    }
    
    static {
        MICSSelfEnrollmentFeatureController.featureName = "SELF_ENROLLMENT";
    }
}
