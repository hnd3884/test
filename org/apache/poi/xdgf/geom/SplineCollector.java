package org.apache.poi.xdgf.geom;

import com.graphbuilder.curve.ShapeMultiPath;
import java.util.Iterator;
import java.awt.geom.Point2D;
import java.awt.Shape;
import com.graphbuilder.curve.Point;
import com.graphbuilder.geom.PointFactory;
import com.graphbuilder.curve.ValueVector;
import com.graphbuilder.curve.ControlPath;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;
import org.apache.poi.xdgf.usermodel.section.geometry.SplineKnot;
import java.util.ArrayList;
import org.apache.poi.xdgf.usermodel.section.geometry.SplineStart;

public class SplineCollector
{
    SplineStart _start;
    ArrayList<SplineKnot> _knots;
    
    public SplineCollector(final SplineStart start) {
        this._knots = new ArrayList<SplineKnot>();
        this._start = start;
    }
    
    public void addKnot(final SplineKnot knot) {
        if (!knot.getDel()) {
            this._knots.add(knot);
        }
    }
    
    public void addToPath(final Path2D.Double path, final XDGFShape parent) {
        final Point2D last = path.getCurrentPoint();
        final ControlPath controlPath = new ControlPath();
        final ValueVector knots = new ValueVector(this._knots.size() + 3);
        final double firstKnot = this._start.getB();
        final double lastKnot = this._start.getC();
        final int degree = this._start.getD();
        knots.add(firstKnot);
        knots.add((double)this._start.getA());
        controlPath.addPoint((Point)PointFactory.create(last.getX(), last.getY()));
        controlPath.addPoint((Point)PointFactory.create((double)this._start.getX(), (double)this._start.getY()));
        for (final SplineKnot knot : this._knots) {
            knots.add((double)knot.getA());
            controlPath.addPoint((Point)PointFactory.create((double)knot.getX(), (double)knot.getY()));
        }
        knots.add(lastKnot);
        final ShapeMultiPath shape = SplineRenderer.createNurbsSpline(controlPath, knots, null, degree);
        path.append((Shape)shape, true);
    }
}
