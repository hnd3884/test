package org.apache.xerces.impl.dtd;

import org.apache.xerces.util.XMLChar;
import java.util.Iterator;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import java.io.IOException;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLComponent;

public class XMLDTDValidator implements XMLComponent, XMLDocumentFilter, XMLDTDValidatorFilter, RevalidationHandler
{
    private static final int TOP_LEVEL_SCOPE = -1;
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    private static final boolean DEBUG_ATTRIBUTES = false;
    private static final boolean DEBUG_ELEMENT_CHILDREN = false;
    protected ValidationManager fValidationManager;
    protected final ValidationState fValidationState;
    protected boolean fNamespaces;
    protected boolean fValidation;
    protected boolean fDTDValidation;
    protected boolean fDynamicValidation;
    protected boolean fBalanceSyntaxTrees;
    protected boolean fWarnDuplicateAttdef;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLGrammarPool fGrammarPool;
    protected DTDGrammarBucket fGrammarBucket;
    protected XMLLocator fDocLocation;
    protected NamespaceContext fNamespaceContext;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected DTDGrammar fDTDGrammar;
    protected boolean fSeenDoctypeDecl;
    private boolean fPerformValidation;
    private String fSchemaType;
    private final QName fCurrentElement;
    private int fCurrentElementIndex;
    private int fCurrentContentSpecType;
    private final QName fRootElement;
    private boolean fInCDATASection;
    private int[] fElementIndexStack;
    private int[] fContentSpecTypeStack;
    private QName[] fElementQNamePartsStack;
    private QName[] fElementChildren;
    private int fElementChildrenLength;
    private int[] fElementChildrenOffsetStack;
    private int fElementDepth;
    private boolean fSeenRootElement;
    private boolean fInElementContent;
    private final XMLElementDecl fTempElementDecl;
    private final XMLAttributeDecl fTempAttDecl;
    private final XMLEntityDecl fEntityDecl;
    private final QName fTempQName;
    private final StringBuffer fBuffer;
    protected DatatypeValidator fValID;
    protected DatatypeValidator fValIDRef;
    protected DatatypeValidator fValIDRefs;
    protected DatatypeValidator fValENTITY;
    protected DatatypeValidator fValENTITIES;
    protected DatatypeValidator fValNMTOKEN;
    protected DatatypeValidator fValNMTOKENS;
    protected DatatypeValidator fValNOTATION;
    
    public XMLDTDValidator() {
        this.fValidationManager = null;
        this.fValidationState = new ValidationState();
        this.fNamespaceContext = null;
        this.fSeenDoctypeDecl = false;
        this.fCurrentElement = new QName();
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fRootElement = new QName();
        this.fInCDATASection = false;
        this.fElementIndexStack = new int[8];
        this.fContentSpecTypeStack = new int[8];
        this.fElementQNamePartsStack = new QName[8];
        this.fElementChildren = new QName[32];
        this.fElementChildrenLength = 0;
        this.fElementChildrenOffsetStack = new int[32];
        this.fElementDepth = -1;
        this.fSeenRootElement = false;
        this.fInElementContent = false;
        this.fTempElementDecl = new XMLElementDecl();
        this.fTempAttDecl = new XMLAttributeDecl();
        this.fEntityDecl = new XMLEntityDecl();
        this.fTempQName = new QName();
        this.fBuffer = new StringBuffer();
        for (int i = 0; i < this.fElementQNamePartsStack.length; ++i) {
            this.fElementQNamePartsStack[i] = new QName();
        }
        this.fGrammarBucket = new DTDGrammarBucket();
    }
    
