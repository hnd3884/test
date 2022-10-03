package com.me.mdm.server.device;

import java.util.Hashtable;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.ds.query.UnionQuery;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.api.command.CommandFacade;
import com.me.devicemanagement.framework.server.common.ErrorCodeHandler;
import com.me.mdm.server.command.CommandStatusHandler;
import com.me.mdm.api.command.CommandAPIHandler;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.export.ExportRequestDetailsHandler;
import com.me.mdm.server.profiles.ProfileFacade;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.profiles.ios.DeviceConfigPayloadsDataHandler;
import com.me.mdm.server.inv.ios.DeviceInstalledCertificateDataHandler;
import com.me.mdm.server.apps.provisioningprofiles.DeviceProvProfilesDataHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.mdm.server.customgroup.GroupFacade;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultRecoveryKeyHander;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Calendar;
import com.me.mdm.server.location.LocationDataHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.me.mdm.server.settings.location.LocationErrorFactors;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Arrays;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Map;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.device.resource.Device;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.google.gson.Gson;
import java.util.logging.Logger;

public class DeviceFacade
{
    protected static Logger logger;
    private Gson gson;
    
    public DeviceFacade() {
        this.gson = new Gson();
    }
    
    public Object getDevice(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Boolean deviceSummary = message.getJSONObject("msg_header").getJSONObject("filters").optBoolean("summary", false);
            return this.getDevice(deviceId, APIUtil.getCustomerID(message), deviceSummary);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in getDevice", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDevice(final Long deviceId, final Long customerID) {
        return this.getDevice(deviceId, customerID, false);
    }
    
