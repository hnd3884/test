package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import org.xml.sax.AttributeList;

public final class Attributes implements AttributeList
{
    private int _element;
    private DOM _document;
    
    public Attributes(final DOM document, final int element) {
        this._element = element;
        this._document = document;
    }
    
    @Override
    public int getLength() {
        return 0;
    }
    
    @Override
    public String getName(final int i) {
        return null;
    }
    
    @Override
    public String getType(final int i) {
        return null;
    }
    
    @Override
    public String getType(final String name) {
        return null;
    }
    
    @Override
    public String getValue(final int i) {
        return null;
    }
    
    @Override
    public String getValue(final String name) {
        return null;
    }
}
