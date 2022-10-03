package org.apache.catalina.storeconfig;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Service;
import java.io.PrintWriter;
import org.apache.catalina.Server;

public interface IStoreConfig
{
    StoreRegistry getRegistry();
    
    void setRegistry(final StoreRegistry p0);
    
    Server getServer();
    
    void setServer(final Server p0);
    
    void storeConfig();
    
    boolean store(final Server p0);
    
    void store(final PrintWriter p0, final int p1, final Server p2) throws Exception;
    
    void store(final PrintWriter p0, final int p1, final Service p2) throws Exception;
    
    void store(final PrintWriter p0, final int p1, final Host p2) throws Exception;
    
    boolean store(final Context p0);
    
    void store(final PrintWriter p0, final int p1, final Context p2) throws Exception;
}
