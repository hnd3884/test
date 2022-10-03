package org.apache.poi.sl.usermodel;

public interface StrokeStyle
{
    PaintStyle getPaint();
    
    LineCap getLineCap();
    
    LineDash getLineDash();
    
    LineCompound getLineCompound();
    
    double getLineWidth();
    
    public enum LineCap
    {
        ROUND(0, 1), 
        SQUARE(1, 2), 
        FLAT(2, 3);
        
        public final int nativeId;
        public final int ooxmlId;
        
        private LineCap(final int nativeId, final int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }
        
        public static LineCap fromNativeId(final int nativeId) {
            for (final LineCap ld : values()) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }
        
        public static LineCap fromOoxmlId(final int ooxmlId) {
            for (final LineCap lc : values()) {
                if (lc.ooxmlId == ooxmlId) {
                    return lc;
                }
            }
            return null;
        }
    }
    
    public enum LineDash
    {
        SOLID(1, 1, (int[])null), 
        DOT(6, 2, new int[] { 1, 1 }), 
        DASH(7, 3, new int[] { 3, 4 }), 
        DASH_DOT(9, 5, new int[] { 4, 3, 1, 3 }), 
        LG_DASH(8, 4, new int[] { 8, 3 }), 
        LG_DASH_DOT(10, 6, new int[] { 8, 3, 1, 3 }), 
        LG_DASH_DOT_DOT(11, 7, new int[] { 8, 3, 1, 3, 1, 3 }), 
        SYS_DASH(2, 8, new int[] { 2, 2 }), 
        SYS_DOT(3, 9, new int[] { 1, 1 }), 
        SYS_DASH_DOT(4, 10, new int[] { 2, 2, 1, 1 }), 
        SYS_DASH_DOT_DOT(5, 11, new int[] { 2, 2, 1, 1, 1, 1 });
        
        public final int[] pattern;
        public final int nativeId;
        public final int ooxmlId;
        
        private LineDash(final int nativeId, final int ooxmlId, final int[] pattern) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
            this.pattern = (int[])((pattern == null || pattern.length == 0) ? null : pattern);
        }
        
        public static LineDash fromNativeId(final int nativeId) {
            for (final LineDash ld : values()) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }
        
        public static LineDash fromOoxmlId(final int ooxmlId) {
            for (final LineDash ld : values()) {
                if (ld.ooxmlId == ooxmlId) {
                    return ld;
                }
            }
            return null;
        }
    }
    
    public enum LineCompound
    {
        SINGLE(0, 1), 
        DOUBLE(1, 2), 
        THICK_THIN(2, 3), 
        THIN_THICK(3, 4), 
        TRIPLE(4, 5);
        
        public final int nativeId;
        public final int ooxmlId;
        
        private LineCompound(final int nativeId, final int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }
        
        public static LineCompound fromNativeId(final int nativeId) {
            for (final LineCompound lc : values()) {
                if (lc.nativeId == nativeId) {
                    return lc;
                }
            }
            return null;
        }
        
        public static LineCompound fromOoxmlId(final int ooxmlId) {
            for (final LineCompound lc : values()) {
                if (lc.ooxmlId == ooxmlId) {
                    return lc;
                }
            }
            return null;
        }
    }
}
