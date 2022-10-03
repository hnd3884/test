package org.apache.commons.collections4.functors;

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class UniquePredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = -3319417438027438040L;
    private final Set<T> iSet;
    
    public static <T> Predicate<T> uniquePredicate() {
        return new UniquePredicate<T>();
    }
    
    public UniquePredicate() {
        this.iSet = new HashSet<T>();
    }
    
    @Override
    public boolean evaluate(final T object) {
        return this.iSet.add(object);
    }
}
