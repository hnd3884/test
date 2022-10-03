package com.sun.org.apache.xerces.internal.impl.xs;

import java.io.StringReader;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.DOMErrorHandler;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import org.w3c.dom.DOMError;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import org.w3c.dom.ls.LSInput;
import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.util.Status;
import java.io.File;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import org.w3c.dom.DOMStringList;
import java.util.Locale;
import java.util.Map;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.traversers.XSDHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import org.w3c.dom.DOMConfiguration;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;

public class XMLSchemaLoader implements XMLGrammarLoader, XMLComponent, XSLoader, DOMConfiguration
{
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    protected static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
    private static final String[] RECOGNIZED_FEATURES;
    public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
    public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
    private static final String[] RECOGNIZED_PROPERTIES;
    private ParserConfigurationSettings fLoaderConfig;
    private SymbolTable fSymbolTable;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityManager fEntityManager;
    private XMLEntityResolver fUserEntityResolver;
    private XMLGrammarPool fGrammarPool;
    private String fExternalSchemas;
    private String fExternalNoNSSchema;
    private Object fJAXPSource;
    private boolean fIsCheckedFully;
    private boolean fJAXPProcessed;
    private boolean fSettingsChanged;
    private XSDHandler fSchemaHandler;
    private XSGrammarBucket fGrammarBucket;
    private XSDeclarationPool fDeclPool;
    private SubstitutionGroupHandler fSubGroupHandler;
    private final CMNodeFactory fNodeFactory;
    private CMBuilder fCMBuilder;
    private XSDDescription fXSDDescription;
    private String faccessExternalSchema;
    private Map fJAXPCache;
    private Locale fLocale;
    private DOMStringList fRecognizedParameters;
    private DOMErrorHandlerWrapper fErrorHandler;
    private DOMEntityResolverWrapper fResourceResolver;
    
    public XMLSchemaLoader() {
        this(new SymbolTable(), null, new XMLEntityManager(), null, null, null);
    }
    
    public XMLSchemaLoader(final SymbolTable symbolTable) {
        this(symbolTable, null, new XMLEntityManager(), null, null, null);
    }
    
    XMLSchemaLoader(final XMLErrorReporter errorReporter, final XSGrammarBucket grammarBucket, final SubstitutionGroupHandler sHandler, final CMBuilder builder) {
        this(null, errorReporter, null, grammarBucket, sHandler, builder);
    }
    
