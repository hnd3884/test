package org.apache.xerces.impl.xs.traversers;

import java.util.Locale;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import java.util.Vector;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Attr;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;
import org.apache.xerces.impl.xs.XSElementDecl;

class XSDElementTraverser extends XSDAbstractTraverser
{
    protected final XSElementDecl fTempElementDecl;
    boolean fDeferTraversingLocalElements;
    
    XSDElementTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
        this.fTempElementDecl = new XSElementDecl();
    }
    
    XSParticleDecl traverseLocal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final int n, final XSObject xsObject) {
        XSParticleDecl particleDecl;
        if (this.fSchemaHandler.fDeclPool != null) {
            particleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
        }
        else {
            particleDecl = new XSParticleDecl();
        }
        if (this.fDeferTraversingLocalElements) {
            particleDecl.fType = 1;
            final Attr attributeNode = element.getAttributeNode(SchemaSymbols.ATT_MINOCCURS);
            if (attributeNode != null) {
                final String value = attributeNode.getValue();
                try {
                    final int int1 = Integer.parseInt(XMLChar.trim(value));
                    if (int1 >= 0) {
                        particleDecl.fMinOccurs = int1;
                    }
                }
                catch (final NumberFormatException ex) {}
            }
            this.fSchemaHandler.fillInLocalElemInfo(element, xsDocumentInfo, n, xsObject, particleDecl);
        }
        else {
            this.traverseLocal(particleDecl, element, xsDocumentInfo, schemaGrammar, n, xsObject, null);
            if (particleDecl.fType == 0) {
                particleDecl = null;
            }
        }
        return particleDecl;
    }
    
    protected void traverseLocal(final XSParticleDecl xsParticleDecl, final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final int n, final XSObject xsObject, final String[] effectiveContext) {
        if (effectiveContext != null) {
            xsDocumentInfo.fNamespaceSupport.setEffectiveContext(effectiveContext);
        }
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_REF];
        final XInt xInt = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt xInt2 = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MAXOCCURS];
        XSObject xsObject2 = null;
        XSElementDecl traverseNamedElement;
        if (element.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
            if (qName != null) {
                traverseNamedElement = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 3, qName, element);
                Element element2 = DOMUtil.getFirstChildElement(element);
                if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    xsObject2 = this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo);
                    element2 = DOMUtil.getNextSiblingElement(element2);
                }
                else {
                    final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                    if (syntheticAnnotation != null) {
                        xsObject2 = this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
                    }
                }
                if (element2 != null) {
                    this.reportSchemaError("src-element.2.2", new Object[] { qName.rawname, DOMUtil.getLocalName(element2) }, element2);
                }
            }
            else {
                traverseNamedElement = null;
            }
        }
        else {
            traverseNamedElement = this.traverseNamedElement(element, checkAttributes, xsDocumentInfo, schemaGrammar, false, xsObject);
        }
        xsParticleDecl.fMinOccurs = xInt.intValue();
        xsParticleDecl.fMaxOccurs = xInt2.intValue();
        if (traverseNamedElement != null) {
            xsParticleDecl.fType = 1;
            xsParticleDecl.fValue = traverseNamedElement;
        }
        else {
            xsParticleDecl.fType = 0;
        }
        if (qName != null) {
            XSObjectListImpl empty_LIST;
            if (xsObject2 != null) {
                empty_LIST = new XSObjectListImpl();
                empty_LIST.addXSObject(xsObject2);
            }
            else {
                empty_LIST = XSObjectListImpl.EMPTY_LIST;
            }
            xsParticleDecl.fAnnotations = empty_LIST;
        }
        else {
            xsParticleDecl.fAnnotations = ((traverseNamedElement != null) ? traverseNamedElement.fAnnotations : XSObjectListImpl.EMPTY_LIST);
        }
        this.checkOccurrences(xsParticleDecl, SchemaSymbols.ELT_ELEMENT, (Element)element.getParentNode(), n, (long)checkAttributes[XSAttributeChecker.ATTIDX_FROMDEFAULT]);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
    }
    
    XSElementDecl traverseGlobal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        final XSElementDecl traverseNamedElement = this.traverseNamedElement(element, checkAttributes, xsDocumentInfo, schemaGrammar, true, null);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return traverseNamedElement;
    }
    
    XSElementDecl traverseNamedElement(final Element element, final Object[] array, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final boolean b, final XSObject isLocal) {
        final Boolean b2 = (Boolean)array[XSAttributeChecker.ATTIDX_ABSTRACT];
        final XInt xInt = (XInt)array[XSAttributeChecker.ATTIDX_BLOCK];
        final String normalizedValue = (String)array[XSAttributeChecker.ATTIDX_DEFAULT];
        final XInt xInt2 = (XInt)array[XSAttributeChecker.ATTIDX_FINAL];
        final String normalizedValue2 = (String)array[XSAttributeChecker.ATTIDX_FIXED];
        final XInt xInt3 = (XInt)array[XSAttributeChecker.ATTIDX_FORM];
        String s = (String)array[XSAttributeChecker.ATTIDX_NAME];
        final Boolean b3 = (Boolean)array[XSAttributeChecker.ATTIDX_NILLABLE];
        final Vector vector = (Vector)array[XSAttributeChecker.ATTIDX_SUBSGROUP];
        String addSymbol = (String)array[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
        final QName fUnresolvedTypeName = (QName)array[XSAttributeChecker.ATTIDX_TYPE];
        XSElementDecl elementDecl;
        if (this.fSchemaHandler.fDeclPool != null) {
            elementDecl = this.fSchemaHandler.fDeclPool.getElementDecl();
        }
        else {
            elementDecl = new XSElementDecl();
        }
        if (s != null) {
            elementDecl.fName = this.fSymbolTable.addSymbol(s);
        }
        if (b) {
            elementDecl.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
            elementDecl.setIsGlobal();
        }
        else {
            if (isLocal != null && (isLocal instanceof XSComplexTypeDecl || this.fSchemaHandler.fSchemaVersion == 4)) {
                elementDecl.setIsLocal(isLocal);
            }
            if (addSymbol != null) {
                addSymbol = this.fSymbolTable.addSymbol(addSymbol);
                elementDecl.fTargetNamespace = addSymbol;
            }
            else if (xInt3 != null) {
                if (xInt3.intValue() == 1) {
                    elementDecl.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
                }
                else {
                    elementDecl.fTargetNamespace = null;
                }
            }
            else if (xsDocumentInfo.fAreLocalElementsQualified) {
                elementDecl.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
            }
            else {
                elementDecl.fTargetNamespace = null;
            }
        }
        elementDecl.fBlock = ((xInt == null) ? xsDocumentInfo.fBlockDefault : xInt.shortValue());
        elementDecl.fFinal = ((xInt2 == null) ? xsDocumentInfo.fFinalDefault : xInt2.shortValue());
        final XSElementDecl xsElementDecl = elementDecl;
        xsElementDecl.fBlock &= 0x7;
        final XSElementDecl xsElementDecl2 = elementDecl;
        xsElementDecl2.fFinal &= 0x3;
        if (b3) {
            elementDecl.setIsNillable();
        }
        if (b2 != null && b2) {
            elementDecl.setIsAbstract();
        }
        if (normalizedValue2 != null) {
            elementDecl.fDefault = new ValidatedInfo();
            elementDecl.fDefault.normalizedValue = normalizedValue2;
            elementDecl.setConstraintType((short)2);
        }
        else if (normalizedValue != null) {
            elementDecl.fDefault = new ValidatedInfo();
            elementDecl.fDefault.normalizedValue = normalizedValue;
            elementDecl.setConstraintType((short)1);
        }
        else {
            elementDecl.setConstraintType((short)0);
        }
        if (vector != null && !vector.isEmpty()) {
            final Vector vector2 = new Vector<XSElementDecl>();
            for (int i = 0; i < vector.size(); ++i) {
                final XSElementDecl xsElementDecl3 = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 3, vector.get(i), element);
                if (xsElementDecl3 != null) {
                    vector2.add(xsElementDecl3);
                }
            }
            final int size = vector2.size();
            if (size > 0) {
                elementDecl.fSubGroup = (XSElementDecl[])vector2.toArray(new XSElementDecl[size]);
            }
        }
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xsObject = this.traverseAnnotationDecl(element2, array, false, xsDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsObject = this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo);
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
        elementDecl.fAnnotations = empty_LIST;
        Object fType = null;
        boolean b4 = false;
        if (element2 != null) {
            final String localName = DOMUtil.getLocalName(element2);
            if (localName.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                fType = this.fSchemaHandler.fComplexTypeTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, elementDecl);
                b4 = true;
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            else if (localName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                fType = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, elementDecl);
                b4 = true;
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
        }
        if (fType == null && fUnresolvedTypeName != null) {
            fType = this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 7, fUnresolvedTypeName, element);
            if (fType == null) {
                elementDecl.fUnresolvedTypeName = fUnresolvedTypeName;
            }
        }
        if (fType == null && elementDecl.fSubGroup != null) {
            fType = elementDecl.fSubGroup[0].fType;
        }
        if (fType == null) {
            fType = SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion);
        }
        elementDecl.fType = (XSTypeDefinition)fType;
        if (element2 != null) {
            String s2 = DOMUtil.getLocalName(element2);
            if (this.fSchemaHandler.fSchemaVersion == 4) {
                while (s2.equals(SchemaSymbols.ELT_ALTERNATIVE)) {
                    this.fSchemaHandler.fTypeAlternativeTraverser.traverse(element2, elementDecl, xsDocumentInfo, schemaGrammar);
                    element2 = DOMUtil.getNextSiblingElement(element2);
                    if (element2 != null) {
                        s2 = DOMUtil.getLocalName(element2);
                    }
                    else {
                        if (!elementDecl.isTypeTableOK()) {
                            this.reportSchemaError("src-element.5", new Object[] { s }, element);
                        }
                        elementDecl.setDefaultTypeDefinition();
                        if (elementDecl.getDefaultTypeDefinition() != null) {
                            elementDecl.getDefaultTypeDefinition().setNamespaceContext(new NamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                            break;
                        }
                        break;
                    }
                }
            }
            while (element2 != null && (s2.equals(SchemaSymbols.ELT_KEY) || s2.equals(SchemaSymbols.ELT_KEYREF) || s2.equals(SchemaSymbols.ELT_UNIQUE))) {
                if (DOMUtil.getAttr(element2, SchemaSymbols.ATT_REF) != null) {
                    this.fSchemaHandler.storeIdentityConstraintReferral(element2, xsDocumentInfo, elementDecl);
                }
                else if (s2.equals(SchemaSymbols.ELT_KEY) || s2.equals(SchemaSymbols.ELT_UNIQUE)) {
                    DOMUtil.setHidden(element2, this.fSchemaHandler.fHiddenNodes);
                    this.fSchemaHandler.fUniqueOrKeyTraverser.traverse(element2, elementDecl, xsDocumentInfo, schemaGrammar);
                    if (DOMUtil.getAttrValue(element2, SchemaSymbols.ATT_NAME).length() != 0) {
                        this.fSchemaHandler.checkForDuplicateNames((xsDocumentInfo.fTargetNamespace == null) ? ("," + DOMUtil.getAttrValue(element2, SchemaSymbols.ATT_NAME)) : (xsDocumentInfo.fTargetNamespace + "," + DOMUtil.getAttrValue(element2, SchemaSymbols.ATT_NAME)), 1, this.fSchemaHandler.getIDRegistry(), this.fSchemaHandler.getIDRegistry_sub(), element2, xsDocumentInfo);
                    }
                }
                else if (s2.equals(SchemaSymbols.ELT_KEYREF)) {
                    this.fSchemaHandler.storeKeyRef(element2, xsDocumentInfo, elementDecl);
                }
                element2 = DOMUtil.getNextSiblingElement(element2);
                if (element2 != null) {
                    s2 = DOMUtil.getLocalName(element2);
                }
            }
        }
        if (s == null) {
            if (b) {
                this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ELEMENT, SchemaSymbols.ATT_NAME }, element);
            }
            else {
                this.reportSchemaError("src-element.2.1", null, element);
            }
            s = "(no name)";
        }
        if (element2 != null) {
            if (this.fSchemaHandler.fSchemaVersion == 4) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { s, "(annotation?, (simpleType | complexType)?, alternative*, (unique | key | keyref)*))", DOMUtil.getLocalName(element2) }, element2);
            }
            else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { s, "(annotation?, (simpleType | complexType)?, (unique | key | keyref)*))", DOMUtil.getLocalName(element2) }, element2);
            }
        }
        if (normalizedValue != null && normalizedValue2 != null) {
            this.reportSchemaError("src-element.1", new Object[] { s }, element);
        }
        if (b4 && fUnresolvedTypeName != null) {
            this.reportSchemaError("src-element.3", new Object[] { s }, element);
        }
        if (addSymbol != null) {
            if (xInt3 != null) {
                this.reportSchemaError("src-element.4.2", new Object[] { s }, element);
            }
            final String fTargetNamespace = xsDocumentInfo.fTargetNamespace;
            if (fTargetNamespace == null || addSymbol != fTargetNamespace) {
                if (isLocal == null || !(isLocal instanceof XSComplexTypeDecl)) {
                    this.reportSchemaError("src-element.4.3.1", new Object[] { s }, element);
                }
                else if (((XSComplexTypeDecl)isLocal).getDerivationMethod() != 2 || ((XSComplexTypeDecl)isLocal).getBaseType() == SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion)) {
                    this.reportSchemaError("src-element.4.3.2", new Object[] { s }, element);
                }
            }
        }
        if (this.fSchemaHandler.fSchemaVersion < 4) {
            this.checkNotationType(s, (XSTypeDefinition)fType, element);
        }
        if (elementDecl.fDefault != null) {
            this.fValidationState.setNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
            this.fValidationState.setDatatypeXMLVersion(xsDocumentInfo.fDatatypeXMLVersion);
            if (this.fSchemaHandler.fXSConstraints.ElementDefaultValidImmediate(elementDecl.fType, elementDecl.fDefault.normalizedValue, this.fValidationState, elementDecl.fDefault) == null) {
                this.reportSchemaError("e-props-correct.2", new Object[] { s, elementDecl.fDefault.normalizedValue }, element);
                elementDecl.fDefault = null;
                elementDecl.setConstraintType((short)0);
            }
        }
        if (elementDecl.fSubGroup != null) {
            for (int j = 0; j < elementDecl.fSubGroup.length; ++j) {
                if (!this.fSchemaHandler.fXSConstraints.checkTypeDerivationOk(elementDecl.fType, elementDecl.fSubGroup[j].fType, elementDecl.fSubGroup[j].fFinal)) {
                    this.reportSchemaError("e-props-correct.4", new Object[] { s, ((QName)vector.get(j)).prefix + ":" + ((QName)vector.get(j)).localpart }, element);
                }
            }
        }
        if (this.fSchemaHandler.fSchemaVersion < 4 && elementDecl.fDefault != null && ((((XSTypeDefinition)fType).getTypeCategory() == 16 && ((XSSimpleType)fType).isIDType()) || (((XSTypeDefinition)fType).getTypeCategory() == 15 && ((XSComplexTypeDecl)fType).containsTypeID()))) {
            this.reportSchemaError("e-props-correct.5", new Object[] { elementDecl.fName }, element);
            elementDecl.fDefault = null;
            elementDecl.setConstraintType((short)0);
        }
        if (elementDecl.fName == null) {
            return null;
        }
        if (b) {
            schemaGrammar.addGlobalElementDeclAll(elementDecl);
            if (schemaGrammar.getGlobalElementDecl(elementDecl.fName) == null) {
                schemaGrammar.addGlobalElementDecl(elementDecl);
            }
            final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
            final XSElementDecl globalElementDecl = schemaGrammar.getGlobalElementDecl(elementDecl.fName, schemaDocument2SystemId);
            if (globalElementDecl == null) {
                schemaGrammar.addGlobalElementDecl(elementDecl, schemaDocument2SystemId);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (globalElementDecl != null) {
                    elementDecl = globalElementDecl;
                }
                this.fSchemaHandler.addGlobalElementDecl(elementDecl);
            }
        }
        return elementDecl;
    }
    
    void reset(final SymbolTable symbolTable, final boolean b, final Locale locale) {
        super.reset(symbolTable, b, locale);
        this.fDeferTraversingLocalElements = true;
    }
}
