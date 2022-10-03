package sun.font;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import java.lang.ref.SoftReference;
import java.awt.font.TextLayout;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.awt.FontMetrics;

public final class FontDesignMetrics extends FontMetrics
{
    static final long serialVersionUID = 4480069578560887773L;
    private static final float UNKNOWN_WIDTH = -1.0f;
    private static final int CURRENT_VERSION = 1;
    private static float roundingUpValue;
    private Font font;
    private float ascent;
    private float descent;
    private float leading;
    private float maxAdvance;
    private double[] matrix;
    private int[] cache;
    private int serVersion;
    private boolean isAntiAliased;
    private boolean usesFractionalMetrics;
    private AffineTransform frcTx;
    private transient float[] advCache;
    private transient int height;
    private transient FontRenderContext frc;
    private transient double[] devmatrix;
    private transient FontStrike fontStrike;
    private static FontRenderContext DEFAULT_FRC;
    private static final ConcurrentHashMap<Object, KeyReference> metricsCache;
    private static final int MAXRECENT = 5;
    private static final FontDesignMetrics[] recentMetrics;
    private static int recentIndex;
    
    private static FontRenderContext getDefaultFrc() {
        if (FontDesignMetrics.DEFAULT_FRC == null) {
            AffineTransform defaultTransform;
            if (GraphicsEnvironment.isHeadless()) {
                defaultTransform = new AffineTransform();
            }
            else {
                defaultTransform = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
            }
            FontDesignMetrics.DEFAULT_FRC = new FontRenderContext(defaultTransform, false, false);
        }
        return FontDesignMetrics.DEFAULT_FRC;
    }
    
    public static FontDesignMetrics getMetrics(final Font font) {
        return getMetrics(font, getDefaultFrc());
    }
    
    public static FontDesignMetrics getMetrics(final Font font, final FontRenderContext fontRenderContext) {
        if (SunFontManager.getInstance().maybeUsingAlternateCompositeFonts() && FontUtilities.getFont2D(font) instanceof CompositeFont) {
            return new FontDesignMetrics(font, fontRenderContext);
        }
        FontDesignMetrics fontDesignMetrics = null;
        final boolean equals = fontRenderContext.equals(getDefaultFrc());
        KeyReference keyReference;
        if (equals) {
            keyReference = FontDesignMetrics.metricsCache.get(font);
        }
        else {
            synchronized (MetricsKey.class) {
                MetricsKey.key.init(font, fontRenderContext);
                keyReference = FontDesignMetrics.metricsCache.get(MetricsKey.key);
            }
        }
        if (keyReference != null) {
            fontDesignMetrics = (FontDesignMetrics)keyReference.get();
        }
        if (fontDesignMetrics == null) {
            fontDesignMetrics = new FontDesignMetrics(font, fontRenderContext);
            if (equals) {
                FontDesignMetrics.metricsCache.put(font, new KeyReference(font, fontDesignMetrics));
            }
            else {
                final MetricsKey metricsKey = new MetricsKey(font, fontRenderContext);
                FontDesignMetrics.metricsCache.put(metricsKey, new KeyReference(metricsKey, fontDesignMetrics));
            }
        }
        for (int i = 0; i < FontDesignMetrics.recentMetrics.length; ++i) {
            if (FontDesignMetrics.recentMetrics[i] == fontDesignMetrics) {
                return fontDesignMetrics;
            }
        }
        synchronized (FontDesignMetrics.recentMetrics) {
            FontDesignMetrics.recentMetrics[FontDesignMetrics.recentIndex++] = fontDesignMetrics;
            if (FontDesignMetrics.recentIndex == 5) {
                FontDesignMetrics.recentIndex = 0;
            }
        }
        return fontDesignMetrics;
    }
    
    private FontDesignMetrics(final Font font) {
        this(font, getDefaultFrc());
    }
    
    private FontDesignMetrics(final Font font, final FontRenderContext frc) {
        super(font);
        this.serVersion = 0;
        this.height = -1;
        this.devmatrix = null;
        this.font = font;
        this.frc = frc;
        this.isAntiAliased = frc.isAntiAliased();
        this.usesFractionalMetrics = frc.usesFractionalMetrics();
        this.frcTx = frc.getTransform();
        this.matrix = new double[4];
        this.initMatrixAndMetrics();
        this.initAdvCache();
    }
    
    private void initMatrixAndMetrics() {
        this.fontStrike = FontUtilities.getFont2D(this.font).getStrike(this.font, this.frc);
        final StrikeMetrics fontMetrics = this.fontStrike.getFontMetrics();
        this.ascent = fontMetrics.getAscent();
        this.descent = fontMetrics.getDescent();
        this.leading = fontMetrics.getLeading();
        this.maxAdvance = fontMetrics.getMaxAdvance();
        this.devmatrix = new double[4];
        this.frcTx.getMatrix(this.devmatrix);
    }
    
    private void initAdvCache() {
        this.advCache = new float[256];
        for (int i = 0; i < 256; ++i) {
            this.advCache[i] = -1.0f;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.serVersion != 1) {
            this.frc = getDefaultFrc();
            this.isAntiAliased = this.frc.isAntiAliased();
            this.usesFractionalMetrics = this.frc.usesFractionalMetrics();
            this.frcTx = this.frc.getTransform();
        }
        else {
            this.frc = new FontRenderContext(this.frcTx, this.isAntiAliased, this.usesFractionalMetrics);
        }
        this.height = -1;
        this.cache = null;
        this.initMatrixAndMetrics();
        this.initAdvCache();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        this.cache = new int[256];
        for (int i = 0; i < 256; ++i) {
            this.cache[i] = -1;
        }
        this.serVersion = 1;
        objectOutputStream.defaultWriteObject();
        this.cache = null;
    }
    
