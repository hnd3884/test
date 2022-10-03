package com.adventnet.db.archive;

import java.util.logging.Logger;
import java.io.Serializable;

public class ArchivePolicyInfo implements Serializable, Cloneable
{
    private Long archivePolicyID;
    private String archivePolicyName;
    private String tableName;
    private String archiveMode;
    private Long threshold;
    private Long dataSourceID;
    private String criteriaString;
    private boolean isBackupEnabled;
    private int rotateArchive;
    private String archivePattern;
    private ArchiveNotificationHandler notificationHandler;
    static final Logger LOGGER;
    
    public ArchivePolicyInfo(final Long archivePolicyID, final String archivePolicyName, final String tableName, final String criteriaString, final Long threshold, final String archiveMode, final Long dataSourceID, final boolean isBackupEnabled, final int rotateArchive, final String archivePattern) {
        this(archivePolicyID, archivePolicyName, tableName, criteriaString, threshold, archiveMode, dataSourceID, isBackupEnabled, rotateArchive, archivePattern, null);
    }
    
    public ArchivePolicyInfo(final Long archivePolicyID, final String archivePolicyName, final String tableName, final String criteriaString, final Long threshold, final String archiveMode, final Long dataSourceID, final boolean isBackupEnabled, final int rotateArchive, final String archivePattern, final ArchiveNotificationHandler handler) {
        this.archivePolicyID = null;
        this.archivePolicyName = null;
        this.tableName = null;
        this.archiveMode = "PULL";
        this.threshold = null;
        this.dataSourceID = null;
        this.criteriaString = "";
        this.isBackupEnabled = false;
        this.rotateArchive = -1;
        this.archivePattern = null;
        this.notificationHandler = null;
        this.archivePolicyID = archivePolicyID;
        this.archivePolicyName = archivePolicyName;
        this.tableName = tableName;
        this.archiveMode = archiveMode;
        this.threshold = threshold;
        this.dataSourceID = dataSourceID;
        this.criteriaString = criteriaString;
        this.isBackupEnabled = isBackupEnabled;
        this.rotateArchive = rotateArchive;
        this.archivePattern = archivePattern;
        this.notificationHandler = handler;
    }
    
    public ArchivePolicyInfo() {
        this.archivePolicyID = null;
        this.archivePolicyName = null;
        this.tableName = null;
        this.archiveMode = "PULL";
        this.threshold = null;
        this.dataSourceID = null;
        this.criteriaString = "";
        this.isBackupEnabled = false;
        this.rotateArchive = -1;
        this.archivePattern = null;
        this.notificationHandler = null;
    }
    
    public void setArchivePolicyID(final Long policyID) {
        this.archivePolicyID = policyID;
    }
    
    public Long getArchivePolicyID() {
        return this.archivePolicyID;
    }
    
    public void setArchivePolicyName(final String policyName) {
        this.archivePolicyName = policyName;
    }
    
