package org.apache.commons.collections.bag;

import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractBagDecorator extends AbstractCollectionDecorator implements Bag
{
    protected AbstractBagDecorator() {
    }
    
    protected AbstractBagDecorator(final Bag bag) {
        super(bag);
    }
    
    protected Bag getBag() {
        return (Bag)this.getCollection();
    }
    
    public int getCount(final Object object) {
        return this.getBag().getCount(object);
    }
    
    public boolean add(final Object object, final int count) {
        return this.getBag().add(object, count);
    }
    
    public boolean remove(final Object object, final int count) {
        return this.getBag().remove(object, count);
    }
    
    public Set uniqueSet() {
        return this.getBag().uniqueSet();
    }
}
