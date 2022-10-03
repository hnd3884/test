package javax.swing.plaf.basic;

import javax.swing.border.Border;
import java.awt.Component;
import javax.swing.border.AbstractBorder;
import javax.swing.LookAndFeel;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

public class BasicPanelUI extends PanelUI
{
    private static PanelUI panelUI;
    
    public static ComponentUI createUI(final JComponent component) {
        if (BasicPanelUI.panelUI == null) {
            BasicPanelUI.panelUI = new BasicPanelUI();
        }
        return BasicPanelUI.panelUI;
    }
    
    @Override
    public void installUI(final JComponent component) {
        final JPanel panel = (JPanel)component;
        super.installUI(panel);
        this.installDefaults(panel);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults((JPanel)component);
        super.uninstallUI(component);
    }
    
    protected void installDefaults(final JPanel panel) {
        LookAndFeel.installColorsAndFont(panel, "Panel.background", "Panel.foreground", "Panel.font");
        LookAndFeel.installBorder(panel, "Panel.border");
        LookAndFeel.installProperty(panel, "opaque", Boolean.TRUE);
    }
    
    protected void uninstallDefaults(final JPanel panel) {
        LookAndFeel.uninstallBorder(panel);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        final Border border = component.getBorder();
        if (border instanceof AbstractBorder) {
            return ((AbstractBorder)border).getBaseline(component, n, n2);
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        final Border border = component.getBorder();
        if (border instanceof AbstractBorder) {
            return ((AbstractBorder)border).getBaselineResizeBehavior(component);
        }
        return Component.BaselineResizeBehavior.OTHER;
    }
}
