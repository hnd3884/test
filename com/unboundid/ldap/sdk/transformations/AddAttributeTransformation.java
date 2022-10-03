package com.unboundid.ldap.sdk.transformations;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Collections;
import java.util.HashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Set;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AddAttributeTransformation implements EntryTransformation
{
    private final Attribute attributeToAdd;
    private final boolean examineFilter;
    private final boolean examineScope;
    private final boolean onlyIfMissing;
    private final DN baseDN;
    private final Filter filter;
    private final Schema schema;
    private final SearchScope scope;
    private final Set<String> names;
    
    public AddAttributeTransformation(final Schema schema, final DN baseDN, final SearchScope scope, final Filter filter, final Attribute attributeToAdd, final boolean onlyIfMissing) {
        this.attributeToAdd = attributeToAdd;
        this.onlyIfMissing = onlyIfMissing;
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
        final HashSet<String> attrNames = new HashSet<String>(StaticUtils.computeMapCapacity(5));
        final String baseName = StaticUtils.toLowerCase(attributeToAdd.getBaseName());
        attrNames.add(baseName);
        if (s != null) {
            final AttributeTypeDefinition at = s.getAttributeType(baseName);
            if (at != null) {
                attrNames.add(StaticUtils.toLowerCase(at.getOID()));
                for (final String name : at.getNames()) {
                    attrNames.add(StaticUtils.toLowerCase(name));
                }
            }
        }
        this.names = Collections.unmodifiableSet((Set<? extends String>)attrNames);
        if (baseDN == null) {
            this.baseDN = DN.NULL_DN;
        }
        else {
            this.baseDN = baseDN;
        }
        if (scope == null) {
            this.scope = SearchScope.SUB;
        }
        else {
            this.scope = scope;
        }
        if (filter == null) {
            this.filter = Filter.createANDFilter(new Filter[0]);
            this.examineFilter = false;
        }
        else {
            this.filter = filter;
            if (filter.getFilterType() == -96) {
                this.examineFilter = (filter.getComponents().length > 0);
            }
            else {
                this.examineFilter = true;
            }
        }
        this.examineScope = (!this.baseDN.isNullDN() || this.scope != SearchScope.SUB);
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        if (this.onlyIfMissing) {
            for (final String name : this.names) {
                if (e.hasAttribute(name)) {
                    return e;
                }
            }
        }
        try {
            if (this.examineScope && !e.matchesBaseAndScope(this.baseDN, this.scope)) {
                return e;
            }
        }
        catch (final Exception ex) {
            Debug.debugException(ex);
            return e;
        }
        try {
            if (this.examineFilter && !this.filter.matchesEntry(e, this.schema)) {
                return e;
            }
        }
        catch (final Exception ex) {
            Debug.debugException(ex);
            return e;
        }
        final Entry copy = e.duplicate();
        final Attribute existingAttribute = copy.getAttribute(this.attributeToAdd.getName(), this.schema);
        if (existingAttribute == null) {
            copy.addAttribute(this.attributeToAdd);
        }
        else {
            copy.addAttribute(existingAttribute.getName(), this.attributeToAdd.getValueByteArrays());
        }
        return copy;
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
