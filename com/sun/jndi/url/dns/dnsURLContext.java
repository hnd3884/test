package com.sun.jndi.url.dns;

import javax.naming.NamingException;
import javax.naming.CompositeName;
import com.sun.jndi.dns.DnsContextFactory;
import java.net.MalformedURLException;
import javax.naming.InvalidNameException;
import com.sun.jndi.dns.DnsUrl;
import javax.naming.spi.ResolveResult;
import java.util.Hashtable;
import com.sun.jndi.toolkit.url.GenericURLDirContext;

public class dnsURLContext extends GenericURLDirContext
{
    public dnsURLContext(final Hashtable<?, ?> hashtable) {
        super(hashtable);
    }
    
    @Override
    protected ResolveResult getRootURLContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        DnsUrl dnsUrl;
        try {
            dnsUrl = new DnsUrl(s);
        }
        catch (final MalformedURLException ex) {
            throw new InvalidNameException(ex.getMessage());
        }
        return new ResolveResult(DnsContextFactory.getContext(".", new DnsUrl[] { dnsUrl }, hashtable), new CompositeName().add(dnsUrl.getDomain()));
    }
}
