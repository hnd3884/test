package org.apache.xerces.jaxp.validation;

import java.util.Iterator;
import java.util.Map;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.NamespaceSupport;
import java.util.Locale;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.XMLEntityManager;
import java.util.HashMap;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.ParserConfigurationSettings;

final class XMLSchemaValidatorComponentManager extends ParserConfigurationSettings implements XMLComponentManager
{
    private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    private static final String VALIDATION = "http://xml.org/sax/features/validation";
    private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    private static final String IGNORE_XSI_TYPE = "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl";
    private static final String ID_IDREF_CHECKING = "http://apache.org/xml/features/validation/id-idref-checking";
    private static final String UNPARSED_ENTITY_CHECKING = "http://apache.org/xml/features/validation/unparsed-entity-checking";
    private static final String IDENTITY_CONSTRAINT_CHECKING = "http://apache.org/xml/features/validation/identity-constraint-checking";
    private static final String TYPE_ALTERNATIVES_CHECKING = "http://apache.org/xml/features/validation/type-alternative-checking";
    private static final String CTA_FULL_XPATH_CHECKING = "http://apache.org/xml/features/validation/cta-full-xpath-checking";
    private static final String ASSERT_COMMENT_PI_CHECKING = "http://apache.org/xml/features/validation/assert-comments-and-pi-checking";
    private static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    private static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    private static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String LOCALE = "http://apache.org/xml/properties/locale";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private boolean fConfigUpdated;
    private boolean fUseGrammarPoolOnly;
    private final String fXSDVersion;
    private final HashMap fComponents;
    private final XMLEntityManager fEntityManager;
    private final XMLErrorReporter fErrorReporter;
    private final NamespaceContext fNamespaceContext;
    private final XMLSchemaValidator fSchemaValidator;
    private final ValidationManager fValidationManager;
    private final HashMap fInitFeatures;
    private final HashMap fInitProperties;
    private final SecurityManager fInitSecurityManager;
    private ErrorHandler fErrorHandler;
    private LSResourceResolver fResourceResolver;
    private Locale fLocale;
    
