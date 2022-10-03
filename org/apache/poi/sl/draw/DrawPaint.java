package org.apache.poi.sl.draw;

import java.util.Objects;
import org.apache.poi.util.POILogFactory;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.function.BiFunction;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint;
import org.apache.poi.sl.usermodel.Insets2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import org.apache.poi.sl.draw.geom.ArcToCommand;
import java.awt.LinearGradientPaint;
import java.util.function.Function;
import java.awt.image.WritableRaster;
import java.util.List;
import java.awt.image.IndexColorModel;
import java.awt.geom.Dimension2D;
import java.io.InputStream;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.sl.usermodel.AbstractColorStyle;
import java.awt.Paint;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.awt.Color;
import org.apache.poi.util.POILogger;

public class DrawPaint
{
    private static final POILogger LOG;
    private static final Color TRANSPARENT;
    protected PlaceableShape<?, ?> shape;
    
    public DrawPaint(final PlaceableShape<?, ?> shape) {
        this.shape = shape;
    }
    
    public static PaintStyle.SolidPaint createSolidPaint(final Color color) {
        return (color == null) ? null : new SimpleSolidPaint(color);
    }
    
    public static PaintStyle.SolidPaint createSolidPaint(final ColorStyle color) {
        return (color == null) ? null : new SimpleSolidPaint(color);
    }
    
    public Paint getPaint(final Graphics2D graphics, final PaintStyle paint) {
        return this.getPaint(graphics, paint, PaintStyle.PaintModifier.NORM);
    }
    
    public Paint getPaint(final Graphics2D graphics, final PaintStyle paint, final PaintStyle.PaintModifier modifier) {
        if (modifier == PaintStyle.PaintModifier.NONE) {
            return DrawPaint.TRANSPARENT;
        }
        if (paint instanceof PaintStyle.SolidPaint) {
            return this.getSolidPaint((PaintStyle.SolidPaint)paint, graphics, modifier);
        }
        if (paint instanceof PaintStyle.GradientPaint) {
            return this.getGradientPaint((PaintStyle.GradientPaint)paint, graphics);
        }
        if (paint instanceof PaintStyle.TexturePaint) {
            return this.getTexturePaint((PaintStyle.TexturePaint)paint, graphics);
        }
        return DrawPaint.TRANSPARENT;
    }
    
    protected Paint getSolidPaint(final PaintStyle.SolidPaint fill, final Graphics2D graphics, final PaintStyle.PaintModifier modifier) {
        final ColorStyle orig = fill.getSolidColor();
        final ColorStyle cs = new AbstractColorStyle() {
            @Override
            public Color getColor() {
                return orig.getColor();
            }
            
            @Override
            public int getAlpha() {
                return orig.getAlpha();
            }
            
            @Override
            public int getHueOff() {
                return orig.getHueOff();
            }
            
            @Override
            public int getHueMod() {
                return orig.getHueMod();
            }
            
            @Override
            public int getSatOff() {
                return orig.getSatOff();
            }
            
            @Override
            public int getSatMod() {
                return orig.getSatMod();
            }
            
            @Override
            public int getLumOff() {
                return orig.getLumOff();
            }
            
            @Override
            public int getLumMod() {
                return orig.getLumMod();
            }
            
            @Override
            public int getShade() {
                return this.scale(orig.getShade(), PaintStyle.PaintModifier.DARKEN_LESS, PaintStyle.PaintModifier.DARKEN);
            }
            
            @Override
            public int getTint() {
                return this.scale(orig.getTint(), PaintStyle.PaintModifier.LIGHTEN_LESS, PaintStyle.PaintModifier.LIGHTEN);
            }
            
            private int scale(final int value, final PaintStyle.PaintModifier lessModifier, final PaintStyle.PaintModifier moreModifier) {
                if (value == -1) {
                    return -1;
                }
                final int delta = (modifier == lessModifier) ? 20000 : ((modifier == moreModifier) ? 40000 : 0);
                return Math.min(100000, Math.max(0, value) + delta);
            }
        };
        return applyColorTransform(cs);
    }
    
    protected Paint getGradientPaint(final PaintStyle.GradientPaint fill, final Graphics2D graphics) {
        switch (fill.getGradientType()) {
            case linear: {
                return this.createLinearGradientPaint(fill, graphics);
            }
            case rectangular:
            case circular: {
                return this.createRadialGradientPaint(fill, graphics);
            }
            case shape: {
                return this.createPathGradientPaint(fill, graphics);
            }
            default: {
                throw new UnsupportedOperationException("gradient fill of type " + fill + " not supported.");
            }
        }
    }
    
