package sun.font;

import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PhysicalStrike extends FontStrike
{
    static final long INTMASK = 4294967295L;
    static boolean longAddresses;
    private PhysicalFont physicalFont;
    protected CharToGlyphMapper mapper;
    protected long pScalerContext;
    protected long[] longGlyphImages;
    protected int[] intGlyphImages;
    ConcurrentHashMap<Integer, Point2D.Float> glyphPointMapCache;
    protected boolean getImageWithAdvance;
    protected static final int complexTX = 124;
    
    PhysicalStrike(final PhysicalFont physicalFont, final FontStrikeDesc desc) {
        this.physicalFont = physicalFont;
        this.desc = desc;
    }
    
    protected PhysicalStrike() {
    }
    
    @Override
    public int getNumGlyphs() {
        return this.physicalFont.getNumGlyphs();
    }
    
    @Override
    StrikeMetrics getFontMetrics() {
        if (this.strikeMetrics == null) {
            this.strikeMetrics = this.physicalFont.getFontMetrics(this.pScalerContext);
        }
        return this.strikeMetrics;
    }
    
    @Override
    float getCodePointAdvance(final int n) {
        return this.getGlyphAdvance(this.physicalFont.getMapper().charToGlyph(n));
    }
    
    @Override
    Point2D.Float getCharMetrics(final char c) {
        return this.getGlyphMetrics(this.physicalFont.getMapper().charToGlyph(c));
    }
    
    int getSlot0GlyphImagePtrs(final int[] array, final long[] array2, final int n) {
        return 0;
    }
    
    Point2D.Float getGlyphPoint(final int n, final int n2) {
        Point2D.Float glyphPoint = null;
        final Integer value = n << 16 | n2;
        if (this.glyphPointMapCache == null) {
            synchronized (this) {
                if (this.glyphPointMapCache == null) {
                    this.glyphPointMapCache = new ConcurrentHashMap<Integer, Point2D.Float>();
                }
            }
        }
        else {
            glyphPoint = this.glyphPointMapCache.get(value);
        }
        if (glyphPoint == null) {
            glyphPoint = this.physicalFont.getGlyphPoint(this.pScalerContext, n, n2);
            this.adjustPoint(glyphPoint);
            this.glyphPointMapCache.put(value, glyphPoint);
        }
        return glyphPoint;
    }
    
    protected void adjustPoint(final Point2D.Float float1) {
    }
    
    static {
        switch (StrikeCache.nativeAddressSize) {
            case 8: {
                PhysicalStrike.longAddresses = true;
                break;
            }
            case 4: {
                PhysicalStrike.longAddresses = false;
                break;
            }
            default: {
                throw new RuntimeException("Unexpected address size");
            }
        }
    }
}
