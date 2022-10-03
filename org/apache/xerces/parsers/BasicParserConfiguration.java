package org.apache.xerces.parsers;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLComponent;
import java.util.HashMap;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.util.ParserConfigurationSettings;

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
    
    protected BasicParserConfiguration(SymbolTable fSymbolTable, final XMLComponentManager xmlComponentManager) {
        super(xmlComponentManager);
        this.fComponents = new ArrayList();
        this.fRecognizedFeatures = new ArrayList();
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        this.addRecognizedFeatures(new String[] { "http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities" });
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
        this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
        this.addRecognizedProperties(new String[] { "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver" });
        if (fSymbolTable == null) {
            fSymbolTable = new SymbolTable();
        }
        this.fSymbolTable = fSymbolTable;
        this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
    }
    
    protected void addComponent(final XMLComponent xmlComponent) {
        if (this.fComponents.contains(xmlComponent)) {
            return;
        }
        this.fComponents.add(xmlComponent);
        final String[] recognizedFeatures = xmlComponent.getRecognizedFeatures();
        this.addRecognizedFeatures(recognizedFeatures);
        final String[] recognizedProperties = xmlComponent.getRecognizedProperties();
        this.addRecognizedProperties(recognizedProperties);
        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; ++i) {
                final String s = recognizedFeatures[i];
                final Boolean featureDefault = xmlComponent.getFeatureDefault(s);
                if (featureDefault != null) {
                    super.setFeature(s, featureDefault);
                }
            }
        }
        if (recognizedProperties != null) {
            for (int j = 0; j < recognizedProperties.length; ++j) {
                final String s2 = recognizedProperties[j];
                final Object propertyDefault = xmlComponent.getPropertyDefault(s2);
                if (propertyDefault != null) {
                    super.setProperty(s2, propertyDefault);
                }
            }
        }
    }
    
    public abstract void parse(final XMLInputSource p0) throws XNIException, IOException;
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
        if (this.fLastComponent != null) {
            this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fLastComponent);
            }
        }
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDTDHandler(final XMLDTDHandler fdtdHandler) {
        this.fDTDHandler = fdtdHandler;
    }
    
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler fdtdContentModelHandler) {
        this.fDTDContentModelHandler = fdtdContentModelHandler;
    }
    
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    public void setEntityResolver(final XMLEntityResolver xmlEntityResolver) {
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", xmlEntityResolver);
    }
    
    public XMLEntityResolver getEntityResolver() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
    }
    
    public void setErrorHandler(final XMLErrorHandler xmlErrorHandler) {
        this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", xmlErrorHandler);
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        for (int size = this.fComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fComponents.get(i)).setFeature(s, b);
        }
        super.setFeature(s, b);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        for (int size = this.fComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fComponents.get(i)).setProperty(s, o);
        }
        super.setProperty(s, o);
    }
    
    public void setLocale(final Locale fLocale) throws XNIException {
        this.fLocale = fLocale;
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    protected void reset() throws XNIException {
        for (int size = this.fComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fComponents.get(i)).reset(this);
        }
    }
    
    protected void checkProperty(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://xml.org/sax/properties/") && s.length() - "http://xml.org/sax/properties/".length() == "xml-string".length() && s.endsWith("xml-string")) {
            throw new XMLConfigurationException((short)1, s);
        }
        super.checkProperty(s);
    }
    
    protected void checkFeature(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/features/") && s.length() - "http://apache.org/xml/features/".length() == "internal/parser-settings".length() && s.endsWith("internal/parser-settings")) {
            throw new XMLConfigurationException((short)1, s);
        }
        super.checkFeature(s);
    }
}
