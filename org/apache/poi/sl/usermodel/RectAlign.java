package org.apache.poi.sl.usermodel;

public enum RectAlign
{
    TOP_LEFT("tl"), 
    TOP("t"), 
    TOP_RIGHT("tr"), 
    LEFT("l"), 
    CENTER("ctr"), 
    RIGHT("r"), 
    BOTTOM_LEFT("bl"), 
    BOTTOM("b"), 
    BOTTOM_RIGHT("br");
    
    private final String dir;
    
    private RectAlign(final String dir) {
        this.dir = dir;
    }
    
    @Override
    public String toString() {
        return this.dir;
    }
}
