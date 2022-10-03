package com.unboundid.ldap.sdk.transformations;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import java.util.Set;
import com.unboundid.util.ObjectPair;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FlattenSubtreeTransformation implements EntryTransformation, Serializable
{
    private static final long serialVersionUID = -5500436195237056110L;
    private final boolean addOmittedRDNAttributesToEntry;
    private final boolean addOmittedRDNAttributesToRDN;
    private final DN flattenBaseDN;
    private final Filter excludeFilter;
    private final RDN[] flattenBaseRDNs;
    private final Schema schema;
    
    public FlattenSubtreeTransformation(final Schema schema, final DN flattenBaseDN, final boolean addOmittedRDNAttributesToEntry, final boolean addOmittedRDNAttributesToRDN, final Filter excludeFilter) {
        this.flattenBaseDN = flattenBaseDN;
        this.addOmittedRDNAttributesToEntry = addOmittedRDNAttributesToEntry;
        this.addOmittedRDNAttributesToRDN = addOmittedRDNAttributesToRDN;
        this.excludeFilter = excludeFilter;
        this.flattenBaseRDNs = flattenBaseDN.getRDNs();
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
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        DN newDN = null;
        LinkedHashSet<ObjectPair<String, String>> omittedRDNValues = null;
        try {
            final DN dn = e.getParsedDN();
            if (dn.isDescendantOf(this.flattenBaseDN, false)) {
                try {
                    if (this.excludeFilter != null && this.excludeFilter.matchesEntry(e)) {
                        return null;
                    }
                }
                catch (final Exception ex) {
                    Debug.debugException(ex);
                }
                if (this.addOmittedRDNAttributesToEntry || this.addOmittedRDNAttributesToRDN) {
                    omittedRDNValues = new LinkedHashSet<ObjectPair<String, String>>(StaticUtils.computeMapCapacity(5));
                }
                newDN = this.transformDN(dn, omittedRDNValues);
            }
        }
        catch (final Exception ex2) {
            Debug.debugException(ex2);
            return e;
        }
        final Collection<Attribute> originalAttributes = e.getAttributes();
        final ArrayList<Attribute> newAttributes = new ArrayList<Attribute>(originalAttributes.size());
        LinkedHashSet<ObjectPair<String, String>> tempOmittedRDNValues;
        if (this.addOmittedRDNAttributesToRDN) {
            tempOmittedRDNValues = new LinkedHashSet<ObjectPair<String, String>>(StaticUtils.computeMapCapacity(5));
        }
        else {
            tempOmittedRDNValues = null;
        }
        for (final Attribute a : originalAttributes) {
            newAttributes.add(this.transformAttribute(a, tempOmittedRDNValues));
        }
        Entry newEntry;
        if (newDN == null) {
            newEntry = new Entry(e.getDN(), this.schema, newAttributes);
        }
        else {
            newEntry = new Entry(newDN, this.schema, newAttributes);
        }
        if (this.addOmittedRDNAttributesToEntry && omittedRDNValues != null) {
            for (final ObjectPair<String, String> p : omittedRDNValues) {
                newEntry.addAttribute(new Attribute(p.getFirst(), this.schema, new String[] { p.getSecond() }));
            }
        }
        return newEntry;
    }
    
    private DN transformDN(final DN dn, final Set<ObjectPair<String, String>> omittedRDNValues) {
        final RDN[] originalRDNs = dn.getRDNs();
        final int numRDNsToOmit = originalRDNs.length - this.flattenBaseRDNs.length - 1;
        if (numRDNsToOmit == 0) {
            return dn;
        }
        final RDN[] newRDNs = new RDN[this.flattenBaseRDNs.length + 1];
        System.arraycopy(this.flattenBaseRDNs, 0, newRDNs, 1, this.flattenBaseRDNs.length);
        if (omittedRDNValues == null) {
            newRDNs[0] = originalRDNs[0];
        }
        else {
            for (int i = 1; i <= numRDNsToOmit; ++i) {
                final String[] names = originalRDNs[i].getAttributeNames();
                final String[] values = originalRDNs[i].getAttributeValues();
                for (int j = 0; j < names.length; ++j) {
                    omittedRDNValues.add(new ObjectPair<String, String>(names[j], values[j]));
                }
            }
            final String[] origNames = originalRDNs[0].getAttributeNames();
            final String[] origValues = originalRDNs[0].getAttributeValues();
            for (int k = 0; k < origNames.length; ++k) {
                omittedRDNValues.remove(new ObjectPair(origNames[k], origValues[k]));
            }
            if (this.addOmittedRDNAttributesToRDN) {
                final String[] originalRDNNames = originalRDNs[0].getAttributeNames();
                final String[] originalRDNValues = originalRDNs[0].getAttributeValues();
                final String[] newRDNNames = new String[originalRDNNames.length + omittedRDNValues.size()];
                final String[] newRDNValues = new String[newRDNNames.length];
                int l = 0;
                for (int m = 0; m < originalRDNNames.length; ++m) {
                    newRDNNames[l] = originalRDNNames[l];
                    newRDNValues[l] = originalRDNValues[l];
                    ++l;
                }
                for (final ObjectPair<String, String> p : omittedRDNValues) {
                    newRDNNames[l] = p.getFirst();
                    newRDNValues[l] = p.getSecond();
                    ++l;
                }
                newRDNs[0] = new RDN(newRDNNames, newRDNValues, this.schema);
            }
            else {
                newRDNs[0] = originalRDNs[0];
            }
        }
        return new DN(newRDNs);
    }
    
    private Attribute transformAttribute(final Attribute a, final Set<ObjectPair<String, String>> omittedRDNValues) {
        boolean hasTransformableDN = false;
        final String[] arr$;
        final String[] values = arr$ = a.getValues();
        for (final String value : arr$) {
            try {
                final DN dn = new DN(value);
                if (dn.isDescendantOf(this.flattenBaseDN, false)) {
                    hasTransformableDN = true;
                    break;
                }
            }
            catch (final Exception ex) {}
        }
        if (!hasTransformableDN) {
            return a;
        }
        final String[] newValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                final DN dn2 = new DN(values[i]);
                if (dn2.isDescendantOf(this.flattenBaseDN, false)) {
                    if (omittedRDNValues != null) {
                        omittedRDNValues.clear();
                    }
                    newValues[i] = this.transformDN(dn2, omittedRDNValues).toString();
                }
                else {
                    newValues[i] = values[i];
                }
            }
            catch (final Exception e) {
                newValues[i] = values[i];
            }
        }
        return new Attribute(a.getName(), this.schema, newValues);
    }
    
    @Override
    public Entry translate(final Entry original, final long firstLineNumber) {
        return this.transformEntry(original);
    }
    
    @Override
    public Entry translateEntryToWrite(final Entry original) {
        return this.transformEntry(original);
    }
}
