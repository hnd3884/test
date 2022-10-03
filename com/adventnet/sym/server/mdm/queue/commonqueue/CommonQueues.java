package com.adventnet.sym.server.mdm.queue.commonqueue;

public enum CommonQueues
{
    MDM_ENROLLMENT("mdm-enrollment-common"), 
    MDM_APP_MGMT("mdm-app-mgmt-common"), 
    MDM_PROFILE_MGMT("mdm-profile-mgmt-common"), 
    MDM_MAILTASK("mdm-mailtask");
    
    private final String queueName;
    
    private CommonQueues(final String qName) {
        this.queueName = qName;
    }
    
    public String getQueueName() {
        return this.queueName;
    }
}
