package com.me.mdm.server.tree.apidatahandler;

import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;

public class ApiGeoFenceViewDataHandler extends ApiListViewDataHandler
{
    @Override
    protected SelectQuery getSelectQuery() {
        (this.selectQuery = super.getSelectQuery()).addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        this.selectQuery.addJoin(new Join("RecentPubProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        this.selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        this.selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        return this.selectQuery;
    }
    
    @Override
    protected SelectQuery setCriteria() throws APIHTTPException {
        this.requestJson.remove("platform");
        this.selectQuery = super.setCriteria();
        final String filterButtonVal = this.requestJson.optString("filterButtonVal");
        Criteria profileTypeCriteria = this.getProfileTypeCriteria(5);
        profileTypeCriteria = profileTypeCriteria.and(this.selectQuery.getCriteria());
        final Criteria profileFilterButCriteria = this.getProfileFilterButtonCriteria(filterButtonVal);
        if (profileFilterButCriteria != null) {
            profileTypeCriteria = profileTypeCriteria.and(profileFilterButCriteria);
        }
        this.selectQuery.setCriteria(profileTypeCriteria);
        return this.selectQuery;
    }
    
    @Override
    protected JSONObject fetchResultObject() throws APIHTTPException {
        try {
            final String filterButtonVal = this.requestJson.optString("filterButtonVal");
            final JSONObject resultJson = new JSONObject();
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)this.selectQuery);
            ApiGeoFenceViewDataHandler.logger.log(Level.FINE, "Query formation for Geo fencing filtered values completed with result");
            final JSONArray yetToApplyArray = new JSONArray();
            final JSONArray successfullyAppliedArray = new JSONArray();
            final JSONArray updateAvailableArray = new JSONArray();
            while (dmDataSetWrapper.next()) {
                final int latestVer = (int)dmDataSetWrapper.getValue("PROFILE_VERSION");
                final JSONObject profileObject = this.setBasicProfileValues(dmDataSetWrapper);
                profileObject.put("version", latestVer);
                if (dmDataSetWrapper.getValue("executed_profile_version") != null) {
                    final Integer execVersion = (Integer)dmDataSetWrapper.getValue("executed_profile_version");
                    if (latestVer > execVersion) {
                        profileObject.put("isUpgrade", true);
                        updateAvailableArray.put((Object)profileObject);
                        yetToApplyArray.put((Object)profileObject);
                    }
                    successfullyAppliedArray.put((Object)profileObject);
                }
                else {
                    yetToApplyArray.put((Object)profileObject);
                }
            }
            if (filterButtonVal.equalsIgnoreCase("all") || filterButtonVal == "") {
                resultJson.put("yet_to_apply", (Object)yetToApplyArray);
            }
            if (filterButtonVal.equalsIgnoreCase("associated") || filterButtonVal == "") {
                resultJson.put("successfull_applied", (Object)successfullyAppliedArray);
            }
            return resultJson;
        }
        catch (final Exception ex) {
            ApiGeoFenceViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching filter data for profiles", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
