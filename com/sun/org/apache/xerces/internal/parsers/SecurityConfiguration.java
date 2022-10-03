package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

public class SecurityConfiguration extends XIncludeAwareParserConfiguration
{
    protected static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
    
    public SecurityConfiguration() {
        this(null, null, null);
    }
    
    public SecurityConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public SecurityConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    }
    
    public SecurityConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool, final XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);
        this.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
    }
}
