package org.htmlparser.filters;

import org.htmlparser.Attribute;
import org.htmlparser.Tag;
import org.htmlparser.Node;
import java.util.Locale;
import org.htmlparser.NodeFilter;

public class HasAttributeFilter implements NodeFilter
{
    protected String mAttribute;
    protected String mValue;
    
    public HasAttributeFilter() {
        this("", null);
    }
    
    public HasAttributeFilter(final String attribute) {
        this(attribute, null);
    }
    
    public HasAttributeFilter(final String attribute, final String value) {
        this.mAttribute = attribute.toUpperCase(Locale.ENGLISH);
        this.mValue = value;
    }
    
    public String getAttributeName() {
        return this.mAttribute;
    }
    
    public void setAttributeName(final String name) {
        this.mAttribute = name;
    }
    
    public String getAttributeValue() {
        return this.mValue;
    }
    
    public void setAttributeValue(final String value) {
        this.mValue = value;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (node instanceof Tag) {
            final Tag tag = (Tag)node;
            final Attribute attribute = tag.getAttributeEx(this.mAttribute);
            ret = (null != attribute);
            if (ret && null != this.mValue) {
                ret = this.mValue.equals(attribute.getValue());
            }
        }
        return ret;
    }
}
