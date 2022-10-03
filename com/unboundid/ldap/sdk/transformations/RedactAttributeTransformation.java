package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.DistinguishedNameMatchingRule;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.HashSet;
import com.unboundid.util.Debug;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RedactAttributeTransformation implements EntryTransformation, LDIFChangeRecordTransformation
{
    private final boolean preserveValueCount;
    private final boolean redactDNAttributes;
    private final Schema schema;
    private final Set<String> attributes;
    
    public RedactAttributeTransformation(final Schema schema, final boolean redactDNAttributes, final boolean preserveValueCount, final String... attributes) {
        this(schema, redactDNAttributes, preserveValueCount, StaticUtils.toList(attributes));
    }
    
    public RedactAttributeTransformation(final Schema schema, final boolean redactDNAttributes, final boolean preserveValueCount, final Collection<String> attributes) {
        this.redactDNAttributes = redactDNAttributes;
        this.preserveValueCount = preserveValueCount;
        Schema s = schema;
        if (s == null) {
            try {
                s = Schema.getDefaultStandardSchema();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.schema = s;
        final HashSet<String> attrNames = new HashSet<String>(StaticUtils.computeMapCapacity(3 * attributes.size()));
        for (final String attrName : attributes) {
            final String baseName = Attribute.getBaseName(StaticUtils.toLowerCase(attrName));
            attrNames.add(baseName);
            if (s != null) {
                final AttributeTypeDefinition at = s.getAttributeType(baseName);
                if (at == null) {
                    continue;
                }
                attrNames.add(StaticUtils.toLowerCase(at.getOID()));
                for (final String name : at.getNames()) {
                    attrNames.add(StaticUtils.toLowerCase(name));
                }
            }
        }
        this.attributes = Collections.unmodifiableSet((Set<? extends String>)attrNames);
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        String newDN;
        if (this.redactDNAttributes) {
            newDN = this.redactDN(e.getDN());
        }
        else {
            newDN = e.getDN();
        }
        final Collection<Attribute> originalAttributes = e.getAttributes();
        final ArrayList<Attribute> newAttributes = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a : originalAttributes) {
            final String baseName = StaticUtils.toLowerCase(a.getBaseName());
            if (this.attributes.contains(baseName)) {
                if (this.preserveValueCount && a.size() > 1) {
                    final ASN1OctetString[] values = new ASN1OctetString[a.size()];
                    for (int i = 0; i < values.length; ++i) {
                        values[i] = new ASN1OctetString("***REDACTED" + (i + 1) + "***");
                    }
                    newAttributes.add(new Attribute(a.getName(), values));
                }
                else {
                    newAttributes.add(new Attribute(a.getName(), "***REDACTED***"));
                }
            }
            else if (this.redactDNAttributes && this.schema != null && MatchingRule.selectEqualityMatchingRule(baseName, this.schema) instanceof DistinguishedNameMatchingRule) {
                final String[] originalValues = a.getValues();
                final String[] newValues = new String[originalValues.length];
                for (int j = 0; j < originalValues.length; ++j) {
                    newValues[j] = this.redactDN(originalValues[j]);
                }
                newAttributes.add(new Attribute(a.getName(), this.schema, newValues));
            }
            else {
                newAttributes.add(a);
            }
        }
        return new Entry(newDN, this.schema, newAttributes);
    }
    
    private String redactDN(final String dn) {
        if (dn == null) {
            return null;
        }
        try {
            boolean changeApplied = false;
            final RDN[] originalRDNs = new DN(dn).getRDNs();
            final RDN[] newRDNs = new RDN[originalRDNs.length];
            for (int i = 0; i < originalRDNs.length; ++i) {
                final String[] names = originalRDNs[i].getAttributeNames();
                final String[] originalValues = originalRDNs[i].getAttributeValues();
                final String[] newValues = new String[originalValues.length];
                for (int j = 0; j < names.length; ++j) {
                    if (this.attributes.contains(StaticUtils.toLowerCase(names[j]))) {
                        changeApplied = true;
                        newValues[j] = "***REDACTED***";
                    }
                    else {
                        newValues[j] = originalValues[j];
                    }
                }
                newRDNs[i] = new RDN(names, newValues, this.schema);
            }
            if (changeApplied) {
                return new DN(newRDNs).toString();
            }
            return dn;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return dn;
        }
    }
    
    @Override
    public LDIFChangeRecord transformChangeRecord(final LDIFChangeRecord r) {
        if (r == null) {
            return null;
        }
        if (r instanceof LDIFAddChangeRecord) {
            final LDIFAddChangeRecord addRecord = (LDIFAddChangeRecord)r;
            return new LDIFAddChangeRecord(this.transformEntry(addRecord.getEntryToAdd()), addRecord.getControls());
        }
        if (r instanceof LDIFDeleteChangeRecord) {
            if (this.redactDNAttributes) {
                final LDIFDeleteChangeRecord deleteRecord = (LDIFDeleteChangeRecord)r;
                return new LDIFDeleteChangeRecord(this.redactDN(deleteRecord.getDN()), deleteRecord.getControls());
            }
            return r;
        }
        else {
            if (r instanceof LDIFModifyChangeRecord) {
                final LDIFModifyChangeRecord modifyRecord = (LDIFModifyChangeRecord)r;
                String newDN;
                if (this.redactDNAttributes) {
                    newDN = this.redactDN(modifyRecord.getDN());
                }
                else {
                    newDN = modifyRecord.getDN();
                }
                final Modification[] originalMods = modifyRecord.getModifications();
                final Modification[] newMods = new Modification[originalMods.length];
                for (int i = 0; i < originalMods.length; ++i) {
                    final Modification m = originalMods[i];
                    if (!m.hasValue()) {
                        newMods[i] = m;
                    }
                    else {
                        final String attrName = StaticUtils.toLowerCase(Attribute.getBaseName(m.getAttributeName()));
                        if (!this.attributes.contains(attrName)) {
                            if (this.redactDNAttributes && this.schema != null && MatchingRule.selectEqualityMatchingRule(attrName, this.schema) instanceof DistinguishedNameMatchingRule) {
                                final String[] originalValues = m.getValues();
                                final String[] newValues = new String[originalValues.length];
                                for (int j = 0; j < originalValues.length; ++j) {
                                    newValues[j] = this.redactDN(originalValues[j]);
                                }
                                newMods[i] = new Modification(m.getModificationType(), m.getAttributeName(), newValues);
                            }
                            else {
                                newMods[i] = m;
                            }
                        }
                        else {
                            final ASN1OctetString[] originalValues2 = m.getRawValues();
                            if (this.preserveValueCount && originalValues2.length > 1) {
                                final ASN1OctetString[] newValues2 = new ASN1OctetString[originalValues2.length];
                                for (int j = 0; j < originalValues2.length; ++j) {
                                    newValues2[j] = new ASN1OctetString("***REDACTED" + (j + 1) + "***");
                                }
                                newMods[i] = new Modification(m.getModificationType(), m.getAttributeName(), newValues2);
                            }
                            else {
                                newMods[i] = new Modification(m.getModificationType(), m.getAttributeName(), "***REDACTED***");
                            }
                        }
                    }
                }
                return new LDIFModifyChangeRecord(newDN, newMods, modifyRecord.getControls());
            }
            if (!(r instanceof LDIFModifyDNChangeRecord)) {
                return r;
            }
            if (this.redactDNAttributes) {
                final LDIFModifyDNChangeRecord modDNRecord = (LDIFModifyDNChangeRecord)r;
                return new LDIFModifyDNChangeRecord(this.redactDN(modDNRecord.getDN()), this.redactDN(modDNRecord.getNewRDN()), modDNRecord.deleteOldRDN(), this.redactDN(modDNRecord.getNewSuperiorDN()), modDNRecord.getControls());
            }
            return r;
        }
    }
    
    @Override
    public Entry translate(final Entry original, final long firstLineNumber) {
        return this.transformEntry(original);
    }
    
    @Override
    public LDIFChangeRecord translate(final LDIFChangeRecord original, final long firstLineNumber) {
        return this.transformChangeRecord(original);
    }
    
    @Override
    public Entry translateEntryToWrite(final Entry original) {
        return this.transformEntry(original);
    }
    
    @Override
    public LDIFChangeRecord translateChangeRecordToWrite(final LDIFChangeRecord original) {
        return this.transformChangeRecord(original);
    }
}
