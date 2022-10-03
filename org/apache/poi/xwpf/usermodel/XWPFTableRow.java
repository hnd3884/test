package org.apache.poi.xwpf.usermodel;

import java.util.Iterator;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.poi.xwpf.model.WMLHelper;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtCell;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.apache.poi.util.Internal;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

public class XWPFTableRow
{
    private CTRow ctRow;
    private XWPFTable table;
    private List<XWPFTableCell> tableCells;
    
    public XWPFTableRow(final CTRow row, final XWPFTable table) {
        this.table = table;
        this.ctRow = row;
        this.getTableCells();
    }
    
    @Internal
    public CTRow getCtRow() {
        return this.ctRow;
    }
    
    public XWPFTableCell createCell() {
        final XWPFTableCell tableCell = new XWPFTableCell(this.ctRow.addNewTc(), this, this.table.getBody());
        this.tableCells.add(tableCell);
        return tableCell;
    }
    
    public XWPFTableCell getCell(final int pos) {
        if (pos >= 0 && pos < this.ctRow.sizeOfTcArray()) {
            return this.getTableCells().get(pos);
        }
        return null;
    }
    
    public void removeCell(final int pos) {
        if (pos >= 0 && pos < this.ctRow.sizeOfTcArray()) {
            this.tableCells.remove(pos);
        }
    }
    
    public XWPFTableCell addNewTableCell() {
        final CTTc cell = this.ctRow.addNewTc();
        final XWPFTableCell tableCell = new XWPFTableCell(cell, this, this.table.getBody());
        this.tableCells.add(tableCell);
        return tableCell;
    }
    
    public int getHeight() {
        final CTTrPr properties = this.getTrPr();
        return (properties.sizeOfTrHeightArray() == 0) ? 0 : properties.getTrHeightArray(0).getVal().intValue();
    }
    
    public void setHeight(final int height) {
        final CTTrPr properties = this.getTrPr();
        final CTHeight h = (properties.sizeOfTrHeightArray() == 0) ? properties.addNewTrHeight() : properties.getTrHeightArray(0);
        h.setVal(new BigInteger(Integer.toString(height)));
    }
    
    private CTTrPr getTrPr() {
        return this.ctRow.isSetTrPr() ? this.ctRow.getTrPr() : this.ctRow.addNewTrPr();
    }
    
    public XWPFTable getTable() {
        return this.table;
    }
    
    public List<ICell> getTableICells() {
        final List<ICell> cells = new ArrayList<ICell>();
        final XmlCursor cursor = this.ctRow.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            final XmlObject o = cursor.getObject();
            if (o instanceof CTTc) {
                cells.add(new XWPFTableCell((CTTc)o, this, this.table.getBody()));
            }
            else {
                if (!(o instanceof CTSdtCell)) {
                    continue;
                }
                cells.add(new XWPFSDTCell((CTSdtCell)o, this, this.table.getBody()));
            }
        }
        cursor.dispose();
        return cells;
    }
    
    public List<XWPFTableCell> getTableCells() {
        if (this.tableCells == null) {
            final List<XWPFTableCell> cells = new ArrayList<XWPFTableCell>();
            for (final CTTc tableCell : this.ctRow.getTcArray()) {
                cells.add(new XWPFTableCell(tableCell, this, this.table.getBody()));
            }
            this.tableCells = cells;
        }
        return this.tableCells;
    }
    
    public XWPFTableCell getTableCell(final CTTc cell) {
        for (int i = 0; i < this.tableCells.size(); ++i) {
            if (this.tableCells.get(i).getCTTc() == cell) {
                return this.tableCells.get(i);
            }
        }
        return null;
    }
    
    public boolean isCantSplitRow() {
        boolean isCant = false;
        if (this.ctRow.isSetTrPr()) {
            final CTTrPr trpr = this.getTrPr();
            if (trpr.sizeOfCantSplitArray() > 0) {
                final CTOnOff onoff = trpr.getCantSplitArray(0);
                isCant = (!onoff.isSetVal() || WMLHelper.convertSTOnOffToBoolean(onoff.getVal()));
            }
        }
        return isCant;
    }
    
    public void setCantSplitRow(final boolean split) {
        final CTTrPr trpr = this.getTrPr();
        final CTOnOff onoff = (trpr.sizeOfCantSplitArray() > 0) ? trpr.getCantSplitArray(0) : trpr.addNewCantSplit();
        onoff.setVal(WMLHelper.convertBooleanToSTOnOff(split));
    }
    
    public boolean isRepeatHeader() {
        boolean repeat = false;
        for (final XWPFTableRow row : this.table.getRows()) {
            repeat = row.getRepeat();
            if (row == this) {
                break;
            }
            if (!repeat) {
                break;
            }
        }
        return repeat;
    }
    
    private boolean getRepeat() {
        boolean repeat = false;
        if (this.ctRow.isSetTrPr()) {
            final CTTrPr trpr = this.getTrPr();
            if (trpr.sizeOfTblHeaderArray() > 0) {
                final CTOnOff rpt = trpr.getTblHeaderArray(0);
                repeat = (!rpt.isSetVal() || WMLHelper.convertSTOnOffToBoolean(rpt.getVal()));
            }
        }
        return repeat;
    }
    
    public void setRepeatHeader(final boolean repeat) {
        final CTTrPr trpr = this.getTrPr();
        final CTOnOff onoff = (trpr.sizeOfTblHeaderArray() > 0) ? trpr.getTblHeaderArray(0) : trpr.addNewTblHeader();
        onoff.setVal(WMLHelper.convertBooleanToSTOnOff(repeat));
    }
}
