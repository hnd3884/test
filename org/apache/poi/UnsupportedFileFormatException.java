package org.apache.poi;

public abstract class UnsupportedFileFormatException extends IllegalArgumentException
{
    private static final long serialVersionUID = -8281969197282030046L;
    
    protected UnsupportedFileFormatException(final String s) {
        super(s);
    }
    
    protected UnsupportedFileFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
