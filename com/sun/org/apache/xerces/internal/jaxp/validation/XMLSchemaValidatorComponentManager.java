package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.Iterator;
import java.util.Map;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import java.util.Locale;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;

final class XMLSchemaValidatorComponentManager extends ParserConfigurationSettings implements XMLComponentManager
{
    private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    private static final String VALIDATION = "http://xml.org/sax/features/validation";
    private static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String LOCALE = "http://apache.org/xml/properties/locale";
    private boolean _isSecureMode;
    private boolean fConfigUpdated;
    private boolean fUseGrammarPoolOnly;
    private final HashMap fComponents;
    private XMLEntityManager fEntityManager;
    private XMLErrorReporter fErrorReporter;
    private NamespaceContext fNamespaceContext;
    private XMLSchemaValidator fSchemaValidator;
    private ValidationManager fValidationManager;
    private final HashMap fInitFeatures;
    private final HashMap fInitProperties;
    private XMLSecurityManager fInitSecurityManager;
    private final XMLSecurityPropertyManager fSecurityPropertyMgr;
    private ErrorHandler fErrorHandler;
    private LSResourceResolver fResourceResolver;
    private Locale fLocale;
    
    public XMLSchemaValidatorComponentManager(final XSGrammarPoolContainer grammarContainer) {
        this._isSecureMode = false;
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
        this.fComponents.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
        this.fValidationManager = new ValidationManager();
        this.fComponents.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
        this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
        this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
        this.fComponents.put("http://apache.org/xml/properties/internal/symbol-table", new SymbolTable());
        this.fComponents.put("http://apache.org/xml/properties/internal/grammar-pool", grammarContainer.getGrammarPool());
        this.fUseGrammarPoolOnly = grammarContainer.isFullyComposed();
        this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        this.addRecognizedParamsAndSetDefaults(this.fEntityManager, grammarContainer);
        this.addRecognizedParamsAndSetDefaults(this.fErrorReporter, grammarContainer);
        this.addRecognizedParamsAndSetDefaults(this.fSchemaValidator, grammarContainer);
        boolean secureProcessing = grammarContainer.getFeature("http://javax.xml.XMLConstants/feature/secure-processing");
        if (System.getSecurityManager() != null) {
            this._isSecureMode = true;
            secureProcessing = true;
        }
        this.fInitSecurityManager = (XMLSecurityManager)grammarContainer.getProperty("http://apache.org/xml/properties/security-manager");
        if (this.fInitSecurityManager != null) {
            this.fInitSecurityManager.setSecureProcessing(secureProcessing);
        }
        else {
            this.fInitSecurityManager = new XMLSecurityManager(secureProcessing);
        }
        this.setProperty("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
        this.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)grammarContainer.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
    }
    
