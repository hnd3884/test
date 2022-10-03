package com.adventnet.client.components.table.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;

public class DCViewRetrieverAction extends DMViewRetrieverAction
{
    @Override
    public void setCriteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        selectQuery = this.getRBCAQuery(selectQuery, viewCtx);
        super.setCriteria(selectQuery, viewCtx);
    }
    
    @Override
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        super.updateViewModel(viewCtx);
    }
    
    public SelectQuery getRBCAQuery(SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            if (CustomerInfoUtil.isDC() || CustomerInfoUtil.isPMP()) {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    selectQuery = WebclientAPIFactoryProvider.getRBCAQuery().getQuery(selectQuery, viewCtx.getRequest());
                }
            }
            return selectQuery;
        }
        catch (final Exception e) {
            Logger.getLogger(DCViewRetrieverAction.class.getName()).log(Level.SEVERE, "Exception occurred : " + e);
            return null;
        }
    }
}
