package org.jfree.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.Serializable;

public class ObjectTable implements Serializable
{
    private static final long serialVersionUID = -3968322452944912066L;
    private int rows;
    private int columns;
    private transient Object[][] data;
    private int rowIncrement;
    private int columnIncrement;
    
    public ObjectTable() {
        this(5, 5);
    }
    
    public ObjectTable(final int increment) {
        this(increment, increment);
    }
    
    public ObjectTable(final int rowIncrement, final int colIncrement) {
        if (rowIncrement < 1) {
            throw new IllegalArgumentException("Increment must be positive.");
        }
        if (colIncrement < 1) {
            throw new IllegalArgumentException("Increment must be positive.");
        }
        this.rows = 0;
        this.columns = 0;
        this.rowIncrement = rowIncrement;
        this.columnIncrement = colIncrement;
        this.data = new Object[rowIncrement][];
    }
    
    public void clear() {
        this.rows = 0;
        this.columns = 0;
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] != null) {
                Arrays.fill(this.data[i], null);
            }
        }
    }
    
    protected void copyColumn(final int oldColumn, final int newColumn) {
        for (int i = 0; i < this.getRowCount(); ++i) {
            this.setObject(i, newColumn, this.getObject(i, oldColumn));
        }
    }
    
    protected void copyRow(final int oldRow, final int newRow) {
        this.ensureCapacity(newRow, this.getColumnCount());
        final Object[] oldRowStorage = this.data[oldRow];
        if (oldRowStorage == null) {
            final Object[] newRowStorage = this.data[newRow];
            if (newRowStorage != null) {
                Arrays.fill(newRowStorage, null);
            }
        }
        else {
            this.data[newRow] = oldRowStorage.clone();
        }
    }
    
    public void ensureCapacity(final int row, final int column) {
        if (row < 0) {
            throw new IndexOutOfBoundsException("Row is invalid. " + row);
        }
        if (column < 0) {
            throw new IndexOutOfBoundsException("Column is invalid. " + column);
        }
        this.ensureRowCapacity(row);
        final Object[] current = this.data[row];
        if (current == null) {
            final Object[] enlarged = new Object[Math.max(column + 1, this.columnIncrement)];
            this.data[row] = enlarged;
        }
        else if (column >= current.length) {
            final Object[] enlarged = new Object[column + this.columnIncrement];
            System.arraycopy(current, 0, enlarged, 0, current.length);
            this.data[row] = enlarged;
        }
    }
    
    protected void ensureRowCapacity(final int row) {
        if (row >= this.data.length) {
            final Object[][] enlarged = new Object[row + this.rowIncrement][];
            System.arraycopy(this.data, 0, enlarged, 0, this.data.length);
            this.data = enlarged;
        }
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectTable)) {
            return false;
        }
        final ObjectTable ot = (ObjectTable)o;
        if (this.getRowCount() != ot.getRowCount()) {
            return false;
        }
        if (this.getColumnCount() != ot.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < this.getRowCount(); ++r) {
            for (int c = 0; c < this.getColumnCount(); ++c) {
                if (!ObjectUtilities.equal(this.getObject(r, c), ot.getObject(r, c))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int getColumnCount() {
        return this.columns;
    }
    
    public int getColumnIncrement() {
        return this.columnIncrement;
    }
    
    protected Object getObject(final int row, final int column) {
        if (row < this.data.length) {
            final Object[] current = this.data[row];
            if (current == null) {
                return null;
            }
            if (column < current.length) {
                return current[column];
            }
        }
        return null;
    }
    
    public int getRowCount() {
        return this.rows;
    }
    
    public int getRowIncrement() {
        return this.rowIncrement;
    }
    
    public int hashCode() {
        int result = this.rows;
        result = 29 * result + this.columns;
        return result;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        final int rowCount = stream.readInt();
        this.data = new Object[rowCount][];
        for (int r = 0; r < rowCount; ++r) {
            final boolean isNotNull = stream.readBoolean();
            if (isNotNull) {
                final int columnCount = stream.readInt();
                final Object[] column = new Object[columnCount];
                this.data[r] = column;
                for (int c = 0; c < columnCount; ++c) {
                    column[c] = this.readSerializedData(stream);
                }
            }
        }
    }
    
    protected Object readSerializedData(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
        return stream.readObject();
    }
    
    protected void setObject(final int row, final int column, final Object object) {
        this.ensureCapacity(row, column);
        this.data[row][column] = object;
        this.rows = Math.max(this.rows, row + 1);
        this.columns = Math.max(this.columns, column + 1);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        final int rowCount = this.data.length;
        stream.writeInt(rowCount);
        for (int r = 0; r < rowCount; ++r) {
            final Object[] column = this.data[r];
            stream.writeBoolean(column != null);
            if (column != null) {
                final int columnCount = column.length;
                stream.writeInt(columnCount);
                for (int c = 0; c < columnCount; ++c) {
                    this.writeSerializedData(stream, column[c]);
                }
            }
        }
    }
    
    protected void writeSerializedData(final ObjectOutputStream stream, final Object o) throws IOException {
        stream.writeObject(o);
    }
}
