package sun.security.krb5;

import java.util.StringTokenizer;
import sun.security.krb5.internal.Krb5;
import javax.naming.Context;
import java.util.Arrays;
import javax.naming.directory.Attribute;
import java.security.PrivilegedActionException;
import javax.naming.NamingException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.net.SocketPermission;
import java.security.Permission;
import javax.naming.directory.Attributes;
import javax.naming.spi.NamingManager;
import java.util.Hashtable;
import javax.naming.directory.DirContext;
import java.util.Random;

class KrbServiceLocator
{
    private static final String SRV_RR = "SRV";
    private static final String[] SRV_RR_ATTR;
    private static final String SRV_TXT = "TXT";
    private static final String[] SRV_TXT_ATTR;
    private static final Random random;
    private static final boolean DEBUG;
    
    private KrbServiceLocator() {
    }
    
    static String[] getKerberosService(final String s) {
        new StringBuilder().append("dns:///_kerberos.").append(s).toString();
        String[] array = null;
        try {
            if (!(NamingManager.getURLContext("dns", new Hashtable<Object, Object>(0)) instanceof DirContext)) {
                return null;
            }
            Attributes attributes;
            try {
                attributes = AccessController.doPrivileged(() -> ((DirContext)context).getAttributes(s2, KrbServiceLocator.SRV_TXT_ATTR), null, new SocketPermission("*", "connect,accept"));
            }
            catch (final PrivilegedActionException ex) {
                throw (NamingException)ex.getCause();
            }
            final Attribute value;
            if (attributes != null && (value = attributes.get("TXT")) != null) {
                final int size = value.size();
                final String[] array2 = new String[size];
                int i = 0;
                int n = 0;
                while (i < size) {
                    try {
                        array2[n] = (String)value.get(i);
                        ++n;
                    }
                    catch (final Exception ex2) {}
                    ++i;
                }
                final int n2 = n;
                if (n2 < size) {
                    final String[] array3 = new String[n2];
                    System.arraycopy(array2, 0, array3, 0, n2);
                    array = array3;
                }
                else {
                    array = array2;
                }
            }
        }
        catch (final NamingException ex3) {}
        return array;
    }
    
    static String[] getKerberosService(final String s, final String s2) {
        new StringBuilder().append("dns:///_kerberos.").append(s2).append(".").append(s).toString();
        String[] hostports = null;
        try {
            if (!(NamingManager.getURLContext("dns", new Hashtable<Object, Object>(0)) instanceof DirContext)) {
                return null;
            }
            Attributes attributes;
            try {
                attributes = AccessController.doPrivileged(() -> ((DirContext)context).getAttributes(s3, KrbServiceLocator.SRV_RR_ATTR), null, new SocketPermission("*", "connect,accept"));
            }
            catch (final PrivilegedActionException ex) {
                throw (NamingException)ex.getCause();
            }
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
                    catch (final Exception ex2) {}
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
        catch (final NamingException ex3) {}
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
        final int n3 = (sum == 0) ? 0 : KrbServiceLocator.random.nextInt(sum + 1);
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
        SRV_TXT_ATTR = new String[] { "TXT" };
        random = new Random();
        DEBUG = Krb5.DEBUG;
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
