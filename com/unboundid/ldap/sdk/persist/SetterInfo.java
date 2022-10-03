package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.ldap.sdk.Entry;
import java.lang.reflect.Type;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import java.lang.reflect.Method;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SetterInfo implements Serializable
{
    private static final long serialVersionUID = -1743750276508505946L;
    private final boolean failOnInvalidValue;
    private final boolean failOnTooManyValues;
    private final boolean supportsMultipleValues;
    private final Class<?> containingClass;
    private final Method method;
    private final ObjectEncoder encoder;
    private final String attributeName;
    
    SetterInfo(final Method m, final Class<?> c) throws LDAPPersistException {
        Validator.ensureNotNull(m, c);
        (this.method = m).setAccessible(true);
        final LDAPSetter a = m.getAnnotation(LDAPSetter.class);
        if (a == null) {
            throw new LDAPPersistException(PersistMessages.ERR_SETTER_INFO_METHOD_NOT_ANNOTATED.get(m.getName(), c.getName()));
        }
        final LDAPObject o = c.getAnnotation(LDAPObject.class);
        if (o == null) {
            throw new LDAPPersistException(PersistMessages.ERR_SETTER_INFO_CLASS_NOT_ANNOTATED.get(c.getName()));
        }
        this.containingClass = c;
        this.failOnInvalidValue = a.failOnInvalidValue();
        final Type[] params = m.getGenericParameterTypes();
        if (params.length != 1) {
            throw new LDAPPersistException(PersistMessages.ERR_SETTER_INFO_METHOD_DOES_NOT_TAKE_ONE_ARGUMENT.get(m.getName(), c.getName()));
        }
        try {
            this.encoder = (ObjectEncoder)a.encoderClass().newInstance();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_SETTER_INFO_CANNOT_GET_ENCODER.get(a.encoderClass().getName(), m.getName(), c.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
        if (!this.encoder.supportsType(params[0])) {
            throw new LDAPPersistException(PersistMessages.ERR_SETTER_INFO_ENCODER_UNSUPPORTED_TYPE.get(this.encoder.getClass().getName(), m.getName(), c.getName(), String.valueOf(params[0])));
        }
        this.supportsMultipleValues = this.encoder.supportsMultipleValues(m);
        if (this.supportsMultipleValues) {
            this.failOnTooManyValues = false;
        }
        else {
            this.failOnTooManyValues = a.failOnTooManyValues();
        }
        final String attrName = a.attribute();
        if (attrName == null || attrName.isEmpty()) {
            final String methodName = m.getName();
            if (!methodName.startsWith("set") || methodName.length() < 4) {
                throw new LDAPPersistException(PersistMessages.ERR_SETTER_INFO_CANNOT_INFER_ATTR.get(methodName, c.getName()));
            }
            this.attributeName = StaticUtils.toInitialLowerCase(methodName.substring(3));
        }
        else {
            this.attributeName = attrName;
        }
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public Class<?> getContainingClass() {
        return this.containingClass;
    }
    
    public boolean failOnInvalidValue() {
        return this.failOnInvalidValue;
    }
    
    public boolean failOnTooManyValues() {
        return this.failOnTooManyValues;
    }
    
    public ObjectEncoder getEncoder() {
        return this.encoder;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public boolean supportsMultipleValues() {
        return this.supportsMultipleValues;
    }
    
    boolean invokeSetter(final Object o, final Entry e, final List<String> failureReasons) {
        boolean successful = true;
        final Attribute a = e.getAttribute(this.attributeName);
        if (a != null) {
            if (a.hasValue()) {
                if (this.failOnTooManyValues && a.size() > 1) {
                    successful = false;
                    failureReasons.add(PersistMessages.ERR_SETTER_INFO_METHOD_NOT_MULTIVALUED.get(this.method.getName(), a.getName(), this.containingClass.getName()));
                }
                try {
                    this.encoder.invokeSetter(this.method, o, a);
                }
                catch (final LDAPPersistException lpe) {
                    Debug.debugException(lpe);
                    if (this.failOnInvalidValue) {
                        successful = false;
                        failureReasons.add(lpe.getMessage());
                    }
                }
                return successful;
            }
        }
        try {
            this.encoder.setNull(this.method, o);
        }
        catch (final LDAPPersistException lpe) {
            Debug.debugException(lpe);
            successful = false;
            failureReasons.add(lpe.getMessage());
        }
        return successful;
    }
}
