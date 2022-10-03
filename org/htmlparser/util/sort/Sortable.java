package org.htmlparser.util.sort;

public interface Sortable
{
    int first();
    
    int last();
    
    Ordered fetch(final int p0, final Ordered p1);
    
    void swap(final int p0, final int p1);
}
