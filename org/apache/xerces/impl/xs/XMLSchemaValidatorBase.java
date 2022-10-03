package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.HashMap;
import org.apache.xerces.impl.dv.xs.EqualityHelper;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.ValueStore;
import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xs.identity.XPathMatcher;
import org.apache.xerces.impl.xs.identity.Selector;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.IOException;
import org.apache.xerces.util.URI;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.util.IntStack;
import java.util.Vector;
import java.util.List;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.xs.XSTypeAlternative;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.models.CMNodeFactory;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.NamespaceContext;
import java.util.ArrayList;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.AugmentationsImpl;
import java.util.Hashtable;
import org.apache.xerces.impl.xs.identity.FieldActivator;

public class XMLSchemaValidatorBase implements XSElementDeclHelper, FieldActivator
{
    protected static final boolean DEBUG = false;
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
    protected static final String IGNORE_XSI_TYPE = "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl";
    protected static final String ID_IDREF_CHECKING = "http://apache.org/xml/features/validation/id-idref-checking";
    protected static final String UNPARSED_ENTITY_CHECKING = "http://apache.org/xml/features/validation/unparsed-entity-checking";
    protected static final String IDENTITY_CONSTRAINT_CHECKING = "http://apache.org/xml/features/validation/identity-constraint-checking";
    protected static final String TYPE_ALTERNATIVES_CHECKING = "http://apache.org/xml/features/validation/type-alternative-checking";
    protected static final String CTA_FULL_XPATH_CHECKING = "http://apache.org/xml/features/validation/cta-full-xpath-checking";
    protected static final String ASSERT_COMMENT_PI_CHECKING = "http://apache.org/xml/features/validation/assert-comments-and-pi-checking";
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
    protected static final String ROOT_TYPE_DEF = "http://apache.org/xml/properties/validation/schema/root-type-definition";
    protected static final String ROOT_ELEMENT_DECL = "http://apache.org/xml/properties/validation/schema/root-element-declaration";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    protected static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    protected static final String DATATYPE_XML_VERSION = "http://apache.org/xml/properties/validation/schema/datatype-xml-version";
    protected static final String[] RECOGNIZED_FEATURES;
    protected static final Boolean[] FEATURE_DEFAULTS;
    protected static final String[] RECOGNIZED_PROPERTIES;
    protected static final Object[] PROPERTY_DEFAULTS;
    protected static final int ID_CONSTRAINT_NUM = 1;
    static final XSAttributeDecl XSI_TYPE;
    static final XSAttributeDecl XSI_NIL;
    static final XSAttributeDecl XSI_SCHEMALOCATION;
    static final XSAttributeDecl XSI_NONAMESPACESCHEMALOCATION;
    protected static final Hashtable EMPTY_TABLE;
    protected ElementPSVImpl fCurrentPSVI;
    protected final AugmentationsImpl fAugmentations;
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
    protected String fSchemaType;
    protected boolean fEntityRef;
    protected boolean fInCDATA;
    protected SymbolTable fSymbolTable;
    protected XMLLocator fLocator;
    protected ArrayList fXSITypeErrors;
    protected IDContext fIDContext;
    protected String fDatatypeXMLVersion;
    protected NamespaceContext fNamespaceContext;
    protected final XSIErrorReporter fXSIErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected ValidationManager fValidationManager;
    protected XSValidationState fValidationState;
    protected XMLGrammarPool fGrammarPool;
    protected String fExternalSchemas;
    protected String fExternalNoNamespaceSchema;
    protected Object fJaxpSchemaSource;
    protected final XSDDescription fXSDDescription;
    protected final Hashtable fLocationPairs;
    protected final Hashtable fExpandedLocationPairs;
    protected final ArrayList fUnparsedLocations;
    short fSchemaVersion;
    XSConstraints fXSConstraints;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    static final int INITIAL_STACK_SIZE = 8;
    static final int INC_STACK_SIZE = 8;
    protected static final boolean DEBUG_NORMALIZATION = false;
    protected final XMLString fEmptyXMLStr;
    protected static final int BUFFER_SIZE = 20;
    protected final XMLString fNormalizedStr;
    protected boolean fFirstChunk;
    protected boolean fTrailing;
    protected short fWhiteSpace;
    protected boolean fUnionType;
    protected final XSGrammarBucket fGrammarBucket;
    protected final SubstitutionGroupHandler fSubGroupHandler;
    protected final XSSimpleType fQNameDV;
    protected final CMNodeFactory nodeFactory;
    protected final CMBuilder fCMBuilder;
    protected final XMLSchemaLoader fSchemaLoader;
    protected String fValidationRoot;
    protected int fSkipValidationDepth;
    protected int fNFullValidationDepth;
    protected int fNNoneValidationDepth;
    protected int fElementDepth;
    protected boolean fSubElement;
    protected boolean[] fSubElementStack;
    protected XSElementDecl fCurrentElemDecl;
    protected XSElementDecl[] fElemDeclStack;
    protected boolean fNil;
    protected boolean[] fNilStack;
    protected XSNotationDecl fNotation;
    protected XSNotationDecl[] fNotationStack;
    protected XSTypeDefinition fCurrentType;
    protected ObjectList fFailedAssertions;
    protected XSTypeAlternative fTypeAlternative;
    protected XSTypeDefinition[] fTypeStack;
    protected XSCMValidator fCurrentCM;
    protected XSCMValidator[] fCMStack;
    protected int[] fCurrCMState;
    protected int[][] fCMStateStack;
    protected boolean fStrictAssess;
    protected boolean[] fStrictAssessStack;
    protected final StringBuffer fBuffer;
    protected boolean fAppendBuffer;
    protected boolean fSawText;
    protected boolean[] fSawTextStack;
    protected boolean fSawCharacters;
    protected boolean[] fStringContent;
    protected final QName fTempQName;
    protected javax.xml.namespace.QName fRootTypeQName;
    protected XSTypeDefinition fRootTypeDefinition;
    protected javax.xml.namespace.QName fRootElementDeclQName;
    protected XSElementDecl fRootElementDeclaration;
    protected int fIgnoreXSITypeDepth;
    protected boolean fIDCChecking;
    protected boolean fTypeAlternativesChecking;
    protected boolean fCommentsAndPIsForAssert;
    protected ValidatedInfo fValidatedInfo;
    protected ValidationState fState4XsiType;
    protected ValidationState fState4ApplyDefault;
    protected XPathMatcherStack fMatcherStack;
    protected ValueStoreCache fValueStoreCache;
    protected XSDAssertionValidator fAssertionValidator;
    boolean fIsAssertProcessingNeededForSTUnionElem;
    List fIsAssertProcessingNeededForSTUnionAttrs;
    protected XSDTypeAlternativeValidator fTypeAlternativeValidator;
    Vector fInheritableAttrList;
    protected IntStack fInhrAttrCountStack;
    
