package org.apache.xerces.parsers;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xinclude.XIncludeHandler;

public class XIncludeAwareParserConfiguration extends XML11Configuration
{
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
    protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    protected XIncludeHandler fXIncludeHandler;
    protected NamespaceSupport fNonXIncludeNSContext;
    protected XIncludeNamespaceSupport fXIncludeNSContext;
    protected NamespaceContext fCurrentNSContext;
    protected boolean fXIncludeEnabled;
    
    public XIncludeAwareParserConfiguration() {
        this(null, null, null);
    }
    
    public XIncludeAwareParserConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public XIncludeAwareParserConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public XIncludeAwareParserConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
        this.fXIncludeEnabled = false;
        this.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" });
        this.addRecognizedProperties(new String[] { "http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/namespace-context" });
        this.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
        this.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
        this.setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
        this.fNonXIncludeNSContext = new NamespaceSupport();
        this.fCurrentNSContext = this.fNonXIncludeNSContext;
        this.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
    }
    
    protected void configurePipeline() {
        super.configurePipeline();
        if (this.fXIncludeEnabled) {
            if (this.fXIncludeHandler == null) {
                this.setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler = new XIncludeHandler());
                this.addCommonComponent(this.fXIncludeHandler);
                this.fXIncludeHandler.reset(this);
            }
            if (this.fCurrentNSContext != this.fXIncludeNSContext) {
                if (this.fXIncludeNSContext == null) {
                    this.fXIncludeNSContext = new XIncludeNamespaceSupport();
                }
                this.fCurrentNSContext = this.fXIncludeNSContext;
                this.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fXIncludeNSContext);
            }
            this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
            this.fDTDProcessor.setDTDSource(this.fDTDScanner);
            this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDTDSource(this.fDTDProcessor);
            this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
            if (this.fDTDHandler != null) {
                this.fDTDHandler.setDTDSource(this.fXIncludeHandler);
            }
            XMLDocumentSource documentSource;
            if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
                documentSource = this.fSchemaValidator.getDocumentSource();
            }
            else {
                documentSource = this.fLastComponent;
                this.fLastComponent = this.fXIncludeHandler;
            }
            final XMLDocumentHandler documentHandler = documentSource.getDocumentHandler();
            documentSource.setDocumentHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDocumentSource(documentSource);
            if (documentHandler != null) {
                this.fXIncludeHandler.setDocumentHandler(documentHandler);
                documentHandler.setDocumentSource(this.fXIncludeHandler);
            }
        }
        else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
            this.fCurrentNSContext = this.fNonXIncludeNSContext;
            this.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
        }
    }
    
    protected void configureXML11Pipeline() {
        super.configureXML11Pipeline();
        if (this.fXIncludeEnabled) {
            if (this.fXIncludeHandler == null) {
                this.setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler = new XIncludeHandler());
                this.addCommonComponent(this.fXIncludeHandler);
                this.fXIncludeHandler.reset(this);
            }
            if (this.fCurrentNSContext != this.fXIncludeNSContext) {
                if (this.fXIncludeNSContext == null) {
                    this.fXIncludeNSContext = new XIncludeNamespaceSupport();
                }
                this.fCurrentNSContext = this.fXIncludeNSContext;
                this.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fXIncludeNSContext);
            }
            this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
            this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
            this.fXML11DTDProcessor.setDTDHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDTDSource(this.fXML11DTDProcessor);
            this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
            if (this.fDTDHandler != null) {
                this.fDTDHandler.setDTDSource(this.fXIncludeHandler);
            }
            XMLDocumentSource documentSource;
            if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
                documentSource = this.fSchemaValidator.getDocumentSource();
            }
            else {
                documentSource = this.fLastComponent;
                this.fLastComponent = this.fXIncludeHandler;
            }
            final XMLDocumentHandler documentHandler = documentSource.getDocumentHandler();
            documentSource.setDocumentHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDocumentSource(documentSource);
            if (documentHandler != null) {
                this.fXIncludeHandler.setDocumentHandler(documentHandler);
                documentHandler.setDocumentSource(this.fXIncludeHandler);
            }
        }
        else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
            this.fCurrentNSContext = this.fNonXIncludeNSContext;
            this.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
        }
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return this.fConfigUpdated;
        }
        if (s.equals("http://apache.org/xml/features/xinclude")) {
            return this.fXIncludeEnabled;
        }
        return super.getFeature0(s);
    }
    
    public void setFeature(final String s, final boolean fxIncludeEnabled) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/features/xinclude")) {
            this.fXIncludeEnabled = fxIncludeEnabled;
            this.fConfigUpdated = true;
            return;
        }
        super.setFeature(s, fxIncludeEnabled);
    }
}
