package com.me.mdm.server.location.lostmode;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.api.APIUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.HashMap;
import org.json.JSONArray;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.LockScreenMessageUtil;
import com.me.mdm.server.command.CommandStatusHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LostModeDataHandler
{
    public Logger locationLogger;
    private Logger logger;
    public static final int LOST_MODE_CANCELLED = 0;
    public static final int LOST_MODE_ACTIVATION_INIT = 1;
    public static final int LOST_MODE_ACTIVATED = 2;
    public static final int LOST_MODE_ACTIVATION_FAILED = 3;
    public static final int LOST_MODE_DEACTIVATION_INIT = 4;
    public static final int LOST_MODE_DEACTIVATED = 5;
    public static final int LOST_MODE_DEACTIVATION_FAILED = 6;
    
    public LostModeDataHandler() {
        this.locationLogger = Logger.getLogger("MDMLocationLogger");
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void addOrUpdateLostModeTrackInfo(final JSONObject trackData) {
        try {
            final Long resourceID = trackData.optLong("RESOURCE_ID");
            if (resourceID != null) {
                final Criteria resCrit = new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)resourceID, 0);
                final DataObject trackDO = MDMUtil.getPersistence().get("LostModeTrackInfo", resCrit);
                if (trackDO.isEmpty()) {
                    final Row trackRow = new Row("LostModeTrackInfo");
                    trackRow.set("RESOURCE_ID", (Object)resourceID);
                    trackRow.set("TRACKING_STATUS", (Object)trackData.optInt("TRACKING_STATUS"));
                    trackDO.addRow(trackRow);
                    MDMUtil.getPersistence().add(trackDO);
                }
                else {
                    final Row trackRow = trackDO.getFirstRow("LostModeTrackInfo");
                    trackRow.set("TRACKING_STATUS", (Object)trackData.optInt("TRACKING_STATUS"));
                    trackDO.updateRow(trackRow);
                    MDMUtil.getPersistence().update(trackDO);
                }
            }
            final long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            if (this.getLostModeDeviceCount(customerID) > 0) {
                MessageProvider.getInstance().unhideMessage("LOST_DEVICE_FOUND_MSG", Long.valueOf(customerID));
            }
            else {
                MessageProvider.getInstance().hideMessage("LOST_DEVICE_FOUND_MSG", Long.valueOf(customerID));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateLostModeTrackInfo", ex);
        }
    }
    
    public void addOrUpdateBulkLostModeTrackInfo(final JSONObject trackData, final List<Long> resourceIds) {
        try {
            final List<Long> resourceList = new ArrayList<Long>(resourceIds);
            final Criteria resCrit = new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject updateTrackDO = MDMUtil.getPersistence().get("LostModeTrackInfo", resCrit);
            if (updateTrackDO != null) {
                final Iterator<Row> iterator = updateTrackDO.getRows("LostModeTrackInfo");
                while (iterator.hasNext()) {
                    final Row trackRow = iterator.next();
                    if (trackRow != null) {
                        trackRow.set("TRACKING_STATUS", (Object)trackData.optInt("TRACKING_STATUS"));
                        updateTrackDO.updateRow(trackRow);
                        resourceList.remove(new Long(trackRow.get("RESOURCE_ID").toString()));
                    }
                }
            }
            MDMUtil.getPersistence().update(updateTrackDO);
            final DataObject addTrackDO = MDMUtil.getPersistence().constructDataObject();
            for (final Long resourceID : resourceList) {
                final Row trackRow2 = new Row("LostModeTrackInfo");
                trackRow2.set("RESOURCE_ID", (Object)resourceID);
                trackRow2.set("TRACKING_STATUS", (Object)trackData.optInt("TRACKING_STATUS"));
                addTrackDO.addRow(trackRow2);
            }
            MDMUtil.getPersistence().add(addTrackDO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateBulkLostModeTrackInfo", ex);
        }
    }
    
    public boolean activateLostMode(final JSONObject lostModeData) {
        final boolean isActivated = true;
        try {
            final Long resourceID = lostModeData.optLong("RESOURCE_ID");
            final String contactNumber = lostModeData.optString("CONTACT_NUMBER", (String)null);
            final String lockScreenMsg = lostModeData.optString("LOCK_SCREEN_MESSAGE", (String)null);
            final boolean isEditLostMode = lostModeData.optBoolean("IS_EDIT_LOSTMODE");
            final int platformType = lostModeData.optInt("PLATFORM_TYPE");
            final Long userId = JSONUtil.optLongForUVH(lostModeData, "ADDED_BY", Long.valueOf(-1L));
            final int lostModeStatus = this.getLostModeStatus(resourceID);
            final LostModeCommandSender lostModeCommandSender = new LostModeCommandSender(resourceID, platformType, lostModeData, userId);
            if (!isEditLostMode && lostModeStatus != 2) {
                MDMGeoLocationHandler.getInstance().removeDeviceLocation(resourceID);
                lostModeCommandSender.addLostMode();
                lostModeData.put("TRACKING_STATUS", 1);
                this.addOrUpdateLostModeTrackInfo(lostModeData);
            }
            else {
                if (platformType != 2) {
                    return false;
                }
                lostModeCommandSender.modifyLostMode();
            }
            Long commandID;
            if (lostModeData.has("COMMAND_ID")) {
                commandID = lostModeData.getLong("COMMAND_ID");
            }
            else {
                commandID = this.getLostModeActivationCommandId(platformType);
            }
            lostModeData.put("COMMAND_ID", (Object)commandID);
            final Long commandHistoryID = new CommandStatusHandler().populateCommandStatus(lostModeData);
            lostModeData.put("COMMAND_HISTORY_ID", (Object)commandHistoryID);
            if (contactNumber != null || lockScreenMsg != null) {
                final JSONObject lockscreenJSON = new JSONObject();
                lockscreenJSON.put("resourceId", (Object)resourceID);
                lockscreenJSON.put("phoneNumber", (Object)contactNumber);
                lockscreenJSON.put("lockMessage", (Object)lockScreenMsg);
                LockScreenMessageUtil.getInstance().addorUpdateLockScreenMessage(lockscreenJSON);
            }
            final ArrayList<Long> resList = new ArrayList<Long>();
            resList.add(resourceID);
            NotificationHandler.getInstance().SendNotification(resList, NotificationHandler.getNotificationType(platformType));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while activateLostMode", ex);
        }
        this.updateEnabledLostModeEventLog(lostModeData);
        return isActivated;
    }
    
    public void activateBulkLostMode(final JSONObject lostModeData) {
        try {
            final String contactNumber = lostModeData.optString("CONTACT_NUMBER", (String)null);
            final String lockScreenMsg = lostModeData.optString("LOCK_SCREEN_MESSAGE", (String)null);
            final boolean isEditLostMode = lostModeData.optBoolean("IS_EDIT_LOSTMODE");
            final int platformType = lostModeData.optInt("PLATFORM_TYPE");
            final Long userId = JSONUtil.optLongForUVH(lostModeData, "ADDED_BY", Long.valueOf(-1L));
            final JSONArray devicesArray = (JSONArray)lostModeData.opt("devices");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(devicesArray);
            if (!isEditLostMode) {
                MDMGeoLocationHandler.getInstance().removeDeviceLocation(resourceList);
                new LostModeCommandSender(resourceList, platformType, lostModeData, userId).addBulkLostMode();
                Long commandID;
                if (lostModeData.has("COMMAND_ID")) {
                    commandID = lostModeData.getLong("COMMAND_ID");
                }
                else {
                    commandID = this.getLostModeActivationCommandId(platformType);
                }
                lostModeData.put("COMMAND_ID", (Object)commandID);
                lostModeData.put("TRACKING_STATUS", 1);
                final JSONObject cmdStatusJSON = new JSONObject();
                for (final Long resID : resourceList) {
                    final JSONObject cmd = new JSONObject();
                    cmd.put("COMMAND_ID", (Object)commandID);
                    cmd.put("ADDED_BY", (Object)userId);
                    final JSONArray cmdArray = new JSONArray();
                    cmdArray.put((Object)cmd);
                    cmdStatusJSON.put(String.valueOf(resID), (Object)cmdArray);
                }
                final HashMap criteriaList = new HashMap();
                final ArrayList<Long> cmdList = new ArrayList<Long>();
                cmdList.add(commandID);
                criteriaList.put("COMMAND_ID", cmdList);
                new CommandStatusHandler().populateCommandStatusForDevices(cmdStatusJSON, criteriaList);
                this.addOrUpdateBulkLostModeTrackInfo(lostModeData, resourceList);
                if (contactNumber != null || lockScreenMsg != null) {
                    final JSONObject lockscreenJSON = new JSONObject();
                    lockscreenJSON.put("phoneNumber", (Object)contactNumber);
                    lockscreenJSON.put("lockMessage", (Object)lockScreenMsg);
                    LockScreenMessageUtil.getInstance().addorUpdateBulkLockScreenMessage(lockscreenJSON, resourceList);
                }
            }
            this.updateBulkEnabledLostModeEventLog(lostModeData);
            NotificationHandler.getInstance().SendNotification(resourceList, NotificationHandler.getNotificationType(platformType));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while activateBulkLostMode", ex);
        }
    }
    
    private Long getLostModeActivationCommandId(final int platformType) {
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID("EnableLostMode");
        return commandId;
    }
    
    private Long getLostModeTrackId(final Long resourceId, final int lostModestatus) {
        Long trackID = -1L;
        try {
            final Criteria resCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria enableStatus = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)lostModestatus, 0);
            final DataObject lostModeTrackDO = MDMUtil.getPersistence().get("LostModeTrackInfo", resCriteria.and(enableStatus));
            if (!lostModeTrackDO.isEmpty()) {
                final Row lostModeTrackRow = lostModeTrackDO.getFirstRow("LostModeTrackInfo");
                trackID = (Long)lostModeTrackRow.get("LOST_MODE_TRACK_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getLostModeTrackId", ex);
        }
        return trackID;
    }
    
    public boolean isLostMode(final Long resourceID) {
        boolean isLostMode = false;
        try {
            if (this.getLostModeStatus(resourceID) == 2) {
                isLostMode = true;
            }
            this.locationLogger.log(Level.INFO, "Is Lost mode enabled for device {0}:{1}", new Object[] { resourceID, isLostMode });
        }
        catch (final Exception ex) {
            this.locationLogger.log(Level.WARNING, "Exception occurred while checking lost mode", ex);
        }
        return isLostMode;
    }
    
    public int getLostModeStatus(final Long resourceID) {
        int status = -1;
        try {
            final Criteria resCrit = new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject trackDO = MDMUtil.getPersistence().get("LostModeTrackInfo", resCrit);
            if (!trackDO.isEmpty()) {
                final Row trackRow = trackDO.getFirstRow("LostModeTrackInfo");
                status = (int)trackRow.get("TRACKING_STATUS");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getLostModeStatus", ex);
        }
        return status;
    }
    
    public List getLostModeInitiatedStatusResources(final List<Long> resourceList) {
        final List<Long> initResourceList = new ArrayList<Long>();
        try {
            Criteria resCrit = new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            resCrit = resCrit.and(new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)1, 0));
            final DataObject trackDO = MDMUtil.getPersistence().get("LostModeTrackInfo", resCrit);
            if (trackDO != null) {
                final Iterator<Row> iterator = trackDO.getRows("LostModeTrackInfo");
                while (iterator.hasNext()) {
                    final Row trackRow = iterator.next();
                    if (trackRow != null) {
                        initResourceList.add(Long.valueOf(trackRow.get("RESOURCE_ID").toString()));
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getLostModeInitStatusResources", ex);
        }
        return initResourceList;
    }
    
    public JSONObject getLostModeDeviceInfo(final Long resourceID, final int platform, final boolean isLostModeEnabled) {
        final JSONObject lostModeInfo = new JSONObject();
        try {
            final Criteria resCrit = new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LostModeTrackInfo"));
            final Join lockMsgJoin = new Join("LostModeTrackInfo", "MdDeviceLockMessage", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(lockMsgJoin);
            selectQuery.addSelectColumn(Column.getColumn("LostModeTrackInfo", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLockMessage", "*"));
            selectQuery.setCriteria(resCrit);
            final DataObject lostModeDO = MDMUtil.getPersistence().get(selectQuery);
            if (!lostModeDO.isEmpty()) {
                final Row lostModeRow = lostModeDO.getFirstRow("LostModeTrackInfo");
                lostModeInfo.put("RESOURCE_ID", (Object)resourceID);
                lostModeInfo.put("TRACKING_STATUS", (Object)lostModeRow.get("TRACKING_STATUS"));
                final Row lockMessageRow = lostModeDO.getFirstRow("MdDeviceLockMessage");
                lostModeInfo.put("PHONE_NUMBER", (Object)lockMessageRow.get("PHONE_NUMBER"));
                lostModeInfo.put("LOCK_MESSAGE", (Object)lockMessageRow.get("LOCK_MESSAGE"));
                if (isLostModeEnabled) {
                    final JSONObject commandInfo = new CommandStatusHandler().getRecentCommandInfo(resourceID, this.getLostModeActivationCommandId(platform));
                    lostModeInfo.put("TICKET_ID", (Object)commandInfo.optString("TICKET_ID"));
                    lostModeInfo.put("AUDIT_MESSAGE", (Object)commandInfo.optString("AUDIT_MESSAGE"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exeption occurred while getLostModeDeviceInfo", ex);
        }
        return lostModeInfo;
    }
    
    public void updateLostModeDeActivated(final Long resourceID) {
        try {
            final JSONObject lostModeData = new JSONObject();
            lostModeData.put("RESOURCE_ID", (Object)resourceID);
            lostModeData.put("TRACKING_STATUS", 5);
            lostModeData.put("COMMAND_ID", (Object)DeviceCommandRepository.getInstance().getCommandID("DisableLostMode"));
            lostModeData.put("REMARKS", (Object)"dc.mdm.geoLoc.find_my_phone.status.deactivated");
            this.setLostModeStatus(lostModeData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateLostModeDeactivated", ex);
        }
    }
    
    public void updateLostModeActivated(final Long resourceID) {
        try {
            final JSONObject lostModeData = new JSONObject();
            lostModeData.put("RESOURCE_ID", (Object)resourceID);
            lostModeData.put("TRACKING_STATUS", 2);
            lostModeData.put("COMMAND_ID", (Object)DeviceCommandRepository.getInstance().getCommandID("EnableLostMode"));
            lostModeData.put("REMARKS", (Object)"dc.mdm.geoLoc.findMyPhone.status.activated");
            this.setLostModeStatus(lostModeData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateLostModeActivated", ex);
        }
    }
    
    public boolean addRefreshLostModeCommands(final Long resourceID) {
        boolean success = true;
        try {
            final DeviceDetails device = new DeviceDetails(resourceID);
            final Long userId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            final LostModeCommandSender cmdSender = new LostModeCommandSender(resourceID, device.platform, userId);
            cmdSender.sendDeviceInformationCommand();
            cmdSender.sendDeviceLocationCommand();
        }
        catch (final Exception ex) {
            success = false;
            this.logger.log(Level.WARNING, "Exception occurred while addEnableLostModeCommands");
        }
        return success;
    }
    
    public boolean deActivateLostMode(final JSONObject deactivationData) {
        boolean isSuccess = true;
        try {
            final Long resourceID = deactivationData.optLong("RESOURCE_ID");
            final int lostModeStatus = this.getLostModeStatus(resourceID);
            final int platformType = deactivationData.optInt("PLATFORM_TYPE");
            final Long userId = JSONUtil.optLongForUVH(deactivationData, "ADDED_BY", Long.valueOf(-1L));
            if (lostModeStatus == 1) {
                deactivationData.put("TRACKING_STATUS", 0);
                this.addOrUpdateLostModeTrackInfo(deactivationData);
                this.disableLostModeCommands(resourceID, platformType);
                final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, DeviceCommandRepository.getInstance().getCommandID("EnableLostMode"));
                statusJSON.put("COMMAND_STATUS", 0);
                statusJSON.put("RESOURCE_ID", (Object)resourceID);
                statusJSON.put("REMARKS", (Object)"dc.mdm.actionlog.actions.lost_mode_disabled");
                if (statusJSON.has("ADDED_BY")) {
                    new CommandStatusHandler().populateCommandStatus(statusJSON);
                }
            }
            else if (platformType == 1 && !ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(resourceID)) {
                deactivationData.put("TRACKING_STATUS", 5);
                final JSONObject statusJSON = new JSONObject();
                Long commandId = DeviceCommandRepository.getInstance().getCommandID("DisableLostMode");
                if (commandId == null) {
                    commandId = DeviceCommandRepository.getInstance().addCommand("DisableLostMode");
                }
                statusJSON.put("RESOURCE_ID", (Object)resourceID);
                statusJSON.put("COMMAND_ID", (Object)commandId);
                statusJSON.put("COMMAND_STATUS", 1);
                statusJSON.put("ADDED_BY", (Object)userId);
                final Long commandHistoryId = new CommandStatusHandler().populateCommandStatus(statusJSON);
                this.addOrUpdateLostModeTrackInfo(deactivationData);
                statusJSON.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                statusJSON.put("COMMAND_STATUS", 2);
                new CommandStatusHandler().populateCommandStatus(statusJSON);
            }
            else {
                final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, DeviceCommandRepository.getInstance().getCommandID("EnableLostMode"));
                if (statusJSON.optInt("COMMAND_STATUS") == 1) {
                    statusJSON.put("COMMAND_STATUS", 0);
                    statusJSON.put("RESOURCE_ID", (Object)resourceID);
                    statusJSON.put("REMARKS", (Object)"dc.mdm.actionlog.actions.lost_mode_disabled");
                    if (statusJSON.has("ADDED_BY")) {
                        new CommandStatusHandler().populateCommandStatus(statusJSON);
                    }
                }
                new LostModeCommandSender(resourceID, platformType, userId).sendDisableLostModeCommand();
                final Long commandID = DeviceCommandRepository.getInstance().getCommandID("DisableLostMode");
                deactivationData.put("COMMAND_ID", (Object)commandID);
                final Long commandHistoryID = new CommandStatusHandler().populateCommandStatus(deactivationData);
                deactivationData.put("COMMAND_HISTORY_ID", (Object)commandHistoryID);
                deactivationData.put("TRACKING_STATUS", 4);
                this.addOrUpdateLostModeTrackInfo(deactivationData);
                final ArrayList<Long> resList = new ArrayList<Long>();
                resList.add(resourceID);
                NotificationHandler.getInstance().SendNotification(resList, NotificationHandler.getNotificationType(platformType));
            }
            this.updateDisableLostModeEventLog(deactivationData);
        }
        catch (final Exception ex) {
            isSuccess = false;
            this.locationLogger.log(Level.WARNING, "exception occurred while stop lost mode", ex);
        }
        return isSuccess;
    }
    
    public void deActivateBulkLostMode(final JSONObject deactivationData) {
        try {
            final int platformType = deactivationData.optInt("PLATFORM_TYPE");
            final Long userId = JSONUtil.optLongForUVH(deactivationData, "ADDED_BY", Long.valueOf(-1L));
            final JSONArray devicesArray = (JSONArray)deactivationData.opt("devices");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(devicesArray);
            final Long time = System.currentTimeMillis();
            final List<Long> lostModeInprogressResources = this.getLostModeInitiatedStatusResources(resourceList);
            if (!lostModeInprogressResources.isEmpty()) {
                resourceList.removeAll(lostModeInprogressResources);
                deactivationData.put("devices", (Collection)lostModeInprogressResources);
                deactivationData.put("TRACKING_STATUS", 0);
                this.addOrUpdateBulkLostModeTrackInfo(deactivationData, lostModeInprogressResources);
                this.disableLostModeCommands(lostModeInprogressResources, platformType);
                final Long commandId = DeviceCommandRepository.getInstance().getCommandID("EnableLostMode");
                final JSONObject statusJSON = new CommandStatusHandler().getRecentBulkCommandInfo(lostModeInprogressResources, commandId);
                for (final Object resID : statusJSON.keySet()) {
                    final String resIdStr = String.valueOf(resID);
                    final JSONObject currentJSON = statusJSON.getJSONArray(resIdStr).getJSONObject(0);
                    currentJSON.put("COMMAND_STATUS", 0);
                    currentJSON.put("REMARKS", (Object)"dc.mdm.actionlog.actions.lost_mode_disabled");
                    currentJSON.put("UPDATED_TIME", (Object)time);
                    final JSONArray currentArray = new JSONArray();
                    currentArray.put((Object)currentJSON);
                    statusJSON.put(resIdStr, (Object)currentArray);
                }
                if (statusJSON.has("ADDED_BY")) {
                    final HashMap criteriaList = new HashMap();
                    final ArrayList<Long> cmdList = new ArrayList<Long>();
                    cmdList.add(commandId);
                    criteriaList.put("COMMAND_ID", cmdList);
                    new CommandStatusHandler().populateCommandStatusForDevices(statusJSON, criteriaList);
                }
                this.updateBulkDisableLostModeEventLog(deactivationData);
                NotificationHandler.getInstance().SendNotification(lostModeInprogressResources, NotificationHandler.getNotificationType(platformType));
            }
            if (platformType == 1) {
                final Set<Long> supervisedAnd9_3AboveDevices = ManagedDeviceHandler.getInstance().fetchSupervisedAnd9_3Above(resourceList);
                final List<Long> resList = new ArrayList<Long>();
                resList.addAll(resourceList);
                resList.removeAll(supervisedAnd9_3AboveDevices);
                resourceList.removeAll(resList);
                if (!resList.isEmpty()) {
                    deactivationData.put("TRACKING_STATUS", 5);
                    Long commandId2 = DeviceCommandRepository.getInstance().getCommandID("DisableLostMode");
                    if (commandId2 == null) {
                        commandId2 = DeviceCommandRepository.getInstance().addCommand("DisableLostMode");
                    }
                    final JSONObject statusJSON2 = new CommandStatusHandler().getRecentBulkCommandInfo(resList, commandId2);
                    for (final Object resID2 : statusJSON2.keySet()) {
                        final String resIdStr2 = String.valueOf(resID2);
                        final JSONObject currentJSON2 = statusJSON2.getJSONArray(resIdStr2).getJSONObject(0);
                        currentJSON2.put("COMMAND_STATUS", 1);
                        currentJSON2.put("REMARKS", (Object)"dc.mdm.actionlog.actions.lost_mode_disabled");
                        currentJSON2.put("UPDATED_TIME", (Object)time);
                        final JSONArray currentArray2 = new JSONArray();
                        currentArray2.put((Object)currentJSON2);
                        statusJSON2.put(resIdStr2, (Object)currentArray2);
                    }
                    final HashMap criteriaList2 = new HashMap();
                    final ArrayList<Long> cmdList2 = new ArrayList<Long>();
                    cmdList2.add(commandId2);
                    criteriaList2.put("COMMAND_ID", cmdList2);
                    new CommandStatusHandler().populateCommandStatusForDevices(statusJSON2, criteriaList2);
                    this.addOrUpdateBulkLostModeTrackInfo(deactivationData, resList);
                }
            }
            if (!resourceList.isEmpty()) {
                new LostModeCommandSender(resourceList, platformType, userId).sendBulkDisableLostModeCommand();
                final Long commandID = DeviceCommandRepository.getInstance().getCommandID("DisableLostMode");
                final JSONObject statusJSON = new CommandStatusHandler().getRecentBulkCommandInfo(resourceList, commandID);
                deactivationData.put("COMMAND_ID", (Object)commandID);
                final HashMap criteriaList = new HashMap();
                final ArrayList<Long> cmdList = new ArrayList<Long>();
                cmdList.add(commandID);
                criteriaList.put("COMMAND_ID", cmdList);
                new CommandStatusHandler().populateCommandStatusForDevices(statusJSON, criteriaList);
                deactivationData.put("TRACKING_STATUS", 4);
                this.addOrUpdateBulkLostModeTrackInfo(deactivationData, resourceList);
                this.updateBulkDisableLostModeEventLog(deactivationData);
                NotificationHandler.getInstance().SendNotification(resourceList, NotificationHandler.getNotificationType(platformType));
            }
        }
        catch (final Exception ex) {
            this.locationLogger.log(Level.WARNING, "exception occurred while stop lost mode in bulk", ex);
        }
    }
    
    private void disableLostModeCommands(final Long resourceID, final int platform) {
        try {
            if (platform == 1) {
                if (ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(resourceID)) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("EnableLostMode", resourceID);
                }
            }
            else if (platform == 2) {
                DeviceCommandRepository.getInstance().deleteResourceCommand("EnableLostMode", resourceID);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void disableLostModeCommands(final List<Long> resourceList, final int platform) {
        try {
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID("EnableLostMode");
            if (platform == 1) {
                final Set<Long> supervisedAnd9_3AboveResources = ManagedDeviceHandler.getInstance().fetchSupervisedAnd9_3Above(resourceList);
                if (supervisedAnd9_3AboveResources.size() > 0) {
                    final List<Long> resList = new ArrayList<Long>();
                    resList.addAll(supervisedAnd9_3AboveResources);
                    DeviceCommandRepository.getInstance().deleteResourcesCommand(commandID, resList);
                }
            }
            else if (platform == 2) {
                DeviceCommandRepository.getInstance().deleteResourcesCommand(commandID, resourceList);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void setLostModeStatus(final JSONObject lostModeData) {
        try {
            final int lostModeStatus = lostModeData.optInt("TRACKING_STATUS");
            switch (lostModeStatus) {
                case 2: {
                    this.addOrUpdateLostModeTrackInfo(lostModeData);
                    final Long resourceID = lostModeData.optLong("RESOURCE_ID");
                    final Long commandID = DeviceCommandRepository.getInstance().getCommandID("EnableLostMode");
                    final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
                    final JSONObject recentCommandInfo = commandStatusHandler.getRecentCommandInfo(resourceID, commandID);
                    final Long commandHistoryID = recentCommandInfo.optLong("COMMAND_HISTORY_ID");
                    lostModeData.put("COMMAND_HISTORY_ID", (Object)commandHistoryID);
                    lostModeData.put("COMMAND_STATUS", 2);
                    commandStatusHandler.populateCommandStatus(lostModeData);
                    break;
                }
                case 5: {
                    this.addOrUpdateLostModeTrackInfo(lostModeData);
                    final Long resourceID = lostModeData.optLong("RESOURCE_ID");
                    final Long commandID = DeviceCommandRepository.getInstance().getCommandID("DisableLostMode");
                    final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
                    final JSONObject recentCommandInfo = commandStatusHandler.getRecentCommandInfo(resourceID, commandID);
                    final Long commandHistoryID = recentCommandInfo.optLong("COMMAND_HISTORY_ID");
                    lostModeData.put("COMMAND_HISTORY_ID", (Object)commandHistoryID);
                    lostModeData.put("COMMAND_STATUS", 2);
                    commandStatusHandler.populateCommandStatus(lostModeData);
                    break;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while setLostModeActivated", ex);
        }
    }
    
    public void setLostModeRequestData(final Long resourceID, final HttpServletRequest request) {
        try {
            final boolean isLostModeEnabled = this.isLostMode(resourceID);
            request.setAttribute("IS_LOST_MODE_ENABLED", (Object)isLostModeEnabled);
            final int lostModeStatus = this.getLostModeStatus(resourceID);
            request.setAttribute("LOST_MODE_STATUS", (Object)lostModeStatus);
            if (lostModeStatus == 3) {
                this.setLostModeErrorData(resourceID, request, DeviceCommandRepository.getInstance().getCommandID("EnableLostMode"));
            }
            else if (lostModeStatus == 6) {
                this.setLostModeErrorData(resourceID, request, DeviceCommandRepository.getInstance().getCommandID("DisableLostMode"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while setLostModeRequestData", ex);
        }
    }
    
    private void setLostModeErrorData(final Long resourceID, final HttpServletRequest request, final Long commandID) {
        final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
        final JSONObject commandData = commandStatusHandler.getRecentCommandInfo(resourceID, commandID);
        final Map lostModeErrData = new HashMap();
        lostModeErrData.put("REMARKS", commandData.optString("REMARKS"));
        lostModeErrData.put("ERROR_CODE", commandData.optString("ERROR_CODE"));
        request.setAttribute("LOST_MODE_ERROR", (Object)lostModeErrData);
    }
    
    private void updateEnabledLostModeEventLog(final JSONObject lostModeData) {
        try {
            final Long resourceID = lostModeData.optLong("RESOURCE_ID");
            Long customerID = null;
            if (lostModeData.has("CUSTOMER_ID")) {
                customerID = lostModeData.optLong("CUSTOMER_ID");
            }
            else {
                customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            }
            final String devName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
            String purpose = "--";
            final String ticketID = lostModeData.optString("TICKET_ID", (String)null);
            final String message = lostModeData.optString("AUDIT_MESSAGE", (String)null);
            if (message != null && !message.isEmpty()) {
                purpose = message;
            }
            final Object remarksArgs = devName + "@@@" + purpose;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), "dc.mdm.geoLoc.eventlog.enable_lost_mode", remarksArgs, customerID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateEnabledLostModeEventLog", ex);
        }
    }
    
    private void updateBulkEnabledLostModeEventLog(final JSONObject lostModeData) {
        try {
            final Long customerID = lostModeData.optLong("CUSTOMER_ID");
            final Long user_id = lostModeData.optLong("ADDED_BY");
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            final JSONArray devicesArray = (JSONArray)lostModeData.opt("devices");
            String purpose = "--";
            final String ticketID = lostModeData.optString("TICKET_ID", (String)null);
            final String message = lostModeData.optString("AUDIT_MESSAGE", (String)null);
            if (message != null && !message.isEmpty()) {
                purpose = message;
            }
            final Object remarksArgs = devicesArray.length() + "@@@" + purpose;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, null, user_name, "dc.mdm.geoLoc.eventlog.enable_lost_mode", remarksArgs, customerID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateEnabledLostModeEventLog", ex);
        }
    }
    
    private void updateDisableLostModeEventLog(final JSONObject lostModeData) {
        try {
            final Long customerID = lostModeData.optLong("CUSTOMER_ID");
            final Long resourceID = lostModeData.optLong("RESOURCE_ID");
            final String message = lostModeData.optString("AUDIT_MESSAGE", (String)null);
            if (message != null && !message.isEmpty()) {
                final String devName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                final Object remarksArgs = devName + "@@@" + message;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), "dc.mdm.geoLoc.eventlog.disable_lost_mode", remarksArgs, customerID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateDisableLostModeEventLog", ex);
        }
    }
    
    private void updateBulkDisableLostModeEventLog(final JSONObject lostModeData) {
        try {
            final Long customerID = lostModeData.optLong("CUSTOMER_ID");
            final JSONArray devicesArray = (JSONArray)lostModeData.opt("devices");
            final String message = lostModeData.optString("AUDIT_MESSAGE", (String)null);
            if (message != null && !message.isEmpty()) {
                final Object remarksArgs = devicesArray.length() + "@@@" + message;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), "dc.mdm.geoLoc.eventlog.disable_lost_mode", remarksArgs, customerID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateBulkDisableLostModeEventLog", ex);
        }
    }
    
    public boolean isTrackingNeededForLostMode(final Long resourceID) {
        boolean isTrackingNeeded = false;
        try {
            final int lostModeStatus = this.getLostModeStatus(resourceID);
            if (lostModeStatus == 1 || lostModeStatus == 2) {
                isTrackingNeeded = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while isTrackingNeededForLostMode", ex);
        }
        return isTrackingNeeded;
    }
    
    public boolean isDeviceInLostMode(final Long resourceID) {
        boolean isDevicehasLostMode = false;
        final int lostModeStatus = this.getLostModeStatus(resourceID);
        if (lostModeStatus == 1 || lostModeStatus == 2 || lostModeStatus == 4) {
            isDevicehasLostMode = true;
        }
        return isDevicehasLostMode;
    }
    
    public JSONObject getAndroidEnableLostModePayloadData(final Long resourceID) {
        final JSONObject payloadData = new JSONObject();
        final HashMap hsLockScreenMessage = LockScreenMessageUtil.getInstance().getLockScreenMessage(resourceID);
        try {
            if (hsLockScreenMessage != null) {
                final Object msg = hsLockScreenMessage.get("LOCK_MESSAGE");
                final Object phone = hsLockScreenMessage.get("PHONE_NUMBER");
                if (msg != null) {
                    payloadData.put("LostModeMessage", msg);
                }
                if (phone != null) {
                    payloadData.put("LostModePhone", phone);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while android enable lost mode payload data", ex);
        }
        return payloadData;
    }
    
    public JSONObject getChromeEnableLostModePayloadData(final Long resourceID) throws Exception {
        final JSONObject payloadData = new JSONObject();
        final HashMap hsLockScreenMessage = LockScreenMessageUtil.getInstance().getLockScreenMessage(resourceID);
        if (hsLockScreenMessage != null) {
            final String defaultLostModeMessage = I18N.getMsg("mdm.lost.mode.lost_mode_default_message", new Object[0]);
            String lockScreenMessage = hsLockScreenMessage.getOrDefault("LOCK_MESSAGE", defaultLostModeMessage);
            if (lockScreenMessage.isEmpty()) {
                lockScreenMessage = defaultLostModeMessage;
            }
            final String phoneNumber = hsLockScreenMessage.get("PHONE_NUMBER");
            payloadData.put("LostModeMessage", (Object)lockScreenMessage);
            payloadData.put("PHONE_NUMBER", (Object)phoneNumber);
        }
        return payloadData;
    }
    
    public JSONObject getLostModeDeviceList(final JSONObject request) {
        final JSONObject response = new JSONObject();
        final JSONArray deviceList = new JSONArray();
        DMDataSetWrapper ds = null;
        try {
            final Long customerID = APIUtil.getCustomerID(request);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LostModeTrackInfo"));
            selectQuery.addJoin(new Join("LostModeTrackInfo", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "USER_RESOURCE", 1));
            selectQuery.addJoin(new Join("Resource", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "DEVICE_NAME"));
            Criteria criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0, false);
            criteria = criteria.and(new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 1, 2, 4, 6 }, 8));
            criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            final String search = request.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
            if (search != null) {
                final Criteria searchCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)search, 12, false);
                criteria = criteria.and(searchCriteria);
            }
            selectQuery.setCriteria(criteria);
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final JSONObject device = new JSONObject();
                device.put("customer_id", ds.getValue("CUSTOMER_ID"));
                device.put("user_name", ds.getValue("NAME"));
                device.put("device_name", ds.getValue("DEVICE_NAME"));
                device.put("domain_netbios_name", ds.getValue("DOMAIN_NETBIOS_NAME"));
                device.put("resource_id", ds.getValue("RESOURCE_ID"));
                device.put("udid", ds.getValue("UDID"));
                device.put("platform_type", ds.getValue("PLATFORM_TYPE"));
                device.put("agent_type", ds.getValue("AGENT_TYPE"));
                device.put("managed_status", ds.getValue("MANAGED_STATUS"));
                device.put("lostmode_status", ds.getValue("TRACKING_STATUS"));
                deviceList.put((Object)device);
            }
            response.put("devices", (Object)deviceList);
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getLostModeDeviceList", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public int getLostModeDeviceCount(final long customerId) throws Exception {
        int lostDeviceCount = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LostModeTrackInfo"));
        selectQuery.addJoin(new Join("LostModeTrackInfo", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID").count());
        Criteria criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0, false);
        criteria = criteria.and(new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 1, 4, 6, 2 }, 8));
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.setCriteria(criteria);
        try {
            lostDeviceCount = DBUtil.getRecordCount(selectQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in get lost mode device count", ex);
            throw ex;
        }
        return lostDeviceCount;
    }
    
    public JSONObject getLockScreenRecentMessage(final JSONObject request) throws Exception {
        final Long customerID = APIUtil.getCustomerID(request);
        return new LockScreenMessageUtil().getLockScreenRecentMessage(customerID);
    }
}
