package org.htmlparser.sax;

import java.util.Vector;
import org.htmlparser.Attribute;
import org.xml.sax.helpers.NamespaceSupport;
import org.htmlparser.Tag;

public class Attributes implements org.xml.sax.Attributes
{
    protected Tag mTag;
    protected NamespaceSupport mSupport;
    protected String[] mParts;
    
    public Attributes(final Tag tag, final NamespaceSupport support, final String[] parts) {
        this.mTag = tag;
        this.mSupport = support;
        this.mParts = parts;
    }
    
    public int getLength() {
        return this.mTag.getAttributesEx().size() - 1;
    }
    
    public String getURI(final int index) {
        this.mSupport.processName(this.getQName(index), this.mParts, true);
        return this.mParts[0];
    }
    
    public String getLocalName(final int index) {
        this.mSupport.processName(this.getQName(index), this.mParts, true);
        return this.mParts[1];
    }
    
    public String getQName(final int index) {
        final Attribute attribute = this.mTag.getAttributesEx().elementAt(index + 1);
        String ret;
        if (attribute.isWhitespace()) {
            ret = "#text";
        }
        else {
            ret = attribute.getName();
        }
        return ret;
    }
    
    public String getType(final int index) {
        return "CDATA";
    }
    
    public String getValue(final int index) {
        final Attribute attribute = this.mTag.getAttributesEx().elementAt(index + 1);
        String ret = attribute.getValue();
        if (null == ret) {
            ret = "";
        }
        return ret;
    }
    
    public int getIndex(final String uri, final String localName) {
        int ret = -1;
        final Vector attributes = this.mTag.getAttributesEx();
        if (null != attributes) {
            for (int size = attributes.size(), i = 1; i < size; ++i) {
                final Attribute attribute = attributes.elementAt(i);
                final String string = attribute.getName();
                if (null != string) {
                    this.mSupport.processName(string, this.mParts, true);
                    if (uri.equals(this.mParts[0]) & localName.equalsIgnoreCase(this.mParts[1])) {
                        ret = i;
                        i = size;
                    }
                }
            }
        }
        return ret;
    }
    
    public int getIndex(final String qName) {
        this.mSupport.processName(qName, this.mParts, true);
        return this.getIndex(this.mParts[0], this.mParts[1]);
    }
    
    public String getType(final String uri, final String localName) {
        return null;
    }
    
    public String getType(final String qName) {
        return null;
    }
    
    public String getValue(final String uri, final String localName) {
        return this.mTag.getAttribute(localName);
    }
    
    public String getValue(final String qName) {
        this.mSupport.processName(qName, this.mParts, true);
        return this.getValue(this.mParts[0], this.mParts[1]);
    }
}
