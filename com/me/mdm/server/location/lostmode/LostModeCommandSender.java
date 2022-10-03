package com.me.mdm.server.location.lostmode;

import java.util.HashMap;
import com.adventnet.sym.server.mdm.security.ResetPasscodeHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.notification.WakeUpProcessor;
import java.util.Collection;
import java.util.logging.Level;
import java.util.Set;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.JSONObject;
import java.util.List;

public class LostModeCommandSender
{
    private Long userId;
    private List<Long> resourceList;
    private int platform;
    private JSONObject lostModeData;
    Logger logger;
    
    public LostModeCommandSender(final Long resourceId, final int platform, final Long userId) {
        this.lostModeData = null;
        this.logger = Logger.getLogger("MDMLogger");
        final List<Long> resList = new ArrayList<Long>();
        resList.add(resourceId);
        this.resourceList = resList;
        this.platform = platform;
        this.userId = userId;
    }
    
    public LostModeCommandSender(final List<Long> resourceList, final int platform, final Long userId) {
        this.lostModeData = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.resourceList = resourceList;
        this.platform = platform;
        this.userId = userId;
    }
    
    public LostModeCommandSender(final Long resourceId, final int platform, final JSONObject lostModeData, final Long userId) {
        this.lostModeData = null;
        this.logger = Logger.getLogger("MDMLogger");
        final List<Long> resList = new ArrayList<Long>();
        resList.add(resourceId);
        this.resourceList = resList;
        this.platform = platform;
        this.lostModeData = lostModeData;
        this.userId = userId;
    }
    
    public LostModeCommandSender(final List<Long> resourceList, final int platform, final JSONObject lostModeData, final Long userId) {
        this.lostModeData = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.resourceList = resourceList;
        this.platform = platform;
        this.lostModeData = lostModeData;
        this.userId = userId;
    }
    
    public void sendEnableLostModeCommand() throws Exception {
        if (this.platform == 1) {
            this.sendEnableLostModeIOSCommand();
        }
    }
    
