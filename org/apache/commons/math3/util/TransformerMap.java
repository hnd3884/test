package org.apache.commons.math3.util;

import java.util.Iterator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class TransformerMap implements NumberTransformer, Serializable
{
    private static final long serialVersionUID = 4605318041528645258L;
    private NumberTransformer defaultTransformer;
    private Map<Class<?>, NumberTransformer> map;
    
    public TransformerMap() {
        this.defaultTransformer = null;
        this.map = null;
        this.map = new HashMap<Class<?>, NumberTransformer>();
        this.defaultTransformer = new DefaultTransformer();
    }
    
    public boolean containsClass(final Class<?> key) {
        return this.map.containsKey(key);
    }
    
    public boolean containsTransformer(final NumberTransformer value) {
        return this.map.containsValue(value);
    }
    
    public NumberTransformer getTransformer(final Class<?> key) {
        return this.map.get(key);
    }
    
    public NumberTransformer putTransformer(final Class<?> key, final NumberTransformer transformer) {
        return this.map.put(key, transformer);
    }
    
    public NumberTransformer removeTransformer(final Class<?> key) {
        return this.map.remove(key);
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public Set<Class<?>> classes() {
        return this.map.keySet();
    }
    
    public Collection<NumberTransformer> transformers() {
        return this.map.values();
    }
    
    public double transform(final Object o) throws MathIllegalArgumentException {
        double value = Double.NaN;
        if (o instanceof Number || o instanceof String) {
            value = this.defaultTransformer.transform(o);
        }
        else {
            final NumberTransformer trans = this.getTransformer(o.getClass());
            if (trans != null) {
                value = trans.transform(o);
            }
        }
        return value;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TransformerMap)) {
            return false;
        }
        final TransformerMap rhs = (TransformerMap)other;
        if (!this.defaultTransformer.equals(rhs.defaultTransformer)) {
            return false;
        }
        if (this.map.size() != rhs.map.size()) {
            return false;
        }
        for (final Map.Entry<Class<?>, NumberTransformer> entry : this.map.entrySet()) {
            if (!entry.getValue().equals(rhs.map.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = this.defaultTransformer.hashCode();
        for (final NumberTransformer t : this.map.values()) {
            hash = hash * 31 + t.hashCode();
        }
        return hash;
    }
}
