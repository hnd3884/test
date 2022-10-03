package javax.swing.plaf.basic;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicFormattedTextFieldUI extends BasicTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new BasicFormattedTextFieldUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "FormattedTextField";
    }
}
