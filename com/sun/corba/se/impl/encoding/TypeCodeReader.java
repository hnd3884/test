package com.sun.corba.se.impl.encoding;

import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.corba.TypeCodeImpl;

public interface TypeCodeReader extends MarshalInputStream
{
    void addTypeCodeAtPosition(final TypeCodeImpl p0, final int p1);
    
    TypeCodeImpl getTypeCodeAtPosition(final int p0);
    
    void setEnclosingInputStream(final InputStream p0);
    
    TypeCodeReader getTopLevelStream();
    
    int getTopLevelPosition();
    
    int getPosition();
    
    void printTypeMap();
}
