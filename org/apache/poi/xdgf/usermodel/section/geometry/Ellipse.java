package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class Ellipse implements GeometryRow
{
    Ellipse _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Double c;
    Double d;
    Boolean deleted;
    
    public Ellipse(final RowType row) {
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
                    throw new POIXMLException("Invalid cell '" + cellName + "' in Ellipse row");
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
        this._master = (Ellipse)row;
    }
    
    public Path2D.Double getPath() {
        if (this.getDel()) {
            return null;
        }
        final double cx = this.getX();
        final double cy = this.getY();
        final double a = this.getA();
        final double b = this.getB();
        final double c = this.getC();
        final double d = this.getD();
        final double rx = Math.hypot(a - cx, b - cy);
        final double ry = Math.hypot(c - cx, d - cy);
        final double angle = (6.283185307179586 + ((cy > b) ? 1.0 : -1.0) * Math.acos((cx - a) / rx)) % 6.283185307179586;
        final Ellipse2D.Double ellipse = new Ellipse2D.Double(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
        final Path2D.Double path = new Path2D.Double(ellipse);
        final AffineTransform tr = new AffineTransform();
        tr.rotate(angle, cx, cy);
        path.transform(tr);
        return path;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        throw new POIXMLException("Ellipse elements cannot be part of a path");
    }
}
