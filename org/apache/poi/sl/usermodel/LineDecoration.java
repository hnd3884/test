package org.apache.poi.sl.usermodel;

public interface LineDecoration
{
    DecorationShape getHeadShape();
    
    DecorationSize getHeadWidth();
    
    DecorationSize getHeadLength();
    
    DecorationShape getTailShape();
    
    DecorationSize getTailWidth();
    
    DecorationSize getTailLength();
    
    public enum DecorationShape
    {
        NONE(0, 1), 
        TRIANGLE(1, 2), 
        STEALTH(2, 3), 
        DIAMOND(3, 4), 
        OVAL(4, 5), 
        ARROW(5, 6);
        
        public final int nativeId;
        public final int ooxmlId;
        
        private DecorationShape(final int nativeId, final int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }
        
        public static DecorationShape fromNativeId(final int nativeId) {
            for (final DecorationShape ld : values()) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }
        
        public static DecorationShape fromOoxmlId(final int ooxmlId) {
            for (final DecorationShape ds : values()) {
                if (ds.ooxmlId == ooxmlId) {
                    return ds;
                }
            }
            return null;
        }
    }
    
    public enum DecorationSize
    {
        SMALL(0, 1), 
        MEDIUM(1, 2), 
        LARGE(2, 3);
        
        public final int nativeId;
        public final int ooxmlId;
        
        private DecorationSize(final int nativeId, final int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }
        
        public static DecorationSize fromNativeId(final int nativeId) {
            for (final DecorationSize ld : values()) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }
        
        public static DecorationSize fromOoxmlId(final int ooxmlId) {
            for (final DecorationSize ds : values()) {
                if (ds.ooxmlId == ooxmlId) {
                    return ds;
                }
            }
            return null;
        }
    }
}
