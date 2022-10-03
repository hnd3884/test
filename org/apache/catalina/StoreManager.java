package org.apache.catalina;

public interface StoreManager extends DistributedManager
{
    Store getStore();
    
    void removeSuper(final Session p0);
}
