package com.me.mdm.server.remotesession;

import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class RemoteControlTask implements SchedulerExecutionInterface
{
    static long[] delaySeconds;
    
    public void executeTask(final Properties properties) {
        final Long resourceId = Long.parseLong(properties.getProperty("RESOURCE_ID"));
        final int index = Integer.parseInt(properties.getProperty("index"));
        final RemoteSessionManager remoteSessionManager = new RemoteSessionManager();
        final int status = remoteSessionManager.handleRemoteSessionStatusUpdate(resourceId, 11);
        if (status == 1) {
            final Properties taskProps = new Properties();
            taskProps.setProperty("TASK_TYPE", "RemoteCommandTimeout");
            taskProps.setProperty("DELAY_PERIOD", (index + 1) % 4 * 1000 + "");
            taskProps.setProperty("index", index + 1 + "");
            taskProps.setProperty("RESOURCE_ID", resourceId + "");
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "RemoteCommandTimeoutTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis() + RemoteControlTask.delaySeconds[index % 4] * 1000L);
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.remotesession.RemoteControlTask", taskInfoMap, taskProps);
        }
        else if (status == 11) {
            remoteSessionManager.handleRemoteSessionStatusUpdate(resourceId, 12);
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID("RemoteSession");
            DeviceCommandRepository.getInstance().deleteResourceCommand(commandId, resourceId);
            DeviceCommandRepository.getInstance().clearCommandsFromCacheForResources(Arrays.asList(commandId), Arrays.asList(resourceId), 1);
        }
    }
    
    public static void startRemoteCommandTimeout(final Long resID) {
        final Properties taskProps = new Properties();
        taskProps.setProperty("TASK_TYPE", "RemoteCommandTimeout");
        taskProps.setProperty("RESOURCE_ID", resID + "");
        taskProps.setProperty("index", "0");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "RemoteCommandTimeoutTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis() + 1000L);
        taskInfoMap.put("poolName", "mdmPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.remotesession.RemoteControlTask", taskInfoMap, taskProps);
    }
    
    static {
        RemoteControlTask.delaySeconds = new long[] { 15L, 15L, 15L, 15L };
    }
}
