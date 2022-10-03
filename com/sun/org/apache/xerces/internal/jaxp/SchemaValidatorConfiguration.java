package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

final class SchemaValidatorConfiguration implements XMLComponentManager
{
    private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    private static final String VALIDATION = "http://xml.org/sax/features/validation";
    private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private final XMLComponentManager fParentComponentManager;
    private final XMLGrammarPool fGrammarPool;
    private final boolean fUseGrammarPoolOnly;
    private final ValidationManager fValidationManager;
    
    public SchemaValidatorConfiguration(final XMLComponentManager parentManager, final XSGrammarPoolContainer grammarContainer, final ValidationManager validationManager) {
        this.fParentComponentManager = parentManager;
        this.fGrammarPool = grammarContainer.getGrammarPool();
        this.fUseGrammarPoolOnly = grammarContainer.isFullyComposed();
        this.fValidationManager = validationManager;
        try {
            final XMLErrorReporter errorReporter = (XMLErrorReporter)this.fParentComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
            if (errorReporter != null) {
                errorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    @Override
    public boolean getFeature(final String featureId) throws XMLConfigurationException {
        final FeatureState state = this.getFeatureState(featureId);
        if (state.isExceptional()) {
            throw new XMLConfigurationException(state.status, featureId);
        }
        return state.state;
    }
    
    @Override
    public FeatureState getFeatureState(final String featureId) {
        if ("http://apache.org/xml/features/internal/parser-settings".equals(featureId)) {
            return this.fParentComponentManager.getFeatureState(featureId);
        }
        if ("http://xml.org/sax/features/validation".equals(featureId) || "http://apache.org/xml/features/validation/schema".equals(featureId)) {
            return FeatureState.is(true);
        }
        if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(featureId)) {
            return FeatureState.is(this.fUseGrammarPoolOnly);
        }
        return this.fParentComponentManager.getFeatureState(featureId);
    }
    
    @Override
    public PropertyState getPropertyState(final String propertyId) {
        if ("http://apache.org/xml/properties/internal/grammar-pool".equals(propertyId)) {
            return PropertyState.is(this.fGrammarPool);
        }
        if ("http://apache.org/xml/properties/internal/validation-manager".equals(propertyId)) {
            return PropertyState.is(this.fValidationManager);
        }
        return this.fParentComponentManager.getPropertyState(propertyId);
    }
    
    @Override
    public Object getProperty(final String propertyId) throws XMLConfigurationException {
        final PropertyState state = this.getPropertyState(propertyId);
        if (state.isExceptional()) {
            throw new XMLConfigurationException(state.status, propertyId);
        }
        return state.state;
    }
    
    @Override
    public boolean getFeature(final String featureId, final boolean defaultValue) {
        final FeatureState state = this.getFeatureState(featureId);
        if (state.isExceptional()) {
            return defaultValue;
        }
        return state.state;
    }
    
    @Override
    public Object getProperty(final String propertyId, final Object defaultValue) {
        final PropertyState state = this.getPropertyState(propertyId);
        if (state.isExceptional()) {
            return defaultValue;
        }
        return state.state;
    }
}
