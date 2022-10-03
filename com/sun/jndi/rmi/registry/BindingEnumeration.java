package com.sun.jndi.rmi.registry;

import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.CompositeName;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;

class BindingEnumeration implements NamingEnumeration<Binding>
{
    private RegistryContext ctx;
    private final String[] names;
    private int nextName;
    
    BindingEnumeration(final RegistryContext registryContext, final String[] names) {
        this.ctx = new RegistryContext(registryContext);
        this.names = names;
        this.nextName = 0;
    }
    
    @Override
    protected void finalize() {
        this.ctx.close();
    }
    
    @Override
    public boolean hasMore() {
        if (this.nextName >= this.names.length) {
            this.ctx.close();
        }
        return this.nextName < this.names.length;
    }
    
    @Override
    public Binding next() throws NamingException {
        if (!this.hasMore()) {
            throw new NoSuchElementException();
        }
        final Name add = new CompositeName().add(this.names[this.nextName++]);
        final Object lookup = this.ctx.lookup(add);
        final String string = add.toString();
        final Binding binding = new Binding(string, lookup);
        binding.setNameInNamespace(string);
        return binding;
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.hasMore();
    }
    
    @Override
    public Binding nextElement() {
        try {
            return this.next();
        }
        catch (final NamingException ex) {
            throw new NoSuchElementException("javax.naming.NamingException was thrown");
        }
    }
    
    @Override
    public void close() {
        this.finalize();
    }
}
