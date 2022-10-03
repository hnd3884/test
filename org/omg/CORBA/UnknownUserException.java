package org.omg.CORBA;

public final class UnknownUserException extends UserException
{
    public Any except;
    
    public UnknownUserException() {
    }
    
    public UnknownUserException(final Any except) {
        this.except = except;
    }
}
