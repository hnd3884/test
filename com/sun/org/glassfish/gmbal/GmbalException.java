package com.sun.org.glassfish.gmbal;

public class GmbalException extends RuntimeException
{
    private static final long serialVersionUID = -7478444176079980162L;
    
    public GmbalException(final String msg) {
        super(msg);
    }
    
    public GmbalException(final String msg, final Throwable thr) {
        super(msg, thr);
    }
}
