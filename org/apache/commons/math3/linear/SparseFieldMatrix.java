package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.util.OpenIntToFieldHashMap;
import org.apache.commons.math3.FieldElement;

public class SparseFieldMatrix<T extends FieldElement<T>> extends AbstractFieldMatrix<T>
{
    private final OpenIntToFieldHashMap<T> entries;
    private final int rows;
    private final int columns;
    
    public SparseFieldMatrix(final Field<T> field) {
        super(field);
        this.rows = 0;
        this.columns = 0;
        this.entries = new OpenIntToFieldHashMap<T>(field);
    }
    
    public SparseFieldMatrix(final Field<T> field, final int rowDimension, final int columnDimension) {
        super(field, rowDimension, columnDimension);
        this.rows = rowDimension;
        this.columns = columnDimension;
        this.entries = new OpenIntToFieldHashMap<T>(field);
    }
    
    public SparseFieldMatrix(final SparseFieldMatrix<T> other) {
        super(other.getField(), other.getRowDimension(), other.getColumnDimension());
        this.rows = other.getRowDimension();
        this.columns = other.getColumnDimension();
        this.entries = new OpenIntToFieldHashMap<T>(other.entries);
    }
    
    public SparseFieldMatrix(final FieldMatrix<T> other) {
        super(other.getField(), other.getRowDimension(), other.getColumnDimension());
        this.rows = other.getRowDimension();
        this.columns = other.getColumnDimension();
        this.entries = new OpenIntToFieldHashMap<T>(this.getField());
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.columns; ++j) {
                this.setEntry(i, j, other.getEntry(i, j));
            }
        }
    }
    
    @Override
    public void addToEntry(final int row, final int column, final T increment) {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        final int key = this.computeKey(row, column);
        final T value = this.entries.get(key).add(increment);
        if (this.getField().getZero().equals(value)) {
            this.entries.remove(key);
        }
        else {
            this.entries.put(key, value);
        }
    }
    
    @Override
    public FieldMatrix<T> copy() {
        return new SparseFieldMatrix((SparseFieldMatrix<FieldElement>)this);
    }
    
    @Override
    public FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension) {
        return new SparseFieldMatrix(this.getField(), rowDimension, columnDimension);
    }
    
    @Override
    public int getColumnDimension() {
        return this.columns;
    }
    
    @Override
    public T getEntry(final int row, final int column) {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        return this.entries.get(this.computeKey(row, column));
    }
    
    @Override
    public int getRowDimension() {
        return this.rows;
    }
    
    @Override
    public void multiplyEntry(final int row, final int column, final T factor) {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        final int key = this.computeKey(row, column);
        final T value = this.entries.get(key).multiply(factor);
        if (this.getField().getZero().equals(value)) {
            this.entries.remove(key);
        }
        else {
            this.entries.put(key, value);
        }
    }
    
    @Override
    public void setEntry(final int row, final int column, final T value) {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        if (this.getField().getZero().equals(value)) {
            this.entries.remove(this.computeKey(row, column));
        }
        else {
            this.entries.put(this.computeKey(row, column), value);
        }
    }
    
    private int computeKey(final int row, final int column) {
        return row * this.columns + column;
    }
}
