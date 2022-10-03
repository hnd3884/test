package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import sun.swing.SwingUtilities2;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.JButton;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MetalScrollBarUI extends BasicScrollBarUI
{
    private static Color shadowColor;
    private static Color highlightColor;
    private static Color darkShadowColor;
    private static Color thumbColor;
    private static Color thumbShadow;
    private static Color thumbHighlightColor;
    protected MetalBumps bumps;
    protected MetalScrollButton increaseButton;
    protected MetalScrollButton decreaseButton;
    protected int scrollBarWidth;
    public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
    protected boolean isFreeStanding;
    
    public MetalScrollBarUI() {
        this.isFreeStanding = true;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalScrollBarUI();
    }
    
    @Override
    protected void installDefaults() {
        this.scrollBarWidth = (int)UIManager.get("ScrollBar.width");
        super.installDefaults();
        this.bumps = new MetalBumps(10, 10, MetalScrollBarUI.thumbHighlightColor, MetalScrollBarUI.thumbShadow, MetalScrollBarUI.thumbColor);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        ((ScrollBarListener)this.propertyChangeListener).handlePropertyChange(this.scrollbar.getClientProperty("JScrollBar.isFreeStanding"));
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new ScrollBarListener();
    }
    
    @Override
    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
        MetalScrollBarUI.shadowColor = UIManager.getColor("ScrollBar.shadow");
        MetalScrollBarUI.highlightColor = UIManager.getColor("ScrollBar.highlight");
        MetalScrollBarUI.darkShadowColor = UIManager.getColor("ScrollBar.darkShadow");
        MetalScrollBarUI.thumbColor = UIManager.getColor("ScrollBar.thumb");
        MetalScrollBarUI.thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
        MetalScrollBarUI.thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (this.scrollbar.getOrientation() == 1) {
            return new Dimension(this.scrollBarWidth, this.scrollBarWidth * 3 + 10);
        }
        return new Dimension(this.scrollBarWidth * 3 + 10, this.scrollBarWidth);
    }
    
    @Override
    protected JButton createDecreaseButton(final int n) {
        return this.decreaseButton = new MetalScrollButton(n, this.scrollBarWidth, this.isFreeStanding);
    }
    
    @Override
    protected JButton createIncreaseButton(final int n) {
        return this.increaseButton = new MetalScrollButton(n, this.scrollBarWidth, this.isFreeStanding);
    }
    
    @Override
    protected void paintTrack(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        graphics.translate(rectangle.x, rectangle.y);
        final boolean leftToRight = MetalUtils.isLeftToRight(component);
        if (this.scrollbar.getOrientation() == 1) {
            if (!this.isFreeStanding) {
                rectangle.width += 2;
                if (!leftToRight) {
                    graphics.translate(-1, 0);
                }
            }
            if (component.isEnabled()) {
                graphics.setColor(MetalScrollBarUI.darkShadowColor);
                SwingUtilities2.drawVLine(graphics, 0, 0, rectangle.height - 1);
                SwingUtilities2.drawVLine(graphics, rectangle.width - 2, 0, rectangle.height - 1);
                SwingUtilities2.drawHLine(graphics, 2, rectangle.width - 1, rectangle.height - 1);
                SwingUtilities2.drawHLine(graphics, 2, rectangle.width - 2, 0);
                graphics.setColor(MetalScrollBarUI.shadowColor);
                SwingUtilities2.drawVLine(graphics, 1, 1, rectangle.height - 2);
                SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 3, 1);
                if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
                    SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 1, this.thumbRect.y + this.thumbRect.height - rectangle.y);
                }
                graphics.setColor(MetalScrollBarUI.highlightColor);
                SwingUtilities2.drawVLine(graphics, rectangle.width - 1, 0, rectangle.height - 1);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, 0, 0, rectangle.width, rectangle.height);
            }
            if (!this.isFreeStanding) {
                rectangle.width -= 2;
                if (!leftToRight) {
                    graphics.translate(1, 0);
                }
            }
        }
        else {
            if (!this.isFreeStanding) {
                rectangle.height += 2;
            }
            if (component.isEnabled()) {
                graphics.setColor(MetalScrollBarUI.darkShadowColor);
                SwingUtilities2.drawHLine(graphics, 0, rectangle.width - 1, 0);
                SwingUtilities2.drawVLine(graphics, 0, 2, rectangle.height - 2);
                SwingUtilities2.drawHLine(graphics, 0, rectangle.width - 1, rectangle.height - 2);
                SwingUtilities2.drawVLine(graphics, rectangle.width - 1, 2, rectangle.height - 1);
                graphics.setColor(MetalScrollBarUI.shadowColor);
                SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 2, 1);
                SwingUtilities2.drawVLine(graphics, 1, 1, rectangle.height - 3);
                SwingUtilities2.drawHLine(graphics, 0, rectangle.width - 1, rectangle.height - 1);
                if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
                    SwingUtilities2.drawVLine(graphics, this.thumbRect.x + this.thumbRect.width - rectangle.x, 1, rectangle.height - 1);
                }
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, 0, 0, rectangle.width, rectangle.height);
            }
            if (!this.isFreeStanding) {
                rectangle.height -= 2;
            }
        }
        graphics.translate(-rectangle.x, -rectangle.y);
    }
    
    @Override
    protected void paintThumb(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        if (!component.isEnabled()) {
            return;
        }
        if (MetalLookAndFeel.usingOcean()) {
            this.oceanPaintThumb(graphics, component, rectangle);
            return;
        }
        final boolean leftToRight = MetalUtils.isLeftToRight(component);
        graphics.translate(rectangle.x, rectangle.y);
        if (this.scrollbar.getOrientation() == 1) {
            if (!this.isFreeStanding) {
                rectangle.width += 2;
                if (!leftToRight) {
                    graphics.translate(-1, 0);
                }
            }
            graphics.setColor(MetalScrollBarUI.thumbColor);
            graphics.fillRect(0, 0, rectangle.width - 2, rectangle.height - 1);
            graphics.setColor(MetalScrollBarUI.thumbShadow);
            SwingUtilities2.drawRect(graphics, 0, 0, rectangle.width - 2, rectangle.height - 1);
            graphics.setColor(MetalScrollBarUI.thumbHighlightColor);
            SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 3, 1);
            SwingUtilities2.drawVLine(graphics, 1, 1, rectangle.height - 2);
            this.bumps.setBumpArea(rectangle.width - 6, rectangle.height - 7);
            this.bumps.paintIcon(component, graphics, 3, 4);
            if (!this.isFreeStanding) {
                rectangle.width -= 2;
                if (!leftToRight) {
                    graphics.translate(1, 0);
                }
            }
        }
        else {
            if (!this.isFreeStanding) {
                rectangle.height += 2;
            }
            graphics.setColor(MetalScrollBarUI.thumbColor);
            graphics.fillRect(0, 0, rectangle.width - 1, rectangle.height - 2);
            graphics.setColor(MetalScrollBarUI.thumbShadow);
            SwingUtilities2.drawRect(graphics, 0, 0, rectangle.width - 1, rectangle.height - 2);
            graphics.setColor(MetalScrollBarUI.thumbHighlightColor);
            SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 3, 1);
            SwingUtilities2.drawVLine(graphics, 1, 1, rectangle.height - 3);
            this.bumps.setBumpArea(rectangle.width - 7, rectangle.height - 6);
            this.bumps.paintIcon(component, graphics, 4, 3);
            if (!this.isFreeStanding) {
                rectangle.height -= 2;
            }
        }
        graphics.translate(-rectangle.x, -rectangle.y);
    }
    
    private void oceanPaintThumb(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        final boolean leftToRight = MetalUtils.isLeftToRight(component);
        graphics.translate(rectangle.x, rectangle.y);
        if (this.scrollbar.getOrientation() == 1) {
            if (!this.isFreeStanding) {
                rectangle.width += 2;
                if (!leftToRight) {
                    graphics.translate(-1, 0);
                }
            }
            if (MetalScrollBarUI.thumbColor != null) {
                graphics.setColor(MetalScrollBarUI.thumbColor);
                graphics.fillRect(0, 0, rectangle.width - 2, rectangle.height - 1);
            }
            graphics.setColor(MetalScrollBarUI.thumbShadow);
            SwingUtilities2.drawRect(graphics, 0, 0, rectangle.width - 2, rectangle.height - 1);
            graphics.setColor(MetalScrollBarUI.thumbHighlightColor);
            SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 3, 1);
            SwingUtilities2.drawVLine(graphics, 1, 1, rectangle.height - 2);
            MetalUtils.drawGradient(component, graphics, "ScrollBar.gradient", 2, 2, rectangle.width - 4, rectangle.height - 3, false);
            final int n = rectangle.width - 8;
            if (n > 2 && rectangle.height >= 10) {
                graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                int n2 = rectangle.height / 2 - 2;
                for (int i = 0; i < 6; i += 2) {
                    graphics.fillRect(4, i + n2, n, 1);
                }
                graphics.setColor(MetalLookAndFeel.getWhite());
                ++n2;
                for (int j = 0; j < 6; j += 2) {
                    graphics.fillRect(5, j + n2, n, 1);
                }
            }
            if (!this.isFreeStanding) {
                rectangle.width -= 2;
                if (!leftToRight) {
                    graphics.translate(1, 0);
                }
            }
        }
        else {
            if (!this.isFreeStanding) {
                rectangle.height += 2;
            }
            if (MetalScrollBarUI.thumbColor != null) {
                graphics.setColor(MetalScrollBarUI.thumbColor);
                graphics.fillRect(0, 0, rectangle.width - 1, rectangle.height - 2);
            }
            graphics.setColor(MetalScrollBarUI.thumbShadow);
            SwingUtilities2.drawRect(graphics, 0, 0, rectangle.width - 1, rectangle.height - 2);
            graphics.setColor(MetalScrollBarUI.thumbHighlightColor);
            SwingUtilities2.drawHLine(graphics, 1, rectangle.width - 2, 1);
            SwingUtilities2.drawVLine(graphics, 1, 1, rectangle.height - 3);
            MetalUtils.drawGradient(component, graphics, "ScrollBar.gradient", 2, 2, rectangle.width - 3, rectangle.height - 4, true);
            final int n3 = rectangle.height - 8;
            if (n3 > 2 && rectangle.width >= 10) {
                graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                int n4 = rectangle.width / 2 - 2;
                for (int k = 0; k < 6; k += 2) {
                    graphics.fillRect(n4 + k, 4, 1, n3);
                }
                graphics.setColor(MetalLookAndFeel.getWhite());
                ++n4;
                for (int l = 0; l < 6; l += 2) {
                    graphics.fillRect(n4 + l, 5, 1, n3);
                }
            }
            if (!this.isFreeStanding) {
                rectangle.height -= 2;
            }
        }
        graphics.translate(-rectangle.x, -rectangle.y);
    }
    
    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(this.scrollBarWidth, this.scrollBarWidth);
    }
    
    @Override
    protected void setThumbBounds(final int n, final int n2, final int n3, final int n4) {
        if (this.thumbRect.x == n && this.thumbRect.y == n2 && this.thumbRect.width == n3 && this.thumbRect.height == n4) {
            return;
        }
        final int min = Math.min(n, this.thumbRect.x);
        final int min2 = Math.min(n2, this.thumbRect.y);
        final int max = Math.max(n + n3, this.thumbRect.x + this.thumbRect.width);
        final int max2 = Math.max(n2 + n4, this.thumbRect.y + this.thumbRect.height);
        this.thumbRect.setBounds(n, n2, n3, n4);
        this.scrollbar.repaint(min, min2, max - min + 1, max2 - min2 + 1);
    }
    
    class ScrollBarListener extends PropertyChangeHandler
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("JScrollBar.isFreeStanding")) {
                this.handlePropertyChange(propertyChangeEvent.getNewValue());
            }
            else {
                super.propertyChange(propertyChangeEvent);
            }
        }
        
        public void handlePropertyChange(final Object o) {
            if (o != null) {
                final boolean booleanValue = (boolean)o;
                final boolean b = !booleanValue && MetalScrollBarUI.this.isFreeStanding;
                final boolean b2 = booleanValue && !MetalScrollBarUI.this.isFreeStanding;
                MetalScrollBarUI.this.isFreeStanding = booleanValue;
                if (b) {
                    this.toFlush();
                }
                else if (b2) {
                    this.toFreeStanding();
                }
            }
            else if (!MetalScrollBarUI.this.isFreeStanding) {
                MetalScrollBarUI.this.isFreeStanding = true;
                this.toFreeStanding();
            }
            if (MetalScrollBarUI.this.increaseButton != null) {
                MetalScrollBarUI.this.increaseButton.setFreeStanding(MetalScrollBarUI.this.isFreeStanding);
            }
            if (MetalScrollBarUI.this.decreaseButton != null) {
                MetalScrollBarUI.this.decreaseButton.setFreeStanding(MetalScrollBarUI.this.isFreeStanding);
            }
        }
        
        protected void toFlush() {
            final MetalScrollBarUI this$0 = MetalScrollBarUI.this;
            this$0.scrollBarWidth -= 2;
        }
        
        protected void toFreeStanding() {
            final MetalScrollBarUI this$0 = MetalScrollBarUI.this;
            this$0.scrollBarWidth += 2;
        }
    }
}
