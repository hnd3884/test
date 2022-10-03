package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import java.util.Collection;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class VPPManagedDeviceHandler
{
    public Logger logger;
    private static VPPManagedDeviceHandler vppDeviceHandler;
    
    public VPPManagedDeviceHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPManagedDeviceHandler getInstance() {
        if (VPPManagedDeviceHandler.vppDeviceHandler == null) {
            VPPManagedDeviceHandler.vppDeviceHandler = new VPPManagedDeviceHandler();
        }
        return VPPManagedDeviceHandler.vppDeviceHandler;
    }
    
    private List getVPPManagedDeviceDetails(final Set vppDeviceSerialIdSet, final Long appGroupId) {
        final List deviceDetails = new ArrayList();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            sQuery.addJoin(new Join("MdDeviceInfo", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria deviceIdCri = new Criteria(new Column("MdDeviceInfo", "SERIAL_NUMBER"), (Object)vppDeviceSerialIdSet.toArray(), 8);
            final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            sQuery.setCriteria(appGroupCri.and(deviceIdCri));
            sQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            sQuery.addSelectColumn(new Column("MdDeviceInfo", "SERIAL_NUMBER"));
            final DataObject deviceInfoDo = MDMUtil.getPersistence().get(sQuery);
            final Iterator item = deviceInfoDo.getRows("MdDeviceInfo");
            while (item.hasNext()) {
                final Row deviceInfoRow = item.next();
                try {
                    final Properties prop = new Properties();
                    ((Hashtable<String, Object>)prop).put("RESOURCE_ID", deviceInfoRow.get("RESOURCE_ID"));
                    ((Hashtable<String, Object>)prop).put("SERIAL_NUMBER", deviceInfoRow.get("SERIAL_NUMBER"));
                    deviceDetails.add(prop);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, " Exception while getVPPManagedUserDetails {0}", e);
                }
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, " Exception while getVPPManagedUserDetails ", e2);
        }
        return deviceDetails;
    }
    
    public Properties mapSerialNoFromVppToResId(final Properties deviceToLicProp, final Long appGroupId) {
        final Properties managedDeviceProp = new Properties();
        try {
            final Set vppDeviceSerialIdSet = deviceToLicProp.keySet();
            List deviceList = null;
            if (vppDeviceSerialIdSet != null && !vppDeviceSerialIdSet.isEmpty()) {
                deviceList = this.getVPPManagedDeviceDetails(vppDeviceSerialIdSet, appGroupId);
                for (final Properties deviceProp : deviceList) {
                    final Long resourceId = ((Hashtable<K, Long>)deviceProp).get("RESOURCE_ID");
                    final String serialno = ((Hashtable<K, Object>)deviceProp).get("SERIAL_NUMBER").toString();
                    ((Hashtable<Long, Object>)managedDeviceProp).put(resourceId, ((Hashtable<K, Object>)deviceToLicProp).get(serialno));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while mapSerialNoFromVppToResId ", e);
        }
        return managedDeviceProp;
    }
    
    public List getlicenseNotAssociatedResList(final List toBeAssociatedresList, final Long appGroupId) {
        final List alreadyLicenseAssociatedResList = new ArrayList();
        try {
            this.logger.log(Level.INFO, "Inside get License Not associated List : LicenseToBeAssociatedList{0} , AppGroupID:{1}", new Object[] { toBeAssociatedresList, appGroupId });
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicenseToAppGroupRel"));
            sQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdVPPLicenseDetails", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
            sQuery.addJoin(new Join("MdVPPLicenseDetails", "MdVPPLicenseToDevice", new String[] { "LICENSE_DETAIL_ID" }, new String[] { "LICENSE_DETAIL_ID" }, 2));
            final Criteria AppGrpIdCri = new Criteria(new Column("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria deviceLsitCri = new Criteria(new Column("MdVPPLicenseToDevice", "MANAGED_DEVICE_ID"), (Object)toBeAssociatedresList.toArray(), 8);
            final Criteria LicenseAssociatedCri = new Criteria(new Column("MdVPPLicenseDetails", "STATUS"), (Object)1, 0);
            sQuery.setCriteria(AppGrpIdCri.and(deviceLsitCri).and(LicenseAssociatedCri));
            sQuery.addSelectColumn(new Column("MdVPPLicenseToDevice", "*"));
            final DataObject licenseToDeviceDO = MDMUtil.getPersistence().get(sQuery);
            final Iterator item = licenseToDeviceDO.getRows("MdVPPLicenseToDevice");
            while (item.hasNext()) {
                final Row licenseToDeviceRow = item.next();
                final Long resId = (Long)licenseToDeviceRow.get("MANAGED_DEVICE_ID");
                alreadyLicenseAssociatedResList.add(resId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception while getting getlicenseNotAssociatedResList {0}", ex);
        }
        this.logger.log(Level.INFO, "License is Already associated to devices:{0} , AppGroupID:{1}", new Object[] { alreadyLicenseAssociatedResList, appGroupId });
        toBeAssociatedresList.removeAll(alreadyLicenseAssociatedResList);
        this.logger.log(Level.INFO, "Final List After Filtering Already Associated License for device:{0} , AppGroupID:{1}", new Object[] { alreadyLicenseAssociatedResList, appGroupId });
        return toBeAssociatedresList;
    }
    
    public List getlicenseNotAssociatedResList(final List toBeAssociatedresList, final Long appGroupId, final Long businessStoreID) {
        final List alreadyLicenseAssociatedResList = new ArrayList();
        try {
            this.logger.log(Level.INFO, "Inside get License Not associated List : LicenseToBeAssociatedList{0} , AppGroupID:{1}", new Object[] { toBeAssociatedresList, appGroupId });
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreToVppRel"));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppAsset", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVppAssetToManagedDeviceRel", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            final Criteria tokenCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)toBeAssociatedresList.toArray(), 8);
            selectQuery.addSelectColumn(Column.getColumn("MdVppAssetToManagedDeviceRel", "*"));
            selectQuery.setCriteria(tokenCriteria.and(appCriteria).and(resCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator item = dataObject.getRows("MdVppAssetToManagedDeviceRel");
            while (item.hasNext()) {
                final Row licenseToDeviceRow = item.next();
                final Long resId = (Long)licenseToDeviceRow.get("MANAGED_DEVICE_ID");
                alreadyLicenseAssociatedResList.add(resId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception while getting getlicenseNotAssociatedResList {0}", ex);
        }
        this.logger.log(Level.INFO, "License is Already associated to devices:{0} , AppGroupID:{1}", new Object[] { alreadyLicenseAssociatedResList, appGroupId });
        toBeAssociatedresList.removeAll(alreadyLicenseAssociatedResList);
        this.logger.log(Level.INFO, "Final List After Filtering Already Associated License for device:{0} , AppGroupID:{1}", new Object[] { alreadyLicenseAssociatedResList, appGroupId });
        return toBeAssociatedresList;
    }
    
    static {
        VPPManagedDeviceHandler.vppDeviceHandler = null;
    }
}