    private float handleCharWidth(final int n) {
        return this.fontStrike.getCodePointAdvance(n);
    }
    
    private float getLatinCharWidth(final char c) {
        float handleCharWidth = this.advCache[c];
        if (handleCharWidth == -1.0f) {
            handleCharWidth = this.handleCharWidth(c);
            this.advCache[c] = handleCharWidth;
        }
        return handleCharWidth;
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.frc;
    }
    
    @Override
    public int charWidth(final char c) {
        float n;
        if (c < '\u0100') {
            n = this.getLatinCharWidth(c);
        }
        else {
            n = this.handleCharWidth(c);
        }
        return (int)(0.5 + n);
    }
    
    @Override
    public int charWidth(int n) {
        if (!Character.isValidCodePoint(n)) {
            n = 65535;
        }
        return (int)(0.5 + this.handleCharWidth(n));
    }
    
    @Override
    public int stringWidth(final String s) {
        float n = 0.0f;
        if (this.font.hasLayoutAttributes()) {
            if (s == null) {
                throw new NullPointerException("str is null");
            }
            if (s.length() == 0) {
                return 0;
            }
            n = new TextLayout(s, this.font, this.frc).getAdvance();
        }
        else {
            for (int length = s.length(), i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                if (char1 < '\u0100') {
                    n += this.getLatinCharWidth(char1);
                }
                else {
                    if (FontUtilities.isNonSimpleChar(char1)) {
                        n = new TextLayout(s, this.font, this.frc).getAdvance();
                        break;
                    }
                    n += this.handleCharWidth(char1);
                }
            }
        }
        return (int)(0.5 + n);
    }
    
    @Override
    public int charsWidth(final char[] array, final int n, final int n2) {
        float n3 = 0.0f;
        if (this.font.hasLayoutAttributes()) {
            if (n2 == 0) {
                return 0;
            }
            n3 = new TextLayout(new String(array, n, n2), this.font, this.frc).getAdvance();
        }
        else {
            if (n2 < 0) {
                throw new IndexOutOfBoundsException("len=" + n2);
            }
            for (int n4 = n + n2, i = n; i < n4; ++i) {
                final char c = array[i];
                if (c < '\u0100') {
                    n3 += this.getLatinCharWidth(c);
                }
                else {
                    if (FontUtilities.isNonSimpleChar(c)) {
                        n3 = new TextLayout(new String(array, n, n2), this.font, this.frc).getAdvance();
                        break;
                    }
                    n3 += this.handleCharWidth(c);
                }
            }
        }
        return (int)(0.5 + n3);
    }
    
    @Override
    public int[] getWidths() {
        final int[] array = new int[256];
        for (int i = 0; i < 256; i = (char)(i + 1)) {
            float n = this.advCache[i];
            if (n == -1.0f) {
                final float[] advCache = this.advCache;
                final int n2 = i;
                final float handleCharWidth = this.handleCharWidth(i);
                advCache[n2] = handleCharWidth;
                n = handleCharWidth;
            }
            array[i] = (int)(0.5 + n);
        }
        return array;
    }
    
    @Override
    public int getMaxAdvance() {
        return (int)(0.99f + this.maxAdvance);
    }
    
    @Override
    public int getAscent() {
        return (int)(FontDesignMetrics.roundingUpValue + this.ascent);
    }
    
    @Override
    public int getDescent() {
        return (int)(FontDesignMetrics.roundingUpValue + this.descent);
    }
    
    @Override
    public int getLeading() {
        return (int)(FontDesignMetrics.roundingUpValue + this.descent + this.leading) - (int)(FontDesignMetrics.roundingUpValue + this.descent);
    }
    
    @Override
    public int getHeight() {
        if (this.height < 0) {
            this.height = this.getAscent() + (int)(FontDesignMetrics.roundingUpValue + this.descent + this.leading);
        }
        return this.height;
    }
    
    static {
        FontDesignMetrics.roundingUpValue = 0.95f;
        FontDesignMetrics.DEFAULT_FRC = null;
        metricsCache = new ConcurrentHashMap<Object, KeyReference>();
        recentMetrics = new FontDesignMetrics[5];
        FontDesignMetrics.recentIndex = 0;
    }
    
    private static class KeyReference extends SoftReference implements DisposerRecord, Disposer.PollDisposable
    {
        static ReferenceQueue queue;
        Object key;
        
        KeyReference(final Object key, final Object o) {
            super(o, KeyReference.queue);
            this.key = key;
            Disposer.addReference(this, this);
        }
        
        @Override
        public void dispose() {
            if (FontDesignMetrics.metricsCache.get(this.key) == this) {
                FontDesignMetrics.metricsCache.remove(this.key);
            }
        }
        
        static {
            KeyReference.queue = Disposer.getQueue();
        }
    }
    
    private static class MetricsKey
    {
        Font font;
        FontRenderContext frc;
        int hash;
        static final MetricsKey key;
        
        MetricsKey() {
        }
        
        MetricsKey(final Font font, final FontRenderContext fontRenderContext) {
            this.init(font, fontRenderContext);
        }
        
        void init(final Font font, final FontRenderContext frc) {
            this.font = font;
            this.frc = frc;
            this.hash = font.hashCode() + frc.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof MetricsKey && this.font.equals(((MetricsKey)o).font) && this.frc.equals(((MetricsKey)o).frc);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        static {
            key = new MetricsKey();
        }
    }
}
