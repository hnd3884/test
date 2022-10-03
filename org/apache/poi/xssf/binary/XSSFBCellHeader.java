package org.apache.poi.xssf.binary;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Internal;

@Internal
class XSSFBCellHeader
{
    public static int length;
    private int rowNum;
    private int colNum;
    private int styleIdx;
    private boolean showPhonetic;
    
    public static void parse(final byte[] data, int offset, final int currentRow, final XSSFBCellHeader cell) {
        final int colNum = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        offset += 4;
        final int styleIdx = XSSFBUtils.get24BitInt(data, offset);
        offset += 3;
        final boolean showPhonetic = false;
        cell.reset(currentRow, colNum, styleIdx, showPhonetic);
    }
    
    public void reset(final int rowNum, final int colNum, final int styleIdx, final boolean showPhonetic) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.styleIdx = styleIdx;
        this.showPhonetic = showPhonetic;
    }
    
    int getColNum() {
        return this.colNum;
    }
    
    int getStyleIdx() {
        return this.styleIdx;
    }
    
    static {
        XSSFBCellHeader.length = 8;
    }
}
