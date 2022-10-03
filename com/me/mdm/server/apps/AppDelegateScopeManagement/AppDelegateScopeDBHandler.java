package com.me.mdm.server.apps.AppDelegateScopeManagement;

import java.util.Iterator;
import com.adventnet.persistence.DataAccess;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.device.api.model.apps.AppDelegateScopeModel;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppDelegateScopeDBHandler
{
    public Logger logger;
    
    public AppDelegateScopeDBHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void addOrModifyDelegatedScope(final JSONObject delegateData, final AppDelegateScopeModel model) throws Exception {
        try {
            final DataObject dataObject = this.constructDelegateDataObj(delegateData, model.getConfigDataItemId());
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addOrModifyDelegatedScope", e);
            throw e;
        }
    }
    
    private DataObject constructDelegateDataObj(final JSONObject delegateScopeData, final Long configDataItemId) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AppDelegateScopes"));
        query.setCriteria(new Criteria(new Column("AppDelegateScopes", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        query.addSelectColumn(new Column("AppDelegateScopes", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (dataObject.isEmpty()) {
            final Row row = new Row("AppDelegateScopes");
            row.set("CONFIG_DATA_ITEM_ID", (Object)configDataItemId);
            row.set("DELEGATION_APP_RESTRICTIONS", (Object)delegateScopeData.optInt("delegation_app_restrictions"));
            row.set("DELEGATION_BLOCK_UNINSTALL", (Object)delegateScopeData.optInt("delegation_block_uninstall"));
            row.set("DELEGATION_CERT_INSTALL", (Object)delegateScopeData.optInt("delegation_cert_install"));
            row.set("DELEGATION_ENABLE_SYSTEM_APP", (Object)delegateScopeData.optInt("delegation_enable_system_app"));
            row.set("DELEGATION_PACKAGE_ACCESS", (Object)delegateScopeData.optInt("delegation_package_access"));
            row.set("DELEGATION_PERMISSION_GRANT", (Object)delegateScopeData.optInt("delegation_permission_grant"));
            dataObject.addRow(row);
        }
        else {
            final Row row = dataObject.getFirstRow("AppDelegateScopes");
            row.set("DELEGATION_APP_RESTRICTIONS", (Object)delegateScopeData.optInt("delegation_app_restrictions"));
            row.set("DELEGATION_BLOCK_UNINSTALL", (Object)delegateScopeData.optInt("delegation_block_uninstall"));
            row.set("DELEGATION_CERT_INSTALL", (Object)delegateScopeData.optInt("delegation_cert_install"));
            row.set("DELEGATION_ENABLE_SYSTEM_APP", (Object)delegateScopeData.optInt("delegation_enable_system_app"));
            row.set("DELEGATION_PACKAGE_ACCESS", (Object)delegateScopeData.optInt("delegation_package_access"));
            row.set("DELEGATION_PERMISSION_GRANT", (Object)delegateScopeData.optInt("delegation_permission_grant"));
            dataObject.updateRow(row);
        }
        return dataObject;
    }
    
    public List constructResourceList(final Long configDataItemId) throws DataAccessException {
        ArrayList resourceList = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDelegateScopes"));
            sQuery.addJoin(new Join("AppDelegateScopes", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            sQuery.addJoin(new Join("InstallAppPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            sQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("Collection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addSelectColumn(new Column("Collection", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            sQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
            sQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            sQuery.addSelectColumn(new Column("InstallAppPolicy", "CONFIG_DATA_ITEM_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("AppDelegateScopes", "CONFIG_DATA_ITEM_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
            sQuery.setCriteria(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
            resourceList = new ArrayList();
            final DataObject dO = DataAccess.get(sQuery);
            if (!dO.isEmpty()) {
                final Iterator it = dO.getRows("RecentProfileForResource");
                while (it.hasNext()) {
                    final Row row = it.next();
                    final Long resourceId = (Long)row.get("RESOURCE_ID");
                    resourceList.add(resourceId);
                }
            }
        }
        catch (final Exception invCmdException) {
            this.logger.log(Level.SEVERE, "Exception while invokeCommandForAssociatedApps", invCmdException);
            throw invCmdException;
        }
        return resourceList;
    }
}
