package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xni.grammars.Grammar;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.DOMException;
import org.w3c.dom.ls.LSInput;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.impl.xs.util.XSGrammarPool;
import org.w3c.dom.DOMConfiguration;
import org.apache.xerces.xs.XSLoader;

public final class XSLoaderImpl implements XSLoader, DOMConfiguration
{
    private final XSGrammarPool fGrammarPool;
    private final XMLSchemaLoader fSchemaLoader;
    
    public XSLoaderImpl() {
        this.fGrammarPool = new XSGrammarMerger();
        (this.fSchemaLoader = new XMLSchemaLoader()).setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
    }
    
    public DOMConfiguration getConfig() {
        return this;
    }
    
    public XSModel loadURIList(final StringList list) {
        final int length = list.getLength();
        try {
            this.fGrammarPool.clear();
            for (int i = 0; i < length; ++i) {
                this.fSchemaLoader.loadGrammar(new XMLInputSource(null, list.item(i), null));
            }
            return this.fGrammarPool.toXSModel();
        }
        catch (final Exception ex) {
            this.fSchemaLoader.reportDOMFatalError(ex);
            return null;
        }
    }
    
    public XSModel loadInputList(final LSInputList list) {
        final int length = list.getLength();
        try {
            this.fGrammarPool.clear();
            for (int i = 0; i < length; ++i) {
                this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(list.item(i)));
            }
            return this.fGrammarPool.toXSModel();
        }
        catch (final Exception ex) {
            this.fSchemaLoader.reportDOMFatalError(ex);
            return null;
        }
    }
    
    public XSModel loadURI(final String s) {
        try {
            this.fGrammarPool.clear();
            return ((XSGrammar)this.fSchemaLoader.loadGrammar(new XMLInputSource(null, s, null))).toXSModel();
        }
        catch (final Exception ex) {
            this.fSchemaLoader.reportDOMFatalError(ex);
            return null;
        }
    }
    
    public XSModel load(final LSInput lsInput) {
        try {
            this.fGrammarPool.clear();
            return ((XSGrammar)this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(lsInput))).toXSModel();
        }
        catch (final Exception ex) {
            this.fSchemaLoader.reportDOMFatalError(ex);
            return null;
        }
    }
    
    public void setParameter(final String s, final Object o) throws DOMException {
        this.fSchemaLoader.setParameter(s, o);
    }
    
    public Object getParameter(final String s) throws DOMException {
        return this.fSchemaLoader.getParameter(s);
    }
    
    public boolean canSetParameter(final String s, final Object o) {
        return this.fSchemaLoader.canSetParameter(s, o);
    }
    
    public DOMStringList getParameterNames() {
        return this.fSchemaLoader.getParameterNames();
    }
    
    private static final class XSGrammarMerger extends XSGrammarPool
    {
        public XSGrammarMerger() {
        }
        
        public void putGrammar(final Grammar grammar) {
            final SchemaGrammar schemaGrammar = this.toSchemaGrammar(super.getGrammar(grammar.getGrammarDescription()));
            if (schemaGrammar != null) {
                final SchemaGrammar schemaGrammar2 = this.toSchemaGrammar(grammar);
                if (schemaGrammar2 != null) {
                    this.mergeSchemaGrammars(schemaGrammar, schemaGrammar2);
                }
            }
            else {
                super.putGrammar(grammar);
            }
        }
        
        private SchemaGrammar toSchemaGrammar(final Grammar grammar) {
            return (grammar instanceof SchemaGrammar) ? ((SchemaGrammar)grammar) : null;
        }
        
        private void mergeSchemaGrammars(final SchemaGrammar schemaGrammar, final SchemaGrammar schemaGrammar2) {
            final XSNamedMap components = schemaGrammar2.getComponents((short)2);
            for (int length = components.getLength(), i = 0; i < length; ++i) {
                final XSElementDecl xsElementDecl = (XSElementDecl)components.item(i);
                if (schemaGrammar.getGlobalElementDecl(xsElementDecl.getName()) == null) {
                    schemaGrammar.addGlobalElementDecl(xsElementDecl);
                }
            }
            final XSNamedMap components2 = schemaGrammar2.getComponents((short)1);
            for (int length2 = components2.getLength(), j = 0; j < length2; ++j) {
                final XSAttributeDecl xsAttributeDecl = (XSAttributeDecl)components2.item(j);
                if (schemaGrammar.getGlobalAttributeDecl(xsAttributeDecl.getName()) == null) {
                    schemaGrammar.addGlobalAttributeDecl(xsAttributeDecl);
                }
            }
            final XSNamedMap components3 = schemaGrammar2.getComponents((short)3);
            for (int length3 = components3.getLength(), k = 0; k < length3; ++k) {
                final XSTypeDefinition xsTypeDefinition = (XSTypeDefinition)components3.item(k);
                if (schemaGrammar.getGlobalTypeDecl(xsTypeDefinition.getName()) == null) {
                    schemaGrammar.addGlobalTypeDecl(xsTypeDefinition);
                }
            }
            final XSNamedMap components4 = schemaGrammar2.getComponents((short)5);
            for (int length4 = components4.getLength(), l = 0; l < length4; ++l) {
                final XSAttributeGroupDecl xsAttributeGroupDecl = (XSAttributeGroupDecl)components4.item(l);
                if (schemaGrammar.getGlobalAttributeGroupDecl(xsAttributeGroupDecl.getName()) == null) {
                    schemaGrammar.addGlobalAttributeGroupDecl(xsAttributeGroupDecl);
                }
            }
            final XSNamedMap components5 = schemaGrammar2.getComponents((short)7);
            for (int length5 = components5.getLength(), n = 0; n < length5; ++n) {
                final XSGroupDecl xsGroupDecl = (XSGroupDecl)components5.item(n);
                if (schemaGrammar.getGlobalGroupDecl(xsGroupDecl.getName()) == null) {
                    schemaGrammar.addGlobalGroupDecl(xsGroupDecl);
                }
            }
            final XSNamedMap components6 = schemaGrammar2.getComponents((short)11);
            for (int length6 = components6.getLength(), n2 = 0; n2 < length6; ++n2) {
                final XSNotationDecl xsNotationDecl = (XSNotationDecl)components6.item(n2);
                if (schemaGrammar.getGlobalNotationDecl(xsNotationDecl.getName()) == null) {
                    schemaGrammar.addGlobalNotationDecl(xsNotationDecl);
                }
            }
            final XSObjectList annotations = schemaGrammar2.getAnnotations();
            for (int length7 = annotations.getLength(), n3 = 0; n3 < length7; ++n3) {
                schemaGrammar.addAnnotation((XSAnnotationImpl)annotations.item(n3));
            }
        }
        
        public boolean containsGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            return false;
        }
        
        public Grammar getGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            return null;
        }
        
        public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            return null;
        }
        
        public Grammar[] retrieveInitialGrammarSet(final String s) {
            return new Grammar[0];
        }
    }
}
