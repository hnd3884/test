package com.sun.xml.internal.bind.v2.model.nav;

import java.util.Arrays;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

class ParameterizedTypeImpl implements ParameterizedType
{
    private Type[] actualTypeArguments;
    private Class<?> rawType;
    private Type ownerType;
    
    ParameterizedTypeImpl(final Class<?> rawType, final Type[] actualTypeArguments, final Type ownerType) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        if (ownerType != null) {
            this.ownerType = ownerType;
        }
        else {
            this.ownerType = rawType.getDeclaringClass();
        }
        this.validateConstructorArguments();
    }
    
    private void validateConstructorArguments() {
        final TypeVariable[] formals = this.rawType.getTypeParameters();
        if (formals.length != this.actualTypeArguments.length) {
            throw new MalformedParameterizedTypeException();
        }
    }
    
    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments.clone();
    }
    
    @Override
    public Class<?> getRawType() {
        return this.rawType;
    }
    
    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType that = (ParameterizedType)o;
        if (this == that) {
            return true;
        }
        final Type thatOwner = that.getOwnerType();
        final Type thatRawType = that.getRawType();
        if (this.ownerType == null) {
            if (thatOwner != null) {
                return false;
            }
        }
        else if (!this.ownerType.equals(thatOwner)) {
            return false;
        }
        if (this.rawType == null) {
            if (thatRawType != null) {
                return false;
            }
        }
        else if (!this.rawType.equals(thatRawType)) {
            return false;
        }
        if (Arrays.equals(this.actualTypeArguments, that.getActualTypeArguments())) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.actualTypeArguments) ^ ((this.ownerType == null) ? 0 : this.ownerType.hashCode()) ^ ((this.rawType == null) ? 0 : this.rawType.hashCode());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.ownerType != null) {
            if (this.ownerType instanceof Class) {
                sb.append(((Class)this.ownerType).getName());
            }
            else {
                sb.append(this.ownerType.toString());
            }
            sb.append(".");
            if (this.ownerType instanceof ParameterizedTypeImpl) {
                sb.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
            }
            else {
                sb.append(this.rawType.getName());
            }
        }
        else {
            sb.append(this.rawType.getName());
        }
        if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
            sb.append("<");
            boolean first = true;
            for (final Type t : this.actualTypeArguments) {
                if (!first) {
                    sb.append(", ");
                }
                if (t instanceof Class) {
                    sb.append(((Class)t).getName());
                }
                else {
                    sb.append(t.toString());
                }
                first = false;
            }
            sb.append(">");
        }
        return sb.toString();
    }
}
