package com.lowagie.text.pdf;

import com.lowagie.text.pdf.events.PdfPTableEventForwarder;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Phrase;
import java.util.ArrayList;
import com.lowagie.text.LargeElement;

public class PdfPTable implements LargeElement
{
    public static final int BASECANVAS = 0;
    public static final int BACKGROUNDCANVAS = 1;
    public static final int LINECANVAS = 2;
    public static final int TEXTCANVAS = 3;
    protected ArrayList rows;
    protected float totalHeight;
    protected PdfPCell[] currentRow;
    protected int currentRowIdx;
    protected PdfPCell defaultCell;
    protected float totalWidth;
    protected float[] relativeWidths;
    protected float[] absoluteWidths;
    protected PdfPTableEvent tableEvent;
    protected int headerRows;
    protected float widthPercentage;
    private int horizontalAlignment;
    private boolean skipFirstHeader;
    private boolean skipLastFooter;
    protected boolean isColspan;
    protected int runDirection;
    private boolean lockedWidth;
    private boolean splitRows;
    protected float spacingBefore;
    protected float spacingAfter;
    private boolean[] extendLastRow;
    private boolean headersInEvent;
    private boolean splitLate;
    private boolean keepTogether;
    protected boolean complete;
    private int footerRows;
    protected boolean rowCompleted;
    
    protected PdfPTable() {
        this.rows = new ArrayList();
        this.totalHeight = 0.0f;
        this.currentRowIdx = 0;
        this.defaultCell = new PdfPCell((Phrase)null);
        this.totalWidth = 0.0f;
        this.widthPercentage = 80.0f;
        this.horizontalAlignment = 1;
        this.skipFirstHeader = false;
        this.skipLastFooter = false;
        this.isColspan = false;
        this.runDirection = 0;
        this.lockedWidth = false;
        this.splitRows = true;
        this.extendLastRow = new boolean[] { false, false };
        this.splitLate = true;
        this.complete = true;
        this.rowCompleted = true;
    }
    
