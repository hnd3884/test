package com.adventnet.client.components.table.web;

import com.me.devicemanagement.framework.webclient.export.ExportPiiValueHandler;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.sql.SQLProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;

public class DCSqlViewController extends SqlViewController
{
    private Logger logger;
    
    public DCSqlViewController() {
        this.logger = Logger.getLogger("ScheduleReportLogger");
    }
    
    public void updateViewModel(final ViewContext context) throws Exception {
        super.updateViewModel(context);
        context.setTitle(ProductUrlLoader.getInstance().getValue("title"));
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
        this.logger.log(Level.INFO, "DCSqlViewController:getVariableValue is {0}", crit);
        crit = DMSqlViewRetriever.getVariableValue(viewCtx, variableName, crit);
        return crit;
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return ExportPiiValueHandler.getMaskedValueMap(vc);
    }
}
