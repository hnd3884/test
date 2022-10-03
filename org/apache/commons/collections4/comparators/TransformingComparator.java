package org.apache.commons.collections4.comparators;

import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;
import java.util.Comparator;

public class TransformingComparator<I, O> implements Comparator<I>, Serializable
{
    private static final long serialVersionUID = 3456940356043606220L;
    private final Comparator<O> decorated;
    private final Transformer<? super I, ? extends O> transformer;
    
    public TransformingComparator(final Transformer<? super I, ? extends O> transformer) {
        this(transformer, ComparatorUtils.NATURAL_COMPARATOR);
    }
    
    public TransformingComparator(final Transformer<? super I, ? extends O> transformer, final Comparator<O> decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }
    
    @Override
    public int compare(final I obj1, final I obj2) {
        final O value1 = (O)this.transformer.transform(obj1);
        final O value2 = (O)this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }
    
    @Override
    public int hashCode() {
        int total = 17;
        total = total * 37 + ((this.decorated == null) ? 0 : this.decorated.hashCode());
        total = total * 37 + ((this.transformer == null) ? 0 : this.transformer.hashCode());
        return total;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            final TransformingComparator<?, ?> comp = (TransformingComparator<?, ?>)object;
            if (null == this.decorated) {
                if (null != comp.decorated) {
                    return false;
                }
            }
            else if (!this.decorated.equals(comp.decorated)) {
                return false;
            }
            if ((null != this.transformer) ? this.transformer.equals(comp.transformer) : (null == comp.transformer)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
