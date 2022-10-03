package com.unboundid.ldap.sdk.persist;

import com.unboundid.asn1.ASN1OctetString;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.ModificationType;
import java.util.HashSet;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.RDN;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.schema.ObjectClassType;
import com.unboundid.ldap.sdk.schema.ObjectClassDefinition;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.Collections;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.TreeMap;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Field;
import com.unboundid.ldap.sdk.DN;
import java.lang.reflect.Constructor;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPObjectHandler<T> implements Serializable
{
    private static final long serialVersionUID = -1480360011153517161L;
    private final Attribute objectClassAttribute;
    private final Class<T> type;
    private final Constructor<T> constructor;
    private final DN defaultParentDN;
    private final Field dnField;
    private final Field entryField;
    private final LDAPObject ldapObject;
    private final LDAPObjectHandler<? super T> superclassHandler;
    private final List<FieldInfo> alwaysAllowedFilterFields;
    private final List<FieldInfo> conditionallyAllowedFilterFields;
    private final List<FieldInfo> requiredFilterFields;
    private final List<FieldInfo> rdnFields;
    private final List<GetterInfo> alwaysAllowedFilterGetters;
    private final List<GetterInfo> conditionallyAllowedFilterGetters;
    private final List<GetterInfo> requiredFilterGetters;
    private final List<GetterInfo> rdnGetters;
    private final Map<String, FieldInfo> fieldMap;
    private final Map<String, GetterInfo> getterMap;
    private final Map<String, SetterInfo> setterMap;
    private final Method postDecodeMethod;
    private final Method postEncodeMethod;
    private final String structuralClass;
    private final String[] attributesToRequest;
    private final String[] auxiliaryClasses;
    private final String[] explicitAttributesToRequest;
    private final String[] lazilyLoadedAttributes;
    private final String[] superiorClasses;
    
    LDAPObjectHandler(final Class<T> type) throws LDAPPersistException {
        this.type = type;
        final Class<? super T> superclassType = type.getSuperclass();
        if (superclassType == null) {
            this.superclassHandler = null;
        }
        else {
            final LDAPObject superclassAnnotation = superclassType.getAnnotation(LDAPObject.class);
            if (superclassAnnotation == null) {
                this.superclassHandler = null;
            }
            else {
                this.superclassHandler = new LDAPObjectHandler<Object>(superclassType);
            }
        }
        final TreeMap<String, FieldInfo> fields = new TreeMap<String, FieldInfo>();
        final TreeMap<String, GetterInfo> getters = new TreeMap<String, GetterInfo>();
        final TreeMap<String, SetterInfo> setters = new TreeMap<String, SetterInfo>();
        this.ldapObject = type.getAnnotation(LDAPObject.class);
        if (this.ldapObject == null) {
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_OBJECT_NOT_ANNOTATED.get(type.getName()));
        }
        final LinkedHashMap<String, String> objectClasses = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        final String oc = this.ldapObject.structuralClass();
        if (oc.isEmpty()) {
            this.structuralClass = StaticUtils.getUnqualifiedClassName(type);
        }
        else {
            this.structuralClass = oc;
        }
        final StringBuilder invalidReason = new StringBuilder();
        if (!PersistUtils.isValidLDAPName(this.structuralClass, invalidReason)) {
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_STRUCTURAL_CLASS.get(type.getName(), this.structuralClass, invalidReason.toString()));
        }
        objectClasses.put(StaticUtils.toLowerCase(this.structuralClass), this.structuralClass);
        this.auxiliaryClasses = this.ldapObject.auxiliaryClass();
        for (final String auxiliaryClass : this.auxiliaryClasses) {
            if (!PersistUtils.isValidLDAPName(auxiliaryClass, invalidReason)) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_AUXILIARY_CLASS.get(type.getName(), auxiliaryClass, invalidReason.toString()));
            }
            objectClasses.put(StaticUtils.toLowerCase(auxiliaryClass), auxiliaryClass);
        }
        this.superiorClasses = this.ldapObject.superiorClass();
        for (final String superiorClass : this.superiorClasses) {
            if (!PersistUtils.isValidLDAPName(superiorClass, invalidReason)) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_SUPERIOR_CLASS.get(type.getName(), superiorClass, invalidReason.toString()));
            }
            objectClasses.put(StaticUtils.toLowerCase(superiorClass), superiorClass);
        }
        if (this.superclassHandler != null) {
            for (final String s : this.superclassHandler.objectClassAttribute.getValues()) {
                objectClasses.put(StaticUtils.toLowerCase(s), s);
            }
        }
        this.objectClassAttribute = new Attribute("objectClass", objectClasses.values());
        final String parentDNStr = this.ldapObject.defaultParentDN();
        try {
            if (parentDNStr.isEmpty() && this.superclassHandler != null) {
                this.defaultParentDN = this.superclassHandler.getDefaultParentDN();
            }
            else {
                this.defaultParentDN = new DN(parentDNStr);
            }
        }
        catch (final LDAPException le) {
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_DEFAULT_PARENT.get(type.getName(), parentDNStr, le.getMessage()), le);
        }
        final String postDecodeMethodName = this.ldapObject.postDecodeMethod();
        Label_0758: {
            if (!postDecodeMethodName.isEmpty()) {
                try {
                    (this.postDecodeMethod = type.getDeclaredMethod(postDecodeMethodName, (Class<?>[])new Class[0])).setAccessible(true);
                    break Label_0758;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_POST_DECODE_METHOD.get(type.getName(), postDecodeMethodName, StaticUtils.getExceptionMessage(e)), e);
                }
            }
            this.postDecodeMethod = null;
        }
        final String postEncodeMethodName = this.ldapObject.postEncodeMethod();
        Label_0860: {
            if (!postEncodeMethodName.isEmpty()) {
                try {
                    (this.postEncodeMethod = type.getDeclaredMethod(postEncodeMethodName, Entry.class)).setAccessible(true);
                    break Label_0860;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_POST_ENCODE_METHOD.get(type.getName(), postEncodeMethodName, StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            this.postEncodeMethod = null;
            try {
                (this.constructor = type.getDeclaredConstructor((Class<?>[])new Class[0])).setAccessible(true);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_NO_DEFAULT_CONSTRUCTOR.get(type.getName()), e2);
            }
        }
        Field tmpDNField = null;
        Field tmpEntryField = null;
        final LinkedList<FieldInfo> tmpRFilterFields = new LinkedList<FieldInfo>();
        final LinkedList<FieldInfo> tmpAAFilterFields = new LinkedList<FieldInfo>();
        final LinkedList<FieldInfo> tmpCAFilterFields = new LinkedList<FieldInfo>();
        final LinkedList<FieldInfo> tmpRDNFields = new LinkedList<FieldInfo>();
        for (final Field f : type.getDeclaredFields()) {
            final LDAPField fieldAnnotation = f.getAnnotation(LDAPField.class);
            final LDAPDNField dnFieldAnnotation = f.getAnnotation(LDAPDNField.class);
            final LDAPEntryField entryFieldAnnotation = f.getAnnotation(LDAPEntryField.class);
            if (fieldAnnotation != null) {
                f.setAccessible(true);
                final FieldInfo fieldInfo = new FieldInfo(f, type);
                final String attrName = StaticUtils.toLowerCase(fieldInfo.getAttributeName());
                if (fields.containsKey(attrName)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ATTR_CONFLICT.get(type.getName(), fieldInfo.getAttributeName()));
                }
                fields.put(attrName, fieldInfo);
                switch (fieldInfo.getFilterUsage()) {
                    case REQUIRED: {
                        tmpRFilterFields.add(fieldInfo);
                        break;
                    }
                    case ALWAYS_ALLOWED: {
                        tmpAAFilterFields.add(fieldInfo);
                        break;
                    }
                    case CONDITIONALLY_ALLOWED: {
                        tmpCAFilterFields.add(fieldInfo);
                        break;
                    }
                }
                if (fieldInfo.includeInRDN()) {
                    tmpRDNFields.add(fieldInfo);
                }
            }
            if (dnFieldAnnotation != null) {
                f.setAccessible(true);
                if (fieldAnnotation != null) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_CONFLICTING_FIELD_ANNOTATIONS.get(type.getName(), "LDAPField", "LDAPDNField", f.getName()));
                }
                if (tmpDNField != null) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_MULTIPLE_DN_FIELDS.get(type.getName()));
                }
                final int modifiers = f.getModifiers();
                if (Modifier.isFinal(modifiers)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_DN_FIELD_FINAL.get(f.getName(), type.getName()));
                }
                if (Modifier.isStatic(modifiers)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_DN_FIELD_STATIC.get(f.getName(), type.getName()));
                }
                final Class<?> fieldType = f.getType();
                if (!fieldType.equals(String.class)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_DN_FIELD_TYPE.get(type.getName(), f.getName(), fieldType.getName()));
                }
                tmpDNField = f;
            }
            if (entryFieldAnnotation != null) {
                f.setAccessible(true);
                if (fieldAnnotation != null) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_CONFLICTING_FIELD_ANNOTATIONS.get(type.getName(), "LDAPField", "LDAPEntryField", f.getName()));
                }
                if (tmpEntryField != null) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_MULTIPLE_ENTRY_FIELDS.get(type.getName()));
                }
                final int modifiers = f.getModifiers();
                if (Modifier.isFinal(modifiers)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ENTRY_FIELD_FINAL.get(f.getName(), type.getName()));
                }
                if (Modifier.isStatic(modifiers)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ENTRY_FIELD_STATIC.get(f.getName(), type.getName()));
                }
                final Class<?> fieldType = f.getType();
                if (!fieldType.equals(ReadOnlyEntry.class)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_ENTRY_FIELD_TYPE.get(type.getName(), f.getName(), fieldType.getName()));
                }
                tmpEntryField = f;
            }
        }
        this.dnField = tmpDNField;
        this.entryField = tmpEntryField;
        this.requiredFilterFields = Collections.unmodifiableList((List<? extends FieldInfo>)tmpRFilterFields);
        this.alwaysAllowedFilterFields = Collections.unmodifiableList((List<? extends FieldInfo>)tmpAAFilterFields);
        this.conditionallyAllowedFilterFields = Collections.unmodifiableList((List<? extends FieldInfo>)tmpCAFilterFields);
        this.rdnFields = Collections.unmodifiableList((List<? extends FieldInfo>)tmpRDNFields);
        final LinkedList<GetterInfo> tmpRFilterGetters = new LinkedList<GetterInfo>();
        final LinkedList<GetterInfo> tmpAAFilterGetters = new LinkedList<GetterInfo>();
        final LinkedList<GetterInfo> tmpCAFilterGetters = new LinkedList<GetterInfo>();
        final LinkedList<GetterInfo> tmpRDNGetters = new LinkedList<GetterInfo>();
        for (final Method m : type.getDeclaredMethods()) {
            final LDAPGetter getter = m.getAnnotation(LDAPGetter.class);
            final LDAPSetter setter = m.getAnnotation(LDAPSetter.class);
            if (getter != null) {
                m.setAccessible(true);
                if (setter != null) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_CONFLICTING_METHOD_ANNOTATIONS.get(type.getName(), "LDAPGetter", "LDAPSetter", m.getName()));
                }
                final GetterInfo methodInfo = new GetterInfo(m, type);
                final String attrName2 = StaticUtils.toLowerCase(methodInfo.getAttributeName());
                if (fields.containsKey(attrName2) || getters.containsKey(attrName2)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ATTR_CONFLICT.get(type.getName(), methodInfo.getAttributeName()));
                }
                getters.put(attrName2, methodInfo);
                switch (methodInfo.getFilterUsage()) {
                    case REQUIRED: {
                        tmpRFilterGetters.add(methodInfo);
                        break;
                    }
                    case ALWAYS_ALLOWED: {
                        tmpAAFilterGetters.add(methodInfo);
                        break;
                    }
                    case CONDITIONALLY_ALLOWED: {
                        tmpCAFilterGetters.add(methodInfo);
                        break;
                    }
                }
                if (methodInfo.includeInRDN()) {
                    tmpRDNGetters.add(methodInfo);
                }
            }
            if (setter != null) {
                m.setAccessible(true);
                final SetterInfo methodInfo2 = new SetterInfo(m, type);
                final String attrName2 = StaticUtils.toLowerCase(methodInfo2.getAttributeName());
                if (fields.containsKey(attrName2) || setters.containsKey(attrName2)) {
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ATTR_CONFLICT.get(type.getName(), methodInfo2.getAttributeName()));
                }
                setters.put(attrName2, methodInfo2);
            }
        }
        this.requiredFilterGetters = Collections.unmodifiableList((List<? extends GetterInfo>)tmpRFilterGetters);
        this.alwaysAllowedFilterGetters = Collections.unmodifiableList((List<? extends GetterInfo>)tmpAAFilterGetters);
        this.conditionallyAllowedFilterGetters = Collections.unmodifiableList((List<? extends GetterInfo>)tmpCAFilterGetters);
        this.rdnGetters = Collections.unmodifiableList((List<? extends GetterInfo>)tmpRDNGetters);
        if (this.rdnFields.isEmpty() && this.rdnGetters.isEmpty() && this.superclassHandler == null) {
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_NO_RDN_DEFINED.get(type.getName()));
        }
        this.fieldMap = Collections.unmodifiableMap((Map<? extends String, ? extends FieldInfo>)fields);
        this.getterMap = Collections.unmodifiableMap((Map<? extends String, ? extends GetterInfo>)getters);
        this.setterMap = Collections.unmodifiableMap((Map<? extends String, ? extends SetterInfo>)setters);
        final TreeSet<String> attrSet = new TreeSet<String>();
        final TreeSet<String> lazySet = new TreeSet<String>();
        for (final FieldInfo i : fields.values()) {
            if (i.lazilyLoad()) {
                lazySet.add(i.getAttributeName());
            }
            else {
                attrSet.add(i.getAttributeName());
            }
        }
        for (final SetterInfo j : setters.values()) {
            attrSet.add(j.getAttributeName());
        }
        if (this.superclassHandler != null) {
            attrSet.addAll(Arrays.asList(this.superclassHandler.explicitAttributesToRequest));
            lazySet.addAll(Arrays.asList(this.superclassHandler.lazilyLoadedAttributes));
        }
        attrSet.toArray(this.explicitAttributesToRequest = new String[attrSet.size()]);
        if (this.requestAllAttributes()) {
            this.attributesToRequest = new String[] { "*", "+" };
        }
        else {
            this.attributesToRequest = this.explicitAttributesToRequest;
        }
        lazySet.toArray(this.lazilyLoadedAttributes = new String[lazySet.size()]);
    }
    
    public Class<T> getType() {
        return this.type;
    }
    
    public LDAPObjectHandler<?> getSuperclassHandler() {
        return this.superclassHandler;
    }
    
    public LDAPObject getLDAPObjectAnnotation() {
        return this.ldapObject;
    }
    
    public Constructor<T> getConstructor() {
        return this.constructor;
    }
    
    public Field getDNField() {
        return this.dnField;
    }
    
    public Field getEntryField() {
        return this.entryField;
    }
    
    public DN getDefaultParentDN() {
        return this.defaultParentDN;
    }
    
    public String getStructuralClass() {
        return this.structuralClass;
    }
    
    public String[] getAuxiliaryClasses() {
        return this.auxiliaryClasses;
    }
    
    public String[] getSuperiorClasses() {
        return this.superiorClasses;
    }
    
    public boolean requestAllAttributes() {
        return this.ldapObject.requestAllAttributes() || (this.superclassHandler != null && this.superclassHandler.requestAllAttributes());
    }
    
    public String[] getAttributesToRequest() {
        return this.attributesToRequest;
    }
    
    public String[] getLazilyLoadedAttributes() {
        return this.lazilyLoadedAttributes;
    }
    
    public String getEntryDN(final T o) throws LDAPPersistException {
        final String dnFieldValue = this.getDNFieldValue(o);
        if (dnFieldValue != null) {
            return dnFieldValue;
        }
        final ReadOnlyEntry entry = this.getEntry(o);
        if (entry != null) {
            return entry.getDN();
        }
        return null;
    }
    
    private String getDNFieldValue(final T o) throws LDAPPersistException {
        if (this.dnField != null) {
            try {
                final Object dnObject = this.dnField.get(o);
                if (dnObject == null) {
                    return null;
                }
                return String.valueOf(dnObject);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_ACCESSING_DN_FIELD.get(this.dnField.getName(), this.type.getName(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (this.superclassHandler != null) {
            return this.superclassHandler.getDNFieldValue(o);
        }
        return null;
    }
    
    public ReadOnlyEntry getEntry(final T o) throws LDAPPersistException {
        if (this.entryField != null) {
            try {
                final Object entryObject = this.entryField.get(o);
                if (entryObject == null) {
                    return null;
                }
                return (ReadOnlyEntry)entryObject;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_ACCESSING_ENTRY_FIELD.get(this.entryField.getName(), this.type.getName(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (this.superclassHandler != null) {
            return this.superclassHandler.getEntry(o);
        }
        return null;
    }
    
    public Map<String, FieldInfo> getFields() {
        return this.fieldMap;
    }
    
    public Map<String, GetterInfo> getGetters() {
        return this.getterMap;
    }
    
    public Map<String, SetterInfo> getSetters() {
        return this.setterMap;
    }
    
    List<ObjectClassDefinition> constructObjectClasses(final OIDAllocator a) throws LDAPPersistException {
        final LinkedHashMap<String, ObjectClassDefinition> ocMap = new LinkedHashMap<String, ObjectClassDefinition>(StaticUtils.computeMapCapacity(1 + this.auxiliaryClasses.length));
        if (this.superclassHandler != null) {
            for (final ObjectClassDefinition d : this.superclassHandler.constructObjectClasses(a)) {
                ocMap.put(StaticUtils.toLowerCase(d.getNameOrOID()), d);
            }
        }
        final String lowerStructuralClass = StaticUtils.toLowerCase(this.structuralClass);
        if (!ocMap.containsKey(lowerStructuralClass)) {
            if (this.superclassHandler == null) {
                ocMap.put(lowerStructuralClass, this.constructObjectClass(this.structuralClass, "top", ObjectClassType.STRUCTURAL, a));
            }
            else {
                ocMap.put(lowerStructuralClass, this.constructObjectClass(this.structuralClass, this.superclassHandler.getStructuralClass(), ObjectClassType.STRUCTURAL, a));
            }
        }
        for (final String s : this.auxiliaryClasses) {
            final String lowerName = StaticUtils.toLowerCase(s);
            if (!ocMap.containsKey(lowerName)) {
                ocMap.put(lowerName, this.constructObjectClass(s, "top", ObjectClassType.AUXILIARY, a));
            }
        }
        return Collections.unmodifiableList((List<? extends ObjectClassDefinition>)new ArrayList<ObjectClassDefinition>(ocMap.values()));
    }
    
    private ObjectClassDefinition constructObjectClass(final String name, final String sup, final ObjectClassType type, final OIDAllocator a) {
        final TreeMap<String, String> requiredAttrs = new TreeMap<String, String>();
        final TreeMap<String, String> optionalAttrs = new TreeMap<String, String>();
        for (final FieldInfo i : this.fieldMap.values()) {
            boolean found = false;
            for (final String s : i.getObjectClasses()) {
                if (name.equalsIgnoreCase(s)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                continue;
            }
            final String attrName = i.getAttributeName();
            final String lowerName = StaticUtils.toLowerCase(attrName);
            if (i.includeInRDN() || (i.isRequiredForDecode() && i.isRequiredForEncode())) {
                requiredAttrs.put(lowerName, attrName);
            }
            else {
                optionalAttrs.put(lowerName, attrName);
            }
        }
        for (final GetterInfo j : this.getterMap.values()) {
            boolean found = false;
            for (final String s : j.getObjectClasses()) {
                if (name.equalsIgnoreCase(s)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                continue;
            }
            final String attrName = j.getAttributeName();
            final String lowerName = StaticUtils.toLowerCase(attrName);
            if (j.includeInRDN()) {
                requiredAttrs.put(lowerName, attrName);
            }
            else {
                optionalAttrs.put(lowerName, attrName);
            }
        }
        if (name.equalsIgnoreCase(this.structuralClass)) {
            for (final SetterInfo k : this.setterMap.values()) {
                final String attrName2 = k.getAttributeName();
                final String lowerName2 = StaticUtils.toLowerCase(attrName2);
                if (!requiredAttrs.containsKey(lowerName2)) {
                    if (optionalAttrs.containsKey(lowerName2)) {
                        continue;
                    }
                    optionalAttrs.put(lowerName2, attrName2);
                }
            }
        }
        final String[] reqArray = new String[requiredAttrs.size()];
        requiredAttrs.values().toArray(reqArray);
        final String[] optArray = new String[optionalAttrs.size()];
        optionalAttrs.values().toArray(optArray);
        return new ObjectClassDefinition(a.allocateObjectClassOID(name), new String[] { name }, null, false, new String[] { sup }, type, reqArray, optArray, null);
    }
    
    T decode(final Entry e) throws LDAPPersistException {
        T o;
        try {
            o = this.constructor.newInstance(new Object[0]);
        }
        catch (final Exception ex) {
            Debug.debugException(ex);
            if (ex instanceof InvocationTargetException) {
                final Throwable targetException = ((InvocationTargetException)ex).getTargetException();
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_INVOKING_CONSTRUCTOR.get(this.type.getName(), StaticUtils.getExceptionMessage(targetException)), targetException);
            }
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_INVOKING_CONSTRUCTOR.get(this.type.getName(), StaticUtils.getExceptionMessage(ex)), ex);
        }
        this.decode(o, e);
        return o;
    }
    
    void decode(final T o, final Entry e) throws LDAPPersistException {
        if (this.superclassHandler != null) {
            this.superclassHandler.decode(o, e);
        }
        this.setDNAndEntryFields(o, e);
        final ArrayList<String> failureReasons = new ArrayList<String>(5);
        boolean successful = true;
        for (final FieldInfo i : this.fieldMap.values()) {
            successful &= i.decode(o, e, failureReasons);
        }
        for (final SetterInfo j : this.setterMap.values()) {
            successful &= j.invokeSetter(o, e, failureReasons);
        }
        Throwable cause = null;
        if (this.postDecodeMethod != null) {
            try {
                this.postDecodeMethod.invoke(o, new Object[0]);
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                StaticUtils.rethrowIfError(ex);
                if (ex instanceof InvocationTargetException) {
                    cause = ((InvocationTargetException)ex).getTargetException();
                }
                else {
                    cause = ex;
                }
                successful = false;
                failureReasons.add(PersistMessages.ERR_OBJECT_HANDLER_ERROR_INVOKING_POST_DECODE_METHOD.get(this.postDecodeMethod.getName(), this.type.getName(), StaticUtils.getExceptionMessage(ex)));
            }
        }
        if (!successful) {
            throw new LDAPPersistException(StaticUtils.concatenateStrings(failureReasons), o, cause);
        }
    }
    
    Entry encode(final T o, final String parentDN) throws LDAPPersistException {
        final LinkedHashMap<String, Attribute> attrMap = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(20));
        attrMap.put("objectClass", this.objectClassAttribute);
        for (final Map.Entry<String, FieldInfo> e : this.fieldMap.entrySet()) {
            final FieldInfo i = e.getValue();
            if (!i.includeInAdd()) {
                continue;
            }
            final Attribute a = i.encode(o, false);
            if (a == null) {
                continue;
            }
            attrMap.put(e.getKey(), a);
        }
        for (final Map.Entry<String, GetterInfo> e2 : this.getterMap.entrySet()) {
            final GetterInfo j = e2.getValue();
            if (!j.includeInAdd()) {
                continue;
            }
            final Attribute a = j.encode(o);
            if (a == null) {
                continue;
            }
            attrMap.put(e2.getKey(), a);
        }
        final String dn = this.constructDN(o, parentDN, attrMap);
        final Entry entry = new Entry(dn, attrMap.values());
        if (this.postEncodeMethod != null) {
            try {
                this.postEncodeMethod.invoke(o, entry);
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                if (ex instanceof InvocationTargetException) {
                    final Throwable targetException = ((InvocationTargetException)ex).getTargetException();
                    throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_INVOKING_POST_ENCODE_METHOD.get(this.postEncodeMethod.getName(), this.type.getName(), StaticUtils.getExceptionMessage(targetException)), targetException);
                }
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_INVOKING_POST_ENCODE_METHOD.get(this.postEncodeMethod.getName(), this.type.getName(), StaticUtils.getExceptionMessage(ex)), ex);
            }
        }
        this.setDNAndEntryFields(o, entry);
        if (this.superclassHandler != null) {
            final Entry e3 = this.superclassHandler.encode(o, parentDN);
            for (final Attribute a2 : e3.getAttributes()) {
                entry.addAttribute(a2);
            }
        }
        return entry;
    }
    
    private void setDNAndEntryFields(final T o, final Entry e) throws LDAPPersistException {
        if (this.dnField != null) {
            try {
                if (this.dnField.get(o) == null) {
                    this.dnField.set(o, e.getDN());
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_SETTING_DN.get(this.type.getName(), e.getDN(), this.dnField.getName(), StaticUtils.getExceptionMessage(ex)), ex);
            }
        }
        if (this.entryField != null) {
            try {
                if (this.entryField.get(o) == null) {
                    this.entryField.set(o, new ReadOnlyEntry(e));
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_ERROR_SETTING_ENTRY.get(this.type.getName(), this.entryField.getName(), StaticUtils.getExceptionMessage(ex)), ex);
            }
        }
        if (this.superclassHandler != null) {
            this.superclassHandler.setDNAndEntryFields(o, e);
        }
    }
    
    public String constructDN(final T o, final String parentDN) throws LDAPPersistException {
        final String existingDN = this.getEntryDN(o);
        if (existingDN != null) {
            return existingDN;
        }
        final int numRDNs = this.rdnFields.size() + this.rdnGetters.size();
        if (numRDNs == 0) {
            return this.superclassHandler.constructDN(o, parentDN);
        }
        final LinkedHashMap<String, Attribute> attrMap = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(numRDNs));
        for (final FieldInfo i : this.rdnFields) {
            final Attribute a = i.encode(o, true);
            if (a == null) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_RDN_FIELD_MISSING_VALUE.get(this.type.getName(), i.getField().getName()));
            }
            attrMap.put(StaticUtils.toLowerCase(i.getAttributeName()), a);
        }
        for (final GetterInfo j : this.rdnGetters) {
            final Attribute a = j.encode(o);
            if (a == null) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_RDN_GETTER_MISSING_VALUE.get(this.type.getName(), j.getMethod().getName()));
            }
            attrMap.put(StaticUtils.toLowerCase(j.getAttributeName()), a);
        }
        return this.constructDN(o, parentDN, attrMap);
    }
    
    String constructDN(final T o, final String parentDN, final Map<String, Attribute> attrMap) throws LDAPPersistException {
        final String existingDN = this.getEntryDN(o);
        if (existingDN != null) {
            return existingDN;
        }
        final int numRDNs = this.rdnFields.size() + this.rdnGetters.size();
        if (numRDNs == 0) {
            return this.superclassHandler.constructDN(o, parentDN);
        }
        final ArrayList<String> rdnNameList = new ArrayList<String>(numRDNs);
        final ArrayList<byte[]> rdnValueList = new ArrayList<byte[]>(numRDNs);
        for (final FieldInfo i : this.rdnFields) {
            final Attribute a = attrMap.get(StaticUtils.toLowerCase(i.getAttributeName()));
            if (a == null) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_RDN_FIELD_MISSING_VALUE.get(this.type.getName(), i.getField().getName()));
            }
            rdnNameList.add(a.getName());
            rdnValueList.add(a.getValueByteArray());
        }
        for (final GetterInfo j : this.rdnGetters) {
            final Attribute a = attrMap.get(StaticUtils.toLowerCase(j.getAttributeName()));
            if (a == null) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_RDN_GETTER_MISSING_VALUE.get(this.type.getName(), j.getMethod().getName()));
            }
            rdnNameList.add(a.getName());
            rdnValueList.add(a.getValueByteArray());
        }
        final String[] rdnNames = new String[rdnNameList.size()];
        rdnNameList.toArray(rdnNames);
        final byte[][] rdnValues = new byte[rdnNames.length][];
        rdnValueList.toArray(rdnValues);
        final RDN rdn = new RDN(rdnNames, rdnValues);
        if (parentDN == null) {
            return new DN(rdn, this.defaultParentDN).toString();
        }
        try {
            final DN parsedParentDN = new DN(parentDN);
            return new DN(rdn, parsedParentDN).toString();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_INVALID_PARENT_DN.get(this.type.getName(), parentDN, le.getMessage()), le);
        }
    }
    
    List<Modification> getModifications(final T o, final boolean deleteNullValues, final boolean byteForByte, final String... attributes) throws LDAPPersistException {
        ReadOnlyEntry originalEntry;
        if (this.entryField != null) {
            originalEntry = this.getEntry(o);
        }
        else {
            originalEntry = null;
        }
        if (originalEntry != null) {
            try {
                final T decodedOrig = this.decode(originalEntry);
                final Entry reEncodedOriginal = this.encode(decodedOrig, originalEntry.getParentDNString());
                final Entry newEntry = this.encode(o, originalEntry.getParentDNString());
                final List<Modification> mods = Entry.diff(reEncodedOriginal, newEntry, true, false, byteForByte, attributes);
                if (!deleteNullValues) {
                    final Iterator<Modification> iterator = mods.iterator();
                    while (iterator.hasNext()) {
                        final Modification m = iterator.next();
                        if (m.getRawValues().length == 0) {
                            iterator.remove();
                        }
                    }
                }
                HashSet<String> stripAttrs = null;
                for (final FieldInfo i : this.fieldMap.values()) {
                    if (!i.includeInModify()) {
                        if (stripAttrs == null) {
                            stripAttrs = new HashSet<String>(StaticUtils.computeMapCapacity(10));
                        }
                        stripAttrs.add(StaticUtils.toLowerCase(i.getAttributeName()));
                    }
                }
                for (final GetterInfo j : this.getterMap.values()) {
                    if (!j.includeInModify()) {
                        if (stripAttrs == null) {
                            stripAttrs = new HashSet<String>(StaticUtils.computeMapCapacity(10));
                        }
                        stripAttrs.add(StaticUtils.toLowerCase(j.getAttributeName()));
                    }
                }
                if (stripAttrs != null) {
                    final Iterator<Modification> iterator2 = mods.iterator();
                    while (iterator2.hasNext()) {
                        final Modification k = iterator2.next();
                        if (stripAttrs.contains(StaticUtils.toLowerCase(k.getAttributeName()))) {
                            iterator2.remove();
                        }
                    }
                }
                return mods;
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            finally {
                this.setDNAndEntryFields(o, originalEntry);
            }
        }
        HashSet<String> attrSet;
        if (attributes == null || attributes.length == 0) {
            attrSet = null;
        }
        else {
            attrSet = new HashSet<String>(StaticUtils.computeMapCapacity(attributes.length));
            for (final String s : attributes) {
                attrSet.add(StaticUtils.toLowerCase(s));
            }
        }
        final ArrayList<Modification> mods2 = new ArrayList<Modification>(5);
        for (final Map.Entry<String, FieldInfo> e2 : this.fieldMap.entrySet()) {
            final String attrName = StaticUtils.toLowerCase(e2.getKey());
            if (attrSet != null && !attrSet.contains(attrName)) {
                continue;
            }
            final FieldInfo l = e2.getValue();
            if (!l.includeInModify()) {
                continue;
            }
            final Attribute a = l.encode(o, false);
            if (a == null) {
                if (!deleteNullValues) {
                    continue;
                }
                if (originalEntry != null && !originalEntry.hasAttribute(attrName)) {
                    continue;
                }
                mods2.add(new Modification(ModificationType.REPLACE, l.getAttributeName()));
            }
            else {
                if (originalEntry != null) {
                    final Attribute originalAttr = originalEntry.getAttribute(attrName);
                    if (originalAttr != null && originalAttr.equals(a)) {
                        continue;
                    }
                }
                mods2.add(new Modification(ModificationType.REPLACE, l.getAttributeName(), a.getRawValues()));
            }
        }
        for (final Map.Entry<String, GetterInfo> e3 : this.getterMap.entrySet()) {
            final String attrName = StaticUtils.toLowerCase(e3.getKey());
            if (attrSet != null && !attrSet.contains(attrName)) {
                continue;
            }
            final GetterInfo i2 = e3.getValue();
            if (!i2.includeInModify()) {
                continue;
            }
            final Attribute a = i2.encode(o);
            if (a == null) {
                if (!deleteNullValues) {
                    continue;
                }
                if (originalEntry != null && !originalEntry.hasAttribute(attrName)) {
                    continue;
                }
                mods2.add(new Modification(ModificationType.REPLACE, i2.getAttributeName()));
            }
            else {
                if (originalEntry != null) {
                    final Attribute originalAttr = originalEntry.getAttribute(attrName);
                    if (originalAttr != null && originalAttr.equals(a)) {
                        continue;
                    }
                }
                mods2.add(new Modification(ModificationType.REPLACE, i2.getAttributeName(), a.getRawValues()));
            }
        }
        if (this.superclassHandler != null) {
            final List<Modification> superMods = this.superclassHandler.getModifications(o, deleteNullValues, byteForByte, attributes);
            final ArrayList<Modification> modsToAdd = new ArrayList<Modification>(superMods.size());
            for (final Modification sm : superMods) {
                boolean add = true;
                for (final Modification m2 : mods2) {
                    if (m2.getAttributeName().equalsIgnoreCase(sm.getAttributeName())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    modsToAdd.add(sm);
                }
            }
            mods2.addAll(modsToAdd);
        }
        return Collections.unmodifiableList((List<? extends Modification>)mods2);
    }
    
    public Filter createBaseFilter() {
        if (this.auxiliaryClasses.length == 0) {
            return Filter.createEqualityFilter("objectClass", this.structuralClass);
        }
        final ArrayList<Filter> comps = new ArrayList<Filter>(1 + this.auxiliaryClasses.length);
        comps.add(Filter.createEqualityFilter("objectClass", this.structuralClass));
        for (final String s : this.auxiliaryClasses) {
            comps.add(Filter.createEqualityFilter("objectClass", s));
        }
        return Filter.createANDFilter(comps);
    }
    
    public Filter createFilter(final T o) throws LDAPPersistException {
        final AtomicBoolean addedRequiredOrAllowed = new AtomicBoolean(false);
        final Filter f = this.createFilter(o, addedRequiredOrAllowed);
        if (!addedRequiredOrAllowed.get()) {
            throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_FILTER_MISSING_REQUIRED_OR_ALLOWED.get());
        }
        return f;
    }
    
    private Filter createFilter(final T o, final AtomicBoolean addedRequiredOrAllowed) throws LDAPPersistException {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(5);
        attrs.add(this.objectClassAttribute);
        for (final FieldInfo i : this.requiredFilterFields) {
            final Attribute a = i.encode(o, true);
            if (a == null) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_FILTER_MISSING_REQUIRED_FIELD.get(i.getField().getName()));
            }
            attrs.add(a);
            addedRequiredOrAllowed.set(true);
        }
        for (final GetterInfo j : this.requiredFilterGetters) {
            final Attribute a = j.encode(o);
            if (a == null) {
                throw new LDAPPersistException(PersistMessages.ERR_OBJECT_HANDLER_FILTER_MISSING_REQUIRED_GETTER.get(j.getMethod().getName()));
            }
            attrs.add(a);
            addedRequiredOrAllowed.set(true);
        }
        for (final FieldInfo i : this.alwaysAllowedFilterFields) {
            final Attribute a = i.encode(o, true);
            if (a != null) {
                attrs.add(a);
                addedRequiredOrAllowed.set(true);
            }
        }
        for (final GetterInfo j : this.alwaysAllowedFilterGetters) {
            final Attribute a = j.encode(o);
            if (a != null) {
                attrs.add(a);
                addedRequiredOrAllowed.set(true);
            }
        }
        for (final FieldInfo i : this.conditionallyAllowedFilterFields) {
            final Attribute a = i.encode(o, true);
            if (a != null) {
                attrs.add(a);
            }
        }
        for (final GetterInfo j : this.conditionallyAllowedFilterGetters) {
            final Attribute a = j.encode(o);
            if (a != null) {
                attrs.add(a);
            }
        }
        final ArrayList<Filter> comps = new ArrayList<Filter>(attrs.size());
        final Iterator i$2 = attrs.iterator();
        while (i$2.hasNext()) {
            final Attribute a = i$2.next();
            for (final ASN1OctetString v : a.getRawValues()) {
                comps.add(Filter.createEqualityFilter(a.getName(), v.getValue()));
            }
        }
        if (this.superclassHandler != null) {
            final Filter f = this.superclassHandler.createFilter(o, addedRequiredOrAllowed);
            if (f.getFilterType() == -96) {
                comps.addAll(Arrays.asList(f.getComponents()));
            }
            else {
                comps.add(f);
            }
        }
        return Filter.createANDFilter(comps);
    }
}
