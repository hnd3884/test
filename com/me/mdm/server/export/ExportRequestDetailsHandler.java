package com.me.mdm.server.export;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ExportRequestDetailsHandler
{
    private static ExportRequestDetailsHandler exportRequestDetailsHandler;
    public static String locExportUserParamPrefix;
    public static Logger logger;
    
    public static ExportRequestDetailsHandler getInstance() {
        if (ExportRequestDetailsHandler.exportRequestDetailsHandler == null) {
            ExportRequestDetailsHandler.exportRequestDetailsHandler = new ExportRequestDetailsHandler();
        }
        return ExportRequestDetailsHandler.exportRequestDetailsHandler;
    }
    
    private DataObject getLocationHistoryExportRequestDO(final JSONObject requestJSON) throws Exception {
        final Long resourceID = requestJSON.getLong("RESOURCE_ID");
        final Long userID = requestJSON.getLong("USER_ID");
        final Long exportReqId = requestJSON.optLong("EXPORT_REQ_ID", -1L);
        final SelectQuery locationExportReqQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LocationExportRequestDetails"));
        locationExportReqQuery.addJoin(new Join("LocationExportRequestDetails", "ExportRequestDetails", new String[] { "EXPORT_REQ_ID" }, new String[] { "EXPORT_REQ_ID" }, 2));
        locationExportReqQuery.addJoin(new Join("ExportRequestDetails", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        locationExportReqQuery.addSelectColumn(Column.getColumn("LocationExportRequestDetails", "EXPORT_REQ_ID"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("LocationExportRequestDetails", "STATUS"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("LocationExportRequestDetails", "REMARKS"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("ExportRequestDetails", "EXPORT_REQ_ID"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("ExportRequestDetails", "STATUS"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("ExportRequestDetails", "USER_ID"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("ExportRequestDetails", "EXPORT_FILE_LOC"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        locationExportReqQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        final Column resourceIdCol = Column.getColumn("LocationExportRequestDetails", "RESOURCE_ID");
        locationExportReqQuery.addSelectColumn(resourceIdCol);
        final Criteria resourceCriteria = new Criteria(resourceIdCol, (Object)resourceID, 0);
        final Criteria userIdCriteria = new Criteria(Column.getColumn("ExportRequestDetails", "USER_ID"), (Object)userID, 0);
        Criteria finalCriteria = resourceCriteria.and(userIdCriteria);
        if (exportReqId != -1L) {
            final Criteria exportReqCriteria = new Criteria(Column.getColumn("ExportRequestDetails", "EXPORT_REQ_ID"), (Object)exportReqId, 0);
            finalCriteria = finalCriteria.and(exportReqCriteria);
        }
        locationExportReqQuery.setCriteria(finalCriteria);
        final DataObject locationExportReqDO = DataAccess.get(locationExportReqQuery);
        return locationExportReqDO;
    }
    
    public Boolean isLocationExportWithAddressAlreadyInProgressForResourceUser(final JSONObject requestJSON) throws Exception {
        Boolean retVal = Boolean.FALSE;
        final DataObject dao = this.getLocationHistoryExportRequestDO(requestJSON);
        if (!dao.isEmpty()) {
            final Row exportReqDetailsRowInProgress = dao.getRow("ExportRequestDetails", new Criteria(Column.getColumn("ExportRequestDetails", "STATUS"), (Object)new Integer[] { 102, 103, 101 }, 8));
            if (exportReqDetailsRowInProgress != null) {
                retVal = Boolean.TRUE;
            }
        }
        return retVal;
    }
    
    public JSONObject addNewEntryForLocationExportRequest(final JSONObject requestJSON) throws Exception {
        final Long userID = requestJSON.getLong("USER_ID");
        final Long customerID = requestJSON.getLong("CUSTOMER_ID");
        final Long resourceID = requestJSON.getLong("RESOURCE_ID");
        String emailAddressCSV = null;
        if (!requestJSON.optBoolean("isApiRequest", (boolean)Boolean.FALSE)) {
            emailAddressCSV = String.valueOf(requestJSON.get("EMAIL_ADDRESS"));
        }
        final String locExportFilterData = requestJSON.optString("LOC_EXPORT_FILTER_DATA", (String)null);
        final DataObject locationExportReqDO = this.getLocationHistoryExportRequestDO(requestJSON);
        Long exportReqIdInProgress = null;
        Long oldReqUserId = null;
        Long exportReqIdCompleted = null;
        if (locationExportReqDO != null) {
            final Row exportReqDetailsRowInProgress = locationExportReqDO.getRow("ExportRequestDetails", new Criteria(Column.getColumn("ExportRequestDetails", "STATUS"), (Object)new Integer[] { 102, 103, 101 }, 8));
            final Row exportReqDetailsRowSuccessFail = locationExportReqDO.getRow("ExportRequestDetails", new Criteria(Column.getColumn("ExportRequestDetails", "STATUS"), (Object)new Integer[] { 102, 103, 101 }, 9));
            if (exportReqDetailsRowInProgress != null) {
                exportReqIdInProgress = (Long)exportReqDetailsRowInProgress.get("EXPORT_REQ_ID");
                oldReqUserId = (Long)exportReqDetailsRowInProgress.get("USER_ID");
            }
            if (exportReqDetailsRowSuccessFail != null) {
                exportReqIdCompleted = (Long)exportReqDetailsRowSuccessFail.get("EXPORT_REQ_ID");
            }
        }
        final JSONObject exportDetailsJSON = new JSONObject();
        if (exportReqIdInProgress == null) {
            exportDetailsJSON.put("USER_ID", (Object)userID);
            exportDetailsJSON.put("CUSTOMER_ID", (Object)customerID);
            exportDetailsJSON.put("STATUS", 101);
            exportDetailsJSON.put("REMARKS", (Object)"mdm.inv.loc_export_scheduled");
            exportDetailsJSON.put("EXPORT_TYPE", 1);
            if (exportReqIdCompleted != null) {
                exportDetailsJSON.put("EXPORT_REQ_ID", (Object)exportReqIdCompleted);
                exportDetailsJSON.put("considerAsNewRequest", (Object)Boolean.TRUE);
                MDMUtil.deleteUserParameter(ExportRequestDetailsHandler.locExportUserParamPrefix + exportReqIdCompleted);
            }
            exportReqIdCompleted = this.addOrUpdateExportRequestDetails(exportDetailsJSON);
            exportDetailsJSON.put("EXPORT_REQ_ID", (Object)exportReqIdCompleted);
            if (emailAddressCSV != null) {
                exportDetailsJSON.put("EMAIL_ADDRESS", (Object)emailAddressCSV);
            }
            exportDetailsJSON.put("RESOURCE_ID", (Object)resourceID);
            if (locExportFilterData != null) {
                exportDetailsJSON.put("LOC_EXPORT_FILTER_DATA", (Object)locExportFilterData);
            }
            exportReqIdCompleted = this.addOrUpdateLocationExportRequestDetails(exportDetailsJSON);
            exportDetailsJSON.put("EXPORT_REQ_ID", (Object)exportReqIdCompleted);
            exportDetailsJSON.put("STATUS", (Object)"Success");
        }
        else {
            final Row aaaUserRow = locationExportReqDO.getRow("AaaUser", new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)oldReqUserId, 0));
            exportDetailsJSON.put("STATUS", (Object)"Failed");
            exportDetailsJSON.put("REMARKS", (Object)I18N.getMsg("mdm.inv.loc_export_in_progress_for_resource", new Object[] { aaaUserRow.get("FIRST_NAME") }));
            exportDetailsJSON.put("USER_NAME", aaaUserRow.get("FIRST_NAME"));
            exportDetailsJSON.put("EXPORT_REQ_ID", (Object)exportReqIdInProgress);
        }
        return exportDetailsJSON;
    }
    
    public Long addOrUpdateExportRequestDetails(final JSONObject requestJSON) throws Exception {
        final Long exportRequestId = requestJSON.optLong("EXPORT_REQ_ID", -1L);
        Row exportDetailsRow = null;
        DataObject exportDetailsDO = null;
        if (exportRequestId != -1L) {
            exportDetailsDO = MDMUtil.getPersistenceLite().get("ExportRequestDetails", new Criteria(Column.getColumn("ExportRequestDetails", "EXPORT_REQ_ID"), (Object)exportRequestId, 0));
            exportDetailsRow = exportDetailsDO.getFirstRow("ExportRequestDetails");
        }
        if (exportDetailsDO == null) {
            exportDetailsDO = (DataObject)new WritableDataObject();
        }
        if (exportDetailsRow == null) {
            exportDetailsRow = new Row("ExportRequestDetails");
            exportDetailsRow.set("CUSTOMER_ID", (Object)requestJSON.getLong("CUSTOMER_ID"));
            exportDetailsRow.set("USER_ID", (Object)requestJSON.getLong("USER_ID"));
            exportDetailsRow.set("EXPORT_TYPE", (Object)requestJSON.getInt("EXPORT_TYPE"));
            final Long exportRequestTime = MDMUtil.getCurrentTimeInMillis();
            requestJSON.put("REQUESTED_TIME", (Object)exportRequestTime);
            exportDetailsRow.set("REQUESTED_TIME", (Object)requestJSON.optLong("REQUESTED_TIME", (long)exportRequestTime));
            exportDetailsRow.set("UPDATED_TIME", (Object)requestJSON.optLong("REQUESTED_TIME", (long)exportRequestTime));
            exportDetailsRow.set("STATUS", (Object)requestJSON.getInt("STATUS"));
            if (requestJSON.has("EXPORT_FILE_LOC")) {
                exportDetailsRow.set("EXPORT_FILE_LOC", (Object)String.valueOf(requestJSON.get("EXPORT_FILE_LOC")));
            }
            exportDetailsDO.addRow(exportDetailsRow);
        }
        else {
            if (requestJSON.has("EXPORT_FILE_LOC")) {
                exportDetailsRow.set("EXPORT_FILE_LOC", (Object)String.valueOf(requestJSON.get("EXPORT_FILE_LOC")));
            }
            if (requestJSON.has("STATUS")) {
                exportDetailsRow.set("STATUS", (Object)requestJSON.getInt("STATUS"));
            }
            final Long exportRequestUpdatedTime = MDMUtil.getCurrentTimeInMillis();
            if (requestJSON.optBoolean("considerAsNewRequest", (boolean)Boolean.FALSE)) {
                exportDetailsRow.set("REQUESTED_TIME", (Object)exportRequestUpdatedTime);
                exportDetailsRow.set("EXPORT_FILE_LOC", (Object)null);
                requestJSON.put("REQUESTED_TIME", (Object)exportRequestUpdatedTime);
            }
            requestJSON.put("UPDATED_TIME", (Object)exportRequestUpdatedTime);
            exportDetailsRow.set("UPDATED_TIME", (Object)requestJSON.optLong("UPDATED_TIME", (long)exportRequestUpdatedTime));
            exportDetailsDO.updateRow(exportDetailsRow);
        }
        exportDetailsDO = MDMUtil.getPersistence().update(exportDetailsDO);
        exportDetailsRow = exportDetailsDO.getFirstRow("ExportRequestDetails");
        return (Long)exportDetailsRow.get("EXPORT_REQ_ID");
    }
    
    public Long addOrUpdateLocationExportRequestDetails(final JSONObject requestJSON) throws Exception {
        final Long exportReqId = requestJSON.getLong("EXPORT_REQ_ID");
        final String zmapsBatchId = requestJSON.optString("ZMAPS_BATCH_ID", (String)null);
        final String locExportFilterData = requestJSON.optString("LOC_EXPORT_FILTER_DATA", (String)null);
        final String emailAddressCSV = requestJSON.optString("EMAIL_ADDRESS", (String)null);
        final Integer status = requestJSON.optInt("STATUS", -1);
        final Long resourceID = requestJSON.optLong("RESOURCE_ID");
        final String remarks = requestJSON.optString("REMARKS", (String)null);
        Boolean isNewRow = Boolean.FALSE;
        DataObject locExportReqDetailsDO = MDMUtil.getPersistenceLite().get("LocationExportRequestDetails", new Criteria(Column.getColumn("LocationExportRequestDetails", "EXPORT_REQ_ID"), (Object)exportReqId, 0));
        Row exportDetailsRow = null;
        if (!locExportReqDetailsDO.isEmpty()) {
            exportDetailsRow = locExportReqDetailsDO.getFirstRow("LocationExportRequestDetails");
        }
        if (exportDetailsRow == null) {
            exportDetailsRow = new Row("LocationExportRequestDetails");
            exportDetailsRow.set("EXPORT_REQ_ID", (Object)exportReqId);
            exportDetailsRow.set("RESOURCE_ID", (Object)resourceID);
            isNewRow = Boolean.TRUE;
        }
        if (requestJSON.optBoolean("considerAsNewRequest", (boolean)Boolean.FALSE)) {
            exportDetailsRow.set("ZMAPS_BATCH_ID", (Object)null);
            exportDetailsRow.set("LOC_EXPORT_FILTER_DATA", (Object)null);
            exportDetailsRow.set("EMAIL_ADDRESS", (Object)null);
        }
        if (!MDMStringUtils.isEmpty(zmapsBatchId)) {
            exportDetailsRow.set("ZMAPS_BATCH_ID", (Object)zmapsBatchId);
        }
        if (!MDMStringUtils.isEmpty(locExportFilterData)) {
            exportDetailsRow.set("LOC_EXPORT_FILTER_DATA", (Object)locExportFilterData);
        }
        if (!MDMStringUtils.isEmpty(emailAddressCSV)) {
            exportDetailsRow.set("EMAIL_ADDRESS", (Object)emailAddressCSV);
        }
        if (status != -1) {
            exportDetailsRow.set("STATUS", (Object)status);
        }
        if (!MDMStringUtils.isEmpty(remarks)) {
            exportDetailsRow.set("REMARKS", (Object)remarks);
        }
        if (isNewRow) {
            locExportReqDetailsDO.addRow(exportDetailsRow);
        }
        else {
            locExportReqDetailsDO.updateRow(exportDetailsRow);
        }
        locExportReqDetailsDO = MDMUtil.getPersistence().update(locExportReqDetailsDO);
        if (isNewRow) {
            exportDetailsRow = locExportReqDetailsDO.getFirstRow("LocationExportRequestDetails");
        }
        return (Long)exportDetailsRow.get("EXPORT_REQ_ID");
    }
    
    public void sendLocExportMail(final JSONObject requestJson) throws Exception {
        MDMMailNotificationHandler.getInstance().sendLocationExportMail(requestJson);
    }
    
    public JSONObject getLocationExportRequestDetails(final JSONObject exportRequestDetails) throws Exception {
        Long exportRequestId = exportRequestDetails.optLong("EXPORT_REQ_ID", -1L);
        final Long customerId = exportRequestDetails.getLong("CUSTOMER_ID");
        final Long userId = exportRequestDetails.getLong("USER_ID");
        final Long resourceId = exportRequestDetails.getLong("RESOURCE_ID");
        final DataObject dao = this.getLocationHistoryExportRequestDO(exportRequestDetails);
        final JSONObject responseJson = new JSONObject();
        if (!dao.isEmpty()) {
            Criteria exportReqCriteria = null;
            if (exportRequestId.equals(-1L)) {
                exportReqCriteria = new Criteria(Column.getColumn("ExportRequestDetails", "USER_ID"), (Object)userId, 0);
            }
            else {
                exportReqCriteria = new Criteria(Column.getColumn("ExportRequestDetails", "EXPORT_REQ_ID"), (Object)exportRequestId, 0);
            }
            final Row exportRequestDetailsRow = dao.getRow("ExportRequestDetails", exportReqCriteria);
            if (exportRequestDetailsRow != null) {
                final Integer exportReqStatus = (Integer)exportRequestDetailsRow.get("STATUS");
                exportRequestId = (Long)exportRequestDetailsRow.get("EXPORT_REQ_ID");
                if (!exportRequestDetails.optBoolean("isApiRequest", (boolean)Boolean.TRUE)) {
                    responseJson.put("EXPORT_REQ_ID", (Object)exportRequestId);
                }
                if (exportReqStatus.equals(104)) {
                    if (exportRequestDetails.optBoolean("isApiRequest", (boolean)Boolean.TRUE)) {
                        String filePath = MDMMetaDataUtil.getInstance().getLocationHistoryExportFilePath(exportRequestId, customerId);
                        filePath = filePath + File.separator + "LocationHistory_APIResult.json";
                        final String locationJsonString = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(filePath);
                        final JSONArray locationJsonArray = new JSONArray(locationJsonString);
                        responseJson.put("locations", (Object)locationJsonArray);
                    }
                    responseJson.put("status", 200);
                }
                else if (exportReqStatus.equals(101) || exportReqStatus.equals(102) || exportReqStatus.equals(103)) {
                    responseJson.put("status", 204);
                }
                else {
                    final Row locExportRequestDetailsRow = dao.getRow("LocationExportRequestDetails", new Criteria(Column.getColumn("LocationExportRequestDetails", "EXPORT_REQ_ID"), (Object)exportRequestId, 0));
                    final String locExportRequestRemarks = (String)locExportRequestDetailsRow.get("REMARKS");
                    final Integer locExportReqStatus = (Integer)locExportRequestDetailsRow.get("STATUS");
                    responseJson.put("status", 500);
                    responseJson.put("internalStatus", (Object)locExportReqStatus);
                    responseJson.put("errorKey", (Object)locExportRequestRemarks);
                    responseJson.put("error", (Object)I18N.getMsg(locExportRequestRemarks, new Object[0]));
                }
            }
            else {
                responseJson.put("status", 404);
            }
        }
        else {
            responseJson.put("status", 404);
        }
        return responseJson;
    }
    
    public void markExportRequestsInProgressForMoreThanOneDayAsFailed() throws DataAccessException {
        ExportRequestDetailsHandler.logger.log(Level.INFO, "Entering method markExportRequestsInProgressForMoreThanOneDayAsFailed called via daily MDMGlobalTask");
        final Long currentTimeInMillis = MDMUtil.getCurrentTimeInMillis();
        final Long timeValueBeforeOneDay = currentTimeInMillis - 86400000L;
        final Criteria exportDetailsUpdatedTimeCriteria = new Criteria(Column.getColumn("ExportRequestDetails", "UPDATED_TIME"), (Object)timeValueBeforeOneDay, 6);
        final Criteria exportStatusCriteria = new Criteria(Column.getColumn("ExportRequestDetails", "STATUS"), (Object)new Integer[] { 104, 115, 101 }, 9);
        final Criteria finalCriteria = exportStatusCriteria.and(exportDetailsUpdatedTimeCriteria);
        final UpdateQuery locExportRequestDetailsUpdate = (UpdateQuery)new UpdateQueryImpl("LocationExportRequestDetails");
        locExportRequestDetailsUpdate.addJoin(new Join("LocationExportRequestDetails", "ExportRequestDetails", new String[] { "EXPORT_REQ_ID" }, new String[] { "EXPORT_REQ_ID" }, 2));
        locExportRequestDetailsUpdate.setCriteria(finalCriteria);
        locExportRequestDetailsUpdate.setUpdateColumn("STATUS", (Object)511);
        locExportRequestDetailsUpdate.setUpdateColumn("REMARKS", (Object)"mdm.inv.loc_export_failed_due_to_server_restart");
        MDMUtil.getPersistence().update(locExportRequestDetailsUpdate);
        final UpdateQuery exportRequestDetailsUpdate = (UpdateQuery)new UpdateQueryImpl("ExportRequestDetails");
        exportRequestDetailsUpdate.setCriteria(exportStatusCriteria.and(exportDetailsUpdatedTimeCriteria));
        exportRequestDetailsUpdate.setUpdateColumn("STATUS", (Object)115);
        exportRequestDetailsUpdate.setUpdateColumn("UPDATED_TIME", (Object)currentTimeInMillis);
        MDMUtil.getPersistence().update(exportRequestDetailsUpdate);
        ExportRequestDetailsHandler.logger.log(Level.INFO, "Exiting method markExportRequestsInProgressForMoreThanOneDayAsFailed called via daily MDMGlobalTask");
    }
    
    static {
        ExportRequestDetailsHandler.exportRequestDetailsHandler = null;
        ExportRequestDetailsHandler.locExportUserParamPrefix = "LocExp_";
        ExportRequestDetailsHandler.logger = Logger.getLogger("MDMLocationLogger");
    }
}
