package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ORBAlreadyRegistered extends UserException
{
    public String orbId;
    
    public ORBAlreadyRegistered() {
        super(ORBAlreadyRegisteredHelper.id());
        this.orbId = null;
    }
    
    public ORBAlreadyRegistered(final String orbId) {
        super(ORBAlreadyRegisteredHelper.id());
        this.orbId = null;
        this.orbId = orbId;
    }
    
    public ORBAlreadyRegistered(final String s, final String orbId) {
        super(ORBAlreadyRegisteredHelper.id() + "  " + s);
        this.orbId = null;
        this.orbId = orbId;
    }
}
