package org.apache.xerces.impl.xs.util;

import org.apache.xerces.impl.xs.XSModelImpl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import java.util.ArrayList;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.util.XMLGrammarPoolImpl;

public class XSGrammarPool extends XMLGrammarPoolImpl
{
    public XSModel toXSModel() {
        return this.toXSModel((short)1);
    }
    
    public XSModel toXSModel(final short n) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < this.fGrammars.length; ++i) {
            for (Entry next = this.fGrammars[i]; next != null; next = next.next) {
                if (next.desc.getGrammarType().equals("http://www.w3.org/2001/XMLSchema")) {
                    list.add(next.grammar);
                }
            }
        }
        final int size = list.size();
        if (size == 0) {
            return this.toXSModel(new SchemaGrammar[0], n);
        }
        return this.toXSModel(list.toArray(new SchemaGrammar[size]), n);
    }
    
    protected XSModel toXSModel(final SchemaGrammar[] array, final short n) {
        return new XSModelImpl(array, n);
    }
}
