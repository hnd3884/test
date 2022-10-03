package org.apache.tomcat;

public interface ContextBind
{
    ClassLoader bind(final boolean p0, final ClassLoader p1);
    
    void unbind(final boolean p0, final ClassLoader p1);
}
