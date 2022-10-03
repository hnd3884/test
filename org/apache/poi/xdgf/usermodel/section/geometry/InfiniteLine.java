package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class InfiniteLine implements GeometryRow
{
    InfiniteLine _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Boolean deleted;
    
    public InfiniteLine(final RowType row) {
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
            else {
                if (!cellName.equals("B")) {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in InfiniteLine row");
                }
                this.b = XDGFCell.parseDoubleValue(cell);
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
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (InfiniteLine)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        throw new POIXMLException("InfiniteLine elements cannot be part of a path");
    }
    
    public Path2D.Double getPath() {
        final Path2D.Double path = new Path2D.Double();
        final double max_val = 100000.0;
        final double x0 = this.getX();
        final double y0 = this.getY();
        final double x2 = this.getA();
        final double y2 = this.getB();
        if (x0 == x2) {
            path.moveTo(x0, -max_val);
            path.lineTo(x0, max_val);
        }
        else if (y0 == y2) {
            path.moveTo(-max_val, y0);
            path.lineTo(max_val, y0);
        }
        else {
            final double m = (y2 - y0) / (x2 - x0);
            final double c = y0 - m * x0;
            path.moveTo(max_val, m * max_val + c);
            path.lineTo(max_val, (max_val - c) / m);
        }
        return path;
    }
}
