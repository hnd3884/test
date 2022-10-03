package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class SpanClipRenderer implements CompositePipe
{
    CompositePipe outpipe;
    static Class RegionClass;
    static Class RegionIteratorClass;
    
    static native void initIDs(final Class p0, final Class p1);
    
    public SpanClipRenderer(final CompositePipe outpipe) {
        this.outpipe = outpipe;
    }
    
    @Override
    public Object startSequence(final SunGraphics2D sunGraphics2D, final Shape shape, final Rectangle rectangle, final int[] array) {
        return new SCRcontext(sunGraphics2D.clipRegion.getIterator(), this.outpipe.startSequence(sunGraphics2D, shape, rectangle, array));
    }
    
    @Override
    public boolean needTile(final Object o, final int n, final int n2, final int n3, final int n4) {
        return this.outpipe.needTile(((SCRcontext)o).outcontext, n, n2, n3, n4);
    }
    
    public void renderPathTile(final Object o, final byte[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final ShapeSpanIterator shapeSpanIterator) {
        this.renderPathTile(o, array, n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void renderPathTile(final Object o, byte[] tile, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        final SCRcontext scRcontext = (SCRcontext)o;
        final RegionIterator copy = scRcontext.iterator.createCopy();
        final int[] band = scRcontext.band;
        band[0] = n3;
        band[1] = n4;
        band[2] = n3 + n5;
        band[3] = n4 + n6;
        if (tile == null) {
            final int n7 = n5 * n6;
            tile = scRcontext.tile;
            if (tile != null && tile.length < n7) {
                tile = null;
            }
            if (tile == null) {
                tile = new byte[n7];
                scRcontext.tile = tile;
            }
            n = 0;
            n2 = n5;
            this.fillTile(copy, tile, n, n2, band);
        }
        else {
            this.eraseTile(copy, tile, n, n2, band);
        }
        if (band[2] > band[0] && band[3] > band[1]) {
            n += (band[1] - n4) * n2 + (band[0] - n3);
            this.outpipe.renderPathTile(scRcontext.outcontext, tile, n, n2, band[0], band[1], band[2] - band[0], band[3] - band[1]);
        }
    }
    
    public native void fillTile(final RegionIterator p0, final byte[] p1, final int p2, final int p3, final int[] p4);
    
    public native void eraseTile(final RegionIterator p0, final byte[] p1, final int p2, final int p3, final int[] p4);
    
    @Override
    public void skipTile(final Object o, final int n, final int n2) {
        this.outpipe.skipTile(((SCRcontext)o).outcontext, n, n2);
    }
    
    @Override
    public void endSequence(final Object o) {
        this.outpipe.endSequence(((SCRcontext)o).outcontext);
    }
    
    static {
        SpanClipRenderer.RegionClass = Region.class;
        SpanClipRenderer.RegionIteratorClass = RegionIterator.class;
        initIDs(SpanClipRenderer.RegionClass, SpanClipRenderer.RegionIteratorClass);
    }
    
    class SCRcontext
    {
        RegionIterator iterator;
        Object outcontext;
        int[] band;
        byte[] tile;
        
        public SCRcontext(final RegionIterator iterator, final Object outcontext) {
            this.iterator = iterator;
            this.outcontext = outcontext;
            this.band = new int[4];
        }
    }
}
