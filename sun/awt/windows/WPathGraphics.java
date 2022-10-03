package sun.awt.windows;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.print.PrinterException;
import java.awt.Paint;
import sun.print.ProxyGraphics2D;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.ComponentSampleModel;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.ByteComponentRaster;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.ImageObserver;
import java.awt.geom.Rectangle2D;
import java.awt.Image;
import java.util.Arrays;
import java.util.Locale;
import java.awt.font.GlyphVector;
import sun.font.PhysicalFont;
import java.awt.Color;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import sun.font.Font2D;
import sun.font.TrueTypeFont;
import sun.font.CompositeFont;
import sun.font.FontUtilities;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.Stroke;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.awt.Graphics2D;
import sun.print.PathGraphics;

final class WPathGraphics extends PathGraphics
{
    private static final int DEFAULT_USER_RES = 72;
    private static final float MIN_DEVICE_LINEWIDTH = 1.2f;
    private static final float MAX_THINLINE_INCHES = 0.014f;
    private static boolean useGDITextLayout;
    private static boolean preferGDITextLayout;
    
    WPathGraphics(final Graphics2D graphics2D, final PrinterJob printerJob, final Printable printable, final PageFormat pageFormat, final int n, final boolean b) {
        super(graphics2D, printerJob, printable, pageFormat, n, b);
    }
    
    @Override
    public Graphics create() {
        return new WPathGraphics((Graphics2D)this.getDelegate().create(), this.getPrinterJob(), this.getPrintable(), this.getPageFormat(), this.getPageIndex(), this.canDoRedraws());
    }
    
