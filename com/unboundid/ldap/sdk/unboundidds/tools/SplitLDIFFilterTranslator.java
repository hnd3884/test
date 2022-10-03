package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldif.LDIFException;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.LinkedHashSet;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.Filter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class SplitLDIFFilterTranslator extends SplitLDIFTranslator
{
    private final ConcurrentHashMap<String, Set<String>> rdnCache;
    private final Map<Filter, Set<String>> setFilters;
    private final Map<Integer, Set<String>> setNames;
    private final Schema schema;
    private final Set<String> outsideSplitBaseSetNames;
    private final Set<String> splitBaseEntrySetNames;
    
    SplitLDIFFilterTranslator(final DN splitBaseDN, final Schema schema, final LinkedHashSet<Filter> filters, final boolean assumeFlatDIT, final boolean addEntriesOutsideSplitToAllSets, final boolean addEntriesOutsideSplitToDedicatedSet) {
        super(splitBaseDN);
        this.schema = schema;
        if (assumeFlatDIT) {
            this.rdnCache = null;
        }
        else {
            this.rdnCache = new ConcurrentHashMap<String, Set<String>>(StaticUtils.computeMapCapacity(100));
        }
        final int numSets = filters.size();
        this.outsideSplitBaseSetNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(numSets + 1));
        this.splitBaseEntrySetNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(numSets));
        if (addEntriesOutsideSplitToDedicatedSet) {
            this.outsideSplitBaseSetNames.add(".outside-split");
        }
        this.setFilters = new LinkedHashMap<Filter, Set<String>>(StaticUtils.computeMapCapacity(numSets));
        this.setNames = new LinkedHashMap<Integer, Set<String>>(StaticUtils.computeMapCapacity(numSets));
        int i = 0;
        for (final Filter f : filters) {
            final String setName = ".set" + (i + 1);
            final Set<String> sets = Collections.singleton(setName);
            this.splitBaseEntrySetNames.add(setName);
            if (addEntriesOutsideSplitToAllSets) {
                this.outsideSplitBaseSetNames.add(setName);
            }
            this.setFilters.put(f, sets);
            this.setNames.put(i, sets);
            ++i;
        }
    }
    
    @Override
    public SplitLDIFEntry translate(final Entry original, final long firstLineNumber) throws LDIFException {
        DN dn;
        try {
            dn = original.getParsedDN();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_FILTER_TRANSLATOR_CANNOT_PARSE_DN.get(le.getMessage()), this.getErrorSetNames());
        }
        if (!dn.isDescendantOf(this.getSplitBaseDN(), true)) {
            return this.createEntry(original, this.outsideSplitBaseSetNames);
        }
        if (dn.equals(this.getSplitBaseDN())) {
            return this.createEntry(original, this.splitBaseEntrySetNames);
        }
        final RDN[] rdns = dn.getRDNs();
        final int targetRDNIndex = rdns.length - this.getSplitBaseRDNs().length - 1;
        final String normalizedRDNString = rdns[targetRDNIndex].toNormalizedString();
        if (targetRDNIndex <= 0) {
            for (final Map.Entry<Filter, Set<String>> e : this.setFilters.entrySet()) {
                final Filter f = e.getKey();
                try {
                    if (f.matchesEntry(original, this.schema)) {
                        final Set<String> sets = e.getValue();
                        if (this.rdnCache != null) {
                            this.rdnCache.put(normalizedRDNString, sets);
                        }
                        return this.createEntry(original, sets);
                    }
                    continue;
                }
                catch (final Exception ex) {
                    Debug.debugException(ex);
                }
            }
            final SplitLDIFEntry e2 = this.createFromRDNHash(original, dn, this.setNames);
            if (this.rdnCache != null) {
                this.rdnCache.put(normalizedRDNString, e2.getSets());
            }
            return e2;
        }
        if (this.rdnCache == null) {
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_FILTER_TRANSLATOR_NON_FLAT_DIT.get(this.getSplitBaseDN().toString()), this.getErrorSetNames());
        }
        return this.createEntry(original, this.rdnCache.get(normalizedRDNString));
    }
}
