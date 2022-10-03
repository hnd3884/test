package com.sun.java.util.jar.pack;

import java.util.Hashtable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.util.HashMap;
import java.io.IOException;
import java.util.Properties;
import java.util.Comparator;
import java.util.Set;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

final class PropMap implements SortedMap<String, String>
{
    private final TreeMap<String, String> theMap;
    private final List<Object> listenerList;
    private static Map<String, String> defaultProps;
    
    void addListener(final Object o) {
        assert Beans.isPropertyChangeListener(o);
        this.listenerList.add(o);
    }
    
    void removeListener(final Object o) {
        assert Beans.isPropertyChangeListener(o);
        this.listenerList.remove(o);
    }
    
    @Override
    public String put(final String s, final String s2) {
        final String s3 = this.theMap.put(s, s2);
        if (s2 != s3 && !this.listenerList.isEmpty()) {
            assert Beans.isBeansPresent();
            final Object propertyChangeEvent = Beans.newPropertyChangeEvent(this, s, s3, s2);
            final Iterator<Object> iterator = this.listenerList.iterator();
            while (iterator.hasNext()) {
                Beans.invokePropertyChange(iterator.next(), propertyChangeEvent);
            }
        }
        return s3;
    }
    
    PropMap() {
        this.theMap = new TreeMap<String, String>();
        this.listenerList = new ArrayList<Object>(1);
        this.theMap.putAll(PropMap.defaultProps);
    }
    
    SortedMap<String, String> prefixMap(final String s) {
        final int length = s.length();
        if (length == 0) {
            return this;
        }
        return this.subMap(s, s.substring(0, length - 1) + (char)(s.charAt(length - 1) + '\u0001'));
    }
    
    String getProperty(final String s) {
        return this.get((Object)s);
    }
    
    String getProperty(final String s, final String s2) {
        final String property = this.getProperty(s);
        if (property == null) {
            return s2;
        }
        return property;
    }
    
    String setProperty(final String s, final String s2) {
        return this.put(s, s2);
    }
    
    List<String> getProperties(final String s) {
        final Collection<String> values = this.prefixMap(s).values();
        final ArrayList list = new ArrayList(values.size());
        list.addAll((Collection)values);
        while (list.remove(null)) {}
        return (List<String>)list;
    }
    
    private boolean toBoolean(final String s) {
        return Boolean.valueOf(s);
    }
    
    boolean getBoolean(final String s) {
        return this.toBoolean(this.getProperty(s));
    }
    
    boolean setBoolean(final String s, final boolean b) {
        return this.toBoolean(this.setProperty(s, String.valueOf(b)));
    }
    
    int toInteger(final String s) {
        return this.toInteger(s, 0);
    }
    
    int toInteger(final String s, final int n) {
        if (s == null) {
            return n;
        }
        if ("true".equals(s)) {
            return 1;
        }
        if ("false".equals(s)) {
            return 0;
        }
        return Integer.parseInt(s);
    }
    
    int getInteger(final String s, final int n) {
        return this.toInteger(this.getProperty(s), n);
    }
    
    int getInteger(final String s) {
        return this.toInteger(this.getProperty(s));
    }
    
    int setInteger(final String s, final int n) {
        return this.toInteger(this.setProperty(s, String.valueOf(n)));
    }
    
