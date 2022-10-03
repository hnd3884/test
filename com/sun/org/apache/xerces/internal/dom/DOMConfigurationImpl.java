package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.PropertyState;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import jdk.xml.internal.JdkXmlUtils;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import org.w3c.dom.DOMStringList;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import org.w3c.dom.DOMConfiguration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;

public class DOMConfigurationImpl extends ParserConfigurationSettings implements XMLParserConfiguration, DOMConfiguration
{
    protected static final String XERCES_VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String XERCES_NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String SCHEMA = "http://apache.org/xml/features/validation/schema";
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SEND_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String DTD_VALIDATOR_FACTORY_PROPERTY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    XMLDocumentHandler fDocumentHandler;
    protected short features;
    protected static final short NAMESPACES = 1;
    protected static final short DTNORMALIZATION = 2;
    protected static final short ENTITIES = 4;
    protected static final short CDATA = 8;
    protected static final short SPLITCDATA = 16;
    protected static final short COMMENTS = 32;
    protected static final short VALIDATE = 64;
    protected static final short PSVI = 128;
    protected static final short WELLFORMED = 256;
    protected static final short NSDECL = 512;
    protected static final short INFOSET_TRUE_PARAMS = 801;
    protected static final short INFOSET_FALSE_PARAMS = 14;
    protected static final short INFOSET_MASK = 815;
    protected SymbolTable fSymbolTable;
    protected ArrayList fComponents;
    protected ValidationManager fValidationManager;
    protected Locale fLocale;
    protected XMLErrorReporter fErrorReporter;
    protected final DOMErrorHandlerWrapper fErrorHandlerWrapper;
    private DOMStringList fRecognizedParameters;
    
    protected DOMConfigurationImpl() {
        this(null, null);
    }
    
    protected DOMConfigurationImpl(final SymbolTable symbolTable) {
        this(symbolTable, null);
    }
    
