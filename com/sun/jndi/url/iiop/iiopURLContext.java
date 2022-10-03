package com.sun.jndi.url.iiop;

import javax.naming.InvalidNameException;
import java.net.MalformedURLException;
import com.sun.jndi.cosnaming.CorbanameUrl;
import com.sun.jndi.cosnaming.IiopUrl;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;
import java.util.Hashtable;
import com.sun.jndi.toolkit.url.GenericURLContext;

public class iiopURLContext extends GenericURLContext
{
    iiopURLContext(final Hashtable<?, ?> hashtable) {
        super(hashtable);
    }
    
    @Override
    protected ResolveResult getRootURLContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        return iiopURLContextFactory.getUsingURLIgnoreRest(s, hashtable);
    }
    
    @Override
    protected Name getURLSuffix(final String s, final String s2) throws NamingException {
        try {
            if (s2.startsWith("iiop://") || s2.startsWith("iiopname://")) {
                return new IiopUrl(s2).getCosName();
            }
            if (s2.startsWith("corbaname:")) {
                return new CorbanameUrl(s2).getCosName();
            }
            throw new MalformedURLException("Not a valid URL: " + s2);
        }
        catch (final MalformedURLException ex) {
            throw new InvalidNameException(ex.getMessage());
        }
    }
}
