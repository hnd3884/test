package com.sun.mail.imap;

import java.util.Date;
import javax.mail.Message;
import javax.mail.search.SearchTerm;

public final class OlderTerm extends SearchTerm
{
    private int interval;
    private static final long serialVersionUID = 3951078948727995682L;
    
    public OlderTerm(final int interval) {
        this.interval = interval;
    }
    
    public int getInterval() {
        return this.interval;
    }
    
    @Override
    public boolean match(final Message msg) {
        Date d;
        try {
            d = msg.getReceivedDate();
        }
        catch (final Exception e) {
            return false;
        }
        return d != null && d.getTime() <= System.currentTimeMillis() - this.interval * 1000L;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OlderTerm && this.interval == ((OlderTerm)obj).interval;
    }
    
    @Override
    public int hashCode() {
        return this.interval;
    }
}
