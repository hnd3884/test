package org.apache.commons.collections4.functors;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public class ComparatorPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = -1863209236504077399L;
    private final T object;
    private final Comparator<T> comparator;
    private final Criterion criterion;
    
    public static <T> Predicate<T> comparatorPredicate(final T object, final Comparator<T> comparator) {
        return comparatorPredicate(object, comparator, Criterion.EQUAL);
    }
    
    public static <T> Predicate<T> comparatorPredicate(final T object, final Comparator<T> comparator, final Criterion criterion) {
        if (comparator == null) {
            throw new NullPointerException("Comparator must not be null.");
        }
        if (criterion == null) {
            throw new NullPointerException("Criterion must not be null.");
        }
        return new ComparatorPredicate<T>(object, comparator, criterion);
    }
    
    public ComparatorPredicate(final T object, final Comparator<T> comparator, final Criterion criterion) {
        this.object = object;
        this.comparator = comparator;
        this.criterion = criterion;
    }
    
    @Override
    public boolean evaluate(final T target) {
        boolean result = false;
        final int comparison = this.comparator.compare(this.object, target);
        switch (this.criterion) {
            case EQUAL: {
                result = (comparison == 0);
                break;
            }
            case GREATER: {
                result = (comparison > 0);
                break;
            }
            case LESS: {
                result = (comparison < 0);
                break;
            }
            case GREATER_OR_EQUAL: {
                result = (comparison >= 0);
                break;
            }
            case LESS_OR_EQUAL: {
                result = (comparison <= 0);
                break;
            }
            default: {
                throw new IllegalStateException("The current criterion '" + this.criterion + "' is invalid.");
            }
        }
        return result;
    }
    
    public enum Criterion
    {
        EQUAL, 
        GREATER, 
        LESS, 
        GREATER_OR_EQUAL, 
        LESS_OR_EQUAL;
    }
}
