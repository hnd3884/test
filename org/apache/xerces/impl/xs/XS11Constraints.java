package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.models.XS11CMRestriction;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.xni.QName;
import java.util.Enumeration;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xs.XSTerm;
import java.util.Stack;
import java.util.ArrayList;
import org.apache.xerces.util.SymbolHash;

class XS11Constraints extends XSConstraints
{
    public XS11Constraints() {
        super(SchemaGrammar.getXSAnyType((short)4), (short)4);
    }
    
    public boolean overlapUPA(final XSElementDecl xsElementDecl, final XSWildcardDecl xsWildcardDecl, final SubstitutionGroupHandler substitutionGroupHandler) {
        return false;
    }
    
    protected final void checkElementDeclsConsistent(final XSComplexTypeDecl xsComplexTypeDecl, final XSParticleDecl xsParticleDecl, final SymbolHash symbolHash, final SubstitutionGroupHandler substitutionGroupHandler, final XSGrammarBucket xsGrammarBucket, final ArrayList list, final Stack stack) throws XMLSchemaException {
        if (xsParticleDecl.fType == 2) {
            return;
        }
        if (list.size() > 0) {
            list.clear();
        }
        if (stack.size() > 0) {
            stack.clear();
        }
        if (xsComplexTypeDecl.fOpenContent != null) {
            final XSWildcardDecl fWildcard = xsComplexTypeDecl.fOpenContent.fWildcard;
            if (fWildcard != null && fWildcard.fProcessContents != 2) {
                list.add(fWildcard);
            }
        }
        if (xsParticleDecl.fType == 1) {
            stack.push(xsParticleDecl.fValue);
        }
        else {
            this.preprocessModelGroupParticle((XSModelGroupImpl)xsParticleDecl.fValue, list, stack);
        }
        while (!stack.empty()) {
            final XSElementDecl xsElementDecl = stack.pop();
            this.findElemInTable(xsComplexTypeDecl, xsElementDecl, symbolHash);
            if (xsElementDecl.fScope == 1) {
                final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
                for (int i = 0; i < substitutionGroup.length; ++i) {
                    this.findElemInTable(xsComplexTypeDecl, substitutionGroup[i], symbolHash);
                }
            }
            else {
                this.checkExtraEDCRules(xsComplexTypeDecl, xsElementDecl, xsGrammarBucket, list);
            }
        }
    }
    
    public final void findElemInTable(final XSComplexTypeDecl xsComplexTypeDecl, final XSElementDecl xsElementDecl, final SymbolHash symbolHash) throws XMLSchemaException {
        final XSElementDecl existingElement = this.findExistingElement(xsElementDecl, symbolHash);
        if (existingElement == null || existingElement == xsElementDecl) {
            return;
        }
        if (xsElementDecl.fType != existingElement.fType) {
            throw new XMLSchemaException("cos-element-consistent", new Object[] { xsComplexTypeDecl.fName, xsElementDecl.fName });
        }
        if (XS11TypeHelper.isTypeTablesComparable(xsElementDecl.getTypeAlternatives(), existingElement.getTypeAlternatives()) && !this.isTypeTablesEquivalent(xsElementDecl, existingElement)) {
            throw new XMLSchemaException("cos-element-consistent.4.b", new Object[] { xsComplexTypeDecl.fName, xsElementDecl.fName });
        }
    }
    
    protected void preprocessModelGroupParticle(final XSModelGroupImpl xsModelGroupImpl, final ArrayList list, final Stack stack) {
        for (int i = xsModelGroupImpl.fParticleCount - 1; i >= 0; --i) {
            final XSParticleDecl xsParticleDecl = xsModelGroupImpl.fParticles[i];
            final short fType = xsParticleDecl.fType;
            if (fType == 2) {
                final XSWildcardDecl xsWildcardDecl = (XSWildcardDecl)xsParticleDecl.fValue;
                if (xsWildcardDecl.fProcessContents != 2) {
                    list.add(xsWildcardDecl);
                }
            }
            else if (fType == 1) {
                stack.push(xsParticleDecl.fValue);
            }
            else {
                this.preprocessModelGroupParticle((XSModelGroupImpl)xsParticleDecl.fValue, list, stack);
            }
        }
    }
    
