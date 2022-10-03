package com.adventnet.customview.service;

import com.adventnet.persistence.DataObject;
import java.util.HashMap;

public class SQTemplateValuesServiceConfiguration implements ServiceConfiguration
{
    private String serviceName;
    private HashMap hashMapValuesForSQTemplates;
    private DataObject dataObjectValuesForSQTemplates;
    private Object handler;
    private Object handlerContext;
    
    public SQTemplateValuesServiceConfiguration(final HashMap hashMap) {
        this.serviceName = "SQTemplateValues";
        this.hashMapValuesForSQTemplates = null;
        this.dataObjectValuesForSQTemplates = null;
        this.handler = null;
        this.handlerContext = null;
        this.hashMapValuesForSQTemplates = hashMap;
    }
    
    public SQTemplateValuesServiceConfiguration(final DataObject dataObject) {
        this.serviceName = "SQTemplateValues";
        this.hashMapValuesForSQTemplates = null;
        this.dataObjectValuesForSQTemplates = null;
        this.handler = null;
        this.handlerContext = null;
        this.dataObjectValuesForSQTemplates = dataObject;
    }
    
    public SQTemplateValuesServiceConfiguration(final Object handler, final Object handlerContext) {
        this.serviceName = "SQTemplateValues";
        this.hashMapValuesForSQTemplates = null;
        this.dataObjectValuesForSQTemplates = null;
        this.handler = null;
        this.handlerContext = null;
        this.handler = handler;
        this.handlerContext = handlerContext;
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    public HashMap getValuesFromHashMap() {
        return this.hashMapValuesForSQTemplates;
    }
    
    public DataObject getValuesFromDO() {
        return this.dataObjectValuesForSQTemplates;
    }
    
    public Object getHandler() {
        return this.handler;
    }
    
    public Object getHandlerContext() {
        return this.handlerContext;
    }
}
