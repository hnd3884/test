package org.apache.xerces.parsers;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xinclude.XIncludeHandler;

public class XIncludeParserConfiguration extends XML11Configuration
{
    private XIncludeHandler fXIncludeHandler;
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
    protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    
    public XIncludeParserConfiguration() {
        this(null, null, null);
    }
    
    public XIncludeParserConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public XIncludeParserConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public XIncludeParserConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
        this.addCommonComponent(this.fXIncludeHandler = new XIncludeHandler());
        this.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" });
        this.addRecognizedProperties(new String[] { "http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/namespace-context" });
        this.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
        this.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
        this.setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
        this.setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
        this.setProperty("http://apache.org/xml/properties/internal/namespace-context", new XIncludeNamespaceSupport());
    }
    
    protected void configurePipeline() {
        super.configurePipeline();
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
    
    protected void configureXML11Pipeline() {
        super.configureXML11Pipeline();
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
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/properties/internal/xinclude-handler")) {}
        super.setProperty(s, o);
    }
}
