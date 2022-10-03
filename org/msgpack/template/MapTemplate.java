package org.msgpack.template;

import java.util.HashMap;
import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import java.util.Iterator;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.util.Map;

public class MapTemplate<K, V> extends AbstractTemplate<Map<K, V>>
{
    private Template<K> keyTemplate;
    private Template<V> valueTemplate;
    
    public MapTemplate(final Template<K> keyTemplate, final Template<V> valueTemplate) {
        this.keyTemplate = keyTemplate;
        this.valueTemplate = valueTemplate;
    }
    
    @Override
    public void write(final Packer pk, final Map<K, V> target, final boolean required) throws IOException {
        if (target instanceof Map) {
            final Map<K, V> map = target;
            pk.writeMapBegin(map.size());
            for (final Map.Entry<K, V> pair : map.entrySet()) {
                this.keyTemplate.write(pk, pair.getKey());
                this.valueTemplate.write(pk, pair.getValue());
            }
            pk.writeMapEnd();
            return;
        }
        if (target != null) {
            throw new MessageTypeException("Target is not a Map but " + target.getClass());
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Map<K, V> read(final Unpacker u, final Map<K, V> to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final int n = u.readMapBegin();
        Map<K, V> map;
        if (to != null) {
            map = to;
            map.clear();
        }
        else {
            map = new HashMap<K, V>(n);
        }
        for (int i = 0; i < n; ++i) {
            final K key = this.keyTemplate.read(u, null);
            final V value = this.valueTemplate.read(u, null);
            map.put(key, value);
        }
        u.readMapEnd();
        return map;
    }
}
