package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.xni.grammars.XMLSchemaDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.impl.dv.xs.DecimalDV;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.util.Locale;
import java.util.Enumeration;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.StringList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.util.StAXLocationWrapper;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXException;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import java.util.Stack;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.w3c.dom.Node;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.util.DefaultErrorHandler;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.util.XSInputSource;
import org.apache.xerces.util.StAXInputSource;
import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.util.DOMInputSource;
import org.apache.xerces.util.URI;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.impl.xs.opti.SchemaParsingConfig;
import org.w3c.dom.Document;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSObject;
import org.w3c.dom.Element;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.dv.xs.TypeValidatorHelper;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xs.datatypes.XSDecimal;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import java.util.Vector;
import org.apache.xerces.impl.xs.XSDeclarationPool;
import java.util.Hashtable;

public class XSDHandler
{
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String CTA_FULL_XPATH = "http://apache.org/xml/features/validation/cta-full-xpath-checking";
    protected static final String ASSERT_COMMENT_PI = "http://apache.org/xml/features/validation/assert-comments-and-pi-checking";
    private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    protected static final String DATATYPE_XML_VERSION = "http://apache.org/xml/properties/validation/schema/datatype-xml-version";
    protected static final boolean DEBUG_NODE_POOL = false;
    static final int ATTRIBUTE_TYPE = 1;
    static final int ATTRIBUTEGROUP_TYPE = 2;
    static final int ELEMENT_TYPE = 3;
    static final int GROUP_TYPE = 4;
    static final int IDENTITYCONSTRAINT_TYPE = 5;
    static final int NOTATION_TYPE = 6;
    static final int TYPEDECL_TYPE = 7;
    public static final String REDEF_IDENTIFIER = "_fn3dktizrknc9pi";
    protected Hashtable fNotationRegistry;
    protected XSDeclarationPool fDeclPool;
    private Hashtable fUnparsedAttributeRegistry;
    private Hashtable fUnparsedAttributeGroupRegistry;
    private Hashtable fUnparsedElementRegistry;
    private Hashtable fUnparsedGroupRegistry;
    private Hashtable fUnparsedIdentityConstraintRegistry;
    private Hashtable fUnparsedNotationRegistry;
    private Hashtable fUnparsedTypeRegistry;
    private Hashtable fUnparsedAttributeRegistrySub;
    private Hashtable fUnparsedAttributeGroupRegistrySub;
    private Hashtable fUnparsedElementRegistrySub;
    private Hashtable fUnparsedGroupRegistrySub;
    private Hashtable fUnparsedIdentityConstraintRegistrySub;
    private Hashtable fUnparsedNotationRegistrySub;
    private Hashtable fUnparsedTypeRegistrySub;
    private Hashtable[] fUnparsedRegistriesExt;
    private Hashtable fXSDocumentInfoRegistry;
    private Hashtable fDependencyMap;
    private Hashtable fImportMap;
    private Vector fAllTNSs;
    private Hashtable fLocationPairs;
    private static final Hashtable EMPTY_TABLE;
    Hashtable fHiddenNodes;
    private static final String XSD_VERSION_1_0 = "1.0";
    private static final String XSD_VERSION_1_1 = "1.1";
    private static final TypeValidator DECIMAL_DV;
    private static final XSDecimal SUPPORTED_VERSION_1_0;
    private static final XSDecimal SUPPORTED_VERSION_1_1;
    private XSDecimal fSupportedVersion;
    private Hashtable fTraversed;
    private Hashtable fDoc2SystemId;
    private Hashtable fDoc2DatatypeXMLVersion;
    private XSDocumentInfo fRoot;
    private Hashtable fDoc2XSDocumentMap;
    private Hashtable fRedefine2XSDMap;
    private Hashtable fOverrideDependencyMap;
    private Hashtable fOverrideDependencyMapNsNormalization;
    private Hashtable fRedefine2NSSupport;
    private Hashtable fRedefinedRestrictedAttributeGroupRegistry;
    private Hashtable fRedefinedRestrictedGroupRegistry;
    private boolean fLastSchemaWasDuplicate;
    private boolean fValidateAnnotations;
    private boolean fHonourAllSchemaLocations;
    boolean fNamespaceGrowth;
    boolean fTolerateDuplicates;
    boolean fFullXPathForCTA;
    boolean fCommentsAndPIsForAssert;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityResolver fEntityResolver;
    private XSAttributeChecker fAttributeChecker;
    private SymbolTable fSymbolTable;
    private XSGrammarBucket fGrammarBucket;
    private XSDDescription fSchemaGrammarDescription;
    private XMLGrammarPool fGrammarPool;
    private OverrideTransformationManager fOverrideHandler;
    XSDAttributeGroupTraverser fAttributeGroupTraverser;
    XSDAttributeTraverser fAttributeTraverser;
    XSDComplexTypeTraverser fComplexTypeTraverser;
    XSDElementTraverser fElementTraverser;
    XSDGroupTraverser fGroupTraverser;
    XSDKeyrefTraverser fKeyrefTraverser;
    XSDNotationTraverser fNotationTraverser;
    XSDSimpleTypeTraverser fSimpleTypeTraverser;
    XSDUniqueOrKeyTraverser fUniqueOrKeyTraverser;
    XSDWildcardTraverser fWildCardTraverser;
    XSDTypeAlternativeTraverser fTypeAlternativeTraverser;
    SchemaDVFactory fDVFactory;
    SchemaDOMParser fSchemaParser;
    SchemaContentHandler fXSContentHandler;
    StAXSchemaParser fStAXSchemaParser;
    XML11Configuration fAnnotationValidator;
    XSAnnotationGrammarPool fGrammarBucketAdapter;
    short fSchemaVersion;
    XSConstraints fXSConstraints;
    TypeValidatorHelper fTypeValidatorHelper;
    String fDatatypeXMLVersion;
    private static final int INIT_STACK_SIZE = 30;
    private static final int INC_STACK_SIZE = 10;
    private int fLocalElemStackPos;
    private XSParticleDecl[] fParticle;
    private Element[] fLocalElementDecl;
    private XSDocumentInfo[] fLocalElementDecl_schema;
    private int[] fAllContext;
    private XSObject[] fParent;
    private String[][] fLocalElemNamespaceContext;
    private static final int INIT_KEYREF_STACK = 2;
    private static final int INC_KEYREF_STACK_AMOUNT = 2;
    private int fKeyrefStackPos;
    private Element[] fKeyrefs;
    private XSDocumentInfo[] fKeyrefsMapXSDocumentInfo;
    private XSElementDecl[] fKeyrefElems;
    private String[][] fKeyrefNamespaceContext;
    SymbolHash fGlobalAttrDecls;
    SymbolHash fGlobalAttrGrpDecls;
    SymbolHash fGlobalElemDecls;
    SymbolHash fGlobalGroupDecls;
    SymbolHash fGlobalNotationDecls;
    SymbolHash fGlobalIDConstraintDecls;
    SymbolHash fGlobalTypeDecls;
    private static final int INIT_IC_REFERRAL_STACK = 2;
    private static final int INC_IC_REFERRAL_STACK_AMOUNT = 2;
    private int fICReferralStackPos;
    private Element[] fICReferrals;
    private XSDocumentInfo[] fICReferralsMapXSDocumentInfo;
    private XSElementDecl[] fICReferralElems;
    private String[][] fICReferralNamespaceContext;
    private static final String[][] NS_ERROR_CODES;
    private static final String[] ELE_ERROR_CODES;
    private Vector fReportedTNS;
    private static final String[] COMP_TYPE;
    private static final String[] CIRCULAR_CODES;
    private SimpleLocator xl;
    
    private static XSDecimal getSupportedVersion(final String s) {
        XSDecimal xsDecimal = null;
        try {
            xsDecimal = (XSDecimal)XSDHandler.DECIMAL_DV.getActualValue(s, null);
        }
        catch (final InvalidDatatypeValueException ex) {}
        return xsDecimal;
    }
    
    private String null2EmptyString(final String s) {
        return (s == null) ? XMLSymbols.EMPTY_STRING : s;
    }
    
    private String emptyString2Null(final String s) {
        return (s == XMLSymbols.EMPTY_STRING) ? null : s;
    }
    
    private String doc2SystemId(final Element element) {
        String s = null;
        final Document ownerDocument = element.getOwnerDocument();
        if (ownerDocument instanceof SchemaDOM) {
            s = ((SchemaDOM)ownerDocument).getDocumentURI();
        }
        else if (ownerDocument.getImplementation().hasFeature("Core", "3.0")) {
            s = ownerDocument.getDocumentURI();
        }
        return (s != null) ? s : this.fDoc2SystemId.get(element);
    }
    
    public XSDHandler(final short fSchemaVersion, final XSConstraints fxsConstraints) {
        this.fNotationRegistry = new Hashtable();
        this.fDeclPool = null;
        this.fUnparsedAttributeRegistry = new Hashtable();
        this.fUnparsedAttributeGroupRegistry = new Hashtable();
        this.fUnparsedElementRegistry = new Hashtable();
        this.fUnparsedGroupRegistry = new Hashtable();
        this.fUnparsedIdentityConstraintRegistry = new Hashtable();
        this.fUnparsedNotationRegistry = new Hashtable();
        this.fUnparsedTypeRegistry = new Hashtable();
        this.fUnparsedAttributeRegistrySub = new Hashtable();
        this.fUnparsedAttributeGroupRegistrySub = new Hashtable();
        this.fUnparsedElementRegistrySub = new Hashtable();
        this.fUnparsedGroupRegistrySub = new Hashtable();
        this.fUnparsedIdentityConstraintRegistrySub = new Hashtable();
        this.fUnparsedNotationRegistrySub = new Hashtable();
        this.fUnparsedTypeRegistrySub = new Hashtable();
        this.fUnparsedRegistriesExt = new Hashtable[] { null, new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable() };
        this.fXSDocumentInfoRegistry = new Hashtable();
        this.fDependencyMap = new Hashtable();
        this.fImportMap = new Hashtable();
        this.fAllTNSs = new Vector();
        this.fLocationPairs = null;
        this.fHiddenNodes = null;
        this.fSupportedVersion = XSDHandler.SUPPORTED_VERSION_1_0;
        this.fTraversed = new Hashtable();
        this.fDoc2SystemId = new Hashtable();
        this.fDoc2DatatypeXMLVersion = null;
        this.fRoot = null;
        this.fDoc2XSDocumentMap = new Hashtable();
        this.fRedefine2XSDMap = new Hashtable();
        this.fOverrideDependencyMap = new Hashtable();
        this.fOverrideDependencyMapNsNormalization = new Hashtable();
        this.fRedefine2NSSupport = new Hashtable();
        this.fRedefinedRestrictedAttributeGroupRegistry = new Hashtable();
        this.fRedefinedRestrictedGroupRegistry = new Hashtable();
        this.fValidateAnnotations = false;
        this.fHonourAllSchemaLocations = false;
        this.fNamespaceGrowth = false;
        this.fTolerateDuplicates = false;
        this.fFullXPathForCTA = false;
        this.fCommentsAndPIsForAssert = false;
        this.fLocalElemStackPos = 0;
        this.fParticle = new XSParticleDecl[30];
        this.fLocalElementDecl = new Element[30];
        this.fLocalElementDecl_schema = new XSDocumentInfo[30];
        this.fAllContext = new int[30];
        this.fParent = new XSObject[30];
        this.fLocalElemNamespaceContext = new String[30][1];
        this.fKeyrefStackPos = 0;
        this.fKeyrefs = new Element[2];
        this.fKeyrefsMapXSDocumentInfo = new XSDocumentInfo[2];
        this.fKeyrefElems = new XSElementDecl[2];
        this.fKeyrefNamespaceContext = new String[2][1];
        this.fGlobalAttrDecls = new SymbolHash(12);
        this.fGlobalAttrGrpDecls = new SymbolHash(5);
        this.fGlobalElemDecls = new SymbolHash(25);
        this.fGlobalGroupDecls = new SymbolHash(5);
        this.fGlobalNotationDecls = new SymbolHash(1);
        this.fGlobalIDConstraintDecls = new SymbolHash(3);
        this.fGlobalTypeDecls = new SymbolHash(25);
        this.fICReferralStackPos = 0;
        this.fICReferrals = new Element[2];
        this.fICReferralsMapXSDocumentInfo = new XSDocumentInfo[2];
        this.fICReferralElems = new XSElementDecl[2];
        this.fICReferralNamespaceContext = new String[2][1];
        this.fReportedTNS = null;
        this.xl = new SimpleLocator();
        this.fSchemaVersion = fSchemaVersion;
        this.fXSConstraints = fxsConstraints;
        this.fHiddenNodes = new Hashtable();
        (this.fSchemaParser = new SchemaDOMParser(new SchemaParsingConfig())).setSupportedVersion(this.fSupportedVersion);
    }
    
    public XSDHandler(final XSGrammarBucket fGrammarBucket, final short n, final XSConstraints xsConstraints) {
        this(n, xsConstraints);
        this.fGrammarBucket = fGrammarBucket;
        this.fSchemaGrammarDescription = new XSDDescription();
    }
    
