package com.adventnet.sym.server.mdm.queue;

public class MDMDataQueueUtil
{
    private static MDMDataQueueUtil instance;
    
    public static MDMDataQueueUtil getInstance() {
        return MDMDataQueueUtil.instance;
    }
    
    public String getPlatformNameForLogging(final int queueDataType) {
        String platformType = null;
        switch (queueDataType) {
            case 101:
            case 102:
            case 121:
            case 122: {
                platformType = "android";
                break;
            }
            case 124: {
                platformType = "NativeAndroid";
                break;
            }
            case 100:
            case 107:
            case 120:
            case 140: {
                platformType = "iOS";
                break;
            }
            case 103:
            case 123:
            case 143: {
                platformType = "windows";
                break;
            }
            case 106:
            case 126: {
                platformType = "Chrome";
                break;
            }
            case 105:
            case 125: {
                platformType = "AdminAgent";
                break;
            }
            default: {
                platformType = "Others";
                break;
            }
        }
        return platformType;
    }
    
    public int getQueueIdFromName(final String name) {
        if (name.equalsIgnoreCase(QueueName.ENROLLMENT_DATA.getQueueName())) {
            return MDMQueueConstants.ENROLLMENT_DATA_ID;
        }
        if (name.equalsIgnoreCase(QueueName.ASSET_DATA.getQueueName())) {
            return MDMQueueConstants.ASSET_DATA_ID;
        }
        if (name.equalsIgnoreCase(QueueName.SECURITY_DATA.getQueueName())) {
            return MDMQueueConstants.SECURITY_DATA_ID;
        }
        if (name.equalsIgnoreCase(QueueName.PASSIVE_EVENTS.getQueueName())) {
            return MDMQueueConstants.PASSIVE_EVENTS_ID;
        }
        if (name.equalsIgnoreCase(QueueName.ACTIVE_EVENTS.getQueueName())) {
            return MDMQueueConstants.ACTIVE_EVENTS_ID;
        }
        if (name.equalsIgnoreCase(QueueName.PROFILE_COLLECTION_STATUS.getQueueName())) {
            return MDMQueueConstants.PROFILE_COLLECTION_STATUS_ID;
        }
        if (name.equalsIgnoreCase(QueueName.APP_COLLECTION_STATUS.getQueueName())) {
            return MDMQueueConstants.APP_COLLECTION_STATUS_ID;
        }
        if (name.equalsIgnoreCase(QueueName.BLACKLIST_COLLECTION_STATUS.getQueueName())) {
            return MDMQueueConstants.BLACKLIST_COLLECTION_STATUS_ID;
        }
        if (name.equalsIgnoreCase(QueueName.OSUPDATE_COLLECTION_STATUS.getQueueName())) {
            return MDMQueueConstants.OSUPDATE_COLLECTION_STATUS_ID;
        }
        if (name.equalsIgnoreCase(QueueName.AGENT_UPGRADE.getQueueName())) {
            return MDMQueueConstants.AGENT_UPGRADE_ID;
        }
        return MDMQueueConstants.OTHERS_ID;
    }
    
    public String getQueueNameFromId(final int queueId) {
        if (queueId == MDMQueueConstants.ENROLLMENT_DATA_ID) {
            return QueueName.ENROLLMENT_DATA.getQueueName();
        }
        if (queueId == MDMQueueConstants.ASSET_DATA_ID) {
            return QueueName.ASSET_DATA.getQueueName();
        }
        if (queueId == MDMQueueConstants.SECURITY_DATA_ID) {
            return QueueName.SECURITY_DATA.getQueueName();
        }
        if (queueId == MDMQueueConstants.ACTIVE_EVENTS_ID) {
            return QueueName.ACTIVE_EVENTS.getQueueName();
        }
        if (queueId == MDMQueueConstants.PASSIVE_EVENTS_ID) {
            return QueueName.PASSIVE_EVENTS.getQueueName();
        }
        if (queueId == MDMQueueConstants.PROFILE_COLLECTION_STATUS_ID) {
            return QueueName.PROFILE_COLLECTION_STATUS.getQueueName();
        }
        if (queueId == MDMQueueConstants.APP_COLLECTION_STATUS_ID) {
            return QueueName.APP_COLLECTION_STATUS.getQueueName();
        }
        if (queueId == MDMQueueConstants.BLACKLIST_COLLECTION_STATUS_ID) {
            return QueueName.BLACKLIST_COLLECTION_STATUS.getQueueName();
        }
        if (queueId == MDMQueueConstants.OSUPDATE_COLLECTION_STATUS_ID) {
            return QueueName.OSUPDATE_COLLECTION_STATUS.getQueueName();
        }
        if (queueId == MDMQueueConstants.AGENT_UPGRADE_ID) {
            return QueueName.AGENT_UPGRADE.getQueueName();
        }
        return QueueName.OTHERS.getQueueName();
    }
    
    static {
        MDMDataQueueUtil.instance = new MDMDataQueueUtil();
    }
}
