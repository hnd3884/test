package com.sun.org.apache.xerces.internal.impl.xpath;

public class XPathException extends Exception
{
    static final long serialVersionUID = -948482312169512085L;
    private String fKey;
    
    public XPathException() {
        this.fKey = "c-general-xpath";
    }
    
    public XPathException(final String key) {
        this.fKey = key;
    }
    
    public String getKey() {
        return this.fKey;
    }
}