    @Override
    public void draw(final Shape shape) {
        final Stroke stroke = this.getStroke();
        if (stroke instanceof BasicStroke) {
            Stroke stroke2 = null;
            final BasicStroke stroke3 = (BasicStroke)stroke;
            final float lineWidth = stroke3.getLineWidth();
            final Point2D.Float float1 = new Point2D.Float(lineWidth, lineWidth);
            final AffineTransform transform = this.getTransform();
            transform.deltaTransform(float1, float1);
            if (Math.min(Math.abs(float1.x), Math.abs(float1.y)) < 1.2f) {
                final Point2D.Float float2 = new Point2D.Float(1.2f, 1.2f);
                try {
                    transform.createInverse().deltaTransform(float2, float2);
                    stroke2 = new BasicStroke(Math.max(Math.abs(float2.x), Math.abs(float2.y)), stroke3.getEndCap(), stroke3.getLineJoin(), stroke3.getMiterLimit(), stroke3.getDashArray(), stroke3.getDashPhase());
                    this.setStroke(stroke2);
                }
                catch (final NoninvertibleTransformException ex) {}
            }
            super.draw(shape);
            if (stroke2 != null) {
                this.setStroke(stroke3);
            }
        }
        else {
            super.draw(shape);
        }
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        this.drawString(s, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final String s, final float n, final float n2) {
        this.drawString(s, n, n2, this.getFont(), this.getFontRenderContext(), 0.0f);
    }
    
    @Override
    protected int platformFontCount(final Font font, final String s) {
        final AffineTransform affineTransform = new AffineTransform(this.getTransform());
        affineTransform.concatenate(this.getFont().getTransform());
        final int type = affineTransform.getType();
        if (type == 32 || (type & 0x40) != 0x0) {
            return 0;
        }
        final Font2D font2D = FontUtilities.getFont2D(font);
        if (font2D instanceof CompositeFont || font2D instanceof TrueTypeFont) {
            return 1;
        }
        return 0;
    }
    
    private static boolean isXP() {
        final String property = System.getProperty("os.version");
        return property != null && Float.valueOf(property) >= 5.1f;
    }
    
    private boolean strNeedsTextLayout(final String s, final Font font) {
        final char[] charArray = s.toCharArray();
        return FontUtilities.isComplexText(charArray, 0, charArray.length) && (!WPathGraphics.useGDITextLayout || (!WPathGraphics.preferGDITextLayout && (!isXP() || !FontUtilities.textLayoutIsCompatible(font))));
    }
    
    private int getAngle(final Point2D.Double double1) {
        double degrees = Math.toDegrees(Math.atan2(double1.y, double1.x));
        if (degrees < 0.0) {
            degrees += 360.0;
        }
        if (degrees != 0.0) {
            degrees = 360.0 - degrees;
        }
        return (int)Math.round(degrees * 10.0);
    }
    
    private float getAwScale(final double n, final double n2) {
        float n3 = (float)(n / n2);
        if (n3 > 0.999f && n3 < 1.001f) {
            n3 = 1.0f;
        }
        return n3;
    }
    
    public void drawString(final String s, final float n, final float n2, final Font font, final FontRenderContext fontRenderContext, final float n3) {
        if (s.length() == 0) {
            return;
        }
        if (WPrinterJob.shapeTextProp) {
            super.drawString(s, n, n2, font, fontRenderContext, n3);
            return;
        }
        final boolean strNeedsTextLayout = this.strNeedsTextLayout(s, font);
        if ((font.hasLayoutAttributes() || strNeedsTextLayout) && !this.printingGlyphVector) {
            new TextLayout(s, font, fontRenderContext).draw(this, n, n2);
            return;
        }
        if (strNeedsTextLayout) {
            super.drawString(s, n, n2, font, fontRenderContext, n3);
            return;
        }
        final AffineTransform transform = this.getTransform();
        final AffineTransform affineTransform = new AffineTransform(transform);
        affineTransform.concatenate(font.getTransform());
        final int type = affineTransform.getType();
        boolean b = type != 32 && (type & 0x40) == 0x0;
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        try {
            wPrinterJob.setTextColor((Color)this.getPaint());
        }
        catch (final ClassCastException ex) {
            b = false;
        }
        if (!b) {
            super.drawString(s, n, n2, font, fontRenderContext, n3);
            return;
        }
        final Point2D.Float float1 = new Point2D.Float(n, n2);
        final Point2D.Float float2 = new Point2D.Float();
        if (font.isTransformed()) {
            final AffineTransform transform2 = font.getTransform();
            float n4 = (float)transform2.getTranslateX();
            float n5 = (float)transform2.getTranslateY();
            if (Math.abs(n4) < 1.0E-5) {
                n4 = 0.0f;
            }
            if (Math.abs(n5) < 1.0E-5) {
                n5 = 0.0f;
            }
            final Point2D.Float float3 = float1;
            float3.x += n4;
            final Point2D.Float float4 = float1;
            float4.y += n5;
        }
        transform.transform(float1, float2);
        if (this.getClip() != null) {
            this.deviceClip(this.getClip().getPathIterator(transform));
        }
        final float size2D = font.getSize2D();
        double xRes = wPrinterJob.getXRes();
        double yRes = wPrinterJob.getYRes();
        final double n6 = yRes / 72.0;
        final int orientation = this.getPageFormat().getOrientation();
        if (orientation == 0 || orientation == 2) {
            final double n7 = xRes;
            xRes = yRes;
            yRes = n7;
        }
        affineTransform.scale(1.0 / (xRes / 72.0), 1.0 / (yRes / 72.0));
        final Point2D.Double double1 = new Point2D.Double(0.0, 1.0);
        affineTransform.deltaTransform(double1, double1);
        final double sqrt = Math.sqrt(double1.x * double1.x + double1.y * double1.y);
        final float n8 = (float)(size2D * sqrt * n6);
        final Point2D.Double double2 = new Point2D.Double(1.0, 0.0);
        affineTransform.deltaTransform(double2, double2);
        final float awScale = this.getAwScale(Math.sqrt(double2.x * double2.x + double2.y * double2.y), sqrt);
        final int angle = this.getAngle(double2);
        final Point2D.Double double3 = new Point2D.Double(1.0, 0.0);
        transform.deltaTransform(double3, double3);
        final double sqrt2 = Math.sqrt(double3.x * double3.x + double3.y * double3.y);
        final Point2D.Double double4 = new Point2D.Double(0.0, 1.0);
        transform.deltaTransform(double4, double4);
        final double sqrt3 = Math.sqrt(double4.x * double4.x + double4.y * double4.y);
        final Font2D font2D = FontUtilities.getFont2D(font);
        if (font2D instanceof TrueTypeFont) {
            this.textOut(s, font, (PhysicalFont)font2D, fontRenderContext, n8, angle, awScale, sqrt2, sqrt3, n, n2, float2.x, float2.y, n3);
        }
        else if (font2D instanceof CompositeFont) {
            final CompositeFont compositeFont = (CompositeFont)font2D;
            float n9 = n;
            float n10 = float2.x;
            float n11 = float2.y;
            final char[] charArray = s.toCharArray();
            final int length = charArray.length;
            final int[] array = new int[length];
            compositeFont.getMapper().charsToGlyphs(length, charArray, array);
            int i = 0;
            while (i < length) {
                final int n12 = i;
                int n13;
                for (n13 = array[n12] >>> 24; i < length && array[i] >>> 24 == n13; ++i) {}
                final String s2 = new String(charArray, n12, i - n12);
                this.textOut(s2, font, compositeFont.getSlotFont(n13), fontRenderContext, n8, angle, awScale, sqrt2, sqrt3, n9, n2, n10, n11, 0.0f);
                final float n14 = (float)font.getStringBounds(s2, fontRenderContext).getWidth();
                n9 += n14;
                final Point2D.Float float5 = float1;
                float5.x += n14;
                transform.transform(float1, float2);
                n10 = float2.x;
                n11 = float2.y;
            }
        }
        else {
            super.drawString(s, n, n2, font, fontRenderContext, n3);
        }
    }
    
    @Override
    protected boolean printGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        if ((glyphVector.getLayoutFlags() & 0x1) != 0x0) {
            return false;
        }
        if (glyphVector.getNumGlyphs() == 0) {
            return true;
        }
        final AffineTransform transform = this.getTransform();
        final AffineTransform affineTransform = new AffineTransform(transform);
        final Font font = glyphVector.getFont();
        affineTransform.concatenate(font.getTransform());
        final int type = affineTransform.getType();
        boolean b = type != 32 && (type & 0x40) == 0x0;
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        try {
            wPrinterJob.setTextColor((Color)this.getPaint());
        }
        catch (final ClassCastException ex) {
            b = false;
        }
        if (WPrinterJob.shapeTextProp || !b) {
            return false;
        }
        final Point2D.Float float1 = new Point2D.Float(n, n2);
        final Point2D glyphPosition = glyphVector.getGlyphPosition(0);
        final Point2D.Float float2 = float1;
        float2.x += (float)glyphPosition.getX();
        final Point2D.Float float3 = float1;
        float3.y += (float)glyphPosition.getY();
        final Point2D.Float float4 = new Point2D.Float();
        if (font.isTransformed()) {
            final AffineTransform transform2 = font.getTransform();
            float n3 = (float)transform2.getTranslateX();
            float n4 = (float)transform2.getTranslateY();
            if (Math.abs(n3) < 1.0E-5) {
                n3 = 0.0f;
            }
            if (Math.abs(n4) < 1.0E-5) {
                n4 = 0.0f;
            }
            final Point2D.Float float5 = float1;
            float5.x += n3;
            final Point2D.Float float6 = float1;
            float6.y += n4;
        }
        transform.transform(float1, float4);
        if (this.getClip() != null) {
            this.deviceClip(this.getClip().getPathIterator(transform));
        }
        final float size2D = font.getSize2D();
        double xRes = wPrinterJob.getXRes();
        double yRes = wPrinterJob.getYRes();
        final double n5 = yRes / 72.0;
        final int orientation = this.getPageFormat().getOrientation();
        if (orientation == 0 || orientation == 2) {
            final double n6 = xRes;
            xRes = yRes;
            yRes = n6;
        }
        affineTransform.scale(1.0 / (xRes / 72.0), 1.0 / (yRes / 72.0));
        final Point2D.Double double1 = new Point2D.Double(0.0, 1.0);
        affineTransform.deltaTransform(double1, double1);
        final double sqrt = Math.sqrt(double1.x * double1.x + double1.y * double1.y);
        final float n7 = (float)(size2D * sqrt * n5);
        final Point2D.Double double2 = new Point2D.Double(1.0, 0.0);
        affineTransform.deltaTransform(double2, double2);
        final float awScale = this.getAwScale(Math.sqrt(double2.x * double2.x + double2.y * double2.y), sqrt);
        final int angle = this.getAngle(double2);
        final Point2D.Double double3 = new Point2D.Double(1.0, 0.0);
        transform.deltaTransform(double3, double3);
        final double sqrt2 = Math.sqrt(double3.x * double3.x + double3.y * double3.y);
        final Point2D.Double double4 = new Point2D.Double(0.0, 1.0);
        transform.deltaTransform(double4, double4);
        final double sqrt3 = Math.sqrt(double4.x * double4.x + double4.y * double4.y);
        int numGlyphs = glyphVector.getNumGlyphs();
        int[] glyphCodes = glyphVector.getGlyphCodes(0, numGlyphs, null);
        float[] glyphPositions = glyphVector.getGlyphPositions(0, numGlyphs, null);
        int n8 = 0;
        for (int i = 0; i < numGlyphs; ++i) {
            if ((glyphCodes[i] & 0xFFFF) >= 65534) {
                ++n8;
            }
        }
        if (n8 > 0) {
            final int n9 = numGlyphs - n8;
            final int[] array = new int[n9];
            final float[] array2 = new float[n9 * 2];
            int n10 = 0;
            for (int j = 0; j < numGlyphs; ++j) {
                if ((glyphCodes[j] & 0xFFFF) < 65534) {
                    array[n10] = glyphCodes[j];
                    array2[n10 * 2] = glyphPositions[j * 2];
                    array2[n10 * 2 + 1] = glyphPositions[j * 2 + 1];
                    ++n10;
                }
            }
            numGlyphs = n9;
            glyphCodes = array;
            glyphPositions = array2;
        }
        final AffineTransform scaleInstance = AffineTransform.getScaleInstance(sqrt2, sqrt3);
        final float[] array3 = new float[glyphPositions.length];
        scaleInstance.transform(glyphPositions, 0, array3, 0, glyphPositions.length / 2);
        final Font2D font2D = FontUtilities.getFont2D(font);
        if (font2D instanceof TrueTypeFont) {
            if (!wPrinterJob.setFont(font2D.getFamilyName(null), n7, font.getStyle() | font2D.getStyle(), angle, awScale)) {
                return false;
            }
            wPrinterJob.glyphsOut(glyphCodes, float4.x, float4.y, array3);
        }
        else {
            if (!(font2D instanceof CompositeFont)) {
                return false;
            }
            final CompositeFont compositeFont = (CompositeFont)font2D;
            float n11 = float4.x;
            float n12 = float4.y;
            int k = 0;
            while (k < numGlyphs) {
                final int n13 = k;
                int n14;
                for (n14 = glyphCodes[n13] >>> 24; k < numGlyphs && glyphCodes[k] >>> 24 == n14; ++k) {}
                final PhysicalFont slotFont = compositeFont.getSlotFont(n14);
                if (!(slotFont instanceof TrueTypeFont)) {
                    return false;
                }
                if (!wPrinterJob.setFont(slotFont.getFamilyName(null), n7, font.getStyle() | slotFont.getStyle(), angle, awScale)) {
                    return false;
                }
                final int[] copyOfRange = Arrays.copyOfRange(glyphCodes, n13, k);
                final float[] copyOfRange2 = Arrays.copyOfRange(array3, n13 * 2, k * 2);
                if (n13 != 0) {
                    final Point2D.Float float7 = new Point2D.Float(n + glyphPositions[n13 * 2], n2 + glyphPositions[n13 * 2 + 1]);
                    transform.transform(float7, float7);
                    n11 = float7.x;
                    n12 = float7.y;
                }
                wPrinterJob.glyphsOut(copyOfRange, n11, n12, copyOfRange2);
            }
        }
        return true;
    }
    
