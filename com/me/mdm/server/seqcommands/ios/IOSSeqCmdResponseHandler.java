package com.me.mdm.server.seqcommands.ios;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.sym.server.mdm.apps.MDDeviceInstalledAppsHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.mdm.server.seqcommands.SeqCmdConstants;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IOSSeqCmdResponseHandler extends IOSBaseSeqCmdResponseHandler
{
    private static final Logger MDMLOGGER;
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        final Long nextCommand = super.onSuccess(params);
        final Long resourceID = params.optLong("resourceID");
        final String CommandUUID = params.optString("commandUUID");
        final JSONObject currentParams = params.optJSONObject("CurCmdParam");
        if (CommandUUID.contains("InstallApplication")) {
            final Long collectionId = SeqCmdUtils.getInstance().getBaseCollectionIDForResource(resourceID);
            final String installType = currentParams.optString("installType");
            boolean addToCollection = true;
            if (installType.equalsIgnoreCase("kioskAppUpdate")) {
                addToCollection = false;
            }
            if (collectionId != null && addToCollection) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 3, "dc.db.mdm.apps.status.Installing");
            }
        }
        return nextCommand;
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        final Long resourceID = params.optLong("resourceID");
        final String CommandUUID = params.optString("commandUUID");
        if (CommandUUID.contains("ManagedApplicationList") || CommandUUID.contains("InstallApplication")) {
            final Long collectionId = SeqCmdUtils.getInstance().getBaseCollectionIDForResource(resourceID);
            final JSONObject currentParam = params.getJSONObject("CurCmdParam");
            final String remarks = currentParam.optString("Remarks");
            if (!MDMStringUtils.isEmpty(remarks)) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 7, remarks);
            }
            else {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 7, "mdm.profile.ios.kiosk.redistributeProfile");
            }
            final int errorCode = currentParam.optInt("ErrorCode");
            if (errorCode != 0) {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, collectionId, errorCode);
            }
        }
        return SeqCmdConstants.ABORT_COMMAND;
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        super.subCommandPreProcessor(resourceID, commandID, sequentialSubCommand);
        try {
            final JSONObject params = sequentialSubCommand.params;
            final JSONObject currentParams = params.optJSONObject("CurCmdParam");
            final JSONObject commandScopeParams = params.optJSONObject("cmdScopeParams");
            final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
            if (commandUUID.contains("InstallApplication") || commandUUID.contains("ManagedApplicationList")) {
                List resourceList = new ArrayList();
                resourceList.add(resourceID);
                final JSONObject initialParams = params.optJSONObject("initialParams");
                final int status = (currentParams != null) ? currentParams.optInt("AppInstallationStatus") : 0;
                final Long collectionId = (initialParams != null) ? initialParams.optLong(IOSSeqCmdUtil.appCollection) : 0L;
                resourceList = new MDDeviceInstalledAppsHandler().removeInstalledAppResourceFromList(resourceList, collectionId);
                if (resourceList.isEmpty() || status == 6) {
                    return false;
                }
            }
            else {
                if (commandUUID.contains("DeviceLock")) {
                    return this.isPasscodeEnabled(resourceID);
                }
                if (commandUUID.contains("DefaultMDMKioskProfile") || commandUUID.contains("DefaultMDMRemoveKioskProfile")) {
                    return !this.isPasscodeEnabled(resourceID);
                }
                if (commandUUID.contains("LockScreenMessages")) {
                    final DataObject dataObject = InventoryUtil.getInstance().getDeviceDetailedInfo(resourceID);
                    final String iOSversion = (String)dataObject.getValue("MdDeviceInfo", "OS_VERSION", (Criteria)null);
                    final boolean isSuperVised = (boolean)dataObject.getValue("MdDeviceInfo", "IS_SUPERVISED", (Criteria)null);
                    if (new VersionChecker().isGreaterOrEqual(iOSversion, "8") && isSuperVised) {
                        return true;
                    }
                    final Long collectionId = DeviceCommandRepository.getInstance().getCollectionId(commandID);
                    final List resourceList2 = new ArrayList();
                    resourceList2.add(resourceID);
                    IOSSeqCmdResponseHandler.MDMLOGGER.log(Level.INFO, "Distribution failed due to LockScreen incompactiability. OS Version: {0} and Supervised:{1} resourceId : {2}", new Object[] { iOSversion, isSuperVised, resourceID });
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList2, collectionId, 7, "mdm.profile.lockscreen.failed");
                    MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, collectionId, 29000);
                    return false;
                }
                else {
                    if (commandUUID.startsWith("KioskDefaultRestriction")) {
                        return false;
                    }
                    if (commandUUID.contains("SharedDeviceRestrictions") && commandScopeParams != null && commandScopeParams.length() > 0) {
                        final boolean isMultiuser = commandScopeParams.getBoolean("IS_MULTIUSER");
                        if (!isMultiuser) {
                            return false;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "On IOS subCommandPreprocessor", e);
        }
        return true;
    }
    
    private boolean isPasscodeEnabled(final Long resourceID) throws SyMException {
        JSONObject details = new JSONObject();
        details = InventoryUtil.getInstance().getSecurityInfo(resourceID, details);
        final JSONObject securityDetails = details.optJSONObject("security");
        final boolean isDeviceLocked = securityDetails.optBoolean("PASSCODE_PRESENT");
        return isDeviceLocked;
    }
    
    static {
        MDMLOGGER = Logger.getLogger("MDMLogger");
    }
}
