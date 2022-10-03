package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import java.util.Collection;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPAPIRequestGenerator;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class VPPAppLicenseHandler
{
    public Logger logger;
    private static VPPAppLicenseHandler vppLicenseHandler;
    
    public VPPAppLicenseHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPAppLicenseHandler getInstance() {
        if (VPPAppLicenseHandler.vppLicenseHandler == null) {
            VPPAppLicenseHandler.vppLicenseHandler = new VPPAppLicenseHandler();
        }
        return VPPAppLicenseHandler.vppLicenseHandler;
    }
    
    public HashMap getVPPLicenseDetails(final String storeID, final Long customerId, final Long businessStoreID) throws Exception {
        final ArrayList<String> userList = new ArrayList<String>();
        final ArrayList<String> deviceList = new ArrayList<String>();
        HashMap<String, Object> responseMap = null;
        final HashMap associationsMap = new HashMap();
        try {
            String currentBatchToken = null;
            String sinceModifiedToken = null;
            if (storeID == null) {
                sinceModifiedToken = this.getLicenseSinceModifiedToken(businessStoreID);
            }
            do {
                final Integer appStoreId = Integer.parseInt(storeID);
                final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
                final String command = new VPPAPIRequestGenerator(sToken).getVPPLicenseCommand(appStoreId, customerId, currentBatchToken, sinceModifiedToken, businessStoreID, Boolean.TRUE);
                final String dummyCommand = command.replace(sToken, "*****");
                this.logger.log(Level.INFO, "Request for getVppLicensesSrv for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
                final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "getLicensesSrvUrl", sToken, businessStoreID);
                this.logger.log(Level.INFO, "Response for getVppLicensesSrv for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
                responseMap = (HashMap)VPPResponseProcessor.getInstance().processResponse(responseJSON, "getVPPLicensesSrvForAdamId");
                if (responseMap.containsKey("errorNumber")) {
                    break;
                }
                userList.addAll(responseMap.get("user"));
                deviceList.addAll(responseMap.get("device"));
                currentBatchToken = responseMap.get("batchToken");
            } while (currentBatchToken != null);
            if (responseMap.containsKey("errorNumber")) {
                associationsMap.put("errorNumber", responseMap.get("errorNumber"));
                associationsMap.put("errorMessage", responseMap.get("errorMessage"));
            }
            else {
                if (storeID == null) {
                    final String currentSinceModifiedToken = responseMap.get("sinceModifiedToken");
                    if (currentSinceModifiedToken != null) {
                        this.setLicenseSinceModifiedToken(businessStoreID, currentSinceModifiedToken);
                    }
                }
                associationsMap.put("user", userList);
                associationsMap.put("device", deviceList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVPPLicenseDetails : {0}", e);
            throw e;
        }
        return associationsMap;
    }
    
    public HashMap getAllVPPLicenseDetails(final Long businessStoreID, final Long customerId) {
        final HashMap<String, Object> licenseDetails = new HashMap<String, Object>();
        try {
            String currentBatchToken = null;
            do {
                final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
                final String command = new VPPAPIRequestGenerator(sToken).getVPPLicenseCommand(null, customerId, currentBatchToken, null, businessStoreID, Boolean.TRUE);
                final String dummyCommand = command.replace(sToken, "*****");
                this.logger.log(Level.INFO, "Request for getLicensesSrvUrl(All) for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
                final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "getLicensesSrvUrl", sToken, businessStoreID);
                this.logger.log(Level.INFO, "Response for getLicensesSrvUrl(All) for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
                final HashMap<String, Object> responseMap = (HashMap<String, Object>)VPPResponseProcessor.getInstance().processResponse(responseJSON, "getLicensesSrvUrl");
                if (responseMap.containsKey("errorNumber")) {
                    this.logger.log(Level.SEVERE, "Error occured while getting All licenses for BusinessStoreID: {0} with error number {1} and error message {2}", new Object[] { businessStoreID, responseMap.get("errorNumber"), responseMap.get("errorMessage") });
                    break;
                }
                for (final String adamID : responseMap.keySet()) {
                    if (!adamID.equals("sinceModifiedToken") && !adamID.equals("batchToken")) {
                        HashMap tempLicenseDetails = licenseDetails.getOrDefault(adamID, null);
                        if (tempLicenseDetails == null) {
                            tempLicenseDetails = new HashMap();
                            final List responseUserList = responseMap.getOrDefault("user", null);
                            final List responseDeviceList = responseMap.getOrDefault("device", null);
                            if (responseUserList != null) {
                                tempLicenseDetails.put("user", responseUserList);
                            }
                            if (responseUserList != null) {
                                tempLicenseDetails.put("device", responseDeviceList);
                            }
                            licenseDetails.put(adamID, tempLicenseDetails);
                        }
                        else {
                            final List responseUserList = responseMap.getOrDefault("user", null);
                            final List responseDeviceList = responseMap.getOrDefault("device", null);
                            List finalUserList = tempLicenseDetails.getOrDefault("user", null);
                            if (finalUserList == null) {
                                finalUserList = new ArrayList();
                            }
                            List finalDeviceList = tempLicenseDetails.getOrDefault("user", null);
                            if (finalDeviceList == null) {
                                finalDeviceList = new ArrayList();
                            }
                            if (responseUserList != null) {
                                finalUserList.addAll(responseUserList);
                                tempLicenseDetails.put("user", finalUserList);
                            }
                            if (responseUserList != null) {
                                finalDeviceList.addAll(responseDeviceList);
                                tempLicenseDetails.put("device", finalDeviceList);
                            }
                            licenseDetails.put(adamID, tempLicenseDetails);
                        }
                    }
                }
                currentBatchToken = responseMap.get("batchToken");
            } while (currentBatchToken != null);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVPPLicenseDetails : {0}", e);
        }
        return licenseDetails;
    }
    
    private String getLicenseSinceModifiedToken(final Long businessStoreID) {
        String sinceModifiedToken = null;
        try {
            final SelectQuery sinceModifiedTokenQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPTokenDetails"));
            sinceModifiedTokenQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            sinceModifiedTokenQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            sinceModifiedTokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LICENSE_SINCE_MODIFIED_TOKEN"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sinceModifiedTokenQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getRow("MdVPPTokenDetails");
                if (row != null) {
                    sinceModifiedToken = (String)row.get("LICENSE_SINCE_MODIFIED_TOKEN");
                }
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in getLicenseSinceModifiedToken : ", (Throwable)ex);
        }
        return sinceModifiedToken;
    }
    
    public void setLicenseSinceModifiedToken(final Long businessStoreID, final String sinceModifiedToken) {
        try {
            final SelectQuery sinceModifiedTokenQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPTokenDetails"));
            sinceModifiedTokenQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            sinceModifiedTokenQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            sinceModifiedTokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LICENSE_SINCE_MODIFIED_TOKEN"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sinceModifiedTokenQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getRow("MdVPPTokenDetails");
                if (row != null) {
                    row.set("LICENSE_SINCE_MODIFIED_TOKEN", (Object)sinceModifiedToken);
                    dataObject.updateRow(row);
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in setLicenseSinceModifiedToken : ", (Throwable)ex);
        }
    }
    
    public Properties getVPPAppLicenseDetailsForDevice(final Long appGroupID, final Long deviceID) {
        final Properties appLicenseDetails = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVppAssetToVppUserRel", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 1));
            selectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppUser", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 1));
            selectQuery.addJoin(new Join("MdVppUser", "MdManagedUserToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 1));
            final Criteria appCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupID, 0);
            Criteria vppAssetToMDJoinCri = new Criteria(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"), (Object)Column.getColumn("MdVppAssetToManagedDeviceRel", "VPP_ASSET_ID"), 0);
            vppAssetToMDJoinCri = vppAssetToMDJoinCri.and(new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)deviceID, 0));
            Criteria managedUserToDevJoinCri = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), 0);
            managedUserToDevJoinCri = managedUserToDevJoinCri.and(new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceID, 0));
            selectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "ManagedUserToDevice", managedUserToDevJoinCri, 1));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVppAssetToManagedDeviceRel", vppAssetToMDJoinCri, 1));
            selectQuery.setCriteria(appCriteria);
            selectQuery.addSelectColumn(Column.getColumn("MdVppAssetToVppUserRel", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAssetToManagedDeviceRel", "*"));
            final DataObject assetToAppGroupDo = MDMUtil.getPersistence().get(selectQuery);
            if (!assetToAppGroupDo.isEmpty()) {
                final Iterator assetToUserIter = assetToAppGroupDo.getRows("MdVppAssetToVppUserRel");
                final Iterator assetToDeviceIter = assetToAppGroupDo.getRows("MdVppAssetToManagedDeviceRel");
                if (assetToUserIter.hasNext()) {
                    ((Hashtable<Integer, Boolean>)appLicenseDetails).put(1, Boolean.TRUE);
                }
                else {
                    ((Hashtable<Integer, Boolean>)appLicenseDetails).put(1, Boolean.FALSE);
                }
                if (assetToDeviceIter.hasNext()) {
                    ((Hashtable<Integer, Boolean>)appLicenseDetails).put(2, Boolean.TRUE);
                }
                else {
                    ((Hashtable<Integer, Boolean>)appLicenseDetails).put(2, Boolean.FALSE);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isAppLicenseAvailableForDeviceSerialNo", e);
        }
        return appLicenseDetails;
    }
    
    public void addOrUpdateVPPLicenseToUser(final ArrayList<Long> associatedManagedUserIDList, final int appStoreID, final Long businessStoreID) {
        if (!associatedManagedUserIDList.isEmpty()) {
            try {
                this.logger.log(Level.INFO, "Starting to VPP Asset and Vpp User To Managed user tables for businessStoreID", new Object[] { businessStoreID });
                final VPPAssetsHandler vppAssetsHandler = new VPPAssetsHandler();
                final String adamID = Integer.toString(appStoreID);
                int availableLicenseCount = 0;
                int assignedLicenseCount = 0;
                final DataObject assetDo = this.getTokenAssetDo(adamID, businessStoreID);
                if (!assetDo.isEmpty()) {
                    final Row assetRow = assetDo.getFirstRow("MdVppAsset");
                    final Long assetID = (Long)assetRow.get("VPP_ASSET_ID");
                    final Criteria businessCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                    final Criteria managedUserCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)associatedManagedUserIDList.toArray(), 8);
                    final DataObject vppUserToManagedUserDo = VPPManagedUserHandler.getInstance().getVppUserToManagedUserDO(businessCriteria.and(managedUserCriteria));
                    final List vppUserIDList = DBUtil.getColumnValuesAsList(vppUserToManagedUserDo.getRows("MdVppUser"), "VPP_USER_ID");
                    final Long[] vppUserIDs = vppUserIDList.toArray(new Long[vppUserIDList.size()]);
                    final int newlyAddedUserCount = vppAssetsHandler.addOrUpdateVppAssetToMdVppUserRel(vppUserIDs, assetID, Boolean.FALSE);
                    availableLicenseCount = (int)assetRow.get("AVAILABLE_LICENSE_COUNT") - newlyAddedUserCount;
                    assignedLicenseCount = (int)assetRow.get("ASSIGNED_LICENSE_COUNT") + newlyAddedUserCount;
                    if (availableLicenseCount < 0) {
                        this.logger.log(Level.WARNING, "Incorrect Asset Data: addOrUpdateVPPLicenseToUser: Available license Count for asset: {0} is less than 0. Hence, preventing update", new Object[] { assetID });
                    }
                    else {
                        assetRow.set("AVAILABLE_LICENSE_COUNT", (Object)availableLicenseCount);
                        assetRow.set("ASSIGNED_LICENSE_COUNT", (Object)assignedLicenseCount);
                        assetDo.updateRow(assetRow);
                        this.logger.log(Level.INFO, "Updated Asset Row: {0}", new Object[] { assetRow });
                    }
                }
                MDMUtil.getPersistence().update(assetDo);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in addOrUpdateVPPLicenseToUser..", e);
            }
        }
    }
    
    public DataObject getTokenAssetDo(final String adamID, final Long businessStoreID) {
        DataObject assetDO = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = VPPAssetsHandler.getInstance().getVppAssetsQuery();
            final Criteria appCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamID, 0);
            final Criteria businessCriteria = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            selectQuery.setCriteria(appCriteria.and(businessCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdVppAsset", "*"));
            assetDO = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getTokenAssetDo");
        }
        return assetDO;
    }
    
    public void addOrUpdateVPPLicenseToDevice(final ArrayList associatedDeviceList, final int appStoreID, final Long businessStoreID, final Long customerID) {
        if (!associatedDeviceList.isEmpty()) {
            try {
                this.logger.log(Level.INFO, "Starting to update Vpp Asset and Vpp Asset to Managed Device tables for businessStoreID: {0}", new Object[] { businessStoreID });
                final Long[] deviceIDs = associatedDeviceList.toArray(new Long[associatedDeviceList.size()]);
                final VPPAssetsHandler vppAssetsHandler = new VPPAssetsHandler();
                final String adamID = String.valueOf(appStoreID);
                int availableLicenseCount = 0;
                int assignedLicenseCount = 0;
                final DataObject assetDo = this.getTokenAssetDo(adamID, businessStoreID);
                if (!assetDo.isEmpty()) {
                    final Row assetRow = assetDo.getFirstRow("MdVppAsset");
                    final Long assetID = (Long)assetRow.get("VPP_ASSET_ID");
                    final int newlyAddedDeviceCounts = vppAssetsHandler.addOrUpdateVppAssetToManagedDeviceRel(deviceIDs, assetID, customerID, Boolean.FALSE);
                    availableLicenseCount = (int)assetRow.get("AVAILABLE_LICENSE_COUNT") - newlyAddedDeviceCounts;
                    assignedLicenseCount = (int)assetRow.get("ASSIGNED_LICENSE_COUNT") + newlyAddedDeviceCounts;
                    if (availableLicenseCount < 0) {
                        this.logger.log(Level.WARNING, "Incorrect Asset Data: addOrUpdateVPPLicenseToDevice: Available license Count for asset: {0} is less than 0. Hence, preventing update", new Object[] { assetID });
                    }
                    else {
                        assetRow.set("AVAILABLE_LICENSE_COUNT", (Object)availableLicenseCount);
                        assetRow.set("ASSIGNED_LICENSE_COUNT", (Object)assignedLicenseCount);
                        assetDo.updateRow(assetRow);
                        this.logger.log(Level.INFO, "Updated Asset Row: {0}", new Object[] { assetRow });
                    }
                }
                MDMUtil.getPersistence().update(assetDo);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in addOrUpdateVPPLicenseToUser..", e);
            }
        }
    }
    
    static {
        VPPAppLicenseHandler.vppLicenseHandler = null;
    }
}
