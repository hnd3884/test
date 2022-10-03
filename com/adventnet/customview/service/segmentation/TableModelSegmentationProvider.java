package com.adventnet.customview.service.segmentation;

import com.adventnet.customview.CustomViewException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.customview.RequestContext;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.customview.CustomViewManagerContext;
import com.adventnet.customview.service.ServiceProvider;

public class TableModelSegmentationProvider implements ServiceProvider
{
    private String serviceName;
    ServiceProvider next;
    CustomViewManagerContext customViewManagerContext;
    private static Logger logger;
    List requestContexts;
    
    public TableModelSegmentationProvider() {
        this.serviceName = "TableModelSegmentationProvider";
        this.next = null;
        this.customViewManagerContext = null;
        this.requestContexts = new ArrayList();
        TableModelSegmentationProvider.logger.log(Level.FINEST, "Inside constructor of {0}", this);
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        final RequestContext requestContext = customViewRequest.getRequestContext();
        if (this.requestContexts.contains(requestContext)) {
            TableModelSegmentationProvider.logger.log(Level.FINER, "Old {0} in Customviewrequest, not adding segmentation conditions.", requestContext);
        }
        else {
            TableModelSegmentationProvider.logger.log(Level.FINER, "New {0} in CustomViewRequest, adding segmentation conditions.", requestContext);
            this.requestContexts.add(requestContext);
            SelectQuery cvQuery = customViewRequest.getSelectQuery();
            cvQuery = SegmentationUtil.segmentSelectQuery(cvQuery);
            customViewRequest.setSelectQuery(cvQuery);
            TableModelSegmentationProvider.logger.log(Level.FINEST, "cvsConf.getSelectQuery = {0}", customViewRequest.getSelectQuery());
        }
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
        this.requestContexts = null;
    }
    
    static {
        TableModelSegmentationProvider.logger = Logger.getLogger(TableModelSegmentationProvider.class.getName());
    }
}
