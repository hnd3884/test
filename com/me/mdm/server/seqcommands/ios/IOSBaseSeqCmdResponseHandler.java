package com.me.mdm.server.seqcommands.ios;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.apps.MDDeviceInstalledAppsHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.api.MdmInvDataProcessor;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import org.json.JSONArray;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.BaseSeqCmdResponseHandler;

public class IOSBaseSeqCmdResponseHandler extends BaseSeqCmdResponseHandler
{
    protected static final Logger LOGGER;
    protected static final String APP_INSTALLATION_STATUS = "AppInstallationStatus";
    private List commandTypeList;
    
    public IOSBaseSeqCmdResponseHandler() {
        this.commandTypeList = new ArrayList();
    }
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        IOSBaseSeqCmdResponseHandler.LOGGER.log(Level.FINE, "IOS Base Seq Response Success Handler. Params:{0}", params);
        final Long resourceID = params.optLong("resourceID");
        final String CommandUUID = params.optString("commandUUID");
        final JSONObject currentParams = params.optJSONObject("CurCmdParam");
        final boolean isNeedToRemove = currentParams.optBoolean("isNeedToRemove");
        if (isNeedToRemove) {
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(CommandUUID);
            DeviceCommandRepository.getInstance().deleteResourceCommand(commandID, resourceID);
        }
        IOSBaseSeqCmdResponseHandler.LOGGER.log(Level.FINE, "IOS Base Seq Response Handler Completes");
        return super.onSuccess(params);
    }
    
    @Override
    public JSONObject processLater(final JSONObject params) throws Exception {
        final JSONObject currentCommandParams = params.getJSONObject("CurCmdParam");
        final Long timeOffset = currentCommandParams.optLong("timeout");
        final JSONObject kioskLater = new JSONObject();
        kioskLater.put("taskClass", (Object)"com.me.mdm.server.seqcommands.ios.IOSProcessSequentialCommandScheduler");
        kioskLater.put("timeOffset", (Object)timeOffset);
        return kioskLater;
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        try {
            final int order = sequentialSubCommand.order;
            if (order == 1) {
                this.addCommandNeedsToBeQueued(sequentialSubCommand.params, resourceID);
                this.addCommandScopeParams(sequentialSubCommand, resourceID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in processing sub command proprocessor", e);
        }
        return true;
    }
    
    protected void addCommandNeedsToBeQueued(final JSONObject params, final Long resourceId) throws Exception {
        JSONObject commandLevelParams = params.optJSONObject("CommandLevelParams");
        JSONObject commandScopeParams = params.optJSONObject("cmdScopeParams");
        if (commandScopeParams == null) {
            commandScopeParams = new JSONObject();
            params.put("cmdScopeParams", (Object)commandScopeParams);
        }
        if (commandLevelParams == null) {
            commandLevelParams = new JSONObject();
        }
        final boolean isClearPasscodeCommand = commandLevelParams.optBoolean("isClearPasscodeCommand");
        final JSONArray queuedCommands = new JSONArray();
        final JSONArray commandArray = SeqCmdUtils.getInstance().getNextSubCommandsDetailsInQueueForResource(resourceId);
        for (int i = 0; i < commandArray.length(); ++i) {
            final JSONObject command = commandArray.getJSONObject(i);
            final String commandType = command.getString("COMMAND_TYPE");
            this.commandTypeList.add(commandType);
        }
        final boolean isManagedSettingCommand = this.commandTypeList.contains("InstallManagedSettings");
        final boolean isRestrictionStatus = commandLevelParams.optBoolean("RestrictionProfileStatus");
        final boolean isSharedDeviceRestriction = commandLevelParams.optBoolean("SharedDeviceRestrictions");
        final List<String> commandRequestTypeList = new ArrayList<String>();
        if (isManagedSettingCommand || isClearPasscodeCommand) {
            final boolean isSupervisedDevice = InventoryUtil.getInstance().isSupervisedDevice(resourceId);
            commandScopeParams.put("isSupervised", isSupervisedDevice);
        }
        if (isClearPasscodeCommand) {
            final boolean isSupervisedDevice = commandScopeParams.getBoolean("isSupervised");
            if (!isSupervisedDevice) {
                commandRequestTypeList.add("ClearPasscodeForPasscodeRestriction");
            }
        }
        if (isManagedSettingCommand || isSharedDeviceRestriction) {
            final HashMap deviceDetails = MdmInvDataProcessor.getInstance().getDeviceDetails(resourceId, new HashMap());
            commandScopeParams.put("OS_VERSION", deviceDetails.get("OS_VERSION"));
            if (isManagedSettingCommand) {
                final Integer cellularTechnology = deviceDetails.get("CELLULAR_TECHNOLOGY");
                commandScopeParams.put("cellularTechnology", (Object)cellularTechnology);
            }
            if (isSharedDeviceRestriction) {
                final boolean isMultiuser = deviceDetails.get("IS_MULTIUSER");
                commandScopeParams.put("IS_MULTIUSER", isMultiuser);
                if (!isMultiuser) {
                    commandRequestTypeList.add("SharedDeviceRestrictions");
                }
            }
        }
        if (isRestrictionStatus) {
            commandRequestTypeList.add("RestrictionProfileStatus");
        }
        final String finalLastCommandUUID = this.validateAndGetLastCommandUUID(commandArray, commandRequestTypeList);
        if (!MDMStringUtils.isEmpty(finalLastCommandUUID)) {
            queuedCommands.put((Object)finalLastCommandUUID);
        }
        commandScopeParams.put("QueueCommandUUIDS", (Object)queuedCommands);
    }
    
    private String validateAndGetLastCommandUUID(final JSONArray commandArray, final List<String> commandRequestTypeList) {
        String commandUUID = null;
        int i;
        for (int commandLength = i = commandArray.length() - 1; i >= 0; --i) {
            final JSONObject commandObject = commandArray.getJSONObject(i);
            final String commandRequestType = commandObject.getString("COMMAND_TYPE");
            if (!commandRequestTypeList.contains(commandRequestType)) {
                commandUUID = commandObject.getString("COMMAND_UUID");
                return commandUUID;
            }
        }
        return commandUUID;
    }
    
    protected void addCommandScopeParams(final SequentialSubCommand subCommand, final Long resourceId) {
        try {
            final JSONObject params = subCommand.params;
            JSONObject commandScopeParams = params.optJSONObject("cmdScopeParams");
            if (commandScopeParams == null) {
                commandScopeParams = new JSONObject();
                params.put("cmdScopeParams", (Object)commandScopeParams);
            }
            if (this.commandTypeList.contains("InstallApplication")) {
                List resourceList = new ArrayList();
                resourceList.add(resourceId);
                final JSONObject initialParams = params.optJSONObject("initialParams");
                final Long collectionId = (initialParams != null) ? initialParams.optLong(IOSSeqCmdUtil.appCollection) : 0L;
                resourceList = new MDDeviceInstalledAppsHandler().removeInstalledAppResourceFromList(resourceList, collectionId);
                if (resourceList.isEmpty()) {
                    commandScopeParams.put("AppInstallationStatus", 6);
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in adding command scope params", (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
