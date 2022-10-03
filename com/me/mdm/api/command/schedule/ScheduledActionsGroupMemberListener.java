package com.me.mdm.api.command.schedule;

import org.json.simple.JSONArray;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import org.json.simple.JSONObject;
import java.util.Set;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import java.util.HashSet;
import com.me.mdm.api.command.CommandFacade;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class ScheduledActionsGroupMemberListener implements MDMGroupMemberListener
{
    private final Logger logger;
    
    public ScheduledActionsGroupMemberListener() {
        this.logger = Logger.getLogger("ActionsLogger");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            final Long groupId = groupEvent.groupID;
            final List addedResourceIDs = new ArrayList(Arrays.asList(groupEvent.memberIds));
            final Long userID = groupEvent.userId;
            final Long customerID = groupEvent.customerId;
            List groupActionIDs = GroupActionToCollectionHandler.getInstance().getGroupActionIDsForGroupIDs(Collections.singletonList(groupId));
            groupActionIDs = GroupActionScheduleUtils.getUnsuspendedGroupActionIDs(groupActionIDs);
            final Map info = new HashMap();
            info.put("reason_message", "dummy");
            info.put("user_id", userID);
            info.put("group_id", groupId);
            info.put("is_group_action", true);
            final List resourceIDs = Arrays.asList(addedResourceIDs);
            for (final Long groupActionID : groupActionIDs) {
                final List validDevices = new ArrayList();
                final List validDeviceDetails = new ArrayList();
                final Long collectionID = GroupActionToCollectionHandler.getInstance().getCollectionForGroupAction(groupActionID);
                final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collectionID);
                String commandName = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
                commandName = ScheduledActionsUtils.getActionNameForCommandName(commandName);
                final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceIDs);
                if (commandName.equals("restart")) {
                    info.put("scheduled", "scheduled_restart");
                }
                else if (commandName.equals("shutdown")) {
                    info.put("scheduled", "scheduled_restart");
                }
                final Integer commandRepositoryType = 1;
                final HashMap commandMap = new CommandFacade().getPlatformBasedCommandName(commandName);
                for (final int platform : commandMap.keySet()) {
                    final HashSet deviceSet = deviceMap.get(platform);
                    final JSONArray validDevicesJSONArray = InvActionUtilProvider.getInvActionUtil(platform).getApplicableBulkActionDevices(deviceSet, commandName, customerID);
                    validDeviceDetails.addAll(Arrays.asList(validDevicesJSONArray.toArray()));
                }
                for (final JSONObject resource : validDeviceDetails) {
                    final Long resourceID = Long.valueOf(resource.get((Object)"RESOURCE_ID").toString());
                    ProfileAssociateHandler.getInstance().associateCollectionToResources(collectionID, Collections.singletonList(resourceID), customerID, userID);
                    ScheduledCollectionToResourceHandler.getInstance().addCollectionToResource(resourceID, collectionID, commandRepositoryType);
                    final Integer resourceType = ScheduledActionsUtils.getResourceTypeForResourceID(resourceID);
                    final Boolean isComputerFeatureParamEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabledInDB("SCHEDULED_ACTION_COMPUTER_DEVICE_BLOCK");
                    if (resourceType.equals(121) && !isComputerFeatureParamEnabled) {
                        continue;
                    }
                    validDevices.add(resourceID);
                }
                final Long scheduleID = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDForCollection(collectionID);
                final Integer executionType = ScheduleRepositoryHandler.getInstance().getScheduleExecutionTypeForSchedule(scheduleID);
                Long scheduleExecutionTime = ScheduledActionsUtils.getNextExecutionTimeForSchedule(scheduleID);
                Boolean isScheduledOnce = false;
                Long preScheduleExecutionTime;
                if (executionType.equals(1)) {
                    final Long preScheduleID = ScheduleMapperHandler.getInstance().getPreScheduleId(scheduleID);
                    preScheduleExecutionTime = ScheduledActionsUtils.getNextExecutionTimeForSchedule(preScheduleID);
                }
                else {
                    scheduleExecutionTime = ScheduledCommandToCollectionHandler.getInstance().getExecutionTimeForCollection(collectionID);
                    preScheduleExecutionTime = System.currentTimeMillis();
                    isScheduledOnce = true;
                }
                final List androidExcludedDevices = ScheduledActionsUtils.excludeAndroidDevicesForResourceList(validDevices);
                final Long expiry = ScheduledCommandToCollectionHandler.getInstance().getExpiryForCollection(collectionID);
                DeviceInvCommandHandler.getInstance().addOrUpdateGroupActionsCommandHistory(validDevices, commandID, userID, commandName, groupActionID, info);
                if (preScheduleExecutionTime > scheduleExecutionTime + expiry || isScheduledOnce) {
                    final Long tempCommandID = ScheduledActionsUtils.getTempCommandIDForCommandID(commandID, collectionID);
                    final Integer scheduleType = ScheduledActionsUtils.getScheduleExecutionTypeForCollection(collectionID);
                    if (scheduleExecutionTime < System.currentTimeMillis() && System.currentTimeMillis() < scheduleExecutionTime + expiry) {
                        GroupActionScheduleUtils.updateCommandHistoryStatusByGroupActionIDs(Collections.singletonList(groupActionID), androidExcludedDevices, Collections.singletonList(7), 1, "dc.mdm.general.command.initiated");
                    }
                    if (scheduleType.equals(1)) {
                        ScheduledActionsUtils.addCommmandsToDeviceForSchedule(scheduleID);
                    }
                    else {
                        final Long executionTime = ScheduledCommandToCollectionHandler.getInstance().getExecutionTimeForCollection(collectionID);
                        DeviceCommandRepository.getInstance().assignCommandToDevicesWithSlot(Collections.singletonList(tempCommandID), androidExcludedDevices, commandRepositoryType, executionTime, executionTime + expiry);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while assigning scheduled action for the devices:{0} beLonging to the groupID", new Object[] { groupEvent.memberIds, groupEvent.groupID });
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        final ScheduleCommandService scheduleCommandService = new ScheduleCommandService();
        try {
            final Long groupId = groupEvent.groupID;
            final List removedResourceIDs = new ArrayList(Arrays.asList(groupEvent.memberIds));
            final Long userID = groupEvent.userId;
            final Long customerID = groupEvent.customerId;
            final List groupActionIDs = GroupActionToCollectionHandler.getInstance().getGroupActionIDsForGroupIDs(Collections.singletonList(groupId));
            GroupActionScheduleUtils.removeDevicesFromGroupAction(Arrays.asList(removedResourceIDs), groupActionIDs);
            for (final Long groupActionID : groupActionIDs) {
                final Long collectionID = GroupActionToCollectionHandler.getInstance().getCollectionForGroupAction(groupActionID);
                ProfileAssociateHandler.getInstance().disassociateCollectionToResources(collectionID, removedResourceIDs, customerID, userID);
                scheduleCommandService.deleteScheduledCommand(collectionID, Arrays.asList(removedResourceIDs), customerID, userID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while dissociating resources from scheduled actions", e);
        }
    }
}
