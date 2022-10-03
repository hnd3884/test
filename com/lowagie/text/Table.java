package com.lowagie.text;

import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTableEvent;
import com.lowagie.text.pdf.PdfPTable;
import java.util.Iterator;
import java.awt.Dimension;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Point;
import java.util.ArrayList;

public class Table extends Rectangle implements LargeElement
{
    private int columns;
    private ArrayList rows;
    private Point curPosition;
    private Cell defaultCell;
    private int lastHeaderRow;
    private int alignment;
    private float cellpadding;
    private float cellspacing;
    private float width;
    private boolean locked;
    private float[] widths;
    private boolean mTableInserted;
    protected boolean autoFillEmptyCells;
    boolean tableFitsPage;
    boolean cellsFitPage;
    float offset;
    protected boolean convert2pdfptable;
    protected boolean notAddedYet;
    protected boolean complete;
    
    public Table(final int columns) throws BadElementException {
        this(columns, 1);
    }
    
    public Table(final int columns, final int rows) throws BadElementException {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.rows = new ArrayList();
        this.curPosition = new Point(0, 0);
        this.defaultCell = new Cell(true);
        this.lastHeaderRow = -1;
        this.alignment = 1;
        this.width = 80.0f;
        this.locked = false;
        this.mTableInserted = false;
        this.autoFillEmptyCells = false;
        this.tableFitsPage = false;
        this.cellsFitPage = false;
        this.offset = Float.NaN;
        this.convert2pdfptable = false;
        this.notAddedYet = true;
        this.complete = true;
        this.setBorder(15);
        this.setBorderWidth(1.0f);
        this.defaultCell.setBorder(15);
        if (columns <= 0) {
            throw new BadElementException(MessageLocalization.getComposedMessage("a.table.should.have.at.least.1.column"));
        }
        this.columns = columns;
        for (int i = 0; i < rows; ++i) {
            this.rows.add(new Row(columns));
        }
        this.curPosition = new Point(0, 0);
        this.widths = new float[columns];
        final float width = 100.0f / columns;
        for (int j = 0; j < columns; ++j) {
            this.widths[j] = width;
        }
    }
    
    public Table(final Table t) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.rows = new ArrayList();
        this.curPosition = new Point(0, 0);
        this.defaultCell = new Cell(true);
        this.lastHeaderRow = -1;
        this.alignment = 1;
        this.width = 80.0f;
        this.locked = false;
        this.mTableInserted = false;
        this.autoFillEmptyCells = false;
        this.tableFitsPage = false;
        this.cellsFitPage = false;
        this.offset = Float.NaN;
        this.convert2pdfptable = false;
        this.notAddedYet = true;
        this.complete = true;
        this.cloneNonPositionParameters(t);
        this.columns = t.columns;
        this.rows = t.rows;
        this.curPosition = t.curPosition;
        this.defaultCell = t.defaultCell;
        this.lastHeaderRow = t.lastHeaderRow;
        this.alignment = t.alignment;
        this.cellpadding = t.cellpadding;
        this.cellspacing = t.cellspacing;
        this.width = t.width;
        this.widths = t.widths;
        this.autoFillEmptyCells = t.autoFillEmptyCells;
        this.tableFitsPage = t.tableFitsPage;
        this.cellsFitPage = t.cellsFitPage;
        this.offset = t.offset;
        this.convert2pdfptable = t.convert2pdfptable;
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
    
    @Override
    public int type() {
        return 22;
    }
    
    @Override
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public int size() {
        return this.rows.size();
    }
    
    public Dimension getDimension() {
        return new Dimension(this.columns, this.size());
    }
    
    public Cell getDefaultCell() {
        return this.defaultCell;
    }
    
    public void setDefaultCell(final Cell value) {
        this.defaultCell = value;
    }
    
    public int getLastHeaderRow() {
        return this.lastHeaderRow;
    }
    
    public void setLastHeaderRow(final int value) {
        this.lastHeaderRow = value;
    }
    
    public int endHeaders() {
        return this.lastHeaderRow = this.curPosition.x - 1;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int value) {
        this.alignment = value;
    }
    
    public void setAlignment(final String alignment) {
        if ("Left".equalsIgnoreCase(alignment)) {
            this.alignment = 0;
            return;
        }
        if ("right".equalsIgnoreCase(alignment)) {
            this.alignment = 2;
            return;
        }
        this.alignment = 1;
    }
    
