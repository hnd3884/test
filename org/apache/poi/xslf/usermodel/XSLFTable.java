package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.sl.draw.DrawTextShape;
import org.apache.poi.sl.usermodel.TextShape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.apache.poi.sl.draw.DrawFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrameNonVisual;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCol;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import java.util.Collections;
import org.apache.poi.util.Units;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.apache.poi.sl.usermodel.TableShape;

public class XSLFTable extends XSLFGraphicFrame implements Iterable<XSLFTableRow>, TableShape<XSLFShape, XSLFTextParagraph>
{
    static final String TABLE_URI = "http://schemas.openxmlformats.org/drawingml/2006/table";
    private CTTable _table;
    private List<XSLFTableRow> _rows;
    
    XSLFTable(final CTGraphicalObjectFrame shape, final XSLFSheet sheet) {
        super(shape, sheet);
        final CTGraphicalObjectData god = shape.getGraphic().getGraphicData();
        final XmlCursor xc = god.newCursor();
        try {
            if (!xc.toChild("http://schemas.openxmlformats.org/drawingml/2006/main", "tbl")) {
                throw new IllegalStateException("a:tbl element was not found in\n " + god);
            }
            final XmlObject xo = xc.getObject();
            if (xo instanceof XmlAnyTypeImpl) {
                final String errStr = "Schemas (*.xsb) for CTTable can't be loaded - usually this happens when OSGI loading is used and the thread context classloader has no reference to the xmlbeans classes";
                throw new IllegalStateException(errStr);
            }
            this._table = (CTTable)xo;
        }
        finally {
            xc.dispose();
        }
        this._rows = new ArrayList<XSLFTableRow>(this._table.sizeOfTrArray());
        for (final CTTableRow row : this._table.getTrList()) {
            this._rows.add(new XSLFTableRow(row, this));
        }
        this.updateRowColIndexes();
    }
    
    public XSLFTableCell getCell(final int row, final int col) {
        final List<XSLFTableRow> rows = this.getRows();
        if (row < 0 || rows.size() <= row) {
            return null;
        }
        final XSLFTableRow r = rows.get(row);
        if (r == null) {
            return null;
        }
        final List<XSLFTableCell> cells = r.getCells();
        if (col < 0 || cells.size() <= col) {
            return null;
        }
        return cells.get(col);
    }
    
    @Internal
    public CTTable getCTTable() {
        return this._table;
    }
    
    public int getNumberOfColumns() {
        return this._table.getTblGrid().sizeOfGridColArray();
    }
    
    public int getNumberOfRows() {
        return this._table.sizeOfTrArray();
    }
    
    public double getColumnWidth(final int idx) {
        return Units.toPoints(this._table.getTblGrid().getGridColArray(idx).getW());
    }
    
    public void setColumnWidth(final int idx, final double width) {
        this._table.getTblGrid().getGridColArray(idx).setW((long)Units.toEMU(width));
    }
    
    public double getRowHeight(final int row) {
        return Units.toPoints(this._table.getTrArray(row).getH());
    }
    
    public void setRowHeight(final int row, final double height) {
        this._table.getTrArray(row).setH((long)Units.toEMU(height));
    }
    
    @Override
    public Iterator<XSLFTableRow> iterator() {
        return this._rows.iterator();
    }
    
    public List<XSLFTableRow> getRows() {
        return Collections.unmodifiableList((List<? extends XSLFTableRow>)this._rows);
    }
    
    public XSLFTableRow addRow() {
        final CTTableRow tr = this._table.addNewTr();
        final XSLFTableRow row = new XSLFTableRow(tr, this);
        row.setHeight(20.0);
        this._rows.add(row);
        this.updateRowColIndexes();
        return row;
    }
    
    public void removeRow(final int rowIdx) {
        this._table.removeTr(rowIdx);
        this._rows.remove(rowIdx);
        this.updateRowColIndexes();
    }
    
