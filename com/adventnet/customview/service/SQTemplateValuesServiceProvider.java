package com.adventnet.customview.service;

import com.adventnet.customview.CustomViewException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Map;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.logging.Level;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import java.util.logging.Logger;
import com.adventnet.customview.CustomViewManagerContext;

public class SQTemplateValuesServiceProvider implements ServiceProvider
{
    private ServiceProvider nextSP;
    private CustomViewManagerContext customViewManagerContext;
    private static final Logger out;
    
    public SQTemplateValuesServiceProvider() {
        this.nextSP = null;
        this.customViewManagerContext = null;
    }
    
    @Override
    public void cleanup() {
        this.nextSP = null;
        this.customViewManagerContext = null;
    }
    
    @Override
    public String getServiceName() {
        return "SQTemplateValues";
    }
    
    @Override
    public ViewData process(final CustomViewRequest customViewRequest) throws CustomViewException {
        final SQTemplateValuesServiceConfiguration serviceConfiguration = (SQTemplateValuesServiceConfiguration)customViewRequest.getServiceConfiguration("SQTemplateValues");
        final SelectQuery sq = customViewRequest.getSelectQuery();
        HashMap hashMap = null;
        DataObject dataObject = null;
        Object handler = null;
        Object handlerContext = null;
        SQTemplateValuesServiceProvider.out.log(Level.FINEST, "CustomViewRequest Template SelectQuery :: {0}", sq);
        if (serviceConfiguration != null) {
            try {
                SQTemplateValuesServiceProvider.out.log(Level.FINEST, "SQTemplateValues ServiceConfiguration found");
                Criteria replacedCriteria = null;
                hashMap = serviceConfiguration.getValuesFromHashMap();
                handler = serviceConfiguration.getHandler();
                handlerContext = serviceConfiguration.getHandlerContext();
                final Criteria sqCriteria = sq.getCriteria();
                if (hashMap != null) {
                    SQTemplateValuesServiceProvider.out.log(Level.FINEST, "hashMap    :: {0}", hashMap);
                    if (sqCriteria != null) {
                        replacedCriteria = QueryUtil.getTemplateReplacedCriteria(sqCriteria, (Map)hashMap);
                    }
                }
                else if (handler != null && sqCriteria != null) {
                    replacedCriteria = QueryUtil.getTemplateReplacedCriteria(sqCriteria, handler, handlerContext);
                }
                else {
                    dataObject = serviceConfiguration.getValuesFromDO();
                    SQTemplateValuesServiceProvider.out.log(Level.FINEST, "DataObject :: {0}", dataObject);
                    if (sqCriteria != null) {
                        replacedCriteria = QueryUtil.getTemplateReplacedCriteria(sqCriteria, dataObject);
                    }
                }
                SQTemplateValuesServiceProvider.out.log(Level.FINEST, "Replaced Criteria :: {0}", replacedCriteria);
                sq.setCriteria(replacedCriteria);
            }
            catch (final QueryConstructionException qce) {
                throw new IllegalArgumentException("Exception occured while transforming the given Map values " + hashMap + " and DataObject " + dataObject + " to criteria");
            }
        }
        final ViewData viewDataToReturn = this.nextSP.process(customViewRequest);
        return viewDataToReturn;
    }
    
    @Override
    public void setCustomViewManagerContext(final CustomViewManagerContext customViewManagerContext) {
        this.customViewManagerContext = customViewManagerContext;
    }
    
    @Override
    public void setNextServiceProvider(final ServiceProvider sp) {
        this.nextSP = sp;
    }
    
    static {
        out = Logger.getLogger(SQTemplateValuesServiceProvider.class.getName());
    }
}
