package com.sun.jndi.ldap;

import sun.misc.InnocuousThread;
import com.sun.jndi.ldap.pool.PoolCleaner;
import java.util.Locale;
import java.util.StringTokenizer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.PrintStream;
import com.sun.jndi.ldap.pool.PooledConnectionFactory;
import javax.naming.ldap.Control;
import javax.naming.NamingException;
import javax.naming.CommunicationException;
import java.util.Hashtable;
import java.io.OutputStream;
import com.sun.jndi.ldap.pool.Pool;

public final class LdapPoolManager
{
    private static final String DEBUG = "com.sun.jndi.ldap.connect.pool.debug";
    public static final boolean debug;
    public static final boolean trace;
    private static final String POOL_AUTH = "com.sun.jndi.ldap.connect.pool.authentication";
    private static final String POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
    private static final String MAX_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
    private static final String PREF_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
    private static final String INIT_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.initsize";
    private static final String POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
    private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
    private static final int DEFAULT_MAX_POOL_SIZE = 0;
    private static final int DEFAULT_PREF_POOL_SIZE = 0;
    private static final int DEFAULT_INIT_POOL_SIZE = 1;
    private static final int DEFAULT_TIMEOUT = 0;
    private static final String DEFAULT_AUTH_MECHS = "none simple";
    private static final String DEFAULT_PROTOCOLS = "plain";
    private static final int NONE = 0;
    private static final int SIMPLE = 1;
    private static final int DIGEST = 2;
    private static final long idleTimeout;
    private static final int maxSize;
    private static final int prefSize;
    private static final int initSize;
    private static boolean supportPlainProtocol;
    private static boolean supportSslProtocol;
    private static final Pool[] pools;
    
    private LdapPoolManager() {
    }
    
    private static int findPool(final String s) {
        if ("none".equalsIgnoreCase(s)) {
            return 0;
        }
        if ("simple".equalsIgnoreCase(s)) {
            return 1;
        }
        if ("digest-md5".equalsIgnoreCase(s)) {
            return 2;
        }
        return -1;
    }
    
    static boolean isPoolingAllowed(final String s, final OutputStream outputStream, final String s2, final String s3, final Hashtable<?, ?> hashtable) throws NamingException {
        if ((outputStream != null && !LdapPoolManager.debug) || (s3 == null && !LdapPoolManager.supportPlainProtocol) || ("ssl".equalsIgnoreCase(s3) && !LdapPoolManager.supportSslProtocol)) {
            d("Pooling disallowed due to tracing or unsupported pooling of protocol");
            return false;
        }
        final String s4 = "java.util.Comparator";
        boolean b = false;
        if (s != null && !s.equals("javax.net.ssl.SSLSocketFactory")) {
            try {
                final Class<?>[] interfaces = Obj.helper.loadClass(s).getInterfaces();
                for (int i = 0; i < interfaces.length; ++i) {
                    if (interfaces[i].getCanonicalName().equals(s4)) {
                        b = true;
                    }
                }
            }
            catch (final Exception rootCause) {
                final CommunicationException ex = new CommunicationException("Loading the socket factory");
                ex.setRootCause(rootCause);
                throw ex;
            }
            if (!b) {
                return false;
            }
        }
        final int pool = findPool(s2);
        if (pool < 0 || LdapPoolManager.pools[pool] == null) {
            d("authmech not found: ", s2);
            return false;
        }
        d("using authmech: ", s2);
        switch (pool) {
            case 0:
            case 1: {
                return true;
            }
            case 2: {
                return hashtable == null || hashtable.get("java.naming.security.sasl.callback") == null;
            }
            default: {
                return false;
            }
        }
    }
    
    static LdapClient getLdapClient(final String s, final int n, final String s2, final int n2, final int n3, final OutputStream outputStream, final int n4, final String s3, final Control[] array, final String s4, final String s5, final Object o, final Hashtable<?, ?> hashtable) throws NamingException {
        Object o2 = null;
        final int pool = findPool(s3);
        final Pool pool2;
        if (pool < 0 || (pool2 = LdapPoolManager.pools[pool]) == null) {
            throw new IllegalArgumentException("Attempting to use pooling for an unsupported mechanism: " + s3);
        }
        switch (pool) {
            case 0: {
                o2 = new ClientId(n4, s, n, s4, array, outputStream, s2);
                break;
            }
            case 1: {
                o2 = new SimpleClientId(n4, s, n, s4, array, outputStream, s2, s5, o);
                break;
            }
            case 2: {
                o2 = new DigestClientId(n4, s, n, s4, array, outputStream, s2, s5, o, hashtable);
                break;
            }
        }
        return (LdapClient)pool2.getPooledConnection(o2, n2, new LdapClientFactory(s, n, s2, n2, n3, outputStream));
    }
    
