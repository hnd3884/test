package com.unboundid.ldap.sdk.persist;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import com.unboundid.ldap.sdk.LDAPException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.Array;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import com.unboundid.ldap.matchingrules.GeneralizedTimeMatchingRule;
import com.unboundid.ldap.matchingrules.BooleanMatchingRule;
import com.unboundid.ldap.matchingrules.OctetStringMatchingRule;
import com.unboundid.ldap.sdk.Attribute;
import java.lang.reflect.Method;
import java.util.Map;
import com.unboundid.ldap.sdk.schema.AttributeUsage;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.lang.reflect.Field;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.io.Serializable;
import java.util.UUID;
import java.net.URL;
import java.net.URI;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DN;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Type;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DefaultObjectEncoder extends ObjectEncoder
{
    private static final long serialVersionUID = -4566874784628920022L;
    
    @Override
    public boolean supportsType(final Type t) {
        final TypeInfo typeInfo = new TypeInfo(t);
        if (!typeInfo.isSupported()) {
            return false;
        }
        final Class<?> baseClass = typeInfo.getBaseClass();
        if (supportsTypeInternal(baseClass)) {
            return true;
        }
        final Class<?> componentType = typeInfo.getComponentType();
        if (componentType == null) {
            return false;
        }
        if (typeInfo.isArray()) {
            return supportsTypeInternal(componentType);
        }
        if (typeInfo.isList()) {
            return isSupportedListType(baseClass) && supportsTypeInternal(componentType);
        }
        return typeInfo.isSet() && isSupportedSetType(baseClass) && supportsTypeInternal(componentType);
    }
    
    private static boolean supportsTypeInternal(final Class<?> c) {
        if (c.equals(AtomicInteger.class) || c.equals(AtomicLong.class) || c.equals(BigDecimal.class) || c.equals(BigInteger.class) || c.equals(Boolean.class) || c.equals(Boolean.TYPE) || c.equals(Date.class) || c.equals(DN.class) || c.equals(Double.class) || c.equals(Double.TYPE) || c.equals(Filter.class) || c.equals(Float.class) || c.equals(Float.TYPE) || c.equals(Integer.class) || c.equals(Integer.TYPE) || c.equals(LDAPURL.class) || c.equals(Long.class) || c.equals(Long.TYPE) || c.equals(RDN.class) || c.equals(Short.class) || c.equals(Short.TYPE) || c.equals(String.class) || c.equals(StringBuffer.class) || c.equals(StringBuilder.class) || c.equals(URI.class) || c.equals(URL.class) || c.equals(UUID.class)) {
            return true;
        }
        if (c.isArray()) {
            final Class<?> t = c.getComponentType();
            if (t.equals(Byte.TYPE) || t.equals(Character.TYPE)) {
                return true;
            }
        }
        return c.isEnum() || (Serializable.class.isAssignableFrom(c) && !c.isArray() && !Collection.class.isAssignableFrom(c));
    }
    
    private static boolean isSupportedListType(final Class<?> t) {
        return t.equals(List.class) || t.equals(ArrayList.class) || t.equals(LinkedList.class) || t.equals(CopyOnWriteArrayList.class);
    }
    
    private static List<?> createList(final Class<?> t, final int size) {
        if (t.equals(List.class) || t.equals(ArrayList.class)) {
            return new ArrayList<Object>(size);
        }
        if (t.equals(LinkedList.class)) {
            return new LinkedList<Object>();
        }
        if (t.equals(CopyOnWriteArrayList.class)) {
            return new CopyOnWriteArrayList<Object>();
        }
        return null;
    }
    
    private static boolean isSupportedSetType(final Class<?> t) {
        return t.equals(Set.class) || t.equals(HashSet.class) || t.equals(LinkedHashSet.class) || t.equals(TreeSet.class) || t.equals(CopyOnWriteArraySet.class);
    }
    
    private static Set<?> createSet(final Class<?> t, final int size) {
        if (t.equals(Set.class) || t.equals(LinkedHashSet.class)) {
            return new LinkedHashSet<Object>(StaticUtils.computeMapCapacity(size));
        }
        if (t.equals(HashSet.class)) {
            return new HashSet<Object>(StaticUtils.computeMapCapacity(size));
        }
        if (t.equals(TreeSet.class)) {
            return new TreeSet<Object>();
        }
        if (t.equals(CopyOnWriteArraySet.class)) {
            return new CopyOnWriteArraySet<Object>();
        }
        return null;
    }
    
    @Override
    public AttributeTypeDefinition constructAttributeType(final Field f, final OIDAllocator a) throws LDAPPersistException {
        final LDAPField at = f.getAnnotation(LDAPField.class);
        String attrName;
        if (at.attribute().isEmpty()) {
            attrName = f.getName();
        }
        else {
            attrName = at.attribute();
        }
        final String oid = a.allocateAttributeTypeOID(attrName);
        final TypeInfo typeInfo = new TypeInfo(f.getGenericType());
        if (!typeInfo.isSupported()) {
            throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(String.valueOf(typeInfo.getType())));
        }
        final boolean isSingleValued = !supportsMultipleValues(typeInfo);
        String syntaxOID;
        if (isSingleValued) {
            syntaxOID = getSyntaxOID(typeInfo.getBaseClass());
        }
        else {
            syntaxOID = getSyntaxOID(typeInfo.getComponentType());
        }
        final MatchingRule mr = MatchingRule.selectMatchingRuleForSyntax(syntaxOID);
        return new AttributeTypeDefinition(oid, new String[] { attrName }, null, false, null, mr.getEqualityMatchingRuleNameOrOID(), mr.getOrderingMatchingRuleNameOrOID(), mr.getSubstringMatchingRuleNameOrOID(), syntaxOID, isSingleValued, false, false, AttributeUsage.USER_APPLICATIONS, null);
    }
    
    @Override
    public AttributeTypeDefinition constructAttributeType(final Method m, final OIDAllocator a) throws LDAPPersistException {
        final LDAPGetter at = m.getAnnotation(LDAPGetter.class);
        String attrName;
        if (at.attribute().isEmpty()) {
            attrName = StaticUtils.toInitialLowerCase(m.getName().substring(3));
        }
        else {
            attrName = at.attribute();
        }
        final String oid = a.allocateAttributeTypeOID(attrName);
        final TypeInfo typeInfo = new TypeInfo(m.getGenericReturnType());
        if (!typeInfo.isSupported()) {
            throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(String.valueOf(typeInfo.getType())));
        }
        final boolean isSingleValued = !supportsMultipleValues(typeInfo);
        String syntaxOID;
        if (isSingleValued) {
            syntaxOID = getSyntaxOID(typeInfo.getBaseClass());
        }
        else {
            syntaxOID = getSyntaxOID(typeInfo.getComponentType());
        }
        return new AttributeTypeDefinition(oid, new String[] { attrName }, null, false, null, null, null, null, syntaxOID, isSingleValued, false, false, AttributeUsage.USER_APPLICATIONS, null);
    }
    
    private static String getSyntaxOID(final Class<?> t) {
        if (t.equals(BigDecimal.class) || t.equals(Double.class) || t.equals(Double.TYPE) || t.equals(Float.class) || t.equals(Float.TYPE) || t.equals(String.class) || t.equals(StringBuffer.class) || t.equals(StringBuilder.class) || t.equals(URI.class) || t.equals(URL.class) || t.equals(Filter.class) || t.equals(LDAPURL.class)) {
            return "1.3.6.1.4.1.1466.115.121.1.15";
        }
        if (t.equals(AtomicInteger.class) || t.equals(AtomicLong.class) || t.equals(BigInteger.class) || t.equals(Integer.class) || t.equals(Integer.TYPE) || t.equals(Long.class) || t.equals(Long.TYPE) || t.equals(Short.class) || t.equals(Short.TYPE)) {
            return "1.3.6.1.4.1.1466.115.121.1.27";
        }
        if (t.equals(UUID.class)) {
            return "1.3.6.1.4.1.1466.115.121.1.15";
        }
        if (t.equals(DN.class) || t.equals(RDN.class)) {
            return "1.3.6.1.4.1.1466.115.121.1.12";
        }
        if (t.equals(Boolean.class) || t.equals(Boolean.TYPE)) {
            return "1.3.6.1.4.1.1466.115.121.1.7";
        }
        if (t.equals(Date.class)) {
            return "1.3.6.1.4.1.1466.115.121.1.24";
        }
        if (t.isArray()) {
            final Class<?> ct = t.getComponentType();
            if (ct.equals(Byte.TYPE)) {
                return "1.3.6.1.4.1.1466.115.121.1.40";
            }
            if (ct.equals(Character.TYPE)) {
                return "1.3.6.1.4.1.1466.115.121.1.15";
            }
        }
        else {
            if (t.isEnum()) {
                return "1.3.6.1.4.1.1466.115.121.1.15";
            }
            if (Serializable.class.isAssignableFrom(t)) {
                return "1.3.6.1.4.1.1466.115.121.1.40";
            }
        }
        return null;
    }
    
    @Override
    public boolean supportsMultipleValues(final Field field) {
        return supportsMultipleValues(new TypeInfo(field.getGenericType()));
    }
    
    @Override
    public boolean supportsMultipleValues(final Method method) {
        final Type[] paramTypes = method.getGenericParameterTypes();
        return paramTypes.length == 1 && supportsMultipleValues(new TypeInfo(paramTypes[0]));
    }
    
    private static boolean supportsMultipleValues(final TypeInfo t) {
        if (t.isArray()) {
            final Class<?> componentType = t.getComponentType();
            return !componentType.equals(Byte.TYPE) && !componentType.equals(Character.TYPE);
        }
        return t.isMultiValued();
    }
    
    @Override
    public Attribute encodeFieldValue(final Field field, final Object value, final String name) throws LDAPPersistException {
        return encodeValue(field.getGenericType(), value, name);
    }
    
    @Override
    public Attribute encodeMethodValue(final Method method, final Object value, final String name) throws LDAPPersistException {
        return encodeValue(method.getGenericReturnType(), value, name);
    }
    
    private static Attribute encodeValue(final Type type, final Object value, final String name) throws LDAPPersistException {
        final TypeInfo typeInfo = new TypeInfo(type);
        final Class<?> c = typeInfo.getBaseClass();
        if (c.equals(AtomicInteger.class) || c.equals(AtomicLong.class) || c.equals(BigDecimal.class) || c.equals(BigInteger.class) || c.equals(Double.class) || c.equals(Double.TYPE) || c.equals(Float.class) || c.equals(Float.TYPE) || c.equals(Integer.class) || c.equals(Integer.TYPE) || c.equals(Long.class) || c.equals(Long.TYPE) || c.equals(Short.class) || c.equals(Short.TYPE) || c.equals(String.class) || c.equals(StringBuffer.class) || c.equals(StringBuilder.class) || c.equals(UUID.class) || c.equals(DN.class) || c.equals(Filter.class) || c.equals(LDAPURL.class) || c.equals(RDN.class)) {
            final String syntaxOID = getSyntaxOID(c);
            final MatchingRule matchingRule = MatchingRule.selectMatchingRuleForSyntax(syntaxOID);
            return new Attribute(name, matchingRule, String.valueOf(value));
        }
        if (value instanceof URI) {
            final URI uri = (URI)value;
            return new Attribute(name, uri.toASCIIString());
        }
        if (value instanceof URL) {
            final URL url = (URL)value;
            return new Attribute(name, url.toExternalForm());
        }
        if (value instanceof byte[]) {
            return new Attribute(name, OctetStringMatchingRule.getInstance(), (byte[])value);
        }
        if (value instanceof char[]) {
            return new Attribute(name, new String((char[])value));
        }
        if (c.equals(Boolean.class) || c.equals(Boolean.TYPE)) {
            final Boolean b = (Boolean)value;
            final MatchingRule matchingRule = BooleanMatchingRule.getInstance();
            if (b) {
                return new Attribute(name, matchingRule, "TRUE");
            }
            return new Attribute(name, matchingRule, "FALSE");
        }
        else {
            if (c.equals(Date.class)) {
                final Date d = (Date)value;
                return new Attribute(name, GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(d));
            }
            if (typeInfo.isArray()) {
                return encodeArray(typeInfo.getComponentType(), value, name);
            }
            if (typeInfo.isEnum()) {
                final Enum<?> e = (Enum<?>)value;
                return new Attribute(name, e.name());
            }
            if (Collection.class.isAssignableFrom(c)) {
                return encodeCollection(typeInfo.getComponentType(), (Collection<?>)value, name);
            }
            if (Serializable.class.isAssignableFrom(c)) {
                try {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(value);
                    oos.close();
                    return new Attribute(name, OctetStringMatchingRule.getInstance(), baos.toByteArray());
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_CANNOT_SERIALIZE.get(name, StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(String.valueOf(type)));
        }
    }
    
    private static Attribute encodeArray(final Class<?> arrayType, final Object arrayObject, final String attributeName) throws LDAPPersistException {
        final ASN1OctetString[] values = new ASN1OctetString[Array.getLength(arrayObject)];
        final AtomicReference<MatchingRule> matchingRule = new AtomicReference<MatchingRule>();
        for (int i = 0; i < values.length; ++i) {
            final Object o = Array.get(arrayObject, i);
            if (arrayType.equals(AtomicInteger.class) || arrayType.equals(AtomicLong.class) || arrayType.equals(BigDecimal.class) || arrayType.equals(BigInteger.class) || arrayType.equals(Double.class) || arrayType.equals(Double.TYPE) || arrayType.equals(Float.class) || arrayType.equals(Float.TYPE) || arrayType.equals(Integer.class) || arrayType.equals(Integer.TYPE) || arrayType.equals(Long.class) || arrayType.equals(Long.TYPE) || arrayType.equals(Short.class) || arrayType.equals(Short.TYPE) || arrayType.equals(String.class) || arrayType.equals(StringBuffer.class) || arrayType.equals(StringBuilder.class) || arrayType.equals(UUID.class) || arrayType.equals(DN.class) || arrayType.equals(Filter.class) || arrayType.equals(LDAPURL.class) || arrayType.equals(RDN.class)) {
                if (matchingRule.get() == null) {
                    final String syntaxOID = getSyntaxOID(arrayType);
                    matchingRule.set(MatchingRule.selectMatchingRuleForSyntax(syntaxOID));
                }
                values[i] = new ASN1OctetString(String.valueOf(o));
            }
            else if (arrayType.equals(URI.class)) {
                final URI uri = (URI)o;
                values[i] = new ASN1OctetString(uri.toASCIIString());
            }
            else if (arrayType.equals(URL.class)) {
                final URL url = (URL)o;
                values[i] = new ASN1OctetString(url.toExternalForm());
            }
            else if (o instanceof byte[]) {
                matchingRule.compareAndSet(null, OctetStringMatchingRule.getInstance());
                values[i] = new ASN1OctetString((byte[])o);
            }
            else if (o instanceof char[]) {
                values[i] = new ASN1OctetString(new String((char[])o));
            }
            else if (arrayType.equals(Boolean.class) || arrayType.equals(Boolean.TYPE)) {
                matchingRule.compareAndSet(null, BooleanMatchingRule.getInstance());
                final Boolean b = (Boolean)o;
                if (b) {
                    values[i] = new ASN1OctetString("TRUE");
                }
                else {
                    values[i] = new ASN1OctetString("FALSE");
                }
            }
            else if (arrayType.equals(Date.class)) {
                matchingRule.compareAndSet(null, GeneralizedTimeMatchingRule.getInstance());
                final Date d = (Date)o;
                values[i] = new ASN1OctetString(StaticUtils.encodeGeneralizedTime(d));
            }
            else {
                if (!arrayType.isEnum()) {
                    if (Serializable.class.isAssignableFrom(arrayType)) {
                        matchingRule.compareAndSet(null, OctetStringMatchingRule.getInstance());
                        try {
                            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            final ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(o);
                            oos.close();
                            values[i] = new ASN1OctetString(baos.toByteArray());
                            continue;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_CANNOT_SERIALIZE.get(attributeName, StaticUtils.getExceptionMessage(e)), e);
                        }
                    }
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(arrayType.getName()));
                }
                final Enum<?> e2 = (Enum<?>)o;
                values[i] = new ASN1OctetString(e2.name());
            }
        }
        matchingRule.compareAndSet(null, CaseIgnoreStringMatchingRule.getInstance());
        return new Attribute(attributeName, matchingRule.get(), values);
    }
    
    private static Attribute encodeCollection(final Class<?> genericType, final Collection<?> collection, final String attributeName) throws LDAPPersistException {
        final ASN1OctetString[] values = new ASN1OctetString[collection.size()];
        final AtomicReference<MatchingRule> matchingRule = new AtomicReference<MatchingRule>();
        int i = 0;
        for (final Object o : collection) {
            Label_0752: {
                if (genericType.equals(AtomicInteger.class) || genericType.equals(AtomicLong.class) || genericType.equals(BigDecimal.class) || genericType.equals(BigInteger.class) || genericType.equals(Double.class) || genericType.equals(Double.TYPE) || genericType.equals(Float.class) || genericType.equals(Float.TYPE) || genericType.equals(Integer.class) || genericType.equals(Integer.TYPE) || genericType.equals(Long.class) || genericType.equals(Long.TYPE) || genericType.equals(Short.class) || genericType.equals(Short.TYPE) || genericType.equals(String.class) || genericType.equals(StringBuffer.class) || genericType.equals(StringBuilder.class) || genericType.equals(UUID.class) || genericType.equals(DN.class) || genericType.equals(Filter.class) || genericType.equals(LDAPURL.class) || genericType.equals(RDN.class)) {
                    if (matchingRule.get() == null) {
                        final String syntaxOID = getSyntaxOID(genericType);
                        matchingRule.set(MatchingRule.selectMatchingRuleForSyntax(syntaxOID));
                    }
                    values[i] = new ASN1OctetString(String.valueOf(o));
                }
                else if (genericType.equals(URI.class)) {
                    final URI uri = (URI)o;
                    values[i] = new ASN1OctetString(uri.toASCIIString());
                }
                else if (genericType.equals(URL.class)) {
                    final URL url = (URL)o;
                    values[i] = new ASN1OctetString(url.toExternalForm());
                }
                else if (o instanceof byte[]) {
                    matchingRule.compareAndSet(null, OctetStringMatchingRule.getInstance());
                    values[i] = new ASN1OctetString((byte[])o);
                }
                else if (o instanceof char[]) {
                    values[i] = new ASN1OctetString(new String((char[])o));
                }
                else if (genericType.equals(Boolean.class) || genericType.equals(Boolean.TYPE)) {
                    matchingRule.compareAndSet(null, BooleanMatchingRule.getInstance());
                    final Boolean b = (Boolean)o;
                    if (b) {
                        values[i] = new ASN1OctetString("TRUE");
                    }
                    else {
                        values[i] = new ASN1OctetString("FALSE");
                    }
                }
                else if (genericType.equals(Date.class)) {
                    matchingRule.compareAndSet(null, GeneralizedTimeMatchingRule.getInstance());
                    final Date d = (Date)o;
                    values[i] = new ASN1OctetString(StaticUtils.encodeGeneralizedTime(d));
                }
                else {
                    if (!genericType.isEnum()) {
                        if (Serializable.class.isAssignableFrom(genericType)) {
                            matchingRule.compareAndSet(null, OctetStringMatchingRule.getInstance());
                            try {
                                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                                oos.writeObject(o);
                                oos.close();
                                values[i] = new ASN1OctetString(baos.toByteArray());
                                break Label_0752;
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_CANNOT_SERIALIZE.get(attributeName, StaticUtils.getExceptionMessage(e)), e);
                            }
                        }
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(genericType.getName()));
                    }
                    final Enum<?> e2 = (Enum<?>)o;
                    values[i] = new ASN1OctetString(e2.name());
                }
            }
            ++i;
        }
        matchingRule.compareAndSet(null, CaseIgnoreStringMatchingRule.getInstance());
        return new Attribute(attributeName, matchingRule.get(), values);
    }
    
    @Override
    public void decodeField(final Field field, final Object object, final Attribute attribute) throws LDAPPersistException {
        field.setAccessible(true);
        final TypeInfo typeInfo = new TypeInfo(field.getGenericType());
        try {
            final Class<?> baseClass = typeInfo.getBaseClass();
            final Object newValue = getValue(baseClass, attribute, 0);
            if (newValue != null) {
                field.set(object, newValue);
                return;
            }
            if (typeInfo.isArray()) {
                final Class<?> componentType = typeInfo.getComponentType();
                final ASN1OctetString[] values = attribute.getRawValues();
                final Object arrayObject = Array.newInstance(componentType, values.length);
                for (int i = 0; i < values.length; ++i) {
                    final Object o = getValue(componentType, attribute, i);
                    if (o == null) {
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(componentType.getName()));
                    }
                    Array.set(arrayObject, i, o);
                }
                field.set(object, arrayObject);
                return;
            }
            if (typeInfo.isList() && isSupportedListType(baseClass)) {
                final Class<?> componentType = typeInfo.getComponentType();
                if (componentType == null) {
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(baseClass.getName()));
                }
                final ASN1OctetString[] values = attribute.getRawValues();
                final List<?> l = createList(baseClass, values.length);
                for (int i = 0; i < values.length; ++i) {
                    final Object o = getValue(componentType, attribute, i);
                    if (o == null) {
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(componentType.getName()));
                    }
                    invokeAdd(l, o);
                }
                field.set(object, l);
            }
            else {
                if (!typeInfo.isSet() || !isSupportedSetType(baseClass)) {
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(baseClass.getName()));
                }
                final Class<?> componentType = typeInfo.getComponentType();
                if (componentType == null) {
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(baseClass.getName()));
                }
                final ASN1OctetString[] values = attribute.getRawValues();
                final Set<?> j = createSet(baseClass, values.length);
                for (int i = 0; i < values.length; ++i) {
                    final Object o = getValue(componentType, attribute, i);
                    if (o == null) {
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(componentType.getName()));
                    }
                    invokeAdd(j, o);
                }
                field.set(object, j);
            }
        }
        catch (final LDAPPersistException lpe) {
            Debug.debugException(lpe);
            throw lpe;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPPersistException(StaticUtils.getExceptionMessage(e), e);
        }
    }
    
    @Override
    public void invokeSetter(final Method method, final Object object, final Attribute attribute) throws LDAPPersistException {
        final TypeInfo typeInfo = new TypeInfo(method.getGenericParameterTypes()[0]);
        final Class<?> baseClass = typeInfo.getBaseClass();
        method.setAccessible(true);
        try {
            final Object newValue = getValue(baseClass, attribute, 0);
            if (newValue != null) {
                method.invoke(object, newValue);
                return;
            }
            if (typeInfo.isArray()) {
                final Class<?> componentType = typeInfo.getComponentType();
                final ASN1OctetString[] values = attribute.getRawValues();
                final Object arrayObject = Array.newInstance(componentType, values.length);
                for (int i = 0; i < values.length; ++i) {
                    final Object o = getValue(componentType, attribute, i);
                    if (o == null) {
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(componentType.getName()));
                    }
                    Array.set(arrayObject, i, o);
                }
                method.invoke(object, arrayObject);
                return;
            }
            if (typeInfo.isList() && isSupportedListType(baseClass)) {
                final Class<?> componentType = typeInfo.getComponentType();
                if (componentType == null) {
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(baseClass.getName()));
                }
                final ASN1OctetString[] values = attribute.getRawValues();
                final List<?> l = createList(baseClass, values.length);
                for (int i = 0; i < values.length; ++i) {
                    final Object o = getValue(componentType, attribute, i);
                    if (o == null) {
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(componentType.getName()));
                    }
                    invokeAdd(l, o);
                }
                method.invoke(object, l);
            }
            else {
                if (!typeInfo.isSet() || !isSupportedSetType(baseClass)) {
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(baseClass.getName()));
                }
                final Class<?> componentType = typeInfo.getComponentType();
                if (componentType == null) {
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(baseClass.getName()));
                }
                final ASN1OctetString[] values = attribute.getRawValues();
                final Set<?> s = createSet(baseClass, values.length);
                for (int i = 0; i < values.length; ++i) {
                    final Object o = getValue(componentType, attribute, i);
                    if (o == null) {
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE.get(componentType.getName()));
                    }
                    invokeAdd(s, o);
                }
                method.invoke(object, s);
            }
        }
        catch (final LDAPPersistException lpe) {
            Debug.debugException(lpe);
            throw lpe;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InvocationTargetException) {
                final Throwable targetException = ((InvocationTargetException)e).getTargetException();
                throw new LDAPPersistException(StaticUtils.getExceptionMessage(targetException), targetException);
            }
            throw new LDAPPersistException(StaticUtils.getExceptionMessage(e), e);
        }
    }
    
    private static Object getValue(final Class<?> t, final Attribute a, final int p) throws LDAPPersistException {
        final ASN1OctetString v = a.getRawValues()[p];
        if (t.equals(AtomicInteger.class)) {
            return new AtomicInteger(Integer.valueOf(v.stringValue()));
        }
        if (t.equals(AtomicLong.class)) {
            return new AtomicLong(Long.valueOf(v.stringValue()));
        }
        if (t.equals(BigDecimal.class)) {
            return new BigDecimal(v.stringValue());
        }
        if (t.equals(BigInteger.class)) {
            return new BigInteger(v.stringValue());
        }
        if (t.equals(Double.class) || t.equals(Double.TYPE)) {
            return Double.valueOf(v.stringValue());
        }
        if (t.equals(Float.class) || t.equals(Float.TYPE)) {
            return Float.valueOf(v.stringValue());
        }
        if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
            return Integer.valueOf(v.stringValue());
        }
        if (t.equals(Long.class) || t.equals(Long.TYPE)) {
            return Long.valueOf(v.stringValue());
        }
        if (t.equals(Short.class) || t.equals(Short.TYPE)) {
            return Short.valueOf(v.stringValue());
        }
        if (t.equals(String.class)) {
            return String.valueOf(v.stringValue());
        }
        if (t.equals(StringBuffer.class)) {
            return new StringBuffer(v.stringValue());
        }
        if (t.equals(StringBuilder.class)) {
            return new StringBuilder(v.stringValue());
        }
        if (t.equals(URI.class)) {
            try {
                return new URI(v.stringValue());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_VALUE_INVALID_URI.get(v.stringValue(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (t.equals(URL.class)) {
            try {
                return new URL(v.stringValue());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_VALUE_INVALID_URL.get(v.stringValue(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (t.equals(UUID.class)) {
            try {
                return UUID.fromString(v.stringValue());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_VALUE_INVALID_UUID.get(v.stringValue(), StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (t.equals(DN.class)) {
            try {
                return new DN(v.stringValue());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPPersistException(le.getMessage(), le);
            }
        }
        if (t.equals(Filter.class)) {
            try {
                return Filter.create(v.stringValue());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPPersistException(le.getMessage(), le);
            }
        }
        if (t.equals(LDAPURL.class)) {
            try {
                return new LDAPURL(v.stringValue());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPPersistException(le.getMessage(), le);
            }
        }
        if (t.equals(RDN.class)) {
            try {
                return new RDN(v.stringValue());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPPersistException(le.getMessage(), le);
            }
        }
        if (!t.equals(Boolean.class) && !t.equals(Boolean.TYPE)) {
            if (t.equals(Date.class)) {
                try {
                    return StaticUtils.decodeGeneralizedTime(v.stringValue());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_VALUE_INVALID_DATE.get(v.stringValue(), e.getMessage()), e);
                }
            }
            if (t.isArray()) {
                final Class<?> componentType = t.getComponentType();
                if (componentType.equals(Byte.TYPE)) {
                    return v.getValue();
                }
                if (componentType.equals(Character.TYPE)) {
                    return v.stringValue().toCharArray();
                }
            }
            else {
                if (t.isEnum()) {
                    try {
                        final Class<? extends Enum> enumClass = (Class<? extends Enum>)t;
                        return Enum.valueOf((Class<Object>)enumClass, v.stringValue());
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_VALUE_INVALID_ENUM.get(v.stringValue(), StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                if (Serializable.class.isAssignableFrom(t)) {
                    if (t.isArray() || Collection.class.isAssignableFrom(t)) {
                        return null;
                    }
                    try {
                        final ByteArrayInputStream bais = new ByteArrayInputStream(v.getValue());
                        final ObjectInputStream ois = new ObjectInputStream(bais);
                        final Object o = ois.readObject();
                        ois.close();
                        return o;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_CANNOT_DESERIALIZE.get(a.getName(), StaticUtils.getExceptionMessage(e)), e);
                    }
                }
            }
            return null;
        }
        final String s = v.stringValue();
        if (s.equalsIgnoreCase("TRUE")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("FALSE")) {
            return Boolean.FALSE;
        }
        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_VALUE_INVALID_BOOLEAN.get(s));
    }
    
    private static void invokeAdd(final Object l, final Object o) throws LDAPPersistException {
        final Class<?> c = l.getClass();
        for (final Method m : c.getMethods()) {
            if (m.getName().equals("add") && m.getGenericParameterTypes().length == 1) {
                try {
                    m.invoke(l, o);
                    return;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_CANNOT_ADD.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
        }
        throw new LDAPPersistException(PersistMessages.ERR_DEFAULT_ENCODER_CANNOT_FIND_ADD_METHOD.get());
    }
}
