package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.apache.poi.ss.usermodel.ClientAnchor;

public class XSSFClientAnchor extends XSSFAnchor implements ClientAnchor
{
    private static final CTMarker EMPTY_MARKER;
    private ClientAnchor.AnchorType anchorType;
    private CTMarker cell1;
    private CTMarker cell2;
    private CTPositiveSize2D size;
    private CTPoint2D position;
    private XSSFSheet sheet;
    
    public XSSFClientAnchor() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
    }
    
    public XSSFClientAnchor(final int dx1, final int dy1, final int dx2, final int dy2, final int col1, final int row1, final int col2, final int row2) {
        this.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE;
        (this.cell1 = CTMarker.Factory.newInstance()).setCol(col1);
        this.cell1.setColOff((long)dx1);
        this.cell1.setRow(row1);
        this.cell1.setRowOff((long)dy1);
        (this.cell2 = CTMarker.Factory.newInstance()).setCol(col2);
        this.cell2.setColOff((long)dx2);
        this.cell2.setRow(row2);
        this.cell2.setRowOff((long)dy2);
    }
    
    protected XSSFClientAnchor(final CTMarker cell1, final CTMarker cell2) {
        this.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE;
        this.cell1 = cell1;
        this.cell2 = cell2;
    }
    
    protected XSSFClientAnchor(final XSSFSheet sheet, final CTMarker cell1, final CTPositiveSize2D size) {
        this.anchorType = ClientAnchor.AnchorType.MOVE_DONT_RESIZE;
        this.sheet = sheet;
        this.size = size;
        this.cell1 = cell1;
    }
    
    protected XSSFClientAnchor(final XSSFSheet sheet, final CTPoint2D position, final CTPositiveSize2D size) {
        this.anchorType = ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE;
        this.sheet = sheet;
        this.position = position;
        this.size = size;
    }
    
    private CTMarker calcCell(final CTMarker cell, final long w, final long h) {
        final CTMarker c2 = CTMarker.Factory.newInstance();
        int r = cell.getRow();
        int c3 = cell.getCol();
        int cw;
        long wPos;
        for (cw = Units.columnWidthToEMU(this.sheet.getColumnWidth(c3)), wPos = cw - cell.getColOff(); wPos < w; wPos += cw) {
            ++c3;
            cw = Units.columnWidthToEMU(this.sheet.getColumnWidth(c3));
        }
        c2.setCol(c3);
        c2.setColOff(cw - (wPos - w));
        int rh;
        long hPos;
        for (rh = Units.toEMU((double)getRowHeight(this.sheet, r)), hPos = rh - cell.getRowOff(); hPos < h; hPos += rh) {
            ++r;
            rh = Units.toEMU((double)getRowHeight(this.sheet, r));
        }
        c2.setRow(r);
        c2.setRowOff(rh - (hPos - h));
        return c2;
    }
    
    private static float getRowHeight(final XSSFSheet sheet, final int row) {
        final XSSFRow r = sheet.getRow(row);
        return (r == null) ? sheet.getDefaultRowHeightInPoints() : r.getHeightInPoints();
    }
    
    private CTMarker getCell1() {
        return (this.cell1 != null) ? this.cell1 : this.calcCell(XSSFClientAnchor.EMPTY_MARKER, this.position.getX(), this.position.getY());
    }
    
    private CTMarker getCell2() {
        return (this.cell2 != null) ? this.cell2 : this.calcCell(this.getCell1(), this.size.getCx(), this.size.getCy());
    }
    
    public short getCol1() {
        return (short)this.getCell1().getCol();
    }
    
    public void setCol1(final int col1) {
        this.cell1.setCol(col1);
    }
    
    public short getCol2() {
        return (short)this.getCell2().getCol();
    }
    
    public void setCol2(final int col2) {
        this.cell2.setCol(col2);
    }
    
    public int getRow1() {
        return this.getCell1().getRow();
    }
    
    public void setRow1(final int row1) {
        this.cell1.setRow(row1);
    }
    
    public int getRow2() {
        return this.getCell2().getRow();
    }
    
    public void setRow2(final int row2) {
        this.cell2.setRow(row2);
    }
    
    public int getDx1() {
        return Math.toIntExact(this.getCell1().getColOff());
    }
    
    public void setDx1(final int dx1) {
        this.cell1.setColOff((long)dx1);
    }
    
    public int getDy1() {
        return Math.toIntExact(this.getCell1().getRowOff());
    }
    
    public void setDy1(final int dy1) {
        this.cell1.setRowOff((long)dy1);
    }
    
    public int getDy2() {
        return Math.toIntExact(this.getCell2().getRowOff());
    }
    
    public void setDy2(final int dy2) {
        this.cell2.setRowOff((long)dy2);
    }
    
    public int getDx2() {
        return Math.toIntExact(this.getCell2().getColOff());
    }
    
    public void setDx2(final int dx2) {
        this.cell2.setColOff((long)dx2);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof XSSFClientAnchor)) {
            return false;
        }
        final XSSFClientAnchor anchor = (XSSFClientAnchor)o;
        return this.getDx1() == anchor.getDx1() && this.getDx2() == anchor.getDx2() && this.getDy1() == anchor.getDy1() && this.getDy2() == anchor.getDy2() && this.getCol1() == anchor.getCol1() && this.getCol2() == anchor.getCol2() && this.getRow1() == anchor.getRow1() && this.getRow2() == anchor.getRow2();
    }
    
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42;
    }
    
    public String toString() {
        return "from : " + this.getCell1() + "; to: " + this.getCell2();
    }
    
    @Internal
    public CTMarker getFrom() {
        return this.getCell1();
    }
    
    protected void setFrom(final CTMarker from) {
        this.cell1 = from;
    }
    
    @Internal
    public CTMarker getTo() {
        return this.getCell2();
    }
    
    protected void setTo(final CTMarker to) {
        this.cell2 = to;
    }
    
    public CTPoint2D getPosition() {
        return this.position;
    }
    
    public void setPosition(final CTPoint2D position) {
        this.position = position;
    }
    
    public CTPositiveSize2D getSize() {
        return this.size;
    }
    
    public void setSize(final CTPositiveSize2D size) {
        this.size = size;
    }
    
    public void setAnchorType(final ClientAnchor.AnchorType anchorType) {
        this.anchorType = anchorType;
    }
    
    public ClientAnchor.AnchorType getAnchorType() {
        return this.anchorType;
    }
    
    public boolean isSet() {
        final CTMarker c1 = this.getCell1();
        final CTMarker c2 = this.getCell2();
        return c1.getCol() != 0 || c2.getCol() != 0 || c1.getRow() != 0 || c2.getRow() != 0;
    }
    
    static {
        EMPTY_MARKER = CTMarker.Factory.newInstance();
    }
}
