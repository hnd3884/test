package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDSimpleTypeTraverser extends XSDAbstractTraverser
{
    private boolean fIsBuiltIn;
    
    XSDSimpleTypeTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
        this.fIsBuiltIn = false;
    }
    
    XSSimpleType traverseGlobal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        if (s == null) {
            checkAttributes[XSAttributeChecker.ATTIDX_NAME] = "(no name)";
        }
        XSSimpleType traverseSimpleTypeDecl = this.traverseSimpleTypeDecl(element, checkAttributes, xsDocumentInfo, schemaGrammar);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        if (s == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, SchemaSymbols.ATT_NAME }, element);
            traverseSimpleTypeDecl = null;
        }
        if (traverseSimpleTypeDecl != null) {
            if (DOMUtil.getLocalName(DOMUtil.getParent(element)).equals(SchemaSymbols.ELT_REDEFINE)) {
                if (this.fSchemaHandler.fSchemaVersion == 4) {
                    final XSTypeDefinition baseType = traverseSimpleTypeDecl.getBaseType();
                    if (baseType instanceof XSSimpleTypeDecl) {
                        ((XSSimpleTypeDecl)baseType).setContext(traverseSimpleTypeDecl);
                    }
                }
                schemaGrammar.addGlobalSimpleTypeDecl(traverseSimpleTypeDecl);
            }
            if (schemaGrammar.getGlobalTypeDecl(traverseSimpleTypeDecl.getName()) == null) {
                schemaGrammar.addGlobalSimpleTypeDecl(traverseSimpleTypeDecl);
            }
            final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
            final XSTypeDefinition globalTypeDecl = schemaGrammar.getGlobalTypeDecl(traverseSimpleTypeDecl.getName(), schemaDocument2SystemId);
            if (globalTypeDecl == null) {
                schemaGrammar.addGlobalSimpleTypeDecl(traverseSimpleTypeDecl, schemaDocument2SystemId);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (globalTypeDecl != null && globalTypeDecl instanceof XSSimpleType) {
                    traverseSimpleTypeDecl = (XSSimpleType)globalTypeDecl;
                }
                this.fSchemaHandler.addGlobalTypeDecl(traverseSimpleTypeDecl);
            }
        }
        return traverseSimpleTypeDecl;
    }
    
    XSSimpleType traverseLocal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final XSObject context) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final XSSimpleType simpleType = this.getSimpleType(this.genAnonTypeName(element), element, checkAttributes, xsDocumentInfo, schemaGrammar);
        if (simpleType instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)simpleType).setAnonymous(true);
            if (context != null && this.fSchemaHandler.fSchemaVersion == 4) {
                ((XSSimpleTypeDecl)simpleType).setContext(context);
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return simpleType;
    }
    
    private XSSimpleType traverseSimpleTypeDecl(final Element element, final Object[] array, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        return this.getSimpleType((String)array[XSAttributeChecker.ATTIDX_NAME], element, array, xsDocumentInfo, schemaGrammar);
    }
    
    private String genAnonTypeName(final Element element) {
        final StringBuffer sb = new StringBuffer("#AnonType_");
        for (Element element2 = DOMUtil.getParent(element); element2 != null && element2 != DOMUtil.getRoot(DOMUtil.getDocument(element2)); element2 = DOMUtil.getParent(element2)) {
            sb.append(element2.getAttribute(SchemaSymbols.ATT_NAME));
        }
        return sb.toString();
    }
    
    private XSSimpleType getSimpleType(final String s, final Element element, final Object[] array, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final XInt xInt = (XInt)array[XSAttributeChecker.ATTIDX_FINAL];
        final int n = (xInt == null) ? xsDocumentInfo.fFinalDefault : xInt.intValue();
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSAnnotationImpl[] array2 = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            final XSAnnotationImpl traverseAnnotationDecl = this.traverseAnnotationDecl(element2, array, false, xsDocumentInfo);
            if (traverseAnnotationDecl != null) {
                array2 = new XSAnnotationImpl[] { traverseAnnotationDecl };
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                array2 = new XSAnnotationImpl[] { this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo) };
            }
        }
        if (element2 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))" }, element);
            return this.errorType(s, xsDocumentInfo.fTargetNamespace, (short)2);
        }
        final String localName = DOMUtil.getLocalName(element2);
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        short n2;
        if (localName.equals(SchemaSymbols.ELT_RESTRICTION)) {
            n2 = 2;
            b = true;
        }
        else if (localName.equals(SchemaSymbols.ELT_LIST)) {
            n2 = 16;
            b2 = true;
        }
        else {
            if (!localName.equals(SchemaSymbols.ELT_UNION)) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))", localName }, element);
                return this.errorType(s, xsDocumentInfo.fTargetNamespace, (short)2);
            }
            n2 = 8;
            b3 = true;
        }
        final Element nextSiblingElement = DOMUtil.getNextSiblingElement(element2);
        if (nextSiblingElement != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))", DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
        }
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element2, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes[b ? XSAttributeChecker.ATTIDX_BASE : XSAttributeChecker.ATTIDX_ITEMTYPE];
        final Vector vector = (Vector)checkAttributes[XSAttributeChecker.ATTIDX_MEMBERTYPES];
        Element element3 = DOMUtil.getFirstChildElement(element2);
        if (element3 != null && DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
            final XSAnnotationImpl traverseAnnotationDecl2 = this.traverseAnnotationDecl(element3, checkAttributes, false, xsDocumentInfo);
            if (traverseAnnotationDecl2 != null) {
                if (array2 == null) {
                    array2 = new XSAnnotationImpl[] { traverseAnnotationDecl2 };
                }
                else {
                    array2 = new XSAnnotationImpl[] { array2[0], traverseAnnotationDecl2 };
                }
            }
            element3 = DOMUtil.getNextSiblingElement(element3);
        }
        else {
            final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element2);
            if (syntheticAnnotation2 != null) {
                final XSAnnotationImpl traverseSyntheticAnnotation = this.traverseSyntheticAnnotation(element2, syntheticAnnotation2, checkAttributes, false, xsDocumentInfo);
                if (array2 == null) {
                    array2 = new XSAnnotationImpl[] { traverseSyntheticAnnotation };
                }
                else {
                    array2 = new XSAnnotationImpl[] { array2[0], traverseSyntheticAnnotation };
                }
            }
        }
        XSSimpleType xsSimpleType = null;
        if ((b || b2) && qName != null) {
            xsSimpleType = this.findDTValidator(element2, s, qName, n2, xsDocumentInfo);
            if (xsSimpleType == null && this.fIsBuiltIn) {
                this.fIsBuiltIn = false;
                return null;
            }
        }
        ArrayList list = null;
        if (b3 && vector != null && vector.size() > 0) {
            final int size = vector.size();
            list = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                final XSSimpleType dtValidator = this.findDTValidator(element2, s, vector.elementAt(i), (short)8, xsDocumentInfo);
                if (dtValidator != null) {
                    if (dtValidator.getVariety() == 3 && this.fSchemaHandler.fSchemaVersion < 4) {
                        final XSObjectList memberTypes = dtValidator.getMemberTypes();
                        for (int j = 0; j < memberTypes.getLength(); ++j) {
                            list.add(memberTypes.item(j));
                        }
                    }
                    else {
                        list.add(dtValidator);
                    }
                }
            }
        }
        final ArrayList list2 = new ArrayList<XSSimpleType>(2);
        if (element3 != null && DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            if (b || b2) {
                if (qName != null) {
                    this.reportSchemaError(b2 ? "src-simple-type.3.a" : "src-simple-type.2.a", null, element3);
                }
                if (xsSimpleType == null) {
                    xsSimpleType = this.traverseLocal(element3, xsDocumentInfo, schemaGrammar, null);
                    if (this.fSchemaHandler.fSchemaVersion == 4 && xsSimpleType instanceof XSSimpleTypeDecl) {
                        list2.add(xsSimpleType);
                    }
                }
                element3 = DOMUtil.getNextSiblingElement(element3);
            }
            else if (b3) {
                if (list == null) {
                    list = new ArrayList(2);
                }
                do {
                    final XSSimpleType traverseLocal = this.traverseLocal(element3, xsDocumentInfo, schemaGrammar, null);
                    if (traverseLocal != null) {
                        if (this.fSchemaHandler.fSchemaVersion == 4) {
                            list.add(traverseLocal);
                            if (traverseLocal instanceof XSSimpleTypeDecl) {
                                list2.add(traverseLocal);
                            }
                        }
                        else if (traverseLocal.getVariety() == 3) {
                            final XSObjectList memberTypes2 = traverseLocal.getMemberTypes();
                            for (int k = 0; k < memberTypes2.getLength(); ++k) {
                                list.add(memberTypes2.item(k));
                            }
                        }
                        else {
                            list.add(traverseLocal);
                        }
                    }
                    element3 = DOMUtil.getNextSiblingElement(element3);
                    if (element3 != null) {
                        continue;
                    }
                    break;
                } while (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_SIMPLETYPE));
            }
        }
        else if ((b || b2) && qName == null) {
            this.reportSchemaError(b2 ? "src-simple-type.3.b" : "src-simple-type.2.b", null, element2);
        }
        else if (b3 && (vector == null || vector.size() == 0)) {
            this.reportSchemaError("src-union-memberTypes-or-simpleTypes", null, element2);
        }
        if ((b || b2) && xsSimpleType == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return this.errorType(s, xsDocumentInfo.fTargetNamespace, (short)(b ? 2 : 16));
        }
        if (b3 && (list == null || list.size() == 0)) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return this.errorType(s, xsDocumentInfo.fTargetNamespace, (short)8);
        }
        if (b2 && this.isListDatatype(xsSimpleType)) {
            this.reportSchemaError("cos-st-restricts.2.1", new Object[] { s, xsSimpleType.getName() }, element2);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return this.errorType(s, xsDocumentInfo.fTargetNamespace, (short)16);
        }
        XSSimpleType context = null;
        if (b) {
            context = this.fSchemaHandler.fDVFactory.createTypeRestriction(s, xsDocumentInfo.fTargetNamespace, (short)n, xsSimpleType, (array2 == null) ? null : new XSObjectListImpl(array2, array2.length));
        }
        else if (b2) {
            context = this.fSchemaHandler.fDVFactory.createTypeList(s, xsDocumentInfo.fTargetNamespace, (short)n, xsSimpleType, (array2 == null) ? null : new XSObjectListImpl(array2, array2.length));
        }
        else if (b3) {
            context = this.fSchemaHandler.fDVFactory.createTypeUnion(s, xsDocumentInfo.fTargetNamespace, (short)n, (XSSimpleType[])list.toArray(new XSSimpleType[list.size()]), (array2 == null) ? null : new XSObjectListImpl(array2, array2.length));
        }
        if (b && element3 != null) {
            final FacetInfo traverseFacets = this.traverseFacets(element3, context, xsSimpleType, xsDocumentInfo);
            element3 = traverseFacets.nodeAfterFacets;
            try {
                this.fValidationState.setNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
                this.fValidationState.setDatatypeXMLVersion(xsDocumentInfo.fDatatypeXMLVersion);
                context.applyFacets(traverseFacets.facetdata, traverseFacets.fPresentFacets, traverseFacets.fFixedFacets, this.fValidationState);
            }
            catch (final InvalidDatatypeFacetException ex) {
                this.reportSchemaError(ex.getKey(), ex.getArgs(), element2);
                context = this.fSchemaHandler.fDVFactory.createTypeRestriction(s, xsDocumentInfo.fTargetNamespace, (short)n, xsSimpleType, (array2 == null) ? null : new XSObjectListImpl(array2, array2.length));
            }
        }
        for (int size2 = list2.size(), l = 0; l < size2; ++l) {
            ((XSSimpleTypeDecl)list2.get(l)).setContext(context);
        }
        if (element3 != null) {
            if (b) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_RESTRICTION, "(annotation?, (simpleType?, (minExclusive | minInclusive | maxExclusive | maxInclusive | totalDigits | fractionDigits | length | minLength | maxLength | enumeration | whiteSpace | pattern)*))", DOMUtil.getLocalName(element3) }, element3);
            }
            else if (b2) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_LIST, "(annotation?, (simpleType?))", DOMUtil.getLocalName(element3) }, element3);
            }
            else if (b3) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_UNION, "(annotation?, (simpleType*))", DOMUtil.getLocalName(element3) }, element3);
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return context;
    }
    
    private XSSimpleType findDTValidator(final Element element, final String s, final QName qName, final short n, final XSDocumentInfo xsDocumentInfo) {
        if (qName == null) {
            return null;
        }
        final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 7, qName, element);
        if (xsTypeDefinition == null) {
            return null;
        }
        if (xsTypeDefinition.getTypeCategory() != 16) {
            this.reportSchemaError("cos-st-restricts.1.1", new Object[] { qName.rawname, s }, element);
            return null;
        }
        if (xsTypeDefinition == SchemaGrammar.fAnySimpleType || xsTypeDefinition == SchemaGrammar.fAnyAtomicType) {
            if (n == 2) {
                if (this.checkBuiltIn(s, xsDocumentInfo.fTargetNamespace)) {
                    return null;
                }
                this.reportSchemaError("cos-st-restricts.1.1", new Object[] { qName.rawname, s }, element);
                return null;
            }
            else if (this.fSchemaHandler.fSchemaVersion == 4) {
                this.reportSchemaError("st-props-correct.1", new Object[] { s, (n == 16) ? "xs:list" : "xs:union" }, element);
                return null;
            }
        }
        if ((xsTypeDefinition.getFinal() & n) != 0x0) {
            if (n == 2) {
                this.reportSchemaError("st-props-correct.3", new Object[] { s, qName.rawname }, element);
            }
            else if (n == 16) {
                this.reportSchemaError("cos-st-restricts.2.3.1.1", new Object[] { qName.rawname, s }, element);
            }
            else if (n == 8) {
                this.reportSchemaError("cos-st-restricts.3.3.1.1", new Object[] { qName.rawname, s }, element);
            }
            return null;
        }
        return (XSSimpleType)xsTypeDefinition;
    }
    
    private final boolean checkBuiltIn(final String s, final String s2) {
        if (s2 != SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            return false;
        }
        if (this.fSchemaHandler.fSchemaVersion == 2 && (s.equals("duration") || s.equals("yearMonthDuration") || s.equals("dayTimeDuration"))) {
            return false;
        }
        if (SchemaGrammar.getS4SGrammar(this.fSchemaHandler.fSchemaVersion).getGlobalTypeDecl(s) != null) {
            this.fIsBuiltIn = true;
        }
        return this.fIsBuiltIn;
    }
    
    private boolean isListDatatype(final XSSimpleType xsSimpleType) {
        if (xsSimpleType.getVariety() == 2) {
            return true;
        }
        if (xsSimpleType.getVariety() == 3) {
            final XSObjectList memberTypes = xsSimpleType.getMemberTypes();
            for (int i = 0; i < memberTypes.getLength(); ++i) {
                if (((XSSimpleType)memberTypes.item(i)).getVariety() == 2) {
                    return true;
                }
                if (this.fSchemaHandler.fSchemaVersion == 4 && ((XSSimpleType)memberTypes.item(i)).getVariety() == 3 && this.isListDatatype((XSSimpleType)memberTypes.item(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private XSSimpleType errorType(final String s, final String s2, final short n) {
        final XSSimpleType xsSimpleType = (XSSimpleType)SchemaGrammar.getS4SGrammar(this.fSchemaHandler.fSchemaVersion).getTypeDefinition("string");
        switch (n) {
            case 2: {
                return this.fSchemaHandler.fDVFactory.createTypeRestriction(s, s2, (short)0, xsSimpleType, null);
            }
            case 16: {
                return this.fSchemaHandler.fDVFactory.createTypeList(s, s2, (short)0, xsSimpleType, null);
            }
            case 8: {
                return this.fSchemaHandler.fDVFactory.createTypeUnion(s, s2, (short)0, new XSSimpleType[] { xsSimpleType }, null);
            }
            default: {
                return null;
            }
        }
    }
}
