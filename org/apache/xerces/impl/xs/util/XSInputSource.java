package org.apache.xerces.impl.xs.util;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class XSInputSource extends XMLInputSource
{
    private SchemaGrammar[] fGrammars;
    private XSObject[] fComponents;
    
    public XSInputSource(final SchemaGrammar[] fGrammars) {
        super(null, null, null);
        this.fGrammars = fGrammars;
        this.fComponents = null;
    }
    
    public XSInputSource(final XSObject[] fComponents) {
        super(null, null, null);
        this.fGrammars = null;
        this.fComponents = fComponents;
    }
    
    public SchemaGrammar[] getGrammars() {
        return this.fGrammars;
    }
    
    public void setGrammars(final SchemaGrammar[] fGrammars) {
        this.fGrammars = fGrammars;
    }
    
    public XSObject[] getComponents() {
        return this.fComponents;
    }
    
    public void setComponents(final XSObject[] fComponents) {
        this.fComponents = fComponents;
    }
}
