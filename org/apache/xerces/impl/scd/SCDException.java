package org.apache.xerces.impl.scd;

public class SCDException extends Exception
{
    static final long serialVersionUID = -948482312169512085L;
    private final String fKey;
    
    public SCDException() {
        this.fKey = "c-general-SCDParser";
    }
    
    public SCDException(final String fKey) {
        this.fKey = fKey;
    }
    
    public String getKey() {
        return this.fKey;
    }
}
