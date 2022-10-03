package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class GrowthList<E> extends AbstractSerializableListDecorator<E>
{
    private static final long serialVersionUID = -3620001881672L;
    
    public static <E> GrowthList<E> growthList(final List<E> list) {
        return new GrowthList<E>(list);
    }
    
    public GrowthList() {
        super(new ArrayList());
    }
    
    public GrowthList(final int initialSize) {
        super(new ArrayList(initialSize));
    }
    
    protected GrowthList(final List<E> list) {
        super(list);
    }
    
    @Override
    public void add(final int index, final E element) {
        final int size = this.decorated().size();
        if (index > size) {
            this.decorated().addAll((Collection<? extends E>)Collections.nCopies(index - size, (Object)null));
        }
        this.decorated().add(index, element);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        final int size = this.decorated().size();
        boolean result = false;
        if (index > size) {
            this.decorated().addAll((Collection<? extends E>)Collections.nCopies(index - size, (Object)null));
            result = true;
        }
        return this.decorated().addAll(index, coll) | result;
    }
    
    @Override
    public E set(final int index, final E element) {
        final int size = this.decorated().size();
        if (index >= size) {
            this.decorated().addAll((Collection<? extends E>)Collections.nCopies(index - size + 1, (Object)null));
        }
        return this.decorated().set(index, element);
    }
}
