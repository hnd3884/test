package com.azul.crs.com.fasterxml.jackson.databind.util;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import java.util.HashMap;
import java.io.Serializable;

public class EnumResolver implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Class<Enum<?>> _enumClass;
    protected final Enum<?>[] _enums;
    protected final HashMap<String, Enum<?>> _enumsById;
    protected final Enum<?> _defaultValue;
    protected final boolean _isIgnoreCase;
    
    protected EnumResolver(final Class<Enum<?>> enumClass, final Enum<?>[] enums, final HashMap<String, Enum<?>> map, final Enum<?> defaultValue, final boolean isIgnoreCase) {
        this._enumClass = enumClass;
        this._enums = enums;
        this._enumsById = map;
        this._defaultValue = defaultValue;
        this._isIgnoreCase = isIgnoreCase;
    }
    
    public static EnumResolver constructFor(final DeserializationConfig config, final Class<?> enumCls) {
        return _constructFor(enumCls, config.getAnnotationIntrospector(), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
    }
    
    protected static EnumResolver _constructFor(final Class<?> enumCls0, final AnnotationIntrospector ai, final boolean isIgnoreCase) {
        final Class<Enum<?>> enumCls = _enumClass(enumCls0);
        final Enum<?>[] enumConstants = _enumConstants(enumCls0);
        final String[] names = ai.findEnumValues(enumCls, enumConstants, new String[enumConstants.length]);
        final String[][] allAliases = new String[names.length][];
        ai.findEnumAliases(enumCls, enumConstants, allAliases);
        final HashMap<String, Enum<?>> map = new HashMap<String, Enum<?>>();
        for (int i = 0, len = enumConstants.length; i < len; ++i) {
            final Enum<?> enumValue = enumConstants[i];
            String name = names[i];
            if (name == null) {
                name = enumValue.name();
            }
            map.put(name, enumValue);
            final String[] aliases = allAliases[i];
            if (aliases != null) {
                for (final String alias : aliases) {
                    if (!map.containsKey(alias)) {
                        map.put(alias, enumValue);
                    }
                }
            }
        }
        return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, enumCls), isIgnoreCase);
    }
    
    public static EnumResolver constructUsingToString(final DeserializationConfig config, final Class<?> enumCls) {
        return _constructUsingToString(enumCls, config.getAnnotationIntrospector(), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
    }
    
    protected static EnumResolver _constructUsingToString(final Class<?> enumCls0, final AnnotationIntrospector ai, final boolean isIgnoreCase) {
        final Class<Enum<?>> enumCls = _enumClass(enumCls0);
        final Enum<?>[] enumConstants = _enumConstants(enumCls0);
        final HashMap<String, Enum<?>> map = new HashMap<String, Enum<?>>();
        final String[][] allAliases = new String[enumConstants.length][];
        ai.findEnumAliases(enumCls, enumConstants, allAliases);
        int i = enumConstants.length;
        while (--i >= 0) {
            final Enum<?> enumValue = enumConstants[i];
            map.put(enumValue.toString(), enumValue);
            final String[] aliases = allAliases[i];
            if (aliases != null) {
                for (final String alias : aliases) {
                    if (!map.containsKey(alias)) {
                        map.put(alias, enumValue);
                    }
                }
            }
        }
        return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, enumCls), isIgnoreCase);
    }
    
    public static EnumResolver constructUsingMethod(final DeserializationConfig config, final Class<?> enumCls, final AnnotatedMember accessor) {
        return _constructUsingMethod(enumCls, accessor, config.getAnnotationIntrospector(), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
    }
    
    protected static EnumResolver _constructUsingMethod(final Class<?> enumCls0, final AnnotatedMember accessor, final AnnotationIntrospector ai, final boolean isIgnoreCase) {
        final Class<Enum<?>> enumCls = _enumClass(enumCls0);
        final Enum<?>[] enumConstants = _enumConstants(enumCls0);
        final HashMap<String, Enum<?>> map = new HashMap<String, Enum<?>>();
        int i = enumConstants.length;
        while (--i >= 0) {
            final Enum<?> en = enumConstants[i];
            try {
                final Object o = accessor.getValue(en);
                if (o == null) {
                    continue;
                }
                map.put(o.toString(), en);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + e.getMessage());
            }
        }
        return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, enumCls), isIgnoreCase);
    }
    
    public CompactStringObjectMap constructLookup() {
        return CompactStringObjectMap.construct(this._enumsById);
    }
    
    protected static Class<Enum<?>> _enumClass(final Class<?> enumCls0) {
        return (Class<Enum<?>>)enumCls0;
    }
    
    protected static Enum<?>[] _enumConstants(final Class<?> enumCls) {
        final Enum<?>[] enumValues = _enumClass(enumCls).getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
        }
        return enumValues;
    }
    
    protected static Enum<?> _enumDefault(final AnnotationIntrospector intr, final Class<?> enumCls) {
        return (intr != null) ? intr.findDefaultEnumValue(_enumClass(enumCls)) : null;
    }
    
    @Deprecated
    protected EnumResolver(final Class<Enum<?>> enumClass, final Enum<?>[] enums, final HashMap<String, Enum<?>> map, final Enum<?> defaultValue) {
        this(enumClass, enums, map, defaultValue, false);
    }
    
    @Deprecated
    public static EnumResolver constructFor(final Class<Enum<?>> enumCls, final AnnotationIntrospector ai) {
        return _constructFor(enumCls, ai, false);
    }
    
    @Deprecated
    public static EnumResolver constructUnsafe(final Class<?> rawEnumCls, final AnnotationIntrospector ai) {
        return _constructFor(rawEnumCls, ai, false);
    }
    
    @Deprecated
    public static EnumResolver constructUsingToString(final Class<Enum<?>> enumCls, final AnnotationIntrospector ai) {
        return _constructUsingToString(enumCls, ai, false);
    }
    
    @Deprecated
    public static EnumResolver constructUnsafeUsingToString(final Class<?> rawEnumCls, final AnnotationIntrospector ai) {
        return _constructUsingToString(rawEnumCls, ai, false);
    }
    
    @Deprecated
    public static EnumResolver constructUsingToString(final Class<Enum<?>> enumCls) {
        return _constructUsingToString(enumCls, null, false);
    }
    
    @Deprecated
    public static EnumResolver constructUsingMethod(final Class<Enum<?>> enumCls, final AnnotatedMember accessor, final AnnotationIntrospector ai) {
        return _constructUsingMethod(enumCls, accessor, ai, false);
    }
    
    @Deprecated
    public static EnumResolver constructUnsafeUsingMethod(final Class<?> rawEnumCls, final AnnotatedMember accessor, final AnnotationIntrospector ai) {
        return _constructUsingMethod(rawEnumCls, accessor, ai, false);
    }
    
    public Enum<?> findEnum(final String key) {
        final Enum<?> en = this._enumsById.get(key);
        if (en == null && this._isIgnoreCase) {
            return this._findEnumCaseInsensitive(key);
        }
        return en;
    }
    
    protected Enum<?> _findEnumCaseInsensitive(final String key) {
        for (final Map.Entry<String, Enum<?>> entry : this._enumsById.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public Enum<?> getEnum(final int index) {
        if (index < 0 || index >= this._enums.length) {
            return null;
        }
        return this._enums[index];
    }
    
    public Enum<?> getDefaultValue() {
        return this._defaultValue;
    }
    
    public Enum<?>[] getRawEnums() {
        return this._enums;
    }
    
    public List<Enum<?>> getEnums() {
        final ArrayList<Enum<?>> enums = new ArrayList<Enum<?>>(this._enums.length);
        for (final Enum<?> e : this._enums) {
            enums.add(e);
        }
        return enums;
    }
    
    public Collection<String> getEnumIds() {
        return this._enumsById.keySet();
    }
    
    public Class<Enum<?>> getEnumClass() {
        return this._enumClass;
    }
    
    public int lastValidIndex() {
        return this._enums.length - 1;
    }
}
