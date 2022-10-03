package com.adventnet.management.log;

public class LogErrUser extends LogUser
{
    public LogErrUser(final String s, final int n, final LogBaseWriter logBaseWriter) {
        super(s, n, logBaseWriter);
    }
    
    public void fail(final String s, final Throwable t) {
        super.fail(s, t);
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
