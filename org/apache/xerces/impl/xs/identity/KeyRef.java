package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.xs.XSIDCDefinition;

public class KeyRef extends IdentityConstraint
{
    protected final UniqueOrKey fKey;
    
    public KeyRef(final String s, final String s2, final UniqueOrKey fKey) {
        super(s, s2);
        this.fKey = fKey;
        this.type = 2;
    }
    
    public UniqueOrKey getKey() {
        return this.fKey;
    }
    
    public XSIDCDefinition getRefKey() {
        return this.fKey;
    }
}
