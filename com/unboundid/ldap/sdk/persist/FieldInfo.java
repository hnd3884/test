package com.unboundid.ldap.sdk.persist;

import java.util.List;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.lang.reflect.Modifier;
import com.unboundid.util.Validator;
import java.lang.reflect.Field;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FieldInfo implements Serializable
{
    private static final long serialVersionUID = -5715642176677596417L;
    private final boolean failOnInvalidValue;
    private final boolean failOnTooManyValues;
    private final boolean includeInAdd;
    private final boolean includeInModify;
    private final boolean includeInRDN;
    private final boolean isRequiredForDecode;
    private final boolean isRequiredForEncode;
    private final boolean lazilyLoad;
    private final boolean supportsMultipleValues;
    private final Class<?> containingClass;
    private final Field field;
    private final FilterUsage filterUsage;
    private final ObjectEncoder encoder;
    private final String attributeName;
    private final String[] defaultDecodeValues;
    private final String[] defaultEncodeValues;
    private final String[] objectClasses;
    
    FieldInfo(final Field f, final Class<?> c) throws LDAPPersistException {
        Validator.ensureNotNull(f, c);
        (this.field = f).setAccessible(true);
        final LDAPField a = f.getAnnotation(LDAPField.class);
        if (a == null) {
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_FIELD_NOT_ANNOTATED.get(f.getName(), c.getName()));
        }
        final LDAPObject o = c.getAnnotation(LDAPObject.class);
        if (o == null) {
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_CLASS_NOT_ANNOTATED.get(c.getName()));
        }
        this.containingClass = c;
        this.failOnInvalidValue = a.failOnInvalidValue();
        this.includeInRDN = a.inRDN();
        this.includeInAdd = (this.includeInRDN || a.inAdd());
        this.includeInModify = (!this.includeInRDN && a.inModify());
        this.filterUsage = a.filterUsage();
        this.lazilyLoad = a.lazilyLoad();
        this.isRequiredForDecode = (a.requiredForDecode() && !this.lazilyLoad);
        this.isRequiredForEncode = (this.includeInRDN || a.requiredForEncode());
        this.defaultDecodeValues = a.defaultDecodeValue();
        this.defaultEncodeValues = a.defaultEncodeValue();
        if (this.lazilyLoad) {
            if (this.defaultDecodeValues.length > 0) {
                throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_LAZY_WITH_DEFAULT_DECODE.get(f.getName(), c.getName()));
            }
            if (this.defaultEncodeValues.length > 0) {
                throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_LAZY_WITH_DEFAULT_ENCODE.get(f.getName(), c.getName()));
            }
            if (this.includeInRDN) {
                throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_LAZY_IN_RDN.get(f.getName(), c.getName()));
            }
        }
        final int modifiers = f.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_FIELD_FINAL.get(f.getName(), c.getName()));
        }
        if (Modifier.isStatic(modifiers)) {
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_FIELD_STATIC.get(f.getName(), c.getName()));
        }
        try {
            this.encoder = (ObjectEncoder)a.encoderClass().newInstance();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_CANNOT_GET_ENCODER.get(a.encoderClass().getName(), f.getName(), c.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
        if (!this.encoder.supportsType(f.getGenericType())) {
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_ENCODER_UNSUPPORTED_TYPE.get(this.encoder.getClass().getName(), f.getName(), c.getName(), f.getGenericType()));
        }
        this.supportsMultipleValues = this.encoder.supportsMultipleValues(f);
        if (this.supportsMultipleValues) {
            this.failOnTooManyValues = false;
        }
        else {
            this.failOnTooManyValues = a.failOnTooManyValues();
            if (this.defaultDecodeValues.length > 1) {
                throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_UNSUPPORTED_MULTIPLE_DEFAULT_DECODE_VALUES.get(f.getName(), c.getName()));
            }
            if (this.defaultEncodeValues.length > 1) {
                throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_UNSUPPORTED_MULTIPLE_DEFAULT_ENCODE_VALUES.get(f.getName(), c.getName()));
            }
        }
        final String attrName = a.attribute();
        if (attrName == null || attrName.isEmpty()) {
            this.attributeName = f.getName();
        }
        else {
            this.attributeName = attrName;
        }
        final StringBuilder invalidReason = new StringBuilder();
        if (!PersistUtils.isValidLDAPName(this.attributeName, true, invalidReason)) {
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_INVALID_ATTR_NAME.get(f.getName(), c.getName(), invalidReason.toString()));
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
                    throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_INVALID_OC.get(f.getName(), c.getName(), s));
                }
            }
        }
    }
    
    public Field getField() {
        return this.field;
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
    
    public boolean isRequiredForDecode() {
        return this.isRequiredForDecode;
    }
    
    public boolean isRequiredForEncode() {
        return this.isRequiredForEncode;
    }
    
    public boolean lazilyLoad() {
        return this.lazilyLoad;
    }
    
    public ObjectEncoder getEncoder() {
        return this.encoder;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String[] getDefaultDecodeValues() {
        return this.defaultDecodeValues;
    }
    
    public String[] getDefaultEncodeValues() {
        return this.defaultEncodeValues;
    }
    
    public String[] getObjectClasses() {
        return this.objectClasses;
    }
    
    public boolean supportsMultipleValues() {
        return this.supportsMultipleValues;
    }
    
    AttributeTypeDefinition constructAttributeType() throws LDAPPersistException {
        return this.constructAttributeType(DefaultOIDAllocator.getInstance());
    }
    
    AttributeTypeDefinition constructAttributeType(final OIDAllocator a) throws LDAPPersistException {
        return this.encoder.constructAttributeType(this.field, a);
    }
    
    Attribute encode(final Object o, final boolean ignoreRequiredFlag) throws LDAPPersistException {
        try {
            final Object fieldValue = this.field.get(o);
            if (fieldValue != null) {
                return this.encoder.encodeFieldValue(this.field, fieldValue, this.attributeName);
            }
            if (this.defaultEncodeValues.length > 0) {
                return new Attribute(this.attributeName, this.defaultEncodeValues);
            }
            if (this.isRequiredForEncode && !ignoreRequiredFlag) {
                throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_MISSING_REQUIRED_VALUE.get(this.field.getName(), this.containingClass.getName()));
            }
            return null;
        }
        catch (final LDAPPersistException lpe) {
            Debug.debugException(lpe);
            throw lpe;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(PersistMessages.ERR_FIELD_INFO_CANNOT_ENCODE.get(this.field.getName(), this.containingClass.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    boolean decode(final Object o, final Entry e, final List<String> failureReasons) {
        boolean successful = true;
        Attribute a = e.getAttribute(this.attributeName);
        if (a == null || !a.hasValue()) {
            if (this.defaultDecodeValues.length <= 0) {
                if (this.isRequiredForDecode) {
                    successful = false;
                    failureReasons.add(PersistMessages.ERR_FIELD_INFO_MISSING_REQUIRED_ATTRIBUTE.get(this.containingClass.getName(), e.getDN(), this.attributeName, this.field.getName()));
                }
                try {
                    this.encoder.setNull(this.field, o);
                }
                catch (final LDAPPersistException lpe) {
                    Debug.debugException(lpe);
                    successful = false;
                    failureReasons.add(lpe.getMessage());
                }
                return successful;
            }
            a = new Attribute(this.attributeName, this.defaultDecodeValues);
        }
        if (this.failOnTooManyValues && a.size() > 1) {
            successful = false;
            failureReasons.add(PersistMessages.ERR_FIELD_INFO_FIELD_NOT_MULTIVALUED.get(a.getName(), this.field.getName(), this.containingClass.getName()));
        }
        try {
            this.encoder.decodeField(this.field, o, a);
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
