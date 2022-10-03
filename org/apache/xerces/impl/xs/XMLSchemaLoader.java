package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.QName;
import java.io.StringReader;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.DOMErrorHandler;
import org.apache.xerces.dom.DOMStringListImpl;
import org.w3c.dom.DOMException;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.w3c.dom.DOMError;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.ls.LSInput;
import org.apache.xerces.impl.dv.xs.BaseSchemaDVFactory;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import org.xml.sax.InputSource;
import java.io.InputStream;
import org.apache.xerces.util.URI;
import java.util.StringTokenizer;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.util.XMLSymbols;
import java.util.Hashtable;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.XNIException;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.impl.xs.models.CMNodeFactory;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.DOMErrorHandlerWrapper;
import org.w3c.dom.DOMStringList;
import java.util.Locale;
import java.util.WeakHashMap;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.w3c.dom.DOMConfiguration;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;

public class XMLSchemaLoader implements XMLGrammarLoader, XMLComponent, XSElementDeclHelper, XSLoader, DOMConfiguration
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
    protected static final String CTA_FULL_XPATH = "http://apache.org/xml/features/validation/cta-full-xpath-checking";
    protected static final String ASSERT_COMMENT_PI = "http://apache.org/xml/features/validation/assert-comments-and-pi-checking";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
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
    protected static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    protected static final String DATATYPE_XML_VERSION = "http://apache.org/xml/properties/validation/schema/datatype-xml-version";
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl";
    private static final String SCHEMA11_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.Schema11DVFactoryImpl";
    private final ParserConfigurationSettings fLoaderConfig;
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
    private CMBuilder fCMBuilder;
    private XSDDescription fXSDDescription;
    private SchemaDVFactory fDefaultSchemaDVFactory;
    private WeakHashMap fJAXPCache;
    private Locale fLocale;
    private DOMStringList fRecognizedParameters;
    private DOMErrorHandlerWrapper fErrorHandler;
    private DOMEntityResolverWrapper fResourceResolver;
    private short fSchemaVersion;
    private XSConstraints fXSConstraints;
    
    public XMLSchemaLoader() {
        this(new SymbolTable(), null, new XMLEntityManager(), null, null, null);
    }
    
    public XMLSchemaLoader(final SymbolTable symbolTable) {
        this(symbolTable, null, new XMLEntityManager(), null, null, null);
    }
    
    XMLSchemaLoader(final XMLErrorReporter xmlErrorReporter, final XSGrammarBucket xsGrammarBucket, final SubstitutionGroupHandler substitutionGroupHandler, final CMBuilder cmBuilder) {
        this(null, xmlErrorReporter, null, xsGrammarBucket, substitutionGroupHandler, cmBuilder);
    }
    
    XMLSchemaLoader(final SymbolTable symbolTable, XMLErrorReporter fErrorReporter, final XMLEntityManager fEntityManager, XSGrammarBucket fGrammarBucket, SubstitutionGroupHandler fSubGroupHandler, CMBuilder fcmBuilder) {
        this.fLoaderConfig = new ParserConfigurationSettings();
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
        this.fXSDDescription = new XSDDescription();
        this.fLocale = Locale.getDefault();
        this.fRecognizedParameters = null;
        this.fErrorHandler = null;
        this.fResourceResolver = null;
        this.fSchemaVersion = 1;
        this.fXSConstraints = XSConstraints.XS_1_0_CONSTRAINTS;
        this.fLoaderConfig.addRecognizedFeatures(XMLSchemaLoader.RECOGNIZED_FEATURES);
        this.fLoaderConfig.addRecognizedProperties(XMLSchemaLoader.RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        }
        if (fErrorReporter == null) {
            fErrorReporter = new XMLErrorReporter();
            fErrorReporter.setLocale(this.fLocale);
            fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
        }
        this.fErrorReporter = fErrorReporter;
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.fEntityManager = fEntityManager;
        if (this.fEntityManager != null) {
            this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        }
        this.fLoaderConfig.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
        if (fGrammarBucket == null) {
            fGrammarBucket = new XSGrammarBucket();
        }
        this.fGrammarBucket = fGrammarBucket;
        if (fSubGroupHandler == null) {
            fSubGroupHandler = new SubstitutionGroupHandler(this);
        }
        this.fSubGroupHandler = fSubGroupHandler;
        final CMNodeFactory cmNodeFactory = new CMNodeFactory();
        if (fcmBuilder == null) {
            fcmBuilder = new CMBuilder(cmNodeFactory);
        }
        this.fCMBuilder = fcmBuilder;
        this.fSchemaHandler = new XSDHandler(this.fGrammarBucket, this.fSchemaVersion, this.fXSConstraints);
        this.fJAXPCache = new WeakHashMap();
        this.fSettingsChanged = true;
    }
    
    public String[] getRecognizedFeatures() {
        return XMLSchemaLoader.RECOGNIZED_FEATURES.clone();
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        return this.fLoaderConfig.getFeature(s);
    }
    
    public void setFeature(final String s, final boolean generateSyntheticAnnotations) throws XMLConfigurationException {
        this.fSettingsChanged = true;
        if (s.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
            this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", generateSyntheticAnnotations);
        }
        else if (s.equals("http://apache.org/xml/features/generate-synthetic-annotations")) {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(generateSyntheticAnnotations);
        }
        this.fLoaderConfig.setFeature(s, generateSyntheticAnnotations);
    }
    
    public String[] getRecognizedProperties() {
        return XMLSchemaLoader.RECOGNIZED_PROPERTIES.clone();
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        return this.fLoaderConfig.getProperty(s);
    }
    
    public void setProperty(final String s, final Object fjaxpSource) throws XMLConfigurationException {
        this.fSettingsChanged = true;
        this.fLoaderConfig.setProperty(s, fjaxpSource);
        if (s.equals("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
            this.fJAXPSource = fjaxpSource;
            this.fJAXPProcessed = false;
        }
        else if (s.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            this.fGrammarPool = (XMLGrammarPool)fjaxpSource;
        }
        else if (s.equals("http://apache.org/xml/properties/schema/external-schemaLocation")) {
            this.fExternalSchemas = (String)fjaxpSource;
        }
        else if (s.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation")) {
            this.fExternalNoNSSchema = (String)fjaxpSource;
        }
        else if (s.equals("http://apache.org/xml/properties/locale")) {
            this.setLocale((Locale)fjaxpSource);
        }
        else if (s.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fjaxpSource);
        }
        else if (s.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.fErrorReporter = (XMLErrorReporter)fjaxpSource;
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
            }
        }
        else if (s.equals("http://apache.org/xml/properties/validation/schema/version")) {
            this.setSchemaVersion((String)fjaxpSource);
        }
    }
    
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    public void setErrorHandler(final XMLErrorHandler xmlErrorHandler) {
        this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", xmlErrorHandler);
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }
    
    public void setEntityResolver(final XMLEntityResolver fUserEntityResolver) {
        this.fUserEntityResolver = fUserEntityResolver;
        this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fUserEntityResolver);
        this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fUserEntityResolver);
    }
    
    public XMLEntityResolver getEntityResolver() {
        return this.fUserEntityResolver;
    }
    
    private void setSchemaVersion(final String s) {
        if (s.equals(Constants.W3C_XML_SCHEMA10_NS_URI)) {
            this.fSchemaVersion = 1;
            this.fXSConstraints = XSConstraints.XS_1_0_CONSTRAINTS;
        }
        else if (s.equals(Constants.W3C_XML_SCHEMA11_NS_URI)) {
            this.fSchemaVersion = 4;
            this.fXSConstraints = XSConstraints.XS_1_1_CONSTRAINTS;
        }
        else {
            this.fSchemaVersion = 2;
            this.fXSConstraints = XSConstraints.XS_1_0_CONSTRAINTS_EXTENDED;
        }
        this.fSchemaHandler.setSchemaVersionInfo(this.fSchemaVersion, this.fXSConstraints);
        this.fCMBuilder.setSchemaVersion(this.fSchemaVersion);
    }
    
    short getSchemaVersion() {
        return this.fSchemaVersion;
    }
    
    XSConstraints getXSConstraints() {
        return this.fXSConstraints;
    }
    
    public void loadGrammar(final XMLInputSource[] array) throws IOException, XNIException {
        for (int length = array.length, i = 0; i < length; ++i) {
            this.loadGrammar(array[i]);
        }
    }
    
    public Grammar loadGrammar(final XMLInputSource xmlInputSource) throws IOException, XNIException {
        this.reset(this.fLoaderConfig);
        this.fSettingsChanged = false;
        final XSDDescription xsdDescription = new XSDDescription();
        xsdDescription.fContextType = 3;
        xsdDescription.setBaseSystemId(xmlInputSource.getBaseSystemId());
        xsdDescription.setLiteralSystemId(xmlInputSource.getSystemId());
        final Hashtable hashtable = new Hashtable();
        processExternalHints(this.fExternalSchemas, this.fExternalNoNSSchema, hashtable, this.fErrorReporter);
        final SchemaGrammar loadSchema = this.loadSchema(xsdDescription, xmlInputSource, hashtable);
        if (loadSchema != null && this.fGrammarPool != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", this.fGrammarBucket.getGrammars());
            if (this.fIsCheckedFully && this.fJAXPCache.get(loadSchema) != loadSchema) {
                this.fXSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
            }
        }
        return loadSchema;
    }
    
    SchemaGrammar loadSchema(final XSDDescription xsdDescription, final XMLInputSource xmlInputSource, final Hashtable hashtable) throws IOException, XNIException {
        if (!this.fJAXPProcessed) {
            this.processJAXPSchemaSource(hashtable);
        }
        return this.fSchemaHandler.parseSchema(xmlInputSource, xsdDescription, hashtable);
    }
    
    public static XMLInputSource resolveDocument(final XSDDescription xsdDescription, final Hashtable hashtable, final XMLEntityResolver xmlEntityResolver) throws IOException {
        String firstLocation = null;
        if (xsdDescription.getContextType() == 2 || xsdDescription.fromInstance()) {
            final String targetNamespace = xsdDescription.getTargetNamespace();
            final LocationArray locationArray = hashtable.get((targetNamespace == null) ? XMLSymbols.EMPTY_STRING : targetNamespace);
            if (locationArray != null) {
                firstLocation = locationArray.getFirstLocation();
            }
        }
        if (firstLocation == null) {
            final String[] locationHints = xsdDescription.getLocationHints();
            if (locationHints != null && locationHints.length > 0) {
                firstLocation = locationHints[0];
            }
        }
        final String expandSystemId = XMLEntityManager.expandSystemId(firstLocation, xsdDescription.getBaseSystemId(), false);
        xsdDescription.setLiteralSystemId(firstLocation);
        xsdDescription.setExpandedSystemId(expandSystemId);
        return xmlEntityResolver.resolveEntity(xsdDescription);
    }
    
    public static void processExternalHints(final String s, final String s2, final Hashtable hashtable, final XMLErrorReporter xmlErrorReporter) {
        if (s != null) {
            try {
                SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION).fType.validate(s, null, null);
                if (!tokenizeSchemaLocationStr(s, hashtable, null)) {
                    xmlErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { s }, (short)0);
                }
            }
            catch (final InvalidDatatypeValueException ex) {
                xmlErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", ex.getKey(), ex.getArgs(), (short)0);
            }
        }
        if (s2 != null) {
            try {
                SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION).fType.validate(s2, null, null);
                LocationArray locationArray = hashtable.get(XMLSymbols.EMPTY_STRING);
                if (locationArray == null) {
                    locationArray = new LocationArray();
                    hashtable.put(XMLSymbols.EMPTY_STRING, locationArray);
                }
                locationArray.addLocation(s2);
            }
            catch (final InvalidDatatypeValueException ex2) {
                xmlErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", ex2.getKey(), ex2.getArgs(), (short)0);
            }
        }
    }
    
    public static boolean tokenizeSchemaLocationStr(final String s, final Hashtable hashtable, final String s2) {
        if (s != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                if (!stringTokenizer.hasMoreTokens()) {
                    return false;
                }
                String s3 = stringTokenizer.nextToken();
                LocationArray locationArray = hashtable.get(nextToken);
                if (locationArray == null) {
                    locationArray = new LocationArray();
                    hashtable.put(nextToken, locationArray);
                }
                if (s2 != null) {
                    try {
                        s3 = XMLEntityManager.expandSystemId(s3, s2, false);
                    }
                    catch (final URI.MalformedURIException ex) {}
                }
                locationArray.addLocation(s3);
            }
        }
        return true;
    }
    
    private void processJAXPSchemaSource(final Hashtable hashtable) throws IOException {
        this.fJAXPProcessed = true;
        if (this.fJAXPSource == null) {
            return;
        }
        final Class<?> componentType = this.fJAXPSource.getClass().getComponentType();
        if (componentType == null) {
            if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
                final SchemaGrammar schemaGrammar = this.fJAXPCache.get(this.fJAXPSource);
                if (schemaGrammar != null) {
                    this.fGrammarBucket.putGrammar(schemaGrammar);
                    return;
                }
            }
            this.fXSDDescription.reset();
            final XMLInputSource xsdToXMLInputSource = this.xsdToXMLInputSource(this.fJAXPSource);
            final String systemId = xsdToXMLInputSource.getSystemId();
            this.fXSDDescription.fContextType = 3;
            if (systemId != null) {
                this.fXSDDescription.setBaseSystemId(xsdToXMLInputSource.getBaseSystemId());
                this.fXSDDescription.setLiteralSystemId(systemId);
                this.fXSDDescription.setExpandedSystemId(systemId);
                this.fXSDDescription.fLocationHints = new String[] { systemId };
            }
            final SchemaGrammar loadSchema = this.loadSchema(this.fXSDDescription, xsdToXMLInputSource, hashtable);
            if (loadSchema != null) {
                if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
                    this.fJAXPCache.put(this.fJAXPSource, loadSchema);
                    if (this.fIsCheckedFully) {
                        this.fXSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
                    }
                }
                this.fGrammarBucket.putGrammar(loadSchema);
            }
            return;
        }
        if (componentType != Object.class && componentType != String.class && componentType != File.class && componentType != InputStream.class && componentType != InputSource.class && !File.class.isAssignableFrom(componentType) && !InputStream.class.isAssignableFrom(componentType) && !InputSource.class.isAssignableFrom(componentType) && !componentType.isInterface()) {
            throw new XMLConfigurationException((short)1, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1").formatMessage(this.fErrorReporter.getLocale(), "jaxp12-schema-source-type.2", new Object[] { componentType.getName() }));
        }
        final Object[] array = (Object[])this.fJAXPSource;
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof InputStream || array[i] instanceof InputSource) {
                final SchemaGrammar schemaGrammar2 = this.fJAXPCache.get(array[i]);
                if (schemaGrammar2 != null) {
                    this.fGrammarBucket.putGrammar(schemaGrammar2);
                    continue;
                }
            }
            this.fXSDDescription.reset();
            final XMLInputSource xsdToXMLInputSource2 = this.xsdToXMLInputSource(array[i]);
            final String systemId2 = xsdToXMLInputSource2.getSystemId();
            this.fXSDDescription.fContextType = 3;
            if (systemId2 != null) {
                this.fXSDDescription.setBaseSystemId(xsdToXMLInputSource2.getBaseSystemId());
                this.fXSDDescription.setLiteralSystemId(systemId2);
                this.fXSDDescription.setExpandedSystemId(systemId2);
                this.fXSDDescription.fLocationHints = new String[] { systemId2 };
            }
            final SchemaGrammar schema = this.fSchemaHandler.parseSchema(xsdToXMLInputSource2, this.fXSDDescription, hashtable);
            if (this.fIsCheckedFully) {
                this.fXSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
            }
            if (schema != null) {
                final String targetNamespace = schema.getTargetNamespace();
                if (list.contains(targetNamespace)) {
                    throw new IllegalArgumentException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1").formatMessage(this.fErrorReporter.getLocale(), "jaxp12-schema-source-ns", null));
                }
                list.add(targetNamespace);
                if (array[i] instanceof InputStream || array[i] instanceof InputSource) {
                    this.fJAXPCache.put(array[i], schema);
                }
                this.fGrammarBucket.putGrammar(schema);
            }
        }
    }
    
    private XMLInputSource xsdToXMLInputSource(final Object o) {
        if (o instanceof String) {
            final String s = (String)o;
            this.fXSDDescription.reset();
            this.fXSDDescription.setValues(null, s, null, null);
            XMLInputSource resolveEntity = null;
            try {
                resolveEntity = this.fEntityManager.resolveEntity(this.fXSDDescription);
            }
            catch (final IOException ex) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { s }, (short)1);
            }
            if (resolveEntity == null) {
                return new XMLInputSource(null, s, null);
            }
            return resolveEntity;
        }
        else {
            if (o instanceof InputSource) {
                return saxToXMLInputSource((InputSource)o);
            }
            if (o instanceof InputStream) {
                return new XMLInputSource(null, null, null, (InputStream)o, null);
            }
            if (o instanceof File) {
                final File file = (File)o;
                final String filepath2URI = FilePathToURI.filepath2URI(file.getAbsolutePath());
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(file));
                }
                catch (final FileNotFoundException ex2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { file.toString() }, (short)1);
                }
                return new XMLInputSource(null, filepath2URI, null, inputStream, null);
            }
            throw new XMLConfigurationException((short)1, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1").formatMessage(this.fErrorReporter.getLocale(), "jaxp12-schema-source-type.1", new Object[] { (o != null) ? o.getClass().getName() : "null" }));
        }
    }
    
    private static XMLInputSource saxToXMLInputSource(final InputSource inputSource) {
        final String publicId = inputSource.getPublicId();
        final String systemId = inputSource.getSystemId();
        final Reader characterStream = inputSource.getCharacterStream();
        if (characterStream != null) {
            return new XMLInputSource(publicId, systemId, null, characterStream, null);
        }
        final InputStream byteStream = inputSource.getByteStream();
        if (byteStream != null) {
            return new XMLInputSource(publicId, systemId, null, byteStream, inputSource.getEncoding());
        }
        return new XMLInputSource(publicId, systemId, null);
    }
    
    public Boolean getFeatureDefault(final String s) {
        if (s.equals("http://apache.org/xml/features/validation/schema/augment-psvi")) {
            return Boolean.TRUE;
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        return null;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        this.fGrammarBucket.reset();
        this.fSubGroupHandler.reset();
        if (!this.fSettingsChanged || !this.parserSettingsUpdated(xmlComponentManager)) {
            this.fJAXPProcessed = false;
            this.initGrammarBucket();
            if (this.fDeclPool != null) {
                this.fDeclPool.reset();
            }
            return;
        }
        this.fEntityManager = (XMLEntityManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        SchemaDVFactory fDefaultSchemaDVFactory = null;
        try {
            fDefaultSchemaDVFactory = (SchemaDVFactory)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validation/schema/dv-factory");
        }
        catch (final XMLConfigurationException ex) {}
        if (fDefaultSchemaDVFactory == null) {
            if (this.fDefaultSchemaDVFactory == null) {
                this.fDefaultSchemaDVFactory = this.getSchemaDVFactory(this.fSchemaVersion);
            }
            fDefaultSchemaDVFactory = this.fDefaultSchemaDVFactory;
        }
        this.fSchemaHandler.setDVFactory(fDefaultSchemaDVFactory);
        try {
            this.fExternalSchemas = (String)xmlComponentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation");
            this.fExternalNoNSSchema = (String)xmlComponentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
        }
        catch (final XMLConfigurationException ex2) {
            this.fExternalSchemas = null;
            this.fExternalNoNSSchema = null;
        }
        try {
            this.fJAXPSource = xmlComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
            this.fJAXPProcessed = false;
        }
        catch (final XMLConfigurationException ex3) {
            this.fJAXPSource = null;
            this.fJAXPProcessed = false;
        }
        try {
            this.fGrammarPool = (XMLGrammarPool)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        }
        catch (final XMLConfigurationException ex4) {
            this.fGrammarPool = null;
        }
        this.initGrammarBucket();
        boolean feature;
        try {
            feature = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi");
        }
        catch (final XMLConfigurationException ex5) {
            feature = false;
        }
        if (feature || this.fGrammarPool == null) {}
        this.fCMBuilder.setDeclPool(null);
        this.fSchemaHandler.setDeclPool(null);
        if (fDefaultSchemaDVFactory instanceof BaseSchemaDVFactory) {
            ((BaseSchemaDVFactory)fDefaultSchemaDVFactory).setDeclPool(null);
        }
        try {
            this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", xmlComponentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error"));
        }
        catch (final XMLConfigurationException ex6) {}
        try {
            this.fIsCheckedFully = xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking");
        }
        catch (final XMLConfigurationException ex7) {
            this.fIsCheckedFully = false;
        }
        try {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(xmlComponentManager.getFeature("http://apache.org/xml/features/generate-synthetic-annotations"));
        }
        catch (final XMLConfigurationException ex8) {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(false);
        }
        this.fSchemaHandler.reset(xmlComponentManager);
    }
    
    private SchemaDVFactory getSchemaDVFactory(final short n) {
        if (n == 1) {
            return SchemaDVFactory.getInstance();
        }
        if (n == 4) {
            return SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.Schema11DVFactoryImpl");
        }
        return SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl");
    }
    
    private boolean parserSettingsUpdated(final XMLComponentManager xmlComponentManager) {
        if (xmlComponentManager != this.fLoaderConfig) {
            try {
                return xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings");
            }
            catch (final XMLConfigurationException ex) {}
        }
        return true;
    }
    
    private void initGrammarBucket() {
        if (this.fGrammarPool != null) {
            final Grammar[] retrieveInitialGrammarSet = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
            for (int n = (retrieveInitialGrammarSet != null) ? retrieveInitialGrammarSet.length : 0, i = 0; i < n; ++i) {
                if (!this.fGrammarBucket.putGrammar((SchemaGrammar)retrieveInitialGrammarSet[i], true)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
                }
            }
        }
    }
    
    public DOMConfiguration getConfig() {
        return this;
    }
    
    public XSModel load(final LSInput lsInput) {
        try {
            return ((XSGrammar)this.loadGrammar(this.dom2xmlInputSource(lsInput))).toXSModel();
        }
        catch (final Exception ex) {
            this.reportDOMFatalError(ex);
            return null;
        }
    }
    
    public XSModel loadInputList(final LSInputList list) {
        final int length = list.getLength();
        final SchemaGrammar[] array = new SchemaGrammar[length];
        for (int i = 0; i < length; ++i) {
            try {
                array[i] = (SchemaGrammar)this.loadGrammar(this.dom2xmlInputSource(list.item(i)));
            }
            catch (final Exception ex) {
                this.reportDOMFatalError(ex);
                return null;
            }
        }
        return new XSModelImpl(array);
    }
    
    public XSModel loadURI(final String s) {
        try {
            return ((XSGrammar)this.loadGrammar(new XMLInputSource(null, s, null))).toXSModel();
        }
        catch (final Exception ex) {
            this.reportDOMFatalError(ex);
            return null;
        }
    }
    
    public XSModel loadURIList(final StringList list) {
        final int length = list.getLength();
        final SchemaGrammar[] array = new SchemaGrammar[length];
        for (int i = 0; i < length; ++i) {
            try {
                array[i] = (SchemaGrammar)this.loadGrammar(new XMLInputSource(null, list.item(i), null));
            }
            catch (final Exception ex) {
                this.reportDOMFatalError(ex);
                return null;
            }
        }
        return new XSModelImpl(array);
    }
    
    void reportDOMFatalError(final Exception fException) {
        if (this.fErrorHandler != null) {
            final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
            domErrorImpl.fException = fException;
            domErrorImpl.fMessage = fException.getMessage();
            domErrorImpl.fSeverity = 3;
            this.fErrorHandler.getErrorHandler().handleError(domErrorImpl);
        }
    }
    
    public boolean canSetParameter(final String s, final Object o) {
        if (o instanceof Boolean) {
            return s.equals("validate") || s.equals("http://apache.org/xml/features/validation/schema-full-checking") || s.equals("http://apache.org/xml/features/validate-annotations") || s.equals("http://apache.org/xml/features/continue-after-fatal-error") || s.equals("http://apache.org/xml/features/allow-java-encodings") || s.equals("http://apache.org/xml/features/standard-uri-conformant") || s.equals("http://apache.org/xml/features/generate-synthetic-annotations") || s.equals("http://apache.org/xml/features/honour-all-schemaLocations") || s.equals("http://apache.org/xml/features/namespace-growth") || s.equals("http://apache.org/xml/features/internal/tolerate-duplicates");
        }
        return s.equals("error-handler") || s.equals("resource-resolver") || s.equals("http://apache.org/xml/properties/internal/symbol-table") || s.equals("http://apache.org/xml/properties/internal/error-reporter") || s.equals("http://apache.org/xml/properties/internal/error-handler") || s.equals("http://apache.org/xml/properties/internal/entity-resolver") || s.equals("http://apache.org/xml/properties/internal/grammar-pool") || s.equals("http://apache.org/xml/properties/schema/external-schemaLocation") || s.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation") || s.equals("http://java.sun.com/xml/jaxp/properties/schemaSource") || s.equals("http://apache.org/xml/properties/internal/validation/schema/dv-factory");
    }
    
    public Object getParameter(final String s) throws DOMException {
        if (s.equals("error-handler")) {
            return (this.fErrorHandler != null) ? this.fErrorHandler.getErrorHandler() : null;
        }
        if (s.equals("resource-resolver")) {
            return (this.fResourceResolver != null) ? this.fResourceResolver.getEntityResolver() : null;
        }
        try {
            return this.getFeature(s) ? Boolean.TRUE : Boolean.FALSE;
        }
        catch (final Exception ex) {
            try {
                return this.getProperty(s);
            }
            catch (final Exception ex2) {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
            }
        }
    }
    
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final ArrayList list = new ArrayList();
            list.add("validate");
            list.add("error-handler");
            list.add("resource-resolver");
            list.add("http://apache.org/xml/properties/internal/symbol-table");
            list.add("http://apache.org/xml/properties/internal/error-reporter");
            list.add("http://apache.org/xml/properties/internal/error-handler");
            list.add("http://apache.org/xml/properties/internal/entity-resolver");
            list.add("http://apache.org/xml/properties/internal/grammar-pool");
            list.add("http://apache.org/xml/properties/schema/external-schemaLocation");
            list.add("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
            list.add("http://java.sun.com/xml/jaxp/properties/schemaSource");
            list.add("http://apache.org/xml/features/validation/schema-full-checking");
            list.add("http://apache.org/xml/features/continue-after-fatal-error");
            list.add("http://apache.org/xml/features/allow-java-encodings");
            list.add("http://apache.org/xml/features/standard-uri-conformant");
            list.add("http://apache.org/xml/features/validate-annotations");
            list.add("http://apache.org/xml/features/generate-synthetic-annotations");
            list.add("http://apache.org/xml/features/honour-all-schemaLocations");
            list.add("http://apache.org/xml/features/namespace-growth");
            list.add("http://apache.org/xml/features/internal/tolerate-duplicates");
            this.fRecognizedParameters = new DOMStringListImpl(list);
        }
        return this.fRecognizedParameters;
    }
    
    public void setParameter(final String s, final Object o) throws DOMException {
        if (o instanceof Boolean) {
            final boolean booleanValue = (boolean)o;
            if (s.equals("validate") && booleanValue) {
                return;
            }
            try {
                this.setFeature(s, booleanValue);
            }
            catch (final Exception ex) {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
            }
        }
        else if (s.equals("error-handler")) {
            if (o instanceof DOMErrorHandler) {
                try {
                    this.setErrorHandler(this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)o));
                }
                catch (final XMLConfigurationException ex2) {}
                return;
            }
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
        }
        else {
            if (!s.equals("resource-resolver")) {
                try {
                    this.setProperty(s, o);
                }
                catch (final Exception ex3) {
                    throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
                }
                return;
            }
            if (o instanceof LSResourceResolver) {
                try {
                    this.setEntityResolver(this.fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver)o));
                }
                catch (final XMLConfigurationException ex4) {}
                return;
            }
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
        }
    }
    
    XMLInputSource dom2xmlInputSource(final LSInput lsInput) {
        XMLInputSource xmlInputSource;
        if (lsInput.getCharacterStream() != null) {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI(), lsInput.getCharacterStream(), "UTF-16");
        }
        else if (lsInput.getByteStream() != null) {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI(), lsInput.getByteStream(), lsInput.getEncoding());
        }
        else if (lsInput.getStringData() != null && lsInput.getStringData().length() != 0) {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI(), new StringReader(lsInput.getStringData()), "UTF-16");
        }
        else {
            xmlInputSource = new XMLInputSource(lsInput.getPublicId(), lsInput.getSystemId(), lsInput.getBaseURI());
        }
        return xmlInputSource;
    }
    
    public XSElementDecl getGlobalElementDecl(final QName qName) {
        final SchemaGrammar grammar = this.fGrammarBucket.getGrammar(qName.uri);
        if (grammar != null) {
            return grammar.getGlobalElementDecl(qName.localpart);
        }
        return null;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/validation/cta-full-xpath-checking", "http://apache.org/xml/features/validation/assert-comments-and-pi-checking" };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://apache.org/xml/properties/validation/schema/version", "http://apache.org/xml/properties/validation/schema/datatype-xml-version" };
    }
    
    static class LocationArray
    {
        int length;
        String[] locations;
        
        LocationArray() {
            this.locations = new String[2];
        }
        
        public void resize(final int n, final int n2) {
            final String[] locations = new String[n2];
            System.arraycopy(this.locations, 0, locations, 0, Math.min(n, n2));
            this.locations = locations;
            this.length = Math.min(n, n2);
        }
        
        public void addLocation(final String s) {
            if (this.length >= this.locations.length) {
                this.resize(this.length, Math.max(1, this.length * 2));
            }
            this.locations[this.length++] = s;
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
