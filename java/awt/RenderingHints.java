package java.awt;

import java.lang.ref.WeakReference;
import sun.awt.SunHints;
import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class RenderingHints implements Map<Object, Object>, Cloneable
{
    HashMap<Object, Object> hintmap;
    public static final Key KEY_ANTIALIASING;
    public static final Object VALUE_ANTIALIAS_ON;
    public static final Object VALUE_ANTIALIAS_OFF;
    public static final Object VALUE_ANTIALIAS_DEFAULT;
    public static final Key KEY_RENDERING;
    public static final Object VALUE_RENDER_SPEED;
    public static final Object VALUE_RENDER_QUALITY;
    public static final Object VALUE_RENDER_DEFAULT;
    public static final Key KEY_DITHERING;
    public static final Object VALUE_DITHER_DISABLE;
    public static final Object VALUE_DITHER_ENABLE;
    public static final Object VALUE_DITHER_DEFAULT;
    public static final Key KEY_TEXT_ANTIALIASING;
    public static final Object VALUE_TEXT_ANTIALIAS_ON;
    public static final Object VALUE_TEXT_ANTIALIAS_OFF;
    public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT;
    public static final Object VALUE_TEXT_ANTIALIAS_GASP;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB;
    public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR;
    public static final Key KEY_TEXT_LCD_CONTRAST;
    public static final Key KEY_FRACTIONALMETRICS;
    public static final Object VALUE_FRACTIONALMETRICS_OFF;
    public static final Object VALUE_FRACTIONALMETRICS_ON;
    public static final Object VALUE_FRACTIONALMETRICS_DEFAULT;
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
    
    public RenderingHints(final Map<Key, ?> map) {
        this.hintmap = new HashMap<Object, Object>(7);
        if (map != null) {
            this.hintmap.putAll(map);
        }
    }
    
    public RenderingHints(final Key key, final Object o) {
        (this.hintmap = new HashMap<Object, Object>(7)).put(key, o);
    }
    
    @Override
    public int size() {
        return this.hintmap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.hintmap.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.hintmap.containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.hintmap.containsValue(o);
    }
    
    @Override
    public Object get(final Object o) {
        return this.hintmap.get(o);
    }
    
    @Override
    public Object put(final Object o, final Object o2) {
        if (!((Key)o).isCompatibleValue(o2)) {
            throw new IllegalArgumentException(o2 + " incompatible with " + o);
        }
        return this.hintmap.put(o, o2);
    }
    
    public void add(final RenderingHints renderingHints) {
        this.hintmap.putAll(renderingHints.hintmap);
    }
    
    @Override
    public void clear() {
        this.hintmap.clear();
    }
    
    @Override
    public Object remove(final Object o) {
        return this.hintmap.remove(o);
    }
    
    @Override
    public void putAll(final Map<?, ?> map) {
        if (RenderingHints.class.isInstance(map)) {
            for (final Entry entry : map.entrySet()) {
                this.hintmap.put(entry.getKey(), entry.getValue());
            }
        }
        else {
            for (final Entry entry2 : map.entrySet()) {
                this.put(entry2.getKey(), entry2.getValue());
            }
        }
    }
    
    @Override
    public Set<Object> keySet() {
        return this.hintmap.keySet();
    }
    
    @Override
    public Collection<Object> values() {
        return this.hintmap.values();
    }
    
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableMap((Map<?, ?>)this.hintmap).entrySet();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof RenderingHints) {
            return this.hintmap.equals(((RenderingHints)o).hintmap);
        }
        return o instanceof Map && this.hintmap.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.hintmap.hashCode();
    }
    
    public Object clone() {
        RenderingHints renderingHints;
        try {
            renderingHints = (RenderingHints)super.clone();
            if (this.hintmap != null) {
                renderingHints.hintmap = (HashMap)this.hintmap.clone();
            }
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
        return renderingHints;
    }
    
    @Override
    public String toString() {
        if (this.hintmap == null) {
            return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + " (0 hints)";
        }
        return this.hintmap.toString();
    }
    
    static {
        KEY_ANTIALIASING = SunHints.KEY_ANTIALIASING;
        VALUE_ANTIALIAS_ON = SunHints.VALUE_ANTIALIAS_ON;
        VALUE_ANTIALIAS_OFF = SunHints.VALUE_ANTIALIAS_OFF;
        VALUE_ANTIALIAS_DEFAULT = SunHints.VALUE_ANTIALIAS_DEFAULT;
        KEY_RENDERING = SunHints.KEY_RENDERING;
        VALUE_RENDER_SPEED = SunHints.VALUE_RENDER_SPEED;
        VALUE_RENDER_QUALITY = SunHints.VALUE_RENDER_QUALITY;
        VALUE_RENDER_DEFAULT = SunHints.VALUE_RENDER_DEFAULT;
        KEY_DITHERING = SunHints.KEY_DITHERING;
        VALUE_DITHER_DISABLE = SunHints.VALUE_DITHER_DISABLE;
        VALUE_DITHER_ENABLE = SunHints.VALUE_DITHER_ENABLE;
        VALUE_DITHER_DEFAULT = SunHints.VALUE_DITHER_DEFAULT;
        KEY_TEXT_ANTIALIASING = SunHints.KEY_TEXT_ANTIALIASING;
        VALUE_TEXT_ANTIALIAS_ON = SunHints.VALUE_TEXT_ANTIALIAS_ON;
        VALUE_TEXT_ANTIALIAS_OFF = SunHints.VALUE_TEXT_ANTIALIAS_OFF;
        VALUE_TEXT_ANTIALIAS_DEFAULT = SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        VALUE_TEXT_ANTIALIAS_GASP = SunHints.VALUE_TEXT_ANTIALIAS_GASP;
        VALUE_TEXT_ANTIALIAS_LCD_HRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
        VALUE_TEXT_ANTIALIAS_LCD_HBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
        VALUE_TEXT_ANTIALIAS_LCD_VRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
        VALUE_TEXT_ANTIALIAS_LCD_VBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
        KEY_TEXT_LCD_CONTRAST = SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST;
        KEY_FRACTIONALMETRICS = SunHints.KEY_FRACTIONALMETRICS;
        VALUE_FRACTIONALMETRICS_OFF = SunHints.VALUE_FRACTIONALMETRICS_OFF;
        VALUE_FRACTIONALMETRICS_ON = SunHints.VALUE_FRACTIONALMETRICS_ON;
        VALUE_FRACTIONALMETRICS_DEFAULT = SunHints.VALUE_FRACTIONALMETRICS_DEFAULT;
        KEY_INTERPOLATION = SunHints.KEY_INTERPOLATION;
        VALUE_INTERPOLATION_NEAREST_NEIGHBOR = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        VALUE_INTERPOLATION_BILINEAR = SunHints.VALUE_INTERPOLATION_BILINEAR;
        VALUE_INTERPOLATION_BICUBIC = SunHints.VALUE_INTERPOLATION_BICUBIC;
        KEY_ALPHA_INTERPOLATION = SunHints.KEY_ALPHA_INTERPOLATION;
        VALUE_ALPHA_INTERPOLATION_SPEED = SunHints.VALUE_ALPHA_INTERPOLATION_SPEED;
        VALUE_ALPHA_INTERPOLATION_QUALITY = SunHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
        VALUE_ALPHA_INTERPOLATION_DEFAULT = SunHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
        KEY_COLOR_RENDERING = SunHints.KEY_COLOR_RENDERING;
        VALUE_COLOR_RENDER_SPEED = SunHints.VALUE_COLOR_RENDER_SPEED;
        VALUE_COLOR_RENDER_QUALITY = SunHints.VALUE_COLOR_RENDER_QUALITY;
        VALUE_COLOR_RENDER_DEFAULT = SunHints.VALUE_COLOR_RENDER_DEFAULT;
        KEY_STROKE_CONTROL = SunHints.KEY_STROKE_CONTROL;
        VALUE_STROKE_DEFAULT = SunHints.VALUE_STROKE_DEFAULT;
        VALUE_STROKE_NORMALIZE = SunHints.VALUE_STROKE_NORMALIZE;
        VALUE_STROKE_PURE = SunHints.VALUE_STROKE_PURE;
    }
    
    public abstract static class Key
    {
        private static HashMap<Object, Object> identitymap;
        private int privatekey;
        
        private String getIdentity() {
            return this.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this.getClass())) + ":" + Integer.toHexString(this.privatekey);
        }
        
        private static synchronized void recordIdentity(final Key key) {
            final String identity = key.getIdentity();
            final Object value = Key.identitymap.get(identity);
            if (value != null) {
                final Key key2 = (Key)((WeakReference)value).get();
                if (key2 != null && key2.getClass() == key.getClass()) {
                    throw new IllegalArgumentException((Object)identity + " already registered");
                }
            }
            Key.identitymap.put(identity, new WeakReference(key));
        }
        
        protected Key(final int privatekey) {
            this.privatekey = privatekey;
            recordIdentity(this);
        }
        
        public abstract boolean isCompatibleValue(final Object p0);
        
        protected final int intKey() {
            return this.privatekey;
        }
        
        @Override
        public final int hashCode() {
            return super.hashCode();
        }
        
        @Override
        public final boolean equals(final Object o) {
            return this == o;
        }
        
        static {
            Key.identitymap = new HashMap<Object, Object>(17);
        }
    }
}
