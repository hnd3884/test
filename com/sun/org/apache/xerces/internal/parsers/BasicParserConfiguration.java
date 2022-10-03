package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import java.util.ArrayList;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;

public abstract class BasicParserConfiguration extends ParserConfigurationSettings implements XMLParserConfiguration
{
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected SymbolTable fSymbolTable;
    protected Locale fLocale;
    protected ArrayList fComponents;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLDocumentSource fLastComponent;
    
    protected BasicParserConfiguration() {
        this(null, null);
    }
    
    protected BasicParserConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null);
    }
    
    protected BasicParserConfiguration(SymbolTable symbolTable, final XMLComponentManager parentSettings) {
        super(parentSettings);
        this.fComponents = new ArrayList();
        this.fFeatures = new HashMap<String, Boolean>();
        this.fProperties = new HashMap<String, Object>();
        final String[] recognizedFeatures = { "http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities" };
        this.addRecognizedFeatures(recognizedFeatures);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
        this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
        final String[] recognizedProperties = { "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver" };
        this.addRecognizedProperties(recognizedProperties);
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        this.fSymbolTable = symbolTable;
        this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
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
        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; ++i) {
                final String featureId = recognizedFeatures[i];
                final Boolean state = component.getFeatureDefault(featureId);
                if (state != null) {
                    super.setFeature(featureId, state);
                }
            }
        }
        if (recognizedProperties != null) {
            for (int i = 0; i < recognizedProperties.length; ++i) {
                final String propertyId = recognizedProperties[i];
                final Object value = component.getPropertyDefault(propertyId);
                if (value != null) {
                    super.setProperty(propertyId, value);
                }
            }
        }
    }
    
    @Override
    public abstract void parse(final XMLInputSource p0) throws XNIException, IOException;
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
        if (this.fLastComponent != null) {
            this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fLastComponent);
            }
        }
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void setDTDHandler(final XMLDTDHandler dtdHandler) {
        this.fDTDHandler = dtdHandler;
    }
    
    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    @Override
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler handler) {
        this.fDTDContentModelHandler = handler;
    }
    
    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    @Override
    public void setEntityResolver(final XMLEntityResolver resolver) {
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
    }
    
    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
    }
    
    @Override
    public void setErrorHandler(final XMLErrorHandler errorHandler) {
        this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
    }
    
    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        for (int count = this.fComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fComponents.get(i);
            c.setFeature(featureId, state);
        }
        super.setFeature(featureId, state);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        for (int count = this.fComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fComponents.get(i);
            c.setProperty(propertyId, value);
        }
        super.setProperty(propertyId, value);
    }
    
    @Override
    public void setLocale(final Locale locale) throws XNIException {
        this.fLocale = locale;
    }
    
    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
    
    protected void reset() throws XNIException {
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
    
    @Override
    protected FeatureState checkFeature(final String featureId) throws XMLConfigurationException {
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
            if (suffixLength == "internal/parser-settings".length() && featureId.endsWith("internal/parser-settings")) {
                return FeatureState.NOT_SUPPORTED;
            }
        }
        return super.checkFeature(featureId);
    }
}
