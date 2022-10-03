package org.apache.tomcat.util.descriptor.web;

public interface NamingResources
{
    void addEnvironment(final ContextEnvironment p0);
    
    void removeEnvironment(final String p0);
    
    void addResource(final ContextResource p0);
    
    void removeResource(final String p0);
    
    void addResourceLink(final ContextResourceLink p0);
    
    void removeResourceLink(final String p0);
    
    Object getContainer();
}
