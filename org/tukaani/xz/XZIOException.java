package org.tukaani.xz;

import java.io.IOException;

public class XZIOException extends IOException
{
    private static final long serialVersionUID = 3L;
    
    public XZIOException() {
    }
    
    public XZIOException(final String s) {
        super(s);
    }
}
