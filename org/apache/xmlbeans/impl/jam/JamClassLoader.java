package org.apache.xmlbeans.impl.jam;

public interface JamClassLoader
{
    JClass loadClass(final String p0);
    
    JPackage getPackage(final String p0);
}
