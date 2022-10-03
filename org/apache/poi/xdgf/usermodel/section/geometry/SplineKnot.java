package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class SplineKnot implements GeometryRow
{
    SplineKnot _master;
    Double x;
    Double y;
    Double a;
    Boolean deleted;
    
    public SplineKnot(final RowType row) {
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
            else {
                if (!cellName.equals("A")) {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in SplineKnot row");
                }
                this.a = XDGFCell.parseDoubleValue(cell);
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
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (SplineKnot)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        throw new POIXMLException("Error: Use SplineRenderer!");
    }
    
    @Override
    public String toString() {
        return "{SplineKnot x=" + this.getX() + " y=" + this.getY() + " a=" + this.getA() + "}";
    }
}
