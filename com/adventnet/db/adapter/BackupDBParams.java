package com.adventnet.db.adapter;

import java.sql.Timestamp;
import java.io.File;

public class BackupDBParams
{
    public long backupID;
    public BackupRestoreConfigurations.BACKUP_TYPE backupType;
    public long backupStartTime;
    public long backupEndTime;
    public String zipFileName;
    public File backupFolder;
    public String backupLabelFile;
    public boolean incrementalBackupEnabled;
    public long lastIncrementalBackupEndTime;
    public long prevBackupLastDataFileModifiedTime;
    public long lastFullBackupStartTime;
    public String fullbackup_zipname;
    public String previous_incr_backup_zipnames;
    public String lastDataFileName;
    public int backupLabelWaitDuration;
    public BackupRestoreConfigurations.BACKUP_MODE backupMode;
    public BackupRestoreConfigurations.BACKUP_CONTENT_TYPE backupContentType;
    public long databaseSize;
    public long backupSize;
    public long expectedBackupSize;
    public String remoteBackupDir;
    public String archivePassword;
    public String archiveEncAlgo;
    
    public BackupDBParams() {
        this.backupID = -1L;
        this.backupStartTime = -1L;
        this.backupEndTime = -1L;
        this.backupFolder = null;
        this.incrementalBackupEnabled = true;
        this.lastIncrementalBackupEndTime = -1L;
        this.prevBackupLastDataFileModifiedTime = -1L;
        this.lastFullBackupStartTime = -1L;
        this.fullbackup_zipname = null;
        this.previous_incr_backup_zipnames = null;
        this.lastDataFileName = null;
        this.backupLabelWaitDuration = 10;
        this.backupMode = BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP;
        this.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY;
        this.databaseSize = -1L;
        this.backupSize = -1L;
        this.expectedBackupSize = -1L;
        this.archiveEncAlgo = "AES256";
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<BackupDBParams backupID=\"");
        sb.append(this.backupID);
        sb.append("\" backupType=\"");
        if (this.backupType != null) {
            sb.append(this.backupType.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" backupStartTime=\"");
        sb.append(new Timestamp(this.backupStartTime));
        sb.append("\" backupEndTime=\"");
        sb.append(new Timestamp(this.backupEndTime));
        sb.append("\" zipFileName=\"");
        sb.append(this.zipFileName);
        sb.append("\" backupFolder=\"");
        sb.append(this.backupFolder);
        sb.append("\" backupLabelFile=\"");
        sb.append(this.backupLabelFile);
        sb.append("\" incrementalBackupEnabled=\"");
        sb.append(this.incrementalBackupEnabled);
        sb.append("\" lastIncrementalBackupEndTime=\"");
        sb.append(new Timestamp(this.lastIncrementalBackupEndTime));
        sb.append("\" lastDataFileName=\"");
        sb.append(this.lastDataFileName);
        sb.append("\" prevBackupLastDataFileModifiedTime=\"");
        sb.append(this.prevBackupLastDataFileModifiedTime);
        sb.append("\" lastFullBackupStartTime=\"");
        sb.append(this.lastFullBackupStartTime);
        sb.append("\" fullbackup_zipname=\"");
        sb.append(this.fullbackup_zipname);
        sb.append("\" previous_incr_backup_zipnames=\"");
        sb.append(this.previous_incr_backup_zipnames);
        sb.append("\" backupLabelWaitDuration=\"");
        sb.append(this.backupLabelWaitDuration);
        sb.append("\" remoteBackupDir=\"");
        sb.append(this.remoteBackupDir);
        sb.append("\" backupMode=\"");
        if (this.backupMode != null) {
            sb.append(this.backupMode.name());
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
        sb.append("\" databaseSize \"");
        sb.append(this.databaseSize);
        sb.append("\" backupSize \"");
        sb.append(this.backupSize);
        sb.append("\" expectedBackupSize \"");
        sb.append(this.expectedBackupSize);
        if (this.archivePassword != null) {
            sb.append("\" archivePassword= \"");
            sb.append("*****");
            sb.append("\" archiveEncAlgo= \"");
            sb.append(this.archiveEncAlgo);
        }
        sb.append("\"/>");
        return sb.toString();
    }
}
