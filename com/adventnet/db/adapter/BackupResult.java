package com.adventnet.db.adapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

public class BackupResult
{
    private String backupFile;
    private String backupFolder;
    private BackupRestoreConfigurations.BACKUP_STATUS backupStatus;
    private long backupStartTime;
    private long backupEndTime;
    private long duration;
    private List<String> tables;
    private BackupRestoreConfigurations.BACKUP_MODE backupMode;
    private List<String> filesToBeCleaned;
    private BackupRestoreConfigurations.BACKUP_TYPE backupType;
    private File dataDirectory;
    private BackupRestoreConfigurations.BACKUP_CONTENT_TYPE backupContentType;
    private long backupSize;
    
    public BackupResult() {
        this.tables = new ArrayList<String>();
        this.filesToBeCleaned = new ArrayList<String>();
    }
    
    public BackupResult(final String backupFile) {
        this.tables = new ArrayList<String>();
        this.filesToBeCleaned = new ArrayList<String>();
        this.backupFile = backupFile;
    }
    
    public BackupResult(final String backupFile, final String backupFolder) {
        this(backupFile);
        this.backupFolder = backupFolder;
    }
    
    public String getBackupFile() {
        return this.backupFile;
    }
    
    public void setBackupFile(final String backupFile) {
        this.backupFile = backupFile;
    }
    
    public String getBackupFolder() {
        return this.backupFolder;
    }
    
    public void setBackupFolder(final String backupFolder) {
        this.backupFolder = backupFolder;
    }
    
    public BackupRestoreConfigurations.BACKUP_STATUS getBackupStatus() {
        return this.backupStatus;
    }
    
    public void setBackupStatus(final BackupRestoreConfigurations.BACKUP_STATUS backupStatus) {
        this.backupStatus = backupStatus;
    }
    
    public long getStartTime() {
        return this.backupStartTime;
    }
    
    public void setStartTime(final long backupStartTime) {
        this.backupStartTime = backupStartTime;
    }
    
    public long getEndTime() {
        return this.backupEndTime;
    }
    
    public void setEndTime(final long backupEndTime) {
        this.backupEndTime = backupEndTime;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public void calculateDuration() {
        this.duration = this.backupEndTime - this.backupStartTime;
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
    
    public List<String> getFilesToBeCleaned() {
        return this.filesToBeCleaned;
    }
    
    public void setFilesToBeCleaned(final List<String> filesToBeCleaned) {
        this.filesToBeCleaned = filesToBeCleaned;
    }
    
    public void addFileToBeCleaned(final String fileToBeCleaned) {
        this.filesToBeCleaned.add(fileToBeCleaned);
    }
    
    public void setBackupType(final BackupRestoreConfigurations.BACKUP_TYPE backupType) {
        this.backupType = backupType;
    }
    
    public BackupRestoreConfigurations.BACKUP_TYPE getBackupType() {
        return this.backupType;
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
    
    public long getBackupSize() {
        return this.backupSize;
    }
    
    public void setBackupSize(final long backupSize) {
        this.backupSize = backupSize;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<BackupRestoreResult backupFile=\"");
        sb.append(this.backupFile);
        sb.append("\" backupFolder=\"");
        sb.append(this.backupFolder);
        sb.append("\" backupStatus=\"");
        sb.append(this.backupStatus.name());
        sb.append("\" backupStartTime=\"");
        sb.append(new Timestamp(this.backupStartTime));
        sb.append("\" backupEndTime=\"");
        sb.append(new Timestamp(this.backupEndTime));
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
        sb.append("\" backupSize \"");
        sb.append(this.backupSize);
        sb.append("\"/>");
        return sb.toString();
    }
}
