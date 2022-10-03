package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class NotNullPredicate implements Predicate, Serializable
{
    private static final long serialVersionUID = 7533784454832764388L;
    public static final Predicate INSTANCE;
    
    public static Predicate getInstance() {
        return NotNullPredicate.INSTANCE;
    }
    
    private NotNullPredicate() {
    }
    
    public boolean evaluate(final Object object) {
        return object != null;
    }
    
    static {
        INSTANCE = new NotNullPredicate();
    }
}
