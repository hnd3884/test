package com.sun.xml.internal.ws.util.xml;

import org.w3c.dom.NamedNodeMap;
import java.util.Iterator;

public class NamedNodeMapIterator implements Iterator
{
    protected NamedNodeMap _map;
    protected int _index;
    
    public NamedNodeMapIterator(final NamedNodeMap map) {
        this._map = map;
        this._index = 0;
    }
    
    @Override
    public boolean hasNext() {
        return this._map != null && this._index < this._map.getLength();
    }
    
    @Override
    public Object next() {
        final Object obj = this._map.item(this._index);
        if (obj != null) {
            ++this._index;
        }
        return obj;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
