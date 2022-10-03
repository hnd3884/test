package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynFixedOperations extends DynAnyOperations
{
    String get_value();
    
    boolean set_value(final String p0) throws TypeMismatch, InvalidValue;
}