    XMLSchemaLoader(final SymbolTable symbolTable, XMLErrorReporter errorReporter, final XMLEntityManager entityResolver, XSGrammarBucket grammarBucket, SubstitutionGroupHandler sHandler, CMBuilder builder) {
        this.fLoaderConfig = new ParserConfigurationSettings();
        this.fSymbolTable = null;
        this.fErrorReporter = new XMLErrorReporter();
        this.fEntityManager = null;
        this.fUserEntityResolver = null;
        this.fGrammarPool = null;
        this.fExternalSchemas = null;
        this.fExternalNoNSSchema = null;
        this.fJAXPSource = null;
        this.fIsCheckedFully = false;
        this.fJAXPProcessed = false;
        this.fSettingsChanged = true;
        this.fDeclPool = null;
        this.fNodeFactory = new CMNodeFactory();
        this.fXSDDescription = new XSDDescription();
        this.faccessExternalSchema = "all";
        this.fLocale = Locale.getDefault();
        this.fRecognizedParameters = null;
        this.fErrorHandler = null;
        this.fResourceResolver = null;
        this.fLoaderConfig.addRecognizedFeatures(XMLSchemaLoader.RECOGNIZED_FEATURES);
        this.fLoaderConfig.addRecognizedProperties(XMLSchemaLoader.RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        }
        if (errorReporter == null) {
            errorReporter = new XMLErrorReporter();
            errorReporter.setLocale(this.fLocale);
            errorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
        }
        this.fErrorReporter = errorReporter;
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.fEntityManager = entityResolver;
        if (this.fEntityManager != null) {
            this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        }
        this.fLoaderConfig.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
        if (grammarBucket == null) {
            grammarBucket = new XSGrammarBucket();
        }
        this.fGrammarBucket = grammarBucket;
        if (sHandler == null) {
            sHandler = new SubstitutionGroupHandler(this.fGrammarBucket);
        }
        this.fSubGroupHandler = sHandler;
        if (builder == null) {
            builder = new CMBuilder(this.fNodeFactory);
        }
        this.fCMBuilder = builder;
        this.fSchemaHandler = new XSDHandler(this.fGrammarBucket);
        if (this.fDeclPool != null) {
            this.fDeclPool.reset();
        }
        this.fJAXPCache = new HashMap();
        this.fSettingsChanged = true;
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLSchemaLoader.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public boolean getFeature(final String featureId) throws XMLConfigurationException {
        return this.fLoaderConfig.getFeature(featureId);
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        this.fSettingsChanged = true;
        if (featureId.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
            this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", state);
        }
        else if (featureId.equals("http://apache.org/xml/features/generate-synthetic-annotations")) {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(state);
        }
        this.fLoaderConfig.setFeature(featureId, state);
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLSchemaLoader.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public Object getProperty(final String propertyId) throws XMLConfigurationException {
        return this.fLoaderConfig.getProperty(propertyId);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object state) throws XMLConfigurationException {
        this.fSettingsChanged = true;
        this.fLoaderConfig.setProperty(propertyId, state);
        if (propertyId.equals("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
            this.fJAXPSource = state;
            this.fJAXPProcessed = false;
        }
        else if (propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            this.fGrammarPool = (XMLGrammarPool)state;
        }
        else if (propertyId.equals("http://apache.org/xml/properties/schema/external-schemaLocation")) {
            this.fExternalSchemas = (String)state;
        }
        else if (propertyId.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation")) {
            this.fExternalNoNSSchema = (String)state;
        }
        else if (propertyId.equals("http://apache.org/xml/properties/locale")) {
            this.setLocale((Locale)state);
        }
        else if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", state);
        }
        else if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.fErrorReporter = (XMLErrorReporter)state;
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
            }
        }
        else if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)state;
            this.faccessExternalSchema = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
        }
    }
    
    @Override
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
    
    @Override
    public void setErrorHandler(final XMLErrorHandler errorHandler) {
        this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler);
    }
    
    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }
    
    @Override
    public void setEntityResolver(final XMLEntityResolver entityResolver) {
        this.fUserEntityResolver = entityResolver;
        this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
        this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
    }
    
    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fUserEntityResolver;
    }
    
    public void loadGrammar(final XMLInputSource[] source) throws IOException, XNIException {
        for (int numSource = source.length, i = 0; i < numSource; ++i) {
            this.loadGrammar(source[i]);
        }
    }
    
    @Override
    public Grammar loadGrammar(final XMLInputSource source) throws IOException, XNIException {
        this.reset(this.fLoaderConfig);
        this.fSettingsChanged = false;
        final XSDDescription desc = new XSDDescription();
        desc.fContextType = 3;
        desc.setBaseSystemId(source.getBaseSystemId());
        desc.setLiteralSystemId(source.getSystemId());
        final Map locationPairs = new HashMap();
        processExternalHints(this.fExternalSchemas, this.fExternalNoNSSchema, locationPairs, this.fErrorReporter);
        final SchemaGrammar grammar = this.loadSchema(desc, source, locationPairs);
        if (grammar != null && this.fGrammarPool != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", this.fGrammarBucket.getGrammars());
            if (this.fIsCheckedFully && this.fJAXPCache.get(grammar) != grammar) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
            }
        }
        return grammar;
    }
    
    SchemaGrammar loadSchema(final XSDDescription desc, final XMLInputSource source, final Map<String, LocationArray> locationPairs) throws IOException, XNIException {
        if (!this.fJAXPProcessed) {
            this.processJAXPSchemaSource(locationPairs);
        }
        if (desc.isExternal()) {
            final String accessError = SecuritySupport.checkAccess(desc.getExpandedSystemId(), this.faccessExternalSchema, "all");
            if (accessError != null) {
                throw new XNIException(this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.access", new Object[] { SecuritySupport.sanitizePath(desc.getExpandedSystemId()), accessError }, (short)1));
            }
        }
        final SchemaGrammar grammar = this.fSchemaHandler.parseSchema(source, desc, locationPairs);
        return grammar;
    }
    
    public static XMLInputSource resolveDocument(final XSDDescription desc, final Map<String, LocationArray> locationPairs, final XMLEntityResolver entityResolver) throws IOException {
        String loc = null;
        if (desc.getContextType() == 2 || desc.fromInstance()) {
            final String namespace = desc.getTargetNamespace();
            final String ns = (namespace == null) ? XMLSymbols.EMPTY_STRING : namespace;
            final LocationArray tempLA = locationPairs.get(ns);
            if (tempLA != null) {
                loc = tempLA.getFirstLocation();
            }
        }
        if (loc == null) {
            final String[] hints = desc.getLocationHints();
            if (hints != null && hints.length > 0) {
                loc = hints[0];
            }
        }
        final String expandedLoc = XMLEntityManager.expandSystemId(loc, desc.getBaseSystemId(), false);
        desc.setLiteralSystemId(loc);
        desc.setExpandedSystemId(expandedLoc);
        return entityResolver.resolveEntity(desc);
    }
    
    public static void processExternalHints(final String sl, final String nsl, final Map<String, LocationArray> locations, final XMLErrorReporter er) {
        if (sl != null) {
            try {
                final XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
                attrDecl.fType.validate(sl, null, null);
                if (!tokenizeSchemaLocationStr(sl, locations)) {
                    er.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { sl }, (short)0);
                }
            }
            catch (final InvalidDatatypeValueException ex) {
                er.reportError("http://www.w3.org/TR/xml-schema-1", ex.getKey(), ex.getArgs(), (short)0);
            }
        }
        if (nsl != null) {
            try {
                final XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                attrDecl.fType.validate(nsl, null, null);
                LocationArray la = locations.get(XMLSymbols.EMPTY_STRING);
                if (la == null) {
                    la = new LocationArray();
                    locations.put(XMLSymbols.EMPTY_STRING, la);
                }
                la.addLocation(nsl);
            }
            catch (final InvalidDatatypeValueException ex) {
                er.reportError("http://www.w3.org/TR/xml-schema-1", ex.getKey(), ex.getArgs(), (short)0);
            }
        }
    }
    
    public static boolean tokenizeSchemaLocationStr(final String schemaStr, final Map<String, LocationArray> locations) {
        if (schemaStr != null) {
            final StringTokenizer t = new StringTokenizer(schemaStr, " \n\t\r");
            while (t.hasMoreTokens()) {
                final String namespace = t.nextToken();
                if (!t.hasMoreTokens()) {
                    return false;
                }
                final String location = t.nextToken();
                LocationArray la = locations.get(namespace);
                if (la == null) {
                    la = new LocationArray();
                    locations.put(namespace, la);
                }
                la.addLocation(location);
            }
        }
        return true;
    }
    
    private void processJAXPSchemaSource(final Map<String, LocationArray> locationPairs) throws IOException {
        this.fJAXPProcessed = true;
        if (this.fJAXPSource == null) {
            return;
        }
        final Class componentType = this.fJAXPSource.getClass().getComponentType();
        XMLInputSource xis = null;
        String sid = null;
        if (componentType == null) {
            if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
                final SchemaGrammar g = this.fJAXPCache.get(this.fJAXPSource);
                if (g != null) {
                    this.fGrammarBucket.putGrammar(g);
                    return;
                }
            }
            this.fXSDDescription.reset();
            xis = this.xsdToXMLInputSource(this.fJAXPSource);
            sid = xis.getSystemId();
            this.fXSDDescription.fContextType = 3;
            if (sid != null) {
                this.fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
                this.fXSDDescription.setLiteralSystemId(sid);
                this.fXSDDescription.setExpandedSystemId(sid);
                this.fXSDDescription.fLocationHints = new String[] { sid };
            }
            final SchemaGrammar g = this.loadSchema(this.fXSDDescription, xis, locationPairs);
            if (g != null) {
                if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
                    this.fJAXPCache.put(this.fJAXPSource, g);
                    if (this.fIsCheckedFully) {
                        XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
                    }
                }
                this.fGrammarBucket.putGrammar(g);
            }
            return;
        }
        if (componentType != Object.class && componentType != String.class && componentType != File.class && componentType != InputStream.class && componentType != InputSource.class) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have an array of type {" + componentType.getName() + "}. Possible types of the array supported are Object, String, File, InputStream, InputSource.");
        }
        final Object[] objArr = (Object[])this.fJAXPSource;
        final Vector jaxpSchemaSourceNamespaces = new Vector();
        for (int i = 0; i < objArr.length; ++i) {
            if (objArr[i] instanceof InputStream || objArr[i] instanceof InputSource) {
                final SchemaGrammar g2 = this.fJAXPCache.get(objArr[i]);
                if (g2 != null) {
                    this.fGrammarBucket.putGrammar(g2);
                    continue;
                }
            }
            this.fXSDDescription.reset();
            xis = this.xsdToXMLInputSource(objArr[i]);
            sid = xis.getSystemId();
            this.fXSDDescription.fContextType = 3;
            if (sid != null) {
                this.fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
                this.fXSDDescription.setLiteralSystemId(sid);
                this.fXSDDescription.setExpandedSystemId(sid);
                this.fXSDDescription.fLocationHints = new String[] { sid };
            }
            String targetNamespace = null;
            final SchemaGrammar grammar = this.fSchemaHandler.parseSchema(xis, this.fXSDDescription, locationPairs);
            if (this.fIsCheckedFully) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
            }
            if (grammar != null) {
                targetNamespace = grammar.getTargetNamespace();
                if (jaxpSchemaSourceNamespaces.contains(targetNamespace)) {
                    throw new IllegalArgumentException(" When using array of Objects as the value of SCHEMA_SOURCE property , no two Schemas should share the same targetNamespace. ");
                }
                jaxpSchemaSourceNamespaces.add(targetNamespace);
                if (objArr[i] instanceof InputStream || objArr[i] instanceof InputSource) {
                    this.fJAXPCache.put(objArr[i], grammar);
                }
                this.fGrammarBucket.putGrammar(grammar);
            }
        }
    }
    
    private XMLInputSource xsdToXMLInputSource(final Object val) {
        if (val instanceof String) {
            final String loc = (String)val;
            this.fXSDDescription.reset();
            this.fXSDDescription.setValues(null, loc, null, null);
            XMLInputSource xis = null;
            try {
                xis = this.fEntityManager.resolveEntity(this.fXSDDescription);
            }
            catch (final IOException ex) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { loc }, (short)1);
            }
            if (xis == null) {
                return new XMLInputSource(null, loc, null);
            }
            return xis;
        }
        else {
            if (val instanceof InputSource) {
                return saxToXMLInputSource((InputSource)val);
            }
            if (val instanceof InputStream) {
                return new XMLInputSource(null, null, null, (InputStream)val, null);
            }
            if (val instanceof File) {
                final File file = (File)val;
                InputStream is = null;
                try {
                    is = new BufferedInputStream(new FileInputStream(file));
                }
                catch (final FileNotFoundException ex2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { file.toString() }, (short)1);
                }
                return new XMLInputSource(null, null, null, is, null);
            }
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have a value of type {" + val.getClass().getName() + "}. Possible types of the value supported are String, File, InputStream, InputSource OR an array of these types.");
        }
    }
    
    private static XMLInputSource saxToXMLInputSource(final InputSource sis) {
        final String publicId = sis.getPublicId();
        final String systemId = sis.getSystemId();
        final Reader charStream = sis.getCharacterStream();
        if (charStream != null) {
            return new XMLInputSource(publicId, systemId, null, charStream, null);
        }
        final InputStream byteStream = sis.getByteStream();
        if (byteStream != null) {
            return new XMLInputSource(publicId, systemId, null, byteStream, sis.getEncoding());
        }
        return new XMLInputSource(publicId, systemId, null);
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        if (featureId.equals("http://apache.org/xml/features/validation/schema/augment-psvi")) {
            return Boolean.TRUE;
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        return null;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
        if (spm == null) {
            spm = new XMLSecurityPropertyManager();
            this.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", spm);
        }
        final XMLSecurityManager sm = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
        if (sm == null) {
            this.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
        }
        this.faccessExternalSchema = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
        this.fGrammarBucket.reset();
        this.fSubGroupHandler.reset();
        final boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
        if (!parser_settings || !this.fSettingsChanged) {
            this.fJAXPProcessed = false;
            this.initGrammarBucket();
            return;
        }
        this.fNodeFactory.reset(componentManager);
        this.fEntityManager = (XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        SchemaDVFactory dvFactory = null;
        dvFactory = this.fSchemaHandler.getDVFactory();
        if (dvFactory == null) {
            dvFactory = SchemaDVFactory.getInstance();
            this.fSchemaHandler.setDVFactory(dvFactory);
        }
        final boolean psvi = componentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", false);
        if (!psvi) {
            if (this.fDeclPool != null) {
                this.fDeclPool.reset();
            }
            else {
                this.fDeclPool = new XSDeclarationPool();
            }
            this.fCMBuilder.setDeclPool(this.fDeclPool);
            this.fSchemaHandler.setDeclPool(this.fDeclPool);
            if (dvFactory instanceof SchemaDVFactoryImpl) {
                this.fDeclPool.setDVFactory((SchemaDVFactoryImpl)dvFactory);
                ((SchemaDVFactoryImpl)dvFactory).setDeclPool(this.fDeclPool);
            }
        }
        else {
            this.fCMBuilder.setDeclPool(null);
            this.fSchemaHandler.setDeclPool(null);
        }
        try {
            this.fExternalSchemas = (String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
            this.fExternalNoNSSchema = (String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
        }
        catch (final XMLConfigurationException e) {
            this.fExternalSchemas = null;
            this.fExternalNoNSSchema = null;
        }
        this.fJAXPSource = componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
        this.fJAXPProcessed = false;
        this.fGrammarPool = (XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null);
        this.initGrammarBucket();
        try {
            final boolean fatalError = componentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            if (!fatalError) {
                this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", fatalError);
            }
        }
        catch (final XMLConfigurationException ex) {}
        this.fIsCheckedFully = componentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
        this.fSchemaHandler.setGenerateSyntheticAnnotations(componentManager.getFeature("http://apache.org/xml/features/generate-synthetic-annotations", false));
        this.fSchemaHandler.reset(componentManager);
    }
    
    private void initGrammarBucket() {
        if (this.fGrammarPool != null) {
            final Grammar[] initialGrammars = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
            for (int i = 0; i < initialGrammars.length; ++i) {
                if (!this.fGrammarBucket.putGrammar((SchemaGrammar)initialGrammars[i], true)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
                }
            }
        }
    }
    
    @Override
    public DOMConfiguration getConfig() {
        return this;
    }
    
    @Override
    public XSModel load(final LSInput is) {
        try {
            final Grammar g = this.loadGrammar(this.dom2xmlInputSource(is));
            return ((XSGrammar)g).toXSModel();
        }
        catch (final Exception e) {
            this.reportDOMFatalError(e);
            return null;
        }
    }
    
    @Override
    public XSModel loadInputList(final LSInputList is) {
        final int length = is.getLength();
        final SchemaGrammar[] gs = new SchemaGrammar[length];
        for (int i = 0; i < length; ++i) {
            try {
                gs[i] = (SchemaGrammar)this.loadGrammar(this.dom2xmlInputSource(is.item(i)));
            }
            catch (final Exception e) {
                this.reportDOMFatalError(e);
                return null;
            }
        }
        return new XSModelImpl(gs);
    }
    
    @Override
    public XSModel loadURI(final String uri) {
        try {
            final Grammar g = this.loadGrammar(new XMLInputSource(null, uri, null));
            return ((XSGrammar)g).toXSModel();
        }
        catch (final Exception e) {
            this.reportDOMFatalError(e);
            return null;
        }
    }
    
    @Override
    public XSModel loadURIList(final StringList uriList) {
        final int length = uriList.getLength();
        final SchemaGrammar[] gs = new SchemaGrammar[length];
        for (int i = 0; i < length; ++i) {
            try {
                gs[i] = (SchemaGrammar)this.loadGrammar(new XMLInputSource(null, uriList.item(i), null));
            }
            catch (final Exception e) {
                this.reportDOMFatalError(e);
                return null;
            }
        }
        return new XSModelImpl(gs);
    }
    
    void reportDOMFatalError(final Exception e) {
        if (this.fErrorHandler != null) {
            final DOMErrorImpl error = new DOMErrorImpl();
            error.fException = e;
            error.fMessage = e.getMessage();
            error.fSeverity = 3;
            this.fErrorHandler.getErrorHandler().handleError(error);
        }
    }
    
    @Override
    public boolean canSetParameter(final String name, final Object value) {
        if (value instanceof Boolean) {
            return name.equals("validate") || name.equals("http://apache.org/xml/features/validation/schema-full-checking") || name.equals("http://apache.org/xml/features/validate-annotations") || name.equals("http://apache.org/xml/features/continue-after-fatal-error") || name.equals("http://apache.org/xml/features/allow-java-encodings") || name.equals("http://apache.org/xml/features/standard-uri-conformant") || name.equals("http://apache.org/xml/features/generate-synthetic-annotations") || name.equals("http://apache.org/xml/features/honour-all-schemaLocations") || name.equals("http://apache.org/xml/features/namespace-growth") || name.equals("http://apache.org/xml/features/internal/tolerate-duplicates") || name.equals("jdk.xml.overrideDefaultParser");
        }
        return name.equals("error-handler") || name.equals("resource-resolver") || name.equals("http://apache.org/xml/properties/internal/symbol-table") || name.equals("http://apache.org/xml/properties/internal/error-reporter") || name.equals("http://apache.org/xml/properties/internal/error-handler") || name.equals("http://apache.org/xml/properties/internal/entity-resolver") || name.equals("http://apache.org/xml/properties/internal/grammar-pool") || name.equals("http://apache.org/xml/properties/schema/external-schemaLocation") || name.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation") || name.equals("http://java.sun.com/xml/jaxp/properties/schemaSource") || name.equals("http://apache.org/xml/properties/internal/validation/schema/dv-factory");
    }
    
    @Override
    public Object getParameter(final String name) throws DOMException {
        if (name.equals("error-handler")) {
            return (this.fErrorHandler != null) ? this.fErrorHandler.getErrorHandler() : null;
        }
        if (name.equals("resource-resolver")) {
            return (this.fResourceResolver != null) ? this.fResourceResolver.getEntityResolver() : null;
        }
        try {
            final boolean feature = this.getFeature(name);
            return feature ? Boolean.TRUE : Boolean.FALSE;
        }
        catch (final Exception e) {
            try {
                final Object property = this.getProperty(name);
                return property;
            }
            catch (final Exception ex) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                throw new DOMException((short)9, msg);
            }
        }
    }
    
    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final Vector v = new Vector();
            v.add("validate");
            v.add("error-handler");
            v.add("resource-resolver");
            v.add("http://apache.org/xml/properties/internal/symbol-table");
            v.add("http://apache.org/xml/properties/internal/error-reporter");
            v.add("http://apache.org/xml/properties/internal/error-handler");
            v.add("http://apache.org/xml/properties/internal/entity-resolver");
            v.add("http://apache.org/xml/properties/internal/grammar-pool");
            v.add("http://apache.org/xml/properties/schema/external-schemaLocation");
            v.add("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
            v.add("http://java.sun.com/xml/jaxp/properties/schemaSource");
            v.add("http://apache.org/xml/features/validation/schema-full-checking");
            v.add("http://apache.org/xml/features/continue-after-fatal-error");
            v.add("http://apache.org/xml/features/allow-java-encodings");
            v.add("http://apache.org/xml/features/standard-uri-conformant");
            v.add("http://apache.org/xml/features/validate-annotations");
            v.add("http://apache.org/xml/features/generate-synthetic-annotations");
            v.add("http://apache.org/xml/features/honour-all-schemaLocations");
            v.add("http://apache.org/xml/features/namespace-growth");
            v.add("http://apache.org/xml/features/internal/tolerate-duplicates");
            v.add("jdk.xml.overrideDefaultParser");
            this.fRecognizedParameters = new DOMStringListImpl(v);
        }
        return this.fRecognizedParameters;
    }
    
    @Override
    public void setParameter(final String name, final Object value) throws DOMException {
        if (value instanceof Boolean) {
            final boolean state = (boolean)value;
            if (name.equals("validate") && state) {
                return;
            }
            try {
                this.setFeature(name, state);
            }
            catch (final Exception e) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                throw new DOMException((short)9, msg);
            }
        }
        else if (name.equals("error-handler")) {
            if (value instanceof DOMErrorHandler) {
                try {
                    this.setErrorHandler(this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)value));
                }
                catch (final XMLConfigurationException ex2) {}
                return;
            }
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
            throw new DOMException((short)9, msg2);
        }
        else {
            if (!name.equals("resource-resolver")) {
                try {
                    this.setProperty(name, value);
                }
                catch (final Exception ex) {
                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                    throw new DOMException((short)9, msg3);
                }
                return;
            }
            if (value instanceof LSResourceResolver) {
                try {
                    this.setEntityResolver(this.fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver)value));
                }
                catch (final XMLConfigurationException ex3) {}
                return;
            }
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
            throw new DOMException((short)9, msg2);
        }
    }
    
    XMLInputSource dom2xmlInputSource(final LSInput is) {
        XMLInputSource xis = null;
        if (is.getCharacterStream() != null) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getCharacterStream(), "UTF-16");
        }
        else if (is.getByteStream() != null) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getByteStream(), is.getEncoding());
        }
        else if (is.getStringData() != null && is.getStringData().length() != 0) {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), new StringReader(is.getStringData()), "UTF-16");
        }
        else {
            xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI());
        }
        return xis;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser" };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
    }
    
    public static class LocationArray
    {
        int length;
        String[] locations;
        
        public LocationArray() {
            this.locations = new String[2];
        }
        
        public void resize(final int oldLength, final int newLength) {
            final String[] temp = new String[newLength];
            System.arraycopy(this.locations, 0, temp, 0, Math.min(oldLength, newLength));
            this.locations = temp;
            this.length = Math.min(oldLength, newLength);
        }
        
        public void addLocation(final String location) {
            if (this.length >= this.locations.length) {
                this.resize(this.length, Math.max(1, this.length * 2));
            }
            this.locations[this.length++] = location;
        }
        
        public String[] getLocationArray() {
            if (this.length < this.locations.length) {
                this.resize(this.locations.length, this.length);
            }
            return this.locations;
        }
        
        public String getFirstLocation() {
            return (this.length > 0) ? this.locations[0] : null;
        }
        
        public int getLength() {
            return this.length;
        }
    }
}
