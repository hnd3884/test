package com.unboundid.ldap.sdk.schema;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.Attribute;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.ldap.sdk.Entry;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public final class EntryValidator implements Serializable
{
    private static final long serialVersionUID = -8945609557086398241L;
    private final AtomicLong entriesExamined;
    private final AtomicLong entriesMissingRDNValues;
    private final AtomicLong invalidEntries;
    private final AtomicLong malformedDNs;
    private final AtomicLong missingSuperiorClasses;
    private final AtomicLong multipleStructuralClasses;
    private final AtomicLong nameFormViolations;
    private final AtomicLong noObjectClasses;
    private final AtomicLong noStructuralClass;
    private boolean checkAttributeSyntax;
    private boolean checkEntryMissingRDNValues;
    private boolean checkMalformedDNs;
    private boolean checkMissingAttributes;
    private boolean checkMissingSuperiorObjectClasses;
    private boolean checkNameForms;
    private boolean checkProhibitedAttributes;
    private boolean checkProhibitedObjectClasses;
    private boolean checkSingleValuedAttributes;
    private boolean checkStructuralObjectClasses;
    private boolean checkUndefinedAttributes;
    private boolean checkUndefinedObjectClasses;
    private final ConcurrentHashMap<String, AtomicLong> attributesViolatingSyntax;
    private final ConcurrentHashMap<String, AtomicLong> missingAttributes;
    private final ConcurrentHashMap<String, AtomicLong> prohibitedAttributes;
    private final ConcurrentHashMap<String, AtomicLong> prohibitedObjectClasses;
    private final ConcurrentHashMap<String, AtomicLong> singleValueViolations;
    private final ConcurrentHashMap<String, AtomicLong> undefinedAttributes;
    private final ConcurrentHashMap<String, AtomicLong> undefinedObjectClasses;
    private final Schema schema;
    private Set<AttributeTypeDefinition> ignoreSyntaxViolationTypes;
    
    public EntryValidator(final Schema schema) {
        this.schema = schema;
        this.checkAttributeSyntax = true;
        this.checkEntryMissingRDNValues = true;
        this.checkMalformedDNs = true;
        this.checkMissingAttributes = true;
        this.checkMissingSuperiorObjectClasses = true;
        this.checkNameForms = true;
        this.checkProhibitedAttributes = true;
        this.checkProhibitedObjectClasses = true;
        this.checkSingleValuedAttributes = true;
        this.checkStructuralObjectClasses = true;
        this.checkUndefinedAttributes = true;
        this.checkUndefinedObjectClasses = true;
        this.ignoreSyntaxViolationTypes = Collections.emptySet();
        this.entriesExamined = new AtomicLong(0L);
        this.entriesMissingRDNValues = new AtomicLong(0L);
        this.invalidEntries = new AtomicLong(0L);
        this.malformedDNs = new AtomicLong(0L);
        this.missingSuperiorClasses = new AtomicLong(0L);
        this.multipleStructuralClasses = new AtomicLong(0L);
        this.nameFormViolations = new AtomicLong(0L);
        this.noObjectClasses = new AtomicLong(0L);
        this.noStructuralClass = new AtomicLong(0L);
        this.attributesViolatingSyntax = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
        this.missingAttributes = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
        this.prohibitedAttributes = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
        this.prohibitedObjectClasses = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
        this.singleValueViolations = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
        this.undefinedAttributes = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
        this.undefinedObjectClasses = new ConcurrentHashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(20));
    }
    
    public boolean checkMissingAttributes() {
        return this.checkMissingAttributes;
    }
    
    public void setCheckMissingAttributes(final boolean checkMissingAttributes) {
        this.checkMissingAttributes = checkMissingAttributes;
    }
    
    public boolean checkMissingSuperiorObjectClasses() {
        return this.checkMissingSuperiorObjectClasses;
    }
    
    public void setCheckMissingSuperiorObjectClasses(final boolean checkMissingSuperiorObjectClasses) {
        this.checkMissingSuperiorObjectClasses = checkMissingSuperiorObjectClasses;
    }
    
    public boolean checkMalformedDNs() {
        return this.checkMalformedDNs;
    }
    
    public void setCheckMalformedDNs(final boolean checkMalformedDNs) {
        this.checkMalformedDNs = checkMalformedDNs;
    }
    
    public boolean checkEntryMissingRDNValues() {
        return this.checkEntryMissingRDNValues;
    }
    
    public void setCheckEntryMissingRDNValues(final boolean checkEntryMissingRDNValues) {
        this.checkEntryMissingRDNValues = checkEntryMissingRDNValues;
    }
    
    public boolean checkNameForms() {
        return this.checkNameForms;
    }
    
    public void setCheckNameForms(final boolean checkNameForms) {
        this.checkNameForms = checkNameForms;
    }
    
    public boolean checkProhibitedAttributes() {
        return this.checkProhibitedAttributes;
    }
    
    public void setCheckProhibitedAttributes(final boolean checkProhibitedAttributes) {
        this.checkProhibitedAttributes = checkProhibitedAttributes;
    }
    
    public boolean checkProhibitedObjectClasses() {
        return this.checkProhibitedObjectClasses;
    }
    
    public void setCheckProhibitedObjectClasses(final boolean checkProhibitedObjectClasses) {
        this.checkProhibitedObjectClasses = checkProhibitedObjectClasses;
    }
    
    public boolean checkSingleValuedAttributes() {
        return this.checkSingleValuedAttributes;
    }
    
    public void setCheckSingleValuedAttributes(final boolean checkSingleValuedAttributes) {
        this.checkSingleValuedAttributes = checkSingleValuedAttributes;
    }
    
    public boolean checkStructuralObjectClasses() {
        return this.checkStructuralObjectClasses;
    }
    
    public void setCheckStructuralObjectClasses(final boolean checkStructuralObjectClasses) {
        this.checkStructuralObjectClasses = checkStructuralObjectClasses;
    }
    
    public boolean checkAttributeSyntax() {
        return this.checkAttributeSyntax;
    }
    
    public void setCheckAttributeSyntax(final boolean checkAttributeSyntax) {
        this.checkAttributeSyntax = checkAttributeSyntax;
    }
    
    public Set<AttributeTypeDefinition> getIgnoreSyntaxViolationsAttributeTypes() {
        return this.ignoreSyntaxViolationTypes;
    }
    
    public void setIgnoreSyntaxViolationAttributeTypes(final AttributeTypeDefinition... attributeTypes) {
        if (attributeTypes == null) {
            this.ignoreSyntaxViolationTypes = Collections.emptySet();
        }
        else {
            this.ignoreSyntaxViolationTypes = Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)new HashSet<AttributeTypeDefinition>(StaticUtils.toList(attributeTypes)));
        }
    }
    
    public void setIgnoreSyntaxViolationAttributeTypes(final String... attributeTypes) {
        this.setIgnoreSyntaxViolationAttributeTypes(StaticUtils.toList(attributeTypes));
    }
    
    public void setIgnoreSyntaxViolationAttributeTypes(final Collection<String> attributeTypes) {
        if (attributeTypes == null) {
            this.ignoreSyntaxViolationTypes = Collections.emptySet();
            return;
        }
        final HashSet<AttributeTypeDefinition> atSet = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(attributeTypes.size()));
        for (final String s : attributeTypes) {
            final AttributeTypeDefinition d = this.schema.getAttributeType(s);
            if (d != null) {
                atSet.add(d);
            }
        }
        this.ignoreSyntaxViolationTypes = Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)atSet);
    }
    
    public boolean checkUndefinedAttributes() {
        return this.checkUndefinedAttributes;
    }
    
    public void setCheckUndefinedAttributes(final boolean checkUndefinedAttributes) {
        this.checkUndefinedAttributes = checkUndefinedAttributes;
    }
    
    public boolean checkUndefinedObjectClasses() {
        return this.checkUndefinedObjectClasses;
    }
    
    public void setCheckUndefinedObjectClasses(final boolean checkUndefinedObjectClasses) {
        this.checkUndefinedObjectClasses = checkUndefinedObjectClasses;
    }
    
    public boolean entryIsValid(final Entry entry, final List<String> invalidReasons) {
        Validator.ensureNotNull(entry);
        boolean entryValid = true;
        this.entriesExamined.incrementAndGet();
        RDN rdn = null;
        try {
            rdn = entry.getParsedDN().getRDN();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (this.checkMalformedDNs) {
                entryValid = false;
                this.malformedDNs.incrementAndGet();
                if (invalidReasons != null) {
                    invalidReasons.add(SchemaMessages.ERR_ENTRY_MALFORMED_DN.get(StaticUtils.getExceptionMessage(le)));
                }
            }
        }
        final HashSet<ObjectClassDefinition> ocSet = new HashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(10));
        final boolean missingOC = !this.getObjectClasses(entry, ocSet, invalidReasons);
        if (missingOC) {
            entryValid = false;
        }
        DITContentRuleDefinition ditContentRule = null;
        NameFormDefinition nameForm = null;
        if (!missingOC) {
            final AtomicReference<ObjectClassDefinition> ref = new AtomicReference<ObjectClassDefinition>(null);
            entryValid &= this.getStructuralClass(ocSet, ref, invalidReasons);
            final ObjectClassDefinition structuralClass = ref.get();
            if (structuralClass != null) {
                ditContentRule = this.schema.getDITContentRule(structuralClass.getOID());
                nameForm = this.schema.getNameFormByObjectClass(structuralClass.getNameOrOID());
            }
        }
        HashSet<AttributeTypeDefinition> requiredAttrs = null;
        if (this.checkMissingAttributes || this.checkProhibitedAttributes) {
            requiredAttrs = this.getRequiredAttributes(ocSet, ditContentRule);
            if (this.checkMissingAttributes) {
                entryValid &= this.checkForMissingAttributes(entry, rdn, requiredAttrs, invalidReasons);
            }
        }
        HashSet<AttributeTypeDefinition> optionalAttrs = null;
        if (this.checkProhibitedAttributes) {
            optionalAttrs = this.getOptionalAttributes(ocSet, ditContentRule, requiredAttrs);
        }
        for (final Attribute a : entry.getAttributes()) {
            entryValid &= this.checkAttribute(a, requiredAttrs, optionalAttrs, invalidReasons);
        }
        if (this.checkProhibitedObjectClasses && ditContentRule != null) {
            entryValid &= this.checkAuxiliaryClasses(ocSet, ditContentRule, invalidReasons);
        }
        if (rdn != null) {
            entryValid &= this.checkRDN(rdn, entry, requiredAttrs, optionalAttrs, nameForm, invalidReasons);
        }
        if (!entryValid) {
            this.invalidEntries.incrementAndGet();
        }
        return entryValid;
    }
    
    private boolean getObjectClasses(final Entry entry, final HashSet<ObjectClassDefinition> ocSet, final List<String> invalidReasons) {
        final String[] ocValues = entry.getObjectClassValues();
        if (ocValues == null || ocValues.length == 0) {
            this.noObjectClasses.incrementAndGet();
            if (invalidReasons != null) {
                invalidReasons.add(SchemaMessages.ERR_ENTRY_NO_OCS.get());
            }
            return false;
        }
        boolean entryValid = true;
        final HashSet<String> missingOCs = new HashSet<String>(StaticUtils.computeMapCapacity(ocValues.length));
        for (final String ocName : entry.getObjectClassValues()) {
            final ObjectClassDefinition d = this.schema.getObjectClass(ocName);
            if (d == null) {
                if (this.checkUndefinedObjectClasses) {
                    entryValid = false;
                    missingOCs.add(StaticUtils.toLowerCase(ocName));
                    updateCount(ocName, this.undefinedObjectClasses);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_UNDEFINED_OC.get(ocName));
                    }
                }
            }
            else {
                ocSet.add(d);
            }
        }
        for (final ObjectClassDefinition d2 : new HashSet(ocSet)) {
            entryValid &= this.addSuperiorClasses(d2, ocSet, missingOCs, invalidReasons);
        }
        return entryValid;
    }
    
    private boolean addSuperiorClasses(final ObjectClassDefinition d, final HashSet<ObjectClassDefinition> ocSet, final HashSet<String> missingOCNames, final List<String> invalidReasons) {
        boolean entryValid = true;
        for (final String ocName : d.getSuperiorClasses()) {
            final ObjectClassDefinition supOC = this.schema.getObjectClass(ocName);
            if (supOC == null) {
                if (this.checkUndefinedObjectClasses) {
                    entryValid = false;
                    final String lowerName = StaticUtils.toLowerCase(ocName);
                    if (!missingOCNames.contains(lowerName)) {
                        missingOCNames.add(lowerName);
                        updateCount(ocName, this.undefinedObjectClasses);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_UNDEFINED_SUP_OC.get(d.getNameOrOID(), ocName));
                        }
                    }
                }
            }
            else {
                if (!ocSet.contains(supOC)) {
                    ocSet.add(supOC);
                    if (this.checkMissingSuperiorObjectClasses) {
                        entryValid = false;
                        this.missingSuperiorClasses.incrementAndGet();
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_MISSING_SUP_OC.get(supOC.getNameOrOID(), d.getNameOrOID()));
                        }
                    }
                }
                entryValid &= this.addSuperiorClasses(supOC, ocSet, missingOCNames, invalidReasons);
            }
        }
        return entryValid;
    }
    
    private boolean getStructuralClass(final HashSet<ObjectClassDefinition> ocSet, final AtomicReference<ObjectClassDefinition> structuralClass, final List<String> invalidReasons) {
        final HashSet<ObjectClassDefinition> ocCopy = new HashSet<ObjectClassDefinition>(ocSet);
        for (final ObjectClassDefinition d : ocSet) {
            final ObjectClassType t = d.getObjectClassType(this.schema);
            if (t == ObjectClassType.STRUCTURAL) {
                ocCopy.removeAll(d.getSuperiorClasses(this.schema, true));
            }
            else {
                if (t != ObjectClassType.AUXILIARY) {
                    continue;
                }
                ocCopy.remove(d);
                ocCopy.removeAll(d.getSuperiorClasses(this.schema, true));
            }
        }
        boolean entryValid = true;
        Iterator<ObjectClassDefinition> iterator = ocCopy.iterator();
        while (iterator.hasNext()) {
            final ObjectClassDefinition d2 = iterator.next();
            if (d2.getObjectClassType(this.schema) == ObjectClassType.ABSTRACT) {
                if (this.checkProhibitedObjectClasses) {
                    entryValid = false;
                    updateCount(d2.getNameOrOID(), this.prohibitedObjectClasses);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_INVALID_ABSTRACT_CLASS.get(d2.getNameOrOID()));
                    }
                }
                iterator.remove();
            }
        }
        switch (ocCopy.size()) {
            case 0: {
                if (!this.checkStructuralObjectClasses) {
                    break;
                }
                entryValid = false;
                this.noStructuralClass.incrementAndGet();
                if (invalidReasons != null) {
                    invalidReasons.add(SchemaMessages.ERR_ENTRY_NO_STRUCTURAL_CLASS.get());
                    break;
                }
                break;
            }
            case 1: {
                structuralClass.set(ocCopy.iterator().next());
                break;
            }
            default: {
                if (!this.checkStructuralObjectClasses) {
                    break;
                }
                entryValid = false;
                this.multipleStructuralClasses.incrementAndGet();
                if (invalidReasons != null) {
                    final StringBuilder ocList = new StringBuilder();
                    iterator = ocCopy.iterator();
                    while (iterator.hasNext()) {
                        ocList.append(iterator.next().getNameOrOID());
                        if (iterator.hasNext()) {
                            ocList.append(", ");
                        }
                    }
                    invalidReasons.add(SchemaMessages.ERR_ENTRY_MULTIPLE_STRUCTURAL_CLASSES.get(ocList));
                    break;
                }
                break;
            }
        }
        return entryValid;
    }
    
    private HashSet<AttributeTypeDefinition> getRequiredAttributes(final HashSet<ObjectClassDefinition> ocSet, final DITContentRuleDefinition ditContentRule) {
        final HashSet<AttributeTypeDefinition> attrSet = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(20));
        for (final ObjectClassDefinition oc : ocSet) {
            attrSet.addAll((Collection<?>)oc.getRequiredAttributes(this.schema, false));
        }
        if (ditContentRule != null) {
            for (final String s : ditContentRule.getRequiredAttributes()) {
                final AttributeTypeDefinition d = this.schema.getAttributeType(s);
                if (d != null) {
                    attrSet.add(d);
                }
            }
        }
        return attrSet;
    }
    
    private HashSet<AttributeTypeDefinition> getOptionalAttributes(final HashSet<ObjectClassDefinition> ocSet, final DITContentRuleDefinition ditContentRule, final HashSet<AttributeTypeDefinition> requiredAttrSet) {
        final HashSet<AttributeTypeDefinition> attrSet = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(20));
        for (final ObjectClassDefinition oc : ocSet) {
            if (oc.hasNameOrOID("extensibleObject") || oc.hasNameOrOID("1.3.6.1.4.1.1466.101.120.111")) {
                attrSet.addAll((Collection<?>)this.schema.getUserAttributeTypes());
                break;
            }
            for (final AttributeTypeDefinition d : oc.getOptionalAttributes(this.schema, false)) {
                if (!requiredAttrSet.contains(d)) {
                    attrSet.add(d);
                }
            }
        }
        if (ditContentRule != null) {
            for (final String s : ditContentRule.getOptionalAttributes()) {
                final AttributeTypeDefinition d2 = this.schema.getAttributeType(s);
                if (d2 != null && !requiredAttrSet.contains(d2)) {
                    attrSet.add(d2);
                }
            }
            for (final String s : ditContentRule.getProhibitedAttributes()) {
                final AttributeTypeDefinition d2 = this.schema.getAttributeType(s);
                if (d2 != null) {
                    attrSet.remove(d2);
                }
            }
        }
        return attrSet;
    }
    
    private boolean checkForMissingAttributes(final Entry entry, final RDN rdn, final HashSet<AttributeTypeDefinition> requiredAttrs, final List<String> invalidReasons) {
        boolean entryValid = true;
        for (final AttributeTypeDefinition d : requiredAttrs) {
            boolean found = false;
            for (final String s : d.getNames()) {
                if (entry.hasAttribute(s) || (rdn != null && rdn.hasAttribute(s))) {
                    found = true;
                    break;
                }
            }
            if (!found && !entry.hasAttribute(d.getOID()) && (rdn == null || !rdn.hasAttribute(d.getOID()))) {
                entryValid = false;
                updateCount(d.getNameOrOID(), this.missingAttributes);
                if (invalidReasons == null) {
                    continue;
                }
                invalidReasons.add(SchemaMessages.ERR_ENTRY_MISSING_REQUIRED_ATTR.get(d.getNameOrOID()));
            }
        }
        return entryValid;
    }
    
    private boolean checkAttribute(final Attribute attr, final HashSet<AttributeTypeDefinition> requiredAttrs, final HashSet<AttributeTypeDefinition> optionalAttrs, final List<String> invalidReasons) {
        boolean entryValid = true;
        final AttributeTypeDefinition d = this.schema.getAttributeType(attr.getBaseName());
        if (d == null) {
            if (this.checkUndefinedAttributes) {
                entryValid = false;
                updateCount(attr.getBaseName(), this.undefinedAttributes);
                if (invalidReasons != null) {
                    invalidReasons.add(SchemaMessages.ERR_ENTRY_UNDEFINED_ATTR.get(attr.getBaseName()));
                }
            }
            return entryValid;
        }
        if (this.checkProhibitedAttributes && !d.isOperational() && !requiredAttrs.contains(d) && !optionalAttrs.contains(d)) {
            entryValid = false;
            updateCount(d.getNameOrOID(), this.prohibitedAttributes);
            if (invalidReasons != null) {
                invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_NOT_ALLOWED.get(d.getNameOrOID()));
            }
        }
        final ASN1OctetString[] rawValues = attr.getRawValues();
        if (this.checkSingleValuedAttributes && d.isSingleValued() && rawValues.length > 1) {
            entryValid = false;
            updateCount(d.getNameOrOID(), this.singleValueViolations);
            if (invalidReasons != null) {
                invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_HAS_MULTIPLE_VALUES.get(d.getNameOrOID()));
            }
        }
        if (this.checkAttributeSyntax && !this.ignoreSyntaxViolationTypes.contains(d)) {
            final MatchingRule r = MatchingRule.selectEqualityMatchingRule(d.getNameOrOID(), this.schema);
            final Map<String, String[]> extensions = d.getExtensions();
            for (final ASN1OctetString v : rawValues) {
                try {
                    r.normalize(v);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    entryValid = false;
                    updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_INVALID_SYNTAX.get(v.stringValue(), d.getNameOrOID(), StaticUtils.getExceptionMessage(le)));
                    }
                }
                final String[] allowedValues = extensions.get("X-ALLOWED-VALUE");
                if (allowedValues != null) {
                    boolean isAllowed = false;
                    for (final String allowedValue : allowedValues) {
                        try {
                            if (r.valuesMatch(v, new ASN1OctetString(allowedValue))) {
                                isAllowed = true;
                                break;
                            }
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                        }
                    }
                    if (!isAllowed) {
                        entryValid = false;
                        updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_NOT_ALLOWED.get(v.stringValue(), d.getNameOrOID()));
                        }
                    }
                }
                final String[] valueRegexes = extensions.get("X-VALUE-REGEX");
                if (valueRegexes != null) {
                    boolean matchesRegex = false;
                    for (final String regex : valueRegexes) {
                        try {
                            final Pattern pattern = Pattern.compile(regex);
                            if (pattern.matcher(v.stringValue()).matches()) {
                                matchesRegex = true;
                                break;
                            }
                        }
                        catch (final Exception e2) {
                            Debug.debugException(e2);
                        }
                    }
                    if (!matchesRegex) {
                        entryValid = false;
                        updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_NOT_ALLOWED_BY_REGEX.get(v.stringValue(), d.getNameOrOID()));
                        }
                    }
                }
                final String[] minValueLengths = extensions.get("X-MIN-VALUE-LENGTH");
                if (minValueLengths != null) {
                    int minLength = 0;
                    for (final String s : minValueLengths) {
                        try {
                            minLength = Math.max(minLength, Integer.parseInt(s));
                        }
                        catch (final Exception e3) {
                            Debug.debugException(e3);
                        }
                    }
                    if (v.stringValue().length() < minLength) {
                        entryValid = false;
                        updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_SHORTER_THAN_MIN_LENGTH.get(v.stringValue(), d.getNameOrOID(), minLength));
                        }
                    }
                }
                final String[] maxValueLengths = extensions.get("X-MAX-VALUE-LENGTH");
                if (maxValueLengths != null) {
                    int maxLength = Integer.MAX_VALUE;
                    for (final String s2 : maxValueLengths) {
                        try {
                            maxLength = Math.min(maxLength, Integer.parseInt(s2));
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                        }
                    }
                    if (v.stringValue().length() > maxLength) {
                        entryValid = false;
                        updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_LONGER_THAN_MAX_LENGTH.get(v.stringValue(), d.getNameOrOID(), maxLength));
                        }
                    }
                }
                final String[] minIntValues = extensions.get("X-MIN-INT-VALUE");
                if (minIntValues != null) {
                    try {
                        final long longValue = Long.parseLong(v.stringValue());
                        long minAllowedValue = 0L;
                        for (final String s3 : minIntValues) {
                            try {
                                minAllowedValue = Math.max(minAllowedValue, Long.parseLong(s3));
                            }
                            catch (final Exception e5) {
                                Debug.debugException(e5);
                            }
                        }
                        if (longValue < minAllowedValue) {
                            entryValid = false;
                            updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                            if (invalidReasons != null) {
                                invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_INT_TOO_SMALL.get(longValue, d.getNameOrOID(), minAllowedValue));
                            }
                        }
                    }
                    catch (final Exception e6) {
                        Debug.debugException(e6);
                        entryValid = false;
                        updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_NOT_INT.get(v.stringValue(), d.getNameOrOID(), "X-MIN-INT-VALUE"));
                        }
                    }
                }
                final String[] maxIntValues = extensions.get("X-MAX-INT-VALUE");
                if (maxIntValues != null) {
                    try {
                        final long longValue2 = Long.parseLong(v.stringValue());
                        long maxAllowedValue = Long.MAX_VALUE;
                        for (final String s4 : maxIntValues) {
                            try {
                                maxAllowedValue = Math.min(maxAllowedValue, Long.parseLong(s4));
                            }
                            catch (final Exception e7) {
                                Debug.debugException(e7);
                            }
                        }
                        if (longValue2 > maxAllowedValue) {
                            entryValid = false;
                            updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                            if (invalidReasons != null) {
                                invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_INT_TOO_LARGE.get(longValue2, d.getNameOrOID(), maxAllowedValue));
                            }
                        }
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        entryValid = false;
                        updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                        if (invalidReasons != null) {
                            invalidReasons.add(SchemaMessages.ERR_ENTRY_ATTR_VALUE_NOT_INT.get(v.stringValue(), d.getNameOrOID(), "X-MAX-INT-VALUE"));
                        }
                    }
                }
            }
            final String[] minValueCounts = extensions.get("X-MIN-VALUE-COUNT");
            if (minValueCounts != null) {
                int minValueCount = 0;
                for (final String s5 : minValueCounts) {
                    try {
                        minValueCount = Math.max(minValueCount, Integer.parseInt(s5));
                    }
                    catch (final Exception e8) {
                        Debug.debugException(e8);
                    }
                }
                if (rawValues.length < minValueCount) {
                    entryValid = false;
                    updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_TOO_FEW_VALUES.get(rawValues.length, d.getNameOrOID(), minValueCount));
                    }
                }
            }
            final String[] maxValueCounts = extensions.get("X-MAX-VALUE-COUNT");
            if (maxValueCounts != null) {
                int maxValueCount = Integer.MAX_VALUE;
                for (final String s6 : maxValueCounts) {
                    try {
                        maxValueCount = Math.min(maxValueCount, Integer.parseInt(s6));
                    }
                    catch (final Exception e9) {
                        Debug.debugException(e9);
                    }
                }
                if (rawValues.length > maxValueCount) {
                    entryValid = false;
                    updateCount(d.getNameOrOID(), this.attributesViolatingSyntax);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_TOO_MANY_VALUES.get(rawValues.length, d.getNameOrOID(), maxValueCount));
                    }
                }
            }
        }
        return entryValid;
    }
    
    private boolean checkAuxiliaryClasses(final HashSet<ObjectClassDefinition> ocSet, final DITContentRuleDefinition ditContentRule, final List<String> invalidReasons) {
        final HashSet<ObjectClassDefinition> auxSet = new HashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(20));
        for (final String s : ditContentRule.getAuxiliaryClasses()) {
            final ObjectClassDefinition d = this.schema.getObjectClass(s);
            if (d != null) {
                auxSet.add(d);
            }
        }
        boolean entryValid = true;
        for (final ObjectClassDefinition d2 : ocSet) {
            final ObjectClassType t = d2.getObjectClassType(this.schema);
            if (t == ObjectClassType.AUXILIARY && !auxSet.contains(d2)) {
                entryValid = false;
                updateCount(d2.getNameOrOID(), this.prohibitedObjectClasses);
                if (invalidReasons == null) {
                    continue;
                }
                invalidReasons.add(SchemaMessages.ERR_ENTRY_AUX_CLASS_NOT_ALLOWED.get(d2.getNameOrOID()));
            }
        }
        return entryValid;
    }
    
    private boolean checkRDN(final RDN rdn, final Entry entry, final HashSet<AttributeTypeDefinition> requiredAttrs, final HashSet<AttributeTypeDefinition> optionalAttrs, final NameFormDefinition nameForm, final List<String> invalidReasons) {
        final HashSet<AttributeTypeDefinition> nfReqAttrs = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(5));
        final HashSet<AttributeTypeDefinition> nfAllowedAttrs = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(5));
        if (nameForm != null) {
            for (final String s : nameForm.getRequiredAttributes()) {
                final AttributeTypeDefinition d = this.schema.getAttributeType(s);
                if (d != null) {
                    nfReqAttrs.add(d);
                }
            }
            nfAllowedAttrs.addAll((Collection<?>)nfReqAttrs);
            for (final String s : nameForm.getOptionalAttributes()) {
                final AttributeTypeDefinition d = this.schema.getAttributeType(s);
                if (d != null) {
                    nfAllowedAttrs.add(d);
                }
            }
        }
        boolean entryValid = true;
        final String[] attributeNames = rdn.getAttributeNames();
        final byte[][] attributeValues = rdn.getByteArrayAttributeValues();
        for (int i = 0; i < attributeNames.length; ++i) {
            final String name = attributeNames[i];
            if (this.checkEntryMissingRDNValues) {
                final byte[] value = attributeValues[i];
                final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(name, this.schema);
                if (!entry.hasAttributeValue(name, value, matchingRule)) {
                    entryValid = false;
                    this.entriesMissingRDNValues.incrementAndGet();
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_MISSING_RDN_VALUE.get(rdn.getAttributeValues()[i], name));
                    }
                }
            }
            final AttributeTypeDefinition d2 = this.schema.getAttributeType(name);
            if (d2 == null) {
                if (this.checkUndefinedAttributes) {
                    entryValid = false;
                    updateCount(name, this.undefinedAttributes);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_RDN_ATTR_NOT_DEFINED.get(name));
                    }
                }
            }
            else {
                if (this.checkProhibitedAttributes && !requiredAttrs.contains(d2) && !optionalAttrs.contains(d2) && !d2.isOperational()) {
                    entryValid = false;
                    updateCount(d2.getNameOrOID(), this.prohibitedAttributes);
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_RDN_ATTR_NOT_ALLOWED_IN_ENTRY.get(d2.getNameOrOID()));
                    }
                }
                if (this.checkNameForms && nameForm != null && !nfReqAttrs.remove(d2) && !nfAllowedAttrs.contains(d2)) {
                    if (entryValid) {
                        entryValid = false;
                        this.nameFormViolations.incrementAndGet();
                    }
                    if (invalidReasons != null) {
                        invalidReasons.add(SchemaMessages.ERR_ENTRY_RDN_ATTR_NOT_ALLOWED_BY_NF.get(name));
                    }
                }
            }
        }
        if (this.checkNameForms && !nfReqAttrs.isEmpty()) {
            if (entryValid) {
                entryValid = false;
                this.nameFormViolations.incrementAndGet();
            }
            if (invalidReasons != null) {
                for (final AttributeTypeDefinition d : nfReqAttrs) {
                    invalidReasons.add(SchemaMessages.ERR_ENTRY_RDN_MISSING_REQUIRED_ATTR.get(d.getNameOrOID()));
                }
            }
        }
        return entryValid;
    }
    
    private static void updateCount(final String key, final ConcurrentHashMap<String, AtomicLong> map) {
        final String lowerKey = StaticUtils.toLowerCase(key);
        AtomicLong l = map.get(lowerKey);
        if (l == null) {
            l = map.putIfAbsent(lowerKey, new AtomicLong(1L));
            if (l == null) {
                return;
            }
        }
        l.incrementAndGet();
    }
    
    public void resetCounts() {
        this.entriesExamined.set(0L);
        this.entriesMissingRDNValues.set(0L);
        this.invalidEntries.set(0L);
        this.malformedDNs.set(0L);
        this.missingSuperiorClasses.set(0L);
        this.multipleStructuralClasses.set(0L);
        this.nameFormViolations.set(0L);
        this.noObjectClasses.set(0L);
        this.noStructuralClass.set(0L);
        this.attributesViolatingSyntax.clear();
        this.missingAttributes.clear();
        this.prohibitedAttributes.clear();
        this.prohibitedObjectClasses.clear();
        this.singleValueViolations.clear();
        this.undefinedAttributes.clear();
        this.undefinedObjectClasses.clear();
    }
    
    public long getEntriesExamined() {
        return this.entriesExamined.get();
    }
    
    public long getInvalidEntries() {
        return this.invalidEntries.get();
    }
    
    public long getMalformedDNs() {
        return this.malformedDNs.get();
    }
    
    public long getEntriesMissingRDNValues() {
        return this.entriesMissingRDNValues.get();
    }
    
    public long getEntriesWithoutAnyObjectClasses() {
        return this.noObjectClasses.get();
    }
    
    public long getEntriesMissingStructuralObjectClass() {
        return this.noStructuralClass.get();
    }
    
    public long getEntriesWithMultipleStructuralObjectClasses() {
        return this.multipleStructuralClasses.get();
    }
    
    public long getEntriesWithMissingSuperiorObjectClasses() {
        return this.missingSuperiorClasses.get();
    }
    
    public long getNameFormViolations() {
        return this.nameFormViolations.get();
    }
    
    public long getTotalUndefinedObjectClasses() {
        return getMapTotal(this.undefinedObjectClasses);
    }
    
    public Map<String, Long> getUndefinedObjectClasses() {
        return convertMap(this.undefinedObjectClasses);
    }
    
    public long getTotalUndefinedAttributes() {
        return getMapTotal(this.undefinedAttributes);
    }
    
    public Map<String, Long> getUndefinedAttributes() {
        return convertMap(this.undefinedAttributes);
    }
    
    public long getTotalProhibitedObjectClasses() {
        return getMapTotal(this.prohibitedObjectClasses);
    }
    
    public Map<String, Long> getProhibitedObjectClasses() {
        return convertMap(this.prohibitedObjectClasses);
    }
    
    public long getTotalProhibitedAttributes() {
        return getMapTotal(this.prohibitedAttributes);
    }
    
    public Map<String, Long> getProhibitedAttributes() {
        return convertMap(this.prohibitedAttributes);
    }
    
    public long getTotalMissingAttributes() {
        return getMapTotal(this.missingAttributes);
    }
    
    public Map<String, Long> getMissingAttributes() {
        return convertMap(this.missingAttributes);
    }
    
    public long getTotalAttributesViolatingSyntax() {
        return getMapTotal(this.attributesViolatingSyntax);
    }
    
    public Map<String, Long> getAttributesViolatingSyntax() {
        return convertMap(this.attributesViolatingSyntax);
    }
    
    public long getTotalSingleValueViolations() {
        return getMapTotal(this.singleValueViolations);
    }
    
    public Map<String, Long> getSingleValueViolations() {
        return convertMap(this.singleValueViolations);
    }
    
    private static long getMapTotal(final Map<String, AtomicLong> map) {
        long total = 0L;
        for (final AtomicLong l : map.values()) {
            total += l.longValue();
        }
        return total;
    }
    
    private static Map<String, Long> convertMap(final Map<String, AtomicLong> map) {
        final TreeMap<String, Long> m = new TreeMap<String, Long>();
        for (final Map.Entry<String, AtomicLong> e : map.entrySet()) {
            m.put(e.getKey(), e.getValue().longValue());
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends Long>)m);
    }
    
    public List<String> getInvalidEntrySummary(final boolean detailedResults) {
        final long numInvalid = this.invalidEntries.get();
        if (numInvalid == 0L) {
            return Collections.emptyList();
        }
        final ArrayList<String> messages = new ArrayList<String>(5);
        final long numEntries = this.entriesExamined.get();
        long pct = 100L * numInvalid / numEntries;
        messages.add(SchemaMessages.INFO_ENTRY_INVALID_ENTRY_COUNT.get(numInvalid, numEntries, pct));
        final long numBadDNs = this.malformedDNs.get();
        if (numBadDNs > 0L) {
            pct = 100L * numBadDNs / numEntries;
            messages.add(SchemaMessages.INFO_ENTRY_MALFORMED_DN_COUNT.get(numBadDNs, numEntries, pct));
        }
        final long numEntriesMissingRDNValues = this.entriesMissingRDNValues.get();
        if (numEntriesMissingRDNValues > 0L) {
            pct = 100L * numEntriesMissingRDNValues / numEntries;
            messages.add(SchemaMessages.INFO_ENTRY_MISSING_RDN_VALUE_COUNT.get(numEntriesMissingRDNValues, numEntries, pct));
        }
        final long numNoOCs = this.noObjectClasses.get();
        if (numNoOCs > 0L) {
            pct = 100L * numNoOCs / numEntries;
            messages.add(SchemaMessages.INFO_ENTRY_NO_OC_COUNT.get(numNoOCs, numEntries, pct));
        }
        final long numMissingStructural = this.noStructuralClass.get();
        if (numMissingStructural > 0L) {
            pct = 100L * numMissingStructural / numEntries;
            messages.add(SchemaMessages.INFO_ENTRY_NO_STRUCTURAL_OC_COUNT.get(numMissingStructural, numEntries, pct));
        }
        final long numMultipleStructural = this.multipleStructuralClasses.get();
        if (numMultipleStructural > 0L) {
            pct = 100L * numMultipleStructural / numEntries;
            messages.add(SchemaMessages.INFO_ENTRY_MULTIPLE_STRUCTURAL_OCS_COUNT.get(numMultipleStructural, numEntries, pct));
        }
        final long numNFViolations = this.nameFormViolations.get();
        if (numNFViolations > 0L) {
            pct = 100L * numNFViolations / numEntries;
            messages.add(SchemaMessages.INFO_ENTRY_NF_VIOLATION_COUNT.get(numNFViolations, numEntries, pct));
        }
        final long numUndefinedOCs = this.getTotalUndefinedObjectClasses();
        if (numUndefinedOCs > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_UNDEFINED_OC_COUNT.get(numUndefinedOCs));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e : this.undefinedObjectClasses.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_UNDEFINED_OC_NAME_COUNT.get(e.getKey(), e.getValue().longValue()));
                }
            }
        }
        final long numProhibitedOCs = this.getTotalProhibitedObjectClasses();
        if (numProhibitedOCs > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_PROHIBITED_OC_COUNT.get(numProhibitedOCs));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e2 : this.prohibitedObjectClasses.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_PROHIBITED_OC_NAME_COUNT.get(e2.getKey(), e2.getValue().longValue()));
                }
            }
        }
        final long numMissingSuperior = this.getEntriesWithMissingSuperiorObjectClasses();
        if (numMissingSuperior > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_MISSING_SUPERIOR_OC_COUNT.get(numMissingSuperior));
        }
        final long numUndefinedAttrs = this.getTotalUndefinedAttributes();
        if (numUndefinedAttrs > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_UNDEFINED_ATTR_COUNT.get(numUndefinedAttrs));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e3 : this.undefinedAttributes.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_UNDEFINED_ATTR_NAME_COUNT.get(e3.getKey(), e3.getValue().longValue()));
                }
            }
        }
        final long numMissingAttrs = this.getTotalMissingAttributes();
        if (numMissingAttrs > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_MISSING_ATTR_COUNT.get(numMissingAttrs));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e4 : this.missingAttributes.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_MISSING_ATTR_NAME_COUNT.get(e4.getKey(), e4.getValue().longValue()));
                }
            }
        }
        final long numProhibitedAttrs = this.getTotalProhibitedAttributes();
        if (numProhibitedAttrs > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_PROHIBITED_ATTR_COUNT.get(numProhibitedAttrs));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e5 : this.prohibitedAttributes.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_PROHIBITED_ATTR_NAME_COUNT.get(e5.getKey(), e5.getValue().longValue()));
                }
            }
        }
        final long numSingleValuedViolations = this.getTotalSingleValueViolations();
        if (numSingleValuedViolations > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_SINGLE_VALUE_VIOLATION_COUNT.get(numSingleValuedViolations));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e6 : this.singleValueViolations.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_SINGLE_VALUE_VIOLATION_NAME_COUNT.get(e6.getKey(), e6.getValue().longValue()));
                }
            }
        }
        final long numSyntaxViolations = this.getTotalAttributesViolatingSyntax();
        if (numSyntaxViolations > 0L) {
            messages.add(SchemaMessages.INFO_ENTRY_SYNTAX_VIOLATION_COUNT.get(numSyntaxViolations));
            if (detailedResults) {
                for (final Map.Entry<String, AtomicLong> e7 : this.attributesViolatingSyntax.entrySet()) {
                    messages.add(SchemaMessages.INFO_ENTRY_SYNTAX_VIOLATION_NAME_COUNT.get(e7.getKey(), e7.getValue().longValue()));
                }
            }
        }
        return Collections.unmodifiableList((List<? extends String>)messages);
    }
}
