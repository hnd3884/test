package com.me.mdm.server.location.lostmode;

import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.command.CommandStatusHandler;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.dd.plist.NSDictionary;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSArray;
import java.util.HashMap;
import java.util.logging.Logger;

public class LostModeCommandResponseProcessor
{
    private static final Logger logger;
    private Integer platformType;
    private Long resourceId;
    private Long customerId;
    private String errorCode;
    private String errorMsg;
    private LostModeCommandSender cmdSender;
    private LostModeDataHandler dataHandler;
    HashMap responseMap;
    public static final int IOS_LOST_MODE_APPLE_ERROR = 12069;
    
    public LostModeCommandResponseProcessor(final Long resourceId, final int platformType, final Long customerId, final HashMap responseMap, final Long userId) {
        this.platformType = null;
        this.errorCode = null;
        this.errorMsg = null;
        this.customerId = customerId;
        this.resourceId = resourceId;
        this.responseMap = responseMap;
        this.platformType = platformType;
        this.setError();
        this.cmdSender = new LostModeCommandSender(resourceId, platformType, userId);
        this.dataHandler = new LostModeDataHandler();
    }
    
    private void setError() {
        if (this.platformType == 1 && this.responseMap.containsKey("ErrorChain")) {
            try {
                final String errorChain = this.responseMap.get("ErrorChain").toString();
                final NSArray errorChainArray = (NSArray)DMSecurityUtil.parsePropertyList(errorChain.getBytes());
                if (errorChainArray.count() > 0) {
                    final NSDictionary errorChainDict = (NSDictionary)errorChainArray.lastObject();
                    final HashMap errorHash = PlistWrapper.getInstance().getHashFromDict(errorChainDict);
                    this.errorCode = errorHash.get("ErrorCode");
                    if (this.errorCode.equals(String.valueOf(12069))) {
                        this.errorCode = String.valueOf(12143);
                    }
                }
            }
            catch (final Exception e) {
                LostModeCommandResponseProcessor.logger.log(Level.SEVERE, "Failed to pass the error response as NSArray", e);
            }
        }
        Object o = this.responseMap.get("ErrorCode");
        if (o != null) {
            this.errorCode = (String)o;
        }
        o = this.responseMap.get("EnglishRemarks");
        if (o != null) {
            this.errorMsg = (String)o;
        }
    }
    
    public void processEnableLostModeResponse() {
        try {
            if (this.errorCode != null) {
                this.processEnableLostModeError();
            }
            else {
                this.updateLostModeActivated();
                this.cmdSender.postLostModeActivationCommands();
            }
        }
        catch (final Exception e) {
            LostModeCommandResponseProcessor.logger.log(Level.SEVERE, "LostModeCommandResponseProcessor: Exception processEnableLostModeResponse() ", e);
        }
    }
    
    public void processEnableLostModeResponseForOlderAgent() {
        try {
            this.updateLostModeActivated();
            this.cmdSender.handleAndroidPostLostModeCommandsForOlderAgent();
        }
        catch (final Exception ex) {
            LostModeCommandResponseProcessor.logger.log(Level.WARNING, "Exception occurred while processEnableLostModeResponseForOlderAgent", ex);
        }
    }
    
    public int processDeviceLocationResponse() {
        int status = 0;
        try {
            if (this.errorCode != null) {
                this.processLostModeDeviceLocationError();
            }
            else {
                MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(this.getLocationDataJson(), this.responseMap.get("UDID"));
                status = 2;
            }
        }
        catch (final Exception e) {
            LostModeCommandResponseProcessor.logger.log(Level.SEVERE, "LostModeCommandResponseProcessor: Exception processDeviceLocationResponse() ", e);
        }
        return status;
    }
    
    public void processDisableLostModeResponse() {
        try {
            if (this.errorCode != null) {
                this.processDisableLostModeError();
            }
            else {
                this.updateLostModeDeActivated();
                DeviceCommandRepository.getInstance().deleteResourceCommand("EnableLostMode", this.resourceId);
                DeviceCommandRepository.getInstance().deleteResourceCommand("LostModeDeviceLocation", this.resourceId);
            }
        }
        catch (final Exception e) {
            LostModeCommandResponseProcessor.logger.log(Level.SEVERE, "LostModeCommandResponseProcessor: Exception processDeviceLocationResponse() ", e);
        }
    }
    
    public void processDisableLostModeResponseForOlderAgent() {
        try {
            this.updateLostModeDeActivated();
            DeviceCommandRepository.getInstance().deleteResourceCommand("EnableLostMode", this.resourceId);
            DeviceCommandRepository.getInstance().deleteResourceCommand("LostModeDeviceLocation", this.resourceId);
        }
        catch (final Exception e) {
            LostModeCommandResponseProcessor.logger.log(Level.SEVERE, "LostModeCommandResponseProcessor: Exception processDeviceLocationResponse() ", e);
        }
    }
    
