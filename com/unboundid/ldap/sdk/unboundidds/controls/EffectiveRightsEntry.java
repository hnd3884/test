package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.Validator;
import java.util.StringTokenizer;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import com.unboundid.ldap.sdk.Attribute;
import java.util.HashMap;
import java.util.Collections;
import java.util.logging.Level;
import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ReadOnlyEntry;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EffectiveRightsEntry extends ReadOnlyEntry
{
    private static final String ATTR_ACL_RIGHTS = "aclRights";
    private static final long serialVersionUID = -3203127456449579174L;
    private final Set<EntryRight> entryRights;
    private final Map<String, Set<AttributeRight>> attributeRights;
    
    public EffectiveRightsEntry(final Entry entry) {
        super(entry);
        final HashSet<String> options = StaticUtils.hashSetOf("entryLevel");
        List<Attribute> attrList = this.getAttributesWithOptions("aclRights", options);
        if (attrList == null || attrList.isEmpty()) {
            if (Debug.debugEnabled(DebugType.LDAP)) {
                Debug.debug(Level.WARNING, DebugType.LDAP, "No entry-level aclRights information contained in entry " + entry.getDN());
            }
            this.entryRights = null;
        }
        else {
            this.entryRights = Collections.unmodifiableSet((Set<? extends EntryRight>)parseEntryRights(attrList));
        }
        options.clear();
        options.add("attributeLevel");
        attrList = this.getAttributesWithOptions("aclRights", options);
        if (attrList == null || attrList.isEmpty()) {
            if (Debug.debugEnabled(DebugType.LDAP)) {
                Debug.debug(Level.WARNING, DebugType.LDAP, "No attribute-level aclRights information contained in entry " + entry.getDN());
            }
            this.attributeRights = null;
        }
        else {
            final HashMap<String, Set<AttributeRight>> attrRightsMap = new HashMap<String, Set<AttributeRight>>(StaticUtils.computeMapCapacity(attrList.size()));
            for (final Attribute a : attrList) {
                final Set<String> attrOptions = a.getOptions();
                String attrName = null;
                for (final String s : attrOptions) {
                    if (!s.equalsIgnoreCase("attributeLevel")) {
                        attrName = s;
                    }
                }
                if (attrName == null) {
                    if (!Debug.debugEnabled(DebugType.LDAP)) {
                        continue;
                    }
                    Debug.debug(Level.WARNING, DebugType.LDAP, "Unable to determine the target attribute name from " + a.getName());
                }
                else {
                    final String lowerName = StaticUtils.toLowerCase(attrName);
                    final Set<AttributeRight> rights = parseAttributeRights(a);
                    attrRightsMap.put(lowerName, rights);
                }
            }
            this.attributeRights = Collections.unmodifiableMap((Map<? extends String, ? extends Set<AttributeRight>>)attrRightsMap);
        }
    }
    
    private static Set<EntryRight> parseEntryRights(final List<Attribute> attrList) {
        final EnumSet<EntryRight> entryRightsSet = EnumSet.noneOf(EntryRight.class);
        for (final Attribute a : attrList) {
            for (final String value : a.getValues()) {
                final StringTokenizer tokenizer = new StringTokenizer(value, ", ");
                while (tokenizer.hasMoreTokens()) {
                    final String token = tokenizer.nextToken();
                    if (token.endsWith(":1")) {
                        final String rightName = token.substring(0, token.length() - 2);
                        final EntryRight r = EntryRight.forName(rightName);
                        if (r == null) {
                            if (!Debug.debugEnabled(DebugType.LDAP)) {
                                continue;
                            }
                            Debug.debug(Level.WARNING, DebugType.LDAP, "Unrecognized entry right " + rightName);
                        }
                        else {
                            entryRightsSet.add(r);
                        }
                    }
                }
            }
        }
        return entryRightsSet;
    }
    
    private static Set<AttributeRight> parseAttributeRights(final Attribute a) {
        final EnumSet<AttributeRight> rightsSet = EnumSet.noneOf(AttributeRight.class);
        for (final String value : a.getValues()) {
            final StringTokenizer tokenizer = new StringTokenizer(value, ", ");
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                if (token.endsWith(":1")) {
                    final String rightName = token.substring(0, token.length() - 2);
                    final AttributeRight r = AttributeRight.forName(rightName);
                    if (r == null) {
                        if (!Debug.debugEnabled(DebugType.LDAP)) {
                            continue;
                        }
                        Debug.debug(Level.WARNING, DebugType.LDAP, "Unrecognized attribute right " + rightName);
                    }
                    else {
                        rightsSet.add(r);
                    }
                }
            }
        }
        return rightsSet;
    }
    
    public boolean rightsInformationAvailable() {
        return this.entryRights != null || this.attributeRights != null;
    }
    
    public Set<EntryRight> getEntryRights() {
        return this.entryRights;
    }
    
    public boolean hasEntryRight(final EntryRight entryRight) {
        Validator.ensureNotNull(entryRight);
        return this.entryRights != null && this.entryRights.contains(entryRight);
    }
    
    public Map<String, Set<AttributeRight>> getAttributeRights() {
        return this.attributeRights;
    }
    
    public Set<AttributeRight> getAttributeRights(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        if (this.attributeRights == null) {
            return null;
        }
        return this.attributeRights.get(StaticUtils.toLowerCase(attributeName));
    }
    
    public boolean hasAttributeRight(final AttributeRight attributeRight, final String attributeName) {
        Validator.ensureNotNull(attributeName, attributeRight);
        final Set<AttributeRight> attrRights = this.getAttributeRights(attributeName);
        return attrRights != null && attrRights.contains(attributeRight);
    }
}
