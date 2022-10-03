package sun.awt;

import java.awt.RenderingHints;

public class SunHints
{
    private static final int NUM_KEYS = 10;
    private static final int VALS_PER_KEY = 8;
    public static final int INTKEY_RENDERING = 0;
    public static final int INTVAL_RENDER_DEFAULT = 0;
    public static final int INTVAL_RENDER_SPEED = 1;
    public static final int INTVAL_RENDER_QUALITY = 2;
    public static final int INTKEY_ANTIALIASING = 1;
    public static final int INTVAL_ANTIALIAS_DEFAULT = 0;
    public static final int INTVAL_ANTIALIAS_OFF = 1;
    public static final int INTVAL_ANTIALIAS_ON = 2;
    public static final int INTKEY_TEXT_ANTIALIASING = 2;
    public static final int INTVAL_TEXT_ANTIALIAS_DEFAULT = 0;
    public static final int INTVAL_TEXT_ANTIALIAS_OFF = 1;
    public static final int INTVAL_TEXT_ANTIALIAS_ON = 2;
    public static final int INTVAL_TEXT_ANTIALIAS_GASP = 3;
    public static final int INTVAL_TEXT_ANTIALIAS_LCD_HRGB = 4;
    public static final int INTVAL_TEXT_ANTIALIAS_LCD_HBGR = 5;
    public static final int INTVAL_TEXT_ANTIALIAS_LCD_VRGB = 6;
    public static final int INTVAL_TEXT_ANTIALIAS_LCD_VBGR = 7;
    public static final int INTKEY_FRACTIONALMETRICS = 3;
    public static final int INTVAL_FRACTIONALMETRICS_DEFAULT = 0;
    public static final int INTVAL_FRACTIONALMETRICS_OFF = 1;
    public static final int INTVAL_FRACTIONALMETRICS_ON = 2;
    public static final int INTKEY_DITHERING = 4;
    public static final int INTVAL_DITHER_DEFAULT = 0;
    public static final int INTVAL_DITHER_DISABLE = 1;
    public static final int INTVAL_DITHER_ENABLE = 2;
    public static final int INTKEY_INTERPOLATION = 5;
    public static final int INTVAL_INTERPOLATION_NEAREST_NEIGHBOR = 0;
    public static final int INTVAL_INTERPOLATION_BILINEAR = 1;
    public static final int INTVAL_INTERPOLATION_BICUBIC = 2;
    public static final int INTKEY_ALPHA_INTERPOLATION = 6;
    public static final int INTVAL_ALPHA_INTERPOLATION_DEFAULT = 0;
    public static final int INTVAL_ALPHA_INTERPOLATION_SPEED = 1;
    public static final int INTVAL_ALPHA_INTERPOLATION_QUALITY = 2;
    public static final int INTKEY_COLOR_RENDERING = 7;
    public static final int INTVAL_COLOR_RENDER_DEFAULT = 0;
    public static final int INTVAL_COLOR_RENDER_SPEED = 1;
    public static final int INTVAL_COLOR_RENDER_QUALITY = 2;
    public static final int INTKEY_STROKE_CONTROL = 8;
    public static final int INTVAL_STROKE_DEFAULT = 0;
    public static final int INTVAL_STROKE_NORMALIZE = 1;
    public static final int INTVAL_STROKE_PURE = 2;
    public static final int INTKEY_RESOLUTION_VARIANT = 9;
    public static final int INTVAL_RESOLUTION_VARIANT_DEFAULT = 0;
    public static final int INTVAL_RESOLUTION_VARIANT_OFF = 1;
    public static final int INTVAL_RESOLUTION_VARIANT_ON = 2;
    public static final int INTKEY_AATEXT_LCD_CONTRAST = 100;
    public static final Key KEY_RENDERING;
    public static final Object VALUE_RENDER_SPEED;
    public static final Object VALUE_RENDER_QUALITY;
    public static final Object VALUE_RENDER_DEFAULT;
    public static final Key KEY_ANTIALIASING;
    public static final Object VALUE_ANTIALIAS_ON;
    public static final Object VALUE_ANTIALIAS_OFF;
    public static final Object VALUE_ANTIALIAS_DEFAULT;
    public static final Key KEY_TEXT_ANTIALIASING;
    public static final Object VALUE_TEXT_ANTIALIAS_ON;
    public static final Object VALUE_TEXT_ANTIALIAS_OFF;
    public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT;
    public static final Object VALUE_TEXT_ANTIALIAS_GASP;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR;
    public static final Key KEY_FRACTIONALMETRICS;
    public static final Object VALUE_FRACTIONALMETRICS_ON;
    public static final Object VALUE_FRACTIONALMETRICS_OFF;
    public static final Object VALUE_FRACTIONALMETRICS_DEFAULT;
    public static final Key KEY_DITHERING;
    public static final Object VALUE_DITHER_ENABLE;
    public static final Object VALUE_DITHER_DISABLE;
    public static final Object VALUE_DITHER_DEFAULT;
    public static final Key KEY_INTERPOLATION;
    public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    public static final Object VALUE_INTERPOLATION_BILINEAR;
    public static final Object VALUE_INTERPOLATION_BICUBIC;
    public static final Key KEY_ALPHA_INTERPOLATION;
    public static final Object VALUE_ALPHA_INTERPOLATION_SPEED;
    public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY;
    public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT;
    public static final Key KEY_COLOR_RENDERING;
    public static final Object VALUE_COLOR_RENDER_SPEED;
    public static final Object VALUE_COLOR_RENDER_QUALITY;
    public static final Object VALUE_COLOR_RENDER_DEFAULT;
    public static final Key KEY_STROKE_CONTROL;
    public static final Object VALUE_STROKE_DEFAULT;
    public static final Object VALUE_STROKE_NORMALIZE;
    public static final Object VALUE_STROKE_PURE;
    public static final Key KEY_RESOLUTION_VARIANT;
    public static final Object VALUE_RESOLUTION_VARIANT_DEFAULT;
    public static final Object VALUE_RESOLUTION_VARIANT_OFF;
    public static final Object VALUE_RESOLUTION_VARIANT_ON;
    public static final RenderingHints.Key KEY_TEXT_ANTIALIAS_LCD_CONTRAST;
    
