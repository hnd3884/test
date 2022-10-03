package org.apache.poi.xssf.binary;

import java.util.Queue;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.LittleEndian;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.util.Internal;

@Internal
public class XSSFBSheetHandler extends XSSFBParser
{
    private static final int CHECK_ALL_ROWS = -1;
    private final SharedStrings stringsTable;
    private final XSSFSheetXMLHandler.SheetContentsHandler handler;
    private final XSSFBStylesTable styles;
    private final XSSFBCommentsTable comments;
    private final DataFormatter dataFormatter;
    private final boolean formulasNotResults;
    private int lastEndedRow;
    private int lastStartedRow;
    private int currentRow;
    private byte[] rkBuffer;
    private XSSFBCellRange hyperlinkCellRange;
    private StringBuilder xlWideStringBuffer;
    private final XSSFBCellHeader cellBuffer;
    
    public XSSFBSheetHandler(final InputStream is, final XSSFBStylesTable styles, final XSSFBCommentsTable comments, final SharedStrings strings, final XSSFSheetXMLHandler.SheetContentsHandler sheetContentsHandler, final DataFormatter dataFormatter, final boolean formulasNotResults) {
        super(is);
        this.lastEndedRow = -1;
        this.lastStartedRow = -1;
        this.rkBuffer = new byte[8];
        this.xlWideStringBuffer = new StringBuilder();
        this.cellBuffer = new XSSFBCellHeader();
        this.styles = styles;
        this.comments = comments;
        this.stringsTable = strings;
        this.handler = sheetContentsHandler;
        this.dataFormatter = dataFormatter;
        this.formulasNotResults = formulasNotResults;
    }
    
