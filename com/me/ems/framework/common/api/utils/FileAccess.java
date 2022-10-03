package com.me.ems.framework.common.api.utils;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;
import com.me.devicemanagement.framework.server.scheduler.SchedulerProviderInterface;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.io.File;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.WritableDataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class FileAccess
{
    private static Logger logger;
    public static final Integer FILE_STATUS_NOT_READY;
    public static final Integer FILE_STATUS_READY;
    public static final Integer FILE_STATUS_FAILED;
    public static final Integer FILE_AV_SCANNING_INPROGRESS;
    public static final Integer FILE_AV_SCANNING_COMPLETED;
    public static final Integer FILE_VIRUS_DETECTED;
    public static final String FILE_ID = "file_id";
    public static final String FILE_AVAILABILITY_STATUS = "file_availability_status";
    public static final String REMARKS = "remarks";
    
    public Long addOrUpdateDMFiles(final Row fileDetails, Long fileID) throws DataAccessException {
        final int fileStatus = (int)fileDetails.get("FILE_STATUS");
        final String filePath = (String)fileDetails.get("FILE_SYSTEM_LOCATION");
        if (fileStatus == FileAccess.FILE_STATUS_READY && filePath != null && filePath.isEmpty()) {
            throw new IllegalArgumentException("File path is mandatory for AVAILABILITY-READY files");
        }
        if (fileID != null) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCFiles"));
            selectQuery.setCriteria(new Criteria(new Column("DCFiles", "FILE_ID"), (Object)fileID, 0));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                FileAccess.logger.log(Level.SEVERE, "Given Files ID not found in DB ({0})", fileID);
                return -1L;
            }
            dataObject.updateRow(fileDetails);
            SyMUtil.getPersistence().update(dataObject);
        }
        else {
            DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(fileDetails);
            dataObject = SyMUtil.getPersistence().add(dataObject);
            fileID = (Long)dataObject.getFirstValue("DCFiles", "FILE_ID");
        }
        return fileID;
    }
    
    public String getFilePath(final Long fileID, final Long customerID, final String moduleName) throws APIException {
        final DataObject dataObject = this.getFileDetailsDO(fileID, customerID, moduleName);
        Row fileDetailsRow;
        try {
            fileDetailsRow = dataObject.getRow("DCFiles");
        }
        catch (final Exception ex) {
            FileAccess.logger.log(Level.SEVERE, " Exception while retriving File Path ... : File ID :" + fileID + " Customer ID :" + customerID + " ModuleName :" + moduleName, ex);
            throw new APIException("GENERIC0005");
        }
        return fileDetailsRow.get("FILE_SYSTEM_LOCATION").toString();
    }
    
    public String getFileName(final Long fileID, final Long customerID, final String moduleName) throws APIException {
        final DataObject dataObject = this.getFileDetailsDO(fileID, customerID, moduleName);
        Row fileDetailsRow;
        try {
            fileDetailsRow = dataObject.getRow("DCFiles");
        }
        catch (final Exception ex) {
            FileAccess.logger.log(Level.SEVERE, " Exception while retriving File Path ... : File ID :" + fileID + " Customer ID :" + customerID + " ModuleName :" + moduleName, ex);
            throw new APIException("GENERIC0005");
        }
        return fileDetailsRow.get("FILE_NAME").toString();
    }
    
    public DataObject getFileDetailsDO(final Long fileID, final Long customerID, final String moduleName) throws APIException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCFiles"));
        final Criteria customerIDCrit = new Criteria(new Column("DCFiles", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(new Criteria(new Column("DCFiles", "FILE_ID"), (Object)fileID, 0).and(customerIDCrit));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIException("RESOURCE0004");
            }
            final Row row = dataObject.getRow("DCFiles");
            final String moduleNameFromTable = (String)row.get("MODULE_NAME");
            if (moduleNameFromTable != null && !moduleNameFromTable.isEmpty() && (moduleNameFromTable.equalsIgnoreCase("DEFAULT_MODULE") || moduleNameFromTable.equalsIgnoreCase(moduleName))) {
                return dataObject;
            }
        }
        catch (final APIException ex) {
            FileAccess.logger.log(Level.SEVERE, " Exception while retriving File Data Object... : File ID :" + fileID + " Customer ID :" + customerID + " ModuleName :" + moduleName, ex);
            throw ex;
        }
        catch (final Exception ex2) {
            FileAccess.logger.log(Level.SEVERE, " Exception while retriving File Data Object... : File ID :" + fileID + " Customer ID :" + customerID + " ModuleName :" + moduleName, ex2);
            throw new APIException("GENERIC0005");
        }
        throw new APIException("USER0002");
    }
    
    public Row constructFileDetailsRow(final String fileName, final String filePath, final String contentType, final Long contentLength, final String remarks, final Long customerID, final Long expiryTime, final String moduleName) {
        return this.constructFileDetailsRowWithStatus(fileName, filePath, contentType, contentLength, remarks, customerID, expiryTime, moduleName, FileAccess.FILE_STATUS_READY);
    }
    
    public Row constructFileDetailsRowWithStatus(final String fileName, final String filePath, final String contentType, final Long contentLength, final String remarks, final Long customerID, final Long expiryTime, final String moduleName, final int status) {
        final Row fileDetailsRow = new Row("DCFiles");
        fileDetailsRow.set("EXPIRY_TIME", (Object)expiryTime);
        fileDetailsRow.set("FILE_SYSTEM_LOCATION", (Object)filePath);
        fileDetailsRow.set("CUSTOMER_ID", (Object)customerID);
        fileDetailsRow.set("CONTENT_TYPE", (Object)contentType);
        fileDetailsRow.set("CONTENT_LENGTH", (Object)contentLength);
        fileDetailsRow.set("FILE_STATUS", (Object)status);
        fileDetailsRow.set("FILE_STATUS_REMARKS", (Object)remarks);
        fileDetailsRow.set("FILE_NAME", (Object)fileName);
        fileDetailsRow.set("MODULE_NAME", (Object)moduleName);
        return fileDetailsRow;
    }
    
    public Long addDCFile(final String fileName, final String filePath, final Long customerID, final String moduleName) throws DataAccessException, FileNotFoundException {
        final File file = new File(filePath);
        try {
            if (file.exists()) {
                final Long contentLength = file.length();
                final Long expiryTime = -1L;
                final String contentType = Files.probeContentType(FileSystems.getDefault().getPath(filePath, new String[0]));
                final Row fileDetailsRow = this.constructFileDetailsRow(fileName, filePath, contentType, contentLength, "--", customerID, expiryTime, moduleName);
                final Long fileId = this.addOrUpdateDMFiles(fileDetailsRow, null);
                return fileId;
            }
            throw new FileNotFoundException();
        }
        catch (final IOException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
    
    public void updateFilePath(final Long fileID, final String filePath) throws DataAccessException {
        this.updateFilePathAndName(fileID, filePath, null);
    }
    
    public void updateFilePathAndName(final Long fileID, final String filePath, final String fileName) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DCFiles");
        final Criteria fileIDCriteria = new Criteria(Column.getColumn("DCFiles", "FILE_ID"), (Object)fileID, 0);
        updateQuery.setCriteria(fileIDCriteria);
        updateQuery.setUpdateColumn("FILE_SYSTEM_LOCATION", (Object)filePath);
        updateQuery.setUpdateColumn("EXPIRY_TIME", (Object)(-1));
        if (fileName != null) {
            updateQuery.setUpdateColumn("FILE_NAME", (Object)fileName);
        }
        DataAccess.update(updateQuery);
    }
    
    public void deleteFile(final Long fileID) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DCFiles");
        final Criteria fileIDCriteria = new Criteria(Column.getColumn("DCFiles", "FILE_ID"), (Object)fileID, 0);
        updateQuery.setCriteria(fileIDCriteria);
        updateQuery.setUpdateColumn("EXPIRY_TIME", (Object)System.currentTimeMillis());
        DataAccess.update(updateQuery);
    }
    
    public File getUploadedFile(final Long fileID, final Long customerID, final String moduleName) throws APIException {
        final FileAccess dcFileAccess = new FileAccess();
        final String filePath = dcFileAccess.getFilePath(fileID, customerID, moduleName);
        if (filePath != null && !filePath.trim().equalsIgnoreCase("")) {
            return new File(filePath);
        }
        throw new APIException("RESOURCE0004");
    }
    
    public DataObject getFileDetailsDOWithoutCustomerID(final Long fileID, final String moduleName) throws APIException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCFiles"));
        selectQuery.setCriteria(new Criteria(new Column("DCFiles", "FILE_ID"), (Object)fileID, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIException("RESOURCE0004");
            }
            final Row row = dataObject.getRow("DCFiles");
            final String moduleNameFromTable = (String)row.get("MODULE_NAME");
            if (moduleNameFromTable != null && !moduleNameFromTable.isEmpty() && (moduleNameFromTable.equalsIgnoreCase("DEFAULT_MODULE") || moduleNameFromTable.equalsIgnoreCase(moduleName))) {
                return dataObject;
            }
        }
        catch (final APIException ex) {
            FileAccess.logger.log(Level.SEVERE, " Exception while retriving File Data Object Without Customer ID... : File ID :" + fileID + " ModuleName :" + moduleName, ex);
            throw ex;
        }
        catch (final Exception ex2) {
            FileAccess.logger.log(Level.SEVERE, " Exception while retriving File Data Object Without Customer ID... : File ID :" + fileID + " ModuleName :" + moduleName, ex2);
            throw new APIException("GENERIC0005");
        }
        throw new APIException("USER0002");
    }
    
    public static void createFilesTableCleanupScheduler() {
        final SchedulerProviderInterface schedulerAPI = ApiFactoryProvider.getSchedulerAPI();
        if (!schedulerAPI.isScheduleCreated("CleanFilesTableScheduler")) {
            final HashMap schedulerProps = new HashMap();
            schedulerProps.put("workflowName", "CleanFilesTableScheduler");
            schedulerProps.put("schedulerName", "CleanFilesTableScheduler");
            schedulerProps.put("operationType", String.valueOf(11000));
            schedulerProps.put("taskName", "CleanFilesTableScheduler");
            schedulerProps.put("className", "com.me.ems.framework.common.api.utils.CleanFilesTableTask");
            schedulerProps.put("description", "Scheduler to Remove Temporary Files from Files Table and Temp Location executed Daily.");
            schedulerProps.put("schType", "Daily");
            schedulerProps.put("skip_missed_schedule", "false");
            schedulerProps.put("time", "01:00:00");
            schedulerProps.put("dailyIntervalType", "everyDay");
            schedulerAPI.createScheduler(schedulerProps);
            schedulerAPI.setSchedulerState(true, "CleanFilesTableScheduler");
        }
    }
    
    public void deleteFileFromTableAndLocation(final Long fileID, final Long customerID, final String moduleName) throws Exception {
        ApiFactoryProvider.getFileAccessAPI().deleteFile(this.getFilePath(fileID, customerID, moduleName));
        DataAccess.delete(new Criteria(Column.getColumn("DCFiles", "FILE_ID"), (Object)fileID, 0));
        FileAccess.logger.log(Level.INFO, "File Deleted in FileAccess -> deleteFileFromTableAndLocation;  FileID " + fileID);
    }
    
    @Deprecated
    public void updateCustomerID(final Long fileID, final Long customerID) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DCFiles");
        updateQuery.setCriteria(new Criteria(Column.getColumn("DCFiles", "FILE_ID"), (Object)fileID, 0));
        updateQuery.setUpdateColumn("CUSTOMER_ID", (Object)customerID);
        DataAccess.update(updateQuery);
        FileAccess.logger.log(Level.INFO, "FileAccess updateCustomerID : File Id : " + fileID + " Customer ID" + customerID);
    }
    
    public JSONArray getFileStatus(final List<Long> fileIds, final Long customerId) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCFiles"));
        selectQuery.setCriteria(new Criteria(new Column("DCFiles", "FILE_ID"), (Object)fileIds.toArray(), 8).and(Column.getColumn("DCFiles", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final JSONArray responseArray = new JSONArray();
            final Iterator rows = dataObject.getRows("DCFiles");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final JSONObject response = new JSONObject();
                final String fileID = row.get("FILE_ID").toString();
                response.put("file_id", (Object)fileID);
                response.put("file_availability_status", row.get("FILE_STATUS"));
                response.put("remarks", row.get("FILE_STATUS_REMARKS"));
                responseArray.put((Object)response);
            }
            return responseArray;
        }
        throw new IllegalArgumentException("Invalid file_id passed!");
    }
    
    public void changeFileAvailabilityStatus(final Long fileId, final int status, final String remarks) throws DataAccessException {
        if (status != FileAccess.FILE_STATUS_FAILED && status != FileAccess.FILE_STATUS_NOT_READY && status != FileAccess.FILE_STATUS_READY && status != FileAccess.FILE_AV_SCANNING_INPROGRESS && status != FileAccess.FILE_AV_SCANNING_COMPLETED && status != FileAccess.FILE_VIRUS_DETECTED) {
            throw new IllegalArgumentException("Invalid status passed!");
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCFiles"));
        selectQuery.setCriteria(new Criteria(new Column("DCFiles", "FILE_ID"), (Object)fileId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new IllegalArgumentException("Invalid file_id passed!");
        }
        final Row row = dataObject.getRow("DCFiles");
        row.set("FILE_STATUS", (Object)status);
        if (remarks != null) {
            row.set("FILE_STATUS_REMARKS", (Object)remarks);
        }
        dataObject.updateRow(row);
        DataAccess.update(dataObject);
    }
    
    static {
        FileAccess.logger = Logger.getLogger(FileAccess.class.getName());
        FILE_STATUS_NOT_READY = 1;
        FILE_STATUS_READY = 2;
        FILE_STATUS_FAILED = 3;
        FILE_AV_SCANNING_INPROGRESS = 4;
        FILE_AV_SCANNING_COMPLETED = 5;
        FILE_VIRUS_DETECTED = 6;
    }
}
