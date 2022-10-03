package org.omg.Dynamic;

import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.IDLEntity;

public final class Parameter implements IDLEntity
{
    public Any argument;
    public ParameterMode mode;
    
    public Parameter() {
        this.argument = null;
        this.mode = null;
    }
    
    public Parameter(final Any argument, final ParameterMode mode) {
        this.argument = null;
        this.mode = null;
        this.argument = argument;
        this.mode = mode;
    }
}
