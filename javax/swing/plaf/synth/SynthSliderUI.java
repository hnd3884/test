package javax.swing.plaf.synth;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.FontMetrics;
import java.util.Enumeration;
import java.util.Dictionary;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

public class SynthSliderUI extends BasicSliderUI implements PropertyChangeListener, SynthUI
{
    private Rectangle valueRect;
    private boolean paintValue;
    private Dimension lastSize;
    private int trackHeight;
    private int trackBorder;
    private int thumbWidth;
    private int thumbHeight;
    private SynthStyle style;
    private SynthStyle sliderTrackStyle;
    private SynthStyle sliderThumbStyle;
    private transient boolean thumbActive;
    private transient boolean thumbPressed;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthSliderUI((JSlider)component);
    }
    
    protected SynthSliderUI(final JSlider slider) {
        super(slider);
        this.valueRect = new Rectangle();
    }
    
    @Override
    protected void installDefaults(final JSlider slider) {
        this.updateStyle(slider);
    }
    
    @Override
    protected void uninstallDefaults(final JSlider slider) {
        final SynthContext context = this.getContext(slider, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        final SynthContext context2 = this.getContext(slider, Region.SLIDER_TRACK, 1);
        this.sliderTrackStyle.uninstallDefaults(context2);
        context2.dispose();
        this.sliderTrackStyle = null;
        final SynthContext context3 = this.getContext(slider, Region.SLIDER_THUMB, 1);
        this.sliderThumbStyle.uninstallDefaults(context3);
        context3.dispose();
        this.sliderThumbStyle = null;
    }
    
    @Override
    protected void installListeners(final JSlider slider) {
        super.installListeners(slider);
        slider.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners(final JSlider slider) {
        slider.removePropertyChangeListener(this);
        super.uninstallListeners(slider);
    }
    
    private void updateStyle(final JSlider slider) {
        final SynthContext context = this.getContext(slider, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.thumbWidth = this.style.getInt(context, "Slider.thumbWidth", 30);
            this.thumbHeight = this.style.getInt(context, "Slider.thumbHeight", 14);
            final String s = (String)this.slider.getClientProperty("JComponent.sizeVariant");
            if (s != null) {
                if ("large".equals(s)) {
                    this.thumbWidth *= (int)1.15;
                    this.thumbHeight *= (int)1.15;
                }
                else if ("small".equals(s)) {
                    this.thumbWidth *= (int)0.857;
                    this.thumbHeight *= (int)0.857;
                }
                else if ("mini".equals(s)) {
                    this.thumbWidth *= (int)0.784;
                    this.thumbHeight *= (int)0.784;
                }
            }
            this.trackBorder = this.style.getInt(context, "Slider.trackBorder", 1);
            this.trackHeight = this.thumbHeight + this.trackBorder * 2;
            this.paintValue = this.style.getBoolean(context, "Slider.paintValue", true);
            if (style != null) {
                this.uninstallKeyboardActions(slider);
                this.installKeyboardActions(slider);
            }
        }
        context.dispose();
        final SynthContext context2 = this.getContext(slider, Region.SLIDER_TRACK, 1);
        this.sliderTrackStyle = SynthLookAndFeel.updateStyle(context2, this);
        context2.dispose();
        final SynthContext context3 = this.getContext(slider, Region.SLIDER_THUMB, 1);
        this.sliderThumbStyle = SynthLookAndFeel.updateStyle(context3, this);
        context3.dispose();
    }
    
    @Override
    protected TrackListener createTrackListener(final JSlider slider) {
        return new SynthTrackListener();
    }
    
    private void updateThumbState(final int n, final int n2) {
        this.setThumbActive(this.thumbRect.contains(n, n2));
    }
    
    private void updateThumbState(final int n, final int n2, final boolean thumbPressed) {
        this.updateThumbState(n, n2);
        this.setThumbPressed(thumbPressed);
    }
    
    private void setThumbActive(final boolean thumbActive) {
        if (this.thumbActive != thumbActive) {
            this.thumbActive = thumbActive;
            this.slider.repaint(this.thumbRect);
        }
    }
    
    private void setThumbPressed(final boolean thumbPressed) {
        if (this.thumbPressed != thumbPressed) {
            this.thumbPressed = thumbPressed;
            this.slider.repaint(this.thumbRect);
        }
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        if (this.slider.getPaintLabels() && this.labelsHaveSameBaselines()) {
            final Insets insets = new Insets(0, 0, 0, 0);
            final SynthContext context = this.getContext(this.slider, Region.SLIDER_TRACK);
            this.style.getInsets(context, insets);
            context.dispose();
            if (this.slider.getOrientation() == 0) {
                int maximumCharHeight = 0;
                if (this.paintValue) {
                    final SynthContext context2 = this.getContext(this.slider);
                    maximumCharHeight = context2.getStyle().getGraphicsUtils(context2).getMaximumCharHeight(context2);
                    context2.dispose();
                }
                int tickLength = 0;
                if (this.slider.getPaintTicks()) {
                    tickLength = this.getTickLength();
                }
                final int n3 = n2 / 2 - (maximumCharHeight + this.trackHeight + insets.top + insets.bottom + tickLength + this.getHeightOfTallestLabel() + 4) / 2 + (maximumCharHeight + 2) + (this.trackHeight + insets.top + insets.bottom) + (tickLength + 2);
                final JComponent component2 = this.slider.getLabelTable().elements().nextElement();
                final Dimension preferredSize = component2.getPreferredSize();
                return n3 + component2.getBaseline(preferredSize.width, preferredSize.height);
            }
            final Integer n4 = this.slider.getInverted() ? this.getLowestValue() : this.getHighestValue();
            if (n4 != null) {
                final int top = this.insetCache.top;
                int maximumCharHeight2 = 0;
                if (this.paintValue) {
                    final SynthContext context3 = this.getContext(this.slider);
                    maximumCharHeight2 = context3.getStyle().getGraphicsUtils(context3).getMaximumCharHeight(context3);
                    context3.dispose();
                }
                final int yPositionForValue = this.yPositionForValue(n4, top + maximumCharHeight2, n2 - this.insetCache.top - this.insetCache.bottom - maximumCharHeight2);
                final JComponent component3 = this.slider.getLabelTable().get(n4);
                final Dimension preferredSize2 = component3.getPreferredSize();
                return yPositionForValue - preferredSize2.height / 2 + component3.getBaseline(preferredSize2.width, preferredSize2.height);
            }
        }
        return -1;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        this.recalculateIfInsetsChanged();
        final Dimension dimension = new Dimension(this.contentRect.width, this.contentRect.height);
        if (this.slider.getOrientation() == 1) {
            dimension.height = 200;
        }
        else {
            dimension.width = 200;
        }
        final Insets insets = this.slider.getInsets();
        final Dimension dimension2 = dimension;
        dimension2.width += insets.left + insets.right;
        final Dimension dimension3 = dimension;
        dimension3.height += insets.top + insets.bottom;
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        this.recalculateIfInsetsChanged();
        final Dimension dimension = new Dimension(this.contentRect.width, this.contentRect.height);
        if (this.slider.getOrientation() == 1) {
            dimension.height = this.thumbRect.height + this.insetCache.top + this.insetCache.bottom;
        }
        else {
            dimension.width = this.thumbRect.width + this.insetCache.left + this.insetCache.right;
        }
        return dimension;
    }
    
    @Override
    protected void calculateGeometry() {
        this.calculateThumbSize();
        this.layout();
        this.calculateThumbLocation();
    }
    
    protected void layout() {
        final SynthContext context = this.getContext(this.slider);
        final SynthGraphicsUtils graphicsUtils = this.style.getGraphicsUtils(context);
        final Insets insets = new Insets(0, 0, 0, 0);
        final SynthContext context2 = this.getContext(this.slider, Region.SLIDER_TRACK);
        this.style.getInsets(context2, insets);
        context2.dispose();
        if (this.slider.getOrientation() == 0) {
            this.valueRect.height = 0;
            if (this.paintValue) {
                this.valueRect.height = graphicsUtils.getMaximumCharHeight(context);
            }
            this.trackRect.height = this.trackHeight;
            this.tickRect.height = 0;
            if (this.slider.getPaintTicks()) {
                this.tickRect.height = this.getTickLength();
            }
            this.labelRect.height = 0;
            if (this.slider.getPaintLabels()) {
                this.labelRect.height = this.getHeightOfTallestLabel();
            }
            this.contentRect.height = this.valueRect.height + this.trackRect.height + insets.top + insets.bottom + this.tickRect.height + this.labelRect.height + 4;
            this.contentRect.width = this.slider.getWidth() - this.insetCache.left - this.insetCache.right;
            int max = 0;
            if (this.slider.getPaintLabels()) {
                this.trackRect.x = this.insetCache.left;
                this.trackRect.width = this.contentRect.width;
                final Dictionary labelTable = this.slider.getLabelTable();
                if (labelTable != null) {
                    final int minimum = this.slider.getMinimum();
                    final int maximum = this.slider.getMaximum();
                    int n = Integer.MAX_VALUE;
                    int n2 = Integer.MIN_VALUE;
                    final Enumeration keys = labelTable.keys();
                    while (keys.hasMoreElements()) {
                        final int intValue = (int)keys.nextElement();
                        if (intValue >= minimum && intValue < n) {
                            n = intValue;
                        }
                        if (intValue <= maximum && intValue > n2) {
                            n2 = intValue;
                        }
                    }
                    max = Math.max(this.getPadForLabel(n), this.getPadForLabel(n2));
                }
            }
            final Rectangle valueRect = this.valueRect;
            final Rectangle trackRect = this.trackRect;
            final Rectangle tickRect = this.tickRect;
            final Rectangle labelRect = this.labelRect;
            final int n3 = this.insetCache.left + max;
            labelRect.x = n3;
            tickRect.x = n3;
            trackRect.x = n3;
            valueRect.x = n3;
            final Rectangle valueRect2 = this.valueRect;
            final Rectangle trackRect2 = this.trackRect;
            final Rectangle tickRect2 = this.tickRect;
            final Rectangle labelRect2 = this.labelRect;
            final int n4 = this.contentRect.width - max * 2;
            labelRect2.width = n4;
            tickRect2.width = n4;
            trackRect2.width = n4;
            valueRect2.width = n4;
            final int y = this.slider.getHeight() / 2 - this.contentRect.height / 2;
            this.valueRect.y = y;
            final int n5 = y + (this.valueRect.height + 2);
            this.trackRect.y = n5 + insets.top;
            final int y2 = n5 + (this.trackRect.height + insets.top + insets.bottom);
            this.tickRect.y = y2;
            final int y3 = y2 + (this.tickRect.height + 2);
            this.labelRect.y = y3;
            final int n6 = y3 + this.labelRect.height;
        }
        else {
            this.trackRect.width = this.trackHeight;
            this.tickRect.width = 0;
            if (this.slider.getPaintTicks()) {
                this.tickRect.width = this.getTickLength();
            }
            this.labelRect.width = 0;
            if (this.slider.getPaintLabels()) {
                this.labelRect.width = this.getWidthOfWidestLabel();
            }
            this.valueRect.y = this.insetCache.top;
            this.valueRect.height = 0;
            if (this.paintValue) {
                this.valueRect.height = graphicsUtils.getMaximumCharHeight(context);
            }
            final FontMetrics fontMetrics = this.slider.getFontMetrics(this.slider.getFont());
            this.valueRect.width = Math.max(graphicsUtils.computeStringWidth(context, this.slider.getFont(), fontMetrics, "" + this.slider.getMaximum()), graphicsUtils.computeStringWidth(context, this.slider.getFont(), fontMetrics, "" + this.slider.getMinimum()));
            final int n7 = this.valueRect.width / 2;
            final int n8 = insets.left + this.trackRect.width / 2;
            final int n9 = this.trackRect.width / 2 + insets.right + this.tickRect.width + this.labelRect.width;
            this.contentRect.width = Math.max(n8, n7) + Math.max(n9, n7) + 2 + this.insetCache.left + this.insetCache.right;
            this.contentRect.height = this.slider.getHeight() - this.insetCache.top - this.insetCache.bottom;
            final Rectangle trackRect3 = this.trackRect;
            final Rectangle tickRect3 = this.tickRect;
            final Rectangle labelRect3 = this.labelRect;
            final int y4 = this.valueRect.y + this.valueRect.height;
            labelRect3.y = y4;
            tickRect3.y = y4;
            trackRect3.y = y4;
            final Rectangle trackRect4 = this.trackRect;
            final Rectangle tickRect4 = this.tickRect;
            final Rectangle labelRect4 = this.labelRect;
            final int height = this.contentRect.height - this.valueRect.height;
            labelRect4.height = height;
            tickRect4.height = height;
            trackRect4.height = height;
            int x = this.slider.getWidth() / 2 - this.contentRect.width / 2;
            if (SynthLookAndFeel.isLeftToRight(this.slider)) {
                if (n7 > n8) {
                    x += n7 - n8;
                }
                this.trackRect.x = x + insets.left;
                final int x2 = x + (insets.left + this.trackRect.width + insets.right);
                this.tickRect.x = x2;
                this.labelRect.x = x2 + this.tickRect.width + 2;
            }
            else {
                if (n7 > n9) {
                    x += n7 - n9;
                }
                this.labelRect.x = x;
                final int x3 = x + (this.labelRect.width + 2);
                this.tickRect.x = x3;
                this.trackRect.x = x3 + this.tickRect.width + insets.left;
            }
        }
        context.dispose();
        this.lastSize = this.slider.getSize();
    }
    
    private int getPadForLabel(final int n) {
        int n2 = 0;
        final JComponent component = this.slider.getLabelTable().get(n);
        if (component != null) {
            final int xPositionForValue = this.xPositionForValue(n);
            final int n3 = component.getPreferredSize().width / 2;
            if (xPositionForValue - n3 < this.insetCache.left) {
                n2 = Math.max(n2, this.insetCache.left - (xPositionForValue - n3));
            }
            if (xPositionForValue + n3 > this.slider.getWidth() - this.insetCache.right) {
                n2 = Math.max(n2, xPositionForValue + n3 - (this.slider.getWidth() - this.insetCache.right));
            }
        }
        return n2;
    }
    
    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        if (this.slider.getOrientation() == 0) {
            final Rectangle thumbRect = this.thumbRect;
            thumbRect.y += this.trackBorder;
        }
        else {
            final Rectangle thumbRect2 = this.thumbRect;
            thumbRect2.x += this.trackBorder;
        }
        final Point mousePosition = this.slider.getMousePosition();
        if (mousePosition != null) {
            this.updateThumbState(mousePosition.x, mousePosition.y);
        }
    }
    
    @Override
    public void setThumbLocation(final int n, final int n2) {
        super.setThumbLocation(n, n2);
        this.slider.repaint(this.valueRect.x, this.valueRect.y, this.valueRect.width, this.valueRect.height);
        this.setThumbActive(false);
    }
    
    @Override
    protected int xPositionForValue(final int n) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final int n2 = this.trackRect.x + this.thumbRect.width / 2 + this.trackBorder;
        final int n3 = this.trackRect.x + this.trackRect.width - this.thumbRect.width / 2 - this.trackBorder;
        final double n4 = (n3 - n2) / (maximum - (double)minimum);
        int n5;
        if (!this.drawInverted()) {
            n5 = (int)(n2 + Math.round(n4 * (n - (double)minimum)));
        }
        else {
            n5 = (int)(n3 - Math.round(n4 * (n - (double)minimum)));
        }
        return Math.min(n3, Math.max(n2, n5));
    }
    
    @Override
    protected int yPositionForValue(final int n, final int n2, final int n3) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final int n4 = n2 + this.thumbRect.height / 2 + this.trackBorder;
        final int n5 = n2 + n3 - this.thumbRect.height / 2 - this.trackBorder;
        final double n6 = (n5 - n4) / (maximum - (double)minimum);
        int n7;
        if (!this.drawInverted()) {
            n7 = (int)(n4 + Math.round(n6 * (maximum - (double)n)));
        }
        else {
            n7 = (int)(n4 + Math.round(n6 * (n - (double)minimum)));
        }
        return Math.min(n5, Math.max(n4, n7));
    }
    
    @Override
    public int valueForYPosition(final int n) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final int n2 = this.trackRect.y + this.thumbRect.height / 2 + this.trackBorder;
        final int n3 = this.trackRect.y + this.trackRect.height - this.thumbRect.height / 2 - this.trackBorder;
        final int n4 = n3 - n2;
        int n5;
        if (n <= n2) {
            n5 = (this.drawInverted() ? minimum : maximum);
        }
        else if (n >= n3) {
            n5 = (this.drawInverted() ? maximum : minimum);
        }
        else {
            final int n6 = (int)Math.round((n - n2) * ((maximum - (double)minimum) / n4));
            n5 = (this.drawInverted() ? (minimum + n6) : (maximum - n6));
        }
        return n5;
    }
    
    @Override
    public int valueForXPosition(final int n) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final int n2 = this.trackRect.x + this.thumbRect.width / 2 + this.trackBorder;
        final int n3 = this.trackRect.x + this.trackRect.width - this.thumbRect.width / 2 - this.trackBorder;
        final int n4 = n3 - n2;
        int n5;
        if (n <= n2) {
            n5 = (this.drawInverted() ? maximum : minimum);
        }
        else if (n >= n3) {
            n5 = (this.drawInverted() ? minimum : maximum);
        }
        else {
            final int n6 = (int)Math.round((n - n2) * ((maximum - (double)minimum) / n4));
            n5 = (this.drawInverted() ? (maximum - n6) : (minimum + n6));
        }
        return n5;
    }
    
    @Override
    protected Dimension getThumbSize() {
        final Dimension dimension = new Dimension();
        if (this.slider.getOrientation() == 1) {
            dimension.width = this.thumbHeight;
            dimension.height = this.thumbWidth;
        }
        else {
            dimension.width = this.thumbWidth;
            dimension.height = this.thumbHeight;
        }
        return dimension;
    }
    
    @Override
    protected void recalculateIfInsetsChanged() {
        final SynthContext context = this.getContext(this.slider);
        final Insets insets = this.style.getInsets(context, null);
        final Insets insets2 = this.slider.getInsets();
        final Insets insets3 = insets;
        insets3.left += insets2.left;
        final Insets insets4 = insets;
        insets4.right += insets2.right;
        final Insets insets5 = insets;
        insets5.top += insets2.top;
        final Insets insets6 = insets;
        insets6.bottom += insets2.bottom;
        if (!insets.equals(this.insetCache)) {
            this.insetCache = insets;
            this.calculateGeometry();
        }
        context.dispose();
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private SynthContext getContext(final JComponent component, final Region region) {
        return this.getContext(component, region, this.getComponentState(component, region));
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final int n) {
        SynthStyle synthStyle = null;
        if (region == Region.SLIDER_TRACK) {
            synthStyle = this.sliderTrackStyle;
        }
        else if (region == Region.SLIDER_THUMB) {
            synthStyle = this.sliderThumbStyle;
        }
        return SynthContext.getContext(component, region, synthStyle, n);
    }
    
    private int getComponentState(final JComponent component, final Region region) {
        if (region == Region.SLIDER_THUMB && this.thumbActive && component.isEnabled()) {
            int n = this.thumbPressed ? 4 : 2;
            if (component.isFocusOwner()) {
                n |= 0x100;
            }
            return n;
        }
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintSliderBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight(), this.slider.getOrientation());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        this.recalculateIfInsetsChanged();
        this.recalculateIfOrientationChanged();
        final Rectangle clipBounds = graphics.getClipBounds();
        if (this.lastSize == null || !this.lastSize.equals(this.slider.getSize())) {
            this.calculateGeometry();
        }
        if (this.paintValue) {
            final int computeStringWidth = synthContext.getStyle().getGraphicsUtils(synthContext).computeStringWidth(synthContext, graphics.getFont(), SwingUtilities2.getFontMetrics(this.slider, graphics), "" + this.slider.getValue());
            this.valueRect.x = this.thumbRect.x + (this.thumbRect.width - computeStringWidth) / 2;
            if (this.slider.getOrientation() == 0) {
                if (this.valueRect.x + computeStringWidth > this.insetCache.left + this.contentRect.width) {
                    this.valueRect.x = this.insetCache.left + this.contentRect.width - computeStringWidth;
                }
                this.valueRect.x = Math.max(this.valueRect.x, 0);
            }
            graphics.setColor(synthContext.getStyle().getColor(synthContext, ColorType.TEXT_FOREGROUND));
            synthContext.getStyle().getGraphicsUtils(synthContext).paintText(synthContext, graphics, "" + this.slider.getValue(), this.valueRect.x, this.valueRect.y, -1);
        }
        if (this.slider.getPaintTrack() && clipBounds.intersects(this.trackRect)) {
            final SynthContext context = this.getContext(this.slider, Region.SLIDER_TRACK);
            this.paintTrack(context, graphics, this.trackRect);
            context.dispose();
        }
        if (clipBounds.intersects(this.thumbRect)) {
            final SynthContext context2 = this.getContext(this.slider, Region.SLIDER_THUMB);
            this.paintThumb(context2, graphics, this.thumbRect);
            context2.dispose();
        }
        if (this.slider.getPaintTicks() && clipBounds.intersects(this.tickRect)) {
            this.paintTicks(graphics);
        }
        if (this.slider.getPaintLabels() && clipBounds.intersects(this.labelRect)) {
            this.paintLabels(graphics);
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintSliderBorder(synthContext, graphics, n, n2, n3, n4, this.slider.getOrientation());
    }
    
    protected void paintThumb(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        final int orientation = this.slider.getOrientation();
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle);
        synthContext.getPainter().paintSliderThumbBackground(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, orientation);
        synthContext.getPainter().paintSliderThumbBorder(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, orientation);
    }
    
    protected void paintTrack(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        final int orientation = this.slider.getOrientation();
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle);
        synthContext.getPainter().paintSliderTrackBackground(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, orientation);
        synthContext.getPainter().paintSliderTrackBorder(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, orientation);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JSlider)propertyChangeEvent.getSource());
        }
    }
    
    private class SynthTrackListener extends TrackListener
    {
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            SynthSliderUI.this.setThumbActive(false);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            super.mousePressed(mouseEvent);
            SynthSliderUI.this.setThumbPressed(SynthSliderUI.this.thumbRect.contains(mouseEvent.getX(), mouseEvent.getY()));
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            super.mouseReleased(mouseEvent);
            SynthSliderUI.this.updateThumbState(mouseEvent.getX(), mouseEvent.getY(), false);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (!SynthSliderUI.this.slider.isEnabled()) {
                return;
            }
            this.currentMouseX = mouseEvent.getX();
            this.currentMouseY = mouseEvent.getY();
            if (!BasicSliderUI.this.isDragging()) {
                return;
            }
            SynthSliderUI.this.slider.setValueIsAdjusting(true);
            switch (SynthSliderUI.this.slider.getOrientation()) {
                case 1: {
                    final int n = SynthSliderUI.this.thumbRect.height / 2;
                    final int n2 = mouseEvent.getY() - this.offset;
                    final int y = SynthSliderUI.this.trackRect.y;
                    int n3 = SynthSliderUI.this.trackRect.y + SynthSliderUI.this.trackRect.height - n - SynthSliderUI.this.trackBorder;
                    final int access$1600 = BasicSliderUI.this.yPositionForValue(SynthSliderUI.this.slider.getMaximum() - SynthSliderUI.this.slider.getExtent());
                    int n4;
                    if (BasicSliderUI.this.drawInverted()) {
                        n3 = access$1600;
                        n4 = y + n;
                    }
                    else {
                        n4 = access$1600;
                    }
                    final int min = Math.min(Math.max(n2, n4 - n), n3 - n);
                    SynthSliderUI.this.setThumbLocation(SynthSliderUI.this.thumbRect.x, min);
                    SynthSliderUI.this.slider.setValue(SynthSliderUI.this.valueForYPosition(min + n));
                    break;
                }
                case 0: {
                    final int n5 = SynthSliderUI.this.thumbRect.width / 2;
                    final int n6 = mouseEvent.getX() - this.offset;
                    int n7 = SynthSliderUI.this.trackRect.x + n5 + SynthSliderUI.this.trackBorder;
                    int n8 = SynthSliderUI.this.trackRect.x + SynthSliderUI.this.trackRect.width - n5 - SynthSliderUI.this.trackBorder;
                    final int xPositionForValue = SynthSliderUI.this.xPositionForValue(SynthSliderUI.this.slider.getMaximum() - SynthSliderUI.this.slider.getExtent());
                    if (BasicSliderUI.this.drawInverted()) {
                        n7 = xPositionForValue;
                    }
                    else {
                        n8 = xPositionForValue;
                    }
                    final int min2 = Math.min(Math.max(n6, n7 - n5), n8 - n5);
                    SynthSliderUI.this.setThumbLocation(min2, SynthSliderUI.this.thumbRect.y);
                    SynthSliderUI.this.slider.setValue(SynthSliderUI.this.valueForXPosition(min2 + n5));
                    break;
                }
                default: {
                    return;
                }
            }
            if (SynthSliderUI.this.slider.getValueIsAdjusting()) {
                SynthSliderUI.this.setThumbActive(true);
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            SynthSliderUI.this.updateThumbState(mouseEvent.getX(), mouseEvent.getY());
        }
    }
}
