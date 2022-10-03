package com.unboundid.ldap.sdk.schema;

import java.io.InputStream;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldif.LDIFReader;
import java.util.Arrays;
import com.unboundid.ldif.LDIFException;
import java.io.IOException;
import java.io.File;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Entry;
import java.util.Set;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Schema implements Serializable
{
    public static final String ATTR_ATTRIBUTE_SYNTAX = "ldapSyntaxes";
    public static final String ATTR_ATTRIBUTE_TYPE = "attributeTypes";
    public static final String ATTR_DIT_CONTENT_RULE = "dITContentRules";
    public static final String ATTR_DIT_STRUCTURE_RULE = "dITStructureRules";
    public static final String ATTR_MATCHING_RULE = "matchingRules";
    public static final String ATTR_MATCHING_RULE_USE = "matchingRuleUse";
    public static final String ATTR_NAME_FORM = "nameForms";
    public static final String ATTR_OBJECT_CLASS = "objectClasses";
    public static final String ATTR_SUBSCHEMA_SUBENTRY = "subschemaSubentry";
    private static final AtomicReference<Schema> DEFAULT_STANDARD_SCHEMA;
    private static final String[] SCHEMA_REQUEST_ATTRS;
    private static final String[] SUBSCHEMA_SUBENTRY_REQUEST_ATTRS;
    private static final String DEFAULT_SCHEMA_RESOURCE_PATH = "com/unboundid/ldap/sdk/schema/standard-schema.ldif";
    private static final long serialVersionUID = 8081839633831517925L;
    private final Map<AttributeTypeDefinition, List<AttributeTypeDefinition>> subordinateAttributeTypes;
    private final Map<String, AttributeSyntaxDefinition> asMap;
    private final Map<String, AttributeTypeDefinition> atMap;
    private final Map<String, DITContentRuleDefinition> dcrMap;
    private final Map<Integer, DITStructureRuleDefinition> dsrMapByID;
    private final Map<String, DITStructureRuleDefinition> dsrMapByName;
    private final Map<String, DITStructureRuleDefinition> dsrMapByNameForm;
    private final Map<String, MatchingRuleDefinition> mrMap;
    private final Map<String, MatchingRuleUseDefinition> mruMap;
    private final Map<String, NameFormDefinition> nfMapByName;
    private final Map<String, NameFormDefinition> nfMapByOC;
    private final Map<String, ObjectClassDefinition> ocMap;
    private final ReadOnlyEntry schemaEntry;
    private final Set<AttributeSyntaxDefinition> asSet;
    private final Set<AttributeTypeDefinition> atSet;
    private final Set<AttributeTypeDefinition> operationalATSet;
    private final Set<AttributeTypeDefinition> userATSet;
    private final Set<DITContentRuleDefinition> dcrSet;
    private final Set<DITStructureRuleDefinition> dsrSet;
    private final Set<MatchingRuleDefinition> mrSet;
    private final Set<MatchingRuleUseDefinition> mruSet;
    private final Set<NameFormDefinition> nfSet;
    private final Set<ObjectClassDefinition> ocSet;
    private final Set<ObjectClassDefinition> abstractOCSet;
    private final Set<ObjectClassDefinition> auxiliaryOCSet;
    private final Set<ObjectClassDefinition> structuralOCSet;
    
    public Schema(final Entry schemaEntry) {
        this(schemaEntry, null, null, null, null, null, null, null, null);
    }
    
    public Schema(final Entry schemaEntry, final Map<String, LDAPException> unparsableAttributeSyntaxes, final Map<String, LDAPException> unparsableMatchingRules, final Map<String, LDAPException> unparsableAttributeTypes, final Map<String, LDAPException> unparsableObjectClasses, final Map<String, LDAPException> unparsableDITContentRules, final Map<String, LDAPException> unparsableDITStructureRules, final Map<String, LDAPException> unparsableNameForms, final Map<String, LDAPException> unparsableMatchingRuleUses) {
        this.schemaEntry = new ReadOnlyEntry(schemaEntry);
        String[] defs = schemaEntry.getAttributeValues("ldapSyntaxes");
        if (defs == null) {
            this.asMap = Collections.emptyMap();
            this.asSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, AttributeSyntaxDefinition> m = new LinkedHashMap<String, AttributeSyntaxDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<AttributeSyntaxDefinition> s = new LinkedHashSet<AttributeSyntaxDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def : defs) {
                try {
                    final AttributeSyntaxDefinition as = new AttributeSyntaxDefinition(def);
                    s.add(as);
                    m.put(StaticUtils.toLowerCase(as.getOID()), as);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (unparsableAttributeSyntaxes != null) {
                        unparsableAttributeSyntaxes.put(def, le);
                    }
                }
            }
            this.asMap = Collections.unmodifiableMap((Map<? extends String, ? extends AttributeSyntaxDefinition>)m);
            this.asSet = Collections.unmodifiableSet((Set<? extends AttributeSyntaxDefinition>)s);
        }
        defs = schemaEntry.getAttributeValues("attributeTypes");
        if (defs == null) {
            this.atMap = Collections.emptyMap();
            this.atSet = Collections.emptySet();
            this.operationalATSet = Collections.emptySet();
            this.userATSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, AttributeTypeDefinition> i = new LinkedHashMap<String, AttributeTypeDefinition>(StaticUtils.computeMapCapacity(2 * defs.length));
            final LinkedHashSet<AttributeTypeDefinition> s2 = new LinkedHashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<AttributeTypeDefinition> sUser = new LinkedHashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<AttributeTypeDefinition> sOperational = new LinkedHashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def2 : defs) {
                try {
                    final AttributeTypeDefinition at = new AttributeTypeDefinition(def2);
                    s2.add(at);
                    i.put(StaticUtils.toLowerCase(at.getOID()), at);
                    for (final String name : at.getNames()) {
                        i.put(StaticUtils.toLowerCase(name), at);
                    }
                    if (at.isOperational()) {
                        sOperational.add(at);
                    }
                    else {
                        sUser.add(at);
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    if (unparsableAttributeTypes != null) {
                        unparsableAttributeTypes.put(def2, le2);
                    }
                }
            }
            this.atMap = Collections.unmodifiableMap((Map<? extends String, ? extends AttributeTypeDefinition>)i);
            this.atSet = Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)s2);
            this.operationalATSet = Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)sOperational);
            this.userATSet = Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)sUser);
        }
        defs = schemaEntry.getAttributeValues("dITContentRules");
        if (defs == null) {
            this.dcrMap = Collections.emptyMap();
            this.dcrSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, DITContentRuleDefinition> j = new LinkedHashMap<String, DITContentRuleDefinition>(2 * defs.length);
            final LinkedHashSet<DITContentRuleDefinition> s3 = new LinkedHashSet<DITContentRuleDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def : defs) {
                try {
                    final DITContentRuleDefinition dcr = new DITContentRuleDefinition(def);
                    s3.add(dcr);
                    j.put(StaticUtils.toLowerCase(dcr.getOID()), dcr);
                    for (final String name2 : dcr.getNames()) {
                        j.put(StaticUtils.toLowerCase(name2), dcr);
                    }
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (unparsableDITContentRules != null) {
                        unparsableDITContentRules.put(def, le);
                    }
                }
            }
            this.dcrMap = Collections.unmodifiableMap((Map<? extends String, ? extends DITContentRuleDefinition>)j);
            this.dcrSet = Collections.unmodifiableSet((Set<? extends DITContentRuleDefinition>)s3);
        }
        defs = schemaEntry.getAttributeValues("dITStructureRules");
        if (defs == null) {
            this.dsrMapByID = Collections.emptyMap();
            this.dsrMapByName = Collections.emptyMap();
            this.dsrMapByNameForm = Collections.emptyMap();
            this.dsrSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<Integer, DITStructureRuleDefinition> mID = new LinkedHashMap<Integer, DITStructureRuleDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashMap<String, DITStructureRuleDefinition> mN = new LinkedHashMap<String, DITStructureRuleDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashMap<String, DITStructureRuleDefinition> mNF = new LinkedHashMap<String, DITStructureRuleDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<DITStructureRuleDefinition> s4 = new LinkedHashSet<DITStructureRuleDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def2 : defs) {
                try {
                    final DITStructureRuleDefinition dsr = new DITStructureRuleDefinition(def2);
                    s4.add(dsr);
                    mID.put(dsr.getRuleID(), dsr);
                    mNF.put(StaticUtils.toLowerCase(dsr.getNameFormID()), dsr);
                    for (final String name : dsr.getNames()) {
                        mN.put(StaticUtils.toLowerCase(name), dsr);
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    if (unparsableDITStructureRules != null) {
                        unparsableDITStructureRules.put(def2, le2);
                    }
                }
            }
            this.dsrMapByID = Collections.unmodifiableMap((Map<? extends Integer, ? extends DITStructureRuleDefinition>)mID);
            this.dsrMapByName = Collections.unmodifiableMap((Map<? extends String, ? extends DITStructureRuleDefinition>)mN);
            this.dsrMapByNameForm = Collections.unmodifiableMap((Map<? extends String, ? extends DITStructureRuleDefinition>)mNF);
            this.dsrSet = Collections.unmodifiableSet((Set<? extends DITStructureRuleDefinition>)s4);
        }
        defs = schemaEntry.getAttributeValues("matchingRules");
        if (defs == null) {
            this.mrMap = Collections.emptyMap();
            this.mrSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, MatchingRuleDefinition> k = new LinkedHashMap<String, MatchingRuleDefinition>(StaticUtils.computeMapCapacity(2 * defs.length));
            final LinkedHashSet<MatchingRuleDefinition> s5 = new LinkedHashSet<MatchingRuleDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def : defs) {
                try {
                    final MatchingRuleDefinition mr = new MatchingRuleDefinition(def);
                    s5.add(mr);
                    k.put(StaticUtils.toLowerCase(mr.getOID()), mr);
                    for (final String name2 : mr.getNames()) {
                        k.put(StaticUtils.toLowerCase(name2), mr);
                    }
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (unparsableMatchingRules != null) {
                        unparsableMatchingRules.put(def, le);
                    }
                }
            }
            this.mrMap = Collections.unmodifiableMap((Map<? extends String, ? extends MatchingRuleDefinition>)k);
            this.mrSet = Collections.unmodifiableSet((Set<? extends MatchingRuleDefinition>)s5);
        }
        defs = schemaEntry.getAttributeValues("matchingRuleUse");
        if (defs == null) {
            this.mruMap = Collections.emptyMap();
            this.mruSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, MatchingRuleUseDefinition> l = new LinkedHashMap<String, MatchingRuleUseDefinition>(StaticUtils.computeMapCapacity(2 * defs.length));
            final LinkedHashSet<MatchingRuleUseDefinition> s6 = new LinkedHashSet<MatchingRuleUseDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def : defs) {
                try {
                    final MatchingRuleUseDefinition mru = new MatchingRuleUseDefinition(def);
                    s6.add(mru);
                    l.put(StaticUtils.toLowerCase(mru.getOID()), mru);
                    for (final String name2 : mru.getNames()) {
                        l.put(StaticUtils.toLowerCase(name2), mru);
                    }
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (unparsableMatchingRuleUses != null) {
                        unparsableMatchingRuleUses.put(def, le);
                    }
                }
            }
            this.mruMap = Collections.unmodifiableMap((Map<? extends String, ? extends MatchingRuleUseDefinition>)l);
            this.mruSet = Collections.unmodifiableSet((Set<? extends MatchingRuleUseDefinition>)s6);
        }
        defs = schemaEntry.getAttributeValues("nameForms");
        if (defs == null) {
            this.nfMapByName = Collections.emptyMap();
            this.nfMapByOC = Collections.emptyMap();
            this.nfSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, NameFormDefinition> mN2 = new LinkedHashMap<String, NameFormDefinition>(StaticUtils.computeMapCapacity(2 * defs.length));
            final LinkedHashMap<String, NameFormDefinition> mOC = new LinkedHashMap<String, NameFormDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<NameFormDefinition> s7 = new LinkedHashSet<NameFormDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def3 : defs) {
                try {
                    final NameFormDefinition nf = new NameFormDefinition(def3);
                    s7.add(nf);
                    mOC.put(StaticUtils.toLowerCase(nf.getStructuralClass()), nf);
                    mN2.put(StaticUtils.toLowerCase(nf.getOID()), nf);
                    for (final String name3 : nf.getNames()) {
                        mN2.put(StaticUtils.toLowerCase(name3), nf);
                    }
                }
                catch (final LDAPException le3) {
                    Debug.debugException(le3);
                    if (unparsableNameForms != null) {
                        unparsableNameForms.put(def3, le3);
                    }
                }
            }
            this.nfMapByName = Collections.unmodifiableMap((Map<? extends String, ? extends NameFormDefinition>)mN2);
            this.nfMapByOC = Collections.unmodifiableMap((Map<? extends String, ? extends NameFormDefinition>)mOC);
            this.nfSet = Collections.unmodifiableSet((Set<? extends NameFormDefinition>)s7);
        }
        defs = schemaEntry.getAttributeValues("objectClasses");
        if (defs == null) {
            this.ocMap = Collections.emptyMap();
            this.ocSet = Collections.emptySet();
            this.abstractOCSet = Collections.emptySet();
            this.auxiliaryOCSet = Collections.emptySet();
            this.structuralOCSet = Collections.emptySet();
        }
        else {
            final LinkedHashMap<String, ObjectClassDefinition> m2 = new LinkedHashMap<String, ObjectClassDefinition>(StaticUtils.computeMapCapacity(2 * defs.length));
            final LinkedHashSet<ObjectClassDefinition> s8 = new LinkedHashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<ObjectClassDefinition> sAbstract = new LinkedHashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<ObjectClassDefinition> sAuxiliary = new LinkedHashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(defs.length));
            final LinkedHashSet<ObjectClassDefinition> sStructural = new LinkedHashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(defs.length));
            for (final String def4 : defs) {
                try {
                    final ObjectClassDefinition oc = new ObjectClassDefinition(def4);
                    s8.add(oc);
                    m2.put(StaticUtils.toLowerCase(oc.getOID()), oc);
                    for (final String name4 : oc.getNames()) {
                        m2.put(StaticUtils.toLowerCase(name4), oc);
                    }
                    switch (oc.getObjectClassType(null)) {
                        case ABSTRACT: {
                            sAbstract.add(oc);
                            break;
                        }
                        case AUXILIARY: {
                            sAuxiliary.add(oc);
                            break;
                        }
                        case STRUCTURAL: {
                            sStructural.add(oc);
                            break;
                        }
                    }
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                    if (unparsableObjectClasses != null) {
                        unparsableObjectClasses.put(def4, le4);
                    }
                }
            }
            this.ocMap = Collections.unmodifiableMap((Map<? extends String, ? extends ObjectClassDefinition>)m2);
            this.ocSet = Collections.unmodifiableSet((Set<? extends ObjectClassDefinition>)s8);
            this.abstractOCSet = Collections.unmodifiableSet((Set<? extends ObjectClassDefinition>)sAbstract);
            this.auxiliaryOCSet = Collections.unmodifiableSet((Set<? extends ObjectClassDefinition>)sAuxiliary);
            this.structuralOCSet = Collections.unmodifiableSet((Set<? extends ObjectClassDefinition>)sStructural);
        }
        final LinkedHashMap<AttributeTypeDefinition, List<AttributeTypeDefinition>> subAttrTypes = new LinkedHashMap<AttributeTypeDefinition, List<AttributeTypeDefinition>>(StaticUtils.computeMapCapacity(this.atSet.size()));
        for (final AttributeTypeDefinition d : this.atSet) {
            for (AttributeTypeDefinition sup = d.getSuperiorType(this); sup != null; sup = sup.getSuperiorType(this)) {
                List<AttributeTypeDefinition> l2 = subAttrTypes.get(sup);
                if (l2 == null) {
                    l2 = new ArrayList<AttributeTypeDefinition>(1);
                    subAttrTypes.put(sup, l2);
                }
                l2.add(d);
            }
        }
        this.subordinateAttributeTypes = Collections.unmodifiableMap((Map<? extends AttributeTypeDefinition, ? extends List<AttributeTypeDefinition>>)subAttrTypes);
    }
    
    public static Schema parseSchemaEntry(final Entry schemaEntry) throws LDAPException {
        final Map<String, LDAPException> unparsableAttributeSyntaxes = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableMatchingRules = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableAttributeTypes = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableObjectClasses = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableDITContentRules = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableDITStructureRules = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableNameForms = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Map<String, LDAPException> unparsableMatchingRuleUses = new LinkedHashMap<String, LDAPException>(StaticUtils.computeMapCapacity(10));
        final Schema schema = new Schema(schemaEntry, unparsableAttributeSyntaxes, unparsableMatchingRules, unparsableAttributeTypes, unparsableObjectClasses, unparsableDITContentRules, unparsableDITStructureRules, unparsableNameForms, unparsableMatchingRuleUses);
        if (unparsableAttributeSyntaxes.isEmpty() && unparsableMatchingRules.isEmpty() && unparsableAttributeTypes.isEmpty() && unparsableObjectClasses.isEmpty() && unparsableDITContentRules.isEmpty() && unparsableDITStructureRules.isEmpty() && unparsableNameForms.isEmpty() && unparsableMatchingRuleUses.isEmpty()) {
            return schema;
        }
        final StringBuilder messageBuffer = new StringBuilder();
        for (final Map.Entry<String, LDAPException> e : unparsableAttributeSyntaxes.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_AS.get("ldapSyntaxes", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableMatchingRules.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_MR.get("matchingRules", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableAttributeTypes.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_AT.get("attributeTypes", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableObjectClasses.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_OC.get("objectClasses", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableDITContentRules.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_DCR.get("dITContentRules", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableDITStructureRules.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_DSR.get("dITStructureRules", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableNameForms.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_NF.get("nameForms", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        for (final Map.Entry<String, LDAPException> e : unparsableMatchingRuleUses.entrySet()) {
            appendErrorMessage(messageBuffer, SchemaMessages.ERR_SCHEMA_UNPARSABLE_MRU.get("matchingRuleUse", e.getKey(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, messageBuffer.toString());
    }
    
    private static void appendErrorMessage(final StringBuilder buffer, final String message) {
        final int length = buffer.length();
        if (length > 0) {
            if (buffer.charAt(length - 1) == '.') {
                buffer.append("  ");
            }
            else {
                buffer.append(".  ");
            }
        }
        buffer.append(message);
    }
    
    public static Schema getSchema(final LDAPConnection connection) throws LDAPException {
        return getSchema(connection, "");
    }
    
    public static Schema getSchema(final LDAPConnection connection, final String entryDN) throws LDAPException {
        return getSchema(connection, entryDN, false);
    }
    
    public static Schema getSchema(final LDAPConnection connection, final String entryDN, final boolean throwOnUnparsableElement) throws LDAPException {
        Validator.ensureNotNull(connection);
        String subschemaSubentryDN;
        if (entryDN == null) {
            subschemaSubentryDN = getSubschemaSubentryDN(connection, "");
        }
        else {
            subschemaSubentryDN = getSubschemaSubentryDN(connection, entryDN);
        }
        if (subschemaSubentryDN == null) {
            return null;
        }
        final Entry schemaEntry = connection.searchForEntry(subschemaSubentryDN, SearchScope.BASE, Filter.createEqualityFilter("objectClass", "subschema"), Schema.SCHEMA_REQUEST_ATTRS);
        if (schemaEntry == null) {
            return null;
        }
        if (throwOnUnparsableElement) {
            return parseSchemaEntry(schemaEntry);
        }
        return new Schema(schemaEntry);
    }
    
    public static Schema getSchema(final String... schemaFiles) throws IOException, LDIFException {
        Validator.ensureNotNull(schemaFiles);
        Validator.ensureFalse(schemaFiles.length == 0);
        final ArrayList<File> files = new ArrayList<File>(schemaFiles.length);
        for (final String s : schemaFiles) {
            files.add(new File(s));
        }
        return getSchema(files);
    }
    
    public static Schema getSchema(final File... schemaFiles) throws IOException, LDIFException {
        Validator.ensureNotNull(schemaFiles);
        Validator.ensureFalse(schemaFiles.length == 0);
        return getSchema(Arrays.asList(schemaFiles));
    }
    
    public static Schema getSchema(final List<File> schemaFiles) throws IOException, LDIFException {
        return getSchema(schemaFiles, false);
    }
    
    public static Schema getSchema(final List<File> schemaFiles, final boolean throwOnUnparsableElement) throws IOException, LDIFException {
        Validator.ensureNotNull(schemaFiles);
        Validator.ensureFalse(schemaFiles.isEmpty());
        Entry schemaEntry = null;
        for (final File f : schemaFiles) {
            final LDIFReader ldifReader = new LDIFReader(f);
            try {
                final Entry e = ldifReader.readEntry();
                if (e == null) {
                    continue;
                }
                e.addAttribute("objectClass", "top", "ldapSubentry", "subschema");
                if (schemaEntry == null) {
                    schemaEntry = e;
                }
                else {
                    for (final Attribute a : e.getAttributes()) {
                        schemaEntry.addAttribute(a);
                    }
                }
            }
            finally {
                ldifReader.close();
            }
        }
        if (schemaEntry == null) {
            return null;
        }
        if (throwOnUnparsableElement) {
            try {
                return parseSchemaEntry(schemaEntry);
            }
            catch (final LDAPException e2) {
                Debug.debugException(e2);
                throw new LDIFException(e2.getMessage(), 0L, false, e2);
            }
        }
        return new Schema(schemaEntry);
    }
    
    public static Schema getSchema(final InputStream inputStream) throws IOException, LDIFException {
        Validator.ensureNotNull(inputStream);
        final LDIFReader ldifReader = new LDIFReader(inputStream);
        try {
            final Entry e = ldifReader.readEntry();
            if (e == null) {
                return null;
            }
            return new Schema(e);
        }
        finally {
            ldifReader.close();
        }
    }
    
    public static Schema getDefaultStandardSchema() throws LDAPException {
        final Schema s = Schema.DEFAULT_STANDARD_SCHEMA.get();
        if (s != null) {
            return s;
        }
        synchronized (Schema.DEFAULT_STANDARD_SCHEMA) {
            try {
                final ClassLoader classLoader = Schema.class.getClassLoader();
                final InputStream inputStream = classLoader.getResourceAsStream("com/unboundid/ldap/sdk/schema/standard-schema.ldif");
                final LDIFReader ldifReader = new LDIFReader(inputStream);
                final Entry schemaEntry = ldifReader.readEntry();
                ldifReader.close();
                final Schema schema = new Schema(schemaEntry);
                Schema.DEFAULT_STANDARD_SCHEMA.set(schema);
                return schema;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, SchemaMessages.ERR_SCHEMA_CANNOT_LOAD_DEFAULT_DEFINITIONS.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
    }
    
    public static Schema mergeSchemas(final Schema... schemas) {
        if (schemas == null || schemas.length == 0) {
            return null;
        }
        if (schemas.length == 1) {
            return schemas[0];
        }
        final LinkedHashMap<String, String> asMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(100));
        final LinkedHashMap<String, String> atMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(100));
        final LinkedHashMap<String, String> dcrMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        final LinkedHashMap<Integer, String> dsrMap = new LinkedHashMap<Integer, String>(StaticUtils.computeMapCapacity(10));
        final LinkedHashMap<String, String> mrMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(100));
        final LinkedHashMap<String, String> mruMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        final LinkedHashMap<String, String> nfMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        final LinkedHashMap<String, String> ocMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(100));
        for (final Schema s : schemas) {
            for (final AttributeSyntaxDefinition as : s.asSet) {
                asMap.put(StaticUtils.toLowerCase(as.getOID()), as.toString());
            }
            for (final AttributeTypeDefinition at : s.atSet) {
                atMap.put(StaticUtils.toLowerCase(at.getOID()), at.toString());
            }
            for (final DITContentRuleDefinition dcr : s.dcrSet) {
                dcrMap.put(StaticUtils.toLowerCase(dcr.getOID()), dcr.toString());
            }
            for (final DITStructureRuleDefinition dsr : s.dsrSet) {
                dsrMap.put(dsr.getRuleID(), dsr.toString());
            }
            for (final MatchingRuleDefinition mr : s.mrSet) {
                mrMap.put(StaticUtils.toLowerCase(mr.getOID()), mr.toString());
            }
            for (final MatchingRuleUseDefinition mru : s.mruSet) {
                mruMap.put(StaticUtils.toLowerCase(mru.getOID()), mru.toString());
            }
            for (final NameFormDefinition nf : s.nfSet) {
                nfMap.put(StaticUtils.toLowerCase(nf.getOID()), nf.toString());
            }
            for (final ObjectClassDefinition oc : s.ocSet) {
                ocMap.put(StaticUtils.toLowerCase(oc.getOID()), oc.toString());
            }
        }
        final Entry e = new Entry(schemas[0].getSchemaEntry().getDN());
        final Attribute ocAttr = schemas[0].getSchemaEntry().getObjectClassAttribute();
        if (ocAttr == null) {
            e.addAttribute("objectClass", "top", "ldapSubEntry", "subschema");
        }
        else {
            e.addAttribute(ocAttr);
        }
        if (!asMap.isEmpty()) {
            final String[] values = new String[asMap.size()];
            e.addAttribute("ldapSyntaxes", (String[])asMap.values().toArray(values));
        }
        if (!mrMap.isEmpty()) {
            final String[] values = new String[mrMap.size()];
            e.addAttribute("matchingRules", (String[])mrMap.values().toArray(values));
        }
        if (!atMap.isEmpty()) {
            final String[] values = new String[atMap.size()];
            e.addAttribute("attributeTypes", (String[])atMap.values().toArray(values));
        }
        if (!ocMap.isEmpty()) {
            final String[] values = new String[ocMap.size()];
            e.addAttribute("objectClasses", (String[])ocMap.values().toArray(values));
        }
        if (!dcrMap.isEmpty()) {
            final String[] values = new String[dcrMap.size()];
            e.addAttribute("dITContentRules", (String[])dcrMap.values().toArray(values));
        }
        if (!dsrMap.isEmpty()) {
            final String[] values = new String[dsrMap.size()];
            e.addAttribute("dITStructureRules", (String[])dsrMap.values().toArray(values));
        }
        if (!nfMap.isEmpty()) {
            final String[] values = new String[nfMap.size()];
            e.addAttribute("nameForms", (String[])nfMap.values().toArray(values));
        }
        if (!mruMap.isEmpty()) {
            final String[] values = new String[mruMap.size()];
            e.addAttribute("matchingRuleUse", (String[])mruMap.values().toArray(values));
        }
        return new Schema(e);
    }
    
    public ReadOnlyEntry getSchemaEntry() {
        return this.schemaEntry;
    }
    
    public static String getSubschemaSubentryDN(final LDAPConnection connection, final String entryDN) throws LDAPException {
        Validator.ensureNotNull(connection);
        Entry e;
        if (entryDN == null) {
            e = connection.getEntry("", Schema.SUBSCHEMA_SUBENTRY_REQUEST_ATTRS);
        }
        else {
            e = connection.getEntry(entryDN, Schema.SUBSCHEMA_SUBENTRY_REQUEST_ATTRS);
        }
        if (e == null) {
            return null;
        }
        return e.getAttributeValue("subschemaSubentry");
    }
    
    public Set<AttributeSyntaxDefinition> getAttributeSyntaxes() {
        return this.asSet;
    }
    
    public AttributeSyntaxDefinition getAttributeSyntax(final String oid) {
        Validator.ensureNotNull(oid);
        final String lowerOID = StaticUtils.toLowerCase(oid);
        final int curlyPos = lowerOID.indexOf(123);
        if (curlyPos > 0) {
            return this.asMap.get(lowerOID.substring(0, curlyPos));
        }
        return this.asMap.get(lowerOID);
    }
    
    public Set<AttributeTypeDefinition> getAttributeTypes() {
        return this.atSet;
    }
    
    public Set<AttributeTypeDefinition> getOperationalAttributeTypes() {
        return this.operationalATSet;
    }
    
    public Set<AttributeTypeDefinition> getUserAttributeTypes() {
        return this.userATSet;
    }
    
    public AttributeTypeDefinition getAttributeType(final String name) {
        Validator.ensureNotNull(name);
        return this.atMap.get(StaticUtils.toLowerCase(name));
    }
    
    public List<AttributeTypeDefinition> getSubordinateAttributeTypes(final AttributeTypeDefinition d) {
        Validator.ensureNotNull(d);
        final List<AttributeTypeDefinition> l = this.subordinateAttributeTypes.get(d);
        if (l == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends AttributeTypeDefinition>)l);
    }
    
    public Set<DITContentRuleDefinition> getDITContentRules() {
        return this.dcrSet;
    }
    
    public DITContentRuleDefinition getDITContentRule(final String name) {
        Validator.ensureNotNull(name);
        return this.dcrMap.get(StaticUtils.toLowerCase(name));
    }
    
    public Set<DITStructureRuleDefinition> getDITStructureRules() {
        return this.dsrSet;
    }
    
    public DITStructureRuleDefinition getDITStructureRuleByID(final int ruleID) {
        return this.dsrMapByID.get(ruleID);
    }
    
    public DITStructureRuleDefinition getDITStructureRuleByName(final String ruleName) {
        Validator.ensureNotNull(ruleName);
        return this.dsrMapByName.get(StaticUtils.toLowerCase(ruleName));
    }
    
    public DITStructureRuleDefinition getDITStructureRuleByNameForm(final String nameForm) {
        Validator.ensureNotNull(nameForm);
        return this.dsrMapByNameForm.get(StaticUtils.toLowerCase(nameForm));
    }
    
    public Set<MatchingRuleDefinition> getMatchingRules() {
        return this.mrSet;
    }
    
    public MatchingRuleDefinition getMatchingRule(final String name) {
        Validator.ensureNotNull(name);
        return this.mrMap.get(StaticUtils.toLowerCase(name));
    }
    
    public Set<MatchingRuleUseDefinition> getMatchingRuleUses() {
        return this.mruSet;
    }
    
    public MatchingRuleUseDefinition getMatchingRuleUse(final String name) {
        Validator.ensureNotNull(name);
        return this.mruMap.get(StaticUtils.toLowerCase(name));
    }
    
    public Set<NameFormDefinition> getNameForms() {
        return this.nfSet;
    }
    
    public NameFormDefinition getNameFormByName(final String name) {
        Validator.ensureNotNull(name);
        return this.nfMapByName.get(StaticUtils.toLowerCase(name));
    }
    
    public NameFormDefinition getNameFormByObjectClass(final String objectClass) {
        Validator.ensureNotNull(objectClass);
        return this.nfMapByOC.get(StaticUtils.toLowerCase(objectClass));
    }
    
    public Set<ObjectClassDefinition> getObjectClasses() {
        return this.ocSet;
    }
    
    public Set<ObjectClassDefinition> getAbstractObjectClasses() {
        return this.abstractOCSet;
    }
    
    public Set<ObjectClassDefinition> getAuxiliaryObjectClasses() {
        return this.auxiliaryOCSet;
    }
    
    public Set<ObjectClassDefinition> getStructuralObjectClasses() {
        return this.structuralOCSet;
    }
    
    public ObjectClassDefinition getObjectClass(final String name) {
        Validator.ensureNotNull(name);
        return this.ocMap.get(StaticUtils.toLowerCase(name));
    }
    
    @Override
    public int hashCode() {
        int hc;
        try {
            hc = this.schemaEntry.getParsedDN().hashCode();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            hc = StaticUtils.toLowerCase(this.schemaEntry.getDN()).hashCode();
        }
        Attribute a = this.schemaEntry.getAttribute("ldapSyntaxes");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("matchingRules");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("attributeTypes");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("objectClasses");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("nameForms");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("dITContentRules");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("dITStructureRules");
        if (a != null) {
            hc += a.hashCode();
        }
        a = this.schemaEntry.getAttribute("matchingRuleUse");
        if (a != null) {
            hc += a.hashCode();
        }
        return hc;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Schema)) {
            return false;
        }
        final Schema s = (Schema)o;
        try {
            if (!this.schemaEntry.getParsedDN().equals(s.schemaEntry.getParsedDN())) {
                return false;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (!this.schemaEntry.getDN().equalsIgnoreCase(s.schemaEntry.getDN())) {
                return false;
            }
        }
        return this.asSet.equals(s.asSet) && this.mrSet.equals(s.mrSet) && this.atSet.equals(s.atSet) && this.ocSet.equals(s.ocSet) && this.nfSet.equals(s.nfSet) && this.dcrSet.equals(s.dcrSet) && this.dsrSet.equals(s.dsrSet) && this.mruSet.equals(s.mruSet);
    }
    
    @Override
    public String toString() {
        return this.schemaEntry.toString();
    }
    
    static {
        DEFAULT_STANDARD_SCHEMA = new AtomicReference<Schema>();
        SCHEMA_REQUEST_ATTRS = new String[] { "*", "ldapSyntaxes", "attributeTypes", "dITContentRules", "dITStructureRules", "matchingRules", "matchingRuleUse", "nameForms", "objectClasses" };
        SUBSCHEMA_SUBENTRY_REQUEST_ATTRS = new String[] { "subschemaSubentry" };
    }
}
