package com.sun.org.apache.xerces.internal.impl.dv;

public class InvalidDatatypeFacetException extends DatatypeException
{
    static final long serialVersionUID = -4104066085909970654L;
    
    public InvalidDatatypeFacetException(final String key, final Object[] args) {
        super(key, args);
    }
}
