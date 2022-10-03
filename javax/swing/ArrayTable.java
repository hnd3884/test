package javax.swing;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;

class ArrayTable implements Cloneable
{
    private Object table;
    private static final int ARRAY_BOUNDARY = 8;
    
    ArrayTable() {
        this.table = null;
    }
    
    static void writeArrayTable(final ObjectOutputStream objectOutputStream, final ArrayTable arrayTable) throws IOException {
        final Object[] keys;
        if (arrayTable == null || (keys = arrayTable.getKeys(null)) == null) {
            objectOutputStream.writeInt(0);
        }
        else {
            int n = 0;
            for (int i = 0; i < keys.length; ++i) {
                final Object o = keys[i];
                if ((o instanceof Serializable && arrayTable.get(o) instanceof Serializable) || (o instanceof ClientPropertyKey && ((ClientPropertyKey)o).getReportValueNotSerializable())) {
                    ++n;
                }
                else {
                    keys[i] = null;
                }
            }
            objectOutputStream.writeInt(n);
            if (n > 0) {
                for (final Object o2 : keys) {
                    if (o2 != null) {
                        objectOutputStream.writeObject(o2);
                        objectOutputStream.writeObject(arrayTable.get(o2));
                        if (--n == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public void put(final Object o, final Object o2) {
        if (this.table == null) {
            this.table = new Object[] { o, o2 };
        }
        else {
            final int size = this.size();
            if (size < 8) {
                if (this.containsKey(o)) {
                    final Object[] array = (Object[])this.table;
                    for (int i = 0; i < array.length - 1; i += 2) {
                        if (array[i].equals(o)) {
                            array[i + 1] = o2;
                            break;
                        }
                    }
                }
                else {
                    final Object[] array2 = (Object[])this.table;
                    final int length = array2.length;
                    final Object[] table = new Object[length + 2];
                    System.arraycopy(array2, 0, table, 0, length);
                    table[length] = o;
                    table[length + 1] = o2;
                    this.table = table;
                }
            }
            else {
                if (size == 8 && this.isArray()) {
                    this.grow();
                }
                ((Hashtable)this.table).put(o, o2);
            }
        }
    }
    
    public Object get(final Object o) {
        Object value = null;
        if (this.table != null) {
            if (this.isArray()) {
                final Object[] array = (Object[])this.table;
                for (int i = 0; i < array.length - 1; i += 2) {
                    if (array[i].equals(o)) {
                        value = array[i + 1];
                        break;
                    }
                }
            }
            else {
                value = ((Hashtable)this.table).get(o);
            }
        }
        return value;
    }
    
    public int size() {
        if (this.table == null) {
            return 0;
        }
        int size;
        if (this.isArray()) {
            size = ((Object[])this.table).length / 2;
        }
        else {
            size = ((Hashtable)this.table).size();
        }
        return size;
    }
    
    public boolean containsKey(final Object o) {
        boolean containsKey = false;
        if (this.table != null) {
            if (this.isArray()) {
                final Object[] array = (Object[])this.table;
                for (int i = 0; i < array.length - 1; i += 2) {
                    if (array[i].equals(o)) {
                        containsKey = true;
                        break;
                    }
                }
            }
            else {
                containsKey = ((Hashtable)this.table).containsKey(o);
            }
        }
        return containsKey;
    }
    
    public Object remove(final Object o) {
        Object remove = null;
        if (o == null) {
            return null;
        }
        if (this.table != null) {
            if (this.isArray()) {
                int n = -1;
                final Object[] array = (Object[])this.table;
                for (int i = array.length - 2; i >= 0; i -= 2) {
                    if (array[i].equals(o)) {
                        n = i;
                        remove = array[i + 1];
                        break;
                    }
                }
                if (n != -1) {
                    final Object[] array2 = new Object[array.length - 2];
                    System.arraycopy(array, 0, array2, 0, n);
                    if (n < array2.length) {
                        System.arraycopy(array, n + 2, array2, n, array2.length - n);
                    }
                    this.table = ((array2.length == 0) ? null : array2);
                }
            }
            else {
                remove = ((Hashtable)this.table).remove(o);
            }
            if (this.size() == 7 && !this.isArray()) {
                this.shrink();
            }
        }
        return remove;
    }
    
    public void clear() {
        this.table = null;
    }
    
    public Object clone() {
        final ArrayTable arrayTable = new ArrayTable();
        if (this.isArray()) {
            final Object[] array = (Object[])this.table;
            for (int i = 0; i < array.length - 1; i += 2) {
                arrayTable.put(array[i], array[i + 1]);
            }
        }
        else {
            final Hashtable hashtable = (Hashtable)this.table;
            final Enumeration keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                final Object nextElement = keys.nextElement();
                arrayTable.put(nextElement, hashtable.get(nextElement));
            }
        }
        return arrayTable;
    }
    
    public Object[] getKeys(Object[] array) {
        if (this.table == null) {
            return null;
        }
        if (this.isArray()) {
            final Object[] array2 = (Object[])this.table;
            if (array == null) {
                array = new Object[array2.length / 2];
            }
            for (int i = 0, n = 0; i < array2.length - 1; i += 2, ++n) {
                array[n] = array2[i];
            }
        }
        else {
            final Hashtable hashtable = (Hashtable)this.table;
            final Enumeration keys = hashtable.keys();
            int j = hashtable.size();
            if (array == null) {
                array = new Object[j];
            }
            while (j > 0) {
                array[--j] = keys.nextElement();
            }
        }
        return array;
    }
    
    private boolean isArray() {
        return this.table instanceof Object[];
    }
    
    private void grow() {
        final Object[] array = (Object[])this.table;
        final Hashtable table = new Hashtable<Object, Object>(array.length / 2);
        for (int i = 0; i < array.length; i += 2) {
            table.put(array[i], array[i + 1]);
        }
        this.table = table;
    }
    
    private void shrink() {
        final Hashtable hashtable = (Hashtable)this.table;
        final Object[] table = new Object[hashtable.size() * 2];
        final Enumeration keys = hashtable.keys();
        int n = 0;
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            table[n] = nextElement;
            table[n + 1] = hashtable.get(nextElement);
            n += 2;
        }
        this.table = table;
    }
}
