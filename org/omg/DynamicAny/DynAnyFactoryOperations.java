package org.omg.DynamicAny;

import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.CORBA.Any;

public interface DynAnyFactoryOperations
{
    DynAny create_dyn_any(final Any p0) throws InconsistentTypeCode;
    
    DynAny create_dyn_any_from_type_code(final TypeCode p0) throws InconsistentTypeCode;
}
