package com.unboundid.ldap.sdk;

import com.unboundid.util.ByteStringBuffer;
import java.util.StringTokenizer;
import com.unboundid.ldif.LDIFWriter;
import java.math.BigInteger;
import java.util.Arrays;
import com.unboundid.util.Debug;
import com.unboundid.ldap.matchingrules.OctetStringMatchingRule;
import java.util.Map;
import java.util.HashSet;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.ldif.LDIFException;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.LinkedHashMap;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.Mutable;
import com.unboundid.ldif.LDIFRecord;

@Mutable
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class Entry implements LDIFRecord
{
    private static final ASN1OctetString EMPTY_OCTET_STRING;
    private static final long serialVersionUID = -4438809025903729197L;
    private volatile DN parsedDN;
    private final LinkedHashMap<String, Attribute> attributes;
    private final Schema schema;
    private String dn;
    
    protected Entry(final Entry e) {
        this.parsedDN = e.parsedDN;
        this.attributes = e.attributes;
        this.schema = e.schema;
        this.dn = e.dn;
    }
    
    public Entry(final String dn) {
        this(dn, (Schema)null);
    }
    
    public Entry(final String dn, final Schema schema) {
        Validator.ensureNotNull(dn);
        this.dn = dn;
        this.schema = schema;
        this.attributes = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(20));
    }
    
    public Entry(final DN dn) {
        this(dn, (Schema)null);
    }
    
    public Entry(final DN dn, final Schema schema) {
        Validator.ensureNotNull(dn);
        this.parsedDN = dn;
        this.dn = this.parsedDN.toString();
        this.schema = schema;
        this.attributes = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(20));
    }
    
    public Entry(final String dn, final Attribute... attributes) {
        this(dn, (Schema)null, attributes);
    }
    
    public Entry(final String dn, final Schema schema, final Attribute... attributes) {
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn;
        this.schema = schema;
        this.attributes = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(attributes.length));
        for (final Attribute a : attributes) {
            final String name = StaticUtils.toLowerCase(a.getName());
            final Attribute attr = this.attributes.get(name);
            if (attr == null) {
                this.attributes.put(name, a);
            }
            else {
                this.attributes.put(name, Attribute.mergeAttributes(attr, a));
            }
        }
    }
    
    public Entry(final DN dn, final Attribute... attributes) {
        this(dn, (Schema)null, attributes);
    }
    
    public Entry(final DN dn, final Schema schema, final Attribute... attributes) {
        Validator.ensureNotNull(dn, attributes);
        this.parsedDN = dn;
        this.dn = this.parsedDN.toString();
        this.schema = schema;
        this.attributes = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(attributes.length));
        for (final Attribute a : attributes) {
            final String name = StaticUtils.toLowerCase(a.getName());
            final Attribute attr = this.attributes.get(name);
            if (attr == null) {
                this.attributes.put(name, a);
            }
            else {
                this.attributes.put(name, Attribute.mergeAttributes(attr, a));
            }
        }
    }
    
    public Entry(final String dn, final Collection<Attribute> attributes) {
        this(dn, null, attributes);
    }
    
    public Entry(final String dn, final Schema schema, final Collection<Attribute> attributes) {
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn;
        this.schema = schema;
        this.attributes = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(attributes.size()));
        for (final Attribute a : attributes) {
            final String name = StaticUtils.toLowerCase(a.getName());
            final Attribute attr = this.attributes.get(name);
            if (attr == null) {
                this.attributes.put(name, a);
            }
            else {
                this.attributes.put(name, Attribute.mergeAttributes(attr, a));
            }
        }
    }
    
    public Entry(final DN dn, final Collection<Attribute> attributes) {
        this(dn, null, attributes);
    }
    
    public Entry(final DN dn, final Schema schema, final Collection<Attribute> attributes) {
        Validator.ensureNotNull(dn, attributes);
        this.parsedDN = dn;
        this.dn = this.parsedDN.toString();
        this.schema = schema;
        this.attributes = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(attributes.size()));
        for (final Attribute a : attributes) {
            final String name = StaticUtils.toLowerCase(a.getName());
            final Attribute attr = this.attributes.get(name);
            if (attr == null) {
                this.attributes.put(name, a);
            }
            else {
                this.attributes.put(name, Attribute.mergeAttributes(attr, a));
            }
        }
    }
    
    public Entry(final String... entryLines) throws LDIFException {
        this(null, entryLines);
    }
    
    public Entry(final Schema schema, final String... entryLines) throws LDIFException {
        final Entry e = LDIFReader.decodeEntry(false, schema, entryLines);
        this.schema = schema;
        this.dn = e.dn;
        this.parsedDN = e.parsedDN;
        this.attributes = e.attributes;
    }
    
    @Override
    public final String getDN() {
        return this.dn;
    }
    
    public void setDN(final String dn) {
        Validator.ensureNotNull(dn);
        this.dn = dn;
        this.parsedDN = null;
    }
    
    public void setDN(final DN dn) {
        Validator.ensureNotNull(dn);
        this.parsedDN = dn;
        this.dn = this.parsedDN.toString();
    }
    
    @Override
    public final DN getParsedDN() throws LDAPException {
        if (this.parsedDN == null) {
            this.parsedDN = new DN(this.dn, this.schema);
        }
        return this.parsedDN;
    }
    
    public final RDN getRDN() throws LDAPException {
        return this.getParsedDN().getRDN();
    }
    
    public final DN getParentDN() throws LDAPException {
        if (this.parsedDN == null) {
            this.parsedDN = new DN(this.dn, this.schema);
        }
        return this.parsedDN.getParent();
    }
    
    public final String getParentDNString() throws LDAPException {
        if (this.parsedDN == null) {
            this.parsedDN = new DN(this.dn, this.schema);
        }
        final DN parentDN = this.parsedDN.getParent();
        if (parentDN == null) {
            return null;
        }
        return parentDN.toString();
    }
    
    protected Schema getSchema() {
        return this.schema;
    }
    
    public final boolean hasAttribute(final String attributeName) {
        return this.hasAttribute(attributeName, this.schema);
    }
    
    public final boolean hasAttribute(final String attributeName, final Schema schema) {
        Validator.ensureNotNull(attributeName);
        if (this.attributes.containsKey(StaticUtils.toLowerCase(attributeName))) {
            return true;
        }
        if (schema != null) {
            final int semicolonPos = attributeName.indexOf(59);
            String baseName;
            String options;
            if (semicolonPos > 0) {
                baseName = attributeName.substring(0, semicolonPos);
                options = StaticUtils.toLowerCase(attributeName.substring(semicolonPos));
            }
            else {
                baseName = attributeName;
                options = "";
            }
            final AttributeTypeDefinition at = schema.getAttributeType(baseName);
            if (at != null) {
                if (this.attributes.containsKey(StaticUtils.toLowerCase(at.getOID()) + options)) {
                    return true;
                }
                for (final String name : at.getNames()) {
                    if (this.attributes.containsKey(StaticUtils.toLowerCase(name) + options)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public final boolean hasAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        final String lowerName = StaticUtils.toLowerCase(attribute.getName());
        final Attribute attr = this.attributes.get(lowerName);
        return attr != null && attr.equals(attribute);
    }
    
    public final boolean hasAttributeValue(final String attributeName, final String attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute attr = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        return attr != null && attr.hasValue(attributeValue);
    }
    
    public final boolean hasAttributeValue(final String attributeName, final String attributeValue, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute attr = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        return attr != null && attr.hasValue(attributeValue, matchingRule);
    }
    
    public final boolean hasAttributeValue(final String attributeName, final byte[] attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute attr = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        return attr != null && attr.hasValue(attributeValue);
    }
    
    public final boolean hasAttributeValue(final String attributeName, final byte[] attributeValue, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute attr = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        return attr != null && attr.hasValue(attributeValue, matchingRule);
    }
    
    public final boolean hasObjectClass(final String objectClassName) {
        return this.hasAttributeValue("objectClass", objectClassName);
    }
    
    public final Collection<Attribute> getAttributes() {
        return Collections.unmodifiableCollection((Collection<? extends Attribute>)this.attributes.values());
    }
    
    public final Attribute getAttribute(final String attributeName) {
        return this.getAttribute(attributeName, this.schema);
    }
    
    public final Attribute getAttribute(final String attributeName, final Schema schema) {
        Validator.ensureNotNull(attributeName);
        Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a != null || schema == null) {
            return a;
        }
        final int semicolonPos = attributeName.indexOf(59);
        String baseName;
        String options;
        if (semicolonPos > 0) {
            baseName = attributeName.substring(0, semicolonPos);
            options = StaticUtils.toLowerCase(attributeName.substring(semicolonPos));
        }
        else {
            baseName = attributeName;
            options = "";
        }
        final AttributeTypeDefinition at = schema.getAttributeType(baseName);
        if (at == null) {
            return null;
        }
        a = this.attributes.get(StaticUtils.toLowerCase(at.getOID() + options));
        if (a == null) {
            for (final String name : at.getNames()) {
                a = this.attributes.get(StaticUtils.toLowerCase(name) + options);
                if (a != null) {
                    return a;
                }
            }
        }
        return a;
    }
    
    public final List<Attribute> getAttributesWithOptions(final String baseName, final Set<String> options) {
        Validator.ensureNotNull(baseName);
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(10);
        for (final Attribute a : this.attributes.values()) {
            if (a.getBaseName().equalsIgnoreCase(baseName)) {
                if (options == null || options.isEmpty()) {
                    attrList.add(a);
                }
                else {
                    boolean allFound = true;
                    for (final String option : options) {
                        if (!a.hasOption(option)) {
                            allFound = false;
                            break;
                        }
                    }
                    if (!allFound) {
                        continue;
                    }
                    attrList.add(a);
                }
            }
        }
        return Collections.unmodifiableList((List<? extends Attribute>)attrList);
    }
    
    public String getAttributeValue(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValue();
    }
    
    public byte[] getAttributeValueBytes(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueByteArray();
    }
    
    public Boolean getAttributeValueAsBoolean(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueAsBoolean();
    }
    
    public Date getAttributeValueAsDate(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueAsDate();
    }
    
    public DN getAttributeValueAsDN(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueAsDN();
    }
    
    public Integer getAttributeValueAsInteger(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueAsInteger();
    }
    
    public Long getAttributeValueAsLong(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueAsLong();
    }
    
    public String[] getAttributeValues(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValues();
    }
    
    public byte[][] getAttributeValueByteArrays(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Attribute a = this.attributes.get(StaticUtils.toLowerCase(attributeName));
        if (a == null) {
            return null;
        }
        return a.getValueByteArrays();
    }
    
    public final Attribute getObjectClassAttribute() {
        return this.getAttribute("objectClass");
    }
    
    public final String[] getObjectClassValues() {
        return this.getAttributeValues("objectClass");
    }
    
    public boolean addAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        final String lowerName = StaticUtils.toLowerCase(attribute.getName());
        final Attribute attr = this.attributes.get(lowerName);
        if (attr == null) {
            this.attributes.put(lowerName, attribute);
            return true;
        }
        final Attribute newAttr = Attribute.mergeAttributes(attr, attribute);
        this.attributes.put(lowerName, newAttr);
        return attr.getRawValues().length != newAttr.getRawValues().length;
    }
    
    public boolean addAttribute(final String attributeName, final String attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        return this.addAttribute(new Attribute(attributeName, this.schema, new String[] { attributeValue }));
    }
    
    public boolean addAttribute(final String attributeName, final byte[] attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        return this.addAttribute(new Attribute(attributeName, this.schema, new byte[][] { attributeValue }));
    }
    
    public boolean addAttribute(final String attributeName, final String... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        return this.addAttribute(new Attribute(attributeName, this.schema, attributeValues));
    }
    
    public boolean addAttribute(final String attributeName, final byte[]... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        return this.addAttribute(new Attribute(attributeName, this.schema, attributeValues));
    }
    
    public boolean addAttribute(final String attributeName, final Collection<String> attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        return this.addAttribute(new Attribute(attributeName, this.schema, attributeValues));
    }
    
    public boolean removeAttribute(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        if (this.schema == null) {
            return this.attributes.remove(StaticUtils.toLowerCase(attributeName)) != null;
        }
        final Attribute a = this.getAttribute(attributeName, this.schema);
        if (a == null) {
            return false;
        }
        this.attributes.remove(StaticUtils.toLowerCase(a.getName()));
        return true;
    }
    
    public boolean removeAttributeValue(final String attributeName, final String attributeValue) {
        return this.removeAttributeValue(attributeName, attributeValue, null);
    }
    
    public boolean removeAttributeValue(final String attributeName, final String attributeValue, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute attr = this.getAttribute(attributeName, this.schema);
        if (attr == null) {
            return false;
        }
        final String lowerName = StaticUtils.toLowerCase(attr.getName());
        final Attribute newAttr = Attribute.removeValues(attr, new Attribute(attributeName, attributeValue), matchingRule);
        if (newAttr.hasValue()) {
            this.attributes.put(lowerName, newAttr);
        }
        else {
            this.attributes.remove(lowerName);
        }
        return attr.getRawValues().length != newAttr.getRawValues().length;
    }
    
    public boolean removeAttributeValue(final String attributeName, final byte[] attributeValue) {
        return this.removeAttributeValue(attributeName, attributeValue, null);
    }
    
    public boolean removeAttributeValue(final String attributeName, final byte[] attributeValue, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute attr = this.getAttribute(attributeName, this.schema);
        if (attr == null) {
            return false;
        }
        final String lowerName = StaticUtils.toLowerCase(attr.getName());
        final Attribute newAttr = Attribute.removeValues(attr, new Attribute(attributeName, attributeValue), matchingRule);
        if (newAttr.hasValue()) {
            this.attributes.put(lowerName, newAttr);
        }
        else {
            this.attributes.remove(lowerName);
        }
        return attr.getRawValues().length != newAttr.getRawValues().length;
    }
    
    public boolean removeAttributeValues(final String attributeName, final String... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        final Attribute attr = this.getAttribute(attributeName, this.schema);
        if (attr == null) {
            return false;
        }
        final String lowerName = StaticUtils.toLowerCase(attr.getName());
        final Attribute newAttr = Attribute.removeValues(attr, new Attribute(attributeName, attributeValues));
        if (newAttr.hasValue()) {
            this.attributes.put(lowerName, newAttr);
        }
        else {
            this.attributes.remove(lowerName);
        }
        return attr.getRawValues().length != newAttr.getRawValues().length;
    }
    
    public boolean removeAttributeValues(final String attributeName, final byte[]... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        final Attribute attr = this.getAttribute(attributeName, this.schema);
        if (attr == null) {
            return false;
        }
        final String lowerName = StaticUtils.toLowerCase(attr.getName());
        final Attribute newAttr = Attribute.removeValues(attr, new Attribute(attributeName, attributeValues));
        if (newAttr.hasValue()) {
            this.attributes.put(lowerName, newAttr);
        }
        else {
            this.attributes.remove(lowerName);
        }
        return attr.getRawValues().length != newAttr.getRawValues().length;
    }
    
    public void setAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        final Attribute a = this.getAttribute(attribute.getName(), this.schema);
        String lowerName;
        if (a == null) {
            lowerName = StaticUtils.toLowerCase(attribute.getName());
        }
        else {
            lowerName = StaticUtils.toLowerCase(a.getName());
        }
        this.attributes.put(lowerName, attribute);
    }
    
    public void setAttribute(final String attributeName, final String attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        this.setAttribute(new Attribute(attributeName, this.schema, new String[] { attributeValue }));
    }
    
    public void setAttribute(final String attributeName, final byte[] attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        this.setAttribute(new Attribute(attributeName, this.schema, new byte[][] { attributeValue }));
    }
    
    public void setAttribute(final String attributeName, final String... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        this.setAttribute(new Attribute(attributeName, this.schema, attributeValues));
    }
    
    public void setAttribute(final String attributeName, final byte[]... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        this.setAttribute(new Attribute(attributeName, this.schema, attributeValues));
    }
    
    public void setAttribute(final String attributeName, final Collection<String> attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        this.setAttribute(new Attribute(attributeName, this.schema, attributeValues));
    }
    
    public boolean matchesBaseAndScope(final String baseDN, final SearchScope scope) throws LDAPException {
        return this.getParsedDN().matchesBaseAndScope(new DN(baseDN), scope);
    }
    
    public boolean matchesBaseAndScope(final DN baseDN, final SearchScope scope) throws LDAPException {
        return this.getParsedDN().matchesBaseAndScope(baseDN, scope);
    }
    
    public static List<Modification> diff(final Entry sourceEntry, final Entry targetEntry, final boolean ignoreRDN, final String... attributes) {
        return diff(sourceEntry, targetEntry, ignoreRDN, true, attributes);
    }
    
    public static List<Modification> diff(final Entry sourceEntry, final Entry targetEntry, final boolean ignoreRDN, final boolean reversible, final String... attributes) {
        return diff(sourceEntry, targetEntry, ignoreRDN, reversible, false, attributes);
    }
    
    public static List<Modification> diff(final Entry sourceEntry, final Entry targetEntry, final boolean ignoreRDN, final boolean reversible, final boolean byteForByte, final String... attributes) {
        HashSet<String> compareAttrs = null;
        if (attributes != null && attributes.length > 0) {
            compareAttrs = new HashSet<String>(StaticUtils.computeMapCapacity(attributes.length));
            for (final String s : attributes) {
                compareAttrs.add(StaticUtils.toLowerCase(Attribute.getBaseName(s)));
            }
        }
        final LinkedHashMap<String, Attribute> sourceOnlyAttrs = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(20));
        final LinkedHashMap<String, Attribute> targetOnlyAttrs = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(20));
        final LinkedHashMap<String, Attribute> commonAttrs = new LinkedHashMap<String, Attribute>(StaticUtils.computeMapCapacity(20));
        for (final Map.Entry<String, Attribute> e : sourceEntry.attributes.entrySet()) {
            final String lowerName = StaticUtils.toLowerCase(e.getKey());
            if (compareAttrs != null && !compareAttrs.contains(Attribute.getBaseName(lowerName))) {
                continue;
            }
            Attribute attr;
            if (byteForByte) {
                final Attribute a = e.getValue();
                attr = new Attribute(a.getName(), OctetStringMatchingRule.getInstance(), a.getRawValues());
            }
            else {
                attr = e.getValue();
            }
            sourceOnlyAttrs.put(lowerName, attr);
            commonAttrs.put(lowerName, attr);
        }
        for (final Map.Entry<String, Attribute> e : targetEntry.attributes.entrySet()) {
            final String lowerName = StaticUtils.toLowerCase(e.getKey());
            if (compareAttrs != null && !compareAttrs.contains(Attribute.getBaseName(lowerName))) {
                continue;
            }
            if (sourceOnlyAttrs.remove(lowerName) != null) {
                continue;
            }
            Attribute attr;
            if (byteForByte) {
                final Attribute a = e.getValue();
                attr = new Attribute(a.getName(), OctetStringMatchingRule.getInstance(), a.getRawValues());
            }
            else {
                attr = e.getValue();
            }
            targetOnlyAttrs.put(lowerName, attr);
        }
        for (final String lowerName2 : sourceOnlyAttrs.keySet()) {
            commonAttrs.remove(lowerName2);
        }
        RDN sourceRDN = null;
        RDN targetRDN = null;
        if (ignoreRDN) {
            try {
                sourceRDN = sourceEntry.getRDN();
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
            try {
                targetRDN = targetEntry.getRDN();
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
        final ArrayList<Modification> mods = new ArrayList<Modification>(10);
        Iterator i$3 = sourceOnlyAttrs.values().iterator();
        while (i$3.hasNext()) {
            final Attribute a = i$3.next();
            if (reversible) {
                ASN1OctetString[] values = a.getRawValues();
                if (sourceRDN != null && sourceRDN.hasAttribute(a.getName())) {
                    final ArrayList<ASN1OctetString> newValues = new ArrayList<ASN1OctetString>(values.length);
                    for (final ASN1OctetString value : values) {
                        if (!sourceRDN.hasAttributeValue(a.getName(), value.getValue())) {
                            newValues.add(value);
                        }
                    }
                    if (newValues.isEmpty()) {
                        continue;
                    }
                    values = new ASN1OctetString[newValues.size()];
                    newValues.toArray(values);
                }
                mods.add(new Modification(ModificationType.DELETE, a.getName(), values));
            }
            else {
                mods.add(new Modification(ModificationType.REPLACE, a.getName()));
            }
        }
        i$3 = targetOnlyAttrs.values().iterator();
        while (i$3.hasNext()) {
            final Attribute a = i$3.next();
            ASN1OctetString[] values = a.getRawValues();
            if (targetRDN != null && targetRDN.hasAttribute(a.getName())) {
                final ArrayList<ASN1OctetString> newValues = new ArrayList<ASN1OctetString>(values.length);
                for (final ASN1OctetString value : values) {
                    if (!targetRDN.hasAttributeValue(a.getName(), value.getValue())) {
                        newValues.add(value);
                    }
                }
                if (newValues.isEmpty()) {
                    continue;
                }
                values = new ASN1OctetString[newValues.size()];
                newValues.toArray(values);
            }
            if (reversible) {
                mods.add(new Modification(ModificationType.ADD, a.getName(), values));
            }
            else {
                mods.add(new Modification(ModificationType.REPLACE, a.getName(), values));
            }
        }
        i$3 = commonAttrs.values().iterator();
        while (i$3.hasNext()) {
            final Attribute sourceAttr = i$3.next();
            Attribute targetAttr = targetEntry.getAttribute(sourceAttr.getName());
            if (byteForByte && targetAttr != null) {
                targetAttr = new Attribute(targetAttr.getName(), OctetStringMatchingRule.getInstance(), targetAttr.getRawValues());
            }
            if (sourceAttr.equals(targetAttr)) {
                continue;
            }
            if (reversible || (targetRDN != null && targetRDN.hasAttribute(targetAttr.getName()))) {
                final ASN1OctetString[] sourceValueArray = sourceAttr.getRawValues();
                final LinkedHashMap<ASN1OctetString, ASN1OctetString> sourceValues = new LinkedHashMap<ASN1OctetString, ASN1OctetString>(StaticUtils.computeMapCapacity(sourceValueArray.length));
                for (final ASN1OctetString s2 : sourceValueArray) {
                    try {
                        sourceValues.put(sourceAttr.getMatchingRule().normalize(s2), s2);
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                        sourceValues.put(s2, s2);
                    }
                }
                final ASN1OctetString[] targetValueArray = targetAttr.getRawValues();
                final LinkedHashMap<ASN1OctetString, ASN1OctetString> targetValues = new LinkedHashMap<ASN1OctetString, ASN1OctetString>(StaticUtils.computeMapCapacity(targetValueArray.length));
                for (final ASN1OctetString s3 : targetValueArray) {
                    try {
                        targetValues.put(sourceAttr.getMatchingRule().normalize(s3), s3);
                    }
                    catch (final Exception e4) {
                        Debug.debugException(e4);
                        targetValues.put(s3, s3);
                    }
                }
                final Iterator<Map.Entry<ASN1OctetString, ASN1OctetString>> sourceIterator = sourceValues.entrySet().iterator();
                while (sourceIterator.hasNext()) {
                    final Map.Entry<ASN1OctetString, ASN1OctetString> e5 = sourceIterator.next();
                    if (targetValues.remove(e5.getKey()) != null) {
                        sourceIterator.remove();
                    }
                    else {
                        if (sourceRDN == null || !sourceRDN.hasAttributeValue(sourceAttr.getName(), e5.getValue().getValue())) {
                            continue;
                        }
                        sourceIterator.remove();
                    }
                }
                final Iterator<Map.Entry<ASN1OctetString, ASN1OctetString>> targetIterator = targetValues.entrySet().iterator();
                while (targetIterator.hasNext()) {
                    final Map.Entry<ASN1OctetString, ASN1OctetString> e6 = targetIterator.next();
                    if (targetRDN != null && targetRDN.hasAttributeValue(targetAttr.getName(), e6.getValue().getValue())) {
                        targetIterator.remove();
                    }
                }
                final ArrayList<ASN1OctetString> delValues = new ArrayList<ASN1OctetString>(sourceValues.values());
                if (!delValues.isEmpty()) {
                    final ASN1OctetString[] delArray = new ASN1OctetString[delValues.size()];
                    mods.add(new Modification(ModificationType.DELETE, sourceAttr.getName(), delValues.toArray(delArray)));
                }
                final ArrayList<ASN1OctetString> addValues = new ArrayList<ASN1OctetString>(targetValues.values());
                if (addValues.isEmpty()) {
                    continue;
                }
                final ASN1OctetString[] addArray = new ASN1OctetString[addValues.size()];
                mods.add(new Modification(ModificationType.ADD, targetAttr.getName(), addValues.toArray(addArray)));
            }
            else {
                mods.add(new Modification(ModificationType.REPLACE, targetAttr.getName(), targetAttr.getRawValues()));
            }
        }
        return mods;
    }
    
    public static Entry mergeEntries(final Entry... entries) {
        Validator.ensureNotNull(entries);
        Validator.ensureTrue(entries.length > 0);
        final Entry newEntry = entries[0].duplicate();
        for (int i = 1; i < entries.length; ++i) {
            for (final Attribute a : entries[i].attributes.values()) {
                newEntry.addAttribute(a);
            }
        }
        return newEntry;
    }
    
    public static Entry intersectEntries(final Entry... entries) {
        Validator.ensureNotNull(entries);
        Validator.ensureTrue(entries.length > 0);
        final Entry newEntry = entries[0].duplicate();
        for (final Attribute a : entries[0].attributes.values()) {
            final String name = a.getName();
            for (final byte[] v : a.getValueByteArrays()) {
                for (int i = 1; i < entries.length; ++i) {
                    if (!entries[i].hasAttributeValue(name, v)) {
                        newEntry.removeAttributeValue(name, v);
                        break;
                    }
                }
            }
        }
        return newEntry;
    }
    
    public static Entry applyModifications(final Entry entry, final boolean lenient, final Modification... modifications) throws LDAPException {
        Validator.ensureNotNull(entry, modifications);
        Validator.ensureFalse(modifications.length == 0);
        return applyModifications(entry, lenient, Arrays.asList(modifications));
    }
    
    public static Entry applyModifications(final Entry entry, final boolean lenient, final List<Modification> modifications) throws LDAPException {
        Validator.ensureNotNull(entry, modifications);
        Validator.ensureFalse(modifications.isEmpty());
        final Entry e = entry.duplicate();
        final ArrayList<String> errors = new ArrayList<String>(modifications.size());
        ResultCode resultCode = null;
        RDN rdn = null;
        try {
            rdn = entry.getRDN();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
        for (final Modification m : modifications) {
            final String name = m.getAttributeName();
            final byte[][] values = m.getValueByteArrays();
            switch (m.getModificationType().intValue()) {
                case 0: {
                    if (lenient) {
                        e.addAttribute(m.getAttribute());
                        continue;
                    }
                    if (values.length == 0) {
                        errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_ADD_NO_VALUES.get(name));
                    }
                    for (int i = 0; i < values.length; ++i) {
                        if (!e.addAttribute(name, values[i])) {
                            if (resultCode == null) {
                                resultCode = ResultCode.ATTRIBUTE_OR_VALUE_EXISTS;
                            }
                            errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_ADD_EXISTING.get(m.getValues()[i], name));
                        }
                    }
                    continue;
                }
                case 1: {
                    if (values.length == 0) {
                        final boolean removed = e.removeAttribute(name);
                        if (lenient || removed) {
                            continue;
                        }
                        if (resultCode == null) {
                            resultCode = ResultCode.NO_SUCH_ATTRIBUTE;
                        }
                        errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_DELETE_NONEXISTENT_ATTR.get(name));
                        continue;
                    }
                    for (int i = 0; i < values.length; ++i) {
                        final boolean removed2 = e.removeAttributeValue(name, values[i]);
                        if (!lenient && !removed2) {
                            if (resultCode == null) {
                                resultCode = ResultCode.NO_SUCH_ATTRIBUTE;
                            }
                            errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_DELETE_NONEXISTENT_VALUE.get(m.getValues()[i], name));
                        }
                    }
                    continue;
                }
                case 2: {
                    if (values.length == 0) {
                        e.removeAttribute(name);
                        continue;
                    }
                    e.setAttribute(m.getAttribute());
                    continue;
                }
                case 3: {
                    final Attribute a = e.getAttribute(name);
                    if (a == null || !a.hasValue()) {
                        errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_INCREMENT_NO_SUCH_ATTR.get(name));
                        continue;
                    }
                    if (a.size() > 1) {
                        errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_INCREMENT_NOT_SINGLE_VALUED.get(name));
                        continue;
                    }
                    if (rdn != null && rdn.hasAttribute(name)) {
                        final String msg = LDAPMessages.ERR_ENTRY_APPLY_MODS_TARGETS_RDN.get(entry.getDN());
                        if (!errors.contains(msg)) {
                            errors.add(msg);
                        }
                        if (resultCode == null) {
                            resultCode = ResultCode.NOT_ALLOWED_ON_RDN;
                            continue;
                        }
                        continue;
                    }
                    else {
                        BigInteger currentValue;
                        try {
                            currentValue = new BigInteger(a.getValue());
                        }
                        catch (final NumberFormatException nfe) {
                            Debug.debugException(nfe);
                            errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_INCREMENT_ENTRY_VALUE_NOT_INTEGER.get(name, a.getValue()));
                            continue;
                        }
                        if (values.length == 0) {
                            errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_INCREMENT_NO_MOD_VALUES.get(name));
                            continue;
                        }
                        if (values.length > 1) {
                            errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_INCREMENT_MULTIPLE_MOD_VALUES.get(name));
                            continue;
                        }
                        final String incrementValueStr = m.getValues()[0];
                        BigInteger incrementValue;
                        try {
                            incrementValue = new BigInteger(incrementValueStr);
                        }
                        catch (final NumberFormatException nfe2) {
                            Debug.debugException(nfe2);
                            errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_INCREMENT_MOD_VALUE_NOT_INTEGER.get(name, incrementValueStr));
                            continue;
                        }
                        final BigInteger newValue = currentValue.add(incrementValue);
                        e.setAttribute(name, newValue.toString());
                        continue;
                    }
                    break;
                }
                default: {
                    errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_UNKNOWN_TYPE.get(String.valueOf(m.getModificationType())));
                    continue;
                }
            }
        }
        if (rdn != null) {
            final String[] rdnAttrs = rdn.getAttributeNames();
            final byte[][] rdnValues = rdn.getByteArrayAttributeValues();
            int j = 0;
            while (j < rdnAttrs.length) {
                if (!e.hasAttributeValue(rdnAttrs[j], rdnValues[j])) {
                    errors.add(LDAPMessages.ERR_ENTRY_APPLY_MODS_TARGETS_RDN.get(entry.getDN()));
                    if (resultCode == null) {
                        resultCode = ResultCode.NOT_ALLOWED_ON_RDN;
                        break;
                    }
                    break;
                }
                else {
                    ++j;
                }
            }
        }
        if (errors.isEmpty()) {
            return e;
        }
        if (resultCode == null) {
            resultCode = ResultCode.CONSTRAINT_VIOLATION;
        }
        throw new LDAPException(resultCode, LDAPMessages.ERR_ENTRY_APPLY_MODS_FAILURE.get(e.getDN(), StaticUtils.concatenateStrings(errors)));
    }
    
    public static Entry applyModifyDN(final Entry entry, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        return applyModifyDN(entry, newRDN, deleteOldRDN, null);
    }
    
    public static Entry applyModifyDN(final Entry entry, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) throws LDAPException {
        Validator.ensureNotNull(entry);
        Validator.ensureNotNull(newRDN);
        final DN parsedOldDN = entry.getParsedDN();
        final RDN parsedOldRDN = parsedOldDN.getRDN();
        final DN parsedOldSuperiorDN = parsedOldDN.getParent();
        final RDN parsedNewRDN = new RDN(newRDN);
        DN parsedNewSuperiorDN;
        if (newSuperiorDN == null) {
            parsedNewSuperiorDN = parsedOldSuperiorDN;
        }
        else {
            parsedNewSuperiorDN = new DN(newSuperiorDN);
        }
        final Entry newEntry = entry.duplicate();
        if (parsedNewSuperiorDN == null) {
            newEntry.setDN(new DN(new RDN[] { parsedNewRDN }));
        }
        else {
            newEntry.setDN(new DN(parsedNewRDN, parsedNewSuperiorDN));
        }
        if (deleteOldRDN && parsedOldRDN != null) {
            final String[] oldNames = parsedOldRDN.getAttributeNames();
            final byte[][] oldValues = parsedOldRDN.getByteArrayAttributeValues();
            for (int i = 0; i < oldNames.length; ++i) {
                if (!parsedNewRDN.hasAttributeValue(oldNames[i], oldValues[i])) {
                    newEntry.removeAttributeValue(oldNames[i], oldValues[i]);
                }
            }
        }
        final String[] newNames = parsedNewRDN.getAttributeNames();
        final byte[][] newValues = parsedNewRDN.getByteArrayAttributeValues();
        for (int i = 0; i < newNames.length; ++i) {
            if (parsedOldRDN == null || !parsedOldRDN.hasAttributeValue(newNames[i], newValues[i])) {
                newEntry.addAttribute(newNames[i], newValues[i]);
            }
        }
        return newEntry;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        try {
            hashCode += this.getParsedDN().hashCode();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            hashCode += this.dn.hashCode();
        }
        for (final Attribute a : this.attributes.values()) {
            hashCode += a.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Entry)) {
            return false;
        }
        final Entry e = (Entry)o;
        try {
            final DN thisDN = this.getParsedDN();
            final DN thatDN = e.getParsedDN();
            if (!thisDN.equals(thatDN)) {
                return false;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (!this.dn.equals(e.dn)) {
                return false;
            }
        }
        if (this.attributes.size() != e.attributes.size()) {
            return false;
        }
        for (final Attribute a : this.attributes.values()) {
            if (!e.hasAttribute(a)) {
                return false;
            }
        }
        return true;
    }
    
    public Entry duplicate() {
        return new Entry(this.dn, this.schema, this.attributes.values());
    }
    
    @Override
    public final String[] toLDIF() {
        return this.toLDIF(0);
    }
    
    @Override
    public final String[] toLDIF(final int wrapColumn) {
        List<String> ldifLines = new ArrayList<String>(2 * this.attributes.size());
        encodeNameAndValue("dn", new ASN1OctetString(this.dn), ldifLines);
        for (final Attribute a : this.attributes.values()) {
            final String name = a.getName();
            if (a.hasValue()) {
                for (final ASN1OctetString value : a.getRawValues()) {
                    encodeNameAndValue(name, value, ldifLines);
                }
            }
            else {
                encodeNameAndValue(name, Entry.EMPTY_OCTET_STRING, ldifLines);
            }
        }
        if (wrapColumn > 2) {
            ldifLines = LDIFWriter.wrapLines(wrapColumn, ldifLines);
        }
        final String[] lineArray = new String[ldifLines.size()];
        ldifLines.toArray(lineArray);
        return lineArray;
    }
    
    private static void encodeNameAndValue(final String name, final ASN1OctetString value, final List<String> lines) {
        final String line = LDIFWriter.encodeNameAndValue(name, value);
        if (LDIFWriter.commentAboutBase64EncodedValues() && line.startsWith(name + "::")) {
            final StringTokenizer tokenizer = new StringTokenizer(line, "\r\n");
            while (tokenizer.hasMoreTokens()) {
                lines.add(tokenizer.nextToken());
            }
        }
        else {
            lines.add(line);
        }
    }
    
    @Override
    public final void toLDIF(final ByteStringBuffer buffer) {
        this.toLDIF(buffer, 0);
    }
    
    @Override
    public final void toLDIF(final ByteStringBuffer buffer, final int wrapColumn) {
        LDIFWriter.encodeNameAndValue("dn", new ASN1OctetString(this.dn), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL_BYTES);
        for (final Attribute a : this.attributes.values()) {
            final String name = a.getName();
            if (a.hasValue()) {
                for (final ASN1OctetString value : a.getRawValues()) {
                    LDIFWriter.encodeNameAndValue(name, value, buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL_BYTES);
                }
            }
            else {
                LDIFWriter.encodeNameAndValue(name, Entry.EMPTY_OCTET_STRING, buffer, wrapColumn);
                buffer.append(StaticUtils.EOL_BYTES);
            }
        }
    }
    
    @Override
    public final String toLDIFString() {
        final StringBuilder buffer = new StringBuilder();
        this.toLDIFString(buffer, 0);
        return buffer.toString();
    }
    
    @Override
    public final String toLDIFString(final int wrapColumn) {
        final StringBuilder buffer = new StringBuilder();
        this.toLDIFString(buffer, wrapColumn);
        return buffer.toString();
    }
    
    @Override
    public final void toLDIFString(final StringBuilder buffer) {
        this.toLDIFString(buffer, 0);
    }
    
    @Override
    public final void toLDIFString(final StringBuilder buffer, final int wrapColumn) {
        LDIFWriter.encodeNameAndValue("dn", new ASN1OctetString(this.dn), buffer, wrapColumn);
        buffer.append(StaticUtils.EOL);
        for (final Attribute a : this.attributes.values()) {
            final String name = a.getName();
            if (a.hasValue()) {
                for (final ASN1OctetString value : a.getRawValues()) {
                    LDIFWriter.encodeNameAndValue(name, value, buffer, wrapColumn);
                    buffer.append(StaticUtils.EOL);
                }
            }
            else {
                LDIFWriter.encodeNameAndValue(name, Entry.EMPTY_OCTET_STRING, buffer, wrapColumn);
                buffer.append(StaticUtils.EOL);
            }
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("Entry(dn='");
        buffer.append(this.dn);
        buffer.append("', attributes={");
        final Iterator<Attribute> iterator = this.attributes.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
    
    static {
        EMPTY_OCTET_STRING = new ASN1OctetString();
    }
}
