package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.util.Debug;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.controls.SortKey;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import java.util.Comparator;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EntrySorter implements Comparator<Entry>, Serializable
{
    private static final long serialVersionUID = 7606107105238612142L;
    private final boolean sortByHierarchy;
    private final List<SortKey> sortKeys;
    private final Schema schema;
    
    public EntrySorter() {
        this(true, null, Collections.emptyList());
    }
    
    public EntrySorter(final boolean sortByHierarchy, final SortKey... sortKeys) {
        this(sortByHierarchy, null, Arrays.asList(sortKeys));
    }
    
    public EntrySorter(final boolean sortByHierarchy, final Schema schema, final SortKey... sortKeys) {
        this(sortByHierarchy, schema, Arrays.asList(sortKeys));
    }
    
    public EntrySorter(final boolean sortByHierarchy, final List<SortKey> sortKeys) {
        this(sortByHierarchy, null, sortKeys);
    }
    
    public EntrySorter(final boolean sortByHierarchy, final Schema schema, final List<SortKey> sortKeys) {
        this.sortByHierarchy = sortByHierarchy;
        this.schema = schema;
        if (sortKeys == null) {
            this.sortKeys = Collections.emptyList();
        }
        else {
            this.sortKeys = Collections.unmodifiableList((List<? extends SortKey>)new ArrayList<SortKey>(sortKeys));
        }
    }
    
    public SortedSet<Entry> sort(final Collection<? extends Entry> entries) {
        final TreeSet<Entry> entrySet = new TreeSet<Entry>(this);
        entrySet.addAll(entries);
        return entrySet;
    }
    
    @Override
    public int compare(final Entry e1, final Entry e2) {
        DN parsedDN1 = null;
        DN parsedDN2 = null;
        if (this.sortByHierarchy) {
            try {
                parsedDN1 = e1.getParsedDN();
                parsedDN2 = e2.getParsedDN();
                if (parsedDN1.isAncestorOf(parsedDN2, false)) {
                    return -1;
                }
                if (parsedDN2.isAncestorOf(parsedDN1, false)) {
                    return 1;
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
            }
        }
        for (final SortKey k : this.sortKeys) {
            final String attrName = k.getAttributeName();
            final Attribute a1 = e1.getAttribute(attrName);
            final Attribute a2 = e2.getAttribute(attrName);
            if (a1 == null || !a1.hasValue()) {
                if (a2 == null) {
                    continue;
                }
                if (!a2.hasValue()) {
                    continue;
                }
                return 1;
            }
            else {
                if (a2 == null || !a2.hasValue()) {
                    return -1;
                }
                final MatchingRule matchingRule = MatchingRule.selectOrderingMatchingRule(attrName, k.getMatchingRuleID(), this.schema);
                if (k.reverseOrder()) {
                    ASN1OctetString v1 = null;
                    for (final ASN1OctetString s : a1.getRawValues()) {
                        if (v1 == null) {
                            v1 = s;
                        }
                        else {
                            try {
                                if (matchingRule.compareValues(s, v1) > 0) {
                                    v1 = s;
                                }
                            }
                            catch (final LDAPException le2) {
                                Debug.debugException(le2);
                            }
                        }
                    }
                    ASN1OctetString v2 = null;
                    for (final ASN1OctetString s2 : a2.getRawValues()) {
                        if (v2 == null) {
                            v2 = s2;
                        }
                        else {
                            try {
                                if (matchingRule.compareValues(s2, v2) > 0) {
                                    v2 = s2;
                                }
                            }
                            catch (final LDAPException le3) {
                                Debug.debugException(le3);
                            }
                        }
                    }
                    try {
                        final int value = matchingRule.compareValues(v2, v1);
                        if (value != 0) {
                            return value;
                        }
                        continue;
                    }
                    catch (final LDAPException le4) {
                        Debug.debugException(le4);
                    }
                }
                else {
                    ASN1OctetString v1 = null;
                    for (final ASN1OctetString s : a1.getRawValues()) {
                        if (v1 == null) {
                            v1 = s;
                        }
                        else {
                            try {
                                if (matchingRule.compareValues(s, v1) < 0) {
                                    v1 = s;
                                }
                            }
                            catch (final LDAPException le2) {
                                Debug.debugException(le2);
                            }
                        }
                    }
                    ASN1OctetString v2 = null;
                    for (final ASN1OctetString s2 : a2.getRawValues()) {
                        if (v2 == null) {
                            v2 = s2;
                        }
                        else {
                            try {
                                if (matchingRule.compareValues(s2, v2) < 0) {
                                    v2 = s2;
                                }
                            }
                            catch (final LDAPException le3) {
                                Debug.debugException(le3);
                            }
                        }
                    }
                    try {
                        final int value = matchingRule.compareValues(v1, v2);
                        if (value != 0) {
                            return value;
                        }
                        continue;
                    }
                    catch (final LDAPException le4) {
                        Debug.debugException(le4);
                    }
                }
            }
        }
        try {
            if (parsedDN1 == null) {
                parsedDN1 = e1.getParsedDN();
            }
            if (parsedDN2 == null) {
                parsedDN2 = e2.getParsedDN();
            }
            return parsedDN1.compareTo(parsedDN2);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            final String lowerDN1 = StaticUtils.toLowerCase(e1.getDN());
            final String lowerDN2 = StaticUtils.toLowerCase(e2.getDN());
            return lowerDN1.compareTo(lowerDN2);
        }
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.sortByHierarchy) {
            ++hashCode;
        }
        for (final SortKey k : this.sortKeys) {
            if (k.reverseOrder()) {
                hashCode *= -31;
            }
            else {
                hashCode *= 31;
            }
            hashCode += StaticUtils.toLowerCase(k.getAttributeName()).hashCode();
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof EntrySorter)) {
            return false;
        }
        final EntrySorter s = (EntrySorter)o;
        return this.sortByHierarchy == s.sortByHierarchy && this.sortKeys.equals(s.sortKeys);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("EntrySorter(sortByHierarchy=");
        buffer.append(this.sortByHierarchy);
        buffer.append(", sortKeys={");
        final Iterator<SortKey> iterator = this.sortKeys.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
