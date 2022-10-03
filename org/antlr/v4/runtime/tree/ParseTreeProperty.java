package org.antlr.v4.runtime.tree;

import java.util.IdentityHashMap;
import java.util.Map;

public class ParseTreeProperty<V>
{
    protected Map<ParseTree, V> annotations;
    
    public ParseTreeProperty() {
        this.annotations = new IdentityHashMap<ParseTree, V>();
    }
    
    public V get(final ParseTree node) {
        return this.annotations.get(node);
    }
    
    public void put(final ParseTree node, final V value) {
        this.annotations.put(node, value);
    }
    
    public V removeFrom(final ParseTree node) {
        return this.annotations.remove(node);
    }
}
