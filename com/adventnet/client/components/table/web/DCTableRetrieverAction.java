package com.adventnet.client.components.table.web;

import com.me.devicemanagement.framework.webclient.export.ExportPiiValueHandler;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.client.view.web.ViewContext;

public class DCTableRetrieverAction extends TableRetrieverAction
{
    public void updateViewModel(final ViewContext context) throws Exception {
        super.updateViewModel(context);
        context.setTitle(ProductUrlLoader.getInstance().getValue("title"));
    }
    
    public void setCriteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        selectQuery = DMViewRetriver.criteria(selectQuery, viewCtx);
        super.setCriteria(selectQuery, viewCtx);
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return ExportPiiValueHandler.getMaskedValueMap(vc);
    }
    
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewContext) throws Exception {
        return (SelectQuery)super.fetchAndCacheSelectQuery(viewContext).clone();
    }
}
