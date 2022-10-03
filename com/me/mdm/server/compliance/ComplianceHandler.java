package com.me.mdm.server.compliance;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.me.mdm.server.compliance.dbutil.ActionEngineDBUtil;
import com.adventnet.persistence.WritableDataObject;
import java.util.Collections;
import org.json.JSONException;
import java.util.Comparator;
import org.json.JSONArray;
import com.me.mdm.server.compliance.dbutil.RuleEngineDBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.Properties;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplianceHandler
{
    private static ComplianceHandler complianceHandler;
    private Logger logger;
    
    private ComplianceHandler() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- ComplianceHandler()   >   new object Creation  ");
    }
    
    public static ComplianceHandler getInstance() {
        if (ComplianceHandler.complianceHandler == null) {
            ComplianceHandler.complianceHandler = new ComplianceHandler();
        }
        return ComplianceHandler.complianceHandler;
    }
    
    public void publishComplianceProfile(final JSONObject responseJSON) throws Exception {
        try {
            final Long complianceId = JSONUtil.optLongForUVH(responseJSON, "compliance_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(responseJSON, "user_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(responseJSON, "customer_id", Long.valueOf(-1L));
            final Long collectionId = JSONUtil.optLongForUVH(ComplianceDBUtil.getInstance().getCollectionForComplianceProfile(responseJSON), "collection_id", Long.valueOf(-1L));
            ProfileHandler.addOrUpdateRecentPublishedProfileToCollection(complianceId, collectionId);
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerId, "compliance", collectionId);
            final String deviceComplianceFileNamePath = mdmProfileDir + File.separator + "compliance_profile.json";
            final String removeDeviceComplianceFileNamePath = mdmProfileDir + File.separator + "remove_compliance_profile.json";
            final DeviceCommand publishComplianceCommand = new DeviceCommand();
            final DeviceCommand removeComplianceCommand = new DeviceCommand();
            final Properties publishComplianceProperties = new Properties();
            final Properties removeComplianceProperties = new Properties();
            responseJSON.put("compliance_file_name_path", (Object)deviceComplianceFileNamePath);
            responseJSON.put("remove_compliance_file_name_path", (Object)removeDeviceComplianceFileNamePath);
            publishComplianceCommand.commandFilePath = this.getComplianceFilePath(customerId, collectionId);
            removeComplianceCommand.commandFilePath = this.getRemoveComplianceFilePath(customerId, collectionId);
            publishComplianceCommand.commandUUID = this.generateComplianceProfile(responseJSON);
            removeComplianceCommand.commandUUID = this.generateRemoveComplianceProfile(responseJSON);
            publishComplianceCommand.commandType = "DeviceCompliance";
            removeComplianceCommand.commandType = "RemoveDeviceCompliance";
            final List metaDataList = new ArrayList();
            publishComplianceProperties.setProperty("commandUUID", publishComplianceCommand.commandUUID);
            removeComplianceProperties.setProperty("commandUUID", removeComplianceCommand.commandUUID);
            publishComplianceProperties.setProperty("commandType", publishComplianceCommand.commandType);
            removeComplianceProperties.setProperty("commandType", removeComplianceCommand.commandType);
            publishComplianceProperties.setProperty("commandFilePath", publishComplianceCommand.commandFilePath);
            removeComplianceProperties.setProperty("commandFilePath", removeComplianceCommand.commandFilePath);
            publishComplianceProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            removeComplianceProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(publishComplianceProperties);
            metaDataList.add(removeComplianceProperties);
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionId, metaDataList);
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerId, "compliance", collectionId);
            MDMConfigHandler.getInstance().addorUpdateCollectionMetaData(collectionId, mdmProfileDir, "MDM");
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("compliance_id", complianceId);
            ((Hashtable<String, Long>)properties).put("collection_id", collectionId);
            ((Hashtable<String, Long>)properties).put("customer_id", customerId);
            ((Hashtable<String, Long>)properties).put("user_id", userId);
            ((Hashtable<String, String>)properties).put("compliance_state", "compliance_updated");
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "ComplianceProfilePublishTask");
            taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
            taskInfoMap.put("poolName", "asynchThreadPool");
            this.logger.log(Level.INFO, " Beginning to execute task for publish compliance ");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.compliance.task.ComplianceProfilePublishTask", taskInfoMap, properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " publishComplianceProfile()   >   Error   ", e);
            throw e;
        }
    }
    
    private String generateRemoveComplianceProfile(final JSONObject requestJSON) throws Exception {
        try {
            final String removeComplianceFilePathName = String.valueOf(requestJSON.get("remove_compliance_file_name_path"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONObject complianceJSON = this.generateRemoveComplianceProfileJson(requestJSON);
            final String commandUUID = "RemoveDeviceCompliance;Collection=" + collectionId.toString();
            final String complianceProperty = complianceJSON.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(removeComplianceFilePathName, complianceProperty.getBytes());
            return commandUUID;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " generateRemoveComplianceProfile()   >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject generateRemoveComplianceProfileJson(final JSONObject requestJSON) throws Exception {
        try {
            final String removeComplianceFilePathName = String.valueOf(requestJSON.get("remove_compliance_file_name_path"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONObject complianceJSON = new JSONObject();
            final JSONObject commandJSON = new JSONObject();
            final JSONObject payloadJSON = new JSONObject();
            payloadJSON.put("compliance_id", (Object)JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L)));
            commandJSON.put("RequestType", (Object)"RemoveDeviceCompliance");
            commandJSON.put("RequestData", (Object)payloadJSON);
            complianceJSON.put("CommandScope", (Object)"device");
            complianceJSON.put("CommandUUID", (Object)("RemoveDeviceCompliance;Collection=" + collectionId.toString()));
            complianceJSON.put("Command", (Object)commandJSON);
            complianceJSON.put("CommandFilePath", (Object)removeComplianceFilePathName);
            return complianceJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " generateRemoveComplianceProfile()   >   Error   ", e);
            throw e;
        }
    }
    
    private String generateComplianceProfile(final JSONObject requestJSON) throws Exception {
        try {
            this.logger.log(Level.INFO, "Generating compliance profile -- {0}", requestJSON.toString());
            final String complianceFileNamePath = String.valueOf(requestJSON.get("compliance_file_name_path"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONObject complianceJSON = this.generateComplianceProfileJson(requestJSON);
            final String commandUUID = "DeviceCompliance;Collection=" + collectionId.toString();
            if (complianceJSON != null && complianceJSON.length() > 0) {
                final JSONObject payloadJSON = complianceJSON.getJSONObject("Command").getJSONObject("RequestData");
                ActionEngineHandler.getInstance().generateCommandForActions(payloadJSON);
                final String complianceProperty = complianceJSON.toString();
                ApiFactoryProvider.getFileAccessAPI().writeFile(complianceFileNamePath, complianceProperty.getBytes());
            }
            return commandUUID;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " generateComplianceProfile()   >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject generateComplianceProfileJson(final JSONObject requestJSON) throws Exception {
        try {
            final String complianceFileNamePath = String.valueOf(requestJSON.get("compliance_file_name_path"));
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONObject complianceJSON = new JSONObject();
            final JSONObject payloadJSON = ComplianceDBUtil.getInstance().getComplianceProfile(requestJSON, 2);
            payloadJSON.put("compliance_id", (Object)JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L)));
            complianceJSON.put("CommandUUID", (Object)("DeviceCompliance;Collection=" + collectionId.toString()));
            complianceJSON.put("CommandFilePath", (Object)complianceFileNamePath);
            complianceJSON.put("CommandScope", (Object)"device");
            final JSONObject commandJSON = new JSONObject();
            commandJSON.put("RequestData", (Object)payloadJSON);
            commandJSON.put("RequestType", (Object)"DeviceCompliance");
            complianceJSON.put("Command", (Object)commandJSON);
            return complianceJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " generateComplianceProfile()   >   Error   ", e);
            throw e;
        }
    }
    
    private String getComplianceFilePath(final Long customerId, final Long collectionId) {
        final String mdmComplianceProfileRelativePath = MDMMetaDataUtil.getInstance().mdmComplianceRelativeDirPath(customerId, collectionId);
        return mdmComplianceProfileRelativePath + File.separator + "compliance_profile.json";
    }
    
    private String getRemoveComplianceFilePath(final Long customerId, final Long collectionId) {
        final String mdmComplianceProfileRelativePath = MDMMetaDataUtil.getInstance().mdmComplianceRelativeDirPath(customerId, collectionId);
        return mdmComplianceProfileRelativePath + File.separator + "remove_compliance_profile.json";
    }
    
    public void processComplianceAlertMessage(final JSONObject messageJSON) throws Exception {
        try {
            this.logger.log(Level.INFO, " -->  processComplianceAlertMessage()");
            String remarks = "";
            JSONObject additionalDataJSON = messageJSON.getJSONObject("AlertData");
            final Long customerId = JSONUtil.optLongForUVH(messageJSON, "customer_id", Long.valueOf(-1L));
            final Long resourceId = JSONUtil.optLongForUVH(messageJSON, "resource_id", Long.valueOf(-1L));
            final int platformType = messageJSON.getInt("platform_type");
            final Long ruleId = JSONUtil.optLongForUVH(additionalDataJSON, "RuleId", Long.valueOf(-1L));
            final String udid = String.valueOf(messageJSON.get("udid"));
            final Long complianceId = JSONUtil.optLongForUVH(additionalDataJSON, "ComplianceId", Long.valueOf(-1L));
            final int ruleState = additionalDataJSON.getInt("RuleState");
            JSONObject complianceJSON = new JSONObject();
            complianceJSON.put("compliance_id", (Object)complianceId);
            complianceJSON.put("customer_id", (Object)customerId);
            complianceJSON = ComplianceDBUtil.getInstance().getComplianceProfile(complianceJSON);
            final String complianceName = String.valueOf(complianceJSON.get("compliance_name"));
            additionalDataJSON.put("resource_id", (Object)resourceId);
            additionalDataJSON.put("compliance_id", (Object)complianceId);
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            additionalDataJSON.put("collection_id", (Object)collectionId);
            additionalDataJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(additionalDataJSON);
            final Long complianceStatusId = JSONUtil.optLongForUVH(additionalDataJSON, "compliance_status_id", Long.valueOf(-1L));
            additionalDataJSON.put("customer_id", (Object)customerId);
            additionalDataJSON.put("rule_id", (Object)ruleId);
            additionalDataJSON.put("rule_state", ruleState);
            additionalDataJSON.put("rule_evaluated_time", (Object)String.valueOf(messageJSON.get("Time")));
            final JSONObject existingStatusJSON = ComplianceDBUtil.getInstance().getCurrentRuleComplianceStatus(additionalDataJSON);
            String remarkArgs = "";
            final JSONArray policyArray = complianceJSON.getJSONArray("policies");
            JSONObject actionJSON = new JSONObject();
            String ruleName = "";
            JSONObject ruleJSON = new JSONObject();
            for (int i = 0; i < policyArray.length(); ++i) {
                final JSONObject policyJSON = policyArray.getJSONObject(i);
                final JSONObject tempJSON = policyJSON.getJSONObject("rule");
                if (ruleId.equals(JSONUtil.optLongForUVH(tempJSON, "rule_id", Long.valueOf(-1L)))) {
                    ruleJSON = tempJSON;
                    actionJSON = policyJSON.getJSONObject("action");
                    ruleName = String.valueOf(ruleJSON.get("rule_name"));
                    break;
                }
            }
            final HashMap deviceDetails = ManagedDeviceHandler.getInstance().getManagedDeviceDetails(udid);
            final String deviceName = deviceDetails.get("DEVICE_NAME");
            final Criteria collectionCrieria = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
            final Long userId = (Long)MDMUtil.getPersistence().get("ResourceToProfileHistory", collectionCrieria.and(resourceCriteria)).getRow("ResourceToProfileHistory").get("ASSOCIATED_BY");
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            RuleEngineDBUtil.getInstance().checkRuleExistence(additionalDataJSON);
            additionalDataJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(additionalDataJSON);
            additionalDataJSON.put("user_name", (Object)userName);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("rule_id", (Object)ruleId);
            requestJSON.put("rule_state", ruleState);
            requestJSON.put("resource_id", (Object)resourceId);
            requestJSON.put("collection_id", (Object)collectionId);
            final Boolean isValidChange = this.checkIsStatusChangeValid(requestJSON);
            if (ruleState == 801) {
                additionalDataJSON.put("rule_state", ruleState);
                additionalDataJSON.put("remarks", (Object)"mdm.compliance.yet_to_be_evaluated");
                additionalDataJSON.put("remarks_args", (Object)(ruleName + "@@@" + complianceName + "@@@" + deviceName));
            }
            else if (ruleState == 802 && isValidChange) {
                additionalDataJSON.put("rule_state", ruleState);
                if (additionalDataJSON.has("Location")) {
                    final JSONObject locationJSON = additionalDataJSON.getJSONObject("Location");
                    remarkArgs = locationJSON.get("Latitude") + "@@@" + locationJSON.get("Longitude");
                    additionalDataJSON.put("remarks_args", (Object)remarkArgs);
                }
                final JSONArray unSortedActionAttributesJSONArray = actionJSON.getJSONArray("action_attributes");
                additionalDataJSON.put("remarks", (Object)"mdm.compliance.rule_broken");
                additionalDataJSON.put("remarks_args", (Object)(ruleName + "@@@" + complianceName + "@@@" + deviceName));
                final JSONArray jsonArr = new JSONArray(unSortedActionAttributesJSONArray.toString());
                final JSONArray actionAttributesJSONArray = new JSONArray();
                final List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                for (int j = 0; j < jsonArr.length(); ++j) {
                    jsonValues.add(jsonArr.getJSONObject(j));
                }
                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    @Override
                    public int compare(final JSONObject o1, final JSONObject o2) {
                        int compare = 0;
                        try {
                            final int keyA = o1.getInt("execution_order");
                            final int keyB = o2.getInt("execution_order");
                            compare = Integer.compare(keyA, keyB);
                        }
                        catch (final JSONException e) {
                            ComplianceHandler.this.logger.log(Level.SEVERE, " -- processComplianceAlertMessage()   Error   ", (Throwable)e);
                        }
                        return compare;
                    }
                });
                for (int j = 0; j < jsonArr.length(); ++j) {
                    actionAttributesJSONArray.put((Object)jsonValues.get(j));
                }
                DataObject dataObject = (DataObject)new WritableDataObject();
                for (int k = 0; k < actionAttributesJSONArray.length(); ++k) {
                    final JSONObject actionAttributeJSON = actionAttributesJSONArray.getJSONObject(k);
                    final int actionAttributeType = actionAttributeJSON.getInt("action_attribute_type");
                    actionAttributeJSON.put("customer_id", (Object)customerId);
                    actionAttributeJSON.put("resource_id", (Object)resourceId);
                    actionAttributeJSON.put("platform_type", platformType);
                    final Long commandId = ActionEngineDBUtil.getInstance().getCommandIdForCommandDataId(actionAttributeJSON);
                    actionAttributeJSON.put("command_id", (Object)commandId);
                    actionAttributeJSON.put("collection_id", (Object)collectionId);
                    actionAttributeJSON.put("user_id", (Object)userId);
                    actionAttributeJSON.put("compliance_name", (Object)complianceName);
                    actionAttributeJSON.put("rule_id", (Object)ruleId);
                    actionAttributeJSON.put("rule_name", (Object)String.valueOf(ruleJSON.get("rule_name")));
                    actionAttributeJSON.put("location", additionalDataJSON.get("Location"));
                    actionAttributeJSON.put("device_name", (Object)deviceName);
                    final Long timeToExecution = JSONUtil.optLong(actionAttributeJSON, "time_to_execution", -1L);
                    final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
                    if (timeToExecution.equals(-1L) || timeToExecution.equals(0L)) {
                        switch (actionAttributeType) {
                            case 1: {
                                ActionEngineHandler.getInstance().sendMailAlert(actionAttributeJSON);
                                break;
                            }
                            case 2: {
                                ActionEngineHandler.getInstance().sendLostMode(actionAttributeJSON);
                                break;
                            }
                            case 3: {
                                ActionEngineHandler.getInstance().sendCompleteWipe(actionAttributeJSON);
                                break;
                            }
                            case 5: {
                                ActionEngineHandler.getInstance().sendMarkAsNonCompliant(actionAttributeJSON);
                                break;
                            }
                            default: {
                                this.logger.log(Level.SEVERE, " -- processComplianceAlertMessage()   >   Invalid action attribute type + {0}", actionAttributeType);
                                throw new UnsupportedOperationException();
                            }
                        }
                    }
                    else {
                        final Criteria complianceStatusIdCriteria = new Criteria(Column.getColumn("CompliancePendingActions", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
                        final Criteria commandIdCriteria = new Criteria(Column.getColumn("CompliancePendingActions", "COMMAND_DATA_ID"), (Object)actionAttributeId, 0);
                        dataObject = MDMUtil.getPersistence().get("CompliancePendingActions", complianceStatusIdCriteria.and(commandIdCriteria));
                        if (dataObject.isEmpty()) {
                            dataObject = (DataObject)new WritableDataObject();
                            final Row row = new Row("CompliancePendingActions");
                            row.set("COMPLIANCE_STATUS_ID", (Object)complianceStatusId);
                            row.set("EXECUTION_TIME", (Object)(MDMUtil.getCurrentTimeInMillis() + timeToExecution));
                            row.set("COMMAND_DATA_ID", (Object)actionAttributeId);
                            dataObject.addRow(row);
                        }
                        else {
                            final Row row = dataObject.getRow("CompliancePendingActions");
                            row.set("COMPLIANCE_STATUS_ID", (Object)complianceStatusId);
                            row.set("EXECUTION_TIME", (Object)(MDMUtil.getCurrentTimeInMillis() + timeToExecution));
                            row.set("COMMAND_DATA_ID", (Object)actionAttributeId);
                            dataObject.updateRow(row);
                        }
                    }
                }
                MDMUtil.getPersistence().update(dataObject);
            }
            else if (ruleState == 804) {
                final String errRemarks = "";
                additionalDataJSON.put("rule_state", ruleState);
                final JSONObject errorJSON = additionalDataJSON.getJSONObject("Error");
                remarks = "";
                final int errorCode = (int)errorJSON.get("ErrorCode");
                switch (errorCode) {
                    case 1001: {
                        remarks = I18N.getMsg("mdm.compliance.gps_off", new Object[0]);
                        MDMUtil.getInstance();
                        remarks = MDMUtil.replaceProductUrlLoaderValuesinText(remarks, null);
                        break;
                    }
                    case 1002: {
                        remarks = I18N.getMsg("mdm.compliance.gps_off", new Object[0]);
                        MDMUtil.getInstance();
                        remarks = MDMUtil.replaceProductUrlLoaderValuesinText(remarks, null);
                        break;
                    }
                    case 1004: {
                        remarks = I18N.getMsg("mdm.compliance.location_privacy", new Object[0]);
                        break;
                    }
                }
                additionalDataJSON.put("remarks_args", (Object)(ruleName + "@@@" + complianceName + "@@@" + deviceName + errorJSON.get("ErrorCode") + "@@@" + errorJSON.get("ErrorMessage")));
                additionalDataJSON.put("remarks", (Object)"mdm.compliance.cannot_be_evaluated");
            }
            else if (ruleState == 803 && isValidChange) {
                JSONObject revertJSON = new JSONObject();
                revertJSON.put("collection_id", (Object)collectionId);
                revertJSON.put("resource_id", (Object)resourceId);
                revertJSON = ComplianceDBUtil.getInstance().getComplianceStatusID(revertJSON);
                revertJSON.put("rule_id", (Object)ruleId);
                revertJSON.put("customer_id", (Object)customerId);
                revertJSON.put("user_name", (Object)userName);
                this.removeScheduledCommandsOnDeviceCompliance(revertJSON);
                additionalDataJSON.put("rule_state", ruleState);
                additionalDataJSON.put("remarks", (Object)"mdm.compliance.rule_compliant");
                additionalDataJSON.put("remarks_args", (Object)(deviceName + "@@@" + ruleName + "@@@" + complianceName));
            }
            else if (ruleState == 805) {
                additionalDataJSON.put("rule_state", ruleState);
                remarks = I18N.getMsg("mdm.compliance.not_applicable", new Object[] { ruleName, complianceName, deviceName });
                additionalDataJSON.put("remarks", (Object)remarks);
            }
            additionalDataJSON.put("event_id", 72413);
            if (isValidChange) {
                ComplianceDBUtil.getInstance().complianceEventLogEntry(additionalDataJSON);
                if (ruleState == 804) {
                    additionalDataJSON.put("remarks", (Object)remarks);
                }
                MDMUtil.getPersistence().update(ComplianceDBUtil.getInstance().addOrUpdateRuleStatusForDevice(additionalDataJSON, existingStatusJSON));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " processComplianceAlertMessage()   >   Error   ", e);
            throw e;
        }
    }
    
    private Boolean checkIsStatusChangeValid(final JSONObject requestJSON) throws JSONException, DataAccessException {
        Boolean isValidChange = false;
        try {
            final Long ruleId = JSONUtil.optLong(requestJSON, "rule_id", -1L);
            final Long collectionId = JSONUtil.optLong(requestJSON, "collection_id", -1L);
            final Long resourceId = JSONUtil.optLong(requestJSON, "resource_id", -1L);
            final int ruleState = requestJSON.getInt("rule_state");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join recentRuleStatusJoin = new Join("ComplianceToResource", "RuleToDeviceRecentStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleStatusJoin = new Join("RuleToDeviceRecentStatus", "RuleStatusHistory", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
            selectQuery.addJoin(recentRuleStatusJoin);
            selectQuery.addJoin(ruleStatusJoin);
            selectQuery.addSortColumn(new SortColumn("RuleStatusHistory", "RULE_EVALUATED_TIME", (boolean)Boolean.FALSE));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_EVALUATED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToDeviceRecentStatus", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"));
            final Criteria ruleIdCriteria = new Criteria(Column.getColumn("RuleStatusHistory", "RULE_ID"), (Object)ruleId, 0);
            final Criteria collectionIdCriteria = new Criteria(Column.getColumn("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("ComplianceToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(ruleIdCriteria.and(collectionIdCriteria).and(resourceCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final int oldState = (int)dataObject.getFirstRow("RuleStatusHistory").get("RULE_STATUS");
                if (ruleState != oldState || oldState == 804) {
                    isValidChange = true;
                }
            }
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " checkIsStatusChangeValid()   >   Error   ", e);
            throw e;
        }
        return isValidChange;
    }
    
    private void removeScheduledCommandsOnDeviceCompliance(final JSONObject revertJSON) throws Exception {
        try {
            final Long customerId = JSONUtil.optLongForUVH(revertJSON, "customer_id", Long.valueOf(-1L));
            final Long collectionId = JSONUtil.optLongForUVH(revertJSON, "collection_id", Long.valueOf(-1L));
            final Long resourceId = JSONUtil.optLong(revertJSON, "resource_id", -1L);
            final Long complianceStatusId = JSONUtil.optLongForUVH(revertJSON, "compliance_status_id", Long.valueOf(-1L));
            final Long ruleId = JSONUtil.optLong(revertJSON, "rule_id", -1L);
            final String userName = String.valueOf(revertJSON.get("user_name"));
            final JSONObject actionJSON = ActionEngineDBUtil.getInstance().getAction(revertJSON, 0);
            final JSONArray actionAttributesJSONArray = actionJSON.getJSONArray("action_attributes");
            final List removeCommandList = new ArrayList();
            for (int i = 0; i < actionAttributesJSONArray.length(); ++i) {
                final JSONObject actionAttributeJSON = actionAttributesJSONArray.getJSONObject(i);
                final Long actionAttributeId = JSONUtil.optLongForUVH(actionAttributeJSON, "action_attribute_id", Long.valueOf(-1L));
                removeCommandList.add(actionAttributeId);
            }
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("CompliancePendingActions");
            final Criteria complianceStatusIdCriteria = new Criteria(Column.getColumn("CompliancePendingActions", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
            final Criteria pendingActionCriteria = new Criteria(Column.getColumn("CompliancePendingActions", "COMMAND_DATA_ID"), (Object)removeCommandList.toArray(), 8);
            deleteQuery.setCriteria(complianceStatusIdCriteria.and(pendingActionCriteria));
            MDMUtil.getPersistence().delete(deleteQuery);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72421);
            eventLogJSON.put("resource_id", (Object)resourceId);
            eventLogJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.remove_pending_actions", new Object[] { ManagedDeviceHandler.getInstance().getDeviceName(resourceId) }));
            eventLogJSON.put("user_name", (Object)userName);
            eventLogJSON.put("customer_id", (Object)customerId);
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " removeScheduledCommandsOnDeviceCompliance()   >   Error   ", e);
            throw e;
        }
    }
    
    public void updateComplianceStateForProfileAssociation(final String commandUUID, final Long resourceID, final String strStatus, final Integer errorCode) throws Exception {
        try {
            final JSONObject requestJSON = new JSONObject();
            final String[] command = commandUUID.split("=");
            final String collection = command[1];
            final Long collectionId = Long.valueOf(collection);
            requestJSON.put("resource_id", (Object)resourceID);
            requestJSON.put("collection_id", (Object)collectionId);
            requestJSON.put("status", (Object)strStatus);
            requestJSON.put("ErrorCode", (Object)errorCode);
            ComplianceDBUtil.getInstance().updateComplianceStateOnprofileAssociation(requestJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " updateComplianceStateForProfileAssociation()   >   Error   ", e);
            throw e;
        }
    }
    
    public void disassociateComplianceOnLicenseDowngrade(final Long userId) throws Exception {
        try {
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("event_id", 72420);
            this.logger.log(Level.INFO, "Beginning to disassociate compliance policies on license downgrade");
            final JSONObject complianceCustomerJSON = ComplianceDBUtil.getInstance().getComplianceCustomerJSON();
            final Long customerId = JSONUtil.optLongForUVH(complianceCustomerJSON, "customer_id", Long.valueOf(-1L));
            eventLogJSON.put("customer_id", (Object)customerId);
            eventLogJSON.put("remarks", (Object)I18N.getMsg("mdm.compliance.license_downgrade", new Object[0]));
            eventLogJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            ComplianceDBUtil.getInstance().complianceEventLogEntry(eventLogJSON);
            this.disassociateComplianceForGroupsOnDowngrade(complianceCustomerJSON);
            this.disassociateComplianceForUsersOnDowngrade(complianceCustomerJSON);
            this.disassociateComplianceForDevicesOnDowngrade(complianceCustomerJSON);
            this.logger.log(Level.INFO, "Sucessfully disassociated compliance policies on license downgrade");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- disassociateComplianceOnLicenseDowngrade()\t", e);
            throw e;
        }
    }
    
    private void disassociateComplianceForDevicesOnDowngrade(final JSONObject complianceCustomerJSON) throws Exception {
        try {
            final JSONArray complianceListJSONArray = complianceCustomerJSON.getJSONArray("compliance_list");
            final Long userId = SyMUtil.getInstance().getLoggedInUserID();
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            for (int i = 0; i < complianceListJSONArray.length(); ++i) {
                final JSONObject distributionJSON = complianceListJSONArray.getJSONObject(i);
                final Long profileId = JSONUtil.optLongForUVH(distributionJSON, "compliance_id", Long.valueOf(-1L));
                final Long collectionId = JSONUtil.optLongForUVH(distributionJSON, "collection_id", Long.valueOf(-1L));
                final Long customerId = JSONUtil.optLongForUVH(distributionJSON, "customer_id", Long.valueOf(-1L));
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                final Join deviceJoin = new Join("Profile", "RecentProfileForResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
                final Join recentProfileJoin = new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
                selectQuery.addJoin(deviceJoin);
                selectQuery.addJoin(recentProfileJoin);
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
                selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"));
                final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0).and(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
                selectQuery.setCriteria(profileCriteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = dataObject.getRows("ResourceToProfileHistory");
                final JSONArray resourceJSONArray = new JSONArray();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long resourceId = (Long)row.get("RESOURCE_ID");
                    resourceJSONArray.put((Object)resourceId);
                }
                distributionJSON.put("user_name", (Object)userName);
                distributionJSON.put("profile_id", (Object)profileId);
                distributionJSON.put("user_id", (Object)userId);
                distributionJSON.put("resource_list", (Object)resourceJSONArray);
                ComplianceDistributionHandler.getInstance().disassociateComplianceToDevices(distributionJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- disassociateComplianceForDevicesOnDowngrade()\t", e);
            throw e;
        }
    }
    
    private void disassociateComplianceForUsersOnDowngrade(final JSONObject complianceCustomerJSON) throws Exception {
        try {
            final JSONArray complianceListJSONArray = complianceCustomerJSON.getJSONArray("compliance_list");
            final Long userId = SyMUtil.getInstance().getLoggedInUserID();
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            for (int i = 0; i < complianceListJSONArray.length(); ++i) {
                final JSONObject distributionJSON = complianceListJSONArray.getJSONObject(i);
                final Long profileId = JSONUtil.optLongForUVH(distributionJSON, "compliance_id", Long.valueOf(-1L));
                final Long collectionId = JSONUtil.optLongForUVH(distributionJSON, "collection_id", Long.valueOf(-1L));
                final Long customerId = JSONUtil.optLongForUVH(distributionJSON, "customer_id", Long.valueOf(-1L));
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                final Join mdmResourceJoin = new Join("Profile", "RecentProfileForMDMResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
                selectQuery.addJoin(mdmResourceJoin);
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"));
                final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0).and(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
                selectQuery.setCriteria(profileCriteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = dataObject.getRows("RecentProfileForMDMResource");
                final JSONArray resourceJSONArray = new JSONArray();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long resourceId = (Long)row.get("RESOURCE_ID");
                    resourceJSONArray.put((Object)resourceId);
                }
                final JSONArray profileList = new JSONArray();
                profileList.put((Object)profileId);
                distributionJSON.put("user_name", (Object)userName);
                distributionJSON.put("profile_id", (Object)profileId);
                distributionJSON.put("user_id", (Object)userId);
                distributionJSON.put("resource_list", (Object)resourceJSONArray);
                distributionJSON.put("resource_type", 2);
                distributionJSON.put("profile_list", (Object)profileList);
                ComplianceDistributionHandler.getInstance().disassociateComplianceToMDMResource(distributionJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- disassociateComplianceForUsersOnDowngrade()\t", e);
            throw e;
        }
    }
    
    private void disassociateComplianceForGroupsOnDowngrade(final JSONObject complianceCustomerJSON) throws Exception {
        try {
            final JSONArray complianceListJSONArray = complianceCustomerJSON.getJSONArray("compliance_list");
            final Long userId = SyMUtil.getInstance().getLoggedInUserID();
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            for (int i = 0; i < complianceListJSONArray.length(); ++i) {
                final JSONObject distributionJSON = complianceListJSONArray.getJSONObject(i);
                final Long profileId = JSONUtil.optLongForUVH(distributionJSON, "compliance_id", Long.valueOf(-1L));
                final Long collectionId = JSONUtil.optLongForUVH(distributionJSON, "collection_id", Long.valueOf(-1L));
                final Long customerId = JSONUtil.optLongForUVH(distributionJSON, "customer_id", Long.valueOf(-1L));
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
                final Join groupJoin = new Join("Profile", "RecentProfileForGroup", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
                final Join historyJoin = new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
                selectQuery.addJoin(groupJoin);
                selectQuery.addJoin(historyJoin);
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "GROUP_HISTORY_ID"));
                selectQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "GROUP_ID"));
                final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0);
                selectQuery.setCriteria(profileCriteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator iterator = dataObject.getRows("GroupToProfileHistory");
                final JSONArray resourceJSONArray = new JSONArray();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long resourceId = (Long)row.get("GROUP_ID");
                    resourceJSONArray.put((Object)resourceId);
                }
                distributionJSON.put("user_name", (Object)userName);
                distributionJSON.put("profile_id", (Object)profileId);
                distributionJSON.put("user_id", (Object)userId);
                distributionJSON.put("resource_list", (Object)resourceJSONArray);
                ComplianceDistributionHandler.getInstance().disassociateComplianceToGroups(distributionJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- disassociateComplianceForGroupsOnDowngrade()\t", e);
            throw e;
        }
    }
    
    public void addToQueue(final JSONObject complianceJSON, final int queueType) throws Exception {
        try {
            final DCQueueData dcQueueData = new DCQueueData();
            final DCQueue dcQueue = DCQueueHandler.getQueue("mdm-device-compliance");
            final Long customerId = JSONUtil.optLongForUVH(complianceJSON, "customer_id", Long.valueOf(-1L));
            dcQueueData.fileName = customerId + "-" + queueType + "-" + MDMUtil.getCurrentTimeInMillis() + ".txt";
            dcQueueData.queueData = complianceJSON.toString();
            dcQueueData.queueDataType = queueType;
            dcQueueData.postTime = MDMUtil.getCurrentTimeInMillis();
            dcQueue.addToQueue(dcQueueData);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- addToQueue()", e);
            throw e;
        }
    }
    
    public HashMap complianceUpdateRoles(final JSONObject roleJSON, final JSONObject aaaRoleJSON) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UMRole"));
        final Join umRoleModuleRelationJoin = new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
        final Join umModuleJoin = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2);
        final Join aaaRoleJoin = new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
        selectQuery.addJoin(umRoleModuleRelationJoin);
        selectQuery.addJoin(umModuleJoin);
        selectQuery.addJoin(aaaRoleJoin);
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "UM_MODULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMModule", "UM_MODULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"*_inventory*", 2, false));
        final HashMap hashMap = new HashMap();
        try {
            final Iterator iterator = roleJSON.keys();
            final List umIdList = new ArrayList();
            while (iterator.hasNext()) {
                umIdList.add(JSONUtil.optLongForUVH(roleJSON, (String)iterator.next(), Long.valueOf(-1L)));
            }
            final DataObject dataObject = MDMUtil.getPersistence().get("UMRoleModuleRelation", new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_MODULE_ID"), (Object)umIdList.toArray(), 8));
            final DataObject writableDO = (DataObject)new WritableDataObject();
            final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
            final List checkList = new ArrayList();
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                final String roleName = (String)tempJSON.get((Object)"NAME");
                final Long aaaRoleId = (Long)tempJSON.get((Object)"ROLE_ID");
                final Long umRoleId = (Long)tempJSON.get((Object)"UM_ROLE_ID");
                this.logger.log(Level.INFO, roleName);
                this.logger.log(Level.INFO, (String)tempJSON.get((Object)"UM_ROLE_NAME"));
                final Criteria umRoleCriteria = new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)umRoleId, 0);
                Long geoFenceUmModuleId = -1L;
                Long complianceUmModuleId = -1L;
                Long geoFenceAaaRoleId = -1L;
                Long complianceAaaRoleId = -1L;
                this.logger.log(Level.INFO, "roleName:   {0}", roleName);
                final String s = roleName;
                switch (s) {
                    case "MDM_Inventory_Read": {
                        geoFenceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "MDM_Geofence_Read", Long.valueOf(-1L));
                        complianceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "MDM_Compliance_Read", Long.valueOf(-1L));
                        geoFenceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "MDM_Geofence_Read", Long.valueOf(-1L));
                        complianceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "MDM_Compliance_Read", Long.valueOf(-1L));
                        break;
                    }
                    case "MDM_Inventory_Write": {
                        geoFenceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "MDM_Geofence_Write", Long.valueOf(-1L));
                        complianceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "MDM_Compliance_Write", Long.valueOf(-1L));
                        geoFenceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "MDM_Geofence_Write", Long.valueOf(-1L));
                        complianceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "MDM_Compliance_Write", Long.valueOf(-1L));
                        break;
                    }
                    case "MDM_Inventory_Admin": {
                        geoFenceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "MDM_Geofence_Admin", Long.valueOf(-1L));
                        complianceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "MDM_Compliance_Admin", Long.valueOf(-1L));
                        geoFenceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "MDM_Geofence_Admin", Long.valueOf(-1L));
                        complianceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "MDM_Compliance_Admin", Long.valueOf(-1L));
                        break;
                    }
                    case "ModernMgmt_Inventory_Read": {
                        geoFenceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "ModernMgmt_Geofence_Read", Long.valueOf(-1L));
                        complianceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "ModernMgmt_Compliance_Read", Long.valueOf(-1L));
                        geoFenceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "ModernMgmt_Geofence_Read", Long.valueOf(-1L));
                        complianceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "ModernMgmt_Compliance_Read", Long.valueOf(-1L));
                        break;
                    }
                    case "ModernMgmt_Inventory_Write": {
                        geoFenceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "ModernMgmt_Geofence_Write", Long.valueOf(-1L));
                        complianceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "ModernMgmt_Compliance_Write", Long.valueOf(-1L));
                        geoFenceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "ModernMgmt_Geofence_Write", Long.valueOf(-1L));
                        complianceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "ModernMgmt_Compliance_Write", Long.valueOf(-1L));
                        break;
                    }
                    case "ModernMgmt_Inventory_Admin": {
                        geoFenceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "ModernMgmt_Geofence_Admin", Long.valueOf(-1L));
                        complianceUmModuleId = JSONUtil.optLongForUVH(roleJSON, "ModernMgmt_Compliance_Admin", Long.valueOf(-1L));
                        geoFenceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "ModernMgmt_Geofence_Admin", Long.valueOf(-1L));
                        complianceAaaRoleId = JSONUtil.optLongForUVH(aaaRoleJSON, "ModernMgmt_Compliance_Admin", Long.valueOf(-1L));
                        break;
                    }
                    default: {
                        this.logger.log(Level.INFO, "{0} :   Not applicable", roleName);
                        break;
                    }
                }
                final List idQueryList = new ArrayList();
                idQueryList.add(geoFenceUmModuleId);
                idQueryList.add(complianceUmModuleId);
                final Criteria umModuleCriteria = new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_MODULE_ID"), (Object)idQueryList.toArray(), 8);
                if (dataObject.isEmpty() && dataObject.getRow("UMRoleModuleRelation", umRoleCriteria.and(umModuleCriteria)) == null) {
                    Row row = new Row("UMRoleModuleRelation");
                    row.set("UM_ROLE_ID", (Object)umRoleId);
                    row.set("UM_MODULE_ID", (Object)geoFenceUmModuleId);
                    writableDO.addRow(row);
                    this.logger.log(Level.INFO, row.toString());
                    row = new Row("UMRoleModuleRelation");
                    row.set("UM_ROLE_ID", (Object)umRoleId);
                    row.set("UM_MODULE_ID", (Object)complianceUmModuleId);
                    writableDO.addRow(row);
                    this.logger.log(Level.INFO, row.toString());
                }
                if (!checkList.contains(aaaRoleId)) {
                    hashMap.put(complianceAaaRoleId, aaaRoleId);
                    hashMap.put(geoFenceAaaRoleId, aaaRoleId);
                    checkList.add(aaaRoleId);
                }
            }
            MDMUtil.getPersistence().add(writableDO);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- populateComplianceGeofenceRoleForInventoryRoles()\t", e);
            throw e;
        }
        return hashMap;
    }
    
    static {
        ComplianceHandler.complianceHandler = null;
    }
}
