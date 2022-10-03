package sun.font;

import java.awt.font.GlyphJustificationInfo;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.font.GraphicAttribute;

public final class GraphicComponent implements TextLineComponent, Decoration.Label
{
    public static final float GRAPHIC_LEADING = 2.0f;
    private GraphicAttribute graphic;
    private int graphicCount;
    private int[] charsLtoV;
    private byte[] levels;
    private Rectangle2D visualBounds;
    private float graphicAdvance;
    private AffineTransform baseTx;
    private CoreMetrics cm;
    private Decoration decorator;
    
    public GraphicComponent(final GraphicAttribute graphic, final Decoration decorator, final int[] array, final byte[] array2, final int n, final int n2, final AffineTransform baseTx) {
        this.visualBounds = null;
        if (n2 <= n) {
            throw new IllegalArgumentException("0 or negative length in GraphicComponent");
        }
        this.graphic = graphic;
        this.graphicAdvance = graphic.getAdvance();
        this.decorator = decorator;
        this.cm = createCoreMetrics(graphic);
        this.baseTx = baseTx;
        this.initLocalOrdering(array, array2, n, n2);
    }
    
    private GraphicComponent(final GraphicComponent graphicComponent, int n, int n2, final int n3) {
        this.visualBounds = null;
        this.graphic = graphicComponent.graphic;
        this.graphicAdvance = graphicComponent.graphicAdvance;
        this.decorator = graphicComponent.decorator;
        this.cm = graphicComponent.cm;
        this.baseTx = graphicComponent.baseTx;
        int[] charsLtoV = null;
        byte[] levels = null;
        if (n3 == 2) {
            charsLtoV = graphicComponent.charsLtoV;
            levels = graphicComponent.levels;
        }
        else {
            if (n3 != 0 && n3 != 1) {
                throw new IllegalArgumentException("Invalid direction flag");
            }
            n2 -= n;
            n = 0;
            if (n3 == 1) {
                charsLtoV = new int[n2];
                levels = new byte[n2];
                for (int i = 0; i < n2; ++i) {
                    charsLtoV[i] = n2 - i - 1;
                    levels[i] = 1;
                }
            }
        }
        this.initLocalOrdering(charsLtoV, levels, n, n2);
    }
    
    private void initLocalOrdering(final int[] charsLtoV, final byte[] levels, final int n, final int n2) {
        this.graphicCount = n2 - n;
        if (charsLtoV == null || charsLtoV.length == this.graphicCount) {
            this.charsLtoV = charsLtoV;
        }
        else {
            this.charsLtoV = BidiUtils.createNormalizedMap(charsLtoV, levels, n, n2);
        }
        if (levels == null || levels.length == this.graphicCount) {
            this.levels = levels;
        }
        else {
            System.arraycopy(levels, n, this.levels = new byte[this.graphicCount], 0, this.graphicCount);
        }
    }
    
    @Override
    public boolean isSimple() {
        return false;
    }
    