    @Override
    public void handleRecord(final int id, final byte[] data) throws XSSFBParseException {
        final XSSFBRecordType type = XSSFBRecordType.lookup(id);
        switch (type) {
            case BrtRowHdr: {
                final int rw = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 0));
                if (rw > 1048576) {
                    throw new XSSFBParseException("Row number beyond allowable range: " + rw);
                }
                this.checkMissedComments(this.currentRow = rw);
                this.startRow(this.currentRow);
                break;
            }
            case BrtCellIsst: {
                this.handleBrtCellIsst(data);
                break;
            }
            case BrtCellSt: {
                this.handleCellSt(data);
                break;
            }
            case BrtCellRk: {
                this.handleCellRk(data);
                break;
            }
            case BrtCellReal: {
                this.handleCellReal(data);
                break;
            }
            case BrtCellBool: {
                this.handleBoolean(data);
                break;
            }
            case BrtCellError: {
                this.handleCellError(data);
                break;
            }
            case BrtCellBlank: {
                this.beforeCellValue(data);
                break;
            }
            case BrtFmlaString: {
                this.handleFmlaString(data);
                break;
            }
            case BrtFmlaNum: {
                this.handleFmlaNum(data);
                break;
            }
            case BrtFmlaError: {
                this.handleFmlaError(data);
                break;
            }
            case BrtEndSheetData: {
                this.checkMissedComments(-1);
                this.endRow(this.lastStartedRow);
                break;
            }
            case BrtBeginHeaderFooter: {
                this.handleHeaderFooter(data);
                break;
            }
        }
    }
    
    private void beforeCellValue(final byte[] data) {
        XSSFBCellHeader.parse(data, 0, this.currentRow, this.cellBuffer);
        this.checkMissedComments(this.currentRow, this.cellBuffer.getColNum());
    }
    
    private void handleCellValue(final String formattedValue) {
        final CellAddress cellAddress = new CellAddress(this.currentRow, this.cellBuffer.getColNum());
        XSSFBComment comment = null;
        if (this.comments != null) {
            comment = this.comments.get(cellAddress);
        }
        this.handler.cell(cellAddress.formatAsString(), formattedValue, comment);
    }
    
    private void handleFmlaNum(final byte[] data) {
        this.beforeCellValue(data);
        final double val = LittleEndian.getDouble(data, XSSFBCellHeader.length);
        this.handleCellValue(this.formatVal(val, this.cellBuffer.getStyleIdx()));
    }
    
    private void handleCellSt(final byte[] data) {
        this.beforeCellValue(data);
        this.xlWideStringBuffer.setLength(0);
        XSSFBUtils.readXLWideString(data, XSSFBCellHeader.length, this.xlWideStringBuffer);
        this.handleCellValue(this.xlWideStringBuffer.toString());
    }
    
    private void handleFmlaString(final byte[] data) {
        this.beforeCellValue(data);
        this.xlWideStringBuffer.setLength(0);
        XSSFBUtils.readXLWideString(data, XSSFBCellHeader.length, this.xlWideStringBuffer);
        this.handleCellValue(this.xlWideStringBuffer.toString());
    }
    
    private void handleCellError(final byte[] data) {
        this.beforeCellValue(data);
        this.handleCellValue("ERROR");
    }
    
    private void handleFmlaError(final byte[] data) {
        this.beforeCellValue(data);
        this.handleCellValue("ERROR");
    }
    
    private void handleBoolean(final byte[] data) {
        this.beforeCellValue(data);
        final String formattedVal = (data[XSSFBCellHeader.length] == 1) ? "TRUE" : "FALSE";
        this.handleCellValue(formattedVal);
    }
    
    private void handleCellReal(final byte[] data) {
        this.beforeCellValue(data);
        final double val = LittleEndian.getDouble(data, XSSFBCellHeader.length);
        this.handleCellValue(this.formatVal(val, this.cellBuffer.getStyleIdx()));
    }
    
    private void handleCellRk(final byte[] data) {
        this.beforeCellValue(data);
        final double val = this.rkNumber(data, XSSFBCellHeader.length);
        this.handleCellValue(this.formatVal(val, this.cellBuffer.getStyleIdx()));
    }
    
    private String formatVal(final double val, final int styleIdx) {
        String formatString = this.styles.getNumberFormatString(styleIdx);
        short styleIndex = this.styles.getNumberFormatIndex(styleIdx);
        if (formatString == null) {
            formatString = BuiltinFormats.getBuiltinFormat(0);
            styleIndex = 0;
        }
        return this.dataFormatter.formatRawCellContents(val, (int)styleIndex, formatString);
    }
    
    private void handleBrtCellIsst(final byte[] data) {
        this.beforeCellValue(data);
        final int idx = XSSFBUtils.castToInt(LittleEndian.getUInt(data, XSSFBCellHeader.length));
        final RichTextString rtss = this.stringsTable.getItemAt(idx);
        this.handleCellValue(rtss.getString());
    }
    
    private void handleHeaderFooter(final byte[] data) {
        final XSSFBHeaderFooters headerFooter = XSSFBHeaderFooters.parse(data);
        this.outputHeaderFooter(headerFooter.getHeader());
        this.outputHeaderFooter(headerFooter.getFooter());
        this.outputHeaderFooter(headerFooter.getHeaderEven());
        this.outputHeaderFooter(headerFooter.getFooterEven());
        this.outputHeaderFooter(headerFooter.getHeaderFirst());
        this.outputHeaderFooter(headerFooter.getFooterFirst());
    }
    
    private void outputHeaderFooter(final XSSFBHeaderFooter headerFooter) {
        final String text = headerFooter.getString();
        if (text != null && text.trim().length() > 0) {
            this.handler.headerFooter(text, headerFooter.isHeader(), headerFooter.getHeaderFooterTypeLabel());
        }
    }
    
    private void checkMissedComments(final int currentRow, final int colNum) {
        if (this.comments == null) {
            return;
        }
        final Queue<CellAddress> queue = this.comments.getAddresses();
        while (queue.size() > 0) {
            CellAddress cellAddress = queue.peek();
            if (cellAddress.getRow() == currentRow && cellAddress.getColumn() < colNum) {
                cellAddress = queue.remove();
                this.dumpEmptyCellComment(cellAddress, this.comments.get(cellAddress));
            }
            else {
                if (cellAddress.getRow() == currentRow && cellAddress.getColumn() == colNum) {
                    queue.remove();
                    return;
                }
                if (cellAddress.getRow() == currentRow && cellAddress.getColumn() > colNum) {
                    return;
                }
                if (cellAddress.getRow() > currentRow) {
                    return;
                }
                continue;
            }
        }
    }
    
    private void checkMissedComments(final int currentRow) {
        if (this.comments == null) {
            return;
        }
        final Queue<CellAddress> queue = this.comments.getAddresses();
        int lastInterpolatedRow = -1;
        while (queue.size() > 0) {
            CellAddress cellAddress = queue.peek();
            if (currentRow != -1 && cellAddress.getRow() >= currentRow) {
                break;
            }
            cellAddress = queue.remove();
            if (cellAddress.getRow() != lastInterpolatedRow) {
                this.startRow(cellAddress.getRow());
            }
            this.dumpEmptyCellComment(cellAddress, this.comments.get(cellAddress));
            lastInterpolatedRow = cellAddress.getRow();
        }
    }
    
    private void startRow(final int row) {
        if (row == this.lastStartedRow) {
            return;
        }
        if (this.lastStartedRow != this.lastEndedRow) {
            this.endRow(this.lastStartedRow);
        }
        this.handler.startRow(row);
        this.lastStartedRow = row;
    }
    
    private void endRow(final int row) {
        if (this.lastEndedRow == row) {
            return;
        }
        this.handler.endRow(row);
        this.lastEndedRow = row;
    }
    
    private void dumpEmptyCellComment(final CellAddress cellAddress, final XSSFBComment comment) {
        this.handler.cell(cellAddress.formatAsString(), null, comment);
    }
    
    private double rkNumber(final byte[] data, final int offset) {
        byte b0 = data[offset];
        final boolean numDivBy100 = (b0 & 0x1) == 0x1;
        final boolean floatingPoint = (b0 >> 1 & 0x1) == 0x0;
        b0 &= 0xFFFFFFFE;
        b0 &= 0xFFFFFFFD;
        this.rkBuffer[4] = b0;
        System.arraycopy(data, offset + 1, this.rkBuffer, 5, 3);
        double d = 0.0;
        if (floatingPoint) {
            d = LittleEndian.getDouble(this.rkBuffer);
        }
        else {
            final int rawInt = LittleEndian.getInt(this.rkBuffer, 4);
            d = rawInt >> 2;
        }
        d = (numDivBy100 ? (d / 100.0) : d);
        return d;
    }
    
    public interface SheetContentsHandler extends XSSFSheetXMLHandler.SheetContentsHandler
    {
        void hyperlinkCell(final String p0, final String p1, final String p2, final String p3, final XSSFComment p4);
    }
}
