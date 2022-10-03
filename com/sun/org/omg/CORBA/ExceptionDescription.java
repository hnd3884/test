package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ExceptionDescription implements IDLEntity
{
    public String name;
    public String id;
    public String defined_in;
    public String version;
    public TypeCode type;
    
    public ExceptionDescription() {
        this.name = null;
        this.id = null;
        this.defined_in = null;
        this.version = null;
        this.type = null;
    }
    
    public ExceptionDescription(final String name, final String id, final String defined_in, final String version, final TypeCode type) {
        this.name = null;
        this.id = null;
        this.defined_in = null;
        this.version = null;
        this.type = null;
        this.name = name;
        this.id = id;
        this.defined_in = defined_in;
        this.version = version;
        this.type = type;
    }
}
