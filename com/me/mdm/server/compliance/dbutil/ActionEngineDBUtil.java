package com.me.mdm.server.compliance.dbutil;

import java.util.List;
import java.util.ArrayList;
import com.adventnet.persistence.RowIterator;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActionEngineDBUtil
{
    private static ActionEngineDBUtil actionEngineDBUtil;
    private Logger logger;
    
    private ActionEngineDBUtil() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- ActionEngineDBUtil()   >   new objection Creation  ");
    }
    
    public static ActionEngineDBUtil getInstance() {
        if (ActionEngineDBUtil.actionEngineDBUtil == null) {
            ActionEngineDBUtil.actionEngineDBUtil = new ActionEngineDBUtil();
        }
        return ActionEngineDBUtil.actionEngineDBUtil;
    }
    
    protected DataObject addOrUpdateActionEngine(final JSONObject requestJSON) throws Exception {
        try {
            Long actionId = 0L;
            final DataObject actionEngineDO = (DataObject)new WritableDataObject();
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            JSONObject actionJSON = new JSONObject();
            actionJSON = requestJSON.getJSONObject("action");
            actionJSON.put("customer_id", (Object)customerId);
            actionId = JSONUtil.optLongForUVH(actionJSON, "action_id", Long.valueOf(-1L));
            actionJSON.put("action_id", (Object)actionId);
            if (actionId >= -1L) {
                final Row actionRow = new Row("ActionEngine");
                actionEngineDO.addRow(actionRow);
                Object actionIdUVH = new UniqueValueHolder();
                actionIdUVH = actionRow.get("ACTION_ID");
                actionJSON.put("action_id", actionIdUVH);
                actionEngineDO.merge(this.addOrUpdateActionAttributes(actionJSON));
                actionEngineDO.merge(ComplianceDBUtil.getInstance().mapRuleToAction(requestJSON));
            }
            return actionEngineDO;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateActionEngine()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject mapActionToActionAttributes(final JSONObject actionsJSON) throws DataAccessException, JSONException {
        try {
            final Object actionId = actionsJSON.get("action_id");
            final Object attributeId = actionsJSON.get("action_attribute_id");
            final int executionOrder = actionsJSON.optInt("execution_order", -1);
            final Long timeToExecution = actionsJSON.optLong("time_to_execution", -1L);
            final Row mappingRow = new Row("ActionToActionAttributes");
            mappingRow.set("ACTION_ATTRIBUTE_ID", attributeId);
            mappingRow.set("ACTION_ID", actionId);
            mappingRow.set("EXECUTION_ORDER", (Object)executionOrder);
            mappingRow.set("TIME_TO_EXECUTION", (Object)timeToExecution);
            final DataObject actionToActionAttributesDO = (DataObject)new WritableDataObject();
            actionToActionAttributesDO.addRow(mappingRow);
            return actionToActionAttributesDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- mapActionToActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject addOrUpdateActionAttributes(final JSONObject actionAttributesJSON) throws Exception {
        try {
            final DataObject actionAttributesDO = (DataObject)new WritableDataObject();
            JSONArray actionAttributeJSONArray = new JSONArray();
            actionAttributeJSONArray = actionAttributesJSON.getJSONArray("action_attributes");
            final Object actionId = actionAttributesJSON.get("action_id");
            Boolean delayedActionsPresent = false;
            final Long customerId = JSONUtil.optLongForUVH(actionAttributesJSON, "customer_id", Long.valueOf(-1L));
            final JSONArray attributeAndOrderJSONArray = new JSONArray();
            for (int i = 0; i < actionAttributeJSONArray.length(); ++i) {
                final JSONObject actionAttributeJSON = actionAttributeJSONArray.getJSONObject(i);
                final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
                actionAttributeJSON.put("action_attribute_id", (Object)actionAttributeId);
                final int actionAttributeType = actionAttributeJSON.getInt("action_attribute_type");
                final Long timeToExecution = actionAttributeJSON.optLong("time_to_execution", -1L);
                if (timeToExecution > 0L && !delayedActionsPresent) {
                    delayedActionsPresent = true;
                }
                final int executionOrder = actionAttributeJSON.optInt("execution_order", -1);
                if (actionAttributeId >= -1L) {
                    final Row actionAttributesRow = new Row("CommandData");
                    actionAttributesRow.set("COMMAND_TYPE", (Object)actionAttributeType);
                    actionAttributesDO.addRow(actionAttributesRow);
                    Object actionAttributeUVH = new UniqueValueHolder();
                    actionAttributeUVH = actionAttributesRow.get("COMMAND_DATA_ID");
                    final JSONObject returnJSON = new JSONObject();
                    returnJSON.put("action_attribute_id", actionAttributeUVH);
                    returnJSON.put("action_id", actionId);
                    returnJSON.put("time_to_execution", (Object)timeToExecution);
                    returnJSON.put("execution_order", executionOrder);
                    attributeAndOrderJSONArray.put((Object)returnJSON);
                    actionAttributeJSON.put("action_attribute_id", actionAttributeUVH);
                    switch (actionAttributeType) {
                        case 1: {
                            actionAttributesDO.merge(this.addEmailAlertActionAttributes(actionAttributeJSON));
                            break;
                        }
                        case 2: {
                            actionAttributesDO.merge(this.addLostModeActionAttributes(actionAttributeJSON));
                            break;
                        }
                        case 3: {
                            actionAttributesDO.merge(this.addWipeActionAttributes(actionAttributeJSON));
                            break;
                        }
                        case 5: {
                            actionAttributesDO.merge(this.addMarkAsNonCompliantActionAttributes(actionAttributeJSON));
                            break;
                        }
                        default: {
                            this.logger.log(Level.SEVERE, " -- addOrUpdateActionAttributes() >   invalid actionAttributeType: {0}", actionAttributeType);
                            break;
                        }
                    }
                    actionAttributesDO.merge(this.mapActionToActionAttributes(returnJSON));
                }
                else {
                    final boolean moveToTrash = actionAttributeJSON.optBoolean("move_to_trash");
                    if (moveToTrash) {
                        final JSONObject removeActionAttributesJSON = new JSONObject();
                        final JSONArray removeActionAttributeJSONArray = new JSONArray();
                        removeActionAttributeJSONArray.put((Object)actionAttributeJSON);
                        removeActionAttributesJSON.put("action_attributes", (Object)removeActionAttributeJSONArray);
                        this.removeActionAttributeFromDB(removeActionAttributesJSON);
                    }
                    else {
                        switch (actionAttributeType) {
                            case 1: {
                                actionAttributesDO.merge(this.updateEmailAlertActionAttributes(actionAttributeJSON));
                                break;
                            }
                            case 2: {
                                actionAttributesDO.merge(this.updateLostModeActionAttributes(actionAttributeJSON));
                                break;
                            }
                            case 3: {
                                actionAttributesDO.merge(this.updateWipeActionAttributes(actionAttributeJSON));
                                break;
                            }
                            default: {
                                this.logger.log(Level.SEVERE, " -- addOrUpdateActionAttributes() >   invalid actionAttributeType:    {0}", actionAttributeType);
                                break;
                            }
                        }
                        final JSONObject returnJSON2 = new JSONObject();
                        returnJSON2.put("action_attribute_id", (Object)actionAttributeId);
                        returnJSON2.put("action_id", actionId);
                        returnJSON2.put("time_to_execution", (Object)timeToExecution);
                        returnJSON2.put("execution_order", executionOrder);
                        actionAttributesDO.merge(this.mapActionToActionAttributes(returnJSON2));
                    }
                }
            }
            if (ApiFactoryProvider.getSchedulerAPI().isScheduleCreated("ComplianceDelayedActionExecutionTask") && delayedActionsPresent) {
                final Boolean isSchedulerDisabled = ApiFactoryProvider.getSchedulerAPI().isSchedulerDisabled("ComplianceDelayedActionExecutionTask");
                if (isSchedulerDisabled) {
                    ApiFactoryProvider.getSchedulerAPI().setSchedulerState(true, "ComplianceDelayedActionExecutionTask");
                }
            }
            else if (delayedActionsPresent) {
                ComplianceDBUtil.getInstance().createComplianceScheduler(customerId);
            }
            return actionAttributesDO;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject updateWipeActionAttributes(final JSONObject actionAttributeJSON) throws DataAccessException, JSONException {
        try {
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
            final Boolean wipeSdCard = actionAttributeJSON.getBoolean("wipe_sd_card");
            final Boolean wipeButRetainMDM = actionAttributeJSON.getBoolean("wipe_but_retain_mdm");
            final String wipeLockPin = actionAttributeJSON.optString("wipe_lock_pin");
            final DataObject dataObject = MDMUtil.getPersistence().get("WipeCommandData", new Criteria(new Column("WipeCommandData", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            final Row wipeActionAttributesRow = dataObject.getRow("WipeCommandData");
            wipeActionAttributesRow.set("WIPE_LOCK_PIN", (Object)wipeLockPin);
            wipeActionAttributesRow.set("WIPE_BUT_RETAIN_MDM", (Object)wipeButRetainMDM);
            wipeActionAttributesRow.set("WIPE_SD_CARD", (Object)wipeSdCard);
            final DataObject wipeActionDO = (DataObject)new WritableDataObject();
            dataObject.updateRow(wipeActionAttributesRow);
            wipeActionDO.merge(dataObject);
            return wipeActionDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- updateWipeActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject updateLostModeActionAttributes(final JSONObject actionAttributeJSON) throws DataAccessException, JSONException {
        try {
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
            final String phoneNumber = String.valueOf(actionAttributeJSON.get("phone_number"));
            final String lockMessage = String.valueOf(actionAttributeJSON.get("lock_message"));
            final String unlockPin = String.valueOf(actionAttributeJSON.get("unlock_pin"));
            final boolean sendEmailToUser = actionAttributeJSON.getBoolean("send_email_to_user");
            final boolean isRemoteAlarm = actionAttributeJSON.optBoolean("is_remote_alarm", false);
            final UpdateQuery attributeUpdate = (UpdateQuery)new UpdateQueryImpl("LostModeCommandData");
            attributeUpdate.setCriteria(new Criteria(new Column("LostModeCommandData", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            attributeUpdate.setUpdateColumn("LOCK_MESSAGE", (Object)lockMessage);
            attributeUpdate.setUpdateColumn("PHONE_NUMBER", (Object)phoneNumber);
            attributeUpdate.setUpdateColumn("UNLOCK_PIN", (Object)unlockPin);
            attributeUpdate.setUpdateColumn("SEND_EMAIL_TO_USER", (Object)sendEmailToUser);
            attributeUpdate.setUpdateColumn("IS_REMOTE_ALARM", (Object)isRemoteAlarm);
            final DataObject dataObject = MDMUtil.getPersistence().get("LostModeCommandData", new Criteria(new Column("LostModeCommandData", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            final Row lostModeActionRow = dataObject.getRow("LostModeCommandData");
            lostModeActionRow.set("UNLOCK_PIN", (Object)unlockPin);
            lostModeActionRow.set("LOCK_MESSAGE", (Object)lockMessage);
            lostModeActionRow.set("PHONE_NUMBER", (Object)phoneNumber);
            final DataObject lostModeActionDO = (DataObject)new WritableDataObject();
            dataObject.updateRow(lostModeActionRow);
            lostModeActionDO.merge(dataObject);
            return lostModeActionDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- updateLostModeActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject updateEmailAlertActionAttributes(final JSONObject actionAttributeJSON) throws JSONException, DataAccessException {
        try {
            JSONArray alertEmailJSONArray = new JSONArray();
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
            final String bodyMessage = String.valueOf(actionAttributeJSON.get("body_message"));
            final String subject = String.valueOf(actionAttributeJSON.get("subject"));
            alertEmailJSONArray = actionAttributeJSON.getJSONArray("alert_email_ids");
            final Boolean alertUser = actionAttributeJSON.optBoolean("alert_user", false);
            String alertEmailString = "";
            for (int i = 0; i < alertEmailJSONArray.length(); ++i) {
                final String email = String.valueOf(alertEmailJSONArray.getJSONObject(i).get("email"));
                final String userName = String.valueOf(alertEmailJSONArray.getJSONObject(i).get("user_name"));
                final Long emailUVH = this.generateNameForEmail(email, userName);
                alertEmailString = alertEmailString + emailUVH + "@@@";
            }
            if (alertUser) {
                alertEmailString += "alert_user";
            }
            final Row emailAlertActionRow = new Row("EmailAlertCommandData");
            emailAlertActionRow.set("COMMAND_DATA_ID", (Object)actionAttributeId);
            emailAlertActionRow.set("BODY", (Object)bodyMessage);
            emailAlertActionRow.set("SUBJECT", (Object)subject);
            emailAlertActionRow.set("ALERT_EMAIL_ID", (Object)alertEmailString);
            emailAlertActionRow.set("ALERT_USER", (Object)alertUser);
            final DataObject emailAlertActionDO = (DataObject)new WritableDataObject();
            emailAlertActionDO.updateRow(emailAlertActionRow);
            return emailAlertActionDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- updateEmailAlertActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private Long generateNameForEmail(final String email, final String userName) throws DataAccessException {
        try {
            final SelectQuery emailQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EmailNameRel"));
            emailQuery.setCriteria(new Criteria(Column.getColumn("EmailNameRel", "EMAIL"), (Object)email.trim(), 0));
            emailQuery.addSelectColumn(new Column("EmailNameRel", "EMAIL_NAME_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(emailQuery);
            Long emailNameId = -1L;
            if (dataObject.isEmpty()) {
                final Row emailRow = new Row("EmailNameRel");
                emailRow.set("EMAIL", (Object)email);
                emailRow.set("NAME", (Object)userName);
                final DataObject emailDO = (DataObject)new WritableDataObject();
                emailDO.addRow(emailRow);
                MDMUtil.getPersistence().add(emailDO);
                emailNameId = (Long)emailDO.getRow("EmailNameRel").get("EMAIL_NAME_ID");
            }
            else {
                emailNameId = (Long)dataObject.getRow("EmailNameRel").get("EMAIL_NAME_ID");
            }
            return emailNameId;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- generateNameForEmail()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    private DataObject addWipeActionAttributes(final JSONObject actionAttributeJSON) throws JSONException, DataAccessException {
        try {
            final DataObject wipeActionAttributeDO = (DataObject)new WritableDataObject();
            final UniqueValueHolder actionAttributeId = (UniqueValueHolder)actionAttributeJSON.get("action_attribute_id");
            final Boolean wipeSdCard = actionAttributeJSON.getBoolean("wipe_sd_card");
            final Boolean wipeButRetainMDM = actionAttributeJSON.getBoolean("wipe_but_retain_mdm");
            final String wipeLockPin = actionAttributeJSON.optString("wipe_lock_pin");
            final Row attributeRow = new Row("WipeCommandData");
            attributeRow.set("COMMAND_DATA_ID", (Object)actionAttributeId);
            attributeRow.set("WIPE_BUT_RETAIN_MDM", (Object)wipeButRetainMDM);
            if (!wipeLockPin.isEmpty()) {
                attributeRow.set("WIPE_LOCK_PIN", (Object)wipeLockPin);
            }
            attributeRow.set("WIPE_SD_CARD", (Object)wipeSdCard);
            wipeActionAttributeDO.addRow(attributeRow);
            return wipeActionAttributeDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addWipeActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject addLostModeActionAttributes(final JSONObject actionAttributeJSON) throws JSONException, DataAccessException {
        try {
            final DataObject lostModeActionDO = (DataObject)new WritableDataObject();
            final Object actionAttributeId = actionAttributeJSON.get("action_attribute_id");
            final String phoneNumber = actionAttributeJSON.optString("phone_number", (String)null);
            final String lockMessage = actionAttributeJSON.optString("lock_message", (String)null);
            final String unlockPin = actionAttributeJSON.optString("unlock_pin", (String)null);
            final boolean sendEmailToUser = actionAttributeJSON.optBoolean("send_email_to_user", (boolean)Boolean.FALSE);
            final Boolean isRemoteAlarm = actionAttributeJSON.optBoolean("is_remote_alarm", false);
            final Row attributeRow = new Row("LostModeCommandData");
            attributeRow.set("COMMAND_DATA_ID", actionAttributeId);
            attributeRow.set("LOCK_MESSAGE", (Object)lockMessage);
            attributeRow.set("PHONE_NUMBER", (Object)phoneNumber);
            attributeRow.set("UNLOCK_PIN", (Object)unlockPin);
            attributeRow.set("SEND_EMAIL_TO_USER", (Object)sendEmailToUser);
            attributeRow.set("IS_REMOTE_ALARM", (Object)isRemoteAlarm);
            lostModeActionDO.addRow(attributeRow);
            return lostModeActionDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addLostModeActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject addEmailAlertActionAttributes(final JSONObject actionAttributeJSON) throws JSONException, DataAccessException {
        try {
            final DataObject emailAlertActionDO = (DataObject)new WritableDataObject();
            JSONArray alertEmailJSONArray = new JSONArray();
            final Object actionAttributeId = actionAttributeJSON.get("action_attribute_id");
            final String bodyMessage = String.valueOf(actionAttributeJSON.get("body_message"));
            alertEmailJSONArray = actionAttributeJSON.getJSONArray("alert_email_ids");
            final String subject = String.valueOf(actionAttributeJSON.get("subject"));
            final Boolean alertUser = actionAttributeJSON.optBoolean("alert_user", false);
            String alertEmailString = "";
            for (int i = 0; i < alertEmailJSONArray.length(); ++i) {
                final String email = String.valueOf(alertEmailJSONArray.getJSONObject(i).get("email"));
                final String userName = String.valueOf(alertEmailJSONArray.getJSONObject(i).get("user_name"));
                final Long emailUVH = this.generateNameForEmail(email, userName);
                alertEmailString = alertEmailString + emailUVH + "@@@";
            }
            if (alertUser) {
                alertEmailString += "alert_user";
            }
            final Row attributeRow = new Row("EmailAlertCommandData");
            attributeRow.set("COMMAND_DATA_ID", actionAttributeId);
            attributeRow.set("ALERT_EMAIL_ID", (Object)alertEmailString);
            attributeRow.set("BODY", (Object)bodyMessage);
            attributeRow.set("SUBJECT", (Object)subject);
            attributeRow.set("ALERT_USER", (Object)alertUser);
            emailAlertActionDO.addRow(attributeRow);
            return emailAlertActionDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addEmailAlertActionsAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject addMarkAsNonCompliantActionAttributes(final JSONObject actionAttributeJSON) throws JSONException, DataAccessException {
        try {
            final DataObject markAsNonCompliantDO = (DataObject)new WritableDataObject();
            final Object actionAttributeId = actionAttributeJSON.get("action_attribute_id");
            final Boolean isMarkAsNonCompliant = actionAttributeJSON.getBoolean("is_mark_as_non_compliant");
            final Row attributeRow = new Row("MarkDeviceAsNonCompliantCommandData");
            attributeRow.set("COMMAND_DATA_ID", actionAttributeId);
            markAsNonCompliantDO.addRow(attributeRow);
            return markAsNonCompliantDO;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addMarkAsNonCompliantActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getAction(JSONObject ruleJSON, final int params) throws JSONException, DataAccessException {
        try {
            final Long ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            final Table ruleToActionTable = new Table("RuleToAction");
            final SelectQuery ruleToActionQuery = (SelectQuery)new SelectQueryImpl(ruleToActionTable);
            ruleToActionQuery.addSelectColumn(new Column("RuleToAction", "*"));
            ruleToActionQuery.setCriteria(new Criteria(new Column("RuleToAction", "RULE_ID"), (Object)ruleId, 0));
            final DataObject ruleToActionDO = MDMUtil.getPersistence().get(ruleToActionQuery);
            final Row ruleToActionRow = ruleToActionDO.getFirstRow("RuleToAction");
            final Long actionId = (Long)ruleToActionRow.get("ACTION_ID");
            ruleJSON.put("action_id", (Object)actionId);
            ruleJSON = this.getActionAttributes(ruleJSON, 0);
            return ruleJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getAction()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getAction(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final Long ruleId = JSONUtil.optLongForUVH(requestJSON, "rule_id", Long.valueOf(-1L));
            JSONObject actionJSON = new JSONObject();
            final Table ruleToActionTable = new Table("RuleToAction");
            final SelectQuery ruleToActionQuery = (SelectQuery)new SelectQueryImpl(ruleToActionTable);
            ruleToActionQuery.addSelectColumn(new Column("RuleToAction", "*"));
            ruleToActionQuery.setCriteria(new Criteria(new Column("RuleToAction", "RULE_ID"), (Object)ruleId, 0));
            final DataObject ruleToActionDO = MDMUtil.getPersistence().get(ruleToActionQuery);
            final Row ruleToActionRow = ruleToActionDO.getFirstRow("RuleToAction");
            final Long actionId = (Long)ruleToActionRow.get("ACTION_ID");
            actionJSON.put("action_id", (Object)actionId);
            actionJSON = this.getActionAttributes(actionJSON);
            return actionJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getAction()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getActionAttributes(final JSONObject ruleJSON) throws DataAccessException, JSONException {
        try {
            final Long actionId = JSONUtil.optLongForUVH(ruleJSON, "action_id", Long.valueOf(-1L));
            final Table actionToActionAttributesTable = new Table("ActionToActionAttributes");
            final SelectQuery actionAttributesQuery = (SelectQuery)new SelectQueryImpl(actionToActionAttributesTable);
            actionAttributesQuery.addJoin(new Join("ActionToActionAttributes", "CommandData", new String[] { "ACTION_ATTRIBUTE_ID" }, new String[] { "COMMAND_DATA_ID" }, 1));
            actionAttributesQuery.addSelectColumn(new Column("ActionToActionAttributes", "ACTION_ATTRIBUTE_ID"));
            actionAttributesQuery.addSelectColumn(new Column("ActionToActionAttributes", "ACTION_ID"));
            actionAttributesQuery.addSelectColumn(new Column("ActionToActionAttributes", "TIME_TO_EXECUTION"));
            actionAttributesQuery.addSelectColumn(new Column("ActionToActionAttributes", "EXECUTION_ORDER"));
            actionAttributesQuery.addSelectColumn(new Column("CommandData", "COMMAND_DATA_ID"));
            actionAttributesQuery.addSelectColumn(new Column("CommandData", "COMMAND_TYPE"));
            actionAttributesQuery.setCriteria(new Criteria(new Column("ActionToActionAttributes", "ACTION_ID"), (Object)actionId, 0));
            DataObject actionAttributeDO = null;
            actionAttributeDO = MDMUtil.getPersistence().get(actionAttributesQuery);
            Iterator actionAttributesRowIterator = null;
            Iterator actionToActionAttributesRowIterator = null;
            if (actionAttributeDO != null) {
                actionToActionAttributesRowIterator = actionAttributeDO.getRows("ActionToActionAttributes");
                actionAttributesRowIterator = actionAttributeDO.getRows("CommandData");
            }
            final JSONArray actionAttributesJSONArray = new JSONArray();
            while (actionAttributesRowIterator.hasNext() && actionToActionAttributesRowIterator.hasNext()) {
                JSONObject actionAttributesJSON = new JSONObject();
                final Row actionAttributesRow = actionAttributesRowIterator.next();
                final Row actionToActionAttributesRow = actionToActionAttributesRowIterator.next();
                final Long actionAttributeId = (Long)actionToActionAttributesRow.get("ACTION_ATTRIBUTE_ID");
                final int executionOrder = (int)actionToActionAttributesRow.get("EXECUTION_ORDER");
                final int actionAttributeType = (int)actionAttributesRow.get("COMMAND_TYPE");
                final Long timeToExecution = (Long)actionToActionAttributesRow.get("TIME_TO_EXECUTION");
                actionAttributesJSON.put("action_attribute_id", (Object)actionAttributeId);
                actionAttributesJSON.put("execution_order", executionOrder);
                actionAttributesJSON.put("time_to_execution", (Object)timeToExecution);
                switch (actionAttributeType) {
                    case 1: {
                        actionAttributesJSON = this.getEmailAlertActionAttributes(actionAttributesJSON);
                        break;
                    }
                    case 2: {
                        actionAttributesJSON = this.getLostModeActionAttributes(actionAttributesJSON);
                        break;
                    }
                    case 3: {
                        actionAttributesJSON = this.getWipeActionAttributes(actionAttributesJSON);
                        break;
                    }
                    case 5: {
                        actionAttributesJSON = this.getMarkAsNonCompliantActionAttributes(actionAttributesJSON);
                        break;
                    }
                    default: {
                        this.logger.log(Level.SEVERE, "Unknown action attribute-- getActionAttributes()  >   invalid actionAttributeType    {0}", actionAttributeType);
                        break;
                    }
                }
                actionAttributesJSON.put("action_attribute_type", actionAttributeType);
                actionAttributesJSONArray.put((Object)actionAttributesJSON);
            }
            ruleJSON.put("action_attributes", (Object)actionAttributesJSONArray);
            return ruleJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getActionAttributes(final JSONObject ruleJSON, final int params) throws JSONException, DataAccessException {
        try {
            final Long actionId = JSONUtil.optLongForUVH(ruleJSON, "action_id", Long.valueOf(-1L));
            final Table actionToActionAttributesTable = new Table("ActionToActionAttributes");
            final SelectQuery actionAttributesQuery = (SelectQuery)new SelectQueryImpl(actionToActionAttributesTable);
            actionAttributesQuery.addSelectColumn(new Column("ActionToActionAttributes", "ACTION_ID"));
            actionAttributesQuery.addSelectColumn(new Column("ActionToActionAttributes", "ACTION_ATTRIBUTE_ID"));
            actionAttributesQuery.setCriteria(new Criteria(new Column("ActionToActionAttributes", "ACTION_ID"), (Object)actionId, 0));
            final DataObject actionAttributeDO = MDMUtil.getPersistence().get(actionAttributesQuery);
            RowIterator actionToActionAttributesRowIterator = null;
            if (actionAttributeDO != null) {
                actionToActionAttributesRowIterator = (RowIterator)actionAttributeDO.getRows("ActionToActionAttributes");
            }
            final JSONArray actionAttributesJSONArray = new JSONArray();
            while (actionToActionAttributesRowIterator.hasNext()) {
                final JSONObject actionAttributesJSON = new JSONObject();
                final Row actionToActionAttributesRow = (Row)actionToActionAttributesRowIterator.next();
                final Long actionAttributeId = (Long)actionToActionAttributesRow.get("ACTION_ATTRIBUTE_ID");
                actionAttributesJSON.put("action_attribute_id", (Object)actionAttributeId);
                actionAttributesJSONArray.put((Object)actionAttributesJSON);
            }
            ruleJSON.put("action_attributes", (Object)actionAttributesJSONArray);
            return ruleJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getActionAttributes()  >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getEmailAlertActionAttributes(final JSONObject actionAttributesJSON) throws JSONException, DataAccessException {
        try {
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributesJSON, "action_attribute_id", Long.valueOf(-1L));
            final Table actionAttributesTable = new Table("EmailAlertCommandData");
            final SelectQuery actionAttributesQuery = (SelectQuery)new SelectQueryImpl(actionAttributesTable);
            actionAttributesQuery.addSelectColumn(new Column("EmailAlertCommandData", "*"));
            actionAttributesQuery.setCriteria(new Criteria(new Column("EmailAlertCommandData", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            final DataObject actionAttributesDO = MDMUtil.getPersistence().get(actionAttributesQuery);
            final Row actionAttributesRow = actionAttributesDO.getFirstRow("EmailAlertCommandData");
            final String alertEmailString = (String)actionAttributesRow.get("ALERT_EMAIL_ID");
            final String bodyMessage = (String)actionAttributesRow.get("BODY");
            final String subject = (String)actionAttributesRow.get("SUBJECT");
            final Boolean alertUser = (Boolean)actionAttributesRow.get("ALERT_USER");
            final String[] emailAlertArray = alertEmailString.split("@@@");
            final JSONArray emailAlertJSONArray = new JSONArray();
            final List emailIdList = new ArrayList();
            for (int i = 0; i < emailAlertArray.length; ++i) {
                if (!emailAlertArray[i].equalsIgnoreCase("alert_user")) {
                    final Long emailIDUVH = Long.valueOf(emailAlertArray[i]);
                    emailIdList.add(emailIDUVH);
                }
            }
            final DataObject dataObject = MDMUtil.getPersistence().get("EmailNameRel", new Criteria(new Column("EmailNameRel", "EMAIL_NAME_ID"), (Object)emailIdList.toArray(), 8));
            final Iterator iterator = dataObject.getRows("EmailNameRel");
            while (iterator.hasNext()) {
                final Row emailRow = iterator.next();
                final String email = (String)emailRow.get("EMAIL");
                final String name = (String)emailRow.get("NAME");
                final JSONObject emailJSON = new JSONObject();
                emailJSON.put("email", (Object)email);
                emailJSON.put("user_name", (Object)name);
                emailAlertJSONArray.put((Object)emailJSON);
            }
            actionAttributesJSON.put("alert_email_ids", (Object)emailAlertJSONArray);
            actionAttributesJSON.put("body_message", (Object)bodyMessage);
            actionAttributesJSON.put("subject", (Object)subject);
            actionAttributesJSON.put("alert_user", (Object)alertUser);
            return actionAttributesJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getEmailAlertActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getWipeActionAttributes(final JSONObject actionAttributesJSON) throws DataAccessException, JSONException {
        try {
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributesJSON, "action_attribute_id", Long.valueOf(-1L));
            final Table actionAttributesTable = new Table("WipeCommandData");
            final SelectQuery actionAttributesQuery = (SelectQuery)new SelectQueryImpl(actionAttributesTable);
            actionAttributesQuery.addSelectColumn(new Column("WipeCommandData", "*"));
            actionAttributesQuery.setCriteria(new Criteria(new Column("WipeCommandData", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            DataObject actionAttributesDO = null;
            actionAttributesDO = MDMUtil.getPersistence().get(actionAttributesQuery);
            final Row actionAttributesRow = actionAttributesDO.getFirstRow("WipeCommandData");
            final Boolean wipeSdCard = (Boolean)actionAttributesRow.get("WIPE_SD_CARD");
            final Boolean wipeButRetainMDM = (Boolean)actionAttributesRow.get("WIPE_BUT_RETAIN_MDM");
            final String wipeLockPin = (String)actionAttributesRow.get("WIPE_LOCK_PIN");
            actionAttributesJSON.put("wipe_sd_card", (Object)wipeSdCard);
            actionAttributesJSON.put("wipe_but_retain_mdm", (Object)wipeButRetainMDM);
            actionAttributesJSON.put("wipe_lock_pin", (Object)wipeLockPin);
            return actionAttributesJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getWipeActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getLostModeActionAttributes(final JSONObject actionAttributesJSON) throws JSONException, DataAccessException {
        try {
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributesJSON, "action_attribute_id", Long.valueOf(-1L));
            final Table actionAttributesTable = new Table("LostModeCommandData");
            final SelectQuery actionAttributesQuery = (SelectQuery)new SelectQueryImpl(actionAttributesTable);
            actionAttributesQuery.addSelectColumn(new Column("LostModeCommandData", "*"));
            actionAttributesQuery.setCriteria(new Criteria(new Column("LostModeCommandData", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            final DataObject actionAttributesDO = MDMUtil.getPersistence().get(actionAttributesQuery);
            final Row actionAttributesRow = actionAttributesDO.getFirstRow("LostModeCommandData");
            final String phoneNumber = (String)actionAttributesRow.get("PHONE_NUMBER");
            final String lockMessage = (String)actionAttributesRow.get("LOCK_MESSAGE");
            final String unlockPin = (String)actionAttributesRow.get("UNLOCK_PIN");
            final Boolean sendEmailToUser = (Boolean)actionAttributesRow.get("SEND_EMAIL_TO_USER");
            final Boolean isRemoteAlarm = (Boolean)actionAttributesRow.get("IS_REMOTE_ALARM");
            actionAttributesJSON.put("phone_number", (Object)phoneNumber);
            actionAttributesJSON.put("lock_message", (Object)lockMessage);
            actionAttributesJSON.put("unlock_pin", (Object)unlockPin);
            actionAttributesJSON.put("send_email_to_user", (Object)sendEmailToUser);
            actionAttributesJSON.put("is_remote_alarm", (Object)isRemoteAlarm);
            return actionAttributesJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getLostModeActionAttributes()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getMarkAsNonCompliantActionAttributes(final JSONObject actionAttributesJSON) throws DataAccessException, JSONException {
        try {
            actionAttributesJSON.put("is_mark_as_non_compliant", true);
            return actionAttributesJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- getMarkAsNonCompliantActionAttributes()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    protected JSONObject removeActionFromDB(final JSONObject ruleJSON) throws JSONException {
        try {
            JSONObject removeActionCriteriaJSON = new JSONObject();
            removeActionCriteriaJSON = this.removeActionAttributeFromDB(ruleJSON);
            final Long actionId = JSONUtil.optLongForUVH(ruleJSON, "action_id", Long.valueOf(-1L));
            final Criteria actionEngineCriteria = new Criteria(new Column("ActionEngine", "ACTION_ID"), (Object)actionId, 0);
            removeActionCriteriaJSON.put("remove_action_criteria", (Object)actionEngineCriteria);
            return removeActionCriteriaJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- removeActionFromDB()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    private JSONObject removeActionAttributeFromDB(final JSONObject ruleJSON) throws JSONException {
        try {
            final ArrayList<Long> actionAttributeList = new ArrayList<Long>();
            JSONArray actionAttributeJSONArray = new JSONArray();
            actionAttributeJSONArray = ruleJSON.getJSONArray("action_attributes");
            for (int j = 0; j < actionAttributeJSONArray.length(); ++j) {
                JSONObject actionAttributeJSON = new JSONObject();
                actionAttributeJSON = actionAttributeJSONArray.getJSONObject(j);
                final Long actionAttributeId = actionAttributeJSON.optLong("action_attribute_id", -1L);
                if (actionAttributeId != -1L) {
                    actionAttributeList.add(actionAttributeId);
                }
            }
            final Criteria actionAttributeCriteria = new Criteria(new Column("CommandData", "COMMAND_DATA_ID"), (Object)actionAttributeList.toArray(), 8);
            final JSONObject removeActionAttributesCriteriaJSON = new JSONObject();
            removeActionAttributesCriteriaJSON.put("remove_action_attributes_criteria", (Object)actionAttributeCriteria);
            return removeActionAttributesCriteriaJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- removeActionAttributesFromDB()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public Long getCommandIdForCommandDataId(final JSONObject actionAttributeJSON) throws JSONException, DataAccessException {
        try {
            final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandDataToMDCommand"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("CommandDataToMDCommand", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0));
            selectQuery.addSelectColumn(new Column("CommandDataToMDCommand", "COMMAND_DATA_ID"));
            selectQuery.addSelectColumn(new Column("CommandDataToMDCommand", "COMMAND_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            return (Long)dataObject.getRow("CommandDataToMDCommand").get("COMMAND_ID");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getCommandIdForCommandDataId()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    static {
        ActionEngineDBUtil.actionEngineDBUtil = null;
    }
}
