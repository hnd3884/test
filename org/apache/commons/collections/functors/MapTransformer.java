package org.apache.commons.collections.functors;

import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public final class MapTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 862391807045468939L;
    private final Map iMap;
    
    public static Transformer getInstance(final Map map) {
        if (map == null) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        return new MapTransformer(map);
    }
    
    private MapTransformer(final Map map) {
        this.iMap = map;
    }
    
    public Object transform(final Object input) {
        return this.iMap.get(input);
    }
    
    public Map getMap() {
        return this.iMap;
    }
}
