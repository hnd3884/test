package org.apache.commons.math3.linear;

public class DefaultRealMatrixPreservingVisitor implements RealMatrixPreservingVisitor
{
    public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
    }
    
    public void visit(final int row, final int column, final double value) {
    }
    
    public double end() {
        return 0.0;
    }
}
