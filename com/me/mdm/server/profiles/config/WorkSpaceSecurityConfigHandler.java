package com.me.mdm.server.profiles.config;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class WorkSpaceSecurityConfigHandler extends DefaultConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        JSONArray result = null;
        try {
            if (!dataObject.isEmpty()) {
                result = super.DOToAPIJSON(dataObject, configName, tableName);
                Iterator iterator = dataObject.getRows("MdAppGroupDetails");
                final List<Long> appGroupIDList = new ArrayList<Long>();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    appGroupIDList.add((Long)row.get("APP_GROUP_ID"));
                }
                final JSONObject configObject = result.getJSONObject(0);
                final JSONObject appDetailsJsonObj = this.getApplicationInfo(appGroupIDList);
                final JSONArray appDetailsJsonArray = new JSONArray();
                iterator = appDetailsJsonObj.keys();
                while (iterator.hasNext()) {
                    final String key = iterator.next();
                    final JSONObject appDetails = appDetailsJsonObj.getJSONObject(key);
                    final JSONObject appDetailsLowerCase = new JSONObject();
                    final Iterator appDetailItr = appDetails.keys();
                    while (appDetailItr.hasNext()) {
                        final String appDetailsKey = appDetailItr.next();
                        if (appDetailsKey.equals("APP_GROUP_ID")) {
                            appDetailsLowerCase.put("app_id", appDetails.get(appDetailsKey));
                        }
                        else {
                            appDetailsLowerCase.put(appDetailsKey.toLowerCase(), appDetails.get(appDetailsKey));
                        }
                    }
                    appDetailsJsonArray.put((Object)appDetailsLowerCase);
                }
                configObject.put("allowed_apps", (Object)appDetailsJsonArray);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in WorkSpaceSecurity Config", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    private JSONObject getApplicationInfo(final List appGroupIDList) throws SyMException {
        final JSONObject resultSet = new JSONObject();
        try {
            final Criteria criteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIDList.toArray(), 8);
            final DataObject dO = DataAccess.get("MdAppGroupDetails", criteria);
            final Iterator itr = dO.getRows("MdAppGroupDetails");
            while (itr.hasNext()) {
                final Row r = itr.next();
                final JSONObject appDetails = new JSONObject();
                appDetails.put("APP_GROUP_ID", r.get("APP_GROUP_ID"));
                appDetails.put("IDENTIFIER", r.get("IDENTIFIER"));
                appDetails.put("GROUP_DISPLAY_NAME", r.get("GROUP_DISPLAY_NAME"));
                resultSet.put(String.valueOf(r.get("APP_GROUP_ID")), (Object)appDetails);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred while fetching app details ", this.getClass().getName());
            throw new SyMException(1002, e.getCause());
        }
        return resultSet;
    }
}
