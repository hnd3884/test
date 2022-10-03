package javax.swing.plaf.nimbus;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import java.awt.Insets;
import javax.swing.border.Border;

class LoweredBorder extends AbstractRegionPainter implements Border
{
    private static final int IMG_SIZE = 30;
    private static final int RADIUS = 13;
    private static final Insets INSETS;
    private static final PaintContext PAINT_CONTEXT;
    
    @Override
    protected Object[] getExtendedCacheKeys(final JComponent component) {
        return (Object[])((component != null) ? new Object[] { component.getBackground() } : null);
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] array) {
        final Color color = (component == null) ? Color.BLACK : component.getBackground();
        final BufferedImage bufferedImage = new BufferedImage(30, 30, 2);
        final BufferedImage bufferedImage2 = new BufferedImage(30, 30, 2);
        final Graphics2D graphics2D2 = (Graphics2D)bufferedImage.getGraphics();
        graphics2D2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D2.setColor(color);
        graphics2D2.fillRoundRect(2, 0, 26, 26, 13, 13);
        graphics2D2.dispose();
        final InnerShadowEffect innerShadowEffect = new InnerShadowEffect();
        innerShadowEffect.setDistance(1);
        innerShadowEffect.setSize(3);
        innerShadowEffect.setColor(this.getLighter(color, 2.1f));
        innerShadowEffect.setAngle(90);
        innerShadowEffect.applyEffect(bufferedImage, bufferedImage2, 30, 30);
        final Graphics2D graphics2D3 = (Graphics2D)bufferedImage2.getGraphics();
        graphics2D3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D3.setClip(0, 28, 30, 1);
        graphics2D3.setColor(this.getLighter(color, 0.9f));
        graphics2D3.drawRoundRect(2, 1, 25, 25, 13, 13);
        graphics2D3.dispose();
        if (n != 30 || n2 != 30) {
            ImageScalingHelper.paint(graphics2D, 0, 0, n, n2, bufferedImage2, LoweredBorder.INSETS, LoweredBorder.INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
        }
        else {
            graphics2D.drawImage(bufferedImage2, 0, 0, component);
        }
    }
    
    @Override
    protected PaintContext getPaintContext() {
        return LoweredBorder.PAINT_CONTEXT;
    }
    
    @Override
    public Insets getBorderInsets(final Component component) {
        return (Insets)LoweredBorder.INSETS.clone();
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final JComponent component2 = (component instanceof JComponent) ? ((JComponent)component) : null;
        if (graphics instanceof Graphics2D) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            graphics2D.translate(n, n2);
            this.paint(graphics2D, component2, n3, n4);
            graphics2D.translate(-n, -n2);
        }
        else {
            final BufferedImage bufferedImage = new BufferedImage(30, 30, 2);
            final Graphics2D graphics2D2 = (Graphics2D)bufferedImage.getGraphics();
            this.paint(graphics2D2, component2, n3, n4);
            graphics2D2.dispose();
            ImageScalingHelper.paint(graphics, n, n2, n3, n4, bufferedImage, LoweredBorder.INSETS, LoweredBorder.INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
        }
    }
    
    private Color getLighter(final Color color, final float n) {
        return new Color(Math.min((int)(color.getRed() / n), 255), Math.min((int)(color.getGreen() / n), 255), Math.min((int)(color.getBlue() / n), 255));
    }
    
    static {
        INSETS = new Insets(10, 10, 10, 10);
        PAINT_CONTEXT = new PaintContext(LoweredBorder.INSETS, new Dimension(30, 30), false, PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.147483647E9, 2.147483647E9);
    }
}
