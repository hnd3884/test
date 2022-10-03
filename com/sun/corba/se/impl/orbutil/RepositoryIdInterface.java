package com.sun.corba.se.impl.orbutil;

import java.net.MalformedURLException;

public interface RepositoryIdInterface
{
    Class getClassFromType() throws ClassNotFoundException;
    
    Class getClassFromType(final String p0) throws ClassNotFoundException, MalformedURLException;
    
    Class getClassFromType(final Class p0, final String p1) throws ClassNotFoundException, MalformedURLException;
    
    String getClassName();
}
