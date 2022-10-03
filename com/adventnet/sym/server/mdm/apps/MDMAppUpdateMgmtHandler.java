package com.adventnet.sym.server.mdm.apps;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.AppVersionChecker;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Logger;

public class MDMAppUpdateMgmtHandler
{
    public Logger logger;
    
    public MDMAppUpdateMgmtHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void setAppUpdateAvailable(final Long resourceID, final Long appGroupId, final boolean isUpdate) throws DataAccessException {
        final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
        uQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)isUpdate);
        final Criteria resourceCriteira = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria appCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupId, 0);
        uQuery.setCriteria(resourceCriteira.and(appCriteria));
        MDMUtil.getPersistence().update(uQuery);
    }
    
    public boolean isAppCatalogUpgradeAction(final Long installedAppId, final Long publishedAppId, final int appType, final int platformType) {
        try {
            final AppVersionChecker checker = AppVersionChecker.getInstance(platformType);
            if (installedAppId != null && installedAppId != 0L) {
                if (appType == 2 && platformType != 2) {
                    return installedAppId.compareTo(publishedAppId) != 0;
                }
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
                sQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
                sQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
                final Criteria appIDCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)new Long[] { publishedAppId, installedAppId }, 8);
                sQuery.setCriteria(appIDCriteria);
                final DataObject dO = MDMUtil.getPersistence().get(sQuery);
                final JSONObject pubVersionDetails = new JSONObject();
                final JSONObject installedVersionDetails = new JSONObject();
                if (!dO.isEmpty()) {
                    final Iterator itr = dO.getRows("MdAppDetails");
                    while (itr.hasNext()) {
                        final Row row = itr.next();
                        final Long appId = (Long)row.get("APP_ID");
                        if (appId.equals(installedAppId)) {
                            installedVersionDetails.put("APP_ID", (Object)appId);
                            installedVersionDetails.put("APP_VERSION", row.get("APP_VERSION"));
                            installedVersionDetails.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
                        }
                        if (appId.equals(publishedAppId)) {
                            pubVersionDetails.put("APP_ID", (Object)appId);
                            pubVersionDetails.put("APP_VERSION", row.get("APP_VERSION"));
                            pubVersionDetails.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
                        }
                    }
                    return checker.isAppVersionGreater(pubVersionDetails, installedVersionDetails);
                }
            }
        }
        catch (final NumberFormatException e) {
            this.logger.log(Level.WARNING, "Version check for non number string", e.getMessage());
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while checking isUpgradeAction(): ", e2);
        }
        return false;
    }
}
