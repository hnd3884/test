package com.me.mdm.webclient.reports;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class CRTableRetriverAction extends MDMEmberTableRetrieverAction
{
    private static Logger out;
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        CRTableRetriverAction.out.log(Level.FINE, "Entered into CRTableRetriverAction.setCriteria()");
        final HttpServletRequest request = viewCtx.getRequest();
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        final String viewName = viewCtx.getUniqueId();
        final String paramValue = request.getParameter("reportFrom");
        try {
            Criteria customQueryCri = null;
            if (isMSP) {
                final String isClientCall = CustomerInfoThreadLocal.getIsClientCall();
                if (isClientCall != null) {
                    final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
                    boolean flagSet = false;
                    try {
                        if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                            flagSet = true;
                        }
                        final Long[] customers = CustomerInfoUtil.getInstance().getCustomers();
                        if (customers != null && customers.length > 0 && (customers.length != 1 || customers[0] != -1L)) {
                            Criteria customerIDCriteria = null;
                            if (customers.length == 1) {
                                customerIDCriteria = new Criteria(Column.getColumn("CRToCustomerRel", "CUSTOMER_ID"), (Object)customers[0], 0);
                            }
                            else {
                                customerIDCriteria = new Criteria(Column.getColumn("CRToCustomerRel", "CUSTOMER_ID"), (Object)customers, 8);
                            }
                            if (customQueryCri != null) {
                                customQueryCri = customQueryCri.and(customerIDCriteria);
                            }
                            else {
                                customQueryCri = customerIDCriteria;
                            }
                        }
                    }
                    finally {
                        if (flagSet) {
                            CustomerInfoThreadLocal.setSkipCustomerFilter("false");
                        }
                    }
                }
            }
            final String dbName = DBUtil.getActiveDBName();
            int db_type;
            if (dbName.equals("mysql")) {
                db_type = 1;
            }
            else if (dbName.equals("mssql")) {
                db_type = 2;
            }
            else {
                db_type = 3;
            }
            Criteria dbtypeCrit = new Criteria(Column.getColumn("CRSaveViewDetails", "DB_TYPE"), (Object)db_type, 0);
            final Criteria queryCrit = new Criteria(Column.getColumn("CRSaveViewDetails", "VIEWID"), (Object)null, 1);
            final Criteria queryCrit2 = new Criteria(Column.getColumn("CRSaveViewDetails", "DB_TYPE"), (Object)db_type, 1);
            dbtypeCrit = dbtypeCrit.or(queryCrit.and(queryCrit2));
            if (customQueryCri != null) {
                customQueryCri = customQueryCri.and(dbtypeCrit);
            }
            else {
                customQueryCri = dbtypeCrit;
            }
            final String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final Long loginID = SYMClientUtil.getLoginId(request);
            if (loginID != null && !SYMClientUtil.isUserInAdminRole(request)) {
                final Criteria criteria = new Criteria(Column.getColumn("AaaUser", "FIRST_NAME"), (Object)loginName, 0);
                if (customQueryCri != null) {
                    customQueryCri = customQueryCri.and(criteria);
                }
                else {
                    customQueryCri = criteria;
                }
            }
            if (viewName.equalsIgnoreCase("CQListView")) {
                customQueryCri = customQueryCri.and(new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 1));
            }
            selectQuery.setCriteria(customQueryCri);
            CRTableRetriverAction.out.log(Level.INFO, "the custom report view query is:{0}", RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
        }
        catch (final Exception ex) {
            CRTableRetriverAction.out.log(Level.WARNING, "Exception Occured while setting criteria ", ex);
        }
        CRTableRetriverAction.out.log(Level.FINE, "Finished CRTableRetriverAction.setCriteria()");
        super.setCriteria(selectQuery, viewCtx);
    }
    
    static {
        CRTableRetriverAction.out = Logger.getLogger("QueryExecutorLogger");
    }
}
