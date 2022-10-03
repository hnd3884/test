package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import java.util.Set;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.me.mdm.server.apps.ios.vpp.VPPAPIRequestGenerator;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import org.json.JSONException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Level;
import java.util.Properties;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class VPPAppDisassociationHandler
{
    public Logger logger;
    private static VPPAppDisassociationHandler vppHandler;
    public static final String USER_RESOURCEID_LIST = "userResourceIdList";
    public static final String CLIENT_USERID_STR = "clientUserIdStr";
    public static final String CLIENT_USERID_LSIT = "clientUserIdList";
    
    public VPPAppDisassociationHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPAppDisassociationHandler getInstance() {
        if (VPPAppDisassociationHandler.vppHandler == null) {
            VPPAppDisassociationHandler.vppHandler = new VPPAppDisassociationHandler();
        }
        return VPPAppDisassociationHandler.vppHandler;
    }
    
    public ArrayList<Properties> disassociateVppApps(final JSONObject disassociationJson, final Long businessStoreID) throws Exception {
        ArrayList<Properties> failedDeviceList = null;
        final Long customerID = disassociationJson.getLong("CUSTOMER_ID");
        final int appAssignmentType = new VPPAppMgmtHandler().getVppGlobalAssignmentType(customerID, businessStoreID);
        try {
            if (appAssignmentType == 2) {
                failedDeviceList = this.disassociateLicensesFromDevices(disassociationJson, businessStoreID);
            }
            else if (appAssignmentType == 1) {
                failedDeviceList = this.disassociateAppsFromUser(disassociationJson, businessStoreID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in disassociateVppApps() ", ex);
        }
        return failedDeviceList;
    }
    
    private ArrayList<Properties> disassociateAppsFromUser(final JSONObject disassociationJson, final Long businessStoreID) throws Exception {
        ArrayList<Properties> failedDeviceList = null;
        final Object disassociationDeviceListObj = disassociationJson.get("deviceIdList");
        List<Long> deviceIdList = new ArrayList<Long>();
        deviceIdList = (ArrayList)disassociationDeviceListObj;
        final Long profileID = (Long)disassociationJson.get("PROFILE_ID");
        final String appStoreID = disassociationJson.getString("appStoreId");
        deviceIdList = this.getDeviceListWithUserBasedLicense(deviceIdList, businessStoreID, appStoreID);
        final List userResourceIdList = this.getUserResIdFromDeviceId(deviceIdList, profileID);
        disassociationJson.put("userResourceIdList", (Object)userResourceIdList);
        failedDeviceList = this.disassociateLicenseFromUsers(disassociationJson, businessStoreID);
        return failedDeviceList;
    }
    
    private ArrayList disassociateAndUpdateLicense(final JSONObject disassociationJson, final Long businessStoreID) throws JSONException {
        final Object entityListObj = disassociationJson.get("entityList");
        final List entitiesList = (ArrayList)entityListObj;
        this.logger.log(Level.INFO, "Starting to disassociate licenses for entities: {0} (serialNumbers/clientUserIds) from business store : {1}", new Object[] { entitiesList, businessStoreID });
        final Integer typeOfAssignment = disassociationJson.getInt("typeOfAssignment");
        ArrayList<Properties> failedDeviceList = new ArrayList<Properties>();
        ArrayList successList = new ArrayList();
        try {
            final int appStoreId = disassociationJson.getInt("appStoreId");
            final Properties prop = this.disassociateLicenseInBulk(disassociationJson, businessStoreID);
            successList = ((Hashtable<K, ArrayList>)prop).get("Success");
            final VPPAssetsHandler vppAssetsHandler = new VPPAssetsHandler();
            final Long assetID = vppAssetsHandler.getAssetIDForAdamID(businessStoreID, String.valueOf(appStoreId));
            this.logger.log(Level.INFO, "Licenses disassociation complete for entities (serialNumbers/clientUserIds) from business store : {0} -- successList {1}", new Object[] { entitiesList, businessStoreID, successList });
            if (!successList.isEmpty()) {
                if (typeOfAssignment == 2) {
                    final Criteria assetCriteria = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "VPP_ASSET_ID"), (Object)assetID, 0);
                    final Criteria serialNoCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)successList.toArray(), 8);
                    vppAssetsHandler.deleteAssetToManagedDeviceRel(assetCriteria.and(serialNoCriteria));
                }
                else {
                    final Criteria assetCriteria = new Criteria(Column.getColumn("MdVppAssetToVppUserRel", "VPP_ASSET_ID"), (Object)assetID, 0);
                    final Criteria clientStrCriteria = new Criteria(Column.getColumn("MdVppUser", "VPP_CLIENT_USER_ID"), (Object)successList.toArray(), 8);
                    vppAssetsHandler.deleteAssetToVppUserRel(clientStrCriteria.and(assetCriteria));
                }
                vppAssetsHandler.changeLicenseAvailableCountForAssets(assetID, successList.size(), "INCREMENT");
            }
            failedDeviceList = ((Hashtable<K, ArrayList<Properties>>)prop).get("Fail");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting associate user to license ", e);
        }
        return failedDeviceList;
    }
    
    public Properties disassociateLicenseInBulk(final JSONObject disassociationJson, final Long businessStoreID) {
        final Properties returnProp = new Properties();
        try {
            final Object entityListObj = disassociationJson.get("entityList");
            final List entitiesList = (ArrayList)entityListObj;
            final Integer typeOfAssignment = (Integer)disassociationJson.get("typeOfAssignment");
            final ArrayList<Properties> failedDeviceList = new ArrayList<Properties>();
            final ArrayList successList = new ArrayList();
            for (int lastIndex = 0, firstIndex = 0; firstIndex < entitiesList.size(); firstIndex = lastIndex) {
                VPPServiceConfigHandler.getInstance().checkAndFetchServiceUrl();
                final String maxLicenseDisassCountStr = VPPAppAPIRequestHandler.getInstance().getServiceUrl("maxBatchDisassociateLicenseCount");
                final Integer maxLicenseDisassociateCount = (maxLicenseDisassCountStr != null) ? Integer.parseInt(maxLicenseDisassCountStr) : 1;
                lastIndex = firstIndex + maxLicenseDisassociateCount;
                final List subEntitiesList = (lastIndex > entitiesList.size()) ? entitiesList.subList(firstIndex, entitiesList.size()) : entitiesList.subList(firstIndex, lastIndex);
                Properties prop = new Properties();
                JSONObject json = new JSONObject();
                json = disassociationJson;
                String entiryUsedForLicenseAssignment = "";
                if (typeOfAssignment == 2) {
                    final Object subEntitiesListAsObj = new ArrayList(subEntitiesList);
                    json.put("deviceSerialNumbersList", subEntitiesListAsObj);
                    prop = this.disassociateLicenseUsingSerialNumber(json, businessStoreID);
                    entiryUsedForLicenseAssignment = "serialNumber";
                }
                else {
                    json.put("clientUserIdList", (Object)new ArrayList(subEntitiesList));
                    prop = this.disassociateLicenseUsingClientUserId(disassociationJson, businessStoreID);
                    entiryUsedForLicenseAssignment = "clientUserIdStr";
                }
                if (!prop.containsKey("disassociations")) {
                    for (int failLicIndex = firstIndex; failLicIndex < entitiesList.size(); ++failLicIndex) {
                        final String valueOfTheEntity = entitiesList.get(failLicIndex);
                        final Properties failProp = this.getErrorProperty(prop, valueOfTheEntity);
                        if (!failProp.isEmpty()) {
                            failedDeviceList.add(failProp);
                        }
                    }
                    break;
                }
                for (int index = 0; index < ((Hashtable<K, List>)prop).get("disassociations").size(); ++index) {
                    final Properties disassocitedDevicePropList = ((Hashtable<K, List<Properties>>)prop).get("disassociations").get(index);
                    final String valueOfTheEntity2 = ((Hashtable<K, String>)disassocitedDevicePropList).get(entiryUsedForLicenseAssignment);
                    if (subEntitiesList.contains(valueOfTheEntity2)) {
                        successList.add(valueOfTheEntity2);
                    }
                }
            }
            ((Hashtable<String, ArrayList>)returnProp).put("Success", successList);
            ((Hashtable<String, ArrayList<Properties>>)returnProp).put("Fail", failedDeviceList);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while disassociateLicenseInBulk {0}", ex);
        }
        return returnProp;
    }
    
    public ArrayList<Properties> disassociateLicensesFromDevices(final JSONObject disassociationJson, final Long businessStoreID) throws JSONException {
        this.logger.log(Level.INFO, "Going to disassociate VPP License for device Props {0} for businessStoreID {1} :", new Object[] { disassociationJson, businessStoreID });
        ArrayList<Properties> failedDeviceList = new ArrayList<Properties>();
        final Object disassociationDeviceListObj = disassociationJson.get("deviceIdList");
        List<Long> deviceIdList = new ArrayList<Long>();
        deviceIdList = (ArrayList)disassociationDeviceListObj;
        final String appStoreId = disassociationJson.get("appStoreId").toString();
        deviceIdList = this.getDeviceWithDeviceBasedLicense(deviceIdList, businessStoreID, appStoreId);
        disassociationJson.put("typeOfAssignment", 2);
        try {
            final Criteria deviceIdCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)deviceIdList.toArray(), 8);
            final DataObject deviceIdDataObject = MDMUtil.getPersistence().get("MdDeviceInfo", deviceIdCriteria);
            final HashMap serialNumberToResMap = new HashMap();
            final Iterator deviceInfoIter = deviceIdDataObject.getRows("MdDeviceInfo");
            while (deviceInfoIter.hasNext()) {
                final Row deviceInfoRow = deviceInfoIter.next();
                final Long resourceId = (Long)deviceInfoRow.get("RESOURCE_ID");
                final String serialNumber = (String)deviceInfoRow.get("SERIAL_NUMBER");
                serialNumberToResMap.put(serialNumber, resourceId);
            }
            final List entireDeviceSerialNumberList = DBUtil.getColumnValuesAsList(deviceIdDataObject.getRows("MdDeviceInfo"), "SERIAL_NUMBER");
            disassociationJson.put("entityList", (Object)entireDeviceSerialNumberList);
            disassociationJson.put("entityMap", (Object)serialNumberToResMap);
            failedDeviceList = this.disassociateAndUpdateLicense(disassociationJson, businessStoreID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while disassociating licenses from devices : {0} ", e);
        }
        return failedDeviceList;
    }
    
    public ArrayList<Properties> disassociateLicenseFromUsers(final JSONObject disassociationJson, final Long businessStoreID) throws JSONException {
        this.logger.log(Level.INFO, "Going to disassociate VPP License for user. Props: {0} for businessStoreID {1} :", new Object[] { disassociationJson, businessStoreID });
        ArrayList<Properties> failedDeviceList = new ArrayList<Properties>();
        final Object usersJsonObject = disassociationJson.get("userResourceIdList");
        List userResourceIdList = new ArrayList();
        userResourceIdList = (ArrayList)usersJsonObject;
        disassociationJson.put("typeOfAssignment", 1);
        try {
            final Criteria businessCriteria = new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria managedUserCriteria = new Criteria(Column.getColumn("MdManagedUserToVppUserRel", "MANAGED_USER_ID"), (Object)userResourceIdList.toArray(), 8);
            final DataObject dataObject = new VPPManagedUserHandler().getVppUserDO(businessCriteria.and(managedUserCriteria));
            final HashMap clientIdToUserIdMap = new HashMap();
            final Iterator userInfoIter = dataObject.getRows("ManagedUser");
            final Iterator vppUserInfoIter = dataObject.getRows("MdVppUser");
            while (userInfoIter.hasNext()) {
                final Row userInfoRow = userInfoIter.next();
                final Row vppUserInfoRow = vppUserInfoIter.next();
                final Long userId = (Long)userInfoRow.get("MANAGED_USER_ID");
                final String clientUserId = (String)vppUserInfoRow.get("VPP_CLIENT_USER_ID");
                clientIdToUserIdMap.put(clientUserId, userId);
            }
            final List clientUserIdsList = DBUtil.getColumnValuesAsList(dataObject.getRows("MdVppUser"), "VPP_CLIENT_USER_ID");
            disassociationJson.put("entityList", (Object)clientUserIdsList);
            disassociationJson.put("entityMap", (Object)clientIdToUserIdMap);
            failedDeviceList = this.disassociateAndUpdateLicense(disassociationJson, businessStoreID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while disassociating license from users : {0}", e);
        }
        return failedDeviceList;
    }
    
    @Deprecated
    private void updateLicenseDO(final List queryList, final int appStoreId, final boolean isDeviceList) {
        try {
            final UpdateQuery udpateQuery = (UpdateQuery)new UpdateQueryImpl("MdVPPLicenseDetails");
            String tableName = null;
            final String licenseDetailId = null;
            final String managedId = null;
            Join join1 = null;
            Join join2 = null;
            Criteria cri;
            if (isDeviceList) {
                join1 = new Join("MdVPPLicenseDetails", "MdVPPLicenseToDevice", new String[] { "LICENSE_DETAIL_ID" }, new String[] { "LICENSE_DETAIL_ID" }, 2);
                join2 = new Join("MdVPPLicenseToDevice", "MdDeviceInfo", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                cri = new Criteria(new Column("MdDeviceInfo", "SERIAL_NUMBER"), (Object)queryList.toArray(), 8);
                tableName = "MdVPPLicenseToDevice";
            }
            else {
                join1 = new Join("MdVPPLicenseDetails", "MdVPPLicenseToUser", new String[] { "LICENSE_DETAIL_ID" }, new String[] { "LICENSE_DETAIL_ID" }, 2);
                join2 = new Join("MdVPPLicenseToUser", "MdVPPManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
                cri = new Criteria(new Column("MdVPPManagedUser", "VPP_CLIENT_USER_ID"), (Object)queryList.toArray(), 8);
                tableName = "MdVPPLicenseToUser";
            }
            udpateQuery.addJoin(join1);
            udpateQuery.addJoin(join2);
            final Criteria cadamId = new Criteria(new Column("MdVPPLicenseDetails", "ADAM_ID"), (Object)appStoreId, 0);
            udpateQuery.setCriteria(cri.and(cadamId));
            udpateQuery.setUpdateColumn("STATUS", (Object)0);
            MDMUtil.getPersistence().update(udpateQuery);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPLicenseDetails"));
            sQuery.addJoin(join1);
            sQuery.addJoin(join2);
            sQuery.setCriteria(cri.and(cadamId));
            sQuery.addSelectColumn(Column.getColumn("MdVPPLicenseDetails", "*"));
            sQuery.addSelectColumn(Column.getColumn(tableName, "*"));
            final DataObject deleteDO = MDMUtil.getPersistence().get(sQuery);
            deleteDO.deleteRows(tableName, cadamId);
            MDMUtil.getPersistence().update(deleteDO);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception while getting license dataObject ", (Throwable)ex);
        }
    }
    
    private Properties disassociateLicenseUsingSerialNumber(final JSONObject disassociationJson, final Long businessStoreID) throws JSONException {
        Properties prop = new Properties();
        final Object serialNumberListObj = disassociationJson.get("deviceSerialNumbersList");
        final List serialNumberList = (ArrayList)serialNumberListObj;
        final Long customerId = (Long)disassociationJson.get("CUSTOMER_ID");
        final int appStoreId = disassociationJson.getInt("appStoreId");
        final Boolean notifyDisassociation = (Boolean)disassociationJson.get("notifyDisassociation");
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            if (sToken != null) {
                final String command = new VPPAPIRequestGenerator(sToken).getDisassociateAdamIdFromDevicesCommand(businessStoreID, appStoreId, "STDQ", serialNumberList, notifyDisassociation);
                final String dummyCommand = command.replace(sToken, "*****");
                this.logger.log(Level.INFO, "Request for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
                final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "manageVPPLicensesByAdamIdSrvUrl", sToken, businessStoreID);
                this.logger.log(Level.INFO, "Response for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
                prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "manageVPPLicensesByAdamIdSrvUrl");
            }
            else {
                this.logger.log(Level.INFO, "No business store found in DB for app license disassociation. May be token removal case");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while sending disassociateLicenseUsingSerialNumber", e);
        }
        return prop;
    }
    
    public Properties disassociateLicenseUsingClientUserId(final JSONObject disassociationJson, final Long businessStoreID) throws JSONException {
        Properties prop = new Properties();
        final Object clientUserIdObj = disassociationJson.get("clientUserIdList");
        final Long customerId = (Long)disassociationJson.get("CUSTOMER_ID");
        final int appStoreId = disassociationJson.getInt("appStoreId");
        final Boolean notifyDisassociation = (Boolean)disassociationJson.get("notifyDisassociation");
        try {
            final List clientUserIdList = (ArrayList)clientUserIdObj;
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getDisassociateAdamIdFromUsersCommand(customerId, appStoreId, "STDQ", clientUserIdList, notifyDisassociation);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "manageVPPLicensesByAdamIdSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response for manageVPPLicensesByAdamIdSrvUrl for businessStoreID: {0} is {1}", new Object[] { businessStoreID, responseJSON });
            prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "manageVPPLicensesByAdamIdSrvUrl");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while seding associate user srv", e);
        }
        return prop;
    }
    
    private List getDeviceWithDeviceBasedLicense(final List deviceIDList, final Long businessStoreID, final String adamID) {
        final List resList = new ArrayList();
        final List tempIDList = new ArrayList(deviceIDList);
        try {
            this.logger.log(Level.INFO, "START: Excluding deviceIDs that doesn't have licenses in businessStore {0}", businessStoreID);
            final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAssetToManagedDeviceRel"));
            deviceQuery.addJoin(new Join("MdVppAssetToManagedDeviceRel", "MdVppAsset", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            deviceQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            deviceQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            deviceQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            final Criteria businessCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria adamIDCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamID, 0);
            final Criteria deviceIDsCrit = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)deviceIDList.toArray(), 8);
            deviceQuery.setCriteria(businessCriteria.and(adamIDCriteria).and(deviceIDsCrit));
            deviceQuery.addSelectColumn(Column.getColumn("MdVppAssetToManagedDeviceRel", "*"));
            final DataObject deviceDO = MDMUtil.getPersistence().get(deviceQuery);
            if (!deviceDO.isEmpty()) {
                final Iterator userIter = deviceDO.getRows("MdVppAssetToManagedDeviceRel");
                while (userIter.hasNext()) {
                    final Row userRow = userIter.next();
                    final Long deviceID = (Long)userRow.get("MANAGED_DEVICE_ID");
                    resList.add(deviceID);
                }
                tempIDList.removeAll(resList);
                if (!tempIDList.isEmpty()) {
                    this.logger.log(Level.WARNING, "END: Excluding deviceIDs {0} since they doesn't have licenses in businessStore {1}", new Object[] { tempIDList, businessStoreID });
                }
                else {
                    this.logger.log(Level.WARNING, "END: All deviceIDs {0} have licenses in businessStore {1}", new Object[] { deviceIDList, businessStoreID });
                }
            }
            else {
                this.logger.log(Level.WARNING, "No device based license is associated with the deviceIDs {0} in businessStore {1}", new Object[] { deviceIDList, businessStoreID });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "getUserResListForUserBasedLicense", e);
        }
        return resList;
    }
    
    private List getDeviceListWithUserBasedLicense(final List deviceIDList, final Long businessStoreID, final String adamID) {
        final List resList = new ArrayList();
        try {
            this.logger.log(Level.INFO, "START: Excluding deviceIDs that doesn't have licenses in businessStore {0}", businessStoreID);
            final SelectQuery userQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            userQuery.addJoin(new Join("ManagedUserToDevice", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            userQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppAssetToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            userQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppAsset", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            userQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            userQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            userQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            final Criteria businessCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria adamIDCriteria = new Criteria(Column.getColumn("MdVppAsset", "ADAM_ID"), (Object)adamID, 0);
            final Criteria deviceIDsCrit = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceIDList.toArray(), 8);
            userQuery.setCriteria(businessCriteria.and(adamIDCriteria).and(deviceIDsCrit));
            userQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "*"));
            final DataObject userDO = MDMUtil.getPersistence().get(userQuery);
            if (!userDO.isEmpty()) {
                final Iterator userIter = userDO.getRows("ManagedUserToDevice");
                while (userIter.hasNext()) {
                    final Row userRow = userIter.next();
                    final Long deviceID = (Long)userRow.get("MANAGED_DEVICE_ID");
                    resList.add(deviceID);
                }
                deviceIDList.removeAll(resList);
                if (!deviceIDList.isEmpty()) {
                    this.logger.log(Level.WARNING, "END: Excluding deviceIDs {0} since they doesn't have licenses in businessStore {1}", new Object[] { deviceIDList, businessStoreID });
                }
                else {
                    this.logger.log(Level.WARNING, "END: All deviceIDs {0} have licenses in businessStore {1}", new Object[] { deviceIDList, businessStoreID });
                }
            }
            else {
                this.logger.log(Level.WARNING, "No user based license is associated with the deviceIDs {0} in businessStore {1}", new Object[] { deviceIDList, businessStoreID });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "getUserResListForUserBasedLicense", e);
        }
        return resList;
    }
    
    private List getUserResIdFromDeviceId(final List deviceIdList, final Long profileID) {
        final List userIdList = new ArrayList();
        DMDataSetWrapper ds = null;
        try {
            final Criteria criteria = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceIdList.toArray(), 8);
            final List userIdListStr = DBUtil.getDistinctColumnValue("ManagedUserToDevice", "MANAGED_USER_ID", criteria);
            for (int i = 0; i < userIdListStr.size(); ++i) {
                userIdList.add(Long.parseLong(userIdListStr.get(i)));
            }
            this.logger.log(Level.INFO, "getUserResIdFromDeviceId : user id list for device list {0}", userIdList);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            sQuery.addJoin(new Join("ManagedUserToDevice", "RecentProfileForResource", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria cDevice = new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userIdList.toArray(), 8);
            final Criteria cResIdNotInCri = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIdList.toArray(), 9);
            final Criteria cCollectionId = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 0);
            final Criteria cDelete = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            sQuery.setCriteria(cCollectionId.and(cResIdNotInCri).and(cDelete).and(cDevice));
            final Column userIdCol = Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID");
            sQuery.addSelectColumn(userIdCol);
            Column deviceCol = Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID");
            deviceCol = deviceCol.count();
            deviceCol.setColumnAlias("DEVICE_ID_COUNT");
            sQuery.addSelectColumn(deviceCol);
            final List groupByColumns = new ArrayList();
            groupByColumns.add(userIdCol);
            final GroupByClause grpByCls = new GroupByClause(groupByColumns);
            sQuery.setGroupByClause(grpByCls);
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            final HashMap usermap = new HashMap();
            while (ds.next()) {
                final Long userId = (Long)ds.getValue("MANAGED_USER_ID");
                final Integer deviceCount = (Integer)ds.getValue("DEVICE_ID_COUNT");
                usermap.put(userId, deviceCount);
            }
            this.logger.log(Level.INFO, "getUserResIdFromDeviceId : user id and device count map {0}", usermap);
            final Set userIdSet = usermap.keySet();
            userIdList.removeAll(userIdSet);
            this.logger.log(Level.INFO, "getUserResIdFromDeviceId : Final user id details to disassociate license {0}", userIdList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getUserIdFromDeviceId", e);
        }
        return userIdList;
    }
    
    private Properties getErrorProperty(final Properties prop, final String id) {
        String errorMessage = null;
        final Properties failedProp = new Properties();
        final Properties failedProperty = new Properties();
        long errorNumber;
        if (prop.containsKey("errorNumber")) {
            errorNumber = Long.parseLong(((Hashtable<K, Object>)prop).get("errorNumber").toString());
            errorMessage = ((Hashtable<K, Object>)prop).get("errorMessage").toString();
        }
        else {
            errorNumber = Long.parseLong(((Hashtable<K, Object>)((Hashtable<K, ArrayList<Properties>>)prop).get("disassociations").get(0)).get("errorCode").toString());
            errorMessage = ((Hashtable<K, Object>)((Hashtable<K, ArrayList<Properties>>)prop).get("disassociations").get(0)).get("errorMessage").toString();
        }
        if (errorNumber != 9619L) {
            ((Hashtable<String, String>)failedProperty).put("REMARKS", errorMessage);
            ((Hashtable<String, Long>)failedProperty).put("ERROR_CODE", errorNumber);
            ((Hashtable<String, Properties>)failedProp).put(id, failedProperty);
        }
        return failedProp;
    }
    
    static {
        VPPAppDisassociationHandler.vppHandler = null;
    }
}
