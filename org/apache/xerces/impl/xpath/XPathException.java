package org.apache.xerces.impl.xpath;

public class XPathException extends Exception
{
    static final long serialVersionUID = -948482312169512085L;
    private final String fKey;
    
    public XPathException() {
        this.fKey = "c-general-xpath";
    }
    
    public XPathException(final String fKey) {
        this.fKey = fKey;
    }
    
    public String getKey() {
        return this.fKey;
    }
}
