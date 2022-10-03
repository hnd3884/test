package com.sun.jndi.ldap;

import javax.naming.directory.BasicAttribute;
import java.util.Vector;
import javax.naming.directory.Attribute;
import java.util.Iterator;
import com.sun.jndi.ldap.spi.LdapDnsProviderResult;
import javax.naming.directory.DirContext;
import java.util.Enumeration;
import javax.naming.StringRefAddr;
import javax.naming.RefAddr;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ConfigurationException;
import javax.naming.Reference;
import com.sun.jndi.url.ldap.ldapURLContextFactory;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public final class LdapCtxFactory implements ObjectFactory, InitialContextFactory
{
    public static final String ADDRESS_TYPE = "URL";
    
    @Override
    public Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws Exception {
        if (!isLdapRef(o)) {
            return null;
        }
        return new ldapURLContextFactory().getObjectInstance(getURLs((Reference)o), name, context, hashtable);
    }
    
    @Override
    public Context getInitialContext(final Hashtable<?, ?> hashtable) throws NamingException {
        try {
            final String s = (hashtable != null) ? ((String)hashtable.get("java.naming.provider.url")) : null;
            if (s == null) {
                return new LdapCtx("", "localhost", 389, hashtable, false);
            }
            final String[] fromList = LdapURL.fromList(s);
            if (fromList.length == 0) {
                throw new ConfigurationException("java.naming.provider.url property does not contain a URL");
            }
            return getLdapCtxInstance(fromList, hashtable);
        }
        catch (final LdapReferralException ex) {
            if (hashtable != null && "throw".equals(hashtable.get("java.naming.referral"))) {
                throw ex;
            }
            return ex.getReferralContext(hashtable, (Control[])((hashtable != null) ? ((Control[])(Object)hashtable.get("java.naming.ldap.control.connect")) : null));
        }
    }
    
    private static boolean isLdapRef(final Object o) {
        return o instanceof Reference && LdapCtxFactory.class.getName().equals(((Reference)o).getFactoryClassName());
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
    
    public static DirContext getLdapCtxInstance(final Object o, final Hashtable<?, ?> hashtable) throws NamingException {
        if (o instanceof String) {
            return getUsingURL((String)o, hashtable);
        }
        if (o instanceof String[]) {
            return getUsingURLs((String[])o, hashtable);
        }
        throw new IllegalArgumentException("argument must be an LDAP URL String or array of them");
    }
    
    private static DirContext getUsingURL(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        try {
            final LdapDnsProviderResult lookupEndpoints = LdapDnsProviderService.getInstance().lookupEndpoints(s, hashtable);
            NamingException ex = null;
            for (final String s2 : lookupEndpoints.getEndpoints()) {
                try {
                    return getLdapCtxFromUrl(lookupEndpoints.getDomainName(), s, new LdapURL(s2), hashtable);
                }
                catch (final NamingException ex2) {
                    ex = ex2;
                    continue;
                }
                break;
            }
            if (ex != null) {
                throw ex;
            }
            throw new NamingException("Could not resolve a valid ldap host");
        }
        catch (final NamingException ex3) {
            throw ex3;
        }
        catch (final Exception rootCause) {
            final NamingException ex4 = new NamingException();
            ex4.setRootCause(rootCause);
            throw ex4;
        }
    }
    
    private static LdapCtx getLdapCtxFromUrl(final String domainName, final String providerUrl, final LdapURL ldapURL, final Hashtable<?, ?> hashtable) throws NamingException {
        final LdapCtx ldapCtx = new LdapCtx(ldapURL.getDN(), ldapURL.getHost(), ldapURL.getPort(), hashtable, ldapURL.useSsl());
        ldapCtx.setDomainName(domainName);
        ldapCtx.setProviderUrl(providerUrl);
        return ldapCtx;
    }
    
    private static DirContext getUsingURLs(final String[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        NamingException ex = null;
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final String s = array[i];
            try {
                return getUsingURL(s, hashtable);
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
    
    public static Attribute createTypeNameAttr(final Class<?> clazz) {
        final String[] typeNames = getTypeNames(clazz, new Vector<String>(10));
        if (typeNames.length > 0) {
            final BasicAttribute basicAttribute = new BasicAttribute(Obj.JAVA_ATTRIBUTES[6]);
            for (int i = 0; i < typeNames.length; ++i) {
                basicAttribute.add(typeNames[i]);
            }
            return basicAttribute;
        }
        return null;
    }
    
    private static String[] getTypeNames(final Class<?> clazz, final Vector<String> vector) {
        getClassesAux(clazz, vector);
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            getClassesAux(interfaces[i], vector);
        }
        final String[] array = new String[vector.size()];
        int n = 0;
        final Iterator iterator = vector.iterator();
        while (iterator.hasNext()) {
            array[n++] = (String)iterator.next();
        }
        return array;
    }
    
    private static void getClassesAux(final Class<?> clazz, final Vector<String> vector) {
        if (!vector.contains(clazz.getName())) {
            vector.addElement(clazz.getName());
        }
        for (Class clazz2 = clazz.getSuperclass(); clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            getTypeNames(clazz2, vector);
        }
    }
}
