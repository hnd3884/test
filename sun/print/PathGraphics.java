package sun.print;

import java.awt.image.RenderedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.ColorModel;
import sun.awt.image.SunWritableRaster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import sun.awt.image.ToolkitImage;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.geom.PathIterator;
import sun.font.PhysicalFont;
import java.awt.font.TextAttribute;
import sun.font.Font2D;
import sun.font.CompositeFont;
import sun.font.FontUtilities;
import java.awt.font.GlyphVector;
import java.text.AttributedCharacterIterator;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.font.TextLayout;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.print.PrinterException;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.print.PrinterJob;
import java.awt.Graphics2D;
import sun.font.Font2DHandle;
import java.util.Hashtable;
import java.lang.ref.SoftReference;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

public abstract class PathGraphics extends ProxyGraphics2D
{
    private Printable mPainter;
    private PageFormat mPageFormat;
    private int mPageIndex;
    private boolean mCanRedraw;
    protected boolean printingGlyphVector;
    protected static SoftReference<Hashtable<Font2DHandle, Object>> fontMapRef;
    
    protected PathGraphics(final Graphics2D graphics2D, final PrinterJob printerJob, final Printable mPainter, final PageFormat mPageFormat, final int mPageIndex, final boolean mCanRedraw) {
        super(graphics2D, printerJob);
        this.mPainter = mPainter;
        this.mPageFormat = mPageFormat;
        this.mPageIndex = mPageIndex;
        this.mCanRedraw = mCanRedraw;
    }
    
    protected Printable getPrintable() {
        return this.mPainter;
    }
    
    protected PageFormat getPageFormat() {
        return this.mPageFormat;
    }
    
    protected int getPageIndex() {
        return this.mPageIndex;
    }
    
    public boolean canDoRedraws() {
        return this.mCanRedraw;
    }
    
