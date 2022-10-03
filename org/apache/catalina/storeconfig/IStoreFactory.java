package org.apache.catalina.storeconfig;

import java.io.PrintWriter;

public interface IStoreFactory
{
    StoreAppender getStoreAppender();
    
    void setStoreAppender(final StoreAppender p0);
    
    void setRegistry(final StoreRegistry p0);
    
    StoreRegistry getRegistry();
    
    void store(final PrintWriter p0, final int p1, final Object p2) throws Exception;
    
    void storeXMLHead(final PrintWriter p0);
}
