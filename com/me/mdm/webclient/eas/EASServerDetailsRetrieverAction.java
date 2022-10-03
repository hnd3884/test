package com.me.mdm.webclient.eas;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.easmanagement.EASMgmt;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class EASServerDetailsRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public EASServerDetailsRetrieverAction() {
        this.logger = Logger.getLogger("EASMgmtLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
            final Long serverID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
            final Criteria queryCriteria = new Criteria(Column.getColumn("EASServerDetails", "EAS_SERVER_ID"), (Object)serverID, 0);
            selectQuery.setCriteria(queryCriteria);
            final JSONObject CEADetailsRequest = new JSONObject();
            CEADetailsRequest.put((Object)"EASServerDetails", (Object)String.valueOf(Boolean.TRUE));
            final JSONObject CEAdetails = EASMgmt.getInstance().getCEAdetails(CEADetailsRequest);
            super.setCriteria(selectQuery, viewCtx);
            request.setAttribute("CEAdetails", (Object)CEAdetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in EASServerDetailsRetrieverAction...", e);
        }
    }
}
