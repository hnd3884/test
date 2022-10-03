package com.adventnet.sym.server.mdm.task;

import java.util.Hashtable;
import org.json.JSONObject;
import java.util.Iterator;
import com.me.mdm.server.profiles.ios.IOSPasscodeRestrictionHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMScheduledCommandTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties taskProps) {
        try {
            MDMScheduledCommandTask.logger.log(Level.INFO, "####### MDMScheduledCommandTask : executeTask #######");
            final String commandName = ((Hashtable<K, String>)taskProps).get("commandName");
            final String resourceListStr = ((Hashtable<K, String>)taskProps).get("resourceList");
            final String[] stringsListArr = resourceListStr.replace("[", "").replace("]", "").split(", ");
            final List<String> resourceList = Arrays.asList(stringsListArr);
            MDMScheduledCommandTask.logger.log(Level.INFO, "MDMScheduledCommandTask, Command Name : {0}, ResourceList: {1}", new Object[] { commandName, resourceListStr });
            if (commandName.equalsIgnoreCase("ManagedApplicationList")) {
                final Long resourceID = Long.valueOf(resourceList.get(0));
                final List<String> appScanCommand = DeviceCommandRepository.getInstance().getAppleAppInstalledScanCommand(resourceID);
                final List<Long> commandList = new ArrayList<Long>();
                for (final String commandUUID : appScanCommand) {
                    final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
                    commandList.add(commandId);
                }
                final List<Long> commandApplicableResourceList = new ArrayList<Long>();
                commandApplicableResourceList.add(resourceID);
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, commandApplicableResourceList);
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
            else if (commandName.equalsIgnoreCase("InstallApplication")) {
                final Long resourceID = Long.valueOf(resourceList.get(0));
                final Long collectionId = ((Hashtable<K, Long>)taskProps).get("collectionId");
                MDMUtil.getInstance().wakeUpDeviceToInstallApp(resourceID, collectionId);
            }
            else if (commandName.equalsIgnoreCase("ScepStatusCheck")) {
                final Long collectionId2 = Long.valueOf(((Hashtable<K, String>)taskProps).get("COLLECTION_ID"));
                final Long resourceID2 = Long.valueOf(resourceList.get(0));
                final String commandNameToAdd = "ScepStatusCheck;Collection=" + collectionId2;
                DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceID2), commandNameToAdd);
                NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceID2), 3);
            }
            else if (commandName.equalsIgnoreCase("CheckCommandTask")) {
                Integer commandStatus = null;
                final Long resourceID2 = Long.valueOf(resourceList.get(0));
                final Long currentTime = System.currentTimeMillis();
                final int deviceNotificationStatus = NotificationHandler.getInstance().getNotificationStatus(resourceID2);
                String remarks = "dc.mdm.inv.will_be_executed_later";
                final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
                final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID2, commandId);
                if (deviceNotificationStatus == 2) {
                    final int agentType = ManagedDeviceHandler.getInstance().getAgentType(resourceID2);
                    switch (agentType) {
                        case 2:
                        case 3: {
                            remarks = "dc.db.mdm.scan.remarks.unable_to_reach_gcm";
                            break;
                        }
                        case 1:
                        case 8: {
                            remarks = "dc.db.mdm.scan.remarks.unable_to_reach_apns";
                            break;
                        }
                        case 4: {
                            remarks = "dc.db.mdm.scan.remarks.unable_to_reach_wns";
                            break;
                        }
                    }
                    commandStatus = 0;
                }
                else {
                    commandStatus = -1;
                }
                statusJSON.put("COMMAND_STATUS", (Object)commandStatus);
                statusJSON.put("REMARKS", (Object)remarks);
                statusJSON.put("UPDATED_TIME", (Object)currentTime);
                new CommandStatusHandler().populateCommandStatus(statusJSON);
            }
            else if (commandName.equalsIgnoreCase("RestrictPasscode")) {
                final Long resourceID = Long.valueOf(resourceList.get(0));
                new IOSPasscodeRestrictionHandler().checkAndAddScheduleSecurityCommand(resourceID);
            }
        }
        catch (final Exception exp) {
            MDMScheduledCommandTask.logger.log(Level.INFO, "Exception occurred during message processing : {0}", exp);
        }
    }
    
    static {
        MDMScheduledCommandTask.logger = Logger.getLogger("MDMLogger");
    }
}
