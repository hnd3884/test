package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Map;
import java.util.HashMap;

public class StAXManager
{
    protected static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    protected static final String STAX_ENTITIES = "javax.xml.stream.entities";
    HashMap features;
    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;
    
    public StAXManager() {
        this.features = new HashMap();
    }
    
    public StAXManager(final int context) {
        this.features = new HashMap();
        switch (context) {
            case 1: {
                this.initConfigurableReaderProperties();
                break;
            }
            case 2: {
                this.initWriterProps();
                break;
            }
        }
    }
    
    public StAXManager(final StAXManager manager) {
        this.features = new HashMap();
        final HashMap properties = manager.getProperties();
        this.features.putAll(properties);
    }
    
    private HashMap getProperties() {
        return this.features;
    }
    
    private void initConfigurableReaderProperties() {
        this.features.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        this.features.put("javax.xml.stream.isValidating", Boolean.FALSE);
        this.features.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
        this.features.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
        this.features.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
        this.features.put("javax.xml.stream.supportDTD", Boolean.FALSE);
        this.features.put("javax.xml.stream.reporter", null);
        this.features.put("javax.xml.stream.resolver", null);
        this.features.put("javax.xml.stream.allocator", null);
        this.features.put("javax.xml.stream.notations", null);
    }
    
    private void initWriterProps() {
        this.features.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
    }
    
    public boolean containsProperty(final String property) {
        return this.features.containsKey(property);
    }
    
    public Object getProperty(final String name) {
        this.checkProperty(name);
        return this.features.get(name);
    }
    
    public void setProperty(final String name, final Object value) {
        this.checkProperty(name);
        if (name.equals("javax.xml.stream.isValidating") && Boolean.TRUE.equals(value)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.validationNotSupported") + CommonResourceBundle.getInstance().getString("support_validation"));
        }
        if (name.equals("javax.xml.stream.isSupportingExternalEntities") && Boolean.TRUE.equals(value)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.externalEntities") + CommonResourceBundle.getInstance().getString("resolve_external_entities_"));
        }
        this.features.put(name, value);
    }
    
    public void checkProperty(final String name) {
        if (!this.features.containsKey(name)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { name }));
        }
    }
    
    @Override
    public String toString() {
        return this.features.toString();
    }
}
