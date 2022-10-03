package com.me.mdm.server.metracker;

import org.json.JSONArray;
import org.json.JSONException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.CaseExpression;
import com.me.mdm.server.tracker.MDMTrackerUtil;
import java.net.URLEncoder;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsAzureADEnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsLaptopEnrollmentHandler;
import com.me.mdm.core.enrollment.GSuiteChromeDeviceEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidZTEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidQREnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsWICDEnrollmentHandler;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.mdm.core.enrollment.KNOXAdminEnrollmentHandler;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidAdminEnrollmentHandler;
import java.sql.SQLException;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.Row;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.me.mdm.server.util.CalendarUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class MEMDMTrackerUtil
{
    private static String sourceClass;
    private static Logger logger;
    
    public static Properties getNATConfiguration() {
        Properties natProp = null;
        try {
            natProp = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            final String natAddress = natProp.getProperty("NAT_ADDRESS", "");
            if (natAddress.isEmpty()) {
                natProp.setProperty("IS_NAT_CONFIGURED", "false");
            }
            else {
                natProp.setProperty("IS_NAT_CONFIGURED", "true");
                final boolean isNAT = SYMClientUtil.isIPAddress(natAddress);
                natProp.setProperty("IS_NAT_IP", String.valueOf(isNAT));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getNATConfiguration", "Exception : ", (Throwable)e);
        }
        return natProp;
    }
    
    public static String isMailServerConfigured() {
        final boolean ismailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
        if (ismailServerConfigured) {
            return "true";
        }
        return "false";
    }
    
    public static String getProxySettings() {
        String proxyDefined = "false";
        try {
            proxyDefined = MDMUtil.getSyMParameter("proxy_defined");
            if (proxyDefined != null) {
                return proxyDefined;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getProxySettings", "Exception : ", (Throwable)e);
        }
        return proxyDefined;
    }
    
    public static Boolean isGeoTrackingEnabled() {
        boolean isGeoTrackingEnabled = false;
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final Boolean isGeoLocationEnable = (Boolean)DBUtil.getValueFromDB("LocationSettings", "CUSTOMER_ID", (Object)customerID, "IS_LOCATION_TRACKING");
                if (isGeoLocationEnable) {
                    isGeoTrackingEnabled = true;
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "isGeoTrackingEnabled", "Exception : ", (Throwable)ex);
            }
        }
        return isGeoTrackingEnabled;
    }
    
    public static int getGeoTrackingStatus() {
        int trackingStatus = -1;
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                trackingStatus = LocationSettingsDataHandler.getInstance().getLocationTrackingStatus(customerID);
            }
            catch (final Exception e) {
                SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getGeoTrackingStatus", "Exception : ", (Throwable)e);
            }
        }
        return trackingStatus;
    }
    
    public static JSONObject geDevicesByLostModeStatus() {
        final JSONObject lostModeDevices = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LostModeTrackInfo"));
            selectQuery.addSelectColumn(Column.getColumn("LostModeTrackInfo", "*"));
            final DataObject lostModeDeviceDO = SyMUtil.getPersistence().get(selectQuery);
            if (lostModeDeviceDO != null && !lostModeDeviceDO.isEmpty()) {
                Iterator lostModeDeviceIterator = lostModeDeviceDO.getRows("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)1, 0));
                lostModeDevices.put("Lost_Mode_Initiated_Devices", getIteratorSize(lostModeDeviceIterator));
                lostModeDeviceIterator = lostModeDeviceDO.getRows("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)2, 0));
                lostModeDevices.put("Lost_Mode_Activated_Devices", getIteratorSize(lostModeDeviceIterator));
                lostModeDeviceIterator = lostModeDeviceDO.getRows("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)3, 0));
                lostModeDevices.put("Lost_Mode_Activation_Failed_Devices", getIteratorSize(lostModeDeviceIterator));
                lostModeDeviceIterator = lostModeDeviceDO.getRows("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)4, 0));
                lostModeDevices.put("Lost_Mode_Initiated_Devices", getIteratorSize(lostModeDeviceIterator));
                lostModeDeviceIterator = lostModeDeviceDO.getRows("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)5, 0));
                lostModeDevices.put("Lost_Mode_DeActivated_Devices", getIteratorSize(lostModeDeviceIterator));
                lostModeDeviceIterator = lostModeDeviceDO.getRows("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)6, 0));
                lostModeDevices.put("Lost_Mode_DeActivation_Failed_Devices", getIteratorSize(lostModeDeviceIterator));
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getLostModeDeviceCounts", "Exception : ", (Throwable)ex);
        }
        return lostModeDevices;
    }
    
    public static String getLocationHistoryDataStartDate() {
        String date = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.setRange(new Range(1, 1));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), true);
            selectQuery.addSortColumn(sortColumn);
            final DataObject locDO = MDMUtil.getPersistence().get(selectQuery);
            if (!locDO.isEmpty()) {
                final Row row = locDO.getFirstRow("MdDeviceLocationDetails");
                final Long addedTime = (Long)row.get("ADDED_TIME");
                date = CalendarUtil.getInstance().getDateAsString(addedTime, new SimpleDateFormat("dd/MM/yyyy"));
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getLocationHistoryDataStartDate", "Exception : ", (Throwable)ex);
        }
        return date;
    }
    
    public static int getLocationHistoryDataCount() {
        int count = -1;
        try {
            count = DBUtil.getRecordCount("MdDeviceLocationDetails", "LOCATION_DETAIL_ID", (Criteria)null);
        }
        catch (final Exception ex) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "locationHistoryDataCount", "Exception : ", (Throwable)ex);
        }
        return count;
    }
    
    private static int getIteratorSize(final Iterator iterator) {
        int size = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++size;
        }
        return size;
    }
    
    public static Boolean isGeoTrackingDevicesIncluded() {
        Boolean isDevicesIncluded = false;
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final Boolean isResourceIncluded = LocationSettingsDataHandler.getInstance().isResourceIncluded(customerID);
                if (isResourceIncluded) {
                    isDevicesIncluded = true;
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "isGeoTrackingDevicesIncluded", "Exception : ", (Throwable)ex);
            }
        }
        return isDevicesIncluded;
    }
    
    public static String getGeoLocationMapType() {
        String mapTypeStr = "ZOHO";
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final int mapType = LocationSettingsDataHandler.getInstance().getMapType(customerID);
                if (mapType == 1) {
                    mapTypeStr = "GOOGLE";
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getGeoLocationMapType", "Exception : ", (Throwable)ex);
            }
        }
        return mapTypeStr;
    }
    
    public static Boolean isGoogleMapwithAuthenticationKey() {
        Boolean isGoogleMapwithKey = false;
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final String googleMapAPIKey = LocationSettingsDataHandler.getInstance().getGoogleMapAPIKey(customerID);
                if (googleMapAPIKey != null && googleMapAPIKey != "") {
                    isGoogleMapwithKey = true;
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "isGoogleMapwithAuthenticationKey", "Exception : ", (Throwable)ex);
            }
        }
        return isGoogleMapwithKey;
    }
    
    public static String getForwardingServerConfigured() {
        try {
            final String fsconfigured = MDMUtil.getSyMParameter("forwarding_server_config");
            if (fsconfigured != null) {
                return fsconfigured;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addForwardingServerConfigured", "Exception : ", (Throwable)e);
        }
        return "false";
    }
    
    public static String addAndroidEnrollementRequestFailedCount() {
        Criteria androidPlatformcriteria = null;
        try {
            androidPlatformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)2, 0);
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
        return String.valueOf(getEnrollmentRequestFailedCount(androidPlatformcriteria));
    }
    
    public static String addWindowsEnrollementRequestFailedCount() {
        Criteria windowsPlatformcriteria = null;
        try {
            windowsPlatformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)3, 0);
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
        return String.valueOf(getEnrollmentRequestFailedCount(windowsPlatformcriteria));
    }
    
    public static String addIOSEnrollementRequestFailedCount() {
        Criteria iosPlatformcriteria = null;
        try {
            iosPlatformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)1, 0);
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
        return String.valueOf(getEnrollmentRequestFailedCount(iosPlatformcriteria));
    }
    
    public static String addTotalEnrollementRequestFailedCount() {
        final Criteria Platformcriteria = null;
        return String.valueOf(getEnrollmentRequestFailedCount(Platformcriteria));
    }
    
    public static String addErrorCodeDetails() {
        final JSONObject errorCodeDetails = getErrorCodeDetails();
        return errorCodeDetails.toString();
    }
    
    private static int getEnrollmentRequestFailedCount(final Criteria platformCriteria) {
        final int enrollmentReqFailedCount = 0;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        final Criteria errorCodecriteria = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0);
        if (platformCriteria != null) {
            query.setCriteria(errorCodecriteria.and(platformCriteria));
        }
        else {
            query.setCriteria(errorCodecriteria);
        }
        query.addSelectColumn(new Column((String)null, "*"));
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                return resultDO.size("DeviceEnrollmentRequest");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getEnrollmentRequestFailedCount", "Caught exception while retrieving AD Domains from DB", (Throwable)ex);
        }
        return enrollmentReqFailedCount;
    }
    
    private static JSONObject getErrorCodeDetails() {
        final JSONObject erroCodeDetails = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollReqToErrCode", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            final Criteria errorCodecriteria = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0);
            sQuery.setCriteria(errorCodecriteria);
            final Column enrollmentIDCol = new Column("DeviceEnrollReqToErrCode", "ENROLLMENT_REQUEST_ID").distinct().count();
            enrollmentIDCol.setColumnAlias("ENROLLMENT_REQUEST_ID");
            final Column errorCodeCol = new Column("DeviceEnrollReqToErrCode", "ERROR_CODE");
            sQuery.addGroupByColumn(new Column("DeviceEnrollReqToErrCode", "ERROR_CODE"));
            sQuery.addSelectColumn(enrollmentIDCol);
            sQuery.addSelectColumn(errorCodeCol);
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Object enrollmentIDCount = ds.getValue("ENROLLMENT_REQUEST_ID");
                final Object errorCode = ds.getValue("ERROR_CODE");
                if (enrollmentIDCount != null && errorCode != null) {
                    erroCodeDetails.put(errorCode.toString(), enrollmentIDCount);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addErrorCodeDetails", "Caught exception while retrieving error code details", (Throwable)e);
        }
        return erroCodeDetails;
    }
    
    public static String getDEPSettingStatus() {
        String depStatus = "NA";
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final Integer status = (Integer)DBUtil.getValueFromDB("DEPEnrollmentStatus", "CUSTOMER_ID", (Object)customerID, "DEP_STAUS");
                if (status != null && status == 2) {
                    depStatus = "DEP Token Uploaded";
                }
                else if (status != null && status == 3) {
                    depStatus = "DEP Profile Created";
                }
                else if (status != null && status == 1) {
                    depStatus = "DEP Certificate Downloaded";
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addDEPSettings", "Exception : ", (Throwable)e);
        }
        return depStatus;
    }
    
    private static Boolean addVppAppAssignment() {
        Boolean isVppConfigured = false;
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            isVppConfigured = VPPTokenDataHandler.getInstance().isVppTokenConfigured(customerID);
        }
        return isVppConfigured;
    }
    
    private static int addVppManagedDistributionAppCount() {
        int appRepCount = 0;
        try {
            final Criteria cManagedDistribution = new Criteria(new Column("MdLicense", "LICENSED_TYPE"), (Object)2, 0);
            appRepCount = DBUtil.getRecordCount("MdLicense", "LICENSE_ID", cManagedDistribution);
            return appRepCount;
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addVppManagedDistributionAppCount", "Exception : ", (Throwable)e);
            return appRepCount;
        }
    }
    
    private static int getVppfreeAppsCount() {
        int freeAppsCount = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria freeAppCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)0, 0);
            selectQuery.setCriteria(freeAppCri);
            freeAppsCount = DBUtil.getRecordCount(selectQuery, "MdAppGroupDetails", "APP_GROUP_ID");
        }
        catch (final Exception exp) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred getVppfreeAppsCount()", exp);
        }
        return freeAppsCount;
    }
    
    private static String getVppLicenseDistributionType() {
        String licenseType = "";
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final Integer typeOfAssignment = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(customerID);
            if (typeOfAssignment == 2) {
                licenseType = "serialNumber";
            }
            else if (typeOfAssignment == 1) {
                licenseType = "userBased";
            }
        }
        return licenseType;
    }
    
    private static String isVppSkippedOnAddApp() {
        String isVppSkippedOnAddApp = "false";
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                isVppSkippedOnAddApp = CustomerParamsHandler.getInstance().getParameterValue("isVppSkippedWhenAddingApp", (long)customerID);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred in isVppSkippedOnAddApp", ex);
        }
        return isVppSkippedOnAddApp;
    }
    
    private static String getVPPTokenMisusedCount() {
        String tokenMisUseCount = null;
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                tokenMisUseCount = CustomerParamsHandler.getInstance().getParameterValue("misUsedTokenSyncCount", (long)customerID);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred in getVPPTokenMisusedCount", ex);
        }
        return tokenMisUseCount;
    }
    
    private static String getVppPageSource() {
        String vppPageSource = "";
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                vppPageSource = CustomerParamsHandler.getInstance().getParameterValue("vppPageSource", (long)customerID);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred in getVppPageSource", ex);
        }
        return vppPageSource;
    }
    
    private static String appCoutWhenVppSkippedOnAddApp() {
        String iosAppsNotPurchasedFromPortal = "";
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                iosAppsNotPurchasedFromPortal = CustomerParamsHandler.getInstance().getParameterValue("iosAppsNotPurchasedFromPortal", (long)customerID);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred in isVppSkippedOnAddApp", ex);
        }
        return iosAppsNotPurchasedFromPortal;
    }
    
    private static JSONObject getVppTokenDetails() {
        int typeOfAssignment = 0;
        Long vppTokenAddedTime = -1L;
        Boolean isVppConfigurd = false;
        Boolean isLocationTokenUsed = false;
        final JSONObject vppTokenDetails = new JSONObject();
        final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final DataObject dataObject = cachedPersistence.get("MdVPPTokenDetails", (Criteria)null);
                if (!dataObject.isEmpty()) {
                    final Criteria cCustomerId = new Criteria(new Column("MdVPPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
                    final Row settingsRow = dataObject.getRow("MdVPPTokenDetails", cCustomerId);
                    if (settingsRow != null) {
                        typeOfAssignment = (int)settingsRow.get("LICENSE_ASSIGN_TYPE");
                        vppTokenAddedTime = (Long)settingsRow.get("VPP_TOKEN_ADDED_TIME");
                        final String locationId = (String)settingsRow.get("LOCATION_ID");
                        isVppConfigurd = true;
                        if (locationId != null) {
                            isLocationTokenUsed = true;
                        }
                    }
                }
            }
            vppTokenDetails.put("typeOfAssignment", typeOfAssignment);
            vppTokenDetails.put("vppTokenAddedTime", (Object)vppTokenAddedTime);
            vppTokenDetails.put("isVppConfigurd", (Object)isVppConfigurd);
            vppTokenDetails.put("isLocationTokenUsed", (Object)isLocationTokenUsed);
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, " Exception in getTheVppTokenDetails", e);
        }
        return vppTokenDetails;
    }
    
    private static JSONObject getNoOfUserAndDeviceBasedVppApp() throws SQLException {
        final JSONObject getCountOfVppAppType = new JSONObject();
        DMDataSetWrapper dataSetForVppAppCount = null;
        try {
            getCountOfVppAppType.put("user", 0);
            getCountOfVppAppType.put("device", 0);
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDAppAssignableDetails"));
                sQuery.addJoin(new Join("MDAppAssignableDetails", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
                sQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
                final Criteria vppAppCriteria = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)2, 0);
                sQuery.setCriteria(vppAppCriteria);
                final Column appAssignableCount = Column.getColumn("MDAppAssignableDetails", "APP_ASSIGNMENT_ID").count();
                appAssignableCount.setColumnAlias("COUNT_OF_APP_TYPE");
                sQuery.addSelectColumn(appAssignableCount);
                sQuery.addSelectColumn(Column.getColumn("MDAppAssignableDetails", "APP_ASSIGNABLE_TYPE"));
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("MDAppAssignableDetails", "APP_ASSIGNABLE_TYPE");
                list.add(groupByCol);
                final GroupByClause appGroupBy = new GroupByClause(list);
                sQuery.setGroupByClause(appGroupBy);
                dataSetForVppAppCount = DMDataSetWrapper.executeQuery((Object)sQuery);
                if (dataSetForVppAppCount != null) {
                    while (dataSetForVppAppCount.next()) {
                        final int appAssignmentType = (int)dataSetForVppAppCount.getValue("APP_ASSIGNABLE_TYPE");
                        final int count = (int)dataSetForVppAppCount.getValue("COUNT_OF_APP_TYPE");
                        final String key = (appAssignmentType == 1) ? "user" : "device";
                        getCountOfVppAppType.put(key, count);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred in getNoOfUserAndDeviceBasedVppApp ", ex);
        }
        return getCountOfVppAppType;
    }
    
    private static int isMEMDMAppInVpp() {
        int meMDMinVppStatus = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            final Criteria meMDMAppCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
            sQuery.setCriteria(meMDMAppCriteria);
            sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "*"));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty()) {
                if (dO.size("MdStoreAssetToAppGroupRel") > 0) {
                    meMDMinVppStatus = 1;
                }
                else {
                    meMDMinVppStatus = 2;
                }
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in isMEMDMAppInVpp {0}", ex);
        }
        return meMDMinVppStatus;
    }
    
    public static JSONObject getVppDetails() {
        final JSONObject vppDetails = new JSONObject();
        try {
            final JSONObject vppTokenDetails = getVppTokenDetails();
            final Boolean isVppConfigure = (Boolean)vppTokenDetails.get("isVppConfigurd");
            vppDetails.put("iOS_VPP_App_Assignment_Configured", (Object)String.valueOf(isVppConfigure));
            vppDetails.put("iOS_VPP_ManagedDistribution_App_Count", (Object)String.valueOf(addVppManagedDistributionAppCount()));
            vppDetails.put("iOS_VPP_Free_App_Count", (Object)String.valueOf(getVppfreeAppsCount()));
            vppDetails.put("iOS_VPP_license_distribution_type", (int)vppTokenDetails.get("typeOfAssignment"));
            final JSONObject getCountOfVppAppType = getNoOfUserAndDeviceBasedVppApp();
            vppDetails.put("vppUserBasedApp", getCountOfVppAppType.get("user"));
            vppDetails.put("vppDeviceBasedApp", getCountOfVppAppType.get("device"));
            vppDetails.put("vppTokenAddedTime", (Object)vppTokenDetails.get("vppTokenAddedTime"));
            vppDetails.put("iOSMEMDMAppInVpp", isMEMDMAppInVpp());
            final String vppPageSource = getVppPageSource();
            if (vppPageSource != null) {
                vppDetails.put("vppPageSource", (Object)vppPageSource);
            }
            if (!isVppConfigure) {
                final String isVppSkippedOnAddApp = isVppSkippedOnAddApp();
                vppDetails.put("isVppSkipped", (Object)isVppSkippedOnAddApp);
                vppDetails.put("appCountOnSkip", (Object)appCoutWhenVppSkippedOnAddApp());
            }
            final String vppTokenMisuseCount = getVPPTokenMisusedCount();
            if (vppTokenMisuseCount != null) {
                vppDetails.put("vppTokenMisUseCount", (Object)vppTokenMisuseCount);
            }
            vppDetails.put("vppIsLocationToken", vppTokenDetails.get("isLocationTokenUsed"));
        }
        catch (final Exception e) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addVppDetails", "Exception : ", (Throwable)e);
        }
        return vppDetails;
    }
    
    public static JSONObject getAdminEnrollmentData() {
        final JSONObject adminEnrollment = new JSONObject();
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final int adminCount = DBUtil.getRecordCount("AndroidAdminDeviceDetails", "LOGIN_ID", (Criteria)null);
                adminEnrollment.put("Admin_Enrollment_NFC_Used", (Object)DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Admin_Enrollment_NFC_Used", "PARAM_VALUE"));
                adminEnrollment.put("Admin_Enrollment_NFC_Admin_Count", adminCount);
                AdminEnrollmentHandler handler = new AndroidAdminEnrollmentHandler();
                adminEnrollment.put("Admin_Enrollment_NFC_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_NFC_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new AppleConfiguratorEnrollmentHandler();
                adminEnrollment.put("Admin_Enrollment_APPLECONF_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_APPLECONF_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new KNOXAdminEnrollmentHandler();
                adminEnrollment.put("Admin_Enrollment_KNOX_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_KNOX_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new DEPAdminEnrollmentHandler();
                adminEnrollment.put("Admin_Enrollment_DEP_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_DEP_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_DEP_Unenrolled_Count", handler.getUnEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_DEP_UnenrolledAndUnassigned_Count", handler.getUnEnrolledAndUnassignedDeviceCount(customerID));
                handler = new WindowsWICDEnrollmentHandler();
                Object clickVal = DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Admin_Enrollment_WICD_Tool_Download_Clicked", "PARAM_VALUE");
                adminEnrollment.put("Admin_Enrollment_WICD_Tool_Download_Clicked", (Object)((clickVal == null) ? Boolean.FALSE : Boolean.valueOf(clickVal.toString())));
                clickVal = DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Admin_Enrollment_WICD_Tool_Download_Success", "PARAM_VALUE");
                adminEnrollment.put("Admin_Enrollment_WICD_Tool_Download_Success", (Object)((clickVal == null) ? Boolean.FALSE : Boolean.valueOf(clickVal.toString())));
                adminEnrollment.put("Admin_Enrollment_WICD_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_WICD_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new AndroidQREnrollmentHandler();
                adminEnrollment.put("Admin_Enrollment_QR_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_QR_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new AndroidZTEnrollmentHandler();
                adminEnrollment.put("Admin_Enrollment_ZT_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_Zt_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new GSuiteChromeDeviceEnrollmentHandler();
                adminEnrollment.put("Chrome_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new WindowsLaptopEnrollmentHandler();
                clickVal = DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Admin_Enrollment_LAPTOP_Tool_Download_Clicked", "PARAM_VALUE");
                adminEnrollment.put("Admin_Enrollment_LAPTOP_Tool_Download_Clicked", (Object)((clickVal == null) ? Boolean.FALSE : Boolean.valueOf(clickVal.toString())));
                clickVal = DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Admin_Enrollment_LAPTOP_Tool_Download_Success", "PARAM_VALUE");
                adminEnrollment.put("Admin_Enrollment_LAPTOP_Tool_Download_Success", (Object)((clickVal == null) ? Boolean.FALSE : Boolean.valueOf(clickVal.toString())));
                adminEnrollment.put("Admin_Enrollment_LAPTOP_Enrolled_Count", handler.getAdminEnrolledDeviceCount(customerID));
                adminEnrollment.put("Admin_Enrollment_LAPTOP_Unassigned_Count", handler.getUnassignedDeviceCount(customerID));
                handler = new WindowsAzureADEnrollmentHandler();
                adminEnrollment.put("WP_AZURE_UNASSIGNED_COUNT", handler.getUnassignedDeviceCount(customerID));
                adminEnrollment.put("WP_ADMIN_AZURE_ENROLLED_DEVICE_COUNT", handler.getAdminEnrolledDeviceCount(customerID));
            }
            catch (final Exception exp) {
                SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "addAdminEnrollmentCount", "Exception : ", (Throwable)exp);
            }
        }
        return adminEnrollment;
    }
    
    public static JSONObject getAndroidMEMDMAppSetting() {
        final JSONObject mdmAppSetting = new JSONObject();
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final MDMAgentSettingsHandler agentSettingsHandler = MDMAgentSettingsHandler.getInstance();
            final JSONObject androidSettings = agentSettingsHandler.getAndroidAgentSetting(customerID);
            final JSONObject rebrandingSetting = agentSettingsHandler.getAgentRebrandingSetting(customerID);
            final int androidNotification = agentSettingsHandler.getNotificaitonServiceType(2, customerID);
            final String service = (androidNotification == 2) ? "Polling" : "GCM";
            final int androidAppDownloadMode = agentSettingsHandler.getAndroidAgentDownloadMode();
            final String downloadMode = (androidAppDownloadMode == 3) ? "Google Play Store" : "MDM Server";
            mdmAppSetting.put("Allow_App_Uninstall", !androidSettings.optBoolean("ALLOW_ADMIN_DISABLE", true));
            mdmAppSetting.put("Hide_Server_Details", androidSettings.optBoolean("HIDE_SERVER_DETAILS", false));
            mdmAppSetting.put("Hide_App", androidSettings.optBoolean("HIDE_MDM_APP", false));
            mdmAppSetting.put("Hide_Server_Info", androidSettings.optBoolean("HIDE_SERVER_INFO", false));
            mdmAppSetting.put("App_Name", (Object)URLEncoder.encode(rebrandingSetting.optString("MDM_APP_NAME", ""), "UTF-8"));
            mdmAppSetting.put("App_Icon_Changed", (Object)rebrandingSetting.optString("MDM_APP_ICON_FILE_NAME", ""));
            mdmAppSetting.put("App_Splash_Image_Changed", (Object)rebrandingSetting.optString("MDM_APP_SPLASH_IMAGE_FILE_NAME", ""));
            mdmAppSetting.put("Android_Communication_Mode", (Object)service);
            mdmAppSetting.put("Android_MEMDMApp_Download_Mode", (Object)downloadMode);
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception occurred while getAndroidMEMDMAppSetting", ex);
        }
        return mdmAppSetting;
    }
    
    private static Integer getRowCountInMdmAppTrackingTbl() {
        Integer noOfRecordsInTable = 0;
        try {
            noOfRecordsInTable = DBUtil.getRecordCount("MDMAppAnalyticData", "RESOURCE_ID", new Criteria(Column.getColumn("MDMAppAnalyticData", "DOC_VIEWER_USED_COUNT"), (Object)0, 4));
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getRowCountInMdmAppTrackingTbl", ex);
        }
        return noOfRecordsInTable;
    }
    
    private static Integer getDocsSavedCountInIosAgent() {
        Integer noOfdevicesDocsSaved = 0;
        try {
            noOfdevicesDocsSaved = DBUtil.getRecordActualCount("MDMAppAnalyticData", "DOCS_SAVED_COUNT", new Criteria(Column.getColumn("MDMAppAnalyticData", "DOCS_SAVED_COUNT"), (Object)0, 5));
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getDocsSavedCountInIosAgent", ex);
        }
        return noOfdevicesDocsSaved;
    }
    
    private static Integer getDcoViewerUsedCountInIosAgent() {
        Integer noOfDevicesDocViewerUsed = 0;
        try {
            noOfDevicesDocViewerUsed = DBUtil.getRecordActualCount("MDMAppAnalyticData", "DOC_VIEWER_USED_COUNT", new Criteria(Column.getColumn("MDMAppAnalyticData", "DOC_VIEWER_USED_COUNT"), (Object)0, 5));
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getDcoViewerUsedCountInIosAgent", ex);
        }
        return noOfDevicesDocViewerUsed;
    }
    
    public static JSONObject getMDMAppAnalyticData() {
        final JSONObject mdmAppTrackingJson = new JSONObject();
        try {
            final Integer noOfDevicesDocsSaved = getDocsSavedCountInIosAgent();
            final Integer noOfDevicesDocViewerUsed = getDcoViewerUsedCountInIosAgent();
            final Integer rowCountInMdmAppTrackingTbl = getRowCountInMdmAppTrackingTbl();
            mdmAppTrackingJson.put("docsSaved", (Object)noOfDevicesDocsSaved);
            mdmAppTrackingJson.put("docViewed", (Object)noOfDevicesDocViewerUsed);
            mdmAppTrackingJson.put("docViewerAgent", (Object)rowCountInMdmAppTrackingTbl);
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getMDMAppAnalyticData", ex);
        }
        return mdmAppTrackingJson;
    }
    
    protected static JSONObject getConflictingKioskPayloadErrorCount() {
        final JSONObject object = new JSONObject();
        Integer errCount = 0;
        Integer noAppErrorCount = 0;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MDMCollnToResErrorCode"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria error1 = new Criteria(new Column("MDMCollnToResErrorCode", "ERROR_CODE"), (Object)48000, 0);
            final Criteria error2 = new Criteria(new Column("MDMCollnToResErrorCode", "ERROR_CODE"), (Object)3002, 0);
            final Criteria noAppCriteria = new Criteria(new Column("MDMCollnToResErrorCode", "ERROR_CODE"), (Object)21008, 0);
            sq.setCriteria(error1.or(error2).or(noAppCriteria));
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            if (dO != null && !dO.isEmpty()) {
                errCount = DBUtil.getIteratorSize(dO.getRows("MDMCollnToResErrorCode", error1.or(error2)));
                noAppErrorCount = DBUtil.getIteratorSize(dO.getRows("MDMCollnToResErrorCode", noAppCriteria));
            }
            object.put("errorCount", (Object)errCount);
            object.put("noAppErrorCount", (Object)noAppErrorCount);
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getConflictingKioskPayloadErrorStatus", ex);
        }
        return object;
    }
    
    protected static void getIOSKioskPayloadCount(final JSONObject json) {
        try {
            final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
            final DerivedTable table = MDMTrackerUtil.getIOSScreenLayoutMoreAppsTable();
            final DerivedTable autonomousTable = MDMTrackerUtil.getAutonomousKioskAppTable();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileToColln"));
            sQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            sQuery.addJoin(new Join("AppLockPolicy", "ScreenLayoutSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            sQuery.addJoin(new Join(new Table("AppLockPolicy"), (Table)table, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            sQuery.addJoin(new Join(new Table("AppLockPolicy"), (Table)autonomousTable, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayoutSettings", "ScreenLayout", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayout", "ScreenLayoutToPageRelation", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayoutToPageRelation", "ScreenLayoutPageDetails", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayoutPageDetails", "ScreenPageToPageLayout", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
            sQuery.addJoin(new Join("ScreenPageToPageLayout", "ScreenPageLayout", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
            final CaseExpression singleAppKiosk = new CaseExpression("IOSSingleAppKioskCount");
            singleAppKiosk.addWhen(new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)1, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression multipleAppKiosk = new CaseExpression("IOSMultiAppKioskCount");
            multipleAppKiosk.addWhen(new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)2, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression iOSSingleWebAppKiosk = new CaseExpression("IOSSingleWebAppKioskCount");
            iOSSingleWebAppKiosk.addWhen(new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)3, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression iOSHomeScreenCount = new CaseExpression("IOSHomeScreenCount");
            iOSHomeScreenCount.addWhen(new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)Column.getColumn("AppLockPolicy", "CONFIG_DATA_ITEM_ID"), 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression iOSDockConfiguredCount = new CaseExpression("IOSHomeScreenDockCount");
            iOSDockConfiguredCount.addWhen(new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)2, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression iOSFolderConfiguredCount = new CaseExpression("IOSHomeScreenFolderCount");
            iOSFolderConfiguredCount.addWhen(new Criteria(new Column("ScreenPageLayout", "PAGE_LAYOUT_TYPE"), (Object)3, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression iosHomeScreenMoreAppCount = new CaseExpression("IOSHomeScreenMoreAppCount");
            iosHomeScreenMoreAppCount.addWhen(new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)Column.getColumn("AppLockPolicy", "CONFIG_DATA_ITEM_ID"), 0).and(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)new Column(table.getTableAlias(), "CONFIG_DATA_ITEM_ID"), 0)), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression iOSAutonomousKiosk = new CaseExpression("IOSAutonomousAppKioskCount");
            iOSAutonomousKiosk.addWhen(new Criteria(new Column(autonomousTable.getTableAlias(), "CONFIG_DATA_ITEM_ID"), (Object)null, 1), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)multipleAppKiosk));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSSingleWebAppKiosk));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSHomeScreenCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSDockConfiguredCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSFolderConfiguredCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosHomeScreenMoreAppCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)singleAppKiosk));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAutonomousKiosk));
            final DMDataSetWrapper wrapper = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (wrapper.next()) {
                final List<Column> columns = sQuery.getSelectColumns();
                for (final Column column : columns) {
                    json.put(column.getColumnAlias(), (Object)wrapper.getValue(column.getColumnAlias()).toString());
                }
            }
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getIOSMultipleAppKioskPayloadCount", e);
        }
    }
    
    protected static void getAndroidMultipleAppKioskPayloadCount(final JSONObject json) {
        try {
            final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
            final DerivedTable androidAppTable = MDMTrackerUtil.getAndroidScreenLayoutMoreAppTable();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileToColln"));
            sQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            sQuery.addJoin(new Join("AndroidKioskPolicy", "ScreenLayoutSettings", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            sQuery.addJoin(new Join(new Table("AndroidKioskPolicy"), (Table)androidAppTable, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayoutSettings", "ScreenLayout", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayout", "ScreenLayoutToPageRelation", new String[] { "SCREEN_LAYOUT_ID" }, new String[] { "SCREEN_LAYOUT_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayoutToPageRelation", "ScreenLayoutPageDetails", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
            sQuery.addJoin(new Join("ScreenLayoutPageDetails", "ScreenPageToPageLayout", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
            sQuery.addJoin(new Join("ScreenPageToPageLayout", "ScreenPageLayout", new String[] { "PAGE_LAYOUT_ID" }, new String[] { "PAGE_LAYOUT_ID" }, 1));
            final CaseExpression singleAppKiosk = new CaseExpression("AndroidSingleAppKioskCount");
            singleAppKiosk.addWhen(new Criteria(new Column("AndroidKioskPolicy", "KIOSK_MODE"), (Object)0, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression multipleAppKiosk = new CaseExpression("AndroidMultiAppKioskCount");
            multipleAppKiosk.addWhen(new Criteria(new Column("AndroidKioskPolicy", "KIOSK_MODE"), (Object)1, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression androidSingleWebAppKiosk = new CaseExpression("AndroidSingleWebAppKioskCount");
            androidSingleWebAppKiosk.addWhen(new Criteria(new Column("AndroidKioskPolicy", "KIOSK_MODE"), (Object)3, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression androidHomeScreen = new CaseExpression("AndroidHomeScreenCount");
            androidHomeScreen.addWhen(new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)Column.getColumn("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"), 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression androidDockConfiguredCount = new CaseExpression("AndroidHomeScreenDockCount");
            androidDockConfiguredCount.addWhen(new Criteria(new Column("ScreenLayoutPageDetails", "PAGE_TYPE"), (Object)2, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final CaseExpression androidFolderConfiguredCount = new CaseExpression("AndroidHomeScreenFolderCount");
            androidFolderConfiguredCount.addWhen(new Criteria(new Column("ScreenPageLayout", "PAGE_LAYOUT_TYPE"), (Object)3, 0), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            final DerivedTable androidAppGroupCountTable = MDMTrackerUtil.getAndroidScreenLayoutMoreAppTable();
            final CaseExpression androidHomeScreenMoreAppCount = new CaseExpression("AndroidHomeScreenMoreAppCount");
            androidHomeScreenMoreAppCount.addWhen(new Criteria(new Column("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID"), (Object)Column.getColumn("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"), 0).and(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)new Column(androidAppGroupCountTable.getTableAlias(), "CONFIG_DATA_ITEM_ID"), 0)), (Object)new Column("RecentProfileToColln", "PROFILE_ID"));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)multipleAppKiosk));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidSingleWebAppKiosk));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidHomeScreen));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidDockConfiguredCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidFolderConfiguredCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidHomeScreenMoreAppCount));
            sQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)singleAppKiosk));
            final DMDataSetWrapper wrapper = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (wrapper.next()) {
                final List<Column> columns = sQuery.getSelectColumns();
                for (final Column column : columns) {
                    json.put(column.getColumnAlias(), (Object)wrapper.getValue(column.getColumnAlias()).toString());
                }
            }
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getAndroidMultipleAppKioskPayloadCount", e);
        }
    }
    
    protected static JSONObject getAndroidKioskLauncherCount() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileToColln"));
            sQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigDataItem", "AndroidKioskPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            sQuery.setCriteria(new Criteria(new Column("AndroidKioskPolicy", "KIOSK_MODE"), (Object)1, 0).and(new Criteria(new Column("AndroidKioskPolicy", "LAUNCHER_TYPE"), (Object)2, 0)));
            DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty()) {
                jsonObject.put("mdmLauncher", (Object)DBUtil.getIteratorSize(dO.getRows("AndroidKioskPolicy")).toString());
            }
            else {
                jsonObject.put("mdmLauncher", (Object)"0");
            }
            sQuery.setCriteria(new Criteria(new Column("AndroidKioskPolicy", "KIOSK_MODE"), (Object)1, 0).and(new Criteria(new Column("AndroidKioskPolicy", "LAUNCHER_TYPE"), (Object)1, 0)));
            dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty()) {
                jsonObject.put("deviceLauncher", (Object)DBUtil.getIteratorSize(dO.getRows("AndroidKioskPolicy")).toString());
            }
            else {
                jsonObject.put("deviceLauncher", (Object)"0");
            }
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getAndroidMultipleAppKioskPayloadCount", e);
        }
        return jsonObject;
    }
    
    protected static JSONObject getManagedAppConfigurationAppsCount() throws SQLException {
        DMDataSetWrapper dataSet = null;
        final JSONObject appCaonfigData = new JSONObject();
        try {
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            baseSelectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdPackageToAppGroup", "AppConfigTemplate", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            baseSelectQuery.addJoin(new Join("MdPackageToAppData", "InstallAppPolicy", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
            baseSelectQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            baseSelectQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
            final Column appCount = Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE").count();
            appCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(appCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
            baseSelectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
            final List list = new ArrayList();
            final Column groupByCol1 = Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE");
            final Column groupByCol2 = Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE");
            list.add(groupByCol1);
            list.add(groupByCol2);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            dataSet = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSet != null) {
                while (dataSet.next()) {
                    final int platformType = (int)dataSet.getValue("PLATFORM_TYPE");
                    final int packageType = (int)dataSet.getValue("PACKAGE_TYPE");
                    final int count = (int)dataSet.getValue("APP_COUNT");
                    String key = "";
                    if (platformType == 1) {
                        key = "ios";
                    }
                    else if (platformType == 2) {
                        key = "android";
                    }
                    else if (platformType == 3) {
                        key = "windows";
                    }
                    if (packageType == 0 || packageType == 1) {
                        final String appStoreKey = key + "_store";
                        int storeAppCount = 0;
                        final Object storeAppCountObj = appCaonfigData.opt(appStoreKey);
                        if (storeAppCountObj != null) {
                            storeAppCount = (int)storeAppCountObj;
                        }
                        appCaonfigData.put(appStoreKey, storeAppCount + count);
                    }
                    else {
                        if (packageType != 2) {
                            continue;
                        }
                        final String enterpriseKey = key + "_enterprise";
                        appCaonfigData.put(enterpriseKey, count);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in   {0}", ex);
        }
        return appCaonfigData;
    }
    
    public static JSONObject getPlatformModelWiseInfo(final Integer platformType) {
        final JSONObject platformModelJSON = new JSONObject();
        final SelectQuery modelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        modelQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        modelQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        if (platformType != null) {
            managedCriteria = managedCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0));
        }
        modelQuery.setCriteria(managedCriteria);
        final Column platformTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Column modelTypeColumn = Column.getColumn("MdModelInfo", "MODEL_TYPE");
        final Column modelCountColumn = Column.getColumn("ManagedDevice", "RESOURCE_ID").count();
        modelCountColumn.setColumnAlias("MODEL_COUNT");
        modelQuery.addSelectColumn(platformTypeColumn);
        modelQuery.addSelectColumn(modelTypeColumn);
        modelQuery.addSelectColumn(modelCountColumn);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(platformTypeColumn);
        groupByColumns.add(modelTypeColumn);
        final GroupByClause groupBy = new GroupByClause(groupByColumns);
        modelQuery.setGroupByClause(groupBy);
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)modelQuery);
            while (ds.next()) {
                final String platformValue = String.valueOf(ds.getValue("PLATFORM_TYPE"));
                final String modelValue = String.valueOf(ds.getValue("MODEL_TYPE"));
                final String modelCount = String.valueOf(ds.getValue("MODEL_COUNT"));
                JSONObject platformJSON = platformModelJSON.optJSONObject(platformValue);
                if (platformJSON == null) {
                    platformJSON = new JSONObject();
                }
                platformJSON.put(modelValue, (Object)modelCount);
                platformModelJSON.put(platformValue, (Object)platformJSON);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception in obtaining model info json {0}", ex);
        }
        return platformModelJSON;
    }
    
    public static JSONObject getProfileAddedDistributedData(final int profileType) {
        final JSONObject profileDatajson = new JSONObject();
        Long distributedTime = -1L;
        Long addedTime = -1L;
        Integer distributedCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            sQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addJoin(new Join("ProfileToCollection", "ResourceToProfileHistory", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            sQuery.addJoin(new Join("ResourceToProfileHistory", "RecentProfileForResource", new String[] { "PROFILE_ID", "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID", "RESOURCE_ID" }, 1));
            final Criteria profileVersionCri = new Criteria(new Column("ProfileToCollection", "PROFILE_VERSION"), (Object)1, 0);
            final Criteria profileTypeCri = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria markForDeleteNotTrueCri = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)true, 1);
            sQuery.setCriteria(profileVersionCri.and(profileTypeCri));
            sQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            sQuery.addSelectColumn(new Column("Profile", "CREATION_TIME"));
            sQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            sQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"));
            sQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
            sQuery.addSelectColumn(new Column("ResourceToProfileHistory", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_ID"));
            sQuery.addSelectColumn(new Column("ResourceToProfileHistory", "ASSOCIATED_TIME"));
            sQuery.addSelectColumn(new Column("ResourceToProfileHistory", "PROFILE_ID"));
            final DataObject profileDataDo = MDMUtil.getPersistence().get(sQuery);
            if (!profileDataDo.isEmpty()) {
                final SortColumn sortColumn = new SortColumn(Column.getColumn("Profile", "CREATION_TIME"), true);
                profileDataDo.sortRows("Profile", new SortColumn[] { sortColumn });
                final Row profileRow = profileDataDo.getFirstRow("Profile");
                addedTime = (Long)profileRow.get("CREATION_TIME");
                final SortColumn sortColumnAssociatedTime = new SortColumn(Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_TIME"), true);
                profileDataDo.sortRows("ResourceToProfileHistory", new SortColumn[] { sortColumnAssociatedTime });
                if (profileDataDo.getTableNames().contains("ResourceToProfileHistory")) {
                    final Row resToProfileHistoryRow = profileDataDo.getFirstRow("ResourceToProfileHistory");
                    distributedTime = (Long)resToProfileHistoryRow.get("ASSOCIATED_TIME");
                }
                distributedCount = DBUtil.getIteratorSize(profileDataDo.getRows("ResourceToProfileHistory", markForDeleteNotTrueCri));
            }
            profileDatajson.put("profile_Added_Time", (Object)addedTime.toString());
            profileDatajson.put("profile_Distributed_Time", (Object)distributedTime.toString());
            profileDatajson.put("profile_Distributed_Count", (Object)distributedCount.toString());
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getProfileAddedDistributedData {0}", ex);
        }
        return profileDatajson;
    }
    
    public static Integer getViewVisitCount(final String viewName) {
        Integer count = 0;
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final String countStr = CustomerParamsHandler.getInstance().getParameterValue(viewName, (long)customerId);
                if (countStr != null) {
                    count = Integer.parseInt(countStr);
                }
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception occurred in  getViewVisitCount {0}", ex);
        }
        return count;
    }
    
    public static Column getDistinctIntegerCountOfCaseExpression(final CaseExpression expression) {
        final Column selectDistinctColumn = (Column)Column.createFunction("DISTINCT", new Object[] { expression });
        final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { selectDistinctColumn });
        selectColumn.setType(4);
        selectColumn.setColumnAlias(expression.getColumnAlias());
        return selectColumn;
    }
    
    public static Column getIntegerCountOfCaseExpression(final CaseExpression expression) {
        final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { expression });
        selectColumn.setType(4);
        selectColumn.setColumnAlias(expression.getColumnAlias());
        return selectColumn;
    }
    
    public static JSONObject getExchangeActiveSyncDetailsJSON() {
        final JSONObject exchangePolicyJSON = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Criteria isAndroidPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria isIosPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria isWindowsPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria androidOffice365Cri = new Criteria(new Column("AndroidActiveSyncPolicy", "ACTIVE_SYNC_HOST"), (Object)"*outlook.office365.com*", 2);
        final Criteria iOSOffice365Cri = new Criteria(new Column("ExchangeActiveSyncPolicy", "ACTIVE_SYNC_HOST"), (Object)"*outlook.office365.com*", 2);
        final Criteria winOffice365Cri = new Criteria(new Column("WpExchangeActiveSyncPolicy", "SERVER_NAME"), (Object)"*outlook.office365.com*", 2);
        final Criteria iOSSMIMECri = new Criteria(new Column("ExchangeActiveSyncPolicy", "USE_MIME_ENCRYPT"), (Object)true, 0);
        final Criteria iOSIdentityCri = new Criteria(new Column("ExchangeActiveSyncPolicy", "IDENTITY_CERT_ID"), (Object)0, 5);
        final Criteria androidIdentityCri = new Criteria(new Column("AndroidActiveSyncPolicy", "IDENTITY_CERT_ID"), (Object)0, 5);
        final Criteria iOSSigningCertCri = new Criteria(new Column("ExchangeActiveSyncPolicy", "SIGNING_CERT_ID"), (Object)0, 5);
        final Criteria iOSEncryptionCertCri = new Criteria(new Column("ExchangeActiveSyncPolicy", "ENCRYPTION_CERT_ID"), (Object)0, 5);
        sQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        sQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "AndroidActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        sQuery.addJoin(new Join("ConfigDataItem", "WpExchangeActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        sQuery.addJoin(new Join("ConfigDataItem", "ExchangeActiveSyncPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        final CaseExpression androidOffice365Exchangecount = new CaseExpression("Android_O365");
        androidOffice365Exchangecount.addWhen(isAndroidPlatform.and(androidOffice365Cri), (Object)new Column("AndroidActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression iOSOffice365Exchangecount = new CaseExpression("iOS_O365");
        iOSOffice365Exchangecount.addWhen(isIosPlatform.and(iOSOffice365Cri), (Object)new Column("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression winOffice365Exchangecount = new CaseExpression("Win_O365");
        winOffice365Exchangecount.addWhen(isWindowsPlatform.and(winOffice365Cri), (Object)new Column("WpExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression androidOPExchangecount = new CaseExpression("Android_OP");
        androidOPExchangecount.addWhen(isAndroidPlatform.and(androidOffice365Cri.negate()), (Object)new Column("AndroidActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression iOSOPExchangecount = new CaseExpression("iOS_OP");
        iOSOPExchangecount.addWhen(isIosPlatform.and(iOSOffice365Cri.negate()), (Object)new Column("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression winOPExchangecount = new CaseExpression("Win_OP");
        winOPExchangecount.addWhen(isWindowsPlatform.and(winOffice365Cri.negate()), (Object)new Column("WpExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression iOSSMIMEEnabledCount = new CaseExpression("iOS_SMIME");
        iOSSMIMEEnabledCount.addWhen(isIosPlatform.and(iOSSMIMECri), (Object)new Column("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression iOSIdentityCertCount = new CaseExpression("iOS_Identity_Crt");
        iOSIdentityCertCount.addWhen(isIosPlatform.and(iOSIdentityCri), (Object)new Column("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression androidIdentityCertCount = new CaseExpression("Android_Identity_Crt");
        androidIdentityCertCount.addWhen(isAndroidPlatform.and(androidIdentityCri), (Object)new Column("AndroidActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression iOSSigningCrtCount = new CaseExpression("iOS_Sign_Crt");
        iOSSigningCrtCount.addWhen(isIosPlatform.and(iOSSigningCertCri), (Object)new Column("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        final CaseExpression iOSEncryptionCrtCount = new CaseExpression("iOS_Enrty_Crt");
        iOSEncryptionCrtCount.addWhen(isIosPlatform.and(iOSEncryptionCertCri), (Object)new Column("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID"));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(androidOffice365Exchangecount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(iOSOffice365Exchangecount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(winOffice365Exchangecount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(androidOPExchangecount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(iOSOPExchangecount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(winOPExchangecount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(iOSSMIMEEnabledCount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(iOSIdentityCertCount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(iOSSigningCrtCount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(iOSEncryptionCrtCount));
        sQuery.addSelectColumn(getIntegerCountOfCaseExpression(androidIdentityCertCount));
        DMDataSetWrapper ds = null;
        try {
            MEMDMTrackerUtil.logger.log(Level.INFO, "getExchangeActiveSyncDetailsJSON QUERY : {0}", RelationalAPI.getInstance().getSelectSQL((Query)sQuery));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            if (ds.next()) {
                exchangePolicyJSON.put("Android_O365", ds.getValue("Android_O365"));
                exchangePolicyJSON.put("iOS_O365", ds.getValue("iOS_O365"));
                exchangePolicyJSON.put("Win_O365", ds.getValue("Win_O365"));
                exchangePolicyJSON.put("Android_OP", ds.getValue("Android_OP"));
                exchangePolicyJSON.put("iOS_OP", ds.getValue("iOS_OP"));
                exchangePolicyJSON.put("Win_OP", ds.getValue("Win_OP"));
                exchangePolicyJSON.put("iOS_SMIME", ds.getValue("iOS_SMIME"));
                exchangePolicyJSON.put("iOS_Identity_Crt", ds.getValue("iOS_Identity_Crt"));
                exchangePolicyJSON.put("Android_Identity_Crt", ds.getValue("Android_Identity_Crt"));
                exchangePolicyJSON.put("iOS_Enrty_Crt", ds.getValue("iOS_Enrty_Crt"));
                exchangePolicyJSON.put("iOS_Sign_Crt", ds.getValue("iOS_Sign_Crt"));
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "SQLException occurred while getting getExchangeActiveSyncDetailsJSON");
        }
        return exchangePolicyJSON;
    }
    
    public static JSONObject getProfileCertificateDetails() {
        final JSONObject profileCertificate = new JSONObject();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Certificates"));
            final Criteria scepCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)1, 0);
            final Criteria uploadedCertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)0, 0);
            final CaseExpression scepcount = new CaseExpression("SCEPConfigCnt");
            scepcount.addWhen(scepCriteria, (Object)new Column("Certificates", "CERTIFICATE_RESOURCE_ID"));
            final CaseExpression uploadedCnt = new CaseExpression("CertUploaded");
            uploadedCnt.addWhen(uploadedCertCriteria, (Object)new Column("Certificates", "CERTIFICATE_RESOURCE_ID"));
            sQuery.addSelectColumn(getIntegerCountOfCaseExpression(scepcount));
            sQuery.addSelectColumn(getIntegerCountOfCaseExpression(uploadedCnt));
            DMDataSetWrapper ds = null;
            try {
                MEMDMTrackerUtil.logger.log(Level.INFO, "getProfileCertificateDetails QUERY : {0}", RelationalAPI.getInstance().getSelectSQL((Query)sQuery));
                ds = DMDataSetWrapper.executeQuery((Object)sQuery);
                if (ds.next()) {
                    profileCertificate.put("CertUploaded", ds.getValue("CertUploaded"));
                    profileCertificate.put("SCEPConfigCnt", ds.getValue("SCEPConfigCnt"));
                }
            }
            catch (final Exception ex) {
                MEMDMTrackerUtil.logger.log(Level.SEVERE, "SQLException occurred while getting getProfileCertificateDetails");
            }
        }
        catch (final Exception ex2) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "SQLException occurred while getting getProfileCertificateDetails");
        }
        return profileCertificate;
    }
    
    public static JSONObject getRestrictionUsage1() {
        final JSONObject restrictionUsage = new JSONObject();
        try {
            restrictionUsage.put("iOSRestrictions", (Object)getRestrictionPlatformRestrictionUsage(1).toString());
            restrictionUsage.put("AndroidRestrictions", (Object)getRestrictionPlatformRestrictionUsage(2).toString());
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "SQLException occurred while getting getRestrictionUsage");
        }
        return restrictionUsage;
    }
    
    public static JSONObject getRestrictionPlatformRestrictionUsage(final int platform) {
        final JSONObject restrictionUsage = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final String platfromRestrictionTable = (platform == 1) ? "RestrictionsPolicy" : ((platform == 2) ? "AndroidRestrictionsPolicy" : "WpRestrictionsPolicy");
            sQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            sQuery.addJoin(new Join("ConfigDataItem", platfromRestrictionTable, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria cPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
            sQuery.setCriteria(cPlatform);
            final List selectedColumnList = getRestrictionsSelectedColumn(platform);
            sQuery.addSelectColumns(selectedColumnList);
            MEMDMTrackerUtil.logger.log(Level.INFO, "getRestrictionPlatformRestrictionUsage QUERY : {0}", RelationalAPI.getInstance().getSelectSQL((Query)sQuery));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            if (ds.next()) {
                for (final Column col : selectedColumnList) {
                    final String columnName = col.getColumnAlias();
                    final Object columnValue = ds.getValue(columnName);
                    if (columnName != null && columnValue != null) {
                        restrictionUsage.put(columnName, columnValue);
                    }
                }
            }
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception in getRestrictionPlatformRestrictionUsage", e);
        }
        return restrictionUsage;
    }
    
    private static List getRestrictionsSelectedColumn(final int platform) {
        final List selectColsList = new ArrayList();
        if (platform == 1) {
            final List<String> iOSRestrictions = new ArrayList<String>();
            iOSRestrictions.add("FORCE_WIFI_WHITELISTING");
            for (final String colName : iOSRestrictions) {
                final CaseExpression iOSWifiWhiteListExpression = new CaseExpression(colName);
                iOSWifiWhiteListExpression.addWhen(new Criteria(new Column("RestrictionsPolicy", colName), (Object)true, 0), (Object)new Column("RestrictionsPolicy", "CONFIG_DATA_ITEM_ID"));
                selectColsList.add(getIntegerCountOfCaseExpression(iOSWifiWhiteListExpression));
            }
        }
        else if (platform == 2) {
            final List<String> andRestrictions = new ArrayList<String>();
            andRestrictions.add("ALLOW_WHITELIST_WIFI_ONLY");
            for (final String colName : andRestrictions) {
                final CaseExpression androidWifiWhiteListExpression = new CaseExpression(colName);
                androidWifiWhiteListExpression.addWhen(new Criteria(new Column("AndroidRestrictionsPolicy", colName), (Object)1, 0), (Object)new Column("AndroidRestrictionsPolicy", "CONFIG_DATA_ITEM_ID"));
                selectColsList.add(getIntegerCountOfCaseExpression(androidWifiWhiteListExpression));
            }
        }
        return selectColsList;
    }
    
    public static int getChannelURINotObtainedCount() {
        int channelURINullCount = 0;
        try {
            final SelectQuery notificationDetailsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join deviceDetailsJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join notificationDetailsJoin = new Join("ManagedDevice", "ManagedDeviceNotification", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            notificationDetailsQuery.addJoin(deviceDetailsJoin);
            notificationDetailsQuery.addJoin(notificationDetailsJoin);
            notificationDetailsQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final Criteria platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
            final Criteria notificationDetailsNullCriteria = new Criteria(Column.getColumn("ManagedDeviceNotification", "NOTIFICATION_DETAILS_ID"), (Object)null, 0);
            final Criteria enrollmentStatusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 2, 5 }, 8);
            notificationDetailsQuery.setCriteria(platformTypeCriteria.and(notificationDetailsNullCriteria).and(enrollmentStatusCriteria));
            final DataObject dao = MDMUtil.getPersistence().get(notificationDetailsQuery);
            final Iterator iter = dao.getRows("ManagedDevice");
            while (iter.hasNext()) {
                iter.next();
                ++channelURINullCount;
            }
        }
        catch (final DataAccessException ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception in getChannelURINotObtainedCount method {0}", (Throwable)ex);
        }
        return channelURINullCount;
    }
    
    private static SelectQuery getTemporaryNewConfigurationQuery() {
        final Criteria l2tpCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)0, 0);
        final Criteria pptpCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)1, 0);
        final Criteria ipsecCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)2, 0);
        final Criteria ciscoLegacyCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)3, 0);
        final Criteria juniperCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)4, 0);
        final Criteria f5sslLegacyCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)5, 0);
        final Criteria customsslCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)6, 0);
        final Criteria pulseCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)7, 0);
        final Criteria ikev2Criteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)8, 0);
        final Criteria ciscoCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)9, 0);
        final Criteria sonicWallCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)10, 0);
        final Criteria arubaViaCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)11, 0);
        final Criteria checkPointCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)12, 0);
        final Criteria paloAltoLegacyCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)"com.paloaltonetworks.GlobalProtect.vpnplugin", 0);
        final Criteria paloAltoCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)"com.paloaltonetworks.globalprotect.vpn", 0);
        final Criteria f5sslCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)"com.f5.access.ios", 0);
        final Criteria openVPNCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)"net.openvpn.connect.app", 0);
        final Criteria citrixLegacyCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)"com.citrix.NetScalerGateway.ios.app", 0);
        final Criteria citrixCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)"com.citrix.NetScalerGateway.ios.app", 0);
        final CaseExpression iosL2tp = new CaseExpression("VPNL2TP");
        iosL2tp.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(l2tpCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosPptp = new CaseExpression("VPNPPTP");
        iosPptp.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(pptpCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosIpsec = new CaseExpression("VPNIPSEC");
        iosIpsec.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(ipsecCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosCiscoLegacy = new CaseExpression("VPNCISCOLEGACY");
        iosCiscoLegacy.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(ciscoLegacyCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosJuniperssl = new CaseExpression("VPNJUNIPERSSL");
        iosJuniperssl.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(juniperCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosF5sslLegacy = new CaseExpression("VPNF5SSL");
        iosF5sslLegacy.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(f5sslLegacyCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosCustomssl = new CaseExpression("VPNCUSTOMSSL");
        iosCustomssl.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosPulsesecure = new CaseExpression("VPNPULSESECURE");
        iosPulsesecure.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(pulseCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosIkev2 = new CaseExpression("VPNIKEV2");
        iosIkev2.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(ikev2Criteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosCisco = new CaseExpression("VPNCISCO");
        iosCisco.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(ciscoCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosSonicWall = new CaseExpression("VPNSONICWALL");
        iosSonicWall.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(sonicWallCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosArubavia = new CaseExpression("VPNARUBAVIA");
        iosArubavia.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(arubaViaCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosCheckpoint = new CaseExpression("VPNCHECKPOINT");
        iosCheckpoint.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(checkPointCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosPaloAltoLegacy = new CaseExpression("VPNPALOALTOLEGACY");
        iosPaloAltoLegacy.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(paloAltoLegacyCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosPaloAlto = new CaseExpression("PALOALTO");
        iosPaloAlto.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(paloAltoCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosF5ssl = new CaseExpression("VPNF5SSLNEW");
        iosF5ssl.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(f5sslCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosCitrixLegacy = new CaseExpression("VPNCITRIXLEGACY");
        iosCitrixLegacy.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(citrixLegacyCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosCitrix = new CaseExpression("VPNOPENVPN");
        iosCitrix.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(citrixCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosOpenvpn = new CaseExpression("VPNOPENVPN");
        iosOpenvpn.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(openVPNCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
        final SelectQuery configurationQuery = getConfigurationQuery();
        configurationQuery.addJoin(new Join("ConfigDataItem", "VpnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        configurationQuery.addJoin(new Join("VpnPolicy", "VpnCustomSSL", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosL2tp));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosPptp));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosIpsec));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosCiscoLegacy));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosJuniperssl));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosF5sslLegacy));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosCustomssl));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosPulsesecure));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosIkev2));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosCisco));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosSonicWall));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosArubavia));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosCheckpoint));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosPaloAltoLegacy));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosPaloAlto));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosF5ssl));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosOpenvpn));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosCitrixLegacy));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iosCitrix));
        final List pluginIds = MDMTrackerUtil.getVPNPluginIds();
        for (final Object identifier : pluginIds) {
            String identifierString = identifier.toString();
            final Criteria identifierCriteria = new Criteria(new Column("VpnCustomSSL", "IDENTIFIER"), (Object)identifierString, 0);
            identifierString = identifierString.replaceAll("\\.", "_");
            final CaseExpression identifierCaseExpression = new CaseExpression(identifierString);
            identifierCaseExpression.addWhen(MDMCoreQuery.getInstance().getIOSPlatformCriteria().and(customsslCriteria).and(identifierCriteria), (Object)new Column("Profile", "PROFILE_ID"));
            configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)identifierCaseExpression));
        }
        configurationQuery.setGroupByClause(trackerUtil.getCustomerGroupClause());
        return configurationQuery;
    }
    
    private static SelectQuery getTemporaryNewConfigurationQueryAndroid() {
        final Criteria l2tpPskCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)19, 0);
        final Criteria l2tpRsaCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)20, 0);
        final Criteria pptpCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)1, 0);
        final Criteria xauthPskCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)14, 0);
        final Criteria ikev2PskCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)16, 0);
        final Criteria ciscoCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)9, 0);
        final Criteria f5sslCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)5, 0);
        final Criteria paloAltoCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)13, 0);
        final Criteria pulseCriteria = new Criteria(new Column("VpnPolicy", "CONNECTION_TYPE"), (Object)7, 0);
        final CaseExpression androidL2tp = new CaseExpression("VPNL2TP");
        androidL2tp.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(l2tpPskCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidPptp = new CaseExpression("VPNPPTP");
        androidPptp.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(pptpCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidXauthPsk = new CaseExpression("VPNIPSEC");
        androidXauthPsk.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(xauthPskCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidIkev2Psk = new CaseExpression("VPNIPSEC");
        androidIkev2Psk.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(ikev2PskCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidF5ssl = new CaseExpression("VPNF5SSL");
        androidF5ssl.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(f5sslCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidPulsesecure = new CaseExpression("VPNPULSESECURE");
        androidPulsesecure.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(pulseCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidCisco = new CaseExpression("VPNCISCO");
        androidCisco.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(ciscoCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidPaloAlto = new CaseExpression("PALOALTO");
        androidPaloAlto.addWhen(MDMCoreQuery.getInstance().getAndroidPlatformCriteria().and(paloAltoCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
        final SelectQuery configurationQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerInfo"));
        configurationQuery.addJoin(new Join("CustomerInfo", "ProfileToCustomerRel", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        configurationQuery.addJoin(new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        configurationQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        configurationQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        configurationQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        configurationQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        configurationQuery.addJoin(new Join("ConfigDataItem", "VpnPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidL2tp));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidPptp));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidIkev2Psk));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidXauthPsk));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidCisco));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidF5ssl));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidPulsesecure));
        configurationQuery.addSelectColumn(trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)androidPaloAlto));
        configurationQuery.setGroupByClause(trackerUtil.getCustomerGroupClause());
        return configurationQuery;
    }
    
    private static SelectQuery getConfigurationQuery() {
        final SelectQuery configurationQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerInfo"));
        configurationQuery.addJoin(new Join("CustomerInfo", "ProfileToCustomerRel", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        configurationQuery.addJoin(new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        configurationQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        configurationQuery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        configurationQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        configurationQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        return configurationQuery;
    }
    
    public static JSONObject getAppTrashData() {
        DMDataSetWrapper trashDS = null;
        final JSONObject apptrashDetails = new JSONObject();
        final SelectQuery appTrashQuery = MDMCoreQuery.getInstance().getMDMQueryMap("APP_TRASH_QUERY");
        try {
            trashDS = DMDataSetWrapper.executeQuery((Object)appTrashQuery);
            while (trashDS.next()) {
                apptrashDetails.put("TRASH_APP_COUNT", trashDS.getValue("TRASH_APP_COUNT"));
                apptrashDetails.put("ACCOUNT_APPS_DELETED", trashDS.getValue("ACCOUNT_APPS_DELETED"));
                apptrashDetails.put("VPP_APP_COUNT", trashDS.getValue("VPP_APPS_DELETED"));
                apptrashDetails.put("PFW_APP_COUNT", trashDS.getValue("PFW_APPS_DELETED"));
                apptrashDetails.put("KIOSK_APPS_IN_TRASH", trashDS.getValue("KIOSK_APPS_IN_TRASH"));
            }
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.WARNING, "Exception in adding tracking data for app trash mode", e);
        }
        return apptrashDetails;
    }
    
    public static JSONObject getVPNType() {
        Connection con = null;
        DataSet vpnDs = null;
        DMDataSetWrapper androidVpnDs = null;
        try {
            final JSONObject vpnType = new JSONObject();
            final JSONObject iosVpnType = new JSONObject();
            final JSONObject androidVpnType = new JSONObject();
            final SelectQuery vpnTypeQuery = getTemporaryNewConfigurationQuery();
            con = RelationalAPI.getInstance().getConnection();
            vpnDs = RelationalAPI.getInstance().executeQuery((Query)vpnTypeQuery, con);
            while (vpnDs.next()) {
                iosVpnType.put("VPNL2TP", vpnDs.getInt("VPNL2TP"));
                iosVpnType.put("VPNPPTP", vpnDs.getInt("VPNPPTP"));
                iosVpnType.put("VPNIPSEC", vpnDs.getInt("VPNIPSEC"));
                iosVpnType.put("VPNCISCOLEGACY", vpnDs.getInt("VPNCISCOLEGACY"));
                iosVpnType.put("VPNJUNIPERSSL", vpnDs.getInt("VPNJUNIPERSSL"));
                iosVpnType.put("VPNF5SSL", vpnDs.getInt("VPNF5SSL"));
                iosVpnType.put("VPNCUSTOMSSL", vpnDs.getInt("VPNCUSTOMSSL"));
                iosVpnType.put("VPNPULSESECURE", vpnDs.getInt("VPNPULSESECURE"));
                iosVpnType.put("VPNIKEV2", vpnDs.getInt("VPNIKEV2"));
                iosVpnType.put("VPNCISCO", vpnDs.getInt("VPNCISCO"));
                iosVpnType.put("VPNSONICWALL", vpnDs.getInt("VPNSONICWALL"));
                iosVpnType.put("VPNARUBAVIA", vpnDs.getInt("VPNARUBAVIA"));
                iosVpnType.put("VPNCHECKPOINT", vpnDs.getInt("VPNCHECKPOINT"));
                iosVpnType.put("VPNOPENVPN", vpnDs.getInt("VPNOPENVPN"));
                iosVpnType.put("VPNCITRIXLEGACY", vpnDs.getInt("VPNCITRIXLEGACY"));
                iosVpnType.put("VPNOPENVPN", vpnDs.getInt("VPNOPENVPN"));
                iosVpnType.put("VPNPALOALTOLEGACY", vpnDs.getInt("VPNPALOALTOLEGACY"));
                iosVpnType.put("PALOALTO", vpnDs.getInt("PALOALTO"));
                iosVpnType.put("VPNF5SSLNEW", vpnDs.getInt("VPNF5SSLNEW"));
                final int columnCount = vpnDs.getColumnCount();
                String vpnPlugins = "";
                for (int i = 20; i <= columnCount; ++i) {
                    final String columnName = vpnDs.getColumnName(i);
                    if (!MDMStringUtils.isEmpty(vpnPlugins)) {
                        final String modifiedColumn = columnName.replaceAll("_", "\\.");
                        vpnPlugins = vpnPlugins + "," + modifiedColumn;
                    }
                    else {
                        vpnPlugins = columnName.replaceAll("_", "\\.");
                    }
                }
                iosVpnType.put("VPNOTHERS", (Object)vpnPlugins);
            }
            vpnType.put("iOS", (Object)iosVpnType);
            con.close();
            final SelectQuery androidVpnTypeQuery = getTemporaryNewConfigurationQueryAndroid();
            androidVpnDs = DMDataSetWrapper.executeQuery((Object)androidVpnTypeQuery);
            while (androidVpnDs.next()) {
                androidVpnType.put("VPNL2TP", androidVpnDs.getValue("VPNL2TP"));
                androidVpnType.put("VPNPPTP", androidVpnDs.getValue("VPNPPTP"));
                androidVpnType.put("VPNIPSEC", androidVpnDs.getValue("VPNIPSEC"));
                androidVpnType.put("VPNF5SSL", androidVpnDs.getValue("VPNF5SSL"));
                androidVpnType.put("VPNPULSESECURE", androidVpnDs.getValue("VPNPULSESECURE"));
                androidVpnType.put("VPNCISCO", androidVpnDs.getValue("VPNCISCO"));
                androidVpnType.put("PALOALTO", androidVpnDs.getValue("PALOALTO"));
            }
            vpnType.put("Android", (Object)androidVpnType);
            return vpnType;
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception while Creating IOS VPN Tracking", e);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(con, vpnDs);
        }
        return null;
    }
    
    protected static JSONObject getKioskPauseResumeCount() {
        final JSONObject object = new JSONObject();
        Integer androidPauseCount = 0;
        Integer androidResumeCount = 0;
        final Integer iOSPauseCount = 0;
        final Integer iOSResumeCount = 0;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
            sq.addJoin(new Join("MdCommands", "CommandHistory", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            sq.addJoin(new Join("CommandHistory", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sq.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
            sq.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            sq.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
            final Criteria kioskResumeUUID = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"ResumeKioskCommand", 0);
            final Criteria androidCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            sq.setCriteria(androidCriteria.and(kioskResumeUUID));
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            if (dO != null && !dO.isEmpty()) {
                androidPauseCount = DBUtil.getRecordCount("PauseKioskCommandHistory", "COMMAND_HISTORY_ID", (Criteria)null);
                androidResumeCount = DBUtil.getIteratorSize(dO.getRows("CommandHistory"));
            }
            object.put("androidKioskPauseCount", (Object)androidPauseCount);
            object.put("androidKioskResumeCount", (Object)androidResumeCount);
            object.put("iOSKioskPauseCount", (Object)iOSPauseCount);
            object.put("iOSKioskResumeCount", (Object)iOSResumeCount);
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception while tracking kiosk pause/resume data", ex);
        }
        return object;
    }
    
    public static SelectQuery EFRPMailIdCount(final SelectQuery selectQuery) {
        selectQuery.addJoin(new Join("AndroidEFRPPolicy", "EFRPAccDetails", new String[] { "EFRP_ACC_ID" }, new String[] { "EFRP_ACC_ID" }, 1));
        final Column countCol = new Column("EFRPAccDetails", "EMAIL_USER_ID").distinct().count();
        countCol.setColumnAlias("EFRP_MAIL_ID_COUNT");
        selectQuery.addSelectColumn(countCol);
        return selectQuery;
    }
    
    public static int getERFPIdCount() {
        int idCount = 0;
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidEFRPPolicy"));
        selectQuery = EFRPMailIdCount(selectQuery);
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                idCount = (int)ds.getValue("EFRP_MAIL_ID_COUNT");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(MEMDMTrackerUtil.logger, MEMDMTrackerUtil.sourceClass, "getERFPIdCount", "Caught exception while retrieving getERFPIdCount from DB", (Throwable)ex);
        }
        return idCount;
    }
    
    public static JSONObject getSecurityActionsInfo() {
        final JSONObject responseJSON = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MEMDMTrackParams"));
            selectQuery.addSelectColumn(Column.getColumn("MEMDMTrackParams", "ME_MDM_TRACK_PARAM_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MEMDMTrackParams", "MODULE_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MEMDMTrackParams", "PARAM_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MEMDMTrackParams", "PARAM_VALUE"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MEMDMTrackParams", "MODULE_NAME"), (Object)"INVENTORY_ACTIONS_MODULE", 0));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MEMDMTrackParams");
                while (iterator.hasNext()) {
                    final Row trackParamsRow = iterator.next();
                    final String paramName = (String)trackParamsRow.get("PARAM_NAME");
                    final String paramValue = (String)trackParamsRow.get("PARAM_VALUE");
                    responseJSON.put(paramName, (Object)paramValue);
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception in getSecurityActionsInfo", e);
        }
        return responseJSON;
    }
    
    public static JSONObject getCustomProfileTrackingDetails() {
        final JSONObject customProfileObject = new JSONObject();
        try {
            final SelectQuery configurationQuery = getConfigurationQuery();
            configurationQuery.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            configurationQuery.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
            configurationQuery.addJoin(new Join("CustomProfileDetails", "CustomProfileToPayloadDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
            configurationQuery.addJoin(new Join("CustomProfileToPayloadDetails", "PayloadTypeDetails", new String[] { "PAYLOAD_TYPE_ID" }, new String[] { "PAYLOAD_TYPE_ID" }, 1));
            final List columnName = new ArrayList();
            columnName.add(new Column("Profile", "PLATFORM_TYPE"));
            columnName.add(new Column("PayloadTypeDetails", "PAYLOAD_TYPE"));
            final Criteria iosCustomProfileCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)525, 0);
            final Criteria macCustomProfileCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)767, 0);
            final Criteria windowsCustomProfileCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)612, 0);
            configurationQuery.setCriteria(iosCustomProfileCriteria.or(windowsCustomProfileCriteria).or(macCustomProfileCriteria));
            final GroupByClause groupByClause = new GroupByClause(columnName);
            configurationQuery.setGroupByClause(groupByClause);
            configurationQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
            configurationQuery.addSelectColumn(new Column("PayloadTypeDetails", "PAYLOAD_TYPE"));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)configurationQuery);
            customProfileObject.put("iOS", (Object)new JSONArray());
            customProfileObject.put("Windows", (Object)new JSONArray());
            while (ds.next()) {
                final Integer platformType = (Integer)ds.getValue("PLATFORM_TYPE");
                final String payloadType = (String)ds.getValue("PAYLOAD_TYPE");
                if (platformType == 1) {
                    final JSONArray payloadArray = customProfileObject.optJSONArray("iOS");
                    payloadArray.put((Object)payloadType);
                }
                else {
                    if (platformType != 3) {
                        continue;
                    }
                    final JSONArray payloadArray = customProfileObject.optJSONArray("Windows");
                    payloadArray.put((Object)payloadType);
                }
            }
        }
        catch (final Exception e) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception in getting custom profile details", e);
        }
        return customProfileObject;
    }
    
    public static JSONObject getADMXConfigDataCount(final String[] admxDataId) {
        final JSONObject admxBackedPolicyTrackingDetails = new JSONObject();
        try {
            final Criteria admxConfigDataIdCriterea = new Criteria(new Column("ADMXBackedPolicyData", "DATA_ID"), (Object)admxDataId, 8, false);
            final Criteria finalCriteria = admxConfigDataIdCriterea.and(new Criteria(new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_CONFIG_DATA_VALUE"), (Object)"true", 0, false));
            final SelectQueryImpl selectQuery = new SelectQueryImpl(new Table("ADMXBackedPolicyDataConfig"));
            selectQuery.addJoin(new Join("ADMXBackedPolicyDataConfig", "ADMXBackedPolicyData", new String[] { "ADMX_BACKED_POLICY_DATA_ID" }, new String[] { "ADMX_BACKED_POLICY_DATA_ID" }, 1));
            final Column countColumn = new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_CONFIG_ID").count();
            countColumn.setColumnAlias("COUNT");
            selectQuery.addSelectColumn(countColumn);
            selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "DATA_ID"));
            selectQuery.addGroupByColumn(new Column("ADMXBackedPolicyData", "DATA_ID"));
            selectQuery.setCriteria(finalCriteria);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final int count = (int)dataSet.getValue("COUNT");
                final String dataID = dataSet.getValue("DATA_ID").toString();
                admxBackedPolicyTrackingDetails.put(dataID, count);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception while getting admx config data tracking details", ex);
        }
        return admxBackedPolicyTrackingDetails;
    }
    
    public static JSONObject getADMXConfigCount(final String[] admxPolicyGPNames) {
        final JSONObject admxBackedPolicyTrackingDetails = new JSONObject();
        try {
            final Criteria admxPolicyCriterea = new Criteria(new Column("ADMXBackedPolicy", "GP_NAME"), (Object)admxPolicyGPNames, 8, false);
            final Criteria finalCriteria = admxPolicyCriterea.and(new Criteria(new Column("ADMXBackedPolicyConfig", "ADMX_STATUS"), (Object)1, 0, false));
            final SelectQueryImpl selectQuery = new SelectQueryImpl(new Table("ADMXBackedPolicy"));
            selectQuery.addJoin(new Join("ADMXBackedPolicy", "ADMXBackedPolicyConfig", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
            final Column countColumn = new Column("ADMXBackedPolicyConfig", "ADMX_BACKED_POLICY_CONFIG_ID").count();
            countColumn.setColumnAlias("COUNT");
            selectQuery.addSelectColumn(countColumn);
            selectQuery.addSelectColumn(new Column("ADMXBackedPolicy", "GP_NAME"));
            selectQuery.addGroupByColumn(new Column("ADMXBackedPolicy", "GP_NAME"));
            selectQuery.setCriteria(finalCriteria);
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final int count = (int)dataSet.getValue("COUNT");
                final String admxBackedPolicyGPName = dataSet.getValue("GP_NAME").toString();
                admxBackedPolicyTrackingDetails.put(admxBackedPolicyGPName, count);
            }
        }
        catch (final Exception ex) {
            MEMDMTrackerUtil.logger.log(Level.SEVERE, "Exception while getting admx config tracking details", ex);
        }
        return admxBackedPolicyTrackingDetails;
    }
    
    public static void addBitlockerColumns(final SelectQuery selectQuery) {
        selectQuery.addJoin(new Join("ConfigDataItem", "BitlockerPolicyToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        selectQuery.addJoin(new Join("BitlockerPolicyToCfgDataItem", "BitlockerPolicy", new String[] { "BITLOCKER_POLICY_ID" }, new String[] { "BITLOCKER_POLICY_ID" }, 1));
        selectQuery.addJoin(new Join("BitlockerPolicy", "ADMXBackedPolicyGroup", new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicyGroup", "ADMXGroupToADMXPolicy", new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXGroupToADMXPolicy", "ADMXBackedPolicyConfig", new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicyConfig", "ADMXBackedPolicy", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicy", "ADMXBackedPolicyData", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicyConfig", "ADMXBackedPolicyDataConfig", new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, 1));
    }
    
    public static String getAppConfigPolicyCount(final SelectQuery selectQuery) throws Exception {
        final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONObject trackerProperties = new JSONObject();
        if (dataSet.next()) {
            final int OEMCount = (int)dataSet.getValue("OEM_COUNT");
            final int OEMTrashCount = (int)dataSet.getValue("OEM_TRASH_COUNT");
            trackerProperties.put("OEM_PROFILE_COUNT", OEMCount);
            trackerProperties.put("OEM_PROFILE_COUNT_TRASH", OEMTrashCount);
        }
        return trackerProperties.toString();
    }
    
    public static String getGroupActionCount(final SelectQuery selectQuery) throws Exception {
        final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONObject trackerProperties = new JSONObject();
        if (dataSet.next()) {
            trackerProperties.put("RESTART_GROUP_ACTION_COUNT", dataSet.getValue("RESTART_GROUP_ACTION_COUNT"));
            trackerProperties.put("RESET_APPS_GROUP_ACTION_COUNT", dataSet.getValue("RESET_APPS_GROUP_ACTION_COUNT"));
            trackerProperties.put("SCHEDULED_RESTART_GROUP_ACTION_COUNT", dataSet.getValue("RESTART_GROUP_ACTION_COUNT"));
            trackerProperties.put("DIFFRENT_TIMEZONE_GROUP_ACTION_COUNT", dataSet.getValue("DIFFRENT_TIMEZONE_GROUP_ACTION_COUNT"));
            trackerProperties.put("ONCE_GROUP_ACTION_COUNT", dataSet.getValue("ONCE_GROUP_ACTION_COUNT"));
            trackerProperties.put("REAPEAT_GROUP_ACTION_COUNT", dataSet.getValue("REAPEAT_GROUP_ACTION_COUNT"));
            trackerProperties.put("SHUTDOWN_GROUP_ACTION_COUNT", dataSet.getValue("SHUTDOWN_GROUP_ACTION_COUNT"));
            trackerProperties.put("SCHEDULED_SHUTDOWN_GROUP_ACTION_COUNT", dataSet.getValue("SCHEDULED_SHUTDOWN_GROUP_ACTION_COUNT"));
            trackerProperties.put("DIFFRENT_TIMEZONE_GROUP_ACTION_COUNT", dataSet.getValue("DIFFRENT_TIMEZONE_GROUP_ACTION_COUNT"));
        }
        return trackerProperties.toString();
    }
    
    public static String getDeviceActionCount(final SelectQuery selectQuery) throws Exception {
        final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONObject trackerProperties = new JSONObject();
        if (dataSet.next()) {
            trackerProperties.put("RESTART_DEVICE_ACTION_COUNT", dataSet.getValue("RESTART_DEVICE_ACTION_COUNT"));
            trackerProperties.put("SHUTDOWN_DEVICE_ACTION_COUNT", dataSet.getValue("SHUTDOWN_DEVICE_ACTION_COUNT"));
            trackerProperties.put("RESET_APPS_DEVICE_ACTION_COUNT", dataSet.getValue("RESET_APPS_DEVICE_ACTION_COUNT"));
        }
        return trackerProperties.toString();
    }
    
    static {
        MEMDMTrackerUtil.sourceClass = "MEMDMTrackerConstantsUtil";
        MEMDMTrackerUtil.logger = Logger.getLogger("METrackLog");
    }
}
