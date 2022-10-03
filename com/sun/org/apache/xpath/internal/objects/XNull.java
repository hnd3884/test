package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xpath.internal.XPathContext;

public class XNull extends XNodeSet
{
    static final long serialVersionUID = -6841683711458983005L;
    
    @Override
    public int getType() {
        return -1;
    }
    
    @Override
    public String getTypeString() {
        return "#CLASS_NULL";
    }
    
    @Override
    public double num() {
        return 0.0;
    }
    
    @Override
    public boolean bool() {
        return false;
    }
    
    @Override
    public String str() {
        return "";
    }
    
    @Override
    public int rtf(final XPathContext support) {
        return -1;
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        return obj2.getType() == -1;
    }
}
