package com.sun.org.apache.xerces.internal.parsers;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

public class SAXParser extends AbstractSAXParser
{
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
    private static final String[] RECOGNIZED_FEATURES;
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String[] RECOGNIZED_PROPERTIES;
    
    public SAXParser(final XMLParserConfiguration config) {
        super(config);
    }
    
    public SAXParser() {
        this(null, null);
    }
    
    public SAXParser(final SymbolTable symbolTable) {
        this(symbolTable, null);
    }
    
    public SAXParser(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        super(new XIncludeAwareParserConfiguration());
        this.fConfiguration.addRecognizedFeatures(SAXParser.RECOGNIZED_FEATURES);
        this.fConfiguration.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
        this.fConfiguration.addRecognizedProperties(SAXParser.RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        }
        if (grammarPool != null) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
        }
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://apache.org/xml/properties/security-manager")) {
            super.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager = XMLSecurityManager.convert(value, this.securityManager));
            return;
        }
        if (name.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            if (value == null) {
                this.securityPropertyManager = new XMLSecurityPropertyManager();
            }
            else {
                this.securityPropertyManager = (XMLSecurityPropertyManager)value;
            }
            super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
            return;
        }
        if (this.securityManager == null) {
            super.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager = new XMLSecurityManager(true));
        }
        if (this.securityPropertyManager == null) {
            super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager = new XMLSecurityPropertyManager());
        }
        final int index = this.securityPropertyManager.getIndex(name);
        if (index > -1) {
            this.securityPropertyManager.setValue(index, XMLSecurityPropertyManager.State.APIPROPERTY, (String)value);
        }
        else if (!this.securityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) {
            super.setProperty(name, value);
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace" };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
    }
}