    static {
        KEY_RENDERING = new Key(0, "Global rendering quality key");
        VALUE_RENDER_SPEED = new Value(SunHints.KEY_RENDERING, 1, "Fastest rendering methods");
        VALUE_RENDER_QUALITY = new Value(SunHints.KEY_RENDERING, 2, "Highest quality rendering methods");
        VALUE_RENDER_DEFAULT = new Value(SunHints.KEY_RENDERING, 0, "Default rendering methods");
        KEY_ANTIALIASING = new Key(1, "Global antialiasing enable key");
        VALUE_ANTIALIAS_ON = new Value(SunHints.KEY_ANTIALIASING, 2, "Antialiased rendering mode");
        VALUE_ANTIALIAS_OFF = new Value(SunHints.KEY_ANTIALIASING, 1, "Nonantialiased rendering mode");
        VALUE_ANTIALIAS_DEFAULT = new Value(SunHints.KEY_ANTIALIASING, 0, "Default antialiasing rendering mode");
        KEY_TEXT_ANTIALIASING = new Key(2, "Text-specific antialiasing enable key");
        VALUE_TEXT_ANTIALIAS_ON = new Value(SunHints.KEY_TEXT_ANTIALIASING, 2, "Antialiased text mode");
        VALUE_TEXT_ANTIALIAS_OFF = new Value(SunHints.KEY_TEXT_ANTIALIASING, 1, "Nonantialiased text mode");
        VALUE_TEXT_ANTIALIAS_DEFAULT = new Value(SunHints.KEY_TEXT_ANTIALIASING, 0, "Default antialiasing text mode");
        VALUE_TEXT_ANTIALIAS_GASP = new Value(SunHints.KEY_TEXT_ANTIALIASING, 3, "gasp antialiasing text mode");
        VALUE_TEXT_ANTIALIAS_LCD_HRGB = new Value(SunHints.KEY_TEXT_ANTIALIASING, 4, "LCD HRGB antialiasing text mode");
        VALUE_TEXT_ANTIALIAS_LCD_HBGR = new Value(SunHints.KEY_TEXT_ANTIALIASING, 5, "LCD HBGR antialiasing text mode");
        VALUE_TEXT_ANTIALIAS_LCD_VRGB = new Value(SunHints.KEY_TEXT_ANTIALIASING, 6, "LCD VRGB antialiasing text mode");
        VALUE_TEXT_ANTIALIAS_LCD_VBGR = new Value(SunHints.KEY_TEXT_ANTIALIASING, 7, "LCD VBGR antialiasing text mode");
        KEY_FRACTIONALMETRICS = new Key(3, "Fractional metrics enable key");
        VALUE_FRACTIONALMETRICS_ON = new Value(SunHints.KEY_FRACTIONALMETRICS, 2, "Fractional text metrics mode");
        VALUE_FRACTIONALMETRICS_OFF = new Value(SunHints.KEY_FRACTIONALMETRICS, 1, "Integer text metrics mode");
        VALUE_FRACTIONALMETRICS_DEFAULT = new Value(SunHints.KEY_FRACTIONALMETRICS, 0, "Default fractional text metrics mode");
        KEY_DITHERING = new Key(4, "Dithering quality key");
        VALUE_DITHER_ENABLE = new Value(SunHints.KEY_DITHERING, 2, "Dithered rendering mode");
        VALUE_DITHER_DISABLE = new Value(SunHints.KEY_DITHERING, 1, "Nondithered rendering mode");
        VALUE_DITHER_DEFAULT = new Value(SunHints.KEY_DITHERING, 0, "Default dithering mode");
        KEY_INTERPOLATION = new Key(5, "Image interpolation method key");
        VALUE_INTERPOLATION_NEAREST_NEIGHBOR = new Value(SunHints.KEY_INTERPOLATION, 0, "Nearest Neighbor image interpolation mode");
        VALUE_INTERPOLATION_BILINEAR = new Value(SunHints.KEY_INTERPOLATION, 1, "Bilinear image interpolation mode");
        VALUE_INTERPOLATION_BICUBIC = new Value(SunHints.KEY_INTERPOLATION, 2, "Bicubic image interpolation mode");
        KEY_ALPHA_INTERPOLATION = new Key(6, "Alpha blending interpolation method key");
        VALUE_ALPHA_INTERPOLATION_SPEED = new Value(SunHints.KEY_ALPHA_INTERPOLATION, 1, "Fastest alpha blending methods");
        VALUE_ALPHA_INTERPOLATION_QUALITY = new Value(SunHints.KEY_ALPHA_INTERPOLATION, 2, "Highest quality alpha blending methods");
        VALUE_ALPHA_INTERPOLATION_DEFAULT = new Value(SunHints.KEY_ALPHA_INTERPOLATION, 0, "Default alpha blending methods");
        KEY_COLOR_RENDERING = new Key(7, "Color rendering quality key");
        VALUE_COLOR_RENDER_SPEED = new Value(SunHints.KEY_COLOR_RENDERING, 1, "Fastest color rendering mode");
        VALUE_COLOR_RENDER_QUALITY = new Value(SunHints.KEY_COLOR_RENDERING, 2, "Highest quality color rendering mode");
        VALUE_COLOR_RENDER_DEFAULT = new Value(SunHints.KEY_COLOR_RENDERING, 0, "Default color rendering mode");
        KEY_STROKE_CONTROL = new Key(8, "Stroke normalization control key");
        VALUE_STROKE_DEFAULT = new Value(SunHints.KEY_STROKE_CONTROL, 0, "Default stroke normalization");
        VALUE_STROKE_NORMALIZE = new Value(SunHints.KEY_STROKE_CONTROL, 1, "Normalize strokes for consistent rendering");
        VALUE_STROKE_PURE = new Value(SunHints.KEY_STROKE_CONTROL, 2, "Pure stroke conversion for accurate paths");
        KEY_RESOLUTION_VARIANT = new Key(9, "Global image resolution variant key");
        VALUE_RESOLUTION_VARIANT_DEFAULT = new Value(SunHints.KEY_RESOLUTION_VARIANT, 0, "Choose image resolutions based on a default heuristic");
        VALUE_RESOLUTION_VARIANT_OFF = new Value(SunHints.KEY_RESOLUTION_VARIANT, 1, "Use only the standard resolution of an image");
        VALUE_RESOLUTION_VARIANT_ON = new Value(SunHints.KEY_RESOLUTION_VARIANT, 2, "Always use resolution-specific variants of images");
        KEY_TEXT_ANTIALIAS_LCD_CONTRAST = new LCDContrastKey(100, "Text-specific LCD contrast key");
    }
    
