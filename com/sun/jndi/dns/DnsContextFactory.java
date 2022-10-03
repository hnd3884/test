package com.sun.jndi.dns;

import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.net.MalformedURLException;
import javax.naming.ConfigurationException;
import sun.net.dns.ResolverConfiguration;
import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.spi.InitialContextFactory;

public class DnsContextFactory implements InitialContextFactory
{
    private static final String DEFAULT_URL = "dns:";
    private static final int DEFAULT_PORT = 53;
    
    @Override
    public Context getInitialContext(Hashtable<?, ?> hashtable) throws NamingException {
        if (hashtable == null) {
            hashtable = new Hashtable<Object, Object>(5);
        }
        return urlToContext(getInitCtxUrl(hashtable), hashtable);
    }
    
    public static DnsContext getContext(final String s, final String[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        return new DnsContext(s, array, hashtable);
    }
    
    public static DnsContext getContext(final String s, final DnsUrl[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        final String[] serversForUrls = serversForUrls(array);
        final DnsContext context = getContext(s, serversForUrls, hashtable);
        if (platformServersUsed(array)) {
            context.setProviderUrl(constructProviderUrl(s, serversForUrls));
        }
        return context;
    }
    
    public static boolean platformServersAvailable() {
        return !filterNameServers(ResolverConfiguration.open().nameservers(), true).isEmpty();
    }
    
    private static Context urlToContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        DnsUrl[] fromList;
        try {
            fromList = DnsUrl.fromList(s);
        }
        catch (final MalformedURLException ex) {
            throw new ConfigurationException(ex.getMessage());
        }
        if (fromList.length == 0) {
            throw new ConfigurationException("Invalid DNS pseudo-URL(s): " + s);
        }
        final String domain = fromList[0].getDomain();
        for (int i = 1; i < fromList.length; ++i) {
            if (!domain.equalsIgnoreCase(fromList[i].getDomain())) {
                throw new ConfigurationException("Conflicting domains: " + s);
            }
        }
        return getContext(domain, fromList, hashtable);
    }
    
    private static String[] serversForUrls(final DnsUrl[] array) throws NamingException {
        if (array.length == 0) {
            throw new ConfigurationException("DNS pseudo-URL required");
        }
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            String host = array[i].getHost();
            final int port = array[i].getPort();
            if (host == null && port < 0) {
                final List<String> filterNameServers = filterNameServers(ResolverConfiguration.open().nameservers(), false);
                if (!filterNameServers.isEmpty()) {
                    list.addAll(filterNameServers);
                    continue;
                }
            }
            if (host == null) {
                host = "localhost";
            }
            list.add((port < 0) ? host : (host + ":" + port));
        }
        return (String[])list.toArray(new String[list.size()]);
    }
    
    private static boolean platformServersUsed(final DnsUrl[] array) {
        if (!platformServersAvailable()) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].getHost() == null && array[i].getPort() < 0) {
                return true;
            }
        }
        return false;
    }
    
    private static String constructProviderUrl(final String s, final String[] array) {
        String string = "";
        if (!s.equals(".")) {
            try {
                string = "/" + UrlUtil.encode(s, "ISO-8859-1");
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append("dns://").append(array[i]).append(string);
        }
        return sb.toString();
    }
    
    private static String getInitCtxUrl(final Hashtable<?, ?> hashtable) {
        final String s = (String)hashtable.get("java.naming.provider.url");
        return (s != null) ? s : "dns:";
    }
    
    private static List<String> filterNameServers(final List<String> list, final boolean b) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null || list == null || list.isEmpty()) {
            return list;
        }
        final ArrayList list2 = new ArrayList();
        for (final String s : list) {
            final int index = s.indexOf(58, s.indexOf(93) + 1);
            final int n = (index < 0) ? 53 : Integer.parseInt(s.substring(index + 1));
            final String s2 = (index < 0) ? s : s.substring(0, index);
            try {
                securityManager.checkConnect(s2, n);
                list2.add(s);
                if (b) {
                    return list2;
                }
                continue;
            }
            catch (final SecurityException ex) {}
        }
        return list2;
    }
}
