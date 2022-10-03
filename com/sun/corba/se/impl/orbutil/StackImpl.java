package com.sun.corba.se.impl.orbutil;

import java.util.EmptyStackException;

public class StackImpl
{
    private Object[] data;
    private int top;
    
    public StackImpl() {
        this.data = new Object[3];
        this.top = -1;
    }
    
    public final boolean empty() {
        return this.top == -1;
    }
    
    public final Object peek() {
        if (this.empty()) {
            throw new EmptyStackException();
        }
        return this.data[this.top];
    }
    
    public final Object pop() {
        final Object peek = this.peek();
        this.data[this.top] = null;
        --this.top;
        return peek;
    }
    
    private void ensure() {
        if (this.top == this.data.length - 1) {
            final Object[] data = new Object[2 * this.data.length];
            System.arraycopy(this.data, 0, data, 0, this.data.length);
            this.data = data;
        }
    }
    
    public final Object push(final Object o) {
        this.ensure();
        ++this.top;
        return this.data[this.top] = o;
    }
}
