package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class InstanceofPredicate implements Predicate<Object>, Serializable
{
    private static final long serialVersionUID = -6682656911025165584L;
    private final Class<?> iType;
    
    public static Predicate<Object> instanceOfPredicate(final Class<?> type) {
        if (type == null) {
            throw new NullPointerException("The type to check instanceof must not be null");
        }
        return new InstanceofPredicate(type);
    }
    
    public InstanceofPredicate(final Class<?> type) {
        this.iType = type;
    }
    
    @Override
    public boolean evaluate(final Object object) {
        return this.iType.isInstance(object);
    }
    
    public Class<?> getType() {
        return this.iType;
    }
}
