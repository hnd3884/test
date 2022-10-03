package com.unboundid.ldap.sdk.persist;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Attribute;
import java.lang.reflect.Method;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class ObjectEncoder implements Serializable
{
    private static final long serialVersionUID = -5139516629886911696L;
    
    public abstract boolean supportsType(final Type p0);
    
    public final AttributeTypeDefinition constructAttributeType(final Field f) throws LDAPPersistException {
        return this.constructAttributeType(f, DefaultOIDAllocator.getInstance());
    }
    
    public abstract AttributeTypeDefinition constructAttributeType(final Field p0, final OIDAllocator p1) throws LDAPPersistException;
    
    public final AttributeTypeDefinition constructAttributeType(final Method m) throws LDAPPersistException {
        return this.constructAttributeType(m, DefaultOIDAllocator.getInstance());
    }
    
    public abstract AttributeTypeDefinition constructAttributeType(final Method p0, final OIDAllocator p1) throws LDAPPersistException;
    
    public abstract boolean supportsMultipleValues(final Field p0);
    
    public abstract boolean supportsMultipleValues(final Method p0);
    
    public abstract Attribute encodeFieldValue(final Field p0, final Object p1, final String p2) throws LDAPPersistException;
    
    public abstract Attribute encodeMethodValue(final Method p0, final Object p1, final String p2) throws LDAPPersistException;
    
    public abstract void decodeField(final Field p0, final Object p1, final Attribute p2) throws LDAPPersistException;
    
    public void setNull(final Field f, final Object o) throws LDAPPersistException {
        try {
            f.setAccessible(true);
            final Class<?> type = f.getType();
            if (type.equals(Boolean.TYPE)) {
                f.set(o, Boolean.FALSE);
            }
            else if (type.equals(Byte.TYPE)) {
                f.set(o, 0);
            }
            else if (type.equals(Character.TYPE)) {
                f.set(o, '\0');
            }
            else if (type.equals(Double.TYPE)) {
                f.set(o, 0.0);
            }
            else if (type.equals(Float.TYPE)) {
                f.set(o, 0.0f);
            }
            else if (type.equals(Integer.TYPE)) {
                f.set(o, 0);
            }
            else if (type.equals(Long.TYPE)) {
                f.set(o, 0L);
            }
            else if (type.equals(Short.TYPE)) {
                f.set(o, 0);
            }
            else {
                f.set(o, null);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_ENCODER_CANNOT_SET_NULL_FIELD_VALUE.get(f.getName(), o.getClass().getName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public void setNull(final Method m, final Object o) throws LDAPPersistException {
        try {
            m.setAccessible(true);
            final Class<?> type = m.getParameterTypes()[0];
            if (type.equals(Boolean.TYPE)) {
                m.invoke(o, Boolean.FALSE);
            }
            else if (type.equals(Byte.TYPE)) {
                m.invoke(o, 0);
            }
            else if (type.equals(Character.TYPE)) {
                m.invoke(o, '\0');
            }
            else if (type.equals(Double.TYPE)) {
                m.invoke(o, 0.0);
            }
            else if (type.equals(Float.TYPE)) {
                m.invoke(o, 0.0f);
            }
            else if (type.equals(Integer.TYPE)) {
                m.invoke(o, 0);
            }
            else if (type.equals(Long.TYPE)) {
                m.invoke(o, 0L);
            }
            else if (type.equals(Short.TYPE)) {
                m.invoke(o, 0);
            }
            else {
                m.invoke(o, type.cast(null));
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_ENCODER_CANNOT_SET_NULL_METHOD_VALUE.get(m.getName(), o.getClass().getName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public abstract void invokeSetter(final Method p0, final Object p1, final Attribute p2) throws LDAPPersistException;
}
