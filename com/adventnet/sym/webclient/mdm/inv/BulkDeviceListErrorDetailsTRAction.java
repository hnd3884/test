package com.adventnet.sym.webclient.mdm.inv;

import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class BulkDeviceListErrorDetailsTRAction extends MDMTableRetrieverAction
{
    private static Logger logger;
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            BulkDeviceListErrorDetailsTRAction.logger.log(Level.SEVERE, "Exception while setting criteria: {0}", ex);
        }
    }
    
    static {
        BulkDeviceListErrorDetailsTRAction.logger = Logger.getLogger("MDMLogger");
    }
}
