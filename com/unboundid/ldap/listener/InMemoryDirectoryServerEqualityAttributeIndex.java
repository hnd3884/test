package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import java.util.Set;
import com.unboundid.util.InternalUseOnly;
import java.util.Iterator;
import java.util.Collections;
import java.util.SortedSet;
import java.util.HashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.DN;
import java.util.TreeSet;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Map;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class InMemoryDirectoryServerEqualityAttributeIndex
{
    private final AttributeTypeDefinition attributeType;
    private final Map<ASN1OctetString, TreeSet<DN>> indexMap;
    private final MatchingRule matchingRule;
    private final Schema schema;
    
    InMemoryDirectoryServerEqualityAttributeIndex(final String attributeType, final Schema schema) throws LDAPException {
        this.schema = schema;
        if (schema == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_DS_EQ_INDEX_NO_SCHEMA.get(attributeType));
        }
        this.attributeType = schema.getAttributeType(attributeType);
        if (this.attributeType == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_DS_EQ_INDEX_UNDEFINED_ATTRIBUTE_TYPE.get(attributeType));
        }
        this.matchingRule = MatchingRule.selectEqualityMatchingRule(attributeType, schema);
        this.indexMap = new HashMap<ASN1OctetString, TreeSet<DN>>(StaticUtils.computeMapCapacity(100));
    }
    
    AttributeTypeDefinition getAttributeType() {
        return this.attributeType;
    }
    
    synchronized void clear() {
        this.indexMap.clear();
    }
    
    @InternalUseOnly
    synchronized Map<ASN1OctetString, TreeSet<DN>> copyMap() {
        final HashMap<ASN1OctetString, TreeSet<DN>> m = new HashMap<ASN1OctetString, TreeSet<DN>>(StaticUtils.computeMapCapacity(this.indexMap.size()));
        for (final Map.Entry<ASN1OctetString, TreeSet<DN>> e : this.indexMap.entrySet()) {
            m.put(e.getKey(), new TreeSet<DN>(e.getValue()));
        }
        return Collections.unmodifiableMap((Map<? extends ASN1OctetString, ? extends TreeSet<DN>>)m);
    }
    
    synchronized Set<DN> getMatchingEntries(final ASN1OctetString value) throws LDAPException {
        final TreeSet<DN> dnSet = this.indexMap.get(this.matchingRule.normalize(value));
        if (dnSet == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet((Set<? extends DN>)dnSet);
    }
    
    synchronized void processAdd(final Entry entry) throws LDAPException {
        final Attribute a = entry.getAttribute(this.attributeType.getNameOrOID(), this.schema);
        if (a != null) {
            final DN dn = entry.getParsedDN();
            final ASN1OctetString[] rawValues = a.getRawValues();
            final ASN1OctetString[] normalizedValues = new ASN1OctetString[rawValues.length];
            for (int i = 0; i < rawValues.length; ++i) {
                normalizedValues[i] = this.matchingRule.normalize(rawValues[i]);
            }
            for (final ASN1OctetString v : normalizedValues) {
                TreeSet<DN> dnSet = this.indexMap.get(v);
                if (dnSet == null) {
                    dnSet = new TreeSet<DN>();
                    this.indexMap.put(v, dnSet);
                }
                dnSet.add(dn);
            }
        }
    }
    
    synchronized void processDelete(final Entry entry) throws LDAPException {
        final Attribute a = entry.getAttribute(this.attributeType.getNameOrOID(), this.schema);
        if (a != null) {
            final DN dn = entry.getParsedDN();
            final ASN1OctetString[] rawValues = a.getRawValues();
            final ASN1OctetString[] normalizedValues = new ASN1OctetString[rawValues.length];
            for (int i = 0; i < rawValues.length; ++i) {
                normalizedValues[i] = this.matchingRule.normalize(rawValues[i]);
            }
            for (final ASN1OctetString v : normalizedValues) {
                final TreeSet<DN> dnSet = this.indexMap.get(v);
                if (dnSet != null) {
                    dnSet.remove(dn);
                    if (dnSet.isEmpty()) {
                        this.indexMap.remove(v);
                    }
                }
            }
        }
    }
}
