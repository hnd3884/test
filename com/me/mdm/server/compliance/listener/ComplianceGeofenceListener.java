package com.me.mdm.server.compliance.listener;

import org.json.JSONArray;
import java.util.logging.Level;
import com.me.mdm.server.compliance.ComplianceHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.me.mdm.server.geofence.GeoFenceDBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.geofence.listener.GeoFenceListener;

public class ComplianceGeofenceListener implements GeoFenceListener
{
    Logger logger;
    
    public ComplianceGeofenceListener() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    @Override
    public void geoFenceModified(final JSONObject paramsJSON) {
        try {
            final Long geoFenceId = JSONUtil.optLongForUVH(paramsJSON, "geo_fence_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(paramsJSON, "customer_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(paramsJSON, "user_id", Long.valueOf(-1L));
            final JSONObject responseJSON = GeoFenceDBUtil.getInstance().getComplianceForGeoFence(paramsJSON);
            final JSONArray complianceProfilesJSONArray = responseJSON.getJSONArray("compliance_profiles");
            for (int i = 0; i < complianceProfilesJSONArray.length(); ++i) {
                final Long complianceId = JSONUtil.optLongForUVH(complianceProfilesJSONArray, i, -1L);
                paramsJSON.put("compliance_id", (Object)complianceId);
                final JSONObject complianceProfileJSON = ComplianceDBUtil.getInstance().getComplianceProfile(paramsJSON);
                final JSONArray policiesJSONArray = complianceProfileJSON.getJSONArray("policies");
                for (int j = 0; j < policiesJSONArray.length(); ++j) {
                    final JSONArray ruleCriterionsJSONArray = policiesJSONArray.getJSONObject(j).getJSONObject("rule").getJSONArray("rule_criterions");
                    for (int k = 0; k < ruleCriterionsJSONArray.length(); ++k) {
                        if (ruleCriterionsJSONArray.getJSONObject(k).getInt("rule_criteria_type") == 2) {
                            ruleCriterionsJSONArray.getJSONObject(k).put("geo_fence_id", (Object)geoFenceId);
                        }
                    }
                }
                complianceProfileJSON.put("policies", (Object)policiesJSONArray);
                complianceProfileJSON.put("user_id", (Object)userId);
                complianceProfileJSON.put("customer_id", (Object)customerId);
                ComplianceDBUtil.getInstance().addOrUpdateComplianceProfile(complianceProfileJSON);
                final JSONObject publishProfileJSON = new JSONObject();
                publishProfileJSON.put("compliance_id", (Object)complianceId);
                publishProfileJSON.put("collection_id", (Object)ProfileHandler.getRecentProfileCollectionID(complianceId));
                publishProfileJSON.put("customer_id", (Object)customerId);
                publishProfileJSON.put("user_id", (Object)userId);
                publishProfileJSON.put("compliance_name", complianceProfileJSON.get("compliance_name"));
                publishProfileJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
                ComplianceHandler.getInstance().publishComplianceProfile(publishProfileJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- geoFenceModified() >   Error   ", e);
        }
    }
}
