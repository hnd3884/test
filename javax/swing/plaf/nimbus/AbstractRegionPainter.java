package javax.swing.plaf.nimbus;

import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.GraphicsConfiguration;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import sun.reflect.misc.MethodUtil;
import javax.swing.JTable;
import javax.swing.JList;
import java.awt.RadialGradientPaint;
import java.awt.LinearGradientPaint;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.print.PrinterGraphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.Painter;

public abstract class AbstractRegionPainter implements Painter<JComponent>
{
    private PaintContext ctx;
    private float f;
    private float leftWidth;
    private float topHeight;
    private float centerWidth;
    private float centerHeight;
    private float rightWidth;
    private float bottomHeight;
    private float leftScale;
    private float topScale;
    private float centerHScale;
    private float centerVScale;
    private float rightScale;
    private float bottomScale;
    
    protected AbstractRegionPainter() {
    }
    
    @Override
    public final void paint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            return;
        }
        final Object[] extendedCacheKeys = this.getExtendedCacheKeys(component);
        this.ctx = this.getPaintContext();
        final PaintContext.CacheMode cacheMode = (this.ctx == null) ? PaintContext.CacheMode.NO_CACHING : this.ctx.cacheMode;
        if (cacheMode == PaintContext.CacheMode.NO_CACHING || !ImageCache.getInstance().isImageCachable(n, n2) || graphics2D instanceof PrinterGraphics) {
            this.paint0(graphics2D, component, n, n2, extendedCacheKeys);
        }
        else if (cacheMode == PaintContext.CacheMode.FIXED_SIZES) {
            this.paintWithFixedSizeCaching(graphics2D, component, n, n2, extendedCacheKeys);
        }
        else {
            this.paintWith9SquareCaching(graphics2D, this.ctx, component, n, n2, extendedCacheKeys);
        }
    }
    
    protected Object[] getExtendedCacheKeys(final JComponent component) {
        return null;
    }
    
    protected abstract PaintContext getPaintContext();
    
    protected void configureGraphics(final Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    protected abstract void doPaint(final Graphics2D p0, final JComponent p1, final int p2, final int p3, final Object[] p4);
    
    protected final float decodeX(final float n) {
        if (n >= 0.0f && n <= 1.0f) {
            return n * this.leftWidth;
        }
        if (n > 1.0f && n < 2.0f) {
            return (n - 1.0f) * this.centerWidth + this.leftWidth;
        }
        if (n >= 2.0f && n <= 3.0f) {
            return (n - 2.0f) * this.rightWidth + this.leftWidth + this.centerWidth;
        }
        throw new IllegalArgumentException("Invalid x");
    }
    
    protected final float decodeY(final float n) {
        if (n >= 0.0f && n <= 1.0f) {
            return n * this.topHeight;
        }
        if (n > 1.0f && n < 2.0f) {
            return (n - 1.0f) * this.centerHeight + this.topHeight;
        }
        if (n >= 2.0f && n <= 3.0f) {
            return (n - 2.0f) * this.bottomHeight + this.topHeight + this.centerHeight;
        }
        throw new IllegalArgumentException("Invalid y");
    }
    
    protected final float decodeAnchorX(final float n, final float n2) {
        if (n >= 0.0f && n <= 1.0f) {
            return this.decodeX(n) + n2 * this.leftScale;
        }
        if (n > 1.0f && n < 2.0f) {
            return this.decodeX(n) + n2 * this.centerHScale;
        }
        if (n >= 2.0f && n <= 3.0f) {
            return this.decodeX(n) + n2 * this.rightScale;
        }
        throw new IllegalArgumentException("Invalid x");
    }
    
    protected final float decodeAnchorY(final float n, final float n2) {
        if (n >= 0.0f && n <= 1.0f) {
            return this.decodeY(n) + n2 * this.topScale;
        }
        if (n > 1.0f && n < 2.0f) {
            return this.decodeY(n) + n2 * this.centerVScale;
        }
        if (n >= 2.0f && n <= 3.0f) {
            return this.decodeY(n) + n2 * this.bottomScale;
        }
        throw new IllegalArgumentException("Invalid y");
    }
    
    protected final Color decodeColor(final String s, final float n, final float n2, final float n3, final int n4) {
        if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel) {
            return ((NimbusLookAndFeel)UIManager.getLookAndFeel()).getDerivedColor(s, n, n2, n3, n4, true);
        }
        return Color.getHSBColor(n, n2, n3);
    }
    
    protected final Color decodeColor(final Color color, final Color color2, final float n) {
        return new Color(NimbusLookAndFeel.deriveARGB(color, color2, n));
    }
    
    protected final LinearGradientPaint decodeGradient(final float n, final float n2, final float n3, float n4, final float[] array, final Color[] array2) {
        if (n == n3 && n2 == n4) {
            n4 += 1.0E-5f;
        }
        return new LinearGradientPaint(n, n2, n3, n4, array, array2);
    }
    
    protected final RadialGradientPaint decodeRadialGradient(final float n, final float n2, float n3, final float[] array, final Color[] array2) {
        if (n3 == 0.0f) {
            n3 = 1.0E-5f;
        }
        return new RadialGradientPaint(n, n2, n3, array, array2);
    }
    
    protected final Color getComponentColor(final JComponent component, final String s, final Color color, final float n, final float n2, final int n3) {
        Color color2 = null;
        if (component != null) {
            if ("background".equals(s)) {
                color2 = component.getBackground();
            }
            else if ("foreground".equals(s)) {
                color2 = component.getForeground();
            }
            else if (component instanceof JList && "selectionForeground".equals(s)) {
                color2 = ((JList)component).getSelectionForeground();
            }
            else if (component instanceof JList && "selectionBackground".equals(s)) {
                color2 = ((JList)component).getSelectionBackground();
            }
            else if (component instanceof JTable && "selectionForeground".equals(s)) {
                color2 = ((JTable)component).getSelectionForeground();
            }
            else if (component instanceof JTable && "selectionBackground".equals(s)) {
                color2 = ((JTable)component).getSelectionBackground();
            }
            else {
                final String string = "get" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
                try {
                    color2 = (Color)MethodUtil.invoke(MethodUtil.getMethod(component.getClass(), string, null), component, null);
                }
                catch (final Exception ex) {}
                if (color2 == null) {
                    final Object clientProperty = component.getClientProperty(s);
                    if (clientProperty instanceof Color) {
                        color2 = (Color)clientProperty;
                    }
                }
            }
        }
        if (color2 == null || color2 instanceof UIResource) {
            return color;
        }
        if (n != 0.0f || n2 != 0.0f || n3 != 0) {
            final float[] rgBtoHSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
            rgBtoHSB[1] = this.clamp(rgBtoHSB[1] + n);
            rgBtoHSB[2] = this.clamp(rgBtoHSB[2] + n2);
            return new Color((Color.HSBtoRGB(rgBtoHSB[0], rgBtoHSB[1], rgBtoHSB[2]) & 0xFFFFFF) | this.clamp(color2.getAlpha() + n3) << 24);
        }
        return color2;
    }
    
    private void prepare(final float n, final float n2) {
        if (this.ctx == null || this.ctx.canvasSize == null) {
            this.f = 1.0f;
            final float leftWidth = 0.0f;
            this.rightWidth = leftWidth;
            this.centerWidth = leftWidth;
            this.leftWidth = leftWidth;
            final float topHeight = 0.0f;
            this.bottomHeight = topHeight;
            this.centerHeight = topHeight;
            this.topHeight = topHeight;
            final float leftScale = 0.0f;
            this.rightScale = leftScale;
            this.centerHScale = leftScale;
            this.leftScale = leftScale;
            final float topScale = 0.0f;
            this.bottomScale = topScale;
            this.centerVScale = topScale;
            this.topScale = topScale;
            return;
        }
        final Number n3 = (Number)UIManager.get("scale");
        this.f = ((n3 == null) ? 1.0f : n3.floatValue());
        if (this.ctx.inverted) {
            this.centerWidth = (this.ctx.b - this.ctx.a) * this.f;
            final float n4 = n - this.centerWidth;
            this.leftWidth = n4 * this.ctx.aPercent;
            this.rightWidth = n4 * this.ctx.bPercent;
            this.centerHeight = (this.ctx.d - this.ctx.c) * this.f;
            final float n5 = n2 - this.centerHeight;
            this.topHeight = n5 * this.ctx.cPercent;
            this.bottomHeight = n5 * this.ctx.dPercent;
        }
        else {
            this.leftWidth = this.ctx.a * this.f;
            this.rightWidth = (float)(this.ctx.canvasSize.getWidth() - this.ctx.b) * this.f;
            this.centerWidth = n - this.leftWidth - this.rightWidth;
            this.topHeight = this.ctx.c * this.f;
            this.bottomHeight = (float)(this.ctx.canvasSize.getHeight() - this.ctx.d) * this.f;
            this.centerHeight = n2 - this.topHeight - this.bottomHeight;
        }
        this.leftScale = ((this.ctx.a == 0.0f) ? 0.0f : (this.leftWidth / this.ctx.a));
        this.centerHScale = ((this.ctx.b - this.ctx.a == 0.0f) ? 0.0f : (this.centerWidth / (this.ctx.b - this.ctx.a)));
        this.rightScale = ((this.ctx.canvasSize.width - this.ctx.b == 0.0f) ? 0.0f : (this.rightWidth / (this.ctx.canvasSize.width - this.ctx.b)));
        this.topScale = ((this.ctx.c == 0.0f) ? 0.0f : (this.topHeight / this.ctx.c));
        this.centerVScale = ((this.ctx.d - this.ctx.c == 0.0f) ? 0.0f : (this.centerHeight / (this.ctx.d - this.ctx.c)));
        this.bottomScale = ((this.ctx.canvasSize.height - this.ctx.d == 0.0f) ? 0.0f : (this.bottomHeight / (this.ctx.canvasSize.height - this.ctx.d)));
    }
    
    private void paintWith9SquareCaching(final Graphics2D graphics2D, final PaintContext paintContext, final JComponent component, final int n, final int n2, final Object[] array) {
        final Dimension access$100 = paintContext.canvasSize;
        final Insets access$101 = paintContext.stretchingInsets;
        if (n <= access$100.width * paintContext.maxHorizontalScaleFactor && n2 <= access$100.height * paintContext.maxVerticalScaleFactor) {
            final VolatileImage image = this.getImage(graphics2D.getDeviceConfiguration(), component, access$100.width, access$100.height, array);
            if (image != null) {
                Insets insets;
                if (paintContext.inverted) {
                    final int n3 = (n - (access$100.width - (access$101.left + access$101.right))) / 2;
                    final int n4 = (n2 - (access$100.height - (access$101.top + access$101.bottom))) / 2;
                    insets = new Insets(n4, n3, n4, n3);
                }
                else {
                    insets = access$101;
                }
                final Object renderingHint = graphics2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                ImageScalingHelper.paint(graphics2D, 0, 0, n, n2, image, access$101, insets, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, (renderingHint != null) ? renderingHint : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
            else {
                this.paint0(graphics2D, component, n, n2, array);
            }
        }
        else {
            this.paint0(graphics2D, component, n, n2, array);
        }
    }
    
    private void paintWithFixedSizeCaching(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] array) {
        final VolatileImage image = this.getImage(graphics2D.getDeviceConfiguration(), component, n, n2, array);
        if (image != null) {
            graphics2D.drawImage(image, 0, 0, null);
        }
        else {
            this.paint0(graphics2D, component, n, n2, array);
        }
    }
    
    private VolatileImage getImage(final GraphicsConfiguration graphicsConfiguration, final JComponent component, final int n, final int n2, final Object[] array) {
        final ImageCache instance = ImageCache.getInstance();
        VolatileImage compatibleVolatileImage = (VolatileImage)instance.getImage(graphicsConfiguration, n, n2, this, array);
        int n3 = 0;
        do {
            int validate = 2;
            if (compatibleVolatileImage != null) {
                validate = compatibleVolatileImage.validate(graphicsConfiguration);
            }
            if (validate == 2 || validate == 1) {
                if (compatibleVolatileImage == null || compatibleVolatileImage.getWidth() != n || compatibleVolatileImage.getHeight() != n2 || validate == 2) {
                    if (compatibleVolatileImage != null) {
                        compatibleVolatileImage.flush();
                    }
                    compatibleVolatileImage = graphicsConfiguration.createCompatibleVolatileImage(n, n2, 3);
                    instance.setImage(compatibleVolatileImage, graphicsConfiguration, n, n2, this, array);
                }
                final Graphics2D graphics = compatibleVolatileImage.createGraphics();
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(0, 0, n, n2);
                graphics.setComposite(AlphaComposite.SrcOver);
                this.configureGraphics(graphics);
                this.paint0(graphics, component, n, n2, array);
                graphics.dispose();
            }
        } while (compatibleVolatileImage.contentsLost() && n3++ < 3);
        if (n3 == 3) {
            return null;
        }
        return compatibleVolatileImage;
    }
    
    private void paint0(Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] array) {
        this.prepare((float)n, (float)n2);
        graphics2D = (Graphics2D)graphics2D.create();
        this.configureGraphics(graphics2D);
        this.doPaint(graphics2D, component, n, n2, array);
        graphics2D.dispose();
    }
    
    private float clamp(float n) {
        if (n < 0.0f) {
            n = 0.0f;
        }
        else if (n > 1.0f) {
            n = 1.0f;
        }
        return n;
    }
    
    private int clamp(int n) {
        if (n < 0) {
            n = 0;
        }
        else if (n > 255) {
            n = 255;
        }
        return n;
    }
    
    protected static class PaintContext
    {
        private static Insets EMPTY_INSETS;
        private Insets stretchingInsets;
        private Dimension canvasSize;
        private boolean inverted;
        private CacheMode cacheMode;
        private double maxHorizontalScaleFactor;
        private double maxVerticalScaleFactor;
        private float a;
        private float b;
        private float c;
        private float d;
        private float aPercent;
        private float bPercent;
        private float cPercent;
        private float dPercent;
        
        public PaintContext(final Insets insets, final Dimension dimension, final boolean b) {
            this(insets, dimension, b, null, 1.0, 1.0);
        }
        
        public PaintContext(final Insets insets, final Dimension dimension, final boolean b, final CacheMode cacheMode, final double maxHorizontalScaleFactor, final double maxVerticalScaleFactor) {
            if (maxHorizontalScaleFactor < 1.0 || maxHorizontalScaleFactor < 1.0) {
                throw new IllegalArgumentException("Both maxH and maxV must be >= 1");
            }
            this.stretchingInsets = ((insets == null) ? PaintContext.EMPTY_INSETS : insets);
            this.canvasSize = dimension;
            this.inverted = b;
            this.cacheMode = ((cacheMode == null) ? CacheMode.NO_CACHING : cacheMode);
            this.maxHorizontalScaleFactor = maxHorizontalScaleFactor;
            this.maxVerticalScaleFactor = maxVerticalScaleFactor;
            if (dimension != null) {
                this.a = (float)this.stretchingInsets.left;
                this.b = (float)(dimension.width - this.stretchingInsets.right);
                this.c = (float)this.stretchingInsets.top;
                this.d = (float)(dimension.height - this.stretchingInsets.bottom);
                this.canvasSize = dimension;
                if (this.inverted = b) {
                    final float n = dimension.width - (this.b - this.a);
                    this.aPercent = ((n > 0.0f) ? (this.a / n) : 0.0f);
                    this.bPercent = ((n > 0.0f) ? (this.b / n) : 0.0f);
                    final float n2 = dimension.height - (this.d - this.c);
                    this.cPercent = ((n2 > 0.0f) ? (this.c / n2) : 0.0f);
                    this.dPercent = ((n2 > 0.0f) ? (this.d / n2) : 0.0f);
                }
            }
        }
        
        static {
            PaintContext.EMPTY_INSETS = new Insets(0, 0, 0, 0);
        }
        
        protected enum CacheMode
        {
            NO_CACHING, 
            FIXED_SIZES, 
            NINE_SQUARE_SCALE;
        }
    }
}
