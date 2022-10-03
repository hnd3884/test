package sun.font;

import java.util.Locale;
import java.lang.ref.SoftReference;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.font.FontRenderContext;

public abstract class Font2D
{
    public static final int FONT_CONFIG_RANK = 2;
    public static final int JRE_RANK = 2;
    public static final int TTF_RANK = 3;
    public static final int TYPE1_RANK = 4;
    public static final int NATIVE_RANK = 5;
    public static final int UNKNOWN_RANK = 6;
    public static final int DEFAULT_RANK = 4;
    private static final String[] boldNames;
    private static final String[] italicNames;
    private static final String[] boldItalicNames;
    private static final FontRenderContext DEFAULT_FRC;
    public Font2DHandle handle;
    protected String familyName;
    protected String fullName;
    protected int style;
    protected FontFamily family;
    protected int fontRank;
    protected CharToGlyphMapper mapper;
    protected ConcurrentHashMap<FontStrikeDesc, Reference> strikeCache;
    protected Reference<FontStrike> lastFontStrike;
    private int strikeCacheMax;
    private boolean useWeak;
    public static final int FWIDTH_NORMAL = 5;
    public static final int FWEIGHT_NORMAL = 400;
    public static final int FWEIGHT_BOLD = 700;
    
    public Font2D() {
        this.style = 0;
        this.fontRank = 4;
        this.strikeCache = new ConcurrentHashMap<FontStrikeDesc, Reference>();
        this.lastFontStrike = new WeakReference<FontStrike>(null);
        this.strikeCacheMax = 0;
    }
    
    void setUseWeakRefs(final boolean useWeak, final int n) {
        this.useWeak = useWeak;
        this.strikeCacheMax = ((useWeak && n > 0) ? n : 0);
    }
    
    public int getStyle() {
        return this.style;
    }
    
    protected void setStyle() {
        final String lowerCase = this.fullName.toLowerCase();
        for (int i = 0; i < Font2D.boldItalicNames.length; ++i) {
            if (lowerCase.indexOf(Font2D.boldItalicNames[i]) != -1) {
                this.style = 3;
                return;
            }
        }
        for (int j = 0; j < Font2D.italicNames.length; ++j) {
            if (lowerCase.indexOf(Font2D.italicNames[j]) != -1) {
                this.style = 2;
                return;
            }
        }
        for (int k = 0; k < Font2D.boldNames.length; ++k) {
            if (lowerCase.indexOf(Font2D.boldNames[k]) != -1) {
                this.style = 1;
                return;
            }
        }
    }
    
    public int getWidth() {
        return 5;
    }
    
    public int getWeight() {
        if ((this.style & 0x1) != 0x0) {
            return 700;
        }
        return 400;
    }
    
    int getRank() {
        return this.fontRank;
    }
    
    void setRank(final int fontRank) {
        this.fontRank = fontRank;
    }
    
    abstract CharToGlyphMapper getMapper();
    
    protected int getValidatedGlyphCode(int missingGlyphCode) {
        if (missingGlyphCode < 0 || missingGlyphCode >= this.getMapper().getNumGlyphs()) {
            missingGlyphCode = this.getMapper().getMissingGlyphCode();
        }
        return missingGlyphCode;
    }
    
    abstract FontStrike createStrike(final FontStrikeDesc p0);
    
    public FontStrike getStrike(final Font font) {
        final FontStrike fontStrike = this.lastFontStrike.get();
        if (fontStrike != null) {
            return fontStrike;
        }
        return this.getStrike(font, Font2D.DEFAULT_FRC);
    }
    
    public FontStrike getStrike(final Font font, final AffineTransform affineTransform, final int n, final int n2) {
        final double n3 = font.getSize2D();
        final AffineTransform affineTransform2 = (AffineTransform)affineTransform.clone();
        affineTransform2.scale(n3, n3);
        if (font.isTransformed()) {
            affineTransform2.concatenate(font.getTransform());
        }
        if (affineTransform2.getTranslateX() != 0.0 || affineTransform2.getTranslateY() != 0.0) {
            affineTransform2.setTransform(affineTransform2.getScaleX(), affineTransform2.getShearY(), affineTransform2.getShearX(), affineTransform2.getScaleY(), 0.0, 0.0);
        }
        return this.getStrike(new FontStrikeDesc(affineTransform, affineTransform2, font.getStyle(), n, n2), false);
    }
    
    public FontStrike getStrike(final Font font, final AffineTransform affineTransform, final AffineTransform affineTransform2, final int n, final int n2) {
        return this.getStrike(new FontStrikeDesc(affineTransform, affineTransform2, font.getStyle(), n, n2), false);
    }
    
    public FontStrike getStrike(final Font font, final FontRenderContext fontRenderContext) {
        final AffineTransform transform = fontRenderContext.getTransform();
        final double n = font.getSize2D();
        transform.scale(n, n);
        if (font.isTransformed()) {
            transform.concatenate(font.getTransform());
            if (transform.getTranslateX() != 0.0 || transform.getTranslateY() != 0.0) {
                transform.setTransform(transform.getScaleX(), transform.getShearY(), transform.getShearX(), transform.getScaleY(), 0.0, 0.0);
            }
        }
        return this.getStrike(new FontStrikeDesc(fontRenderContext.getTransform(), transform, font.getStyle(), FontStrikeDesc.getAAHintIntVal(this, font, fontRenderContext), FontStrikeDesc.getFMHintIntVal(fontRenderContext.getFractionalMetricsHint())), false);
    }
    
