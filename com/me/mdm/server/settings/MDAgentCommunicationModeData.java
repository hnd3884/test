package com.me.mdm.server.settings;

public class MDAgentCommunicationModeData
{
    Long customerId;
    int platform;
    int communicationMode;
    
    public MDAgentCommunicationModeData() {
        this.customerId = -1L;
        this.platform = -1;
        this.communicationMode = -1;
    }
    
    public MDAgentCommunicationModeData addCustomerID(final Long customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public MDAgentCommunicationModeData addPlatform(final int platform) {
        this.platform = platform;
        return this;
    }
    
    public MDAgentCommunicationModeData addCommunicationMode(final int communicationMode) {
        this.communicationMode = communicationMode;
        return this;
    }
    
    @Override
    public String toString() {
        return "CustomerId=" + this.customerId + "; Platform=" + this.platform + "; communicationMode=" + this.communicationMode;
    }
}
