package javax.swing;

public interface MutableComboBoxModel<E> extends ComboBoxModel<E>
{
    void addElement(final E p0);
    
    void removeElement(final Object p0);
    
    void insertElementAt(final E p0, final int p1);
    
    void removeElementAt(final int p0);
}
