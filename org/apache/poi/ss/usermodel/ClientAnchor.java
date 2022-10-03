package org.apache.poi.ss.usermodel;

import org.apache.poi.util.Internal;

public interface ClientAnchor
{
    short getCol1();
    
    void setCol1(final int p0);
    
    short getCol2();
    
    void setCol2(final int p0);
    
    int getRow1();
    
    void setRow1(final int p0);
    
    int getRow2();
    
    void setRow2(final int p0);
    
    int getDx1();
    
    void setDx1(final int p0);
    
    int getDy1();
    
    void setDy1(final int p0);
    
    int getDy2();
    
    void setDy2(final int p0);
    
    int getDx2();
    
    void setDx2(final int p0);
    
    void setAnchorType(final AnchorType p0);
    
    AnchorType getAnchorType();
    
    public enum AnchorType
    {
        MOVE_AND_RESIZE(0), 
        DONT_MOVE_DO_RESIZE(1), 
        MOVE_DONT_RESIZE(2), 
        DONT_MOVE_AND_RESIZE(3);
        
        public final short value;
        
        private AnchorType(final int value) {
            this.value = (short)value;
        }
        
        @Internal
        public static AnchorType byId(final int value) {
            return values()[value];
        }
    }
}
