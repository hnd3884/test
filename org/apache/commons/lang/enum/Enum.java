package org.apache.commons.lang.enum;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang.ClassUtils;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import java.util.Map;
import java.io.Serializable;

public abstract class Enum implements Comparable, Serializable
{
    private static final long serialVersionUID = -487045951170455942L;
    private static final Map EMPTY_MAP;
    private static final Map cEnumClasses;
    private final String iName;
    private final transient int iHashCode;
    protected transient String iToString;
    
    protected Enum(final String name) {
        this.iToString = null;
        this.init(name);
        this.iName = name;
        this.iHashCode = 7 + this.getEnumClass().hashCode() + 3 * name.hashCode();
    }
    
    private void init(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The Enum name must not be empty or null");
        }
        final Class enumClass = this.getEnumClass();
        if (enumClass == null) {
            throw new IllegalArgumentException("getEnumClass() must not be null");
        }
        Class cls = this.getClass();
        boolean ok = false;
        while (cls != null && cls != Enum.class && cls != ValuedEnum.class) {
            if (cls == enumClass) {
                ok = true;
                break;
            }
            cls = cls.getSuperclass();
        }
        if (!ok) {
            throw new IllegalArgumentException("getEnumClass() must return a superclass of this class");
        }
        Entry entry = Enum.cEnumClasses.get(enumClass);
        if (entry == null) {
            entry = createEntry(enumClass);
            Enum.cEnumClasses.put(enumClass, entry);
        }
        if (entry.map.containsKey(name)) {
            throw new IllegalArgumentException("The Enum name must be unique, '" + name + "' has already been added");
        }
        entry.map.put(name, this);
        entry.list.add(this);
    }
    
    protected Object readResolve() {
        final Entry entry = Enum.cEnumClasses.get(this.getEnumClass());
        if (entry == null) {
            return null;
        }
        return entry.map.get(this.getName());
    }
    
    protected static Enum getEnum(final Class enumClass, final String name) {
        final Entry entry = getEntry(enumClass);
        if (entry == null) {
            return null;
        }
        return entry.map.get(name);
    }
    
    protected static Map getEnumMap(final Class enumClass) {
        final Entry entry = getEntry(enumClass);
        if (entry == null) {
            return Enum.EMPTY_MAP;
        }
        return entry.unmodifiableMap;
    }
    
    protected static List getEnumList(final Class enumClass) {
        final Entry entry = getEntry(enumClass);
        if (entry == null) {
            return Collections.EMPTY_LIST;
        }
        return entry.unmodifiableList;
    }
    
    protected static Iterator iterator(final Class enumClass) {
        return getEnumList(enumClass).iterator();
    }
    
    private static Entry getEntry(final Class enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("The Enum Class must not be null");
        }
        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("The Class must be a subclass of Enum");
        }
        final Entry entry = Enum.cEnumClasses.get(enumClass);
        return entry;
    }
    
    private static Entry createEntry(final Class enumClass) {
        final Entry entry = new Entry();
        for (Class cls = enumClass.getSuperclass(); cls != null && cls != Enum.class && cls != ValuedEnum.class; cls = cls.getSuperclass()) {
            final Entry loopEntry = Enum.cEnumClasses.get(cls);
            if (loopEntry != null) {
                entry.list.addAll(loopEntry.list);
                entry.map.putAll(loopEntry.map);
                break;
            }
        }
        return entry;
    }
    
    public final String getName() {
        return this.iName;
    }
    
    public Class getEnumClass() {
        return this.getClass();
    }
    
    public final boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() == this.getClass()) {
            return this.iName.equals(((Enum)other).iName);
        }
        if (((Enum)other).getEnumClass().getName().equals(this.getEnumClass().getName())) {
            try {
                return this.iName.equals(((Enum)other).iName);
            }
            catch (final ClassCastException ex) {
                try {
                    final Method mth = other.getClass().getMethod("getName", (Class<?>[])null);
                    final String name = (String)mth.invoke(other, (Object[])null);
                    return this.iName.equals(name);
                }
                catch (final NoSuchMethodException ex2) {}
                catch (final IllegalAccessException ex3) {}
                catch (final InvocationTargetException ex4) {}
                return false;
            }
        }
        return false;
    }
    
    public final int hashCode() {
        return this.iHashCode;
    }
    
    public int compareTo(final Object other) {
        if (other == this) {
            return 0;
        }
        return this.iName.compareTo(((Enum)other).iName);
    }
    
    public String toString() {
        if (this.iToString == null) {
            final String shortName = ClassUtils.getShortClassName(this.getEnumClass());
            this.iToString = shortName + "[" + this.getName() + "]";
        }
        return this.iToString;
    }
    
    static {
        EMPTY_MAP = Collections.unmodifiableMap((Map<?, ?>)new HashMap<Object, Object>(0));
        cEnumClasses = new HashMap();
    }
    
    private static class Entry
    {
        final Map map;
        final Map unmodifiableMap;
        final List list;
        final List unmodifiableList;
        
        private Entry() {
            this.map = new HashMap();
            this.unmodifiableMap = Collections.unmodifiableMap((Map<?, ?>)this.map);
            this.list = new ArrayList(25);
            this.unmodifiableList = Collections.unmodifiableList((List<?>)this.list);
        }
    }
}
