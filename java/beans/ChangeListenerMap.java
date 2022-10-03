package java.beans;

import java.util.Collections;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.EventListener;

abstract class ChangeListenerMap<L extends EventListener>
{
    private Map<String, L[]> map;
    
    protected abstract L[] newArray(final int p0);
    
    protected abstract L newProxy(final String p0, final L p1);
    
    public final synchronized void add(final String s, final L l) {
        if (this.map == null) {
            this.map = new HashMap<String, L[]>();
        }
        final L[] array = this.map.get(s);
        final int n = (array != null) ? array.length : 0;
        final L[] array2 = this.newArray(n + 1);
        array2[n] = l;
        if (array != null) {
            System.arraycopy(array, 0, array2, 0, n);
        }
        this.map.put(s, array2);
    }
    
    public final synchronized void remove(final String s, final L l) {
        if (this.map != null) {
            final L[] array = this.map.get(s);
            if (array != null) {
                int i = 0;
                while (i < array.length) {
                    if (l.equals(array[i])) {
                        final int n = array.length - 1;
                        if (n > 0) {
                            final L[] array2 = this.newArray(n);
                            System.arraycopy(array, 0, array2, 0, i);
                            System.arraycopy(array, i + 1, array2, i, n - i);
                            this.map.put(s, array2);
                            break;
                        }
                        this.map.remove(s);
                        if (this.map.isEmpty()) {
                            this.map = null;
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
    }
    
    public final synchronized L[] get(final String s) {
        return (L[])((this.map != null) ? ((L[])this.map.get(s)) : null);
    }
    
    public final void set(final String s, final L[] array) {
        if (array != null) {
            if (this.map == null) {
                this.map = new HashMap<String, L[]>();
            }
            this.map.put(s, array);
        }
        else if (this.map != null) {
            this.map.remove(s);
            if (this.map.isEmpty()) {
                this.map = null;
            }
        }
    }
    
    public final synchronized L[] getListeners() {
        if (this.map == null) {
            return this.newArray(0);
        }
        final ArrayList list = new ArrayList();
        final L[] array = this.map.get(null);
        if (array != null) {
            final L[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                list.add(array2[i]);
            }
        }
        for (final Map.Entry entry : this.map.entrySet()) {
            final String s = (String)entry.getKey();
            if (s != null) {
                final EventListener[] array3 = (EventListener[])entry.getValue();
                for (int length2 = array3.length, j = 0; j < length2; ++j) {
                    list.add(this.newProxy(s, array3[j]));
                }
            }
        }
        return (L[])list.toArray(this.newArray(list.size()));
    }
    
    public final L[] getListeners(final String s) {
        if (s != null) {
            final EventListener[] value = this.get(s);
            if (value != null) {
                return (L[])value.clone();
            }
        }
        return this.newArray(0);
    }
    
    public final synchronized boolean hasListeners(final String s) {
        return this.map != null && (this.map.get(null) != null || (s != null && null != this.map.get(s)));
    }
    
    public final Set<Map.Entry<String, L[]>> getEntries() {
        return (this.map != null) ? this.map.entrySet() : Collections.emptySet();
    }
    
    public abstract L extract(final L p0);
}
