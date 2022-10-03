package org.apache.catalina;

import java.io.File;
import org.apache.catalina.startup.Catalina;
import javax.naming.Context;
import org.apache.catalina.deploy.NamingResourcesImpl;

public interface Server extends Lifecycle
{
    NamingResourcesImpl getGlobalNamingResources();
    
    void setGlobalNamingResources(final NamingResourcesImpl p0);
    
    Context getGlobalNamingContext();
    
    int getPort();
    
    void setPort(final int p0);
    
    String getAddress();
    
    void setAddress(final String p0);
    
    String getShutdown();
    
    void setShutdown(final String p0);
    
    ClassLoader getParentClassLoader();
    
    void setParentClassLoader(final ClassLoader p0);
    
    Catalina getCatalina();
    
    void setCatalina(final Catalina p0);
    
    File getCatalinaBase();
    
    void setCatalinaBase(final File p0);
    
    File getCatalinaHome();
    
    void setCatalinaHome(final File p0);
    
    void addService(final Service p0);
    
    void await();
    
    Service findService(final String p0);
    
    Service[] findServices();
    
    void removeService(final Service p0);
    
    Object getNamingToken();
}
