package com.adventnet.client.components.table.web;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;

public class DMSqlViewRetriever
{
    private static Logger logger;
    
    public static void setStateParams(final ViewContext viewCtx) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        final String scheduleId = String.valueOf(request.getParameter("scheduleID"));
        final ReportCriteriaUtil reportUtil = ReportCriteriaUtil.getInstance();
        String viewId = request.getParameter("viewId");
        final String isDCViewFilterReset = request.getParameter("isDCViewFilterReset");
        viewId = ((viewId == null && isDCViewFilterReset == null) ? request.getParameter("viewId") : viewId);
        String criteriaJSONString = request.getParameter("criteriaJSON");
        final String dcViewFilterID = request.getParameter("dcViewFilterID");
        criteriaJSONString = ((criteriaJSONString == null && isDCViewFilterReset == null) ? request.getParameter("criteriaJSON") : criteriaJSONString);
        if (criteriaJSONString == null && isDCViewFilterReset == null && dcViewFilterID != null) {
            final JSONObject criteriaJSON = DCViewFilterUtil.getInstance().getCriteriaJSONForFilter(Long.valueOf(dcViewFilterID)).dcViewFilterMapper();
            criteriaJSONString = (criteriaJSON.isNull("criteria") ? null : criteriaJSON.toString());
        }
        if (criteriaJSONString != null && viewId != null && isDCViewFilterReset == null) {
            viewCtx.setStateParameter("viewId", (Object)viewId);
            viewCtx.setStateParameter("criteriaJSON", (Object)criteriaJSONString);
        }
        else if (criteriaJSONString == null && viewId == null) {
            final Long viewID = reportUtil.getViewIdForScheduleReport(scheduleId);
            viewId = ((viewID != null) ? String.valueOf(viewID) : null);
            criteriaJSONString = reportUtil.constructCriteriaJSONForScheduleID(scheduleId);
            if (viewId != null && criteriaJSONString != null) {
                viewCtx.setStateParameter("viewId", (Object)(viewId + ""));
                viewCtx.setStateParameter("criteriaJSON", (Object)criteriaJSONString);
            }
        }
    }
    
    public static String getVariableValue(final ViewContext viewCtx, final String variableName, String crit) {
        final HttpServletRequest request = viewCtx.getRequest();
        final String viewFilterCriteria = DCViewFilterUtil.getInstance().checkAndAddVariableValueForDCViewFilter(viewCtx, variableName);
        if (viewFilterCriteria != null) {
            if (crit != null) {
                crit += " AND";
                crit = crit + " " + viewFilterCriteria;
            }
            else {
                crit = viewFilterCriteria;
            }
        }
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            crit = checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        return crit;
    }
    
    public static String checkAndGetDBRangeCriteriaAssociatedValue(String variableName) {
        variableName = variableName.replaceAll(" ", "");
        final String dbRangeCriteriaRegex = "(DBRANGECRITERIA-)([\\w]+)(-[\\w]+){0,1}";
        String associatedValue = " 1 = 1 ";
        if (variableName.matches(dbRangeCriteriaRegex)) {
            final String[] values = variableName.split("-");
            final String tableName = values[1];
            String tableAlais = null;
            if (values.length > 2) {
                tableAlais = values[2];
            }
            try {
                associatedValue = ApiFactoryProvider.getUtilAccessAPI().dbRangeCriteriaReplaceString(tableName, tableAlais);
            }
            catch (final Exception e) {
                DMSqlViewRetriever.logger.log(Level.SEVERE, null, e);
            }
        }
        return associatedValue;
    }
    
    static {
        DMSqlViewRetriever.logger = Logger.getLogger("DMSqlViewRetriever");
    }
}
