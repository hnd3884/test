package com.adventnet.sym.webclient.common.renderer;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class RecentActiveSessionViewController extends MDMEmberSqlViewController
{
    private static String className;
    private static Logger logger;
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        if (variableName.equals("SCRITERIA")) {
            try {
                super.getVariableValue(viewCtx, variableName);
                final HttpServletRequest request = viewCtx.getRequest();
                final Object param = request.getParameter("loginID");
                final String accountID = (param == null) ? "Default" : param.toString();
                Long accountId;
                if (accountID.equalsIgnoreCase("Default")) {
                    accountId = DMOnPremiseUserUtil.getAccountID();
                }
                else {
                    accountId = Long.parseLong(accountID);
                }
                if (accountId != null) {
                    Criteria AccountIdCriteria = new Criteria(Column.getColumn("AaaAccSession", "ACCOUNT_ID"), (Object)accountId, 0);
                    final Criteria statusCriteria = new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"CLOSED", 0);
                    final Criteria sessionIdCriteria = new Criteria(Column.getColumn("AaaAccHttpSession", "SSO_ID"), (Object)"null", 1);
                    String criteria = super.getVariableValue(viewCtx, variableName);
                    AccountIdCriteria = AccountIdCriteria.and(statusCriteria.or(sessionIdCriteria));
                    criteria = criteria + " and " + AccountIdCriteria;
                    RecentActiveSessionViewController.logger.log(Level.FINE, "{0}Criteria  ---- {1}", new Object[] { RecentActiveSessionViewController.className, criteria });
                    return criteria;
                }
            }
            catch (final Exception ex) {
                RecentActiveSessionViewController.logger.log(Level.SEVERE, "{0}:setCritetia:: Exception while setting Recent Active Session Criteria.{1}", new Object[] { RecentActiveSessionViewController.className, ex });
            }
        }
        return super.getVariableValue(viewCtx, variableName);
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final String sQuery = super.getSQLString(viewCtx);
        RecentActiveSessionViewController.logger.log(Level.FINE, "{0}:getSQLString::Query  ---- {1}", new Object[] { RecentActiveSessionViewController.className, sQuery });
        return sQuery;
    }
    
    static {
        RecentActiveSessionViewController.className = RecentActiveSessionViewController.class.getName();
        RecentActiveSessionViewController.logger = Logger.getLogger("UserManagementLogger");
    }
}
