package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import java.util.Set;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFileVaultProfileOSVersionNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> naList = this.getResourcesWithLessthanGeraterThanGivenVersion(configNotApplicable.resourceList, 10.13f, false);
        MacFileVaultProfileOSVersionNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: Not applicable mac OS version :{0}", naList);
        return naList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.profile.filevault_os_not_compatable");
        }
        catch (final Exception ex) {
            MacFileVaultProfileOSVersionNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultProfileOSVersionNotApplicableHandler setNotApplicableStatus : ", ex);
        }
    }
    
    public List getResourcesWithLessthanGeraterThanGivenVersion(final List resourceIDs, final Float version, final boolean isGreaterThan) {
        final List<Long> osVersionFilteredList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            final Criteria resIDCri = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "OS_VERSION"));
            selectQuery.setCriteria(resIDCri);
            DataObject dO = null;
            try {
                dO = MDMUtil.getPersistence().get(selectQuery);
                if (!dO.isEmpty()) {
                    final Iterator itr = dO.getRows("MdDeviceInfo");
                    while (itr.hasNext()) {
                        final Row row = itr.next();
                        final String deviceVersion = (String)row.get("OS_VERSION");
                        final boolean isGreaterEqual = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceVersion, version);
                        if (isGreaterEqual == isGreaterThan) {
                            final Long resourceID = (Long)row.get("RESOURCE_ID");
                            osVersionFilteredList.add(resourceID);
                        }
                    }
                }
            }
            catch (final Exception e) {
                MacFileVaultProfileOSVersionNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: Exception in  MacFileVaultProfileOSVersionNotApplicableHandler getResourcesWithLessthanGeraterThanGivenVersion:", e);
            }
        }
        catch (final Exception ex) {
            MacFileVaultProfileOSVersionNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: Exception in MacFileVaultProfileOSVersionNotApplicableHandler getResourcesWithLessthanGeraterThanGivenVersion:", ex);
        }
        final Set<Long> removedduplicatedEnries = new HashSet<Long>(osVersionFilteredList);
        MacFileVaultProfileOSVersionNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: FileVault Policy not applicable to these  Mac devices less than specified version {0}", removedduplicatedEnries);
        return new ArrayList(removedduplicatedEnries);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
