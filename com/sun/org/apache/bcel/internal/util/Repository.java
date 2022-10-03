package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;

public interface Repository extends Serializable
{
    void storeClass(final JavaClass p0);
    
    void removeClass(final JavaClass p0);
    
    JavaClass findClass(final String p0);
    
    JavaClass loadClass(final String p0) throws ClassNotFoundException;
    
    JavaClass loadClass(final Class p0) throws ClassNotFoundException;
    
    void clear();
}
