package org.apache.xerces.parsers;

import org.w3c.dom.Element;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import java.io.Reader;
import java.io.StringReader;
import org.w3c.dom.Node;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.w3c.dom.ls.LSInput;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.DOMError;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.Document;
import org.apache.xerces.dom.DOMStringListImpl;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.w3c.dom.DOMException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.w3c.dom.ls.LSResourceResolver;
import org.apache.xerces.util.DOMErrorHandlerWrapper;
import org.w3c.dom.DOMErrorHandler;
import java.util.Locale;
import java.util.Stack;
import org.w3c.dom.ls.LSParserFilter;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSParser;

public class DOMParserImpl extends AbstractDOMParser implements LSParser, DOMConfiguration
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    protected static final String XMLSCHEMA = "http://apache.org/xml/features/validation/schema";
    protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String PSVI_AUGMENT = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected boolean fNamespaceDeclarations;
    protected String fSchemaType;
    protected boolean fBusy;
    private boolean abortNow;
    private Thread currentThread;
    protected static final boolean DEBUG = false;
    private String fSchemaLocation;
    private DOMStringList fRecognizedParameters;
    private boolean fNullFilterInUse;
    private AbortHandler abortHandler;
    
    public DOMParserImpl(final String s, final String s2) {
        this((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", s));
        if (s2 != null) {
            if (s2.equals(Constants.NS_DTD)) {
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                this.fSchemaType = Constants.NS_DTD;
            }
            else if (s2.equals(Constants.NS_XMLSCHEMA)) {
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
            }
        }
    }
    
    public DOMParserImpl(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
        this.fNamespaceDeclarations = true;
        this.fSchemaType = null;
        this.fBusy = false;
        this.abortNow = false;
        this.fSchemaLocation = null;
        this.fNullFilterInUse = false;
        this.abortHandler = null;
        this.fConfiguration.addRecognizedFeatures(new String[] { "canonical-form", "cdata-sections", "charset-overrides-xml-encoding", "infoset", "namespace-declarations", "split-cdata-sections", "supported-media-types-only", "certified", "well-formed", "ignore-unknown-character-denormalizations" });
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        this.fConfiguration.setFeature("namespace-declarations", true);
        this.fConfiguration.setFeature("well-formed", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
        this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
        this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", false);
        this.fConfiguration.setFeature("canonical-form", false);
        this.fConfiguration.setFeature("charset-overrides-xml-encoding", true);
        this.fConfiguration.setFeature("split-cdata-sections", true);
        this.fConfiguration.setFeature("supported-media-types-only", false);
        this.fConfiguration.setFeature("ignore-unknown-character-denormalizations", true);
        this.fConfiguration.setFeature("certified", true);
        try {
            this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    public DOMParserImpl(final SymbolTable symbolTable) {
        this((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
    }
    
    public DOMParserImpl(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xmlGrammarPool);
    }
    
    public void reset() {
        super.reset();
        this.fNamespaceDeclarations = this.fConfiguration.getFeature("namespace-declarations");
        if (this.fNullFilterInUse) {
            this.fDOMFilter = null;
            this.fNullFilterInUse = false;
        }
        if (this.fSkippedElemStack != null) {
            this.fSkippedElemStack.removeAllElements();
        }
        this.fRejectedElementDepth = 0;
        this.fFilterReject = false;
        this.fSchemaType = null;
    }
    
    public DOMConfiguration getDomConfig() {
        return this;
    }
    
    public LSParserFilter getFilter() {
        return this.fNullFilterInUse ? null : this.fDOMFilter;
    }
    
    public void setFilter(final LSParserFilter fdomFilter) {
        if (this.fBusy && fdomFilter == null && this.fDOMFilter != null) {
            this.fNullFilterInUse = true;
            this.fDOMFilter = NullLSParserFilter.INSTANCE;
        }
        else {
            this.fDOMFilter = fdomFilter;
        }
        if (this.fSkippedElemStack == null) {
            this.fSkippedElemStack = new Stack();
        }
    }
    
    public void setParameter(final String s, final Object o) throws DOMException {
        if (o instanceof Boolean) {
            final boolean booleanValue = (boolean)o;
            try {
                if (s.equalsIgnoreCase("comments")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", booleanValue);
                }
                else if (s.equalsIgnoreCase("datatype-normalization")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", booleanValue);
                }
                else if (s.equalsIgnoreCase("entities")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", booleanValue);
                }
                else if (s.equalsIgnoreCase("disallow-doctype")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/disallow-doctype-decl", booleanValue);
                }
                else if (s.equalsIgnoreCase("supported-media-types-only") || s.equalsIgnoreCase("normalize-characters") || s.equalsIgnoreCase("check-character-normalization") || s.equalsIgnoreCase("canonical-form")) {
                    if (booleanValue) {
                        throw newFeatureNotSupportedError(s);
                    }
                }
                else if (s.equalsIgnoreCase("namespaces")) {
                    this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", booleanValue);
                }
                else if (s.equalsIgnoreCase("infoset")) {
                    if (booleanValue) {
                        this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", true);
                        this.fConfiguration.setFeature("namespace-declarations", true);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", false);
                    }
                }
                else if (s.equalsIgnoreCase("cdata-sections")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", booleanValue);
                }
                else if (s.equalsIgnoreCase("namespace-declarations")) {
                    this.fConfiguration.setFeature("namespace-declarations", booleanValue);
                }
                else if (s.equalsIgnoreCase("well-formed") || s.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                    if (!booleanValue) {
                        throw newFeatureNotSupportedError(s);
                    }
                }
                else if (s.equalsIgnoreCase("validate")) {
                    this.fConfiguration.setFeature("http://xml.org/sax/features/validation", booleanValue);
                    if (this.fSchemaType != Constants.NS_DTD) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", booleanValue);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", booleanValue);
                    }
                    if (booleanValue) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
                    }
                }
                else if (s.equalsIgnoreCase("validate-if-schema")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", booleanValue);
                    if (booleanValue) {
                        this.fConfiguration.setFeature("http://xml.org/sax/features/validation", false);
                    }
                }
                else if (s.equalsIgnoreCase("element-content-whitespace")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", booleanValue);
                }
                else if (s.equalsIgnoreCase("psvi")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.PSVIDocumentImpl");
                }
                else {
                    String lowerCase;
                    if (s.equalsIgnoreCase("http://apache.org/xml/features/honour-all-schemaLocations")) {
                        lowerCase = "http://apache.org/xml/features/honour-all-schemaLocations";
                    }
                    else if (s.equals("http://apache.org/xml/features/namespace-growth")) {
                        lowerCase = "http://apache.org/xml/features/namespace-growth";
                    }
                    else if (s.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                        lowerCase = "http://apache.org/xml/features/internal/tolerate-duplicates";
                    }
                    else {
                        lowerCase = s.toLowerCase(Locale.ENGLISH);
                    }
                    this.fConfiguration.setFeature(lowerCase, booleanValue);
                }
                return;
            }
            catch (final XMLConfigurationException ex) {
                throw newFeatureNotFoundError(s);
            }
        }
        if (s.equalsIgnoreCase("error-handler")) {
            if (!(o instanceof DOMErrorHandler)) {
                if (o != null) {
                    throw newTypeMismatchError(s);
                }
            }
            try {
                this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)o);
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fErrorHandler);
            }
            catch (final XMLConfigurationException ex2) {}
        }
        else if (s.equalsIgnoreCase("resource-resolver")) {
            if (!(o instanceof LSResourceResolver)) {
                if (o != null) {
                    throw newTypeMismatchError(s);
                }
            }
            try {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper((LSResourceResolver)o));
            }
            catch (final XMLConfigurationException ex3) {}
        }
        else if (s.equalsIgnoreCase("schema-location")) {
            if (!(o instanceof String)) {
                if (o != null) {
                    throw newTypeMismatchError(s);
                }
            }
            try {
                if (o == null) {
                    this.fSchemaLocation = null;
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
                }
                else {
                    this.fSchemaLocation = (String)o;
                    final StringTokenizer stringTokenizer = new StringTokenizer(this.fSchemaLocation, " \n\t\r");
                    if (stringTokenizer.hasMoreTokens()) {
                        final ArrayList list = new ArrayList();
                        list.add(stringTokenizer.nextToken());
                        while (stringTokenizer.hasMoreTokens()) {
                            list.add(stringTokenizer.nextToken());
                        }
                        this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", list.toArray());
                    }
                    else {
                        this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", o);
                    }
                }
            }
            catch (final XMLConfigurationException ex4) {}
        }
        else if (s.equalsIgnoreCase("schema-type")) {
            if (!(o instanceof String)) {
                if (o != null) {
                    throw newTypeMismatchError(s);
                }
            }
            try {
                if (o == null) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", false);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
                    this.fSchemaType = null;
                }
                else if (o.equals(Constants.NS_XMLSCHEMA)) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
                    this.fSchemaType = Constants.NS_XMLSCHEMA;
                }
                else if (o.equals(Constants.NS_DTD)) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", false);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                    this.fSchemaType = Constants.NS_DTD;
                }
            }
            catch (final XMLConfigurationException ex5) {}
        }
        else if (s.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", o);
        }
        else {
            String lowerCase2 = s.toLowerCase(Locale.ENGLISH);
            try {
                this.fConfiguration.setProperty(lowerCase2, o);
            }
            catch (final XMLConfigurationException ex6) {
                try {
                    if (s.equalsIgnoreCase("http://apache.org/xml/features/honour-all-schemaLocations")) {
                        lowerCase2 = "http://apache.org/xml/features/honour-all-schemaLocations";
                    }
                    else if (s.equals("http://apache.org/xml/features/namespace-growth")) {
                        lowerCase2 = "http://apache.org/xml/features/namespace-growth";
                    }
                    else if (s.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                        lowerCase2 = "http://apache.org/xml/features/internal/tolerate-duplicates";
                    }
                    this.fConfiguration.getFeature(lowerCase2);
                    throw newTypeMismatchError(s);
                }
                catch (final XMLConfigurationException ex7) {
                    throw newFeatureNotFoundError(s);
                }
            }
        }
    }
    
    public Object getParameter(final String s) throws DOMException {
        if (s.equalsIgnoreCase("comments")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("datatype-normalization")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/validation/schema/normalized-value") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("entities")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("namespaces")) {
            return this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("validate")) {
            return this.fConfiguration.getFeature("http://xml.org/sax/features/validation") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("validate-if-schema")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/validation/dynamic") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("element-content-whitespace")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("disallow-doctype")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/disallow-doctype-decl") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("infoset")) {
            return (this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces") && this.fConfiguration.getFeature("namespace-declarations") && this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") && this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") && !this.fConfiguration.getFeature("http://apache.org/xml/features/validation/dynamic") && !this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") && !this.fConfiguration.getFeature("http://apache.org/xml/features/validation/schema/normalized-value") && !this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes")) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("cdata-sections")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("check-character-normalization") || s.equalsIgnoreCase("normalize-characters")) {
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("namespace-declarations") || s.equalsIgnoreCase("well-formed") || s.equalsIgnoreCase("ignore-unknown-character-denormalizations") || s.equalsIgnoreCase("canonical-form") || s.equalsIgnoreCase("supported-media-types-only") || s.equalsIgnoreCase("split-cdata-sections") || s.equalsIgnoreCase("charset-overrides-xml-encoding")) {
            return this.fConfiguration.getFeature(s.toLowerCase(Locale.ENGLISH)) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("error-handler")) {
            if (this.fErrorHandler != null) {
                return this.fErrorHandler.getErrorHandler();
            }
            return null;
        }
        else {
            if (s.equalsIgnoreCase("resource-resolver")) {
                try {
                    final XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
                    if (xmlEntityResolver != null && xmlEntityResolver instanceof DOMEntityResolverWrapper) {
                        return ((DOMEntityResolverWrapper)xmlEntityResolver).getEntityResolver();
                    }
                }
                catch (final XMLConfigurationException ex) {}
                return null;
            }
            if (s.equalsIgnoreCase("schema-type")) {
                return this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
            }
            if (s.equalsIgnoreCase("schema-location")) {
                return this.fSchemaLocation;
            }
            if (s.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
                return this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
            }
            if (s.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
                return this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name");
            }
            String lowerCase;
            if (s.equalsIgnoreCase("http://apache.org/xml/features/honour-all-schemaLocations")) {
                lowerCase = "http://apache.org/xml/features/honour-all-schemaLocations";
            }
            else if (s.equals("http://apache.org/xml/features/namespace-growth")) {
                lowerCase = "http://apache.org/xml/features/namespace-growth";
            }
            else if (s.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                lowerCase = "http://apache.org/xml/features/internal/tolerate-duplicates";
            }
            else {
                lowerCase = s.toLowerCase(Locale.ENGLISH);
            }
            try {
                return this.fConfiguration.getFeature(lowerCase) ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (final XMLConfigurationException ex2) {
                try {
                    return this.fConfiguration.getProperty(lowerCase);
                }
                catch (final XMLConfigurationException ex3) {
                    throw newFeatureNotFoundError(s);
                }
            }
        }
    }
    
    public boolean canSetParameter(final String s, final Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Boolean) {
            final boolean booleanValue = (boolean)o;
            if (s.equalsIgnoreCase("supported-media-types-only") || s.equalsIgnoreCase("normalize-characters") || s.equalsIgnoreCase("check-character-normalization") || s.equalsIgnoreCase("canonical-form")) {
                return !booleanValue;
            }
            if (s.equalsIgnoreCase("well-formed") || s.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                return booleanValue;
            }
            if (s.equalsIgnoreCase("cdata-sections") || s.equalsIgnoreCase("charset-overrides-xml-encoding") || s.equalsIgnoreCase("comments") || s.equalsIgnoreCase("datatype-normalization") || s.equalsIgnoreCase("disallow-doctype") || s.equalsIgnoreCase("entities") || s.equalsIgnoreCase("infoset") || s.equalsIgnoreCase("namespaces") || s.equalsIgnoreCase("namespace-declarations") || s.equalsIgnoreCase("validate") || s.equalsIgnoreCase("validate-if-schema") || s.equalsIgnoreCase("element-content-whitespace") || s.equalsIgnoreCase("xml-declaration")) {
                return true;
            }
            try {
                String lowerCase;
                if (s.equalsIgnoreCase("http://apache.org/xml/features/honour-all-schemaLocations")) {
                    lowerCase = "http://apache.org/xml/features/honour-all-schemaLocations";
                }
                else if (s.equalsIgnoreCase("http://apache.org/xml/features/namespace-growth")) {
                    lowerCase = "http://apache.org/xml/features/namespace-growth";
                }
                else if (s.equalsIgnoreCase("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                    lowerCase = "http://apache.org/xml/features/internal/tolerate-duplicates";
                }
                else {
                    lowerCase = s.toLowerCase(Locale.ENGLISH);
                }
                this.fConfiguration.getFeature(lowerCase);
                return true;
            }
            catch (final XMLConfigurationException ex) {
                return false;
            }
        }
        if (s.equalsIgnoreCase("error-handler")) {
            return o instanceof DOMErrorHandler || o == null;
        }
        if (s.equalsIgnoreCase("resource-resolver")) {
            return o instanceof LSResourceResolver || o == null;
        }
        if (s.equalsIgnoreCase("schema-type")) {
            return (o instanceof String && (o.equals(Constants.NS_XMLSCHEMA) || o.equals(Constants.NS_DTD))) || o == null;
        }
        if (s.equalsIgnoreCase("schema-location")) {
            return o instanceof String || o == null;
        }
        if (s.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
            return true;
        }
        try {
            this.fConfiguration.getProperty(s.toLowerCase(Locale.ENGLISH));
            return true;
        }
        catch (final XMLConfigurationException ex2) {
            return false;
        }
    }
    
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final ArrayList list = new ArrayList();
            list.add("namespaces");
            list.add("cdata-sections");
            list.add("canonical-form");
            list.add("namespace-declarations");
            list.add("split-cdata-sections");
            list.add("entities");
            list.add("validate-if-schema");
            list.add("validate");
            list.add("datatype-normalization");
            list.add("charset-overrides-xml-encoding");
            list.add("check-character-normalization");
            list.add("supported-media-types-only");
            list.add("ignore-unknown-character-denormalizations");
            list.add("normalize-characters");
            list.add("well-formed");
            list.add("infoset");
            list.add("disallow-doctype");
            list.add("element-content-whitespace");
            list.add("comments");
            list.add("error-handler");
            list.add("resource-resolver");
            list.add("schema-location");
            list.add("schema-type");
            this.fRecognizedParameters = new DOMStringListImpl(list);
        }
        return this.fRecognizedParameters;
    }
    
    public Document parseURI(final String s) throws LSException {
        if (this.fBusy) {
            throw newInvalidStateError();
        }
        final XMLInputSource xmlInputSource = new XMLInputSource(null, s, null);
        try {
            this.currentThread = Thread.currentThread();
            this.fBusy = true;
            this.parse(xmlInputSource);
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                this.abortNow = false;
                Thread.interrupted();
            }
        }
        catch (final Exception fException) {
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                Thread.interrupted();
            }
            if (this.abortNow) {
                this.abortNow = false;
                this.restoreHandlers();
                return null;
            }
            if (fException != Abort.INSTANCE) {
                if (!(fException instanceof XMLParseException) && this.fErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                    domErrorImpl.fException = fException;
                    domErrorImpl.fMessage = fException.getMessage();
                    domErrorImpl.fSeverity = 3;
                    this.fErrorHandler.getErrorHandler().handleError(domErrorImpl);
                }
                throw (LSException)DOMUtil.createLSException((short)81, fException).fillInStackTrace();
            }
        }
        final Document document = this.getDocument();
        this.dropDocumentReferences();
        return document;
    }
    
    public Document parse(final LSInput lsInput) throws LSException {
        final XMLInputSource dom2xmlInputSource = this.dom2xmlInputSource(lsInput);
        if (this.fBusy) {
            throw newInvalidStateError();
        }
        try {
            this.currentThread = Thread.currentThread();
            this.fBusy = true;
            this.parse(dom2xmlInputSource);
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                this.abortNow = false;
                Thread.interrupted();
            }
        }
        catch (final Exception fException) {
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                Thread.interrupted();
            }
            if (this.abortNow) {
                this.abortNow = false;
                this.restoreHandlers();
                return null;
            }
            if (fException != Abort.INSTANCE) {
                if (!(fException instanceof XMLParseException) && this.fErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                    domErrorImpl.fException = fException;
                    domErrorImpl.fMessage = fException.getMessage();
                    domErrorImpl.fSeverity = 3;
                    this.fErrorHandler.getErrorHandler().handleError(domErrorImpl);
                }
                throw (LSException)DOMUtil.createLSException((short)81, fException).fillInStackTrace();
            }
        }
        final Document document = this.getDocument();
        this.dropDocumentReferences();
        return document;
    }
    
    private void restoreHandlers() {
        this.fConfiguration.setDocumentHandler(this);
        this.fConfiguration.setDTDHandler(this);
        this.fConfiguration.setDTDContentModelHandler(this);
    }
    
    public Node parseWithContext(final LSInput lsInput, final Node node, final short n) throws DOMException, LSException {
        throw new DOMException((short)9, "Not supported");
    }
    
    XMLInputSource dom2xmlInputSource(final LSInput lsInput) {
        XMLInputSource xmlInputSource;
        if (lsInput.getCharacterStream() != null) {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI(), lsInput.getCharacterStream(), "UTF-16");
        }
        else if (lsInput.getByteStream() != null) {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI(), lsInput.getByteStream(), lsInput.getEncoding());
        }
        else if (lsInput.getStringData() != null && lsInput.getStringData().length() > 0) {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI(), new StringReader(lsInput.getStringData()), "UTF-16");
        }
        else {
            if ((lsInput.getSystemId() == null || lsInput.getSystemId().length() <= 0) && (lsInput.getPublicId() == null || lsInput.getPublicId().length() <= 0)) {
                if (this.fErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                    domErrorImpl.fType = "no-input-specified";
                    domErrorImpl.fMessage = "no-input-specified";
                    domErrorImpl.fSeverity = 3;
                    this.fErrorHandler.getErrorHandler().handleError(domErrorImpl);
                }
                throw new LSException((short)81, "no-input-specified");
            }
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI());
        }
        return xmlInputSource;
    }
    
    public boolean getAsync() {
        return false;
    }
    
    public boolean getBusy() {
        return this.fBusy;
    }
    
    public void abort() {
        if (this.fBusy) {
            this.fBusy = false;
            if (this.currentThread != null) {
                this.abortNow = true;
                if (this.abortHandler == null) {
                    this.abortHandler = new AbortHandler();
                }
                this.fConfiguration.setDocumentHandler(this.abortHandler);
                this.fConfiguration.setDTDHandler(this.abortHandler);
                this.fConfiguration.setDTDContentModelHandler(this.abortHandler);
                if (this.currentThread == Thread.currentThread()) {
                    throw Abort.INSTANCE;
                }
                this.currentThread.interrupt();
            }
        }
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) {
        if (!this.fNamespaceDeclarations && this.fNamespaceAware) {
            for (int i = xmlAttributes.getLength() - 1; i >= 0; --i) {
                if (XMLSymbols.PREFIX_XMLNS == xmlAttributes.getPrefix(i) || XMLSymbols.PREFIX_XMLNS == xmlAttributes.getQName(i)) {
                    xmlAttributes.removeAttributeAt(i);
                }
            }
        }
        super.startElement(qName, xmlAttributes, augmentations);
    }
    
    private static DOMException newInvalidStateError() {
        throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
    }
    
    private static DOMException newFeatureNotSupportedError(final String s) {
        return new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
    }
    
    private static DOMException newFeatureNotFoundError(final String s) {
        return new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { s }));
    }
    
    private static DOMException newTypeMismatchError(final String s) {
        return new DOMException((short)17, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { s }));
    }
    
    private static final class AbortHandler implements XMLDocumentHandler, XMLDTDHandler, XMLDTDContentModelHandler
    {
        private XMLDocumentSource documentSource;
        private XMLDTDContentModelSource dtdContentSource;
        private XMLDTDSource dtdSource;
        
        public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startCDATA(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endCDATA(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endDocument(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void setDocumentSource(final XMLDocumentSource documentSource) {
            this.documentSource = documentSource;
        }
        
        public XMLDocumentSource getDocumentSource() {
            return this.documentSource;
        }
        
        public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endExternalSubset(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endAttlist(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endConditional(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endDTD(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void setDTDSource(final XMLDTDSource dtdSource) {
            this.dtdSource = dtdSource;
        }
        
        public XMLDTDSource getDTDSource() {
            return this.dtdSource;
        }
        
        public void startContentModel(final String s, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void any(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void empty(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void startGroup(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void pcdata(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void element(final String s, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void separator(final short n, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void occurrence(final short n, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endGroup(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void endContentModel(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        public void setDTDContentModelSource(final XMLDTDContentModelSource dtdContentSource) {
            this.dtdContentSource = dtdContentSource;
        }
        
        public XMLDTDContentModelSource getDTDContentModelSource() {
            return this.dtdContentSource;
        }
    }
    
    static final class NullLSParserFilter implements LSParserFilter
    {
        static final NullLSParserFilter INSTANCE;
        
        private NullLSParserFilter() {
        }
        
        public short acceptNode(final Node node) {
            return 1;
        }
        
        public int getWhatToShow() {
            return -1;
        }
        
        public short startElement(final Element element) {
            return 1;
        }
        
        static {
            INSTANCE = new NullLSParserFilter();
        }
    }
}