    DTDGrammarBucket getGrammarBucket() {
        return this.fGrammarBucket;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        this.fDTDGrammar = null;
        this.fSeenDoctypeDecl = false;
        this.fInCDATASection = false;
        this.fSeenRootElement = false;
        this.fInElementContent = false;
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fRootElement.clear();
        this.fValidationState.resetIDTables();
        this.fGrammarBucket.clear();
        this.fElementDepth = -1;
        this.fElementChildrenLength = 0;
        boolean feature;
        try {
            feature = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings");
        }
        catch (final XMLConfigurationException ex) {
            feature = true;
        }
        if (!feature) {
            this.fValidationManager.addValidationState(this.fValidationState);
            return;
        }
        try {
            this.fNamespaces = xmlComponentManager.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final XMLConfigurationException ex2) {
            this.fNamespaces = true;
        }
        try {
            this.fValidation = xmlComponentManager.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final XMLConfigurationException ex3) {
            this.fValidation = false;
        }
        try {
            this.fDTDValidation = !xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema");
        }
        catch (final XMLConfigurationException ex4) {
            this.fDTDValidation = true;
        }
        try {
            this.fDynamicValidation = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/dynamic");
        }
        catch (final XMLConfigurationException ex5) {
            this.fDynamicValidation = false;
        }
        try {
            this.fBalanceSyntaxTrees = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/balance-syntax-trees");
        }
        catch (final XMLConfigurationException ex6) {
            this.fBalanceSyntaxTrees = false;
        }
        try {
            this.fWarnDuplicateAttdef = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef");
        }
        catch (final XMLConfigurationException ex7) {
            this.fWarnDuplicateAttdef = false;
        }
        try {
            this.fSchemaType = (String)xmlComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
        }
        catch (final XMLConfigurationException ex8) {
            this.fSchemaType = null;
        }
        (this.fValidationManager = (ValidationManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager")).addValidationState(this.fValidationState);
        this.fValidationState.setUsingNamespaces(this.fNamespaces);
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        try {
            this.fGrammarPool = (XMLGrammarPool)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException ex9) {
            this.fGrammarPool = null;
        }
        this.fDatatypeValidatorFactory = (DTDDVFactory)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/datatype-validator-factory");
        this.init();
    }
    
    public String[] getRecognizedFeatures() {
        return XMLDTDValidator.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
    }
    
    public String[] getRecognizedProperties() {
        return XMLDTDValidator.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLDTDValidator.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDTDValidator.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLDTDValidator.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLDTDValidator.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDTDValidator.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLDTDValidator.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDocumentSource(final XMLDocumentSource fDocumentSource) {
        this.fDocumentSource = fDocumentSource;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startDocument(final XMLLocator fDocLocation, final String s, final NamespaceContext fNamespaceContext, final Augmentations augmentations) throws XNIException {
        if (this.fGrammarPool != null) {
            final Grammar[] retrieveInitialGrammarSet = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/TR/REC-xml");
            for (int n = (retrieveInitialGrammarSet != null) ? retrieveInitialGrammarSet.length : 0, i = 0; i < n; ++i) {
                this.fGrammarBucket.putGrammar((DTDGrammar)retrieveInitialGrammarSet[i]);
            }
        }
        this.fDocLocation = fDocLocation;
        this.fNamespaceContext = fNamespaceContext;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(fDocLocation, s, fNamespaceContext, augmentations);
        }
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        this.fGrammarBucket.setStandalone(s3 != null && s3.equals("yes"));
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(s, s2, s3, augmentations);
        }
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        this.fSeenDoctypeDecl = true;
        this.fRootElement.setValues(null, s, s, null);
        String expandSystemId = null;
        try {
            expandSystemId = XMLEntityManager.expandSystemId(s3, this.fDocLocation.getExpandedSystemId(), false);
        }
        catch (final IOException ex) {}
        final XMLDTDDescription xmldtdDescription = new XMLDTDDescription(s2, s3, this.fDocLocation.getExpandedSystemId(), expandSystemId, s);
        this.fDTDGrammar = this.fGrammarBucket.getGrammar(xmldtdDescription);
        if (this.fDTDGrammar == null && this.fGrammarPool != null && (s3 != null || s2 != null)) {
            this.fDTDGrammar = (DTDGrammar)this.fGrammarPool.retrieveGrammar(xmldtdDescription);
        }
        if (this.fDTDGrammar == null) {
            if (!this.fBalanceSyntaxTrees) {
                this.fDTDGrammar = new DTDGrammar(this.fSymbolTable, xmldtdDescription);
            }
            else {
                this.fDTDGrammar = new BalancedDTDGrammar(this.fSymbolTable, xmldtdDescription);
            }
        }
        else {
            this.fValidationManager.setCachedDTD(true);
        }
        this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(s, s2, s3, augmentations);
        }
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.handleStartElement(qName, xmlAttributes, augmentations);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(qName, xmlAttributes, augmentations);
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        final boolean handleStartElement = this.handleStartElement(qName, xmlAttributes, augmentations);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(qName, xmlAttributes, augmentations);
        }
        if (!handleStartElement) {
            this.handleEndElement(qName, augmentations, true);
        }
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        boolean b = true;
        boolean b2 = true;
        for (int i = xmlString.offset; i < xmlString.offset + xmlString.length; ++i) {
            if (!this.isSpace(xmlString.ch[i])) {
                b2 = false;
                break;
            }
        }
        if (this.fInElementContent && b2 && !this.fInCDATASection && this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(xmlString, augmentations);
            b = false;
        }
        if (this.fPerformValidation) {
            if (this.fInElementContent) {
                if (this.fGrammarBucket.getStandalone() && this.fDTDGrammar.getElementDeclIsExternal(this.fCurrentElementIndex) && b2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_WHITE_SPACE_IN_ELEMENT_CONTENT_WHEN_STANDALONE", null, (short)1);
                }
                if (!b2) {
                    this.charDataInContent();
                }
                if (augmentations != null && augmentations.getItem("CHAR_REF_PROBABLE_WS") == Boolean.TRUE) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, this.fDTDGrammar.getContentSpecAsString(this.fElementDepth), "character reference" }, (short)1);
                }
            }
            if (this.fCurrentContentSpecType == 1) {
                this.charDataInContent();
            }
        }
        if (b && this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(xmlString, augmentations);
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(xmlString, augmentations);
        }
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        this.handleEndElement(qName, augmentations, false);
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fInElementContent) {
            this.charDataInContent();
        }
        this.fInCDATASection = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        this.fInCDATASection = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augmentations);
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
            this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
            if (this.fTempElementDecl.type == 1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "comment" }, (short)1);
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(xmlString, augmentations);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
            this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
            if (this.fTempElementDecl.type == 1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "processing instruction" }, (short)1);
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(s, xmlString, augmentations);
        }
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
            this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
            if (this.fTempElementDecl.type == 1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[] { this.fCurrentElement.rawname, "EMPTY", "ENTITY" }, (short)1);
            }
            if (this.fGrammarBucket.getStandalone()) {
                XMLDTDProcessor.checkStandaloneEntityRef(s, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter);
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(s, augmentations);
        }
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(s, s2, augmentations);
        }
    }
    
    public final boolean hasGrammar() {
        return this.fDTDGrammar != null;
    }
    
    public final boolean validate() {
        return this.fSchemaType != Constants.NS_XMLSCHEMA && ((!this.fDynamicValidation && this.fValidation) || (this.fDynamicValidation && this.fSeenDoctypeDecl)) && (this.fDTDValidation || this.fSeenDoctypeDecl);
    }
    
    protected void addDTDDefaultAttrsAndValidate(final QName qName, final int n, final XMLAttributes xmlAttributes) throws XNIException {
        if (n == -1 || this.fDTDGrammar == null) {
            return;
        }
        for (int i = this.fDTDGrammar.getFirstAttributeDeclIndex(n); i != -1; i = this.fDTDGrammar.getNextAttributeDeclIndex(i)) {
            this.fDTDGrammar.getAttributeDecl(i, this.fTempAttDecl);
            String s = this.fTempAttDecl.name.prefix;
            String s2 = this.fTempAttDecl.name.localpart;
            final String rawname = this.fTempAttDecl.name.rawname;
            final String attributeTypeName = this.getAttributeTypeName(this.fTempAttDecl);
            final short defaultType = this.fTempAttDecl.simpleType.defaultType;
            String defaultValue = null;
            if (this.fTempAttDecl.simpleType.defaultValue != null) {
                defaultValue = this.fTempAttDecl.simpleType.defaultValue;
            }
            boolean b = false;
            final boolean b2 = defaultType == 2;
            if (attributeTypeName != XMLSymbols.fCDATASymbol || b2 || defaultValue != null) {
                for (int length = xmlAttributes.getLength(), j = 0; j < length; ++j) {
                    if (xmlAttributes.getQName(j) == rawname) {
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                if (b2) {
                    if (this.fPerformValidation) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REQUIRED_ATTRIBUTE_NOT_SPECIFIED", new Object[] { qName.localpart, rawname }, (short)1);
                    }
                }
                else if (defaultValue != null) {
                    if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && this.fDTDGrammar.getAttributeDeclIsExternal(i)) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DEFAULTED_ATTRIBUTE_NOT_SPECIFIED", new Object[] { qName.localpart, rawname }, (short)1);
                    }
                    if (this.fNamespaces) {
                        final int index = rawname.indexOf(58);
                        if (index != -1) {
                            s = this.fSymbolTable.addSymbol(rawname.substring(0, index));
                            s2 = this.fSymbolTable.addSymbol(rawname.substring(index + 1));
                        }
                    }
                    this.fTempQName.setValues(s, s2, rawname, this.fTempAttDecl.name.uri);
                    xmlAttributes.addAttribute(this.fTempQName, attributeTypeName, defaultValue);
                }
            }
        }
        for (int length2 = xmlAttributes.getLength(), k = 0; k < length2; ++k) {
            final String qName2 = xmlAttributes.getQName(k);
            boolean b3 = false;
            if (this.fPerformValidation && this.fGrammarBucket.getStandalone()) {
                final String nonNormalizedValue = xmlAttributes.getNonNormalizedValue(k);
                if (nonNormalizedValue != null) {
                    final String externalEntityRefInAttrValue = this.getExternalEntityRefInAttrValue(nonNormalizedValue);
                    if (externalEntityRefInAttrValue != null) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { externalEntityRefInAttrValue }, (short)1);
                    }
                }
            }
            int l;
            for (l = this.fDTDGrammar.getFirstAttributeDeclIndex(n); l != -1; l = this.fDTDGrammar.getNextAttributeDeclIndex(l)) {
                this.fDTDGrammar.getAttributeDecl(l, this.fTempAttDecl);
                if (this.fTempAttDecl.name.rawname == qName2) {
                    b3 = true;
                    break;
                }
            }
            if (!b3) {
                if (this.fPerformValidation) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_NOT_DECLARED", new Object[] { qName.rawname, qName2 }, (short)1);
                }
            }
            else {
                final String attributeTypeName2 = this.getAttributeTypeName(this.fTempAttDecl);
                xmlAttributes.setType(k, attributeTypeName2);
                xmlAttributes.getAugmentations(k).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
                String s4;
                final String s3 = s4 = xmlAttributes.getValue(k);
                if (xmlAttributes.isSpecified(k) && attributeTypeName2 != XMLSymbols.fCDATASymbol) {
                    final boolean normalizeAttrValue = this.normalizeAttrValue(xmlAttributes, k);
                    s4 = xmlAttributes.getValue(k);
                    if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && normalizeAttrValue && this.fDTDGrammar.getAttributeDeclIsExternal(l)) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTVALUE_CHANGED_DURING_NORMALIZATION_WHEN_STANDALONE", new Object[] { qName2, s3, s4 }, (short)1);
                    }
                }
                if (this.fPerformValidation) {
                    if (this.fTempAttDecl.simpleType.defaultType == 1) {
                        final String defaultValue2 = this.fTempAttDecl.simpleType.defaultValue;
                        if (!s4.equals(defaultValue2)) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_FIXED_ATTVALUE_INVALID", new Object[] { qName.localpart, qName2, s4, defaultValue2 }, (short)1);
                        }
                    }
                    if (this.fTempAttDecl.simpleType.type == 1 || this.fTempAttDecl.simpleType.type == 2 || this.fTempAttDecl.simpleType.type == 3 || this.fTempAttDecl.simpleType.type == 4 || this.fTempAttDecl.simpleType.type == 5 || this.fTempAttDecl.simpleType.type == 6) {
                        this.validateDTDattribute(qName, s4, this.fTempAttDecl);
                    }
                }
            }
        }
    }
    
    protected String getExternalEntityRefInAttrValue(final String s) {
        final int length = s.length();
        for (int i = s.indexOf(38); i != -1; i = s.indexOf(38, i + 1)) {
            if (i + 1 < length && s.charAt(i + 1) != '#') {
                String s2 = this.fSymbolTable.addSymbol(s.substring(i + 1, s.indexOf(59, i + 1)));
                final int entityDeclIndex = this.fDTDGrammar.getEntityDeclIndex(s2);
                if (entityDeclIndex > -1) {
                    this.fDTDGrammar.getEntityDecl(entityDeclIndex, this.fEntityDecl);
                    if (this.fEntityDecl.inExternal || (s2 = this.getExternalEntityRefInAttrValue(this.fEntityDecl.value)) != null) {
                        return s2;
                    }
                }
            }
        }
        return null;
    }
    
    protected void validateDTDattribute(final QName qName, final String s, final XMLAttributeDecl xmlAttributeDecl) throws XNIException {
        switch (xmlAttributeDecl.simpleType.type) {
            case 1: {
                final boolean list = xmlAttributeDecl.simpleType.list;
                try {
                    if (list) {
                        this.fValENTITIES.validate(s, this.fValidationState);
                    }
                    else {
                        this.fValENTITY.validate(s, this.fValidationState);
                    }
                }
                catch (final InvalidDatatypeValueException ex) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", ex.getKey(), ex.getArgs(), (short)1);
                }
                break;
            }
            case 2:
            case 6: {
                boolean b = false;
                final String[] enumeration = xmlAttributeDecl.simpleType.enumeration;
                if (enumeration == null) {
                    b = false;
                }
                else {
                    for (int i = 0; i < enumeration.length; ++i) {
                        if (s == enumeration[i] || s.equals(enumeration[i])) {
                            b = true;
                            break;
                        }
                    }
                }
                if (!b) {
                    final StringBuffer sb = new StringBuffer();
                    if (enumeration != null) {
                        for (int j = 0; j < enumeration.length; ++j) {
                            sb.append(enumeration[j] + " ");
                        }
                    }
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_VALUE_NOT_IN_LIST", new Object[] { xmlAttributeDecl.name.rawname, s, sb }, (short)1);
                    break;
                }
                break;
            }
            case 3: {
                try {
                    this.fValID.validate(s, this.fValidationState);
                }
                catch (final InvalidDatatypeValueException ex2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", ex2.getKey(), ex2.getArgs(), (short)1);
                }
                break;
            }
            case 4: {
                final boolean list2 = xmlAttributeDecl.simpleType.list;
                try {
                    if (list2) {
                        this.fValIDRefs.validate(s, this.fValidationState);
                    }
                    else {
                        this.fValIDRef.validate(s, this.fValidationState);
                    }
                }
                catch (final InvalidDatatypeValueException ex3) {
                    if (list2) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDREFSInvalid", new Object[] { s }, (short)1);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", ex3.getKey(), ex3.getArgs(), (short)1);
                    }
                }
                break;
            }
            case 5: {
                final boolean list3 = xmlAttributeDecl.simpleType.list;
                try {
                    if (list3) {
                        this.fValNMTOKENS.validate(s, this.fValidationState);
                    }
                    else {
                        this.fValNMTOKEN.validate(s, this.fValidationState);
                    }
                }
                catch (final InvalidDatatypeValueException ex4) {
                    if (list3) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NMTOKENSInvalid", new Object[] { s }, (short)1);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NMTOKENInvalid", new Object[] { s }, (short)1);
                    }
                }
                break;
            }
        }
    }
    
    protected boolean invalidStandaloneAttDef(final QName qName, final QName qName2) {
        return true;
    }
    
    private boolean normalizeAttrValue(final XMLAttributes xmlAttributes, final int n) {
        int n2 = 1;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        final String value = xmlAttributes.getValue(n);
        final char[] array = new char[value.length()];
        this.fBuffer.setLength(0);
        value.getChars(0, value.length(), array, 0);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == ' ') {
                if (n4 != 0) {
                    n3 = 1;
                    n4 = 0;
                }
                if (n3 != 0 && n2 == 0) {
                    n3 = 0;
                    this.fBuffer.append(array[i]);
                    ++n5;
                }
                else if (n2 != 0 || n3 == 0) {
                    ++n6;
                }
            }
            else {
                n4 = 1;
                n3 = 0;
                n2 = 0;
                this.fBuffer.append(array[i]);
                ++n5;
            }
        }
        if (n5 > 0 && this.fBuffer.charAt(n5 - 1) == ' ') {
            this.fBuffer.setLength(n5 - 1);
        }
        final String string = this.fBuffer.toString();
        xmlAttributes.setValue(n, string);
        return !value.equals(string);
    }
    
    private final void rootElementSpecified(final QName qName) throws XNIException {
        if (this.fPerformValidation) {
            final String rawname = this.fRootElement.rawname;
            final String rawname2 = qName.rawname;
            if (rawname == null || !rawname.equals(rawname2)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { rawname, rawname2 }, (short)1);
            }
        }
    }
    
    private int checkContent(final int n, final QName[] array, final int n2, final int n3) throws XNIException {
        this.fDTDGrammar.getElementDecl(n, this.fTempElementDecl);
        final String rawname = this.fCurrentElement.rawname;
        final int fCurrentContentSpecType = this.fCurrentContentSpecType;
        if (fCurrentContentSpecType == 1) {
            if (n3 != 0) {
                return 0;
            }
        }
        else if (fCurrentContentSpecType != 0) {
            if (fCurrentContentSpecType == 2 || fCurrentContentSpecType == 3) {
                return this.fTempElementDecl.contentModelValidator.validate(array, n2, n3);
            }
            if (fCurrentContentSpecType != -1) {
                if (fCurrentContentSpecType == 4) {}
            }
        }
        return -1;
    }
    
    private int getContentSpecType(final int n) {
        int type = -1;
        if (n > -1 && this.fDTDGrammar.getElementDecl(n, this.fTempElementDecl)) {
            type = this.fTempElementDecl.type;
        }
        return type;
    }
    
    private void charDataInContent() {
        if (this.fElementChildren.length <= this.fElementChildrenLength) {
            final QName[] fElementChildren = new QName[this.fElementChildren.length * 2];
            System.arraycopy(this.fElementChildren, 0, fElementChildren, 0, this.fElementChildren.length);
            this.fElementChildren = fElementChildren;
        }
        QName qName = this.fElementChildren[this.fElementChildrenLength];
        if (qName == null) {
            for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; ++i) {
                this.fElementChildren[i] = new QName();
            }
            qName = this.fElementChildren[this.fElementChildrenLength];
        }
        qName.clear();
        ++this.fElementChildrenLength;
    }
    
    private String getAttributeTypeName(final XMLAttributeDecl xmlAttributeDecl) {
        switch (xmlAttributeDecl.simpleType.type) {
            case 1: {
                return xmlAttributeDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
            }
            case 2: {
                final StringBuffer sb = new StringBuffer();
                sb.append('(');
                for (int i = 0; i < xmlAttributeDecl.simpleType.enumeration.length; ++i) {
                    if (i > 0) {
                        sb.append('|');
                    }
                    sb.append(xmlAttributeDecl.simpleType.enumeration[i]);
                }
                sb.append(')');
                return this.fSymbolTable.addSymbol(sb.toString());
            }
            case 3: {
                return XMLSymbols.fIDSymbol;
            }
            case 4: {
                return xmlAttributeDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
            }
            case 5: {
                return xmlAttributeDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
            }
            case 6: {
                return XMLSymbols.fNOTATIONSymbol;
            }
            default: {
                return XMLSymbols.fCDATASymbol;
            }
        }
    }
    
    protected void init() {
        if (!this.fValidation) {
            if (!this.fDynamicValidation) {
                return;
            }
        }
        try {
            this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDSymbol);
            this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSymbol);
            this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSSymbol);
            this.fValENTITY = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITYSymbol);
            this.fValENTITIES = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITIESSymbol);
            this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSymbol);
            this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSSymbol);
            this.fValNOTATION = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNOTATIONSymbol);
        }
        catch (final Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    private void ensureStackCapacity(final int n) {
        if (n == this.fElementQNamePartsStack.length) {
            final QName[] fElementQNamePartsStack = new QName[n * 2];
            System.arraycopy(this.fElementQNamePartsStack, 0, fElementQNamePartsStack, 0, n);
            this.fElementQNamePartsStack = fElementQNamePartsStack;
            if (this.fElementQNamePartsStack[n] == null) {
                for (int i = n; i < this.fElementQNamePartsStack.length; ++i) {
                    this.fElementQNamePartsStack[i] = new QName();
                }
            }
            final int[] fElementIndexStack = new int[n * 2];
            System.arraycopy(this.fElementIndexStack, 0, fElementIndexStack, 0, n);
            this.fElementIndexStack = fElementIndexStack;
            final int[] fContentSpecTypeStack = new int[n * 2];
            System.arraycopy(this.fContentSpecTypeStack, 0, fContentSpecTypeStack, 0, n);
            this.fContentSpecTypeStack = fContentSpecTypeStack;
        }
    }
    
    protected boolean handleStartElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (!this.fSeenRootElement) {
            this.fPerformValidation = this.validate();
            this.fSeenRootElement = true;
            this.fValidationManager.setEntityState(this.fDTDGrammar);
            this.fValidationManager.setGrammarFound(this.fSeenDoctypeDecl);
            this.rootElementSpecified(qName);
        }
        if (this.fDTDGrammar == null) {
            if (!this.fPerformValidation) {
                this.fCurrentElementIndex = -1;
                this.fCurrentContentSpecType = -1;
                this.fInElementContent = false;
            }
            if (this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { qName.rawname }, (short)1);
            }
            if (this.fDocumentSource != null) {
                this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
                if (this.fDocumentHandler != null) {
                    this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
                }
                return true;
            }
        }
        else {
            this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(qName);
            this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
            if (this.fCurrentContentSpecType == -1 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_NOT_DECLARED", new Object[] { qName.rawname }, (short)1);
            }
            this.addDTDDefaultAttrsAndValidate(qName, this.fCurrentElementIndex, xmlAttributes);
        }
        this.fInElementContent = (this.fCurrentContentSpecType == 3);
        ++this.fElementDepth;
        if (this.fPerformValidation) {
            if (this.fElementChildrenOffsetStack.length <= this.fElementDepth) {
                final int[] fElementChildrenOffsetStack = new int[this.fElementChildrenOffsetStack.length * 2];
                System.arraycopy(this.fElementChildrenOffsetStack, 0, fElementChildrenOffsetStack, 0, this.fElementChildrenOffsetStack.length);
                this.fElementChildrenOffsetStack = fElementChildrenOffsetStack;
            }
            this.fElementChildrenOffsetStack[this.fElementDepth] = this.fElementChildrenLength;
            if (this.fElementChildren.length <= this.fElementChildrenLength) {
                final QName[] fElementChildren = new QName[this.fElementChildrenLength * 2];
                System.arraycopy(this.fElementChildren, 0, fElementChildren, 0, this.fElementChildren.length);
                this.fElementChildren = fElementChildren;
            }
            QName qName2 = this.fElementChildren[this.fElementChildrenLength];
            if (qName2 == null) {
                for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; ++i) {
                    this.fElementChildren[i] = new QName();
                }
                qName2 = this.fElementChildren[this.fElementChildrenLength];
            }
            qName2.setValues(qName);
            ++this.fElementChildrenLength;
        }
        this.fCurrentElement.setValues(qName);
        this.ensureStackCapacity(this.fElementDepth);
        this.fElementQNamePartsStack[this.fElementDepth].setValues(this.fCurrentElement);
        this.fElementIndexStack[this.fElementDepth] = this.fCurrentElementIndex;
        this.fContentSpecTypeStack[this.fElementDepth] = this.fCurrentContentSpecType;
        this.startNamespaceScope(qName, xmlAttributes, augmentations);
        return false;
    }
    
    protected void startNamespaceScope(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) {
    }
    
    protected void handleEndElement(final QName qName, final Augmentations augmentations, final boolean b) throws XNIException {
        --this.fElementDepth;
        if (this.fPerformValidation) {
            final int fCurrentElementIndex = this.fCurrentElementIndex;
            if (fCurrentElementIndex != -1 && this.fCurrentContentSpecType != -1) {
                final QName[] fElementChildren = this.fElementChildren;
                final int n = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
                final int n2 = this.fElementChildrenLength - n;
                final int checkContent = this.checkContent(fCurrentElementIndex, fElementChildren, n, n2);
                if (checkContent != -1) {
                    this.fDTDGrammar.getElementDecl(fCurrentElementIndex, this.fTempElementDecl);
                    if (this.fTempElementDecl.type == 1) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID", new Object[] { qName.rawname, "EMPTY" }, (short)1);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", (checkContent != n2) ? "MSG_CONTENT_INVALID" : "MSG_CONTENT_INCOMPLETE", new Object[] { qName.rawname, this.fDTDGrammar.getContentSpecAsString(fCurrentElementIndex) }, (short)1);
                    }
                }
            }
            this.fElementChildrenLength = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
        }
        this.endNamespaceScope(this.fCurrentElement, augmentations, b);
        if (this.fElementDepth < -1) {
            throw new RuntimeException("FWK008 Element stack underflow");
        }
        if (this.fElementDepth < 0) {
            this.fCurrentElement.clear();
            this.fCurrentElementIndex = -1;
            this.fCurrentContentSpecType = -1;
            this.fInElementContent = false;
            if (this.fPerformValidation) {
                final Iterator checkIDRefID = this.fValidationState.checkIDRefID();
                if (checkIDRefID != null) {
                    while (checkIDRefID.hasNext()) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_WITH_ID_REQUIRED", new Object[] { checkIDRefID.next() }, (short)1);
                    }
                }
            }
            return;
        }
        this.fCurrentElement.setValues(this.fElementQNamePartsStack[this.fElementDepth]);
        this.fCurrentElementIndex = this.fElementIndexStack[this.fElementDepth];
        this.fCurrentContentSpecType = this.fContentSpecTypeStack[this.fElementDepth];
        this.fInElementContent = (this.fCurrentContentSpecType == 3);
    }
    
    protected void endNamespaceScope(final QName qName, final Augmentations augmentations, final boolean b) {
        if (this.fDocumentHandler != null && !b) {
            this.fDocumentHandler.endElement(this.fCurrentElement, augmentations);
        }
    }
    
    protected boolean isSpace(final int n) {
        return XMLChar.isSpace(n);
    }
    
    public boolean characterData(final String s, final Augmentations augmentations) {
        this.characters(new XMLString(s.toCharArray(), 0, s.length()), augmentations);
        return true;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/balance-syntax-trees" };
        FEATURE_DEFAULTS = new Boolean[] { null, null, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null, null };
    }
}