    private void checkExtraEDCRules(final XSComplexTypeDecl xsComplexTypeDecl, final XSElementDecl xsElementDecl, final XSGrammarBucket xsGrammarBucket, final ArrayList list) throws XMLSchemaException {
        for (int i = 0; i < list.size(); ++i) {
            if (((XSWildcardDecl)list.get(i)).allowName(xsElementDecl.fTargetNamespace, xsElementDecl.fName)) {
                final SchemaGrammar grammar = xsGrammarBucket.getGrammar(xsElementDecl.fTargetNamespace);
                if (grammar != null) {
                    final XSElementDecl globalElementDecl = grammar.getGlobalElementDecl(xsElementDecl.fName);
                    if (globalElementDecl != null && globalElementDecl != xsElementDecl && XS11TypeHelper.isTypeTablesComparable(xsElementDecl.getTypeAlternatives(), globalElementDecl.getTypeAlternatives()) && !this.isTypeTablesEquivalent(xsElementDecl, globalElementDecl)) {
                        throw new XMLSchemaException("cos-element-consistent.4.b", new Object[] { xsComplexTypeDecl.fName, xsElementDecl.fName });
                    }
                }
            }
        }
    }
    
    public final boolean isTypeTablesEquivalent(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2) {
        boolean typeAlternativesEquivalent = true;
        final XSTypeAlternativeImpl[] typeAlternatives = xsElementDecl.getTypeAlternatives();
        final XSTypeAlternativeImpl[] typeAlternatives2 = xsElementDecl2.getTypeAlternatives();
        if (typeAlternatives.length != typeAlternatives2.length) {
            typeAlternativesEquivalent = false;
        }
        if (typeAlternativesEquivalent) {
            for (int i = 0; i < typeAlternatives.length; ++i) {
                if (!this.isTypeAlternativesEquivalent(typeAlternatives[i], typeAlternatives2[i])) {
                    typeAlternativesEquivalent = false;
                    break;
                }
            }
        }
        if (typeAlternativesEquivalent && !xsElementDecl.isTypeTableOK()) {
            typeAlternativesEquivalent = this.isTypeAlternativesEquivalent(xsElementDecl.getDefaultTypeDefinition(), xsElementDecl2.getDefaultTypeDefinition());
        }
        return typeAlternativesEquivalent;
    }
    
    private boolean isTypeAlternativesEquivalent(final XSTypeAlternativeImpl xsTypeAlternativeImpl, final XSTypeAlternativeImpl xsTypeAlternativeImpl2) {
        final String xPathDefaultNamespace = xsTypeAlternativeImpl.getXPathDefaultNamespace();
        final String xPathDefaultNamespace2 = xsTypeAlternativeImpl2.getXPathDefaultNamespace();
        final String s = (xsTypeAlternativeImpl.getTest() == null) ? null : xsTypeAlternativeImpl.getTest().toString();
        final String s2 = (xsTypeAlternativeImpl2.getTest() == null) ? null : xsTypeAlternativeImpl2.getTest().toString();
        final XSTypeDefinition typeDefinition = xsTypeAlternativeImpl.getTypeDefinition();
        final XSTypeDefinition typeDefinition2 = xsTypeAlternativeImpl2.getTypeDefinition();
        final String baseURI = xsTypeAlternativeImpl.getBaseURI();
        final String baseURI2 = xsTypeAlternativeImpl2.getBaseURI();
        if (xPathDefaultNamespace != xPathDefaultNamespace2 || typeDefinition != typeDefinition2 || (s == null && s2 != null) || (s != null && !s.equals(s2)) || (baseURI == null && baseURI2 != null) || (baseURI != null && !baseURI.equals(baseURI2))) {
            return false;
        }
        final NamespaceSupport namespaceContext = xsTypeAlternativeImpl.getNamespaceContext();
        final NamespaceSupport namespaceContext2 = xsTypeAlternativeImpl2.getNamespaceContext();
        final Enumeration allPrefixes = namespaceContext.getAllPrefixes();
        final Enumeration allPrefixes2 = namespaceContext2.getAllPrefixes();
        while (allPrefixes.hasMoreElements()) {
            if (!allPrefixes2.hasMoreElements()) {
                return false;
            }
            final String s3 = allPrefixes.nextElement();
            final String s4 = allPrefixes2.nextElement();
            if (namespaceContext.getURI(s3) != namespaceContext2.getURI(s3) || namespaceContext.getURI(s4) != namespaceContext2.getURI(s4)) {
                return false;
            }
        }
        return !allPrefixes2.hasMoreElements();
    }
    
