package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicTextPaneUI extends BasicEditorPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTextPaneUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "TextPane";
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
    }
}
