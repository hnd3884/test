package com.sun.jmx.remote.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Iterator;
import com.sun.jmx.remote.security.NotificationAccessController;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import java.util.Map;
import java.util.SortedSet;

public class EnvHelp
{
    public static final String CREDENTIAL_TYPES = "jmx.remote.rmi.server.credential.types";
    private static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
    private static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
    public static final String BUFFER_SIZE_PROPERTY = "jmx.remote.x.notification.buffer.size";
    public static final String MAX_FETCH_NOTIFS = "jmx.remote.x.notification.fetch.max";
    public static final String FETCH_TIMEOUT = "jmx.remote.x.notification.fetch.timeout";
    public static final String NOTIF_ACCESS_CONTROLLER = "com.sun.jmx.remote.notification.access.controller";
    public static final String DEFAULT_ORB = "java.naming.corba.orb";
    public static final String HIDDEN_ATTRIBUTES = "jmx.remote.x.hidden.attributes";
    public static final String DEFAULT_HIDDEN_ATTRIBUTES = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
    private static final SortedSet<String> defaultHiddenStrings;
    private static final SortedSet<String> defaultHiddenPrefixes;
    public static final String SERVER_CONNECTION_TIMEOUT = "jmx.remote.x.server.connection.timeout";
    public static final String CLIENT_CONNECTION_CHECK_PERIOD = "jmx.remote.x.client.connection.check.period";
    public static final String JMX_SERVER_DAEMON = "jmx.remote.x.daemon";
    private static final ClassLogger logger;
    
