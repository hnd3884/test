package org.apache.xerces.jaxp;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;

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
    
    public SchemaValidatorConfiguration(final XMLComponentManager fParentComponentManager, final XSGrammarPoolContainer xsGrammarPoolContainer, final ValidationManager fValidationManager) {
        this.fParentComponentManager = fParentComponentManager;
        this.fGrammarPool = xsGrammarPoolContainer.getGrammarPool();
        this.fUseGrammarPoolOnly = xsGrammarPoolContainer.isFullyComposed();
        this.fValidationManager = fValidationManager;
        try {
            final XMLErrorReporter xmlErrorReporter = (XMLErrorReporter)this.fParentComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
            if (xmlErrorReporter != null) {
                xmlErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/features/internal/parser-settings".equals(s)) {
            return this.fParentComponentManager.getFeature(s);
        }
        if ("http://xml.org/sax/features/validation".equals(s) || "http://apache.org/xml/features/validation/schema".equals(s)) {
            return true;
        }
        if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(s)) {
            return this.fUseGrammarPoolOnly;
        }
        return this.fParentComponentManager.getFeature(s);
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/internal/grammar-pool".equals(s)) {
            return this.fGrammarPool;
        }
        if ("http://apache.org/xml/properties/internal/validation-manager".equals(s)) {
            return this.fValidationManager;
        }
        return this.fParentComponentManager.getProperty(s);
    }
}
