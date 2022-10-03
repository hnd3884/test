package com.me.mdm.files.upload;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class FileUploadManager
{
    static Logger logger;
    public static final String FILE_SYSTEM_LOCATION = "file_system_location";
    public static final String EXPIRY_OFFSET = "expiry_offset";
    public static final String CONTENT_TYPE = "content_type";
    public static final String FILE_NAME = "file_name";
    public static final String CONTENT_LENGTH = "content_length";
    public static final String FILE_AVAILABILIY_STATUS = "file_availability_status";
    public static final String REMARKS = "remarks";
    public static final int FILE_AVAILABILITY_NOT_READY = 1;
    public static final int FILE_AVAILABILITY_READY = 2;
    public static final int FILE_AVAILABILITY_FAILED = 3;
    public static final int FILE_AV_SCANNING_INPROGRESS = 4;
    public static final int FILE_AV_SCANNING_COMPLETED = 5;
    public static final int FILE_VIRUS_DETECTED = 6;
    
    public Long addOrUpdateDMFiles(final JSONObject fileJSON) throws JSONException, DataAccessException {
        final Long expiryOffset = fileJSON.optLong("expiry_offset", 600000L);
        final int fileStatus = fileJSON.getInt("file_availability_status");
        final String completedFileName = fileJSON.optString("file_system_location");
        Long fileID = -1L;
        if (fileStatus == 2 && completedFileName.isEmpty()) {
            throw new IllegalArgumentException("File path is mandatory for AVAILABILITY-READY files");
        }
        if (fileJSON.has("FILE_ID")) {
            fileID = fileJSON.getLong("FILE_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DMFiles"));
            selectQuery.setCriteria(new Criteria(new Column("DMFiles", "FILE_ID"), (Object)fileID, 0));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(selectQuery);
            if (dO.isEmpty()) {
                FileUploadManager.logger.log(Level.SEVERE, "Given DMFiles ID not found in DB ({0})", fileID);
                return -1L;
            }
            final Row row = dO.getFirstRow("DMFiles");
            if (fileJSON.has("expiry_offset")) {
                final Long expiryTime = MDMUtil.getCurrentTimeInMillis() + expiryOffset;
                row.set("EXPIRY_TIME", (Object)expiryTime);
            }
            if (fileJSON.has("file_system_location")) {
                row.set("FILE_SYSTEM_LOCATION", (Object)completedFileName);
            }
            if (fileJSON.has("content_type")) {
                row.set("CONTENT_TYPE", (Object)String.valueOf(fileJSON.get("content_type")));
            }
            if (fileJSON.has("file_name")) {
                row.set("FILE_NAME", (Object)fileJSON.getString("file_name"));
            }
            if (fileJSON.has("content_length")) {
                row.set("CONTENT_LENGTH", (Object)fileJSON.optInt("content_length", 1));
            }
            if (fileJSON.has("file_availability_status")) {
                row.set("FILE_STATUS", (Object)fileStatus);
            }
            if (fileJSON.has("remarks")) {
                row.set("FILE_STATUS_REMARKS", (Object)String.valueOf(fileJSON.get("remarks")));
            }
            dO.updateRow(row);
            DataAccess.update(dO);
        }
        else {
            Long customerId = fileJSON.getLong("CUSTOMER_ID");
            if (customerId < 0L) {
                customerId = null;
            }
            DataObject dO = (DataObject)new WritableDataObject();
            final Row row = new Row("DMFiles");
            final Long expiryTime = MDMUtil.getCurrentTimeInMillis() + expiryOffset;
            row.set("EXPIRY_TIME", (Object)expiryTime);
            row.set("FILE_SYSTEM_LOCATION", (Object)completedFileName);
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("FILE_NAME", (Object)fileJSON.optString("file_name", "--"));
            row.set("CONTENT_TYPE", (Object)fileJSON.getString("content_type"));
            row.set("CONTENT_LENGTH", (Object)fileJSON.optInt("content_length", 1));
            row.set("FILE_STATUS", (Object)fileStatus);
            if (fileJSON.has("remarks")) {
                row.set("FILE_STATUS_REMARKS", (Object)String.valueOf(fileJSON.get("remarks")));
            }
            dO.addRow(row);
            dO = DataAccess.add(dO);
            fileID = (Long)dO.getFirstValue("DMFiles", "FILE_ID");
        }
        return fileID;
    }
    
    public void changeFileAvailabilityStatus(final Long fileId, final int status) throws DataAccessException {
        this.changeFileAvailabilityStatus(fileId, status, null);
    }
    
    public void changeFileAvailabilityStatus(final Long fileId, final int status, final String remarks) throws DataAccessException {
        if (status != 3 && status != 1 && status != 2 && status != 4 && status != 5 && status != 6) {
            throw new IllegalArgumentException("Invalid status passed!");
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DMFiles"));
        selectQuery.setCriteria(new Criteria(new Column("DMFiles", "FILE_ID"), (Object)fileId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(selectQuery);
        if (dO.isEmpty()) {
            throw new IllegalArgumentException("Invalid file_id passed!");
        }
        final Row row = dO.getRow("DMFiles");
        row.set("FILE_STATUS", (Object)status);
        if (remarks != null) {
            row.set("FILE_STATUS_REMARKS", (Object)remarks);
        }
        dO.updateRow(row);
        DataAccess.update(dO);
    }
    
    public JSONObject getFileStatus(final Long fileId, final Long customerId) throws DataAccessException, JSONException {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DMFiles"));
        selectQuery.setCriteria(new Criteria(new Column("DMFiles", "FILE_ID"), (Object)fileId, 0).and(Column.getColumn("DMFiles", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(selectQuery);
        if (dO.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "File id :" + fileId });
        }
        final Row row = dO.getRow("DMFiles");
        response.put("file_availability_status", row.get("FILE_STATUS"));
        response.put("remarks", row.get("FILE_STATUS_REMARKS"));
        return response;
    }
    
    public static JSONObject getFileDetails(final Long fileID, final Long customerId) throws APIHTTPException {
        try {
            Criteria criteria = new Criteria(Column.getColumn("DMFiles", "FILE_ID"), (Object)fileID, 0).and(new Criteria(Column.getColumn("DMFiles", "EXPIRY_TIME"), (Object)MDMUtil.getCurrentTimeInMillis(), 5));
            if (customerId != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("DMFiles", "CUSTOMER_ID"), (Object)customerId, 0));
            }
            final DataObject DO = MDMUtil.getPersistence().get("DMFiles", criteria);
            String fileName = null;
            if (!DO.isEmpty()) {
                fileName = (String)DO.getFirstValue("DMFiles", "FILE_SYSTEM_LOCATION");
                final int fileStatus = (int)DO.getFirstValue("DMFiles", "FILE_STATUS");
                if (fileStatus != 2) {
                    throw new APIHTTPException("COM0014", new Object[] { (fileStatus == 3) ? ("File unavailable - Upload Failed, Reason :" + (String)DO.getFirstValue("DMFiles", "FILE_STATUS_REMARKS")) : "File unavailable yet - Async Upload inprogress, use the /mdm/files/<file_id>/status endpoint to know the file status" });
                }
            }
            if (MDMStringUtils.isEmpty(fileName)) {
                throw new APIHTTPException("COM0008", new Object[] { fileID });
            }
            final JSONObject fileJSON = JSONUtil.toJSON("file_path", fileName);
            fileJSON.put("content_type", (Object)DO.getFirstValue("DMFiles", "CONTENT_TYPE"));
            fileJSON.put("file_name", (Object)DO.getFirstValue("DMFiles", "FILE_NAME"));
            return fileJSON;
        }
        catch (final DataAccessException | JSONException e) {
            FileUploadManager.logger.log(Level.SEVERE, "error while getting file", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static String getFilePath(final Long fileID) throws APIHTTPException, JSONException {
        final JSONObject fileDetailsJSON = getFileDetails(fileID, null);
        return String.valueOf(fileDetailsJSON.get("file_path"));
    }
    
    public static JSONObject getFilePath(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long fileID = JSONUtil.optLongForUVH(requestJSON, "file_id", (Long)null);
            if (fileID == 0L) {
                return null;
            }
            final String fileName = getFilePath(fileID);
            return JSONUtil.toJSON("file_path", fileName);
        }
        catch (final JSONException e) {
            FileUploadManager.logger.log(Level.SEVERE, "error while getting file", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        FileUploadManager.logger = Logger.getLogger("MDMApiLogger");
    }
}
