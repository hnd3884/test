package com.me.mdm.server.backup;

public class BackupFileData
{
    private Long featureId;
    private String fileName;
    private Long createdTime;
    private Long fileSize;
    
    public Long getFeatureId() {
        return this.featureId;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public Long getCreatedTime() {
        return this.createdTime;
    }
    
    public Long getFileSize() {
        return this.fileSize;
    }
    
    public BackupFileData addFeatureId(final Long featureId) {
        this.featureId = featureId;
        return this;
    }
    
    public BackupFileData addFileName(final String fileName) {
        this.fileName = fileName;
        return this;
    }
    
    public BackupFileData addCreatedTime(final Long createdTime) {
        this.createdTime = createdTime;
        return this;
    }
    
    public BackupFileData addFileSize(final Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }
    
    @Override
    public String toString() {
        return "BackupFileData : {FEATURE_ID:+" + this.featureId + ";FILE_NAME:" + this.fileName + ";CREATED_TIME:" + this.createdTime + ";FILE_SIZE:" + this.fileSize + "}";
    }
}
