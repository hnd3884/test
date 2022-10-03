package com.me.mdm.server.metracker;

import org.json.simple.JSONArray;
import com.me.mdm.server.tracker.MDMCoreQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerFenceRepositoryImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerFenceRepositoryImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPFenceRepositoryImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getTrackerProperties", "MDMP Fence repository implementation begins");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addGeoFenceCount();
            this.addGeoFenceCreationCount();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getTrackerProperties", "Exception ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addGeoFenceCreationCount() {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MEMDMTrackParams"));
            final List selectList = new ArrayList();
            selectList.add("MDM_GEOFENCE_FENCE_REPOSITORY_TAB_COUNT");
            selectList.add("MDM_GEOFENCE_FENCE_REPOSITORY_CREATE_COUNT");
            selectList.add("MDM_GEOFENCE_COMPLIANCE_CREATE_COUNT");
            selectList.add("MDM_GEOFENCE_SAVE_CLICK_COUNT");
            query.addSelectColumn(new Column("MEMDMTrackParams", "*"));
            query.setCriteria(new Criteria(new Column("MEMDMTrackParams", "PARAM_NAME"), (Object)selectList.toArray(), 8));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Iterator iterator = dataObject.getRows("MEMDMTrackParams");
            final JSONObject tempJSON = new JSONObject();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String paramName = (String)row.get("PARAM_NAME");
                final String value = (String)row.get("PARAM_VALUE");
                tempJSON.put(paramName, (Object)value);
            }
            this.mdmTrackerProperties.setProperty("MDM_GEOFENCE_CREATION", tempJSON.toString());
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addGeoFenceCreationCount()    >   Error", e);
        }
    }
    
    private void addGeoFenceCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("GEO_FENCE_REPOSITORY_QUERY");
            final JSONArray resultJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final org.json.simple.JSONObject resultJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                tempJSON.put("MDM_GEOFENCE_CREATED_TOTAL_COUNT", (Object)((resultJSON.get((Object)"MDM_GEOFENCE_CREATED_TOTAL_COUNT") == null) ? Integer.valueOf(0) : resultJSON.get((Object)"MDM_GEOFENCE_CREATED_TOTAL_COUNT").toString()));
                tempJSON.put("MDM_GEOFENCE_COMPLIANCE_COUNT", (Object)((resultJSON.get((Object)"MDM_GEOFENCE_COMPLIANCE_COUNT") == null) ? Integer.valueOf(0) : resultJSON.get((Object)"MDM_GEOFENCE_COMPLIANCE_COUNT").toString()));
                tempJSON.put("MDM_GEOFENCE_UNUNSED_COUNT", (Object)((resultJSON.get((Object)"MDM_GEOFENCE_UNUNSED_COUNT") == null) ? Integer.valueOf(0) : resultJSON.get((Object)"MDM_GEOFENCE_UNUNSED_COUNT").toString()));
                tempJSON.put("MDM_GEOFENCE_TRASH_COUNT", (Object)((resultJSON.get((Object)"MDM_GEOFENCE_TRASH_COUNT") == null) ? Integer.valueOf(0) : resultJSON.get((Object)"MDM_GEOFENCE_TRASH_COUNT").toString()));
                this.mdmTrackerProperties.setProperty("MDM_GEOFENCE_COUNT", tempJSON.toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- addGeoFenceCount()    >   Error", e);
        }
    }
}
