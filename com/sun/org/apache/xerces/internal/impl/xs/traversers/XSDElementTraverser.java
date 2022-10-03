package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Map;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;

class XSDElementTraverser extends XSDAbstractTraverser
{
    protected final XSElementDecl fTempElementDecl;
    boolean fDeferTraversingLocalElements;
    
    XSDElementTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
        this.fTempElementDecl = new XSElementDecl();
    }
    
    XSParticleDecl traverseLocal(final Element elmDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final int allContextFlags, final XSObject parent) {
        XSParticleDecl particle = null;
        if (this.fSchemaHandler.fDeclPool != null) {
            particle = this.fSchemaHandler.fDeclPool.getParticleDecl();
        }
        else {
            particle = new XSParticleDecl();
        }
        if (this.fDeferTraversingLocalElements) {
            particle.fType = 1;
            final Attr attr = elmDecl.getAttributeNode(SchemaSymbols.ATT_MINOCCURS);
            if (attr != null) {
                final String min = attr.getValue();
                try {
                    final int m = Integer.parseInt(XMLChar.trim(min));
                    if (m >= 0) {
                        particle.fMinOccurs = m;
                    }
                }
                catch (final NumberFormatException ex) {}
            }
            this.fSchemaHandler.fillInLocalElemInfo(elmDecl, schemaDoc, allContextFlags, parent, particle);
        }
        else {
            this.traverseLocal(particle, elmDecl, schemaDoc, grammar, allContextFlags, parent, null);
            if (particle.fType == 0) {
                particle = null;
            }
        }
        return particle;
    }
    
    protected void traverseLocal(final XSParticleDecl particle, final Element elmDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final int allContextFlags, final XSObject parent, final String[] localNSDecls) {
        if (localNSDecls != null) {
            schemaDoc.fNamespaceSupport.setEffectiveContext(localNSDecls);
        }
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmDecl, false, schemaDoc);
        final QName refAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_REF];
        final XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
        XSElementDecl element = null;
        XSAnnotationImpl annotation = null;
        if (elmDecl.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
            if (refAtt != null) {
                element = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(schemaDoc, 3, refAtt, elmDecl);
                Element child = DOMUtil.getFirstChildElement(elmDecl);
                if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                    child = DOMUtil.getNextSiblingElement(child);
                }
                else {
                    final String text = DOMUtil.getSyntheticAnnotation(elmDecl);
                    if (text != null) {
                        annotation = this.traverseSyntheticAnnotation(elmDecl, text, attrValues, false, schemaDoc);
                    }
                }
                if (child != null) {
                    this.reportSchemaError("src-element.2.2", new Object[] { refAtt.rawname, DOMUtil.getLocalName(child) }, child);
                }
            }
            else {
                element = null;
            }
        }
        else {
            element = this.traverseNamedElement(elmDecl, attrValues, schemaDoc, grammar, false, parent);
        }
        particle.fMinOccurs = minAtt.intValue();
        particle.fMaxOccurs = maxAtt.intValue();
        if (element != null) {
            particle.fType = 1;
            particle.fValue = element;
        }
        else {
            particle.fType = 0;
        }
        if (refAtt != null) {
            XSObjectList annotations;
            if (annotation != null) {
                annotations = new XSObjectListImpl();
                ((XSObjectListImpl)annotations).addXSObject(annotation);
            }
            else {
                annotations = XSObjectListImpl.EMPTY_LIST;
            }
            particle.fAnnotations = annotations;
        }
        else {
            particle.fAnnotations = ((element != null) ? element.fAnnotations : XSObjectListImpl.EMPTY_LIST);
        }
        final Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        this.checkOccurrences(particle, SchemaSymbols.ELT_ELEMENT, (Element)elmDecl.getParentNode(), allContextFlags, defaultVals);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
    }
    
    XSElementDecl traverseGlobal(final Element elmDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmDecl, true, schemaDoc);
        final XSElementDecl element = this.traverseNamedElement(elmDecl, attrValues, schemaDoc, grammar, true, null);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return element;
    }
    
    XSElementDecl traverseNamedElement(final Element elmDecl, final Object[] attrValues, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final boolean isGlobal, final XSObject parent) {
        final Boolean abstractAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_ABSTRACT];
        final XInt blockAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_BLOCK];
        final String defaultAtt = (String)attrValues[XSAttributeChecker.ATTIDX_DEFAULT];
        final XInt finalAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FINAL];
        final String fixedAtt = (String)attrValues[XSAttributeChecker.ATTIDX_FIXED];
        final XInt formAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FORM];
        String nameAtt = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        final Boolean nillableAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_NILLABLE];
        final QName subGroupAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_SUBSGROUP];
        final QName typeAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_TYPE];
        XSElementDecl element = null;
        if (this.fSchemaHandler.fDeclPool != null) {
            element = this.fSchemaHandler.fDeclPool.getElementDecl();
        }
        else {
            element = new XSElementDecl();
        }
        if (nameAtt != null) {
            element.fName = this.fSymbolTable.addSymbol(nameAtt);
        }
        if (isGlobal) {
            element.fTargetNamespace = schemaDoc.fTargetNamespace;
            element.setIsGlobal();
        }
        else {
            if (parent instanceof XSComplexTypeDecl) {
                element.setIsLocal((XSComplexTypeDecl)parent);
            }
            if (formAtt != null) {
                if (formAtt.intValue() == 1) {
                    element.fTargetNamespace = schemaDoc.fTargetNamespace;
                }
                else {
                    element.fTargetNamespace = null;
                }
            }
            else if (schemaDoc.fAreLocalElementsQualified) {
                element.fTargetNamespace = schemaDoc.fTargetNamespace;
            }
            else {
                element.fTargetNamespace = null;
            }
        }
        if (blockAtt == null) {
            element.fBlock = schemaDoc.fBlockDefault;
            if (element.fBlock != 31) {
                final XSElementDecl xsElementDecl = element;
                xsElementDecl.fBlock &= 0x7;
            }
        }
        else {
            element.fBlock = blockAtt.shortValue();
            if (element.fBlock != 31 && (element.fBlock | 0x7) != 0x7) {
                this.reportSchemaError("s4s-att-invalid-value", new Object[] { element.fName, "block", "must be (#all | List of (extension | restriction | substitution))" }, elmDecl);
            }
        }
        element.fFinal = ((finalAtt == null) ? schemaDoc.fFinalDefault : finalAtt.shortValue());
        final XSElementDecl xsElementDecl2 = element;
        xsElementDecl2.fFinal &= 0x3;
        if (nillableAtt) {
            element.setIsNillable();
        }
        if (abstractAtt != null && abstractAtt) {
            element.setIsAbstract();
        }
        if (fixedAtt != null) {
            element.fDefault = new ValidatedInfo();
            element.fDefault.normalizedValue = fixedAtt;
            element.setConstraintType((short)2);
        }
        else if (defaultAtt != null) {
            element.fDefault = new ValidatedInfo();
            element.fDefault.normalizedValue = defaultAtt;
            element.setConstraintType((short)1);
        }
        else {
            element.setConstraintType((short)0);
        }
        if (subGroupAtt != null) {
            element.fSubGroup = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(schemaDoc, 3, subGroupAtt, elmDecl);
        }
        Element child = DOMUtil.getFirstChildElement(elmDecl);
        XSAnnotationImpl annotation = null;
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(elmDecl);
            if (text != null) {
                annotation = this.traverseSyntheticAnnotation(elmDecl, text, attrValues, false, schemaDoc);
            }
        }
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
        }
        else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        element.fAnnotations = annotations;
        XSTypeDefinition elementType = null;
        boolean haveAnonType = false;
        if (child != null) {
            final String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                elementType = this.fSchemaHandler.fComplexTypeTraverser.traverseLocal(child, schemaDoc, grammar);
                haveAnonType = true;
                child = DOMUtil.getNextSiblingElement(child);
            }
            else if (childName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                elementType = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(child, schemaDoc, grammar);
                haveAnonType = true;
                child = DOMUtil.getNextSiblingElement(child);
            }
        }
        if (elementType == null && typeAtt != null) {
            elementType = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, typeAtt, elmDecl);
            if (elementType == null) {
                element.fUnresolvedTypeName = typeAtt;
            }
        }
        if (elementType == null && element.fSubGroup != null) {
            elementType = element.fSubGroup.fType;
        }
        if (elementType == null) {
            elementType = SchemaGrammar.fAnyType;
        }
        element.fType = elementType;
        if (child != null) {
            for (String childName = DOMUtil.getLocalName(child); child != null && (childName.equals(SchemaSymbols.ELT_KEY) || childName.equals(SchemaSymbols.ELT_KEYREF) || childName.equals(SchemaSymbols.ELT_UNIQUE)); childName = DOMUtil.getLocalName(child)) {
                if (childName.equals(SchemaSymbols.ELT_KEY) || childName.equals(SchemaSymbols.ELT_UNIQUE)) {
                    DOMUtil.setHidden(child, this.fSchemaHandler.fHiddenNodes);
                    this.fSchemaHandler.fUniqueOrKeyTraverser.traverse(child, element, schemaDoc, grammar);
                    if (DOMUtil.getAttrValue(child, SchemaSymbols.ATT_NAME).length() != 0) {
                        final XSDHandler fSchemaHandler = this.fSchemaHandler;
                        final String qName = (schemaDoc.fTargetNamespace == null) ? ("," + DOMUtil.getAttrValue(child, SchemaSymbols.ATT_NAME)) : (schemaDoc.fTargetNamespace + "," + DOMUtil.getAttrValue(child, SchemaSymbols.ATT_NAME));
                        final XSDHandler fSchemaHandler2 = this.fSchemaHandler;
                        fSchemaHandler.checkForDuplicateNames(qName, 1, this.fSchemaHandler.getIDRegistry(), this.fSchemaHandler.getIDRegistry_sub(), child, schemaDoc);
                    }
                }
                else if (childName.equals(SchemaSymbols.ELT_KEYREF)) {
                    this.fSchemaHandler.storeKeyRef(child, schemaDoc, element);
                }
                child = DOMUtil.getNextSiblingElement(child);
                if (child != null) {}
            }
        }
        if (nameAtt == null) {
            if (isGlobal) {
                this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ELEMENT, SchemaSymbols.ATT_NAME }, elmDecl);
            }
            else {
                this.reportSchemaError("src-element.2.1", null, elmDecl);
            }
            nameAtt = "(no name)";
        }
        if (child != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { nameAtt, "(annotation?, (simpleType | complexType)?, (unique | key | keyref)*))", DOMUtil.getLocalName(child) }, child);
        }
        if (defaultAtt != null && fixedAtt != null) {
            this.reportSchemaError("src-element.1", new Object[] { nameAtt }, elmDecl);
        }
        if (haveAnonType && typeAtt != null) {
            this.reportSchemaError("src-element.3", new Object[] { nameAtt }, elmDecl);
        }
        this.checkNotationType(nameAtt, elementType, elmDecl);
        if (element.fDefault != null) {
            this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
            if (XSConstraints.ElementDefaultValidImmediate(element.fType, element.fDefault.normalizedValue, this.fValidationState, element.fDefault) == null) {
                this.reportSchemaError("e-props-correct.2", new Object[] { nameAtt, element.fDefault.normalizedValue }, elmDecl);
                element.fDefault = null;
                element.setConstraintType((short)0);
            }
        }
        if (element.fSubGroup != null && !XSConstraints.checkTypeDerivationOk(element.fType, element.fSubGroup.fType, element.fSubGroup.fFinal)) {
            this.reportSchemaError("e-props-correct.4", new Object[] { nameAtt, subGroupAtt.prefix + ":" + subGroupAtt.localpart }, elmDecl);
            element.fSubGroup = null;
        }
        if (element.fDefault != null && ((elementType.getTypeCategory() == 16 && ((XSSimpleType)elementType).isIDType()) || (elementType.getTypeCategory() == 15 && ((XSComplexTypeDecl)elementType).containsTypeID()))) {
            this.reportSchemaError("e-props-correct.5", new Object[] { element.fName }, elmDecl);
            element.fDefault = null;
            element.setConstraintType((short)0);
        }
        if (element.fName == null) {
            return null;
        }
        if (isGlobal) {
            grammar.addGlobalElementDeclAll(element);
            if (grammar.getGlobalElementDecl(element.fName) == null) {
                grammar.addGlobalElementDecl(element);
            }
            final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            final XSElementDecl element2 = grammar.getGlobalElementDecl(element.fName, loc);
            if (element2 == null) {
                grammar.addGlobalElementDecl(element, loc);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (element2 != null) {
                    element = element2;
                }
                this.fSchemaHandler.addGlobalElementDecl(element);
            }
        }
        return element;
    }
    
    @Override
    void reset(final SymbolTable symbolTable, final boolean validateAnnotations, final Locale locale) {
        super.reset(symbolTable, validateAnnotations, locale);
        this.fDeferTraversingLocalElements = true;
    }
}
