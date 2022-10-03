package com.me.mdm.webclient.directory.oauth;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class OauthDetailsRetreiverAction extends MDMEmberTableRetrieverAction
{
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
                final long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final long customerID = MSPWebClientUtil.getCustomerID(request);
                query.setCriteria(new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0).and(new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 0)));
            }
            query.addSortColumn(new SortColumn(Column.getColumn("OauthMetadata", "OAUTH_METADATA_ID"), true));
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, null, e);
        }
    }
}
