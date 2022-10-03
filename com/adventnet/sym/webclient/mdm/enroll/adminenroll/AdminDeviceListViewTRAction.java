package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.components.table.web.DMViewRetrieverAction;

public class AdminDeviceListViewTRAction extends DMViewRetrieverAction
{
    public void modifyToRBDAQuery(final SelectQuery selectQuery) {
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID != null) {
                final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin) {
                    Criteria criteria = selectQuery.getCriteria();
                    if (criteria == null) {
                        criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
                    }
                    else {
                        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0));
                    }
                    selectQuery.setCriteria(criteria);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(AdminDeviceListViewTRAction.class.getName()).log(Level.SEVERE, "{0}", ex);
        }
    }
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        this.modifyToRBDAQuery(query);
        super.setCriteria(query, viewCtx);
    }
}