    protected Paint getTexturePaint(final PaintStyle.TexturePaint fill, final Graphics2D graphics) {
        assert graphics != null;
        final String contentType = fill.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            return DrawPaint.TRANSPARENT;
        }
        final ImageRenderer renderer = DrawPictureShape.getImageRenderer(graphics, contentType);
        final Rectangle2D textAnchor = this.shape.getAnchor();
        try (final InputStream is = fill.getImageData()) {
            if (is == null) {
                return DrawPaint.TRANSPARENT;
            }
            renderer.loadImage(is, contentType);
            final int alpha = fill.getAlpha();
            if (0 <= alpha && alpha < 100000) {
                renderer.setAlpha(alpha / 100000.0f);
            }
            Dimension2D imgDim = renderer.getDimension();
            if ("image/x-wmf".contains(contentType)) {
                imgDim = new Dimension2DDouble(textAnchor.getWidth(), textAnchor.getHeight());
            }
            BufferedImage image = renderer.getImage(imgDim);
            if (image == null) {
                DrawPaint.LOG.log(7, "Can't load image data");
                return DrawPaint.TRANSPARENT;
            }
            double flipX = 1.0;
            double flipY = 1.0;
            final PaintStyle.FlipMode flip = fill.getFlipMode();
            if (flip != null && flip != PaintStyle.FlipMode.NONE) {
                final int width = image.getWidth();
                final int height = image.getHeight();
                switch (flip) {
                    case X: {
                        flipX = 2.0;
                        break;
                    }
                    case Y: {
                        flipY = 2.0;
                        break;
                    }
                    case XY: {
                        flipX = 2.0;
                        flipY = 2.0;
                        break;
                    }
                }
                final BufferedImage img = new BufferedImage((int)(width * flipX), (int)(height * flipY), 2);
                final Graphics2D g = img.createGraphics();
                g.drawImage(image, 0, 0, null);
                switch (flip) {
                    case X: {
                        g.drawImage(image, 2 * width, 0, -width, height, null);
                        break;
                    }
                    case Y: {
                        g.drawImage(image, 0, 2 * height, width, -height, null);
                        break;
                    }
                    case XY: {
                        g.drawImage(image, 2 * width, 0, -width, height, null);
                        g.drawImage(image, 0, 2 * height, width, -height, null);
                        g.drawImage(image, 2 * width, 2 * height, -width, -height, null);
                        break;
                    }
                }
                g.dispose();
                image = img;
            }
            image = colorizePattern(fill, image);
            final Shape s = (Shape)graphics.getRenderingHint(Drawable.GRADIENT_SHAPE);
            return new DrawTexturePaint(image, s, fill, flipX, flipY, renderer instanceof BitmapImageRenderer);
        }
        catch (final IOException e) {
            DrawPaint.LOG.log(7, "Can't load image data - using transparent color", e);
            return DrawPaint.TRANSPARENT;
        }
    }
    
    private static BufferedImage colorizePattern(final PaintStyle.TexturePaint fill, final BufferedImage pattern) {
        final List<ColorStyle> duoTone = fill.getDuoTone();
        if (duoTone == null || duoTone.size() != 2) {
            return pattern;
        }
        final int redBits = pattern.getSampleModel().getSampleSize(0);
        final int blendBits = Math.max(Math.min(redBits, 8), 1);
        final int blendShades = 1 << blendBits;
        final double blendRatio = blendShades / (double)(1 << Math.max(redBits, 1));
        final int[] gradSample = linearBlendedColors(duoTone, blendShades);
        final IndexColorModel icm = new IndexColorModel(blendBits, blendShades, gradSample, 0, true, -1, 0);
        final BufferedImage patIdx = new BufferedImage(pattern.getWidth(), pattern.getHeight(), 13, icm);
        final WritableRaster rasterRGBA = pattern.getRaster();
        final WritableRaster rasterIdx = patIdx.getRaster();
        final int[] redSample = new int[pattern.getWidth()];
        for (int y = 0; y < pattern.getHeight(); ++y) {
            rasterRGBA.getSamples(0, y, redSample.length, 1, 0, redSample);
            scaleShades(redSample, blendRatio);
            rasterIdx.setSamples(0, y, redSample.length, 1, 0, redSample);
        }
        return patIdx;
    }
    
    private static void scaleShades(final int[] samples, final double ratio) {
        if (ratio != 1.0) {
            for (int x = 0; x < samples.length; ++x) {
                samples[x] = (int)Math.rint(samples[x] * ratio);
            }
        }
    }
    
    private static int[] linearBlendedColors(final List<ColorStyle> duoTone, final int blendShades) {
        final Color[] colors = duoTone.stream().map((Function<? super Object, ?>)DrawPaint::applyColorTransform).toArray(Color[]::new);
        final float[] fractions = { 0.0f, 1.0f };
        final BufferedImage gradBI = new BufferedImage(blendShades, 1, 2);
        final Graphics2D gradG = gradBI.createGraphics();
        gradG.setPaint(new LinearGradientPaint(0.0f, 0.0f, (float)blendShades, 0.0f, fractions, colors));
        gradG.fillRect(0, 0, blendShades, 1);
        gradG.dispose();
        return gradBI.getRGB(0, 0, blendShades, 1, null, 0, blendShades);
    }
    
    public static Color applyColorTransform(final ColorStyle color) {
        if (color == null || color.getColor() == null) {
            return DrawPaint.TRANSPARENT;
        }
        Color result = color.getColor();
        final double alpha = getAlpha(result, color);
        final double[] hsl = RGB2HSL(result);
        applyHslModOff(hsl, 0, color.getHueMod(), color.getHueOff());
        applyHslModOff(hsl, 1, color.getSatMod(), color.getSatOff());
        applyHslModOff(hsl, 2, color.getLumMod(), color.getLumOff());
        applyShade(hsl, color);
        applyTint(hsl, color);
        result = HSL2RGB(hsl[0], hsl[1], hsl[2], alpha);
        return result;
    }
    
    private static double getAlpha(final Color c, final ColorStyle fc) {
        double alpha = c.getAlpha() / 255.0;
        final int fcAlpha = fc.getAlpha();
        if (fcAlpha != -1) {
            alpha *= fcAlpha / 100000.0;
        }
        return Math.min(1.0, Math.max(0.0, alpha));
    }
    
    private static void applyHslModOff(final double[] hsl, final int hslPart, final int mod, final int off) {
        if (mod != -1) {
            hsl[hslPart] *= mod / 100000.0;
        }
        if (off != -1) {
            hsl[hslPart] += off / 1000.0;
        }
    }
    
    private static void applyShade(final double[] hsl, final ColorStyle fc) {
        final int shade = fc.getShade();
        if (shade == -1) {
            return;
        }
        final double shadePct = shade / 100000.0;
        final int n = 2;
        hsl[n] *= shadePct;
    }
    
    private static void applyTint(final double[] hsl, final ColorStyle fc) {
        final int tint = fc.getTint();
        if (tint == -1 || tint == 0) {
            return;
        }
        final double tintPct = tint / 100000.0;
        if (tintPct < 0.0) {
            final int n = 2;
            hsl[n] *= 1.0 + tintPct;
        }
        else {
            hsl[2] = hsl[2] * (1.0 - tintPct) + (100.0 - 100.0 * (1.0 - tintPct));
        }
    }
    
    protected Paint createLinearGradientPaint(final PaintStyle.GradientPaint fill, final Graphics2D graphics) {
        double angle = fill.getGradientAngle();
        if (!fill.isRotatedWithShape()) {
            angle -= this.shape.getRotation();
        }
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, this.shape);
        if (anchor == null) {
            return DrawPaint.TRANSPARENT;
        }
        angle = ArcToCommand.convertOoxml2AwtAngle(-angle, anchor.getWidth(), anchor.getHeight());
        final AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(angle), anchor.getCenterX(), anchor.getCenterY());
        final double diagonal = Math.sqrt(Math.pow(anchor.getWidth(), 2.0) + Math.pow(anchor.getHeight(), 2.0));
        final Point2D p1 = at.transform(new Point2D.Double(anchor.getCenterX() - diagonal / 2.0, anchor.getCenterY()), null);
        final Point2D p2 = at.transform(new Point2D.Double(anchor.getMaxX(), anchor.getCenterY()), null);
        return (p1.equals(p2) || fill.getGradientFractions().length < 2) ? null : this.safeFractions((f, c) -> new LinearGradientPaint(p1, p2, f, c), fill);
    }
    
    protected Paint createRadialGradientPaint(final PaintStyle.GradientPaint fill, final Graphics2D graphics) {
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, this.shape);
        if (anchor == null) {
            return DrawPaint.TRANSPARENT;
        }
        Insets2D insets = fill.getFillToInsets();
        if (insets == null) {
            insets = new Insets2D(0.0, 0.0, 0.0, 0.0);
        }
        final Point2D pCenter = new Point2D.Double(anchor.getCenterX(), anchor.getCenterY());
        final Point2D pFocus = new Point2D.Double(getCenterVal(anchor.getMinX(), anchor.getMaxX(), insets.left, insets.right), getCenterVal(anchor.getMinY(), anchor.getMaxY(), insets.top, insets.bottom));
        final float radius = (float)Math.max(anchor.getWidth(), anchor.getHeight());
        final AffineTransform at = new AffineTransform();
        at.translate(pFocus.getX(), pFocus.getY());
        at.scale(getScale(anchor.getMinX(), anchor.getMaxX(), insets.left, insets.right), getScale(anchor.getMinY(), anchor.getMaxY(), insets.top, insets.bottom));
        at.translate(-pFocus.getX(), -pFocus.getY());
        return this.safeFractions((f, c) -> new RadialGradientPaint(pCenter, radius, pFocus, f, c, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, at), fill);
    }
    
    private static double getScale(final double absMin, final double absMax, final double relMin, final double relMax) {
        final double absDelta = absMax - absMin;
        final double absStart = absMin + absDelta * relMin;
        final double absStop = (relMin + relMax <= 1.0) ? (absMax - absDelta * relMax) : (absMax + absDelta * relMax);
        return (absDelta == 0.0) ? 1.0 : ((absStop - absStart) / absDelta);
    }
    
    private static double getCenterVal(final double absMin, final double absMax, final double relMin, final double relMax) {
        final double absDelta = absMax - absMin;
        final double absStart = absMin + absDelta * relMin;
        final double absStop = (relMin + relMax <= 1.0) ? (absMax - absDelta * relMax) : (absMax + absDelta * relMax);
        return absStart + (absStop - absStart) / 2.0;
    }
    
    protected Paint createPathGradientPaint(final PaintStyle.GradientPaint fill, final Graphics2D graphics) {
        return this.safeFractions((BiFunction<float[], Color[], Paint>)PathGradientPaint::new, fill);
    }
    
    private Paint safeFractions(final BiFunction<float[], Color[], Paint> init, final PaintStyle.GradientPaint fill) {
        final Iterator<Color> styles = Stream.of(fill.getGradientColors()).map(s -> (s == null) ? DrawPaint.TRANSPARENT : applyColorTransform(s)).iterator();
        final Map<Float, Color> m = new TreeMap<Float, Color>();
        for (final float fraction : fill.getGradientFractions()) {
            m.put(fraction, styles.next());
        }
        return init.apply(toArray(m.keySet()), m.values().toArray(new Color[0]));
    }
    
    private static float[] toArray(final Collection<Float> floatList) {
        final int[] idx = { 0 };
        final float[] ret = new float[floatList.size()];
        floatList.forEach(f -> ret[idx[0]++] = f);
        return ret;
    }
    
    public static Color HSL2RGB(double h, double s, double l, final double alpha) {
        s = Math.max(0.0, Math.min(100.0, s));
        l = Math.max(0.0, Math.min(100.0, l));
        if (alpha < 0.0 || alpha > 1.0) {
            final String message = "Color parameter outside of expected range - Alpha: " + alpha;
            throw new IllegalArgumentException(message);
        }
        h %= 360.0;
        h /= 360.0;
        s /= 100.0;
        l /= 100.0;
        final double q = (l < 0.5) ? (l * (1.0 + s)) : (l + s - s * l);
        final double p = 2.0 * l - q;
        double r = Math.max(0.0, HUE2RGB(p, q, h + 0.3333333333333333));
        double g = Math.max(0.0, HUE2RGB(p, q, h));
        double b = Math.max(0.0, HUE2RGB(p, q, h - 0.3333333333333333));
        r = Math.min(r, 1.0);
        g = Math.min(g, 1.0);
        b = Math.min(b, 1.0);
        return new Color((float)r, (float)g, (float)b, (float)alpha);
    }
    
    private static double HUE2RGB(final double p, final double q, double h) {
        if (h < 0.0) {
            ++h;
        }
        if (h > 1.0) {
            --h;
        }
        if (6.0 * h < 1.0) {
            return p + (q - p) * 6.0 * h;
        }
        if (2.0 * h < 1.0) {
            return q;
        }
        if (3.0 * h < 2.0) {
            return p + (q - p) * 6.0 * (0.6666666666666666 - h);
        }
        return p;
    }
    
    public static double[] RGB2HSL(final Color color) {
        final float[] rgb = color.getRGBColorComponents(null);
        final double r = rgb[0];
        final double g = rgb[1];
        final double b = rgb[2];
        final double min = Math.min(r, Math.min(g, b));
        final double max = Math.max(r, Math.max(g, b));
        double h = 0.0;
        if (max == min) {
            h = 0.0;
        }
        else if (max == r) {
            h = (60.0 * (g - b) / (max - min) + 360.0) % 360.0;
        }
        else if (max == g) {
            h = 60.0 * (b - r) / (max - min) + 120.0;
        }
        else if (max == b) {
            h = 60.0 * (r - g) / (max - min) + 240.0;
        }
        final double l = (max + min) / 2.0;
        double s;
        if (max == min) {
            s = 0.0;
        }
        else if (l <= 0.5) {
            s = (max - min) / (max + min);
        }
        else {
            s = (max - min) / (2.0 - max - min);
        }
        return new double[] { h, s * 100.0, l * 100.0 };
    }
    
    public static int srgb2lin(final float sRGB) {
        if (sRGB <= 0.04045) {
            return (int)Math.rint(100000.0 * sRGB / 12.92);
        }
        return (int)Math.rint(100000.0 * Math.pow((sRGB + 0.055) / 1.055, 2.4));
    }
    
    public static float lin2srgb(final int linRGB) {
        if (linRGB <= 0.0031308) {
            return (float)(linRGB / 100000.0 * 12.92);
        }
        return (float)(1.055 * Math.pow(linRGB / 100000.0, 0.4166666666666667) - 0.055);
    }
    
    static void fillPaintWorkaround(final Graphics2D graphics, final Shape shape) {
        try {
            graphics.fill(shape);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            DrawPaint.LOG.log(5, "IBM JDK failed with TexturePaintContext AIOOBE - try adding the following to the VM parameter:\n-Xjit:exclude={sun/java2d/pipe/AlphaPaintPipe.renderPathTile(Ljava/lang/Object;[BIIIIII)V} and search for 'JIT Problem Determination for IBM SDK using -Xjit' (http://www-01.ibm.com/support/docview.wss?uid=swg21294023) for how to add/determine further excludes", e);
        }
    }
    
    static {
        LOG = POILogFactory.getLogger(DrawPaint.class);
        TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.0f);
    }
    
    private static class SimpleSolidPaint implements PaintStyle.SolidPaint
    {
        private final ColorStyle solidColor;
        
        SimpleSolidPaint(final Color color) {
            if (color == null) {
                throw new NullPointerException("Color needs to be specified");
            }
            this.solidColor = new AbstractColorStyle() {
                @Override
                public Color getColor() {
                    return new Color(color.getRed(), color.getGreen(), color.getBlue());
                }
                
                @Override
                public int getAlpha() {
                    return (int)Math.round(color.getAlpha() * 100000.0 / 255.0);
                }
                
                @Override
                public int getHueOff() {
                    return -1;
                }
                
                @Override
                public int getHueMod() {
                    return -1;
                }
                
                @Override
                public int getSatOff() {
                    return -1;
                }
                
                @Override
                public int getSatMod() {
                    return -1;
                }
                
                @Override
                public int getLumOff() {
                    return -1;
                }
                
                @Override
                public int getLumMod() {
                    return -1;
                }
                
                @Override
                public int getShade() {
                    return -1;
                }
                
                @Override
                public int getTint() {
                    return -1;
                }
            };
        }
        
        SimpleSolidPaint(final ColorStyle color) {
            if (color == null) {
                throw new NullPointerException("Color needs to be specified");
            }
            this.solidColor = color;
        }
        
        @Override
        public ColorStyle getSolidColor() {
            return this.solidColor;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof PaintStyle.SolidPaint && Objects.equals(this.getSolidColor(), ((PaintStyle.SolidPaint)o).getSolidColor()));
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.solidColor);
        }
    }
}
