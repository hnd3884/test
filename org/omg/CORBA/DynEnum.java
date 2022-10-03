package org.omg.CORBA;

@Deprecated
public interface DynEnum extends Object, DynAny
{
    String value_as_string();
    
    void value_as_string(final String p0);
    
    int value_as_ulong();
    
    void value_as_ulong(final int p0);
}