    public abstract void redrawRegion(final Rectangle2D p0, final double p1, final double p2, final Shape p3, final AffineTransform p4) throws PrinterException;
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        final Paint paint = this.getPaint();
        try {
            final AffineTransform transform = this.getTransform();
            if (this.getClip() != null) {
                this.deviceClip(this.getClip().getPathIterator(transform));
            }
            this.deviceDrawLine(n, n2, n3, n4, (Color)paint);
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException("Expected a Color instance");
        }
    }
    
    @Override
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        final Paint paint = this.getPaint();
        try {
            final AffineTransform transform = this.getTransform();
            if (this.getClip() != null) {
                this.deviceClip(this.getClip().getPathIterator(transform));
            }
            this.deviceFrameRect(n, n2, n3, n4, (Color)paint);
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException("Expected a Color instance");
        }
    }
    
    @Override
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        final Paint paint = this.getPaint();
        try {
            final AffineTransform transform = this.getTransform();
            if (this.getClip() != null) {
                this.deviceClip(this.getClip().getPathIterator(transform));
            }
            this.deviceFillRect(n, n2, n3, n4, (Color)paint);
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException("Expected a Color instance");
        }
    }
    
    @Override
    public void clearRect(final int n, final int n2, final int n3, final int n4) {
        this.fill(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4), this.getBackground());
    }
    
    @Override
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.draw(new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.fill(new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void drawOval(final int n, final int n2, final int n3, final int n4) {
        this.draw(new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void fillOval(final int n, final int n2, final int n3, final int n4) {
        this.fill(new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.draw(new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 0));
    }
    
    @Override
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.fill(new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 2));
    }
    
    @Override
    public void drawPolyline(final int[] array, final int[] array2, final int n) {
        if (n > 0) {
            float n2 = (float)array[0];
            float n3 = (float)array2[0];
            for (int i = 1; i < n; ++i) {
                final float n4 = (float)array[i];
                final float n5 = (float)array2[i];
                this.draw(new Line2D.Float(n2, n3, n4, n5));
                n2 = n4;
                n3 = n5;
            }
        }
    }
    
    @Override
    public void drawPolygon(final int[] array, final int[] array2, final int n) {
        this.draw(new Polygon(array, array2, n));
    }
    
    @Override
    public void drawPolygon(final Polygon polygon) {
        this.draw(polygon);
    }
    
    @Override
    public void fillPolygon(final int[] array, final int[] array2, final int n) {
        this.fill(new Polygon(array, array2, n));
    }
    
    @Override
    public void fillPolygon(final Polygon polygon) {
        this.fill(polygon);
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        this.drawString(s, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final String s, final float n, final float n2) {
        if (s.length() == 0) {
            return;
        }
        new TextLayout(s, this.getFont(), this.getFontRenderContext()).draw(this, n, n2);
    }
    
    protected void drawString(final String s, final float n, final float n2, final Font font, final FontRenderContext fontRenderContext, final float n3) {
        this.fill(new TextLayout(s, font, fontRenderContext).getOutline(AffineTransform.getTranslateInstance(n, n2)));
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        this.drawString(attributedCharacterIterator, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final float n, final float n2) {
        if (attributedCharacterIterator == null) {
            throw new NullPointerException("attributedcharacteriterator is null");
        }
        new TextLayout(attributedCharacterIterator, this.getFontRenderContext()).draw(this, n, n2);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        if (!this.printingGlyphVector) {
            try {
                this.printingGlyphVector = true;
                if (RasterPrinterJob.shapeTextProp || !this.printedSimpleGlyphVector(glyphVector, n, n2)) {
                    this.fill(glyphVector.getOutline(n, n2));
                }
            }
            finally {
                this.printingGlyphVector = false;
            }
            return;
        }
        assert !this.printingGlyphVector;
        this.fill(glyphVector.getOutline(n, n2));
    }
    
    protected int platformFontCount(final Font font, final String s) {
        return 0;
    }
    
    protected boolean printGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        return false;
    }
    
    boolean printedSimpleGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        final int layoutFlags = glyphVector.getLayoutFlags();
        if (layoutFlags != 0 && layoutFlags != 2) {
            return this.printGlyphVector(glyphVector, n, n2);
        }
        final Font font = glyphVector.getFont();
        final Font2D font2D = FontUtilities.getFont2D(font);
        if (font2D.handle.font2D != font2D) {
            return false;
        }
        Hashtable hashtable;
        synchronized (PathGraphics.class) {
            hashtable = PathGraphics.fontMapRef.get();
            if (hashtable == null) {
                hashtable = new Hashtable();
                PathGraphics.fontMapRef = new SoftReference<Hashtable<Font2DHandle, Object>>(hashtable);
            }
        }
        final int numGlyphs = glyphVector.getNumGlyphs();
        final int[] glyphCodes = glyphVector.getGlyphCodes(0, numGlyphs, null);
        Object glyphToCharMapForFont = null;
        char[][] array = null;
        CompositeFont compositeFont = null;
        synchronized (hashtable) {
            if (font2D instanceof CompositeFont) {
                compositeFont = (CompositeFont)font2D;
                final int numSlots = compositeFont.getNumSlots();
                array = (char[][])hashtable.get(font2D.handle);
                if (array == null) {
                    array = new char[numSlots][];
                    hashtable.put(font2D.handle, array);
                }
                for (int i = 0; i < numGlyphs; ++i) {
                    final int n3 = glyphCodes[i] >>> 24;
                    if (n3 >= numSlots) {
                        return false;
                    }
                    if (array[n3] == null) {
                        final PhysicalFont slotFont = compositeFont.getSlotFont(n3);
                        char[] glyphToCharMapForFont2 = (char[])hashtable.get(slotFont.handle);
                        if (glyphToCharMapForFont2 == null) {
                            glyphToCharMapForFont2 = getGlyphToCharMapForFont(slotFont);
                        }
                        array[n3] = glyphToCharMapForFont2;
                    }
                }
            }
            else {
                glyphToCharMapForFont = hashtable.get(font2D.handle);
                if (glyphToCharMapForFont == null) {
                    glyphToCharMapForFont = getGlyphToCharMapForFont(font2D);
                    hashtable.put(font2D.handle, glyphToCharMapForFont);
                }
            }
        }
        final char[] array2 = new char[numGlyphs];
        if (compositeFont != null) {
            for (int j = 0; j < numGlyphs; ++j) {
                final int n4 = glyphCodes[j];
                final char[] array3 = array[n4 >>> 24];
                final int n5 = n4 & 0xFFFFFF;
                if (array3 == null) {
                    return false;
                }
                char c;
                if (n5 == 65535) {
                    c = '\n';
                }
                else {
                    if (n5 < 0 || n5 >= array3.length) {
                        return false;
                    }
                    c = array3[n5];
                }
                if (c == '\uffff') {
                    return false;
                }
                array2[j] = c;
            }
        }
        else {
            for (int k = 0; k < numGlyphs; ++k) {
                final int n6 = glyphCodes[k];
                char c2;
                if (n6 == 65535) {
                    c2 = '\n';
                }
                else {
                    if (n6 < 0 || n6 >= glyphToCharMapForFont.length) {
                        return false;
                    }
                    c2 = glyphToCharMapForFont[n6];
                }
                if (c2 == '\uffff') {
                    return false;
                }
                array2[k] = c2;
            }
        }
        final FontRenderContext fontRenderContext = glyphVector.getFontRenderContext();
        final GlyphVector glyphVector2 = font.createGlyphVector(fontRenderContext, array2);
        if (glyphVector2.getNumGlyphs() != numGlyphs) {
            return this.printGlyphVector(glyphVector, n, n2);
        }
        final int[] glyphCodes2 = glyphVector2.getGlyphCodes(0, numGlyphs, null);
        for (int l = 0; l < numGlyphs; ++l) {
            if (glyphCodes[l] != glyphCodes2[l]) {
                return this.printGlyphVector(glyphVector, n, n2);
            }
        }
        final FontRenderContext fontRenderContext2 = this.getFontRenderContext();
        int equals = fontRenderContext.equals(fontRenderContext2) ? 1 : 0;
        if (equals == 0 && fontRenderContext.usesFractionalMetrics() == fontRenderContext2.usesFractionalMetrics()) {
            final AffineTransform transform = fontRenderContext.getTransform();
            final AffineTransform transform2 = this.getTransform();
            final double[] array4 = new double[4];
            final double[] array5 = new double[4];
            transform.getMatrix(array4);
            transform2.getMatrix(array5);
            equals = 1;
            for (int n7 = 0; n7 < 4; ++n7) {
                if (array4[n7] != array5[n7]) {
                    equals = 0;
                    break;
                }
            }
        }
        final String s = new String(array2, 0, numGlyphs);
        final int platformFontCount = this.platformFontCount(font, s);
        if (platformFontCount == 0) {
            return false;
        }
        final float[] glyphPositions = glyphVector.getGlyphPositions(0, numGlyphs, null);
        int n8 = ((layoutFlags & 0x2) == 0x0 || this.samePositions(glyphVector2, glyphCodes2, glyphCodes, glyphPositions)) ? 1 : 0;
        final float n9 = (float)glyphVector.getGlyphPosition(numGlyphs).getX();
        boolean b = false;
        if (font.hasLayoutAttributes() && this.printingGlyphVector && n8 != 0) {
            final Object value = font.getAttributes().get(TextAttribute.TRACKING);
            if (value != null && value instanceof Number && ((Number)value).floatValue() != 0.0f) {
                n8 = 0;
            }
            else if (Math.abs((float)font.getStringBounds(s, fontRenderContext).getWidth() - n9) > 1.0E-5) {
                b = true;
            }
        }
        if (equals != 0 && n8 != 0 && !b) {
            this.drawString(s, n, n2, font, fontRenderContext, 0.0f);
            return true;
        }
        if (platformFontCount == 1 && this.canDrawStringToWidth() && n8 != 0) {
            this.drawString(s, n, n2, font, fontRenderContext, n9);
            return true;
        }
        if (FontUtilities.isComplexText(array2, 0, array2.length)) {
            return this.printGlyphVector(glyphVector, n, n2);
        }
        if (numGlyphs > 10 && this.printGlyphVector(glyphVector, n, n2)) {
            return true;
        }
        for (int n10 = 0; n10 < numGlyphs; ++n10) {
            this.drawString(new String(array2, n10, 1), n + glyphPositions[n10 * 2], n2 + glyphPositions[n10 * 2 + 1], font, fontRenderContext, 0.0f);
        }
        return true;
    }
    
    private boolean samePositions(final GlyphVector glyphVector, final int[] array, final int[] array2, final float[] array3) {
        final int numGlyphs = glyphVector.getNumGlyphs();
        final float[] glyphPositions = glyphVector.getGlyphPositions(0, numGlyphs, null);
        if (numGlyphs != array.length || array2.length != array.length || array3.length != glyphPositions.length) {
            return false;
        }
        for (int i = 0; i < numGlyphs; ++i) {
            if (array[i] != array2[i] || glyphPositions[i] != array3[i]) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean canDrawStringToWidth() {
        return false;
    }
    
    private static char[] getGlyphToCharMapForFont(final Font2D font2D) {
        final int numGlyphs = font2D.getNumGlyphs();
        final int missingGlyphCode = font2D.getMissingGlyphCode();
        final char[] array = new char[numGlyphs];
        for (int i = 0; i < numGlyphs; ++i) {
            array[i] = '\uffff';
        }
        for (char c = '\0'; c < '\uffff'; ++c) {
            if (c < '\ud800' || c > '\udfff') {
                final int charToGlyph = font2D.charToGlyph(c);
                if (charToGlyph != missingGlyphCode && charToGlyph >= 0 && charToGlyph < numGlyphs && array[charToGlyph] == '\uffff') {
                    array[charToGlyph] = c;
                }
            }
        }
        return array;
    }
    
    @Override
    public void draw(final Shape shape) {
        this.fill(this.getStroke().createStrokedShape(shape));
    }
    
    @Override
    public void fill(final Shape shape) {
        final Paint paint = this.getPaint();
        try {
            this.fill(shape, (Color)paint);
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException("Expected a Color instance");
        }
    }
    
    public void fill(final Shape shape, final Color color) {
        final AffineTransform transform = this.getTransform();
        if (this.getClip() != null) {
            this.deviceClip(this.getClip().getPathIterator(transform));
        }
        this.deviceFill(shape.getPathIterator(transform), color);
    }
    
    protected abstract void deviceFill(final PathIterator p0, final Color p1);
    
    protected abstract void deviceClip(final PathIterator p0);
    
    protected abstract void deviceFrameRect(final int p0, final int p1, final int p2, final int p3, final Color p4);
    
    protected abstract void deviceDrawLine(final int p0, final int p1, final int p2, final int p3, final Color p4);
    
    protected abstract void deviceFillRect(final int p0, final int p1, final int p2, final int p3, final Color p4);
    
    protected BufferedImage getBufferedImage(final Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        if (image instanceof ToolkitImage) {
            return ((ToolkitImage)image).getBufferedImage();
        }
        if (image instanceof VolatileImage) {
            return ((VolatileImage)image).getSnapshot();
        }
        return null;
    }
    
    protected boolean hasTransparentPixels(final BufferedImage bufferedImage) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        boolean b = colorModel == null || colorModel.getTransparency() != 1;
        if (b && bufferedImage != null && (bufferedImage.getType() == 2 || bufferedImage.getType() == 3)) {
            final DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();
            final SampleModel sampleModel = bufferedImage.getRaster().getSampleModel();
            if (dataBuffer instanceof DataBufferInt && sampleModel instanceof SinglePixelPackedSampleModel) {
                final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
                final int[] stealData = SunWritableRaster.stealData((DataBufferInt)dataBuffer, 0);
                final int minX = bufferedImage.getMinX();
                final int minY = bufferedImage.getMinY();
                final int width = bufferedImage.getWidth();
                final int height = bufferedImage.getHeight();
                final int scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
                boolean b2 = false;
                for (int i = minY; i < minY + height; ++i) {
                    final int n = i * scanlineStride;
                    for (int j = minX; j < minX + width; ++j) {
                        if ((stealData[n + j] & 0xFF000000) != 0xFF000000) {
                            b2 = true;
                            break;
                        }
                    }
                    if (b2) {
                        break;
                    }
                }
                if (!b2) {
                    b = false;
                }
            }
        }
        return b;
    }
    
    protected boolean isBitmaskTransparency(final BufferedImage bufferedImage) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        return colorModel != null && colorModel.getTransparency() == 2;
    }
    
    protected boolean drawBitmaskImage(final BufferedImage bufferedImage, final AffineTransform affineTransform, final Color color, final int n, final int n2, final int n3, final int n4) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        if (!(colorModel instanceof IndexColorModel)) {
            return false;
        }
        final IndexColorModel indexColorModel = (IndexColorModel)colorModel;
        if (colorModel.getTransparency() != 2) {
            return false;
        }
        if (color != null && color.getAlpha() < 128) {
            return false;
        }
        if ((affineTransform.getType() & 0xFFFFFFF4) != 0x0) {
            return false;
        }
        if ((this.getTransform().getType() & 0xFFFFFFF4) != 0x0) {
            return false;
        }
        final WritableRaster raster = bufferedImage.getRaster();
        final int transparentPixel = indexColorModel.getTransparentPixel();
        final byte[] array = new byte[indexColorModel.getMapSize()];
        indexColorModel.getAlphas(array);
        if (transparentPixel >= 0) {
            array[transparentPixel] = 0;
        }
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        if (n > width || n2 > height) {
            return false;
        }
        int n5;
        int n6;
        if (n + n3 > width) {
            n5 = width;
            n6 = n5 - n;
        }
        else {
            n5 = n + n3;
            n6 = n3;
        }
        int n7;
        if (n2 + n4 > height) {
            n7 = height;
        }
        else {
            n7 = n2 + n4;
        }
        final int[] array2 = new int[n6];
        for (int i = n2; i < n7; ++i) {
            int n8 = -1;
            raster.getPixels(n, i, n6, 1, array2);
            for (int j = n; j < n5; ++j) {
                if (array[array2[j - n]] == 0) {
                    if (n8 >= 0) {
                        final BufferedImage subimage = bufferedImage.getSubimage(n8, i, j - n8, 1);
                        affineTransform.translate(n8, i);
                        this.drawImageToPlatform(subimage, affineTransform, color, 0, 0, j - n8, 1, true);
                        affineTransform.translate(-n8, -i);
                        n8 = -1;
                    }
                }
                else if (n8 < 0) {
                    n8 = j;
                }
            }
            if (n8 >= 0) {
                final BufferedImage subimage2 = bufferedImage.getSubimage(n8, i, n5 - n8, 1);
                affineTransform.translate(n8, i);
                this.drawImageToPlatform(subimage2, affineTransform, color, 0, 0, n5 - n8, 1, true);
                affineTransform.translate(-n8, -i);
            }
        }
        return true;
    }
    
    protected abstract boolean drawImageToPlatform(final Image p0, final AffineTransform p1, final Color p2, final int p3, final int p4, final int p5, final int p6, final boolean p7);
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, null, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, n3, n4, null, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        return width >= 0 && height >= 0 && this.drawImage(image, n, n2, width, height, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        return width >= 0 && height >= 0 && this.drawImage(image, n, n2, n + n3, n2 + n4, 0, 0, width, height, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, null, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, int n5, int n6, int n7, int n8, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (width < 0 || height < 0) {
            return true;
        }
        final int n9 = n7 - n5;
        final int n10 = n8 - n6;
        final float n11 = (n3 - n) / (float)n9;
        final float n12 = (n4 - n2) / (float)n10;
        final AffineTransform affineTransform = new AffineTransform(n11, 0.0f, 0.0f, n12, n - n5 * n11, n2 - n6 * n12);
        if (n7 < n5) {
            final int n13 = n5;
            n5 = n7;
            n7 = n13;
        }
        if (n8 < n6) {
            final int n14 = n6;
            n6 = n8;
            n8 = n14;
        }
        if (n5 < 0) {
            n5 = 0;
        }
        else if (n5 > width) {
            n5 = width;
        }
        if (n7 < 0) {
            n7 = 0;
        }
        else if (n7 > width) {
            n7 = width;
        }
        if (n6 < 0) {
            n6 = 0;
        }
        else if (n6 > height) {
            n6 = height;
        }
        if (n8 < 0) {
            n8 = 0;
        }
        else if (n8 > height) {
            n8 = height;
        }
        final int n15 = n7 - n5;
        final int n16 = n8 - n6;
        return n15 <= 0 || n16 <= 0 || this.drawImageToPlatform(image, affineTransform, color, n5, n6, n15, n16, false);
    }
    
    @Override
    public boolean drawImage(final Image image, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        return width >= 0 && height >= 0 && this.drawImageToPlatform(image, affineTransform, null, 0, 0, width, height, false);
    }
    
    @Override
    public void drawImage(BufferedImage filter, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (filter == null) {
            return;
        }
        final int width = filter.getWidth(null);
        final int height = filter.getHeight(null);
        if (bufferedImageOp != null) {
            filter = bufferedImageOp.filter(filter, null);
        }
        if (width <= 0 || height <= 0) {
            return;
        }
        this.drawImageToPlatform(filter, new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, (float)n, (float)n2), null, 0, 0, width, height, false);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage renderedImage, final AffineTransform affineTransform) {
        if (renderedImage == null) {
            return;
        }
        final int width = renderedImage.getWidth();
        final int height = renderedImage.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        BufferedImage bufferedImage;
        if (renderedImage instanceof BufferedImage) {
            bufferedImage = (BufferedImage)renderedImage;
        }
        else {
            bufferedImage = new BufferedImage(width, height, 2);
            bufferedImage.createGraphics().drawRenderedImage(renderedImage, affineTransform);
        }
        this.drawImageToPlatform(bufferedImage, affineTransform, null, 0, 0, width, height, false);
    }
    
    static {
        PathGraphics.fontMapRef = new SoftReference<Hashtable<Font2DHandle, Object>>(null);
    }
}
