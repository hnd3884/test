package javax.swing.table;

import javax.swing.event.TableModelEvent;
import java.util.Vector;
import java.io.Serializable;

public class DefaultTableModel extends AbstractTableModel implements Serializable
{
    protected Vector dataVector;
    protected Vector columnIdentifiers;
    
    public DefaultTableModel() {
        this(0, 0);
    }
    
    private static Vector newVector(final int size) {
        final Vector vector = new Vector(size);
        vector.setSize(size);
        return vector;
    }
    
    public DefaultTableModel(final int n, final int n2) {
        this(newVector(n2), n);
    }
    
    public DefaultTableModel(final Vector vector, final int n) {
        this.setDataVector(newVector(n), vector);
    }
    
    public DefaultTableModel(final Object[] array, final int n) {
        this(convertToVector(array), n);
    }
    
    public DefaultTableModel(final Vector vector, final Vector vector2) {
        this.setDataVector(vector, vector2);
    }
    
    public DefaultTableModel(final Object[][] array, final Object[] array2) {
        this.setDataVector(array, array2);
    }
    
    public Vector getDataVector() {
        return this.dataVector;
    }
    
    private static Vector nonNullVector(final Vector vector) {
        return (vector != null) ? vector : new Vector();
    }
    
    public void setDataVector(final Vector vector, final Vector vector2) {
        this.dataVector = nonNullVector(vector);
        this.columnIdentifiers = nonNullVector(vector2);
        this.justifyRows(0, this.getRowCount());
        this.fireTableStructureChanged();
    }
    
    public void setDataVector(final Object[][] array, final Object[] array2) {
        this.setDataVector(convertToVector(array), convertToVector(array2));
    }
    
    public void newDataAvailable(final TableModelEvent tableModelEvent) {
        this.fireTableChanged(tableModelEvent);
    }
    
    private void justifyRows(final int n, final int n2) {
        this.dataVector.setSize(this.getRowCount());
        for (int i = n; i < n2; ++i) {
            if (this.dataVector.elementAt(i) == null) {
                this.dataVector.setElementAt(new Vector<Vector<Vector<Vector>>>(), i);
            }
            ((Vector)this.dataVector.elementAt(i)).setSize(this.getColumnCount());
        }
    }
    
    public void newRowsAdded(final TableModelEvent tableModelEvent) {
        this.justifyRows(tableModelEvent.getFirstRow(), tableModelEvent.getLastRow() + 1);
        this.fireTableChanged(tableModelEvent);
    }
    
    public void rowsRemoved(final TableModelEvent tableModelEvent) {
        this.fireTableChanged(tableModelEvent);
    }
    
    public void setNumRows(final int size) {
        final int rowCount = this.getRowCount();
        if (rowCount == size) {
            return;
        }
        this.dataVector.setSize(size);
        if (size <= rowCount) {
            this.fireTableRowsDeleted(size, rowCount - 1);
        }
        else {
            this.justifyRows(rowCount, size);
            this.fireTableRowsInserted(rowCount, size - 1);
        }
    }
    
    public void setRowCount(final int numRows) {
        this.setNumRows(numRows);
    }
    
    public void addRow(final Vector vector) {
        this.insertRow(this.getRowCount(), vector);
    }
    
    public void addRow(final Object[] array) {
        this.addRow(convertToVector(array));
    }
    
    public void insertRow(final int n, final Vector vector) {
        this.dataVector.insertElementAt(vector, n);
        this.justifyRows(n, n + 1);
        this.fireTableRowsInserted(n, n);
    }
    
    public void insertRow(final int n, final Object[] array) {
        this.insertRow(n, convertToVector(array));
    }
    
    private static int gcd(final int n, final int n2) {
        return (n2 == 0) ? n : gcd(n2, n % n2);
    }
    
    private static void rotate(final Vector vector, final int n, final int n2, final int n3) {
        final int n4 = n2 - n;
        final int n5 = n4 - n3;
        for (int gcd = gcd(n4, n5), i = 0; i < gcd; ++i) {
            int n6 = i;
            final Object element = vector.elementAt(n + n6);
            for (int j = (n6 + n5) % n4; j != i; j = (n6 + n5) % n4) {
                vector.setElementAt(vector.elementAt(n + j), n + n6);
                n6 = j;
            }
            vector.setElementAt(element, n + n6);
        }
    }
    
    public void moveRow(final int n, final int n2, final int n3) {
        final int n4 = n3 - n;
        int n5;
        int n6;
        if (n4 < 0) {
            n5 = n3;
            n6 = n2;
        }
        else {
            n5 = n;
            n6 = n3 + n2 - n;
        }
        rotate(this.dataVector, n5, n6 + 1, n4);
        this.fireTableRowsUpdated(n5, n6);
    }
    
    public void removeRow(final int n) {
        this.dataVector.removeElementAt(n);
        this.fireTableRowsDeleted(n, n);
    }
    
    public void setColumnIdentifiers(final Vector vector) {
        this.setDataVector(this.dataVector, vector);
    }
    
    public void setColumnIdentifiers(final Object[] array) {
        this.setColumnIdentifiers(convertToVector(array));
    }
    
    public void setColumnCount(final int size) {
        this.columnIdentifiers.setSize(size);
        this.justifyRows(0, this.getRowCount());
        this.fireTableStructureChanged();
    }
    
    public void addColumn(final Object o) {
        this.addColumn(o, (Vector)null);
    }
    
    public void addColumn(final Object o, final Vector vector) {
        this.columnIdentifiers.addElement(o);
        if (vector != null) {
            final int size = vector.size();
            if (size > this.getRowCount()) {
                this.dataVector.setSize(size);
            }
            this.justifyRows(0, this.getRowCount());
            final int n = this.getColumnCount() - 1;
            for (int i = 0; i < size; ++i) {
                ((Vector<Object>)this.dataVector.elementAt(i)).setElementAt(vector.elementAt(i), n);
            }
        }
        else {
            this.justifyRows(0, this.getRowCount());
        }
        this.fireTableStructureChanged();
    }
    
    public void addColumn(final Object o, final Object[] array) {
        this.addColumn(o, convertToVector(array));
    }
    
    @Override
    public int getRowCount() {
        return this.dataVector.size();
    }
    
    @Override
    public int getColumnCount() {
        return this.columnIdentifiers.size();
    }
    
    @Override
    public String getColumnName(final int n) {
        Object element = null;
        if (n < this.columnIdentifiers.size() && n >= 0) {
            element = this.columnIdentifiers.elementAt(n);
        }
        return (element == null) ? super.getColumnName(n) : element.toString();
    }
    
    @Override
    public boolean isCellEditable(final int n, final int n2) {
        return true;
    }
    
    @Override
    public Object getValueAt(final int n, final int n2) {
        return this.dataVector.elementAt(n).elementAt(n2);
    }
    
    @Override
    public void setValueAt(final Object o, final int n, final int n2) {
        this.dataVector.elementAt(n).setElementAt(o, n2);
        this.fireTableCellUpdated(n, n2);
    }
    
    protected static Vector convertToVector(final Object[] array) {
        if (array == null) {
            return null;
        }
        final Vector vector = new Vector(array.length);
        for (int length = array.length, i = 0; i < length; ++i) {
            vector.addElement(array[i]);
        }
        return vector;
    }
    
    protected static Vector convertToVector(final Object[][] array) {
        if (array == null) {
            return null;
        }
        final Vector vector = new Vector(array.length);
        for (int length = array.length, i = 0; i < length; ++i) {
            vector.addElement(convertToVector(array[i]));
        }
        return vector;
    }
}
