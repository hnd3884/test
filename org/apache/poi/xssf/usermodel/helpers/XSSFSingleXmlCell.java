package org.apache.poi.xssf.usermodel.helpers;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlCellPr;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.model.SingleXmlCells;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;

public class XSSFSingleXmlCell
{
    private CTSingleXmlCell singleXmlCell;
    private SingleXmlCells parent;
    
    public XSSFSingleXmlCell(final CTSingleXmlCell singleXmlCell, final SingleXmlCells parent) {
        this.singleXmlCell = singleXmlCell;
        this.parent = parent;
    }
    
    public XSSFCell getReferencedCell() {
        XSSFCell cell = null;
        final CellReference cellReference = new CellReference(this.singleXmlCell.getR());
        XSSFRow row = this.parent.getXSSFSheet().getRow(cellReference.getRow());
        if (row == null) {
            row = this.parent.getXSSFSheet().createRow(cellReference.getRow());
        }
        cell = row.getCell(cellReference.getCol());
        if (cell == null) {
            cell = row.createCell(cellReference.getCol());
        }
        return cell;
    }
    
    public String getXpath() {
        final CTXmlCellPr xmlCellPr = this.singleXmlCell.getXmlCellPr();
        final CTXmlPr xmlPr = xmlCellPr.getXmlPr();
        return xmlPr.getXpath();
    }
    
    public long getMapId() {
        return this.singleXmlCell.getXmlCellPr().getXmlPr().getMapId();
    }
    
    public STXmlDataType.Enum getXmlDataType() {
        final CTXmlCellPr xmlCellPr = this.singleXmlCell.getXmlCellPr();
        final CTXmlPr xmlPr = xmlCellPr.getXmlPr();
        return xmlPr.getXmlDataType();
    }
}
