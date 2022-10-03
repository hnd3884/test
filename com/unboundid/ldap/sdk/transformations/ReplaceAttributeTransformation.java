package com.unboundid.ldap.sdk.transformations;

import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Map;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReplaceAttributeTransformation implements EntryTransformation
{
    private final Schema schema;
    private final Map<String, Attribute> attributes;
    
    public ReplaceAttributeTransformation(final Schema schema, final String attributeName, final String... newValues) {
        this(schema, new Attribute[] { new Attribute(attributeName, schema, newValues) });
    }
    
    public ReplaceAttributeTransformation(final Schema schema, final String attributeName, final Collection<String> newValues) {
        this(schema, new Attribute[] { new Attribute(attributeName, schema, newValues) });
    }
    
    public ReplaceAttributeTransformation(final Schema schema, final Attribute... attributes) {
        this(schema, StaticUtils.toList(attributes));
    }
    
    public ReplaceAttributeTransformation(final Schema schema, final Collection<Attribute> attributes) {
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
        final HashMap<String, Attribute> attrMap = new HashMap<String, Attribute>(StaticUtils.computeMapCapacity(10));
        for (final Attribute a : attributes) {
            final String baseName = StaticUtils.toLowerCase(a.getBaseName());
            attrMap.put(baseName, a);
            if (s != null) {
                final AttributeTypeDefinition at = s.getAttributeType(baseName);
                if (at == null) {
                    continue;
                }
                attrMap.put(StaticUtils.toLowerCase(at.getOID()), new Attribute(at.getOID(), s, a.getValues()));
                for (final String name : at.getNames()) {
                    final String lowerName = StaticUtils.toLowerCase(name);
                    if (!attrMap.containsKey(lowerName)) {
                        attrMap.put(lowerName, new Attribute(name, s, a.getValues()));
                    }
                }
            }
        }
        this.attributes = Collections.unmodifiableMap((Map<? extends String, ? extends Attribute>)attrMap);
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        boolean hasAttributeToReplace = false;
        final Collection<Attribute> originalAttributes = e.getAttributes();
        for (final Attribute a : originalAttributes) {
            if (this.attributes.containsKey(StaticUtils.toLowerCase(a.getBaseName()))) {
                hasAttributeToReplace = true;
                break;
            }
        }
        if (!hasAttributeToReplace) {
            return e;
        }
        final ArrayList<Attribute> newAttributes = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a2 : originalAttributes) {
            final Attribute replacement = this.attributes.get(StaticUtils.toLowerCase(a2.getBaseName()));
            if (replacement == null) {
                newAttributes.add(a2);
            }
            else if (a2.hasOptions()) {
                newAttributes.add(new Attribute(a2.getName(), this.schema, replacement.getRawValues()));
            }
            else {
                newAttributes.add(replacement);
            }
        }
        return new Entry(e.getDN(), this.schema, newAttributes);
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
