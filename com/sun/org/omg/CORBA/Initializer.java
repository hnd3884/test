package com.sun.org.omg.CORBA;

import org.omg.CORBA.StructMember;
import org.omg.CORBA.portable.IDLEntity;

public final class Initializer implements IDLEntity
{
    public StructMember[] members;
    public String name;
    
    public Initializer() {
        this.members = null;
        this.name = null;
    }
    
    public Initializer(final StructMember[] members, final String name) {
        this.members = null;
        this.name = null;
        this.members = members;
        this.name = name;
    }
}
