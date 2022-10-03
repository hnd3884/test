package com.me.mdm.server.msp.sync;

import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ConfigurationsSyncEngineHandler
{
    public static ConfigurationsSyncEngineHandler configurationsSyncEngineHandler;
    
    public static ConfigurationsSyncEngineHandler getInstance() {
        if (ConfigurationsSyncEngineHandler.configurationsSyncEngineHandler == null) {
            ConfigurationsSyncEngineHandler.configurationsSyncEngineHandler = new ConfigurationsSyncEngineHandler();
        }
        return ConfigurationsSyncEngineHandler.configurationsSyncEngineHandler;
    }
    
    public BaseConfigurationsSyncEngine getActionHandler(final DCQueueData dcQueueData) {
        BaseConfigurationsSyncEngine baseConfigurationsSyncEngine = null;
        switch (dcQueueData.queueDataType) {
            case 100: {
                baseConfigurationsSyncEngine = new ProfilePublishActionSyncHandler(dcQueueData);
                break;
            }
            case 101: {
                baseConfigurationsSyncEngine = new ProfileTrashSyncHandler(dcQueueData);
                break;
            }
            case 102: {
                baseConfigurationsSyncEngine = new ProfileDeleteSyncHandler(dcQueueData);
                break;
            }
            case 103: {
                baseConfigurationsSyncEngine = new ProfileRestoreSyncHandler(dcQueueData);
                break;
            }
            case 201: {
                baseConfigurationsSyncEngine = new NewAppAdditionSyncHandler(dcQueueData);
                break;
            }
            case 203: {
                baseConfigurationsSyncEngine = new ExistingAppVersionUpdateSyncHandler(dcQueueData);
                break;
            }
            case 202: {
                baseConfigurationsSyncEngine = new AppVersionAddedAsNewSyncHandler(dcQueueData);
                break;
            }
            case 204: {
                baseConfigurationsSyncEngine = new AppVersionApprovalSyncHandler(dcQueueData);
                break;
            }
            case 205: {
                baseConfigurationsSyncEngine = new AppsMoveToAllCustomerHandler(dcQueueData);
                break;
            }
            case 206: {
                baseConfigurationsSyncEngine = new AppConfigurationAddSyncHandler(dcQueueData);
                break;
            }
            case 208: {
                baseConfigurationsSyncEngine = new AppConfigurationDeleteSyncHandler(dcQueueData);
                break;
            }
            case 212: {
                baseConfigurationsSyncEngine = new AppVersionDeleteSyncHandler(dcQueueData);
                break;
            }
            case 210: {
                baseConfigurationsSyncEngine = new AppDeletePermanentlySyncHandler(dcQueueData);
                break;
            }
            case 209: {
                baseConfigurationsSyncEngine = new AppTrashSyncHandler(dcQueueData);
                break;
            }
            case 211: {
                baseConfigurationsSyncEngine = new AppRestoreSyncHandler(dcQueueData);
                break;
            }
        }
        return baseConfigurationsSyncEngine;
    }
    
    static {
        ConfigurationsSyncEngineHandler.configurationsSyncEngineHandler = null;
    }
}
