package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.models.XSCMValidator;
import java.util.ArrayList;
import java.util.Stack;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.dv.XSSimpleType;

public abstract class XSConstraints
{
    static final int OCCURRENCE_UNKNOWN = -2;
    static final XSSimpleType STRING_TYPE;
    private static XSParticleDecl fEmptyParticle;
    static final XSConstraints XS_1_0_CONSTRAINTS;
    static final XSConstraints XS_1_0_CONSTRAINTS_EXTENDED;
    static final XSConstraints XS_1_1_CONSTRAINTS;
    private final XSComplexTypeDecl fAnyType;
    protected final short fSchemaVersion;
    
    public static XSParticleDecl getEmptySequence() {
        if (XSConstraints.fEmptyParticle == null) {
            final XSModelGroupImpl fValue = new XSModelGroupImpl();
            fValue.fCompositor = 102;
            fValue.fParticleCount = 0;
            fValue.fParticles = null;
            fValue.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            final XSParticleDecl fEmptyParticle = new XSParticleDecl();
            fEmptyParticle.fType = 3;
            fEmptyParticle.fValue = fValue;
            fEmptyParticle.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            XSConstraints.fEmptyParticle = fEmptyParticle;
        }
        return XSConstraints.fEmptyParticle;
    }
    
    protected XSConstraints(final XSComplexTypeDecl fAnyType, final short fSchemaVersion) {
        this.fAnyType = fAnyType;
        this.fSchemaVersion = fSchemaVersion;
    }
    
    public final short getSchemaVersion() {
        return this.fSchemaVersion;
    }
    
    public boolean isTypeTablesEquivalent(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2) {
        return true;
    }
    
    public boolean checkTypeDerivationOk(final XSTypeDefinition xsTypeDefinition, XSTypeDefinition fAnySimpleType, final short n) {
        if (xsTypeDefinition == this.fAnyType) {
            return xsTypeDefinition == fAnySimpleType;
        }
        if (xsTypeDefinition == SchemaGrammar.fAnySimpleType) {
            return fAnySimpleType == this.fAnyType || fAnySimpleType == SchemaGrammar.fAnySimpleType;
        }
        if (xsTypeDefinition.getTypeCategory() == 16) {
            if (fAnySimpleType.getTypeCategory() == 15) {
                if (fAnySimpleType != this.fAnyType) {
                    return false;
                }
                fAnySimpleType = SchemaGrammar.fAnySimpleType;
            }
            return this.checkSimpleDerivation((XSSimpleType)xsTypeDefinition, (XSSimpleType)fAnySimpleType, n);
        }
        return this.checkComplexDerivation((XSComplexTypeDecl)xsTypeDefinition, fAnySimpleType, n);
    }
    
    public boolean checkSimpleDerivationOk(final XSSimpleType xsSimpleType, XSTypeDefinition fAnySimpleType, final short n) {
        if (xsSimpleType == SchemaGrammar.fAnySimpleType) {
            return fAnySimpleType == this.fAnyType || fAnySimpleType == SchemaGrammar.fAnySimpleType;
        }
        if (fAnySimpleType.getTypeCategory() == 15) {
            if (fAnySimpleType != this.fAnyType) {
                return false;
            }
            fAnySimpleType = SchemaGrammar.fAnySimpleType;
        }
        return this.checkSimpleDerivation(xsSimpleType, (XSSimpleType)fAnySimpleType, n);
    }
    
    public boolean checkComplexDerivationOk(final XSComplexTypeDecl xsComplexTypeDecl, final XSTypeDefinition xsTypeDefinition, final short n) {
        if (xsComplexTypeDecl == this.fAnyType) {
            return xsComplexTypeDecl == xsTypeDefinition;
        }
        return this.checkComplexDerivation(xsComplexTypeDecl, xsTypeDefinition, n);
    }
    
