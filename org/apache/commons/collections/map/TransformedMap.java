package org.apache.commons.collections.map;

import java.util.Iterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import java.io.Serializable;

public class TransformedMap extends AbstractInputCheckedMapDecorator implements Serializable
{
    private static final long serialVersionUID = 7023152376788900464L;
    protected final Transformer keyTransformer;
    protected final Transformer valueTransformer;
    
    public static Map decorate(final Map map, final Transformer keyTransformer, final Transformer valueTransformer) {
        return new TransformedMap(map, keyTransformer, valueTransformer);
    }
    
    public static Map decorateTransform(final Map map, final Transformer keyTransformer, final Transformer valueTransformer) {
        final TransformedMap decorated = new TransformedMap(map, keyTransformer, valueTransformer);
        if (map.size() > 0) {
            final Map transformed = decorated.transformMap(map);
            decorated.clear();
            decorated.getMap().putAll(transformed);
        }
        return decorated;
    }
    
    protected TransformedMap(final Map map, final Transformer keyTransformer, final Transformer valueTransformer) {
        super(map);
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    protected Object transformKey(final Object object) {
        if (this.keyTransformer == null) {
            return object;
        }
        return this.keyTransformer.transform(object);
    }
    
    protected Object transformValue(final Object object) {
        if (this.valueTransformer == null) {
            return object;
        }
        return this.valueTransformer.transform(object);
    }
    
    protected Map transformMap(final Map map) {
        if (map.isEmpty()) {
            return map;
        }
        final Map result = new LinkedMap(map.size());
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            result.put(this.transformKey(entry.getKey()), this.transformValue(entry.getValue()));
        }
        return result;
    }
    
    protected Object checkSetValue(final Object value) {
        return this.valueTransformer.transform(value);
    }
    
    protected boolean isSetValueChecking() {
        return this.valueTransformer != null;
    }
    
    public Object put(Object key, Object value) {
        key = this.transformKey(key);
        value = this.transformValue(value);
        return this.getMap().put(key, value);
    }
    
    public void putAll(Map mapToCopy) {
        mapToCopy = this.transformMap(mapToCopy);
        this.getMap().putAll(mapToCopy);
    }
}
