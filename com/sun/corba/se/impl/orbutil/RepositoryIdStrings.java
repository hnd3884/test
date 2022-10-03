package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Serializable;

public interface RepositoryIdStrings
{
    String createForAnyType(final Class p0);
    
    String createForJavaType(final Serializable p0) throws TypeMismatchException;
    
    String createForJavaType(final Class p0) throws TypeMismatchException;
    
    String createSequenceRepID(final Object p0);
    
    String createSequenceRepID(final Class p0);
    
    RepositoryIdInterface getFromString(final String p0);
    
    String getClassDescValueRepId();
    
    String getWStringValueRepId();
}
