package com.sun.xml.internal.ws.util.xml;

import org.w3c.dom.NodeList;
import java.util.Iterator;

public class NodeListIterator implements Iterator
{
    protected NodeList _list;
    protected int _index;
    
    public NodeListIterator(final NodeList list) {
        this._list = list;
        this._index = 0;
    }
    
    @Override
    public boolean hasNext() {
        return this._list != null && this._index < this._list.getLength();
    }
    
    @Override
    public Object next() {
        final Object obj = this._list.item(this._index);
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
