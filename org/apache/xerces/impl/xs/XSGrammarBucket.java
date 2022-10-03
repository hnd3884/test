package org.apache.xerces.impl.xs;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

public class XSGrammarBucket
{
    Hashtable fGrammarRegistry;
    SchemaGrammar fNoNSGrammar;
    
    public XSGrammarBucket() {
        this.fGrammarRegistry = new Hashtable();
        this.fNoNSGrammar = null;
    }
    
    public SchemaGrammar getGrammar(final String s) {
        if (s == null) {
            return this.fNoNSGrammar;
        }
        return this.fGrammarRegistry.get(s);
    }
    
    public void putGrammar(final SchemaGrammar fNoNSGrammar) {
        if (fNoNSGrammar.getTargetNamespace() == null) {
            this.fNoNSGrammar = fNoNSGrammar;
        }
        else {
            this.fGrammarRegistry.put(fNoNSGrammar.getTargetNamespace(), fNoNSGrammar);
        }
    }
    
    public boolean putGrammar(final SchemaGrammar schemaGrammar, final boolean b) {
        final SchemaGrammar grammar = this.getGrammar(schemaGrammar.fTargetNamespace);
        if (grammar != null) {
            return grammar == schemaGrammar;
        }
        if (!b) {
            this.putGrammar(schemaGrammar);
            return true;
        }
        final Vector importedGrammars = schemaGrammar.getImportedGrammars();
        if (importedGrammars == null) {
            this.putGrammar(schemaGrammar);
            return true;
        }
        final Vector vector = (Vector)importedGrammars.clone();
        for (int i = 0; i < vector.size(); ++i) {
            final SchemaGrammar schemaGrammar2 = vector.elementAt(i);
            final SchemaGrammar grammar2 = this.getGrammar(schemaGrammar2.fTargetNamespace);
            if (grammar2 == null) {
                final Vector importedGrammars2 = schemaGrammar2.getImportedGrammars();
                if (importedGrammars2 != null) {
                    for (int j = importedGrammars2.size() - 1; j >= 0; --j) {
                        final SchemaGrammar schemaGrammar3 = importedGrammars2.elementAt(j);
                        if (!vector.contains(schemaGrammar3)) {
                            vector.addElement(schemaGrammar3);
                        }
                    }
                }
            }
            else if (grammar2 != schemaGrammar2) {
                return false;
            }
        }
        this.putGrammar(schemaGrammar);
        for (int k = vector.size() - 1; k >= 0; --k) {
            this.putGrammar((SchemaGrammar)vector.elementAt(k));
        }
        return true;
    }
    
    public boolean putGrammar(final SchemaGrammar schemaGrammar, final boolean b, final boolean b2) {
        if (!b2) {
            return this.putGrammar(schemaGrammar, b);
        }
        if (this.getGrammar(schemaGrammar.fTargetNamespace) == null) {
            this.putGrammar(schemaGrammar);
        }
        if (!b) {
            return true;
        }
        final Vector importedGrammars = schemaGrammar.getImportedGrammars();
        if (importedGrammars == null) {
            return true;
        }
        final Vector vector = (Vector)importedGrammars.clone();
        for (int i = 0; i < vector.size(); ++i) {
            final SchemaGrammar schemaGrammar2 = vector.elementAt(i);
            if (this.getGrammar(schemaGrammar2.fTargetNamespace) == null) {
                final Vector importedGrammars2 = schemaGrammar2.getImportedGrammars();
                if (importedGrammars2 != null) {
                    for (int j = importedGrammars2.size() - 1; j >= 0; --j) {
                        final SchemaGrammar schemaGrammar3 = importedGrammars2.elementAt(j);
                        if (!vector.contains(schemaGrammar3)) {
                            vector.addElement(schemaGrammar3);
                        }
                    }
                }
            }
            else {
                vector.remove(schemaGrammar2);
            }
        }
        for (int k = vector.size() - 1; k >= 0; --k) {
            this.putGrammar((SchemaGrammar)vector.elementAt(k));
        }
        return true;
    }
    
    public SchemaGrammar[] getGrammars() {
        final int n = this.fGrammarRegistry.size() + ((this.fNoNSGrammar != null) ? 1 : 0);
        final SchemaGrammar[] array = new SchemaGrammar[n];
        final Enumeration elements = this.fGrammarRegistry.elements();
        int n2 = 0;
        while (elements.hasMoreElements()) {
            array[n2++] = (SchemaGrammar)elements.nextElement();
        }
        if (this.fNoNSGrammar != null) {
            array[n - 1] = this.fNoNSGrammar;
        }
        return array;
    }
    
    public void reset() {
        this.fNoNSGrammar = null;
        this.fGrammarRegistry.clear();
    }
}
