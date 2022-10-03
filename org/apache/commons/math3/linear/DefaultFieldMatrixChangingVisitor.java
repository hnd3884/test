package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public class DefaultFieldMatrixChangingVisitor<T extends FieldElement<T>> implements FieldMatrixChangingVisitor<T>
{
    private final T zero;
    
    public DefaultFieldMatrixChangingVisitor(final T zero) {
        this.zero = zero;
    }
    
    public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
    }
    
    public T visit(final int row, final int column, final T value) {
        return value;
    }
    
    public T end() {
        return this.zero;
    }
}
