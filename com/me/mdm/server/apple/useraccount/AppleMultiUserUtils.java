package com.me.mdm.server.apple.useraccount;

import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.logging.Level;
import com.me.mdm.server.apple.objects.AppleUserAccount;
import java.util.List;
import java.util.logging.Logger;

public class AppleMultiUserUtils
{
    public static Logger logger;
    
    public static void populateAppleUserAccountsForResource(final Long resourceID, final List<AppleUserAccount> userAccountList) {
        if (userAccountList.size() == 0) {
            AppleMultiUserUtils.logger.log(Level.WARNING, "No User account present in device , it should be the case in multiuser , so not deleting accounts");
            return;
        }
        Collections.sort(userAccountList);
        final Comparator<AppleUserAccount> byLoggedIn = Comparator.comparing(obj -> new Boolean(obj.getIsLoggedIn()));
        userAccountList.sort(byLoggedIn);
        final DataObject dataObject = getDeviceUserAccountDO(resourceID);
        if (dataObject != null) {
            try {
                int order = 1;
                final List<Long> deviceRecentuserList = new ArrayList<Long>();
                final List currentUserList = MDMDBUtil.getColumnValuesAsList(dataObject.getRows("MdDeviceRecentUsersInfo"), "DEVICE_RECENT_USER_ID");
                for (final AppleUserAccount account : userAccountList) {
                    final Long deviceRecentUserId = getDeviceRecentUserId(dataObject, account);
                    Row recentUserInfoRow = null;
                    Row recentUserInfpExtn = null;
                    Row accountRow = null;
                    if (deviceRecentUserId != null) {
                        final Criteria deviceRecentCriteria = new Criteria(new Column("MdDeviceRecentUsersInfo", "DEVICE_RECENT_USER_ID"), (Object)deviceRecentUserId, 0);
                        recentUserInfoRow = dataObject.getRow("MdDeviceRecentUsersInfo", deviceRecentCriteria);
                        recentUserInfpExtn = dataObject.getRow("MdDeviceRecentUsersInfoExtn", recentUserInfoRow);
                        accountRow = dataObject.getRow("MDDeviceUserAccounts", recentUserInfoRow);
                        deviceRecentuserList.add(deviceRecentUserId);
                    }
                    else {
                        recentUserInfoRow = new Row("MdDeviceRecentUsersInfo");
                        recentUserInfpExtn = new Row("MdDeviceRecentUsersInfoExtn");
                        recentUserInfpExtn.set("LOGIN_TIME", (Object)(-1));
                        accountRow = new Row("MDDeviceUserAccounts");
                    }
                    recentUserInfoRow.set("RESOURCE_ID", (Object)resourceID);
                    recentUserInfoRow.set("ORDER", (Object)(order++));
                    recentUserInfoRow.set("USER_ID", (Object)null);
                    recentUserInfoRow.set("USER_MANAGEMENT_TYPE", (Object)2);
                    recentUserInfpExtn.set("DEVICE_RECENT_USER_ID", recentUserInfoRow.get("DEVICE_RECENT_USER_ID"));
                    recentUserInfpExtn.set("LOGON_USER_NAME", (Object)account.getUserName());
                    recentUserInfpExtn.set("LOGON_USER_DISPLAY_NAME", (Object)account.getFullName());
                    accountRow.set("DEVICE_RECENT_USER_ID", recentUserInfoRow.get("DEVICE_RECENT_USER_ID"));
                    accountRow.set("USER_GUID", (Object)account.getUserGUID());
                    accountRow.set("IS_LOGGED_IN", (Object)account.getIsLoggedIn());
                    accountRow.set("IS_MOBILE_ACCOUNT", (Object)account.getMobileAccount());
                    accountRow.set("DATA_SYNCED", (Object)account.getHasDataToSync());
                    if (!MDMStringUtils.isEmpty(account.getDataQuota())) {
                        accountRow.set("DATA_QUOTA", (Object)account.getDataQuota());
                    }
                    if (!MDMStringUtils.isEmpty(account.getDataUsed())) {
                        accountRow.set("DATA_USED", (Object)account.getDataUsed());
                    }
                    accountRow.set("HAS_SECURE_TOKEN", (Object)account.isHasSecureToken());
                    if (deviceRecentUserId != null) {
                        dataObject.updateRow(recentUserInfoRow);
                        dataObject.updateRow(recentUserInfpExtn);
                        dataObject.updateRow(accountRow);
                    }
                    else {
                        dataObject.addRow(recentUserInfoRow);
                        dataObject.addRow(recentUserInfpExtn);
                        dataObject.addRow(accountRow);
                    }
                }
                currentUserList.removeAll(deviceRecentuserList);
                if (currentUserList.size() > 0) {
                    AppleMultiUserUtils.logger.log(Level.INFO, "Device Recent User list:{0}", new Object[] { currentUserList });
                    dataObject.deleteRows("MdDeviceRecentUsersInfo", new Criteria(new Column("MdDeviceRecentUsersInfo", "DEVICE_RECENT_USER_ID"), (Object)currentUserList.toArray(), 8));
                }
                MDMUtil.getPersistence().update(dataObject);
            }
            catch (final DataAccessException e) {
                AppleMultiUserUtils.logger.log(Level.SEVERE, "Exception in addOrUpdateAppleUserAccount", (Throwable)e);
            }
        }
    }
    