    public static class Key extends RenderingHints.Key
    {
        String description;
        
        public Key(final int n, final String description) {
            super(n);
            this.description = description;
        }
        
        public final int getIndex() {
            return this.intKey();
        }
        
        @Override
        public final String toString() {
            return this.description;
        }
        
        @Override
        public boolean isCompatibleValue(final Object o) {
            return o instanceof Value && ((Value)o).isCompatibleKey(this);
        }
    }
    
    public static class Value
    {
        private Key myKey;
        private int index;
        private String description;
        private static Value[][] ValueObjects;
        
        private static synchronized void register(final Key key, final Value value) {
            final int index = key.getIndex();
            final int index2 = value.getIndex();
            if (Value.ValueObjects[index][index2] != null) {
                throw new InternalError("duplicate index: " + index2);
            }
            Value.ValueObjects[index][index2] = value;
        }
        
        public static Value get(final int n, final int n2) {
            return Value.ValueObjects[n][n2];
        }
        
        public Value(final Key myKey, final int index, final String description) {
            this.myKey = myKey;
            this.index = index;
            this.description = description;
            register(myKey, this);
        }
        
        public final int getIndex() {
            return this.index;
        }
        
        @Override
        public final String toString() {
            return this.description;
        }
        
        public final boolean isCompatibleKey(final Key key) {
            return this.myKey == key;
        }
        
        @Override
        public final int hashCode() {
            return System.identityHashCode(this);
        }
        
        @Override
        public final boolean equals(final Object o) {
            return this == o;
        }
        
        static {
            Value.ValueObjects = new Value[10][8];
        }
    }
    
    public static class LCDContrastKey extends Key
    {
        public LCDContrastKey(final int n, final String s) {
            super(n, s);
        }
        
        @Override
        public final boolean isCompatibleValue(final Object o) {
            if (o instanceof Integer) {
                final int intValue = (int)o;
                return intValue >= 100 && intValue <= 250;
            }
            return false;
        }
    }
}
