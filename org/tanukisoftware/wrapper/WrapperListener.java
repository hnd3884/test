package org.tanukisoftware.wrapper;

public interface WrapperListener
{
    Integer start(final String[] p0);
    
    int stop(final int p0);
    
    void controlEvent(final int p0);
}
