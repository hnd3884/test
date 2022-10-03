package com.sun.corba.se.impl.corba;

public interface TypeCodeFactory
{
    void setTypeCode(final String p0, final TypeCodeImpl p1);
    
    TypeCodeImpl getTypeCode(final String p0);
    
    void setTypeCodeForClass(final Class p0, final TypeCodeImpl p1);
    
    TypeCodeImpl getTypeCodeForClass(final Class p0);
}
