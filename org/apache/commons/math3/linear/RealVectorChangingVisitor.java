package org.apache.commons.math3.linear;

public interface RealVectorChangingVisitor
{
    void start(final int p0, final int p1, final int p2);
    
    double visit(final int p0, final double p1);
    
    double end();
}
