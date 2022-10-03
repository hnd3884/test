package org.apache.commons.math3.linear;

public class DefaultRealMatrixChangingVisitor implements RealMatrixChangingVisitor
{
    public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
    }
    
    public double visit(final int row, final int column, final double value) {
        return value;
    }
    
    public double end() {
        return 0.0;
    }
}
