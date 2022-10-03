package com.me.mdm.server.compliance;

import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.dd.plist.Base64;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandStatusHandler;

public class ActionEngineHandler
{
    private static ActionEngineHandler actionEngineHandler;
    private static CommandStatusHandler commandStatusHandler;
    private Logger logger;
    
    private ActionEngineHandler() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- ActionEngineDBUtil()   >   new objection Creation  ");
    }
    
    public static ActionEngineHandler getInstance() {
        if (ActionEngineHandler.actionEngineHandler == null) {
            ActionEngineHandler.actionEngineHandler = new ActionEngineHandler();
        }
        return ActionEngineHandler.actionEngineHandler;
    }
    
    public void generateCommandForActions(JSONObject payloadJSON) throws Exception {
        try {
            payloadJSON = ComplianceDBUtil.getInstance().getCollectionForComplianceProfile(payloadJSON);
            final Long collectionId = JSONUtil.optLongForUVH(payloadJSON, "collection_id", Long.valueOf(-1L));
            final JSONArray policiesJSONArray = payloadJSON.getJSONArray("policies");
            final DataObject dataObject = (DataObject)new WritableDataObject();
            for (int i = 0; i < policiesJSONArray.length(); ++i) {
                final JSONObject actionJSON = policiesJSONArray.getJSONObject(i).getJSONObject("action");
                final JSONArray actionAttributesJSONArray = actionJSON.getJSONArray("action_attributes");
                for (int j = 0; j < actionAttributesJSONArray.length(); ++j) {
                    final JSONObject actionAttributeJSON = actionAttributesJSONArray.getJSONObject(j);
                    final int actionAttributeType = actionAttributeJSON.getInt("action_attribute_type");
                    actionAttributeJSON.put("collection_id", (Object)collectionId);
                    Long commandId = null;
                    switch (actionAttributeType) {
                        case 1: {
                            commandId = this.generateEmailAlertCommand(actionAttributeJSON);
                            break;
                        }
                        case 2: {
                            commandId = this.generateLostModeCommand(actionAttributeJSON);
                            break;
                        }
                        case 3: {
                            commandId = this.generateCompleteWipeCommand(actionAttributeJSON);
                            break;
                        }
                        case 5: {
                            commandId = this.generateMarkAsNonCompliantCommand(actionAttributeJSON);
                            break;
                        }
                        default: {
                            throw new UnsupportedOperationException("invalid actionAttributeType " + actionAttributeType);
                        }
                    }
                    final Row commandDataToMDCommandRow = new Row("CommandDataToMDCommand");
                    commandDataToMDCommandRow.set("COMMAND_DATA_ID", (Object)JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L)));
                    commandDataToMDCommandRow.set("COMMAND_ID", (Object)commandId);
                    dataObject.addRow(commandDataToMDCommandRow);
                }
            }
            MDMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- generateCommandForActions() >   Error   ", e);
            throw e;
        }
    }
    
    private Long generateMarkAsNonCompliantCommand(final JSONObject actionAttributeJSON) throws Exception {
        try {
            Long commandId = -1L;
            final Long collectionId = JSONUtil.optLongForUVH(actionAttributeJSON, "collection_id", Long.valueOf(-1L));
            actionAttributeJSON.remove("collection_id");
            final String commandUUID = "MarkAsNonCompliant;Collection=" + String.valueOf(collectionId);
            final String commandType = "MarkAsNonCompliant";
            final String commandDataValue = actionAttributeJSON.toString();
            final String encodedCommandData = Base64.encodeBytes(commandDataValue.getBytes());
            commandId = DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, encodedCommandData);
            actionAttributeJSON.put("collection_id", (Object)collectionId);
            if (commandId != -1L) {
                return commandId;
            }
            throw new Exception("Error generating mark as non compliant command");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- generateMarkAsNonCompliantCommand() >   Error   ", e);
            throw e;
        }
    }
    
    private Long generateCompleteWipeCommand(final JSONObject actionAttributeJSON) throws Exception {
        try {
            Long commandId = -1L;
            final Long collectionId = JSONUtil.optLongForUVH(actionAttributeJSON, "collection_id", Long.valueOf(-1L));
            actionAttributeJSON.remove("collection_id");
            final String commandUUID = "EraseDevice;Collection=" + String.valueOf(collectionId);
            final String commandType = "EraseDevice";
            final String commandDataValue = actionAttributeJSON.toString();
            final String encodedCommandData = Base64.encodeBytes(commandDataValue.getBytes());
            commandId = DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, encodedCommandData);
            actionAttributeJSON.put("collection_id", (Object)collectionId);
            this.generateCorporateWipeCommand(actionAttributeJSON);
            if (commandId != -1L) {
                return commandId;
            }
            throw new Exception("Error generating complete wipe command");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- generateCompleteWipeCommand() >   Error   ", e);
            throw e;
        }
    }
    
    private Long generateCorporateWipeCommand(final JSONObject actionAttributeJSON) throws Exception {
        try {
            Long commandId = -1L;
            final Long collectionId = JSONUtil.optLongForUVH(actionAttributeJSON, "collection_id", Long.valueOf(-1L));
            actionAttributeJSON.remove("collection_id");
            final String commandUUID = "CorporateWipe;Collection=" + String.valueOf(collectionId);
            final String commandType = "CorporateWipe";
            commandId = DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, null);
            if (commandId != -1L) {
                return commandId;
            }
            throw new Exception("Error generating corporate wipe command");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- generateCorporateWipeCommand() >   Error   ", e);
            throw e;
        }
    }
    
    private Long generateLostModeCommand(final JSONObject actionAttributeJSON) throws Exception {
        try {
            Long commandId = -1L;
            final Long collectionId = JSONUtil.optLongForUVH(actionAttributeJSON, "collection_id", Long.valueOf(-1L));
            actionAttributeJSON.remove("collection_id");
            String commandUUID = "DeviceRing;Collection=" + collectionId;
            String commandType = "DeviceRing";
            String commandDataValue = actionAttributeJSON.toString();
            String encodedCommandData = Base64.encodeBytes(commandDataValue.getBytes());
            commandId = DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, encodedCommandData);
            commandUUID = "PlayLostModeSound;Collection=" + collectionId;
            commandType = "PlayLostModeSound";
            DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, encodedCommandData);
            actionAttributeJSON.put("collection_id", (Object)collectionId);
            commandUUID = "LostModeCommand;Collection=" + collectionId + ";ActionAttribute=" + JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
            DeviceCommandRepository.getInstance().addComplianceActionCommand("EnableLostMode", commandUUID, encodedCommandData);
            commandUUID = "LostModeCommand;Collection=" + collectionId;
            commandType = "EnableLostMode";
            commandDataValue = actionAttributeJSON.toString();
            encodedCommandData = Base64.encodeBytes(commandDataValue.getBytes());
            commandId = DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, encodedCommandData);
            if (commandId != -1L) {
                return commandId;
            }
            throw new Exception("Error generating lost mode command");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- generateLostModeCommand() >   Error   ", e);
            throw e;
        }
    }
    
    private Long generateEmailAlertCommand(final JSONObject actionAttributeJSON) throws Exception {
        try {
            Long commandId = -1L;
            final Long collectionId = JSONUtil.optLongForUVH(actionAttributeJSON, "collection_id", Long.valueOf(-1L));
            actionAttributeJSON.remove("collection_id");
            final String commandUUID = "EmailAlertCommand;Collection=" + String.valueOf(collectionId) + ";ActionAttribute=" + JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
            final String commandType = "EmailAlertCommand";
            final String commandDataValue = actionAttributeJSON.toString();
            final String encodedCommandData = Base64.encodeBytes(commandDataValue.getBytes());
            commandId = DeviceCommandRepository.getInstance().addComplianceActionCommand(commandType, commandUUID, encodedCommandData);
            if (commandId != -1L) {
                return commandId;
            }
            throw new Exception("Error generating Email alert command");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- generateEmailAlertCommand() >   Error   ", e);
            throw e;
        }
    }
    
    public void sendMailAlert(JSONObject requestJSON) throws Exception {
        try {
            final JSONObject commandStatusJSON = new JSONObject();
            final String complianceName = String.valueOf(requestJSON.get("compliance_name"));
            final String ruleName = String.valueOf(requestJSON.get("rule_name"));
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final Long commandId = JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceId);
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("ADDED_BY", (Object)userId);
            commandStatusJSON.put("REMARKS", (Object)"mdm.compliance.email_alert_initiated");
            commandStatusJSON.put("COMMAND_STATUS", 1);
            Long commandHistoryId = ActionEngineHandler.commandStatusHandler.populateCommandStatus(commandStatusJSON);
            requestJSON.put("command_history_id", (Object)commandHistoryId);
            requestJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(requestJSON);
            final String commandUUID = "EmailAlertCommand;Collection=" + collectionId + ";ActionAttribute=" + JSONUtil.optLongForUVH(requestJSON, "action_attribute_id", Long.valueOf(-1L));
            final DeviceCommand deviceCommand = this.getComplianceCommand(commandUUID);
            final String encodedCommandData = deviceCommand.commandStr;
            final byte[] commandData = Base64.decode(encodedCommandData);
            final String commandJSONString = new String(commandData);
            final JSONObject commandJSON = new JSONObject(commandJSONString);
            MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final String alertMessage = String.valueOf(commandJSON.get("body_message"));
            String subject = String.valueOf(commandJSON.get("subject"));
            final int platformType = requestJSON.getInt("platform_type");
            final String deviceName = String.valueOf(requestJSON.get("device_name"));
            final String platformName = MDMUtil.getInstance().getPlatformName(platformType);
            final String deviceNameHeader = I18N.getMsg("dc.mdm.enroll.device_name", new Object[0]);
            final String platformTypeString = I18N.getMsg("dc.mdm.group.view.Platform_Type", new Object[0]);
            final String messageString = I18N.getMsg("mdm.osupdate.deployMessageTitleDesc", new Object[0]);
            subject = subject + ":     " + I18N.getMsg("mdm.compliance.rule_broken", new Object[] { ruleName, complianceName, deviceName });
            final String body = "<table border=1 style=\"width:50%\"><tr><th>" + deviceNameHeader + "</th><th>" + deviceName + "</th></tr><tr><td>" + platformTypeString + "</td><td>" + platformName + "</td></tr><tr><td>" + messageString + "</td><td>" + alertMessage + "</td></tr></table>";
            final JSONArray emailIdsArray = commandJSON.getJSONArray("alert_email_ids");
            if (commandJSON.getBoolean("alert_user")) {
                final SelectQuery userEmailQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
                final Join userJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
                final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
                userEmailQuery.addJoin(userJoin);
                userEmailQuery.setCriteria(deviceCriteria);
                userEmailQuery.addSelectColumn(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
                userEmailQuery.addSelectColumn(new Column("ManagedUserToDevice", "MANAGED_USER_ID"));
                userEmailQuery.addSelectColumn(new Column("ManagedUser", "MANAGED_USER_ID"));
                userEmailQuery.addSelectColumn(new Column("ManagedUser", "EMAIL_ADDRESS"));
                final DataObject dataObject = MDMUtil.getPersistence().get(userEmailQuery);
                final String userEmail = (String)dataObject.getRow("ManagedUser").get("EMAIL_ADDRESS");
                emailIdsArray.put((Object)new JSONObject().put("email", (Object)userEmail).put("user_name", (Object)"user_name"));
            }
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceId);
            commandStatusJSON.put("COMMAND_ID", (Object)JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L)));
            commandStatusJSON.put("ADDED_BY", (Object)userId);
            commandStatusJSON.put("REMARKS", (Object)"mdm.compliance.email_alert_initiated");
            String remarkArgs = "";
            String notifyUser = "";
            for (int i = 0; i < emailIdsArray.length(); ++i) {
                try {
                    MDMMailNotificationHandler.getInstance().sendMail(String.valueOf(emailIdsArray.getJSONObject(i).get("email")), body, subject);
                    remarkArgs = remarkArgs + emailIdsArray.getJSONObject(i).get("user_name") + "@@@";
                    notifyUser = notifyUser + emailIdsArray.getJSONObject(i).get("user_name") + ", ";
                }
                catch (final Exception e) {
                    commandStatusJSON.put("ERROR_CODE", 1101);
                }
            }
            notifyUser = notifyUser.substring(0, notifyUser.length() - 2);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72414);
            eventLogJSON.put("resource_id", (Object)resourceId);
            eventLogJSON.put("customer_id", (Object)customerId);
            eventLogJSON.put("remarks", (Object)"mdm.compliance.email_alert_initiated");
            eventLogJSON.put("remarks_args", (Object)(notifyUser + "@@@" + deviceName));
            eventLogJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            commandStatusJSON.put("COMMAND_STATUS", 2);
            commandStatusJSON.put("REMARKS_ARGS", (Object)remarkArgs);
            commandHistoryId = ActionEngineHandler.commandStatusHandler.populateCommandStatus(commandStatusJSON);
            requestJSON.put("command_history_id", (Object)commandHistoryId);
            MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON));
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, " sendMailAlert()   >   Error  sending mail ", e2);
            throw e2;
        }
    }
    
    private DeviceCommand getComplianceCommand(final String commandUUID) throws DataAccessException {
        try {
            final DeviceCommand deviceCommand = new DeviceCommand();
            final Criteria commnadUUIDCriteria = new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)commandUUID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdCommands", commnadUUIDCriteria);
            final Row commandRow = dataObject.getRow("MdCommands");
            deviceCommand.commandStr = (String)commandRow.get("COMMAND_DATA_VALUE");
            deviceCommand.commandUUID = (String)commandRow.get("COMMAND_UUID");
            deviceCommand.commandType = (String)commandRow.get("COMMAND_TYPE");
            return deviceCommand;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " getComplianceCommand()   >   Error  sending mail ", (Throwable)e);
            throw e;
        }
    }
    
    public void sendLostMode(JSONObject requestJSON) throws Exception {
        try {
            final String deviceName = String.valueOf(requestJSON.get("device_name"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Long commandId = JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L));
            final Boolean isRemoteAlarm = requestJSON.getBoolean("is_remote_alarm");
            final String commandUUID = "LostModeCommand;Collection=" + collectionId;
            final DeviceCommand deviceCommand = this.getComplianceCommand(commandUUID);
            final String encodedCommandData = deviceCommand.commandStr;
            final byte[] commandData = Base64.decode(encodedCommandData);
            final String commandJSONString = new String(commandData);
            final JSONObject commandJSON = new JSONObject(commandJSONString);
            final JSONObject commandStatusJSON = new JSONObject();
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final int platformType = requestJSON.getInt("platform_type");
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final String phoneNumber = String.valueOf(commandJSON.get("phone_number"));
            final String lockMessage = String.valueOf(commandJSON.get("lock_message"));
            final String unlockPin = String.valueOf(commandJSON.get("unlock_pin"));
            final Boolean sendEmailToUser = commandJSON.getBoolean("send_email_to_user");
            final JSONObject lostModeJSON = new JSONObject();
            lostModeJSON.put("CONTACT_NUMBER", (Object)phoneNumber);
            lostModeJSON.put("LOCK_SCREEN_MESSAGE", (Object)lockMessage);
            lostModeJSON.put("PLATFORM_TYPE", platformType);
            lostModeJSON.put("RESOURCE_ID", (Object)resourceId);
            lostModeJSON.put("CUSTOMER_ID", (Object)customerId);
            lostModeJSON.put("AUDIT_MESSAGE", (Object)"device out of compliance");
            lostModeJSON.put("ADDED_BY", (Object)userId);
            lostModeJSON.put("COMMAND_ID", (Object)commandId);
            lostModeJSON.put("PASSCODE", (Object)unlockPin);
            lostModeJSON.put("EMAIL_SENT_TO_USER", (Object)sendEmailToUser);
            lostModeJSON.put("EMAIL_SENT_TO_ADMIN", (Object)Boolean.TRUE);
            final String remarks = I18N.getMsg("mdm.compliance.lost_mode_initiated", new Object[] { deviceName });
            lostModeJSON.put("REMARKS", (Object)remarks);
            lostModeJSON.put("COMMAND_UUID", (Object)commandUUID);
            final LostModeDataHandler lostModeDataHandler = new LostModeDataHandler();
            lostModeDataHandler.activateLostMode(lostModeJSON);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72415);
            eventLogJSON.put("resource_id", (Object)resourceId);
            eventLogJSON.put("customer_id", (Object)customerId);
            eventLogJSON.put("remarks", (Object)"mdm.compliance.lost_mode_initiated");
            eventLogJSON.put("remarks_args", (Object)deviceName);
            eventLogJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            final JSONObject statusJSON = ActionEngineHandler.commandStatusHandler.getRecentCommandInfo(resourceId, commandId);
            final Long commandHistoryId = statusJSON.getLong("COMMAND_HISTORY_ID");
            requestJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(requestJSON);
            requestJSON.put("command_history_id", (Object)commandHistoryId);
            MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON));
            if (isRemoteAlarm) {
                this.sendRemoteAlarm(requestJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " sendLostMode()   >   Error   ", e);
            throw e;
        }
    }
    
    public void sendCompleteWipe(JSONObject requestJSON) throws Exception {
        try {
            final String deviceName = String.valueOf(requestJSON.get("device_name"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            Long commandId = JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            String commandUUID = "EraseDevice;Collection=" + collectionId;
            final DeviceCommand deviceCommand = this.getComplianceCommand(commandUUID);
            final String encodedCommandData = deviceCommand.commandStr;
            final byte[] commandData = Base64.decode(encodedCommandData);
            final String commandJSONString = new String(commandData);
            final JSONObject commandJSON = new JSONObject(commandJSONString);
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
            if (ManagedDeviceHandler.getInstance().isProfileOwner(resourceId)) {
                commandUUID = "CorporateWipe;Collection=" + collectionId;
                commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
                requestJSON.put("command_id", (Object)commandId);
                this.sendCorporateWipe(requestJSON);
            }
            else {
                JSONObject statusJSON = new JSONObject();
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, commandUUID, userId);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("event_id", 72416);
                eventLogJSON.put("resource_id", (Object)resourceId);
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.complete_wipe_initiated");
                eventLogJSON.put("remarks_args", (Object)deviceName);
                eventLogJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
                ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
                statusJSON = ActionEngineHandler.commandStatusHandler.getRecentCommandInfo(resourceId, commandId);
                final Long commandHistoryId = statusJSON.getLong("COMMAND_HISTORY_ID");
                requestJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(requestJSON);
                requestJSON.put("command_history_id", (Object)commandHistoryId);
                MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " sendCompleteWipe()   >   Error   ", e);
            throw e;
        }
    }
    
    private void sendCorporateWipe(JSONObject requestJSON) throws Exception {
        try {
            final String deviceName = String.valueOf(requestJSON.get("device_name"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            Long commandId = JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final String commandUUID = "CorporateWipe;Collection=" + collectionId;
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            if (commandId == -1L) {
                commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            }
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
            JSONObject statusJSON = new JSONObject();
            DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, commandUUID, userId);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72417);
            eventLogJSON.put("resource_id", (Object)resourceId);
            eventLogJSON.put("customer_id", (Object)customerId);
            eventLogJSON.put("remarks", (Object)"mdm.compliance.corporate_wipe_initiated");
            eventLogJSON.put("remarks_args", (Object)deviceName);
            eventLogJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            statusJSON = ActionEngineHandler.commandStatusHandler.getRecentCommandInfo(resourceId, commandId);
            final Long commandHistoryId = statusJSON.getLong("COMMAND_HISTORY_ID");
            requestJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(requestJSON);
            requestJSON.put("command_history_id", (Object)commandHistoryId);
            MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " sendCorporateWipe()   >   Error   ", e);
            throw e;
        }
    }
    
    private void sendRemoteAlarm(JSONObject requestJSON) throws Exception {
        try {
            final String deviceName = String.valueOf(requestJSON.get("device_name"));
            final Long actionAttributeId = JSONUtil.optLongForUVH(requestJSON, "action_attribute_id", Long.valueOf(-1L));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final int platformType = requestJSON.getInt("platform_type");
            Long commandId = JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            String commandUUID;
            if (platformType == 1) {
                commandUUID = "PlayLostModeSound;Collection=" + collectionId;
                commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            }
            else {
                commandUUID = "DeviceRing;Collection=" + collectionId;
            }
            final DeviceCommand deviceCommand = this.getComplianceCommand(commandUUID);
            final String encodedCommandData = deviceCommand.commandStr;
            final byte[] commandData = Base64.decode(encodedCommandData);
            final String commandJSONString = new String(commandData);
            final JSONObject commandJSON = new JSONObject(commandJSONString);
            final JSONObject commandStatusJSON = new JSONObject();
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
            JSONObject statusJSON = new JSONObject();
            DeviceInvCommandHandler.getInstance().sendCommandToDevice(deviceDetails, commandUUID, userId);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72422);
            eventLogJSON.put("resource_id", (Object)resourceId);
            eventLogJSON.put("customer_id", (Object)customerId);
            eventLogJSON.put("remarks", (Object)"mdm.compliance.remote_alarm_initiated");
            eventLogJSON.put("remarks_args", (Object)deviceName);
            eventLogJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            statusJSON = ActionEngineHandler.commandStatusHandler.getRecentCommandInfo(resourceId, commandId);
            final Long commandHistoryId = statusJSON.getLong("COMMAND_HISTORY_ID");
            requestJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(requestJSON);
            requestJSON.put("command_history_id", (Object)commandHistoryId);
            MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " sendRemoteAlarm()   >   Error   ", e);
            throw e;
        }
    }
    
    public void sendMarkAsNonCompliant(JSONObject requestJSON) throws Exception {
        try {
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final String deviceName = String.valueOf(requestJSON.get("device_name"));
            requestJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(requestJSON);
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final Long complianceStatusId = JSONUtil.optLongForUVH(requestJSON, "compliance_status_id", Long.valueOf(-1L));
            final Long ruleId = JSONUtil.optLongForUVH(requestJSON, "rule_id", Long.valueOf(-1L));
            final Long commandId = JSONUtil.optLongForUVH(requestJSON, "command_id", Long.valueOf(-1L));
            final String policyName = String.valueOf(requestJSON.get("compliance_name"));
            final JSONObject commandStatusJSON = new JSONObject();
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceId);
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("COMMAND_STATUS", 1);
            commandStatusJSON.put("ADDED_BY", (Object)userId);
            Long commandHistoryId = new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
            requestJSON.put("command_history_id", (Object)commandHistoryId);
            final DataObject dataObject = ComplianceDBUtil.getInstance().addOrUpdateActionAttributeToDeviceStatus(requestJSON);
            final JSONObject additionalDataJSON = new JSONObject();
            additionalDataJSON.put("compliance_status_id", (Object)complianceStatusId);
            additionalDataJSON.put("resource_id", (Object)resourceId);
            additionalDataJSON.put("rule_id", (Object)ruleId);
            requestJSON.put("platform_type_id", requestJSON.getInt("platform_type"));
            requestJSON.put("remarks", (Object)"mdm.compliance.rule_broken");
            requestJSON.put("rule_state", 802);
            requestJSON.put("rule_evaluated_time", (Object)ComplianceDBUtil.getInstance().getISO8601Time(MDMUtil.getCurrentTimeInMillis()));
            MDMUtil.getPersistence().update(dataObject);
            commandStatusJSON.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceId);
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("COMMAND_STATUS", 2);
            commandHistoryId = new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72423);
            eventLogJSON.put("resource_id", (Object)resourceId);
            eventLogJSON.put("customer_id", (Object)customerId);
            eventLogJSON.put("remarks", (Object)"mdm.compliance.mark_as_non_compliant_done");
            eventLogJSON.put("remarks_args", (Object)(deviceName + "@@@" + userName + "@@@" + policyName));
            eventLogJSON.put("user_name", (Object)userName);
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " sendMarkAsNonCompliant()   >   Error   ", e);
            throw e;
        }
    }
    
    static {
        ActionEngineHandler.actionEngineHandler = null;
        ActionEngineHandler.commandStatusHandler = new CommandStatusHandler();
    }
}
