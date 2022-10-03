package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;

public class HeadTail
{
    private int status;
    private Name head;
    private Name tail;
    
    public HeadTail(final Name name, final Name name2) {
        this(name, name2, 0);
    }
    
    public HeadTail(final Name head, final Name tail, final int status) {
        this.status = status;
        this.head = head;
        this.tail = tail;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public Name getHead() {
        return this.head;
    }
    
    public Name getTail() {
        return this.tail;
    }
    
    public int getStatus() {
        return this.status;
    }
}
