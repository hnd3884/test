package com.me.mdm.server.geofence;

import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.api.APIUtil;
import org.json.JSONArray;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoFenceDBUtil
{
    private static GeoFenceDBUtil geoFenceDBUtil;
    private static GeoFenceFacade geoFenceFacade;
    private Logger logger;
    
    private GeoFenceDBUtil() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- GeoFenceDBUtil   >   new object creation");
    }
    
    public static GeoFenceDBUtil getInstance() {
        if (GeoFenceDBUtil.geoFenceDBUtil == null) {
            GeoFenceDBUtil.geoFenceDBUtil = new GeoFenceDBUtil();
        }
        return GeoFenceDBUtil.geoFenceDBUtil;
    }
    
    protected JSONObject addOrUpdateGeoFence(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            DataObject geoFenceDO = (DataObject)new WritableDataObject();
            final String fenceName = String.valueOf(requestJSON.get("fence_name"));
            Long geoFenceId = JSONUtil.optLongForUVH(requestJSON, "geo_fence_id", Long.valueOf(-1L));
            final String latitude = String.valueOf(requestJSON.get("latitude"));
            final String longitude = String.valueOf(requestJSON.get("longitude"));
            final String address = String.valueOf(requestJSON.get("address"));
            final int radius = requestJSON.getInt("radius");
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            if (geoFenceId == -1L) {
                if (GeoFenceDBUtil.geoFenceFacade.checkGeoFenceNameExists(customerId, fenceName)) {
                    throw new APIHTTPException("GEO003", new Object[0]);
                }
                final Long createdBy = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
                final Long currentTime = MDMUtil.getCurrentTimeInMillis();
                final Row addGeoFenceRow = new Row("GeoFence");
                addGeoFenceRow.set("FENCE_NAME", (Object)fenceName);
                addGeoFenceRow.set("CREATED_BY", (Object)createdBy);
                addGeoFenceRow.set("CREATION_TIME", (Object)currentTime);
                addGeoFenceRow.set("LAST_MODIFIED_BY", (Object)createdBy);
                addGeoFenceRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                addGeoFenceRow.set("LATITUDE", (Object)latitude);
                addGeoFenceRow.set("LONGITUDE", (Object)longitude);
                addGeoFenceRow.set("RADIUS", (Object)radius);
                addGeoFenceRow.set("ADDRESS", (Object)address);
                addGeoFenceRow.set("CUSTOMER_ID", (Object)customerId);
                geoFenceDO.addRow(addGeoFenceRow);
            }
            else {
                final Long modifiedBy = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
                final Long currentTime = MDMUtil.getCurrentTimeInMillis();
                if (GeoFenceDBUtil.geoFenceFacade.checkGeoFenceNameExists(customerId, fenceName) && !GeoFenceDBUtil.geoFenceFacade.getFenceNameForGeoFenceId(customerId, geoFenceId).equals(fenceName)) {
                    throw new APIHTTPException("GEO003", new Object[0]);
                }
                geoFenceDO = MDMUtil.getPersistence().get("GeoFence", new Criteria(new Column("GeoFence", "GEO_FENCE_ID"), (Object)geoFenceId, 0));
                final Row updateGeoFenceRow = geoFenceDO.getRow("GeoFence");
                updateGeoFenceRow.set("FENCE_NAME", (Object)fenceName);
                updateGeoFenceRow.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                updateGeoFenceRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                updateGeoFenceRow.set("LATITUDE", (Object)latitude);
                updateGeoFenceRow.set("LONGITUDE", (Object)longitude);
                updateGeoFenceRow.set("RADIUS", (Object)radius);
                updateGeoFenceRow.set("ADDRESS", (Object)address);
                geoFenceDO.updateRow(updateGeoFenceRow);
            }
            MDMUtil.getPersistence().update(geoFenceDO);
            if (geoFenceId == -1L) {
                geoFenceId = (Long)geoFenceDO.getFirstValue("GeoFence", "GEO_FENCE_ID");
            }
            geoFenceDO.isEmpty();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("geo_fence_id", (Object)geoFenceId);
            responseJSON.put("fence_name", (Object)fenceName);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateGeoFence()  >   Error ", e);
            throw e;
        }
    }
    
    protected void removeGeoFence(final JSONObject requestJSON) throws Exception {
        try {
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final List geoFenceList = new ArrayList();
            final JSONArray geoFenceArray = requestJSON.getJSONArray("geo_fence_ids");
            for (int i = 0; i < geoFenceArray.length(); ++i) {
                final Long geoFenceId = JSONUtil.optLongForUVH(geoFenceArray, i, -1L);
                requestJSON.put("geo_fence_id", (Object)geoFenceId);
                GeoFenceDBUtil.geoFenceFacade.checkGeoFenceExistence(requestJSON);
                if (!GeoFenceDBUtil.geoFenceFacade.checkGeoFenceDeleteSafe(geoFenceId)) {
                    throw new APIHTTPException("COM0010", new Object[] { "Geo fence in use - " + geoFenceId });
                }
                geoFenceList.add(geoFenceId);
            }
            final Criteria deleteGeoFenceCriteria = new Criteria(new Column("GeoFence", "GEO_FENCE_ID"), (Object)geoFenceList.toArray(), 8);
            final DataObject dataObject = MDMUtil.getPersistence().get("GeoFence", deleteGeoFenceCriteria);
            final Iterator iterator = dataObject.getRows("GeoFence");
            final JSONArray eventLogArray = new JSONArray();
            while (iterator.hasNext()) {
                final Row geoFenceRow = iterator.next();
                geoFenceRow.set("IS_MOVED_TO_TRASH", (Object)Boolean.TRUE);
                dataObject.updateRow(geoFenceRow);
                final JSONObject eventLogJSON = new JSONObject();
                eventLogJSON.put("customer_id", (Object)customerId);
                final String geofenceName = String.valueOf(geoFenceRow.get("FENCE_NAME"));
                eventLogJSON.put("remarks", (Object)"mdm.geofence.deleted");
                eventLogJSON.put("remarks_args", (Object)geofenceName);
                eventLogJSON.put("user_name", (Object)APIUtil.getUserName(requestJSON));
                eventLogJSON.put("event_id", 72412);
                eventLogArray.put((Object)eventLogJSON);
            }
            MDMUtil.getPersistence().update(dataObject);
            for (int j = 0; j < eventLogArray.length(); ++j) {
                getInstance().geoFenceEventLogEntry(eventLogArray.getJSONObject(j));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- removeGeoFence()  >   Error ", e);
            throw e;
        }
    }
    
    protected JSONObject getGeoFence(final JSONObject messageJSON) throws JSONException, DataAccessException, SyMException {
        try {
            final Long geoFenceId = JSONUtil.optLongForUVH(messageJSON, "geo_fence_id", Long.valueOf(-1L));
            final DataObject fencePolicyDO = MDMUtil.getPersistence().get("GeoFence", new Criteria(new Column("GeoFence", "GEO_FENCE_ID"), (Object)geoFenceId, 0));
            final Row geoFenceRow = fencePolicyDO.getRow("GeoFence");
            final JSONObject responseJSON = new JSONObject();
            final Long createdBy = (Long)geoFenceRow.get("CREATED_BY");
            final Long modifiedBy = (Long)geoFenceRow.get("LAST_MODIFIED_BY");
            responseJSON.put("geo_fence_id", (Object)geoFenceId);
            responseJSON.put("fence_name", geoFenceRow.get("FENCE_NAME"));
            responseJSON.put("address", geoFenceRow.get("ADDRESS"));
            responseJSON.put("created_by", (Object)createdBy);
            responseJSON.put("creation_time", geoFenceRow.get("CREATION_TIME"));
            responseJSON.put("last_modified_by", (Object)modifiedBy);
            responseJSON.put("last_modified_time", geoFenceRow.get("LAST_MODIFIED_TIME"));
            responseJSON.put("latitude", geoFenceRow.get("LATITUDE"));
            responseJSON.put("longitude", geoFenceRow.get("LONGITUDE"));
            responseJSON.put("radius", geoFenceRow.get("RADIUS"));
            responseJSON.put("created_by_name", (Object)DMUserHandler.getUserNameFromUserID(createdBy));
            responseJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(modifiedBy));
            return responseJSON;
        }
        catch (final DataAccessException | JSONException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateGegetGeoFenceoFence()  >   Error ", e);
            throw e;
        }
    }
    
    public JSONObject getAllGeoFenceFromDB(final JSONObject requestJSON) throws JSONException, DataAccessException, SyMException {
        try {
            Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final SelectQuery geoFenceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GeoFence"));
            final Criteria trashCriteria = new Criteria(Column.getColumn("GeoFence", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("GeoFence", "CUSTOMER_ID"), (Object)customerId, 0);
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "*"));
            geoFenceQuery.setCriteria(trashCriteria.and(customerCriteria));
            final DataObject geoFenceDO = MDMUtil.getPersistence().get(geoFenceQuery);
            final Iterator iterator = geoFenceDO.getRows("GeoFence");
            final JSONArray geoFenceJSONArray = new JSONArray();
            while (iterator.hasNext()) {
                JSONObject geoFenceAssociatedJSON = new JSONObject();
                final Row geoFenceRow = iterator.next();
                final JSONObject geoFenceJSON = new JSONObject();
                final String fenceName = (String)geoFenceRow.get("FENCE_NAME");
                final Long geoFenceId = (Long)geoFenceRow.get("GEO_FENCE_ID");
                final String address = (String)geoFenceRow.get("ADDRESS");
                final String latitude = (String)geoFenceRow.get("LATITUDE");
                final String longitude = (String)geoFenceRow.get("LONGITUDE");
                final Long lastModifiedTime = (Long)geoFenceRow.get("LAST_MODIFIED_TIME");
                final Long lastModifiedBy = (Long)geoFenceRow.get("LAST_MODIFIED_BY");
                final Long creationTime = (Long)geoFenceRow.get("CREATION_TIME");
                final Long createdBy = (Long)geoFenceRow.get("CREATED_BY");
                final int radius = (int)geoFenceRow.get("RADIUS");
                customerId = (Long)geoFenceRow.get("CUSTOMER_ID");
                geoFenceJSON.put("geo_fence_id", (Object)geoFenceId);
                geoFenceJSON.put("fence_name", (Object)fenceName);
                geoFenceJSON.put("address", (Object)address);
                geoFenceJSON.put("latitude", (Object)latitude);
                geoFenceJSON.put("longitude", (Object)longitude);
                geoFenceJSON.put("radius", radius);
                geoFenceJSON.put("last_modified_time", (Object)lastModifiedTime);
                geoFenceJSON.put("last_modified_by", (Object)lastModifiedBy);
                geoFenceJSON.put("last_modified_by_name", (Object)DMUserHandler.getUserNameFromUserID(lastModifiedBy));
                geoFenceJSON.put("created_by_name", (Object)DMUserHandler.getUserNameFromUserID(createdBy));
                geoFenceJSON.put("creation_time", (Object)creationTime);
                geoFenceJSON.put("created_by", (Object)createdBy);
                geoFenceAssociatedJSON = getInstance().getGeoFenceInUseDetails(geoFenceJSON);
                geoFenceJSON.put("association_details", (Object)geoFenceAssociatedJSON.getJSONArray("association_details"));
                geoFenceJSON.put("customer_id", (Object)customerId);
                geoFenceJSONArray.put((Object)geoFenceJSON);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("geo_fences", (Object)geoFenceJSONArray);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getAllGeoFenceFromDB() >   Error ", e);
            throw e;
        }
    }
    
    public void geoFenceEventLogEntry(final JSONObject requestJSON) throws JSONException {
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
            this.logger.log(Level.SEVERE, " -- geoFenceEventLogEntry() >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public JSONObject getGeoFenceInUseDetails(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            final JSONObject returnJSON = new JSONObject();
            final JSONArray associationJSONArray = new JSONArray();
            final JSONObject complianceGeoFenceJSON = this.getComplianceForGeoFence(requestJSON);
            if (complianceGeoFenceJSON.getJSONArray("compliance_profiles").length() != 0) {
                associationJSONArray.put((Object)complianceGeoFenceJSON);
            }
            returnJSON.put("association_details", (Object)associationJSONArray);
            return returnJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getGeoFenceInUseDetails() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceForGeoFence(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            final Join profileJoin = new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileToCollectionJoin = new Join("ProfileToCollection", "CollectionToRules", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join collectionToRuleJoin = new Join("CollectionToRules", "RuleEngine", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join ruleToRuleCriteriaJoin = new Join("RuleEngine", "RulesToRuleCriteria", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join ruleCriteriaToGeofenceJoin = new Join("RulesToRuleCriteria", "GeoFenceCriteria", new String[] { "RULE_CRITERIA_ID" }, new String[] { "RULE_CRITERIA_ID" }, 2);
            final Long geoFenceId = JSONUtil.optLongForUVH(requestJSON, "geo_fence_id", Long.valueOf(-1L));
            final Criteria geoFenceCriteria = new Criteria(new Column("GeoFenceCriteria", "GEO_FENCE_ID"), (Object)geoFenceId, 0);
            final Criteria trashCriteria = new Criteria(new Column("RuleEngine", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CollectionToRules", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CollectionToRules", "RULE_ID"));
            selectQuery.addSelectColumn(new Column("RuleEngine", "RULE_ID"));
            selectQuery.addSelectColumn(new Column("RulesToRuleCriteria", "RULE_CRITERIA_ID"));
            selectQuery.addSelectColumn(new Column("GeoFenceCriteria", "RULE_CRITERIA_ID"));
            selectQuery.addJoin(profileJoin);
            selectQuery.addJoin(profileToCollectionJoin);
            selectQuery.addJoin(collectionToRuleJoin);
            selectQuery.addJoin(ruleToRuleCriteriaJoin);
            selectQuery.addJoin(ruleCriteriaToGeofenceJoin);
            selectQuery.setCriteria(geoFenceCriteria.and(trashCriteria));
            final JSONObject returnJSON = new JSONObject();
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                returnJSON.put("compliance_profiles", (Object)new JSONArray());
            }
            else {
                final Iterator iterator = dataObject.getRows("Profile");
                final JSONArray complianceArray = new JSONArray();
                while (iterator.hasNext()) {
                    final Row profileRow = iterator.next();
                    final Long complianceId = (Long)profileRow.get("PROFILE_ID");
                    complianceArray.put((Object)complianceId);
                }
                returnJSON.put("compliance_profiles", (Object)complianceArray);
            }
            return returnJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceForGeoFence() >   Error   ", e);
            throw e;
        }
    }
    
    static {
        GeoFenceDBUtil.geoFenceDBUtil = null;
        GeoFenceDBUtil.geoFenceFacade = new GeoFenceFacade();
    }
}
