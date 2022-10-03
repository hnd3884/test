package com.me.idps.mdmop;

import java.util.Properties;
import com.me.devicemanagement.onpremise.webclient.dblock.DbLockDetectTask;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class DirectoryDBImpl extends DCQueueDataProcessor
{
    public void processData(final DCQueueData qData) {
        new DbLockDetectTask().executeTask(new Properties());
    }
}
