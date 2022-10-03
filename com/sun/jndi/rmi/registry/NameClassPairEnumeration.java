package com.sun.jndi.rmi.registry;

import javax.naming.NamingException;
import javax.naming.CompositeName;
import java.util.NoSuchElementException;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

class NameClassPairEnumeration implements NamingEnumeration<NameClassPair>
{
    private final String[] names;
    private int nextName;
    
    NameClassPairEnumeration(final String[] names) {
        this.names = names;
        this.nextName = 0;
    }
    
    @Override
    public boolean hasMore() {
        return this.nextName < this.names.length;
    }
    
    @Override
    public NameClassPair next() throws NamingException {
        if (!this.hasMore()) {
            throw new NoSuchElementException();
        }
        final String nameInNamespace = this.names[this.nextName++];
        final NameClassPair nameClassPair = new NameClassPair(new CompositeName().add(nameInNamespace).toString(), "java.lang.Object");
        nameClassPair.setNameInNamespace(nameInNamespace);
        return nameClassPair;
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.hasMore();
    }
    
    @Override
    public NameClassPair nextElement() {
        try {
            return this.next();
        }
        catch (final NamingException ex) {
            throw new NoSuchElementException("javax.naming.NamingException was thrown");
        }
    }
    
    @Override
    public void close() {
        this.nextName = this.names.length;
    }
}
