package com.sun.jndi.url.rmi;

import javax.naming.NamingException;
import javax.naming.ConfigurationException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class rmiURLContextFactory implements ObjectFactory
{
    @Override
    public Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        if (o == null) {
            return new rmiURLContext(hashtable);
        }
        if (o instanceof String) {
            return getUsingURL((String)o, hashtable);
        }
        if (o instanceof String[]) {
            return getUsingURLs((String[])o, hashtable);
        }
        throw new ConfigurationException("rmiURLContextFactory.getObjectInstance: argument must be an RMI URL String or an array of them");
    }
    
    private static Object getUsingURL(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        final rmiURLContext rmiURLContext = new rmiURLContext(hashtable);
        try {
            return rmiURLContext.lookup(s);
        }
        finally {
            rmiURLContext.close();
        }
    }
    
    private static Object getUsingURLs(final String[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        if (array.length == 0) {
            throw new ConfigurationException("rmiURLContextFactory: empty URL array");
        }
        final rmiURLContext rmiURLContext = new rmiURLContext(hashtable);
        try {
            NamingException ex = null;
            int i = 0;
            while (i < array.length) {
                try {
                    return rmiURLContext.lookup(array[i]);
                }
                catch (final NamingException ex2) {
                    ex = ex2;
                    ++i;
                    continue;
                }
                break;
            }
            throw ex;
        }
        finally {
            rmiURLContext.close();
        }
    }
}
