package com.me.mdm.server.location;

import java.util.Hashtable;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.mdm.server.settings.location.LocationSettingsUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.export.ExportRequestDetailsHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import com.me.mdm.server.backup.moduleimpl.LocationHistoryDataBackup;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.Date;
import com.me.mdm.server.util.CalendarUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import java.util.HashMap;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONArray;
import com.me.mdm.server.util.MDMSecurityLogger;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LocationDataHandler
{
    public static final Logger logger;
    private static LocationDataHandler locationDataHandler;
    private static String mapsBaseUrl;
    private Long recentLocationUpdationTime;
    private Long recentHisLocationUpdationTime;
    private int DEFAULT_START_RANGE;
    private int DEFAULT_MAX_LOCATIONS_DATA_COUNT;
    public static final String EXPORT_FOLDER_NAME = "exports";
    public static final int EXPORT_HISTORY_TYPE_SPECIFIC_DAY = 2;
    public static final int EXPORT_HISTORY_TYPE_DATE_RANGE = 3;
    
    public LocationDataHandler() {
        this.recentLocationUpdationTime = -1L;
        this.recentHisLocationUpdationTime = -1L;
        this.DEFAULT_START_RANGE = 0;
        this.DEFAULT_MAX_LOCATIONS_DATA_COUNT = 500;
    }
    
    public JSONObject getRecentDeviceLocationInfo(final Long resourceId) {
        JSONObject devLocData = null;
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceRecentLocation", "RESOURCE_ID"), (Object)resourceId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            final Join recentDevLocJoin = new Join("MdDeviceLocationDetails", "DeviceRecentLocation", new String[] { "LOCATION_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 2);
            selectQuery.addJoin(recentDevLocJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.setCriteria(resourceCriteria);
            final DataObject recentDevLocDO = MDMUtil.getPersistence().get(selectQuery);
            if (recentDevLocDO != null && !recentDevLocDO.isEmpty()) {
                devLocData = new JSONObject();
                final Row row = recentDevLocDO.getRow("MdDeviceLocationDetails");
                devLocData.put("DEVICE_ID", (Object)resourceId);
                devLocData.put("LATITUDE", (Object)row.get("LATITUDE"));
                devLocData.put("LONGITUDE", (Object)row.get("LONGITUDE"));
                devLocData.put("ADDED_TIME", (Object)row.get("ADDED_TIME"));
                devLocData.put("LOCATED_TIME", (Object)row.get("LOCATED_TIME"));
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while getRecentDeviceLocationInfo", ex);
        }
        return devLocData;
    }
    
    public void deviceLocationUpdates(final Long resourceId, final Map<String, String> deviceData) {
        try {
            MDMSecurityLogger.info(Logger.getLogger("MDMLogger"), "LocationDataHandler", "deviceLocationUpdates", "Received Device Location Updates{0}", deviceData.toString());
            final String strStatus = deviceData.get("Status");
            final JSONObject locationInfo = new JSONObject((String)deviceData.get("Message"));
            final JSONArray locationArr = locationInfo.optJSONArray("Locations");
            this.handleLocationUpdates(resourceId, locationArr, true);
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while handleLocationUpdates", ex);
        }
    }
    
    public void deviceLocationUpdates(final Long resourceId, final JSONObject locationData) {
        try {
            final JSONArray locationArr = new JSONArray();
            locationArr.put((Object)locationData);
            this.handleLocationUpdates(resourceId, locationArr, false);
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while deviceLocationUpdates", ex);
        }
    }
    
    private void handleLocationUpdates(final Long resourceId, final JSONArray locationArr, final Boolean isHistory) {
        try {
            final DataObject locationsDO = MDMUtil.getPersistence().constructDataObject();
            this.recentHisLocationUpdationTime = this.getRecentHistoryLocatedTimeForResource(resourceId);
            this.recentLocationUpdationTime = this.getRecentLocatedTimeForResource(resourceId);
            final Boolean fetchLocationAddress = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FetchLocationAddress");
            final HashMap locationMap = new HashMap();
            LocationDataHandler.logger.log(Level.INFO, "Going to handle location Update for resource:{0}", new Object[] { resourceId });
            for (int i = 0; i < locationArr.length(); ++i) {
                final JSONObject locationData = locationArr.optJSONObject(i);
                final String latitude = locationData.optString("Latitude", (String)null);
                final String longitude = locationData.optString("Longitude", (String)null);
                final Long locationUpdationTime = locationData.optLong("LocationUpdationTime", -1L);
                LocationDataHandler.logger.log(Level.INFO, "Adding location to resource:{0} for time:{1}", new Object[] { resourceId, locationUpdationTime });
                if (isHistory) {
                    if (locationUpdationTime <= this.recentHisLocationUpdationTime) {
                        LocationDataHandler.logger.log(Level.INFO, "Since recent location update time {0} is less than location update time{1} not adding to DB", new Object[] { locationUpdationTime, this.recentHisLocationUpdationTime });
                        continue;
                    }
                    this.recentHisLocationUpdationTime = locationUpdationTime;
                }
                if (locationUpdationTime <= this.recentLocationUpdationTime) {
                    if (!isHistory) {
                        LocationDataHandler.logger.log(Level.INFO, "Since recent location update time {0} is less than location update time{1} not adding to DB", new Object[] { locationUpdationTime, this.recentLocationUpdationTime });
                        continue;
                    }
                }
                else {
                    this.recentLocationUpdationTime = locationUpdationTime;
                }
                String address = null;
                if (fetchLocationAddress) {
                    if (locationMap.containsKey(latitude + ";" + longitude)) {
                        address = locationMap.get(latitude + ";" + longitude);
                    }
                    else {
                        LocationDataHandler.logger.log(Level.INFO, "Going call maps api");
                        final long startTime = System.currentTimeMillis();
                        final String geoAddressResult = this.getGeoAddressFromZoho(latitude, longitude);
                        LocationDataHandler.logger.log(Level.INFO, "received result from maps....  time taken in ms: {0}", System.currentTimeMillis() - startTime);
                        if (geoAddressResult != null) {
                            address = new JSONObject(geoAddressResult).optString("display_name", (String)null);
                            if (address != null && address.length() > 1000) {
                                address = address.substring(0, 999);
                            }
                            if (locationMap.size() < 10000) {
                                locationMap.put(latitude + ";" + longitude, address);
                            }
                        }
                    }
                }
                final Row locationRow = new Row("MdDeviceLocationDetails");
                locationRow.set("DEVICE_ID", (Object)resourceId);
                locationRow.set("LATITUDE", (Object)latitude);
                locationRow.set("LONGITUDE", (Object)longitude);
                locationRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                locationRow.set("LOCATION_ADDRESS", (Object)address);
                locationRow.set("LOCATED_TIME", (Object)locationUpdationTime);
                locationsDO.addRow(locationRow);
            }
            LocationDataHandler.logger.log(Level.INFO, "Going persist location data");
            final long startTime2 = System.currentTimeMillis();
            MDMUtil.getPersistence().add(locationsDO);
            LocationDataHandler.logger.log(Level.INFO, "data persisted... Time Taken in ms: {0}", System.currentTimeMillis() - startTime2);
            this.handleDeviceRecentLocation(resourceId);
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while handleLocationUpdates", ex);
        }
    }
    
    private Long getRecentLocatedTimeForResource(final Long resourceId) {
        try {
            final SelectQuery recentLocatedTimeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceRecentLocation"));
            recentLocatedTimeQuery.addJoin(new Join("DeviceRecentLocation", "MdDeviceLocationDetails", new String[] { "LOCATION_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 2));
            recentLocatedTimeQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0));
            recentLocatedTimeQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "*"));
            final DataObject recentLocationDO = MDMUtil.getPersistence().get(recentLocatedTimeQuery);
            return (Long)(recentLocationDO.isEmpty() ? -1L : recentLocationDO.getFirstValue("MdDeviceLocationDetails", "LOCATED_TIME"));
        }
        catch (final Exception e) {
            LocationDataHandler.logger.log(Level.WARNING, "LocationDataHandler: Exception occurred while fetching recent Location of Device", e);
            return -1L;
        }
    }
    
    private Long getRecentHistoryLocatedTimeForResource(final Long resourceId) {
        try {
            final SelectQuery recentLocatedTimeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceRecentLocation"));
            recentLocatedTimeQuery.addJoin(new Join("DeviceRecentLocation", "MdDeviceLocationDetails", new String[] { "RECENT_HIS_LOC_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 2));
            recentLocatedTimeQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0));
            recentLocatedTimeQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "*"));
            final DataObject recentLocationDO = MDMUtil.getPersistence().get(recentLocatedTimeQuery);
            return (Long)(recentLocationDO.isEmpty() ? -1L : recentLocationDO.getFirstValue("MdDeviceLocationDetails", "LOCATED_TIME"));
        }
        catch (final Exception e) {
            LocationDataHandler.logger.log(Level.WARNING, "LocationDataHandler: Exception occurred while fetching recent Location of Device", e);
            return -1L;
        }
    }
    
    private void checkRecentLocationForResource(final Long locationUpdationTime) {
        if (locationUpdationTime > this.recentLocationUpdationTime) {
            this.recentLocationUpdationTime = locationUpdationTime;
        }
    }
    
    private void handleDeviceRecentLocation(final Long resourceId) {
        try {
            final Criteria recLocUpdTime = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)this.recentLocationUpdationTime, 0);
            final Criteria recHisLocUpdTime = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)this.recentHisLocationUpdationTime, 0);
            final Criteria resCrit = new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0);
            final Criteria criteria = resCrit.and(recLocUpdTime.or(recHisLocUpdTime));
            final DataObject locationsDO = MDMUtil.getPersistence().get("MdDeviceLocationDetails", criteria);
            if (!locationsDO.isEmpty()) {
                final Row recLocationRow = locationsDO.getRow("MdDeviceLocationDetails", recLocUpdTime);
                final Long locationDetailsId = (Long)recLocationRow.get("LOCATION_DETAIL_ID");
                final Row recHisLocationRow = locationsDO.getRow("MdDeviceLocationDetails", recHisLocUpdTime);
                Long hisLocationDetailsId = null;
                if (recHisLocationRow != null) {
                    hisLocationDetailsId = (Long)recHisLocationRow.get("LOCATION_DETAIL_ID");
                }
                this.addOrUpdateDeviceRecentLocation(resourceId, locationDetailsId, hisLocationDetailsId);
                if (MDMGeoLocationHandler.getInstance().getLocationErrorCode(resourceId) != 12142) {
                    MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(resourceId);
                }
                final JSONObject locationSettingsJson = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId));
                final int locationHistoryEnabled = locationSettingsJson.getInt("LOCATION_HISTORY_STATUS");
                if (locationHistoryEnabled == 0) {
                    this.deleteLocHistoryOfRes(resourceId);
                }
            }
            this.recentLocationUpdationTime = -1L;
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while handleDeviceRecentLocation", ex);
        }
    }
    
    private void deleteLocHistoryOfRes(final Long resourceID) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceLocationDetails");
            final Criteria historyLocCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)Column.getColumn("DeviceRecentLocation", "LOCATION_DETAIL_ID"), 1);
            final Criteria resCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceID, 0);
            deleteQuery.addJoin(new Join("MdDeviceLocationDetails", "DeviceRecentLocation", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.setCriteria(historyLocCri.and(resCri));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            LocationDataHandler.logger.log(Level.SEVERE, "error in deleting location history", e);
        }
    }
    
    public void addOrUpdateDeviceRecentLocation(final Long resourceId, final Long locationDetailsId, final Long hisLocationDetailsId) {
        try {
            if (resourceId != null) {
                final Criteria criteria = new Criteria(Column.getColumn("DeviceRecentLocation", "RESOURCE_ID"), (Object)resourceId, 0);
                final DataObject deviceRecentLocationDO = MDMUtil.getPersistence().get("DeviceRecentLocation", criteria);
                if (deviceRecentLocationDO.isEmpty()) {
                    final Row deviceRecentLocationRow = new Row("DeviceRecentLocation");
                    deviceRecentLocationRow.set("RESOURCE_ID", (Object)resourceId);
                    deviceRecentLocationRow.set("LOCATION_DETAIL_ID", (Object)locationDetailsId);
                    deviceRecentLocationRow.set("RECENT_HIS_LOC_DETAIL_ID", (Object)hisLocationDetailsId);
                    deviceRecentLocationDO.addRow(deviceRecentLocationRow);
                    MDMUtil.getPersistence().add(deviceRecentLocationDO);
                }
                else {
                    final Row deviceRecentLocationRow = deviceRecentLocationDO.getFirstRow("DeviceRecentLocation");
                    deviceRecentLocationRow.set("LOCATION_DETAIL_ID", (Object)locationDetailsId);
                    deviceRecentLocationRow.set("RECENT_HIS_LOC_DETAIL_ID", (Object)hisLocationDetailsId);
                    deviceRecentLocationDO.updateRow(deviceRecentLocationRow);
                    MDMUtil.getPersistence().update(deviceRecentLocationDO);
                }
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while addOrUpdateDeviceRecentLocation", ex);
        }
    }
    
    public JSONObject requestDeviceLocationsOnDay(final JSONObject reqData) {
        final JSONObject locationData = new JSONObject();
        try {
            final String resIDStr = reqData.optString("RESOURCE_ID", (String)null);
            final String dayTimeStr = reqData.optString("LOC_ON_DAY_MILLI", (String)null);
            if (resIDStr != null && !resIDStr.isEmpty() && dayTimeStr != null && !dayTimeStr.isEmpty()) {
                final Long resourceId = Long.valueOf(resIDStr);
                final Date curDay = CalendarUtil.getInstance().getStartTimeOfTheDay(Long.valueOf(dayTimeStr));
                final Long startTime = curDay.getTime();
                final Long endTime = CalendarUtil.getInstance().getStartTimeOfTheDay(CalendarUtil.getInstance().addDays(curDay, 1).getTime()).getTime();
                LocationDataHandler.logger.log(Level.INFO, "Location data fetched for resourceId:{0}, time between {1} to {2}", new Object[] { resIDStr, startTime, endTime });
                final JSONObject loc = this.getDeviceLocationsBetween(resourceId, startTime, endTime);
                if (loc.length() > 0) {
                    locationData.put(startTime + "", (Object)loc);
                }
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while requestDeviceLocationsOnDay", ex);
        }
        return locationData;
    }
    
    public JSONObject requestDeviceLocations(final JSONObject reqData) {
        final JSONObject devLocationData = new JSONObject();
        try {
            final String resIDStr = reqData.optString("RESOURCE_ID", (String)null);
            final String startRangeStr = reqData.optString("START_INDEX", (String)null);
            final String maxDataStr = reqData.optString("MAX_LOCATIONS_DATA", (String)null);
            if (resIDStr != null && !resIDStr.isEmpty()) {
                final Long resourceId = Long.valueOf(resIDStr);
                final int startIndex = (startRangeStr != null && !startRangeStr.isEmpty()) ? Integer.valueOf(startRangeStr) : this.DEFAULT_START_RANGE;
                final int maxLocDataCount = (maxDataStr != null && !maxDataStr.isEmpty()) ? Integer.valueOf(maxDataStr) : this.DEFAULT_MAX_LOCATIONS_DATA_COUNT;
                devLocationData.put("LOCATIONS", (Object)this.getDeviceLocations(resourceId, startIndex, maxLocDataCount));
                devLocationData.put("RESOURCE_ID", (Object)resourceId);
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while requestDeviceLocations", ex);
        }
        return devLocationData;
    }
    
    public JSONObject requestCompleteDeviceLocations(final Long resourceId) {
        final JSONObject devLocationData = new JSONObject();
        try {
            devLocationData.put("locations", (Object)this.getDeviceLocations(resourceId, -1, -1));
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while requestDeviceLocations", ex);
        }
        return devLocationData;
    }
    
    private JSONObject getDeviceLocations(final Long resourceId, final int startIndex, final int maxDataCount) {
        LocationDataHandler.logger.log(Level.INFO, "Locations request for RESOURCE_ID : {0}, Range {1} - {2}", new Object[] { resourceId, startIndex, maxDataCount });
        final JSONObject locationData = new JSONObject();
        DMDataSetWrapper dataSet = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.setCriteria(criteria);
            if (maxDataCount != -1) {
                selectQuery.setRange(new Range(startIndex, maxDataCount));
            }
            final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), false);
            selectQuery.addSortColumn(sortColumn);
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                Long locatedTime = (Long)dataSet.getValue("LOCATED_TIME");
                final Long addedTime = (Long)dataSet.getValue("ADDED_TIME");
                final JSONObject locationInfo = new JSONObject();
                locationInfo.put("LATITUDE", (Object)dataSet.getValue("LATITUDE"));
                locationInfo.put("LONGITUDE", (Object)dataSet.getValue("LONGITUDE"));
                locationInfo.put("ADDED_TIME", (Object)addedTime);
                if (locatedTime == null || locatedTime == -1L) {
                    locatedTime = addedTime;
                }
                locationInfo.put("LOCATED_TIME", (Object)locatedTime);
                locationData.put(locatedTime + "", (Object)locationInfo);
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while getDeviceLocations", ex);
        }
        return locationData;
    }
    
    private Criteria limitLocationsDataCriteria() {
        final CalendarUtil calendarUtil = new CalendarUtil();
        final Long limitTime = calendarUtil.getStartTimeOfTheDay(calendarUtil.addDays(new Date(System.currentTimeMillis()), -1 * (LocationHistoryDataBackup.MAX_DAYS_OF_LOCATION_HISTORY + 3)).getTime()).getTime();
        return new Criteria(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), (Object)limitTime, 5);
    }
    
    private JSONObject getDeviceLocationsBetween(final Long resourceId, final Long fromTime, final Long toTime) {
        final JSONObject locationData = new JSONObject();
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0);
            final Criteria dateDuration = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)new Long[] { fromTime, toTime }, 14);
            final Criteria criteria = resourceCriteria.and(dateDuration);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.setCriteria(criteria);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), false);
            selectQuery.addSortColumn(sortColumn);
            final DataObject locationDO = MDMUtil.getPersistence().get(selectQuery);
            LocationDataHandler.logger.log(Level.INFO, "LOCATION_DATA_SIZE : {0}", locationDO.size("MdDeviceLocationDetails"));
            if (!locationDO.isEmpty()) {
                final Iterator locationIterator = locationDO.getRows("MdDeviceLocationDetails");
                while (locationIterator.hasNext()) {
                    final Row row = locationIterator.next();
                    final Long locatedTime = (Long)row.get("LOCATED_TIME");
                    final JSONObject locationInfo = new JSONObject();
                    locationInfo.put("LATITUDE", (Object)row.get("LATITUDE"));
                    locationInfo.put("LONGITUDE", (Object)row.get("LONGITUDE"));
                    locationInfo.put("ADDED_TIME", (Object)row.get("ADDED_TIME"));
                    locationInfo.put("LOCATED_TIME", (Object)row.get("LOCATED_TIME"));
                    locationData.put(locatedTime + "", (Object)locationInfo);
                }
                LocationDataHandler.logger.log(Level.INFO, "LOCATIONS DETECTED FOR RESOURCE_ID : {0}", resourceId);
            }
            else {
                LocationDataHandler.logger.log(Level.INFO, "NO LOCATIONS FOUND FOR RESOURCE_ID {0}", resourceId);
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while getDeviceLocationsBetween", ex);
        }
        return locationData;
    }
    
    public StringBuilder exportLocationHistoryData(final Long resourceId, final JSONObject data) {
        return this.exportLocationHistoryData(resourceId, data, null);
    }
    
    public StringBuilder exportLocationHistoryData(final Long resourceId, final JSONObject data, final JSONObject addressDetails) {
        final StringBuilder lhContentStrBuilder = new StringBuilder();
        try {
            final Integer exportType = Integer.parseInt(data.optString("EXPORT_HISTORY_TYPE", "-1"));
            if (exportType != -1) {
                final JSONArray locationHistoryJson = this.getLocationHistoryDataForDevice(resourceId, data);
                lhContentStrBuilder.append("Latitude");
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append("Longitude");
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append(I18N.getMsg("dc.mdm.dc.mdm.geoLoc.located.on", new Object[0]));
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append(I18N.getMsg("dc.mdm.dc.mdm.geoLoc.location.milli", new Object[0]));
                if (addressDetails != null) {
                    lhContentStrBuilder.append(",");
                    lhContentStrBuilder.append(I18N.getMsg("mdm.geoLoc.location.address", new Object[0]));
                }
                lhContentStrBuilder.append("\n");
                for (int i = 0; i < locationHistoryJson.length(); ++i) {
                    final JSONObject locationEntryJson = locationHistoryJson.getJSONObject(i);
                    lhContentStrBuilder.append(String.valueOf(locationEntryJson.get("LATITUDE")));
                    lhContentStrBuilder.append(",");
                    lhContentStrBuilder.append(String.valueOf(locationEntryJson.get("LONGITUDE")));
                    lhContentStrBuilder.append(",");
                    lhContentStrBuilder.append((Utils.getEventTime(Long.valueOf(locationEntryJson.getLong("LOCATED_TIME"))) + "").replace(',', ' '));
                    lhContentStrBuilder.append(",");
                    lhContentStrBuilder.append((Object)locationEntryJson.getLong("LOCATED_TIME") + "");
                    if (addressDetails != null) {
                        lhContentStrBuilder.append(",");
                        final String address = addressDetails.optString(String.valueOf(locationEntryJson.getLong("LOCATION_DETAIL_ID")), (String)null);
                        if (!MDMStringUtils.isEmpty(address)) {
                            lhContentStrBuilder.append("\"");
                            lhContentStrBuilder.append(address);
                            lhContentStrBuilder.append("\"");
                        }
                    }
                    lhContentStrBuilder.append("\n");
                }
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while exportLocationHistoryData", ex);
        }
        return lhContentStrBuilder;
    }
    
    public JSONObject exportLocationHistoryDataWithAddress(final JSONObject data) throws Exception {
        final JSONObject responseStatusJson = new JSONObject();
        final Properties props = new Properties();
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "LoctionExportTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        ((Hashtable<String, JSONObject>)props).put("REQUEST_DATA", data);
        ((Hashtable<String, String>)props).put("OPERATION", "sendBatchReq");
        ((Hashtable<String, Long>)props).put("userID", data.optLong("USER_ID"));
        final JSONObject statusJSON = new JSONObject().put("EXPORT_REQ_ID", (Object)JSONUtil.optLongForUVH(data, "EXPORT_REQ_ID", (Long)null)).put("STATUS", 102).put("REMARKS", (Object)"mdm.inv.loc_export_started");
        ExportRequestDetailsHandler.getInstance().addOrUpdateExportRequestDetails(statusJSON);
        ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(statusJSON);
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.location.LocationExportTask", taskInfoMap, props);
        responseStatusJson.put("STATUS", (Object)"Success");
        responseStatusJson.put("REMARKS", (Object)I18N.getMsg("mdm.inv.loc_export_with_address_req_submitted", new Object[0]));
        return responseStatusJson;
    }
    
    public JSONArray getLocationHistoryDataForDevice(final Long resourceId, final JSONObject data) throws Exception {
        DMDataSetWrapper dataSet = null;
        JSONArray locationHistoryJSON = null;
        try {
            final Integer exportType = Integer.parseInt(data.optString("EXPORT_HISTORY_TYPE", "-1"));
            locationHistoryJSON = new JSONArray();
            final Criteria durationCriteria = this.getLocationHistoryExportCriteria(exportType, data);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0);
            Criteria criteria = (durationCriteria != null) ? resourceCriteria.and(durationCriteria) : resourceCriteria;
            if (data.has("REQUESTED_TIME")) {
                criteria = criteria.and(new Criteria(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), (Object)data.getLong("REQUESTED_TIME"), 6));
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.setCriteria(criteria);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (boolean)Boolean.TRUE);
            selectQuery.addSortColumn(sortColumn);
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final JSONObject locationEntryJson = new JSONObject();
                locationEntryJson.put("LATITUDE", (Object)dataSet.getValue("LATITUDE"));
                locationEntryJson.put("LONGITUDE", (Object)dataSet.getValue("LONGITUDE"));
                locationEntryJson.put("LOCATED_TIME", (Object)dataSet.getValue("LOCATED_TIME"));
                locationEntryJson.put("LOCATION_DETAIL_ID", (Object)dataSet.getValue("LOCATION_DETAIL_ID"));
                locationEntryJson.put("ADDED_TIME", (Object)dataSet.getValue("ADDED_TIME"));
                locationHistoryJSON.put((Object)locationEntryJson);
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while fetching LocationHistoryDataForDevice", ex);
            throw ex;
        }
        return locationHistoryJSON;
    }
    
    public Boolean isLocationDataAvailableForFilter(final JSONObject data) throws Exception {
        Boolean retVal = Boolean.FALSE;
        final Long resourceId = JSONUtil.optLongForUVH(data, "RESOURCE_ID", Long.valueOf(-1L));
        final Integer exportType = data.getInt("EXPORT_HISTORY_TYPE");
        final Criteria durationCriteria = this.getLocationHistoryExportCriteria(exportType, data);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)resourceId, 0);
        Criteria criteria = (durationCriteria != null) ? resourceCriteria.and(durationCriteria) : resourceCriteria;
        if (data.has("REQUESTED_TIME")) {
            criteria = criteria.and(new Criteria(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), (Object)JSONUtil.optLongForUVH(data, "REQUESTED_TIME", Long.valueOf(-1L)), 6));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
        final Column mdDeviceLocationDetailsCountCol = Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID").count();
        mdDeviceLocationDetailsCountCol.setColumnAlias("LocationDetailsCount");
        selectQuery.addSelectColumn(mdDeviceLocationDetailsCountCol);
        selectQuery.setCriteria(criteria);
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            final Integer count = (Integer)dmDataSetWrapper.getValue("LocationDetailsCount");
            if (count != null) {
                retVal = (count > 0);
            }
        }
        return retVal;
    }
    
    public StringBuilder exportAllDeviceLocationData(final Long customerId) {
        final StringBuilder lhContentStrBuilder = new StringBuilder();
        try {
            final JSONObject resLocJSON = MDMGeoLocationHandler.getInstance().getLocationResourceJSON(customerId, null, null);
            final JSONArray array = resLocJSON.getJSONArray("DEVICE");
            lhContentStrBuilder.append(I18N.getMsg("dc.mdm.enroll.device_name", new Object[0]));
            lhContentStrBuilder.append(",");
            lhContentStrBuilder.append("Latitude");
            lhContentStrBuilder.append(",");
            lhContentStrBuilder.append("Longitude");
            lhContentStrBuilder.append(",");
            lhContentStrBuilder.append(I18N.getMsg("dc.mdm.dc.mdm.geoLoc.located.on", new Object[0]));
            lhContentStrBuilder.append(",");
            lhContentStrBuilder.append(I18N.getMsg("dc.mdm.dc.mdm.geoLoc.location.milli", new Object[0]));
            lhContentStrBuilder.append("\n");
            for (int i = 0; i < array.length(); ++i) {
                lhContentStrBuilder.append(String.valueOf(array.getJSONObject(i).get("NAME")));
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append(array.getJSONObject(i).get("LATITUDE"));
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append(array.getJSONObject(i).get("LONGITUDE"));
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append(String.valueOf(array.getJSONObject(i).get("ADDED_TIME_STR")).replace(',', ' '));
                lhContentStrBuilder.append(",");
                lhContentStrBuilder.append(array.getJSONObject(i).get("ADDED_TIME"));
                lhContentStrBuilder.append("\n");
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while exportLocationHistoryData", ex);
        }
        return lhContentStrBuilder;
    }
    
    private Criteria getLocationHistoryExportCriteria(final int exportType, final JSONObject data) {
        Criteria criteria = null;
        switch (exportType) {
            case 2: {
                final String selectedDayStr = data.optString("SELECTED_FROM", "-1l");
                final Date curDay = CalendarUtil.getInstance().getStartTimeOfTheDay(Long.valueOf(selectedDayStr));
                final Long startTime = curDay.getTime();
                final Long endTime = CalendarUtil.getInstance().getStartTimeOfTheDay(CalendarUtil.getInstance().addDays(curDay, 1).getTime()).getTime();
                criteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)new Long[] { startTime, endTime }, 14);
                break;
            }
            case 3: {
                final Long sTime = Long.valueOf(data.optString("SELECTED_FROM", "-1l"));
                final Long eTime = CalendarUtil.getInstance().getEndTimeOfTheDay(Long.valueOf(data.optString("SELECTED_TO", "-1l"))).getTime();
                criteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)new Long[] { sTime, eTime }, 14);
                break;
            }
        }
        return criteria;
    }
    
    private String getMapsBaseUrl() {
        if (LocationDataHandler.mapsBaseUrl == null) {
            LocationDataHandler.mapsBaseUrl = LocationSettingsUtil.getLocationSettingsProperty("maps.url");
        }
        return LocationDataHandler.mapsBaseUrl;
    }
    
    private JSONObject limitGeocodingDataPrecision(final JSONObject geoData) throws Exception {
        final int precisionToMark = 5;
        String lat = geoData.optString("Latitude", (String)null);
        String lon = geoData.optString("Longitude", (String)null);
        final int latDotIndex = lat.indexOf(".");
        final int latLen = lat.length();
        if (latDotIndex != -1 && latLen - latDotIndex > precisionToMark) {
            lat = lat.substring(0, latDotIndex + precisionToMark);
        }
        final int lonDotIndex = lon.indexOf(".");
        final int lonLen = lon.length();
        if (lonDotIndex != -1 && lonLen - lonDotIndex > precisionToMark) {
            lon = lon.substring(0, lonDotIndex + precisionToMark);
        }
        geoData.put("Latitude", (Object)lat);
        geoData.put("Longitude", (Object)lon);
        return geoData;
    }
    
    public JSONObject sendBatchResultReqToZMaps(final String batchId) throws Exception {
        LocationDataHandler.logger.log(Level.INFO, "Going to send batchResult query to ZohoMaps for BatchId {0}", new Object[] { batchId });
        final String batchQueryResultAPI = "/v1/batch_result/";
        final DMHttpRequest httpRequest = new DMHttpRequest();
        httpRequest.method = "GET";
        httpRequest.url = this.getMapsBaseUrl() + batchQueryResultAPI + batchId;
        final DMHttpClient httpClient = new DMHttpClient();
        final DMHttpResponse httpResponse = httpClient.execute(httpRequest);
        LocationDataHandler.logger.log(Level.INFO, "Response status from Zoho Maps batch result query API - {0}", new Object[] { httpResponse.status });
        final JSONObject responseJson = new JSONObject();
        responseJson.put("Status", httpResponse.status);
        responseJson.put("MsgResponseType", (Object)"BatchResultApiResponse");
        if (!MDMStringUtils.isEmpty(httpResponse.responseBodyAsString)) {
            if (httpResponse.responseBodyAsString.startsWith("[")) {
                responseJson.put("MsgResponse", (Object)new JSONArray(httpResponse.responseBodyAsString));
            }
            else if (httpResponse.responseBodyAsString.startsWith("{")) {
                responseJson.put("MsgResponse", (Object)new JSONObject(httpResponse.responseBodyAsString));
            }
            else {
                responseJson.put("MsgResponse", (Object)httpResponse.responseBodyAsString);
            }
        }
        return responseJson;
    }
    
    public JSONObject sendBatchReqToZMapsForRGeocode(final JSONArray latLongArray) throws Exception {
        final String callbackUrl = null;
        return this.sendBatchReqToZMapsForRGeocode(latLongArray, callbackUrl);
    }
    
    private String sendGet(final String url) {
        String resp = "{}";
        try {
            final URL obj = new URL(url);
            final HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            final int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                final StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                resp = response.toString();
            }
            else {
                Logger.getLogger(LocationDataHandler.class.getName()).log(Level.WARNING, "Http request Response: {0}", responseCode);
            }
        }
        catch (final IOException ex) {
            Logger.getLogger(LocationDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }
    
    private String getGeoAddressFromZoho(final String latitude, final String longitude) {
        String address = null;
        final String reverseGeoCodeAPI = "/v1/query/rgeocode";
        try {
            final String params = "{\"lat\":" + latitude + ",\"lon\":" + longitude + "}";
            final JSONObject headerJson = new JSONObject();
            headerJson.put("Content-Type", (Object)"application/json");
            final JSONObject paramsJson = new JSONObject();
            paramsJson.put("api_key", (Object)"undefined");
            paramsJson.put("params", (Object)params);
            final DMHttpRequest httpRequest = new DMHttpRequest();
            httpRequest.method = "GET";
            httpRequest.url = this.getMapsBaseUrl() + "/v1/query/rgeocode";
            httpRequest.parameters = paramsJson;
            final DMHttpClient httpClient = new DMHttpClient();
            final DMHttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.status == 200 && !MDMStringUtils.isEmpty(httpResponse.responseBodyAsString)) {
                address = httpResponse.responseBodyAsString;
            }
        }
        catch (final Exception ex) {
            LocationDataHandler.logger.log(Level.WARNING, "Exception occurred while getGeoAddressFromZoho", ex);
        }
        return address;
    }
    
    public JSONObject sendBatchReqToZMapsForRGeocode(final JSONArray latLongArray, final String callbackUrl) throws Exception {
        LocationDataHandler.logger.log(Level.INFO, "Going to send batchRequest to ZohoMaps for ReverseGeocoding");
        final String asyncBatchRGeocodeAPI = "/v1/batch/rgeocode";
        final String mapAPIKey = MDMUtil.getSyMParameter("ZOHO_MAPS_API_KEY");
        final JSONObject headerJson = new JSONObject();
        headerJson.put("Content-Type", (Object)"application/json");
        final JSONObject paramsJson = new JSONObject();
        paramsJson.put("api_key", (Object)mapAPIKey);
        if (!MDMStringUtils.isEmpty(callbackUrl)) {
            paramsJson.put("callback", (Object)callbackUrl);
        }
        final DMHttpRequest httpRequest = new DMHttpRequest();
        httpRequest.method = "POST";
        httpRequest.headers = headerJson;
        httpRequest.parameters = paramsJson;
        httpRequest.url = this.getMapsBaseUrl() + asyncBatchRGeocodeAPI;
        httpRequest.data = latLongArray.toString().getBytes();
        final DMHttpClient httpClient = new DMHttpClient();
        final DMHttpResponse httpResponse = httpClient.execute(httpRequest);
        LocationDataHandler.logger.log(Level.INFO, "Response status from Zoho Maps rGeocode API - {0} and body content {1}", new Object[] { httpResponse.status, httpResponse.responseBodyAsString });
        final JSONObject responseJson = new JSONObject();
        responseJson.put("Status", httpResponse.status);
        responseJson.put("MsgResponseType", (Object)"AsyncRGeocodeResponse");
        if (!MDMStringUtils.isEmpty(httpResponse.responseBodyAsString)) {
            if (httpResponse.responseBodyAsString.startsWith("{")) {
                responseJson.put("MsgResponse", (Object)new JSONObject(httpResponse.responseBodyAsString));
            }
            else {
                responseJson.put("MsgResponse", (Object)httpResponse.responseBodyAsString);
            }
        }
        return responseJson;
    }
    
    public static LocationDataHandler getInstance() {
        return (LocationDataHandler.locationDataHandler == null) ? (LocationDataHandler.locationDataHandler = new LocationDataHandler()) : LocationDataHandler.locationDataHandler;
    }
    
    private String limitGeocodingData(String cord) {
        if (cord != null) {
            final int precisionToMark = 5;
            final int dotIndex = cord.indexOf(".");
            final int len = cord.length();
            if (dotIndex != -1 && len - dotIndex > precisionToMark) {
                cord = cord.substring(0, dotIndex + precisionToMark);
            }
        }
        return cord;
    }
    
    public JSONObject getLocationDataWithAddressStatus(final JSONObject requestJson) throws Exception {
        final Long resourceId = requestJson.getLong("RESOURCE_ID");
        final Long technicianId = requestJson.getLong("USER_ID");
        final Long customerId = requestJson.getLong("CUSTOMER_ID");
        final JSONObject responseJson = new JSONObject();
        if (resourceId != null && technicianId != null) {
            final JSONObject reqJson = new JSONObject().put("RESOURCE_ID", (Object)resourceId).put("USER_ID", (Object)technicianId).put("CUSTOMER_ID", (Object)customerId).put("isApiRequest", (Object)Boolean.FALSE);
            final JSONObject respJson = ExportRequestDetailsHandler.getInstance().getLocationExportRequestDetails(reqJson);
            final Integer status = respJson.getInt("status");
            responseJson.put("status", (Object)status);
            if (status.equals(200) || status.equals(500)) {
                final Long exportReqId = respJson.getLong("EXPORT_REQ_ID");
                String paramValue = MDMUtil.getUserParameter(technicianId, ExportRequestDetailsHandler.locExportUserParamPrefix + exportReqId);
                if (MDMStringUtils.isEmpty(paramValue)) {
                    paramValue = Boolean.FALSE.toString();
                }
                responseJson.put("isAlreadyShown", (Object)Boolean.valueOf(paramValue));
                if (status.equals(500)) {
                    responseJson.put("internalStatus", (Object)String.valueOf(respJson.get("internalStatus")));
                    responseJson.put("errorKey", (Object)String.valueOf(respJson.get("errorKey")));
                    responseJson.put("error", (Object)String.valueOf(respJson.get("error")));
                }
                if (!Boolean.valueOf(paramValue)) {
                    MDMUtil.updateUserParameter(technicianId, ExportRequestDetailsHandler.locExportUserParamPrefix + exportReqId, Boolean.TRUE.toString());
                }
            }
        }
        else {
            responseJson.put("status", 400);
        }
        return JSONUtil.getInstance().convertLongToString(responseJson);
    }
    
    static {
        logger = Logger.getLogger("MDMLogger");
        LocationDataHandler.locationDataHandler = null;
        LocationDataHandler.mapsBaseUrl = null;
    }
}
