package com.adventnet.client.components.form.web;

import java.util.Hashtable;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import com.adventnet.client.components.web.DefaultTransformerContext;

public class FormTransformerContext extends DefaultTransformerContext
{
    private Properties props;
    private DataObject columnConfiguration;
    private String propertyName;
    private String mode;
    private String dataType;
    
    public FormTransformerContext(final Properties props, final ViewContext viewContext) {
        super(viewContext);
        this.props = null;
        this.columnConfiguration = null;
        this.propertyName = null;
        this.mode = null;
        this.dataType = null;
        this.props = props;
    }
    
    @Override
    public Object getDataModel() {
        return this.props;
    }
    
    public Object getViewModel() {
        return this.props;
    }
    
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }
    
    @Override
    public String getPropertyName() {
        return this.propertyName;
    }
    
    @Override
    public Object getPropertyValue() {
        return ((Hashtable<K, Object>)this.props).get(this.propertyName);
    }
    
    @Override
    public Object getAssociatedPropertyValue(final String propName) {
        return ((Hashtable<K, Object>)this.props).get(propName);
    }
    
    @Override
    public HashMap getRenderedAttributes() {
        return this.renderedProperties;
    }
    
    public void setMode(final String mode) {
        this.mode = mode;
    }
    
    public String getMode() {
        return this.mode;
    }
    
    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    @Override
    public Object getAssociatedPropertyValue(final String propertyName, final boolean encrypted) {
        return null;
    }
}
