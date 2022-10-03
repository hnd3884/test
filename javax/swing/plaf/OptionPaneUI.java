package javax.swing.plaf;

import javax.swing.JOptionPane;

public abstract class OptionPaneUI extends ComponentUI
{
    public abstract void selectInitialValue(final JOptionPane p0);
    
    public abstract boolean containsCustomComponents(final JOptionPane p0);
}
