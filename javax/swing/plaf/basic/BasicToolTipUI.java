package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import javax.swing.UIManager;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.JToolTip;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.ToolTipUI;

public class BasicToolTipUI extends ToolTipUI
{
    static BasicToolTipUI sharedInstance;
    private static PropertyChangeListener sharedPropertyChangedListener;
    private PropertyChangeListener propertyChangeListener;
    
    public static ComponentUI createUI(final JComponent component) {
        return BasicToolTipUI.sharedInstance;
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults(component);
        this.installComponents(component);
        this.installListeners(component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults(component);
        this.uninstallComponents(component);
        this.uninstallListeners(component);
    }
    
    protected void installDefaults(final JComponent component) {
        LookAndFeel.installColorsAndFont(component, "ToolTip.background", "ToolTip.foreground", "ToolTip.font");
        LookAndFeel.installProperty(component, "opaque", Boolean.TRUE);
        this.componentChanged(component);
    }
    
    protected void uninstallDefaults(final JComponent component) {
        LookAndFeel.uninstallBorder(component);
    }
    
    private void installComponents(final JComponent component) {
        BasicHTML.updateRenderer(component, ((JToolTip)component).getTipText());
    }
    
    private void uninstallComponents(final JComponent component) {
        BasicHTML.updateRenderer(component, "");
    }
    
    protected void installListeners(final JComponent component) {
        component.addPropertyChangeListener(this.propertyChangeListener = this.createPropertyChangeListener(component));
    }
    
    protected void uninstallListeners(final JComponent component) {
        component.removePropertyChangeListener(this.propertyChangeListener);
        this.propertyChangeListener = null;
    }
    
    private PropertyChangeListener createPropertyChangeListener(final JComponent component) {
        if (BasicToolTipUI.sharedPropertyChangedListener == null) {
            BasicToolTipUI.sharedPropertyChangedListener = new PropertyChangeHandler();
        }
        return BasicToolTipUI.sharedPropertyChangedListener;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Font font = component.getFont();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics, font);
        final Dimension size = component.getSize();
        graphics.setColor(component.getForeground());
        String tipText = ((JToolTip)component).getTipText();
        if (tipText == null) {
            tipText = "";
        }
        final Insets insets = component.getInsets();
        final Rectangle rectangle = new Rectangle(insets.left + 3, insets.top, size.width - (insets.left + insets.right) - 6, size.height - (insets.top + insets.bottom));
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            view.paint(graphics, rectangle);
        }
        else {
            graphics.setFont(font);
            SwingUtilities2.drawString(component, graphics, tipText, rectangle.x, rectangle.y + fontMetrics.getAscent());
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
        final Insets insets = component.getInsets();
        final Dimension dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        final String tipText = ((JToolTip)component).getTipText();
        if (tipText != null && !tipText.equals("")) {
            final View view = (component != null) ? ((View)component.getClientProperty("html")) : null;
            if (view != null) {
                final Dimension dimension2 = dimension;
                dimension2.width += (int)view.getPreferredSpan(0) + 6;
                final Dimension dimension3 = dimension;
                dimension3.height += (int)view.getPreferredSpan(1);
            }
            else {
                final Dimension dimension4 = dimension;
                dimension4.width += SwingUtilities2.stringWidth(component, fontMetrics, tipText) + 6;
                final Dimension dimension5 = dimension;
                dimension5.height += fontMetrics.getHeight();
            }
        }
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width -= (int)(view.getPreferredSpan(0) - view.getMinimumSpan(0));
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width += (int)(view.getMaximumSpan(0) - view.getPreferredSpan(0));
        }
        return preferredSize;
    }
    
    private void componentChanged(final JComponent component) {
        final JComponent component2 = ((JToolTip)component).getComponent();
        if (component2 != null && !component2.isEnabled()) {
            if (UIManager.getBorder("ToolTip.borderInactive") != null) {
                LookAndFeel.installBorder(component, "ToolTip.borderInactive");
            }
            else {
                LookAndFeel.installBorder(component, "ToolTip.border");
            }
            if (UIManager.getColor("ToolTip.backgroundInactive") != null) {
                LookAndFeel.installColors(component, "ToolTip.backgroundInactive", "ToolTip.foregroundInactive");
            }
            else {
                LookAndFeel.installColors(component, "ToolTip.background", "ToolTip.foreground");
            }
        }
        else {
            LookAndFeel.installBorder(component, "ToolTip.border");
            LookAndFeel.installColors(component, "ToolTip.background", "ToolTip.foreground");
        }
    }
    
    static {
        BasicToolTipUI.sharedInstance = new BasicToolTipUI();
    }
    
    private static class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("tiptext") || "font".equals(propertyName) || "foreground".equals(propertyName)) {
                final JToolTip toolTip = (JToolTip)propertyChangeEvent.getSource();
                BasicHTML.updateRenderer(toolTip, toolTip.getTipText());
            }
            else if ("component".equals(propertyName)) {
                final JToolTip toolTip2 = (JToolTip)propertyChangeEvent.getSource();
                if (toolTip2.getUI() instanceof BasicToolTipUI) {
                    ((BasicToolTipUI)toolTip2.getUI()).componentChanged(toolTip2);
                }
            }
        }
    }
}
