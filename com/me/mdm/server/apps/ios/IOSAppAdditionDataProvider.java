package com.me.mdm.server.apps.ios;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.me.mdm.server.apps.BaseAppAdditionDataProvider;

public class IOSAppAdditionDataProvider extends BaseAppAdditionDataProvider
{
    @Override
    public JSONObject modifyAppAdditionData(final JSONObject appAdditionDetails) throws Exception {
        final Boolean storeApp = appAdditionDetails.has("PACKAGE_TYPE") && (appAdditionDetails.getInt("PACKAGE_TYPE") == 1 || appAdditionDetails.getInt("PACKAGE_TYPE") == 0);
        if (storeApp) {
            return this.modifyStoreAppAdditionData(appAdditionDetails);
        }
        return appAdditionDetails;
    }
    
    private JSONObject modifyStoreAppAdditionData(final JSONObject appAdditionDetails) throws Exception {
        final Long oldCollectionId = appAdditionDetails.optLong("oldCollectionId", -1L);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
        selectQuery.addJoin(new Join("MdAppToCollection", "InstallAppPolicy", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addSelectColumn(new Column("AppConfigPolicy", "APP_CONFIG_ID"));
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "APP_ID"));
        selectQuery.addSelectColumn(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"));
        selectQuery.setCriteria(new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)oldCollectionId, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty()) {
            final Row appConfigRow = dataObject.getRow("AppConfigPolicy");
            final Long appConfigId = (Long)appConfigRow.get("APP_CONFIG_ID");
            final Row mdapptocollection = dataObject.getRow("MdAppToCollection");
            final Long appId = (Long)mdapptocollection.get("APP_ID");
            final Long configDataItemId = (Long)appConfigRow.get("CONFIG_DATA_ITEM_ID");
            if (appConfigId != null && appConfigId != -1L) {
                final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
                final String configTemplate = appConfigDataHandler.getAppConfigTemplateFromConfigDataItemID(configDataItemId);
                final String appConfiguration = appConfigDataHandler.getAppConfig(appConfigId);
                if (!MDMStringUtils.isEmpty(appConfiguration)) {
                    appAdditionDetails.put("APP_CONFIGURATION", (Object)appConfiguration);
                }
                if (!MDMStringUtils.isEmpty(configTemplate)) {
                    appAdditionDetails.put("APP_CONFIG_TEMPLATE", (Object)new JSONObject().put("APP_CONFIG_FORM", (Object)configTemplate));
                }
            }
        }
        return appAdditionDetails;
    }
}
