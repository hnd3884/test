package org.apache.poi.xdgf.geom;

import com.graphbuilder.curve.MultiPath;
import com.graphbuilder.curve.NURBSpline;
import com.graphbuilder.curve.GroupIterator;
import com.graphbuilder.curve.ShapeMultiPath;
import com.graphbuilder.curve.ValueVector;
import com.graphbuilder.curve.ControlPath;

public class SplineRenderer
{
    public static ShapeMultiPath createNurbsSpline(final ControlPath controlPoints, final ValueVector knots, final ValueVector weights, final int degree) {
        final double firstKnot = knots.get(0);
        final int count = knots.size();
        final double lastKnot = knots.get(count - 1);
        for (int i = 0; i < count; ++i) {
            knots.set((knots.get(i) - firstKnot) / lastKnot, i);
        }
        for (int knotsToAdd = controlPoints.numPoints() + degree + 1, j = count; j < knotsToAdd; ++j) {
            knots.add(1.0);
        }
        final GroupIterator gi = new GroupIterator("0:n-1", controlPoints.numPoints());
        final NURBSpline spline = new NURBSpline(controlPoints, gi);
        spline.setDegree(degree);
        spline.setKnotVectorType(2);
        spline.setKnotVector(knots);
        if (weights == null) {
            spline.setUseWeightVector(false);
        }
        else {
            spline.setWeightVector(weights);
        }
        final ShapeMultiPath shape = new ShapeMultiPath();
        shape.setFlatness(0.01);
        spline.appendTo((MultiPath)shape);
        return shape;
    }
}
