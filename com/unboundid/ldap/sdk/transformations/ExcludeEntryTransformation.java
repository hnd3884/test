package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DN;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExcludeEntryTransformation implements EntryTransformation, Serializable
{
    private static final long serialVersionUID = 103514669827637043L;
    private final AtomicLong excludedCount;
    private final boolean allEntriesMatchFilter;
    private final boolean allEntriesAreInScope;
    private final boolean excludeMatching;
    private final DN baseDN;
    private final Filter filter;
    private final Schema schema;
    private final SearchScope scope;
    
    public ExcludeEntryTransformation(final Schema schema, final DN baseDN, final SearchScope scope, final Filter filter, final boolean excludeMatching, final AtomicLong excludedCount) {
        this.excludeMatching = excludeMatching;
        this.excludedCount = excludedCount;
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
        this.allEntriesAreInScope = (this.baseDN.isNullDN() && this.scope == SearchScope.SUB);
        if (filter == null) {
            this.filter = Filter.createANDFilter(new Filter[0]);
            this.allEntriesMatchFilter = true;
        }
        else {
            this.filter = filter;
            if (filter.getFilterType() == -96) {
                this.allEntriesMatchFilter = (filter.getComponents().length == 0);
            }
            else {
                this.allEntriesMatchFilter = false;
            }
        }
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        boolean matchesScope;
        try {
            matchesScope = (this.allEntriesAreInScope || e.matchesBaseAndScope(this.baseDN, this.scope));
        }
        catch (final Exception ex) {
            Debug.debugException(ex);
            matchesScope = false;
        }
        boolean matchesFilter;
        try {
            matchesFilter = (this.allEntriesMatchFilter || this.filter.matchesEntry(e, this.schema));
        }
        catch (final Exception ex2) {
            Debug.debugException(ex2);
            matchesFilter = false;
        }
        if (matchesScope && matchesFilter) {
            if (this.excludeMatching) {
                if (this.excludedCount != null) {
                    this.excludedCount.incrementAndGet();
                }
                return null;
            }
            return e;
        }
        else {
            if (this.excludeMatching) {
                return e;
            }
            if (this.excludedCount != null) {
                this.excludedCount.incrementAndGet();
            }
            return null;
        }
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
