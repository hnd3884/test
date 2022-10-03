package sun.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Set;
import java.util.AbstractMap;

public abstract class PreHashedMap<V> extends AbstractMap<String, V>
{
    private final int rows;
    private final int size;
    private final int shift;
    private final int mask;
    private final Object[] ht;
    
    protected PreHashedMap(final int rows, final int size, final int shift, final int mask) {
        this.rows = rows;
        this.size = size;
        this.shift = shift;
        this.mask = mask;
        this.init(this.ht = new Object[rows]);
    }
    
    protected abstract void init(final Object[] p0);
    
    private V toV(final Object o) {
        return (V)o;
    }
    
    @Override
    public V get(final Object o) {
        Object[] array = (Object[])this.ht[o.hashCode() >> this.shift & this.mask];
        if (array == null) {
            return null;
        }
        while (!array[0].equals(o)) {
            if (array.length < 3) {
                return null;
            }
            array = (Object[])array[2];
        }
        return this.toV(array[1]);
    }
    
    @Override
    public V put(final String s, final V v) {
        Object[] array = (Object[])this.ht[s.hashCode() >> this.shift & this.mask];
        if (array == null) {
            throw new UnsupportedOperationException(s);
        }
        while (!array[0].equals(s)) {
            if (array.length < 3) {
                throw new UnsupportedOperationException(s);
            }
            array = (Object[])array[2];
        }
        final V v2 = this.toV(array[1]);
        array[1] = v;
        return v2;
    }
    
    @Override
    public Set<String> keySet() {
        return new AbstractSet<String>() {
            @Override
            public int size() {
                return PreHashedMap.this.size;
            }
            
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private int i = -1;
                    Object[] a = null;
                    String cur = null;
                    
                    private boolean findNext() {
                        if (this.a != null) {
                            if (this.a.length == 3) {
                                this.a = (Object[])this.a[2];
                                this.cur = (String)this.a[0];
                                return true;
                            }
                            ++this.i;
                            this.a = null;
                        }
                        this.cur = null;
                        if (this.i >= PreHashedMap.this.rows) {
                            return false;
                        }
                        Label_0155: {
                            if (this.i < 0 || PreHashedMap.this.ht[this.i] == null) {
                                while (++this.i < PreHashedMap.this.rows) {
                                    if (PreHashedMap.this.ht[this.i] != null) {
                                        break Label_0155;
                                    }
                                }
                                return false;
                            }
                        }
                        this.a = (Object[])PreHashedMap.this.ht[this.i];
                        this.cur = (String)this.a[0];
                        return true;
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.cur != null || this.findNext();
                    }
                    
                    @Override
                    public String next() {
                        if (this.cur == null && !this.findNext()) {
                            throw new NoSuchElementException();
                        }
                        final String cur = this.cur;
                        this.cur = null;
                        return cur;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return new AbstractSet<Map.Entry<String, V>>() {
            @Override
            public int size() {
                return PreHashedMap.this.size;
            }
            
            @Override
            public Iterator<Map.Entry<String, V>> iterator() {
                return new Iterator<Map.Entry<String, V>>() {
                    final Iterator<String> i = PreHashedMap.this.keySet().iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }
                    
                    @Override
                    public Map.Entry<String, V> next() {
                        return new Map.Entry<String, V>() {
                            String k = Iterator.this.i.next();
                            
                            @Override
                            public String getKey() {
                                return this.k;
                            }
                            
                            @Override
                            public V getValue() {
                                return PreHashedMap.this.get(this.k);
                            }
                            
                            @Override
                            public int hashCode() {
                                final Object value = PreHashedMap.this.get(this.k);
                                return this.k.hashCode() + ((value == null) ? 0 : value.hashCode());
                            }
                            
                            @Override
                            public boolean equals(final Object o) {
                                if (o == this) {
                                    return true;
                                }
                                if (!(o instanceof Map.Entry)) {
                                    return false;
                                }
                                final Map.Entry entry = (Map.Entry)o;
                                if (this.getKey() == null) {
                                    if (entry.getKey() != null) {
                                        return false;
                                    }
                                }
                                else if (!this.getKey().equals(entry.getKey())) {
                                    return false;
                                }
                                if ((this.getValue() != null) ? this.getValue().equals(entry.getValue()) : (entry.getValue() == null)) {
                                    return true;
                                }
                                return false;
                            }
                            
                            @Override
                            public V setValue(final V v) {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
