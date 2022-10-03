package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class ExceptionPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Predicate INSTANCE;
    
    public static <T> Predicate<T> exceptionPredicate() {
        return ExceptionPredicate.INSTANCE;
    }
    
    private ExceptionPredicate() {
    }
    
    @Override
    public boolean evaluate(final T object) {
        throw new FunctorException("ExceptionPredicate invoked");
    }
    
    private Object readResolve() {
        return ExceptionPredicate.INSTANCE;
    }
    
    static {
        INSTANCE = new ExceptionPredicate();
    }
}
