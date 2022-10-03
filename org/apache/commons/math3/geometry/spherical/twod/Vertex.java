package org.apache.commons.math3.geometry.spherical.twod;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Vertex
{
    private final S2Point location;
    private Edge incoming;
    private Edge outgoing;
    private final List<Circle> circles;
    
    Vertex(final S2Point location) {
        this.location = location;
        this.incoming = null;
        this.outgoing = null;
        this.circles = new ArrayList<Circle>();
    }
    
    public S2Point getLocation() {
        return this.location;
    }
    
    void bindWith(final Circle circle) {
        this.circles.add(circle);
    }
    
    Circle sharedCircleWith(final Vertex vertex) {
        for (final Circle circle1 : this.circles) {
            for (final Circle circle2 : vertex.circles) {
                if (circle1 == circle2) {
                    return circle1;
                }
            }
        }
        return null;
    }
    
    void setIncoming(final Edge incoming) {
        this.incoming = incoming;
        this.bindWith(incoming.getCircle());
    }
    
    public Edge getIncoming() {
        return this.incoming;
    }
    
    void setOutgoing(final Edge outgoing) {
        this.outgoing = outgoing;
        this.bindWith(outgoing.getCircle());
    }
    
    public Edge getOutgoing() {
        return this.outgoing;
    }
}
