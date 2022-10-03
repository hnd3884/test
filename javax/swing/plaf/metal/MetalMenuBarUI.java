package javax.swing.plaf.metal;

import java.awt.Component;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class MetalMenuBarUI extends BasicMenuBarUI
{
    public static ComponentUI createUI(final JComponent component) {
        if (component == null) {
            throw new NullPointerException("Must pass in a non-null component");
        }
        return new MetalMenuBarUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        MetalToolBarUI.register(component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        MetalToolBarUI.unregister(component);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final boolean opaque = component.isOpaque();
        if (graphics == null) {
            throw new NullPointerException("Graphics must be non-null");
        }
        if (opaque && component.getBackground() instanceof UIResource && UIManager.get("MenuBar.gradient") != null) {
            if (MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)component)) {
                final JToolBar toolBar = (JToolBar)MetalToolBarUI.findRegisteredComponentOfType(component, JToolBar.class);
                if (toolBar.isOpaque() && toolBar.getBackground() instanceof UIResource) {
                    MetalUtils.drawGradient(component, graphics, "MenuBar.gradient", 0, 0, component.getWidth(), component.getHeight() + toolBar.getHeight(), true);
                    this.paint(graphics, component);
                    return;
                }
            }
            MetalUtils.drawGradient(component, graphics, "MenuBar.gradient", 0, 0, component.getWidth(), component.getHeight(), true);
            this.paint(graphics, component);
        }
        else {
            super.update(graphics, component);
        }
    }
}