    public static ClassLoader resolveServerClassLoader(final Map<String, ?> map, final MBeanServer mBeanServer) throws InstanceNotFoundException {
        if (map == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        final Object value = map.get("jmx.remote.default.class.loader");
        final Object value2 = map.get("jmx.remote.default.class.loader.name");
        if (value != null && value2 != null) {
            throw new IllegalArgumentException("Only one of jmx.remote.default.class.loader or jmx.remote.default.class.loader.name should be specified.");
        }
        if (value == null && value2 == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        if (value != null) {
            if (value instanceof ClassLoader) {
                return (ClassLoader)value;
            }
            throw new IllegalArgumentException("ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + ((ClassLoader)value).getClass().getName());
        }
        else {
            if (!(value2 instanceof ObjectName)) {
                throw new IllegalArgumentException("ClassLoader name is not an instance of " + ObjectName.class.getName() + " : " + ((ObjectName)value2).getClass().getName());
            }
            final ObjectName objectName = (ObjectName)value2;
            if (mBeanServer == null) {
                throw new IllegalArgumentException("Null MBeanServer object");
            }
            return mBeanServer.getClassLoader(objectName);
        }
    }
    
    public static ClassLoader resolveClientClassLoader(final Map<String, ?> map) {
        if (map == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        final Object value = map.get("jmx.remote.default.class.loader");
        if (value == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        if (value instanceof ClassLoader) {
            return (ClassLoader)value;
        }
        throw new IllegalArgumentException("ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + ((ClassLoader)value).getClass().getName());
    }
    
    public static <T extends Throwable> T initCause(final T t, final Throwable t2) {
        t.initCause(t2);
        return t;
    }
    
    public static Throwable getCause(final Throwable t) {
        Throwable t2 = t;
        try {
            t2 = (Throwable)t.getClass().getMethod("getCause", (Class<?>[])null).invoke(t, (Object[])null);
        }
        catch (final Exception ex) {}
        return (t2 != null) ? t2 : t;
    }
    
    public static int getNotifBufferSize(final Map<String, ?> map) {
        int n = 1000;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.remote.x.notification.buffer.size"));
            if (s != null) {
                n = Integer.parseInt(s);
            }
            else {
                final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.remote.x.buffer.size"));
                if (s2 != null) {
                    n = Integer.parseInt(s2);
                }
            }
        }
        catch (final RuntimeException ex) {
            EnvHelp.logger.warning("getNotifBufferSize", "Can't use System property jmx.remote.x.notification.buffer.size: " + ex);
            EnvHelp.logger.debug("getNotifBufferSize", ex);
        }
        int n2 = n;
        try {
            if (map.containsKey("jmx.remote.x.notification.buffer.size")) {
                n2 = (int)getIntegerAttribute(map, "jmx.remote.x.notification.buffer.size", n, 0L, 2147483647L);
            }
            else {
                n2 = (int)getIntegerAttribute(map, "jmx.remote.x.buffer.size", n, 0L, 2147483647L);
            }
        }
        catch (final RuntimeException ex2) {
            EnvHelp.logger.warning("getNotifBufferSize", "Can't determine queuesize (using default): " + ex2);
            EnvHelp.logger.debug("getNotifBufferSize", ex2);
        }
        return n2;
    }
    
    public static int getMaxFetchNotifNumber(final Map<String, ?> map) {
        return (int)getIntegerAttribute(map, "jmx.remote.x.notification.fetch.max", 1000L, 1L, 2147483647L);
    }
    
    public static long getFetchTimeout(final Map<String, ?> map) {
        return getIntegerAttribute(map, "jmx.remote.x.notification.fetch.timeout", 60000L, 0L, Long.MAX_VALUE);
    }
    
    public static NotificationAccessController getNotificationAccessController(final Map<String, ?> map) {
        return (map == null) ? null : ((NotificationAccessController)map.get("com.sun.jmx.remote.notification.access.controller"));
    }
    
    public static long getIntegerAttribute(final Map<String, ?> map, final String s, final long n, final long n2, final long n3) {
        final Object value;
        if (map == null || (value = map.get(s)) == null) {
            return n;
        }
        long n4;
        if (value instanceof Number) {
            n4 = ((Number)value).longValue();
        }
        else {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("Attribute " + s + " value must be Integer or String: " + value);
            }
            n4 = Long.parseLong((String)value);
        }
        if (n4 < n2) {
            throw new IllegalArgumentException("Attribute " + s + " value must be at least " + n2 + ": " + n4);
        }
        if (n4 > n3) {
            throw new IllegalArgumentException("Attribute " + s + " value must be at most " + n3 + ": " + n4);
        }
        return n4;
    }
    
    public static void checkAttributes(final Map<?, ?> map) {
        for (final Object next : map.keySet()) {
            if (!(next instanceof String)) {
                throw new IllegalArgumentException("Attributes contain key that is not a string: " + next);
            }
        }
    }
    
    public static <V> Map<String, V> filterAttributes(final Map<String, V> map) {
        if (EnvHelp.logger.traceOn()) {
            EnvHelp.logger.trace("filterAttributes", "starts");
        }
        final TreeMap treeMap = new TreeMap((Map<? extends K, ? extends V>)map);
        purgeUnserializable(treeMap.values());
        hideAttributes(treeMap);
        return treeMap;
    }
    
    private static void purgeUnserializable(final Collection<?> collection) {
        EnvHelp.logger.trace("purgeUnserializable", "starts");
        ObjectOutputStream objectOutputStream = null;
        int n = 0;
        final Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            if (next == null || next instanceof String) {
                if (EnvHelp.logger.traceOn()) {
                    EnvHelp.logger.trace("purgeUnserializable", "Value trivially serializable: " + next);
                }
            }
            else {
                try {
                    if (objectOutputStream == null) {
                        objectOutputStream = new ObjectOutputStream(new SinkOutputStream());
                    }
                    objectOutputStream.writeObject(next);
                    if (EnvHelp.logger.traceOn()) {
                        EnvHelp.logger.trace("purgeUnserializable", "Value serializable: " + next);
                    }
                }
                catch (final IOException ex) {
                    if (EnvHelp.logger.traceOn()) {
                        EnvHelp.logger.trace("purgeUnserializable", "Value not serializable: " + next + ": " + ex);
                    }
                    iterator.remove();
                    objectOutputStream = null;
                }
            }
            ++n;
        }
    }
    
    private static void hideAttributes(final SortedMap<String, ?> sortedMap) {
        if (sortedMap.isEmpty()) {
            return;
        }
        final String s = (String)sortedMap.get("jmx.remote.x.hidden.attributes");
        SortedSet<String> defaultHiddenStrings;
        SortedSet<String> defaultHiddenPrefixes;
        if (s != null) {
            String s2;
            if (s.startsWith("=")) {
                s2 = s.substring(1);
            }
            else {
                s2 = s + " java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
            }
            defaultHiddenStrings = new TreeSet<String>();
            defaultHiddenPrefixes = new TreeSet<String>();
            parseHiddenAttributes(s2, defaultHiddenStrings, defaultHiddenPrefixes);
        }
        else {
            final String s3 = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
            synchronized (EnvHelp.defaultHiddenStrings) {
                if (EnvHelp.defaultHiddenStrings.isEmpty()) {
                    parseHiddenAttributes(s3, EnvHelp.defaultHiddenStrings, EnvHelp.defaultHiddenPrefixes);
                }
                defaultHiddenStrings = EnvHelp.defaultHiddenStrings;
                defaultHiddenPrefixes = EnvHelp.defaultHiddenPrefixes;
            }
        }
        final String string = sortedMap.lastKey() + "X";
        final Iterator<String> iterator = sortedMap.keySet().iterator();
        final Iterator<Object> iterator2 = defaultHiddenStrings.iterator();
        final Iterator<Object> iterator3 = defaultHiddenPrefixes.iterator();
        String s4;
        if (iterator2.hasNext()) {
            s4 = iterator2.next();
        }
        else {
            s4 = string;
        }
        String s5;
        if (iterator3.hasNext()) {
            s5 = iterator3.next();
        }
        else {
            s5 = string;
        }
        while (iterator.hasNext()) {
            final String s6 = iterator.next();
            int compareTo;
            while ((compareTo = s4.compareTo(s6)) < 0) {
                if (iterator2.hasNext()) {
                    s4 = iterator2.next();
                }
                else {
                    s4 = string;
                }
            }
            if (compareTo == 0) {
                iterator.remove();
            }
            else {
                while (s5.compareTo(s6) <= 0) {
                    if (s6.startsWith(s5)) {
                        iterator.remove();
                        break;
                    }
                    if (iterator3.hasNext()) {
                        s5 = iterator3.next();
                    }
                    else {
                        s5 = string;
                    }
                }
            }
        }
    }
    
    private static void parseHiddenAttributes(final String s, final SortedSet<String> set, final SortedSet<String> set2) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.endsWith("*")) {
                set2.add(nextToken.substring(0, nextToken.length() - 1));
            }
            else {
                set.add(nextToken);
            }
        }
    }
    
