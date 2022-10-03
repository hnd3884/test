package com.me.mdm.server.command.kiosk;

import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class KioskPauseResumeManager
{
    public Logger logger;
    public static final Integer IN_KIOSK;
    public static final Integer KIOSK_PAUSED;
    public static final Integer NOT_IN_KIOSK;
    
    public KioskPauseResumeManager() {
        this.logger = Logger.getLogger(KioskPauseResumeManager.class.getName());
    }
    
    public void addKioskCommand(final Long resourceId, final Long aaaUserId, final HashMap commandDetails) throws Exception {
        try {
            final JSONObject commandHistoryData = new JSONObject();
            commandHistoryData.put("RESOURCE_ID", (Object)resourceId);
            final Long commandId = DeviceCommandRepository.getInstance().addCommand(commandDetails.get("commandType").toString());
            commandHistoryData.put("COMMAND_ID", (Object)commandId);
            commandHistoryData.put("ADDED_BY", (Object)aaaUserId);
            final Long commandHistoryId = new CommandStatusHandler().populateCommandStatus(commandHistoryData);
            final KioskPauseResumeHandler kioskPauseResumeHandler = new KioskPauseResumeHandler();
            final JSONObject actionToCmdHistData = new JSONObject();
            actionToCmdHistData.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
            actionToCmdHistData.put("RESUME_KIOSK_INTERVAL", (Object)commandDetails.get("ReEnterTime"));
            kioskPauseResumeHandler.addPauseKioskCommandInfo(actionToCmdHistData);
            final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
            final JSONObject cmdAuditInfo = new JSONObject();
            cmdAuditInfo.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
            cmdAuditInfo.put("AUDIT_MESSAGE", (Object)commandDetails.get("actionReason").toString());
            cmdAuditInfo.put("TICKET_ID", (Object)commandHistoryId);
            commandStatusHandler.addOrUpdateAuditForCommand(cmdAuditInfo);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, (Throwable)ex, () -> "Kiosk Command: Exception occured while initiating " + hashMap.get("commandType").toString());
        }
    }
    
    public Long getResumeDelay(final Long resourceID) throws DataAccessException, JSONException {
        final Long commandHistoryId = new CommandStatusHandler().getRecentCommandInfo(resourceID, DeviceCommandRepository.getInstance().getCommandID("PauseKioskCommand")).getLong("COMMAND_HISTORY_ID");
        return new KioskPauseResumeHandler().getPauseDelayForDevice(commandHistoryId);
    }
    
    public Integer getDeviceKioskState(final long resourceID) {
        Integer state = null;
        try {
            state = new KioskPauseResumeHandler().getDeviceKioskState(resourceID);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Kiosk Command: Exception occured while getting device's kiosk state ", (Throwable)e);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Kiosk Command: Exception occured while getting device's kiosk state ", (Throwable)e2);
        }
        return state;
    }
    
    public void handleKioskStatusUpdateFromAgent(final Long resourceID, final JSONObject messagejson) throws DataAccessException, JSONException, Exception {
        final JSONArray dataArray = (JSONArray)messagejson.get("KioskStatuses");
        for (int index = 0; index < dataArray.length(); ++index) {
            final JSONObject data = dataArray.getJSONObject(index);
            Integer status = data.getInt("Action");
            String remarks = (String)data.get("Reason");
            switch (status) {
                case 1: {
                    remarks = "profile_appplied";
                    break;
                }
                case 3: {
                    status = 1;
                    break;
                }
                case 4: {
                    status = 3;
                    remarks = "profile_removed";
                    break;
                }
            }
            final JSONObject deviceKioskStatusUpdate = new JSONObject();
            deviceKioskStatusUpdate.put("RESOURCE_ID", (Object)resourceID);
            deviceKioskStatusUpdate.put("CURRENT_KIOSK_STATE", (Object)status);
            deviceKioskStatusUpdate.put("REMARKS", (Object)remarks);
            deviceKioskStatusUpdate.put("LAST_CHANGED_TIME", data.get("TimeStamp"));
            final KioskPauseResumeHandler handler = new KioskPauseResumeHandler();
            handler.addOrUpdateKioskStateForDevice(deviceKioskStatusUpdate);
            if (!remarks.equalsIgnoreCase("pause_kiosk_command") && !remarks.equalsIgnoreCase("resume_kiosk_command") && !remarks.equalsIgnoreCase("profile_appplied") && !remarks.equalsIgnoreCase("profile_removed")) {
                handler.logUpdateFromAgent(resourceID, remarks);
            }
        }
    }
    
    public String getDeviceSpecificKioskPassword(final Long resourceId) throws Exception {
        final HashMap deviceDetails = ManagedDeviceHandler.getInstance().getManagedDeviceDetailsFromResourceID(resourceId);
        return ManagedDeviceHandler.getInstance().deviceKioskOrRevokeAdminPassword(deviceDetails.get("UDID"), "kiosk", deviceDetails.get("AGENT_VERSION_CODE")).substring(3, 8);
    }
    
    public void addOrUpdateKioskStateFromCollection(final Long resID, final Integer state) {
        String remarks = null;
        try {
            if (state.equals(PauseResumeKioskConstants.KIOSK_RUNNING)) {
                remarks = "profile_applied";
            }
            else if (state.equals(PauseResumeKioskConstants.KIOSK_NOT_APPLIED_OR_REMOVED)) {
                remarks = "profile_removed";
            }
            final KioskPauseResumeHandler kioskPauseResumeHandler = new KioskPauseResumeHandler();
            final JSONObject kioskData = new JSONObject();
            kioskData.put("CURRENT_KIOSK_STATE", (Object)state);
            kioskData.put("RESOURCE_ID", (Object)resID);
            kioskData.put("REMARKS", (Object)remarks);
            kioskData.put("LAST_CHANGED_TIME", System.currentTimeMillis());
            kioskPauseResumeHandler.addOrUpdateKioskStateForDevice(kioskData);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Kiosk Command: Exception occured while setting device's kiosk state ", e);
        }
    }
    
    public Boolean isKioskProfilePublishedForPlatform(final Integer platform) {
        Boolean isPublished = false;
        try {
            isPublished = new KioskPauseResumeHandler().isProfilePublisedForPlatform(platform);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Kiosk Command: Exception occured while getting device's kiosk state ", e);
        }
        return isPublished;
    }
    
    static {
        IN_KIOSK = 1;
        KIOSK_PAUSED = 2;
        NOT_IN_KIOSK = 3;
    }
}
