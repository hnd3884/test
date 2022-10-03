package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class NotNullPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = 7533784454832764388L;
    public static final Predicate INSTANCE;
    
    public static <T> Predicate<T> notNullPredicate() {
        return NotNullPredicate.INSTANCE;
    }
    
    private NotNullPredicate() {
    }
    
    @Override
    public boolean evaluate(final T object) {
        return object != null;
    }
    
    private Object readResolve() {
        return NotNullPredicate.INSTANCE;
    }
    
    static {
        INSTANCE = new NotNullPredicate();
    }
}
