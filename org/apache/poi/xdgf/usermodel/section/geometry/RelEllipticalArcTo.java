package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class RelEllipticalArcTo implements GeometryRow
{
    RelEllipticalArcTo _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Double c;
    Double d;
    Boolean deleted;
    
    public RelEllipticalArcTo(final RowType row) {
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
                    throw new POIXMLException("Invalid cell '" + cellName + "' in RelEllipticalArcTo row");
                }
                this.d = XDGFCell.parseDoubleValue(cell);
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
    
    public Double getD() {
        return (this.d == null) ? this._master.d : this.d;
    }
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (RelEllipticalArcTo)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        final double w = parent.getWidth();
        final double h = parent.getHeight();
        final double x = this.getX() * w;
        final double y = this.getY() * h;
        final double a = this.getA() * w;
        final double b = this.getB() * h;
        final double c = this.getC();
        final double d = this.getD();
        EllipticalArcTo.createEllipticalArc(x, y, a, b, c, d, path);
    }
}
