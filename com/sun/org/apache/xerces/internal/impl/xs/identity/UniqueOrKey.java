package com.sun.org.apache.xerces.internal.impl.xs.identity;

public class UniqueOrKey extends IdentityConstraint
{
    public UniqueOrKey(final String namespace, final String identityConstraintName, final String elemName, final short type) {
        super(namespace, identityConstraintName, elemName);
        this.type = type;
    }
}
