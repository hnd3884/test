package javax.swing;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.io.Serializable;

public class ActionMap implements Serializable
{
    private transient ArrayTable arrayTable;
    private ActionMap parent;
    
    public void setParent(final ActionMap parent) {
        this.parent = parent;
    }
    
    public ActionMap getParent() {
        return this.parent;
    }
    
    public void put(final Object o, final Action action) {
        if (o == null) {
            return;
        }
        if (action == null) {
            this.remove(o);
        }
        else {
            if (this.arrayTable == null) {
                this.arrayTable = new ArrayTable();
            }
            this.arrayTable.put(o, action);
        }
    }
    
    public Action get(final Object o) {
        final Action action = (this.arrayTable == null) ? null : ((Action)this.arrayTable.get(o));
        if (action == null) {
            final ActionMap parent = this.getParent();
            if (parent != null) {
                return parent.get(o);
            }
        }
        return action;
    }
    
    public void remove(final Object o) {
        if (this.arrayTable != null) {
            this.arrayTable.remove(o);
        }
    }
    
    public void clear() {
        if (this.arrayTable != null) {
            this.arrayTable.clear();
        }
    }
    
    public Object[] keys() {
        if (this.arrayTable == null) {
            return null;
        }
        return this.arrayTable.getKeys(null);
    }
    
    public int size() {
        if (this.arrayTable == null) {
            return 0;
        }
        return this.arrayTable.size();
    }
    
    public Object[] allKeys() {
        final int size = this.size();
        final ActionMap parent = this.getParent();
        if (size == 0) {
            if (parent != null) {
                return parent.allKeys();
            }
            return this.keys();
        }
        else {
            if (parent == null) {
                return this.keys();
            }
            final Object[] keys = this.keys();
            final Object[] allKeys = parent.allKeys();
            if (allKeys == null) {
                return keys;
            }
            if (keys == null) {
                return allKeys;
            }
            final HashMap hashMap = new HashMap();
            for (int i = keys.length - 1; i >= 0; --i) {
                hashMap.put(keys[i], keys[i]);
            }
            for (int j = allKeys.length - 1; j >= 0; --j) {
                hashMap.put(allKeys[j], allKeys[j]);
            }
            return hashMap.keySet().toArray();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        ArrayTable.writeArrayTable(objectOutputStream, this.arrayTable);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        for (int i = objectInputStream.readInt() - 1; i >= 0; --i) {
            this.put(objectInputStream.readObject(), (Action)objectInputStream.readObject());
        }
    }
}
