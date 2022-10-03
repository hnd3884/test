package com.adventnet.sym.server.mdm.config.task;

import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChromeCollectionDeviceCommandTask implements CollectionCommandTask
{
    public static Logger profileDistributionLog;
    
    @Override
    public void installProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionDeviceCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallProfile");
            SeqCmdUtils.getInstance().removeEarlierVersionSeqProfileCommand(resourceList, profileCollectionMap);
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList.get(j), collResConstant, remark);
        }
        final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, collectionCommandTaskData.getCommandName());
        final JSONObject seqCmdParams = new JSONObject();
        seqCmdParams.put("UserId", (Object)collectionCommandTaskData.getLoggedInUserId());
        SeqCmdRepository.getInstance().executeSequentially(resourceList, commandList, seqCmdParams);
        DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
        NotificationHandler.getInstance().SendNotification(resourceList, 4);
        ChromeCollectionDeviceCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task completed ******");
    }
    
    @Override
    public void uninstallProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionDeviceCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallProfile");
        }
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, collectionList, "InstallProfile");
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant, remark);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            NotificationHandler.getInstance().SendNotification(clonedResourceList, 4);
            SeqCmdUtils.getInstance().removeSeqInstallProfileCmd(resourceList, Arrays.asList(collectionList.get(j)));
        }
        ChromeCollectionDeviceCommandTask.profileDistributionLog.log(Level.INFO, "************** Uninstall profile sub task completed *************");
    }
    
    @Override
    public void installApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void uninstallApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void blackListApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void removeBlacklisting(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void installDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void removeDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void installAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void removeAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void installAnnouncement(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void installScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionDeviceCommandTask.profileDistributionLog.log(Level.INFO, "No command Is added for resource as it is not agent delegated");
    }
    
    @Override
    public void removeScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionDeviceCommandTask.profileDistributionLog.log(Level.INFO, "No command Is added for resource as it is not agent delegated");
    }
    
    static {
        ChromeCollectionDeviceCommandTask.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
}