    long toLong(final String s) {
        try {
            return (s == null) ? 0L : Long.parseLong(s);
        }
        catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid value");
        }
    }
    
    long getLong(final String s) {
        return this.toLong(this.getProperty(s));
    }
    
    long setLong(final String s, final long n) {
        return this.toLong(this.setProperty(s, String.valueOf(n)));
    }
    
    int getTime(final String s) {
        final String property = this.getProperty(s, "0");
        if ("now".equals(property)) {
            return (int)((System.currentTimeMillis() + 500L) / 1000L);
        }
        final long long1 = this.toLong(property);
        if (long1 < 10000000000L && !"0".equals(property)) {
            Utils.log.warning("Supplied modtime appears to be seconds rather than milliseconds: " + property);
        }
        return (int)((long1 + 500L) / 1000L);
    }
    
    void list(final PrintStream printStream) {
        final PrintWriter printWriter = new PrintWriter(printStream);
        this.list(printWriter);
        printWriter.flush();
    }
    
    void list(final PrintWriter printWriter) {
        printWriter.println("#PACK200[");
        final Set<Map.Entry<String, String>> entrySet = PropMap.defaultProps.entrySet();
        for (final Map.Entry entry : this.theMap.entrySet()) {
            if (entrySet.contains(entry)) {
                continue;
            }
            printWriter.println("  " + (String)entry.getKey() + " = " + (String)entry.getValue());
        }
        printWriter.println("#]");
    }
    
    @Override
    public int size() {
        return this.theMap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.theMap.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.theMap.containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.theMap.containsValue(o);
    }
    
    @Override
    public String get(final Object o) {
        return this.theMap.get(o);
    }
    
    @Override
    public String remove(final Object o) {
        return this.theMap.remove(o);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends String> map) {
        this.theMap.putAll(map);
    }
    
    @Override
    public void clear() {
        this.theMap.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.theMap.keySet();
    }
    
    @Override
    public Collection<String> values() {
        return this.theMap.values();
    }
    
    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return this.theMap.entrySet();
    }
    
    @Override
    public Comparator<? super String> comparator() {
        return this.theMap.comparator();
    }
    
    @Override
    public SortedMap<String, String> subMap(final String s, final String s2) {
        return this.theMap.subMap(s, s2);
    }
    
    @Override
    public SortedMap<String, String> headMap(final String s) {
        return this.theMap.headMap(s);
    }
    
    @Override
    public SortedMap<String, String> tailMap(final String s) {
        return this.theMap.tailMap(s);
    }
    
    @Override
    public String firstKey() {
        return this.theMap.firstKey();
    }
    
    @Override
    public String lastKey() {
        return this.theMap.lastKey();
    }
    
    static {
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("com.sun.java.util.jar.pack.disable.native", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.disable.native")));
        ((Hashtable<String, String>)properties).put("com.sun.java.util.jar.pack.verbose", String.valueOf(Integer.getInteger("com.sun.java.util.jar.pack.verbose", 0)));
        ((Hashtable<String, String>)properties).put("com.sun.java.util.jar.pack.default.timezone", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.default.timezone")));
        ((Hashtable<String, String>)properties).put("pack.segment.limit", "-1");
        ((Hashtable<String, String>)properties).put("pack.keep.file.order", "true");
        ((Hashtable<String, String>)properties).put("pack.modification.time", "keep");
        ((Hashtable<String, String>)properties).put("pack.deflate.hint", "keep");
        ((Hashtable<String, String>)properties).put("pack.unknown.attribute", "pass");
        ((Hashtable<String, String>)properties).put("com.sun.java.util.jar.pack.class.format.error", System.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass"));
        ((Hashtable<String, String>)properties).put("pack.effort", "5");
        final String s = "intrinsic.properties";
        try (final InputStream resourceAsStream = PackerImpl.class.getResourceAsStream(s)) {
            if (resourceAsStream == null) {
                throw new RuntimeException(s + " cannot be loaded");
            }
            properties.load(resourceAsStream);
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        for (final Map.Entry entry : ((Hashtable<String, String>)properties).entrySet()) {
            final String s2 = (String)entry.getKey();
            final String s3 = (String)entry.getValue();
            if (s2.startsWith("attribute.")) {
                entry.setValue(Attribute.normalizeLayoutString(s3));
            }
        }
        PropMap.defaultProps = new HashMap<String, String>((Map<? extends String, ? extends String>)properties);
    }
    
    private static class Beans
    {
        private static final Class<?> propertyChangeListenerClass;
        private static final Class<?> propertyChangeEventClass;
        private static final Method propertyChangeMethod;
        private static final Constructor<?> propertyEventCtor;
        
        private static Class<?> getClass(final String s) {
            try {
                return Class.forName(s, true, Beans.class.getClassLoader());
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
        }
        
        private static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... array) {
            try {
                return (clazz == null) ? null : clazz.getDeclaredConstructor(array);
            }
            catch (final NoSuchMethodException ex) {
                throw new AssertionError((Object)ex);
            }
        }
        
        private static Method getMethod(final Class<?> clazz, final String s, final Class<?>... array) {
            try {
                return (clazz == null) ? null : clazz.getMethod(s, array);
            }
            catch (final NoSuchMethodException ex) {
                throw new AssertionError((Object)ex);
            }
        }
        
        static boolean isBeansPresent() {
            return Beans.propertyChangeListenerClass != null && Beans.propertyChangeEventClass != null;
        }
        
        static boolean isPropertyChangeListener(final Object o) {
            return Beans.propertyChangeListenerClass != null && Beans.propertyChangeListenerClass.isInstance(o);
        }
        
        static Object newPropertyChangeEvent(final Object o, final String s, final Object o2, final Object o3) {
            try {
                return Beans.propertyEventCtor.newInstance(o, s, o2, o3);
            }
            catch (final InstantiationException | IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
            catch (final InvocationTargetException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new AssertionError((Object)ex2);
            }
        }
        
        static void invokePropertyChange(final Object o, final Object o2) {
            try {
                Beans.propertyChangeMethod.invoke(o, o2);
            }
            catch (final IllegalAccessException ex) {
                throw new AssertionError((Object)ex);
            }
            catch (final InvocationTargetException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new AssertionError((Object)ex2);
            }
        }
        
        static {
            propertyChangeListenerClass = getClass("java.beans.PropertyChangeListener");
            propertyChangeEventClass = getClass("java.beans.PropertyChangeEvent");
            propertyChangeMethod = getMethod(Beans.propertyChangeListenerClass, "propertyChange", Beans.propertyChangeEventClass);
            propertyEventCtor = getConstructor(Beans.propertyChangeEventClass, Object.class, String.class, Object.class, Object.class);
        }
    }
}
