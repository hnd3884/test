package com.adventnet.db.adapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

public class RestoreResult
{
    private String restoreFile;
    private String backupFolder;
    private long restoreStartTime;
    private long restoreEndTime;
    private long duration;
    private List<String> tables;
    private BackupRestoreConfigurations.BACKUP_MODE backupMode;
    private List<File> filesToBeCleaned;
    private BackupRestoreConfigurations.BACKUP_TYPE backupType;
    private BackupRestoreConfigurations.RESTORE_STATUS restoreStatus;
    private File dataDirectory;
    private String oldCryptTag;
    private BackupRestoreConfigurations.BACKUP_CONTENT_TYPE backupContentType;
    
    public RestoreResult() {
        this.tables = new ArrayList<String>();
        this.filesToBeCleaned = new ArrayList<File>();
    }
    
    public RestoreResult(final String restoreFile) {
        this.tables = new ArrayList<String>();
        this.filesToBeCleaned = new ArrayList<File>();
        final File file = new File(restoreFile);
        this.restoreFile = file.getName();
        this.backupFolder = file.getParent();
    }
    
    public RestoreResult(final String restoreFile, final String backupFolder) {
        this(restoreFile);
        this.backupFolder = backupFolder;
    }
    
    public String getBackupFile() {
        return this.restoreFile;
    }
    
    public void setBackupFile(final String backupFile) {
        this.restoreFile = backupFile;
    }
    
    public String getBackupFolder() {
        return this.backupFolder;
    }
    
    public void setBackupFolder(final String backupFolder) {
        this.backupFolder = backupFolder;
    }
    
    public long getStartTime() {
        return this.restoreStartTime;
    }
    
    public void setStartTime(final long backupStartTime) {
        this.restoreStartTime = backupStartTime;
    }
    
    public long getEndTime() {
        return this.restoreEndTime;
    }
    
    public void setEndTime(final long backupEndTime) {
        this.restoreEndTime = backupEndTime;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public void calculateDuration() {
        this.duration = this.restoreEndTime - this.restoreStartTime;
    }
    
    public List<String> getTables() {
        return this.tables;
    }
    
    public void setTables(final List<String> tables) {
        this.tables = tables;
    }
    
    public void addTable(final String tableName) {
        this.tables.add(tableName);
    }
    
    public BackupRestoreConfigurations.BACKUP_MODE getBackupMode() {
        return this.backupMode;
    }
    
    public void setBackupMode(final BackupRestoreConfigurations.BACKUP_MODE backupMode) {
        this.backupMode = backupMode;
    }
    
    public List<File> getFilesToBeCleaned() {
        return this.filesToBeCleaned;
    }
    
    public void setFilesToBeCleaned(final List<File> filesToBeCleaned) {
        this.filesToBeCleaned = filesToBeCleaned;
    }
    
    public void addFileToBeCleaned(final File fileToBeCleaned) {
        this.filesToBeCleaned.add(fileToBeCleaned);
    }
    
    public void setBackupType(final BackupRestoreConfigurations.BACKUP_TYPE backupType) {
        this.backupType = backupType;
    }
    
    public BackupRestoreConfigurations.BACKUP_TYPE getBackupType() {
        return this.backupType;
    }
    
    public BackupRestoreConfigurations.RESTORE_STATUS getRestoreStatus() {
        return this.restoreStatus;
    }
    
    public void setRestoreStatus(final BackupRestoreConfigurations.RESTORE_STATUS restoreStatus) {
        this.restoreStatus = restoreStatus;
    }
    
    public String getOldCryptTag() {
        return this.oldCryptTag;
    }
    
    public void setOldCryptTag(final String oldCryptTag) {
        this.oldCryptTag = oldCryptTag;
    }
    
    public File getDataDirectory() {
        return this.dataDirectory;
    }
    
    public void setDataDirectory(final File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }
    
    public BackupRestoreConfigurations.BACKUP_CONTENT_TYPE getBackupContentType() {
        return this.backupContentType;
    }
    
    public void setBackupContentType(final BackupRestoreConfigurations.BACKUP_CONTENT_TYPE backupContentType) {
        this.backupContentType = backupContentType;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<RestoreResult restoreFile=\"");
        sb.append(this.restoreFile);
        sb.append("\" backupFolder=\"");
        sb.append(this.backupFolder);
        sb.append("\" restoreStatus=\"");
        sb.append(this.restoreStatus.name());
        sb.append("\" restoreStartTime=\"");
        sb.append(new Timestamp(this.restoreStartTime));
        sb.append("\" restoreEndTime=\"");
        sb.append(new Timestamp(this.restoreEndTime));
        sb.append("\" duration=\"");
        sb.append(this.duration);
        sb.append("\" tables=\"");
        sb.append(this.tables);
        sb.append("\" backupMode=\"");
        if (this.backupMode != null) {
            sb.append(this.backupMode.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" filesToBeCleaned=\"");
        sb.append(this.filesToBeCleaned);
        sb.append("\" backupType=\"");
        if (this.backupType != null) {
            sb.append(this.backupType.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" backupContentType=\"");
        if (this.backupContentType != null) {
            sb.append(this.backupContentType.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" dataDirectory=\"");
        sb.append(this.dataDirectory);
        sb.append("\"/>");
        return sb.toString();
    }
}