    protected DOMConfigurationImpl(SymbolTable symbolTable, final XMLComponentManager parentSettings) {
        super(parentSettings);
        this.features = 0;
        this.fErrorHandlerWrapper = new DOMErrorHandlerWrapper();
        this.fFeatures = new HashMap<String, Boolean>();
        this.fProperties = new HashMap<String, Object>();
        final String[] recognizedFeatures = { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "jdk.xml.overrideDefaultParser" };
        this.addRecognizedFeatures(recognizedFeatures);
        this.setFeature("http://xml.org/sax/features/validation", false);
        this.setFeature("http://apache.org/xml/features/validation/schema", false);
        this.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
        this.setFeature("http://apache.org/xml/features/validation/dynamic", false);
        this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
        this.setFeature("http://xml.org/sax/features/namespaces", true);
        this.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
        this.setFeature("http://apache.org/xml/features/namespace-growth", false);
        this.setFeature("jdk.xml.overrideDefaultParser", JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
        final String[] recognizedProperties = { "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
        this.addRecognizedProperties(recognizedProperties);
        this.features |= 0x1;
        this.features |= 0x4;
        this.features |= 0x20;
        this.features |= 0x8;
        this.features |= 0x10;
        this.features |= 0x100;
        this.features |= 0x200;
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        this.fSymbolTable = symbolTable;
        this.fComponents = new ArrayList();
        this.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
        this.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter = new XMLErrorReporter());
        this.addComponent(this.fErrorReporter);
        this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", DTDDVFactory.getInstance());
        final XMLEntityManager manager = new XMLEntityManager();
        this.setProperty("http://apache.org/xml/properties/internal/entity-manager", manager);
        this.addComponent(manager);
        this.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager = this.createValidationManager());
        this.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
        this.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", new XMLSecurityPropertyManager());
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmft = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            MessageFormatter xmft2 = null;
            try {
                xmft2 = (MessageFormatter)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter", true);
            }
            catch (final Exception ex) {}
            if (xmft2 != null) {
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft2);
            }
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex2) {}
    }
    
    @Override
    public void parse(final XMLInputSource inputSource) throws XNIException, IOException {
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void setDTDHandler(final XMLDTDHandler dtdHandler) {
    }
    
    @Override
    public XMLDTDHandler getDTDHandler() {
        return null;
    }
    
    @Override
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler handler) {
    }
    
    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return null;
    }
    
    @Override
    public void setEntityResolver(final XMLEntityResolver resolver) {
        if (resolver != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
        }
    }
    
    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
    }
    
    @Override
    public void setErrorHandler(final XMLErrorHandler errorHandler) {
        if (errorHandler != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
        }
    }
    
    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        super.setFeature(featureId, state);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        super.setProperty(propertyId, value);
    }
    
    @Override
    public void setLocale(final Locale locale) throws XNIException {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
    
    @Override
    public void setParameter(final String name, final Object value) throws DOMException {
        boolean found = true;
        if (value instanceof Boolean) {
            final boolean state = (boolean)value;
            if (name.equalsIgnoreCase("comments")) {
                this.features = (short)(state ? (this.features | 0x20) : (this.features & 0xFFFFFFDF));
            }
            else if (name.equalsIgnoreCase("datatype-normalization")) {
                this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", state);
                this.features = (short)(state ? (this.features | 0x2) : (this.features & 0xFFFFFFFD));
                if (state) {
                    this.features |= 0x40;
                }
            }
            else if (name.equalsIgnoreCase("namespaces")) {
                this.features = (short)(state ? (this.features | 0x1) : (this.features & 0xFFFFFFFE));
            }
            else if (name.equalsIgnoreCase("cdata-sections")) {
                this.features = (short)(state ? (this.features | 0x8) : (this.features & 0xFFFFFFF7));
            }
            else if (name.equalsIgnoreCase("entities")) {
                this.features = (short)(state ? (this.features | 0x4) : (this.features & 0xFFFFFFFB));
            }
            else if (name.equalsIgnoreCase("split-cdata-sections")) {
                this.features = (short)(state ? (this.features | 0x10) : (this.features & 0xFFFFFFEF));
            }
            else if (name.equalsIgnoreCase("validate")) {
                this.features = (short)(state ? (this.features | 0x40) : (this.features & 0xFFFFFFBF));
            }
            else if (name.equalsIgnoreCase("well-formed")) {
                this.features = (short)(state ? (this.features | 0x100) : (this.features & 0xFFFFFEFF));
            }
            else if (name.equalsIgnoreCase("namespace-declarations")) {
                this.features = (short)(state ? (this.features | 0x200) : (this.features & 0xFFFFFDFF));
            }
            else if (name.equalsIgnoreCase("infoset")) {
                if (state) {
                    this.features |= 0x321;
                    this.features &= 0xFFFFFFF1;
                    this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
                }
            }
            else if (name.equalsIgnoreCase("normalize-characters") || name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("check-character-normalization")) {
                if (state) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                    throw new DOMException((short)9, msg);
                }
            }
            else if (name.equalsIgnoreCase("element-content-whitespace")) {
                if (!state) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                    throw new DOMException((short)9, msg);
                }
            }
            else if (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
                if (!state) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                    throw new DOMException((short)9, msg);
                }
            }
            else if (name.equalsIgnoreCase("psvi")) {
                this.features = (short)(state ? (this.features | 0x80) : (this.features & 0xFFFFFF7F));
            }
            else {
                found = false;
            }
        }
        if (!found || !(value instanceof Boolean)) {
            found = true;
            if (name.equalsIgnoreCase("error-handler")) {
                if (!(value instanceof DOMErrorHandler) && value != null) {
                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg2);
                }
                this.fErrorHandlerWrapper.setErrorHandler((DOMErrorHandler)value);
                this.setErrorHandler(this.fErrorHandlerWrapper);
            }
            else if (name.equalsIgnoreCase("resource-resolver")) {
                if (!(value instanceof LSResourceResolver)) {
                    if (value != null) {
                        final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                        throw new DOMException((short)17, msg2);
                    }
                }
                try {
                    this.setEntityResolver(new DOMEntityResolverWrapper((LSResourceResolver)value));
                }
                catch (final XMLConfigurationException ex) {}
            }
            else if (name.equalsIgnoreCase("schema-location")) {
                if (!(value instanceof String)) {
                    if (value != null) {
                        final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                        throw new DOMException((short)17, msg2);
                    }
                }
                try {
                    this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", value);
                }
                catch (final XMLConfigurationException ex2) {}
            }
            else if (name.equalsIgnoreCase("schema-type")) {
                if (!(value instanceof String)) {
                    if (value != null) {
                        final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                        throw new DOMException((short)17, msg2);
                    }
                }
                try {
                    if (value == null) {
                        this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
                    }
                    else if (value.equals(Constants.NS_XMLSCHEMA)) {
                        this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
                    }
                    else if (value.equals(Constants.NS_DTD)) {
                        this.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                    }
                }
                catch (final XMLConfigurationException ex3) {}
            }
            else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
                if (!(value instanceof SymbolTable)) {
                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg2);
                }
                this.setProperty("http://apache.org/xml/properties/internal/symbol-table", value);
            }
            else {
                if (!name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
                    throw new DOMException((short)8, msg2);
                }
                if (!(value instanceof XMLGrammarPool)) {
                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                    throw new DOMException((short)17, msg2);
                }
                this.setProperty("http://apache.org/xml/properties/internal/grammar-pool", value);
            }
        }
    }
    
    @Override
    public Object getParameter(final String name) throws DOMException {
        if (name.equalsIgnoreCase("comments")) {
            return ((this.features & 0x20) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespaces")) {
            return ((this.features & 0x1) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("datatype-normalization")) {
            return ((this.features & 0x2) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("cdata-sections")) {
            return ((this.features & 0x8) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("entities")) {
            return ((this.features & 0x4) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("split-cdata-sections")) {
            return ((this.features & 0x10) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("validate")) {
            return ((this.features & 0x40) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("well-formed")) {
            return ((this.features & 0x100) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespace-declarations")) {
            return ((this.features & 0x200) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("infoset")) {
            return ((this.features & 0x32F) == 0x321) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("normalize-characters") || name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("check-character-normalization")) {
            return Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
            return Boolean.TRUE;
        }
        if (name.equalsIgnoreCase("psvi")) {
            return ((this.features & 0x80) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("element-content-whitespace")) {
            return Boolean.TRUE;
        }
        if (name.equalsIgnoreCase("error-handler")) {
            return this.fErrorHandlerWrapper.getErrorHandler();
        }
        if (name.equalsIgnoreCase("resource-resolver")) {
            final XMLEntityResolver entityResolver = this.getEntityResolver();
            if (entityResolver != null && entityResolver instanceof DOMEntityResolverWrapper) {
                return ((DOMEntityResolverWrapper)entityResolver).getEntityResolver();
            }
            return null;
        }
        else {
            if (name.equalsIgnoreCase("schema-type")) {
                return this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
            }
            if (name.equalsIgnoreCase("schema-location")) {
                return this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
            }
            if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
                return this.getProperty("http://apache.org/xml/properties/internal/symbol-table");
            }
            if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
                return this.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
            }
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
            throw new DOMException((short)8, msg);
        }
    }
    
    @Override
    public boolean canSetParameter(final String name, final Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Boolean) {
            if (name.equalsIgnoreCase("comments") || name.equalsIgnoreCase("datatype-normalization") || name.equalsIgnoreCase("cdata-sections") || name.equalsIgnoreCase("entities") || name.equalsIgnoreCase("split-cdata-sections") || name.equalsIgnoreCase("namespaces") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("infoset") || name.equalsIgnoreCase("namespace-declarations")) {
                return true;
            }
            if (name.equalsIgnoreCase("normalize-characters") || name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("check-character-normalization")) {
                return !value.equals(Boolean.TRUE);
            }
            return (name.equalsIgnoreCase("element-content-whitespace") || name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) && value.equals(Boolean.TRUE);
        }
        else {
            if (name.equalsIgnoreCase("error-handler")) {
                return value instanceof DOMErrorHandler;
            }
            if (name.equalsIgnoreCase("resource-resolver")) {
                return value instanceof LSResourceResolver;
            }
            if (name.equalsIgnoreCase("schema-location")) {
                return value instanceof String;
            }
            if (name.equalsIgnoreCase("schema-type")) {
                return value instanceof String && value.equals(Constants.NS_XMLSCHEMA);
            }
            if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
                return value instanceof SymbolTable;
            }
            return name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool") && value instanceof XMLGrammarPool;
        }
    }
    
    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final Vector parameters = new Vector();
            parameters.add("comments");
            parameters.add("datatype-normalization");
            parameters.add("cdata-sections");
            parameters.add("entities");
            parameters.add("split-cdata-sections");
            parameters.add("namespaces");
            parameters.add("validate");
            parameters.add("infoset");
            parameters.add("normalize-characters");
            parameters.add("canonical-form");
            parameters.add("validate-if-schema");
            parameters.add("check-character-normalization");
            parameters.add("well-formed");
            parameters.add("namespace-declarations");
            parameters.add("element-content-whitespace");
            parameters.add("error-handler");
            parameters.add("schema-type");
            parameters.add("schema-location");
            parameters.add("resource-resolver");
            parameters.add("http://apache.org/xml/properties/internal/grammar-pool");
            parameters.add("http://apache.org/xml/properties/internal/symbol-table");
            parameters.add("http://apache.org/xml/features/validation/schema/augment-psvi");
            this.fRecognizedParameters = new DOMStringListImpl(parameters);
        }
        return this.fRecognizedParameters;
    }
    
    protected void reset() throws XNIException {
        if (this.fValidationManager != null) {
            this.fValidationManager.reset();
        }
        for (int count = this.fComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fComponents.get(i);
            c.reset(this);
        }
    }
    
    @Override
    protected PropertyState checkProperty(final String propertyId) throws XMLConfigurationException {
        if (propertyId.startsWith("http://xml.org/sax/properties/")) {
            final int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
            if (suffixLength == "xml-string".length() && propertyId.endsWith("xml-string")) {
                return PropertyState.NOT_SUPPORTED;
            }
        }
        return super.checkProperty(propertyId);
    }
    
    protected void addComponent(final XMLComponent component) {
        if (this.fComponents.contains(component)) {
            return;
        }
        this.fComponents.add(component);
        final String[] recognizedFeatures = component.getRecognizedFeatures();
        this.addRecognizedFeatures(recognizedFeatures);
        final String[] recognizedProperties = component.getRecognizedProperties();
        this.addRecognizedProperties(recognizedProperties);
    }
    
    protected ValidationManager createValidationManager() {
        return new ValidationManager();
    }
}
