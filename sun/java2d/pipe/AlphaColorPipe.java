package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class AlphaColorPipe implements CompositePipe, ParallelogramPipe
{
    @Override
    public Object startSequence(final SunGraphics2D sunGraphics2D, final Shape shape, final Rectangle rectangle, final int[] array) {
        return sunGraphics2D;
    }
    
    @Override
    public boolean needTile(final Object o, final int n, final int n2, final int n3, final int n4) {
        return true;
    }
    
    @Override
    public void renderPathTile(final Object o, final byte[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final SunGraphics2D sunGraphics2D = (SunGraphics2D)o;
        sunGraphics2D.alphafill.MaskFill(sunGraphics2D, sunGraphics2D.getSurfaceData(), sunGraphics2D.composite, n3, n4, n5, n6, array, n, n2);
    }
    
    @Override
    public void skipTile(final Object o, final int n, final int n2) {
    }
    
    @Override
    public void endSequence(final Object o) {
    }
    
    @Override
    public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
        sunGraphics2D.alphafill.FillAAPgram(sunGraphics2D, sunGraphics2D.getSurfaceData(), sunGraphics2D.composite, n5, n6, n7, n8, n9, n10);
    }
    
    @Override
    public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        sunGraphics2D.alphafill.DrawAAPgram(sunGraphics2D, sunGraphics2D.getSurfaceData(), sunGraphics2D.composite, n5, n6, n7, n8, n9, n10, n11, n12);
    }
}
