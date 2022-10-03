package org.tukaani.xz;

interface FilterEncoder extends FilterCoder
{
    long getFilterID();
    
    byte[] getFilterProps();
    
    boolean supportsFlushing();
    
    FinishableOutputStream getOutputStream(final FinishableOutputStream p0, final ArrayCache p1);
}
