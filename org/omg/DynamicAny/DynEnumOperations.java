package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public interface DynEnumOperations extends DynAnyOperations
{
    String get_as_string();
    
    void set_as_string(final String p0) throws InvalidValue;
    
    int get_as_ulong();
    
    void set_as_ulong(final int p0) throws InvalidValue;
}