    void updateLastStrikeRef(final FontStrike fontStrike) {
        this.lastFontStrike.clear();
        if (this.useWeak) {
            this.lastFontStrike = new WeakReference<FontStrike>(fontStrike);
        }
        else {
            this.lastFontStrike = new SoftReference<FontStrike>(fontStrike);
        }
    }
    
    FontStrike getStrike(final FontStrikeDesc fontStrikeDesc) {
        return this.getStrike(fontStrikeDesc, true);
    }
    
    private FontStrike getStrike(FontStrikeDesc fontStrikeDesc, final boolean b) {
        final FontStrike fontStrike = this.lastFontStrike.get();
        if (fontStrike != null && fontStrikeDesc.equals(fontStrike.desc)) {
            return fontStrike;
        }
        final Reference reference = this.strikeCache.get(fontStrikeDesc);
        if (reference != null) {
            final FontStrike fontStrike2 = (FontStrike)reference.get();
            if (fontStrike2 != null) {
                this.updateLastStrikeRef(fontStrike2);
                StrikeCache.refStrike(fontStrike2);
                return fontStrike2;
            }
        }
        if (b) {
            fontStrikeDesc = new FontStrikeDesc(fontStrikeDesc);
        }
        final FontStrike strike = this.createStrike(fontStrikeDesc);
        final int type = fontStrikeDesc.glyphTx.getType();
        Reference reference2;
        if (this.useWeak || type == 32 || ((type & 0x10) != 0x0 && this.strikeCache.size() > 10)) {
            reference2 = StrikeCache.getStrikeRef(strike, true);
        }
        else {
            reference2 = StrikeCache.getStrikeRef(strike, this.useWeak);
        }
        this.strikeCache.put(fontStrikeDesc, reference2);
        this.updateLastStrikeRef(strike);
        StrikeCache.refStrike(strike);
        return strike;
    }
    
    public void getFontMetrics(final Font font, final AffineTransform affineTransform, final Object o, final Object o2, final float[] array) {
        final StrikeMetrics fontMetrics = this.getStrike(font, affineTransform, FontStrikeDesc.getAAHintIntVal(o, this, font.getSize()), FontStrikeDesc.getFMHintIntVal(o2)).getFontMetrics();
        array[0] = fontMetrics.getAscent();
        array[1] = fontMetrics.getDescent();
        array[2] = fontMetrics.getLeading();
        array[3] = fontMetrics.getMaxAdvance();
        this.getStyleMetrics(font.getSize2D(), array, 4);
    }
    
    public void getStyleMetrics(final float n, final float[] array, final int n2) {
        array[n2] = -array[0] / 2.5f;
        array[n2 + 1] = n / 12.0f;
        array[n2 + 2] = array[n2 + 1] / 1.5f;
        array[n2 + 3] = array[n2 + 1];
    }
    
    public void getFontMetrics(final Font font, final FontRenderContext fontRenderContext, final float[] array) {
        final StrikeMetrics fontMetrics = this.getStrike(font, fontRenderContext).getFontMetrics();
        array[0] = fontMetrics.getAscent();
        array[1] = fontMetrics.getDescent();
        array[2] = fontMetrics.getLeading();
        array[3] = fontMetrics.getMaxAdvance();
    }
    
    protected byte[] getTableBytes(final int n) {
        return null;
    }
    
    protected long getLayoutTableCache() {
        return 0L;
    }
    
    protected long getUnitsPerEm() {
        return 2048L;
    }
    
    boolean supportsEncoding(final String s) {
        return false;
    }
    
    public boolean canDoStyle(final int n) {
        return n == this.style;
    }
    
    public boolean useAAForPtSize(final int n) {
        return true;
    }
    
    public boolean hasSupplementaryChars() {
        return false;
    }
    
    public String getPostscriptName() {
        return this.fullName;
    }
    
    public String getFontName(final Locale locale) {
        return this.fullName;
    }
    
    public String getFamilyName(final Locale locale) {
        return this.familyName;
    }
    
    public int getNumGlyphs() {
        return this.getMapper().getNumGlyphs();
    }
    
    public int charToGlyph(final int n) {
        return this.getMapper().charToGlyph(n);
    }
    
    public int getMissingGlyphCode() {
        return this.getMapper().getMissingGlyphCode();
    }
    
    public boolean canDisplay(final char c) {
        return this.getMapper().canDisplay(c);
    }
    
    public boolean canDisplay(final int n) {
        return this.getMapper().canDisplay(n);
    }
    
    public byte getBaselineFor(final char c) {
        return 0;
    }
    
    public float getItalicAngle(final Font font, final AffineTransform affineTransform, final Object o, final Object o2) {
        final StrikeMetrics fontMetrics = this.getStrike(font, affineTransform, FontStrikeDesc.getAAHintIntVal(o, this, 12), FontStrikeDesc.getFMHintIntVal(o2)).getFontMetrics();
        if (fontMetrics.ascentY == 0.0f || fontMetrics.ascentX == 0.0f) {
            return 0.0f;
        }
        return fontMetrics.ascentX / -fontMetrics.ascentY;
    }
    
    static {
        boldNames = new String[] { "bold", "demibold", "demi-bold", "demi bold", "negreta", "demi" };
        italicNames = new String[] { "italic", "cursiva", "oblique", "inclined" };
        boldItalicNames = new String[] { "bolditalic", "bold-italic", "bold italic", "boldoblique", "bold-oblique", "bold oblique", "demibold italic", "negreta cursiva", "demi oblique" };
        DEFAULT_FRC = new FontRenderContext(null, false, false);
    }
}
