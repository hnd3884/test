package com.adventnet.db.adapter;

import java.sql.Timestamp;
import java.util.List;
import java.io.File;

public class BackupStatus
{
    private long backupID;
    private BackupRestoreConfigurations.BACKUP_TYPE backupType;
    private long backupStartTime;
    private long backupEndTime;
    private String zipFileName;
    private File backupFolder;
    private BackupRestoreConfigurations.BACKUP_STATUS status;
    private int dataFileCount;
    private String lastDataFileName;
    private long lastDataFileModifiedTime;
    private List<String> fileNames;
    
    public BackupStatus() {
        this.backupID = -1L;
        this.backupStartTime = -1L;
        this.backupEndTime = -1L;
        this.status = null;
        this.dataFileCount = 0;
        this.lastDataFileName = null;
        this.lastDataFileModifiedTime = -1L;
        this.fileNames = null;
    }
    
    public BackupRestoreConfigurations.BACKUP_TYPE getBackupType() {
        return this.backupType;
    }
    
    public long getBackupStartTime() {
        return this.backupStartTime;
    }
    
    public long getBackupEndTime() {
        return this.backupEndTime;
    }
    
    public String getZipFileName() {
        return this.zipFileName;
    }
    
    public File getBackupFolder() {
        return this.backupFolder;
    }
    
    public BackupRestoreConfigurations.BACKUP_STATUS getStatus() {
        return this.status;
    }
    
    public long getBackupID() {
        return this.backupID;
    }
    
    public int getDataFileCount() {
        return this.dataFileCount;
    }
    
    public String getLastDataFileName() {
        return this.lastDataFileName;
    }
    
    public long getLastDataFileModifiedTime() {
        return this.lastDataFileModifiedTime;
    }
    
    public void setDataFileCount(final int dataFileCount) {
        this.dataFileCount = dataFileCount;
    }
    
    public void setLastDataFileName(final String lastDataFileName) {
        this.lastDataFileName = lastDataFileName;
    }
    
    public List<String> getFileNames() {
        return this.fileNames;
    }
    
    public void setFileNames(final List<String> fileNames) {
        this.fileNames = fileNames;
    }
    
    public void setLastDataFileModifiedTime(final long lastDataFileModifiedTime) {
        this.lastDataFileModifiedTime = lastDataFileModifiedTime;
    }
    
    public void setStatus(final BackupRestoreConfigurations.BACKUP_STATUS status) {
        this.status = status;
    }
    
    public void setBackupEndTime(final long backupEndTime) {
        this.backupEndTime = backupEndTime;
    }
    
    public void setZipFileName(final String zipFileName) {
        this.zipFileName = zipFileName;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<BackupStatus backup_id=");
        sb.append(this.backupID);
        sb.append(" backupType=\"");
        if (this.backupType != null) {
            sb.append(this.backupType.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" backup_folder=\"");
        sb.append(this.backupFolder);
        sb.append("\" backup_zipname=\"");
        sb.append(this.zipFileName);
        sb.append("\" backup_starttime=\"");
        sb.append(new Timestamp(this.backupStartTime));
        sb.append("\" backup_endtime=\"");
        sb.append(new Timestamp(this.backupEndTime));
        sb.append("\" backup_status=\"");
        if (this.status != null) {
            sb.append(this.status.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" dataFileCount=\"");
        sb.append(this.dataFileCount);
        sb.append("\" lastDataFileName=\"");
        sb.append(this.lastDataFileName);
        sb.append("\" lastDataFileModifiedTime=\"");
        sb.append(this.lastDataFileModifiedTime);
        sb.append("\" fileNames=\"");
        sb.append(this.fileNames);
        sb.append("\"/>");
        return sb.toString();
    }
    
    public void setBackupID(final long backupID) {
        this.backupID = backupID;
    }
    
    public void setBackupType(final BackupRestoreConfigurations.BACKUP_TYPE backupType) {
        this.backupType = backupType;
    }
    
    public void setBackupStartTime(final long backupStartTime) {
        this.backupStartTime = backupStartTime;
    }
    
    public void setBackupFolder(final File backupFolder) {
        this.backupFolder = backupFolder;
    }
}
