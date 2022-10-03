package com.me.mdm.server.profiles.config;

import java.util.HashSet;
import org.json.JSONException;
import java.util.List;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class AppNotificationPolicyConfigHandler extends DefaultConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        final JSONArray result = new JSONArray();
        try {
            final JSONObject payloadJSON = new JSONObject();
            final Iterator configDataItemsItr = dataObject.getRows("ConfigDataItem");
            while (configDataItemsItr.hasNext()) {
                final Row configDataItemRow = configDataItemsItr.next();
                final Long configDataItemId = (Long)configDataItemRow.get("CONFIG_DATA_ITEM_ID");
                payloadJSON.put("payload_id", (Object)configDataItemId);
                final Iterator notificationPolicyItr = dataObject.getRows("MdmAppNotificationPolicy", new Criteria(new Column("MdmAppNotificationPolicyToConfigRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0), new Join("MdmAppNotificationPolicyToConfigRel", "MdmAppNotificationPolicy", new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, 2));
                final JSONArray notificationPoliciesJSON = new JSONArray();
                while (notificationPolicyItr.hasNext()) {
                    final Row notificationPolicyRow = notificationPolicyItr.next();
                    final Long notificationPolicyId = (Long)notificationPolicyRow.get("MDM_APP_NOTIFICATION_POLICY_ID");
                    final JSONObject notificationPolicyJSON = new JSONObject();
                    this.addConfigForRow(notificationPolicyRow, dataObject, configName, notificationPolicyJSON, "MdmAppNotificationPolicy");
                    final Join appGroupJoin = new Join("MdAppGroupDetails", "MdmAppNotificationPolicyToAppRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
                    final Criteria notificationPolicyIdCriteria = new Criteria(new Column("MdmAppNotificationPolicyToAppRel", "MDM_APP_NOTIFICATION_POLICY_ID"), (Object)notificationPolicyId, 0);
                    final Iterator appsItr = dataObject.getRows("MdAppGroupDetails", notificationPolicyIdCriteria, appGroupJoin);
                    final List apps = new ArrayList();
                    while (appsItr.hasNext()) {
                        final Row appGroupRow = appsItr.next();
                        final Long appGroupId = (Long)appGroupRow.get("APP_GROUP_ID");
                        final String appName = (String)appGroupRow.get("GROUP_DISPLAY_NAME");
                        final JSONObject appDetails = new JSONObject();
                        appDetails.put("app_id", (Object)appGroupId);
                        appDetails.put("app_name", (Object)appName);
                        apps.add(appDetails);
                    }
                    if (apps.size() > 0) {
                        notificationPolicyJSON.put("apps", (Object)new JSONArray((Collection)apps));
                    }
                    notificationPoliciesJSON.put((Object)notificationPolicyJSON);
                }
                payloadJSON.put("app_notification_settings", (Object)notificationPoliciesJSON);
                result.put((Object)payloadJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    @Override
    protected JSONObject getDetailsForColName(final String configName, final String columnName) {
        try {
            final JSONObject notificationPolicyConfig = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName).optJSONObject(1);
            final JSONArray properties = notificationPolicyConfig.optJSONArray("properties");
            for (int i = 0; i < properties.length(); ++i) {
                final JSONObject property = (JSONObject)properties.get(i);
                if (property.has("name") && property.get("name").equals(columnName)) {
                    return property;
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception occurred in getAliasForName", (Throwable)e);
        }
        return null;
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        super.validateServerJSON(serverJSON);
        final JSONArray notificationSettingsJSON = serverJSON.optJSONArray("APP_NOTIFICATION_POLICY");
        if (notificationSettingsJSON == null) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final List appsList = new ArrayList();
        for (int i = 0; i < notificationSettingsJSON.length(); ++i) {
            final JSONArray appsJSON = notificationSettingsJSON.getJSONObject(i).optJSONArray("APPS");
            if (appsJSON == null) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            appsList.addAll(appsJSON.toList());
            if (appsList.size() != new HashSet(appsList).size()) {
                throw new APIHTTPException("APP0016", new Object[0]);
            }
        }
    }
}
