package com.adventnet.client.components.table.web;

import com.me.devicemanagement.framework.webclient.export.ExportPiiValueHandler;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.CSRTableController;

public class DMViewRetrieverAction extends CSRTableController
{
    private Logger logger;
    
    public DMViewRetrieverAction() {
        this.logger = Logger.getLogger(DMViewRetrieverAction.class.getName());
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
    
    public void setCriteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        selectQuery = DMViewRetriver.criteria(selectQuery, viewCtx);
        super.setCriteria(selectQuery, viewCtx);
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) {
        try {
            return ExportPiiValueHandler.getMaskedValueMap(vc);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewContext) throws Exception {
        return (SelectQuery)super.fetchAndCacheSelectQuery(viewContext).clone();
    }
}
