package com.me.mdm.webclient.schedulereport;

import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class ScheduleReportTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private static Logger logger;
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        final String viewID = viewCtx.getUniqueId();
        if (viewID.equalsIgnoreCase("ScheduledReportTasks")) {
            final DMWebClientCommonUtil webClientUtil = new DMWebClientCommonUtil();
            webClientUtil.setNextExecTimeinViewContext(viewCtx);
        }
        return super.processPreRendering(viewCtx, request, response, viewUrl);
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            Criteria baseCriteria = null;
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (isMSP) {
                final String isClientCall = CustomerInfoThreadLocal.getIsClientCall();
                if (isClientCall != null) {
                    final Long[] customers = CustomerInfoUtil.getInstance().getCustomers();
                    baseCriteria = selectQuery.getCriteria();
                    if (customers != null && customers.length > 0 && (customers.length != 1 || customers[0] != -1L)) {
                        Criteria customerIDCriteria = null;
                        if (customers.length == 1) {
                            customerIDCriteria = new Criteria(Column.getColumn("ScheduleRepTask", "CUSTOMER_ID"), (Object)customers[0], 0);
                        }
                        else {
                            customerIDCriteria = new Criteria(Column.getColumn("ScheduleRepTask", "CUSTOMER_ID"), (Object)customers, 8);
                        }
                        if (baseCriteria != null) {
                            baseCriteria = baseCriteria.and(customerIDCriteria);
                        }
                        else {
                            baseCriteria = customerIDCriteria;
                        }
                    }
                }
            }
            final String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final Long loginID = SYMClientUtil.getLoginId(request);
            if (loginID != null && !SYMClientUtil.isUserInAdminRole(request)) {
                final Criteria criteria = new Criteria(Column.getColumn("TaskDetails", "OWNER"), (Object)loginName, 0);
                if (baseCriteria != null) {
                    baseCriteria = baseCriteria.and(criteria);
                }
                else {
                    baseCriteria = criteria;
                }
            }
            selectQuery.setCriteria(baseCriteria);
        }
        catch (final Exception ex) {
            ScheduleReportTableRetrieverAction.logger.log(Level.INFO, "Exceprion in schedule retriver action for schedule report", ex);
        }
        super.setCriteria(selectQuery, viewCtx);
    }
    
    static {
        ScheduleReportTableRetrieverAction.logger = Logger.getLogger(ScheduleReportTableRetrieverAction.class.getName());
    }
}
