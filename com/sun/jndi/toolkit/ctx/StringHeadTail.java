package com.sun.jndi.toolkit.ctx;

public class StringHeadTail
{
    private int status;
    private String head;
    private String tail;
    
    public StringHeadTail(final String s, final String s2) {
        this(s, s2, 0);
    }
    
    public StringHeadTail(final String head, final String tail, final int status) {
        this.status = status;
        this.head = head;
        this.tail = tail;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public String getHead() {
        return this.head;
    }
    
    public String getTail() {
        return this.tail;
    }
    
    public int getStatus() {
        return this.status;
    }
}
