package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class FalsePredicate implements Predicate, Serializable
{
    private static final long serialVersionUID = 7533784454832764388L;
    public static final Predicate INSTANCE;
    
    public static Predicate getInstance() {
        return FalsePredicate.INSTANCE;
    }
    
    private FalsePredicate() {
    }
    
    public boolean evaluate(final Object object) {
        return false;
    }
    
    static {
        INSTANCE = new FalsePredicate();
    }
}
