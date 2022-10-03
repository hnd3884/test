package com.me.mdm.server.inv.ios.DeviceAttestation;

public class DeviceAttestationModel
{
    private boolean isSignedByApple;
    private String udid;
    private String serialNumber;
    private String osVersion;
    private String nonce;
    private Long nonceGeneratedTime;
    private Long updatedCommandTime;
    private Integer status;
    private boolean deviceInformationCommandStatus;
    
    public DeviceAttestationModel() {
        this.udid = null;
        this.serialNumber = null;
        this.osVersion = null;
        this.nonce = null;
        this.nonceGeneratedTime = null;
        this.updatedCommandTime = null;
        this.status = 0;
    }
    
    public String getUdid() {
        return this.udid;
    }
    
    public void setUdid(final String udid) {
        this.udid = udid;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getNonce() {
        return this.nonce;
    }
    
    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }
    
    public boolean isSignedByApple() {
        return this.isSignedByApple;
    }
    
    public void setSignedByApple(final boolean signedByApple) {
        this.isSignedByApple = signedByApple;
    }
    
    public Long getNonceGeneratedTime() {
        return this.nonceGeneratedTime;
    }
    
    public void setNonceGeneratedTime(final Long nonceGeneratedTime) {
        this.nonceGeneratedTime = nonceGeneratedTime;
    }
    
    public Long getUpdatedCommandTime() {
        return this.updatedCommandTime;
    }
    
    public void setUpdatedCommandTime(final Long updatedCommandTime) {
        this.updatedCommandTime = updatedCommandTime;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public boolean getDeviceInformationCommandStatus() {
        return this.deviceInformationCommandStatus;
    }
    
    public void setDeviceInformationCommandStatus(final boolean deviceInformationCommandStatus) {
        this.deviceInformationCommandStatus = deviceInformationCommandStatus;
    }
}
