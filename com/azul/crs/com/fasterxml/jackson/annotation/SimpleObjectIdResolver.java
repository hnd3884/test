package com.azul.crs.com.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;

public class SimpleObjectIdResolver implements ObjectIdResolver
{
    protected Map<ObjectIdGenerator.IdKey, Object> _items;
    
    @Override
    public void bindItem(final ObjectIdGenerator.IdKey id, final Object ob) {
        if (this._items == null) {
            this._items = new HashMap<ObjectIdGenerator.IdKey, Object>();
        }
        else {
            final Object old = this._items.get(id);
            if (old != null) {
                if (old == ob) {
                    return;
                }
                throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
            }
        }
        this._items.put(id, ob);
    }
    
    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        return (this._items == null) ? null : this._items.get(id);
    }
    
    @Override
    public boolean canUseFor(final ObjectIdResolver resolverType) {
        return resolverType.getClass() == this.getClass();
    }
    
    @Override
    public ObjectIdResolver newForDeserialization(final Object context) {
        return new SimpleObjectIdResolver();
    }
}