    public SchemaGrammar parseSchema(final XMLInputSource xmlInputSource, final XSDDescription xsdDescription, final Hashtable fLocationPairs) throws IOException {
        this.fLocationPairs = fLocationPairs;
        this.fSchemaParser.resetNodePool();
        SchemaGrammar schemaGrammar = null;
        String s = null;
        final short contextType = xsdDescription.getContextType();
        if (contextType != 3) {
            if (this.fHonourAllSchemaLocations && contextType == 2 && this.isExistingGrammar(xsdDescription, this.fNamespaceGrowth)) {
                schemaGrammar = this.fGrammarBucket.getGrammar(xsdDescription.getTargetNamespace());
            }
            else {
                schemaGrammar = this.findGrammar(xsdDescription, this.fNamespaceGrowth);
            }
            if (schemaGrammar != null) {
                if (!this.fNamespaceGrowth) {
                    return schemaGrammar;
                }
                try {
                    if (schemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xmlInputSource.getSystemId(), xmlInputSource.getBaseSystemId(), false))) {
                        return schemaGrammar;
                    }
                }
                catch (final URI.MalformedURIException ex) {}
            }
            s = xsdDescription.getTargetNamespace();
            if (s != null) {
                s = this.fSymbolTable.addSymbol(s);
            }
        }
        this.prepareForParse();
        Element element;
        if (xmlInputSource instanceof DOMInputSource) {
            element = this.getSchemaDocument(s, (DOMInputSource)xmlInputSource, contextType == 3, contextType, null);
        }
        else if (xmlInputSource instanceof SAXInputSource) {
            element = this.getSchemaDocument(s, (SAXInputSource)xmlInputSource, contextType == 3, contextType, null);
        }
        else if (xmlInputSource instanceof StAXInputSource) {
            element = this.getSchemaDocument(s, (StAXInputSource)xmlInputSource, contextType == 3, contextType, null);
        }
        else if (xmlInputSource instanceof XSInputSource) {
            element = this.getSchemaDocument((XSInputSource)xmlInputSource, xsdDescription);
        }
        else {
            element = this.getSchemaDocument(s, xmlInputSource, contextType == 3, contextType, null);
        }
        if (element == null) {
            if (xmlInputSource instanceof XSInputSource) {
                final XSInputSource xsInputSource = (XSInputSource)xmlInputSource;
                final SchemaGrammar[] grammars = xsInputSource.getGrammars();
                if (grammars != null && grammars.length > 0) {
                    schemaGrammar = this.fGrammarBucket.getGrammar(grammars[0].getTargetNamespace());
                }
                else {
                    final XSObject[] components = xsInputSource.getComponents();
                    if (components != null && components.length > 0) {
                        schemaGrammar = this.fGrammarBucket.getGrammar(components[0].getNamespace());
                    }
                }
            }
            return schemaGrammar;
        }
        if (contextType == 3) {
            final String attrValue = DOMUtil.getAttrValue(element, SchemaSymbols.ATT_TARGETNAMESPACE);
            String addSymbol;
            if (attrValue != null && attrValue.length() > 0) {
                addSymbol = this.fSymbolTable.addSymbol(attrValue);
                xsdDescription.setTargetNamespace(addSymbol);
            }
            else {
                addSymbol = null;
            }
            schemaGrammar = this.findGrammar(xsdDescription, this.fNamespaceGrowth);
            final String expandSystemId = XMLEntityManager.expandSystemId(xmlInputSource.getSystemId(), xmlInputSource.getBaseSystemId(), false);
            if (schemaGrammar != null && (!this.fNamespaceGrowth || (expandSystemId != null && schemaGrammar.getDocumentLocations().contains(expandSystemId)))) {
                return schemaGrammar;
            }
            this.fTraversed.put(new XSDKey(expandSystemId, contextType, addSymbol), element);
            if (expandSystemId != null) {
                this.fDoc2SystemId.put(element, expandSystemId);
            }
        }
        this.prepareForTraverse();
        if (this.fSchemaVersion == 4) {
            this.fOverrideHandler.addSchemaRoot((String)this.fDoc2SystemId.get(element), element);
        }
        this.fRoot = this.constructTrees(element, xmlInputSource.getSystemId(), xsdDescription, schemaGrammar != null);
        if (this.fRoot == null) {
            return null;
        }
        this.buildGlobalNameRegistries();
        if (this.fSchemaVersion == 4) {
            this.buildDefaultAttributes();
        }
        final ArrayList list = this.fValidateAnnotations ? new ArrayList() : null;
        this.traverseSchemas(list);
        this.traverseLocalElements();
        this.resolveKeyRefs();
        this.resolveIdentityConstraintReferrals();
        for (int i = this.fAllTNSs.size() - 1; i >= 0; --i) {
            final String s2 = this.fAllTNSs.elementAt(i);
            final Vector importedGrammars = this.fImportMap.get(s2);
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(this.emptyString2Null(s2));
            if (grammar != null) {
                int size = 0;
                for (int j = 0; j < importedGrammars.size(); ++j) {
                    final SchemaGrammar grammar2 = this.fGrammarBucket.getGrammar((String)importedGrammars.elementAt(j));
                    if (grammar2 != null) {
                        importedGrammars.setElementAt(grammar2, size++);
                    }
                }
                importedGrammars.setSize(size);
                grammar.setImportedGrammars(importedGrammars);
            }
        }
        if (this.fValidateAnnotations && list.size() > 0) {
            this.validateAnnotations(list);
        }
        return this.fGrammarBucket.getGrammar(this.fRoot.fTargetNamespace);
    }
    
    private void validateAnnotations(final ArrayList list) {
        if (this.fAnnotationValidator == null) {
            this.createAnnotationValidator();
        }
        final int size = list.size();
        final XMLInputSource xmlInputSource = new XMLInputSource(null, null, null);
        this.fGrammarBucketAdapter.refreshGrammars(this.fGrammarBucket);
        for (int i = 0; i < size; i += 2) {
            xmlInputSource.setSystemId((String)list.get(i));
            for (XSAnnotationInfo next = list.get(i + 1); next != null; next = next.next) {
                xmlInputSource.setCharacterStream(new StringReader(next.fAnnotation));
                try {
                    this.fAnnotationValidator.parse(xmlInputSource);
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    private void createAnnotationValidator() {
        this.fAnnotationValidator = new XML11Configuration();
        this.fGrammarBucketAdapter = new XSAnnotationGrammarPool(this.fSchemaVersion);
        this.fAnnotationValidator.setFeature("http://xml.org/sax/features/validation", true);
        this.fAnnotationValidator.setFeature("http://apache.org/xml/features/validation/schema", true);
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarBucketAdapter);
        final XMLErrorHandler errorHandler = this.fErrorReporter.getErrorHandler();
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", (errorHandler != null) ? errorHandler : new DefaultErrorHandler());
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", this.fErrorReporter.getLocale());
    }
    
    SchemaGrammar getGrammar(final String s) {
        return this.fGrammarBucket.getGrammar(s);
    }
    
    protected SchemaGrammar findGrammar(final XSDDescription xsdDescription, final boolean b) {
        SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsdDescription.getTargetNamespace());
        if (grammar == null && this.fGrammarPool != null) {
            grammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(xsdDescription);
            if (grammar != null && !this.fGrammarBucket.putGrammar(grammar, true, b)) {
                this.reportSchemaWarning("GrammarConflict", null, null);
                grammar = null;
            }
        }
        return grammar;
    }
    
    protected XSDocumentInfo constructTrees(final Element element, final String s, final XSDDescription xsdDescription, final boolean b) {
        if (element == null) {
            return null;
        }
        String s2 = xsdDescription.getTargetNamespace();
        final short contextType = xsdDescription.getContextType();
        XSDocumentInfo xsDocumentInfo;
        try {
            short n = 1;
            if (this.fSchemaVersion == 4) {
                n = (short)("1.1".equals((this.fDatatypeXMLVersion == null) ? this.fDoc2DatatypeXMLVersion.get(element) : this.fDatatypeXMLVersion) ? 2 : 1);
            }
            xsDocumentInfo = new XSDocumentInfo(element, this.fAttributeChecker, this.fSymbolTable, this.fTypeValidatorHelper, n);
        }
        catch (final XMLSchemaException ex) {
            this.reportSchemaError(XSDHandler.ELE_ERROR_CODES[contextType], new Object[] { s }, element);
            return null;
        }
        if (xsDocumentInfo.fTargetNamespace != null && xsDocumentInfo.fTargetNamespace.length() == 0) {
            this.reportSchemaWarning("EmptyTargetNamespace", new Object[] { s }, element);
            xsDocumentInfo.fTargetNamespace = null;
        }
        if (s2 != null) {
            final int n2 = 0;
            if (contextType == 0 || contextType == 1 || contextType == 8) {
                if (xsDocumentInfo.fTargetNamespace == null) {
                    xsDocumentInfo.fTargetNamespace = s2;
                    xsDocumentInfo.fIsChameleonSchema = true;
                }
                else if (s2 != xsDocumentInfo.fTargetNamespace) {
                    this.reportSchemaError(XSDHandler.NS_ERROR_CODES[contextType][n2], new Object[] { s2, xsDocumentInfo.fTargetNamespace }, element);
                    return null;
                }
            }
            else if (contextType != 3 && s2 != xsDocumentInfo.fTargetNamespace) {
                this.reportSchemaError(XSDHandler.NS_ERROR_CODES[contextType][n2], new Object[] { s2, xsDocumentInfo.fTargetNamespace }, element);
                return null;
            }
        }
        else if (xsDocumentInfo.fTargetNamespace != null) {
            if (contextType != 3) {
                this.reportSchemaError(XSDHandler.NS_ERROR_CODES[contextType][1], new Object[] { s2, xsDocumentInfo.fTargetNamespace }, element);
                return null;
            }
            xsdDescription.setTargetNamespace(xsDocumentInfo.fTargetNamespace);
            s2 = xsDocumentInfo.fTargetNamespace;
        }
        xsDocumentInfo.addAllowedNS(xsDocumentInfo.fTargetNamespace);
        SchemaGrammar schemaGrammar;
        if (b) {
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace);
            if (grammar.isImmutable()) {
                schemaGrammar = new SchemaGrammar(grammar);
                this.fGrammarBucket.putGrammar(schemaGrammar);
                this.updateImportListWith(schemaGrammar);
            }
            else {
                schemaGrammar = grammar;
            }
            this.updateImportListFor(schemaGrammar);
        }
        else if (contextType == 0 || contextType == 1 || contextType == 8) {
            schemaGrammar = this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace);
        }
        else if (this.fHonourAllSchemaLocations && contextType == 2) {
            schemaGrammar = this.findGrammar(xsdDescription, false);
            if (schemaGrammar == null) {
                schemaGrammar = new SchemaGrammar(xsDocumentInfo.fTargetNamespace, xsdDescription.makeClone(), this.fSymbolTable, this.fSchemaVersion);
                this.fGrammarBucket.putGrammar(schemaGrammar);
            }
        }
        else {
            schemaGrammar = new SchemaGrammar(xsDocumentInfo.fTargetNamespace, xsdDescription.makeClone(), this.fSymbolTable, this.fSchemaVersion);
            this.fGrammarBucket.putGrammar(schemaGrammar);
        }
        schemaGrammar.addDocument(null, this.fDoc2SystemId.get(xsDocumentInfo.fSchemaElement));
        this.fDoc2XSDocumentMap.put(element, xsDocumentInfo);
        final Vector vector = new Vector();
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        Element element2 = null;
        for (Element element3 = DOMUtil.getFirstChildElement(element); element3 != null; element3 = DOMUtil.getNextSiblingElement(element3)) {
            final String localName = DOMUtil.getLocalName(element3);
            boolean b2 = false;
            if (!localName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                String literalSystemId;
                if (localName.equals(SchemaSymbols.ELT_IMPORT)) {
                    final Object[] checkAttributes = this.fAttributeChecker.checkAttributes(element3, true, xsDocumentInfo);
                    literalSystemId = (String)checkAttributes[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
                    String addSymbol = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAMESPACE];
                    if (addSymbol != null) {
                        addSymbol = this.fSymbolTable.addSymbol(addSymbol);
                    }
                    final Element firstChildElement = DOMUtil.getFirstChildElement(element3);
                    if (firstChildElement != null) {
                        final String localName2 = DOMUtil.getLocalName(firstChildElement);
                        if (localName2.equals(SchemaSymbols.ELT_ANNOTATION)) {
                            schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(firstChildElement, checkAttributes, true, xsDocumentInfo));
                        }
                        else {
                            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", localName2 }, element3);
                        }
                        if (DOMUtil.getNextSiblingElement(firstChildElement) != null) {
                            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(firstChildElement)) }, element3);
                        }
                    }
                    else {
                        final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element3);
                        if (syntheticAnnotation != null) {
                            schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element3, syntheticAnnotation, checkAttributes, true, xsDocumentInfo));
                        }
                    }
                    this.fAttributeChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    if (addSymbol == xsDocumentInfo.fTargetNamespace) {
                        this.reportSchemaError((addSymbol != null) ? "src-import.1.1" : "src-import.1.2", new Object[] { addSymbol }, element3);
                        continue;
                    }
                    if (xsDocumentInfo.isAllowedNS(addSymbol)) {
                        if (!this.fHonourAllSchemaLocations && !this.fNamespaceGrowth) {
                            continue;
                        }
                    }
                    else {
                        xsDocumentInfo.addAllowedNS(addSymbol);
                    }
                    final String null2EmptyString = this.null2EmptyString(xsDocumentInfo.fTargetNamespace);
                    final Vector vector4 = this.fImportMap.get(null2EmptyString);
                    if (vector4 == null) {
                        this.fAllTNSs.addElement(null2EmptyString);
                        final Vector vector5 = new Vector();
                        this.fImportMap.put(null2EmptyString, vector5);
                        vector5.addElement(addSymbol);
                    }
                    else if (!vector4.contains(addSymbol)) {
                        vector4.addElement(addSymbol);
                    }
                    this.fSchemaGrammarDescription.reset();
                    this.fSchemaGrammarDescription.setContextType((short)2);
                    this.fSchemaGrammarDescription.setBaseSystemId(this.doc2SystemId(element));
                    this.fSchemaGrammarDescription.setLiteralSystemId(literalSystemId);
                    this.fSchemaGrammarDescription.setLocationHints(new String[] { literalSystemId });
                    this.fSchemaGrammarDescription.setTargetNamespace(addSymbol);
                    final SchemaGrammar grammar2 = this.findGrammar(this.fSchemaGrammarDescription, this.fNamespaceGrowth);
                    if (grammar2 != null) {
                        if (this.fNamespaceGrowth) {
                            try {
                                if (grammar2.getDocumentLocations().contains(XMLEntityManager.expandSystemId(literalSystemId, this.fSchemaGrammarDescription.getBaseSystemId(), false))) {
                                    continue;
                                }
                                b2 = true;
                            }
                            catch (final URI.MalformedURIException ex2) {}
                        }
                        else {
                            if (!this.fHonourAllSchemaLocations) {
                                continue;
                            }
                            if (this.isExistingGrammar(this.fSchemaGrammarDescription, false)) {
                                continue;
                            }
                        }
                    }
                    element2 = this.resolveSchema(this.fSchemaGrammarDescription, false, element3, grammar2 == null);
                }
                else {
                    if (!localName.equals(SchemaSymbols.ELT_INCLUDE) && !localName.equals(SchemaSymbols.ELT_REDEFINE) && (!localName.equals(SchemaSymbols.ELT_OVERRIDE) || this.fSchemaVersion != 4)) {
                        break;
                    }
                    final Object[] checkAttributes2 = this.fAttributeChecker.checkAttributes(element3, true, xsDocumentInfo);
                    literalSystemId = (String)checkAttributes2[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
                    if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
                        this.fRedefine2NSSupport.put(element3, new SchemaNamespaceSupport(xsDocumentInfo.fNamespaceSupport));
                    }
                    if (localName.equals(SchemaSymbols.ELT_INCLUDE)) {
                        final Element firstChildElement2 = DOMUtil.getFirstChildElement(element3);
                        if (firstChildElement2 != null) {
                            final String localName3 = DOMUtil.getLocalName(firstChildElement2);
                            if (localName3.equals(SchemaSymbols.ELT_ANNOTATION)) {
                                schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(firstChildElement2, checkAttributes2, true, xsDocumentInfo));
                            }
                            else {
                                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", localName3 }, element3);
                            }
                            if (DOMUtil.getNextSiblingElement(firstChildElement2) != null) {
                                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(firstChildElement2)) }, element3);
                            }
                        }
                        else {
                            final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element3);
                            if (syntheticAnnotation2 != null) {
                                schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element3, syntheticAnnotation2, checkAttributes2, true, xsDocumentInfo));
                            }
                        }
                    }
                    else {
                        for (Element element4 = DOMUtil.getFirstChildElement(element3); element4 != null; element4 = DOMUtil.getNextSiblingElement(element4)) {
                            if (DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
                                schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(element4, checkAttributes2, true, xsDocumentInfo));
                                DOMUtil.setHidden(element4, this.fHiddenNodes);
                            }
                            else {
                                final String syntheticAnnotation3 = DOMUtil.getSyntheticAnnotation(element3);
                                if (syntheticAnnotation3 != null) {
                                    schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element3, syntheticAnnotation3, checkAttributes2, true, xsDocumentInfo));
                                }
                            }
                        }
                    }
                    this.fAttributeChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    if (literalSystemId == null) {
                        this.reportSchemaError("s4s-att-must-appear", new Object[] { "<include> or <redefine>", "schemaLocation" }, element3);
                    }
                    boolean b3 = false;
                    short contextType2 = 0;
                    if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
                        b3 = this.nonAnnotationContent(element3);
                        contextType2 = 1;
                    }
                    else if (localName.equals(SchemaSymbols.ELT_OVERRIDE)) {
                        b3 = this.nonAnnotationContent(element3);
                        contextType2 = 8;
                    }
                    this.fSchemaGrammarDescription.reset();
                    this.fSchemaGrammarDescription.setContextType(contextType2);
                    this.fSchemaGrammarDescription.setBaseSystemId(this.doc2SystemId(element));
                    this.fSchemaGrammarDescription.setLocationHints(new String[] { literalSystemId });
                    this.fSchemaGrammarDescription.setTargetNamespace(s2);
                    final XMLInputSource resolveSchemaSource = this.resolveSchemaSource(this.fSchemaGrammarDescription, b3, element3, true);
                    String expandSystemId = null;
                    try {
                        expandSystemId = XMLEntityManager.expandSystemId(resolveSchemaSource.getSystemId(), resolveSchemaSource.getBaseSystemId(), false);
                    }
                    catch (final URI.MalformedURIException ex3) {}
                    if (!this.fNamespaceGrowth || !schemaGrammar.getDocumentLocations().contains(expandSystemId)) {
                        element2 = this.resolveSchema(resolveSchemaSource, this.fSchemaGrammarDescription, b3, element3);
                        final String fTargetNamespace = xsDocumentInfo.fTargetNamespace;
                        if (this.fSchemaVersion == 4) {
                            if (contextType2 == 8) {
                                if (element2 != null && this.isValidTargetUriForIncludeOrOverride(expandSystemId, s)) {
                                    final Element transform = this.fOverrideHandler.transform(expandSystemId, element3, element2);
                                    if (transform == null) {
                                        this.fLastSchemaWasDuplicate = true;
                                    }
                                    else if (this.fLastSchemaWasDuplicate && transform != element2) {
                                        this.fLastSchemaWasDuplicate = false;
                                    }
                                    element2 = transform;
                                }
                                else {
                                    this.fOverrideHandler.checkSchemaRoot(expandSystemId, element3, element2);
                                }
                            }
                            else if (contextType2 == 0 && !this.isValidTargetUriForIncludeOrOverride(expandSystemId, s)) {
                                this.fLastSchemaWasDuplicate = true;
                            }
                        }
                    }
                    else {
                        this.fLastSchemaWasDuplicate = true;
                    }
                }
                XSDocumentInfo constructTrees;
                if (this.fLastSchemaWasDuplicate) {
                    constructTrees = ((element2 == null) ? null : this.fDoc2XSDocumentMap.get(element2));
                }
                else {
                    constructTrees = this.constructTrees(element2, literalSystemId, this.fSchemaGrammarDescription, b2);
                }
                if (localName.equals(SchemaSymbols.ELT_REDEFINE) && constructTrees != null) {
                    this.fRedefine2XSDMap.put(element3, constructTrees);
                }
                if (this.fSchemaVersion == 4 && localName.equals(SchemaSymbols.ELT_OVERRIDE) && constructTrees != null) {
                    vector2.addElement(constructTrees);
                    vector3.addElement(element3);
                }
                if (element2 != null) {
                    if (constructTrees != null) {
                        vector.addElement(constructTrees);
                    }
                    element2 = null;
                }
            }
        }
        if (this.fSchemaVersion == 4) {
            this.fOverrideDependencyMap.put(xsDocumentInfo, vector2);
            this.fOverrideDependencyMapNsNormalization.put(xsDocumentInfo, vector3);
        }
        this.fDependencyMap.put(xsDocumentInfo, vector);
        return xsDocumentInfo;
    }
    
    private boolean isValidTargetUriForIncludeOrOverride(final String s, final String s2) {
        boolean b;
        try {
            final String expandSystemId = XMLEntityManager.expandSystemId(s2, this.fSchemaGrammarDescription.getBaseSystemId(), false);
            b = (!"".equals(s) && expandSystemId != null && !expandSystemId.equals(s));
        }
        catch (final URI.MalformedURIException ex) {
            b = false;
        }
        return b;
    }
    
    private boolean isExistingGrammar(final XSDDescription xsdDescription, final boolean b) {
        final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsdDescription.getTargetNamespace());
        if (grammar == null) {
            return this.findGrammar(xsdDescription, b) != null;
        }
        if (grammar.isImmutable()) {
            return true;
        }
        try {
            return grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xsdDescription.getLiteralSystemId(), xsdDescription.getBaseSystemId(), false));
        }
        catch (final URI.MalformedURIException ex) {
            return false;
        }
    }
    
    private void updateImportListFor(final SchemaGrammar schemaGrammar) {
        final Vector importedGrammars = schemaGrammar.getImportedGrammars();
        if (importedGrammars != null) {
            for (int i = 0; i < importedGrammars.size(); ++i) {
                final SchemaGrammar schemaGrammar2 = importedGrammars.elementAt(i);
                final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(schemaGrammar2.getTargetNamespace());
                if (grammar != null && schemaGrammar2 != grammar) {
                    importedGrammars.set(i, grammar);
                }
            }
        }
    }
    
    private void updateImportListWith(final SchemaGrammar schemaGrammar) {
        final SchemaGrammar[] grammars = this.fGrammarBucket.getGrammars();
        for (int i = 0; i < grammars.length; ++i) {
            final SchemaGrammar schemaGrammar2 = grammars[i];
            if (schemaGrammar2 != schemaGrammar) {
                final Vector importedGrammars = schemaGrammar2.getImportedGrammars();
                if (importedGrammars != null) {
                    int j = 0;
                    while (j < importedGrammars.size()) {
                        final SchemaGrammar schemaGrammar3 = importedGrammars.elementAt(j);
                        if (this.null2EmptyString(schemaGrammar3.getTargetNamespace()).equals(this.null2EmptyString(schemaGrammar.getTargetNamespace()))) {
                            if (schemaGrammar3 != schemaGrammar) {
                                importedGrammars.set(j, schemaGrammar);
                                break;
                            }
                            break;
                        }
                        else {
                            ++j;
                        }
                    }
                }
            }
        }
    }
    
    protected void buildGlobalNameRegistries() {
        final Stack stack = new Stack();
        stack.push(this.fRoot);
        while (!stack.empty()) {
            final XSDocumentInfo xsDocumentInfo = stack.pop();
            final Element fSchemaElement = xsDocumentInfo.fSchemaElement;
            if (DOMUtil.isHidden(fSchemaElement, this.fHiddenNodes)) {
                continue;
            }
            final Element element = fSchemaElement;
            int n = 1;
            for (Element element2 = DOMUtil.getFirstChildElement(element); element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
                if (!DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_INCLUDE) || DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_IMPORT) || (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_OVERRIDE) && this.fSchemaVersion == 4)) {
                        if (n == 0) {
                            this.reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName(element2) }, element2);
                        }
                        DOMUtil.setHidden(element2, this.fHiddenNodes);
                    }
                    else if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_REDEFINE)) {
                        if (n == 0) {
                            this.reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName(element2) }, element2);
                        }
                        for (Element element3 = DOMUtil.getFirstChildElement(element2); element3 != null; element3 = DOMUtil.getNextSiblingElement(element3)) {
                            final String attrValue = DOMUtil.getAttrValue(element3, SchemaSymbols.ATT_NAME);
                            if (attrValue.length() != 0) {
                                final String s = (xsDocumentInfo.fTargetNamespace == null) ? ("," + attrValue) : (xsDocumentInfo.fTargetNamespace + "," + attrValue);
                                final String localName = DOMUtil.getLocalName(element3);
                                if (localName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                                    this.checkForDuplicateNames(s, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, element3, xsDocumentInfo);
                                    this.renameRedefiningComponents(xsDocumentInfo, element3, SchemaSymbols.ELT_ATTRIBUTEGROUP, attrValue, DOMUtil.getAttrValue(element3, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi");
                                }
                                else if (localName.equals(SchemaSymbols.ELT_COMPLEXTYPE) || localName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                                    this.checkForDuplicateNames(s, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, element3, xsDocumentInfo);
                                    final String string = DOMUtil.getAttrValue(element3, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                                    if (localName.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                                        this.renameRedefiningComponents(xsDocumentInfo, element3, SchemaSymbols.ELT_COMPLEXTYPE, attrValue, string);
                                    }
                                    else {
                                        this.renameRedefiningComponents(xsDocumentInfo, element3, SchemaSymbols.ELT_SIMPLETYPE, attrValue, string);
                                    }
                                }
                                else if (localName.equals(SchemaSymbols.ELT_GROUP)) {
                                    this.checkForDuplicateNames(s, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, element3, xsDocumentInfo);
                                    this.renameRedefiningComponents(xsDocumentInfo, element3, SchemaSymbols.ELT_GROUP, attrValue, DOMUtil.getAttrValue(element3, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi");
                                }
                            }
                        }
                    }
                    else {
                        final String attrValue2 = DOMUtil.getAttrValue(element2, SchemaSymbols.ATT_NAME);
                        final String localName2 = DOMUtil.getLocalName(element2);
                        if (this.fSchemaVersion >= 4 && localName2.equals(SchemaSymbols.ELT_DEFAULTOPENCONTENT)) {
                            if (n == 0) {
                                this.reportSchemaError("s4s-elt-invalid-content.3", new Object[] { localName2 }, element2);
                            }
                            xsDocumentInfo.fDefaultOpenContent = this.fComplexTypeTraverser.traverseOpenContent(element2, xsDocumentInfo, this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace), true);
                            DOMUtil.setHidden(element2, this.fHiddenNodes);
                            n = 0;
                        }
                        else {
                            n = 0;
                            if (attrValue2.length() != 0) {
                                final String s2 = (xsDocumentInfo.fTargetNamespace == null) ? ("," + attrValue2) : (xsDocumentInfo.fTargetNamespace + "," + attrValue2);
                                if (localName2.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                                    this.checkForDuplicateNames(s2, 1, this.fUnparsedAttributeRegistry, this.fUnparsedAttributeRegistrySub, element2, xsDocumentInfo);
                                }
                                else if (localName2.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                                    this.checkForDuplicateNames(s2, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, element2, xsDocumentInfo);
                                }
                                else if (localName2.equals(SchemaSymbols.ELT_COMPLEXTYPE) || localName2.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                                    this.checkForDuplicateNames(s2, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, element2, xsDocumentInfo);
                                }
                                else if (localName2.equals(SchemaSymbols.ELT_ELEMENT)) {
                                    this.checkForDuplicateNames(s2, 3, this.fUnparsedElementRegistry, this.fUnparsedElementRegistrySub, element2, xsDocumentInfo);
                                }
                                else if (localName2.equals(SchemaSymbols.ELT_GROUP)) {
                                    this.checkForDuplicateNames(s2, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, element2, xsDocumentInfo);
                                }
                                else if (localName2.equals(SchemaSymbols.ELT_NOTATION)) {
                                    this.checkForDuplicateNames(s2, 6, this.fUnparsedNotationRegistry, this.fUnparsedNotationRegistrySub, element2, xsDocumentInfo);
                                }
                            }
                        }
                    }
                }
            }
            DOMUtil.setHidden(fSchemaElement, this.fHiddenNodes);
            final Vector vector = this.fDependencyMap.get(xsDocumentInfo);
            for (int i = 0; i < vector.size(); ++i) {
                stack.push(vector.elementAt(i));
            }
        }
    }
    
    protected void buildDefaultAttributes() {
        final Stack stack = new Stack();
        this.setSchemasVisible(this.fRoot);
        stack.push(this.fRoot);
        while (!stack.empty()) {
            final XSDocumentInfo xsDocumentInfo = stack.pop();
            final Element fSchemaElement = xsDocumentInfo.fSchemaElement;
            if (DOMUtil.isHidden(fSchemaElement, this.fHiddenNodes)) {
                continue;
            }
            if (xsDocumentInfo.fDefaultAttributes != null && xsDocumentInfo.fDefaultAGroup == null) {
                xsDocumentInfo.fDefaultAGroup = (XSAttributeGroupDecl)this.getGlobalDecl(xsDocumentInfo, 2, xsDocumentInfo.fDefaultAttributes, fSchemaElement);
            }
            DOMUtil.setHidden(fSchemaElement, this.fHiddenNodes);
            final Vector vector = this.fDependencyMap.get(xsDocumentInfo);
            for (int i = 0; i < vector.size(); ++i) {
                stack.push(vector.elementAt(i));
            }
        }
    }
    
    protected void traverseSchemas(final ArrayList list) {
        this.setSchemasVisible(this.fRoot);
        final Stack stack = new Stack();
        stack.push(this.fRoot);
        while (!stack.empty()) {
            final XSDocumentInfo xsDocumentInfo = stack.pop();
            final Element fSchemaElement = xsDocumentInfo.fSchemaElement;
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace);
            if (DOMUtil.isHidden(fSchemaElement, this.fHiddenNodes)) {
                continue;
            }
            final Element element = fSchemaElement;
            boolean traverseXSDSchemaGlobalDecls = false;
            for (Element element2 = DOMUtil.getFirstVisibleChildElement(element, this.fHiddenNodes); element2 != null; element2 = DOMUtil.getNextVisibleSiblingElement(element2, this.fHiddenNodes)) {
                traverseXSDSchemaGlobalDecls = this.traverseXSDSchemaGlobalDecls(xsDocumentInfo, grammar, traverseXSDSchemaGlobalDecls, element2);
            }
            if (!traverseXSDSchemaGlobalDecls) {
                final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                if (syntheticAnnotation != null) {
                    grammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element, syntheticAnnotation, xsDocumentInfo.getSchemaAttrs(), true, xsDocumentInfo));
                }
            }
            if (list != null) {
                final XSAnnotationInfo annotations = xsDocumentInfo.getAnnotations();
                if (annotations != null) {
                    list.add(this.doc2SystemId(fSchemaElement));
                    list.add(annotations);
                }
            }
            xsDocumentInfo.returnSchemaAttrs();
            DOMUtil.setHidden(fSchemaElement, this.fHiddenNodes);
            final Vector vector = this.fDependencyMap.get(xsDocumentInfo);
            for (int i = 0; i < vector.size(); ++i) {
                stack.push(vector.elementAt(i));
            }
        }
    }
    
    private boolean traverseXSDSchemaGlobalDecls(final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, boolean b, final Element element) {
        DOMUtil.setHidden(element, this.fHiddenNodes);
        final String localName = DOMUtil.getLocalName(element);
        if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_REDEFINE)) {
            xsDocumentInfo.backupNSSupport(this.fRedefine2NSSupport.get(element));
            for (Element element2 = DOMUtil.getFirstVisibleChildElement(element, this.fHiddenNodes); element2 != null; element2 = DOMUtil.getNextVisibleSiblingElement(element2, this.fHiddenNodes)) {
                final String localName2 = DOMUtil.getLocalName(element2);
                DOMUtil.setHidden(element2, this.fHiddenNodes);
                if (localName2.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                    this.fAttributeGroupTraverser.traverseGlobal(element2, xsDocumentInfo, schemaGrammar);
                }
                else if (localName2.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                    this.fComplexTypeTraverser.traverseGlobal(element2, xsDocumentInfo, schemaGrammar);
                }
                else if (localName2.equals(SchemaSymbols.ELT_GROUP)) {
                    this.fGroupTraverser.traverseGlobal(element2, xsDocumentInfo, schemaGrammar);
                }
                else if (localName2.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                    this.fSimpleTypeTraverser.traverseGlobal(element2, xsDocumentInfo, schemaGrammar);
                }
                else {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { DOMUtil.getLocalName(element), "(annotation | (simpleType | complexType | group | attributeGroup))*", localName2 }, element2);
                }
            }
            xsDocumentInfo.restoreNSSupport();
        }
        else if (localName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
            this.fAttributeTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
            this.fAttributeGroupTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
            this.fComplexTypeTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_ELEMENT)) {
            this.fElementTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_GROUP)) {
            this.fGroupTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_NOTATION)) {
            this.fNotationTraverser.traverse(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            this.fSimpleTypeTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
        }
        else if (localName.equals(SchemaSymbols.ELT_ANNOTATION)) {
            schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(element, xsDocumentInfo.getSchemaAttrs(), true, xsDocumentInfo));
            b = true;
        }
        else {
            this.reportSchemaError("s4s-elt-invalid-content.1", new Object[] { SchemaSymbols.ELT_SCHEMA, DOMUtil.getLocalName(element) }, element);
        }
        return b;
    }
    
    private final boolean needReportTNSError(final String s) {
        if (this.fReportedTNS == null) {
            this.fReportedTNS = new Vector();
        }
        else if (this.fReportedTNS.contains(s)) {
            return false;
        }
        this.fReportedTNS.addElement(s);
        return true;
    }
    
    void addGlobalAttributeDecl(final XSAttributeDecl xsAttributeDecl) {
        final String namespace = xsAttributeDecl.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + xsAttributeDecl.getName()) : (namespace + "," + xsAttributeDecl.getName());
        if (this.fGlobalAttrDecls.get(s) == null) {
            this.fGlobalAttrDecls.put(s, xsAttributeDecl);
        }
    }
    
    void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl xsAttributeGroupDecl) {
        final String namespace = xsAttributeGroupDecl.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + xsAttributeGroupDecl.getName()) : (namespace + "," + xsAttributeGroupDecl.getName());
        if (this.fGlobalAttrGrpDecls.get(s) == null) {
            this.fGlobalAttrGrpDecls.put(s, xsAttributeGroupDecl);
        }
    }
    
    void addGlobalElementDecl(final XSElementDecl xsElementDecl) {
        final String namespace = xsElementDecl.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + xsElementDecl.getName()) : (namespace + "," + xsElementDecl.getName());
        if (this.fGlobalElemDecls.get(s) == null) {
            this.fGlobalElemDecls.put(s, xsElementDecl);
        }
    }
    
    void addGlobalGroupDecl(final XSGroupDecl xsGroupDecl) {
        final String namespace = xsGroupDecl.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + xsGroupDecl.getName()) : (namespace + "," + xsGroupDecl.getName());
        if (this.fGlobalGroupDecls.get(s) == null) {
            this.fGlobalGroupDecls.put(s, xsGroupDecl);
        }
    }
    
    void addGlobalNotationDecl(final XSNotationDecl xsNotationDecl) {
        final String namespace = xsNotationDecl.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + xsNotationDecl.getName()) : (namespace + "," + xsNotationDecl.getName());
        if (this.fGlobalNotationDecls.get(s) == null) {
            this.fGlobalNotationDecls.put(s, xsNotationDecl);
        }
    }
    
    void addGlobalTypeDecl(final XSTypeDefinition xsTypeDefinition) {
        final String namespace = xsTypeDefinition.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + xsTypeDefinition.getName()) : (namespace + "," + xsTypeDefinition.getName());
        if (this.fGlobalTypeDecls.get(s) == null) {
            this.fGlobalTypeDecls.put(s, xsTypeDefinition);
        }
    }
    
    void addIDConstraintDecl(final IdentityConstraint identityConstraint) {
        final String namespace = identityConstraint.getNamespace();
        final String s = (namespace == null || namespace.length() == 0) ? ("," + identityConstraint.getIdentityConstraintName()) : (namespace + "," + identityConstraint.getIdentityConstraintName());
        if (this.fGlobalIDConstraintDecls.get(s) == null) {
            this.fGlobalIDConstraintDecls.put(s, identityConstraint);
        }
    }
    
    private XSAttributeDecl getGlobalAttributeDecl(final String s) {
        return (XSAttributeDecl)this.fGlobalAttrDecls.get(s);
    }
    
    private XSAttributeGroupDecl getGlobalAttributeGroupDecl(final String s) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(s);
    }
    
    private XSElementDecl getGlobalElementDecl(final String s) {
        return (XSElementDecl)this.fGlobalElemDecls.get(s);
    }
    
    private XSGroupDecl getGlobalGroupDecl(final String s) {
        return (XSGroupDecl)this.fGlobalGroupDecls.get(s);
    }
    
    private XSNotationDecl getGlobalNotationDecl(final String s) {
        return (XSNotationDecl)this.fGlobalNotationDecls.get(s);
    }
    
    private XSTypeDefinition getGlobalTypeDecl(final String s) {
        return (XSTypeDefinition)this.fGlobalTypeDecls.get(s);
    }
    
    private IdentityConstraint getIDConstraintDecl(final String s) {
        return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(s);
    }
    
    protected Object getGlobalDecl(final XSDocumentInfo xsDocumentInfo, final int n, final QName qName, final Element element) {
        if (qName.uri != null && qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && n == 7) {
            if (this.fSchemaVersion == 2 && (qName.localpart.equals("duration") || qName.localpart.equals("yearMonthDuration") || qName.localpart.equals("dayTimeDuration"))) {
                return null;
            }
            final XSTypeDefinition globalTypeDecl = SchemaGrammar.getS4SGrammar(this.fSchemaVersion).getGlobalTypeDecl(qName.localpart);
            if (globalTypeDecl != null) {
                return globalTypeDecl;
            }
        }
        if (this.fSchemaVersion == 4 && n == 1 && qName.uri == SchemaSymbols.URI_XSI) {
            final XSAttributeDecl globalAttributeDecl = SchemaGrammar.getXSIGrammar(this.fSchemaVersion).getGlobalAttributeDecl(qName.localpart);
            if (globalAttributeDecl != null) {
                return globalAttributeDecl;
            }
        }
        if (!xsDocumentInfo.isAllowedNS(qName.uri) && xsDocumentInfo.needReportTNSError(qName.uri)) {
            this.reportSchemaError((qName.uri == null) ? "src-resolve.4.1" : "src-resolve.4.2", new Object[] { this.fDoc2SystemId.get(xsDocumentInfo.fSchemaElement), qName.uri, qName.rawname }, element);
        }
        final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(qName.uri);
        if (grammar == null) {
            if (this.needReportTNSError(qName.uri)) {
                this.reportSchemaError("src-resolve", new Object[] { qName.rawname, XSDHandler.COMP_TYPE[n] }, element);
            }
            return null;
        }
        final Object globalDeclFromGrammar = this.getGlobalDeclFromGrammar(grammar, n, qName.localpart);
        final String s = (qName.uri == null) ? ("," + qName.localpart) : (qName.uri + "," + qName.localpart);
        if (!this.fTolerateDuplicates) {
            if (globalDeclFromGrammar != null) {
                return globalDeclFromGrammar;
            }
        }
        else {
            final Object globalDecl = this.getGlobalDecl(s, n);
            if (globalDecl != null) {
                return globalDecl;
            }
        }
        Element element2 = null;
        XSDocumentInfo xsDocumentInfo2 = null;
        switch (n) {
            case 1: {
                element2 = this.fUnparsedAttributeRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedAttributeRegistrySub.get(s);
                break;
            }
            case 2: {
                element2 = this.fUnparsedAttributeGroupRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedAttributeGroupRegistrySub.get(s);
                break;
            }
            case 3: {
                element2 = this.fUnparsedElementRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedElementRegistrySub.get(s);
                break;
            }
            case 4: {
                element2 = this.fUnparsedGroupRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedGroupRegistrySub.get(s);
                break;
            }
            case 5: {
                element2 = this.fUnparsedIdentityConstraintRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedIdentityConstraintRegistrySub.get(s);
                break;
            }
            case 6: {
                element2 = this.fUnparsedNotationRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedNotationRegistrySub.get(s);
                break;
            }
            case 7: {
                element2 = this.fUnparsedTypeRegistry.get(s);
                xsDocumentInfo2 = this.fUnparsedTypeRegistrySub.get(s);
                break;
            }
            default: {
                this.reportSchemaError("Internal-Error", new Object[] { "XSDHandler asked to locate component of type " + n + "; it does not recognize this type!" }, element);
                break;
            }
        }
        if (element2 == null) {
            if (globalDeclFromGrammar == null) {
                this.reportSchemaError("src-resolve", new Object[] { qName.rawname, XSDHandler.COMP_TYPE[n] }, element);
            }
            return globalDeclFromGrammar;
        }
        final XSDocumentInfo xsDocumentForDecl = this.findXSDocumentForDecl(xsDocumentInfo, element2, xsDocumentInfo2);
        if (xsDocumentForDecl == null) {
            if (globalDeclFromGrammar == null) {
                this.reportSchemaError((qName.uri == null) ? "src-resolve.4.1" : "src-resolve.4.2", new Object[] { this.fDoc2SystemId.get(xsDocumentInfo.fSchemaElement), qName.uri, qName.rawname }, element);
            }
            return globalDeclFromGrammar;
        }
        if (DOMUtil.isHidden(element2, this.fHiddenNodes)) {
            if (globalDeclFromGrammar == null) {
                String s2 = XSDHandler.CIRCULAR_CODES[n];
                if (n == 7 && SchemaSymbols.ELT_COMPLEXTYPE.equals(DOMUtil.getLocalName(element2))) {
                    s2 = "ct-props-correct.3";
                }
                this.reportSchemaError(s2, new Object[] { qName.prefix + ":" + qName.localpart }, element);
            }
            return globalDeclFromGrammar;
        }
        return this.traverseGlobalDecl(n, element2, xsDocumentForDecl, grammar);
    }
    
    protected Object getGlobalDecl(final String s, final int n) {
        Object o = null;
        switch (n) {
            case 1: {
                o = this.getGlobalAttributeDecl(s);
                break;
            }
            case 2: {
                o = this.getGlobalAttributeGroupDecl(s);
                break;
            }
            case 3: {
                o = this.getGlobalElementDecl(s);
                break;
            }
            case 4: {
                o = this.getGlobalGroupDecl(s);
                break;
            }
            case 5: {
                o = this.getIDConstraintDecl(s);
                break;
            }
            case 6: {
                o = this.getGlobalNotationDecl(s);
                break;
            }
            case 7: {
                o = this.getGlobalTypeDecl(s);
                break;
            }
        }
        return o;
    }
    
    protected Object getGlobalDeclFromGrammar(final SchemaGrammar schemaGrammar, final int n, final String s) {
        Object o = null;
        switch (n) {
            case 1: {
                o = schemaGrammar.getGlobalAttributeDecl(s);
                break;
            }
            case 2: {
                o = schemaGrammar.getGlobalAttributeGroupDecl(s);
                break;
            }
            case 3: {
                o = schemaGrammar.getGlobalElementDecl(s);
                break;
            }
            case 4: {
                o = schemaGrammar.getGlobalGroupDecl(s);
                break;
            }
            case 5: {
                o = schemaGrammar.getIDConstraintDecl(s);
                break;
            }
            case 6: {
                o = schemaGrammar.getGlobalNotationDecl(s);
                break;
            }
            case 7: {
                o = schemaGrammar.getGlobalTypeDecl(s);
                break;
            }
        }
        return o;
    }
    
    protected Object getGlobalDeclFromGrammar(final SchemaGrammar schemaGrammar, final int n, final String s, final String s2) {
        Object o = null;
        switch (n) {
            case 1: {
                o = schemaGrammar.getGlobalAttributeDecl(s, s2);
                break;
            }
            case 2: {
                o = schemaGrammar.getGlobalAttributeGroupDecl(s, s2);
                break;
            }
            case 3: {
                o = schemaGrammar.getGlobalElementDecl(s, s2);
                break;
            }
            case 4: {
                o = schemaGrammar.getGlobalGroupDecl(s, s2);
                break;
            }
            case 5: {
                o = schemaGrammar.getIDConstraintDecl(s, s2);
                break;
            }
            case 6: {
                o = schemaGrammar.getGlobalNotationDecl(s, s2);
                break;
            }
            case 7: {
                o = schemaGrammar.getGlobalTypeDecl(s, s2);
                break;
            }
        }
        return o;
    }
    
    protected Object traverseGlobalDecl(final int n, final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        Object o = null;
        DOMUtil.setHidden(element, this.fHiddenNodes);
        SchemaNamespaceSupport schemaNamespaceSupport = null;
        final Element parent = DOMUtil.getParent(element);
        if (DOMUtil.getLocalName(parent).equals(SchemaSymbols.ELT_REDEFINE)) {
            schemaNamespaceSupport = (SchemaNamespaceSupport)this.fRedefine2NSSupport.get(parent);
        }
        xsDocumentInfo.backupNSSupport(schemaNamespaceSupport);
        switch (n) {
            case 7: {
                if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                    o = this.fComplexTypeTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
                    break;
                }
                o = this.fSimpleTypeTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
                break;
            }
            case 1: {
                o = this.fAttributeTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
                break;
            }
            case 3: {
                o = this.fElementTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
                break;
            }
            case 2: {
                o = this.fAttributeGroupTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
                break;
            }
            case 4: {
                o = this.fGroupTraverser.traverseGlobal(element, xsDocumentInfo, schemaGrammar);
                break;
            }
            case 6: {
                o = this.fNotationTraverser.traverse(element, xsDocumentInfo, schemaGrammar);
                break;
            }
        }
        xsDocumentInfo.restoreNSSupport();
        return o;
    }
    
    public String schemaDocument2SystemId(final XSDocumentInfo xsDocumentInfo) {
        return this.fDoc2SystemId.get(xsDocumentInfo.fSchemaElement);
    }
    
    Object getGrpOrAttrGrpRedefinedByRestriction(final int n, final QName qName, final XSDocumentInfo xsDocumentInfo, final Element element) {
        final String s = (qName.uri != null) ? (qName.uri + "," + qName.localpart) : ("," + qName.localpart);
        String s2 = null;
        switch (n) {
            case 2: {
                s2 = this.fRedefinedRestrictedAttributeGroupRegistry.get(s);
                break;
            }
            case 4: {
                s2 = this.fRedefinedRestrictedGroupRegistry.get(s);
                break;
            }
            default: {
                return null;
            }
        }
        if (s2 == null) {
            return null;
        }
        final int index = s2.indexOf(",");
        final Object globalDecl = this.getGlobalDecl(xsDocumentInfo, n, new QName(XMLSymbols.EMPTY_STRING, s2.substring(index + 1), s2.substring(index), (index == 0) ? null : s2.substring(0, index)), element);
        if (globalDecl == null) {
            switch (n) {
                case 2: {
                    this.reportSchemaError("src-redefine.7.2.1", new Object[] { qName.localpart }, element);
                    break;
                }
                case 4: {
                    this.reportSchemaError("src-redefine.6.2.1", new Object[] { qName.localpart }, element);
                    break;
                }
            }
            return null;
        }
        return globalDecl;
    }
    
    protected void resolveKeyRefs() {
        for (int i = 0; i < this.fKeyrefStackPos; ++i) {
            final XSDocumentInfo xsDocumentInfo = this.fKeyrefsMapXSDocumentInfo[i];
            xsDocumentInfo.fNamespaceSupport.makeGlobal();
            xsDocumentInfo.fNamespaceSupport.setEffectiveContext(this.fKeyrefNamespaceContext[i]);
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace);
            DOMUtil.setHidden(this.fKeyrefs[i], this.fHiddenNodes);
            this.fKeyrefTraverser.traverse(this.fKeyrefs[i], this.fKeyrefElems[i], xsDocumentInfo, grammar);
        }
    }
    
    protected void resolveIdentityConstraintReferrals() {
        for (int i = 0; i < this.fICReferralStackPos; ++i) {
            final XSDocumentInfo xsDocumentInfo = this.fICReferralsMapXSDocumentInfo[i];
            xsDocumentInfo.fNamespaceSupport.makeGlobal();
            xsDocumentInfo.fNamespaceSupport.setEffectiveContext(this.fICReferralNamespaceContext[i]);
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace);
            DOMUtil.setHidden(this.fICReferrals[i], this.fHiddenNodes);
            this.fUniqueOrKeyTraverser.traverseIdentityConstraintReferral(this.fICReferrals[i], this.fICReferralElems[i], xsDocumentInfo, grammar);
        }
    }
    
    protected Hashtable getIDRegistry() {
        return this.fUnparsedIdentityConstraintRegistry;
    }
    
    protected Hashtable getIDRegistry_sub() {
        return this.fUnparsedIdentityConstraintRegistrySub;
    }
    
    protected void storeKeyRef(final Element element, final XSDocumentInfo xsDocumentInfo, final XSElementDecl xsElementDecl) {
        final String attrValue = DOMUtil.getAttrValue(element, SchemaSymbols.ATT_NAME);
        if (attrValue.length() != 0) {
            this.checkForDuplicateNames((xsDocumentInfo.fTargetNamespace == null) ? ("," + attrValue) : (xsDocumentInfo.fTargetNamespace + "," + attrValue), 5, this.fUnparsedIdentityConstraintRegistry, this.fUnparsedIdentityConstraintRegistrySub, element, xsDocumentInfo);
        }
        if (this.fKeyrefStackPos == this.fKeyrefs.length) {
            final Element[] fKeyrefs = new Element[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefs, 0, fKeyrefs, 0, this.fKeyrefStackPos);
            this.fKeyrefs = fKeyrefs;
            final XSElementDecl[] fKeyrefElems = new XSElementDecl[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefElems, 0, fKeyrefElems, 0, this.fKeyrefStackPos);
            this.fKeyrefElems = fKeyrefElems;
            final String[][] fKeyrefNamespaceContext = new String[this.fKeyrefStackPos + 2][];
            System.arraycopy(this.fKeyrefNamespaceContext, 0, fKeyrefNamespaceContext, 0, this.fKeyrefStackPos);
            this.fKeyrefNamespaceContext = fKeyrefNamespaceContext;
            final XSDocumentInfo[] fKeyrefsMapXSDocumentInfo = new XSDocumentInfo[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefsMapXSDocumentInfo, 0, fKeyrefsMapXSDocumentInfo, 0, this.fKeyrefStackPos);
            this.fKeyrefsMapXSDocumentInfo = fKeyrefsMapXSDocumentInfo;
        }
        this.fKeyrefs[this.fKeyrefStackPos] = element;
        this.fKeyrefElems[this.fKeyrefStackPos] = xsElementDecl;
        this.fKeyrefNamespaceContext[this.fKeyrefStackPos] = xsDocumentInfo.fNamespaceSupport.getEffectiveLocalContext();
        this.fKeyrefsMapXSDocumentInfo[this.fKeyrefStackPos++] = xsDocumentInfo;
    }
    
    protected void storeIdentityConstraintReferral(final Element element, final XSDocumentInfo xsDocumentInfo, final XSElementDecl xsElementDecl) {
        if (this.fICReferralStackPos == this.fICReferrals.length) {
            final Element[] ficReferrals = new Element[this.fICReferralStackPos + 2];
            System.arraycopy(this.fICReferrals, 0, ficReferrals, 0, this.fICReferralStackPos);
            this.fICReferrals = ficReferrals;
            final XSElementDecl[] ficReferralElems = new XSElementDecl[this.fICReferralStackPos + 2];
            System.arraycopy(this.fICReferralElems, 0, ficReferralElems, 0, this.fICReferralStackPos);
            this.fICReferralElems = ficReferralElems;
            final String[][] ficReferralNamespaceContext = new String[this.fICReferralStackPos + 2][];
            System.arraycopy(this.fICReferralNamespaceContext, 0, ficReferralNamespaceContext, 0, this.fICReferralStackPos);
            this.fICReferralNamespaceContext = ficReferralNamespaceContext;
            final XSDocumentInfo[] ficReferralsMapXSDocumentInfo = new XSDocumentInfo[this.fICReferralStackPos + 2];
            System.arraycopy(this.fICReferralsMapXSDocumentInfo, 0, ficReferralsMapXSDocumentInfo, 0, this.fICReferralStackPos);
            this.fICReferralsMapXSDocumentInfo = ficReferralsMapXSDocumentInfo;
        }
        this.fICReferrals[this.fICReferralStackPos] = element;
        this.fICReferralElems[this.fICReferralStackPos] = xsElementDecl;
        this.fICReferralNamespaceContext[this.fICReferralStackPos] = xsDocumentInfo.fNamespaceSupport.getEffectiveLocalContext();
        this.fICReferralsMapXSDocumentInfo[this.fICReferralStackPos++] = xsDocumentInfo;
    }
    
    private Element resolveSchema(final XSDDescription xsdDescription, final boolean b, final Element element, final boolean b2) {
        XMLInputSource resolveDocument = null;
        try {
            resolveDocument = XMLSchemaLoader.resolveDocument(xsdDescription, b2 ? this.fLocationPairs : XSDHandler.EMPTY_TABLE, this.fEntityResolver);
        }
        catch (final IOException ex) {
            if (b) {
                this.reportSchemaError("schema_reference.4", new Object[] { xsdDescription.getLocationHints()[0] }, element);
            }
            else {
                this.reportSchemaWarning("schema_reference.4", new Object[] { xsdDescription.getLocationHints()[0] }, element);
            }
        }
        if (resolveDocument instanceof DOMInputSource) {
            return this.getSchemaDocument(xsdDescription.getTargetNamespace(), (DOMInputSource)resolveDocument, b, xsdDescription.getContextType(), element);
        }
        if (resolveDocument instanceof SAXInputSource) {
            return this.getSchemaDocument(xsdDescription.getTargetNamespace(), (SAXInputSource)resolveDocument, b, xsdDescription.getContextType(), element);
        }
        if (resolveDocument instanceof StAXInputSource) {
            return this.getSchemaDocument(xsdDescription.getTargetNamespace(), (StAXInputSource)resolveDocument, b, xsdDescription.getContextType(), element);
        }
        if (resolveDocument instanceof XSInputSource) {
            return this.getSchemaDocument((XSInputSource)resolveDocument, xsdDescription);
        }
        return this.getSchemaDocument(xsdDescription.getTargetNamespace(), resolveDocument, b, xsdDescription.getContextType(), element);
    }
    
    private Element resolveSchema(final XMLInputSource xmlInputSource, final XSDDescription xsdDescription, final boolean b, final Element element) {
        if (xmlInputSource instanceof DOMInputSource) {
            return this.getSchemaDocument(xsdDescription.getTargetNamespace(), (DOMInputSource)xmlInputSource, b, xsdDescription.getContextType(), element);
        }
        if (xmlInputSource instanceof SAXInputSource) {
            return this.getSchemaDocument(xsdDescription.getTargetNamespace(), (SAXInputSource)xmlInputSource, b, xsdDescription.getContextType(), element);
        }
        if (xmlInputSource instanceof StAXInputSource) {
            return this.getSchemaDocument(xsdDescription.getTargetNamespace(), (StAXInputSource)xmlInputSource, b, xsdDescription.getContextType(), element);
        }
        if (xmlInputSource instanceof XSInputSource) {
            return this.getSchemaDocument((XSInputSource)xmlInputSource, xsdDescription);
        }
        return this.getSchemaDocument(xsdDescription.getTargetNamespace(), xmlInputSource, b, xsdDescription.getContextType(), element);
    }
    
    private XMLInputSource resolveSchemaSource(final XSDDescription xsdDescription, final boolean b, final Element element, final boolean b2) {
        XMLInputSource resolveDocument = null;
        try {
            resolveDocument = XMLSchemaLoader.resolveDocument(xsdDescription, b2 ? this.fLocationPairs : XSDHandler.EMPTY_TABLE, this.fEntityResolver);
        }
        catch (final IOException ex) {
            if (b) {
                this.reportSchemaError("schema_reference.4", new Object[] { xsdDescription.getLocationHints()[0] }, element);
            }
            else {
                this.reportSchemaWarning("schema_reference.4", new Object[] { xsdDescription.getLocationHints()[0] }, element);
            }
        }
        return resolveDocument;
    }
    
    private Element getSchemaDocument(final String s, final XMLInputSource xmlInputSource, final boolean b, final short n, final Element element) {
        boolean b2 = true;
        IOException ex = null;
        try {
            if (xmlInputSource != null && (xmlInputSource.getSystemId() != null || xmlInputSource.getByteStream() != null || xmlInputSource.getCharacterStream() != null)) {
                XSDKey xsdKey = null;
                String expandSystemId = null;
                if (n != 3) {
                    expandSystemId = XMLEntityManager.expandSystemId(xmlInputSource.getSystemId(), xmlInputSource.getBaseSystemId(), false);
                    xsdKey = new XSDKey(expandSystemId, n, s);
                    final Element element2;
                    if ((element2 = this.fTraversed.get(xsdKey)) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return element2;
                    }
                }
                this.fSchemaParser.parse(xmlInputSource);
                final Document document = this.fSchemaParser.getDocument();
                return this.getSchemaDocument0(xsdKey, expandSystemId, (document != null) ? DOMUtil.getRoot(document) : null, (this.fSchemaVersion == 4) ? this.fSchemaParser.getProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version") : null);
            }
            b2 = false;
        }
        catch (final IOException ex2) {
            ex = ex2;
        }
        return this.getSchemaDocument1(b, b2, xmlInputSource, element, ex);
    }
    
    private Element getSchemaDocument(final String s, final SAXInputSource saxInputSource, final boolean b, final short n, final Element element) {
        XMLReader xmlReader = saxInputSource.getXMLReader();
        final InputSource inputSource = saxInputSource.getInputSource();
        boolean b2 = true;
        IOException ex = null;
        try {
            if (inputSource != null && (inputSource.getSystemId() != null || inputSource.getByteStream() != null || inputSource.getCharacterStream() != null)) {
                XSDKey xsdKey = null;
                String expandSystemId = null;
                if (n != 3) {
                    expandSystemId = XMLEntityManager.expandSystemId(inputSource.getSystemId(), saxInputSource.getBaseSystemId(), false);
                    xsdKey = new XSDKey(expandSystemId, n, s);
                    final Element element2;
                    if ((element2 = this.fTraversed.get(xsdKey)) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return element2;
                    }
                }
                boolean feature = false;
                if (xmlReader != null) {
                    try {
                        feature = xmlReader.getFeature("http://xml.org/sax/features/namespace-prefixes");
                    }
                    catch (final SAXException ex2) {}
                }
                else {
                    try {
                        xmlReader = XMLReaderFactory.createXMLReader();
                    }
                    catch (final SAXException ex3) {
                        xmlReader = new SAXParser();
                    }
                    try {
                        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                        feature = true;
                        if (xmlReader instanceof SAXParser) {
                            final Object property = this.fSchemaParser.getProperty("http://apache.org/xml/properties/security-manager");
                            if (property != null) {
                                xmlReader.setProperty("http://apache.org/xml/properties/security-manager", property);
                            }
                        }
                    }
                    catch (final SAXException ex4) {}
                }
                boolean feature2 = false;
                try {
                    feature2 = xmlReader.getFeature("http://xml.org/sax/features/string-interning");
                }
                catch (final SAXException ex5) {}
                if (this.fXSContentHandler == null) {
                    this.fXSContentHandler = new SchemaContentHandler();
                }
                this.fXSContentHandler.reset(this.fSchemaParser, this.fSymbolTable, feature, feature2);
                xmlReader.setContentHandler(this.fXSContentHandler);
                xmlReader.setErrorHandler(this.fErrorReporter.getSAXErrorHandler());
                xmlReader.parse(inputSource);
                try {
                    xmlReader.setContentHandler(null);
                    xmlReader.setErrorHandler(null);
                }
                catch (final Exception ex6) {}
                final Document document = this.fXSContentHandler.getDocument();
                return this.getSchemaDocument0(xsdKey, expandSystemId, (document != null) ? DOMUtil.getRoot(document) : null, (this.fSchemaVersion == 4) ? this.fSchemaParser.getProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version") : null);
            }
            b2 = false;
        }
        catch (final SAXParseException ex7) {
            throw SAX2XNIUtil.createXMLParseException0(ex7);
        }
        catch (final SAXException ex8) {
            throw SAX2XNIUtil.createXNIException0(ex8);
        }
        catch (final IOException ex9) {
            ex = ex9;
        }
        return this.getSchemaDocument1(b, b2, saxInputSource, element, ex);
    }
    
    private Element getSchemaDocument(final String s, final DOMInputSource domInputSource, final boolean b, final short n, final Element element) {
        boolean b2 = true;
        IOException ex = null;
        Element root = null;
        final Node node = domInputSource.getNode();
        int nodeType = -1;
        if (node != null) {
            nodeType = node.getNodeType();
            if (nodeType == 9) {
                root = DOMUtil.getRoot((Document)node);
            }
            else if (nodeType == 1) {
                root = (Element)node;
            }
        }
        try {
            if (root != null) {
                XSDKey xsdKey = null;
                String expandSystemId = null;
                if (n != 3) {
                    expandSystemId = XMLEntityManager.expandSystemId(domInputSource.getSystemId(), domInputSource.getBaseSystemId(), false);
                    boolean b3 = nodeType == 9;
                    if (!b3) {
                        final Node parentNode = root.getParentNode();
                        if (parentNode != null) {
                            b3 = (parentNode.getNodeType() == 9);
                        }
                    }
                    if (b3) {
                        xsdKey = new XSDKey(expandSystemId, n, s);
                        final Element element2;
                        if ((element2 = this.fTraversed.get(xsdKey)) != null) {
                            this.fLastSchemaWasDuplicate = true;
                            return element2;
                        }
                    }
                }
                return this.getSchemaDocument0(xsdKey, expandSystemId, root, null);
            }
            b2 = false;
        }
        catch (final IOException ex2) {
            ex = ex2;
        }
        return this.getSchemaDocument1(b, b2, domInputSource, element, ex);
    }
    
    private Element getSchemaDocument(final String s, final StAXInputSource stAXInputSource, final boolean b, final short n, final Element element) {
        IOException ex2;
        try {
            final boolean shouldConsumeRemainingContent = stAXInputSource.shouldConsumeRemainingContent();
            final XMLStreamReader xmlStreamReader = stAXInputSource.getXMLStreamReader();
            final XMLEventReader xmlEventReader = stAXInputSource.getXMLEventReader();
            XSDKey xsdKey = null;
            String expandSystemId = null;
            if (n != 3) {
                expandSystemId = XMLEntityManager.expandSystemId(stAXInputSource.getSystemId(), stAXInputSource.getBaseSystemId(), false);
                boolean startDocument = shouldConsumeRemainingContent;
                if (!startDocument) {
                    if (xmlStreamReader != null) {
                        startDocument = (xmlStreamReader.getEventType() == 7);
                    }
                    else {
                        startDocument = xmlEventReader.peek().isStartDocument();
                    }
                }
                if (startDocument) {
                    xsdKey = new XSDKey(expandSystemId, n, s);
                    final Element element2;
                    if ((element2 = this.fTraversed.get(xsdKey)) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return element2;
                    }
                }
            }
            if (this.fStAXSchemaParser == null) {
                this.fStAXSchemaParser = new StAXSchemaParser();
            }
            this.fStAXSchemaParser.reset(this.fSchemaParser, this.fSymbolTable);
            if (xmlStreamReader != null) {
                this.fStAXSchemaParser.parse(xmlStreamReader);
                if (shouldConsumeRemainingContent) {
                    while (xmlStreamReader.hasNext()) {
                        xmlStreamReader.next();
                    }
                }
            }
            else {
                this.fStAXSchemaParser.parse(xmlEventReader);
                if (shouldConsumeRemainingContent) {
                    while (xmlEventReader.hasNext()) {
                        xmlEventReader.nextEvent();
                    }
                }
            }
            final Document document = this.fStAXSchemaParser.getDocument();
            return this.getSchemaDocument0(xsdKey, expandSystemId, (document != null) ? DOMUtil.getRoot(document) : null, (this.fSchemaVersion == 4) ? this.fSchemaParser.getProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version") : null);
        }
        catch (final XMLStreamException ex) {
            final Throwable nestedException = ex.getNestedException();
            if (!(nestedException instanceof IOException)) {
                final StAXLocationWrapper stAXLocationWrapper = new StAXLocationWrapper();
                stAXLocationWrapper.setLocation(ex.getLocation());
                throw new XMLParseException(stAXLocationWrapper, ex.getMessage(), ex);
            }
            ex2 = (IOException)nestedException;
        }
        catch (final IOException ex3) {
            ex2 = ex3;
        }
        return this.getSchemaDocument1(b, true, stAXInputSource, element, ex2);
    }
    
    private Element getSchemaDocument0(final XSDKey xsdKey, final String s, final Element element, final Object o) {
        if (element != null) {
            if (xsdKey != null) {
                this.fTraversed.put(xsdKey, element);
            }
            if (s != null) {
                this.fDoc2SystemId.put(element, s);
            }
            if (o != null) {
                this.fDoc2DatatypeXMLVersion.put(element, o);
            }
            this.fLastSchemaWasDuplicate = false;
        }
        else {
            this.fLastSchemaWasDuplicate = true;
        }
        return element;
    }
    
    private Element getSchemaDocument1(final boolean b, final boolean b2, final XMLInputSource xmlInputSource, final Element element, final IOException ex) {
        if (b) {
            if (b2) {
                this.reportSchemaError("schema_reference.4", new Object[] { xmlInputSource.getSystemId() }, element, ex);
            }
            else {
                this.reportSchemaError("schema_reference.4", new Object[] { (xmlInputSource == null) ? "" : xmlInputSource.getSystemId() }, element, ex);
            }
        }
        else if (b2) {
            this.reportSchemaWarning("schema_reference.4", new Object[] { xmlInputSource.getSystemId() }, element, ex);
        }
        this.fLastSchemaWasDuplicate = false;
        return null;
    }
    
    private Element getSchemaDocument(final XSInputSource xsInputSource, final XSDDescription xsdDescription) {
        final SchemaGrammar[] grammars = xsInputSource.getGrammars();
        final short contextType = xsdDescription.getContextType();
        if (grammars != null && grammars.length > 0) {
            final Vector expandGrammars = this.expandGrammars(grammars);
            if (this.fNamespaceGrowth || !this.existingGrammars(expandGrammars)) {
                this.addGrammars(expandGrammars);
                if (contextType == 3) {
                    xsdDescription.setTargetNamespace(grammars[0].getTargetNamespace());
                }
            }
        }
        else {
            final XSObject[] components = xsInputSource.getComponents();
            if (components != null && components.length > 0) {
                final Hashtable hashtable = new Hashtable();
                final Vector expandComponents = this.expandComponents(components, hashtable);
                if (this.fNamespaceGrowth || this.canAddComponents(expandComponents)) {
                    this.addGlobalComponents(expandComponents, hashtable);
                    if (contextType == 3) {
                        xsdDescription.setTargetNamespace(components[0].getNamespace());
                    }
                }
            }
        }
        return null;
    }
    
    private Vector expandGrammars(final SchemaGrammar[] array) {
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            if (!vector.contains(array[i])) {
                vector.add(array[i]);
            }
        }
        for (int j = 0; j < vector.size(); ++j) {
            final Vector importedGrammars = vector.elementAt(j).getImportedGrammars();
            if (importedGrammars != null) {
                for (int k = importedGrammars.size() - 1; k >= 0; --k) {
                    final SchemaGrammar schemaGrammar = importedGrammars.elementAt(k);
                    if (!vector.contains(schemaGrammar)) {
                        vector.addElement(schemaGrammar);
                    }
                }
            }
        }
        return vector;
    }
    
    private boolean existingGrammars(final Vector vector) {
        final int size = vector.size();
        final XSDDescription xsdDescription = new XSDDescription();
        for (int i = 0; i < size; ++i) {
            xsdDescription.setNamespace(((SchemaGrammar)vector.elementAt(i)).getTargetNamespace());
            if (this.findGrammar(xsdDescription, false) != null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean canAddComponents(final Vector vector) {
        final int size = vector.size();
        final XSDDescription xsdDescription = new XSDDescription();
        for (int i = 0; i < size; ++i) {
            if (!this.canAddComponent((XSObject)vector.elementAt(i), xsdDescription)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean canAddComponent(final XSObject xsObject, final XSDDescription xsdDescription) {
        xsdDescription.setNamespace(xsObject.getNamespace());
        final SchemaGrammar grammar = this.findGrammar(xsdDescription, false);
        if (grammar == null) {
            return true;
        }
        if (grammar.isImmutable()) {
            return false;
        }
        final short type = xsObject.getType();
        final String name = xsObject.getName();
        switch (type) {
            case 3: {
                if (grammar.getGlobalTypeDecl(name) == xsObject) {
                    return true;
                }
                break;
            }
            case 1: {
                if (grammar.getGlobalAttributeDecl(name) == xsObject) {
                    return true;
                }
                break;
            }
            case 5: {
                if (grammar.getGlobalAttributeDecl(name) == xsObject) {
                    return true;
                }
                break;
            }
            case 2: {
                if (grammar.getGlobalElementDecl(name) == xsObject) {
                    return true;
                }
                break;
            }
            case 6: {
                if (grammar.getGlobalGroupDecl(name) == xsObject) {
                    return true;
                }
                break;
            }
            case 11: {
                if (grammar.getGlobalNotationDecl(name) == xsObject) {
                    return true;
                }
                break;
            }
            default: {
                return true;
            }
        }
        return false;
    }
    
    private void addGrammars(final Vector vector) {
        final int size = vector.size();
        final XSDDescription xsdDescription = new XSDDescription();
        for (int i = 0; i < size; ++i) {
            final SchemaGrammar schemaGrammar = vector.elementAt(i);
            xsdDescription.setNamespace(schemaGrammar.getTargetNamespace());
            final SchemaGrammar grammar = this.findGrammar(xsdDescription, this.fNamespaceGrowth);
            if (schemaGrammar != grammar) {
                this.addGrammarComponents(schemaGrammar, grammar);
            }
        }
    }
    
    private void addGrammarComponents(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        if (schemaGrammar2 == null) {
            this.createGrammarFrom(schemaGrammar);
            return;
        }
        SchemaGrammar grammar = schemaGrammar2;
        if (grammar.isImmutable()) {
            grammar = this.createGrammarFrom(schemaGrammar2);
        }
        this.addNewGrammarLocations(schemaGrammar, grammar);
        this.addNewImportedGrammars(schemaGrammar, grammar);
        this.addNewGrammarComponents(schemaGrammar, grammar);
    }
    
    private SchemaGrammar createGrammarFrom(final SchemaGrammar schemaGrammar) {
        final SchemaGrammar schemaGrammar2 = new SchemaGrammar(schemaGrammar);
        this.fGrammarBucket.putGrammar(schemaGrammar2);
        this.updateImportListWith(schemaGrammar2);
        this.updateImportListFor(schemaGrammar2);
        return schemaGrammar2;
    }
    
    private void addNewGrammarLocations(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final StringList documentLocations = schemaGrammar.getDocumentLocations();
        final int size = documentLocations.size();
        final StringList documentLocations2 = schemaGrammar2.getDocumentLocations();
        for (int i = 0; i < size; ++i) {
            final String item = documentLocations.item(i);
            if (!documentLocations2.contains(item)) {
                schemaGrammar2.addDocument(null, item);
            }
        }
    }
    
    private void addNewImportedGrammars(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final Vector importedGrammars = schemaGrammar.getImportedGrammars();
        if (importedGrammars != null) {
            Vector importedGrammars2 = schemaGrammar2.getImportedGrammars();
            if (importedGrammars2 == null) {
                importedGrammars2 = new Vector();
                schemaGrammar2.setImportedGrammars(importedGrammars2);
            }
            for (int size = importedGrammars.size(), i = 0; i < size; ++i) {
                SchemaGrammar schemaGrammar3 = importedGrammars.elementAt(i);
                final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(schemaGrammar3.getTargetNamespace());
                if (grammar != null) {
                    schemaGrammar3 = grammar;
                }
                if (!this.containedImportedGrammar(importedGrammars2, schemaGrammar3)) {
                    importedGrammars2.add(schemaGrammar3);
                }
            }
        }
    }
    
    private void updateImportList(final Vector vector, final Vector vector2) {
        for (int size = vector.size(), i = 0; i < size; ++i) {
            final SchemaGrammar schemaGrammar = vector.elementAt(i);
            if (!this.containedImportedGrammar(vector2, schemaGrammar)) {
                vector2.add(schemaGrammar);
            }
        }
    }
    
    private void addNewGrammarComponents(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        schemaGrammar2.resetComponents();
        this.addGlobalElementDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalAttributeDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalAttributeGroupDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalGroupDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalTypeDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalNotationDecls(schemaGrammar, schemaGrammar2);
    }
    
    private void addGlobalElementDecls(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final XSNamedMap components = schemaGrammar.getComponents((short)2);
        for (int length = components.getLength(), i = 0; i < length; ++i) {
            final XSElementDecl xsElementDecl = (XSElementDecl)components.item(i);
            final XSElementDecl globalElementDecl = schemaGrammar2.getGlobalElementDecl(xsElementDecl.getName());
            if (globalElementDecl == null) {
                schemaGrammar2.addGlobalElementDecl(xsElementDecl);
            }
            else if (globalElementDecl != xsElementDecl) {}
        }
        final ObjectList componentsExt = schemaGrammar.getComponentsExt((short)2);
        for (int length2 = componentsExt.getLength(), j = 0; j < length2; j += 2) {
            final String s = (String)componentsExt.item(j);
            final int index = s.indexOf(44);
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1, s.length());
            final XSElementDecl xsElementDecl2 = (XSElementDecl)componentsExt.item(j + 1);
            final XSElementDecl globalElementDecl2 = schemaGrammar2.getGlobalElementDecl(substring2, substring);
            if (globalElementDecl2 == null) {
                schemaGrammar2.addGlobalElementDecl(xsElementDecl2, substring);
            }
            else if (globalElementDecl2 != xsElementDecl2) {}
        }
    }
    
    private void addGlobalAttributeDecls(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final XSNamedMap components = schemaGrammar.getComponents((short)1);
        for (int length = components.getLength(), i = 0; i < length; ++i) {
            final XSAttributeDecl xsAttributeDecl = (XSAttributeDecl)components.item(i);
            final XSAttributeDecl globalAttributeDecl = schemaGrammar2.getGlobalAttributeDecl(xsAttributeDecl.getName());
            if (globalAttributeDecl == null) {
                schemaGrammar2.addGlobalAttributeDecl(xsAttributeDecl);
            }
            else if (globalAttributeDecl != xsAttributeDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(xsAttributeDecl.getNamespace(), xsAttributeDecl.getName());
            }
        }
        final ObjectList componentsExt = schemaGrammar.getComponentsExt((short)1);
        for (int length2 = componentsExt.getLength(), j = 0; j < length2; j += 2) {
            final String s = (String)componentsExt.item(j);
            final int index = s.indexOf(44);
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1, s.length());
            final XSAttributeDecl xsAttributeDecl2 = (XSAttributeDecl)componentsExt.item(j + 1);
            final XSAttributeDecl globalAttributeDecl2 = schemaGrammar2.getGlobalAttributeDecl(substring2, substring);
            if (globalAttributeDecl2 == null) {
                schemaGrammar2.addGlobalAttributeDecl(xsAttributeDecl2, substring);
            }
            else if (globalAttributeDecl2 != xsAttributeDecl2) {}
        }
    }
    
    private void addGlobalAttributeGroupDecls(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final XSNamedMap components = schemaGrammar.getComponents((short)5);
        for (int length = components.getLength(), i = 0; i < length; ++i) {
            final XSAttributeGroupDecl xsAttributeGroupDecl = (XSAttributeGroupDecl)components.item(i);
            final XSAttributeGroupDecl globalAttributeGroupDecl = schemaGrammar2.getGlobalAttributeGroupDecl(xsAttributeGroupDecl.getName());
            if (globalAttributeGroupDecl == null) {
                schemaGrammar2.addGlobalAttributeGroupDecl(xsAttributeGroupDecl);
            }
            else if (globalAttributeGroupDecl != xsAttributeGroupDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(xsAttributeGroupDecl.getNamespace(), xsAttributeGroupDecl.getName());
            }
        }
        final ObjectList componentsExt = schemaGrammar.getComponentsExt((short)5);
        for (int length2 = componentsExt.getLength(), j = 0; j < length2; j += 2) {
            final String s = (String)componentsExt.item(j);
            final int index = s.indexOf(44);
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1, s.length());
            final XSAttributeGroupDecl xsAttributeGroupDecl2 = (XSAttributeGroupDecl)componentsExt.item(j + 1);
            final XSAttributeGroupDecl globalAttributeGroupDecl2 = schemaGrammar2.getGlobalAttributeGroupDecl(substring2, substring);
            if (globalAttributeGroupDecl2 == null) {
                schemaGrammar2.addGlobalAttributeGroupDecl(xsAttributeGroupDecl2, substring);
            }
            else if (globalAttributeGroupDecl2 != xsAttributeGroupDecl2) {}
        }
    }
    
    private void addGlobalNotationDecls(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final XSNamedMap components = schemaGrammar.getComponents((short)11);
        for (int length = components.getLength(), i = 0; i < length; ++i) {
            final XSNotationDecl xsNotationDecl = (XSNotationDecl)components.item(i);
            final XSNotationDecl globalNotationDecl = schemaGrammar2.getGlobalNotationDecl(xsNotationDecl.getName());
            if (globalNotationDecl == null) {
                schemaGrammar2.addGlobalNotationDecl(xsNotationDecl);
            }
            else if (globalNotationDecl != xsNotationDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(xsNotationDecl.getNamespace(), xsNotationDecl.getName());
            }
        }
        final ObjectList componentsExt = schemaGrammar.getComponentsExt((short)11);
        for (int length2 = componentsExt.getLength(), j = 0; j < length2; j += 2) {
            final String s = (String)componentsExt.item(j);
            final int index = s.indexOf(44);
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1, s.length());
            final XSNotationDecl xsNotationDecl2 = (XSNotationDecl)componentsExt.item(j + 1);
            final XSNotationDecl globalNotationDecl2 = schemaGrammar2.getGlobalNotationDecl(substring2, substring);
            if (globalNotationDecl2 == null) {
                schemaGrammar2.addGlobalNotationDecl(xsNotationDecl2, substring);
            }
            else if (globalNotationDecl2 != xsNotationDecl2) {}
        }
    }
    
    private void addGlobalGroupDecls(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final XSNamedMap components = schemaGrammar.getComponents((short)6);
        for (int length = components.getLength(), i = 0; i < length; ++i) {
            final XSGroupDecl xsGroupDecl = (XSGroupDecl)components.item(i);
            final XSGroupDecl globalGroupDecl = schemaGrammar2.getGlobalGroupDecl(xsGroupDecl.getName());
            if (globalGroupDecl == null) {
                schemaGrammar2.addGlobalGroupDecl(xsGroupDecl);
            }
            else if (xsGroupDecl != globalGroupDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(xsGroupDecl.getNamespace(), xsGroupDecl.getName());
            }
        }
        final ObjectList componentsExt = schemaGrammar.getComponentsExt((short)6);
        for (int length2 = componentsExt.getLength(), j = 0; j < length2; j += 2) {
            final String s = (String)componentsExt.item(j);
            final int index = s.indexOf(44);
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1, s.length());
            final XSGroupDecl xsGroupDecl2 = (XSGroupDecl)componentsExt.item(j + 1);
            final XSGroupDecl globalGroupDecl2 = schemaGrammar2.getGlobalGroupDecl(substring2, substring);
            if (globalGroupDecl2 == null) {
                schemaGrammar2.addGlobalGroupDecl(xsGroupDecl2, substring);
            }
            else if (globalGroupDecl2 != xsGroupDecl2) {}
        }
    }
    
    private void addGlobalTypeDecls(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
        final XSNamedMap components = schemaGrammar.getComponents((short)3);
        for (int length = components.getLength(), i = 0; i < length; ++i) {
            final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)components.item(i);
            final XSTypeDefinition globalTypeDecl = schemaGrammar2.getGlobalTypeDecl(xsTypeDefinition.getName());
            if (globalTypeDecl == null) {
                schemaGrammar2.addGlobalTypeDecl(xsTypeDefinition);
            }
            else if (globalTypeDecl != xsTypeDefinition && !this.fTolerateDuplicates) {
                this.reportSharingError(xsTypeDefinition.getNamespace(), xsTypeDefinition.getName());
            }
        }
        final ObjectList componentsExt = schemaGrammar.getComponentsExt((short)3);
        for (int length2 = componentsExt.getLength(), j = 0; j < length2; j += 2) {
            final String s = (String)componentsExt.item(j);
            final int index = s.indexOf(44);
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1, s.length());
            final XSTypeDefinition xsTypeDefinition2 = (XSTypeDefinition)componentsExt.item(j + 1);
            final XSTypeDefinition globalTypeDecl2 = schemaGrammar2.getGlobalTypeDecl(substring2, substring);
            if (globalTypeDecl2 == null) {
                schemaGrammar2.addGlobalTypeDecl(xsTypeDefinition2, substring);
            }
            else if (globalTypeDecl2 != xsTypeDefinition2) {}
        }
    }
    
    private Vector expandComponents(final XSObject[] array, final Hashtable hashtable) {
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            if (!vector.contains(array[i])) {
                vector.add(array[i]);
            }
        }
        for (int j = 0; j < vector.size(); ++j) {
            this.expandRelatedComponents((XSObject)vector.elementAt(j), vector, hashtable);
        }
        return vector;
    }
    
    private void expandRelatedComponents(final XSObject xsObject, final Vector vector, final Hashtable hashtable) {
        switch (xsObject.getType()) {
            case 3: {
                this.expandRelatedTypeComponents((XSTypeDefinition)xsObject, vector, xsObject.getNamespace(), hashtable);
                break;
            }
            case 1: {
                this.expandRelatedAttributeComponents((XSAttributeDeclaration)xsObject, vector, xsObject.getNamespace(), hashtable);
                break;
            }
            case 5: {
                this.expandRelatedAttributeGroupComponents((XSAttributeGroupDefinition)xsObject, vector, xsObject.getNamespace(), hashtable);
            }
            case 2: {
                this.expandRelatedElementComponents((XSElementDeclaration)xsObject, vector, xsObject.getNamespace(), hashtable);
                break;
            }
            case 6: {
                this.expandRelatedModelGroupDefinitionComponents((XSModelGroupDefinition)xsObject, vector, xsObject.getNamespace(), hashtable);
                break;
            }
        }
    }
    
    private void expandRelatedAttributeComponents(final XSAttributeDeclaration xsAttributeDeclaration, final Vector vector, final String s, final Hashtable hashtable) {
        this.addRelatedType(xsAttributeDeclaration.getTypeDefinition(), vector, s, hashtable);
    }
    
    private void expandRelatedElementComponents(final XSElementDeclaration xsElementDeclaration, final Vector vector, final String s, final Hashtable hashtable) {
        this.addRelatedType(xsElementDeclaration.getTypeDefinition(), vector, s, hashtable);
        final XSElementDecl[] fSubGroup = ((XSElementDecl)xsElementDeclaration).fSubGroup;
        if (fSubGroup != null) {
            for (int i = 0; i < fSubGroup.length; ++i) {
                this.addRelatedElement(fSubGroup[i], vector, s, hashtable);
            }
        }
    }
    
    private void expandRelatedTypeComponents(final XSTypeDefinition xsTypeDefinition, final Vector vector, final String s, final Hashtable hashtable) {
        if (xsTypeDefinition instanceof XSComplexTypeDecl) {
            this.expandRelatedComplexTypeComponents((XSComplexTypeDecl)xsTypeDefinition, vector, s, hashtable);
        }
        else if (xsTypeDefinition instanceof XSSimpleTypeDecl) {
            this.expandRelatedSimpleTypeComponents((XSSimpleTypeDefinition)xsTypeDefinition, vector, s, hashtable);
        }
    }
    
    private void expandRelatedModelGroupDefinitionComponents(final XSModelGroupDefinition xsModelGroupDefinition, final Vector vector, final String s, final Hashtable hashtable) {
        this.expandRelatedModelGroupComponents(xsModelGroupDefinition.getModelGroup(), vector, s, hashtable);
    }
    
    private void expandRelatedAttributeGroupComponents(final XSAttributeGroupDefinition xsAttributeGroupDefinition, final Vector vector, final String s, final Hashtable hashtable) {
        this.expandRelatedAttributeUsesComponents(xsAttributeGroupDefinition.getAttributeUses(), vector, s, hashtable);
    }
    
    private void expandRelatedComplexTypeComponents(final XSComplexTypeDecl xsComplexTypeDecl, final Vector vector, final String s, final Hashtable hashtable) {
        this.addRelatedType(xsComplexTypeDecl.getBaseType(), vector, s, hashtable);
        this.expandRelatedAttributeUsesComponents(xsComplexTypeDecl.getAttributeUses(), vector, s, hashtable);
        final XSParticle particle = xsComplexTypeDecl.getParticle();
        if (particle != null) {
            this.expandRelatedParticleComponents(particle, vector, s, hashtable);
        }
    }
    
    private void expandRelatedSimpleTypeComponents(final XSSimpleTypeDefinition xsSimpleTypeDefinition, final Vector vector, final String s, final Hashtable hashtable) {
        final XSTypeDefinition baseType = xsSimpleTypeDefinition.getBaseType();
        if (baseType != null) {
            this.addRelatedType(baseType, vector, s, hashtable);
        }
        final XSSimpleTypeDefinition itemType = xsSimpleTypeDefinition.getItemType();
        if (itemType != null) {
            this.addRelatedType(itemType, vector, s, hashtable);
        }
        final XSSimpleTypeDefinition primitiveType = xsSimpleTypeDefinition.getPrimitiveType();
        if (primitiveType != null) {
            this.addRelatedType(primitiveType, vector, s, hashtable);
        }
        final XSObjectList memberTypes = xsSimpleTypeDefinition.getMemberTypes();
        if (memberTypes.size() > 0) {
            for (int i = 0; i < memberTypes.size(); ++i) {
                this.addRelatedType((XSTypeDefinition)memberTypes.item(i), vector, s, hashtable);
            }
        }
    }
    
    private void expandRelatedAttributeUsesComponents(final XSObjectList list, final Vector vector, final String s, final Hashtable hashtable) {
        for (int n = (list == null) ? 0 : list.size(), i = 0; i < n; ++i) {
            this.expandRelatedAttributeUseComponents((XSAttributeUse)list.item(i), vector, s, hashtable);
        }
    }
    
    private void expandRelatedAttributeUseComponents(final XSAttributeUse xsAttributeUse, final Vector vector, final String s, final Hashtable hashtable) {
        this.addRelatedAttribute(xsAttributeUse.getAttrDeclaration(), vector, s, hashtable);
    }
    
    private void expandRelatedParticleComponents(final XSParticle xsParticle, final Vector vector, final String s, final Hashtable hashtable) {
        final XSTerm term = xsParticle.getTerm();
        switch (term.getType()) {
            case 2: {
                this.addRelatedElement((XSElementDeclaration)term, vector, s, hashtable);
                break;
            }
            case 7: {
                this.expandRelatedModelGroupComponents((XSModelGroup)term, vector, s, hashtable);
                break;
            }
        }
    }
    
    private void expandRelatedModelGroupComponents(final XSModelGroup xsModelGroup, final Vector vector, final String s, final Hashtable hashtable) {
        final XSObjectList particles = xsModelGroup.getParticles();
        for (int n = (particles == null) ? 0 : particles.getLength(), i = 0; i < n; ++i) {
            this.expandRelatedParticleComponents((XSParticle)particles.item(i), vector, s, hashtable);
        }
    }
    
    private void addRelatedType(final XSTypeDefinition xsTypeDefinition, final Vector vector, final String s, final Hashtable hashtable) {
        if (!xsTypeDefinition.getAnonymous()) {
            if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xsTypeDefinition.getNamespace()) && !vector.contains(xsTypeDefinition)) {
                this.addNamespaceDependency(s, xsTypeDefinition.getNamespace(), this.findDependentNamespaces(s, hashtable));
                vector.add(xsTypeDefinition);
            }
        }
        else {
            this.expandRelatedTypeComponents(xsTypeDefinition, vector, s, hashtable);
        }
    }
    
    private void addRelatedElement(final XSElementDeclaration xsElementDeclaration, final Vector vector, final String s, final Hashtable hashtable) {
        if (xsElementDeclaration.getScope() == 1) {
            if (!vector.contains(xsElementDeclaration)) {
                this.addNamespaceDependency(s, xsElementDeclaration.getNamespace(), this.findDependentNamespaces(s, hashtable));
                vector.add(xsElementDeclaration);
            }
        }
        else {
            this.expandRelatedElementComponents(xsElementDeclaration, vector, s, hashtable);
        }
    }
    
    private void addRelatedAttribute(final XSAttributeDeclaration xsAttributeDeclaration, final Vector vector, final String s, final Hashtable hashtable) {
        if (xsAttributeDeclaration.getScope() == 1) {
            if (!vector.contains(xsAttributeDeclaration)) {
                this.addNamespaceDependency(s, xsAttributeDeclaration.getNamespace(), this.findDependentNamespaces(s, hashtable));
                vector.add(xsAttributeDeclaration);
            }
        }
        else {
            this.expandRelatedAttributeComponents(xsAttributeDeclaration, vector, s, hashtable);
        }
    }
    
    private void addGlobalComponents(final Vector vector, final Hashtable hashtable) {
        final XSDDescription xsdDescription = new XSDDescription();
        for (int size = vector.size(), i = 0; i < size; ++i) {
            this.addGlobalComponent((XSObject)vector.elementAt(i), xsdDescription);
        }
        this.updateImportDependencies(hashtable);
    }
    
    private void addGlobalComponent(final XSObject xsObject, final XSDDescription xsdDescription) {
        xsdDescription.setNamespace(xsObject.getNamespace());
        final SchemaGrammar schemaGrammar = this.getSchemaGrammar(xsdDescription);
        final short type = xsObject.getType();
        final String name = xsObject.getName();
        switch (type) {
            case 3: {
                if (((XSTypeDefinition)xsObject).getAnonymous()) {
                    break;
                }
                if (schemaGrammar.getGlobalTypeDecl(name) == null) {
                    schemaGrammar.addGlobalTypeDecl((XSTypeDefinition)xsObject);
                }
                if (schemaGrammar.getGlobalTypeDecl(name, "") == null) {
                    schemaGrammar.addGlobalTypeDecl((XSTypeDefinition)xsObject, "");
                    break;
                }
                break;
            }
            case 1: {
                if (((XSAttributeDecl)xsObject).getScope() != 1) {
                    break;
                }
                if (schemaGrammar.getGlobalAttributeDecl(name) == null) {
                    schemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)xsObject);
                }
                if (schemaGrammar.getGlobalAttributeDecl(name, "") == null) {
                    schemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)xsObject, "");
                    break;
                }
                break;
            }
            case 5: {
                if (schemaGrammar.getGlobalAttributeDecl(name) == null) {
                    schemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)xsObject);
                }
                if (schemaGrammar.getGlobalAttributeDecl(name, "") == null) {
                    schemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)xsObject, "");
                    break;
                }
                break;
            }
            case 2: {
                if (((XSElementDecl)xsObject).getScope() != 1) {
                    break;
                }
                schemaGrammar.addGlobalElementDeclAll((XSElementDecl)xsObject);
                if (schemaGrammar.getGlobalElementDecl(name) == null) {
                    schemaGrammar.addGlobalElementDecl((XSElementDecl)xsObject);
                }
                if (schemaGrammar.getGlobalElementDecl(name, "") == null) {
                    schemaGrammar.addGlobalElementDecl((XSElementDecl)xsObject, "");
                    break;
                }
                break;
            }
            case 6: {
                if (schemaGrammar.getGlobalGroupDecl(name) == null) {
                    schemaGrammar.addGlobalGroupDecl((XSGroupDecl)xsObject);
                }
                if (schemaGrammar.getGlobalGroupDecl(name, "") == null) {
                    schemaGrammar.addGlobalGroupDecl((XSGroupDecl)xsObject, "");
                    break;
                }
                break;
            }
            case 11: {
                if (schemaGrammar.getGlobalNotationDecl(name) == null) {
                    schemaGrammar.addGlobalNotationDecl((XSNotationDecl)xsObject);
                }
                if (schemaGrammar.getGlobalNotationDecl(name, "") == null) {
                    schemaGrammar.addGlobalNotationDecl((XSNotationDecl)xsObject, "");
                    break;
                }
                break;
            }
        }
    }
    
    private void updateImportDependencies(final Hashtable hashtable) {
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final Vector vector = hashtable.get(this.null2EmptyString(s));
            if (vector.size() > 0) {
                this.expandImportList(s, vector);
            }
        }
    }
    
    private void expandImportList(final String s, final Vector vector) {
        final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(s);
        if (grammar != null) {
            final Vector importedGrammars = grammar.getImportedGrammars();
            if (importedGrammars == null) {
                final Vector importedGrammars2 = new Vector();
                this.addImportList(grammar, importedGrammars2, vector);
                grammar.setImportedGrammars(importedGrammars2);
            }
            else {
                this.updateImportList(grammar, importedGrammars, vector);
            }
        }
    }
    
    private void addImportList(final SchemaGrammar schemaGrammar, final Vector vector, final Vector vector2) {
        for (int size = vector2.size(), i = 0; i < size; ++i) {
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(vector2.elementAt(i));
            if (grammar != null) {
                vector.add(grammar);
            }
        }
    }
    
    private void updateImportList(final SchemaGrammar schemaGrammar, final Vector vector, final Vector vector2) {
        for (int size = vector2.size(), i = 0; i < size; ++i) {
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(vector2.elementAt(i));
            if (grammar != null && !this.containedImportedGrammar(vector, grammar)) {
                vector.add(grammar);
            }
        }
    }
    
    private boolean containedImportedGrammar(final Vector vector, final SchemaGrammar schemaGrammar) {
        for (int size = vector.size(), i = 0; i < size; ++i) {
            if (this.null2EmptyString(((SchemaGrammar)vector.elementAt(i)).getTargetNamespace()).equals(this.null2EmptyString(schemaGrammar.getTargetNamespace()))) {
                return true;
            }
        }
        return false;
    }
    
    private SchemaGrammar getSchemaGrammar(final XSDDescription xsdDescription) {
        SchemaGrammar schemaGrammar = this.findGrammar(xsdDescription, this.fNamespaceGrowth);
        if (schemaGrammar == null) {
            schemaGrammar = new SchemaGrammar(xsdDescription.getNamespace(), xsdDescription.makeClone(), this.fSymbolTable);
            this.fGrammarBucket.putGrammar(schemaGrammar);
        }
        else if (schemaGrammar.isImmutable()) {
            schemaGrammar = this.createGrammarFrom(schemaGrammar);
        }
        return schemaGrammar;
    }
    
    private Vector findDependentNamespaces(final String s, final Hashtable hashtable) {
        final String null2EmptyString = this.null2EmptyString(s);
        Vector vector = hashtable.get(null2EmptyString);
        if (vector == null) {
            vector = new Vector();
            hashtable.put(null2EmptyString, vector);
        }
        return vector;
    }
    
    private void addNamespaceDependency(final String s, final String s2, final Vector vector) {
        final String null2EmptyString = this.null2EmptyString(s);
        final String null2EmptyString2 = this.null2EmptyString(s2);
        if (!null2EmptyString.equals(null2EmptyString2) && !vector.contains(null2EmptyString2)) {
            vector.add(null2EmptyString2);
        }
    }
    
    private void reportSharingError(final String s, final String s2) {
        this.reportSchemaError("sch-props-correct.2", new Object[] { (s == null) ? ("," + s2) : (s + "," + s2) }, null);
    }
    
    private void createTraversers() {
        this.fAttributeChecker = new XSAttributeChecker(this);
        this.fAttributeGroupTraverser = new XSDAttributeGroupTraverser(this, this.fAttributeChecker);
        this.fAttributeTraverser = new XSDAttributeTraverser(this, this.fAttributeChecker);
        this.fComplexTypeTraverser = new XSDComplexTypeTraverser(this, this.fAttributeChecker);
        this.fElementTraverser = new XSDElementTraverser(this, this.fAttributeChecker);
        this.fGroupTraverser = new XSDGroupTraverser(this, this.fAttributeChecker);
        this.fKeyrefTraverser = new XSDKeyrefTraverser(this, this.fAttributeChecker);
        this.fNotationTraverser = new XSDNotationTraverser(this, this.fAttributeChecker);
        this.fSimpleTypeTraverser = new XSDSimpleTypeTraverser(this, this.fAttributeChecker);
        this.fUniqueOrKeyTraverser = new XSDUniqueOrKeyTraverser(this, this.fAttributeChecker);
        this.fWildCardTraverser = new XSDWildcardTraverser(this, this.fAttributeChecker);
    }
    
    private void createXSD11Traversers() {
        this.fTypeAlternativeTraverser = new XSDTypeAlternativeTraverser(this, this.fAttributeChecker);
    }
    
    void prepareForParse() {
        this.fTraversed.clear();
        this.fDoc2SystemId.clear();
        if (this.fDoc2DatatypeXMLVersion != null) {
            this.fDoc2DatatypeXMLVersion.clear();
        }
        this.fHiddenNodes.clear();
        this.fLastSchemaWasDuplicate = false;
    }
    
    void prepareForTraverse() {
        this.fUnparsedAttributeRegistry.clear();
        this.fUnparsedAttributeGroupRegistry.clear();
        this.fUnparsedElementRegistry.clear();
        this.fUnparsedGroupRegistry.clear();
        this.fUnparsedIdentityConstraintRegistry.clear();
        this.fUnparsedNotationRegistry.clear();
        this.fUnparsedTypeRegistry.clear();
        this.fUnparsedAttributeRegistrySub.clear();
        this.fUnparsedAttributeGroupRegistrySub.clear();
        this.fUnparsedElementRegistrySub.clear();
        this.fUnparsedGroupRegistrySub.clear();
        this.fUnparsedIdentityConstraintRegistrySub.clear();
        this.fUnparsedNotationRegistrySub.clear();
        this.fUnparsedTypeRegistrySub.clear();
        for (int i = 1; i <= 7; ++i) {
            this.fUnparsedRegistriesExt[i].clear();
        }
        this.fXSDocumentInfoRegistry.clear();
        this.fDependencyMap.clear();
        this.fDoc2XSDocumentMap.clear();
        this.fRedefine2XSDMap.clear();
        this.fRedefine2NSSupport.clear();
        this.fAllTNSs.removeAllElements();
        this.fImportMap.clear();
        this.fRoot = null;
        for (int j = 0; j < this.fLocalElemStackPos; ++j) {
            this.fParticle[j] = null;
            this.fLocalElementDecl[j] = null;
            this.fLocalElementDecl_schema[j] = null;
            this.fLocalElemNamespaceContext[j] = null;
        }
        this.fLocalElemStackPos = 0;
        for (int k = 0; k < this.fKeyrefStackPos; ++k) {
            this.fKeyrefs[k] = null;
            this.fKeyrefElems[k] = null;
            this.fKeyrefNamespaceContext[k] = null;
            this.fKeyrefsMapXSDocumentInfo[k] = null;
        }
        this.fKeyrefStackPos = 0;
        for (int l = 0; l < this.fICReferralStackPos; ++l) {
            this.fICReferrals[l] = null;
            this.fICReferralsMapXSDocumentInfo[l] = null;
            this.fICReferralElems[l] = null;
            this.fICReferralNamespaceContext[l] = null;
        }
        this.fICReferralStackPos = 0;
        if (this.fAttributeChecker == null) {
            this.createTraversers();
        }
        if (this.fSchemaVersion == 4 && this.fTypeAlternativeTraverser == null) {
            this.createXSD11Traversers();
        }
        final Locale locale = this.fErrorReporter.getLocale();
        this.fAttributeChecker.reset(this.fSymbolTable);
        this.fAttributeGroupTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fAttributeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fComplexTypeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fElementTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fGroupTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fKeyrefTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fNotationTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fSimpleTypeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fUniqueOrKeyTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fWildCardTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        if (this.fTypeAlternativeTraverser != null) {
            this.fTypeAlternativeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        }
        this.fRedefinedRestrictedAttributeGroupRegistry.clear();
        this.fRedefinedRestrictedGroupRegistry.clear();
        this.fGlobalAttrDecls.clear();
        this.fGlobalAttrGrpDecls.clear();
        this.fGlobalElemDecls.clear();
        this.fGlobalGroupDecls.clear();
        this.fGlobalNotationDecls.clear();
        this.fGlobalIDConstraintDecls.clear();
        this.fGlobalTypeDecls.clear();
        if (this.fOverrideHandler != null) {
            this.fOverrideHandler.reset();
        }
    }
    
    public void setDeclPool(final XSDeclarationPool fDeclPool) {
        this.fDeclPool = fDeclPool;
    }
    
    public void setDVFactory(final SchemaDVFactory fdvFactory) {
        this.fDVFactory = fdvFactory;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) {
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fEntityResolver = (XMLEntityResolver)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        final XMLEntityResolver entityResolver = (XMLEntityResolver)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
        if (entityResolver != null) {
            this.fSchemaParser.setEntityResolver(entityResolver);
        }
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            final XMLErrorHandler errorHandler = this.fErrorReporter.getErrorHandler();
            if (errorHandler != this.fSchemaParser.getProperty("http://apache.org/xml/properties/internal/error-handler")) {
                this.fSchemaParser.setProperty("http://apache.org/xml/properties/internal/error-handler", (errorHandler != null) ? errorHandler : new DefaultErrorHandler());
                if (this.fAnnotationValidator != null) {
                    this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", (errorHandler != null) ? errorHandler : new DefaultErrorHandler());
                }
            }
            final Locale locale = this.fErrorReporter.getLocale();
            if (locale != this.fSchemaParser.getProperty("http://apache.org/xml/properties/locale")) {
                this.fSchemaParser.setProperty("http://apache.org/xml/properties/locale", locale);
                if (this.fAnnotationValidator != null) {
                    this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", locale);
                }
            }
        }
        catch (final XMLConfigurationException ex) {}
        try {
            this.fValidateAnnotations = xmlComponentManager.getFeature("http://apache.org/xml/features/validate-annotations");
        }
        catch (final XMLConfigurationException ex2) {
            this.fValidateAnnotations = false;
        }
        try {
            this.fHonourAllSchemaLocations = xmlComponentManager.getFeature("http://apache.org/xml/features/honour-all-schemaLocations");
        }
        catch (final XMLConfigurationException ex3) {
            this.fHonourAllSchemaLocations = false;
        }
        try {
            this.fNamespaceGrowth = xmlComponentManager.getFeature("http://apache.org/xml/features/namespace-growth");
        }
        catch (final XMLConfigurationException ex4) {
            this.fNamespaceGrowth = false;
        }
        try {
            this.fTolerateDuplicates = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/tolerate-duplicates");
        }
        catch (final XMLConfigurationException ex5) {
            this.fTolerateDuplicates = false;
        }
        try {
            this.fFullXPathForCTA = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking");
        }
        catch (final XMLConfigurationException ex6) {
            this.fFullXPathForCTA = false;
        }
        try {
            this.fCommentsAndPIsForAssert = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/assert-comments-and-pi-checking");
        }
        catch (final XMLConfigurationException ex7) {
            this.fCommentsAndPIsForAssert = false;
        }
        try {
            this.fSchemaParser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", this.fErrorReporter.getFeature("http://apache.org/xml/features/continue-after-fatal-error"));
        }
        catch (final XMLConfigurationException ex8) {}
        try {
            this.fSchemaParser.setFeature("http://apache.org/xml/features/allow-java-encodings", xmlComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings"));
        }
        catch (final XMLConfigurationException ex9) {}
        try {
            this.fSchemaParser.setFeature("http://apache.org/xml/features/standard-uri-conformant", xmlComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant"));
        }
        catch (final XMLConfigurationException ex10) {}
        try {
            this.fGrammarPool = (XMLGrammarPool)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException ex11) {
            this.fGrammarPool = null;
        }
        try {
            this.fSchemaParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", xmlComponentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl"));
        }
        catch (final XMLConfigurationException ex12) {}
        try {
            final Object property = xmlComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
            if (property != null) {
                this.fSchemaParser.setProperty("http://apache.org/xml/properties/security-manager", property);
            }
        }
        catch (final XMLConfigurationException ex13) {}
        if (this.fSchemaVersion == 4) {
            try {
                final Object property2 = xmlComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version");
                if (property2 instanceof String) {
                    this.fDatatypeXMLVersion = (String)property2;
                }
                else {
                    this.fDatatypeXMLVersion = null;
                }
            }
            catch (final XMLConfigurationException ex14) {
                this.fDatatypeXMLVersion = null;
            }
        }
        this.fTypeValidatorHelper = TypeValidatorHelper.getInstance(this.fSchemaVersion);
    }
    
    void traverseLocalElements() {
        this.fElementTraverser.fDeferTraversingLocalElements = false;
        for (int i = 0; i < this.fLocalElemStackPos; ++i) {
            final Element element = this.fLocalElementDecl[i];
            final XSDocumentInfo xsDocumentInfo = this.fLocalElementDecl_schema[i];
            final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(xsDocumentInfo.fTargetNamespace);
            final XSDocumentInfo overridingSchemaDocument = this.getOverridingSchemaDocument(xsDocumentInfo);
            if (overridingSchemaDocument != null) {
                final SchemaNamespaceSupport fNamespaceSupport = overridingSchemaDocument.fNamespaceSupport;
                final Enumeration allPrefixes = fNamespaceSupport.getAllPrefixes();
                while (allPrefixes.hasMoreElements()) {
                    final String s = allPrefixes.nextElement();
                    if (!xsDocumentInfo.fNamespaceSupport.containsPrefix(s)) {
                        xsDocumentInfo.fNamespaceSupport.declarePrefix(s, fNamespaceSupport.getURI(s));
                    }
                }
            }
            this.fElementTraverser.traverseLocal(this.fParticle[i], element, xsDocumentInfo, grammar, this.fAllContext[i], this.fParent[i], this.fLocalElemNamespaceContext[i]);
            if (this.fParticle[i].fType == 0) {
                XSModelGroupImpl fModelGroup = null;
                if (this.fParent[i] instanceof XSComplexTypeDecl) {
                    final XSParticle particle = ((XSComplexTypeDecl)this.fParent[i]).getParticle();
                    if (particle != null) {
                        fModelGroup = (XSModelGroupImpl)particle.getTerm();
                    }
                }
                else {
                    fModelGroup = ((XSGroupDecl)this.fParent[i]).fModelGroup;
                }
                if (fModelGroup != null) {
                    this.removeParticle(fModelGroup, this.fParticle[i]);
                }
            }
        }
    }
    
    XSDocumentInfo getOverridingSchemaDocument(final XSDocumentInfo xsDocumentInfo) {
        XSDocumentInfo xsDocumentInfo2 = null;
        final Enumeration keys = this.fOverrideDependencyMap.keys();
        while (keys.hasMoreElements()) {
            final XSDocumentInfo xsDocumentInfo3 = (XSDocumentInfo)keys.nextElement();
            if (((Vector)this.fOverrideDependencyMap.get(xsDocumentInfo3)).contains(xsDocumentInfo)) {
                xsDocumentInfo2 = xsDocumentInfo3;
                break;
            }
        }
        return xsDocumentInfo2;
    }
    
    Element getOverridingXSElement(final XSDocumentInfo xsDocumentInfo) {
        final Enumeration keys = this.fOverrideDependencyMap.keys();
        while (keys.hasMoreElements()) {
            final XSDocumentInfo xsDocumentInfo2 = (XSDocumentInfo)keys.nextElement();
            final Vector vector = this.fOverrideDependencyMap.get(xsDocumentInfo2);
            final Vector vector2 = this.fOverrideDependencyMapNsNormalization.get(xsDocumentInfo2);
            for (int i = 0; i < vector.size(); ++i) {
                if (xsDocumentInfo.equals(vector.get(i))) {
                    return (Element)vector2.get(i);
                }
            }
        }
        return null;
    }
    
    private boolean removeParticle(final XSModelGroupImpl xsModelGroupImpl, final XSParticleDecl xsParticleDecl) {
        for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
            final XSParticleDecl xsParticleDecl2 = xsModelGroupImpl.fParticles[i];
            if (xsParticleDecl2 == xsParticleDecl) {
                for (int j = i; j < xsModelGroupImpl.fParticleCount - 1; ++j) {
                    xsModelGroupImpl.fParticles[j] = xsModelGroupImpl.fParticles[j + 1];
                }
                --xsModelGroupImpl.fParticleCount;
                return true;
            }
            if (xsParticleDecl2.fType == 3 && this.removeParticle((XSModelGroupImpl)xsParticleDecl2.fValue, xsParticleDecl)) {
                return true;
            }
        }
        return false;
    }
    
    void fillInLocalElemInfo(final Element element, final XSDocumentInfo xsDocumentInfo, final int n, final XSObject xsObject, final XSParticleDecl xsParticleDecl) {
        if (this.fParticle.length == this.fLocalElemStackPos) {
            final XSParticleDecl[] fParticle = new XSParticleDecl[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fParticle, 0, fParticle, 0, this.fLocalElemStackPos);
            this.fParticle = fParticle;
            final Element[] fLocalElementDecl = new Element[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fLocalElementDecl, 0, fLocalElementDecl, 0, this.fLocalElemStackPos);
            this.fLocalElementDecl = fLocalElementDecl;
            final XSDocumentInfo[] fLocalElementDecl_schema = new XSDocumentInfo[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fLocalElementDecl_schema, 0, fLocalElementDecl_schema, 0, this.fLocalElemStackPos);
            this.fLocalElementDecl_schema = fLocalElementDecl_schema;
            final int[] fAllContext = new int[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fAllContext, 0, fAllContext, 0, this.fLocalElemStackPos);
            this.fAllContext = fAllContext;
            final XSObject[] fParent = new XSObject[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fParent, 0, fParent, 0, this.fLocalElemStackPos);
            this.fParent = fParent;
            final String[][] fLocalElemNamespaceContext = new String[this.fLocalElemStackPos + 10][];
            System.arraycopy(this.fLocalElemNamespaceContext, 0, fLocalElemNamespaceContext, 0, this.fLocalElemStackPos);
            this.fLocalElemNamespaceContext = fLocalElemNamespaceContext;
        }
        this.fParticle[this.fLocalElemStackPos] = xsParticleDecl;
        this.fLocalElementDecl[this.fLocalElemStackPos] = element;
        this.fLocalElementDecl_schema[this.fLocalElemStackPos] = xsDocumentInfo;
        this.fAllContext[this.fLocalElemStackPos] = n;
        this.fParent[this.fLocalElemStackPos] = xsObject;
        this.fLocalElemNamespaceContext[this.fLocalElemStackPos++] = xsDocumentInfo.fNamespaceSupport.getEffectiveLocalContext();
    }
    
    void checkForDuplicateNames(final String s, final int n, final Hashtable hashtable, final Hashtable hashtable2, final Element element, final XSDocumentInfo xsDocumentInfo) {
        final Element value;
        if ((value = hashtable.get(s)) == null) {
            if (this.fNamespaceGrowth && !this.fTolerateDuplicates) {
                this.checkForDuplicateNames(s, n, element);
            }
            hashtable.put(s, element);
            hashtable2.put(s, xsDocumentInfo);
        }
        else {
            final Element element2 = value;
            final XSDocumentInfo xsDocumentInfo2 = hashtable2.get(s);
            if (element2 == element) {
                return;
            }
            XSDocumentInfo xsDocumentInfo3 = null;
            boolean b = true;
            final Element parent;
            if (DOMUtil.getLocalName(parent = DOMUtil.getParent(element2)).equals(SchemaSymbols.ELT_REDEFINE)) {
                xsDocumentInfo3 = (XSDocumentInfo)this.fRedefine2XSDMap.get(parent);
            }
            else if (DOMUtil.getLocalName(DOMUtil.getParent(element)).equals(SchemaSymbols.ELT_REDEFINE)) {
                xsDocumentInfo3 = xsDocumentInfo2;
                b = false;
            }
            if (xsDocumentInfo3 != null) {
                if (xsDocumentInfo2 == xsDocumentInfo) {
                    this.reportSchemaError("sch-props-correct.2", new Object[] { s }, element);
                    return;
                }
                final String string = s.substring(s.lastIndexOf(44) + 1) + "_fn3dktizrknc9pi";
                if (xsDocumentInfo3 == xsDocumentInfo) {
                    element.setAttribute(SchemaSymbols.ATT_NAME, string);
                    if (xsDocumentInfo.fTargetNamespace == null) {
                        hashtable.put("," + string, element);
                        hashtable2.put("," + string, xsDocumentInfo);
                    }
                    else {
                        hashtable.put(xsDocumentInfo.fTargetNamespace + "," + string, element);
                        hashtable2.put(xsDocumentInfo.fTargetNamespace + "," + string, xsDocumentInfo);
                    }
                    if (xsDocumentInfo.fTargetNamespace == null) {
                        this.checkForDuplicateNames("," + string, n, hashtable, hashtable2, element, xsDocumentInfo);
                    }
                    else {
                        this.checkForDuplicateNames(xsDocumentInfo.fTargetNamespace + "," + string, n, hashtable, hashtable2, element, xsDocumentInfo);
                    }
                }
                else if (b) {
                    if (xsDocumentInfo.fTargetNamespace == null) {
                        this.checkForDuplicateNames("," + string, n, hashtable, hashtable2, element, xsDocumentInfo);
                    }
                    else {
                        this.checkForDuplicateNames(xsDocumentInfo.fTargetNamespace + "," + string, n, hashtable, hashtable2, element, xsDocumentInfo);
                    }
                }
                else {
                    this.reportSchemaError("sch-props-correct.2", new Object[] { s }, element);
                }
            }
            else if (!this.fTolerateDuplicates || this.fUnparsedRegistriesExt[n].get(s) == xsDocumentInfo) {
                this.reportSchemaError("sch-props-correct.2", new Object[] { s }, element);
            }
        }
        if (this.fTolerateDuplicates) {
            this.fUnparsedRegistriesExt[n].put(s, xsDocumentInfo);
        }
    }
    
    void checkForDuplicateNames(final String s, final int n, final Element element) {
        final int index = s.indexOf(44);
        final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(this.emptyString2Null(s.substring(0, index)));
        if (grammar != null && this.getGlobalDeclFromGrammar(grammar, n, s.substring(index + 1)) != null) {
            this.reportSchemaError("sch-props-correct.2", new Object[] { s }, element);
        }
    }
    
    private void renameRedefiningComponents(final XSDocumentInfo xsDocumentInfo, final Element element, final String s, final String s2, final String s3) {
        if (s.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            Element element2 = DOMUtil.getFirstChildElement(element);
            if (element2 == null) {
                this.reportSchemaError("src-redefine.5.a.a", null, element);
            }
            else {
                if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    element2 = DOMUtil.getNextSiblingElement(element2);
                }
                if (element2 == null) {
                    this.reportSchemaError("src-redefine.5.a.a", null, element);
                }
                else {
                    final String localName = DOMUtil.getLocalName(element2);
                    if (!localName.equals(SchemaSymbols.ELT_RESTRICTION)) {
                        this.reportSchemaError("src-redefine.5.a.b", new Object[] { localName }, element);
                    }
                    else {
                        final Object[] checkAttributes = this.fAttributeChecker.checkAttributes(element2, false, xsDocumentInfo);
                        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_BASE];
                        if (qName == null || qName.uri != xsDocumentInfo.fTargetNamespace || !qName.localpart.equals(s2)) {
                            this.reportSchemaError("src-redefine.5.a.c", new Object[] { localName, ((xsDocumentInfo.fTargetNamespace == null) ? "" : xsDocumentInfo.fTargetNamespace) + "," + s2 }, element);
                        }
                        else if (qName.prefix != null && qName.prefix.length() > 0) {
                            element2.setAttribute(SchemaSymbols.ATT_BASE, qName.prefix + ":" + s3);
                        }
                        else {
                            element2.setAttribute(SchemaSymbols.ATT_BASE, s3);
                        }
                        this.fAttributeChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
                    }
                }
            }
        }
        else if (s.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
            Element element3 = DOMUtil.getFirstChildElement(element);
            if (element3 == null) {
                this.reportSchemaError("src-redefine.5.b.a", null, element);
            }
            else {
                if (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    element3 = DOMUtil.getNextSiblingElement(element3);
                }
                if (element3 == null) {
                    this.reportSchemaError("src-redefine.5.b.a", null, element);
                }
                else {
                    Element element4 = DOMUtil.getFirstChildElement(element3);
                    if (element4 == null) {
                        this.reportSchemaError("src-redefine.5.b.b", null, element3);
                    }
                    else {
                        if (DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
                            element4 = DOMUtil.getNextSiblingElement(element4);
                        }
                        if (element4 == null) {
                            this.reportSchemaError("src-redefine.5.b.b", null, element3);
                        }
                        else {
                            final String localName2 = DOMUtil.getLocalName(element4);
                            if (!localName2.equals(SchemaSymbols.ELT_RESTRICTION) && !localName2.equals(SchemaSymbols.ELT_EXTENSION)) {
                                this.reportSchemaError("src-redefine.5.b.c", new Object[] { localName2 }, element4);
                            }
                            else {
                                final QName qName2 = (QName)this.fAttributeChecker.checkAttributes(element4, false, xsDocumentInfo)[XSAttributeChecker.ATTIDX_BASE];
                                if (qName2 == null || qName2.uri != xsDocumentInfo.fTargetNamespace || !qName2.localpart.equals(s2)) {
                                    this.reportSchemaError("src-redefine.5.b.d", new Object[] { localName2, ((xsDocumentInfo.fTargetNamespace == null) ? "" : xsDocumentInfo.fTargetNamespace) + "," + s2 }, element4);
                                }
                                else if (qName2.prefix != null && qName2.prefix.length() > 0) {
                                    element4.setAttribute(SchemaSymbols.ATT_BASE, qName2.prefix + ":" + s3);
                                }
                                else {
                                    element4.setAttribute(SchemaSymbols.ATT_BASE, s3);
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (s.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
            final String s4 = (xsDocumentInfo.fTargetNamespace == null) ? ("," + s2) : (xsDocumentInfo.fTargetNamespace + "," + s2);
            final int changeRedefineGroup = this.changeRedefineGroup(s4, s, s3, element, xsDocumentInfo);
            if (changeRedefineGroup > 1) {
                this.reportSchemaError("src-redefine.7.1", new Object[] { new Integer(changeRedefineGroup) }, element);
            }
            else if (changeRedefineGroup != 1) {
                if (xsDocumentInfo.fTargetNamespace == null) {
                    this.fRedefinedRestrictedAttributeGroupRegistry.put(s4, "," + s3);
                }
                else {
                    this.fRedefinedRestrictedAttributeGroupRegistry.put(s4, xsDocumentInfo.fTargetNamespace + "," + s3);
                }
            }
        }
        else if (s.equals(SchemaSymbols.ELT_GROUP)) {
            final String s5 = (xsDocumentInfo.fTargetNamespace == null) ? ("," + s2) : (xsDocumentInfo.fTargetNamespace + "," + s2);
            final int changeRedefineGroup2 = this.changeRedefineGroup(s5, s, s3, element, xsDocumentInfo);
            if (changeRedefineGroup2 > 1) {
                this.reportSchemaError("src-redefine.6.1.1", new Object[] { new Integer(changeRedefineGroup2) }, element);
            }
            else if (changeRedefineGroup2 != 1) {
                if (xsDocumentInfo.fTargetNamespace == null) {
                    this.fRedefinedRestrictedGroupRegistry.put(s5, "," + s3);
                }
                else {
                    this.fRedefinedRestrictedGroupRegistry.put(s5, xsDocumentInfo.fTargetNamespace + "," + s3);
                }
            }
        }
        else {
            this.reportSchemaError("Internal-Error", new Object[] { "could not handle this particular <redefine>; please submit your schemas and instance document in a bug report!" }, element);
        }
    }
    
    private String findQName(final String s, final XSDocumentInfo xsDocumentInfo) {
        final SchemaNamespaceSupport fNamespaceSupport = xsDocumentInfo.fNamespaceSupport;
        final int index = s.indexOf(58);
        String s2 = XMLSymbols.EMPTY_STRING;
        if (index > 0) {
            s2 = s.substring(0, index);
        }
        String s3 = fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(s2));
        final String s4 = (index == 0) ? s : s.substring(index + 1);
        if (s2 == XMLSymbols.EMPTY_STRING && s3 == null && xsDocumentInfo.fIsChameleonSchema) {
            s3 = xsDocumentInfo.fTargetNamespace;
        }
        if (s3 == null) {
            return "," + s4;
        }
        return s3 + "," + s4;
    }
    
    private int changeRedefineGroup(final String s, final String s2, final String s3, final Element element, final XSDocumentInfo xsDocumentInfo) {
        int n = 0;
        for (Element element2 = DOMUtil.getFirstChildElement(element); element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
            if (!DOMUtil.getLocalName(element2).equals(s2)) {
                n += this.changeRedefineGroup(s, s2, s3, element2, xsDocumentInfo);
            }
            else {
                final String attribute = element2.getAttribute(SchemaSymbols.ATT_REF);
                if (attribute.length() != 0 && s.equals(this.findQName(attribute, xsDocumentInfo))) {
                    final String empty_STRING = XMLSymbols.EMPTY_STRING;
                    final int index = attribute.indexOf(":");
                    if (index > 0) {
                        element2.setAttribute(SchemaSymbols.ATT_REF, attribute.substring(0, index) + ":" + s3);
                    }
                    else {
                        element2.setAttribute(SchemaSymbols.ATT_REF, s3);
                    }
                    ++n;
                    if (s2.equals(SchemaSymbols.ELT_GROUP)) {
                        final String attribute2 = element2.getAttribute(SchemaSymbols.ATT_MINOCCURS);
                        final String attribute3 = element2.getAttribute(SchemaSymbols.ATT_MAXOCCURS);
                        if ((attribute3.length() != 0 && !attribute3.equals("1")) || (attribute2.length() != 0 && !attribute2.equals("1"))) {
                            this.reportSchemaError("src-redefine.6.1.2", new Object[] { attribute }, element2);
                        }
                    }
                }
            }
        }
        return n;
    }
    
    private XSDocumentInfo findXSDocumentForDecl(final XSDocumentInfo xsDocumentInfo, final Element element, final XSDocumentInfo xsDocumentInfo2) {
        if (xsDocumentInfo2 == null) {
            return null;
        }
        return xsDocumentInfo2;
    }
    
    private boolean nonAnnotationContent(final Element element) {
        for (Element element2 = DOMUtil.getFirstChildElement(element); element2 != null; element2 = DOMUtil.getNextSiblingElement(element2)) {
            if (!DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                return true;
            }
        }
        return false;
    }
    
    private void setSchemasVisible(final XSDocumentInfo xsDocumentInfo) {
        if (DOMUtil.isHidden(xsDocumentInfo.fSchemaElement, this.fHiddenNodes)) {
            DOMUtil.setVisible(xsDocumentInfo.fSchemaElement, this.fHiddenNodes);
            final Vector vector = this.fDependencyMap.get(xsDocumentInfo);
            for (int i = 0; i < vector.size(); ++i) {
                this.setSchemasVisible((XSDocumentInfo)vector.elementAt(i));
            }
        }
    }
    
    public SimpleLocator element2Locator(final Element element) {
        if (!(element instanceof ElementImpl)) {
            return null;
        }
        final SimpleLocator simpleLocator = new SimpleLocator();
        return this.element2Locator(element, simpleLocator) ? simpleLocator : null;
    }
    
    public boolean element2Locator(final Element element, final SimpleLocator simpleLocator) {
        if (simpleLocator == null) {
            return false;
        }
        if (element instanceof ElementImpl) {
            final ElementImpl elementImpl = (ElementImpl)element;
            final String s = this.fDoc2SystemId.get(DOMUtil.getRoot(elementImpl.getOwnerDocument()));
            simpleLocator.setValues(s, s, elementImpl.getLineNumber(), elementImpl.getColumnNumber(), elementImpl.getCharacterOffset());
            return true;
        }
        return false;
    }
    
    public void reportSchemaError(final String s, final Object[] array, final Element element) {
        this.reportSchemaError(s, array, element, null);
    }
    
    void reportSchemaError(final String s, final Object[] array, final Element element, final Exception ex) {
        if (this.element2Locator(element, this.xl)) {
            this.fErrorReporter.reportError(this.xl, "http://www.w3.org/TR/xml-schema-1", s, array, (short)1, ex);
        }
        else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", s, array, (short)1, ex);
        }
    }
    
    public void reportSchemaWarning(final String s, final Object[] array, final Element element) {
        this.reportSchemaWarning(s, array, element, null);
    }
    
    void reportSchemaWarning(final String s, final Object[] array, final Element element, final Exception ex) {
        if (this.element2Locator(element, this.xl)) {
            this.fErrorReporter.reportError(this.xl, "http://www.w3.org/TR/xml-schema-1", s, array, (short)0, ex);
        }
        else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", s, array, (short)0, ex);
        }
    }
    
    public void setGenerateSyntheticAnnotations(final boolean b) {
        this.fSchemaParser.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", b);
    }
    
    public void setSchemaVersionInfo(final short fSchemaVersion, final XSConstraints fxsConstraints) {
        this.fSchemaVersion = fSchemaVersion;
        this.fXSConstraints = fxsConstraints;
        if (fSchemaVersion < 4) {
            this.fSupportedVersion = XSDHandler.SUPPORTED_VERSION_1_0;
        }
        else {
            this.fSupportedVersion = XSDHandler.SUPPORTED_VERSION_1_1;
            if (this.fOverrideHandler == null) {
                this.fOverrideHandler = new OverrideTransformationManager(this, new DOMOverrideImpl(this));
            }
            if (this.fDoc2DatatypeXMLVersion == null) {
                this.fDoc2DatatypeXMLVersion = new Hashtable();
            }
        }
        this.fSchemaParser.setSupportedVersion(this.fSupportedVersion);
    }
    
    public String getDocumentURI() {
        return this.fSchemaParser.getDocument().getDocumentURI();
    }
    
    public String getDocumentURI(final Element element) {
        return this.doc2SystemId(element);
    }
    
    public short getSchemaVersion() {
        return this.fSchemaVersion;
    }
    
    static {
        EMPTY_TABLE = new Hashtable();
        DECIMAL_DV = new DecimalDV();
        SUPPORTED_VERSION_1_0 = getSupportedVersion("1.0");
        SUPPORTED_VERSION_1_1 = getSupportedVersion("1.1");
        NS_ERROR_CODES = new String[][] { { "src-include.2.1", "src-include.2.1" }, { "src-redefine.3.1", "src-redefine.3.1" }, { "src-import.3.1", "src-import.3.2" }, null, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "src-override.2.1", "src-override.2.1" } };
        ELE_ERROR_CODES = new String[] { "src-include.1", "src-redefine.2", "src-import.2", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4" };
        COMP_TYPE = new String[] { null, "attribute declaration", "attribute group", "element declaration", "group", "identity constraint", "notation", "type definition" };
        CIRCULAR_CODES = new String[] { "Internal-Error", "Internal-Error", "src-attribute_group.3", "e-props-correct.6", "mg-props-correct.2", "Internal-Error", "Internal-Error", "st-props-correct.2" };
    }
    
    private static final class SAX2XNIUtil extends ErrorHandlerWrapper
    {
        public static XMLParseException createXMLParseException0(final SAXParseException ex) {
            return ErrorHandlerWrapper.createXMLParseException(ex);
        }
        
        public static XNIException createXNIException0(final SAXException ex) {
            return ErrorHandlerWrapper.createXNIException(ex);
        }
    }
    
    private static class XSAnnotationGrammarPool implements XMLGrammarPool
    {
        private XSGrammarBucket fGrammarBucket;
        private Grammar[] fInitialGrammarSet;
        private short fSchemaVersion;
        
        XSAnnotationGrammarPool(final short fSchemaVersion) {
            this.fSchemaVersion = fSchemaVersion;
        }
        
        public Grammar[] retrieveInitialGrammarSet(final String s) {
            if (s == "http://www.w3.org/2001/XMLSchema") {
                if (this.fInitialGrammarSet == null) {
                    if (this.fGrammarBucket == null) {
                        this.fInitialGrammarSet = new Grammar[] { SchemaGrammar.Schema4Annotations.getSchema4Annotations(this.fSchemaVersion) };
                    }
                    else {
                        final SchemaGrammar[] grammars = this.fGrammarBucket.getGrammars();
                        for (int i = 0; i < grammars.length; ++i) {
                            if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(grammars[i].getTargetNamespace())) {
                                return this.fInitialGrammarSet = grammars;
                            }
                        }
                        final Grammar[] fInitialGrammarSet = new Grammar[grammars.length + 1];
                        System.arraycopy(grammars, 0, fInitialGrammarSet, 0, grammars.length);
                        fInitialGrammarSet[fInitialGrammarSet.length - 1] = SchemaGrammar.Schema4Annotations.getSchema4Annotations(this.fSchemaVersion);
                        this.fInitialGrammarSet = fInitialGrammarSet;
                    }
                }
                return this.fInitialGrammarSet;
            }
            return new Grammar[0];
        }
        
        public void cacheGrammars(final String s, final Grammar[] array) {
        }
        
        public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            if (xmlGrammarDescription.getGrammarType() == "http://www.w3.org/2001/XMLSchema") {
                final String targetNamespace = ((XMLSchemaDescription)xmlGrammarDescription).getTargetNamespace();
                if (this.fGrammarBucket != null) {
                    final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(targetNamespace);
                    if (grammar != null) {
                        return grammar;
                    }
                }
                if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(targetNamespace)) {
                    return SchemaGrammar.Schema4Annotations.getSchema4Annotations(this.fSchemaVersion);
                }
            }
            return null;
        }
        
        public void refreshGrammars(final XSGrammarBucket fGrammarBucket) {
            this.fGrammarBucket = fGrammarBucket;
            this.fInitialGrammarSet = null;
        }
        
        public void lockPool() {
        }
        
        public void unlockPool() {
        }
        
        public void clear() {
        }
    }
    
    private static class XSDKey
    {
        String systemId;
        short referType;
        String referNS;
        
        XSDKey(final String systemId, final short referType, final String referNS) {
            this.systemId = systemId;
            this.referType = referType;
            this.referNS = referNS;
        }
        
        public int hashCode() {
            return (this.referNS == null) ? 0 : this.referNS.hashCode();
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof XSDKey)) {
                return false;
            }
            final XSDKey xsdKey = (XSDKey)o;
            return this.referNS == xsdKey.referNS && this.systemId != null && this.systemId.equals(xsdKey.systemId);
        }
    }
}
