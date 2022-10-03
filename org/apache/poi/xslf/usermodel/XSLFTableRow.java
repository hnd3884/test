package org.apache.poi.xslf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Units;
import java.util.Collections;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;

public class XSLFTableRow implements Iterable<XSLFTableCell>
{
    private final CTTableRow _row;
    private final List<XSLFTableCell> _cells;
    private final XSLFTable _table;
    
    XSLFTableRow(final CTTableRow row, final XSLFTable table) {
        this._row = row;
        this._table = table;
        final CTTableCell[] tcArray = this._row.getTcArray();
        this._cells = new ArrayList<XSLFTableCell>(tcArray.length);
        for (final CTTableCell cell : tcArray) {
            this._cells.add(new XSLFTableCell(cell, table));
        }
    }
    
    public CTTableRow getXmlObject() {
        return this._row;
    }
    
    @Override
    public Iterator<XSLFTableCell> iterator() {
        return this._cells.iterator();
    }
    
    public List<XSLFTableCell> getCells() {
        return Collections.unmodifiableList((List<? extends XSLFTableCell>)this._cells);
    }
    
    public double getHeight() {
        return Units.toPoints(this._row.getH());
    }
    
    public void setHeight(final double height) {
        this._row.setH((long)Units.toEMU(height));
    }
    
    public XSLFTableCell addCell() {
        final CTTableCell c = this._row.addNewTc();
        c.set((XmlObject)XSLFTableCell.prototype());
        final XSLFTableCell cell = new XSLFTableCell(c, this._table);
        this._cells.add(cell);
        if (this._table.getNumberOfColumns() < this._row.sizeOfTcArray()) {
            this._table.getCTTable().getTblGrid().addNewGridCol().setW((long)Units.toEMU(100.0));
        }
        this._table.updateRowColIndexes();
        return cell;
    }
    
    public XSLFTableCell insertCell(final int colIdx) {
        final CTTableCell c = this._row.insertNewTc(colIdx);
        c.set((XmlObject)XSLFTableCell.prototype());
        final XSLFTableCell cell = new XSLFTableCell(c, this._table);
        this._cells.add(colIdx, cell);
        if (this._table.getNumberOfColumns() < this._row.sizeOfTcArray()) {
            this._table.getCTTable().getTblGrid().insertNewGridCol(colIdx).setW((long)Units.toEMU(100.0));
        }
        this._table.updateRowColIndexes();
        return cell;
    }
    
    public void removeCell(final int colIdx) {
        this._row.removeTc(colIdx);
        this._cells.remove(colIdx);
        this._table.updateRowColIndexes();
    }
    
    public void mergeCells(final int firstCol, final int lastCol) {
        if (firstCol >= lastCol) {
            throw new IllegalArgumentException("Cannot merge, first column >= last column : " + firstCol + " >= " + lastCol);
        }
        final int colSpan = lastCol - firstCol + 1;
        this._cells.get(firstCol).setGridSpan(colSpan);
        for (final XSLFTableCell cell : this._cells.subList(firstCol + 1, lastCol + 1)) {
            cell.setHMerge();
        }
    }
}
