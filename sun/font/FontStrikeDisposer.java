package sun.font;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

class FontStrikeDisposer implements DisposerRecord, Disposer.PollDisposable
{
    ConcurrentHashMap<FontStrikeDesc, Reference> strikeCache;
    FontStrikeDesc desc;
    long[] longGlyphImages;
    int[] intGlyphImages;
    int[][] segIntGlyphImages;
    long[][] segLongGlyphImages;
    long pScalerContext;
    boolean disposed;
    boolean comp;
    
    public FontStrikeDisposer(final Font2D font2D, final FontStrikeDesc desc, final long pScalerContext, final int[] intGlyphImages) {
        this.pScalerContext = 0L;
        this.disposed = false;
        this.comp = false;
        this.strikeCache = font2D.strikeCache;
        this.desc = desc;
        this.pScalerContext = pScalerContext;
        this.intGlyphImages = intGlyphImages;
    }
    
    public FontStrikeDisposer(final Font2D font2D, final FontStrikeDesc desc, final long pScalerContext, final long[] longGlyphImages) {
        this.pScalerContext = 0L;
        this.disposed = false;
        this.comp = false;
        this.strikeCache = font2D.strikeCache;
        this.desc = desc;
        this.pScalerContext = pScalerContext;
        this.longGlyphImages = longGlyphImages;
    }
    
    public FontStrikeDisposer(final Font2D font2D, final FontStrikeDesc desc, final long pScalerContext) {
        this.pScalerContext = 0L;
        this.disposed = false;
        this.comp = false;
        this.strikeCache = font2D.strikeCache;
        this.desc = desc;
        this.pScalerContext = pScalerContext;
    }
    
    public FontStrikeDisposer(final Font2D font2D, final FontStrikeDesc desc) {
        this.pScalerContext = 0L;
        this.disposed = false;
        this.comp = false;
        this.strikeCache = font2D.strikeCache;
        this.desc = desc;
        this.comp = true;
    }
    
    @Override
    public synchronized void dispose() {
        if (!this.disposed) {
            final Reference reference = this.strikeCache.get(this.desc);
            if (reference != null && reference.get() == null) {
                this.strikeCache.remove(this.desc);
            }
            StrikeCache.disposeStrike(this);
            this.disposed = true;
        }
    }
}
