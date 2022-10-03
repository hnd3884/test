package com.sun.mail.util.logging;

import java.util.Hashtable;
import java.io.ObjectStreamException;
import java.util.Map;
import java.util.Enumeration;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.logging.ErrorManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Formatter;
import java.util.logging.Filter;
import java.util.Locale;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.LogRecord;
import java.security.Permission;
import java.util.logging.LoggingPermission;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.LogManager;
import java.lang.reflect.Method;
import java.util.Properties;

final class LogManagerProperties extends Properties
{
    private static final long serialVersionUID = -2239983349056806252L;
    private static final Method LR_GET_INSTANT;
    private static final Method ZI_SYSTEM_DEFAULT;
    private static final Method ZDT_OF_INSTANT;
    private static volatile String[] REFLECT_NAMES;
    private static final Object LOG_MANAGER;
    private final String prefix;
    
    private static Object loadLogManager() {
        Object m;
        try {
            m = LogManager.getLogManager();
        }
        catch (final LinkageError restricted) {
            m = readConfiguration();
        }
        catch (final RuntimeException unexpected) {
            m = readConfiguration();
        }
        return m;
    }
    
    private static Properties readConfiguration() {
        final Properties props = new Properties();
        try {
            final String n = System.getProperty("java.util.logging.config.file");
            if (n != null) {
                final File f = new File(n).getCanonicalFile();
                final InputStream in = new FileInputStream(f);
                try {
                    props.load(in);
                }
                finally {
                    in.close();
                }
            }
        }
        catch (final RuntimeException ex) {}
        catch (final Exception ex2) {}
        catch (final LinkageError linkageError) {}
        return props;
    }
    
    static String fromLogManager(final String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        final Object m = LogManagerProperties.LOG_MANAGER;
        try {
            if (m instanceof Properties) {
                return ((Properties)m).getProperty(name);
            }
        }
        catch (final RuntimeException ex) {}
        if (m != null) {
            try {
                if (m instanceof LogManager) {
                    return ((LogManager)m).getProperty(name);
                }
            }
            catch (final LinkageError linkageError) {}
            catch (final RuntimeException ex2) {}
        }
        return null;
    }
    
    static void checkLogManagerAccess() {
        boolean checked = false;
        final Object m = LogManagerProperties.LOG_MANAGER;
        if (m != null) {
            try {
                if (m instanceof LogManager) {
                    checked = true;
                    ((LogManager)m).checkAccess();
                }
            }
            catch (final SecurityException notAllowed) {
                if (checked) {
                    throw notAllowed;
                }
            }
            catch (final LinkageError linkageError) {}
            catch (final RuntimeException ex) {}
        }
        if (!checked) {
            checkLoggingAccess();
        }
    }
    
