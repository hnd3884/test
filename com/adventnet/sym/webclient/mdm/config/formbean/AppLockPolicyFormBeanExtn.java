package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;
import com.me.mdm.server.profiles.config.ProfileConfigurationUtil;
import com.me.mdm.server.profiles.config.ConfigHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class AppLockPolicyFormBeanExtn extends AppLockPolicyFormBean
{
    private Logger logger;
    HashMap<Long, Object> webClipsParentChildMap;
    
    public AppLockPolicyFormBeanExtn() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.webClipsParentChildMap = null;
    }
    
    private void cloneAppLockPolicyApps(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws Exception {
        if (cloneConfigDO.containsTable("AppLockPolicyApps")) {
            final List kioskConfiguredApps = CloneGlobalConfigHandler.getInstance().getKioskConfiguredApps(configDOFromDB, "MdAppGroupDetails", "APP_GROUP_ID");
            final HashMap<Long, Object> parentChildUVHMap = CloneGlobalConfigHandler.getInstance().getParentChildAppsUVHMap(cloneConfigDO, kioskConfiguredApps);
            final Object configDataItem = CloneGlobalConfigHandler.getInstance().getConfigDataItemId(configID, cloneConfigDO);
            final Iterator<Row> iterator = cloneConfigDO.getRows("AppLockPolicyApps", new Criteria(Column.getColumn("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"), configDataItem, 0));
            while (iterator.hasNext()) {
                final Row appDetailsRow = iterator.next();
                final Long parentAppGroupId = (Long)appDetailsRow.get("APP_GROUP_ID");
                final Object childAppGroupId = parentChildUVHMap.get(parentAppGroupId);
                if (childAppGroupId != null) {
                    appDetailsRow.set("APP_GROUP_ID", childAppGroupId);
                    cloneConfigDO.updateRow(appDetailsRow);
                }
            }
        }
    }
    
    private void cloneWebClipsPolicies(final Integer configId, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        if (cloneConfigDO.containsTable("WebClipToConfigRel")) {
            this.webClipsParentChildMap = new WebClipsPolicyFormBean().cloneWebClipsPolicies(configId, configDOFromDB, cloneConfigDO);
        }
    }
    
    private void cloneHomeScreenLayoutPolicy(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws Exception {
        if (cloneConfigDO.containsTable("ScreenLayoutSettings")) {
            final List homeScreenConfiguredApps = CloneGlobalConfigHandler.getInstance().getKioskConfiguredApps(configDOFromDB, "screenLayoutAppGroup", "APP_GROUP_ID");
            final HashMap<Long, Object> parentChildUVHAppsMap = CloneGlobalConfigHandler.getInstance().getParentChildAppsUVHMap(cloneConfigDO, homeScreenConfiguredApps);
            final Long parentCollectionID = CloneGlobalConfigHandler.getInstance().getCollectionId(configDOFromDB);
            final Long childCollectionId = CloneGlobalConfigHandler.getInstance().getCollectionId(cloneConfigDO);
            final DataObject configDO = MDMConfigUtil.getConfigDataItemDOByCollectionId(configID, parentCollectionID);
            final String payloadName = "applockhomescreenpolicy";
            final ConfigHandler configHandler = (ConfigHandler)ProfileConfigurationUtil.getInstance().getPayloadConfigurationHandler(payloadName);
            final JSONArray payloadJSON = configHandler.DOToAPIJSON(configDO, payloadName);
            final JSONObject dynaJSON = configHandler.apiJSONToServerJSON(payloadName, payloadJSON.getJSONObject(0));
            final JSONObject homeScreenJSON = dynaJSON.getJSONObject("ScreenLayout");
            CloneGlobalConfigHandler.getInstance().cloneHomeScreenPolicy(homeScreenJSON, parentChildUVHAppsMap, this.webClipsParentChildMap, "ScreenLayoutPageDetails", "ScreenPageLayout");
            dynaJSON.put("ScreenLayout", (Object)homeScreenJSON);
            dynaJSON.put("CUSTOMER_ID", (Object)CloneGlobalConfigHandler.getInstance().getCustomerId(cloneConfigDO));
            dynaJSON.put("COLLECTION_ID", (Object)childCollectionId);
            dynaJSON.put("PROFILE_NAME", (Object)CloneGlobalConfigHandler.getInstance().getProfileName(cloneConfigDO));
            this.addScreenLayout(cloneConfigDO, dynaJSON, dynaJSON);
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Long parentCustomerId = CloneGlobalConfigHandler.getInstance().getCustomerId(configDOFromDB);
        final Long childCustomerId = CloneGlobalConfigHandler.getInstance().getCustomerId(cloneConfigDO);
        if (!parentCustomerId.equals(childCustomerId)) {
            try {
                this.cloneAppLockPolicyApps(configID, configDOFromDB, cloneConfigDO);
                this.cloneWebClipsPolicies(configID, configDOFromDB, cloneConfigDO);
                this.cloneHomeScreenLayoutPolicy(configID, configDOFromDB, cloneConfigDO);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in cloneConfigDO of AppLockPolicyFormBeanExtn", ex);
            }
        }
    }
}
