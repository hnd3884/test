package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class EmptyIterator implements Iterator
{
    public static final EmptyIterator instance;
    
    private EmptyIterator() {
    }
    
    public static EmptyIterator getInstance() {
        return EmptyIterator.instance;
    }
    
    @Override
    public boolean hasNext() {
        return false;
    }
    
    @Override
    public Object next() throws NoSuchElementException {
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.emptyIterator"));
    }
    
    static {
        instance = new EmptyIterator();
    }
}