    private static void checkLoggingAccess() {
        boolean checked = false;
        final Logger global = Logger.getLogger("global");
        try {
            if (Logger.class == global.getClass()) {
                global.removeHandler(null);
                checked = true;
            }
        }
        catch (final NullPointerException ex) {}
        if (!checked) {
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(new LoggingPermission("control", null));
            }
        }
    }
    
    static boolean hasLogManager() {
        final Object m = LogManagerProperties.LOG_MANAGER;
        return m != null && !(m instanceof Properties);
    }
    
    static Comparable<?> getZonedDateTime(final LogRecord record) {
        if (record == null) {
            throw new NullPointerException();
        }
        final Method m = LogManagerProperties.ZDT_OF_INSTANT;
        if (m != null) {
            try {
                return (Comparable)m.invoke(null, LogManagerProperties.LR_GET_INSTANT.invoke(record, new Object[0]), LogManagerProperties.ZI_SYSTEM_DEFAULT.invoke(null, new Object[0]));
            }
            catch (final RuntimeException ignore) {
                assert LogManagerProperties.LR_GET_INSTANT != null && LogManagerProperties.ZI_SYSTEM_DEFAULT != null : ignore;
            }
            catch (final InvocationTargetException ite) {
                final Throwable cause = ite.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new UndeclaredThrowableException(ite);
            }
            catch (final Exception ex) {}
        }
        return null;
    }
    
    static String getLocalHost(final Object s) throws Exception {
        try {
            final Method m = s.getClass().getMethod("getLocalHost", (Class<?>[])new Class[0]);
            if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == String.class) {
                return (String)m.invoke(s, new Object[0]);
            }
            throw new NoSuchMethodException(m.toString());
        }
        catch (final ExceptionInInitializerError EIIE) {
            throw wrapOrThrow(EIIE);
        }
        catch (final InvocationTargetException ite) {
            throw paramOrError(ite);
        }
    }
    
    static long parseDurationToMillis(final CharSequence value) throws Exception {
        try {
            final Class<?> k = findClass("java.time.Duration");
            final Method parse = k.getMethod("parse", CharSequence.class);
            if (!k.isAssignableFrom(parse.getReturnType()) || !Modifier.isStatic(parse.getModifiers())) {
                throw new NoSuchMethodException(parse.toString());
            }
            final Method toMillis = k.getMethod("toMillis", (Class<?>[])new Class[0]);
            if (!Long.TYPE.isAssignableFrom(toMillis.getReturnType()) || Modifier.isStatic(toMillis.getModifiers())) {
                throw new NoSuchMethodException(toMillis.toString());
            }
            return (long)toMillis.invoke(parse.invoke(null, value), new Object[0]);
        }
        catch (final ExceptionInInitializerError EIIE) {
            throw wrapOrThrow(EIIE);
        }
        catch (final InvocationTargetException ite) {
            throw paramOrError(ite);
        }
    }
    
    static String toLanguageTag(final Locale locale) {
        final String l = locale.getLanguage();
        final String c = locale.getCountry();
        final String v = locale.getVariant();
        final char[] b = new char[l.length() + c.length() + v.length() + 2];
        int count = l.length();
        l.getChars(0, count, b, 0);
        if (c.length() != 0 || (l.length() != 0 && v.length() != 0)) {
            b[count] = '-';
            ++count;
            c.getChars(0, c.length(), b, count);
            count += c.length();
        }
        if (v.length() != 0 && (l.length() != 0 || c.length() != 0)) {
            b[count] = '-';
            ++count;
            v.getChars(0, v.length(), b, count);
            count += v.length();
        }
        return String.valueOf(b, 0, count);
    }
    
    static Filter newFilter(final String name) throws Exception {
        return newObjectFrom(name, Filter.class);
    }
    
    static Formatter newFormatter(final String name) throws Exception {
        return newObjectFrom(name, Formatter.class);
    }
    
    static Comparator<? super LogRecord> newComparator(final String name) throws Exception {
        return newObjectFrom(name, Comparator.class);
    }
    
    static <T> Comparator<T> reverseOrder(final Comparator<T> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        Comparator<T> reverse = null;
        try {
            final Method m = c.getClass().getMethod("reversed", (Class<?>[])new Class[0]);
            if (!Modifier.isStatic(m.getModifiers()) && Comparator.class.isAssignableFrom(m.getReturnType())) {
                try {
                    reverse = (Comparator)m.invoke(c, new Object[0]);
                }
                catch (final ExceptionInInitializerError eiie) {
                    throw wrapOrThrow(eiie);
                }
            }
        }
        catch (final NoSuchMethodException ex) {}
        catch (final IllegalAccessException ex2) {}
        catch (final RuntimeException ex3) {}
        catch (final InvocationTargetException ite) {
            paramOrError(ite);
        }
        if (reverse == null) {
            reverse = Collections.reverseOrder(c);
        }
        return reverse;
    }
    
    static ErrorManager newErrorManager(final String name) throws Exception {
        return newObjectFrom(name, ErrorManager.class);
    }
    
    static boolean isStaticUtilityClass(final String name) throws Exception {
        final Class<?> c = findClass(name);
        final Class<?> obj = Object.class;
        final Method[] methods;
        boolean util;
        if (c != obj && (methods = c.getMethods()).length != 0) {
            util = true;
            for (final Method m : methods) {
                if (m.getDeclaringClass() != obj && !Modifier.isStatic(m.getModifiers())) {
                    util = false;
                    break;
                }
            }
        }
        else {
            util = false;
        }
        return util;
    }
    
    static boolean isReflectionClass(final String name) throws Exception {
        String[] names = LogManagerProperties.REFLECT_NAMES;
        if (names == null) {
            names = (LogManagerProperties.REFLECT_NAMES = reflectionClassNames());
        }
        for (final String rf : names) {
            if (name.equals(rf)) {
                return true;
            }
        }
        findClass(name);
        return false;
    }
    
    private static String[] reflectionClassNames() throws Exception {
        final Class<?> thisClass = LogManagerProperties.class;
        assert Modifier.isFinal(thisClass.getModifiers()) : thisClass;
        try {
            final HashSet<String> traces = new HashSet<String>();
            final Throwable t = Throwable.class.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            for (final StackTraceElement ste : t.getStackTrace()) {
                if (thisClass.getName().equals(ste.getClassName())) {
                    break;
                }
                traces.add(ste.getClassName());
            }
            Throwable.class.getMethod("fillInStackTrace", (Class<?>[])new Class[0]).invoke(t, new Object[0]);
            for (final StackTraceElement ste : t.getStackTrace()) {
                if (thisClass.getName().equals(ste.getClassName())) {
                    break;
                }
                traces.add(ste.getClassName());
            }
            return traces.toArray(new String[traces.size()]);
        }
        catch (final InvocationTargetException ITE) {
            throw paramOrError(ITE);
        }
    }
    
    static <T> T newObjectFrom(final String name, final Class<T> type) throws Exception {
        try {
            final Class<?> clazz = findClass(name);
            if (type.isAssignableFrom(clazz)) {
                try {
                    return type.cast(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
                }
                catch (final InvocationTargetException ITE) {
                    throw paramOrError(ITE);
                }
            }
            throw new ClassCastException(clazz.getName() + " cannot be cast to " + type.getName());
        }
        catch (final NoClassDefFoundError NCDFE) {
            throw new ClassNotFoundException(NCDFE.toString(), NCDFE);
        }
        catch (final ExceptionInInitializerError EIIE) {
            throw wrapOrThrow(EIIE);
        }
    }
    
    private static Exception paramOrError(final InvocationTargetException ite) {
        final Throwable cause = ite.getCause();
        if (cause != null && (cause instanceof VirtualMachineError | cause instanceof ThreadDeath)) {
            throw (Error)cause;
        }
        return ite;
    }
    
    private static InvocationTargetException wrapOrThrow(final ExceptionInInitializerError eiie) {
        if (eiie.getCause() instanceof Error) {
            throw eiie;
        }
        return new InvocationTargetException(eiie);
    }
    
    private static Class<?> findClass(final String name) throws ClassNotFoundException {
        final ClassLoader[] loaders = getClassLoaders();
        assert loaders.length == 2 : loaders.length;
        Class<?> clazz;
        if (loaders[0] != null) {
            try {
                clazz = Class.forName(name, false, loaders[0]);
            }
            catch (final ClassNotFoundException tryContext) {
                clazz = tryLoad(name, loaders[1]);
            }
        }
        else {
            clazz = tryLoad(name, loaders[1]);
        }
        return clazz;
    }
    
    private static Class<?> tryLoad(final String name, final ClassLoader l) throws ClassNotFoundException {
        if (l != null) {
            return Class.forName(name, false, l);
        }
        return Class.forName(name);
    }
    
    private static ClassLoader[] getClassLoaders() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader[]>)new PrivilegedAction<ClassLoader[]>() {
            @Override
            public ClassLoader[] run() {
                final ClassLoader[] loaders = new ClassLoader[2];
                try {
                    loaders[0] = ClassLoader.getSystemClassLoader();
                }
                catch (final SecurityException ignore) {
                    loaders[0] = null;
                }
                try {
                    loaders[1] = Thread.currentThread().getContextClassLoader();
                }
                catch (final SecurityException ignore) {
                    loaders[1] = null;
                }
                return loaders;
            }
        });
    }
    
    LogManagerProperties(final Properties parent, final String prefix) {
        super(parent);
        if (parent == null || prefix == null) {
            throw new NullPointerException();
        }
        this.prefix = prefix;
    }
    
    @Override
    public synchronized Object clone() {
        return this.exportCopy(this.defaults);
    }
    
    @Override
    public synchronized String getProperty(final String key) {
        String value = this.defaults.getProperty(key);
        if (value == null) {
            if (key.length() > 0) {
                value = fromLogManager(this.prefix + '.' + key);
            }
            if (value == null) {
                value = fromLogManager(key);
            }
            if (value != null) {
                super.put(key, value);
            }
            else {
                final Object v = super.get(key);
                value = ((v instanceof String) ? ((String)v) : null);
            }
        }
        return value;
    }
    
    @Override
    public String getProperty(final String key, final String def) {
        final String value = this.getProperty(key);
        return (value == null) ? def : value;
    }
    
    @Override
    public synchronized Object get(final Object key) {
        Object value;
        if (key instanceof String) {
            value = this.getProperty((String)key);
        }
        else {
            value = null;
        }
        if (value == null) {
            value = ((Hashtable<K, Object>)this.defaults).get(key);
            if (value == null && !this.defaults.containsKey(key)) {
                value = super.get(key);
            }
        }
        return value;
    }
    
    @Override
    public synchronized Object put(final Object key, final Object value) {
        if (key instanceof String && value instanceof String) {
            final Object def = this.preWrite(key);
            final Object man = super.put(key, value);
            return (man == null) ? def : man;
        }
        return super.put(key, value);
    }
    
    @Override
    public Object setProperty(final String key, final String value) {
        return this.put(key, value);
    }
    
    @Override
    public synchronized boolean containsKey(final Object key) {
        boolean found = key instanceof String && this.getProperty((String)key) != null;
        if (!found) {
            found = (this.defaults.containsKey(key) || super.containsKey(key));
        }
        return found;
    }
    
    @Override
    public synchronized Object remove(final Object key) {
        final Object def = this.preWrite(key);
        final Object man = super.remove(key);
        return (man == null) ? def : man;
    }
    
    @Override
    public Enumeration<?> propertyNames() {
        assert false;
        return super.propertyNames();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Properties)) {
            return false;
        }
        assert false : this.prefix;
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        assert false : this.prefix.hashCode();
        return super.hashCode();
    }
    
    private Object preWrite(final Object key) {
        assert Thread.holdsLock(this);
        return this.get(key);
    }
    
    private Properties exportCopy(final Properties parent) {
        Thread.holdsLock(this);
        final Properties child = new Properties(parent);
        child.putAll(this);
        return child;
    }
    
    private synchronized Object writeReplace() throws ObjectStreamException {
        assert false;
        return this.exportCopy((Properties)this.defaults.clone());
    }
    
    static {
        Method lrgi = null;
        Method zisd = null;
        Method zdtoi = null;
        try {
            lrgi = LogRecord.class.getMethod("getInstant", (Class<?>[])new Class[0]);
            assert Comparable.class.isAssignableFrom(lrgi.getReturnType()) : lrgi;
            zisd = findClass("java.time.ZoneId").getMethod("systemDefault", (Class<?>[])new Class[0]);
            if (!Modifier.isStatic(zisd.getModifiers())) {
                throw new NoSuchMethodException(zisd.toString());
            }
            zdtoi = findClass("java.time.ZonedDateTime").getMethod("ofInstant", findClass("java.time.Instant"), findClass("java.time.ZoneId"));
            if (!Modifier.isStatic(zdtoi.getModifiers())) {
                throw new NoSuchMethodException(zdtoi.toString());
            }
            if (!Comparable.class.isAssignableFrom(zdtoi.getReturnType())) {
                throw new NoSuchMethodException(zdtoi.toString());
            }
        }
        catch (final RuntimeException ex) {}
        catch (final Exception ex2) {}
        catch (final LinkageError linkageError) {}
        finally {
            if (lrgi == null || zisd == null || zdtoi == null) {
                lrgi = null;
                zisd = null;
                zdtoi = null;
            }
        }
        LR_GET_INSTANT = lrgi;
        ZI_SYSTEM_DEFAULT = zisd;
        ZDT_OF_INSTANT = zdtoi;
        LOG_MANAGER = loadLogManager();
    }
}
