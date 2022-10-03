package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public interface DynValue extends Object, DynAny
{
    String current_member_name();
    
    TCKind current_member_kind();
    
    NameValuePair[] get_members();
    
    void set_members(final NameValuePair[] p0) throws InvalidSeq;
}
