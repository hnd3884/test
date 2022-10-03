package com.adventnet.db.adapter;

public class RestoreDBParams
{
    private boolean validRestore;
    private String srcZipFile;
    private BackupRestoreConfigurations.BACKUP_MODE restoreBackupMode;
    private BackupRestoreConfigurations.BACKUP_CONTENT_TYPE restoreBackupContentType;
    private BackupRestoreConfigurations.DB_START_MODE startDBMode;
    private BackupRestoreConfigurations.INIT_PERSISTENCE initializePersistenceType;
    private BackupRestoreConfigurations.RESET_MICKEY resetMickeyType;
    private boolean isDBStopRequired;
    private String archivePassword;
    private String oldCryptTag;
    
    public String getOldCryptTag() {
        return this.oldCryptTag;
    }
    
    public void setOldCryptTag(final String oldCryptTag) {
        this.oldCryptTag = oldCryptTag;
    }
    
    public RestoreDBParams() {
        this.validRestore = true;
        this.srcZipFile = null;
        this.isDBStopRequired = false;
    }
    
    public RestoreDBParams(final String srcFile) {
        this.srcZipFile = srcFile;
        this.validRestore = true;
        this.isDBStopRequired = false;
    }
    
    public void setValid(final boolean valid) {
        this.validRestore = valid;
    }
    
    public boolean isValid() {
        return this.validRestore;
    }
    
    public void setSourceFile(final String src) {
        this.srcZipFile = src;
    }
    
    public String getSourceFile() {
        return this.srcZipFile;
    }
    
    public void setRestoreBackupMode(final BackupRestoreConfigurations.BACKUP_MODE restoreMode) {
        this.restoreBackupMode = restoreMode;
    }
    
    public BackupRestoreConfigurations.BACKUP_MODE getRestoreBackupMode() {
        return this.restoreBackupMode;
    }
    
    public void setStartDBMode(final BackupRestoreConfigurations.DB_START_MODE mode) {
        this.startDBMode = mode;
    }
    
    public BackupRestoreConfigurations.DB_START_MODE getStartDBStartMode() {
        return this.startDBMode;
    }
    
    public void setInitializePersistenceType(final BackupRestoreConfigurations.INIT_PERSISTENCE mode) {
        this.initializePersistenceType = mode;
    }
    
    public BackupRestoreConfigurations.INIT_PERSISTENCE getInitializePersistenceType() {
        return this.initializePersistenceType;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<RestoreDBParams validRestore=\"");
        sb.append(this.validRestore);
        sb.append("\" srcZipFile=\"");
        sb.append(this.srcZipFile);
        sb.append("\" restoreBackupMode=\"");
        if (this.restoreBackupMode != null) {
            sb.append(this.restoreBackupMode.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" startDBMode=\"");
        if (this.startDBMode != null) {
            sb.append(this.startDBMode.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" restoreBackupContentType=\"");
        if (this.restoreBackupContentType != null) {
            sb.append(this.restoreBackupContentType.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" initializePersistenceType=\"");
        if (this.initializePersistenceType != null) {
            sb.append(this.initializePersistenceType.name());
        }
        else {
            sb.append("null");
        }
        if (this.resetMickeyType != null) {
            sb.append(this.resetMickeyType.name());
        }
        else {
            sb.append("null");
        }
        sb.append("\" isDBStopRequired=\"");
        sb.append(this.isDBStopRequired);
        if (this.archivePassword != null) {
            sb.append("\" archivePassword=\"");
            sb.append("*****");
        }
        sb.append("\" />");
        return sb.toString();
    }
    
    public boolean isDBStopRequired() {
        return this.isDBStopRequired;
    }
    
    public void requiresDBStop(final boolean dbStop) {
        this.isDBStopRequired = dbStop;
    }
    
    public BackupRestoreConfigurations.BACKUP_CONTENT_TYPE getRestoreBackupContentType() {
        return this.restoreBackupContentType;
    }
    
    public void setRestoreBackupContentType(final BackupRestoreConfigurations.BACKUP_CONTENT_TYPE restoreBackupContentType) {
        this.restoreBackupContentType = restoreBackupContentType;
    }
    
    public BackupRestoreConfigurations.RESET_MICKEY getResetMickeyType() {
        return this.resetMickeyType;
    }
    
    public void setResetMickeyType(final BackupRestoreConfigurations.RESET_MICKEY resetMickey) {
        this.resetMickeyType = resetMickey;
    }
    
    public void setArchivePassword(final String archivePassword) {
        this.archivePassword = archivePassword;
    }
    
    public String getArchivePassword() {
        return this.archivePassword;
    }
}
