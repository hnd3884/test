package com.me.mdm.server.settings.location;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.server.tracker.mics.MICSGeoTrackingFeatureController;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.customgroup.resource.Group;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.inv.settings.GeoTrackingSettings;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.Collection;
import java.util.List;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import org.json.JSONException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashSet;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LocationSettingsDataHandler
{
    private static LocationSettingsDataHandler locSettings;
    public static Logger logger;
    public static final int TRACKING_STATUS_ALWAYS = 1;
    public static final int TRACKING_STATUS_WHEN_LOST = 2;
    public static final int TRACKING_STATUS_DISABLE = 3;
    public static final int LOCATION_HISTORY_ENABLED = 1;
    public static final int LOCATION_HISTORY_DISABLED = 0;
    
    public static LocationSettingsDataHandler getInstance() {
        if (LocationSettingsDataHandler.locSettings == null) {
            LocationSettingsDataHandler.locSettings = new LocationSettingsDataHandler();
        }
        return LocationSettingsDataHandler.locSettings;
    }
    
    public void handleLocationSettingsResource(final JSONObject locationSettingsJSON) {
        try {
            MDMUtil.getUserTransaction().begin();
            this.addOrUpdateLocationResourceCriteria(locationSettingsJSON);
            final HashSet locationDeviceList = this.getLocationDeviceSet(locationSettingsJSON);
            this.updateLocationDeviceStatus(locationSettingsJSON, locationDeviceList);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in handleLocationSettingsResource() method : {0}", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex) {
                LocationSettingsDataHandler.logger.log(Level.SEVERE, "Exception while rollback of handling Location setting resources", ex);
            }
        }
    }
    
    public SelectQuery getLocationResourceCriteriaQuery(final JSONObject locationSettingsJSON) {
        SelectQuery locationResourceQuery = null;
        try {
            final Long locationSettingsId = JSONUtil.optLongForUVH(locationSettingsJSON, "LOCATION_SETTINGS_ID", Long.valueOf(-1L));
            final Long locationSettingsUpdatedTime = locationSettingsJSON.getLong("UPDATED_TIME");
            locationResourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LocationResourceCriteria"));
            final Join resJoin = new Join("LocationResourceCriteria", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join deviceExtnJoin = new Join("Resource", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            final Join userCustomGroupMapping = new Join("Resource", "UserCustomGroupMapping", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
            final Join customGroupExtn = new Join("Resource", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            locationResourceQuery.addJoin(resJoin);
            locationResourceQuery.addJoin(deviceExtnJoin);
            locationResourceQuery.addJoin(userCustomGroupMapping);
            locationResourceQuery.addJoin(customGroupExtn);
            final Criteria locationSettingsIdCri = new Criteria(Column.getColumn("LocationResourceCriteria", "LOCATION_SETTINGS_ID"), (Object)locationSettingsId, 0);
            final Criteria locationSettingsUpdatedTimeCri = new Criteria(Column.getColumn("LocationResourceCriteria", "UPDATED_TIME"), (Object)locationSettingsUpdatedTime, 4);
            final Criteria cri = locationSettingsIdCri.and(locationSettingsUpdatedTimeCri);
            locationResourceQuery.setCriteria(cri);
            locationResourceQuery.addSelectColumn(Column.getColumn("LocationResourceCriteria", "RESOURCE_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("LocationResourceCriteria", "LOCATION_SETTINGS_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("LocationResourceCriteria", "UPDATED_TIME"));
            locationResourceQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            locationResourceQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
            locationResourceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            locationResourceQuery.addSelectColumn(Column.getColumn("UserCustomGroupMapping", "GROUP_RESOURCE_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"));
            locationResourceQuery.addSelectColumn(Column.getColumn("CustomGroupExtn", "CREATED_BY"));
        }
        catch (final JSONException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getLocationResourceCriteriaQuery() method : {0}", (Throwable)e);
        }
        return locationResourceQuery;
    }
    
    public void addOrUpdateLocationResourceCriteria(final JSONObject locationSettingsJSON) {
        try {
            final Long locationSettingsId = locationSettingsJSON.getLong("LOCATION_SETTINGS_ID");
            final int locationTrackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
            if (locationTrackingStatus == 1) {
                final JSONObject resourceJSON = locationSettingsJSON.optJSONObject("resourceJSON");
                final JSONObject resourceToRemove = locationSettingsJSON.optJSONObject("resourceToRemove");
                Long resourceId = null;
                Row locationResCriRow = null;
                final Criteria locationResCriIdCri = new Criteria(Column.getColumn("LocationResourceCriteria", "LOCATION_SETTINGS_ID"), (Object)locationSettingsId, 0);
                final DataObject locationResCriDO = MDMUtil.getPersistence().get("LocationResourceCriteria", locationResCriIdCri);
                if (resourceJSON != null) {
                    final Iterator resIdItr = resourceJSON.keys();
                    while (resIdItr.hasNext()) {
                        final String resourceIdStr = resIdItr.next();
                        resourceId = Long.valueOf(resourceIdStr);
                        final Criteria resIdCri = new Criteria(Column.getColumn("LocationResourceCriteria", "RESOURCE_ID"), (Object)resourceId, 0);
                        locationResCriRow = locationResCriDO.getRow("LocationResourceCriteria", resIdCri);
                        if (locationResCriRow == null) {
                            locationResCriRow = new Row("LocationResourceCriteria");
                            locationResCriRow.set("LOCATION_SETTINGS_ID", (Object)locationSettingsId);
                            locationResCriRow.set("RESOURCE_ID", (Object)resourceId);
                            locationResCriRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                            locationResCriDO.addRow(locationResCriRow);
                        }
                        else {
                            locationResCriRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                            locationResCriDO.updateRow(locationResCriRow);
                        }
                    }
                }
                if (resourceToRemove != null) {
                    final Iterator resIdItr = resourceToRemove.keys();
                    while (resIdItr.hasNext()) {
                        final String resourceIdStr = resIdItr.next();
                        resourceId = Long.valueOf(resourceIdStr);
                        final Criteria resIdCri = new Criteria(Column.getColumn("LocationResourceCriteria", "RESOURCE_ID"), (Object)resourceId, 0);
                        locationResCriRow = locationResCriDO.getRow("LocationResourceCriteria", resIdCri);
                        if (locationResCriRow != null) {
                            locationResCriDO.deleteRow(locationResCriRow);
                        }
                    }
                }
                MDMUtil.getPersistence().update(locationResCriDO);
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in handleLocationResourceCriteria() method : {0}", (Throwable)e);
        }
        catch (final JSONException e2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in handleLocationResourceCriteria() method : {0}", (Throwable)e2);
        }
    }
    
    private HashSet getLocationDeviceSet(final JSONObject locationSettingsJSON) {
        HashSet locationDeviceList = null;
        try {
            final int trackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
            final JSONObject resourceJSON = locationSettingsJSON.optJSONObject("resourceJSON");
            if (trackingStatus == 1 && resourceJSON != null) {
                final Iterator resIdItr = resourceJSON.keys();
                Long resourceId = null;
                locationDeviceList = new HashSet();
                final ArrayList<Long> resourceList = new ArrayList<Long>();
                while (resIdItr.hasNext()) {
                    final String resourceIdStr = resIdItr.next();
                    final JSONObject resourcePropJSON = resourceJSON.getJSONObject(resourceIdStr);
                    resourceId = JSONUtil.optLongForUVH(resourcePropJSON, "NODE_ID", (Long)null);
                    resourceList.add(resourceId);
                }
                final HashMap groupMap = MDMGroupHandler.getInstance().getGroupTypeBasedMapForGroups(resourceList);
                ArrayList<Long> groupIdList = new ArrayList<Long>();
                ArrayList<Long> userGroupIdList = new ArrayList<Long>();
                if (groupMap.containsKey(6)) {
                    groupIdList = groupMap.get(6);
                    resourceList.removeAll(groupIdList);
                }
                if (groupMap.containsKey(7)) {
                    userGroupIdList = groupMap.get(7);
                    resourceList.removeAll(userGroupIdList);
                }
                locationDeviceList.addAll(resourceList);
                if (groupIdList != null && !groupIdList.isEmpty()) {
                    final List<Integer> resourceTypeList = new ArrayList<Integer>();
                    resourceTypeList.add(120);
                    resourceTypeList.add(121);
                    final ArrayList<Long> groupMemberList = (ArrayList)MDMGroupHandler.getMemberIdListForGroups(groupIdList, resourceTypeList);
                    locationDeviceList.addAll(groupMemberList);
                }
                if (userGroupIdList != null && !userGroupIdList.isEmpty()) {
                    final List<Integer> resourceTypeList = new ArrayList<Integer>();
                    resourceTypeList.add(2);
                    final ArrayList<Long> groupMemberList = (ArrayList)MDMGroupHandler.getMemberIdListForGroups(userGroupIdList, resourceTypeList);
                    final List<Long> deviceMemberList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(groupMemberList, 2);
                    locationDeviceList.addAll(deviceMemberList);
                }
            }
        }
        catch (final JSONException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in handleLocationDeviceStatus", (Throwable)e);
        }
        catch (final Exception e2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in handleLocationDeviceStatus", e2);
        }
        return locationDeviceList;
    }
    
    private void updateLocationDeviceStatus(final JSONObject locationSettingsJSON, HashSet locationDeviceList) {
        try {
            final int resourceCriteria = locationSettingsJSON.getInt("RESOURCE_CRITERIA");
            final int trackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
            final Boolean isInclusion = resourceCriteria != 0;
            final DataObject locationDeviceStatusDO = this.getLocationDeviceStatusDO(locationSettingsJSON);
            if (locationDeviceList == null) {
                locationDeviceList = new HashSet();
            }
            if (trackingStatus == 1) {
                this.updateSelectedDeviceStatus(locationDeviceStatusDO, locationDeviceList, isInclusion);
                this.updateNotSelectedDeviceStatus(locationDeviceStatusDO, locationDeviceList, isInclusion);
            }
            else {
                this.updateLocationTrackingDisabledStatus(locationDeviceStatusDO);
            }
            MDMUtil.getPersistence().update(locationDeviceStatusDO);
        }
        catch (final JSONException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in updateLocationDeviceStatus", (Throwable)e);
        }
        catch (final DataAccessException e2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in updateLocationDeviceStatus", (Throwable)e2);
        }
    }
    
    private void updateLocationTrackingDisabledStatus(final DataObject locationDeviceStatusDO) {
        try {
            if (!locationDeviceStatusDO.isEmpty()) {
                final Criteria enableCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)true, 0);
                final Iterator selectedDeviceItr = locationDeviceStatusDO.getRows("LocationDeviceStatus", enableCri);
                Row locationDeviceRow = null;
                while (selectedDeviceItr.hasNext()) {
                    locationDeviceRow = selectedDeviceItr.next();
                    locationDeviceRow.set("IS_ENABLED", (Object)false);
                    locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                    locationDeviceStatusDO.updateRow(locationDeviceRow);
                }
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in updateLocationTrackingDisabledStatus", (Throwable)e);
        }
    }
    
    public void updateSelectedDeviceStatus(final DataObject locationDeviceStatusDO, final HashSet locationDeviceList, final Boolean isInclusion) {
        try {
            if (!locationDeviceStatusDO.isEmpty()) {
                final Criteria locationDeviceListCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)locationDeviceList.toArray(), 8);
                final Criteria enableDisableCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)isInclusion, 1);
                final Iterator selectedDeviceItr = locationDeviceStatusDO.getRows("ManagedDevice", locationDeviceListCri);
                Row managedDeviceRow = null;
                while (selectedDeviceItr.hasNext()) {
                    managedDeviceRow = selectedDeviceItr.next();
                    final Long resourceId = (Long)managedDeviceRow.get("RESOURCE_ID");
                    Row locationDeviceRow = locationDeviceStatusDO.getRow("LocationDeviceStatus", new Criteria(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
                    if (locationDeviceRow != null) {
                        locationDeviceRow.set("IS_ENABLED", (Object)isInclusion);
                        locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        locationDeviceStatusDO.updateRow(locationDeviceRow);
                    }
                    else {
                        locationDeviceRow = new Row("LocationDeviceStatus");
                        locationDeviceRow.set("MANAGED_DEVICE_ID", (Object)resourceId);
                        locationDeviceRow.set("IS_ENABLED", (Object)isInclusion);
                        locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        locationDeviceStatusDO.addRow(locationDeviceRow);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in updateSelectedDeviceStatus", (Throwable)e);
        }
    }
    
    private void updateNotSelectedDeviceStatus(final DataObject locationDeviceStatusDO, final HashSet locationDeviceList, final Boolean isInclusion) {
        try {
            if (!locationDeviceStatusDO.isEmpty()) {
                final Criteria locationDeviceListCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)locationDeviceList.toArray(), 9);
                final Iterator selectedDeviceItr = locationDeviceStatusDO.getRows("ManagedDevice", locationDeviceListCri);
                Row managedDeviceRow = null;
                while (selectedDeviceItr.hasNext()) {
                    managedDeviceRow = selectedDeviceItr.next();
                    final Long resourceId = (Long)managedDeviceRow.get("RESOURCE_ID");
                    Row locationDeviceRow = locationDeviceStatusDO.getRow("LocationDeviceStatus", new Criteria(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
                    if (locationDeviceRow != null) {
                        locationDeviceRow.set("IS_ENABLED", (Object)!isInclusion);
                        locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        locationDeviceStatusDO.updateRow(locationDeviceRow);
                    }
                    else {
                        locationDeviceRow = new Row("LocationDeviceStatus");
                        locationDeviceRow.set("MANAGED_DEVICE_ID", (Object)resourceId);
                        locationDeviceRow.set("IS_ENABLED", (Object)!isInclusion);
                        locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        locationDeviceStatusDO.addRow(locationDeviceRow);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in updateSelectedDeviceStatus", (Throwable)e);
        }
    }
    
    public JSONObject getLocationSettingsJSON(final Long customerID) {
        JSONObject locationSettingsJSON = null;
        try {
            final Criteria locationCri = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject locationSettingsDO = MDMUtil.getPersistence().get("LocationSettings", locationCri);
            if (!locationSettingsDO.isEmpty()) {
                final Row locationSettingsRow = locationSettingsDO.getFirstRow("LocationSettings");
                final int locationServices;
                final boolean isLocationTrackingEnable = (locationServices = (((boolean)locationSettingsRow.get("IS_LOCATION_TRACKING")) ? 1 : 0)) != 0;
                locationSettingsJSON = new JSONObject();
                locationSettingsJSON.put("IS_LOCATION_TRACKING", locationServices);
                locationSettingsJSON.put("TRACKING_STATUS", locationSettingsRow.get("TRACKING_STATUS"));
                locationSettingsJSON.put("LOCATION_SETTINGS_ID", locationSettingsRow.get("LOCATION_SETTINGS_ID"));
                locationSettingsJSON.put("CUSTOMER_ID", locationSettingsRow.get("CUSTOMER_ID"));
                locationSettingsJSON.put("RESOURCE_CRITERIA", locationSettingsRow.get("RESOURCE_CRITERIA"));
                locationSettingsJSON.put("LOCATION_INTERVAL", locationSettingsRow.get("LOCATION_INTERVAL"));
                locationSettingsJSON.put("LOCATION_ACCURACY", locationSettingsRow.get("LOCATION_ACCURACY"));
                locationSettingsJSON.put("LOCATION_RADIUS", locationSettingsRow.get("LOCATION_RADIUS"));
                locationSettingsJSON.put("UPDATED_TIME", locationSettingsRow.get("UPDATED_TIME"));
                locationSettingsJSON.put("UPDATED_BY", locationSettingsRow.get("UPDATED_BY"));
                locationSettingsJSON.put("GOOGLE_MAP_API_KEY", locationSettingsRow.get("GOOGLE_MAP_API_KEY"));
                locationSettingsJSON.put("EMAIL_ADDRESS", locationSettingsRow.get("EMAIL_ADDRESS"));
                locationSettingsJSON.put("MAP_TYPE", locationSettingsRow.get("MAP_TYPE"));
                locationSettingsJSON.put("LOCATION_HISTORY_STATUS", locationSettingsRow.get("LOCATION_HISTORY_STATUS"));
                locationSettingsJSON.put("LOCATION_HISTORY_DURATION", locationSettingsRow.get("LOCATION_HISTORY_DURATION"));
            }
        }
        catch (final JSONException ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getLocationSettingsJSON() method : {0}", (Throwable)ex);
        }
        catch (final DataAccessException ex2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getLocationSettingsJSON() method : {0}", (Throwable)ex2);
        }
        return locationSettingsJSON;
    }
    
    public JSONObject getLocationSettingsJSON(final DataObject locationSettingsDO) {
        JSONObject locationSettingsJSON = null;
        try {
            if (!locationSettingsDO.isEmpty()) {
                final Row locationRow = locationSettingsDO.getRow("LocationSettings");
                final Long customerId = (Long)locationRow.get("CUSTOMER_ID");
                final Long locationSettingsId = (Long)locationRow.get("LOCATION_SETTINGS_ID");
                final Boolean isLocationEnabled = (Boolean)locationRow.get("IS_LOCATION_TRACKING");
                final int resourceCriteria = (int)locationRow.get("RESOURCE_CRITERIA");
                final Long locationSettingsUpdatedTime = (Long)locationRow.get("UPDATED_TIME");
                final int locationTrackingStatus = (int)locationRow.get("TRACKING_STATUS");
                locationSettingsJSON = new JSONObject();
                locationSettingsJSON.put("CUSTOMER_ID", (Object)customerId);
                locationSettingsJSON.put("LOCATION_SETTINGS_ID", (Object)locationSettingsId);
                locationSettingsJSON.put("IS_LOCATION_TRACKING", (Object)isLocationEnabled);
                locationSettingsJSON.put("TRACKING_STATUS", locationTrackingStatus);
                locationSettingsJSON.put("RESOURCE_CRITERIA", resourceCriteria);
                locationSettingsJSON.put("UPDATED_TIME", (Object)locationSettingsUpdatedTime);
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getLocationSettingsJSON() method : {0}", (Throwable)e);
        }
        catch (final JSONException e2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getLocationSettingsJSON() method : {0}", (Throwable)e2);
        }
        return locationSettingsJSON;
    }
    
    public void addOrUpdateLocationDeviceStatus(final Long deviceId, final boolean isDeviceLocationStatusEnabled) {
        try {
            final Criteria deviceIdCri = new Criteria(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
            final DataObject locationDeviceStatusDO = MDMUtil.getPersistence().get("LocationDeviceStatus", deviceIdCri);
            Row locationDeviceRow = null;
            if (locationDeviceStatusDO.isEmpty()) {
                locationDeviceRow = new Row("LocationDeviceStatus");
                locationDeviceRow.set("MANAGED_DEVICE_ID", (Object)deviceId);
                locationDeviceRow.set("IS_ENABLED", (Object)isDeviceLocationStatusEnabled);
                locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                locationDeviceStatusDO.addRow(locationDeviceRow);
                MDMUtil.getPersistence().add(locationDeviceStatusDO);
            }
            else {
                locationDeviceRow = locationDeviceStatusDO.getRow("LocationDeviceStatus", deviceIdCri);
                locationDeviceRow.set("IS_ENABLED", (Object)isDeviceLocationStatusEnabled);
                locationDeviceRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                locationDeviceStatusDO.updateRow(locationDeviceRow);
                MDMUtil.getPersistence().update(locationDeviceStatusDO);
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in addOrUpdateLocationDeviceStatus", (Throwable)e);
        }
    }
    
    public HashSet getNonAssociatedDevices(final MDMGroupMemberEvent groupEvent) {
        HashSet nonAssociatedDeviceList = null;
        final Long[] deviceIds = groupEvent.memberIds;
        final HashSet removedDevicesList = new HashSet();
        removedDevicesList.addAll(Arrays.asList(deviceIds));
        final HashSet associatedLocationDeviceList = this.getAssociatedDevices(groupEvent);
        removedDevicesList.removeAll(associatedLocationDeviceList);
        nonAssociatedDeviceList = removedDevicesList;
        return nonAssociatedDeviceList;
    }
    
    private HashSet getAssociatedDevices(final MDMGroupMemberEvent groupEvent) {
        HashSet locationDeviceList = null;
        try {
            final DataObject locationCGDevicesDO = this.getAssociatedCGDevicesDO(groupEvent);
            final DataObject locationDevicesDO = this.getAssociatedManuallyAddedDevicesDO(groupEvent);
            final Iterator customGroupDevicesItr = locationCGDevicesDO.getRows("ManagedDevice");
            final Iterator locationResourceCriDevicesItr = locationDevicesDO.getRows("LocationResourceCriteria");
            Long deviceId = null;
            Row deviceRow = null;
            locationDeviceList = new HashSet();
            if (!locationCGDevicesDO.isEmpty()) {
                while (customGroupDevicesItr.hasNext()) {
                    deviceRow = customGroupDevicesItr.next();
                    deviceId = (Long)deviceRow.get("RESOURCE_ID");
                    locationDeviceList.add(deviceId);
                }
            }
            if (!locationDevicesDO.isEmpty()) {
                while (locationResourceCriDevicesItr.hasNext()) {
                    deviceRow = locationResourceCriDevicesItr.next();
                    deviceId = (Long)deviceRow.get("RESOURCE_ID");
                    locationDeviceList.add(deviceId);
                }
            }
        }
        catch (final Exception e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getNonAssociatedDevices() method : {0}", e);
        }
        return locationDeviceList;
    }
    
    public DataObject getAssociatedCGDevicesDO(final MDMGroupMemberEvent groupEvent) {
        DataObject locationCGDevicesDO = null;
        try {
            final Long groupId = groupEvent.groupID;
            final Long customerId = groupEvent.customerId;
            final Long locationSettingsUpdatedTime = this.getLocationSettingsUpdatedTime(customerId);
            final SelectQuery locationDevicesQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LocationSettings"));
            final Join locationSettingsJoin = new Join("LocationSettings", "LocationResourceCriteria", new String[] { "LOCATION_SETTINGS_ID" }, new String[] { "LOCATION_SETTINGS_ID" }, 2);
            final Join cGJoin = new Join("LocationResourceCriteria", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join cGMemberJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
            final Join userJoin = new Join("CustomGroupMemberRel", "ManagedUser", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
            final Join userDeviceJoin = new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
            Criteria criteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
            criteria = criteria.or(new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0));
            final Join managedDeviceJoin = new Join("CustomGroupMemberRel", "ManagedDevice", criteria, 2);
            locationDevicesQuery.addJoin(locationSettingsJoin);
            locationDevicesQuery.addJoin(cGJoin);
            locationDevicesQuery.addJoin(cGMemberJoin);
            locationDevicesQuery.addJoin(userJoin);
            locationDevicesQuery.addJoin(userDeviceJoin);
            locationDevicesQuery.addJoin(managedDeviceJoin);
            final Criteria customerCri = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria updatedTimeCri = new Criteria(Column.getColumn("LocationResourceCriteria", "UPDATED_TIME"), (Object)locationSettingsUpdatedTime, 4);
            final Criteria cri = customerCri.and(updatedTimeCri);
            locationDevicesQuery.setCriteria(cri);
            locationDevicesQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            locationCGDevicesDO = MDMUtil.getPersistence().get(locationDevicesQuery);
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getAssociatedCGDevicesDO() method : {0}", (Throwable)e);
        }
        return locationCGDevicesDO;
    }
    
    private DataObject getAssociatedManuallyAddedDevicesDO(final MDMGroupMemberEvent groupEvent) {
        DataObject locationDevicesDO = null;
        try {
            final Long groupId = groupEvent.groupID;
            final Long customerId = groupEvent.customerId;
            final Long locationSettingsUpdatedTime = this.getLocationSettingsUpdatedTime(customerId);
            final SelectQuery locationDevicesQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LocationSettings"));
            final Join locationSettingsJoin = new Join("LocationSettings", "LocationResourceCriteria", new String[] { "LOCATION_SETTINGS_ID" }, new String[] { "LOCATION_SETTINGS_ID" }, 2);
            final Join locationResDeviceJoin = new Join("LocationResourceCriteria", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            locationDevicesQuery.addJoin(locationSettingsJoin);
            locationDevicesQuery.addJoin(locationResDeviceJoin);
            final Criteria customerCri = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria updatedTimeCri = new Criteria(Column.getColumn("LocationResourceCriteria", "UPDATED_TIME"), (Object)locationSettingsUpdatedTime, 4);
            final Criteria cri = customerCri.and(updatedTimeCri);
            locationDevicesQuery.setCriteria(cri);
            locationDevicesQuery.addSelectColumn(Column.getColumn("LocationResourceCriteria", "LOCATION_SETTINGS_ID"));
            locationDevicesQuery.addSelectColumn(Column.getColumn("LocationResourceCriteria", "RESOURCE_ID"));
            locationDevicesDO = MDMUtil.getPersistence().get(locationDevicesQuery);
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getAssociatedManuallyAddedDevicesDO() method : {0}", (Throwable)e);
        }
        return locationDevicesDO;
    }
    
    public DataObject getLocationDeviceStatusDO(final JSONObject locationSettingsJSON) {
        DataObject locationDeviceStatusDO = null;
        try {
            final Long customerId = locationSettingsJSON.getLong("CUSTOMER_ID");
            final SelectQuery locationDeviceStatusQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join locationDeviceStatusJoin = new Join("ManagedDevice", "LocationDeviceStatus", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            locationDeviceStatusQuery.addJoin(resourceJoin);
            locationDeviceStatusQuery.addJoin(locationDeviceStatusJoin);
            final Criteria customerIdCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria managedStatusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            locationDeviceStatusQuery.setCriteria(customerIdCri.and(managedStatusCriteria));
            locationDeviceStatusQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            locationDeviceStatusQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"));
            locationDeviceStatusQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"));
            locationDeviceStatusDO = MDMUtil.getPersistence().get(locationDeviceStatusQuery);
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in getLocationDeviceStatusDO", (Throwable)e);
        }
        catch (final JSONException e2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in getLocationDeviceStatusDO", (Throwable)e2);
        }
        return locationDeviceStatusDO;
    }
    
    public DataObject getLocationSettingsCommandDO(final JSONObject locationSettingsJSON) {
        DataObject locationDeviceCommandDO = null;
        try {
            final Long locationSettingsUpdatedTime = locationSettingsJSON.getLong("UPDATED_TIME");
            final Long customerId = locationSettingsJSON.getLong("CUSTOMER_ID");
            final SelectQuery locationCommandquery = (SelectQuery)new SelectQueryImpl(Table.getTable("LocationDeviceStatus"));
            final Join resjoin = new Join("LocationDeviceStatus", "Resource", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join managedDevicejoin = new Join("LocationDeviceStatus", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            locationCommandquery.addJoin(resjoin);
            locationCommandquery.addJoin(managedDevicejoin);
            final Criteria updatedTimeCri = new Criteria(Column.getColumn("LocationDeviceStatus", "UPDATED_TIME"), (Object)locationSettingsUpdatedTime, 4);
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria managedStatusCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria cri = updatedTimeCri.and(customerCri).and(managedStatusCri);
            locationCommandquery.setCriteria(cri);
            locationCommandquery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            locationCommandquery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            locationDeviceCommandDO = MDMUtil.getPersistence().get(locationCommandquery);
        }
        catch (final DataAccessException e) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in getLocationSettingsCommandDO", (Throwable)e);
        }
        catch (final JSONException e2) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occured in getLocationSettingsCommandDO", (Throwable)e2);
        }
        return locationDeviceCommandDO;
    }
    
    public boolean isLocationTrackingEnabled(final Long customerID) {
        boolean isLocationEnable = false;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerID);
            if (locationSettingsJSON != null) {
                final Integer isLocationEnableInt = locationSettingsJSON.getInt("TRACKING_STATUS");
                if (isLocationEnableInt == 1) {
                    isLocationEnable = true;
                }
            }
        }
        catch (final JSONException ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in isLocationTrackingEnabled() method : {0}", (Throwable)ex);
        }
        return isLocationEnable;
    }
    
    public boolean isLocationTrackingEnabledforDevice(final Long resourceID) {
        boolean isLocationEnable = false;
        try {
            final Criteria statusCri = new Criteria(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"), (Object)resourceID, 0);
            final DataObject deviceStatusDO = MDMUtil.getPersistence().get("LocationDeviceStatus", statusCri);
            if (!deviceStatusDO.isEmpty()) {
                final Row statusRow = deviceStatusDO.getRow("LocationDeviceStatus");
                isLocationEnable = (boolean)statusRow.get("IS_ENABLED");
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in isLocationTrackingEnabledforDevice() method : {0}", ex);
        }
        return isLocationEnable;
    }
    
    private Long getLocationSettingsUpdatedTime(final Long customerID) {
        Long locationSettingsUpdatedTime = null;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerID);
            locationSettingsUpdatedTime = locationSettingsJSON.getLong("UPDATED_TIME");
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getLocationSettingsUpdatedTime() method : {0}", ex);
        }
        return locationSettingsUpdatedTime;
    }
    
    public org.json.simple.JSONObject getGoogleMapJSON(final Long customerID) {
        String googleMapAPIKey = "";
        String emailAddress = "";
        org.json.simple.JSONObject googleMapJSON = null;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerID);
            googleMapJSON = new org.json.simple.JSONObject();
            if (locationSettingsJSON.has("GOOGLE_MAP_API_KEY")) {
                googleMapAPIKey = String.valueOf(locationSettingsJSON.get("GOOGLE_MAP_API_KEY"));
            }
            if (locationSettingsJSON.has("EMAIL_ADDRESS")) {
                emailAddress = String.valueOf(locationSettingsJSON.get("EMAIL_ADDRESS"));
            }
            googleMapJSON.put((Object)"GOOGLE_MAP_API_KEY", (Object)googleMapAPIKey);
            googleMapJSON.put((Object)"EMAIL_ADDRESS", (Object)emailAddress);
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getGoogleMapAPIKey() method : {0}", ex);
        }
        return googleMapJSON;
    }
    
    public String getGoogleMapAPIKey(final Long customerID) {
        String googleMapAPIKey = "";
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerID);
            if (locationSettingsJSON.has("GOOGLE_MAP_API_KEY")) {
                googleMapAPIKey = String.valueOf(locationSettingsJSON.get("GOOGLE_MAP_API_KEY"));
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getGoogleMapAPIKey() method : {0}", ex);
        }
        return googleMapAPIKey;
    }
    
    public void updateGoogleMapAPIKey(final Long customerID, final JSONObject googleMapJSON) {
        try {
            final Criteria locationCri = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject locationSettingsDO = MDMUtil.getPersistence().get("LocationSettings", locationCri);
            if (!locationSettingsDO.isEmpty()) {
                final Row locationSettingsRow = locationSettingsDO.getFirstRow("LocationSettings");
                locationSettingsRow.set("GOOGLE_MAP_API_KEY", (Object)String.valueOf(googleMapJSON.get("GOOGLE_MAP_API_KEY")));
                locationSettingsRow.set("EMAIL_ADDRESS", (Object)String.valueOf(googleMapJSON.get("EMAIL_ADDRESS")));
                locationSettingsDO.updateRow(locationSettingsRow);
                MDMUtil.getPersistence().update(locationSettingsDO);
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in updateGoogleMapAPIKey() method : {0}", ex);
        }
    }
    
    public int getMapType(final Long customerID) {
        int mapType = 0;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerID);
            mapType = locationSettingsJSON.getInt("MAP_TYPE");
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getMapType() method : {0}", ex);
        }
        return mapType;
    }
    
    public void updateMapType(final Long customerID, final int mapType) {
        try {
            final Criteria locationCri = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject locationSettingsDO = MDMUtil.getPersistence().get("LocationSettings", locationCri);
            if (!locationSettingsDO.isEmpty()) {
                final Row locationSettingsRow = locationSettingsDO.getFirstRow("LocationSettings");
                locationSettingsRow.set("MAP_TYPE", (Object)mapType);
                locationSettingsDO.updateRow(locationSettingsRow);
                MDMUtil.getPersistence().update(locationSettingsDO);
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in updateMapType() method : {0}", ex);
        }
    }
    
    public boolean isResourceIncluded(final Long customerID) {
        boolean isResourceIncluded = false;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerID);
            final int isLocationResourceCriteriaInt = locationSettingsJSON.getInt("RESOURCE_CRITERIA");
            if (isLocationResourceCriteriaInt == 1) {
                isResourceIncluded = true;
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in isResourceIncluded() method : {0}", ex);
        }
        return isResourceIncluded;
    }
    
    public int getGroupInclusionStatus(final Long customerId, final List<Long> groupId) {
        int groupInclusionStatus = -1;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerId);
            final int resourceCriteria = locationSettingsJSON.getInt("RESOURCE_CRITERIA");
            final SelectQuery locationResourceQuery = getInstance().getLocationResourceCriteriaQuery(locationSettingsJSON);
            final Criteria groupCri = new Criteria(Column.getColumn("LocationResourceCriteria", "RESOURCE_ID"), (Object)groupId.toArray(), 8);
            final Criteria cri = locationResourceQuery.getCriteria().and(groupCri);
            locationResourceQuery.setCriteria(cri);
            final DataObject locationSettingsDO = MDMUtil.getPersistence().get(locationResourceQuery);
            if (!locationSettingsDO.isEmpty()) {
                groupInclusionStatus = resourceCriteria;
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in getGroupInclusionStatus() method : {0}", ex);
        }
        return groupInclusionStatus;
    }
    
    public Properties getDefaultLocationSettingsProp() {
        Properties prop = null;
        try {
            prop = new Properties();
            ((Hashtable<String, Integer>)prop).put("LOCATION_ACCURACY", 0);
            ((Hashtable<String, Integer>)prop).put("LOCATION_INTERVAL", 60);
            ((Hashtable<String, Boolean>)prop).put("IS_LOCATION_TRACKING", false);
            ((Hashtable<String, Integer>)prop).put("TRACKING_STATUS", 3);
            ((Hashtable<String, Integer>)prop).put("LOCATION_RADIUS", 100);
            ((Hashtable<String, Integer>)prop).put("RESOURCE_CRITERIA", 0);
            ((Hashtable<String, Integer>)prop).put("LOCATION_HISTORY_STATUS", 0);
            ((Hashtable<String, Long>)prop).put("UPDATED_TIME", System.currentTimeMillis());
            Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId == null && CustomerInfoUtil.getInstance().isMSP()) {
                userId = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
            }
            ((Hashtable<String, Long>)prop).put("UPDATED_BY", userId);
        }
        catch (final Exception ex) {
            Logger.getLogger(LocationSettingsRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prop;
    }
    
    public void addorUpdateLocationSettings(final Long customerId, final Properties locationSettingsProp) throws Exception {
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject locationSettingsDO = MDMUtil.getPersistence().get("LocationSettings", cusCri);
            Row locationSettingsRow = null;
            if (locationSettingsDO.isEmpty()) {
                locationSettingsRow = new Row("LocationSettings");
                locationSettingsRow.set("CUSTOMER_ID", (Object)customerId);
                locationSettingsRow.set("IS_LOCATION_TRACKING", ((Hashtable<K, Object>)locationSettingsProp).get("IS_LOCATION_TRACKING"));
                locationSettingsRow.set("TRACKING_STATUS", ((Hashtable<K, Object>)locationSettingsProp).get("TRACKING_STATUS"));
                locationSettingsRow.set("LOCATION_INTERVAL", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_INTERVAL"));
                locationSettingsRow.set("LOCATION_ACCURACY", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_ACCURACY"));
                locationSettingsRow.set("LOCATION_RADIUS", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_RADIUS"));
                locationSettingsRow.set("RESOURCE_CRITERIA", ((Hashtable<K, Object>)locationSettingsProp).get("RESOURCE_CRITERIA"));
                locationSettingsRow.set("UPDATED_TIME", ((Hashtable<K, Object>)locationSettingsProp).get("UPDATED_TIME"));
                locationSettingsRow.set("UPDATED_BY", ((Hashtable<K, Object>)locationSettingsProp).get("UPDATED_BY"));
                locationSettingsRow.set("LOCATION_HISTORY_STATUS", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_HISTORY_STATUS"));
                locationSettingsDO.addRow(locationSettingsRow);
                MDMUtil.getPersistence().add(locationSettingsDO);
            }
            else {
                locationSettingsRow = locationSettingsDO.getFirstRow("LocationSettings");
                locationSettingsRow.set("IS_LOCATION_TRACKING", ((Hashtable<K, Object>)locationSettingsProp).get("IS_LOCATION_TRACKING"));
                locationSettingsRow.set("TRACKING_STATUS", ((Hashtable<K, Object>)locationSettingsProp).get("TRACKING_STATUS"));
                locationSettingsRow.set("LOCATION_INTERVAL", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_INTERVAL"));
                locationSettingsRow.set("LOCATION_ACCURACY", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_ACCURACY"));
                locationSettingsRow.set("LOCATION_RADIUS", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_RADIUS"));
                locationSettingsRow.set("RESOURCE_CRITERIA", ((Hashtable<K, Object>)locationSettingsProp).get("RESOURCE_CRITERIA"));
                locationSettingsRow.set("UPDATED_TIME", ((Hashtable<K, Object>)locationSettingsProp).get("UPDATED_TIME"));
                locationSettingsRow.set("UPDATED_BY", ((Hashtable<K, Object>)locationSettingsProp).get("UPDATED_BY"));
                locationSettingsRow.set("LOCATION_HISTORY_STATUS", ((Hashtable<K, Object>)locationSettingsProp).get("LOCATION_HISTORY_STATUS"));
                locationSettingsDO.updateRow(locationSettingsRow);
                MDMUtil.getPersistence().update(locationSettingsDO);
            }
        }
        catch (final DataAccessException ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in addorUpdateLocationSettings() method : {0}", (Throwable)ex);
            throw ex;
        }
    }
    
    public String getLostModeTrackingEventMsg(final int status) {
        String statusStr = "";
        if (status == 1) {
            statusStr = "dc.mdm.actionlog.location_settings.always_track";
        }
        else if (status == 2) {
            statusStr = "dc.mdm.actionlog.location_settings.when_lost";
        }
        else if (status == 3) {
            statusStr = "dc.mdm.actionlog.location_settings.disable";
        }
        return statusStr;
    }
    
    public boolean isLocationHistoryEnabled(final Long customerId) {
        boolean isEnabled = false;
        try {
            final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(customerId);
            if (locationSettingsJSON != null) {
                final int status = locationSettingsJSON.optInt("LOCATION_HISTORY_STATUS", -1);
                if (status == 1) {
                    isEnabled = true;
                }
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred while checking isLocationHistoryEnabled", ex);
        }
        LocationSettingsDataHandler.logger.log(Level.INFO, "IS LOCATION HISTORY ENABLED : {0}; CUSTOMER ID: {1}", new Object[] { isEnabled, customerId });
        return isEnabled;
    }
    
    public int getLocationTrackingStatus(final Long customerID) {
        int trackingStatus = -1;
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("LocationSettings", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject trackSettingsDO = MDMUtil.getPersistence().get("LocationSettings", customerCriteria);
            if (!trackSettingsDO.isEmpty()) {
                final Row trackingSettingRow = trackSettingsDO.getRow("LocationSettings");
                trackingStatus = (int)trackingSettingRow.get("TRACKING_STATUS");
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred in isLocationTrackingEnabledforDevice() method : {0}", ex);
        }
        return trackingStatus;
    }
    
    private SelectQuery getLocationSettingsApplicableDevicesQuery(final Long customerId) {
        final Criteria managedDevices = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria customerCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria criteria = managedDevices.and(customerCrit);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join iosNativeAppJoin = new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        selectQuery.addJoin(resourceJoin);
        selectQuery.addJoin(iosNativeAppJoin);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        return selectQuery;
    }
    
    private Criteria getLocationSettingsApplicableDeviceCriteria(final int platform, Criteria criteria) {
        Criteria lhPlafCriteria = null;
        final Criteria platformCriteria = lhPlafCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
        if (criteria != null) {
            criteria = criteria.and(lhPlafCriteria);
        }
        else {
            criteria = lhPlafCriteria;
        }
        return criteria;
    }
    
    public List<Long> getLocationSettingsApplicableDevices(final Long customerId, final int platformType) {
        final List<Long> resourceList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = this.getLocationSettingsApplicableDevicesQuery(customerId);
            selectQuery.setCriteria(this.getLocationSettingsApplicableDeviceCriteria(platformType, selectQuery.getCriteria()));
            final DataObject resDO = MDMUtil.getPersistence().get(selectQuery);
            if (!resDO.isEmpty()) {
                final Iterator resIterator = resDO.getRows("ManagedDevice");
                while (resIterator.hasNext()) {
                    final Row row = resIterator.next();
                    resourceList.add((Long)row.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred while getLocationSettingsEligibleDevices", ex);
        }
        return resourceList;
    }
    
    public JSONObject getLocationSettingsForDevice(final Long deviceId) {
        final DeviceDetails deviceDetails = new DeviceDetails(deviceId);
        final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(deviceDetails.customerId);
        final JSONObject locationJsonObj = new JSONObject();
        final int trackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
        final int locationServices = this.getLocationTrackingStatus(deviceDetails, trackingStatus);
        try {
            locationJsonObj.put("location_services", locationServices);
            locationJsonObj.put("location_interval", locationSettingsJSON.get("LOCATION_INTERVAL"));
            locationJsonObj.put("location_radius", locationSettingsJSON.get("LOCATION_RADIUS"));
            locationJsonObj.put("is_location_history_enabled", locationSettingsJSON.get("LOCATION_HISTORY_STATUS"));
            locationJsonObj.put("location_tracking_status", locationServices);
            locationJsonObj.put("tracking_status", trackingStatus);
            locationJsonObj.put("location_history_duration", locationSettingsJSON.optInt("LOCATION_HISTORY_DURATION", 30));
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred while getLocationSettingsForDevice", ex);
        }
        return locationJsonObj;
    }
    
    public JSONObject getLocationConfigCommandData(final DeviceDetails deviceDetails) {
        final JSONObject locationSettingsJSON = this.getLocationSettingsJSON(deviceDetails.customerId);
        final JSONObject locationJsonObj = new JSONObject();
        final int trackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
        final int locationServices = this.getLocationTrackingStatus(deviceDetails, trackingStatus);
        final int trackingSettingAndroid = this.getTrackingSettingAndroid(deviceDetails, trackingStatus);
        try {
            locationJsonObj.put("LocationServices", locationServices);
            locationJsonObj.put("ContactInterval", locationSettingsJSON.get("LOCATION_INTERVAL"));
            locationJsonObj.put("LocationRadius", locationSettingsJSON.get("LOCATION_RADIUS"));
            locationJsonObj.put("IsLocationHistoryEnabled", locationSettingsJSON.get("LOCATION_HISTORY_STATUS"));
            locationJsonObj.put("LocationTrackingSetting", trackingSettingAndroid);
        }
        catch (final Exception ex) {
            LocationSettingsDataHandler.logger.log(Level.WARNING, "Exception occurred while getLocationConfigCommandData", ex);
        }
        return locationJsonObj;
    }
    
    private int getLocationTrackingStatus(final DeviceDetails deviceDetails, final int trackingStatus) {
        int locationServices = -1;
        final int platformType = deviceDetails.platform;
        if (platformType == 2) {
            if (trackingStatus == 2) {
                locationServices = 1;
            }
            else if (new LostModeDataHandler().isTrackingNeededForLostMode(deviceDetails.resourceId)) {
                locationServices = 1;
            }
            else {
                final boolean isLocationTrackingDeviceEnable = (locationServices = (this.isLocationTrackingEnabledforDevice(deviceDetails.resourceId) ? 1 : 0)) != 0;
            }
        }
        else if (platformType == 1) {
            locationServices = 1;
            try {
                final boolean isEnabled = this.isLocationTrackingEnabledforDevice(deviceDetails.resourceId);
                if (trackingStatus == 2 || !isEnabled) {
                    locationServices = 0;
                }
                else {
                    locationServices = 1;
                }
            }
            catch (final Exception e) {
                LocationSettingsDataHandler.logger.log(Level.SEVERE, "Exception in getLocationTrackingStatus ", e);
            }
        }
        return locationServices;
    }
    
    public int getTrackingSettingAndroid(final DeviceDetails deviceDetails, final int trackingStatus) {
        int trackingStatusAndroid = -1;
        final int platformType = deviceDetails.platform;
        if (platformType == 2) {
            if (trackingStatus == 2) {
                trackingStatusAndroid = 0;
            }
            else if (trackingStatus == 1) {
                final boolean isLocationTrackingDeviceEnable = (trackingStatusAndroid = (this.isLocationTrackingEnabledforDevice(deviceDetails.resourceId) ? 1 : 0)) != 0;
            }
        }
        return trackingStatusAndroid;
    }
    
    public List disableLocationOnEditionChange() throws DataAccessException {
        final List customerList = new ArrayList();
        final DataObject DO = MDMUtil.getPersistence().get("LocationSettings", (Criteria)null);
        final Iterator rowItem = DO.getRows("LocationSettings");
        while (rowItem.hasNext()) {
            final Row locRow = rowItem.next();
            locRow.set("LOCATION_HISTORY_STATUS", (Object)0);
            DO.updateRow(locRow);
            customerList.add(locRow.get("CUSTOMER_ID"));
        }
        MDMUtil.getPersistence().update(DO);
        return customerList;
    }
    
    public GeoTrackingSettings getGeoTrackingSettings(final JSONObject request) throws APIHTTPException {
        final Long customerID = APIUtil.getCustomerID(request);
        final GeoTrackingSettings response = new GeoTrackingSettings();
        try {
            final JSONObject settingsJSON = this.getLocationSettingsJSON(customerID);
            final org.json.simple.JSONObject simpleCriteriaJSON = LocationSettingsRequestHandler.getInstance().getLocationResourceCriteriaJSON(settingsJSON);
            final JSONObject criteriaJSON = new JSONObject(simpleCriteriaJSON.toJSONString());
            response.trackingMode = settingsJSON.getInt("TRACKING_STATUS");
            response.applyToAll = (settingsJSON.getInt("RESOURCE_CRITERIA") == 0);
            response.enableLocationHistory = (settingsJSON.getInt("LOCATION_HISTORY_STATUS") == 1);
            response.locationRadius = settingsJSON.getInt("LOCATION_RADIUS");
            response.locationTrackingInterval = settingsJSON.getInt("LOCATION_INTERVAL");
            final Iterator<String> keyIterator = criteriaJSON.keys();
            if (keyIterator.hasNext()) {
                response.groupList = new ArrayList<Group>();
                response.groups = new ArrayList<Long>();
            }
            while (keyIterator.hasNext()) {
                final String key = keyIterator.next();
                final JSONObject groupJSON = criteriaJSON.getJSONObject(key);
                final Group group = new Group();
                group.name = String.valueOf(groupJSON.get("NODE_NAME"));
                group.groupID = Long.valueOf(key);
                response.groupList.add(group);
                response.groups.add(Long.valueOf(key));
            }
            return response;
        }
        catch (final Exception e) {
            LocationSettingsDataHandler.logger.log(Level.SEVERE, "DataAccess Exception in GeoTrackingSettings", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void setLocationSettings(final GeoTrackingSettings geoTrackingSettings, final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            final JSONObject settingsJSON = this.getLocationSettingsJSON(customerID);
            final JSONObject resourceJSON = new JSONObject();
            final Properties properties = new Properties();
            MICSGeoTrackingFeatureController.addTrackingData(geoTrackingSettings.trackingMode);
            settingsJSON.put("TRACKING_STATUS", (Object)geoTrackingSettings.trackingMode);
            settingsJSON.put("UPDATED_TIME", MDMUtil.getCurrentTimeInMillis());
            settingsJSON.put("UPDATED_BY", (Object)APIUtil.getUserID(apiRequest));
            if (geoTrackingSettings.enableLocationHistory == null) {
                geoTrackingSettings.enableLocationHistory = false;
            }
            if (geoTrackingSettings.trackingMode == 1) {
                settingsJSON.put("RESOURCE_CRITERIA", (int)(Object)!geoTrackingSettings.applyToAll);
                settingsJSON.put("LOCATION_HISTORY_STATUS", (int)(((boolean)geoTrackingSettings.enableLocationHistory) ? 1 : 0));
                settingsJSON.put("LOCATION_RADIUS", (Object)geoTrackingSettings.locationRadius);
                settingsJSON.put("LOCATION_INTERVAL", (Object)geoTrackingSettings.locationTrackingInterval);
                if (geoTrackingSettings.trackingMode == 1) {
                    settingsJSON.put("IS_LOCATION_TRACKING", true);
                }
            }
            else {
                settingsJSON.put("IS_LOCATION_TRACKING", false);
                settingsJSON.put("LOCATION_HISTORY_STATUS", 0);
            }
            ((Hashtable<String, Object>)properties).put("TRACKING_STATUS", settingsJSON.get("TRACKING_STATUS"));
            ((Hashtable<String, Object>)properties).put("RESOURCE_CRITERIA", settingsJSON.get("RESOURCE_CRITERIA"));
            ((Hashtable<String, Object>)properties).put("LOCATION_HISTORY_STATUS", settingsJSON.get("LOCATION_HISTORY_STATUS"));
            ((Hashtable<String, Object>)properties).put("LOCATION_RADIUS", settingsJSON.get("LOCATION_RADIUS"));
            ((Hashtable<String, Object>)properties).put("LOCATION_INTERVAL", settingsJSON.get("LOCATION_INTERVAL"));
            ((Hashtable<String, Object>)properties).put("IS_LOCATION_TRACKING", settingsJSON.get("IS_LOCATION_TRACKING"));
            ((Hashtable<String, Object>)properties).put("LOCATION_ACCURACY", settingsJSON.get("LOCATION_ACCURACY"));
            ((Hashtable<String, Object>)properties).put("UPDATED_TIME", settingsJSON.get("UPDATED_TIME"));
            ((Hashtable<String, Object>)properties).put("UPDATED_BY", settingsJSON.get("UPDATED_BY"));
            if (geoTrackingSettings.groups != null) {
                final GroupFacade groupFacade = new GroupFacade();
                groupFacade.validateGroupsIfExists(geoTrackingSettings.groups, APIUtil.getCustomerID(apiRequest));
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                    if (MDMGroupHandler.getInstance().isInCycle(geoTrackingSettings.groups)) {
                        return;
                    }
                    geoTrackingSettings.groups.addAll(MDMGroupHandler.getInstance().getSubGroupList(geoTrackingSettings.groups));
                }
                for (final Long groupID : geoTrackingSettings.groups) {
                    resourceJSON.put(String.valueOf(groupID), (Object)new JSONObject().put("NODE_ID", (Object)groupID));
                }
                settingsJSON.put("resourceJSON", (Object)resourceJSON);
            }
            this.addorUpdateLocationSettings(customerID, properties);
            LocationSettingsRequestHandler.getInstance().handleLocationSettingsUpdate(settingsJSON);
            String remarks;
            if (geoTrackingSettings.trackingMode == 1) {
                remarks = "mdm.geotracking.always_action_log";
            }
            else {
                remarks = "mdm.geotracking.when_lost_action_log";
            }
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2065, null, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), remarks, "", customerID);
        }
        catch (final JSONException e) {
            LocationSettingsDataHandler.logger.log(Level.SEVERE, "Exception in setLocationSettings ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            LocationSettingsDataHandler.logger.log(Level.SEVERE, "Exception in setLocationSettings ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public int getLocationConfigurationMethod() {
        try {
            final Boolean isAndroidApiLocationConfEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("isAndroidApiLocationConfMethod");
            if (!isAndroidApiLocationConfEnabled) {
                return 0;
            }
        }
        catch (final Exception e) {
            LocationSettingsDataHandler.logger.log(Level.SEVERE, "Exception in getLocationConfigurationMethod ", e);
        }
        return 1;
    }
    
    static {
        LocationSettingsDataHandler.locSettings = null;
        LocationSettingsDataHandler.logger = Logger.getLogger("MDMLogger");
    }
}
