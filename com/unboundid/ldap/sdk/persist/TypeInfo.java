package com.unboundid.ldap.sdk.persist;

import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.List;
import java.lang.reflect.Type;

final class TypeInfo
{
    private final boolean isArray;
    private final boolean isEnum;
    private final boolean isList;
    private final boolean isSet;
    private final boolean isSupported;
    private final Class<?> baseClass;
    private final Class<?> componentType;
    private final Type type;
    
    TypeInfo(final Type type) {
        this.type = type;
        if (type instanceof Class) {
            this.isSupported = true;
            this.baseClass = (Class)type;
            this.isArray = this.baseClass.isArray();
            this.isEnum = this.baseClass.isEnum();
            if (this.isArray) {
                this.componentType = this.baseClass.getComponentType();
                this.isList = false;
                this.isSet = false;
            }
            else if (List.class.isAssignableFrom(this.baseClass)) {
                this.componentType = Object.class;
                this.isList = true;
                this.isSet = false;
            }
            else if (Set.class.isAssignableFrom(this.baseClass)) {
                this.componentType = Object.class;
                this.isList = false;
                this.isSet = true;
            }
            else {
                this.componentType = null;
                this.isList = false;
                this.isSet = false;
            }
        }
        else if (type instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)type;
            final Type rawType = pt.getRawType();
            final Type[] typeParams = pt.getActualTypeArguments();
            if (rawType instanceof Class && typeParams.length == 1 && typeParams[0] instanceof Class) {
                this.baseClass = (Class)rawType;
                this.componentType = (Class)typeParams[0];
                if (List.class.isAssignableFrom(this.baseClass)) {
                    this.isSupported = true;
                    this.isArray = false;
                    this.isEnum = false;
                    this.isList = true;
                    this.isSet = false;
                }
                else if (Set.class.isAssignableFrom(this.baseClass)) {
                    this.isSupported = true;
                    this.isArray = false;
                    this.isEnum = false;
                    this.isList = false;
                    this.isSet = true;
                }
                else {
                    this.isSupported = false;
                    this.isArray = false;
                    this.isEnum = false;
                    this.isList = false;
                    this.isSet = false;
                }
            }
            else {
                this.isSupported = false;
                this.isArray = false;
                this.isEnum = false;
                this.isList = false;
                this.isSet = false;
                this.baseClass = null;
                this.componentType = null;
            }
        }
        else {
            this.isSupported = false;
            this.isArray = false;
            this.isEnum = false;
            this.isList = false;
            this.isSet = false;
            this.baseClass = null;
            this.componentType = null;
        }
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean isSupported() {
        return this.isSupported;
    }
    
    public Class<?> getBaseClass() {
        return this.baseClass;
    }
    
    public Class<?> getComponentType() {
        return this.componentType;
    }
    
    public boolean isArray() {
        return this.isArray;
    }
    
    public boolean isEnum() {
        return this.isEnum;
    }
    
    public boolean isList() {
        return this.isList;
    }
    
    public boolean isSet() {
        return this.isSet;
    }
    
    public boolean isMultiValued() {
        return this.isArray || this.isList || this.isSet;
    }
}
