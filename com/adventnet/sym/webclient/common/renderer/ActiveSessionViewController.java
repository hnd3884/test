package com.adventnet.sym.webclient.common.renderer;

import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DCTableRetrieverAction;

public class ActiveSessionViewController extends DCTableRetrieverAction
{
    private static String className;
    private static Logger logger;
    
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final Long accountId = DMOnPremiseUserUtil.getAccountID();
            if (accountId != null) {
                final Criteria AccountIdCriteria = new Criteria(Column.getColumn("AaaAccSession", "ACCOUNT_ID"), (Object)accountId, 0);
                final Criteria statusCriteria = new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"ACTIVE", 0);
                selectQuery.setCriteria(AccountIdCriteria.and(statusCriteria));
            }
        }
        catch (final Exception e) {
            ActiveSessionViewController.logger.log(Level.SEVERE, e, () -> ActiveSessionViewController.className + ":setCritetia:: Exception while setting Active Session Criteria.");
        }
        super.setCriteria(selectQuery, viewCtx);
    }
    
    static {
        ActiveSessionViewController.className = ActiveSessionViewController.class.getName();
        ActiveSessionViewController.logger = Logger.getLogger("UserManagementLogger");
    }
}
