package com.adventnet.client.view;

import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;

public class ViewModel
{
    protected DataObject viewConfig;
    protected HashMap<Object, Object> compiledDataMap;
    protected String viewName;
    private Long viewNameNo;
    protected DataObject uiComponentConfig;
    
    public ViewModel(final DataObject viewConfigArg) {
        this.compiledDataMap = null;
        this.viewName = null;
        this.viewNameNo = null;
        this.setViewConfiguration(viewConfigArg);
    }
    
    public DataObject getUIComponentConfig() {
        return this.uiComponentConfig;
    }
    
    public void setUIComponentConfig(final DataObject newUiComponentConfig) {
        this.uiComponentConfig = newUiComponentConfig;
    }
    
    public DataObject getViewConfiguration() {
        return this.viewConfig;
    }
    
    public void setViewConfiguration(final DataObject viewConfigArg) {
        this.viewConfig = viewConfigArg;
        try {
            final Long tempNameNo = (Long)this.viewConfig.getFirstValue("ViewConfiguration", 1);
            final String tempName = (String)this.viewConfig.getFirstValue("ViewConfiguration", 2);
            this.viewName = UserPersonalizationAPI.getOriginalViewName(tempNameNo, WebClientUtil.getAccountId());
            this.viewNameNo = UserPersonalizationAPI.getOriginalViewNameNo(tempNameNo, WebClientUtil.getAccountId());
            if (this.viewName == null) {
                this.viewName = tempName;
            }
            if (this.viewNameNo == null) {
                this.viewNameNo = tempNameNo;
            }
        }
        catch (final DataAccessException dae) {
            throw new RuntimeException((Throwable)dae);
        }
    }
    
    public String getViewName() {
        return this.viewName;
    }
    
    public Long getViewNameNo() {
        return this.viewNameNo;
    }
    
    public String getFeatureValue(final String featureKey) {
        HashMap<String, String> featureMap = (HashMap<String, String>)this.getCompiledData("FEATUREMAP");
        if (featureMap == null) {
            featureMap = new HashMap<String, String>();
            try {
                if (!this.viewConfig.containsTable("FeatureParams")) {
                    return null;
                }
                final Iterator<Row> ite = this.viewConfig.getRows("FeatureParams");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    featureMap.put((String)r.get(2), (String)r.get(3));
                }
            }
            catch (final DataAccessException dae) {
                throw new RuntimeException((Throwable)dae);
            }
            this.addCompiledData("FEATUREMAP", featureMap);
        }
        return featureMap.get(featureKey);
    }
    
    public Object getCompiledData(final Object key) {
        return (this.compiledDataMap == null) ? null : this.compiledDataMap.get(key);
    }
    
    public void addCompiledData(final Object key, final Object compiledValue) {
        final HashMap<Object, Object> newData = (this.compiledDataMap == null) ? new HashMap<Object, Object>() : new HashMap<Object, Object>(this.compiledDataMap);
        newData.put(key, compiledValue);
        synchronized (newData) {
            this.compiledDataMap = newData;
        }
    }
}