    @Override
    public FeatureState getFeatureState(final String featureId) throws XMLConfigurationException {
        if ("http://apache.org/xml/features/internal/parser-settings".equals(featureId)) {
            return FeatureState.is(this.fConfigUpdated);
        }
        if ("http://xml.org/sax/features/validation".equals(featureId) || "http://apache.org/xml/features/validation/schema".equals(featureId)) {
            return FeatureState.is(true);
        }
        if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(featureId)) {
            return FeatureState.is(this.fUseGrammarPoolOnly);
        }
        if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(featureId)) {
            return FeatureState.is(this.fInitSecurityManager.isSecureProcessing());
        }
        if ("http://apache.org/xml/features/validation/schema/element-default".equals(featureId)) {
            return FeatureState.is(true);
        }
        return super.getFeatureState(featureId);
    }
    
    @Override
    public void setFeature(final String featureId, final boolean value) throws XMLConfigurationException {
        if ("http://apache.org/xml/features/internal/parser-settings".equals(featureId)) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
        }
        if (!value && ("http://xml.org/sax/features/validation".equals(featureId) || "http://apache.org/xml/features/validation/schema".equals(featureId))) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
        }
        if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(featureId) && value != this.fUseGrammarPoolOnly) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
        }
        if (!"http://javax.xml.XMLConstants/feature/secure-processing".equals(featureId)) {
            this.fConfigUpdated = true;
            this.fEntityManager.setFeature(featureId, value);
            this.fErrorReporter.setFeature(featureId, value);
            this.fSchemaValidator.setFeature(featureId, value);
            if (!this.fInitFeatures.containsKey(featureId)) {
                final boolean current = super.getFeature(featureId);
                this.fInitFeatures.put(featureId, current ? Boolean.TRUE : Boolean.FALSE);
            }
            super.setFeature(featureId, value);
            return;
        }
        if (this._isSecureMode && !value) {
            throw new XMLConfigurationException(Status.NOT_ALLOWED, "http://javax.xml.XMLConstants/feature/secure-processing");
        }
        this.fInitSecurityManager.setSecureProcessing(value);
        this.setProperty("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
        if (value && Constants.IS_JDK8_OR_ABOVE) {
            this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
            this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
            this.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        }
    }
    
    @Override
    public PropertyState getPropertyState(final String propertyId) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
            return PropertyState.is(this.getLocale());
        }
        final Object component = this.fComponents.get(propertyId);
        if (component != null) {
            return PropertyState.is(component);
        }
        if (this.fComponents.containsKey(propertyId)) {
            return PropertyState.is(null);
        }
        return super.getPropertyState(propertyId);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/internal/entity-manager".equals(propertyId) || "http://apache.org/xml/properties/internal/error-reporter".equals(propertyId) || "http://apache.org/xml/properties/internal/namespace-context".equals(propertyId) || "http://apache.org/xml/properties/internal/validator/schema".equals(propertyId) || "http://apache.org/xml/properties/internal/symbol-table".equals(propertyId) || "http://apache.org/xml/properties/internal/validation-manager".equals(propertyId) || "http://apache.org/xml/properties/internal/grammar-pool".equals(propertyId)) {
            throw new XMLConfigurationException(Status.NOT_SUPPORTED, propertyId);
        }
        this.fConfigUpdated = true;
        this.fEntityManager.setProperty(propertyId, value);
        this.fErrorReporter.setProperty(propertyId, value);
        this.fSchemaValidator.setProperty(propertyId, value);
        if ("http://apache.org/xml/properties/internal/entity-resolver".equals(propertyId) || "http://apache.org/xml/properties/internal/error-handler".equals(propertyId) || "http://apache.org/xml/properties/security-manager".equals(propertyId)) {
            this.fComponents.put(propertyId, value);
            return;
        }
        if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
            this.setLocale((Locale)value);
            this.fComponents.put(propertyId, value);
            return;
        }
        if ((this.fInitSecurityManager == null || !this.fInitSecurityManager.setLimit(propertyId, XMLSecurityManager.State.APIPROPERTY, value)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(propertyId, XMLSecurityPropertyManager.State.APIPROPERTY, value))) {
            if (!this.fInitProperties.containsKey(propertyId)) {
                this.fInitProperties.put(propertyId, super.getProperty(propertyId));
            }
            super.setProperty(propertyId, value);
        }
    }
    
    public void addRecognizedParamsAndSetDefaults(final XMLComponent component, final XSGrammarPoolContainer grammarContainer) {
        final String[] recognizedFeatures = component.getRecognizedFeatures();
        this.addRecognizedFeatures(recognizedFeatures);
        final String[] recognizedProperties = component.getRecognizedProperties();
        this.addRecognizedProperties(recognizedProperties);
        this.setFeatureDefaults(component, recognizedFeatures, grammarContainer);
        this.setPropertyDefaults(component, recognizedProperties);
    }
    
    public void reset() throws XNIException {
        this.fNamespaceContext.reset();
        this.fValidationManager.reset();
        this.fEntityManager.reset(this);
        this.fErrorReporter.reset(this);
        this.fSchemaValidator.reset(this);
        this.fConfigUpdated = false;
    }
    
    void setErrorHandler(final ErrorHandler errorHandler) {
        this.fErrorHandler = errorHandler;
        this.setProperty("http://apache.org/xml/properties/internal/error-handler", (errorHandler != null) ? new ErrorHandlerWrapper(errorHandler) : new ErrorHandlerWrapper(DraconianErrorHandler.getInstance()));
    }
    
    ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    void setResourceResolver(final LSResourceResolver resourceResolver) {
        this.fResourceResolver = resourceResolver;
        this.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper(resourceResolver));
    }
    
    LSResourceResolver getResourceResolver() {
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
        this.setLocale(null);
        this.fComponents.put("http://apache.org/xml/properties/locale", null);
        this.fComponents.put("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
        this.setLocale(null);
        this.fComponents.put("http://apache.org/xml/properties/locale", null);
        if (!this.fInitFeatures.isEmpty()) {
            for (final Map.Entry entry : this.fInitFeatures.entrySet()) {
                final String name = entry.getKey();
                final boolean value = entry.getValue();
                super.setFeature(name, value);
            }
            this.fInitFeatures.clear();
        }
        if (!this.fInitProperties.isEmpty()) {
            for (final Map.Entry entry : this.fInitProperties.entrySet()) {
                final String name = entry.getKey();
                final Object value2 = entry.getValue();
                super.setProperty(name, value2);
            }
            this.fInitProperties.clear();
        }
    }
    
    private void setFeatureDefaults(final XMLComponent component, final String[] recognizedFeatures, final XSGrammarPoolContainer grammarContainer) {
        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; ++i) {
                final String featureId = recognizedFeatures[i];
                Boolean state = grammarContainer.getFeature(featureId);
                if (state == null) {
                    state = component.getFeatureDefault(featureId);
                }
                if (state != null && !this.fFeatures.containsKey(featureId)) {
                    this.fFeatures.put(featureId, state);
                    this.fConfigUpdated = true;
                }
            }
        }
    }
    
    private void setPropertyDefaults(final XMLComponent component, final String[] recognizedProperties) {
        if (recognizedProperties != null) {
            for (int i = 0; i < recognizedProperties.length; ++i) {
                final String propertyId = recognizedProperties[i];
                final Object value = component.getPropertyDefault(propertyId);
                if (value != null && !this.fProperties.containsKey(propertyId)) {
                    this.fProperties.put(propertyId, value);
                    this.fConfigUpdated = true;
                }
            }
        }
    }
}
