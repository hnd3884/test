package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.QueryConstructionException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPAPIRequestGenerator;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import java.util.HashMap;
import java.util.logging.Logger;

public class VPPAssetsHandler
{
    public Logger logger;
    private String className;
    private static VPPAssetsHandler vppAssetsHandler;
    
    public VPPAssetsHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
        this.className = VPPManagedUserHandler.class.getName();
    }
    
    public static VPPAssetsHandler getInstance() {
        if (VPPAssetsHandler.vppAssetsHandler == null) {
            VPPAssetsHandler.vppAssetsHandler = new VPPAssetsHandler();
        }
        return VPPAssetsHandler.vppAssetsHandler;
    }
    
    public HashMap getVppAssetDetailsMap(final Long businessStoreID, final Long customerID) {
        HashMap appDetails = new HashMap();
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getVPPAssetsCommand();
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for getVPPAssetsSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "getVPPAssetsSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for getVPPAssetsSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            appDetails = (HashMap)VPPResponseProcessor.getInstance().processResponse(responseJSON, "getVPPAssetsSrvUrl");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in addVPPAssetsDetails ", e);
        }
        return appDetails;
    }
    
    private DataObject getVppAssetDOForAdamIDs(final Long tokenID, final Criteria adamIDsCriteria) {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final SelectQuery assetQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAsset"));
            assetQuery.addJoin(new Join("MdVppAsset", "MdBusinessStoreToAssetRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            Criteria criteria = new Criteria(new Column("MdVppAsset", "TOKEN_ID"), (Object)tokenID, 0);
            criteria = criteria.and(adamIDsCriteria);
            assetQuery.setCriteria(criteria);
            assetQuery.addSelectColumn(Column.getColumn("MdVppAsset", "*"));
            dataObject = MDMUtil.getPersistence().get(assetQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppAssetDOForAdamIDs", e);
        }
        return dataObject;
    }
    
    public void addOrUpdateAssetErrorDetails(final Long businessStoreID, final String adamID, final String remarks, final String remarksParams, final Integer errorCode) {
        final Long assetID = this.getAssetIDForAdamID(businessStoreID, adamID);
        MDBusinessStoreAssetUtil.addOrUpdateMdStoreAssetErrorDetails(assetID, errorCode, remarks, remarksParams);
    }
    
    public Long getAssetIDForAdamID(final Long businessStoreID, final String adamID) {
        Long assetID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAsset"));
            selectQuery.addJoin(new Join("MdVppAsset", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            final Criteria bsCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria adamIDCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamID, 0);
            selectQuery.setCriteria(bsCriteria.and(adamIDCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row assetRow = dataObject.getFirstRow("MdVppAsset");
                assetID = (Long)assetRow.get("VPP_ASSET_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAssetIDForAdamID", e);
        }
        return assetID;
    }
    
    private ArrayList<String> updateVPPAssetRows(final DataObject dataObject, final HashMap vppAssetMap, final Integer appAssignmentType) throws DataAccessException {
        final Iterator iter = dataObject.getRows("MdVppAsset");
        final ArrayList<String> updatedAdamIDList = new ArrayList<String>();
        while (iter.hasNext()) {
            final Row vppAssetRow = iter.next();
            final String adamID = vppAssetRow.get("ADAM_ID").toString();
            final Properties prop = vppAssetMap.get(adamID);
            vppAssetRow.set("ASSET_TYPE", ((Hashtable<K, Object>)prop).get("ASSET_TYPE"));
            vppAssetRow.set("TOTAL_LICENSE", ((Hashtable<K, Object>)prop).get("TOTAL_LICENSE"));
            vppAssetRow.set("AVAILABLE_LICENSE_COUNT", ((Hashtable<K, Object>)prop).get("AVAILABLE_LICENSE_COUNT"));
            vppAssetRow.set("ASSIGNED_LICENSE_COUNT", ((Hashtable<K, Object>)prop).get("ASSIGNED_LICENSE_COUNT"));
            vppAssetRow.set("RETIRED_COUNT", ((Hashtable<K, Object>)prop).get("RETIRED_COUNT"));
            vppAssetRow.set("PRICING_PARAM", ((Hashtable<K, Object>)prop).get("PRICING_PARAM"));
            vppAssetRow.set("IS_DEVICE_ASSIGNABLE", ((Hashtable<K, Object>)prop).get("IS_DEVICE_ASSIGNABLE"));
            vppAssetRow.set("LICENSE_TYPE", (Object)appAssignmentType);
            vppAssetRow.set("IS_IRREVOCABLE", ((Hashtable<K, Object>)prop).get("IS_IRREVOCABLE"));
            dataObject.updateRow(vppAssetRow);
            updatedAdamIDList.add(adamID);
        }
        return updatedAdamIDList;
    }
    
    private void addVPPAssetRows(final DataObject dataObject, final List toBeAddedAdamIDList, final HashMap vppAssetMap, final Integer appAssignmentType, final Long businessStoreID, final Long tokenID) throws DataAccessException {
        for (int i = 0; i < toBeAddedAdamIDList.size(); ++i) {
            final String adamID = toBeAddedAdamIDList.get(i);
            final Row businessStoreAssetRow = new Row("MdBusinessStoreToAssetRel");
            businessStoreAssetRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            businessStoreAssetRow.set("ASSET_IDENTIFIER", (Object)adamID);
            dataObject.addRow(businessStoreAssetRow);
            final Row vppAssetRow = new Row("MdVppAsset");
            final Properties prop = vppAssetMap.get(adamID);
            vppAssetRow.set("VPP_ASSET_ID", businessStoreAssetRow.get("STORE_ASSET_ID"));
            vppAssetRow.set("ADAM_ID", (Object)adamID);
            vppAssetRow.set("ASSET_TYPE", ((Hashtable<K, Object>)prop).get("ASSET_TYPE"));
            vppAssetRow.set("TOTAL_LICENSE", ((Hashtable<K, Object>)prop).get("TOTAL_LICENSE"));
            vppAssetRow.set("AVAILABLE_LICENSE_COUNT", ((Hashtable<K, Object>)prop).get("AVAILABLE_LICENSE_COUNT"));
            vppAssetRow.set("ASSIGNED_LICENSE_COUNT", ((Hashtable<K, Object>)prop).get("ASSIGNED_LICENSE_COUNT"));
            vppAssetRow.set("RETIRED_COUNT", ((Hashtable<K, Object>)prop).get("RETIRED_COUNT"));
            vppAssetRow.set("PRICING_PARAM", ((Hashtable<K, Object>)prop).get("PRICING_PARAM"));
            vppAssetRow.set("IS_DEVICE_ASSIGNABLE", ((Hashtable<K, Object>)prop).get("IS_DEVICE_ASSIGNABLE"));
            vppAssetRow.set("LICENSE_TYPE", (Object)appAssignmentType);
            vppAssetRow.set("TOKEN_ID", (Object)tokenID);
            vppAssetRow.set("IS_IRREVOCABLE", ((Hashtable<K, Object>)prop).get("IS_IRREVOCABLE"));
            dataObject.addRow(vppAssetRow);
        }
    }
    
    private void setAssetIDinAssetMap(final DataObject dataObject, final HashMap vppAssetMap) throws DataAccessException {
        if (!dataObject.isEmpty()) {
            final Iterator iter = dataObject.getRows("MdVppAsset");
            while (iter.hasNext()) {
                final Row assetRow = iter.next();
                final String adamID = assetRow.get("ADAM_ID").toString();
                final Properties prop = vppAssetMap.get(adamID);
                final Long assetID = (Long)assetRow.get("VPP_ASSET_ID");
                ((Hashtable<String, Long>)prop).put("VPP_ASSET_ID", assetID);
                vppAssetMap.put(adamID, prop);
            }
        }
    }
    
    private DataObject getVppAssetToDeviceDO(final Long assetID, final List deviceIDs, final Long customerID) {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAssetToManagedDeviceRel"));
            selectQuery.addJoin(new Join("MdVppAssetToManagedDeviceRel", "Resource", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria deviceIDCriteria = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)deviceIDs.toArray(), 8);
            final Criteria assetCrit = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "VPP_ASSET_ID"), (Object)assetID, 0);
            selectQuery.setCriteria(customerCrit.and(deviceIDCriteria).and(assetCrit));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAssetToManagedDeviceRel", "*"));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVppAssetToDeviceDO", ex);
        }
        return dataObject;
    }
    
    private ArrayList<Long> getAlreadyAssignedDevices(final DataObject dataObject) {
        final ArrayList<Long> updatedDeviceIDs = new ArrayList<Long>();
        try {
            final Iterator iter = dataObject.getRows("MdVppAssetToManagedDeviceRel");
            while (iter.hasNext()) {
                final Row deviceRow = iter.next();
                final Long deviceID = (Long)deviceRow.get("MANAGED_DEVICE_ID");
                updatedDeviceIDs.add(deviceID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateVPPAssetToDeviceRow");
        }
        return updatedDeviceIDs;
    }
    
    private void addVPPAssetToDeviceRows(final DataObject dataObject, final List deviceIds, final Long assetID) {
        try {
            for (int i = 0; i < deviceIds.size(); ++i) {
                final Row relRow = new Row("MdVppAssetToManagedDeviceRel");
                relRow.set("VPP_ASSET_ID", (Object)assetID);
                relRow.set("MANAGED_DEVICE_ID", deviceIds.get(i));
                dataObject.addRow(relRow);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addVPPAssetToDeviceRows", e);
        }
    }
    
    private ArrayList<Long> checkDeviceSerialNumbersInMDM(final ArrayList<String> serialNumberList, final Long businessStoreID, final Long customerID) {
        final ArrayList<Long> tempList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            selectQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join());
            final Criteria customerCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria serialNumberCrit = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)serialNumberList.toArray(), 8);
            final Criteria managedStatusCrit = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            selectQuery.setCriteria(customerCrit.and(serialNumberCrit).and(managedStatusCrit));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MdDeviceInfo");
            while (iterator.hasNext()) {
                final Row deviceRow = iterator.next();
                final Long deviceID = (Long)deviceRow.get("RESOURCE_ID");
                final String serialNumber = (String)deviceRow.get("SERIAL_NUMBER");
                tempList.add(deviceID);
                serialNumberList.remove(serialNumber);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkDeviceSerialNumbersInMDM", e);
        }
        return tempList;
    }
    
    public int addOrUpdateVppAssetToManagedDeviceRel(final Long[] deviceIDs, final Long assetID, final Long customerID, final Boolean isAppLicenseSync) {
        this.logger.log(Level.INFO, "Adding/Updating DB with serialNumbers = {0} for VPP App with AssetID = {1} in BusinessStore {1}", new Object[] { deviceIDs, assetID });
        int newlyAddedDeviceCounts = 0;
        try {
            final List assignedDeviceIDs = new ArrayList();
            final List allDeviceIDs = new ArrayList(Arrays.asList(deviceIDs));
            final List<List> allDeviceSubList = MDMUtil.getInstance().splitListIntoSubLists(allDeviceIDs, 300);
            final Iterator iterator = allDeviceSubList.iterator();
            while (iterator.hasNext()) {
                List updatedDeviceIDs = new ArrayList();
                final List tempDeviceIDs = iterator.next();
                final List toBeAddedDeviceIDs = new ArrayList(tempDeviceIDs);
                final DataObject dataObject = this.getVppAssetToDeviceDO(assetID, toBeAddedDeviceIDs, customerID);
                if (!dataObject.isEmpty()) {
                    updatedDeviceIDs = this.getAlreadyAssignedDevices(dataObject);
                }
                toBeAddedDeviceIDs.removeAll(updatedDeviceIDs);
                if (toBeAddedDeviceIDs != null && !toBeAddedDeviceIDs.isEmpty()) {
                    this.addVPPAssetToDeviceRows(dataObject, toBeAddedDeviceIDs, assetID);
                }
                MDMUtil.getPersistence().update(dataObject);
                if (toBeAddedDeviceIDs.size() > 0) {
                    newlyAddedDeviceCounts += toBeAddedDeviceIDs.size();
                }
                assignedDeviceIDs.addAll(toBeAddedDeviceIDs);
                assignedDeviceIDs.addAll(updatedDeviceIDs);
            }
            if (isAppLicenseSync) {
                this.logger.log(Level.INFO, "Deleting unknown Asset To Device Relations that are not in assignedDeviceIDs: {0}", new Object[] { assignedDeviceIDs });
                Criteria criteria = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "VPP_ASSET_ID"), (Object)assetID, 0);
                if (assignedDeviceIDs != null && !assignedDeviceIDs.isEmpty()) {
                    criteria = criteria.and(new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)assignedDeviceIDs.toArray(), 9));
                }
                this.deleteAssetToManagedDeviceRel(criteria);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateVppAssetToManagedDeviceRel", ex);
        }
        return newlyAddedDeviceCounts;
    }
    
    private DataObject getVppAssetToVppUserDO(final Long assetID, final List vppUserIDs) {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAssetToVppUserRel"));
            selectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            final Criteria userIdStrCrit = new Criteria(Column.getColumn("MdVppUser", "VPP_USER_ID"), (Object)vppUserIDs.toArray(), 8);
            final Criteria assetCrit = new Criteria(Column.getColumn("MdVppAssetToVppUserRel", "VPP_ASSET_ID"), (Object)assetID, 0);
            selectQuery.setCriteria(userIdStrCrit.and(assetCrit));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppAssetToVppUserDO", e);
        }
        return dataObject;
    }
    
    private ArrayList<Long> checkUserIdsInVppToken(final ArrayList<String> userIDList, final Long businessStoreID) {
        final ArrayList<Long> tempList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppUser"));
            selectQuery.addJoin(new Join("MdVppUser", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria userIDCriteria = new Criteria(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"), (Object)userIDList.toArray(), 8);
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"));
            selectQuery.setCriteria(businessStoreCriteria.and(userIDCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MdVppUser");
            while (iterator.hasNext()) {
                final Row userRow = iterator.next();
                final String userIdstr = (String)userRow.get("VPP_CLIENT_USER_ID");
                final Long vppUserID = (Long)userRow.get("VPP_USER_ID");
                userIDList.remove(userIdstr);
                tempList.add(vppUserID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkUserIdsInVppToken", e);
        }
        return tempList;
    }
    
    private List getAlreadyAssignedUserIds(final DataObject dataObject) {
        final List updatedUserIDList = new ArrayList();
        try {
            final Iterator iter = dataObject.getRows("MdVppUser");
            while (iter.hasNext()) {
                final Row userRow = iter.next();
                final Long vppUserID = (Long)userRow.get("VPP_USER_ID");
                updatedUserIDList.add(vppUserID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateVPPAssetToDeviceRow");
        }
        return updatedUserIDList;
    }
    
    private void setAssignedVppUserIDsInList(final DataObject dataObject, final ArrayList<Long> vppUserIDList) {
        try {
            final Iterator iterator = dataObject.getRows("MdVppUser");
            while (iterator.hasNext()) {
                final Row relRow = iterator.next();
                final Long deviceID = (Long)relRow.get("VPP_USER_ID");
                vppUserIDList.add(deviceID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in setAssignedVppUserIDsInList", e);
        }
    }
    
    private void addAssetToVppUserRel(final DataObject dataObject, final List vppUserIDs, final Long assetID) {
        try {
            for (int i = 0; i < vppUserIDs.size(); ++i) {
                final Row assetRelRow = new Row("MdVppAssetToVppUserRel");
                assetRelRow.set("VPP_ASSET_ID", (Object)assetID);
                assetRelRow.set("VPP_USER_ID", vppUserIDs.get(i));
                dataObject.addRow(assetRelRow);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addAssetToVppUserRel", e);
        }
    }
    
    public void deleteAssetToVppUserRel(final Criteria criteria) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdVppAssetToVppUserRel");
            deleteQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            deleteQuery.setCriteria(criteria);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteAssetToDeviceRel", e);
        }
    }
    
    private void deleteAssetNotInListForBS(final Long businessStoreID, final List assetIDList) {
        try {
            if (businessStoreID != null) {
                this.logger.log(Level.INFO, "Deleting assetIDs which are not in the list: {0}", new Object[] { assetIDList });
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdBusinessStoreToAssetRel");
                deleteQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
                final Criteria assetsNotInCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)assetIDList.toArray(), 9);
                final Criteria businessStoreCri = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                deleteQuery.setCriteria(assetsNotInCriteria.and(businessStoreCri));
                MDMUtil.getPersistence().delete(deleteQuery);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteAssetNotInListForBS", e);
        }
    }
    
    public int addOrUpdateVppAssetToMdVppUserRel(final Long[] vppUserIDs, final Long assetID, final Boolean isAppLicenseSync) {
        int newLyAddedUserCount = 0;
        try {
            final List assignedVppUserIds = new ArrayList();
            final List allVppUserIDList = new ArrayList(Arrays.asList(vppUserIDs));
            final List<List> vppUserIDSubList = MDMUtil.getInstance().splitListIntoSubLists(allVppUserIDList, 300);
            for (final List tempVppUserIds : vppUserIDSubList) {
                final List toBeAddedVppUserIds = new ArrayList(tempVppUserIds);
                List updatedVppUserIDs = new ArrayList();
                final DataObject dataObject = this.getVppAssetToVppUserDO(assetID, toBeAddedVppUserIds);
                if (!dataObject.isEmpty()) {
                    updatedVppUserIDs = this.getAlreadyAssignedUserIds(dataObject);
                }
                toBeAddedVppUserIds.removeAll(updatedVppUserIDs);
                if (toBeAddedVppUserIds != null && !toBeAddedVppUserIds.isEmpty()) {
                    this.addAssetToVppUserRel(dataObject, toBeAddedVppUserIds, assetID);
                }
                assignedVppUserIds.addAll(toBeAddedVppUserIds);
                if (toBeAddedVppUserIds.size() > 0) {
                    newLyAddedUserCount += toBeAddedVppUserIds.size();
                }
                assignedVppUserIds.addAll(updatedVppUserIDs);
                MDMUtil.getPersistence().update(dataObject);
            }
            if (isAppLicenseSync) {
                final Criteria assetCriteria = new Criteria(Column.getColumn("MdVppAssetToVppUserRel", "VPP_ASSET_ID"), (Object)assetID, 0);
                final Criteria vppUsersNotInCriteria = new Criteria(Column.getColumn("MdVppAssetToVppUserRel", "VPP_USER_ID"), (Object)assignedVppUserIds.toArray(), 9);
                this.logger.log(Level.INFO, "Deleting VppUserIds which are not in vppUserIDList: {0}", new Object[] { assignedVppUserIds });
                this.deleteAssetToVppUserRel(assetCriteria.and(vppUsersNotInCriteria));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateVppAssetToMdVppUserRel", ex);
        }
        return newLyAddedUserCount;
    }
    
    public void addOrUpdateVppAssets(final HashMap vppAssetMap, final String[] adamIDArray, final Long businessStoreID, final Integer appAssignmentType) {
        Criteria adamIDsCriteria = null;
        try {
            final Row tokenRow = DBUtil.getRowFromDB("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID", (Object)businessStoreID);
            final Long tokenID = (Long)tokenRow.get("TOKEN_ID");
            final List adamIDList = new ArrayList(Arrays.asList(adamIDArray));
            final List<List> adamIDSubList = MDMUtil.getInstance().splitListIntoSubLists(adamIDList, 300);
            final Iterator adamIDSubListIterator = adamIDSubList.iterator();
            final List totalAdamIDList = new ArrayList();
            while (adamIDSubListIterator.hasNext()) {
                List<String> updatedAdamIDList = new ArrayList<String>();
                final List tempAdamIDList = adamIDSubListIterator.next();
                final List toBeAddedAdamIDList = new ArrayList(tempAdamIDList);
                adamIDsCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)toBeAddedAdamIDList.toArray(), 8);
                final DataObject dataObject = this.getVppAssetDOForAdamIDs(tokenID, adamIDsCriteria);
                if (!dataObject.isEmpty()) {
                    updatedAdamIDList = this.updateVPPAssetRows(dataObject, vppAssetMap, appAssignmentType);
                }
                toBeAddedAdamIDList.removeAll(updatedAdamIDList);
                if (toBeAddedAdamIDList != null && !toBeAddedAdamIDList.isEmpty()) {
                    this.addVPPAssetRows(dataObject, toBeAddedAdamIDList, vppAssetMap, appAssignmentType, businessStoreID, tokenID);
                }
                totalAdamIDList.addAll(toBeAddedAdamIDList);
                totalAdamIDList.addAll(updatedAdamIDList);
                MDMUtil.getPersistence().update(dataObject);
                this.setAssetIDinAssetMap(dataObject, vppAssetMap);
            }
            this.deleteAssetNotInListForBS(businessStoreID, totalAdamIDList);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateVppAssets", ex);
        }
    }
    
    public int getAssetAssignmentType(final Long businessStoreID, final String appStoreID) {
        int assetAssignmentType = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAsset"));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            final Criteria businessCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)appStoreID, 0);
            selectQuery.setCriteria(businessCriteria.and(appCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LICENSE_TYPE"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row assetRow = dataObject.getFirstRow("MdVppAsset");
                assetAssignmentType = (int)assetRow.get("LICENSE_TYPE");
            }
        }
        catch (final Exception E) {
            this.logger.log(Level.SEVERE, "Exception in getAssetAssignmentType");
        }
        return assetAssignmentType;
    }
    
    private DataObject getVppAssetRelForAssetIDs(final Criteria assetsCriteria) {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final SelectQuery assetRelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            assetRelQuery.setCriteria(assetsCriteria);
            assetRelQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "*"));
            dataObject = MDMUtil.getPersistence().get(assetRelQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppAssetRelForAssetIDs", e);
        }
        return dataObject;
    }
    
    private List updateAssetToAppGroupRelRow(final DataObject dataObject, final HashMap syncedAppDetails) throws DataAccessException {
        final Iterator iter = dataObject.getRows("MdStoreAssetToAppGroupRel");
        final List updatedAssetList = new ArrayList();
        while (iter.hasNext()) {
            final Row assetToAppGrpRow = iter.next();
            final Long assetID = (Long)assetToAppGrpRow.get("STORE_ASSET_ID");
            final Properties prop = syncedAppDetails.get(assetID);
            assetToAppGrpRow.set("APP_GROUP_ID", ((Hashtable<K, Object>)prop).get("APP_GROUP_ID"));
            dataObject.updateRow(assetToAppGrpRow);
            updatedAssetList.add(assetID);
        }
        return updatedAssetList;
    }
    
    private void addAssetToAppGroupRelRow(final DataObject dataObject, final List toBeUpdatedList, final HashMap syncedAppDetails) throws DataAccessException {
        for (int i = 0; i < toBeUpdatedList.size(); ++i) {
            final Long assetID = toBeUpdatedList.get(i);
            final Row assetToAppGroupRow = new Row("MdStoreAssetToAppGroupRel");
            final Properties prop = syncedAppDetails.get(assetID);
            assetToAppGroupRow.set("APP_GROUP_ID", ((Hashtable<K, Object>)prop).get("APP_GROUP_ID"));
            assetToAppGroupRow.set("STORE_ASSET_ID", (Object)assetID);
            dataObject.addRow(assetToAppGroupRow);
        }
    }
    
    public void addOrUpdateAssetToAppGroupRel(final HashMap syncedAppDetails, final Long[] assetIDs) {
        Criteria assetIDsCriteria = null;
        try {
            final int size = assetIDs.length;
            final List allAssetList = new ArrayList(Arrays.asList(assetIDs));
            final List<List> subLists = MDMUtil.getInstance().splitListIntoSubLists(allAssetList, 300);
            final Iterator subListsIter = subLists.iterator();
            final List totalAssetIDs = new ArrayList();
            while (subListsIter.hasNext()) {
                List updatedAssetIDList = new ArrayList();
                final List tempAssetIDs = subListsIter.next();
                final List toBeAddedAssetIDList = new ArrayList(tempAssetIDs);
                assetIDsCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "STORE_ASSET_ID"), (Object)toBeAddedAssetIDList.toArray(), 8);
                final DataObject dataObject = this.getVppAssetRelForAssetIDs(assetIDsCriteria);
                if (!dataObject.isEmpty()) {
                    updatedAssetIDList = this.updateAssetToAppGroupRelRow(dataObject, syncedAppDetails);
                }
                toBeAddedAssetIDList.removeAll(updatedAssetIDList);
                if (!toBeAddedAssetIDList.isEmpty() && toBeAddedAssetIDList != null) {
                    this.addAssetToAppGroupRelRow(dataObject, toBeAddedAssetIDList, syncedAppDetails);
                }
                totalAssetIDs.addAll(toBeAddedAssetIDList);
                totalAssetIDs.addAll(updatedAssetIDList);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateAssetToAppGroupRel", ex);
        }
    }
    
    public void deleteAssetToManagedDeviceRel(final Criteria criteria) {
        try {
            if (criteria != null) {
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdVppAssetToManagedDeviceRel");
                deleteQuery.addJoin(new Join("MdVppAssetToManagedDeviceRel", "MdDeviceInfo", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                deleteQuery.setCriteria(criteria);
                MDMUtil.getPersistence().delete(deleteQuery);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteAssetToManagedDeviceRel");
        }
    }
    
    public void changeLicenseAvailableCountForAssets(final Long assetID, final int count, final String operation) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAsset"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"), (Object)assetID, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "ASSIGNED_LICENSE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOTAL_LICENSE"));
            int availableLicenseCount = 0;
            int assignedLicenseCount = 0;
            int totalLicensesCount = 0;
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row assetRow = dataObject.getFirstRow("MdVppAsset");
                availableLicenseCount = (int)assetRow.get("AVAILABLE_LICENSE_COUNT");
                assignedLicenseCount = (int)assetRow.get("ASSIGNED_LICENSE_COUNT");
                totalLicensesCount = (int)assetRow.get("TOTAL_LICENSE");
                if (operation.equals("INCREMENT")) {
                    availableLicenseCount += count;
                    assignedLicenseCount -= count;
                }
                else if (operation.equals("DECREMENT")) {
                    availableLicenseCount -= count;
                    assignedLicenseCount += count;
                }
                if (availableLicenseCount < 0 || totalLicensesCount < assignedLicenseCount) {
                    this.logger.log(Level.SEVERE, "Incorrect Asset Data: Asset update info for for assetID: {0} has availableLicenses>0 or assignedLicenses>totalLicense which is incorrect. Hence ignoring update. Available License: {1}, AssignedLicense: {2}, TotalLicense: {3}", new Object[] { assetID, availableLicenseCount, assignedLicenseCount, totalLicensesCount });
                }
                else {
                    assetRow.set("AVAILABLE_LICENSE_COUNT", (Object)availableLicenseCount);
                    assetRow.set("ASSIGNED_LICENSE_COUNT", (Object)assignedLicenseCount);
                    dataObject.updateRow(assetRow);
                    this.logger.log(Level.INFO, "Updated Asset Row: {0}", new Object[] { assetRow });
                }
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in changeLicenseAvailableCountForAssets", e);
        }
    }
    
    public SelectQuery getVppAssetsQuery() {
        final SelectQuery selectQuery = VPPTokenDataHandler.getInstance().getVppTokenDetailsQuery();
        selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdVppAsset", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        return selectQuery;
    }
    
    public void updateAppStatusForBusinessStore(final Long businessStoreID, final String adamID, final int status, final Long lastSyncTime) {
        try {
            final Long assetID = this.getAssetIDForAdamID(businessStoreID, adamID);
            this.updateVPPAssetSyncStatus(assetID, status, lastSyncTime);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateAppStatusForBusinessStore", e);
        }
    }
    
    public void updateVPPAssetSyncStatus(final Long assetID, final Integer status, final Long lastSyncTime) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAsset"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "APP_SYNC_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LAST_SYNC_TIME"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"), (Object)assetID, 0));
            final DataObject assetDO = MDMUtil.getPersistence().get(selectQuery);
            if (!assetDO.isEmpty()) {
                final Row assetRow = assetDO.getFirstRow("MdVppAsset");
                if (status != null) {
                    assetRow.set("APP_SYNC_STATUS", (Object)status);
                }
                if (lastSyncTime != null) {
                    assetRow.set("LAST_SYNC_TIME", (Object)lastSyncTime);
                }
                assetDO.updateRow(assetRow);
                MDMUtil.getPersistence().update(assetDO);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in setVPPAssetSyncStatus", e);
        }
    }
    
    public int getVppAppSyncStatus(final Long assetID) {
        int status = 0;
        try {
            final Row assetRow = DBUtil.getRowFromDB("MdVppAsset", "VPP_ASSET_ID", (Object)assetID);
            if (assetRow != null) {
                status = (int)assetRow.get("APP_SYNC_STATUS");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppAppSyncStatus");
        }
        return status;
    }
    
    public int getVppAppSyncStatusFromAppGroupID(final Long businessStoreID, final Long appGroupID) {
        int status = 0;
        try {
            final SelectQuery selectQuery = this.getVppAssetsQuery();
            selectQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final Criteria businessCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(businessCriteria.and(appGroupCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "APP_SYNC_STATUS"));
            final DataObject assetDo = MDMUtil.getPersistence().get(selectQuery);
            if (!assetDo.isEmpty()) {
                final Row assetRow = assetDo.getFirstRow("MdVppAsset");
                if (assetRow != null) {
                    status = (int)assetRow.get("APP_SYNC_STATUS");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppAppSyncStatus");
        }
        return status;
    }
    
    public DataObject getVPPAssetDo(final String adamID, final Long customerId) throws DataAccessException, QueryConstructionException {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = getInstance().getVppAssetsQuery();
            final Criteria appCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamID, 0);
            final Criteria businessCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(appCriteria.and(businessCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "*"));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVPPAssetDo", e);
            throw e;
        }
        return dataObject;
    }
    
    public Long getBsIDWithHighestAvailableLicenseForApp(final Long appGroupID) {
        Long bsStoreID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            final Column vppAvailableLicenseColumn = Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT");
            final SortColumn sortColumn = new SortColumn(vppAvailableLicenseColumn, false);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0));
            selectQuery.setRange(new Range(0, 1));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row bsToAssetRow = dataObject.getFirstRow("MdBusinessStoreToAssetRel");
                bsStoreID = (Long)bsToAssetRow.get("BUSINESSSTORE_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getBsIDWithHighestAvailableLicenseForApp");
        }
        return bsStoreID;
    }
    
    static {
        VPPAssetsHandler.vppAssetsHandler = null;
    }
}
