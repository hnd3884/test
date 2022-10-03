package com.adventnet.tools.update.viewer;

import javax.swing.table.JTableHeader;
import java.awt.event.MouseListener;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import java.util.List;
import java.util.Collections;
import java.util.Vector;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;

public class SortTableModel extends DefaultTableModel implements Comparator
{
    protected int currentColumn;
    protected Vector stateVect;
    protected Integer one;
    protected Integer minusOne;
    
    public SortTableModel(final Vector data, final Vector names) {
        super(data, names);
        this.one = new Integer(1);
        this.minusOne = new Integer(-1);
        this.stateVect = new Vector();
        for (int i = 0; i < names.size(); ++i) {
            this.stateVect.add(i, new Integer(1));
        }
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int colIndex) {
        return false;
    }
    
    @Override
    public void addColumn(final Object columnName) {
        super.addColumn(columnName);
        this.stateVect.add(this.one);
    }
    
    @Override
    public void addColumn(final Object columnName, final Object[] columnData) {
        super.addColumn(columnName, columnData);
        this.stateVect.add(this.one);
    }
    
    @Override
    public void addColumn(final Object columnName, final Vector columnData) {
        super.addColumn(columnName, columnData);
        this.stateVect.add(this.one);
    }
    
    @Override
    public int compare(final Object v1, final Object v2) {
        final int ascending = this.stateVect.get(this.currentColumn);
        if (v1 == null && v2 == null) {
            return 0;
        }
        if (v2 == null) {
            return 1 * ascending;
        }
        if (v1 == null) {
            return -1 * ascending;
        }
        final Object firstObj = ((Vector)v1).get(this.currentColumn);
        final Object secondObj = ((Vector)v2).get(this.currentColumn);
        if (firstObj == null && secondObj == null) {
            return 0;
        }
        if (secondObj == null) {
            return 1 * ascending;
        }
        if (firstObj == null) {
            return -1 * ascending;
        }
        if (firstObj instanceof Number && secondObj instanceof Number) {
            final Number n1 = (Number)firstObj;
            final double d1 = n1.doubleValue();
            final Number n2 = (Number)secondObj;
            final double d2 = n2.doubleValue();
            if (d1 == d2) {
                return 0;
            }
            if (d1 > d2) {
                return 1 * ascending;
            }
            return -1 * ascending;
        }
        else {
            if (!(firstObj instanceof Boolean) || !(secondObj instanceof Boolean)) {
                if (firstObj instanceof Comparable && secondObj instanceof Comparable) {
                    final Comparable c1 = (Comparable)firstObj;
                    final Comparable c2 = (Comparable)secondObj;
                    try {
                        return c1.compareTo(c2) * ascending;
                    }
                    catch (final ClassCastException ex) {}
                }
                final String s1 = firstObj.toString();
                final String s2 = secondObj.toString();
                return s1.compareTo(s2) * ascending;
            }
            final Boolean bool1 = (Boolean)firstObj;
            final boolean b1 = bool1;
            final Boolean bool2 = (Boolean)secondObj;
            final boolean b2 = bool2;
            if (b1 == b2) {
                return 0;
            }
            if (b1) {
                return 1 * ascending;
            }
            return -1 * ascending;
        }
    }
    
    public void sort() {
        Collections.sort((List<Object>)this.dataVector, this);
        final Integer val = this.stateVect.get(this.currentColumn);
        this.stateVect.remove(this.currentColumn);
        if (val.equals(this.one)) {
            this.stateVect.add(this.currentColumn, this.minusOne);
        }
        else {
            this.stateVect.add(this.currentColumn, this.one);
        }
    }
    
    public void sortByColumn(final int column) {
        this.currentColumn = column;
        this.sort();
        this.fireTableChanged(new TableModelEvent(this));
    }
    
    public void addMouseListenerToHeaderInTable(final JTable table) {
        final SortTableModel sorter = this;
        final JTable tableView = table;
        tableView.setColumnSelectionAllowed(false);
        final MouseAdapter listMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final TableColumnModel columnModel = tableView.getColumnModel();
                final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                final int column = tableView.convertColumnIndexToModel(viewColumn);
                if (e.getClickCount() == 1 && column != -1) {
                    final int shiftPressed = e.getModifiers() & 0x1;
                    final boolean ascending = shiftPressed == 0;
                    sorter.sortByColumn(column);
                }
            }
        };
        final JTableHeader th = tableView.getTableHeader();
        th.addMouseListener(listMouseListener);
    }
}
