package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class OperationDescription implements IDLEntity
{
    public String name;
    public String id;
    public String defined_in;
    public String version;
    public TypeCode result;
    public OperationMode mode;
    public String[] contexts;
    public ParameterDescription[] parameters;
    public ExceptionDescription[] exceptions;
    
    public OperationDescription() {
        this.name = null;
        this.id = null;
        this.defined_in = null;
        this.version = null;
        this.result = null;
        this.mode = null;
        this.contexts = null;
        this.parameters = null;
        this.exceptions = null;
    }
    
    public OperationDescription(final String name, final String id, final String defined_in, final String version, final TypeCode result, final OperationMode mode, final String[] contexts, final ParameterDescription[] parameters, final ExceptionDescription[] exceptions) {
        this.name = null;
        this.id = null;
        this.defined_in = null;
        this.version = null;
        this.result = null;
        this.mode = null;
        this.contexts = null;
        this.parameters = null;
        this.exceptions = null;
        this.name = name;
        this.id = id;
        this.defined_in = defined_in;
        this.version = version;
        this.result = result;
        this.mode = mode;
        this.contexts = contexts;
        this.parameters = parameters;
        this.exceptions = exceptions;
    }
}
