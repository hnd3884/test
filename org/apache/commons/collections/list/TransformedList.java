package org.apache.commons.collections.list;

import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;
import java.util.ListIterator;
import java.util.Collection;
import org.apache.commons.collections.Transformer;
import java.util.List;
import org.apache.commons.collections.collection.TransformedCollection;

public class TransformedList extends TransformedCollection implements List
{
    private static final long serialVersionUID = 1077193035000013141L;
    
    public static List decorate(final List list, final Transformer transformer) {
        return new TransformedList(list, transformer);
    }
    
    protected TransformedList(final List list, final Transformer transformer) {
        super(list, transformer);
    }
    
    protected List getList() {
        return (List)this.collection;
    }
    
    public Object get(final int index) {
        return this.getList().get(index);
    }
    
    public int indexOf(final Object object) {
        return this.getList().indexOf(object);
    }
    
    public int lastIndexOf(final Object object) {
        return this.getList().lastIndexOf(object);
    }
    
    public Object remove(final int index) {
        return this.getList().remove(index);
    }
    
    public void add(final int index, Object object) {
        object = this.transform(object);
        this.getList().add(index, object);
    }
    
    public boolean addAll(final int index, Collection coll) {
        coll = this.transform(coll);
        return this.getList().addAll(index, coll);
    }
    
    public ListIterator listIterator() {
        return this.listIterator(0);
    }
    
    public ListIterator listIterator(final int i) {
        return new TransformedListIterator(this.getList().listIterator(i));
    }
    
    public Object set(final int index, Object object) {
        object = this.transform(object);
        return this.getList().set(index, object);
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        final List sub = this.getList().subList(fromIndex, toIndex);
        return new TransformedList(sub, this.transformer);
    }
    
    protected class TransformedListIterator extends AbstractListIteratorDecorator
    {
        protected TransformedListIterator(final ListIterator iterator) {
            super(iterator);
        }
        
        public void add(Object object) {
            object = TransformedCollection.this.transform(object);
            this.iterator.add(object);
        }
        
        public void set(Object object) {
            object = TransformedCollection.this.transform(object);
            this.iterator.set(object);
        }
    }
}
