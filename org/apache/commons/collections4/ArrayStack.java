package org.apache.commons.collections4;

import java.util.EmptyStackException;
import java.util.ArrayList;

@Deprecated
public class ArrayStack<E> extends ArrayList<E>
{
    private static final long serialVersionUID = 2130079159931574599L;
    
    public ArrayStack() {
    }
    
    public ArrayStack(final int initialSize) {
        super(initialSize);
    }
    
    public boolean empty() {
        return this.isEmpty();
    }
    
    public E peek() throws EmptyStackException {
        final int n = this.size();
        if (n <= 0) {
            throw new EmptyStackException();
        }
        return this.get(n - 1);
    }
    
    public E peek(final int n) throws EmptyStackException {
        final int m = this.size() - n - 1;
        if (m < 0) {
            throw new EmptyStackException();
        }
        return this.get(m);
    }
    
    public E pop() throws EmptyStackException {
        final int n = this.size();
        if (n <= 0) {
            throw new EmptyStackException();
        }
        return this.remove(n - 1);
    }
    
    public E push(final E item) {
        this.add(item);
        return item;
    }
    
    public int search(final Object object) {
        for (int i = this.size() - 1, n = 1; i >= 0; --i, ++n) {
            final Object current = this.get(i);
            if ((object == null && current == null) || (object != null && object.equals(current))) {
                return n;
            }
        }
        return -1;
    }
}
