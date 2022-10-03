package com.fasterxml.jackson.databind;

import java.util.List;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Modifier;
import com.fasterxml.jackson.databind.type.TypeBindings;
import java.lang.reflect.Type;
import java.io.Serializable;
import com.fasterxml.jackson.core.type.ResolvedType;

public abstract class JavaType extends ResolvedType implements Serializable, Type
{
    private static final long serialVersionUID = 1L;
    protected final Class<?> _class;
    protected final int _hash;
    protected final Object _valueHandler;
    protected final Object _typeHandler;
    protected final boolean _asStatic;
    
    protected JavaType(final Class<?> raw, final int additionalHash, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        this._class = raw;
        this._hash = raw.getName().hashCode() + additionalHash;
        this._valueHandler = valueHandler;
        this._typeHandler = typeHandler;
        this._asStatic = asStatic;
    }
    
    protected JavaType(final JavaType base) {
        this._class = base._class;
        this._hash = base._hash;
        this._valueHandler = base._valueHandler;
        this._typeHandler = base._typeHandler;
        this._asStatic = base._asStatic;
    }
    
    public abstract JavaType withTypeHandler(final Object p0);
    
    public abstract JavaType withContentTypeHandler(final Object p0);
    
    public abstract JavaType withValueHandler(final Object p0);
    
    public abstract JavaType withContentValueHandler(final Object p0);
    
    public JavaType withHandlersFrom(final JavaType src) {
        JavaType type = this;
        Object h = src.getTypeHandler();
        if (h != this._typeHandler) {
            type = type.withTypeHandler(h);
        }
        h = src.getValueHandler();
        if (h != this._valueHandler) {
            type = type.withValueHandler(h);
        }
        return type;
    }
    
    public abstract JavaType withContentType(final JavaType p0);
    
    public abstract JavaType withStaticTyping();
    
    public abstract JavaType refine(final Class<?> p0, final TypeBindings p1, final JavaType p2, final JavaType[] p3);
    
    @Deprecated
    public JavaType forcedNarrowBy(final Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        return this._narrow(subclass);
    }
    
    @Deprecated
    protected abstract JavaType _narrow(final Class<?> p0);
    
    public final Class<?> getRawClass() {
        return this._class;
    }
    
    public final boolean hasRawClass(final Class<?> clz) {
        return this._class == clz;
    }
    
    public boolean hasContentType() {
        return true;
    }
    
    public final boolean isTypeOrSubTypeOf(final Class<?> clz) {
        return this._class == clz || clz.isAssignableFrom(this._class);
    }
    
    public final boolean isTypeOrSuperTypeOf(final Class<?> clz) {
        return this._class == clz || this._class.isAssignableFrom(clz);
    }
    
    public boolean isAbstract() {
        return Modifier.isAbstract(this._class.getModifiers());
    }
    
    public boolean isConcrete() {
        final int mod = this._class.getModifiers();
        return (mod & 0x600) == 0x0 || this._class.isPrimitive();
    }
    
    public boolean isThrowable() {
        return Throwable.class.isAssignableFrom(this._class);
    }
    
    public boolean isArrayType() {
        return false;
    }
    
    public final boolean isEnumType() {
        return ClassUtil.isEnumType(this._class);
    }
    
    public final boolean isEnumImplType() {
        return ClassUtil.isEnumType(this._class) && this._class != Enum.class;
    }
    
    public final boolean isInterface() {
        return this._class.isInterface();
    }
    
    public final boolean isPrimitive() {
        return this._class.isPrimitive();
    }
    
    public final boolean isFinal() {
        return Modifier.isFinal(this._class.getModifiers());
    }
    
    public abstract boolean isContainerType();
    
    public boolean isCollectionLikeType() {
        return false;
    }
    
    public boolean isMapLikeType() {
        return false;
    }
    
    public final boolean isJavaLangObject() {
        return this._class == Object.class;
    }
    
    public final boolean useStaticType() {
        return this._asStatic;
    }
    
    public boolean hasGenericTypes() {
        return this.containedTypeCount() > 0;
    }
    
    public JavaType getKeyType() {
        return null;
    }
    
    public JavaType getContentType() {
        return null;
    }
    
    public JavaType getReferencedType() {
        return null;
    }
    
    public abstract int containedTypeCount();
    
    public abstract JavaType containedType(final int p0);
    
    @Deprecated
    public abstract String containedTypeName(final int p0);
    
    @Deprecated
    public Class<?> getParameterSource() {
        return null;
    }
    
    public JavaType containedTypeOrUnknown(final int index) {
        final JavaType t = this.containedType(index);
        return (t == null) ? TypeFactory.unknownType() : t;
    }
    
    public abstract TypeBindings getBindings();
    
    public abstract JavaType findSuperType(final Class<?> p0);
    
    public abstract JavaType getSuperClass();
    
    public abstract List<JavaType> getInterfaces();
    
    public abstract JavaType[] findTypeParameters(final Class<?> p0);
    
    public <T> T getValueHandler() {
        return (T)this._valueHandler;
    }
    
    public <T> T getTypeHandler() {
        return (T)this._typeHandler;
    }
    
    public Object getContentValueHandler() {
        return null;
    }
    
    public Object getContentTypeHandler() {
        return null;
    }
    
    public boolean hasValueHandler() {
        return this._valueHandler != null;
    }
    
    public boolean hasHandlers() {
        return this._typeHandler != null || this._valueHandler != null;
    }
    
    public String getGenericSignature() {
        final StringBuilder sb = new StringBuilder(40);
        this.getGenericSignature(sb);
        return sb.toString();
    }
    
    public abstract StringBuilder getGenericSignature(final StringBuilder p0);
    
    public String getErasedSignature() {
        final StringBuilder sb = new StringBuilder(40);
        this.getErasedSignature(sb);
        return sb.toString();
    }
    
    public abstract StringBuilder getErasedSignature(final StringBuilder p0);
    
    public abstract String toString();
    
    public abstract boolean equals(final Object p0);
    
    public final int hashCode() {
        return this._hash;
    }
}
