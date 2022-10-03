package javax.swing.plaf.basic;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.Component;
import java.awt.FontMetrics;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

public class BasicGraphicsUtils
{
    private static final Insets GROOVE_INSETS;
    private static final Insets ETCHED_INSETS;
    
    public static void drawEtchedRect(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Color color, final Color color2, final Color color3, final Color color4) {
        final Color color5 = graphics.getColor();
        graphics.translate(n, n2);
        graphics.setColor(color);
        graphics.drawLine(0, 0, n3 - 1, 0);
        graphics.drawLine(0, 1, 0, n4 - 2);
        graphics.setColor(color2);
        graphics.drawLine(1, 1, n3 - 3, 1);
        graphics.drawLine(1, 2, 1, n4 - 3);
        graphics.setColor(color4);
        graphics.drawLine(n3 - 1, 0, n3 - 1, n4 - 1);
        graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
        graphics.setColor(color3);
        graphics.drawLine(n3 - 2, 1, n3 - 2, n4 - 3);
        graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
        graphics.translate(-n, -n2);
        graphics.setColor(color5);
    }
    
    public static Insets getEtchedInsets() {
        return BasicGraphicsUtils.ETCHED_INSETS;
    }
    
    public static void drawGroove(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Color color, final Color color2) {
        final Color color3 = graphics.getColor();
        graphics.translate(n, n2);
        graphics.setColor(color);
        graphics.drawRect(0, 0, n3 - 2, n4 - 2);
        graphics.setColor(color2);
        graphics.drawLine(1, n4 - 3, 1, 1);
        graphics.drawLine(1, 1, n3 - 3, 1);
        graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
        graphics.drawLine(n3 - 1, n4 - 1, n3 - 1, 0);
        graphics.translate(-n, -n2);
        graphics.setColor(color3);
    }
    
    public static Insets getGrooveInsets() {
        return BasicGraphicsUtils.GROOVE_INSETS;
    }
    
    public static void drawBezel(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final boolean b, final boolean b2, final Color color, final Color color2, final Color color3, final Color color4) {
        final Color color5 = graphics.getColor();
        graphics.translate(n, n2);
        if (b && b2) {
            graphics.setColor(color2);
            graphics.drawRect(0, 0, n3 - 1, n4 - 1);
            graphics.setColor(color);
            graphics.drawRect(1, 1, n3 - 3, n4 - 3);
        }
        else if (b) {
            drawLoweredBezel(graphics, n, n2, n3, n4, color, color2, color3, color4);
        }
        else if (b2) {
            graphics.setColor(color2);
            graphics.drawRect(0, 0, n3 - 1, n4 - 1);
            graphics.setColor(color4);
            graphics.drawLine(1, 1, 1, n4 - 3);
            graphics.drawLine(2, 1, n3 - 3, 1);
            graphics.setColor(color3);
            graphics.drawLine(2, 2, 2, n4 - 4);
            graphics.drawLine(3, 2, n3 - 4, 2);
            graphics.setColor(color);
            graphics.drawLine(2, n4 - 3, n3 - 3, n4 - 3);
            graphics.drawLine(n3 - 3, 2, n3 - 3, n4 - 4);
            graphics.setColor(color2);
            graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
            graphics.drawLine(n3 - 2, n4 - 2, n3 - 2, 1);
        }
        else {
            graphics.setColor(color4);
            graphics.drawLine(0, 0, 0, n4 - 1);
            graphics.drawLine(1, 0, n3 - 2, 0);
            graphics.setColor(color3);
            graphics.drawLine(1, 1, 1, n4 - 3);
            graphics.drawLine(2, 1, n3 - 3, 1);
            graphics.setColor(color);
            graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
            graphics.drawLine(n3 - 2, 1, n3 - 2, n4 - 3);
            graphics.setColor(color2);
            graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
            graphics.drawLine(n3 - 1, n4 - 1, n3 - 1, 0);
        }
        graphics.translate(-n, -n2);
        graphics.setColor(color5);
    }
    
    public static void drawLoweredBezel(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Color color, final Color color2, final Color color3, final Color color4) {
        graphics.setColor(color2);
        graphics.drawLine(0, 0, 0, n4 - 1);
        graphics.drawLine(1, 0, n3 - 2, 0);
        graphics.setColor(color);
        graphics.drawLine(1, 1, 1, n4 - 2);
        graphics.drawLine(1, 1, n3 - 3, 1);
        graphics.setColor(color4);
        graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
        graphics.drawLine(n3 - 1, n4 - 1, n3 - 1, 0);
        graphics.setColor(color3);
        graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
        graphics.drawLine(n3 - 2, n4 - 2, n3 - 2, 1);
    }
    
    public static void drawString(final Graphics graphics, final String s, final int n, final int n2, final int n3) {
        int n4 = -1;
        if (n != 0) {
            final char upperCase = Character.toUpperCase((char)n);
            final char lowerCase = Character.toLowerCase((char)n);
            final int index = s.indexOf(upperCase);
            final int index2 = s.indexOf(lowerCase);
            if (index == -1) {
                n4 = index2;
            }
            else if (index2 == -1) {
                n4 = index;
            }
            else {
                n4 = ((index2 < index) ? index2 : index);
            }
        }
        drawStringUnderlineCharAt(graphics, s, n4, n2, n3);
    }
    
    public static void drawStringUnderlineCharAt(final Graphics graphics, final String s, final int n, final int n2, final int n3) {
        SwingUtilities2.drawStringUnderlineCharAt(null, graphics, s, n, n2, n3);
    }
    
    public static void drawDashedRect(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        for (int i = n; i < n + n3; i += 2) {
            graphics.fillRect(i, n2, 1, 1);
            graphics.fillRect(i, n2 + n4 - 1, 1, 1);
        }
        for (int j = n2; j < n2 + n4; j += 2) {
            graphics.fillRect(n, j, 1, 1);
            graphics.fillRect(n + n3 - 1, j, 1, 1);
        }
    }
    
    public static Dimension getPreferredButtonSize(final AbstractButton abstractButton, final int n) {
        if (abstractButton.getComponentCount() > 0) {
            return null;
        }
        final Icon icon = abstractButton.getIcon();
        final String text = abstractButton.getText();
        final FontMetrics fontMetrics = abstractButton.getFontMetrics(abstractButton.getFont());
        final Rectangle rectangle = new Rectangle();
        final Rectangle rectangle2 = new Rectangle();
        SwingUtilities.layoutCompoundLabel(abstractButton, fontMetrics, text, icon, abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), new Rectangle(32767, 32767), rectangle, rectangle2, (text == null) ? 0 : n);
        final Rectangle union = rectangle.union(rectangle2);
        final Insets insets = abstractButton.getInsets();
        final Rectangle rectangle3 = union;
        rectangle3.width += insets.left + insets.right;
        final Rectangle rectangle4 = union;
        rectangle4.height += insets.top + insets.bottom;
        return union.getSize();
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
    
    static boolean isMenuShortcutKeyDown(final InputEvent inputEvent) {
        return (inputEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0x0;
    }
    
    static {
        GROOVE_INSETS = new Insets(2, 2, 2, 2);
        ETCHED_INSETS = new Insets(2, 2, 2, 2);
    }
}
