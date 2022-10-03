package com.me.mdm.files;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.task.FileCheckSumCalculationTask;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class FileCheckSumAsyncTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties properties) {
        if (properties.containsKey("customerId")) {
            final Long customerId = ((Hashtable<K, Long>)properties).get("customerId");
            new FileCheckSumCalculationTask().executeTask(Arrays.asList(customerId));
        }
        else {
            new FileCheckSumCalculationTask().executeTask();
        }
        MDMUtil.deleteSyMParameter("checkSumCalculationNeeded");
    }
}