    private boolean checkSimpleDerivation(final XSSimpleType xsSimpleType, XSSimpleType xsSimpleType2, final short n) {
        if (xsSimpleType == xsSimpleType2) {
            return true;
        }
        if ((n & 0x2) != 0x0 || (xsSimpleType.getBaseType().getFinal() & 0x2) != 0x0) {
            return false;
        }
        final XSSimpleType xsSimpleType3 = (XSSimpleType)xsSimpleType.getBaseType();
        if (xsSimpleType3 == xsSimpleType2) {
            return true;
        }
        if (xsSimpleType3 != SchemaGrammar.fAnySimpleType && this.checkSimpleDerivation(xsSimpleType3, xsSimpleType2, n)) {
            return true;
        }
        if ((xsSimpleType.getVariety() == 2 || xsSimpleType.getVariety() == 3) && xsSimpleType2 == SchemaGrammar.fAnySimpleType) {
            return true;
        }
        if (xsSimpleType2.getVariety() == 3 && this.checkEmptyFacets(xsSimpleType2)) {
            final XSObjectList memberTypes = xsSimpleType2.getMemberTypes();
            for (int length = memberTypes.getLength(), i = 0; i < length; ++i) {
                xsSimpleType2 = (XSSimpleType)memberTypes.item(i);
                if (this.checkSimpleDerivation(xsSimpleType, xsSimpleType2, n)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean checkComplexDerivation(final XSComplexTypeDecl xsComplexTypeDecl, XSTypeDefinition fAnySimpleType, final short n) {
        if (xsComplexTypeDecl == fAnySimpleType) {
            return true;
        }
        if ((xsComplexTypeDecl.fDerivedBy & n) != 0x0) {
            return false;
        }
        final XSTypeDefinition fBaseType = xsComplexTypeDecl.fBaseType;
        if (fBaseType == fAnySimpleType) {
            return true;
        }
        if (fBaseType == this.fAnyType || fBaseType == SchemaGrammar.fAnySimpleType) {
            return false;
        }
        if (fBaseType.getTypeCategory() == 15) {
            return this.checkComplexDerivation((XSComplexTypeDecl)fBaseType, fAnySimpleType, n);
        }
        if (fBaseType.getTypeCategory() == 16) {
            if (fAnySimpleType.getTypeCategory() == 15) {
                if (fAnySimpleType != this.fAnyType) {
                    return false;
                }
                fAnySimpleType = SchemaGrammar.fAnySimpleType;
            }
            return this.checkSimpleDerivation((XSSimpleType)fBaseType, (XSSimpleType)fAnySimpleType, n);
        }
        return false;
    }
    
    public Object ElementDefaultValidImmediate(final XSTypeDefinition xsTypeDefinition, final String s, final ValidationContext validationContext, final ValidatedInfo validatedInfo) {
        XSSimpleType xsSimpleType = null;
        if (xsTypeDefinition.getTypeCategory() == 16) {
            xsSimpleType = (XSSimpleType)xsTypeDefinition;
        }
        else {
            final XSComplexTypeDecl xsComplexTypeDecl = (XSComplexTypeDecl)xsTypeDefinition;
            if (xsComplexTypeDecl.fContentType == 1) {
                xsSimpleType = xsComplexTypeDecl.fXSSimpleType;
            }
            else {
                if (xsComplexTypeDecl.fContentType != 3) {
                    return null;
                }
                if (!((XSParticleDecl)xsComplexTypeDecl.getParticle()).emptiable()) {
                    return null;
                }
            }
        }
        if (xsSimpleType == null) {
            xsSimpleType = XSConstraints.STRING_TYPE;
        }
        Object o;
        try {
            o = xsSimpleType.validate(s, validationContext, validatedInfo);
            if (validatedInfo != null) {
                o = xsSimpleType.validate(validatedInfo.stringValue(), validationContext, validatedInfo);
            }
        }
        catch (final InvalidDatatypeValueException ex) {
            return null;
        }
        return o;
    }
    
    void reportSchemaError(final XMLErrorReporter xmlErrorReporter, final SimpleLocator simpleLocator, final String s, final Object[] array) {
        if (simpleLocator != null) {
            xmlErrorReporter.reportError(simpleLocator, "http://www.w3.org/TR/xml-schema-1", s, array, (short)1);
        }
        else {
            xmlErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", s, array, (short)1);
        }
    }
    
    public void fullSchemaChecking(final XSGrammarBucket xsGrammarBucket, final SubstitutionGroupHandler substitutionGroupHandler, final CMBuilder cmBuilder, final XMLErrorReporter xmlErrorReporter) {
        final SchemaGrammar[] grammars = xsGrammarBucket.getGrammars();
        for (int i = grammars.length - 1; i >= 0; --i) {
            substitutionGroupHandler.addSubstitutionGroup(grammars[i].getSubstitutionGroups());
        }
        final XSParticleDecl xsParticleDecl = new XSParticleDecl();
        final XSParticleDecl xsParticleDecl2 = new XSParticleDecl();
        xsParticleDecl.fType = 3;
        xsParticleDecl2.fType = 3;
        for (int j = grammars.length - 1; j >= 0; --j) {
            final XSGroupDecl[] redefinedGroupDecls = grammars[j].getRedefinedGroupDecls();
            final SimpleLocator[] rgLocators = grammars[j].getRGLocators();
            int k = 0;
            while (k < redefinedGroupDecls.length) {
                final XSGroupDecl xsGroupDecl = redefinedGroupDecls[k++];
                final XSModelGroupImpl fModelGroup = xsGroupDecl.fModelGroup;
                final XSModelGroupImpl fModelGroup2 = redefinedGroupDecls[k++].fModelGroup;
                xsParticleDecl.fValue = fModelGroup;
                if ((xsParticleDecl2.fValue = fModelGroup2) == null) {
                    if (fModelGroup == null) {
                        continue;
                    }
                    this.reportSchemaError(xmlErrorReporter, rgLocators[k / 2 - 1], "src-redefine.6.2.2", new Object[] { xsGroupDecl.fName, "rcase-Recurse.2" });
                }
                else if (fModelGroup == null) {
                    if (xsParticleDecl2.emptiable()) {
                        continue;
                    }
                    this.reportSchemaError(xmlErrorReporter, rgLocators[k / 2 - 1], "src-redefine.6.2.2", new Object[] { xsGroupDecl.fName, "rcase-Recurse.2" });
                }
                else {
                    this.groupSubsumption(xsParticleDecl, xsParticleDecl2, xsGrammarBucket, substitutionGroupHandler, cmBuilder, xmlErrorReporter, xsGroupDecl.fName, rgLocators[k / 2 - 1]);
                }
            }
        }
        final SymbolHash symbolHash = new SymbolHash();
        final Stack stack = new Stack();
        final ArrayList list = (this.fSchemaVersion == 4) ? new ArrayList() : null;
        for (int l = grammars.length - 1; l >= 0; --l) {
            int uncheckedTypeNum = 0;
            final boolean fFullChecked = grammars[l].fFullChecked;
            final XSComplexTypeDecl[] uncheckedComplexTypeDecls = grammars[l].getUncheckedComplexTypeDecls();
            final SimpleLocator[] uncheckedCTLocators = grammars[l].getUncheckedCTLocators();
            for (int n = 0; n < uncheckedComplexTypeDecls.length; ++n) {
                if (!fFullChecked && uncheckedComplexTypeDecls[n].fParticle != null) {
                    symbolHash.clear();
                    try {
                        this.checkElementDeclsConsistent(uncheckedComplexTypeDecls[n], uncheckedComplexTypeDecls[n].fParticle, symbolHash, substitutionGroupHandler, xsGrammarBucket, list, stack);
                    }
                    catch (final XMLSchemaException ex) {
                        this.reportSchemaError(xmlErrorReporter, uncheckedCTLocators[n], ex.getKey(), ex.getArgs());
                    }
                }
                if (uncheckedComplexTypeDecls[n].fBaseType != null && uncheckedComplexTypeDecls[n].fBaseType != this.fAnyType && uncheckedComplexTypeDecls[n].fDerivedBy == 2 && uncheckedComplexTypeDecls[n].fBaseType instanceof XSComplexTypeDecl) {
                    final XSParticleDecl fParticle = uncheckedComplexTypeDecls[n].fParticle;
                    final XSComplexTypeDecl xsComplexTypeDecl = (XSComplexTypeDecl)uncheckedComplexTypeDecls[n].fBaseType;
                    final XSParticleDecl fParticle2 = xsComplexTypeDecl.fParticle;
                    if (fParticle == null) {
                        if (fParticle2 != null && !fParticle2.emptiable()) {
                            this.reportSchemaError(xmlErrorReporter, uncheckedCTLocators[n], "derivation-ok-restriction.5.3.2", new Object[] { uncheckedComplexTypeDecls[n].fName, uncheckedComplexTypeDecls[n].fBaseType.getName() });
                        }
                    }
                    else if (fParticle2 != null) {
                        this.typeSubsumption(uncheckedComplexTypeDecls[n], xsComplexTypeDecl, xsGrammarBucket, substitutionGroupHandler, cmBuilder, xmlErrorReporter, uncheckedCTLocators[n]);
                    }
                    else {
                        this.reportSchemaError(xmlErrorReporter, uncheckedCTLocators[n], "derivation-ok-restriction.5.4.2", new Object[] { uncheckedComplexTypeDecls[n].fName });
                    }
                }
                final XSCMValidator contentModel = uncheckedComplexTypeDecls[n].getContentModel(cmBuilder, true);
                boolean checkUniqueParticleAttribution = false;
                if (contentModel != null) {
                    try {
                        checkUniqueParticleAttribution = contentModel.checkUniqueParticleAttribution(substitutionGroupHandler, this);
                    }
                    catch (final XMLSchemaException ex2) {
                        this.reportSchemaError(xmlErrorReporter, uncheckedCTLocators[n], ex2.getKey(), ex2.getArgs());
                    }
                }
                if (!fFullChecked && checkUniqueParticleAttribution) {
                    uncheckedComplexTypeDecls[uncheckedTypeNum++] = uncheckedComplexTypeDecls[n];
                }
            }
            if (!fFullChecked) {
                grammars[l].setUncheckedTypeNum(uncheckedTypeNum);
                grammars[l].fFullChecked = true;
            }
        }
    }
    
    public void checkElementDeclsConsistent(final XSComplexTypeDecl xsComplexTypeDecl, final XSParticleDecl xsParticleDecl, final SymbolHash symbolHash, final SubstitutionGroupHandler substitutionGroupHandler) throws XMLSchemaException {
        final short fType = xsParticleDecl.fType;
        if (fType == 2) {
            return;
        }
        if (fType == 1) {
            final XSElementDecl xsElementDecl = (XSElementDecl)xsParticleDecl.fValue;
            this.findElemInTable(xsComplexTypeDecl, xsElementDecl, symbolHash);
            if (xsElementDecl.fScope == 1) {
                final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
                for (int i = 0; i < substitutionGroup.length; ++i) {
                    this.findElemInTable(xsComplexTypeDecl, substitutionGroup[i], symbolHash);
                }
            }
            return;
        }
        final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
        for (int j = 0; j < xsModelGroupImpl.fParticleCount; ++j) {
            this.checkElementDeclsConsistent(xsComplexTypeDecl, xsModelGroupImpl.fParticles[j], symbolHash, substitutionGroupHandler);
        }
    }
    
    protected void checkElementDeclsConsistent(final XSComplexTypeDecl xsComplexTypeDecl, XSParticleDecl xsParticleDecl, final SymbolHash symbolHash, final SubstitutionGroupHandler substitutionGroupHandler, final XSGrammarBucket xsGrammarBucket, final ArrayList list, final Stack stack) throws XMLSchemaException {
        if (stack.size() > 0) {
            stack.clear();
        }
        while (true) {
            final short fType = xsParticleDecl.fType;
            if (fType != 2) {
                if (fType == 1) {
                    final XSElementDecl xsElementDecl = (XSElementDecl)xsParticleDecl.fValue;
                    this.findElemInTable(xsComplexTypeDecl, xsElementDecl, symbolHash);
                    if (xsElementDecl.fScope == 1) {
                        final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
                        for (int i = 0; i < substitutionGroup.length; ++i) {
                            this.findElemInTable(xsComplexTypeDecl, substitutionGroup[i], symbolHash);
                        }
                    }
                }
                else {
                    final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
                    for (int j = xsModelGroupImpl.fParticleCount - 1; j >= 0; --j) {
                        stack.push(xsModelGroupImpl.fParticles[j]);
                    }
                }
            }
            if (stack.isEmpty()) {
                break;
            }
            xsParticleDecl = stack.pop();
        }
    }
    
    public void findElemInTable(final XSComplexTypeDecl xsComplexTypeDecl, final XSElementDecl xsElementDecl, final SymbolHash symbolHash) throws XMLSchemaException {
        final XSElementDecl existingElement = this.findExistingElement(xsElementDecl, symbolHash);
        if (existingElement == null || existingElement == xsElementDecl) {
            return;
        }
        if (xsElementDecl.fType != existingElement.fType) {
            throw new XMLSchemaException("cos-element-consistent", new Object[] { xsComplexTypeDecl.fName, xsElementDecl.fName });
        }
    }
    
    protected XSElementDecl findExistingElement(final XSElementDecl xsElementDecl, final SymbolHash symbolHash) {
        final String string = xsElementDecl.fName + "," + xsElementDecl.fTargetNamespace;
        final XSElementDecl xsElementDecl2 = (XSElementDecl)symbolHash.get(string);
        if (xsElementDecl2 == null) {
            symbolHash.put(string, xsElementDecl);
        }
        return xsElementDecl2;
    }
    
    protected boolean overlapUPA(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2, final SubstitutionGroupHandler substitutionGroupHandler) {
        if (xsElementDecl.fName == xsElementDecl2.fName && xsElementDecl.fTargetNamespace == xsElementDecl2.fTargetNamespace) {
            return true;
        }
        final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
        for (int i = substitutionGroup.length - 1; i >= 0; --i) {
            if (substitutionGroup[i].fName == xsElementDecl2.fName && substitutionGroup[i].fTargetNamespace == xsElementDecl2.fTargetNamespace) {
                return true;
            }
        }
        final XSElementDecl[] substitutionGroup2 = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl2, this.fSchemaVersion);
        for (int j = substitutionGroup2.length - 1; j >= 0; --j) {
            if (substitutionGroup2[j].fName == xsElementDecl.fName && substitutionGroup2[j].fTargetNamespace == xsElementDecl.fTargetNamespace) {
                return true;
            }
        }
        for (int k = substitutionGroup.length - 1; k >= 0; --k) {
            for (int l = substitutionGroup2.length - 1; l >= 0; --l) {
                if (substitutionGroup[k].fName == substitutionGroup2[l].fName && substitutionGroup[k].fTargetNamespace == substitutionGroup2[l].fTargetNamespace) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean overlapUPA(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        final XSWildcardDecl performIntersectionWith = this.performIntersectionWith(xsWildcardDecl, xsWildcardDecl2, xsWildcardDecl.fProcessContents);
        return performIntersectionWith == null || performIntersectionWith.fType != 3 || performIntersectionWith.fNamespaceList.length != 0;
    }
    
    public boolean overlapUPA(final Object o, final Object o2, final SubstitutionGroupHandler substitutionGroupHandler) {
        if (o instanceof XSElementDecl) {
            if (o2 instanceof XSElementDecl) {
                return this.overlapUPA((XSElementDecl)o, (XSElementDecl)o2, substitutionGroupHandler);
            }
            return this.overlapUPA((XSElementDecl)o, (XSWildcardDecl)o2, substitutionGroupHandler);
        }
        else {
            if (o2 instanceof XSElementDecl) {
                return this.overlapUPA((XSElementDecl)o2, (XSWildcardDecl)o, substitutionGroupHandler);
            }
            return this.overlapUPA((XSWildcardDecl)o, (XSWildcardDecl)o2);
        }
    }
    
    boolean areSame(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        if (xsWildcardDecl.fType == xsWildcardDecl2.fType) {
            if (xsWildcardDecl.fType == 1) {
                return true;
            }
            if (xsWildcardDecl.fType == 2) {
                return xsWildcardDecl.fNamespaceList[0] == xsWildcardDecl2.fNamespaceList[0];
            }
            if (xsWildcardDecl.fNamespaceList.length == xsWildcardDecl2.fNamespaceList.length) {
                for (int i = 0; i < xsWildcardDecl.fNamespaceList.length; ++i) {
                    if (!this.elementInSet(xsWildcardDecl.fNamespaceList[i], xsWildcardDecl2.fNamespaceList)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    String[] intersect2sets(final String[] array, final String[] array2) {
        final String[] array3 = new String[Math.min(array.length, array2.length)];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (this.elementInSet(array[i], array2)) {
                array3[n++] = array[i];
            }
        }
        final String[] array4 = new String[n];
        System.arraycopy(array3, 0, array4, 0, n);
        return array4;
    }
    
    String[] union2sets(final String[] array, final String[] array2) {
        final String[] array3 = new String[array.length];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (!this.elementInSet(array[i], array2)) {
                array3[n++] = array[i];
            }
        }
        final String[] array4 = new String[n + array2.length];
        System.arraycopy(array3, 0, array4, 0, n);
        System.arraycopy(array2, 0, array4, n, array2.length);
        return array4;
    }
    
    boolean subset2sets(final String[] array, final String[] array2) {
        for (int i = 0; i < array.length; ++i) {
            if (!this.elementInSet(array[i], array2)) {
                return false;
            }
        }
        return true;
    }
    
    boolean elementInSet(final String s, final String[] array) {
        boolean b = false;
        for (int n = 0; n < array.length && !b; ++n) {
            if (s == array[n]) {
                b = true;
            }
        }
        return b;
    }
    
    boolean disjoint2sets(final String[] array, final String[] array2) {
        for (int i = 0; i < array.length; ++i) {
            if (this.elementInSet(array[i], array2)) {
                return false;
            }
        }
        return true;
    }
    
    public abstract boolean isSubsetOf(final XSWildcardDecl p0, final XSWildcardDecl p1);
    
    public abstract XSWildcardDecl performUnionWith(final XSWildcardDecl p0, final XSWildcardDecl p1, final short p2);
    
    public abstract XSWildcardDecl performIntersectionWith(final XSWildcardDecl p0, final XSWildcardDecl p1, final short p2);
    
    protected abstract boolean checkEmptyFacets(final XSSimpleType p0);
    
    public abstract boolean overlapUPA(final XSElementDecl p0, final XSWildcardDecl p1, final SubstitutionGroupHandler p2);
    
    protected abstract void groupSubsumption(final XSParticleDecl p0, final XSParticleDecl p1, final XSGrammarBucket p2, final SubstitutionGroupHandler p3, final CMBuilder p4, final XMLErrorReporter p5, final String p6, final SimpleLocator p7);
    
    protected abstract void typeSubsumption(final XSComplexTypeDecl p0, final XSComplexTypeDecl p1, final XSGrammarBucket p2, final SubstitutionGroupHandler p3, final CMBuilder p4, final XMLErrorReporter p5, final SimpleLocator p6);
    
    static {
        STRING_TYPE = (XSSimpleType)SchemaGrammar.getS4SGrammar((short)1).getGlobalTypeDecl("string");
        XSConstraints.fEmptyParticle = null;
        XS_1_0_CONSTRAINTS = new XS10Constraints((short)1);
        XS_1_0_CONSTRAINTS_EXTENDED = new XS10Constraints((short)2);
        XS_1_1_CONSTRAINTS = new XS11Constraints();
    }
}
