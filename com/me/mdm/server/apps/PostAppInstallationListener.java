package com.me.mdm.server.apps;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PostAppInstallationListener
{
    private static Logger logger;
    
    public List getAppGroupIDfromAppID(final List installedAppIDList) {
        final List appGroupIDList = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
            final Criteria appGroupCrt = new Criteria(new Column("MdAppToGroupRel", "APP_ID"), (Object)installedAppIDList.toArray(), 8);
            sq.setCriteria(appGroupCrt);
            sq.addSelectColumn(new Column("MdAppToGroupRel", "APP_GROUP_ID"));
            sq.addSelectColumn(new Column("MdAppToGroupRel", "APP_ID"));
            final DataObject appGroupIDDO = MDMUtil.getPersistence().get(sq);
            if (!appGroupIDDO.isEmpty()) {
                final Iterator appGroupIDIterator = appGroupIDDO.getRows("MdAppToGroupRel");
                while (appGroupIDIterator.hasNext()) {
                    final Row row = appGroupIDIterator.next();
                    appGroupIDList.add(row.get("APP_GROUP_ID"));
                }
            }
        }
        catch (final Exception E) {
            PostAppInstallationListener.logger.log(Level.SEVERE, "Error in fetching App Group ID :", E);
        }
        return appGroupIDList;
    }
    
    public void pushAppConfigurationProfile(final List installedAppGroupIDList, final Long resourceID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ManagedAppConfigurationPolicy"));
            final Criteria appGroupIDCrt = new Criteria(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"), (Object)installedAppGroupIDList.toArray(), 8);
            final Criteria statusCrt = new Criteria(new Column("CollnToResources", "STATUS"), (Object)7, 0);
            final Criteria markedForDeleteCrt = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            final Criteria resourceCrt = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
            sq.addJoin(new Join("ManagedAppConfigurationPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            sq.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sq.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sq.addJoin(new Join("CfgDataToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sq.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
            sq.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "CONFIG_DATA_ITEM_ID"));
            sq.setCriteria(appGroupIDCrt.and(statusCrt.and(markedForDeleteCrt.and(resourceCrt))));
            sq.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            sq.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
            sq.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            sq.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("CollnToResources", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("CollnToResources", "RESOURCE_ID"));
            sq.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"));
            sq.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
            final DataObject configDO = MDMUtil.getPersistence().get(sq);
            final List collectionIDList = new ArrayList();
            final List resourceIDList = new ArrayList();
            resourceIDList.add(resourceID);
            if (!configDO.isEmpty()) {
                final List collectionID = DBUtil.getColumnValuesAsList(configDO.getRows("CfgDataToCollection"), "COLLECTION_ID");
                final Iterator DOIterator = configDO.getRows("CollnToResources");
                while (DOIterator.hasNext()) {
                    final Row row = DOIterator.next();
                    final Long appConfigCollectionID = (Long)row.get("COLLECTION_ID");
                    if (collectionID.contains(appConfigCollectionID)) {
                        collectionIDList.add(appConfigCollectionID);
                    }
                }
                if (!collectionIDList.isEmpty()) {
                    PostAppInstallationListener.logger.log(Level.INFO, "Collection ID''s fetched for auto push app configuration :{0}", collectionIDList);
                    final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIDList, "InstallApplicationConfiguration");
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceIDList);
                    PostAppInstallationListener.logger.log(Level.INFO, "Successfully Assigned Command to devices for redistribution");
                    final String remarks = "mdm.profile.distribution.waitingfordeviceinfo";
                    final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
                    collnUpdater.updateStatusForCollntoRes(resourceIDList, collectionIDList, 18, remarks);
                }
            }
        }
        catch (final Exception E) {
            PostAppInstallationListener.logger.log(Level.SEVERE, "Error in fetching Collection ID from AppGroupID : Method :pushAppConfigurationProfile,Exception:", E);
        }
    }
    
    static {
        PostAppInstallationListener.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
