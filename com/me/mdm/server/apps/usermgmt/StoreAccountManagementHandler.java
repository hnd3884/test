package com.me.mdm.server.apps.usermgmt;

import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import com.me.mdm.server.apps.android.afw.usermgmt.EMMManagedUsersDirectory;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import java.util.Collection;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Join;
import org.json.JSONException;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.apache.commons.collections.IteratorUtils;
import org.json.JSONObject;
import java.util.logging.Logger;

public class StoreAccountManagementHandler
{
    private Logger logger;
    public static final int BUSINESS_STORE_ACCOUNT_STATUS_ACTIVE_PRIMARY = 1;
    public static final int BUSINESS_STORE_ACCOUNT_STATUS_ACTIVE_SECONDARY = 2;
    public static final int BUSINESS_STORE_ACCOUNT_STATUS_REMOVED = 3;
    public static final int BUSINESS_STORE_ACCOUNT_STATE_ENABLED = 1;
    public static final int BUSINESS_STORE_ACCOUNT_STATE_DISABLED = 0;
    
    public StoreAccountManagementHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public void addOrUpdateStoreUserAccounts(final JSONObject jsonObject) throws JSONException {
        try {
            final Long businessStoreId = jsonObject.getLong("businessstore_id");
            final JSONObject syncedUserDetails = jsonObject.optJSONObject("users");
            if (syncedUserDetails != null && syncedUserDetails.length() > 0) {
                final Iterator iterator = syncedUserDetails.keys();
                final List<String> syncedEmailIDList = IteratorUtils.toList(iterator);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BusinessStoreUsers"));
                final Criteria storeIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)businessStoreId, 0);
                final Criteria userEmailCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_MDM_ID"), (Object)syncedEmailIDList.toArray(), 8);
                selectQuery.setCriteria(storeIdCriteria.and(userEmailCriteria));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_MDM_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final DataObject tempClonedDataObject = (DataObject)dataObject.clone();
                for (final String syncedEmail : syncedEmailIDList) {
                    final JSONObject userJSON = syncedUserDetails.getJSONObject(syncedEmail);
                    final Criteria emailIDCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_MDM_ID"), (Object)syncedEmail, 0, false);
                    final Iterator existingRowsForSycnedEmailID = tempClonedDataObject.getRows("BusinessStoreUsers", emailIDCriteria);
                    final String syncedUserStoreId = String.valueOf(userJSON.get("BS_STORE_ID"));
                    boolean isNewUser = true;
                    while (existingRowsForSycnedEmailID.hasNext()) {
                        final Row existingRow = existingRowsForSycnedEmailID.next();
                        final String existingUserStoreId = (String)existingRow.get("BS_STORE_ID");
                        if (!existingUserStoreId.equals(syncedUserStoreId)) {
                            this.logger.log(Level.INFO, "Updating Business user store from {0} to {1} for user id {2}", new Object[] { existingUserStoreId, syncedUserStoreId, existingRow.get("BS_USER_ID") });
                            existingRow.set("BS_STORE_ID", (Object)syncedUserStoreId);
                            dataObject.updateRow(existingRow);
                        }
                        isNewUser = false;
                    }
                    if (isNewUser) {
                        final Row newUser = new Row("BusinessStoreUsers");
                        newUser.set("BUSINESSSTORE_ID", (Object)businessStoreId);
                        newUser.set("BS_STORE_ID", (Object)syncedUserStoreId);
                        newUser.set("BS_MDM_ID", (Object)syncedEmail);
                        dataObject.addRow(newUser);
                        this.logger.log(Level.INFO, "New user added for business store {0}", newUser.toString());
                    }
                }
                MDMUtil.getPersistence().update(dataObject);
            }
            this.logger.log(Level.INFO, "Business Store Users addition completed for Business Store Id {0}", new Object[] { businessStoreId });
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Unable to add Business Store Users", exp);
        }
    }
    
    public void addOrUpdateStoreUserDevices(final JSONObject jsonObject) throws JSONException {
        try {
            final Long businessStoreId = jsonObject.getLong("businessstore_id");
            final JSONObject users = jsonObject.optJSONObject("usersanddevices");
            if (users != null) {
                final Iterator iterator = users.keys();
                final List usersList = IteratorUtils.toList(iterator);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BusinessStoreUsers"));
                final Join bsUserJoin = new Join("BusinessStoreUsers", "BusinessStoreUserDevices", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 1);
                final Criteria storeUserIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"), (Object)usersList.toArray(), 8);
                final Criteria storeIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)businessStoreId, 0);
                selectQuery.setCriteria(storeIdCriteria.and(storeUserIdCriteria));
                selectQuery.addJoin(bsUserJoin);
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUserDevices", "*"));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "*"));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                for (int i = 0; i < usersList.size(); ++i) {
                    final String storeUserID = usersList.get(i);
                    final JSONArray devices = users.getJSONArray(storeUserID);
                    final Criteria userCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"), (Object)storeUserID, 0);
                    final Long userId = (Long)dataObject.getRow("BusinessStoreUsers", userCriteria).get("BS_USER_ID");
                    final Criteria devicesUserCriteria = new Criteria(Column.getColumn("BusinessStoreUserDevices", "BS_USER_ID"), (Object)userId, 0);
                    for (int j = 0; j < devices.length(); ++j) {
                        final String deviceID = String.valueOf(devices.get(j));
                        final Criteria deviceCriteria = new Criteria(Column.getColumn("BusinessStoreUserDevices", "BS_DEVICE_IDENTIFIER"), (Object)deviceID, 0);
                        Row r = dataObject.getRow("BusinessStoreUserDevices", devicesUserCriteria.and(deviceCriteria));
                        if (r == null) {
                            r = new Row("BusinessStoreUserDevices");
                            r.set("BS_USER_ID", (Object)userId);
                            r.set("BS_DEVICE_IDENTIFIER", (Object)deviceID);
                            dataObject.addRow(r);
                        }
                    }
                }
                MDMUtil.getPersistence().update(dataObject);
            }
            this.logger.log(Level.INFO, "Business Store Users Devices {0} Added Successfully for Business Store Id {1}", new Object[] { jsonObject, businessStoreId });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Unable to add Business Store Users Device", (Throwable)e);
        }
    }
    
    public Boolean isBSDeviceAvailableForManagedDevice(final Long resourceID) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            final Join bsUsersToMDeviceJoin = new Join("MdDeviceInfo", "BSUsersToManagedDevices", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Criteria bsUserCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"), (Object)Column.getColumn("BusinessStoreUserDevices", "BS_USER_ID"), 0);
            final Criteria gsfIDCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "GOOGLE_PLAY_SERVICE_ID"), (Object)Column.getColumn("BusinessStoreUserDevices", "BS_DEVICE_IDENTIFIER"), 0);
            final Join bsUserDevicesJoin = new Join("BSUsersToManagedDevices", "BusinessStoreUserDevices", bsUserCriteria.and(gsfIDCriteria), 2);
            sQuery.addJoin(bsUsersToMDeviceJoin);
            sQuery.addJoin(bsUserDevicesJoin);
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "GOOGLE_PLAY_SERVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"));
            sQuery.addSelectColumn(Column.getColumn("BusinessStoreUserDevices", "BS_USER_ID"));
            sQuery.addSelectColumn(Column.getColumn("BusinessStoreUserDevices", "BS_USER_DEVICE_ID"));
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            sQuery.setCriteria(resourceCriteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            return !dO.isEmpty();
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in getBSUserDeviceForManagedDevice()", (Throwable)ex);
            return false;
        }
    }
    
    public void addOrUpdateStoreUserToManagedDevice(final JSONObject jsonObject) throws JSONException {
        try {
            final Long storeUserId = jsonObject.getLong("BS_USER_ID");
            final Long managedDeviceId = jsonObject.getLong("MANAGED_DEVICE_ID");
            final Integer accStatus = jsonObject.getInt("ACCOUNT_STATUS");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BSUsersToManagedDevices"));
            selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "*"));
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0);
            final Criteria bsUserIdCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"), (Object)storeUserId, 0);
            selectQuery.setCriteria(managedDeviceCriteria.and(bsUserIdCriteria));
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            if (dO.isEmpty()) {
                final Row row = new Row("BSUsersToManagedDevices");
                row.set("BS_USER_ID", (Object)storeUserId);
                row.set("MANAGED_DEVICE_ID", (Object)managedDeviceId);
                row.set("ACCOUNT_STATUS", (Object)accStatus);
                dO.addRow(row);
            }
            else {
                final Row row = dO.getRow("BSUsersToManagedDevices");
                row.set("ACCOUNT_STATUS", (Object)accStatus);
                dO.updateRow(row);
            }
            MDMUtil.getPersistence().update(dO);
            this.logger.log(Level.INFO, "Business Store Users Devices {0} Added Successfully for Business Store Id {1}", new Object[] { jsonObject, storeUserId });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Unable to add Business Store Users Device", (Throwable)e);
        }
    }
    
    public void addOrUpdateStoreUserToManagedDeviceAccState(final JSONObject jsonObject, final Integer accState) throws JSONException {
        try {
            final Long storeUserId = jsonObject.getLong("BS_USER_ID");
            final Long managedDeviceId = jsonObject.getLong("MANAGED_DEVICE_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BSUsersToManagedDevices"));
            final Join accStateJoin = new Join("BSUsersToManagedDevices", "BSUserToManagedDeviceAccState", new String[] { "BS_USER_TO_DEVICE_ID" }, new String[] { "BS_USER_TO_DEVICE_ID" }, 1);
            selectQuery.addJoin(accStateJoin);
            selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "*"));
            selectQuery.addSelectColumn(Column.getColumn("BSUserToManagedDeviceAccState", "*"));
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0);
            final Criteria bsUserIdCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"), (Object)storeUserId, 0);
            selectQuery.setCriteria(managedDeviceCriteria.and(bsUserIdCriteria));
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            if (!dO.isEmpty()) {
                Row row = dO.getRow("BSUserToManagedDeviceAccState");
                if (row == null) {
                    row = new Row("BSUserToManagedDeviceAccState");
                    row.set("ACCOUNT_STATE", (Object)accState);
                    row.set("BS_USER_TO_DEVICE_ID", dO.getFirstValue("BSUsersToManagedDevices", "BS_USER_TO_DEVICE_ID"));
                    dO.addRow(row);
                }
                else {
                    row.set("ACCOUNT_STATE", (Object)accState);
                    dO.updateRow(row);
                }
                MDMUtil.getPersistence().update(dO);
            }
            this.logger.log(Level.INFO, "Changed Business store users to Managed device Google Account state {0}", new Object[] { jsonObject });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Unable to add Business Store Users Device", (Throwable)e);
        }
    }
    
    public int getDeviceAccountState(final Long resourceId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BSUsersToManagedDevices"));
        final Join accStateJoin = new Join("BSUsersToManagedDevices", "BSUserToManagedDeviceAccState", new String[] { "BS_USER_TO_DEVICE_ID" }, new String[] { "BS_USER_TO_DEVICE_ID" }, 2);
        selectQuery.addJoin(accStateJoin);
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "*"));
        selectQuery.addSelectColumn(Column.getColumn("BSUserToManagedDeviceAccState", "*"));
        final Criteria bsAccStatusCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"), (Object)1, 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(bsAccStatusCriteria.and(resCriteria));
        final DataObject dO = DataAccess.get(selectQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getRow("BSUserToManagedDeviceAccState");
            return (int)row.get("ACCOUNT_STATE");
        }
        return 0;
    }
    
    public Long getBSUserIdFromMDMId(final Long bsId, final String identifier) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BusinessStoreUsers"));
        final Criteria identifierCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_MDM_ID"), (Object)identifier, 0);
        final Criteria bsIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)bsId, 0);
        selectQuery.addSelectColumn(new Column("BusinessStoreUsers", "*"));
        selectQuery.setCriteria(bsIdCriteria.and(identifierCriteria));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        if (!dO.isEmpty()) {
            return (Long)dO.getFirstRow("BusinessStoreUsers").get("BS_USER_ID");
        }
        return null;
    }
    
    public Long getBSUserIdFromStoreId(final Long bsId, final String identifier) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BusinessStoreUsers"));
        final Criteria identifierCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"), (Object)identifier, 0);
        final Criteria bsIdCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)bsId, 0);
        selectQuery.addSelectColumn(new Column("BusinessStoreUsers", "*"));
        selectQuery.setCriteria(bsIdCriteria.and(identifierCriteria));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        if (!dO.isEmpty()) {
            return (Long)dO.getFirstRow("BusinessStoreUsers").get("BS_USER_ID");
        }
        return null;
    }
    
    public ArrayList getStoreAccountUsers() {
        ArrayList usersList = new ArrayList();
        try {
            final DataObject dataObject = DataAccess.get("BusinessStoreUsers", (Criteria)null);
            final Iterator rowIterator = dataObject.getRows("BusinessStoreUsers");
            usersList = (ArrayList)DBUtil.getColumnValuesAsList(rowIterator, "BS_STORE_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Unable to get Business Store Users ", e);
        }
        return usersList;
    }
    
    public String getStoreAccountUserByDevice(final Long resourceId) {
        return this.getStoreAccountUserByDevice(Arrays.asList(resourceId)).get(resourceId);
    }
    
    public Map<Long, String> getStoreAccountUserByDevice(final List<Long> resourceList) {
        DMDataSetWrapper responseData = null;
        final Map storeAccountToDevice = new HashMap();
        try {
            final List<List> chunkList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 5000);
            final Iterator<List> iterator = (Iterator<List>)chunkList.iterator();
            while (iterator.hasNext()) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BSUsersToManagedDevices"));
                final Join businessStoreUserJoin = new Join("BSUsersToManagedDevices", "BusinessStoreUsers", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 2);
                selectQuery.addJoin(businessStoreUserJoin);
                selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_TO_DEVICE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"));
                selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"));
                final Criteria criteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)iterator.next().toArray(), 8);
                final Criteria isActivecriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"), (Object)1, 0);
                selectQuery.setCriteria(criteria.and(isActivecriteria));
                responseData = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (responseData.next()) {
                    final String userId = responseData.getValue("BS_STORE_ID").toString();
                    final Long resourceId = (Long)responseData.getValue("MANAGED_DEVICE_ID");
                    storeAccountToDevice.put(resourceId, userId);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getting getStoreAccountUserByDevice", e);
        }
        return storeAccountToDevice;
    }
    
    private DMDataSetWrapper getStoreAccountUsersByDevicesResult(final List resList) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Join managedDeviceJoin = new Join("ManagedDevice", "BSUsersToManagedDevices", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join businessStoreUserJoin = new Join("BSUsersToManagedDevices", "BusinessStoreUsers", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 2);
        selectQuery.addJoin(managedDeviceJoin);
        selectQuery.addJoin(businessStoreUserJoin);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_TO_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"));
        final Criteria resCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        final Criteria primaryAccCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"), (Object)1, 0);
        selectQuery.setCriteria(resCriteria.and(primaryAccCriteria));
        return DMDataSetWrapper.executeQuery((Object)selectQuery);
    }
    
    public Map<Long, JSONObject> getStoreAccountUsersByDevicesMap(final ArrayList resList) throws JSONException {
        final Map<Long, JSONObject> deviceToBStoreUserIdMap = new HashMap<Long, JSONObject>();
        try {
            final DMDataSetWrapper responseData = this.getStoreAccountUsersByDevicesResult(resList);
            while (responseData.next()) {
                final JSONObject userIdJson = new JSONObject();
                final Long resId = (Long)responseData.getValue("MANAGED_DEVICE_ID");
                userIdJson.put("MANAGED_DEVICE_ID", (Object)resId);
                userIdJson.put("BS_STORE_ID", (Object)responseData.getValue("BS_STORE_ID").toString());
                deviceToBStoreUserIdMap.put(resId, userIdJson);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception ", e);
        }
        return deviceToBStoreUserIdMap;
    }
    
    public JSONObject getStoreAccountUsersByDevices(final ArrayList resList) throws JSONException {
        DMDataSetWrapper responseData = null;
        final JSONObject usersList = new JSONObject();
        final JSONArray usersAvailableList = new JSONArray();
        JSONArray usersNotAvailableList = new JSONArray();
        final ArrayList usersNotAvailableArrayList = new ArrayList(resList);
        try {
            responseData = this.getStoreAccountUsersByDevicesResult(resList);
            while (responseData.next()) {
                final JSONObject userIdJson = new JSONObject();
                final Long resId = (Long)responseData.getValue("MANAGED_DEVICE_ID");
                userIdJson.put("MANAGED_DEVICE_ID", (Object)resId);
                userIdJson.put("BS_STORE_ID", (Object)responseData.getValue("BS_STORE_ID").toString());
                usersAvailableList.put((Object)userIdJson);
                usersNotAvailableArrayList.remove(resId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception ", e);
        }
        usersNotAvailableList = new JSONArray((Collection)usersNotAvailableArrayList);
        usersList.put("UsersAvailableList", (Object)usersAvailableList);
        usersList.put("UsersNotAvailableList", (Object)usersNotAvailableList);
        return usersList;
    }
    
    private SelectQuery getStoreAccountUserDeviceDetailsQuery(final List resList) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Join managedDeviceJoin = new Join("ManagedDevice", "BSUsersToManagedDevices", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
        final Join businessStoreUserJoin = new Join("BSUsersToManagedDevices", "BusinessStoreUsers", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 1);
        final Join mdDeviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(managedDeviceJoin);
        selectQuery.addJoin(businessStoreUserJoin);
        selectQuery.addJoin(mdDeviceInfoJoin);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_TO_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_STORE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "GOOGLE_PLAY_SERVICE_ID"));
        final Criteria criteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resList.toArray(), 8);
        final Criteria isActivecriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"), (Object)1, 0);
        selectQuery.setCriteria(criteria.and(isActivecriteria));
        return selectQuery;
    }
    
    public Map<Long, JSONObject> getStoreAccountUserDeviceDetailsByDevicesMap(final List resList) {
        DMDataSetWrapper responseDS = null;
        final Map<Long, JSONObject> resToUserObject = new HashMap<Long, JSONObject>();
        try {
            final SelectQuery selectQuery = this.getStoreAccountUserDeviceDetailsQuery(resList);
            responseDS = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (responseDS.next()) {
                final JSONObject jsonObject = new JSONObject();
                final Long resId = (Long)responseDS.getValue("MANAGED_DEVICE_ID");
                jsonObject.put("MANAGED_DEVICE_ID", (Object)resId);
                jsonObject.put("BS_STORE_ID", (Object)responseDS.getValue("BS_STORE_ID").toString());
                jsonObject.put("GOOGLE_PLAY_SERVICE_ID", (Object)responseDS.getValue("GOOGLE_PLAY_SERVICE_ID").toString());
                resToUserObject.put(resId, jsonObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception ", e);
        }
        return resToUserObject;
    }
    
    public JSONObject getStoreAccountUserDeviceDetailsByDevices(final List resList) throws JSONException {
        DMDataSetWrapper responseDS = null;
        final JSONObject usersanddevices = new JSONObject();
        final JSONArray usersAvailableList = new JSONArray();
        JSONArray usersNotAvailableList = new JSONArray();
        final ArrayList usersNotAvailableArrayList = new ArrayList(resList);
        try {
            final SelectQuery selectQuery = this.getStoreAccountUserDeviceDetailsQuery(resList);
            responseDS = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (responseDS.next()) {
                final JSONObject jsonObject = new JSONObject();
                final Long resId = (Long)responseDS.getValue("MANAGED_DEVICE_ID");
                jsonObject.put("MANAGED_DEVICE_ID", (Object)resId);
                jsonObject.put("BS_STORE_ID", (Object)responseDS.getValue("BS_STORE_ID").toString());
                jsonObject.put("GOOGLE_PLAY_SERVICE_ID", (Object)responseDS.getValue("GOOGLE_PLAY_SERVICE_ID").toString());
                usersAvailableList.put((Object)jsonObject);
                usersNotAvailableArrayList.remove(resId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception ", e);
        }
        usersNotAvailableList = new JSONArray((Collection)usersNotAvailableArrayList);
        usersanddevices.put("UsersAvailableList", (Object)usersAvailableList);
        usersanddevices.put("UsersNotAvailableList", (Object)usersNotAvailableList);
        return usersanddevices;
    }
    
    public List addOrUpdateStoreUserForDevice(final Long customerId, final Long resourceId, final String udid) {
        try {
            final List resourceDetailsList = new ArrayList();
            final JSONObject resJSON = new JSONObject();
            resJSON.put("resID", (Object)resourceId);
            resJSON.put("udid", (Object)("udid#" + udid));
            resourceDetailsList.add(resJSON);
            return this.addOrUpdateStoreUserForDevice(customerId, resourceDetailsList);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateStoreUserForDevice", (Throwable)ex);
            return null;
        }
    }
    
    public List addOrUpdateStoreUserForDevice(final Long customerId, final List resList) {
        this.logger.log(Level.INFO, "addOrUpdateStoreUserForDevice starts for resList {0}", resList);
        final List successResList = new ArrayList();
        final List failureResList = new ArrayList();
        try {
            final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final long bsID = playstoreDetails.getLong("BUSINESSSTORE_ID");
            final EMMManagedUsersDirectory emmUsersDirectory = new EMMManagedUsersDirectory();
            emmUsersDirectory.initialize(playstoreDetails);
            final List resourceIdList = new ArrayList();
            final JSONArray udidList = new JSONArray();
            for (final Object resource : resList) {
                final JSONObject resJSON = (JSONObject)resource;
                resourceIdList.add(resJSON.getLong("resID"));
                final JSONObject userJSON = new JSONObject();
                userJSON.put("UserIdentifier", (Object)String.valueOf(resJSON.get("udid")));
                udidList.put((Object)userJSON);
            }
            JSONObject usersJSON = new JSONObject();
            usersJSON.put("users", (Object)udidList);
            final JSONObject responseJSON = emmUsersDirectory.addUser(usersJSON);
            final JSONObject insertedUsers = responseJSON.getJSONObject("SuccessList");
            usersJSON = new JSONObject();
            usersJSON.put("users", (Object)insertedUsers);
            usersJSON.put("businessstore_id", bsID);
            this.addOrUpdateStoreUserAccounts(usersJSON);
            for (int i = 0; i < resourceIdList.size(); ++i) {
                try {
                    final Long resourceId = resourceIdList.get(i);
                    final JSONObject userJSON2 = (JSONObject)udidList.get(i);
                    final String mdmUserId = userJSON2.get("UserIdentifier").toString();
                    final Long bsUserId = this.getBSUserIdFromMDMId(bsID, mdmUserId);
                    if (bsUserId != null) {
                        final JSONObject mappingJSON = new JSONObject();
                        mappingJSON.put("BS_USER_ID", (Object)bsUserId);
                        mappingJSON.put("MANAGED_DEVICE_ID", (Object)resourceId);
                        mappingJSON.put("ACCOUNT_STATUS", 1);
                        this.addOrUpdateStoreUserToManagedDevice(mappingJSON);
                        this.addOrUpdateStoreUserToManagedDeviceAccState(mappingJSON, 1);
                        successResList.add(resourceId);
                    }
                    else {
                        failureResList.add(resourceId);
                        this.logger.log(Level.INFO, "Insert user failed. So, not sending Add AFW command for resource {0}", resourceId);
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception in handling afw account addition", ex);
                }
            }
            if (!failureResList.isEmpty()) {
                new AFWAccountStatusHandler().updateUserInsertionFailure(failureResList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in handling afw account store user addition", e);
        }
        this.logger.log(Level.INFO, "addOrUpdateStoreUserForDevice completed");
        return successResList;
    }
    
    public void deleteStoreUserForDevice(final Long customerId, final List resList) {
        this.logger.log(Level.INFO, "deleteStoreUserForDevice starts for resList {0}", resList);
        try {
            final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final EMMManagedUsersDirectory emmUsersDirectory = new EMMManagedUsersDirectory();
            emmUsersDirectory.initialize(playstoreDetails);
            final List userIdList = new ArrayList();
            final Long bsId = playstoreDetails.getLong("BUSINESSSTORE_ID");
            final JSONArray udidList = new JSONArray();
            for (final Object resource : resList) {
                final JSONObject userJSON = new JSONObject();
                final String storeId = this.getStoreAccountUserByDevice((Long)resource);
                if (storeId != null) {
                    userJSON.put("BS_STORE_ID", (Object)storeId);
                    udidList.put((Object)userJSON);
                    userIdList.add(this.getBSUserIdFromStoreId(bsId, storeId));
                }
                else {
                    this.logger.log(Level.INFO, "Resource {0} does not contain AfW account. So need not delete it", resource);
                }
            }
            final JSONObject usersJSON = new JSONObject();
            usersJSON.put("users", (Object)udidList);
            emmUsersDirectory.deleteUser(usersJSON);
            if (!userIdList.isEmpty()) {
                DataAccess.delete("BusinessStoreUsers", new Criteria(new Column("BusinessStoreUsers", "BS_USER_ID"), (Object)userIdList.toArray(), 8));
            }
            this.logger.log(Level.INFO, "addOrUpdateStoreUserForDevice completed");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in handling afw registration for already enrolled devices ", ex);
        }
    }
    
    public boolean isStoreAccountAddedForDevice(final Long resourceID, final Long bsId) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BSUsersToManagedDevices"));
            final Join businessStoreUserJoin = new Join("BSUsersToManagedDevices", "BusinessStoreUsers", new String[] { "BS_USER_ID" }, new String[] { "BS_USER_ID" }, 2);
            sQuery.addJoin(businessStoreUserJoin);
            sQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "BS_USER_TO_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("BusinessStoreUsers", "BS_USER_ID"));
            final Criteria deviceCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resourceID, 0);
            final Criteria storeCriteria = new Criteria(Column.getColumn("BusinessStoreUsers", "BUSINESSSTORE_ID"), (Object)bsId, 0);
            final Criteria accStatusCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"), (Object)1, 0);
            sQuery.setCriteria(deviceCriteria.and(storeCriteria).and(accStatusCriteria));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            this.logger.log(Level.INFO, "isStoreAccountAddedForDevice for resourceId={0};bsId={1}is {2}", new Object[] { resourceID, bsId, !dO.isEmpty() });
            return !dO.isEmpty();
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception when checking bsuser for a device ", (Throwable)ex);
            return false;
        }
    }
}
