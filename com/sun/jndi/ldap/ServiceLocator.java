package com.sun.jndi.ldap;

import java.util.StringTokenizer;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Arrays;
import javax.naming.directory.DirContext;
import javax.naming.spi.NamingManager;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.InvalidNameException;
import java.util.List;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import java.util.Random;

class ServiceLocator
{
    private static final String SRV_RR = "SRV";
    private static final String[] SRV_RR_ATTR;
    private static final Random random;
    
    private ServiceLocator() {
    }
    
    static String mapDnToDomainName(final String s) throws InvalidNameException {
        if (s == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        final List<Rdn> rdns = new LdapName(s).getRdns();
        for (int i = rdns.size() - 1; i >= 0; --i) {
            final Rdn rdn = rdns.get(i);
            if (rdn.size() == 1 && "dc".equalsIgnoreCase(rdn.getType())) {
                final Object value = rdn.getValue();
                if (value instanceof String) {
                    if (value.equals(".") || (sb.length() == 1 && sb.charAt(0) == '.')) {
                        sb.setLength(0);
                    }
                    if (sb.length() > 0) {
                        sb.append('.');
                    }
                    sb.append(value);
                }
                else {
                    sb.setLength(0);
                }
            }
            else {
                sb.setLength(0);
            }
        }
        return (sb.length() != 0) ? sb.toString() : null;
    }
    
    static String[] getLdapService(final String s, final Map<?, ?> map) {
        if (map instanceof Hashtable) {
            return getLdapService(s, (Hashtable<?, ?>)map);
        }
        return getLdapService(s, new Hashtable<Object, Object>(map));
    }
    
    static String[] getLdapService(final String s, final Hashtable<?, ?> hashtable) {
        if (s == null || s.length() == 0) {
            return null;
        }
        final String string = "dns:///_ldap._tcp." + s;
        String[] hostports = null;
        try {
            final Context urlContext = NamingManager.getURLContext("dns", hashtable);
            if (!(urlContext instanceof DirContext)) {
                return null;
            }
            final Attributes attributes = ((DirContext)urlContext).getAttributes(string, ServiceLocator.SRV_RR_ATTR);
            final Attribute value;
            if (attributes != null && (value = attributes.get("SRV")) != null) {
                final int size = value.size();
                SrvRecord[] array = new SrvRecord[size];
                int i = 0;
                int n = 0;
                while (i < size) {
                    try {
                        array[n] = new SrvRecord((String)value.get(i));
                        ++n;
                    }
                    catch (final Exception ex) {}
                    ++i;
                }
                final int n2 = n;
                if (n2 < size) {
                    final SrvRecord[] array2 = new SrvRecord[n2];
                    System.arraycopy(array, 0, array2, 0, n2);
                    array = array2;
                }
                if (n2 > 1) {
                    Arrays.sort(array);
                }
                hostports = extractHostports(array);
            }
        }
        catch (final NamingException ex2) {}
        return hostports;
    }
    
    private static String[] extractHostports(final SrvRecord[] array) {
        String[] array2 = null;
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array2 == null) {
                array2 = new String[array.length];
            }
            final int n2 = i;
            while (i < array.length - 1 && array[i].priority == array[i + 1].priority) {
                ++i;
            }
            final int n3 = i;
            for (int n4 = n3 - n2 + 1, j = 0; j < n4; ++j) {
                array2[n++] = selectHostport(array, n2, n3);
            }
        }
        return array2;
    }
    
    private static String selectHostport(final SrvRecord[] array, final int n, final int n2) {
        if (n == n2) {
            return array[n].hostport;
        }
        int sum = 0;
        for (int i = n; i <= n2; ++i) {
            if (array[i] != null) {
                sum += array[i].weight;
                array[i].sum = sum;
            }
        }
        String hostport = null;
        final int n3 = (sum == 0) ? 0 : ServiceLocator.random.nextInt(sum + 1);
        for (int j = n; j <= n2; ++j) {
            if (array[j] != null && array[j].sum >= n3) {
                hostport = array[j].hostport;
                array[j] = null;
                break;
            }
        }
        return hostport;
    }
    
    static {
        SRV_RR_ATTR = new String[] { "SRV" };
        random = new Random();
    }
    
    static class SrvRecord implements Comparable<SrvRecord>
    {
        int priority;
        int weight;
        int sum;
        String hostport;
        
        SrvRecord(final String s) throws Exception {
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
            if (stringTokenizer.countTokens() == 4) {
                this.priority = Integer.parseInt(stringTokenizer.nextToken());
                this.weight = Integer.parseInt(stringTokenizer.nextToken());
                this.hostport = stringTokenizer.nextToken() + ":" + stringTokenizer.nextToken();
                return;
            }
            throw new IllegalArgumentException();
        }
        
        @Override
        public int compareTo(final SrvRecord srvRecord) {
            if (this.priority > srvRecord.priority) {
                return 1;
            }
            if (this.priority < srvRecord.priority) {
                return -1;
            }
            if (this.weight == 0 && srvRecord.weight != 0) {
                return -1;
            }
            if (this.weight != 0 && srvRecord.weight == 0) {
                return 1;
            }
            return 0;
        }
    }
}
