package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.GenericArrayType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

public final class TypeInfo
{
    public final QName tagName;
    public Type type;
    public final Annotation[] annotations;
    private Map<String, Object> properties;
    private boolean isGlobalElement;
    private TypeInfo parentCollectionType;
    private Type genericType;
    private boolean nillable;
    
    public TypeInfo(final QName tagName, final Type type, final Annotation... annotations) {
        this.properties = new HashMap<String, Object>();
        this.isGlobalElement = true;
        this.nillable = true;
        if (tagName == null || type == null || annotations == null) {
            String nullArgs = "";
            if (tagName == null) {
                nullArgs = "tagName";
            }
            if (type == null) {
                nullArgs += ((nullArgs.length() > 0) ? ", type" : "type");
            }
            if (annotations == null) {
                nullArgs += ((nullArgs.length() > 0) ? ", annotations" : "annotations");
            }
            throw new IllegalArgumentException("Argument(s) \"" + nullArgs + "\" can''t be null.)");
        }
        this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
        this.type = type;
        if (type instanceof Class && ((Class)type).isPrimitive()) {
            this.nillable = false;
        }
        this.annotations = annotations;
    }
    
    public <A extends Annotation> A get(final Class<A> annotationType) {
        for (final Annotation a : this.annotations) {
            if (a.annotationType() == annotationType) {
                return annotationType.cast(a);
            }
        }
        return null;
    }
    
    public TypeInfo toItemType() {
        final Type t = (this.genericType != null) ? this.genericType : this.type;
        final Type base = Utils.REFLECTION_NAVIGATOR.getBaseClass(t, Collection.class);
        if (base == null) {
            return this;
        }
        return new TypeInfo(this.tagName, Utils.REFLECTION_NAVIGATOR.getTypeArgument(base, 0), new Annotation[0]);
    }
    
    public Map<String, Object> properties() {
        return this.properties;
    }
    
    public boolean isGlobalElement() {
        return this.isGlobalElement;
    }
    
    public void setGlobalElement(final boolean isGlobalElement) {
        this.isGlobalElement = isGlobalElement;
    }
    
    public TypeInfo getParentCollectionType() {
        return this.parentCollectionType;
    }
    
    public void setParentCollectionType(final TypeInfo parentCollectionType) {
        this.parentCollectionType = parentCollectionType;
    }
    
    public boolean isRepeatedElement() {
        return this.parentCollectionType != null;
    }
    
    public Type getGenericType() {
        return this.genericType;
    }
    
    public void setGenericType(final Type genericType) {
        this.genericType = genericType;
    }
    
    public boolean isNillable() {
        return this.nillable;
    }
    
    public void setNillable(final boolean nillable) {
        this.nillable = nillable;
    }
    
    @Override
    public String toString() {
        return "TypeInfo: Type = " + this.type + ", tag = " + this.tagName;
    }
    
    public TypeInfo getItemType() {
        if (this.type instanceof Class && ((Class)this.type).isArray() && !byte[].class.equals(this.type)) {
            Type componentType = ((Class)this.type).getComponentType();
            Type genericComponentType = null;
            if (this.genericType != null && this.genericType instanceof GenericArrayType) {
                final GenericArrayType arrayType = (GenericArrayType)this.type;
                genericComponentType = arrayType.getGenericComponentType();
                componentType = arrayType.getGenericComponentType();
            }
            final TypeInfo ti = new TypeInfo(this.tagName, componentType, this.annotations);
            if (genericComponentType != null) {
                ti.setGenericType(genericComponentType);
            }
            return ti;
        }
        final Type t = (this.genericType != null) ? this.genericType : this.type;
        final Type base = Utils.REFLECTION_NAVIGATOR.getBaseClass(t, Collection.class);
        if (base != null) {
            return new TypeInfo(this.tagName, Utils.REFLECTION_NAVIGATOR.getTypeArgument(base, 0), this.annotations);
        }
        return null;
    }
}
