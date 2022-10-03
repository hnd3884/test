package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XNIException;

public class XMLConfigurationException extends XNIException
{
    static final long serialVersionUID = -5437427404547669188L;
    public static final short NOT_RECOGNIZED = 0;
    public static final short NOT_SUPPORTED = 1;
    protected short fType;
    protected String fIdentifier;
    
    public XMLConfigurationException(final short fType, final String fIdentifier) {
        super(fIdentifier);
        this.fType = fType;
        this.fIdentifier = fIdentifier;
    }
    
    public XMLConfigurationException(final short fType, final String fIdentifier, final String s) {
        super(s);
        this.fType = fType;
        this.fIdentifier = fIdentifier;
    }
    
    public short getType() {
        return this.fType;
    }
    
    public String getIdentifier() {
        return this.fIdentifier;
    }
}
