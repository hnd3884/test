package com.adventnet.sym.server.mdm.queue;

public enum QueueName
{
    ENROLLMENT_DATA("enrollment-data"), 
    SECURITY_DATA("security-data"), 
    PASSIVE_EVENTS("passive-events"), 
    ACTIVE_EVENTS("active-events"), 
    ASSET_DATA("asset-data"), 
    AGENT_UPGRADE("agent-upgrade-queue"), 
    PROFILE_COLLECTION_STATUS("profile-collection-command"), 
    APP_COLLECTION_STATUS("app-collection-command"), 
    BLACKLIST_COLLECTION_STATUS("blacklist-collection-command"), 
    OSUPDATE_COLLECTION_STATUS("osupdate-collection-command"), 
    SEQUENTIAL_COMMAND("sequential-command"), 
    ENROLLMENT_DATA_ID("enrollment-data-id"), 
    DISCOVERED_APP_DATA("discovered-app"), 
    OTHERS("other-data"), 
    DEVICE_TRACKING_UPDATES("device-tracking-updates"), 
    MODERN_MGMT_ASSET_DATA("modern-mgmt-asset-data");
    
    private final String queueName;
    
    private QueueName(final String qName) {
        this.queueName = qName;
    }
    
    public String getQueueName() {
        return this.queueName;
    }
}
