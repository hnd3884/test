package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

public class AttList implements Attributes
{
    NamedNodeMap m_attrs;
    int m_lastIndex;
    
    public AttList(final NamedNodeMap attrs) {
        this.m_attrs = attrs;
        this.m_lastIndex = this.m_attrs.getLength() - 1;
    }
    
    @Override
    public int getLength() {
        return this.m_attrs.getLength();
    }
    
    @Override
    public String getURI(final int index) {
        String ns = DOM2Helper.getNamespaceOfNode(this.m_attrs.item(index));
        if (null == ns) {
            ns = "";
        }
        return ns;
    }
    
    @Override
    public String getLocalName(final int index) {
        return DOM2Helper.getLocalNameOfNode(this.m_attrs.item(index));
    }
    
    @Override
    public String getQName(final int i) {
        return ((Attr)this.m_attrs.item(i)).getName();
    }
    
    @Override
    public String getType(final int i) {
        return "CDATA";
    }
    
    @Override
    public String getValue(final int i) {
        return ((Attr)this.m_attrs.item(i)).getValue();
    }
    
    @Override
    public String getType(final String name) {
        return "CDATA";
    }
    
    @Override
    public String getType(final String uri, final String localName) {
        return "CDATA";
    }
    
    @Override
    public String getValue(final String name) {
        final Attr attr = (Attr)this.m_attrs.getNamedItem(name);
        return (null != attr) ? attr.getValue() : null;
    }
    
    @Override
    public String getValue(final String uri, final String localName) {
        final Node a = this.m_attrs.getNamedItemNS(uri, localName);
        return (a == null) ? null : a.getNodeValue();
    }
    
    @Override
    public int getIndex(final String uri, final String localPart) {
        for (int i = this.m_attrs.getLength() - 1; i >= 0; --i) {
            final Node a = this.m_attrs.item(i);
            final String u = a.getNamespaceURI();
            if (u == null) {
                if (uri != null) {
                    continue;
                }
            }
            else if (!u.equals(uri)) {
                continue;
            }
            if (a.getLocalName().equals(localPart)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int getIndex(final String qName) {
        for (int i = this.m_attrs.getLength() - 1; i >= 0; --i) {
            final Node a = this.m_attrs.item(i);
            if (a.getNodeName().equals(qName)) {
                return i;
            }
        }
        return -1;
    }
}
