package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import java.util.Arrays;
import com.unboundid.ldap.sdk.RDN;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MoveSubtreeTransformation implements EntryTransformation, LDIFChangeRecordTransformation
{
    private final DN sourceDN;
    private final List<RDN> targetRDNs;
    
    public MoveSubtreeTransformation(final DN sourceDN, final DN targetDN) {
        this.sourceDN = sourceDN;
        this.targetRDNs = Arrays.asList(targetDN.getRDNs());
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        final Collection<Attribute> originalAttributes = e.getAttributes();
        final ArrayList<Attribute> newAttributes = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a : originalAttributes) {
            final String[] originalValues = a.getValues();
            final String[] newValues = new String[originalValues.length];
            for (int i = 0; i < originalValues.length; ++i) {
                newValues[i] = this.processString(originalValues[i]);
            }
            newAttributes.add(new Attribute(a.getName(), newValues));
        }
        return new Entry(this.processString(e.getDN()), newAttributes);
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
            return new LDIFDeleteChangeRecord(this.processString(r.getDN()), r.getControls());
        }
        if (r instanceof LDIFModifyChangeRecord) {
            final LDIFModifyChangeRecord modRecord = (LDIFModifyChangeRecord)r;
            final Modification[] originalMods = modRecord.getModifications();
            final Modification[] newMods = new Modification[originalMods.length];
            for (int i = 0; i < originalMods.length; ++i) {
                final Modification m = originalMods[i];
                if (m.hasValue()) {
                    final String[] originalValues = m.getValues();
                    final String[] newValues = new String[originalValues.length];
                    for (int j = 0; j < originalValues.length; ++j) {
                        newValues[j] = this.processString(originalValues[j]);
                    }
                    newMods[i] = new Modification(m.getModificationType(), m.getAttributeName(), newValues);
                }
                else {
                    newMods[i] = originalMods[i];
                }
            }
            return new LDIFModifyChangeRecord(this.processString(modRecord.getDN()), newMods, modRecord.getControls());
        }
        if (r instanceof LDIFModifyDNChangeRecord) {
            final LDIFModifyDNChangeRecord modDNRecord = (LDIFModifyDNChangeRecord)r;
            return new LDIFModifyDNChangeRecord(this.processString(modDNRecord.getDN()), modDNRecord.getNewRDN(), modDNRecord.deleteOldRDN(), this.processString(modDNRecord.getNewSuperiorDN()), modDNRecord.getControls());
        }
        return r;
    }
    
    String processString(final String s) {
        if (s == null) {
            return null;
        }
        try {
            final DN dn = new DN(s);
            if (!dn.isDescendantOf(this.sourceDN, true)) {
                return s;
            }
            final RDN[] originalRDNs = dn.getRDNs();
            final RDN[] sourceRDNs = this.sourceDN.getRDNs();
            final ArrayList<RDN> newRDNs = new ArrayList<RDN>(2 * originalRDNs.length);
            for (int numComponentsToKeep = originalRDNs.length - sourceRDNs.length, i = 0; i < numComponentsToKeep; ++i) {
                newRDNs.add(originalRDNs[i]);
            }
            newRDNs.addAll(this.targetRDNs);
            return new DN(newRDNs).toString();
        }
        catch (final Exception e) {
            return s;
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
