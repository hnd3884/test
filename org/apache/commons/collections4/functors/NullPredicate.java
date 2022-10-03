package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class NullPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = 7533784454832764388L;
    public static final Predicate INSTANCE;
    
    public static <T> Predicate<T> nullPredicate() {
        return NullPredicate.INSTANCE;
    }
    
    private NullPredicate() {
    }
    
    @Override
    public boolean evaluate(final T object) {
        return object == null;
    }
    
    private Object readResolve() {
        return NullPredicate.INSTANCE;
    }
    
    static {
        INSTANCE = new NullPredicate();
    }
}
