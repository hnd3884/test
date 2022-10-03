package com.me.devicemanagement.onpremise.webclient.common;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMViewRetrieverAction;

public class EmberDomainsListVRAction extends DMViewRetrieverAction
{
    private static String className;
    private static Logger out;
    
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        EmberDomainsListVRAction.out.log(Level.FINE, "Entered into AffectedSystemsTRAction.setCriteria()");
        ApiFactoryProvider.getPatchDBAPI().setDomainExceptionListViewCriteria(selectQuery, viewCtx);
        super.setCriteria(selectQuery, viewCtx);
        EmberDomainsListVRAction.out.log(Level.FINE, "Finished DomainsListTRAction.setCriteria()");
    }
    
    static {
        EmberDomainsListVRAction.className = DomainsListTRAction.class.getName();
        EmberDomainsListVRAction.out = Logger.getLogger(EmberDomainsListVRAction.className);
    }
}