    public PdfPTable(final float[] relativeWidths) {
        this.rows = new ArrayList();
        this.totalHeight = 0.0f;
        this.currentRowIdx = 0;
        this.defaultCell = new PdfPCell((Phrase)null);
        this.totalWidth = 0.0f;
        this.widthPercentage = 80.0f;
        this.horizontalAlignment = 1;
        this.skipFirstHeader = false;
        this.skipLastFooter = false;
        this.isColspan = false;
        this.runDirection = 0;
        this.lockedWidth = false;
        this.splitRows = true;
        this.extendLastRow = new boolean[] { false, false };
        this.splitLate = true;
        this.complete = true;
        this.rowCompleted = true;
        if (relativeWidths == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("the.widths.array.in.pdfptable.constructor.can.not.be.null"));
        }
        if (relativeWidths.length == 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.widths.array.in.pdfptable.constructor.can.not.have.zero.length"));
        }
        System.arraycopy(relativeWidths, 0, this.relativeWidths = new float[relativeWidths.length], 0, relativeWidths.length);
        this.absoluteWidths = new float[relativeWidths.length];
        this.calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }
    
    public PdfPTable(final int numColumns) {
        this.rows = new ArrayList();
        this.totalHeight = 0.0f;
        this.currentRowIdx = 0;
        this.defaultCell = new PdfPCell((Phrase)null);
        this.totalWidth = 0.0f;
        this.widthPercentage = 80.0f;
        this.horizontalAlignment = 1;
        this.skipFirstHeader = false;
        this.skipLastFooter = false;
        this.isColspan = false;
        this.runDirection = 0;
        this.lockedWidth = false;
        this.splitRows = true;
        this.extendLastRow = new boolean[] { false, false };
        this.splitLate = true;
        this.complete = true;
        this.rowCompleted = true;
        if (numColumns <= 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.columns.in.pdfptable.constructor.must.be.greater.than.zero"));
        }
        this.relativeWidths = new float[numColumns];
        for (int k = 0; k < numColumns; ++k) {
            this.relativeWidths[k] = 1.0f;
        }
        this.absoluteWidths = new float[this.relativeWidths.length];
        this.calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }
    
    public PdfPTable(final PdfPTable table) {
        this.rows = new ArrayList();
        this.totalHeight = 0.0f;
        this.currentRowIdx = 0;
        this.defaultCell = new PdfPCell((Phrase)null);
        this.totalWidth = 0.0f;
        this.widthPercentage = 80.0f;
        this.horizontalAlignment = 1;
        this.skipFirstHeader = false;
        this.skipLastFooter = false;
        this.isColspan = false;
        this.runDirection = 0;
        this.lockedWidth = false;
        this.splitRows = true;
        this.extendLastRow = new boolean[] { false, false };
        this.splitLate = true;
        this.complete = true;
        this.rowCompleted = true;
        this.copyFormat(table);
        for (int k = 0; k < this.currentRow.length && table.currentRow[k] != null; ++k) {
            this.currentRow[k] = new PdfPCell(table.currentRow[k]);
        }
        for (int k = 0; k < table.rows.size(); ++k) {
            PdfPRow row = table.rows.get(k);
            if (row != null) {
                row = new PdfPRow(row);
            }
            this.rows.add(row);
        }
    }
    
    public static PdfPTable shallowCopy(final PdfPTable table) {
        final PdfPTable nt = new PdfPTable();
        nt.copyFormat(table);
        return nt;
    }
    
    protected void copyFormat(final PdfPTable sourceTable) {
        this.relativeWidths = new float[sourceTable.getNumberOfColumns()];
        this.absoluteWidths = new float[sourceTable.getNumberOfColumns()];
        System.arraycopy(sourceTable.relativeWidths, 0, this.relativeWidths, 0, this.getNumberOfColumns());
        System.arraycopy(sourceTable.absoluteWidths, 0, this.absoluteWidths, 0, this.getNumberOfColumns());
        this.totalWidth = sourceTable.totalWidth;
        this.totalHeight = sourceTable.totalHeight;
        this.currentRowIdx = 0;
        this.tableEvent = sourceTable.tableEvent;
        this.runDirection = sourceTable.runDirection;
        this.defaultCell = new PdfPCell(sourceTable.defaultCell);
        this.currentRow = new PdfPCell[sourceTable.currentRow.length];
        this.isColspan = sourceTable.isColspan;
        this.splitRows = sourceTable.splitRows;
        this.spacingAfter = sourceTable.spacingAfter;
        this.spacingBefore = sourceTable.spacingBefore;
        this.headerRows = sourceTable.headerRows;
        this.footerRows = sourceTable.footerRows;
        this.lockedWidth = sourceTable.lockedWidth;
        this.extendLastRow = sourceTable.extendLastRow;
        this.headersInEvent = sourceTable.headersInEvent;
        this.widthPercentage = sourceTable.widthPercentage;
        this.splitLate = sourceTable.splitLate;
        this.skipFirstHeader = sourceTable.skipFirstHeader;
        this.skipLastFooter = sourceTable.skipLastFooter;
        this.horizontalAlignment = sourceTable.horizontalAlignment;
        this.keepTogether = sourceTable.keepTogether;
        this.complete = sourceTable.complete;
    }
    
    public void setWidths(final float[] relativeWidths) throws DocumentException {
        if (relativeWidths.length != this.getNumberOfColumns()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        System.arraycopy(relativeWidths, 0, this.relativeWidths = new float[relativeWidths.length], 0, relativeWidths.length);
        this.absoluteWidths = new float[relativeWidths.length];
        this.totalHeight = 0.0f;
        this.calculateWidths();
        this.calculateHeights(true);
    }
    
    public void setWidths(final int[] relativeWidths) throws DocumentException {
        final float[] tb = new float[relativeWidths.length];
        for (int k = 0; k < relativeWidths.length; ++k) {
            tb[k] = (float)relativeWidths[k];
        }
        this.setWidths(tb);
    }
    
    protected void calculateWidths() {
        if (this.totalWidth <= 0.0f) {
            return;
        }
        float total = 0.0f;
        final int numCols = this.getNumberOfColumns();
        for (int k = 0; k < numCols; ++k) {
            total += this.relativeWidths[k];
        }
        for (int k = 0; k < numCols; ++k) {
            this.absoluteWidths[k] = this.totalWidth * this.relativeWidths[k] / total;
        }
    }
    
    public void setTotalWidth(final float totalWidth) {
        if (this.totalWidth == totalWidth) {
            return;
        }
        this.totalWidth = totalWidth;
        this.totalHeight = 0.0f;
        this.calculateWidths();
        this.calculateHeights(true);
    }
    
    public void setTotalWidth(final float[] columnWidth) throws DocumentException {
        if (columnWidth.length != this.getNumberOfColumns()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        this.totalWidth = 0.0f;
        for (int k = 0; k < columnWidth.length; ++k) {
            this.totalWidth += columnWidth[k];
        }
        this.setWidths(columnWidth);
    }
    
    public void setWidthPercentage(final float[] columnWidth, final Rectangle pageSize) throws DocumentException {
        if (columnWidth.length != this.getNumberOfColumns()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        float totalWidth = 0.0f;
        for (int k = 0; k < columnWidth.length; ++k) {
            totalWidth += columnWidth[k];
        }
        this.widthPercentage = totalWidth / (pageSize.getRight() - pageSize.getLeft()) * 100.0f;
        this.setWidths(columnWidth);
    }
    
    public float getTotalWidth() {
        return this.totalWidth;
    }
    
    public float calculateHeights(final boolean firsttime) {
        if (this.totalWidth <= 0.0f) {
            return 0.0f;
        }
        this.totalHeight = 0.0f;
        for (int k = 0; k < this.rows.size(); ++k) {
            this.totalHeight += this.getRowHeight(k, firsttime);
        }
        return this.totalHeight;
    }
    
    public void calculateHeightsFast() {
        this.calculateHeights(false);
    }
    
    public PdfPCell getDefaultCell() {
        return this.defaultCell;
    }
    
    public void addCell(final PdfPCell cell) {
        this.rowCompleted = false;
        final PdfPCell ncell = new PdfPCell(cell);
        int colspan = ncell.getColspan();
        colspan = Math.max(colspan, 1);
        colspan = Math.min(colspan, this.currentRow.length - this.currentRowIdx);
        ncell.setColspan(colspan);
        if (colspan != 1) {
            this.isColspan = true;
        }
        final int rdir = ncell.getRunDirection();
        if (rdir == 0) {
            ncell.setRunDirection(this.runDirection);
        }
        this.skipColsWithRowspanAbove();
        boolean cellAdded = false;
        if (this.currentRowIdx < this.currentRow.length) {
            this.currentRow[this.currentRowIdx] = ncell;
            this.currentRowIdx += colspan;
            cellAdded = true;
        }
        this.skipColsWithRowspanAbove();
        while (this.currentRowIdx >= this.currentRow.length) {
            final int numCols = this.getNumberOfColumns();
            if (this.runDirection == 3) {
                final PdfPCell[] rtlRow = new PdfPCell[numCols];
                int rev = this.currentRow.length;
                int cspan;
                for (int k = 0; k < this.currentRow.length; k += cspan - 1, ++k) {
                    final PdfPCell rcell = this.currentRow[k];
                    cspan = rcell.getColspan();
                    rev -= cspan;
                    rtlRow[rev] = rcell;
                }
                this.currentRow = rtlRow;
            }
            final PdfPRow row = new PdfPRow(this.currentRow);
            if (this.totalWidth > 0.0f) {
                row.setWidths(this.absoluteWidths);
                this.totalHeight += row.getMaxHeights();
            }
            this.rows.add(row);
            this.currentRow = new PdfPCell[numCols];
            this.currentRowIdx = 0;
            this.skipColsWithRowspanAbove();
            this.rowCompleted = true;
        }
        if (!cellAdded) {
            this.currentRow[this.currentRowIdx] = ncell;
            this.currentRowIdx += colspan;
        }
    }
    
    private void skipColsWithRowspanAbove() {
        int direction = 1;
        if (this.runDirection == 3) {
            direction = -1;
        }
        while (this.rowSpanAbove(this.rows.size(), this.currentRowIdx)) {
            this.currentRowIdx += direction;
        }
    }
    
    PdfPCell obtainCell(final int row, final int col) {
        final PdfPCell[] cells = this.rows.get(row).getCells();
        for (int i = 0; i < cells.length; ++i) {
            if (cells[i] != null && col >= i && col < i + cells[i].getColspan()) {
                return cells[i];
            }
        }
        return null;
    }
    
    boolean rowSpanAbove(final int currRow, final int currCol) {
        if (currCol >= this.getNumberOfColumns() || currCol < 0 || currRow < 1) {
            return false;
        }
        int row = currRow - 1;
        PdfPRow aboveRow = this.rows.get(row);
        if (aboveRow == null) {
            return false;
        }
        PdfPCell aboveCell;
        for (aboveCell = this.obtainCell(row, currCol); aboveCell == null && row > 0; aboveCell = this.obtainCell(row, currCol)) {
            aboveRow = this.rows.get(--row);
            if (aboveRow == null) {
                return false;
            }
        }
        int distance = currRow - row;
        if (aboveCell == null) {
            int col;
            for (col = currCol - 1, aboveCell = this.obtainCell(row, col); aboveCell == null && row > 0; aboveCell = this.obtainCell(row, --col)) {}
            return aboveCell != null && aboveCell.getRowspan() > distance;
        }
        if (aboveCell.getRowspan() == 1 && distance > 1) {
            int col = currCol - 1;
            aboveRow = this.rows.get(row + 1);
            --distance;
            for (aboveCell = aboveRow.getCells()[col]; aboveCell == null && col > 0; aboveCell = aboveRow.getCells()[--col]) {}
        }
        return aboveCell != null && aboveCell.getRowspan() > distance;
    }
    
    public void addCell(final String text) {
        this.addCell(new Phrase(text));
    }
    
    public void addCell(final PdfPTable table) {
        this.defaultCell.setTable(table);
        this.addCell(this.defaultCell);
        this.defaultCell.setTable(null);
    }
    
    public void addCell(final Image image) {
        this.defaultCell.setImage(image);
        this.addCell(this.defaultCell);
        this.defaultCell.setImage(null);
    }
    
    public void addCell(final Phrase phrase) {
        this.defaultCell.setPhrase(phrase);
        this.addCell(this.defaultCell);
        this.defaultCell.setPhrase(null);
    }
    
    public float writeSelectedRows(final int rowStart, final int rowEnd, final float xPos, final float yPos, final PdfContentByte[] canvases) {
        return this.writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvases);
    }
    
    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, final float xPos, float yPos, final PdfContentByte[] canvases) {
        if (this.totalWidth <= 0.0f) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.table.width.must.be.greater.than.zero"));
        }
        final int totalRows = this.rows.size();
        if (rowStart < 0) {
            rowStart = 0;
        }
        if (rowEnd < 0) {
            rowEnd = totalRows;
        }
        else {
            rowEnd = Math.min(rowEnd, totalRows);
        }
        if (rowStart >= rowEnd) {
            return yPos;
        }
        final int totalCols = this.getNumberOfColumns();
        if (colStart < 0) {
            colStart = 0;
        }
        else {
            colStart = Math.min(colStart, totalCols);
        }
        if (colEnd < 0) {
            colEnd = totalCols;
        }
        else {
            colEnd = Math.min(colEnd, totalCols);
        }
        final float yPosStart = yPos;
        for (int k = rowStart; k < rowEnd; ++k) {
            final PdfPRow row = this.rows.get(k);
            if (row != null) {
                row.writeCells(colStart, colEnd, xPos, yPos, canvases);
                yPos -= row.getMaxHeights();
            }
        }
        if (this.tableEvent != null && colStart == 0 && colEnd == totalCols) {
            final float[] heights = new float[rowEnd - rowStart + 1];
            heights[0] = yPosStart;
            for (int i = rowStart; i < rowEnd; ++i) {
                final PdfPRow row2 = this.rows.get(i);
                float hr = 0.0f;
                if (row2 != null) {
                    hr = row2.getMaxHeights();
                }
                heights[i - rowStart + 1] = heights[i - rowStart] - hr;
            }
            this.tableEvent.tableLayout(this, this.getEventWidths(xPos, rowStart, rowEnd, this.headersInEvent), heights, this.headersInEvent ? this.headerRows : 0, rowStart, canvases);
        }
        return yPos;
    }
    
    public float writeSelectedRows(final int rowStart, final int rowEnd, final float xPos, final float yPos, final PdfContentByte canvas) {
        return this.writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvas);
    }
    
    public float writeSelectedRows(int colStart, int colEnd, final int rowStart, final int rowEnd, final float xPos, final float yPos, final PdfContentByte canvas) {
        final int totalCols = this.getNumberOfColumns();
        if (colStart < 0) {
            colStart = 0;
        }
        else {
            colStart = Math.min(colStart, totalCols);
        }
        if (colEnd < 0) {
            colEnd = totalCols;
        }
        else {
            colEnd = Math.min(colEnd, totalCols);
        }
        final boolean clip = colStart != 0 || colEnd != totalCols;
        if (clip) {
            float w = 0.0f;
            for (int k = colStart; k < colEnd; ++k) {
                w += this.absoluteWidths[k];
            }
            canvas.saveState();
            final float lx = (colStart == 0) ? 10000.0f : 0.0f;
            final float rx = (colEnd == totalCols) ? 10000.0f : 0.0f;
            canvas.rectangle(xPos - lx, -10000.0f, w + lx + rx, 20000.0f);
            canvas.clip();
            canvas.newPath();
        }
        final PdfContentByte[] canvases = beginWritingRows(canvas);
        final float y = this.writeSelectedRows(colStart, colEnd, rowStart, rowEnd, xPos, yPos, canvases);
        endWritingRows(canvases);
        if (clip) {
            canvas.restoreState();
        }
        return y;
    }
    
    public static PdfContentByte[] beginWritingRows(final PdfContentByte canvas) {
        return new PdfContentByte[] { canvas, canvas.getDuplicate(), canvas.getDuplicate(), canvas.getDuplicate() };
    }
    
    public static void endWritingRows(final PdfContentByte[] canvases) {
        final PdfContentByte canvas = canvases[0];
        canvas.saveState();
        canvas.add(canvases[1]);
        canvas.restoreState();
        canvas.saveState();
        canvas.setLineCap(2);
        canvas.resetRGBColorStroke();
        canvas.add(canvases[2]);
        canvas.restoreState();
        canvas.add(canvases[3]);
    }
    
    public int size() {
        return this.rows.size();
    }
    
    public float getTotalHeight() {
        return this.totalHeight;
    }
    
    public float getRowHeight(final int idx) {
        return this.getRowHeight(idx, false);
    }
    
    public float getRowHeight(final int idx, final boolean firsttime) {
        if (this.totalWidth <= 0.0f || idx < 0 || idx >= this.rows.size()) {
            return 0.0f;
        }
        final PdfPRow row = this.rows.get(idx);
        if (row == null) {
            return 0.0f;
        }
        if (firsttime) {
            row.setWidths(this.absoluteWidths);
        }
        float height = row.getMaxHeights();
        for (int i = 0; i < this.relativeWidths.length; ++i) {
            if (this.rowSpanAbove(idx, i)) {
                int rs;
                for (rs = 1; this.rowSpanAbove(idx - rs, i); ++rs) {}
                final PdfPRow tmprow = this.rows.get(idx - rs);
                final PdfPCell cell = tmprow.getCells()[i];
                float tmp = 0.0f;
                if (cell != null && cell.getRowspan() == rs + 1) {
                    tmp = cell.getMaxHeight();
                    while (rs > 0) {
                        tmp -= this.getRowHeight(idx - rs);
                        --rs;
                    }
                }
                if (tmp > height) {
                    height = tmp;
                }
            }
        }
        row.setMaxHeights(height);
        return height;
    }
    
    public float getRowspanHeight(final int rowIndex, final int cellIndex) {
        if (this.totalWidth <= 0.0f || rowIndex < 0 || rowIndex >= this.rows.size()) {
            return 0.0f;
        }
        final PdfPRow row = this.rows.get(rowIndex);
        if (row == null || cellIndex >= row.getCells().length) {
            return 0.0f;
        }
        final PdfPCell cell = row.getCells()[cellIndex];
        if (cell == null) {
            return 0.0f;
        }
        float rowspanHeight = 0.0f;
        for (int j = 0; j < cell.getRowspan(); ++j) {
            rowspanHeight += this.getRowHeight(rowIndex + j);
        }
        return rowspanHeight;
    }
    
    public float getHeaderHeight() {
        float total = 0.0f;
        for (int size = Math.min(this.rows.size(), this.headerRows), k = 0; k < size; ++k) {
            final PdfPRow row = this.rows.get(k);
            if (row != null) {
                total += row.getMaxHeights();
            }
        }
        return total;
    }
    
    public float getFooterHeight() {
        float total = 0.0f;
        final int start = Math.max(0, this.headerRows - this.footerRows);
        for (int size = Math.min(this.rows.size(), this.headerRows), k = start; k < size; ++k) {
            final PdfPRow row = this.rows.get(k);
            if (row != null) {
                total += row.getMaxHeights();
            }
        }
        return total;
    }
    
    public boolean deleteRow(final int rowNumber) {
        if (rowNumber < 0 || rowNumber >= this.rows.size()) {
            return false;
        }
        if (this.totalWidth > 0.0f) {
            final PdfPRow row = this.rows.get(rowNumber);
            if (row != null) {
                this.totalHeight -= row.getMaxHeights();
            }
        }
        this.rows.remove(rowNumber);
        if (rowNumber < this.headerRows) {
            --this.headerRows;
            if (rowNumber >= this.headerRows - this.footerRows) {
                --this.footerRows;
            }
        }
        return true;
    }
    
    public boolean deleteLastRow() {
        return this.deleteRow(this.rows.size() - 1);
    }
    
    public void deleteBodyRows() {
        final ArrayList rows2 = new ArrayList();
        for (int k = 0; k < this.headerRows; ++k) {
            rows2.add(this.rows.get(k));
        }
        this.rows = rows2;
        this.totalHeight = 0.0f;
        if (this.totalWidth > 0.0f) {
            this.totalHeight = this.getHeaderHeight();
        }
    }
    
    public int getNumberOfColumns() {
        return this.relativeWidths.length;
    }
    
    public int getHeaderRows() {
        return this.headerRows;
    }
    
    public void setHeaderRows(int headerRows) {
        if (headerRows < 0) {
            headerRows = 0;
        }
        this.headerRows = headerRows;
    }
    
    @Override
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    @Override
    public int type() {
        return 23;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    public float getWidthPercentage() {
        return this.widthPercentage;
    }
    
    public void setWidthPercentage(final float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }
    
    public PdfPRow getRow(final int idx) {
        return this.rows.get(idx);
    }
    
    public ArrayList getRows() {
        return this.rows;
    }
    
    public ArrayList getRows(final int start, final int end) {
        final ArrayList list = new ArrayList();
        if (start < 0 || end > this.size()) {
            return list;
        }
        final PdfPRow firstRow = this.adjustCellsInRow(start, end);
        int colIndex = 0;
        while (colIndex < this.getNumberOfColumns()) {
            int rowIndex = start;
            while (this.rowSpanAbove(rowIndex--, colIndex)) {
                final PdfPRow row = this.getRow(rowIndex);
                if (row != null) {
                    final PdfPCell replaceCell = row.getCells()[colIndex];
                    if (replaceCell == null) {
                        continue;
                    }
                    firstRow.getCells()[colIndex] = new PdfPCell(replaceCell);
                    float extra = 0.0f;
                    for (int stop = Math.min(rowIndex + replaceCell.getRowspan(), end), j = start + 1; j < stop; ++j) {
                        extra += this.getRowHeight(j);
                    }
                    firstRow.setExtraHeight(colIndex, extra);
                    final float diff = this.getRowspanHeight(rowIndex, colIndex) - this.getRowHeight(start) - extra;
                    firstRow.getCells()[colIndex].consumeHeight(diff);
                }
            }
            final PdfPCell cell = firstRow.getCells()[colIndex];
            if (cell == null) {
                ++colIndex;
            }
            else {
                colIndex += cell.getColspan();
            }
        }
        list.add(firstRow);
        for (int i = start + 1; i < end; ++i) {
            list.add(this.adjustCellsInRow(i, end));
        }
        return list;
    }
    
    protected PdfPRow adjustCellsInRow(final int start, final int end) {
        final PdfPRow row = new PdfPRow(this.getRow(start));
        row.initExtraHeights();
        final PdfPCell[] cells = row.getCells();
        for (int i = 0; i < cells.length; ++i) {
            final PdfPCell cell = cells[i];
            if (cell != null) {
                if (cell.getRowspan() != 1) {
                    final int stop = Math.min(end, start + cell.getRowspan());
                    float extra = 0.0f;
                    for (int k = start + 1; k < stop; ++k) {
                        extra += this.getRowHeight(k);
                    }
                    row.setExtraHeight(i, extra);
                }
            }
        }
        return row;
    }
    
    public void setTableEvent(final PdfPTableEvent event) {
        if (event == null) {
            this.tableEvent = null;
        }
        else if (this.tableEvent == null) {
            this.tableEvent = event;
        }
        else if (this.tableEvent instanceof PdfPTableEventForwarder) {
            ((PdfPTableEventForwarder)this.tableEvent).addTableEvent(event);
        }
        else {
            final PdfPTableEventForwarder forward = new PdfPTableEventForwarder();
            forward.addTableEvent(this.tableEvent);
            forward.addTableEvent(event);
            this.tableEvent = forward;
        }
    }
    
    public PdfPTableEvent getTableEvent() {
        return this.tableEvent;
    }
    
    public float[] getAbsoluteWidths() {
        return this.absoluteWidths;
    }
    
    float[][] getEventWidths(final float xPos, int firstRow, int lastRow, final boolean includeHeaders) {
        if (includeHeaders) {
            firstRow = Math.max(firstRow, this.headerRows);
            lastRow = Math.max(lastRow, this.headerRows);
        }
        final float[][] widths = new float[(includeHeaders ? this.headerRows : 0) + lastRow - firstRow][];
        if (this.isColspan) {
            int n = 0;
            if (includeHeaders) {
                for (int k = 0; k < this.headerRows; ++k) {
                    final PdfPRow row = this.rows.get(k);
                    if (row == null) {
                        ++n;
                    }
                    else {
                        widths[n++] = row.getEventWidth(xPos);
                    }
                }
            }
            while (firstRow < lastRow) {
                final PdfPRow row2 = this.rows.get(firstRow);
                if (row2 == null) {
                    ++n;
                }
                else {
                    widths[n++] = row2.getEventWidth(xPos);
                }
                ++firstRow;
            }
        }
        else {
            final int numCols = this.getNumberOfColumns();
            final float[] width = new float[numCols + 1];
            width[0] = xPos;
            for (int i = 0; i < numCols; ++i) {
                width[i + 1] = width[i] + this.absoluteWidths[i];
            }
            for (int i = 0; i < widths.length; ++i) {
                widths[i] = width;
            }
        }
        return widths;
    }
    
    public boolean isSkipFirstHeader() {
        return this.skipFirstHeader;
    }
    
    public boolean isSkipLastFooter() {
        return this.skipLastFooter;
    }
    
    public void setSkipFirstHeader(final boolean skipFirstHeader) {
        this.skipFirstHeader = skipFirstHeader;
    }
    
    public void setSkipLastFooter(final boolean skipLastFooter) {
        this.skipLastFooter = skipLastFooter;
    }
    
    public void setRunDirection(final int runDirection) {
        switch (runDirection) {
            case 0:
            case 1:
            case 2:
            case 3: {
                this.runDirection = runDirection;
                return;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
            }
        }
    }
    
    public int getRunDirection() {
        return this.runDirection;
    }
    
    public boolean isLockedWidth() {
        return this.lockedWidth;
    }
    
    public void setLockedWidth(final boolean lockedWidth) {
        this.lockedWidth = lockedWidth;
    }
    
    public boolean isSplitRows() {
        return this.splitRows;
    }
    
    public void setSplitRows(final boolean splitRows) {
        this.splitRows = splitRows;
    }
    
    public void setSpacingBefore(final float spacing) {
        this.spacingBefore = spacing;
    }
    
    public void setSpacingAfter(final float spacing) {
        this.spacingAfter = spacing;
    }
    
    public float spacingBefore() {
        return this.spacingBefore;
    }
    
    public float spacingAfter() {
        return this.spacingAfter;
    }
    
    public boolean isExtendLastRow() {
        return this.extendLastRow[0];
    }
    
    public void setExtendLastRow(final boolean extendLastRows) {
        this.extendLastRow[0] = extendLastRows;
        this.extendLastRow[1] = extendLastRows;
    }
    
    public void setExtendLastRow(final boolean extendLastRows, final boolean extendFinalRow) {
        this.extendLastRow[0] = extendLastRows;
        this.extendLastRow[1] = extendFinalRow;
    }
    
    public boolean isExtendLastRow(final boolean newPageFollows) {
        if (newPageFollows) {
            return this.extendLastRow[0];
        }
        return this.extendLastRow[1];
    }
    
    public boolean isHeadersInEvent() {
        return this.headersInEvent;
    }
    
    public void setHeadersInEvent(final boolean headersInEvent) {
        this.headersInEvent = headersInEvent;
    }
    
    public boolean isSplitLate() {
        return this.splitLate;
    }
    
    public void setSplitLate(final boolean splitLate) {
        this.splitLate = splitLate;
    }
    
    public void setKeepTogether(final boolean keepTogether) {
        this.keepTogether = keepTogether;
    }
    
    public boolean getKeepTogether() {
        return this.keepTogether;
    }
    
    public int getFooterRows() {
        return this.footerRows;
    }
    
    public void setFooterRows(int footerRows) {
        if (footerRows < 0) {
            footerRows = 0;
        }
        this.footerRows = footerRows;
    }
    
    public void completeRow() {
        while (!this.rowCompleted) {
            this.addCell(this.defaultCell);
        }
    }
    
    @Override
    public void flushContent() {
        this.deleteBodyRows();
        this.setSkipFirstHeader(true);
    }
    
    @Override
    public boolean isComplete() {
        return this.complete;
    }
    
    @Override
    public void setComplete(final boolean complete) {
        this.complete = complete;
    }
}
