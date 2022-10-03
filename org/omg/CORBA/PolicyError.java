package org.omg.CORBA;

public final class PolicyError extends UserException
{
    public short reason;
    
    public PolicyError() {
    }
    
    public PolicyError(final short reason) {
        this.reason = reason;
    }
    
    public PolicyError(final String s, final short reason) {
        super(s);
        this.reason = reason;
    }
}
