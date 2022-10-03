package com.unboundid.ldap.sdk.transformations;

import java.util.List;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
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
public final class ExcludeAttributeTransformation implements EntryTransformation, LDIFChangeRecordTransformation
{
    private final Schema schema;
    private final Set<String> attributes;
    
    public ExcludeAttributeTransformation(final Schema schema, final String... attributes) {
        this(schema, StaticUtils.toList(attributes));
    }
    
    public ExcludeAttributeTransformation(final Schema schema, final Collection<String> attributes) {
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
        boolean hasAttributeToRemove = false;
        final Collection<Attribute> originalAttributes = e.getAttributes();
        for (final Attribute a : originalAttributes) {
            if (this.attributes.contains(StaticUtils.toLowerCase(a.getBaseName()))) {
                hasAttributeToRemove = true;
                break;
            }
        }
        if (!hasAttributeToRemove) {
            return e;
        }
        final ArrayList<Attribute> attributesToKeep = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a2 : originalAttributes) {
            if (!this.attributes.contains(StaticUtils.toLowerCase(a2.getBaseName()))) {
                attributesToKeep.add(a2);
            }
        }
        return new Entry(e.getDN(), this.schema, attributesToKeep);
    }
    
    @Override
    public LDIFChangeRecord transformChangeRecord(final LDIFChangeRecord r) {
        if (r == null) {
            return null;
        }
        if (r instanceof LDIFAddChangeRecord) {
            final LDIFAddChangeRecord addRecord = (LDIFAddChangeRecord)r;
            final Entry updatedEntry = this.transformEntry(addRecord.getEntryToAdd());
            if (updatedEntry.getAttributes().isEmpty()) {
                return null;
            }
            return new LDIFAddChangeRecord(updatedEntry, addRecord.getControls());
        }
        else {
            if (!(r instanceof LDIFModifyChangeRecord)) {
                return r;
            }
            final LDIFModifyChangeRecord modifyRecord = (LDIFModifyChangeRecord)r;
            final Modification[] originalMods = modifyRecord.getModifications();
            final ArrayList<Modification> modsToKeep = new ArrayList<Modification>(originalMods.length);
            for (final Modification m : originalMods) {
                final String attrName = StaticUtils.toLowerCase(Attribute.getBaseName(m.getAttributeName()));
                if (!this.attributes.contains(attrName)) {
                    modsToKeep.add(m);
                }
            }
            if (modsToKeep.isEmpty()) {
                return null;
            }
            return new LDIFModifyChangeRecord(modifyRecord.getDN(), modsToKeep, modifyRecord.getControls());
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
