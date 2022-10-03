package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import java.util.Set;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.mdm.server.config.MDMCollectionUtil;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableListener;

public class TvOSProfileNotApplicableDeviceHandler implements MDMCollectionNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final List resourceList, final Long collectionID, final List configId, final long customerId) {
        try {
            final int profilePlatformType = MDMCollectionUtil.getPlatformType(collectionID);
            if (profilePlatformType == 7) {
                return this.getNonTVOSResourceIds(resourceList);
            }
        }
        catch (final Exception e) {
            TvOSProfileNotApplicableDeviceHandler.LOGGER.log(Level.SEVERE, "Exception in getting restriction applied.", e);
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "dc.mdm.devicemgmt.not_supported_profile_platform");
        }
        catch (final DataAccessException e) {
            TvOSProfileNotApplicableDeviceHandler.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    public List getNonTVOSResourceIds(final List resourceIDs) {
        List<Long> notApplicableList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            final Criteria resIDCri = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria modelTypeCri = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 5 }, 9);
            selectQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.setCriteria(resIDCri.and(modelTypeCri));
            DataObject dO = null;
            try {
                dO = MDMUtil.getPersistence().get(selectQuery);
                if (!dO.isEmpty()) {
                    notApplicableList = DBUtil.getColumnValuesAsList(dO.getRows("MdDeviceInfo"), "RESOURCE_ID");
                }
            }
            catch (final Exception e) {
                TvOSProfileNotApplicableDeviceHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  NonTvOSDevicesNotApplicableHandler:", e);
            }
        }
        catch (final Exception ex) {
            TvOSProfileNotApplicableDeviceHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  NonTvOSDevicesNotApplicableHandler:", ex);
        }
        final Set<Long> removedduplicatedEnries = new HashSet<Long>(notApplicableList);
        TvOSProfileNotApplicableDeviceHandler.LOGGER.log(Level.INFO, "NotApplicableHander: tvOS restriction not applicable to these Non tvOS devices {0}", removedduplicatedEnries);
        return new ArrayList(removedduplicatedEnries);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
