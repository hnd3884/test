package com.me.mdm.server.location;

import java.util.Hashtable;
import java.util.HashMap;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.mdm.server.export.ExportRequestDetailsHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class LocationExportTask implements SchedulerExecutionInterface
{
    Logger logger;
    
    public LocationExportTask() {
        this.logger = Logger.getLogger("MDMLocationLogger");
    }
    
    public void executeTask(final Properties props) {
        try {
            final String operationType = props.getProperty("OPERATION");
            final Object requestDataObj = ((Hashtable<K, Object>)props).get("REQUEST_DATA");
            JSONObject data = null;
            if (requestDataObj instanceof JSONObject) {
                data = (JSONObject)requestDataObj;
            }
            else {
                data = new JSONObject((String)requestDataObj);
            }
            final Long exportReqId = data.getLong("EXPORT_REQ_ID");
            final Long customerId = data.getLong("CUSTOMER_ID");
            final Long resourceId = Long.valueOf(String.valueOf(data.get("RESOURCE_ID")));
            final String deviceName = String.valueOf(data.get("ManagedDeviceExtn.NAME"));
            final String userName = String.valueOf(data.get("FIRST_NAME"));
            final Boolean isApiRequest = data.optBoolean("isApiRequest", (boolean)Boolean.FALSE);
            final String evtLogRemarksFailureKey = "mdm.inv.evtlog.loc_export_with_address_failed";
            final String evtLogRemarksSuccessKey = "mdm.inv.evtlog.loc_export_with_address_success";
            DMSecurityLogger.info(this.logger, "LocationExportTask", "executeTask", "Inside LocationExportTask with operationType {0} and requestData {1}", (Object)new Object[] { operationType, data });
            if (!props.containsKey("CURRENT_ATTEMPT_COUNT")) {
                final JSONObject statusJSON = new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 103).put("REMARKS", (Object)("mdm.inv.loc_export_inprogress@@@" + deviceName));
                ExportRequestDetailsHandler.getInstance().addOrUpdateExportRequestDetails(statusJSON);
            }
            if (operationType.equalsIgnoreCase("sendBatchReq")) {
                final JSONArray locationHistoryJson = new LocationDataHandler().getLocationHistoryDataForDevice(resourceId, data);
                final JSONArray zMapsRGeocodeQueryBody = new JSONArray();
                for (int i = 0; i < locationHistoryJson.length(); ++i) {
                    final JSONObject locationJson = locationHistoryJson.getJSONObject(i);
                    final JSONObject zMapsRGeocode = new JSONObject();
                    zMapsRGeocode.put("id", locationJson.getLong("LOCATION_DETAIL_ID"));
                    zMapsRGeocode.put("lat", (Object)String.valueOf(locationJson.get("LATITUDE")));
                    zMapsRGeocode.put("lon", (Object)String.valueOf(locationJson.get("LONGITUDE")));
                    zMapsRGeocodeQueryBody.put((Object)zMapsRGeocode);
                }
                final JSONObject zMapsRGeocodeResponseJSON = new LocationDataHandler().sendBatchReqToZMapsForRGeocode(zMapsRGeocodeQueryBody);
                if (zMapsRGeocodeResponseJSON.getInt("Status") == 200 || zMapsRGeocodeResponseJSON.getInt("Status") / 100 == 2) {
                    final JSONObject statusJson = zMapsRGeocodeResponseJSON.getJSONObject("MsgResponse");
                    final Integer waitTimeInSecs = statusJson.getInt("wait");
                    final String batchId = String.valueOf(statusJson.get("bid"));
                    data.put("ZMAPS_BATCH_ID", (Object)batchId);
                    ((Hashtable<String, JSONObject>)props).put("REQUEST_DATA", data);
                    ((Hashtable<String, String>)props).put("CURRENT_ATTEMPT_COUNT", "0");
                    ((Hashtable<String, String>)props).put("OPERATION", "queryBatchReqResult");
                    this.logger.log(Level.INFO, "Successfully submitted reverse geocode api data to ZohoMaps");
                    ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("ZMAPS_BATCH_ID", (Object)batchId).put("STATUS", 502).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_req_success"));
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
                        ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 503).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_req_failed"));
                        ExportRequestDetailsHandler.getInstance().addOrUpdateExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 115));
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2161, resourceId, userName, evtLogRemarksFailureKey, deviceName, customerId);
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Failed to send reverseGeoCodeApiRequest to ZohoMaps. Rescheduling api submit request after 2 mins. Error received - {0}", zMapsRGeocodeResponseJSON);
                        ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 501).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_req_retry"));
                        ((Hashtable<String, String>)props).put("CURRENT_ATTEMPT_COUNT", String.valueOf(currentAttemptCount));
                        this.reScheduleTask(props, 120);
                    }
                }
            }
            else if (operationType.equalsIgnoreCase("queryBatchReqResult")) {
                final String batchId2 = String.valueOf(data.get("ZMAPS_BATCH_ID"));
                final JSONObject zMapsRGeocodeBatchResultJSON = new LocationDataHandler().sendBatchResultReqToZMaps(batchId2);
                if (zMapsRGeocodeBatchResultJSON.getInt("Status") == 200) {
                    final JSONArray rGeocodeData = zMapsRGeocodeBatchResultJSON.getJSONArray("MsgResponse");
                    this.logger.log(Level.INFO, "Obtained successful response for reverseGeoCode batch result query request from ZohoMaps for batchId {0}", batchId2);
                    final Long userId = Long.valueOf(((Hashtable<K, Long>)props).getOrDefault("userID", -1L).toString());
                    if (userId > 0L) {
                        final String[] loginDetails = ScheduleReportUtil.getInstance().getLoginName(userId);
                        ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(loginDetails[0], "system", loginDetails[1], userId);
                    }
                    else {
                        this.logger.log(Level.INFO, "User credential is not set in LocationExportTask params");
                    }
                    final Object locHistoryData = this.getLocationHistoryDataWithAddress(data, rGeocodeData);
                    String filePath = MDMMetaDataUtil.getInstance().getLocationHistoryExportFilePath(exportReqId, customerId);
                    byte[] locData;
                    if (!isApiRequest) {
                        final StringBuilder lhCSVData = (StringBuilder)locHistoryData;
                        filePath = filePath + File.separator + deviceName + "_LocationHistory.csv";
                        locData = lhCSVData.toString().getBytes();
                    }
                    else {
                        final JSONArray lhJsonData = (JSONArray)locHistoryData;
                        filePath = filePath + File.separator + "LocationHistory_APIResult.json";
                        locData = lhJsonData.toString().getBytes();
                    }
                    ApiFactoryProvider.getFileAccessAPI().writeFile(filePath, locData);
                    this.logger.log(Level.INFO, "Successfully saved locationExport details in filePath {0}", filePath);
                    ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 505).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_query_success"));
                    ExportRequestDetailsHandler.getInstance().addOrUpdateExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 104).put("EXPORT_FILE_LOC", (Object)filePath));
                    if (!isApiRequest) {
                        ExportRequestDetailsHandler.getInstance().sendLocExportMail(data.put("EXPORT_FILE_LOC", (Object)filePath).put("NAME", (Object)deviceName));
                        this.logger.log(Level.INFO, "Successfully scheduled locationExport details mail");
                    }
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2160, resourceId, userName, evtLogRemarksSuccessKey, deviceName, customerId);
                    ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("REMARKS", (Object)("mdm.inv.loc_export_success@@@" + deviceName)));
                    this.logger.log(Level.INFO, "Successfully completed location export with address request for export_req_id {0} for resourceId {1}", new Object[] { exportReqId, resourceId });
                }
                else if (zMapsRGeocodeBatchResultJSON.getInt("Status") == 204) {
                    Integer currentAttemptCount2 = Integer.parseInt(props.getProperty("CURRENT_ATTEMPT_COUNT"));
                    ++currentAttemptCount2;
                    if (currentAttemptCount2 >= 5) {
                        this.logger.log(Level.SEVERE, "Retry count of 5 exceeded. Aborting query API for reverseGeoCode batch result from ZohoMaps. Error - {0}", zMapsRGeocodeBatchResultJSON);
                        ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 506).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_query_failed"));
                        ExportRequestDetailsHandler.getInstance().addOrUpdateExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 115));
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2161, resourceId, userName, evtLogRemarksFailureKey, deviceName, customerId);
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Failed to send reverseGeoCodeApi batch query request to ZohoMaps. Rescheduling api submit request after 2 mins. Error received - {0}", zMapsRGeocodeBatchResultJSON);
                        ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 504).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_query_retry"));
                        ((Hashtable<String, String>)props).put("CURRENT_ATTEMPT_COUNT", String.valueOf(currentAttemptCount2));
                        this.reScheduleTask(props, 120);
                    }
                }
                else {
                    this.logger.log(Level.SEVERE, "Reverse geocode reult query response has status other than 200/204 - Response from ZMaps {0}", zMapsRGeocodeBatchResultJSON);
                    ExportRequestDetailsHandler.getInstance().addOrUpdateLocationExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 506).put("REMARKS", (Object)"mdm.inv.loc_export_rgeocode_query_failed"));
                    ExportRequestDetailsHandler.getInstance().addOrUpdateExportRequestDetails(new JSONObject().put("EXPORT_REQ_ID", (Object)exportReqId).put("STATUS", 115));
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2161, resourceId, userName, evtLogRemarksFailureKey, deviceName, customerId);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception in LocationExportTask for taskProps: " + properties.toString());
        }
        finally {
            ApiFactoryProvider.getAuthUtilAccessAPI().flushCredentials();
        }
    }
    
    private void reScheduleTask(final Properties props, final Integer waitTimeInSecs) {
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "LoctionExportTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis() + waitTimeInSecs * 1000);
        taskInfoMap.put("poolName", "asynchThreadPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.location.LocationExportTask", taskInfoMap, props);
    }
    
    private Object getLocationHistoryDataWithAddress(final JSONObject data, final JSONArray addressArr) throws Exception {
        Object retVal = null;
        final Boolean isApiRequest = data.optBoolean("isApiRequest", (boolean)Boolean.FALSE);
        final JSONObject idToAddrJson = new JSONObject();
        final Long resourceId = Long.valueOf(String.valueOf(data.get("RESOURCE_ID")));
        for (int i = 0; i < addressArr.length(); ++i) {
            final JSONObject rGeocodeJson = addressArr.getJSONObject(i);
            final String locationDetailIDStr = String.valueOf(rGeocodeJson.get("id"));
            final String displayName = String.valueOf(rGeocodeJson.getJSONObject("result").get("display_name"));
            idToAddrJson.put(locationDetailIDStr, (Object)displayName);
        }
        final LocationDataHandler locationDataHandler = new LocationDataHandler();
        if (!isApiRequest) {
            final StringBuilder lhData = (StringBuilder)(retVal = locationDataHandler.exportLocationHistoryData(resourceId, data, idToAddrJson));
        }
        else {
            final JSONArray locaDataArray = locationDataHandler.getLocationHistoryDataForDevice(resourceId, data);
            final JSONArray respJsonArray = new JSONArray();
            for (int j = 0; j < locaDataArray.length(); ++j) {
                final JSONObject locJson = locaDataArray.getJSONObject(j);
                final Long locDetailId = locJson.getLong("LOCATION_DETAIL_ID");
                final String displayName2 = idToAddrJson.optString(locDetailId.toString(), (String)null);
                locJson.remove("LOCATION_DETAIL_ID");
                if (displayName2 != null) {
                    locJson.put("address", (Object)displayName2);
                }
                respJsonArray.put((Object)locJson);
            }
            retVal = respJsonArray;
        }
        return retVal;
    }
}
