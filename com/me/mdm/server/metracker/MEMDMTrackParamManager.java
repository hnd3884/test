package com.me.mdm.server.metracker;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.tracker.MDMCoreQuery;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MEMDMTrackParamManager
{
    private static Logger logger;
    private static MEMDMTrackParamManager mdmTrackParamManager;
    public static final String OVERWRITE_PARAM_TYPE = "OVERWRITE_PARAM";
    public static final String INCREMENT_PARAM_TYPE = "INCREMENT_PARAM";
    
    public MEMDMTrackParamManager() {
        MEMDMTrackParamManager.logger.log(Level.INFO, "----------- MEMDMTrackParamManager class object is created -----------");
    }
    
    public static MEMDMTrackParamManager getInstance() {
        if (MEMDMTrackParamManager.mdmTrackParamManager == null) {
            MEMDMTrackParamManager.mdmTrackParamManager = (MEMDMTrackParamManager)ApiFactoryProvider.getImplClassInstance("MDM_METRACKPARAM_CLASS");
        }
        return MEMDMTrackParamManager.mdmTrackParamManager;
    }
    
    public void addOrUpdateTrackParam(final Long customerId, final String module, final String paramName, final String paramValue) {
        try {
            MEMDMTrackParamManager.mdmTrackParamManager.putTrackParamInCache(customerId, module, paramName, paramValue, "OVERWRITE_PARAM");
        }
        catch (final JSONException ex) {
            MEMDMTrackParamManager.logger.log(Level.SEVERE, "Exception when updating tracking params", (Throwable)ex);
        }
    }
    
    public void incrementTrackValue(final Long customerId, final String module, final String paramName) throws JSONException {
        if (!paramName.isEmpty()) {
            MEMDMTrackParamManager.mdmTrackParamManager.putTrackParamInCache(customerId, module, paramName, "", "INCREMENT_PARAM");
        }
    }
    
    public abstract void putTrackParamInCache(final Long p0, final String p1, final String p2, final String p3, final String p4) throws JSONException;
    
    public void trackRecentPageReferer(final Long customerID, final String module, final String pageName, String referrer) {
        referrer = ((referrer == null) ? "NoSource" : referrer);
        this.addOrUpdateTrackParam(customerID, module, pageName + "RecentReferrer", referrer);
    }
    
    public void trackPageRefererCount(final Long customerID, final String module, final String pageName, String referrer) throws JSONException {
        referrer = ((referrer == null) ? "NoSource" : referrer);
        this.incrementTrackValue(customerID, module, pageName + "From" + referrer);
    }
    
    public void trackPageVisit(final Long customerID, final String module, final String pageName, String referrer) throws JSONException {
        referrer = ((referrer == null) ? "NoSource" : referrer);
        this.trackRecentPageReferer(customerID, module, pageName, referrer);
        this.trackPageRefererCount(customerID, module, pageName, referrer);
    }
    
    public String getTrackParamValueFromDB(final Long customerID, final String module, final String paramName, final String defaultValue) throws Exception {
        String value = defaultValue;
        final SelectQuery sQuery = MDMCoreQuery.getInstance().getMDMTrackParamQuery(customerID, module);
        final Criteria paramNameCriteria = new Criteria(Column.getColumn("MEMDMTrackParams", "PARAM_NAME"), (Object)paramName, 0);
        final Criteria existingCriteria = sQuery.getCriteria();
        sQuery.setCriteria(existingCriteria.and(paramNameCriteria));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getFirstRow("MEMDMTrackParams");
            if (row != null) {
                value = ((row.get("PARAM_VALUE") == null) ? null : row.get("PARAM_VALUE").toString());
            }
        }
        return value;
    }
    
    static {
        MEMDMTrackParamManager.logger = Logger.getLogger(MEMDMTrackParamManager.class.getName());
        MEMDMTrackParamManager.mdmTrackParamManager = null;
    }
}
