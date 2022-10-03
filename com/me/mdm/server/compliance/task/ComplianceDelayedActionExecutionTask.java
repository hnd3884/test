package com.me.mdm.server.compliance.task;

import java.util.List;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.compliance.ActionEngineHandler;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ComplianceDelayedActionExecutionTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public ComplianceDelayedActionExecutionTask() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Beginning to check for pending actions");
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CompliancePendingActions"));
            final Join commandDataJoin = new Join("CompliancePendingActions", "CommandData", new String[] { "COMMAND_DATA_ID" }, new String[] { "COMMAND_DATA_ID" }, 2);
            final Join commandDatatoMdCommandJoin = new Join("CommandData", "CommandDataToMDCommand", new String[] { "COMMAND_DATA_ID" }, new String[] { "COMMAND_DATA_ID" }, 2);
            final Join mdCommandsJoin = new Join("CommandDataToMDCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
            final Join complianceToResourceJoin = new Join("CompliancePendingActions", "ComplianceToResource", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join recentProfileJoin = new Join("ComplianceToResource", "RecentProfileForResource", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2);
            final Join profileHistoryJoin = new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "RESOURCE_ID", "COLLECTION_ID", "PROFILE_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID", "PROFILE_ID" }, 2);
            final Join actionToActionAttributeJoin = new Join("CommandData", "ActionToActionAttributes", new String[] { "COMMAND_DATA_ID" }, new String[] { "ACTION_ATTRIBUTE_ID" }, 2);
            final Join ruleToActionJoin = new Join("ActionToActionAttributes", "RuleToAction", new String[] { "ACTION_ID" }, new String[] { "ACTION_ID" }, 2);
            final Join ruleEngineJoin = new Join("RuleToAction", "RuleEngine", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(commandDataJoin);
            selectQuery.addJoin(commandDatatoMdCommandJoin);
            selectQuery.addJoin(mdCommandsJoin);
            selectQuery.addJoin(complianceToResourceJoin);
            selectQuery.addJoin(recentProfileJoin);
            selectQuery.addJoin(profileHistoryJoin);
            selectQuery.addJoin(actionToActionAttributeJoin);
            selectQuery.addJoin(ruleToActionJoin);
            selectQuery.addJoin(ruleEngineJoin);
            selectQuery.addJoin(profileJoin);
            final Long currentTime = MDMUtil.getCurrentTimeInMillis();
            final Criteria timeCriteria = new Criteria(Column.getColumn("CompliancePendingActions", "EXECUTION_TIME"), (Object)currentTime, 6);
            selectQuery.addSelectColumn(new Column("CompliancePendingActions", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(new Column("CompliancePendingActions", "COMMAND_DATA_ID"));
            selectQuery.addSelectColumn(new Column("CompliancePendingActions", "EXECUTION_TIME"));
            selectQuery.addSelectColumn(new Column("CommandData", "COMMAND_DATA_ID"));
            selectQuery.addSelectColumn(new Column("CommandData", "COMMAND_TYPE"));
            selectQuery.addSelectColumn(new Column("CommandDataToMDCommand", "COMMAND_ID"));
            selectQuery.addSelectColumn(new Column("CommandDataToMDCommand", "COMMAND_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(new Column("ComplianceToResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("ComplianceToResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "ASSOCIATED_BY"));
            selectQuery.addSelectColumn(new Column("ActionToActionAttributes", "ACTION_ID"));
            selectQuery.addSelectColumn(new Column("ActionToActionAttributes", "ACTION_ATTRIBUTE_ID"));
            selectQuery.addSelectColumn(new Column("RuleEngine", "RULE_ID"));
            selectQuery.addSelectColumn(new Column("RuleEngine", "RULE_NAME"));
            selectQuery.addSelectColumn(new Column("RuleEngine", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
            selectQuery.setCriteria(timeCriteria);
            final JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
            final List toDeleteList = new ArrayList();
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final JSONObject tempJSON = (JSONObject)resultJSONArray.get(i);
                final Long complianceStatusId = (Long)tempJSON.get((Object)"COMPLIANCE_STATUS_ID");
                final int commandType = (int)tempJSON.get((Object)"COMMAND_TYPE");
                final Long commandId = (Long)tempJSON.get((Object)"COMMAND_ID");
                final Long resourceId = (Long)tempJSON.get((Object)"RESOURCE_ID");
                final Long userId = (Long)tempJSON.get((Object)"ASSOCIATED_BY");
                final Long collectionId = (Long)tempJSON.get((Object)"COLLECTION_ID");
                final String ruleName = (String)tempJSON.get((Object)"RULE_NAME");
                final String complianceName = (String)tempJSON.get((Object)"PROFILE_NAME");
                final Long actionAttributeId = (Long)tempJSON.get((Object)"ACTION_ATTRIBUTE_ID");
                final Long customerId = (Long)tempJSON.get((Object)"CUSTOMER_ID");
                final Long ruleId = (Long)tempJSON.get((Object)"RULE_ID");
                final ActionEngineHandler actionEngineHandler = ActionEngineHandler.getInstance();
                final org.json.JSONObject requestJSON = new org.json.JSONObject();
                final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
                final String deviceName = deviceDetails.name;
                final int platformType = deviceDetails.platform;
                requestJSON.put("command_id", (Object)commandId);
                requestJSON.put("resource_id", (Object)resourceId);
                requestJSON.put("collection_id", (Object)collectionId);
                requestJSON.put("rule_name", (Object)ruleName);
                requestJSON.put("user_id", (Object)userId);
                requestJSON.put("customer_id", (Object)customerId);
                requestJSON.put("device_name", (Object)deviceName);
                requestJSON.put("platform_type", platformType);
                requestJSON.put("compliance_name", (Object)complianceName);
                requestJSON.put("action_attribute_id", (Object)actionAttributeId);
                requestJSON.put("rule_id", (Object)ruleId);
                switch (commandType) {
                    case 1: {
                        actionEngineHandler.sendMailAlert(requestJSON);
                        break;
                    }
                    case 2: {
                        actionEngineHandler.sendLostMode(requestJSON);
                        break;
                    }
                    case 3: {
                        actionEngineHandler.sendCompleteWipe(requestJSON);
                        break;
                    }
                    case 5: {
                        actionEngineHandler.sendMarkAsNonCompliant(requestJSON);
                        break;
                    }
                }
                toDeleteList.add(complianceStatusId);
            }
            final Criteria deleteCriteria = new Criteria(Column.getColumn("CompliancePendingActions", "COMPLIANCE_STATUS_ID"), (Object)toDeleteList.toArray(), 8);
            MDMUtil.getPersistence().delete(deleteCriteria);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- ComplianceDelayedActionExecutionTask > Error   ", e);
        }
        this.logger.log(Level.INFO, "Finished executing pending actions");
    }
}
