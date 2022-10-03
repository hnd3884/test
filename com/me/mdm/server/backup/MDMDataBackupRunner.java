package com.me.mdm.server.backup;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.server.util.DMReadOnlyDataSetWrapper;
import com.adventnet.ds.query.Range;
import java.util.Collection;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;

public abstract class MDMDataBackupRunner
{
    protected JSONObject backupProperties;
    private SelectQuery selectQuery;
    private double fileSizeinBytes;
    protected String fullFileName;
    protected String filePath;
    protected String moduleName;
    private final int MAX_DATA_COUNT = 1000;
    private ArrayList<Long> customerIdList;
    protected Long customerId;
    protected StringBuilder fileDataStr;
    public static final Logger logger;
    
    public MDMDataBackupRunner() {
        this.backupProperties = null;
        this.fileSizeinBytes = 5242880.0;
        this.fileDataStr = new StringBuilder();
    }
    
    public final void initBackup() {
        MDMDataBackupRunner.logger.log(Level.INFO, " ** Data backup Init ** ");
        try {
            this.prepBackupModuleProperties(this.moduleName);
            this.selectQuery = this.constructBackupQuery();
            if (this.isNeedBackup()) {
                this.doBackup();
            }
            this.doCleanup();
        }
        catch (final Exception ex) {
            MDMDataBackupRunner.logger.log(Level.WARNING, "Exception occurred while initBackup ", ex);
        }
        MDMDataBackupRunner.logger.log(Level.INFO, " ** Data backup Completed ** ");
    }
    
    public final void initBackupForAllCustomer() throws Exception {
        this.customerIdList = new ArrayList<Long>(Arrays.asList(CustomerInfoUtil.getInstance().getCustomerIdsFromDB()));
        for (int i = 0; i < this.customerIdList.size(); ++i) {
            this.customerId = this.customerIdList.get(i);
            this.initBackup();
        }
    }
    
    protected void setFilePath(final String filePath) {
        this.filePath = filePath;
    }
    
    protected boolean isNeedBackup() {
        return true;
    }
    
    private void doBackup() {
        int rangeStart = 1;
        final int columnCount = this.selectQuery.getSelectColumns().size();
        DMDataSetWrapper ds = null;
        boolean isExecuteAgain = true;
        try {
            do {
                final Range range = new Range(rangeStart, 1000);
                this.selectQuery.setRange(range);
                ds = DMReadOnlyDataSetWrapper.executeQuery(this.selectQuery);
                if (ds.next()) {
                    this.addDataToFileBuffer(ds, columnCount);
                    while (ds.next()) {
                        if (!this.isBackupFileSizeExceed()) {
                            this.addDataToFileBuffer(ds, columnCount);
                        }
                        else {
                            this.addDataToFileBuffer(ds, columnCount);
                            this.writeFile();
                            this.resetFileBuffer();
                        }
                    }
                }
                else {
                    isExecuteAgain = false;
                }
                rangeStart += 1000;
            } while (isExecuteAgain);
            if (this.fileDataStr.length() > 0) {
                this.writeFile();
                this.resetFileBuffer();
            }
        }
        catch (final Exception ex) {
            MDMDataBackupRunner.logger.log(Level.WARNING, "Exception occurred while executeQuery", ex);
        }
    }
    
    private void addDataToFileBuffer(final DMDataSetWrapper ds, final int count) throws Exception {
        for (int i = 1; i <= count; ++i) {
            final Object ob = ds.getValue(i);
            if (ob != null) {
                this.fileDataStr.append(ob);
            }
            else {
                this.fileDataStr.append("--");
            }
            if (i != count) {
                this.fileDataStr.append(",");
            }
            else {
                this.fileDataStr.append("\n");
            }
        }
    }
    
    private boolean isBackupFileSizeExceed() {
        boolean isExceed = false;
        final byte[] bufferDataBytes = this.fileDataStr.toString().getBytes();
        if (bufferDataBytes.length > this.fileSizeinBytes) {
            isExceed = true;
        }
        return isExceed;
    }
    
    private void setMaxFileSize(final double sizeInBytes) {
        this.fileSizeinBytes = sizeInBytes;
    }
    
    private void writeFile() {
        final String path = this.getBackupFilePath();
        try {
            final FileAccessAPI fileAPI = ApiFactoryProvider.getFileAccessAPI();
            fileAPI.writeFile(path, this.fileDataStr.toString().getBytes());
            this.prepAndUpdateBackupFileData();
        }
        catch (final Exception ex) {
            MDMDataBackupRunner.logger.log(Level.WARNING, "Exception occurred while writing data into file", ex);
        }
    }
    
    private void prepAndUpdateBackupFileData() {
        final BackupFileData backupFileData = new BackupFileData();
        backupFileData.addFeatureId(this.backupProperties.optLong("FEATURE_ID"));
        backupFileData.addCreatedTime(System.currentTimeMillis());
        backupFileData.addFileName(this.fullFileName);
        backupFileData.addFileSize(ApiFactoryProvider.getFileAccessAPI().getFileSize(this.getBackupFilePath()));
        new BackupFileDataHandler().addBackupDataFilesInfo(backupFileData);
    }
    
    protected String getBackupFilePath() {
        final String basePath = this.backupProperties.optString("BACKUP_LOCATION", (String)null);
        String path = "";
        if (basePath != null) {
            String fileName = this.backupProperties.optString("FILE_NAME");
            final SimpleDateFormat e = new SimpleDateFormat("-yy-MM-dd-HH-mm-ss-SSS");
            fileName = fileName.replace("-date", e.format(new Date()));
            this.fullFileName = fileName;
            path = System.getProperty("server.home") + File.separator + basePath + File.separator + fileName;
        }
        return path;
    }
    
    private void resetFileBuffer() {
        this.fileDataStr.setLength(0);
        this.fileDataStr.trimToSize();
    }
    
    private void prepBackupModuleProperties(final String moduleName) {
        try {
            final Criteria criteria = new Criteria(new Column("DCCleanUpDataInfo", "FEATURE_NAME"), (Object)moduleName, 0);
            final DataObject modDO = DataAccess.get("DCCleanUpDataInfo", criteria);
            if (modDO.isEmpty()) {
                throw new Exception("Backup module not found in DB. ModuleName:" + moduleName);
            }
            this.backupProperties = new JSONObject();
            final Row modRow = modDO.getFirstRow("DCCleanUpDataInfo");
            this.backupProperties.put("FEATURE_ID", modRow.get("FEATURE_ID"));
            this.backupProperties.put("FEATURE_NAME", (Object)moduleName);
            String location = (String)modRow.get("BACKUP_LOCATION");
            location = location.replace("/", File.separator);
            this.backupProperties.put("BACKUP_LOCATION", (Object)location);
            this.backupProperties.put("FILE_NAME", modRow.get("FILE_NAME"));
            this.backupProperties.put("MAX_FOLDER_SIZE", modRow.get("MAX_FOLDER_SIZE"));
        }
        catch (final Exception ex) {
            MDMDataBackupRunner.logger.log(Level.WARNING, "Exception occurred while getBackupModuleProps", ex);
        }
    }
    
    protected abstract SelectQuery constructBackupQuery();
    
    protected abstract void doCleanup();
    
    static {
        logger = Logger.getLogger("MDMLogger");
    }
}
