package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class TruePredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = 3374767158756189740L;
    public static final Predicate INSTANCE;
    
    public static <T> Predicate<T> truePredicate() {
        return TruePredicate.INSTANCE;
    }
    
    private TruePredicate() {
    }
    
    @Override
    public boolean evaluate(final T object) {
        return true;
    }
    
    private Object readResolve() {
        return TruePredicate.INSTANCE;
    }
    
    static {
        INSTANCE = new TruePredicate();
    }
}
