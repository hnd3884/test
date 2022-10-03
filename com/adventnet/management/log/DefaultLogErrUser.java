package com.adventnet.management.log;

public class DefaultLogErrUser extends LogErrUser
{
    public DefaultLogErrUser(final String s, final int n, final LogBaseWriter logBaseWriter) {
        super(s, n, logBaseWriter);
    }
    
    public void assert(final boolean b, final String s) {
        if (!b) {
            this.fail(s, null);
        }
    }
    
    public void abort(final String s, final Throwable t) {
        this.fail(s, t);
    }
}
