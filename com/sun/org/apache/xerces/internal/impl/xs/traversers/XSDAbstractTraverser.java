package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import org.w3c.dom.Element;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

abstract class XSDAbstractTraverser
{
    protected static final String NO_NAME = "(no name)";
    protected static final int NOT_ALL_CONTEXT = 0;
    protected static final int PROCESSING_ALL_EL = 1;
    protected static final int GROUP_REF_WITH_ALL = 2;
    protected static final int CHILD_OF_GROUP = 4;
    protected static final int PROCESSING_ALL_GP = 8;
    protected XSDHandler fSchemaHandler;
    protected SymbolTable fSymbolTable;
    protected XSAttributeChecker fAttrChecker;
    protected boolean fValidateAnnotations;
    ValidationState fValidationState;
    private static final XSSimpleType fQNameDV;
    private StringBuffer fPattern;
    private final XSFacets xsFacets;
    
    XSDAbstractTraverser(final XSDHandler handler, final XSAttributeChecker attrChecker) {
        this.fSchemaHandler = null;
        this.fSymbolTable = null;
        this.fAttrChecker = null;
        this.fValidateAnnotations = false;
        this.fValidationState = new ValidationState();
        this.fPattern = new StringBuffer();
        this.xsFacets = new XSFacets();
        this.fSchemaHandler = handler;
        this.fAttrChecker = attrChecker;
    }
    
    void reset(final SymbolTable symbolTable, final boolean validateAnnotations, final Locale locale) {
        this.fSymbolTable = symbolTable;
        this.fValidateAnnotations = validateAnnotations;
        this.fValidationState.setExtraChecking(false);
        this.fValidationState.setSymbolTable(symbolTable);
        this.fValidationState.setLocale(locale);
    }
    
