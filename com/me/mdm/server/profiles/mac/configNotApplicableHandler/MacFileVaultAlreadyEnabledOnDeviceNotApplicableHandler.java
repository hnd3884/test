package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List resourceList = configNotApplicable.resourceList;
        final Long collnId = configNotApplicable.collectionId;
        final List deviceswithFVEnabled = getFileVaultEnabledDevices(resourceList);
        final List<Long> deviceWithThisCollectionAlready = MacFileVaultProfileAlreadyExistsNotApplicableHandler.getNAListExludingCurrentCollection(resourceList, collnId);
        MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: FileVault already enabled devices : {0}", deviceswithFVEnabled);
        MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: This FileVault profile Collection already distributed to..  : {0}", deviceWithThisCollectionAlready);
        deviceswithFVEnabled.removeAll(deviceWithThisCollectionAlready);
        final List<Long> devicesWithFVRecoveryKeyImported = getFilevaultKeyImportDevices(deviceswithFVEnabled);
        MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: Devices that has FV Recovery key imported , so allowing them to distribute : {0}", devicesWithFVRecoveryKeyImported);
        deviceswithFVEnabled.removeAll(devicesWithFVRecoveryKeyImported);
        MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.INFO, "FileVaultLog: Setting NA for these resIDs : {0}", deviceswithFVEnabled);
        return deviceswithFVEnabled;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            String errorURL = "$(mdmUrl)/help/profile_management/ios/mdm_filevault_encryption.html?$(traceurl)&pgSrc=$(pageSource)#import_existing_key";
            errorURL = "\"" + MDMUtil.replaceProductUrlLoaderValuesinText(errorURL, "ProfileNotApplicable") + "\"";
            final String remarks = MDMI18N.getI18Nmsg("mdm.profile.fv_already_enabled_na", new Object[] { errorURL });
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, remarks);
        }
        catch (final Exception ex) {
            MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in  MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler setNotApplicableStatus():", ex);
        }
    }
    
    public static List<Long> getFileVaultEnabledDevices(final List<Long> resourceIDList) {
        final Criteria resIDCri = new Criteria(new Column("MDMDeviceFileVaultInfo", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8);
        final Criteria enabledCri = new Criteria(new Column("MDMDeviceFileVaultInfo", "IS_ENCRYPTION_ENABLED"), (Object)true, 0);
        final List<Long> resListLong = new ArrayList<Long>();
        try {
            final List resList = DBUtil.getDistinctColumnValue("MDMDeviceFileVaultInfo", "RESOURCE_ID", resIDCri.and(enabledCri));
            for (int i = 0; i < resList.size(); ++i) {
                resListLong.add(Long.parseLong(resList.get(i)));
            }
            return resListLong;
        }
        catch (final Exception e) {
            MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in  MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler getFileVaultEnabledDevices():", e);
            return new ArrayList<Long>();
        }
    }
    
    public static List<Long> getFilevaultKeyImportDevices(final List<Long> resourceIDList) {
        List<Long> resListLong = new ArrayList<Long>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerIDCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("MDMFileVaultRotateKeyImportInfo", "CUSTOMER_ID"), 0);
        final Criteria serialNoCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)Column.getColumn("MDMFileVaultRotateKeyImportInfo", "DEVICE_IDENTIFIER"), 0);
        sQuery.addJoin(new Join("MdDeviceInfo", "MDMFileVaultRotateKeyImportInfo", customerIDCriteria.and(serialNoCriteria), 2));
        sQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        try {
            final DataObject dob = MDMUtil.getPersistence().get(sQuery);
            if (dob.containsTable("ManagedDevice")) {
                final Iterator itr = dob.getRows("ManagedDevice");
                resListLong = MDMDBUtil.getColumnValuesAsList(itr, "RESOURCE_ID");
            }
        }
        catch (final Exception e) {
            MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in  MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler getFilevaultKeyImportDevices():", e);
        }
        return resListLong;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
