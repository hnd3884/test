package javax.swing.plaf.basic;

import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.ViewportUI;

public class BasicViewportUI extends ViewportUI
{
    private static ViewportUI viewportUI;
    
    public static ComponentUI createUI(final JComponent component) {
        if (BasicViewportUI.viewportUI == null) {
            BasicViewportUI.viewportUI = new BasicViewportUI();
        }
        return BasicViewportUI.viewportUI;
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.installDefaults(component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults(component);
        super.uninstallUI(component);
    }
    
    protected void installDefaults(final JComponent component) {
        LookAndFeel.installColorsAndFont(component, "Viewport.background", "Viewport.foreground", "Viewport.font");
        LookAndFeel.installProperty(component, "opaque", Boolean.TRUE);
    }
    
    protected void uninstallDefaults(final JComponent component) {
    }
}
