package com.unboundid.ldap.sdk.transformations;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.HashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Set;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplaceWithCounterTransformation implements EntryTransformation
{
    private final AtomicLong counter;
    private final boolean replaceInRDN;
    private final long incrementAmount;
    private final Schema schema;
    private final Set<String> names;
    private final String afterText;
    private final String beforeText;
    
    public ReplaceWithCounterTransformation(final Schema schema, final String attributeName, final long initialValue, final long incrementAmount, final String beforeText, final String afterText, final boolean replaceInRDN) {
        this.incrementAmount = incrementAmount;
        this.replaceInRDN = replaceInRDN;
        this.counter = new AtomicLong(initialValue);
        if (beforeText == null) {
            this.beforeText = "";
        }
        else {
            this.beforeText = beforeText;
        }
        if (afterText == null) {
            this.afterText = "";
        }
        else {
            this.afterText = afterText;
        }
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
        final HashSet<String> nameSet = new HashSet<String>(StaticUtils.computeMapCapacity(5));
        final String baseName = StaticUtils.toLowerCase(Attribute.getBaseName(attributeName));
        nameSet.add(baseName);
        if (s != null) {
            final AttributeTypeDefinition at = s.getAttributeType(baseName);
            if (at != null) {
                nameSet.add(StaticUtils.toLowerCase(at.getOID()));
                for (final String name : at.getNames()) {
                    nameSet.add(StaticUtils.toLowerCase(name));
                }
            }
        }
        this.names = Collections.unmodifiableSet((Set<? extends String>)nameSet);
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        String dn = e.getDN();
        String newValue = null;
        if (this.replaceInRDN) {
            try {
                final DN parsedDN = new DN(dn);
                final RDN rdn = parsedDN.getRDN();
                for (final String name : this.names) {
                    if (rdn.hasAttribute(name)) {
                        newValue = this.beforeText + this.counter.getAndAdd(this.incrementAmount) + this.afterText;
                        break;
                    }
                }
                if (newValue != null) {
                    if (rdn.isMultiValued()) {
                        final String[] attrNames = rdn.getAttributeNames();
                        final byte[][] originalValues = rdn.getByteArrayAttributeValues();
                        final byte[][] newValues = new byte[originalValues.length][];
                        for (int i = 0; i < attrNames.length; ++i) {
                            if (this.names.contains(StaticUtils.toLowerCase(attrNames[i]))) {
                                newValues[i] = StaticUtils.getBytes(newValue);
                            }
                            else {
                                newValues[i] = originalValues[i];
                            }
                        }
                        dn = new DN(new RDN(attrNames, newValues, this.schema), parsedDN.getParent()).toString();
                    }
                    else {
                        dn = new DN(new RDN(rdn.getAttributeNames()[0], newValue, this.schema), parsedDN.getParent()).toString();
                    }
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        if (newValue == null) {
            boolean hasAttribute = false;
            for (final String name2 : this.names) {
                if (e.hasAttribute(name2)) {
                    hasAttribute = true;
                    break;
                }
            }
            if (!hasAttribute) {
                return e;
            }
        }
        if (newValue == null) {
            newValue = this.beforeText + this.counter.getAndAdd(this.incrementAmount) + this.afterText;
        }
        final Collection<Attribute> originalAttributes = e.getAttributes();
        final ArrayList<Attribute> updatedAttributes = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a : originalAttributes) {
            if (this.names.contains(StaticUtils.toLowerCase(a.getBaseName()))) {
                updatedAttributes.add(new Attribute(a.getName(), this.schema, new String[] { newValue }));
            }
            else {
                updatedAttributes.add(a);
            }
        }
        return new Entry(dn, this.schema, updatedAttributes);
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
