package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.impl.xs.XSModelImpl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;

public class XSGrammarPool extends XMLGrammarPoolImpl
{
    public XSModel toXSModel() {
        return this.toXSModel((short)1);
    }
    
    public XSModel toXSModel(final short schemaVersion) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < this.fGrammars.length; ++i) {
            for (Entry entry = this.fGrammars[i]; entry != null; entry = entry.next) {
                if (entry.desc.getGrammarType().equals("http://www.w3.org/2001/XMLSchema")) {
                    list.add(entry.grammar);
                }
            }
        }
        final int size = list.size();
        if (size == 0) {
            return this.toXSModel(new SchemaGrammar[0], schemaVersion);
        }
        final SchemaGrammar[] gs = list.toArray(new SchemaGrammar[size]);
        return this.toXSModel(gs, schemaVersion);
    }
    
    protected XSModel toXSModel(final SchemaGrammar[] grammars, final short schemaVersion) {
        return new XSModelImpl(grammars, schemaVersion);
    }
}
