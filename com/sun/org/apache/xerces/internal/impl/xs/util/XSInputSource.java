package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

public final class XSInputSource extends XMLInputSource
{
    private SchemaGrammar[] fGrammars;
    private XSObject[] fComponents;
    
    public XSInputSource(final SchemaGrammar[] grammars) {
        super(null, null, null);
        this.fGrammars = grammars;
        this.fComponents = null;
    }
    
    public XSInputSource(final XSObject[] component) {
        super(null, null, null);
        this.fGrammars = null;
        this.fComponents = component;
    }
    
    public SchemaGrammar[] getGrammars() {
        return this.fGrammars;
    }
    
    public void setGrammars(final SchemaGrammar[] grammars) {
        this.fGrammars = grammars;
    }
    
    public XSObject[] getComponents() {
        return this.fComponents;
    }
    
    public void setComponents(final XSObject[] components) {
        this.fComponents = components;
    }
}
