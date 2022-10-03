package org.apache.tomcat;

import java.lang.instrument.ClassFileTransformer;

public interface InstrumentableClassLoader
{
    void addTransformer(final ClassFileTransformer p0);
    
    void removeTransformer(final ClassFileTransformer p0);
    
    ClassLoader copyWithoutTransformers();
}
