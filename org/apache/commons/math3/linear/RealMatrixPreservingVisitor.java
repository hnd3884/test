package org.apache.commons.math3.linear;

public interface RealMatrixPreservingVisitor
{
    void start(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    void visit(final int p0, final int p1, final double p2);
    
    double end();
}
