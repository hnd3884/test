package org.omg.CosNaming;

public interface BindingIteratorOperations
{
    boolean next_one(final BindingHolder p0);
    
    boolean next_n(final int p0, final BindingListHolder p1);
    
    void destroy();
}
