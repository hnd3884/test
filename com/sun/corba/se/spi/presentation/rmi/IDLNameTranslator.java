package com.sun.corba.se.spi.presentation.rmi;

import java.lang.reflect.Method;

public interface IDLNameTranslator
{
    Class[] getInterfaces();
    
    Method[] getMethods();
    
    Method getMethod(final String p0);
    
    String getIDLName(final Method p0);
}
