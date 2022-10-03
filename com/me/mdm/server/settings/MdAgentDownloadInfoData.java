package com.me.mdm.server.settings;

public class MdAgentDownloadInfoData
{
    Long customerId;
    int platformType;
    int downloadMode;
    
    public MdAgentDownloadInfoData() {
        this.customerId = -1L;
        this.platformType = -1;
        this.downloadMode = -1;
    }
    
    public MdAgentDownloadInfoData addCustomerID(final Long customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public MdAgentDownloadInfoData addPlatform(final int platform) {
        this.platformType = platform;
        return this;
    }
    
    public MdAgentDownloadInfoData addDownloadMode(final int downloadMode) {
        this.downloadMode = downloadMode;
        return this;
    }
    
    @Override
    public String toString() {
        return "CustomerId=" + this.customerId + "; Platform=" + this.platformType + "; downloadMod=" + this.downloadMode;
    }
}
