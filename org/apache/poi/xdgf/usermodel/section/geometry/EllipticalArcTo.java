package org.apache.poi.xdgf.usermodel.section.geometry;

import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class EllipticalArcTo implements GeometryRow
{
    EllipticalArcTo _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Double c;
    Double d;
    Boolean deleted;
    public static int draw;
    
    public EllipticalArcTo(final RowType row) {
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
                    throw new POIXMLException("Invalid cell '" + cellName + "' in EllipticalArcTo row");
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
        this._master = (EllipticalArcTo)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        final double x = this.getX();
        final double y = this.getY();
        final double a = this.getA();
        final double b = this.getB();
        final double c = this.getC();
        final double d = this.getD();
        createEllipticalArc(x, y, a, b, c, d, path);
    }
    
    public static void createEllipticalArc(double x, double y, double a, double b, final double c, final double d, final Path2D.Double path) {
        final Point2D last = path.getCurrentPoint();
        double x2 = last.getX();
        double y2 = last.getY();
        final AffineTransform at = AffineTransform.getRotateInstance(-c);
        final double[] pts = { x2, y2, x, y, a, b };
        at.transform(pts, 0, pts, 0, 3);
        x2 = pts[0];
        y2 = pts[1];
        x = pts[2];
        y = pts[3];
        a = pts[4];
        b = pts[5];
        final double d2 = d * d;
        final double cx = ((x2 - x) * (x2 + x) * (y - b) - (x - a) * (x + a) * (y2 - y) + d2 * (y2 - y) * (y - b) * (y2 - b)) / (2.0 * ((x2 - x) * (y - b) - (x - a) * (y2 - y)));
        final double cy = ((x2 - x) * (x - a) * (x2 - a) / d2 + (x - a) * (y2 - y) * (y2 + y) - (x2 - x) * (y - b) * (y + b)) / (2.0 * ((x - a) * (y2 - y) - (x2 - x) * (y - b)));
        final double rx = Math.sqrt(Math.pow(x2 - cx, 2.0) + Math.pow(y2 - cy, 2.0) * d2);
        final double ry = rx / d;
        final double ctrlAngle = Math.toDegrees(Math.atan2((b - cy) / ry, (a - cx) / rx));
        final double startAngle = Math.toDegrees(Math.atan2((y2 - cy) / ry, (x2 - cx) / rx));
        final double endAngle = Math.toDegrees(Math.atan2((y - cy) / ry, (x - cx) / rx));
        final double sweep = computeSweep(startAngle, endAngle, ctrlAngle);
        final Arc2D arc = new Arc2D.Double(cx - rx, cy - ry, rx * 2.0, ry * 2.0, -startAngle, sweep, 0);
        at.setToRotation(c);
        path.append(at.createTransformedShape(arc), false);
    }
    
    protected static double computeSweep(double startAngle, double endAngle, double ctrlAngle) {
        startAngle = (360.0 + startAngle) % 360.0;
        endAngle = (360.0 + endAngle) % 360.0;
        ctrlAngle = (360.0 + ctrlAngle) % 360.0;
        double sweep;
        if (startAngle < endAngle) {
            if (startAngle < ctrlAngle && ctrlAngle < endAngle) {
                sweep = startAngle - endAngle;
            }
            else {
                sweep = 360.0 + (startAngle - endAngle);
            }
        }
        else if (endAngle < ctrlAngle && ctrlAngle < startAngle) {
            sweep = startAngle - endAngle;
        }
        else {
            sweep = -(360.0 - (startAngle - endAngle));
        }
        return sweep;
    }
}
