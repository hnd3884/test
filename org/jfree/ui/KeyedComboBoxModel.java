package org.jfree.ui;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;

public class KeyedComboBoxModel implements ComboBoxModel
{
    private int selectedItem;
    private ArrayList data;
    private ArrayList listdatalistener;
    private transient ListDataListener[] tempListeners;
    
    public KeyedComboBoxModel() {
        this.data = new ArrayList();
        this.listdatalistener = new ArrayList();
    }
    
    public KeyedComboBoxModel(final Object[] keys, final Object[] values) {
        this();
        this.setData(keys, values);
    }
    
    public void add(final Object key, final Object cbitem) {
        final ComboBoxItemPair con = new ComboBoxItemPair(key, cbitem);
        this.data.add(con);
        final ListDataEvent evt = new ListDataEvent(this, 1, this.data.size() - 2, this.data.size() - 2);
        this.fireListDataEvent(evt);
    }
    
    public synchronized void addListDataListener(final ListDataListener l) {
        this.listdatalistener.add(l);
        this.tempListeners = null;
    }
    
    public void clear() {
        final int size = this.getSize();
        this.data.clear();
        final ListDataEvent evt = new ListDataEvent(this, 2, 0, size - 1);
        this.fireListDataEvent(evt);
    }
    
    private int findDataElementIndex(final Object anItem) {
        if (anItem == null) {
            throw new NullPointerException("Item to find must not be null");
        }
        for (int i = 0; i < this.data.size(); ++i) {
            final ComboBoxItemPair datacon = this.data.get(i);
            if (anItem.equals(datacon.getKey())) {
                return i;
            }
        }
        return -1;
    }
    
    public int findElementIndex(final Object key) {
        if (key == null) {
            throw new NullPointerException("Item to find must not be null");
        }
        for (int i = 0; i < this.data.size(); ++i) {
            final ComboBoxItemPair datacon = this.data.get(i);
            if (key.equals(datacon.getValue())) {
                return i;
            }
        }
        return -1;
    }
    
    protected synchronized void fireListDataEvent(final ListDataEvent evt) {
        if (this.tempListeners == null) {
            this.tempListeners = this.listdatalistener.toArray(new ListDataListener[this.listdatalistener.size()]);
        }
        for (int i = 0; i < this.tempListeners.length; ++i) {
            final ListDataListener l = this.tempListeners[i];
            l.contentsChanged(evt);
        }
    }
    
    public Object getElementAt(final int index) {
        if (index >= this.data.size()) {
            return null;
        }
        final ComboBoxItemPair datacon = this.data.get(index);
        if (datacon == null) {
            return null;
        }
        return datacon.getValue();
    }
    
    public Object getKeyAt(final int index) {
        if (index >= this.data.size()) {
            return null;
        }
        if (index < 0) {
            return null;
        }
        final ComboBoxItemPair datacon = this.data.get(index);
        if (datacon == null) {
            return null;
        }
        return datacon.getKey();
    }
    
    public Object getSelectedItem() {
        if (this.selectedItem >= this.data.size()) {
            return null;
        }
        if (this.selectedItem < 0) {
            return null;
        }
        final ComboBoxItemPair item = this.data.get(this.selectedItem);
        return item.getValue();
    }
    
    public Object getSelectedKey() {
        return this.getKeyAt(this.selectedItem);
    }
    
    public int getSize() {
        return this.data.size();
    }
    
    public void removeDataElement(final Object key) {
        final int idx = this.findDataElementIndex(key);
        if (idx == -1) {
            return;
        }
        this.data.remove(idx);
        final ListDataEvent evt = new ListDataEvent(this, 2, idx, idx);
        this.fireListDataEvent(evt);
    }
    
    public void removeListDataListener(final ListDataListener l) {
        this.listdatalistener.remove(l);
        this.tempListeners = null;
    }
    
    public void setData(final Object[] keys, final Object[] values) {
        if (values.length != keys.length) {
            throw new IllegalArgumentException("Values and text must have the same length.");
        }
        this.data.clear();
        this.data.ensureCapacity(keys.length);
        for (int i = 0; i < values.length; ++i) {
            this.add(keys[i], values[i]);
        }
        this.selectedItem = -1;
        final ListDataEvent evt = new ListDataEvent(this, 0, 0, this.data.size() - 1);
        this.fireListDataEvent(evt);
    }
    
    public void setSelectedItem(final Object anItem) {
        if (anItem == null) {
            this.selectedItem = -1;
        }
        else {
            final int newSelectedItem = this.findElementIndex(anItem);
            this.selectedItem = newSelectedItem;
        }
        this.fireListDataEvent(new ListDataEvent(this, 0, -1, -1));
    }
    
    public void setSelectedKey(final Object anItem) {
        if (anItem == null) {
            this.selectedItem = -1;
        }
        else {
            final int newSelectedItem = this.findDataElementIndex(anItem);
            this.selectedItem = newSelectedItem;
        }
        this.fireListDataEvent(new ListDataEvent(this, 0, -1, -1));
    }
    
    private static class ComboBoxItemPair
    {
        private Object key;
        private Object value;
        
        public ComboBoxItemPair(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public void setValue(final Object value) {
            this.value = value;
        }
    }
}
