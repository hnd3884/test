package com.adventnet.customview.service.i18n;

import com.adventnet.customview.CustomViewManagerContext;
import com.adventnet.customview.CustomViewException;
import java.util.Map;
import com.adventnet.model.Model;
import com.adventnet.customview.RequestContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.model.table.CVTableModelImpl;
import com.adventnet.customview.CVUtil;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.customview.service.ServiceProvider;

public class I18nServiceProvider implements ServiceProvider
{
    private String serviceName;
    protected ServiceProvider sp;
    private static Logger logger;
    List requestContexts;
    
    public I18nServiceProvider() {
        this.serviceName = "DS_I18N_SERVICE";
        this.sp = null;
        this.requestContexts = new ArrayList();
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider sp) {
        this.sp = sp;
    }
    
    @Override
    public ViewData process(final CustomViewRequest request) throws CustomViewException {
        I18nServiceProvider.logger.finer("Inside process of I18nServiceProvider");
        final I18nServiceConfiguration i18nService = (I18nServiceConfiguration)request.getServiceConfiguration(this.serviceName);
        if (i18nService == null) {
            return this.sp.process(request);
        }
        SelectQuery firstSelect = request.getSelectQuery();
        if (firstSelect == null) {
            final String cvName = request.getCustomViewConfigurationName();
            request.setCustomViewConfiguration(CVUtil.getDOForCVName(cvName));
            firstSelect = request.getSelectQuery();
        }
        final I18nHandler handler = new I18nHandler(i18nService);
        final RequestContext requestContext = request.getRequestContext();
        handler.setDSSortOrder(firstSelect);
        request.setSelectQuery(firstSelect);
        final ViewData viewData = this.sp.process(request);
        final Model model = viewData.getModel();
        if (model instanceof CVTableModelImpl) {
            I18nServiceProvider.logger.finer("I18nServiceProvider : model is an instance of CVTableModelImpl");
            final CVTableModelImpl tableModel = (CVTableModelImpl)model;
            final Map sortOrders = handler.getSortOrders(tableModel.getColumns());
            I18nServiceProvider.logger.log(Level.FINER, "I18nServiceProvider : sortOrders : {0}", sortOrders);
            tableModel.setSortOrders(sortOrders);
        }
        return viewData;
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
    }
    
    @Override
    public void cleanup() {
        this.sp = null;
    }
    
    static {
        I18nServiceProvider.logger = Logger.getLogger(I18nServiceProvider.class.getName());
    }
}
