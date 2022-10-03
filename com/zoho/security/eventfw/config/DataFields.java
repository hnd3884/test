package com.zoho.security.eventfw.config;

import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.Element;
import com.zoho.security.eventfw.builtinfieldsImpl.BuiltInFieldsProvider;

public class DataFields
{
    private String name;
    private String type;
    private String ref;
    private String mapStr;
    private BuiltInFieldsProvider builtinFieldImpl;
    
    public DataFields(final Element dataField) {
        this.builtinFieldImpl = null;
        this.name = dataField.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value()).toUpperCase();
        this.name = this.name.replaceAll("[^A-Z_$0-9]", "_");
        this.type = dataField.getAttribute(EventFWConstants.ATTRIBUTES.TYPE.value());
        this.ref = dataField.getAttribute(EventFWConstants.ATTRIBUTES.REF.value());
    }
    
    public DataFields(final Element dataField, final BuiltInFieldsProvider implProvider) {
        this(dataField);
        this.builtinFieldImpl = implProvider;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getRef() {
        return this.ref;
    }
    
    @Override
    public String toString() {
        if (this.mapStr != null) {
            return this.mapStr;
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("type", this.type);
        return this.mapStr = map.toString();
    }
    
    public BuiltInFieldsProvider getImplProvider() {
        return this.builtinFieldImpl;
    }
}
