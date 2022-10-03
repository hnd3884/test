package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.io.Reader;
import java.io.StringReader;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import org.w3c.dom.ls.LSInput;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.DOMError;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import org.w3c.dom.ls.LSResourceResolver;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import org.w3c.dom.DOMErrorHandler;
import java.util.Locale;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.util.Stack;
import org.w3c.dom.ls.LSParserFilter;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.w3c.dom.DOMStringList;
import java.util.Vector;
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
    private Vector fSchemaLocations;
    private String fSchemaLocation;
    private DOMStringList fRecognizedParameters;
    private AbortHandler abortHandler;
    
    public DOMParserImpl(final XMLParserConfiguration config, final String schemaType) {
        this(config);
        if (schemaType != null) {
            if (schemaType.equals(Constants.NS_DTD)) {
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                this.fSchemaType = Constants.NS_DTD;
            }
            else if (schemaType.equals(Constants.NS_XMLSCHEMA)) {
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
            }
        }
    }
    
    public DOMParserImpl(final XMLParserConfiguration config) {
        super(config);
        this.fNamespaceDeclarations = true;
        this.fSchemaType = null;
        this.fBusy = false;
        this.abortNow = false;
        this.fSchemaLocations = new Vector();
        this.fSchemaLocation = null;
        this.abortHandler = null;
        final String[] domRecognizedFeatures = { "canonical-form", "cdata-sections", "charset-overrides-xml-encoding", "infoset", "namespace-declarations", "split-cdata-sections", "supported-media-types-only", "certified", "well-formed", "ignore-unknown-character-denormalizations" };
        this.fConfiguration.addRecognizedFeatures(domRecognizedFeatures);
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
        this(new XIncludeAwareParserConfiguration());
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
    }
    
    public DOMParserImpl(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(new XIncludeAwareParserConfiguration());
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.fNamespaceDeclarations = this.fConfiguration.getFeature("namespace-declarations");
        if (this.fSkippedElemStack != null) {
            this.fSkippedElemStack.removeAllElements();
        }
        this.fSchemaLocations.clear();
        this.fRejectedElementDepth = 0;
        this.fFilterReject = false;
        this.fSchemaType = null;
    }
    
    @Override
    public DOMConfiguration getDomConfig() {
        return this;
    }
    
    @Override
    public LSParserFilter getFilter() {
        return this.fDOMFilter;
    }
    
    @Override
    public void setFilter(final LSParserFilter filter) {
        this.fDOMFilter = filter;
        if (this.fSkippedElemStack == null) {
            this.fSkippedElemStack = new Stack();
        }
    }
    
    @Override
    public void setParameter(final String name, final Object value) throws DOMException {
        if (value instanceof Boolean) {
            final boolean state = (boolean)value;
            try {
                if (name.equalsIgnoreCase("comments")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", state);
                }
                else if (name.equalsIgnoreCase("datatype-normalization")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", state);
                }
                else if (name.equalsIgnoreCase("entities")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", state);
                }
                else if (name.equalsIgnoreCase("disallow-doctype")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/disallow-doctype-decl", state);
                }
                else if (name.equalsIgnoreCase("supported-media-types-only") || name.equalsIgnoreCase("normalize-characters") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("canonical-form")) {
                    if (state) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                        throw new DOMException((short)9, msg);
                    }
                }
                else if (name.equalsIgnoreCase("namespaces")) {
                    this.fConfiguration.setFeature("http://xml.org/sax/features/namespaces", state);
                }
                else if (name.equalsIgnoreCase("infoset")) {
                    if (state) {
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
                else if (name.equalsIgnoreCase("cdata-sections")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", state);
                }
                else if (name.equalsIgnoreCase("namespace-declarations")) {
                    this.fConfiguration.setFeature("namespace-declarations", state);
                }
                else if (name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                    if (!state) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                        throw new DOMException((short)9, msg);
                    }
                }
                else if (name.equalsIgnoreCase("validate")) {
                    this.fConfiguration.setFeature("http://xml.org/sax/features/validation", state);
                    if (this.fSchemaType != Constants.NS_DTD) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", state);
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", state);
                    }
                    if (state) {
                        this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", false);
                    }
                }
                else if (name.equalsIgnoreCase("validate-if-schema")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/dynamic", state);
                    if (state) {
                        this.fConfiguration.setFeature("http://xml.org/sax/features/validation", false);
                    }
                }
                else if (name.equalsIgnoreCase("element-content-whitespace")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", state);
                }
                else if (name.equalsIgnoreCase("psvi")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl");
                }
                else {
                    String normalizedName;
                    if (name.equals("http://apache.org/xml/features/namespace-growth")) {
                        normalizedName = "http://apache.org/xml/features/namespace-growth";
                    }
                    else if (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                        normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
                    }
                    else {
                        normalizedName = name.toLowerCase(Locale.ENGLISH);
                    }
                    this.fConfiguration.setFeature(normalizedName, state);
                }
            }
            catch (final XMLConfigurationException e) {
                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
                throw new DOMException((short)8, msg2);
            }
        }
        else if (name.equalsIgnoreCase("error-handler")) {
            if (!(value instanceof DOMErrorHandler)) {
                if (value != null) {
                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg3);
                }
            }
            try {
                this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)value);
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fErrorHandler);
            }
            catch (final XMLConfigurationException ex) {}
        }
        else if (name.equalsIgnoreCase("resource-resolver")) {
            if (!(value instanceof LSResourceResolver)) {
                if (value != null) {
                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg3);
                }
            }
            try {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper((LSResourceResolver)value));
            }
            catch (final XMLConfigurationException ex2) {}
        }
        else if (name.equalsIgnoreCase("schema-location")) {
            if (!(value instanceof String)) {
                if (value != null) {
                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg3);
                }
            }
            try {
                if (value == null) {
                    this.fSchemaLocation = null;
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
                }
                else {
                    this.fSchemaLocation = (String)value;
                    final StringTokenizer t = new StringTokenizer(this.fSchemaLocation, " \n\t\r");
                    if (t.hasMoreTokens()) {
                        this.fSchemaLocations.clear();
                        this.fSchemaLocations.add(t.nextToken());
                        while (t.hasMoreTokens()) {
                            this.fSchemaLocations.add(t.nextToken());
                        }
                        this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", this.fSchemaLocations.toArray());
                    }
                    else {
                        this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", value);
                    }
                }
            }
            catch (final XMLConfigurationException ex3) {}
        }
        else if (name.equalsIgnoreCase("schema-type")) {
            if (!(value instanceof String)) {
                if (value != null) {
                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg3);
                }
            }
            try {
                if (value == null) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", false);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
                    this.fSchemaType = null;
                }
                else if (value.equals(Constants.NS_XMLSCHEMA)) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
                    this.fSchemaType = Constants.NS_XMLSCHEMA;
                }
                else if (value.equals(Constants.NS_DTD)) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", false);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                    this.fSchemaType = Constants.NS_DTD;
                }
            }
            catch (final XMLConfigurationException ex4) {}
        }
        else if (name.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", value);
        }
        else {
            String normalizedName2 = name.toLowerCase(Locale.ENGLISH);
            try {
                this.fConfiguration.setProperty(normalizedName2, value);
            }
            catch (final XMLConfigurationException ex5) {
                try {
                    if (name.equals("http://apache.org/xml/features/namespace-growth")) {
                        normalizedName2 = "http://apache.org/xml/features/namespace-growth";
                    }
                    else if (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                        normalizedName2 = "http://apache.org/xml/features/internal/tolerate-duplicates";
                    }
                    this.fConfiguration.getFeature(normalizedName2);
                    throw newTypeMismatchError(name);
                }
                catch (final XMLConfigurationException ex6) {
                    throw newFeatureNotFoundError(name);
                }
            }
        }
    }
    
    @Override
    public Object getParameter(final String name) throws DOMException {
        if (name.equalsIgnoreCase("comments")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("datatype-normalization")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/validation/schema/normalized-value") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("entities")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespaces")) {
            return this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("validate")) {
            return this.fConfiguration.getFeature("http://xml.org/sax/features/validation") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("validate-if-schema")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/validation/dynamic") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("element-content-whitespace")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("disallow-doctype")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/disallow-doctype-decl") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("infoset")) {
            final boolean infoset = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces") && this.fConfiguration.getFeature("namespace-declarations") && this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") && this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") && !this.fConfiguration.getFeature("http://apache.org/xml/features/validation/dynamic") && !this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") && !this.fConfiguration.getFeature("http://apache.org/xml/features/validation/schema/normalized-value") && !this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes");
            return infoset ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("cdata-sections")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("normalize-characters")) {
            return Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespace-declarations") || name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("ignore-unknown-character-denormalizations") || name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("supported-media-types-only") || name.equalsIgnoreCase("split-cdata-sections") || name.equalsIgnoreCase("charset-overrides-xml-encoding")) {
            return this.fConfiguration.getFeature(name.toLowerCase(Locale.ENGLISH)) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (!name.equalsIgnoreCase("error-handler")) {
            if (name.equalsIgnoreCase("resource-resolver")) {
                try {
                    final XMLEntityResolver entityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
                    if (entityResolver != null && entityResolver instanceof DOMEntityResolverWrapper) {
                        return ((DOMEntityResolverWrapper)entityResolver).getEntityResolver();
                    }
                    return null;
                }
                catch (final XMLConfigurationException ex) {
                    return null;
                }
            }
            if (name.equalsIgnoreCase("schema-type")) {
                return this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
            }
            if (name.equalsIgnoreCase("schema-location")) {
                return this.fSchemaLocation;
            }
            if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
                return this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
            }
            if (name.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
                return this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name");
            }
            String normalizedName;
            if (name.equals("http://apache.org/xml/features/namespace-growth")) {
                normalizedName = "http://apache.org/xml/features/namespace-growth";
            }
            else if (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
            }
            else {
                normalizedName = name.toLowerCase(Locale.ENGLISH);
            }
            try {
                return this.fConfiguration.getFeature(normalizedName) ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (final XMLConfigurationException ex2) {
                try {
                    return this.fConfiguration.getProperty(normalizedName);
                }
                catch (final XMLConfigurationException ex3) {
                    throw newFeatureNotFoundError(name);
                }
            }
            return null;
        }
        if (this.fErrorHandler != null) {
            return this.fErrorHandler.getErrorHandler();
        }
        return null;
    }
    
    @Override
    public boolean canSetParameter(final String name, final Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Boolean) {
            final boolean state = (boolean)value;
            if (name.equalsIgnoreCase("supported-media-types-only") || name.equalsIgnoreCase("normalize-characters") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("canonical-form")) {
                return !state;
            }
            if (name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                return state;
            }
            if (name.equalsIgnoreCase("cdata-sections") || name.equalsIgnoreCase("charset-overrides-xml-encoding") || name.equalsIgnoreCase("comments") || name.equalsIgnoreCase("datatype-normalization") || name.equalsIgnoreCase("disallow-doctype") || name.equalsIgnoreCase("entities") || name.equalsIgnoreCase("infoset") || name.equalsIgnoreCase("namespaces") || name.equalsIgnoreCase("namespace-declarations") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("element-content-whitespace") || name.equalsIgnoreCase("xml-declaration")) {
                return true;
            }
            try {
                String normalizedName;
                if (name.equalsIgnoreCase("http://apache.org/xml/features/namespace-growth")) {
                    normalizedName = "http://apache.org/xml/features/namespace-growth";
                }
                else if (name.equalsIgnoreCase("http://apache.org/xml/features/internal/tolerate-duplicates")) {
                    normalizedName = "http://apache.org/xml/features/internal/tolerate-duplicates";
                }
                else {
                    normalizedName = name.toLowerCase(Locale.ENGLISH);
                }
                this.fConfiguration.getFeature(normalizedName);
                return true;
            }
            catch (final XMLConfigurationException e) {
                return false;
            }
        }
        if (name.equalsIgnoreCase("error-handler")) {
            return value instanceof DOMErrorHandler || value == null;
        }
        if (name.equalsIgnoreCase("resource-resolver")) {
            return value instanceof LSResourceResolver || value == null;
        }
        if (name.equalsIgnoreCase("schema-type")) {
            return (value instanceof String && (value.equals(Constants.NS_XMLSCHEMA) || value.equals(Constants.NS_DTD))) || value == null;
        }
        if (name.equalsIgnoreCase("schema-location")) {
            return value instanceof String || value == null;
        }
        return name.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name");
    }
    
    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final Vector parameters = new Vector();
            parameters.add("namespaces");
            parameters.add("cdata-sections");
            parameters.add("canonical-form");
            parameters.add("namespace-declarations");
            parameters.add("split-cdata-sections");
            parameters.add("entities");
            parameters.add("validate-if-schema");
            parameters.add("validate");
            parameters.add("datatype-normalization");
            parameters.add("charset-overrides-xml-encoding");
            parameters.add("check-character-normalization");
            parameters.add("supported-media-types-only");
            parameters.add("ignore-unknown-character-denormalizations");
            parameters.add("normalize-characters");
            parameters.add("well-formed");
            parameters.add("infoset");
            parameters.add("disallow-doctype");
            parameters.add("element-content-whitespace");
            parameters.add("comments");
            parameters.add("error-handler");
            parameters.add("resource-resolver");
            parameters.add("schema-location");
            parameters.add("schema-type");
            this.fRecognizedParameters = new DOMStringListImpl(parameters);
        }
        return this.fRecognizedParameters;
    }
    
    @Override
    public Document parseURI(final String uri) throws LSException {
        if (this.fBusy) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null);
            throw new DOMException((short)11, msg);
        }
        final XMLInputSource source = new XMLInputSource(null, uri, null);
        try {
            this.currentThread = Thread.currentThread();
            this.fBusy = true;
            this.parse(source);
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                this.abortNow = false;
                Thread.interrupted();
            }
        }
        catch (final Exception e) {
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                Thread.interrupted();
            }
            if (this.abortNow) {
                this.abortNow = false;
                this.restoreHandlers();
                return null;
            }
            if (e != Abort.INSTANCE) {
                if (!(e instanceof XMLParseException) && this.fErrorHandler != null) {
                    final DOMErrorImpl error = new DOMErrorImpl();
                    error.fException = e;
                    error.fMessage = e.getMessage();
                    error.fSeverity = 3;
                    this.fErrorHandler.getErrorHandler().handleError(error);
                }
                throw (LSException)DOMUtil.createLSException((short)81, e).fillInStackTrace();
            }
        }
        final Document doc = this.getDocument();
        this.dropDocumentReferences();
        return doc;
    }
    
    @Override
    public Document parse(final LSInput is) throws LSException {
        final XMLInputSource xmlInputSource = this.dom2xmlInputSource(is);
        if (this.fBusy) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null);
            throw new DOMException((short)11, msg);
        }
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
        catch (final Exception e) {
            this.fBusy = false;
            if (this.abortNow && this.currentThread.isInterrupted()) {
                Thread.interrupted();
            }
            if (this.abortNow) {
                this.abortNow = false;
                this.restoreHandlers();
                return null;
            }
            if (e != Abort.INSTANCE) {
                if (!(e instanceof XMLParseException) && this.fErrorHandler != null) {
                    final DOMErrorImpl error = new DOMErrorImpl();
                    error.fException = e;
                    error.fMessage = e.getMessage();
                    error.fSeverity = 3;
                    this.fErrorHandler.getErrorHandler().handleError(error);
                }
                throw (LSException)DOMUtil.createLSException((short)81, e).fillInStackTrace();
            }
        }
        final Document doc = this.getDocument();
        this.dropDocumentReferences();
        return doc;
    }
    
    private void restoreHandlers() {
        this.fConfiguration.setDocumentHandler(this);
        this.fConfiguration.setDTDHandler(this);
        this.fConfiguration.setDTDContentModelHandler(this);
    }
    
    @Override
    public Node parseWithContext(final LSInput is, final Node cnode, final short action) throws DOMException, LSException {
        throw new DOMException((short)9, "Not supported");
    }
    
    XMLInputSource dom2xmlInputSource(final LSInput is) {
        XMLInputSource xis = null;
        if (is.getCharacterStream() != null) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getCharacterStream(), "UTF-16");
        }
        else if (is.getByteStream() != null) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getByteStream(), is.getEncoding());
        }
        else if (is.getStringData() != null && is.getStringData().length() > 0) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), new StringReader(is.getStringData()), "UTF-16");
        }
        else {
            if ((is.getSystemId() == null || is.getSystemId().length() <= 0) && (is.getPublicId() == null || is.getPublicId().length() <= 0)) {
                if (this.fErrorHandler != null) {
                    final DOMErrorImpl error = new DOMErrorImpl();
                    error.fType = "no-input-specified";
                    error.fMessage = "no-input-specified";
                    error.fSeverity = 3;
                    this.fErrorHandler.getErrorHandler().handleError(error);
                }
                throw new LSException((short)81, "no-input-specified");
            }
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI());
        }
        return xis;
    }
    
    @Override
    public boolean getAsync() {
        return false;
    }
    
    @Override
    public boolean getBusy() {
        return this.fBusy;
    }
    
    @Override
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
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) {
        if (!this.fNamespaceDeclarations && this.fNamespaceAware) {
            final int len = attributes.getLength();
            for (int i = len - 1; i >= 0; --i) {
                if (XMLSymbols.PREFIX_XMLNS == attributes.getPrefix(i) || XMLSymbols.PREFIX_XMLNS == attributes.getQName(i)) {
                    attributes.removeAttributeAt(i);
                }
            }
        }
        super.startElement(element, attributes, augs);
    }
    
    private static DOMException newFeatureNotFoundError(final String name) {
        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
        return new DOMException((short)8, msg);
    }
    
    private static DOMException newTypeMismatchError(final String name) {
        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
        return new DOMException((short)17, msg);
    }
    
    private class AbortHandler implements XMLDocumentHandler, XMLDTDHandler, XMLDTDContentModelHandler
    {
        private XMLDocumentSource documentSource;
        private XMLDTDContentModelSource dtdContentSource;
        private XMLDTDSource dtdSource;
        
        @Override
        public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void comment(final XMLString text, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void characters(final XMLString text, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endElement(final QName element, final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startCDATA(final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endCDATA(final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endDocument(final Augmentations augs) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void setDocumentSource(final XMLDocumentSource source) {
            this.documentSource = source;
        }
        
        @Override
        public XMLDocumentSource getDocumentSource() {
            return this.documentSource;
        }
        
        @Override
        public void startDTD(final XMLLocator locator, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endParameterEntity(final String name, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endExternalSubset(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void elementDecl(final String name, final String contentModel, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startAttlist(final String elementName, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endAttlist(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startConditional(final short type, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void ignoredCharacters(final XMLString text, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endConditional(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endDTD(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void setDTDSource(final XMLDTDSource source) {
            this.dtdSource = source;
        }
        
        @Override
        public XMLDTDSource getDTDSource() {
            return this.dtdSource;
        }
        
        @Override
        public void startContentModel(final String elementName, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void any(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void empty(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void startGroup(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void pcdata(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void element(final String elementName, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void separator(final short separator, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void occurrence(final short occurrence, final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endGroup(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void endContentModel(final Augmentations augmentations) throws XNIException {
            throw Abort.INSTANCE;
        }
        
        @Override
        public void setDTDContentModelSource(final XMLDTDContentModelSource source) {
            this.dtdContentSource = source;
        }
        
        @Override
        public XMLDTDContentModelSource getDTDContentModelSource() {
            return this.dtdContentSource;
        }
    }
}
