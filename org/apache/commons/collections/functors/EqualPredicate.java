package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class EqualPredicate implements Predicate, Serializable
{
    private static final long serialVersionUID = 5633766978029907089L;
    private final Object iValue;
    
    public static Predicate getInstance(final Object object) {
        if (object == null) {
            return NullPredicate.INSTANCE;
        }
        return new EqualPredicate(object);
    }
    
    public EqualPredicate(final Object object) {
        this.iValue = object;
    }
    
    public boolean evaluate(final Object object) {
        return this.iValue.equals(object);
    }
    
    public Object getValue() {
        return this.iValue;
    }
}
