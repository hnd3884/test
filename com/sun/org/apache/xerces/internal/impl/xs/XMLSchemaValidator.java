package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.Iterator;
import java.util.Stack;
import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.util.IntStack;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import java.util.Collections;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.impl.xs.identity.ValueStore;
import com.sun.org.apache.xerces.internal.impl.xs.identity.XPathMatcher;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import java.util.Map;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.xs.identity.FieldActivator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

public class XMLSchemaValidator implements XMLComponent, XMLDocumentFilter, FieldActivator, RevalidationHandler
{
    private static final boolean DEBUG = false;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
    public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    protected static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected static final int ID_CONSTRAINT_NUM = 1;
    protected ElementPSVImpl fCurrentPSVI;
    protected final AugmentationsImpl fAugmentations;
    protected final HashMap fMayMatchFieldMap;
    protected XMLString fDefaultValue;
    protected boolean fDynamicValidation;
    protected boolean fSchemaDynamicValidation;
    protected boolean fDoValidation;
    protected boolean fFullChecking;
    protected boolean fNormalizeData;
    protected boolean fSchemaElementDefault;
    protected boolean fAugPSVI;
    protected boolean fIdConstraint;
    protected boolean fUseGrammarPoolOnly;
    protected boolean fNamespaceGrowth;
    private String fSchemaType;
    protected boolean fEntityRef;
    protected boolean fInCDATA;
    protected boolean fSawOnlyWhitespaceInElementContent;
    protected SymbolTable fSymbolTable;
    private XMLLocator fLocator;
    protected final XSIErrorReporter fXSIErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected ValidationManager fValidationManager;
    protected ValidationState fValidationState;
    protected XMLGrammarPool fGrammarPool;
    protected String fExternalSchemas;
    protected String fExternalNoNamespaceSchema;
    protected Object fJaxpSchemaSource;
    protected final XSDDescription fXSDDescription;
    protected final Map<String, XMLSchemaLoader.LocationArray> fLocationPairs;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    boolean reportWhitespace;
    static final int INITIAL_STACK_SIZE = 8;
    static final int INC_STACK_SIZE = 8;
    private static final boolean DEBUG_NORMALIZATION = false;
    private final XMLString fEmptyXMLStr;
    private static final int BUFFER_SIZE = 20;
    private final XMLString fNormalizedStr;
    private boolean fFirstChunk;
    private boolean fTrailing;
    private short fWhiteSpace;
    private boolean fUnionType;
    private final XSGrammarBucket fGrammarBucket;
    private final SubstitutionGroupHandler fSubGroupHandler;
    private final XSSimpleType fQNameDV;
    private final CMNodeFactory nodeFactory;
    private final CMBuilder fCMBuilder;
    private final XMLSchemaLoader fSchemaLoader;
    private String fValidationRoot;
    private int fSkipValidationDepth;
    private int fNFullValidationDepth;
    private int fNNoneValidationDepth;
    private int fElementDepth;
    private boolean fSubElement;
    private boolean[] fSubElementStack;
    private XSElementDecl fCurrentElemDecl;
    private XSElementDecl[] fElemDeclStack;
    private boolean fNil;
    private boolean[] fNilStack;
    private XSNotationDecl fNotation;
    private XSNotationDecl[] fNotationStack;
    private XSTypeDefinition fCurrentType;
    private XSTypeDefinition[] fTypeStack;
    private XSCMValidator fCurrentCM;
    private XSCMValidator[] fCMStack;
    private int[] fCurrCMState;
    private int[][] fCMStateStack;
    private boolean fStrictAssess;
    private boolean[] fStrictAssessStack;
    private final StringBuffer fBuffer;
    private boolean fAppendBuffer;
    private boolean fSawText;
    private boolean[] fSawTextStack;
    private boolean fSawCharacters;
    private boolean[] fStringContent;
    private final QName fTempQName;
    private ValidatedInfo fValidatedInfo;
    private ValidationState fState4XsiType;
    private ValidationState fState4ApplyDefault;
    protected XPathMatcherStack fMatcherStack;
    protected ValueStoreCache fValueStoreCache;
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLSchemaValidator.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLSchemaValidator.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLSchemaValidator.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLSchemaValidator.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLSchemaValidator.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLSchemaValidator.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLSchemaValidator.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLSchemaValidator.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
        if (documentHandler instanceof XMLParser) {
            try {
                this.reportWhitespace = ((XMLParser)documentHandler).getFeature("http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace");
            }
            catch (final Exception e) {
                this.reportWhitespace = false;
            }
        }
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.fValidationState.setNamespaceSupport(namespaceContext);
        this.fState4XsiType.setNamespaceSupport(namespaceContext);
        this.fState4ApplyDefault.setNamespaceSupport(namespaceContext);
        this.handleStartDocument(this.fLocator = locator, encoding);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
        }
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        final Augmentations modifiedAugs = this.handleStartElement(element, attributes, augs);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(element, attributes, modifiedAugs);
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        Augmentations modifiedAugs = this.handleStartElement(element, attributes, augs);
        this.fDefaultValue = null;
        if (this.fElementDepth != -2) {
            modifiedAugs = this.handleEndElement(element, modifiedAugs);
        }
        if (this.fDocumentHandler != null) {
            if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
                this.fDocumentHandler.emptyElement(element, attributes, modifiedAugs);
            }
            else {
                this.fDocumentHandler.startElement(element, attributes, modifiedAugs);
                this.fDocumentHandler.characters(this.fDefaultValue, null);
                this.fDocumentHandler.endElement(element, modifiedAugs);
            }
        }
    }
    
    @Override
    public void characters(XMLString text, final Augmentations augs) throws XNIException {
        text = this.handleCharacters(text);
        if (this.fSawOnlyWhitespaceInElementContent) {
            this.fSawOnlyWhitespaceInElementContent = false;
            if (!this.reportWhitespace) {
                this.ignorableWhitespace(text, augs);
                return;
            }
        }
        if (this.fDocumentHandler != null) {
            if (this.fNormalizeData && this.fUnionType) {
                if (augs != null) {
                    this.fDocumentHandler.characters(this.fEmptyXMLStr, augs);
                }
            }
            else {
                this.fDocumentHandler.characters(text, augs);
            }
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.handleIgnorableWhitespace(text);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        this.fDefaultValue = null;
        final Augmentations modifiedAugs = this.handleEndElement(element, augs);
        if (this.fDocumentHandler != null) {
            if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
                this.fDocumentHandler.endElement(element, modifiedAugs);
            }
            else {
                this.fDocumentHandler.characters(this.fDefaultValue, null);
                this.fDocumentHandler.endElement(element, modifiedAugs);
            }
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATA = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATA = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        this.handleEndDocument();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
        }
        this.fLocator = null;
    }
    
    @Override
    public boolean characterData(final String data, final Augmentations augs) {
        this.fSawText = (this.fSawText || data.length() > 0);
        if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(data, this.fWhiteSpace == 2);
            this.fBuffer.append(this.fNormalizedStr.ch, this.fNormalizedStr.offset, this.fNormalizedStr.length);
        }
        else if (this.fAppendBuffer) {
            this.fBuffer.append(data);
        }
        boolean allWhiteSpace = true;
        if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
            final XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
            if (ctype.fContentType == 2) {
                for (int i = 0; i < data.length(); ++i) {
                    if (!XMLChar.isSpace(data.charAt(i))) {
                        allWhiteSpace = false;
                        this.fSawCharacters = true;
                        break;
                    }
                }
            }
        }
        return allWhiteSpace;
    }
    
    public void elementDefault(final String data) {
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        this.fEntityRef = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(text, augs);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        this.fEntityRef = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    public XMLSchemaValidator() {
        this.fCurrentPSVI = new ElementPSVImpl();
        this.fAugmentations = new AugmentationsImpl();
        this.fMayMatchFieldMap = new HashMap();
        this.fDynamicValidation = false;
        this.fSchemaDynamicValidation = false;
        this.fDoValidation = false;
        this.fFullChecking = false;
        this.fNormalizeData = true;
        this.fSchemaElementDefault = true;
        this.fAugPSVI = true;
        this.fIdConstraint = false;
        this.fUseGrammarPoolOnly = false;
        this.fNamespaceGrowth = false;
        this.fSchemaType = null;
        this.fEntityRef = false;
        this.fInCDATA = false;
        this.fSawOnlyWhitespaceInElementContent = false;
        this.fXSIErrorReporter = new XSIErrorReporter();
        this.fValidationManager = null;
        this.fValidationState = new ValidationState();
        this.fExternalSchemas = null;
        this.fExternalNoNamespaceSchema = null;
        this.fJaxpSchemaSource = null;
        this.fXSDDescription = new XSDDescription();
        this.fLocationPairs = new HashMap<String, XMLSchemaLoader.LocationArray>();
        this.reportWhitespace = false;
        this.fEmptyXMLStr = new XMLString(null, 0, -1);
        this.fNormalizedStr = new XMLString();
        this.fFirstChunk = true;
        this.fTrailing = false;
        this.fWhiteSpace = -1;
        this.fUnionType = false;
        this.fGrammarBucket = new XSGrammarBucket();
        this.fSubGroupHandler = new SubstitutionGroupHandler(this.fGrammarBucket);
        this.fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
        this.nodeFactory = new CMNodeFactory();
        this.fCMBuilder = new CMBuilder(this.nodeFactory);
        this.fSchemaLoader = new XMLSchemaLoader(this.fXSIErrorReporter.fErrorReporter, this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder);
        this.fSubElementStack = new boolean[8];
        this.fElemDeclStack = new XSElementDecl[8];
        this.fNilStack = new boolean[8];
        this.fNotationStack = new XSNotationDecl[8];
        this.fTypeStack = new XSTypeDefinition[8];
        this.fCMStack = new XSCMValidator[8];
        this.fCMStateStack = new int[8][];
        this.fStrictAssess = true;
        this.fStrictAssessStack = new boolean[8];
        this.fBuffer = new StringBuffer();
        this.fAppendBuffer = true;
        this.fSawText = false;
        this.fSawTextStack = new boolean[8];
        this.fSawCharacters = false;
        this.fStringContent = new boolean[8];
        this.fTempQName = new QName();
        this.fValidatedInfo = new ValidatedInfo();
        this.fState4XsiType = new ValidationState();
        this.fState4ApplyDefault = new ValidationState();
        this.fMatcherStack = new XPathMatcherStack();
        this.fValueStoreCache = new ValueStoreCache();
        this.fState4XsiType.setExtraChecking(false);
        this.fState4ApplyDefault.setFacetChecking(false);
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        this.fIdConstraint = false;
        this.fLocationPairs.clear();
        this.fValidationState.resetIDTables();
        this.nodeFactory.reset(componentManager);
        this.fSchemaLoader.reset(componentManager);
        this.fCurrentElemDecl = null;
        this.fCurrentCM = null;
        this.fCurrCMState = null;
        this.fSkipValidationDepth = -1;
        this.fNFullValidationDepth = -1;
        this.fNNoneValidationDepth = -1;
        this.fElementDepth = -1;
        this.fSubElement = false;
        this.fSchemaDynamicValidation = false;
        this.fEntityRef = false;
        this.fInCDATA = false;
        this.fMatcherStack.clear();
        if (!this.fMayMatchFieldMap.isEmpty()) {
            this.fMayMatchFieldMap.clear();
        }
        this.fXSIErrorReporter.reset((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
        final boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
        if (!parser_settings) {
            this.fValidationManager.addValidationState(this.fValidationState);
            XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
            return;
        }
        final SymbolTable symbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        if (symbolTable != this.fSymbolTable) {
            this.fSymbolTable = symbolTable;
        }
        this.fNamespaceGrowth = componentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
        this.fDynamicValidation = componentManager.getFeature("http://apache.org/xml/features/validation/dynamic", false);
        if (this.fDynamicValidation) {
            this.fDoValidation = true;
        }
        else {
            this.fDoValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
        }
        if (this.fDoValidation) {
            this.fDoValidation |= componentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
        }
        this.fFullChecking = componentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
        this.fNormalizeData = componentManager.getFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
        this.fSchemaElementDefault = componentManager.getFeature("http://apache.org/xml/features/validation/schema/element-default", false);
        this.fAugPSVI = componentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
        this.fSchemaType = (String)componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
        this.fUseGrammarPoolOnly = componentManager.getFeature("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", false);
        this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        (this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager")).addValidationState(this.fValidationState);
        this.fValidationState.setSymbolTable(this.fSymbolTable);
        try {
            this.fExternalSchemas = (String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
            this.fExternalNoNamespaceSchema = (String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
        }
        catch (final XMLConfigurationException e) {
            this.fExternalSchemas = null;
            this.fExternalNoNamespaceSchema = null;
        }
        XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
        this.fJaxpSchemaSource = componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
        this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
        this.fState4XsiType.setSymbolTable(symbolTable);
        this.fState4ApplyDefault.setSymbolTable(symbolTable);
    }
    
    @Override
    public void startValueScopeFor(final IdentityConstraint identityConstraint, final int initialDepth) {
        final ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
        valueStore.startValueScope();
    }
    
    @Override
    public XPathMatcher activateField(final Field field, final int initialDepth) {
        final ValueStore valueStore = this.fValueStoreCache.getValueStoreFor(field.getIdentityConstraint(), initialDepth);
        this.setMayMatch(field, Boolean.TRUE);
        final XPathMatcher matcher = field.createMatcher(this, valueStore);
        this.fMatcherStack.addMatcher(matcher);
        matcher.startDocumentFragment();
        return matcher;
    }
    
    @Override
    public void endValueScopeFor(final IdentityConstraint identityConstraint, final int initialDepth) {
        final ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
        valueStore.endValueScope();
    }
    
    @Override
    public void setMayMatch(final Field field, final Boolean state) {
        this.fMayMatchFieldMap.put(field, state);
    }
    
    @Override
    public Boolean mayMatch(final Field field) {
        return this.fMayMatchFieldMap.get(field);
    }
    
    private void activateSelectorFor(final IdentityConstraint ic) {
        final Selector selector = ic.getSelector();
        final FieldActivator activator = this;
        if (selector == null) {
            return;
        }
        final XPathMatcher matcher = selector.createMatcher(activator, this.fElementDepth);
        this.fMatcherStack.addMatcher(matcher);
        matcher.startDocumentFragment();
    }
    
    void ensureStackCapacity() {
        if (this.fElementDepth == this.fElemDeclStack.length) {
            final int newSize = this.fElementDepth + 8;
            boolean[] newArrayB = new boolean[newSize];
            System.arraycopy(this.fSubElementStack, 0, newArrayB, 0, this.fElementDepth);
            this.fSubElementStack = newArrayB;
            final XSElementDecl[] newArrayE = new XSElementDecl[newSize];
            System.arraycopy(this.fElemDeclStack, 0, newArrayE, 0, this.fElementDepth);
            this.fElemDeclStack = newArrayE;
            newArrayB = new boolean[newSize];
            System.arraycopy(this.fNilStack, 0, newArrayB, 0, this.fElementDepth);
            this.fNilStack = newArrayB;
            final XSNotationDecl[] newArrayN = new XSNotationDecl[newSize];
            System.arraycopy(this.fNotationStack, 0, newArrayN, 0, this.fElementDepth);
            this.fNotationStack = newArrayN;
            final XSTypeDefinition[] newArrayT = new XSTypeDefinition[newSize];
            System.arraycopy(this.fTypeStack, 0, newArrayT, 0, this.fElementDepth);
            this.fTypeStack = newArrayT;
            final XSCMValidator[] newArrayC = new XSCMValidator[newSize];
            System.arraycopy(this.fCMStack, 0, newArrayC, 0, this.fElementDepth);
            this.fCMStack = newArrayC;
            newArrayB = new boolean[newSize];
            System.arraycopy(this.fSawTextStack, 0, newArrayB, 0, this.fElementDepth);
            this.fSawTextStack = newArrayB;
            newArrayB = new boolean[newSize];
            System.arraycopy(this.fStringContent, 0, newArrayB, 0, this.fElementDepth);
            this.fStringContent = newArrayB;
            newArrayB = new boolean[newSize];
            System.arraycopy(this.fStrictAssessStack, 0, newArrayB, 0, this.fElementDepth);
            this.fStrictAssessStack = newArrayB;
            final int[][] newArrayIA = new int[newSize][];
            System.arraycopy(this.fCMStateStack, 0, newArrayIA, 0, this.fElementDepth);
            this.fCMStateStack = newArrayIA;
        }
    }
    
    void handleStartDocument(final XMLLocator locator, final String encoding) {
        this.fValueStoreCache.startDocument();
        if (this.fAugPSVI) {
            this.fCurrentPSVI.fGrammars = null;
            this.fCurrentPSVI.fSchemaInformation = null;
        }
    }
    
    void handleEndDocument() {
        this.fValueStoreCache.endDocument();
    }
    
    XMLString handleCharacters(XMLString text) {
        if (this.fSkipValidationDepth >= 0) {
            return text;
        }
        this.fSawText = (this.fSawText || text.length > 0);
        if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(text, this.fWhiteSpace == 2);
            text = this.fNormalizedStr;
        }
        if (this.fAppendBuffer) {
            this.fBuffer.append(text.ch, text.offset, text.length);
        }
        this.fSawOnlyWhitespaceInElementContent = false;
        if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
            final XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
            if (ctype.fContentType == 2) {
                for (int i = text.offset; i < text.offset + text.length; ++i) {
                    if (!XMLChar.isSpace(text.ch[i])) {
                        this.fSawCharacters = true;
                        break;
                    }
                    this.fSawOnlyWhitespaceInElementContent = !this.fSawCharacters;
                }
            }
        }
        return text;
    }
    
    private void normalizeWhitespace(final XMLString value, final boolean collapse) {
        boolean skipSpace = collapse;
        boolean sawNonWS = false;
        boolean leading = false;
        boolean trailing = false;
        final int size = value.offset + value.length;
        if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < value.length + 1) {
            this.fNormalizedStr.ch = new char[value.length + 1];
        }
        this.fNormalizedStr.offset = 1;
        this.fNormalizedStr.length = 1;
        for (int i = value.offset; i < size; ++i) {
            final char c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                if (!skipSpace) {
                    this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
                    skipSpace = collapse;
                }
                if (!sawNonWS) {
                    leading = true;
                }
            }
            else {
                this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
                skipSpace = false;
                sawNonWS = true;
            }
        }
        if (skipSpace) {
            if (this.fNormalizedStr.length > 1) {
                final XMLString fNormalizedStr = this.fNormalizedStr;
                --fNormalizedStr.length;
                trailing = true;
            }
            else if (leading && !this.fFirstChunk) {
                trailing = true;
            }
        }
        if (this.fNormalizedStr.length > 1 && !this.fFirstChunk && this.fWhiteSpace == 2) {
            if (this.fTrailing) {
                this.fNormalizedStr.offset = 0;
                this.fNormalizedStr.ch[0] = ' ';
            }
            else if (leading) {
                this.fNormalizedStr.offset = 0;
                this.fNormalizedStr.ch[0] = ' ';
            }
        }
        final XMLString fNormalizedStr2 = this.fNormalizedStr;
        fNormalizedStr2.length -= this.fNormalizedStr.offset;
        if ((this.fTrailing = trailing) || sawNonWS) {
            this.fFirstChunk = false;
        }
    }
    
    private void normalizeWhitespace(final String value, final boolean collapse) {
        boolean skipSpace = collapse;
        final int size = value.length();
        if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < size) {
            this.fNormalizedStr.ch = new char[size];
        }
        this.fNormalizedStr.offset = 0;
        this.fNormalizedStr.length = 0;
        for (int i = 0; i < size; ++i) {
            final char c = value.charAt(i);
            if (XMLChar.isSpace(c)) {
                if (!skipSpace) {
                    this.fNormalizedStr.ch[this.fNormalizedStr.length++] = ' ';
                    skipSpace = collapse;
                }
            }
            else {
                this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
                skipSpace = false;
            }
        }
        if (skipSpace && this.fNormalizedStr.length != 0) {
            final XMLString fNormalizedStr = this.fNormalizedStr;
            --fNormalizedStr.length;
        }
    }
    
    void handleIgnorableWhitespace(final XMLString text) {
        if (this.fSkipValidationDepth >= 0) {
            return;
        }
    }
    
    Augmentations handleStartElement(final QName element, final XMLAttributes attributes, Augmentations augs) {
        if (this.fElementDepth == -1 && this.fValidationManager.isGrammarFound() && this.fSchemaType == null && !this.fUseGrammarPoolOnly) {
            this.fSchemaDynamicValidation = true;
        }
        final String sLocation = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION);
        final String nsLocation = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
        this.storeLocations(sLocation, nsLocation);
        if (this.fSkipValidationDepth >= 0) {
            ++this.fElementDepth;
            if (this.fAugPSVI) {
                augs = this.getEmptyAugs(augs);
            }
            return augs;
        }
        final SchemaGrammar sGrammar = this.findSchemaGrammar((short)5, element.uri, null, element, attributes);
        Object decl = null;
        if (this.fCurrentCM != null) {
            decl = this.fCurrentCM.oneTransition(element, this.fCurrCMState, this.fSubGroupHandler);
            if (this.fCurrCMState[0] == -1) {
                final XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
                final Vector next;
                if (ctype.fParticle != null && (next = this.fCurrentCM.whatCanGoHere(this.fCurrCMState)).size() > 0) {
                    final String expected = this.expectedStr(next);
                    this.reportSchemaError("cvc-complex-type.2.4.a", new Object[] { element.rawname, expected });
                }
                else {
                    this.reportSchemaError("cvc-complex-type.2.4.d", new Object[] { element.rawname });
                }
            }
        }
        if (this.fElementDepth != -1) {
            this.ensureStackCapacity();
            this.fSubElementStack[this.fElementDepth] = true;
            this.fSubElement = false;
            this.fElemDeclStack[this.fElementDepth] = this.fCurrentElemDecl;
            this.fNilStack[this.fElementDepth] = this.fNil;
            this.fNotationStack[this.fElementDepth] = this.fNotation;
            this.fTypeStack[this.fElementDepth] = this.fCurrentType;
            this.fStrictAssessStack[this.fElementDepth] = this.fStrictAssess;
            this.fCMStack[this.fElementDepth] = this.fCurrentCM;
            this.fCMStateStack[this.fElementDepth] = this.fCurrCMState;
            this.fSawTextStack[this.fElementDepth] = this.fSawText;
            this.fStringContent[this.fElementDepth] = this.fSawCharacters;
        }
        ++this.fElementDepth;
        this.fCurrentElemDecl = null;
        XSWildcardDecl wildcard = null;
        this.fCurrentType = null;
        this.fStrictAssess = true;
        this.fNil = false;
        this.fNotation = null;
        this.fBuffer.setLength(0);
        this.fSawText = false;
        this.fSawCharacters = false;
        if (decl != null) {
            if (decl instanceof XSElementDecl) {
                this.fCurrentElemDecl = (XSElementDecl)decl;
            }
            else {
                wildcard = (XSWildcardDecl)decl;
            }
        }
        if (wildcard != null && wildcard.fProcessContents == 2) {
            this.fSkipValidationDepth = this.fElementDepth;
            if (this.fAugPSVI) {
                augs = this.getEmptyAugs(augs);
            }
            return augs;
        }
        if (this.fCurrentElemDecl == null && sGrammar != null) {
            this.fCurrentElemDecl = sGrammar.getGlobalElementDecl(element.localpart);
        }
        if (this.fCurrentElemDecl != null) {
            this.fCurrentType = this.fCurrentElemDecl.fType;
        }
        final String xsiType = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);
        if (this.fCurrentType == null && xsiType == null) {
            if (this.fElementDepth == 0) {
                if (this.fDynamicValidation || this.fSchemaDynamicValidation) {
                    if (this.fDocumentSource != null) {
                        this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
                        if (this.fDocumentHandler != null) {
                            this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
                        }
                        this.fElementDepth = -2;
                        return augs;
                    }
                    this.fSkipValidationDepth = this.fElementDepth;
                    if (this.fAugPSVI) {
                        augs = this.getEmptyAugs(augs);
                    }
                    return augs;
                }
                else {
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-elt.1", new Object[] { element.rawname }, (short)1);
                }
            }
            else if (wildcard != null && wildcard.fProcessContents == 1) {
                this.reportSchemaError("cvc-complex-type.2.4.c", new Object[] { element.rawname });
            }
            this.fCurrentType = SchemaGrammar.fAnyType;
            this.fStrictAssess = false;
            this.fNFullValidationDepth = this.fElementDepth;
            this.fAppendBuffer = false;
            this.fXSIErrorReporter.pushContext();
        }
        else {
            this.fXSIErrorReporter.pushContext();
            if (xsiType != null) {
                final XSTypeDefinition oldType = this.fCurrentType;
                this.fCurrentType = this.getAndCheckXsiType(element, xsiType, attributes);
                if (this.fCurrentType == null) {
                    if (oldType == null) {
                        this.fCurrentType = SchemaGrammar.fAnyType;
                    }
                    else {
                        this.fCurrentType = oldType;
                    }
                }
            }
            this.fNNoneValidationDepth = this.fElementDepth;
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                this.fAppendBuffer = true;
            }
            else if (this.fCurrentType.getTypeCategory() == 16) {
                this.fAppendBuffer = true;
            }
            else {
                final XSComplexTypeDecl ctype2 = (XSComplexTypeDecl)this.fCurrentType;
                this.fAppendBuffer = (ctype2.fContentType == 1);
            }
        }
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getAbstract()) {
            this.reportSchemaError("cvc-elt.2", new Object[] { element.rawname });
        }
        if (this.fElementDepth == 0) {
            this.fValidationRoot = element.rawname;
        }
        if (this.fNormalizeData) {
            this.fFirstChunk = true;
            this.fTrailing = false;
            this.fUnionType = false;
            this.fWhiteSpace = -1;
        }
        if (this.fCurrentType.getTypeCategory() == 15) {
            final XSComplexTypeDecl ctype2 = (XSComplexTypeDecl)this.fCurrentType;
            if (ctype2.getAbstract()) {
                this.reportSchemaError("cvc-type.2", new Object[] { element.rawname });
            }
            if (this.fNormalizeData && ctype2.fContentType == 1) {
                if (ctype2.fXSSimpleType.getVariety() == 3) {
                    this.fUnionType = true;
                }
                else {
                    try {
                        this.fWhiteSpace = ctype2.fXSSimpleType.getWhitespace();
                    }
                    catch (final DatatypeException ex) {}
                }
            }
        }
        else if (this.fNormalizeData) {
            final XSSimpleType dv = (XSSimpleType)this.fCurrentType;
            if (dv.getVariety() == 3) {
                this.fUnionType = true;
            }
            else {
                try {
                    this.fWhiteSpace = dv.getWhitespace();
                }
                catch (final DatatypeException ex2) {}
            }
        }
        this.fCurrentCM = null;
        if (this.fCurrentType.getTypeCategory() == 15) {
            this.fCurrentCM = ((XSComplexTypeDecl)this.fCurrentType).getContentModel(this.fCMBuilder);
        }
        this.fCurrCMState = null;
        if (this.fCurrentCM != null) {
            this.fCurrCMState = this.fCurrentCM.startContentModel();
        }
        final String xsiNil = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL);
        if (xsiNil != null && this.fCurrentElemDecl != null) {
            this.fNil = this.getXsiNil(element, xsiNil);
        }
        XSAttributeGroupDecl attrGrp = null;
        if (this.fCurrentType.getTypeCategory() == 15) {
            final XSComplexTypeDecl ctype3 = (XSComplexTypeDecl)this.fCurrentType;
            attrGrp = ctype3.getAttrGrp();
        }
        this.fValueStoreCache.startElement();
        this.fMatcherStack.pushContext();
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fIDCPos > 0) {
            this.fIdConstraint = true;
            this.fValueStoreCache.initValueStoresFor(this.fCurrentElemDecl, this);
        }
        this.processAttributes(element, attributes, attrGrp);
        if (attrGrp != null) {
            this.addDefaultAttributes(element, attributes, attrGrp);
        }
        for (int count = this.fMatcherStack.getMatcherCount(), i = 0; i < count; ++i) {
            final XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
            matcher.startElement(element, attributes);
        }
        if (this.fAugPSVI) {
            augs = this.getEmptyAugs(augs);
            this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
            this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
            this.fCurrentPSVI.fNotation = this.fNotation;
        }
        return augs;
    }
    
    Augmentations handleEndElement(final QName element, Augmentations augs) {
        if (this.fSkipValidationDepth >= 0) {
            if (this.fSkipValidationDepth == this.fElementDepth && this.fSkipValidationDepth > 0) {
                this.fNFullValidationDepth = this.fSkipValidationDepth - 1;
                this.fSkipValidationDepth = -1;
                --this.fElementDepth;
                this.fSubElement = this.fSubElementStack[this.fElementDepth];
                this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
                this.fNil = this.fNilStack[this.fElementDepth];
                this.fNotation = this.fNotationStack[this.fElementDepth];
                this.fCurrentType = this.fTypeStack[this.fElementDepth];
                this.fCurrentCM = this.fCMStack[this.fElementDepth];
                this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
                this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
                this.fSawText = this.fSawTextStack[this.fElementDepth];
                this.fSawCharacters = this.fStringContent[this.fElementDepth];
            }
            else {
                --this.fElementDepth;
            }
            if (this.fElementDepth == -1 && this.fFullChecking) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }
            if (this.fAugPSVI) {
                augs = this.getEmptyAugs(augs);
            }
            return augs;
        }
        this.processElementContent(element);
        final int oldCount = this.fMatcherStack.getMatcherCount();
        for (int i = oldCount - 1; i >= 0; --i) {
            final XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
            if (this.fCurrentElemDecl == null) {
                matcher.endElement(element, null, false, this.fValidatedInfo.actualValue, this.fValidatedInfo.actualValueType, this.fValidatedInfo.itemValueTypes);
            }
            else {
                matcher.endElement(element, this.fCurrentType, this.fCurrentElemDecl.getNillable(), (this.fDefaultValue == null) ? this.fValidatedInfo.actualValue : this.fCurrentElemDecl.fDefault.actualValue, (this.fDefaultValue == null) ? this.fValidatedInfo.actualValueType : this.fCurrentElemDecl.fDefault.actualValueType, (this.fDefaultValue == null) ? this.fValidatedInfo.itemValueTypes : this.fCurrentElemDecl.fDefault.itemValueTypes);
            }
        }
        if (this.fMatcherStack.size() > 0) {
            this.fMatcherStack.popContext();
        }
        final int newCount = this.fMatcherStack.getMatcherCount();
        for (int j = oldCount - 1; j >= newCount; --j) {
            final XPathMatcher matcher2 = this.fMatcherStack.getMatcherAt(j);
            if (matcher2 instanceof Selector.Matcher) {
                final Selector.Matcher selMatcher = (Selector.Matcher)matcher2;
                final IdentityConstraint id;
                if ((id = selMatcher.getIdentityConstraint()) != null && id.getCategory() != 2) {
                    this.fValueStoreCache.transplant(id, selMatcher.getInitialDepth());
                }
            }
        }
        for (int j = oldCount - 1; j >= newCount; --j) {
            final XPathMatcher matcher2 = this.fMatcherStack.getMatcherAt(j);
            if (matcher2 instanceof Selector.Matcher) {
                final Selector.Matcher selMatcher = (Selector.Matcher)matcher2;
                final IdentityConstraint id;
                if ((id = selMatcher.getIdentityConstraint()) != null && id.getCategory() == 2) {
                    final ValueStoreBase values = this.fValueStoreCache.getValueStoreFor(id, selMatcher.getInitialDepth());
                    if (values != null) {
                        values.endDocumentFragment();
                    }
                }
            }
        }
        this.fValueStoreCache.endElement();
        SchemaGrammar[] grammars = null;
        if (this.fElementDepth == 0) {
            final String invIdRef = this.fValidationState.checkIDRefID();
            this.fValidationState.resetIDTables();
            if (invIdRef != null) {
                this.reportSchemaError("cvc-id.1", new Object[] { invIdRef });
            }
            if (this.fFullChecking) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }
            grammars = this.fGrammarBucket.getGrammars();
            if (this.fGrammarPool != null) {
                for (int k = 0; k < grammars.length; ++k) {
                    grammars[k].setImmutable(true);
                }
                this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", grammars);
            }
            augs = this.endElementPSVI(true, grammars, augs);
        }
        else {
            augs = this.endElementPSVI(false, grammars, augs);
            --this.fElementDepth;
            this.fSubElement = this.fSubElementStack[this.fElementDepth];
            this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
            this.fNil = this.fNilStack[this.fElementDepth];
            this.fNotation = this.fNotationStack[this.fElementDepth];
            this.fCurrentType = this.fTypeStack[this.fElementDepth];
            this.fCurrentCM = this.fCMStack[this.fElementDepth];
            this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
            this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
            this.fSawText = this.fSawTextStack[this.fElementDepth];
            this.fSawCharacters = this.fStringContent[this.fElementDepth];
            this.fWhiteSpace = -1;
            this.fAppendBuffer = false;
            this.fUnionType = false;
        }
        return augs;
    }
    
    final Augmentations endElementPSVI(final boolean root, final SchemaGrammar[] grammars, Augmentations augs) {
        if (this.fAugPSVI) {
            augs = this.getEmptyAugs(augs);
            this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
            this.fCurrentPSVI.fNotation = this.fNotation;
            this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
            if (this.fElementDepth > this.fNFullValidationDepth) {
                this.fCurrentPSVI.fValidationAttempted = 2;
            }
            else if (this.fElementDepth > this.fNNoneValidationDepth) {
                this.fCurrentPSVI.fValidationAttempted = 0;
            }
            else {
                this.fCurrentPSVI.fValidationAttempted = 1;
                final int n = this.fElementDepth - 1;
                this.fNNoneValidationDepth = n;
                this.fNFullValidationDepth = n;
            }
            if (this.fDefaultValue != null) {
                this.fCurrentPSVI.fSpecified = true;
            }
            this.fCurrentPSVI.fNil = this.fNil;
            this.fCurrentPSVI.fMemberType = this.fValidatedInfo.memberType;
            this.fCurrentPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
            this.fCurrentPSVI.fActualValue = this.fValidatedInfo.actualValue;
            this.fCurrentPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
            this.fCurrentPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
            if (this.fStrictAssess) {
                final String[] errors = this.fXSIErrorReporter.mergeContext();
                this.fCurrentPSVI.fErrorCodes = errors;
                this.fCurrentPSVI.fValidity = (short)((errors == null) ? 2 : 1);
            }
            else {
                this.fCurrentPSVI.fValidity = 0;
                this.fXSIErrorReporter.popContext();
            }
            if (root) {
                this.fCurrentPSVI.fGrammars = grammars;
                this.fCurrentPSVI.fSchemaInformation = null;
            }
        }
        return augs;
    }
    
    Augmentations getEmptyAugs(Augmentations augs) {
        if (augs == null) {
            augs = this.fAugmentations;
            augs.removeAllItems();
        }
        augs.putItem("ELEMENT_PSVI", this.fCurrentPSVI);
        this.fCurrentPSVI.reset();
        return augs;
    }
    
    void storeLocations(final String sLocation, final String nsLocation) {
        if (sLocation != null && !XMLSchemaLoader.tokenizeSchemaLocationStr(sLocation, this.fLocationPairs)) {
            this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { sLocation }, (short)0);
        }
        if (nsLocation != null) {
            XMLSchemaLoader.LocationArray la = this.fLocationPairs.get(XMLSymbols.EMPTY_STRING);
            if (la == null) {
                la = new XMLSchemaLoader.LocationArray();
                this.fLocationPairs.put(XMLSymbols.EMPTY_STRING, la);
            }
            la.addLocation(nsLocation);
        }
    }
    
    SchemaGrammar findSchemaGrammar(final short contextType, final String namespace, final QName enclosingElement, final QName triggeringComponet, final XMLAttributes attributes) {
        SchemaGrammar grammar = null;
        grammar = this.fGrammarBucket.getGrammar(namespace);
        if (grammar == null) {
            this.fXSDDescription.setNamespace(namespace);
            if (this.fGrammarPool != null) {
                grammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(this.fXSDDescription);
                if (grammar != null && !this.fGrammarBucket.putGrammar(grammar, true, this.fNamespaceGrowth)) {
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
                    grammar = null;
                }
            }
        }
        if ((grammar == null && !this.fUseGrammarPoolOnly) || this.fNamespaceGrowth) {
            this.fXSDDescription.reset();
            this.fXSDDescription.fContextType = contextType;
            this.fXSDDescription.setNamespace(namespace);
            this.fXSDDescription.fEnclosedElementName = enclosingElement;
            this.fXSDDescription.fTriggeringComponent = triggeringComponet;
            this.fXSDDescription.fAttributes = attributes;
            if (this.fLocator != null) {
                this.fXSDDescription.setBaseSystemId(this.fLocator.getExpandedSystemId());
            }
            Map<String, XMLSchemaLoader.LocationArray> locationPairs = this.fLocationPairs;
            final XMLSchemaLoader.LocationArray locationArray = locationPairs.get((namespace == null) ? XMLSymbols.EMPTY_STRING : namespace);
            if (locationArray != null) {
                final String[] temp = locationArray.getLocationArray();
                if (temp.length != 0) {
                    this.setLocationHints(this.fXSDDescription, temp, grammar);
                }
            }
            if (grammar == null || this.fXSDDescription.fLocationHints != null) {
                boolean toParseSchema = true;
                if (grammar != null) {
                    locationPairs = Collections.emptyMap();
                }
                try {
                    final XMLInputSource xis = XMLSchemaLoader.resolveDocument(this.fXSDDescription, locationPairs, this.fEntityResolver);
                    if (grammar != null && this.fNamespaceGrowth) {
                        try {
                            if (grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xis.getSystemId(), xis.getBaseSystemId(), false))) {
                                toParseSchema = false;
                            }
                        }
                        catch (final URI.MalformedURIException ex2) {}
                    }
                    if (toParseSchema) {
                        grammar = this.fSchemaLoader.loadSchema(this.fXSDDescription, xis, this.fLocationPairs);
                    }
                }
                catch (final IOException ex) {
                    final String[] locationHints = this.fXSDDescription.getLocationHints();
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { (locationHints != null) ? locationHints[0] : XMLSymbols.EMPTY_STRING }, (short)0);
                }
            }
        }
        return grammar;
    }
    
    private void setLocationHints(final XSDDescription desc, final String[] locations, final SchemaGrammar grammar) {
        final int length = locations.length;
        if (grammar == null) {
            System.arraycopy(locations, 0, this.fXSDDescription.fLocationHints = new String[length], 0, length);
        }
        else {
            this.setLocationHints(desc, locations, grammar.getDocumentLocations());
        }
    }
    
    private void setLocationHints(final XSDDescription desc, final String[] locations, final StringList docLocations) {
        final int length = locations.length;
        final String[] hints = new String[length];
        int counter = 0;
        for (int i = 0; i < length; ++i) {
            try {
                final String id = XMLEntityManager.expandSystemId(locations[i], desc.getBaseSystemId(), false);
                if (!docLocations.contains(id)) {
                    hints[counter++] = locations[i];
                }
            }
            catch (final URI.MalformedURIException ex) {}
        }
        if (counter > 0) {
            if (counter == length) {
                this.fXSDDescription.fLocationHints = hints;
            }
            else {
                System.arraycopy(hints, 0, this.fXSDDescription.fLocationHints = new String[counter], 0, counter);
            }
        }
    }
    
    XSTypeDefinition getAndCheckXsiType(final QName element, final String xsiType, final XMLAttributes attributes) {
        QName typeName = null;
        try {
            typeName = (QName)this.fQNameDV.validate(xsiType, this.fValidationState, null);
        }
        catch (final InvalidDatatypeValueException e) {
            this.reportSchemaError(e.getKey(), e.getArgs());
            this.reportSchemaError("cvc-elt.4.1", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, xsiType });
            return null;
        }
        XSTypeDefinition type = null;
        if (typeName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            type = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(typeName.localpart);
        }
        if (type == null) {
            final SchemaGrammar grammar = this.findSchemaGrammar((short)7, typeName.uri, element, typeName, attributes);
            if (grammar != null) {
                type = grammar.getGlobalTypeDecl(typeName.localpart);
            }
        }
        if (type == null) {
            this.reportSchemaError("cvc-elt.4.2", new Object[] { element.rawname, xsiType });
            return null;
        }
        if (this.fCurrentType != null) {
            short block = this.fCurrentElemDecl.fBlock;
            if (this.fCurrentType.getTypeCategory() == 15) {
                block |= ((XSComplexTypeDecl)this.fCurrentType).fBlock;
            }
            if (!XSConstraints.checkTypeDerivationOk(type, this.fCurrentType, block)) {
                this.reportSchemaError("cvc-elt.4.3", new Object[] { element.rawname, xsiType, this.fCurrentType.getName() });
            }
        }
        return type;
    }
    
    boolean getXsiNil(final QName element, final String xsiNil) {
        if (this.fCurrentElemDecl != null && !this.fCurrentElemDecl.getNillable()) {
            this.reportSchemaError("cvc-elt.3.1", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
        }
        else {
            final String value = XMLChar.trim(xsiNil);
            if (value.equals("true") || value.equals("1")) {
                if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                    this.reportSchemaError("cvc-elt.3.2.2", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
                }
                return true;
            }
        }
        return false;
    }
    
    void processAttributes(final QName element, final XMLAttributes attributes, final XSAttributeGroupDecl attrGrp) {
        String wildcardIDName = null;
        final int attCount = attributes.getLength();
        Augmentations augs = null;
        AttributePSVImpl attrPSVI = null;
        final boolean isSimple = this.fCurrentType == null || this.fCurrentType.getTypeCategory() == 16;
        XSObjectList attrUses = null;
        int useCount = 0;
        XSWildcardDecl attrWildcard = null;
        if (!isSimple) {
            attrUses = attrGrp.getAttributeUses();
            useCount = attrUses.getLength();
            attrWildcard = attrGrp.fAttributeWC;
        }
        for (int index = 0; index < attCount; ++index) {
            attributes.getName(index, this.fTempQName);
            if (this.fAugPSVI || this.fIdConstraint) {
                augs = attributes.getAugmentations(index);
                attrPSVI = (AttributePSVImpl)augs.getItem("ATTRIBUTE_PSVI");
                if (attrPSVI != null) {
                    attrPSVI.reset();
                }
                else {
                    attrPSVI = new AttributePSVImpl();
                    augs.putItem("ATTRIBUTE_PSVI", attrPSVI);
                }
                attrPSVI.fValidationContext = this.fValidationRoot;
            }
            if (this.fTempQName.uri == SchemaSymbols.URI_XSI) {
                XSAttributeDecl attrDecl = null;
                if (this.fTempQName.localpart == SchemaSymbols.XSI_SCHEMALOCATION) {
                    attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
                }
                else if (this.fTempQName.localpart == SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION) {
                    attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                }
                else if (this.fTempQName.localpart == SchemaSymbols.XSI_NIL) {
                    attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NIL);
                }
                else if (this.fTempQName.localpart == SchemaSymbols.XSI_TYPE) {
                    attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_TYPE);
                }
                if (attrDecl != null) {
                    this.processOneAttribute(element, attributes, index, attrDecl, null, attrPSVI);
                    continue;
                }
            }
            if (this.fTempQName.rawname != XMLSymbols.PREFIX_XMLNS) {
                if (!this.fTempQName.rawname.startsWith("xmlns:")) {
                    if (isSimple) {
                        this.reportSchemaError("cvc-type.3.1.1", new Object[] { element.rawname, this.fTempQName.rawname });
                    }
                    else {
                        XSAttributeUseImpl currUse = null;
                        for (int i = 0; i < useCount; ++i) {
                            final XSAttributeUseImpl oneUse = (XSAttributeUseImpl)attrUses.item(i);
                            if (oneUse.fAttrDecl.fName == this.fTempQName.localpart && oneUse.fAttrDecl.fTargetNamespace == this.fTempQName.uri) {
                                currUse = oneUse;
                                break;
                            }
                        }
                        if (currUse == null && (attrWildcard == null || !attrWildcard.allowNamespace(this.fTempQName.uri))) {
                            this.reportSchemaError("cvc-complex-type.3.2.2", new Object[] { element.rawname, this.fTempQName.rawname });
                        }
                        else {
                            XSAttributeDecl currDecl = null;
                            if (currUse != null) {
                                currDecl = currUse.fAttrDecl;
                            }
                            else {
                                if (attrWildcard.fProcessContents == 2) {
                                    continue;
                                }
                                final SchemaGrammar grammar = this.findSchemaGrammar((short)6, this.fTempQName.uri, element, this.fTempQName, attributes);
                                if (grammar != null) {
                                    currDecl = grammar.getGlobalAttributeDecl(this.fTempQName.localpart);
                                }
                                if (currDecl == null) {
                                    if (attrWildcard.fProcessContents == 1) {
                                        this.reportSchemaError("cvc-complex-type.3.2.2", new Object[] { element.rawname, this.fTempQName.rawname });
                                    }
                                    continue;
                                }
                                else if (currDecl.fType.getTypeCategory() == 16 && currDecl.fType.isIDType()) {
                                    if (wildcardIDName != null) {
                                        this.reportSchemaError("cvc-complex-type.5.1", new Object[] { element.rawname, currDecl.fName, wildcardIDName });
                                    }
                                    else {
                                        wildcardIDName = currDecl.fName;
                                    }
                                }
                            }
                            this.processOneAttribute(element, attributes, index, currDecl, currUse, attrPSVI);
                        }
                    }
                }
            }
        }
        if (!isSimple && attrGrp.fIDAttrName != null && wildcardIDName != null) {
            this.reportSchemaError("cvc-complex-type.5.2", new Object[] { element.rawname, wildcardIDName, attrGrp.fIDAttrName });
        }
    }
    
    void processOneAttribute(final QName element, final XMLAttributes attributes, final int index, final XSAttributeDecl currDecl, final XSAttributeUseImpl currUse, final AttributePSVImpl attrPSVI) {
        final String attrValue = attributes.getValue(index);
        this.fXSIErrorReporter.pushContext();
        final XSSimpleType attDV = currDecl.fType;
        Object actualValue = null;
        try {
            actualValue = attDV.validate(attrValue, this.fValidationState, this.fValidatedInfo);
            if (this.fNormalizeData) {
                attributes.setValue(index, this.fValidatedInfo.normalizedValue);
            }
            if (attributes instanceof XMLAttributesImpl) {
                final XMLAttributesImpl attrs = (XMLAttributesImpl)attributes;
                final boolean schemaId = (this.fValidatedInfo.memberType != null) ? this.fValidatedInfo.memberType.isIDType() : attDV.isIDType();
                attrs.setSchemaId(index, schemaId);
            }
            if (attDV.getVariety() == 1 && attDV.getPrimitiveKind() == 20) {
                final QName qName = (QName)actualValue;
                final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(qName.uri);
                if (grammar != null) {
                    this.fNotation = grammar.getGlobalNotationDecl(qName.localpart);
                }
            }
        }
        catch (final InvalidDatatypeValueException idve) {
            this.reportSchemaError(idve.getKey(), idve.getArgs());
            this.reportSchemaError("cvc-attribute.3", new Object[] { element.rawname, this.fTempQName.rawname, attrValue, attDV.getName() });
        }
        if (actualValue != null && currDecl.getConstraintType() == 2 && (!this.isComparable(this.fValidatedInfo, currDecl.fDefault) || !actualValue.equals(currDecl.fDefault.actualValue))) {
            this.reportSchemaError("cvc-attribute.4", new Object[] { element.rawname, this.fTempQName.rawname, attrValue, currDecl.fDefault.stringValue() });
        }
        if (actualValue != null && currUse != null && currUse.fConstraintType == 2 && (!this.isComparable(this.fValidatedInfo, currUse.fDefault) || !actualValue.equals(currUse.fDefault.actualValue))) {
            this.reportSchemaError("cvc-complex-type.3.1", new Object[] { element.rawname, this.fTempQName.rawname, attrValue, currUse.fDefault.stringValue() });
        }
        if (this.fIdConstraint) {
            attrPSVI.fActualValue = actualValue;
        }
        if (this.fAugPSVI) {
            attrPSVI.fDeclaration = currDecl;
            attrPSVI.fTypeDecl = attDV;
            attrPSVI.fMemberType = this.fValidatedInfo.memberType;
            attrPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
            attrPSVI.fActualValue = this.fValidatedInfo.actualValue;
            attrPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
            attrPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
            attrPSVI.fValidationAttempted = 2;
            final String[] errors = this.fXSIErrorReporter.mergeContext();
            attrPSVI.fErrorCodes = errors;
            attrPSVI.fValidity = (short)((errors == null) ? 2 : 1);
        }
    }
    
    void addDefaultAttributes(final QName element, final XMLAttributes attributes, final XSAttributeGroupDecl attrGrp) {
        final XSObjectList attrUses = attrGrp.getAttributeUses();
        for (int useCount = attrUses.getLength(), i = 0; i < useCount; ++i) {
            final XSAttributeUseImpl currUse = (XSAttributeUseImpl)attrUses.item(i);
            final XSAttributeDecl currDecl = currUse.fAttrDecl;
            short constType = currUse.fConstraintType;
            ValidatedInfo defaultValue = currUse.fDefault;
            if (constType == 0) {
                constType = currDecl.getConstraintType();
                defaultValue = currDecl.fDefault;
            }
            final boolean isSpecified = attributes.getValue(currDecl.fTargetNamespace, currDecl.fName) != null;
            if (currUse.fUse == 1 && !isSpecified) {
                this.reportSchemaError("cvc-complex-type.4", new Object[] { element.rawname, currDecl.fName });
            }
            if (!isSpecified && constType != 0) {
                final QName attName = new QName(null, currDecl.fName, currDecl.fName, currDecl.fTargetNamespace);
                final String normalized = (defaultValue != null) ? defaultValue.stringValue() : "";
                final int attrIndex = attributes.addAttribute(attName, "CDATA", normalized);
                if (attributes instanceof XMLAttributesImpl) {
                    final XMLAttributesImpl attrs = (XMLAttributesImpl)attributes;
                    final boolean schemaId = (defaultValue != null && defaultValue.memberType != null) ? defaultValue.memberType.isIDType() : currDecl.fType.isIDType();
                    attrs.setSchemaId(attrIndex, schemaId);
                }
                if (this.fAugPSVI) {
                    final Augmentations augs = attributes.getAugmentations(attrIndex);
                    final AttributePSVImpl attrPSVI = new AttributePSVImpl();
                    augs.putItem("ATTRIBUTE_PSVI", attrPSVI);
                    attrPSVI.fDeclaration = currDecl;
                    attrPSVI.fTypeDecl = currDecl.fType;
                    attrPSVI.fMemberType = defaultValue.memberType;
                    attrPSVI.fNormalizedValue = normalized;
                    attrPSVI.fActualValue = defaultValue.actualValue;
                    attrPSVI.fActualValueType = defaultValue.actualValueType;
                    attrPSVI.fItemValueTypes = defaultValue.itemValueTypes;
                    attrPSVI.fValidationContext = this.fValidationRoot;
                    attrPSVI.fValidity = 2;
                    attrPSVI.fValidationAttempted = 2;
                    attrPSVI.fSpecified = true;
                }
            }
        }
    }
    
    void processElementContent(final QName element) {
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fDefault != null && !this.fSawText && !this.fSubElement && !this.fNil) {
            final String strv = this.fCurrentElemDecl.fDefault.stringValue();
            final int bufLen = strv.length();
            if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < bufLen) {
                this.fNormalizedStr.ch = new char[bufLen];
            }
            strv.getChars(0, bufLen, this.fNormalizedStr.ch, 0);
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.length = bufLen;
            this.fDefaultValue = this.fNormalizedStr;
        }
        this.fValidatedInfo.normalizedValue = null;
        if (this.fNil && (this.fSubElement || this.fSawText)) {
            this.reportSchemaError("cvc-elt.3.2.1", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
        }
        this.fValidatedInfo.reset();
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() != 0 && !this.fSubElement && !this.fSawText && !this.fNil) {
            if (this.fCurrentType != this.fCurrentElemDecl.fType && XSConstraints.ElementDefaultValidImmediate(this.fCurrentType, this.fCurrentElemDecl.fDefault.stringValue(), this.fState4XsiType, null) == null) {
                this.reportSchemaError("cvc-elt.5.1.1", new Object[] { element.rawname, this.fCurrentType.getName(), this.fCurrentElemDecl.fDefault.stringValue() });
            }
            this.elementLocallyValidType(element, this.fCurrentElemDecl.fDefault.stringValue());
        }
        else {
            final Object actualValue = this.elementLocallyValidType(element, this.fBuffer);
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2 && !this.fNil) {
                final String content = this.fBuffer.toString();
                if (this.fSubElement) {
                    this.reportSchemaError("cvc-elt.5.2.2.1", new Object[] { element.rawname });
                }
                if (this.fCurrentType.getTypeCategory() == 15) {
                    final XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
                    if (ctype.fContentType == 3) {
                        if (!this.fCurrentElemDecl.fDefault.normalizedValue.equals(content)) {
                            this.reportSchemaError("cvc-elt.5.2.2.2.1", new Object[] { element.rawname, content, this.fCurrentElemDecl.fDefault.normalizedValue });
                        }
                    }
                    else if (ctype.fContentType == 1 && actualValue != null && (!this.isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) || !actualValue.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
                        this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { element.rawname, content, this.fCurrentElemDecl.fDefault.stringValue() });
                    }
                }
                else if (this.fCurrentType.getTypeCategory() == 16 && actualValue != null && (!this.isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) || !actualValue.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
                    this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { element.rawname, content, this.fCurrentElemDecl.fDefault.stringValue() });
                }
            }
        }
        if (this.fDefaultValue == null && this.fNormalizeData && this.fDocumentHandler != null && this.fUnionType) {
            String content2 = this.fValidatedInfo.normalizedValue;
            if (content2 == null) {
                content2 = this.fBuffer.toString();
            }
            final int bufLen = content2.length();
            if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < bufLen) {
                this.fNormalizedStr.ch = new char[bufLen];
            }
            content2.getChars(0, bufLen, this.fNormalizedStr.ch, 0);
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.length = bufLen;
            this.fDocumentHandler.characters(this.fNormalizedStr, null);
        }
    }
    
    Object elementLocallyValidType(final QName element, final Object textContent) {
        if (this.fCurrentType == null) {
            return null;
        }
        Object retValue = null;
        if (this.fCurrentType.getTypeCategory() == 16) {
            if (this.fSubElement) {
                this.reportSchemaError("cvc-type.3.1.2", new Object[] { element.rawname });
            }
            if (!this.fNil) {
                final XSSimpleType dv = (XSSimpleType)this.fCurrentType;
                try {
                    if (!this.fNormalizeData || this.fUnionType) {
                        this.fValidationState.setNormalizationRequired(true);
                    }
                    retValue = dv.validate(textContent, this.fValidationState, this.fValidatedInfo);
                }
                catch (final InvalidDatatypeValueException e) {
                    this.reportSchemaError(e.getKey(), e.getArgs());
                    this.reportSchemaError("cvc-type.3.1.3", new Object[] { element.rawname, textContent });
                }
            }
        }
        else {
            retValue = this.elementLocallyValidComplexType(element, textContent);
        }
        return retValue;
    }
    
    Object elementLocallyValidComplexType(final QName element, final Object textContent) {
        Object actualValue = null;
        final XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
        if (!this.fNil) {
            if (ctype.fContentType == 0 && (this.fSubElement || this.fSawText)) {
                this.reportSchemaError("cvc-complex-type.2.1", new Object[] { element.rawname });
            }
            else if (ctype.fContentType == 1) {
                if (this.fSubElement) {
                    this.reportSchemaError("cvc-complex-type.2.2", new Object[] { element.rawname });
                }
                final XSSimpleType dv = ctype.fXSSimpleType;
                try {
                    if (!this.fNormalizeData || this.fUnionType) {
                        this.fValidationState.setNormalizationRequired(true);
                    }
                    actualValue = dv.validate(textContent, this.fValidationState, this.fValidatedInfo);
                }
                catch (final InvalidDatatypeValueException e) {
                    this.reportSchemaError(e.getKey(), e.getArgs());
                    this.reportSchemaError("cvc-complex-type.2.2", new Object[] { element.rawname });
                }
            }
            else if (ctype.fContentType == 2 && this.fSawCharacters) {
                this.reportSchemaError("cvc-complex-type.2.3", new Object[] { element.rawname });
            }
            if (ctype.fContentType == 2 || ctype.fContentType == 3) {
                if (this.fCurrCMState[0] >= 0 && !this.fCurrentCM.endContentModel(this.fCurrCMState)) {
                    final String expected = this.expectedStr(this.fCurrentCM.whatCanGoHere(this.fCurrCMState));
                    this.reportSchemaError("cvc-complex-type.2.4.b", new Object[] { element.rawname, expected });
                }
                else {
                    final ArrayList errors = this.fCurrentCM.checkMinMaxBounds();
                    if (errors != null) {
                        for (int i = 0; i < errors.size(); i += 2) {
                            this.reportSchemaError(errors.get(i), new Object[] { element.rawname, errors.get(i + 1) });
                        }
                    }
                }
            }
        }
        return actualValue;
    }
    
    void reportSchemaError(final String key, final Object[] arguments) {
        if (this.fDoValidation) {
            this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, arguments, (short)1);
        }
    }
    
    private boolean isComparable(final ValidatedInfo info1, final ValidatedInfo info2) {
        final short primitiveType1 = this.convertToPrimitiveKind(info1.actualValueType);
        final short primitiveType2 = this.convertToPrimitiveKind(info2.actualValueType);
        if (primitiveType1 != primitiveType2) {
            return (primitiveType1 == 1 && primitiveType2 == 2) || (primitiveType1 == 2 && primitiveType2 == 1);
        }
        if (primitiveType1 == 44 || primitiveType1 == 43) {
            final ShortList typeList1 = info1.itemValueTypes;
            final ShortList typeList2 = info2.itemValueTypes;
            final int typeList1Length = (typeList1 != null) ? typeList1.getLength() : 0;
            final int typeList2Length = (typeList2 != null) ? typeList2.getLength() : 0;
            if (typeList1Length != typeList2Length) {
                return false;
            }
            for (int i = 0; i < typeList1Length; ++i) {
                final short primitiveItem1 = this.convertToPrimitiveKind(typeList1.item(i));
                final short primitiveItem2 = this.convertToPrimitiveKind(typeList2.item(i));
                if (primitiveItem1 != primitiveItem2 && (primitiveItem1 != 1 || primitiveItem2 != 2) && (primitiveItem1 != 2 || primitiveItem2 != 1)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private short convertToPrimitiveKind(final short valueType) {
        if (valueType <= 20) {
            return valueType;
        }
        if (valueType <= 29) {
            return 2;
        }
        if (valueType <= 42) {
            return 4;
        }
        return valueType;
    }
    
    private String expectedStr(final Vector expected) {
        final StringBuffer ret = new StringBuffer("{");
        for (int size = expected.size(), i = 0; i < size; ++i) {
            if (i > 0) {
                ret.append(", ");
            }
            ret.append(expected.elementAt(i).toString());
        }
        ret.append('}');
        return ret.toString();
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser" };
        FEATURE_DEFAULTS = new Boolean[] { null, null, null, null, null, null, null, null, null, null, null, null, null, JdkXmlUtils.OVERRIDE_PARSER_DEFAULT };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null, null, null, null, null, null, null, null, null, null };
    }
    
    protected final class XSIErrorReporter
    {
        XMLErrorReporter fErrorReporter;
        Vector fErrors;
        int[] fContext;
        int fContextCount;
        
        protected XSIErrorReporter() {
            this.fErrors = new Vector();
            this.fContext = new int[8];
        }
        
        public void reset(final XMLErrorReporter errorReporter) {
            this.fErrorReporter = errorReporter;
            this.fErrors.removeAllElements();
            this.fContextCount = 0;
        }
        
        public void pushContext() {
            if (!XMLSchemaValidator.this.fAugPSVI) {
                return;
            }
            if (this.fContextCount == this.fContext.length) {
                final int newSize = this.fContextCount + 8;
                final int[] newArray = new int[newSize];
                System.arraycopy(this.fContext, 0, newArray, 0, this.fContextCount);
                this.fContext = newArray;
            }
            this.fContext[this.fContextCount++] = this.fErrors.size();
        }
        
        public String[] popContext() {
            if (!XMLSchemaValidator.this.fAugPSVI) {
                return null;
            }
            final int[] fContext = this.fContext;
            final int fContextCount = this.fContextCount - 1;
            this.fContextCount = fContextCount;
            final int contextPos = fContext[fContextCount];
            final int size = this.fErrors.size() - contextPos;
            if (size == 0) {
                return null;
            }
            final String[] errors = new String[size];
            for (int i = 0; i < size; ++i) {
                errors[i] = this.fErrors.elementAt(contextPos + i);
            }
            this.fErrors.setSize(contextPos);
            return errors;
        }
        
        public String[] mergeContext() {
            if (!XMLSchemaValidator.this.fAugPSVI) {
                return null;
            }
            final int[] fContext = this.fContext;
            final int fContextCount = this.fContextCount - 1;
            this.fContextCount = fContextCount;
            final int contextPos = fContext[fContextCount];
            final int size = this.fErrors.size() - contextPos;
            if (size == 0) {
                return null;
            }
            final String[] errors = new String[size];
            for (int i = 0; i < size; ++i) {
                errors[i] = this.fErrors.elementAt(contextPos + i);
            }
            return errors;
        }
        
        public void reportError(final String domain, final String key, final Object[] arguments, final short severity) throws XNIException {
            this.fErrorReporter.reportError(domain, key, arguments, severity);
            if (XMLSchemaValidator.this.fAugPSVI) {
                this.fErrors.addElement(key);
            }
        }
        
        public void reportError(final XMLLocator location, final String domain, final String key, final Object[] arguments, final short severity) throws XNIException {
            this.fErrorReporter.reportError(location, domain, key, arguments, severity);
            if (XMLSchemaValidator.this.fAugPSVI) {
                this.fErrors.addElement(key);
            }
        }
    }
    
    protected static class XPathMatcherStack
    {
        protected XPathMatcher[] fMatchers;
        protected int fMatchersCount;
        protected IntStack fContextStack;
        
        public XPathMatcherStack() {
            this.fMatchers = new XPathMatcher[4];
            this.fContextStack = new IntStack();
        }
        
        public void clear() {
            for (int i = 0; i < this.fMatchersCount; ++i) {
                this.fMatchers[i] = null;
            }
            this.fMatchersCount = 0;
            this.fContextStack.clear();
        }
        
        public int size() {
            return this.fContextStack.size();
        }
        
        public int getMatcherCount() {
            return this.fMatchersCount;
        }
        
        public void addMatcher(final XPathMatcher matcher) {
            this.ensureMatcherCapacity();
            this.fMatchers[this.fMatchersCount++] = matcher;
        }
        
        public XPathMatcher getMatcherAt(final int index) {
            return this.fMatchers[index];
        }
        
        public void pushContext() {
            this.fContextStack.push(this.fMatchersCount);
        }
        
        public void popContext() {
            this.fMatchersCount = this.fContextStack.pop();
        }
        
        private void ensureMatcherCapacity() {
            if (this.fMatchersCount == this.fMatchers.length) {
                final XPathMatcher[] array = new XPathMatcher[this.fMatchers.length * 2];
                System.arraycopy(this.fMatchers, 0, array, 0, this.fMatchers.length);
                this.fMatchers = array;
            }
        }
    }
    
    protected abstract class ValueStoreBase implements ValueStore
    {
        protected IdentityConstraint fIdentityConstraint;
        protected int fFieldCount;
        protected Field[] fFields;
        protected Object[] fLocalValues;
        protected short[] fLocalValueTypes;
        protected ShortList[] fLocalItemValueTypes;
        protected int fValuesCount;
        public final Vector fValues;
        public ShortVector fValueTypes;
        public Vector fItemValueTypes;
        private boolean fUseValueTypeVector;
        private int fValueTypesLength;
        private short fValueType;
        private boolean fUseItemValueTypeVector;
        private int fItemValueTypesLength;
        private ShortList fItemValueType;
        final StringBuffer fTempBuffer;
        
        protected ValueStoreBase(final IdentityConstraint identityConstraint) {
            this.fFieldCount = 0;
            this.fFields = null;
            this.fLocalValues = null;
            this.fLocalValueTypes = null;
            this.fLocalItemValueTypes = null;
            this.fValues = new Vector();
            this.fValueTypes = null;
            this.fItemValueTypes = null;
            this.fUseValueTypeVector = false;
            this.fValueTypesLength = 0;
            this.fValueType = 0;
            this.fUseItemValueTypeVector = false;
            this.fItemValueTypesLength = 0;
            this.fItemValueType = null;
            this.fTempBuffer = new StringBuffer();
            this.fIdentityConstraint = identityConstraint;
            this.fFieldCount = this.fIdentityConstraint.getFieldCount();
            this.fFields = new Field[this.fFieldCount];
            this.fLocalValues = new Object[this.fFieldCount];
            this.fLocalValueTypes = new short[this.fFieldCount];
            this.fLocalItemValueTypes = new ShortList[this.fFieldCount];
            for (int i = 0; i < this.fFieldCount; ++i) {
                this.fFields[i] = this.fIdentityConstraint.getFieldAt(i);
            }
        }
        
        public void clear() {
            this.fValuesCount = 0;
            this.fUseValueTypeVector = false;
            this.fValueTypesLength = 0;
            this.fValueType = 0;
            this.fUseItemValueTypeVector = false;
            this.fItemValueTypesLength = 0;
            this.fItemValueType = null;
            this.fValues.setSize(0);
            if (this.fValueTypes != null) {
                this.fValueTypes.clear();
            }
            if (this.fItemValueTypes != null) {
                this.fItemValueTypes.setSize(0);
            }
        }
        
        public void append(final ValueStoreBase newVal) {
            for (int i = 0; i < newVal.fValues.size(); ++i) {
                this.fValues.addElement(newVal.fValues.elementAt(i));
            }
        }
        
        public void startValueScope() {
            this.fValuesCount = 0;
            for (int i = 0; i < this.fFieldCount; ++i) {
                this.fLocalValues[i] = null;
                this.fLocalValueTypes[i] = 0;
                this.fLocalItemValueTypes[i] = null;
            }
        }
        
        public void endValueScope() {
            if (this.fValuesCount == 0) {
                if (this.fIdentityConstraint.getCategory() == 1) {
                    final String code = "AbsentKeyValue";
                    final String eName = this.fIdentityConstraint.getElementName();
                    final String cName = this.fIdentityConstraint.getIdentityConstraintName();
                    XMLSchemaValidator.this.reportSchemaError(code, new Object[] { eName, cName });
                }
                return;
            }
            if (this.fValuesCount != this.fFieldCount) {
                if (this.fIdentityConstraint.getCategory() == 1) {
                    final String code = "KeyNotEnoughValues";
                    final UniqueOrKey key = (UniqueOrKey)this.fIdentityConstraint;
                    final String eName2 = this.fIdentityConstraint.getElementName();
                    final String cName2 = key.getIdentityConstraintName();
                    XMLSchemaValidator.this.reportSchemaError(code, new Object[] { eName2, cName2 });
                }
            }
        }
        
        public void endDocumentFragment() {
        }
        
        public void endDocument() {
        }
        
        @Override
        public void reportError(final String key, final Object[] args) {
            XMLSchemaValidator.this.reportSchemaError(key, args);
        }
        
        @Override
        public void addValue(final Field field, final Object actualValue, final short valueType, final ShortList itemValueType) {
            int i;
            for (i = this.fFieldCount - 1; i > -1 && this.fFields[i] != field; --i) {}
            if (i == -1) {
                final String code = "UnknownField";
                final String eName = this.fIdentityConstraint.getElementName();
                final String cName = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(code, new Object[] { field.toString(), eName, cName });
                return;
            }
            if (Boolean.TRUE != XMLSchemaValidator.this.mayMatch(field)) {
                final String code = "FieldMultipleMatch";
                final String cName2 = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(code, new Object[] { field.toString(), cName2 });
            }
            else {
                ++this.fValuesCount;
            }
            this.fLocalValues[i] = actualValue;
            this.fLocalValueTypes[i] = valueType;
            this.fLocalItemValueTypes[i] = itemValueType;
            if (this.fValuesCount == this.fFieldCount) {
                this.checkDuplicateValues();
                for (i = 0; i < this.fFieldCount; ++i) {
                    this.fValues.addElement(this.fLocalValues[i]);
                    this.addValueType(this.fLocalValueTypes[i]);
                    this.addItemValueType(this.fLocalItemValueTypes[i]);
                }
            }
        }
        
        public boolean contains() {
            int next = 0;
            final int size = this.fValues.size();
            int i = 0;
        Label_0012:
            while (i < size) {
                next = i + this.fFieldCount;
                int j = 0;
                while (j < this.fFieldCount) {
                    final Object value1 = this.fLocalValues[j];
                    final Object value2 = this.fValues.elementAt(i);
                    final short valueType1 = this.fLocalValueTypes[j];
                    final short valueType2 = this.getValueTypeAt(i);
                    Label_0165: {
                        if (value1 != null && value2 != null && valueType1 == valueType2) {
                            if (value1.equals(value2)) {
                                if (valueType1 == 44 || valueType1 == 43) {
                                    final ShortList list1 = this.fLocalItemValueTypes[j];
                                    final ShortList list2 = this.getItemValueTypeAt(i);
                                    if (list1 == null || list2 == null) {
                                        break Label_0165;
                                    }
                                    if (!list1.equals(list2)) {
                                        break Label_0165;
                                    }
                                }
                                ++i;
                                ++j;
                                continue;
                            }
                        }
                    }
                    i = next;
                    continue Label_0012;
                }
                return true;
            }
            return false;
        }
        
        public int contains(final ValueStoreBase vsb) {
            final Vector values = vsb.fValues;
            final int size1 = values.size();
            if (this.fFieldCount <= 1) {
                for (int i = 0; i < size1; ++i) {
                    final short val = vsb.getValueTypeAt(i);
                    if (!this.valueTypeContains(val) || !this.fValues.contains(values.elementAt(i))) {
                        return i;
                    }
                    if (val == 44 || val == 43) {
                        final ShortList list1 = vsb.getItemValueTypeAt(i);
                        if (!this.itemValueTypeContains(list1)) {
                            return i;
                        }
                    }
                }
            }
            else {
                final int size2 = this.fValues.size();
                int j = 0;
            Label_0118:
                while (j < size1) {
                    int k = 0;
                Label_0127:
                    while (k < size2) {
                        int l = 0;
                        while (l < this.fFieldCount) {
                            final Object value1 = values.elementAt(j + l);
                            final Object value2 = this.fValues.elementAt(k + l);
                            final short valueType1 = vsb.getValueTypeAt(j + l);
                            final short valueType2 = this.getValueTypeAt(k + l);
                            Label_0293: {
                                if (value1 != value2) {
                                    if (valueType1 != valueType2 || value1 == null) {
                                        break Label_0293;
                                    }
                                    if (!value1.equals(value2)) {
                                        break Label_0293;
                                    }
                                }
                                if (valueType1 == 44 || valueType1 == 43) {
                                    final ShortList list2 = vsb.getItemValueTypeAt(j + l);
                                    final ShortList list3 = this.getItemValueTypeAt(k + l);
                                    if (list2 == null || list3 == null) {
                                        break Label_0293;
                                    }
                                    if (!list2.equals(list3)) {
                                        break Label_0293;
                                    }
                                }
                                ++l;
                                continue;
                            }
                            k += this.fFieldCount;
                            continue Label_0127;
                        }
                        j += this.fFieldCount;
                        continue Label_0118;
                    }
                    return j;
                }
            }
            return -1;
        }
        
        protected void checkDuplicateValues() {
        }
        
        protected String toString(final Object[] values) {
            final int size = values.length;
            if (size == 0) {
                return "";
            }
            this.fTempBuffer.setLength(0);
            for (int i = 0; i < size; ++i) {
                if (i > 0) {
                    this.fTempBuffer.append(',');
                }
                this.fTempBuffer.append(values[i]);
            }
            return this.fTempBuffer.toString();
        }
        
        protected String toString(final Vector values, final int start, final int length) {
            if (length == 0) {
                return "";
            }
            if (length == 1) {
                return String.valueOf(values.elementAt(start));
            }
            final StringBuffer str = new StringBuffer();
            for (int i = 0; i < length; ++i) {
                if (i > 0) {
                    str.append(',');
                }
                str.append(values.elementAt(start + i));
            }
            return str.toString();
        }
        
        @Override
        public String toString() {
            String s = super.toString();
            final int index1 = s.lastIndexOf(36);
            if (index1 != -1) {
                s = s.substring(index1 + 1);
            }
            final int index2 = s.lastIndexOf(46);
            if (index2 != -1) {
                s = s.substring(index2 + 1);
            }
            return s + '[' + this.fIdentityConstraint + ']';
        }
        
        private void addValueType(final short type) {
            if (this.fUseValueTypeVector) {
                this.fValueTypes.add(type);
            }
            else if (this.fValueTypesLength++ == 0) {
                this.fValueType = type;
            }
            else if (this.fValueType != type) {
                this.fUseValueTypeVector = true;
                if (this.fValueTypes == null) {
                    this.fValueTypes = new ShortVector(this.fValueTypesLength * 2);
                }
                for (int i = 1; i < this.fValueTypesLength; ++i) {
                    this.fValueTypes.add(this.fValueType);
                }
                this.fValueTypes.add(type);
            }
        }
        
        private short getValueTypeAt(final int index) {
            if (this.fUseValueTypeVector) {
                return this.fValueTypes.valueAt(index);
            }
            return this.fValueType;
        }
        
        private boolean valueTypeContains(final short value) {
            if (this.fUseValueTypeVector) {
                return this.fValueTypes.contains(value);
            }
            return this.fValueType == value;
        }
        
        private void addItemValueType(final ShortList itemValueType) {
            if (this.fUseItemValueTypeVector) {
                this.fItemValueTypes.add(itemValueType);
            }
            else if (this.fItemValueTypesLength++ == 0) {
                this.fItemValueType = itemValueType;
            }
            else if (this.fItemValueType != itemValueType && (this.fItemValueType == null || !this.fItemValueType.equals(itemValueType))) {
                this.fUseItemValueTypeVector = true;
                if (this.fItemValueTypes == null) {
                    this.fItemValueTypes = new Vector(this.fItemValueTypesLength * 2);
                }
                for (int i = 1; i < this.fItemValueTypesLength; ++i) {
                    this.fItemValueTypes.add(this.fItemValueType);
                }
                this.fItemValueTypes.add(itemValueType);
            }
        }
        
        private ShortList getItemValueTypeAt(final int index) {
            if (this.fUseItemValueTypeVector) {
                return this.fItemValueTypes.elementAt(index);
            }
            return this.fItemValueType;
        }
        
        private boolean itemValueTypeContains(final ShortList value) {
            if (this.fUseItemValueTypeVector) {
                return this.fItemValueTypes.contains(value);
            }
            return this.fItemValueType == value || (this.fItemValueType != null && this.fItemValueType.equals(value));
        }
    }
    
    protected class UniqueValueStore extends ValueStoreBase
    {
        public UniqueValueStore(final UniqueOrKey unique) {
            super(unique);
        }
        
        @Override
        protected void checkDuplicateValues() {
            if (this.contains()) {
                final String code = "DuplicateUnique";
                final String value = this.toString(this.fLocalValues);
                final String eName = this.fIdentityConstraint.getElementName();
                final String cName = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(code, new Object[] { value, eName, cName });
            }
        }
    }
    
    protected class KeyValueStore extends ValueStoreBase
    {
        public KeyValueStore(final UniqueOrKey key) {
            super(key);
        }
        
        @Override
        protected void checkDuplicateValues() {
            if (this.contains()) {
                final String code = "DuplicateKey";
                final String value = this.toString(this.fLocalValues);
                final String eName = this.fIdentityConstraint.getElementName();
                final String cName = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(code, new Object[] { value, eName, cName });
            }
        }
    }
    
    protected class KeyRefValueStore extends ValueStoreBase
    {
        protected ValueStoreBase fKeyValueStore;
        
        public KeyRefValueStore(final KeyRef keyRef, final KeyValueStore keyValueStore) {
            super(keyRef);
            this.fKeyValueStore = keyValueStore;
        }
        
        @Override
        public void endDocumentFragment() {
            super.endDocumentFragment();
            this.fKeyValueStore = XMLSchemaValidator.this.fValueStoreCache.fGlobalIDConstraintMap.get(((KeyRef)this.fIdentityConstraint).getKey());
            if (this.fKeyValueStore == null) {
                final String code = "KeyRefOutOfScope";
                final String value = this.fIdentityConstraint.toString();
                XMLSchemaValidator.this.reportSchemaError(code, new Object[] { value });
                return;
            }
            final int errorIndex = this.fKeyValueStore.contains(this);
            if (errorIndex != -1) {
                final String code2 = "KeyNotFound";
                final String values = this.toString(this.fValues, errorIndex, this.fFieldCount);
                final String element = this.fIdentityConstraint.getElementName();
                final String name = this.fIdentityConstraint.getName();
                XMLSchemaValidator.this.reportSchemaError(code2, new Object[] { name, values, element });
            }
        }
        
        @Override
        public void endDocument() {
            super.endDocument();
        }
    }
    
    protected class ValueStoreCache
    {
        final LocalIDKey fLocalId;
        protected final Vector fValueStores;
        protected final Map<LocalIDKey, ValueStoreBase> fIdentityConstraint2ValueStoreMap;
        protected final Stack<Map<IdentityConstraint, ValueStoreBase>> fGlobalMapStack;
        protected final Map<IdentityConstraint, ValueStoreBase> fGlobalIDConstraintMap;
        
        public ValueStoreCache() {
            this.fLocalId = new LocalIDKey();
            this.fValueStores = new Vector();
            this.fIdentityConstraint2ValueStoreMap = new HashMap<LocalIDKey, ValueStoreBase>();
            this.fGlobalMapStack = new Stack<Map<IdentityConstraint, ValueStoreBase>>();
            this.fGlobalIDConstraintMap = new HashMap<IdentityConstraint, ValueStoreBase>();
        }
        
        public void startDocument() {
            this.fValueStores.removeAllElements();
            this.fIdentityConstraint2ValueStoreMap.clear();
            this.fGlobalIDConstraintMap.clear();
            this.fGlobalMapStack.removeAllElements();
        }
        
        public void startElement() {
            if (this.fGlobalIDConstraintMap.size() > 0) {
                this.fGlobalMapStack.push((Map)((HashMap)this.fGlobalIDConstraintMap).clone());
            }
            else {
                this.fGlobalMapStack.push(null);
            }
            this.fGlobalIDConstraintMap.clear();
        }
        
        public void endElement() {
            if (this.fGlobalMapStack.isEmpty()) {
                return;
            }
            final Map<IdentityConstraint, ValueStoreBase> oldMap = this.fGlobalMapStack.pop();
            if (oldMap == null) {
                return;
            }
            for (final Map.Entry<IdentityConstraint, ValueStoreBase> entry : oldMap.entrySet()) {
                final IdentityConstraint id = entry.getKey();
                final ValueStoreBase oldVal = entry.getValue();
                if (oldVal != null) {
                    final ValueStoreBase currVal = this.fGlobalIDConstraintMap.get(id);
                    if (currVal == null) {
                        this.fGlobalIDConstraintMap.put(id, oldVal);
                    }
                    else {
                        if (currVal == oldVal) {
                            continue;
                        }
                        currVal.append(oldVal);
                    }
                }
            }
        }
        
        public void initValueStoresFor(final XSElementDecl eDecl, final FieldActivator activator) {
            final IdentityConstraint[] icArray = eDecl.fIDConstraints;
            for (int icCount = eDecl.fIDCPos, i = 0; i < icCount; ++i) {
                switch (icArray[i].getCategory()) {
                    case 3: {
                        final UniqueOrKey unique = (UniqueOrKey)icArray[i];
                        final LocalIDKey toHash = new LocalIDKey(unique, XMLSchemaValidator.this.fElementDepth);
                        UniqueValueStore uniqueValueStore = this.fIdentityConstraint2ValueStoreMap.get(toHash);
                        if (uniqueValueStore == null) {
                            uniqueValueStore = new UniqueValueStore(unique);
                            this.fIdentityConstraint2ValueStoreMap.put(toHash, uniqueValueStore);
                        }
                        else {
                            uniqueValueStore.clear();
                        }
                        this.fValueStores.addElement(uniqueValueStore);
                        XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
                        break;
                    }
                    case 1: {
                        final UniqueOrKey key = (UniqueOrKey)icArray[i];
                        final LocalIDKey toHash = new LocalIDKey(key, XMLSchemaValidator.this.fElementDepth);
                        KeyValueStore keyValueStore = this.fIdentityConstraint2ValueStoreMap.get(toHash);
                        if (keyValueStore == null) {
                            keyValueStore = new KeyValueStore(key);
                            this.fIdentityConstraint2ValueStoreMap.put(toHash, keyValueStore);
                        }
                        else {
                            keyValueStore.clear();
                        }
                        this.fValueStores.addElement(keyValueStore);
                        XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
                        break;
                    }
                    case 2: {
                        final KeyRef keyRef = (KeyRef)icArray[i];
                        final LocalIDKey toHash = new LocalIDKey(keyRef, XMLSchemaValidator.this.fElementDepth);
                        KeyRefValueStore keyRefValueStore = this.fIdentityConstraint2ValueStoreMap.get(toHash);
                        if (keyRefValueStore == null) {
                            keyRefValueStore = new KeyRefValueStore(keyRef, null);
                            this.fIdentityConstraint2ValueStoreMap.put(toHash, keyRefValueStore);
                        }
                        else {
                            keyRefValueStore.clear();
                        }
                        this.fValueStores.addElement(keyRefValueStore);
                        XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
                        break;
                    }
                }
            }
        }
        
        public ValueStoreBase getValueStoreFor(final IdentityConstraint id, final int initialDepth) {
            this.fLocalId.fDepth = initialDepth;
            this.fLocalId.fId = id;
            return this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
        }
        
        public ValueStoreBase getGlobalValueStoreFor(final IdentityConstraint id) {
            return this.fGlobalIDConstraintMap.get(id);
        }
        
        public void transplant(final IdentityConstraint id, final int initialDepth) {
            this.fLocalId.fDepth = initialDepth;
            this.fLocalId.fId = id;
            final ValueStoreBase newVals = this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
            if (id.getCategory() == 2) {
                return;
            }
            final ValueStoreBase currVals = this.fGlobalIDConstraintMap.get(id);
            if (currVals != null) {
                currVals.append(newVals);
                this.fGlobalIDConstraintMap.put(id, currVals);
            }
            else {
                this.fGlobalIDConstraintMap.put(id, newVals);
            }
        }
        
        public void endDocument() {
            for (int count = this.fValueStores.size(), i = 0; i < count; ++i) {
                final ValueStoreBase valueStore = this.fValueStores.elementAt(i);
                valueStore.endDocument();
            }
        }
        
        @Override
        public String toString() {
            final String s = super.toString();
            final int index1 = s.lastIndexOf(36);
            if (index1 != -1) {
                return s.substring(index1 + 1);
            }
            final int index2 = s.lastIndexOf(46);
            if (index2 != -1) {
                return s.substring(index2 + 1);
            }
            return s;
        }
    }
    
    protected class LocalIDKey
    {
        public IdentityConstraint fId;
        public int fDepth;
        
        public LocalIDKey() {
        }
        
        public LocalIDKey(final IdentityConstraint id, final int depth) {
            this.fId = id;
            this.fDepth = depth;
        }
        
        @Override
        public int hashCode() {
            return this.fId.hashCode() + this.fDepth;
        }
        
        @Override
        public boolean equals(final Object localIDKey) {
            if (localIDKey instanceof LocalIDKey) {
                final LocalIDKey lIDKey = (LocalIDKey)localIDKey;
                return lIDKey.fId == this.fId && lIDKey.fDepth == this.fDepth;
            }
            return false;
        }
    }
    
    protected static final class ShortVector
    {
        private int fLength;
        private short[] fData;
        
        public ShortVector() {
        }
        
        public ShortVector(final int initialCapacity) {
            this.fData = new short[initialCapacity];
        }
        
        public int length() {
            return this.fLength;
        }
        
        public void add(final short value) {
            this.ensureCapacity(this.fLength + 1);
            this.fData[this.fLength++] = value;
        }
        
        public short valueAt(final int position) {
            return this.fData[position];
        }
        
        public void clear() {
            this.fLength = 0;
        }
        
        public boolean contains(final short value) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fData[i] == value) {
                    return true;
                }
            }
            return false;
        }
        
        private void ensureCapacity(final int size) {
            if (this.fData == null) {
                this.fData = new short[8];
            }
            else if (this.fData.length <= size) {
                final short[] newdata = new short[this.fData.length * 2];
                System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
                this.fData = newdata;
            }
        }
    }
}
