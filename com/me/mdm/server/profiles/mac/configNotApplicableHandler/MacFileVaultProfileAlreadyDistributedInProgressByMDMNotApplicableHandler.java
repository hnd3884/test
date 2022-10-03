package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> naList = this.getFileVaultDistributedResources(configNotApplicable.resourceList, configNotApplicable.collectionId);
        MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: Not Applicable DeviceList as Profile is already distributed : {0}", naList);
        return naList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.profile.filevault_already_distributed");
        }
        catch (final Exception ex) {
            MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler setNotApplicableStatus : ", ex);
        }
    }
    
    public List<Long> getFileVaultDistributedResources(final List<Long> naList, final Long collnID) {
        final List notApplicableList = new ArrayList();
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        sq.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sq.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sq.addJoin(new Join("ConfigData", "Configuration", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 2));
        sq.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        final Criteria resIDCri = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)naList.toArray(), 8);
        final Criteria fvPolicyCri = new Criteria(new Column("Configuration", "CONFIG_ID"), (Object)770, 0);
        final Criteria collnCri = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collnID, 1);
        final Criteria collnStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new Integer[] { 12, 8 }, 9);
        sq.addSelectColumn(new Column((String)null, "*"));
        sq.setCriteria(resIDCri.and(fvPolicyCri).and(collnCri).and(collnStatus));
        try {
            final DataObject dObj = MDMUtil.getPersistence().get(sq);
            if (dObj != null && dObj.containsTable("RecentProfileForResource")) {
                final Iterator rowItr = dObj.getRows("RecentProfileForResource");
                while (rowItr.hasNext()) {
                    final Row row = rowItr.next();
                    final Long resID = (Long)row.get("RESOURCE_ID");
                    notApplicableList.add(resID);
                }
            }
        }
        catch (final DataAccessException e) {
            MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in getFileVaultDistributedResources setNotApplicableStatus : ", (Throwable)e);
        }
        catch (final Exception e2) {
            MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in getFileVaultDistributedResources setNotApplicableStatus : ", e2);
        }
        return notApplicableList;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
