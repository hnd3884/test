package org.apache.catalina;

public interface ContainerServlet
{
    Wrapper getWrapper();
    
    void setWrapper(final Wrapper p0);
}
