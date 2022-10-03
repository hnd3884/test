package org.apache.poi.xdgf.usermodel.section.geometry;

import com.graphbuilder.curve.ShapeMultiPath;
import java.awt.geom.Point2D;
import java.awt.Shape;
import org.apache.poi.xdgf.geom.SplineRenderer;
import com.graphbuilder.curve.Point;
import com.graphbuilder.geom.PointFactory;
import com.graphbuilder.curve.ValueVector;
import com.graphbuilder.curve.ControlPath;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import com.microsoft.schemas.office.visio.x2012.main.RowType;

public class NURBSTo implements GeometryRow
{
    NURBSTo _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Double c;
    Double d;
    String e;
    Boolean deleted;
    
    public NURBSTo(final RowType row) {
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
            else if (cellName.equals("D")) {
                this.d = XDGFCell.parseDoubleValue(cell);
            }
            else {
                if (!cellName.equals("E")) {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in NURBS row");
                }
                this.e = cell.getV();
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
    
    public String getE() {
        return (this.e == null) ? this._master.e : this.e;
    }
    
    @Override
    public void setupMaster(final GeometryRow row) {
        this._master = (NURBSTo)row;
    }
    
    @Override
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        final Point2D last = path.getCurrentPoint();
        final String formula = this.getE().trim();
        if (!formula.startsWith("NURBS(") || !formula.endsWith(")")) {
            throw new POIXMLException("Invalid NURBS formula: " + formula);
        }
        final String[] components = formula.substring(6, formula.length() - 1).split(",");
        if (components.length < 8) {
            throw new POIXMLException("Invalid NURBS formula (not enough arguments)");
        }
        if ((components.length - 4) % 4 != 0) {
            throw new POIXMLException("Invalid NURBS formula -- need 4 + n*4 arguments, got " + components.length);
        }
        final double lastControlX = this.getX();
        final double lastControlY = this.getY();
        final double secondToLastKnot = this.getA();
        final double lastWeight = this.getB();
        final double firstKnot = this.getC();
        final double firstWeight = this.getD();
        final double lastKnot = Double.parseDouble(components[0].trim());
        final int degree = Integer.parseInt(components[1].trim());
        final int xType = Integer.parseInt(components[2].trim());
        final int yType = Integer.parseInt(components[3].trim());
        double xScale = 1.0;
        double yScale = 1.0;
        if (xType == 0) {
            xScale = parent.getWidth();
        }
        if (yType == 0) {
            yScale = parent.getHeight();
        }
        final ControlPath controlPath = new ControlPath();
        final ValueVector knots = new ValueVector();
        final ValueVector weights = new ValueVector();
        knots.add(firstKnot);
        weights.add(firstWeight);
        controlPath.addPoint((Point)PointFactory.create(last.getX(), last.getY()));
        for (int sets = (components.length - 4) / 4, i = 0; i < sets; ++i) {
            final double x1 = Double.parseDouble(components[4 + i * 4 + 0].trim());
            final double y1 = Double.parseDouble(components[4 + i * 4 + 1].trim());
            final double k = Double.parseDouble(components[4 + i * 4 + 2].trim());
            final double w = Double.parseDouble(components[4 + i * 4 + 3].trim());
            controlPath.addPoint((Point)PointFactory.create(x1 * xScale, y1 * yScale));
            knots.add(k);
            weights.add(w);
        }
        knots.add(secondToLastKnot);
        knots.add(lastKnot);
        weights.add(lastWeight);
        controlPath.addPoint((Point)PointFactory.create(lastControlX, lastControlY));
        final ShapeMultiPath shape = SplineRenderer.createNurbsSpline(controlPath, knots, weights, degree);
        path.append((Shape)shape, true);
    }
}
