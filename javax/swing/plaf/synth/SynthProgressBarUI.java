package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import sun.swing.SwingUtilities2;
import java.awt.Shape;
import java.awt.Insets;
import java.awt.Graphics;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class SynthProgressBarUI extends BasicProgressBarUI implements SynthUI, PropertyChangeListener
{
    private SynthStyle style;
    private int progressPadding;
    private boolean rotateText;
    private boolean paintOutsideClip;
    private boolean tileWhenIndeterminate;
    private int tileWidth;
    private Dimension minBarSize;
    private int glowWidth;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthProgressBarUI();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.progressBar.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.progressBar.removePropertyChangeListener(this);
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.progressBar);
    }
    
    private void updateStyle(final JProgressBar progressBar) {
        final SynthContext context = this.getContext(progressBar, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        this.setCellLength(this.style.getInt(context, "ProgressBar.cellLength", 1));
        this.setCellSpacing(this.style.getInt(context, "ProgressBar.cellSpacing", 0));
        this.progressPadding = this.style.getInt(context, "ProgressBar.progressPadding", 0);
        this.paintOutsideClip = this.style.getBoolean(context, "ProgressBar.paintOutsideClip", false);
        this.rotateText = this.style.getBoolean(context, "ProgressBar.rotateText", false);
        this.tileWhenIndeterminate = this.style.getBoolean(context, "ProgressBar.tileWhenIndeterminate", false);
        this.tileWidth = this.style.getInt(context, "ProgressBar.tileWidth", 15);
        final String s = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
        if (s != null) {
            if ("large".equals(s)) {
                this.tileWidth *= (int)1.15;
            }
            else if ("small".equals(s)) {
                this.tileWidth *= (int)0.857;
            }
            else if ("mini".equals(s)) {
                this.tileWidth *= (int)0.784;
            }
        }
        this.minBarSize = (Dimension)this.style.get(context, "ProgressBar.minBarSize");
        this.glowWidth = this.style.getInt(context, "ProgressBar.glowWidth", 0);
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.progressBar, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
            final SynthContext context = this.getContext(component);
            final FontMetrics fontMetrics = this.progressBar.getFontMetrics(context.getStyle().getFont(context));
            context.dispose();
            return (n2 - fontMetrics.getAscent() - fontMetrics.getDescent()) / 2 + fontMetrics.getAscent();
        }
        return -1;
    }
    
    @Override
    protected Rectangle getBox(final Rectangle rectangle) {
        if (this.tileWhenIndeterminate) {
            return SwingUtilities.calculateInnerArea(this.progressBar, rectangle);
        }
        return super.getBox(rectangle);
    }
    
    @Override
    protected void setAnimationIndex(final int n) {
        if (this.paintOutsideClip) {
            if (this.getAnimationIndex() == n) {
                return;
            }
            super.setAnimationIndex(n);
            this.progressBar.repaint();
        }
        else {
            super.setAnimationIndex(n);
        }
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintProgressBarBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight(), this.progressBar.getOrientation());
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
        final JProgressBar progressBar = (JProgressBar)synthContext.getComponent();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        if (!progressBar.isIndeterminate()) {
            final Insets insets = progressBar.getInsets();
            final double percentComplete = progressBar.getPercentComplete();
            if (percentComplete != 0.0) {
                if (progressBar.getOrientation() == 0) {
                    n = insets.left + this.progressPadding;
                    n2 = insets.top + this.progressPadding;
                    n3 = (int)(percentComplete * (progressBar.getWidth() - (insets.left + this.progressPadding + insets.right + this.progressPadding)));
                    n4 = progressBar.getHeight() - (insets.top + this.progressPadding + insets.bottom + this.progressPadding);
                    if (!SynthLookAndFeel.isLeftToRight(progressBar)) {
                        n = progressBar.getWidth() - insets.right - n3 - this.progressPadding - this.glowWidth;
                    }
                }
                else {
                    n = insets.left + this.progressPadding;
                    n3 = progressBar.getWidth() - (insets.left + this.progressPadding + insets.right + this.progressPadding);
                    n4 = (int)(percentComplete * (progressBar.getHeight() - (insets.top + this.progressPadding + insets.bottom + this.progressPadding)));
                    n2 = progressBar.getHeight() - insets.bottom - n4 - this.progressPadding;
                    if (SynthLookAndFeel.isLeftToRight(progressBar)) {
                        n2 -= this.glowWidth;
                    }
                }
            }
        }
        else {
            this.boxRect = this.getBox(this.boxRect);
            n = this.boxRect.x + this.progressPadding;
            n2 = this.boxRect.y + this.progressPadding;
            n3 = this.boxRect.width - this.progressPadding - this.progressPadding;
            n4 = this.boxRect.height - this.progressPadding - this.progressPadding;
        }
        if (this.tileWhenIndeterminate && progressBar.isIndeterminate()) {
            final int n5 = (int)(this.getAnimationIndex() / (double)this.getFrameCount() * this.tileWidth);
            final Shape clip = graphics.getClip();
            graphics.clipRect(n, n2, n3, n4);
            if (progressBar.getOrientation() == 0) {
                for (int i = n - this.tileWidth + n5; i <= n3; i += this.tileWidth) {
                    synthContext.getPainter().paintProgressBarForeground(synthContext, graphics, i, n2, this.tileWidth, n4, progressBar.getOrientation());
                }
            }
            else {
                for (int j = n2 - n5; j < n4 + this.tileWidth; j += this.tileWidth) {
                    synthContext.getPainter().paintProgressBarForeground(synthContext, graphics, n, j, n3, this.tileWidth, progressBar.getOrientation());
                }
            }
            graphics.setClip(clip);
        }
        else if (this.minBarSize == null || (n3 >= this.minBarSize.width && n4 >= this.minBarSize.height)) {
            synthContext.getPainter().paintProgressBarForeground(synthContext, graphics, n, n2, n3, n4, progressBar.getOrientation());
        }
        if (progressBar.isStringPainted()) {
            this.paintText(synthContext, graphics, progressBar.getString());
        }
    }
    
    protected void paintText(final SynthContext synthContext, final Graphics graphics, final String s) {
        if (this.progressBar.isStringPainted()) {
            final SynthStyle style = synthContext.getStyle();
            final Font font = style.getFont(synthContext);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.progressBar, graphics, font);
            final int computeStringWidth = style.getGraphicsUtils(synthContext).computeStringWidth(synthContext, font, fontMetrics, s);
            final Rectangle bounds = this.progressBar.getBounds();
            if (this.rotateText && this.progressBar.getOrientation() == 1) {
                final Graphics2D graphics2D = (Graphics2D)graphics;
                AffineTransform affineTransform;
                Point point;
                if (this.progressBar.getComponentOrientation().isLeftToRight()) {
                    affineTransform = AffineTransform.getRotateInstance(-1.5707963267948966);
                    point = new Point((bounds.width + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2, (bounds.height + computeStringWidth) / 2);
                }
                else {
                    affineTransform = AffineTransform.getRotateInstance(1.5707963267948966);
                    point = new Point((bounds.width - fontMetrics.getAscent() + fontMetrics.getDescent()) / 2, (bounds.height - computeStringWidth) / 2);
                }
                if (point.x < 0) {
                    return;
                }
                graphics2D.setFont(font.deriveFont(affineTransform));
                graphics2D.setColor(style.getColor(synthContext, ColorType.TEXT_FOREGROUND));
                style.getGraphicsUtils(synthContext).paintText(synthContext, graphics, s, point.x, point.y, -1);
            }
            else {
                final Rectangle rectangle = new Rectangle(bounds.width / 2 - computeStringWidth / 2, (bounds.height - (fontMetrics.getAscent() + fontMetrics.getDescent())) / 2, 0, 0);
                if (rectangle.y < 0) {
                    return;
                }
                graphics.setColor(style.getColor(synthContext, ColorType.TEXT_FOREGROUND));
                graphics.setFont(font);
                style.getGraphicsUtils(synthContext).paintText(synthContext, graphics, s, rectangle.x, rectangle.y, -1);
            }
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintProgressBarBorder(synthContext, graphics, n, n2, n3, n4, this.progressBar.getOrientation());
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent) || "indeterminate".equals(propertyChangeEvent.getPropertyName())) {
            this.updateStyle((JProgressBar)propertyChangeEvent.getSource());
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Insets insets = this.progressBar.getInsets();
        final FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
        final String string = this.progressBar.getString();
        final int n = fontMetrics.getHeight() + fontMetrics.getDescent();
        Dimension dimension;
        if (this.progressBar.getOrientation() == 0) {
            dimension = new Dimension(this.getPreferredInnerHorizontal());
            if (this.progressBar.isStringPainted()) {
                if (n > dimension.height) {
                    dimension.height = n;
                }
                final int stringWidth = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, string);
                if (stringWidth > dimension.width) {
                    dimension.width = stringWidth;
                }
            }
        }
        else {
            dimension = new Dimension(this.getPreferredInnerVertical());
            if (this.progressBar.isStringPainted()) {
                if (n > dimension.width) {
                    dimension.width = n;
                }
                final int stringWidth2 = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, string);
                if (stringWidth2 > dimension.height) {
                    dimension.height = stringWidth2;
                }
            }
        }
        final String s = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
        if (s != null) {
            if ("large".equals(s)) {
                final Dimension dimension2 = dimension;
                dimension2.width *= (int)1.15f;
                final Dimension dimension3 = dimension;
                dimension3.height *= (int)1.15f;
            }
            else if ("small".equals(s)) {
                final Dimension dimension4 = dimension;
                dimension4.width *= (int)0.9f;
                final Dimension dimension5 = dimension;
                dimension5.height *= (int)0.9f;
            }
            else if ("mini".equals(s)) {
                final Dimension dimension6 = dimension;
                dimension6.width *= (int)0.784f;
                final Dimension dimension7 = dimension;
                dimension7.height *= (int)0.784f;
            }
        }
        final Dimension dimension8 = dimension;
        dimension8.width += insets.left + insets.right;
        final Dimension dimension9 = dimension;
        dimension9.height += insets.top + insets.bottom;
        return dimension;
    }
}
