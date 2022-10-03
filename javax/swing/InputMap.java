package javax.swing;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.io.Serializable;

public class InputMap implements Serializable
{
    private transient ArrayTable arrayTable;
    private InputMap parent;
    
    public void setParent(final InputMap parent) {
        this.parent = parent;
    }
    
    public InputMap getParent() {
        return this.parent;
    }
    
    public void put(final KeyStroke keyStroke, final Object o) {
        if (keyStroke == null) {
            return;
        }
        if (o == null) {
            this.remove(keyStroke);
        }
        else {
            if (this.arrayTable == null) {
                this.arrayTable = new ArrayTable();
            }
            this.arrayTable.put(keyStroke, o);
        }
    }
    
    public Object get(final KeyStroke keyStroke) {
        if (this.arrayTable != null) {
            final Object value = this.arrayTable.get(keyStroke);
            if (value == null) {
                final InputMap parent = this.getParent();
                if (parent != null) {
                    return parent.get(keyStroke);
                }
            }
            return value;
        }
        final InputMap parent2 = this.getParent();
        if (parent2 != null) {
            return parent2.get(keyStroke);
        }
        return null;
    }
    
    public void remove(final KeyStroke keyStroke) {
        if (this.arrayTable != null) {
            this.arrayTable.remove(keyStroke);
        }
    }
    
    public void clear() {
        if (this.arrayTable != null) {
            this.arrayTable.clear();
        }
    }
    
    public KeyStroke[] keys() {
        if (this.arrayTable == null) {
            return null;
        }
        final KeyStroke[] array = new KeyStroke[this.arrayTable.size()];
        this.arrayTable.getKeys(array);
        return array;
    }
    
    public int size() {
        if (this.arrayTable == null) {
            return 0;
        }
        return this.arrayTable.size();
    }
    
    public KeyStroke[] allKeys() {
        final int size = this.size();
        final InputMap parent = this.getParent();
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
            final KeyStroke[] keys = this.keys();
            final KeyStroke[] allKeys = parent.allKeys();
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
            return (KeyStroke[])hashMap.keySet().toArray(new KeyStroke[hashMap.size()]);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        ArrayTable.writeArrayTable(objectOutputStream, this.arrayTable);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        for (int i = objectInputStream.readInt() - 1; i >= 0; --i) {
            this.put((KeyStroke)objectInputStream.readObject(), objectInputStream.readObject());
        }
    }
}