    public static void showStats(final PrintStream printStream) {
        printStream.println("***** start *****");
        printStream.println("idle timeout: " + LdapPoolManager.idleTimeout);
        printStream.println("maximum pool size: " + LdapPoolManager.maxSize);
        printStream.println("preferred pool size: " + LdapPoolManager.prefSize);
        printStream.println("initial pool size: " + LdapPoolManager.initSize);
        printStream.println("protocol types: " + (LdapPoolManager.supportPlainProtocol ? "plain " : "") + (LdapPoolManager.supportSslProtocol ? "ssl" : ""));
        printStream.println("authentication types: " + ((LdapPoolManager.pools[0] != null) ? "none " : "") + ((LdapPoolManager.pools[1] != null) ? "simple " : "") + ((LdapPoolManager.pools[2] != null) ? "DIGEST-MD5 " : ""));
        for (int i = 0; i < LdapPoolManager.pools.length; ++i) {
            if (LdapPoolManager.pools[i] != null) {
                printStream.println(((i == 0) ? "anonymous pools" : ((i == 1) ? "simple auth pools" : ((i == 2) ? "digest pools" : ""))) + ":");
                LdapPoolManager.pools[i].showStats(printStream);
            }
        }
        printStream.println("***** end *****");
    }
    
    public static void expire(final long n) {
        for (int i = 0; i < LdapPoolManager.pools.length; ++i) {
            if (LdapPoolManager.pools[i] != null) {
                LdapPoolManager.pools[i].expire(n);
            }
        }
    }
    
    private static void d(final String s) {
        if (LdapPoolManager.debug) {
            System.err.println("LdapPoolManager: " + s);
        }
    }
    
    private static void d(final String s, final String s2) {
        if (LdapPoolManager.debug) {
            System.err.println("LdapPoolManager: " + s + s2);
        }
    }
    
    private static final String getProperty(final String s, final String s2) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    return System.getProperty(s, s2);
                }
                catch (final SecurityException ex) {
                    return s2;
                }
            }
        });
    }
    
    private static final int getInteger(final String s, final int n) {
        return AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                try {
                    return Integer.getInteger(s, n);
                }
                catch (final SecurityException ex) {
                    return new Integer(n);
                }
            }
        });
    }
    
    private static final long getLong(final String s, final long n) {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                try {
                    return Long.getLong(s, n);
                }
                catch (final SecurityException ex) {
                    return new Long(n);
                }
            }
        });
    }
    
    static {
        debug = "all".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", null));
        trace = (LdapPoolManager.debug || "fine".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", null)));
        LdapPoolManager.supportPlainProtocol = false;
        LdapPoolManager.supportSslProtocol = false;
        pools = new Pool[3];
        maxSize = getInteger("com.sun.jndi.ldap.connect.pool.maxsize", 0);
        prefSize = getInteger("com.sun.jndi.ldap.connect.pool.prefsize", 0);
        initSize = getInteger("com.sun.jndi.ldap.connect.pool.initsize", 1);
        idleTimeout = getLong("com.sun.jndi.ldap.connect.pool.timeout", 0L);
        final StringTokenizer stringTokenizer = new StringTokenizer(getProperty("com.sun.jndi.ldap.connect.pool.authentication", "none simple"));
        for (int countTokens = stringTokenizer.countTokens(), i = 0; i < countTokens; ++i) {
            String lowerCase = stringTokenizer.nextToken().toLowerCase(Locale.ENGLISH);
            if (lowerCase.equals("anonymous")) {
                lowerCase = "none";
            }
            final int pool = findPool(lowerCase);
            if (pool >= 0 && LdapPoolManager.pools[pool] == null) {
                LdapPoolManager.pools[pool] = new Pool(LdapPoolManager.initSize, LdapPoolManager.prefSize, LdapPoolManager.maxSize);
            }
        }
        final StringTokenizer stringTokenizer2 = new StringTokenizer(getProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain"));
        for (int countTokens2 = stringTokenizer2.countTokens(), j = 0; j < countTokens2; ++j) {
            final String nextToken = stringTokenizer2.nextToken();
            if ("plain".equalsIgnoreCase(nextToken)) {
                LdapPoolManager.supportPlainProtocol = true;
            }
            else if ("ssl".equalsIgnoreCase(nextToken)) {
                LdapPoolManager.supportSslProtocol = true;
            }
        }
        if (LdapPoolManager.idleTimeout > 0L) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    final Thread systemThread = InnocuousThread.newSystemThread("LDAP PoolCleaner", new PoolCleaner(LdapPoolManager.idleTimeout, LdapPoolManager.pools));
                    assert systemThread.getContextClassLoader() == null;
                    systemThread.setDaemon(true);
                    systemThread.start();
                    return null;
                }
            });
        }
        if (LdapPoolManager.debug) {
            showStats(System.err);
        }
    }
}
