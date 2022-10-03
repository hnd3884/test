package org.dom4j.util;

public interface SingletonStrategy
{
    Object instance();
    
    void reset();
    
    void setSingletonClassName(final String p0);
}
