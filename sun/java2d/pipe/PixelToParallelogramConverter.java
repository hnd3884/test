package sun.java2d.pipe;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.BasicStroke;
import sun.java2d.SunGraphics2D;

public class PixelToParallelogramConverter extends PixelToShapeConverter implements ShapeDrawPipe
{
    ParallelogramPipe outrenderer;
    double minPenSize;
    double normPosition;
    double normRoundingBias;
    boolean adjustfill;
    
    public PixelToParallelogramConverter(final ShapeDrawPipe shapeDrawPipe, final ParallelogramPipe outrenderer, final double minPenSize, final double normPosition, final boolean adjustfill) {
        super(shapeDrawPipe);
        this.outrenderer = outrenderer;
        this.minPenSize = minPenSize;
        this.normPosition = normPosition;
        this.normRoundingBias = 0.5 - normPosition;
        this.adjustfill = adjustfill;
    }
    
    @Override
    public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (!this.drawGeneralLine(sunGraphics2D, n, n2, n3, n4)) {
            super.drawLine(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (n3 >= 0 && n4 >= 0) {
            if (sunGraphics2D.strokeState < 3) {
                final BasicStroke basicStroke = (BasicStroke)sunGraphics2D.stroke;
                if (n3 <= 0 || n4 <= 0) {
                    this.drawLine(sunGraphics2D, n, n2, n + n3, n2 + n4);
                    return;
                }
                if (basicStroke.getLineJoin() == 0 && basicStroke.getDashArray() == null) {
                    this.drawRectangle(sunGraphics2D, n, n2, n3, n4, basicStroke.getLineWidth());
                    return;
                }
            }
            super.drawRect(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        if (n3 > 0 && n4 > 0) {
            this.fillRectangle(sunGraphics2D, n, n2, n3, n4);
        }
    }
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (sunGraphics2D.strokeState < 3) {
            final BasicStroke basicStroke = (BasicStroke)sunGraphics2D.stroke;
            if (shape instanceof Rectangle2D) {
                if (basicStroke.getLineJoin() == 0 && basicStroke.getDashArray() == null) {
                    final Rectangle2D rectangle2D = (Rectangle2D)shape;
                    final double width = rectangle2D.getWidth();
                    final double height = rectangle2D.getHeight();
                    final double x = rectangle2D.getX();
                    final double y = rectangle2D.getY();
                    if (width >= 0.0 && height >= 0.0) {
                        this.drawRectangle(sunGraphics2D, x, y, width, height, basicStroke.getLineWidth());
                    }
                    return;
                }
            }
            else if (shape instanceof Line2D) {
                final Line2D line2D = (Line2D)shape;
                if (this.drawGeneralLine(sunGraphics2D, line2D.getX1(), line2D.getY1(), line2D.getX2(), line2D.getY2())) {
                    return;
                }
            }
        }
        this.outpipe.draw(sunGraphics2D, shape);
    }
    
    @Override
    public void fill(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (shape instanceof Rectangle2D) {
            final Rectangle2D rectangle2D = (Rectangle2D)shape;
            final double width = rectangle2D.getWidth();
            final double height = rectangle2D.getHeight();
            if (width > 0.0 && height > 0.0) {
                this.fillRectangle(sunGraphics2D, rectangle2D.getX(), rectangle2D.getY(), width, height);
            }
            return;
        }
        this.outpipe.fill(sunGraphics2D, shape);
    }
    
    static double len(final double n, final double n2) {
        return (n == 0.0) ? Math.abs(n2) : ((n2 == 0.0) ? Math.abs(n) : Math.sqrt(n * n + n2 * n2));
    }
    
    double normalize(final double n) {
        return Math.floor(n + this.normRoundingBias) + this.normPosition;
    }
    
    public boolean drawGeneralLine(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4) {
        if (sunGraphics2D.strokeState == 3 || sunGraphics2D.strokeState == 1) {
            return false;
        }
        final BasicStroke basicStroke = (BasicStroke)sunGraphics2D.stroke;
        final int endCap = basicStroke.getEndCap();
        if (endCap == 1 || basicStroke.getDashArray() != null) {
            return false;
        }
        double n5 = basicStroke.getLineWidth();
        double n6 = n3 - n;
        final double n7 = n4 - n2;
        double normalize = 0.0;
        double normalize2 = 0.0;
        double normalize3 = 0.0;
        double normalize4 = 0.0;
        switch (sunGraphics2D.transformState) {
            case 3:
            case 4: {
                final double[] array = { n, n2, n3, n4 };
                sunGraphics2D.transform.transform(array, 0, array, 0, 2);
                normalize = array[0];
                normalize2 = array[1];
                normalize3 = array[2];
                normalize4 = array[3];
                break;
            }
            case 1:
            case 2: {
                final double translateX = sunGraphics2D.transform.getTranslateX();
                final double translateY = sunGraphics2D.transform.getTranslateY();
                normalize = n + translateX;
                normalize2 = n2 + translateY;
                normalize3 = n3 + translateX;
                normalize4 = n4 + translateY;
                break;
            }
            case 0: {
                normalize = n;
                normalize2 = n2;
                normalize3 = n3;
                normalize4 = n4;
                break;
            }
            default: {
                throw new InternalError("unknown TRANSFORM state...");
            }
        }
        if (sunGraphics2D.strokeHint != 2) {
            if (sunGraphics2D.strokeState == 0 && this.outrenderer instanceof PixelDrawPipe) {
                ((PixelDrawPipe)this.outrenderer).drawLine(sunGraphics2D, (int)Math.floor(normalize - sunGraphics2D.transX), (int)Math.floor(normalize2 - sunGraphics2D.transY), (int)Math.floor(normalize3 - sunGraphics2D.transX), (int)Math.floor(normalize4 - sunGraphics2D.transY));
                return true;
            }
            normalize = this.normalize(normalize);
            normalize2 = this.normalize(normalize2);
            normalize3 = this.normalize(normalize3);
            normalize4 = this.normalize(normalize4);
        }
        if (sunGraphics2D.transformState >= 3) {
            double len = len(n6, n7);
            if (len == 0.0) {
                len = (n6 = 1.0);
            }
            final double[] array2 = { n7 / len, -n6 / len };
            sunGraphics2D.transform.deltaTransform(array2, 0, array2, 0, 1);
            n5 *= len(array2[0], array2[1]);
        }
        final double max = Math.max(n5, this.minPenSize);
        double n8 = normalize3 - normalize;
        double n9 = normalize4 - normalize2;
        final double len2 = len(n8, n9);
        double n10;
        double n11;
        if (len2 == 0.0) {
            if (endCap == 0) {
                return true;
            }
            n10 = max;
            n11 = 0.0;
        }
        else {
            n10 = max * n8 / len2;
            n11 = max * n9 / len2;
        }
        double n12 = normalize + n11 / 2.0;
        double n13 = normalize2 - n10 / 2.0;
        if (endCap == 2) {
            n12 -= n10 / 2.0;
            n13 -= n11 / 2.0;
            n8 += n10;
            n9 += n11;
        }
        this.outrenderer.fillParallelogram(sunGraphics2D, n, n2, n3, n4, n12, n13, -n11, n10, n8, n9);
        return true;
    }
    
    public void fillRectangle(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4) {
        final AffineTransform transform = sunGraphics2D.transform;
        final double scaleX = transform.getScaleX();
        final double shearY = transform.getShearY();
        final double shearX = transform.getShearX();
        final double scaleY = transform.getScaleY();
        double n5 = n * scaleX + n2 * shearX + transform.getTranslateX();
        double n6 = n * shearY + n2 * scaleY + transform.getTranslateY();
        double n7 = scaleX * n3;
        double n8 = shearY * n3;
        double n9 = shearX * n4;
        double n10 = scaleY * n4;
        if (this.adjustfill && sunGraphics2D.strokeState < 3 && sunGraphics2D.strokeHint != 2) {
            final double normalize = this.normalize(n5);
            final double normalize2 = this.normalize(n6);
            n7 = this.normalize(n5 + n7) - normalize;
            n8 = this.normalize(n6 + n8) - normalize2;
            n9 = this.normalize(n5 + n9) - normalize;
            n10 = this.normalize(n6 + n10) - normalize2;
            n5 = normalize;
            n6 = normalize2;
        }
        this.outrenderer.fillParallelogram(sunGraphics2D, n, n2, n + n3, n2 + n4, n5, n6, n7, n8, n9, n10);
    }
    
    public void drawRectangle(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5) {
        final AffineTransform transform = sunGraphics2D.transform;
        final double scaleX = transform.getScaleX();
        final double shearY = transform.getShearY();
        final double shearX = transform.getShearX();
        final double scaleY = transform.getScaleY();
        double n6 = n * scaleX + n2 * shearX + transform.getTranslateX();
        double n7 = n * shearY + n2 * scaleY + transform.getTranslateY();
        final double n8 = len(scaleX, shearY) * n5;
        final double n9 = len(shearX, scaleY) * n5;
        double n10 = scaleX * n3;
        double n11 = shearY * n3;
        double n12 = shearX * n4;
        double n13 = scaleY * n4;
        if (sunGraphics2D.strokeState < 3 && sunGraphics2D.strokeHint != 2) {
            final double normalize = this.normalize(n6);
            final double normalize2 = this.normalize(n7);
            n10 = this.normalize(n6 + n10) - normalize;
            n11 = this.normalize(n7 + n11) - normalize2;
            n12 = this.normalize(n6 + n12) - normalize;
            n13 = this.normalize(n7 + n13) - normalize2;
            n6 = normalize;
            n7 = normalize2;
        }
        final double max = Math.max(n8, this.minPenSize);
        final double max2 = Math.max(n9, this.minPenSize);
        final double len = len(n10, n11);
        final double len2 = len(n12, n13);
        if (max >= len || max2 >= len2) {
            this.fillOuterParallelogram(sunGraphics2D, n, n2, n + n3, n2 + n4, n6, n7, n10, n11, n12, n13, len, len2, max, max2);
        }
        else {
            this.outrenderer.drawParallelogram(sunGraphics2D, n, n2, n + n3, n2 + n4, n6, n7, n10, n11, n12, n13, max / len, max2 / len2);
        }
    }
    
    public void fillOuterParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, double n5, double n6, double n7, double n8, double n9, double n10, final double n11, final double n12, final double n13, final double n14) {
        double n15 = n7 / n11;
        double n16 = n8 / n11;
        double n17 = n9 / n12;
        double n18 = n10 / n12;
        if (n11 == 0.0) {
            if (n12 == 0.0) {
                n17 = 0.0;
                n18 = 1.0;
            }
            n15 = n18;
            n16 = -n17;
        }
        else if (n12 == 0.0) {
            n17 = n16;
            n18 = -n15;
        }
        final double n19 = n15 * n13;
        final double n20 = n16 * n13;
        final double n21 = n17 * n14;
        final double n22 = n18 * n14;
        n5 -= (n19 + n21) / 2.0;
        n6 -= (n20 + n22) / 2.0;
        n7 += n19;
        n8 += n20;
        n9 += n21;
        n10 += n22;
        this.outrenderer.fillParallelogram(sunGraphics2D, n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
    }
}
