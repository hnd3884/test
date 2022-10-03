package com.me.mdm.server.compliance.dbutil;

import com.me.mdm.api.error.APIHTTPException;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.compliance.ComplianceFacade;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.geofence.GeoFenceFacade;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuleEngineDBUtil
{
    private static RuleEngineDBUtil ruleEngineDBUtil;
    private Logger logger;
    
    private RuleEngineDBUtil() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- RuleEngineDBUtil() >   new object creation");
    }
    
    public static RuleEngineDBUtil getInstance() {
        if (RuleEngineDBUtil.ruleEngineDBUtil == null) {
            RuleEngineDBUtil.ruleEngineDBUtil = new RuleEngineDBUtil();
        }
        return RuleEngineDBUtil.ruleEngineDBUtil;
    }
    
    protected DataObject addOrUpdateRuleEngine(final JSONObject requestJSON) throws Exception {
        try {
            final DataObject addRuleEngineDO = (DataObject)new WritableDataObject();
            final JSONObject ruleJSON = requestJSON.getJSONObject("rule");
            final String ruleName = String.valueOf(ruleJSON.get("rule_name"));
            final int evaluationOrder = ruleJSON.optInt("execution_order", -1);
            final JSONArray addRuleJSONArray = new JSONArray();
            final JSONArray removeRuleJSONArray = new JSONArray();
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Long ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            if (ruleId >= -1L) {
                final Row ruleEngineRow = new Row("RuleEngine");
                ruleEngineRow.set("RULE_NAME", (Object)ruleName);
                addRuleEngineDO.addRow(ruleEngineRow);
                UniqueValueHolder ruleIdUVH = new UniqueValueHolder();
                ruleIdUVH = (UniqueValueHolder)ruleEngineRow.get("RULE_ID");
                ruleJSON.put("rule_id", (Object)ruleIdUVH);
                ruleJSON.put("customer_id", (Object)JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L)));
                ruleEngineRow.set("LAST_MODIFIED_BY", (Object)userId);
                ruleEngineRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                ruleEngineRow.set("CUSTOMER_ID", (Object)customerId);
                final JSONObject addRuleJSON = new JSONObject();
                addRuleJSON.put("rule_id", (Object)ruleIdUVH);
                addRuleJSON.put("evaluation_order", evaluationOrder);
                addRuleJSONArray.put((Object)addRuleJSON);
                if (ruleId == -1L) {
                    ruleEngineRow.set("CREATED_BY", (Object)userId);
                    ruleEngineRow.set("CREATION_TIME", (Object)System.currentTimeMillis());
                }
                else if (ruleId > 0L) {
                    removeRuleJSONArray.put((Object)ruleId);
                    final DataObject dataObject = MDMUtil.getPersistence().get("RuleEngine", new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0));
                    final Row ruleRow = dataObject.getRow("RuleEngine");
                    ruleEngineRow.set("CREATED_BY", ruleRow.get("CREATED_BY"));
                    ruleEngineRow.set("CREATION_TIME", ruleRow.get("CREATION_TIME"));
                }
                addRuleEngineDO.merge(this.addOrUpdateRuleCriteria(ruleJSON));
                final JSONObject mappingJSON = new JSONObject();
                mappingJSON.put("remove_rule_list", (Object)removeRuleJSONArray);
                mappingJSON.put("add_rule_list", (Object)addRuleJSONArray);
                mappingJSON.put("collection_id", requestJSON.get("collection_id"));
                addRuleEngineDO.merge(ComplianceDBUtil.getInstance().mapCollectionToRules(mappingJSON));
            }
            return addRuleEngineDO;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateRuleEngine()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject addOrUpdateRuleCriteria(final JSONObject requestJSON) throws Exception {
        try {
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Object ruleId = requestJSON.get("rule_id");
            final DataObject addRuleCriteriaDO = (DataObject)new WritableDataObject();
            final JSONArray ruleCriterionsJSONArray = requestJSON.getJSONArray("rule_criterions");
            for (int i = 0; i < ruleCriterionsJSONArray.length(); ++i) {
                JSONObject ruleCriteriaJSON = new JSONObject();
                ruleCriteriaJSON.put("customer_id", (Object)customerId);
                ruleCriteriaJSON = ruleCriterionsJSONArray.getJSONObject(i);
                final long ruleCriteriaId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "rule_criteria_id", Long.valueOf(-1L));
                if (ruleCriteriaId >= -1L) {
                    final boolean moveToTrash = ruleCriteriaJSON.optBoolean("move_to_trash", (boolean)Boolean.FALSE);
                    if (!moveToTrash) {
                        final int ruleCriteriaType = ruleCriteriaJSON.getInt("rule_criteria_type");
                        switch (ruleCriteriaType) {
                            case 2: {
                                ruleCriteriaJSON.put("customer_id", (Object)JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L)));
                                final DataObject dataObject = this.addOrUpdateGeoFenceCriteria(ruleCriteriaJSON);
                                addRuleCriteriaDO.merge(dataObject);
                                final UniqueValueHolder ruleCriteriaUVH = (UniqueValueHolder)dataObject.getFirstValue("GeoFenceCriteria", "RULE_CRITERIA_ID");
                                ruleCriteriaJSON.put("rule_criteria_id", (Object)ruleCriteriaUVH);
                                ruleCriteriaJSON.put("rule_id", ruleId);
                                addRuleCriteriaDO.merge(this.mapRulesToRuleCriteria(ruleCriteriaJSON));
                                break;
                            }
                            default: {
                                this.logger.log(Level.SEVERE, " -- addOrUpdateRuleCriteria()    >   invalid ruleCriteriaType   {0}", ruleCriteriaType);
                                throw new UnsupportedOperationException();
                            }
                        }
                    }
                    else {
                        final JSONArray removeRuleCriteriaJSONArray = new JSONArray();
                        final JSONObject removeRuleCriteriasJSON = new JSONObject();
                        removeRuleCriteriaJSONArray.put((Object)ruleCriteriaJSON);
                        removeRuleCriteriasJSON.put("rule_criterions", (Object)removeRuleCriteriaJSONArray);
                        this.removeRuleCriteriaFromDB(removeRuleCriteriasJSON, 0);
                    }
                }
            }
            return addRuleCriteriaDO;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateRuleCriteria()    >   Error   ", e);
            throw e;
        }
    }
    
    private void removeRuleCriteriaFromDB(final JSONObject ruleJSON, final int i) throws JSONException, DataAccessException {
        try {
            Long ruleCriteriaId = null;
            final ArrayList<Long> ruleCriteriaIdList = new ArrayList<Long>();
            JSONArray ruleCriteriaJSONArray = new JSONArray();
            ruleCriteriaJSONArray = ruleJSON.getJSONArray("rule_criterions");
            for (int j = 0; j < ruleCriteriaJSONArray.length(); ++j) {
                JSONObject ruleCriteriaJSON = new JSONObject();
                ruleCriteriaJSON = ruleCriteriaJSONArray.getJSONObject(j);
                ruleCriteriaId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "rule_criteria_id", Long.valueOf(-1L));
                ruleCriteriaIdList.add(ruleCriteriaId);
            }
            final Criteria ruleCriteriaCriteria = new Criteria(new Column("RuleCriteria", "RULE_CRITERIA_ID"), (Object)ruleCriteriaIdList.toArray(), 8);
            MDMUtil.getPersistence().delete(ruleCriteriaCriteria);
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- removeRuleCriteriaFromDB()    >   Error   ", e);
            throw e;
        }
    }
    
    private DataObject addOrUpdateGeoFenceCriteria(final JSONObject ruleCriteriaJSON) throws Exception {
        try {
            final Long customerId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "customer_id", Long.valueOf(-1L));
            final Long ruleCriteriaId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "rule_criteria_id", Long.valueOf(-1L));
            final DataObject geoFenceDO = (DataObject)new WritableDataObject();
            if (ruleCriteriaId >= -1L) {
                final Row ruleCriteriaRow = new Row("RuleCriteria");
                ruleCriteriaRow.set("RULE_CRITERIA_TYPE", (Object)2);
                final int ruleCriteriaState = ruleCriteriaJSON.getInt("rule_criteria_state");
                ruleCriteriaRow.set("RULE_CRITERIA_STATE", (Object)ruleCriteriaState);
                geoFenceDO.addRow(ruleCriteriaRow);
                final UniqueValueHolder ruleCriteriaUVH = (UniqueValueHolder)ruleCriteriaRow.get("RULE_CRITERIA_ID");
                final Long geoFenceId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "geo_fence_id", Long.valueOf(-1L));
                ruleCriteriaJSON.put("geo_fence_id", (Object)geoFenceId);
                final GeoFenceFacade geoFenceFacade = new GeoFenceFacade();
                geoFenceFacade.checkGeoFenceExistence(ruleCriteriaJSON);
                final Row geoFenceCriteriaRow = new Row("GeoFenceCriteria");
                geoFenceCriteriaRow.set("GEO_FENCE_ID", (Object)geoFenceId);
                geoFenceCriteriaRow.set("RULE_CRITERIA_ID", (Object)ruleCriteriaUVH);
                geoFenceDO.addRow(geoFenceCriteriaRow);
            }
            return geoFenceDO;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateGeoFenceCriteria()    >   Error   ", e);
            throw e;
        }
    }
    
    public Boolean checkGeoTrackingEnabled(final Long customerID) throws Exception {
        boolean isGeoTrackingEnabled = false;
        try {
            final int trackingStatus = (int)DBUtil.getValueFromDB("LocationSettings", "CUSTOMER_ID", (Object)customerID, "TRACKING_STATUS");
            if (trackingStatus == 1) {
                isGeoTrackingEnabled = true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- checkGeoTrackingEnabled()    >   Error   ", e);
            throw e;
        }
        return isGeoTrackingEnabled;
    }
    
    private DataObject mapRulesToRuleCriteria(final JSONObject ruleToRuleCriteriaJSON) throws JSONException, DataAccessException {
        try {
            final Object ruleId = ruleToRuleCriteriaJSON.get("rule_id");
            final Object ruleCriteriaId = ruleToRuleCriteriaJSON.get("rule_criteria_id");
            final DataObject ruleCriteriaDO = (DataObject)new WritableDataObject();
            final Row rulesToRuleCriteriaRow = new Row("RulesToRuleCriteria");
            rulesToRuleCriteriaRow.set("RULE_CRITERIA_ID", ruleCriteriaId);
            rulesToRuleCriteriaRow.set("RULE_ID", ruleId);
            ruleCriteriaDO.addRow(rulesToRuleCriteriaRow);
            return ruleCriteriaDO;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- mapRulesToRuleCriteria()    >   Error   ", e);
            throw e;
        }
    }
    
    protected JSONObject removeRuleFromDB(final JSONObject ruleJSON) throws JSONException {
        try {
            JSONObject removeRuleCriteriaJSON = new JSONObject();
            removeRuleCriteriaJSON = this.removeRuleCriteriaFromDB(ruleJSON);
            Long ruleId = null;
            ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            final Criteria ruleEngineCriteria = new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0);
            removeRuleCriteriaJSON.put("remove_rule_criteria", (Object)ruleEngineCriteria);
            return removeRuleCriteriaJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- removeRuleFromDB()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    private JSONObject removeRuleCriteriaFromDB(final JSONObject ruleJSON) throws JSONException {
        try {
            Long ruleCriteriaId = null;
            final ArrayList<Long> ruleCriteriaIdList = new ArrayList<Long>();
            JSONArray ruleCriteriaJSONArray = new JSONArray();
            ruleCriteriaJSONArray = ruleJSON.getJSONArray("rule_criterions");
            for (int j = 0; j < ruleCriteriaJSONArray.length(); ++j) {
                JSONObject ruleCriteriaJSON = new JSONObject();
                ruleCriteriaJSON = ruleCriteriaJSONArray.getJSONObject(j);
                ruleCriteriaId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "rule_criteria_id", Long.valueOf(-1L));
                ruleCriteriaIdList.add(ruleCriteriaId);
            }
            final Criteria ruleCriteriaCriteria = new Criteria(new Column("RuleCriteria", "RULE_CRITERIA_ID"), (Object)ruleCriteriaIdList.toArray(), 8);
            final JSONObject removeRuleCriteriaJSON = new JSONObject();
            removeRuleCriteriaJSON.put("remove_rulecriteria_criteria", (Object)ruleCriteriaCriteria);
            return removeRuleCriteriaJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- removeRuleCriteriaFromDB()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject getRule(JSONObject ruleJSON, final int param) throws DataAccessException, JSONException, SyMException {
        try {
            ruleJSON = this.getRuleCriteria(ruleJSON, param);
            final Long ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            final Table ruleEngineTable = new Table("RuleEngine");
            final SelectQuery ruleQuery = (SelectQuery)new SelectQueryImpl(ruleEngineTable);
            ruleQuery.setCriteria(new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0));
            ruleQuery.addSelectColumn(new Column("RuleEngine", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(ruleQuery);
            final ComplianceFacade complianceFacade = new ComplianceFacade();
            final Row ruleEngineRow = dataObject.getRow("RuleEngine");
            final Long createdBy = (Long)ruleEngineRow.get("CREATED_BY");
            final Long modifiedBy = (Long)ruleEngineRow.get("LAST_MODIFIED_BY");
            ruleJSON.put("rule_name", ruleEngineRow.get("RULE_NAME"));
            ruleJSON.put("created_by", (Object)createdBy);
            ruleJSON.put("creation_time", ruleEngineRow.get("CREATION_TIME"));
            ruleJSON.put("last_modified_by", (Object)modifiedBy);
            ruleJSON.put("created_by_name", (Object)DMUserHandler.getUserNameFromUserID(createdBy));
            ruleJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(modifiedBy));
            ruleJSON.put("last_modified_time", ruleEngineRow.get("LAST_MODIFIED_TIME"));
            return ruleJSON;
        }
        catch (final JSONException | DataAccessException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getRule()  >   Error getting rule from DB  ", e);
            throw e;
        }
    }
    
    private JSONObject getRuleCriteria(final JSONObject ruleJSON, final int param) throws JSONException, DataAccessException, SyMException {
        try {
            Long ruleId = null;
            ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            final Table ruleEngineTable = new Table("RuleEngine");
            final SelectQuery ruleQuery = (SelectQuery)new SelectQueryImpl(ruleEngineTable);
            final Join rulesToRuleCriteriaJoin = new Join("RuleEngine", "RulesToRuleCriteria", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1);
            ruleQuery.addJoin(rulesToRuleCriteriaJoin);
            final Join ruleCriteriaJoin = new Join("RulesToRuleCriteria", "RuleCriteria", new String[] { "RULE_CRITERIA_ID" }, new String[] { "RULE_CRITERIA_ID" }, 1);
            ruleQuery.addJoin(ruleCriteriaJoin);
            ruleQuery.addSelectColumn(new Column("RuleEngine", "RULE_ID"));
            ruleQuery.addSelectColumn(new Column("RulesToRuleCriteria", "RULE_ID"));
            ruleQuery.addSelectColumn(new Column("RulesToRuleCriteria", "RULE_CRITERIA_ID"));
            ruleQuery.addSelectColumn(new Column("RuleCriteria", "RULE_CRITERIA_ID"));
            ruleQuery.addSelectColumn(new Column("RuleCriteria", "RULE_CRITERIA_TYPE"));
            ruleQuery.addSelectColumn(new Column("RuleCriteria", "RULE_CRITERIA_STATE"));
            ruleQuery.setCriteria(new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0));
            DataObject ruleDataObject = null;
            ruleDataObject = MDMUtil.getPersistence().get(ruleQuery);
            Iterator ruleCriteriaIterator = null;
            final JSONArray ruleCriteriaJSONArray = new JSONArray();
            if (param == 0) {
                ruleCriteriaIterator = ruleDataObject.getRows("RulesToRuleCriteria");
                while (ruleCriteriaIterator.hasNext()) {
                    final Row rulesToRuleCriteriaRow = ruleCriteriaIterator.next();
                    final Long ruleCriteriaId = (Long)rulesToRuleCriteriaRow.get("RULE_CRITERIA_ID");
                    final JSONObject ruleCriteriaJSON = new JSONObject();
                    ruleCriteriaJSON.put("rule_criteria_id", (Object)ruleCriteriaId);
                    ruleCriteriaJSONArray.put((Object)ruleCriteriaJSON);
                }
            }
            else if (param == 1) {
                ruleCriteriaIterator = ruleDataObject.getRows("RuleCriteria");
                while (ruleCriteriaIterator.hasNext()) {
                    final Row rulesToRuleCriteriaRow = ruleCriteriaIterator.next();
                    final Long ruleCriteriaId = (Long)rulesToRuleCriteriaRow.get("RULE_CRITERIA_ID");
                    final int ruleCriteriaType = (int)rulesToRuleCriteriaRow.get("RULE_CRITERIA_TYPE");
                    final int ruleCriteriaState = (int)rulesToRuleCriteriaRow.get("RULE_CRITERIA_STATE");
                    JSONObject ruleCriteriaJSON2 = new JSONObject();
                    ruleCriteriaJSON2.put("customer_id", (Object)JSONUtil.optLongForUVH(ruleJSON, "customer_id", Long.valueOf(-1L)));
                    ruleCriteriaJSON2.put("rule_criteria_id", (Object)ruleCriteriaId);
                    switch (ruleCriteriaType) {
                        case 2: {
                            ruleCriteriaJSON2 = this.getGeoFenceCriteria(ruleCriteriaJSON2);
                            break;
                        }
                        default: {
                            this.logger.log(Level.SEVERE, " -- getRuleCriteria()    >   invalid ruleCriteriaType  {0}", ruleCriteriaType);
                            break;
                        }
                    }
                    ruleCriteriaJSON2.put("rule_criteria_type", ruleCriteriaType);
                    ruleCriteriaJSON2.put("rule_criteria_state", ruleCriteriaState);
                    ruleCriteriaJSONArray.put((Object)ruleCriteriaJSON2);
                }
            }
            else if (param == 2) {
                ruleCriteriaIterator = ruleDataObject.getRows("RuleCriteria");
                while (ruleCriteriaIterator.hasNext()) {
                    final Row rulesToRuleCriteriaRow = ruleCriteriaIterator.next();
                    final Long ruleCriteriaId = (Long)rulesToRuleCriteriaRow.get("RULE_CRITERIA_ID");
                    final int ruleCriteriaType = (int)rulesToRuleCriteriaRow.get("RULE_CRITERIA_TYPE");
                    final int ruleCriteriaState = (int)rulesToRuleCriteriaRow.get("RULE_CRITERIA_STATE");
                    final JSONObject ruleCriteriaJSON2 = new JSONObject();
                    JSONObject ruleCriteriaParams = new JSONObject();
                    ruleCriteriaJSON2.put("rule_criteria_id", (Object)ruleCriteriaId);
                    ruleCriteriaJSON2.put("customer_id", (Object)JSONUtil.optLongForUVH(ruleJSON, "customer_id", Long.valueOf(-1L)));
                    switch (ruleCriteriaType) {
                        case 2: {
                            ruleCriteriaParams = this.getGeoFenceCriteria(ruleCriteriaJSON2);
                            ruleCriteriaParams.remove("last_modified_time");
                            ruleCriteriaParams.remove("creation_time");
                            ruleCriteriaParams.remove("is_moved_to_trash");
                            ruleCriteriaParams.remove("created_by");
                            ruleCriteriaParams.remove("last_modified_by");
                            break;
                        }
                        default: {
                            this.logger.log(Level.SEVERE, " -- getRuleCriteria()    >   invalid ruleCriteriaType  {0}", ruleCriteriaType);
                            break;
                        }
                    }
                    ruleCriteriaJSON2.put("rule_criteria_params", (Object)ruleCriteriaParams);
                    ruleCriteriaJSON2.remove("geo_fence_id");
                    ruleCriteriaJSON2.remove("customer_id");
                    ruleCriteriaJSON2.put("rule_criteria_type", ruleCriteriaType);
                    ruleCriteriaJSON2.put("rule_criteria_state", ruleCriteriaState);
                    ruleCriteriaJSONArray.put((Object)ruleCriteriaJSON2);
                }
            }
            ruleJSON.put("rule_criterions", (Object)ruleCriteriaJSONArray);
            return ruleJSON;
        }
        catch (final JSONException | DataAccessException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getRuleCriteria()    >   Error   ", e);
            throw e;
        }
    }
    
    private JSONObject getGeoFenceCriteria(final JSONObject ruleCriteriaJSON) throws JSONException, DataAccessException, SyMException {
        try {
            final Long ruleCriteriaId = JSONUtil.optLongForUVH(ruleCriteriaJSON, "rule_criteria_id", Long.valueOf(-1L));
            final Long geoFenceId = (Long)MDMUtil.getPersistence().get("GeoFenceCriteria", new Criteria(new Column("GeoFenceCriteria", "RULE_CRITERIA_ID"), (Object)ruleCriteriaId, 0)).getRow("GeoFenceCriteria").get("GEO_FENCE_ID");
            final GeoFenceFacade geoFenceFacade = new GeoFenceFacade();
            ruleCriteriaJSON.put("geo_fence_id", (Object)geoFenceId);
            return geoFenceFacade.getGeoFence(ruleCriteriaJSON);
        }
        catch (final DataAccessException | JSONException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getGeoFenceCriteria()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getRulesForCollection(final JSONObject complianceJSON) throws JSONException, DataAccessException {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(complianceJSON, "collection_id", Long.valueOf(-1L));
            final Criteria collectionCriteria = new Criteria(new Column("CollectionToRules", "COLLECTION_ID"), (Object)collectionId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("CollectionToRules", collectionCriteria);
            final Iterator collectionToRulesIterator = dataObject.getRows("CollectionToRules");
            final JSONArray ruleJSONArray = new JSONArray();
            while (collectionToRulesIterator.hasNext()) {
                final Row collectionToRulesRow = collectionToRulesIterator.next();
                ruleJSONArray.put((long)collectionToRulesRow.get("RULE_ID"));
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("rules", (Object)ruleJSONArray);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getRulesForCollection()    >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getRuleDetails(final JSONObject ruleJSON) throws JSONException, DataAccessException {
        try {
            final Long ruleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
            final DataObject dataObject = MDMUtil.getPersistence().get("RuleEngine", new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0));
            if (!dataObject.isEmpty()) {
                final Row ruleRow = dataObject.getRow("RuleEngine");
                final String ruleName = (String)ruleRow.get("RULE_NAME");
                final Long createdBy = (Long)ruleRow.get("CREATED_BY");
                final Long customerId = (Long)ruleRow.get("CUSTOMER_ID");
                final Long lastModifiedBy = (Long)ruleRow.get("LAST_MODIFIED_BY");
                final Boolean isTrashed = (Boolean)ruleRow.get("IS_MOVED_TO_TRASH");
                ruleJSON.put("rule_name", (Object)ruleName);
                ruleJSON.put("created_by", (Object)createdBy);
                ruleJSON.put("customer_id", (Object)customerId);
                ruleJSON.put("last_modified_by", (Object)lastModifiedBy);
                ruleJSON.put("is_moved_to_trash", (Object)isTrashed);
                return ruleJSON;
            }
            throw new UnsupportedOperationException("Rule Does not exist    " + ruleId);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getRuleDetails()    >   Error   ", e);
            throw e;
        }
    }
    
    public void trashRule(final JSONObject trashRuleJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray ruleArray = (JSONArray)trashRuleJSON.get("remove_rule_list");
            final List removeRuleList = new ArrayList();
            for (int i = 0; i < ruleArray.length(); ++i) {
                removeRuleList.add(ruleArray.getLong(i));
            }
            final DataObject dataObject = MDMUtil.getPersistence().get("RuleEngine", new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)removeRuleList.toArray(), 8));
            final Iterator iterator = dataObject.getRows("RuleEngine");
            while (iterator.hasNext()) {
                final Row ruleRow = iterator.next();
                ruleRow.set("IS_MOVED_TO_TRASH", (Object)Boolean.TRUE);
                dataObject.updateRow(ruleRow);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- trashRule()    >   Error   ", e);
            throw e;
        }
    }
    
    public void checkRuleExistence(final JSONObject additionalDataJSON) throws DataAccessException, APIHTTPException {
        try {
            final Long ruleId = JSONUtil.optLongForUVH(additionalDataJSON, "rule_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(additionalDataJSON, "customer_id", Long.valueOf(-1L));
            final Long complianceId = JSONUtil.optLongForUVH(additionalDataJSON, "compliance_id", Long.valueOf(-1L));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "CollectionToRules", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CollectionToRules", "RuleEngine", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCustomerRel", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            selectQuery.addSelectColumns((List)Arrays.asList(new Column("Profile", "*"), new Column("ProfileToCollection", "*"), new Column("CollectionToRules", "*"), new Column("RuleEngine", "*"), new Column("ProfileToCustomerRel", "*"), new Column("CustomerInfo", "*")));
            selectQuery.setCriteria(new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0).and(new Criteria(new Column("Profile", "PROFILE_ID"), (Object)complianceId, 0)).and(new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 0)).and(new Criteria(new Column("RuleEngine", "CUSTOMER_ID"), (Object)customerId, 0)));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { "Rule Id does not exist    " + ruleId });
            }
            final Row ruleRow = dataObject.getRow("RuleEngine");
            final Boolean moveToTrash = (Boolean)ruleRow.get("IS_MOVED_TO_TRASH");
            if (moveToTrash) {
                throw new APIHTTPException("COM0008", new Object[] { "Rule moved to trash    " + ruleId });
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- checkRuleExistence()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public Long getUserIdForRule(final Long ruleId) {
        Long userId = -1L;
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get("RuleEngine", new Criteria(new Column("RuleEngine", "RULE_ID"), (Object)ruleId, 0));
            if (dataObject != null) {
                userId = (Long)dataObject.getRow("RuleEngine").get("LAST_MODIFIED_BY");
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getUserIdForRule()    >   Error   ", (Throwable)e);
        }
        return userId;
    }
    
    public String getRuleName(final Long ruleId) {
        String ruleName = "";
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get("RuleEngine", new Criteria(Column.getColumn("RuleEngine", "RULE_ID"), (Object)ruleId, 0));
            if (!dataObject.isEmpty()) {
                ruleName = (String)dataObject.getRow("RuleEngine").get("RULE_NAME");
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- getRuleName()    >   Error   ", (Throwable)e);
        }
        return ruleName;
    }
    
    public String getRuleName(final Long ruleId, final JSONArray policiesJSONArray) {
        final String ruleName = "";
        try {
            for (int i = 0; i < policiesJSONArray.length(); ++i) {
                final JSONObject ruleJSON = policiesJSONArray.getJSONObject(i).getJSONObject("rule");
                final Long checkRuleId = JSONUtil.optLongForUVH(ruleJSON, "rule_id", Long.valueOf(-1L));
                if (ruleId.equals(checkRuleId)) {
                    return String.valueOf(ruleJSON.get("rule_name"));
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- getRuleName()    >   Error   ", (Throwable)e);
        }
        return ruleName;
    }
    
    static {
        RuleEngineDBUtil.ruleEngineDBUtil = null;
    }
}
