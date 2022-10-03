package org.apache.commons.math3.geometry.spherical.twod;

import java.util.Collection;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.Iterator;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.spherical.oned.S1Point;
import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;

class EdgesBuilder implements BSPTreeVisitor<Sphere2D>
{
    private final BSPTree<Sphere2D> root;
    private final double tolerance;
    private final Map<Edge, BSPTree<Sphere2D>> edgeToNode;
    private final Map<BSPTree<Sphere2D>, List<Edge>> nodeToEdgesList;
    
    EdgesBuilder(final BSPTree<Sphere2D> root, final double tolerance) {
        this.root = root;
        this.tolerance = tolerance;
        this.edgeToNode = new IdentityHashMap<Edge, BSPTree<Sphere2D>>();
        this.nodeToEdgesList = new IdentityHashMap<BSPTree<Sphere2D>, List<Edge>>();
    }
    
    public Order visitOrder(final BSPTree<Sphere2D> node) {
        return Order.MINUS_SUB_PLUS;
    }
    
    public void visitInternalNode(final BSPTree<Sphere2D> node) {
        this.nodeToEdgesList.put(node, new ArrayList<Edge>());
        final BoundaryAttribute<Sphere2D> attribute = (BoundaryAttribute<Sphere2D>)node.getAttribute();
        if (attribute.getPlusOutside() != null) {
            this.addContribution((SubCircle)attribute.getPlusOutside(), false, node);
        }
        if (attribute.getPlusInside() != null) {
            this.addContribution((SubCircle)attribute.getPlusInside(), true, node);
        }
    }
    
    public void visitLeafNode(final BSPTree<Sphere2D> node) {
    }
    
    private void addContribution(final SubCircle sub, final boolean reversed, final BSPTree<Sphere2D> node) {
        final Circle circle = (Circle)sub.getHyperplane();
        final List<Arc> arcs = ((ArcsSet)sub.getRemainingRegion()).asList();
        for (final Arc a : arcs) {
            final Vertex start = new Vertex(circle.toSpace((Point<Sphere1D>)new S1Point(a.getInf())));
            final Vertex end = new Vertex(circle.toSpace((Point<Sphere1D>)new S1Point(a.getSup())));
            start.bindWith(circle);
            end.bindWith(circle);
            Edge edge;
            if (reversed) {
                edge = new Edge(end, start, a.getSize(), circle.getReverse());
            }
            else {
                edge = new Edge(start, end, a.getSize(), circle);
            }
            this.edgeToNode.put(edge, node);
            this.nodeToEdgesList.get(node).add(edge);
        }
    }
    
    private Edge getFollowingEdge(final Edge previous) throws MathIllegalStateException {
        final S2Point point = previous.getEnd().getLocation();
        final List<BSPTree<Sphere2D>> candidates = this.root.getCloseCuts(point, this.tolerance);
        double closest = this.tolerance;
        Edge following = null;
        for (final BSPTree<Sphere2D> node : candidates) {
            for (final Edge edge : this.nodeToEdgesList.get(node)) {
                if (edge != previous && edge.getStart().getIncoming() == null) {
                    final Vector3D edgeStart = edge.getStart().getLocation().getVector();
                    final double gap = Vector3D.angle(point.getVector(), edgeStart);
                    if (gap > closest) {
                        continue;
                    }
                    closest = gap;
                    following = edge;
                }
            }
        }
        if (following != null) {
            return following;
        }
        final Vector3D previousStart = previous.getStart().getLocation().getVector();
        if (Vector3D.angle(point.getVector(), previousStart) <= this.tolerance) {
            return previous;
        }
        throw new MathIllegalStateException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN, new Object[0]);
    }
    
    public List<Edge> getEdges() throws MathIllegalStateException {
        for (final Edge previous : this.edgeToNode.keySet()) {
            previous.setNextEdge(this.getFollowingEdge(previous));
        }
        return new ArrayList<Edge>(this.edgeToNode.keySet());
    }
}
