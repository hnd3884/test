package com.adventnet.client.components.table.web;

import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.CSRTableController;

public class DCEmberTableController extends CSRTableController
{
    public void updateViewModel(final ViewContext context) throws Exception {
        super.updateViewModel(context);
        context.setTitle(ProductUrlLoader.getInstance().getValue("title"));
    }
    
    public void setCriteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        selectQuery = DCViewFilterUtil.getInstance().checkAndAddDCViewFilterCriteria(selectQuery, viewCtx);
        final String isScheduleReport = String.valueOf(request.getParameter("isScheduledReport"));
        final String scheduleId = String.valueOf(request.getParameter("scheduleID"));
        super.setCriteria(ReportCriteriaUtil.getInstance().appendCriteriaToSelectQuery(isScheduleReport, scheduleId, selectQuery), viewCtx);
    }
}
