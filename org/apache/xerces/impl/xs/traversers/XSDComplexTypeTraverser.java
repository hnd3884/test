package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.AnyURIDV;
import java.util.Enumeration;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.impl.xs.assertion.XSAssert;
import org.apache.xerces.impl.xs.assertion.Test;
import java.util.Vector;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.w3c.dom.Attr;
import org.apache.xerces.impl.xs.util.XInt;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;

class XSDComplexTypeTraverser extends XSDAbstractParticleTraverser
{
    private static final int GLOBAL_NUM = 13;
    private static XSParticleDecl fErrorContent;
    private static XSWildcardDecl fErrorWildcard;
    private String fName;
    private String fTargetNamespace;
    private short fDerivedBy;
    private short fFinal;
    private short fBlock;
    private short fContentType;
    private XSTypeDefinition fBaseType;
    private XSAttributeGroupDecl fAttrGrp;
    private XSSimpleType fXSSimpleType;
    private XSParticleDecl fParticle;
    private boolean fIsAbstract;
    private XSComplexTypeDecl fComplexTypeDecl;
    private XSAnnotationImpl[] fAnnotations;
    private XSOpenContentDecl fOpenContent;
    private XSAssertImpl[] fAssertions;
    private Object[] fGlobalStore;
    private int fGlobalStorePos;
    private static final boolean DEBUG = false;
    
    private static XSParticleDecl getErrorContent() {
        if (XSDComplexTypeTraverser.fErrorContent == null) {
            final XSParticleDecl xsParticleDecl = new XSParticleDecl();
            xsParticleDecl.fType = 2;
            xsParticleDecl.fValue = getErrorWildcard();
            xsParticleDecl.fMinOccurs = 0;
            xsParticleDecl.fMaxOccurs = -1;
            final XSModelGroupImpl fValue = new XSModelGroupImpl();
            fValue.fCompositor = 102;
            fValue.fParticleCount = 1;
            (fValue.fParticles = new XSParticleDecl[1])[0] = xsParticleDecl;
            final XSParticleDecl fErrorContent = new XSParticleDecl();
            fErrorContent.fType = 3;
            fErrorContent.fValue = fValue;
            XSDComplexTypeTraverser.fErrorContent = fErrorContent;
        }
        return XSDComplexTypeTraverser.fErrorContent;
    }
    
    private static XSWildcardDecl getErrorWildcard() {
        if (XSDComplexTypeTraverser.fErrorWildcard == null) {
            final XSWildcardDecl fErrorWildcard = new XSWildcardDecl();
            fErrorWildcard.fProcessContents = 2;
            XSDComplexTypeTraverser.fErrorWildcard = fErrorWildcard;
        }
        return XSDComplexTypeTraverser.fErrorWildcard;
    }
    
    XSDComplexTypeTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
        this.fName = null;
        this.fTargetNamespace = null;
        this.fDerivedBy = 2;
        this.fFinal = 0;
        this.fBlock = 0;
        this.fContentType = 0;
        this.fBaseType = null;
        this.fAttrGrp = null;
        this.fXSSimpleType = null;
        this.fParticle = null;
        this.fIsAbstract = false;
        this.fComplexTypeDecl = null;
        this.fAnnotations = null;
        this.fOpenContent = null;
        this.fAssertions = null;
        this.fGlobalStore = null;
        this.fGlobalStorePos = 0;
    }
    
    XSComplexTypeDecl traverseLocal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final XSObject xsObject) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final String genAnonTypeName = this.genAnonTypeName(element);
        this.contentBackup();
        final XSComplexTypeDecl traverseComplexTypeDecl = this.traverseComplexTypeDecl(element, genAnonTypeName, checkAttributes, xsDocumentInfo, schemaGrammar, xsObject);
        this.contentRestore();
        schemaGrammar.addComplexTypeDecl(traverseComplexTypeDecl, this.fSchemaHandler.element2Locator(element));
        traverseComplexTypeDecl.setIsAnonymous();
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return traverseComplexTypeDecl;
    }
    
    XSComplexTypeDecl traverseGlobal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        this.contentBackup();
        XSComplexTypeDecl traverseComplexTypeDecl = this.traverseComplexTypeDecl(element, s, checkAttributes, xsDocumentInfo, schemaGrammar, null);
        this.contentRestore();
        schemaGrammar.addComplexTypeDecl(traverseComplexTypeDecl, this.fSchemaHandler.element2Locator(element));
        if (s == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_COMPLEXTYPE, SchemaSymbols.ATT_NAME }, element);
            traverseComplexTypeDecl = null;
        }
        else {
            if (DOMUtil.getLocalName(DOMUtil.getParent(element)).equals(SchemaSymbols.ELT_REDEFINE)) {
                if (this.fSchemaHandler.fSchemaVersion == 4) {
                    ((XSComplexTypeDecl)traverseComplexTypeDecl.getBaseType()).setContext(traverseComplexTypeDecl);
                }
                schemaGrammar.addGlobalComplexTypeDecl(traverseComplexTypeDecl);
            }
            if (schemaGrammar.getGlobalTypeDecl(traverseComplexTypeDecl.getName()) == null) {
                schemaGrammar.addGlobalComplexTypeDecl(traverseComplexTypeDecl);
            }
            final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
            final XSTypeDefinition globalTypeDecl = schemaGrammar.getGlobalTypeDecl(traverseComplexTypeDecl.getName(), schemaDocument2SystemId);
            if (globalTypeDecl == null) {
                schemaGrammar.addGlobalComplexTypeDecl(traverseComplexTypeDecl, schemaDocument2SystemId);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (globalTypeDecl != null && globalTypeDecl instanceof XSComplexTypeDecl) {
                    traverseComplexTypeDecl = (XSComplexTypeDecl)globalTypeDecl;
                }
                this.fSchemaHandler.addGlobalTypeDecl(traverseComplexTypeDecl);
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return traverseComplexTypeDecl;
    }
    
    XSOpenContentDecl traverseOpenContent(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final boolean b) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, b, xsDocumentInfo);
        final XSOpenContentDecl xsOpenContentDecl = new XSOpenContentDecl();
        final short shortValue = ((XInt)checkAttributes[XSAttributeChecker.ATTIDX_MODE]).shortValue();
        if (b) {
            xsOpenContentDecl.fAppliesToEmpty = (boolean)checkAttributes[XSAttributeChecker.ATTIDX_APPLIESTOEMPTY];
        }
        xsOpenContentDecl.fMode = shortValue;
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null) {
            if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.addAnnotation(this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo));
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            else {
                final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                if (syntheticAnnotation != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo));
                }
            }
            if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.reportSchemaError("s4s-elt-invalid-content.1", new Object[] { SchemaSymbols.ELT_OPENCONTENT, SchemaSymbols.ELT_ANNOTATION }, element2);
            }
        }
        else {
            final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation2 != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation2, checkAttributes, false, xsDocumentInfo));
            }
        }
        if (element2 == null) {
            if (shortValue != 0) {
                this.reportSchemaError("src-ct.6", new Object[] { this.fName }, element);
            }
        }
        else {
            final String localName = DOMUtil.getLocalName(element2);
            if (!localName.equals(SchemaSymbols.ELT_ANY) || DOMUtil.getNextSiblingElement(element2) != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_OPENCONTENT, "(annotation?, any?)", localName }, element2);
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                return xsOpenContentDecl;
            }
            final Attr attr = DOMUtil.getAttr(element2, SchemaSymbols.ATT_MINOCCURS);
            final Attr attr2 = DOMUtil.getAttr(element2, SchemaSymbols.ATT_MAXOCCURS);
            if (attr != null || attr2 != null) {
                this.reportSchemaError("s4s-att-not-allowed", new Object[] { DOMUtil.getLocalName(element) + "=>" + SchemaSymbols.ELT_ANY, SchemaSymbols.ATT_MINOCCURS + "|" + SchemaSymbols.ATT_MAXOCCURS }, element2);
            }
            final Object[] checkAttributes2 = this.fAttrChecker.checkAttributes(element2, false, xsDocumentInfo);
            xsOpenContentDecl.fWildcard = this.fSchemaHandler.fWildCardTraverser.traverseWildcardDecl(element2, checkAttributes2, xsDocumentInfo, schemaGrammar);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            if (shortValue == 0) {
                xsOpenContentDecl.fWildcard = null;
                this.reportSchemaError("src-ct11.3", new Object[] { this.fName }, element);
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return xsOpenContentDecl;
    }
    
    private XSComplexTypeDecl traverseComplexTypeDecl(final Element element, final String fName, final Object[] array, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final XSObject context) {
        this.fComplexTypeDecl = new XSComplexTypeDecl();
        this.fAttrGrp = new XSAttributeGroupDecl();
        final Boolean b = (Boolean)array[XSAttributeChecker.ATTIDX_ABSTRACT];
        final XInt xInt = (XInt)array[XSAttributeChecker.ATTIDX_BLOCK];
        final Boolean b2 = (Boolean)array[XSAttributeChecker.ATTIDX_MIXED];
        final XInt xInt2 = (XInt)array[XSAttributeChecker.ATTIDX_FINAL];
        this.fName = fName;
        this.fComplexTypeDecl.setName(this.fName);
        this.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
        this.fBlock = ((xInt == null) ? xsDocumentInfo.fBlockDefault : xInt.shortValue());
        this.fFinal = ((xInt2 == null) ? xsDocumentInfo.fFinalDefault : xInt2.shortValue());
        this.fBlock &= 0x3;
        this.fFinal &= 0x3;
        this.fIsAbstract = (b != null && b);
        this.fAnnotations = null;
        this.fOpenContent = null;
        this.fAssertions = null;
        try {
            if (this.fSchemaHandler.fSchemaVersion == 4 && (boolean)array[XSAttributeChecker.ATTIDX_DEFAULTATTRAPPLY] && xsDocumentInfo.fDefaultAGroup != null) {
                this.mergeAttributes(xsDocumentInfo.fDefaultAGroup, this.fAttrGrp, this.fName, true, element);
            }
            Element element2 = DOMUtil.getFirstChildElement(element);
            if (element2 != null) {
                if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    this.addAnnotation(this.traverseAnnotationDecl(element2, array, false, xsDocumentInfo));
                    element2 = DOMUtil.getNextSiblingElement(element2);
                }
                else {
                    final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                    if (syntheticAnnotation != null) {
                        this.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo));
                    }
                }
                if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, element2);
                }
            }
            else {
                final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element);
                if (syntheticAnnotation2 != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation2, array, false, xsDocumentInfo));
                }
            }
            if (element2 == null) {
                this.fBaseType = SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion);
                this.fDerivedBy = 2;
                if (this.fSchemaHandler.fSchemaVersion == 4) {
                    this.fComplexTypeDecl.setBaseType(this.fBaseType);
                }
                this.processComplexContent(element2, b2, false, xsDocumentInfo, schemaGrammar);
            }
            else if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_SIMPLECONTENT)) {
                this.traverseSimpleContent(element2, xsDocumentInfo, schemaGrammar);
                final Element nextSiblingElement = DOMUtil.getNextSiblingElement(element2);
                if (nextSiblingElement != null) {
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
                }
            }
            else if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_COMPLEXCONTENT)) {
                this.traverseComplexContent(element2, b2, this.fSchemaHandler.fSchemaVersion == 4 && DOMUtil.getAttr(element, SchemaSymbols.ATT_MIXED) != null, xsDocumentInfo, schemaGrammar);
                final Element nextSiblingElement2 = DOMUtil.getNextSiblingElement(element2);
                if (nextSiblingElement2 != null) {
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(nextSiblingElement2) }, nextSiblingElement2);
                }
            }
            else {
                this.fBaseType = SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion);
                this.fDerivedBy = 2;
                if (this.fSchemaHandler.fSchemaVersion == 4) {
                    this.fComplexTypeDecl.setBaseType(this.fBaseType);
                }
                this.processComplexContent(element2, b2, false, xsDocumentInfo, schemaGrammar);
            }
        }
        catch (final ComplexTypeRecoverableError complexTypeRecoverableError) {
            this.handleComplexTypeError(complexTypeRecoverableError.getMessage(), complexTypeRecoverableError.errorSubstText, complexTypeRecoverableError.errorElem);
        }
        this.fComplexTypeDecl.setValues(this.fName, this.fTargetNamespace, this.fBaseType, this.fDerivedBy, this.fFinal, this.fBlock, this.fContentType, this.fIsAbstract, this.fAttrGrp, this.fXSSimpleType, this.fParticle, new XSObjectListImpl(this.fAnnotations, (this.fAnnotations == null) ? 0 : this.fAnnotations.length), this.fOpenContent);
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.fComplexTypeDecl.setContext(context);
        }
        this.fComplexTypeDecl.setAssertions((this.fAssertions != null) ? new XSObjectListImpl(this.fAssertions, this.fAssertions.length) : null);
        return this.fComplexTypeDecl;
    }
    
    private void traverseSimpleContent(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) throws ComplexTypeRecoverableError {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        this.fContentType = 1;
        this.fParticle = null;
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            this.addAnnotation(this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo));
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo));
            }
        }
        if (element2 == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[] { this.fName, SchemaSymbols.ELT_SIMPLECONTENT }, element);
        }
        final String localName = DOMUtil.getLocalName(element2);
        if (localName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            this.fDerivedBy = 2;
        }
        else {
            if (!localName.equals(SchemaSymbols.ELT_EXTENSION)) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, localName }, element2);
            }
            this.fDerivedBy = 1;
            if (this.fSchemaHandler.fSchemaVersion == 4) {
                this.fComplexTypeDecl.setDerivationMethod((short)1);
            }
        }
        final Element nextSiblingElement = DOMUtil.getNextSiblingElement(element2);
        if (nextSiblingElement != null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
        }
        final Object[] checkAttributes2 = this.fAttrChecker.checkAttributes(element2, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes2[XSAttributeChecker.ATTIDX_BASE];
        if (qName == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("s4s-att-must-appear", new Object[] { localName, "base" }, element2);
        }
        final XSTypeDefinition fBaseType = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 7, qName, element2);
        if (fBaseType == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError();
        }
        this.fBaseType = fBaseType;
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.fComplexTypeDecl.setBaseType(this.fBaseType);
        }
        XSSimpleType fxsSimpleType = null;
        XSComplexTypeDecl xsComplexTypeDecl = null;
        short n;
        if (fBaseType.getTypeCategory() == 15) {
            xsComplexTypeDecl = (XSComplexTypeDecl)fBaseType;
            n = xsComplexTypeDecl.getFinal();
            if (xsComplexTypeDecl.getContentType() == 1) {
                fxsSimpleType = (XSSimpleType)xsComplexTypeDecl.getSimpleType();
            }
            else if (this.fDerivedBy != 2 || xsComplexTypeDecl.getContentType() != 3 || !((XSParticleDecl)xsComplexTypeDecl.getParticle()).emptiable()) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("src-ct.2.1", new Object[] { this.fName, xsComplexTypeDecl.getName() }, element2);
            }
        }
        else {
            fxsSimpleType = (XSSimpleType)fBaseType;
            if (this.fDerivedBy == 2) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("src-ct.2.1", new Object[] { this.fName, fxsSimpleType.getName() }, element2);
            }
            n = fxsSimpleType.getFinal();
        }
        if ((n & this.fDerivedBy) != 0x0) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError((this.fDerivedBy == 1) ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1", new Object[] { this.fName, this.fBaseType.getName() }, element2);
        }
        final Element element3 = element2;
        Element element4 = DOMUtil.getFirstChildElement(element2);
        if (element4 != null) {
            if (DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.addAnnotation(this.traverseAnnotationDecl(element4, checkAttributes2, false, xsDocumentInfo));
                element4 = DOMUtil.getNextSiblingElement(element4);
            }
            else {
                final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element3);
                if (syntheticAnnotation2 != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(element3, syntheticAnnotation2, checkAttributes2, false, xsDocumentInfo));
                }
            }
            if (element4 != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, element4);
            }
        }
        else {
            final String syntheticAnnotation3 = DOMUtil.getSyntheticAnnotation(element3);
            if (syntheticAnnotation3 != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(element3, syntheticAnnotation3, checkAttributes2, false, xsDocumentInfo));
            }
        }
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.addAssertsFromBaseTypes(this.fBaseType);
        }
        if (this.fDerivedBy == 2) {
            boolean b = false;
            if (element4 != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                final XSSimpleType traverseLocal = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element4, xsDocumentInfo, schemaGrammar, null);
                if (traverseLocal == null) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError();
                }
                if (fxsSimpleType != null && !this.fSchemaHandler.fXSConstraints.checkSimpleDerivationOk(traverseLocal, fxsSimpleType, fxsSimpleType.getFinal())) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("derivation-ok-restriction.5.2.2.1", new Object[] { this.fName, traverseLocal.getName(), fxsSimpleType.getName() }, element4);
                }
                if (this.fSchemaHandler.fSchemaVersion == 4 && traverseLocal instanceof XSSimpleTypeDecl) {
                    b = true;
                }
                fxsSimpleType = traverseLocal;
                element4 = DOMUtil.getNextSiblingElement(element4);
            }
            else if (fxsSimpleType == SchemaGrammar.fAnySimpleType || fxsSimpleType == SchemaGrammar.fAnyAtomicType) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("cos-st-restricts.1.1", new Object[] { fxsSimpleType.getName(), this.genAnonTypeName(element) }, element);
            }
            if (fxsSimpleType == null) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("src-ct.2.2", new Object[] { this.fName }, element4);
            }
            Element nodeAfterFacets = null;
            XSFacets facetdata = null;
            int fPresentFacets = 0;
            int fFixedFacets = 0;
            if (element4 != null) {
                final FacetInfo traverseFacets = this.traverseFacets(element4, this.fComplexTypeDecl, fxsSimpleType, xsDocumentInfo);
                nodeAfterFacets = traverseFacets.nodeAfterFacets;
                facetdata = traverseFacets.facetdata;
                fPresentFacets = traverseFacets.fPresentFacets;
                fFixedFacets = traverseFacets.fFixedFacets;
            }
            final String genAnonTypeName = this.genAnonTypeName(element);
            this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(genAnonTypeName, xsDocumentInfo.fTargetNamespace, (short)0, fxsSimpleType, null);
            try {
                this.fValidationState.setNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
                this.fValidationState.setDatatypeXMLVersion(xsDocumentInfo.fDatatypeXMLVersion);
                this.fXSSimpleType.applyFacets(facetdata, fPresentFacets, fFixedFacets, this.fValidationState);
            }
            catch (final InvalidDatatypeFacetException ex) {
                this.reportSchemaError(ex.getKey(), ex.getArgs(), element4);
                this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(genAnonTypeName, xsDocumentInfo.fTargetNamespace, (short)0, fxsSimpleType, null);
            }
            if (this.fXSSimpleType instanceof XSSimpleTypeDecl) {
                ((XSSimpleTypeDecl)this.fXSSimpleType).setAnonymous(true);
            }
            if (b) {
                ((XSSimpleTypeDecl)fxsSimpleType).setContext(this.fXSSimpleType);
            }
            if (nodeAfterFacets != null) {
                if (this.isAttrOrAttrGroup(nodeAfterFacets)) {
                    final Element traverseAttrsAndAttrGrps = this.traverseAttrsAndAttrGrps(nodeAfterFacets, this.fAttrGrp, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                    if (traverseAttrsAndAttrGrps != null) {
                        if (!this.isAssert(traverseAttrsAndAttrGrps)) {
                            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(traverseAttrsAndAttrGrps) }, traverseAttrsAndAttrGrps);
                        }
                        this.traverseAsserts(traverseAttrsAndAttrGrps, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                    }
                }
                else {
                    if (!this.isAssert(nodeAfterFacets)) {
                        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                        this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(nodeAfterFacets) }, nodeAfterFacets);
                    }
                    this.traverseAsserts(nodeAfterFacets, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                }
            }
            try {
                this.mergeAttributes(xsComplexTypeDecl.getAttrGrp(), this.fAttrGrp, this.fName, false, element);
            }
            catch (final ComplexTypeRecoverableError complexTypeRecoverableError) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw complexTypeRecoverableError;
            }
            this.fAttrGrp.removeProhibitedAttrs();
            final Object[] validRestriction = this.fAttrGrp.validRestrictionOf(this.fName, xsComplexTypeDecl.getAttrGrp(), this.fSchemaHandler.fXSConstraints);
            if (validRestriction != null) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError((String)validRestriction[validRestriction.length - 1], validRestriction, nodeAfterFacets);
            }
        }
        else {
            this.fXSSimpleType = fxsSimpleType;
            if (element4 != null) {
                final Element element5 = element4;
                if (this.isAttrOrAttrGroup(element5)) {
                    final Element traverseAttrsAndAttrGrps2 = this.traverseAttrsAndAttrGrps(element5, this.fAttrGrp, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                    if (traverseAttrsAndAttrGrps2 != null) {
                        if (!this.isAssert(traverseAttrsAndAttrGrps2)) {
                            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(traverseAttrsAndAttrGrps2) }, traverseAttrsAndAttrGrps2);
                        }
                        this.traverseAsserts(traverseAttrsAndAttrGrps2, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                    }
                }
                else {
                    if (!this.isAssert(element5)) {
                        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                        this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element5) }, element5);
                    }
                    this.traverseAsserts(element5, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                }
                this.fAttrGrp.removeProhibitedAttrs();
            }
            if (xsComplexTypeDecl != null) {
                try {
                    this.mergeAttributes(xsComplexTypeDecl.getAttrGrp(), this.fAttrGrp, this.fName, true, element);
                }
                catch (final ComplexTypeRecoverableError complexTypeRecoverableError2) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw complexTypeRecoverableError2;
                }
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
    }
    
    private void traverseComplexContent(final Element element, final boolean b, final boolean b2, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) throws ComplexTypeRecoverableError {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        boolean booleanValue = b;
        final Boolean b3 = (Boolean)checkAttributes[XSAttributeChecker.ATTIDX_MIXED];
        if (b3 != null) {
            booleanValue = b3;
            if (b2 && booleanValue != b) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("src-ct11.4", new Object[] { this.fName }, element);
            }
        }
        this.fXSSimpleType = null;
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            this.addAnnotation(this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo));
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo));
            }
        }
        if (element2 == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[] { this.fName, SchemaSymbols.ELT_COMPLEXCONTENT }, element);
        }
        final String localName = DOMUtil.getLocalName(element2);
        if (localName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            this.fDerivedBy = 2;
        }
        else {
            if (!localName.equals(SchemaSymbols.ELT_EXTENSION)) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, localName }, element2);
            }
            this.fDerivedBy = 1;
            if (this.fSchemaHandler.fSchemaVersion == 4) {
                this.fComplexTypeDecl.setDerivationMethod((short)1);
            }
        }
        final Element nextSiblingElement = DOMUtil.getNextSiblingElement(element2);
        if (nextSiblingElement != null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
        }
        final Object[] checkAttributes2 = this.fAttrChecker.checkAttributes(element2, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes2[XSAttributeChecker.ATTIDX_BASE];
        if (qName == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("s4s-att-must-appear", new Object[] { localName, "base" }, element2);
        }
        final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 7, qName, element2);
        if (xsTypeDefinition == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError();
        }
        if (!(xsTypeDefinition instanceof XSComplexTypeDecl)) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError("src-ct.1", new Object[] { this.fName, xsTypeDefinition.getName() }, element2);
        }
        final XSComplexTypeDecl fBaseType = (XSComplexTypeDecl)xsTypeDefinition;
        this.fBaseType = fBaseType;
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.fComplexTypeDecl.setBaseType(this.fBaseType);
        }
        if ((fBaseType.getFinal() & this.fDerivedBy) != 0x0) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw new ComplexTypeRecoverableError((this.fDerivedBy == 1) ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1", new Object[] { this.fName, this.fBaseType.getName() }, element2);
        }
        Element element3 = DOMUtil.getFirstChildElement(element2);
        if (element3 != null) {
            if (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.addAnnotation(this.traverseAnnotationDecl(element3, checkAttributes2, false, xsDocumentInfo));
                element3 = DOMUtil.getNextSiblingElement(element3);
            }
            else {
                final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element3);
                if (syntheticAnnotation2 != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(element3, syntheticAnnotation2, checkAttributes2, false, xsDocumentInfo));
                }
            }
            if (element3 != null && DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, element3);
            }
        }
        else {
            final String syntheticAnnotation3 = DOMUtil.getSyntheticAnnotation(element3);
            if (syntheticAnnotation3 != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(element3, syntheticAnnotation3, checkAttributes2, false, xsDocumentInfo));
            }
        }
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.addAssertsFromBaseTypes(this.fBaseType);
        }
        try {
            this.processComplexContent(element3, booleanValue, true, xsDocumentInfo, schemaGrammar);
        }
        catch (final ComplexTypeRecoverableError complexTypeRecoverableError) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            throw complexTypeRecoverableError;
        }
        final XSParticleDecl fParticle = (XSParticleDecl)fBaseType.getParticle();
        XSOpenContentDecl fOpenContent = null;
        if (this.fDerivedBy == 2) {
            if (this.fContentType == 3 && fBaseType.getContentType() != 3) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw new ComplexTypeRecoverableError("derivation-ok-restriction.5.4.1.2", new Object[] { this.fName, fBaseType.getName() }, element3);
            }
            try {
                this.mergeAttributes(fBaseType.getAttrGrp(), this.fAttrGrp, this.fName, false, element3);
            }
            catch (final ComplexTypeRecoverableError complexTypeRecoverableError2) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw complexTypeRecoverableError2;
            }
            this.fAttrGrp.removeProhibitedAttrs();
            if (fBaseType != SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion)) {
                final Object[] validRestriction = this.fAttrGrp.validRestrictionOf(this.fName, fBaseType.getAttrGrp(), this.fSchemaHandler.fXSConstraints);
                if (validRestriction != null) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError((String)validRestriction[validRestriction.length - 1], validRestriction, element3);
                }
            }
        }
        else {
            if (this.fParticle == null) {
                this.fContentType = fBaseType.getContentType();
                this.fXSSimpleType = (XSSimpleType)fBaseType.getSimpleType();
                this.fParticle = fParticle;
                if (this.fSchemaHandler.fSchemaVersion == 4) {
                    fOpenContent = (XSOpenContentDecl)fBaseType.getOpenContent();
                }
            }
            else if (fBaseType.getContentType() != 0) {
                if (this.fContentType == 2 && fBaseType.getContentType() != 2) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.a", new Object[] { this.fName }, element3);
                }
                if (this.fContentType == 3 && fBaseType.getContentType() != 3) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.b", new Object[] { this.fName }, element3);
                }
                final boolean b4 = ((XSParticleDecl)fBaseType.getParticle()).fType == 3 && ((XSModelGroupImpl)((XSParticleDecl)fBaseType.getParticle()).fValue).fCompositor == 103;
                final boolean b5 = this.fParticle.fType == 3 && ((XSModelGroupImpl)this.fParticle.fValue).fCompositor == 103;
                if (b4 || b5) {
                    if (this.fSchemaHandler.fSchemaVersion != 4 || !b4 || !b5) {
                        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                        this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                        throw new ComplexTypeRecoverableError("cos-all-limited.1.2", new Object[0], element3);
                    }
                    if (this.fParticle.fMinOccurs != fParticle.fMinOccurs) {
                        throw new ComplexTypeRecoverableError("cos-particle-extends.3.1", new Object[0], element3);
                    }
                    final XSModelGroupImpl fValue = new XSModelGroupImpl();
                    fValue.fCompositor = 103;
                    fValue.fParticleCount = ((XSModelGroupImpl)fParticle.fValue).fParticleCount + ((XSModelGroupImpl)this.fParticle.fValue).fParticleCount;
                    fValue.fParticles = new XSParticleDecl[fValue.fParticleCount];
                    System.arraycopy(((XSModelGroupImpl)fParticle.fValue).fParticles, 0, fValue.fParticles, 0, ((XSModelGroupImpl)fParticle.fValue).fParticleCount);
                    System.arraycopy(((XSModelGroupImpl)this.fParticle.fValue).fParticles, 0, fValue.fParticles, ((XSModelGroupImpl)fParticle.fValue).fParticleCount, ((XSModelGroupImpl)this.fParticle.fValue).fParticleCount);
                    fValue.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                    final XSParticleDecl fParticle2 = new XSParticleDecl();
                    fParticle2.fType = 3;
                    fParticle2.fValue = fValue;
                    fParticle2.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                    fParticle2.fMinOccurs = this.fParticle.fMinOccurs;
                    this.fParticle = fParticle2;
                    fOpenContent = (XSOpenContentDecl)fBaseType.getOpenContent();
                }
                else {
                    final XSModelGroupImpl fValue2 = new XSModelGroupImpl();
                    fValue2.fCompositor = 102;
                    fValue2.fParticleCount = 2;
                    (fValue2.fParticles = new XSParticleDecl[2])[0] = (XSParticleDecl)fBaseType.getParticle();
                    fValue2.fParticles[1] = this.fParticle;
                    fValue2.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                    final XSParticleDecl fParticle3 = new XSParticleDecl();
                    fParticle3.fType = 3;
                    fParticle3.fValue = fValue2;
                    fParticle3.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                    this.fParticle = fParticle3;
                    if (this.fSchemaHandler.fSchemaVersion == 4) {
                        fOpenContent = (XSOpenContentDecl)fBaseType.getOpenContent();
                    }
                }
            }
            this.fAttrGrp.removeProhibitedAttrs();
            try {
                this.mergeAttributes(fBaseType.getAttrGrp(), this.fAttrGrp, this.fName, true, element3);
            }
            catch (final ComplexTypeRecoverableError complexTypeRecoverableError3) {
                this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                throw complexTypeRecoverableError3;
            }
        }
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            final XSOpenContentDecl xsOpenContentDecl = (XSOpenContentDecl)fBaseType.getOpenContent();
            if (this.fOpenContent == null && xsDocumentInfo.fDefaultOpenContent != null && (this.fContentType != 0 || xsDocumentInfo.fDefaultOpenContent.fAppliesToEmpty)) {
                this.fOpenContent = xsDocumentInfo.fDefaultOpenContent;
            }
            if (this.fOpenContent == null || this.fOpenContent.fMode == 0) {
                this.fOpenContent = fOpenContent;
            }
            else {
                if (this.fContentType == 0) {
                    this.fParticle = XSConstraints.getEmptySequence();
                    this.fContentType = 2;
                }
                if (fOpenContent != null && this.fOpenContent.fWildcard != null) {
                    final XSOpenContentDecl fOpenContent2 = new XSOpenContentDecl();
                    fOpenContent2.fMode = this.fOpenContent.fMode;
                    fOpenContent2.fWildcard = this.fSchemaHandler.fXSConstraints.performUnionWith(this.fOpenContent.fWildcard, fOpenContent.fWildcard, this.fOpenContent.fWildcard.fProcessContents);
                    fOpenContent2.fWildcard.fAnnotations = this.fOpenContent.fWildcard.fAnnotations;
                    this.fOpenContent = fOpenContent2;
                }
            }
            if (this.fDerivedBy == 1 && fBaseType.getContentType() != 0 && xsOpenContentDecl != null && this.fOpenContent != xsOpenContentDecl) {
                if (this.fOpenContent == null) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.3", new Object[] { this.fName }, element3);
                }
                if (this.fOpenContent.fMode == 2 && xsOpenContentDecl.fMode != 2) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.3.3", new Object[] { this.fName }, element3);
                }
                if (!this.fSchemaHandler.fXSConstraints.isSubsetOf(xsOpenContentDecl.fWildcard, this.fOpenContent.fWildcard)) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.3.4", new Object[] { this.fName }, element3);
                }
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
    }
    
    private void mergeAttributes(final XSAttributeGroupDecl xsAttributeGroupDecl, final XSAttributeGroupDecl xsAttributeGroupDecl2, final String s, final boolean b, final Element element) throws ComplexTypeRecoverableError {
        final XSObjectList attributeUses = xsAttributeGroupDecl.getAttributeUses();
        for (int length = attributeUses.getLength(), i = 0; i < length; ++i) {
            final XSAttributeUseImpl xsAttributeUseImpl = (XSAttributeUseImpl)attributeUses.item(i);
            final XSAttributeUse attributeUse = xsAttributeGroupDecl2.getAttributeUse(xsAttributeUseImpl.fAttrDecl.getNamespace(), xsAttributeUseImpl.fAttrDecl.getName());
            if (attributeUse == null) {
                final String addAttributeUse = xsAttributeGroupDecl2.addAttributeUse(xsAttributeUseImpl, this.fSchemaHandler.fSchemaVersion == 4);
                if (addAttributeUse != null) {
                    throw new ComplexTypeRecoverableError("ct-props-correct.5", new Object[] { s, addAttributeUse, xsAttributeUseImpl.fAttrDecl.getName() }, element);
                }
            }
            else if (attributeUse != xsAttributeUseImpl && b) {
                this.reportSchemaError("ct-props-correct.4", new Object[] { s, xsAttributeUseImpl.fAttrDecl.getName() }, element);
                xsAttributeGroupDecl2.replaceAttributeUse(attributeUse, xsAttributeUseImpl);
            }
        }
        if (b) {
            if (xsAttributeGroupDecl2.fAttributeWC == null) {
                xsAttributeGroupDecl2.fAttributeWC = xsAttributeGroupDecl.fAttributeWC;
            }
            else if (xsAttributeGroupDecl.fAttributeWC != null) {
                xsAttributeGroupDecl2.fAttributeWC = this.fSchemaHandler.fXSConstraints.performUnionWith(xsAttributeGroupDecl2.fAttributeWC, xsAttributeGroupDecl.fAttributeWC, xsAttributeGroupDecl2.fAttributeWC.fProcessContents);
                if (xsAttributeGroupDecl2.fAttributeWC == null) {
                    throw new ComplexTypeRecoverableError("src-ct.5", new Object[] { s }, element);
                }
            }
        }
    }
    
    private void addAssertsFromBaseTypes(final XSTypeDefinition xsTypeDefinition) {
        if (xsTypeDefinition != null) {
            if (xsTypeDefinition instanceof XSComplexTypeDefinition) {
                final XSObjectList assertions = ((XSComplexTypeDefinition)xsTypeDefinition).getAssertions();
                for (int i = 0; i < assertions.size(); ++i) {
                    if (!this.assertExists((XSAssertImpl)assertions.get(i))) {
                        this.addAssertion((XSAssertImpl)assertions.get(i));
                    }
                }
            }
            else if (xsTypeDefinition instanceof XSSimpleTypeDefinition) {
                final XSObjectList multiValueFacets = ((XSSimpleTypeDefinition)xsTypeDefinition).getMultiValueFacets();
                for (int j = 0; j < multiValueFacets.getLength(); ++j) {
                    final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)multiValueFacets.item(j);
                    if (xsMultiValueFacet.getFacetKind() == 16384) {
                        final Vector asserts = xsMultiValueFacet.getAsserts();
                        for (int k = 0; k < asserts.size(); ++k) {
                            this.addAssertion((XSAssertImpl)asserts.get(k));
                        }
                        break;
                    }
                }
            }
            final XSTypeDefinition baseType = xsTypeDefinition.getBaseType();
            if (baseType != null && !XS11TypeHelper.getSchemaTypeName(baseType).equals("anyType") && !baseType.derivedFrom(Constants.NS_XMLSCHEMA, "anyAtomicType", (short)2)) {
                this.addAssertsFromBaseTypes(baseType);
            }
        }
    }
    
    private boolean assertExists(final XSAssertImpl xsAssertImpl) {
        boolean b = false;
        if (this.fAssertions != null) {
            for (int i = 0; i < this.fAssertions.length; ++i) {
                if (this.fAssertions[i].equals(xsAssertImpl)) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    
    private void processComplexContent(Element nextSiblingElement, final boolean b, final boolean b2, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) throws ComplexTypeRecoverableError {
        Element element = null;
        XSParticleDecl fParticle = null;
        boolean b3 = false;
        String s = (nextSiblingElement != null) ? DOMUtil.getLocalName(nextSiblingElement) : null;
        if (this.fSchemaHandler.fSchemaVersion == 4 && s != null && s.equals(SchemaSymbols.ELT_OPENCONTENT)) {
            this.fOpenContent = this.traverseOpenContent(nextSiblingElement, xsDocumentInfo, schemaGrammar, false);
            nextSiblingElement = DOMUtil.getNextSiblingElement(nextSiblingElement);
            s = ((nextSiblingElement != null) ? DOMUtil.getLocalName(nextSiblingElement) : null);
        }
        if (s != null) {
            if (s.equals(SchemaSymbols.ELT_GROUP)) {
                fParticle = this.fSchemaHandler.fGroupTraverser.traverseLocal(nextSiblingElement, xsDocumentInfo, schemaGrammar);
                element = DOMUtil.getNextSiblingElement(nextSiblingElement);
            }
            else if (s.equals(SchemaSymbols.ELT_SEQUENCE)) {
                fParticle = this.traverseSequence(nextSiblingElement, xsDocumentInfo, schemaGrammar, 0, this.fComplexTypeDecl);
                if (fParticle != null && ((XSModelGroupImpl)fParticle.fValue).fParticleCount == 0) {
                    b3 = true;
                }
                element = DOMUtil.getNextSiblingElement(nextSiblingElement);
            }
            else if (s.equals(SchemaSymbols.ELT_CHOICE)) {
                fParticle = this.traverseChoice(nextSiblingElement, xsDocumentInfo, schemaGrammar, 0, this.fComplexTypeDecl);
                if (fParticle != null && fParticle.fMinOccurs == 0 && ((XSModelGroupImpl)fParticle.fValue).fParticleCount == 0) {
                    b3 = true;
                }
                element = DOMUtil.getNextSiblingElement(nextSiblingElement);
            }
            else if (s.equals(SchemaSymbols.ELT_ALL)) {
                fParticle = this.traverseAll(nextSiblingElement, xsDocumentInfo, schemaGrammar, 8, this.fComplexTypeDecl);
                if (fParticle != null && ((XSModelGroupImpl)fParticle.fValue).fParticleCount == 0) {
                    b3 = true;
                }
                element = DOMUtil.getNextSiblingElement(nextSiblingElement);
            }
            else {
                element = nextSiblingElement;
            }
        }
        if (b3) {
            Element element2 = DOMUtil.getFirstChildElement(nextSiblingElement);
            if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            if (element2 == null) {
                fParticle = null;
            }
        }
        if (fParticle == null && b) {
            fParticle = XSConstraints.getEmptySequence();
        }
        if (this.fSchemaHandler.fSchemaVersion == 4 && !b2) {
            if (this.fOpenContent == null) {
                if (xsDocumentInfo.fDefaultOpenContent != null && (fParticle != null || xsDocumentInfo.fDefaultOpenContent.fAppliesToEmpty)) {
                    this.fOpenContent = xsDocumentInfo.fDefaultOpenContent;
                }
            }
            else if (this.fOpenContent.fMode == 0) {
                this.fOpenContent = null;
            }
        }
        if (fParticle == null && !b2 && this.fOpenContent != null) {
            fParticle = XSConstraints.getEmptySequence();
        }
        this.fParticle = fParticle;
        if (this.fParticle == null) {
            this.fContentType = 0;
        }
        else if (b) {
            this.fContentType = 3;
        }
        else {
            this.fContentType = 2;
        }
        if (element != null) {
            if (this.isAttrOrAttrGroup(element)) {
                final Element traverseAttrsAndAttrGrps = this.traverseAttrsAndAttrGrps(element, this.fAttrGrp, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                if (!b2) {
                    this.fAttrGrp.removeProhibitedAttrs();
                }
                if (traverseAttrsAndAttrGrps != null) {
                    if (!this.isAssert(traverseAttrsAndAttrGrps)) {
                        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(traverseAttrsAndAttrGrps) }, traverseAttrsAndAttrGrps);
                    }
                    this.traverseAsserts(traverseAttrsAndAttrGrps, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
                }
            }
            else {
                if (!this.isAssert(element)) {
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element) }, element);
                }
                this.traverseAsserts(element, xsDocumentInfo, schemaGrammar, this.fComplexTypeDecl);
            }
        }
    }
    
    private boolean isAttrOrAttrGroup(final Element element) {
        final String localName = DOMUtil.getLocalName(element);
        return localName.equals(SchemaSymbols.ELT_ATTRIBUTE) || localName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP) || localName.equals(SchemaSymbols.ELT_ANYATTRIBUTE);
    }
    
    private boolean isAssert(final Element element) {
        return this.fSchemaHandler.fSchemaVersion == 4 && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ASSERT);
    }
    
    private void traverseAsserts(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final XSComplexTypeDecl xsComplexTypeDecl) throws ComplexTypeRecoverableError {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_XPATH];
        if (s != null) {
            final Element firstChildElement = DOMUtil.getFirstChildElement(element);
            XSObject xsObject = null;
            if (firstChildElement != null) {
                if (DOMUtil.getLocalName(firstChildElement).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    xsObject = this.traverseAnnotationDecl(firstChildElement, checkAttributes, false, xsDocumentInfo);
                    final Element nextSiblingElement = DOMUtil.getNextSiblingElement(firstChildElement);
                    if (nextSiblingElement != null) {
                        this.reportSchemaError("s4s-elt-invalid-content.1", new Object[] { DOMUtil.getLocalName(element), DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
                    }
                }
                else {
                    final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(firstChildElement);
                    if (syntheticAnnotation != null) {
                        xsObject = this.traverseSyntheticAnnotation(firstChildElement, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
                    }
                }
            }
            XSObjectListImpl empty_LIST;
            if (xsObject != null) {
                empty_LIST = new XSObjectListImpl();
                empty_LIST.addXSObject(xsObject);
            }
            else {
                empty_LIST = XSObjectListImpl.EMPTY_LIST;
            }
            final XSAssertImpl xsAssertImpl = new XSAssertImpl(xsComplexTypeDecl, empty_LIST, this.fSchemaHandler);
            final Test test = new Test(s, xsDocumentInfo.fNamespaceSupport, xsAssertImpl);
            final String xPathDefaultNamespaceForAssert = this.getXPathDefaultNamespaceForAssert(element, xsDocumentInfo, checkAttributes);
            xsAssertImpl.setTest(test, element);
            xsAssertImpl.setXPathDefaultNamespace(xPathDefaultNamespaceForAssert);
            xsAssertImpl.setXPath2NamespaceContext(this.normalizeNsContextForAssertInOverride(xsDocumentInfo, element));
            final String trim = XMLChar.trim(element.getAttributeNS(SchemaSymbols.URI_XERCES_EXTENSIONS, SchemaSymbols.ATT_ASSERT_MESSAGE));
            if (!"".equals(trim)) {
                xsAssertImpl.setMessage(trim);
            }
            this.addAssertion(xsAssertImpl);
            final Element nextSiblingElement2 = DOMUtil.getNextSiblingElement(element);
            if (nextSiblingElement2 != null) {
                if (!nextSiblingElement2.getLocalName().equals(SchemaSymbols.ELT_ASSERT)) {
                    this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(nextSiblingElement2) }, nextSiblingElement2);
                }
                this.traverseAsserts(nextSiblingElement2, xsDocumentInfo, schemaGrammar, xsComplexTypeDecl);
            }
        }
        else {
            this.reportSchemaError("src-assert.3.13.1", new Object[] { DOMUtil.getLocalName(element), XS11TypeHelper.getSchemaTypeName(xsComplexTypeDecl) }, element);
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
    }
    
    private SchemaNamespaceSupport normalizeNsContextForAssertInOverride(final XSDocumentInfo xsDocumentInfo, final Element element) {
        final SchemaNamespaceSupport schemaNamespaceSupport = new SchemaNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
        final XSDocumentInfo overridingSchemaDocument = this.fSchemaHandler.getOverridingSchemaDocument(xsDocumentInfo);
        if (overridingSchemaDocument != null) {
            final Enumeration allPrefixes = schemaNamespaceSupport.getAllPrefixes();
            while (allPrefixes.hasMoreElements()) {
                final String s = allPrefixes.nextElement();
                if (this.isNsDeclOnSchemaElementOfOverridenSchema(s, element)) {
                    schemaNamespaceSupport.deletePrefix(s);
                }
            }
            final Attr[] attrs = DOMUtil.getAttrs(this.fSchemaHandler.getOverridingXSElement(xsDocumentInfo));
            for (int i = 0; i < attrs.length; ++i) {
                final Attr attr = attrs[i];
                final String name = attr.getName();
                final int index = name.indexOf(58);
                if (index != -1) {
                    schemaNamespaceSupport.declarePrefix(name.substring(index + 1), attr.getValue());
                }
            }
            final Enumeration allPrefixes2 = overridingSchemaDocument.fNamespaceSupport.getAllPrefixes();
            while (allPrefixes2.hasMoreElements()) {
                final String s2 = allPrefixes2.nextElement();
                if (s2 != XMLSymbols.PREFIX_XML && s2 != XMLSymbols.PREFIX_XMLNS && !this.isNsDeclOnComplexTypeTree(s2, element)) {
                    schemaNamespaceSupport.declarePrefix(s2, overridingSchemaDocument.fNamespaceSupport.getURI(s2));
                }
            }
        }
        return schemaNamespaceSupport;
    }
    
    private boolean isNsDeclOnSchemaElementOfOverridenSchema(final String s, final Element element) {
        boolean b = false;
        final Attr[] attrs = DOMUtil.getAttrs(DOMUtil.getRoot(DOMUtil.getDocument(element)));
        for (int i = 0; i < attrs.length; ++i) {
            final String name = attrs[i].getName();
            final int index = name.indexOf(58);
            if (index != -1 && name.substring(index + 1).equals(s)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    private boolean isNsDeclOnComplexTypeTree(final String s, final Element element) {
        boolean nsDeclOnComplexTypeTree = false;
        final Attr[] attrs = DOMUtil.getAttrs(element);
        for (int i = 0; i < attrs.length; ++i) {
            final String name = attrs[i].getName();
            final int index = name.indexOf(58);
            if (index != -1 && name.substring(index + 1).equals(s)) {
                nsDeclOnComplexTypeTree = true;
                break;
            }
        }
        if (!nsDeclOnComplexTypeTree) {
            final Element parent = DOMUtil.getParent(element);
            if (parent != DOMUtil.getRoot(DOMUtil.getDocument(parent))) {
                nsDeclOnComplexTypeTree = this.isNsDeclOnComplexTypeTree(s, parent);
            }
        }
        return nsDeclOnComplexTypeTree;
    }
    
    private String getXPathDefaultNamespaceForAssert(final Element element, final XSDocumentInfo xsDocumentInfo, final Object[] array) {
        String s = (String)array[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS];
        if (s == null) {
            if (xsDocumentInfo.fXpathDefaultNamespaceIs2PoundDefault) {
                s = xsDocumentInfo.fValidationContext.getURI(XMLSymbols.EMPTY_STRING);
                if (s != null) {
                    s = this.fSymbolTable.addSymbol(s);
                }
            }
            else {
                s = xsDocumentInfo.fXpathDefaultNamespace;
            }
        }
        if (s == null) {
            final Attr attributeNode = element.getOwnerDocument().getDocumentElement().getAttributeNode(SchemaSymbols.ATT_XPATH_DEFAULT_NS);
            if (attributeNode != null) {
                s = attributeNode.getValue();
                if ("##targetNamespace".equals(s)) {
                    s = xsDocumentInfo.fTargetNamespace;
                }
                else if ("##defaultNamespace".equals(s)) {
                    s = xsDocumentInfo.fValidationContext.getURI(XMLSymbols.EMPTY_STRING);
                    if (s != null) {
                        s = this.fSymbolTable.addSymbol(s);
                    }
                    array[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS_TWOPOUNDDFLT] = Boolean.TRUE;
                }
                else if (!s.equals("##local")) {
                    try {
                        new AnyURIDV().getActualValue(s, xsDocumentInfo.fValidationContext);
                        s = this.fSymbolTable.addSymbol(s);
                    }
                    catch (final InvalidDatatypeValueException ex) {
                        this.reportSchemaError("cvc-datatype-valid.1.2.3", new Object[] { s, "anyURI | ##defaultNamespace | ##targetNamespace | ##local" }, element);
                    }
                }
            }
        }
        return s;
    }
    
    private String genAnonTypeName(final Element element) {
        final StringBuffer sb = new StringBuffer("#AnonType_");
        for (Element element2 = DOMUtil.getParent(element); element2 != null && element2 != DOMUtil.getRoot(DOMUtil.getDocument(element2)); element2 = DOMUtil.getParent(element2)) {
            sb.append(element2.getAttribute(SchemaSymbols.ATT_NAME));
        }
        return sb.toString();
    }
    
    private void handleComplexTypeError(final String s, final Object[] array, final Element element) {
        if (s != null) {
            this.reportSchemaError(s, array, element);
        }
        this.fBaseType = SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion);
        this.fContentType = 3;
        this.fXSSimpleType = null;
        this.fParticle = getErrorContent();
        this.fAttrGrp.fAttributeWC = getErrorWildcard();
    }
    
    private void contentBackup() {
        if (this.fGlobalStore == null) {
            this.fGlobalStore = new Object[13];
            this.fGlobalStorePos = 0;
        }
        if (this.fGlobalStorePos == this.fGlobalStore.length) {
            final Object[] fGlobalStore = new Object[this.fGlobalStorePos + 13];
            System.arraycopy(this.fGlobalStore, 0, fGlobalStore, 0, this.fGlobalStorePos);
            this.fGlobalStore = fGlobalStore;
        }
        this.fGlobalStore[this.fGlobalStorePos++] = this.fComplexTypeDecl;
        this.fGlobalStore[this.fGlobalStorePos++] = (this.fIsAbstract ? Boolean.TRUE : Boolean.FALSE);
        this.fGlobalStore[this.fGlobalStorePos++] = this.fName;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fTargetNamespace;
        this.fGlobalStore[this.fGlobalStorePos++] = new Integer((this.fDerivedBy << 16) + this.fFinal);
        this.fGlobalStore[this.fGlobalStorePos++] = new Integer((this.fBlock << 16) + this.fContentType);
        this.fGlobalStore[this.fGlobalStorePos++] = this.fBaseType;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fAttrGrp;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fParticle;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fXSSimpleType;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fAnnotations;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fOpenContent;
        this.fGlobalStore[this.fGlobalStorePos++] = this.fAssertions;
    }
    
    private void contentRestore() {
        final Object[] fGlobalStore = this.fGlobalStore;
        final int fGlobalStorePos = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos;
        this.fAssertions = (XSAssertImpl[])fGlobalStore[fGlobalStorePos];
        final Object[] fGlobalStore2 = this.fGlobalStore;
        final int fGlobalStorePos2 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos2;
        this.fOpenContent = (XSOpenContentDecl)fGlobalStore2[fGlobalStorePos2];
        final Object[] fGlobalStore3 = this.fGlobalStore;
        final int fGlobalStorePos3 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos3;
        this.fAnnotations = (XSAnnotationImpl[])fGlobalStore3[fGlobalStorePos3];
        final Object[] fGlobalStore4 = this.fGlobalStore;
        final int fGlobalStorePos4 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos4;
        this.fXSSimpleType = (XSSimpleType)fGlobalStore4[fGlobalStorePos4];
        final Object[] fGlobalStore5 = this.fGlobalStore;
        final int fGlobalStorePos5 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos5;
        this.fParticle = (XSParticleDecl)fGlobalStore5[fGlobalStorePos5];
        final Object[] fGlobalStore6 = this.fGlobalStore;
        final int fGlobalStorePos6 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos6;
        this.fAttrGrp = (XSAttributeGroupDecl)fGlobalStore6[fGlobalStorePos6];
        final Object[] fGlobalStore7 = this.fGlobalStore;
        final int fGlobalStorePos7 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos7;
        this.fBaseType = (XSTypeDefinition)fGlobalStore7[fGlobalStorePos7];
        final Object[] fGlobalStore8 = this.fGlobalStore;
        final int fGlobalStorePos8 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos8;
        final int intValue = (int)fGlobalStore8[fGlobalStorePos8];
        this.fBlock = (short)(intValue >> 16);
        this.fContentType = (short)intValue;
        final Object[] fGlobalStore9 = this.fGlobalStore;
        final int fGlobalStorePos9 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos9;
        final int intValue2 = (int)fGlobalStore9[fGlobalStorePos9];
        this.fDerivedBy = (short)(intValue2 >> 16);
        this.fFinal = (short)intValue2;
        final Object[] fGlobalStore10 = this.fGlobalStore;
        final int fGlobalStorePos10 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos10;
        this.fTargetNamespace = (String)fGlobalStore10[fGlobalStorePos10];
        final Object[] fGlobalStore11 = this.fGlobalStore;
        final int fGlobalStorePos11 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos11;
        this.fName = (String)fGlobalStore11[fGlobalStorePos11];
        final Object[] fGlobalStore12 = this.fGlobalStore;
        final int fGlobalStorePos12 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos12;
        this.fIsAbstract = (boolean)fGlobalStore12[fGlobalStorePos12];
        final Object[] fGlobalStore13 = this.fGlobalStore;
        final int fGlobalStorePos13 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos13;
        this.fComplexTypeDecl = (XSComplexTypeDecl)fGlobalStore13[fGlobalStorePos13];
    }
    
    private void addAnnotation(final XSAnnotationImpl xsAnnotationImpl) {
        if (xsAnnotationImpl == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[1];
        }
        else {
            final XSAnnotationImpl[] fAnnotations = new XSAnnotationImpl[this.fAnnotations.length + 1];
            System.arraycopy(this.fAnnotations, 0, fAnnotations, 0, this.fAnnotations.length);
            this.fAnnotations = fAnnotations;
        }
        this.fAnnotations[this.fAnnotations.length - 1] = xsAnnotationImpl;
    }
    
    private void addAssertion(final XSAssertImpl xsAssertImpl) {
        if (xsAssertImpl == null) {
            return;
        }
        if (this.fAssertions == null) {
            this.fAssertions = new XSAssertImpl[1];
        }
        else {
            final XSAssertImpl[] fAssertions = new XSAssertImpl[this.fAssertions.length + 1];
            System.arraycopy(this.fAssertions, 0, fAssertions, 0, this.fAssertions.length);
            this.fAssertions = fAssertions;
        }
        this.fAssertions[this.fAssertions.length - 1] = xsAssertImpl;
    }
    
    static {
        XSDComplexTypeTraverser.fErrorContent = null;
        XSDComplexTypeTraverser.fErrorWildcard = null;
    }
    
    private static final class ComplexTypeRecoverableError extends Exception
    {
        private static final long serialVersionUID = 6802729912091130335L;
        Object[] errorSubstText;
        Element errorElem;
        
        ComplexTypeRecoverableError() {
            this.errorSubstText = null;
            this.errorElem = null;
        }
        
        ComplexTypeRecoverableError(final String s, final Object[] errorSubstText, final Element errorElem) {
            super(s);
            this.errorSubstText = null;
            this.errorElem = null;
            this.errorSubstText = errorSubstText;
            this.errorElem = errorElem;
        }
    }
}
