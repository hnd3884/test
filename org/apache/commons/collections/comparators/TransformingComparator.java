package org.apache.commons.collections.comparators;

import org.apache.commons.collections.Transformer;
import java.util.Comparator;

public class TransformingComparator implements Comparator
{
    protected Comparator decorated;
    protected Transformer transformer;
    
    public TransformingComparator(final Transformer transformer) {
        this(transformer, new ComparableComparator());
    }
    
    public TransformingComparator(final Transformer transformer, final Comparator decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }
    
    public int compare(final Object obj1, final Object obj2) {
        final Object value1 = this.transformer.transform(obj1);
        final Object value2 = this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }
}
