package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import java.util.ArrayList;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDSimpleTypeTraverser extends XSDAbstractTraverser
{
    private boolean fIsBuiltIn;
    
    XSDSimpleTypeTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
        this.fIsBuiltIn = false;
    }
    
    XSSimpleType traverseGlobal(final Element elmNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, true, schemaDoc);
        final String nameAtt = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        if (nameAtt == null) {
            attrValues[XSAttributeChecker.ATTIDX_NAME] = "(no name)";
        }
        XSSimpleType type = this.traverseSimpleTypeDecl(elmNode, attrValues, schemaDoc, grammar);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        if (nameAtt == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, SchemaSymbols.ATT_NAME }, elmNode);
            type = null;
        }
        if (type != null) {
            if (grammar.getGlobalTypeDecl(type.getName()) == null) {
                grammar.addGlobalSimpleTypeDecl(type);
            }
            final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            final XSTypeDefinition type2 = grammar.getGlobalTypeDecl(type.getName(), loc);
            if (type2 == null) {
                grammar.addGlobalSimpleTypeDecl(type, loc);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (type2 != null && type2 instanceof XSSimpleType) {
                    type = (XSSimpleType)type2;
                }
                this.fSchemaHandler.addGlobalTypeDecl(type);
            }
        }
        return type;
    }
    
    XSSimpleType traverseLocal(final Element elmNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        final String name = this.genAnonTypeName(elmNode);
        final XSSimpleType type = this.getSimpleType(name, elmNode, attrValues, schemaDoc, grammar);
        if (type instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)type).setAnonymous(true);
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return type;
    }
    
    private XSSimpleType traverseSimpleTypeDecl(final Element simpleTypeDecl, final Object[] attrValues, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final String name = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        return this.getSimpleType(name, simpleTypeDecl, attrValues, schemaDoc, grammar);
    }
    
    private String genAnonTypeName(final Element simpleTypeDecl) {
        final StringBuffer typeName = new StringBuffer("#AnonType_");
        for (Element node = DOMUtil.getParent(simpleTypeDecl); node != null && node != DOMUtil.getRoot(DOMUtil.getDocument(node)); node = DOMUtil.getParent(node)) {
            typeName.append(node.getAttribute(SchemaSymbols.ATT_NAME));
        }
        return typeName.toString();
    }
    
    private XSSimpleType getSimpleType(final String name, final Element simpleTypeDecl, final Object[] attrValues, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final XInt finalAttr = (XInt)attrValues[XSAttributeChecker.ATTIDX_FINAL];
        final int finalProperty = (finalAttr == null) ? schemaDoc.fFinalDefault : finalAttr.intValue();
        Element child = DOMUtil.getFirstChildElement(simpleTypeDecl);
        XSAnnotationImpl[] annotations = null;
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            final XSAnnotationImpl annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            if (annotation != null) {
                annotations = new XSAnnotationImpl[] { annotation };
            }
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(simpleTypeDecl);
            if (text != null) {
                final XSAnnotationImpl annotation2 = this.traverseSyntheticAnnotation(simpleTypeDecl, text, attrValues, false, schemaDoc);
                annotations = new XSAnnotationImpl[] { annotation2 };
            }
        }
        if (child == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))" }, simpleTypeDecl);
            return this.errorType(name, schemaDoc.fTargetNamespace, (short)2);
        }
        final String varietyProperty = DOMUtil.getLocalName(child);
        short refType = 2;
        boolean restriction = false;
        boolean list = false;
        boolean union = false;
        if (varietyProperty.equals(SchemaSymbols.ELT_RESTRICTION)) {
            refType = 2;
            restriction = true;
        }
        else if (varietyProperty.equals(SchemaSymbols.ELT_LIST)) {
            refType = 16;
            list = true;
        }
        else {
            if (!varietyProperty.equals(SchemaSymbols.ELT_UNION)) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))", varietyProperty }, simpleTypeDecl);
                return this.errorType(name, schemaDoc.fTargetNamespace, (short)2);
            }
            refType = 8;
            union = true;
        }
        final Element nextChild = DOMUtil.getNextSiblingElement(child);
        if (nextChild != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))", DOMUtil.getLocalName(nextChild) }, nextChild);
        }
        final Object[] contentAttrs = this.fAttrChecker.checkAttributes(child, false, schemaDoc);
        final QName baseTypeName = (QName)contentAttrs[restriction ? XSAttributeChecker.ATTIDX_BASE : XSAttributeChecker.ATTIDX_ITEMTYPE];
        final Vector memberTypes = (Vector)contentAttrs[XSAttributeChecker.ATTIDX_MEMBERTYPES];
        Element content = DOMUtil.getFirstChildElement(child);
        if (content != null && DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
            final XSAnnotationImpl annotation3 = this.traverseAnnotationDecl(content, contentAttrs, false, schemaDoc);
            if (annotation3 != null) {
                if (annotations == null) {
                    annotations = new XSAnnotationImpl[] { annotation3 };
                }
                else {
                    final XSAnnotationImpl[] tempArray = annotations = new XSAnnotationImpl[] { annotations[0], null };
                    annotations[1] = annotation3;
                }
            }
            content = DOMUtil.getNextSiblingElement(content);
        }
        else {
            final String text2 = DOMUtil.getSyntheticAnnotation(child);
            if (text2 != null) {
                final XSAnnotationImpl annotation4 = this.traverseSyntheticAnnotation(child, text2, contentAttrs, false, schemaDoc);
                if (annotations == null) {
                    annotations = new XSAnnotationImpl[] { annotation4 };
                }
                else {
                    final XSAnnotationImpl[] tempArray2 = annotations = new XSAnnotationImpl[] { annotations[0], null };
                    annotations[1] = annotation4;
                }
            }
        }
        XSSimpleType baseValidator = null;
        if ((restriction || list) && baseTypeName != null) {
            baseValidator = this.findDTValidator(child, name, baseTypeName, refType, schemaDoc);
            if (baseValidator == null && this.fIsBuiltIn) {
                this.fIsBuiltIn = false;
                return null;
            }
        }
        ArrayList dTValidators = null;
        XSSimpleType dv = null;
        if (union && memberTypes != null && memberTypes.size() > 0) {
            final int size = memberTypes.size();
            dTValidators = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                dv = this.findDTValidator(child, name, memberTypes.elementAt(i), (short)8, schemaDoc);
                if (dv != null) {
                    if (dv.getVariety() == 3) {
                        final XSObjectList dvs = dv.getMemberTypes();
                        for (int j = 0; j < dvs.getLength(); ++j) {
                            dTValidators.add(dvs.item(j));
                        }
                    }
                    else {
                        dTValidators.add(dv);
                    }
                }
            }
        }
        if (content != null && DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            if (restriction || list) {
                if (baseTypeName != null) {
                    this.reportSchemaError(list ? "src-simple-type.3.a" : "src-simple-type.2.a", null, content);
                }
                if (baseValidator == null) {
                    baseValidator = this.traverseLocal(content, schemaDoc, grammar);
                }
                content = DOMUtil.getNextSiblingElement(content);
            }
            else if (union) {
                if (dTValidators == null) {
                    dTValidators = new ArrayList(2);
                }
                do {
                    dv = this.traverseLocal(content, schemaDoc, grammar);
                    if (dv != null) {
                        if (dv.getVariety() == 3) {
                            final XSObjectList dvs = dv.getMemberTypes();
                            for (int k = 0; k < dvs.getLength(); ++k) {
                                dTValidators.add(dvs.item(k));
                            }
                        }
                        else {
                            dTValidators.add(dv);
                        }
                    }
                    content = DOMUtil.getNextSiblingElement(content);
                    if (content != null) {
                        continue;
                    }
                    break;
                } while (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_SIMPLETYPE));
            }
        }
        else if ((restriction || list) && baseTypeName == null) {
            this.reportSchemaError(list ? "src-simple-type.3.b" : "src-simple-type.2.b", null, child);
        }
        else if (union && (memberTypes == null || memberTypes.size() == 0)) {
            this.reportSchemaError("src-union-memberTypes-or-simpleTypes", null, child);
        }
        if ((restriction || list) && baseValidator == null) {
            this.fAttrChecker.returnAttrArray(contentAttrs, schemaDoc);
            return this.errorType(name, schemaDoc.fTargetNamespace, (short)(restriction ? 2 : 16));
        }
        if (union && (dTValidators == null || dTValidators.size() == 0)) {
            this.fAttrChecker.returnAttrArray(contentAttrs, schemaDoc);
            return this.errorType(name, schemaDoc.fTargetNamespace, (short)8);
        }
        if (list && this.isListDatatype(baseValidator)) {
            this.reportSchemaError("cos-st-restricts.2.1", new Object[] { name, baseValidator.getName() }, child);
            this.fAttrChecker.returnAttrArray(contentAttrs, schemaDoc);
            return this.errorType(name, schemaDoc.fTargetNamespace, (short)16);
        }
        XSSimpleType newDecl = null;
        if (restriction) {
            newDecl = this.fSchemaHandler.fDVFactory.createTypeRestriction(name, schemaDoc.fTargetNamespace, (short)finalProperty, baseValidator, (annotations == null) ? null : new XSObjectListImpl(annotations, annotations.length));
        }
        else if (list) {
            newDecl = this.fSchemaHandler.fDVFactory.createTypeList(name, schemaDoc.fTargetNamespace, (short)finalProperty, baseValidator, (annotations == null) ? null : new XSObjectListImpl(annotations, annotations.length));
        }
        else if (union) {
            final XSSimpleType[] memberDecls = dTValidators.toArray(new XSSimpleType[dTValidators.size()]);
            newDecl = this.fSchemaHandler.fDVFactory.createTypeUnion(name, schemaDoc.fTargetNamespace, (short)finalProperty, memberDecls, (annotations == null) ? null : new XSObjectListImpl(annotations, annotations.length));
        }
        if (restriction && content != null) {
            final FacetInfo fi = this.traverseFacets(content, baseValidator, schemaDoc);
            content = fi.nodeAfterFacets;
            try {
                this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
                newDecl.applyFacets(fi.facetdata, fi.fPresentFacets, fi.fFixedFacets, this.fValidationState);
            }
            catch (final InvalidDatatypeFacetException ex) {
                this.reportSchemaError(ex.getKey(), ex.getArgs(), child);
                newDecl = this.fSchemaHandler.fDVFactory.createTypeRestriction(name, schemaDoc.fTargetNamespace, (short)finalProperty, baseValidator, (annotations == null) ? null : new XSObjectListImpl(annotations, annotations.length));
            }
        }
        if (content != null) {
            if (restriction) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_RESTRICTION, "(annotation?, (simpleType?, (minExclusive | minInclusive | maxExclusive | maxInclusive | totalDigits | fractionDigits | length | minLength | maxLength | enumeration | whiteSpace | pattern)*))", DOMUtil.getLocalName(content) }, content);
            }
            else if (list) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_LIST, "(annotation?, (simpleType?))", DOMUtil.getLocalName(content) }, content);
            }
            else if (union) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_UNION, "(annotation?, (simpleType*))", DOMUtil.getLocalName(content) }, content);
            }
        }
        this.fAttrChecker.returnAttrArray(contentAttrs, schemaDoc);
        return newDecl;
    }
    
    private XSSimpleType findDTValidator(final Element elm, final String refName, final QName baseTypeStr, final short baseRefContext, final XSDocumentInfo schemaDoc) {
        if (baseTypeStr == null) {
            return null;
        }
        final XSTypeDefinition baseType = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, baseTypeStr, elm);
        if (baseType == null) {
            return null;
        }
        if (baseType.getTypeCategory() != 16) {
            this.reportSchemaError("cos-st-restricts.1.1", new Object[] { baseTypeStr.rawname, refName }, elm);
            return null;
        }
        if (baseType == SchemaGrammar.fAnySimpleType && baseRefContext == 2) {
            if (this.checkBuiltIn(refName, schemaDoc.fTargetNamespace)) {
                return null;
            }
            this.reportSchemaError("cos-st-restricts.1.1", new Object[] { baseTypeStr.rawname, refName }, elm);
            return null;
        }
        else {
            if ((baseType.getFinal() & baseRefContext) != 0x0) {
                if (baseRefContext == 2) {
                    this.reportSchemaError("st-props-correct.3", new Object[] { refName, baseTypeStr.rawname }, elm);
                }
                else if (baseRefContext == 16) {
                    this.reportSchemaError("cos-st-restricts.2.3.1.1", new Object[] { baseTypeStr.rawname, refName }, elm);
                }
                else if (baseRefContext == 8) {
                    this.reportSchemaError("cos-st-restricts.3.3.1.1", new Object[] { baseTypeStr.rawname, refName }, elm);
                }
                return null;
            }
            return (XSSimpleType)baseType;
        }
    }
    
    private final boolean checkBuiltIn(final String name, final String namespace) {
        if (namespace != SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            return false;
        }
        if (SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(name) != null) {
            this.fIsBuiltIn = true;
        }
        return this.fIsBuiltIn;
    }
    
    private boolean isListDatatype(final XSSimpleType validator) {
        if (validator.getVariety() == 2) {
            return true;
        }
        if (validator.getVariety() == 3) {
            final XSObjectList temp = validator.getMemberTypes();
            for (int i = 0; i < temp.getLength(); ++i) {
                if (((XSSimpleType)temp.item(i)).getVariety() == 2) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private XSSimpleType errorType(final String name, final String namespace, final short refType) {
        final XSSimpleType stringType = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getTypeDefinition("string");
        switch (refType) {
            case 2: {
                return this.fSchemaHandler.fDVFactory.createTypeRestriction(name, namespace, (short)0, stringType, null);
            }
            case 16: {
                return this.fSchemaHandler.fDVFactory.createTypeList(name, namespace, (short)0, stringType, null);
            }
            case 8: {
                return this.fSchemaHandler.fDVFactory.createTypeUnion(name, namespace, (short)0, new XSSimpleType[] { stringType }, null);
            }
            default: {
                return null;
            }
        }
    }
}
