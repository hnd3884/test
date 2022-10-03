package org.apache.poi.ss.util;

public class PaneInformation
{
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_UPPER_LEFT = 3;
    private final short x;
    private final short y;
    private final short topRow;
    private final short leftColumn;
    private final byte activePane;
    private final boolean frozen;
    
    public PaneInformation(final short x, final short y, final short top, final short left, final byte active, final boolean frozen) {
        this.x = x;
        this.y = y;
        this.topRow = top;
        this.leftColumn = left;
        this.activePane = active;
        this.frozen = frozen;
    }
    
    public short getVerticalSplitPosition() {
        return this.x;
    }
    
    public short getHorizontalSplitPosition() {
        return this.y;
    }
    
    public short getHorizontalSplitTopRow() {
        return this.topRow;
    }
    
    public short getVerticalSplitLeftColumn() {
        return this.leftColumn;
    }
    
    public byte getActivePane() {
        return this.activePane;
    }
    
    public boolean isFreezePane() {
        return this.frozen;
    }
}