    private static Long getDeviceRecentUserId(final DataObject dataObject, final AppleUserAccount userAccount) throws DataAccessException {
        Long deviceRecentUserId = null;
        if (MDMStringUtils.isEmpty(userAccount.getUserGUID())) {
            final Criteria userNameCriteria = new Criteria(new Column("MdDeviceRecentUsersInfoExtn", "LOGON_USER_NAME"), (Object)userAccount.getUserName(), 0);
            final Row row = dataObject.getRow("MdDeviceRecentUsersInfoExtn", userNameCriteria);
            if (row != null) {
                deviceRecentUserId = (Long)row.get("DEVICE_RECENT_USER_ID");
            }
        }
        else {
            final Criteria guidCriteria = new Criteria(new Column("MDDeviceUserAccounts", "USER_GUID"), (Object)userAccount.getUserGUID(), 0);
            final Row row = dataObject.getRow("MDDeviceUserAccounts", guidCriteria);
            if (row != null) {
                deviceRecentUserId = (Long)row.get("DEVICE_RECENT_USER_ID");
            }
        }
        return deviceRecentUserId;
    }
    
    private static DataObject getDeviceUserAccountDO(final Long resourceID) {
        final Criteria resourceCriteria = new Criteria(new Column("MdDeviceRecentUsersInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        return getDeviceUserAccountDO(resourceCriteria);
    }
    
    public static DataObject getDeviceUserAccountDO(final Criteria criteria) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceRecentUsersInfo"));
        query.addJoin(new Join("MdDeviceRecentUsersInfo", "MdDeviceRecentUsersInfoExtn", new String[] { "DEVICE_RECENT_USER_ID" }, new String[] { "DEVICE_RECENT_USER_ID" }, 1));
        query.addJoin(new Join("MdDeviceRecentUsersInfo", "MDDeviceUserAccounts", new String[] { "DEVICE_RECENT_USER_ID" }, new String[] { "DEVICE_RECENT_USER_ID" }, 1));
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("MdDeviceRecentUsersInfo", "*"));
        query.addSelectColumn(new Column("MdDeviceRecentUsersInfoExtn", "*"));
        query.addSelectColumn(new Column("MDDeviceUserAccounts", "*"));
        try {
            final DataObject DO = MDMUtil.getPersistence().get(query);
            return DO;
        }
        catch (final DataAccessException e) {
            AppleMultiUserUtils.logger.log(Level.SEVERE, "Exception in getDeviceUserAccountDO", (Throwable)e);
            return null;
        }
    }
    
