package com.me.devicemanagement.onpremise.webclient.common;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DCTableRetrieverAction;

@Deprecated
public class DomainsListTRAction extends DCTableRetrieverAction
{
    private static String className;
    private static Logger out;
    
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        DomainsListTRAction.out.log(Level.FINE, "Entered into AffectedSystemsTRAction.setCriteria()");
        ApiFactoryProvider.getPatchDBAPI().setDomainExceptionListViewCriteria(selectQuery, viewCtx);
        super.setCriteria(selectQuery, viewCtx);
        DomainsListTRAction.out.log(Level.FINE, "Finished DomainsListTRAction.setCriteria()");
    }
    
    static {
        DomainsListTRAction.className = DomainsListTRAction.class.getName();
        DomainsListTRAction.out = Logger.getLogger(DomainsListTRAction.className);
    }
}
