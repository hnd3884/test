package com.sun.corba.se.impl.io;

import java.io.IOException;

public class OptionalDataException extends IOException
{
    public int length;
    public boolean eof;
    
    OptionalDataException(final int length) {
        this.eof = false;
        this.length = length;
    }
    
    OptionalDataException(final boolean eof) {
        this.length = 0;
        this.eof = eof;
    }
}
