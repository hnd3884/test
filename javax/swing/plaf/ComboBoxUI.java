package javax.swing.plaf;

import javax.swing.JComboBox;

public abstract class ComboBoxUI extends ComponentUI
{
    public abstract void setPopupVisible(final JComboBox p0, final boolean p1);
    
    public abstract boolean isPopupVisible(final JComboBox p0);
    
    public abstract boolean isFocusTraversable(final JComboBox p0);
}
