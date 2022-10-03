package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import java.util.Iterator;
import org.apache.commons.math3.geometry.partitioning.Side;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Point;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class PolygonsSet extends AbstractRegion<Euclidean2D, Euclidean1D>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private Vector2D[][] vertices;
    
    public PolygonsSet(final double tolerance) {
        super(tolerance);
    }
    
    public PolygonsSet(final BSPTree<Euclidean2D> tree, final double tolerance) {
        super(tree, tolerance);
    }
    
    public PolygonsSet(final Collection<SubHyperplane<Euclidean2D>> boundary, final double tolerance) {
        super(boundary, tolerance);
    }
    
    public PolygonsSet(final double xMin, final double xMax, final double yMin, final double yMax, final double tolerance) {
        super(boxBoundary(xMin, xMax, yMin, yMax, tolerance), tolerance);
    }
    
    public PolygonsSet(final double hyperplaneThickness, final Vector2D... vertices) {
        super(verticesToTree(hyperplaneThickness, vertices), hyperplaneThickness);
    }
    
    @Deprecated
    public PolygonsSet() {
        this(1.0E-10);
    }
    
    @Deprecated
    public PolygonsSet(final BSPTree<Euclidean2D> tree) {
        this(tree, 1.0E-10);
    }
    
    @Deprecated
    public PolygonsSet(final Collection<SubHyperplane<Euclidean2D>> boundary) {
        this(boundary, 1.0E-10);
    }
    
    @Deprecated
    public PolygonsSet(final double xMin, final double xMax, final double yMin, final double yMax) {
        this(xMin, xMax, yMin, yMax, 1.0E-10);
    }
    
    private static Line[] boxBoundary(final double xMin, final double xMax, final double yMin, final double yMax, final double tolerance) {
        if (xMin >= xMax - tolerance || yMin >= yMax - tolerance) {
            return null;
        }
        final Vector2D minMin = new Vector2D(xMin, yMin);
        final Vector2D minMax = new Vector2D(xMin, yMax);
        final Vector2D maxMin = new Vector2D(xMax, yMin);
        final Vector2D maxMax = new Vector2D(xMax, yMax);
        return new Line[] { new Line(minMin, maxMin, tolerance), new Line(maxMin, maxMax, tolerance), new Line(maxMax, minMax, tolerance), new Line(minMax, minMin, tolerance) };
    }
    
    private static BSPTree<Euclidean2D> verticesToTree(final double hyperplaneThickness, final Vector2D... vertices) {
        final int n = vertices.length;
        if (n == 0) {
            return new BSPTree<Euclidean2D>(Boolean.TRUE);
        }
        final Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; ++i) {
            vArray[i] = new Vertex(vertices[i]);
        }
        final List<Edge> edges = new ArrayList<Edge>(n);
        for (int j = 0; j < n; ++j) {
            final Vertex start = vArray[j];
            final Vertex end = vArray[(j + 1) % n];
            Line line = start.sharedLineWith(end);
            if (line == null) {
                line = new Line(start.getLocation(), end.getLocation(), hyperplaneThickness);
            }
            edges.add(new Edge(start, end, line));
            for (final Vertex vertex : vArray) {
                if (vertex != start && vertex != end && FastMath.abs(line.getOffset((Point<Euclidean2D>)vertex.getLocation())) <= hyperplaneThickness) {
                    vertex.bindWith(line);
                }
            }
        }
        final BSPTree<Euclidean2D> tree = new BSPTree<Euclidean2D>();
        insertEdges(hyperplaneThickness, tree, edges);
        return tree;
    }
    
    private static void insertEdges(final double hyperplaneThickness, final BSPTree<Euclidean2D> node, final List<Edge> edges) {
        int index = 0;
        Edge inserted = null;
        while (inserted == null && index < edges.size()) {
            inserted = edges.get(index++);
            if (inserted.getNode() == null) {
                if (node.insertCut(inserted.getLine())) {
                    inserted.setNode(node);
                }
                else {
                    inserted = null;
                }
            }
            else {
                inserted = null;
            }
        }
        if (inserted == null) {
            final BSPTree<Euclidean2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            }
            else {
                node.setAttribute(Boolean.FALSE);
            }
            return;
        }
        final List<Edge> plusList = new ArrayList<Edge>();
        final List<Edge> minusList = new ArrayList<Edge>();
        for (final Edge edge : edges) {
            if (edge != inserted) {
                final double startOffset = inserted.getLine().getOffset((Point<Euclidean2D>)edge.getStart().getLocation());
                final double endOffset = inserted.getLine().getOffset((Point<Euclidean2D>)edge.getEnd().getLocation());
                final Side startSide = (FastMath.abs(startOffset) <= hyperplaneThickness) ? Side.HYPER : ((startOffset < 0.0) ? Side.MINUS : Side.PLUS);
                final Side endSide = (FastMath.abs(endOffset) <= hyperplaneThickness) ? Side.HYPER : ((endOffset < 0.0) ? Side.MINUS : Side.PLUS);
                switch (startSide) {
                    case PLUS: {
                        if (endSide == Side.MINUS) {
                            final Vertex splitPoint = edge.split(inserted.getLine());
                            minusList.add(splitPoint.getOutgoing());
                            plusList.add(splitPoint.getIncoming());
                            continue;
                        }
                        plusList.add(edge);
                        continue;
                    }
                    case MINUS: {
                        if (endSide == Side.PLUS) {
                            final Vertex splitPoint = edge.split(inserted.getLine());
                            minusList.add(splitPoint.getIncoming());
                            plusList.add(splitPoint.getOutgoing());
                            continue;
                        }
                        minusList.add(edge);
                        continue;
                    }
                    default: {
                        if (endSide == Side.PLUS) {
                            plusList.add(edge);
                            continue;
                        }
                        if (endSide == Side.MINUS) {
                            minusList.add(edge);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        if (!plusList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getPlus(), plusList);
        }
        else {
            node.getPlus().setAttribute(Boolean.FALSE);
        }
        if (!minusList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getMinus(), minusList);
        }
        else {
            node.getMinus().setAttribute(Boolean.TRUE);
        }
    }
    
    @Override
    public PolygonsSet buildNew(final BSPTree<Euclidean2D> tree) {
        return new PolygonsSet(tree, this.getTolerance());
    }
    
    @Override
    protected void computeGeometricalProperties() {
        final Vector2D[][] v = this.getVertices();
        if (v.length == 0) {
            final BSPTree<Euclidean2D> tree = ((AbstractRegion<Euclidean2D, T>)this).getTree(false);
            if (tree.getCut() == null && (boolean)tree.getAttribute()) {
                this.setSize(Double.POSITIVE_INFINITY);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter((Point<Euclidean2D>)Vector2D.NaN);
            }
            else {
                this.setSize(0.0);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter((Point<Euclidean2D>)new Vector2D(0.0, 0.0));
            }
        }
        else if (v[0][0] == null) {
            this.setSize(Double.POSITIVE_INFINITY);
            ((AbstractRegion<Euclidean2D, T>)this).setBarycenter((Point<Euclidean2D>)Vector2D.NaN);
        }
        else {
            double sum = 0.0;
            double sumX = 0.0;
            double sumY = 0.0;
            for (final Vector2D[] loop : v) {
                double x1 = loop[loop.length - 1].getX();
                double y1 = loop[loop.length - 1].getY();
                for (final Vector2D point : loop) {
                    final double x2 = x1;
                    final double y2 = y1;
                    x1 = point.getX();
                    y1 = point.getY();
                    final double factor = x2 * y1 - y2 * x1;
                    sum += factor;
                    sumX += factor * (x2 + x1);
                    sumY += factor * (y2 + y1);
                }
            }
            if (sum < 0.0) {
                this.setSize(Double.POSITIVE_INFINITY);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter((Point<Euclidean2D>)Vector2D.NaN);
            }
            else {
                this.setSize(sum / 2.0);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter((Point<Euclidean2D>)new Vector2D(sumX / (3.0 * sum), sumY / (3.0 * sum)));
            }
        }
    }
    
    public Vector2D[][] getVertices() {
        if (this.vertices == null) {
            if (((AbstractRegion<Euclidean2D, T>)this).getTree(false).getCut() == null) {
                this.vertices = new Vector2D[0][];
            }
            else {
                final SegmentsBuilder visitor = new SegmentsBuilder(this.getTolerance());
                ((AbstractRegion<Euclidean2D, T>)this).getTree(true).visit(visitor);
                final List<ConnectableSegment> segments = visitor.getSegments();
                int pending = segments.size();
                pending -= this.naturalFollowerConnections(segments);
                if (pending > 0) {
                    pending -= this.splitEdgeConnections(segments);
                }
                if (pending > 0) {
                    pending -= this.closeVerticesConnections(segments);
                }
                final ArrayList<List<Segment>> loops = new ArrayList<List<Segment>>();
                for (ConnectableSegment s = this.getUnprocessed(segments); s != null; s = this.getUnprocessed(segments)) {
                    final List<Segment> loop = this.followLoop(s);
                    if (loop != null) {
                        if (loop.get(0).getStart() == null) {
                            loops.add(0, loop);
                        }
                        else {
                            loops.add(loop);
                        }
                    }
                }
                this.vertices = new Vector2D[loops.size()][];
                int i = 0;
                for (final List<Segment> loop2 : loops) {
                    if (loop2.size() < 2 || (loop2.size() == 2 && loop2.get(0).getStart() == null && loop2.get(1).getEnd() == null)) {
                        final Line line = loop2.get(0).getLine();
                        this.vertices[i++] = new Vector2D[] { null, line.toSpace((Point<Euclidean1D>)new Vector1D(-3.4028234663852886E38)), line.toSpace((Point<Euclidean1D>)new Vector1D(3.4028234663852886E38)) };
                    }
                    else if (loop2.get(0).getStart() == null) {
                        final Vector2D[] array = new Vector2D[loop2.size() + 2];
                        int j = 0;
                        for (final Segment segment : loop2) {
                            if (j == 0) {
                                double x = segment.getLine().toSubSpace((Point<Euclidean2D>)segment.getEnd()).getX();
                                x -= FastMath.max(1.0, FastMath.abs(x / 2.0));
                                array[j++] = null;
                                array[j++] = segment.getLine().toSpace((Point<Euclidean1D>)new Vector1D(x));
                            }
                            if (j < array.length - 1) {
                                array[j++] = segment.getEnd();
                            }
                            if (j == array.length - 1) {
                                double x = segment.getLine().toSubSpace((Point<Euclidean2D>)segment.getStart()).getX();
                                x += FastMath.max(1.0, FastMath.abs(x / 2.0));
                                array[j++] = segment.getLine().toSpace((Point<Euclidean1D>)new Vector1D(x));
                            }
                        }
                        this.vertices[i++] = array;
                    }
                    else {
                        final Vector2D[] array = new Vector2D[loop2.size()];
                        int j = 0;
                        for (final Segment segment : loop2) {
                            array[j++] = segment.getStart();
                        }
                        this.vertices[i++] = array;
                    }
                }
            }
        }
        return this.vertices.clone();
    }
    
    private int naturalFollowerConnections(final List<ConnectableSegment> segments) {
        int connected = 0;
        for (final ConnectableSegment segment : segments) {
            if (segment.getNext() == null) {
                final BSPTree<Euclidean2D> node = segment.getNode();
                final BSPTree<Euclidean2D> end = segment.getEndNode();
                for (final ConnectableSegment candidateNext : segments) {
                    if (candidateNext.getPrevious() == null && candidateNext.getNode() == end && candidateNext.getStartNode() == node) {
                        segment.setNext(candidateNext);
                        candidateNext.setPrevious(segment);
                        ++connected;
                        break;
                    }
                }
            }
        }
        return connected;
    }
    
    private int splitEdgeConnections(final List<ConnectableSegment> segments) {
        int connected = 0;
        for (final ConnectableSegment segment : segments) {
            if (segment.getNext() == null) {
                final Hyperplane<Euclidean2D> hyperplane = segment.getNode().getCut().getHyperplane();
                final BSPTree<Euclidean2D> end = segment.getEndNode();
                for (final ConnectableSegment candidateNext : segments) {
                    if (candidateNext.getPrevious() == null && candidateNext.getNode().getCut().getHyperplane() == hyperplane && candidateNext.getStartNode() == end) {
                        segment.setNext(candidateNext);
                        candidateNext.setPrevious(segment);
                        ++connected;
                        break;
                    }
                }
            }
        }
        return connected;
    }
    
    private int closeVerticesConnections(final List<ConnectableSegment> segments) {
        int connected = 0;
        for (final ConnectableSegment segment : segments) {
            if (segment.getNext() == null && segment.getEnd() != null) {
                final Vector2D end = segment.getEnd();
                ConnectableSegment selectedNext = null;
                double min = Double.POSITIVE_INFINITY;
                for (final ConnectableSegment candidateNext : segments) {
                    if (candidateNext.getPrevious() == null && candidateNext.getStart() != null) {
                        final double distance = Vector2D.distance(end, candidateNext.getStart());
                        if (distance >= min) {
                            continue;
                        }
                        selectedNext = candidateNext;
                        min = distance;
                    }
                }
                if (min > this.getTolerance()) {
                    continue;
                }
                segment.setNext(selectedNext);
                selectedNext.setPrevious(segment);
                ++connected;
            }
        }
        return connected;
    }
    
    private ConnectableSegment getUnprocessed(final List<ConnectableSegment> segments) {
        for (final ConnectableSegment segment : segments) {
            if (!segment.isProcessed()) {
                return segment;
            }
        }
        return null;
    }
    
    private List<Segment> followLoop(final ConnectableSegment defining) {
        final List<Segment> loop = new ArrayList<Segment>();
        loop.add(defining);
        defining.setProcessed(true);
        ConnectableSegment next;
        for (next = defining.getNext(); next != defining && next != null; next = next.getNext()) {
            loop.add(next);
            next.setProcessed(true);
        }
        if (next == null) {
            for (ConnectableSegment previous = defining.getPrevious(); previous != null; previous = previous.getPrevious()) {
                loop.add(0, previous);
                previous.setProcessed(true);
            }
        }
        this.filterSpuriousVertices(loop);
        if (loop.size() == 2 && loop.get(0).getStart() != null) {
            return null;
        }
        return loop;
    }
    
    private void filterSpuriousVertices(final List<Segment> loop) {
        for (int i = 0; i < loop.size(); ++i) {
            final Segment previous = loop.get(i);
            final int j = (i + 1) % loop.size();
            final Segment next = loop.get(j);
            if (next != null && Precision.equals(previous.getLine().getAngle(), next.getLine().getAngle(), Precision.EPSILON)) {
                loop.set(j, new Segment(previous.getStart(), next.getEnd(), previous.getLine()));
                loop.remove(i--);
            }
        }
    }
    
    private static class Vertex
    {
        private final Vector2D location;
        private Edge incoming;
        private Edge outgoing;
        private final List<Line> lines;
        
        Vertex(final Vector2D location) {
            this.location = location;
            this.incoming = null;
            this.outgoing = null;
            this.lines = new ArrayList<Line>();
        }
        
        public Vector2D getLocation() {
            return this.location;
        }
        
        public void bindWith(final Line line) {
            this.lines.add(line);
        }
        
        public Line sharedLineWith(final Vertex vertex) {
            for (final Line line1 : this.lines) {
                for (final Line line2 : vertex.lines) {
                    if (line1 == line2) {
                        return line1;
                    }
                }
            }
            return null;
        }
        
        public void setIncoming(final Edge incoming) {
            this.incoming = incoming;
            this.bindWith(incoming.getLine());
        }
        
        public Edge getIncoming() {
            return this.incoming;
        }
        
        public void setOutgoing(final Edge outgoing) {
            this.outgoing = outgoing;
            this.bindWith(outgoing.getLine());
        }
        
        public Edge getOutgoing() {
            return this.outgoing;
        }
    }
    
    private static class Edge
    {
        private final Vertex start;
        private final Vertex end;
        private final Line line;
        private BSPTree<Euclidean2D> node;
        
        Edge(final Vertex start, final Vertex end, final Line line) {
            this.start = start;
            this.end = end;
            this.line = line;
            this.node = null;
            start.setOutgoing(this);
            end.setIncoming(this);
        }
        
        public Vertex getStart() {
            return this.start;
        }
        
        public Vertex getEnd() {
            return this.end;
        }
        
        public Line getLine() {
            return this.line;
        }
        
        public void setNode(final BSPTree<Euclidean2D> node) {
            this.node = node;
        }
        
        public BSPTree<Euclidean2D> getNode() {
            return this.node;
        }
        
        public Vertex split(final Line splitLine) {
            final Vertex splitVertex = new Vertex(this.line.intersection(splitLine));
            splitVertex.bindWith(splitLine);
            final Edge startHalf = new Edge(this.start, splitVertex, this.line);
            final Edge endHalf = new Edge(splitVertex, this.end, this.line);
            startHalf.node = this.node;
            endHalf.node = this.node;
            return splitVertex;
        }
    }
    
    private static class ConnectableSegment extends Segment
    {
        private final BSPTree<Euclidean2D> node;
        private final BSPTree<Euclidean2D> startNode;
        private final BSPTree<Euclidean2D> endNode;
        private ConnectableSegment previous;
        private ConnectableSegment next;
        private boolean processed;
        
        ConnectableSegment(final Vector2D start, final Vector2D end, final Line line, final BSPTree<Euclidean2D> node, final BSPTree<Euclidean2D> startNode, final BSPTree<Euclidean2D> endNode) {
            super(start, end, line);
            this.node = node;
            this.startNode = startNode;
            this.endNode = endNode;
            this.previous = null;
            this.next = null;
            this.processed = false;
        }
        
        public BSPTree<Euclidean2D> getNode() {
            return this.node;
        }
        
        public BSPTree<Euclidean2D> getStartNode() {
            return this.startNode;
        }
        
        public BSPTree<Euclidean2D> getEndNode() {
            return this.endNode;
        }
        
        public ConnectableSegment getPrevious() {
            return this.previous;
        }
        
        public void setPrevious(final ConnectableSegment previous) {
            this.previous = previous;
        }
        
        public ConnectableSegment getNext() {
            return this.next;
        }
        
        public void setNext(final ConnectableSegment next) {
            this.next = next;
        }
        
        public void setProcessed(final boolean processed) {
            this.processed = processed;
        }
        
        public boolean isProcessed() {
            return this.processed;
        }
    }
    
    private static class SegmentsBuilder implements BSPTreeVisitor<Euclidean2D>
    {
        private final double tolerance;
        private final List<ConnectableSegment> segments;
        
        SegmentsBuilder(final double tolerance) {
            this.tolerance = tolerance;
            this.segments = new ArrayList<ConnectableSegment>();
        }
        
        public Order visitOrder(final BSPTree<Euclidean2D> node) {
            return Order.MINUS_SUB_PLUS;
        }
        
        public void visitInternalNode(final BSPTree<Euclidean2D> node) {
            final BoundaryAttribute<Euclidean2D> attribute = (BoundaryAttribute<Euclidean2D>)node.getAttribute();
            final Iterable<BSPTree<Euclidean2D>> splitters = attribute.getSplitters();
            if (attribute.getPlusOutside() != null) {
                this.addContribution(attribute.getPlusOutside(), node, splitters, false);
            }
            if (attribute.getPlusInside() != null) {
                this.addContribution(attribute.getPlusInside(), node, splitters, true);
            }
        }
        
        public void visitLeafNode(final BSPTree<Euclidean2D> node) {
        }
        
        private void addContribution(final SubHyperplane<Euclidean2D> sub, final BSPTree<Euclidean2D> node, final Iterable<BSPTree<Euclidean2D>> splitters, final boolean reversed) {
            final AbstractSubHyperplane<Euclidean2D, Euclidean1D> absSub = (AbstractSubHyperplane)sub;
            final Line line = (Line)sub.getHyperplane();
            final List<Interval> intervals = ((IntervalsSet)absSub.getRemainingRegion()).asList();
            for (final Interval i : intervals) {
                final Vector2D startV = Double.isInfinite(i.getInf()) ? null : line.toSpace((Point<Euclidean1D>)new Vector1D(i.getInf()));
                final Vector2D endV = Double.isInfinite(i.getSup()) ? null : line.toSpace((Point<Euclidean1D>)new Vector1D(i.getSup()));
                final BSPTree<Euclidean2D> startN = this.selectClosest(startV, splitters);
                final BSPTree<Euclidean2D> endN = this.selectClosest(endV, splitters);
                if (reversed) {
                    this.segments.add(new ConnectableSegment(endV, startV, line.getReverse(), node, endN, startN));
                }
                else {
                    this.segments.add(new ConnectableSegment(startV, endV, line, node, startN, endN));
                }
            }
        }
        
        private BSPTree<Euclidean2D> selectClosest(final Vector2D point, final Iterable<BSPTree<Euclidean2D>> candidates) {
            BSPTree<Euclidean2D> selected = null;
            double min = Double.POSITIVE_INFINITY;
            for (final BSPTree<Euclidean2D> node : candidates) {
                final double distance = FastMath.abs(node.getCut().getHyperplane().getOffset(point));
                if (distance < min) {
                    selected = node;
                    min = distance;
                }
            }
            return (min <= this.tolerance) ? selected : null;
        }
        
        public List<ConnectableSegment> getSegments() {
            return this.segments;
        }
    }
}
