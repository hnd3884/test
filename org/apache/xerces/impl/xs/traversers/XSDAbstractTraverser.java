package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import java.util.Iterator;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import java.util.Map;
import java.util.HashMap;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.dv.util.Base64;
import org.apache.xerces.xs.XSAnnotation;
import java.util.Collection;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XS11TypeHelper;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.apache.xerces.impl.xs.assertion.XSAssert;
import org.apache.xerces.impl.xs.assertion.Test;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.assertion.XSAssertImpl;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.w3c.dom.Element;
import java.util.Locale;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.validation.ValidationState;
import java.util.Vector;
import org.apache.xerces.util.SymbolTable;

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
    private Vector baseAsserts;
    ValidationState fValidationState;
    private static final XSSimpleType fQNameDV;
    private StringBuffer fPattern;
    private final XSFacets xsFacets;
    
    XSDAbstractTraverser(final XSDHandler fSchemaHandler, final XSAttributeChecker fAttrChecker) {
        this.fSchemaHandler = null;
        this.fSymbolTable = null;
        this.fAttrChecker = null;
        this.fValidateAnnotations = false;
        this.baseAsserts = new Vector();
        this.fValidationState = new ValidationState();
        this.fPattern = new StringBuffer();
        this.xsFacets = new XSFacets();
        this.fSchemaHandler = fSchemaHandler;
        this.fAttrChecker = fAttrChecker;
    }
    
    void reset(final SymbolTable symbolTable, final boolean fValidateAnnotations, final Locale locale) {
        this.fSymbolTable = symbolTable;
        this.fValidateAnnotations = fValidateAnnotations;
        this.fValidationState.setExtraChecking(false);
        this.fValidationState.setSymbolTable(symbolTable);
        this.fValidationState.setLocale(locale);
        this.fValidationState.setTypeValidatorHelper(this.fSchemaHandler.fTypeValidatorHelper);
    }
    
    XSAnnotationImpl traverseAnnotationDecl(final Element element, final Object[] array, final boolean b, final XSDocumentInfo xsDocumentInfo) {
        this.fAttrChecker.returnAttrArray(this.fAttrChecker.checkAttributes(element, b, xsDocumentInfo), xsDocumentInfo);
        final String annotation = DOMUtil.getAnnotation(element);
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null) {
            do {
                final String localName = DOMUtil.getLocalName(element2);
                if (!localName.equals(SchemaSymbols.ELT_APPINFO) && !localName.equals(SchemaSymbols.ELT_DOCUMENTATION)) {
                    this.reportSchemaError("src-annotation", new Object[] { localName }, element2);
                }
                else {
                    this.fAttrChecker.returnAttrArray(this.fAttrChecker.checkAttributes(element2, true, xsDocumentInfo), xsDocumentInfo);
                }
                element2 = DOMUtil.getNextSiblingElement(element2);
            } while (element2 != null);
        }
        if (annotation == null) {
            return null;
        }
        final SchemaGrammar grammar = this.fSchemaHandler.getGrammar(xsDocumentInfo.fTargetNamespace);
        final Vector vector = (Vector)array[XSAttributeChecker.ATTIDX_NONSCHEMA];
        if (vector == null || vector.isEmpty()) {
            if (this.fValidateAnnotations) {
                xsDocumentInfo.addAnnotation(new XSAnnotationInfo(annotation, element));
            }
            return new XSAnnotationImpl(annotation, grammar);
        }
        final StringBuffer sb = new StringBuffer(64);
        sb.append(' ');
        int i = 0;
        while (i < vector.size()) {
            final String s = vector.elementAt(i++);
            final int index = s.indexOf(58);
            String substring;
            String substring2;
            if (index == -1) {
                substring = "";
                substring2 = s;
            }
            else {
                substring = s.substring(0, index);
                substring2 = s.substring(index + 1);
            }
            if (element.getAttributeNS(xsDocumentInfo.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(substring)), substring2).length() != 0) {
                ++i;
            }
            else {
                sb.append(s).append("=\"");
                sb.append(processAttValue((String)vector.elementAt(i++))).append("\" ");
            }
        }
        final StringBuffer sb2 = new StringBuffer(annotation.length() + sb.length());
        final int index2 = annotation.indexOf(SchemaSymbols.ELT_ANNOTATION);
        if (index2 == -1) {
            return null;
        }
        final int n = index2 + SchemaSymbols.ELT_ANNOTATION.length();
        sb2.append(annotation.substring(0, n));
        sb2.append(sb.toString());
        sb2.append(annotation.substring(n, annotation.length()));
        final String string = sb2.toString();
        if (this.fValidateAnnotations) {
            xsDocumentInfo.addAnnotation(new XSAnnotationInfo(string, element));
        }
        return new XSAnnotationImpl(string, grammar);
    }
    
    XSAnnotationImpl traverseSyntheticAnnotation(final Element element, final String s, final Object[] array, final boolean b, final XSDocumentInfo xsDocumentInfo) {
        final SchemaGrammar grammar = this.fSchemaHandler.getGrammar(xsDocumentInfo.fTargetNamespace);
        final Vector vector = (Vector)array[XSAttributeChecker.ATTIDX_NONSCHEMA];
        if (vector == null || vector.isEmpty()) {
            if (this.fValidateAnnotations) {
                xsDocumentInfo.addAnnotation(new XSAnnotationInfo(s, element));
            }
            return new XSAnnotationImpl(s, grammar);
        }
        final StringBuffer sb = new StringBuffer(64);
        sb.append(' ');
        int i = 0;
        while (i < vector.size()) {
            final String s2 = vector.elementAt(i++);
            final int index = s2.indexOf(58);
            String substring;
            if (index == -1) {
                substring = "";
            }
            else {
                substring = s2.substring(0, index);
                s2.substring(index + 1);
            }
            xsDocumentInfo.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(substring));
            sb.append(s2).append("=\"");
            sb.append(processAttValue((String)vector.elementAt(i++))).append("\" ");
        }
        final StringBuffer sb2 = new StringBuffer(s.length() + sb.length());
        final int index2 = s.indexOf(SchemaSymbols.ELT_ANNOTATION);
        if (index2 == -1) {
            return null;
        }
        final int n = index2 + SchemaSymbols.ELT_ANNOTATION.length();
        sb2.append(s.substring(0, n));
        sb2.append(sb.toString());
        sb2.append(s.substring(n, s.length()));
        final String string = sb2.toString();
        if (this.fValidateAnnotations) {
            xsDocumentInfo.addAnnotation(new XSAnnotationInfo(string, element));
        }
        return new XSAnnotationImpl(string, grammar);
    }
    
    private void getAssertsFromBaseTypes(final XSSimpleType xsSimpleType) {
        final XSObjectList multiValueFacets = xsSimpleType.getMultiValueFacets();
        for (int i = 0; i < multiValueFacets.getLength(); ++i) {
            final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)multiValueFacets.item(i);
            if (xsMultiValueFacet.getFacetKind() == 16384) {
                final Vector asserts = xsMultiValueFacet.getAsserts();
                for (int j = 0; j < asserts.size(); ++j) {
                    if (!this.assertExists((XSAssertImpl)asserts.get(j))) {
                        this.baseAsserts.add(asserts.get(j));
                    }
                }
                break;
            }
        }
        if (xsSimpleType.getBaseType() != null) {
            this.getAssertsFromBaseTypes((XSSimpleType)xsSimpleType.getBaseType());
        }
    }
    
    private boolean assertExists(final XSAssertImpl xsAssertImpl) {
        boolean b = false;
        for (int i = 0; i < this.baseAsserts.size(); ++i) {
            if (((XSAssertImpl)this.baseAsserts.get(i)).equals(xsAssertImpl)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    FacetInfo traverseFacets(Element element, final XSTypeDefinition xsTypeDefinition, final XSSimpleType xsSimpleType, final XSDocumentInfo xsDocumentInfo) {
        short n = 0;
        short n2 = 0;
        final boolean containsQName = this.containsQName(xsSimpleType);
        Vector<String> enumeration = null;
        Vector<XSAssertImpl> assertFacets = null;
        XSObjectListImpl enumAnnotations = null;
        XSObjectListImpl patternAnnotations = null;
        final Vector enumNSDecls = containsQName ? new Vector<NamespaceSupport>() : null;
        this.xsFacets.reset();
        boolean b = false;
        final Element element2 = (Element)element.getParentNode();
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        while (element != null) {
            final String localName = DOMUtil.getLocalName(element);
            Object[] array;
            if (localName.equals(SchemaSymbols.ELT_ENUMERATION)) {
                array = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo, containsQName);
                final String s = (String)array[XSAttributeChecker.ATTIDX_VALUE];
                if (s == null) {
                    this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ENUMERATION, SchemaSymbols.ATT_VALUE }, element);
                    this.fAttrChecker.returnAttrArray(array, xsDocumentInfo);
                    element = DOMUtil.getNextSiblingElement(element);
                    continue;
                }
                final NamespaceSupport namespaceSupport = (NamespaceSupport)array[XSAttributeChecker.ATTIDX_ENUMNSDECLS];
                if (xsSimpleType.getVariety() == 1 && xsSimpleType.getPrimitiveKind() == 20) {
                    xsDocumentInfo.fValidationContext.setNamespaceSupport(namespaceSupport);
                    Object globalDecl = null;
                    try {
                        globalDecl = this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 6, (QName)XSDAbstractTraverser.fQNameDV.validate(s, xsDocumentInfo.fValidationContext, null), element);
                    }
                    catch (final InvalidDatatypeValueException ex) {
                        this.reportSchemaError(ex.getKey(), ex.getArgs(), element);
                    }
                    if (globalDecl == null) {
                        this.fAttrChecker.returnAttrArray(array, xsDocumentInfo);
                        element = DOMUtil.getNextSiblingElement(element);
                        continue;
                    }
                    xsDocumentInfo.fValidationContext.setNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
                }
                if (enumeration == null) {
                    enumeration = new Vector<String>();
                    enumAnnotations = new XSObjectListImpl();
                }
                enumeration.addElement(s);
                enumAnnotations.addXSObject(null);
                if (containsQName) {
                    enumNSDecls.addElement(namespaceSupport);
                }
                Element element3 = DOMUtil.getFirstChildElement(element);
                if (element3 != null && DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    enumAnnotations.addXSObject(enumAnnotations.getLength() - 1, this.traverseAnnotationDecl(element3, array, false, xsDocumentInfo));
                    element3 = DOMUtil.getNextSiblingElement(element3);
                }
                else {
                    final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                    if (syntheticAnnotation != null) {
                        enumAnnotations.addXSObject(enumAnnotations.getLength() - 1, this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo));
                    }
                }
                if (element3 != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "enumeration", "(annotation?)", DOMUtil.getLocalName(element3) }, element3);
                }
            }
            else if (localName.equals(SchemaSymbols.ELT_PATTERN)) {
                array = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
                final String s2 = (String)array[XSAttributeChecker.ATTIDX_VALUE];
                if (s2 == null) {
                    this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_PATTERN, SchemaSymbols.ATT_VALUE }, element);
                    this.fAttrChecker.returnAttrArray(array, xsDocumentInfo);
                    element = DOMUtil.getNextSiblingElement(element);
                    continue;
                }
                b = true;
                if (this.fPattern.length() == 0) {
                    this.fPattern.append(s2);
                }
                else {
                    this.fPattern.append('|');
                    this.fPattern.append(s2);
                }
                Element element4 = DOMUtil.getFirstChildElement(element);
                if (element4 != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    if (patternAnnotations == null) {
                        patternAnnotations = new XSObjectListImpl();
                    }
                    patternAnnotations.addXSObject(this.traverseAnnotationDecl(element4, array, false, xsDocumentInfo));
                    element4 = DOMUtil.getNextSiblingElement(element4);
                }
                else {
                    final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element);
                    if (syntheticAnnotation2 != null) {
                        if (patternAnnotations == null) {
                            patternAnnotations = new XSObjectListImpl();
                        }
                        patternAnnotations.addXSObject(this.traverseSyntheticAnnotation(element, syntheticAnnotation2, array, false, xsDocumentInfo));
                    }
                }
                if (element4 != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "pattern", "(annotation?)", DOMUtil.getLocalName(element4) }, element4);
                }
            }
            else if (localName.equals(SchemaSymbols.ELT_ASSERTION) && this.fSchemaHandler.fSchemaVersion == 4) {
                array = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
                final String s3 = (String)array[XSAttributeChecker.ATTIDX_XPATH];
                String xPathDefaultNamespace = (String)array[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS];
                if (xPathDefaultNamespace == null) {
                    if (xsDocumentInfo.fXpathDefaultNamespaceIs2PoundDefault) {
                        xPathDefaultNamespace = xsDocumentInfo.fValidationContext.getURI(XMLSymbols.EMPTY_STRING);
                        if (xPathDefaultNamespace != null) {
                            xPathDefaultNamespace = this.fSymbolTable.addSymbol(xPathDefaultNamespace);
                        }
                    }
                    else {
                        xPathDefaultNamespace = xsDocumentInfo.fXpathDefaultNamespace;
                    }
                }
                if (s3 != null) {
                    final Element firstChildElement = DOMUtil.getFirstChildElement(element);
                    XSObject xsObject = null;
                    if (firstChildElement != null) {
                        if (DOMUtil.getLocalName(firstChildElement).equals(SchemaSymbols.ELT_ANNOTATION)) {
                            xsObject = this.traverseAnnotationDecl(firstChildElement, array, false, xsDocumentInfo);
                            final Element nextSiblingElement = DOMUtil.getNextSiblingElement(firstChildElement);
                            if (nextSiblingElement != null) {
                                this.reportSchemaError("s4s-elt-invalid-content.1", new Object[] { DOMUtil.getLocalName(element), DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
                            }
                        }
                        else {
                            final String syntheticAnnotation3 = DOMUtil.getSyntheticAnnotation(firstChildElement);
                            if (syntheticAnnotation3 != null) {
                                xsObject = this.traverseSyntheticAnnotation(firstChildElement, syntheticAnnotation3, array, false, xsDocumentInfo);
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
                    final XSAssertImpl xsAssertImpl = new XSAssertImpl(xsTypeDefinition, empty_LIST, this.fSchemaHandler);
                    final Test test = new Test(s3, xsDocumentInfo.fNamespaceSupport, xsAssertImpl);
                    xsAssertImpl.setAssertKind((short)17);
                    xsAssertImpl.setTest(test, element);
                    xsAssertImpl.setXPathDefaultNamespace(xPathDefaultNamespace);
                    xsAssertImpl.setXPath2NamespaceContext(new SchemaNamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                    final String trim = XMLChar.trim(element.getAttributeNS(SchemaSymbols.URI_XERCES_EXTENSIONS, SchemaSymbols.ATT_ASSERT_MESSAGE));
                    if (!"".equals(trim)) {
                        xsAssertImpl.setMessage(trim);
                    }
                    if (assertFacets == null) {
                        assertFacets = new Vector<XSAssertImpl>();
                    }
                    assertFacets.addElement(xsAssertImpl);
                }
                else {
                    this.reportSchemaError("src-assert.3.13.1", new Object[] { DOMUtil.getLocalName(element), XS11TypeHelper.getSchemaTypeName(xsTypeDefinition) }, element);
                }
            }
            else {
                short n3;
                if (localName.equals(SchemaSymbols.ELT_MINLENGTH)) {
                    n3 = 2;
                }
                else if (localName.equals(SchemaSymbols.ELT_MAXLENGTH)) {
                    n3 = 4;
                }
                else if (localName.equals(SchemaSymbols.ELT_MAXEXCLUSIVE)) {
                    n3 = 64;
                }
                else if (localName.equals(SchemaSymbols.ELT_MAXINCLUSIVE)) {
                    n3 = 32;
                }
                else if (localName.equals(SchemaSymbols.ELT_MINEXCLUSIVE)) {
                    n3 = 128;
                }
                else if (localName.equals(SchemaSymbols.ELT_MININCLUSIVE)) {
                    n3 = 256;
                }
                else if (localName.equals(SchemaSymbols.ELT_TOTALDIGITS)) {
                    n3 = 512;
                }
                else if (localName.equals(SchemaSymbols.ELT_FRACTIONDIGITS)) {
                    n3 = 1024;
                }
                else if (localName.equals(SchemaSymbols.ELT_WHITESPACE)) {
                    n3 = 16;
                }
                else if (localName.equals(SchemaSymbols.ELT_LENGTH)) {
                    n3 = 1;
                }
                else if (localName.equals(SchemaSymbols.ELT_MAXSCALE)) {
                    n3 = 4096;
                }
                else if (localName.equals(SchemaSymbols.ELT_MINSCALE)) {
                    n3 = 8192;
                }
                else {
                    if (!localName.equals(SchemaSymbols.ELT_EXPLICITTIMEZONE) || this.fSchemaHandler.fSchemaVersion != 4) {
                        break;
                    }
                    n3 = -32768;
                }
                array = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
                if ((n & n3) != 0x0) {
                    this.reportSchemaError("src-single-facet-value", new Object[] { localName }, element);
                    this.fAttrChecker.returnAttrArray(array, xsDocumentInfo);
                    element = DOMUtil.getNextSiblingElement(element);
                    continue;
                }
                if (array[XSAttributeChecker.ATTIDX_VALUE] == null) {
                    if (element.getAttributeNodeNS(null, "value") == null) {
                        this.reportSchemaError("s4s-att-must-appear", new Object[] { element.getLocalName(), SchemaSymbols.ATT_VALUE }, element);
                    }
                    this.fAttrChecker.returnAttrArray(array, xsDocumentInfo);
                    element = DOMUtil.getNextSiblingElement(element);
                    continue;
                }
                n |= n3;
                if (array[XSAttributeChecker.ATTIDX_FIXED]) {
                    n2 |= n3;
                }
                switch (n3) {
                    case 2: {
                        this.xsFacets.minLength = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        b3 = true;
                        break;
                    }
                    case 4: {
                        this.xsFacets.maxLength = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        b4 = true;
                        break;
                    }
                    case 64: {
                        this.xsFacets.maxExclusive = (String)array[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 32: {
                        this.xsFacets.maxInclusive = (String)array[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 128: {
                        this.xsFacets.minExclusive = (String)array[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 256: {
                        this.xsFacets.minInclusive = (String)array[XSAttributeChecker.ATTIDX_VALUE];
                        break;
                    }
                    case 512: {
                        this.xsFacets.totalDigits = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 1024: {
                        this.xsFacets.fractionDigits = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 16: {
                        this.xsFacets.whiteSpace = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).shortValue();
                        break;
                    }
                    case 1: {
                        this.xsFacets.length = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        b2 = true;
                        break;
                    }
                    case 4096: {
                        this.xsFacets.maxScale = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case 8192: {
                        this.xsFacets.minScale = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                        break;
                    }
                    case -32768: {
                        this.xsFacets.explicitTimezone = ((XInt)array[XSAttributeChecker.ATTIDX_VALUE]).shortValue();
                        break;
                    }
                }
                Element element5 = DOMUtil.getFirstChildElement(element);
                XSAnnotation explicitTimezoneAnnotation = null;
                if (element5 != null && DOMUtil.getLocalName(element5).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    explicitTimezoneAnnotation = this.traverseAnnotationDecl(element5, array, false, xsDocumentInfo);
                    element5 = DOMUtil.getNextSiblingElement(element5);
                }
                else {
                    final String syntheticAnnotation4 = DOMUtil.getSyntheticAnnotation(element);
                    if (syntheticAnnotation4 != null) {
                        explicitTimezoneAnnotation = this.traverseSyntheticAnnotation(element, syntheticAnnotation4, array, false, xsDocumentInfo);
                    }
                }
                switch (n3) {
                    case 2: {
                        this.xsFacets.minLengthAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 4: {
                        this.xsFacets.maxLengthAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 64: {
                        this.xsFacets.maxExclusiveAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 32: {
                        this.xsFacets.maxInclusiveAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 128: {
                        this.xsFacets.minExclusiveAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 256: {
                        this.xsFacets.minInclusiveAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 512: {
                        this.xsFacets.totalDigitsAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 1024: {
                        this.xsFacets.fractionDigitsAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 16: {
                        this.xsFacets.whiteSpaceAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 1: {
                        this.xsFacets.lengthAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 4096: {
                        this.xsFacets.maxScaleAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case 8192: {
                        this.xsFacets.minScaleAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                    case -32768: {
                        this.xsFacets.explicitTimezoneAnnotation = explicitTimezoneAnnotation;
                        break;
                    }
                }
                if (element5 != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "(annotation?)", DOMUtil.getLocalName(element5) }, element5);
                }
            }
            this.fAttrChecker.returnAttrArray(array, xsDocumentInfo);
            element = DOMUtil.getNextSiblingElement(element);
        }
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.getAssertsFromBaseTypes(xsSimpleType);
            if (this.baseAsserts.size() > 0) {
                if (assertFacets == null) {
                    assertFacets = new Vector<XSAssertImpl>();
                }
                assertFacets.addAll(this.baseAsserts);
                this.baseAsserts.clear();
            }
        }
        if (enumeration != null) {
            n |= 0x800;
            this.xsFacets.enumeration = enumeration;
            this.xsFacets.enumNSDecls = enumNSDecls;
            this.xsFacets.enumAnnotations = enumAnnotations;
        }
        if (b) {
            n |= 0x8;
            this.xsFacets.pattern = this.fPattern.toString();
            this.xsFacets.patternAnnotations = patternAnnotations;
        }
        if (assertFacets != null) {
            n |= 0x4000;
            this.xsFacets.assertFacets = assertFacets;
        }
        this.fPattern.setLength(0);
        if (enumeration != null) {
            if (b2) {
                this.checkEnumerationAndLengthInconsistency(xsSimpleType, enumeration, element2, XS11TypeHelper.getSchemaTypeName(xsTypeDefinition));
            }
            if (b3) {
                this.checkEnumerationAndMinLengthInconsistency(xsSimpleType, enumeration, element2, XS11TypeHelper.getSchemaTypeName(xsTypeDefinition));
            }
            if (b4) {
                this.checkEnumerationAndMaxLengthInconsistency(xsSimpleType, enumeration, element2, XS11TypeHelper.getSchemaTypeName(xsTypeDefinition));
            }
        }
        return new FacetInfo(this.xsFacets, element, n, n2);
    }
    
    private void checkEnumerationAndMaxLengthInconsistency(final XSSimpleType xsSimpleType, final Vector vector, final Element element, final String s) {
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleType.getNamespace()) && "hexBinary".equals(xsSimpleType.getName())) {
            for (int i = 0; i < vector.size(); ++i) {
                final String s2 = vector.get(i);
                if (s2.length() / 2 > this.xsFacets.maxLength) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s2, SchemaSymbols.ELT_MAXLENGTH, s }, element);
                }
            }
        }
        else if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleType.getNamespace()) && "base64Binary".equals(xsSimpleType.getName())) {
            for (int j = 0; j < vector.size(); ++j) {
                final String s3 = vector.get(j);
                final byte[] decode = Base64.decode(s3);
                if (decode != null && new String(decode).length() > this.xsFacets.maxLength) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s3, SchemaSymbols.ELT_MAXLENGTH, s }, element);
                }
            }
        }
        else {
            for (int k = 0; k < vector.size(); ++k) {
                final String s4 = vector.get(k);
                if (s4.length() > this.xsFacets.maxLength) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s4, SchemaSymbols.ELT_MAXLENGTH, s }, element);
                }
            }
        }
    }
    
    private void checkEnumerationAndMinLengthInconsistency(final XSSimpleType xsSimpleType, final Vector vector, final Element element, final String s) {
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleType.getNamespace()) && "hexBinary".equals(xsSimpleType.getName())) {
            for (int i = 0; i < vector.size(); ++i) {
                final String s2 = vector.get(i);
                if (s2.length() / 2 < this.xsFacets.minLength) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s2, SchemaSymbols.ELT_MINLENGTH, s }, element);
                }
            }
        }
        else if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleType.getNamespace()) && "base64Binary".equals(xsSimpleType.getName())) {
            for (int j = 0; j < vector.size(); ++j) {
                final String s3 = vector.get(j);
                final byte[] decode = Base64.decode(s3);
                if (decode != null && new String(decode).length() < this.xsFacets.minLength) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s3, SchemaSymbols.ELT_MINLENGTH, s }, element);
                }
            }
        }
        else {
            for (int k = 0; k < vector.size(); ++k) {
                final String s4 = vector.get(k);
                if (s4.length() < this.xsFacets.minLength) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s4, SchemaSymbols.ELT_MINLENGTH, s }, element);
                }
            }
        }
    }
    
    private void checkEnumerationAndLengthInconsistency(final XSSimpleType xsSimpleType, final Vector vector, final Element element, final String s) {
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleType.getNamespace()) && "hexBinary".equals(xsSimpleType.getName())) {
            for (int i = 0; i < vector.size(); ++i) {
                final String s2 = vector.get(i);
                if (s2.length() / 2 != this.xsFacets.length) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s2, SchemaSymbols.ELT_LENGTH, s }, element);
                }
            }
        }
        else if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsSimpleType.getNamespace()) && "base64Binary".equals(xsSimpleType.getName())) {
            for (int j = 0; j < vector.size(); ++j) {
                final String s3 = vector.get(j);
                final byte[] decode = Base64.decode(s3);
                if (decode != null && new String(decode).length() != this.xsFacets.length) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s3, SchemaSymbols.ELT_LENGTH, s }, element);
                }
            }
        }
        else {
            for (int k = 0; k < vector.size(); ++k) {
                final String s4 = vector.get(k);
                if (s4.length() != this.xsFacets.length) {
                    this.reportSchemaWarning("FacetsContradict", new Object[] { s4, SchemaSymbols.ELT_LENGTH, s }, element);
                }
            }
        }
    }
    
    private boolean containsQName(final XSSimpleType xsSimpleType) {
        if (xsSimpleType.getVariety() == 1) {
            final short primitiveKind = xsSimpleType.getPrimitiveKind();
            return primitiveKind == 18 || primitiveKind == 20;
        }
        if (xsSimpleType.getVariety() == 2) {
            return this.containsQName((XSSimpleType)xsSimpleType.getItemType());
        }
        if (xsSimpleType.getVariety() == 3) {
            final XSObjectList memberTypes = xsSimpleType.getMemberTypes();
            for (int i = 0; i < memberTypes.getLength(); ++i) {
                if (this.containsQName((XSSimpleType)memberTypes.item(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    Element traverseAttrsAndAttrGrps(final Element element, final XSAttributeGroupDecl xsAttributeGroupDecl, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final XSObject xsObject) {
        final HashMap hashMap = new HashMap();
        Element element2;
        for (element2 = element; element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
            final String localName = DOMUtil.getLocalName(element2);
            if (localName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                final XSAttributeUseImpl traverseLocal = this.fSchemaHandler.fAttributeTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, xsObject);
                if (traverseLocal != null) {
                    if (traverseLocal.fUse == 2) {
                        xsAttributeGroupDecl.addAttributeUse(traverseLocal);
                    }
                    else {
                        final XSAttributeUse attributeUseNoProhibited = xsAttributeGroupDecl.getAttributeUseNoProhibited(traverseLocal.fAttrDecl.getNamespace(), traverseLocal.fAttrDecl.getName());
                        if (attributeUseNoProhibited == null) {
                            final String addAttributeUse = xsAttributeGroupDecl.addAttributeUse(traverseLocal, this.fSchemaHandler.fSchemaVersion == 4);
                            if (addAttributeUse != null) {
                                this.reportSchemaError((xsObject instanceof XSAttributeGroupDecl) ? "ag-props-correct.3" : "ct-props-correct.5", new Object[] { xsObject.getName(), traverseLocal.fAttrDecl.getName(), addAttributeUse }, element2);
                            }
                        }
                        else if (attributeUseNoProhibited != traverseLocal) {
                            this.reportSchemaError((xsObject instanceof XSAttributeGroupDecl) ? "ag-props-correct.2" : "ct-props-correct.4", new Object[] { xsObject.getName(), traverseLocal.fAttrDecl.getName() }, element2);
                        }
                    }
                }
            }
            else {
                if (!localName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                    break;
                }
                final XSAttributeGroupDecl traverseLocal2 = this.fSchemaHandler.fAttributeGroupTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar);
                if (traverseLocal2 != null) {
                    this.setAttributeGroupCount(hashMap, traverseLocal2.getName(), traverseLocal2.getNamespace());
                    final XSObjectList attributeUses = traverseLocal2.getAttributeUses();
                    for (int length = attributeUses.getLength(), i = 0; i < length; ++i) {
                        final XSAttributeUseImpl xsAttributeUseImpl = (XSAttributeUseImpl)attributeUses.item(i);
                        if (xsAttributeUseImpl.fUse == 2) {
                            xsAttributeGroupDecl.addAttributeUse(xsAttributeUseImpl);
                        }
                        else {
                            final XSAttributeUse attributeUseNoProhibited2 = xsAttributeGroupDecl.getAttributeUseNoProhibited(xsAttributeUseImpl.fAttrDecl.getNamespace(), xsAttributeUseImpl.fAttrDecl.getName());
                            if (attributeUseNoProhibited2 == null) {
                                final String addAttributeUse2 = xsAttributeGroupDecl.addAttributeUse(xsAttributeUseImpl, this.fSchemaHandler.fSchemaVersion == 4);
                                if (addAttributeUse2 != null) {
                                    this.reportSchemaError((xsObject instanceof XSAttributeGroupDecl) ? "ag-props-correct.3" : "ct-props-correct.5", new Object[] { xsObject.getName(), xsAttributeUseImpl.fAttrDecl.getName(), addAttributeUse2 }, element2);
                                }
                            }
                            else if (xsAttributeUseImpl != attributeUseNoProhibited2) {
                                this.reportSchemaError((xsObject instanceof XSAttributeGroupDecl) ? "ag-props-correct.2" : "ct-props-correct.4", new Object[] { xsObject.getName(), xsAttributeUseImpl.fAttrDecl.getName() }, element2);
                            }
                        }
                    }
                    if (traverseLocal2.fAttributeWC != null) {
                        if (xsAttributeGroupDecl.fAttributeWC == null) {
                            xsAttributeGroupDecl.fAttributeWC = traverseLocal2.fAttributeWC;
                        }
                        else {
                            xsAttributeGroupDecl.fAttributeWC = this.fSchemaHandler.fXSConstraints.performIntersectionWith(xsAttributeGroupDecl.fAttributeWC, traverseLocal2.fAttributeWC, xsAttributeGroupDecl.fAttributeWC.fProcessContents);
                            if (xsAttributeGroupDecl.fAttributeWC == null) {
                                this.reportSchemaError((xsObject instanceof XSAttributeGroupDecl) ? "src-attribute_group.2" : "src-ct.4", new Object[] { xsObject.getName() }, element2);
                            }
                        }
                    }
                }
            }
        }
        final Iterator iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            final QName qName = (QName)iterator.next();
            if ((int)hashMap.get(qName) > 1) {
                this.reportSchemaWarning("src-ct.7", new Object[] { qName.localpart, xsObject.getName() }, (Element)element.getParentNode());
            }
        }
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANYATTRIBUTE)) {
            final XSWildcardDecl traverseAnyAttribute = this.fSchemaHandler.fWildCardTraverser.traverseAnyAttribute(element2, xsDocumentInfo, schemaGrammar);
            if (xsAttributeGroupDecl.fAttributeWC == null) {
                xsAttributeGroupDecl.fAttributeWC = traverseAnyAttribute;
            }
            else {
                xsAttributeGroupDecl.fAttributeWC = this.fSchemaHandler.fXSConstraints.performIntersectionWith(traverseAnyAttribute, xsAttributeGroupDecl.fAttributeWC, traverseAnyAttribute.fProcessContents);
                if (xsAttributeGroupDecl.fAttributeWC == null) {
                    this.reportSchemaError((xsObject instanceof XSAttributeGroupDecl) ? "src-attribute_group.2" : "src-ct.4", new Object[] { xsObject.getName() }, element2);
                }
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        return element2;
    }
    
    private void setAttributeGroupCount(final Map map, final String s, final String s2) {
        final QName qName = new QName(null, s, s, s2);
        if (map.containsKey(qName)) {
            map.put(qName, new Integer((int)map.get(qName) + 1));
        }
        else {
            map.put(qName, new Integer(1));
        }
    }
    
    void reportSchemaError(final String s, final Object[] array, final Element element) {
        this.fSchemaHandler.reportSchemaError(s, array, element);
    }
    
    void reportSchemaWarning(final String s, final Object[] array, final Element element) {
        this.fSchemaHandler.reportSchemaWarning(s, array, element);
    }
    
    void checkNotationType(final String s, final XSTypeDefinition xsTypeDefinition, final Element element) {
        if (xsTypeDefinition.getTypeCategory() == 16 && ((XSSimpleType)xsTypeDefinition).getVariety() == 1 && ((XSSimpleType)xsTypeDefinition).getPrimitiveKind() == 20 && (((XSSimpleType)xsTypeDefinition).getDefinedFacets() & 0x800) == 0x0) {
            this.reportSchemaError("enumeration-required-notation", new Object[] { xsTypeDefinition.getName(), s, DOMUtil.getLocalName(element) }, element);
        }
    }
    
    protected XSParticleDecl checkOccurrences(final XSParticleDecl xsParticleDecl, final String s, final Element element, final int n, final long n2) {
        int fMinOccurs = xsParticleDecl.fMinOccurs;
        int fMaxOccurs = xsParticleDecl.fMaxOccurs;
        final boolean b = (n2 & (long)(1 << XSAttributeChecker.ATTIDX_MINOCCURS)) != 0x0L;
        final boolean b2 = (n2 & (long)(1 << XSAttributeChecker.ATTIDX_MAXOCCURS)) != 0x0L;
        final boolean b3 = (n & 0x1) != 0x0;
        final boolean b4 = (n & 0x8) != 0x0;
        final boolean b5 = (n & 0x2) != 0x0;
        if ((n & 0x4) != 0x0) {
            if (!b) {
                this.reportSchemaError("s4s-att-not-allowed", new Object[] { s, "minOccurs" }, element);
                fMinOccurs = 1;
            }
            if (!b2) {
                this.reportSchemaError("s4s-att-not-allowed", new Object[] { s, "maxOccurs" }, element);
                fMaxOccurs = 1;
            }
        }
        if (fMinOccurs == 0 && fMaxOccurs == 0) {
            xsParticleDecl.fType = 0;
            return null;
        }
        if (b3) {
            if (fMaxOccurs != 1 && this.fSchemaHandler.fSchemaVersion != 4) {
                this.reportSchemaError("cos-all-limited.2", new Object[] { (fMaxOccurs == -1) ? "unbounded" : Integer.toString(fMaxOccurs), ((XSElementDecl)xsParticleDecl.fValue).getName() }, element);
                fMaxOccurs = 1;
                if (fMinOccurs > 1) {
                    fMinOccurs = 1;
                }
            }
        }
        else if ((b4 || b5) && fMaxOccurs != 1) {
            this.reportSchemaError("cos-all-limited.1.2", null, element);
            if (fMinOccurs > 1) {
                fMinOccurs = 1;
            }
            fMaxOccurs = 1;
        }
        xsParticleDecl.fMinOccurs = fMinOccurs;
        xsParticleDecl.fMaxOccurs = fMaxOccurs;
        return xsParticleDecl;
    }
    
    private static String processAttValue(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"' || char1 == '<' || char1 == '&' || char1 == '\t' || char1 == '\n' || char1 == '\r') {
                return escapeAttValue(s, i);
            }
        }
        return s;
    }
    
    private static String escapeAttValue(final String s, final int n) {
        final int length = s.length();
        final StringBuffer sb = new StringBuffer(length);
        sb.append(s.substring(0, n));
        for (int i = n; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"') {
                sb.append("&quot;");
            }
            else if (char1 == '<') {
                sb.append("&lt;");
            }
            else if (char1 == '&') {
                sb.append("&amp;");
            }
            else if (char1 == '\t') {
                sb.append("&#x9;");
            }
            else if (char1 == '\n') {
                sb.append("&#xA;");
            }
            else if (char1 == '\r') {
                sb.append("&#xD;");
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    static {
        fQNameDV = (XSSimpleType)SchemaGrammar.getS4SGrammar((short)1).getGlobalTypeDecl("QName");
    }
    
    static final class FacetInfo
    {
        final XSFacets facetdata;
        final Element nodeAfterFacets;
        final short fPresentFacets;
        final short fFixedFacets;
        
        FacetInfo(final XSFacets facetdata, final Element nodeAfterFacets, final short fPresentFacets, final short fFixedFacets) {
            this.facetdata = facetdata;
            this.nodeAfterFacets = nodeAfterFacets;
            this.fPresentFacets = fPresentFacets;
            this.fFixedFacets = fFixedFacets;
        }
    }
}
