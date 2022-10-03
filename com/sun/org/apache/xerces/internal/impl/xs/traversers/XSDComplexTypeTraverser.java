package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;

class XSDComplexTypeTraverser extends XSDAbstractParticleTraverser
{
    private static final int GLOBAL_NUM = 11;
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
    private Object[] fGlobalStore;
    private int fGlobalStorePos;
    private static final boolean DEBUG = false;
    
    private static XSParticleDecl getErrorContent() {
        if (XSDComplexTypeTraverser.fErrorContent == null) {
            final XSParticleDecl particle = new XSParticleDecl();
            particle.fType = 2;
            particle.fValue = getErrorWildcard();
            particle.fMinOccurs = 0;
            particle.fMaxOccurs = -1;
            final XSModelGroupImpl group = new XSModelGroupImpl();
            group.fCompositor = 102;
            group.fParticleCount = 1;
            (group.fParticles = new XSParticleDecl[1])[0] = particle;
            final XSParticleDecl errorContent = new XSParticleDecl();
            errorContent.fType = 3;
            errorContent.fValue = group;
            XSDComplexTypeTraverser.fErrorContent = errorContent;
        }
        return XSDComplexTypeTraverser.fErrorContent;
    }
    
    private static XSWildcardDecl getErrorWildcard() {
        if (XSDComplexTypeTraverser.fErrorWildcard == null) {
            final XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = 2;
            XSDComplexTypeTraverser.fErrorWildcard = wildcard;
        }
        return XSDComplexTypeTraverser.fErrorWildcard;
    }
    
    XSDComplexTypeTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
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
        this.fGlobalStore = null;
        this.fGlobalStorePos = 0;
    }
    
    XSComplexTypeDecl traverseLocal(final Element complexTypeNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(complexTypeNode, false, schemaDoc);
        final String complexTypeName = this.genAnonTypeName(complexTypeNode);
        this.contentBackup();
        final XSComplexTypeDecl type = this.traverseComplexTypeDecl(complexTypeNode, complexTypeName, attrValues, schemaDoc, grammar);
        this.contentRestore();
        grammar.addComplexTypeDecl(type, this.fSchemaHandler.element2Locator(complexTypeNode));
        type.setIsAnonymous();
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return type;
    }
    
    XSComplexTypeDecl traverseGlobal(final Element complexTypeNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(complexTypeNode, true, schemaDoc);
        final String complexTypeName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        this.contentBackup();
        XSComplexTypeDecl type = this.traverseComplexTypeDecl(complexTypeNode, complexTypeName, attrValues, schemaDoc, grammar);
        this.contentRestore();
        grammar.addComplexTypeDecl(type, this.fSchemaHandler.element2Locator(complexTypeNode));
        if (complexTypeName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_COMPLEXTYPE, SchemaSymbols.ATT_NAME }, complexTypeNode);
            type = null;
        }
        else {
            if (grammar.getGlobalTypeDecl(type.getName()) == null) {
                grammar.addGlobalComplexTypeDecl(type);
            }
            final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            final XSTypeDefinition type2 = grammar.getGlobalTypeDecl(type.getName(), loc);
            if (type2 == null) {
                grammar.addGlobalComplexTypeDecl(type, loc);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (type2 != null && type2 instanceof XSComplexTypeDecl) {
                    type = (XSComplexTypeDecl)type2;
                }
                this.fSchemaHandler.addGlobalTypeDecl(type);
            }
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return type;
    }
    
    private XSComplexTypeDecl traverseComplexTypeDecl(final Element complexTypeDecl, final String complexTypeName, final Object[] attrValues, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        this.fComplexTypeDecl = new XSComplexTypeDecl();
        this.fAttrGrp = new XSAttributeGroupDecl();
        final Boolean abstractAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_ABSTRACT];
        final XInt blockAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_BLOCK];
        final Boolean mixedAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_MIXED];
        final XInt finalAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FINAL];
        this.fName = complexTypeName;
        this.fComplexTypeDecl.setName(this.fName);
        this.fTargetNamespace = schemaDoc.fTargetNamespace;
        this.fBlock = ((blockAtt == null) ? schemaDoc.fBlockDefault : blockAtt.shortValue());
        this.fFinal = ((finalAtt == null) ? schemaDoc.fFinalDefault : finalAtt.shortValue());
        this.fBlock &= 0x3;
        this.fFinal &= 0x3;
        this.fIsAbstract = (abstractAtt != null && abstractAtt);
        this.fAnnotations = null;
        Element child = null;
        try {
            child = DOMUtil.getFirstChildElement(complexTypeDecl);
            if (child != null) {
                if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    this.addAnnotation(this.traverseAnnotationDecl(child, attrValues, false, schemaDoc));
                    child = DOMUtil.getNextSiblingElement(child);
                }
                else {
                    final String text = DOMUtil.getSyntheticAnnotation(complexTypeDecl);
                    if (text != null) {
                        this.addAnnotation(this.traverseSyntheticAnnotation(complexTypeDecl, text, attrValues, false, schemaDoc));
                    }
                }
                if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, child);
                }
            }
            else {
                final String text = DOMUtil.getSyntheticAnnotation(complexTypeDecl);
                if (text != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(complexTypeDecl, text, attrValues, false, schemaDoc));
                }
            }
            if (child == null) {
                this.fBaseType = SchemaGrammar.fAnyType;
                this.fDerivedBy = 2;
                this.processComplexContent(child, mixedAtt, false, schemaDoc, grammar);
            }
            else if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_SIMPLECONTENT)) {
                this.traverseSimpleContent(child, schemaDoc, grammar);
                final Element elemTmp = DOMUtil.getNextSiblingElement(child);
                if (elemTmp != null) {
                    final String siblingName = DOMUtil.getLocalName(elemTmp);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, siblingName }, elemTmp);
                }
            }
            else if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_COMPLEXCONTENT)) {
                this.traverseComplexContent(child, mixedAtt, schemaDoc, grammar);
                final Element elemTmp = DOMUtil.getNextSiblingElement(child);
                if (elemTmp != null) {
                    final String siblingName = DOMUtil.getLocalName(elemTmp);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, siblingName }, elemTmp);
                }
            }
            else {
                this.fBaseType = SchemaGrammar.fAnyType;
                this.fDerivedBy = 2;
                this.processComplexContent(child, mixedAtt, false, schemaDoc, grammar);
            }
        }
        catch (final ComplexTypeRecoverableError e) {
            this.handleComplexTypeError(e.getMessage(), e.errorSubstText, e.errorElem);
        }
        this.fComplexTypeDecl.setValues(this.fName, this.fTargetNamespace, this.fBaseType, this.fDerivedBy, this.fFinal, this.fBlock, this.fContentType, this.fIsAbstract, this.fAttrGrp, this.fXSSimpleType, this.fParticle, new XSObjectListImpl(this.fAnnotations, (this.fAnnotations == null) ? 0 : this.fAnnotations.length));
        return this.fComplexTypeDecl;
    }
    
    private void traverseSimpleContent(final Element simpleContentElement, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) throws ComplexTypeRecoverableError {
        final Object[] simpleContentAttrValues = this.fAttrChecker.checkAttributes(simpleContentElement, false, schemaDoc);
        this.fContentType = 1;
        this.fParticle = null;
        Element simpleContent = DOMUtil.getFirstChildElement(simpleContentElement);
        if (simpleContent != null && DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
            this.addAnnotation(this.traverseAnnotationDecl(simpleContent, simpleContentAttrValues, false, schemaDoc));
            simpleContent = DOMUtil.getNextSiblingElement(simpleContent);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(simpleContentElement);
            if (text != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(simpleContentElement, text, simpleContentAttrValues, false, schemaDoc));
            }
        }
        if (simpleContent == null) {
            this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[] { this.fName, SchemaSymbols.ELT_SIMPLECONTENT }, simpleContentElement);
        }
        final String simpleContentName = DOMUtil.getLocalName(simpleContent);
        if (simpleContentName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            this.fDerivedBy = 2;
        }
        else {
            if (!simpleContentName.equals(SchemaSymbols.ELT_EXTENSION)) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, simpleContentName }, simpleContent);
            }
            this.fDerivedBy = 1;
        }
        final Element elemTmp = DOMUtil.getNextSiblingElement(simpleContent);
        if (elemTmp != null) {
            this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
            final String siblingName = DOMUtil.getLocalName(elemTmp);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, siblingName }, elemTmp);
        }
        final Object[] derivationTypeAttrValues = this.fAttrChecker.checkAttributes(simpleContent, false, schemaDoc);
        final QName baseTypeName = (QName)derivationTypeAttrValues[XSAttributeChecker.ATTIDX_BASE];
        if (baseTypeName == null) {
            this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError("s4s-att-must-appear", new Object[] { simpleContentName, "base" }, simpleContent);
        }
        final XSTypeDefinition type = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, baseTypeName, simpleContent);
        if (type == null) {
            this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError();
        }
        this.fBaseType = type;
        XSSimpleType baseValidator = null;
        XSComplexTypeDecl baseComplexType = null;
        int baseFinalSet = 0;
        if (type.getTypeCategory() == 15) {
            baseComplexType = (XSComplexTypeDecl)type;
            baseFinalSet = baseComplexType.getFinal();
            if (baseComplexType.getContentType() == 1) {
                baseValidator = (XSSimpleType)baseComplexType.getSimpleType();
            }
            else if (this.fDerivedBy != 2 || baseComplexType.getContentType() != 3 || !((XSParticleDecl)baseComplexType.getParticle()).emptiable()) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("src-ct.2.1", new Object[] { this.fName, baseComplexType.getName() }, simpleContent);
            }
        }
        else {
            baseValidator = (XSSimpleType)type;
            if (this.fDerivedBy == 2) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("src-ct.2.1", new Object[] { this.fName, baseValidator.getName() }, simpleContent);
            }
            baseFinalSet = baseValidator.getFinal();
        }
        if ((baseFinalSet & this.fDerivedBy) != 0x0) {
            this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            final String errorKey = (this.fDerivedBy == 1) ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1";
            throw new ComplexTypeRecoverableError(errorKey, new Object[] { this.fName, this.fBaseType.getName() }, simpleContent);
        }
        final Element scElement = simpleContent;
        simpleContent = DOMUtil.getFirstChildElement(simpleContent);
        if (simpleContent != null) {
            if (DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.addAnnotation(this.traverseAnnotationDecl(simpleContent, derivationTypeAttrValues, false, schemaDoc));
                simpleContent = DOMUtil.getNextSiblingElement(simpleContent);
            }
            else {
                final String text2 = DOMUtil.getSyntheticAnnotation(scElement);
                if (text2 != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(scElement, text2, derivationTypeAttrValues, false, schemaDoc));
                }
            }
            if (simpleContent != null && DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, simpleContent);
            }
        }
        else {
            final String text2 = DOMUtil.getSyntheticAnnotation(scElement);
            if (text2 != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(scElement, text2, derivationTypeAttrValues, false, schemaDoc));
            }
        }
        if (this.fDerivedBy == 2) {
            if (simpleContent != null && DOMUtil.getLocalName(simpleContent).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                final XSSimpleType dv = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(simpleContent, schemaDoc, grammar);
                if (dv == null) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError();
                }
                if (baseValidator != null && !XSConstraints.checkSimpleDerivationOk(dv, baseValidator, baseValidator.getFinal())) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("derivation-ok-restriction.5.2.2.1", new Object[] { this.fName, dv.getName(), baseValidator.getName() }, simpleContent);
                }
                baseValidator = dv;
                simpleContent = DOMUtil.getNextSiblingElement(simpleContent);
            }
            if (baseValidator == null) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("src-ct.2.2", new Object[] { this.fName }, simpleContent);
            }
            Element attrNode = null;
            XSFacets facetData = null;
            short presentFacets = 0;
            short fixedFacets = 0;
            if (simpleContent != null) {
                final FacetInfo fi = this.traverseFacets(simpleContent, baseValidator, schemaDoc);
                attrNode = fi.nodeAfterFacets;
                facetData = fi.facetdata;
                presentFacets = fi.fPresentFacets;
                fixedFacets = fi.fFixedFacets;
            }
            final String name = this.genAnonTypeName(simpleContentElement);
            this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(name, schemaDoc.fTargetNamespace, (short)0, baseValidator, null);
            try {
                this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
                this.fXSSimpleType.applyFacets(facetData, presentFacets, fixedFacets, this.fValidationState);
            }
            catch (final InvalidDatatypeFacetException ex) {
                this.reportSchemaError(ex.getKey(), ex.getArgs(), simpleContent);
                this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(name, schemaDoc.fTargetNamespace, (short)0, baseValidator, null);
            }
            if (this.fXSSimpleType instanceof XSSimpleTypeDecl) {
                ((XSSimpleTypeDecl)this.fXSSimpleType).setAnonymous(true);
            }
            if (attrNode != null) {
                if (!this.isAttrOrAttrGroup(attrNode)) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(attrNode) }, attrNode);
                }
                final Element node = this.traverseAttrsAndAttrGrps(attrNode, this.fAttrGrp, schemaDoc, grammar, this.fComplexTypeDecl);
                if (node != null) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(node) }, node);
                }
            }
            try {
                this.mergeAttributes(baseComplexType.getAttrGrp(), this.fAttrGrp, this.fName, false, simpleContentElement);
            }
            catch (final ComplexTypeRecoverableError e) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw e;
            }
            this.fAttrGrp.removeProhibitedAttrs();
            final Object[] errArgs = this.fAttrGrp.validRestrictionOf(this.fName, baseComplexType.getAttrGrp());
            if (errArgs != null) {
                this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError((String)errArgs[errArgs.length - 1], errArgs, attrNode);
            }
        }
        else {
            this.fXSSimpleType = baseValidator;
            if (simpleContent != null) {
                final Element attrNode = simpleContent;
                if (!this.isAttrOrAttrGroup(attrNode)) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(attrNode) }, attrNode);
                }
                final Element node2 = this.traverseAttrsAndAttrGrps(attrNode, this.fAttrGrp, schemaDoc, grammar, this.fComplexTypeDecl);
                if (node2 != null) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(node2) }, node2);
                }
                this.fAttrGrp.removeProhibitedAttrs();
            }
            if (baseComplexType != null) {
                try {
                    this.mergeAttributes(baseComplexType.getAttrGrp(), this.fAttrGrp, this.fName, true, simpleContentElement);
                }
                catch (final ComplexTypeRecoverableError e2) {
                    this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw e2;
                }
            }
        }
        this.fAttrChecker.returnAttrArray(simpleContentAttrValues, schemaDoc);
        this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
    }
    
    private void traverseComplexContent(final Element complexContentElement, final boolean mixedOnType, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) throws ComplexTypeRecoverableError {
        final Object[] complexContentAttrValues = this.fAttrChecker.checkAttributes(complexContentElement, false, schemaDoc);
        boolean mixedContent = mixedOnType;
        final Boolean mixedAtt = (Boolean)complexContentAttrValues[XSAttributeChecker.ATTIDX_MIXED];
        if (mixedAtt != null) {
            mixedContent = mixedAtt;
        }
        this.fXSSimpleType = null;
        Element complexContent = DOMUtil.getFirstChildElement(complexContentElement);
        if (complexContent != null && DOMUtil.getLocalName(complexContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
            this.addAnnotation(this.traverseAnnotationDecl(complexContent, complexContentAttrValues, false, schemaDoc));
            complexContent = DOMUtil.getNextSiblingElement(complexContent);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(complexContentElement);
            if (text != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(complexContentElement, text, complexContentAttrValues, false, schemaDoc));
            }
        }
        if (complexContent == null) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[] { this.fName, SchemaSymbols.ELT_COMPLEXCONTENT }, complexContentElement);
        }
        final String complexContentName = DOMUtil.getLocalName(complexContent);
        if (complexContentName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            this.fDerivedBy = 2;
        }
        else {
            if (!complexContentName.equals(SchemaSymbols.ELT_EXTENSION)) {
                this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, complexContentName }, complexContent);
            }
            this.fDerivedBy = 1;
        }
        final Element elemTmp = DOMUtil.getNextSiblingElement(complexContent);
        if (elemTmp != null) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            final String siblingName = DOMUtil.getLocalName(elemTmp);
            throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, siblingName }, elemTmp);
        }
        final Object[] derivationTypeAttrValues = this.fAttrChecker.checkAttributes(complexContent, false, schemaDoc);
        final QName baseTypeName = (QName)derivationTypeAttrValues[XSAttributeChecker.ATTIDX_BASE];
        if (baseTypeName == null) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError("s4s-att-must-appear", new Object[] { complexContentName, "base" }, complexContent);
        }
        final XSTypeDefinition type = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, baseTypeName, complexContent);
        if (type == null) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError();
        }
        if (!(type instanceof XSComplexTypeDecl)) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            throw new ComplexTypeRecoverableError("src-ct.1", new Object[] { this.fName, type.getName() }, complexContent);
        }
        final XSComplexTypeDecl baseType = (XSComplexTypeDecl)type;
        this.fBaseType = baseType;
        if ((baseType.getFinal() & this.fDerivedBy) != 0x0) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            final String errorKey = (this.fDerivedBy == 1) ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1";
            throw new ComplexTypeRecoverableError(errorKey, new Object[] { this.fName, this.fBaseType.getName() }, complexContent);
        }
        complexContent = DOMUtil.getFirstChildElement(complexContent);
        if (complexContent != null) {
            if (DOMUtil.getLocalName(complexContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.addAnnotation(this.traverseAnnotationDecl(complexContent, derivationTypeAttrValues, false, schemaDoc));
                complexContent = DOMUtil.getNextSiblingElement(complexContent);
            }
            else {
                final String text2 = DOMUtil.getSyntheticAnnotation(complexContent);
                if (text2 != null) {
                    this.addAnnotation(this.traverseSyntheticAnnotation(complexContent, text2, derivationTypeAttrValues, false, schemaDoc));
                }
            }
            if (complexContent != null && DOMUtil.getLocalName(complexContent).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, complexContent);
            }
        }
        else {
            final String text2 = DOMUtil.getSyntheticAnnotation(complexContent);
            if (text2 != null) {
                this.addAnnotation(this.traverseSyntheticAnnotation(complexContent, text2, derivationTypeAttrValues, false, schemaDoc));
            }
        }
        try {
            this.processComplexContent(complexContent, mixedContent, true, schemaDoc, grammar);
        }
        catch (final ComplexTypeRecoverableError e) {
            this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
            this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
            throw e;
        }
        final XSParticleDecl baseContent = (XSParticleDecl)baseType.getParticle();
        if (this.fDerivedBy == 2) {
            if (this.fContentType == 3 && baseType.getContentType() != 3) {
                this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw new ComplexTypeRecoverableError("derivation-ok-restriction.5.4.1.2", new Object[] { this.fName, baseType.getName() }, complexContent);
            }
            try {
                this.mergeAttributes(baseType.getAttrGrp(), this.fAttrGrp, this.fName, false, complexContent);
            }
            catch (final ComplexTypeRecoverableError e2) {
                this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw e2;
            }
            this.fAttrGrp.removeProhibitedAttrs();
            if (baseType != SchemaGrammar.fAnyType) {
                final Object[] errArgs = this.fAttrGrp.validRestrictionOf(this.fName, baseType.getAttrGrp());
                if (errArgs != null) {
                    this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError((String)errArgs[errArgs.length - 1], errArgs, complexContent);
                }
            }
        }
        else {
            if (this.fParticle == null) {
                this.fContentType = baseType.getContentType();
                this.fXSSimpleType = (XSSimpleType)baseType.getSimpleType();
                this.fParticle = baseContent;
            }
            else if (baseType.getContentType() != 0) {
                if (this.fContentType == 2 && baseType.getContentType() != 2) {
                    this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.a", new Object[] { this.fName }, complexContent);
                }
                if (this.fContentType == 3 && baseType.getContentType() != 3) {
                    this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.b", new Object[] { this.fName }, complexContent);
                }
                if ((this.fParticle.fType == 3 && ((XSModelGroupImpl)this.fParticle.fValue).fCompositor == 103) || (((XSParticleDecl)baseType.getParticle()).fType == 3 && ((XSModelGroupImpl)((XSParticleDecl)baseType.getParticle()).fValue).fCompositor == 103)) {
                    this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                    this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                    throw new ComplexTypeRecoverableError("cos-all-limited.1.2", new Object[0], complexContent);
                }
                final XSModelGroupImpl group = new XSModelGroupImpl();
                group.fCompositor = 102;
                group.fParticleCount = 2;
                (group.fParticles = new XSParticleDecl[2])[0] = (XSParticleDecl)baseType.getParticle();
                group.fParticles[1] = this.fParticle;
                group.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                final XSParticleDecl particle = new XSParticleDecl();
                particle.fType = 3;
                particle.fValue = group;
                particle.fAnnotations = XSObjectListImpl.EMPTY_LIST;
                this.fParticle = particle;
            }
            this.fAttrGrp.removeProhibitedAttrs();
            try {
                this.mergeAttributes(baseType.getAttrGrp(), this.fAttrGrp, this.fName, true, complexContent);
            }
            catch (final ComplexTypeRecoverableError e2) {
                this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
                this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
                throw e2;
            }
        }
        this.fAttrChecker.returnAttrArray(complexContentAttrValues, schemaDoc);
        this.fAttrChecker.returnAttrArray(derivationTypeAttrValues, schemaDoc);
    }
    
    private void mergeAttributes(final XSAttributeGroupDecl fromAttrGrp, final XSAttributeGroupDecl toAttrGrp, final String typeName, final boolean extension, final Element elem) throws ComplexTypeRecoverableError {
        final XSObjectList attrUseS = fromAttrGrp.getAttributeUses();
        XSAttributeUseImpl oneAttrUse = null;
        for (int attrCount = attrUseS.getLength(), i = 0; i < attrCount; ++i) {
            oneAttrUse = (XSAttributeUseImpl)attrUseS.item(i);
            final XSAttributeUse existingAttrUse = toAttrGrp.getAttributeUse(oneAttrUse.fAttrDecl.getNamespace(), oneAttrUse.fAttrDecl.getName());
            if (existingAttrUse == null) {
                final String idName = toAttrGrp.addAttributeUse(oneAttrUse);
                if (idName != null) {
                    throw new ComplexTypeRecoverableError("ct-props-correct.5", new Object[] { typeName, idName, oneAttrUse.fAttrDecl.getName() }, elem);
                }
            }
            else if (existingAttrUse != oneAttrUse && extension) {
                this.reportSchemaError("ct-props-correct.4", new Object[] { typeName, oneAttrUse.fAttrDecl.getName() }, elem);
                toAttrGrp.replaceAttributeUse(existingAttrUse, oneAttrUse);
            }
        }
        if (extension) {
            if (toAttrGrp.fAttributeWC == null) {
                toAttrGrp.fAttributeWC = fromAttrGrp.fAttributeWC;
            }
            else if (fromAttrGrp.fAttributeWC != null) {
                toAttrGrp.fAttributeWC = toAttrGrp.fAttributeWC.performUnionWith(fromAttrGrp.fAttributeWC, toAttrGrp.fAttributeWC.fProcessContents);
                if (toAttrGrp.fAttributeWC == null) {
                    throw new ComplexTypeRecoverableError("src-ct.5", new Object[] { typeName }, elem);
                }
            }
        }
    }
    
    private void processComplexContent(final Element complexContentChild, final boolean isMixed, final boolean isDerivation, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) throws ComplexTypeRecoverableError {
        Element attrNode = null;
        XSParticleDecl particle = null;
        boolean emptyParticle = false;
        if (complexContentChild != null) {
            final String childName = DOMUtil.getLocalName(complexContentChild);
            if (childName.equals(SchemaSymbols.ELT_GROUP)) {
                particle = this.fSchemaHandler.fGroupTraverser.traverseLocal(complexContentChild, schemaDoc, grammar);
                attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            }
            else if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
                particle = this.traverseSequence(complexContentChild, schemaDoc, grammar, 0, this.fComplexTypeDecl);
                if (particle != null) {
                    final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
                    if (group.fParticleCount == 0) {
                        emptyParticle = true;
                    }
                }
                attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            }
            else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
                particle = this.traverseChoice(complexContentChild, schemaDoc, grammar, 0, this.fComplexTypeDecl);
                if (particle != null && particle.fMinOccurs == 0) {
                    final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
                    if (group.fParticleCount == 0) {
                        emptyParticle = true;
                    }
                }
                attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            }
            else if (childName.equals(SchemaSymbols.ELT_ALL)) {
                particle = this.traverseAll(complexContentChild, schemaDoc, grammar, 8, this.fComplexTypeDecl);
                if (particle != null) {
                    final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
                    if (group.fParticleCount == 0) {
                        emptyParticle = true;
                    }
                }
                attrNode = DOMUtil.getNextSiblingElement(complexContentChild);
            }
            else {
                attrNode = complexContentChild;
            }
        }
        if (emptyParticle) {
            Element child = DOMUtil.getFirstChildElement(complexContentChild);
            if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                child = DOMUtil.getNextSiblingElement(child);
            }
            if (child == null) {
                particle = null;
            }
        }
        if (particle == null && isMixed) {
            particle = XSConstraints.getEmptySequence();
        }
        this.fParticle = particle;
        if (this.fParticle == null) {
            this.fContentType = 0;
        }
        else if (isMixed) {
            this.fContentType = 3;
        }
        else {
            this.fContentType = 2;
        }
        if (attrNode != null) {
            if (!this.isAttrOrAttrGroup(attrNode)) {
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(attrNode) }, attrNode);
            }
            final Element node = this.traverseAttrsAndAttrGrps(attrNode, this.fAttrGrp, schemaDoc, grammar, this.fComplexTypeDecl);
            if (node != null) {
                throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(node) }, node);
            }
            if (!isDerivation) {
                this.fAttrGrp.removeProhibitedAttrs();
            }
        }
    }
    
    private boolean isAttrOrAttrGroup(final Element e) {
        final String elementName = DOMUtil.getLocalName(e);
        return elementName.equals(SchemaSymbols.ELT_ATTRIBUTE) || elementName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP) || elementName.equals(SchemaSymbols.ELT_ANYATTRIBUTE);
    }
    
    private void traverseSimpleContentDecl(final Element simpleContentDecl) {
    }
    
    private void traverseComplexContentDecl(final Element complexContentDecl, final boolean mixedOnComplexTypeDecl) {
    }
    
    private String genAnonTypeName(final Element complexTypeDecl) {
        final StringBuffer typeName = new StringBuffer("#AnonType_");
        for (Element node = DOMUtil.getParent(complexTypeDecl); node != null && node != DOMUtil.getRoot(DOMUtil.getDocument(node)); node = DOMUtil.getParent(node)) {
            typeName.append(node.getAttribute(SchemaSymbols.ATT_NAME));
        }
        return typeName.toString();
    }
    
    private void handleComplexTypeError(final String messageId, final Object[] args, final Element e) {
        if (messageId != null) {
            this.reportSchemaError(messageId, args, e);
        }
        this.fBaseType = SchemaGrammar.fAnyType;
        this.fContentType = 3;
        this.fXSSimpleType = null;
        this.fParticle = getErrorContent();
        this.fAttrGrp.fAttributeWC = getErrorWildcard();
    }
    
    private void contentBackup() {
        if (this.fGlobalStore == null) {
            this.fGlobalStore = new Object[11];
            this.fGlobalStorePos = 0;
        }
        if (this.fGlobalStorePos == this.fGlobalStore.length) {
            final Object[] newArray = new Object[this.fGlobalStorePos + 11];
            System.arraycopy(this.fGlobalStore, 0, newArray, 0, this.fGlobalStorePos);
            this.fGlobalStore = newArray;
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
    }
    
    private void contentRestore() {
        final Object[] fGlobalStore = this.fGlobalStore;
        final int fGlobalStorePos = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos;
        this.fAnnotations = (XSAnnotationImpl[])fGlobalStore[fGlobalStorePos];
        final Object[] fGlobalStore2 = this.fGlobalStore;
        final int fGlobalStorePos2 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos2;
        this.fXSSimpleType = (XSSimpleType)fGlobalStore2[fGlobalStorePos2];
        final Object[] fGlobalStore3 = this.fGlobalStore;
        final int fGlobalStorePos3 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos3;
        this.fParticle = (XSParticleDecl)fGlobalStore3[fGlobalStorePos3];
        final Object[] fGlobalStore4 = this.fGlobalStore;
        final int fGlobalStorePos4 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos4;
        this.fAttrGrp = (XSAttributeGroupDecl)fGlobalStore4[fGlobalStorePos4];
        final Object[] fGlobalStore5 = this.fGlobalStore;
        final int fGlobalStorePos5 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos5;
        this.fBaseType = (XSTypeDefinition)fGlobalStore5[fGlobalStorePos5];
        final Object[] fGlobalStore6 = this.fGlobalStore;
        final int fGlobalStorePos6 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos6;
        int i = (int)fGlobalStore6[fGlobalStorePos6];
        this.fBlock = (short)(i >> 16);
        this.fContentType = (short)i;
        final Object[] fGlobalStore7 = this.fGlobalStore;
        final int fGlobalStorePos7 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos7;
        i = (int)fGlobalStore7[fGlobalStorePos7];
        this.fDerivedBy = (short)(i >> 16);
        this.fFinal = (short)i;
        final Object[] fGlobalStore8 = this.fGlobalStore;
        final int fGlobalStorePos8 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos8;
        this.fTargetNamespace = (String)fGlobalStore8[fGlobalStorePos8];
        final Object[] fGlobalStore9 = this.fGlobalStore;
        final int fGlobalStorePos9 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos9;
        this.fName = (String)fGlobalStore9[fGlobalStorePos9];
        final Object[] fGlobalStore10 = this.fGlobalStore;
        final int fGlobalStorePos10 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos10;
        this.fIsAbstract = (boolean)fGlobalStore10[fGlobalStorePos10];
        final Object[] fGlobalStore11 = this.fGlobalStore;
        final int fGlobalStorePos11 = this.fGlobalStorePos - 1;
        this.fGlobalStorePos = fGlobalStorePos11;
        this.fComplexTypeDecl = (XSComplexTypeDecl)fGlobalStore11[fGlobalStorePos11];
    }
    
    private void addAnnotation(final XSAnnotationImpl annotation) {
        if (annotation == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[1];
        }
        else {
            final XSAnnotationImpl[] tempArray = new XSAnnotationImpl[this.fAnnotations.length + 1];
            System.arraycopy(this.fAnnotations, 0, tempArray, 0, this.fAnnotations.length);
            this.fAnnotations = tempArray;
        }
        this.fAnnotations[this.fAnnotations.length - 1] = annotation;
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
        
        ComplexTypeRecoverableError(final String msgKey, final Object[] args, final Element e) {
            super(msgKey);
            this.errorSubstText = null;
            this.errorElem = null;
            this.errorSubstText = args;
            this.errorElem = e;
        }
    }
}
