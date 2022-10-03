package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.dv.xs.EqualityHelper;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDAttributeTraverser extends XSDAbstractTraverser
{
    public XSDAttributeTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    protected XSAttributeUseImpl traverseLocal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final XSObject xsObject) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        String normalizedValue = (String)checkAttributes[XSAttributeChecker.ATTIDX_DEFAULT];
        String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_FIXED];
        String localpart = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_REF];
        final XInt xInt = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_USE];
        final Boolean b = (Boolean)checkAttributes[XSAttributeChecker.ATTIDX_INHERITABLE];
        XSObject xsObject2 = null;
        XSAttributeDecl traverseNamedAttr;
        if (element.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
            if (qName != null) {
                traverseNamedAttr = (XSAttributeDecl)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 1, qName, element);
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
                    this.reportSchemaError("src-attribute.3.2", new Object[] { qName.rawname }, element2);
                }
                localpart = qName.localpart;
            }
            else {
                traverseNamedAttr = null;
            }
        }
        else {
            traverseNamedAttr = this.traverseNamedAttr(element, checkAttributes, xsDocumentInfo, schemaGrammar, false, xsObject);
        }
        short fConstraintType = 0;
        if (normalizedValue != null) {
            fConstraintType = 1;
        }
        else if (s != null) {
            fConstraintType = 2;
            normalizedValue = s;
            s = null;
        }
        XSAttributeUseImpl attributeUse = null;
        if (traverseNamedAttr != null) {
            if (this.fSchemaHandler.fDeclPool != null) {
                attributeUse = this.fSchemaHandler.fDeclPool.getAttributeUse();
            }
            else {
                attributeUse = new XSAttributeUseImpl();
            }
            attributeUse.fAttrDecl = traverseNamedAttr;
            attributeUse.fUse = xInt.shortValue();
            attributeUse.fConstraintType = fConstraintType;
            if (normalizedValue != null) {
                attributeUse.fDefault = new ValidatedInfo();
                attributeUse.fDefault.normalizedValue = normalizedValue;
            }
            if (this.fSchemaHandler.fSchemaVersion >= 4) {
                if (element.getAttributeNode(SchemaSymbols.ATT_INHERITABLE) != null) {
                    attributeUse.fInheritable = b;
                }
                else {
                    attributeUse.fInheritable = traverseNamedAttr.getInheritable();
                }
            }
            else {
                attributeUse.fInheritable = false;
            }
            if (element.getAttributeNode(SchemaSymbols.ATT_REF) == null) {
                attributeUse.fAnnotations = traverseNamedAttr.getAnnotations();
            }
            else {
                XSObjectListImpl empty_LIST;
                if (xsObject2 != null) {
                    empty_LIST = new XSObjectListImpl();
                    empty_LIST.addXSObject(xsObject2);
                }
                else {
                    empty_LIST = XSObjectListImpl.EMPTY_LIST;
                }
                attributeUse.fAnnotations = empty_LIST;
            }
        }
        if (normalizedValue != null && s != null) {
            this.reportSchemaError("src-attribute.1", new Object[] { localpart }, element);
        }
        if (fConstraintType == 1 && xInt != null && xInt.intValue() != 0) {
            this.reportSchemaError("src-attribute.2", new Object[] { localpart }, element);
            attributeUse.fUse = 0;
        }
        if (fConstraintType == 2 && xInt != null && xInt.intValue() == 2 && this.fSchemaHandler.fSchemaVersion == 4) {
            this.reportSchemaError("src-attribute.5", new Object[] { localpart }, element);
            attributeUse.fUse = 0;
        }
        if (normalizedValue != null && attributeUse != null) {
            this.fValidationState.setNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
            this.fValidationState.setDatatypeXMLVersion(xsDocumentInfo.fDatatypeXMLVersion);
            try {
                this.checkDefaultValid(attributeUse);
            }
            catch (final InvalidDatatypeValueException ex) {
                this.reportSchemaError(ex.getKey(), ex.getArgs(), element);
                this.reportSchemaError("a-props-correct.2", new Object[] { localpart, normalizedValue }, element);
                attributeUse.fDefault = null;
                attributeUse.fConstraintType = 0;
            }
            if (this.fSchemaHandler.fSchemaVersion < 4 && ((XSSimpleType)traverseNamedAttr.getTypeDefinition()).isIDType()) {
                this.reportSchemaError("a-props-correct.3", new Object[] { localpart }, element);
                attributeUse.fDefault = null;
                attributeUse.fConstraintType = 0;
            }
            if (attributeUse.fAttrDecl.getConstraintType() == 2 && attributeUse.fConstraintType != 0 && (attributeUse.fConstraintType != 2 || !EqualityHelper.isEqual(attributeUse.fAttrDecl.getValInfo(), attributeUse.fDefault, this.fSchemaHandler.fSchemaVersion))) {
                this.reportSchemaError("au-props-correct.2", new Object[] { localpart, attributeUse.fAttrDecl.getValInfo().stringValue() }, element);
                attributeUse.fDefault = attributeUse.fAttrDecl.getValInfo();
                attributeUse.fConstraintType = 2;
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return attributeUse;
    }
    
    protected XSAttributeDecl traverseGlobal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        final XSAttributeDecl traverseNamedAttr = this.traverseNamedAttr(element, checkAttributes, xsDocumentInfo, schemaGrammar, true, null);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return traverseNamedAttr;
    }
    
    XSAttributeDecl traverseNamedAttr(final Element element, final Object[] array, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final boolean b, final XSObject xsObject) {
        final String normalizedValue = (String)array[XSAttributeChecker.ATTIDX_DEFAULT];
        final String normalizedValue2 = (String)array[XSAttributeChecker.ATTIDX_FIXED];
        final XInt xInt = (XInt)array[XSAttributeChecker.ATTIDX_FORM];
        String addSymbol = (String)array[XSAttributeChecker.ATTIDX_NAME];
        final String s = (String)array[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
        final QName fUnresolvedTypeName = (QName)array[XSAttributeChecker.ATTIDX_TYPE];
        final Boolean b2 = (Boolean)array[XSAttributeChecker.ATTIDX_INHERITABLE];
        XSAttributeDecl attributeDecl;
        if (this.fSchemaHandler.fDeclPool != null) {
            attributeDecl = this.fSchemaHandler.fDeclPool.getAttributeDecl();
        }
        else {
            attributeDecl = new XSAttributeDecl();
        }
        if (addSymbol != null) {
            addSymbol = this.fSymbolTable.addSymbol(addSymbol);
        }
        String s2 = null;
        XSComplexTypeDecl xsComplexTypeDecl = null;
        XSObject xsObject2 = null;
        short n = 0;
        if (b) {
            s2 = xsDocumentInfo.fTargetNamespace;
            n = 1;
        }
        else {
            if (xsObject != null) {
                if (xsObject instanceof XSComplexTypeDecl) {
                    xsComplexTypeDecl = (XSComplexTypeDecl)xsObject;
                    xsObject2 = xsObject;
                    n = 2;
                }
                else if (this.fSchemaHandler.fSchemaVersion == 4) {
                    xsObject2 = xsObject;
                    n = 2;
                }
            }
            if (s != null) {
                s2 = this.fSymbolTable.addSymbol(s);
            }
            else if (xInt != null) {
                if (xInt.intValue() == 1) {
                    s2 = xsDocumentInfo.fTargetNamespace;
                }
            }
            else if (xsDocumentInfo.fAreLocalAttributesQualified) {
                s2 = xsDocumentInfo.fTargetNamespace;
            }
        }
        ValidatedInfo validatedInfo = null;
        short n2 = 0;
        if (b) {
            if (normalizedValue2 != null) {
                validatedInfo = new ValidatedInfo();
                validatedInfo.normalizedValue = normalizedValue2;
                n2 = 2;
            }
            else if (normalizedValue != null) {
                validatedInfo = new ValidatedInfo();
                validatedInfo.normalizedValue = normalizedValue;
                n2 = 1;
            }
        }
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject3 = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xsObject3 = this.traverseAnnotationDecl(element2, array, false, xsDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsObject3 = this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo);
            }
        }
        XSSimpleType xsSimpleType = null;
        boolean b3 = false;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            xsSimpleType = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, attributeDecl);
            b3 = true;
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        if (xsSimpleType == null && fUnresolvedTypeName != null) {
            final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 7, fUnresolvedTypeName, element);
            if (xsTypeDefinition != null && xsTypeDefinition.getTypeCategory() == 16) {
                xsSimpleType = (XSSimpleType)xsTypeDefinition;
            }
            else {
                this.reportSchemaError("src-resolve", new Object[] { fUnresolvedTypeName.rawname, "simpleType definition" }, element);
                if (xsTypeDefinition == null) {
                    attributeDecl.fUnresolvedTypeName = fUnresolvedTypeName;
                }
            }
        }
        if (xsSimpleType == null) {
            xsSimpleType = SchemaGrammar.fAnySimpleType;
        }
        XSObjectListImpl empty_LIST;
        if (xsObject3 != null) {
            empty_LIST = new XSObjectListImpl();
            empty_LIST.addXSObject(xsObject3);
        }
        else {
            empty_LIST = XSObjectListImpl.EMPTY_LIST;
        }
        boolean booleanValue = false;
        if (b2 != null) {
            booleanValue = b2;
        }
        attributeDecl.setValues(addSymbol, s2, xsSimpleType, n2, n, validatedInfo, xsObject2, empty_LIST, booleanValue);
        if (addSymbol == null) {
            if (b) {
                this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ATTRIBUTE, SchemaSymbols.ATT_NAME }, element);
            }
            else {
                this.reportSchemaError("src-attribute.3.1", null, element);
            }
            addSymbol = "(no name)";
        }
        if (element2 != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { addSymbol, "(annotation?, (simpleType?))", DOMUtil.getLocalName(element2) }, element2);
        }
        if (normalizedValue != null && normalizedValue2 != null) {
            this.reportSchemaError("src-attribute.1", new Object[] { addSymbol }, element);
        }
        if (b3 && fUnresolvedTypeName != null) {
            this.reportSchemaError("src-attribute.4", new Object[] { addSymbol }, element);
        }
        if (this.fSchemaHandler.fSchemaVersion < 4) {
            this.checkNotationType(addSymbol, xsSimpleType, element);
        }
        if (s != null) {
            if (xInt != null) {
                this.reportSchemaError("src-attribute.6.2", new Object[] { addSymbol }, element);
            }
            final String fTargetNamespace = xsDocumentInfo.fTargetNamespace;
            if (fTargetNamespace == null || s2 != fTargetNamespace) {
                if (xsComplexTypeDecl == null) {
                    this.reportSchemaError("src-attribute.6.3.1", new Object[] { addSymbol }, element);
                }
                else if (xsComplexTypeDecl.getDerivationMethod() != 2 || xsComplexTypeDecl.getBaseType() == SchemaGrammar.getXSAnyType(this.fSchemaHandler.fSchemaVersion)) {
                    this.reportSchemaError("src-attribute.6.3.2", new Object[] { addSymbol }, element);
                }
            }
        }
        if (validatedInfo != null) {
            this.fValidationState.setNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
            this.fValidationState.setDatatypeXMLVersion(xsDocumentInfo.fDatatypeXMLVersion);
            try {
                this.checkDefaultValid(attributeDecl);
            }
            catch (final InvalidDatatypeValueException ex) {
                this.reportSchemaError(ex.getKey(), ex.getArgs(), element);
                this.reportSchemaError("a-props-correct.2", new Object[] { addSymbol, validatedInfo.normalizedValue }, element);
                validatedInfo = null;
                attributeDecl.setValues(addSymbol, s2, xsSimpleType, (short)0, n, validatedInfo, xsObject2, empty_LIST, booleanValue);
            }
        }
        if (this.fSchemaHandler.fSchemaVersion < 4 && validatedInfo != null && xsSimpleType.isIDType()) {
            this.reportSchemaError("a-props-correct.3", new Object[] { addSymbol }, element);
            attributeDecl.setValues(addSymbol, s2, xsSimpleType, (short)0, n, null, xsObject2, empty_LIST, booleanValue);
        }
        if (addSymbol != null && addSymbol.equals(XMLSymbols.PREFIX_XMLNS)) {
            this.reportSchemaError("no-xmlns", null, element);
            return null;
        }
        if (s2 != null && s2.equals(SchemaSymbols.URI_XSI)) {
            this.reportSchemaError("no-xsi", new Object[] { SchemaSymbols.URI_XSI }, element);
            return null;
        }
        if (addSymbol.equals("(no name)")) {
            return null;
        }
        if (b) {
            if (schemaGrammar.getGlobalAttributeDecl(addSymbol) == null) {
                schemaGrammar.addGlobalAttributeDecl(attributeDecl);
            }
            final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
            final XSAttributeDecl globalAttributeDecl = schemaGrammar.getGlobalAttributeDecl(addSymbol, schemaDocument2SystemId);
            if (globalAttributeDecl == null) {
                schemaGrammar.addGlobalAttributeDecl(attributeDecl, schemaDocument2SystemId);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (globalAttributeDecl != null) {
                    attributeDecl = globalAttributeDecl;
                }
                this.fSchemaHandler.addGlobalAttributeDecl(attributeDecl);
            }
        }
        return attributeDecl;
    }
    
    void checkDefaultValid(final XSAttributeDecl xsAttributeDecl) throws InvalidDatatypeValueException {
        ((XSSimpleType)xsAttributeDecl.getTypeDefinition()).validate(xsAttributeDecl.getValInfo().normalizedValue, this.fValidationState, xsAttributeDecl.getValInfo());
        ((XSSimpleType)xsAttributeDecl.getTypeDefinition()).validate(xsAttributeDecl.getValInfo().stringValue(), this.fValidationState, xsAttributeDecl.getValInfo());
    }
    
    void checkDefaultValid(final XSAttributeUseImpl xsAttributeUseImpl) throws InvalidDatatypeValueException {
        ((XSSimpleType)xsAttributeUseImpl.fAttrDecl.getTypeDefinition()).validate(xsAttributeUseImpl.fDefault.normalizedValue, this.fValidationState, xsAttributeUseImpl.fDefault);
        ((XSSimpleType)xsAttributeUseImpl.fAttrDecl.getTypeDefinition()).validate(xsAttributeUseImpl.fDefault.stringValue(), this.fValidationState, xsAttributeUseImpl.fDefault);
    }
}