    @Override
    public Rectangle getPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2) {
        throw new InternalError("do not call if isSimple returns false");
    }
    
    @Override
    public Rectangle2D handleGetVisualBounds() {
        final Rectangle2D bounds = this.graphic.getBounds();
        return new Rectangle2D.Float((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth() + this.graphicAdvance * (this.graphicCount - 1), (float)bounds.getHeight());
    }
    
    @Override
    public CoreMetrics getCoreMetrics() {
        return this.cm;
    }
    
    public static CoreMetrics createCoreMetrics(final GraphicAttribute graphicAttribute) {
        return new CoreMetrics(graphicAttribute.getAscent(), graphicAttribute.getDescent(), 2.0f, graphicAttribute.getAscent() + graphicAttribute.getDescent() + 2.0f, graphicAttribute.getAlignment(), new float[] { 0.0f, -graphicAttribute.getAscent() / 2.0f, -graphicAttribute.getAscent() }, -graphicAttribute.getAscent() / 2.0f, graphicAttribute.getAscent() / 12.0f, graphicAttribute.getDescent() / 3.0f, graphicAttribute.getAscent() / 12.0f, 0.0f, 0.0f);
    }
    
    public float getItalicAngle() {
        return 0.0f;
    }
    
    @Override
    public Rectangle2D getVisualBounds() {
        if (this.visualBounds == null) {
            this.visualBounds = this.decorator.getVisualBounds(this);
        }
        final Rectangle2D.Float float1 = new Rectangle2D.Float();
        float1.setRect(this.visualBounds);
        return float1;
    }
    
    @Override
    public Shape handleGetOutline(final float n, final float n2) {
        final double[] array = { 1.0, 0.0, 0.0, 1.0, n, n2 };
        if (this.graphicCount == 1) {
            return this.graphic.getOutline(new AffineTransform(array));
        }
        final GeneralPath generalPath = new GeneralPath();
        for (int i = 0; i < this.graphicCount; ++i) {
            generalPath.append(this.graphic.getOutline(new AffineTransform(array)), false);
            final double[] array2 = array;
            final int n3 = 4;
            array2[n3] += this.graphicAdvance;
        }
        return generalPath;
    }
    
    @Override
    public AffineTransform getBaselineTransform() {
        return this.baseTx;
    }
    
    @Override
    public Shape getOutline(final float n, final float n2) {
        return this.decorator.getOutline(this, n, n2);
    }
    
    @Override
    public void handleDraw(final Graphics2D graphics2D, float n, final float n2) {
        for (int i = 0; i < this.graphicCount; ++i) {
            this.graphic.draw(graphics2D, n, n2);
            n += this.graphicAdvance;
        }
    }
    
    @Override
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        this.decorator.drawTextAndDecorations(this, graphics2D, n, n2);
    }
    
    @Override
    public Rectangle2D getCharVisualBounds(final int n) {
        return this.decorator.getCharVisualBounds(this, n);
    }
    
    @Override
    public int getNumCharacters() {
        return this.graphicCount;
    }
    
    @Override
    public float getCharX(final int n) {
        return this.graphicAdvance * ((this.charsLtoV == null) ? n : this.charsLtoV[n]);
    }
    
    @Override
    public float getCharY(final int n) {
        return 0.0f;
    }
    
    @Override
    public float getCharAdvance(final int n) {
        return this.graphicAdvance;
    }
    
    @Override
    public boolean caretAtOffsetIsValid(final int n) {
        return true;
    }
    
    @Override
    public Rectangle2D handleGetCharVisualBounds(final int n) {
        final Rectangle2D bounds = this.graphic.getBounds();
        final Rectangle2D.Float float1 = new Rectangle2D.Float();
        float1.setRect(bounds);
        final Rectangle2D.Float float2 = float1;
        float2.x += this.graphicAdvance * n;
        return float1;
    }
    
    @Override
    public int getLineBreakIndex(final int n, final float n2) {
        int n3 = (int)(n2 / this.graphicAdvance);
        if (n3 > this.graphicCount - n) {
            n3 = this.graphicCount - n;
        }
        return n3;
    }
    
    @Override
    public float getAdvanceBetween(final int n, final int n2) {
        return this.graphicAdvance * (n2 - n);
    }
    
    @Override
    public Rectangle2D getLogicalBounds() {
        final float n = 0.0f;
        final float n2 = -this.cm.ascent;
        return new Rectangle2D.Float(n, n2, this.graphicAdvance * this.graphicCount, this.cm.descent - n2);
    }
    
    @Override
    public float getAdvance() {
        return this.graphicAdvance * this.graphicCount;
    }
    
    @Override
    public Rectangle2D getItalicBounds() {
        return this.getLogicalBounds();
    }
    
    @Override
    public TextLineComponent getSubset(final int n, final int n2, final int n3) {
        if (n < 0 || n2 > this.graphicCount || n >= n2) {
            throw new IllegalArgumentException("Invalid range.  start=" + n + "; limit=" + n2);
        }
        if (n == 0 && n2 == this.graphicCount && n3 == 2) {
            return this;
        }
        return new GraphicComponent(this, n, n2, n3);
    }
    
    @Override
    public String toString() {
        return "[graphic=" + this.graphic + ":count=" + this.getNumCharacters() + "]";
    }
    
    @Override
    public int getNumJustificationInfos() {
        return 0;
    }
    
    @Override
    public void getJustificationInfos(final GlyphJustificationInfo[] array, final int n, final int n2, final int n3) {
    }
    
    @Override
    public TextLineComponent applyJustificationDeltas(final float[] array, final int n, final boolean[] array2) {
        return this;
    }
}
