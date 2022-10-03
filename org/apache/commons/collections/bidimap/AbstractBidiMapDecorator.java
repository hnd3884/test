package org.apache.commons.collections.bidimap;

import org.apache.commons.collections.MapIterator;
import java.util.Map;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.map.AbstractMapDecorator;

public abstract class AbstractBidiMapDecorator extends AbstractMapDecorator implements BidiMap
{
    protected AbstractBidiMapDecorator(final BidiMap map) {
        super(map);
    }
    
    protected BidiMap getBidiMap() {
        return (BidiMap)this.map;
    }
    
    public MapIterator mapIterator() {
        return this.getBidiMap().mapIterator();
    }
    
    public Object getKey(final Object value) {
        return this.getBidiMap().getKey(value);
    }
    
    public Object removeValue(final Object value) {
        return this.getBidiMap().removeValue(value);
    }
    
    public BidiMap inverseBidiMap() {
        return this.getBidiMap().inverseBidiMap();
    }
}
