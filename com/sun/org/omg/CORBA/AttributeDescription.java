package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class AttributeDescription implements IDLEntity
{
    public String name;
    public String id;
    public String defined_in;
    public String version;
    public TypeCode type;
    public AttributeMode mode;
    
    public AttributeDescription() {
        this.name = null;
        this.id = null;
        this.defined_in = null;
        this.version = null;
        this.type = null;
        this.mode = null;
    }
    
    public AttributeDescription(final String name, final String id, final String defined_in, final String version, final TypeCode type, final AttributeMode mode) {
        this.name = null;
        this.id = null;
        this.defined_in = null;
        this.version = null;
        this.type = null;
        this.mode = null;
        this.name = name;
        this.id = id;
        this.defined_in = defined_in;
        this.version = version;
        this.type = type;
        this.mode = mode;
    }
}
