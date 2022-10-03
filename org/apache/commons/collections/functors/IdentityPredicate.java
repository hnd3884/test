package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class IdentityPredicate implements Predicate, Serializable
{
    private static final long serialVersionUID = -89901658494523293L;
    private final Object iValue;
    
    public static Predicate getInstance(final Object object) {
        if (object == null) {
            return NullPredicate.INSTANCE;
        }
        return new IdentityPredicate(object);
    }
    
    public IdentityPredicate(final Object object) {
        this.iValue = object;
    }
    
    public boolean evaluate(final Object object) {
        return this.iValue == object;
    }
    
    public Object getValue() {
        return this.iValue;
    }
}
