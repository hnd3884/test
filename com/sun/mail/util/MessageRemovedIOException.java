package com.sun.mail.util;

import java.io.IOException;

public class MessageRemovedIOException extends IOException
{
    private static final long serialVersionUID = 4280468026581616424L;
    
    public MessageRemovedIOException() {
    }
    
    public MessageRemovedIOException(final String s) {
        super(s);
    }
}
