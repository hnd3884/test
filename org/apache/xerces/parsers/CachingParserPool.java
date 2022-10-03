package org.apache.xerces.parsers;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.util.ShadowedSymbolTable;
import org.apache.xerces.util.SynchronizedSymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;

public class CachingParserPool
{
    public static final boolean DEFAULT_SHADOW_SYMBOL_TABLE = false;
    public static final boolean DEFAULT_SHADOW_GRAMMAR_POOL = false;
    protected SymbolTable fSynchronizedSymbolTable;
    protected XMLGrammarPool fSynchronizedGrammarPool;
    protected boolean fShadowSymbolTable;
    protected boolean fShadowGrammarPool;
    
    public CachingParserPool() {
        this(new SymbolTable(), new XMLGrammarPoolImpl());
    }
    
    public CachingParserPool(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this.fShadowSymbolTable = false;
        this.fShadowGrammarPool = false;
        this.fSynchronizedSymbolTable = new SynchronizedSymbolTable(symbolTable);
        this.fSynchronizedGrammarPool = new SynchronizedGrammarPool(xmlGrammarPool);
    }
    
    public SymbolTable getSymbolTable() {
        return this.fSynchronizedSymbolTable;
    }
    
    public XMLGrammarPool getXMLGrammarPool() {
        return this.fSynchronizedGrammarPool;
    }
    
    public void setShadowSymbolTable(final boolean fShadowSymbolTable) {
        this.fShadowSymbolTable = fShadowSymbolTable;
    }
    
    public DOMParser createDOMParser() {
        return new DOMParser(this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable, this.fShadowGrammarPool ? new ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool);
    }
    
    public SAXParser createSAXParser() {
        return new SAXParser(this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable, this.fShadowGrammarPool ? new ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool);
    }
    
    public static final class ShadowedGrammarPool extends XMLGrammarPoolImpl
    {
        private XMLGrammarPool fGrammarPool;
        
        public ShadowedGrammarPool(final XMLGrammarPool fGrammarPool) {
            this.fGrammarPool = fGrammarPool;
        }
        
        public Grammar[] retrieveInitialGrammarSet(final String s) {
            final Grammar[] retrieveInitialGrammarSet = super.retrieveInitialGrammarSet(s);
            if (retrieveInitialGrammarSet != null) {
                return retrieveInitialGrammarSet;
            }
            return this.fGrammarPool.retrieveInitialGrammarSet(s);
        }
        
        public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            final Grammar retrieveGrammar = super.retrieveGrammar(xmlGrammarDescription);
            if (retrieveGrammar != null) {
                return retrieveGrammar;
            }
            return this.fGrammarPool.retrieveGrammar(xmlGrammarDescription);
        }
        
        public void cacheGrammars(final String s, final Grammar[] array) {
            super.cacheGrammars(s, array);
            this.fGrammarPool.cacheGrammars(s, array);
        }
        
        public Grammar getGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            if (super.containsGrammar(xmlGrammarDescription)) {
                return super.getGrammar(xmlGrammarDescription);
            }
            return null;
        }
        
        public boolean containsGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            return super.containsGrammar(xmlGrammarDescription);
        }
    }
    
    public static final class SynchronizedGrammarPool implements XMLGrammarPool
    {
        private XMLGrammarPool fGrammarPool;
        
        public SynchronizedGrammarPool(final XMLGrammarPool fGrammarPool) {
            this.fGrammarPool = fGrammarPool;
        }
        
        public Grammar[] retrieveInitialGrammarSet(final String s) {
            synchronized (this.fGrammarPool) {
                return this.fGrammarPool.retrieveInitialGrammarSet(s);
            }
        }
        
        public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            synchronized (this.fGrammarPool) {
                return this.fGrammarPool.retrieveGrammar(xmlGrammarDescription);
            }
        }
        
        public void cacheGrammars(final String s, final Grammar[] array) {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.cacheGrammars(s, array);
            }
        }
        
        public void lockPool() {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.lockPool();
            }
        }
        
        public void clear() {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.clear();
            }
        }
        
        public void unlockPool() {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.unlockPool();
            }
        }
    }
}
