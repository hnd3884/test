package com.me.mdm.onpremise.server.integration;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.onpremise.server.integration.jira.MDMJiraIntegrationUtil;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import java.util.logging.Logger;

public class MDMIntegrationUtil
{
    public Logger INTEGLOGGER;
    private static MDMIntegrationUtil integUtil;
    
    public MDMIntegrationUtil() {
        this.INTEGLOGGER = Logger.getLogger("MDMIntegrationLog");
    }
    
    public static MDMIntegrationUtil getInstance() {
        if (MDMIntegrationUtil.integUtil == null) {
            MDMIntegrationUtil.integUtil = new MDMIntegrationUtil();
        }
        return MDMIntegrationUtil.integUtil;
    }
    
    public void handleIntegrationMETrack(final String trackingCode, final String reqURI, final String queryString) {
        if (trackingCode.equalsIgnoreCase("sdp")) {
            MDMSDPIntegrationUtil.getInstance().handleSDPUIMETrack(reqURI, queryString);
        }
        if (trackingCode.equalsIgnoreCase("jira")) {
            MDMJiraIntegrationUtil.getInstance().handleJiraMETrack(reqURI, queryString);
        }
    }
    
    public String getIntegrationParamValue(final String paramName) {
        String integParamVal = null;
        try {
            integParamVal = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(paramName);
            if (integParamVal == null) {
                final Criteria crit = new Criteria(Column.getColumn("IntegrationParams", "PARAM_NAME"), (Object)paramName, 0);
                final DataObject formatDO = MDMUtil.getPersistence().get("IntegrationParams", crit);
                final Row formatRow = formatDO.getRow("IntegrationParams");
                if (formatRow != null) {
                    integParamVal = (String)formatRow.get("PARAM_VALUE");
                }
                final ArrayList tableNames = new ArrayList();
                tableNames.add("IntegrationParams");
                ApiFactoryProvider.getCacheAccessAPI().putCache(paramName, (Object)integParamVal, (List)tableNames);
            }
        }
        catch (final Exception e) {
            this.INTEGLOGGER.log(Level.SEVERE, "Exception in getting Integration Param Value", e);
        }
        return integParamVal;
    }
    
    public void updateIntegrationParameter(final String paramName, final String paramValue) {
        try {
            final Column col = new Column("IntegrationParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)paramName, 0, (boolean)Boolean.FALSE);
            final DataObject paramDO = MDMUtil.getPersistence().get("IntegrationParams", criteria);
            if (paramDO.isEmpty()) {
                final Row paramRow = new Row("IntegrationParams");
                paramRow.set("PARAM_NAME", (Object)paramName);
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                paramDO.addRow(paramRow);
                DataAccess.add(paramDO);
                this.INTEGLOGGER.log(Level.INFO, "Integration Parameter has been added - Param Name: {0}  Param Value: {1}", new Object[] { paramName, paramValue });
            }
            else {
                final Row paramRow = paramDO.getFirstRow("IntegrationParams");
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                paramDO.updateRow(paramRow);
                DataAccess.update(paramDO);
                this.INTEGLOGGER.log(Level.INFO, "Integration Parameter has been updated - Param Name: {0}  Param Value: {1}", new Object[] { paramName, paramValue });
            }
            final ArrayList tableNames = new ArrayList();
            tableNames.add("IntegrationParams");
            ApiFactoryProvider.getCacheAccessAPI().putCache(paramName, (Object)paramValue, (List)tableNames);
        }
        catch (final Exception ex) {
            this.INTEGLOGGER.log(Level.WARNING, "Exception while updating Integration Parameter:{0} {1}", new Object[] { paramName, ex.getMessage() });
        }
    }
    
    public void incrementIntegCount(final String integParam) {
        try {
            String integCount = this.getIntegrationParamValue(integParam);
            if (integCount == null) {
                integCount = "0";
            }
            int pageViewCount = Integer.parseInt(integCount);
            MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter(integParam, String.valueOf(++pageViewCount));
        }
        catch (final Exception ex) {
            this.INTEGLOGGER.log(Level.SEVERE, "Exception in adding or incrementing integration value", ex);
        }
    }
    
    static {
        MDMIntegrationUtil.integUtil = null;
    }
}
