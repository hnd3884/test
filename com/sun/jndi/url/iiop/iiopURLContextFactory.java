package com.sun.jndi.url.iiop;

import javax.naming.NamingException;
import com.sun.jndi.cosnaming.CNCtx;
import javax.naming.spi.ResolveResult;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class iiopURLContextFactory implements ObjectFactory
{
    @Override
    public Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws Exception {
        if (o == null) {
            return new iiopURLContext(hashtable);
        }
        if (o instanceof String) {
            return getUsingURL((String)o, hashtable);
        }
        if (o instanceof String[]) {
            return getUsingURLs((String[])o, hashtable);
        }
        throw new IllegalArgumentException("iiopURLContextFactory.getObjectInstance: argument must be a URL String or array of URLs");
    }
    
    static ResolveResult getUsingURLIgnoreRest(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        return CNCtx.createUsingURL(s, hashtable);
    }
    
    private static Object getUsingURL(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        final ResolveResult usingURLIgnoreRest = getUsingURLIgnoreRest(s, hashtable);
        final Context context = (Context)usingURLIgnoreRest.getResolvedObj();
        try {
            return context.lookup(usingURLIgnoreRest.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    private static Object getUsingURLs(final String[] array, final Hashtable<?, ?> hashtable) {
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            try {
                final Object usingURL = getUsingURL(s, hashtable);
                if (usingURL != null) {
                    return usingURL;
                }
            }
            catch (final NamingException ex) {}
        }
        return null;
    }
}
