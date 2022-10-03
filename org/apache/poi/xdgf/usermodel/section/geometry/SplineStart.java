package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class SplineStart implements GeometryRow
{
    SplineStart _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Double c;
    Integer d;
    Boolean deleted;
    
    public SplineStart(final RowType row) {
        if (row.isSetDel()) {
            this.deleted = row.getDel();
        }
        for (final CellType cell : row.getCellArray()) {
            final String cellName = cell.getN();
            if (cellName.equals("X")) {
                this.x = XDGFCell.parseDoubleValue(cell);
            }
            else if (cellName.equals("Y")) {
                this.y = XDGFCell.parseDoubleValue(cell);
            }
            else if (cellName.equals("A")) {
                this.a = XDGFCell.parseDoubleValue(cell);
            }
            else if (cellName.equals("B")) {
                this.b = XDGFCell.parseDoubleValue(cell);
            }
            else if (cellName.equals("C")) {
                this.c = XDGFCell.parseDoubleValue(cell);
            }
            else {
                if (!cellName.equals("D")) {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in SplineStart row");
                }
                this.d = XDGFCell.parseIntegerValue(cell);
            }
        }
    }
    
    public boolean getDel() {
        if (this.deleted != null) {
            return this.deleted;
        }
        return this._master != null && this._master.getDel();
    }
    
    public Double getX() {
        return (this.x == null) ? this._master.x : this.x;
    }
    
    public Double getY() {
        return (this.y == null) ? this._master.y : this.y;
    }
    
    public Double getA() {
        return (this.a == null) ? this._master.a : this.a;
    }
    
    public Double getB() {
        return (this.b == null) ? this._master.b : this.b;
    }
    
    public Double getC() {
        return (this.c == null) ? this._master.c : this.c;
    }
    
    public Integer getD() {
        return (this.d == null) ? this._master.d : this.d;
    }
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (SplineStart)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        throw new POIXMLException("Error: Use SplineRenderer!");
    }
    
    @Override
    public String toString() {
        return "{SplineStart x=" + this.getX() + " y=" + this.getY() + " a=" + this.getA() + " b=" + this.getB() + " c=" + this.getC() + " d=" + this.getD() + "}";
    }
}
