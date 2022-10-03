package com.me.mdm.server.geofence;

import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.geofence.listener.GeoFenceListenerHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import javax.transaction.SystemException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class GeoFenceFacade
{
    private Logger logger;
    
    public GeoFenceFacade() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public JSONObject createGeoFence(final JSONObject messageJSON) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        try {
            if (!messageJSON.has("msg_body")) {
                this.logger.log(Level.SEVERE, " -- createGeoFence() >   Error ");
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject requestJSON = messageJSON.getJSONObject("msg_body");
            if (requestJSON.length() == 0) {
                this.logger.log(Level.SEVERE, " -- createGeoFence() >   Error ");
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long customerId = APIUtil.getCustomerID(messageJSON);
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)APIUtil.getUserID(messageJSON));
            MDMUtil.getUserTransaction().begin();
            JSONObject responseJSON = GeoFenceDBUtil.getInstance().addOrUpdateGeoFence(requestJSON);
            responseJSON = GeoFenceDBUtil.getInstance().getGeoFence(responseJSON);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("customer_id", (Object)customerId);
            final String geofenceName = String.valueOf(responseJSON.get("fence_name"));
            eventLogJSON.put("remarks", (Object)"mdm.geofence.created");
            eventLogJSON.put("remarks_args", (Object)geofenceName);
            eventLogJSON.put("user_name", (Object)APIUtil.getUserName(messageJSON));
            eventLogJSON.put("event_id", 72410);
            GeoFenceDBUtil.getInstance().geoFenceEventLogEntry(eventLogJSON);
            MDMUtil.getUserTransaction().commit();
            secLog.put((Object)"GEOFENCE_ID", responseJSON.opt("geo_fence_id"));
            remarks = "create-success";
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, "error in transaction rollback()...", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- createGeoFence() >   Error ");
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in createGeoFence()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ADD_GEOFENCE", secLog);
        }
    }
    
    public JSONObject modifyGeoFence(JSONObject requestJSON) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            JSONObject responseJSON = new JSONObject();
            final Long geofenceId = APIUtil.getResourceID(requestJSON, "geofenc_id");
            secLog.put((Object)"GEOFENCE_ID", (Object)geofenceId);
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            requestJSON.put("geo_fence_id", (Object)geofenceId);
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            MDMUtil.getUserTransaction().begin();
            this.checkGeoFenceExistence(requestJSON);
            requestJSON = requestJSON.getJSONObject("msg_body");
            requestJSON.put("geo_fence_id", (Object)geofenceId);
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            responseJSON = GeoFenceDBUtil.getInstance().addOrUpdateGeoFence(requestJSON);
            final JSONObject eventLogJSON = new JSONObject();
            eventLogJSON.put("customer_id", (Object)customerId);
            final String geofenceName = String.valueOf(responseJSON.get("fence_name"));
            eventLogJSON.put("remarks", (Object)"mdm.geofence.updated");
            eventLogJSON.put("remarks_args", (Object)geofenceName);
            eventLogJSON.put("user_name", (Object)userName);
            eventLogJSON.put("event_id", 72411);
            GeoFenceDBUtil.getInstance().geoFenceEventLogEntry(eventLogJSON);
            final JSONObject geoFenceListenerJSON = new JSONObject();
            geoFenceListenerJSON.put("geo_fence_id", (Object)geofenceId);
            geoFenceListenerJSON.put("customer_id", (Object)customerId);
            geoFenceListenerJSON.put("user_id", (Object)userId);
            GeoFenceListenerHandler.getInstance().invokeGeoFenceListener(geoFenceListenerJSON, 1);
            MDMUtil.getUserTransaction().commit();
            remarks = "update-success";
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, "error in transaction rollback()...", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- modifyGeoFence() >   Error ");
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in modifyGeoFence()...", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "MODIFY_GEOFENCE", secLog);
        }
    }
    
    public void checkGeoFenceExistence(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long geoFenceId = JSONUtil.optLongForUVH(requestJSON, "geo_fence_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Table geoFenceTable = new Table("GeoFence");
            final SelectQuery geoFenceQuery = (SelectQuery)new SelectQueryImpl(geoFenceTable);
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "GEO_FENCE_ID"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "CUSTOMER_ID"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "IS_MOVED_TO_TRASH"));
            final Criteria geoFenceCriteria = new Criteria(new Column("GeoFence", "GEO_FENCE_ID"), (Object)geoFenceId, 0);
            final Criteria customerCriteria = new Criteria(new Column("GeoFence", "CUSTOMER_ID"), (Object)customerId, 0);
            geoFenceQuery.setCriteria(geoFenceCriteria.and(customerCriteria));
            final DataObject geofenceDO = MDMUtil.getPersistence().get(geoFenceQuery);
            if (geofenceDO.isEmpty()) {
                this.logger.log(Level.SEVERE, " -- checkGeoFenceExistence() >   Error ");
                throw new APIHTTPException("COM0008", new Object[] { I18N.getMsg("mdm.geofence.fence_not_exist", new Object[0]) });
            }
            if (geofenceDO.getRow("GeoFence").get("IS_MOVED_TO_TRASH")) {
                this.logger.log(Level.SEVERE, " -- checkGeoFenceExistence() >   Error ");
                final String remarks = I18N.getMsg("mdm.geofence.fence_trashed", new Object[] { "1" });
                throw new APIHTTPException("COM0008", new Object[] { remarks });
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- checkGeoFenceExistence() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject removeGeoFence(final JSONObject requestJSON) throws APIHTTPException {
        try {
            MDMUtil.getUserTransaction().begin();
            Long geoFenceId = APIUtil.getResourceID(requestJSON, "geofenc_id");
            this.logger.log(Level.INFO, "geo fence id {0}", geoFenceId);
            JSONArray geoFenceJSONArray = new JSONArray();
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            if (geoFenceId != -1L) {
                requestJSON.put("geo_fence_id", (Object)geoFenceId);
                requestJSON.put("customer_id", (Object)customerId);
                geoFenceJSONArray.put((Object)geoFenceId);
            }
            else {
                geoFenceJSONArray = requestJSON.getJSONObject("msg_body").getJSONArray("geofence_ids");
                for (int i = 0; i < geoFenceJSONArray.length(); ++i) {
                    geoFenceId = geoFenceJSONArray.getLong(i);
                    requestJSON.put("geo_fence_id", (Object)geoFenceId);
                    requestJSON.put("customer_id", (Object)customerId);
                }
            }
            requestJSON.put("geo_fence_ids", (Object)geoFenceJSONArray);
            requestJSON.put("user_id", (Object)APIUtil.getUserID(requestJSON));
            GeoFenceDBUtil.getInstance().removeGeoFence(requestJSON);
            MDMUtil.getUserTransaction().commit();
            final JSONObject profileIdJSON = new JSONObject();
            profileIdJSON.put("geo_fence_id", (Object)geoFenceId);
            return profileIdJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- removeGeoFence() >   Error ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error in transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- removeGeoFence() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- removeGeoFence() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getGeoFenceDetails(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long geoFenceId = APIUtil.getResourceID(requestJSON, "geofenc_id");
            requestJSON.put("geo_fence_id", (Object)geoFenceId);
            requestJSON.put("customer_id", (Object)customerId);
            final JSONObject responseJSON = this.getGeoFence(requestJSON);
            final JSONObject geoFenceAssociatedJSON = GeoFenceDBUtil.getInstance().getGeoFenceInUseDetails(requestJSON);
            responseJSON.put("association_details", (Object)geoFenceAssociatedJSON.getJSONArray("association_details"));
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getGeoFenceDetails() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getGeoFenceDetails() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getGeoFence(final JSONObject requestJSON) throws JSONException, DataAccessException, SyMException {
        try {
            this.checkGeoFenceExistence(requestJSON);
            final JSONObject responseJSON = GeoFenceDBUtil.getInstance().getGeoFence(requestJSON);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException | SyMException e) {
            this.logger.log(Level.SEVERE, " -- getGeoFenceDetails() >   Error ", e);
            throw e;
        }
    }
    
    public JSONObject getAllGeoFence(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            requestJSON.put("customer_id", (Object)customerId);
            final JSONObject responseJSON = GeoFenceDBUtil.getInstance().getAllGeoFenceFromDB(requestJSON);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getAllGeoFence() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getAllGeoFence() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public boolean checkGeoFenceNameExists(final Long customerId, final String geoFenceName) throws DataAccessException {
        try {
            Boolean isExists = false;
            final SelectQuery geoFenceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GeoFence"));
            final Criteria customerCriteria = new Criteria(new Column("GeoFence", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria nameCriteria = new Criteria(new Column("GeoFence", "FENCE_NAME"), (Object)geoFenceName, 2, (boolean)Boolean.FALSE);
            final Criteria trashCriteria = new Criteria(new Column("GeoFence", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0);
            geoFenceQuery.setCriteria(customerCriteria.and(nameCriteria).and(trashCriteria));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "GEO_FENCE_ID"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "CUSTOMER_ID"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "FENCE_NAME"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "IS_MOVED_TO_TRASH"));
            if (!MDMUtil.getPersistence().get(geoFenceQuery).isEmpty()) {
                isExists = true;
            }
            return isExists;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- checkGeoFenceNameExists() >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    public String getFenceNameForGeoFenceId(final Long customerId, final Long geoFenceId) throws DataAccessException {
        try {
            final SelectQuery geoFenceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GeoFence"));
            final Criteria customerCriteria = new Criteria(new Column("GeoFence", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria idCriteria = new Criteria(new Column("GeoFence", "GEO_FENCE_ID"), (Object)geoFenceId, 0);
            geoFenceQuery.setCriteria(customerCriteria.and(idCriteria));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "GEO_FENCE_ID"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "CUSTOMER_ID"));
            geoFenceQuery.addSelectColumn(new Column("GeoFence", "FENCE_NAME"));
            String name = "";
            final DataObject dataObject = MDMUtil.getPersistence().get(geoFenceQuery);
            if (!dataObject.isEmpty()) {
                name = (String)dataObject.getRow("GeoFence").get("FENCE_NAME");
            }
            return name;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- checkGeoFenceNameExists() >   Error ", (Throwable)e);
            throw e;
        }
    }
    
    public boolean checkGeoFenceDeleteSafe(final Long geoFenceId) throws DataAccessException {
        try {
            boolean deleteSafe = false;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GeoFenceCriteria"));
            final Join ruleToRuleCriteriaJoin = new Join("GeoFenceCriteria", "RulesToRuleCriteria", new String[] { "RULE_CRITERIA_ID" }, new String[] { "RULE_CRITERIA_ID" }, 2);
            final Join ruleEngineJoin = new Join("RulesToRuleCriteria", "RuleEngine", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join collectionToRulesJoin = new Join("RuleEngine", "CollectionToRules", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join profileToCollectionJoin = new Join("CollectionToRules", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            selectQuery.addJoin(ruleToRuleCriteriaJoin);
            selectQuery.addJoin(ruleEngineJoin);
            selectQuery.addJoin(collectionToRulesJoin);
            selectQuery.addJoin(profileToCollectionJoin);
            selectQuery.addJoin(profileJoin);
            selectQuery.addSelectColumn(Column.getColumn("GeoFenceCriteria", "GEO_FENCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GeoFenceCriteria", "RULE_CRITERIA_ID"));
            final Criteria complianceGeoFenceCriteria = new Criteria(new Column("GeoFenceCriteria", "GEO_FENCE_ID"), (Object)geoFenceId, 0);
            final Criteria profileTrashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            final Criteria ruleTrashCriteria = new Criteria(Column.getColumn("RuleEngine", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            selectQuery.setCriteria(complianceGeoFenceCriteria.and(profileTrashCriteria).and(ruleTrashCriteria));
            if (MDMUtil.getPersistence().get(selectQuery).isEmpty()) {
                deleteSafe = true;
            }
            return deleteSafe;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- checkGeoFenceNameExists() >   Error ", (Throwable)e);
            throw e;
        }
    }
}
