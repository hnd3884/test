package com.adventnet.sym.server.mdm.config.task;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleUsersDirectory;
import com.me.idps.core.util.DirectoryUtil;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChromeCollectionUserCommandTask implements CollectionCommandTask
{
    public static Logger profileDistributionLog;
    
    @Override
    public void installProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task initiated : {0}", collectionCommandTaskData);
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
        final List<Long> resourceToWakeup = new ArrayList<Long>();
        for (final Object resourceId : resourceList) {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID((Long)resourceId);
            if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                final JSONObject esaDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
                final String emailAddress = DirectoryUtil.getInstance().getFirstDirObjAttrValue((Long)resourceId, Long.valueOf(106L));
                final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
                userDirectory.initialize(esaDetails);
                final JSONObject userDetails = userDirectory.getUser(esaDetails, emailAddress);
                if (!userDetails.has("users")) {
                    continue;
                }
                final String userGuid = userDetails.getJSONObject("users").keys().next();
                for (final Object commandId : commandList) {
                    DeviceCommandRepository.getInstance().assignCommandToDevice((Long)commandId, userGuid);
                }
                resourceToWakeup.add((Long)resourceId);
            }
        }
        NotificationHandler.getInstance().SendNotification(resourceToWakeup, 401);
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task completed ******");
    }
    
    @Override
    public void uninstallProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallApplication");
        }
        final int collResConstant = 18;
        final Long customerId = collectionCommandTaskData.getCustomerId();
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, Arrays.asList(collectionList.get(j)), "InstallProfile");
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            final List<Long> resourceToWakeup = new ArrayList<Long>();
            for (final Object resourceId : resourceList) {
                if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                    final JSONObject esaDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
                    final String emailAddress = DirectoryUtil.getInstance().getFirstDirObjAttrValue((Long)resourceId, Long.valueOf(106L));
                    final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
                    userDirectory.initialize(esaDetails);
                    final JSONObject userDetails = userDirectory.getUser(esaDetails, emailAddress);
                    if (!userDetails.has("users")) {
                        continue;
                    }
                    final String userGuid = userDetails.getJSONObject("users").keys().next();
                    for (final Object commandId : commandList) {
                        DeviceCommandRepository.getInstance().assignCommandToDevice((Long)commandId, userGuid);
                    }
                    resourceToWakeup.add((Long)resourceId);
                }
            }
            NotificationHandler.getInstance().SendNotification(resourceToWakeup, 401);
            SeqCmdUtils.getInstance().removeSeqInstallProfileCmd(resourceList, Arrays.asList(collectionList.get(j)));
        }
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "************** Uninstall profile sub task completed *************");
    }
    
    @Override
    public void installApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, false, "RemoveApplication");
        ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallApplication");
        final Boolean isSilentInstall = collectionCommandTaskData.getTaskProperties().get("isSilentInstall") != null && ((Hashtable<K, Boolean>)collectionCommandTaskData.getTaskProperties()).get("isSilentInstall");
        final Long customerId = collectionCommandTaskData.getCustomerId();
        if (isSilentInstall) {
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, collectionCommandTaskData.getCommandName());
            final List<Long> resourceToWakeup = new ArrayList<Long>();
            for (final Object resourceId : resourceList) {
                if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                    final JSONObject esaDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
                    final String emailAddress = DirectoryUtil.getInstance().getFirstDirObjAttrValue((Long)resourceId, Long.valueOf(106L));
                    final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
                    userDirectory.initialize(esaDetails);
                    final JSONObject userDetails = userDirectory.getUser(esaDetails, emailAddress);
                    if (!userDetails.has("users")) {
                        continue;
                    }
                    final String userGuid = userDetails.getJSONObject("users").keys().next();
                    for (final Object commandId : commandList) {
                        DeviceCommandRepository.getInstance().assignCommandToDevice((Long)commandId, userGuid);
                    }
                    resourceToWakeup.add((Long)resourceId);
                }
            }
            NotificationHandler.getInstance().SendNotification(resourceToWakeup, 401);
        }
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task completed **********");
    }
    
    @Override
    public void uninstallApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task initiated : {0}", collectionCommandTaskData);
        final Long customerId = collectionCommandTaskData.getCustomerId();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallApplication");
        }
        final int collResConstant = 18;
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            final List<Long> resourceToWakeup = new ArrayList<Long>();
            for (final Object resourceId : resourceList) {
                if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                    final JSONObject esaDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
                    final String emailAddress = DirectoryUtil.getInstance().getFirstDirObjAttrValue((Long)resourceId, Long.valueOf(106L));
                    final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
                    userDirectory.initialize(esaDetails);
                    final JSONObject userDetails = userDirectory.getUser(esaDetails, emailAddress);
                    if (!userDetails.has("users")) {
                        continue;
                    }
                    final String userGuid = userDetails.getJSONObject("users").keys().next();
                    for (final Object commandId : commandList) {
                        DeviceCommandRepository.getInstance().assignCommandToDevice((Long)commandId, userGuid);
                    }
                    resourceToWakeup.add((Long)resourceId);
                }
            }
            NotificationHandler.getInstance().SendNotification(resourceToWakeup, 401);
        }
        ChromeCollectionUserCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task completed **********");
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
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 8, "--");
    }
    
    @Override
    public void removeDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 8, "--");
    }
    
    @Override
    public void installAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) {
        throw new UnsupportedOperationException("App Configuration policy not supported for chrome");
    }
    
    @Override
    public void removeAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) {
        throw new UnsupportedOperationException("App Configuration policy not supported for chrome");
    }
    
    @Override
    public void installAnnouncement(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void installScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void removeScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    static {
        ChromeCollectionUserCommandTask.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
}
