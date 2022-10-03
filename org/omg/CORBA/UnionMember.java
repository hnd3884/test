package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class UnionMember implements IDLEntity
{
    public String name;
    public Any label;
    public TypeCode type;
    public IDLType type_def;
    
    public UnionMember() {
    }
    
    public UnionMember(final String name, final Any label, final TypeCode type, final IDLType type_def) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.type_def = type_def;
    }
}
