package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

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
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class NonMacDevicesNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> notApplicableList = this.getNonMacResourceIds(configNotApplicable.resourceList);
        return notApplicableList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.profile.filevault_not_applicable_model");
        }
        catch (final Exception ex) {
            NonMacDevicesNotApplicableHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  NonMacDevicesNotApplicableHandler:", ex);
        }
    }
    
    public List getNonMacResourceIds(final List resourceIDs) {
        List<Long> notApplicableList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            final Criteria resIDCri = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria modelTypeCri = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 3, 4 }, 9);
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
                NonMacDevicesNotApplicableHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  NonMacDevicesNotApplicableHandler:", e);
            }
        }
        catch (final Exception ex) {
            NonMacDevicesNotApplicableHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  NonMacDevicesNotApplicableHandler:", ex);
        }
        final Set<Long> removedduplicatedEnries = new HashSet<Long>(notApplicableList);
        NonMacDevicesNotApplicableHandler.LOGGER.log(Level.INFO, "NotApplicableHander: FileVault/Firmware Policy not applicable to these Non Mac devices {0}", removedduplicatedEnries);
        return new ArrayList(removedduplicatedEnries);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
