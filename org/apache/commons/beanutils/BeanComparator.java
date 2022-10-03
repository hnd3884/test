package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections.comparators.ComparableComparator;
import java.io.Serializable;
import java.util.Comparator;

public class BeanComparator<T> implements Comparator<T>, Serializable
{
    private String property;
    private final Comparator<?> comparator;
    
    public BeanComparator() {
        this(null);
    }
    
    public BeanComparator(final String property) {
        this(property, (Comparator)ComparableComparator.getInstance());
    }
    
    public BeanComparator(final String property, final Comparator<?> comparator) {
        this.setProperty(property);
        if (comparator != null) {
            this.comparator = comparator;
        }
        else {
            this.comparator = (Comparator<?>)ComparableComparator.getInstance();
        }
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    public String getProperty() {
        return this.property;
    }
    
    public Comparator<?> getComparator() {
        return this.comparator;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        if (this.property == null) {
            return this.internalCompare(o1, o2);
        }
        try {
            final Object value1 = PropertyUtils.getProperty(o1, this.property);
            final Object value2 = PropertyUtils.getProperty(o2, this.property);
            return this.internalCompare(value1, value2);
        }
        catch (final IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException: " + iae.toString());
        }
        catch (final InvocationTargetException ite) {
            throw new RuntimeException("InvocationTargetException: " + ite.toString());
        }
        catch (final NoSuchMethodException nsme) {
            throw new RuntimeException("NoSuchMethodException: " + nsme.toString());
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanComparator)) {
            return false;
        }
        final BeanComparator<?> beanComparator = (BeanComparator<?>)o;
        if (!this.comparator.equals(beanComparator.comparator)) {
            return false;
        }
        if (this.property != null) {
            return this.property.equals(beanComparator.property);
        }
        return beanComparator.property == null;
    }
    
    @Override
    public int hashCode() {
        final int result = this.comparator.hashCode();
        return result;
    }
    
    private int internalCompare(final Object val1, final Object val2) {
        final Comparator c = this.comparator;
        return c.compare(val1, val2);
    }
}
