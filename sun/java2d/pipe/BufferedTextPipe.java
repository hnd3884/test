package sun.java2d.pipe;

import java.awt.Composite;
import java.awt.AlphaComposite;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class BufferedTextPipe extends GlyphListPipe
{
    private static final int BYTES_PER_GLYPH_IMAGE = 8;
    private static final int BYTES_PER_GLYPH_POSITION = 8;
    private static final int OFFSET_CONTRAST = 8;
    private static final int OFFSET_RGBORDER = 2;
    private static final int OFFSET_SUBPIXPOS = 1;
    private static final int OFFSET_POSITIONS = 0;
    protected final RenderQueue rq;
    
    private static int createPackedParams(final SunGraphics2D sunGraphics2D, final GlyphList list) {
        return (list.usePositions() ? 1 : 0) << 0 | (list.isSubPixPos() ? 1 : 0) << 1 | (list.isRGBOrder() ? 1 : 0) << 2 | (sunGraphics2D.lcdTextContrast & 0xFF) << 8;
    }
    
    protected BufferedTextPipe(final RenderQueue rq) {
        this.rq = rq;
    }
    
    @Override
    protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list) {
        Composite composite = sunGraphics2D.composite;
        if (composite == AlphaComposite.Src) {
            composite = AlphaComposite.SrcOver;
        }
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D, composite);
            this.enqueueGlyphList(sunGraphics2D, list);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    private void enqueueGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list) {
        final RenderBuffer buffer = this.rq.getBuffer();
        final int numGlyphs = list.getNumGlyphs();
        final int n = 24 + numGlyphs * 8 + (list.usePositions() ? (numGlyphs * 8) : 0);
        final long[] images = list.getImages();
        final float n2 = list.getX() + 0.5f;
        final float n3 = list.getY() + 0.5f;
        this.rq.addReference(list.getStrike());
        if (n <= buffer.capacity()) {
            if (n > buffer.remaining()) {
                this.rq.flushNow();
            }
            this.rq.ensureAlignment(20);
            buffer.putInt(40);
            buffer.putInt(numGlyphs);
            buffer.putInt(createPackedParams(sunGraphics2D, list));
            buffer.putFloat(n2);
            buffer.putFloat(n3);
            buffer.put(images, 0, numGlyphs);
            if (list.usePositions()) {
                buffer.put(list.getPositions(), 0, 2 * numGlyphs);
            }
        }
        else {
            this.rq.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    BufferedTextPipe.this.drawGlyphList(numGlyphs, list.usePositions(), list.isSubPixPos(), list.isRGBOrder(), sunGraphics2D.lcdTextContrast, n2, n3, images, list.getPositions());
                }
            });
        }
    }
    
    protected abstract void drawGlyphList(final int p0, final boolean p1, final boolean p2, final boolean p3, final int p4, final float p5, final float p6, final long[] p7, final float[] p8);
    
    protected abstract void validateContext(final SunGraphics2D p0, final Composite p1);
}
