package org.apache.catalina;

import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.connector.Connector;

public interface Service extends Lifecycle
{
    Engine getContainer();
    
    void setContainer(final Engine p0);
    
    String getName();
    
    void setName(final String p0);
    
    Server getServer();
    
    void setServer(final Server p0);
    
    ClassLoader getParentClassLoader();
    
    void setParentClassLoader(final ClassLoader p0);
    
    String getDomain();
    
    void addConnector(final Connector p0);
    
    Connector[] findConnectors();
    
    void removeConnector(final Connector p0);
    
    void addExecutor(final Executor p0);
    
    Executor[] findExecutors();
    
    Executor getExecutor(final String p0);
    
    void removeExecutor(final Executor p0);
    
    Mapper getMapper();
}
