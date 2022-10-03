package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.lang.reflect.Type;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.lang.reflect.Modifier;
import com.unboundid.util.Validator;
import java.lang.reflect.Method;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetterInfo implements Serializable
{
    private static final long serialVersionUID = 1578187843924054389L;
    private final boolean includeInAdd;
    private final boolean includeInModify;
    private final boolean includeInRDN;
    private final Class<?> containingClass;
    private final FilterUsage filterUsage;
    private final Method method;
    private final ObjectEncoder encoder;
    private final String attributeName;
    private final String[] objectClasses;
    
    GetterInfo(final Method m, final Class<?> c) throws LDAPPersistException {
        Validator.ensureNotNull(m, c);
        (this.method = m).setAccessible(true);
        final LDAPGetter a = m.getAnnotation(LDAPGetter.class);
        if (a == null) {
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_METHOD_NOT_ANNOTATED.get(m.getName(), c.getName()));
        }
        final LDAPObject o = c.getAnnotation(LDAPObject.class);
        if (o == null) {
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_CLASS_NOT_ANNOTATED.get(c.getName()));
        }
        this.containingClass = c;
        this.includeInRDN = a.inRDN();
        this.includeInAdd = (this.includeInRDN || a.inAdd());
        this.includeInModify = (!this.includeInRDN && a.inModify());
        this.filterUsage = a.filterUsage();
        final int modifiers = m.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_METHOD_STATIC.get(m.getName(), c.getName()));
        }
        final Type[] params = m.getGenericParameterTypes();
        if (params.length > 0) {
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_METHOD_TAKES_ARGUMENTS.get(m.getName(), c.getName()));
        }
        try {
            this.encoder = (ObjectEncoder)a.encoderClass().newInstance();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_CANNOT_GET_ENCODER.get(a.encoderClass().getName(), m.getName(), c.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
        if (!this.encoder.supportsType(m.getGenericReturnType())) {
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_ENCODER_UNSUPPORTED_TYPE.get(this.encoder.getClass().getName(), m.getName(), c.getName(), String.valueOf(m.getGenericReturnType())));
        }
        String structuralClass;
        if (o.structuralClass().isEmpty()) {
            structuralClass = StaticUtils.getUnqualifiedClassName(c);
        }
        else {
            structuralClass = o.structuralClass();
        }
        final String[] ocs = a.objectClass();
        if (ocs == null || ocs.length == 0) {
            this.objectClasses = new String[] { structuralClass };
        }
        else {
            this.objectClasses = ocs;
        }
        for (final String s : this.objectClasses) {
            if (!s.equalsIgnoreCase(structuralClass)) {
                boolean found = false;
                for (final String oc : o.auxiliaryClass()) {
                    if (s.equalsIgnoreCase(oc)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_INVALID_OC.get(m.getName(), c.getName(), s));
                }
            }
        }
        final String attrName = a.attribute();
        if (attrName == null || attrName.isEmpty()) {
            final String methodName = m.getName();
            if (!methodName.startsWith("get") || methodName.length() < 4) {
                throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_CANNOT_INFER_ATTR.get(methodName, c.getName()));
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
    
    public boolean includeInAdd() {
        return this.includeInAdd;
    }
    
    public boolean includeInModify() {
        return this.includeInModify;
    }
    
    public boolean includeInRDN() {
        return this.includeInRDN;
    }
    
    public FilterUsage getFilterUsage() {
        return this.filterUsage;
    }
    
    public ObjectEncoder getEncoder() {
        return this.encoder;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String[] getObjectClasses() {
        return this.objectClasses;
    }
    
    AttributeTypeDefinition constructAttributeType() throws LDAPPersistException {
        return this.constructAttributeType(DefaultOIDAllocator.getInstance());
    }
    
    AttributeTypeDefinition constructAttributeType(final OIDAllocator a) throws LDAPPersistException {
        return this.encoder.constructAttributeType(this.method, a);
    }
    
    Attribute encode(final Object o) throws LDAPPersistException {
        try {
            final Object methodValue = this.method.invoke(o, new Object[0]);
            if (methodValue == null) {
                return null;
            }
            return this.encoder.encodeMethodValue(this.method, methodValue, this.attributeName);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_GETTER_INFO_CANNOT_ENCODE.get(this.method.getName(), this.containingClass.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
}
