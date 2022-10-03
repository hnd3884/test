package com.me.mdm.server.apps.ios.vpp;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppLicenseHandler;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.Collection;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAssetsHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Logger;

public class VPPLicenseSyncHandler
{
    final Logger logger;
    List deviceListWithAppDistributedInMDM;
    List deviceListWithAppFromOtherToken;
    List deviceListWithAppFromSameToken;
    DataObject vpprelDO;
    List insufficientLicenseDeviceList;
    Properties userToDeviceMap;
    Properties insufficientUserToDeviceMap;
    int availableLicenseCount;
    int assignedLicenseCount;
    int licenseAssignmentType;
    List userBasedAppUsersListInMDM;
    Long customerId;
    Long appGroupId;
    int syncStatus;
    String appStoreId;
    Long tokenID;
    Long assetID;
    Long userID;
    Long businessStoreID;
    boolean isFreeToVpp;
    boolean isVppTokenSync;
    
    public int getSyncStatus() {
        return this.syncStatus;
    }
    
    private void setVPPRelDataObject() throws DataAccessException {
        final SelectQuery selectQuery = AppsUtil.getInstance().getQueryForManagedDeviceListWithAppDistributedInMDM();
        if (this.licenseAssignmentType == 2) {
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            Criteria managedDeviceJoinCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), 0);
            managedDeviceJoinCriteria = managedDeviceJoinCriteria.and(new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "VPP_ASSET_ID"), (Object)this.assetID, 0));
            selectQuery.addJoin(new Join("ManagedDevice", "MdVppAssetToManagedDeviceRel", managedDeviceJoinCriteria, 1));
        }
        else {
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            Criteria vppUserJoinCriteria = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "VPP_USER_ID"), (Object)Column.getColumn("MdVppUser", "VPP_USER_ID"), 0);
            vppUserJoinCriteria = vppUserJoinCriteria.and(new Criteria(Column.getColumn("MdVppUser", "TOKEN_ID"), (Object)this.tokenID, 0));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppUser", vppUserJoinCriteria, 1));
            Criteria vppUserToAssetJoinCri = new Criteria(Column.getColumn("MdVppUser", "VPP_USER_ID"), (Object)Column.getColumn("MdVppAssetToVppUserRel", "VPP_USER_ID"), 0);
            vppUserToAssetJoinCri = vppUserToAssetJoinCri.and(new Criteria(Column.getColumn("MdVppAssetToVppUserRel", "VPP_ASSET_ID"), (Object)this.assetID, 0));
            selectQuery.addJoin(new Join("MdVppUser", "MdVppAssetToVppUserRel", vppUserToAssetJoinCri, 1));
            selectQuery.addSelectColumn(Column.getColumn("MdManagedUserToVppUserRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAssetToVppUserRel", "*"));
        }
        Criteria depConfigJoinCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
        depConfigJoinCriteria = depConfigJoinCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0));
        final Criteria businessCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0);
        selectQuery.addJoin(new Join("RecentProfileForResource", "MDMResourceToDeploymentConfigs", depConfigJoinCriteria.and(businessCriteria), 1));
        final Criteria notApplicableCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1);
        final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)this.appGroupId, 0);
        final Criteria markForDeleteCri = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerId, 0);
        selectQuery.setCriteria(notApplicableCriteria.and(appGroupCri).and(markForDeleteCri).and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"));
        this.vpprelDO = MDMUtil.getPersistence().get(selectQuery);
    }
    
    private DataObject getAssetDO(final String adamID, final Long businessStoreID) throws DataAccessException {
        final SelectQuery selectQuery = VPPAssetsHandler.getInstance().getVppAssetsQuery();
        final Criteria appCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamID, 0);
        final Criteria businessCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
        selectQuery.setCriteria(appCriteria.and(businessCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public VPPLicenseSyncHandler(final Long customerId, final Long userID, final Long appGroupId, final String appStoreId, final Long businessStoreID, final boolean isFreeToVpp, final boolean isVppTokenSync) throws Exception {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
        this.deviceListWithAppDistributedInMDM = new ArrayList();
        this.deviceListWithAppFromOtherToken = new ArrayList();
        this.deviceListWithAppFromSameToken = new ArrayList();
        this.vpprelDO = (DataObject)new WritableDataObject();
        this.insufficientLicenseDeviceList = new ArrayList();
        this.userToDeviceMap = new Properties();
        this.insufficientUserToDeviceMap = new Properties();
        this.userBasedAppUsersListInMDM = new ArrayList();
        this.syncStatus = 0;
        try {
            this.customerId = customerId;
            this.appGroupId = appGroupId;
            this.appStoreId = appStoreId;
            this.businessStoreID = businessStoreID;
            this.isFreeToVpp = isFreeToVpp;
            this.userID = userID;
            final DataObject assetDO = this.getAssetDO(appStoreId, businessStoreID);
            final Row assetRow = assetDO.getFirstRow("MdVppAsset");
            this.assetID = (Long)assetRow.get("VPP_ASSET_ID");
            VPPAssetsHandler.getInstance().updateVPPAssetSyncStatus(this.assetID, 1, null);
            final Row tokenRow = assetDO.getFirstRow("MdVPPTokenDetails");
            this.availableLicenseCount = (int)assetRow.get("AVAILABLE_LICENSE_COUNT");
            this.tokenID = (Long)assetRow.get("TOKEN_ID");
            this.assignedLicenseCount = (int)assetRow.get("ASSIGNED_LICENSE_COUNT");
            this.licenseAssignmentType = (int)tokenRow.get("LICENSE_ASSIGN_TYPE");
            this.syncStatus = (int)assetRow.get("APP_SYNC_STATUS");
            this.isVppTokenSync = isVppTokenSync;
            this.setBusinessStoreIDForDeploymentConfigs();
            this.setVPPRelDataObject();
            this.deviceListWithAppDistributedInMDM = this.getDeviceListWithAppInMDM();
            this.deviceListWithAppFromSameToken = this.getDeviceListWithAppFromSameToken();
            (this.deviceListWithAppFromOtherToken = new ArrayList(this.deviceListWithAppDistributedInMDM)).removeAll(this.deviceListWithAppFromSameToken);
            if (this.licenseAssignmentType == 1) {
                this.setUserToDeviceMap();
            }
            this.deleteAssetToErrorDetails();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while initailizing VPPLicenseSyncHandler ", ex);
            throw ex;
        }
    }
    
    private void setUserToDeviceMap() throws DataAccessException {
        final Iterator iterator = this.vpprelDO.getRows("ManagedUserToDevice");
        while (iterator.hasNext()) {
            final Row mdUserToDeviceRow = iterator.next();
            final Long managedUserID = (Long)mdUserToDeviceRow.get("MANAGED_USER_ID");
            final Long deviceID = (Long)mdUserToDeviceRow.get("MANAGED_DEVICE_ID");
            List deviceIDList = ((Hashtable<K, List>)this.userToDeviceMap).get(managedUserID);
            if (deviceIDList == null) {
                deviceIDList = new ArrayList();
            }
            if (!deviceIDList.contains(deviceID)) {
                deviceIDList.add(deviceID);
            }
            ((Hashtable<Long, List>)this.userToDeviceMap).put(managedUserID, deviceIDList);
        }
    }
    
    private List getDeviceListWithAppInMDM() {
        final List deviceList = new ArrayList();
        try {
            final Iterator iterator = this.vpprelDO.getRows("RecentProfileForResource");
            while (iterator.hasNext()) {
                final Row recentProfileRow = iterator.next();
                final Long deviceID = (Long)recentProfileRow.get("RESOURCE_ID");
                if (!deviceList.contains(deviceID)) {
                    deviceList.add(deviceID);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceListWithAppInMDM", e);
        }
        return deviceList;
    }
    
    private List getDeviceListWithAppFromSameToken() {
        final List deviceList = new ArrayList();
        final List deviceListWithNoBsAssociations = new ArrayList();
        try {
            final Iterator iterator = this.vpprelDO.getRows("MDMResourceToDeploymentConfigs");
            while (iterator.hasNext()) {
                final Row recentProfileRow = iterator.next();
                final Long deviceID = (Long)recentProfileRow.get("RESOURCE_ID");
                final Long resBsID = (Long)recentProfileRow.get("BUSINESSSTORE_ID");
                if (resBsID != null) {
                    if (!resBsID.equals(this.businessStoreID) || deviceList.contains(deviceID)) {
                        continue;
                    }
                    deviceList.add(deviceID);
                }
                else {
                    if (resBsID != null || deviceListWithNoBsAssociations.contains(deviceID)) {
                        continue;
                    }
                    deviceListWithNoBsAssociations.add(deviceID);
                }
            }
            if (!deviceListWithNoBsAssociations.isEmpty()) {
                this.logger.log(Level.WARNING, "These resources: {0} has no vpp associations for appGroupID: {1}.", new Object[] { deviceListWithNoBsAssociations, this.appGroupId });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceListWithAppInMDM", e);
        }
        return deviceList;
    }
    
    private void deleteAssetToErrorDetails() {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdStoreAssetErrorDetails");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("MdStoreAssetErrorDetails", "STORE_ASSET_ID"), (Object)this.assetID, 0));
            MDMUtil.getPersistence().delete(deleteQuery);
            this.logger.log(Level.INFO, "The AssetToErrorDetails is deleted for assetID: {0} before syncing licenses ", new Object[] { this.assetID });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteAssetToErrorDetails", e);
        }
    }
    
    public boolean isValidToSyncVppLicenses() {
        boolean result = false;
        try {
            int assignedLicenseInDBSize = 0;
            if (this.licenseAssignmentType == 2) {
                assignedLicenseInDBSize = ((this.vpprelDO.size("MdVppAssetToManagedDeviceRel") == -1) ? 0 : this.vpprelDO.size("MdVppAssetToManagedDeviceRel"));
                result = (this.deviceListWithAppDistributedInMDM.size() != this.assignedLicenseCount || assignedLicenseInDBSize != this.assignedLicenseCount);
            }
            else {
                assignedLicenseInDBSize = ((this.vpprelDO.size("MdVppAssetToVppUserRel") == -1) ? 0 : this.vpprelDO.size("MdVppAssetToVppUserRel"));
                result = (((Hashtable<Object, V>)this.userToDeviceMap).keySet().size() != this.assignedLicenseCount || assignedLicenseInDBSize != this.assignedLicenseCount);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isValidToSyncVppLicenses");
        }
        return result;
    }
    
    public void syncVppLicenses() {
        this.logger.log(Level.INFO, "Syncing VPP License begins for AppGroupId {0} : App store id {1} : BusinessStore ID {2}", new Object[] { this.appGroupId, this.appStoreId, this.businessStoreID });
        try {
            final HashMap licenseResMap = VPPAppLicenseHandler.getInstance().getVPPLicenseDetails(this.appStoreId, this.customerId, this.businessStoreID);
            if (licenseResMap.containsKey("errorNumber")) {
                if (this.isVppTokenSync) {
                    MDBusinessStoreUtil.incrementBusinessStoreAppsFailedCount(this.businessStoreID, new Integer(1));
                }
            }
            else {
                final ArrayList<String> vppDeviceSerialList = new ArrayList<String>(licenseResMap.get("device"));
                final ArrayList<String> vppUserStrList = new ArrayList<String>(licenseResMap.get("user"));
                final List failedSerialNumbers = new ArrayList(vppDeviceSerialList);
                final List failedUserIdStrs = new ArrayList(vppUserStrList);
                if (this.licenseAssignmentType == 2) {
                    Long[] deviceIDs = new Long[0];
                    List vppDeviceIDListWithLicenses = new ArrayList();
                    if (!vppDeviceSerialList.isEmpty()) {
                        List serialListInVppForSameBs = new ArrayList();
                        serialListInVppForSameBs = DBUtil.getColumnValuesAsList(this.vpprelDO.getRows("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)this.deviceListWithAppFromSameToken.toArray(), 8)), "SERIAL_NUMBER");
                        failedSerialNumbers.removeAll(serialListInVppForSameBs);
                        vppDeviceSerialList.removeAll(failedSerialNumbers);
                        vppDeviceIDListWithLicenses = DBUtil.getColumnValuesAsList(this.vpprelDO.getRows("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)vppDeviceSerialList.toArray(), 8)), "RESOURCE_ID");
                        deviceIDs = vppDeviceIDListWithLicenses.toArray(new Long[vppDeviceIDListWithLicenses.size()]);
                        new VPPAssetsHandler().addOrUpdateVppAssetToManagedDeviceRel(deviceIDs, this.assetID, this.customerId, Boolean.TRUE);
                    }
                    else {
                        new VPPAssetsHandler().addOrUpdateVppAssetToManagedDeviceRel(deviceIDs, this.assetID, this.customerId, Boolean.TRUE);
                        this.logger.log(Level.INFO, "No serial numbers from getVPPLicenseSrv response is available for device with appgroupid {0} in MDM", new Object[] { this.appGroupId });
                    }
                    this.deleteMdVppAssetToVppUserRel(this.assetID);
                    this.setInsufficientLicenseDeviceList(vppDeviceIDListWithLicenses);
                }
                else {
                    Long[] vppUserIDs = new Long[0];
                    List vppUserIDListWithLicenses = new ArrayList();
                    if (!vppUserStrList.isEmpty()) {
                        List vppUserStrListInMDM = new ArrayList();
                        if (this.vpprelDO.size("MdVppUser") > 0) {
                            vppUserStrListInMDM = DBUtil.getColumnValuesAsList(this.vpprelDO.getRows("MdVppUser"), "VPP_CLIENT_USER_ID");
                        }
                        failedUserIdStrs.removeAll(vppUserStrListInMDM);
                        vppUserStrList.removeAll(failedUserIdStrs);
                        vppUserIDListWithLicenses = DBUtil.getColumnValuesAsList(this.vpprelDO.getRows("MdVppUser", new Criteria(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"), (Object)vppUserStrList.toArray(), 8)), "VPP_USER_ID");
                        vppUserIDs = vppUserIDListWithLicenses.toArray(new Long[vppUserIDListWithLicenses.size()]);
                        new VPPAssetsHandler().addOrUpdateVppAssetToMdVppUserRel(vppUserIDs, this.assetID, Boolean.TRUE);
                    }
                    else {
                        new VPPAssetsHandler().addOrUpdateVppAssetToMdVppUserRel(vppUserIDs, this.assetID, Boolean.TRUE);
                        this.logger.log(Level.INFO, "No userIDStrs from getVPPLicenseSrv response is available for device with appgroupid {0} in MDM", new Object[] { this.appGroupId });
                    }
                    this.deleteMdVppAssetToManagedDeviceRel(this.assetID);
                    this.setInsufficientLicenseDeviceList(vppUserIDListWithLicenses);
                }
                if (!failedSerialNumbers.isEmpty() || !failedUserIdStrs.isEmpty()) {
                    final JSONObject appData = new JSONObject();
                    appData.put("APP_GROUP_ID", (Object)this.appGroupId);
                    appData.put("ADAM_ID", (Object)this.appStoreId);
                    appData.put("VPP_ASSET_ID", (Object)this.assetID);
                    appData.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
                    if (!failedSerialNumbers.isEmpty()) {
                        this.logger.log(Level.INFO, "The serial numbers {0} provided are not associated with the app with appgroupid {1} in the businessStore {2}. Hence adding in queue to dissociate the licenses for serialNumbers", new Object[] { failedSerialNumbers, this.appGroupId, this.businessStoreID });
                        appData.put("failedSerialNumbers", (Collection)failedSerialNumbers);
                        if (this.licenseAssignmentType == 1) {
                            this.availableLicenseCount += failedSerialNumbers.size();
                        }
                    }
                    if (!failedUserIdStrs.isEmpty()) {
                        this.logger.log(Level.INFO, "The userIdStrs {0} provided are not associated with the app with appgroupid {1} in the businessStore {2}. Hence adding in queue to dissociate the licenses for userIDs", new Object[] { failedUserIdStrs, this.appGroupId, this.businessStoreID });
                        appData.put("failedUserIdStrs", (Collection)failedUserIdStrs);
                        if (this.licenseAssignmentType == 2) {
                            this.availableLicenseCount += failedUserIdStrs.size();
                        }
                    }
                    if (this.isVppTokenSync) {
                        this.addRevokeLicenseTaskToQueue(appData);
                    }
                    else {
                        new VPPAppMgmtHandler().removeImproperLicenseForApps(appData, this.customerId);
                    }
                }
                else {
                    VPPAssetsHandler.getInstance().updateVPPAssetSyncStatus(this.assetID, 0, MDMUtil.getCurrentTimeInMillis());
                }
                this.assignLicenseForDevicesWithVppApps();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in syncVppLicenses {0}", ex);
            if (this.isVppTokenSync) {
                MDBusinessStoreUtil.incrementBusinessStoreAppsFailedCount(this.businessStoreID, new Integer(1));
            }
            VPPAssetsHandler.getInstance().updateVPPAssetSyncStatus(this.assetID, 0, MDMUtil.getCurrentTimeInMillis());
        }
    }
    
    private void setInsufficientLicenseDeviceList(final List associatedResources) throws DataAccessException {
        try {
            if (this.licenseAssignmentType == 2) {
                (this.insufficientLicenseDeviceList = new ArrayList(this.deviceListWithAppFromSameToken)).removeAll(associatedResources);
            }
            else {
                final List deviceListWithLicences = new ArrayList();
                final Join vppUserToDeviceJoin = new Join("ManagedUserToDevice", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
                final Iterator iterator = this.vpprelDO.getRows("ManagedUserToDevice", new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "VPP_USER_ID"), (Object)associatedResources.toArray(), 8), vppUserToDeviceJoin);
                while (iterator.hasNext()) {
                    final Row managedUserToDeviceRow = iterator.next();
                    final Long deviceID = (Long)managedUserToDeviceRow.get("MANAGED_USER_ID");
                    if (!deviceListWithLicences.contains(deviceID)) {
                        deviceListWithLicences.add(deviceID);
                    }
                }
                (this.insufficientLicenseDeviceList = new ArrayList(this.deviceListWithAppFromSameToken)).removeAll(deviceListWithLicences);
                final Iterator insufficientLicenseIter = this.vpprelDO.getRows("ManagedUserToDevice", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)this.insufficientLicenseDeviceList.toArray(), 8));
                while (insufficientLicenseIter.hasNext()) {
                    final Row managedUserToDeviceRow2 = insufficientLicenseIter.next();
                    final Long managedUserID = (Long)managedUserToDeviceRow2.get("MANAGED_USER_ID");
                    final Long deviceID2 = (Long)managedUserToDeviceRow2.get("MANAGED_DEVICE_ID");
                    List deviceList = ((Hashtable<K, List>)this.insufficientUserToDeviceMap).get(managedUserID);
                    if (deviceList == null) {
                        deviceList = new ArrayList();
                    }
                    if (!deviceList.contains(deviceID2)) {
                        deviceList.add(deviceID2);
                    }
                    ((Hashtable<Long, List>)this.insufficientUserToDeviceMap).put(managedUserID, deviceList);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in setInsufficientLicenseDeviceList", (Throwable)e);
        }
    }
    
    private void addRevokeLicenseTaskToQueue(final JSONObject jsonQueueData) throws Exception {
        jsonQueueData.put("MsgType", (Object)"VppAppImproperLicenseRemoval");
        final String strData = jsonQueueData.toString();
        final String separator = "\t";
        final String qFileName = "vpp-license-handling-" + System.currentTimeMillis();
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = System.currentTimeMillis();
        queueData.queueData = strData;
        queueData.customerID = this.customerId;
        this.logger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}", new Object[] { "vpp-license-handling", separator, separator, queueData.fileName, separator, String.valueOf(System.currentTimeMillis()) });
        final DCQueue queue = DCQueueHandler.getQueue("vpp-license-handling");
        queue.addToQueue(queueData);
    }
    
    private void setBusinessStoreIDForDeploymentConfigs() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Criteria customerIDCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerId, 0);
            final Criteria businessIDNullCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)null, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)this.appGroupId, 0);
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "*"));
            selectQuery.setCriteria(customerIDCriteria.and(businessIDNullCriteria.and(appCriteria)));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                Long bStoreID = null;
                if (dataObject.size("ManagedBusinessStore") == 1) {
                    final Row businessStoreRow = dataObject.getFirstRow("MdBusinessStoreToAssetRel");
                    bStoreID = (Long)businessStoreRow.get("BUSINESSSTORE_ID");
                    this.logger.log(Level.INFO, "Some resources doesn't have vpp app license associated with it for the appGroupID: {0}", new Object[] { this.appGroupId });
                }
                else {
                    bStoreID = new VPPAssetsHandler().getBsIDWithHighestAvailableLicenseForApp(this.appGroupId);
                    this.logger.log(Level.INFO, "The app with appGroupID {0} is available in multiple businessStore. Hence, assigning businessStore with highest available license for resources in deployment configs", this.appGroupId);
                }
                final Iterator iter = dataObject.getRows("MDMResourceToDeploymentConfigs");
                final List resList = new ArrayList();
                while (iter.hasNext()) {
                    final Row depConfigRow = iter.next();
                    final Long resID = (Long)depConfigRow.get("RESOURCE_ID");
                    depConfigRow.set("BUSINESSSTORE_ID", (Object)bStoreID);
                    if (!resList.contains(resID)) {
                        resList.add(resID);
                    }
                    dataObject.updateRow(depConfigRow);
                }
                this.logger.log(Level.INFO, "The resources {0} does not have licenses associated from a specific token. Since the app {1} is available in only one businessStore {2}, the deployment config is set with the same businessStoreID", new Object[] { resList, this.appGroupId, bStoreID });
                MDMUtil.getPersistence().update(dataObject);
            }
            else {
                this.logger.log(Level.INFO, "There is no devices associated with the app: {0} ", this.appGroupId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in setBusinessStoreIDForDeploymentConfigs", e);
        }
    }
    
    private void assignLicenseForDevicesWithVppApps() {
        try {
            if (!this.insufficientLicenseDeviceList.isEmpty()) {
                int requiredLicenseCount = 0;
                int insufficientLicenseCount = 0;
                final List insufficientDeviceLicenseList = new ArrayList();
                List resListToReAssociateApps = new ArrayList();
                if (this.licenseAssignmentType == 2) {
                    requiredLicenseCount = this.insufficientLicenseDeviceList.size();
                    resListToReAssociateApps = ((this.insufficientLicenseDeviceList.size() <= this.availableLicenseCount) ? this.insufficientLicenseDeviceList : this.insufficientLicenseDeviceList.subList(0, this.availableLicenseCount));
                }
                else {
                    final List userList = new ArrayList();
                    userList.addAll(this.insufficientUserToDeviceMap.keySet());
                    requiredLicenseCount = ((Hashtable<Object, V>)this.insufficientUserToDeviceMap).keySet().size();
                    final List userToAssociateApps = (userList.size() <= this.availableLicenseCount) ? userList : userList.subList(0, this.availableLicenseCount);
                    for (int i = 0; i < userToAssociateApps.size(); ++i) {
                        final List deviceIDList = ((Hashtable<K, List>)this.insufficientUserToDeviceMap).get(userToAssociateApps.get(i));
                        resListToReAssociateApps.addAll(deviceIDList);
                    }
                }
                if (requiredLicenseCount > this.availableLicenseCount) {
                    insufficientLicenseCount = requiredLicenseCount - this.availableLicenseCount;
                }
                if (!resListToReAssociateApps.isEmpty()) {
                    final JSONObject appData = new JSONObject();
                    appData.put("APP_GROUP_ID", (Object)this.appGroupId);
                    appData.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
                    appData.put("resListToReAssociateApps", (Collection)resListToReAssociateApps);
                    appData.put("userID", (Object)this.userID);
                    if (this.isVppTokenSync) {
                        this.addInsufficientLicenseAssociationToQueue(appData, this.customerId);
                    }
                    else {
                        new VPPAppMgmtHandler().assignLicensesForFailedDevices(appData, this.customerId);
                    }
                }
                this.insufficientLicenseDeviceList.removeAll(resListToReAssociateApps);
                if (!this.insufficientLicenseDeviceList.isEmpty()) {
                    this.logger.log(Level.INFO, "The devices {0} doesn't have enough licenses for the app with appgroupid {1} in the businessStore {2}", new Object[] { this.insufficientLicenseDeviceList, this.appGroupId, this.businessStoreID });
                    new VPPAssetsHandler().addOrUpdateAssetErrorDetails(this.businessStoreID, this.appStoreId, null, null, 888804);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in assignLicenseForDevicesWithVppApps", e);
        }
    }
    
    private void addInsufficientLicenseAssociationToQueue(final JSONObject jsonQueueData, final Long customerID) throws Exception {
        jsonQueueData.put("MsgType", (Object)"VppAppInsufficientLicenseHandling");
        final String strData = jsonQueueData.toString();
        final String separator = "\t";
        final String qFileName = "vpp-license-handling-" + System.currentTimeMillis();
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = System.currentTimeMillis();
        queueData.queueData = strData;
        queueData.customerID = this.customerId;
        this.logger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}", new Object[] { "vpp-license-handling", separator, separator, queueData.fileName, separator, String.valueOf(System.currentTimeMillis()) });
        final DCQueue queue = DCQueueHandler.getQueue("vpp-license-handling");
        queue.addToQueue(queueData);
    }
    
    private void deleteMdVppAssetToVppUserRel(final Long assetID) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdVppAssetToVppUserRel");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("MdVppAssetToVppUserRel", "VPP_ASSET_ID"), (Object)assetID, 0));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteMdVppAssetToVppUserRel", e);
        }
    }
    
    private void deleteMdVppAssetToManagedDeviceRel(final Long assetID) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdVppAssetToManagedDeviceRel");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "VPP_ASSET_ID"), (Object)assetID, 0));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteMdVppAssetToVppUserRel", e);
        }
    }
}
