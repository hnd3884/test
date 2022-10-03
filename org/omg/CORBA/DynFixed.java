package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

@Deprecated
public interface DynFixed extends Object, DynAny
{
    byte[] get_value();
    
    void set_value(final byte[] p0) throws InvalidValue;
}
