package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.util.ShadowedSymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

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
    
    public CachingParserPool(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this.fShadowSymbolTable = false;
        this.fShadowGrammarPool = false;
        this.fSynchronizedSymbolTable = new SynchronizedSymbolTable(symbolTable);
        this.fSynchronizedGrammarPool = new SynchronizedGrammarPool(grammarPool);
    }
    
    public SymbolTable getSymbolTable() {
        return this.fSynchronizedSymbolTable;
    }
    
    public XMLGrammarPool getXMLGrammarPool() {
        return this.fSynchronizedGrammarPool;
    }
    
    public void setShadowSymbolTable(final boolean shadow) {
        this.fShadowSymbolTable = shadow;
    }
    
    public DOMParser createDOMParser() {
        final SymbolTable symbolTable = this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable;
        final XMLGrammarPool grammarPool = this.fShadowGrammarPool ? new ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool;
        return new DOMParser(symbolTable, grammarPool);
    }
    
    public SAXParser createSAXParser() {
        final SymbolTable symbolTable = this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable;
        final XMLGrammarPool grammarPool = this.fShadowGrammarPool ? new ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool;
        return new SAXParser(symbolTable, grammarPool);
    }
    
    public static final class SynchronizedGrammarPool implements XMLGrammarPool
    {
        private XMLGrammarPool fGrammarPool;
        
        public SynchronizedGrammarPool(final XMLGrammarPool grammarPool) {
            this.fGrammarPool = grammarPool;
        }
        
        @Override
        public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
            synchronized (this.fGrammarPool) {
                return this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
            }
        }
        
        @Override
        public Grammar retrieveGrammar(final XMLGrammarDescription gDesc) {
            synchronized (this.fGrammarPool) {
                return this.fGrammarPool.retrieveGrammar(gDesc);
            }
        }
        
        @Override
        public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.cacheGrammars(grammarType, grammars);
            }
        }
        
        @Override
        public void lockPool() {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.lockPool();
            }
        }
        
        @Override
        public void clear() {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.clear();
            }
        }
        
        @Override
        public void unlockPool() {
            synchronized (this.fGrammarPool) {
                this.fGrammarPool.unlockPool();
            }
        }
    }
    
    public static final class ShadowedGrammarPool extends XMLGrammarPoolImpl
    {
        private XMLGrammarPool fGrammarPool;
        
        public ShadowedGrammarPool(final XMLGrammarPool grammarPool) {
            this.fGrammarPool = grammarPool;
        }
        
        @Override
        public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
            final Grammar[] grammars = super.retrieveInitialGrammarSet(grammarType);
            if (grammars != null) {
                return grammars;
            }
            return this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
        }
        
        @Override
        public Grammar retrieveGrammar(final XMLGrammarDescription gDesc) {
            final Grammar g = super.retrieveGrammar(gDesc);
            if (g != null) {
                return g;
            }
            return this.fGrammarPool.retrieveGrammar(gDesc);
        }
        
        @Override
        public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
            super.cacheGrammars(grammarType, grammars);
            this.fGrammarPool.cacheGrammars(grammarType, grammars);
        }
        
        @Override
        public Grammar getGrammar(final XMLGrammarDescription desc) {
            if (super.containsGrammar(desc)) {
                return super.getGrammar(desc);
            }
            return null;
        }
        
        @Override
        public boolean containsGrammar(final XMLGrammarDescription desc) {
            return super.containsGrammar(desc);
        }
    }
}
