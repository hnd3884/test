package javax.swing;

import javax.swing.event.ChangeListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.ItemSelectable;

public interface ButtonModel extends ItemSelectable
{
    boolean isArmed();
    
    boolean isSelected();
    
    boolean isEnabled();
    
    boolean isPressed();
    
    boolean isRollover();
    
    void setArmed(final boolean p0);
    
    void setSelected(final boolean p0);
    
    void setEnabled(final boolean p0);
    
    void setPressed(final boolean p0);
    
    void setRollover(final boolean p0);
    
    void setMnemonic(final int p0);
    
    int getMnemonic();
    
    void setActionCommand(final String p0);
    
    String getActionCommand();
    
    void setGroup(final ButtonGroup p0);
    
    void addActionListener(final ActionListener p0);
    
    void removeActionListener(final ActionListener p0);
    
    void addItemListener(final ItemListener p0);
    
    void removeItemListener(final ItemListener p0);
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
}
