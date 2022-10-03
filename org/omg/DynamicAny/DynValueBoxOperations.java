package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.Any;

public interface DynValueBoxOperations extends DynValueCommonOperations
{
    Any get_boxed_value() throws InvalidValue;
    
    void set_boxed_value(final Any p0) throws TypeMismatch;
    
    DynAny get_boxed_value_as_dyn_any() throws InvalidValue;
    
    void set_boxed_value_as_dyn_any(final DynAny p0) throws TypeMismatch;
}