    public JSONObject getDevice(final Long deviceId, final Long customerID, final boolean deviceSummary) {
        try {
            this.validateIfDeviceExists(deviceId, customerID);
            JSONObject temp = InventoryUtil.getInstance().getDeviceDetails(deviceId);
            if (temp.has("DEVICE_OWNED_BY") && temp.getInt("OWNED_BY") == 0) {
                temp.put("OWNED_BY", temp.get("DEVICE_OWNED_BY"));
            }
            temp = this.convertKeysToLowerCase(temp);
            this.removeEmptyKeys(temp);
            final Device device = (Device)this.gson.fromJson(temp.toString(), (Class)Device.class);
            device.setIOSNativeAppRegistered(IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(device.getResourceId()));
            final JSONObject response = new JSONObject(this.gson.toJson((Object)device));
            final LostModeDataHandler lostModeDataHandler = new LostModeDataHandler();
            response.put("owned_by", MDMEnrollmentUtil.getInstance().getOwnedBy(deviceId));
            response.put("notification_service_type", MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(device.getPlatformType(), customerID));
            response.put("is_lost_mode_enabled", lostModeDataHandler.isLostMode(deviceId));
            response.put("lost_mode_status", lostModeDataHandler.getLostModeStatus(deviceId));
            response.put("managed_status", (Object)ManagedDeviceHandler.getInstance().getManagedDeviceStatus(deviceId));
            response.put("last_scan_time", (Object)MDMUtil.getInstance().getLastScanTime(deviceId));
            response.put("privacy_settings", (Map)new PrivacySettingsHandler().getPrivacySettingsForMdDevices(deviceId));
            response.put("remote_settings_enabled", MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerID));
            response.put("location_settings", (Object)LocationSettingsDataHandler.getInstance().getLocationSettingsForDevice(deviceId));
            response.put("is_mail_server_enabled", ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured());
            response.put("last_contact_time", (Object)MDMUtil.getInstance().getLastContactTime(deviceId));
            if (deviceSummary) {
                final Table managedDeviceTable = Table.getTable("ManagedDevice");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(managedDeviceTable);
                selectQuery.addJoin(new Join("ManagedDevice", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "PROFILE_COUNT"));
                selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "APP_COUNT"));
                selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "DOC_COUNT"));
                SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
                subSQ.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
                final Column column_config_data = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID").count();
                column_config_data.setColumnAlias("GROUP_RESOURCE_ID");
                final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                subSQ.addJoin(customGroupJoin);
                subSQ.setCriteria(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)9, 1));
                subSQ = RBDAUtil.getInstance().getRBDAQuery(subSQ);
                subSQ.addSelectColumn(column_config_data);
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
                list.add(groupByCol);
                final GroupByClause memberGroupBy = new GroupByClause(list);
                subSQ.setGroupByClause(memberGroupBy);
                final DerivedTable groupDerievedTab = new DerivedTable("CustomGroupMemberRel", (Query)subSQ);
                selectQuery.addJoin(new Join(managedDeviceTable, (Table)groupDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                final Column maxCountCol = new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
                maxCountCol.setColumnAlias("GROUP_COUNT");
                selectQuery.addSelectColumn(maxCountCol);
                final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
                selectQuery.setCriteria(criteria);
                final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
                if (ds.next()) {
                    final JSONObject summary = new JSONObject();
                    summary.put("profile_count", (ds.getValue("PROFILE_COUNT") != null) ? ds.getValue("PROFILE_COUNT") : Integer.valueOf(0));
                    summary.put("app_count", (ds.getValue("APP_COUNT") != null) ? ds.getValue("APP_COUNT") : Integer.valueOf(0));
                    summary.put("doc_count", (ds.getValue("DOC_COUNT") != null) ? ds.getValue("DOC_COUNT") : Integer.valueOf(0));
                    summary.put("group_count", ds.getValue("GROUP_COUNT"));
                    response.put("summary", (Object)summary);
                }
            }
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
                final SelectQuery selectQuery2 = this.getEnrollmentRequestDetail(deviceId);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery2);
                if (!dataObject.isEmpty()) {
                    final Row deviceEnrollmentrow = dataObject.getRow("DeviceEnrollmentRequest");
                    if (deviceEnrollmentrow != null) {
                        response.put("enrollment_request_time", deviceEnrollmentrow.get("REQUESTED_TIME"));
                    }
                    final Row enrollemntTempleteRow = dataObject.getRow("EnrollmentTemplate");
                    if (enrollemntTempleteRow != null) {
                        response.put("enrollment_type", enrollemntTempleteRow.get("TEMPLATE_TYPE"));
                    }
                }
            }
            return response;
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, "Exception in getDevice() -- ", e);
            return new JSONObject();
        }
    }
    
    private JSONObject convertKeysToLowerCase(final JSONObject json) throws JSONException {
        final JSONObject result = new JSONObject();
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            Object tempObj = null;
            if (json.get(key) instanceof JSONObject) {
                tempObj = this.convertKeysToLowerCase(json.getJSONObject(key));
            }
            else if (json.get(key) instanceof JSONArray) {
                tempObj = this.convertKeysToLowerCase(json.getJSONArray(key));
            }
            else {
                result.put(key.toLowerCase(), json.get(key));
            }
            if (tempObj != null) {
                result.put(key.toLowerCase(), tempObj);
            }
        }
        return result;
    }
    
    private JSONArray convertKeysToLowerCase(final JSONArray json) throws JSONException {
        final JSONArray result = new JSONArray();
        for (int i = 0; i < json.length(); ++i) {
            if (json.get(i) instanceof JSONObject) {
                result.put((Object)this.convertKeysToLowerCase(json.getJSONObject(i)));
            }
            else if (json.get(i) instanceof JSONArray) {
                result.put((Object)this.convertKeysToLowerCase(json.getJSONArray(i)));
            }
            else {
                result.put(json.get(i));
            }
        }
        return result;
    }
    
    public Object getDeviceSummary(final JSONObject message) throws APIHTTPException {
        Long deviceId;
        try {
            deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final JSONObject response = InventoryUtil.getInstance().getSummaryDetails(deviceId);
        this.removeEmptyKeys(response);
        return response;
    }
    
    private void removeEmptyKeys(final JSONObject obj) throws APIHTTPException {
        final Iterator<String> iterator = obj.keys();
        try {
            while (iterator.hasNext()) {
                final String key = iterator.next();
                final Object temp = obj.get(key);
                if (temp instanceof JSONArray) {
                    final JSONArray tempObj = (JSONArray)temp;
                    for (int i = 0; i < tempObj.length(); ++i) {
                        final Object innerObject = tempObj.get(i);
                        if (innerObject instanceof JSONObject) {
                            this.removeEmptyKeys((JSONObject)innerObject);
                        }
                    }
                }
                else if (temp instanceof JSONObject) {
                    this.removeEmptyKeys((JSONObject)temp);
                }
                else {
                    if (!temp.equals("--")) {
                        continue;
                    }
                    iterator.remove();
                }
            }
        }
        catch (final JSONException e) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in removeEmptyKeys", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getDeviceHardware(final JSONObject message) throws APIHTTPException {
        Long deviceId;
        try {
            deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final JSONObject response = InventoryUtil.getInstance().getDeviceHardwareDetails(deviceId);
        this.removeEmptyKeys(response);
        return response;
    }
    
    public SelectQuery getDeviceValidationQuery(final Collection<Long> deviceIDs, final Long customerID) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceIDs.toArray(new Long[deviceIDs.size()]), 8)));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return selectQuery;
    }
    
    public HashMap validateIfDevicesExists(Collection<Long> deviceIDs, final Long customerID) throws APIHTTPException {
        try {
            deviceIDs = new HashSet<Long>(deviceIDs);
            final DataObject dataObject = DataAccess.get(this.getDeviceValidationQuery(deviceIDs, customerID));
            final Iterator<Row> rows = dataObject.getRows("ManagedDevice");
            final ArrayList<Long> devices = new ArrayList<Long>();
            final HashMap<Integer, ArrayList> platformDeviceMap = new HashMap<Integer, ArrayList>();
            while (rows.hasNext()) {
                final Row managedDeviceRow = rows.next();
                final Integer platformType = (Integer)managedDeviceRow.get("PLATFORM_TYPE");
                final Long deviceId = Long.valueOf(String.valueOf(managedDeviceRow.get("RESOURCE_ID")));
                devices.add(deviceId);
                ArrayList deviceIds = new ArrayList();
                if (platformDeviceMap.containsKey(platformType)) {
                    deviceIds = platformDeviceMap.get(platformType);
                }
                deviceIds.add(deviceId);
                platformDeviceMap.put(platformType, deviceIds);
            }
            deviceIDs.removeAll(devices);
            if (deviceIDs.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(deviceIDs) });
            }
            return platformDeviceMap;
        }
        catch (final DataAccessException ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Issue on validating device ids", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public HashMap getProfilePLatformDeviceMap(Collection<Long> deviceIDs, final Long customerID) throws APIHTTPException {
        try {
            deviceIDs = new HashSet<Long>(deviceIDs);
            final DataObject dataObject = DataAccess.get(this.getDeviceValidationQuery(deviceIDs, customerID));
            final Iterator<Row> rows = dataObject.getRows("ManagedDevice");
            final ArrayList<Long> devices = new ArrayList<Long>();
            final HashMap<Integer, ArrayList> platformDeviceMap = new HashMap<Integer, ArrayList>();
            while (rows.hasNext()) {
                final Row managedDeviceRow = rows.next();
                Integer platformType = (Integer)managedDeviceRow.get("PLATFORM_TYPE");
                final Long deviceId = Long.valueOf(String.valueOf(managedDeviceRow.get("RESOURCE_ID")));
                final Long modelID = (Long)dataObject.getRow("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)deviceId, 0)).get("MODEL_ID");
                final Integer modelType = (Integer)dataObject.getRow("MdModelInfo", new Criteria(Column.getColumn("MdModelInfo", "MODEL_ID"), (Object)modelID, 0)).get("MODEL_TYPE");
                if (platformType.equals(1)) {
                    platformType = ((modelType == 3 || modelType == 4) ? 6 : ((modelType == 5) ? 7 : 1));
                }
                devices.add(deviceId);
                ArrayList deviceIds = new ArrayList();
                if (platformDeviceMap.containsKey(platformType)) {
                    deviceIds = platformDeviceMap.get(platformType);
                }
                deviceIds.add(deviceId);
                platformDeviceMap.put(platformType, deviceIds);
            }
            deviceIDs.removeAll(devices);
            if (deviceIDs.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(deviceIDs) });
            }
            return platformDeviceMap;
        }
        catch (final DataAccessException ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Issue on validating device ids", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void validateIfDeviceExists(final Long deviceID, final Long customerID) throws APIHTTPException {
        if (deviceID == null || deviceID == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final DataObject dataObject = DataAccess.get(this.getDeviceValidationQuery(new ArrayList<Long>(Arrays.asList(deviceID)), customerID));
            final Iterator<Row> rows = dataObject.getRows("ManagedDevice");
            final ArrayList<Long> devices = new ArrayList<Long>();
            while (rows.hasNext()) {
                devices.add(Long.valueOf(String.valueOf(rows.next().get("RESOURCE_ID"))));
            }
            if (devices.size() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { deviceID });
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addCustomerFilter(final SelectQuery query, final Long userID, final Long customerID) {
        Criteria cri = null;
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            query.addJoin(new Join("Resource", "LoginUserCustomerMapping", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            cri = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0);
        }
        if (customerID != null) {
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            if (cri == null) {
                cri = customerCri;
            }
            else {
                cri = cri.and(customerCri);
            }
        }
        if (query.getCriteria() != null) {
            cri = query.getCriteria().and(cri);
        }
        query.setCriteria(cri);
    }
    
    public JSONArray validateandGetDeviceDetails(final List deviceList, final Long customerID, final Long userID) throws APIHTTPException {
        try {
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Join join = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            selectQuery.addJoin(join);
            this.addCustomerFilter(selectQuery, userID, customerID);
            final Criteria cri = selectQuery.getCriteria().and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(new Long[deviceList.size()]), 8));
            selectQuery.setCriteria(cri);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator rows = dataObject.getRows("ManagedDevice");
            final JSONArray device_list = new JSONArray();
            while (rows.hasNext()) {
                final JSONObject device_details = new JSONObject();
                final Row row = rows.next();
                final Long deviceID = (Long)row.get("RESOURCE_ID");
                device_details.put("device_id", (Object)String.valueOf(deviceID));
                device_details.put("platform_type_id", row.get("PLATFORM_TYPE"));
                device_details.put("device_name", dataObject.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)deviceID, 0)).get("NAME"));
                device_details.put("udid", (Object)String.valueOf(row.get("UDID")));
                device_details.put("customer_id", (Object)String.valueOf(dataObject.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceID, 0)).get("CUSTOMER_ID")));
                device_list.put((Object)device_details);
                deviceList.remove(deviceID);
            }
            if (deviceList.size() == 0) {
                return device_list;
            }
            throw new APIHTTPException("COM0008", new Object[] { deviceList.toString() });
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in validating devices", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getDeviceLocations(final JSONObject message) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Boolean lastKnownLocation = message.getJSONObject("msg_header").getJSONObject("filters").optBoolean("last_known", false);
            Boolean lastLocationOnly = lastKnownLocation || (!LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition() && LicenseProvider.getInstance().getEvaluationDays() <= 1L);
            final Integer noOfDays = message.getJSONObject("msg_header").getJSONObject("filters").optInt("no_of_days", 3);
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            final int locationTrackingStatus = LocationSettingsDataHandler.getInstance().getLocationTrackingStatus(APIUtil.getCustomerID(message));
            final boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final int lostModeStatus = new LostModeDataHandler().getLostModeStatus(deviceId);
            final ArrayList<Integer> locationHistoryAllowedStatus = new ArrayList<Integer>(Arrays.asList(2, 1, 3, 6, 4));
            if (!isProfessional && !locationHistoryAllowedStatus.contains(lostModeStatus)) {
                lastLocationOnly = true;
            }
            else if (locationHistoryAllowedStatus.contains(lostModeStatus)) {
                lastLocationOnly = false;
            }
            if (locationTrackingStatus == 3) {
                throw new APIHTTPException("LOC0006", new Object[0]);
            }
            if (locationTrackingStatus == 2 && !new LostModeDataHandler().isLostMode(deviceId)) {
                throw new APIHTTPException("LOC0005", new Object[0]);
            }
            if (!LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(deviceId)) {
                throw new APIHTTPException("LOC0006", new Object[0]);
            }
            final HashMap errorMap = new LocationErrorFactors(deviceId, customerId).getDeviceGeoTrackingErrorRenderingProperties();
            final boolean isEmpty = MDMGeoLocationHandler.getInstance().isLocationDataEmptyForDevice(deviceId, customerId, lostModeStatus);
            if (isEmpty && errorMap != null && !errorMap.isEmpty()) {
                throw new APIHTTPException("LOC0001", new Object[] { errorMap.get("geoErrorDescription") });
            }
            final LocationDataHandler locationDataHandler = LocationDataHandler.getInstance();
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("RESOURCE_ID", (Object)deviceId);
            if (lastLocationOnly) {
                requestJSON.put("START_INDEX", 0);
                requestJSON.put("MAX_LOCATIONS_DATA", 1);
            }
            else {
                requestJSON.put("START_INDEX", -1);
                requestJSON.put("MAX_LOCATIONS_DATA", -1);
            }
            final JSONObject locations = locationDataHandler.requestDeviceLocations(requestJSON).getJSONObject("LOCATIONS");
            final Iterator<String> keys = locations.keys();
            final JSONArray locationsJSON = new JSONArray();
            final Calendar cal = Calendar.getInstance();
            cal.add(6, -1 * noOfDays);
            final long nDaysAgo = cal.getTimeInMillis();
            while (keys.hasNext()) {
                final String key = keys.next();
                JSONObject location = locations.getJSONObject(key);
                if (!lastLocationOnly && location.getLong("ADDED_TIME") < nDaysAgo) {
                    continue;
                }
                location = JSONUtil.getInstance().changeJSONKeyCase(location, 2);
                locationsJSON.put((Object)location);
            }
            final JSONObject response = new JSONObject();
            response.put("error_map", (Map)MDMGeoLocationHandler.getInstance().getLocationErrorMap(deviceId));
            if (locationsJSON.length() == 0) {
                response.put("locations", (Object)locationsJSON);
                return response;
            }
            response.put("locations", (Object)locationsJSON);
            return response;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getDeviceRestrictions(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            return InventoryUtil.getInstance().getDeviceRestrictions(deviceId).get("restrictions");
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in getDeviceRestrictions", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getDeviceNetwork(final JSONObject message) throws APIHTTPException {
        Long deviceId;
        try {
            deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return InventoryUtil.getInstance().getDeviceNetworkDetails(deviceId);
    }
    
    public Object getDeviceSIM(final JSONObject message) throws APIHTTPException {
        Long deviceId;
        try {
            deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final JSONObject response = InventoryUtil.getInstance().getDeviceSIMDetails(deviceId);
        this.removeEmptyKeys(response);
        return response;
    }
    
    public Object getFileValutPersonalKeyInfo(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            String prk = MDMFileVaultRecoveryKeyHander.getFileVaultRecoveryKey(deviceId);
            final DeviceDetails deviceDetail = new DeviceDetails(deviceId);
            if (!MDMStringUtils.isEmpty(prk)) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2141, deviceId, MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo().get("UserName"), "mdm.profile.filevault.personal_key_audit", deviceDetail.name + "@@@" + deviceDetail.udid + "@@@" + deviceDetail.serialNumber, deviceDetail.customerId);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"get-success");
                logJSON.put((Object)"RESOURCE_ID", (Object)deviceDetail.resourceId);
                logJSON.put((Object)"NAME", (Object)deviceDetail.name);
                MDMOneLineLogger.log(Level.INFO, "VIEW_FILEVAULT_PERSONAL_KEY", logJSON);
            }
            else {
                prk = I18N.getMsg("mdm.policy.keyNotRetrieved", new Object[0]);
            }
            JSONObject response = new JSONObject();
            response.put("PERSONAL_RECOVERY_KEY", (Object)prk);
            response.put("RESOURCE_ID", (Object)deviceId);
            response = JSONUtil.getInstance().convertLongToString(response);
            return response;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getFileValutInstitutionalKeyInfo(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long customerID = APIUtil.getCustomerID(message);
            this.validateIfDeviceExists(deviceId, customerID);
            final Long certificateID = MDMFileVaultRecoveryKeyHander.getFileVaultInstitutionalRecoveryCert(deviceId);
            final DeviceDetails deviceDetail = new DeviceDetails(deviceId);
            final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(customerID, certificateID);
            final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
            final String certPassword = (String)certRow.get("CERTIFICATE_PASSWORD");
            final String certSerialNumber = (String)certRow.get("CERTIFICATE_SERIAL_NUMBER");
            final String displayName = (String)certRow.get("CERTIFICATE_DISPLAY_NAME");
            final String certSubjectDN = (String)certRow.get("CERTIFICATE_SUBJECT_DN");
            final String certIssueDN = (String)certRow.get("CERTIFICATE_ISSUER_DN");
            if (certificateID != null) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2142, deviceId, MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo().get("UserName"), "mdm.profile.filevault.inst_key_audit", displayName + "@@@" + certSubjectDN + "@@@" + certIssueDN + "@@@" + certSerialNumber, deviceDetail.customerId);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"get-success");
                logJSON.put((Object)"RESOURCE_ID", (Object)deviceDetail.resourceId);
                logJSON.put((Object)"NAME", (Object)deviceDetail.name);
                MDMOneLineLogger.log(Level.INFO, "VIEW_FILEVAULT_INSTITUTIONAL_KEY", logJSON);
            }
            JSONObject response = new JSONObject();
            response.put("CERTIFICATE_PASSWORD", (Object)certPassword);
            response.put("CERTIFICATE_ID", (Object)certificateID);
            response.put("RESOURCE_ID", (Object)deviceId);
            response = JSONUtil.getInstance().convertLongToString(response);
            return response;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getDevices(final JSONObject request) throws APIHTTPException {
        final JSONArray result = new JSONArray();
        final JSONObject response = new JSONObject();
        Connection conn = null;
        DataSet ds = null;
        try {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            Long customerID;
            try {
                customerID = APIUtil.getCustomerID(request);
            }
            catch (final APIHTTPException e) {
                if (!ApiFactoryProvider.getUtilAccessAPI().isMSP() || e.toJSONObject().getString("error_code") != "COM0022" || !APIUtil.getBooleanFilter(request, "include_all", false)) {
                    throw e;
                }
                customerID = null;
            }
            final PagingUtil pagingUtil = apiUtil.getPagingParams(request);
            final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(request);
            final Boolean selectAll = APIUtil.getBooleanFilter(request, "select_all");
            final String email = request.getJSONObject("msg_header").getJSONObject("filters").optString("email", (String)null);
            final String platform = request.getJSONObject("msg_header").getJSONObject("filters").optString("platform", (String)null);
            final String search = request.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            final Object obj = request.getJSONObject("msg_header").getJSONObject("filters").optString("group_id", (String)null);
            final boolean excludeRemoved = request.getJSONObject("msg_header").getJSONObject("filters").optBoolean("exclude_removed", false);
            final int ownedBy = request.getJSONObject("msg_header").getJSONObject("filters").optInt("owned_by", -1);
            final String deviceType = request.getJSONObject("msg_header").getJSONObject("filters").optString("device_type", (String)null);
            final String imei = request.getJSONObject("msg_header").getJSONObject("filters").optString("imei", (String)null);
            final String serialNumber = request.getJSONObject("msg_header").getJSONObject("filters").optString("serial_number", (String)null);
            final Boolean deviceGroupUnassigned = request.getJSONObject("msg_header").getJSONObject("filters").optBoolean("device_group_unassigned", false);
            final Boolean deviceSummary = request.getJSONObject("msg_header").getJSONObject("filters").optBoolean("summary", false);
            final boolean isLost = APIUtil.getBooleanFilter(request, "is_lost", false);
            final String multipleCustomers = request.getJSONObject("msg_header").getJSONObject("filters").optString("customer_ids", (String)null);
            Long group_id = null;
            if (obj != null) {
                group_id = Long.valueOf(obj.toString());
            }
            Criteria criteria = null;
            if (email != null) {
                criteria = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)email, 0);
            }
            if (imei != null) {
                final Criteria imeiCri = new Criteria(Column.getColumn("PRIMARYMDSIMINFO", "IMEI"), (Object)imei, 12).or(new Criteria(Column.getColumn("SECONDRYMDSIMINFO", "IMEI"), (Object)imei, 12));
                if (criteria == null) {
                    criteria = imeiCri;
                }
                else {
                    criteria = criteria.and(imeiCri);
                }
            }
            JSONObject searchByJSON = pagingUtil.getSearchJSON();
            if (searchByJSON == null && search != null) {
                pagingUtil.setSearchBy("devicename", search);
                searchByJSON = pagingUtil.getSearchJSON();
            }
            if (searchByJSON != null && searchByJSON.has("searchfield")) {
                final String searchField = String.valueOf(searchByJSON.get("searchfield")).toLowerCase();
                final String searchKey = String.valueOf(searchByJSON.get("searchkey"));
                final List<String> searchFields = Arrays.asList(searchField.split(","));
                Criteria searchCriteria = null;
                if (searchFields.contains("devicename")) {
                    searchCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchKey, 12, false);
                }
                if (searchFields.contains("username")) {
                    searchCriteria = ((searchCriteria == null) ? new Criteria(Column.getColumn("USER_RESOURCE", "NAME"), (Object)searchKey, 12, false) : searchCriteria.or(new Criteria(Column.getColumn("USER_RESOURCE", "NAME"), (Object)searchKey, 12, false)));
                }
                if (criteria == null) {
                    criteria = searchCriteria;
                }
                else {
                    criteria = criteria.and(searchCriteria);
                }
            }
            if (excludeRemoved) {
                final Criteria excludeCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                if (criteria == null) {
                    criteria = excludeCri;
                }
                else {
                    criteria = criteria.and(excludeCri);
                }
            }
            if (platform != null && platform.length() != 0) {
                int p = -1;
                if (platform.equalsIgnoreCase("android")) {
                    p = 2;
                }
                else if (platform.equalsIgnoreCase("ios")) {
                    p = 1;
                }
                else if (platform.equalsIgnoreCase("windows")) {
                    p = 3;
                }
                else if (platform.equalsIgnoreCase("chrome")) {
                    p = 4;
                }
                else {
                    final String[] platformTypes = platform.split(",");
                    final ArrayList<Integer> values = new ArrayList<Integer>();
                    for (int i = 0; i < platformTypes.length; ++i) {
                        final int temp = Integer.parseInt(platformTypes[i]);
                        if (temp == 2 || temp == 1 || temp == 3 || temp == 4) {
                            values.add(temp);
                        }
                    }
                    if (values.size() != 0) {
                        final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)values.toArray(), 8);
                        if (criteria == null) {
                            criteria = platformCriteria;
                        }
                        else {
                            criteria = criteria.and(platformCriteria);
                        }
                    }
                }
                if (p != -1) {
                    final Criteria platformCriteria2 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)p, 0);
                    if (criteria == null) {
                        criteria = platformCriteria2;
                    }
                    else {
                        criteria = criteria.and(platformCriteria2);
                    }
                }
            }
            if (ownedBy != -1) {
                final Criteria ownedByCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                if (ownedBy == 1 || ownedBy == 2) {
                    if (criteria == null) {
                        criteria = ownedByCri;
                    }
                    else {
                        criteria = criteria.and(ownedByCri);
                    }
                }
            }
            if (deviceType != null) {
                final String[] deviceTypes = deviceType.split(",");
                final ArrayList<Integer> values2 = new ArrayList<Integer>();
                for (int j = 0; j < deviceTypes.length; ++j) {
                    final int temp2 = Integer.parseInt(deviceTypes[j]);
                    if (temp2 == 0 || temp2 == 1 || temp2 == 2 || temp2 == 4 || temp2 == 3 || temp2 == 5) {
                        values2.add(temp2);
                    }
                }
                if (values2.size() != 0) {
                    final Criteria deviceTypeCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)values2.toArray(), 8);
                    if (criteria == null) {
                        criteria = deviceTypeCri;
                    }
                    else {
                        criteria = criteria.and(deviceTypeCri);
                    }
                }
            }
            if (isLost) {
                final Criteria lostCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8);
                if (criteria == null) {
                    criteria = lostCriteria;
                }
                else {
                    criteria = criteria.and(lostCriteria);
                }
            }
            if (serialNumber != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)serialNumber, 12, false));
            }
            if (deltaTokenUtil != null) {
                final Long lastRequestTime = deltaTokenUtil.getRequestTimestamp();
                Criteria deltaCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "LAST_MODIFIED_TIME"), (Object)lastRequestTime, 5);
                deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("ManagedDevice", "ADDED_TIME"), (Object)lastRequestTime, 5));
                deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)lastRequestTime, 5));
                deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("USER_RESOURCE", "DB_UPDATED_TIME"), (Object)lastRequestTime, 5));
                if (criteria == null) {
                    criteria = deltaCriteria;
                }
                else {
                    criteria = criteria.and(deltaCriteria);
                }
            }
            if (deviceGroupUnassigned != null && deviceGroupUnassigned) {
                final Criteria deviceCriteria = new GroupFacade().getDeviceGroupAssignedFilterCriteria(-1L);
                criteria = criteria.and(deviceCriteria);
            }
            final SelectQuery query = this.getDevicesBaseQuery();
            final SelectQuery cQuery = this.getDevicesBaseQuery();
            this.addSelectColForDevicesQuery(query);
            this.addCustomerFilter(query, APIUtil.getUserID(request), customerID);
            this.addCustomerFilter(cQuery, APIUtil.getUserID(request), customerID);
            this.addSelectColForDevicesCountQuery(cQuery);
            if (deviceSummary) {
                query.addJoin(new Join("ManagedDevice", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                query.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "PROFILE_COUNT"));
                query.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "APP_COUNT"));
                query.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "DOC_COUNT"));
                final Table resourceTable = Table.getTable("Resource");
                SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
                subSQ.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
                final Column column_config_data = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID").count();
                column_config_data.setColumnAlias("GROUP_RESOURCE_ID");
                final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                subSQ.addJoin(customGroupJoin);
                subSQ.setCriteria(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)9, 1));
                subSQ = RBDAUtil.getInstance().getRBDAQuery(subSQ);
                subSQ.addSelectColumn(column_config_data);
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
                list.add(groupByCol);
                final GroupByClause memberGroupBy = new GroupByClause(list);
                subSQ.setGroupByClause(memberGroupBy);
                final DerivedTable groupDerievedTab = new DerivedTable("CustomGroupMemberRel", (Query)subSQ);
                query.addJoin(new Join(resourceTable, (Table)groupDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                final Column maxCountCol = new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
                maxCountCol.setColumnAlias("GROUP_COUNT");
                query.addSelectColumn(maxCountCol);
            }
            if (group_id != null) {
                final Join groupRelJoin = new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, "ManagedDevice", "CUSTOMGROUPMEMBERRELFILTER", 1);
                query.addJoin(groupRelJoin);
                cQuery.addJoin(groupRelJoin);
                final Criteria groupCriteria = new Criteria(Column.getColumn("CUSTOMGROUPMEMBERRELFILTER", "GROUP_RESOURCE_ID"), (Object)group_id, 0);
                if (criteria == null) {
                    criteria = groupCriteria;
                }
                else {
                    criteria = criteria.and(groupCriteria);
                }
            }
            this.setCriteriaForGetDevicesQuery(query, criteria);
            this.setCriteriaForGetDevicesQuery(cQuery, criteria);
            if (customerID == null && multipleCustomers != null) {
                List customerList = new ArrayList();
                final String[] customerArray = multipleCustomers.split(",");
                for (int k = 0; k < customerArray.length; ++k) {
                    customerList.add(Long.parseLong(customerArray[k]));
                }
                customerList = MDMCustomerInfoUtil.getInstance().getValidCustomerIds(customerList);
                if (customerList.isEmpty()) {
                    throw new APIHTTPException("COM0015", new Object[] { "Customer Id does not exists" });
                }
                criteria = query.getCriteria();
                final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerList.toArray(), 8);
                if (criteria == null) {
                    criteria = customerCri;
                }
                else {
                    criteria = criteria.and(customerCri);
                }
                query.setCriteria(criteria);
                cQuery.setCriteria(criteria);
            }
            final int count = DBUtil.getRecordCount(cQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            response.put("metadata", (Object)meta);
            final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(request.getJSONObject("msg_header").get("request_url")));
            if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
                response.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
            }
            if (count != 0) {
                if (!selectAll) {
                    final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                    if (pagingJSON != null) {
                        response.put("paging", (Object)pagingJSON);
                    }
                    query.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                }
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("devicename")) {
                        query.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                    }
                }
                else if (customerID == null) {
                    query.addSortColumn(new SortColumn("Resource", "CUSTOMER_ID", true));
                }
                else {
                    query.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
                }
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)query, conn);
                while (ds.next()) {
                    if (ds.getValue("MANAGED_STATUS") != null) {
                        final JSONObject deviceMap = new JSONObject();
                        final Integer enrollStatus = Integer.valueOf(String.valueOf(ds.getValue("MANAGED_STATUS")));
                        deviceMap.put("managed_status", (Object)enrollStatus);
                        switch (enrollStatus) {
                            case 2: {
                                final JSONObject user = new JSONObject();
                                final JSONObject summary = new JSONObject();
                                deviceMap.put("customer_id", (Object)ds.getValue("CUSTOMER_ID").toString());
                                deviceMap.put("customer_name", ds.getValue("CUSTOMER_NAME"));
                                deviceMap.put("device_id", (Object)ds.getValue("RESOURCE_ID").toString());
                                deviceMap.put("udid", (Object)ds.getValue("UDID").toString());
                                deviceMap.put("serial_number", (Object)String.valueOf(ds.getValue("SERIAL_NUMBER")));
                                deviceMap.put("is_lost_mode_enabled", new LostModeDataHandler().isLostMode(Long.valueOf(String.valueOf(ds.getValue("RESOURCE_ID")))));
                                user.put("user_email", ds.getValue("EMAIL_ADDRESS"));
                                final int platformType = Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString());
                                deviceMap.put("platform_type_id", platformType);
                                if (platformType == 1) {
                                    deviceMap.put("platform_type", (Object)"ios");
                                }
                                else if (platformType == 2) {
                                    deviceMap.put("platform_type", (Object)"android");
                                }
                                else if (platformType == 3) {
                                    deviceMap.put("platform_type", (Object)"windows");
                                }
                                deviceMap.put("model", ds.getValue("MODEL"));
                                deviceMap.put("device_type", ds.getValue("MODEL_TYPE"));
                                deviceMap.put("owned_by", ds.getValue("OWNED_BY"));
                                deviceMap.put("product_name", ds.getValue("PRODUCT_NAME"));
                                deviceMap.put("os_version", ds.getValue("OS_VERSION"));
                                deviceMap.put("device_capacity", ds.getValue("DEVICE_CAPACITY"));
                                user.put("user_name", ds.getValue("NAME"));
                                user.put("user_id", (Object)String.valueOf(ds.getValue("MANAGED_USER_ID")));
                                deviceMap.put("device_name", ds.getValue("DEVICE_NAME"));
                                deviceMap.put("last_contact_time", ds.getValue("LAST_CONTACT_TIME"));
                                deviceMap.put("user", (Object)user);
                                deviceMap.put("is_removed", (Object)"false");
                                deviceMap.put("located_time", ds.getValue("LOCATED_TIME"));
                                final boolean isSupervised = ds.getValue("IS_SUPERVISED") != null && (boolean)ds.getValue("IS_SUPERVISED");
                                deviceMap.put("is_supervised", isSupervised);
                                final JSONArray imeiArray = new JSONArray();
                                if (ds.getValue("PRIMARY_IMEI") != null) {
                                    imeiArray.put(ds.getValue("PRIMARY_IMEI"));
                                }
                                if (ds.getValue("SECONDRY_IMEI") != null) {
                                    imeiArray.put(ds.getValue("SECONDRY_IMEI"));
                                }
                                if (imeiArray.length() > 0) {
                                    deviceMap.put("imei", (Object)imeiArray);
                                }
                                if (deviceSummary) {
                                    summary.put("profile_count", ds.getValue("PROFILE_COUNT"));
                                    summary.put("app_count", ds.getValue("APP_COUNT"));
                                    summary.put("doc_count", ds.getValue("DOC_COUNT"));
                                    summary.put("group_count", ds.getValue("GROUP_COUNT"));
                                    deviceMap.put("summary", (Object)summary);
                                }
                                result.put((Object)deviceMap);
                                continue;
                            }
                            case 4:
                            case 7:
                            case 9:
                            case 10:
                            case 11: {
                                deviceMap.put("device_id", (Object)ds.getValue("RESOURCE_ID").toString());
                                deviceMap.put("is_removed", (Object)"true");
                                result.put((Object)deviceMap);
                                continue;
                            }
                        }
                    }
                    else {
                        final JSONObject deviceMap = new JSONObject();
                        deviceMap.put("device_id", (Object)ds.getValue("RESOURCE_ID").toString());
                        deviceMap.put("is_removed", true);
                        result.put((Object)deviceMap);
                    }
                }
            }
            response.put("devices", (Object)result);
            return response;
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            DeviceFacade.logger.log(Level.SEVERE, "error in getDevices()", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    private void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            DeviceFacade.logger.log(Level.WARNING, "Exception occurred in closeConnection....", ex);
        }
    }
    
    public Object getInstalledAppList(final JSONObject message) throws APIHTTPException {
        JSONArray result = null;
        Connection conn = null;
        DataSet ds = null;
        try {
            Long deviceId;
            try {
                deviceId = APIUtil.getResourceID(message, "device_id");
                if (deviceId == -1L) {
                    final String udid = APIUtil.getResourceIDString(message, "udid");
                    deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
                }
                this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final String include = APIUtil.optStringFilter(message, "include", "");
            final String search = APIUtil.optStringFilter(message, "search", null);
            final String showAllApps = APIUtil.optStringFilter(message, "showallapps", null);
            SelectQuery query = new AppFacade().getAppDeviceQuery(Arrays.asList(deviceId), APIUtil.getCustomerID(message));
            query.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            query.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            query.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            query.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
            query.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "APPLIED_TIME"));
            query.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
            query.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
            if (include.equalsIgnoreCase("details")) {
                query.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "RESOURCE_ID", "PROFILE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "PROFILE_ID", "COLLECTION_ID" }, 2));
                query.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppData", new String[] { "APP_GROUP_ID", "APP_ID" }, new String[] { "APP_GROUP_ID", "APP_ID" }, 2));
                query.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                query.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
                query.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
                query.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
                query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ADDED_TIME"));
                query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_MODIFIED_TIME"));
                query.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "LAST_MODIFIED_TIME"));
                query.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_TIME"));
                query.setDistinct(true);
            }
            if (search != null) {
                final Criteria searchAppNameCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME"), (Object)search, 12, false);
                final Criteria searchAppIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)search, 12, false);
                final Criteria searchCriteria = searchAppNameCriteria.or(searchAppIdentifierCriteria);
                final Criteria criteria = query.getCriteria().and(searchCriteria);
                query.setCriteria(criteria);
            }
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), 0);
            query.setCriteria(query.getCriteria().and(platformCriteria).and(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(message), 0)));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            result = new JSONArray();
            while (ds.next()) {
                final JSONObject appDetails = new JSONObject();
                appDetails.put("APP_ID", ds.getValue("PACKAGE_ID"));
                appDetails.put("APP_VERSION", ds.getValue("APP_VERSION"));
                appDetails.put("app_version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                final int platformType = Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString());
                if (platformType == 1) {
                    appDetails.put("platform_type", (Object)"ios");
                }
                else if (platformType == 2) {
                    appDetails.put("platform_type", (Object)"android");
                }
                else if (platformType == 3) {
                    appDetails.put("platform_type", (Object)"windows");
                }
                if (include.equalsIgnoreCase("details")) {
                    appDetails.put("platform", platformType);
                    appDetails.put("PACKAGE_TYPE", ds.getValue("PACKAGE_TYPE"));
                    appDetails.put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
                    appDetails.put("added_time", ds.getValue("PACKAGE_ADDED_TIME"));
                    appDetails.put("modified_time", ds.getValue("PACKAGE_MODIFIED_TIME"));
                    if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                        final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                        if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                            if (!displayImageLoc.startsWith("http")) {
                                appDetails.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                            }
                            else {
                                appDetails.put("icon", (Object)displayImageLoc);
                            }
                        }
                    }
                    final Long appGroupId = (Long)appDetails.get("APP_GROUP_ID");
                    appDetails.put("associated_on", ds.getValue("ASSOCIATED_TIME"));
                    appDetails.put("installed_version", (Object)AppsUtil.getInstance().getInstalledVersionForApp(deviceId, appGroupId));
                    final Long releaseLabel = (Long)ds.getValue("RELEASE_LABEL_ID");
                    appDetails.put("latest_version", (Object)AppsUtil.getInstance().getAppLatestVersion(appGroupId, releaseLabel));
                }
                appDetails.put("APP_NAME", ds.getValue("APP_NAME"));
                appDetails.put("IDENTIFIER", ds.getValue("IDENTIFIER"));
                appDetails.put("status", ds.getValue("STATUS"));
                appDetails.put("executed_on", ds.getValue("APPLIED_TIME"));
                appDetails.put("remarks", (Object)MDMI18N.getMsg(String.valueOf(ds.getValue("REMARKS")), true));
                appDetails.put("localized_remark", (Object)MDMI18N.getMsg(String.valueOf(ds.getValue("REMARKS_EN")), true));
                final JSONObject releaseLabelDetails = new JSONObject();
                releaseLabelDetails.put("release_label_name", (Object)MDMI18N.getMsg(String.valueOf(ds.getValue("RELEASE_LABEL_DISPLAY_NAME")), Boolean.TRUE));
                releaseLabelDetails.put("release_label_id", (Object)String.valueOf(ds.getValue("RELEASE_LABEL_ID")));
                appDetails.put("release_label_details", (Object)releaseLabelDetails);
                result.put((Object)appDetails);
            }
            final JSONObject apps = new JSONObject().put("apps", (Object)result);
            final JSONObject appViewSettings = AppSettingsDataHandler.getInstance().getAppViewSettings(APIUtil.getCustomerID(message));
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            query.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            query.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            query.addJoin(new Join("MdAppDetails", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            query.setCriteria(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)deviceId, 0));
            Criteria appScanCriteria = null;
            if (appViewSettings.getBoolean("SHOW_USER_INSTALLED_APPS")) {
                appScanCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            }
            if (appViewSettings.getBoolean("SHOW_SYSTEM_APPS")) {
                if (appScanCriteria == null) {
                    appScanCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
                }
                else {
                    appScanCriteria = appScanCriteria.or(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0));
                }
            }
            if (appViewSettings.getBoolean("SHOW_MANAGED_APPS")) {
                if (appScanCriteria == null) {
                    appScanCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)3, 0);
                }
                else {
                    appScanCriteria = appScanCriteria.or(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)3, 0));
                }
            }
            if (search != null) {
                final Criteria searchAppNameCriteria2 = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME"), (Object)search, 12, false);
                final Criteria searchAppIdentifierCriteria2 = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)search, 12, false);
                final Criteria searchCriteria2 = searchAppNameCriteria2.or(searchAppIdentifierCriteria2);
                final Criteria criteria2 = query.getCriteria().and(searchCriteria2);
                query.setCriteria(criteria2);
            }
            if (showAllApps != null && showAllApps.equalsIgnoreCase("true")) {
                appScanCriteria = null;
            }
            if (appScanCriteria != null) {
                query.setCriteria(query.getCriteria().and(appScanCriteria));
            }
            query.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
            query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            ds = relapi.executeQuery((Query)query, conn);
            result = new JSONArray();
            while (ds.next()) {
                final JSONObject appDetails2 = new JSONObject();
                appDetails2.put("APP_GROUP_ID", ds.getValue("APP_GROUP_ID"));
                appDetails2.put("APP_ID", ds.getValue("APP_ID"));
                appDetails2.put("APP_VERSION", ds.getValue("APP_VERSION"));
                appDetails2.put("app_version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                final int platformType2 = Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString());
                if (platformType2 == 1) {
                    appDetails2.put("platform_type", (Object)"ios");
                }
                else if (platformType2 == 2) {
                    appDetails2.put("platform_type", (Object)"android");
                }
                else if (platformType2 == 3) {
                    appDetails2.put("platform_type", (Object)"windows");
                }
                appDetails2.put("APP_NAME", ds.getValue("APP_NAME"));
                appDetails2.put("IDENTIFIER", ds.getValue("IDENTIFIER"));
                result.put((Object)appDetails2);
            }
            apps.put("installed_apps", (Object)result);
            return apps;
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            DeviceFacade.logger.log(Level.SEVERE, "error in getInstalledAppList()", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    public List<Long> getDeviceIdsForUser(final Long userId) throws APIHTTPException {
        List<Long> deviceIds = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0).and(new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userId, 0)));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        try {
            final Object[] ids = DBUtil.getColumnValues(MDMUtil.getPersistence().get(query).getRows("ManagedDevice"), "RESOURCE_ID");
            deviceIds = new ArrayList<Long>();
            for (final Object id : ids) {
                deviceIds.add((Long)id);
            }
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return deviceIds;
    }
    
    public JSONObject getDeviceInstalledProfiles(final JSONObject message) throws SyMException {
        try {
            final JSONObject json = new JSONObject();
            json.put("provisioning_profiles", (Object)this.getDeviceInstalledProvisioningProfiles(message));
            json.put("configuration_profiles", (Object)this.getDeviceInstalledConfigurationProfiles(message));
            return json;
        }
        catch (final JSONException ex) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ex);
        }
        catch (final NumberFormatException ne) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ne);
        }
        catch (final Exception e) {
            throw new SyMException(500, "Internal server error while fetching provisioning profiles. See trace: ", (Throwable)e);
        }
    }
    
    public JSONArray getDeviceInstalledProvisioningProfiles(final JSONObject message) throws SyMException {
        try {
            Long resourceID = APIUtil.getResourceID(message, "device_id");
            if (resourceID == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            return new DeviceProvProfilesDataHandler().getInstalledProvisioningProfiles(resourceID);
        }
        catch (final JSONException ex) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ex);
        }
        catch (final NumberFormatException ne) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ne);
        }
        catch (final Exception e) {
            throw new SyMException(500, "Internal server error while fetching provisioning profiles. See trace: ", (Throwable)e);
        }
    }
    
    public JSONObject getDeviceInstalledCertificates(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject installedCertificateJSON = new JSONObject();
            Long resourceID = APIUtil.getResourceID(message, "device_id");
            final Long customerId = APIUtil.getCustomerID(message);
            if (resourceID == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(resourceID, customerId);
            Long expiry = APIUtil.getLongFilter(message, "expiry");
            if (expiry == -1L) {
                expiry = null;
            }
            return installedCertificateJSON.put("certificates", (Object)new DeviceInstalledCertificateDataHandler().getInstalledCertificateList(resourceID, expiry));
        }
        catch (final JSONException ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Error while parsing request JSON. See trace: ", (Throwable)ex);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception ex2) {
            DeviceFacade.logger.log(Level.SEVERE, "Internal server errors while fetching installed certificate. See trace:", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceInstalledProvisioningProfilesResponse(final JSONObject message) throws SyMException {
        try {
            final JSONObject json = new JSONObject();
            json.put("provisioning_profiles", (Object)this.getDeviceInstalledProvisioningProfiles(message));
            return json;
        }
        catch (final JSONException ex) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ex);
        }
    }
    
    public JSONArray getDeviceInstalledConfigurationProfiles(final JSONObject message) throws SyMException, APIHTTPException {
        try {
            Long resourceID = APIUtil.getResourceID(message, "device_id");
            if (resourceID == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long customerId = APIUtil.getCustomerID(message);
            this.validateIfDeviceExists(resourceID, customerId);
            final String payloadIdentifier = APIUtil.getStringFilter(message, "payload_identifier");
            final Integer installedSource = APIUtil.getIntegerFilter(message, "installed_source");
            final JSONObject filterObject = new JSONObject();
            filterObject.put("INSTALLED_SOURCE", (Object)installedSource);
            filterObject.put("PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
            return new DeviceConfigPayloadsDataHandler().getInstalledProfilesDetails(resourceID, filterObject);
        }
        catch (final JSONException ex) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ex);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final NumberFormatException ne) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ne);
        }
        catch (final Exception e) {
            throw new SyMException(500, "Internal server error while fetching provisioning profiles. See trace: ", (Throwable)e);
        }
    }
    
    public JSONObject getDeviceInstalledConfigurationProfilesResponse(final JSONObject message) throws SyMException, APIHTTPException {
        try {
            final JSONObject json = new JSONObject();
            json.put("configuration_profiles", (Object)JSONUtil.getInstance().changeJSONKeyCase(this.getDeviceInstalledConfigurationProfiles(message), 2));
            return json;
        }
        catch (final JSONException ex) {
            throw new SyMException(400, "Error while parsing request JSON. See trace: ", (Throwable)ex);
        }
    }
    
    public void addSelectColForDevicesCountQuery(final SelectQuery query) {
        Column selCol = new Column("Resource", "RESOURCE_ID");
        selCol = selCol.distinct();
        selCol = selCol.count();
        query.addSelectColumn(selCol);
    }
    
    public void addSelectColForDevicesQuery(final SelectQuery query) {
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "PRODUCT_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "DEVICE_CAPACITY"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        query.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
        query.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "DEVICE_NAME"));
        query.addSelectColumn(Column.getColumn("PRIMARYMDSIMINFO", "IMEI", "PRIMARY_IMEI"));
        query.addSelectColumn(Column.getColumn("SECONDRYMDSIMINFO", "IMEI", "SECONDRY_IMEI"));
        query.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
        query.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_NAME"));
    }
    
    private void setCriteriaForGetDevicesQuery(final SelectQuery query, final Criteria criteria) {
        Criteria finalCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new int[] { 121, 120 }, 8);
        finalCriteria = finalCriteria.and(criteria);
        finalCriteria = finalCriteria.and(query.getCriteria());
        query.setCriteria(finalCriteria);
    }
    
    public SelectQuery getDevicesBaseQuery() {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join resDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join device_deviceexten = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
        final Join device_userid = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
        final Join userdetails = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join mddeviceinfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join modelinfoJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1);
        final Join userresource = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "USER_RESOURCE", 1);
        final Join deviceToErReqJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join erToDeviceJoin = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        final Join agentContact = new Join("Resource", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join lostModeTrackInfoJoin = new Join("Resource", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join deviceRecentLocationJoin = new Join("ManagedDevice", "DeviceRecentLocation", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join mdDeviceLocationDetailsJoin = new Join("DeviceRecentLocation", "MdDeviceLocationDetails", new String[] { "LOCATION_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 1);
        final Join customerInfoJoin = new Join("Resource", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1);
        final Criteria primaryImeiCri = new Criteria(Column.getColumn("PRIMARYMDSIMINFO", "RESOURCE_ID"), (Object)Column.getColumn("Resource", "RESOURCE_ID"), 0).and(Column.getColumn("PRIMARYMDSIMINFO", "IMEI"), (Object)Column.getColumn("MdDeviceInfo", "IMEI"), 0);
        final Join primaryImeiJoin = new Join("Resource", "Resource", "MdSIMInfo", "PRIMARYMDSIMINFO", primaryImeiCri, 1);
        final Criteria secondryImeiCri = new Criteria(Column.getColumn("SECONDRYMDSIMINFO", "RESOURCE_ID"), (Object)Column.getColumn("Resource", "RESOURCE_ID"), 0).and(Column.getColumn("SECONDRYMDSIMINFO", "IMEI"), (Object)Column.getColumn("MdDeviceInfo", "IMEI"), 1);
        final Join secondryImeiJoin = new Join("Resource", "Resource", "MdSIMInfo", "SECONDRYMDSIMINFO", secondryImeiCri, 1);
        query.addJoin(resDeviceJoin);
        query.addJoin(device_deviceexten);
        query.addJoin(device_userid);
        query.addJoin(userdetails);
        query.addJoin(mddeviceinfoJoin);
        query.addJoin(modelinfoJoin);
        query.addJoin(userresource);
        query.addJoin(deviceToErReqJoin);
        query.addJoin(erToDeviceJoin);
        query.addJoin(agentContact);
        query.addJoin(primaryImeiJoin);
        query.addJoin(secondryImeiJoin);
        query.addJoin(lostModeTrackInfoJoin);
        query.addJoin(deviceRecentLocationJoin);
        query.addJoin(mdDeviceLocationDetailsJoin);
        query.addJoin(customerInfoJoin);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        query.setCriteria(userNotInTrashCriteria);
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        return query;
    }
    
    public void deleteDevice(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            final Long erid = MDMEnrollmentUtil.getInstance().getEnrollRequestIDFromManagedDeviceID(deviceId);
            final String userName = DMUserHandler.getDCUser(APIUtil.getLoginID(message));
            final Long customerId = APIUtil.getCustomerID(message);
            MDMEnrollmentUtil.getInstance().removeDevice(String.valueOf(erid), userName, customerId);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateDeviceDetails(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            if (deviceId == null) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            final JSONObject deviceDetails = message.optJSONObject("msg_body");
            final String deviceName = deviceDetails.optString("device_name");
            Boolean isDeviceNameChanged = false;
            if (!MDMStringUtils.isEmpty(deviceName)) {
                deviceDetails.put("name", (Object)deviceName);
                isDeviceNameChanged = true;
            }
            deviceDetails.remove("device_name");
            final String apnUserName = deviceDetails.optString("apn_username");
            if (!MDMStringUtils.isEmpty(apnUserName)) {
                deviceDetails.put("apn_user_name", (Object)apnUserName);
            }
            deviceDetails.remove("apn_username");
            final JSONObject modifiedJSONObject = JSONUtil.getInstance().changeJSONKeyCase(deviceDetails, 1);
            modifiedJSONObject.put("MANAGED_DEVICE_ID", (Object)deviceId);
            modifiedJSONObject.put("IS_MODIFIED", true);
            final JSONObject additionalDataJSON = new JSONObject();
            additionalDataJSON.put("customer_id", (Object)APIUtil.getCustomerID(message));
            additionalDataJSON.put("user_id", (Object)APIUtil.getUserID(message));
            additionalDataJSON.put("user_name", (Object)APIUtil.getUserName(message));
            additionalDataJSON.put("is_device_name_changed", (Object)isDeviceNameChanged);
            modifiedJSONObject.put("additional_data", (Object)additionalDataJSON);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget")) {
                final SelectQuery selectQuery = this.getEnrollmentRequestDetail(deviceId);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    boolean canUpdate = false;
                    if (deviceDetails.has("registered_time")) {
                        final Long registeredTime = deviceDetails.getLong("registered_time");
                        final Row row = dataObject.getRow("ManagedDevice");
                        if (row != null) {
                            row.set("REGISTERED_TIME", (Object)registeredTime);
                            dataObject.updateRow(row);
                            canUpdate = true;
                        }
                        modifiedJSONObject.remove("REGISTERED_TIME");
                    }
                    if (deviceDetails.has("enrollment_request_time")) {
                        final Long enrollmentReqTime = deviceDetails.getLong("enrollment_request_time");
                        final Row row = dataObject.getRow("DeviceEnrollmentRequest");
                        if (row != null) {
                            row.set("REQUESTED_TIME", (Object)enrollmentReqTime);
                            dataObject.updateRow(row);
                            canUpdate = true;
                        }
                        modifiedJSONObject.remove("ENROLLMENT_REQUEST_TIME");
                    }
                    if (deviceDetails.has("enrollment_type")) {
                        final Integer enrollmentType = deviceDetails.getInt("enrollment_type");
                        final Row row = dataObject.getRow("EnrollmentTemplate");
                        row.set("TEMPLATE_TYPE", (Object)enrollmentType);
                        dataObject.updateRow(row);
                        canUpdate = true;
                        modifiedJSONObject.remove("ENROLLMENT_TYPE");
                    }
                    if (canUpdate) {
                        MDMUtil.getPersistence().update(dataObject);
                    }
                }
            }
            MDCustomDetailsRequestHandler.getInstance().updateCustomDeviceDetails(modifiedJSONObject);
        }
        catch (final JSONException e) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in updateDeviceDetails");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in updateDeviceDetails");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceProfiles(final JSONObject request) throws APIHTTPException {
        Connection conn = null;
        DataSet ds = null;
        try {
            Long deviceId = APIUtil.getResourceID(request, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(request, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Boolean distributionSummary = APIUtil.getBooleanFilter(request, "summary");
            this.validateIfDeviceExists(deviceId, APIUtil.getCustomerID(request));
            final JSONObject result = new JSONObject();
            final APIUtil apiUtil = new APIUtil();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(request);
            SelectQuery selectQuery = this.getDeviceProfilesBaseQuery(deviceId);
            this.addSelectColumnsForDeviceProfiles(selectQuery);
            final SelectQuery countQuery = this.getDeviceProfilesBaseQuery(deviceId);
            this.addSelectColumnsForDeviceProfilesCount(countQuery);
            if (distributionSummary) {
                selectQuery = new ProfileFacade().getProfileAssociationGroupCount(selectQuery);
                selectQuery = new ProfileFacade().getProfileAssociationDeviceCount(selectQuery);
            }
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            result.put("metadata", (Object)meta);
            final JSONArray profiles = new JSONArray();
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    result.put("paging", (Object)pagingJSON);
                }
                selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                selectQuery.addSortColumn(new SortColumn("Profile", "PROFILE_ID", true));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)selectQuery, conn);
                while (ds.next()) {
                    final JSONObject profile = apiUtil.getJSONObjectFromDS(ds, selectQuery);
                    if (profile.has("localized_remarks")) {
                        profile.put("localized_remarks", (Object)I18N.getMsg(String.valueOf(profile.get("localized_remarks")), new Object[0]));
                    }
                    if (profile.has("remarks")) {
                        profile.put("remarks", (Object)I18N.getMsg(String.valueOf(profile.get("remarks")), new Object[0]));
                    }
                    profiles.put((Object)profile);
                }
            }
            result.put("profiles", (Object)profiles);
            return result;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in getDeviceProfiles", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    public JSONObject requestDeviceLocationsWithAddress(final JSONObject request) throws APIHTTPException {
        try {
            final Long managedDeviceId = APIUtil.getResourceID(request, "device_id");
            final Long customerId = APIUtil.getCustomerID(request);
            final Boolean isApiRequest = APIUtil.getBooleanFilter(request, "isApiRequest", Boolean.TRUE);
            JSONArray email_address_list = null;
            if (!isApiRequest) {
                email_address_list = request.optJSONObject("msg_body").optJSONArray("email_address_list");
            }
            this.validateIfDeviceExists(managedDeviceId, customerId);
            final int locationTrackingStatus = LocationSettingsDataHandler.getInstance().getLocationTrackingStatus(customerId);
            if (locationTrackingStatus == 3) {
                throw new APIHTTPException("LOC0006", new Object[0]);
            }
            if (locationTrackingStatus == 2 && !new LostModeDataHandler().isLostMode(managedDeviceId)) {
                throw new APIHTTPException("LOC0005", new Object[0]);
            }
            final HashMap errorMap = MDMGeoLocationHandler.getInstance().getLocationErrorMap(managedDeviceId);
            final boolean isLocationDataEmpty = MDMGeoLocationHandler.getInstance().isLocationDataEmptyForDevice(managedDeviceId, customerId, 5);
            if (errorMap != null && isLocationDataEmpty) {
                throw new APIHTTPException("LOC0001", new Object[] { errorMap.get("DETAILED_DESC") });
            }
            final JSONObject responseJson = new JSONObject();
            String eventLogRemarksKey = null;
            Integer numberOfDays = 3;
            Long startDate = null;
            Long endDate = null;
            JSONObject reqData = request.optJSONObject("msg_body");
            if (reqData == null) {
                reqData = new JSONObject();
            }
            if (reqData.has("no_of_days")) {
                numberOfDays = reqData.getInt("no_of_days");
                if (numberOfDays > 30) {
                    numberOfDays = 30;
                }
            }
            if (reqData.has("from")) {
                startDate = JSONUtil.optLongForUVH(reqData, "from", (Long)null);
                if (reqData.has("to")) {
                    endDate = JSONUtil.optLongForUVH(reqData, "to", (Long)null);
                }
                else {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(startDate);
                    cal.add(6, numberOfDays);
                    endDate = cal.getTimeInMillis();
                }
            }
            else {
                final Calendar cal = Calendar.getInstance();
                endDate = cal.getTimeInMillis();
                cal.add(6, -1 * numberOfDays);
                startDate = cal.getTimeInMillis();
            }
            if (startDate > endDate) {
                throw new APIHTTPException("COM0005", new Object[] { "Start time for location history data is greater than end time." });
            }
            reqData.put("RESOURCE_ID", (Object)managedDeviceId);
            reqData.put("SELECTED_FROM", (Object)startDate);
            reqData.put("SELECTED_TO", (Object)endDate);
            reqData.put("EXPORT_HISTORY_TYPE", 3);
            reqData.put("isApiRequest", (Object)isApiRequest);
            final Boolean isLocationDataAvailable = LocationDataHandler.getInstance().isLocationDataAvailableForFilter(reqData);
            if (isLocationDataAvailable) {
                final Long userId = APIUtil.getUserID(request);
                final String userName = APIUtil.getUserName(request);
                final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(managedDeviceId);
                reqData.put("ManagedDeviceExtn.NAME", (Object)deviceName);
                final JSONObject exportRequestJson = new JSONObject();
                exportRequestJson.put("USER_ID", (Object)userId).put("CUSTOMER_ID", (Object)customerId).put("RESOURCE_ID", (Object)managedDeviceId);
                exportRequestJson.put("isApiRequest", (Object)isApiRequest);
                exportRequestJson.put("LOC_EXPORT_FILTER_DATA", (Object)reqData.toString());
                if (email_address_list != null && email_address_list.length() > 0) {
                    exportRequestJson.put("EMAIL_ADDRESS", (Object)email_address_list.join(",").replace("\"", ""));
                    reqData.put("EMAIL_ADDRESS_LIST", (Object)email_address_list);
                }
                final JSONObject exportResponseJson = ExportRequestDetailsHandler.getInstance().addNewEntryForLocationExportRequest(exportRequestJson);
                if (!String.valueOf(exportResponseJson.get("STATUS")).equalsIgnoreCase("Success")) {
                    DMSecurityLogger.info(DeviceFacade.logger, DeviceFacade.class.getName(), "requestDeviceLocationsWithAddress", "Location export request failed with the following data {0}", (Object)exportRequestJson);
                    throw new APIHTTPException("LOC0002", new Object[] { userName });
                }
                reqData.put("EXPORT_REQ_ID", exportResponseJson.getLong("EXPORT_REQ_ID"));
                reqData.put("CUSTOMER_ID", (Object)customerId);
                reqData.put("USER_ID", (Object)userId);
                reqData.put("REQUESTED_TIME", exportResponseJson.getLong("REQUESTED_TIME"));
                reqData.put("FIRST_NAME", (Object)userName);
                new LocationDataHandler().exportLocationHistoryDataWithAddress(reqData);
                final JSONObject tempJson = new JSONObject();
                tempJson.put("wait", 150 + numberOfDays * 30);
                tempJson.put("export_batch_id", exportResponseJson.getLong("EXPORT_REQ_ID"));
                tempJson.put("status", (Object)"SCHEDULED");
                responseJson.put("status", 200);
                responseJson.put("RESPONSE", (Object)tempJson);
                eventLogRemarksKey = "mdm.inv.evtlog.loc_export_with_address";
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2160, managedDeviceId, userName, eventLogRemarksKey, deviceName, customerId);
            }
            else {
                responseJson.put("status", 204);
                responseJson.put("RESPONSE", (Object)new JSONObject());
            }
            return responseJson;
        }
        catch (final JSONException ex) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in requestDeviceLocationsWithAddress", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception ex2) {
            if (ex2 instanceof APIHTTPException) {
                throw (APIHTTPException)ex2;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in requestDeviceLocationsWithAddress", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject requestDeviceLocationsWithAddressInternal(final JSONObject request) throws APIHTTPException {
        try {
            final JSONArray emailAddressList = request.optJSONObject("msg_body").optJSONArray("email_address_list");
            if (emailAddressList == null || emailAddressList.length() <= 0) {
                throw new APIHTTPException("COM0005", new Object[] { "Mandatory body content key email_address_list is not present" });
            }
            request.getJSONObject("msg_header").getJSONObject("filters").put("isApiRequest", (Object)Boolean.FALSE);
            final JSONObject apiResponseJson = this.requestDeviceLocationsWithAddress(request);
            final JSONObject responseJson = new JSONObject();
            final Integer status = apiResponseJson.getInt("status");
            responseJson.put("status", (Object)status);
            responseJson.put("RESPONSE", (Object)apiResponseJson.getJSONObject("RESPONSE"));
            if (status.equals(200)) {
                final JSONObject responseBodyJson = new JSONObject();
                responseBodyJson.put("STATUS", (Object)"Success");
                responseBodyJson.put("REMARKS", (Object)I18N.getMsg("mdm.inv.loc_export_with_address_req_submitted", new Object[0]));
                responseJson.put("RESPONSE", (Object)responseBodyJson);
            }
            return responseJson;
        }
        catch (final JSONException ex) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occurred in requestDeviceLocationsWithAddressInternal", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception ex2) {
            if (ex2 instanceof APIHTTPException) {
                throw (APIHTTPException)ex2;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in requestDeviceLocationsWithAddressInternal", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceLocationsWithAddress(final JSONObject request) throws APIHTTPException {
        try {
            final JSONObject paramsJson = request.getJSONObject("msg_header").optJSONObject("filters");
            if (paramsJson == null || !paramsJson.has("export_batch_id")) {
                throw new APIHTTPException("COM0024", new Object[] { "Mandatory parameter export_batch_id is not present" });
            }
            final Long managedDeviceId = APIUtil.getResourceID(request, "device_id");
            final Long customerId = APIUtil.getCustomerID(request);
            this.validateIfDeviceExists(managedDeviceId, customerId);
            final Long exportRequestId = JSONUtil.optLongForUVH(paramsJson, "export_batch_id", (Long)null);
            final Long userId = APIUtil.getUserID(request);
            final JSONObject exportRequestDetails = new JSONObject();
            exportRequestDetails.put("EXPORT_REQ_ID", (Object)exportRequestId);
            exportRequestDetails.put("RESOURCE_ID", (Object)managedDeviceId);
            exportRequestDetails.put("USER_ID", (Object)userId);
            exportRequestDetails.put("CUSTOMER_ID", (Object)customerId);
            final JSONObject locJsonResponse = ExportRequestDetailsHandler.getInstance().getLocationExportRequestDetails(exportRequestDetails);
            final Integer status = locJsonResponse.getInt("status");
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", (Object)status);
            if (status.equals(200)) {
                final JSONObject resp = new JSONObject();
                resp.put("locations", (Object)locJsonResponse.getJSONArray("locations"));
                responseJSON.put("RESPONSE", (Object)resp);
            }
            else if (status.equals(204)) {
                responseJSON.put("RESPONSE", (Object)new JSONObject());
            }
            else {
                if (status.equals(404)) {
                    throw new APIHTTPException("LOC0004", new Object[] { "The export_batch_id value is not found" });
                }
                throw new APIHTTPException("LOC0003", new Object[0]);
            }
            return responseJSON;
        }
        catch (final JSONException e) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in getDeviceLocationsWithAddress", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in getDeviceLocationsWithAddress", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceLocationsWithAddressInternal(final JSONObject request) throws APIHTTPException {
        try {
            final Long resourceId = APIUtil.getResourceID(request, "device_id");
            final Long customerId = APIUtil.getCustomerID(request);
            this.validateIfDeviceExists(resourceId, customerId);
            final Long userId = APIUtil.getUserID(request);
            final JSONObject exportRequestDetails = new JSONObject();
            exportRequestDetails.put("RESOURCE_ID", (Object)resourceId);
            exportRequestDetails.put("USER_ID", (Object)userId);
            exportRequestDetails.put("CUSTOMER_ID", (Object)customerId);
            final JSONObject responseJson = LocationDataHandler.getInstance().getLocationDataWithAddressStatus(exportRequestDetails);
            final Integer status = responseJson.getInt("status");
            final JSONObject apiResponse = new JSONObject();
            if (status.equals(404)) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            if (status.equals(200) || status.equals(204)) {
                apiResponse.put("status", (Object)status);
                final JSONObject apiResponseBody = new JSONObject();
                if (status.equals(200)) {
                    apiResponseBody.put("isAlreadyShown", responseJson.getBoolean("isAlreadyShown"));
                }
                apiResponse.put("RESPONSE", (Object)apiResponseBody);
                return apiResponse;
            }
            throw new APIHTTPException("LOC0003", new Object[0]);
        }
        catch (final JSONException ex) {
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in getDeviceLocationsWithAddressInternal", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception ex2) {
            if (ex2 instanceof APIHTTPException) {
                throw (APIHTTPException)ex2;
            }
            DeviceFacade.logger.log(Level.SEVERE, "exception occured in getDeviceLocationsWithAddressInternal", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public SelectQuery getDeviceProfilesBaseQuery(final Long deviceID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join recentProfileCollnJoin = new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join latestProfileCollnJoin = new Join("RecentPubProfileToColln", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, "RecentPubProfileToColln", "LATESTPROFILETOCOLLECTION", 2);
        final Join collnToResJoin = new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2);
        final Join profileCollnJoin = new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2);
        final Join resourceHistoryJoin = new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2);
        final Join aaaUserJoin = new Join("ResourceToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2);
        selectQuery.addJoin(profileJoin);
        selectQuery.addJoin(collnToResJoin);
        selectQuery.addJoin(resourceHistoryJoin);
        selectQuery.addJoin(aaaUserJoin);
        selectQuery.addJoin(recentProfileCollnJoin);
        selectQuery.addJoin(latestProfileCollnJoin);
        selectQuery.addJoin(profileCollnJoin);
        selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "Profile", "CREATED_USER", 2));
        selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "Profile", "LAST_MODIFIED_USER", 2));
        Criteria deviceIdCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceID, 0);
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new int[] { 1, 10 }, 8);
        deviceIdCriteria = deviceIdCriteria.and(profileTypeCriteria);
        final Criteria notapplicable = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1);
        selectQuery.setCriteria(deviceIdCriteria.and(notapplicable));
        return selectQuery;
    }
    
    public void addSelectColumnsForDeviceProfiles(final SelectQuery selectQuery) {
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID", "profile_id"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME", "profile_name"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE", "platform_type"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION", "profile_description"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME", "last_modified_time"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY", "last_modified_by"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATED_BY", "created_by"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME", "creation_time"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "APPLIED_TIME", "applied_time"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS", "status"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS", "localized_remarks"));
        selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN", "remarks"));
        selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_BY", "associated_by_user_id"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME", "associated_by_user_name"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "executed_version"));
        selectQuery.addSelectColumn(Column.getColumn("LATESTPROFILETOCOLLECTION", "PROFILE_VERSION", "latest_version"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_USER", "FIRST_NAME", "created_by_user"));
        selectQuery.addSelectColumn(Column.getColumn("LAST_MODIFIED_USER", "FIRST_NAME", "last_modified_by_user"));
    }
    
    public void addSelectColumnsForDeviceProfilesCount(final SelectQuery selectQuery) {
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID").distinct().count());
    }
    
    public JSONObject getDeviceDistributionListForProfile(final JSONObject requestJSON) throws Exception {
        try {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
            final DeltaTokenUtil deltaTokenUtil = apiUtil.getDeltaTokenForAPIRequest(requestJSON);
            final JSONArray devicesJSONArray = new JSONArray();
            final JSONObject responseJSON = new JSONObject();
            final Long profileId = APIUtil.getResourceID(requestJSON, "distribute_profile_id");
            final Long businessStoreID = APIUtil.getLongFilter(requestJSON, "businessstore_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            new ProfileFacade().validateIfProfileExists(profileId, customerId);
            final List managedDeviceList = ManagedDeviceHandler.getInstance().getManagedDevicesListForLoggedUser(customerId);
            final String platform = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("platform", "--");
            final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
            Integer listType = APIUtil.getIntegerFilter(requestJSON, "list_type");
            final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            if (listType == -1) {
                listType = 1;
            }
            final int profileType = ProfileUtil.getProfileType(profileId);
            Long collectionId = null;
            if (profileType == 2) {
                collectionId = APIUtil.getResourceID(requestJSON, "collection_id");
            }
            else {
                collectionId = ProfileHandler.getRecentProfileCollectionID(profileId);
            }
            SelectQuery selectQuery;
            SelectQuery countQuery;
            if (listType == 1) {
                selectQuery = this.getYetToApplyDevicesQuery(profileId, collectionId, customerId, profileType, businessStoreID);
                countQuery = this.getYetToApplyDevicesCountQuery(profileId, collectionId, customerId, profileType, businessStoreID);
            }
            else if (listType == 3) {
                selectQuery = this.getAlreadyDistributedDevicesQuery(profileId, collectionId, customerId, businessStoreID);
                countQuery = this.getAlreadyDistributedDevicesCountQuery(profileId, collectionId, customerId, businessStoreID);
            }
            else {
                selectQuery = this.getYetToUpdateDevicesQuery(profileId, collectionId, customerId, profileType, businessStoreID);
                countQuery = this.getYetToUpdateDevicesCountQuery(profileId, collectionId, customerId, profileType, businessStoreID);
            }
            if (!platform.equalsIgnoreCase("--")) {
                final String[] filters = platform.split(",");
                final List platFormList = new ArrayList();
                for (final String filterKey : filters) {
                    final int platformType = getPlatformType(filterKey);
                    platFormList.add(platformType);
                }
                Criteria platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormList.toArray(), 8);
                platformTypeCriteria = selectQuery.getCriteria().and(platformTypeCriteria);
                selectQuery.setCriteria(platformTypeCriteria);
                platformTypeCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormList.toArray(), 8);
                platformTypeCriteria = countQuery.getCriteria().and(platformTypeCriteria);
                countQuery.setCriteria(platformTypeCriteria);
            }
            if (managedDeviceList != null) {
                Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceList.toArray(), 8);
                managedDeviceCriteria = selectQuery.getCriteria().and(managedDeviceCriteria);
                selectQuery.setCriteria(managedDeviceCriteria);
                managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceList.toArray(), 8);
                managedDeviceCriteria = countQuery.getCriteria().and(managedDeviceCriteria);
                countQuery.setCriteria(managedDeviceCriteria);
            }
            if (search != null) {
                final Criteria searchCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)search, 12, false);
                Criteria criteria = selectQuery.getCriteria().and(searchCriteria);
                selectQuery.setCriteria(criteria);
                criteria = countQuery.getCriteria().and(searchCriteria);
                countQuery.setCriteria(criteria);
            }
            if (!selectAll && deltaTokenUtil != null) {
                final Long lastRequestTime = deltaTokenUtil.getRequestTimestamp();
                Criteria deltaCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "LAST_MODIFIED_TIME"), (Object)lastRequestTime, 5);
                deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("ManagedDevice", "ADDED_TIME"), (Object)lastRequestTime, 5));
                deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)lastRequestTime, 5));
                deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("USER_RESOURCE", "DB_UPDATED_TIME"), (Object)lastRequestTime, 5));
                Criteria criteria2 = selectQuery.getCriteria().and(deltaCriteria);
                selectQuery.setCriteria(criteria2);
                criteria2 = countQuery.getCriteria().and(deltaCriteria);
                countQuery.setCriteria(criteria2);
            }
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            responseJSON.put("metadata", (Object)meta);
            final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(requestJSON.getJSONObject("msg_header").get("request_url")));
            if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
                responseJSON.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
            }
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    responseJSON.put("paging", (Object)pagingJSON);
                }
                if (!selectAll) {
                    selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                    if (orderByJSON != null && orderByJSON.has("orderby")) {
                        final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                        if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("devicename")) {
                            selectQuery.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                        }
                    }
                    else {
                        selectQuery.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
                    }
                }
                final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
                for (int i = 0; i < resultJSONArray.size(); ++i) {
                    final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                    devicesJSONArray.put((Object)this.getDeviceJSON(tempJSON));
                }
            }
            if (profileType == 5) {
                final JSONObject complianceJSON = new JSONObject();
                complianceJSON.put("compliance_id", (Object)profileId);
                complianceJSON.put("customer_id", (Object)customerId);
                final JSONObject policyJSON = ComplianceDBUtil.getInstance().getComplianceProfile(complianceJSON);
                final JSONObject privacyJSON = new PrivacySettingsHandler().getPrivacyDetails(customerId);
                Boolean isGeoFence = false;
                Boolean isWipe = false;
                if (policyJSON.toString().contains("geo_fence_id")) {
                    isGeoFence = true;
                }
                if (policyJSON.toString().contains("wipe_sd_card")) {
                    isWipe = true;
                }
                privacyJSON.put("EraseDevice", (Object)isWipe);
                privacyJSON.put("geo_fences", (Object)isGeoFence);
                this.addPrivacyKeys(privacyJSON, devicesJSONArray);
            }
            if (listType == 1) {
                responseJSON.put("yet_to_apply_list", (Object)devicesJSONArray);
            }
            else if (listType == 3) {
                responseJSON.put("distributed_list", (Object)devicesJSONArray);
            }
            else if (listType == 2) {
                responseJSON.put("yet_to_update_list", (Object)devicesJSONArray);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getDeviceDistributionListForProfile()  >   Error ", e);
            throw e;
        }
    }
    
    public JSONObject getSystemActivity(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final Long deviceId = APIUtil.getResourceID(requestJSON, "device_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        this.validateIfDeviceExists(deviceId, customerId);
        responseJSON.put("active_time", (Object)InventoryUtil.getInstance().getActiveTimeDetailsJSON(deviceId));
        responseJSON.put("recent_users", (Object)InventoryUtil.getInstance().getRecentUsersDetailsJSON(deviceId));
        responseJSON.put("system_activity", (Object)InventoryUtil.getInstance().getSystemActivityDetailsJSON(deviceId));
        responseJSON.put("login_activity", (Object)InventoryUtil.getInstance().getSystemLoginActivityDetailsJSON(deviceId));
        return responseJSON;
    }
    
    private void addPrivacyKeys(final JSONObject privacyJSON, final JSONArray devicesJSONArray) throws Exception {
        try {
            JSONArray applicableForJSONArray = new JSONArray();
            if (privacyJSON.has("applicable_for")) {
                applicableForJSONArray = privacyJSON.optJSONArray("applicable_for");
            }
            else {
                applicableForJSONArray.put(1);
                applicableForJSONArray.put(2);
            }
            final int fetchLocation = privacyJSON.optInt("fetch_location", 0);
            Boolean corpFetchLocation = true;
            Boolean personalFetchLocation = true;
            if (applicableForJSONArray.length() == 2 && fetchLocation != 0) {
                corpFetchLocation = false;
                personalFetchLocation = false;
            }
            else {
                final int applicableFor = applicableForJSONArray.getInt(0);
                if (applicableFor == 1 && fetchLocation != 0) {
                    corpFetchLocation = false;
                }
                else if (applicableFor == 2 && fetchLocation != 0) {
                    personalFetchLocation = false;
                }
            }
            for (int i = 0; i < devicesJSONArray.length(); ++i) {
                final JSONObject tempJSON = devicesJSONArray.getJSONObject(i);
                if (!tempJSON.getBoolean("is_removed")) {
                    final int ownedBy = tempJSON.getInt("owned_by");
                    if (ownedBy == 2) {
                        tempJSON.put("collect_location", (Object)personalFetchLocation);
                        if (!personalFetchLocation) {
                            tempJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.privacy.location_personal_devices", new Object[0]));
                        }
                    }
                    else if (ownedBy == 1) {
                        tempJSON.put("collect_location", (Object)corpFetchLocation);
                        if (!corpFetchLocation) {
                            tempJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.privacy.location_corp_devices", new Object[0]));
                        }
                    }
                    devicesJSONArray.put(i, (Object)tempJSON);
                }
            }
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getDeviceDistributionListForProfile()  >   Error ", e);
            throw e;
        }
    }
    
    public static int getPlatformType(final String platform) {
        int platformType = -1;
        switch (platform) {
            case "ios": {
                platformType = 1;
                break;
            }
            case "android": {
                platformType = 2;
                break;
            }
            case "windows": {
                platformType = 3;
                break;
            }
            case "chrome": {
                platformType = 4;
                break;
            }
        }
        return platformType;
    }
    
    private JSONObject getDeviceJSON(final org.json.simple.JSONObject tempJSON) throws JSONException {
        try {
            final JSONObject deviceJSON = new JSONObject();
            if (tempJSON.get((Object)"MANAGED_STATUS") != null) {
                final int enrollStatus = (int)tempJSON.get((Object)"MANAGED_STATUS");
                deviceJSON.put("managed_status", enrollStatus);
                switch (enrollStatus) {
                    case 2: {
                        final JSONObject user = new JSONObject();
                        deviceJSON.put("device_id", (Object)tempJSON.get((Object)"RESOURCE_ID").toString());
                        deviceJSON.put("udid", (Object)tempJSON.get((Object)"UDID").toString());
                        if (tempJSON.containsKey((Object)"SERIAL_NUMBER")) {
                            deviceJSON.put("serial_number", (Object)tempJSON.get((Object)"SERIAL_NUMBER").toString());
                        }
                        user.put("user_email", tempJSON.get((Object)"EMAIL_ADDRESS"));
                        final int platformType = Integer.valueOf(tempJSON.get((Object)"PLATFORM_TYPE").toString());
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
                        deviceJSON.put("model", tempJSON.get((Object)"MODEL"));
                        deviceJSON.put("device_type", tempJSON.get((Object)"MODEL_TYPE"));
                        if (0 == (int)tempJSON.get((Object)"OWNED_BY")) {
                            deviceJSON.put("owned_by", tempJSON.get((Object)"OWNED_BY"));
                        }
                        else {
                            deviceJSON.put("owned_by", tempJSON.get((Object)"OWNED_BY"));
                        }
                        deviceJSON.put("product_name", tempJSON.get((Object)"PRODUCT_NAME"));
                        deviceJSON.put("os_version", tempJSON.get((Object)"OS_VERSION"));
                        deviceJSON.put("device_capacity", tempJSON.get((Object)"DEVICE_CAPACITY"));
                        user.put("user_name", tempJSON.get((Object)"NAME"));
                        user.put("user_id", (Object)String.valueOf(tempJSON.get((Object)"MANAGED_USER_ID")));
                        deviceJSON.put("device_name", tempJSON.get((Object)"DEVICE_NAME"));
                        deviceJSON.put("last_contact_time", tempJSON.get((Object)"LAST_CONTACT_TIME"));
                        deviceJSON.put("user", (Object)user);
                        deviceJSON.put("is_removed", (Object)Boolean.FALSE);
                        break;
                    }
                    case 4:
                    case 7:
                    case 9:
                    case 10:
                    case 11: {
                        deviceJSON.put("device_id", (Object)deviceJSON.get("RESOURCE_ID").toString());
                        deviceJSON.put("is_removed", (Object)Boolean.TRUE);
                        break;
                    }
                }
            }
            else {
                deviceJSON.put("device_id", tempJSON.get((Object)"RESOURCE_ID"));
                deviceJSON.put("is_removed", (Object)Boolean.TRUE);
            }
            return deviceJSON;
        }
        catch (final JSONException e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getDeviceJSON()  >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    private SelectQuery getAlreadyDistributedDevicesQuery(final Long profileID, final Long collectionID, final Long customerID, final Long businessStoreID) {
        final SelectQuery alreadyCollectionDistributedQuery = this.getDevicesBaseQuery();
        this.addProfileQuery(alreadyCollectionDistributedQuery);
        this.addSelectColForDevicesQuery(alreadyCollectionDistributedQuery);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria disassociationCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        alreadyCollectionDistributedQuery.setCriteria(alreadyCollectionDistributedQuery.getCriteria().and(profileCriteria).and(collectionCriteria).and(customerCriteria).and(disassociationCriteria));
        if (businessStoreID != -1L) {
            this.addBusinesssStoreCriteria(alreadyCollectionDistributedQuery, businessStoreID);
        }
        return alreadyCollectionDistributedQuery;
    }
    
    private SelectQuery getAlreadyDistributedDevicesCountQuery(final Long profileID, final Long collectionID, final Long customerID, final Long businessStoreID) {
        final SelectQuery alreadyCollectionDistributedCountQuery = this.getDevicesBaseQuery();
        this.addProfileQuery(alreadyCollectionDistributedCountQuery);
        final ArrayList<Column> columnList = (ArrayList<Column>)alreadyCollectionDistributedCountQuery.getSelectColumns();
        for (final Column column : columnList) {
            alreadyCollectionDistributedCountQuery.removeSelectColumn(column);
        }
        Column countColumn = Column.getColumn("Resource", "NAME");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        alreadyCollectionDistributedCountQuery.addSelectColumn(countColumn);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria disassociationCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        alreadyCollectionDistributedCountQuery.setCriteria(profileCriteria.and(collectionCriteria).and(customerCriteria).and(disassociationCriteria));
        if (businessStoreID != -1L) {
            this.addBusinesssStoreCriteria(alreadyCollectionDistributedCountQuery, businessStoreID);
        }
        return alreadyCollectionDistributedCountQuery;
    }
    
    private SelectQuery getYetToUpdateDevicesQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) {
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        final SelectQuery selectQuery = this.getDevicesBaseQuery();
        this.addProfileQuery(selectQuery);
        this.addSelectColForDevicesQuery(selectQuery);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 1);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(collectionCriteria).and(profileTypeCriteria).and(deleteCriteria));
        if (profileType == 2) {
            selectQuery.addJoin(new Join("RecentProfileForResource", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            final Long releaseLabelIdForAppCollectionId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionId);
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelIdForAppCollectionId, 0)));
        }
        if (businessStoreID != -1L) {
            this.addBusinesssStoreCriteria(selectQuery, businessStoreID);
        }
        return selectQuery;
    }
    
    private SelectQuery getYetToUpdateDevicesCountQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) {
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        final SelectQuery selectQuery = this.getDevicesBaseQuery();
        this.addProfileQuery(selectQuery);
        final ArrayList<Column> selectColumnsList = (ArrayList<Column>)selectQuery.getSelectColumns();
        for (final Column selectColumn : selectColumnsList) {
            selectQuery.removeSelectColumn(selectColumn);
        }
        Column countColumn = Column.getColumn("Resource", "RESOURCE_ID");
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        selectQuery.addSelectColumn(countColumn);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 1);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(collectionCriteria).and(profileTypeCriteria).and(deleteCriteria));
        if (profileType == 2) {
            selectQuery.addJoin(new Join("RecentProfileForResource", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            final Long releaseLabelIdForAppCollectionId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionId);
            selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelIdForAppCollectionId, 0)));
        }
        if (businessStoreID != -1L) {
            this.addBusinesssStoreCriteria(selectQuery, businessStoreID);
        }
        return selectQuery;
    }
    
    private void addProfileQuery(final SelectQuery selectQuery) {
        final Join recentProfileToResourceJoin = new Join("Resource", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join profileToCustomerRelJoin = new Join("RecentProfileForResource", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profileJoin = new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        selectQuery.addJoin(recentProfileToResourceJoin);
        selectQuery.addJoin(profileToCustomerRelJoin);
        selectQuery.addJoin(profileJoin);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
    }
    
    private SelectQuery getYetToApplyDevicesQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) throws DataAccessException, Exception {
        final SelectQuery selectQuery = this.getDevicesBaseQuery();
        final List deviceList = this.getDistributedDeviceListForProfile(profileId, collectionId, customerId, profileType, businessStoreID);
        this.addSelectColForDevicesQuery(selectQuery);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deviceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceList.toArray(), 9);
        selectQuery.setCriteria(selectQuery.getCriteria().and(deviceCriteria).and(customerCriteria));
        if (profileType == 2) {
            final int collectionPlatformType = ProfileUtil.getInstance().getPlatformType(profileId);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)collectionPlatformType, 0);
            selectQuery.setCriteria(selectQuery.getCriteria().and(platformCriteria));
        }
        return selectQuery;
    }
    
    private void addBusinesssStoreCriteria(final SelectQuery selectQuery, final Long businessStoreID) {
        final Criteria joinCri1 = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
        final Criteria joinCri2 = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0);
        final Criteria joinCri3 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
        selectQuery.addJoin(new Join("RecentProfileForResource", "MDMResourceToDeploymentConfigs", joinCri1.and(joinCri2).and(joinCri3), 2));
    }
    
    private SelectQuery getYetToApplyDevicesCountQuery(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) throws DataAccessException, Exception {
        final SelectQuery selectQuery = this.getDevicesBaseQuery();
        final List deviceList = this.getDistributedDeviceListForProfile(profileId, collectionId, customerId, profileType, businessStoreID);
        this.addSelectColForDevicesCountQuery(selectQuery);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deviceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceList.toArray(), 9);
        selectQuery.setCriteria(deviceCriteria.and(customerCriteria));
        if (profileType == 2) {
            final int collectionPlatformType = ProfileUtil.getInstance().getPlatformType(profileId);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)collectionPlatformType, 0);
            selectQuery.setCriteria(selectQuery.getCriteria().and(platformCriteria));
        }
        return selectQuery;
    }
    
    private List getDistributedDeviceListForProfile(final Long profileId, final Long collectionId, final Long customerId, final int profileType, final Long businessStoreID) throws DataAccessException, Exception {
        try {
            final List deviceList = new ArrayList();
            final int platformType = ProfileUtil.getInstance().getPlatformType(profileId);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            final Join profileToCustomerRelJoin = new Join("RecentProfileForResource", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileJoin = new Join("ProfileToCustomerRel", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(profileToCustomerRelJoin);
            selectQuery.addJoin(profileJoin);
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
            final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria deleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(profileTypeCriteria).and(deleteCriteria));
            if (profileType == 2) {
                selectQuery.addJoin(new Join("RecentProfileForResource", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
                selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
                final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
                final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), 0);
                selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppCatalogToResource", appGroupCriteria.and(resourceCriteria), 2));
                selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
                selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "ApprovedAppToColln", 2));
                selectQuery.setCriteria(selectQuery.getCriteria().and(AppVersionHandler.getInstance(platformType).getDistributedDeviceListForAppCriteria(collectionId, profileId)));
            }
            if (businessStoreID != -1L) {
                this.addBusinesssStoreCriteria(selectQuery, businessStoreID);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("RecentProfileForResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long deviceId = (Long)row.get("RESOURCE_ID");
                deviceList.add(deviceId);
            }
            return deviceList;
        }
        catch (final DataAccessException e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getDistributedDeviceListForProfile()  >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject getCommandHistoryForDevice(final JSONObject apiRequestJSON) {
        try {
            final Long deviceId = APIUtil.getResourceID(apiRequestJSON, "device_id");
            Integer historyDays = APIUtil.getIntegerFilter(apiRequestJSON, "days");
            if (historyDays == -1) {
                historyDays = null;
            }
            Long startTime = APIUtil.getLongFilter(apiRequestJSON, "start_time");
            if (startTime == -1L) {
                startTime = null;
            }
            Long endTime = APIUtil.getLongFilter(apiRequestJSON, "end_time");
            if (endTime == -1L) {
                endTime = null;
            }
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            this.validateIfDeviceExists(deviceId, customerId);
            final ArrayList allowedSecurityCommandList = MDMUtil.getInstance().getSecurityCommandList();
            allowedSecurityCommandList.add("LostModeDeviceLocation");
            final SelectQuery selectQuery = this.getCommandHistoryBaseQuery();
            Criteria securityCommandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)allowedSecurityCommandList.toArray(), 8, false);
            if (historyDays != null && historyDays > 0) {
                securityCommandCriteria = securityCommandCriteria.and(new Criteria(Column.getColumn("CommandHistory", "ADDED_TIME"), (Object)(MDMUtil.getCurrentTimeInMillis() - historyDays * 24 * 60 * 60 * 1000L), 4));
            }
            else if (startTime != null && endTime != null && startTime > 0L && endTime > 0L && endTime > startTime) {
                securityCommandCriteria = securityCommandCriteria.and(new Criteria(Column.getColumn("CommandHistory", "ADDED_TIME"), (Object)startTime, 4)).and(new Criteria(Column.getColumn("CommandHistory", "ADDED_TIME"), (Object)endTime, 6));
            }
            else {
                if (historyDays != null && historyDays <= 0) {
                    throw new APIHTTPException("COM0024", new Object[] { "days cannot be less than 0" });
                }
                if (startTime != null && endTime != null && startTime > endTime) {
                    throw new APIHTTPException("COM0024", new Object[] { "start_time cannot be greater than end_time" });
                }
                if (startTime != null && startTime < 0L) {
                    throw new APIHTTPException("COM0024", new Object[] { "start_time cannot be less than 0" });
                }
                if (endTime != null && endTime < 0L) {
                    throw new APIHTTPException("COM0024", new Object[] { "end_time cannot be less than 0" });
                }
            }
            final Criteria resourceCriteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)deviceId, 0);
            selectQuery.setCriteria(securityCommandCriteria.and(resourceCriteria));
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(apiRequestJSON);
            final DeltaTokenUtil deltaTokenUtil = APIUtil.getNewInstance().getDeltaTokenForAPIRequest(apiRequestJSON);
            if (deltaTokenUtil != null && System.currentTimeMillis() - deltaTokenUtil.getRequestTimestamp() > 36000000L) {
                throw new APIHTTPException("COM0021", new Object[0]);
            }
            final Range range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
            selectQuery.setRange(range);
            selectQuery.addSortColumn(new SortColumn("CommandHistory", "COMMAND_HISTORY_ID", false));
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            final JSONObject responseJSON = new JSONObject();
            JSONArray commandsJSONArray = new JSONArray();
            if (dmDataSetWrapper != null) {
                commandsJSONArray = this.getCommandHistoryJSONArrayForDS(dmDataSetWrapper, deviceId);
            }
            final JSONObject meta = new JSONObject();
            final int commandCount = commandsJSONArray.length();
            meta.put("total_record_count", commandCount);
            responseJSON.put("commands", (Object)commandsJSONArray);
            responseJSON.put("metadata", (Object)meta);
            final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(String.valueOf(apiRequestJSON.getJSONObject("msg_header").get("request_url")));
            if (pagingUtil.getNextToken(commandCount) == null || pagingUtil.getPreviousToken() == null) {
                responseJSON.put("delta-token", (Object)newDeltaTokenUtil.getDeltaToken());
            }
            if (commandCount != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(commandCount);
                if (pagingJSON != null) {
                    responseJSON.put("paging", (Object)pagingJSON);
                }
            }
            return responseJSON;
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getCommandHistoryForDevice()  >   Error ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONArray getCommandHistoryJSONArrayForDS(final DMDataSetWrapper dmDataSetWrapper, final Long deviceId) throws Exception {
        final ArrayList commandHistoryList = new ArrayList();
        while (dmDataSetWrapper.next()) {
            commandHistoryList.add(dmDataSetWrapper.getValue("COMMAND_HISTORY_ID"));
        }
        final SelectQuery selectQuery = this.getDevicesCommandHistoryQuery();
        this.addSelectColumnsForCommandHistoryQuery(selectQuery);
        selectQuery.setCriteria(new Criteria(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryList.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator commandHistoryIterator = dataObject.getRows("CommandHistory");
        final JSONArray commandsJSONArray = new JSONArray();
        final DeviceDetails deviceDetails = new DeviceDetails(deviceId);
        final String deviceName = deviceDetails.name;
        final int platform = deviceDetails.platform;
        final String udid = deviceDetails.udid;
        while (commandHistoryIterator.hasNext()) {
            final JSONObject commandJSON = new JSONObject();
            final Row commandHistoryRow = commandHistoryIterator.next();
            final Long commandHistoryId = (Long)commandHistoryRow.get("COMMAND_HISTORY_ID");
            final Long resourceId = (Long)commandHistoryRow.get("RESOURCE_ID");
            final Long commandId = (Long)commandHistoryRow.get("COMMAND_ID");
            final Long addedTime = (Long)commandHistoryRow.get("ADDED_TIME");
            final Long addedBy = (Long)commandHistoryRow.get("ADDED_BY");
            String remarks = (String)commandHistoryRow.get("REMARKS");
            final String remarksArgs = (String)commandHistoryRow.get("REMARKS_ARGS");
            final Criteria commandIdCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandId, 0);
            final Criteria userCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)addedBy, 0);
            final String addedByName = (String)dataObject.getRow("AaaUser", userCriteria).get("FIRST_NAME");
            Criteria commandHistoryCriteria = new Criteria(Column.getColumn("CommandAuditLog", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
            final String commandType = (String)dataObject.getRow("MdCommands", commandIdCriteria).get("COMMAND_TYPE");
            final String commandName = DeviceCommandRepository.getInstance().getI18NCommandName(commandType);
            final Iterator commandAuditIterator = dataObject.getRows("CommandAuditLog", commandHistoryCriteria);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            final int managedStatus = (int)dataObject.getRow("ManagedDevice", resourceCriteria).get("MANAGED_STATUS");
            final JSONArray commandLifeCycleJSONArray = new JSONArray();
            while (commandAuditIterator.hasNext()) {
                final JSONObject commandLifeJSON = new JSONObject();
                final Row commandAuditLogRow = commandAuditIterator.next();
                final int commandStatus = (int)commandAuditLogRow.get("COMMAND_STATUS");
                final Long updatedTime = (Long)commandAuditLogRow.get("UPDATED_TIME");
                if (commandStatus == 0) {
                    commandHistoryCriteria = new Criteria(Column.getColumn("CommandError", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
                    int errorCode = -1;
                    String kbUrl = "--";
                    final Row commandErrorRow = dataObject.getRow("CommandError", commandHistoryCriteria);
                    if (commandErrorRow != null) {
                        errorCode = (int)commandErrorRow.get("ERROR_CODE");
                        final Criteria errorCodeCriteria = new Criteria(Column.getColumn("ErrorCode", "ERROR_CODE"), (Object)errorCode, 0);
                        final Row errorCodeRow = dataObject.getRow("ErrorCode", errorCodeCriteria);
                        kbUrl = ((errorCodeRow.get("KB_URL") != null) ? String.valueOf(errorCodeRow.get("KB_URL")) : "--");
                        if (!MDMUtil.isStringEmpty(kbUrl)) {
                            kbUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbUrl);
                        }
                    }
                    commandLifeJSON.put("error_code", errorCode);
                    commandLifeJSON.put("kb_url", (Object)kbUrl);
                }
                final String auditRemarks = new LostModeDataHandler().getLostModeDeviceInfo(deviceId, platform, true).optString("AUDIT_MESSAGE", "--");
                final JSONObject requestJSON = new JSONObject();
                requestJSON.put("RESOURCE_ID", (Object)resourceId);
                requestJSON.put("COMMAND_TYPE", (Object)commandType);
                requestJSON.put("COMMAND_STATUS", commandStatus);
                requestJSON.put("NAME", (Object)deviceName);
                requestJSON.put("AUDIT_MESSAGE", (Object)auditRemarks);
                requestJSON.put("REMARKS", (Object)remarks);
                requestJSON.put("UDID", (Object)udid);
                requestJSON.put("PLATFORM_TYPE", deviceDetails.platform);
                remarks = DeviceCommandRepository.getInstance().getCommandRemarks(requestJSON);
                switch (commandStatus) {
                    case 2: {
                        commandLifeJSON.put("status_code", 2);
                        commandLifeJSON.put("status_description", (Object)"Command Success");
                        break;
                    }
                    case 1: {
                        commandLifeJSON.put("status_code", 1);
                        commandLifeJSON.put("status_description", (Object)"Command Initiated");
                        break;
                    }
                    case 3: {
                        commandLifeJSON.put("status_code", 3);
                        commandLifeJSON.put("status_description", (Object)"Command Not Initiated");
                        break;
                    }
                    case -1: {
                        commandLifeJSON.put("status_code", -1);
                        commandLifeJSON.put("status_description", (Object)"Command Timed Out");
                        break;
                    }
                    case 5: {
                        break;
                    }
                    default: {
                        commandLifeJSON.put("status_code", 0);
                        commandLifeJSON.put("status_description", (Object)"Command Failed");
                        break;
                    }
                }
                commandLifeJSON.put("command_name", (Object)commandName);
                commandLifeJSON.put("command_id", (Object)commandId);
                commandLifeJSON.put("updated_time", (Object)updatedTime);
                commandLifeJSON.put("added_by_name", (Object)addedByName);
                commandLifeJSON.put("added_by", (Object)addedBy);
                commandLifeJSON.put("remarks", (Object)remarks);
                if (commandStatus != 5) {
                    commandLifeCycleJSONArray.put((Object)commandLifeJSON);
                }
            }
            commandJSON.put("command_life", (Object)commandLifeCycleJSONArray);
            commandJSON.put("device_id", (Object)resourceId);
            commandJSON.put("managed_status", managedStatus);
            commandJSON.put("command_history_id", (Object)commandHistoryId);
            commandJSON.put("command_id", (Object)commandId);
            commandJSON.put("command_name", (Object)commandName);
            commandJSON.put("command_status", commandHistoryRow.get("COMMAND_STATUS"));
            commandJSON.put("added_time", (Object)addedTime);
            commandJSON.put("added_by", (Object)addedBy);
            commandJSON.put("added_by_name", (Object)addedByName);
            commandJSON.put("remarks", (Object)remarks);
            commandJSON.put("remarks_args", (Object)remarksArgs);
            commandsJSONArray.put((Object)commandJSON);
        }
        return commandsJSONArray;
    }
    
    private void addSelectColumnsForCommandHistoryQuery(final SelectQuery selectQuery) {
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "ADDED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "ADDED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "UPDATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "REMARKS"));
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "REMARKS_ARGS"));
        selectQuery.addSelectColumn(Column.getColumn("CommandAuditLog", "COMMAND_HISTORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandAuditLog", "UPDATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("CommandAuditLog", "COMMAND_AUDIT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandAuditLog", "COMMAND_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("CommandError", "COMMAND_ERROR_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandError", "COMMAND_HISTORY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CommandError", "ERROR_CODE"));
        selectQuery.addSelectColumn(Column.getColumn("ErrorCode", "ERROR_CODE"));
        selectQuery.addSelectColumn(Column.getColumn("ErrorCode", "KB_URL"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
    }
    
    private SelectQuery getCommandHistoryBaseQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandHistory"));
        final Join mdCommandsJoin = new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
        selectQuery.addJoin(mdCommandsJoin);
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
        return selectQuery;
    }
    
    private SelectQuery getDevicesCommandHistoryQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandHistory"));
        final Join commandAuditLogJoin = new Join("CommandHistory", "CommandAuditLog", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
        final Join managedDeviceJoin = new Join("CommandHistory", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join commandErrorJoin = new Join("CommandHistory", "CommandError", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1);
        final Join errorCodeJoin = new Join("CommandError", "ErrorCode", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 1);
        final Join aaaUserJoin = new Join("CommandHistory", "AaaUser", new String[] { "ADDED_BY" }, new String[] { "USER_ID" }, 2);
        final Join mdCommandsJoin = new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
        selectQuery.addJoin(commandAuditLogJoin);
        selectQuery.addJoin(managedDeviceJoin);
        selectQuery.addJoin(commandErrorJoin);
        selectQuery.addJoin(errorCodeJoin);
        selectQuery.addJoin(aaaUserJoin);
        selectQuery.addJoin(mdCommandsJoin);
        return selectQuery;
    }
    
    public Object getFirmwarePasswordForDevice(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long customerID = APIUtil.getCustomerID(message);
            final Long userID = APIUtil.getUserID(message);
            this.validateIfDeviceExists(deviceId, customerID);
            JSONObject response = MacFirmwareUtil.getFirmwarePasswordForDevice(deviceId, userID);
            if (response == null || response.length() == 0 || !response.has("resource_id") || !response.has("firmware_password")) {
                response.put("status", 404);
                return response;
            }
            response = JSONUtil.getInstance().convertLongToString(response);
            return response;
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getNextPollTime()  >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getNextPollTime(final JSONObject apiRequestJSON) {
        try {
            final Long deviceId = APIUtil.getResourceID(apiRequestJSON, "device_id");
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            this.validateIfDeviceExists(deviceId, customerId);
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(deviceId);
            if (platformType != 2 && platformType != 3) {
                throw new APIHTTPException("DEV001", new Object[] { MDMEnrollmentUtil.getPlatformString(platformType) });
            }
            return this.getNextPollTimeForDevice(deviceId, customerId, platformType);
        }
        catch (final DataAccessException | JSONException e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getNextPollTime()  >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject getNextPollTimeForDevice(final Long deviceId, final Long customerId, final int platformType) throws JSONException, DataAccessException {
        final Criteria deviceCriteria = new Criteria(Column.getColumn("AgentContact", "RESOURCE_ID"), (Object)deviceId, 0);
        Long lastContactTime = null;
        final JSONObject commModeJSON = MDMAgentSettingsHandler.getInstance().getNotificaitonServiceJSON(platformType, customerId);
        if (commModeJSON.has("SERVICE_TYPE".toLowerCase())) {
            final int service = commModeJSON.getInt("SERVICE_TYPE".toLowerCase());
            if (service != 2) {
                throw new APIHTTPException("DEV002", new Object[] { deviceId });
            }
            final DataObject dataObject = MDMUtil.getPersistence().get("AgentContact", deviceCriteria);
            if (!dataObject.isEmpty()) {
                lastContactTime = (Long)dataObject.getFirstRow("AgentContact").get("LAST_CONTACT_TIME");
            }
            final JSONObject pollingConfig = MDMAgentSettingsHandler.getInstance().getScheduledPollingConfig();
            final int pollingInterval = pollingConfig.getInt("POLLING_INTERVAL");
            commModeJSON.put("next_poll_time", lastContactTime + pollingInterval * 60000);
        }
        return commModeJSON;
    }
    
    public JSONObject getCountsForInventoryDevicesTab(final JSONObject requestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final int jailBrokenCount = MDMUtil.getInstance().getJailBrokenDeviceCount(customerId);
            final int rootedCount = MDMUtil.getInstance().getRootedDeviceCount(customerId);
            final int blackListedDeviceCount = MDMUtil.getInstance().getBlackListAppCount(customerId);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("jail_broken_device_count", jailBrokenCount);
            responseJSON.put("rooted_device_count", rootedCount);
            responseJSON.put("blacklisted_app_count", blackListedDeviceCount);
            return responseJSON;
        }
        catch (final JSONException e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getCountsForInventoryDevicesTab()  >   Error ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object getFirmwareDetailsForDevice(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long customerID = APIUtil.getCustomerID(message);
            this.validateIfDeviceExists(deviceId, customerID);
            JSONObject response = MacFirmwareUtil.getFirmwareDeviceDetails(deviceId);
            response = JSONUtil.getInstance().convertLongToString(response);
            return response;
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, "MacFirmware: Exception in getFirmwareDetailsForDevice API", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getRecentCommandStatusForDevice(final JSONObject apiRequestJSON) {
        try {
            final JSONObject responseJSON = new JSONObject();
            final CommandAPIHandler commandAPIHandler = new CommandAPIHandler();
            final Long deviceId = APIUtil.getResourceID(apiRequestJSON, "device_id");
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            this.validateIfDeviceExists(deviceId, customerId);
            final DeviceDetails deviceDetails = new DeviceDetails(deviceId);
            final SelectQuery commandHistoryQuery = commandAPIHandler.getCommandHistoryQuery();
            final Criteria deviceIdCriteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)deviceId, 0);
            final ArrayList allowedCommandList = MDMUtil.getInstance().getSecurityCommandList();
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)allowedCommandList.toArray(), 8);
            final ArrayList commandUUIDExcludeList = new ArrayList();
            commandUUIDExcludeList.add("GetLocationForLostDevice");
            final Criteria excludeCommandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUIDExcludeList.toArray(), 9);
            final Criteria userCriteria = new Criteria(Column.getColumn("CommandHistory", "ADDED_BY"), (Object)userId, 0);
            commandHistoryQuery.setCriteria(deviceIdCriteria.and(commandCriteria).and(userCriteria).and(excludeCommandCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(commandHistoryQuery);
            long historyCmdTime = 0L;
            int status = 3;
            Long errorCode = null;
            String kburl = null;
            String commandClientName = null;
            Long commandId = null;
            String commandName = null;
            String commandType = "";
            String remarks = null;
            String auditMessage = "--";
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row historyRow = dataObject.getFirstRow("CommandHistory");
                historyCmdTime = (long)historyRow.get("ADDED_TIME");
                commandId = (Long)historyRow.get("COMMAND_ID");
                commandType = (String)dataObject.getRow("MdCommands", new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandId, 0)).get("COMMAND_TYPE");
                commandName = DeviceCommandRepository.getInstance().getI18NCommandName(commandType);
                status = (int)historyRow.get("COMMAND_STATUS");
                remarks = (String)historyRow.get("REMARKS");
                if (status == 0) {
                    final JSONObject errorJSON = new CommandStatusHandler().getRecentCommandInfo(deviceId, commandId);
                    errorCode = JSONUtil.optLongForUVH(errorJSON, "ERROR_CODE", Long.valueOf(-1L));
                    kburl = ErrorCodeHandler.getInstance().getKBURL((long)errorCode);
                }
                if (commandType.equalsIgnoreCase("DisableLostMode") || commandType.equalsIgnoreCase("EnableLostMode")) {
                    auditMessage = new LostModeDataHandler().getLostModeDeviceInfo(deviceId, ManagedDeviceHandler.getInstance().getPlatformType(deviceId), true).optString("AUDIT_MESSAGE");
                }
            }
            if (MDMUtil.getCurrentTimeInMillis() > historyCmdTime + 300000L) {
                status = -1;
            }
            commandClientName = new CommandFacade().getCommandClientName(commandType);
            responseJSON.put("command_id", (Object)commandId);
            responseJSON.put("command_name", (Object)commandName);
            responseJSON.put("error_code", (Object)errorCode);
            responseJSON.put("kb_url", (Object)kburl);
            responseJSON.put("added_time", historyCmdTime);
            responseJSON.put("name", (Object)commandClientName);
            final String udid = deviceDetails.udid;
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("RESOURCE_ID", (Object)deviceId);
            requestJSON.put("COMMAND_TYPE", (Object)commandType);
            requestJSON.put("COMMAND_STATUS", status);
            requestJSON.put("NAME", (Object)deviceDetails.name);
            requestJSON.put("ERROR_CODE", (Object)errorCode);
            requestJSON.put("AUDIT_MESSAGE", (Object)auditMessage);
            requestJSON.put("REMARKS", (Object)remarks);
            requestJSON.put("UDID", (Object)udid);
            requestJSON.put("PLATFORM_TYPE", deviceDetails.platform);
            switch (status) {
                case 2: {
                    responseJSON.put("status_code", 2);
                    responseJSON.put("status_description", (Object)"Command Success");
                    responseJSON.put("remarks", (Object)DeviceCommandRepository.getInstance().getCommandRemarks(requestJSON));
                    break;
                }
                case 1: {
                    responseJSON.put("status_code", 1);
                    responseJSON.put("status_description", (Object)"Command Initiated");
                    responseJSON.put("remarks", (Object)DeviceCommandRepository.getInstance().getCommandRemarks(requestJSON));
                    break;
                }
                case 3: {
                    responseJSON.put("status_code", 3);
                    responseJSON.put("status_description", (Object)"Command Not Initiated");
                    break;
                }
                case -1: {
                    responseJSON.put("status_code", -1);
                    responseJSON.put("status_description", (Object)"Command Timed Out");
                    responseJSON.put("remarks", (Object)I18N.getMsg("dc.mdm.actions.will_be_executed_later", new Object[] { commandName, deviceDetails.name, remarks }));
                    break;
                }
                default: {
                    responseJSON.put("status_code", 0);
                    responseJSON.put("status_description", (Object)"Command Failed");
                    responseJSON.put("remarks", (Object)DeviceCommandRepository.getInstance().getCommandRemarks(requestJSON));
                    break;
                }
            }
            return responseJSON;
        }
        catch (final Exception e) {
            DeviceFacade.logger.log(Level.SEVERE, " -- getRecentCommandStatusForDevice()  >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDeviceDistributionSummary(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long customerId = APIUtil.getCustomerID(message);
            this.validateIfDeviceExists(deviceId, customerId);
            DeviceFacade.logger.log(Level.INFO, "get device summary, device id: {0}", deviceId);
            final JSONObject resultJson = new JSONObject();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDeviceExtn"));
            selectQuery.addJoin(new Join("ManagedDeviceExtn", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION"));
            selectQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
            deviceCriteria = deviceCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            selectQuery.setCriteria(deviceCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { deviceId });
            }
            final Row managedDeviceRow = dataObject.getRow("ManagedDevice");
            resultJson.put("device_id", managedDeviceRow.get("RESOURCE_ID"));
            resultJson.put("platform_type", managedDeviceRow.get("PLATFORM_TYPE"));
            resultJson.put("agent_version", managedDeviceRow.get("AGENT_VERSION"));
            final Row managedDeviceExtnRow = dataObject.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), managedDeviceRow.get("RESOURCE_ID"), 0));
            resultJson.put("device_name", managedDeviceExtnRow.get("NAME"));
            final Row agentContactRow = dataObject.getRow("AgentContact", new Criteria(Column.getColumn("AgentContact", "RESOURCE_ID"), managedDeviceRow.get("RESOURCE_ID"), 0));
            resultJson.put("last_contact_time", agentContactRow.get("LAST_CONTACT_TIME"));
            final HashMap deviceSummaryMap = (HashMap)ProfileAssociateHandler.getInstance().getDeviceSummary(new ArrayList(Arrays.asList(deviceId)));
            int deviceAppCount = 0;
            int docDistributedCount = 0;
            int deviceProfileCount = 0;
            if (!deviceSummaryMap.isEmpty()) {
                final Properties deviceSummary = deviceSummaryMap.get(deviceId);
                deviceAppCount = ((deviceSummary.get("APP_COUNT") != null) ? ((Hashtable<K, Integer>)deviceSummary).get("APP_COUNT") : 0);
                docDistributedCount = ((deviceSummary.get("DOC_COUNT") != null) ? ((Hashtable<K, Integer>)deviceSummary).get("DOC_COUNT") : 0);
                deviceProfileCount = ((deviceSummary.get("PROFILE_COUNT") != null) ? ((Hashtable<K, Integer>)deviceSummary).get("PROFILE_COUNT") : 0);
            }
            final int actionCount = this.getDeviceActionCount(deviceId);
            resultJson.put("action_count", actionCount);
            resultJson.put("app_count", deviceAppCount);
            resultJson.put("doc_count", docDistributedCount);
            resultJson.put("profile_count", deviceProfileCount);
            final JSONArray groupArray = this.getAssociatedGroupName(deviceId);
            resultJson.put("group_count", groupArray.length());
            if (groupArray.length() > 0) {
                resultJson.put("group", (Object)groupArray);
            }
            return resultJson;
        }
        catch (final Exception ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Exception while getting summary for the profile", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getAssociatedGroupName(final Long resId) {
        DeviceFacade.logger.log(Level.INFO, "get Associated Group name, resId: {0}", resId);
        final JSONArray groupList = new JSONArray();
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        final Join customGroup = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join resourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        query.addJoin(customGroup);
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        query.addJoin(resourceJoin);
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        final Criteria cRes = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resId, 0);
        final Criteria gRes = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)new int[] { 9, 8 }, 9);
        query.setCriteria(cRes.and(gRes));
        try {
            final DataObject DO = MDMUtil.getPersistence().get(query);
            if (!DO.isEmpty()) {
                final Iterator iGroup = DO.getRows("Resource");
                while (iGroup.hasNext()) {
                    final JSONObject jsonObject = new JSONObject();
                    final Row row = iGroup.next();
                    jsonObject.put("group_id", row.get("RESOURCE_ID"));
                    jsonObject.put("group_name", row.get("NAME"));
                    groupList.put((Object)jsonObject);
                }
            }
        }
        catch (final Exception ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Exception while getting associated group name", ex);
        }
        return groupList;
    }
    
    private SelectQuery getEnrollmentRequestDetail(final Long deviceId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REGISTERED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUESTED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0));
        return selectQuery;
    }
    
    private SelectQuery getManagedDeviceValidationQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        return selectQuery;
    }
    
    public Map<Long, ArrayList> validateAndGetDevicesForAllCustomerIds(final List<Long> deviceIDs, final ArrayList customerIDs) throws Exception {
        final Map<Long, ArrayList> customerToDevicesMap = new HashMap<Long, ArrayList>();
        try {
            final SelectQuery selectQuery = this.getManagedDeviceValidationQuery();
            selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerIDs.toArray(), 8).and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceIDs.toArray(new Long[deviceIDs.size()]), 8)));
            final DataObject deviceDO = MDMUtil.getPersistence().get(selectQuery);
            if (!deviceDO.isEmpty()) {
                final Iterator deviceRows = deviceDO.getRows("Resource");
                while (deviceRows.hasNext()) {
                    final Row deviceRow = deviceRows.next();
                    final Long customerID = (Long)deviceRow.get("CUSTOMER_ID");
                    final Long deviceID = (Long)deviceRow.get("RESOURCE_ID");
                    if (customerToDevicesMap.get(customerID) == null) {
                        final ArrayList deviceList = new ArrayList();
                        deviceList.add(deviceID);
                        customerToDevicesMap.put(customerID, deviceList);
                    }
                    else {
                        final ArrayList deviceList = customerToDevicesMap.get(customerID);
                        deviceList.add(deviceID);
                        customerToDevicesMap.put(customerID, deviceList);
                    }
                    deviceIDs.remove(deviceID);
                }
            }
            if (deviceIDs.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(deviceIDs) });
            }
        }
        catch (final Exception ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Issue on validating device ids", ex);
            if (ex instanceof APIHTTPException) {
                throw ex;
            }
            throw ex;
        }
        return customerToDevicesMap;
    }
    
    public List<Long> getAllManagedDevicesExceptDeviceList(final List<Long> deviceList, final ArrayList customerArray) throws APIHTTPException {
        final List<Long> deviceIDList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = this.getManagedDeviceValidationQuery();
            selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerArray.toArray(), 8).and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(new Long[deviceList.size()]), 9)).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0)));
            final DataObject deviceDO = MDMUtil.getPersistence().get(selectQuery);
            if (!deviceDO.isEmpty()) {
                final Iterator deviceRows = deviceDO.getRows("ManagedDevice");
                while (deviceRows.hasNext()) {
                    final Row deviceRow = deviceRows.next();
                    final Long deviceId = (Long)deviceRow.get("RESOURCE_ID");
                    deviceIDList.add(deviceId);
                }
            }
        }
        catch (final Exception ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Issue on getting inverse of device ids", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return deviceIDList;
    }
    
    private int getDeviceActionCount(final Long deviceId) {
        int deviceActionCount = 0;
        try {
            final UnionQuery unionQuery = this.getDeviceActionHistoryUnionQuery(deviceId);
            final String uqString = RelationalAPI.getInstance().getSelectSQL((Query)unionQuery);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)uqString);
            while (dmDataSetWrapper.next()) {
                ++deviceActionCount;
            }
        }
        catch (final Exception ex) {
            DeviceFacade.logger.log(Level.SEVERE, "Exception in getting DeviceActionCount :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return deviceActionCount;
    }
    
    public UnionQuery getDeviceActionHistoryUnionQuery(final Long deviceId) {
        final Criteria deviceCriteria = new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)deviceId, 0);
        final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(new Table("DeviceActionHistory"));
        selectQuery1.addJoin(new Join("DeviceActionHistory", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1));
        final Column recent_action_id = new Column("DeviceActionHistory", "DEVICE_ACTION_ID").maximum();
        recent_action_id.setColumnAlias("DEVICE_ACTION_ID");
        selectQuery1.addSelectColumn(recent_action_id);
        final Column action = new Column("DeviceActionHistory", "ACTION_ID");
        selectQuery1.setCriteria(deviceCriteria);
        selectQuery1.addSelectColumn(action);
        final List groupByList = new ArrayList();
        groupByList.add(action);
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        selectQuery1.setGroupByClause(groupByClause);
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("DeviceActionHistory"));
        selectQuery2.addJoin(new Join("DeviceActionHistory", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1));
        final Column actionId = new Column("DeviceActionHistory", "DEVICE_ACTION_ID");
        actionId.setColumnAlias("DEVICE_ACTION_ID");
        selectQuery2.addSelectColumn(actionId);
        selectQuery2.addSelectColumn(action);
        selectQuery2.setCriteria(new Criteria(new Column("CommandHistory", "UPDATED_TIME"), (Object)(MDMUtil.getCurrentTimeInMillis() - 604800000), 4).and(deviceCriteria));
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)selectQuery1, (Query)selectQuery2, false);
        return unionQuery;
    }
    
    public SelectQuery getDevicesTreeBaseQuery() {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join resDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join device_userid = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join userdetails = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final Join userresource = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "USER_RESOURCE", 2);
        final Join customerInfoJoin = new Join("Resource", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2);
        final Join deviceToErReqJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join mddeviceinfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join modelinfoJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2);
        final Join device_deviceexten = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        query.addJoin(resDeviceJoin);
        query.addJoin(device_userid);
        query.addJoin(userdetails);
        query.addJoin(userresource);
        query.addJoin(customerInfoJoin);
        query.addJoin(deviceToErReqJoin);
        query.addJoin(mddeviceinfoJoin);
        query.addJoin(modelinfoJoin);
        query.addJoin(device_deviceexten);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        query.setCriteria(userNotInTrashCriteria.and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()));
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        return query;
    }
    
    public void addSelectColForDevicesTreeQuery(final SelectQuery query) {
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        query.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "DEVICE_NAME"));
    }
    
    static {
        DeviceFacade.logger = Logger.getLogger("MDMApiLogger");
    }
}