    public boolean isSubsetOf(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        if (xsWildcardDecl2 == null) {
            return false;
        }
        if (xsWildcardDecl2.fType != 1) {
            if (xsWildcardDecl.fType == 3) {
                if (xsWildcardDecl2.fType == 3) {
                    if (!this.subset2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList)) {
                        return false;
                    }
                }
                else if (!this.disjoint2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList)) {
                    return false;
                }
            }
            else {
                if (xsWildcardDecl.fType != 2) {
                    return false;
                }
                if (xsWildcardDecl2.fType != 2 || !this.subset2sets(xsWildcardDecl2.fNamespaceList, xsWildcardDecl.fNamespaceList)) {
                    return false;
                }
            }
        }
        return (!xsWildcardDecl2.fDisallowedDefined || xsWildcardDecl.fDisallowedDefined) && (!xsWildcardDecl2.fDisallowedSibling || !xsWildcardDecl.fDisallowedSibling) && (xsWildcardDecl2.fDisallowedNamesList == null || !this.allowedNames(xsWildcardDecl, xsWildcardDecl2));
    }
    
    public XSWildcardDecl performUnionWith(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2, final short fProcessContents) {
        if (xsWildcardDecl2 == null) {
            return null;
        }
        final XSWildcardDecl xsWildcardDecl3 = new XSWildcardDecl();
        xsWildcardDecl3.fProcessContents = fProcessContents;
        if (this.areSame(xsWildcardDecl, xsWildcardDecl2)) {
            xsWildcardDecl3.fType = xsWildcardDecl.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl.fNamespaceList;
        }
        else if (xsWildcardDecl.fType == 1 || xsWildcardDecl2.fType == 1) {
            xsWildcardDecl3.fType = 1;
        }
        else if (xsWildcardDecl.fType == 3 && xsWildcardDecl2.fType == 3) {
            xsWildcardDecl3.fType = 3;
            xsWildcardDecl3.fNamespaceList = this.union2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList);
        }
        else if (xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 2) {
            final String[] intersect2sets = this.intersect2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList);
            if (intersect2sets.length == 0) {
                xsWildcardDecl3.fType = 1;
            }
            else {
                xsWildcardDecl3.fType = 2;
                xsWildcardDecl3.fNamespaceList = intersect2sets;
            }
        }
        else {
            final String[] fNamespaceList = (xsWildcardDecl.fType == 2) ? this.difference2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList) : this.difference2sets(xsWildcardDecl2.fNamespaceList, xsWildcardDecl.fNamespaceList);
            if (fNamespaceList.length == 0) {
                xsWildcardDecl3.fType = 1;
            }
            else {
                xsWildcardDecl3.fType = 2;
                xsWildcardDecl3.fNamespaceList = fNamespaceList;
            }
        }
        xsWildcardDecl3.fDisallowedNamesList = this.disallowedNamesUnion(xsWildcardDecl, xsWildcardDecl2);
        xsWildcardDecl3.fDisallowedDefined = (xsWildcardDecl.fDisallowedDefined && xsWildcardDecl2.fDisallowedDefined);
        return xsWildcardDecl3;
    }
    
    public XSWildcardDecl performIntersectionWith(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2, final short fProcessContents) {
        if (xsWildcardDecl2 == null) {
            return null;
        }
        final XSWildcardDecl xsWildcardDecl3 = new XSWildcardDecl();
        xsWildcardDecl3.fProcessContents = fProcessContents;
        if (this.areSame(xsWildcardDecl, xsWildcardDecl2)) {
            xsWildcardDecl3.fType = xsWildcardDecl.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl.fNamespaceList;
        }
        else if (xsWildcardDecl.fType == 1 || xsWildcardDecl2.fType == 1) {
            XSWildcardDecl xsWildcardDecl4 = xsWildcardDecl;
            if (xsWildcardDecl.fType == 1) {
                xsWildcardDecl4 = xsWildcardDecl2;
            }
            xsWildcardDecl3.fType = xsWildcardDecl4.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl4.fNamespaceList;
        }
        else if (xsWildcardDecl.fType == 3 && xsWildcardDecl2.fType == 3) {
            xsWildcardDecl3.fType = 3;
            xsWildcardDecl3.fNamespaceList = this.intersect2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList);
        }
        else if (xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 2) {
            xsWildcardDecl3.fType = 2;
            xsWildcardDecl3.fNamespaceList = this.union2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList);
        }
        else {
            xsWildcardDecl3.fType = 3;
            xsWildcardDecl3.fNamespaceList = ((xsWildcardDecl.fType == 2) ? this.difference2sets(xsWildcardDecl2.fNamespaceList, xsWildcardDecl.fNamespaceList) : this.difference2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList));
        }
        xsWildcardDecl3.fDisallowedNamesList = this.disallowedNamesIntersection(xsWildcardDecl, xsWildcardDecl2);
        xsWildcardDecl3.fDisallowedDefined = (xsWildcardDecl.fDisallowedDefined || xsWildcardDecl2.fDisallowedDefined);
        return xsWildcardDecl3;
    }
    
    boolean areSame(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        if (xsWildcardDecl.fType == xsWildcardDecl2.fType) {
            if (xsWildcardDecl.fType == 1) {
                return true;
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
    
    private boolean allowedNames(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        for (int i = 0; i < xsWildcardDecl2.fDisallowedNamesList.length; ++i) {
            if (xsWildcardDecl.allowQName(xsWildcardDecl2.fDisallowedNamesList[i])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean disallowedNamespaces(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        if (xsWildcardDecl2.fType == 1) {
            return false;
        }
        if (xsWildcardDecl.fType == 1) {
            return true;
        }
        if (xsWildcardDecl.fType == 3) {
            for (int i = 0; i < xsWildcardDecl.fNamespaceList.length; ++i) {
                if (!xsWildcardDecl2.allowNamespace(xsWildcardDecl.fNamespaceList[i])) {
                    return true;
                }
            }
            return false;
        }
        if (xsWildcardDecl2.fType == 2) {
            for (int j = 0; j < xsWildcardDecl2.fNamespaceList.length; ++j) {
                if (xsWildcardDecl.allowNamespace(xsWildcardDecl2.fNamespaceList[j])) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private String[] difference2sets(final String[] array, final String[] array2) {
        final String[] array3 = new String[array.length];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (!this.elementInSet(array[i], array2)) {
                array3[n++] = array[i];
            }
        }
        final String[] array4 = new String[n];
        System.arraycopy(array3, 0, array4, 0, n);
        return array4;
    }
    
    private QName[] disallowedNamesUnion(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        final int n = (xsWildcardDecl.fDisallowedNamesList == null) ? 0 : xsWildcardDecl.fDisallowedNamesList.length;
        final int n2 = (xsWildcardDecl2.fDisallowedNamesList == null) ? 0 : xsWildcardDecl2.fDisallowedNamesList.length;
        final QName[] array = new QName[n + n2];
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            if (!xsWildcardDecl2.allowQName(xsWildcardDecl.fDisallowedNamesList[i])) {
                array[n3++] = xsWildcardDecl.fDisallowedNamesList[i];
            }
        }
        for (int j = 0; j < n2; ++j) {
            if (!xsWildcardDecl.allowQName(xsWildcardDecl2.fDisallowedNamesList[j])) {
                array[n3++] = xsWildcardDecl2.fDisallowedNamesList[j];
            }
        }
        final QName[] array2 = new QName[n3];
        System.arraycopy(array, 0, array2, 0, n3);
        return array2;
    }
    
    private QName[] disallowedNamesIntersection(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        final int n = (xsWildcardDecl.fDisallowedNamesList == null) ? 0 : xsWildcardDecl.fDisallowedNamesList.length;
        final int n2 = (xsWildcardDecl2.fDisallowedNamesList == null) ? 0 : xsWildcardDecl2.fDisallowedNamesList.length;
        final QName[] array = new QName[n + n2];
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            final QName qName = xsWildcardDecl.fDisallowedNamesList[i];
            if (xsWildcardDecl2.allowQName(qName)) {
                array[n3++] = qName;
            }
            else if (this.elementInSet(qName, xsWildcardDecl2.fDisallowedNamesList)) {
                array[n3++] = qName;
            }
        }
        for (int j = 0; j < n2; ++j) {
            if (xsWildcardDecl.allowQName(xsWildcardDecl2.fDisallowedNamesList[j])) {
                array[n3++] = xsWildcardDecl2.fDisallowedNamesList[j];
            }
        }
        final QName[] array2 = new QName[n3];
        System.arraycopy(array, 0, array2, 0, n3);
        return array2;
    }
    
    private boolean elementInSet(final QName qName, final QName[] array) {
        boolean b = false;
        for (int n = (array == null) ? 0 : array.length, n2 = 0; n2 < n && !b; ++n2) {
            if (qName.equals(array[n2])) {
                b = true;
            }
        }
        return b;
    }
    
    protected void groupSubsumption(final XSParticleDecl xsParticleDecl, final XSParticleDecl xsParticleDecl2, final XSGrammarBucket xsGrammarBucket, final SubstitutionGroupHandler substitutionGroupHandler, final CMBuilder cmBuilder, final XMLErrorReporter xmlErrorReporter, final String s, final SimpleLocator simpleLocator) {
        if (!new XS11CMRestriction(cmBuilder.getContentModel(xsParticleDecl2), cmBuilder.getContentModel(xsParticleDecl), substitutionGroupHandler, xsGrammarBucket, cmBuilder, this).check()) {
            this.reportSchemaError(xmlErrorReporter, simpleLocator, "src-redefine.6.2.2", new Object[] { s, "" });
        }
    }
    
    protected void typeSubsumption(final XSComplexTypeDecl xsComplexTypeDecl, final XSComplexTypeDecl xsComplexTypeDecl2, final XSGrammarBucket xsGrammarBucket, final SubstitutionGroupHandler substitutionGroupHandler, final CMBuilder cmBuilder, final XMLErrorReporter xmlErrorReporter, final SimpleLocator simpleLocator) {
        if (!new XS11CMRestriction(xsComplexTypeDecl2.getContentModel(cmBuilder), xsComplexTypeDecl.getContentModel(cmBuilder), substitutionGroupHandler, xsGrammarBucket, cmBuilder, this).check()) {
            this.reportSchemaError(xmlErrorReporter, simpleLocator, "derivation-ok-restriction.5.4.2", new Object[] { xsComplexTypeDecl.fName });
        }
    }
    
    protected final boolean checkEmptyFacets(final XSSimpleType xsSimpleType) {
        return xsSimpleType.getMultiValueFacets() == XSObjectListImpl.EMPTY_LIST;
    }
}
