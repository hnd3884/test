package org.apache.commons.collections.bag;

import org.apache.commons.collections.set.TransformedSet;
import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.collection.TransformedCollection;

public class TransformedBag extends TransformedCollection implements Bag
{
    private static final long serialVersionUID = 5421170911299074185L;
    
    public static Bag decorate(final Bag bag, final Transformer transformer) {
        return new TransformedBag(bag, transformer);
    }
    
    protected TransformedBag(final Bag bag, final Transformer transformer) {
        super(bag, transformer);
    }
    
    protected Bag getBag() {
        return (Bag)this.collection;
    }
    
    public int getCount(final Object object) {
        return this.getBag().getCount(object);
    }
    
    public boolean remove(final Object object, final int nCopies) {
        return this.getBag().remove(object, nCopies);
    }
    
    public boolean add(Object object, final int nCopies) {
        object = this.transform(object);
        return this.getBag().add(object, nCopies);
    }
    
    public Set uniqueSet() {
        final Set set = this.getBag().uniqueSet();
        return TransformedSet.decorate(set, this.transformer);
    }
}
