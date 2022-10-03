package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.Region;
import java.util.Iterator;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;

public class SubLine
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private final Line line;
    private final IntervalsSet remainingRegion;
    
    public SubLine(final Line line, final IntervalsSet remainingRegion) {
        this.line = line;
        this.remainingRegion = remainingRegion;
    }
    
    public SubLine(final Vector3D start, final Vector3D end, final double tolerance) throws MathIllegalArgumentException {
        this(new Line(start, end, tolerance), buildIntervalSet(start, end, tolerance));
    }
    
    @Deprecated
    public SubLine(final Vector3D start, final Vector3D end) throws MathIllegalArgumentException {
        this(start, end, 1.0E-10);
    }
    
    public SubLine(final Segment segment) throws MathIllegalArgumentException {
        this(segment.getLine(), buildIntervalSet(segment.getStart(), segment.getEnd(), segment.getLine().getTolerance()));
    }
    
    public List<Segment> getSegments() {
        final List<Interval> list = this.remainingRegion.asList();
        final List<Segment> segments = new ArrayList<Segment>(list.size());
        for (final Interval interval : list) {
            final Vector3D start = this.line.toSpace((Point<Euclidean1D>)new Vector1D(interval.getInf()));
            final Vector3D end = this.line.toSpace((Point<Euclidean1D>)new Vector1D(interval.getSup()));
            segments.add(new Segment(start, end, this.line));
        }
        return segments;
    }
    
    public Vector3D intersection(final SubLine subLine, final boolean includeEndPoints) {
        final Vector3D v1D = this.line.intersection(subLine.line);
        if (v1D == null) {
            return null;
        }
        final Region.Location loc1 = ((AbstractRegion<Euclidean1D, T>)this.remainingRegion).checkPoint((Point<Euclidean1D>)this.line.toSubSpace((Point<Euclidean3D>)v1D));
        final Region.Location loc2 = ((AbstractRegion<Euclidean1D, T>)subLine.remainingRegion).checkPoint((Point<Euclidean1D>)subLine.line.toSubSpace((Point<Euclidean3D>)v1D));
        if (includeEndPoints) {
            return (loc1 != Region.Location.OUTSIDE && loc2 != Region.Location.OUTSIDE) ? v1D : null;
        }
        return (loc1 == Region.Location.INSIDE && loc2 == Region.Location.INSIDE) ? v1D : null;
    }
    
    private static IntervalsSet buildIntervalSet(final Vector3D start, final Vector3D end, final double tolerance) throws MathIllegalArgumentException {
        final Line line = new Line(start, end, tolerance);
        return new IntervalsSet(line.toSubSpace((Point<Euclidean3D>)start).getX(), line.toSubSpace((Point<Euclidean3D>)end).getX(), tolerance);
    }
}
