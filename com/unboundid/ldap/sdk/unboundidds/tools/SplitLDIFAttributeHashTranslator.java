package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldif.LDIFException;
import java.util.Iterator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.Attribute;
import java.security.MessageDigest;
import com.unboundid.ldap.sdk.RDN;
import java.util.TreeSet;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.DN;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class SplitLDIFAttributeHashTranslator extends SplitLDIFTranslator
{
    private final boolean useAllValues;
    private final ConcurrentHashMap<String, Set<String>> rdnCache;
    private final Map<Integer, Set<String>> setNames;
    private final Set<String> outsideSplitBaseSetNames;
    private final Set<String> splitBaseEntrySetNames;
    private final String attributeName;
    
    SplitLDIFAttributeHashTranslator(final DN splitBaseDN, final int numSets, final String attributeName, final boolean useAllValues, final boolean assumeFlatDIT, final boolean addEntriesOutsideSplitToAllSets, final boolean addEntriesOutsideSplitToDedicatedSet) {
        super(splitBaseDN);
        this.attributeName = attributeName;
        this.useAllValues = useAllValues;
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
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_ATTR_HASH_TRANSLATOR_CANNOT_PARSE_DN.get(le.getMessage()), this.getErrorSetNames());
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
            MessageDigest md5Digest;
            try {
                md5Digest = this.getMD5();
                md5Digest.reset();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_TRANSLATOR_CANNOT_GET_MD5.get(StaticUtils.getExceptionMessage(e)), this.getErrorSetNames());
            }
            byte[] md5Bytes = null;
            final Attribute a = original.getAttribute(this.attributeName);
            if (a != null) {
                MatchingRule mr = a.getMatchingRule();
                if (mr == null) {
                    mr = CaseIgnoreStringMatchingRule.getInstance();
                }
                if (this.useAllValues && a.size() > 1) {
                    try {
                        final TreeSet<String> sortedValues = new TreeSet<String>();
                        for (final ASN1OctetString value : a.getRawValues()) {
                            sortedValues.add(mr.normalize(value).stringValue());
                        }
                        for (final String value2 : sortedValues) {
                            md5Digest.update(StaticUtils.getBytes(value2));
                        }
                        md5Bytes = md5Digest.digest();
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                    }
                }
                else if (a.size() != 0) {
                    try {
                        md5Bytes = md5Digest.digest(mr.normalize(a.getRawValues()[0]).getValue());
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                    }
                }
            }
            if (md5Bytes == null) {
                md5Bytes = md5Digest.digest(StaticUtils.getBytes(normalizedRDNString));
            }
            final int checksum = (md5Bytes[0] & 0x7F) << 24 | (md5Bytes[1] & 0xFF) << 16 | (md5Bytes[2] & 0xFF) << 8 | (md5Bytes[3] & 0xFF);
            final int setNumber = checksum % this.setNames.size();
            final Set<String> sets = this.setNames.get(setNumber);
            if (this.rdnCache != null) {
                this.rdnCache.put(normalizedRDNString, sets);
            }
            return this.createEntry(original, sets);
        }
        if (this.rdnCache == null) {
            return this.createEntry(original, ToolMessages.ERR_SPLIT_LDIF_ATTR_HASH_TRANSLATOR_NON_FLAT_DIT.get(this.getSplitBaseDN().toString()), this.getErrorSetNames());
        }
        return this.createEntry(original, this.rdnCache.get(normalizedRDNString));
    }
}
