package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDAttributeTraverser extends XSDAbstractTraverser
{
    public XSDAttributeTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    protected XSAttributeUseImpl traverseLocal(final Element attrDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final XSComplexTypeDecl enclosingCT) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(attrDecl, false, schemaDoc);
        String defaultAtt = (String)attrValues[XSAttributeChecker.ATTIDX_DEFAULT];
        String fixedAtt = (String)attrValues[XSAttributeChecker.ATTIDX_FIXED];
        String nameAtt = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        final QName refAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_REF];
        final XInt useAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_USE];
        XSAttributeDecl attribute = null;
        XSAnnotationImpl annotation = null;
        if (attrDecl.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
            if (refAtt != null) {
                attribute = (XSAttributeDecl)this.fSchemaHandler.getGlobalDecl(schemaDoc, 1, refAtt, attrDecl);
                Element child = DOMUtil.getFirstChildElement(attrDecl);
                if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                    child = DOMUtil.getNextSiblingElement(child);
                }
                else {
                    final String text = DOMUtil.getSyntheticAnnotation(attrDecl);
                    if (text != null) {
                        annotation = this.traverseSyntheticAnnotation(attrDecl, text, attrValues, false, schemaDoc);
                    }
                }
                if (child != null) {
                    this.reportSchemaError("src-attribute.3.2", new Object[] { refAtt.rawname }, child);
                }
                nameAtt = refAtt.localpart;
            }
            else {
                attribute = null;
            }
        }
        else {
            attribute = this.traverseNamedAttr(attrDecl, attrValues, schemaDoc, grammar, false, enclosingCT);
        }
        short consType = 0;
        if (defaultAtt != null) {
            consType = 1;
        }
        else if (fixedAtt != null) {
            consType = 2;
            defaultAtt = fixedAtt;
            fixedAtt = null;
        }
        XSAttributeUseImpl attrUse = null;
        if (attribute != null) {
            if (this.fSchemaHandler.fDeclPool != null) {
                attrUse = this.fSchemaHandler.fDeclPool.getAttributeUse();
            }
            else {
                attrUse = new XSAttributeUseImpl();
            }
            attrUse.fAttrDecl = attribute;
            attrUse.fUse = useAtt.shortValue();
            attrUse.fConstraintType = consType;
            if (defaultAtt != null) {
                attrUse.fDefault = new ValidatedInfo();
                attrUse.fDefault.normalizedValue = defaultAtt;
            }
            if (attrDecl.getAttributeNode(SchemaSymbols.ATT_REF) == null) {
                attrUse.fAnnotations = attribute.getAnnotations();
            }
            else {
                XSObjectList annotations;
                if (annotation != null) {
                    annotations = new XSObjectListImpl();
                    ((XSObjectListImpl)annotations).addXSObject(annotation);
                }
                else {
                    annotations = XSObjectListImpl.EMPTY_LIST;
                }
                attrUse.fAnnotations = annotations;
            }
        }
        if (defaultAtt != null && fixedAtt != null) {
            this.reportSchemaError("src-attribute.1", new Object[] { nameAtt }, attrDecl);
        }
        if (consType == 1 && useAtt != null && useAtt.intValue() != 0) {
            this.reportSchemaError("src-attribute.2", new Object[] { nameAtt }, attrDecl);
            attrUse.fUse = 0;
        }
        if (defaultAtt != null && attrUse != null) {
            this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
            try {
                this.checkDefaultValid(attrUse);
            }
            catch (final InvalidDatatypeValueException ide) {
                this.reportSchemaError(ide.getKey(), ide.getArgs(), attrDecl);
                this.reportSchemaError("a-props-correct.2", new Object[] { nameAtt, defaultAtt }, attrDecl);
                attrUse.fDefault = null;
                attrUse.fConstraintType = 0;
            }
            if (((XSSimpleType)attribute.getTypeDefinition()).isIDType()) {
                this.reportSchemaError("a-props-correct.3", new Object[] { nameAtt }, attrDecl);
                attrUse.fDefault = null;
                attrUse.fConstraintType = 0;
            }
            if (attrUse.fAttrDecl.getConstraintType() == 2 && attrUse.fConstraintType != 0 && (attrUse.fConstraintType != 2 || !attrUse.fAttrDecl.getValInfo().actualValue.equals(attrUse.fDefault.actualValue))) {
                this.reportSchemaError("au-props-correct.2", new Object[] { nameAtt, attrUse.fAttrDecl.getValInfo().stringValue() }, attrDecl);
                attrUse.fDefault = attrUse.fAttrDecl.getValInfo();
                attrUse.fConstraintType = 2;
            }
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrUse;
    }
    
    protected XSAttributeDecl traverseGlobal(final Element attrDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(attrDecl, true, schemaDoc);
        final XSAttributeDecl attribute = this.traverseNamedAttr(attrDecl, attrValues, schemaDoc, grammar, true, null);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attribute;
    }
    
    XSAttributeDecl traverseNamedAttr(final Element attrDecl, final Object[] attrValues, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final boolean isGlobal, final XSComplexTypeDecl enclosingCT) {
        final String defaultAtt = (String)attrValues[XSAttributeChecker.ATTIDX_DEFAULT];
        final String fixedAtt = (String)attrValues[XSAttributeChecker.ATTIDX_FIXED];
        final XInt formAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FORM];
        String nameAtt = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        final QName typeAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_TYPE];
        XSAttributeDecl attribute = null;
        if (this.fSchemaHandler.fDeclPool != null) {
            attribute = this.fSchemaHandler.fDeclPool.getAttributeDecl();
        }
        else {
            attribute = new XSAttributeDecl();
        }
        if (nameAtt != null) {
            nameAtt = this.fSymbolTable.addSymbol(nameAtt);
        }
        String tnsAtt = null;
        XSComplexTypeDecl enclCT = null;
        short scope = 0;
        if (isGlobal) {
            tnsAtt = schemaDoc.fTargetNamespace;
            scope = 1;
        }
        else {
            if (enclosingCT != null) {
                enclCT = enclosingCT;
                scope = 2;
            }
            if (formAtt != null) {
                if (formAtt.intValue() == 1) {
                    tnsAtt = schemaDoc.fTargetNamespace;
                }
            }
            else if (schemaDoc.fAreLocalAttributesQualified) {
                tnsAtt = schemaDoc.fTargetNamespace;
            }
        }
        ValidatedInfo attDefault = null;
        short constraintType = 0;
        if (isGlobal) {
            if (fixedAtt != null) {
                attDefault = new ValidatedInfo();
                attDefault.normalizedValue = fixedAtt;
                constraintType = 2;
            }
            else if (defaultAtt != null) {
                attDefault = new ValidatedInfo();
                attDefault.normalizedValue = defaultAtt;
                constraintType = 1;
            }
        }
        Element child = DOMUtil.getFirstChildElement(attrDecl);
        XSAnnotationImpl annotation = null;
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(attrDecl);
            if (text != null) {
                annotation = this.traverseSyntheticAnnotation(attrDecl, text, attrValues, false, schemaDoc);
            }
        }
        XSSimpleType attrType = null;
        boolean haveAnonType = false;
        if (child != null) {
            final String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                attrType = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(child, schemaDoc, grammar);
                haveAnonType = true;
                child = DOMUtil.getNextSiblingElement(child);
            }
        }
        if (attrType == null && typeAtt != null) {
            final XSTypeDefinition type = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, typeAtt, attrDecl);
            if (type != null && type.getTypeCategory() == 16) {
                attrType = (XSSimpleType)type;
            }
            else {
                this.reportSchemaError("src-resolve", new Object[] { typeAtt.rawname, "simpleType definition" }, attrDecl);
                if (type == null) {
                    attribute.fUnresolvedTypeName = typeAtt;
                }
            }
        }
        if (attrType == null) {
            attrType = SchemaGrammar.fAnySimpleType;
        }
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
        }
        else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        attribute.setValues(nameAtt, tnsAtt, attrType, constraintType, scope, attDefault, enclCT, annotations);
        if (nameAtt == null) {
            if (isGlobal) {
                this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ATTRIBUTE, SchemaSymbols.ATT_NAME }, attrDecl);
            }
            else {
                this.reportSchemaError("src-attribute.3.1", null, attrDecl);
            }
            nameAtt = "(no name)";
        }
        if (child != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { nameAtt, "(annotation?, (simpleType?))", DOMUtil.getLocalName(child) }, child);
        }
        if (defaultAtt != null && fixedAtt != null) {
            this.reportSchemaError("src-attribute.1", new Object[] { nameAtt }, attrDecl);
        }
        if (haveAnonType && typeAtt != null) {
            this.reportSchemaError("src-attribute.4", new Object[] { nameAtt }, attrDecl);
        }
        this.checkNotationType(nameAtt, attrType, attrDecl);
        if (attDefault != null) {
            this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
            try {
                this.checkDefaultValid(attribute);
            }
            catch (final InvalidDatatypeValueException ide) {
                this.reportSchemaError(ide.getKey(), ide.getArgs(), attrDecl);
                this.reportSchemaError("a-props-correct.2", new Object[] { nameAtt, attDefault.normalizedValue }, attrDecl);
                attDefault = null;
                constraintType = 0;
                attribute.setValues(nameAtt, tnsAtt, attrType, constraintType, scope, attDefault, enclCT, annotations);
            }
        }
        if (attDefault != null && attrType.isIDType()) {
            this.reportSchemaError("a-props-correct.3", new Object[] { nameAtt }, attrDecl);
            attDefault = null;
            constraintType = 0;
            attribute.setValues(nameAtt, tnsAtt, attrType, constraintType, scope, attDefault, enclCT, annotations);
        }
        if (nameAtt != null && nameAtt.equals(XMLSymbols.PREFIX_XMLNS)) {
            this.reportSchemaError("no-xmlns", null, attrDecl);
            return null;
        }
        if (tnsAtt != null && tnsAtt.equals(SchemaSymbols.URI_XSI)) {
            this.reportSchemaError("no-xsi", new Object[] { SchemaSymbols.URI_XSI }, attrDecl);
            return null;
        }
        if (nameAtt.equals("(no name)")) {
            return null;
        }
        if (isGlobal) {
            if (grammar.getGlobalAttributeDecl(nameAtt) == null) {
                grammar.addGlobalAttributeDecl(attribute);
            }
            final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            final XSAttributeDecl attribute2 = grammar.getGlobalAttributeDecl(nameAtt, loc);
            if (attribute2 == null) {
                grammar.addGlobalAttributeDecl(attribute, loc);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (attribute2 != null) {
                    attribute = attribute2;
                }
                this.fSchemaHandler.addGlobalAttributeDecl(attribute);
            }
        }
        return attribute;
    }
    
    void checkDefaultValid(final XSAttributeDecl attribute) throws InvalidDatatypeValueException {
        ((XSSimpleType)attribute.getTypeDefinition()).validate(attribute.getValInfo().normalizedValue, this.fValidationState, attribute.getValInfo());
        ((XSSimpleType)attribute.getTypeDefinition()).validate(attribute.getValInfo().stringValue(), this.fValidationState, attribute.getValInfo());
    }
    
    void checkDefaultValid(final XSAttributeUseImpl attrUse) throws InvalidDatatypeValueException {
        ((XSSimpleType)attrUse.fAttrDecl.getTypeDefinition()).validate(attrUse.fDefault.normalizedValue, this.fValidationState, attrUse.fDefault);
        ((XSSimpleType)attrUse.fAttrDecl.getTypeDefinition()).validate(attrUse.fDefault.stringValue(), this.fValidationState, attrUse.fDefault);
    }
}