    public void addColumn() {
        final long width = this._table.getTblGrid().getGridColArray(this._table.getTblGrid().sizeOfGridColArray() - 1).getW();
        final CTTableCol col = this._table.getTblGrid().addNewGridCol();
        col.setW(width);
        for (final XSLFTableRow row : this._rows) {
            final XSLFTableCell cell = row.addCell();
            new XDDFTextBody(cell, cell.getTextBody(true)).initialize();
        }
        this.updateRowColIndexes();
    }
    
    public void insertColumn(final int colIdx) {
        if (this._table.getTblGrid().sizeOfGridColArray() < colIdx) {
            throw new IndexOutOfBoundsException("Cannot insert column at " + colIdx + "; table has only " + this._table.getTblGrid().sizeOfGridColArray() + "columns.");
        }
        final long width = this._table.getTblGrid().getGridColArray(colIdx).getW();
        final CTTableCol col = this._table.getTblGrid().insertNewGridCol(colIdx);
        col.setW(width);
        for (final XSLFTableRow row : this._rows) {
            final XSLFTableCell cell = row.insertCell(colIdx);
            new XDDFTextBody(cell, cell.getTextBody(true)).initialize();
        }
        this.updateRowColIndexes();
    }
    
    public void removeColumn(final int colIdx) {
        this._table.getTblGrid().removeGridCol(colIdx);
        for (final XSLFTableRow row : this._rows) {
            row.removeCell(colIdx);
        }
        this.updateRowColIndexes();
    }
    