    protected XMLSchemaValidatorBase() {
        this.fCurrentPSVI = new ElementPSVImpl();
        this.fAugmentations = new AugmentationsImpl();
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
        this.fXSITypeErrors = new ArrayList(4);
        this.fIDContext = null;
        this.fDatatypeXMLVersion = null;
        this.fNamespaceContext = null;
        this.fXSIErrorReporter = new XSIErrorReporter();
        this.fValidationManager = null;
        this.fValidationState = new XSValidationState();
        this.fExternalSchemas = null;
        this.fExternalNoNamespaceSchema = null;
        this.fJaxpSchemaSource = null;
        this.fXSDDescription = new XSDDescription();
        this.fLocationPairs = new Hashtable();
        this.fExpandedLocationPairs = new Hashtable();
        this.fUnparsedLocations = new ArrayList();
        this.fEmptyXMLStr = new XMLString(null, 0, -1);
        this.fNormalizedStr = new XMLString();
        this.fFirstChunk = true;
        this.fTrailing = false;
        this.fWhiteSpace = -1;
        this.fUnionType = false;
        this.fGrammarBucket = new XSGrammarBucket();
        this.fSubGroupHandler = new SubstitutionGroupHandler(this);
        this.fQNameDV = (XSSimpleType)SchemaGrammar.getS4SGrammar((short)1).getGlobalTypeDecl("QName");
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
        this.fRootTypeQName = null;
        this.fRootTypeDefinition = null;
        this.fRootElementDeclQName = null;
        this.fRootElementDeclaration = null;
        this.fValidatedInfo = new ValidatedInfo();
        this.fState4XsiType = new ValidationState();
        this.fState4ApplyDefault = new ValidationState();
        this.fMatcherStack = new XPathMatcherStack();
        this.fValueStoreCache = new ValueStoreCache();
        this.fAssertionValidator = null;
        this.fIsAssertProcessingNeededForSTUnionElem = true;
        this.fIsAssertProcessingNeededForSTUnionAttrs = new ArrayList();
        this.fTypeAlternativeValidator = null;
        this.fInheritableAttrList = new Vector();
        this.fInhrAttrCountStack = new IntStack();
    }
    
