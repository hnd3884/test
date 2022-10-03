package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.TypeInfo;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class AttrImpl extends NodeImpl implements Attr
{
    Element element;
    String value;
    
    public AttrImpl() {
        this.nodeType = 2;
    }
    
    public AttrImpl(final Element element, final String prefix, final String localpart, final String rawname, final String uri, final String value) {
        super(prefix, localpart, rawname, uri, (short)2);
        this.element = element;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return this.rawname;
    }
    
    @Override
    public boolean getSpecified() {
        return true;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String getNodeValue() {
        return this.getValue();
    }
    
    @Override
    public Element getOwnerElement() {
        return this.element;
    }
    
    @Override
    public Document getOwnerDocument() {
        return this.element.getOwnerDocument();
    }
    
    @Override
    public void setValue(final String value) throws DOMException {
        this.value = value;
    }
    
    @Override
    public boolean isId() {
        return false;
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    @Override
    public String toString() {
        return this.getName() + "=\"" + this.getValue() + "\"";
    }
}