    public static String getLoggedInUserName(final Long resourceId) {
        try {
            final Criteria resourceCriteria = new Criteria(new Column("MdDeviceRecentUsersInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria isLoggedIn = new Criteria(new Column("MDDeviceUserAccounts", "IS_LOGGED_IN"), (Object)true, 0);
            final DataObject dataObject = getDeviceUserAccountDO(resourceCriteria.and(isLoggedIn));
            if (!dataObject.isEmpty()) {
                final Row userRow = dataObject.getRow("MdDeviceRecentUsersInfoExtn");
                return (String)userRow.get("LOGON_USER_NAME");
            }
        }
        catch (final Exception e) {
            AppleMultiUserUtils.logger.log(Level.SEVERE, "Exception in getting logged in user returning null", e);
        }
        return null;
    }
    
    public static void addNewUserOnLoginEvent(final Long resourceID, final JSONArray jsonArray) throws DataAccessException, JSONException {
        final DataObject dataObject = getDeviceUserAccountDO(resourceID);
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject mddevcieRecentUsersInfoJSON = jsonArray.getJSONObject(i);
            final String userName = mddevcieRecentUsersInfoJSON.optString("LOGON_USER_NAME");
            int offset = dataObject.size("MdDeviceRecentUsersInfo");
            if (!MDMStringUtils.isEmpty(userName)) {
                final Criteria userNameCriteria = new Criteria(new Column("MdDeviceRecentUsersInfoExtn", "LOGON_USER_NAME"), (Object)userName, 0);
                Row deviceRecentUsersInfoExtn = dataObject.getRow("MdDeviceRecentUsersInfoExtn", userNameCriteria);
                Row deviceRecentUsersInfo = dataObject.getRow("MdDeviceRecentUsersInfo", deviceRecentUsersInfoExtn);
                Row accountRow = dataObject.getRow("MDDeviceUserAccounts", deviceRecentUsersInfo);
                if (!dataObject.isEmpty()) {
                    final Criteria criteria = new Criteria(new Column("MDDeviceUserAccounts", "IS_LOGGED_IN"), (Object)true, 0);
                    final Iterator iterator = dataObject.getRows("MDDeviceUserAccounts", criteria);
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        row.set("IS_LOGGED_IN", (Object)false);
                        dataObject.updateRow(row);
                    }
                }
                boolean isAdded = false;
                if (deviceRecentUsersInfoExtn == null) {
                    deviceRecentUsersInfo = new Row("MdDeviceRecentUsersInfo");
                    deviceRecentUsersInfoExtn = new Row("MdDeviceRecentUsersInfoExtn");
                    deviceRecentUsersInfoExtn.set("DEVICE_RECENT_USER_ID", deviceRecentUsersInfo.get("DEVICE_RECENT_USER_ID"));
                    accountRow = new Row("MDDeviceUserAccounts");
                    accountRow.set("DEVICE_RECENT_USER_ID", deviceRecentUsersInfo.get("DEVICE_RECENT_USER_ID"));
                    isAdded = true;
                }
                else {
                    offset = (int)deviceRecentUsersInfo.get("ORDER") - 1;
                }
                incrementOrderOfRecentUser(dataObject, offset);
                deviceRecentUsersInfo.set("RESOURCE_ID", (Object)resourceID);
                deviceRecentUsersInfo.set("USER_ID", (Object)(mddevcieRecentUsersInfoJSON.has("USER_ID") ? Long.valueOf(mddevcieRecentUsersInfoJSON.getLong("USER_ID")) : null));
                deviceRecentUsersInfo.set("ORDER", (Object)mddevcieRecentUsersInfoJSON.optInt("ORDER", 1));
                deviceRecentUsersInfo.set("USER_MANAGEMENT_TYPE", (Object)mddevcieRecentUsersInfoJSON.optInt("USER_MANAGEMENT_TYPE", 2));
                deviceRecentUsersInfoExtn.set("LOGON_USER_DISPLAY_NAME", (Object)mddevcieRecentUsersInfoJSON.optString("LOGON_USER_DISPLAY_NAME"));
                deviceRecentUsersInfoExtn.set("LOGON_USER_NAME", (Object)mddevcieRecentUsersInfoJSON.optString("LOGON_USER_NAME"));
                deviceRecentUsersInfoExtn.set("LOGIN_TIME", (Object)mddevcieRecentUsersInfoJSON.optLong("LOGIN_TIME"));
                accountRow.set("IS_LOGGED_IN", (Object)true);
                accountRow.set("DATA_SYNCED", (Object)false);
                if (isAdded) {
                    dataObject.addRow(deviceRecentUsersInfo);
                    dataObject.addRow(deviceRecentUsersInfoExtn);
                    dataObject.addRow(accountRow);
                }
                else {
                    dataObject.updateRow(deviceRecentUsersInfo);
                    dataObject.updateRow(deviceRecentUsersInfoExtn);
                    dataObject.updateRow(accountRow);
                }
            }
        }
        MDMUtil.getPersistence().update(dataObject);
    }
    