    public static long getServerConnectionTimeout(final Map<String, ?> map) {
        return getIntegerAttribute(map, "jmx.remote.x.server.connection.timeout", 120000L, 0L, Long.MAX_VALUE);
    }
    
    public static long getConnectionCheckPeriod(final Map<String, ?> map) {
        return getIntegerAttribute(map, "jmx.remote.x.client.connection.check.period", 60000L, 0L, Long.MAX_VALUE);
    }
    
    public static boolean computeBooleanFromString(final String s) {
        return computeBooleanFromString(s, false);
    }
    
    public static boolean computeBooleanFromString(final String s, final boolean b) {
        if (s == null) {
            return b;
        }
        if (s.equalsIgnoreCase("true")) {
            return true;
        }
        if (s.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalArgumentException("Property value must be \"true\" or \"false\" instead of \"" + s + "\"");
    }
    
    public static <K, V> Hashtable<K, V> mapToHashtable(final Map<K, V> map) {
        final HashMap hashMap = new HashMap((Map<? extends K, ? extends V>)map);
        if (hashMap.containsKey(null)) {
            hashMap.remove(null);
        }
        final Iterator iterator = hashMap.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
        return new Hashtable<K, V>(hashMap);
    }
    
    public static boolean isServerDaemon(final Map<String, ?> map) {
        return map != null && "true".equalsIgnoreCase((String)map.get("jmx.remote.x.daemon"));
    }
    
    static {
        defaultHiddenStrings = new TreeSet<String>();
        defaultHiddenPrefixes = new TreeSet<String>();
        logger = new ClassLogger("javax.management.remote.misc", "EnvHelp");
    }
    
    private static final class SinkOutputStream extends OutputStream
    {
        @Override
        public void write(final byte[] array, final int n, final int n2) {
        }
        
        @Override
        public void write(final int n) {
        }
    }
}
