package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.StringTokenizer;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.Map;

final class SymbolTable
{
    private final Map<String, Stylesheet> _stylesheets;
    private final Map<String, Vector> _primops;
    private Map<String, VariableBase> _variables;
    private Map<String, Template> _templates;
    private Map<String, AttributeSet> _attributeSets;
    private Map<String, String> _aliases;
    private Map<String, Integer> _excludedURI;
    private Stack<Map<String, Integer>> _excludedURIStack;
    private Map<String, DecimalFormatting> _decimalFormats;
    private Map<String, Key> _keys;
    private int _nsCounter;
    private SyntaxTreeNode _current;
    
    SymbolTable() {
        this._stylesheets = new HashMap<String, Stylesheet>();
        this._primops = new HashMap<String, Vector>();
        this._variables = null;
        this._templates = null;
        this._attributeSets = null;
        this._aliases = null;
        this._excludedURI = null;
        this._excludedURIStack = null;
        this._decimalFormats = null;
        this._keys = null;
        this._nsCounter = 0;
        this._current = null;
    }
    
    public DecimalFormatting getDecimalFormatting(final QName name) {
        if (this._decimalFormats == null) {
            return null;
        }
        return this._decimalFormats.get(name.getStringRep());
    }
    
    public void addDecimalFormatting(final QName name, final DecimalFormatting symbols) {
        if (this._decimalFormats == null) {
            this._decimalFormats = new HashMap<String, DecimalFormatting>();
        }
        this._decimalFormats.put(name.getStringRep(), symbols);
    }
    
    public Key getKey(final QName name) {
        if (this._keys == null) {
            return null;
        }
        return this._keys.get(name.getStringRep());
    }
    
    public void addKey(final QName name, final Key key) {
        if (this._keys == null) {
            this._keys = new HashMap<String, Key>();
        }
        this._keys.put(name.getStringRep(), key);
    }
    
    public Stylesheet addStylesheet(final QName name, final Stylesheet node) {
        return this._stylesheets.put(name.getStringRep(), node);
    }
    
    public Stylesheet lookupStylesheet(final QName name) {
        return this._stylesheets.get(name.getStringRep());
    }
    
    public Template addTemplate(final Template template) {
        final QName name = template.getName();
        if (this._templates == null) {
            this._templates = new HashMap<String, Template>();
        }
        return this._templates.put(name.getStringRep(), template);
    }
    
    public Template lookupTemplate(final QName name) {
        if (this._templates == null) {
            return null;
        }
        return this._templates.get(name.getStringRep());
    }
    
    public Variable addVariable(final Variable variable) {
        if (this._variables == null) {
            this._variables = new HashMap<String, VariableBase>();
        }
        final String name = variable.getName().getStringRep();
        return this._variables.put(name, variable);
    }
    
    public Param addParam(final Param parameter) {
        if (this._variables == null) {
            this._variables = new HashMap<String, VariableBase>();
        }
        final String name = parameter.getName().getStringRep();
        return this._variables.put(name, parameter);
    }
    
    public Variable lookupVariable(final QName qname) {
        if (this._variables == null) {
            return null;
        }
        final String name = qname.getStringRep();
        final VariableBase obj = this._variables.get(name);
        return (obj instanceof Variable) ? ((Variable)obj) : null;
    }
    
    public Param lookupParam(final QName qname) {
        if (this._variables == null) {
            return null;
        }
        final String name = qname.getStringRep();
        final VariableBase obj = this._variables.get(name);
        return (obj instanceof Param) ? ((Param)obj) : null;
    }
    
    public SyntaxTreeNode lookupName(final QName qname) {
        if (this._variables == null) {
            return null;
        }
        final String name = qname.getStringRep();
        return this._variables.get(name);
    }
    
    public AttributeSet addAttributeSet(final AttributeSet atts) {
        if (this._attributeSets == null) {
            this._attributeSets = new HashMap<String, AttributeSet>();
        }
        return this._attributeSets.put(atts.getName().getStringRep(), atts);
    }
    
    public AttributeSet lookupAttributeSet(final QName name) {
        if (this._attributeSets == null) {
            return null;
        }
        return this._attributeSets.get(name.getStringRep());
    }
    
    public void addPrimop(final String name, final MethodType mtype) {
        Vector methods = this._primops.get(name);
        if (methods == null) {
            this._primops.put(name, methods = new Vector());
        }
        methods.addElement(mtype);
    }
    
    public Vector lookupPrimop(final String name) {
        return this._primops.get(name);
    }
    
    public String generateNamespacePrefix() {
        return "ns" + this._nsCounter++;
    }
    
    public void setCurrentNode(final SyntaxTreeNode node) {
        this._current = node;
    }
    
    public String lookupNamespace(final String prefix) {
        if (this._current == null) {
            return "";
        }
        return this._current.lookupNamespace(prefix);
    }
    
    public void addPrefixAlias(final String prefix, final String alias) {
        if (this._aliases == null) {
            this._aliases = new HashMap<String, String>();
        }
        this._aliases.put(prefix, alias);
    }
    
    public String lookupPrefixAlias(final String prefix) {
        if (this._aliases == null) {
            return null;
        }
        return this._aliases.get(prefix);
    }
    
    public void excludeURI(final String uri) {
        if (uri == null) {
            return;
        }
        if (this._excludedURI == null) {
            this._excludedURI = new HashMap<String, Integer>();
        }
        Integer refcnt = this._excludedURI.get(uri);
        if (refcnt == null) {
            refcnt = 1;
        }
        else {
            ++refcnt;
        }
        this._excludedURI.put(uri, refcnt);
    }
    
    public void excludeNamespaces(final String prefixes) {
        if (prefixes != null) {
            final StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                String uri;
                if (prefix.equals("#default")) {
                    uri = this.lookupNamespace("");
                }
                else {
                    uri = this.lookupNamespace(prefix);
                }
                if (uri != null) {
                    this.excludeURI(uri);
                }
            }
        }
    }
    
    public boolean isExcludedNamespace(final String uri) {
        if (uri != null && this._excludedURI != null) {
            final Integer refcnt = this._excludedURI.get(uri);
            return refcnt != null && refcnt > 0;
        }
        return false;
    }
    
    public void unExcludeNamespaces(final String prefixes) {
        if (this._excludedURI == null) {
            return;
        }
        if (prefixes != null) {
            final StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                String uri;
                if (prefix.equals("#default")) {
                    uri = this.lookupNamespace("");
                }
                else {
                    uri = this.lookupNamespace(prefix);
                }
                final Integer refcnt = this._excludedURI.get(uri);
                if (refcnt != null) {
                    this._excludedURI.put(uri, refcnt - 1);
                }
            }
        }
    }
    
    public void pushExcludedNamespacesContext() {
        if (this._excludedURIStack == null) {
            this._excludedURIStack = new Stack<Map<String, Integer>>();
        }
        this._excludedURIStack.push(this._excludedURI);
        this._excludedURI = null;
    }
    
    public void popExcludedNamespacesContext() {
        this._excludedURI = this._excludedURIStack.pop();
        if (this._excludedURIStack.isEmpty()) {
            this._excludedURIStack = null;
        }
    }
}
