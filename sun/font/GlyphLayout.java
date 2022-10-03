package sun.font;

import java.awt.geom.NoninvertibleTransformException;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.SoftReference;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public final class GlyphLayout
{
    private GVData _gvdata;
    private static volatile GlyphLayout cache;
    private LayoutEngineFactory _lef;
    private TextRecord _textRecord;
    private ScriptRun _scriptRuns;
    private FontRunIterator _fontRuns;
    private int _ercount;
    private ArrayList _erecords;
    private Point2D.Float _pt;
    private FontStrikeDesc _sd;
    private float[] _mat;
    private int _typo_flags;
    private int _offset;
    
    public static GlyphLayout get(LayoutEngineFactory instance) {
        if (instance == null) {
            instance = SunLayoutEngine.instance();
        }
        GlyphLayout cache = null;
        synchronized (GlyphLayout.class) {
            if (GlyphLayout.cache != null) {
                cache = GlyphLayout.cache;
                GlyphLayout.cache = null;
            }
        }
        if (cache == null) {
            cache = new GlyphLayout();
        }
        cache._lef = instance;
        return cache;
    }
    
    public static void done(final GlyphLayout cache) {
        cache._lef = null;
        GlyphLayout.cache = cache;
    }
    
    public StandardGlyphVector layout(final Font font, final FontRenderContext fontRenderContext, final char[] array, final int offset, final int n, final int n2, final StandardGlyphVector standardGlyphVector) {
        if (array == null || offset < 0 || n < 0 || n > array.length - offset) {
            throw new IllegalArgumentException();
        }
        this.init(n);
        if (font.hasLayoutAttributes()) {
            final AttributeValues values = ((AttributeMap)font.getAttributes()).getValues();
            if (values.getKerning() != 0) {
                this._typo_flags |= 0x1;
            }
            if (values.getLigatures() != 0) {
                this._typo_flags |= 0x2;
            }
        }
        this._offset = offset;
        final SDCache value = SDCache.get(font, fontRenderContext);
        this._mat[0] = (float)value.gtx.getScaleX();
        this._mat[1] = (float)value.gtx.getShearY();
        this._mat[2] = (float)value.gtx.getShearX();
        this._mat[3] = (float)value.gtx.getScaleY();
        this._pt.setLocation(value.delta);
        final int n3 = offset + n;
        int n4 = 0;
        int length = array.length;
        if (n2 != 0) {
            if ((n2 & 0x1) != 0x0) {
                this._typo_flags |= Integer.MIN_VALUE;
            }
            if ((n2 & 0x2) != 0x0) {
                n4 = offset;
            }
            if ((n2 & 0x4) != 0x0) {
                length = n3;
            }
        }
        final int n5 = -1;
        Font2D font2D = FontUtilities.getFont2D(font);
        if (font2D instanceof FontSubstitution) {
            font2D = ((FontSubstitution)font2D).getCompositeFont2D();
        }
        this._textRecord.init(array, offset, n3, n4, length);
        int n6 = offset;
        if (font2D instanceof CompositeFont) {
            this._scriptRuns.init(array, offset, n);
            this._fontRuns.init((CompositeFont)font2D, array, offset, n3);
            while (this._scriptRuns.next()) {
                final int scriptLimit = this._scriptRuns.getScriptLimit();
                final int scriptCode = this._scriptRuns.getScriptCode();
                while (this._fontRuns.next(scriptCode, scriptLimit)) {
                    PhysicalFont physicalFont = this._fontRuns.getFont();
                    if (physicalFont instanceof NativeFont) {
                        physicalFont = ((NativeFont)physicalFont).getDelegateFont();
                    }
                    final int glyphMask = this._fontRuns.getGlyphMask();
                    final int pos = this._fontRuns.getPos();
                    this.nextEngineRecord(n6, pos, scriptCode, n5, physicalFont, glyphMask);
                    n6 = pos;
                }
            }
        }
        else {
            this._scriptRuns.init(array, offset, n);
            while (this._scriptRuns.next()) {
                final int scriptLimit2 = this._scriptRuns.getScriptLimit();
                this.nextEngineRecord(n6, scriptLimit2, this._scriptRuns.getScriptCode(), n5, font2D, 0);
                n6 = scriptLimit2;
            }
        }
        int i = 0;
        int ercount = this._ercount;
        int n7 = 1;
        if (this._typo_flags < 0) {
            i = ercount - 1;
            ercount = -1;
            n7 = -1;
        }
        this._sd = value.sd;
        while (i != ercount) {
            final EngineRecord engineRecord = this._erecords.get(i);
            while (true) {
                try {
                    engineRecord.layout();
                }
                catch (final IndexOutOfBoundsException ex) {
                    if (this._gvdata._count >= 0) {
                        this._gvdata.grow();
                    }
                    continue;
                }
                break;
            }
            if (this._gvdata._count < 0) {
                break;
            }
            i += n7;
        }
        StandardGlyphVector glyphVector;
        if (this._gvdata._count < 0) {
            glyphVector = new StandardGlyphVector(font, array, offset, n, fontRenderContext);
            if (FontUtilities.debugFonts()) {
                FontUtilities.getLogger().warning("OpenType layout failed on font: " + font);
            }
        }
        else {
            glyphVector = this._gvdata.createGlyphVector(font, fontRenderContext, standardGlyphVector);
        }
        return glyphVector;
    }
    
    private GlyphLayout() {
        this._gvdata = new GVData();
        this._textRecord = new TextRecord();
        this._scriptRuns = new ScriptRun();
        this._fontRuns = new FontRunIterator();
        this._erecords = new ArrayList(10);
        this._pt = new Point2D.Float();
        this._sd = new FontStrikeDesc();
        this._mat = new float[4];
    }
    
    private void init(final int n) {
        this._typo_flags = 0;
        this._ercount = 0;
        this._gvdata.init(n);
    }
    
    private void nextEngineRecord(final int n, final int n2, final int n3, final int n4, final Font2D font2D, final int n5) {
        EngineRecord engineRecord;
        if (this._ercount == this._erecords.size()) {
            engineRecord = new EngineRecord();
            this._erecords.add(engineRecord);
        }
        else {
            engineRecord = this._erecords.get(this._ercount);
        }
        engineRecord.init(n, n2, font2D, n3, n4, n5);
        ++this._ercount;
    }
    
    public static final class LayoutEngineKey
    {
        private Font2D font;
        private int script;
        private int lang;
        
        LayoutEngineKey() {
        }
        
        LayoutEngineKey(final Font2D font2D, final int n, final int n2) {
            this.init(font2D, n, n2);
        }
        
        void init(final Font2D font, final int script, final int lang) {
            this.font = font;
            this.script = script;
            this.lang = lang;
        }
        
        LayoutEngineKey copy() {
            return new LayoutEngineKey(this.font, this.script, this.lang);
        }
        
        Font2D font() {
            return this.font;
        }
        
        int script() {
            return this.script;
        }
        
        int lang() {
            return this.lang;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            try {
                final LayoutEngineKey layoutEngineKey = (LayoutEngineKey)o;
                return this.script == layoutEngineKey.script && this.lang == layoutEngineKey.lang && this.font.equals(layoutEngineKey.font);
            }
            catch (final ClassCastException ex) {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return this.script ^ this.lang ^ this.font.hashCode();
        }
    }
    
    private static final class SDCache
    {
        public Font key_font;
        public FontRenderContext key_frc;
        public AffineTransform dtx;
        public AffineTransform invdtx;
        public AffineTransform gtx;
        public Point2D.Float delta;
        public FontStrikeDesc sd;
        private static final Point2D.Float ZERO_DELTA;
        private static SoftReference<ConcurrentHashMap<SDKey, SDCache>> cacheRef;
        
        private SDCache(final Font key_font, final FontRenderContext key_frc) {
            this.key_font = key_font;
            this.key_frc = key_frc;
            (this.dtx = key_frc.getTransform()).setTransform(this.dtx.getScaleX(), this.dtx.getShearY(), this.dtx.getShearX(), this.dtx.getScaleY(), 0.0, 0.0);
            if (!this.dtx.isIdentity()) {
                try {
                    this.invdtx = this.dtx.createInverse();
                }
                catch (final NoninvertibleTransformException ex) {
                    throw new InternalError(ex);
                }
            }
            final float size2D = key_font.getSize2D();
            if (key_font.isTransformed()) {
                (this.gtx = key_font.getTransform()).scale(size2D, size2D);
                this.delta = new Point2D.Float((float)this.gtx.getTranslateX(), (float)this.gtx.getTranslateY());
                this.gtx.setTransform(this.gtx.getScaleX(), this.gtx.getShearY(), this.gtx.getShearX(), this.gtx.getScaleY(), 0.0, 0.0);
                this.gtx.preConcatenate(this.dtx);
            }
            else {
                this.delta = SDCache.ZERO_DELTA;
                (this.gtx = new AffineTransform(this.dtx)).scale(size2D, size2D);
            }
            this.sd = new FontStrikeDesc(this.dtx, this.gtx, key_font.getStyle(), FontStrikeDesc.getAAHintIntVal(key_frc.getAntiAliasingHint(), FontUtilities.getFont2D(key_font), (int)Math.abs(size2D)), FontStrikeDesc.getFMHintIntVal(key_frc.getFractionalMetricsHint()));
        }
        
        public static SDCache get(final Font font, FontRenderContext fontRenderContext) {
            if (fontRenderContext.isTransformed()) {
                final AffineTransform transform = fontRenderContext.getTransform();
                if (transform.getTranslateX() != 0.0 || transform.getTranslateY() != 0.0) {
                    fontRenderContext = new FontRenderContext(new AffineTransform(transform.getScaleX(), transform.getShearY(), transform.getShearX(), transform.getScaleY(), 0.0, 0.0), fontRenderContext.getAntiAliasingHint(), fontRenderContext.getFractionalMetricsHint());
                }
            }
            final SDKey sdKey = new SDKey(font, fontRenderContext);
            ConcurrentHashMap<SDKey, SDCache> concurrentHashMap = null;
            SDCache sdCache = null;
            if (SDCache.cacheRef != null) {
                concurrentHashMap = SDCache.cacheRef.get();
                if (concurrentHashMap != null) {
                    sdCache = concurrentHashMap.get(sdKey);
                }
            }
            if (sdCache == null) {
                sdCache = new SDCache(font, fontRenderContext);
                if (concurrentHashMap == null) {
                    concurrentHashMap = new ConcurrentHashMap<SDKey, SDCache>(10);
                    SDCache.cacheRef = new SoftReference<ConcurrentHashMap<SDKey, SDCache>>(concurrentHashMap);
                }
                else if (concurrentHashMap.size() >= 512) {
                    concurrentHashMap.clear();
                }
                concurrentHashMap.put(sdKey, sdCache);
            }
            return sdCache;
        }
        
        static {
            ZERO_DELTA = new Point2D.Float();
        }
        
        private static final class SDKey
        {
            private final Font font;
            private final FontRenderContext frc;
            private final int hash;
            
            SDKey(final Font font, final FontRenderContext frc) {
                this.font = font;
                this.frc = frc;
                this.hash = (font.hashCode() ^ frc.hashCode());
            }
            
            @Override
            public int hashCode() {
                return this.hash;
            }
            
            @Override
            public boolean equals(final Object o) {
                try {
                    final SDKey sdKey = (SDKey)o;
                    return this.hash == sdKey.hash && this.font.equals(sdKey.font) && this.frc.equals(sdKey.frc);
                }
                catch (final ClassCastException ex) {
                    return false;
                }
            }
        }
    }
    
    public static final class GVData
    {
        public int _count;
        public int _flags;
        public int[] _glyphs;
        public float[] _positions;
        public int[] _indices;
        private static final int UNINITIALIZED_FLAGS = -1;
        
        public void init(int n) {
            this._count = 0;
            this._flags = -1;
            if (this._glyphs == null || this._glyphs.length < n) {
                if (n < 20) {
                    n = 20;
                }
                this._glyphs = new int[n];
                this._positions = new float[n * 2 + 2];
                this._indices = new int[n];
            }
        }
        
        public void grow() {
            this.grow(this._glyphs.length / 4);
        }
        
        public void grow(final int n) {
            final int n2 = this._glyphs.length + n;
            final int[] glyphs = new int[n2];
            System.arraycopy(this._glyphs, 0, glyphs, 0, this._count);
            this._glyphs = glyphs;
            final float[] positions = new float[n2 * 2 + 2];
            System.arraycopy(this._positions, 0, positions, 0, this._count * 2 + 2);
            this._positions = positions;
            final int[] indices = new int[n2];
            System.arraycopy(this._indices, 0, indices, 0, this._count);
            this._indices = indices;
        }
        
        public void adjustPositions(final AffineTransform affineTransform) {
            affineTransform.transform(this._positions, 0, this._positions, 0, this._count);
        }
        
        public StandardGlyphVector createGlyphVector(final Font font, final FontRenderContext fontRenderContext, StandardGlyphVector standardGlyphVector) {
            if (this._flags == -1) {
                this._flags = 0;
                if (this._count > 1) {
                    boolean b = true;
                    boolean b2 = true;
                    int n2;
                    for (int count = this._count, n = 0; n < this._count && (b || b2); b = (b && n2 == n), b2 = (b2 && n2 == --count), ++n) {
                        n2 = this._indices[n];
                    }
                    if (b2) {
                        this._flags |= 0x4;
                    }
                    if (!b2 && !b) {
                        this._flags |= 0x8;
                    }
                }
                this._flags |= 0x2;
            }
            final int[] array = new int[this._count];
            System.arraycopy(this._glyphs, 0, array, 0, this._count);
            float[] array2 = null;
            if ((this._flags & 0x2) != 0x0) {
                array2 = new float[this._count * 2 + 2];
                System.arraycopy(this._positions, 0, array2, 0, array2.length);
            }
            int[] array3 = null;
            if ((this._flags & 0x8) != 0x0) {
                array3 = new int[this._count];
                System.arraycopy(this._indices, 0, array3, 0, this._count);
            }
            if (standardGlyphVector == null) {
                standardGlyphVector = new StandardGlyphVector(font, fontRenderContext, array, array2, array3, this._flags);
            }
            else {
                standardGlyphVector.initGlyphVector(font, fontRenderContext, array, array2, array3, this._flags);
            }
            return standardGlyphVector;
        }
    }
    
    private final class EngineRecord
    {
        private int start;
        private int limit;
        private int gmask;
        private int eflags;
        private LayoutEngineKey key;
        private LayoutEngine engine;
        
        EngineRecord() {
            this.key = new LayoutEngineKey();
        }
        
        void init(final int start, final int limit, final Font2D font2D, final int n, final int n2, final int gmask) {
            this.start = start;
            this.limit = limit;
            this.gmask = gmask;
            this.key.init(font2D, n, n2);
            this.eflags = 0;
            for (int i = start; i < limit; ++i) {
                int codePoint = GlyphLayout.this._textRecord.text[i];
                if (Character.isHighSurrogate((char)codePoint) && i < limit - 1 && Character.isLowSurrogate(GlyphLayout.this._textRecord.text[i + 1])) {
                    codePoint = Character.toCodePoint((char)codePoint, GlyphLayout.this._textRecord.text[++i]);
                }
                final int type = Character.getType(codePoint);
                if (type == 6 || type == 7 || type == 8) {
                    this.eflags = 4;
                    break;
                }
            }
            this.engine = GlyphLayout.this._lef.getEngine(this.key);
        }
        
        void layout() {
            GlyphLayout.this._textRecord.start = this.start;
            GlyphLayout.this._textRecord.limit = this.limit;
            this.engine.layout(GlyphLayout.this._sd, GlyphLayout.this._mat, this.gmask, this.start - GlyphLayout.this._offset, GlyphLayout.this._textRecord, GlyphLayout.this._typo_flags | this.eflags, GlyphLayout.this._pt, GlyphLayout.this._gvdata);
        }
    }
    
    public interface LayoutEngine
    {
        void layout(final FontStrikeDesc p0, final float[] p1, final int p2, final int p3, final TextRecord p4, final int p5, final Point2D.Float p6, final GVData p7);
    }
    
    public interface LayoutEngineFactory
    {
        LayoutEngine getEngine(final Font2D p0, final int p1, final int p2);
        
        LayoutEngine getEngine(final LayoutEngineKey p0);
    }
}
