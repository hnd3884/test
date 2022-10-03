package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.Nullable;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.istack.internal.NotNull;

public class BindingHelper
{
    @NotNull
    public static String mangleNameToVariableName(@NotNull final String localName) {
        return NameConverter.standard.toVariableName(localName);
    }
    
    @NotNull
    public static String mangleNameToClassName(@NotNull final String localName) {
        return NameConverter.standard.toClassName(localName);
    }
    
    @NotNull
    public static String mangleNameToPropertyName(@NotNull final String localName) {
        return NameConverter.standard.toPropertyName(localName);
    }
    
    @Nullable
    public static Type getBaseType(@NotNull final Type type, @NotNull final Class baseType) {
        return Utils.REFLECTION_NAVIGATOR.getBaseClass(type, baseType);
    }
    
    public static <T> Class<T> erasure(final Type t) {
        return Utils.REFLECTION_NAVIGATOR.erasure(t);
    }
}
