package sun.java2d.loops;

import sun.java2d.pipe.SpanIterator;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class SetFillSpansANY extends FillSpans
{
    SetFillSpansANY() {
        super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void FillSpans(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final SpanIterator spanIterator) {
        final PixelWriter solidPixelWriter = GeneralRenderer.createSolidPixelWriter(sunGraphics2D, surfaceData);
        final int[] array = new int[4];
        while (spanIterator.nextSpan(array)) {
            GeneralRenderer.doSetRect(surfaceData, solidPixelWriter, array[0], array[1], array[2], array[3]);
        }
    }
}
