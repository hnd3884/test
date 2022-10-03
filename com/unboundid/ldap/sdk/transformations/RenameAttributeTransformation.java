package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.DistinguishedNameMatchingRule;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.HashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Set;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RenameAttributeTransformation implements EntryTransformation, LDIFChangeRecordTransformation
{
    private final boolean renameInDNs;
    private final Schema schema;
    private final Set<String> baseSourceNames;
    private final String baseTargetName;
    
    public RenameAttributeTransformation(final Schema schema, final String sourceAttribute, final String targetAttribute, final boolean renameInDNs) {
        this.renameInDNs = renameInDNs;
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
        final HashSet<String> sourceNames = new HashSet<String>(StaticUtils.computeMapCapacity(5));
        final String baseSourceName = StaticUtils.toLowerCase(Attribute.getBaseName(sourceAttribute));
        sourceNames.add(baseSourceName);
        if (s != null) {
            final AttributeTypeDefinition at = s.getAttributeType(baseSourceName);
            if (at != null) {
                sourceNames.add(StaticUtils.toLowerCase(at.getOID()));
                for (final String name : at.getNames()) {
                    sourceNames.add(StaticUtils.toLowerCase(name));
                }
            }
        }
        this.baseSourceNames = Collections.unmodifiableSet((Set<? extends String>)sourceNames);
        this.baseTargetName = Attribute.getBaseName(targetAttribute);
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        String newDN;
        if (this.renameInDNs) {
            newDN = this.replaceDN(e.getDN());
        }
        else {
            newDN = e.getDN();
        }
        final Collection<Attribute> originalAttributes = e.getAttributes();
        final ArrayList<Attribute> newAttributes = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a : originalAttributes) {
            final String baseName = StaticUtils.toLowerCase(a.getBaseName());
            String newName;
            if (this.baseSourceNames.contains(baseName)) {
                if (a.hasOptions()) {
                    final StringBuilder buffer = new StringBuilder();
                    buffer.append(this.baseTargetName);
                    for (final String option : a.getOptions()) {
                        buffer.append(';');
                        buffer.append(option);
                    }
                    newName = buffer.toString();
                }
                else {
                    newName = this.baseTargetName;
                }
            }
            else {
                newName = a.getName();
            }
            String[] newValues;
            if (this.renameInDNs && this.schema != null && MatchingRule.selectEqualityMatchingRule(baseName, this.schema) instanceof DistinguishedNameMatchingRule) {
                final String[] originalValues = a.getValues();
                newValues = new String[originalValues.length];
                for (int i = 0; i < originalValues.length; ++i) {
                    newValues[i] = this.replaceDN(originalValues[i]);
                }
            }
            else {
                newValues = a.getValues();
            }
            newAttributes.add(new Attribute(newName, this.schema, newValues));
        }
        return new Entry(newDN, newAttributes);
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
            if (this.renameInDNs) {
                return new LDIFDeleteChangeRecord(this.replaceDN(r.getDN()), r.getControls());
            }
            return r;
        }
        else {
            if (r instanceof LDIFModifyChangeRecord) {
                final LDIFModifyChangeRecord modRecord = (LDIFModifyChangeRecord)r;
                String newDN;
                if (this.renameInDNs) {
                    newDN = this.replaceDN(modRecord.getDN());
                }
                else {
                    newDN = modRecord.getDN();
                }
                final Modification[] originalMods = modRecord.getModifications();
                final Modification[] newMods = new Modification[originalMods.length];
                for (int i = 0; i < originalMods.length; ++i) {
                    final Modification m = originalMods[i];
                    final String baseName = StaticUtils.toLowerCase(Attribute.getBaseName(m.getAttributeName()));
                    String newName;
                    if (this.baseSourceNames.contains(baseName)) {
                        final Set<String> options = Attribute.getOptions(m.getAttributeName());
                        if (options.isEmpty()) {
                            newName = this.baseTargetName;
                        }
                        else {
                            final StringBuilder buffer = new StringBuilder();
                            buffer.append(this.baseTargetName);
                            for (final String option : options) {
                                buffer.append(';');
                                buffer.append(option);
                            }
                            newName = buffer.toString();
                        }
                    }
                    else {
                        newName = m.getAttributeName();
                    }
                    String[] newValues;
                    if (this.renameInDNs && this.schema != null && MatchingRule.selectEqualityMatchingRule(baseName, this.schema) instanceof DistinguishedNameMatchingRule) {
                        final String[] originalValues = m.getValues();
                        newValues = new String[originalValues.length];
                        for (int j = 0; j < originalValues.length; ++j) {
                            newValues[j] = this.replaceDN(originalValues[j]);
                        }
                    }
                    else {
                        newValues = m.getValues();
                    }
                    newMods[i] = new Modification(m.getModificationType(), newName, newValues);
                }
                return new LDIFModifyChangeRecord(newDN, newMods, modRecord.getControls());
            }
            if (!(r instanceof LDIFModifyDNChangeRecord)) {
                return r;
            }
            if (this.renameInDNs) {
                final LDIFModifyDNChangeRecord modDNRecord = (LDIFModifyDNChangeRecord)r;
                return new LDIFModifyDNChangeRecord(this.replaceDN(modDNRecord.getDN()), this.replaceDN(modDNRecord.getNewRDN()), modDNRecord.deleteOldRDN(), this.replaceDN(modDNRecord.getNewSuperiorDN()), modDNRecord.getControls());
            }
            return r;
        }
    }
    
    private String replaceDN(final String dn) {
        try {
            final DN parsedDN = new DN(dn);
            final RDN[] originalRDNs = parsedDN.getRDNs();
            final RDN[] newRDNs = new RDN[originalRDNs.length];
            for (int i = 0; i < originalRDNs.length; ++i) {
                final String[] originalNames = originalRDNs[i].getAttributeNames();
                final String[] newNames = new String[originalNames.length];
                for (int j = 0; j < originalNames.length; ++j) {
                    if (this.baseSourceNames.contains(StaticUtils.toLowerCase(originalNames[j]))) {
                        newNames[j] = this.baseTargetName;
                    }
                    else {
                        newNames[j] = originalNames[j];
                    }
                }
                newRDNs[i] = new RDN(newNames, originalRDNs[i].getByteArrayAttributeValues());
            }
            return new DN(newRDNs).toString();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return dn;
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
