package org.apache.poi.sl.usermodel;

public interface TabStop
{
    double getPositionInPoints();
    
    void setPositionInPoints(final double p0);
    
    TabStopType getType();
    
    void setType(final TabStopType p0);
    
    public enum TabStopType
    {
        LEFT(0, 1), 
        CENTER(1, 2), 
        RIGHT(2, 3), 
        DECIMAL(3, 4);
        
        public final int nativeId;
        public final int ooxmlId;
        
        private TabStopType(final int nativeId, final int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }
        
        public static TabStopType fromNativeId(final int nativeId) {
            for (final TabStopType tst : values()) {
                if (tst.nativeId == nativeId) {
                    return tst;
                }
            }
            return null;
        }
        
        public static TabStopType fromOoxmlId(final int ooxmlId) {
            for (final TabStopType tst : values()) {
                if (tst.ooxmlId == ooxmlId) {
                    return tst;
                }
            }
            return null;
        }
    }
}
