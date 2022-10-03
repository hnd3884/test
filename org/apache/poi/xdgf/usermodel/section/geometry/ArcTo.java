package org.apache.poi.xdgf.usermodel.section.geometry;

import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class ArcTo implements GeometryRow
{
    ArcTo _master;
    Double x;
    Double y;
    Double a;
    Boolean deleted;
    
    public ArcTo(final RowType row) {
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
                    throw new POIXMLException("Invalid cell '" + cellName + "' in ArcTo row");
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
        this._master = (ArcTo)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        final Point2D last = path.getCurrentPoint();
        final double x = this.getX();
        final double y = this.getY();
        final double a = this.getA();
        if (a == 0.0) {
            path.lineTo(x, y);
            return;
        }
        final double x2 = last.getX();
        final double y2 = last.getY();
        final double chordLength = Math.hypot(y - y2, x - x2);
        final double radius = (4.0 * a * a + chordLength * chordLength) / (8.0 * Math.abs(a));
        final double cx = x2 + (x - x2) / 2.0;
        final double cy = y2 + (y - y2) / 2.0;
        final double rotate = Math.atan2(y - cy, x - cx);
        final Arc2D arc = new Arc2D.Double(x2, y2 - radius, chordLength, 2.0 * radius, 180.0, (x2 < x) ? 180.0 : -180.0, 0);
        path.append(AffineTransform.getRotateInstance(rotate, x2, y2).createTransformedShape(arc), true);
    }
}
