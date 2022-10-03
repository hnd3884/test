package com.me.mdm.server.device.api.service;

import com.me.mdm.server.enrollment.ios.MacBootstrapTokenHandler;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.device.api.model.BootstrapTokenInfoModel;
import com.me.mdm.server.device.api.model.LostDeviceCountModel;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.server.profiles.api.model.ProfileModel;
import com.me.mdm.server.profiles.api.model.DeviceProfilesModel;
import com.me.mdm.server.device.api.model.WorkDataSecurityModel;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.api.model.ReleaseLabelDetailModel;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.mdm.server.device.api.model.AppDetailsModel;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.device.api.model.DeviceInstalledAppModel;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.device.api.model.CertifcateDetailsModel;
import com.me.mdm.server.inv.ios.DeviceInstalledCertificateDataHandler;
import com.me.mdm.server.device.api.model.DeviceCertificateDetailsModel;
import com.me.mdm.server.device.DeviceUnlockPinHandler;
import com.me.mdm.server.device.api.model.DeviceUnlockSettingsModel;
import com.me.mdm.server.device.api.model.NetworkUsageModel;
import com.me.mdm.server.device.api.model.KnoxDetailsModel;
import com.adventnet.sym.server.mdm.featuresettings.battery.MDMBatterySettingsDBHandler;
import com.me.mdm.server.device.api.model.SimModel;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.settings.wifi.MdDeviceWifiSSIDDBHandler;
import com.me.mdm.server.device.api.model.NetworkModel;
import com.me.mdm.server.device.api.model.SharedDeviceDetailModel;
import org.json.JSONArray;
import com.me.mdm.server.device.api.model.EFRPAccountDetailModel;
import com.me.mdm.server.device.api.model.SecurityModel;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.device.api.model.OsModel;
import java.util.Iterator;
import java.util.Map;
import com.me.mdm.server.device.api.model.LocationErrorModel;
import java.util.Calendar;
import com.me.mdm.server.device.api.model.DeviceLocationModel;
import com.me.mdm.server.location.LocationDataHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.me.mdm.server.settings.location.LocationErrorFactors;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.device.api.model.DeviceLocationListModel;
import com.me.mdm.server.device.api.model.DeviceLocation;
import com.me.mdm.server.device.api.model.IOSAccessibilitySettingsModel;
import com.me.mdm.server.device.api.model.DeviceFilevaultDetailsModel;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.device.api.model.LocationSettingModel;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.api.common.PojoUtil;
import com.me.mdm.server.device.api.model.PrivacySettingModel;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.mdm.server.device.api.model.DeviceDetailsModel;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.api.model.BaseAPIModel;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.device.api.model.DeviceExtnModel;
import java.util.logging.Level;
import com.me.mdm.server.device.api.model.DeviceUpdate;
import java.util.Arrays;
import com.me.mdm.server.customgroup.GroupFacade;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.paging.SearchUtil;
import org.json.JSONObject;
import com.me.mdm.api.paging.model.PagingResponse;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.server.device.api.model.DeviceSummaryModel;
import com.me.mdm.server.device.api.model.UserModel;
import java.util.List;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.server.device.api.model.MetaDataModel;
import com.me.mdm.server.device.api.model.DeviceModel;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.server.device.api.model.DeviceListModel;
import com.me.mdm.server.device.api.model.SearchDevice;
import java.util.logging.Logger;

public class DeviceService
{
    protected static Logger logger;
    