    private void updateLostModeActivated() {
        try {
            final JSONObject lostModeData = new JSONObject();
            lostModeData.put("RESOURCE_ID", (Object)this.resourceId);
            lostModeData.put("TRACKING_STATUS", 2);
            lostModeData.put("COMMAND_ID", (Object)DeviceCommandRepository.getInstance().getCommandID("EnableLostMode"));
            lostModeData.put("REMARKS", (Object)"dc.mdm.geoLoc.findMyPhone.status.activated");
            this.dataHandler.setLostModeStatus(lostModeData);
        }
        catch (final Exception ex) {
            LostModeCommandResponseProcessor.logger.log(Level.WARNING, "Exception occurred while updateLostModeActivated", ex);
        }
    }
    
    private void updateLostModeDeActivated() {
        this.dataHandler.updateLostModeDeActivated(this.resourceId);
    }
    
    private JSONObject getLocationDataJson() {
        final JSONObject json = new JSONObject();
        try {
            json.put("Latitude", (Object)this.responseMap.get("Latitude"));
            json.put("Longitude", (Object)this.responseMap.get("Longitude"));
            String locUpdationTime = this.responseMap.get("Timestamp");
            LostModeCommandResponseProcessor.logger.log(Level.INFO, "locUpdationTime time stamp :{0} in getLocationDataJson while processing lost location", locUpdationTime);
            locUpdationTime = locUpdationTime.replace(" ", "");
            locUpdationTime = locUpdationTime.replace("T", "");
            if (locUpdationTime != null) {
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssX");
                final Date updatedTime = f.parse(locUpdationTime);
                json.put("LocationUpdationTime", updatedTime.getTime());
            }
            else {
                json.put("LocationUpdationTime", System.currentTimeMillis());
            }
        }
        catch (final Exception e) {
            LostModeCommandResponseProcessor.logger.log(Level.SEVERE, "LostModeCommandResponseProcessor: Exception processEnableLostModeResponse() ", e);
        }
        return json;
    }
    
    private void processEnableLostModeError() throws Exception {
        LostModeCommandResponseProcessor.logger.log(Level.INFO, "LostModeCommandResponseProcessor: processEnableLostModeError() {0} {1}", new String[] { this.errorCode, this.errorMsg });
        final Long commandID = DeviceCommandRepository.getInstance().getCommandID("EnableLostMode");
        this.updateLostModeError(commandID, 3);
    }
    
    private void processDisableLostModeError() throws Exception {
        LostModeCommandResponseProcessor.logger.log(Level.INFO, "LostModeCommandResponseProcessor: processDisableLostModeError() {0} {1}", new String[] { this.errorCode, this.errorMsg });
        final Long commandID = DeviceCommandRepository.getInstance().getCommandID("DisableLostMode");
        this.updateLostModeError(commandID, 6);
    }
    
    private void updateLostModeError(final Long commandID, final int status) throws Exception {
        final JSONObject lostModeError = new JSONObject();
        lostModeError.put("RESOURCE_ID", (Object)this.resourceId);
        lostModeError.put("TRACKING_STATUS", status);
        this.dataHandler.addOrUpdateLostModeTrackInfo(lostModeError);
        final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
        final JSONObject recentCommandInfo = commandStatusHandler.getRecentCommandInfo(this.resourceId, commandID);
        final Long commandHistory = recentCommandInfo.optLong("COMMAND_HISTORY_ID", -1L);
        if (commandHistory != -1L) {
            final String remarks = I18N.getMsg("mdm.lost.mode.disable_lost_mode_failed", new Object[0]);
            final String remarksArgs = I18N.getMsg("mdm.lostmode.disable.ios.failed", new Object[0]);
            final String message = remarks + remarksArgs;
            lostModeError.put("COMMAND_HISTORY_ID", (Object)commandHistory);
            lostModeError.put("COMMAND_STATUS", 0);
            lostModeError.put("ERROR_CODE", Integer.parseInt(this.errorCode));
            lostModeError.put("COMMAND_ID", (Object)commandID);
            lostModeError.put("REMARKS", (Object)message);
            lostModeError.put("REMARKS_ARGS", (Object)message);
            commandStatusHandler.populateCommandStatus(lostModeError);
            final Long userId = JSONUtil.optLongForUVH(recentCommandInfo, "ADDED_BY", Long.valueOf(-1L));
            if (userId != -1L) {
                final String userName = DMUserHandler.getUserNameFromUserID(userId);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, this.resourceId, userName, remarks + remarksArgs, remarksArgs, this.customerId);
            }
        }
    }
    
    private void processLostModeDeviceLocationError() {
        LostModeCommandResponseProcessor.logger.log(Level.INFO, "LostModeCommandResponseProcessor: processLostModeDeviceLocationError() {0} {1}", new String[] { this.errorCode, this.errorMsg });
        MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(this.resourceId, Integer.parseInt(this.errorCode));
    }
    
    static {
        logger = Logger.getLogger("MDMLogger");
    }
}
