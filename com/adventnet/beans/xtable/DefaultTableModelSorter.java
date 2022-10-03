package com.adventnet.beans.xtable;

import java.text.Collator;
import java.util.Vector;
import javax.swing.table.TableModel;
import java.util.Locale;
import java.util.Comparator;

public class DefaultTableModelSorter implements TableModelSorter
{
    private int pKey;
    private int sKey;
    private XTable xt;
    private Comparator comparator;
    public Locale locale;
    
    public DefaultTableModelSorter() {
        this.pKey = -1;
        this.sKey = 0;
        this.comparator = new ColumnSorter();
    }
    
    public TableModel sortView(final TableModel tableModel, final SortColumn[] array, final Locale locale) {
        final Vector vector = new Vector(0);
        for (int i = 0; i < tableModel.getRowCount(); ++i) {
            final Vector<Object> vector2 = new Vector<Object>(1);
            for (int j = 0; j < tableModel.getColumnCount(); ++j) {
                vector2.add(tableModel.getValueAt(i, j));
            }
            vector.add(vector2);
        }
        this.sort(vector, array);
        for (int k = 0; k < tableModel.getRowCount(); ++k) {
            for (int l = 0; l < tableModel.getColumnCount(); ++l) {
                tableModel.setValueAt(vector.get(k).get(l), k, l);
            }
        }
        return tableModel;
    }
    
    public TableModel sortModel(final TableModel tableModel, final SortColumn[] array, final Locale locale) {
        final Vector vector = new Vector(0);
        for (int i = 0; i < tableModel.getRowCount(); ++i) {
            final Vector<Object> vector2 = new Vector<Object>(1);
            for (int j = 0; j < tableModel.getColumnCount(); ++j) {
                vector2.add(tableModel.getValueAt(i, j));
            }
            vector.add(vector2);
        }
        this.sort(vector, array);
        for (int k = 0; k < tableModel.getRowCount(); ++k) {
            for (int l = 0; l < tableModel.getColumnCount(); ++l) {
                tableModel.setValueAt(vector.get(k).get(l), k, l);
            }
        }
        return tableModel;
    }
    
    private void sort(final Vector vector, final SortColumn[] array) {
        for (int i = 0; i < array.length; ++i) {
            int columnIndex = -1;
            if (i > 0) {
                columnIndex = array[i - 1].getColumnIndex();
            }
            final int columnIndex2 = array[i].getColumnIndex();
            for (int j = 0; j < vector.size(); ++j) {
                for (int k = j + 1; k < vector.size(); ++k) {
                    Object value = null;
                    Object value2 = null;
                    if (columnIndex != -1) {
                        value = vector.get(j).get(columnIndex);
                        value2 = vector.get(k).get(columnIndex);
                    }
                    if (this.comparator.compare(value, value2) != 0) {
                        break;
                    }
                    final Object value3 = vector.get(j).get(columnIndex2);
                    final Object value4 = vector.get(k).get(columnIndex2);
                    if (this.comparator.compare(value3, value4) < 0) {
                        if (array[i].isAscending()) {
                            final Object value5 = vector.get(j);
                            vector.set(j, vector.get(k));
                            vector.set(k, value5);
                        }
                    }
                    else if (this.comparator.compare(value3, value4) > 0 && !array[i].isAscending()) {
                        final Object value6 = vector.get(j);
                        vector.set(j, vector.get(k));
                        vector.set(k, value6);
                    }
                }
            }
        }
    }
    
    public class ColumnSorter implements Comparator
    {
        private int prevSortColumn;
        private int sortCol;
        boolean ascending;
        int baseCol;
        
        public int compare(Object o, Object o2) {
            if (o instanceof String && ((String)o).length() == 0) {
                o = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
            if (o == null && o2 == null) {
                return 0;
            }
            if (o == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            Collator collator;
            if (DefaultTableModelSorter.this.locale == null) {
                collator = Collator.getInstance();
            }
            else {
                collator = Collator.getInstance(DefaultTableModelSorter.this.locale);
            }
            if (o instanceof String && o2 instanceof String) {
                if (this.ascending) {
                    return collator.compare(o, o2);
                }
                return collator.compare(o2, o);
            }
            else {
                final String name = o.getClass().getName();
                final String name2 = o2.getClass().getName();
                if (o instanceof Comparable && o2 instanceof Comparable && name.equals(name2)) {
                    if (this.ascending) {
                        return ((Comparable)o).compareTo(o2);
                    }
                    return ((Comparable)o2).compareTo(o);
                }
                else {
                    if (this.ascending) {
                        return collator.compare(o.toString(), o2.toString());
                    }
                    return collator.compare(o2.toString(), o.toString());
                }
            }
        }
    }
}