    public DeviceListModel getDevices(final SearchDevice searchDevice) throws Exception {
        final Criteria criteria = this.setDeviceCriteria(searchDevice);
        final PagingUtil pagingUtil = searchDevice.getPagingUtil();
        SelectQuery query = new DeviceFacade().getDevicesBaseQuery();
        final SelectQuery cQuery = new DeviceFacade().getDevicesBaseQuery();
        new DeviceFacade().addSelectColForDevicesQuery(query);
        new DeviceFacade().addSelectColForDevicesCountQuery(cQuery);
        if (searchDevice.isDeviceSummary()) {
            query = this.getDeviceSummaryQuery(query);
        }
        if (searchDevice.getGroupId() != null) {
            final Join groupRelJoin = new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1);
            query.addJoin(groupRelJoin);
            cQuery.addJoin(groupRelJoin);
        }
        new DeviceFacade().addCustomerFilter(query, searchDevice.getUserId(), searchDevice.getCustomerId());
        new DeviceFacade().addCustomerFilter(cQuery, searchDevice.getUserId(), searchDevice.getCustomerId());
        query.setCriteria(MDMDBUtil.andCriteria(query.getCriteria(), criteria));
        cQuery.setCriteria(MDMDBUtil.andCriteria(cQuery.getCriteria(), criteria));
        final int count = DBUtil.getRecordCount(cQuery);
        final DeviceListModel response = new DeviceListModel();
        final List<DeviceModel> result = new ArrayList<DeviceModel>();
        final MetaDataModel meta = new MetaDataModel();
        meta.setTotalCount(count);
        response.setMetadata(meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(searchDevice.getRequestUri());
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            response.setDeltaToken(newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            if (!searchDevice.isSelectAll()) {
                final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
                if (pagingJSON != null) {
                    response.setPaging(pagingJSON);
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
            else {
                query.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
            }
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final DeviceModel deviceModel = new DeviceModel();
                if (ds.getValue("MANAGED_STATUS") != null) {
                    final Integer enrollStatus = Integer.valueOf(String.valueOf(ds.getValue("MANAGED_STATUS")));
                    deviceModel.setManagedStatus(enrollStatus);
                    switch (enrollStatus) {
                        case 2: {
                            deviceModel.setDeviceId((Long)ds.getValue("RESOURCE_ID"));
                            deviceModel.setCustomerId((Long)ds.getValue("CUSTOMER_ID"));
                            deviceModel.setUdid(ds.getValue("UDID").toString());
                            if (ds.getValue("SERIAL_NUMBER") != null) {
                                deviceModel.setSerialNumber(ds.getValue("SERIAL_NUMBER").toString());
                            }
                            deviceModel.setLostModeEnabled(new LostModeDataHandler().isLostMode(Long.valueOf(String.valueOf(ds.getValue("RESOURCE_ID")))));
                            deviceModel.setUserMail(ds.getValue("EMAIL_ADDRESS").toString());
                            deviceModel.setPlatformTypeId(Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString()));
                            if (ds.getValue("MODEL") != null) {
                                deviceModel.setModel(ds.getValue("MODEL").toString());
                            }
                            if (ds.getValue("MODEL_TYPE") != null) {
                                deviceModel.setDeviceType(ds.getValue("MODEL_TYPE").toString());
                            }
                            deviceModel.setOwnedBy(ds.getValue("OWNED_BY").toString());
                            if (ds.getValue("PRODUCT_NAME") != null) {
                                deviceModel.setProductName(ds.getValue("PRODUCT_NAME").toString());
                            }
                            if (ds.getValue("OS_VERSION") != null) {
                                deviceModel.setOsVersion(ds.getValue("OS_VERSION").toString());
                            }
                            if (ds.getValue("DEVICE_CAPACITY") != null) {
                                deviceModel.setDeviceCapacity(ds.getValue("DEVICE_CAPACITY").toString());
                            }
                            if (ds.getValue("LAST_CONTACT_TIME") != null) {
                                deviceModel.setLastContactTime(ds.getValue("LAST_CONTACT_TIME").toString());
                            }
                            deviceModel.setDeviceName(ds.getValue("DEVICE_NAME").toString());
                            deviceModel.setRemoved(false);
                            final boolean isSupervised = ds.getValue("IS_SUPERVISED") != null && (boolean)ds.getValue("IS_SUPERVISED");
                            deviceModel.setIsSupervised(isSupervised);
                            if (ds.getValue("LOCATED_TIME") != null) {
                                deviceModel.setLocatedTime(ds.getValue("LOCATED_TIME").toString());
                            }
                            final List imeiArray = new ArrayList();
                            if (ds.getValue("PRIMARY_IMEI") != null) {
                                imeiArray.add(ds.getValue("PRIMARY_IMEI"));
                            }
                            if (ds.getValue("SECONDRY_IMEI") != null) {
                                imeiArray.add(ds.getValue("SECONDRY_IMEI"));
                            }
                            if (imeiArray.size() > 0) {
                                deviceModel.setImei(imeiArray);
                            }
                            final UserModel user = new UserModel();
                            user.setUserName(ds.getValue("NAME").toString());
                            user.setUserId((Long)ds.getValue("MANAGED_USER_ID"));
                            deviceModel.setUser(user);
                            if (searchDevice.isDeviceSummary()) {
                                final DeviceSummaryModel deviceSummary = new DeviceSummaryModel();
                                deviceSummary.setProfileCount((Integer)ds.getValue("PROFILE_COUNT"));
                                deviceSummary.setAppCount((Integer)ds.getValue("APP_COUNT"));
                                deviceSummary.setDocCount((Integer)ds.getValue("DOC_COUNT"));
                                deviceSummary.setGroupCount((Integer)ds.getValue("GROUP_COUNT"));
                                deviceModel.setSummary(deviceSummary);
                            }
                            result.add(deviceModel);
                            continue;
                        }
                        case 4:
                        case 7:
                        case 9:
                        case 10:
                        case 11: {
                            deviceModel.setDeviceId((Long)ds.getValue("RESOURCE_ID"));
                            deviceModel.setRemoved(true);
                            result.add(deviceModel);
                            continue;
                        }
                    }
                }
                else {
                    deviceModel.setDeviceId((Long)ds.getValue("RESOURCE_ID"));
                    deviceModel.setRemoved(true);
                    result.add(deviceModel);
                }
            }
        }
        response.setDevices(result);
        return response;
    }
    
