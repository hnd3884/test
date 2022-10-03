package org.msgpack.io;

import java.io.EOFException;

public class EndOfBufferException extends EOFException
{
    public EndOfBufferException() {
    }
    
    public EndOfBufferException(final String s) {
        super(s);
    }
}
