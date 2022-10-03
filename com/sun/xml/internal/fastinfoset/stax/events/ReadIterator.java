package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;

public class ReadIterator implements Iterator
{
    Iterator iterator;
    
    public ReadIterator() {
        this.iterator = EmptyIterator.getInstance();
    }
    
    public ReadIterator(final Iterator iterator) {
        this.iterator = EmptyIterator.getInstance();
        if (iterator != null) {
            this.iterator = iterator;
        }
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    @Override
    public Object next() {
        return this.iterator.next();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.readonlyList"));
    }
}
