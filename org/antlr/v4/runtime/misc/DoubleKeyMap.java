package org.antlr.v4.runtime.misc;

import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DoubleKeyMap<Key1, Key2, Value>
{
    Map<Key1, Map<Key2, Value>> data;
    
    public DoubleKeyMap() {
        this.data = new LinkedHashMap<Key1, Map<Key2, Value>>();
    }
    
    public Value put(final Key1 k1, final Key2 k2, final Value v) {
        Map<Key2, Value> data2 = this.data.get(k1);
        Value prev = null;
        if (data2 == null) {
            data2 = new LinkedHashMap<Key2, Value>();
            this.data.put(k1, data2);
        }
        else {
            prev = data2.get(k2);
        }
        data2.put(k2, v);
        return prev;
    }
    
    public Value get(final Key1 k1, final Key2 k2) {
        final Map<Key2, Value> data2 = this.data.get(k1);
        if (data2 == null) {
            return null;
        }
        return data2.get(k2);
    }
    
    public Map<Key2, Value> get(final Key1 k1) {
        return this.data.get(k1);
    }
    
    public Collection<Value> values(final Key1 k1) {
        final Map<Key2, Value> data2 = this.data.get(k1);
        if (data2 == null) {
            return null;
        }
        return data2.values();
    }
    
    public Set<Key1> keySet() {
        return this.data.keySet();
    }
    
    public Set<Key2> keySet(final Key1 k1) {
        final Map<Key2, Value> data2 = this.data.get(k1);
        if (data2 == null) {
            return null;
        }
        return data2.keySet();
    }
}
