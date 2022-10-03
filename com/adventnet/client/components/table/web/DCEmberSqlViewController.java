package com.adventnet.client.components.table.web;

import com.me.devicemanagement.framework.webclient.export.ExportPiiValueHandler;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.sql.SQLProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.CSRSqlTableController;

public class DCEmberSqlViewController extends CSRSqlTableController
{
    private Logger logger;
    
    public DCEmberSqlViewController() {
        this.logger = Logger.getLogger("ScheduleReportLogger");
    }
    
    public void updateViewModel(final ViewContext context) throws Exception {
        super.updateViewModel(context);
        context.setTitle(ProductUrlLoader.getInstance().getValue("title"));
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        final int sqlId = ReportCriteriaUtil.getInstance().getNativeSqlId(request.getParameter("toolID"));
        final String isScheduleReport = String.valueOf(request.getParameter("isScheduledReport"));
        final String scheduleId = String.valueOf(request.getParameter("scheduleID"));
        String viewId = request.getParameter("viewId");
        final String isDCViewFilterReset = request.getParameter("isDCViewFilterReset");
        viewId = ((viewId == null && isDCViewFilterReset == null) ? request.getParameter("viewId") : viewId);
        String criteriaJSONString = request.getParameter("criteriaJSON");
        criteriaJSONString = ((criteriaJSONString == null && isDCViewFilterReset == null) ? request.getParameter("criteriaJSON") : criteriaJSONString);
        if (criteriaJSONString != null && viewId != null && isDCViewFilterReset == null) {
            viewCtx.setStateParameter("viewId", (Object)viewId);
            viewCtx.setStateParameter("criteriaJSON", (Object)criteriaJSONString);
        }
        String query;
        if (sqlId == 0) {
            query = super.getSQLString(viewCtx);
        }
        else {
            final Properties variableProps = new Properties();
            query = SQLProvider.getInstance().getSQLStringFromDB(sqlId, variableProps);
        }
        return ReportCriteriaUtil.getInstance().appendCriteriaToNativeQuery(isScheduleReport, scheduleId, query, request.getParameter("toolID"));
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String crit = super.getVariableValue(viewCtx, variableName);
        this.logger.log(Level.INFO, "DCSqlViewController:getVariableValue is {0}", crit);
        final HttpServletRequest request = viewCtx.getRequest();
        if (crit == null) {
            final String isScheduleReport = String.valueOf(request.getParameter("isScheduledReport"));
            final String scheduleId = String.valueOf(request.getParameter("scheduleID"));
            crit = ReportCriteriaUtil.getInstance().getVariableValueForScheduledView(isScheduleReport, scheduleId, request.getParameter("toolID"), variableName);
        }
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
        return crit;
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return ExportPiiValueHandler.getMaskedValueMap(vc);
    }
}
