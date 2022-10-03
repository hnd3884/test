package org.omg.DynamicAny;

import org.omg.CORBA.portable.IDLEntity;

public final class NameDynAnyPair implements IDLEntity
{
    public String id;
    public DynAny value;
    
    public NameDynAnyPair() {
        this.id = null;
        this.value = null;
    }
    
    public NameDynAnyPair(final String id, final DynAny value) {
        this.id = null;
        this.value = null;
        this.id = id;
        this.value = value;
    }
}
