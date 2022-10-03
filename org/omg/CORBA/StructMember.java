package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class StructMember implements IDLEntity
{
    public String name;
    public TypeCode type;
    public IDLType type_def;
    
    public StructMember() {
    }
    
    public StructMember(final String name, final TypeCode type, final IDLType type_def) {
        this.name = name;
        this.type = type;
        this.type_def = type_def;
    }
}
