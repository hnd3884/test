package org.jfree.data;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class KeyedObjects implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 1321582394193530984L;
    private List data;
    
    public KeyedObjects() {
        this.data = new ArrayList();
    }
    
    public int getItemCount() {
        return this.data.size();
    }
    
    public Object getObject(final int item) {
        Object result = null;
        if (item >= 0 && item < this.data.size()) {
            final KeyedObject kobj = this.data.get(item);
            if (kobj != null) {
                result = kobj.getObject();
            }
        }
        return result;
    }
    
    public Comparable getKey(final int index) {
        Comparable result = null;
        if (index >= 0 && index < this.data.size()) {
            final KeyedObject item = this.data.get(index);
            if (item != null) {
                result = item.getKey();
            }
        }
        return result;
    }
    
    public int getIndex(final Comparable key) {
        int result = -1;
        int i = 0;
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final KeyedObject ko = iterator.next();
            if (ko.getKey().equals(key)) {
                result = i;
            }
            ++i;
        }
        return result;
    }
    
    public List getKeys() {
        final List result = new ArrayList();
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final KeyedObject ko = iterator.next();
            result.add(ko.getKey());
        }
        return result;
    }
    
    public Object getObject(final Comparable key) {
        return this.getObject(this.getIndex(key));
    }
    
    public void addObject(final Comparable key, final Object object) {
        this.setObject(key, object);
    }
    
    public void setObject(final Comparable key, final Object object) {
        final int keyIndex = this.getIndex(key);
        if (keyIndex >= 0) {
            final KeyedObject ko = this.data.get(keyIndex);
            ko.setObject(object);
        }
        else {
            final KeyedObject ko = new KeyedObject(key, object);
            this.data.add(ko);
        }
    }
    
    public void removeValue(final int index) {
        this.data.remove(index);
    }
    
    public void removeValue(final Comparable key) {
        this.removeValue(this.getIndex(key));
    }
    
    public Object clone() throws CloneNotSupportedException {
        final KeyedObjects clone = (KeyedObjects)super.clone();
        clone.data = new ArrayList();
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final KeyedObject ko = iterator.next();
            clone.data.add(ko.clone());
        }
        return clone;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyedObjects)) {
            return false;
        }
        final KeyedObjects kos = (KeyedObjects)o;
        final int count = this.getItemCount();
        if (count != kos.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; ++i) {
            final Comparable k1 = this.getKey(i);
            final Comparable k2 = kos.getKey(i);
            if (!k1.equals(k2)) {
                return false;
            }
            final Object o2 = this.getObject(i);
            final Object o3 = kos.getObject(i);
            if (o2 == null) {
                if (o3 != null) {
                    return false;
                }
            }
            else if (!o2.equals(o3)) {
                return false;
            }
        }
        return true;
    }
}
