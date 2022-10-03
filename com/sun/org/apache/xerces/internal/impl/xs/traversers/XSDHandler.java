package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import java.util.Iterator;
import com.sun.org.apache.xerces.internal.xs.XSTerm;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.StringList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.Collections;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.XSNotationDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import java.util.Stack;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSInputSource;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaParsingConfig;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOM;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSDDescription;
import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import java.util.Vector;
import org.w3c.dom.Element;
import java.util.Map;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;

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
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    protected static final boolean DEBUG_NODE_POOL = false;
    static final int ATTRIBUTE_TYPE = 1;
    static final int ATTRIBUTEGROUP_TYPE = 2;
    static final int ELEMENT_TYPE = 3;
    static final int GROUP_TYPE = 4;
    static final int IDENTITYCONSTRAINT_TYPE = 5;
    static final int NOTATION_TYPE = 6;
    static final int TYPEDECL_TYPE = 7;
    public static final String REDEF_IDENTIFIER = "_fn3dktizrknc9pi";
    protected XSDeclarationPool fDeclPool;
    protected XMLSecurityManager fSecurityManager;
    private String fAccessExternalSchema;
    private String fAccessExternalDTD;
    private boolean registryEmpty;
    private Map<String, Element> fUnparsedAttributeRegistry;
    private Map<String, Element> fUnparsedAttributeGroupRegistry;
    private Map<String, Element> fUnparsedElementRegistry;
    private Map<String, Element> fUnparsedGroupRegistry;
    private Map<String, Element> fUnparsedIdentityConstraintRegistry;
    private Map<String, Element> fUnparsedNotationRegistry;
    private Map<String, Element> fUnparsedTypeRegistry;
    private Map<String, XSDocumentInfo> fUnparsedAttributeRegistrySub;
    private Map<String, XSDocumentInfo> fUnparsedAttributeGroupRegistrySub;
    private Map<String, XSDocumentInfo> fUnparsedElementRegistrySub;
    private Map<String, XSDocumentInfo> fUnparsedGroupRegistrySub;
    private Map<String, XSDocumentInfo> fUnparsedIdentityConstraintRegistrySub;
    private Map<String, XSDocumentInfo> fUnparsedNotationRegistrySub;
    private Map<String, XSDocumentInfo> fUnparsedTypeRegistrySub;
    private Map<String, XSDocumentInfo>[] fUnparsedRegistriesExt;
    private Map<XSDocumentInfo, Vector<XSDocumentInfo>> fDependencyMap;
    private Map<String, Vector> fImportMap;
    private Vector<String> fAllTNSs;
    private Map<String, XMLSchemaLoader.LocationArray> fLocationPairs;
    Map<Node, String> fHiddenNodes;
    private Map<XSDKey, Element> fTraversed;
    private Map<Element, String> fDoc2SystemId;
    private XSDocumentInfo fRoot;
    private Map fDoc2XSDocumentMap;
    private Map fRedefine2XSDMap;
    private Map fRedefine2NSSupport;
    private Map fRedefinedRestrictedAttributeGroupRegistry;
    private Map fRedefinedRestrictedGroupRegistry;
    private boolean fLastSchemaWasDuplicate;
    private boolean fValidateAnnotations;
    private boolean fHonourAllSchemaLocations;
    boolean fNamespaceGrowth;
    boolean fTolerateDuplicates;
    private XMLErrorReporter fErrorReporter;
    private XMLErrorHandler fErrorHandler;
    private Locale fLocale;
    private XMLEntityResolver fEntityManager;
    private XSAttributeChecker fAttributeChecker;
    private SymbolTable fSymbolTable;
    private XSGrammarBucket fGrammarBucket;
    private XSDDescription fSchemaGrammarDescription;
    private XMLGrammarPool fGrammarPool;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;
    private boolean fOverrideDefaultParser;
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
    SchemaDVFactory fDVFactory;
    SchemaDOMParser fSchemaParser;
    SchemaContentHandler fXSContentHandler;
    StAXSchemaParser fStAXSchemaParser;
    XML11Configuration fAnnotationValidator;
    XSAnnotationGrammarPool fGrammarBucketAdapter;
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
    private static final String[][] NS_ERROR_CODES;
    private static final String[] ELE_ERROR_CODES;
    private Vector fReportedTNS;
    private static final String[] COMP_TYPE;
    private static final String[] CIRCULAR_CODES;
    private SimpleLocator xl;
    
    private String null2EmptyString(final String ns) {
        return (ns == null) ? XMLSymbols.EMPTY_STRING : ns;
    }
    
    private String emptyString2Null(final String ns) {
        return (ns == XMLSymbols.EMPTY_STRING) ? null : ns;
    }
    
    private String doc2SystemId(final Element ele) {
        String documentURI = null;
        if (ele.getOwnerDocument() instanceof SchemaDOM) {
            documentURI = ((SchemaDOM)ele.getOwnerDocument()).getDocumentURI();
        }
        return (documentURI != null) ? documentURI : this.fDoc2SystemId.get(ele);
    }
    
    public XSDHandler() {
        this.fDeclPool = null;
        this.fSecurityManager = null;
        this.registryEmpty = true;
        this.fUnparsedAttributeRegistry = new HashMap<String, Element>();
        this.fUnparsedAttributeGroupRegistry = new HashMap<String, Element>();
        this.fUnparsedElementRegistry = new HashMap<String, Element>();
        this.fUnparsedGroupRegistry = new HashMap<String, Element>();
        this.fUnparsedIdentityConstraintRegistry = new HashMap<String, Element>();
        this.fUnparsedNotationRegistry = new HashMap<String, Element>();
        this.fUnparsedTypeRegistry = new HashMap<String, Element>();
        this.fUnparsedAttributeRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedAttributeGroupRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedElementRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedGroupRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedIdentityConstraintRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedNotationRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedTypeRegistrySub = new HashMap<String, XSDocumentInfo>();
        this.fUnparsedRegistriesExt = new HashMap[] { null, null, null, null, null, null, null, null };
        this.fDependencyMap = new HashMap<XSDocumentInfo, Vector<XSDocumentInfo>>();
        this.fImportMap = new HashMap<String, Vector>();
        this.fAllTNSs = new Vector<String>();
        this.fLocationPairs = null;
        this.fHiddenNodes = null;
        this.fTraversed = new HashMap<XSDKey, Element>();
        this.fDoc2SystemId = new HashMap<Element, String>();
        this.fRoot = null;
        this.fDoc2XSDocumentMap = new HashMap();
        this.fRedefine2XSDMap = null;
        this.fRedefine2NSSupport = null;
        this.fRedefinedRestrictedAttributeGroupRegistry = new HashMap();
        this.fRedefinedRestrictedGroupRegistry = new HashMap();
        this.fValidateAnnotations = false;
        this.fHonourAllSchemaLocations = false;
        this.fNamespaceGrowth = false;
        this.fTolerateDuplicates = false;
        this.fSecurityPropertyMgr = null;
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
        this.fReportedTNS = null;
        this.xl = new SimpleLocator();
        this.fHiddenNodes = new HashMap<Node, String>();
        this.fSchemaParser = new SchemaDOMParser(new SchemaParsingConfig());
    }
    
    public XSDHandler(final XSGrammarBucket gBucket) {
        this();
        this.fGrammarBucket = gBucket;
        this.fSchemaGrammarDescription = new XSDDescription();
    }
    
    public SchemaGrammar parseSchema(final XMLInputSource is, final XSDDescription desc, final Map<String, XMLSchemaLoader.LocationArray> locationPairs) throws IOException {
        this.fLocationPairs = locationPairs;
        this.fSchemaParser.resetNodePool();
        SchemaGrammar grammar = null;
        String schemaNamespace = null;
        final short referType = desc.getContextType();
        if (referType != 3) {
            if (this.fHonourAllSchemaLocations && referType == 2 && this.isExistingGrammar(desc, this.fNamespaceGrowth)) {
                grammar = this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
            }
            else {
                grammar = this.findGrammar(desc, this.fNamespaceGrowth);
            }
            if (grammar != null) {
                if (!this.fNamespaceGrowth) {
                    return grammar;
                }
                try {
                    if (grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(is.getSystemId(), is.getBaseSystemId(), false))) {
                        return grammar;
                    }
                }
                catch (final URI.MalformedURIException ex) {}
            }
            schemaNamespace = desc.getTargetNamespace();
            if (schemaNamespace != null) {
                schemaNamespace = this.fSymbolTable.addSymbol(schemaNamespace);
            }
        }
        this.prepareForParse();
        Element schemaRoot = null;
        if (is instanceof DOMInputSource) {
            schemaRoot = this.getSchemaDocument(schemaNamespace, (DOMInputSource)is, referType == 3, referType, null);
        }
        else if (is instanceof SAXInputSource) {
            schemaRoot = this.getSchemaDocument(schemaNamespace, (SAXInputSource)is, referType == 3, referType, null);
        }
        else if (is instanceof StAXInputSource) {
            schemaRoot = this.getSchemaDocument(schemaNamespace, (StAXInputSource)is, referType == 3, referType, null);
        }
        else if (is instanceof XSInputSource) {
            schemaRoot = this.getSchemaDocument((XSInputSource)is, desc);
        }
        else {
            schemaRoot = this.getSchemaDocument(schemaNamespace, is, referType == 3, referType, null);
        }
        if (schemaRoot == null) {
            if (is instanceof XSInputSource) {
                return this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
            }
            return grammar;
        }
        else {
            if (referType == 3) {
                final Element schemaElem = schemaRoot;
                schemaNamespace = DOMUtil.getAttrValue(schemaElem, SchemaSymbols.ATT_TARGETNAMESPACE);
                if (schemaNamespace != null && schemaNamespace.length() > 0) {
                    schemaNamespace = this.fSymbolTable.addSymbol(schemaNamespace);
                    desc.setTargetNamespace(schemaNamespace);
                }
                else {
                    schemaNamespace = null;
                }
                grammar = this.findGrammar(desc, this.fNamespaceGrowth);
                final String schemaId = XMLEntityManager.expandSystemId(is.getSystemId(), is.getBaseSystemId(), false);
                if (grammar != null && (!this.fNamespaceGrowth || (schemaId != null && grammar.getDocumentLocations().contains(schemaId)))) {
                    return grammar;
                }
                final XSDKey key = new XSDKey(schemaId, referType, schemaNamespace);
                this.fTraversed.put(key, schemaRoot);
                if (schemaId != null) {
                    this.fDoc2SystemId.put(schemaRoot, schemaId);
                }
            }
            this.prepareForTraverse();
            this.fRoot = this.constructTrees(schemaRoot, is.getSystemId(), desc, grammar != null);
            if (this.fRoot == null) {
                return null;
            }
            this.buildGlobalNameRegistries();
            final ArrayList annotationInfo = this.fValidateAnnotations ? new ArrayList() : null;
            this.traverseSchemas(annotationInfo);
            this.traverseLocalElements();
            this.resolveKeyRefs();
            for (int i = this.fAllTNSs.size() - 1; i >= 0; --i) {
                final String tns = this.fAllTNSs.elementAt(i);
                final Vector ins = this.fImportMap.get(tns);
                final SchemaGrammar sg = this.fGrammarBucket.getGrammar(this.emptyString2Null(tns));
                if (sg != null) {
                    int count = 0;
                    for (int j = 0; j < ins.size(); ++j) {
                        final SchemaGrammar isg = this.fGrammarBucket.getGrammar(ins.elementAt(j));
                        if (isg != null) {
                            ins.setElementAt(isg, count++);
                        }
                    }
                    ins.setSize(count);
                    sg.setImportedGrammars(ins);
                }
            }
            if (this.fValidateAnnotations && annotationInfo.size() > 0) {
                this.validateAnnotations(annotationInfo);
            }
            return this.fGrammarBucket.getGrammar(this.fRoot.fTargetNamespace);
        }
    }
    
    private void validateAnnotations(final ArrayList annotationInfo) {
        if (this.fAnnotationValidator == null) {
            this.createAnnotationValidator();
        }
        final int size = annotationInfo.size();
        final XMLInputSource src = new XMLInputSource(null, null, null);
        this.fGrammarBucketAdapter.refreshGrammars(this.fGrammarBucket);
        for (int i = 0; i < size; i += 2) {
            src.setSystemId(annotationInfo.get(i));
            for (XSAnnotationInfo annotation = annotationInfo.get(i + 1); annotation != null; annotation = annotation.next) {
                src.setCharacterStream(new StringReader(annotation.fAnnotation));
                try {
                    this.fAnnotationValidator.parse(src);
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    private void createAnnotationValidator() {
        this.fAnnotationValidator = new XML11Configuration();
        this.fGrammarBucketAdapter = new XSAnnotationGrammarPool();
        this.fAnnotationValidator.setFeature("http://xml.org/sax/features/validation", true);
        this.fAnnotationValidator.setFeature("http://apache.org/xml/features/validation/schema", true);
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarBucketAdapter);
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/security-manager", (this.fSecurityManager != null) ? this.fSecurityManager : new XMLSecurityManager(true));
        this.fAnnotationValidator.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", (this.fErrorHandler != null) ? this.fErrorHandler : new DefaultErrorHandler());
        this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", this.fLocale);
    }
    
    SchemaGrammar getGrammar(final String tns) {
        return this.fGrammarBucket.getGrammar(tns);
    }
    
    protected SchemaGrammar findGrammar(final XSDDescription desc, final boolean ignoreConflict) {
        SchemaGrammar sg = this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
        if (sg == null && this.fGrammarPool != null) {
            sg = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(desc);
            if (sg != null && !this.fGrammarBucket.putGrammar(sg, true, ignoreConflict)) {
                this.reportSchemaWarning("GrammarConflict", null, null);
                sg = null;
            }
        }
        return sg;
    }
    
    protected XSDocumentInfo constructTrees(final Element schemaRoot, final String locationHint, final XSDDescription desc, final boolean nsCollision) {
        if (schemaRoot == null) {
            return null;
        }
        String callerTNS = desc.getTargetNamespace();
        final short referType = desc.getContextType();
        XSDocumentInfo currSchemaInfo = null;
        try {
            currSchemaInfo = new XSDocumentInfo(schemaRoot, this.fAttributeChecker, this.fSymbolTable);
        }
        catch (final XMLSchemaException se) {
            this.reportSchemaError(XSDHandler.ELE_ERROR_CODES[referType], new Object[] { locationHint }, schemaRoot);
            return null;
        }
        if (currSchemaInfo.fTargetNamespace != null && currSchemaInfo.fTargetNamespace.length() == 0) {
            this.reportSchemaWarning("EmptyTargetNamespace", new Object[] { locationHint }, schemaRoot);
            currSchemaInfo.fTargetNamespace = null;
        }
        if (callerTNS != null) {
            final int secondIdx = 0;
            if (referType == 0 || referType == 1) {
                if (currSchemaInfo.fTargetNamespace == null) {
                    currSchemaInfo.fTargetNamespace = callerTNS;
                    currSchemaInfo.fIsChameleonSchema = true;
                }
                else if (callerTNS != currSchemaInfo.fTargetNamespace) {
                    this.reportSchemaError(XSDHandler.NS_ERROR_CODES[referType][secondIdx], new Object[] { callerTNS, currSchemaInfo.fTargetNamespace }, schemaRoot);
                    return null;
                }
            }
            else if (referType != 3 && callerTNS != currSchemaInfo.fTargetNamespace) {
                this.reportSchemaError(XSDHandler.NS_ERROR_CODES[referType][secondIdx], new Object[] { callerTNS, currSchemaInfo.fTargetNamespace }, schemaRoot);
                return null;
            }
        }
        else if (currSchemaInfo.fTargetNamespace != null) {
            if (referType != 3) {
                final int secondIdx = 1;
                this.reportSchemaError(XSDHandler.NS_ERROR_CODES[referType][secondIdx], new Object[] { callerTNS, currSchemaInfo.fTargetNamespace }, schemaRoot);
                return null;
            }
            desc.setTargetNamespace(currSchemaInfo.fTargetNamespace);
            callerTNS = currSchemaInfo.fTargetNamespace;
        }
        currSchemaInfo.addAllowedNS(currSchemaInfo.fTargetNamespace);
        SchemaGrammar sg = null;
        if (nsCollision) {
            final SchemaGrammar sg2 = this.fGrammarBucket.getGrammar(currSchemaInfo.fTargetNamespace);
            if (sg2.isImmutable()) {
                sg = new SchemaGrammar(sg2);
                this.fGrammarBucket.putGrammar(sg);
                this.updateImportListWith(sg);
            }
            else {
                sg = sg2;
            }
            this.updateImportListFor(sg);
        }
        else if (referType == 0 || referType == 1) {
            sg = this.fGrammarBucket.getGrammar(currSchemaInfo.fTargetNamespace);
        }
        else if (this.fHonourAllSchemaLocations && referType == 2) {
            sg = this.findGrammar(desc, false);
            if (sg == null) {
                sg = new SchemaGrammar(currSchemaInfo.fTargetNamespace, desc.makeClone(), this.fSymbolTable);
                this.fGrammarBucket.putGrammar(sg);
            }
        }
        else {
            sg = new SchemaGrammar(currSchemaInfo.fTargetNamespace, desc.makeClone(), this.fSymbolTable);
            this.fGrammarBucket.putGrammar(sg);
        }
        sg.addDocument(null, this.fDoc2SystemId.get(currSchemaInfo.fSchemaElement));
        this.fDoc2XSDocumentMap.put(schemaRoot, currSchemaInfo);
        final Vector<XSDocumentInfo> dependencies = new Vector<XSDocumentInfo>();
        final Element rootNode = schemaRoot;
        Element newSchemaRoot = null;
        for (Element child = DOMUtil.getFirstChildElement(rootNode); child != null; child = DOMUtil.getNextSiblingElement(child)) {
            String schemaNamespace = null;
            String schemaHint = null;
            final String localName = DOMUtil.getLocalName(child);
            short refType = -1;
            boolean importCollision = false;
            if (!localName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                if (localName.equals(SchemaSymbols.ELT_IMPORT)) {
                    refType = 2;
                    final Object[] importAttrs = this.fAttributeChecker.checkAttributes(child, true, currSchemaInfo);
                    schemaHint = (String)importAttrs[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
                    schemaNamespace = (String)importAttrs[XSAttributeChecker.ATTIDX_NAMESPACE];
                    if (schemaNamespace != null) {
                        schemaNamespace = this.fSymbolTable.addSymbol(schemaNamespace);
                    }
                    final Element importChild = DOMUtil.getFirstChildElement(child);
                    if (importChild != null) {
                        final String importComponentType = DOMUtil.getLocalName(importChild);
                        if (importComponentType.equals(SchemaSymbols.ELT_ANNOTATION)) {
                            sg.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(importChild, importAttrs, true, currSchemaInfo));
                        }
                        else {
                            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", importComponentType }, child);
                        }
                        if (DOMUtil.getNextSiblingElement(importChild) != null) {
                            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(importChild)) }, child);
                        }
                    }
                    else {
                        final String text = DOMUtil.getSyntheticAnnotation(child);
                        if (text != null) {
                            sg.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(child, text, importAttrs, true, currSchemaInfo));
                        }
                    }
                    this.fAttributeChecker.returnAttrArray(importAttrs, currSchemaInfo);
                    if (schemaNamespace == currSchemaInfo.fTargetNamespace) {
                        this.reportSchemaError((schemaNamespace != null) ? "src-import.1.1" : "src-import.1.2", new Object[] { schemaNamespace }, child);
                        continue;
                    }
                    if (currSchemaInfo.isAllowedNS(schemaNamespace)) {
                        if (!this.fHonourAllSchemaLocations && !this.fNamespaceGrowth) {
                            continue;
                        }
                    }
                    else {
                        currSchemaInfo.addAllowedNS(schemaNamespace);
                    }
                    final String tns = this.null2EmptyString(currSchemaInfo.fTargetNamespace);
                    Vector ins = this.fImportMap.get(tns);
                    if (ins == null) {
                        this.fAllTNSs.addElement(tns);
                        ins = new Vector();
                        this.fImportMap.put(tns, ins);
                        ins.addElement(schemaNamespace);
                    }
                    else if (!ins.contains(schemaNamespace)) {
                        ins.addElement(schemaNamespace);
                    }
                    this.fSchemaGrammarDescription.reset();
                    this.fSchemaGrammarDescription.setContextType((short)2);
                    this.fSchemaGrammarDescription.setBaseSystemId(this.doc2SystemId(schemaRoot));
                    this.fSchemaGrammarDescription.setLiteralSystemId(schemaHint);
                    this.fSchemaGrammarDescription.setLocationHints(new String[] { schemaHint });
                    this.fSchemaGrammarDescription.setTargetNamespace(schemaNamespace);
                    final SchemaGrammar isg = this.findGrammar(this.fSchemaGrammarDescription, this.fNamespaceGrowth);
                    if (isg != null) {
                        if (this.fNamespaceGrowth) {
                            try {
                                if (isg.getDocumentLocations().contains(XMLEntityManager.expandSystemId(schemaHint, this.fSchemaGrammarDescription.getBaseSystemId(), false))) {
                                    continue;
                                }
                                importCollision = true;
                            }
                            catch (final URI.MalformedURIException ex) {}
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
                    newSchemaRoot = this.resolveSchema(this.fSchemaGrammarDescription, false, child, isg == null);
                }
                else {
                    if (!localName.equals(SchemaSymbols.ELT_INCLUDE) && !localName.equals(SchemaSymbols.ELT_REDEFINE)) {
                        break;
                    }
                    final Object[] includeAttrs = this.fAttributeChecker.checkAttributes(child, true, currSchemaInfo);
                    schemaHint = (String)includeAttrs[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
                    if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
                        if (this.fRedefine2NSSupport == null) {
                            this.fRedefine2NSSupport = new HashMap();
                        }
                        this.fRedefine2NSSupport.put(child, new SchemaNamespaceSupport(currSchemaInfo.fNamespaceSupport));
                    }
                    if (localName.equals(SchemaSymbols.ELT_INCLUDE)) {
                        final Element includeChild = DOMUtil.getFirstChildElement(child);
                        if (includeChild != null) {
                            final String includeComponentType = DOMUtil.getLocalName(includeChild);
                            if (includeComponentType.equals(SchemaSymbols.ELT_ANNOTATION)) {
                                sg.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(includeChild, includeAttrs, true, currSchemaInfo));
                            }
                            else {
                                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", includeComponentType }, child);
                            }
                            if (DOMUtil.getNextSiblingElement(includeChild) != null) {
                                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(includeChild)) }, child);
                            }
                        }
                        else {
                            final String text = DOMUtil.getSyntheticAnnotation(child);
                            if (text != null) {
                                sg.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(child, text, includeAttrs, true, currSchemaInfo));
                            }
                        }
                    }
                    else {
                        for (Element redefinedChild = DOMUtil.getFirstChildElement(child); redefinedChild != null; redefinedChild = DOMUtil.getNextSiblingElement(redefinedChild)) {
                            final String redefinedComponentType = DOMUtil.getLocalName(redefinedChild);
                            if (redefinedComponentType.equals(SchemaSymbols.ELT_ANNOTATION)) {
                                sg.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(redefinedChild, includeAttrs, true, currSchemaInfo));
                                DOMUtil.setHidden(redefinedChild, this.fHiddenNodes);
                            }
                            else {
                                final String text2 = DOMUtil.getSyntheticAnnotation(child);
                                if (text2 != null) {
                                    sg.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(child, text2, includeAttrs, true, currSchemaInfo));
                                }
                            }
                        }
                    }
                    this.fAttributeChecker.returnAttrArray(includeAttrs, currSchemaInfo);
                    if (schemaHint == null) {
                        this.reportSchemaError("s4s-att-must-appear", new Object[] { "<include> or <redefine>", "schemaLocation" }, child);
                    }
                    boolean mustResolve = false;
                    refType = 0;
                    if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
                        mustResolve = this.nonAnnotationContent(child);
                        refType = 1;
                    }
                    this.fSchemaGrammarDescription.reset();
                    this.fSchemaGrammarDescription.setContextType(refType);
                    this.fSchemaGrammarDescription.setBaseSystemId(this.doc2SystemId(schemaRoot));
                    this.fSchemaGrammarDescription.setLocationHints(new String[] { schemaHint });
                    this.fSchemaGrammarDescription.setTargetNamespace(callerTNS);
                    boolean alreadyTraversed = false;
                    final XMLInputSource schemaSource = this.resolveSchemaSource(this.fSchemaGrammarDescription, mustResolve, child, true);
                    if (this.fNamespaceGrowth && refType == 0) {
                        try {
                            final String schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
                            alreadyTraversed = sg.getDocumentLocations().contains(schemaId);
                        }
                        catch (final URI.MalformedURIException ex2) {}
                    }
                    if (!alreadyTraversed) {
                        newSchemaRoot = this.resolveSchema(schemaSource, this.fSchemaGrammarDescription, mustResolve, child);
                        schemaNamespace = currSchemaInfo.fTargetNamespace;
                    }
                    else {
                        this.fLastSchemaWasDuplicate = true;
                    }
                }
                XSDocumentInfo newSchemaInfo = null;
                if (this.fLastSchemaWasDuplicate) {
                    newSchemaInfo = ((newSchemaRoot == null) ? null : this.fDoc2XSDocumentMap.get(newSchemaRoot));
                }
                else {
                    newSchemaInfo = this.constructTrees(newSchemaRoot, schemaHint, this.fSchemaGrammarDescription, importCollision);
                }
                if (localName.equals(SchemaSymbols.ELT_REDEFINE) && newSchemaInfo != null) {
                    if (this.fRedefine2XSDMap == null) {
                        this.fRedefine2XSDMap = new HashMap();
                    }
                    this.fRedefine2XSDMap.put(child, newSchemaInfo);
                }
                if (newSchemaRoot != null) {
                    if (newSchemaInfo != null) {
                        dependencies.addElement(newSchemaInfo);
                    }
                    newSchemaRoot = null;
                }
            }
        }
        this.fDependencyMap.put(currSchemaInfo, dependencies);
        return currSchemaInfo;
    }
    
    private boolean isExistingGrammar(final XSDDescription desc, final boolean ignoreConflict) {
        final SchemaGrammar sg = this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
        if (sg == null) {
            return this.findGrammar(desc, ignoreConflict) != null;
        }
        if (sg.isImmutable()) {
            return true;
        }
        try {
            return sg.getDocumentLocations().contains(XMLEntityManager.expandSystemId(desc.getLiteralSystemId(), desc.getBaseSystemId(), false));
        }
        catch (final URI.MalformedURIException e) {
            return false;
        }
    }
    
    private void updateImportListFor(final SchemaGrammar grammar) {
        final Vector importedGrammars = grammar.getImportedGrammars();
        if (importedGrammars != null) {
            for (int i = 0; i < importedGrammars.size(); ++i) {
                final SchemaGrammar isg1 = importedGrammars.elementAt(i);
                final SchemaGrammar isg2 = this.fGrammarBucket.getGrammar(isg1.getTargetNamespace());
                if (isg2 != null && isg1 != isg2) {
                    importedGrammars.set(i, isg2);
                }
            }
        }
    }
    
    private void updateImportListWith(final SchemaGrammar newGrammar) {
        final SchemaGrammar[] schemaGrammars = this.fGrammarBucket.getGrammars();
        for (int i = 0; i < schemaGrammars.length; ++i) {
            final SchemaGrammar sg = schemaGrammars[i];
            if (sg != newGrammar) {
                final Vector importedGrammars = sg.getImportedGrammars();
                if (importedGrammars != null) {
                    int j = 0;
                    while (j < importedGrammars.size()) {
                        final SchemaGrammar isg = importedGrammars.elementAt(j);
                        if (this.null2EmptyString(isg.getTargetNamespace()).equals(this.null2EmptyString(newGrammar.getTargetNamespace()))) {
                            if (isg != newGrammar) {
                                importedGrammars.set(j, newGrammar);
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
        this.registryEmpty = false;
        final Stack schemasToProcess = new Stack();
        schemasToProcess.push(this.fRoot);
        while (!schemasToProcess.empty()) {
            final XSDocumentInfo currSchemaDoc = schemasToProcess.pop();
            final Element currDoc = currSchemaDoc.fSchemaElement;
            if (DOMUtil.isHidden(currDoc, this.fHiddenNodes)) {
                continue;
            }
            final Element currRoot = currDoc;
            boolean dependenciesCanOccur = true;
            for (Element globalComp = DOMUtil.getFirstChildElement(currRoot); globalComp != null; globalComp = DOMUtil.getNextSiblingElement(globalComp)) {
                if (!DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    if (DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_INCLUDE) || DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_IMPORT)) {
                        if (!dependenciesCanOccur) {
                            this.reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName(globalComp) }, globalComp);
                        }
                        DOMUtil.setHidden(globalComp, this.fHiddenNodes);
                    }
                    else if (DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_REDEFINE)) {
                        if (!dependenciesCanOccur) {
                            this.reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName(globalComp) }, globalComp);
                        }
                        for (Element redefineComp = DOMUtil.getFirstChildElement(globalComp); redefineComp != null; redefineComp = DOMUtil.getNextSiblingElement(redefineComp)) {
                            final String lName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME);
                            if (lName.length() != 0) {
                                final String qName = (currSchemaDoc.fTargetNamespace == null) ? ("," + lName) : (currSchemaDoc.fTargetNamespace + "," + lName);
                                final String componentType = DOMUtil.getLocalName(redefineComp);
                                if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                                    this.checkForDuplicateNames(qName, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, redefineComp, currSchemaDoc);
                                    final String targetLName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                                    this.renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_ATTRIBUTEGROUP, lName, targetLName);
                                }
                                else if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE) || componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                                    this.checkForDuplicateNames(qName, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, redefineComp, currSchemaDoc);
                                    final String targetLName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                                    if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                                        this.renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_COMPLEXTYPE, lName, targetLName);
                                    }
                                    else {
                                        this.renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_SIMPLETYPE, lName, targetLName);
                                    }
                                }
                                else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
                                    this.checkForDuplicateNames(qName, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, redefineComp, currSchemaDoc);
                                    final String targetLName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                                    this.renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_GROUP, lName, targetLName);
                                }
                            }
                        }
                    }
                    else {
                        dependenciesCanOccur = false;
                        final String lName2 = DOMUtil.getAttrValue(globalComp, SchemaSymbols.ATT_NAME);
                        if (lName2.length() != 0) {
                            final String qName2 = (currSchemaDoc.fTargetNamespace == null) ? ("," + lName2) : (currSchemaDoc.fTargetNamespace + "," + lName2);
                            final String componentType2 = DOMUtil.getLocalName(globalComp);
                            if (componentType2.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                                this.checkForDuplicateNames(qName2, 1, this.fUnparsedAttributeRegistry, this.fUnparsedAttributeRegistrySub, globalComp, currSchemaDoc);
                            }
                            else if (componentType2.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                                this.checkForDuplicateNames(qName2, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, globalComp, currSchemaDoc);
                            }
                            else if (componentType2.equals(SchemaSymbols.ELT_COMPLEXTYPE) || componentType2.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                                this.checkForDuplicateNames(qName2, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, globalComp, currSchemaDoc);
                            }
                            else if (componentType2.equals(SchemaSymbols.ELT_ELEMENT)) {
                                this.checkForDuplicateNames(qName2, 3, this.fUnparsedElementRegistry, this.fUnparsedElementRegistrySub, globalComp, currSchemaDoc);
                            }
                            else if (componentType2.equals(SchemaSymbols.ELT_GROUP)) {
                                this.checkForDuplicateNames(qName2, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, globalComp, currSchemaDoc);
                            }
                            else if (componentType2.equals(SchemaSymbols.ELT_NOTATION)) {
                                this.checkForDuplicateNames(qName2, 6, this.fUnparsedNotationRegistry, this.fUnparsedNotationRegistrySub, globalComp, currSchemaDoc);
                            }
                        }
                    }
                }
            }
            DOMUtil.setHidden(currDoc, this.fHiddenNodes);
            final Vector<XSDocumentInfo> currSchemaDepends = this.fDependencyMap.get(currSchemaDoc);
            for (int i = 0; i < currSchemaDepends.size(); ++i) {
                schemasToProcess.push(currSchemaDepends.elementAt(i));
            }
        }
    }
    
    protected void traverseSchemas(final ArrayList annotationInfo) {
        this.setSchemasVisible(this.fRoot);
        final Stack schemasToProcess = new Stack();
        schemasToProcess.push(this.fRoot);
        while (!schemasToProcess.empty()) {
            final XSDocumentInfo currSchemaDoc = schemasToProcess.pop();
            final Element currDoc = currSchemaDoc.fSchemaElement;
            final SchemaGrammar currSG = this.fGrammarBucket.getGrammar(currSchemaDoc.fTargetNamespace);
            if (DOMUtil.isHidden(currDoc, this.fHiddenNodes)) {
                continue;
            }
            final Element currRoot = currDoc;
            boolean sawAnnotation = false;
            for (Element globalComp = DOMUtil.getFirstVisibleChildElement(currRoot, this.fHiddenNodes); globalComp != null; globalComp = DOMUtil.getNextVisibleSiblingElement(globalComp, this.fHiddenNodes)) {
                DOMUtil.setHidden(globalComp, this.fHiddenNodes);
                final String componentType = DOMUtil.getLocalName(globalComp);
                if (DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_REDEFINE)) {
                    currSchemaDoc.backupNSSupport((this.fRedefine2NSSupport != null) ? this.fRedefine2NSSupport.get(globalComp) : null);
                    for (Element redefinedComp = DOMUtil.getFirstVisibleChildElement(globalComp, this.fHiddenNodes); redefinedComp != null; redefinedComp = DOMUtil.getNextVisibleSiblingElement(redefinedComp, this.fHiddenNodes)) {
                        final String redefinedComponentType = DOMUtil.getLocalName(redefinedComp);
                        DOMUtil.setHidden(redefinedComp, this.fHiddenNodes);
                        if (redefinedComponentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                            this.fAttributeGroupTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
                        }
                        else if (redefinedComponentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                            this.fComplexTypeTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
                        }
                        else if (redefinedComponentType.equals(SchemaSymbols.ELT_GROUP)) {
                            this.fGroupTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
                        }
                        else if (redefinedComponentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                            this.fSimpleTypeTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
                        }
                        else {
                            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { DOMUtil.getLocalName(globalComp), "(annotation | (simpleType | complexType | group | attributeGroup))*", redefinedComponentType }, redefinedComp);
                        }
                    }
                    currSchemaDoc.restoreNSSupport();
                }
                else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                    this.fAttributeTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                    this.fAttributeGroupTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                    this.fComplexTypeTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_ELEMENT)) {
                    this.fElementTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
                    this.fGroupTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_NOTATION)) {
                    this.fNotationTraverser.traverse(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                    this.fSimpleTypeTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
                }
                else if (componentType.equals(SchemaSymbols.ELT_ANNOTATION)) {
                    currSG.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(globalComp, currSchemaDoc.getSchemaAttrs(), true, currSchemaDoc));
                    sawAnnotation = true;
                }
                else {
                    this.reportSchemaError("s4s-elt-invalid-content.1", new Object[] { SchemaSymbols.ELT_SCHEMA, DOMUtil.getLocalName(globalComp) }, globalComp);
                }
            }
            if (!sawAnnotation) {
                final String text = DOMUtil.getSyntheticAnnotation(currRoot);
                if (text != null) {
                    currSG.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(currRoot, text, currSchemaDoc.getSchemaAttrs(), true, currSchemaDoc));
                }
            }
            if (annotationInfo != null) {
                final XSAnnotationInfo info = currSchemaDoc.getAnnotations();
                if (info != null) {
                    annotationInfo.add(this.doc2SystemId(currDoc));
                    annotationInfo.add(info);
                }
            }
            currSchemaDoc.returnSchemaAttrs();
            DOMUtil.setHidden(currDoc, this.fHiddenNodes);
            final Vector<XSDocumentInfo> currSchemaDepends = this.fDependencyMap.get(currSchemaDoc);
            for (int i = 0; i < currSchemaDepends.size(); ++i) {
                schemasToProcess.push(currSchemaDepends.elementAt(i));
            }
        }
    }
    
    private final boolean needReportTNSError(final String uri) {
        if (this.fReportedTNS == null) {
            this.fReportedTNS = new Vector();
        }
        else if (this.fReportedTNS.contains(uri)) {
            return false;
        }
        this.fReportedTNS.addElement(uri);
        return true;
    }
    
    void addGlobalAttributeDecl(final XSAttributeDecl decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getName()) : (namespace + "," + decl.getName());
        if (this.fGlobalAttrDecls.get(declKey) == null) {
            this.fGlobalAttrDecls.put(declKey, decl);
        }
    }
    
    void addGlobalAttributeGroupDecl(final XSAttributeGroupDecl decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getName()) : (namespace + "," + decl.getName());
        if (this.fGlobalAttrGrpDecls.get(declKey) == null) {
            this.fGlobalAttrGrpDecls.put(declKey, decl);
        }
    }
    
    void addGlobalElementDecl(final XSElementDecl decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getName()) : (namespace + "," + decl.getName());
        if (this.fGlobalElemDecls.get(declKey) == null) {
            this.fGlobalElemDecls.put(declKey, decl);
        }
    }
    
    void addGlobalGroupDecl(final XSGroupDecl decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getName()) : (namespace + "," + decl.getName());
        if (this.fGlobalGroupDecls.get(declKey) == null) {
            this.fGlobalGroupDecls.put(declKey, decl);
        }
    }
    
    void addGlobalNotationDecl(final XSNotationDecl decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getName()) : (namespace + "," + decl.getName());
        if (this.fGlobalNotationDecls.get(declKey) == null) {
            this.fGlobalNotationDecls.put(declKey, decl);
        }
    }
    
    void addGlobalTypeDecl(final XSTypeDefinition decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getName()) : (namespace + "," + decl.getName());
        if (this.fGlobalTypeDecls.get(declKey) == null) {
            this.fGlobalTypeDecls.put(declKey, decl);
        }
    }
    
    void addIDConstraintDecl(final IdentityConstraint decl) {
        final String namespace = decl.getNamespace();
        final String declKey = (namespace == null || namespace.length() == 0) ? ("," + decl.getIdentityConstraintName()) : (namespace + "," + decl.getIdentityConstraintName());
        if (this.fGlobalIDConstraintDecls.get(declKey) == null) {
            this.fGlobalIDConstraintDecls.put(declKey, decl);
        }
    }
    
    private XSAttributeDecl getGlobalAttributeDecl(final String declKey) {
        return (XSAttributeDecl)this.fGlobalAttrDecls.get(declKey);
    }
    
    private XSAttributeGroupDecl getGlobalAttributeGroupDecl(final String declKey) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(declKey);
    }
    
    private XSElementDecl getGlobalElementDecl(final String declKey) {
        return (XSElementDecl)this.fGlobalElemDecls.get(declKey);
    }
    
    private XSGroupDecl getGlobalGroupDecl(final String declKey) {
        return (XSGroupDecl)this.fGlobalGroupDecls.get(declKey);
    }
    
    private XSNotationDecl getGlobalNotationDecl(final String declKey) {
        return (XSNotationDecl)this.fGlobalNotationDecls.get(declKey);
    }
    
    private XSTypeDefinition getGlobalTypeDecl(final String declKey) {
        return (XSTypeDefinition)this.fGlobalTypeDecls.get(declKey);
    }
    
    private IdentityConstraint getIDConstraintDecl(final String declKey) {
        return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(declKey);
    }
    
    protected Object getGlobalDecl(final XSDocumentInfo currSchema, final int declType, final QName declToTraverse, final Element elmNode) {
        if (declToTraverse.uri != null && declToTraverse.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && declType == 7) {
            final Object retObj = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(declToTraverse.localpart);
            if (retObj != null) {
                return retObj;
            }
        }
        if (!currSchema.isAllowedNS(declToTraverse.uri) && currSchema.needReportTNSError(declToTraverse.uri)) {
            final String code = (declToTraverse.uri == null) ? "src-resolve.4.1" : "src-resolve.4.2";
            this.reportSchemaError(code, new Object[] { this.fDoc2SystemId.get(currSchema.fSchemaElement), declToTraverse.uri, declToTraverse.rawname }, elmNode);
        }
        final SchemaGrammar sGrammar = this.fGrammarBucket.getGrammar(declToTraverse.uri);
        if (sGrammar == null) {
            if (this.needReportTNSError(declToTraverse.uri)) {
                this.reportSchemaError("src-resolve", new Object[] { declToTraverse.rawname, XSDHandler.COMP_TYPE[declType] }, elmNode);
            }
            return null;
        }
        final Object retObj2 = this.getGlobalDeclFromGrammar(sGrammar, declType, declToTraverse.localpart);
        final String declKey = (declToTraverse.uri == null) ? ("," + declToTraverse.localpart) : (declToTraverse.uri + "," + declToTraverse.localpart);
        if (!this.fTolerateDuplicates) {
            if (retObj2 != null) {
                return retObj2;
            }
        }
        else {
            final Object retObj3 = this.getGlobalDecl(declKey, declType);
            if (retObj3 != null) {
                return retObj3;
            }
        }
        XSDocumentInfo schemaWithDecl = null;
        Element decl = null;
        XSDocumentInfo declDoc = null;
        switch (declType) {
            case 1: {
                decl = this.getElementFromMap(this.fUnparsedAttributeRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedAttributeRegistrySub, declKey);
                break;
            }
            case 2: {
                decl = this.getElementFromMap(this.fUnparsedAttributeGroupRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedAttributeGroupRegistrySub, declKey);
                break;
            }
            case 3: {
                decl = this.getElementFromMap(this.fUnparsedElementRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedElementRegistrySub, declKey);
                break;
            }
            case 4: {
                decl = this.getElementFromMap(this.fUnparsedGroupRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedGroupRegistrySub, declKey);
                break;
            }
            case 5: {
                decl = this.getElementFromMap(this.fUnparsedIdentityConstraintRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedIdentityConstraintRegistrySub, declKey);
                break;
            }
            case 6: {
                decl = this.getElementFromMap(this.fUnparsedNotationRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedNotationRegistrySub, declKey);
                break;
            }
            case 7: {
                decl = this.getElementFromMap(this.fUnparsedTypeRegistry, declKey);
                declDoc = this.getDocInfoFromMap(this.fUnparsedTypeRegistrySub, declKey);
                break;
            }
            default: {
                this.reportSchemaError("Internal-Error", new Object[] { "XSDHandler asked to locate component of type " + declType + "; it does not recognize this type!" }, elmNode);
                break;
            }
        }
        if (decl == null) {
            if (retObj2 == null) {
                this.reportSchemaError("src-resolve", new Object[] { declToTraverse.rawname, XSDHandler.COMP_TYPE[declType] }, elmNode);
            }
            return retObj2;
        }
        schemaWithDecl = this.findXSDocumentForDecl(currSchema, decl, declDoc);
        if (schemaWithDecl == null) {
            if (retObj2 == null) {
                final String code2 = (declToTraverse.uri == null) ? "src-resolve.4.1" : "src-resolve.4.2";
                this.reportSchemaError(code2, new Object[] { this.fDoc2SystemId.get(currSchema.fSchemaElement), declToTraverse.uri, declToTraverse.rawname }, elmNode);
            }
            return retObj2;
        }
        if (DOMUtil.isHidden(decl, this.fHiddenNodes)) {
            if (retObj2 == null) {
                String code2 = XSDHandler.CIRCULAR_CODES[declType];
                if (declType == 7 && SchemaSymbols.ELT_COMPLEXTYPE.equals(DOMUtil.getLocalName(decl))) {
                    code2 = "ct-props-correct.3";
                }
                this.reportSchemaError(code2, new Object[] { declToTraverse.prefix + ":" + declToTraverse.localpart }, elmNode);
            }
            return retObj2;
        }
        return this.traverseGlobalDecl(declType, decl, schemaWithDecl, sGrammar);
    }
    
    protected Object getGlobalDecl(final String declKey, final int declType) {
        Object retObj = null;
        switch (declType) {
            case 1: {
                retObj = this.getGlobalAttributeDecl(declKey);
                break;
            }
            case 2: {
                retObj = this.getGlobalAttributeGroupDecl(declKey);
                break;
            }
            case 3: {
                retObj = this.getGlobalElementDecl(declKey);
                break;
            }
            case 4: {
                retObj = this.getGlobalGroupDecl(declKey);
                break;
            }
            case 5: {
                retObj = this.getIDConstraintDecl(declKey);
                break;
            }
            case 6: {
                retObj = this.getGlobalNotationDecl(declKey);
                break;
            }
            case 7: {
                retObj = this.getGlobalTypeDecl(declKey);
                break;
            }
        }
        return retObj;
    }
    
    protected Object getGlobalDeclFromGrammar(final SchemaGrammar sGrammar, final int declType, final String localpart) {
        Object retObj = null;
        switch (declType) {
            case 1: {
                retObj = sGrammar.getGlobalAttributeDecl(localpart);
                break;
            }
            case 2: {
                retObj = sGrammar.getGlobalAttributeGroupDecl(localpart);
                break;
            }
            case 3: {
                retObj = sGrammar.getGlobalElementDecl(localpart);
                break;
            }
            case 4: {
                retObj = sGrammar.getGlobalGroupDecl(localpart);
                break;
            }
            case 5: {
                retObj = sGrammar.getIDConstraintDecl(localpart);
                break;
            }
            case 6: {
                retObj = sGrammar.getGlobalNotationDecl(localpart);
                break;
            }
            case 7: {
                retObj = sGrammar.getGlobalTypeDecl(localpart);
                break;
            }
        }
        return retObj;
    }
    
    protected Object getGlobalDeclFromGrammar(final SchemaGrammar sGrammar, final int declType, final String localpart, final String schemaLoc) {
        Object retObj = null;
        switch (declType) {
            case 1: {
                retObj = sGrammar.getGlobalAttributeDecl(localpart, schemaLoc);
                break;
            }
            case 2: {
                retObj = sGrammar.getGlobalAttributeGroupDecl(localpart, schemaLoc);
                break;
            }
            case 3: {
                retObj = sGrammar.getGlobalElementDecl(localpart, schemaLoc);
                break;
            }
            case 4: {
                retObj = sGrammar.getGlobalGroupDecl(localpart, schemaLoc);
                break;
            }
            case 5: {
                retObj = sGrammar.getIDConstraintDecl(localpart, schemaLoc);
                break;
            }
            case 6: {
                retObj = sGrammar.getGlobalNotationDecl(localpart, schemaLoc);
                break;
            }
            case 7: {
                retObj = sGrammar.getGlobalTypeDecl(localpart, schemaLoc);
                break;
            }
        }
        return retObj;
    }
    
    protected Object traverseGlobalDecl(final int declType, final Element decl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        Object retObj = null;
        DOMUtil.setHidden(decl, this.fHiddenNodes);
        SchemaNamespaceSupport nsSupport = null;
        final Element parent = DOMUtil.getParent(decl);
        if (DOMUtil.getLocalName(parent).equals(SchemaSymbols.ELT_REDEFINE)) {
            nsSupport = ((this.fRedefine2NSSupport != null) ? this.fRedefine2NSSupport.get(parent) : null);
        }
        schemaDoc.backupNSSupport(nsSupport);
        switch (declType) {
            case 7: {
                if (DOMUtil.getLocalName(decl).equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                    retObj = this.fComplexTypeTraverser.traverseGlobal(decl, schemaDoc, grammar);
                    break;
                }
                retObj = this.fSimpleTypeTraverser.traverseGlobal(decl, schemaDoc, grammar);
                break;
            }
            case 1: {
                retObj = this.fAttributeTraverser.traverseGlobal(decl, schemaDoc, grammar);
                break;
            }
            case 3: {
                retObj = this.fElementTraverser.traverseGlobal(decl, schemaDoc, grammar);
                break;
            }
            case 2: {
                retObj = this.fAttributeGroupTraverser.traverseGlobal(decl, schemaDoc, grammar);
                break;
            }
            case 4: {
                retObj = this.fGroupTraverser.traverseGlobal(decl, schemaDoc, grammar);
                break;
            }
            case 6: {
                retObj = this.fNotationTraverser.traverse(decl, schemaDoc, grammar);
                break;
            }
        }
        schemaDoc.restoreNSSupport();
        return retObj;
    }
    
    public String schemaDocument2SystemId(final XSDocumentInfo schemaDoc) {
        return this.fDoc2SystemId.get(schemaDoc.fSchemaElement);
    }
    
    Object getGrpOrAttrGrpRedefinedByRestriction(final int type, final QName name, final XSDocumentInfo currSchema, final Element elmNode) {
        final String realName = (name.uri != null) ? (name.uri + "," + name.localpart) : ("," + name.localpart);
        String nameToFind = null;
        switch (type) {
            case 2: {
                nameToFind = this.fRedefinedRestrictedAttributeGroupRegistry.get(realName);
                break;
            }
            case 4: {
                nameToFind = this.fRedefinedRestrictedGroupRegistry.get(realName);
                break;
            }
            default: {
                return null;
            }
        }
        if (nameToFind == null) {
            return null;
        }
        final int commaPos = nameToFind.indexOf(",");
        final QName qNameToFind = new QName(XMLSymbols.EMPTY_STRING, nameToFind.substring(commaPos + 1), nameToFind.substring(commaPos), (commaPos == 0) ? null : nameToFind.substring(0, commaPos));
        final Object retObj = this.getGlobalDecl(currSchema, type, qNameToFind, elmNode);
        if (retObj == null) {
            switch (type) {
                case 2: {
                    this.reportSchemaError("src-redefine.7.2.1", new Object[] { name.localpart }, elmNode);
                    break;
                }
                case 4: {
                    this.reportSchemaError("src-redefine.6.2.1", new Object[] { name.localpart }, elmNode);
                    break;
                }
            }
            return null;
        }
        return retObj;
    }
    
    protected void resolveKeyRefs() {
        for (int i = 0; i < this.fKeyrefStackPos; ++i) {
            final XSDocumentInfo keyrefSchemaDoc = this.fKeyrefsMapXSDocumentInfo[i];
            keyrefSchemaDoc.fNamespaceSupport.makeGlobal();
            keyrefSchemaDoc.fNamespaceSupport.setEffectiveContext(this.fKeyrefNamespaceContext[i]);
            final SchemaGrammar keyrefGrammar = this.fGrammarBucket.getGrammar(keyrefSchemaDoc.fTargetNamespace);
            DOMUtil.setHidden(this.fKeyrefs[i], this.fHiddenNodes);
            this.fKeyrefTraverser.traverse(this.fKeyrefs[i], this.fKeyrefElems[i], keyrefSchemaDoc, keyrefGrammar);
        }
    }
    
    protected Map getIDRegistry() {
        return this.fUnparsedIdentityConstraintRegistry;
    }
    
    protected Map getIDRegistry_sub() {
        return this.fUnparsedIdentityConstraintRegistrySub;
    }
    
    protected void storeKeyRef(final Element keyrefToStore, final XSDocumentInfo schemaDoc, final XSElementDecl currElemDecl) {
        final String keyrefName = DOMUtil.getAttrValue(keyrefToStore, SchemaSymbols.ATT_NAME);
        if (keyrefName.length() != 0) {
            final String keyrefQName = (schemaDoc.fTargetNamespace == null) ? ("," + keyrefName) : (schemaDoc.fTargetNamespace + "," + keyrefName);
            this.checkForDuplicateNames(keyrefQName, 5, this.fUnparsedIdentityConstraintRegistry, this.fUnparsedIdentityConstraintRegistrySub, keyrefToStore, schemaDoc);
        }
        if (this.fKeyrefStackPos == this.fKeyrefs.length) {
            final Element[] elemArray = new Element[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefs, 0, elemArray, 0, this.fKeyrefStackPos);
            this.fKeyrefs = elemArray;
            final XSElementDecl[] declArray = new XSElementDecl[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefElems, 0, declArray, 0, this.fKeyrefStackPos);
            this.fKeyrefElems = declArray;
            final String[][] stringArray = new String[this.fKeyrefStackPos + 2][];
            System.arraycopy(this.fKeyrefNamespaceContext, 0, stringArray, 0, this.fKeyrefStackPos);
            this.fKeyrefNamespaceContext = stringArray;
            final XSDocumentInfo[] xsDocumentInfo = new XSDocumentInfo[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefsMapXSDocumentInfo, 0, xsDocumentInfo, 0, this.fKeyrefStackPos);
            this.fKeyrefsMapXSDocumentInfo = xsDocumentInfo;
        }
        this.fKeyrefs[this.fKeyrefStackPos] = keyrefToStore;
        this.fKeyrefElems[this.fKeyrefStackPos] = currElemDecl;
        this.fKeyrefNamespaceContext[this.fKeyrefStackPos] = schemaDoc.fNamespaceSupport.getEffectiveLocalContext();
        this.fKeyrefsMapXSDocumentInfo[this.fKeyrefStackPos++] = schemaDoc;
    }
    
    private Element resolveSchema(final XSDDescription desc, final boolean mustResolve, final Element referElement, final boolean usePairs) {
        XMLInputSource schemaSource = null;
        try {
            final Map<String, XMLSchemaLoader.LocationArray> pairs = usePairs ? this.fLocationPairs : Collections.emptyMap();
            schemaSource = XMLSchemaLoader.resolveDocument(desc, pairs, this.fEntityManager);
        }
        catch (final IOException ex) {
            if (mustResolve) {
                this.reportSchemaError("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
            }
            else {
                this.reportSchemaWarning("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
            }
        }
        if (schemaSource instanceof DOMInputSource) {
            return this.getSchemaDocument(desc.getTargetNamespace(), (DOMInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
        }
        if (schemaSource instanceof SAXInputSource) {
            return this.getSchemaDocument(desc.getTargetNamespace(), (SAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
        }
        if (schemaSource instanceof StAXInputSource) {
            return this.getSchemaDocument(desc.getTargetNamespace(), (StAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
        }
        if (schemaSource instanceof XSInputSource) {
            return this.getSchemaDocument((XSInputSource)schemaSource, desc);
        }
        return this.getSchemaDocument(desc.getTargetNamespace(), schemaSource, mustResolve, desc.getContextType(), referElement);
    }
    
    private Element resolveSchema(final XMLInputSource schemaSource, final XSDDescription desc, final boolean mustResolve, final Element referElement) {
        if (schemaSource instanceof DOMInputSource) {
            return this.getSchemaDocument(desc.getTargetNamespace(), (DOMInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
        }
        if (schemaSource instanceof SAXInputSource) {
            return this.getSchemaDocument(desc.getTargetNamespace(), (SAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
        }
        if (schemaSource instanceof StAXInputSource) {
            return this.getSchemaDocument(desc.getTargetNamespace(), (StAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
        }
        if (schemaSource instanceof XSInputSource) {
            return this.getSchemaDocument((XSInputSource)schemaSource, desc);
        }
        return this.getSchemaDocument(desc.getTargetNamespace(), schemaSource, mustResolve, desc.getContextType(), referElement);
    }
    
    private XMLInputSource resolveSchemaSource(final XSDDescription desc, final boolean mustResolve, final Element referElement, final boolean usePairs) {
        XMLInputSource schemaSource = null;
        try {
            final Map<String, XMLSchemaLoader.LocationArray> pairs = usePairs ? this.fLocationPairs : Collections.emptyMap();
            schemaSource = XMLSchemaLoader.resolveDocument(desc, pairs, this.fEntityManager);
        }
        catch (final IOException ex) {
            if (mustResolve) {
                this.reportSchemaError("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
            }
            else {
                this.reportSchemaWarning("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
            }
        }
        return schemaSource;
    }
    
    private Element getSchemaDocument(final String schemaNamespace, final XMLInputSource schemaSource, final boolean mustResolve, final short referType, final Element referElement) {
        boolean hasInput = true;
        IOException exception = null;
        Element schemaElement = null;
        try {
            if (schemaSource != null && (schemaSource.getSystemId() != null || schemaSource.getByteStream() != null || schemaSource.getCharacterStream() != null)) {
                XSDKey key = null;
                String schemaId = null;
                if (referType != 3) {
                    schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
                    key = new XSDKey(schemaId, referType, schemaNamespace);
                    if ((schemaElement = this.fTraversed.get(key)) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return schemaElement;
                    }
                    if (referType == 2 || referType == 0 || referType == 1) {
                        final String accessError = SecuritySupport.checkAccess(schemaId, this.fAccessExternalSchema, "all");
                        if (accessError != null) {
                            this.reportSchemaFatalError("schema_reference.access", new Object[] { SecuritySupport.sanitizePath(schemaId), accessError }, referElement);
                        }
                    }
                }
                this.fSchemaParser.parse(schemaSource);
                final Document schemaDocument = this.fSchemaParser.getDocument();
                schemaElement = ((schemaDocument != null) ? DOMUtil.getRoot(schemaDocument) : null);
                return this.getSchemaDocument0(key, schemaId, schemaElement);
            }
            hasInput = false;
        }
        catch (final IOException ex) {
            exception = ex;
        }
        return this.getSchemaDocument1(mustResolve, hasInput, schemaSource, referElement, exception);
    }
    
    private Element getSchemaDocument(final String schemaNamespace, final SAXInputSource schemaSource, final boolean mustResolve, final short referType, final Element referElement) {
        XMLReader parser = schemaSource.getXMLReader();
        final InputSource inputSource = schemaSource.getInputSource();
        boolean hasInput = true;
        IOException exception = null;
        Element schemaElement = null;
        try {
            if (inputSource != null && (inputSource.getSystemId() != null || inputSource.getByteStream() != null || inputSource.getCharacterStream() != null)) {
                XSDKey key = null;
                String schemaId = null;
                if (referType != 3) {
                    schemaId = XMLEntityManager.expandSystemId(inputSource.getSystemId(), schemaSource.getBaseSystemId(), false);
                    key = new XSDKey(schemaId, referType, schemaNamespace);
                    if ((schemaElement = this.fTraversed.get(key)) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return schemaElement;
                    }
                }
                boolean namespacePrefixes = false;
                if (parser != null) {
                    try {
                        namespacePrefixes = parser.getFeature("http://xml.org/sax/features/namespace-prefixes");
                    }
                    catch (final SAXException ex) {}
                }
                else {
                    parser = JdkXmlUtils.getXMLReader(this.fOverrideDefaultParser, this.fSecurityManager.isSecureProcessing());
                    try {
                        parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                        namespacePrefixes = true;
                        if (parser instanceof SAXParser && this.fSecurityManager != null) {
                            parser.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
                        }
                    }
                    catch (final SAXException ex2) {}
                    try {
                        parser.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this.fAccessExternalDTD);
                    }
                    catch (final SAXNotRecognizedException exc) {
                        XMLSecurityManager.printWarning(parser.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", exc);
                    }
                }
                boolean stringsInternalized = false;
                try {
                    stringsInternalized = parser.getFeature("http://xml.org/sax/features/string-interning");
                }
                catch (final SAXException ex3) {}
                if (this.fXSContentHandler == null) {
                    this.fXSContentHandler = new SchemaContentHandler();
                }
                this.fXSContentHandler.reset(this.fSchemaParser, this.fSymbolTable, namespacePrefixes, stringsInternalized);
                parser.setContentHandler(this.fXSContentHandler);
                parser.setErrorHandler(this.fErrorReporter.getSAXErrorHandler());
                parser.parse(inputSource);
                try {
                    parser.setContentHandler(null);
                    parser.setErrorHandler(null);
                }
                catch (final Exception ex4) {}
                final Document schemaDocument = this.fXSContentHandler.getDocument();
                schemaElement = ((schemaDocument != null) ? DOMUtil.getRoot(schemaDocument) : null);
                return this.getSchemaDocument0(key, schemaId, schemaElement);
            }
            hasInput = false;
        }
        catch (final SAXParseException spe) {
            throw SAX2XNIUtil.createXMLParseException0(spe);
        }
        catch (final SAXException se) {
            throw SAX2XNIUtil.createXNIException0(se);
        }
        catch (final IOException ioe) {
            exception = ioe;
        }
        return this.getSchemaDocument1(mustResolve, hasInput, schemaSource, referElement, exception);
    }
    
    private Element getSchemaDocument(final String schemaNamespace, final DOMInputSource schemaSource, final boolean mustResolve, final short referType, final Element referElement) {
        boolean hasInput = true;
        IOException exception = null;
        Element schemaElement = null;
        Element schemaRootElement = null;
        final Node node = schemaSource.getNode();
        short nodeType = -1;
        if (node != null) {
            nodeType = node.getNodeType();
            if (nodeType == 9) {
                schemaRootElement = DOMUtil.getRoot((Document)node);
            }
            else if (nodeType == 1) {
                schemaRootElement = (Element)node;
            }
        }
        try {
            if (schemaRootElement != null) {
                XSDKey key = null;
                String schemaId = null;
                if (referType != 3) {
                    schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
                    boolean isDocument = nodeType == 9;
                    if (!isDocument) {
                        final Node parent = schemaRootElement.getParentNode();
                        if (parent != null) {
                            isDocument = (parent.getNodeType() == 9);
                        }
                    }
                    if (isDocument) {
                        key = new XSDKey(schemaId, referType, schemaNamespace);
                        if ((schemaElement = this.fTraversed.get(key)) != null) {
                            this.fLastSchemaWasDuplicate = true;
                            return schemaElement;
                        }
                    }
                }
                schemaElement = schemaRootElement;
                return this.getSchemaDocument0(key, schemaId, schemaElement);
            }
            hasInput = false;
        }
        catch (final IOException ioe) {
            exception = ioe;
        }
        return this.getSchemaDocument1(mustResolve, hasInput, schemaSource, referElement, exception);
    }
    
    private Element getSchemaDocument(final String schemaNamespace, final StAXInputSource schemaSource, final boolean mustResolve, final short referType, final Element referElement) {
        IOException exception = null;
        Element schemaElement = null;
        try {
            final boolean consumeRemainingContent = schemaSource.shouldConsumeRemainingContent();
            final XMLStreamReader streamReader = schemaSource.getXMLStreamReader();
            final XMLEventReader eventReader = schemaSource.getXMLEventReader();
            XSDKey key = null;
            String schemaId = null;
            if (referType != 3) {
                schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
                boolean isDocument = consumeRemainingContent;
                if (!isDocument) {
                    if (streamReader != null) {
                        isDocument = (streamReader.getEventType() == 7);
                    }
                    else {
                        isDocument = eventReader.peek().isStartDocument();
                    }
                }
                if (isDocument) {
                    key = new XSDKey(schemaId, referType, schemaNamespace);
                    if ((schemaElement = this.fTraversed.get(key)) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return schemaElement;
                    }
                }
            }
            if (this.fStAXSchemaParser == null) {
                this.fStAXSchemaParser = new StAXSchemaParser();
            }
            this.fStAXSchemaParser.reset(this.fSchemaParser, this.fSymbolTable);
            if (streamReader != null) {
                this.fStAXSchemaParser.parse(streamReader);
                if (consumeRemainingContent) {
                    while (streamReader.hasNext()) {
                        streamReader.next();
                    }
                }
            }
            else {
                this.fStAXSchemaParser.parse(eventReader);
                if (consumeRemainingContent) {
                    while (eventReader.hasNext()) {
                        eventReader.nextEvent();
                    }
                }
            }
            final Document schemaDocument = this.fStAXSchemaParser.getDocument();
            schemaElement = ((schemaDocument != null) ? DOMUtil.getRoot(schemaDocument) : null);
            return this.getSchemaDocument0(key, schemaId, schemaElement);
        }
        catch (final XMLStreamException e) {
            final StAXLocationWrapper slw = new StAXLocationWrapper();
            slw.setLocation(e.getLocation());
            throw new XMLParseException(slw, e.getMessage(), e);
        }
        catch (final IOException e2) {
            exception = e2;
            return this.getSchemaDocument1(mustResolve, true, schemaSource, referElement, exception);
        }
    }
    
    private Element getSchemaDocument0(final XSDKey key, final String schemaId, final Element schemaElement) {
        if (key != null) {
            this.fTraversed.put(key, schemaElement);
        }
        if (schemaId != null) {
            this.fDoc2SystemId.put(schemaElement, schemaId);
        }
        this.fLastSchemaWasDuplicate = false;
        return schemaElement;
    }
    
    private Element getSchemaDocument1(final boolean mustResolve, final boolean hasInput, final XMLInputSource schemaSource, final Element referElement, final IOException ioe) {
        if (mustResolve) {
            if (hasInput) {
                this.reportSchemaError("schema_reference.4", new Object[] { schemaSource.getSystemId() }, referElement, ioe);
            }
            else {
                this.reportSchemaError("schema_reference.4", new Object[] { (schemaSource == null) ? "" : schemaSource.getSystemId() }, referElement, ioe);
            }
        }
        else if (hasInput) {
            this.reportSchemaWarning("schema_reference.4", new Object[] { schemaSource.getSystemId() }, referElement, ioe);
        }
        this.fLastSchemaWasDuplicate = false;
        return null;
    }
    
    private Element getSchemaDocument(final XSInputSource schemaSource, final XSDDescription desc) {
        final SchemaGrammar[] grammars = schemaSource.getGrammars();
        final short referType = desc.getContextType();
        if (grammars != null && grammars.length > 0) {
            final Vector expandedGrammars = this.expandGrammars(grammars);
            if (this.fNamespaceGrowth || !this.existingGrammars(expandedGrammars)) {
                this.addGrammars(expandedGrammars);
                if (referType == 3) {
                    desc.setTargetNamespace(grammars[0].getTargetNamespace());
                }
            }
        }
        else {
            final XSObject[] components = schemaSource.getComponents();
            if (components != null && components.length > 0) {
                final Map<String, Vector> importDependencies = new HashMap<String, Vector>();
                final Vector expandedComponents = this.expandComponents(components, importDependencies);
                if (this.fNamespaceGrowth || this.canAddComponents(expandedComponents)) {
                    this.addGlobalComponents(expandedComponents, importDependencies);
                    if (referType == 3) {
                        desc.setTargetNamespace(components[0].getNamespace());
                    }
                }
            }
        }
        return null;
    }
    
    private Vector expandGrammars(final SchemaGrammar[] grammars) {
        final Vector currGrammars = new Vector();
        for (int i = 0; i < grammars.length; ++i) {
            if (!currGrammars.contains(grammars[i])) {
                currGrammars.add(grammars[i]);
            }
        }
        for (int j = 0; j < currGrammars.size(); ++j) {
            final SchemaGrammar sg1 = currGrammars.elementAt(j);
            final Vector gs = sg1.getImportedGrammars();
            if (gs != null) {
                for (int k = gs.size() - 1; k >= 0; --k) {
                    final SchemaGrammar sg2 = gs.elementAt(k);
                    if (!currGrammars.contains(sg2)) {
                        currGrammars.addElement(sg2);
                    }
                }
            }
        }
        return currGrammars;
    }
    
    private boolean existingGrammars(final Vector grammars) {
        final int length = grammars.size();
        final XSDDescription desc = new XSDDescription();
        for (int i = 0; i < length; ++i) {
            final SchemaGrammar sg1 = grammars.elementAt(i);
            desc.setNamespace(sg1.getTargetNamespace());
            final SchemaGrammar sg2 = this.findGrammar(desc, false);
            if (sg2 != null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean canAddComponents(final Vector components) {
        final int size = components.size();
        final XSDDescription desc = new XSDDescription();
        for (int i = 0; i < size; ++i) {
            final XSObject component = components.elementAt(i);
            if (!this.canAddComponent(component, desc)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean canAddComponent(final XSObject component, final XSDDescription desc) {
        desc.setNamespace(component.getNamespace());
        final SchemaGrammar sg = this.findGrammar(desc, false);
        if (sg == null) {
            return true;
        }
        if (sg.isImmutable()) {
            return false;
        }
        final short componentType = component.getType();
        final String name = component.getName();
        switch (componentType) {
            case 3: {
                if (sg.getGlobalTypeDecl(name) == component) {
                    return true;
                }
                break;
            }
            case 1: {
                if (sg.getGlobalAttributeDecl(name) == component) {
                    return true;
                }
                break;
            }
            case 5: {
                if (sg.getGlobalAttributeDecl(name) == component) {
                    return true;
                }
                break;
            }
            case 2: {
                if (sg.getGlobalElementDecl(name) == component) {
                    return true;
                }
                break;
            }
            case 6: {
                if (sg.getGlobalGroupDecl(name) == component) {
                    return true;
                }
                break;
            }
            case 11: {
                if (sg.getGlobalNotationDecl(name) == component) {
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
    
    private void addGrammars(final Vector grammars) {
        final int length = grammars.size();
        final XSDDescription desc = new XSDDescription();
        for (int i = 0; i < length; ++i) {
            final SchemaGrammar sg1 = grammars.elementAt(i);
            desc.setNamespace(sg1.getTargetNamespace());
            final SchemaGrammar sg2 = this.findGrammar(desc, this.fNamespaceGrowth);
            if (sg1 != sg2) {
                this.addGrammarComponents(sg1, sg2);
            }
        }
    }
    
    private void addGrammarComponents(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        if (dstGrammar == null) {
            this.createGrammarFrom(srcGrammar);
            return;
        }
        SchemaGrammar tmpGrammar = dstGrammar;
        if (tmpGrammar.isImmutable()) {
            tmpGrammar = this.createGrammarFrom(dstGrammar);
        }
        this.addNewGrammarLocations(srcGrammar, tmpGrammar);
        this.addNewImportedGrammars(srcGrammar, tmpGrammar);
        this.addNewGrammarComponents(srcGrammar, tmpGrammar);
    }
    
    private SchemaGrammar createGrammarFrom(final SchemaGrammar grammar) {
        final SchemaGrammar newGrammar = new SchemaGrammar(grammar);
        this.fGrammarBucket.putGrammar(newGrammar);
        this.updateImportListWith(newGrammar);
        this.updateImportListFor(newGrammar);
        return newGrammar;
    }
    
    private void addNewGrammarLocations(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final StringList locations = srcGrammar.getDocumentLocations();
        final int locSize = locations.size();
        final StringList locations2 = dstGrammar.getDocumentLocations();
        for (int i = 0; i < locSize; ++i) {
            final String loc = locations.item(i);
            if (!locations2.contains(loc)) {
                dstGrammar.addDocument(null, loc);
            }
        }
    }
    
    private void addNewImportedGrammars(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final Vector igs1 = srcGrammar.getImportedGrammars();
        if (igs1 != null) {
            Vector igs2 = dstGrammar.getImportedGrammars();
            if (igs2 == null) {
                igs2 = (Vector)igs1.clone();
                dstGrammar.setImportedGrammars(igs2);
            }
            else {
                this.updateImportList(igs1, igs2);
            }
        }
    }
    
    private void updateImportList(final Vector importedSrc, final Vector importedDst) {
        for (int size = importedSrc.size(), i = 0; i < size; ++i) {
            final SchemaGrammar sg = importedSrc.elementAt(i);
            if (!this.containedImportedGrammar(importedDst, sg)) {
                importedDst.add(sg);
            }
        }
    }
    
    private void addNewGrammarComponents(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        dstGrammar.resetComponents();
        this.addGlobalElementDecls(srcGrammar, dstGrammar);
        this.addGlobalAttributeDecls(srcGrammar, dstGrammar);
        this.addGlobalAttributeGroupDecls(srcGrammar, dstGrammar);
        this.addGlobalGroupDecls(srcGrammar, dstGrammar);
        this.addGlobalTypeDecls(srcGrammar, dstGrammar);
        this.addGlobalNotationDecls(srcGrammar, dstGrammar);
    }
    
    private void addGlobalElementDecls(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final XSNamedMap components = srcGrammar.getComponents((short)2);
        for (int len = components.getLength(), i = 0; i < len; ++i) {
            final XSElementDecl srcDecl = (XSElementDecl)components.item(i);
            final XSElementDecl dstDecl = dstGrammar.getGlobalElementDecl(srcDecl.getName());
            if (dstDecl == null) {
                dstGrammar.addGlobalElementDecl(srcDecl);
            }
            else if (dstDecl != srcDecl) {}
        }
        final ObjectList componentsExt = srcGrammar.getComponentsExt((short)2);
        for (int len = componentsExt.getLength(), j = 0; j < len; j += 2) {
            final String key = (String)componentsExt.item(j);
            final int index = key.indexOf(44);
            final String location = key.substring(0, index);
            final String name = key.substring(index + 1, key.length());
            final XSElementDecl srcDecl = (XSElementDecl)componentsExt.item(j + 1);
            final XSElementDecl dstDecl = dstGrammar.getGlobalElementDecl(name, location);
            if (dstDecl == null) {
                dstGrammar.addGlobalElementDecl(srcDecl, location);
            }
            else if (dstDecl != srcDecl) {}
        }
    }
    
    private void addGlobalAttributeDecls(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final XSNamedMap components = srcGrammar.getComponents((short)1);
        for (int len = components.getLength(), i = 0; i < len; ++i) {
            final XSAttributeDecl srcDecl = (XSAttributeDecl)components.item(i);
            final XSAttributeDecl dstDecl = dstGrammar.getGlobalAttributeDecl(srcDecl.getName());
            if (dstDecl == null) {
                dstGrammar.addGlobalAttributeDecl(srcDecl);
            }
            else if (dstDecl != srcDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
            }
        }
        final ObjectList componentsExt = srcGrammar.getComponentsExt((short)1);
        for (int len = componentsExt.getLength(), j = 0; j < len; j += 2) {
            final String key = (String)componentsExt.item(j);
            final int index = key.indexOf(44);
            final String location = key.substring(0, index);
            final String name = key.substring(index + 1, key.length());
            final XSAttributeDecl srcDecl = (XSAttributeDecl)componentsExt.item(j + 1);
            final XSAttributeDecl dstDecl = dstGrammar.getGlobalAttributeDecl(name, location);
            if (dstDecl == null) {
                dstGrammar.addGlobalAttributeDecl(srcDecl, location);
            }
            else if (dstDecl != srcDecl) {}
        }
    }
    
    private void addGlobalAttributeGroupDecls(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final XSNamedMap components = srcGrammar.getComponents((short)5);
        for (int len = components.getLength(), i = 0; i < len; ++i) {
            final XSAttributeGroupDecl srcDecl = (XSAttributeGroupDecl)components.item(i);
            final XSAttributeGroupDecl dstDecl = dstGrammar.getGlobalAttributeGroupDecl(srcDecl.getName());
            if (dstDecl == null) {
                dstGrammar.addGlobalAttributeGroupDecl(srcDecl);
            }
            else if (dstDecl != srcDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
            }
        }
        final ObjectList componentsExt = srcGrammar.getComponentsExt((short)5);
        for (int len = componentsExt.getLength(), j = 0; j < len; j += 2) {
            final String key = (String)componentsExt.item(j);
            final int index = key.indexOf(44);
            final String location = key.substring(0, index);
            final String name = key.substring(index + 1, key.length());
            final XSAttributeGroupDecl srcDecl = (XSAttributeGroupDecl)componentsExt.item(j + 1);
            final XSAttributeGroupDecl dstDecl = dstGrammar.getGlobalAttributeGroupDecl(name, location);
            if (dstDecl == null) {
                dstGrammar.addGlobalAttributeGroupDecl(srcDecl, location);
            }
            else if (dstDecl != srcDecl) {}
        }
    }
    
    private void addGlobalNotationDecls(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final XSNamedMap components = srcGrammar.getComponents((short)11);
        for (int len = components.getLength(), i = 0; i < len; ++i) {
            final XSNotationDecl srcDecl = (XSNotationDecl)components.item(i);
            final XSNotationDecl dstDecl = dstGrammar.getGlobalNotationDecl(srcDecl.getName());
            if (dstDecl == null) {
                dstGrammar.addGlobalNotationDecl(srcDecl);
            }
            else if (dstDecl != srcDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
            }
        }
        final ObjectList componentsExt = srcGrammar.getComponentsExt((short)11);
        for (int len = componentsExt.getLength(), j = 0; j < len; j += 2) {
            final String key = (String)componentsExt.item(j);
            final int index = key.indexOf(44);
            final String location = key.substring(0, index);
            final String name = key.substring(index + 1, key.length());
            final XSNotationDecl srcDecl = (XSNotationDecl)componentsExt.item(j + 1);
            final XSNotationDecl dstDecl = dstGrammar.getGlobalNotationDecl(name, location);
            if (dstDecl == null) {
                dstGrammar.addGlobalNotationDecl(srcDecl, location);
            }
            else if (dstDecl != srcDecl) {}
        }
    }
    
    private void addGlobalGroupDecls(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final XSNamedMap components = srcGrammar.getComponents((short)6);
        for (int len = components.getLength(), i = 0; i < len; ++i) {
            final XSGroupDecl srcDecl = (XSGroupDecl)components.item(i);
            final XSGroupDecl dstDecl = dstGrammar.getGlobalGroupDecl(srcDecl.getName());
            if (dstDecl == null) {
                dstGrammar.addGlobalGroupDecl(srcDecl);
            }
            else if (srcDecl != dstDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
            }
        }
        final ObjectList componentsExt = srcGrammar.getComponentsExt((short)6);
        for (int len = componentsExt.getLength(), j = 0; j < len; j += 2) {
            final String key = (String)componentsExt.item(j);
            final int index = key.indexOf(44);
            final String location = key.substring(0, index);
            final String name = key.substring(index + 1, key.length());
            final XSGroupDecl srcDecl = (XSGroupDecl)componentsExt.item(j + 1);
            final XSGroupDecl dstDecl = dstGrammar.getGlobalGroupDecl(name, location);
            if (dstDecl == null) {
                dstGrammar.addGlobalGroupDecl(srcDecl, location);
            }
            else if (dstDecl != srcDecl) {}
        }
    }
    
    private void addGlobalTypeDecls(final SchemaGrammar srcGrammar, final SchemaGrammar dstGrammar) {
        final XSNamedMap components = srcGrammar.getComponents((short)3);
        for (int len = components.getLength(), i = 0; i < len; ++i) {
            final XSTypeDefinition srcDecl = (XSTypeDefinition)components.item(i);
            final XSTypeDefinition dstDecl = dstGrammar.getGlobalTypeDecl(srcDecl.getName());
            if (dstDecl == null) {
                dstGrammar.addGlobalTypeDecl(srcDecl);
            }
            else if (dstDecl != srcDecl && !this.fTolerateDuplicates) {
                this.reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
            }
        }
        final ObjectList componentsExt = srcGrammar.getComponentsExt((short)3);
        for (int len = componentsExt.getLength(), j = 0; j < len; j += 2) {
            final String key = (String)componentsExt.item(j);
            final int index = key.indexOf(44);
            final String location = key.substring(0, index);
            final String name = key.substring(index + 1, key.length());
            final XSTypeDefinition srcDecl = (XSTypeDefinition)componentsExt.item(j + 1);
            final XSTypeDefinition dstDecl = dstGrammar.getGlobalTypeDecl(name, location);
            if (dstDecl == null) {
                dstGrammar.addGlobalTypeDecl(srcDecl, location);
            }
            else if (dstDecl != srcDecl) {}
        }
    }
    
    private Vector expandComponents(final XSObject[] components, final Map<String, Vector> dependencies) {
        final Vector newComponents = new Vector();
        for (int i = 0; i < components.length; ++i) {
            if (!newComponents.contains(components[i])) {
                newComponents.add(components[i]);
            }
        }
        for (int i = 0; i < newComponents.size(); ++i) {
            final XSObject component = newComponents.elementAt(i);
            this.expandRelatedComponents(component, newComponents, dependencies);
        }
        return newComponents;
    }
    
    private void expandRelatedComponents(final XSObject component, final Vector componentList, final Map<String, Vector> dependencies) {
        final short componentType = component.getType();
        switch (componentType) {
            case 3: {
                this.expandRelatedTypeComponents((XSTypeDefinition)component, componentList, component.getNamespace(), dependencies);
                break;
            }
            case 1: {
                this.expandRelatedAttributeComponents((XSAttributeDeclaration)component, componentList, component.getNamespace(), dependencies);
                break;
            }
            case 5: {
                this.expandRelatedAttributeGroupComponents((XSAttributeGroupDefinition)component, componentList, component.getNamespace(), dependencies);
            }
            case 2: {
                this.expandRelatedElementComponents((XSElementDeclaration)component, componentList, component.getNamespace(), dependencies);
                break;
            }
            case 6: {
                this.expandRelatedModelGroupDefinitionComponents((XSModelGroupDefinition)component, componentList, component.getNamespace(), dependencies);
                break;
            }
        }
    }
    
    private void expandRelatedAttributeComponents(final XSAttributeDeclaration decl, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        this.addRelatedType(decl.getTypeDefinition(), componentList, namespace, dependencies);
    }
    
    private void expandRelatedElementComponents(final XSElementDeclaration decl, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        this.addRelatedType(decl.getTypeDefinition(), componentList, namespace, dependencies);
        final XSElementDeclaration subElemDecl = decl.getSubstitutionGroupAffiliation();
        if (subElemDecl != null) {
            this.addRelatedElement(subElemDecl, componentList, namespace, dependencies);
        }
    }
    
    private void expandRelatedTypeComponents(final XSTypeDefinition type, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        if (type instanceof XSComplexTypeDecl) {
            this.expandRelatedComplexTypeComponents((XSComplexTypeDecl)type, componentList, namespace, dependencies);
        }
        else if (type instanceof XSSimpleTypeDecl) {
            this.expandRelatedSimpleTypeComponents((XSSimpleTypeDefinition)type, componentList, namespace, dependencies);
        }
    }
    
    private void expandRelatedModelGroupDefinitionComponents(final XSModelGroupDefinition modelGroupDef, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        this.expandRelatedModelGroupComponents(modelGroupDef.getModelGroup(), componentList, namespace, dependencies);
    }
    
    private void expandRelatedAttributeGroupComponents(final XSAttributeGroupDefinition attrGroup, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        this.expandRelatedAttributeUsesComponents(attrGroup.getAttributeUses(), componentList, namespace, dependencies);
    }
    
    private void expandRelatedComplexTypeComponents(final XSComplexTypeDecl type, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        this.addRelatedType(type.getBaseType(), componentList, namespace, dependencies);
        this.expandRelatedAttributeUsesComponents(type.getAttributeUses(), componentList, namespace, dependencies);
        final XSParticle particle = type.getParticle();
        if (particle != null) {
            this.expandRelatedParticleComponents(particle, componentList, namespace, dependencies);
        }
    }
    
    private void expandRelatedSimpleTypeComponents(final XSSimpleTypeDefinition type, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        final XSTypeDefinition baseType = type.getBaseType();
        if (baseType != null) {
            this.addRelatedType(baseType, componentList, namespace, dependencies);
        }
        final XSTypeDefinition itemType = type.getItemType();
        if (itemType != null) {
            this.addRelatedType(itemType, componentList, namespace, dependencies);
        }
        final XSTypeDefinition primitiveType = type.getPrimitiveType();
        if (primitiveType != null) {
            this.addRelatedType(primitiveType, componentList, namespace, dependencies);
        }
        final XSObjectList memberTypes = type.getMemberTypes();
        if (memberTypes.size() > 0) {
            for (int i = 0; i < memberTypes.size(); ++i) {
                this.addRelatedType((XSTypeDefinition)memberTypes.item(i), componentList, namespace, dependencies);
            }
        }
    }
    
    private void expandRelatedAttributeUsesComponents(final XSObjectList attrUses, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        for (int attrUseSize = (attrUses == null) ? 0 : attrUses.size(), i = 0; i < attrUseSize; ++i) {
            this.expandRelatedAttributeUseComponents((XSAttributeUse)attrUses.item(i), componentList, namespace, dependencies);
        }
    }
    
    private void expandRelatedAttributeUseComponents(final XSAttributeUse component, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        this.addRelatedAttribute(component.getAttrDeclaration(), componentList, namespace, dependencies);
    }
    
    private void expandRelatedParticleComponents(final XSParticle component, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        final XSTerm term = component.getTerm();
        switch (term.getType()) {
            case 2: {
                this.addRelatedElement((XSElementDeclaration)term, componentList, namespace, dependencies);
                break;
            }
            case 7: {
                this.expandRelatedModelGroupComponents((XSModelGroup)term, componentList, namespace, dependencies);
                break;
            }
        }
    }
    
    private void expandRelatedModelGroupComponents(final XSModelGroup modelGroup, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        final XSObjectList particles = modelGroup.getParticles();
        for (int length = (particles == null) ? 0 : particles.getLength(), i = 0; i < length; ++i) {
            this.expandRelatedParticleComponents((XSParticle)particles.item(i), componentList, namespace, dependencies);
        }
    }
    
    private void addRelatedType(final XSTypeDefinition type, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        if (!type.getAnonymous()) {
            if (!type.getNamespace().equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && !componentList.contains(type)) {
                final Vector importedNamespaces = this.findDependentNamespaces(namespace, dependencies);
                this.addNamespaceDependency(namespace, type.getNamespace(), importedNamespaces);
                componentList.add(type);
            }
        }
        else {
            this.expandRelatedTypeComponents(type, componentList, namespace, dependencies);
        }
    }
    
    private void addRelatedElement(final XSElementDeclaration decl, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        if (decl.getScope() == 1) {
            if (!componentList.contains(decl)) {
                final Vector importedNamespaces = this.findDependentNamespaces(namespace, dependencies);
                this.addNamespaceDependency(namespace, decl.getNamespace(), importedNamespaces);
                componentList.add(decl);
            }
        }
        else {
            this.expandRelatedElementComponents(decl, componentList, namespace, dependencies);
        }
    }
    
    private void addRelatedAttribute(final XSAttributeDeclaration decl, final Vector componentList, final String namespace, final Map<String, Vector> dependencies) {
        if (decl.getScope() == 1) {
            if (!componentList.contains(decl)) {
                final Vector importedNamespaces = this.findDependentNamespaces(namespace, dependencies);
                this.addNamespaceDependency(namespace, decl.getNamespace(), importedNamespaces);
                componentList.add(decl);
            }
        }
        else {
            this.expandRelatedAttributeComponents(decl, componentList, namespace, dependencies);
        }
    }
    
    private void addGlobalComponents(final Vector components, final Map<String, Vector> importDependencies) {
        final XSDDescription desc = new XSDDescription();
        for (int size = components.size(), i = 0; i < size; ++i) {
            this.addGlobalComponent(components.elementAt(i), desc);
        }
        this.updateImportDependencies(importDependencies);
    }
    
    private void addGlobalComponent(final XSObject component, final XSDDescription desc) {
        final String namespace = component.getNamespace();
        desc.setNamespace(namespace);
        final SchemaGrammar sg = this.getSchemaGrammar(desc);
        final short componentType = component.getType();
        final String name = component.getName();
        switch (componentType) {
            case 3: {
                if (((XSTypeDefinition)component).getAnonymous()) {
                    break;
                }
                if (sg.getGlobalTypeDecl(name) == null) {
                    sg.addGlobalTypeDecl((XSTypeDefinition)component);
                }
                if (sg.getGlobalTypeDecl(name, "") == null) {
                    sg.addGlobalTypeDecl((XSTypeDefinition)component, "");
                    break;
                }
                break;
            }
            case 1: {
                if (((XSAttributeDecl)component).getScope() != 1) {
                    break;
                }
                if (sg.getGlobalAttributeDecl(name) == null) {
                    sg.addGlobalAttributeDecl((XSAttributeDecl)component);
                }
                if (sg.getGlobalAttributeDecl(name, "") == null) {
                    sg.addGlobalAttributeDecl((XSAttributeDecl)component, "");
                    break;
                }
                break;
            }
            case 5: {
                if (sg.getGlobalAttributeDecl(name) == null) {
                    sg.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)component);
                }
                if (sg.getGlobalAttributeDecl(name, "") == null) {
                    sg.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)component, "");
                    break;
                }
                break;
            }
            case 2: {
                if (((XSElementDecl)component).getScope() != 1) {
                    break;
                }
                sg.addGlobalElementDeclAll((XSElementDecl)component);
                if (sg.getGlobalElementDecl(name) == null) {
                    sg.addGlobalElementDecl((XSElementDecl)component);
                }
                if (sg.getGlobalElementDecl(name, "") == null) {
                    sg.addGlobalElementDecl((XSElementDecl)component, "");
                    break;
                }
                break;
            }
            case 6: {
                if (sg.getGlobalGroupDecl(name) == null) {
                    sg.addGlobalGroupDecl((XSGroupDecl)component);
                }
                if (sg.getGlobalGroupDecl(name, "") == null) {
                    sg.addGlobalGroupDecl((XSGroupDecl)component, "");
                    break;
                }
                break;
            }
            case 11: {
                if (sg.getGlobalNotationDecl(name) == null) {
                    sg.addGlobalNotationDecl((XSNotationDecl)component);
                }
                if (sg.getGlobalNotationDecl(name, "") == null) {
                    sg.addGlobalNotationDecl((XSNotationDecl)component, "");
                    break;
                }
                break;
            }
        }
    }
    
    private void updateImportDependencies(final Map<String, Vector> table) {
        if (table == null) {
            return;
        }
        for (final Map.Entry<String, Vector> entry : table.entrySet()) {
            final String namespace = entry.getKey();
            final Vector importList = entry.getValue();
            if (importList.size() > 0) {
                this.expandImportList(namespace, importList);
            }
        }
    }
    
    private void expandImportList(final String namespace, final Vector namespaceList) {
        final SchemaGrammar sg = this.fGrammarBucket.getGrammar(namespace);
        if (sg != null) {
            Vector isgs = sg.getImportedGrammars();
            if (isgs == null) {
                isgs = new Vector();
                this.addImportList(sg, isgs, namespaceList);
                sg.setImportedGrammars(isgs);
            }
            else {
                this.updateImportList(sg, isgs, namespaceList);
            }
        }
    }
    
    private void addImportList(final SchemaGrammar sg, final Vector importedGrammars, final Vector namespaceList) {
        for (int size = namespaceList.size(), i = 0; i < size; ++i) {
            final SchemaGrammar isg = this.fGrammarBucket.getGrammar(namespaceList.elementAt(i));
            if (isg != null) {
                importedGrammars.add(isg);
            }
        }
    }
    
    private void updateImportList(final SchemaGrammar sg, final Vector importedGrammars, final Vector namespaceList) {
        for (int size = namespaceList.size(), i = 0; i < size; ++i) {
            final SchemaGrammar isg = this.fGrammarBucket.getGrammar(namespaceList.elementAt(i));
            if (isg != null && !this.containedImportedGrammar(importedGrammars, isg)) {
                importedGrammars.add(isg);
            }
        }
    }
    
    private boolean containedImportedGrammar(final Vector importedGrammar, final SchemaGrammar grammar) {
        for (int size = importedGrammar.size(), i = 0; i < size; ++i) {
            final SchemaGrammar sg = importedGrammar.elementAt(i);
            if (this.null2EmptyString(sg.getTargetNamespace()).equals(this.null2EmptyString(grammar.getTargetNamespace()))) {
                return true;
            }
        }
        return false;
    }
    
    private SchemaGrammar getSchemaGrammar(final XSDDescription desc) {
        SchemaGrammar sg = this.findGrammar(desc, this.fNamespaceGrowth);
        if (sg == null) {
            sg = new SchemaGrammar(desc.getNamespace(), desc.makeClone(), this.fSymbolTable);
            this.fGrammarBucket.putGrammar(sg);
        }
        else if (sg.isImmutable()) {
            sg = this.createGrammarFrom(sg);
        }
        return sg;
    }
    
    private Vector findDependentNamespaces(final String namespace, final Map table) {
        final String ns = this.null2EmptyString(namespace);
        Vector namespaceList = (Vector)this.getFromMap(table, ns);
        if (namespaceList == null) {
            namespaceList = new Vector();
            table.put(ns, namespaceList);
        }
        return namespaceList;
    }
    
    private void addNamespaceDependency(final String namespace1, final String namespace2, final Vector list) {
        final String ns1 = this.null2EmptyString(namespace1);
        final String ns2 = this.null2EmptyString(namespace2);
        if (!ns1.equals(ns2) && !list.contains(ns2)) {
            list.add(ns2);
        }
    }
    
    private void reportSharingError(final String namespace, final String name) {
        final String qName = (namespace == null) ? ("," + name) : (namespace + "," + name);
        this.reportSchemaError("sch-props-correct.2", new Object[] { qName }, null);
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
    
    void prepareForParse() {
        this.fTraversed.clear();
        this.fDoc2SystemId.clear();
        this.fHiddenNodes.clear();
        this.fLastSchemaWasDuplicate = false;
    }
    
    void prepareForTraverse() {
        if (!this.registryEmpty) {
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
        }
        for (int i = 1; i <= 7; ++i) {
            if (this.fUnparsedRegistriesExt[i] != null) {
                this.fUnparsedRegistriesExt[i].clear();
            }
        }
        this.fDependencyMap.clear();
        this.fDoc2XSDocumentMap.clear();
        if (this.fRedefine2XSDMap != null) {
            this.fRedefine2XSDMap.clear();
        }
        if (this.fRedefine2NSSupport != null) {
            this.fRedefine2NSSupport.clear();
        }
        this.fAllTNSs.removeAllElements();
        this.fImportMap.clear();
        this.fRoot = null;
        for (int i = 0; i < this.fLocalElemStackPos; ++i) {
            this.fParticle[i] = null;
            this.fLocalElementDecl[i] = null;
            this.fLocalElementDecl_schema[i] = null;
            this.fLocalElemNamespaceContext[i] = null;
        }
        this.fLocalElemStackPos = 0;
        for (int i = 0; i < this.fKeyrefStackPos; ++i) {
            this.fKeyrefs[i] = null;
            this.fKeyrefElems[i] = null;
            this.fKeyrefNamespaceContext[i] = null;
            this.fKeyrefsMapXSDocumentInfo[i] = null;
        }
        this.fKeyrefStackPos = 0;
        if (this.fAttributeChecker == null) {
            this.createTraversers();
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
        this.fRedefinedRestrictedAttributeGroupRegistry.clear();
        this.fRedefinedRestrictedGroupRegistry.clear();
        this.fGlobalAttrDecls.clear();
        this.fGlobalAttrGrpDecls.clear();
        this.fGlobalElemDecls.clear();
        this.fGlobalGroupDecls.clear();
        this.fGlobalNotationDecls.clear();
        this.fGlobalIDConstraintDecls.clear();
        this.fGlobalTypeDecls.clear();
    }
    
    public void setDeclPool(final XSDeclarationPool declPool) {
        this.fDeclPool = declPool;
    }
    
    public void setDVFactory(final SchemaDVFactory dvFactory) {
        this.fDVFactory = dvFactory;
    }
    
    public SchemaDVFactory getDVFactory() {
        return this.fDVFactory;
    }
    
    public void reset(final XMLComponentManager componentManager) {
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
        this.fEntityManager = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        final XMLEntityResolver er = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
        if (er != null) {
            this.fSchemaParser.setEntityResolver(er);
        }
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fErrorHandler = this.fErrorReporter.getErrorHandler();
        this.fLocale = this.fErrorReporter.getLocale();
        this.fValidateAnnotations = componentManager.getFeature("http://apache.org/xml/features/validate-annotations", false);
        this.fHonourAllSchemaLocations = componentManager.getFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
        this.fNamespaceGrowth = componentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
        this.fTolerateDuplicates = componentManager.getFeature("http://apache.org/xml/features/internal/tolerate-duplicates", false);
        try {
            if (this.fErrorHandler != this.fSchemaParser.getProperty("http://apache.org/xml/properties/internal/error-handler")) {
                this.fSchemaParser.setProperty("http://apache.org/xml/properties/internal/error-handler", (this.fErrorHandler != null) ? this.fErrorHandler : new DefaultErrorHandler());
                if (this.fAnnotationValidator != null) {
                    this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", (this.fErrorHandler != null) ? this.fErrorHandler : new DefaultErrorHandler());
                }
            }
            if (this.fLocale != this.fSchemaParser.getProperty("http://apache.org/xml/properties/locale")) {
                this.fSchemaParser.setProperty("http://apache.org/xml/properties/locale", this.fLocale);
                if (this.fAnnotationValidator != null) {
                    this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", this.fLocale);
                }
            }
        }
        catch (final XMLConfigurationException ex) {}
        try {
            this.fSchemaParser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", this.fErrorReporter.getFeature("http://apache.org/xml/features/continue-after-fatal-error"));
        }
        catch (final XMLConfigurationException ex2) {}
        try {
            if (componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false)) {
                this.fSchemaParser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            }
        }
        catch (final XMLConfigurationException ex3) {}
        try {
            if (componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false)) {
                this.fSchemaParser.setFeature("http://apache.org/xml/features/standard-uri-conformant", true);
            }
        }
        catch (final XMLConfigurationException ex4) {}
        try {
            this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException e) {
            this.fGrammarPool = null;
        }
        try {
            if (componentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false)) {
                this.fSchemaParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            }
        }
        catch (final XMLConfigurationException ex5) {}
        try {
            if (this.fSecurityManager != null) {
                this.fSchemaParser.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            }
        }
        catch (final XMLConfigurationException ex6) {}
        this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
        this.fSchemaParser.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        this.fAccessExternalDTD = this.fSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this.fAccessExternalSchema = this.fSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
        this.fOverrideDefaultParser = componentManager.getFeature("jdk.xml.overrideDefaultParser");
        this.fSchemaParser.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
    }
    
    void traverseLocalElements() {
        this.fElementTraverser.fDeferTraversingLocalElements = false;
        for (int i = 0; i < this.fLocalElemStackPos; ++i) {
            final Element currElem = this.fLocalElementDecl[i];
            final XSDocumentInfo currSchema = this.fLocalElementDecl_schema[i];
            final SchemaGrammar currGrammar = this.fGrammarBucket.getGrammar(currSchema.fTargetNamespace);
            this.fElementTraverser.traverseLocal(this.fParticle[i], currElem, currSchema, currGrammar, this.fAllContext[i], this.fParent[i], this.fLocalElemNamespaceContext[i]);
            if (this.fParticle[i].fType == 0) {
                XSModelGroupImpl group = null;
                if (this.fParent[i] instanceof XSComplexTypeDecl) {
                    final XSParticle p = ((XSComplexTypeDecl)this.fParent[i]).getParticle();
                    if (p != null) {
                        group = (XSModelGroupImpl)p.getTerm();
                    }
                }
                else {
                    group = ((XSGroupDecl)this.fParent[i]).fModelGroup;
                }
                if (group != null) {
                    this.removeParticle(group, this.fParticle[i]);
                }
            }
        }
    }
    
    private boolean removeParticle(final XSModelGroupImpl group, final XSParticleDecl particle) {
        for (int i = 0; i < group.fParticleCount; ++i) {
            final XSParticleDecl member = group.fParticles[i];
            if (member == particle) {
                for (int j = i; j < group.fParticleCount - 1; ++j) {
                    group.fParticles[j] = group.fParticles[j + 1];
                }
                --group.fParticleCount;
                return true;
            }
            if (member.fType == 3 && this.removeParticle((XSModelGroupImpl)member.fValue, particle)) {
                return true;
            }
        }
        return false;
    }
    
    void fillInLocalElemInfo(final Element elmDecl, final XSDocumentInfo schemaDoc, final int allContextFlags, final XSObject parent, final XSParticleDecl particle) {
        if (this.fParticle.length == this.fLocalElemStackPos) {
            final XSParticleDecl[] newStackP = new XSParticleDecl[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fParticle, 0, newStackP, 0, this.fLocalElemStackPos);
            this.fParticle = newStackP;
            final Element[] newStackE = new Element[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fLocalElementDecl, 0, newStackE, 0, this.fLocalElemStackPos);
            this.fLocalElementDecl = newStackE;
            final XSDocumentInfo[] newStackE_schema = new XSDocumentInfo[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fLocalElementDecl_schema, 0, newStackE_schema, 0, this.fLocalElemStackPos);
            this.fLocalElementDecl_schema = newStackE_schema;
            final int[] newStackI = new int[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fAllContext, 0, newStackI, 0, this.fLocalElemStackPos);
            this.fAllContext = newStackI;
            final XSObject[] newStackC = new XSObject[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fParent, 0, newStackC, 0, this.fLocalElemStackPos);
            this.fParent = newStackC;
            final String[][] newStackN = new String[this.fLocalElemStackPos + 10][];
            System.arraycopy(this.fLocalElemNamespaceContext, 0, newStackN, 0, this.fLocalElemStackPos);
            this.fLocalElemNamespaceContext = newStackN;
        }
        this.fParticle[this.fLocalElemStackPos] = particle;
        this.fLocalElementDecl[this.fLocalElemStackPos] = elmDecl;
        this.fLocalElementDecl_schema[this.fLocalElemStackPos] = schemaDoc;
        this.fAllContext[this.fLocalElemStackPos] = allContextFlags;
        this.fParent[this.fLocalElemStackPos] = parent;
        this.fLocalElemNamespaceContext[this.fLocalElemStackPos++] = schemaDoc.fNamespaceSupport.getEffectiveLocalContext();
    }
    
    void checkForDuplicateNames(final String qName, final int declType, final Map<String, Element> registry, final Map<String, XSDocumentInfo> registry_sub, final Element currComp, final XSDocumentInfo currSchema) {
        Object objElem = null;
        if ((objElem = registry.get(qName)) == null) {
            if (this.fNamespaceGrowth && !this.fTolerateDuplicates) {
                this.checkForDuplicateNames(qName, declType, currComp);
            }
            registry.put(qName, currComp);
            registry_sub.put(qName, currSchema);
        }
        else {
            final Element collidingElem = (Element)objElem;
            final XSDocumentInfo collidingElemSchema = registry_sub.get(qName);
            if (collidingElem == currComp) {
                return;
            }
            Element elemParent = null;
            XSDocumentInfo redefinedSchema = null;
            boolean collidedWithRedefine = true;
            if (DOMUtil.getLocalName(elemParent = DOMUtil.getParent(collidingElem)).equals(SchemaSymbols.ELT_REDEFINE)) {
                redefinedSchema = ((this.fRedefine2XSDMap != null) ? this.fRedefine2XSDMap.get(elemParent) : null);
            }
            else if (DOMUtil.getLocalName(DOMUtil.getParent(currComp)).equals(SchemaSymbols.ELT_REDEFINE)) {
                redefinedSchema = collidingElemSchema;
                collidedWithRedefine = false;
            }
            if (redefinedSchema != null) {
                if (collidingElemSchema == currSchema) {
                    this.reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
                    return;
                }
                final String newName = qName.substring(qName.lastIndexOf(44) + 1) + "_fn3dktizrknc9pi";
                if (redefinedSchema == currSchema) {
                    currComp.setAttribute(SchemaSymbols.ATT_NAME, newName);
                    if (currSchema.fTargetNamespace == null) {
                        registry.put("," + newName, currComp);
                        registry_sub.put("," + newName, currSchema);
                    }
                    else {
                        registry.put(currSchema.fTargetNamespace + "," + newName, currComp);
                        registry_sub.put(currSchema.fTargetNamespace + "," + newName, currSchema);
                    }
                    if (currSchema.fTargetNamespace == null) {
                        this.checkForDuplicateNames("," + newName, declType, registry, registry_sub, currComp, currSchema);
                    }
                    else {
                        this.checkForDuplicateNames(currSchema.fTargetNamespace + "," + newName, declType, registry, registry_sub, currComp, currSchema);
                    }
                }
                else if (collidedWithRedefine) {
                    if (currSchema.fTargetNamespace == null) {
                        this.checkForDuplicateNames("," + newName, declType, registry, registry_sub, currComp, currSchema);
                    }
                    else {
                        this.checkForDuplicateNames(currSchema.fTargetNamespace + "," + newName, declType, registry, registry_sub, currComp, currSchema);
                    }
                }
                else {
                    this.reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
                }
            }
            else if (!this.fTolerateDuplicates) {
                this.reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
            }
            else if (this.fUnparsedRegistriesExt[declType] != null && this.fUnparsedRegistriesExt[declType].get(qName) == currSchema) {
                this.reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
            }
        }
        if (this.fTolerateDuplicates) {
            if (this.fUnparsedRegistriesExt[declType] == null) {
                this.fUnparsedRegistriesExt[declType] = new HashMap<String, XSDocumentInfo>();
            }
            this.fUnparsedRegistriesExt[declType].put(qName, currSchema);
        }
    }
    
    void checkForDuplicateNames(final String qName, final int declType, final Element currComp) {
        final int namespaceEnd = qName.indexOf(44);
        final String namespace = qName.substring(0, namespaceEnd);
        final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(this.emptyString2Null(namespace));
        if (grammar != null) {
            final Object obj = this.getGlobalDeclFromGrammar(grammar, declType, qName.substring(namespaceEnd + 1));
            if (obj != null) {
                this.reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
            }
        }
    }
    
    private void renameRedefiningComponents(final XSDocumentInfo currSchema, final Element child, final String componentType, final String oldName, final String newName) {
        if (componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            Element grandKid = DOMUtil.getFirstChildElement(child);
            if (grandKid == null) {
                this.reportSchemaError("src-redefine.5.a.a", null, child);
            }
            else {
                String grandKidName = DOMUtil.getLocalName(grandKid);
                if (grandKidName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                    grandKid = DOMUtil.getNextSiblingElement(grandKid);
                }
                if (grandKid == null) {
                    this.reportSchemaError("src-redefine.5.a.a", null, child);
                }
                else {
                    grandKidName = DOMUtil.getLocalName(grandKid);
                    if (!grandKidName.equals(SchemaSymbols.ELT_RESTRICTION)) {
                        this.reportSchemaError("src-redefine.5.a.b", new Object[] { grandKidName }, child);
                    }
                    else {
                        final Object[] attrs = this.fAttributeChecker.checkAttributes(grandKid, false, currSchema);
                        final QName derivedBase = (QName)attrs[XSAttributeChecker.ATTIDX_BASE];
                        if (derivedBase == null || derivedBase.uri != currSchema.fTargetNamespace || !derivedBase.localpart.equals(oldName)) {
                            this.reportSchemaError("src-redefine.5.a.c", new Object[] { grandKidName, ((currSchema.fTargetNamespace == null) ? "" : currSchema.fTargetNamespace) + "," + oldName }, child);
                        }
                        else if (derivedBase.prefix != null && derivedBase.prefix.length() > 0) {
                            grandKid.setAttribute(SchemaSymbols.ATT_BASE, derivedBase.prefix + ":" + newName);
                        }
                        else {
                            grandKid.setAttribute(SchemaSymbols.ATT_BASE, newName);
                        }
                        this.fAttributeChecker.returnAttrArray(attrs, currSchema);
                    }
                }
            }
        }
        else if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
            Element grandKid = DOMUtil.getFirstChildElement(child);
            if (grandKid == null) {
                this.reportSchemaError("src-redefine.5.b.a", null, child);
            }
            else {
                if (DOMUtil.getLocalName(grandKid).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    grandKid = DOMUtil.getNextSiblingElement(grandKid);
                }
                if (grandKid == null) {
                    this.reportSchemaError("src-redefine.5.b.a", null, child);
                }
                else {
                    Element greatGrandKid = DOMUtil.getFirstChildElement(grandKid);
                    if (greatGrandKid == null) {
                        this.reportSchemaError("src-redefine.5.b.b", null, grandKid);
                    }
                    else {
                        String greatGrandKidName = DOMUtil.getLocalName(greatGrandKid);
                        if (greatGrandKidName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                            greatGrandKid = DOMUtil.getNextSiblingElement(greatGrandKid);
                        }
                        if (greatGrandKid == null) {
                            this.reportSchemaError("src-redefine.5.b.b", null, grandKid);
                        }
                        else {
                            greatGrandKidName = DOMUtil.getLocalName(greatGrandKid);
                            if (!greatGrandKidName.equals(SchemaSymbols.ELT_RESTRICTION) && !greatGrandKidName.equals(SchemaSymbols.ELT_EXTENSION)) {
                                this.reportSchemaError("src-redefine.5.b.c", new Object[] { greatGrandKidName }, greatGrandKid);
                            }
                            else {
                                final Object[] attrs2 = this.fAttributeChecker.checkAttributes(greatGrandKid, false, currSchema);
                                final QName derivedBase2 = (QName)attrs2[XSAttributeChecker.ATTIDX_BASE];
                                if (derivedBase2 == null || derivedBase2.uri != currSchema.fTargetNamespace || !derivedBase2.localpart.equals(oldName)) {
                                    this.reportSchemaError("src-redefine.5.b.d", new Object[] { greatGrandKidName, ((currSchema.fTargetNamespace == null) ? "" : currSchema.fTargetNamespace) + "," + oldName }, greatGrandKid);
                                }
                                else if (derivedBase2.prefix != null && derivedBase2.prefix.length() > 0) {
                                    greatGrandKid.setAttribute(SchemaSymbols.ATT_BASE, derivedBase2.prefix + ":" + newName);
                                }
                                else {
                                    greatGrandKid.setAttribute(SchemaSymbols.ATT_BASE, newName);
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
            final String processedBaseName = (currSchema.fTargetNamespace == null) ? ("," + oldName) : (currSchema.fTargetNamespace + "," + oldName);
            final int attGroupRefsCount = this.changeRedefineGroup(processedBaseName, componentType, newName, child, currSchema);
            if (attGroupRefsCount > 1) {
                this.reportSchemaError("src-redefine.7.1", new Object[] { new Integer(attGroupRefsCount) }, child);
            }
            else if (attGroupRefsCount != 1) {
                if (currSchema.fTargetNamespace == null) {
                    this.fRedefinedRestrictedAttributeGroupRegistry.put(processedBaseName, "," + newName);
                }
                else {
                    this.fRedefinedRestrictedAttributeGroupRegistry.put(processedBaseName, currSchema.fTargetNamespace + "," + newName);
                }
            }
        }
        else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
            final String processedBaseName = (currSchema.fTargetNamespace == null) ? ("," + oldName) : (currSchema.fTargetNamespace + "," + oldName);
            final int groupRefsCount = this.changeRedefineGroup(processedBaseName, componentType, newName, child, currSchema);
            if (groupRefsCount > 1) {
                this.reportSchemaError("src-redefine.6.1.1", new Object[] { new Integer(groupRefsCount) }, child);
            }
            else if (groupRefsCount != 1) {
                if (currSchema.fTargetNamespace == null) {
                    this.fRedefinedRestrictedGroupRegistry.put(processedBaseName, "," + newName);
                }
                else {
                    this.fRedefinedRestrictedGroupRegistry.put(processedBaseName, currSchema.fTargetNamespace + "," + newName);
                }
            }
        }
        else {
            this.reportSchemaError("Internal-Error", new Object[] { "could not handle this particular <redefine>; please submit your schemas and instance document in a bug report!" }, child);
        }
    }
    
    private String findQName(final String name, final XSDocumentInfo schemaDoc) {
        final SchemaNamespaceSupport currNSMap = schemaDoc.fNamespaceSupport;
        final int colonPtr = name.indexOf(58);
        String prefix = XMLSymbols.EMPTY_STRING;
        if (colonPtr > 0) {
            prefix = name.substring(0, colonPtr);
        }
        String uri = currNSMap.getURI(this.fSymbolTable.addSymbol(prefix));
        final String localpart = (colonPtr == 0) ? name : name.substring(colonPtr + 1);
        if (prefix == XMLSymbols.EMPTY_STRING && uri == null && schemaDoc.fIsChameleonSchema) {
            uri = schemaDoc.fTargetNamespace;
        }
        if (uri == null) {
            return "," + localpart;
        }
        return uri + "," + localpart;
    }
    
    private int changeRedefineGroup(final String originalQName, final String elementSought, final String newName, final Element curr, final XSDocumentInfo schemaDoc) {
        int result = 0;
        for (Element child = DOMUtil.getFirstChildElement(curr); child != null; child = DOMUtil.getNextSiblingElement(child)) {
            final String name = DOMUtil.getLocalName(child);
            if (!name.equals(elementSought)) {
                result += this.changeRedefineGroup(originalQName, elementSought, newName, child, schemaDoc);
            }
            else {
                final String ref = child.getAttribute(SchemaSymbols.ATT_REF);
                if (ref.length() != 0) {
                    final String processedRef = this.findQName(ref, schemaDoc);
                    if (originalQName.equals(processedRef)) {
                        String prefix = XMLSymbols.EMPTY_STRING;
                        final int colonptr = ref.indexOf(":");
                        if (colonptr > 0) {
                            prefix = ref.substring(0, colonptr);
                            child.setAttribute(SchemaSymbols.ATT_REF, prefix + ":" + newName);
                        }
                        else {
                            child.setAttribute(SchemaSymbols.ATT_REF, newName);
                        }
                        ++result;
                        if (elementSought.equals(SchemaSymbols.ELT_GROUP)) {
                            final String minOccurs = child.getAttribute(SchemaSymbols.ATT_MINOCCURS);
                            final String maxOccurs = child.getAttribute(SchemaSymbols.ATT_MAXOCCURS);
                            if ((maxOccurs.length() != 0 && !maxOccurs.equals("1")) || (minOccurs.length() != 0 && !minOccurs.equals("1"))) {
                                this.reportSchemaError("src-redefine.6.1.2", new Object[] { ref }, child);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    private XSDocumentInfo findXSDocumentForDecl(final XSDocumentInfo currSchema, final Element decl, final XSDocumentInfo decl_Doc) {
        final Object temp = decl_Doc;
        if (temp == null) {
            return null;
        }
        final XSDocumentInfo declDocInfo = (XSDocumentInfo)temp;
        return declDocInfo;
    }
    
    private boolean nonAnnotationContent(final Element elem) {
        for (Element child = DOMUtil.getFirstChildElement(elem); child != null; child = DOMUtil.getNextSiblingElement(child)) {
            if (!DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                return true;
            }
        }
        return false;
    }
    
    private void setSchemasVisible(final XSDocumentInfo startSchema) {
        if (DOMUtil.isHidden(startSchema.fSchemaElement, this.fHiddenNodes)) {
            DOMUtil.setVisible(startSchema.fSchemaElement, this.fHiddenNodes);
            final Vector<XSDocumentInfo> dependingSchemas = this.fDependencyMap.get(startSchema);
            for (int i = 0; i < dependingSchemas.size(); ++i) {
                this.setSchemasVisible(dependingSchemas.elementAt(i));
            }
        }
    }
    
    public SimpleLocator element2Locator(final Element e) {
        if (!(e instanceof ElementImpl)) {
            return null;
        }
        final SimpleLocator l = new SimpleLocator();
        return this.element2Locator(e, l) ? l : null;
    }
    
    public boolean element2Locator(final Element e, final SimpleLocator l) {
        if (l == null) {
            return false;
        }
        if (e instanceof ElementImpl) {
            final ElementImpl ele = (ElementImpl)e;
            final Document doc = ele.getOwnerDocument();
            final String sid = this.fDoc2SystemId.get(DOMUtil.getRoot(doc));
            final int line = ele.getLineNumber();
            final int column = ele.getColumnNumber();
            l.setValues(sid, sid, line, column, ele.getCharacterOffset());
            return true;
        }
        return false;
    }
    
    private Element getElementFromMap(final Map<String, Element> registry, final String declKey) {
        if (registry == null) {
            return null;
        }
        return registry.get(declKey);
    }
    
    private XSDocumentInfo getDocInfoFromMap(final Map<String, XSDocumentInfo> registry, final String declKey) {
        if (registry == null) {
            return null;
        }
        return registry.get(declKey);
    }
    
    private Object getFromMap(final Map registry, final String key) {
        if (registry == null) {
            return null;
        }
        return registry.get(key);
    }
    
    void reportSchemaFatalError(final String key, final Object[] args, final Element ele) {
        this.reportSchemaErr(key, args, ele, (short)2, null);
    }
    
    void reportSchemaError(final String key, final Object[] args, final Element ele) {
        this.reportSchemaErr(key, args, ele, (short)1, null);
    }
    
    void reportSchemaError(final String key, final Object[] args, final Element ele, final Exception exception) {
        this.reportSchemaErr(key, args, ele, (short)1, exception);
    }
    
    void reportSchemaWarning(final String key, final Object[] args, final Element ele) {
        this.reportSchemaErr(key, args, ele, (short)0, null);
    }
    
    void reportSchemaWarning(final String key, final Object[] args, final Element ele, final Exception exception) {
        this.reportSchemaErr(key, args, ele, (short)0, exception);
    }
    
    void reportSchemaErr(final String key, final Object[] args, final Element ele, final short type, final Exception exception) {
        if (this.element2Locator(ele, this.xl)) {
            this.fErrorReporter.reportError(this.xl, "http://www.w3.org/TR/xml-schema-1", key, args, type, exception);
        }
        else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, args, type, exception);
        }
    }
    
    public void setGenerateSyntheticAnnotations(final boolean state) {
        this.fSchemaParser.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", state);
    }
    
    static {
        NS_ERROR_CODES = new String[][] { { "src-include.2.1", "src-include.2.1" }, { "src-redefine.3.1", "src-redefine.3.1" }, { "src-import.3.1", "src-import.3.2" }, null, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" } };
        ELE_ERROR_CODES = new String[] { "src-include.1", "src-redefine.2", "src-import.2", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4" };
        COMP_TYPE = new String[] { null, "attribute declaration", "attribute group", "element declaration", "group", "identity constraint", "notation", "type definition" };
        CIRCULAR_CODES = new String[] { "Internal-Error", "Internal-Error", "src-attribute_group.3", "e-props-correct.6", "mg-props-correct.2", "Internal-Error", "Internal-Error", "st-props-correct.2" };
    }
    
    private static class XSAnnotationGrammarPool implements XMLGrammarPool
    {
        private XSGrammarBucket fGrammarBucket;
        private Grammar[] fInitialGrammarSet;
        
        @Override
        public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
            if (grammarType == "http://www.w3.org/2001/XMLSchema") {
                if (this.fInitialGrammarSet == null) {
                    if (this.fGrammarBucket == null) {
                        this.fInitialGrammarSet = new Grammar[] { SchemaGrammar.Schema4Annotations.INSTANCE };
                    }
                    else {
                        final SchemaGrammar[] schemaGrammars = this.fGrammarBucket.getGrammars();
                        for (int i = 0; i < schemaGrammars.length; ++i) {
                            if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(schemaGrammars[i].getTargetNamespace())) {
                                return this.fInitialGrammarSet = schemaGrammars;
                            }
                        }
                        final Grammar[] grammars = new Grammar[schemaGrammars.length + 1];
                        System.arraycopy(schemaGrammars, 0, grammars, 0, schemaGrammars.length);
                        grammars[grammars.length - 1] = SchemaGrammar.Schema4Annotations.INSTANCE;
                        this.fInitialGrammarSet = grammars;
                    }
                }
                return this.fInitialGrammarSet;
            }
            return new Grammar[0];
        }
        
        @Override
        public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
        }
        
        @Override
        public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
            if (desc.getGrammarType() == "http://www.w3.org/2001/XMLSchema") {
                final String tns = ((XMLSchemaDescription)desc).getTargetNamespace();
                if (this.fGrammarBucket != null) {
                    final Grammar grammar = this.fGrammarBucket.getGrammar(tns);
                    if (grammar != null) {
                        return grammar;
                    }
                }
                if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(tns)) {
                    return SchemaGrammar.Schema4Annotations.INSTANCE;
                }
            }
            return null;
        }
        
        public void refreshGrammars(final XSGrammarBucket gBucket) {
            this.fGrammarBucket = gBucket;
            this.fInitialGrammarSet = null;
        }
        
        @Override
        public void lockPool() {
        }
        
        @Override
        public void unlockPool() {
        }
        
        @Override
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
        
        @Override
        public int hashCode() {
            return (this.referNS == null) ? 0 : this.referNS.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof XSDKey)) {
                return false;
            }
            final XSDKey key = (XSDKey)obj;
            return this.referNS == key.referNS && this.systemId != null && this.systemId.equals(key.systemId);
        }
    }
    
    private static final class SAX2XNIUtil extends ErrorHandlerWrapper
    {
        public static XMLParseException createXMLParseException0(final SAXParseException exception) {
            return ErrorHandlerWrapper.createXMLParseException(exception);
        }
        
        public static XNIException createXNIException0(final SAXException exception) {
            return ErrorHandlerWrapper.createXNIException(exception);
        }
    }
}
