package org.apache.xerces.impl.xs.identity;

public class UniqueOrKey extends IdentityConstraint
{
    public UniqueOrKey(final String s, final String s2, final short type) {
        super(s, s2);
        this.type = type;
    }
}
