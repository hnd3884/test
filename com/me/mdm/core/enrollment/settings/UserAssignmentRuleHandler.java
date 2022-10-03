package com.me.mdm.core.enrollment.settings;

import java.util.Arrays;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentConstants;
import java.util.HashMap;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class UserAssignmentRuleHandler
{
    public Logger logger;
    public static List serverAsTemplateList;
    public static List modernTemplateList;
    
    public UserAssignmentRuleHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public Long createAssignmentRule(final JSONObject userRule) throws Exception {
        final String ruleName = userRule.optString("rule_name", "User Assignment Rule");
        final Long userID = userRule.getLong("user_id");
        final Long customerID = userRule.getLong("customer_id");
        final Row row = new Row("OnBoardingRule");
        row.set("RULE_NAME", (Object)ruleName);
        row.set("CUSTOMER_ID", (Object)customerID);
        row.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        row.set("LAST_MODIFIED_BY", (Object)userID);
        final DataObject dataObject = (DataObject)new WritableDataObject();
        dataObject.addRow(row);
        MDMUtil.getPersistenceLite().add(dataObject);
        return (Long)row.get("ON_BOARD_RULE_ID");
    }
    
    public JSONObject persistRules(final JSONObject rules, final Long userAssignmentRuleID, final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        final DataObject dataObject = this.getUserAssignmentDO(userAssignmentRuleID);
        dataObject.deleteRows("UserRule", (Criteria)null);
        dataObject.deleteRows("DeviceNameRule", (Criteria)null);
        dataObject.deleteRows("DeviceTypeRule", (Criteria)null);
        dataObject.deleteRows("GroupAssignmentRule", (Criteria)null);
        dataObject.deleteRows("AgentOnBoardParams", (Criteria)null);
        JSONArray userJSON = rules.optJSONArray("user_rules");
        userJSON = new UserRuleHandler().createUserRules(userJSON, userAssignmentRuleID, dataObject, customerID);
        if (userJSON != null) {
            response.put("user_rules", (Object)userJSON);
        }
        JSONArray groupJSON = rules.optJSONArray("group_rules");
        groupJSON = new GroupAssignmentHandler().createGroupRules(groupJSON, userAssignmentRuleID, dataObject, customerID);
        if (groupJSON != null) {
            response.put("group_rules", (Object)groupJSON);
        }
        JSONArray deviceModelJSON = rules.optJSONArray("device_model_rules");
        deviceModelJSON = new DeviceTypeHandler().createDeviceTypeRules(deviceModelJSON, userAssignmentRuleID, dataObject);
        if (deviceModelJSON != null) {
            response.put("device_model_rules", (Object)deviceModelJSON);
        }
        JSONArray devicenameJSON = rules.optJSONArray("device_name_rules");
        devicenameJSON = new DeviceNameHandler().createDeviceNameRules(devicenameJSON, userAssignmentRuleID, dataObject);
        if (devicenameJSON != null) {
            response.put("device_name_rules", (Object)devicenameJSON);
        }
        JSONArray agentParamsRules = rules.optJSONArray("agent_param_rules");
        agentParamsRules = new AgentParamRuleHandler().createAgentParamRules(agentParamsRules, userAssignmentRuleID, dataObject);
        if (agentParamsRules != null) {
            response.put("agent_param_rules", (Object)agentParamsRules);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
        this.getRulesFromDO(dataObject, response);
        return response;
    }
    
    public JSONObject getUserAssignmentRule(final Long userRuleID) throws Exception {
        JSONObject jsonObject = null;
        final DataObject dataObject = this.getUserAssignmentDO(userRuleID);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("OnBoardingRule");
            jsonObject = this.getRuleJSON(row);
        }
        this.getRulesFromDO(dataObject, jsonObject);
        return jsonObject;
    }
    
    private void getRulesFromDO(final DataObject dataObject, final JSONObject jsonObject) throws Exception {
        Iterator iterator = dataObject.getRows("UserRule");
        JSONArray ruleArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Row userRow = dataObject.getRow("UserRuleMapping", new Criteria(Column.getColumn("UserRuleMapping", "USER_RULE_ID"), row.get("USER_RULE_ID"), 0));
            ruleArray.put((Object)UserRuleHandler.getUserJSON(row, userRow));
        }
        if (ruleArray.length() > 0) {
            jsonObject.put("user_rules", (Object)ruleArray);
        }
        iterator = dataObject.getRows("GroupAssignmentRule");
        ruleArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Row resRow = dataObject.getRow("GroupRuleMapping", new Criteria(Column.getColumn("GroupRuleMapping", "GROUP_RULE_ID"), row.get("GROUP_RULE_ID"), 0));
            ruleArray.put((Object)GroupAssignmentHandler.getGroupRuleJSON(row, resRow));
        }
        if (ruleArray.length() > 0) {
            jsonObject.put("group_rules", (Object)ruleArray);
        }
        iterator = dataObject.getRows("DeviceTypeRule");
        ruleArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            ruleArray.put((Object)DeviceTypeHandler.getDeviceTypeJSON(row));
        }
        if (ruleArray.length() > 0) {
            jsonObject.put("device_model_rules", (Object)ruleArray);
        }
        iterator = dataObject.getRows("DeviceNameRule");
        ruleArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            ruleArray.put((Object)DeviceNameHandler.getDeviceNameJSON(row));
        }
        if (ruleArray.length() > 0) {
            jsonObject.put("device_name_rules", (Object)ruleArray);
        }
        iterator = dataObject.getRows("AgentOnBoardParams");
        ruleArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            ruleArray.put((Object)AgentParamRuleHandler.getAgentParamsRuleJSON(row));
        }
        if (ruleArray.length() > 0) {
            jsonObject.put("agent_param_rules", (Object)ruleArray);
        }
    }
    
    private DataObject getUserAssignmentDO(final Long userRuleID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OnBoardingRule"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("OnBoardingRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0));
        new UserRuleHandler().getUserRuleJoin(selectQuery);
        new DeviceTypeHandler().getDeviceTypeJoin(selectQuery);
        new DeviceNameHandler().getDeviceNameJoin(selectQuery);
        new GroupAssignmentHandler().getGroupJoin(selectQuery);
        new AgentParamRuleHandler().getAgentParams(selectQuery);
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingRule", "*"));
        selectQuery.addSelectColumn(Column.getColumn("UserRule", "*"));
        selectQuery.addSelectColumn(Column.getColumn("UserRuleMapping", "*"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceTypeRule", "*"));
        selectQuery.addSelectColumn(Column.getColumn("GroupAssignmentRule", "*"));
        selectQuery.addSelectColumn(Column.getColumn("GroupRuleMapping", "*"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceNameRule", "*"));
        selectQuery.addSelectColumn(Column.getColumn("AgentOnBoardParams", "*"));
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    private DataObject getUserAssignmentDOForTemplate(final Long templateID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OnBoardingRule"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        new UserRuleHandler().getUserRuleJoin(selectQuery);
        new DeviceTypeHandler().getDeviceTypeJoin(selectQuery);
        new DeviceNameHandler().getDeviceNameJoin(selectQuery);
        new GroupAssignmentHandler().getGroupJoin(selectQuery);
        new AgentParamRuleHandler().getAgentParams(selectQuery);
        selectQuery.addJoin(new Join("OnBoardingRule", "OnBoardingSettings", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("OnBoardingSettings", "TEMPLATE_ID"), (Object)templateID, 0));
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    private JSONObject getRuleJSON(final Row row) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("CUSTOMER_ID".toLowerCase(), row.get("CUSTOMER_ID"));
        jsonObject.put("ON_BOARD_RULE_ID".toLowerCase(), row.get("ON_BOARD_RULE_ID"));
        jsonObject.put("LAST_MODIFIED_BY".toLowerCase(), row.get("LAST_MODIFIED_BY"));
        jsonObject.put("LAST_MODIFIED_TIME".toLowerCase(), row.get("LAST_MODIFIED_TIME"));
        jsonObject.put("RULE_NAME".toLowerCase(), row.get("RULE_NAME"));
        return jsonObject;
    }
    
    public void replicateRulesFromOtherTemplates(final Long templateID, final Integer templateType, final Long customerID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        selectQuery.addJoin(new Join("EnrollmentTemplate", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria typeCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        selectQuery.setCriteria(customerCriteria.and(typeCriteria));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "ON_BOARD_RULE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "TEMPLATE_ID"));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("OnBoardingSettings");
                final Row settingsRow = new Row("OnBoardingSettings");
                settingsRow.set("TEMPLATE_ID", (Object)templateID);
                settingsRow.set("ON_BOARD_RULE_ID", row.get("ON_BOARD_RULE_ID"));
                dataObject.addRow(settingsRow);
                MDMUtil.getPersistenceLite().update(dataObject);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception when trying to replicate user assignment setting while user creation ", (Throwable)e);
        }
    }
    
    public void applyAssignUserRules(final JSONArray resourceProps, final int templateType) throws Exception {
        if (resourceProps.length() == 0) {
            return;
        }
        final JSONObject jsonObject = resourceProps.getJSONObject(0);
        final Long customerID = jsonObject.getLong("customer_id");
        if (!UserAssignmentRuleHandler.serverAsTemplateList.contains(templateType) || UserAssignmentRuleHandler.modernTemplateList.contains(templateType)) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
            selectQuery.addJoin(new Join("EnrollmentTemplate", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria templatecriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
            selectQuery.setCriteria(criteria.and(templatecriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("OnBoardingSettings");
                this.applyAssignUserRules(resourceProps, (Long)row.get("TEMPLATE_ID"));
            }
        }
    }
    
    public void applyAssignUserRules(final JSONArray resourceProps, final String templateToken) throws Exception {
        if (templateToken != null) {
            this.applyAssignUserRules(resourceProps, EnrollmentTemplateHandler.getTemplateIDForTemplateToken(templateToken));
        }
    }
    
    public void applyAssignUserRules(JSONArray resourceProps, final Long templateID) throws Exception {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("pauseAutoAssignUser")) {
            this.logger.log(Level.INFO, "Auto user assignment is paused to not evaluating rules ");
            return;
        }
        this.logger.log(Level.INFO, "Going to apply assign user rule for Devices {0} of template ID {1}", new Object[] { resourceProps, templateID });
        final BaseUserAssignmentHandler userAssignmentInterface = BaseUserAssignmentHandler.getInstance(templateID);
        final DataObject dataObject = this.getUserAssignmentDOForTemplate(templateID);
        if (!dataObject.isEmpty()) {
            final Long customerID = (Long)dataObject.getFirstRow("OnBoardingRule").get("CUSTOMER_ID");
            this.logger.log(Level.INFO, "The customer ID from the rule is {0}", customerID);
            resourceProps = userAssignmentInterface.removeDevicesNotToBeAssigned(resourceProps, customerID, dataObject);
            if (resourceProps.length() != 0) {
                final HashMap modelSeperatedMap = this.getModelSegregationForResourceList(resourceProps, dataObject);
                for (final int model : modelSeperatedMap.keySet()) {
                    final HashMap modelMap = modelSeperatedMap.get(model);
                    final Long userRuleID = modelMap.get("ON_BOARD_RULE_ID");
                    final JSONArray devices = modelMap.get("devices");
                    if (userRuleID != null) {
                        if (devices == null) {
                            continue;
                        }
                        this.logger.log(Level.INFO, "Going to assign for Model Type : {0}, with User rule ID : {1}, device list {2}", new Object[] { model, userRuleID, devices });
                        final Boolean isDelegatedAssignment = new UserRuleHandler().isDelegatedRuleSet(dataObject.getRows("UserRule", new Criteria(Column.getColumn("UserRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0)));
                        if (isDelegatedAssignment) {
                            final Iterator ruleItr = dataObject.getRows("UserRule", new Criteria(Column.getColumn("UserRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0));
                            final JSONObject rules = new JSONObject();
                            final JSONArray userRules = new JSONArray();
                            while (ruleItr.hasNext()) {
                                final Row row = ruleItr.next();
                                userRules.put((Object)row.getAsJSON());
                            }
                            rules.put("user_rules", (Object)userRules);
                            this.logger.log(Level.INFO, "Going To delegate User assignment for {0} with rules {1} ", new Object[] { devices, rules });
                            userAssignmentInterface.delegateUserAssignmentRule(devices, rules);
                            this.logger.log(Level.INFO, "Delgation is complete ");
                        }
                        else {
                            this.logger.log(Level.INFO, "non delegated rule set identified");
                            final Row row2 = dataObject.getRow("UserRule", new Criteria(Column.getColumn("UserRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0));
                            if (row2 == null) {
                                continue;
                            }
                            final Integer ruleType = (Integer)row2.get("RULE_TYPE");
                            JSONArray assignArray = null;
                            if (ruleType.equals(MDMEnrollmentConstants.UserAssignmentRules.UserRules.AUTHENTICATED_USER_TYPE)) {
                                assignArray = userAssignmentInterface.getAuthenticatedUser(devices, customerID);
                                this.logger.log(Level.INFO, "Authenticated user type user identified as {0}", assignArray);
                            }
                            else if (ruleType.equals(MDMEnrollmentConstants.UserAssignmentRules.UserRules.SAME_USER_TYPE)) {
                                final Long userID = (Long)dataObject.getRow("UserRuleMapping", new Criteria(Column.getColumn("UserRuleMapping", "USER_RULE_ID"), row2.get("USER_RULE_ID"), 0)).get("MANAGED_USER_ID");
                                final HashMap userMap = ManagedUserHandler.getInstance().getManagedUserDetails(userID);
                                assignArray = new JSONArray();
                                for (int i = 0; i < devices.length(); ++i) {
                                    final JSONObject assignUserJSON = devices.getJSONObject(i);
                                    assignUserJSON.put("UserName", userMap.get("NAME"));
                                    assignUserJSON.put("DomainName", userMap.get("DOMAIN_NETBIOS_NAME"));
                                    final String email = userMap.get("EMAIL_ADDRESS");
                                    assignUserJSON.put("EmailAddr", (Object)email);
                                    if (MDMStringUtils.isEmpty(email)) {
                                        assignUserJSON.put("skip_user_validation", true);
                                    }
                                    assignArray.put((Object)assignUserJSON);
                                }
                                this.logger.log(Level.INFO, "same user type user identified as {0}", assignArray);
                            }
                            if (assignArray == null || assignArray.length() <= 0) {
                                continue;
                            }
                            this.completeUserAssignmentForDevice(assignArray, dataObject, userRuleID);
                        }
                    }
                }
            }
        }
    }
    
    protected void completeUserAssignmentForDevice(final JSONArray resourceProps, final DataObject dataObject, final Long userRuleID) throws Exception {
        final List<JSONObject> list = new ArrayList<JSONObject>();
        final Row nameRow = dataObject.getRow("DeviceNameRule", new Criteria(Column.getColumn("DeviceNameRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0));
        int counter = 0;
        String nameRegex = null;
        if (nameRow != null) {
            nameRegex = (String)nameRow.get("DEVICE_NAME_REGEX");
            counter = (int)nameRow.get("COUNTER");
        }
        for (int i = 0; i < resourceProps.length(); ++i) {
            final JSONObject assignUserJSON = resourceProps.getJSONObject(i);
            final Long customerID = (Long)dataObject.getFirstRow("OnBoardingRule").get("CUSTOMER_ID");
            assignUserJSON.put("CustomerId", (Object)customerID);
            final Iterator iterator = dataObject.getRows("GroupAssignmentRule", new Criteria(Column.getColumn("GroupAssignmentRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0));
            final JSONArray groupIds = new JSONArray();
            while (iterator.hasNext()) {
                final Row groupRow = iterator.next();
                final Row resRow = dataObject.getRow("GroupRuleMapping", new Criteria(Column.getColumn("GroupRuleMapping", "GROUP_RULE_ID"), groupRow.get("GROUP_RULE_ID"), 0));
                groupIds.put(resRow.get("GROUP_RESOURCE_ID"));
            }
            if (groupIds.length() > 0) {
                assignUserJSON.put("GroupId", (Object)groupIds);
            }
            if (nameRegex != null) {
                assignUserJSON.put("DeviceName", (Object)nameRegex.replaceAll("%counter%", String.valueOf(++counter)));
            }
            assignUserJSON.put("primary_key", i);
            list.add(assignUserJSON);
        }
        final Long techID = (Long)dataObject.getRow("OnBoardingRule", new Criteria(Column.getColumn("OnBoardingRule", "ON_BOARD_RULE_ID"), (Object)userRuleID, 0)).get("LAST_MODIFIED_BY");
        final Long templateID = (Long)dataObject.getFirstRow("OnBoardingSettings").get("TEMPLATE_ID");
        final JSONObject templateDetails = new EnrollmentTemplateHandler().getTemplateDetailsFromTemplateID(templateID);
        final Integer templateType = templateDetails.getInt("TEMPLATE_TYPE");
        final Integer platform = templateDetails.getInt("PLATFORM_TYPE");
        final JSONObject result = AdminEnrollmentHandler.assignUser(list, techID, templateType, "primary_key", platform);
        this.logger.log(Level.INFO, "result of Auto Assign user {0}", result);
        if (nameRegex != null) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DeviceNameRule");
            updateQuery.setCriteria(new Criteria(Column.getColumn("DeviceNameRule", "NAME_RULE_ID"), nameRow.get("NAME_RULE_ID"), 0));
            updateQuery.setUpdateColumn("COUNTER", (Object)counter);
            MDMUtil.getPersistenceLite().update(updateQuery);
        }
    }
    
    private HashMap getModelSegregationForResourceList(final JSONArray devices, final DataObject dataObject) throws DataAccessException {
        final HashMap hashMap = new HashMap();
        final Iterator iterator = dataObject.getRows("DeviceTypeRule");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final int model = (int)row.get("MODEL_TYPE");
            HashMap modelMap = hashMap.get(model);
            if (modelMap == null) {
                modelMap = new HashMap();
            }
            final Long userAssignmentRule = (Long)row.get("ON_BOARD_RULE_ID");
            modelMap.put("ON_BOARD_RULE_ID", userAssignmentRule);
            hashMap.put(model, modelMap);
        }
        for (int i = 0; i < devices.length(); ++i) {
            final JSONObject jsonObject = devices.getJSONObject(i);
            final JSONObject additionalContext = jsonObject.optJSONObject("additional_context");
            final int model2 = additionalContext.getInt("device_model");
            final HashMap modelMap2 = hashMap.get(model2);
            if (modelMap2 != null) {
                JSONArray jsonArray = modelMap2.get("devices");
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                }
                jsonArray.put((Object)jsonObject);
                modelMap2.put("devices", jsonArray);
            }
        }
        return hashMap;
    }
    
    public JSONObject getOrSetDefaultValues(final Long customerID) throws Exception {
        JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OnBoardingSettings"));
        selectQuery.addJoin(new Join("OnBoardingSettings", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)new Integer[] { 12, 33 }, 8);
        selectQuery.setCriteria(customerCriteria.and(templateCriteria));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "ON_BOARD_RULE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            jsonObject = this.createAndUpdateDefaultUserAssignmentRules(customerID, 33);
        }
        else {
            final Row row = dataObject.getFirstRow("OnBoardingSettings");
            jsonObject = this.getUserAssignmentRule((Long)row.get("ON_BOARD_RULE_ID"));
        }
        return jsonObject;
    }
    
    private JSONObject createAndUpdateDefaultUserAssignmentRules(final Long customerID, final Integer templateType) throws Exception {
        Long userId = null;
        final JSONObject userIdJson = ManagedUserHandler.getInstance().getManagedUserIdAndAAAUserIdForAdmin(customerID, Boolean.TRUE);
        userId = userIdJson.getLong("USER_ID");
        final JSONObject userRuleJSON = new JSONObject();
        userRuleJSON.put("rule_name", (Object)"Modern Mgmt Enrollment Default Rule");
        userRuleJSON.put("user_id", (Object)userId);
        userRuleJSON.put("customer_id", (Object)customerID);
        final Long userRuleID = this.createAssignmentRule(userRuleJSON);
        this.logger.log(Level.INFO, "User assignment Rule created {0}, ID : {1}", new Object[] { userRuleJSON, userRuleID });
        final JSONObject rules = new JSONObject();
        final JSONArray userRulesArray = new JSONArray();
        final JSONObject domainRule = new JSONObject();
        domainRule.put("RULE_TYPE".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.UserRules.FIRST_LOGGED_IN_USER_TYPE);
        domainRule.put("CRITERIA".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA);
        userRulesArray.put((Object)domainRule);
        final JSONObject workgroupRule = new JSONObject();
        workgroupRule.put("RULE_TYPE".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.UserRules.WORKGROUP_USER);
        workgroupRule.put("CRITERIA".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA);
        userRulesArray.put((Object)workgroupRule);
        rules.put("user_rules", (Object)userRulesArray);
        final JSONObject response = this.persistRules(rules, userRuleID, customerID);
        this.logger.log(Level.INFO, "Persisted Rules for the ID {0}", response);
        response.put("on_board_rule_id", (Object)userRuleID);
        response.put("LAST_MODIFIED_BY".toLowerCase(), (Object)userId);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        selectQuery.setCriteria(customerCriteria.and(templateCriteria));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("EnrollmentTemplate");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Row settingsRow = new Row("OnBoardingSettings");
            settingsRow.set("ON_BOARD_RULE_ID", (Object)userRuleID);
            settingsRow.set("TEMPLATE_ID", row.get("TEMPLATE_ID"));
            dataObject.addRow(settingsRow);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
        this.logger.log(Level.INFO, "Mapping Updated for the template {0} and rule {1}", new Object[] { templateType, userId });
        return response;
    }
    
    public void createAdminUserRuleForTemplate(final Long customerID, final Integer templateType) throws Exception {
        Long userId = null;
        final SelectQuery templateQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        templateQuery.addJoin(new Join("EnrollmentTemplate", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        templateQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        templateQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "ADDED_USER"));
        templateQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "TEMPLATE_ID"));
        templateQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "ON_BOARD_RULE_ID"));
        templateQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0).and(new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0)));
        final DataObject DO = MDMUtil.getPersistenceLite().get(templateQuery);
        if (DO.getRow("EnrollmentTemplate") == null || DO.getRow("OnBoardingSettings") != null) {
            return;
        }
        final JSONObject userIdJson = ManagedUserHandler.getInstance().getManagedUserIdAndAAAUserIdForAdmin(customerID, Boolean.TRUE);
        userId = userIdJson.getLong("USER_ID");
        final JSONObject userRuleJSON = new JSONObject();
        userRuleJSON.put("rule_name", (Object)"Modern Mgmt Enrollment Default Rule");
        if (templateType != 10) {
            userRuleJSON.put("user_id", (Object)userId);
        }
        else {
            final Row row = DO.getFirstRow("EnrollmentTemplate");
            userRuleJSON.put("user_id", row.get("ADDED_USER"));
        }
        userRuleJSON.put("customer_id", (Object)customerID);
        final Long userRuleID = this.createAssignmentRule(userRuleJSON);
        this.logger.log(Level.INFO, "User assignment Rule created {0}, ID : {1}", new Object[] { userRuleJSON, userRuleID });
        final JSONObject rules = new JSONObject();
        final JSONArray userRulesArray = new JSONArray();
        final JSONObject domainRule = new JSONObject();
        domainRule.put("RULE_TYPE".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.UserRules.SAME_USER_TYPE);
        domainRule.put("CRITERIA".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA);
        domainRule.put("MANAGED_USER_ID".toLowerCase(), userIdJson.getLong("MANAGED_USER_ID"));
        userRulesArray.put((Object)domainRule);
        final JSONArray modelArray = new JSONArray();
        final JSONObject model = new JSONObject();
        model.put("MODEL_TYPE".toLowerCase(), (Object)MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
        modelArray.put((Object)model);
        rules.put("user_rules", (Object)userRulesArray);
        rules.put("device_model_rules", (Object)modelArray);
        final JSONObject response = this.persistRules(rules, userRuleID, customerID);
        this.logger.log(Level.INFO, "Persisted Rules for the ID {0}", response);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        selectQuery.setCriteria(customerCriteria.and(templateCriteria));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("EnrollmentTemplate");
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            final Row settingsRow = new Row("OnBoardingSettings");
            settingsRow.set("ON_BOARD_RULE_ID", (Object)userRuleID);
            settingsRow.set("TEMPLATE_ID", row2.get("TEMPLATE_ID"));
            dataObject.addRow(settingsRow);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
        this.logger.log(Level.INFO, "Mapping Updated for the template {0} and rule {1}", new Object[] { templateType, userId });
        new DeviceForEnrollmentHandler().applyAssignUserRulesForPendingDevices();
    }
    
    public void postUserAssignmentSettingsforAllCustomers(final Boolean force) {
        try {
            final Long[] customerIdsFromDB;
            final Long[] list = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (final Long customerID : customerIdsFromDB) {
                this.postUserAssignmentSettingsforCustomer(customerID, force);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "could not post data to DC", e);
        }
    }
    
    public void postUserAssignmentSettingsforCustomer(final Long customerID, final Boolean force) throws Exception {
        final String dataPosted = CustomerParamsHandler.getInstance().getParameterValue("UEMSettingPosted", (long)customerID);
        if (MDMStringUtils.isEmpty(dataPosted) || force) {
            this.logger.log(Level.INFO, "Is UEM settings posted for the customer : {0} - {1} | Force send - {2}", new Object[] { customerID, dataPosted, force });
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            final JSONObject response = MDMApiFactoryProvider.getMDMUtilAPI().getUserAssignmentRules(jsonObject);
            if (response != null && !response.has("Error")) {
                this.logger.log(Level.INFO, "posted UEM settings for customer : {0}", customerID);
                CustomerParamsHandler.getInstance().addOrUpdateParameter("UEMSettingPosted", "" + System.currentTimeMillis(), (long)customerID);
            }
        }
    }
    
    public JSONObject getAgentParamsForEnrolledDevices(final JSONArray devices) throws Exception {
        final List list = new ArrayList();
        for (int i = 0; i < devices.length(); ++i) {
            list.add(devices.get(i));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentRequestToDevice"));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        selectQuery.addJoin(new Join("OnBoardingSettings", "OnBoardingRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 2));
        selectQuery.addJoin(new Join("OnBoardingSettings", "AgentOnBoardParams", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDeviceExtn", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AgentOnBoardParams", "PARAM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("AgentOnBoardParams", "PARAM_VALUE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "GENERIC_IDENTIFIER"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)list.toArray(), 8));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONObject groupsJSON = new JSONObject();
        while (dmDataSetWrapper.next()) {
            final String agentParam = (String)dmDataSetWrapper.getValue("PARAM_VALUE");
            final Integer type = (Integer)dmDataSetWrapper.getValue("PARAM_TYPE");
            final String genericID = (String)dmDataSetWrapper.getValue("GENERIC_IDENTIFIER");
            JSONObject paramMap = groupsJSON.optJSONObject(agentParam);
            if (paramMap == null) {
                paramMap = new JSONObject();
            }
            JSONArray jsonArray = paramMap.optJSONArray(type.toString());
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            jsonArray.put((Object)genericID);
            paramMap.put(type.toString(), (Object)jsonArray);
            groupsJSON.put(agentParam, (Object)paramMap);
        }
        return groupsJSON;
    }
    
    static {
        UserAssignmentRuleHandler.serverAsTemplateList = Arrays.asList(10, 32, 12, 33);
        UserAssignmentRuleHandler.modernTemplateList = Arrays.asList(12, 33);
    }
}
