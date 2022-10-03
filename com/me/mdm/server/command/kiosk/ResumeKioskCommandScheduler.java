package com.me.mdm.server.command.kiosk;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import java.util.ArrayList;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ResumeKioskCommandScheduler implements SchedulerExecutionInterface
{
    public void executeTask(final Properties properties) {
        try {
            final Long currentlyLoggedInUserId = Long.parseLong(properties.getProperty("technicianID"));
            final HashMap infoMap = new HashMap();
            infoMap.put("isSilentCommand", true);
            infoMap.put("technicianID", currentlyLoggedInUserId);
            final Long resourceId = Long.parseLong(properties.getProperty("RESOURCE_ID"));
            final JSONObject commandHistoryData = new JSONObject();
            commandHistoryData.put("RESOURCE_ID", (Object)resourceId);
            final Long commandId = DeviceCommandRepository.getInstance().addCommand("ResumeKioskCommand");
            commandHistoryData.put("COMMAND_ID", (Object)commandId);
            commandHistoryData.put("ADDED_BY", (Object)currentlyLoggedInUserId);
            new CommandStatusHandler().populateCommandStatus(commandHistoryData);
            if (resourceId != null) {
                final ArrayList<Long> list = new ArrayList<Long>();
                list.add((long)resourceId);
                DeviceInvCommandHandler.getInstance().invokeCommand(list, "ResumeKioskCommand", infoMap);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(ResumeKioskCommandScheduler.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static void startResumeKioskCommandTimeout(final HashMap info) {
        final Properties taskProps = new Properties();
        taskProps.setProperty("TASK_TYPE", "PauseKioskCommandTimeout");
        taskProps.setProperty("RESOURCE_ID", info.get("resID") + "");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "ResumeKioskCommandScheduler" + info.get("resID"));
        taskInfoMap.put("schedulerTime", System.currentTimeMillis() + info.get("ReEnterTime") * 1000L);
        taskInfoMap.put("technicianID", info.get("technicianID"));
        taskInfoMap.put("poolName", "mdmPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.command.kiosk.ResumeKioskCommandScheduler", taskInfoMap, taskProps);
    }
}
