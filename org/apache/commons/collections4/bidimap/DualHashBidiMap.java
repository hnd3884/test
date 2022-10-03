package org.apache.commons.collections4.bidimap;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.BidiMap;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class DualHashBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 721969328361808L;
    
    public DualHashBidiMap() {
        super(new HashMap(), new HashMap());
    }
    
    public DualHashBidiMap(final Map<? extends K, ? extends V> map) {
        super(new HashMap(), new HashMap());
        this.putAll(map);
    }
    
    protected DualHashBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap, final BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }
    
    @Override
    protected BidiMap<V, K> createBidiMap(final Map<V, K> normalMap, final Map<K, V> reverseMap, final BidiMap<K, V> inverseBidiMap) {
        return (BidiMap<V, K>)new DualHashBidiMap((Map<Object, Object>)normalMap, (Map<Object, Object>)reverseMap, (BidiMap<Object, Object>)inverseBidiMap);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.normalMap);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.normalMap = (Map<K, V>)new HashMap<Object, Object>();
        this.reverseMap = (Map<V, K>)new HashMap<Object, Object>();
        final Map<K, V> map = (Map<K, V>)in.readObject();
        this.putAll((Map<? extends K, ? extends V>)map);
    }
}
