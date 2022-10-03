package javax.swing;

public interface ComboBoxModel<E> extends ListModel<E>
{
    void setSelectedItem(final Object p0);
    
    Object getSelectedItem();
}
