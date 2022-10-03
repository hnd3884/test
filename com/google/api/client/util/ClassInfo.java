package com.google.api.client.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Collection;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ClassInfo
{
    private static final ConcurrentMap<Class<?>, ClassInfo> CACHE;
    private static final ConcurrentMap<Class<?>, ClassInfo> CACHE_IGNORE_CASE;
    private final Class<?> clazz;
    private final boolean ignoreCase;
    private final IdentityHashMap<String, FieldInfo> nameToFieldInfoMap;
    final List<String> names;
    
    public static ClassInfo of(final Class<?> underlyingClass) {
        return of(underlyingClass, false);
    }
    
    public static ClassInfo of(final Class<?> underlyingClass, final boolean ignoreCase) {
        if (underlyingClass == null) {
            return null;
        }
        final ConcurrentMap<Class<?>, ClassInfo> cache = ignoreCase ? ClassInfo.CACHE_IGNORE_CASE : ClassInfo.CACHE;
        ClassInfo v;
        final ClassInfo newValue;
        return ((v = cache.get(underlyingClass)) == null && (newValue = new ClassInfo(underlyingClass, ignoreCase)) != null && (v = cache.putIfAbsent(underlyingClass, newValue)) == null) ? newValue : v;
    }
    
    public Class<?> getUnderlyingClass() {
        return this.clazz;
    }
    
    public final boolean getIgnoreCase() {
        return this.ignoreCase;
    }
    
    public FieldInfo getFieldInfo(String name) {
        if (name != null) {
            if (this.ignoreCase) {
                name = name.toLowerCase(Locale.US);
            }
            name = name.intern();
        }
        return this.nameToFieldInfoMap.get(name);
    }
    
    public Field getField(final String name) {
        final FieldInfo fieldInfo = this.getFieldInfo(name);
        return (fieldInfo == null) ? null : fieldInfo.getField();
    }
    
    public boolean isEnum() {
        return this.clazz.isEnum();
    }
    
    public Collection<String> getNames() {
        return this.names;
    }
    
    private ClassInfo(final Class<?> srcClass, final boolean ignoreCase) {
        this.nameToFieldInfoMap = new IdentityHashMap<String, FieldInfo>();
        this.clazz = srcClass;
        this.ignoreCase = ignoreCase;
        Preconditions.checkArgument(!ignoreCase || !srcClass.isEnum(), (Object)("cannot ignore case on an enum: " + srcClass));
        final TreeSet<String> nameSet = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(final String s0, final String s1) {
                return Objects.equal(s0, s1) ? 0 : ((s0 == null) ? -1 : ((s1 == null) ? 1 : s0.compareTo(s1)));
            }
        });
        for (final Field field : srcClass.getDeclaredFields()) {
            final FieldInfo fieldInfo = FieldInfo.of(field);
            if (fieldInfo != null) {
                String fieldName = fieldInfo.getName();
                if (ignoreCase) {
                    fieldName = fieldName.toLowerCase(Locale.US).intern();
                }
                final FieldInfo conflictingFieldInfo = this.nameToFieldInfoMap.get(fieldName);
                Preconditions.checkArgument(conflictingFieldInfo == null, "two fields have the same %sname <%s>: %s and %s", ignoreCase ? "case-insensitive " : "", fieldName, field, (conflictingFieldInfo == null) ? null : conflictingFieldInfo.getField());
                this.nameToFieldInfoMap.put(fieldName, fieldInfo);
                nameSet.add(fieldName);
            }
        }
        final Class<?> superClass = srcClass.getSuperclass();
        if (superClass != null) {
            final ClassInfo superClassInfo = of(superClass, ignoreCase);
            nameSet.addAll(superClassInfo.names);
            for (final Map.Entry<String, FieldInfo> e : superClassInfo.nameToFieldInfoMap.entrySet()) {
                final String name = e.getKey();
                if (!this.nameToFieldInfoMap.containsKey(name)) {
                    this.nameToFieldInfoMap.put(name, e.getValue());
                }
            }
        }
        this.names = (nameSet.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(nameSet)));
    }
    
    public Collection<FieldInfo> getFieldInfos() {
        return Collections.unmodifiableCollection((Collection<? extends FieldInfo>)this.nameToFieldInfoMap.values());
    }
    
    static {
        CACHE = new ConcurrentHashMap<Class<?>, ClassInfo>();
        CACHE_IGNORE_CASE = new ConcurrentHashMap<Class<?>, ClassInfo>();
    }
}
