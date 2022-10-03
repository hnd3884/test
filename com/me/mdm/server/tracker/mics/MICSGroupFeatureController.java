package com.me.mdm.server.tracker.mics;

import java.util.logging.Level;
import java.util.Collection;
import java.util.Arrays;
import org.json.JSONObject;
import java.util.ArrayList;

public class MICSGroupFeatureController implements MICSMailerFeatureController
{
    public static String deviceGroup;
    public static String userGroup;
    public static String directoryGroup;
    public static String subfeature1;
    
    @Override
    public JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> micsMailerSubFeature) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(MICSGroupFeatureController.subfeature1, (Object)micsMailerSubFeature.get(0).getGroupOperationName());
        return jsonObject;
    }
    
    public JSONObject getTrackingJSON(final GroupOperation groupOperation, final String featureName) {
        final JSONObject jsonObject = this.getTrackingJSON(new ArrayList<MICSMailerAPI.MICSMailerSubFeature>(Arrays.asList(groupOperation)));
        jsonObject.put("feature", (Object)featureName);
        return jsonObject;
    }
    
    public static void addTrackingData(final int groupType, final GroupOperation groupOperation, final boolean isMDMDomainName) {
        try {
            final MICSGroupFeatureController micsGroupFeatureController = new MICSGroupFeatureController();
            MICSGroupFeatureController.LOGGER.log(Level.INFO, "MICS Group Tracking : GroupType - {0}, Group Operation - {1}, isMDMDomainName - {2}", new Object[] { groupType, groupOperation, isMDMDomainName });
            if (groupType == 6) {
                micsGroupFeatureController.addTrackingData(micsGroupFeatureController.getTrackingJSON(groupOperation, MICSGroupFeatureController.deviceGroup));
            }
            else if (groupType == 7) {
                if (isMDMDomainName) {
                    micsGroupFeatureController.addTrackingData(micsGroupFeatureController.getTrackingJSON(groupOperation, MICSGroupFeatureController.userGroup));
                }
                else {
                    micsGroupFeatureController.addTrackingData(micsGroupFeatureController.getTrackingJSON(groupOperation, MICSGroupFeatureController.directoryGroup));
                }
            }
        }
        catch (final Exception e) {
            MICSGroupFeatureController.LOGGER.log(Level.INFO, "MICS Tracker Exception - Exception while posting MICS Group Tracking details ", e);
        }
    }
    
    static {
        MICSGroupFeatureController.deviceGroup = "DEVICE_GROUP";
        MICSGroupFeatureController.userGroup = "USER_GROUP";
        MICSGroupFeatureController.directoryGroup = "Directory_GROUP";
        MICSGroupFeatureController.subfeature1 = "operation";
    }
    
    public enum GroupOperation implements MICSMailerAPI.MICSMailerSubFeature
    {
        CREATE("Create"), 
        EDIT("Edit"), 
        DELETE("Delete"), 
        INTEGRATE("Integrate");
        
        String name;
        
        private GroupOperation(final String name) {
            this.name = name;
        }
        
        public String getGroupOperationName() {
            return this.name;
        }
    }
}
