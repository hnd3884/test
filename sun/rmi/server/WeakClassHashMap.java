package sun.rmi.server;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.WeakHashMap;
import java.util.Map;

public abstract class WeakClassHashMap<V>
{
    private Map<Class<?>, ValueCell<V>> internalMap;
    
    protected WeakClassHashMap() {
        this.internalMap = new WeakHashMap<Class<?>, ValueCell<V>>();
    }
    
    public V get(final Class<?> clazz) {
        ValueCell valueCell;
        synchronized (this.internalMap) {
            valueCell = this.internalMap.get(clazz);
            if (valueCell == null) {
                valueCell = new ValueCell();
                this.internalMap.put(clazz, valueCell);
            }
        }
        synchronized (valueCell) {
            Object o = null;
            if (valueCell.ref != null) {
                o = valueCell.ref.get();
            }
            if (o == null) {
                o = this.computeValue(clazz);
                valueCell.ref = (Reference<T>)new SoftReference<Object>(o);
            }
            return (V)o;
        }
    }
    
    protected abstract V computeValue(final Class<?> p0);
    
    private static class ValueCell<T>
    {
        Reference<T> ref;
        
        ValueCell() {
            this.ref = null;
        }
    }
}
