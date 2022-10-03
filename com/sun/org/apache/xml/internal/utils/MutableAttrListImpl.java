package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.Attributes;
import java.io.Serializable;
import org.xml.sax.helpers.AttributesImpl;

public class MutableAttrListImpl extends AttributesImpl implements Serializable
{
    static final long serialVersionUID = 6289452013442934470L;
    
    public MutableAttrListImpl() {
    }
    
    public MutableAttrListImpl(final Attributes atts) {
        super(atts);
    }
    
    @Override
    public void addAttribute(String uri, final String localName, final String qName, final String type, final String value) {
        if (null == uri) {
            uri = "";
        }
        final int index = this.getIndex(qName);
        if (index >= 0) {
            this.setAttribute(index, uri, localName, qName, type, value);
        }
        else {
            super.addAttribute(uri, localName, qName, type, value);
        }
    }
    
    public void addAttributes(final Attributes atts) {
        for (int nAtts = atts.getLength(), i = 0; i < nAtts; ++i) {
            String uri = atts.getURI(i);
            if (null == uri) {
                uri = "";
            }
            final String localName = atts.getLocalName(i);
            final String qname = atts.getQName(i);
            final int index = this.getIndex(uri, localName);
            if (index >= 0) {
                this.setAttribute(index, uri, localName, qname, atts.getType(i), atts.getValue(i));
            }
            else {
                this.addAttribute(uri, localName, qname, atts.getType(i), atts.getValue(i));
            }
        }
    }
    
    public boolean contains(final String name) {
        return this.getValue(name) != null;
    }
}
