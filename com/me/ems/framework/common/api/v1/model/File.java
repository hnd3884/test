package com.me.ems.framework.common.api.v1.model;

public class File
{
    Long fileID;
    String fileName;
    Long customerID;
    String expiryDate;
    Integer fileStatus;
    
    public File() {
    }
    
    public File(final Long fileID, final String fileName, final Long customerID, final String expiryDate, final Integer fileStatus) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.customerID = customerID;
        this.expiryDate = expiryDate;
        this.fileStatus = fileStatus;
    }
    
    public Long getFileID() {
        return this.fileID;
    }
    
    public void setFileID(final Long fileID) {
        this.fileID = fileID;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public Long getCustomerID() {
        return this.customerID;
    }
    
    public void setCustomerID(final Long customerID) {
        this.customerID = customerID;
    }
    
    public String getExpiryDate() {
        return this.expiryDate;
    }
    
    public void setExpiryDate(final String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Integer getFileStatus() {
        return this.fileStatus;
    }
    
    public void setFileStatus(final Integer fileStatus) {
        this.fileStatus = fileStatus;
    }
}
