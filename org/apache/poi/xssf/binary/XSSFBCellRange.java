package org.apache.poi.xssf.binary;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Internal;

@Internal
class XSSFBCellRange
{
    public static final int length = 16;
    int firstRow;
    int lastRow;
    int firstCol;
    int lastCol;
    
    public static XSSFBCellRange parse(final byte[] data, int offset, XSSFBCellRange cellRange) {
        if (cellRange == null) {
            cellRange = new XSSFBCellRange();
        }
        cellRange.firstRow = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        offset += 4;
        cellRange.lastRow = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        offset += 4;
        cellRange.firstCol = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        offset += 4;
        cellRange.lastCol = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        return cellRange;
    }
}