    XSAnnotationImpl traverseAnnotationDecl(final Element annotationDecl, final Object[] parentAttrs, final boolean isGlobal, final XSDocumentInfo schemaDoc) {
        Object[] attrValues = this.fAttrChecker.checkAttributes(annotationDecl, isGlobal, schemaDoc);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        final String contents = DOMUtil.getAnnotation(annotationDecl);
        Element child = DOMUtil.getFirstChildElement(annotationDecl);
        if (child != null) {
            do {
                final String name = DOMUtil.getLocalName(child);
                if (!name.equals(SchemaSymbols.ELT_APPINFO) && !name.equals(SchemaSymbols.ELT_DOCUMENTATION)) {
                    this.reportSchemaError("src-annotation", new Object[] { name }, child);
                }
                else {
                    attrValues = this.fAttrChecker.checkAttributes(child, true, schemaDoc);
                    this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                }
                child = DOMUtil.getNextSiblingElement(child);
            } while (child != null);
        }
        if (contents == null) {
            return null;
        }
        final SchemaGrammar grammar = this.fSchemaHandler.getGrammar(schemaDoc.fTargetNamespace);
        final Vector annotationLocalAttrs = (Vector)parentAttrs[XSAttributeChecker.ATTIDX_NONSCHEMA];
        if (annotationLocalAttrs == null || annotationLocalAttrs.isEmpty()) {
            if (this.fValidateAnnotations) {
                schemaDoc.addAnnotation(new XSAnnotationInfo(contents, annotationDecl));
            }
            return new XSAnnotationImpl(contents, grammar);
        }
        final StringBuffer localStrBuffer = new StringBuffer(64);
        localStrBuffer.append(" ");
        int i = 0;
        while (i < annotationLocalAttrs.size()) {
            final String rawname = annotationLocalAttrs.elementAt(i++);
            final int colonIndex = rawname.indexOf(58);
            String prefix;
            String localpart;
            if (colonIndex == -1) {
                prefix = "";
                localpart = rawname;
            }
            else {
                prefix = rawname.substring(0, colonIndex);
                localpart = rawname.substring(colonIndex + 1);
            }
            final String uri = schemaDoc.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(prefix));
            if (annotationDecl.getAttributeNS(uri, localpart).length() != 0) {
                ++i;
            }
            else {
                localStrBuffer.append(rawname).append("=\"");
                String value = annotationLocalAttrs.elementAt(i++);
                value = processAttValue(value);
                localStrBuffer.append(value).append("\" ");
            }
        }
        final StringBuffer contentBuffer = new StringBuffer(contents.length() + localStrBuffer.length());
        int annotationTokenEnd = contents.indexOf(SchemaSymbols.ELT_ANNOTATION);
        if (annotationTokenEnd == -1) {
            return null;
        }
        annotationTokenEnd += SchemaSymbols.ELT_ANNOTATION.length();
        contentBuffer.append(contents.substring(0, annotationTokenEnd));
        contentBuffer.append(localStrBuffer.toString());
        contentBuffer.append(contents.substring(annotationTokenEnd, contents.length()));
        final String annotation = contentBuffer.toString();
        if (this.fValidateAnnotations) {
            schemaDoc.addAnnotation(new XSAnnotationInfo(annotation, annotationDecl));
        }
        return new XSAnnotationImpl(annotation, grammar);
    }
    
    XSAnnotationImpl traverseSyntheticAnnotation(final Element annotationParent, final String initialContent, final Object[] parentAttrs, final boolean isGlobal, final XSDocumentInfo schemaDoc) {
        final String contents = initialContent;
        final SchemaGrammar grammar = this.fSchemaHandler.getGrammar(schemaDoc.fTargetNamespace);
        final Vector annotationLocalAttrs = (Vector)parentAttrs[XSAttributeChecker.ATTIDX_NONSCHEMA];
        if (annotationLocalAttrs == null || annotationLocalAttrs.isEmpty()) {
            if (this.fValidateAnnotations) {
                schemaDoc.addAnnotation(new XSAnnotationInfo(contents, annotationParent));
            }
            return new XSAnnotationImpl(contents, grammar);
        }
        final StringBuffer localStrBuffer = new StringBuffer(64);
        localStrBuffer.append(" ");
        int i = 0;
        while (i < annotationLocalAttrs.size()) {
            final String rawname = annotationLocalAttrs.elementAt(i++);
            final int colonIndex = rawname.indexOf(58);
            String prefix;
            if (colonIndex == -1) {
                prefix = "";
                final String localpart = rawname;
            }
            else {
                prefix = rawname.substring(0, colonIndex);
                final String localpart = rawname.substring(colonIndex + 1);
            }
            final String uri = schemaDoc.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(prefix));
            localStrBuffer.append(rawname).append("=\"");
            String value = annotationLocalAttrs.elementAt(i++);
            value = processAttValue(value);
            localStrBuffer.append(value).append("\" ");
        }
        final StringBuffer contentBuffer = new StringBuffer(contents.length() + localStrBuffer.length());
        int annotationTokenEnd = contents.indexOf(SchemaSymbols.ELT_ANNOTATION);
        if (annotationTokenEnd == -1) {
            return null;
        }
        annotationTokenEnd += SchemaSymbols.ELT_ANNOTATION.length();
        contentBuffer.append(contents.substring(0, annotationTokenEnd));
        contentBuffer.append(localStrBuffer.toString());
        contentBuffer.append(contents.substring(annotationTokenEnd, contents.length()));
        final String annotation = contentBuffer.toString();
        if (this.fValidateAnnotations) {
            schemaDoc.addAnnotation(new XSAnnotationInfo(annotation, annotationParent));
        }
        return new XSAnnotationImpl(annotation, grammar);
    }
    
    FacetInfo traverseFacets(Element content, final XSSimpleType baseValidator, final XSDocumentInfo schemaDoc) {
        short facetsPresent = 0;
        short facetsFixed = 0;
        final boolean hasQName = this.containsQName(baseValidator);
        Vector enumData = null;
        XSObjectListImpl enumAnnotations = null;
        XSObjectListImpl patternAnnotations = null;
        final Vector enumNSDecls = hasQName ? new Vector() : null;
        int currentFacet = 0;
        this.xsFacets.reset();
        while (content != null) {
            Object[] attrs = null;
            final String facet = DOMUtil.getLocalName(content);
            if (facet.equals(SchemaSymbols.ELT_ENUMERATION)) {
                attrs = this.fAttrChecker.checkAttributes(content, false, schemaDoc, hasQName);
                final String enumVal = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                if (enumVal == null) {
                    this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ENUMERATION, SchemaSymbols.ATT_VALUE }, content);
                    this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                    continue;
                }
                final NamespaceSupport nsDecls = (NamespaceSupport)attrs[XSAttributeChecker.ATTIDX_ENUMNSDECLS];
                if (baseValidator.getVariety() == 1 && baseValidator.getPrimitiveKind() == 20) {
                    schemaDoc.fValidationContext.setNamespaceSupport(nsDecls);
                    Object notation = null;
                    try {
                        final QName temp = (QName)XSDAbstractTraverser.fQNameDV.validate(enumVal, schemaDoc.fValidationContext, null);
                        notation = this.fSchemaHandler.getGlobalDecl(schemaDoc, 6, temp, content);
                    }
                    catch (final InvalidDatatypeValueException ex) {
                        this.reportSchemaError(ex.getKey(), ex.getArgs(), content);
                    }
                    if (notation == null) {
                        this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                        content = DOMUtil.getNextSiblingElement(content);
                        continue;
                    }
                    schemaDoc.fValidationContext.setNamespaceSupport(schemaDoc.fNamespaceSupport);
                }
                if (enumData == null) {
                    enumData = new Vector();
                    enumAnnotations = new XSObjectListImpl();
                }
                enumData.addElement(enumVal);
                enumAnnotations.addXSObject(null);
                if (hasQName) {
                    enumNSDecls.addElement(nsDecls);
                }
                Element child = DOMUtil.getFirstChildElement(content);
                if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    enumAnnotations.addXSObject(enumAnnotations.getLength() - 1, this.traverseAnnotationDecl(child, attrs, false, schemaDoc));
                    child = DOMUtil.getNextSiblingElement(child);
                }
                else {
                    final String text = DOMUtil.getSyntheticAnnotation(content);
                    if (text != null) {
                        enumAnnotations.addXSObject(enumAnnotations.getLength() - 1, this.traverseSyntheticAnnotation(content, text, attrs, false, schemaDoc));
                    }
                }
                if (child != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "enumeration", "(annotation?)", DOMUtil.getLocalName(child) }, child);
                }
            }
            else if (facet.equals(SchemaSymbols.ELT_PATTERN)) {
                facetsPresent |= 0x8;
                attrs = this.fAttrChecker.checkAttributes(content, false, schemaDoc);
                final String patternVal = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                if (patternVal == null) {
                    this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_PATTERN, SchemaSymbols.ATT_VALUE }, content);
                    this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                    continue;
                }
                if (this.fPattern.length() == 0) {
                    this.fPattern.append(patternVal);
                }
                else {
                    this.fPattern.append("|");
                    this.fPattern.append(patternVal);
                }
                Element child2 = DOMUtil.getFirstChildElement(content);
                if (child2 != null && DOMUtil.getLocalName(child2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    if (patternAnnotations == null) {
                        patternAnnotations = new XSObjectListImpl();
                    }
                    patternAnnotations.addXSObject(this.traverseAnnotationDecl(child2, attrs, false, schemaDoc));
                    child2 = DOMUtil.getNextSiblingElement(child2);
                }
                else {
                    final String text2 = DOMUtil.getSyntheticAnnotation(content);
                    if (text2 != null) {
                        if (patternAnnotations == null) {
                            patternAnnotations = new XSObjectListImpl();
                        }
                        patternAnnotations.addXSObject(this.traverseSyntheticAnnotation(content, text2, attrs, false, schemaDoc));
                    }
                }
                if (child2 != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "pattern", "(annotation?)", DOMUtil.getLocalName(child2) }, child2);
                }
            }
            else {
                if (facet.equals(SchemaSymbols.ELT_MINLENGTH)) {
                    currentFacet = 2;
                }
                else if (facet.equals(SchemaSymbols.ELT_MAXLENGTH)) {
                    currentFacet = 4;
                }
                else if (facet.equals(SchemaSymbols.ELT_MAXEXCLUSIVE)) {
                    currentFacet = 64;
                }
                else if (facet.equals(SchemaSymbols.ELT_MAXINCLUSIVE)) {
                    currentFacet = 32;
                }
                else if (facet.equals(SchemaSymbols.ELT_MINEXCLUSIVE)) {
                    currentFacet = 128;
                }
                else if (facet.equals(SchemaSymbols.ELT_MININCLUSIVE)) {
                    currentFacet = 256;
                }
                else if (facet.equals(SchemaSymbols.ELT_TOTALDIGITS)) {
                    currentFacet = 512;
                }
                else if (facet.equals(SchemaSymbols.ELT_FRACTIONDIGITS)) {
                    currentFacet = 1024;
                }
                else if (facet.equals(SchemaSymbols.ELT_WHITESPACE)) {
                    currentFacet = 16;
                }
                else {
                    if (!facet.equals(SchemaSymbols.ELT_LENGTH)) {
                        break;
                    }
                    currentFacet = 1;
                }
                attrs = this.fAttrChecker.checkAttributes(content, false, schemaDoc);
                if ((facetsPresent & currentFacet) != 0x0) {
                    this.reportSchemaError("src-single-facet-value", new Object[] { facet }, content);
                    this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                    continue;
                }
                if (attrs[XSAttributeChecker.ATTIDX_VALUE] == null) {
                    if (content.getAttributeNodeNS(null, "value") == null) {
                        this.reportSchemaError("s4s-att-must-appear", new Object[] { content.getLocalName(), SchemaSymbols.ATT_VALUE }, content);
                    }
                    this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                    continue;
                }
                facetsPresent |= (short)currentFacet;
                if (attrs[XSAttributeChecker.ATTIDX_FIXED]) {
                    facetsFixed |= (short)currentFacet;
                }
                switch (currentFacet) {
                    case 2: {
                        this.xsFacets.minLength = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 4: {
                        this.xsFacets.maxLength = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 64: {
                        this.xsFacets.maxExclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 32: {
                        this.xsFacets.maxInclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 128: {
                        this.xsFacets.minExclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 256: {
                        this.xsFacets.minInclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 512: {
                        this.xsFacets.totalDigits = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 1024: {
                        this.xsFacets.fractionDigits = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 16: {
                        this.xsFacets.whiteSpace = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).shortValue();
                        break;
                    }
                    case 1: {
                        this.xsFacets.length = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                }
                Element child3 = DOMUtil.getFirstChildElement(content);
                XSAnnotationImpl annotation = null;
                if (child3 != null && DOMUtil.getLocalName(child3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    annotation = this.traverseAnnotationDecl(child3, attrs, false, schemaDoc);
                    child3 = DOMUtil.getNextSiblingElement(child3);
                }
                else {
                    final String text2 = DOMUtil.getSyntheticAnnotation(content);
                    if (text2 != null) {
                        annotation = this.traverseSyntheticAnnotation(content, text2, attrs, false, schemaDoc);
                    }
                }
                switch (currentFacet) {
                    case 2: {
                        this.xsFacets.minLengthAnnotation = annotation;
                        break;
                    }
                    case 4: {
                        this.xsFacets.maxLengthAnnotation = annotation;
                        break;
                    }
                    case 64: {
                        this.xsFacets.maxExclusiveAnnotation = annotation;
                        break;
                    }
                    case 32: {
                        this.xsFacets.maxInclusiveAnnotation = annotation;
                        break;
                    }
                    case 128: {
                        this.xsFacets.minExclusiveAnnotation = annotation;
                        break;
                    }
                    case 256: {
                        this.xsFacets.minInclusiveAnnotation = annotation;
                        break;
                    }
                    case 512: {
                        this.xsFacets.totalDigitsAnnotation = annotation;
                        break;
                    }
                    case 1024: {
                        this.xsFacets.fractionDigitsAnnotation = annotation;
                        break;
                    }
                    case 16: {
                        this.xsFacets.whiteSpaceAnnotation = annotation;
                        break;
                    }
                    case 1: {
                        this.xsFacets.lengthAnnotation = annotation;
                        break;
                    }
                }
                if (child3 != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { facet, "(annotation?)", DOMUtil.getLocalName(child3) }, child3);
                }
            }
            this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
            content = DOMUtil.getNextSiblingElement(content);
        }
        if (enumData != null) {
            facetsPresent |= 0x800;
            this.xsFacets.enumeration = enumData;
            this.xsFacets.enumNSDecls = enumNSDecls;
            this.xsFacets.enumAnnotations = enumAnnotations;
        }
        if ((facetsPresent & 0x8) != 0x0) {
            this.xsFacets.pattern = this.fPattern.toString();
            this.xsFacets.patternAnnotations = patternAnnotations;
        }
        this.fPattern.setLength(0);
        return new FacetInfo(this.xsFacets, content, facetsPresent, facetsFixed);
    }
    
    private boolean containsQName(final XSSimpleType type) {
        if (type.getVariety() == 1) {
            final short primitive = type.getPrimitiveKind();
            return primitive == 18 || primitive == 20;
        }
        if (type.getVariety() == 2) {
            return this.containsQName((XSSimpleType)type.getItemType());
        }
        if (type.getVariety() == 3) {
            final XSObjectList members = type.getMemberTypes();
            for (int i = 0; i < members.getLength(); ++i) {
                if (this.containsQName((XSSimpleType)members.item(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    Element traverseAttrsAndAttrGrps(final Element firstAttr, final XSAttributeGroupDecl attrGrp, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final XSComplexTypeDecl enclosingCT) {
        Element child = null;
        XSAttributeGroupDecl tempAttrGrp = null;
        XSAttributeUseImpl tempAttrUse = null;
        XSAttributeUse otherUse = null;
        for (child = firstAttr; child != null; child = DOMUtil.getNextSiblingElement(child)) {
            final String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                tempAttrUse = this.fSchemaHandler.fAttributeTraverser.traverseLocal(child, schemaDoc, grammar, enclosingCT);
                if (tempAttrUse != null) {
                    if (tempAttrUse.fUse == 2) {
                        attrGrp.addAttributeUse(tempAttrUse);
                    }
                    else {
                        otherUse = attrGrp.getAttributeUseNoProhibited(tempAttrUse.fAttrDecl.getNamespace(), tempAttrUse.fAttrDecl.getName());
                        if (otherUse == null) {
                            final String idName = attrGrp.addAttributeUse(tempAttrUse);
                            if (idName != null) {
                                final String code = (enclosingCT == null) ? "ag-props-correct.3" : "ct-props-correct.5";
                                final String name = (enclosingCT == null) ? attrGrp.fName : enclosingCT.getName();
                                this.reportSchemaError(code, new Object[] { name, tempAttrUse.fAttrDecl.getName(), idName }, child);
                            }
                        }
                        else if (otherUse != tempAttrUse) {
                            final String code2 = (enclosingCT == null) ? "ag-props-correct.2" : "ct-props-correct.4";
                            final String name2 = (enclosingCT == null) ? attrGrp.fName : enclosingCT.getName();
                            this.reportSchemaError(code2, new Object[] { name2, tempAttrUse.fAttrDecl.getName() }, child);
                        }
                    }
                }
            }
            else {
                if (!childName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                    break;
                }
                tempAttrGrp = this.fSchemaHandler.fAttributeGroupTraverser.traverseLocal(child, schemaDoc, grammar);
                if (tempAttrGrp != null) {
                    final XSObjectList attrUseS = tempAttrGrp.getAttributeUses();
                    for (int attrCount = attrUseS.getLength(), i = 0; i < attrCount; ++i) {
                        final XSAttributeUseImpl oneAttrUse = (XSAttributeUseImpl)attrUseS.item(i);
                        if (oneAttrUse.fUse == 2) {
                            attrGrp.addAttributeUse(oneAttrUse);
                        }
                        else {
                            otherUse = attrGrp.getAttributeUseNoProhibited(oneAttrUse.fAttrDecl.getNamespace(), oneAttrUse.fAttrDecl.getName());
                            if (otherUse == null) {
                                final String idName2 = attrGrp.addAttributeUse(oneAttrUse);
                                if (idName2 != null) {
                                    final String code3 = (enclosingCT == null) ? "ag-props-correct.3" : "ct-props-correct.5";
                                    final String name3 = (enclosingCT == null) ? attrGrp.fName : enclosingCT.getName();
                                    this.reportSchemaError(code3, new Object[] { name3, oneAttrUse.fAttrDecl.getName(), idName2 }, child);
                                }
                            }
                            else if (oneAttrUse != otherUse) {
                                final String code4 = (enclosingCT == null) ? "ag-props-correct.2" : "ct-props-correct.4";
                                final String name4 = (enclosingCT == null) ? attrGrp.fName : enclosingCT.getName();
                                this.reportSchemaError(code4, new Object[] { name4, oneAttrUse.fAttrDecl.getName() }, child);
                            }
                        }
                    }
                    if (tempAttrGrp.fAttributeWC != null) {
                        if (attrGrp.fAttributeWC == null) {
                            attrGrp.fAttributeWC = tempAttrGrp.fAttributeWC;
                        }
                        else {
                            attrGrp.fAttributeWC = attrGrp.fAttributeWC.performIntersectionWith(tempAttrGrp.fAttributeWC, attrGrp.fAttributeWC.fProcessContents);
                            if (attrGrp.fAttributeWC == null) {
                                final String code5 = (enclosingCT == null) ? "src-attribute_group.2" : "src-ct.4";
                                final String name5 = (enclosingCT == null) ? attrGrp.fName : enclosingCT.getName();
                                this.reportSchemaError(code5, new Object[] { name5 }, child);
                            }
                        }
                    }
                }
            }
        }
        if (child != null) {
            final String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ANYATTRIBUTE)) {
                final XSWildcardDecl tempAttrWC = this.fSchemaHandler.fWildCardTraverser.traverseAnyAttribute(child, schemaDoc, grammar);
                if (attrGrp.fAttributeWC == null) {
                    attrGrp.fAttributeWC = tempAttrWC;
                }
                else {
                    attrGrp.fAttributeWC = tempAttrWC.performIntersectionWith(attrGrp.fAttributeWC, tempAttrWC.fProcessContents);
                    if (attrGrp.fAttributeWC == null) {
                        final String code = (enclosingCT == null) ? "src-attribute_group.2" : "src-ct.4";
                        final String name = (enclosingCT == null) ? attrGrp.fName : enclosingCT.getName();
                        this.reportSchemaError(code, new Object[] { name }, child);
                    }
                }
                child = DOMUtil.getNextSiblingElement(child);
            }
        }
        return child;
    }
    
    void reportSchemaError(final String key, final Object[] args, final Element ele) {
        this.fSchemaHandler.reportSchemaError(key, args, ele);
    }
    
    void checkNotationType(final String refName, final XSTypeDefinition typeDecl, final Element elem) {
        if (typeDecl.getTypeCategory() == 16 && ((XSSimpleType)typeDecl).getVariety() == 1 && ((XSSimpleType)typeDecl).getPrimitiveKind() == 20 && (((XSSimpleType)typeDecl).getDefinedFacets() & 0x800) == 0x0) {
            this.reportSchemaError("enumeration-required-notation", new Object[] { typeDecl.getName(), refName, DOMUtil.getLocalName(elem) }, elem);
        }
    }
    
    protected XSParticleDecl checkOccurrences(final XSParticleDecl particle, final String particleName, final Element parent, final int allContextFlags, final long defaultVals) {
        int min = particle.fMinOccurs;
        int max = particle.fMaxOccurs;
        final boolean defaultMin = (defaultVals & (long)(1 << XSAttributeChecker.ATTIDX_MINOCCURS)) != 0x0L;
        final boolean defaultMax = (defaultVals & (long)(1 << XSAttributeChecker.ATTIDX_MAXOCCURS)) != 0x0L;
        final boolean processingAllEl = (allContextFlags & 0x1) != 0x0;
        final boolean processingAllGP = (allContextFlags & 0x8) != 0x0;
        final boolean groupRefWithAll = (allContextFlags & 0x2) != 0x0;
        final boolean isGroupChild = (allContextFlags & 0x4) != 0x0;
        if (isGroupChild) {
            if (!defaultMin) {
                final Object[] args = { particleName, "minOccurs" };
                this.reportSchemaError("s4s-att-not-allowed", args, parent);
                min = 1;
            }
            if (!defaultMax) {
                final Object[] args = { particleName, "maxOccurs" };
                this.reportSchemaError("s4s-att-not-allowed", args, parent);
                max = 1;
            }
        }
        if (min == 0 && max == 0) {
            particle.fType = 0;
            return null;
        }
        if (processingAllEl) {
            if (max != 1) {
                this.reportSchemaError("cos-all-limited.2", new Object[] { (max == -1) ? "unbounded" : Integer.toString(max), ((XSElementDecl)particle.fValue).getName() }, parent);
                max = 1;
                if (min > 1) {
                    min = 1;
                }
            }
        }
        else if ((processingAllGP || groupRefWithAll) && max != 1) {
            this.reportSchemaError("cos-all-limited.1.2", null, parent);
            if (min > 1) {
                min = 1;
            }
            max = 1;
        }
        particle.fMinOccurs = min;
        particle.fMaxOccurs = max;
        return particle;
    }
    
    private static String processAttValue(final String original) {
        for (int length = original.length(), i = 0; i < length; ++i) {
            final char currChar = original.charAt(i);
            if (currChar == '\"' || currChar == '<' || currChar == '&' || currChar == '\t' || currChar == '\n' || currChar == '\r') {
                return escapeAttValue(original, i);
            }
        }
        return original;
    }
    
    private static String escapeAttValue(final String original, final int from) {
        final int length = original.length();
        final StringBuffer newVal = new StringBuffer(length);
        newVal.append(original.substring(0, from));
        for (int i = from; i < length; ++i) {
            final char currChar = original.charAt(i);
            if (currChar == '\"') {
                newVal.append("&quot;");
            }
            else if (currChar == '<') {
                newVal.append("&lt;");
            }
            else if (currChar == '&') {
                newVal.append("&amp;");
            }
            else if (currChar == '\t') {
                newVal.append("&#x9;");
            }
            else if (currChar == '\n') {
                newVal.append("&#xA;");
            }
            else if (currChar == '\r') {
                newVal.append("&#xD;");
            }
            else {
                newVal.append(currChar);
            }
        }
        return newVal.toString();
    }
    
    static {
        fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
    }
    
    static final class FacetInfo
    {
        final XSFacets facetdata;
        final Element nodeAfterFacets;
        final short fPresentFacets;
        final short fFixedFacets;
        
        FacetInfo(final XSFacets facets, final Element nodeAfterFacets, final short presentFacets, final short fixedFacets) {
            this.facetdata = facets;
            this.nodeAfterFacets = nodeAfterFacets;
            this.fPresentFacets = presentFacets;
            this.fFixedFacets = fixedFacets;
        }
    }
}
