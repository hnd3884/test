package javax.swing;

import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;

public interface Action extends ActionListener
{
    public static final String DEFAULT = "Default";
    public static final String NAME = "Name";
    public static final String SHORT_DESCRIPTION = "ShortDescription";
    public static final String LONG_DESCRIPTION = "LongDescription";
    public static final String SMALL_ICON = "SmallIcon";
    public static final String ACTION_COMMAND_KEY = "ActionCommandKey";
    public static final String ACCELERATOR_KEY = "AcceleratorKey";
    public static final String MNEMONIC_KEY = "MnemonicKey";
    public static final String SELECTED_KEY = "SwingSelectedKey";
    public static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
    public static final String LARGE_ICON_KEY = "SwingLargeIconKey";
    
    Object getValue(final String p0);
    
    void putValue(final String p0, final Object p1);
    
    void setEnabled(final boolean p0);
    
    boolean isEnabled();
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
}
