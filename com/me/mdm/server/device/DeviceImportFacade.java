package com.me.mdm.server.device;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Set;
import java.util.HashSet;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.error.APIError;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.simple.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DeviceImportFacade
{
    private Logger logger;
    
    public DeviceImportFacade() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    private List<String> getDeviceList(final Long customerID, final boolean is_sn_udid_optional) throws Exception {
        final List<String> deviceList = new ArrayList<String>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceListImportInfo"));
            sQuery.addSelectColumn(Column.getColumn("DeviceListImportInfo", "DEVICE_LIST_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceListImportInfo", "SERIAL_NUMBER"));
            sQuery.addSelectColumn(Column.getColumn("DeviceListImportInfo", "UDID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceListImportInfo", "CUSTOMER_ID"));
            sQuery.setCriteria(new Criteria(Column.getColumn("DeviceListImportInfo", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            int processed = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("DeviceList"), (long)customerID);
            if (countStr != null && !countStr.equals("0")) {
                this.clearCustomerParamsForDeviceCSVImport(customerID);
            }
            if (!dobj.isEmpty()) {
                final Iterator<Row> iter = dobj.getRows("DeviceListImportInfo");
                while (iter.hasNext()) {
                    final Row deviceListRow = iter.next();
                    String serialNo = (String)deviceListRow.get("SERIAL_NUMBER");
                    String udid = (String)deviceListRow.get("UDID");
                    if (is_sn_udid_optional) {
                        if ((serialNo != null && MDMStringUtils.isValidDeviceIdentifier(serialNo)) || (udid != null && MDMStringUtils.isValidDeviceIdentifier(udid))) {
                            if ((serialNo == null || !isValidSerialNo(serialNo)) && (udid == null || !isValidUDID(udid))) {
                                this.logger.log(Level.INFO, "serial number:{0} or udid:{1} is not valid", new Object[] { serialNo, udid });
                                throw new APIHTTPException("ENR00105", new Object[0]);
                            }
                            serialNo = ((serialNo != null) ? serialNo.trim() : null);
                            udid = ((udid != null) ? udid.trim() : null);
                            deviceList.add(serialNo + "@@@" + udid);
                        }
                    }
                    else if (MDMStringUtils.isValidDeviceIdentifier(serialNo) && MDMStringUtils.isValidDeviceIdentifier(udid)) {
                        if (!isValidSerialNo(serialNo) || !isValidUDID(udid)) {
                            this.logger.log(Level.INFO, "serial number:{0} or udid:{1} is not valid", new Object[] { serialNo, udid });
                            throw new APIHTTPException("ENR00105", new Object[0]);
                        }
                        deviceList.add(serialNo.trim() + "@@@" + udid.trim());
                    }
                    ++processed;
                }
                final int totalCount = dobj.size("DeviceListImportInfo");
                if (!is_sn_udid_optional) {
                    dobj.deleteRows("DeviceListImportInfo", new Criteria(Column.getColumn("DeviceListImportInfo", "CUSTOMER_ID"), (Object)customerID, 0));
                    MDMUtil.getPersistence().update(dobj);
                }
                CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("DeviceList"), String.valueOf(processed), (long)customerID);
                final JSONObject jsonObj = new JSONObject();
                jsonObj.put((Object)CSVProcessor.getFailedLabel("DeviceList"), (Object)(totalCount - processed));
                jsonObj.put((Object)CSVProcessor.getStatusLabel("DeviceList"), (Object)"COMPLETED");
                CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, (long)customerID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceList ", ex);
            throw ex;
        }
        return deviceList;
    }
    
    private static Boolean isValidSerialNo(final String serial) {
        final String serialNoPattern = "^[a-zA-Z0-9\\s\\+?!,()@%.\\-:_*\\./\\\\=]+$";
        final Pattern serialPattern = Pattern.compile(serialNoPattern);
        final Matcher matcher = serialPattern.matcher(serial);
        return matcher.matches();
    }
    
    private static Boolean isValidUDID(final String udid) {
        final String udidRegexPattern = "^[a-zA-Z0-9\\s\\+\\-_]+$";
        final Pattern udidPattern = Pattern.compile(udidRegexPattern);
        final Matcher matcher = udidPattern.matcher(udid);
        return matcher.matches();
    }
    
    public Map validateManagedDevices(final List<String> deviceList, final Long userID, final org.json.JSONObject body) throws Exception {
        final Map importDeviceResponse = new HashMap();
        final JSONArray succeededEntries = new JSONArray();
        final JSONArray unsupervisedEntries = new JSONArray();
        final JSONArray iosBelow_9_3Entries = new JSONArray();
        final JSONArray macEntries = new JSONArray();
        final JSONArray windowsEntries = new JSONArray();
        final JSONArray lastmodeEnabledEntries = new JSONArray();
        final JSONArray lastmodeDisabledEntries = new JSONArray();
        final List<String> validDevices = new ArrayList<String>();
        final JSONArray invalidDevices = new JSONArray();
        final DeviceFacade deviceFacade = new DeviceFacade();
        final List serialNumberList = new ArrayList();
        final List udidList = new ArrayList();
        final boolean is_sn_udid_optional = body.optBoolean("is_sn_udid_optional");
        final boolean is_mac_allowed = body.optBoolean("is_mac_allowed", (boolean)Boolean.TRUE);
        final boolean is_windows_allowed = body.optBoolean("is_windows_allowed", (boolean)Boolean.TRUE);
        final boolean is_unsupervised_allowed = body.optBoolean("is_unsupervised_allowed", (boolean)Boolean.TRUE);
        final boolean is_ios_below_9_3_allowed = body.optBoolean("is_ios_below_9_3_allowed", (boolean)Boolean.TRUE);
        final boolean is_lastmode_enabled_devices_allowed = body.optBoolean("is_lostmode_enabled_devices_allowed", (boolean)Boolean.TRUE);
        final boolean is_lastmode_disabled_devices_allowed = body.optBoolean("is_lostmode_disabled_devices_allowed", (boolean)Boolean.TRUE);
        final boolean isNotSupportedDevicesLostModeEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowNotSupportedDevicesLostMode");
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery deviceQuery = deviceFacade.getDevicesBaseQuery();
            deviceFacade.addSelectColForDevicesQuery(deviceQuery);
            deviceFacade.addCustomerFilter(deviceQuery, userID, null);
            for (final String device : deviceList) {
                final String[] records = device.split("@@@");
                if (records[0] != null) {
                    serialNumberList.add(records[0]);
                }
                if (records[1] != null) {
                    udidList.add(records[1]);
                }
            }
            final Criteria serialNumberCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)serialNumberList.toArray(), 8, (boolean)Boolean.FALSE);
            final Criteria udidCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udidList.toArray(), 8, (boolean)Boolean.FALSE);
            final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            if (is_sn_udid_optional) {
                deviceQuery.setCriteria(deviceQuery.getCriteria().and(managedCriteria.and(serialNumberCriteria.or(udidCriteria))));
            }
            else {
                deviceQuery.setCriteria(deviceQuery.getCriteria().and(managedCriteria.and(serialNumberCriteria).and(udidCriteria)));
            }
            deviceQuery.addSortColumn(new SortColumn("Resource", "CUSTOMER_ID", true));
            ds = DMDataSetWrapper.executeQuery((Object)deviceQuery);
            while (ds.next()) {
                if (ds.getValue("MANAGED_STATUS") != null) {
                    final org.json.JSONObject deviceJSON = new org.json.JSONObject();
                    final org.json.JSONObject user = new org.json.JSONObject();
                    final Integer enrollStatus = Integer.valueOf(String.valueOf(ds.getValue("MANAGED_STATUS")));
                    deviceJSON.put("managed_status", (Object)enrollStatus);
                    deviceJSON.put("customer_id", (Object)ds.getValue("CUSTOMER_ID").toString());
                    deviceJSON.put("device_id", (Object)ds.getValue("RESOURCE_ID").toString());
                    deviceJSON.put("udid", (Object)ds.getValue("UDID").toString());
                    deviceJSON.put("serial_number", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                    final boolean is_lostmode_enbaled = new LostModeDataHandler().isLostMode(Long.valueOf(String.valueOf(ds.getValue("RESOURCE_ID"))));
                    deviceJSON.put("is_lost_mode_enabled", is_lostmode_enbaled);
                    final int platformType = Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString());
                    deviceJSON.put("platform_type_id", platformType);
                    if (platformType == 1) {
                        deviceJSON.put("platform_type", (Object)"ios");
                    }
                    else if (platformType == 2) {
                        deviceJSON.put("platform_type", (Object)"android");
                    }
                    else if (platformType == 3) {
                        deviceJSON.put("platform_type", (Object)"windows");
                    }
                    else if (platformType == 4) {
                        deviceJSON.put("platform_type", (Object)"chrome");
                    }
                    else if (platformType == 6) {
                        deviceJSON.put("platform_type", (Object)"mac");
                    }
                    deviceJSON.put("model", ds.getValue("MODEL"));
                    deviceJSON.put("device_type", ds.getValue("MODEL_TYPE"));
                    deviceJSON.put("owned_by", ds.getValue("OWNED_BY"));
                    deviceJSON.put("product_name", ds.getValue("PRODUCT_NAME"));
                    deviceJSON.put("os_version", ds.getValue("OS_VERSION"));
                    deviceJSON.put("device_capacity", ds.getValue("DEVICE_CAPACITY"));
                    deviceJSON.put("device_name", ds.getValue("DEVICE_NAME"));
                    deviceJSON.put("last_contact_time", ds.getValue("LAST_CONTACT_TIME"));
                    user.put("user_email", ds.getValue("EMAIL_ADDRESS"));
                    user.put("user_name", ds.getValue("NAME"));
                    user.put("user_id", (Object)String.valueOf(ds.getValue("MANAGED_USER_ID")));
                    deviceJSON.put("user", (Object)user);
                    deviceJSON.put("is_removed", (Object)"false");
                    deviceJSON.put("located_time", ds.getValue("LOCATED_TIME"));
                    final JSONArray imeiArray = new JSONArray();
                    if (ds.getValue("PRIMARY_IMEI") != null) {
                        imeiArray.put(ds.getValue("PRIMARY_IMEI"));
                    }
                    if (ds.getValue("SECONDRY_IMEI") != null) {
                        imeiArray.put(ds.getValue("SECONDRY_IMEI"));
                    }
                    if (imeiArray.length() > 0) {
                        deviceJSON.put("imei", (Object)imeiArray);
                    }
                    final String osVersion = (String)ds.getValue("OS_VERSION");
                    final boolean isSupervised = ds.getValue("IS_SUPERVISED") != null && (boolean)ds.getValue("IS_SUPERVISED");
                    final boolean isHigher = osVersion.equals("9.3") || new VersionChecker().isGreater(osVersion, "9.3");
                    if (!isNotSupportedDevicesLostModeEnabled && platformType == 1 && !isHigher && !is_ios_below_9_3_allowed) {
                        deviceJSON.put("UDID", (Object)ds.getValue("UDID").toString());
                        deviceJSON.put("SERIAL_NUMBER", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                        iosBelow_9_3Entries.put((Object)deviceJSON);
                    }
                    else if (!isNotSupportedDevicesLostModeEnabled && platformType == 1 && !isSupervised && !is_unsupervised_allowed) {
                        deviceJSON.put("UDID", (Object)ds.getValue("UDID").toString());
                        deviceJSON.put("SERIAL_NUMBER", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                        unsupervisedEntries.put((Object)deviceJSON);
                    }
                    else if (!is_mac_allowed && deviceJSON.getString("platform_type").equals("mac")) {
                        deviceJSON.put("UDID", (Object)ds.getValue("UDID").toString());
                        deviceJSON.put("SERIAL_NUMBER", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                        macEntries.put((Object)deviceJSON);
                    }
                    else if (!is_windows_allowed && deviceJSON.getString("platform_type").equals("windows")) {
                        deviceJSON.put("UDID", (Object)ds.getValue("UDID").toString());
                        deviceJSON.put("SERIAL_NUMBER", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                        windowsEntries.put((Object)deviceJSON);
                    }
                    else if (!is_lastmode_disabled_devices_allowed && !is_lostmode_enbaled) {
                        deviceJSON.put("UDID", (Object)ds.getValue("UDID").toString());
                        deviceJSON.put("SERIAL_NUMBER", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                        lastmodeDisabledEntries.put((Object)deviceJSON);
                    }
                    else if (!is_lastmode_enabled_devices_allowed && is_lostmode_enbaled) {
                        deviceJSON.put("UDID", (Object)ds.getValue("UDID").toString());
                        deviceJSON.put("SERIAL_NUMBER", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                        lastmodeEnabledEntries.put((Object)deviceJSON);
                    }
                    else {
                        succeededEntries.put((Object)deviceJSON);
                    }
                    if (is_sn_udid_optional) {
                        if (deviceList.contains(ds.getValue("SERIAL_NUMBER").toString() + "@@@" + (Object)null)) {
                            validDevices.add(ds.getValue("SERIAL_NUMBER").toString() + "@@@" + (Object)null);
                        }
                        else if (deviceList.contains((Object)null + "@@@" + ds.getValue("UDID").toString())) {
                            validDevices.add((Object)null + "@@@" + ds.getValue("UDID").toString());
                        }
                        else {
                            validDevices.add(ds.getValue("SERIAL_NUMBER").toString() + "@@@" + ds.getValue("UDID").toString());
                        }
                    }
                    else {
                        validDevices.add(ds.getValue("SERIAL_NUMBER").toString() + "@@@" + ds.getValue("UDID").toString());
                    }
                }
            }
            deviceList.removeAll(validDevices);
            for (final String device2 : deviceList) {
                final String[] records2 = device2.split("@@@");
                final org.json.JSONObject deviceData = new org.json.JSONObject();
                if (records2[0] != null) {
                    deviceData.put("SERIAL_NUMBER", (Object)records2[0]);
                }
                if (records2[1] != null) {
                    deviceData.put("UDID", (Object)records2[1]);
                }
                invalidDevices.put((Object)deviceData);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in validateManagedDevices ", ex);
            throw ex;
        }
        importDeviceResponse.put("succeededEntries", succeededEntries);
        importDeviceResponse.put("notFoundData", invalidDevices);
        importDeviceResponse.put("macEntries", macEntries);
        importDeviceResponse.put("windowsEntries", windowsEntries);
        importDeviceResponse.put("iosBelow_9_3Entries", iosBelow_9_3Entries);
        importDeviceResponse.put("unsupervisedEntries", unsupervisedEntries);
        importDeviceResponse.put("lostmodeEnabledEntries", lastmodeEnabledEntries);
        importDeviceResponse.put("lostmodeDisabledEntries", lastmodeDisabledEntries);
        importDeviceResponse.put("error_csv", "/bulkDeviceListErrorDetails.csv?fileName=ErrorReport");
        return importDeviceResponse;
    }
    
    public Map validateDevices(final org.json.JSONObject body, final Long userID) throws APIHTTPException {
        Map csvImportDeviceStatus = null;
        try {
            final Long csvFileID = Long.valueOf(String.valueOf(body.optString("csv_file")));
            final String csvFile = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", csvFileID)).get("file_path"));
            final boolean bomPresent = MDMEnrollmentRequestHandler.getInstance().isBOMPresent(ApiFactoryProvider.getFileAccessAPI().getInputStream(csvFile));
            final boolean isUTFencoding = MDMEnrollmentRequestHandler.getInstance().isUTFencoding(ApiFactoryProvider.getFileAccessAPI().getInputStream(csvFile));
            if (!isUTFencoding || (isUTFencoding && bomPresent)) {
                throw new APIHTTPException("CSV0001", new Object[0]);
            }
            final Long customerID = (Long)DBUtil.getFirstValueFromDBWithOutCriteria("CustomerInfo", "CUSTOMER_ID");
            final String className = (String)DBUtil.getValueFromDB("CSVOperation", "LABEL", (Object)"DeviceList", "PARSER_CLASS");
            final CSVProcessor reader = (CSVProcessor)Class.forName(className).newInstance();
            final JSONObject csvReadResponse = reader.persistCSVFile(ApiFactoryProvider.getFileAccessAPI().getInputStream(csvFile), (JSONObject)null, customerID);
            if (csvReadResponse.containsKey((Object)"STATUS") && csvReadResponse.get((Object)"STATUS").equals("FAILURE")) {
                if (csvReadResponse.containsKey((Object)"CODE")) {
                    final APIError error = new APIError();
                    error.setErrorCode("CSV0002");
                    error.setI18nKey(String.valueOf(csvReadResponse.get((Object)"CAUSE")));
                    error.setErrorMsg(I18N.getLocale());
                    error.setHttpStatus(400);
                    throw new APIHTTPException(error);
                }
                throw new APIHTTPException("CSV0003", new Object[0]);
            }
            else {
                final boolean is_sn_udid_optional = body.optBoolean("is_sn_udid_optional");
                final List<String> deviceList = this.getDeviceList(customerID, is_sn_udid_optional);
                final Set<String> deviceSet = new HashSet<String>();
                final JSONArray duplicateArray = new JSONArray();
                for (final String device : deviceList) {
                    boolean duplicate_device = Boolean.FALSE;
                    final String[] records = device.split("@@@");
                    for (final String uni_device : deviceSet) {
                        if ((records[0] != null && !records[0].equals("null") && uni_device.indexOf(records[0]) == 0) || (records[1] != null && !records[1].equals("null") && uni_device.indexOf(records[1]) > 0)) {
                            duplicate_device = Boolean.TRUE;
                            break;
                        }
                    }
                    if (duplicate_device) {
                        final org.json.JSONObject duplicateDevice = new org.json.JSONObject();
                        duplicateDevice.put("SERIAL_NUMBER", (Object)records[0]);
                        duplicateDevice.put("UDID", (Object)records[1]);
                        duplicateArray.put((Object)duplicateDevice);
                    }
                    else {
                        deviceSet.add(device);
                    }
                }
                final List<String> uniqueDeviceList = new ArrayList<String>(deviceSet);
                final int duplicateCount = deviceList.size() - uniqueDeviceList.size();
                csvImportDeviceStatus = this.validateManagedDevices(uniqueDeviceList, userID, body);
                csvImportDeviceStatus.put("duplicateCount", duplicateCount);
                csvImportDeviceStatus.put("duplicateEntries", duplicateArray);
                this.updateErrorDeviceDetails(csvImportDeviceStatus, customerID);
                if (!is_sn_udid_optional) {
                    this.clearCustomerParamsForDeviceCSVImport(customerID);
                }
                else {
                    this.deleteSucceededEntries(customerID);
                }
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return csvImportDeviceStatus;
    }
    
    private void clearCustomerParamsForDeviceCSVImport(final Long customerID) {
        try {
            final DeleteQuery customParamQuery = (DeleteQuery)new DeleteQueryImpl("CustomerParams");
            final Criteria paramNamesCriteria = new Criteria(new Column("CustomerParams", "PARAM_NAME"), (Object)new String[] { CSVProcessor.getProcessedLabel("DeviceList"), CSVProcessor.getFailedLabel("DeviceList"), CSVProcessor.getStatusLabel("DeviceList") }, 8);
            final Criteria customerCriteria = new Criteria(new Column("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
            customParamQuery.setCriteria(paramNamesCriteria.and(customerCriteria));
            DataAccess.delete(customParamQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in clearCustomerParams of DeviceImportFacade", e);
        }
    }
    
    private void updateErrorDeviceDetails(final Map csvImportDeviceStatus, final Long customerID) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceListImportInfo"));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("DeviceListImportInfo", "DEVICE_LIST_ID"), true));
            sQuery.addSelectColumn(Column.getColumn("DeviceListImportInfo", "*"));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            JSONArray deviceList = new JSONArray();
            final Iterator keySet = csvImportDeviceStatus.keySet().iterator();
            String errorInfo = "";
            int failedCount = 0;
            while (keySet.hasNext()) {
                final String deviceDetailKey = keySet.next();
                if (deviceDetailKey.equalsIgnoreCase("notFoundData")) {
                    errorInfo = "mdm.invalid_SN_UDID";
                }
                else if (deviceDetailKey.equalsIgnoreCase("duplicateEntries")) {
                    errorInfo = "mdm.duplicate_device_entry";
                }
                else if (deviceDetailKey.equalsIgnoreCase("windowsEntries")) {
                    errorInfo = "mdm.lostmode_windows_not_allowed";
                }
                else if (deviceDetailKey.equalsIgnoreCase("macEntries")) {
                    errorInfo = "mdm.lostmode_mac_not_allowed";
                }
                else if (deviceDetailKey.equalsIgnoreCase("unsupervisedEntries")) {
                    errorInfo = "mdm.lostmode_unsupervised_not_allowed";
                }
                else if (deviceDetailKey.equalsIgnoreCase("iosBelow_9_3Entries")) {
                    errorInfo = "mdm.lostmode_ios_below_9_3_not_allowed";
                }
                else if (deviceDetailKey.equalsIgnoreCase("lostmodeEnabledEntries")) {
                    errorInfo = "mdm.lostmode_already_enabled";
                }
                else {
                    if (!deviceDetailKey.equalsIgnoreCase("lostmodeDisabledEntries")) {
                        continue;
                    }
                    errorInfo = "mdm.lostmode_already_disabled";
                }
                deviceList = csvImportDeviceStatus.get(deviceDetailKey);
                failedCount += deviceList.length();
                for (int i = 0; i < deviceList.length(); ++i) {
                    final org.json.JSONObject device = deviceList.getJSONObject(i);
                    final String serialNumber = device.getString("SERIAL_NUMBER");
                    final String udid = device.getString("UDID");
                    Criteria criteria = new Criteria(Column.getColumn("DeviceListImportInfo", "ERROR_REMARKS"), (Object)null, 0);
                    if ((serialNumber != null && !serialNumber.equals("null")) || errorInfo.equals("mdm.duplicate_device_entry")) {
                        criteria = criteria.and(new Criteria(Column.getColumn("DeviceListImportInfo", "SERIAL_NUMBER"), (Object)serialNumber, 0).or(new Criteria(Column.getColumn("DeviceListImportInfo", "SERIAL_NUMBER"), (Object)null, 0)));
                    }
                    if ((udid != null && !udid.equals("null")) || errorInfo.equals("mdm.duplicate_device_entry")) {
                        criteria = criteria.and(new Criteria(Column.getColumn("DeviceListImportInfo", "UDID"), (Object)udid, 0).or(new Criteria(Column.getColumn("DeviceListImportInfo", "UDID"), (Object)null, 0)));
                    }
                    final Row r = dobj.getRow("DeviceListImportInfo", criteria);
                    r.set("ERROR_REMARKS", (Object)errorInfo);
                    dobj.updateRow(r);
                }
            }
            SyMUtil.getPersistence().update(dobj);
            CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getFailedLabel("DeviceList"), String.valueOf(failedCount), (long)customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updating failure device details", e);
        }
    }
    
    public void deleteSucceededEntries(final Long customerId) {
        try {
            Criteria criteria = new Criteria(Column.getColumn("DeviceListImportInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceListImportInfo", "ERROR_REMARKS"), (Object)null, 0));
            final DataObject dObj = MDMUtil.getPersistence().get("DeviceListImportInfo", criteria);
            dObj.deleteRows("DeviceListImportInfo", criteria);
            MDMUtil.getPersistence().update(dObj);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteSucceededEntries....", e);
        }
    }
}
