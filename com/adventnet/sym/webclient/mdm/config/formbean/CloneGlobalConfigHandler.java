package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.Hashtable;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class CloneGlobalConfigHandler
{
    public static CloneGlobalConfigHandler cloneGlobalConfigHandler;
    private Logger logger;
    
    public CloneGlobalConfigHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static CloneGlobalConfigHandler getInstance() {
        if (CloneGlobalConfigHandler.cloneGlobalConfigHandler == null) {
            CloneGlobalConfigHandler.cloneGlobalConfigHandler = new CloneGlobalConfigHandler();
        }
        return CloneGlobalConfigHandler.cloneGlobalConfigHandler;
    }
    
    public Object getConfigDataItemId(final Integer configId, final DataObject cloneConfigDO) throws DataAccessException {
        Object configDatItem = -1L;
        final Criteria configIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)configId, 0);
        final Object configDataId = cloneConfigDO.getValue("ConfigData", "CONFIG_DATA_ID", configIdCriteria);
        final Criteria configDataIdCriteria = new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), configDataId, 0);
        configDatItem = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", configDataIdCriteria);
        return configDatItem;
    }
    
    public Long getCustomerId(final DataObject cloneConfigDO) throws DataAccessException {
        return (Long)cloneConfigDO.getFirstValue("ProfileToCustomerRel", "CUSTOMER_ID");
    }
    
    public Boolean isProfileForAllCustomers(final DataObject cloneConfigDO) throws DataAccessException {
        final Integer profileSharedScope = (Integer)cloneConfigDO.getFirstValue("Profile", "PROFILE_SHARED_SCOPE");
        return profileSharedScope == 1;
    }
    
    public int getPlatformType(final DataObject cloneConfigDO) throws DataAccessException {
        return (int)cloneConfigDO.getFirstValue("Profile", "PLATFORM_TYPE");
    }
    
    public Long getCollectionId(final DataObject dataObject) throws DataAccessException {
        return (Long)dataObject.getFirstValue("ProfileToCollection", "COLLECTION_ID");
    }
    
    public String getProfileName(final DataObject dataObject) throws DataAccessException {
        return (String)dataObject.getFirstValue("Profile", "PROFILE_NAME");
    }
    
    public List getKioskConfiguredApps(final DataObject configDOFromDB, final String tableAlias, final String colAlias) throws DataAccessException {
        final List<Long> bundleIds = new ArrayList<Long>();
        final Iterator<Row> iterator = configDOFromDB.getRows(tableAlias);
        while (iterator.hasNext()) {
            final Row appGroupRow = iterator.next();
            bundleIds.add((Long)appGroupRow.get(colAlias));
        }
        return bundleIds;
    }
    
    public HashMap getParentChildAppsUVHMap(final DataObject cloneConfigDO, final List configureAppIds) throws Exception {
        final HashMap<Long, Object> parentChildUVHMap = new HashMap<Long, Object>();
        final Long customerId = getInstance().getCustomerId(cloneConfigDO);
        final int platform = getInstance().getPlatformType(cloneConfigDO);
        final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery1.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)configureAppIds.toArray(), 8));
        selectQuery1.addSelectColumn(new Column("MdAppGroupDetails", "*"));
        final DataObject parentAppDetails = DataAccess.get(selectQuery1);
        final Iterator<Row> iterator = parentAppDetails.getRows("MdAppGroupDetails");
        final List<String> bundleIds = new ArrayList<String>();
        while (iterator.hasNext()) {
            final Row parentAppGroup = iterator.next();
            bundleIds.add((String)parentAppGroup.get("IDENTIFIER"));
        }
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria bundleIdCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleIds.toArray(), 8);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        selectQuery2.setCriteria(customerCriteria.and(bundleIdCriteria).and(platformCriteria));
        selectQuery2.addSelectColumn(new Column("MdAppGroupDetails", "*"));
        final DataObject childAppDetails = DataAccess.get(selectQuery2);
        final Iterator<Row> iterator2 = parentAppDetails.getRows("MdAppGroupDetails");
        while (iterator2.hasNext()) {
            final Row parentAppRow = iterator2.next();
            final Long parentAppGroupId = (Long)parentAppRow.get("APP_GROUP_ID");
            final String identifier = (String)parentAppRow.get("IDENTIFIER");
            final Criteria appBundleIdCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 0, (boolean)AppsUtil.getInstance().getIsBundleIdCaseSenstive(platform));
            Object childAppGroupId = childAppDetails.getValue("MdAppGroupDetails", "APP_GROUP_ID", appBundleIdCriteria);
            if (childAppGroupId == null) {
                childAppGroupId = cloneConfigDO.getValue("MdAppGroupDetails", "APP_GROUP_ID", appBundleIdCriteria);
            }
            if (childAppGroupId == null) {
                final Properties properties = new Properties();
                ((Hashtable<String, Object>)properties).put("APP_NAME", parentAppRow.get("GROUP_DISPLAY_NAME"));
                ((Hashtable<String, Object>)properties).put("APP_TITLE", parentAppRow.get("GROUP_DISPLAY_NAME"));
                ((Hashtable<String, Object>)properties).put("IDENTIFIER", parentAppRow.get("IDENTIFIER"));
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", platform);
                ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerId);
                ((Hashtable<String, Integer>)properties).put("APP_TYPE", 0);
                ((Hashtable<String, Boolean>)properties).put("NOTIFY_ADMIN", false);
                ((Hashtable<String, String>)properties).put("APP_VERSION", "--");
                ((Hashtable<String, String>)properties).put("APP_NAME_SHORT_VERSION", "--");
                this.logger.log(Level.INFO, "Creating app details for MSP all customer profile cloning {0}", new Object[] { properties });
                childAppGroupId = new AppDataHandler().addAppsOnProfileCloning(properties, cloneConfigDO);
            }
            parentChildUVHMap.put(parentAppGroupId, childAppGroupId);
        }
        return parentChildUVHMap;
    }
    
    public void cloneHomeScreenPolicy(final JSONObject screenLayoutJSON, final HashMap appUVHMap, final HashMap webClipsUVHMap, final String pagesAlias, final String pageLayoutAlias) throws DataAccessException {
        final JSONArray pages = screenLayoutJSON.getJSONArray(pagesAlias);
        for (int i = 0; i < pages.length(); ++i) {
            final JSONObject pageJSON = pages.getJSONObject(i);
            final JSONArray screenPageLayoutArray = pageJSON.getJSONArray(pageLayoutAlias);
            for (int j = 0; j < screenPageLayoutArray.length(); ++j) {
                final JSONObject screenPageLayoutJSON = screenPageLayoutArray.getJSONObject(j);
                final int pageType = screenPageLayoutJSON.getInt("PAGE_LAYOUT_TYPE");
                switch (pageType) {
                    case 1: {
                        final Long parentAppId = screenPageLayoutJSON.getLong("APP_GROUP_ID");
                        final Object childAppId = appUVHMap.get(parentAppId);
                        if (childAppId != null) {
                            screenPageLayoutJSON.put("APP_GROUP_ID", childAppId);
                        }
                        break;
                    }
                    case 2: {
                        final Long parentWebClipId = screenPageLayoutJSON.getLong("WEBCLIP_POLICY_ID");
                        final Object childWebClipId = webClipsUVHMap.get(parentWebClipId);
                        if (childWebClipId != null) {
                            screenPageLayoutJSON.put("WEBCLIP_POLICY_ID", childWebClipId);
                        }
                        break;
                    }
                    case 3: {
                        this.cloneHomeScreenPolicy(screenPageLayoutJSON, appUVHMap, webClipsUVHMap, pagesAlias, pageLayoutAlias);
                        break;
                    }
                }
            }
        }
    }
    
    static {
        CloneGlobalConfigHandler.cloneGlobalConfigHandler = null;
    }
}
