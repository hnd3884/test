package com.me.mdm.webclient.audit;

import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.metracker.METrackParamManager;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.audit.EventViewerTRAction;

public class MDMEventViewerTRAction extends EventViewerTRAction
{
    Logger logger;
    
    public MDMEventViewerTRAction() {
        this.logger = Logger.getLogger(MDMEventViewerTRAction.class.getName());
    }
    
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final String sourceMethod = "MDMEventViewerTRAction::setCriteria";
            final HttpServletRequest request = viewCtx.getRequest();
            final String eventModule = request.getParameter("eventModule");
            final String user = request.getParameter("user");
            final String days = request.getParameter("days");
            final String startDate = request.getParameter("startDate");
            final String endDate = request.getParameter("endDate");
            this.logger.log(Level.INFO, "{0} -->  eventModule : {1} , user : {2} , days : {3} , startDate : {4} , endDate : {5}", new Object[] { sourceMethod, eventModule, user, days, startDate, endDate });
            Criteria criteria = null;
            final String isSummaryPage = CustomerInfoThreadLocal.getSummaryPage();
            if (isSummaryPage != null && isSummaryPage.equals("true")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            }
            final String tab = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "selectedTab");
            if (tab != null && tab.equalsIgnoreCase("MDM")) {
                if (eventModule != null && !eventModule.equals("") && !eventModule.equals("all")) {
                    final String[] module = eventModule.split(";");
                    criteria = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)tab, 0);
                    final Criteria cri2 = new Criteria(Column.getColumn("EventCode", "SUB_MODULE"), (Object)module, 8);
                    criteria = criteria.and(cri2);
                    selectQuery.setCriteria(criteria);
                }
                else {
                    criteria = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)tab, 0);
                    selectQuery.setCriteria(criteria);
                }
            }
            else if (DMApplicationHandler.isMdmProduct() && eventModule != null && !eventModule.equals("") && !eventModule.equals("all")) {
                final String[] module = eventModule.split(";");
                criteria = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)module, 8);
                final Criteria cri2 = new Criteria(Column.getColumn("EventCode", "SUB_MODULE"), (Object)module, 8);
                criteria = criteria.or(cri2);
                selectQuery.setCriteria(criteria);
            }
            super.setCriteria(selectQuery, viewCtx);
            final String sqlQuery = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            this.logger.log(Level.INFO, "{0}--> Unique id :{1} \n selectQuery : {2}", new Object[] { sourceMethod, viewCtx.getUniqueId(), sqlQuery });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while setting the criteria on user reports due to : ", ex);
        }
        METrackParamManager.incrementMETrackParams("ActionLogClickUsageCount");
    }
}
