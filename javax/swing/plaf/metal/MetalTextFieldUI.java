package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class MetalTextFieldUI extends BasicTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MetalTextFieldUI();
    }
    
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
    }
}
