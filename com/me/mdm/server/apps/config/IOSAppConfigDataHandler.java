package com.me.mdm.server.apps.config;

import com.me.mdm.api.APIUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.me.mdm.server.config.MDMConfigUtil;
import java.util.logging.Level;
import org.json.JSONObject;

public class IOSAppConfigDataHandler extends AppConfigDataPolicyHandler
{
    @Override
    public void deleteAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
        try {
            appConfigJSON.put("HAS_APP_CONFIGURATION", (Object)Boolean.FALSE);
            this.publishProfile(configDataItemId, appConfigJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeption in deleteAppConfigCommand", e);
        }
    }
    
    @Override
    public void resetAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
        try {
            appConfigJSON.put("PUBLISH_PROFILE", true);
            this.addAppConfigCommand(configDataItemId, appConfigJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeption in resetAppConfigCommand", e);
        }
    }
    
    @Override
    public void addAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
        try {
            final Boolean publishProfile = appConfigJSON.optBoolean("PUBLISH_PROFILE", false);
            if (publishProfile) {
                this.publishProfile(configDataItemId, appConfigJSON);
            }
            final Long collectionId = new MDMConfigUtil().getCollectionIdForItemId(configDataItemId);
            if (AppConfigPolicyDBHandler.getInstance().isConfigurationApplicableForApp(collectionId)) {
                final ArrayList resourceList = this.getAssociatedResourceList(configDataItemId, Boolean.TRUE);
                final List collectionList = new ArrayList();
                collectionList.add(collectionId);
                final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "ApplicationConfiguration");
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeption in app config command distribution", e);
        }
    }
    
    private void publishProfile(final Long configDataItemId, final JSONObject appConfigJSON) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
        sQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.setCriteria(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        sQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
        sQuery.addSelectColumn(new Column("ProfileToCollection", "*"));
        sQuery.addSelectColumn(new Column("InstallAppPolicy", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        final Row collectionRow = DO.getFirstRow("CfgDataToCollection");
        final Long collectionID = (Long)collectionRow.get("COLLECTION_ID");
        final Row profileRow = DO.getFirstRow("ProfileToCollection");
        final Long profileId = (Long)profileRow.get("PROFILE_ID");
        final Row policyRow = DO.getFirstRow("InstallAppPolicy");
        final Long appId = (Long)policyRow.get("APP_ID");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("PROFILE_ID", (Object)profileId);
        jsonObject.put("COLLECTION_ID", (Object)collectionID);
        jsonObject.put("CUSTOMER_ID", appConfigJSON.optLong("CUSTOMER_ID"));
        jsonObject.put("PLATFORM_TYPE", 1);
        jsonObject.put("APP_CONFIG", (Object)Boolean.TRUE);
        jsonObject.put("HAS_APP_CONFIGURATION", appConfigJSON.optBoolean("HAS_APP_CONFIGURATION", (boolean)Boolean.TRUE));
        jsonObject.put("APP_ID", (Object)appId);
        jsonObject.put("LAST_MODIFIED_BY", appConfigJSON.optLong("LAST_MODIFIED_BY"));
        ProfileConfigHandler.publishProfile(jsonObject);
    }
    
    @Override
    public JSONObject addAppConfiguration(final JSONObject apiRequest) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final Long configDataItem = APIUtil.getResourceID(apiRequest, "payloaditem_id");
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final Long userID = APIUtil.getUserID(apiRequest);
        final JSONObject configJSON = apiRequest.getJSONObject("msg_body");
        this.populateAppConfigJSON(configJSON, jsonObject);
        jsonObject.put("CUSTOMER_ID", (Object)customerID);
        jsonObject.put("PACKAGE_MODIFIED_BY", (Object)userID);
        jsonObject.put("PLATFORM_TYPE", 1);
        jsonObject.put("APP_GROUP_ID", configJSON.get("APP_GROUP_ID"));
        jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItem);
        jsonObject.put("PUBLISH_PROFILE", (Object)Boolean.FALSE);
        jsonObject.put("APP_ID", configJSON.get("APP_ID"));
        new AppConfigDataHandler().saveAppConfigData(jsonObject, Boolean.FALSE);
        return this.getAppConfiguration(apiRequest);
    }
    
    @Override
    public JSONObject modifyAppConfiguration(final JSONObject apiRequest) throws Exception {
        return this.addAppConfiguration(apiRequest);
    }
}
