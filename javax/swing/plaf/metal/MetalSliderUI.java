package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.plaf.basic.BasicSliderUI;

public class MetalSliderUI extends BasicSliderUI
{
    protected final int TICK_BUFFER = 4;
    protected boolean filledSlider;
    protected static Color thumbColor;
    protected static Color highlightColor;
    protected static Color darkShadowColor;
    protected static int trackWidth;
    protected static int tickLength;
    private int safeLength;
    protected static Icon horizThumbIcon;
    protected static Icon vertThumbIcon;
    private static Icon SAFE_HORIZ_THUMB_ICON;
    private static Icon SAFE_VERT_THUMB_ICON;
    protected final String SLIDER_FILL = "JSlider.isFilled";
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalSliderUI();
    }
    
    public MetalSliderUI() {
        super(null);
        this.filledSlider = false;
    }
    
    private static Icon getHorizThumbIcon() {
        if (System.getSecurityManager() != null) {
            return MetalSliderUI.SAFE_HORIZ_THUMB_ICON;
        }
        return MetalSliderUI.horizThumbIcon;
    }
    
    private static Icon getVertThumbIcon() {
        if (System.getSecurityManager() != null) {
            return MetalSliderUI.SAFE_VERT_THUMB_ICON;
        }
        return MetalSliderUI.vertThumbIcon;
    }
    
    @Override
    public void installUI(final JComponent component) {
        MetalSliderUI.trackWidth = (int)UIManager.get("Slider.trackWidth");
        final int intValue = (int)UIManager.get("Slider.majorTickLength");
        this.safeLength = intValue;
        MetalSliderUI.tickLength = intValue;
        MetalSliderUI.horizThumbIcon = (MetalSliderUI.SAFE_HORIZ_THUMB_ICON = UIManager.getIcon("Slider.horizontalThumbIcon"));
        MetalSliderUI.vertThumbIcon = (MetalSliderUI.SAFE_VERT_THUMB_ICON = UIManager.getIcon("Slider.verticalThumbIcon"));
        super.installUI(component);
        MetalSliderUI.thumbColor = UIManager.getColor("Slider.thumb");
        MetalSliderUI.highlightColor = UIManager.getColor("Slider.highlight");
        MetalSliderUI.darkShadowColor = UIManager.getColor("Slider.darkShadow");
        this.scrollListener.setScrollByBlock(false);
        this.prepareFilledSliderField();
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener(final JSlider slider) {
        return new MetalPropertyListener();
    }
    
    private void prepareFilledSliderField() {
        this.filledSlider = MetalLookAndFeel.usingOcean();
        final Object clientProperty = this.slider.getClientProperty("JSlider.isFilled");
        if (clientProperty != null) {
            this.filledSlider = (boolean)clientProperty;
        }
    }
    
    @Override
    public void paintThumb(final Graphics graphics) {
        final Rectangle thumbRect = this.thumbRect;
        graphics.translate(thumbRect.x, thumbRect.y);
        if (this.slider.getOrientation() == 0) {
            getHorizThumbIcon().paintIcon(this.slider, graphics, 0, 0);
        }
        else {
            getVertThumbIcon().paintIcon(this.slider, graphics, 0, 0);
        }
        graphics.translate(-thumbRect.x, -thumbRect.y);
    }
    
    private Rectangle getPaintTrackRect() {
        int thumbOverhang = 0;
        int n = 0;
        int n2;
        int n3;
        if (this.slider.getOrientation() == 0) {
            n2 = this.trackRect.height - 1 - this.getThumbOverhang();
            n = n2 - (this.getTrackWidth() - 1);
            n3 = this.trackRect.width - 1;
        }
        else {
            if (MetalUtils.isLeftToRight(this.slider)) {
                thumbOverhang = this.trackRect.width - this.getThumbOverhang() - this.getTrackWidth();
                n3 = this.trackRect.width - this.getThumbOverhang() - 1;
            }
            else {
                thumbOverhang = this.getThumbOverhang();
                n3 = this.getThumbOverhang() + this.getTrackWidth() - 1;
            }
            n2 = this.trackRect.height - 1;
        }
        return new Rectangle(this.trackRect.x + thumbOverhang, this.trackRect.y + n, n3 - thumbOverhang, n2 - n);
    }
    
    @Override
    public void paintTrack(final Graphics graphics) {
        if (MetalLookAndFeel.usingOcean()) {
            this.oceanPaintTrack(graphics);
            return;
        }
        final Color color = this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow();
        final boolean leftToRight = MetalUtils.isLeftToRight(this.slider);
        graphics.translate(this.trackRect.x, this.trackRect.y);
        int thumbOverhang = 0;
        int n = 0;
        int n2;
        int n3;
        if (this.slider.getOrientation() == 0) {
            n2 = this.trackRect.height - 1 - this.getThumbOverhang();
            n = n2 - (this.getTrackWidth() - 1);
            n3 = this.trackRect.width - 1;
        }
        else {
            if (leftToRight) {
                thumbOverhang = this.trackRect.width - this.getThumbOverhang() - this.getTrackWidth();
                n3 = this.trackRect.width - this.getThumbOverhang() - 1;
            }
            else {
                thumbOverhang = this.getThumbOverhang();
                n3 = this.getThumbOverhang() + this.getTrackWidth() - 1;
            }
            n2 = this.trackRect.height - 1;
        }
        if (this.slider.isEnabled()) {
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.drawRect(thumbOverhang, n, n3 - thumbOverhang - 1, n2 - n - 1);
            graphics.setColor(MetalLookAndFeel.getControlHighlight());
            graphics.drawLine(thumbOverhang + 1, n2, n3, n2);
            graphics.drawLine(n3, n + 1, n3, n2);
            graphics.setColor(MetalLookAndFeel.getControlShadow());
            graphics.drawLine(thumbOverhang + 1, n + 1, n3 - 2, n + 1);
            graphics.drawLine(thumbOverhang + 1, n + 1, thumbOverhang + 1, n2 - 2);
        }
        else {
            graphics.setColor(MetalLookAndFeel.getControlShadow());
            graphics.drawRect(thumbOverhang, n, n3 - thumbOverhang - 1, n2 - n - 1);
        }
        if (this.filledSlider) {
            int n5;
            int n6;
            int n7;
            int n8;
            if (this.slider.getOrientation() == 0) {
                final int n4 = this.thumbRect.x + this.thumbRect.width / 2 - this.trackRect.x;
                n5 = (this.slider.isEnabled() ? (n + 1) : n);
                n6 = (this.slider.isEnabled() ? (n2 - 2) : (n2 - 1));
                if (!this.drawInverted()) {
                    n7 = (this.slider.isEnabled() ? (thumbOverhang + 1) : thumbOverhang);
                    n8 = n4;
                }
                else {
                    n7 = n4;
                    n8 = (this.slider.isEnabled() ? (n3 - 2) : (n3 - 1));
                }
            }
            else {
                final int n9 = this.thumbRect.y + this.thumbRect.height / 2 - this.trackRect.y;
                n7 = (this.slider.isEnabled() ? (thumbOverhang + 1) : thumbOverhang);
                n8 = (this.slider.isEnabled() ? (n3 - 2) : (n3 - 1));
                if (!this.drawInverted()) {
                    n5 = n9;
                    n6 = (this.slider.isEnabled() ? (n2 - 2) : (n2 - 1));
                }
                else {
                    n5 = (this.slider.isEnabled() ? (n + 1) : n);
                    n6 = n9;
                }
            }
            if (this.slider.isEnabled()) {
                graphics.setColor(this.slider.getBackground());
                graphics.drawLine(n7, n5, n8, n5);
                graphics.drawLine(n7, n5, n7, n6);
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                graphics.fillRect(n7 + 1, n5 + 1, n8 - n7, n6 - n5);
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                graphics.fillRect(n7, n5, n8 - n7, n6 - n5);
            }
        }
        graphics.translate(-this.trackRect.x, -this.trackRect.y);
    }
    
    private void oceanPaintTrack(final Graphics graphics) {
        final boolean leftToRight = MetalUtils.isLeftToRight(this.slider);
        final boolean drawInverted = this.drawInverted();
        final Color color = (Color)UIManager.get("Slider.altTrackColor");
        final Rectangle paintTrackRect = this.getPaintTrackRect();
        graphics.translate(paintTrackRect.x, paintTrackRect.y);
        final int width = paintTrackRect.width;
        final int height = paintTrackRect.height;
        if (this.slider.getOrientation() == 0) {
            final int n = this.thumbRect.x + this.thumbRect.width / 2 - paintTrackRect.x;
            if (this.slider.isEnabled()) {
                if (n > 0) {
                    graphics.setColor(drawInverted ? MetalLookAndFeel.getControlDarkShadow() : MetalLookAndFeel.getPrimaryControlDarkShadow());
                    graphics.drawRect(0, 0, n - 1, height - 1);
                }
                if (n < width) {
                    graphics.setColor(drawInverted ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(n, 0, width - n - 1, height - 1);
                }
                if (this.filledSlider) {
                    graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
                    int n2;
                    int n3;
                    if (drawInverted) {
                        n2 = n;
                        n3 = width - 2;
                        graphics.drawLine(1, 1, n, 1);
                    }
                    else {
                        n2 = 1;
                        n3 = n;
                        graphics.drawLine(n, 1, width - 1, 1);
                    }
                    if (height == 6) {
                        graphics.setColor(MetalLookAndFeel.getWhite());
                        graphics.drawLine(n2, 1, n3, 1);
                        graphics.setColor(color);
                        graphics.drawLine(n2, 2, n3, 2);
                        graphics.setColor(MetalLookAndFeel.getControlShadow());
                        graphics.drawLine(n2, 3, n3, 3);
                        graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
                        graphics.drawLine(n2, 4, n3, 4);
                    }
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                if (n > 0) {
                    if (!drawInverted && this.filledSlider) {
                        graphics.fillRect(0, 0, n - 1, height - 1);
                    }
                    else {
                        graphics.drawRect(0, 0, n - 1, height - 1);
                    }
                }
                if (n < width) {
                    if (drawInverted && this.filledSlider) {
                        graphics.fillRect(n, 0, width - n - 1, height - 1);
                    }
                    else {
                        graphics.drawRect(n, 0, width - n - 1, height - 1);
                    }
                }
            }
        }
        else {
            final int n4 = this.thumbRect.y + this.thumbRect.height / 2 - paintTrackRect.y;
            if (this.slider.isEnabled()) {
                if (n4 > 0) {
                    graphics.setColor(drawInverted ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(0, 0, width - 1, n4 - 1);
                }
                if (n4 < height) {
                    graphics.setColor(drawInverted ? MetalLookAndFeel.getControlDarkShadow() : MetalLookAndFeel.getPrimaryControlDarkShadow());
                    graphics.drawRect(0, n4, width - 1, height - n4 - 1);
                }
                if (this.filledSlider) {
                    graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
                    int n5;
                    int n6;
                    if (this.drawInverted()) {
                        n5 = 1;
                        n6 = n4;
                        if (leftToRight) {
                            graphics.drawLine(1, n4, 1, height - 1);
                        }
                        else {
                            graphics.drawLine(width - 2, n4, width - 2, height - 1);
                        }
                    }
                    else {
                        n5 = n4;
                        n6 = height - 2;
                        if (leftToRight) {
                            graphics.drawLine(1, 1, 1, n4);
                        }
                        else {
                            graphics.drawLine(width - 2, 1, width - 2, n4);
                        }
                    }
                    if (width == 6) {
                        graphics.setColor(leftToRight ? MetalLookAndFeel.getWhite() : MetalLookAndFeel.getPrimaryControlShadow());
                        graphics.drawLine(1, n5, 1, n6);
                        graphics.setColor(leftToRight ? color : MetalLookAndFeel.getControlShadow());
                        graphics.drawLine(2, n5, 2, n6);
                        graphics.setColor(leftToRight ? MetalLookAndFeel.getControlShadow() : color);
                        graphics.drawLine(3, n5, 3, n6);
                        graphics.setColor(leftToRight ? MetalLookAndFeel.getPrimaryControlShadow() : MetalLookAndFeel.getWhite());
                        graphics.drawLine(4, n5, 4, n6);
                    }
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                if (n4 > 0) {
                    if (drawInverted && this.filledSlider) {
                        graphics.fillRect(0, 0, width - 1, n4 - 1);
                    }
                    else {
                        graphics.drawRect(0, 0, width - 1, n4 - 1);
                    }
                }
                if (n4 < height) {
                    if (!drawInverted && this.filledSlider) {
                        graphics.fillRect(0, n4, width - 1, height - n4 - 1);
                    }
                    else {
                        graphics.drawRect(0, n4, width - 1, height - n4 - 1);
                    }
                }
            }
        }
        graphics.translate(-paintTrackRect.x, -paintTrackRect.y);
    }
    
    @Override
    public void paintFocus(final Graphics graphics) {
    }
    
    @Override
    protected Dimension getThumbSize() {
        final Dimension dimension = new Dimension();
        if (this.slider.getOrientation() == 1) {
            dimension.width = getVertThumbIcon().getIconWidth();
            dimension.height = getVertThumbIcon().getIconHeight();
        }
        else {
            dimension.width = getHorizThumbIcon().getIconWidth();
            dimension.height = getHorizThumbIcon().getIconHeight();
        }
        return dimension;
    }
    
    public int getTickLength() {
        return (this.slider.getOrientation() == 0) ? (this.safeLength + 4 + 1) : (this.safeLength + 4 + 3);
    }
    
    protected int getTrackWidth() {
        if (this.slider.getOrientation() == 0) {
            return (int)(0.4375 * this.thumbRect.height);
        }
        return (int)(0.4375 * this.thumbRect.width);
    }
    
    protected int getTrackLength() {
        if (this.slider.getOrientation() == 0) {
            return this.trackRect.width;
        }
        return this.trackRect.height;
    }
    
    protected int getThumbOverhang() {
        return (int)(this.getThumbSize().getHeight() - this.getTrackWidth()) / 2;
    }
    
    @Override
    protected void scrollDueToClickInTrack(final int n) {
        this.scrollByUnit(n);
    }
    
    @Override
    protected void paintMinorTickForHorizSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.setColor(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow());
        graphics.drawLine(n, 4, n, 4 + this.safeLength / 2);
    }
    
    @Override
    protected void paintMajorTickForHorizSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.setColor(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow());
        graphics.drawLine(n, 4, n, 4 + (this.safeLength - 1));
    }
    
    @Override
    protected void paintMinorTickForVertSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.setColor(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow());
        if (MetalUtils.isLeftToRight(this.slider)) {
            graphics.drawLine(4, n, 4 + this.safeLength / 2, n);
        }
        else {
            graphics.drawLine(0, n, this.safeLength / 2, n);
        }
    }
    
    @Override
    protected void paintMajorTickForVertSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.setColor(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow());
        if (MetalUtils.isLeftToRight(this.slider)) {
            graphics.drawLine(4, n, 4 + this.safeLength, n);
        }
        else {
            graphics.drawLine(0, n, this.safeLength, n);
        }
    }
    
    protected class MetalPropertyListener extends PropertyChangeHandler
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            super.propertyChange(propertyChangeEvent);
            if (propertyChangeEvent.getPropertyName().equals("JSlider.isFilled")) {
                MetalSliderUI.this.prepareFilledSliderField();
            }
        }
    }
}
