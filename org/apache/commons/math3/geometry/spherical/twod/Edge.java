package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.util.MathUtils;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Edge
{
    private final Vertex start;
    private Vertex end;
    private final double length;
    private final Circle circle;
    
    Edge(final Vertex start, final Vertex end, final double length, final Circle circle) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.circle = circle;
        start.setOutgoing(this);
        end.setIncoming(this);
    }
    
    public Vertex getStart() {
        return this.start;
    }
    
    public Vertex getEnd() {
        return this.end;
    }
    
    public double getLength() {
        return this.length;
    }
    
    public Circle getCircle() {
        return this.circle;
    }
    
    public Vector3D getPointAt(final double alpha) {
        return this.circle.getPointAt(alpha + this.circle.getPhase(this.start.getLocation().getVector()));
    }
    
    void setNextEdge(final Edge next) {
        (this.end = next.getStart()).setIncoming(this);
        this.end.bindWith(this.getCircle());
    }
    
    void split(final Circle splitCircle, final List<Edge> outsideList, final List<Edge> insideList) {
        final double edgeStart = this.circle.getPhase(this.start.getLocation().getVector());
        final Arc arc = this.circle.getInsideArc(splitCircle);
        final double arcRelativeStart = MathUtils.normalizeAngle(arc.getInf(), edgeStart + 3.141592653589793) - edgeStart;
        final double arcRelativeEnd = arcRelativeStart + arc.getSize();
        final double unwrappedEnd = arcRelativeEnd - 6.283185307179586;
        final double tolerance = this.circle.getTolerance();
        Vertex previousVertex = this.start;
        if (unwrappedEnd >= this.length - tolerance) {
            insideList.add(this);
        }
        else {
            double alreadyManagedLength = 0.0;
            if (unwrappedEnd >= 0.0) {
                previousVertex = this.addSubEdge(previousVertex, new Vertex(new S2Point(this.circle.getPointAt(edgeStart + unwrappedEnd))), unwrappedEnd, insideList, splitCircle);
                alreadyManagedLength = unwrappedEnd;
            }
            if (arcRelativeStart >= this.length - tolerance) {
                if (unwrappedEnd >= 0.0) {
                    previousVertex = this.addSubEdge(previousVertex, this.end, this.length - alreadyManagedLength, outsideList, splitCircle);
                }
                else {
                    outsideList.add(this);
                }
            }
            else {
                previousVertex = this.addSubEdge(previousVertex, new Vertex(new S2Point(this.circle.getPointAt(edgeStart + arcRelativeStart))), arcRelativeStart - alreadyManagedLength, outsideList, splitCircle);
                alreadyManagedLength = arcRelativeStart;
                if (arcRelativeEnd >= this.length - tolerance) {
                    previousVertex = this.addSubEdge(previousVertex, this.end, this.length - alreadyManagedLength, insideList, splitCircle);
                }
                else {
                    previousVertex = this.addSubEdge(previousVertex, new Vertex(new S2Point(this.circle.getPointAt(edgeStart + arcRelativeStart))), arcRelativeStart - alreadyManagedLength, insideList, splitCircle);
                    alreadyManagedLength = arcRelativeStart;
                    previousVertex = this.addSubEdge(previousVertex, this.end, this.length - alreadyManagedLength, outsideList, splitCircle);
                }
            }
        }
    }
    
    private Vertex addSubEdge(final Vertex subStart, final Vertex subEnd, final double subLength, final List<Edge> list, final Circle splitCircle) {
        if (subLength <= this.circle.getTolerance()) {
            return subStart;
        }
        subEnd.bindWith(splitCircle);
        final Edge edge = new Edge(subStart, subEnd, subLength, this.circle);
        list.add(edge);
        return subEnd;
    }
}