    public XSElementDecl getGlobalElementDecl(final QName qName) {
        final SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)5, qName.uri, null, qName, null);
        if (schemaGrammar != null) {
            return schemaGrammar.getGlobalElementDecl(qName.localpart);
        }
        return null;
    }
    
    SchemaGrammar findSchemaGrammar(final short fContextType, final String s, final QName fEnclosedElementName, final QName fTriggeringComponent, final XMLAttributes fAttributes) {
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(s);
        if (schemaGrammar == null) {
            this.fXSDDescription.setNamespace(s);
            if (this.fGrammarPool != null) {
                schemaGrammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(this.fXSDDescription);
                if (schemaGrammar != null && !this.fGrammarBucket.putGrammar(schemaGrammar, true, this.fNamespaceGrowth)) {
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
                    schemaGrammar = null;
                }
            }
        }
        if (!this.fUseGrammarPoolOnly && (schemaGrammar == null || (this.fNamespaceGrowth && !this.hasSchemaComponent(schemaGrammar, fContextType, fTriggeringComponent)))) {
            this.fXSDDescription.reset();
            this.fXSDDescription.fContextType = fContextType;
            this.fXSDDescription.setNamespace(s);
            this.fXSDDescription.fEnclosedElementName = fEnclosedElementName;
            this.fXSDDescription.fTriggeringComponent = fTriggeringComponent;
            this.fXSDDescription.fAttributes = fAttributes;
            if (this.fLocator != null) {
                this.fXSDDescription.setBaseSystemId(this.fLocator.getExpandedSystemId());
            }
            Hashtable hashtable = this.fLocationPairs;
            final Object value = hashtable.get((s == null) ? XMLSymbols.EMPTY_STRING : s);
            if (value != null) {
                final String[] locationArray = ((XMLSchemaLoader.LocationArray)value).getLocationArray();
                if (locationArray.length != 0) {
                    this.setLocationHints(this.fXSDDescription, locationArray, schemaGrammar);
                }
            }
            if (schemaGrammar == null || this.fXSDDescription.fLocationHints != null) {
                boolean b = true;
                if (schemaGrammar != null) {
                    hashtable = XMLSchemaValidatorBase.EMPTY_TABLE;
                }
                try {
                    final XMLInputSource resolveDocument = XMLSchemaLoader.resolveDocument(this.fXSDDescription, hashtable, this.fEntityResolver);
                    if (schemaGrammar != null && this.fNamespaceGrowth) {
                        try {
                            if (schemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(resolveDocument.getSystemId(), resolveDocument.getBaseSystemId(), false))) {
                                b = false;
                            }
                        }
                        catch (final URI.MalformedURIException ex) {}
                    }
                    if (b) {
                        schemaGrammar = this.fSchemaLoader.loadSchema(this.fXSDDescription, resolveDocument, this.fLocationPairs);
                    }
                }
                catch (final IOException ex2) {
                    final String[] locationHints = this.fXSDDescription.getLocationHints();
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { (locationHints != null) ? locationHints[0] : XMLSymbols.EMPTY_STRING }, (short)0, ex2);
                }
            }
        }
        return schemaGrammar;
    }
    
    private boolean hasSchemaComponent(final SchemaGrammar schemaGrammar, final short n, final QName qName) {
        if (schemaGrammar != null && qName != null) {
            final String localpart = qName.localpart;
            if (localpart != null && localpart.length() > 0) {
                switch (n) {
                    case 5: {
                        return schemaGrammar.getElementDeclaration(localpart) != null;
                    }
                    case 6: {
                        return schemaGrammar.getAttributeDeclaration(localpart) != null;
                    }
                    case 7: {
                        return schemaGrammar.getTypeDefinition(localpart) != null;
                    }
                }
            }
        }
        return false;
    }
    
    private void setLocationHints(final XSDDescription xsdDescription, final String[] array, final SchemaGrammar schemaGrammar) {
        final int length = array.length;
        if (schemaGrammar == null) {
            System.arraycopy(array, 0, this.fXSDDescription.fLocationHints = new String[length], 0, length);
        }
        else {
            this.setLocationHints(xsdDescription, array, schemaGrammar.getDocumentLocations());
        }
    }
    
    private void setLocationHints(final XSDDescription xsdDescription, final String[] array, final StringList list) {
        final int length = array.length;
        final String[] fLocationHints = new String[length];
        int n = 0;
        for (int i = 0; i < length; ++i) {
            if (!list.contains(array[i])) {
                fLocationHints[n++] = array[i];
            }
        }
        if (n > 0) {
            if (n == length) {
                this.fXSDDescription.fLocationHints = fLocationHints;
            }
            else {
                System.arraycopy(fLocationHints, 0, this.fXSDDescription.fLocationHints = new String[n], 0, n);
            }
        }
    }
    
    void reportSchemaError(final String s, final Object[] array) {
        if (this.fDoValidation) {
            this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", s, array, (short)1);
        }
    }
    
    private void activateSelectorFor(final IdentityConstraint identityConstraint) {
        final Selector selector = identityConstraint.getSelector();
        if (selector == null) {
            return;
        }
        final XPathMatcher matcher = selector.createMatcher(this, this.fElementDepth);
        if (this.fSchemaVersion == 4) {
            matcher.setXPathDefaultNamespace(selector.getXPathDefaultNamespace());
        }
        this.fMatcherStack.addMatcher(matcher);
        matcher.startDocumentFragment();
    }
    
    public XPathMatcher activateField(final Field field, final int n) {
        final XPathMatcher matcher = field.createMatcher(this.fValueStoreCache.getValueStoreFor(field.getIdentityConstraint(), n));
        if (this.fSchemaVersion == 4) {
            matcher.setXPathDefaultNamespace(field.getXPathDefaultNamespace());
        }
        this.fMatcherStack.addMatcher(matcher);
        matcher.startDocumentFragment();
        return matcher;
    }
    
    public void startValueScopeFor(final IdentityConstraint identityConstraint, final int n) {
        this.fValueStoreCache.getValueStoreFor(identityConstraint, n).startValueScope();
    }
    
    public void endValueScopeFor(final IdentityConstraint identityConstraint, final int n) {
        this.fValueStoreCache.getValueStoreFor(identityConstraint, n).endValueScope();
    }
    
    XSDAssertionValidator getAssertionValidator() {
        return this.fAssertionValidator;
    }
    
    void setIsAssertProcessingNeededForSTUnionElem(final boolean fIsAssertProcessingNeededForSTUnionElem) {
        this.fIsAssertProcessingNeededForSTUnionElem = fIsAssertProcessingNeededForSTUnionElem;
    }
    
    List getIsAssertProcessingNeededForSTUnionAttrs() {
        return this.fIsAssertProcessingNeededForSTUnionAttrs;
    }
    
    Vector getInheritableAttrList() {
        return this.fInheritableAttrList;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl", "http://apache.org/xml/features/validation/id-idref-checking", "http://apache.org/xml/features/validation/identity-constraint-checking", "http://apache.org/xml/features/validation/unparsed-entity-checking", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/validation/type-alternative-checking", "http://apache.org/xml/features/validation/cta-full-xpath-checking", "http://apache.org/xml/features/validation/assert-comments-and-pi-checking" };
        FEATURE_DEFAULTS = new Boolean[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/validation/schema/root-type-definition", "http://apache.org/xml/properties/validation/schema/root-element-declaration", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://apache.org/xml/properties/validation/schema/version", "http://apache.org/xml/properties/validation/schema/datatype-xml-version" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null, null, null, null, null, null, null, null, null, null };
        XSI_TYPE = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_TYPE);
        XSI_NIL = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NIL);
        XSI_SCHEMALOCATION = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
        XSI_NONAMESPACESCHEMALOCATION = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
        EMPTY_TABLE = new Hashtable();
    }
    
    protected class KeyRefValueStore extends ValueStoreBase
    {
        protected ValueStoreBase fKeyValueStore;
        
        public KeyRefValueStore(final KeyRef keyRef, final KeyValueStore fKeyValueStore, final String s) {
            super(keyRef, s);
            this.fKeyValueStore = fKeyValueStore;
        }
        
        public void endDocumentFragment() {
            super.endDocumentFragment();
            this.fKeyValueStore = XMLSchemaValidatorBase.this.fValueStoreCache.fGlobalIDConstraintMap.get(((KeyRef)this.fIdentityConstraint).getKey());
            if (this.fKeyValueStore == null) {
                XMLSchemaValidatorBase.this.reportSchemaError("KeyRefOutOfScope", new Object[] { this.fIdentityConstraint.getName() });
                return;
            }
            final int contains = this.fKeyValueStore.contains(this);
            if (contains != -1) {
                XMLSchemaValidatorBase.this.reportSchemaError("KeyNotFound", new Object[] { this.fIdentityConstraint.getName(), this.toString(this.fValues, contains, this.fFieldCount), this.fElementName });
            }
        }
        
        public void endDocument() {
            super.endDocument();
        }
    }
    
    protected abstract class ValueStoreBase implements ValueStore
    {
        protected IdentityConstraint fIdentityConstraint;
        protected int fFieldCount;
        protected Field[] fFields;
        protected String fElementName;
        protected Object[] fLocalValues;
        protected short[] fLocalValueTypes;
        protected ShortList[] fLocalItemValueTypes;
        protected int fValuesCount;
        protected boolean fHasValue;
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
        
        protected ValueStoreBase(final IdentityConstraint fIdentityConstraint, final String fElementName) {
            this.fFieldCount = 0;
            this.fFields = null;
            this.fLocalValues = null;
            this.fLocalValueTypes = null;
            this.fLocalItemValueTypes = null;
            this.fHasValue = false;
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
            this.fElementName = fElementName;
            this.fIdentityConstraint = fIdentityConstraint;
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
        
        public void append(final ValueStoreBase valueStoreBase) {
            for (int i = 0; i < valueStoreBase.fValues.size(); ++i) {
                this.fValues.addElement(valueStoreBase.fValues.elementAt(i));
                this.addValueType(valueStoreBase.getValueTypeAt(i));
                this.addItemValueType(valueStoreBase.getItemValueTypeAt(i));
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
                    XMLSchemaValidatorBase.this.reportSchemaError("AbsentKeyValue", new Object[] { this.fElementName, this.fIdentityConstraint.getIdentityConstraintName() });
                }
                return;
            }
            if (this.fValuesCount != this.fFieldCount) {
                if (this.fIdentityConstraint.getCategory() == 1) {
                    XMLSchemaValidatorBase.this.reportSchemaError("KeyNotEnoughValues", new Object[] { this.fElementName, this.fIdentityConstraint.getIdentityConstraintName() });
                }
            }
        }
        
        public void endDocumentFragment() {
        }
        
        public void endDocument() {
        }
        
        public void reportError(final String s, final Object[] array) {
            XMLSchemaValidatorBase.this.reportSchemaError(s, array);
        }
        
        public void addValue(final Field field, final boolean b, final Object o, final short n, final ShortList list) {
            int n2;
            for (n2 = this.fFieldCount - 1; n2 > -1 && this.fFields[n2] != field; --n2) {}
            if (n2 == -1) {
                XMLSchemaValidatorBase.this.reportSchemaError("UnknownField", new Object[] { field.toString(), this.fElementName, this.fIdentityConstraint.getIdentityConstraintName() });
                return;
            }
            if (!b) {
                XMLSchemaValidatorBase.this.reportSchemaError("FieldMultipleMatch", new Object[] { field.toString(), this.fIdentityConstraint.getIdentityConstraintName() });
            }
            else {
                ++this.fValuesCount;
                this.fHasValue = true;
            }
            this.fLocalValues[n2] = o;
            this.fLocalValueTypes[n2] = n;
            this.fLocalItemValueTypes[n2] = list;
            if (this.fValuesCount == this.fFieldCount) {
                this.checkDuplicateValues();
                for (int i = 0; i < this.fFieldCount; ++i) {
                    this.fValues.addElement(this.fLocalValues[i]);
                    this.addValueType(this.fLocalValueTypes[i]);
                    this.addItemValueType(this.fLocalItemValueTypes[i]);
                }
            }
        }
        
        public void setElementName(final String fElementName) {
            this.fElementName = fElementName;
        }
        
        public String getElementName() {
            return this.fElementName;
        }
        
        public boolean contains() {
            final int size = this.fValues.size();
            int i = 0;
        Label_0012:
            while (i < size) {
                final int n = i + this.fFieldCount;
                for (int j = 0; j < this.fFieldCount; ++j) {
                    final Object o = this.fLocalValues[j];
                    final Object element = this.fValues.elementAt(i);
                    final short n2 = this.fLocalValueTypes[j];
                    final short valueType = this.getValueTypeAt(i);
                    if (!EqualityHelper.isEqual(o, element, n2, valueType, this.isListType(n2) ? this.fLocalItemValueTypes[j] : null, this.isListType(valueType) ? this.getItemValueTypeAt(i) : null, XMLSchemaValidatorBase.this.fSchemaVersion)) {
                        i = n;
                        continue Label_0012;
                    }
                    ++i;
                }
                return true;
            }
            return false;
        }
        
        public int contains(final ValueStoreBase valueStoreBase) {
            final Vector fValues = valueStoreBase.fValues;
            final int size = fValues.size();
            if (this.fFieldCount <= 1) {
                int i = 0;
            Label_0021:
                while (i < size) {
                    final Object element = fValues.elementAt(i);
                    final short valueType = valueStoreBase.getValueTypeAt(i);
                    final ShortList list = this.isListType(valueType) ? valueStoreBase.getItemValueTypeAt(i) : null;
                    for (int j = 0; j < this.fValues.size(); ++j) {
                        final Object element2 = this.fValues.elementAt(j);
                        final short valueType2 = this.getValueTypeAt(j);
                        if (EqualityHelper.isEqual(element, element2, valueType, valueType2, list, this.isListType(valueType2) ? this.getItemValueTypeAt(j) : null, XMLSchemaValidatorBase.this.fSchemaVersion)) {
                            ++i;
                            continue Label_0021;
                        }
                    }
                    return i;
                }
            }
            else {
                final int size2 = this.fValues.size();
                int k = 0;
            Label_0174:
                while (k < size) {
                    int l = 0;
                Label_0183:
                    while (l < size2) {
                        for (int n = 0; n < this.fFieldCount; ++n) {
                            final Object element3 = fValues.elementAt(k + n);
                            final Object element4 = this.fValues.elementAt(l + n);
                            final short valueType3 = valueStoreBase.getValueTypeAt(k + n);
                            final short valueType4 = this.getValueTypeAt(l + n);
                            if (!EqualityHelper.isEqual(element3, element4, valueType3, valueType4, this.isListType(valueType3) ? valueStoreBase.getItemValueTypeAt(k + n) : null, this.isListType(valueType4) ? this.getItemValueTypeAt(l + n) : null, XMLSchemaValidatorBase.this.fSchemaVersion)) {
                                l += this.fFieldCount;
                                continue Label_0183;
                            }
                        }
                        k += this.fFieldCount;
                        continue Label_0174;
                    }
                    return k;
                }
            }
            return -1;
        }
        
        protected void checkDuplicateValues() {
        }
        
        protected String toString(final Object[] array) {
            final int length = array.length;
            if (length == 0) {
                return "";
            }
            this.fTempBuffer.setLength(0);
            for (int i = 0; i < length; ++i) {
                if (i > 0) {
                    this.fTempBuffer.append(',');
                }
                this.fTempBuffer.append(array[i]);
            }
            return this.fTempBuffer.toString();
        }
        
        protected String toString(final Vector vector, final int n, final int n2) {
            if (n2 == 0) {
                return "";
            }
            if (n2 == 1) {
                return String.valueOf(vector.elementAt(n));
            }
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < n2; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(vector.elementAt(n + i));
            }
            return sb.toString();
        }
        
        public String toString() {
            String s = super.toString();
            final int lastIndex = s.lastIndexOf(36);
            if (lastIndex != -1) {
                s = s.substring(lastIndex + 1);
            }
            final int lastIndex2 = s.lastIndexOf(46);
            if (lastIndex2 != -1) {
                s = s.substring(lastIndex2 + 1);
            }
            return s + '[' + this.fIdentityConstraint + ']';
        }
        
        private boolean isListType(final short n) {
            return n == 44 || n == 43;
        }
        
        private void addValueType(final short fValueType) {
            if (this.fUseValueTypeVector) {
                this.fValueTypes.add(fValueType);
            }
            else if (this.fValueTypesLength++ == 0) {
                this.fValueType = fValueType;
            }
            else if (this.fValueType != fValueType) {
                this.fUseValueTypeVector = true;
                if (this.fValueTypes == null) {
                    this.fValueTypes = new ShortVector(this.fValueTypesLength * 2);
                }
                for (int i = 1; i < this.fValueTypesLength; ++i) {
                    this.fValueTypes.add(this.fValueType);
                }
                this.fValueTypes.add(fValueType);
            }
        }
        
        private short getValueTypeAt(final int n) {
            if (this.fUseValueTypeVector) {
                return this.fValueTypes.valueAt(n);
            }
            return this.fValueType;
        }
        
        private void addItemValueType(final ShortList fItemValueType) {
            if (this.fUseItemValueTypeVector) {
                this.fItemValueTypes.add(fItemValueType);
            }
            else if (this.fItemValueTypesLength++ == 0) {
                this.fItemValueType = fItemValueType;
            }
            else if (this.fItemValueType != fItemValueType && (this.fItemValueType == null || !this.fItemValueType.equals(fItemValueType))) {
                this.fUseItemValueTypeVector = true;
                if (this.fItemValueTypes == null) {
                    this.fItemValueTypes = new Vector(this.fItemValueTypesLength * 2);
                }
                for (int i = 1; i < this.fItemValueTypesLength; ++i) {
                    this.fItemValueTypes.add(this.fItemValueType);
                }
                this.fItemValueTypes.add(fItemValueType);
            }
        }
        
        private ShortList getItemValueTypeAt(final int n) {
            if (this.fUseItemValueTypeVector) {
                return this.fItemValueTypes.elementAt(n);
            }
            return this.fItemValueType;
        }
    }
    
    protected static final class ShortVector
    {
        private int fLength;
        private short[] fData;
        
        public ShortVector() {
        }
        
        public ShortVector(final int n) {
            this.fData = new short[n];
        }
        
        public int length() {
            return this.fLength;
        }
        
        public void add(final short n) {
            this.ensureCapacity(this.fLength + 1);
            this.fData[this.fLength++] = n;
        }
        
        public short valueAt(final int n) {
            return this.fData[n];
        }
        
        public void clear() {
            this.fLength = 0;
        }
        
        public boolean contains(final short n) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fData[i] == n) {
                    return true;
                }
            }
            return false;
        }
        
        private void ensureCapacity(final int n) {
            if (this.fData == null) {
                this.fData = new short[8];
            }
            else if (this.fData.length <= n) {
                final short[] fData = new short[this.fData.length * 2];
                System.arraycopy(this.fData, 0, fData, 0, this.fData.length);
                this.fData = fData;
            }
        }
    }
    
    protected class ValueStoreCache
    {
        final LocalIDKey fLocalId;
        protected final ArrayList fValueStores;
        protected final HashMap fIdentityConstraint2ValueStoreMap;
        protected final Stack fGlobalMapStack;
        protected final HashMap fGlobalIDConstraintMap;
        
        public ValueStoreCache() {
            this.fLocalId = new LocalIDKey();
            this.fValueStores = new ArrayList();
            this.fIdentityConstraint2ValueStoreMap = new HashMap();
            this.fGlobalMapStack = new Stack();
            this.fGlobalIDConstraintMap = new HashMap();
        }
        
        public void startDocument() {
            this.fValueStores.clear();
            this.fIdentityConstraint2ValueStoreMap.clear();
            this.fGlobalIDConstraintMap.clear();
            this.fGlobalMapStack.removeAllElements();
        }
        
        public void startElement() {
            if (this.fGlobalIDConstraintMap.size() > 0) {
                this.fGlobalMapStack.push(this.fGlobalIDConstraintMap.clone());
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
            final HashMap hashMap = this.fGlobalMapStack.pop();
            if (hashMap == null) {
                return;
            }
            final Iterator iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                final IdentityConstraint identityConstraint = (IdentityConstraint)entry.getKey();
                final ValueStoreBase valueStoreBase = (ValueStoreBase)entry.getValue();
                if (valueStoreBase != null) {
                    final ValueStoreBase valueStoreBase2 = this.fGlobalIDConstraintMap.get(identityConstraint);
                    if (valueStoreBase2 == null) {
                        this.fGlobalIDConstraintMap.put(identityConstraint, valueStoreBase);
                    }
                    else {
                        if (valueStoreBase2 == valueStoreBase) {
                            continue;
                        }
                        valueStoreBase2.append(valueStoreBase);
                    }
                }
            }
        }
        
        public void initValueStoresFor(final XSElementDecl xsElementDecl, final FieldActivator fieldActivator) {
            final IdentityConstraint[] fidConstraints = xsElementDecl.fIDConstraints;
            for (int fidcPos = xsElementDecl.fIDCPos, i = 0; i < fidcPos; ++i) {
                switch (fidConstraints[i].getCategory()) {
                    case 3: {
                        final UniqueOrKey uniqueOrKey = (UniqueOrKey)fidConstraints[i];
                        final LocalIDKey localIDKey = new LocalIDKey(uniqueOrKey, XMLSchemaValidatorBase.this.fElementDepth);
                        UniqueValueStore uniqueValueStore = this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
                        if (uniqueValueStore == null) {
                            uniqueValueStore = new UniqueValueStore(uniqueOrKey, xsElementDecl.getName());
                            this.fIdentityConstraint2ValueStoreMap.put(localIDKey, uniqueValueStore);
                        }
                        else {
                            uniqueValueStore.clear();
                            uniqueValueStore.setElementName(xsElementDecl.getName());
                        }
                        this.fValueStores.add(uniqueValueStore);
                        XMLSchemaValidatorBase.this.activateSelectorFor(fidConstraints[i]);
                        break;
                    }
                    case 1: {
                        final UniqueOrKey uniqueOrKey2 = (UniqueOrKey)fidConstraints[i];
                        final LocalIDKey localIDKey2 = new LocalIDKey(uniqueOrKey2, XMLSchemaValidatorBase.this.fElementDepth);
                        KeyValueStore keyValueStore = this.fIdentityConstraint2ValueStoreMap.get(localIDKey2);
                        if (keyValueStore == null) {
                            keyValueStore = new KeyValueStore(uniqueOrKey2, xsElementDecl.getName());
                            this.fIdentityConstraint2ValueStoreMap.put(localIDKey2, keyValueStore);
                        }
                        else {
                            keyValueStore.clear();
                            keyValueStore.setElementName(xsElementDecl.getName());
                        }
                        this.fValueStores.add(keyValueStore);
                        XMLSchemaValidatorBase.this.activateSelectorFor(fidConstraints[i]);
                        break;
                    }
                    case 2: {
                        final KeyRef keyRef = (KeyRef)fidConstraints[i];
                        final LocalIDKey localIDKey3 = new LocalIDKey(keyRef, XMLSchemaValidatorBase.this.fElementDepth);
                        KeyRefValueStore keyRefValueStore = this.fIdentityConstraint2ValueStoreMap.get(localIDKey3);
                        if (keyRefValueStore == null) {
                            keyRefValueStore = new KeyRefValueStore(keyRef, null, xsElementDecl.getName());
                            this.fIdentityConstraint2ValueStoreMap.put(localIDKey3, keyRefValueStore);
                        }
                        else {
                            keyRefValueStore.clear();
                            keyRefValueStore.setElementName(xsElementDecl.getName());
                        }
                        this.fValueStores.add(keyRefValueStore);
                        XMLSchemaValidatorBase.this.activateSelectorFor(fidConstraints[i]);
                        break;
                    }
                }
            }
        }
        
        public ValueStoreBase getValueStoreFor(final IdentityConstraint fId, final int fDepth) {
            this.fLocalId.fDepth = fDepth;
            this.fLocalId.fId = fId;
            return this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
        }
        
        public ValueStoreBase getGlobalValueStoreFor(final IdentityConstraint identityConstraint) {
            return this.fGlobalIDConstraintMap.get(identityConstraint);
        }
        
        public void transplant(final IdentityConstraint fId, final int fDepth) {
            this.fLocalId.fDepth = fDepth;
            this.fLocalId.fId = fId;
            final ValueStoreBase valueStoreBase = this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
            if (fId.getCategory() == 2) {
                return;
            }
            final ValueStoreBase valueStoreBase2 = this.fGlobalIDConstraintMap.get(fId);
            if (valueStoreBase2 != null) {
                valueStoreBase2.append(valueStoreBase);
                this.fGlobalIDConstraintMap.put(fId, valueStoreBase2);
            }
            else {
                this.fGlobalIDConstraintMap.put(fId, valueStoreBase);
            }
        }
        
        public void endDocument() {
            for (int size = this.fValueStores.size(), i = 0; i < size; ++i) {
                ((ValueStoreBase)this.fValueStores.get(i)).endDocument();
            }
        }
        
        public String toString() {
            final String string = super.toString();
            final int lastIndex = string.lastIndexOf(36);
            if (lastIndex != -1) {
                return string.substring(lastIndex + 1);
            }
            final int lastIndex2 = string.lastIndexOf(46);
            if (lastIndex2 != -1) {
                return string.substring(lastIndex2 + 1);
            }
            return string;
        }
    }
    
    protected class KeyValueStore extends ValueStoreBase
    {
        public KeyValueStore(final UniqueOrKey uniqueOrKey, final String s) {
            super(uniqueOrKey, s);
        }
        
        protected void checkDuplicateValues() {
            if (this.contains()) {
                XMLSchemaValidatorBase.this.reportSchemaError("DuplicateKey", new Object[] { this.toString(this.fLocalValues), this.fElementName, this.fIdentityConstraint.getIdentityConstraintName() });
            }
        }
    }
    
    protected static final class LocalIDKey
    {
        public IdentityConstraint fId;
        public int fDepth;
        
        public LocalIDKey() {
        }
        
        public LocalIDKey(final IdentityConstraint fId, final int fDepth) {
            this.fId = fId;
            this.fDepth = fDepth;
        }
        
        public int hashCode() {
            return this.fId.hashCode() + this.fDepth;
        }
        
        public boolean equals(final Object o) {
            if (o instanceof LocalIDKey) {
                final LocalIDKey localIDKey = (LocalIDKey)o;
                return localIDKey.fId == this.fId && localIDKey.fDepth == this.fDepth;
            }
            return false;
        }
    }
    
    protected class UniqueValueStore extends ValueStoreBase
    {
        public UniqueValueStore(final UniqueOrKey uniqueOrKey, final String s) {
            super(uniqueOrKey, s);
        }
        
        protected void checkDuplicateValues() {
            if (this.contains()) {
                XMLSchemaValidatorBase.this.reportSchemaError("DuplicateUnique", new Object[] { this.toString(this.fLocalValues), this.fElementName, this.fIdentityConstraint.getIdentityConstraintName() });
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
        
        public void addMatcher(final XPathMatcher xPathMatcher) {
            this.ensureMatcherCapacity();
            this.fMatchers[this.fMatchersCount++] = xPathMatcher;
        }
        
        public XPathMatcher getMatcherAt(final int n) {
            return this.fMatchers[n];
        }
        
        public void pushContext() {
            this.fContextStack.push(this.fMatchersCount);
        }
        
        public void popContext() {
            this.fMatchersCount = this.fContextStack.pop();
        }
        
        private void ensureMatcherCapacity() {
            if (this.fMatchersCount == this.fMatchers.length) {
                final XPathMatcher[] fMatchers = new XPathMatcher[this.fMatchers.length * 2];
                System.arraycopy(this.fMatchers, 0, fMatchers, 0, this.fMatchers.length);
                this.fMatchers = fMatchers;
            }
        }
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
        
        public void reset(final XMLErrorReporter fErrorReporter) {
            this.fErrorReporter = fErrorReporter;
            this.fErrors.removeAllElements();
            this.fContextCount = 0;
        }
        
        public void pushContext() {
            if (!XMLSchemaValidatorBase.this.fAugPSVI) {
                return;
            }
            if (this.fContextCount == this.fContext.length) {
                final int[] fContext = new int[this.fContextCount + 8];
                System.arraycopy(this.fContext, 0, fContext, 0, this.fContextCount);
                this.fContext = fContext;
            }
            this.fContext[this.fContextCount++] = this.fErrors.size();
        }
        
        public String[] popContext() {
            if (!XMLSchemaValidatorBase.this.fAugPSVI) {
                return null;
            }
            final int[] fContext = this.fContext;
            final int fContextCount = this.fContextCount - 1;
            this.fContextCount = fContextCount;
            final int size = fContext[fContextCount];
            final int n = this.fErrors.size() - size;
            if (n == 0) {
                return null;
            }
            final String[] array = new String[n];
            for (int i = 0; i < n; ++i) {
                array[i] = (String)this.fErrors.elementAt(size + i);
            }
            this.fErrors.setSize(size);
            return array;
        }
        
        public String[] mergeContext() {
            if (!XMLSchemaValidatorBase.this.fAugPSVI) {
                return null;
            }
            final int[] fContext = this.fContext;
            final int fContextCount = this.fContextCount - 1;
            this.fContextCount = fContextCount;
            final int n = fContext[fContextCount];
            final int n2 = this.fErrors.size() - n;
            if (n2 == 0) {
                return null;
            }
            final String[] array = new String[n2];
            for (int i = 0; i < n2; ++i) {
                array[i] = (String)this.fErrors.elementAt(n + i);
            }
            return array;
        }
        
        public void reportError(final String s, final String s2, final Object[] array, final short n) throws XNIException {
            final String reportError = this.fErrorReporter.reportError(s, s2, array, n);
            if (XMLSchemaValidatorBase.this.fAugPSVI) {
                this.fErrors.addElement(s2);
                this.fErrors.addElement(reportError);
            }
        }
        
        public void reportError(final XMLLocator xmlLocator, final String s, final String s2, final Object[] array, final short n) throws XNIException {
            final String reportError = this.fErrorReporter.reportError(xmlLocator, s, s2, array, n);
            if (XMLSchemaValidatorBase.this.fAugPSVI) {
                this.fErrors.addElement(s2);
                this.fErrors.addElement(reportError);
            }
        }
    }
}