    public String getArchivePolicyName() {
        return this.archivePolicyName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setArchiveMode(final String mode) {
        this.archiveMode = mode;
    }
    
    public String getArchiveMode() {
        return this.archiveMode;
    }
    
    public void setThreshold(final Long value) {
        this.threshold = value;
    }
    
    public Long getThreshold() {
        return this.threshold;
    }
    
    public void setCriteria(final String criteria) {
        this.criteriaString = criteria;
    }
    
    public String getCriteriaString() {
        return this.criteriaString;
    }
    
    public void setBackupEnabled(final boolean status) {
        this.isBackupEnabled = status;
    }
    
    public Boolean isBackupEnabled() {
        return this.isBackupEnabled;
    }
    
    public void setRotationCount(final int count) {
        this.rotateArchive = count;
    }
    
    public int getRotationCount() {
        return this.rotateArchive;
    }
    
    public void setArchivePattern(final String pattern) {
        this.archivePattern = pattern;
    }
    
    public String getArchivePattern() {
        return this.archivePattern;
    }
    
    public void setDataSourceID(final Long sourceID) {
        this.dataSourceID = sourceID;
    }
    
    public Long getDataSourceID() {
        return this.dataSourceID;
    }
    
    public void setNotificationHandler(final ArchiveNotificationHandler handler) {
        this.notificationHandler = handler;
    }
    
    public ArchiveNotificationHandler getNotificationHandler() {
        return this.notificationHandler;
    }
    
    @Override
    public String toString() {
        return "\n=================================\nID : " + this.archivePolicyID + "\nName : " + this.archivePolicyName + "\nTable Name : " + this.tableName + "\nCriteria : " + this.criteriaString + "\nThreshold : " + this.threshold + "\nMode : " + this.archiveMode + "\nData Source ID : " + this.dataSourceID + "\n=================================";
    }
    
    public Object clone() {
        return new ArchivePolicyInfo(this.archivePolicyID, this.archivePolicyName, this.tableName, this.criteriaString, this.threshold, this.archiveMode, this.dataSourceID, this.isBackupEnabled, this.rotateArchive, this.archivePattern);
    }
    
    @Override
    public boolean equals(final Object obj) {
        ArchivePolicyInfo archivePolicy = null;
        if (!(obj instanceof ArchivePolicyInfo)) {
            return false;
        }
        archivePolicy = (ArchivePolicyInfo)obj;
        if (this.archivePolicyID != null) {
            if (archivePolicy.getArchivePolicyID() == null || !this.archivePolicyID.equals(archivePolicy.getArchivePolicyID())) {
                return false;
            }
        }
        else if (archivePolicy.getArchivePolicyID() != null) {
            return false;
        }
        if (this.archivePolicyName != null) {
            if (archivePolicy.getArchivePolicyName() == null || !this.archivePolicyName.equals(archivePolicy.getArchivePolicyName())) {
                return false;
            }
        }
        else if (archivePolicy.getArchivePolicyName() != null) {
            return false;
        }
        if (archivePolicy.getArchiveMode() == null || !this.archiveMode.equals(archivePolicy.getArchiveMode())) {
            return false;
        }
        if (this.archivePattern != null) {
            if (archivePolicy.getArchivePattern() == null || !this.archivePattern.equals(archivePolicy.getArchivePattern())) {
                return false;
            }
        }
        else if (archivePolicy.getArchivePattern() != null) {
            return false;
        }
        if (archivePolicy.getCriteriaString() == null || !this.criteriaString.equals(archivePolicy.getCriteriaString())) {
            return false;
        }
        if (this.dataSourceID != null) {
            if (archivePolicy.getDataSourceID() == null || !this.dataSourceID.equals(archivePolicy.getDataSourceID())) {
                return false;
            }
        }
        else if (archivePolicy.getDataSourceID() != null) {
            return false;
        }
        if (this.tableName != null) {
            if (archivePolicy.getTableName() == null || !this.tableName.equals(archivePolicy.getTableName())) {
                return false;
            }
        }
        else if (archivePolicy.getTableName() != null) {
            return false;
        }
        if (this.threshold != null) {
            if (archivePolicy.getThreshold() == null || !this.threshold.equals(archivePolicy.getThreshold())) {
                return false;
            }
        }
        else if (archivePolicy.getThreshold() != null) {
            return false;
        }
        if (this.notificationHandler != null) {
            if (archivePolicy.getNotificationHandler() == null || !this.notificationHandler.equals(archivePolicy.getNotificationHandler())) {
                return false;
            }
        }
        else if (archivePolicy.getNotificationHandler() != null) {
            return false;
        }
        return this.rotateArchive == archivePolicy.getRotationCount();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 5 + ((this.archivePolicyID != null) ? this.archivePolicyID.hashCode() : 0);
        hash = hash * 13 + ((this.archivePolicyName != null) ? this.archivePolicyName.hashCode() : 0);
        return hash;
    }
    
    static {
        LOGGER = Logger.getLogger(ArchiveTableInfo.class.getName());
    }
}
