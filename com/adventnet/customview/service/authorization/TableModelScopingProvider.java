package com.adventnet.customview.service.authorization;

import com.adventnet.customview.RequestContext;
import com.adventnet.customview.CustomViewException;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.authorization.AuthorizationEngine;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.customview.CustomViewManagerContext;
import com.adventnet.customview.service.ServiceProvider;

public class TableModelScopingProvider implements ServiceProvider
{
    private String serviceName;
    ServiceProvider next;
    CustomViewManagerContext customViewManagerContext;
    private static Logger logger;
    
    public TableModelScopingProvider() {
        this.serviceName = "TableModelScopingProvider";
        this.next = null;
        this.customViewManagerContext = null;
        TableModelScopingProvider.logger.log(Level.FINEST, "Inside constructor of {0}", this);
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    protected void scopeSelectQuery(final SelectQuery sq) throws Exception {
        AuthorizationEngine.scopeSelectQuery(sq);
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        final RequestContext requestContext = customViewRequest.getRequestContext();
        TableModelScopingProvider.logger.log(Level.FINER, "New {0} in CustomViewRequest, adding scoping conditions.", requestContext);
        final SelectQuery cvQuery = customViewRequest.getSelectQuery();
        try {
            this.scopeSelectQuery(cvQuery);
        }
        catch (final Exception e) {
            throw new CustomViewException(e);
        }
        customViewRequest.setSelectQuery(cvQuery);
        TableModelScopingProvider.logger.log(Level.FINEST, "customViewRequest.getSelectQuery = {0}", customViewRequest.getSelectQuery());
        return this.next.process(customViewRequest);
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider serviceProvider) {
        this.next = serviceProvider;
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
        this.customViewManagerContext = customViewManagerContext;
    }
    
    @Override
    public void cleanup() {
        this.customViewManagerContext = null;
        this.next = null;
    }
    
    static {
        TableModelScopingProvider.logger = Logger.getLogger(TableModelScopingProvider.class.getName());
    }
}
