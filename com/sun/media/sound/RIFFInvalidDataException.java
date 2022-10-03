package com.sun.media.sound;

public final class RIFFInvalidDataException extends InvalidDataException
{
    private static final long serialVersionUID = 1L;
    
    public RIFFInvalidDataException() {
        super("Invalid Data!");
    }
    
    public RIFFInvalidDataException(final String s) {
        super(s);
    }
}
