package com.me.mdm.server.metracker;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MEMDMTrackParamsPersistTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties props) {
        MEMDMTrackParamsPersistTask.logger.log(Level.INFO, "MEMDMTrackParamsPersistTask starts..");
        try {
            final String trackParamsString = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("ME_MDM_TRACK_PARAMS", 2);
            this.clearTrackingParamsMap();
            if (trackParamsString != null) {
                final JSONObject trackParams = new JSONObject(trackParamsString);
                final DataObject dO = MDMUtil.getPersistence().get("MEMDMTrackParams", (Criteria)null);
                final Iterator customerItr = trackParams.keys();
                while (customerItr.hasNext()) {
                    final Long customerId = Long.parseLong(customerItr.next());
                    final JSONObject customerParams = trackParams.optJSONObject(customerId.toString());
                    final Iterator paramTypeItr = customerParams.keys();
                    while (paramTypeItr.hasNext()) {
                        final String paramType = paramTypeItr.next();
                        final JSONObject typeParams = customerParams.optJSONObject(paramType);
                        final Iterator modulesItr = typeParams.keys();
                        while (modulesItr.hasNext()) {
                            final String moduleName = modulesItr.next();
                            final JSONObject moduleParams = typeParams.optJSONObject(moduleName);
                            final Iterator paramsItr = moduleParams.keys();
                            while (paramsItr.hasNext()) {
                                final String paramName = paramsItr.next();
                                final String paramValue = moduleParams.optString(paramName);
                                Row row = this.getTrackParamRowFromDO(dO, moduleName, paramName, customerId);
                                if (row == null) {
                                    row = new Row("MEMDMTrackParams");
                                    row.set("MODULE_NAME", (Object)moduleName);
                                    row.set("PARAM_NAME", (Object)paramName);
                                    row.set("PARAM_VALUE", (Object)paramValue);
                                    row.set("CUSTOMER_ID", (Object)customerId);
                                    dO.addRow(row);
                                }
                                else {
                                    if (paramType.equals("INCREMENT_PARAM")) {
                                        final Integer count = Integer.parseInt(row.get("PARAM_VALUE").toString()) + Integer.parseInt(paramValue);
                                        row.set("PARAM_VALUE", (Object)count.toString());
                                    }
                                    else {
                                        row.set("PARAM_VALUE", (Object)paramValue);
                                    }
                                    dO.updateRow(row);
                                }
                            }
                        }
                    }
                }
                MDMUtil.getPersistence().update(dO);
            }
        }
        catch (final Exception e) {
            MEMDMTrackParamsPersistTask.logger.log(Level.SEVERE, "Exception persisting MEMDM tracking params", e);
        }
    }
    
    private void clearTrackingParamsMap() {
        ApiFactoryProvider.getCacheAccessAPI().putCache("ME_MDM_TRACK_PARAMS", (Object)null, 2);
    }
    
    private Row getTrackParamRowFromDO(final DataObject dO, final String moduleName, final String paramName, final Long customerId) throws DataAccessException {
        final Criteria moduleCriteria = new Criteria(Column.getColumn("MEMDMTrackParams", "MODULE_NAME"), (Object)moduleName, 0);
        final Criteria paramCriteria = new Criteria(Column.getColumn("MEMDMTrackParams", "PARAM_NAME"), (Object)paramName, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MEMDMTrackParams", "CUSTOMER_ID"), (Object)customerId, 0);
        return dO.getRow("MEMDMTrackParams", moduleCriteria.and(paramCriteria).and(customerCriteria));
    }
    
    static {
        MEMDMTrackParamsPersistTask.logger = Logger.getLogger(MEMDMTrackParamsPersistTask.class.getName());
    }
}
