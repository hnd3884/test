package com.me.mdm.server.tree.apidatahandler;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;

public class ApiOsUpdateListViewDataHandler extends ApiListViewDataHandler
{
    @Override
    protected SelectQuery setCriteria() {
        this.selectQuery = super.setCriteria();
        final String filterButtonVal = this.requestJson.optString("filterButtonVal");
        Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)3, 0);
        profileTypeCriteria = profileTypeCriteria.and(this.selectQuery.getCriteria());
        if (filterButtonVal.equalsIgnoreCase("all")) {
            final Criteria selectGroupCri = new Criteria(Column.getColumn("derivedProfileTable", "derived_profile_id"), (Object)null, 0);
            profileTypeCriteria = profileTypeCriteria.and(selectGroupCri);
        }
        else if (filterButtonVal.equalsIgnoreCase("associated")) {
            final Criteria selectGroupCri = new Criteria(Column.getColumn("derivedProfileTable", "derived_profile_id"), (Object)null, 1);
            profileTypeCriteria = profileTypeCriteria.and(selectGroupCri);
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
            ApiOsUpdateListViewDataHandler.logger.log(Level.FINE, "Query formation for Osupdate policy filtered values completed with result");
            final JSONArray yetToApplyArray = new JSONArray();
            final JSONArray successfullyAppliedArray = new JSONArray();
            while (dmDataSetWrapper.next()) {
                final JSONObject profileObject = this.setBasicProfileValues(dmDataSetWrapper);
                if (dmDataSetWrapper.getValue("executed_profile_version") != null) {
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
            ApiOsUpdateListViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching filter data for os update policy", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
