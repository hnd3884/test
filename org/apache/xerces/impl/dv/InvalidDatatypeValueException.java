package org.apache.xerces.impl.dv;

public class InvalidDatatypeValueException extends DatatypeException
{
    static final long serialVersionUID = -5523739426958236125L;
    
    public InvalidDatatypeValueException(final String s, final Object[] array) {
        super(s, array);
    }
}
