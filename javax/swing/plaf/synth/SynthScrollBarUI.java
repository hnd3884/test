package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollBar;
import java.awt.LayoutManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SynthScrollBarUI extends BasicScrollBarUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private SynthStyle thumbStyle;
    private SynthStyle trackStyle;
    private boolean validMinimumThumbSize;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthScrollBarUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.trackHighlight = 0;
        if (this.scrollbar.getLayout() == null || this.scrollbar.getLayout() instanceof UIResource) {
            this.scrollbar.setLayout(this);
        }
        this.configureScrollBarColors();
        this.updateStyle(this.scrollbar);
    }
    
    @Override
    protected void configureScrollBarColors() {
    }
    
    private void updateStyle(final JScrollBar scrollBar) {
        final SynthStyle style = this.style;
        final SynthContext context = this.getContext(scrollBar, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.scrollBarWidth = this.style.getInt(context, "ScrollBar.thumbHeight", 14);
            this.minimumThumbSize = (Dimension)this.style.get(context, "ScrollBar.minimumThumbSize");
            if (this.minimumThumbSize == null) {
                this.minimumThumbSize = new Dimension();
                this.validMinimumThumbSize = false;
            }
            else {
                this.validMinimumThumbSize = true;
            }
            this.maximumThumbSize = (Dimension)this.style.get(context, "ScrollBar.maximumThumbSize");
            if (this.maximumThumbSize == null) {
                this.maximumThumbSize = new Dimension(4096, 4097);
            }
            this.incrGap = this.style.getInt(context, "ScrollBar.incrementButtonGap", 0);
            this.decrGap = this.style.getInt(context, "ScrollBar.decrementButtonGap", 0);
            final String s = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
            if (s != null) {
                if ("large".equals(s)) {
                    this.scrollBarWidth *= (int)1.15;
                    this.incrGap *= (int)1.15;
                    this.decrGap *= (int)1.15;
                }
                else if ("small".equals(s)) {
                    this.scrollBarWidth *= (int)0.857;
                    this.incrGap *= (int)0.857;
                    this.decrGap *= (int)0.857;
                }
                else if ("mini".equals(s)) {
                    this.scrollBarWidth *= (int)0.714;
                    this.incrGap *= (int)0.714;
                    this.decrGap *= (int)0.714;
                }
            }
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
        final SynthContext context2 = this.getContext(scrollBar, Region.SCROLL_BAR_TRACK, 1);
        this.trackStyle = SynthLookAndFeel.updateStyle(context2, this);
        context2.dispose();
        final SynthContext context3 = this.getContext(scrollBar, Region.SCROLL_BAR_THUMB, 1);
        this.thumbStyle = SynthLookAndFeel.updateStyle(context3, this);
        context3.dispose();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.scrollbar.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.scrollbar.removePropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.scrollbar, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        final SynthContext context2 = this.getContext(this.scrollbar, Region.SCROLL_BAR_TRACK, 1);
        this.trackStyle.uninstallDefaults(context2);
        context2.dispose();
        this.trackStyle = null;
        final SynthContext context3 = this.getContext(this.scrollbar, Region.SCROLL_BAR_THUMB, 1);
        this.thumbStyle.uninstallDefaults(context3);
        context3.dispose();
        this.thumbStyle = null;
        super.uninstallDefaults();
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
        SynthStyle synthStyle = this.trackStyle;
        if (region == Region.SCROLL_BAR_THUMB) {
            synthStyle = this.thumbStyle;
        }
        return SynthContext.getContext(component, region, synthStyle, n);
    }
    
    private int getComponentState(final JComponent component, final Region region) {
        if (region == Region.SCROLL_BAR_THUMB && this.isThumbRollover() && component.isEnabled()) {
            return 2;
        }
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    public boolean getSupportsAbsolutePositioning() {
        final SynthContext context = this.getContext(this.scrollbar);
        final boolean boolean1 = this.style.getBoolean(context, "ScrollBar.allowsAbsolutePositioning", false);
        context.dispose();
        return boolean1;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintScrollBarBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight(), this.scrollbar.getOrientation());
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
        final SynthContext context = this.getContext(this.scrollbar, Region.SCROLL_BAR_TRACK);
        this.paintTrack(context, graphics, this.getTrackBounds());
        context.dispose();
        final SynthContext context2 = this.getContext(this.scrollbar, Region.SCROLL_BAR_THUMB);
        this.paintThumb(context2, graphics, this.getThumbBounds());
        context2.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintScrollBarBorder(synthContext, graphics, n, n2, n3, n4, this.scrollbar.getOrientation());
    }
    
    protected void paintTrack(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle);
        synthContext.getPainter().paintScrollBarTrackBackground(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this.scrollbar.getOrientation());
        synthContext.getPainter().paintScrollBarTrackBorder(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this.scrollbar.getOrientation());
    }
    
    protected void paintThumb(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle);
        final int orientation = this.scrollbar.getOrientation();
        synthContext.getPainter().paintScrollBarThumbBackground(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, orientation);
        synthContext.getPainter().paintScrollBarThumbBorder(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, orientation);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Insets insets = component.getInsets();
        return (this.scrollbar.getOrientation() == 1) ? new Dimension(this.scrollBarWidth + insets.left + insets.right, 48) : new Dimension(48, this.scrollBarWidth + insets.top + insets.bottom);
    }
    
    @Override
    protected Dimension getMinimumThumbSize() {
        if (!this.validMinimumThumbSize) {
            if (this.scrollbar.getOrientation() == 1) {
                this.minimumThumbSize.width = this.scrollBarWidth;
                this.minimumThumbSize.height = 7;
            }
            else {
                this.minimumThumbSize.width = 7;
                this.minimumThumbSize.height = this.scrollBarWidth;
            }
        }
        return this.minimumThumbSize;
    }
    
    @Override
    protected JButton createDecreaseButton(final int n) {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(n) {
            @Override
            public boolean contains(final int n, final int n2) {
                if (SynthScrollBarUI.this.decrGap < 0) {
                    int width = this.getWidth();
                    int height = this.getHeight();
                    if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1) {
                        height += SynthScrollBarUI.this.decrGap;
                    }
                    else {
                        width += SynthScrollBarUI.this.decrGap;
                    }
                    return n >= 0 && n < width && n2 >= 0 && n2 < height;
                }
                return super.contains(n, n2);
            }
        };
        synthArrowButton.setName("ScrollBar.button");
        return synthArrowButton;
    }
    
    @Override
    protected JButton createIncreaseButton(final int n) {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(n) {
            @Override
            public boolean contains(int n, int n2) {
                if (SynthScrollBarUI.this.incrGap < 0) {
                    int width = this.getWidth();
                    int height = this.getHeight();
                    if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1) {
                        height += SynthScrollBarUI.this.incrGap;
                        n2 += SynthScrollBarUI.this.incrGap;
                    }
                    else {
                        width += SynthScrollBarUI.this.incrGap;
                        n += SynthScrollBarUI.this.incrGap;
                    }
                    return n >= 0 && n < width && n2 >= 0 && n2 < height;
                }
                return super.contains(n, n2);
            }
        };
        synthArrowButton.setName("ScrollBar.button");
        return synthArrowButton;
    }
    
    @Override
    protected void setThumbRollover(final boolean thumbRollover) {
        if (this.isThumbRollover() != thumbRollover) {
            this.scrollbar.repaint(this.getThumbBounds());
            super.setThumbRollover(thumbRollover);
        }
    }
    
    private void updateButtonDirections() {
        final int orientation = this.scrollbar.getOrientation();
        if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
            ((SynthArrowButton)this.incrButton).setDirection((orientation == 0) ? 3 : 5);
            ((SynthArrowButton)this.decrButton).setDirection((orientation == 0) ? 7 : 1);
        }
        else {
            ((SynthArrowButton)this.incrButton).setDirection((orientation == 0) ? 7 : 5);
            ((SynthArrowButton)this.decrButton).setDirection((orientation == 0) ? 3 : 1);
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JScrollBar)propertyChangeEvent.getSource());
        }
        if ("orientation" == propertyName) {
            this.updateButtonDirections();
        }
        else if ("componentOrientation" == propertyName) {
            this.updateButtonDirections();
        }
    }
}
