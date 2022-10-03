package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import java.lang.ref.WeakReference;

final class WeakReferenceXMLSchema extends AbstractXMLSchema
{
    private WeakReference fGrammarPool;
    
    public WeakReferenceXMLSchema() {
        this.fGrammarPool = new WeakReference(null);
    }
    
    @Override
    public synchronized XMLGrammarPool getGrammarPool() {
        XMLGrammarPool grammarPool = (XMLGrammarPool)this.fGrammarPool.get();
        if (grammarPool == null) {
            grammarPool = new SoftReferenceGrammarPool();
            this.fGrammarPool = new WeakReference((T)grammarPool);
        }
        return grammarPool;
    }
    
    @Override
    public boolean isFullyComposed() {
        return false;
    }
}
