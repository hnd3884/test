package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.XSIDCDefinition;

public class KeyRef extends IdentityConstraint
{
    protected UniqueOrKey fKey;
    
    public KeyRef(final String namespace, final String identityConstraintName, final String elemName, final UniqueOrKey key) {
        super(namespace, identityConstraintName, elemName);
        this.fKey = key;
        this.type = 2;
    }
    
    public UniqueOrKey getKey() {
        return this.fKey;
    }
    
    @Override
    public XSIDCDefinition getRefKey() {
        return this.fKey;
    }
}
