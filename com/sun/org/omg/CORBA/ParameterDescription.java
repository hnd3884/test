package com.sun.org.omg.CORBA;

import org.omg.CORBA.IDLType;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ParameterDescription implements IDLEntity
{
    public String name;
    public TypeCode type;
    public IDLType type_def;
    public ParameterMode mode;
    
    public ParameterDescription() {
        this.name = null;
        this.type = null;
        this.type_def = null;
        this.mode = null;
    }
    
    public ParameterDescription(final String name, final TypeCode type, final IDLType type_def, final ParameterMode mode) {
        this.name = null;
        this.type = null;
        this.type_def = null;
        this.mode = null;
        this.name = name;
        this.type = type;
        this.type_def = type_def;
        this.mode = mode;
    }
}
