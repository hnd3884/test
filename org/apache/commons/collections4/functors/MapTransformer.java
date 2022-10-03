package org.apache.commons.collections4.functors;

import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public final class MapTransformer<I, O> implements Transformer<I, O>, Serializable
{
    private static final long serialVersionUID = 862391807045468939L;
    private final Map<? super I, ? extends O> iMap;
    
    public static <I, O> Transformer<I, O> mapTransformer(final Map<? super I, ? extends O> map) {
        if (map == null) {
            return ConstantTransformer.nullTransformer();
        }
        return new MapTransformer<I, O>(map);
    }
    
    private MapTransformer(final Map<? super I, ? extends O> map) {
        this.iMap = map;
    }
    
    @Override
    public O transform(final I input) {
        return (O)this.iMap.get(input);
    }
    
    public Map<? super I, ? extends O> getMap() {
        return this.iMap;
    }
}
