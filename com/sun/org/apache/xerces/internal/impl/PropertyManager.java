package com.sun.org.apache.xerces.internal.impl;

import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import javax.xml.stream.XMLResolver;
import java.util.Map;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import java.util.HashMap;

public class PropertyManager
{
    public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    public static final String STAX_ENTITIES = "javax.xml.stream.entities";
    private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    HashMap supportedProps;
    private XMLSecurityManager fSecurityManager;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;
    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;
    
    public PropertyManager(final int context) {
        this.supportedProps = new HashMap();
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
    
    public PropertyManager(final PropertyManager propertyManager) {
        this.supportedProps = new HashMap();
        final HashMap properties = propertyManager.getProperties();
        this.supportedProps.putAll(properties);
        this.fSecurityManager = (XMLSecurityManager)this.getProperty("http://apache.org/xml/properties/security-manager");
        this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)this.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    }
    
    private HashMap getProperties() {
        return this.supportedProps;
    }
    
    private void initConfigurableReaderProperties() {
        this.supportedProps.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        this.supportedProps.put("javax.xml.stream.isValidating", Boolean.FALSE);
        this.supportedProps.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
        this.supportedProps.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
        this.supportedProps.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
        this.supportedProps.put("javax.xml.stream.supportDTD", Boolean.TRUE);
        this.supportedProps.put("javax.xml.stream.reporter", null);
        this.supportedProps.put("javax.xml.stream.resolver", null);
        this.supportedProps.put("javax.xml.stream.allocator", null);
        this.supportedProps.put("javax.xml.stream.notations", null);
        this.supportedProps.put("http://xml.org/sax/features/string-interning", new Boolean(true));
        this.supportedProps.put("http://apache.org/xml/features/allow-java-encodings", new Boolean(true));
        this.supportedProps.put("add-namespacedecl-as-attrbiute", Boolean.FALSE);
        this.supportedProps.put("http://java.sun.com/xml/stream/properties/reader-in-defined-state", new Boolean(true));
        this.supportedProps.put("reuse-instance", new Boolean(true));
        this.supportedProps.put("http://java.sun.com/xml/stream/properties/report-cdata-event", new Boolean(false));
        this.supportedProps.put("http://java.sun.com/xml/stream/properties/ignore-external-dtd", Boolean.FALSE);
        this.supportedProps.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", new Boolean(false));
        this.supportedProps.put("http://apache.org/xml/features/warn-on-duplicate-entitydef", new Boolean(false));
        this.supportedProps.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", new Boolean(false));
        this.fSecurityManager = new XMLSecurityManager(true);
        this.supportedProps.put("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        this.supportedProps.put("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
    }
    
    private void initWriterProps() {
        this.supportedProps.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
        this.supportedProps.put("escapeCharacters", Boolean.TRUE);
        this.supportedProps.put("reuse-instance", new Boolean(true));
    }
    
    public boolean containsProperty(final String property) {
        return this.supportedProps.containsKey(property) || (this.fSecurityManager != null && this.fSecurityManager.getIndex(property) > -1) || (this.fSecurityPropertyMgr != null && this.fSecurityPropertyMgr.getIndex(property) > -1);
    }
    
    public Object getProperty(final String property) {
        return this.supportedProps.get(property);
    }
    
    public void setProperty(final String property, final Object value) {
        String equivalentProperty = null;
        if (property == "javax.xml.stream.isNamespaceAware" || property.equals("javax.xml.stream.isNamespaceAware")) {
            equivalentProperty = "http://apache.org/xml/features/namespaces";
        }
        else if (property == "javax.xml.stream.isValidating" || property.equals("javax.xml.stream.isValidating")) {
            if (value instanceof Boolean && (boolean)value) {
                throw new IllegalArgumentException("true value of isValidating not supported");
            }
        }
        else if (property == "http://xml.org/sax/features/string-interning" || property.equals("http://xml.org/sax/features/string-interning")) {
            if (value instanceof Boolean && !(boolean)value) {
                throw new IllegalArgumentException("false value of http://xml.org/sax/features/string-interningfeature is not supported");
            }
        }
        else if (property == "javax.xml.stream.resolver" || property.equals("javax.xml.stream.resolver")) {
            this.supportedProps.put("http://apache.org/xml/properties/internal/stax-entity-resolver", new StaxEntityResolverWrapper((XMLResolver)value));
        }
        if (property.equals("http://apache.org/xml/properties/security-manager")) {
            this.fSecurityManager = XMLSecurityManager.convert(value, this.fSecurityManager);
            this.supportedProps.put("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            return;
        }
        if (property.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            if (value == null) {
                this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
            }
            else {
                this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)value;
            }
            this.supportedProps.put("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
            return;
        }
        if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(property, XMLSecurityManager.State.APIPROPERTY, value)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(property, XMLSecurityPropertyManager.State.APIPROPERTY, value))) {
            this.supportedProps.put(property, value);
        }
        if (equivalentProperty != null) {
            this.supportedProps.put(equivalentProperty, value);
        }
    }
    
    @Override
    public String toString() {
        return this.supportedProps.toString();
    }
}