    static CTGraphicalObjectFrame prototype(final int shapeId) {
        final CTGraphicalObjectFrame frame = CTGraphicalObjectFrame.Factory.newInstance();
        final CTGraphicalObjectFrameNonVisual nvGr = frame.addNewNvGraphicFramePr();
        final CTNonVisualDrawingProps cnv = nvGr.addNewCNvPr();
        cnv.setName("Table " + shapeId);
        cnv.setId((long)shapeId);
        nvGr.addNewCNvGraphicFramePr().addNewGraphicFrameLocks().setNoGrp(true);
        nvGr.addNewNvPr();
        frame.addNewXfrm();
        final CTGraphicalObjectData gr = frame.addNewGraphic().addNewGraphicData();
        final XmlCursor grCur = gr.newCursor();
        grCur.toNextToken();
        grCur.beginElement(new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tbl"));
        final CTTable tbl = CTTable.Factory.newInstance();
        tbl.addNewTblPr();
        tbl.addNewTblGrid();
        final XmlCursor tblCur = tbl.newCursor();
        tblCur.moveXmlContents(grCur);
        tblCur.dispose();
        grCur.dispose();
        gr.setUri("http://schemas.openxmlformats.org/drawingml/2006/table");
        return frame;
    }
    
    public void mergeCells(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        if (firstRow > lastRow) {
            throw new IllegalArgumentException("Cannot merge, first row > last row : " + firstRow + " > " + lastRow);
        }
        if (firstCol > lastCol) {
            throw new IllegalArgumentException("Cannot merge, first column > last column : " + firstCol + " > " + lastCol);
        }
        final int rowSpan = lastRow - firstRow + 1;
        final boolean mergeRowRequired = rowSpan > 1;
        final int colSpan = lastCol - firstCol + 1;
        final boolean mergeColumnRequired = colSpan > 1;
        for (int i = firstRow; i <= lastRow; ++i) {
            final XSLFTableRow row = this._rows.get(i);
            for (int colPos = firstCol; colPos <= lastCol; ++colPos) {
                final XSLFTableCell cell = row.getCells().get(colPos);
                if (mergeRowRequired) {
                    if (i == firstRow) {
                        cell.setRowSpan(rowSpan);
                    }
                    else {
                        cell.setVMerge();
                    }
                }
                if (mergeColumnRequired) {
                    if (colPos == firstCol) {
                        cell.setGridSpan(colSpan);
                    }
                    else {
                        cell.setHMerge();
                    }
                }
            }
        }
    }
    
    protected XSLFTableStyle getTableStyle() {
        final CTTable tab = this.getCTTable();
        if (!tab.isSetTblPr() || !tab.getTblPr().isSetTableStyleId()) {
            return null;
        }
        final String styleId = tab.getTblPr().getTableStyleId();
        final XSLFTableStyles styles = this.getSheet().getSlideShow().getTableStyles();
        for (final XSLFTableStyle style : styles.getStyles()) {
            if (style.getStyleId().equals(styleId)) {
                return style;
            }
        }
        return null;
    }
    
    void updateRowColIndexes() {
        int rowIdx = 0;
        for (final XSLFTableRow xr : this) {
            int colIdx = 0;
            for (final XSLFTableCell tc : xr) {
                tc.setRowColIndex(rowIdx, colIdx);
                ++colIdx;
            }
            ++rowIdx;
        }
    }
    
    public void updateCellAnchor() {
        final int rows = this.getNumberOfRows();
        final int cols = this.getNumberOfColumns();
        final double[] colWidths = new double[cols];
        final double[] rowHeights = new double[rows];
        for (int row = 0; row < rows; ++row) {
            rowHeights[row] = this.getRowHeight(row);
        }
        for (int col = 0; col < cols; ++col) {
            colWidths[col] = this.getColumnWidth(col);
        }
        final Rectangle2D tblAnc = this.getAnchor();
        final DrawFactory df = DrawFactory.getInstance((Graphics2D)null);
        double nextY = tblAnc.getY();
        double nextX = tblAnc.getX();
        for (int row2 = 0; row2 < rows; ++row2) {
            double maxHeight = 0.0;
            for (int col2 = 0; col2 < cols; ++col2) {
                final XSLFTableCell tc = this.getCell(row2, col2);
                if (tc != null && tc.getGridSpan() == 1) {
                    if (tc.getRowSpan() == 1) {
                        tc.setAnchor(new Rectangle2D.Double(0.0, 0.0, colWidths[col2], 0.0));
                        final DrawTextShape dts = df.getDrawable((TextShape)tc);
                        maxHeight = Math.max(maxHeight, dts.getTextHeight());
                    }
                }
            }
            rowHeights[row2] = Math.max(rowHeights[row2], maxHeight);
        }
        for (int row2 = 0; row2 < rows; ++row2) {
            nextX = tblAnc.getX();
            for (int col3 = 0; col3 < cols; ++col3) {
                final Rectangle2D bounds = new Rectangle2D.Double(nextX, nextY, colWidths[col3], rowHeights[row2]);
                final XSLFTableCell tc2 = this.getCell(row2, col3);
                if (tc2 != null) {
                    tc2.setAnchor(bounds);
                    nextX += colWidths[col3] + 2.0;
                }
            }
            nextY += rowHeights[row2] + 2.0;
        }
        for (int row2 = 0; row2 < rows; ++row2) {
            for (int col3 = 0; col3 < cols; ++col3) {
                final XSLFTableCell tc3 = this.getCell(row2, col3);
                if (tc3 != null) {
                    final Rectangle2D mergedBounds = tc3.getAnchor();
                    for (int col4 = col3 + 1; col4 < col3 + tc3.getGridSpan(); ++col4) {
                        assert col4 < cols;
                        final XSLFTableCell tc4 = this.getCell(row2, col4);
                        assert tc4.getGridSpan() == 1 && tc4.getRowSpan() == 1;
                        mergedBounds.add(tc4.getAnchor());
                    }
                    for (int row3 = row2 + 1; row3 < row2 + tc3.getRowSpan(); ++row3) {
                        assert row3 < rows;
                        final XSLFTableCell tc4 = this.getCell(row3, col3);
                        assert tc4.getGridSpan() == 1 && tc4.getRowSpan() == 1;
                        mergedBounds.add(tc4.getAnchor());
                    }
                    tc3.setAnchor(mergedBounds);
                }
            }
        }
        this.setAnchor(new Rectangle2D.Double(tblAnc.getX(), tblAnc.getY(), nextX - tblAnc.getX(), nextY - tblAnc.getY()));
    }
}
