package sun.print;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.print.PrinterException;
import java.awt.Paint;
import sun.awt.image.ByteComponentRaster;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Image;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.awt.Graphics2D;

class PSPathGraphics extends PathGraphics
{
    private static final int DEFAULT_USER_RES = 72;
    
    PSPathGraphics(final Graphics2D graphics2D, final PrinterJob printerJob, final Printable printable, final PageFormat pageFormat, final int n, final boolean b) {
        super(graphics2D, printerJob, printable, pageFormat, n, b);
    }
    
    @Override
    public Graphics create() {
        return new PSPathGraphics((Graphics2D)this.getDelegate().create(), this.getPrinterJob(), this.getPrintable(), this.getPageFormat(), this.getPageIndex(), this.canDoRedraws());
    }
    
    @Override
    public void fill(final Shape shape, final Color color) {
        this.deviceFill(shape.getPathIterator(new AffineTransform()), color);
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
    protected boolean canDrawStringToWidth() {
        return true;
    }
    
    @Override
    protected int platformFontCount(final Font font, final String s) {
        return ((PSPrinterJob)this.getPrinterJob()).platformFontCount(font, s);
    }
    
    @Override
    protected void drawString(final String s, final float n, final float n2, final Font font, final FontRenderContext fontRenderContext, final float n3) {
        if (s.length() == 0) {
            return;
        }
        if (font.hasLayoutAttributes() && !this.printingGlyphVector) {
            new TextLayout(s, font, fontRenderContext).draw(this, n, n2);
            return;
        }
        Font font2 = this.getFont();
        if (!font2.equals(font)) {
            this.setFont(font);
        }
        else {
            font2 = null;
        }
        boolean textOut = false;
        float n4 = 0.0f;
        float n5 = 0.0f;
        int transformed = this.getFont().isTransformed() ? 1 : 0;
        if (transformed != 0) {
            final AffineTransform transform = this.getFont().getTransform();
            if (transform.getType() == 1) {
                n4 = (float)transform.getTranslateX();
                n5 = (float)transform.getTranslateY();
                if (Math.abs(n4) < 1.0E-5) {
                    n4 = 0.0f;
                }
                if (Math.abs(n5) < 1.0E-5) {
                    n5 = 0.0f;
                }
                transformed = 0;
            }
        }
        final boolean b = transformed == 0;
        if (!PSPrinterJob.shapeTextProp && b) {
            final PSPrinterJob psPrinterJob = (PSPrinterJob)this.getPrinterJob();
            if (psPrinterJob.setFont(this.getFont())) {
                try {
                    psPrinterJob.setColor((Color)this.getPaint());
                }
                catch (final ClassCastException ex) {
                    if (font2 != null) {
                        this.setFont(font2);
                    }
                    throw new IllegalArgumentException("Expected a Color instance");
                }
                psPrinterJob.setTransform(this.getTransform());
                psPrinterJob.setClip(this.getClip());
                textOut = psPrinterJob.textOut(this, s, n + n4, n2 + n5, font, fontRenderContext, n3);
            }
        }
        if (!textOut) {
            if (font2 != null) {
                this.setFont(font2);
                font2 = null;
            }
            super.drawString(s, n, n2, font, fontRenderContext, n3);
        }
        if (font2 != null) {
            this.setFont(font2);
        }
    }
    
    @Override
    protected boolean drawImageToPlatform(final Image image, AffineTransform affineTransform, Color white, final int n, final int n2, final int n3, final int n4, final boolean b) {
        final BufferedImage bufferedImage = this.getBufferedImage(image);
        if (bufferedImage == null) {
            return true;
        }
        final PSPrinterJob psPrinterJob = (PSPrinterJob)this.getPrinterJob();
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
        final double xRes = psPrinterJob.getXRes();
        final double yRes = psPrinterJob.getYRes();
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
            final Shape transformedShape = affineTransform2.createTransformedShape(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
            final Rectangle2D bounds2D = transformedShape.getBounds2D();
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
                    psPrinterJob.saveState(this.getTransform(), this.getClip(), bounds2D2, n15, n15);
                    return true;
                }
                final BufferedImage bufferedImage2 = new BufferedImage((int)bounds2D.getWidth(), (int)bounds2D.getHeight(), 5);
                final Graphics2D graphics = bufferedImage2.createGraphics();
                graphics.clipRect(0, 0, bufferedImage2.getWidth(), bufferedImage2.getHeight());
                graphics.translate(-bounds2D.getX(), -bounds2D.getY());
                graphics.transform(affineTransform2);
                if (white == null) {
                    white = Color.white;
                }
                graphics.drawImage(bufferedImage, n, n2, n + n3, n2 + n4, n, n2, n + n3, n2 + n4, white, null);
                final Shape clip = this.getClip();
                final Shape transformedShape2 = this.getTransform().createTransformedShape(clip);
                final Area clip2 = new Area(AffineTransform.getScaleInstance(distance, distance2).createTransformedShape(transformedShape));
                clip2.intersect(new Area(transformedShape2));
                psPrinterJob.setClip(clip2);
                final Rectangle2D.Float float4 = new Rectangle2D.Float((float)(bounds2D.getX() * distance), (float)(bounds2D.getY() * distance2), (float)(bounds2D.getWidth() * distance), (float)(bounds2D.getHeight() * distance2));
                psPrinterJob.drawImageBGR(((ByteComponentRaster)bufferedImage2.getRaster()).getDataStorage(), float4.x, float4.y, (float)Math.rint(float4.width + 0.5), (float)Math.rint(float4.height + 0.5), 0.0f, 0.0f, (float)bufferedImage2.getWidth(), (float)bufferedImage2.getHeight(), bufferedImage2.getWidth(), bufferedImage2.getHeight());
                psPrinterJob.setClip(this.getTransform().createTransformedShape(clip));
                graphics.dispose();
            }
        }
        return true;
    }
    
    @Override
    public void redrawRegion(final Rectangle2D rectangle2D, final double n, final double n2, final Shape shape, final AffineTransform affineTransform) throws PrinterException {
        final PSPrinterJob psPrinterJob = (PSPrinterJob)this.getPrinterJob();
        final Printable printable = this.getPrintable();
        final PageFormat pageFormat = this.getPageFormat();
        final int pageIndex = this.getPageIndex();
        final BufferedImage bufferedImage = new BufferedImage((int)rectangle2D.getWidth(), (int)rectangle2D.getHeight(), 5);
        final Graphics2D graphics = bufferedImage.createGraphics();
        final ProxyGraphics2D proxyGraphics2D = new ProxyGraphics2D(graphics, psPrinterJob);
        proxyGraphics2D.setColor(Color.white);
        proxyGraphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        proxyGraphics2D.clipRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        proxyGraphics2D.translate(-rectangle2D.getX(), -rectangle2D.getY());
        proxyGraphics2D.scale((float)(psPrinterJob.getXRes() / n) / 72.0f, (float)(psPrinterJob.getYRes() / n2) / 72.0f);
        proxyGraphics2D.translate(-psPrinterJob.getPhysicalPrintableX(pageFormat.getPaper()) / psPrinterJob.getXRes() * 72.0, -psPrinterJob.getPhysicalPrintableY(pageFormat.getPaper()) / psPrinterJob.getYRes() * 72.0);
        proxyGraphics2D.transform(new AffineTransform(this.getPageFormat().getMatrix()));
        proxyGraphics2D.setPaint(Color.black);
        printable.print(proxyGraphics2D, pageFormat, pageIndex);
        graphics.dispose();
        psPrinterJob.setClip(affineTransform.createTransformedShape(shape));
        final Rectangle2D.Float float1 = new Rectangle2D.Float((float)(rectangle2D.getX() * n), (float)(rectangle2D.getY() * n2), (float)(rectangle2D.getWidth() * n), (float)(rectangle2D.getHeight() * n2));
        psPrinterJob.drawImageBGR(((ByteComponentRaster)bufferedImage.getRaster()).getDataStorage(), float1.x, float1.y, float1.width, float1.height, 0.0f, 0.0f, (float)bufferedImage.getWidth(), (float)bufferedImage.getHeight(), bufferedImage.getWidth(), bufferedImage.getHeight());
    }
    
    @Override
    protected void deviceFill(final PathIterator pathIterator, final Color color) {
        ((PSPrinterJob)this.getPrinterJob()).deviceFill(pathIterator, color, this.getTransform(), this.getClip());
    }
    
    @Override
    protected void deviceFrameRect(final int n, final int n2, final int n3, final int n4, final Color color) {
        this.draw(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    protected void deviceDrawLine(final int n, final int n2, final int n3, final int n4, final Color color) {
        this.draw(new Line2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    protected void deviceFillRect(final int n, final int n2, final int n3, final int n4, final Color color) {
        this.fill(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    protected void deviceClip(final PathIterator pathIterator) {
    }
}
