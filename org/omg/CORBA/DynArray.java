package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public interface DynArray extends Object, DynAny
{
    Any[] get_elements();
    
    void set_elements(final Any[] p0) throws InvalidSeq;
}