    private static void incrementOrderOfRecentUser(final DataObject dataObject, final int offset) throws DataAccessException {
        if (!dataObject.isEmpty()) {
            dataObject.sortRows("MdDeviceRecentUsersInfo", new SortColumn[] { new SortColumn(new Column("MdDeviceRecentUsersInfo", "ORDER"), true) });
            final Iterator iterator = dataObject.getRows("MdDeviceRecentUsersInfo");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final int order = (int)row.get("ORDER");
                if (order > offset) {
                    break;
                }
                row.set("ORDER", (Object)(order + 1));
                dataObject.updateRow(row);
            }
        }
    }
    
    public static boolean isSharediPadEnrollmentRequest(final Long erid) {
        boolean isSharedIPad = false;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
        sQuery.addJoin(new Join("EnrollmentTemplate", "EnrollmentTemplateToRequest", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addSelectColumn(new Column("DEPEnrollmentTemplate", "TEMPLATE_ID"));
        sQuery.addSelectColumn(new Column("DEPEnrollmentTemplate", "IS_MULTIUSER"));
        final Criteria eridCriteria = new Criteria(new Column("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
        sQuery.setCriteria(eridCriteria);
        try {
            final DataObject Do = MDMUtil.getPersistence().get(sQuery);
            if (!Do.isEmpty() && Do.containsTable("DEPEnrollmentTemplate")) {
                final Row row = Do.getFirstRow("DEPEnrollmentTemplate");
                isSharedIPad = (boolean)row.get("IS_MULTIUSER");
                DEPAdminEnrollmentHandler.logger.log(Level.INFO, "SharedIPadStatus erid: " + erid + ", status: " + isSharedIPad);
            }
        }
        catch (final DataAccessException e) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in isSharediPadEnrollmentRequest", (Throwable)e);
        }
        return isSharedIPad;
    }
    
    public static JSONObject getSharedDeviceConfigurationForDevice(final Long resourceID) {
        final Criteria resourceIDCri = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        final DataObject dataObject = getMultiUserDeviceDO(resourceIDCri);
        if (dataObject != null && !dataObject.isEmpty()) {
            try {
                if (dataObject.containsTable("AppleSharedDeviceConfigTemplate")) {
                    final Row configRow = dataObject.getFirstRow("AppleSharedDeviceConfigTemplate");
                    final JSONObject obj = MDMDBUtil.rowToJSON(configRow);
                    return obj;
                }
            }
            catch (final DataAccessException e) {
                DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getSharedDeviceConfigurationForDevice", (Throwable)e);
            }
        }
        DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "No Shared iPad Config Found for device , something wrong: {0}", resourceID);
        return null;
    }
    
    public static DataObject getSharedIpadDO(final Long resourceID) {
        final Criteria resourceIDCri = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria hasSharedIPadConfig = new Criteria(new Column("AppleSharedDeviceConfigTemplate", "TEMPLATE_ID"), (Object)null, 1);
        final DataObject dataObject = getMultiUserDeviceDO(resourceIDCri.and(hasSharedIPadConfig));
        return dataObject;
    }
    
    public static boolean isSharedIPadConfiguration(final DataObject dataObject) {
        if (dataObject != null && !dataObject.isEmpty()) {
            Row osVersionRow = null;
            final Criteria modelType = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 2 }, 8);
            final Criteria isMultiUserDevice = new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)true, 0);
            try {
                final Row modelRow = dataObject.getRow("MdModelInfo", modelType);
                if (modelRow != null) {
                    final Criteria modelCriteria = new Criteria(new Column("MdDeviceInfo", "MODEL_ID"), modelRow.get("MODEL_ID"), 0);
                    osVersionRow = dataObject.getRow("MdDeviceInfo", modelCriteria.and(isMultiUserDevice));
                }
                if (osVersionRow == null) {
                    return false;
                }
            }
            catch (final DataAccessException e) {
                DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in isSharedIPadConfiguration", (Throwable)e);
            }
            final String osVersion = (String)osVersionRow.get("OS_VERSION");
            return new VersionChecker().isGreater(osVersion, "13.3");
        }
        return false;
    }
    
    private static DataObject getMultiUserDeviceDO(final Criteria criteria) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "AppleSharedDeviceConfigTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(criteria);
        try {
            return MDMUtil.getPersistence().get(sQuery);
        }
        catch (final DataAccessException e) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getMultiUserDeviceDO", (Throwable)e);
            return null;
        }
    }
    
    static {
        AppleMultiUserUtils.logger = Logger.getLogger("MDMLogger");
    }
}
