package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Equator;

public class DefaultEquator<T> implements Equator<T>, Serializable
{
    private static final long serialVersionUID = 825802648423525485L;
    public static final DefaultEquator INSTANCE;
    public static final int HASHCODE_NULL = -1;
    
    public static <T> DefaultEquator<T> defaultEquator() {
        return DefaultEquator.INSTANCE;
    }
    
    private DefaultEquator() {
    }
    
    @Override
    public boolean equate(final T o1, final T o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
    
    @Override
    public int hash(final T o) {
        return (o == null) ? -1 : o.hashCode();
    }
    
    private Object readResolve() {
        return DefaultEquator.INSTANCE;
    }
    
    static {
        INSTANCE = new DefaultEquator();
    }
}
