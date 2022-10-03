package com.adventnet.db.adapter;

import java.sql.Timestamp;

public class RestoreStatus
{
    private BackupRestoreConfigurations.BACKUP_MODE backupMode;
    private long restoreStartTime;
    private long restoreEndTime;
    private String zipFileName;
    private BackupRestoreConfigurations.RESTORE_STATUS status;
    
    public RestoreStatus() {
        this.restoreStartTime = -1L;
        this.restoreEndTime = -1L;
        this.status = null;
    }
    
    public BackupRestoreConfigurations.BACKUP_MODE getBackupMode() {
        return this.backupMode;
    }
    
    public void setBackupMode(final BackupRestoreConfigurations.BACKUP_MODE backupMode) {
        this.backupMode = backupMode;
    }
    
    public long getRestoreStartTime() {
        return this.restoreStartTime;
    }
    
    public void setRestoreStartTime(final long restoreStartTime) {
        this.restoreStartTime = restoreStartTime;
    }
    
    public long getRestoreEndTime() {
        return this.restoreEndTime;
    }
    
    public void setRestoreEndTime(final long restoreEndTime) {
        this.restoreEndTime = restoreEndTime;
    }
    
    public String getZipFileName() {
        return this.zipFileName;
    }
    
    public void setZipFileName(final String zipFileName) {
        this.zipFileName = zipFileName;
    }
    
    public BackupRestoreConfigurations.RESTORE_STATUS getStatus() {
        return this.status;
    }
    
    public void setStatus(final BackupRestoreConfigurations.RESTORE_STATUS status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<RestoreStatus backupMode=\"");
        if (this.backupMode != null) {
            sb.append(this.backupMode.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" backup_zipname=\"");
        sb.append(this.zipFileName);
        sb.append("\" restore_starttime=\"");
        sb.append(new Timestamp(this.restoreStartTime));
        sb.append("\" restore_endtime=\"");
        sb.append(new Timestamp(this.restoreEndTime));
        sb.append("\" restore_status=\"");
        if (this.status != null) {
            sb.append(this.status.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\"/>");
        return sb.toString();
    }
}
