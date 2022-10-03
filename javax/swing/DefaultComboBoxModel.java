package javax.swing;

import java.util.Vector;
import java.io.Serializable;

public class DefaultComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E>, Serializable
{
    Vector<E> objects;
    Object selectedObject;
    
    public DefaultComboBoxModel() {
        this.objects = new Vector<E>();
    }
    
    public DefaultComboBoxModel(final E[] array) {
        this.objects = new Vector<E>(array.length);
        for (int i = 0; i < array.length; ++i) {
            this.objects.addElement(array[i]);
        }
        if (this.getSize() > 0) {
            this.selectedObject = this.getElementAt(0);
        }
    }
    
    public DefaultComboBoxModel(final Vector<E> objects) {
        this.objects = objects;
        if (this.getSize() > 0) {
            this.selectedObject = this.getElementAt(0);
        }
    }
    
    @Override
    public void setSelectedItem(final Object selectedObject) {
        if ((this.selectedObject != null && !this.selectedObject.equals(selectedObject)) || (this.selectedObject == null && selectedObject != null)) {
            this.selectedObject = selectedObject;
            this.fireContentsChanged(this, -1, -1);
        }
    }
    
    @Override
    public Object getSelectedItem() {
        return this.selectedObject;
    }
    
    @Override
    public int getSize() {
        return this.objects.size();
    }
    
    @Override
    public E getElementAt(final int n) {
        if (n >= 0 && n < this.objects.size()) {
            return this.objects.elementAt(n);
        }
        return null;
    }
    
    public int getIndexOf(final Object o) {
        return this.objects.indexOf(o);
    }
    
    @Override
    public void addElement(final E selectedItem) {
        this.objects.addElement(selectedItem);
        this.fireIntervalAdded(this, this.objects.size() - 1, this.objects.size() - 1);
        if (this.objects.size() == 1 && this.selectedObject == null && selectedItem != null) {
            this.setSelectedItem(selectedItem);
        }
    }
    
    @Override
    public void insertElementAt(final E e, final int n) {
        this.objects.insertElementAt(e, n);
        this.fireIntervalAdded(this, n, n);
    }
    
    @Override
    public void removeElementAt(final int n) {
        if (this.getElementAt(n) == this.selectedObject) {
            if (n == 0) {
                this.setSelectedItem((this.getSize() == 1) ? null : this.getElementAt(n + 1));
            }
            else {
                this.setSelectedItem(this.getElementAt(n - 1));
            }
        }
        this.objects.removeElementAt(n);
        this.fireIntervalRemoved(this, n, n);
    }
    
    @Override
    public void removeElement(final Object o) {
        final int index = this.objects.indexOf(o);
        if (index != -1) {
            this.removeElementAt(index);
        }
    }
    
    public void removeAllElements() {
        if (this.objects.size() > 0) {
            final int n = 0;
            final int n2 = this.objects.size() - 1;
            this.objects.removeAllElements();
            this.selectedObject = null;
            this.fireIntervalRemoved(this, n, n2);
        }
        else {
            this.selectedObject = null;
        }
    }
}
