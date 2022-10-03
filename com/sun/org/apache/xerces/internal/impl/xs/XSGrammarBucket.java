package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

public class XSGrammarBucket
{
    Map<String, SchemaGrammar> fGrammarRegistry;
    SchemaGrammar fNoNSGrammar;
    
    public XSGrammarBucket() {
        this.fGrammarRegistry = new HashMap<String, SchemaGrammar>();
        this.fNoNSGrammar = null;
    }
    
    public SchemaGrammar getGrammar(final String namespace) {
        if (namespace == null) {
            return this.fNoNSGrammar;
        }
        return this.fGrammarRegistry.get(namespace);
    }
    
    public void putGrammar(final SchemaGrammar grammar) {
        if (grammar.getTargetNamespace() == null) {
            this.fNoNSGrammar = grammar;
        }
        else {
            this.fGrammarRegistry.put(grammar.getTargetNamespace(), grammar);
        }
    }
    
    public boolean putGrammar(final SchemaGrammar grammar, final boolean deep) {
        final SchemaGrammar sg = this.getGrammar(grammar.fTargetNamespace);
        if (sg != null) {
            return sg == grammar;
        }
        if (!deep) {
            this.putGrammar(grammar);
            return true;
        }
        final Vector currGrammars = grammar.getImportedGrammars();
        if (currGrammars == null) {
            this.putGrammar(grammar);
            return true;
        }
        final Vector grammars = (Vector)currGrammars.clone();
        for (int i = 0; i < grammars.size(); ++i) {
            final SchemaGrammar sg2 = grammars.elementAt(i);
            SchemaGrammar sg3 = this.getGrammar(sg2.fTargetNamespace);
            if (sg3 == null) {
                final Vector gs = sg2.getImportedGrammars();
                if (gs != null) {
                    for (int j = gs.size() - 1; j >= 0; --j) {
                        sg3 = gs.elementAt(j);
                        if (!grammars.contains(sg3)) {
                            grammars.addElement(sg3);
                        }
                    }
                }
            }
            else if (sg3 != sg2) {
                return false;
            }
        }
        this.putGrammar(grammar);
        for (int i = grammars.size() - 1; i >= 0; --i) {
            this.putGrammar(grammars.elementAt(i));
        }
        return true;
    }
    
    public boolean putGrammar(final SchemaGrammar grammar, final boolean deep, final boolean ignoreConflict) {
        if (!ignoreConflict) {
            return this.putGrammar(grammar, deep);
        }
        final SchemaGrammar sg = this.getGrammar(grammar.fTargetNamespace);
        if (sg == null) {
            this.putGrammar(grammar);
        }
        if (!deep) {
            return true;
        }
        final Vector currGrammars = grammar.getImportedGrammars();
        if (currGrammars == null) {
            return true;
        }
        final Vector grammars = (Vector)currGrammars.clone();
        for (int i = 0; i < grammars.size(); ++i) {
            final SchemaGrammar sg2 = grammars.elementAt(i);
            SchemaGrammar sg3 = this.getGrammar(sg2.fTargetNamespace);
            if (sg3 == null) {
                final Vector gs = sg2.getImportedGrammars();
                if (gs != null) {
                    for (int j = gs.size() - 1; j >= 0; --j) {
                        sg3 = gs.elementAt(j);
                        if (!grammars.contains(sg3)) {
                            grammars.addElement(sg3);
                        }
                    }
                }
            }
            else {
                grammars.remove(sg2);
            }
        }
        for (int i = grammars.size() - 1; i >= 0; --i) {
            this.putGrammar(grammars.elementAt(i));
        }
        return true;
    }
    
    public SchemaGrammar[] getGrammars() {
        final int count = this.fGrammarRegistry.size() + ((this.fNoNSGrammar != null) ? 1 : 0);
        final SchemaGrammar[] grammars = new SchemaGrammar[count];
        int i = 0;
        for (final Map.Entry<String, SchemaGrammar> entry : this.fGrammarRegistry.entrySet()) {
            grammars[i++] = entry.getValue();
        }
        if (this.fNoNSGrammar != null) {
            grammars[count - 1] = this.fNoNSGrammar;
        }
        return grammars;
    }
    
    public void reset() {
        this.fNoNSGrammar = null;
        this.fGrammarRegistry.clear();
    }
}