    private void textOut(String removeControlChars, final Font font, final PhysicalFont physicalFont, final FontRenderContext fontRenderContext, final float n, final int n2, final float n3, final double n4, final double n5, final float n6, final float n7, final float n8, final float n9, final float n10) {
        final String familyName = physicalFont.getFamilyName(null);
        final int n11 = font.getStyle() | physicalFont.getStyle();
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        if (!wPrinterJob.setFont(familyName, n, n11, n2, n3)) {
            super.drawString(removeControlChars, n6, n7, font, fontRenderContext, n10);
            return;
        }
        float[] array = null;
        if (!this.okGDIMetrics(removeControlChars, font, fontRenderContext, n4)) {
            removeControlChars = wPrinterJob.removeControlChars(removeControlChars);
            final char[] charArray = removeControlChars.toCharArray();
            final int length = charArray.length;
            GlyphVector glyphVector = null;
            if (!FontUtilities.isComplexText(charArray, 0, length)) {
                glyphVector = font.createGlyphVector(fontRenderContext, removeControlChars);
            }
            if (glyphVector == null) {
                super.drawString(removeControlChars, n6, n7, font, fontRenderContext, n10);
                return;
            }
            final float[] glyphPositions = glyphVector.getGlyphPositions(0, length, null);
            glyphVector.getGlyphPosition(glyphVector.getNumGlyphs());
            final AffineTransform scaleInstance = AffineTransform.getScaleInstance(n4, n5);
            final float[] array2 = new float[glyphPositions.length];
            scaleInstance.transform(glyphPositions, 0, array2, 0, glyphPositions.length / 2);
            array = array2;
        }
        wPrinterJob.textOut(removeControlChars, n8, n9, array);
    }
    
