package com.me.idps.core.sync.asynch;

import com.me.idps.core.IdpsPostStartupUpgradeHandler;
import com.me.idps.core.util.DirectoryResetHandler;
import org.json.simple.JSONObject;
import com.me.idps.core.util.DirQueue;

public class DirectoryAsyncProcessor extends DirQueue
{
    public boolean isParallelProcessingQueue() {
        return true;
    }
    
    public void processDirTask(final String taskType, final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final JSONObject qData) throws Exception {
        switch (taskType) {
            case "PRE_SYNC": {
                DirectoryPreSyncHandler.getInstance().preSync(qData);
                break;
            }
            case "postSyncEngine": {
                DirectoryPostSyncHandler.getInstance().handlePostSyncOps(qData);
                break;
            }
            case "RESET": {
                DirectoryResetHandler.getInstance().reset();
                break;
            }
            case "SCHEDULER_TASK": {
                DirectorySchedulerImpl.getInstance().executeTask(qData);
                break;
            }
            case "HANDLE_UPGRADE": {
                IdpsPostStartupUpgradeHandler.handleUpgrade();
                break;
            }
        }
    }
}
