package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class IdentityPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = -89901658494523293L;
    private final T iValue;
    
    public static <T> Predicate<T> identityPredicate(final T object) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new IdentityPredicate<T>(object);
    }
    
    public IdentityPredicate(final T object) {
        this.iValue = object;
    }
    
    @Override
    public boolean evaluate(final T object) {
        return this.iValue == object;
    }
    
    public T getValue() {
        return this.iValue;
    }
}
