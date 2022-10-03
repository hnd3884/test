package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Equator;
import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class EqualPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = 5633766978029907089L;
    private final T iValue;
    private final Equator<T> equator;
    
    public static <T> Predicate<T> equalPredicate(final T object) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new EqualPredicate<T>(object);
    }
    
    public static <T> Predicate<T> equalPredicate(final T object, final Equator<T> equator) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new EqualPredicate<T>(object, equator);
    }
    
    public EqualPredicate(final T object) {
        this(object, null);
    }
    
    public EqualPredicate(final T object, final Equator<T> equator) {
        this.iValue = object;
        this.equator = equator;
    }
    
    @Override
    public boolean evaluate(final T object) {
        if (this.equator != null) {
            return this.equator.equate(this.iValue, object);
        }
        return this.iValue.equals(object);
    }
    
    public Object getValue() {
        return this.iValue;
    }
}
