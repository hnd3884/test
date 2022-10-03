package com.sshtools.zlib;

import java.io.IOException;

public class ZStreamException extends IOException
{
    public ZStreamException() {
    }
    
    public ZStreamException(final String s) {
        super(s);
    }
}
