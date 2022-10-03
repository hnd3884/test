package com.sun.jndi.rmi.registry;

import java.util.Enumeration;
import javax.naming.ConfigurationException;
import javax.naming.StringRefAddr;
import javax.naming.RefAddr;
import javax.naming.NotContextException;
import com.sun.jndi.url.rmi.rmiURLContextFactory;
import javax.naming.Reference;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public class RegistryContextFactory implements ObjectFactory, InitialContextFactory
{
    public static final String ADDRESS_TYPE = "URL";
    
    @Override
    public Context getInitialContext(Hashtable<?, ?> hashtable) throws NamingException {
        if (hashtable != null) {
            hashtable = (Hashtable)hashtable.clone();
        }
        return URLToContext(getInitCtxURL(hashtable), hashtable);
    }
    
    @Override
    public Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        if (!isRegistryRef(o)) {
            return null;
        }
        final Object urLsToObject = URLsToObject(getURLs((Reference)o), hashtable);
        if (urLsToObject instanceof RegistryContext) {
            ((RegistryContext)urLsToObject).reference = (Reference)o;
        }
        return urLsToObject;
    }
    
    private static Context URLToContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        final Object objectInstance = new rmiURLContextFactory().getObjectInstance(s, null, null, hashtable);
        if (objectInstance instanceof Context) {
            return (Context)objectInstance;
        }
        throw new NotContextException(s);
    }
    
    private static Object URLsToObject(final String[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        return new rmiURLContextFactory().getObjectInstance(array, null, null, hashtable);
    }
    
    private static String getInitCtxURL(final Hashtable<?, ?> hashtable) {
        String s = null;
        if (hashtable != null) {
            s = (String)hashtable.get("java.naming.provider.url");
        }
        return (s != null) ? s : "rmi:";
    }
    
    private static boolean isRegistryRef(final Object o) {
        return o instanceof Reference && RegistryContextFactory.class.getName().equals(((Reference)o).getFactoryClassName());
    }
    
    private static String[] getURLs(final Reference reference) throws NamingException {
        int n = 0;
        final String[] array = new String[reference.size()];
        final Enumeration<RefAddr> all = reference.getAll();
        while (all.hasMoreElements()) {
            final RefAddr refAddr = all.nextElement();
            if (refAddr instanceof StringRefAddr && refAddr.getType().equals("URL")) {
                array[n++] = (String)refAddr.getContent();
            }
        }
        if (n == 0) {
            throw new ConfigurationException("Reference contains no valid addresses");
        }
        if (n == reference.size()) {
            return array;
        }
        final String[] array2 = new String[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
}
