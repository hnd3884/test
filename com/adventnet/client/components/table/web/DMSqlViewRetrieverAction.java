package com.adventnet.client.components.table.web;

import com.me.devicemanagement.framework.webclient.export.ExportPiiValueHandler;
import com.me.devicemanagement.framework.server.sql.SQLProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.CSRSqlTableController;

public class DMSqlViewRetrieverAction extends CSRSqlTableController
{
    private Logger logger;
    
    public DMSqlViewRetrieverAction() {
        this.logger = Logger.getLogger("ScheduleReportLogger");
    }
    
    public void updateViewModel(final ViewContext context) throws Exception {
        Connection connection = null;
        try {
            final boolean runonslave = Boolean.parseBoolean("" + FrameworkConfigurations.getSpecificPropertyIfExists("run_scheduler_export_on_slave", "enable", (Object)"false"));
            if (runonslave) {
                final HttpServletRequest request = context.getRequest();
                boolean isSchedulerReport = false;
                if (request != null) {
                    isSchedulerReport = Boolean.parseBoolean(request.getParameter("reportInitByScheduledReport"));
                }
                if (context.isExportType() && isSchedulerReport) {
                    this.logger.log(Level.INFO, "Scheduler Export - Mickey to be exported using slave DB");
                    connection = SyMUtil.getReadOnlyConnection();
                    context.setTransientState("CONNECTION", (Object)connection);
                }
            }
            super.updateViewModel(context);
            context.setTitle(ProductUrlLoader.getInstance().getValue("title"));
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Exception while closing connection : ", e);
                }
            }
        }
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        final int sqlId = ReportCriteriaUtil.getInstance().getNativeSqlId(request.getParameter("toolID"));
        DMSqlViewRetriever.setStateParams(viewCtx);
        String query;
        if (sqlId == 0) {
            query = super.getSQLString(viewCtx);
        }
        else {
            final Properties variableProps = new Properties();
            query = SQLProvider.getInstance().getSQLStringFromDB(sqlId, variableProps);
        }
        return query;
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String crit = super.getVariableValue(viewCtx, variableName);
        this.logger.log(Level.INFO, "DMSqlViewController:getVariableValue is {0}", crit);
        crit = DMSqlViewRetriever.getVariableValue(viewCtx, variableName, crit);
        return crit;
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return ExportPiiValueHandler.getMaskedValueMap(vc);
    }
}
