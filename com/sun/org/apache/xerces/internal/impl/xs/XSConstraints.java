package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.List;
import java.util.Collections;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import java.util.Comparator;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;

public class XSConstraints
{
    static final int OCCURRENCE_UNKNOWN = -2;
    static final XSSimpleType STRING_TYPE;
    private static XSParticleDecl fEmptyParticle;
    private static final Comparator ELEMENT_PARTICLE_COMPARATOR;
    
    public static XSParticleDecl getEmptySequence() {
        if (XSConstraints.fEmptyParticle == null) {
            final XSModelGroupImpl group = new XSModelGroupImpl();
            group.fCompositor = 102;
            group.fParticleCount = 0;
            group.fParticles = null;
            group.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            final XSParticleDecl particle = new XSParticleDecl();
            particle.fType = 3;
            particle.fValue = group;
            particle.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            XSConstraints.fEmptyParticle = particle;
        }
        return XSConstraints.fEmptyParticle;
    }
    
    public static boolean checkTypeDerivationOk(final XSTypeDefinition derived, XSTypeDefinition base, final short block) {
        if (derived == SchemaGrammar.fAnyType) {
            return derived == base;
        }
        if (derived == SchemaGrammar.fAnySimpleType) {
            return base == SchemaGrammar.fAnyType || base == SchemaGrammar.fAnySimpleType;
        }
        if (derived.getTypeCategory() == 16) {
            if (base.getTypeCategory() == 15) {
                if (base != SchemaGrammar.fAnyType) {
                    return false;
                }
                base = SchemaGrammar.fAnySimpleType;
            }
            return checkSimpleDerivation((XSSimpleType)derived, (XSSimpleType)base, block);
        }
        return checkComplexDerivation((XSComplexTypeDecl)derived, base, block);
    }
    
    public static boolean checkSimpleDerivationOk(final XSSimpleType derived, XSTypeDefinition base, final short block) {
        if (derived == SchemaGrammar.fAnySimpleType) {
            return base == SchemaGrammar.fAnyType || base == SchemaGrammar.fAnySimpleType;
        }
        if (base.getTypeCategory() == 15) {
            if (base != SchemaGrammar.fAnyType) {
                return false;
            }
            base = SchemaGrammar.fAnySimpleType;
        }
        return checkSimpleDerivation(derived, (XSSimpleType)base, block);
    }
    
    public static boolean checkComplexDerivationOk(final XSComplexTypeDecl derived, final XSTypeDefinition base, final short block) {
        if (derived == SchemaGrammar.fAnyType) {
            return derived == base;
        }
        return checkComplexDerivation(derived, base, block);
    }
    
