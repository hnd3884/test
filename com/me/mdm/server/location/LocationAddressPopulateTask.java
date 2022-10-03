package com.me.mdm.server.location;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class LocationAddressPopulateTask implements SchedulerExecutionInterface
{
    Logger logger;
    int batchSize;
    
    public LocationAddressPopulateTask() {
        this.logger = Logger.getLogger("MDMLocationLogger");
        this.batchSize = 5000;
    }
    
    public void executeTask(final Properties props) {
        try {
            final String operation = props.getProperty("OPERATION");
            if (operation.equalsIgnoreCase("sendBatchReq")) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
                selectQuery.addJoin(new Join("MdDeviceLocationDetails", "ManagedDevice", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
                selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
                selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
                selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), true));
                final Criteria addressCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_ADDRESS"), (Object)null, 0);
                final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                selectQuery.setCriteria(addressCri.and(managedCri));
                selectQuery.setRange(new Range(0, this.batchSize));
                final DataObject dataObject = DataAccess.get(selectQuery);
                if (dataObject.isEmpty()) {
                    MDMUtil.deleteSyMParameter("addLocationAddress");
                    return;
                }
                final Iterator rows = dataObject.getRows("MdDeviceLocationDetails");
                final JSONArray zMapsRGeocodeQueryBody = new JSONArray();
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final JSONObject zMapsRGeocode = new JSONObject();
                    zMapsRGeocode.put("id", row.get("LOCATION_DETAIL_ID"));
                    zMapsRGeocode.put("lat", (Object)String.valueOf(row.get("LATITUDE")));
                    zMapsRGeocode.put("lon", (Object)String.valueOf(row.get("LONGITUDE")));
                    zMapsRGeocodeQueryBody.put((Object)zMapsRGeocode);
                }
                final JSONObject zMapsRGeocodeResponseJSON = new LocationDataHandler().sendBatchReqToZMapsForRGeocode(zMapsRGeocodeQueryBody);
                if (zMapsRGeocodeResponseJSON.getInt("Status") == 200 || zMapsRGeocodeResponseJSON.getInt("Status") / 100 == 2) {
                    final JSONObject statusJson = zMapsRGeocodeResponseJSON.getJSONObject("MsgResponse");
                    final Integer waitTimeInSecs = statusJson.getInt("wait");
                    final String batchId = String.valueOf(statusJson.get("bid"));
                    ((Hashtable<String, String>)props).put("ZMAPS_BATCH_ID", batchId);
                    ((Hashtable<String, String>)props).put("OPERATION", "queryBatchReqResult");
                    this.logger.log(Level.INFO, "Successfully submitted reverse geocode api data to ZohoMaps");
                    this.reScheduleTask(props, waitTimeInSecs * 2);
                }
                else {
                    Integer currentAttemptCount = 0;
                    if (props.containsKey("CURRENT_ATTEMPT_COUNT")) {
                        currentAttemptCount = Integer.parseInt(props.getProperty("CURRENT_ATTEMPT_COUNT"));
                    }
                    ++currentAttemptCount;
                    if (currentAttemptCount >= 5) {
                        this.logger.log(Level.SEVERE, "Retry count of 5 exceeded. Aborting submission of reverseGeoCode API request to ZohoMaps. Error - {0}", zMapsRGeocodeResponseJSON);
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Failed to send reverseGeoCodeApiRequest to ZohoMaps. Rescheduling api submit request after 2 mins. Error received - {0}", zMapsRGeocodeResponseJSON);
                        ((Hashtable<String, String>)props).put("CURRENT_ATTEMPT_COUNT", String.valueOf(currentAttemptCount));
                        this.reScheduleTask(props, 120);
                    }
                }
            }
            if (operation.equalsIgnoreCase("queryBatchReqResult")) {
                final String batchId2 = props.getProperty("ZMAPS_BATCH_ID");
                final JSONObject zMapsRGeocodeBatchResultJSON = new LocationDataHandler().sendBatchResultReqToZMaps(batchId2);
                if (zMapsRGeocodeBatchResultJSON.getInt("Status") == 200) {
                    final JSONArray rGeocodeData = zMapsRGeocodeBatchResultJSON.getJSONArray("MsgResponse");
                    this.populateLocationAddress(rGeocodeData);
                    ((Hashtable<String, String>)props).replace("OPERATION", "sendBatchReq");
                    props.remove("ZMAPS_BATCH_ID");
                    this.reScheduleTask(props, 10);
                }
                else if (zMapsRGeocodeBatchResultJSON.getInt("Status") == 204) {
                    Integer currentAttemptCount2 = 0;
                    if (props.containsKey("CURRENT_ATTEMPT_COUNT")) {
                        currentAttemptCount2 = Integer.parseInt(props.getProperty("CURRENT_ATTEMPT_COUNT"));
                    }
                    ++currentAttemptCount2;
                    if (currentAttemptCount2 >= 5) {
                        this.logger.log(Level.SEVERE, "Retry count of 5 exceeded. Aborting query API for reverseGeoCode batch result from ZohoMaps. Error - {0}", zMapsRGeocodeBatchResultJSON);
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Failed to send reverseGeoCodeApi batch query request to ZohoMaps. Rescheduling api submit request after 2 mins. Error received - {0}", zMapsRGeocodeBatchResultJSON);
                        ((Hashtable<String, String>)props).put("CURRENT_ATTEMPT_COUNT", String.valueOf(currentAttemptCount2));
                        this.reScheduleTask(props, 120);
                    }
                }
                else {
                    this.logger.log(Level.SEVERE, "Reverse geocode reult query response has status other than 200/204 - Response from ZMaps {0}", zMapsRGeocodeBatchResultJSON);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in populating address", e);
        }
    }
    
    private void populateLocationAddress(final JSONArray rGeocodeData) throws Exception {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_ADDRESS"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_ADDRESS"), (Object)null, 0));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), true));
            selectQuery.setRange(new Range(0, this.batchSize));
            final DataObject dataObject = DataAccess.get(selectQuery);
            for (int i = 0; i < rGeocodeData.length(); ++i) {
                final JSONObject rGeocodeJson = rGeocodeData.getJSONObject(i);
                final String locationDetailIDStr = String.valueOf(rGeocodeJson.get("id"));
                String displayName = String.valueOf(rGeocodeJson.getJSONObject("result").optString("display_name", "--"));
                final Row row = dataObject.getRow("MdDeviceLocationDetails", new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)Long.valueOf(locationDetailIDStr), 0));
                if (row != null) {
                    if (displayName.length() > 1000) {
                        displayName = displayName.substring(0, 999);
                    }
                    row.set("LOCATION_ADDRESS", (Object)displayName);
                    dataObject.updateRow(row);
                }
            }
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in populating address", e);
            throw e;
        }
    }
    
    private void reScheduleTask(final Properties props, final Integer waitTimeInSecs) {
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "AddLocationAddress");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis() + waitTimeInSecs * 1000);
        taskInfoMap.put("poolName", "asynchThreadPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.location.LocationAddressPopulateTask", taskInfoMap, props);
    }
}