    public void sendDeviceLocationCommand() throws Exception {
        if (this.platform == 1) {
            this.sendDeviceLocationIOSCommand();
        }
        else if (this.platform == 2) {
            final DeviceDetails device = new DeviceDetails(this.resourceList.get(0));
            final int platFormBasedOnResourceId = device.platform;
            if (platFormBasedOnResourceId == 2) {
                final DeviceCommand deviceCommand = new DeviceCommand();
                deviceCommand.commandType = "GetLocation";
                deviceCommand.commandUUID = "GetLocationForLostDevice";
                final Long commandId = DeviceCommandRepository.getInstance().addCommand(deviceCommand.commandUUID, deviceCommand.commandType);
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, this.resourceList);
                NotificationHandler.getInstance().SendNotification(this.resourceList, 2);
                DeviceInvCommandHandler.getInstance().addOrUpdateCommandInitiatedCommandHistory(this.resourceList.get(0), commandId, this.userId, "GetLocation");
            }
        }
    }
    
    public void sendDeviceInformationCommand() throws Exception {
        if (this.platform == 1) {
            this.sendDeviceInformationIOSCommand();
        }
        else if (this.platform == 2) {
            this.sendDeviceInformationAndroidCommand();
        }
    }
    
    public void sendDisableLostModeCommand() throws Exception {
        if (this.platform == 1) {
            final boolean isSupervised = ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(this.resourceList.get(0));
            if (isSupervised) {
                this.sendDisableLostModeIOSCommand();
            }
        }
        else if (this.platform == 2) {
            this.sendDisableLostModeAndroidCommand();
        }
        else if (this.platform == 4) {
            DeviceCommandRepository.getInstance().addDisableLostModeCommand(this.resourceList);
        }
    }
    
    public void sendBulkDisableLostModeCommand() throws Exception {
        if (this.platform == 1) {
            final Set<Long> supervisedAnd9_3AboveDevices = ManagedDeviceHandler.getInstance().fetchSupervisedAnd9_3Above(this.resourceList);
            if (supervisedAnd9_3AboveDevices.size() > 0) {
                this.sendBulkDisableLostModeIOSCommand(supervisedAnd9_3AboveDevices);
            }
        }
        else if (this.platform == 2) {
            this.sendBulkDisableLostModeAndroidCommand();
        }
        else if (this.platform == 4) {
            DeviceCommandRepository.getInstance().addDisableLostModeCommand(this.resourceList);
        }
    }
    
    public void addLostMode() throws Exception {
        this.addEnableLostModeCommand();
    }
    
    public void addBulkLostMode() throws Exception {
        this.addBulkEnableLostModeCommand();
    }
    
    public void modifyLostMode() throws Exception {
        this.modifyLostModeCommand();
    }
    
    private Long addEnableLostModeCommand() throws Exception {
        if (this.platform == 1) {
            return this.addEnableLostModeIOSCommand();
        }
        if (this.platform == 2) {
            this.addEnableLostModeAndroidCommand();
        }
        else if (this.platform == 4) {
            this.androidEnableLostModeCommand(this.resourceList);
        }
        return -1L;
    }
    
    private Long addBulkEnableLostModeCommand() throws Exception {
        if (this.platform == 1) {
            return this.addBulkEnableLostModeIOSCommand();
        }
        if (this.platform == 2) {
            this.addBulkEnableLostModeAndroidCommand();
        }
        else if (this.platform == 4) {
            this.androidEnableLostModeCommand(this.resourceList);
        }
        return -1L;
    }
    
    private void modifyLostModeCommand() throws Exception {
        if (this.platform == 1) {
            this.addEnableLostModeIOSCommand();
        }
        else if (this.platform == 2) {
            this.androidEnableLostModeCommand(this.resourceList);
        }
    }
    
    public void postLostModeActivationCommands() {
        try {
            this.sendDeviceLocationCommand();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while postLostModeActivationCommands", ex);
        }
    }
    
    private void sendDeviceInformationIOSCommand() throws Exception {
        DeviceCommandRepository.getInstance().addDeviceInformationCommand(this.resourceList);
        NotificationHandler.getInstance().SendNotification(this.resourceList, 1);
    }
    
    private void sendDeviceInformationAndroidCommand() throws Exception {
        DeviceCommandRepository.getInstance().addAndroidDeviceInformationCommand(this.resourceList);
        NotificationHandler.getInstance().SendNotification(this.resourceList, NotificationHandler.getNotificationType(this.platform));
    }
    
    private Long addEnableLostModeIOSCommand() throws Exception {
        DeviceInvCommandHandler.getInstance().scanDevice(this.resourceList, null);
        if (this.lostModeData.has("COMMAND_UUID")) {
            final String commandUUID = String.valueOf(this.lostModeData.get("COMMAND_UUID"));
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, this.resourceList);
            return commandId;
        }
        return DeviceCommandRepository.getInstance().addEnableLostModeCommand(this.resourceList);
    }
    
    private Long addBulkEnableLostModeIOSCommand() throws Exception {
        DeviceInvCommandHandler.getInstance().scanDevice(this.resourceList, null);
        if (this.lostModeData.has("COMMAND_UUID")) {
            final String commandUUID = String.valueOf(this.lostModeData.get("COMMAND_UUID"));
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, this.resourceList);
            return commandId;
        }
        return DeviceCommandRepository.getInstance().addEnableLostModeCommand(this.resourceList);
    }
    
    private void sendEnableLostModeIOSCommand() throws Exception {
        this.addEnableLostModeIOSCommand();
        NotificationHandler.getInstance().SendNotification(this.resourceList, 1);
    }
    
    private void sendDisableLostModeIOSCommand() throws Exception {
        DeviceCommandRepository.getInstance().addDisableLostModeCommand(this.resourceList);
        NotificationHandler.getInstance().SendNotification(this.resourceList, 1);
    }
    
    private void sendBulkDisableLostModeIOSCommand(final Set resourceIds) throws Exception {
        final ArrayList<Long> resList = new ArrayList<Long>();
        resList.addAll(resourceIds);
        DeviceCommandRepository.getInstance().addDisableLostModeCommand(resList);
        NotificationHandler.getInstance().SendNotification(resList, 1);
    }
    
    private void sendDeviceLocationIOSCommand() throws Exception {
        DeviceCommandRepository.getInstance().addLostModeDeviceLocationCommand(this.resourceList);
        NotificationHandler.getInstance().SendNotification(this.resourceList, 1);
        WakeUpProcessor.wakeUpAsynchronously(1, 1, this.resourceList, 30000);
        DeviceInvCommandHandler.getInstance().addOrUpdateCommandInitiatedCommandHistory(this.resourceList.get(0), DeviceCommandRepository.getInstance().getCommandID("GetLocationForLostDevice"), this.userId, "GetLocation");
    }
    
    private void sendDisableLostModeAndroidCommand() throws Exception {
        DeviceCommandRepository.getInstance().addDisableLostModeCommand(this.resourceList);
    }
    
    private void sendBulkDisableLostModeAndroidCommand() throws Exception {
        DeviceCommandRepository.getInstance().addDisableLostModeCommand(this.resourceList);
    }
    
    private void scheduleDeviceLocationIOSCommand() {
    }
    
    public void scheduleDeviceLocationCommand() {
    }
    
    private void addEnableLostModeAndroidCommand() throws Exception {
        this.androidEnableLostModeCommand(this.resourceList);
        this.addAndroidResetPasscodeCommand(this.resourceList);
    }
    
    private void addBulkEnableLostModeAndroidCommand() throws Exception {
        this.androidEnableLostModeCommand(this.resourceList);
        this.addAndroidResetPasscodeCommand(this.resourceList);
    }
    
    private void addAndroidPostLostModeCommands() throws Exception {
    }
    
    public void handleAndroidPostLostModeCommandsForOlderAgent() throws Exception {
        final DeviceDetails device = new DeviceDetails(this.resourceList.get(0));
        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(this.resourceList);
        DeviceInvCommandHandler.getInstance().sendCommandToDevice(device, "DeviceLock", this.userId);
        final DeviceCommand deviceCommand = new DeviceCommand();
        deviceCommand.commandType = "GetLocation";
        deviceCommand.commandUUID = "GetLocationForLostDevice";
        final Long commandId = DeviceCommandRepository.getInstance().addCommand(deviceCommand.commandUUID, deviceCommand.commandType);
        DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, this.resourceList);
        NotificationHandler.getInstance().SendNotification(this.resourceList, 2);
        DeviceInvCommandHandler.getInstance().addOrUpdateCommandInitiatedCommandHistory(this.resourceList.get(0), commandId, this.userId, "GetLocation");
    }
    
    private void androidEnableLostModeCommand(final List resourceList) throws Exception {
        if (this.lostModeData.has("COMMAND_UUID")) {
            final String commandUUID = String.valueOf(this.lostModeData.get("COMMAND_UUID"));
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList);
        }
        else {
            DeviceCommandRepository.getInstance().addEnableLostModeCommand(resourceList);
        }
    }
    
    private void androidLocationCommand(final List resourceList) throws Exception {
        final DeviceDetails device = new DeviceDetails(resourceList.get(0).toString());
        DeviceInvCommandHandler.getInstance().sendCommandToDevice(device, "GetLocation", this.userId);
    }
    
    private void androidResetPasswordCommand(final List resourceList) throws Exception {
        final HashMap privacyJson = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceList.get(0));
        final int clearPasscodePrivacy = Integer.parseInt(privacyJson.get("disable_clear_passcode").toString());
        if (clearPasscodePrivacy == 0) {
            final JSONObject resetPasswordData = new JSONObject();
            resetPasswordData.put("RESOURCE_ID", resourceList.get(0));
            resetPasswordData.put("PASSCODE", (Object)this.lostModeData.optString("PASSCODE"));
            resetPasswordData.put("EMAIL_SENT_TO_USER", this.lostModeData.optBoolean("EMAIL_SENT_TO_USER"));
            resetPasswordData.put("EMAIL_SENT_TO_ADMIN", this.lostModeData.optBoolean("EMAIL_SENT_TO_ADMIN", false));
            Long userId = null;
            if (this.lostModeData.has("ADDED_BY")) {
                userId = this.lostModeData.getLong("ADDED_BY");
            }
            try {
                if (userId == null) {
                    this.logger.log(Level.INFO, "getting user ID from api thread");
                    userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception in androidResetPasswordCommand: ", e);
            }
            if (userId == null) {
                this.logger.log(Level.INFO, "getting SYSTEM USER");
                userId = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
            }
            resetPasswordData.put("UPDATED_BY", (Object)userId);
            resetPasswordData.put("UPDATED_TIME", System.currentTimeMillis());
            new ResetPasscodeHandler().addorUpdateDevicePasscode(resetPasswordData);
            try {
                final DeviceDetails device = new DeviceDetails(resourceList.get(0));
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(device, "ResetPasscode", userId);
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception in androidResetPasswordCommand: ", ex);
            }
        }
        else {
            this.logger.log(Level.INFO, "androidResetPasswordCommand: No access to perform ResetPasscode on Resource {0}.. ", resourceList.get(0));
        }
    }
    
    private void addAndroidResetPasscodeCommand(final List resourceList) {
        try {
            final String passcode = this.lostModeData.optString("PASSCODE", (String)null);
            if (passcode != null && !passcode.trim().isEmpty()) {
                this.androidResetPasswordCommand(resourceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while adding ResetPasscode command for android in lost mode");
        }
    }
}
