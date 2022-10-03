package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

public interface DynSequenceOperations extends DynAnyOperations
{
    int get_length();
    
    void set_length(final int p0) throws InvalidValue;
    
    Any[] get_elements();
    
    void set_elements(final Any[] p0) throws TypeMismatch, InvalidValue;
    
    DynAny[] get_elements_as_dyn_any();
    
    void set_elements_as_dyn_any(final DynAny[] p0) throws TypeMismatch, InvalidValue;
}
