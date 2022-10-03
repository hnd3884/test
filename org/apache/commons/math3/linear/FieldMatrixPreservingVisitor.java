package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldMatrixPreservingVisitor<T extends FieldElement<?>>
{
    void start(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    void visit(final int p0, final int p1, final T p2);
    
    T end();
}
