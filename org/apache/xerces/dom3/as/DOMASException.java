package org.apache.xerces.dom3.as;

public class DOMASException extends RuntimeException
{
    public short code;
    public static final short DUPLICATE_NAME_ERR = 1;
    public static final short TYPE_ERR = 2;
    public static final short NO_AS_AVAILABLE = 3;
    public static final short WRONG_MIME_TYPE_ERR = 4;
    
    public DOMASException(final short code, final String s) {
        super(s);
        this.code = code;
    }
}
