package com.adventnet.customview;

import java.util.HashMap;
import java.util.Map;
import com.adventnet.model.Model;
import com.adventnet.persistence.DataObject;
import java.io.Serializable;

public class ViewData implements Serializable
{
    protected DataObject customViewConfiguration;
    protected Model model;
    Map hashMap;
    
    protected ViewData() {
        this.hashMap = new HashMap();
    }
    
    public ViewData(final DataObject customViewConfiguration, final Model model) {
        this.hashMap = new HashMap();
        this.customViewConfiguration = customViewConfiguration;
        this.model = model;
    }
    
    public DataObject getCustomViewConfiguration() {
        return this.customViewConfiguration;
    }
    
    public void setCustomViewConfiguration(final DataObject v) {
        this.customViewConfiguration = v;
    }
    
    public Model getModel() {
        return this.model;
    }
    
    public void setModel(final Model v) {
        this.model = v;
    }
    
    public Object put(final Object key, final Object value) {
        return this.hashMap.put(key, value);
    }
    
    public Object remove(final Object key) {
        return this.hashMap.remove(key);
    }
    
    public Object get(final Object key) {
        return this.hashMap.get(key);
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<VIEWDATA>\n");
        buff.append(this.customViewConfiguration);
        buff.append("\n");
        buff.append(this.model);
        buff.append("\n");
        buff.append(this.hashMap);
        buff.append("\n</VIEWDATA>");
        return buff.toString();
    }
}
