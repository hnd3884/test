package javax.swing.plaf.metal;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Color;
import javax.swing.plaf.basic.BasicHTML;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import java.awt.Font;
import javax.swing.plaf.basic.BasicToolTipUI;

public class MetalToolTipUI extends BasicToolTipUI
{
    static MetalToolTipUI sharedInstance;
    private Font smallFont;
    private JToolTip tip;
    public static final int padSpaceBetweenStrings = 12;
    private String acceleratorDelimiter;
    
    public static ComponentUI createUI(final JComponent component) {
        return MetalToolTipUI.sharedInstance;
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.tip = (JToolTip)component;
        final Font font = component.getFont();
        this.smallFont = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
        this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
        if (this.acceleratorDelimiter == null) {
            this.acceleratorDelimiter = "-";
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        this.tip = null;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final JToolTip toolTip = (JToolTip)component;
        final Font font = component.getFont();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics, font);
        final Dimension size = component.getSize();
        graphics.setColor(component.getForeground());
        String tipText = toolTip.getTipText();
        if (tipText == null) {
            tipText = "";
        }
        final String acceleratorString = this.getAcceleratorString(toolTip);
        final int calcAccelSpacing = this.calcAccelSpacing(component, SwingUtilities2.getFontMetrics(component, graphics, this.smallFont), acceleratorString);
        final Insets insets = toolTip.getInsets();
        final Rectangle rectangle = new Rectangle(insets.left + 3, insets.top, size.width - (insets.left + insets.right) - 6 - calcAccelSpacing, size.height - (insets.top + insets.bottom));
        final View view = (View)component.getClientProperty("html");
        int n;
        if (view != null) {
            view.paint(graphics, rectangle);
            n = BasicHTML.getHTMLBaseline(view, rectangle.width, rectangle.height);
        }
        else {
            graphics.setFont(font);
            SwingUtilities2.drawString(toolTip, graphics, tipText, rectangle.x, rectangle.y + fontMetrics.getAscent());
            n = fontMetrics.getAscent();
        }
        if (!acceleratorString.equals("")) {
            graphics.setFont(this.smallFont);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            SwingUtilities2.drawString(toolTip, graphics, acceleratorString, toolTip.getWidth() - 1 - insets.right - calcAccelSpacing + 12 - 3, rectangle.y + n);
        }
    }
    
    private int calcAccelSpacing(final JComponent component, final FontMetrics fontMetrics, final String s) {
        return s.equals("") ? 0 : (12 + SwingUtilities2.stringWidth(component, fontMetrics, s));
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension preferredSize = super.getPreferredSize(component);
        final String acceleratorString = this.getAcceleratorString((JToolTip)component);
        if (!acceleratorString.equals("")) {
            final Dimension dimension = preferredSize;
            dimension.width += this.calcAccelSpacing(component, component.getFontMetrics(this.smallFont), acceleratorString);
        }
        return preferredSize;
    }
    
    protected boolean isAcceleratorHidden() {
        final Boolean b = (Boolean)UIManager.get("ToolTip.hideAccelerator");
        return b != null && b;
    }
    
    private String getAcceleratorString(final JToolTip tip) {
        this.tip = tip;
        final String acceleratorString = this.getAcceleratorString();
        this.tip = null;
        return acceleratorString;
    }
    
    public String getAcceleratorString() {
        if (this.tip == null || this.isAcceleratorHidden()) {
            return "";
        }
        final JComponent component = this.tip.getComponent();
        if (!(component instanceof AbstractButton)) {
            return "";
        }
        final KeyStroke[] keys = component.getInputMap(2).keys();
        if (keys == null) {
            return "";
        }
        String string = "";
        final int n = 0;
        if (n < keys.length) {
            string = KeyEvent.getKeyModifiersText(keys[n].getModifiers()) + this.acceleratorDelimiter + KeyEvent.getKeyText(keys[n].getKeyCode());
        }
        return string;
    }
    
    static {
        MetalToolTipUI.sharedInstance = new MetalToolTipUI();
    }
}
