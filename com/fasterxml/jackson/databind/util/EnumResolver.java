package com.fasterxml.jackson.databind.util;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.HashMap;
import java.io.Serializable;

public class EnumResolver implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Class<Enum<?>> _enumClass;
    protected final Enum<?>[] _enums;
    protected final HashMap<String, Enum<?>> _enumsById;
    protected final Enum<?> _defaultValue;
    
    protected EnumResolver(final Class<Enum<?>> enumClass, final Enum<?>[] enums, final HashMap<String, Enum<?>> map, final Enum<?> defaultValue) {
        this._enumClass = enumClass;
        this._enums = enums;
        this._enumsById = map;
        this._defaultValue = defaultValue;
    }
    
    public static EnumResolver constructFor(final Class<Enum<?>> enumCls, final AnnotationIntrospector ai) {
        final Enum<?>[] enumValues = enumCls.getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
        }
        final String[] names = ai.findEnumValues(enumCls, enumValues, new String[enumValues.length]);
        final String[][] allAliases = new String[names.length][];
        ai.findEnumAliases(enumCls, enumValues, allAliases);
        final HashMap<String, Enum<?>> map = new HashMap<String, Enum<?>>();
        for (int i = 0, len = enumValues.length; i < len; ++i) {
            final Enum<?> enumValue = enumValues[i];
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
        return new EnumResolver(enumCls, enumValues, map, ai.findDefaultEnumValue(enumCls));
    }
    
    @Deprecated
    public static EnumResolver constructUsingToString(final Class<Enum<?>> enumCls) {
        return constructUsingToString(enumCls, null);
    }
    
    public static EnumResolver constructUsingToString(final Class<Enum<?>> enumCls, final AnnotationIntrospector ai) {
        final Enum<?>[] enumConstants = enumCls.getEnumConstants();
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
        return new EnumResolver(enumCls, enumConstants, map, ai.findDefaultEnumValue(enumCls));
    }
    
    public static EnumResolver constructUsingMethod(final Class<Enum<?>> enumCls, final AnnotatedMember accessor, final AnnotationIntrospector ai) {
        final Enum<?>[] enumValues = enumCls.getEnumConstants();
        final HashMap<String, Enum<?>> map = new HashMap<String, Enum<?>>();
        int i = enumValues.length;
        while (--i >= 0) {
            final Enum<?> en = enumValues[i];
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
        final Enum<?> defaultEnum = (ai != null) ? ai.findDefaultEnumValue(enumCls) : null;
        return new EnumResolver(enumCls, enumValues, map, defaultEnum);
    }
    
    public static EnumResolver constructUnsafe(final Class<?> rawEnumCls, final AnnotationIntrospector ai) {
        final Class<Enum<?>> enumCls = (Class<Enum<?>>)rawEnumCls;
        return constructFor(enumCls, ai);
    }
    
    public static EnumResolver constructUnsafeUsingToString(final Class<?> rawEnumCls, final AnnotationIntrospector ai) {
        final Class<Enum<?>> enumCls = (Class<Enum<?>>)rawEnumCls;
        return constructUsingToString(enumCls, ai);
    }
    
    public static EnumResolver constructUnsafeUsingMethod(final Class<?> rawEnumCls, final AnnotatedMember accessor, final AnnotationIntrospector ai) {
        final Class<Enum<?>> enumCls = (Class<Enum<?>>)rawEnumCls;
        return constructUsingMethod(enumCls, accessor, ai);
    }
    
    public CompactStringObjectMap constructLookup() {
        return CompactStringObjectMap.construct(this._enumsById);
    }
    
    public Enum<?> findEnum(final String key) {
        return this._enumsById.get(key);
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
