package org.apache.commons.collections4.bidimap;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.BidiMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Serializable;

public class DualLinkedHashBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 721969328361810L;
    
    public DualLinkedHashBidiMap() {
        super(new LinkedHashMap(), new LinkedHashMap());
    }
    
    public DualLinkedHashBidiMap(final Map<? extends K, ? extends V> map) {
        super(new LinkedHashMap(), new LinkedHashMap());
        this.putAll(map);
    }
    
    protected DualLinkedHashBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap, final BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }
    
    @Override
    protected BidiMap<V, K> createBidiMap(final Map<V, K> normalMap, final Map<K, V> reverseMap, final BidiMap<K, V> inverseBidiMap) {
        return (BidiMap<V, K>)new DualLinkedHashBidiMap((Map<Object, Object>)normalMap, (Map<Object, Object>)reverseMap, (BidiMap<Object, Object>)inverseBidiMap);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.normalMap);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.normalMap = (Map<K, V>)new LinkedHashMap<Object, Object>();
        this.reverseMap = (Map<V, K>)new LinkedHashMap<Object, Object>();
        final Map<K, V> map = (Map<K, V>)in.readObject();
        this.putAll((Map<? extends K, ? extends V>)map);
    }
}
