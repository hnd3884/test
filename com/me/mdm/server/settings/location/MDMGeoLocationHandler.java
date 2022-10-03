package com.me.mdm.server.settings.location;

import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONException;
import java.util.Collection;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.Iterator;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.common.ErrorCodeHandler;
import com.adventnet.i18n.I18N;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.me.mdm.server.location.LocationDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMGeoLocationHandler
{
    private static MDMGeoLocationHandler geoLocation;
    public Logger logger;
    public Logger locationLogger;
    
    public MDMGeoLocationHandler() {
        this.logger = Logger.getLogger("MDMGeoLocationHandler");
        this.locationLogger = Logger.getLogger("MDMLocationLogger");
    }
    
    public static MDMGeoLocationHandler getInstance() {
        if (MDMGeoLocationHandler.geoLocation == null) {
            MDMGeoLocationHandler.geoLocation = new MDMGeoLocationHandler();
        }
        return MDMGeoLocationHandler.geoLocation;
    }
    
    public void addOrUpdateDeviceLocationDetails(final JSONObject joLocation, final String udid) {
        final Long deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        new LocationDataHandler().deviceLocationUpdates(deviceId, joLocation);
    }
    
    public HashMap getRecentDeviceLocationDetails(final Long deviceId) {
        HashMap geoLocMap = null;
        try {
            final Criteria resCrit = new Criteria(Column.getColumn("DeviceRecentLocation", "RESOURCE_ID"), (Object)deviceId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            final Join recLocationJoin = new Join("MdDeviceLocationDetails", "DeviceRecentLocation", new String[] { "LOCATION_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 2);
            selectQuery.addJoin(recLocationJoin);
            selectQuery.setCriteria(resCrit);
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            final DataObject locDO = MDMUtil.getPersistence().get(selectQuery);
            if (!locDO.isEmpty()) {
                geoLocMap = new HashMap();
                final Row locRow = locDO.getFirstRow("MdDeviceLocationDetails");
                geoLocMap.put("LATITUDE", locRow.get("LATITUDE"));
                geoLocMap.put("LONGITUDE", locRow.get("LONGITUDE"));
                final Long updatedTime = (Long)locRow.get("LOCATED_TIME");
                final String updatedTimeStr = Utils.getEventTime(updatedTime);
                geoLocMap.put("ADDED_TIME", updatedTimeStr);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getDeviceLocationDetails", ex);
        }
        return geoLocMap;
    }
    
    public void addorUpdateDeviceLocationErrorCode(final Long resourceId, final int errorCode) {
        final Criteria resourceCri = new Criteria(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"), (Object)resourceId, 0);
        try {
            final DataObject dObj = MDMUtil.getPersistence().get("MdDeviceLocationToErrCode", resourceCri);
            if (dObj.isEmpty()) {
                final Row errorRow = new Row("MdDeviceLocationToErrCode");
                errorRow.set("RESOURCE_ID", (Object)resourceId);
                errorRow.set("ERROR_CODE", (Object)errorCode);
                dObj.addRow(errorRow);
                MDMUtil.getPersistence().add(dObj);
            }
            else {
                final Row errorRow = dObj.getFirstRow("MdDeviceLocationToErrCode");
                errorRow.set("ERROR_CODE", (Object)errorCode);
                dObj.updateRow(errorRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in updateLocationErrorCode", ex);
        }
    }
    
    public void deleteDeviceLocationErrorCode(final Long resourceId) {
        this.logger.log(Level.INFO, "deleteDeviceScanToErrCode(): resourceID:{0}", resourceId);
        try {
            final Criteria resourceCri = new Criteria(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"), (Object)resourceId, 0);
            MDMUtil.getPersistence().delete(resourceCri);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in deleteDeviceLocationErrorCode", ex);
        }
    }
    
    public void deleteDeviceLocationErrorCode(final List<Long> resourceList) {
        this.logger.log(Level.INFO, "deleteDeviceScanToErrCode(): resourceID:{0}", resourceList);
        try {
            final Criteria resourceCri = new Criteria(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            MDMUtil.getPersistence().delete(resourceCri);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in deleteDeviceLocationErrorCode", ex);
        }
    }
    
    public HashMap getLocationErrorMap(final Long resourceId) {
        HashMap locationErrorMap = null;
        try {
            final SelectQuery locationErrorQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationToErrCode"));
            final Join errorCodeJoin = new Join("MdDeviceLocationToErrCode", "ErrorCode", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 2);
            final Join errorCodekbJoin = new Join("ErrorCode", "ErrorCodeToKBUrl", new String[] { "ERROR_CODE" }, new String[] { "ERROR_CODE" }, 2);
            locationErrorQuery.addJoin(errorCodeJoin);
            locationErrorQuery.addJoin(errorCodekbJoin);
            locationErrorQuery.addSelectColumn(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"));
            locationErrorQuery.addSelectColumn(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"));
            locationErrorQuery.addSelectColumn(Column.getColumn("ErrorCodeToKBUrl", "ERROR_CODE"));
            locationErrorQuery.addSelectColumn(Column.getColumn("ErrorCodeToKBUrl", "KB_URL"));
            locationErrorQuery.addSelectColumn(Column.getColumn("ErrorCode", "ERROR_CODE"));
            locationErrorQuery.addSelectColumn(Column.getColumn("ErrorCode", "SHORT_DESC"));
            locationErrorQuery.addSelectColumn(Column.getColumn("ErrorCode", "DETAILED_DESC"));
            final Criteria errorCodeCri = new Criteria(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"), (Object)resourceId, 0);
            locationErrorQuery.setCriteria(errorCodeCri);
            final DataObject errorCodeDObj = MDMUtil.getPersistence().get(locationErrorQuery);
            if (!errorCodeDObj.isEmpty()) {
                locationErrorMap = new HashMap();
                final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
                final Row locationRow = errorCodeDObj.getFirstRow("ErrorCode");
                locationErrorMap.put("ERROR_CODE", locationRow.get("ERROR_CODE"));
                locationErrorMap.put("SHORT_DESC", I18N.getMsg((String)locationRow.get("SHORT_DESC"), new Object[] { deviceName }));
                locationErrorMap.put("DETAILED_DESC", I18N.getMsg((String)locationRow.get("DETAILED_DESC"), new Object[0]));
                locationErrorMap.put("KB_URL", ErrorCodeHandler.getInstance().getKBURL(Long.parseLong(String.valueOf(locationRow.get("ERROR_CODE")))));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getLocationErrorMap", ex);
        }
        return locationErrorMap;
    }
    
    public int getLocationErrorCode(final Long resourceid) {
        int errorCode = 0;
        try {
            final Object result = DBUtil.getValueFromDB("MdDeviceLocationToErrCode", "RESOURCE_ID", (Object)resourceid, "ERROR_CODE");
            if (result != null) {
                errorCode = (int)result;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getLocationErrorCode", ex);
        }
        return errorCode;
    }
    
    public boolean isLocationDetailsEmpty(final Long customerId) {
        boolean isLocEmpty = true;
        try {
            final SelectQuery countQuery = this.getDeviceLocationsDetailsQuery(customerId, null, null, true);
            final ArrayList selectColumnList = (ArrayList)countQuery.getSelectColumns();
            final ArrayList sortColumnList = (ArrayList)countQuery.getSortColumns();
            for (final Object obj : selectColumnList) {
                countQuery.removeSelectColumn((Column)obj);
            }
            for (final Object obj : sortColumnList) {
                countQuery.removeSortColumn((SortColumn)obj);
            }
            final ArrayList groupByColList = new ArrayList();
            Column countColumn = new Column("ManagedDevice", "RESOURCE_ID");
            countColumn = countColumn.distinct();
            countColumn = countColumn.count();
            countColumn.setColumnAlias("COUNT");
            countQuery.addSelectColumn(countColumn);
            groupByColList.add(new Column("Resource", "CUSTOMER_ID"));
            final GroupByClause groupByClause = new GroupByClause((List)groupByColList);
            countQuery.setGroupByClause(groupByClause);
            countQuery.addSelectColumns((List)groupByColList);
            final int count = DBUtil.getRecordCount(countQuery);
            isLocEmpty = (count <= 0);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in isLocationDetailsEmpty", ex);
        }
        return isLocEmpty;
    }
    
    public JSONObject getLocationResourceJSON(final Long customerId, final String searchChar, final Long groupId) {
        JSONObject resLocJSON = null;
        try {
            resLocJSON = new JSONObject();
            if (groupId == null) {
                final SelectQuery locationGroupQuery = this.getLocationGroupQuery(customerId, searchChar);
                MDMGroupHandler.getInstance();
                final List customGroupsList = MDMGroupHandler.getCustomGroupDetailsList(locationGroupQuery);
                final JSONArray groupJSON = new JSONArray((Collection)customGroupsList);
                resLocJSON.put("GROUP", (Object)groupJSON);
            }
            final JSONArray deviceLocDetailsJSON = this.getDeviceLocationsDetails(customerId, groupId, searchChar, null);
            resLocJSON.put("DEVICE", (Object)deviceLocDetailsJSON);
        }
        catch (final JSONException ex) {
            Logger.getLogger(MDMGeoLocationHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return resLocJSON;
    }
    
    private SelectQuery getLocationGroupQuery(final Long customerId, final String searchChar) {
        SelectQuery locationGroupQuery = null;
        final List groupTypeList = MDMGroupHandler.getMDMGroupType();
        MDMGroupHandler.getInstance();
        locationGroupQuery = MDMGroupHandler.getCustomGroupsQuery(groupTypeList);
        Criteria locGroupCriteria = locationGroupQuery.getCriteria();
        final Criteria customerIdCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        if (searchChar != null) {
            final Criteria searchCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchChar, 12, false);
            locGroupCriteria = locGroupCriteria.and(customerIdCri).and(searchCri);
        }
        locationGroupQuery.setCriteria(locGroupCriteria);
        return locationGroupQuery;
    }
    
    private JSONArray getDeviceLocationsDetails(final Long customerId, final Long groupId, final String searchChar, final Boolean isLocEnabled) {
        JSONArray deviceLocDetailsJSON = null;
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery locQuery = this.getDeviceLocationsDetailsQuery(customerId, groupId, searchChar, isLocEnabled);
            ds = DMDataSetWrapper.executeQuery((Object)locQuery);
            JSONObject locationDetailObj = null;
            deviceLocDetailsJSON = new JSONArray();
            Long addedTime = -1L;
            String addedTimeStr = "";
            int deviceStatus = -1;
            while (ds.next()) {
                boolean isManaged = true;
                locationDetailObj = new JSONObject();
                locationDetailObj.put("RESOURCE_ID", (Object)String.valueOf(ds.getValue("RESOURCE_ID")));
                locationDetailObj.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                locationDetailObj.put("MANAGED_STATUS", ds.getValue("MANAGED_STATUS"));
                locationDetailObj.put("IS_ENABLED", ds.getValue("IS_ENABLED"));
                locationDetailObj.put("OS_VERSION", ds.getValue("OS_VERSION"));
                locationDetailObj.put("MODEL_TYPE", ds.getValue("MODEL_TYPE"));
                locationDetailObj.put("NAME", ds.getValue("NAME"));
                locationDetailObj.put("USER_NAME", ds.getValue("USER_NAME"));
                locationDetailObj.put("LATITUDE", ds.getValue("LATITUDE"));
                locationDetailObj.put("LONGITUDE", ds.getValue("LONGITUDE"));
                locationDetailObj.put("ADDED_TIME", ds.getValue("ADDED_TIME"));
                locationDetailObj.put("LOCATION_DETAIL_ID", ds.getValue("LOCATION_DETAIL_ID"));
                addedTime = (Long)ds.getValue("LOCATED_TIME");
                deviceStatus = (int)ds.getValue("MANAGED_STATUS");
                if (deviceStatus == 4) {
                    isManaged = false;
                }
                locationDetailObj.put("IS_MANAGED", isManaged);
                addedTimeStr = Utils.getEventTime(addedTime);
                locationDetailObj.put("ADDED_TIME_STR", (Object)addedTimeStr);
                deviceLocDetailsJSON.put((Object)locationDetailObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getDeviceLocationsDetails", ex);
        }
        return deviceLocDetailsJSON;
    }
    
    public SelectQuery getDeviceLocationsDetailsQuery(final Long customerId, final Long groupId, final String searchChar, final Boolean isLocEnabled) {
        return this.getDeviceLocationsDetailsQuery(customerId, groupId, searchChar, isLocEnabled, true);
    }
    
    public SelectQuery getDeviceLocationsDetailsQuery(final Long customerId, final Long groupId, final String searchChar, final Boolean isLocEnabled, final Boolean onlyManagedDevices) {
        SelectQuery locQuery = null;
        try {
            locQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            final Join recJoin = new Join("MdDeviceLocationDetails", "DeviceRecentLocation", new String[] { "LOCATION_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 2);
            final Join deviceJoin = new Join("DeviceRecentLocation", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join deviceExtnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join customerJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join mdDeviceJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join locStatusJoin = new Join("ManagedDevice", "LocationDeviceStatus", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join mdmodelJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1);
            final Join userDeviceJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join userJoin = new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "UserResource", 2);
            final Join enrollemntRelReuqest = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join enrollemntReuqest = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            locQuery.addJoin(recJoin);
            locQuery.addJoin(deviceJoin);
            locQuery.addJoin(customerJoin);
            locQuery.addJoin(mdDeviceJoin);
            locQuery.addJoin(locStatusJoin);
            locQuery.addJoin(mdmodelJoin);
            locQuery.addJoin(deviceExtnJoin);
            locQuery.addJoin(userDeviceJoin);
            locQuery.addJoin(userJoin);
            locQuery.addJoin(enrollemntRelReuqest);
            locQuery.addJoin(enrollemntReuqest);
            final Criteria cPrivacy = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"), (Object)Column.getColumn("MDPrivacyToOwnedBy", "OWNED_BY"), 0);
            final Criteria cCustomer = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("MDPrivacyToOwnedBy", "CUSTOMER_ID"), 0);
            locQuery.addJoin(new Join("DeviceEnrollmentRequest", "MDPrivacyToOwnedBy", cPrivacy.and(cCustomer), 1));
            locQuery.addJoin(new Join("MDPrivacyToOwnedBy", "MDMPrivacySettings", new String[] { "PRIVACY_SETTINGS_ID" }, new String[] { "PRIVACY_SETTINGS_ID" }, 1));
            locQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            locQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            locQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            locQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            locQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            locQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
            locQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            locQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"));
            locQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"));
            locQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            locQuery.addSelectColumn(Column.getColumn("UserResource", "NAME", "USER_NAME"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            locQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            Criteria locCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            if (onlyManagedDevices) {
                locCriteria = locCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            }
            if (groupId != null) {
                locQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
                locQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
                final Join groupRelJoin = new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2);
                locQuery.addJoin(groupRelJoin);
                final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                locCriteria = locCriteria.and(groupIdCri);
            }
            if (searchChar != null) {
                final Criteria searchDeviceCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchChar, 12, false);
                final Criteria searchUserCri = new Criteria(Column.getColumn("UserResource", "NAME", "USER_NAME"), (Object)searchChar, 12, false);
                locCriteria = locCriteria.and(searchDeviceCri.or(searchUserCri));
            }
            if (isLocEnabled != null) {
                final Criteria locStatusCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)isLocEnabled, 0);
                locCriteria = locCriteria.and(locStatusCri);
            }
            final Criteria privacyCriteria = new Criteria(Column.getColumn("MDMPrivacySettings", "FETCH_LOCATION"), (Object)null, 0);
            final Criteria privacyCriteria2 = new Criteria(Column.getColumn("MDMPrivacySettings", "FETCH_LOCATION"), (Object)0, 0);
            locCriteria = locCriteria.and(privacyCriteria.or(privacyCriteria2));
            locQuery.setCriteria(locCriteria);
            final SortColumn sortCol = new SortColumn(Column.getColumn("ManagedDeviceExtn", "NAME"), true);
            final SortColumn statusCol = new SortColumn(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), false);
            locQuery.addSortColumn(statusCol);
            locQuery.addSortColumn(sortCol);
            locQuery = RBDAUtil.getInstance().getRBDAQuery(locQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getDeviceLocationsDetailsQuery", ex);
        }
        return locQuery;
    }
    
    public void removeDeviceLocation(final Long resourceID) {
        try {
            this.deleteDeviceLocationErrorCode(resourceID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while removeDeviceLocation", ex);
        }
    }
    
    public void removeDeviceLocation(final List<Long> resourceList) {
        try {
            this.deleteDeviceLocationErrorCode(resourceList);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while removeDeviceLocation", ex);
        }
    }
    
    public boolean isLocationDataEmptyForDevice(final Long resourceId, final Long customerId, final Integer lostModeStatus) {
        boolean isEmpty = true;
        try {
            Boolean isLocationEnabled = null;
            if (!lostModeStatus.equals(2)) {
                isLocationEnabled = Boolean.TRUE;
            }
            final SelectQuery countQuery = this.getDeviceLocationsDetailsQuery(customerId, null, ManagedDeviceHandler.getInstance().getDeviceName(resourceId), isLocationEnabled, false);
            final ArrayList selectColumnList = (ArrayList)countQuery.getSelectColumns();
            final ArrayList sortColumnList = (ArrayList)countQuery.getSortColumns();
            for (final Object obj : selectColumnList) {
                countQuery.removeSelectColumn((Column)obj);
            }
            for (final Object obj : sortColumnList) {
                countQuery.removeSortColumn((SortColumn)obj);
            }
            final ArrayList groupByColList = new ArrayList();
            Column countColumn = new Column("ManagedDevice", "RESOURCE_ID");
            countColumn = countColumn.distinct();
            countColumn = countColumn.count();
            countColumn.setColumnAlias("COUNT");
            countQuery.addSelectColumn(countColumn);
            groupByColList.add(new Column("Resource", "CUSTOMER_ID"));
            final GroupByClause groupByClause = new GroupByClause((List)groupByColList);
            countQuery.setGroupByClause(groupByClause);
            countQuery.addSelectColumns((List)groupByColList);
            final int count = DBUtil.getRecordCount(countQuery);
            isEmpty = (count <= 0);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "isLocationDataEmptyForDevice() -- Exception", e);
        }
        return isEmpty;
    }
    
    static {
        MDMGeoLocationHandler.geoLocation = null;
    }
}
