package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ValueMember implements IDLEntity
{
    public String name;
    public String id;
    public String defined_in;
    public String version;
    public TypeCode type;
    public IDLType type_def;
    public short access;
    
    public ValueMember() {
    }
    
    public ValueMember(final String name, final String id, final String defined_in, final String version, final TypeCode type, final IDLType type_def, final short access) {
        this.name = name;
        this.id = id;
        this.defined_in = defined_in;
        this.version = version;
        this.type = type;
        this.type_def = type_def;
        this.access = access;
    }
}
