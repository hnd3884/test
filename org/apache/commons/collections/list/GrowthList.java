package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class GrowthList extends AbstractSerializableListDecorator
{
    private static final long serialVersionUID = -3620001881672L;
    
    public static List decorate(final List list) {
        return new GrowthList(list);
    }
    
    public GrowthList() {
        super(new ArrayList());
    }
    
    public GrowthList(final int initialSize) {
        super(new ArrayList(initialSize));
    }
    
    protected GrowthList(final List list) {
        super(list);
    }
    
    public void add(final int index, final Object element) {
        final int size = this.getList().size();
        if (index > size) {
            this.getList().addAll(Collections.nCopies(index - size, (Object)null));
        }
        this.getList().add(index, element);
    }
    
    public boolean addAll(final int index, final Collection coll) {
        final int size = this.getList().size();
        boolean result = false;
        if (index > size) {
            this.getList().addAll(Collections.nCopies(index - size, (Object)null));
            result = true;
        }
        return this.getList().addAll(index, coll) | result;
    }
    
    public Object set(final int index, final Object element) {
        final int size = this.getList().size();
        if (index >= size) {
            this.getList().addAll(Collections.nCopies(index - size + 1, (Object)null));
        }
        return this.getList().set(index, element);
    }
}
