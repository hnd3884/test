package com.sun.jndi.dns;

import java.util.NoSuchElementException;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.NamingEnumeration;

abstract class BaseNameClassPairEnumeration<T> implements NamingEnumeration<T>
{
    protected Enumeration<NameNode> nodes;
    protected DnsContext ctx;
    
    BaseNameClassPairEnumeration(final DnsContext ctx, final Hashtable<String, NameNode> hashtable) {
        this.ctx = ctx;
        this.nodes = ((hashtable != null) ? hashtable.elements() : null);
    }
    
    @Override
    public final void close() {
        this.nodes = null;
        this.ctx = null;
    }
    
    @Override
    public final boolean hasMore() {
        final boolean b = this.nodes != null && this.nodes.hasMoreElements();
        if (!b) {
            this.close();
        }
        return b;
    }
    
    @Override
    public final boolean hasMoreElements() {
        return this.hasMore();
    }
    
    @Override
    public abstract T next() throws NamingException;
    
    @Override
    public final T nextElement() {
        try {
            return this.next();
        }
        catch (final NamingException ex) {
            final NoSuchElementException ex2 = new NoSuchElementException();
            ex2.initCause(ex);
            throw ex2;
        }
    }
}