    public Criteria setDeviceCriteria(final SearchDevice searchDevice) throws Exception {
        Criteria criteria = SearchUtil.setSearchCriteria(searchDevice);
        if (searchDevice.getPlatform() != null) {
            criteria = this.getMultiPlatformCriteria(searchDevice.getPlatform(), criteria);
        }
        if (searchDevice.getDeviceType() != null) {
            final String[] deviceTypes = searchDevice.getDeviceType().split(",");
            final ArrayList<Integer> values = new ArrayList<Integer>();
            for (int i = 0; i < deviceTypes.length; ++i) {
                final int temp = Integer.parseInt(deviceTypes[i]);
                if (temp == 0 || temp == 1 || temp == 2 || temp == 4 || temp == 3 || temp == 5) {
                    values.add(temp);
                }
            }
            if (values.size() != 0) {
                final Criteria deviceTypeCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)values.toArray(), 8);
                criteria = MDMDBUtil.andCriteria(criteria, deviceTypeCri);
            }
        }
        if (searchDevice.isLost()) {
            final Criteria lostCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8);
            criteria = MDMDBUtil.andCriteria(criteria, lostCriteria);
        }
        if (searchDevice.isExcludeRemoved()) {
            final Criteria excludeCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            criteria = MDMDBUtil.andCriteria(criteria, excludeCri);
        }
        if (searchDevice.getDeltaTokenUtil() != null) {
            final Long lastRequestTime = searchDevice.getDeltaTokenUtil().getRequestTimestamp();
            Criteria deltaCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "LAST_MODIFIED_TIME"), (Object)lastRequestTime, 5);
            deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("ManagedDevice", "ADDED_TIME"), (Object)lastRequestTime, 5));
            deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)lastRequestTime, 5));
            deltaCriteria = deltaCriteria.or(new Criteria(Column.getColumn("USER_RESOURCE", "DB_UPDATED_TIME"), (Object)lastRequestTime, 5));
            criteria = MDMDBUtil.andCriteria(criteria, deltaCriteria);
        }
        if (searchDevice.isDeviceGroupUnassigned()) {
            final Criteria deviceCriteria = new GroupFacade().getDeviceGroupAssignedFilterCriteria(-1L);
            criteria = MDMDBUtil.andCriteria(criteria, deviceCriteria);
        }
        final PagingUtil pagingUtil = searchDevice.getPagingUtil();
        final JSONObject searchByJSON = pagingUtil.getSearchJSON();
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
            criteria = MDMDBUtil.andCriteria(criteria, searchCriteria);
        }
        final Criteria resourceTypeCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new int[] { 121, 120 }, 8);
        criteria = MDMDBUtil.andCriteria(criteria, resourceTypeCriteria);
        return criteria;
    }
    
    public Criteria getMultiPlatformCriteria(final String platform, Criteria criteria) {
        if (platform.length() != 0) {
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
                if (values.size() > 0) {
                    final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)values.toArray(), 8);
                    criteria = MDMDBUtil.andCriteria(criteria, platformCriteria);
                }
            }
            if (p > 0) {
                final Criteria platformCriteria2 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)p, 0);
                criteria = MDMDBUtil.andCriteria(criteria, platformCriteria2);
            }
        }
        return criteria;
    }
    
    public void updateDevice(final DeviceUpdate deviceUpdate) throws Exception {
        DeviceService.logger.log(Level.INFO, "Update device for the id {0}", deviceUpdate.getDeviceId());
        final DeviceExtnModel deviceExtnModel = new DeviceExtnModel();
        deviceExtnModel.setDeviceId(deviceUpdate.getDeviceId());
        deviceExtnModel.setModified(true);
        Boolean isDeviceNameChanged = false;
        if (!MDMStringUtils.isEmpty(deviceUpdate.getDeviceName())) {
            deviceExtnModel.setName(deviceUpdate.getDeviceName());
            isDeviceNameChanged = true;
        }
        deviceExtnModel.setApnUserName(deviceUpdate.getApnUserName());
        final HashMap<String, Object> additionalData = new HashMap<String, Object>();
        additionalData.put("customer_id", deviceUpdate.getCustomerId());
        additionalData.put("user_id", deviceUpdate.getUserId());
        additionalData.put("user_name", deviceUpdate.getUserName());
        additionalData.put("is_device_name_changed", isDeviceNameChanged);
        deviceExtnModel.setAdditionalData(additionalData);
        deviceExtnModel.setDescription(deviceUpdate.getDescription());
        deviceExtnModel.setAssetOwner(deviceUpdate.getAssetOwner());
        deviceExtnModel.setAssetTag(deviceUpdate.getAssetTag());
        deviceExtnModel.setOffice(deviceUpdate.getOffice());
        deviceExtnModel.setBranch(deviceUpdate.getBranch());
        deviceExtnModel.setLocation(deviceUpdate.getLocation());
        deviceExtnModel.setAreaManager(deviceUpdate.getAreaManager());
        deviceExtnModel.setApnPassword(deviceUpdate.getApnPassword());
        deviceExtnModel.setPurchaseDate(deviceUpdate.getPurchaseDate());
        deviceExtnModel.setPurchaseOrderNumber(deviceUpdate.getPurchaseOrderNumber());
        deviceExtnModel.setPurchasePrice(deviceUpdate.getPurchasePrice());
        deviceExtnModel.setPurchaseType(deviceUpdate.getPurchaseType());
        deviceExtnModel.setWarrantyExpirationDate(deviceUpdate.getWarrantyExpirationDate());
        deviceExtnModel.setWarrantyNumber(deviceUpdate.getWarrantyNumber());
        deviceExtnModel.setWarrantyType(deviceUpdate.getWarrantyType());
        deviceExtnModel.setUserId(deviceUpdate.getUserId());
        deviceExtnModel.setLastModifiedTime(MDMUtil.getCurrentTimeInMillis());
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget")) {
            final SelectQuery selectQuery = this.getEnrollmentRequestDetail(deviceUpdate.getDeviceId());
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                boolean canUpdate = false;
                if (deviceUpdate.getRegisteredTime() != null && deviceUpdate.getRegisteredTime() > 0L) {
                    final Long registeredTime = deviceUpdate.getRegisteredTime();
                    final Row row = dataObject.getRow("ManagedDevice");
                    if (row != null) {
                        row.set("REGISTERED_TIME", (Object)registeredTime);
                        dataObject.updateRow(row);
                        canUpdate = true;
                    }
                }
                if (deviceUpdate.getEnrollmentRequestTime() != null && deviceUpdate.getEnrollmentRequestTime() > 0L) {
                    final Long enrollmentReqTime = deviceUpdate.getEnrollmentRequestTime();
                    final Row row = dataObject.getRow("DeviceEnrollmentRequest");
                    if (row != null) {
                        row.set("REQUESTED_TIME", (Object)enrollmentReqTime);
                        dataObject.updateRow(row);
                        canUpdate = true;
                    }
                }
                if (deviceUpdate.getEnrollmentType() != null && deviceUpdate.getEnrollmentType() > 0) {
                    final Integer enrollmentType = deviceUpdate.getEnrollmentType();
                    final Row row = dataObject.getRow("EnrollmentTemplate");
                    row.set("TEMPLATE_TYPE", (Object)enrollmentType);
                    dataObject.updateRow(row);
                    canUpdate = true;
                }
                if (canUpdate) {
                    MDMUtil.getPersistence().update(dataObject);
                }
            }
        }
        final ObjectMapper objectMapper = new ObjectMapper();
        final JSONObject modifiedJSONObject = new JSONObject(objectMapper.writeValueAsString((Object)deviceExtnModel));
        MDCustomDetailsRequestHandler.getInstance().updateCustomDeviceDetails(modifiedJSONObject);
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
    
    public void deleteDevice(final Long deviceId, final BaseAPIModel baseAPIModel) throws Exception {
        final Long erid = MDMEnrollmentUtil.getInstance().getEnrollRequestIDFromManagedDeviceID(deviceId);
        MDMEnrollmentUtil.getInstance().removeDevice(String.valueOf(erid), baseAPIModel.getUserName(), baseAPIModel.getCustomerId());
    }
    
    public SelectQuery getDeviceSummaryQuery(SelectQuery query) {
        final Table managedDeviceTable = Table.getTable("ManagedDevice");
        if (query == null) {
            query = (SelectQuery)new SelectQueryImpl(managedDeviceTable);
        }
        query.addJoin(new Join("ManagedDevice", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "PROFILE_COUNT"));
        query.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "APP_COUNT"));
        query.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "DOC_COUNT"));
        SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        subSQ.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        final Column column_config_data = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID").count();
        column_config_data.setColumnAlias("GROUP_RESOURCE_ID");
        final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        subSQ.addJoin(customGroupJoin);
        subSQ.setCriteria(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getAllMDMGroupTypes(), 8));
        subSQ = RBDAUtil.getInstance().getRBDAQuery(subSQ);
        subSQ.addSelectColumn(column_config_data);
        final List list = new ArrayList();
        final Column groupByCol = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
        list.add(groupByCol);
        final GroupByClause memberGroupBy = new GroupByClause(list);
        subSQ.setGroupByClause(memberGroupBy);
        final DerivedTable groupDerievedTab = new DerivedTable("CustomGroupMemberRel", (Query)subSQ);
        query.addJoin(new Join(managedDeviceTable, (Table)groupDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
        final Column maxCountCol = new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
        maxCountCol.setColumnAlias("GROUP_COUNT");
        query.addSelectColumn(maxCountCol);
        return query;
    }
    
    public DeviceDetailsModel getDevice(final SearchDevice searchDevice) throws Exception {
        final Long deviceId = searchDevice.getDeviceId();
        final DeviceDetailsModel device = this.getDeviceHardwareDetails(deviceId);
        if (device.getAgentType() == 3) {
            device.setNetworkUsage(this.getSafeNetworkUsageDetails(deviceId));
            final boolean isKnoxEnabled = KnoxUtil.getInstance().isRegisteredAsKnox(deviceId);
            device.setKnoxEnabled(isKnoxEnabled);
            if (isKnoxEnabled) {
                device.setKnoxDetails(this.getKnoxDeviceDetails(deviceId));
            }
        }
        device.setNetwork(this.getDeviceNetworkDetails(deviceId));
        device.setSims(this.getDeviceSimDetails(deviceId));
        device.setOs(this.getOsDeviceDetails(deviceId));
        device.setSecurity(this.getDeviceSecurityDetails(deviceId));
        device.setUser(this.getDeviceUserDetails(deviceId));
        device.setSharedDeviceInfo(this.getSharedDeviceDetails(deviceId));
        if (device.isProfileowner() && device.getAgentType() == 2) {
            device.setWorkDataSecurity(this.getDeviceWorkDataSecurityModel(deviceId));
        }
        final PrivacySettingModel privacySettingModel = PojoUtil.getInstance().getModelFromJsonObject(new PrivacySettingsHandler().getPrivacySettingsJSON(deviceId), PrivacySettingModel.class, null, false);
        device.setPrivacySetting(privacySettingModel);
        final LocationSettingModel locationSettingModel = PojoUtil.getInstance().getModelFromJsonObject(LocationSettingsDataHandler.getInstance().getLocationSettingsForDevice(deviceId), LocationSettingModel.class, null, false);
        device.setLocationSettings(locationSettingModel);
        device.setiOSNativeAppRegistered(IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(device.getResourceId()));
        device.setOwnedBy(MDMEnrollmentUtil.getInstance().getOwnedBy(deviceId));
        device.setNotificationServiceType(MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(device.getPlatformType(), searchDevice.getCustomerId()));
        final LostModeDataHandler lostModeDataHandler = new LostModeDataHandler();
        device.setLostModeEnabled(lostModeDataHandler.isLostMode(deviceId));
        device.setLostModeStatus(lostModeDataHandler.getLostModeStatus(deviceId));
        device.setManagedStatus(ManagedDeviceHandler.getInstance().getManagedDeviceStatus(deviceId));
        device.setLastScanTime(MDMUtil.getInstance().getLastScanTime(deviceId));
        device.setRemoteSettingsEnabled(MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(searchDevice.getCustomerId()));
        device.setMailServerEnabled(ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured());
        device.setLastContactTime(MDMUtil.getInstance().getLastContactTime(deviceId));
        if (searchDevice.isDeviceSummary()) {
            SelectQuery selectQuery = null;
            selectQuery = this.getDeviceSummaryQuery(selectQuery);
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
            selectQuery.setCriteria(criteria);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            final DeviceSummaryModel deviceSummary = new DeviceSummaryModel();
            if (ds.next()) {
                deviceSummary.setProfileCount((Integer)ds.getValue("PROFILE_COUNT"));
                deviceSummary.setAppCount((Integer)ds.getValue("APP_COUNT"));
                deviceSummary.setDocCount((Integer)ds.getValue("DOC_COUNT"));
                deviceSummary.setGroupCount((Integer)ds.getValue("GROUP_COUNT"));
                device.setSummary(deviceSummary);
            }
        }
        if (searchDevice.getInclude() != null && searchDevice.getInclude().equals("groupName")) {
            device.setGroups(new GroupFacade().getAssociatedGroupNamesWithResId(deviceId, searchDevice.getCustomerId()));
        }
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
            final SelectQuery selectQuery = this.getEnrollmentRequestDetail(deviceId);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row deviceEnrollmentrow = dataObject.getRow("DeviceEnrollmentRequest");
                if (deviceEnrollmentrow != null) {
                    device.setEnrollmentRequestTime((Long)deviceEnrollmentrow.get("REQUESTED_TIME"));
                }
                final Row enrollemntTempleteRow = dataObject.getRow("EnrollmentTemplate");
                if (enrollemntTempleteRow != null) {
                    device.setEnrollmentType((int)enrollemntTempleteRow.get("TEMPLATE_TYPE"));
                }
            }
        }
        if (device.getPlatformType() == 1 && (device.getModelType() == 3 || device.getModelType() == 4)) {
            final DeviceFilevaultDetailsModel fvDetails = this.getDeviceEncryptionSecurityDetails(deviceId);
            if (fvDetails.isFilevaultPersonalEnabled()) {
                device.setHasFilevaultRecoveryKey(fvDetails.isFilevaultPersonalRecoveryKeyAvailable());
            }
        }
        if (device.getPlatformType() == 1) {
            final IOSAccessibilitySettingsModel settingsModel = this.getIOSAccessibilitySettingsDetails(deviceId);
            if (settingsModel != null) {
                device.setiOSAccessibilitySettingsInfo(settingsModel);
            }
        }
        return device;
    }
    
    public IOSAccessibilitySettingsModel getIOSAccessibilitySettingsDetails(final Long resourceId) {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("MdIosAccessibilitySettingsInfo"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdIosAccessibilitySettingsInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        sq.setCriteria(resourceCriteria);
        sq.addSelectColumn(new Column((String)null, "*"));
        IOSAccessibilitySettingsModel settingModel = null;
        try {
            final DataObject DO = MDMUtil.getPersistence().get(sq);
            if (DO.getRow("MdIosAccessibilitySettingsInfo") != null) {
                settingModel = PojoUtil.getInstance().getModelfromDO(DO, IOSAccessibilitySettingsModel.class, null);
            }
        }
        catch (final Exception e) {
            DeviceService.logger.log(Level.SEVERE, "Exception in DeviceService - getIOSAccessibilitySettingsDetails");
        }
        return settingModel;
    }
    
    public DeviceLocationListModel getDeviceLocations(final DeviceLocation deviceLocation) throws Exception {
        final Long customerId = deviceLocation.getCustomerId();
        final Long deviceId = deviceLocation.getDeviceId();
        final Boolean lastKnownLocation = deviceLocation.isLastKnown();
        Boolean lastLocationOnly = lastKnownLocation || (!LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition() && LicenseProvider.getInstance().getEvaluationDays() <= 1L);
        final Integer noOfDays = deviceLocation.getNoOfDays();
        final int locationTrackingStatus = LocationSettingsDataHandler.getInstance().getLocationTrackingStatus(customerId);
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
        final List<DeviceLocationModel> locationList = new ArrayList<DeviceLocationModel>();
        final Calendar cal = Calendar.getInstance();
        cal.add(6, -1 * noOfDays);
        final long nDaysAgo = cal.getTimeInMillis();
        while (keys.hasNext()) {
            final String key = keys.next();
            final JSONObject location = locations.getJSONObject(key);
            if (!lastLocationOnly && noOfDays != -1 && location.getLong("ADDED_TIME") < nDaysAgo) {
                continue;
            }
            final DeviceLocationModel deviceLocationModel = PojoUtil.getInstance().getModelFromJsonObject(location, DeviceLocationModel.class, null, true);
            locationList.add(deviceLocationModel);
        }
        final DeviceLocationListModel deviceLocationList = new DeviceLocationListModel();
        final HashMap locationErrorMap = MDMGeoLocationHandler.getInstance().getLocationErrorMap(deviceId);
        final LocationErrorModel locationError = PojoUtil.getInstance().getModelFromJsonObject(new JSONObject((Map)locationErrorMap), LocationErrorModel.class, null, true);
        deviceLocationList.setErrorMap(locationError);
        deviceLocationList.setLocations(locationList);
        return deviceLocationList;
    }
    
    public OsModel getOsDeviceDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getOSInfo(deviceId);
        final OsModel osModel = PojoUtil.getInstance().getModelfromDO(dataObject, OsModel.class, null);
        DeviceService.logger.log(Level.FINE, "InventoryUtil : Data Hash for OS info : {0}", osModel);
        return osModel;
    }
    
    public SecurityModel getDeviceSecurityDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getSecurityInfo(deviceId);
        SecurityModel securityDetails = PojoUtil.getInstance().getModelfromDO(dataObject, SecurityModel.class, null);
        final JSONArray efrpDetails = InventoryUtil.getInstance().getEFRPInfo(deviceId);
        final List<EFRPAccountDetailModel> efrpAccountDetailModels = PojoUtil.getInstance().getModelListFromJsonArray(efrpDetails, EFRPAccountDetailModel[].class);
        securityDetails.setEfrpAccountDetails(efrpAccountDetailModels);
        final JSONObject safetyNetDetails = new JSONObject();
        InventoryUtil.getInstance().getSafetyNetDetails(deviceId, safetyNetDetails);
        securityDetails = PojoUtil.getInstance().getModelFromJsonObject(safetyNetDetails, SecurityModel.class, securityDetails, true);
        DeviceService.logger.log(Level.FINE, "InventoryUtil : Data Hash for OS info : {0}", securityDetails);
        return securityDetails;
    }
    
    public UserModel getDeviceUserDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getDeviceUserInfo(deviceId);
        final UserModel userdetails = PojoUtil.getInstance().getModelfromDO(dataObject, UserModel.class, null);
        return userdetails;
    }
    
    public SharedDeviceDetailModel getSharedDeviceDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getSharedDeviceInfo(deviceId);
        final SharedDeviceDetailModel sharedDeviceDetails = PojoUtil.getInstance().getModelfromDO(dataObject, SharedDeviceDetailModel.class, null);
        return sharedDeviceDetails;
    }
    
    public NetworkModel getDeviceNetworkDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getNetworkDetailedInfo(deviceId);
        final NetworkModel networkDetailsModel = PojoUtil.getInstance().getModelfromDO(dataObject, NetworkModel.class, null);
        final String wifiRemarks = MdDeviceWifiSSIDDBHandler.getInstance().getDeviceErrorRemarks(deviceId);
        if (!MDMStringUtils.isEmpty(wifiRemarks)) {
            networkDetailsModel.setWifiSsidRemarks(I18N.getMsg(wifiRemarks, new Object[0]));
        }
        return networkDetailsModel;
    }
    
    public List<SimModel> getDeviceSimDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getSimInfo(deviceId);
        final Iterator<Row> simRows = dataObject.getRows("MdSIMInfo");
        return PojoUtil.getInstance().getModelListFromRows(simRows, SimModel.class);
    }
    
    public DeviceDetailsModel getDeviceHardwareDetails(final Long deviceId) throws Exception {
        final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)deviceId, "AGENT_TYPE");
        DeviceDetailsModel deviceDetailsModel = new DeviceDetailsModel();
        DataObject dataObject = null;
        switch (agentType) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 8: {
                dataObject = InventoryUtil.getInstance().getDeviceDetailedInfo(deviceId);
                final Long deviceTime = MDMBatterySettingsDBHandler.getBatteryLastUpdatedTime(deviceId);
                deviceDetailsModel.setDeviceLastSyncTime(String.valueOf(deviceTime));
                break;
            }
            case 3: {
                dataObject = InventoryUtil.getInstance().getSafeDeviceDetailedInfo(deviceId);
                break;
            }
        }
        deviceDetailsModel = PojoUtil.getInstance().getModelfromDO(dataObject, DeviceDetailsModel.class, null);
        return deviceDetailsModel;
    }
    
    public KnoxDetailsModel getKnoxDeviceDetails(final Long deviceId) throws Exception {
        final JSONObject knoxObject = KnoxUtil.getInstance().getDeviceKnoxDetailsJSON(deviceId);
        return PojoUtil.getInstance().getModelFromJsonObject(knoxObject, KnoxDetailsModel.class, null, true);
    }
    
    public NetworkUsageModel getSafeNetworkUsageDetails(final Long deviceId) throws Exception {
        final DataObject dataObject = InventoryUtil.getInstance().getSafeNetworkUsageDetailedInfo(deviceId);
        return PojoUtil.getInstance().getModelfromDO(dataObject, NetworkUsageModel.class, null);
    }
    
    public Map getDeviceNetworkRelatedDetails(final Long deviceId) throws Exception {
        final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)deviceId, "AGENT_TYPE");
        final Map<String, Object> response = new HashMap<String, Object>();
        if (agentType == 3) {
            response.put("network_usage", this.getSafeNetworkUsageDetails(deviceId));
        }
        response.put("network", this.getDeviceNetworkDetails(deviceId));
        return response;
    }
    
    public void setDeviceUnlockPinDetails(final DeviceUnlockSettingsModel deviceUnlockSettingsModel) throws Exception {
        DeviceUnlockPinHandler.getInstance().setDeviceUnlockPIN(deviceUnlockSettingsModel, 0);
    }
    
    public Map getDeviceSimDetailsMap(final Long deviceId) throws Exception {
        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("sims", this.getDeviceSimDetails(deviceId));
        return response;
    }
    
    public DeviceCertificateDetailsModel getDeviceCertificateDetails(final Long deviceId, Long expiry) throws Exception {
        if (expiry != null && expiry <= 0L) {
            expiry = null;
        }
        final DeviceCertificateDetailsModel response = new DeviceCertificateDetailsModel();
        final List<CertifcateDetailsModel> managedCertificates = PojoUtil.getInstance().getModelListFromJsonArray(new DeviceInstalledCertificateDataHandler().getManagedCertificateDetails(deviceId, expiry), CertifcateDetailsModel[].class);
        final List<CertifcateDetailsModel> unManagedCertificates = PojoUtil.getInstance().getModelListFromJsonArray(new DeviceInstalledCertificateDataHandler().getUnmanagedCertificateDetails(deviceId, expiry), CertifcateDetailsModel[].class);
        response.setManagedCertificates(managedCertificates);
        response.setUnManagedCertificates(unManagedCertificates);
        return response;
    }
    
    public Map getDeviceAppDetails(final SearchDevice searchDevice) throws Exception {
        final JSONObject appViewSettings = AppSettingsDataHandler.getInstance().getAppViewSettings(searchDevice.getCustomerId());
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
        query.addJoin(new Join("MdAppDetails", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.setCriteria(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)searchDevice.getDeviceId(), 0));
        final Criteria appScanCriteria = null;
        if (appViewSettings.getBoolean("SHOW_USER_INSTALLED_APPS")) {
            MDMDBUtil.andCriteria(appScanCriteria, new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0));
        }
        if (appViewSettings.getBoolean("SHOW_SYSTEM_APPS")) {
            MDMDBUtil.orCriteria(appScanCriteria, new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0));
        }
        if (appViewSettings.getBoolean("SHOW_MANAGED_APPS")) {
            MDMDBUtil.orCriteria(appScanCriteria, new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)3, 0));
        }
        query.setCriteria(query.getCriteria().and(appScanCriteria));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        final Iterator<Row> appDetailsRow = dataObject.getRows("MdAppDetails");
        final List<DeviceInstalledAppModel> installedApps = PojoUtil.getInstance().getModelListFromRows(appDetailsRow, DeviceInstalledAppModel.class);
        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("installed_apps", installedApps);
        query = new AppFacade().getAppDeviceQuery(Arrays.asList(searchDevice.getDeviceId()), searchDevice.getCustomerId());
        query.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        query.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        query.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
        query.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        query.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        query.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
        query.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        query.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        if ("details".equalsIgnoreCase(searchDevice.getInclude())) {
            query.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            query.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
            query.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
            query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ADDED_TIME"));
            query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_MODIFIED_TIME"));
        }
        query.setCriteria(query.getCriteria().and(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)searchDevice.getCustomerId(), 0)));
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)query);
        final List<AppDetailsModel> appDetailsList = new ArrayList<AppDetailsModel>();
        while (ds.next()) {
            final AppDetailsModel appDetails = new AppDetailsModel();
            appDetails.setAppId((Long)ds.getValue("PACKAGE_ID"));
            appDetails.setAppVersion((String)ds.getValue("APP_VERSION"));
            appDetails.setAppVersionCode((String)ds.getValue("APP_NAME_SHORT_VERSION"));
            appDetails.setPlatformType(Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString()));
            appDetails.setAppName((String)ds.getValue("APP_NAME"));
            appDetails.setIdentifier((String)ds.getValue("IDENTIFIER"));
            appDetails.setStatus((Integer)ds.getValue("STATUS"));
            appDetails.setRemarks(MDMI18N.getMsg(String.valueOf(ds.getValue("REMARKS")), true));
            appDetails.setLocalizedRemark(MDMI18N.getMsg(String.valueOf(ds.getValue("REMARKS_EN")), true));
            final ReleaseLabelDetailModel releaseLabelDetails = new ReleaseLabelDetailModel();
            releaseLabelDetails.setReleaseLabelName(MDMI18N.getMsg(String.valueOf(ds.getValue("RELEASE_LABEL_DISPLAY_NAME")), Boolean.TRUE));
            releaseLabelDetails.setReleaseLabelId(Long.valueOf(ds.getValue("RELEASE_LABEL_ID").toString()));
            appDetails.setReleaseLabelDetails(releaseLabelDetails);
            if (searchDevice.getInclude() != null && searchDevice.getInclude().equalsIgnoreCase("details")) {
                appDetails.setPackageType((Integer)ds.getValue("PACKAGE_TYPE"));
                appDetails.setAddedTime((Long)ds.getValue("PACKAGE_ADDED_TIME"));
                appDetails.setModifiedTime((Long)ds.getValue("PACKAGE_MODIFIED_TIME"));
                if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                    final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                    if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                        if (!displayImageLoc.startsWith("http")) {
                            appDetails.setIcon(MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                        }
                        else {
                            appDetails.setIcon(displayImageLoc);
                        }
                    }
                }
            }
            appDetailsList.add(appDetails);
        }
        response.put("apps", appDetailsList);
        return response;
    }
    
    public DeviceFilevaultDetailsModel getDeviceEncryptionSecurityDetails(final Long resourceID) {
        final DeviceFilevaultDetailsModel model = new DeviceFilevaultDetailsModel();
        model.setResourceID(resourceID);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMDeviceFileVaultInfo"));
        query.addJoin(new Join("MDMDeviceFileVaultInfo", "DeviceToEncrytptionSettingsRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("DeviceToEncrytptionSettingsRel", "MDMEncryptionSettings", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        query.addJoin(new Join("MDMEncryptionSettings", "MDMFileVaultSettings", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        query.addJoin(new Join("MDMFileVaultSettings", "MDMFileVaultPersonalKeyConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        query.addJoin(new Join("MDMFileVaultSettings", "MDMFileVaultInstitutionConfiguration", new String[] { "ENCRYPTION_SETTINGS_ID" }, new String[] { "ENCRYPTION_SETTINGS_ID" }, 1));
        query.addSelectColumn(new Column((String)null, "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MDMDeviceFileVaultInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        try {
            final DataObject doB = MDMUtil.getPersistence().get(query);
            if (doB.containsTable("MDMFileVaultPersonalKeyConfiguration")) {
                model.setFilevaultManaged(true);
                model.setFilevaultPersonalManaged(true);
            }
            if (doB.containsTable("MDMFileVaultInstitutionConfiguration")) {
                model.setFilevaultManaged(true);
                model.setFilevaultInstitutionalManaged(true);
            }
            if (doB.containsTable("MDMDeviceFileVaultInfo")) {
                final Row fvDetailsRow = doB.getRow("MDMDeviceFileVaultInfo");
                model.setFilevaultEnabled((boolean)fvDetailsRow.get("IS_ENCRYPTION_ENABLED"));
                final Long prkUpdateTime = (Long)fvDetailsRow.get("RECOVERY_KEY_CHANGED_TIME");
                if (prkUpdateTime != null) {
                    model.setPersonalRecoveryKeyUpdatedTime(prkUpdateTime);
                    model.setPersonalRecoveryKeyUpdatedTimeString(MDMUtil.getDate(prkUpdateTime, true));
                }
                model.setFilevaultPersonalEnabled((boolean)fvDetailsRow.get("IS_PERSONAL_RECOVERY_KEY"));
                model.setFilevaultInstitutionalEnabled((boolean)fvDetailsRow.get("IS_INSTITUTION_RECOVERY_KEY"));
                model.setFilevaultPersonalRecoveryKeyAvailable(!MDMStringUtils.isEmpty((String)fvDetailsRow.get("PERSONAL_RECOVERY_KEY")));
                model.setFilevaultPreviousPersonalRecoveryKeyAvailable(!MDMStringUtils.isEmpty((String)fvDetailsRow.get("PREVIOUS_PERSONAL_RECOVERY_KEY")));
                if (model.isFilevaultInstitutionalEnabled() && model.isFilevaultInstitutionalManaged()) {
                    model.setFilevaultInstitutionalRecoveryKeyAvailable(true);
                }
            }
        }
        catch (final DataAccessException e) {
            DeviceService.logger.log(Level.FINE, "InventoryUtil : DataAccessException", (Throwable)e);
        }
        return model;
    }
    
    public WorkDataSecurityModel getDeviceWorkDataSecurityModel(final Long deviceId) throws Exception {
        final DataObject workDataSecurityDo = InventoryUtil.getInstance().getWorkDataSecurityInfoDo(deviceId);
        final WorkDataSecurityModel workDataSecurityModel = PojoUtil.getInstance().getModelfromDO(workDataSecurityDo, WorkDataSecurityModel.class, null);
        return workDataSecurityModel;
    }
    
    public DeviceProfilesModel getDeviceProfiles(final SearchDevice searchDevice) throws Exception {
        final PagingUtil pagingUtil = searchDevice.getPagingUtil();
        final DeviceProfilesModel deviceProfilesModel = new DeviceProfilesModel();
        final List<ProfileModel> profileList = new ArrayList<ProfileModel>();
        SelectQuery selectQuery = new DeviceFacade().getDeviceProfilesBaseQuery(searchDevice.getDeviceId());
        new DeviceFacade().addSelectColumnsForDeviceProfiles(selectQuery);
        final SelectQuery countQuery = new DeviceFacade().getDeviceProfilesBaseQuery(searchDevice.getDeviceId());
        new DeviceFacade().addSelectColumnsForDeviceProfilesCount(countQuery);
        final int count = DBUtil.getRecordCount(countQuery);
        final MetaDataModel metaDataModel = new MetaDataModel();
        metaDataModel.setTotalCount(count);
        deviceProfilesModel.setMetadata(metaDataModel);
        final Boolean distributionSummary = searchDevice.isDeviceSummary();
        if (distributionSummary) {
            selectQuery = new ProfileFacade().getProfileAssociationGroupCount(selectQuery);
            selectQuery = new ProfileFacade().getProfileAssociationDeviceCount(selectQuery);
        }
        if (count != 0) {
            final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
            if (pagingJSON != null) {
                deviceProfilesModel.setPaging(pagingJSON);
            }
        }
        selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
        selectQuery.addSortColumn(new SortColumn("Profile", "PROFILE_ID", true));
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final ProfileModel profileDetailsModel = new ProfileModel();
            profileDetailsModel.setProfileId((Long)ds.getValue("PROFILE_ID".toLowerCase()));
            profileDetailsModel.setProfileName((String)ds.getValue("PROFILE_NAME".toLowerCase()));
            profileDetailsModel.setPlatformType((Integer)ds.getValue("PLATFORM_TYPE".toLowerCase()));
            profileDetailsModel.setProfileDescription((String)ds.getValue("PROFILE_DESCRIPTION".toLowerCase()));
            profileDetailsModel.setLastModifiedTime((Long)ds.getValue("LAST_MODIFIED_TIME".toLowerCase()));
            profileDetailsModel.setLastModifiedBy((Long)ds.getValue("LAST_MODIFIED_BY".toLowerCase()));
            profileDetailsModel.setCreatedBy((Long)ds.getValue("CREATED_BY".toLowerCase()));
            profileDetailsModel.setCreationTime((Long)ds.getValue("CREATION_TIME".toLowerCase()));
            profileDetailsModel.setAppliedTime((Long)ds.getValue("APPLIED_TIME".toLowerCase()));
            profileDetailsModel.setStatus((Integer)ds.getValue("STATUS".toLowerCase()));
            profileDetailsModel.setLocalizedRemarks(I18N.getMsg(ds.getValue("localized_remarks").toString(), new Object[0]));
            profileDetailsModel.setRemarks(I18N.getMsg(ds.getValue("REMARKS".toLowerCase()).toString(), new Object[0]));
            profileDetailsModel.setAssociatedByUserId((Long)ds.getValue("associated_by_user_id"));
            profileDetailsModel.setAssociatedByUserName((String)ds.getValue("associated_by_user_name"));
            profileDetailsModel.setExecutedVersion((Integer)ds.getValue("executed_version"));
            profileDetailsModel.setLatestVersion((Integer)ds.getValue("latest_version"));
            profileDetailsModel.setLastModifiedByUser((String)ds.getValue("last_modified_by_user"));
            profileDetailsModel.setCreatedByUser((String)ds.getValue("created_by_user"));
            profileDetailsModel.setDeviceCount((Integer)ds.getValue("device_count"));
            profileDetailsModel.setGroupCount((Integer)ds.getValue("group_count"));
            profileList.add(profileDetailsModel);
        }
        deviceProfilesModel.setProfiles(profileList);
        return deviceProfilesModel;
    }
    
    public LostDeviceCountModel getLostDeviceCount(final long customerId) throws Exception {
        try {
            final int lostDeviceCount = new LostModeDataHandler().getLostModeDeviceCount(customerId);
            final LostDeviceCountModel response = new LostDeviceCountModel();
            response.setLostDeviceCount(lostDeviceCount);
            return response;
        }
        catch (final Exception e) {
            DeviceService.logger.log(Level.SEVERE, "error in getting lost device count", e);
            throw e;
        }
    }
    
    public BootstrapTokenInfoModel getIndividualMacBootstrapTokenInfo(final Long resourceId) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "getIndividualMacBootstrapTokenInfo():- for resourceId={0}", resourceId);
        try {
            final BootstrapTokenInfoModel bootstrapTokenInfoModel = new BootstrapTokenInfoModel();
            final DataObject dataObject = MacBootstrapTokenHandler.getInstance().getMacBootstrapTokenDataObject(resourceId);
            if (!dataObject.isEmpty()) {
                final Row bootstrapTokenRow = dataObject.getFirstRow("MacBootstrapToken");
                bootstrapTokenInfoModel.setAllowedForAuth((Integer)bootstrapTokenRow.get("ALLOWED_FOR_AUTH"));
                bootstrapTokenInfoModel.setBootstraptokenAllowed((Integer)bootstrapTokenRow.get("BOOTSTRAPTOKEN_ALLOWED"));
                bootstrapTokenInfoModel.setReqForKernalExtApprove((Integer)bootstrapTokenRow.get("REQ_FOR_KERNAL_EXT_APPROVE"));
                bootstrapTokenInfoModel.setReqForSoftwareUpdate((Integer)bootstrapTokenRow.get("REQ_FOR_SOFTWARE_UPDATE"));
                bootstrapTokenInfoModel.setIsBootstrapTokenAvailable((String)bootstrapTokenRow.get("TOKEN"));
            }
            return bootstrapTokenInfoModel;
        }
        catch (final Exception e) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "getIndividualMacBootstrapTokenInfo():- Exception is ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public DeviceListModel getDevicesForGroupTree(final SearchDevice searchDevice) throws Exception {
        final Criteria criteria = this.setDeviceCriteria(searchDevice);
        final PagingUtil pagingUtil = searchDevice.getPagingUtil();
        final SelectQuery query = new DeviceFacade().getDevicesTreeBaseQuery();
        final SelectQuery cQuery = new DeviceFacade().getDevicesTreeBaseQuery();
        new DeviceFacade().addSelectColForDevicesTreeQuery(query);
        new DeviceFacade().addSelectColForDevicesCountQuery(cQuery);
        new DeviceFacade().addCustomerFilter(query, searchDevice.getUserId(), searchDevice.getCustomerId());
        new DeviceFacade().addCustomerFilter(cQuery, searchDevice.getUserId(), searchDevice.getCustomerId());
        query.setCriteria(MDMDBUtil.andCriteria(query.getCriteria(), criteria));
        cQuery.setCriteria(MDMDBUtil.andCriteria(cQuery.getCriteria(), criteria));
        final int count = DBUtil.getRecordCount(cQuery);
        final DeviceListModel response = new DeviceListModel();
        final List<DeviceModel> result = new ArrayList<DeviceModel>();
        final MetaDataModel meta = new MetaDataModel();
        meta.setTotalCount(count);
        response.setMetadata(meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(searchDevice.getRequestUri());
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            response.setDeltaToken(newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            if (!searchDevice.isSelectAll()) {
                final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
                if (pagingJSON != null) {
                    response.setPaging(pagingJSON);
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
            else {
                query.addSortColumn(new SortColumn("ManagedDevice", "RESOURCE_ID", true));
            }
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final DeviceModel deviceModel = new DeviceModel();
                deviceModel.setDeviceId((Long)ds.getValue("RESOURCE_ID"));
                deviceModel.setCustomerId((Long)ds.getValue("CUSTOMER_ID"));
                deviceModel.setPlatformTypeId(Integer.valueOf(ds.getValue("PLATFORM_TYPE").toString()));
                deviceModel.setDeviceType(ds.getValue("MODEL_TYPE").toString());
                deviceModel.setDeviceName(ds.getValue("DEVICE_NAME").toString());
                final UserModel user = new UserModel();
                user.setUserName(ds.getValue("NAME").toString());
                user.setUserId((Long)ds.getValue("MANAGED_USER_ID"));
                deviceModel.setUser(user);
                result.add(deviceModel);
            }
        }
        response.setDevices(result);
        return response;
    }
    
    static {
        DeviceService.logger = Logger.getLogger("MDMApiLogger");
    }
}
