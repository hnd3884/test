package sun.font;

import java.awt.font.GlyphJustificationInfo;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

public interface TextLineComponent
{
    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int UNCHANGED = 2;
    
    CoreMetrics getCoreMetrics();
    
    void draw(final Graphics2D p0, final float p1, final float p2);
    
    Rectangle2D getCharVisualBounds(final int p0);
    
    Rectangle2D getVisualBounds();
    
    float getAdvance();
    
    Shape getOutline(final float p0, final float p1);
    
    int getNumCharacters();
    
    float getCharX(final int p0);
    
    float getCharY(final int p0);
    
    float getCharAdvance(final int p0);
    
    boolean caretAtOffsetIsValid(final int p0);
    
    int getLineBreakIndex(final int p0, final float p1);
    
    float getAdvanceBetween(final int p0, final int p1);
    
    Rectangle2D getLogicalBounds();
    
    Rectangle2D getItalicBounds();
    
    AffineTransform getBaselineTransform();
    
    boolean isSimple();
    
    Rectangle getPixelBounds(final FontRenderContext p0, final float p1, final float p2);
    
    TextLineComponent getSubset(final int p0, final int p1, final int p2);
    
    int getNumJustificationInfos();
    
    void getJustificationInfos(final GlyphJustificationInfo[] p0, final int p1, final int p2, final int p3);
    
    TextLineComponent applyJustificationDeltas(final float[] p0, final int p1, final boolean[] p2);
}
