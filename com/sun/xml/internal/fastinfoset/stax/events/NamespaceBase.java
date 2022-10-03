package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceBase extends AttributeBase implements Namespace
{
    static final String DEFAULT_NS_PREFIX = "";
    static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";
    static final String XML_NS_PREFIX = "xml";
    static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/";
    static final String XMLNS_ATTRIBUTE = "xmlns";
    static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
    static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private boolean defaultDeclaration;
    
    public NamespaceBase(final String namespaceURI) {
        super("xmlns", "", namespaceURI);
        this.defaultDeclaration = false;
        this.setEventType(13);
    }
    
    public NamespaceBase(final String prefix, final String namespaceURI) {
        super("xmlns", prefix, namespaceURI);
        this.defaultDeclaration = false;
        this.setEventType(13);
        if (Util.isEmptyString(prefix)) {
            this.defaultDeclaration = true;
        }
    }
    
    void setPrefix(final String prefix) {
        if (prefix == null) {
            this.setName(new QName("http://www.w3.org/2000/xmlns/", "", "xmlns"));
        }
        else {
            this.setName(new QName("http://www.w3.org/2000/xmlns/", prefix, "xmlns"));
        }
    }
    
    @Override
    public String getPrefix() {
        if (this.defaultDeclaration) {
            return "";
        }
        return super.getLocalName();
    }
    
    void setNamespaceURI(final String uri) {
        this.setValue(uri);
    }
    
    @Override
    public String getNamespaceURI() {
        return this.getValue();
    }
    
    @Override
    public boolean isNamespace() {
        return true;
    }
    
    @Override
    public boolean isDefaultNamespaceDeclaration() {
        return this.defaultDeclaration;
    }
}