    private static boolean checkSimpleDerivation(final XSSimpleType derived, XSSimpleType base, final short block) {
        if (derived == base) {
            return true;
        }
        if ((block & 0x2) != 0x0 || (derived.getBaseType().getFinal() & 0x2) != 0x0) {
            return false;
        }
        final XSSimpleType directBase = (XSSimpleType)derived.getBaseType();
        if (directBase == base) {
            return true;
        }
        if (directBase != SchemaGrammar.fAnySimpleType && checkSimpleDerivation(directBase, base, block)) {
            return true;
        }
        if ((derived.getVariety() == 2 || derived.getVariety() == 3) && base == SchemaGrammar.fAnySimpleType) {
            return true;
        }
        if (base.getVariety() == 3) {
            final XSObjectList subUnionMemberDV = base.getMemberTypes();
            for (int subUnionSize = subUnionMemberDV.getLength(), i = 0; i < subUnionSize; ++i) {
                base = (XSSimpleType)subUnionMemberDV.item(i);
                if (checkSimpleDerivation(derived, base, block)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean checkComplexDerivation(final XSComplexTypeDecl derived, XSTypeDefinition base, final short block) {
        if (derived == base) {
            return true;
        }
        if ((derived.fDerivedBy & block) != 0x0) {
            return false;
        }
        final XSTypeDefinition directBase = derived.fBaseType;
        if (directBase == base) {
            return true;
        }
        if (directBase == SchemaGrammar.fAnyType || directBase == SchemaGrammar.fAnySimpleType) {
            return false;
        }
        if (directBase.getTypeCategory() == 15) {
            return checkComplexDerivation((XSComplexTypeDecl)directBase, base, block);
        }
        if (directBase.getTypeCategory() == 16) {
            if (base.getTypeCategory() == 15) {
                if (base != SchemaGrammar.fAnyType) {
                    return false;
                }
                base = SchemaGrammar.fAnySimpleType;
            }
            return checkSimpleDerivation((XSSimpleType)directBase, (XSSimpleType)base, block);
        }
        return false;
    }
    
    public static Object ElementDefaultValidImmediate(final XSTypeDefinition type, final String value, final ValidationContext context, final ValidatedInfo vinfo) {
        XSSimpleType dv = null;
        if (type.getTypeCategory() == 16) {
            dv = (XSSimpleType)type;
        }
        else {
            final XSComplexTypeDecl ctype = (XSComplexTypeDecl)type;
            if (ctype.fContentType == 1) {
                dv = ctype.fXSSimpleType;
            }
            else {
                if (ctype.fContentType != 3) {
                    return null;
                }
                if (!((XSParticleDecl)ctype.getParticle()).emptiable()) {
                    return null;
                }
            }
        }
        Object actualValue = null;
        if (dv == null) {
            dv = XSConstraints.STRING_TYPE;
        }
        try {
            actualValue = dv.validate(value, context, vinfo);
            if (vinfo != null) {
                actualValue = dv.validate(vinfo.stringValue(), context, vinfo);
            }
        }
        catch (final InvalidDatatypeValueException ide) {
            return null;
        }
        return actualValue;
    }
    
    static void reportSchemaError(final XMLErrorReporter errorReporter, final SimpleLocator loc, final String key, final Object[] args) {
        if (loc != null) {
            errorReporter.reportError(loc, "http://www.w3.org/TR/xml-schema-1", key, args, (short)1);
        }
        else {
            errorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, args, (short)1);
        }
    }
    
    public static void fullSchemaChecking(final XSGrammarBucket grammarBucket, final SubstitutionGroupHandler SGHandler, final CMBuilder cmBuilder, final XMLErrorReporter errorReporter) {
        final SchemaGrammar[] grammars = grammarBucket.getGrammars();
        for (int i = grammars.length - 1; i >= 0; --i) {
            SGHandler.addSubstitutionGroup(grammars[i].getSubstitutionGroups());
        }
        final XSParticleDecl fakeDerived = new XSParticleDecl();
        final XSParticleDecl fakeBase = new XSParticleDecl();
        fakeDerived.fType = 3;
        fakeBase.fType = 3;
        for (int g = grammars.length - 1; g >= 0; --g) {
            final XSGroupDecl[] redefinedGroups = grammars[g].getRedefinedGroupDecls();
            final SimpleLocator[] rgLocators = grammars[g].getRGLocators();
            int j = 0;
            while (j < redefinedGroups.length) {
                final XSGroupDecl derivedGrp = redefinedGroups[j++];
                final XSModelGroupImpl derivedMG = derivedGrp.fModelGroup;
                final XSGroupDecl baseGrp = redefinedGroups[j++];
                final XSModelGroupImpl baseMG = baseGrp.fModelGroup;
                fakeDerived.fValue = derivedMG;
                if ((fakeBase.fValue = baseMG) == null) {
                    if (derivedMG == null) {
                        continue;
                    }
                    reportSchemaError(errorReporter, rgLocators[j / 2 - 1], "src-redefine.6.2.2", new Object[] { derivedGrp.fName, "rcase-Recurse.2" });
                }
                else if (derivedMG == null) {
                    if (fakeBase.emptiable()) {
                        continue;
                    }
                    reportSchemaError(errorReporter, rgLocators[j / 2 - 1], "src-redefine.6.2.2", new Object[] { derivedGrp.fName, "rcase-Recurse.2" });
                }
                else {
                    try {
                        particleValidRestriction(fakeDerived, SGHandler, fakeBase, SGHandler);
                    }
                    catch (final XMLSchemaException e) {
                        final String key = e.getKey();
                        reportSchemaError(errorReporter, rgLocators[j / 2 - 1], key, e.getArgs());
                        reportSchemaError(errorReporter, rgLocators[j / 2 - 1], "src-redefine.6.2.2", new Object[] { derivedGrp.fName, key });
                    }
                }
            }
        }
        final SymbolHash elemTable = new SymbolHash();
        for (int k = grammars.length - 1; k >= 0; --k) {
            int keepType = 0;
            final boolean fullChecked = grammars[k].fFullChecked;
            final XSComplexTypeDecl[] types = grammars[k].getUncheckedComplexTypeDecls();
            final SimpleLocator[] ctLocators = grammars[k].getUncheckedCTLocators();
            for (int l = 0; l < types.length; ++l) {
                if (!fullChecked && types[l].fParticle != null) {
                    elemTable.clear();
                    try {
                        checkElementDeclsConsistent(types[l], types[l].fParticle, elemTable, SGHandler);
                    }
                    catch (final XMLSchemaException e) {
                        reportSchemaError(errorReporter, ctLocators[l], e.getKey(), e.getArgs());
                    }
                }
                if (types[l].fBaseType != null && types[l].fBaseType != SchemaGrammar.fAnyType && types[l].fDerivedBy == 2 && types[l].fBaseType instanceof XSComplexTypeDecl) {
                    final XSParticleDecl derivedParticle = types[l].fParticle;
                    final XSParticleDecl baseParticle = ((XSComplexTypeDecl)types[l].fBaseType).fParticle;
                    if (derivedParticle == null) {
                        if (baseParticle != null && !baseParticle.emptiable()) {
                            reportSchemaError(errorReporter, ctLocators[l], "derivation-ok-restriction.5.3.2", new Object[] { types[l].fName, types[l].fBaseType.getName() });
                        }
                    }
                    else if (baseParticle != null) {
                        try {
                            particleValidRestriction(types[l].fParticle, SGHandler, ((XSComplexTypeDecl)types[l].fBaseType).fParticle, SGHandler);
                        }
                        catch (final XMLSchemaException e2) {
                            reportSchemaError(errorReporter, ctLocators[l], e2.getKey(), e2.getArgs());
                            reportSchemaError(errorReporter, ctLocators[l], "derivation-ok-restriction.5.4.2", new Object[] { types[l].fName });
                        }
                    }
                    else {
                        reportSchemaError(errorReporter, ctLocators[l], "derivation-ok-restriction.5.4.2", new Object[] { types[l].fName });
                    }
                }
                final XSCMValidator cm = types[l].getContentModel(cmBuilder);
                boolean further = false;
                if (cm != null) {
                    try {
                        further = cm.checkUniqueParticleAttribution(SGHandler);
                    }
                    catch (final XMLSchemaException e3) {
                        reportSchemaError(errorReporter, ctLocators[l], e3.getKey(), e3.getArgs());
                    }
                }
                if (!fullChecked && further) {
                    types[keepType++] = types[l];
                }
            }
            if (!fullChecked) {
                grammars[k].setUncheckedTypeNum(keepType);
                grammars[k].fFullChecked = true;
            }
        }
    }
    
    public static void checkElementDeclsConsistent(final XSComplexTypeDecl type, final XSParticleDecl particle, final SymbolHash elemDeclHash, final SubstitutionGroupHandler sgHandler) throws XMLSchemaException {
        final int pType = particle.fType;
        if (pType == 2) {
            return;
        }
        if (pType == 1) {
            final XSElementDecl elem = (XSElementDecl)particle.fValue;
            findElemInTable(type, elem, elemDeclHash);
            if (elem.fScope == 1) {
                final XSElementDecl[] subGroup = sgHandler.getSubstitutionGroup(elem);
                for (int i = 0; i < subGroup.length; ++i) {
                    findElemInTable(type, subGroup[i], elemDeclHash);
                }
            }
            return;
        }
        final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
        for (int j = 0; j < group.fParticleCount; ++j) {
            checkElementDeclsConsistent(type, group.fParticles[j], elemDeclHash, sgHandler);
        }
    }
    
    public static void findElemInTable(final XSComplexTypeDecl type, final XSElementDecl elem, final SymbolHash elemDeclHash) throws XMLSchemaException {
        final String name = elem.fName + "," + elem.fTargetNamespace;
        XSElementDecl existingElem = null;
        if ((existingElem = (XSElementDecl)elemDeclHash.get(name)) == null) {
            elemDeclHash.put(name, elem);
        }
        else {
            if (elem == existingElem) {
                return;
            }
            if (elem.fType != existingElem.fType) {
                throw new XMLSchemaException("cos-element-consistent", new Object[] { type.fName, elem.fName });
            }
        }
    }
    
    private static boolean particleValidRestriction(final XSParticleDecl dParticle, final SubstitutionGroupHandler dSGHandler, final XSParticleDecl bParticle, final SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
        return particleValidRestriction(dParticle, dSGHandler, bParticle, bSGHandler, true);
    }
    
    private static boolean particleValidRestriction(XSParticleDecl dParticle, SubstitutionGroupHandler dSGHandler, XSParticleDecl bParticle, SubstitutionGroupHandler bSGHandler, final boolean checkWCOccurrence) throws XMLSchemaException {
        Vector dChildren = null;
        Vector bChildren = null;
        int dMinEffectiveTotalRange = -2;
        int dMaxEffectiveTotalRange = -2;
        boolean bExpansionHappened = false;
        if (dParticle.isEmpty() && !bParticle.emptiable()) {
            throw new XMLSchemaException("cos-particle-restrict.a", (Object[])null);
        }
        if (!dParticle.isEmpty() && bParticle.isEmpty()) {
            throw new XMLSchemaException("cos-particle-restrict.b", (Object[])null);
        }
        short dType = dParticle.fType;
        if (dType == 3) {
            dType = ((XSModelGroupImpl)dParticle.fValue).fCompositor;
            final XSParticleDecl dtmp = getNonUnaryGroup(dParticle);
            if (dtmp != dParticle) {
                dParticle = dtmp;
                dType = dParticle.fType;
                if (dType == 3) {
                    dType = ((XSModelGroupImpl)dParticle.fValue).fCompositor;
                }
            }
            dChildren = removePointlessChildren(dParticle);
        }
        final int dMinOccurs = dParticle.fMinOccurs;
        final int dMaxOccurs = dParticle.fMaxOccurs;
        if (dSGHandler != null && dType == 1) {
            final XSElementDecl dElement = (XSElementDecl)dParticle.fValue;
            if (dElement.fScope == 1) {
                final XSElementDecl[] subGroup = dSGHandler.getSubstitutionGroup(dElement);
                if (subGroup.length > 0) {
                    dType = 101;
                    dMinEffectiveTotalRange = dMinOccurs;
                    dMaxEffectiveTotalRange = dMaxOccurs;
                    dChildren = new Vector(subGroup.length + 1);
                    for (int i = 0; i < subGroup.length; ++i) {
                        addElementToParticleVector(dChildren, subGroup[i]);
                    }
                    addElementToParticleVector(dChildren, dElement);
                    Collections.sort((List<Object>)dChildren, XSConstraints.ELEMENT_PARTICLE_COMPARATOR);
                    dSGHandler = null;
                }
            }
        }
        short bType = bParticle.fType;
        if (bType == 3) {
            bType = ((XSModelGroupImpl)bParticle.fValue).fCompositor;
            final XSParticleDecl btmp = getNonUnaryGroup(bParticle);
            if (btmp != bParticle) {
                bParticle = btmp;
                bType = bParticle.fType;
                if (bType == 3) {
                    bType = ((XSModelGroupImpl)bParticle.fValue).fCompositor;
                }
            }
            bChildren = removePointlessChildren(bParticle);
        }
        final int bMinOccurs = bParticle.fMinOccurs;
        final int bMaxOccurs = bParticle.fMaxOccurs;
        if (bSGHandler != null && bType == 1) {
            final XSElementDecl bElement = (XSElementDecl)bParticle.fValue;
            if (bElement.fScope == 1) {
                final XSElementDecl[] bsubGroup = bSGHandler.getSubstitutionGroup(bElement);
                if (bsubGroup.length > 0) {
                    bType = 101;
                    bChildren = new Vector(bsubGroup.length + 1);
                    for (int j = 0; j < bsubGroup.length; ++j) {
                        addElementToParticleVector(bChildren, bsubGroup[j]);
                    }
                    addElementToParticleVector(bChildren, bElement);
                    Collections.sort((List<Object>)bChildren, XSConstraints.ELEMENT_PARTICLE_COMPARATOR);
                    bSGHandler = null;
                    bExpansionHappened = true;
                }
            }
        }
        switch (dType) {
            case 1: {
                switch (bType) {
                    case 1: {
                        checkNameAndTypeOK((XSElementDecl)dParticle.fValue, dMinOccurs, dMaxOccurs, (XSElementDecl)bParticle.fValue, bMinOccurs, bMaxOccurs);
                        return bExpansionHappened;
                    }
                    case 2: {
                        checkNSCompat((XSElementDecl)dParticle.fValue, dMinOccurs, dMaxOccurs, (XSWildcardDecl)bParticle.fValue, bMinOccurs, bMaxOccurs, checkWCOccurrence);
                        return bExpansionHappened;
                    }
                    case 101: {
                        dChildren = new Vector();
                        dChildren.addElement(dParticle);
                        checkRecurseLax(dChildren, 1, 1, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    case 102:
                    case 103: {
                        dChildren = new Vector();
                        dChildren.addElement(dParticle);
                        checkRecurse(dChildren, 1, 1, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 2: {
                switch (bType) {
                    case 2: {
                        checkNSSubset((XSWildcardDecl)dParticle.fValue, dMinOccurs, dMaxOccurs, (XSWildcardDecl)bParticle.fValue, bMinOccurs, bMaxOccurs);
                        return bExpansionHappened;
                    }
                    case 1:
                    case 101:
                    case 102:
                    case 103: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "any:choice,sequence,all,elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 103: {
                switch (bType) {
                    case 2: {
                        if (dMinEffectiveTotalRange == -2) {
                            dMinEffectiveTotalRange = dParticle.minEffectiveTotalRange();
                        }
                        if (dMaxEffectiveTotalRange == -2) {
                            dMaxEffectiveTotalRange = dParticle.maxEffectiveTotalRange();
                        }
                        checkNSRecurseCheckCardinality(dChildren, dMinEffectiveTotalRange, dMaxEffectiveTotalRange, dSGHandler, bParticle, bMinOccurs, bMaxOccurs, checkWCOccurrence);
                        return bExpansionHappened;
                    }
                    case 103: {
                        checkRecurse(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    case 1:
                    case 101:
                    case 102: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "all:choice,sequence,elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 101: {
                switch (bType) {
                    case 2: {
                        if (dMinEffectiveTotalRange == -2) {
                            dMinEffectiveTotalRange = dParticle.minEffectiveTotalRange();
                        }
                        if (dMaxEffectiveTotalRange == -2) {
                            dMaxEffectiveTotalRange = dParticle.maxEffectiveTotalRange();
                        }
                        checkNSRecurseCheckCardinality(dChildren, dMinEffectiveTotalRange, dMaxEffectiveTotalRange, dSGHandler, bParticle, bMinOccurs, bMaxOccurs, checkWCOccurrence);
                        return bExpansionHappened;
                    }
                    case 101: {
                        checkRecurseLax(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    case 1:
                    case 102:
                    case 103: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "choice:all,sequence,elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 102: {
                switch (bType) {
                    case 2: {
                        if (dMinEffectiveTotalRange == -2) {
                            dMinEffectiveTotalRange = dParticle.minEffectiveTotalRange();
                        }
                        if (dMaxEffectiveTotalRange == -2) {
                            dMaxEffectiveTotalRange = dParticle.maxEffectiveTotalRange();
                        }
                        checkNSRecurseCheckCardinality(dChildren, dMinEffectiveTotalRange, dMaxEffectiveTotalRange, dSGHandler, bParticle, bMinOccurs, bMaxOccurs, checkWCOccurrence);
                        return bExpansionHappened;
                    }
                    case 103: {
                        checkRecurseUnordered(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    case 102: {
                        checkRecurse(dChildren, dMinOccurs, dMaxOccurs, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    case 101: {
                        final int min1 = dMinOccurs * dChildren.size();
                        final int max1 = (dMaxOccurs == -1) ? dMaxOccurs : (dMaxOccurs * dChildren.size());
                        checkMapAndSum(dChildren, min1, max1, dSGHandler, bChildren, bMinOccurs, bMaxOccurs, bSGHandler);
                        return bExpansionHappened;
                    }
                    case 1: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "seq:elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            default: {
                return bExpansionHappened;
            }
        }
    }
    
    private static void addElementToParticleVector(final Vector v, final XSElementDecl d) {
        final XSParticleDecl p = new XSParticleDecl();
        p.fValue = d;
        p.fType = 1;
        v.addElement(p);
    }
    
    private static XSParticleDecl getNonUnaryGroup(final XSParticleDecl p) {
        if (p.fType == 1 || p.fType == 2) {
            return p;
        }
        if (p.fMinOccurs == 1 && p.fMaxOccurs == 1 && p.fValue != null && ((XSModelGroupImpl)p.fValue).fParticleCount == 1) {
            return getNonUnaryGroup(((XSModelGroupImpl)p.fValue).fParticles[0]);
        }
        return p;
    }
    
    private static Vector removePointlessChildren(final XSParticleDecl p) {
        if (p.fType == 1 || p.fType == 2) {
            return null;
        }
        final Vector children = new Vector();
        final XSModelGroupImpl group = (XSModelGroupImpl)p.fValue;
        for (int i = 0; i < group.fParticleCount; ++i) {
            gatherChildren(group.fCompositor, group.fParticles[i], children);
        }
        return children;
    }
    
    private static void gatherChildren(final int parentType, final XSParticleDecl p, final Vector children) {
        final int min = p.fMinOccurs;
        final int max = p.fMaxOccurs;
        int type = p.fType;
        if (type == 3) {
            type = ((XSModelGroupImpl)p.fValue).fCompositor;
        }
        if (type == 1 || type == 2) {
            children.addElement(p);
            return;
        }
        if (min != 1 || max != 1) {
            children.addElement(p);
        }
        else if (parentType == type) {
            final XSModelGroupImpl group = (XSModelGroupImpl)p.fValue;
            for (int i = 0; i < group.fParticleCount; ++i) {
                gatherChildren(type, group.fParticles[i], children);
            }
        }
        else if (!p.isEmpty()) {
            children.addElement(p);
        }
    }
    
    private static void checkNameAndTypeOK(final XSElementDecl dElement, final int dMin, final int dMax, final XSElementDecl bElement, final int bMin, final int bMax) throws XMLSchemaException {
        if (dElement.fName != bElement.fName || dElement.fTargetNamespace != bElement.fTargetNamespace) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.1", new Object[] { dElement.fName, dElement.fTargetNamespace, bElement.fName, bElement.fTargetNamespace });
        }
        if (!bElement.getNillable() && dElement.getNillable()) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.2", new Object[] { dElement.fName });
        }
        if (!checkOccurrenceRange(dMin, dMax, bMin, bMax)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.3", new Object[] { dElement.fName, Integer.toString(dMin), (dMax == -1) ? "unbounded" : Integer.toString(dMax), Integer.toString(bMin), (bMax == -1) ? "unbounded" : Integer.toString(bMax) });
        }
        if (bElement.getConstraintType() == 2) {
            if (dElement.getConstraintType() != 2) {
                throw new XMLSchemaException("rcase-NameAndTypeOK.4.a", new Object[] { dElement.fName, bElement.fDefault.stringValue() });
            }
            boolean isSimple = false;
            if (dElement.fType.getTypeCategory() == 16 || ((XSComplexTypeDecl)dElement.fType).fContentType == 1) {
                isSimple = true;
            }
            if ((!isSimple && !bElement.fDefault.normalizedValue.equals(dElement.fDefault.normalizedValue)) || (isSimple && !bElement.fDefault.actualValue.equals(dElement.fDefault.actualValue))) {
                throw new XMLSchemaException("rcase-NameAndTypeOK.4.b", new Object[] { dElement.fName, dElement.fDefault.stringValue(), bElement.fDefault.stringValue() });
            }
        }
        checkIDConstraintRestriction(dElement, bElement);
        final int blockSet1 = dElement.fBlock;
        final int blockSet2 = bElement.fBlock;
        if ((blockSet1 & blockSet2) != blockSet2 || (blockSet1 == 0 && blockSet2 != 0)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.6", new Object[] { dElement.fName });
        }
        if (!checkTypeDerivationOk(dElement.fType, bElement.fType, (short)25)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.7", new Object[] { dElement.fName, dElement.fType.getName(), bElement.fType.getName() });
        }
    }
    
    private static void checkIDConstraintRestriction(final XSElementDecl derivedElemDecl, final XSElementDecl baseElemDecl) throws XMLSchemaException {
    }
    
    private static boolean checkOccurrenceRange(final int min1, final int max1, final int min2, final int max2) {
        return min1 >= min2 && (max2 == -1 || (max1 != -1 && max1 <= max2));
    }
    
    private static void checkNSCompat(final XSElementDecl elem, final int min1, final int max1, final XSWildcardDecl wildcard, final int min2, final int max2, final boolean checkWCOccurrence) throws XMLSchemaException {
        if (checkWCOccurrence && !checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-NSCompat.2", new Object[] { elem.fName, Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        if (!wildcard.allowNamespace(elem.fTargetNamespace)) {
            throw new XMLSchemaException("rcase-NSCompat.1", new Object[] { elem.fName, elem.fTargetNamespace });
        }
    }
    
    private static void checkNSSubset(final XSWildcardDecl dWildcard, final int min1, final int max1, final XSWildcardDecl bWildcard, final int min2, final int max2) throws XMLSchemaException {
        if (!checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-NSSubset.2", new Object[] { Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        if (!dWildcard.isSubsetOf(bWildcard)) {
            throw new XMLSchemaException("rcase-NSSubset.1", (Object[])null);
        }
        if (dWildcard.weakerProcessContents(bWildcard)) {
            throw new XMLSchemaException("rcase-NSSubset.3", new Object[] { dWildcard.getProcessContentsAsString(), bWildcard.getProcessContentsAsString() });
        }
    }
    
    private static void checkNSRecurseCheckCardinality(final Vector children, final int min1, final int max1, final SubstitutionGroupHandler dSGHandler, final XSParticleDecl wildcard, final int min2, final int max2, final boolean checkWCOccurrence) throws XMLSchemaException {
        if (checkWCOccurrence && !checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.2", new Object[] { Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        final int count = children.size();
        try {
            for (int i = 0; i < count; ++i) {
                final XSParticleDecl particle1 = children.elementAt(i);
                particleValidRestriction(particle1, dSGHandler, wildcard, null, false);
            }
        }
        catch (final XMLSchemaException e) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.1", (Object[])null);
        }
    }
    
    private static void checkRecurse(final Vector dChildren, final int min1, final int max1, final SubstitutionGroupHandler dSGHandler, final Vector bChildren, final int min2, final int max2, final SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
        if (!checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-Recurse.1", new Object[] { Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        final int count1 = dChildren.size();
        final int count2 = bChildren.size();
        int current = 0;
        int i = 0;
    Label_0096:
        while (i < count1) {
            final XSParticleDecl particle1 = dChildren.elementAt(i);
            int j = current;
            while (j < count2) {
                final XSParticleDecl particle2 = bChildren.elementAt(j);
                ++current;
                Label_0192: {
                    try {
                        particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler);
                        break Label_0192;
                    }
                    catch (final XMLSchemaException e) {
                        if (!particle2.emptiable()) {
                            throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
                        }
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0096;
            }
            throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
        }
        for (int k = current; k < count2; ++k) {
            final XSParticleDecl particle3 = bChildren.elementAt(k);
            if (!particle3.emptiable()) {
                throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
            }
        }
    }
    
    private static void checkRecurseUnordered(final Vector dChildren, final int min1, final int max1, final SubstitutionGroupHandler dSGHandler, final Vector bChildren, final int min2, final int max2, final SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
        if (!checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-RecurseUnordered.1", new Object[] { Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        final int count1 = dChildren.size();
        final int count2 = bChildren.size();
        final boolean[] foundIt = new boolean[count2];
        int i = 0;
    Label_0099:
        while (i < count1) {
            final XSParticleDecl particle1 = dChildren.elementAt(i);
            int j = 0;
            while (j < count2) {
                final XSParticleDecl particle2 = bChildren.elementAt(j);
                Label_0197: {
                    try {
                        particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler);
                        if (foundIt[j]) {
                            throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
                        }
                        foundIt[j] = true;
                        break Label_0197;
                    }
                    catch (final XMLSchemaException ex) {
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0099;
            }
            throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
        }
        for (int k = 0; k < count2; ++k) {
            final XSParticleDecl particle3 = bChildren.elementAt(k);
            if (!foundIt[k] && !particle3.emptiable()) {
                throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
            }
        }
    }
    
    private static void checkRecurseLax(final Vector dChildren, final int min1, final int max1, final SubstitutionGroupHandler dSGHandler, final Vector bChildren, final int min2, final int max2, final SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
        if (!checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-RecurseLax.1", new Object[] { Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        final int count1 = dChildren.size();
        final int count2 = bChildren.size();
        int current = 0;
        int i = 0;
    Label_0096:
        while (i < count1) {
            final XSParticleDecl particle1 = dChildren.elementAt(i);
            int j = current;
            while (j < count2) {
                final XSParticleDecl particle2 = bChildren.elementAt(j);
                ++current;
                Label_0178: {
                    try {
                        if (particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler)) {
                            --current;
                        }
                        break Label_0178;
                    }
                    catch (final XMLSchemaException ex) {
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0096;
            }
            throw new XMLSchemaException("rcase-RecurseLax.2", (Object[])null);
        }
    }
    
    private static void checkMapAndSum(final Vector dChildren, final int min1, final int max1, final SubstitutionGroupHandler dSGHandler, final Vector bChildren, final int min2, final int max2, final SubstitutionGroupHandler bSGHandler) throws XMLSchemaException {
        if (!checkOccurrenceRange(min1, max1, min2, max2)) {
            throw new XMLSchemaException("rcase-MapAndSum.2", new Object[] { Integer.toString(min1), (max1 == -1) ? "unbounded" : Integer.toString(max1), Integer.toString(min2), (max2 == -1) ? "unbounded" : Integer.toString(max2) });
        }
        final int count1 = dChildren.size();
        final int count2 = bChildren.size();
        int i = 0;
    Label_0093:
        while (i < count1) {
            final XSParticleDecl particle1 = dChildren.elementAt(i);
            int j = 0;
            while (j < count2) {
                final XSParticleDecl particle2 = bChildren.elementAt(j);
                Label_0166: {
                    try {
                        particleValidRestriction(particle1, dSGHandler, particle2, bSGHandler);
                        break Label_0166;
                    }
                    catch (final XMLSchemaException ex) {
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0093;
            }
            throw new XMLSchemaException("rcase-MapAndSum.1", (Object[])null);
        }
    }
    
    public static boolean overlapUPA(final XSElementDecl element1, final XSElementDecl element2, final SubstitutionGroupHandler sgHandler) {
        if (element1.fName == element2.fName && element1.fTargetNamespace == element2.fTargetNamespace) {
            return true;
        }
        XSElementDecl[] subGroup = sgHandler.getSubstitutionGroup(element1);
        for (int i = subGroup.length - 1; i >= 0; --i) {
            if (subGroup[i].fName == element2.fName && subGroup[i].fTargetNamespace == element2.fTargetNamespace) {
                return true;
            }
        }
        subGroup = sgHandler.getSubstitutionGroup(element2);
        for (int i = subGroup.length - 1; i >= 0; --i) {
            if (subGroup[i].fName == element1.fName && subGroup[i].fTargetNamespace == element1.fTargetNamespace) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean overlapUPA(final XSElementDecl element, final XSWildcardDecl wildcard, final SubstitutionGroupHandler sgHandler) {
        if (wildcard.allowNamespace(element.fTargetNamespace)) {
            return true;
        }
        final XSElementDecl[] subGroup = sgHandler.getSubstitutionGroup(element);
        for (int i = subGroup.length - 1; i >= 0; --i) {
            if (wildcard.allowNamespace(subGroup[i].fTargetNamespace)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean overlapUPA(final XSWildcardDecl wildcard1, final XSWildcardDecl wildcard2) {
        final XSWildcardDecl intersect = wildcard1.performIntersectionWith(wildcard2, wildcard1.fProcessContents);
        return intersect == null || intersect.fType != 3 || intersect.fNamespaceList.length != 0;
    }
    
    public static boolean overlapUPA(final Object decl1, final Object decl2, final SubstitutionGroupHandler sgHandler) {
        if (decl1 instanceof XSElementDecl) {
            if (decl2 instanceof XSElementDecl) {
                return overlapUPA((XSElementDecl)decl1, (XSElementDecl)decl2, sgHandler);
            }
            return overlapUPA((XSElementDecl)decl1, (XSWildcardDecl)decl2, sgHandler);
        }
        else {
            if (decl2 instanceof XSElementDecl) {
                return overlapUPA((XSElementDecl)decl2, (XSWildcardDecl)decl1, sgHandler);
            }
            return overlapUPA((XSWildcardDecl)decl1, (XSWildcardDecl)decl2);
        }
    }
    
    static {
        STRING_TYPE = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("string");
        XSConstraints.fEmptyParticle = null;
        ELEMENT_PARTICLE_COMPARATOR = new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                final XSParticleDecl pDecl1 = (XSParticleDecl)o1;
                final XSParticleDecl pDecl2 = (XSParticleDecl)o2;
                final XSElementDecl decl1 = (XSElementDecl)pDecl1.fValue;
                final XSElementDecl decl2 = (XSElementDecl)pDecl2.fValue;
                final String namespace1 = decl1.getNamespace();
                final String namespace2 = decl2.getNamespace();
                final String name1 = decl1.getName();
                final String name2 = decl2.getName();
                final boolean sameNamespace = namespace1 == namespace2;
                int namespaceComparison = 0;
                if (!sameNamespace) {
                    if (namespace1 != null) {
                        if (namespace2 != null) {
                            namespaceComparison = namespace1.compareTo(namespace2);
                        }
                        else {
                            namespaceComparison = 1;
                        }
                    }
                    else {
                        namespaceComparison = -1;
                    }
                }
                return (namespaceComparison != 0) ? namespaceComparison : name1.compareTo(name2);
            }
        };
    }
}
