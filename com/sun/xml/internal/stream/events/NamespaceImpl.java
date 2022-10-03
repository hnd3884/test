package com.sun.xml.internal.stream.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceImpl extends AttributeImpl implements Namespace
{
    public NamespaceImpl() {
        this.init();
    }
    
    public NamespaceImpl(final String namespaceURI) {
        super("xmlns", "http://www.w3.org/2000/xmlns/", "", namespaceURI, null);
        this.init();
    }
    
    public NamespaceImpl(final String prefix, final String namespaceURI) {
        super("xmlns", "http://www.w3.org/2000/xmlns/", prefix, namespaceURI, null);
        this.init();
    }
    
    @Override
    public boolean isDefaultNamespaceDeclaration() {
        final QName name = this.getName();
        return name != null && name.getLocalPart().equals("");
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
        final QName name = this.getName();
        if (name != null) {
            return name.getLocalPart();
        }
        return null;
    }
    
    @Override
    public String getNamespaceURI() {
        return this.getValue();
    }
    
    void setNamespaceURI(final String uri) {
        this.setValue(uri);
    }
    
    @Override
    protected void init() {
        this.setEventType(13);
    }
    
    @Override
    public int getEventType() {
        return 13;
    }
    
    @Override
    public boolean isNamespace() {
        return true;
    }
}