    public XMLSchemaValidatorComponentManager(final XSGrammarPoolContainer xsGrammarPoolContainer) {
        this.fConfigUpdated = true;
        this.fComponents = new HashMap();
        this.fInitFeatures = new HashMap();
        this.fInitProperties = new HashMap();
        this.fErrorHandler = null;
        this.fResourceResolver = null;
        this.fLocale = null;
        this.fEntityManager = new XMLEntityManager();
        this.fComponents.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        this.fErrorReporter = new XMLErrorReporter();
        this.fComponents.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.fNamespaceContext = new NamespaceSupport();
        this.fComponents.put("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
        this.fSchemaValidator = new XMLSchemaValidator();
        this.fXSDVersion = xsGrammarPoolContainer.getXMLSchemaVersion();
        this.fSchemaValidator.setProperty("http://apache.org/xml/properties/validation/schema/version", this.fXSDVersion);
        this.fComponents.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
        this.fValidationManager = new ValidationManager();
        this.fComponents.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
        this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
        this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
        this.fComponents.put("http://apache.org/xml/properties/security-manager", null);
        this.fComponents.put("http://apache.org/xml/properties/internal/symbol-table", new SymbolTable());
        this.fComponents.put("http://apache.org/xml/properties/internal/grammar-pool", xsGrammarPoolContainer.getGrammarPool());
        this.fUseGrammarPoolOnly = xsGrammarPoolContainer.isFullyComposed();
        this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        this.addRecognizedFeatures(new String[] { "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi" });
        this.fFeatures.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/schema/element-default", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/schema/augment-psvi", Boolean.TRUE);
        this.addRecognizedParamsAndSetDefaults(this.fEntityManager, xsGrammarPoolContainer);
        this.addRecognizedParamsAndSetDefaults(this.fErrorReporter, xsGrammarPoolContainer);
        this.addRecognizedParamsAndSetDefaults(this.fSchemaValidator, xsGrammarPoolContainer);
        if (Boolean.TRUE.equals(xsGrammarPoolContainer.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"))) {
            this.fInitSecurityManager = new SecurityManager();
        }
        else {
            this.fInitSecurityManager = null;
        }
        this.fComponents.put("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
        this.fFeatures.put("http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/id-idref-checking", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/validation/identity-constraint-checking", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/validation/unparsed-entity-checking", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/validation/type-alternative-checking", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/validation/cta-full-xpath-checking", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/assert-comments-and-pi-checking", Boolean.FALSE);
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/features/internal/parser-settings".equals(s)) {
            return this.fConfigUpdated;
        }
        if ("http://xml.org/sax/features/validation".equals(s) || "http://apache.org/xml/features/validation/schema".equals(s)) {
            return true;
        }
        if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(s)) {
            return this.fUseGrammarPoolOnly;
        }
        if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(s)) {
            return this.getProperty("http://apache.org/xml/properties/security-manager") != null;
        }
        return super.getFeature(s);
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        if ("http://apache.org/xml/features/internal/parser-settings".equals(s)) {
            throw new XMLConfigurationException((short)1, s);
        }
        if (!b && ("http://xml.org/sax/features/validation".equals(s) || "http://apache.org/xml/features/validation/schema".equals(s))) {
            throw new XMLConfigurationException((short)1, s);
        }
        if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(s) && b != this.fUseGrammarPoolOnly) {
            throw new XMLConfigurationException((short)1, s);
        }
        if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(s)) {
            this.setProperty("http://apache.org/xml/properties/security-manager", b ? new SecurityManager() : null);
            return;
        }
        this.fConfigUpdated = true;
        this.fEntityManager.setFeature(s, b);
        this.fErrorReporter.setFeature(s, b);
        this.fSchemaValidator.setFeature(s, b);
        if (!this.fInitFeatures.containsKey(s)) {
            this.fInitFeatures.put(s, super.getFeature(s) ? Boolean.TRUE : Boolean.FALSE);
        }
        super.setFeature(s, b);
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/locale".equals(s)) {
            return this.getLocale();
        }
        if ("http://apache.org/xml/properties/validation/schema/version".equals(s)) {
            return this.fXSDVersion;
        }
        final Object value = this.fComponents.get(s);
        if (value != null) {
            return value;
        }
        if (this.fComponents.containsKey(s)) {
            return null;
        }
        return super.getProperty(s);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/internal/entity-manager".equals(s) || "http://apache.org/xml/properties/internal/error-reporter".equals(s) || "http://apache.org/xml/properties/internal/namespace-context".equals(s) || "http://apache.org/xml/properties/internal/validator/schema".equals(s) || "http://apache.org/xml/properties/internal/symbol-table".equals(s) || "http://apache.org/xml/properties/internal/validation-manager".equals(s) || "http://apache.org/xml/properties/internal/grammar-pool".equals(s) || "http://apache.org/xml/properties/validation/schema/version".equals(s)) {
            throw new XMLConfigurationException((short)1, s);
        }
        this.fConfigUpdated = true;
        this.fEntityManager.setProperty(s, o);
        this.fErrorReporter.setProperty(s, o);
        this.fSchemaValidator.setProperty(s, o);
        if ("http://apache.org/xml/properties/internal/entity-resolver".equals(s) || "http://apache.org/xml/properties/internal/error-handler".equals(s) || "http://apache.org/xml/properties/security-manager".equals(s)) {
            this.fComponents.put(s, o);
            return;
        }
        if ("http://apache.org/xml/properties/locale".equals(s)) {
            this.setLocale((Locale)o);
            this.fComponents.put(s, o);
            return;
        }
        if (!this.fInitProperties.containsKey(s)) {
            this.fInitProperties.put(s, super.getProperty(s));
        }
        super.setProperty(s, o);
    }
    
    public void addRecognizedParamsAndSetDefaults(final XMLComponent xmlComponent, final XSGrammarPoolContainer xsGrammarPoolContainer) {
        final String[] recognizedFeatures = xmlComponent.getRecognizedFeatures();
        this.addRecognizedFeatures(recognizedFeatures);
        final String[] recognizedProperties = xmlComponent.getRecognizedProperties();
        this.addRecognizedProperties(recognizedProperties);
        this.setFeatureDefaults(xmlComponent, recognizedFeatures, xsGrammarPoolContainer);
        this.setPropertyDefaults(xmlComponent, recognizedProperties);
    }
    
    public void reset() throws XNIException {
        this.fNamespaceContext.reset();
        this.fValidationManager.reset();
        this.fEntityManager.reset(this);
        this.fErrorReporter.reset(this);
        this.fSchemaValidator.reset(this);
        this.fConfigUpdated = false;
    }
    
    void setErrorHandler(final ErrorHandler fErrorHandler) {
        this.fErrorHandler = fErrorHandler;
        this.setProperty("http://apache.org/xml/properties/internal/error-handler", (fErrorHandler != null) ? new ErrorHandlerWrapper(fErrorHandler) : new ErrorHandlerWrapper(DraconianErrorHandler.getInstance()));
    }
    
    ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    void setResourceResolver(final LSResourceResolver fResourceResolver) {
        this.fResourceResolver = fResourceResolver;
        this.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper(fResourceResolver));
    }
    
    public LSResourceResolver getResourceResolver() {
        return this.fResourceResolver;
    }
    
    void setLocale(final Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    Locale getLocale() {
        return this.fLocale;
    }
    
    void restoreInitialState() {
        this.fConfigUpdated = true;
        this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
        this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
        this.fComponents.put("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
        this.setLocale(null);
        this.fComponents.put("http://apache.org/xml/properties/locale", null);
        if (!this.fInitFeatures.isEmpty()) {
            final Iterator iterator = this.fInitFeatures.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                super.setFeature((String)entry.getKey(), (boolean)entry.getValue());
            }
            this.fInitFeatures.clear();
        }
        if (!this.fInitProperties.isEmpty()) {
            final Iterator iterator2 = this.fInitProperties.entrySet().iterator();
            while (iterator2.hasNext()) {
                final Map.Entry entry2 = (Map.Entry)iterator2.next();
                super.setProperty((String)entry2.getKey(), entry2.getValue());
            }
            this.fInitProperties.clear();
        }
    }
    
    private void setFeatureDefaults(final XMLComponent xmlComponent, final String[] array, final XSGrammarPoolContainer xsGrammarPoolContainer) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                final String s = array[i];
                Boolean b = xsGrammarPoolContainer.getFeature(s);
                if (b == null) {
                    b = xmlComponent.getFeatureDefault(s);
                }
                if (b != null && !this.fFeatures.containsKey(s)) {
                    this.fFeatures.put(s, b);
                    this.fConfigUpdated = true;
                }
            }
        }
    }
    
    private void setPropertyDefaults(final XMLComponent xmlComponent, final String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                final String s = array[i];
                final Object propertyDefault = xmlComponent.getPropertyDefault(s);
                if (propertyDefault != null && !this.fProperties.containsKey(s)) {
                    this.fProperties.put(s, propertyDefault);
                    this.fConfigUpdated = true;
                }
            }
        }
    }
}
