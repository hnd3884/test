package com.sun.jndi.dns;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.Context;
import javax.naming.spi.DirectoryManager;
import javax.naming.CompositeName;
import java.util.NoSuchElementException;
import java.util.Hashtable;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;

final class BindingEnumeration extends BaseNameClassPairEnumeration<Binding> implements NamingEnumeration<Binding>
{
    BindingEnumeration(final DnsContext dnsContext, final Hashtable<String, NameNode> hashtable) {
        super(dnsContext, hashtable);
    }
    
    @Override
    public Binding next() throws NamingException {
        if (!this.hasMore()) {
            throw new NoSuchElementException();
        }
        final Name add = new DnsName().add(this.nodes.nextElement().getLabel());
        final Name add2 = new CompositeName().add(add.toString());
        final String string = add2.toString();
        final DnsContext dnsContext = new DnsContext(this.ctx, this.ctx.fullyQualify(add));
        try {
            final Binding binding = new Binding(string, DirectoryManager.getObjectInstance(dnsContext, add2, this.ctx, dnsContext.environment, null));
            binding.setNameInNamespace(this.ctx.fullyQualify(add2).toString());
            return binding;
        }
        catch (final Exception rootCause) {
            final NamingException ex = new NamingException("Problem generating object using object factory");
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
}
