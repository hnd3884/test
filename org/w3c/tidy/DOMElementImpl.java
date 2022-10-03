package org.w3c.tidy;

import org.w3c.dom.TypeInfo;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class DOMElementImpl extends DOMNodeImpl implements Element
{
    protected DOMElementImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public short getNodeType() {
        return 1;
    }
    
    public String getTagName() {
        return super.getNodeName();
    }
    
    public String getAttribute(final String s) {
        if (this.adaptee == null) {
            return null;
        }
        AttVal attVal;
        for (attVal = this.adaptee.attributes; attVal != null && !attVal.attribute.equals(s); attVal = attVal.next) {}
        if (attVal != null) {
            return attVal.value;
        }
        return "";
    }
    
    public void setAttribute(final String s, final String value) throws DOMException {
        if (this.adaptee == null) {
            return;
        }
        AttVal attVal;
        for (attVal = this.adaptee.attributes; attVal != null && !attVal.attribute.equals(s); attVal = attVal.next) {}
        if (attVal != null) {
            attVal.value = value;
        }
        else {
            final AttVal attVal2 = new AttVal(null, null, 34, s, value);
            attVal2.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attVal2);
            if (this.adaptee.attributes == null) {
                this.adaptee.attributes = attVal2;
            }
            else {
                attVal2.next = this.adaptee.attributes;
                this.adaptee.attributes = attVal2;
            }
        }
    }
    
    public void removeAttribute(final String s) throws DOMException {
        if (this.adaptee == null) {
            return;
        }
        AttVal attVal = this.adaptee.attributes;
        AttVal attVal2 = null;
        while (attVal != null && !attVal.attribute.equals(s)) {
            attVal2 = attVal;
            attVal = attVal.next;
        }
        if (attVal != null) {
            if (attVal2 == null) {
                this.adaptee.attributes = attVal.next;
            }
            else {
                attVal2.next = attVal.next;
            }
        }
    }
    
    public Attr getAttributeNode(final String s) {
        if (this.adaptee == null) {
            return null;
        }
        AttVal attVal;
        for (attVal = this.adaptee.attributes; attVal != null && !attVal.attribute.equals(s); attVal = attVal.next) {}
        if (attVal != null) {
            return attVal.getAdapter();
        }
        return null;
    }
    
    public Attr setAttributeNode(final Attr adapter) throws DOMException {
        if (adapter == null) {
            return null;
        }
        if (!(adapter instanceof DOMAttrImpl)) {
            throw new DOMException((short)4, "newAttr not instanceof DOMAttrImpl");
        }
        final DOMAttrImpl domAttrImpl = (DOMAttrImpl)adapter;
        final String attribute = domAttrImpl.avAdaptee.attribute;
        Attr adapter2 = null;
        AttVal attVal;
        for (attVal = this.adaptee.attributes; attVal != null && !attVal.attribute.equals(attribute); attVal = attVal.next) {}
        if (attVal != null) {
            adapter2 = attVal.getAdapter();
            attVal.adapter = adapter;
        }
        else if (this.adaptee.attributes == null) {
            this.adaptee.attributes = domAttrImpl.avAdaptee;
        }
        else {
            domAttrImpl.avAdaptee.next = this.adaptee.attributes;
            this.adaptee.attributes = domAttrImpl.avAdaptee;
        }
        return adapter2;
    }
    
    public Attr removeAttributeNode(final Attr attr) throws DOMException {
        if (attr == null) {
            return null;
        }
        AttVal attVal = this.adaptee.attributes;
        AttVal attVal2 = null;
        while (attVal != null && attVal.getAdapter() != attr) {
            attVal2 = attVal;
            attVal = attVal.next;
        }
        if (attVal != null) {
            if (attVal2 == null) {
                this.adaptee.attributes = attVal.next;
            }
            else {
                attVal2.next = attVal.next;
            }
            return attr;
        }
        throw new DOMException((short)8, "oldAttr not found");
    }
    
    public NodeList getElementsByTagName(final String s) {
        return new DOMNodeListByTagNameImpl(this.adaptee, s);
    }
    
    public void normalize() {
    }
    
    public String getAttributeNS(final String s, final String s2) {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public void setAttributeNS(final String s, final String s2, final String s3) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public void removeAttributeNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Attr getAttributeNodeNS(final String s, final String s2) {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Attr setAttributeNodeNS(final Attr attr) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public boolean hasAttribute(final String s) {
        return false;
    }
    
    public boolean hasAttributeNS(final String s, final String s2) {
        return false;
    }
    
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    public void setIdAttribute(final String s, final boolean b) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public void setIdAttributeNode(final Attr attr, final boolean b) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public void setIdAttributeNS(final String s, final String s2, final boolean b) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
}
