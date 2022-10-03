package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldVectorPreservingVisitor<T extends FieldElement<?>>
{
    void start(final int p0, final int p1, final int p2);
    
    void visit(final int p0, final T p1);
    
    T end();
}
