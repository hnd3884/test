package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.DOMException;
import org.w3c.dom.ls.LSInput;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSGrammarPool;
import org.w3c.dom.DOMConfiguration;
import com.sun.org.apache.xerces.internal.xs.XSLoader;

public final class XSLoaderImpl implements XSLoader, DOMConfiguration
{
    private final XSGrammarPool fGrammarPool;
    private final XMLSchemaLoader fSchemaLoader;
    
    public XSLoaderImpl() {
        this.fGrammarPool = new XSGrammarMerger();
        (this.fSchemaLoader = new XMLSchemaLoader()).setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
    }
    
    @Override
    public DOMConfiguration getConfig() {
        return this;
    }
    
    @Override
    public XSModel loadURIList(final StringList uriList) {
        final int length = uriList.getLength();
        try {
            this.fGrammarPool.clear();
            for (int i = 0; i < length; ++i) {
                this.fSchemaLoader.loadGrammar(new XMLInputSource(null, uriList.item(i), null));
            }
            return this.fGrammarPool.toXSModel();
        }
        catch (final Exception e) {
            this.fSchemaLoader.reportDOMFatalError(e);
            return null;
        }
    }
    
    @Override
    public XSModel loadInputList(final LSInputList is) {
        final int length = is.getLength();
        try {
            this.fGrammarPool.clear();
            for (int i = 0; i < length; ++i) {
                this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(is.item(i)));
            }
            return this.fGrammarPool.toXSModel();
        }
        catch (final Exception e) {
            this.fSchemaLoader.reportDOMFatalError(e);
            return null;
        }
    }
    
    @Override
    public XSModel loadURI(final String uri) {
        try {
            this.fGrammarPool.clear();
            return ((XSGrammar)this.fSchemaLoader.loadGrammar(new XMLInputSource(null, uri, null))).toXSModel();
        }
        catch (final Exception e) {
            this.fSchemaLoader.reportDOMFatalError(e);
            return null;
        }
    }
    
    @Override
    public XSModel load(final LSInput is) {
        try {
            this.fGrammarPool.clear();
            return ((XSGrammar)this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(is))).toXSModel();
        }
        catch (final Exception e) {
            this.fSchemaLoader.reportDOMFatalError(e);
            return null;
        }
    }
    
    @Override
    public void setParameter(final String name, final Object value) throws DOMException {
        this.fSchemaLoader.setParameter(name, value);
    }
    
    @Override
    public Object getParameter(final String name) throws DOMException {
        return this.fSchemaLoader.getParameter(name);
    }
    
    @Override
    public boolean canSetParameter(final String name, final Object value) {
        return this.fSchemaLoader.canSetParameter(name, value);
    }
    
    @Override
    public DOMStringList getParameterNames() {
        return this.fSchemaLoader.getParameterNames();
    }
    
    private static final class XSGrammarMerger extends XSGrammarPool
    {
        public XSGrammarMerger() {
        }
        
        @Override
        public void putGrammar(final Grammar grammar) {
            final SchemaGrammar cachedGrammar = this.toSchemaGrammar(super.getGrammar(grammar.getGrammarDescription()));
            if (cachedGrammar != null) {
                final SchemaGrammar newGrammar = this.toSchemaGrammar(grammar);
                if (newGrammar != null) {
                    this.mergeSchemaGrammars(cachedGrammar, newGrammar);
                }
            }
            else {
                super.putGrammar(grammar);
            }
        }
        
        private SchemaGrammar toSchemaGrammar(final Grammar grammar) {
            return (grammar instanceof SchemaGrammar) ? ((SchemaGrammar)grammar) : null;
        }
        
        private void mergeSchemaGrammars(final SchemaGrammar cachedGrammar, final SchemaGrammar newGrammar) {
            XSNamedMap map = newGrammar.getComponents((short)2);
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final XSElementDecl decl = (XSElementDecl)map.item(i);
                if (cachedGrammar.getGlobalElementDecl(decl.getName()) == null) {
                    cachedGrammar.addGlobalElementDecl(decl);
                }
            }
            map = newGrammar.getComponents((short)1);
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final XSAttributeDecl decl2 = (XSAttributeDecl)map.item(i);
                if (cachedGrammar.getGlobalAttributeDecl(decl2.getName()) == null) {
                    cachedGrammar.addGlobalAttributeDecl(decl2);
                }
            }
            map = newGrammar.getComponents((short)3);
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final XSTypeDefinition decl3 = (XSTypeDefinition)map.item(i);
                if (cachedGrammar.getGlobalTypeDecl(decl3.getName()) == null) {
                    cachedGrammar.addGlobalTypeDecl(decl3);
                }
            }
            map = newGrammar.getComponents((short)5);
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final XSAttributeGroupDecl decl4 = (XSAttributeGroupDecl)map.item(i);
                if (cachedGrammar.getGlobalAttributeGroupDecl(decl4.getName()) == null) {
                    cachedGrammar.addGlobalAttributeGroupDecl(decl4);
                }
            }
            map = newGrammar.getComponents((short)7);
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final XSGroupDecl decl5 = (XSGroupDecl)map.item(i);
                if (cachedGrammar.getGlobalGroupDecl(decl5.getName()) == null) {
                    cachedGrammar.addGlobalGroupDecl(decl5);
                }
            }
            map = newGrammar.getComponents((short)11);
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final XSNotationDecl decl6 = (XSNotationDecl)map.item(i);
                if (cachedGrammar.getGlobalNotationDecl(decl6.getName()) == null) {
                    cachedGrammar.addGlobalNotationDecl(decl6);
                }
            }
            final XSObjectList annotations = newGrammar.getAnnotations();
            for (int length = annotations.getLength(), j = 0; j < length; ++j) {
                cachedGrammar.addAnnotation((XSAnnotationImpl)annotations.item(j));
            }
        }
        
        @Override
        public boolean containsGrammar(final XMLGrammarDescription desc) {
            return false;
        }
        
        @Override
        public Grammar getGrammar(final XMLGrammarDescription desc) {
            return null;
        }
        
        @Override
        public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
            return null;
        }
        
        @Override
        public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
            return new Grammar[0];
        }
    }
}
