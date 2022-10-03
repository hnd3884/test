package org.apache.xerces.impl.xs.opti;

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
    
    public AttrImpl(final Element element, final String s, final String s2, final String s3, final String s4, final String value) {
        super(s, s2, s3, s4, (short)2);
        this.element = element;
        this.value = value;
    }
    
    public String getName() {
        return this.rawname;
    }
    
    public boolean getSpecified() {
        return true;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getNodeValue() {
        return this.getValue();
    }
    
    public Element getOwnerElement() {
        return this.element;
    }
    
    public Document getOwnerDocument() {
        return this.element.getOwnerDocument();
    }
    
    public void setValue(final String value) throws DOMException {
        this.value = value;
    }
    
    public boolean isId() {
        return false;
    }
    
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    public String toString() {
        return this.getName() + "=" + "\"" + this.getValue() + "\"";
    }
}
