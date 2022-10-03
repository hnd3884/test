package com.sun.jndi.url.rmi;

import javax.naming.NamingException;
import javax.naming.Name;
import com.sun.jndi.rmi.registry.RegistryContext;
import javax.naming.CompositeName;
import javax.naming.spi.ResolveResult;
import java.util.Hashtable;
import com.sun.jndi.toolkit.url.GenericURLContext;

public class rmiURLContext extends GenericURLContext
{
    public rmiURLContext(final Hashtable<?, ?> hashtable) {
        super(hashtable);
    }
    
    @Override
    protected ResolveResult getRootURLContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        if (!s.startsWith("rmi:")) {
            throw new IllegalArgumentException("rmiURLContext: name is not an RMI URL: " + s);
        }
        String s2 = null;
        int int1 = -1;
        String substring = null;
        int n = 4;
        if (s.startsWith("//", n)) {
            n += 2;
            int n2 = s.indexOf(47, n);
            if (n2 < 0) {
                n2 = s.length();
            }
            int n3;
            if (s.startsWith("[", n)) {
                final int index = s.indexOf(93, n + 1);
                if (index < 0 || index > n2) {
                    throw new IllegalArgumentException("rmiURLContext: name is an Invalid URL: " + s);
                }
                s2 = s.substring(n, index + 1);
                n3 = index + 1;
            }
            else {
                final int index2 = s.indexOf(58, n);
                final int n4 = (index2 < 0 || index2 > n2) ? n2 : index2;
                if (n < n4) {
                    s2 = s.substring(n, n4);
                }
                n3 = n4;
            }
            if (n3 + 1 < n2) {
                if (!s.startsWith(":", n3)) {
                    throw new IllegalArgumentException("rmiURLContext: name is an Invalid URL: " + s);
                }
                ++n3;
                int1 = Integer.parseInt(s.substring(n3, n2));
            }
            n = n2;
        }
        if ("".equals(s2)) {
            s2 = null;
        }
        if (s.startsWith("/", n)) {
            ++n;
        }
        if (n < s.length()) {
            substring = s.substring(n);
        }
        final CompositeName compositeName = new CompositeName();
        if (substring != null) {
            compositeName.add(substring);
        }
        return new ResolveResult(new RegistryContext(s2, int1, hashtable), compositeName);
    }
}
