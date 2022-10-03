package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public class DefaultFieldMatrixPreservingVisitor<T extends FieldElement<T>> implements FieldMatrixPreservingVisitor<T>
{
    private final T zero;
    
    public DefaultFieldMatrixPreservingVisitor(final T zero) {
        this.zero = zero;
    }
    
    public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
    }
    
    public void visit(final int row, final int column, final T value) {
    }
    
    public T end() {
        return this.zero;
    }
}