    private boolean okGDIMetrics(final String s, final Font font, final FontRenderContext fontRenderContext, final double n) {
        final double n2 = (double)Math.round(font.getStringBounds(s, fontRenderContext).getWidth() * n);
        final int gdiAdvance = ((WPrinterJob)this.getPrinterJob()).getGDIAdvance(s);
        if (n2 > 0.0 && gdiAdvance > 0) {
            final double abs = Math.abs(gdiAdvance - n2);
            double n3 = gdiAdvance / n2;
            if (n3 < 1.0) {
                n3 = 1.0 / n3;
            }
            return abs <= 1.0 || n3 < 1.01;
        }
        return true;
    }
    
    @Override
    protected boolean drawImageToPlatform(final Image image, AffineTransform affineTransform, Color white, final int n, final int n2, final int n3, final int n4, final boolean b) {
        final BufferedImage bufferedImage = this.getBufferedImage(image);
        if (bufferedImage == null) {
            return true;
        }
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        final AffineTransform transform = this.getTransform();
        if (affineTransform == null) {
            affineTransform = new AffineTransform();
        }
        transform.concatenate(affineTransform);
        final double[] array = new double[6];
        transform.getMatrix(array);
        final Point2D.Float float1 = new Point2D.Float(1.0f, 0.0f);
        final Point2D.Float float2 = new Point2D.Float(0.0f, 1.0f);
        transform.deltaTransform(float1, float1);
        transform.deltaTransform(float2, float2);
        final Point2D.Float float3 = new Point2D.Float(0.0f, 0.0f);
        double distance = float1.distance(float3);
        double distance2 = float2.distance(float3);
        final double xRes = wPrinterJob.getXRes();
        final double yRes = wPrinterJob.getYRes();
        final double n5 = xRes / 72.0;
        final double n6 = yRes / 72.0;
        if ((transform.getType() & 0x30) != 0x0) {
            if (distance > n5) {
                distance = n5;
            }
            if (distance2 > n6) {
                distance2 = n6;
            }
        }
        if (distance != 0.0 && distance2 != 0.0) {
            final AffineTransform affineTransform2 = new AffineTransform(array[0] / distance, array[1] / distance2, array[2] / distance, array[3] / distance2, array[4] / distance, array[5] / distance2);
            final Rectangle2D.Float float4 = new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4);
            final Rectangle2D bounds2D = affineTransform2.createTransformedShape(float4).getBounds2D();
            bounds2D.setRect(bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth() + 0.001, bounds2D.getHeight() + 0.001);
            final int n7 = (int)bounds2D.getWidth();
            final int n8 = (int)bounds2D.getHeight();
            if (n7 > 0 && n8 > 0) {
                boolean b2 = true;
                if (!b && this.hasTransparentPixels(bufferedImage)) {
                    b2 = false;
                    if (this.isBitmaskTransparency(bufferedImage)) {
                        if (white == null) {
                            if (this.drawBitmaskImage(bufferedImage, affineTransform, white, n, n2, n3, n4)) {
                                return true;
                            }
                        }
                        else if (white.getTransparency() == 1) {
                            b2 = true;
                        }
                    }
                    if (!this.canDoRedraws()) {
                        b2 = true;
                    }
                }
                else {
                    white = null;
                }
                if ((n + n3 > bufferedImage.getWidth(null) || n2 + n4 > bufferedImage.getHeight(null)) && this.canDoRedraws()) {
                    b2 = false;
                }
                if (!b2) {
                    transform.getMatrix(array);
                    final AffineTransform affineTransform3 = new AffineTransform(array[0] / n5, array[1] / n6, array[2] / n5, array[3] / n6, array[4] / n5, array[5] / n6);
                    final Rectangle2D bounds2D2 = transform.createTransformedShape(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4)).getBounds2D();
                    bounds2D2.setRect(bounds2D2.getX(), bounds2D2.getY(), bounds2D2.getWidth() + 0.001, bounds2D2.getHeight() + 0.001);
                    final int n9 = (int)bounds2D2.getWidth();
                    final int n10 = (int)bounds2D2.getHeight();
                    int n11 = n9 * n10 * 3;
                    final int n12 = 8388608;
                    final double n13 = (xRes < yRes) ? xRes : yRes;
                    int n14 = (int)n13;
                    double n15 = 1.0;
                    final double n16 = n9 / (double)n7;
                    final double n17 = n10 / (double)n8;
                    int n18 = (int)(n14 / ((n16 > n17) ? n17 : n16));
                    if (n18 < 72) {
                        n18 = 72;
                    }
                    while (n11 > n12 && n14 > n18) {
                        n15 *= 2.0;
                        n14 /= 2;
                        n11 /= 4;
                    }
                    if (n14 < n18) {
                        n15 = n13 / n18;
                    }
                    bounds2D2.setRect(bounds2D2.getX() / n15, bounds2D2.getY() / n15, bounds2D2.getWidth() / n15, bounds2D2.getHeight() / n15);
                    wPrinterJob.saveState(this.getTransform(), this.getClip(), bounds2D2, n15, n15);
                    return true;
                }
                int n19 = 5;
                IndexColorModel indexColorModel = null;
                final ColorModel colorModel = bufferedImage.getColorModel();
                final int type = bufferedImage.getType();
                if (colorModel instanceof IndexColorModel && colorModel.getPixelSize() <= 8 && (type == 12 || type == 13)) {
                    indexColorModel = (IndexColorModel)colorModel;
                    if ((n19 = type) == 12 && colorModel.getPixelSize() == 2) {
                        final int[] array2 = new int[16];
                        indexColorModel.getRGBs(array2);
                        indexColorModel = new IndexColorModel(4, 16, array2, 0, indexColorModel.getTransparency() != 1, indexColorModel.getTransparentPixel(), 0);
                    }
                }
                final int n20 = (int)bounds2D.getWidth();
                final int n21 = (int)bounds2D.getHeight();
                BufferedImage bufferedImage2;
                if (true) {
                    if (indexColorModel == null) {
                        bufferedImage2 = new BufferedImage(n20, n21, n19);
                    }
                    else {
                        bufferedImage2 = new BufferedImage(n20, n21, n19, indexColorModel);
                    }
                    final Graphics2D graphics = bufferedImage2.createGraphics();
                    graphics.clipRect(0, 0, bufferedImage2.getWidth(), bufferedImage2.getHeight());
                    graphics.translate(-bounds2D.getX(), -bounds2D.getY());
                    graphics.transform(affineTransform2);
                    if (white == null) {
                        white = Color.white;
                    }
                    graphics.drawImage(bufferedImage, n, n2, n + n3, n2 + n4, n, n2, n + n3, n2 + n4, white, null);
                    graphics.dispose();
                }
                else {
                    bufferedImage2 = bufferedImage;
                }
                final Rectangle2D.Float float5 = new Rectangle2D.Float((float)(bounds2D.getX() * distance), (float)(bounds2D.getY() * distance2), (float)(bounds2D.getWidth() * distance), (float)(bounds2D.getHeight() * distance2));
                final WritableRaster raster = bufferedImage2.getRaster();
                byte[] array3;
                if (raster instanceof ByteComponentRaster) {
                    array3 = ((ByteComponentRaster)raster).getDataStorage();
                }
                else {
                    if (!(raster instanceof BytePackedRaster)) {
                        return false;
                    }
                    array3 = ((BytePackedRaster)raster).getDataStorage();
                }
                int pixelBitStride = 24;
                final SampleModel sampleModel = bufferedImage2.getSampleModel();
                if (sampleModel instanceof ComponentSampleModel) {
                    pixelBitStride = ((ComponentSampleModel)sampleModel).getPixelStride() * 8;
                }
                else if (sampleModel instanceof MultiPixelPackedSampleModel) {
                    pixelBitStride = ((MultiPixelPackedSampleModel)sampleModel).getPixelBitStride();
                }
                else if (indexColorModel != null) {
                    final int width = bufferedImage2.getWidth();
                    final int height = bufferedImage2.getHeight();
                    if (width > 0 && height > 0) {
                        pixelBitStride = array3.length * 8 / width / height;
                    }
                }
                final Shape clip = this.getClip();
                this.clip(affineTransform.createTransformedShape(float4));
                this.deviceClip(this.getClip().getPathIterator(this.getTransform()));
                wPrinterJob.drawDIBImage(array3, float5.x, float5.y, (float)Math.rint(float5.width + 0.5), (float)Math.rint(float5.height + 0.5), 0.0f, 0.0f, (float)bufferedImage2.getWidth(), (float)bufferedImage2.getHeight(), pixelBitStride, indexColorModel);
                this.setClip(clip);
            }
        }
        return true;
    }
    
    @Override
    public void redrawRegion(final Rectangle2D rectangle2D, final double n, final double n2, final Shape shape, final AffineTransform affineTransform) throws PrinterException {
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        final Printable printable = this.getPrintable();
        final PageFormat pageFormat = this.getPageFormat();
        final int pageIndex = this.getPageIndex();
        final BufferedImage bufferedImage = new BufferedImage((int)rectangle2D.getWidth(), (int)rectangle2D.getHeight(), 5);
        final Graphics2D graphics = bufferedImage.createGraphics();
        final ProxyGraphics2D proxyGraphics2D = new ProxyGraphics2D(graphics, wPrinterJob);
        proxyGraphics2D.setColor(Color.white);
        proxyGraphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        proxyGraphics2D.clipRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        proxyGraphics2D.translate(-rectangle2D.getX(), -rectangle2D.getY());
        proxyGraphics2D.scale((float)(wPrinterJob.getXRes() / n) / 72.0f, (float)(wPrinterJob.getYRes() / n2) / 72.0f);
        proxyGraphics2D.translate(-wPrinterJob.getPhysicalPrintableX(pageFormat.getPaper()) / wPrinterJob.getXRes() * 72.0, -wPrinterJob.getPhysicalPrintableY(pageFormat.getPaper()) / wPrinterJob.getYRes() * 72.0);
        proxyGraphics2D.transform(new AffineTransform(this.getPageFormat().getMatrix()));
        proxyGraphics2D.setPaint(Color.black);
        printable.print(proxyGraphics2D, pageFormat, pageIndex);
        graphics.dispose();
        if (shape != null) {
            this.deviceClip(shape.getPathIterator(affineTransform));
        }
        final Rectangle2D.Float float1 = new Rectangle2D.Float((float)(rectangle2D.getX() * n), (float)(rectangle2D.getY() * n2), (float)(rectangle2D.getWidth() * n), (float)(rectangle2D.getHeight() * n2));
        wPrinterJob.drawImage3ByteBGR(((ByteComponentRaster)bufferedImage.getRaster()).getDataStorage(), float1.x, float1.y, float1.width, float1.height, 0.0f, 0.0f, (float)bufferedImage.getWidth(), (float)bufferedImage.getHeight());
    }
    
    @Override
    protected void deviceFill(final PathIterator pathIterator, final Color color) {
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        this.convertToWPath(pathIterator);
        wPrinterJob.selectSolidBrush(color);
        wPrinterJob.fillPath();
    }
    
    @Override
    protected void deviceClip(final PathIterator pathIterator) {
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        this.convertToWPath(pathIterator);
        wPrinterJob.selectClipPath();
    }
    
    @Override
    protected void deviceFrameRect(final int n, final int n2, final int n3, final int n4, final Color color) {
        final AffineTransform transform = this.getTransform();
        if ((transform.getType() & 0x30) != 0x0) {
            this.draw(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
            return;
        }
        final Stroke stroke = this.getStroke();
        if (stroke instanceof BasicStroke) {
            final BasicStroke basicStroke = (BasicStroke)stroke;
            final int endCap = basicStroke.getEndCap();
            final int lineJoin = basicStroke.getLineJoin();
            if (endCap == 2 && lineJoin == 0 && basicStroke.getMiterLimit() == 10.0f) {
                final float lineWidth = basicStroke.getLineWidth();
                final Point2D.Float float1 = new Point2D.Float(lineWidth, lineWidth);
                transform.deltaTransform(float1, float1);
                final float min = Math.min(Math.abs(float1.x), Math.abs(float1.y));
                final Point2D.Float float2 = new Point2D.Float((float)n, (float)n2);
                transform.transform(float2, float2);
                final Point2D.Float float3 = new Point2D.Float((float)(n + n3), (float)(n2 + n4));
                transform.transform(float3, float3);
                final float n5 = (float)(float3.getX() - float2.getX());
                final float n6 = (float)(float3.getY() - float2.getY());
                final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
                if (wPrinterJob.selectStylePen(endCap, lineJoin, min, color)) {
                    wPrinterJob.frameRect((float)float2.getX(), (float)float2.getY(), n5, n6);
                }
                else if (min / Math.min(wPrinterJob.getXRes(), wPrinterJob.getYRes()) < 0.014000000432133675) {
                    wPrinterJob.selectPen(min, color);
                    wPrinterJob.frameRect((float)float2.getX(), (float)float2.getY(), n5, n6);
                }
                else {
                    this.draw(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
                }
            }
            else {
                this.draw(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
            }
        }
    }
    
    @Override
    protected void deviceFillRect(final int n, final int n2, final int n3, final int n4, final Color color) {
        final AffineTransform transform = this.getTransform();
        if ((transform.getType() & 0x30) != 0x0) {
            this.fill(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
            return;
        }
        final Point2D.Float float1 = new Point2D.Float((float)n, (float)n2);
        transform.transform(float1, float1);
        final Point2D.Float float2 = new Point2D.Float((float)(n + n3), (float)(n2 + n4));
        transform.transform(float2, float2);
        ((WPrinterJob)this.getPrinterJob()).fillRect((float)float1.getX(), (float)float1.getY(), (float)(float2.getX() - float1.getX()), (float)(float2.getY() - float1.getY()), color);
    }
    
    @Override
    protected void deviceDrawLine(final int n, final int n2, final int n3, final int n4, final Color color) {
        final Stroke stroke = this.getStroke();
        if (stroke instanceof BasicStroke) {
            final BasicStroke basicStroke = (BasicStroke)stroke;
            if (basicStroke.getDashArray() != null) {
                this.draw(new Line2D.Float((float)n, (float)n2, (float)n3, (float)n4));
                return;
            }
            final float lineWidth = basicStroke.getLineWidth();
            final Point2D.Float float1 = new Point2D.Float(lineWidth, lineWidth);
            final AffineTransform transform = this.getTransform();
            transform.deltaTransform(float1, float1);
            final float min = Math.min(Math.abs(float1.x), Math.abs(float1.y));
            final Point2D.Float float2 = new Point2D.Float((float)n, (float)n2);
            transform.transform(float2, float2);
            final Point2D.Float float3 = new Point2D.Float((float)n3, (float)n4);
            transform.transform(float3, float3);
            int endCap = basicStroke.getEndCap();
            final int lineJoin = basicStroke.getLineJoin();
            if (float3.getX() == float2.getX() && float3.getY() == float2.getY()) {
                endCap = 1;
            }
            final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
            if (wPrinterJob.selectStylePen(endCap, lineJoin, min, color)) {
                wPrinterJob.moveTo((float)float2.getX(), (float)float2.getY());
                wPrinterJob.lineTo((float)float3.getX(), (float)float3.getY());
            }
            else {
                final double min2 = Math.min(wPrinterJob.getXRes(), wPrinterJob.getYRes());
                if (endCap == 1 || ((n == n3 || n2 == n4) && min / min2 < 0.014000000432133675)) {
                    wPrinterJob.selectPen(min, color);
                    wPrinterJob.moveTo((float)float2.getX(), (float)float2.getY());
                    wPrinterJob.lineTo((float)float3.getX(), (float)float3.getY());
                }
                else {
                    this.draw(new Line2D.Float((float)n, (float)n2, (float)n3, (float)n4));
                }
            }
        }
    }
    
    private void convertToWPath(final PathIterator pathIterator) {
        final float[] array = new float[6];
        final WPrinterJob wPrinterJob = (WPrinterJob)this.getPrinterJob();
        int polyFillMode;
        if (pathIterator.getWindingRule() == 0) {
            polyFillMode = 1;
        }
        else {
            polyFillMode = 2;
        }
        wPrinterJob.setPolyFillMode(polyFillMode);
        wPrinterJob.beginPath();
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    wPrinterJob.moveTo(array[0], array[1]);
                    break;
                }
                case 1: {
                    wPrinterJob.lineTo(array[0], array[1]);
                    break;
                }
                case 2: {
                    final int penX = wPrinterJob.getPenX();
                    final int penY = wPrinterJob.getPenY();
                    wPrinterJob.polyBezierTo(penX + (array[0] - penX) * 2.0f / 3.0f, penY + (array[1] - penY) * 2.0f / 3.0f, array[2] - (array[2] - array[0]) * 2.0f / 3.0f, array[3] - (array[3] - array[1]) * 2.0f / 3.0f, array[2], array[3]);
                    break;
                }
                case 3: {
                    wPrinterJob.polyBezierTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                    break;
                }
                case 4: {
                    wPrinterJob.closeFigure();
                    break;
                }
            }
            pathIterator.next();
        }
        wPrinterJob.endPath();
    }
    
    static {
        WPathGraphics.useGDITextLayout = true;
        WPathGraphics.preferGDITextLayout = false;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.print.enableGDITextLayout"));
        if (s != null) {
            WPathGraphics.useGDITextLayout = Boolean.getBoolean(s);
            if (!WPathGraphics.useGDITextLayout && s.equalsIgnoreCase("prefer")) {
                WPathGraphics.useGDITextLayout = true;
                WPathGraphics.preferGDITextLayout = true;
            }
        }
    }
}
