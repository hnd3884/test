package com.azul.crs.com.fasterxml.jackson.databind.util;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class BeanUtil
{
    @Deprecated
    public static String okNameForGetter(final AnnotatedMethod am, final boolean stdNaming) {
        final String name = am.getName();
        String str = okNameForIsGetter(am, name, stdNaming);
        if (str == null) {
            str = okNameForRegularGetter(am, name, stdNaming);
        }
        return str;
    }
    
    @Deprecated
    public static String okNameForRegularGetter(final AnnotatedMethod am, final String name, final boolean stdNaming) {
        if (name.startsWith("get")) {
            if ("getCallbacks".equals(name)) {
                if (isCglibGetCallbacks(am)) {
                    return null;
                }
            }
            else if ("getMetaClass".equals(name) && isGroovyMetaClassGetter(am)) {
                return null;
            }
            return stdNaming ? stdManglePropertyName(name, 3) : legacyManglePropertyName(name, 3);
        }
        return null;
    }
    
    @Deprecated
    public static String okNameForIsGetter(final AnnotatedMethod am, final String name, final boolean stdNaming) {
        if (name.startsWith("is")) {
            final Class<?> rt = am.getRawType();
            if (rt == Boolean.class || rt == Boolean.TYPE) {
                return stdNaming ? stdManglePropertyName(name, 2) : legacyManglePropertyName(name, 2);
            }
        }
        return null;
    }
    
    @Deprecated
    public static String okNameForSetter(final AnnotatedMethod am, final boolean stdNaming) {
        return okNameForMutator(am, "set", stdNaming);
    }
    
    @Deprecated
    public static String okNameForMutator(final AnnotatedMethod am, final String prefix, final boolean stdNaming) {
        final String name = am.getName();
        if (name.startsWith(prefix)) {
            return stdNaming ? stdManglePropertyName(name, prefix.length()) : legacyManglePropertyName(name, prefix.length());
        }
        return null;
    }
    
    public static Object getDefaultValue(final JavaType type) {
        final Class<?> cls = type.getRawClass();
        final Class<?> prim = ClassUtil.primitiveType(cls);
        if (prim != null) {
            return ClassUtil.defaultValue(prim);
        }
        if (type.isContainerType() || type.isReferenceType()) {
            return JsonInclude.Include.NON_EMPTY;
        }
        if (cls == String.class) {
            return "";
        }
        if (type.isTypeOrSubTypeOf(Date.class)) {
            return new Date(0L);
        }
        if (type.isTypeOrSubTypeOf(Calendar.class)) {
            final Calendar c = new GregorianCalendar();
            c.setTimeInMillis(0L);
            return c;
        }
        return null;
    }
    
    protected static boolean isCglibGetCallbacks(final AnnotatedMethod am) {
        final Class<?> rt = am.getRawType();
        if (rt.isArray()) {
            final Class<?> compType = rt.getComponentType();
            final String className = compType.getName();
            if (className.contains(".cglib")) {
                return className.startsWith("net.sf.cglib") || className.startsWith("org.hibernate.repackage.cglib") || className.startsWith("org.springframework.cglib");
            }
        }
        return false;
    }
    
    protected static boolean isGroovyMetaClassGetter(final AnnotatedMethod am) {
        return am.getRawType().getName().startsWith("groovy.lang");
    }
    
    protected static String legacyManglePropertyName(final String basename, final int offset) {
        final int end = basename.length();
        if (end == offset) {
            return null;
        }
        char c = basename.charAt(offset);
        char d = Character.toLowerCase(c);
        if (c == d) {
            return basename.substring(offset);
        }
        final StringBuilder sb = new StringBuilder(end - offset);
        sb.append(d);
        for (int i = offset + 1; i < end; ++i) {
            c = basename.charAt(i);
            d = Character.toLowerCase(c);
            if (c == d) {
                sb.append(basename, i, end);
                break;
            }
            sb.append(d);
        }
        return sb.toString();
    }
    
    public static String stdManglePropertyName(final String basename, final int offset) {
        final int end = basename.length();
        if (end == offset) {
            return null;
        }
        final char c0 = basename.charAt(offset);
        final char c2 = Character.toLowerCase(c0);
        if (c0 == c2) {
            return basename.substring(offset);
        }
        if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
            return basename.substring(offset);
        }
        final StringBuilder sb = new StringBuilder(end - offset);
        sb.append(c2);
        sb.append(basename, offset + 1, end);
        return sb.toString();
    }
    
    public static String checkUnsupportedType(final JavaType type) {
        final Class<?> rawType = type.getRawClass();
        String typeName;
        String moduleName;
        if (isJava8TimeClass(rawType)) {
            typeName = "Java 8 date/time";
            moduleName = "com.azul.crs.com.fasterxml.jackson.datatype:jackson-datatype-jsr310";
        }
        else {
            if (!isJodaTimeClass(rawType)) {
                return null;
            }
            typeName = "Joda date/time";
            moduleName = "com.azul.crs.com.fasterxml.jackson.datatype:jackson-datatype-joda";
        }
        return String.format("%s type %s not supported by default: add Module \"%s\" to enable handling", typeName, ClassUtil.getTypeDescription(type), moduleName);
    }
    
    public static boolean isJava8TimeClass(final Class<?> rawType) {
        return rawType.getName().startsWith("java.time.");
    }
    
    public static boolean isJodaTimeClass(final Class<?> rawType) {
        return rawType.getName().startsWith("org.joda.time.");
    }
}
