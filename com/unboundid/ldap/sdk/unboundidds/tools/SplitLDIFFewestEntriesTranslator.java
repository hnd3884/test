package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldif.LDIFException;
import java.util.Iterator;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.DN;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class SplitLDIFFewestEntriesTranslator extends SplitLDIFTranslator
{
    private final ConcurrentHashMap<String, Set<String>> rdnCache;
    private final Map<Set<String>, AtomicLong> setCounts;
    private final Set<String> outsideSplitBaseSetNames;
    private final Set<String> splitBaseEntrySetNames;
    
    SplitLDIFFewestEntriesTranslator(final DN splitBaseDN, final int numSets, final boolean assumeFlatDIT, final boolean addEntriesOutsideSplitToAllSets, final boolean addEntriesOutsideSplitToDedicatedSet) {
        super(splitBaseDN);
        if (assumeFlatDIT) {
            this.rdnCache = null;
        }
        else {
            this.rdnCache = new ConcurrentHashMap<String, Set<String>>(StaticUtils.computeMapCapacity(100));
        }
        this.outsideSplitBaseSetNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(numSets + 1));
        this.splitBaseEntrySetNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(numSets));
        if (addEntriesOutsideSplitToDedicatedSet) {
            this.outsideSplitBaseSetNames.add(".outside-split");
        }
        this.setCounts = new LinkedHashMap<Set<String>, AtomicLong>(StaticUtils.computeMapCapacity(numSets));
        for (int i = 0; i < numSets; ++i) {
            final String setName = ".set" + (i + 1);
            final Set<String> setSet = Collections.singleton(setName);
            this.setCounts.put(setSet, new AtomicLong(0L));
            this.splitBaseEntrySetNames.add(setName);
            if (addEntriesOutsideSplitToAllSets) {
                this.outsideSplitBaseSetNames.add(setName);
            }
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
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_FEWEST_ENTRIES_TRANSLATOR_CANNOT_PARSE_DN.get(le.getMessage()), this.getErrorSetNames());
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
            long lowestCount = Long.MAX_VALUE;
            Set<String> lowestCountSetNames = null;
            for (final Map.Entry<Set<String>, AtomicLong> e : this.setCounts.entrySet()) {
                final long count = e.getValue().get();
                if (count < lowestCount) {
                    lowestCount = count;
                    lowestCountSetNames = e.getKey();
                }
            }
            this.setCounts.get(lowestCountSetNames).incrementAndGet();
            if (this.rdnCache != null) {
                this.rdnCache.put(normalizedRDNString, lowestCountSetNames);
            }
            return this.createEntry(original, lowestCountSetNames);
        }
        if (this.rdnCache == null) {
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_FEWEST_ENTRIES_TRANSLATOR_NON_FLAT_DIT.get(this.getSplitBaseDN().toString()), this.getErrorSetNames());
        }
        final Set<String> sets = this.rdnCache.get(normalizedRDNString);
        if (sets != null) {
            this.setCounts.get(sets).incrementAndGet();
        }
        return this.createEntry(original, sets);
    }
}