    public float getPadding() {
        return this.cellpadding;
    }
    
    public void setPadding(final float value) {
        this.cellpadding = value;
    }
    
    public float getSpacing() {
        return this.cellspacing;
    }
    
    public void setSpacing(final float value) {
        this.cellspacing = value;
    }
    
    public void setAutoFillEmptyCells(final boolean aDoAutoFill) {
        this.autoFillEmptyCells = aDoAutoFill;
    }
    
    @Override
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    public float[] getProportionalWidths() {
        return this.widths;
    }
    
    public void setWidths(final float[] widths) throws BadElementException {
        if (widths.length != this.columns) {
            throw new BadElementException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        float hundredPercent = 0.0f;
        for (int i = 0; i < this.columns; ++i) {
            hundredPercent += widths[i];
        }
        this.widths[this.columns - 1] = 100.0f;
        for (int j = 0; j < this.columns - 1; ++j) {
            final float width = 100.0f * widths[j] / hundredPercent;
            this.widths[j] = width;
            final float[] widths2 = this.widths;
            final int n = this.columns - 1;
            widths2[n] -= width;
        }
    }
    
    public void setWidths(final int[] widths) throws DocumentException {
        final float[] tb = new float[widths.length];
        for (int k = 0; k < widths.length; ++k) {
            tb[k] = (float)widths[k];
        }
        this.setWidths(tb);
    }
    
    public boolean isTableFitsPage() {
        return this.tableFitsPage;
    }
    
    public void setTableFitsPage(final boolean fitPage) {
        this.tableFitsPage = fitPage;
        if (fitPage) {
            this.setCellsFitPage(true);
        }
    }
    
    public boolean isCellsFitPage() {
        return this.cellsFitPage;
    }
    
    public void setCellsFitPage(final boolean fitPage) {
        this.cellsFitPage = fitPage;
    }
    
    public void setOffset(final float offset) {
        this.offset = offset;
    }
    
    public float getOffset() {
        return this.offset;
    }
    
    public boolean isConvert2pdfptable() {
        return this.convert2pdfptable;
    }
    
    public void setConvert2pdfptable(final boolean convert2pdfptable) {
        this.convert2pdfptable = convert2pdfptable;
    }
    
    public void addCell(final Cell aCell, final int row, final int column) throws BadElementException {
        this.addCell(aCell, new Point(row, column));
    }
    
    public void addCell(final Cell aCell, final Point aLocation) throws BadElementException {
        if (aCell == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("addcell.cell.has.null.value"));
        }
        if (aLocation == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("addcell.point.has.null.value"));
        }
        if (aCell.isTable()) {
            this.insertTable(aCell.getElements().next(), aLocation);
        }
        if (aLocation.x < 0) {
            throw new BadElementException(MessageLocalization.getComposedMessage("row.coordinate.of.location.must.be.gt.eq.0"));
        }
        if (aLocation.y <= 0 && aLocation.y > this.columns) {
            throw new BadElementException(MessageLocalization.getComposedMessage("column.coordinate.of.location.must.be.gt.eq.0.and.lt.nr.of.columns"));
        }
        if (!this.isValidLocation(aCell, aLocation)) {
            throw new BadElementException(MessageLocalization.getComposedMessage("adding.a.cell.at.the.location.1.2.with.a.colspan.of.3.and.a.rowspan.of.4.is.illegal.beyond.boundaries.overlapping", String.valueOf(aLocation.x), String.valueOf(aLocation.y), String.valueOf(aCell.getColspan()), String.valueOf(aCell.getRowspan())));
        }
        if (aCell.getBorder() == -1) {
            aCell.setBorder(this.defaultCell.getBorder());
        }
        aCell.fill();
        this.placeCell(this.rows, aCell, aLocation);
        this.setCurrentLocationToNextValidPosition(aLocation);
    }
    
    public void addCell(final Cell cell) {
        try {
            this.addCell(cell, this.curPosition);
        }
        catch (final BadElementException ex) {}
    }
    
    public void addCell(final Phrase content) throws BadElementException {
        this.addCell(content, this.curPosition);
    }
    
    public void addCell(final Phrase content, final Point location) throws BadElementException {
        final Cell cell = new Cell(content);
        cell.setBorder(this.defaultCell.getBorder());
        cell.setBorderWidth(this.defaultCell.getBorderWidth());
        cell.setBorderColor(this.defaultCell.getBorderColor());
        cell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        cell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
        cell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
        cell.setColspan(this.defaultCell.getColspan());
        cell.setRowspan(this.defaultCell.getRowspan());
        this.addCell(cell, location);
    }
    
    public void addCell(final String content) throws BadElementException {
        this.addCell(new Phrase(content), this.curPosition);
    }
    
    public void addCell(final String content, final Point location) throws BadElementException {
        this.addCell(new Phrase(content), location);
    }
    
    public void insertTable(final Table aTable) {
        if (aTable == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.table.has.null.value"));
        }
        this.insertTable(aTable, this.curPosition);
    }
    
    public void insertTable(final Table aTable, final int row, final int column) {
        if (aTable == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.table.has.null.value"));
        }
        this.insertTable(aTable, new Point(row, column));
    }
    
    public void insertTable(final Table aTable, final Point aLocation) {
        if (aTable == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.table.has.null.value"));
        }
        if (aLocation == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.point.has.null.value"));
        }
        this.mTableInserted = true;
        aTable.complete();
        if (aLocation.y > this.columns) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("inserttable.wrong.columnposition.1.of.location.max.eq.2", String.valueOf(aLocation.y), String.valueOf(this.columns)));
        }
        final int rowCount = aLocation.x + 1 - this.rows.size();
        int i = 0;
        if (rowCount > 0) {
            while (i < rowCount) {
                this.rows.add(new Row(this.columns));
                ++i;
            }
        }
        this.rows.get(aLocation.x).setElement(aTable, aLocation.y);
        this.setCurrentLocationToNextValidPosition(aLocation);
    }
    
    public void addColumns(final int aColumns) {
        final ArrayList newRows = new ArrayList(this.rows.size());
        final int newColumns = this.columns + aColumns;
        for (int i = 0; i < this.rows.size(); ++i) {
            final Row row = new Row(newColumns);
            for (int j = 0; j < this.columns; ++j) {
                row.setElement(this.rows.get(i).getCell(j), j);
            }
            for (int j = this.columns; j < newColumns && i < this.curPosition.x; ++j) {
                row.setElement(null, j);
            }
            newRows.add(row);
        }
        final float[] newWidths = new float[newColumns];
        System.arraycopy(this.widths, 0, newWidths, 0, this.columns);
        for (int j = this.columns; j < newColumns; ++j) {
            newWidths[j] = 0.0f;
        }
        this.columns = newColumns;
        this.widths = newWidths;
        this.rows = newRows;
    }
    
    public void deleteColumn(final int column) throws BadElementException {
        final int columns = this.columns - 1;
        this.columns = columns;
        final float[] newWidths = new float[columns];
        System.arraycopy(this.widths, 0, newWidths, 0, column);
        System.arraycopy(this.widths, column + 1, newWidths, column, this.columns - column);
        this.setWidths(newWidths);
        System.arraycopy(this.widths, 0, newWidths, 0, this.columns);
        this.widths = newWidths;
        for (int size = this.rows.size(), i = 0; i < size; ++i) {
            final Row row = this.rows.get(i);
            row.deleteColumn(column);
            this.rows.set(i, row);
        }
        if (column == this.columns) {
            this.curPosition.setLocation(this.curPosition.x + 1, 0);
        }
    }
    
    public boolean deleteRow(final int row) {
        if (row < 0 || row >= this.rows.size()) {
            return false;
        }
        this.rows.remove(row);
        this.curPosition.setLocation(this.curPosition.x - 1, this.curPosition.y);
        return true;
    }
    
    public void deleteAllRows() {
        this.rows.clear();
        this.rows.add(new Row(this.columns));
        this.curPosition.setLocation(0, 0);
        this.lastHeaderRow = -1;
    }
    
    public boolean deleteLastRow() {
        return this.deleteRow(this.rows.size() - 1);
    }
    
    public void complete() {
        if (this.mTableInserted) {
            this.mergeInsertedTables();
            this.mTableInserted = false;
        }
        if (this.autoFillEmptyCells) {
            this.fillEmptyMatrixCells();
        }
    }
    
    public Object getElement(final int row, final int column) {
        return this.rows.get(row).getCell(column);
    }
    
    private void mergeInsertedTables() {
        int i = 0;
        int j = 0;
        float[] lNewWidths = null;
        final int[] lDummyWidths = new int[this.columns];
        final float[][] lDummyColumnWidths = new float[this.columns][];
        final int[] lDummyHeights = new int[this.rows.size()];
        ArrayList newRows = null;
        boolean isTable = false;
        int lTotalRows = 0;
        int lTotalColumns = 0;
        int lNewMaxRows = 0;
        int lNewMaxColumns = 0;
        Table lDummyTable = null;
        for (j = 0; j < this.columns; ++j) {
            lNewMaxColumns = 1;
            float[] tmpWidths = null;
            for (i = 0; i < this.rows.size(); ++i) {
                if (this.rows.get(i).getCell(j) instanceof Table) {
                    isTable = true;
                    lDummyTable = (Table)this.rows.get(i).getCell(j);
                    if (tmpWidths == null) {
                        tmpWidths = lDummyTable.widths;
                        lNewMaxColumns = tmpWidths.length;
                    }
                    else {
                        final int cols = lDummyTable.getDimension().width;
                        final float[] tmpWidthsN = new float[cols * tmpWidths.length];
                        float tpW = 0.0f;
                        float btW = 0.0f;
                        float totW = 0.0f;
                        int tpI = 0;
                        int btI = 0;
                        int totI = 0;
                        tpW += tmpWidths[0];
                        btW += lDummyTable.widths[0];
                        while (tpI < tmpWidths.length && btI < cols) {
                            if (btW > tpW) {
                                tmpWidthsN[totI] = tpW - totW;
                                if (++tpI < tmpWidths.length) {
                                    tpW += tmpWidths[tpI];
                                }
                            }
                            else {
                                tmpWidthsN[totI] = btW - totW;
                                ++btI;
                                if (Math.abs(btW - tpW) < 1.0E-4 && ++tpI < tmpWidths.length) {
                                    tpW += tmpWidths[tpI];
                                }
                                if (btI < cols) {
                                    btW += lDummyTable.widths[btI];
                                }
                            }
                            totW += tmpWidthsN[totI];
                            ++totI;
                        }
                        tmpWidths = new float[totI];
                        System.arraycopy(tmpWidthsN, 0, tmpWidths, 0, totI);
                        lNewMaxColumns = totI;
                    }
                }
            }
            lDummyColumnWidths[j] = tmpWidths;
            lTotalColumns += lNewMaxColumns;
            lDummyWidths[j] = lNewMaxColumns;
        }
        for (i = 0; i < this.rows.size(); ++i) {
            lNewMaxRows = 1;
            for (j = 0; j < this.columns; ++j) {
                if (this.rows.get(i).getCell(j) instanceof Table) {
                    isTable = true;
                    lDummyTable = (Table)this.rows.get(i).getCell(j);
                    if (lDummyTable.getDimension().height > lNewMaxRows) {
                        lNewMaxRows = lDummyTable.getDimension().height;
                    }
                }
            }
            lTotalRows += lNewMaxRows;
            lDummyHeights[i] = lNewMaxRows;
        }
        if (lTotalColumns != this.columns || lTotalRows != this.rows.size() || isTable) {
            lNewWidths = new float[lTotalColumns];
            int lDummy = 0;
            for (int tel = 0; tel < this.widths.length; ++tel) {
                if (lDummyWidths[tel] != 1) {
                    for (int tel2 = 0; tel2 < lDummyWidths[tel]; ++tel2) {
                        lNewWidths[lDummy] = this.widths[tel] * lDummyColumnWidths[tel][tel2] / 100.0f;
                        ++lDummy;
                    }
                }
                else {
                    lNewWidths[lDummy] = this.widths[tel];
                    ++lDummy;
                }
            }
            newRows = new ArrayList(lTotalRows);
            for (i = 0; i < lTotalRows; ++i) {
                newRows.add(new Row(lTotalColumns));
            }
            int lDummyRow = 0;
            int lDummyColumn = 0;
            Object lDummyElement = null;
            for (i = 0; i < this.rows.size(); ++i) {
                lDummyColumn = 0;
                lNewMaxRows = 1;
                for (j = 0; j < this.columns; ++j) {
                    if (this.rows.get(i).getCell(j) instanceof Table) {
                        lDummyTable = (Table)this.rows.get(i).getCell(j);
                        final int[] colMap = new int[lDummyTable.widths.length + 1];
                        int cb = 0;
                        int ct = 0;
                        while (cb < lDummyTable.widths.length) {
                            colMap[cb] = lDummyColumn + ct;
                            final float wb = lDummyTable.widths[cb];
                            float wt = 0.0f;
                            while (ct < lDummyWidths[j]) {
                                wt += lDummyColumnWidths[j][ct++];
                                if (Math.abs(wb - wt) < 1.0E-4) {
                                    break;
                                }
                            }
                            ++cb;
                        }
                        colMap[cb] = lDummyColumn + ct;
                        for (int k = 0; k < lDummyTable.getDimension().height; ++k) {
                            for (int l = 0; l < lDummyTable.getDimension().width; ++l) {
                                lDummyElement = lDummyTable.getElement(k, l);
                                if (lDummyElement != null) {
                                    int col = lDummyColumn + l;
                                    if (lDummyElement instanceof Cell) {
                                        final Cell lDummyC = (Cell)lDummyElement;
                                        col = colMap[l];
                                        final int ot = colMap[l + lDummyC.getColspan()];
                                        lDummyC.setColspan(ot - col);
                                    }
                                    newRows.get(k + lDummyRow).addElement(lDummyElement, col);
                                }
                            }
                        }
                    }
                    else {
                        final Object aElement = this.getElement(i, j);
                        if (aElement instanceof Cell) {
                            ((Cell)aElement).setRowspan(((Cell)this.rows.get(i).getCell(j)).getRowspan() + lDummyHeights[i] - 1);
                            ((Cell)aElement).setColspan(((Cell)this.rows.get(i).getCell(j)).getColspan() + lDummyWidths[j] - 1);
                            this.placeCell(newRows, (Cell)aElement, new Point(lDummyRow, lDummyColumn));
                        }
                    }
                    lDummyColumn += lDummyWidths[j];
                }
                lDummyRow += lDummyHeights[i];
            }
            this.columns = lTotalColumns;
            this.rows = newRows;
            this.widths = lNewWidths;
        }
    }
    
    private void fillEmptyMatrixCells() {
        try {
            for (int i = 0; i < this.rows.size(); ++i) {
                for (int j = 0; j < this.columns; ++j) {
                    if (!this.rows.get(i).isReserved(j)) {
                        this.addCell(this.defaultCell, new Point(i, j));
                    }
                }
            }
        }
        catch (final BadElementException bee) {
            throw new ExceptionConverter(bee);
        }
    }
    
    private boolean isValidLocation(final Cell aCell, final Point aLocation) {
        if (aLocation.x >= this.rows.size()) {
            return aLocation.y + aCell.getColspan() <= this.columns;
        }
        if (aLocation.y + aCell.getColspan() > this.columns) {
            return false;
        }
        final int difx = (this.rows.size() - aLocation.x > aCell.getRowspan()) ? aCell.getRowspan() : (this.rows.size() - aLocation.x);
        final int dify = (this.columns - aLocation.y > aCell.getColspan()) ? aCell.getColspan() : (this.columns - aLocation.y);
        for (int i = aLocation.x; i < aLocation.x + difx; ++i) {
            for (int j = aLocation.y; j < aLocation.y + dify; ++j) {
                if (this.rows.get(i).isReserved(j)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void assumeTableDefaults(final Cell aCell) {
        if (aCell.getBorder() == -1) {
            aCell.setBorder(this.defaultCell.getBorder());
        }
        if (aCell.getBorderWidth() == -1.0f) {
            aCell.setBorderWidth(this.defaultCell.getBorderWidth());
        }
        if (aCell.getBorderColor() == null) {
            aCell.setBorderColor(this.defaultCell.getBorderColor());
        }
        if (aCell.getBackgroundColor() == null) {
            aCell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        }
        if (aCell.getHorizontalAlignment() == -1) {
            aCell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
        }
        if (aCell.getVerticalAlignment() == -1) {
            aCell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
        }
    }
    
    private void placeCell(final ArrayList someRows, final Cell aCell, final Point aPosition) {
        Row row = null;
        final int rowCount = aPosition.x + aCell.getRowspan() - someRows.size();
        this.assumeTableDefaults(aCell);
        if (aPosition.x + aCell.getRowspan() > someRows.size()) {
            for (int i = 0; i < rowCount; ++i) {
                row = new Row(this.columns);
                someRows.add(row);
            }
        }
        for (int i = aPosition.x + 1; i < aPosition.x + aCell.getRowspan(); ++i) {
            if (!someRows.get(i).reserve(aPosition.y, aCell.getColspan())) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("addcell.error.in.reserve"));
            }
        }
        row = someRows.get(aPosition.x);
        row.addElement(aCell, aPosition.y);
    }
    
    private void setCurrentLocationToNextValidPosition(final Point aLocation) {
        int i = aLocation.x;
        int j = aLocation.y;
        do {
            if (j + 1 == this.columns) {
                ++i;
                j = 0;
            }
            else {
                ++j;
            }
        } while (i < this.rows.size() && j < this.columns && this.rows.get(i).isReserved(j));
        this.curPosition = new Point(i, j);
    }
    
    public float[] getWidths(final float left, float totalWidth) {
        final float[] w = new float[this.columns + 1];
        float wPercentage;
        if (this.locked) {
            wPercentage = 100.0f * this.width / totalWidth;
        }
        else {
            wPercentage = this.width;
        }
        switch (this.alignment) {
            case 0: {
                w[0] = left;
                break;
            }
            case 2: {
                w[0] = left + totalWidth * (100.0f - wPercentage) / 100.0f;
                break;
            }
            default: {
                w[0] = left + totalWidth * (100.0f - wPercentage) / 200.0f;
                break;
            }
        }
        totalWidth = totalWidth * wPercentage / 100.0f;
        for (int i = 1; i < this.columns; ++i) {
            w[i] = w[i - 1] + this.widths[i - 1] * totalWidth / 100.0f;
        }
        w[this.columns] = w[0] + totalWidth;
        return w;
    }
    
    public Iterator iterator() {
        return this.rows.iterator();
    }
    
    public PdfPTable createPdfPTable() throws BadElementException {
        if (!this.convert2pdfptable) {
            throw new BadElementException(MessageLocalization.getComposedMessage("no.error.just.an.old.style.table"));
        }
        this.setAutoFillEmptyCells(true);
        this.complete();
        final PdfPTable pdfptable = new PdfPTable(this.widths);
        pdfptable.setComplete(this.complete);
        if (this.isNotAddedYet()) {
            pdfptable.setSkipFirstHeader(true);
        }
        final SimpleTable t_evt = new SimpleTable();
        t_evt.cloneNonPositionParameters(this);
        t_evt.setCellspacing(this.cellspacing);
        pdfptable.setTableEvent(t_evt);
        pdfptable.setHeaderRows(this.lastHeaderRow + 1);
        pdfptable.setSplitLate(this.cellsFitPage);
        pdfptable.setKeepTogether(this.tableFitsPage);
        if (!Float.isNaN(this.offset)) {
            pdfptable.setSpacingBefore(this.offset);
        }
        pdfptable.setHorizontalAlignment(this.alignment);
        if (this.locked) {
            pdfptable.setTotalWidth(this.width);
            pdfptable.setLockedWidth(true);
        }
        else {
            pdfptable.setWidthPercentage(this.width);
        }
        for (final Row row : this) {
            for (int i = 0; i < row.getColumns(); ++i) {
                final Element cell;
                if ((cell = (Element)row.getCell(i)) != null) {
                    PdfPCell pcell;
                    if (cell instanceof Table) {
                        pcell = new PdfPCell(((Table)cell).createPdfPTable());
                    }
                    else if (cell instanceof Cell) {
                        pcell = ((Cell)cell).createPdfPCell();
                        pcell.setPadding(this.cellpadding + this.cellspacing / 2.0f);
                        final SimpleCell c_evt = new SimpleCell(false);
                        c_evt.cloneNonPositionParameters((Rectangle)cell);
                        c_evt.setSpacing(this.cellspacing * 2.0f);
                        pcell.setCellEvent(c_evt);
                    }
                    else {
                        pcell = new PdfPCell();
                    }
                    pdfptable.addCell(pcell);
                }
            }
        }
        return pdfptable;
    }
    
    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }
    
    public void setNotAddedYet(final boolean notAddedYet) {
        this.notAddedYet = notAddedYet;
    }
    
    @Override
    public void flushContent() {
        this.setNotAddedYet(false);
        final ArrayList headerrows = new ArrayList();
        for (int i = 0; i < this.getLastHeaderRow() + 1; ++i) {
            headerrows.add(this.rows.get(i));
        }
        this.rows = headerrows;
    }
    
    @Override
    public boolean isComplete() {
        return this.complete;
    }
    
    @Override
    public void setComplete(final boolean complete) {
        this.complete = complete;
    }
    
    @Deprecated
    public Cell getDefaultLayout() {
        return this.getDefaultCell();
    }
    
    @Deprecated
    public void setDefaultLayout(final Cell value) {
        this.defaultCell = value;
    }
}
