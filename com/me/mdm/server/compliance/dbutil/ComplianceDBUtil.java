package com.me.mdm.server.compliance.dbutil;

import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.text.ParseException;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.TimeZone;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.SortColumn;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.compliance.ComplianceProfileAssociationDataHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.GroupByClause;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.me.mdm.core.management.ManagementUtil;
import com.me.mdm.core.management.ManagementConstants;
import java.util.UUID;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.compliance.ComplianceFacade;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplianceDBUtil
{
    private static ComplianceDBUtil complianceDBUtil;
    private Logger logger;
    
    private ComplianceDBUtil() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- ComplianceDBUtil()   >   new object Creation  ");
    }
    
    public static ComplianceDBUtil getInstance() {
        if (ComplianceDBUtil.complianceDBUtil == null) {
            ComplianceDBUtil.complianceDBUtil = new ComplianceDBUtil();
        }
        return ComplianceDBUtil.complianceDBUtil;
    }
    
    public JSONObject addOrUpdateComplianceProfile(JSONObject requestJSON) throws Exception {
        try {
            DataObject collectionToRuleDO = null;
            final DataObject compliancePolicyDO = (DataObject)new WritableDataObject();
            final List existingRuleList = new ArrayList();
            Object collectionId = null;
            String profileName = null;
            Object profileId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            Long lastModifiedBy = null;
            if ((long)profileId == -1L) {
                final Row profileRow = new Row("Profile");
                final Row collectionRow = new Row("Collection");
                final Row recentProfileToCollnRow = new Row("RecentProfileToColln");
                final Row profileToCollectionRow = new Row("ProfileToCollection");
                String description = null;
                Long createdBy = null;
                final Long currentTime = MDMUtil.getCurrentTimeInMillis();
                profileName = String.valueOf(requestJSON.get("compliance_name"));
                description = String.valueOf(requestJSON.get("description"));
                createdBy = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
                final ComplianceFacade complianceFacade = new ComplianceFacade();
                if (complianceFacade.checkComplianceNameExists(customerId, profileName)) {
                    throw new APIHTTPException("GEO002", new Object[0]);
                }
                profileRow.set("PROFILE_NAME", (Object)profileName);
                profileRow.set("PLATFORM_TYPE", (Object)0);
                profileRow.set("PROFILE_TYPE", (Object)5);
                profileRow.set("PROFILE_IDENTIFIER", (Object)"--");
                final String profilePayloadIdentifier = "com.mdm.compliance" + UUID.randomUUID().toString() + profileName.replaceAll("[^a-zA-Z0-9]", "");
                profileRow.set("PROFILE_PAYLOAD_IDENTIFIER", (Object)profilePayloadIdentifier);
                profileRow.set("PROFILE_DESCRIPTION", (Object)description);
                profileRow.set("CREATED_BY", (Object)createdBy);
                profileRow.set("CREATION_TIME", (Object)currentTime);
                profileRow.set("LAST_MODIFIED_BY", (Object)createdBy);
                profileRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                final Row manaegementRow = new Row("ProfileToManagement");
                manaegementRow.set("PROFILE_ID", profileRow.get("PROFILE_ID"));
                manaegementRow.set("MANAGEMENT_ID", (Object)ManagementUtil.getManagementIDForType(ManagementConstants.Types.MOBILE_MGMT));
                compliancePolicyDO.addRow(profileRow);
                compliancePolicyDO.addRow(manaegementRow);
                profileId = profileRow.get("PROFILE_ID");
                final Row profileToCustomerRelRow = new Row("ProfileToCustomerRel");
                profileToCustomerRelRow.set("CUSTOMER_ID", (Object)customerId);
                profileToCustomerRelRow.set("PROFILE_ID", profileId);
                compliancePolicyDO.addRow(profileToCustomerRelRow);
                collectionRow.set("COLLECTION_NAME", (Object)profileName);
                collectionRow.set("DESCRIPTION", (Object)description);
                collectionRow.set("CREATION_TIME", (Object)currentTime);
                collectionRow.set("MODIFIED_TIME", (Object)currentTime);
                collectionRow.set("COLLECTION_TYPE", (Object)3);
                collectionRow.set("IS_CONFIG_COLLECTION", (Object)Boolean.FALSE);
                collectionRow.set("APPLY_TYPE", (Object)1);
                collectionRow.set("PLATFORM_ID", (Object)0);
                collectionRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                compliancePolicyDO.addRow(collectionRow);
                collectionId = collectionRow.get("COLLECTION_ID");
                profileToCollectionRow.set("PROFILE_ID", profileId);
                profileToCollectionRow.set("COLLECTION_ID", collectionId);
                profileToCollectionRow.set("PROFILE_VERSION", (Object)1);
                recentProfileToCollnRow.set("COLLECTION_ID", collectionId);
                recentProfileToCollnRow.set("PROFILE_ID", profileId);
                compliancePolicyDO.addRow(profileToCollectionRow);
                compliancePolicyDO.addRow(recentProfileToCollnRow);
            }
            else {
                String description2 = null;
                lastModifiedBy = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
                profileName = requestJSON.optString("compliance_name");
                description2 = String.valueOf(requestJSON.get("description"));
                requestJSON = this.getCollectionForComplianceProfile(requestJSON);
                collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
                final Long currentTime2 = MDMUtil.getCurrentTimeInMillis();
                final ComplianceFacade complianceFacade2 = new ComplianceFacade();
                if (complianceFacade2.checkComplianceNameExists(customerId, profileName) && !complianceFacade2.getComplianceNameForComplianceId(customerId, (Long)profileId).equals(profileName)) {
                    throw new APIHTTPException("GEO002", new Object[0]);
                }
                final Join collectionJoin = new Join("Profile", "Collection", new String[0], new String[0], 2);
                collectionJoin.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), collectionId, 0));
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
                selectQuery.setCriteria(new Criteria(new Column("Profile", "PROFILE_ID"), profileId, 0));
                selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
                selectQuery.addSelectColumn(new Column("Collection", "COLLECTION_ID"));
                selectQuery.addJoin(collectionJoin);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Row collectionRow2 = dataObject.getRow("Collection");
                collectionRow2.set("MODIFIED_TIME", (Object)currentTime2);
                collectionRow2.set("DESCRIPTION", (Object)description2);
                collectionRow2.set("COLLECTION_NAME", (Object)profileName);
                collectionRow2.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                dataObject.updateRow(collectionRow2);
                final String profilePayloadIdentifier2 = "com.mdm.compliance." + UUID.randomUUID().toString() + "." + profileName.replaceAll("[^a-zA-Z0-9]", "");
                final Row profileRow2 = dataObject.getRow("Profile");
                profileRow2.set("LAST_MODIFIED_TIME", (Object)currentTime2);
                profileRow2.set("LAST_MODIFIED_BY", (Object)lastModifiedBy);
                profileRow2.set("PROFILE_NAME", (Object)profileName);
                profileRow2.set("PROFILE_DESCRIPTION", (Object)description2);
                profileRow2.set("PROFILE_PAYLOAD_IDENTIFIER", (Object)profilePayloadIdentifier2);
                dataObject.updateRow(profileRow2);
                compliancePolicyDO.merge(dataObject);
                collectionToRuleDO = MDMUtil.getPersistence().get("CollectionToRules", new Criteria(new Column("CollectionToRules", "COLLECTION_ID"), collectionId, 0));
                final Iterator iterator = collectionToRuleDO.getRows("CollectionToRules");
                while (iterator.hasNext()) {
                    final Row collectionToRuleRow = iterator.next();
                    final Long ruleId = (Long)collectionToRuleRow.get("RULE_ID");
                    existingRuleList.add(ruleId);
                }
            }
            final DataObject policies = (DataObject)new WritableDataObject();
            JSONArray policyJSON = null;
            policyJSON = requestJSON.optJSONArray("policies");
            if (policyJSON != null) {
                for (int i = 0; i < policyJSON.length(); ++i) {
                    final JSONObject compliancePolicyJSON = policyJSON.getJSONObject(i);
                    compliancePolicyJSON.put("collection_id", collectionId);
                    compliancePolicyJSON.put("user_id", (Object)userId);
                    compliancePolicyJSON.put("customer_id", (Object)customerId);
                    final JSONObject actionJSON = new JSONObject();
                    final DataObject ruleEngineDO = (DataObject)new WritableDataObject();
                    if (compliancePolicyJSON.has("rule")) {
                        if (!existingRuleList.isEmpty()) {
                            existingRuleList.remove(JSONUtil.optLongForUVH(compliancePolicyJSON.getJSONObject("rule"), "rule_id", Long.valueOf(-1L)));
                        }
                        ruleEngineDO.merge(RuleEngineDBUtil.getInstance().addOrUpdateRuleEngine(compliancePolicyJSON));
                        final Object ruleId2 = ruleEngineDO.getRow("RuleEngine").get("RULE_ID");
                        policies.merge(ruleEngineDO);
                        actionJSON.put("rule_id", ruleId2);
                        if (compliancePolicyJSON.has("action")) {
                            actionJSON.put("action", compliancePolicyJSON.get("action"));
                            actionJSON.put("customer_id", (Object)customerId);
                            policies.merge(ActionEngineDBUtil.getInstance().addOrUpdateActionEngine(actionJSON));
                        }
                    }
                }
                if (!existingRuleList.isEmpty()) {
                    final JSONObject deleteRuleJSON = new JSONObject();
                    final JSONArray removeRuleJSONArray = new JSONArray();
                    for (int j = 0; j < existingRuleList.toArray().length; ++j) {
                        removeRuleJSONArray.put(existingRuleList.get(j));
                    }
                    deleteRuleJSON.put("remove_rule_list", (Object)removeRuleJSONArray);
                    RuleEngineDBUtil.getInstance().trashRule(deleteRuleJSON);
                }
            }
            compliancePolicyDO.merge(policies);
            MDMUtil.getPersistence().update(compliancePolicyDO);
            if (!compliancePolicyDO.isEmpty()) {
                if (!requestJSON.has("compliance_id")) {
                    profileId = compliancePolicyDO.getRow("Profile").get("PROFILE_ID");
                    collectionId = compliancePolicyDO.getRow("Collection").get("COLLECTION_ID");
                }
                else {
                    profileId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
                }
            }
            final JSONObject profileJSON = new JSONObject();
            profileJSON.put("compliance_id", profileId);
            profileJSON.put("collection_id", collectionId);
            profileJSON.put("customer_id", (Object)customerId);
            profileJSON.put("user_id", (Object)userId);
            profileJSON.put("compliance_name", (Object)profileName);
            profileJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            final Long profileCount = this.getComplianceProfileCount(customerId);
            if (profileCount == 1L) {
                final MDMAgentSettingsHandler agentHandler = new MDMAgentSettingsHandler();
                final JSONObject iosData = new JSONObject();
                iosData.put("CUSTOMER_ID", (Object)customerId);
                iosData.put("IS_NATIVE_APP_ENABLE", true);
                agentHandler.processiOSSettings(iosData);
            }
            final JSONObject statusJSON = new JSONObject();
            statusJSON.put("collection_id", collectionId);
            statusJSON.put("profile_collection_status", 110);
            this.addOrUpdateComplianceProfileStatus(statusJSON);
            return profileJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateComplianceProfile()    >   Error   ", e);
            throw e;
        }
    }
    
    public Long getComplianceProfileCount(final Long customerId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Join customerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.addJoin(customerJoin);
        Column countColumn = Column.getColumn("Profile", "PROFILE_ID");
        countColumn = countColumn.count();
        countColumn.setColumnAlias("TOTAL_COUNT");
        selectQuery.addSelectColumn(countColumn);
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
        selectQuery.setCriteria(profileCriteria.and(customerCriteria));
        final List groupByColList = new ArrayList();
        groupByColList.add(new Column("ProfileToCustomerRel", "CUSTOMER_ID"));
        final GroupByClause groupByClause = new GroupByClause(groupByColList);
        selectQuery.setGroupByClause(groupByClause);
        Long profileCount = 0L;
        try {
            profileCount = (long)DBUtil.getRecordCount(selectQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getComplianceProfileCount()    >   Error   ", e);
        }
        return profileCount;
    }
    
    private boolean isComplianceProfileWithDelayedActionsExist(final Long customerId) throws DataAccessException {
        try {
            Boolean isProfileWithDelayedActionsExist = true;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join customerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileToCollectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join collectionToRulesJoin = new Join("ProfileToCollection", "CollectionToRules", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join ruleToActionJoin = new Join("CollectionToRules", "RuleToAction", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join actionToActionAttributes = new Join("RuleToAction", "ActionToActionAttributes", new String[] { "ACTION_ID" }, new String[] { "ACTION_ID" }, 2);
            final Criteria profileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0);
            final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria timeCriteria = new Criteria(Column.getColumn("ActionToActionAttributes", "TIME_TO_EXECUTION"), (Object)0, 5);
            selectQuery.addJoin(customerJoin);
            selectQuery.addJoin(profileToCollectionJoin);
            selectQuery.addJoin(collectionToRulesJoin);
            selectQuery.addJoin(ruleToActionJoin);
            selectQuery.addJoin(actionToActionAttributes);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionToRules", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CollectionToRules", "RULE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToAction", "RULE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToAction", "ACTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ActionToActionAttributes", "ACTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ActionToActionAttributes", "ACTION_ATTRIBUTE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ActionToActionAttributes", "TIME_TO_EXECUTION"));
            selectQuery.setCriteria(profileCriteria.and(customerCriteria).and(timeCriteria).and(trashCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                isProfileWithDelayedActionsExist = false;
            }
            return isProfileWithDelayedActionsExist;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceProfileCount()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    private void addOrUpdateComplianceProfileStatus(final JSONObject statusJSON) throws JSONException, DataAccessException {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(statusJSON, "collection_id", Long.valueOf(-1L));
            final int status = statusJSON.getInt("profile_collection_status");
            final Row collectionStatusRow = new Row("CollectionStatus");
            collectionStatusRow.set("COLLECTION_ID", (Object)collectionId);
            collectionStatusRow.set("PROFILE_COLLECTION_STATUS", (Object)status);
            collectionStatusRow.set("IS_STATUS_COMPUTABLE", (Object)Boolean.TRUE);
            collectionStatusRow.set("STATUS", (Object)new Integer(1));
            collectionStatusRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(collectionStatusRow);
            MDMUtil.getPersistence().delete(collectionStatusRow);
            MDMUtil.getPersistence().add(dataObject);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateComplianceProfileStatus()    >   Error   ", e);
            throw e;
        }
    }
    
    protected DataObject mapRuleToAction(final JSONObject ruleActionJSON) throws JSONException, DataAccessException {
        try {
            Object ruleId = null;
            Object actionId = null;
            ruleId = ruleActionJSON.get("rule_id");
            actionId = ruleActionJSON.getJSONObject("action").get("action_id");
            final DataObject ruleToActionDO = (DataObject)new WritableDataObject();
            final Row ruleToActionRow = new Row("RuleToAction");
            ruleToActionRow.set("ACTION_ID", actionId);
            ruleToActionRow.set("RULE_ID", ruleId);
            if (actionId instanceof Long) {
                final Criteria actionCriteria = new Criteria(new Column("RuleToAction", "ACTION_ID"), actionId, 0);
                MDMUtil.getPersistence().delete(actionCriteria);
            }
            ruleToActionDO.addRow(ruleToActionRow);
            return ruleToActionDO;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- mapRuleToAction()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    protected DataObject mapCollectionToRules(final JSONObject rulesAndCollectionJSON) throws JSONException, DataAccessException {
        try {
            Object collectionId = null;
            Object ruleId = null;
            int evaluationOrder = 0;
            JSONArray removeRuleJSONArray = new JSONArray();
            JSONArray addRuleJSONArray = new JSONArray();
            collectionId = rulesAndCollectionJSON.get("collection_id");
            removeRuleJSONArray = rulesAndCollectionJSON.getJSONArray("remove_rule_list");
            addRuleJSONArray = rulesAndCollectionJSON.getJSONArray("add_rule_list");
            final DataObject dataObject = (DataObject)new WritableDataObject();
            if (addRuleJSONArray.length() != 0) {
                for (int i = 0; i < addRuleJSONArray.length(); ++i) {
                    final JSONObject addRuleJSON = addRuleJSONArray.getJSONObject(i);
                    ruleId = addRuleJSON.get("rule_id");
                    evaluationOrder = addRuleJSON.optInt("evaluation_order", -1);
                    final Row collectionToRulesRow = new Row("CollectionToRules");
                    collectionToRulesRow.set("COLLECTION_ID", collectionId);
                    collectionToRulesRow.set("EVALUATION_ORDER", (Object)evaluationOrder);
                    collectionToRulesRow.set("RULE_ID", ruleId);
                    dataObject.addRow(collectionToRulesRow);
                }
            }
            if (removeRuleJSONArray.length() != 0) {
                final List removeRuleList = new ArrayList();
                for (int j = 0; j < removeRuleJSONArray.length(); ++j) {
                    removeRuleList.add(removeRuleJSONArray.getLong(j));
                }
                final Criteria removeCollectionToRuleCriteria = new Criteria(new Column("CollectionToRules", "RULE_ID"), (Object)removeRuleList.toArray(), 8);
                MDMUtil.getPersistence().delete(removeCollectionToRuleCriteria);
            }
            return dataObject;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- mapCollectionToRules()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceProfile(final JSONObject requestJSON, final int params) throws JSONException, DataAccessException, SyMException {
        try {
            final JSONArray complianceJSONArray = new JSONArray();
            Long collectionId = null;
            collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Table collectionToRules = new Table("CollectionToRules");
            final SelectQuery collectionToRulesQuery = (SelectQuery)new SelectQueryImpl(collectionToRules);
            collectionToRulesQuery.addSelectColumn(new Column("CollectionToRules", "*"));
            collectionToRulesQuery.setCriteria(new Criteria(new Column("CollectionToRules", "COLLECTION_ID"), (Object)collectionId, 0));
            DataObject collectionDataObject = null;
            collectionDataObject = MDMUtil.getPersistence().get(collectionToRulesQuery);
            Iterator ruleIterator = null;
            ruleIterator = collectionDataObject.getRows("CollectionToRules");
            JSONObject complianceJSON = null;
            JSONObject ruleJSON = new JSONObject();
            JSONObject actionJSON = new JSONObject();
            while (ruleIterator.hasNext()) {
                complianceJSON = new JSONObject();
                final Row ruleRow = ruleIterator.next();
                final Long ruleId = (Long)ruleRow.get("RULE_ID");
                final int evaluationOrder = (int)ruleRow.get("EVALUATION_ORDER");
                ruleJSON.put("customer_id", (Object)JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L)));
                ruleJSON.put("rule_id", (Object)ruleId);
                ruleJSON.put("evaluation_order", evaluationOrder);
                if (params == 1) {
                    ruleJSON = RuleEngineDBUtil.getInstance().getRule(ruleJSON, params);
                    complianceJSON.put("rule", (Object)ruleJSON);
                    actionJSON = ActionEngineDBUtil.getInstance().getAction(ruleJSON);
                    complianceJSON.put("action", (Object)actionJSON);
                }
                else if (params == 2) {
                    ruleJSON = RuleEngineDBUtil.getInstance().getRule(ruleJSON, params);
                    ruleJSON.remove("creation_time");
                    ruleJSON.remove("last_modified_time");
                    ruleJSON.remove("last_modified_by");
                    ruleJSON.remove("created_by");
                    ruleJSON.remove("customer_id");
                    complianceJSON.put("rule", (Object)ruleJSON);
                    actionJSON = ActionEngineDBUtil.getInstance().getAction(ruleJSON);
                    complianceJSON.put("action", (Object)actionJSON);
                    complianceJSON.remove("last_modified_time");
                    complianceJSON.remove("creation_time");
                    complianceJSON.remove("created_by");
                    complianceJSON.remove("last_modified_by");
                }
                complianceJSONArray.put((Object)complianceJSON);
            }
            final JSONObject rulesForComplianceJSON = new JSONObject();
            rulesForComplianceJSON.put("policies", (Object)complianceJSONArray);
            return rulesForComplianceJSON;
        }
        catch (final JSONException | DataAccessException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceProfile()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceProfile(JSONObject messageJSON) throws JSONException, DataAccessException, SyMException {
        try {
            messageJSON = this.getCollectionForComplianceProfile(messageJSON);
            Long profileId = null;
            profileId = JSONUtil.optLongForUVH(messageJSON, "compliance_id", Long.valueOf(-1L));
            JSONObject complianceJSON = new JSONObject();
            if (profileId != -1L) {
                complianceJSON = this.getComplianceProfile(messageJSON, 1);
                complianceJSON.put("compliance_id", (Object)profileId);
                complianceJSON = this.getComplianceGeneralDetails(complianceJSON);
                complianceJSON.put("collection_id", (Object)JSONUtil.optLongForUVH(messageJSON, "collection_id", Long.valueOf(-1L)));
                return complianceJSON;
            }
            throw new UnsupportedOperationException(" compliance_id missing ");
        }
        catch (final JSONException | DataAccessException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceProfile()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceGeneralDetails(final JSONObject complianceJSON) throws JSONException, DataAccessException, SyMException {
        try {
            final Long profileId = JSONUtil.optLongForUVH(complianceJSON, "compliance_id", Long.valueOf(-1L));
            final Criteria profileCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join collectionJoin = new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(collectionJoin);
            selectQuery.setCriteria(profileCriteria);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileToColln", "PROFILE_ID"));
            final DataObject profileDO = MDMUtil.getPersistence().get(selectQuery);
            final Row profileRow = profileDO.getRow("Profile");
            final Row collectionRow = profileDO.getRow("RecentProfileToColln");
            final Long createdBy = (Long)profileRow.get("CREATED_BY");
            final Long modifiedBy = (Long)profileRow.get("LAST_MODIFIED_BY");
            complianceJSON.put("compliance_name", profileRow.get("PROFILE_NAME"));
            complianceJSON.put("description", profileRow.get("PROFILE_DESCRIPTION"));
            complianceJSON.put("created_by", (Object)createdBy);
            complianceJSON.put("creation_time", profileRow.get("CREATION_TIME"));
            complianceJSON.put("last_modified_by", (Object)modifiedBy);
            complianceJSON.put("created_by_name", (Object)DMUserHandler.getUserNameFromUserID(createdBy));
            complianceJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(modifiedBy));
            complianceJSON.put("last_modified_time", profileRow.get("LAST_MODIFIED_TIME"));
            complianceJSON.put("is_moved_to_trash", profileRow.get("IS_MOVED_TO_TRASH"));
            complianceJSON.put("collection_id", collectionRow.get("COLLECTION_ID"));
            return complianceJSON;
        }
        catch (final DataAccessException | JSONException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceGeneralDetails()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getCollectionForComplianceProfile(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            Long profileId = null;
            profileId = requestJSON.optLong("compliance_id", -1L);
            if (profileId == -1L) {
                profileId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
            }
            Long collectionId = null;
            final Table profileToCollectionTable = new Table("ProfileToCollection");
            final SelectQuery profileToCollectionQuery = (SelectQuery)new SelectQueryImpl(profileToCollectionTable);
            profileToCollectionQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            profileToCollectionQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            profileToCollectionQuery.setCriteria(new Criteria(new Column("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0));
            DataObject profileToCollectionDO = null;
            profileToCollectionDO = MDMUtil.getPersistence().get(profileToCollectionQuery);
            Row profileToCollectionRow = null;
            if (!profileToCollectionDO.isEmpty()) {
                profileToCollectionRow = profileToCollectionDO.getFirstRow("ProfileToCollection");
                collectionId = (Long)profileToCollectionRow.get("COLLECTION_ID");
            }
            requestJSON.put("collection_id", (Object)collectionId);
            return requestJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getCollectionForComplianceProfile()    >   Error   ", e);
            throw e;
        }
    }
    
    public void removeComplianceProfile(JSONObject requestJSON) throws Exception {
        try {
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final String userName = String.valueOf(requestJSON.get("user_name"));
            final JSONArray complianceList = requestJSON.getJSONArray("profile_list");
            final JSONArray removeRuleJSONArray = new JSONArray();
            for (int j = 0; j < complianceList.length(); ++j) {
                final Long complianceId = JSONUtil.optLongForUVH(complianceList, j, -1L);
                JSONObject complianceJSON = getInstance().getComplianceGeneralDetails(new JSONObject().put("compliance_id", (Object)complianceId));
                final String complianceName = String.valueOf(complianceJSON.get("compliance_name"));
                requestJSON.put("compliance_id", (Object)complianceId);
                requestJSON = this.getCollectionForComplianceProfile(requestJSON);
                Long collectionId = null;
                collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
                JSONObject rulesForComplianceJSON = new JSONObject();
                rulesForComplianceJSON = this.getComplianceProfile(requestJSON, 1);
                JSONArray complianceJSONArray = null;
                complianceJSONArray = rulesForComplianceJSON.getJSONArray("policies");
                for (int i = 0; i < complianceJSONArray.length(); ++i) {
                    complianceJSON = complianceJSONArray.getJSONObject(i);
                    final JSONObject ruleJSON = complianceJSON.getJSONObject("rule");
                    removeRuleJSONArray.put((Object)JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L)));
                }
                final Criteria collectionCriteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0);
                final UpdateQuery collectionUpdate = (UpdateQuery)new UpdateQueryImpl("Collection");
                collectionUpdate.setUpdateColumn("MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
                collectionUpdate.setUpdateColumn("IS_DELETED", (Object)Boolean.TRUE);
                collectionUpdate.setUpdateColumn("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                collectionUpdate.setCriteria(collectionCriteria);
                MDMUtil.getPersistence().update(collectionUpdate);
                final List profileList = new ArrayList();
                profileList.add(JSONUtil.optLongForUVH(complianceList, j, -1L));
                ProfileUtil.getInstance().markAsDeleted(profileList, JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L)));
                final JSONObject diassociationJSON = new JSONObject();
                diassociationJSON.put("customer_id", (Object)customerId);
                diassociationJSON.put("collection_id", (Object)collectionId);
                diassociationJSON.put("compliance_id", (Object)complianceId);
                diassociationJSON.put("user_id", (Object)userId);
                diassociationJSON.put("user_name", (Object)userName);
                ComplianceProfileAssociationDataHandler.getInstance().handleComplianceRemovalForAssociatedResources(diassociationJSON);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("customer_id", (Object)customerId);
                eventLogJSON.put("remarks", (Object)"mdm.compliance.deleted");
                eventLogJSON.put("remarks_args", (Object)(complianceName + "@@@" + userName));
                eventLogJSON.put("user_name", (Object)userName);
                eventLogJSON.put("event_id", 72403);
                getInstance().complianceEventLogEntry(eventLogJSON);
            }
            final JSONObject removeRuleJSON = new JSONObject();
            removeRuleJSON.put("remove_rule_list", (Object)removeRuleJSONArray);
            RuleEngineDBUtil.getInstance().trashRule(removeRuleJSON);
            if (ApiFactoryProvider.getSchedulerAPI().isScheduleCreated("ComplianceDelayedActionExecutionTask")) {
                final Boolean delayedActionSchedulerDisabled = ApiFactoryProvider.getSchedulerAPI().isSchedulerDisabled("ComplianceDelayedActionExecutionTask");
                if (!this.isComplianceProfileWithDelayedActionsExist(customerId) && !delayedActionSchedulerDisabled) {
                    ApiFactoryProvider.getSchedulerAPI().setSchedulerState(false, "ComplianceDelayedActionExecutionTask");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- removeComplianceProfile()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getComplianceProfileForCollection(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            Long collectionId = null;
            collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Table profileToCollectionTable = new Table("ProfileToCollection");
            final SelectQuery profileToCollectionQuery = (SelectQuery)new SelectQueryImpl(profileToCollectionTable);
            profileToCollectionQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            profileToCollectionQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            profileToCollectionQuery.setCriteria(new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
            DataObject profileToCollectionDO = null;
            profileToCollectionDO = MDMUtil.getPersistence().get(profileToCollectionQuery);
            Long profileId = null;
            Row profileToCollectionRow = null;
            profileToCollectionRow = profileToCollectionDO.getFirstRow("ProfileToCollection");
            profileId = (Long)profileToCollectionRow.get("PROFILE_ID");
            requestJSON.put("compliance_id", (Object)profileId);
            return requestJSON;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceProfileForCollection()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceToDeviceStatus(JSONObject deviceJSON) throws Exception {
        try {
            deviceJSON = this.getComplianceStatusID(deviceJSON);
            final Long complianceStatusId = JSONUtil.optLongForUVH(deviceJSON, "compliance_status_id", Long.valueOf(-1L));
            final String filter = deviceJSON.optString("filters", "--");
            final JSONObject complianceJSON = deviceJSON.getJSONObject("compliance_profile");
            final String complianceName = String.valueOf(complianceJSON.get("compliance_name"));
            final int platformType = deviceJSON.getInt("platform_type_id");
            Criteria complianceIdCriteria = new Criteria(Column.getColumn("ComplianceToDeviceStatus", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
            Criteria filterCriteria = null;
            DataObject dataObject = null;
            if (!filter.equalsIgnoreCase("--")) {
                final String[] split;
                final String[] filters = split = filter.split(",");
                for (final String s : split) {
                    final String filterKey = s;
                    switch (s) {
                        case "compliant": {
                            final List stateList = new ArrayList();
                            stateList.add(902);
                            stateList.add(904);
                            filterCriteria = new Criteria(new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATE"), (Object)stateList.toArray(), 8);
                            complianceIdCriteria = complianceIdCriteria.and(filterCriteria);
                            break;
                        }
                        case "non_compliant": {
                            final List stateList = new ArrayList();
                            stateList.add(903);
                            filterCriteria = new Criteria(new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATE"), (Object)stateList.toArray(), 8);
                            complianceIdCriteria = complianceIdCriteria.and(filterCriteria);
                            break;
                        }
                        case "in_progress": {
                            final List stateList = new ArrayList();
                            stateList.add(901);
                            stateList.add(905);
                            filterCriteria = new Criteria(new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATE"), (Object)stateList.toArray(), 8);
                            complianceIdCriteria = complianceIdCriteria.and(filterCriteria);
                            break;
                        }
                    }
                }
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToDeviceStatus"));
            final Join complianceToResourceJoin = new Join("ComplianceToDeviceStatus", "ComplianceToResource", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join managedDeviceJoin = new Join("ComplianceToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join ruleJoin = new Join("ComplianceToDeviceStatus", "RuleToDeviceRecentStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleHistoryJoin = new Join("RuleToDeviceRecentStatus", "RuleStatusHistory", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
            selectQuery.addJoin(ruleJoin);
            selectQuery.addJoin(ruleHistoryJoin);
            selectQuery.addJoin(complianceToResourceJoin);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToResource", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToDeviceRecentStatus", "*"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "REMARKS"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "REMARKS_ARGS"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_EVALUATED_TIME"));
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            selectQuery.setCriteria(complianceIdCriteria.and(managedDeviceCriteria));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final int complianceState = (int)dataObject.getRow("ComplianceToDeviceStatus").get("COMPLIANCE_STATE");
                final Long lastComplianceCheckTime = (Long)dataObject.getRow("ComplianceToDeviceStatus").get("LAST_EVALUATION_TIME");
                final int totalCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("TOTAL_COUNT");
                final int compliantRuleCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("COMPLIANT_RULE_COUNT");
                final int nonCompliantRuleCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("NON_COMPLIANT_RULE_COUNT");
                final int notApplicableRuleCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("NOT_APPLICABLE_COUNT");
                final int ruleCantBeEvaluatedCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("FAILED_TO_EVALUATE_COUNT");
                final int notificationSentCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("NOTIFICATION_SENT_COUNT");
                final int yetToEvalCount = (int)dataObject.getRow("ComplianceToDeviceStatus").get("YET_TO_EVALUATE_COUNT");
                deviceJSON.put("compliance_state", complianceState);
                deviceJSON.put("last_compliance_check", (Object)lastComplianceCheckTime);
                deviceJSON.put("total_rule_count", totalCount);
                deviceJSON.put("compliant_rule_count", compliantRuleCount);
                deviceJSON.put("notification_sent_count", notificationSentCount);
                deviceJSON.put("non_compliant_rule_count", nonCompliantRuleCount);
                deviceJSON.put("yet_to_evaluate_count", yetToEvalCount);
                deviceJSON.put("failed_to_evaluate_count", ruleCantBeEvaluatedCount);
                deviceJSON.put("not_applicable_count", notApplicableRuleCount);
                final Iterator iterator = dataObject.getRows("RuleStatusHistory");
                final String deviceName = String.valueOf(deviceJSON.get("device_name"));
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    String remarks = (String)row.get("REMARKS");
                    final int ruleState = (int)row.get("RULE_STATUS");
                    final Long ruleId = (Long)row.get("RULE_ID");
                    final String lastEvalTime = (String)row.get("RULE_EVALUATED_TIME");
                    final Long lastEvalTimeInMillis = this.getTimeInMillis(lastEvalTime);
                    final String ruleName = RuleEngineDBUtil.getInstance().getRuleName(ruleId, complianceJSON.getJSONArray("policies"));
                    if (ruleState == 805) {
                        final String remarksString = this.getRuleNotApplicableRemarks(deviceJSON, ruleId);
                        deviceJSON.put("remarks_args", (Object)remarksString);
                    }
                    else if (ruleState == 804) {
                        deviceJSON.put("remarks_args", (Object)remarks);
                    }
                    else {
                        if (ruleState != 806 || MDMUtil.getCurrentTimeInMillis() - lastEvalTimeInMillis <= 300000L) {
                            continue;
                        }
                        remarks = I18N.getMsg("mdm.compliance.rule_notification_sent", new Object[] { complianceName });
                        deviceJSON.put("remarks_args", (Object)remarks);
                    }
                }
                float complianceScore;
                if (compliantRuleCount == -1L) {
                    complianceScore = 0.0f;
                }
                else {
                    complianceScore = (float)((compliantRuleCount + notApplicableRuleCount / totalCount) * 100);
                }
                deviceJSON.put("compliance_score", (Object)String.valueOf(complianceScore));
            }
            final JSONObject actionJSON = this.getRecentActionForDevice(deviceJSON);
            return actionJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getComplianceToDeviceStatus()    >   Error   ", e);
            throw e;
        }
    }
    
    public Long getTimeInMillis(final String iso8601Time) throws Exception {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
        Date timeCreatedDate = null;
        try {
            timeCreatedDate = dateFormat.parse(iso8601Time);
            return timeCreatedDate.getTime();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getTimeInMillis()    >   Error   ", e);
            throw e;
        }
    }
    
    private String getRuleNotApplicableRemarks(final JSONObject deviceJSON, final Long ruleId) throws Exception {
        try {
            String ruleErrorRemarks = "";
            final int platformType = deviceJSON.getInt("platform_type_id");
            final String platformName = String.valueOf(deviceJSON.get("platform_type"));
            final JSONObject complianceJSON = deviceJSON.getJSONObject("compliance_profile");
            final JSONArray policiesJSON = complianceJSON.getJSONArray("policies");
            for (int i = 0; i < policiesJSON.length(); ++i) {
                final JSONObject ruleJSON = policiesJSON.getJSONObject(i).getJSONObject("rule");
                if (ruleId.equals(JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L)))) {
                    final JSONArray ruleCriterionsJSONArray = ruleJSON.getJSONArray("rule_criterions");
                    for (int j = 0; j < ruleCriterionsJSONArray.length(); ++j) {
                        final JSONObject ruleCriteriaJSON = ruleCriterionsJSONArray.getJSONObject(j);
                        final int ruleCriteriaType = ruleCriteriaJSON.getInt("rule_criteria_type");
                        String ruleType = "";
                        String link = "";
                        switch (ruleCriteriaType) {
                            case 2: {
                                ruleType = I18N.getMsg("mdm.compliance.geofencing", new Object[0]);
                                if (platformType == 3) {
                                    link = I18N.getMsg("mdm.compliance.windows_geofence_na_link", new Object[0]);
                                }
                                else if (platformType == 4) {
                                    link = I18N.getMsg("mdm.compliance.chrome_geofence_na_link", new Object[0]);
                                }
                                link = MDMUtil.replaceProductUrlLoaderValuesinText(link, null);
                                ruleErrorRemarks = I18N.getMsg("mdm.compliance.platform_not_applicable", new Object[] { ruleType, platformName, link });
                                break;
                            }
                        }
                    }
                }
            }
            return ruleErrorRemarks;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getRuleNotApplicableRemarks()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getRecentActionForDevice(final JSONObject deviceJSON) throws Exception {
        try {
            final Long complianceStatusId = JSONUtil.optLongForUVH(deviceJSON, "compliance_status_id", Long.valueOf(-1L));
            final SelectQuery actionStatusQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ActionAttributesToDeviceRecentStatus"));
            final Join commandHistoryJoin = new Join("ActionAttributesToDeviceRecentStatus", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
            actionStatusQuery.addSelectColumn(new Column("ActionAttributesToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"));
            actionStatusQuery.addSelectColumn(new Column("ActionAttributesToDeviceRecentStatus", "ACTION_ATTRIBUTE_ID"));
            actionStatusQuery.addSelectColumn(new Column("CommandHistory", "COMMAND_HISTORY_ID"));
            actionStatusQuery.addSelectColumn(new Column("CommandHistory", "UPDATED_TIME"));
            actionStatusQuery.addSelectColumn(new Column("CommandHistory", "COMMAND_ID"));
            actionStatusQuery.addJoin(commandHistoryJoin);
            actionStatusQuery.setCriteria(new Criteria(Column.getColumn("ActionAttributesToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0).and(new Criteria(Column.getColumn("CommandHistory", "COMMAND_STATUS"), (Object)2, 0)));
            final WritableDataObject dataObject = (WritableDataObject)MDMUtil.getPersistence().get(actionStatusQuery);
            dataObject.sortRows("CommandHistory", new SortColumn[] { new SortColumn("CommandHistory", "UPDATED_TIME", false) });
            String commandName = "--";
            if (!dataObject.isEmpty()) {
                final Row commandRow = dataObject.getFirstRow("CommandHistory");
                final Long commandId = (Long)commandRow.get("COMMAND_ID");
                commandName = (String)MDMUtil.getPersistence().get("MdCommands", new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandId, 0)).getRow("MdCommands").get("COMMAND_TYPE");
            }
            deviceJSON.put("last_action_taken", (Object)DeviceCommandRepository.getInstance().getI18NCommandName(commandName));
            return deviceJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getRecentActionForDevice()    >   Error   ", e);
            throw e;
        }
    }
    
    public void initiateComplianceDeviceStatus(final JSONObject complianceJSON) throws JSONException, DataAccessException {
        try {
            int platformType = -1;
            final DataObject statusDO = (DataObject)new WritableDataObject();
            final JSONArray resourceList = complianceJSON.getJSONArray("resource_list");
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            final JSONObject complianceStatusIdsJSON = this.addComplianceStatusToResource(complianceJSON);
            final JSONObject ruleJSON = RuleEngineDBUtil.getInstance().getRulesForCollection(complianceJSON);
            final JSONArray rulesJSONArray = ruleJSON.getJSONArray("rules");
            final JSONArray complianceStatusJSONArray = complianceStatusIdsJSON.getJSONArray("compliance_status_id_list");
            final JSONArray ruleActionJSONArray = new JSONArray();
            for (int j = 0; j < rulesJSONArray.length(); ++j) {
                JSONObject actionJSON = new JSONObject();
                final Long ruleId = rulesJSONArray.getLong(j);
                actionJSON.put("rule_id", (Object)ruleId);
                actionJSON = ActionEngineDBUtil.getInstance().getAction(actionJSON, 0);
                ruleActionJSONArray.put((Object)actionJSON);
            }
            for (int i = 0; i < complianceStatusJSONArray.length(); ++i) {
                final JSONObject complianceToDeviceJSON = complianceStatusJSONArray.getJSONObject(i);
                final Long complianceStatusId = JSONUtil.optLongForUVH(complianceToDeviceJSON, "compliance_status_id", Long.valueOf(-1L));
                platformType = this.getPlatformTypeForComplianceStatusId(complianceStatusId);
                complianceToDeviceJSON.put("rules", (Object)ruleActionJSONArray);
                complianceToDeviceJSON.put("platform_type_id", platformType);
                statusDO.append(this.addOrUpdateComplianceToDeviceStatus(complianceToDeviceJSON));
                for (int k = 0; k < ruleActionJSONArray.length(); ++k) {
                    final JSONObject ruleActionJSON = ruleActionJSONArray.getJSONObject(k);
                    ruleActionJSON.put("compliance_status_id", (Object)complianceStatusId);
                    MDMUtil.getPersistence().delete(new Criteria(new Column("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0));
                    ruleActionJSON.put("platform_type_id", platformType);
                    final String isoTime = this.getISO8601Time(MDMUtil.getCurrentTimeInMillis());
                    ruleActionJSON.put("rule_evaluated_time", (Object)isoTime);
                    statusDO.append(this.addOrUpdateRuleStatusForDevice(ruleActionJSON, null));
                }
            }
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("compliance_status_id_list", (Object)complianceStatusJSONArray);
            requestJSON.put("rules", (Object)ruleActionJSONArray);
            MDMUtil.getPersistence().add(statusDO);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- initiateComplianceDeviceStatus()    >   Error   ", e);
            throw e;
        }
    }
    
    public String getISO8601Time(final long currentTimeInMillis) {
        final Date date = new Date(currentTimeInMillis);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String text = sdf.format(date);
        return text;
    }
    
    private int getPlatformTypeForComplianceStatusId(final Long complianceStatusId) throws DataAccessException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join managedDeviceJoin = new Join("ComplianceToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            final Criteria complianceStatusCriteria = new Criteria(Column.getColumn("ComplianceToResource", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
            selectQuery.setCriteria(complianceStatusCriteria);
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            return (int)dataObject.getRow("ManagedDevice").get("PLATFORM_TYPE");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getPlatformTypeForComplianceStatusId()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public DataObject addOrUpdateActionAttributeToDeviceStatus(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final DataObject statusDO = (DataObject)new WritableDataObject();
            final Long actionAttributeId = JSONUtil.optLongForUVH(requestJSON, "action_attribute_id", Long.valueOf(-1L));
            final Long complianceStatusId = JSONUtil.optLongForUVH(requestJSON, "compliance_status_id", Long.valueOf(-1L));
            final Long commandHistoryId = JSONUtil.optLongForUVH(requestJSON, "command_history_id", Long.valueOf(-1L));
            final Criteria actionAttributeCriteria = new Criteria(new Column("ActionAttributesToDeviceRecentStatus", "ACTION_ATTRIBUTE_ID"), (Object)actionAttributeId, 0);
            final Criteria complianceStatusCriteria = new Criteria(new Column("ActionAttributesToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
            DataObject dataObject = MDMUtil.getPersistence().get("ActionAttributesToDeviceRecentStatus", actionAttributeCriteria.and(complianceStatusCriteria));
            if (dataObject.isEmpty()) {
                final Row actionAttributesToDeviceStatusRow = new Row("ActionAttributesToDeviceRecentStatus");
                actionAttributesToDeviceStatusRow.set("COMPLIANCE_STATUS_ID", (Object)complianceStatusId);
                actionAttributesToDeviceStatusRow.set("ACTION_ATTRIBUTE_ID", (Object)actionAttributeId);
                actionAttributesToDeviceStatusRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                dataObject = (DataObject)new WritableDataObject();
                dataObject.addRow(actionAttributesToDeviceStatusRow);
            }
            else {
                final Row actionAttributesToDeviceStatusRow = dataObject.getRow("ActionAttributesToDeviceRecentStatus");
                actionAttributesToDeviceStatusRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                dataObject.updateRow(actionAttributesToDeviceStatusRow);
            }
            return dataObject;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateActionAttributeToDeviceStatus()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public DataObject addOrUpdateRuleStatusForDevice(final JSONObject requestJSON, final JSONObject existingStatusJSON) throws JSONException, DataAccessException {
        try {
            int platformType = -1;
            DataObject statusDO = (DataObject)new WritableDataObject();
            final Long ruleId = JSONUtil.optLongForUVH(requestJSON, "rule_id", Long.valueOf(-1L));
            final Long complianceStatusId = JSONUtil.optLongForUVH(requestJSON, "compliance_status_id", Long.valueOf(-1L));
            if (requestJSON.has("platform_type_id")) {
                platformType = requestJSON.getInt("platform_type_id");
            }
            else {
                platformType = this.getPlatformTypeForComplianceStatusId(complianceStatusId);
            }
            final String remarks = requestJSON.optString("remarks", "mdm.compliance.rule_notification_sent");
            final int ruleState = requestJSON.optInt("rule_state", 806);
            final String ruleEvaluatedTime = requestJSON.optString("rule_evaluated_time", "--");
            final String remarkArgs = requestJSON.optString("remarks_args", "--");
            final Row ruleStatusHistoryRow = new Row("RuleStatusHistory");
            ruleStatusHistoryRow.set("RULE_ID", (Object)ruleId);
            ruleStatusHistoryRow.set("RULE_EVALUATED_TIME", (Object)ruleEvaluatedTime);
            if (platformType != 2 && platformType != 1) {
                ruleStatusHistoryRow.set("REMARKS", (Object)"mdm.compliance.not_applicable");
                ruleStatusHistoryRow.set("RULE_STATUS", (Object)805);
                ruleStatusHistoryRow.set("REMARKS_ARGS", (Object)"mdm.compliance.platform_not_applicable");
            }
            else {
                ruleStatusHistoryRow.set("REMARKS", (Object)remarks);
                ruleStatusHistoryRow.set("RULE_STATUS", (Object)ruleState);
                ruleStatusHistoryRow.set("REMARKS_ARGS", (Object)remarkArgs);
            }
            statusDO.addRow(ruleStatusHistoryRow);
            MDMUtil.getPersistence().add(statusDO);
            final Long ruleStatusHistoryIdUVH = (Long)statusDO.getRow("RuleStatusHistory").get("RULE_STATUS_HISTORY_ID");
            if (ruleState == 806) {
                statusDO = (DataObject)new WritableDataObject();
                final Row ruleToDeviceStatusRow = new Row("RuleToDeviceRecentStatus");
                ruleToDeviceStatusRow.set("COMPLIANCE_STATUS_ID", (Object)complianceStatusId);
                ruleToDeviceStatusRow.set("RULE_STATUS_HISTORY_ID", (Object)ruleStatusHistoryIdUVH);
                statusDO.addRow(ruleToDeviceStatusRow);
            }
            else {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RuleStatusHistory"));
                final Join ruleToDeviceJoin = new Join("RuleStatusHistory", "RuleToDeviceRecentStatus", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
                final Criteria criteria = new Criteria(new Column("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
                final Criteria ruleHistoryCriteria = new Criteria(new Column("RuleStatusHistory", "RULE_ID"), (Object)ruleId, 0);
                selectQuery.addSelectColumn(new Column("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"));
                selectQuery.addSelectColumn(new Column("RuleToDeviceRecentStatus", "RULE_STATUS_HISTORY_ID"));
                selectQuery.addSelectColumn(new Column("RuleStatusHistory", "RULE_STATUS_HISTORY_ID"));
                selectQuery.addSelectColumn(new Column("RuleStatusHistory", "RULE_ID"));
                selectQuery.addJoin(ruleToDeviceJoin);
                selectQuery.setCriteria(criteria.and(ruleHistoryCriteria));
                statusDO = MDMUtil.getPersistence().get(selectQuery);
                if (!statusDO.isEmpty()) {
                    final Row ruleToDeviceStatusRow2 = statusDO.getRow("RuleToDeviceRecentStatus");
                    ruleToDeviceStatusRow2.set("RULE_STATUS_HISTORY_ID", (Object)ruleStatusHistoryIdUVH);
                    statusDO.updateRow(ruleToDeviceStatusRow2);
                }
            }
            if (existingStatusJSON != null) {
                final UpdateQuery complianceStatusUpdateQuery = (UpdateQuery)new UpdateQueryImpl("ComplianceToDeviceStatus");
                complianceStatusUpdateQuery.setCriteria(new Criteria(Column.getColumn("ComplianceToDeviceStatus", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0));
                int notApplicableCount = existingStatusJSON.getInt("not_applicable_count");
                int compliantRuleCount = existingStatusJSON.getInt("compliant_rule_count");
                int notificationSentCount = existingStatusJSON.getInt("notification_sent_count");
                int failedCount = existingStatusJSON.getInt("failed_to_evaluate_count");
                int nonComplaintCount = existingStatusJSON.getInt("non_compliant_rule_count");
                int yetToEvalCount = existingStatusJSON.getInt("yet_to_evaluate_count");
                final int totalCount = existingStatusJSON.getInt("total_rule_count");
                final int complianceState = existingStatusJSON.getInt("compliance_state");
                final int existingRuleState = existingStatusJSON.getInt("rule_state");
                complianceStatusUpdateQuery.setUpdateColumn("COMPLIANCE_STATUS_ID", (Object)complianceStatusId);
                complianceStatusUpdateQuery.setUpdateColumn("LAST_EVALUATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
                if (complianceState == 905) {
                    compliantRuleCount = (notApplicableCount = (notificationSentCount = (failedCount = (nonComplaintCount = (yetToEvalCount = 0)))));
                }
                else {
                    switch (existingRuleState) {
                        case 805: {
                            --notApplicableCount;
                            break;
                        }
                        case 803: {
                            --compliantRuleCount;
                            break;
                        }
                        case 806: {
                            --notificationSentCount;
                            break;
                        }
                        case 804: {
                            --failedCount;
                            break;
                        }
                        case 802: {
                            --nonComplaintCount;
                            break;
                        }
                        case 801: {
                            --yetToEvalCount;
                            break;
                        }
                    }
                }
                switch (ruleState) {
                    case 805: {
                        ++notApplicableCount;
                        break;
                    }
                    case 803: {
                        ++compliantRuleCount;
                        break;
                    }
                    case 806: {
                        ++notificationSentCount;
                        break;
                    }
                    case 804: {
                        ++failedCount;
                        break;
                    }
                    case 802: {
                        ++nonComplaintCount;
                        break;
                    }
                    case 801: {
                        ++yetToEvalCount;
                        break;
                    }
                }
                complianceStatusUpdateQuery.setUpdateColumn("NON_COMPLIANT_RULE_COUNT", (Object)nonComplaintCount);
                complianceStatusUpdateQuery.setUpdateColumn("COMPLIANT_RULE_COUNT", (Object)compliantRuleCount);
                complianceStatusUpdateQuery.setUpdateColumn("NOTIFICATION_SENT_COUNT", (Object)notificationSentCount);
                complianceStatusUpdateQuery.setUpdateColumn("YET_TO_EVALUATE_COUNT", (Object)yetToEvalCount);
                complianceStatusUpdateQuery.setUpdateColumn("NOT_APPLICABLE_COUNT", (Object)notApplicableCount);
                complianceStatusUpdateQuery.setUpdateColumn("FAILED_TO_EVALUATE_COUNT", (Object)failedCount);
                complianceStatusUpdateQuery.setUpdateColumn("TOTAL_COUNT", (Object)totalCount);
                if (notApplicableCount + compliantRuleCount == totalCount) {
                    complianceStatusUpdateQuery.setUpdateColumn("COMPLIANCE_STATE", (Object)902);
                }
                else if (notificationSentCount == totalCount) {
                    complianceStatusUpdateQuery.setUpdateColumn("COMPLIANCE_STATE", (Object)905);
                }
                else if (yetToEvalCount + notificationSentCount + compliantRuleCount + notApplicableCount == totalCount) {
                    complianceStatusUpdateQuery.setUpdateColumn("COMPLIANCE_STATE", (Object)901);
                }
                else {
                    complianceStatusUpdateQuery.setUpdateColumn("COMPLIANCE_STATE", (Object)903);
                }
                MDMUtil.getPersistence().update(complianceStatusUpdateQuery);
            }
            return statusDO;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateRuleStatusForDevice()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    protected DataObject addOrUpdateComplianceToDeviceStatus(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final DataObject statusDO = (DataObject)new WritableDataObject();
            final Long complianceStatusId = JSONUtil.optLongForUVH(requestJSON, "compliance_status_id", Long.valueOf(-1L));
            int complianceState = requestJSON.optInt("compliance_state", 905);
            final JSONArray ruleActionJSONArray = requestJSON.optJSONArray("rules");
            final int compliantRuleCount = requestJSON.optInt("compliant_rule_count", -1);
            final int notificationSentCount = requestJSON.optInt("notification_sent_count", -1);
            final int nonCompliantRuleCount = requestJSON.optInt("non_compliant_rule_count", -1);
            final int yetToEvaluateCount = requestJSON.optInt("yet_to_evaluate_count", -1);
            final int notApplicableCount = requestJSON.optInt("not_applicable_count", -1);
            final int failedToEvaluateCount = requestJSON.optInt("failed_to_evaluate_count", -1);
            final int platformType = requestJSON.optInt("platform_type_id", -1);
            final Row complianceToDeviceStatusRow = new Row("ComplianceToDeviceStatus");
            complianceToDeviceStatusRow.set("COMPLIANCE_STATUS_ID", (Object)complianceStatusId);
            complianceToDeviceStatusRow.set("COMPLIANCE_STATE", (Object)complianceState);
            complianceToDeviceStatusRow.set("LAST_EVALUATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            complianceToDeviceStatusRow.set("TOTAL_COUNT", (Object)ruleActionJSONArray.length());
            complianceToDeviceStatusRow.set("COMPLIANT_RULE_COUNT", (Object)compliantRuleCount);
            complianceToDeviceStatusRow.set("NOTIFICATION_SENT_COUNT", (Object)notificationSentCount);
            complianceToDeviceStatusRow.set("NON_COMPLIANT_RULE_COUNT", (Object)nonCompliantRuleCount);
            complianceToDeviceStatusRow.set("YET_TO_EVALUATE_COUNT", (Object)yetToEvaluateCount);
            complianceToDeviceStatusRow.set("NOT_APPLICABLE_COUNT", (Object)notApplicableCount);
            complianceToDeviceStatusRow.set("FAILED_TO_EVALUATE_COUNT", (Object)failedToEvaluateCount);
            if (complianceState == 905) {
                if (platformType != 2 && platformType != 1) {
                    complianceState = 904;
                    complianceToDeviceStatusRow.set("COMPLIANCE_STATE", (Object)complianceState);
                    complianceToDeviceStatusRow.set("NOT_APPLICABLE_COUNT", (Object)ruleActionJSONArray.length());
                }
                else {
                    complianceToDeviceStatusRow.set("NOTIFICATION_SENT_COUNT", (Object)ruleActionJSONArray.length());
                }
                statusDO.addRow(complianceToDeviceStatusRow);
            }
            return statusDO;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateComplianceToDeviceStatus()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    private JSONObject addComplianceStatusToResource(final JSONObject complianceJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray resourceList = complianceJSON.getJSONArray("resource_list");
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            final JSONArray complianceStatusIdJSONArray = new JSONArray();
            final DataObject dataObject = (DataObject)new WritableDataObject();
            final List resourceRemoveList = new ArrayList();
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = resourceList.getLong(i);
                resourceRemoveList.add(resourceId);
            }
            final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceRemoveList.toArray(), 8);
            MDMUtil.getPersistence().delete(collectionCriteria.and(resourceCriteria));
            for (int j = 0; j < resourceList.length(); ++j) {
                final Long resourceId2 = resourceList.getLong(j);
                final Row complianceToDeviceRow = new Row("ComplianceToResource");
                complianceToDeviceRow.set("COLLECTION_ID", (Object)collectionId);
                complianceToDeviceRow.set("RESOURCE_ID", (Object)resourceId2);
                dataObject.addRow(complianceToDeviceRow);
            }
            MDMUtil.getPersistence().add(dataObject);
            final Iterator iterator = dataObject.getRows("ComplianceToResource");
            while (iterator.hasNext()) {
                final Row complianceToDeviceRow2 = iterator.next();
                final Long complianceStatusId = (Long)complianceToDeviceRow2.get("COMPLIANCE_STATUS_ID");
                final Long resourceId3 = (Long)complianceToDeviceRow2.get("RESOURCE_ID");
                final JSONObject complianceStatusJSON = new JSONObject();
                complianceStatusJSON.put("compliance_status_id", (Object)complianceStatusId);
                complianceStatusJSON.put("collection_id", (Object)collectionId);
                complianceStatusJSON.put("resource_id", (Object)resourceId3);
                complianceStatusIdJSONArray.put((Object)complianceStatusJSON);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("compliance_status_id_list", (Object)complianceStatusIdJSONArray);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceStatusToResource()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceStatusID(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("ComplianceToResource", collectionCriteria.and(resourceCriteria));
            Long complianceStatusId = -1L;
            if (!dataObject.isEmpty()) {
                complianceStatusId = (Long)dataObject.getRow("ComplianceToResource").get("COMPLIANCE_STATUS_ID");
            }
            requestJSON.put("compliance_status_id", (Object)complianceStatusId);
            return requestJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceStatusID()    >   Error   ", e);
            throw e;
        }
    }
    
    public void complianceEventLogEntry(final JSONObject requestJSON) throws JSONException {
        try {
            final int eventId = requestJSON.getInt("event_id");
            Long resourceId = null;
            if (requestJSON.has("resource_id")) {
                resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            }
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final String remarks = requestJSON.optString("remarks", "--");
            final String remarksArgs = requestJSON.optString("remarks_args", "--");
            final String userName = String.valueOf(requestJSON.get("user_name"));
            MDMEventLogHandler.getInstance().MDMEventLogEntry(eventId, resourceId, userName, remarks, remarksArgs, customerId);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- complianceEventLogEntry() >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject getComplianceSummary(final JSONObject requestJSON) throws Exception {
        int yetToEvaluateDevices = 0;
        int compliantDevices = 0;
        int nonCompliantDevices = 0;
        int notApplicableDevices = 0;
        int notificationSentDevices = 0;
        final Join resourceJoin = new Join("ComplianceToResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join managedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
        final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        Column selectColumn2 = new Column("ComplianceToResource", "RESOURCE_ID");
        try {
            final SelectQuery complianceDeviceStatusQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join complianceToDevice = new Join("ComplianceToResource", "ComplianceToDeviceStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            Column selectColumn3 = new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATUS_ID");
            selectColumn3 = selectColumn3.distinct();
            selectColumn3 = selectColumn3.count();
            selectColumn3.setColumnAlias("TOTAL_COUNT");
            final Column statusColumn = new Column("ComplianceToDeviceStatus", "COMPLIANCE_STATE");
            selectColumn2 = selectColumn2.distinct();
            selectColumn2 = selectColumn2.count();
            final List columnList = new ArrayList();
            columnList.add(statusColumn);
            final GroupByClause groupByClause = new GroupByClause(columnList);
            complianceDeviceStatusQuery.addJoin(complianceToDevice);
            complianceDeviceStatusQuery.addJoin(resourceJoin);
            complianceDeviceStatusQuery.addJoin(managedDeviceJoin);
            complianceDeviceStatusQuery.setCriteria(collectionCriteria.and(managedDeviceCriteria));
            complianceDeviceStatusQuery.addSelectColumn(selectColumn3);
            complianceDeviceStatusQuery.addSelectColumn(statusColumn);
            complianceDeviceStatusQuery.setGroupByClause(groupByClause);
            final DMDataSetWrapper deviceStatusJSONArray = DMDataSetWrapper.executeQuery((Object)complianceDeviceStatusQuery);
            while (deviceStatusJSONArray.next()) {
                final int status = (int)deviceStatusJSONArray.getValue("COMPLIANCE_STATE");
                if (status == 901) {
                    yetToEvaluateDevices = (int)deviceStatusJSONArray.getValue("TOTAL_COUNT");
                }
                if (status == 902) {
                    compliantDevices = (int)deviceStatusJSONArray.getValue("TOTAL_COUNT");
                }
                if (status == 903) {
                    nonCompliantDevices = (int)deviceStatusJSONArray.getValue("TOTAL_COUNT");
                }
                if (status == 904) {
                    notApplicableDevices = (int)deviceStatusJSONArray.getValue("TOTAL_COUNT");
                }
                if (status == 905) {
                    notificationSentDevices = (int)deviceStatusJSONArray.getValue("TOTAL_COUNT");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getComplianceSummary() >   Error ", e);
            throw e;
        }
        try {
            final SelectQuery totalCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            totalCountQuery.addJoin(resourceJoin);
            totalCountQuery.addJoin(managedDeviceJoin);
            totalCountQuery.setCriteria(collectionCriteria.and(managedDeviceCriteria));
            selectColumn2.setColumnAlias("TOTAL_COUNT");
            totalCountQuery.addSelectColumn(selectColumn2);
            final Column collectionColumn = new Column("ComplianceToResource", "COLLECTION_ID");
            final List columnList2 = new ArrayList();
            columnList2.add(collectionColumn);
            final GroupByClause groupByClause2 = new GroupByClause(columnList2);
            totalCountQuery.addSelectColumn(collectionColumn);
            totalCountQuery.setGroupByClause(groupByClause2);
            final int totalCount = DBUtil.getRecordCount(totalCountQuery);
            requestJSON.put("compliant_devices_count", compliantDevices);
            requestJSON.put("non_compliant_devices_count", nonCompliantDevices);
            requestJSON.put("yet_to_evaluate_count", yetToEvaluateDevices);
            requestJSON.put("not_applicable_count", notApplicableDevices);
            requestJSON.put("notification_sent_count", notificationSentDevices);
            requestJSON.put("total_count", totalCount);
            return requestJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getComplianceSummary() >   Error ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceRulesSummary(final JSONObject requestJSON) throws Exception {
        Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
        final DataObject dataObject = MDMUtil.getPersistence().get("CollectionToRules", new Criteria(new Column("CollectionToRules", "COLLECTION_ID"), (Object)collectionId, 0));
        final Iterator iterator = dataObject.getRows("CollectionToRules");
        final JSONArray ruleSummaryJSONArray = new JSONArray();
        while (iterator.hasNext()) {
            int compliantDeviceCount = 0;
            int nonCompliantDeviceCount = 0;
            int yetToEvaluateDeviceCount = 0;
            int notApplicableDeviceCount = 0;
            int cantBeEvalDeviceCount = 0;
            int notificationSentCount = 0;
            Long ruleId = -1L;
            final Join resourceJoin = new Join("ComplianceToResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join managedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Row collectionToRulesRow = iterator.next();
            ruleId = (Long)collectionToRulesRow.get("RULE_ID");
            final Criteria ruleIdCriteria = new Criteria(new Column("RuleStatusHistory", "RULE_ID"), (Object)ruleId, 0);
            SelectQuery summaryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join ruleToDeviceRecentStatusJoin = new Join("ComplianceToResource", "RuleToDeviceRecentStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleStatusHistoryJoin = new Join("RuleToDeviceRecentStatus", "RuleStatusHistory", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
            Column countColumn = new Column("ComplianceToResource", "RESOURCE_ID");
            countColumn = countColumn.distinct();
            countColumn = countColumn.count();
            countColumn.setColumnAlias("TOTAL_COUNT");
            final Column groupByColumn = new Column("RuleStatusHistory", "RULE_STATUS");
            List columnList = new ArrayList();
            columnList.add(groupByColumn);
            GroupByClause groupByClause = new GroupByClause(columnList);
            summaryQuery.addSelectColumn(countColumn);
            summaryQuery.addSelectColumn(groupByColumn);
            summaryQuery.addJoin(ruleToDeviceRecentStatusJoin);
            summaryQuery.addJoin(ruleStatusHistoryJoin);
            summaryQuery.addJoin(resourceJoin);
            summaryQuery.addJoin(managedDeviceJoin);
            summaryQuery.setGroupByClause(groupByClause);
            summaryQuery.setCriteria(collectionCriteria.and(ruleIdCriteria).and(managedDeviceCriteria));
            summaryQuery = RBDAUtil.getInstance().getRBDAQuery(summaryQuery);
            final DMDataSetWrapper resultSet = DMDataSetWrapper.executeQuery((Object)summaryQuery);
            if (resultSet != null) {
                while (resultSet.next()) {
                    final int status = (int)resultSet.getValue("RULE_STATUS");
                    switch (status) {
                        case 801: {
                            yetToEvaluateDeviceCount = (int)resultSet.getValue("TOTAL_COUNT");
                            continue;
                        }
                        case 802: {
                            nonCompliantDeviceCount = (int)resultSet.getValue("TOTAL_COUNT");
                            continue;
                        }
                        case 803: {
                            compliantDeviceCount = (int)resultSet.getValue("TOTAL_COUNT");
                            continue;
                        }
                        case 805: {
                            notApplicableDeviceCount = (int)resultSet.getValue("TOTAL_COUNT");
                            continue;
                        }
                        case 804: {
                            cantBeEvalDeviceCount = (int)resultSet.getValue("TOTAL_COUNT");
                            continue;
                        }
                        case 806: {
                            notificationSentCount = (int)resultSet.getValue("TOTAL_COUNT");
                            continue;
                        }
                        default: {
                            throw new UnsupportedOperationException("Invalid status   ");
                        }
                    }
                }
            }
            try {
                final SelectQuery totalCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
                totalCountQuery.addJoin(resourceJoin);
                totalCountQuery.addJoin(managedDeviceJoin);
                totalCountQuery.setCriteria(collectionCriteria.and(managedDeviceCriteria));
                Column selectColumn2 = new Column("ComplianceToResource", "COMPLIANCE_STATUS_ID");
                selectColumn2 = selectColumn2.distinct();
                selectColumn2 = selectColumn2.count();
                selectColumn2.setColumnAlias("TOTAL_COUNT");
                totalCountQuery.addSelectColumn(selectColumn2);
                final Column collectionColumn = new Column("ComplianceToResource", "COLLECTION_ID");
                columnList = new ArrayList();
                columnList.add(collectionColumn);
                groupByClause = new GroupByClause(columnList);
                totalCountQuery.addSelectColumn(collectionColumn);
                totalCountQuery.setGroupByClause(groupByClause);
                final DMDataSetWrapper totalResultSet = DMDataSetWrapper.executeQuery((Object)totalCountQuery);
                int totalCount = 0;
                if (totalResultSet != null) {
                    while (totalResultSet.next()) {
                        collectionId = (Long)totalResultSet.getValue("COLLECTION_ID");
                        totalCount = (int)totalResultSet.getValue("TOTAL_COUNT");
                    }
                }
                JSONObject ruleJSON = new JSONObject();
                ruleJSON.put("rule_id", (Object)ruleId);
                ruleJSON = RuleEngineDBUtil.getInstance().getRuleDetails(ruleJSON);
                ruleJSON.put("compliant_devices_count", compliantDeviceCount);
                ruleJSON.put("non_compliant_devices_count", nonCompliantDeviceCount);
                ruleJSON.put("yet_to_evaluate_count", yetToEvaluateDeviceCount);
                ruleJSON.put("not_applicable_count", notApplicableDeviceCount);
                ruleJSON.put("failed_to_evaluate_count", cantBeEvalDeviceCount);
                ruleJSON.put("notification_sent_count", notificationSentCount);
                ruleJSON.put("total_count", totalCount);
                ruleJSON.put("collection_id", (Object)collectionId);
                ruleJSON = this.getRuleEvaluationTimeDetails(ruleJSON);
                ruleJSON.remove("collection_id");
                float complianceScore = 0.0f;
                if (totalCount != 0) {
                    complianceScore = (compliantDeviceCount + notApplicableDeviceCount) / (float)totalCount * 100.0f;
                }
                ruleJSON.put("compliance_score", (Object)String.valueOf(complianceScore));
                ruleSummaryJSONArray.put((Object)ruleJSON);
            }
            catch (final JSONException | DataAccessException | ParseException e) {
                this.logger.log(Level.SEVERE, " -- getComplianceRulesSummary() >   Error ", e);
                throw e;
            }
        }
        final JSONObject summaryJSON = new JSONObject();
        summaryJSON.put("rules", (Object)ruleSummaryJSONArray);
        return summaryJSON;
    }
    
    private JSONObject getRuleEvaluationTimeDetails(final JSONObject ruleJSON) throws JSONException, DataAccessException, ParseException {
        try {
            final Long ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            final Long collectionId = JSONUtil.optLongForUVH(ruleJSON, "collection_id", Long.valueOf(-1L));
            final SelectQuery timeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join ruleToDeviceRecentStatusJoin = new Join("ComplianceToResource", "RuleToDeviceRecentStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleStatusHistoryJoin = new Join("RuleToDeviceRecentStatus", "RuleStatusHistory", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
            final Criteria ruleIdCriteria = new Criteria(new Column("RuleStatusHistory", "RULE_ID"), (Object)ruleId, 0);
            final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            timeQuery.addSelectColumn(new Column("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
            timeQuery.addSelectColumn(new Column("ComplianceToResource", "COLLECTION_ID"));
            timeQuery.addSelectColumn(new Column("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"));
            timeQuery.addSelectColumn(new Column("RuleToDeviceRecentStatus", "RULE_STATUS_HISTORY_ID"));
            timeQuery.addSelectColumn(new Column("RuleStatusHistory", "RULE_STATUS_HISTORY_ID"));
            timeQuery.addSelectColumn(new Column("RuleStatusHistory", "RULE_ID"));
            timeQuery.addSelectColumn(new Column("RuleStatusHistory", "RULE_EVALUATED_TIME"));
            timeQuery.addJoin(ruleToDeviceRecentStatusJoin);
            timeQuery.addJoin(ruleStatusHistoryJoin);
            timeQuery.setCriteria(ruleIdCriteria.and(collectionCriteria));
            final WritableDataObject dataObject = (WritableDataObject)MDMUtil.getPersistence().get(timeQuery);
            dataObject.sortRows("RuleStatusHistory", new SortColumn[] { new SortColumn("RuleStatusHistory", "RULE_EVALUATED_TIME", false) });
            String time = "--";
            if (!dataObject.isEmpty()) {
                time = (String)dataObject.getFirstRow("RuleStatusHistory").get("RULE_EVALUATED_TIME");
            }
            Long timeInMilli = -1L;
            if (!time.equals("--")) {
                final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
                final Date timeCreatedDate = dateFormat.parse(time);
                timeInMilli = timeCreatedDate.getTime();
            }
            ruleJSON.put("rule_evaluated_time", (Object)timeInMilli);
            ruleJSON.put("rule_next_evaluation_time", -1L);
            return ruleJSON;
        }
        catch (final DataAccessException | JSONException | ParseException e) {
            this.logger.log(Level.SEVERE, " -- getRuleEvaluationTimeDetails() >   Error   ", e);
            throw e;
        }
    }
    
    public void updateComplianceStateOnprofileAssociation(final JSONObject requestJSON) throws Exception {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final Long resourceId = JSONUtil.optLongForUVH(requestJSON, "resource_id", Long.valueOf(-1L));
            final String strStatus = requestJSON.optString("status", "Error");
            final JSONObject statusJSON = this.getComplianceStatusID(requestJSON);
            final Long complianceStatusId = JSONUtil.optLongForUVH(statusJSON, "compliance_status_id", Long.valueOf(-1L));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join ruleToDeviceStatus = new Join("ComplianceToResource", "RuleToDeviceRecentStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleHistoryJoin = new Join("RuleToDeviceRecentStatus", "RuleStatusHistory", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
            final Join complianceStatus = new Join("ComplianceToResource", "ComplianceToDeviceStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            selectQuery.addSelectColumn(new Column("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(new Column("ComplianceToResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("ComplianceToResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(new Column("RuleToDeviceRecentStatus", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(new Column("RuleStatusHistory", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(new Column("ComplianceToDeviceStatus", "*"));
            selectQuery.addJoin(complianceStatus);
            selectQuery.addJoin(ruleToDeviceStatus);
            selectQuery.addJoin(ruleHistoryJoin);
            final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(collectionCriteria.and(resourceCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final List ruleIdList = this.getRuleListForCollection(collectionId);
            final Iterator ruleStatusIterator = dataObject.getRows("RuleToDeviceRecentStatus");
            final Row complianceStatusRow = dataObject.getRow("ComplianceToDeviceStatus");
            if (strStatus.equalsIgnoreCase("Acknowledged")) {
                while (ruleStatusIterator.hasNext() && ruleIdList.iterator().hasNext()) {
                    final Row ruleStatusRow = ruleStatusIterator.next();
                    final Row ruleHistoryRow = new Row("RuleStatusHistory");
                    ruleHistoryRow.set("RULE_ID", ruleIdList.iterator().next());
                    ruleHistoryRow.set("RULE_STATUS", (Object)801);
                    ruleHistoryRow.set("RULE_EVALUATED_TIME", (Object)this.getISO8601Time(MDMUtil.getCurrentTimeInMillis()));
                    ruleHistoryRow.set("REMARKS", (Object)"mdm.compliance.associated_device");
                    final DataObject historyDO = (DataObject)new WritableDataObject();
                    historyDO.addRow(ruleHistoryRow);
                    MDMUtil.getPersistence().add(historyDO);
                    final Long ruleHistoryId = (Long)historyDO.getRow("RuleStatusHistory").get("RULE_STATUS_HISTORY_ID");
                    ruleStatusRow.set("RULE_STATUS_HISTORY_ID", (Object)ruleHistoryId);
                    dataObject.updateRow(ruleStatusRow);
                }
                final int totalCount = (int)complianceStatusRow.get("TOTAL_COUNT");
                complianceStatusRow.set("LAST_EVALUATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
                complianceStatusRow.set("COMPLIANCE_STATE", (Object)901);
                complianceStatusRow.set("FAILED_TO_EVALUATE_COUNT", (Object)0);
                complianceStatusRow.set("YET_TO_EVALUATE_COUNT", (Object)totalCount);
                complianceStatusRow.set("COMPLIANT_RULE_COUNT", (Object)0);
                complianceStatusRow.set("NOTIFICATION_SENT_COUNT", (Object)0);
                complianceStatusRow.set("NON_COMPLIANT_RULE_COUNT", (Object)0);
                complianceStatusRow.set("NOT_APPLICABLE_COUNT", (Object)0);
                dataObject.updateRow(complianceStatusRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            else if (strStatus.equalsIgnoreCase("Error")) {
                final Integer errorCode = requestJSON.optInt("ErrorCode", 0);
                final String errorKey = this.getComplianceErrorRemarks(errorCode);
                final String linkKey = this.getComplianceLearnMoreLink(errorCode);
                while (ruleStatusIterator.hasNext() && ruleIdList.iterator().hasNext()) {
                    final Row ruleStatusRow2 = ruleStatusIterator.next();
                    final Row ruleHistoryRow2 = new Row("RuleStatusHistory");
                    ruleHistoryRow2.set("RULE_ID", ruleIdList.iterator().next());
                    ruleHistoryRow2.set("RULE_STATUS", (Object)804);
                    ruleHistoryRow2.set("RULE_EVALUATED_TIME", (Object)this.getISO8601Time(MDMUtil.getCurrentTimeInMillis()));
                    final String remarks = I18N.getMsg(errorKey, new Object[0]);
                    String link = I18N.getMsg(linkKey, new Object[0]);
                    link = MDMUtil.replaceProductUrlLoaderValuesinText(link, null);
                    final String trackCode = ProductUrlLoader.getInstance().getValue("trackingcode");
                    link = link + "?" + trackCode;
                    ruleHistoryRow2.set("REMARKS", (Object)(remarks + " " + link));
                    ruleHistoryRow2.set("REMARKS_ARGS", (Object)"mdm.compliance.cannot_be_evaluated");
                    final DataObject historyDO2 = (DataObject)new WritableDataObject();
                    historyDO2.addRow(ruleHistoryRow2);
                    MDMUtil.getPersistence().add(historyDO2);
                    final Long ruleHistoryId2 = (Long)historyDO2.getRow("RuleStatusHistory").get("RULE_STATUS_HISTORY_ID");
                    ruleStatusRow2.set("RULE_STATUS_HISTORY_ID", (Object)ruleHistoryId2);
                    dataObject.updateRow(ruleStatusRow2);
                }
                final int totalCount2 = (int)complianceStatusRow.get("TOTAL_COUNT");
                complianceStatusRow.set("LAST_EVALUATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
                complianceStatusRow.set("COMPLIANCE_STATE", (Object)903);
                complianceStatusRow.set("FAILED_TO_EVALUATE_COUNT", (Object)totalCount2);
                complianceStatusRow.set("YET_TO_EVALUATE_COUNT", (Object)0);
                complianceStatusRow.set("COMPLIANT_RULE_COUNT", (Object)0);
                complianceStatusRow.set("NOTIFICATION_SENT_COUNT", (Object)0);
                complianceStatusRow.set("NON_COMPLIANT_RULE_COUNT", (Object)0);
                complianceStatusRow.set("NOT_APPLICABLE_COUNT", (Object)0);
                dataObject.updateRow(complianceStatusRow);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- updateComplianceStateOnprofileAssociation() >   Error   ", e);
            throw e;
        }
    }
    
    private String getComplianceErrorRemarks(final Integer errorCode) {
        String remarks = "";
        switch (errorCode) {
            case 12100: {
                remarks = "mdm.compliance.older_agent";
                break;
            }
        }
        return remarks;
    }
    
    private String getComplianceLearnMoreLink(final Integer errorCode) {
        String link = "";
        switch (errorCode) {
            case 12100: {
                link = "mdm.compliance.older_agent_link";
                break;
            }
        }
        return link;
    }
    
    private List getRuleListForCollection(final Long collectionId) throws DataAccessException {
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get("CollectionToRules", new Criteria(new Column("CollectionToRules", "COLLECTION_ID"), (Object)collectionId, 0));
            final Iterator iterator = dataObject.getRows("CollectionToRules");
            final List ruleList = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long ruleId = (Long)row.get("RULE_ID");
                ruleList.add(ruleId);
            }
            return ruleList;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getRuleListForCollection() >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject getComplianceCustomerJSON() throws DataAccessException, JSONException {
        try {
            final JSONObject returnJSON = new JSONObject();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join customerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join collectionJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(customerJoin);
            selectQuery.addJoin(collectionJoin);
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCustomerRel", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)5, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("ProfileToCustomerRel");
            final JSONArray complianceProfilesJSONArray = new JSONArray();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final JSONObject tempJSON = new JSONObject();
                final Long profileId = (Long)row.get("PROFILE_ID");
                final Long customerId = (Long)row.get("CUSTOMER_ID");
                final Long collectionId = (Long)dataObject.getRow("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0)).get("COLLECTION_ID");
                tempJSON.put("compliance_id", (Object)profileId);
                tempJSON.put("customer_id", (Object)customerId);
                tempJSON.put("collection_id", (Object)collectionId);
                complianceProfilesJSONArray.put((Object)tempJSON);
            }
            returnJSON.put("compliance_list", (Object)complianceProfilesJSONArray);
            return returnJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceCustomerJSON() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getCurrentRuleComplianceStatus(final JSONObject additionalDataJSON) throws DataAccessException, JSONException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Long complianceStatusId = JSONUtil.optLongForUVH(additionalDataJSON, "compliance_status_id", Long.valueOf(-1L));
            final Long ruleId = JSONUtil.optLongForUVH(additionalDataJSON, "rule_id", Long.valueOf(-1L));
            final Long resourceId = JSONUtil.optLongForUVH(additionalDataJSON, "resource_id", Long.valueOf(-1L));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ComplianceToResource"));
            final Join complianceStatusJoin = new Join("ComplianceToResource", "ComplianceToDeviceStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleToDeviceRecentJoin = new Join("ComplianceToResource", "RuleToDeviceRecentStatus", new String[] { "COMPLIANCE_STATUS_ID" }, new String[] { "COMPLIANCE_STATUS_ID" }, 2);
            final Join ruleHistoryJoin = new Join("RuleToDeviceRecentStatus", "RuleStatusHistory", new String[] { "RULE_STATUS_HISTORY_ID" }, new String[] { "RULE_STATUS_HISTORY_ID" }, 2);
            selectQuery.addJoin(complianceStatusJoin);
            selectQuery.addJoin(ruleToDeviceRecentJoin);
            selectQuery.addJoin(ruleHistoryJoin);
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToResource", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "NOT_APPLICABLE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "NOTIFICATION_SENT_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "COMPLIANT_RULE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "FAILED_TO_EVALUATE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "NON_COMPLIANT_RULE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "YET_TO_EVALUATE_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "TOTAL_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("ComplianceToDeviceStatus", "COMPLIANCE_STATE"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToDeviceRecentStatus", "COMPLIANCE_STATUS_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToDeviceRecentStatus", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_STATUS_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("RuleStatusHistory", "RULE_STATUS"));
            final Criteria complianceStatusIdCriteria = new Criteria(Column.getColumn("ComplianceToResource", "COMPLIANCE_STATUS_ID"), (Object)complianceStatusId, 0);
            final Criteria ruleIdCriteria = new Criteria(Column.getColumn("RuleStatusHistory", "RULE_ID"), (Object)ruleId, 0);
            selectQuery.setCriteria(complianceStatusIdCriteria.and(ruleIdCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row complianceStatusRow = dataObject.getRow("ComplianceToDeviceStatus");
                final Row ruleHistoryRow = dataObject.getRow("RuleStatusHistory");
                final int naCount = (int)complianceStatusRow.get("NOT_APPLICABLE_COUNT");
                final int compliantRuleCount = (int)complianceStatusRow.get("COMPLIANT_RULE_COUNT");
                final int notifCount = (int)complianceStatusRow.get("NOTIFICATION_SENT_COUNT");
                final int failedCount = (int)complianceStatusRow.get("FAILED_TO_EVALUATE_COUNT");
                final int nonComplaintCount = (int)complianceStatusRow.get("NON_COMPLIANT_RULE_COUNT");
                final int yetToEval = (int)complianceStatusRow.get("YET_TO_EVALUATE_COUNT");
                final int totalCount = (int)complianceStatusRow.get("TOTAL_COUNT");
                final int complianceState = (int)complianceStatusRow.get("COMPLIANCE_STATE");
                final int ruleState = (int)ruleHistoryRow.get("RULE_STATUS");
                responseJSON.put("not_applicable_count", naCount);
                responseJSON.put("compliant_rule_count", compliantRuleCount);
                responseJSON.put("notification_sent_count", notifCount);
                responseJSON.put("failed_to_evaluate_count", failedCount);
                responseJSON.put("non_compliant_rule_count", nonComplaintCount);
                responseJSON.put("yet_to_evaluate_count", yetToEval);
                responseJSON.put("total_rule_count", totalCount);
                responseJSON.put("compliance_state", complianceState);
                responseJSON.put("rule_state", ruleState);
            }
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getCurrentRuleComplianceStatus() >   Error   ", e);
            throw e;
        }
    }
    
    public void createComplianceScheduler(final Long customerId) throws Exception {
        try {
            final Long userId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            final String userName = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
            final String workEngineId = "DesktopCentral";
            final String taskName = "ComplianceDelayedActionExecutionTask";
            final HashMap schedulerMap = new HashMap();
            schedulerMap.put("operationType", String.valueOf(107));
            schedulerMap.put("schType", "Hourly");
            schedulerMap.put("schedulerName", taskName);
            schedulerMap.put("className", "com.me.mdm.server.compliance.task.ComplianceDelayedActionExecutionTask");
            schedulerMap.put("workflowName", taskName);
            schedulerMap.put("timePeriod", "60");
            schedulerMap.put("unitOfTime", "minutes");
            schedulerMap.put("taskName", taskName);
            schedulerMap.put("ownder", userName);
            schedulerMap.put("customerID", customerId);
            schedulerMap.put("userID", userId);
            schedulerMap.put("workEngineId", workEngineId);
            schedulerMap.put("description", "Scheduler for MDM Compliance Delayed Actions");
            ApiFactoryProvider.getSchedulerAPI().createScheduler(schedulerMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- createComplianceScheduler() >   Error   ", e);
            throw e;
        }
    }
    
    static {
        ComplianceDBUtil.complianceDBUtil = null;
    }
}
