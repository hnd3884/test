package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldif.LDIFException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.DN;
import java.util.Set;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class SplitLDIFRDNHashTranslator extends SplitLDIFTranslator
{
    private final Map<Integer, Set<String>> setNames;
    private final Set<String> outsideSplitBaseSetNames;
    private final Set<String> splitBaseEntrySetNames;
    
    SplitLDIFRDNHashTranslator(final DN splitBaseDN, final int numSets, final boolean addEntriesOutsideSplitToAllSets, final boolean addEntriesOutsideSplitToDedicatedSet) {
        super(splitBaseDN);
        this.outsideSplitBaseSetNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(numSets + 1));
        this.splitBaseEntrySetNames = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(numSets));
        if (addEntriesOutsideSplitToDedicatedSet) {
            this.outsideSplitBaseSetNames.add(".outside-split");
        }
        this.setNames = new LinkedHashMap<Integer, Set<String>>(StaticUtils.computeMapCapacity(numSets));
        for (int i = 0; i < numSets; ++i) {
            final String setName = ".set" + (i + 1);
            this.setNames.put(i, Collections.singleton(setName));
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
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_RDN_HASH_TRANSLATOR_CANNOT_PARSE_DN.get(le.getMessage()), this.getErrorSetNames());
        }
        if (!dn.isDescendantOf(this.getSplitBaseDN(), true)) {
            return this.createEntry(original, this.outsideSplitBaseSetNames);
        }
        if (dn.equals(this.getSplitBaseDN())) {
            return this.createEntry(original, this.splitBaseEntrySetNames);
        }
        return this.createFromRDNHash(original, dn, this.setNames);
    }
}
