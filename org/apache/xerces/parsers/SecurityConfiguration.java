package org.apache.xerces.parsers;

import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;

public class SecurityConfiguration extends XIncludeAwareParserConfiguration
{
    protected static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
    
    public SecurityConfiguration() {
        this(null, null, null);
    }
    
    public SecurityConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public SecurityConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public SecurityConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
        this.setProperty("http://apache.org/xml/properties/security-manager", new SecurityManager());
    }
}
