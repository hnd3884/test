package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class BadServerDefinition extends UserException
{
    public String reason;
    
    public BadServerDefinition() {
        super(BadServerDefinitionHelper.id());
        this.reason = null;
    }
    
    public BadServerDefinition(final String reason) {
        super(BadServerDefinitionHelper.id());
        this.reason = null;
        this.reason = reason;
    }
    
    public BadServerDefinition(final String s, final String reason) {
        super(BadServerDefinitionHelper.id() + "  " + s);
        this.reason = null;
        this.reason = reason;
    }
}
